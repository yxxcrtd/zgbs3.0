package com.joyintech.tams.exchange.service.trade.processor.business.common;

import com.google.common.base.Strings;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.utils.*;
import com.joyintech.tams.exchange.datacommon.db.service.*;
import com.joyintech.tams.exchange.service.trade.processor.MessageSenderManagement;
import com.joyintech.tams.exchange.service.trade.processor.account.AccountPositionHelper;
import com.joyintech.tams.exchange.service.trade.processor.business.bond.BondHelper;
import com.joyintech.tams.exchange.service.trade.processor.others.SplitOrderHelper;
import com.joyintech.tams.exchange.service.trade.transaction.TransactionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.Objects;

import static com.joyintech.tams.exchange.common.utils.TradeUtils.multiply;

@Component
public class TradeCallbackHelper {
    private static final Logger logger = LoggerFactory.getLogger(TradeCallbackHelper.class);

    @Autowired
    private TradeService tradeService;
    @Autowired
    private IntegratedEntrustService integratedEntrustService;
    @Autowired
    private TransactionHelper transactionHelper;
    @Autowired
    private EntrustCancelProcessor entrustCancelProcessor;
    @Autowired
    private EntrustService entrustService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private InstructionService instructionService;
    //    @Autowired
//    private MessageSenderService messageSenderService;
    @Autowired
    private FuturesInstrumentService futuresInstrumentService;
    @Autowired
    private AccountPositionHelper accountPositionHelper;
    @Autowired
    private SplitOrderHelper splitOrderHelper;
    @Autowired
    private BondHelper bondHelper;
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private MessageSenderManagement messageSenderManagement;

    public void onTrade(Trade tradeRecord) {
        logger.info("成交回报信息 {}", JsonUtils.getJsonFromObject(tradeRecord));
        //撤单、废单成交无需去重。 支持重复处理
        Trade tradeDb = tradeService.queryTradeByAccountIdAndBrokerTradeId(tradeRecord.getAccountId(), tradeRecord.getBrokerTradeId());
        Date now = new Date(System.currentTimeMillis());
        if (Objects.isNull(tradeDb)) {
            EntrustInterface entrust = null;
            //处理大宗成交回报
            if (IntegratedEntrustUtils.isIntegratedEntrust(tradeRecord.getEntrustProp())) {
                if (!Strings.isNullOrEmpty(tradeRecord.getEntrustId())) {
                    entrust = integratedEntrustService.findById(tradeRecord.getEntrustId());
                } else if (!Strings.isNullOrEmpty(tradeRecord.getBrokerEntrustId())) {
                    entrust = integratedEntrustService.queryIntegratedEntrustByAccountIdAndBrokerEntrustId(tradeRecord.getAccountId(), tradeRecord.getBrokerEntrustId());
                } else {
                    logger.info("tradeRecord has not found businessFlag brokerEntrustId is {}", tradeRecord.getBrokerEntrustId());
                }
                if (Objects.nonNull(entrust)) {
                    tradeRecord.setPortfolioId(entrust.getPortfolioId());
                    tradeRecord.setEntrustProp(entrust.getEntrustProp());
                    if (IntegratedEntrustUtils.isFixedIncome(tradeRecord.getEntrustProp())) {
                        tradeRecord.setBusinessFlag(BusinessFlag.FIXED_INCOME.value);
                    } else if (IntegratedEntrustUtils.isBulk(tradeRecord.getEntrustProp())) {
                        tradeRecord.setBusinessFlag(BusinessFlag.BULK.value);
                        // 终端无法区分大宗盘后交易成交返回的bsFlag
                        if (EntrustProp.LARGE_AFTER_CLOSING_WEIGHT_PRICE.value.equals(entrust.getEntrustProp())) {
                            tradeRecord.setEntrustProp(EntrustProp.LARGE_AFTER_CLOSING_WEIGHT_PRICE.value);
                        }
                    } else if (IntegratedEntrustUtils.isProtocolBack_SH(tradeRecord.getEntrustProp()) || IntegratedEntrustUtils.isProtocolBack_SZ(tradeRecord.getEntrustProp())) {
                        tradeRecord.setBusinessFlag(BusinessFlag.PROTOCOL_BACK.value);
                    } else {
                        logger.info("tradeRecord has not found businessFlag brokerEntrustId is {}", tradeRecord.getBrokerEntrustId());
                    }
                    IntegratedEntrust integratedEntrust = (IntegratedEntrust) entrust;
                    tradeRecord.setPropSeatNo(integratedEntrust.getPropSeatNo());
                    tradeRecord.setPropSeatName(integratedEntrust.getPropSeatName());
                    tradeRecord.setDiscount(integratedEntrust.getDiscount());
                    transactionHelper.addIntegratedEntrust(integratedEntrust);
                    Instruction instruction = instructionService.findById(entrust.getInstructionId());
                    transactionHelper.setInstructionBean(instruction);
//                    bondHelper.handleTradePriceByFullPrice(tradeRecord, entrust.getIsFullPrice(), instruction);
                    if (tradeRecord.getTradeType() == TradeType.NORMAL.value || tradeRecord.getTradeType() == TradeType.INVALID.value) {
                        processEntrustTrade(entrust, tradeRecord, now, instruction);
                    } else if (tradeRecord.getTradeType() == TradeType.CANCEL.value || tradeRecord.getTradeType() == TradeType.INNER_CANCEL.value || tradeRecord.getTradeType() == TradeType.CANCEL_INVALID.value) {
                        if (tradeRecord.isNoCancelEntrust()) {
                            List<IntegratedEntrust> cancelEntrustList = integratedEntrustService.queryCancelIntegratedEntrustByOrigEntrustIdAndAccount(entrust.getId(), entrust.getAccountId());
                            if (Objects.isNull(cancelEntrustList) || cancelEntrustList.size() == 0) {
                                logger.info("tradeRecord has not found cancelEntrustList brokerEntrustId is {}", tradeRecord.getBrokerEntrustId());
                                return;
                            }
                            IntegratedEntrust cancelEntrust = cancelEntrustList.get(0);
                            transactionHelper.addIntegratedEntrust(cancelEntrust);
                            if (cancelEntrustList.size() > 1) {
                                for (int i = 1; i < cancelEntrustList.size(); i++) {
                                    cancelEntrustList.get(i).setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
                                    cancelEntrustList.get(i).setErrorMsg("存在多个撤单委托");
                                    transactionHelper.addIntegratedEntrust(cancelEntrustList.get(i));
                                }
                            }
                            entrustCancelProcessor.processCancelEntrustTrade(cancelEntrust, tradeRecord, now, entrust, instruction);
                        } else {
                            IntegratedEntrust origEntrust = integratedEntrustService.findById(entrust.getOrigEntrustId());
                            if (Objects.nonNull(origEntrust)) {
                                transactionHelper.addIntegratedEntrust(origEntrust);
                                entrustCancelProcessor.processCancelEntrustTrade(entrust, tradeRecord, now, origEntrust, instruction);
                            } else {
                                logger.info("onTrade tradeRecord has no origEntrust -- {}", tradeRecord.getBrokerTradeId());
                            }
                        }
                    }
                    return;
                }
            } else {
                //处理普通成交
                if (!Strings.isNullOrEmpty(tradeRecord.getEntrustId())) {
                    entrust = entrustService.findById(tradeRecord.getEntrustId());
                } else if (!Strings.isNullOrEmpty(tradeRecord.getBrokerEntrustId())) {
                    entrust = entrustService.queryEntrustByBrokerEntrustIdAndAccount(tradeRecord.getBrokerEntrustId(), tradeRecord.getAccountId());
                } else {
                    logger.info("onTrade tradeRecord has no entrust localEntrustId and brokerEntrustId -- {}", tradeRecord.getBrokerTradeId());
                }
                if (Objects.nonNull(entrust)) {
                    tradeRecord.setPortfolioId(entrust.getPortfolioId());
                    //期货计算成交金额
                    if (InvestmentAssetType.FUTURES.value.equals(tradeRecord.getAssetType())) {
                        FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(StockCodeUtils.formatStockCode(tradeRecord.getStockCode(), entrust.getMarket()));
                        tradeRecord.setTradedBalance(TradeUtils.calculateBalance(null, tradeRecord.getTradedPrice(), tradeRecord.getTradedVolume(), tradeRecord.getAssetType(), null, futuresInstrument));
                    }
                    transactionHelper.addEntrust((Entrust) entrust);
                    tradeRecord.setBusinessFlag(BusinessFlag.NORMAL.value);
                    Instruction instruction = instructionService.findById(entrust.getInstructionId());
                    transactionHelper.setInstructionBean(instruction);
//                    bondHelper.handleTradePriceByFullPrice(tradeRecord, entrust.getIsFullPrice(), instruction);
                    if (tradeRecord.getTradeType() == TradeType.NORMAL.value || tradeRecord.getTradeType() == TradeType.INVALID.value) {
                        processEntrustTrade(entrust, tradeRecord, now, instruction);
                    } else if (tradeRecord.getTradeType() == TradeType.CANCEL.value || tradeRecord.getTradeType() == TradeType.INNER_CANCEL.value || tradeRecord.getTradeType() == TradeType.CANCEL_INVALID.value) {
                        if (tradeRecord.isNoCancelEntrust()) {
                            List<Entrust> cancelEntrustList = entrustService.queryCancelEntrustByOrigEntrustIdAndAccount(entrust.getId(), entrust.getAccountId());
                            if (Objects.isNull(cancelEntrustList) || cancelEntrustList.size() == 0) {
                                logger.info("onTrade tradeRecord has not found cancelEntrustList brokerEntrustId is {}", tradeRecord.getBrokerEntrustId());
                                return;
                            }
                            Entrust cancelEntrust = cancelEntrustList.get(0);
                            transactionHelper.addEntrust(cancelEntrust);
                            if (cancelEntrustList.size() > 1) {
                                for (int i = 1; i < cancelEntrustList.size(); i++) {
                                    cancelEntrustList.get(i).setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
                                    cancelEntrustList.get(i).setErrorMsg("存在多个撤单委托");
                                    transactionHelper.addEntrust(cancelEntrustList.get(i));
                                }
                            }
                            entrustCancelProcessor.processCancelEntrustTrade(cancelEntrust, tradeRecord, now, entrust, instruction);
                        } else {
                            Entrust origEntrust = entrustService.findById(entrust.getOrigEntrustId());
                            if (Objects.nonNull(origEntrust)) {
                                transactionHelper.addEntrust(origEntrust);
                                entrustCancelProcessor.processCancelEntrustTrade(entrust, tradeRecord, now, origEntrust, instruction);
                            } else {
                                logger.info("onTrade tradeRecord has no origEntrust -- {}", tradeRecord.getBrokerTradeId());
                            }
                        }

                    }
                    return;
                }
            }
            logger.info("tradeRecord not found entrust {} - {}", tradeRecord.getBrokerTradeId(), tradeRecord.getBrokerEntrustId());
        } else {
            logger.info("tradeDb has  been existing tradeId {} -- {}", tradeDb.getId(), JsonUtils.getJsonFromObject(tradeDb));
        }
    }

    public void processEntrustTrade(EntrustInterface entrust, Trade tradeRecord, Date now, Instruction instruction) {
        logger.info("处理普通成交或者废单成交，TradeType：{}，entrust：{}，BrokerEntrustId：{}，BrokerTradeId：{}", tradeRecord.getTradeType(), entrust.getId(), tradeRecord.getBrokerEntrustId(), tradeRecord.getBrokerTradeId());
        entrust.setUpdateTime(now);
        if (tradeRecord.getTradeType() == TradeType.CANCEL_INVALID.value) {
            logger.info("processCancelByCancelTrade cancel invalid trade");
            entrust.setEntrustStatus(EntrustStatus.INVALID.value);
            entrust.setErrorMsg("撤单废单");
            return;
        }
        if (Objects.nonNull(entrust.getReportNo())) {
            tradeRecord.setReportNo(entrust.getReportNo());
        }
        tradeRecord.setLocalEntrustId(entrust.getLocalEntrustId());
        tradeRecord.setTradingDay(entrust.getTradingDay());
        //质押入库的三条成交合约代码可能不一致，需要保留
        String market = entrust.getMarket();
        if (Strings.isNullOrEmpty(market)) {
            market = StockCodeUtils.getMarket(entrust.getStockCode());
        }
        tradeRecord.setStockCode(StockCodeUtils.formatStockCode(tradeRecord.getStockCode(), market));
        logger.info("=======InstructionId：{}，BrokerEntrustId：{}，BrokerTradeId：{}，Market：{}，StockCode：{}", entrust.getInstructionId(), tradeRecord.getBrokerEntrustId(), tradeRecord.getBrokerTradeId(), market, tradeRecord.getStockCode());
        if (Strings.isNullOrEmpty(tradeRecord.getStockName())) {
            tradeRecord.setStockName(entrust.getStockName());
        }
        tradeRecord.setInstructionId(entrust.getInstructionId());
        tradeRecord.setParentOrderId(entrust.getParentOrderId());
        tradeRecord.setBsFlag(entrust.getBsFlag());
        tradeRecord.setAccountName(entrust.getAccountName());
        tradeRecord.setEntrustType(entrust.getEntrustType());
        tradeRecord.setEntrustProp(entrust.getEntrustProp());
        tradeRecord.setEntrustBs(entrust.getEntrustBs());
        tradeRecord.setEntrustId(entrust.getId());
        tradeRecord.setAssetType(entrust.getAssetType());
        tradeRecord.setTransCode(entrust.getBrokerEntrustId());
        tradeRecord.setCreateTime(now);
        tradeRecord.setUpdateTime(now);
        tradeRecord.setFullPrice(bondHelper.getFullPriceByTradePrice(tradeRecord, entrust.getIsFullPrice(), instruction));
        tradeRecord.setPortfolioName(instruction.getPortfolioName());
        tradeRecord.setSecAcctId(instruction.getSecAcctId());
        tradeRecord.setSecAcctName(instruction.getSecAcctName());
        tradeRecord.setTradePlat(instruction.getTradePlat());
        tradeRecord.setFinprodType2(instruction.getFinprodType2());
        tradeRecord.setInvManagerId(instruction.getInvManagerId());
        tradeRecord.setPs(instruction.getTradeSide());
        tradeRecord.setBusiTypeTg(instruction.getBusiTypeTg());
        tradeRecord.setEntrustClass(instruction.getEntrustClass());
        if (InvestmentAssetType.isBond(tradeRecord.getFinprodType2()) && tradeRecord.getFullPrice() != null) {
            //成交全价金额
            tradeRecord.setClearBalance(tradeRecord.getFullPrice().multiply(BigDecimal.valueOf(tradeRecord.getTradedVolume())));
        }

        //获取BusinessFlag
        if (IntegratedEntrustUtils.isIntegratedEntrust(tradeRecord.getEntrustProp())) {
            if (IntegratedEntrustUtils.isFixedIncome(tradeRecord.getEntrustProp())) {
                tradeRecord.setBusinessFlag(BusinessFlag.FIXED_INCOME.value);
            } else if (IntegratedEntrustUtils.isBulk(tradeRecord.getEntrustProp())) {
                tradeRecord.setBusinessFlag(BusinessFlag.BULK.value);
            } else if (IntegratedEntrustUtils.isProtocolBack_SH(tradeRecord.getEntrustProp()) || IntegratedEntrustUtils.isProtocolBack_SZ(tradeRecord.getEntrustProp())) {
                tradeRecord.setBusinessFlag(BusinessFlag.PROTOCOL_BACK.value);
            } else {
                logger.info("tradeRecord has not found businessFlag brokerEntrustId is {}", tradeRecord.getBrokerEntrustId());
            }
        } else {
            tradeRecord.setBusinessFlag(BusinessFlag.NORMAL.value);
        }
        Order order = null;
        if (Objects.nonNull(entrust.getParentOrderId())) {
            order = orderService.findById(entrust.getParentOrderId());
            if (Objects.nonNull(order)) transactionHelper.addOrder(order);
        }
        //确保成交记录的成交金额、数量、价格在terminal端已经处理
        if (tradeRecord.getTradeType() == TradeType.NORMAL.value) {
            transactionHelper.setTradeRecord(tradeRecord);
            //质押出入库，返回的标准券代码不用再次处理，只需要处理原始合约代码成交
            if (entrust.getStockCode().equals(tradeRecord.getStockCode())) {
                processNormalTrade(entrust, tradeRecord, order, instruction);
            } else {
                logger.info("成交 [{}] 中合约代码  [{}] 和委托 [{}] 中合约代码 [{}] 不一致 entrustProp [{}], 无需处理该笔成交", tradeRecord.getBrokerTradeId(), tradeRecord.getStockCode(), entrust.getId(), entrust.getStockCode(), entrust.getEntrustProp());
            }
            // 若指令全部完成，则更新指令状态为已完成
            updateInstructionStatus(instruction);
            // 是否需要手工交手
            if (IntegratedEntrustUtils.isProtocolBack(tradeRecord.getEntrustProp())) {//协议回购
//                messageSenderService.pushTradeToMQForFixedIncome(tradeRecord, instruction, entrust);
                messageSenderManagement.pushTradeForFixedIncome(tradeRecord, instruction, entrust);
                return;
            } else if (InvestmentAssetType.isBond(instruction.getAssetType()) && TradePlat.needSettleTrade(instruction.getTradePlat())) {
                Instrument instrument = instrumentService.queryByInnerCode(entrust.getStockCode());
                if (Objects.nonNull(instrument)) {
                    if (EntrustUtils.isSettleTrade(instrument.getTradePlat(), entrust.getEntrustProp())) {
//                        messageSenderService.pushTradeToMQForFixedIncome(tradeRecord, instruction, entrust);
                        messageSenderManagement.pushTradeForFixedIncome(tradeRecord, instruction, entrust);
                        return;
                    }
                }
            }
//            messageSenderService.pushTradeToMQ(tradeRecord, instruction, entrust);
            messageSenderManagement.pushTrade(tradeRecord, instruction, entrust);
        } else if (tradeRecord.getTradeType() == TradeType.INVALID.value) {
            if (entrust.getEntrustStatus() == EntrustStatus.INVALID.value) {
                logger.info("委托已通过委托回报废单处理，忽略废单成交");
                return;
            }
            transactionHelper.setTradeRecord(tradeRecord);
            entrustCancelProcessor.processCancelByTrade(entrust, tradeRecord, order, instruction);
        }
    }

    private void updateInstructionStatus(Instruction instruction) {
        if (Objects.nonNull(instruction)) {
            if ((instruction.getEntrustLeftAmt().longValue() == (instruction.getEntrustNum()) && instruction.getEntrustNum() > 0) || (instruction.getEntrustLeftAmt().doubleValue() == (instruction.getEntrustAmt().doubleValue()) && instruction.getEntrustAmt().doubleValue() > 0))
                instruction.setEntrustStatus(InstructionStatus.COMPLETE.value);
        }
    }

    private void processNormalTrade(EntrustInterface entrust, Trade tradeRecord, Order order, Instruction instruction) {
        logger.info("processNormalTrade entrust {}", entrust.getBrokerEntrustId());
        BigDecimal newTrade = multiply(tradeRecord.getTradedPrice(), tradeRecord.getTradedVolume(), 2);//回购不能直接用tradeBalance
        entrust.setTradedPrice(TradeUtils.divide((TradeUtils.add(multiply(entrust.getTradedPrice(), entrust.getTradedVolume(), 4), newTrade, 4)), entrust.getTradedVolume() + tradeRecord.getTradedVolume(), 4));
        entrust.setTradedBalance(TradeUtils.add(entrust.getTradedBalance(), tradeRecord.getTradedBalance(), 2));
        entrust.setTradedVolume(entrust.getTradedVolume() + tradeRecord.getTradedVolume());
        entrust.setTradeTime(tradeRecord.getTradeTime());
        if (entrust.getTradedVolume() == entrust.getEntrustVolume()) {
            entrust.setEntrustStatus(EntrustStatus.SUCCEEDED.value);
        } else if (entrust.getTradedVolume() < entrust.getEntrustVolume()) {
            entrust.setEntrustStatus(EntrustStatus.PART_SUCCESS.value);
        }
        //解冻金额判断，最后一笔需要释放所有解冻
        BigDecimal unFrozenBalance = tradeRecord.getTradedBalance();
        if (entrust.getTradedVolume() + entrust.getCancelVolume() >= entrust.getEntrustVolume()) {
            unFrozenBalance = entrust.getFrozenBalance();
        }
        if (Objects.nonNull(order)) {
            entrustCancelProcessor.unFrozenOrder(order, tradeRecord.getTradedVolume(), unFrozenBalance);
            order.setTradedPrice(TradeUtils.divide(TradeUtils.add(multiply(order.getTradedPrice(), order.getTradedVolume(), 4), newTrade, 4), order.getTradedVolume() + tradeRecord.getTradedVolume(), 4));
            order.setTradedVolume(order.getTradedVolume() + tradeRecord.getTradedVolume());
            order.setTradedBalance(TradeUtils.add(order.getTradedBalance(), tradeRecord.getTradedBalance(), 2));
        }
        // 处理拆单委托订单
        splitOrderHelper.handleSplitOrderByTrade(entrust, tradeRecord.getTradedVolume(), newTrade, unFrozenBalance, tradeRecord);
        BigDecimal unFrozenBalanceInstruction = null;
        if (Objects.nonNull(instruction)) {
            if (instruction.getEntrustNum() > 0) {
                instruction.setEntrustLeftAmt(BigDecimal.valueOf(instruction.getEntrustLeftAmt().longValue() + tradeRecord.getTradedVolume()));
            } else {
                //可能存在金额指令的委托不是冻结金额的，传入成交金额  ----  对于债券， 现在指令为净价，需要单独处理
                unFrozenBalanceInstruction = bondHelper.handleTradePriceByFullPrice(tradeRecord, entrust.getIsFullPrice(), instruction);
                instruction.setEntrustLeftAmt(TradeUtils.add(instruction.getEntrustLeftAmt(), unFrozenBalanceInstruction, 2));
                //解冻金额判断，最后一笔需要释放所有解冻
                if (instruction.getEntrustLeftAmt().doubleValue() >= instruction.getEntrustAmt().doubleValue()) {
                    unFrozenBalanceInstruction = instruction.getFrozenQuantity();
                }
            }
            logger.info("when trade repair instruction EntrustNum {},EntrustAmt {}, EntrustLeftAmt {}, FrozenQuantity {}, unFrozenBalanceInstruction {}", instruction.getEntrustNum(), instruction.getEntrustAmt(), instruction.getEntrustLeftAmt(), instruction.getFrozenQuantity(), unFrozenBalanceInstruction);
        }
        //成交后处理持仓
        accountPositionHelper.handlePositionByTrade(entrust, tradeRecord.getTradeType(), tradeRecord.getTradedVolume());
        //解冻
        entrustCancelProcessor.unFrozenEntrustAndInstructionAndPosition(entrust, tradeRecord.getTradedVolume(), unFrozenBalance, unFrozenBalanceInstruction, instruction, false, true);
    }
}
