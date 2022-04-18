package com.joyintech.tams.exchange.service.trade.processor.check;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.validation.BeanValidationResult;
import com.joyintech.exchange.service.quote.service.QuoteService;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.exchange.common.BusinessResultException;
import com.joyintech.tams.exchange.common.bean.MinTrdUnitLimit;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.bean.dto.OpenCloseTime;
import com.joyintech.tams.exchange.common.bean.instrument.MinPriceAndMinTrdUnitLimitBean;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    public static final String TRD_TRADE_TIME_SWITCH = "trd_trade_time_switch";
    /**
     * 日志对象
     */
    private static final Logger log = LoggerFactory.getLogger(CommonCheckKit.class);
    private final InstructionService instructionService;
    private final InstrumentService instrumentService;
    private final TradeTimeService tradeTimeService;
    private final QuoteService quoteService;

    public CommonCheckKit(InstructionService instructionService, InstrumentService instrumentService, TradeTimeService tradeTimeService, QuoteService quoteService) {
        this.instructionService = instructionService;
        this.instrumentService = instrumentService;
        this.tradeTimeService = tradeTimeService;
        this.quoteService = quoteService;
    }

    public BeanValidationResult check(final BaseOrderRequest order) throws BusinessResultException {
        checkTime(order);
        checkPrice(order);
        return getCheckResult();
    }

    private Instruction getInstruction(BaseOrderRequest order) {
        Instruction instruction = instructionService.findById(order.getInstructionId());
        if (ObjectUtil.isNull(instruction)) {
            throw new BusinessResultException(ErrorCode.INSTRUCTIONS_NOT_EXIST);
        }
        return instruction;
    }

    private Instrument getInstrument(BaseOrderRequest order) {
        Instrument instrument = instrumentService.findById(order.getInstructionId());
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

    /**
     * 检查是否在交易时间段内
     * - 开启校验开关，交易时间段存在且当前时间不在交易时间段内，返回 true
     * - 其他，返回false
     */
    public void checkTime(BaseOrderRequest order) {

        Instruction instruction = getInstruction(order);

        String trdTradeTimeSwitch = CacheUtil.getSysParam(TRD_TRADE_TIME_SWITCH);
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

        if (!result) {
            StringBuilder builder = StrUtil.builder();
            tradePeriod.forEach(item -> builder.append(item.getOpenTime()).append("~").append(item.getCloseTime()).append(StrUtil.COMMA));
            throw new BusinessResultException(ErrorCode.INNER_RISK_ERROR,
                    StrUtil.format("当前时间[{}],不允许委托,允许委托时间段为：[{}]",
                            localTime, StrUtil.removeSuffix(builder.toString(), StrUtil.COMMA)));
        }
    }

    /**
     * 有效价格申报范围
     */
    public void checkPrice(BaseOrderRequest order) {
        Instruction instruction = getInstruction(order);
        Instrument instrument = getInstrument(order);

        checkBuyUnit(order, instruction, instrument);
        checkPriceStep(order, instruction, instrument);

        String entrustProp = EntrustPropUtils.getEntrustProp(instruction, order.getEntrustProp());
        // 申报类不需要check最小价差和涨跌停
        if (EntrustUtils.isNoTrade(entrustProp)) {
            return;
        }
        checkLimitPrice(order, instruction, instrument);
        checkMinNum(order, instruction, instrument);

    }

    private void checkBuyUnit(BaseOrderRequest order, Instruction instruction, Instrument instrument) {
        String entrustBs = EntrustPropUtils.getEntrustBs(instruction);
        long entrustVolume = order.getEntrustVolume();
        int buyUnit = instrument.getBuyUnit();

        if (buyUnit <= 0) {
            addCheckResult("委托数量", entrustVolume,
                    StrUtil.format("合约[{}]，获取最小买入单位异常", instrument));
            return;
        }

        if (EntrustBs.isBuy(entrustBs)
                && entrustVolume % buyUnit != 0) {
            addCheckResult("委托数量", entrustVolume,
                    StrUtil.format("委托数量[{}]不符合最小买入单位[{}]整数倍", entrustVolume, buyUnit));
        }
    }

    @SuppressWarnings("unused")
    private void checkLimitPrice(BaseOrderRequest order, Instruction instruction, Instrument instrument) {
        TickBean tick = getTick(instruction);
        long entrustVolume = order.getEntrustVolume();
        BigDecimal entrustPrice = order.getEntrustPrice();

        BigDecimal lowerLimitPrice = NumberUtil.round(tick.getLowerLimitPrice(), 4, RoundingMode.UP);
        BigDecimal upperLimitPrice = NumberUtil.round(tick.getLowerLimitPrice(), 4, RoundingMode.DOWN);

        if (lowerLimitPrice.compareTo(BigDecimal.ZERO) == 0
            /*&& upperLimitPrice.compareTo(BigDecimal.ZERO) == 0*/) {
//            priceHelper.checkPrice(entrustPrice, orderInterface, instrument, tick, tradePlat, entrustBussType, priceMode);
            return;
        }
        if (lowerLimitPrice.compareTo(entrustPrice) > 0
                || upperLimitPrice.compareTo(entrustPrice) < 0) {
            addCheckResult("委托价格", entrustVolume,
                    StrUtil.format("委托价格[{}]不符合涨跌停价限制：跌停价[{}]~涨停价[{}]", entrustPrice, lowerLimitPrice, upperLimitPrice));
        }
    }

    @SuppressWarnings("unused")
    private void checkMinNum(BaseOrderRequest request, Instruction instruction, Instrument instrument) {
        MinPriceAndMinTrdUnitLimitBean minPriceAndMinTrdUnitLimitBean = instruction.getMinPriceAndMinTrdUnitLimitBean();
        if (ObjectUtil.isNull(minPriceAndMinTrdUnitLimitBean)
                || ErrorCode.isFail(minPriceAndMinTrdUnitLimitBean.getErrorCode())
                || CollUtil.isEmpty(minPriceAndMinTrdUnitLimitBean.getMinTrdUnitLimitList())) {
            // TODO 错误信息
//             addCheckResult("委托价格", entrustVolume,
//                    StrUtil.format("委托价格[{}]不符合涨跌停价限制：跌停价[{}]~涨停价[{}]", entrustPrice, lowerLimitPrice, upperLimitPrice));
        }
        MinTrdUnitLimit minTrdUnitLimit = minPriceAndMinTrdUnitLimitBean.getMinTrdUnitLimitList().get(0);

        String entrustBs = EntrustPropUtils.getEntrustBs(instruction);
        String entrustProp = EntrustPropUtils.getEntrustProp(instruction, request.getEntrustProp());
        long entrustVolume = request.getEntrustVolume();
        BigDecimal entrustBalance = request.getEntrustBalance();
        if (EntrustBs.isBuy(entrustBs)) {
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
    }

    @SuppressWarnings("unused")
    private void checkPriceStep(BaseOrderRequest request, Instruction instruction, Instrument instrument) {
        BigDecimal entrustPrice = request.getEntrustPrice();
        BigDecimal priceStep = NumberUtil.null2Zero(instrument.getPriceStep());
        if (priceStep.compareTo(BigDecimal.ZERO) <= 0) {
            addCheckResult("委托价格", entrustPrice,
                    StrUtil.format("合约[{}]，获取最小价差异常", instrument));
            return;
        }
        if ((entrustPrice.divideAndRemainder(priceStep))[1].compareTo(BigDecimal.ZERO) != 0) {
            addCheckResult("委托价格", entrustPrice,
                    StrUtil.format("委托价格[{}]不符合最小价差[{}]整数倍", entrustPrice, priceStep));
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

    /**
     * 零股
     */
/*    public void checkxxx(BaseOrderRequest request) {

    }*/
}
