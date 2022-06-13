package com.joyintech.tams.exchange.service.trade.processor.business.common;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Strings;
import com.joyintech.exchange.service.quote.service.QuoteService;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.MinTrdUnitLimit;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.bean.investment.TradeTimeBean;
import com.joyintech.tams.exchange.common.cache.TradeTimeService;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.constant.instrument.SubStockType;
import com.joyintech.tams.exchange.common.constant.joyin.InstructionPriceMode;
import com.joyintech.tams.exchange.common.constant.joyin.Switches;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.dto.EntrustPriceAndYieldDto;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import com.joyintech.tams.exchange.common.utils.EntrustUtils;
import com.joyintech.tams.exchange.common.utils.EnumUtil;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import com.joyintech.tams.exchange.datacommon.db.service.EntrustService;
import com.joyintech.tams.exchange.datacommon.db.service.InstrumentService;
import com.joyintech.tams.exchange.datacommon.db.service.IntegratedEntrustService;
import com.joyintech.tams.exchange.service.trade.management.service.CommonManagement;
import com.joyintech.tams.exchange.service.trade.management.service.InvestmentManagement;
import com.joyintech.tams.exchange.service.trade.processor.business.bond.BondHelper;
import com.joyintech.tams.exchange.service.trade.processor.business.price.PriceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Objects;
import java.util.Optional;

@Component
public class CommonCheckHelper {
    private static final Logger logger = LoggerFactory.getLogger(CommonCheckHelper.class);

    @Autowired
    private EntrustService entrustService;
    @Autowired
    private IntegratedEntrustService integratedEntrustService;
    //    @Autowired
//    private QuoteTickService quoteTickService;
    @Autowired
    private QuoteService quoteService;
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private InvestmentManagement investmentManagement;
    @Autowired
    private BondHelper bondHelper;

    private static final ThreadLocal<SimpleDateFormat> INSTRUCTION_DATE_FORMAT = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyyMMdd");
        }
    };

    @Autowired
    private TradeTimeService tradeTimeService;
    @Autowired
    private PriceHelper priceHelper;
    @Autowired
    private CommonManagement commonManagement;

    // modify by swwan TISHKS-263 闭市校验无效 20211216 start

    /**
     * 检查是否在交易时间段内
     * - 开启校验开关，交易时间段存在且当前时间不在交易时间段内，返回 true
     * - 其他，返回false
     */
    public boolean checkTradeTime(Instruction instruction, Instrument instrument, OrderInterface orderInterface) {

        // TODO 仅15:00-15:30允许下达收盘价固定价格委托，以及科创版校验
        if (Objects.isNull(instruction) || Objects.isNull(instrument) || Objects.isNull(orderInterface)) {
            logger.debug("checkTradeTime() called with parameters => 【instruction = {}】, 【instrument = {}】, 【orderInterface = {}】", instruction, instrument, orderInterface);
            return false;
        }
        if (Switches.CLOSE.value.equals(commonManagement.getTradeTimeSwitch())) {
            logger.debug("checkTradeTime system param: {} = {}", ExchangeConstant.ETR_TRADE_TIME_SWITCH, commonManagement.getTradeTimeSwitch());
            return false;
        }

        TradeTimeBean tradeTimeBean = tradeTimeService.getTradeTimeByInstructionAndEntrustProp(instruction, instrument.getAssetType1(), instrument.getSubStockType(), orderInterface.getEntrustProp());

        if (Objects.isNull(tradeTimeBean)) {
            logger.warn("获取交易时间段信息失败：instructionId {}", instruction.getEntrustId());
            return false;
        }
        logger.debug("checkTradeTime tradeTimeBean : [{}]", JSON.toJSONString(tradeTimeBean));

        // 早盘
        String earlyOpenTime = tradeTimeBean.getEarlyOpenTime();
        String earlyCloseTime = tradeTimeBean.getEarlyCloseTime();
        // 午盘
        String noonOpenTime = tradeTimeBean.getNoonOpenTime();
        String noonCloseTime = tradeTimeBean.getNoonCloseTime();
        // 晚盘
        String nightOpenTime = tradeTimeBean.getNightOpenTime();
        String nightCloseTime = tradeTimeBean.getNightCloseTime();

        LocalTime now = LocalTime.now().withNano(0);

        if (!(isTradeTime(earlyOpenTime, earlyCloseTime, now) || isTradeTime(noonOpenTime, noonCloseTime, now) || isTradeTime(nightOpenTime, nightCloseTime, now))) {

            StringBuilder builder = new StringBuilder();
            builder.append("当前时间[").append(now).append("]不允许委托,允许委托时间段为：[");
            if (StringUtil.isNotEmpty(earlyOpenTime) && StringUtil.isNotEmpty(earlyCloseTime)) {
                builder.append(earlyOpenTime).append("~").append(earlyCloseTime).append(";");
            }
            if (StringUtil.isNotEmpty(noonOpenTime) && StringUtil.isNotEmpty(noonCloseTime)) {
                builder.append(noonOpenTime).append("~").append(noonCloseTime).append(";");
            }
            if (StringUtil.isNotEmpty(nightOpenTime) && StringUtil.isNotEmpty(nightCloseTime)) {
                builder.append(nightOpenTime).append("~").append(nightCloseTime).append(";");
            }
            builder.append("]");
            orderInterface.setErrorMsg(builder.toString());
            orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
            return true;
        }
        return false;
    }

    /**
     * 判断是否在交易时间端内
     */
    private boolean isTradeTime(String openTime, String closeTime, LocalTime now) {
        return StringUtil.isNotEmpty(openTime) && StringUtil.isNotEmpty(closeTime) && now.compareTo(LocalTime.parse(openTime)) >= 0 && now.compareTo(LocalTime.parse(closeTime)) <= 0;
    }


   /* public boolean checkTradeTime(Instruction instruction, Instrument instrument, OrderInterface orderInterface) {
        TradeTimeBean tradeTimeBean = tradeTimeService.getTradeTimeByInstructionAndEntrustProp(instruction, instrument.getAssetType1(), instrument.getSubStockType(), orderInterface.getEntrustProp());
        if (Objects.nonNull(tradeTimeBean)) {
            String earlyOpenTime = tradeTimeBean.getEarlyOpenTime();
            String earlyCloseTime = tradeTimeBean.getEarlyCloseTime();
            String noonOpenTime = tradeTimeBean.getNoonOpenTime();
            String noonCloseTime = tradeTimeBean.getNoonCloseTime();
            String nightOpenTime = tradeTimeBean.getNightOpenTime();
            String nightCloseTime = tradeTimeBean.getNightCloseTime();
            LocalTime now = LocalTime.now().withNano(0);
            if (!((!Strings.isNullOrEmpty(earlyOpenTime) && !Strings.isNullOrEmpty(earlyCloseTime) && now.compareTo(LocalTime.parse(earlyOpenTime)) >= 0
                    && now.compareTo(LocalTime.parse(earlyCloseTime)) <= 0)
                    || (!Strings.isNullOrEmpty(noonOpenTime) && !Strings.isNullOrEmpty(noonCloseTime) && (now.compareTo(LocalTime.parse(noonOpenTime)) >= 0
                    && now.compareTo(LocalTime.parse(noonCloseTime)) <= 0))
                    || (!Strings.isNullOrEmpty(nightOpenTime) && !Strings.isNullOrEmpty(nightCloseTime) && (now.compareTo(LocalTime.parse(nightOpenTime)) >= 0
                    && now.compareTo(LocalTime.parse(nightCloseTime)) <= 0)))) {
                orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
                String errorMsg = "当前时间[" + now + "]不允许委托,允许委托时间段为：";
                if (!Strings.isNullOrEmpty(earlyOpenTime) && !Strings.isNullOrEmpty(earlyCloseTime)) {
                    errorMsg += earlyOpenTime + "~" + earlyCloseTime + ";";
                }
                if (!Strings.isNullOrEmpty(noonOpenTime) && !Strings.isNullOrEmpty(noonCloseTime)) {
                    errorMsg += noonOpenTime + "~" + noonCloseTime + ";";
                }
                if (!Strings.isNullOrEmpty(nightOpenTime) && !Strings.isNullOrEmpty(nightCloseTime)) {
                    errorMsg += nightOpenTime + "~" + nightCloseTime;
                }
                orderInterface.setErrorMsg(errorMsg);
                return true;
            }
        } else {
            logger.info("获取交易时间段信息失败：instructionId {}", instruction.getEntrustId());
        }
        return false;
    }*/
    // modify by swwan TISHKS-263 闭市校验无效 20211216 end

    public String checkApplyEntrust(String entrustProp, String accountId, String stockCode, String instructionId) {
        //申购与配售只能申报一次
        if (EntrustProp.APPLY.value.equals(entrustProp) || EntrustProp.PS.value.equals(entrustProp)) {
            Optional<Entrust> entrustOptional = entrustService.queryEntrustByAccountId(accountId).stream().filter(entrust -> entrust.getEntrustProp().equals(entrustProp) && (entrust.getEntrustStatus() == EntrustStatus.REPORTED.value || entrust.getEntrustStatus() == EntrustStatus.PENDING_APPROVAL.value || entrust.getEntrustStatus() == EntrustStatus.PENDING_REVIEW.value) && entrust.getStockCode().equals(stockCode)).findAny();
            if (entrustOptional.isPresent()) {
                return entrustOptional.get().getId();
            }
        } else if (EntrustProp.SH_REPURCHASE_RENEWAL.value.equals(entrustProp)) {
            //到期续做只能做一次;在途或已成时，不能再报
            Optional<IntegratedEntrust> entrustOptional = integratedEntrustService.queryIntegratedEntrustByInstructionId(instructionId).stream().filter(entrust -> entrust.getEntrustProp().equals(entrustProp) && (EntrustStatus.PART_TRADED_CANCELED.value > entrust.getEntrustStatus() || entrust.getEntrustStatus() == EntrustStatus.SUCCEEDED.value)).findAny();
            if (entrustOptional.isPresent()) {
                return entrustOptional.get().getId();
            }
        }
        return null;
    }

    public boolean checkCommonParam(BigDecimal entrustPrice, long entrustVolume, OrderInterface orderInterface, Instrument instrument, String tradePlat, String instructionPs, BigDecimal entrustBalance, String entrustBussType, String priceMode) {
        if (EntrustBs.BUY.value.equals(orderInterface.getEntrustBs()) && instrument.getBuyUnit() > 0 && entrustVolume % instrument.getBuyUnit() != 0) {
            orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
            orderInterface.setErrorMsg("不满足最小买入单位的整数倍，报单数量:" + entrustPrice.longValue() + ",买入最小单位:" + instrument.getBuyUnit());
            return false;
        }
        // 申报类不需要check最小价差和涨跌停
        if (!EntrustUtils.isNoTrade(orderInterface.getEntrustProp())) {
            if (Double.compare(instrument.getPriceStep().doubleValue(), 0.0) > 0 && TradeUtils.divideAndRemainder(entrustPrice, instrument.getPriceStep()).compareTo(BigDecimal.ZERO) != 0) {
                orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value());
                orderInterface.setErrorMsg("委托价格不满足最小价差：" + instrument.getPriceStep().doubleValue());
                return false;
            }
            ResultBean tickBean = quoteService.getTick(orderInterface.getStockCode());
            if (ErrorCode.isSuccess(tickBean.getErrorCode())) {
                TickBean tick = (TickBean) tickBean.getData();
//            QuoteProto.TickBean tick = quoteTickService.getTickFromCache(orderInterface.getStockCode());
//            if (Objects.nonNull(tick)) {
                double lowerLimitPrice = TradeUtils.formatDouble(tick.getLowerLimitPrice(), 4);
                double upperLimitPrice = TradeUtils.formatDouble(tick.getUpperLimitPrice(), 4);
                if (Double.compare(lowerLimitPrice, 0.0) == 0 && Double.compare(upperLimitPrice, 0.0) == 0) {
                    logger.info("没有涨跌幅");
                    boolean checkPrice = priceHelper.checkPrice(entrustPrice, orderInterface, instrument, tick, tradePlat, entrustBussType, priceMode);
                    if (!checkPrice) {
                        return false;
                    }
                } else {
                    if ((Double.compare(lowerLimitPrice, 0.0) > 0 && Double.compare(lowerLimitPrice, entrustPrice.doubleValue()) > 0) || (Double.compare(upperLimitPrice, 0.0) > 0 && Double.compare(upperLimitPrice, entrustPrice.doubleValue()) < 0)) {
                        orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
                        orderInterface.setErrorMsg("不满足涨跌停限制。 跌停价：[" + lowerLimitPrice + "] -- 涨停价：[" + upperLimitPrice + "]; 委托价格：[" + entrustPrice + "]");
                        return false;
                    }
                }
            }
        }
        MinTrdUnitLimit minTrdUnitLimit = instrumentService.getMinTrdUnitLimit(tradePlat, instrument.getMarket(), instrument, instructionPs);
        if (Objects.nonNull(minTrdUnitLimit) && EntrustBs.BUY.value.equals(orderInterface.getEntrustBs())) {
            BigInteger bgEntrustVolume = BigInteger.valueOf(entrustVolume);
            Integer minNum;
            if (EntrustProp.isMarketPrice(orderInterface.getEntrustProp())) {
                minNum = minTrdUnitLimit.getMinNumMarket();
            } else {
                minNum = minTrdUnitLimit.getMinNum();
            }
            if (bgEntrustVolume.compareTo(BigInteger.valueOf(minNum)) < 0 && entrustBalance.compareTo(minTrdUnitLimit.getMinAmt()) < 0) {
                orderInterface.setErrorCode(ErrorCode.INNER_RISK_ERROR.value);
                orderInterface.setErrorMsg("不满足交易最小买入数量:" + minNum + ",或者金额" + minTrdUnitLimit.getMinAmt());
                return false;
            }
        }
        return true;
    }

    public ErrorCode instructionCheck(Instruction instruction, OrderInterface order, BigDecimal orderPrice, String tradingDay) {
        //权限指令的接收人是否是当前交易员
        if (Objects.isNull(instruction)) {
            return ErrorCode.INSTRUCTIONS_NOT_EXIST;
        }
        if (!InstructionStatus.ALREADY_RECEIVE.value.equals(instruction.getEntrustStatus().trim())) {
            return ErrorCode.INSTRUCTIONS_STATUS_ERROR;
        }
        if (!instruction.getTradeId().equals(order.getUserId())) {
            return ErrorCode.INSTRUCTION_ACCEPT_USER_ERROR;
        }
        if (INSTRUCTION_DATE_FORMAT.get().format(instruction.getVdate()).compareTo(tradingDay) > 0) {
            logger.error("指令的开始日[{}]大于平台日[{}]", INSTRUCTION_DATE_FORMAT.get().format(instruction.getVdate()), tradingDay);
            return ErrorCode.INSTRUCTION_BEGIN_DATE_ERROR;
        }
        if (INSTRUCTION_DATE_FORMAT.get().format(instruction.getMdate()).compareTo(tradingDay) < 0) {
            logger.error("指令的结束日[{}]小于平台日[{}]", INSTRUCTION_DATE_FORMAT.get().format(instruction.getMdate()), tradingDay);
            return ErrorCode.INSTRUCTION_END_DATE_ERROR;
        }
//        if (instruction.getPriceMode().equals(InstructionPriceMode.SPECIFIED.value) && !TradeUtils.equals(orderPrice, instruction.getEntrustPrice())
//                || (instruction.getPriceMode().equals(InstructionPriceMode.FIXED.value) && checkPriceFixed(order, instruction.getEntrustPrice(), orderPrice))) {
//            return ErrorCode.INSTRUCTIONS_PRICE_ERROR;
//        }
        if (checkOrderPrice(instruction, order, orderPrice)) {
            String ps = instruction.getPs();
            if (StringUtil.equals(ps, InstructionPs.BUY.value)) {
                return ErrorCode.INSTRUCTIONS_BUY_PRICE_ERROR;
            } else if (StringUtil.equals(ps, InstructionPs.SELL.value)) {
                return ErrorCode.INSTRUCTIONS_SELL_PRICE_ERROR;
            } else if (StringUtil.equals(ps, InstructionPs.POSITIVEREPO.value)) {
                return ErrorCode.INSTRUCTIONS_POSITIVEREPO_PRICE_ERROR;
            } else if (StringUtil.equals(ps, InstructionPs.REVERSEDREPO.value)) {
                return ErrorCode.INSTRUCTIONS_REVERSEDREPO_PRICE_ERROR;
            }

            return ErrorCode.INSTRUCTIONS_PRICE_ERROR;
        }
        if (InvestAssetTypeEnum.FUTURES.value.equals(instruction.getAssetType())) {
            if (Strings.isNullOrEmpty(instruction.getDerivativeBsDir()) || Strings.isNullOrEmpty(instruction.getDerivativeOcFlag())) {
                return ErrorCode.INSTRUCTIONS_DERIVATIVE_BS_OC_ERROR;
            }
            boolean flag = false;
            if (EntrustBs.BUY.value.equals(order.getEntrustBs()) && OffsetFlag.Open.value.equals(order.getOffsetFlag()) && InstructionPs.BUYOPEN.value.equals(instruction.getPs()) && DerivativeBsDir.BUY.value.equals(instruction.getDerivativeBsDir()) && DerivativeOcFlag.Open.value.equals(instruction.getDerivativeOcFlag())) {
                flag = true;
            } else if (EntrustBs.SELL.value.equals(order.getEntrustBs()) && !OffsetFlag.Open.value.equals(order.getOffsetFlag()) && InstructionPs.SELLCLOSE.value.equals(instruction.getPs()) && DerivativeBsDir.SELL.value.equals(instruction.getDerivativeBsDir()) && DerivativeOcFlag.Close.value.equals(instruction.getDerivativeOcFlag())) {
                flag = true;
            } else if (EntrustBs.SELL.value.equals(order.getEntrustBs()) && OffsetFlag.Open.value.equals(order.getOffsetFlag()) && InstructionPs.SELLOPEN.value.equals(instruction.getPs()) && DerivativeBsDir.SELL.value.equals(instruction.getDerivativeBsDir()) && DerivativeOcFlag.Open.value.equals(instruction.getDerivativeOcFlag())) {
                flag = true;
            } else if (EntrustBs.BUY.value.equals(order.getEntrustBs()) && !OffsetFlag.Open.value.equals(order.getOffsetFlag()) && InstructionPs.BUYCLOSE.value.equals(instruction.getPs()) && DerivativeBsDir.BUY.value.equals(instruction.getDerivativeBsDir()) && DerivativeOcFlag.Close.value.equals(instruction.getDerivativeOcFlag())) {
                flag = true;
            }
            if (!flag) {
                return ErrorCode.INSTRUCTIONS_DERIVATIVE_BS_OC_NOT_MATCH_ERROR;
            }
        }
        return ErrorCode.SUCCESS;
    }

    /**
     * 检查下单价格：返回true 检查失败
     */
    private boolean checkOrderPrice(Instruction instruction, OrderInterface order, BigDecimal orderPrice) {
        //债券买卖
        if (InvestAssetTypeEnum.BOND.codeEquals(instruction.getAssetType())
                && EnumUtil.in(instruction.getPs(), InstructionPs.BUY, InstructionPs.SELL)) {
            String priceMode = instruction.getPriceMode();
            String isFullPrice = order.getIsFullPrice();
            if (StringUtil.equals(priceMode, InstructionPriceMode.SPECIFIED.value)) {
                if (StringUtil.equals(FullPriceConst.FULL_PRICE.value, isFullPrice)) {
                    return !TradeUtils.equals(orderPrice, instruction.getFullPrice());
                } else {
                    return !TradeUtils.equals(orderPrice, instruction.getEntrustPrice());
                }
            } else if (StringUtil.equals(priceMode, InstructionPriceMode.FIXED.value)) {
                if (StringUtil.equals(FullPriceConst.FULL_PRICE.value, isFullPrice)) {
                    return checkPriceFixed(order, instruction.getFullPrice(), orderPrice);
                } else {
                    return checkPriceFixed(order, instruction.getEntrustPrice(), orderPrice);
                }
            }
        }

        //其他交易
        if (instruction.getPriceMode().equals(InstructionPriceMode.SPECIFIED.value) && !TradeUtils.equals(orderPrice, instruction.getEntrustPrice())
                || (instruction.getPriceMode().equals(InstructionPriceMode.FIXED.value) && checkPriceFixed(order, instruction.getEntrustPrice(), orderPrice))) {
            return true;
        }

        return false;
    }

    //超限返回true
    private boolean checkPriceFixed(OrderInterface order, BigDecimal fixedPrice, BigDecimal orderPrice) {
        return order.getEntrustBs().equals(EntrustBs.SELL.value) && TradeUtils.bigThan(fixedPrice, orderPrice)
                || order.getEntrustBs().equals(EntrustBs.BUY.value) && TradeUtils.lessThan(fixedPrice, orderPrice);
    }

    public void marketPriceCheck(Order order, Instrument instrument, Instruction instruction) {
        // 科创板 盘后定价 填写保护价格 不需要反向填充涨跌停价格
        if (EntrustProp.NORMAL_AFTER_SET_PRICE.getValue().equals(order.getEntrustProp())
                && Objects.nonNull(instrument)
                && SubStockType.KCB.value.equals(instrument.getSubStockType())
                && order.getOrderPrice().compareTo(BigDecimal.ZERO) > 0) {
            return;
        }

        //市价委托填充反向涨跌停价
        if (EntrustProp.isMarketPrice(order.getEntrustProp())) {
            ResultBean tickBean = quoteService.getTick(order.getStockCode());
            if (ErrorCode.isSuccess(tickBean.getErrorCode())) {
                TickBean tick = (TickBean) tickBean.getData();
                BigDecimal minPrice = instruction.queryMinPrice(order.getEntrustProp());
                int round = Objects.nonNull(minPrice) ? minPrice.scale() : 4;
//            QuoteProto.TickBean tick = quoteTickService.getTickFromCache(order.getStockCode());
//            if (Objects.nonNull(tick)) {
                BigDecimal price;
                if (order.getEntrustBs().equals(EntrustBs.SELL.value)) {
                    price = TradeUtils.getBigDecimal(tick.getLowerLimitPrice(), round, RoundingMode.UP);
                } else {
                    price = TradeUtils.getBigDecimal(tick.getUpperLimitPrice(), round, RoundingMode.DOWN);
                }
                order.setOrderPrice(price);

                //资产类型是债券
                if (InvestAssetTypeEnum.isBond(order.getAssetType())
                        && EnumUtil.in(instruction.getPs(), InstructionPs.BUY, InstructionPs.SELL)) {
                    EntrustPriceAndYieldDto dto = new EntrustPriceAndYieldDto();
                    dto.setPrice(price);
                    dto.setEntrustVolume(order.getOrderVolume());
                    dto.setRound(round);

                    //判断是否全价交易
                    boolean isFullPrice = FullPriceConst.FULL_PRICE.value.equals(instrument.getIsFullPrice());
                    investmentManagement.getAllPriceAndYield(instruction, dto, isFullPrice);
                    order.setOrderPrice(dto.getNetPrice());
                    order.setFullPrice(dto.getFullPrice());
                    order.setExpireYearRate(dto.getYield());
                }
            } else {
                order.setErrorCode(ErrorCode.QUOTATION_NOT_EXIST.value);
                order.setErrorMsg(ErrorCode.QUOTATION_NOT_EXIST.errorMsg);
            }
        }
    }
}
