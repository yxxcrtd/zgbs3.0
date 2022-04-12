package com.joyintech.tams.exchange.service.trade.processor.business.common;

import com.google.common.base.Strings;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.constant.joyin.InvestmentAssetType1;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.utils.EntrustUtils;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import com.joyintech.tams.exchange.datacommon.db.service.*;
import com.joyintech.tams.exchange.service.trade.management.service.InvestRiskManagement;
import com.joyintech.tams.exchange.service.trade.processor.MessageSenderManagement;
import com.joyintech.tams.exchange.service.trade.processor.TradeHelper;
import com.joyintech.tams.exchange.service.trade.processor.account.AccountPositionHelper;
import com.joyintech.tams.exchange.service.trade.processor.algorithm.AlgorithmHelper;
import com.joyintech.tams.exchange.service.trade.processor.business.bond.BondHelper;
import com.joyintech.tams.exchange.service.trade.processor.constants.Constants;
import com.joyintech.tams.exchange.service.trade.processor.others.SplitOrderHelper;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgent;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgentManager;
import com.joyintech.tams.exchange.service.trade.transaction.TransactionHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Objects;

@Component
public class EntrustCancelProcessor {
    private static final Logger logger = LoggerFactory.getLogger(EntrustCancelProcessor.class);

    @Autowired
    private InstructionService instructionService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private EntrustService entrustService;
    @Autowired
    private InvestRiskManagement investRiskManagement;
    //    @Autowired
//    private MessageSenderService messageSenderService;
    @Autowired
    private TransactionHelper transactionHelper;
    @Autowired
    private AccountRiskParameterService accountRiskParameterService;
    @Autowired
    private TradeAgentManager tradeAgentManager;
    @Autowired
    private TradeHelper tradeHelper;
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private FuturesInstrumentService futuresInstrumentService;
    @Autowired
    private InstructionPledgeService instructionPledgeService;
    @Autowired
    private EntrustCancelProcessor entrustCancelProcessor;
    @Autowired
    private AccountPositionHelper accountPositionHelper;
    @Autowired
    private SplitOrderHelper splitOrderHelper;
    @Autowired
    private AlgorithmHelper algorithmHelper;
    @Autowired
    private BondHelper bondHelper;
    @Autowired
    private MessageSenderManagement messageSenderManagement;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private SimpleDateFormat stringTime = new SimpleDateFormat("HH:mm:ss");
    //    //强制撤单，释放风控，取消冻结
    //    public void closeEntrust(EntrustInterface entrust) {
    //        //todo 待复核，需要撤流程，但是异常撤单，可能投管服务不可用
    //        boolean releaseRisk = true;
    //        if (entrust.getEntrustStatus() == EntrustStatus.PENDING_APPROVAL.value) {
    //            //待审批不用释放风控
    //            releaseRisk = false;
    //        }
    //        Trade tradeRecord = genTradeRecordInCloseEntrust(entrust);
    //        transactionHelper.setTradeRecord(tradeRecord);
    //        Order order = orderService.findById(entrust.getParentOrderId());
    //        if (Objects.nonNull(order))
    //            transactionHelper.setOrder(order);
    //        processCancelByTrade(entrust, tradeRecord, order, null);
    //        Instruction instruction = instructionService.findById(entrust.getInstructionId());
    //        if (Objects.nonNull(instruction)) {
    //            Instrument instrument = instrumentService.queryByInnerCode(entrust.getStockCode());
    //            //可能存在金额指令的委托不是冻结金额的, 撤单成交传入的金额，为委托价*成交数量（柜台可能不会返回成交金额）
    //            BigDecimal unFrozenBalance = TradeUtils.calculateBalance(entrust.getEntrustBs(), entrust.getEntrustPrice(), tradeRecord.getTradedVolume(), entrust.getAssetType(), entrust, instrument);
    //            // 处理已报即成交业务，撤单成交时，回退指令中的已完成数量
    //            if (EntrustUtils.isNoTrade(entrust.getEntrustProp())) {
    //                rollBackInstructionStatus(entrust, instruction, tradeRecord.getTradedVolume(), unFrozenBalance);
    //            } else {
    //                unFrozenEntrustAndInstruction(entrust, tradeRecord.getTradedVolume(), unFrozenBalance, instruction, releaseRisk);
    //            }
    //        }
    //        if (entrust instanceof Entrust) {
    //            transactionHelper.addEntrust((Entrust) entrust);
    //        } else if (entrust instanceof IntegratedEntrust) {
    //            transactionHelper.addIntegratedEntrust((IntegratedEntrust) entrust);
    //        }
    //    }

    private Trade genTradeRecordInCloseEntrust(EntrustInterface entrust) {
        Trade trade = new Trade();
        trade.setEntrustId(entrust.getId());
        trade.setBrokerEntrustId(entrust.getBrokerEntrustId());
        trade.setLocalEntrustId(entrust.getLocalEntrustId());
        trade.setTradeType(TradeType.INNER_CANCEL.value);
        trade.setTradedVolume(entrust.getEntrustVolume() - entrust.getTradedVolume() - entrust.getCancelVolume());
        return trade;
    }

    public void processCancelByTrade(EntrustInterface entrust, Trade tradeRecord, Order order, Instruction instruction) {
        logger.info("processCancelByTrade {}", entrust.getBrokerEntrustId());
        //最后一笔，解冻金额为全部
        BigDecimal unFrozenBalance = (entrust.getFrozenBalance().doubleValue() > 0
                && tradeRecord.getTradedVolume() + entrust.getTradedVolume() + entrust.getCancelVolume() >= entrust.getEntrustVolume())
                ? entrust.getFrozenBalance() : tradeRecord.getTradedBalance();
        if (Objects.nonNull(order)) {
            order.setCancelVolume(order.getCancelVolume() + tradeRecord.getTradedVolume());
            unFrozenOrder(order, tradeRecord.getTradedVolume(), unFrozenBalance);
        }
        splitOrderHelper.unFrozenSplitOrder(entrust.getEntrustProp(), entrust.getRemark(), tradeRecord.getTradedVolume(), unFrozenBalance);
        entrust.setCancelVolume(tradeRecord.getTradedVolume() + entrust.getCancelVolume());
        entrust.setCancelTime(simpleDateFormat.format(new Date(System.currentTimeMillis())));
        if (tradeRecord.getTradeType() == TradeType.INVALID.value) {
            entrust.setEntrustStatus(EntrustStatus.INVALID.value);
        } else if (tradeRecord.getTradeType() == TradeType.CANCEL.value
                || tradeRecord.getTradeType() == TradeType.INNER_CANCEL.value) {
            if (tradeRecord.getTradedVolume() == entrust.getEntrustVolume()) {
                entrust.setEntrustStatus(EntrustStatus.CANCELED.value);
            } else if (tradeRecord.getTradedVolume() < entrust.getEntrustVolume()) {
                entrust.setEntrustStatus(EntrustStatus.PART_TRADED_CANCELED.value);
            }
        }
        if (Objects.nonNull(instruction)) {
            FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(entrust.getStockCode());
            //可能存在金额指令的委托不是冻结金额的, 撤单成交传入的金额，为委托价*成交数量（柜台可能不会返回成交金额）
            unFrozenBalance = TradeUtils.calculateBalance(entrust.getEntrustBs(), entrust.getEntrustPrice(), tradeRecord.getTradedVolume(), entrust.getAssetType(), entrust, futuresInstrument);
            // 处理已报即成交业务，撤单成交时，回退指令中的已完成数量
            if (EntrustUtils.isNoTrade(entrust.getEntrustProp())) {
                rollBackInstructionStatus(entrust, instruction, tradeRecord.getTradedVolume(), unFrozenBalance);
            } else {
                unFrozenEntrustAndInstructionAndPosition(entrust, tradeRecord.getTradedVolume(), unFrozenBalance, instruction);
            }
        }
    }

    public void rollBackInstructionStatus(EntrustInterface entrust, Instruction instruction, long rollBackVolume, BigDecimal rollBackBalance) {
        if (InstructionStatus.COMPLETE.value.equals(instruction.getEntrustStatus())) {
            instruction.setEntrustStatus(InstructionStatus.ALREADY_RECEIVE.value);
        }
        unFrozenEntrustAndInstructionAndPosition(entrust, rollBackVolume, rollBackBalance, instruction);
    }

    public void unFrozenOrderAndEntrustAndInstruction(EntrustInterface entrust, Instruction instruction) {
        unFrozenOrderAndEntrustAndInstruction(entrust, instruction, true);
    }

    public void unFrozenOrderAndEntrustAndInstruction(EntrustInterface entrust, Instruction instruction, boolean release) {
        FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(entrust.getStockCode());
        BigDecimal balance = TradeUtils.calculateBalance(entrust.getEntrustBs(), entrust.getEntrustPrice(), entrust.getEntrustVolume(), entrust.getAssetType(), entrust, futuresInstrument);
        if (!Strings.isNullOrEmpty(entrust.getParentOrderId())) {
            Order order = orderService.findById(entrust.getParentOrderId());
            if (Objects.nonNull(order)) {
                order.setCancelVolume(entrust.getEntrustVolume());
                unFrozenOrder(order, entrust.getFrozenVolume(), entrust.getFrozenBalance());
            }
        }
        splitOrderHelper.unFrozenSplitOrder(entrust.getEntrustProp(), entrust.getRemark(), entrust.getFrozenVolume(), entrust.getFrozenBalance());
        unFrozenEntrustAndInstructionAndPosition(entrust, entrust.getEntrustVolume(), balance, instruction, release);
    }

    public void unFrozenEntrustAndInstructionAndPosition(EntrustInterface entrust, long unFrozenVolume, BigDecimal unFrozenBalance, Instruction instruction) {
        unFrozenEntrustAndInstructionAndPosition(entrust, unFrozenVolume, unFrozenBalance, instruction, true);
    }

    public void unFrozenEntrustAndInstructionAndPosition(EntrustInterface entrust, long unFrozenVolume, BigDecimal unFrozenBalance, Instruction instruction, boolean release) {
        unFrozenEntrustAndInstructionAndPosition(entrust, unFrozenVolume, unFrozenBalance, null, instruction, release, false);
    }

    public void unFrozenEntrustAndInstructionAndPosition(EntrustInterface entrust, long unFrozenVolume, BigDecimal unFrozenBalance, BigDecimal unFrozenBalanceInstructionTrade, Instruction instruction, boolean release, boolean isFromTrade) {
        if (Objects.nonNull(entrust)) {
            if (TradeUtils.isFrozenVolume(entrust.getEntrustProp(), entrust.getEntrustBs(), entrust.getAssetType())) {
                if (entrust.getFrozenVolume() > 0)
                    entrust.setFrozenVolume(entrust.getFrozenVolume() - unFrozenVolume);
                else
                    entrust.setFrozenVolume(0);
            }
            if (TradeUtils.isFrozenBalance(entrust.getEntrustProp(), entrust.getEntrustBs(), entrust.getAssetType())) {
                if (entrust.getFrozenBalance().doubleValue() > 0) {
                    entrust.setFrozenBalance(TradeUtils.subtract(entrust.getFrozenBalance(), unFrozenBalance, 2));
                }
                // 冻结金额为负时修正为0
                if (entrust.getFrozenBalance().doubleValue() < 0) {
                    entrust.setFrozenBalance(BigDecimal.valueOf(0.00));
                }
            }
        }
        //持仓解冻
        accountPositionHelper.unFrozenPositionEnable(entrust, unFrozenVolume);
        //指令解冻
        if (Objects.nonNull(instruction)) {
            if (InvestmentAssetType1.NEGOTIATED_REPO.value.equals(instruction.getAssetType1())) {
                InstructionPledge instructionPledge = instructionPledgeService.queryInstructionPledgesByInstructionIdAndStockCode(instruction.getEntrustId(), entrust.getStockCode());
                // 判断是否为该笔订单已成交， 若已成交，则不需要回退抵押券信息
                if (!isFromTrade) {
                    if (Objects.nonNull(instructionPledge)) {
                        transactionHelper.setInstructionPledge(instructionPledge);
                        instructionPledge.setUsedPledgeNumber(TradeUtils.subtract(instructionPledge.getUsedPledgeNumber(), unFrozenVolume, 0));
                        logger.info("回退抵押券数量 PledgeNumber {} UsedPledgeNumber {} unFrozenVolume {}", instructionPledge.getPledgeNumber(), instructionPledge.getUsedPledgeNumber(), unFrozenVolume);
                    }
                }
            }
            //非大宗意向申报
            if (!EntrustUtils.isIntentionEntrust(entrust.getEntrustProp())) {
                if (instruction.getEntrustNum() > 0) {
                    instruction.setFrozenQuantity(BigDecimal.valueOf(instruction.getFrozenQuantity().longValue() - unFrozenVolume));
                } else if (instruction.getEntrustAmt().doubleValue() > 0) {
                    if (instruction.getFrozenQuantity().doubleValue() > 0) {
                        // 金额指令全部以净价处理， 需要重新计算指令金额冻结
                        unFrozenBalance = bondHelper.calculateBalanceForInstruction(entrust.getEntrustProp(), entrust.getAssetType(), entrust.getEntrustPrice(), unFrozenVolume, unFrozenBalance);
                        instruction.setFrozenQuantity(TradeUtils.subtract(instruction.getFrozenQuantity(), (unFrozenBalanceInstructionTrade == null && !isFromTrade) ? unFrozenBalance : unFrozenBalanceInstructionTrade, 2));
                    } else {
                        instruction.setFrozenQuantity(BigDecimal.valueOf(0.00));
                    }
                }
            }
            //大宗意向申报
            else if (EntrustUtils.isIntentionEntrust(entrust.getEntrustProp())) {
                if (instruction.getEntrustNum() > 0) {
                    instruction.setFrozenQuantity(BigDecimal.valueOf(instruction.getFrozenQuantity().longValue() - unFrozenVolume));
                } else if (instruction.getEntrustAmt().doubleValue() > 0) {
                    if (instruction.getFrozenQuantity().doubleValue() > 0) {
                        // 金额指令全部以净价处理， 需要重新计算指令金额冻结
                        unFrozenBalance = bondHelper.calculateBalanceForInstruction(entrust.getEntrustProp(), entrust.getAssetType(), entrust.getEntrustPrice(), unFrozenVolume, unFrozenBalance);
                        instruction.setFrozenQuantity(TradeUtils.subtract(instruction.getFrozenQuantity(), (unFrozenBalanceInstructionTrade == null && !isFromTrade) ? unFrozenBalance : unFrozenBalanceInstructionTrade, 2));
                    } else {
                        instruction.setFrozenQuantity(BigDecimal.valueOf(0.00));
                    }
                }
            }
//            messageSenderService.pushProgressToMQ(instruction);
            messageSenderManagement.pushProgress(instruction);

            logger.info("解冻指令信息成功，指令委托数量为：{} 指令委托金额为：{} 指令已完成数量或者金额为：{} 指令冻结为：{}", instruction.getEntrustNum(), instruction.getEntrustAmt(), instruction.getEntrustLeftAmt(), instruction.getFrozenQuantity());
            //解冻时释放风控
            if (release && !EntrustUtils.isIntentionEntrust(entrust.getEntrustProp())) {
                investRiskManagement.riskControlRelease(Collections.singletonList(entrust), instruction);
            }
        }
    }

    public void unFrozenOrder(OrderInterface order, long unFrozenVolume, BigDecimal unFrozenBalance) {
        if (TradeUtils.isFrozenVolume(order.getEntrustProp(), order.getEntrustBs(), order.getAssetType())) {
            if (order.getFrozenVolume() > 0)
                order.setFrozenVolume(order.getFrozenVolume() - unFrozenVolume);
            else
                order.setFrozenVolume(0);
        }
        if (TradeUtils.isFrozenBalance(order.getEntrustProp(), order.getEntrustBs(), order.getAssetType())) {
            if (order.getFrozenBalance().doubleValue() > 0)
                order.setFrozenBalance(TradeUtils.subtract(order.getFrozenBalance(), unFrozenBalance, 2));
            else
                order.setFrozenBalance(BigDecimal.valueOf(0.00));
        }
    }

    public void processInnerCancel(Order order, EntrustInterface entrust, Instruction instruction, EntrustInterface cancelEntrust, boolean abnormalFlag) {
        entrust.setCancelTime(simpleDateFormat.format(new Date(System.currentTimeMillis())));
        entrust.setCancelVolume(entrust.getCancelVolume() + entrust.getEntrustVolume());
        if (entrust.getEntrustStatus() == EntrustStatus.REPORTED_TOBE_CANCEL.value
                || entrust.getEntrustStatus() == EntrustStatus.PART_TRADED_TOBE_CANCELED.value) {
            entrust.setCancelVolume(entrust.getCancelVolume() - entrust.getTradedVolume());
        }
        if (Objects.nonNull(order)) {
            order.setFrozenVolume(order.getFrozenVolume() - entrust.getFrozenVolume());
            order.setFrozenBalance(TradeUtils.subtract(order.getFrozenBalance(), entrust.getFrozenBalance(), 2));
            order.setCancelVolume(order.getCancelVolume() + entrust.getCancelVolume());
        }
        // 指令状态修改——主要针对下单即成交业务（根据冻结数量判断指令状态）
        if (InstructionStatus.COMPLETE.value.equals(instruction.getEntrustStatus())) {
            instruction.setEntrustStatus(InstructionStatus.ALREADY_RECEIVE.value);
        }
        // 未报状态需要调用风控接口， 待审批不用调风控接口
        if (entrust.getEntrustStatus() == EntrustStatus.PENDING_APPROVAL.value
                || entrust.getEntrustStatus() == EntrustStatus.CREATED.value) {
            unFrozenEntrustAndInstructionAndPosition(entrust, entrust.getEntrustVolume(), entrust.getFrozenBalance(), instruction, false);
        } else {
            unFrozenEntrustAndInstructionAndPosition(entrust, entrust.getEntrustVolume(), entrust.getFrozenBalance(), instruction, true);
        }
        if (abnormalFlag) {
            entrust.setEntrustStatus(EntrustStatus.ABNORMAL_CANCELED.value);
        } else {
            entrust.setEntrustStatus(EntrustStatus.CANCELED.value);
        }
        cancelEntrust.setEntrustStatus(EntrustStatus.SUCCEEDED.value);
        cancelEntrust.setTradedVolume(entrust.getCancelVolume());
        Instrument instrument = instrumentService.queryByInnerCode(entrust.getStockCode());
        if (EntrustUtils.isFullPrice(instrument, entrust.getAssetType(), entrust.getEntrustProp(), entrust.getIsFullPrice())) {
            cancelEntrust.setTradedPrice(entrust.getFullPrice());
            entrust.setIsFullPrice(FullPriceConst.FULL_PRICE.value);
        } else {
            cancelEntrust.setTradedPrice(entrust.getEntrustPrice());
            entrust.setIsFullPrice(FullPriceConst.NET_PRICE.value);
        }
        FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(entrust.getStockCode());
        cancelEntrust.setTradedBalance(TradeUtils.calculateBalance(entrust.getEntrustBs(), entrust.getEntrustPrice(), entrust.getCancelVolume(), entrust.getAssetType(), entrust, futuresInstrument));
        algorithmHelper.doSupplement(entrust, order);
    }

    public boolean processCancelEntrust(EntrustInterface entrust, int type, EntrustInterface cancelEntrust) {
        if (Objects.nonNull(entrust)) {
            TradeAgent agent = tradeAgentManager.findAgentByAccountId(entrust.getAccountId());
            if (Objects.nonNull(agent)) {
                if (EntrustUtils.hasNoCancelEntrust(entrust.getEntrustStatus())) {
                    logger.info("entrust can not cancel at [{}]", entrust.getEntrustStatus());
                    cancelEntrust.setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
                    cancelEntrust.setErrorMsg("委托状态为：" + entrust.getEntrustStatus() + "，不可以撤单。");
                    return true;
                }
                // 没有交易所委托编号走内部撤单
                //                if (entrust.getEntrustStatus() < EntrustStatus.TOBE_REPORTED.value ) {
                if (entrust.getEntrustStatus() < EntrustStatus.TOBE_REPORTED.value) {
                    unfrozenForInner(entrust, cancelEntrust);
                    //                    Instruction instruction = instructionService.findById(entrust.getInstructionId());
                    //                    transactionHelper.setInstructionBean(instruction);
                    //                    Order order = null;
                    //                    if (Objects.nonNull(entrust.getParentOrderId())) {
                    //                        order = orderService.findById(entrust.getParentOrderId());
                    //                        transactionHelper.setOrder(order);
                    //                    }
                    //                    processInnerCancel(order, entrust, instruction);
                    return true;
                }
                if (Constants.PROCESS_TYPE_ENTRUST == type) {
                    logger.info("send cancelEntrust :{}", entrust.getId());
                    accountCancelRatioControl(agent, (Entrust) entrust, cancelEntrust);
                } else {
                    setCancelStatus(entrust);
                    cancelEntrust.setEntrustStatus(EntrustStatus.TOBE_REPORTED.value);
                    logger.info("send cancelIntegratedEntrust :{}", entrust.getId());
                    agent.cancelIntegratedEntrust((IntegratedEntrust) entrust, (IntegratedEntrust) cancelEntrust);
                }
                return true;
            }
        }
        return false;
    }

    public void unfrozenForInner(EntrustInterface entrust, EntrustInterface cancelEntrust) {
        unfrozenForInner(entrust, cancelEntrust, false);
    }

    public void unfrozenForInner(EntrustInterface entrust, EntrustInterface cancelEntrust, boolean abnormalFlag) {
        Instruction instruction = instructionService.findById(entrust.getInstructionId());
        transactionHelper.setInstructionBean(instruction);
        Order order = null;
        if (Objects.nonNull(entrust.getParentOrderId())) {
            order = orderService.findById(entrust.getParentOrderId());
            transactionHelper.addOrder(order);
        }
        processInnerCancel(order, entrust, instruction, cancelEntrust, abnormalFlag);
    }

    public void processCancelEntrustResp(CancelEntrustBean cancelEntrustBean, EntrustInterface entrustDb, EntrustInterface cancelEntrustDb) {
        Order order = null;
        if (Objects.nonNull(entrustDb.getParentOrderId())) {
            order = orderService.findById(entrustDb.getParentOrderId());
            transactionHelper.addOrder(order);
        }
        if (cancelEntrustBean.getErrorCode() != ErrorCode.SUCCESS.value) {
            logger.info("cancelEntrust error:{}", cancelEntrustBean.getErrorMsg());
            cancelEntrustDb.setErrorCode(cancelEntrustBean.getErrorCode());
            cancelEntrustDb.setEntrustStatus(EntrustStatus.INVALID.value);
            cancelEntrustDb.setErrorMsg(cancelEntrustBean.getErrorMsg());
            tradeHelper.rollbackCancelStatus(entrustDb);
//            if (entrustDb.getTradedVolume() + entrustDb.getCancelVolume() < entrustDb.getEntrustVolume()) {
//                tradeHelper.rollbackCancelStatus(entrustDb);
//            }
        } else {
            cancelEntrustDb.setReportTime(simpleDateFormat.format(new Date(System.currentTimeMillis())));
            cancelEntrustDb.setEntrustStatus(EntrustStatus.REPORTED.value);
            cancelEntrustDb.setBrokerEntrustId(cancelEntrustBean.getCancelBrokerEntrustId());
            entrustDb.setOrigEntrustId(cancelEntrustDb.getId());
        }
    }

    public void accountCancelRatioControl(TradeAgent agent, Entrust entrust, EntrustInterface cancelEntrust) {
        // 账户撤单率控制
        AccountRiskParameter accountRiskParameter = accountRiskParameterService.queryAccountRiskParameterByAccountId(entrust.getAccountId());
        if (entrustService.getAccountCancelRatio(entrust.getAccountId()) < accountRiskParameter.getCancelRatio()) {
            setCancelStatus(entrust);
            cancelEntrust.setEntrustStatus(EntrustStatus.TOBE_REPORTED.value);
            logger.info("send cancelEntrust by Order :{}", entrust.getId());
            agent.cancelEntrust(entrust, cancelEntrust);
        } else {
            cancelEntrust.setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
            entrust.setErrorMsg("撤单率超过限制：" + "当前值:" + entrustService.getAccountCancelRatio(entrust.getAccountId()) + "阈值：" + accountRiskParameter.getCancelRatio());
            cancelEntrust.setErrorMsg(entrust.getErrorMsg());
        }
    }

    public void setCancelStatus(EntrustInterface entrust) {
        if (entrust.getEntrustStatus() == EntrustStatus.REPORTED.value) {
            entrust.setEntrustStatus(EntrustStatus.REPORTED_TOBE_CANCEL.value);
        } else if (entrust.getEntrustStatus() == EntrustStatus.PART_SUCCESS.value) {
            entrust.setEntrustStatus(EntrustStatus.PART_TRADED_TOBE_CANCELED.value);
        }
    }

    public void processCancelEntrustByOrigEntrust(EntrustInterface entrust, EntrustInterface dbEntrust, EntrustInterface dbCancelEntrust) {
        if (entrust.getEntrustStatus() == EntrustStatus.INVALID.value
                || entrust.getEntrustStatus() == EntrustStatus.CANCELED.value
                || entrust.getEntrustStatus() == EntrustStatus.SYSTEM_INVALID.value) {
            handleCancelEntrustByOrigInvalid(entrust, dbEntrust, dbCancelEntrust);
        }
        // 处理订单状态
        if (entrust.getEntrustStatus() >= dbEntrust.getEntrustStatus()) {
            dbEntrust.setEntrustStatus(entrust.getEntrustStatus());
            if (entrust.getEntrustStatus() == EntrustStatus.REPORTED_TOBE_CANCEL.value
                    || entrust.getEntrustStatus() == EntrustStatus.PART_TRADED_TOBE_CANCELED.value) {
                dbCancelEntrust.setEntrustStatus(EntrustStatus.REPORTED.value);
            } else if (entrust.getEntrustStatus() == EntrustStatus.PART_TRADED_CANCELED.value
                    || entrust.getEntrustStatus() == EntrustStatus.CANCELED.value) {
                dbCancelEntrust.setEntrustStatus(EntrustStatus.SUCCEEDED.value);
            }
            if (Strings.isNullOrEmpty(dbEntrust.getBrokerEntrustId())) {
                dbEntrust.setBrokerEntrustId(entrust.getBrokerEntrustId());
            }
            if (Strings.isNullOrEmpty(dbCancelEntrust.getBrokerEntrustId())) {
                dbCancelEntrust.setBrokerEntrustId(dbCancelEntrust.getId());
            }
        } else {
            logger.info("terminal return entrustStatus < dbEntrustStatus {}", dbEntrust.getId());
        }
    }

    private void handleCancelEntrustByOrigInvalid(EntrustInterface entrust, EntrustInterface dbEntrust, EntrustInterface dbCancelEntrust) {
        if (entrust.getEntrustStatus() == EntrustStatus.INVALID.value
                || entrust.getEntrustStatus() == EntrustStatus.SYSTEM_INVALID.value) {
            dbCancelEntrust.setEntrustStatus(entrust.getEntrustStatus());
            dbCancelEntrust.setErrorMsg(entrust.getErrorMsg());
        }
        if (entrust.getEntrustStatus() == EntrustStatus.CANCELED.value) {
            dbCancelEntrust.setEntrustStatus(EntrustStatus.SUCCEEDED.value);
        }
        dbEntrust.setErrorMsg(entrust.getErrorMsg());
        dbEntrust.setEntrustStatus(entrust.getEntrustStatus());
    }

    public void processCancelByCancelTrade(EntrustInterface cancelEntrust, Trade tradeRecord, Order order, Instruction instruction, EntrustInterface origEntrust) {
        logger.info("处理撤单成交,柜台委托编号为： {}", origEntrust.getBrokerEntrustId());
        //最后一笔，解冻金额为全部
        BigDecimal unFrozenBalance = (origEntrust.getFrozenBalance().doubleValue() > 0
                && tradeRecord.getTradedVolume() + origEntrust.getTradedVolume() + origEntrust.getCancelVolume() >= origEntrust.getEntrustVolume())
                ? origEntrust.getFrozenBalance() : tradeRecord.getTradedBalance();
        if (Objects.nonNull(order)) {
            order.setCancelVolume(order.getCancelVolume() + tradeRecord.getTradedVolume());
            unFrozenOrder(order, tradeRecord.getTradedVolume(), unFrozenBalance);
        }
        splitOrderHelper.unFrozenSplitOrder(origEntrust.getEntrustProp(), origEntrust.getRemark(), tradeRecord.getTradedVolume(), unFrozenBalance);
        origEntrust.setCancelVolume(tradeRecord.getTradedVolume() + origEntrust.getCancelVolume());
        origEntrust.setCancelTime(simpleDateFormat.format(new Date(System.currentTimeMillis())));
        Instrument instrument = instrumentService.queryByInnerCode(origEntrust.getStockCode());
        FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(origEntrust.getStockCode());
        if (tradeRecord.getTradeType() == TradeType.CANCEL.value
                || tradeRecord.getTradeType() == TradeType.INNER_CANCEL.value) {
            if (tradeRecord.getTradedVolume() == origEntrust.getEntrustVolume()) {
                origEntrust.setEntrustStatus(EntrustStatus.CANCELED.value);
            } else if (tradeRecord.getTradedVolume() < origEntrust.getEntrustVolume()) {
                origEntrust.setEntrustStatus(EntrustStatus.PART_TRADED_CANCELED.value);
            }
            cancelEntrust.setEntrustStatus(EntrustStatus.SUCCEEDED.value);
            cancelEntrust.setTradedVolume(tradeRecord.getTradedVolume());
            if (EntrustUtils.isFullPrice(instrument, origEntrust.getAssetType(), origEntrust.getEntrustProp(), origEntrust.getIsFullPrice())) {
                cancelEntrust.setTradedPrice(origEntrust.getFullPrice());
                origEntrust.setIsFullPrice(FullPriceConst.FULL_PRICE.value);
            } else {
                cancelEntrust.setTradedPrice(origEntrust.getEntrustPrice());
                origEntrust.setIsFullPrice(FullPriceConst.NET_PRICE.value);
            }
            cancelEntrust.setTradedBalance(TradeUtils.calculateBalance(origEntrust.getEntrustBs(), cancelEntrust.getTradedPrice(), cancelEntrust.getTradedVolume(), origEntrust.getAssetType(), origEntrust, futuresInstrument));
        }
        if (Objects.nonNull(instruction)) {
            if (InvestmentAssetType1.NEGOTIATED_REPO.value.equals(instruction.getAssetType1())) {
                IntegratedEntrust origEntrust1 = (IntegratedEntrust) origEntrust;
                unFrozenBalance = origEntrust1.getEntrustBalance();
            } else {
                //可能存在金额指令的委托不是冻结金额的, 撤单成交传入的金额，为委托价*成交数量（柜台可能不会返回成交金额）
                unFrozenBalance = TradeUtils.calculateBalance(origEntrust.getEntrustBs(), origEntrust.getEntrustPrice(), tradeRecord.getTradedVolume(), origEntrust.getAssetType(), origEntrust, futuresInstrument);
            }
            // 处理已报即成交业务，撤单成交时，回退指令中的已完成数量
            if (EntrustUtils.isNoTrade(origEntrust.getEntrustProp())) {
                rollBackInstructionStatus(origEntrust, instruction, tradeRecord.getTradedVolume(), unFrozenBalance);
            } else {
                unFrozenEntrustAndInstructionAndPosition(origEntrust, tradeRecord.getTradedVolume(), unFrozenBalance, instruction);
            }
        }
        algorithmHelper.doSupplement(origEntrust, order);
    }

    public void processCancelEntrustTrade(EntrustInterface cancelEntrust, Trade tradeRecord, Date now, EntrustInterface origEntrust, Instruction instruction) {
        logger.info("处理撤单或废单成交： {} -- 撤单委托为 {}", tradeRecord.getTradeType(), cancelEntrust.getId());
        origEntrust.setUpdateTime(now);
        cancelEntrust.setUpdateTime(now);
        if (tradeRecord.getTradeType() == TradeType.CANCEL_INVALID.value) {
            logger.info("撤单成交返回撤单类型为：{}(撤单废单)", tradeRecord.getTradeType());
            cancelEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
            cancelEntrust.setErrorMsg("撤单废单");
            tradeHelper.rollbackCancelStatus(origEntrust);
            return;
        }
        tradeRecord.setLocalEntrustId(origEntrust.getLocalEntrustId());
        tradeRecord.setTradingDay(origEntrust.getTradingDay());
        tradeRecord.setStockCode(origEntrust.getStockCode());
        tradeRecord.setStockName(origEntrust.getStockName());
        tradeRecord.setInstructionId(origEntrust.getInstructionId());
        tradeRecord.setParentOrderId(origEntrust.getParentOrderId());
        tradeRecord.setBsFlag(origEntrust.getBsFlag());
        tradeRecord.setAccountName(origEntrust.getAccountName());
        tradeRecord.setEntrustType(cancelEntrust.getEntrustType());
        tradeRecord.setEntrustProp(origEntrust.getEntrustProp());
        tradeRecord.setEntrustBs(origEntrust.getEntrustBs());
        tradeRecord.setEntrustId(origEntrust.getId());
        tradeRecord.setAssetType(origEntrust.getAssetType());
        tradeRecord.setTransCode(origEntrust.getBrokerEntrustId());
        tradeRecord.setCreateTime(now);
        tradeRecord.setUpdateTime(now);
        Order order = null;
        if (Objects.nonNull(origEntrust.getParentOrderId())) {
            order = orderService.findById(origEntrust.getParentOrderId());
            if (Objects.nonNull(order))
                transactionHelper.addOrder(order);
        }
        if (tradeRecord.getTradeType() == TradeType.CANCEL.value
                || tradeRecord.getTradeType() == TradeType.INNER_CANCEL.value
                || tradeRecord.getTradeType() == TradeType.CANCEL_INVALID.value) {
            transactionHelper.setTradeRecord(tradeRecord);
            entrustCancelProcessor.processCancelByCancelTrade(cancelEntrust, tradeRecord, order, instruction, origEntrust);
        }
    }
}
