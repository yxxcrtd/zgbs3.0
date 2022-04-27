package com.joyintech.tams.exchange.service.trade.processor;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.joyintech.exchange.service.quote.service.QuoteService;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.bean.WorkflowBean;
import com.joyintech.tams.exchange.common.bean.investment.ReqRiskTrigger;
import com.joyintech.tams.exchange.common.bean.investment.RiskOccupyBean;
import com.joyintech.tams.exchange.common.cache.QuoteTickService;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.constant.joyin.InstructionPriceMode;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.proto.QuoteProto;
import com.joyintech.tams.exchange.common.utils.EntrustUtils;
import com.joyintech.tams.exchange.common.utils.InvestmentUtils;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import com.joyintech.tams.exchange.datacommon.db.service.*;
import com.joyintech.tams.exchange.service.trade.management.service.CommonManagement;
import com.joyintech.tams.exchange.service.trade.management.service.InvestRiskManagement;
import com.joyintech.tams.exchange.service.trade.management.service.InvestmentManagement;
import com.joyintech.tams.exchange.service.trade.management.service.ReviewManagement;
import com.joyintech.tams.exchange.service.trade.processor.business.bond.BondHelper;
import com.joyintech.tams.exchange.service.trade.processor.msg.MsgType;
import com.joyintech.tams.exchange.service.trade.processor.msg.TradeMsgReceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FairOrderHelper {
    private static final Logger logger = LoggerFactory.getLogger(FairOrderHelper.class);

    @Autowired
    private InstructionService instructionService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private EntrustService entrustService;
    @Autowired
    private InvestRiskManagement investRiskManagement;
    @Autowired
    private TradeHelper tradeHelper;
//    @Autowired
//    private QuoteTickService quoteTickService;
    @Autowired
    private QuoteService quoteService;
    @Autowired
    private CommonManagement commonManagement;
    @Autowired
    private TradeMsgReceiver tradeMsgReceiver;
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private InvestmentManagement investmentManagement;
    @Autowired
    private BondHelper bondHelper;

    @Autowired
    private FuturesInstrumentService futuresInstrumentService;

    private int getRealVolume(double totalVolume, double totalOrderVolume, double leftVolume, int unit) {
        return (int) TradeUtils.formatDouble(totalOrderVolume * leftVolume / totalVolume, -unit, RoundingMode.UP);
    }

    private double getFairOrderVolume(Map<String, Instruction> instructionBeanMap, Order order, Map<String, Double> volumeMap) {
        BigDecimal price = order.getOrderPrice();
        Instrument instrument = instrumentService.queryByInnerCode(order.getStockCode());
        if (EntrustUtils.isFullPrice(instrument, order.getAssetType(), order.getEntrustProp(), order.getIsFullPrice())) {
            // 指令金额计算为净价
//            price = order.getFullPrice();
            order.setIsFullPrice(FullPriceConst.FULL_PRICE.value);
        } else {
            order.setIsFullPrice(FullPriceConst.NET_PRICE.value);
        }
        if (!order.getEntrustProp().equals(EntrustProp.NORMAL.value)) {
            ResultBean tickBean = quoteService.getTick(order.getStockCode());
            if (ErrorCode.isSuccess(tickBean.getErrorCode())) {
                TickBean tick = (TickBean)tickBean.getData();
//            QuoteProto.TickBean tickBean = quoteTickService.getTickFromCache(order.getStockCode());
//            if (Objects.nonNull(tickBean)) {
                price = order.getEntrustBs().equals(EntrustBs.BUY.value) ? TradeUtils.getBigDecimal(tick.getUpperLimitPrice(), 4) : TradeUtils.getBigDecimal(tick.getLowerLimitPrice(), 4);
                // 转换为净价计算 指令数量
                if (EntrustUtils.isFullPrice(instrument, order.getAssetType(), order.getEntrustProp(), order.getIsFullPrice())) {
                    Instruction instruction = instructionService.findById(order.getStockCode());
                    if (Objects.nonNull(instruction)) {
                        price = investmentManagement.queryEntrustPriceByFullPriceFromInvest(price, order.getEntrustVolume(), instruction, bondHelper.getPriceRoundByPriceStep(instrumentService.queryByInnerCode(order.getStockCode())));
                    }
                }
            } else {
                logger.error("get tick failed. instruction {} - {}", order.getInstructionId(), order.getStockCode());
                return 0;
            }
        }
        double totalVolume = 0;
        for (Instruction instruction : instructionBeanMap.values()) {
            double volume;
            if (instruction.volumeInstruction()) {
                volume = instruction.getEntrustNum() - instruction.getEntrustLeftAmt().longValue() - instruction.getFrozenQuantity().longValue();
            } else {
                volume = TradeUtils.divide(TradeUtils.subtract(instruction.getEntrustAmt(), instruction.getEntrustLeftAmt(), instruction.getFrozenQuantity(), 2), price, 2);
            }
            volumeMap.put(instruction.getEntrustId(), volume);
            totalVolume += volume;
        }
        return totalVolume;
    }

    public boolean handleCancelFairOrder(Order order) {
        order.setOrderStatus(OrderStatus.PROGRESSING.value);
        List<Entrust> fairEntrusts = entrustService.queryEntrustByOrderId(order.getId());
        fairEntrusts.forEach(entrust -> {
            EntrustInterface cancelEntrust = new Entrust();
            cancelEntrust.setCreateTime(new Date(System.currentTimeMillis()));
            BeanUtils.copyProperties(entrust, cancelEntrust, "id", "entrustStatus", "frozenBalance", "frozenVolume", "tradedBalance", "tradedPrice", "tradedVolume", "cancelVolume", "entrustType", "localEntrustId", "brokerEntrustId", "origEntrustId");
            if (EntrustStatus.PART_SUCCESS.value == entrust.getEntrustStatus()) {
                cancelEntrust.setEntrustVolume(entrust.getEntrustVolume() - entrust.getTradedVolume());
            }
            cancelEntrust.setOrigEntrustId(entrust.getId());
            cancelEntrust.setEntrustType(EntrustType.CANCEL.value);
            cancelEntrust.setEntrustStatus(EntrustStatus.CREATED.value);
            entrustService.asyncUpdate((Entrust) cancelEntrust);
            logger.info("cancelEntrust new cancelEntrust:{}", JSONObject.toJSONString(cancelEntrust));
            tradeMsgReceiver.addMsg(MsgType.REQ_ENTRUST_CANCEL, cancelEntrust);
        });
        orderService.asyncUpdate(order);
        return true;
    }

    public boolean createFairOrder(Order order) {
        // 判断order是否要做公平交易
        if (!Strings.isNullOrEmpty(order.getAlgorithmId())
                || !EntrustUtils.isFairOrder(order.getEntrustProp())
                || !EntrustUtils.isFairSwitchOpen(commonManagement.getFairCode())) {
            return false;
        }
        if (!tradeHelper.checkCommonParam(order.getFullPrice(), order.getOrderPrice(), order.getOrderVolume(), order)) {
            logger.info("fair order failed");
            order.setOrderStatus(OrderStatus.STOP.value);
            return true;
        }
        Map<String, Instruction> instructionBeanMap = getInstructionBeans(order);
        if (instructionBeanMap.size() <= 1) {
            return false;
        }
        Map<String, Double> volumeMap = new HashMap<>();
        double totalVolume = getFairOrderVolume(instructionBeanMap, order, volumeMap);
        if (totalVolume <= 0) {
            logger.info("fair order {} calculate volume less than zero!", order.getInstructionId());
            return true;    // ???
        }
        order.setBusinessFlag(BusinessFlag.FAIR.value);
        order.setInstructionId("fairTrade");
        orderService.asyncUpdate(order);  // 生成order
        return true;
    }

    public Map<String, Instruction> getInstructionBeans(Order order) {
        Map<String, Instruction> instructionMap = null;
        if (EntrustUtils.isfairModeEntrustPriceBestThanInstructionForbid(commonManagement.getFairModeEntrustPriceBestThanInstruction())) {
            instructionMap = instructionService.queryInstructionByUserIdAndStockCode(order.getUserId(), order.getStockCode())
                    .stream().filter(instruction -> InvestmentUtils.hasSameDir(instruction, order)
                            && InstructionStatus.ALREADY_RECEIVE.value.equals(instruction.getEntrustStatus())
                            && InvestmentUtils.isNormalTradeFromInstruction(instruction.getTradePlat())
                            && !InvestmentUtils.isInstructionProcessComplete(instruction))
                    .collect(Collectors.toMap(Instruction::getEntrustId, instructionBean -> instructionBean));
            // 市价委托，需检查限价委托是否超过涨跌停
            if (EntrustProp.isMarketPrice(order.getEntrustProp())) {
                ResultBean tickBean = quoteService.getTick(order.getStockCode());
                if (ErrorCode.isSuccess(tickBean.getErrorCode())) {
                    TickBean tick = (TickBean)tickBean.getData();
//                QuoteProto.TickBean tick = quoteTickService.getTickFromCache(order.getStockCode());
//                if (Objects.nonNull(tick)) {
                    double lowerLimitPrice = TradeUtils.formatDouble(tick.getLowerLimitPrice(), 4);
                    double upperLimitPrice = TradeUtils.formatDouble(tick.getUpperLimitPrice(), 4);
                    boolean flag = false;
                    if (EntrustBs.BUY.value.equals(order.getEntrustBs())) {
                        // 买入 涨停价
                        flag = instructionMap.entrySet().stream().anyMatch(entry -> {
                            if (InstructionPriceMode.FIXED.value.equals(entry.getValue().getPriceMode())
                                    && Double.compare(upperLimitPrice, entry.getValue().getEntrustPrice().doubleValue()) > 0) {
                                logger.debug("委托价格不优于指令价格系统参数[{}]，买卖方向[{}]，委托类型[{}],存在限价指令[{}]指令价格[{}]小于涨停价[{}]",
                                        commonManagement.getFairModeEntrustPriceBestThanInstruction(),
                                        order.getEntrustBs(),
                                        order.getEntrustProp(),
                                        entry.getValue().getId(),
                                        entry.getValue().getEntrustPrice(),
                                        upperLimitPrice);
                                return true;
                            }
                            return false;
                        });
                    } else if (EntrustBs.SELL.value.equals(order.getEntrustBs())) {
                        // 卖出 跌停价
                        flag = instructionMap.entrySet().stream().anyMatch(entry -> {
                            if (InstructionPriceMode.FIXED.value.equals(entry.getValue().getPriceMode())
                                    && Double.compare(lowerLimitPrice, entry.getValue().getEntrustPrice().doubleValue()) < 0) {
                                logger.info("委托价格不优于指令价格系统参数[{}]，买卖方向[{}]，委托类型[{}],存在限价指令[{}]指令价格[{}]大于跌停价[{}]",
                                        commonManagement.getFairModeEntrustPriceBestThanInstruction(),
                                        order.getEntrustBs(),
                                        order.getEntrustProp(),
                                        entry.getValue().getId(),
                                        entry.getValue().getEntrustPrice(),
                                        upperLimitPrice);
                                return true;
                            }
                            return false;
                        });
                    }
                    if (flag) {
                        logger.info("指令中包含了限价指令，不允许下市价委托");
                        instructionMap.clear();
                    }
                }
            }
        } else {
            // 委托价格不优于指令价格系统参数 02时，若委托价格为市价，则限价指令不参与公平委托
            instructionMap = instructionService.queryInstructionByUserIdAndStockCode(order.getUserId(), order.getStockCode())
                    .stream().filter(instruction -> InvestmentUtils.hasSameDir(instruction, order)
                            && InstructionStatus.ALREADY_RECEIVE.value.equals(instruction.getEntrustStatus())
                            && InvestmentUtils.isNormalTradeFromInstruction(instruction.getTradePlat())
                            && !InvestmentUtils.isInstructionProcessComplete(instruction)
                            && !InstructionPriceMode.MARKET.value.equals(instruction.getPriceMode()))
                    .collect(Collectors.toMap(Instruction::getEntrustId, instructionBean -> instructionBean));
        }
        logger.debug("公平委托相关指令: {}", JsonUtils.getJsonFromObject(instructionMap));
        return instructionMap;
    }

    public void handleFairOrder(Order order, WorkflowBean workflow) {
        //下委托及风险审批
        Map<String, Instruction> instructionBeanMap = getInstructionBeans(order);
        Map<String, Entrust> fairEntrusts = new HashMap<>();
        Map<String, Double> volumeMap = new HashMap<>();
        double totalVolume = getFairOrderVolume(instructionBeanMap, order, volumeMap);
        double totalOrderVolume = order.getOrderVolume();
        instructionBeanMap.values().forEach(instruction -> {
            Entrust fairEntrust = tradeHelper.convertOrderToEntrust(order);
            fairEntrust.setInstructionId(instruction.getEntrustId());
            fairEntrust.setAccountId(instruction.getAcctNo());
            fairEntrust.setOffsetFlag(DerivativeOcFlag.getOffsetFlagByDerivativeOcFlag(instruction.getDerivativeOcFlag()));
            //fairEntrust.setHedgeFlag(instruction.getHedgeFlag());
            fairEntrust.setFinanceAccount(instruction.getBusiAcctNo());
            fairEntrust.setPortfolioId(instruction.getPortfolioId());
            fairEntrust.setParentOrderId(order.getId());
            double leftVolume = volumeMap.get(instruction.getEntrustId());
            int realVolume = 0;
            if (InvestmentAssetType.BOND.value.equals(fairEntrust.getAssetType())) {
                realVolume = getRealVolume(totalVolume, totalOrderVolume, leftVolume, 1);
            } else if (InvestmentAssetType.PLEDGE_REPURCHASE.value.equals(fairEntrust.getAssetType())) {
                if (Market.SZSE.value.equals(fairEntrust.getMarket())) {
                    realVolume = getRealVolume(totalVolume, totalOrderVolume, leftVolume, 1);
                } else {
                    realVolume = getRealVolume(totalVolume, totalOrderVolume, leftVolume, 3);
                }
            } else if (InvestmentAssetType.FUTURES.value.equals(fairEntrust.getAssetType())) {
                realVolume = getRealVolume(totalVolume, totalOrderVolume, leftVolume, 0);
            } else {
                realVolume = getRealVolume(totalVolume, totalOrderVolume, leftVolume, 2);
            }
            fairEntrust.setEntrustVolume(Math.min((int) leftVolume, realVolume));
            fairEntrust.setEntrustPrice(order.getOrderPrice());
            fairEntrust.setFullPrice(order.getFullPrice());
            fairEntrust.setEntrustStatus(EntrustStatus.CREATED.value);
            FuturesInstrument futuresInstrument = futuresInstrumentService.queryByInnerCode(order.getStockCode());
            BigDecimal entrustBalance = TradeUtils.calculateBalance(fairEntrust.getEntrustBs(), fairEntrust.getEntrustPrice(), fairEntrust.getEntrustVolume(), fairEntrust.getAssetType(), fairEntrust, futuresInstrument);
            fairEntrust.setEntrustBalance(entrustBalance);
            order.setEntrustVolume(order.getEntrustVolume() + fairEntrust.getEntrustVolume());
            order.setOrderVolume(order.getEntrustVolume());
            order.setEntrustBalance(TradeUtils.add(order.getEntrustBalance(), entrustBalance, 2));
            logger.info("fairEntrust :[{}], instructionId [{}] entrustVolume[{}] entrustPrice[{}] fullPrice[{}]",
                    fairEntrust.getId(), fairEntrust.getInstructionId(), fairEntrust.getEntrustVolume(), fairEntrust.getEntrustPrice(), fairEntrust.getFullPrice());
            if (fairEntrust.getEntrustVolume() > 0) {
                if (tradeHelper.checkCommonParam(fairEntrust.getFullPrice(), fairEntrust.getEntrustPrice(), fairEntrust.getEntrustVolume(), fairEntrust)) {
                    fairEntrusts.put(fairEntrust.getInstructionId(), fairEntrust);
                } else {
                    fairEntrust.setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
                    logger.info("frozenInstruction failed entrustLeftAmt:[{}],frozenQuantity:[{}],EntrustVolume:[{}],EntrustBalance:[{}],",
                            instruction.getEntrustLeftAmt(), instruction.getFrozenQuantity(), fairEntrust.getEntrustVolume(), fairEntrust.getFrozenBalance());
                    entrustService.asyncUpdate(fairEntrust);
                }
            }
        });
        if (ObjectUtil.isNotEmpty(workflow)) {
            workflow.setOrderId(order.getId());
            ReviewManagement.putReviewMap(order.getId(), workflow);
        }
        orderService.asyncUpdate(order);
        if (fairEntrusts.values().isEmpty()) {
            order.setErrorCode(ErrorCode.NOT_EXIST_FAIR_INSTRUCTION_ERROR.value());
            order.setErrorMsg(ErrorCode.NOT_EXIST_FAIR_INSTRUCTION_ERROR.msg());
            return;
        }
        List<EntrustInterface> param = new ArrayList<>(fairEntrusts.values());
        List<ReqRiskTrigger> parameterList = new ArrayList<ReqRiskTrigger>(param.size());
        RiskOccupyBean riskOccupyBean = investRiskManagement.riskCheck(param, parameterList);
        if (Objects.isNull(riskOccupyBean) || RiskStatus.PASS.value.equals(riskOccupyBean.getRskStatus()) || RiskStatus.ALERT.value.equals(riskOccupyBean.getRskStatus())) {
            List<Entrust> entrustList = new ArrayList<>(fairEntrusts.values());
            tradeHelper.addFairEntrustToReceiver(entrustList);
            entrustService.asyncUpdate(fairEntrusts.values());
        } else if (riskOccupyBean.getRskStatus().equals(RiskStatus.NEED_APPROVE.value)) {
            Collection<Entrust> entrustList = fairEntrusts.values();
            tradeHelper.processFairRiskApprove(entrustList, order, riskOccupyBean, parameterList);
        } else {
            fairEntrusts.values().forEach(entrust -> {
                entrust.setEntrustStatus(EntrustStatus.NOT_RISK.value);
                entrust.setErrorMsg(riskOccupyBean.getMessage());
            });
            entrustService.asyncUpdate(fairEntrusts.values());
        }
    }
}
