package com.joyintech.tams.exchange.service.trade.processor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.joyintech.exchange.service.quote.service.QuoteService;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.exception.PositionException;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.common.bean.OrderPlaceBean;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.bean.WorkflowBean;
import com.joyintech.tams.exchange.common.bean.investment.ReqRiskTrigger;
import com.joyintech.tams.exchange.common.bean.investment.RiskOccupyBean;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.exception.PositionExchangeException;
import com.joyintech.tams.exchange.common.request.PositionInstructionRequest;
import com.joyintech.tams.exchange.common.response.FairPortfolioResponse;
import com.joyintech.tams.exchange.common.response.PortfolioAllocationResponse;
import com.joyintech.tams.exchange.common.utils.EntrustUtils;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import com.joyintech.tams.exchange.datacommon.db.service.*;
import com.joyintech.tams.exchange.service.trade.management.service.*;
import com.joyintech.tams.exchange.service.trade.position.service.PositionRequestServiceInstructionRequest;
import com.joyintech.tams.exchange.service.trade.processor.business.bond.BondHelper;
import com.joyintech.tams.exchange.service.trade.processor.msg.MsgType;
import com.joyintech.tams.exchange.service.trade.processor.msg.TradeMsgReceiver;
import org.apache.commons.collections.CollectionUtils;
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

import static com.joyintech.tams.exchange.common.constant.ErrorCode.INSTRUCTIONS_BUY_OPTIMAL_PRICE_ERROR;
import static com.joyintech.tams.exchange.common.constant.ErrorCode.INSTRUCTIONS_SELL_OPTIMAL_PRICE_ERROR;

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
    @Autowired
    private PositionRequestServiceInstructionRequest positionRequestServiceInstructionRequest;
    @Autowired
    private PortfolioManager portfolioManager;

    private int getRealVolume(double totalVolume, double totalOrderVolume, double leftVolume, int unit) {
        return (int) TradeUtils.formatDouble(totalOrderVolume * leftVolume / totalVolume, -unit, RoundingMode.UP);
    }

    /**
     * 计算指令剩余委托数量之和
     *
     * @param instructionBeanMap 公平指令集合
     * @param order              下单对象
     * @param volumeMap          指令剩余委托数量集合
     * @return
     */
    private double getFairOrderVolume(Map<String, Instruction> instructionBeanMap, Order order, Map<String, Double> volumeMap) {
        BigDecimal price = order.getOrderPrice();
        Instrument instrument = instrumentService.queryByInnerCode(order.getStockCode());
//        if (EntrustUtils.isFullPrice(instrument, order.getAssetType(), order.getEntrustProp(), order.getIsFullPrice())) {
//            // 指令金额计算为净价
////            price = order.getFullPrice();
//            order.setIsFullPrice(FullPriceConst.FULL_PRICE.value);
//        } else {
//            order.setIsFullPrice(FullPriceConst.NET_PRICE.value);
//        }
        if (!order.getEntrustProp().equals(EntrustProp.NORMAL.value)) {
            ResultBean tickBean = quoteService.getTick(order.getStockCode());
            if (ErrorCode.isSuccess(tickBean.getErrorCode())) {
                TickBean tick = (TickBean) tickBean.getData();
//            QuoteProto.TickBean tickBean = quoteTickService.getTickFromCache(order.getStockCode());
//            if (Objects.nonNull(tickBean)) {
                price = order.getEntrustBs().equals(EntrustBs.BUY.value) ? TradeUtils.getBigDecimal(tick.getUpperLimitPrice(), 4) : TradeUtils.getBigDecimal(tick.getLowerLimitPrice(), 4);
                // 转换为净价计算 指令数量
                if (EntrustUtils.isFullPrice(instrument, order.getAssetType(), order.getEntrustProp(), order.getIsFullPrice())) {
                    Instruction instruction = instructionService.findById(order.getStockCode());
                    if (Objects.nonNull(instruction)) {
                        BigDecimal minPrice = instruction.queryMinPrice(order.getEntrustProp());
                        int round = Objects.nonNull(minPrice) ? minPrice.scale() : 4;
                        price = investmentManagement.queryEntrustPriceByFullPriceFromInvest(price, order.getEntrustVolume(), instruction, round);
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

    public boolean createFairOrder(Order order, Instruction instruction) {
        // 判断order是否要做公平交易
        if (!Strings.isNullOrEmpty(order.getAlgorithmId())
                || !EntrustUtils.isFairOrder(order.getEntrustProp())
                || !EntrustUtils.isFairSwitchOpen(commonManagement.getFairCode())) {
            return false;
        }

        OrderPlaceBean orderPlaceBean = convertOrder2OrderPlaceBean(order);
        ResultBean resultBean = portfolioManager.queryPortfolioCalculateWithFair(orderPlaceBean);

        PortfolioAllocationResponse portfolioAllocationResponse = Convert.convert(PortfolioAllocationResponse.class, resultBean.getData());
        List<FairPortfolioResponse> instructions = portfolioAllocationResponse.getList();
        Map<String, List<Instruction>> instructionMap = instructions.stream().collect(Collectors.groupingBy(Instruction::getEntrustId));
        if (instructionMap.size() <= 1) {
            // 走普通委托
            return false;
        }

        if (StrUtil.isNotEmpty(portfolioAllocationResponse.getInfoLevel())
                && portfolioAllocationResponse.getInfoLevel().equals("1")) {
            logger.info("fair order failed");
            order.setOrderStatus(OrderStatus.STOP.value);
            return true;
        }

        long totoleNum = instructions.stream().map(Instruction::getTermDays).reduce(BigDecimal.ZERO, BigDecimal::add).longValue();
        if (!EntrustUtils.isfairModeEntrustPriceBestThanInstructionForbid() && totoleNum == 0) {
            //卖出价格不得小于最优价，买入不得高于最优价
            if (order.getEntrustBs().equals(EntrustBs.SELL.value)) {
                order.setErrorCode(INSTRUCTIONS_SELL_OPTIMAL_PRICE_ERROR.value);
                order.setErrorMsg(INSTRUCTIONS_SELL_OPTIMAL_PRICE_ERROR.errorMsg);
                return true;
            } else if (order.getEntrustBs().equals(EntrustBs.BUY.value)) {
                order.setErrorCode(INSTRUCTIONS_BUY_OPTIMAL_PRICE_ERROR.value);
                order.setErrorMsg(INSTRUCTIONS_BUY_OPTIMAL_PRICE_ERROR.errorMsg);
                return true;
            }
        }

        order.setBusinessFlag(BusinessFlag.FAIR.value);
        orderService.asyncUpdate(order);
        return true;
    }

    public boolean isFairOrder(OrderPlaceBean orderPlaceBean) {
        ResultBean resultBean = portfolioManager.queryPortfolioCalculateWithFair(orderPlaceBean);

        PortfolioAllocationResponse portfolioAllocationResponse = Convert.convert(PortfolioAllocationResponse.class, resultBean.getData());
        if (StrUtil.isNotEmpty(portfolioAllocationResponse.getInfoLevel())) {
            return false;
        }

        return CollectionUtils.isNotEmpty(portfolioAllocationResponse.getList());
    }

    private List<FairPortfolioResponse> getFairInstructions(Order order) {
        OrderPlaceBean orderPlaceBean = convertOrder2OrderPlaceBean(order);
        ResultBean resultBean = portfolioManager.queryPortfolioCalculateWithFair(orderPlaceBean);

        PortfolioAllocationResponse portfolioAllocationResponse = Convert.convert(PortfolioAllocationResponse.class, resultBean.getData());
        if (StrUtil.isNotEmpty(portfolioAllocationResponse.getInfoLevel()) &&
                StringUtil.equals(portfolioAllocationResponse.getInfoLevel(), "1")) {
            return ListUtil.empty();
        }
        return portfolioAllocationResponse.getList();
    }

    private OrderPlaceBean convertOrder2OrderPlaceBean(Order order) {
        OrderPlaceBean orderPlaceBean = new OrderPlaceBean();
        orderPlaceBean.setPortfolioId(order.getPortfolioId());
        orderPlaceBean.setUserId(order.getUserId());
        orderPlaceBean.setUsername(order.getUsername());
        orderPlaceBean.setAccountId(order.getAccountId());
        orderPlaceBean.setAlgorithmId(order.getAlgorithmId());
        orderPlaceBean.setBsFlag(order.getBsFlag());
        orderPlaceBean.setStockCode(order.getStockCode());
        orderPlaceBean.setEntrustBs(order.getEntrustBs());
        orderPlaceBean.setOffsetFlag(order.getOffsetFlag());
        orderPlaceBean.setHedgeFlag(order.getHedgeFlag());
        orderPlaceBean.setInstructionId(order.getInstructionId());
        orderPlaceBean.setEntrustProp(order.getEntrustProp());
        orderPlaceBean.setEntrustVolume(order.getOrderVolume());
        orderPlaceBean.setEntrustPrice(order.getOrderPrice());
        orderPlaceBean.setFullPrice(order.getFullPrice());
        orderPlaceBean.setEntrustBalance(order.getEntrustBalance());
        orderPlaceBean.setSoftSkipMode(order.getSoftSkipMode());
        return orderPlaceBean;
    }

    //下委托及风险审批
    public void handleFairOrder(Order order, WorkflowBean workflow) {
        List<FairPortfolioResponse> fairInstructions = getFairInstructions(order);
        // fairInstructions是根据组合维度的生成的，需要根据指令代码重新聚合，注意字段借用
        Map<String, List<Instruction>> instructionMap = fairInstructions.stream().collect(Collectors.groupingBy(Instruction::getEntrustId));

        // 委托明细
        Map<String, Entrust> fairEntrusts = MapUtil.newHashMap();
        for (Map.Entry<String, List<Instruction>> entry : instructionMap.entrySet()) {
            List<Instruction> instructions = entry.getValue();
            Instruction instruction = instructions.get(0);
            Entrust entrust = convertOrderToEntrust(order);
            fillEntrustWithInstruction(instruction, entrust);
            entrust.setEntrustVolume(instructions.stream().map(Instruction::getTermDays).reduce(BigDecimal.ZERO, BigDecimal::add).longValue());
            if (entrust.getEntrustVolume() <= 0) {
                continue;
            }

            entrust.setEntrustBalance(TradeUtils.calculateBalance(entrust.getEntrustBs(), entrust.getEntrustPrice(), entrust.getEntrustVolume(), entrust.getAssetType(), entrust));
            logger.info("entrust :[{}], instructionId [{}] entrustVolume[{}] entrustPrice[{}] fullPrice[{}]",
                    entrust.getId(), entrust.getInstructionId(), entrust.getEntrustVolume(), entrust.getEntrustPrice(), entrust.getFullPrice());

            fairEntrusts.put(entrust.getInstructionId(), entrust);
        }

        if (fairEntrusts.size() == 0) {
            order.setErrorCode(ErrorCode.NOT_EXIST_FAIR_INSTRUCTION_ERROR.value());
            order.setErrorMsg(ErrorCode.NOT_EXIST_FAIR_INSTRUCTION_ERROR.msg());
            return;
        }

        Collection<Entrust> entrusts = fairEntrusts.values();
        dealOrderVolumeAndBalance(entrusts, order);
        checkPosition(entrusts, order);
        checkRisk(entrusts, order);

        if (ObjectUtil.isNotEmpty(workflow)) {
            workflow.setOrderId(order.getId());
            ReviewManagement.putReviewMap(order.getId(), workflow);
        }
    }

    private void dealOrderVolumeAndBalance(Collection<Entrust> entrusts, Order order) {
        long sumEntrustVolume = entrusts.stream().mapToLong(Entrust::getEntrustVolume).sum();
        BigDecimal sumEntrustBalance = entrusts.stream().map(Entrust::getEntrustBalance).reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setEntrustVolume(sumEntrustVolume);
        order.setOrderVolume(sumEntrustVolume);
        order.setEntrustBalance(NumberUtil.round(sumEntrustBalance, 2));
        order.setInstructionId("fairTrade");
        orderService.asyncUpdate(order);
    }

    private int dealEntrustVolume(Map<String, Double> volumeMap, double totalVolume, double totalOrderVolume, Entrust entrust) {
        double leftVolume = volumeMap.get(entrust.getInstructionId());
        int realVolume = 0;
        if (InvestmentAssetType.BOND.value.equals(entrust.getAssetType())) {
            realVolume = getRealVolume(totalVolume, totalOrderVolume, leftVolume, 1);
        } else if (InvestmentAssetType.PLEDGE_REPURCHASE.value.equals(entrust.getAssetType())) {
            if (Market.SZSE.value.equals(entrust.getMarket())) {
                realVolume = getRealVolume(totalVolume, totalOrderVolume, leftVolume, 1);
            } else {
                realVolume = getRealVolume(totalVolume, totalOrderVolume, leftVolume, 3);
            }
        } else if (InvestmentAssetType.FUTURES.value.equals(entrust.getAssetType())) {
            realVolume = getRealVolume(totalVolume, totalOrderVolume, leftVolume, 0);
        } else {
            realVolume = getRealVolume(totalVolume, totalOrderVolume, leftVolume, 2);
        }
        return Math.min((int) leftVolume, realVolume);
    }

    private void fillEntrustWithInstruction(Instruction instruction, Entrust entrust) {
        entrust.setInstructionId(instruction.getEntrustId());
        entrust.setAccountId(instruction.getAcctNo());
        entrust.setAccountName(instruction.getAcctName());
        entrust.setOffsetFlag(DerivativeOcFlag.getOffsetFlagByDerivativeOcFlag(instruction.getDerivativeOcFlag()));
        entrust.setFinanceAccount(instruction.getBusiAcctNo());
        entrust.setPortfolioId(instruction.getPortfolioId());
        entrust.setPortfolioName(instruction.getPortfolioName());
        entrust.setSecAcctId(instruction.getSecAcctId());
        entrust.setSecAcctName(instruction.getSecAcctName());
        if (StringUtil.equals(instruction.getIsNetPrice(), "N")) {
            entrust.setIsFullPrice("Y");
        } else {
            entrust.setIsFullPrice("N");
        }
    }

    public Entrust convertOrderToEntrust(Order order) {
        if (order == null) {
            return null;
        }
        Entrust entrust = new Entrust();
//        entrust.setId(order.getId());
//        entrust.setInstructionId(order.getInstructionId());
//        entrust.setPortfolioId(order.getPortfolioId());
        entrust.setUserId(order.getUserId());
        entrust.setUsername(order.getUsername());
//        entrust.setAccountId(order.getAccountId());
//        entrust.setAccountName(order.getAccountName());
//        entrust.setFinanceAccount(order.getFinanceAccount());
        entrust.setAssetType(order.getAssetType());
        entrust.setEntrustType(order.getEntrustType());
        entrust.setEntrustProp(order.getEntrustProp());
        entrust.setEntrustBs(order.getEntrustBs());
        entrust.setBsFlag(order.getBsFlag());
        entrust.setStockCode(order.getStockCode());
        entrust.setStockName(order.getStockName());
//        entrust.setEntrustVolume(order.getEntrustVolume());
        entrust.setEntrustPrice(order.getOrderPrice());
        entrust.setFullPrice(order.getFullPrice());
        entrust.setExpireYearRate(order.getExpireYearRate());
//        entrust.setEntrustBalance(order.getEntrustBalance());
//        entrust.setFrozenBalance(order.getFrozenBalance());
//        entrust.setFrozenVolume(order.getFrozenVolume());
//        entrust.setTradedBalance(order.getTradedBalance());
//        entrust.setTradedVolume(order.getTradedVolume());
//        entrust.setTradedPrice(order.getTradedPrice());
//        entrust.setCancelVolume(order.getCancelVolume());
//        entrust.setErrorCode(order.getErrorCode());
//        entrust.setErrorMsg(order.getErrorMsg());
        entrust.setTradingDay(order.getTradingDay());
        entrust.setIpMac(order.getIpMac());
        entrust.setRemark(order.getRemark());
//        entrust.setCreateTime(order.getCreateTime());
//        entrust.setUpdateTime(order.getUpdateTime());
        entrust.setPs(order.getPs());
        entrust.setCount(order.getCount());
//        entrust.setIsFullPrice(order.getIsFullPrice());
//        entrust.setOffsetFlag(order.getOffsetFlag());
//        entrust.setHedgeFlag(order.getHedgeFlag());
//        entrust.setPortfolioName(order.getPortfolioName());
//        entrust.setSecAcctId(order.getSecAcctId());
//        entrust.setSecAcctName(order.getSecAcctName());
        entrust.setTradePlat(order.getTradePlat());
        entrust.setFinprodType2(order.getFinprodType2());
        entrust.setInvManagerId(order.getInvManagerId());
        entrust.setBusiTypeTg(order.getBusiTypeTg());
        entrust.setExtend1(order.getExtend1());
        entrust.setExtend2(order.getExtend2());
        entrust.setTradeDate(order.getTradeDate());
        entrust.setCreateDateTime(order.getCreateDateTime());
        entrust.setConNum(order.getConNum());
        entrust.setEntrustClass(order.getEntrustClass());
        entrust.setTargetType(order.getTargetType());
        entrust.setIsNoGuard(order.getIsNoGuard());

        entrust.setCreateTime(DateUtil.now());
        entrust.setUpdateTime(entrust.getCreateTime());
        if (Objects.nonNull(order.getStockCode())) {
            entrust.setMarket(StockCodeUtils.getMarket(order.getStockCode()));
        }
        entrust.setStockCode(order.getStockCode());
        entrust.setEntrustStatus(EntrustStatus.CREATED.value);
        entrust.setId(entrustService.genDbId());
        entrust.setParentOrderId(order.getId());
        return entrust;
    }

    // 批量调用风险三合一
    private void checkRisk(Collection<Entrust> entrusts, Order order) {
        List<EntrustInterface> param = new ArrayList<>(entrusts);
        List<ReqRiskTrigger> parameterList = new ArrayList<ReqRiskTrigger>(param.size());
        RiskOccupyBean riskOccupyBean = investRiskManagement.riskCheck(param, parameterList);
        if (Objects.isNull(riskOccupyBean) || RiskStatus.PASS.value.equals(riskOccupyBean.getRskStatus()) || RiskStatus.ALERT.value.equals(riskOccupyBean.getRskStatus())) {
            List<Entrust> entrustList = new ArrayList<>(entrusts);
            tradeHelper.addFairEntrustToReceiver(entrustList);
            entrustService.asyncUpdate(entrusts);
        } else if (riskOccupyBean.getRskStatus().equals(RiskStatus.NEED_APPROVE.value)) {
            Collection<Entrust> entrustList = entrusts;
            tradeHelper.processFairRiskApprove(entrustList, order, riskOccupyBean, parameterList);
        } else {
            entrusts.forEach(entrust -> {
                entrust.setEntrustStatus(EntrustStatus.NOT_RISK.value);
                entrust.setErrorMsg(riskOccupyBean.getMessage());
            });
            entrustService.asyncUpdate(entrusts);
        }
    }

    /*public boolean createFairOrder(Order order) {
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
                    TickBean tick = (TickBean) tickBean.getData();
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
        checkPosition(fairEntrusts.values(), order);
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
    }*/

    //头寸引擎余额检查
    @SuppressWarnings("rawtypes")
    private void checkPosition(Collection<Entrust> entrusts, Order order) {
        PositionException exception = null;
        List<String> errorMessages = Lists.newArrayList();
        for (Entrust entrust : entrusts) {
            String instructionId = entrust.getInstructionId();
            Instruction instruction = instructionService.findById(instructionId);
            PositionInstructionRequest instructionClone = new PositionInstructionRequest();
            BeanUtil.copyProperties(instruction, instructionClone);
            instructionClone.setSoftSkipMode(order.getSoftSkipMode());

            logger.info("公平交易头寸检查 => 【instructionId = {}】", instructionId);
            instructionClone.setEntrustAmt(entrust.getEntrustPrice().multiply(BigDecimal.valueOf(entrust.getEntrustVolume())));
            instructionClone.setEntrustNum(entrust.getEntrustVolume());
            instructionClone.setEntrustPrice(entrust.getEntrustPrice());
            instructionClone.setFullPrice(entrust.getFullPrice());
            instructionClone.setEntrustNo(instruction.getEntrustId());
            instructionClone.setEntrustProp(entrust.getEntrustProp());

            try {
                positionRequestServiceInstructionRequest.sendRequest(instructionClone, DictCodeListExt3.业务动作_交易下单.getValue(), DictCodeListExt3.系统动作_检查.getValue(), DictCodeListExt3.系统动作_新建.getValue());
            } catch (PositionException e) {
                exception = e;
                Message message = PositionUtil.ajaxErrorReturn(e);
                Message innerMessage = Convert.convert(Message.class, message.getData());
                // 拼接软控信息
                if ("000".equals(message.getCode()) && "300".equals(innerMessage.getCode())) {
                    errorMessages.add(innerMessage.getMessage().replace("，请确认是否继续？", ""));
                }
            } finally {
                if (ObjectUtil.isNotNull(exception)) {
                    // 非软控异常，抛出
                    Message message = PositionUtil.ajaxErrorReturn(exception);
                    if (!"000".equals(message.getCode())) {
                        throw exception;
                    }
                }
            }
        }
        if (CollUtil.isNotEmpty(errorMessages)) {
            String errorMessage = Joiner.on(StrUtil.COMMA).join(errorMessages) + "，请确认是否继续？";
            Message message = new Message("300", errorMessage, errorMessage);
            PositionExchangeException positionExchangeException = PositionExchangeException.create(message);
            throw positionExchangeException;
        }
    }
}
