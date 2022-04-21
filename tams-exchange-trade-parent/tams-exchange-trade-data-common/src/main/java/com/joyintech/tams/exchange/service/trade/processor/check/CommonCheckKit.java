package com.joyintech.tams.exchange.service.trade.processor.check;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.validation.BeanValidationResult;
import com.joyintech.exchange.service.quote.service.QuoteService;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.exchange.common.BusinessResultException;
import com.joyintech.tams.exchange.common.bean.MinPrice;
import com.joyintech.tams.exchange.common.bean.MinTrdUnitLimit;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.bean.dto.OpenCloseTime;
import com.joyintech.tams.exchange.common.bean.request.BaseOrderRequest;
import com.joyintech.tams.exchange.common.cache.TradeTimeService;
import com.joyintech.tams.exchange.common.constant.EntrustBs;
import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.enums.SwitcheEnum;
import com.joyintech.tams.exchange.common.utils.EntrustPropUtils;
import com.joyintech.tams.exchange.common.utils.EntrustUtils;
import com.joyintech.tams.exchange.datacommon.db.service.InstructionService;
import com.joyintech.tams.exchange.datacommon.db.service.InstrumentService;
import com.joyintech.tams.exchange.service.trade.processor.business.price.PriceHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalTime;
import java.util.List;

/**
 * 报单通用检查
 * <p/>
 * 硬控抛异常，软控错误信息添加到列表中
 *
 * @author swwan
 * @date 2022/4/13 - 15:54
 */
@Component
public class CommonCheckKit extends BaseCheckKit {

    /**
     * 系统参数：非交易时间段是否禁止场内交易下单（Y：禁止 N：不禁止）
     */
    public static final String TRD_TRADE_TIME_SWITCH = "trd_trade_time_switch";
    /**
     * 系统参数：场内交易下单价格超过有效价格申报范围（01 禁止；02 仅提示；03 允许）
     */
    public static final String TRD_PRICE_SCOPE_CONTROL = "trd_price_scope_control";
    public static final String TRD_PRICE_SCOPE_CONTROL_FORBID = "01";
    public static final String TRD_PRICE_SCOPE_CONTROL_PROMPT = "02";
    public static final String TRD_PRICE_SCOPE_CONTROL_ALLOW = "03";
    /**
     * 日志对象
     */
    private static final Logger log = LoggerFactory.getLogger(CommonCheckKit.class);
    private InstructionService instructionService;
    private InstrumentService instrumentService;
    private TradeTimeService tradeTimeService;
    private QuoteService quoteService;
    private PriceHelper priceHelper;

    @Autowired
    public void setInstructionService(InstructionService instructionService) {
        this.instructionService = instructionService;
    }

    @Autowired
    public void setInstrumentService(InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
    }

    @Autowired
    public void setTradeTimeService(TradeTimeService tradeTimeService) {
        this.tradeTimeService = tradeTimeService;
    }

    @Autowired
    public void setQuoteService(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @Autowired
    public void setPriceHelper(PriceHelper priceHelper) {
        this.priceHelper = priceHelper;
    }

    public ResultBean check(final BaseOrderRequest order) throws BusinessResultException {

        checkTime(order);
        checkPrice(order);
        checkPosition(order);

        if (checkResult.isSuccess()) {
            log.info("通用检查通过：订单信息 => 【{}】", order);
            return ResultBean.success();
        }

        List<BeanValidationResult.ErrorMessage> errorMessages = getCheckResult().getErrorMessages();
        StringBuilder builder = StrUtil.builder();
        for (BeanValidationResult.ErrorMessage errorMessage : errorMessages) {
            builder.append(StrUtil.format("{}\n", errorMessage.getMessage()));
        }

        String trdPriceScopeControl = CacheUtil.getSysParam(TRD_PRICE_SCOPE_CONTROL);
        if (TRD_PRICE_SCOPE_CONTROL_FORBID.equals(trdPriceScopeControl)) {
            throw new BusinessResultException(ErrorCode.INNER_RISK_ERROR,
                    builder.toString());
        } else if (TRD_PRICE_SCOPE_CONTROL_PROMPT.equals(trdPriceScopeControl) || trdPriceScopeControl == null) {
            if (SwitcheEnum.OPEN.codeEquals(order.getSoftSkipMode())) {
                return ResultBean.error(ErrorCode.INNER_RISK_ERROR, builder.toString());
            }
        } else if (TRD_PRICE_SCOPE_CONTROL_ALLOW.equals(trdPriceScopeControl)) {
        }
        return ResultBean.success();
    }


    /**
     * 检查是否在交易时间段内
     * - 开启校验开关，交易时间段存在且当前时间不在交易时间段内，返回 true
     * - 其他，返回false
     */
    public void checkTime(BaseOrderRequest order) throws BusinessResultException {

        Instruction instruction = getInstruction(order);

        String trdTradeTimeSwitch = CacheUtil.getSysParam(TRD_TRADE_TIME_SWITCH);
        // TODO  方便测试
        if (SwitcheEnum.OPEN.codeNotEquals(trdTradeTimeSwitch)) {
            log.debug("系统参数[{}:{}]，跳过交易时间检查", TRD_TRADE_TIME_SWITCH, trdTradeTimeSwitch);
            return;
        }

        List<OpenCloseTime> tradePeriod = tradeTimeService.getTradePeriod(instruction, order);
        String localTime = LocalTime.now().toString();
        boolean result = false;
        for (OpenCloseTime openCloseTime : tradePeriod) {
            result = DateUtil.isIn(DateUtil.parseTime(localTime), DateUtil.parseTime(openCloseTime.getOpenTime()), DateUtil.parseTime(openCloseTime.getCloseTime()));
            if (result) {
                break;
            }
        }

        // TODO: 方便测试
/*        if (!result) {
            StringBuilder builder = StrUtil.builder();
            tradePeriod.forEach(item -> builder.append(item.getOpenTime()).append("~").append(item.getCloseTime()).append(StrUtil.COMMA));
            throw new BusinessResultException(ErrorCode.INNER_RISK_ERROR,
                    StrUtil.format("当前时间[{}],不允许委托,允许委托时间段为：[{}]",
                            localTime, StrUtil.removeSuffix(builder.toString(), StrUtil.COMMA)));
        }*/
    }

    /**
     * 有效价格申报范围等
     */
    public void checkPrice(BaseOrderRequest order) throws BusinessResultException {
        Instruction instruction = getInstruction(order);
        Instrument instrument = getInstrument(order);

        // 最小价差校验 MINI_PRICE （最小价格变动单位）
        checkPriceStep(order, instruction);

        // 最小交易单元 MIN_STEP （最小申报单位）卖出涉及零股问题
        checkMinStepWithBuy(order, instruction);

        // 最低交易数量 MIN_NUM （最低申报数量）
        // 最低交易数量（市价）MIN_NUM_MARKET （最低申报数量）
        // 最低交易金额（元）MIN_AMT （最低申报金额）
        checkMinNum(order, instruction, instrument);

        // 涨跌停或有效申报价格校验
        String entrustProp = EntrustPropUtils.getEntrustProp(instruction, order.getEntrustProp());
        // 申报类不需要check最小价差和涨跌停
        if (EntrustUtils.isNoTrade(entrustProp)) {
            return;
        }
        checkLimitPrice(order, instruction, instrument);
    }

    /**
     * 持仓检查
     */
    public void checkPosition(BaseOrderRequest order) throws BusinessResultException {
        // TODO: 持仓检查
        /*if (!accountPositionHelper.checkLooseStocksByPosition(orderInterface, entrustVolume, instrument.getBuyUnit())) {
            return false;
        }*/
    }

    private void checkMinStepWithBuy(BaseOrderRequest order, Instruction instruction) {
        String entrustBs = EntrustPropUtils.getEntrustBs(instruction);
        long entrustVolume = order.getEntrustVolume();
        MinTrdUnitLimit minTrdUnitLimit = getMinTrdUnitLimit(order, instruction);
        int minStep = minTrdUnitLimit.getMinStep();

        if (minStep <= 0) {
            addCheckResult("委托数量", entrustVolume,
                    StrUtil.format("获取最小交易单位异常 => 【minStep = {}】", minStep));
            return;
        }

        if (EntrustBs.isBuy(entrustBs)
                && entrustVolume % minStep != 0) {
            addCheckResult("委托数量", entrustVolume,
                    StrUtil.format("委托数量[{}]不符合最小买入单位[{}]整数倍", entrustVolume, minStep));
        }
    }

    @SuppressWarnings("unused")
    private void checkLimitPrice(BaseOrderRequest order, Instruction instruction, Instrument instrument) {
        TickBean tick = getTick(instruction);
        BigDecimal entrustPrice = order.getEntrustPrice();

        BigDecimal lowerLimitPrice = NumberUtil.round(tick.getLowerLimitPrice(), 4, RoundingMode.UP);
        BigDecimal upperLimitPrice = NumberUtil.round(tick.getLowerLimitPrice(), 4, RoundingMode.DOWN);

        if (isNoLimitPrice(tick)) {
            String errorMsg = priceHelper.checkPrice(order, instruction, instrument, tick);
            if (StrUtil.isNotEmpty(errorMsg)) {
                addCheckResult("委托价格", entrustPrice, errorMsg);
            }return;
        }
        if (lowerLimitPrice.compareTo(entrustPrice) > 0
                || upperLimitPrice.compareTo(entrustPrice) < 0) {
            addCheckResult("委托价格", entrustPrice,
                    StrUtil.format("委托价格[{}]不符合涨跌停价限制：跌停价[{}]~涨停价[{}]", entrustPrice, lowerLimitPrice, upperLimitPrice));
        }
    }

    /**
     * 判断是否有涨跌停价
     *
     * @param tick 行情对象
     * @return 是否有涨跌停价
     */
    private boolean isNoLimitPrice(TickBean tick) {
        BigDecimal lowerLimitPrice = NumberUtil.round(tick.getLowerLimitPrice(), 4, RoundingMode.UP);
        BigDecimal upperLimitPrice = NumberUtil.round(tick.getLowerLimitPrice(), 4, RoundingMode.DOWN);

        return lowerLimitPrice.compareTo(BigDecimal.ZERO) == 0
                && upperLimitPrice.compareTo(BigDecimal.ZERO) == 0;
    }

    @SuppressWarnings("unused")
    private void checkMinNum(BaseOrderRequest order, Instruction instruction, Instrument instrument) throws BusinessResultException {
        MinTrdUnitLimit minTrdUnitLimit = getMinTrdUnitLimit(order, instruction);
        String entrustBs = EntrustPropUtils.getEntrustBs(instruction);
        String entrustProp = EntrustPropUtils.getEntrustProp(instruction, order.getEntrustProp());
        long entrustVolume = order.getEntrustVolume();
        BigDecimal entrustBalance = order.getEntrustBalance();

        if (!EntrustBs.isBuy(entrustBs)) {
            return;
        }
        long minNum;
        if (EntrustProp.isMarketPrice(entrustProp)) {
            minNum = minTrdUnitLimit.getMinNumMarket();
        } else {
            minNum = minTrdUnitLimit.getMinNum();
        }

        BigDecimal minAmt = minTrdUnitLimit.getMinAmt();
        if (entrustVolume < minNum && entrustBalance.compareTo(minAmt) < 0) {
            addCheckResult("委托数量", entrustVolume,
                    StrUtil.format("委托数量[{}]不符合最小买入数量限制[{}]", entrustVolume, minNum));
            addCheckResult("委托金额", entrustVolume,
                    StrUtil.format("委托金额[{}]不符合最小买入金额限制[{}]", entrustBalance, minAmt));
        }
    }

    @SuppressWarnings("unused")
    private void checkPriceStep(BaseOrderRequest order, Instruction instruction) throws BusinessResultException {
        BigDecimal entrustPrice = order.getEntrustPrice();
        BigDecimal priceStep = NumberUtil.null2Zero(instruction.queryMinPrice(order.getEntrustProp()));
        if (priceStep.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessResultException(ErrorCode.INNER_RISK_ERROR,
                    StrUtil.format("无法获取最小价格变动单位"));
        }
        if ((entrustPrice.divideAndRemainder(priceStep))[1].compareTo(BigDecimal.ZERO) != 0) {
            addCheckResult("委托价格", entrustPrice,
                    StrUtil.format("委托价格[{}]不符合最小价格变动单位[{}]的整数倍", entrustPrice, priceStep));
        }
    }

    private void addCheckResult(String propertyName, Object value, String message) {
        BeanValidationResult.ErrorMessage errorMessage = new BeanValidationResult.ErrorMessage();
        errorMessage.setPropertyName(propertyName);
        errorMessage.setValue(value);
        errorMessage.setMessage(message);

        checkResult.setSuccess(false);
        checkResult.addErrorMessage(errorMessage);
    }

    private Instruction getInstruction(BaseOrderRequest order) {
        Instruction instruction = instructionService.findById(order.getInstructionId());
        if (ObjectUtil.isNull(instruction)) {
            throw new BusinessResultException(ErrorCode.INSTRUCTIONS_NOT_EXIST);
        }
        return instruction;
    }

    private Instrument getInstrument(BaseOrderRequest order) {
        Instruction instruction = getInstruction(order);
        Instrument instrument = instrumentService.findById(instruction.getFinprodMarketId());
        if (ObjectUtil.isNull(instrument)) {
            throw new BusinessResultException(ErrorCode.DERIVATIVE_INSTRUMENT_ERROR);
        }
        return instrument;
    }

    @SuppressWarnings("unused")
    private TickBean getTick(BaseOrderRequest order) {
        Instruction instruction = getInstruction(order);
        return getTick(instruction);
    }

    private TickBean getTick(Instruction instruction) {
        ResultBean tick = quoteService.getTick(instruction.getFinprodMarketId());
        if (ErrorCode.isFail(tick.getErrorCode())) {
            throw new BusinessResultException(ErrorCode.QUOTE_NOT_EXIT);
        }
        return (TickBean) tick.getData();
    }

    private MinTrdUnitLimit getMinTrdUnitLimit(BaseOrderRequest order, Instruction instruction) {
        MinTrdUnitLimit minTrdUnitLimit = instruction.queryMinTrdUnitLimit(order.getEntrustProp());
        if (ObjectUtil.isNull(minTrdUnitLimit)) {
            throw new BusinessResultException(ErrorCode.INNER_RISK_ERROR,
                    StrUtil.format("无法获取交易所交易属性"));
        }
        return minTrdUnitLimit;
    }

    /**
     * 零股
     */
/*    public void checkxxx(BaseOrderRequest request) {

    }*/
}
