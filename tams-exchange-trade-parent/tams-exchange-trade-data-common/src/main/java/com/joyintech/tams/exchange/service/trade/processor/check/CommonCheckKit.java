package com.joyintech.tams.exchange.service.trade.processor.check;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.validation.BeanValidationResult;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.BusinessResultException;
import com.joyintech.tams.exchange.common.bean.MinTrdUnitLimit;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.bean.dto.OpenCloseTime;
import com.joyintech.tams.exchange.common.bean.request.BaseOrderRequest;
import com.joyintech.tams.exchange.common.cache.TradeTimeService;
import com.joyintech.tams.exchange.common.constant.EntrustBs;
import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.enums.SwitcheEnum;
import com.joyintech.tams.exchange.common.utils.EntrustPropUtils;
import com.joyintech.tams.exchange.common.utils.EntrustUtils;
import com.joyintech.tams.exchange.common.utils.EnumUtil;
import com.joyintech.tams.exchange.service.trade.processor.account.AccountPositionHelper;
import com.joyintech.tams.exchange.service.trade.processor.business.price.PriceHelper;
import com.joyintech.tams.exchange.service.trade.server.agent.AccountManager;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgent;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgentManager;
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
    private TradeTimeService tradeTimeService;
    private PriceHelper priceHelper;
    private AccountPositionHelper accountPositionHelper;
    private AccountManager accountManager;
    private TradeAgentManager tradeAgentManager;

    @Autowired
    public void setTradeTimeService(TradeTimeService tradeTimeService) {
        this.tradeTimeService = tradeTimeService;
    }

    @Autowired
    public void setPriceHelper(PriceHelper priceHelper) {
        this.priceHelper = priceHelper;
    }

    @Autowired
    public void setAccountPositionHelper(AccountPositionHelper accountPositionHelper) {
        this.accountPositionHelper = accountPositionHelper;
    }

    @Autowired
    public void setAccountManager(AccountManager accountManager) {
        this.accountManager = accountManager;
    }

    @Autowired
    public void setTradeAgentManager(TradeAgentManager tradeAgentManager) {
        this.tradeAgentManager = tradeAgentManager;
    }

    @Override
    public ResultBean check(BaseOrderRequest order, Instruction instruction, Instrument instrument, TickBean tickBean) throws BusinessResultException {

        checkAccountAndAgent(instruction);
        checkTime(order, instruction, instrument);
        checkPrice(order, instruction, instrument, tickBean);
        checkPosition(order, instruction);

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
            throw new BusinessResultException(ErrorCode.CHECK_ERROR,
                    builder.toString());
        } else if (TRD_PRICE_SCOPE_CONTROL_PROMPT.equals(trdPriceScopeControl) || trdPriceScopeControl == null) {
            if (!SwitcheEnum.OPEN.codeEquals(order.getOrderSoftControl())) {
                return ResultBean.error(ErrorCode.TRIGGER_SOFT_CONTROL, builder.toString());
            }
        } else if (TRD_PRICE_SCOPE_CONTROL_ALLOW.equals(trdPriceScopeControl)) {
        }
        return ResultBean.success();
    }

    /**
     * 检查账号及终端登录情况
     */
    private void checkAccountAndAgent(Instruction instruction) throws BusinessResultException {
        String acctNo = instruction.getAcctNo();

        Account account = accountManager.getAccountById(acctNo);
        if (ObjectUtil.isNull(account)) {
            throw new BusinessResultException(ErrorCode.ACCOUNT_NOT_LOGIN);
        }

        TradeAgent agent = tradeAgentManager.findAgentByAccountId(acctNo);
        if (ObjectUtil.isNull(agent)) {
            throw new BusinessResultException(ErrorCode.TERMINAL_NOT_ONLINE);
        }
    }

    /**
     * 检查是否在交易时间段内
     * - 开启校验开关，交易时间段存在且当前时间不在交易时间段内，返回 true
     * - 其他，返回false
     */
    public void checkTime(BaseOrderRequest order, Instruction instruction, Instrument instrument) throws BusinessResultException {

        String trdTradeTimeSwitch = CacheUtil.getSysParam(TRD_TRADE_TIME_SWITCH);
        if (SwitcheEnum.OPEN.codeNotEquals(trdTradeTimeSwitch)) {
            log.debug("系统参数[{}:{}]，跳过交易时间检查", TRD_TRADE_TIME_SWITCH, trdTradeTimeSwitch);
            return;
        }

        List<OpenCloseTime> tradePeriod = tradeTimeService.getTradePeriod(instruction, order, instrument);
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
            throw new BusinessResultException(ErrorCode.CHECK_ERROR,
                    StrUtil.format("当前时间[{}]，不允许委托，允许委托时间段为：[{}]",
                            localTime, StrUtil.removeSuffix(builder.toString(), StrUtil.COMMA)));
        }
    }

    /**
     * 有效价格申报范围等
     */
    public void checkPrice(BaseOrderRequest order, Instruction instruction, Instrument instrument, TickBean tickBean) throws BusinessResultException {

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
        checkLimitPrice(order, instruction, instrument, tickBean);
    }

    /**
     * 持仓校验
     */
    public void checkPosition(BaseOrderRequest order, Instruction instruction) throws BusinessResultException {
        OrderInterface orderInterface = new Order();
        orderInterface.setEntrustBs(EntrustPropUtils.getEntrustBs(instruction));
        orderInterface.setAssetType(instruction.getAssetType());
        orderInterface.setAccountId(instruction.getAcctNo());
        orderInterface.setStockCode(instruction.getFinprodMarketId());
        long entrustVolume = order.getEntrustVolume();
        MinTrdUnitLimit minTrdUnitLimit = getMinTrdUnitLimit(order, instruction);
        Integer minStep = minTrdUnitLimit.getMinStep();
        accountPositionHelper.checkLooseStocksByPosition(orderInterface, entrustVolume, minStep);
        if (StrUtil.isNotEmpty(orderInterface.getErrorMsg())) {
            String errorMsg = StrUtil.format("持仓检查未通过[{}]", orderInterface.getErrorMsg());
            log.error(errorMsg);
            throw new BusinessResultException(ErrorCode.CHECK_ERROR, errorMsg);
        }
    }

    private void checkMinStepWithBuy(BaseOrderRequest order, Instruction instruction) {
        String entrustBs = EntrustPropUtils.getEntrustBs(instruction);
        long entrustVolume = order.getEntrustVolume();
        MinTrdUnitLimit minTrdUnitLimit = getMinTrdUnitLimit(order, instruction);
        int minStep = minTrdUnitLimit.getMinStep();

        if (minStep <= 0) {
            String errorMsg = StrUtil.format("获取的最小交易单位[{}]异常", minStep);
            log.error(errorMsg);
            throw new BusinessResultException(ErrorCode.CHECK_ERROR, errorMsg);
        }

        if (InvestAssetTypeEnum.isPledgeRepurchase(minTrdUnitLimit.getAssetType())
                && StringUtil.equals(minTrdUnitLimit.getMinStepUnit(), "05")) {
            minStep = minStep / 100;
        }

        if (EntrustBs.isBuy(entrustBs)
                && entrustVolume % minStep != 0) {
            String errorMsg = StrUtil.format("委托数量[{}]不符合最小买入单位[{}]整数倍", entrustVolume, minStep);
            log.error(errorMsg);
            throw new BusinessResultException(ErrorCode.CHECK_ERROR, errorMsg);
        }
    }

    @SuppressWarnings("unused")
    private void checkLimitPrice(BaseOrderRequest order, Instruction instruction, Instrument instrument, TickBean tick) {
        BigDecimal entrustPrice = order.getEntrustPrice();

        BigDecimal lowerLimitPrice = NumberUtil.round(tick.getLowerLimitPrice(), 4, RoundingMode.UP);
        BigDecimal upperLimitPrice = NumberUtil.round(tick.getUpperLimitPrice(), 4, RoundingMode.DOWN);

        if (isNoLimitPrice(tick) || EnumUtil.in(instruction.getAssetType(), InvestAssetTypeEnum.BOND, InvestAssetTypeEnum.PLEDGE_REPURCHASE)) {
            String errorMsg = priceHelper.checkPrice(order, instruction, instrument, tick);
            if (StrUtil.isNotEmpty(errorMsg)) {
                addCheckResult("委托价格", entrustPrice, errorMsg);
            }
            return;
        }
        if (lowerLimitPrice.compareTo(entrustPrice) > 0
                || upperLimitPrice.compareTo(entrustPrice) < 0) {
            String errorMsg = StrUtil.format("委托价格[{}]不符合涨跌停价限制：跌停价[{}]~涨停价[{}]",
                    entrustPrice, lowerLimitPrice, upperLimitPrice);
            log.error(errorMsg);
            throw new BusinessResultException(ErrorCode.CHECK_ERROR, errorMsg);
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
        BigDecimal entrustBalance = NumberUtil.mul(entrustVolume, order.getEntrustPrice());

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
            String errorMsg = StrUtil.format("委托数量[{}]不符合最小买入数量限制[{}],委托金额[{}]不符合最小买入金额限制[{}]",
                    entrustVolume, minNum, entrustBalance, minAmt);
            log.error(errorMsg);
            throw new BusinessResultException(ErrorCode.CHECK_ERROR, errorMsg);
        }
    }

    @SuppressWarnings("unused")
    private void checkPriceStep(BaseOrderRequest order, Instruction instruction) throws BusinessResultException {
        BigDecimal entrustPrice = order.getEntrustPrice();
        String entrustProp = EntrustPropUtils.getEntrustPropWithHandleNewBond(instruction, order.getEntrustProp());
        BigDecimal priceStep = NumberUtil.null2Zero(instruction.queryMinPrice(entrustProp));
        if (priceStep.compareTo(BigDecimal.ZERO) <= 0) {
            String errorMsg = StrUtil.format("无法获取最小价格变动单位");
            log.error(errorMsg);
            throw new BusinessResultException(ErrorCode.CHECK_ERROR, errorMsg);
        }
        if ((entrustPrice.divideAndRemainder(priceStep))[1].compareTo(BigDecimal.ZERO) != 0) {
            String errorMsg = StrUtil.format("委托价格[{}]不符合最小价格变动单位[{}]的整数倍", entrustPrice, priceStep);
            log.error(errorMsg);
            throw new BusinessResultException(ErrorCode.CHECK_ERROR, errorMsg);
        }
    }

    private MinTrdUnitLimit getMinTrdUnitLimit(BaseOrderRequest order, Instruction instruction) {
        String entrustProp = EntrustPropUtils.getEntrustPropWithHandleNewBond(instruction, order.getEntrustProp());
        MinTrdUnitLimit minTrdUnitLimit = instruction.queryMinTrdUnitLimit(entrustProp);
        if (ObjectUtil.isNull(minTrdUnitLimit)) {
            String errorMsg = StrUtil.format("无法获取交易所交易属性");
            log.error(errorMsg);
            throw new BusinessResultException(ErrorCode.CHECK_ERROR, errorMsg);
        }
        return minTrdUnitLimit;
    }

    /**
     * 零股校验
     */
    public void checkFractional(BaseOrderRequest request) {

    }
}
