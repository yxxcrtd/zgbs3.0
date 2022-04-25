package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述:BID_ORDER_BID_PRICE表的实体类
 *
 * @author: jp
 * @修改时间: 2022-04-25
 */
public class BidOrderBidPrice extends BidOrderBidPriceKey {
    /**
     * 是否中标
     */
    private String isWin;

    /**
     * 中标标位
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal winPrice;

    /**
     * 中标净价
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal winCprice;

    /**
     * 理论中标券面总额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal theWinPrinAmt;

    /**
     * 实际中标券面总额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal winPrinAmt;

    /**
     * 交易编号
     */
    private String tradeId;

    /**
     * 是否头寸冻结
     */
    private String isFrozenPosition;

    /**
     * 本方
     */
    private String ouPartyId;

    /**
     * 本方交易员
     */
    private String ourTrader;

    /**
     * 认购量
     */
    private BigDecimal subscriptionAmount;

    /**
     * 应缴款金额
     */
    private BigDecimal payableAmount;

    /**
     * 结算服务费
     */
    private BigDecimal settFeeAmt;

    /**
     * 前置投标编
     */
    private String pBidId;

    /**
     * 是否撤销
     */
    private String isCancel;

    /**
     * 交易状态
     */
    private String tradeStatus;

    /**
     * 是否中标
     *
     * @return IS_WIN 是否中标
     */
    public String getIsWin() {
        return isWin;
    }

    /**
     * 是否中标
     *
     * @param isWin 是否中标
     */
    public void setIsWin(String isWin) {
        this.isWin = isWin == null ? null : isWin.trim();
    }

    /**
     * 中标标位
     *
     * @return WIN_PRICE 中标标位
     */
    public BigDecimal getWinPrice() {
        return winPrice;
    }

    /**
     * 中标标位
     *
     * @param winPrice 中标标位
     */
    public void setWinPrice(BigDecimal winPrice) {
        this.winPrice = winPrice;
    }

    /**
     * 中标净价
     *
     * @return WIN_CPRICE 中标净价
     */
    public BigDecimal getWinCprice() {
        return winCprice;
    }

    /**
     * 中标净价
     *
     * @param winCprice 中标净价
     */
    public void setWinCprice(BigDecimal winCprice) {
        this.winCprice = winCprice;
    }

    /**
     * 理论中标券面总额
     *
     * @return THE_WIN_PRIN_AMT 理论中标券面总额
     */
    public BigDecimal getTheWinPrinAmt() {
        return theWinPrinAmt;
    }

    /**
     * 理论中标券面总额
     *
     * @param theWinPrinAmt 理论中标券面总额
     */
    public void setTheWinPrinAmt(BigDecimal theWinPrinAmt) {
        this.theWinPrinAmt = theWinPrinAmt;
    }

    /**
     * 实际中标券面总额
     *
     * @return WIN_PRIN_AMT 实际中标券面总额
     */
    public BigDecimal getWinPrinAmt() {
        return winPrinAmt;
    }

    /**
     * 实际中标券面总额
     *
     * @param winPrinAmt 实际中标券面总额
     */
    public void setWinPrinAmt(BigDecimal winPrinAmt) {
        this.winPrinAmt = winPrinAmt;
    }

    /**
     * 交易编号
     *
     * @return TRADE_ID 交易编号
     */
    public String getTradeId() {
        return tradeId;
    }

    /**
     * 交易编号
     *
     * @param tradeId 交易编号
     */
    public void setTradeId(String tradeId) {
        this.tradeId = tradeId == null ? null : tradeId.trim();
    }
/**add by sunhui 20210719 重庆银行：中标结果维护，对应明细中，增加【是否头寸冻结】字段   start**/
    /**
     * 是否头寸冻结
     *
     * @return IS_FROZEN_POSITION 是否头寸冻结
     */
    public String getIsFrozenPosition() {
        return isFrozenPosition;
    }

    /**
     * 是否头寸冻结
     *
     * @param isFrozenPosition 是否头寸冻结
     */
    public void setIsFrozenPosition(String isFrozenPosition) {
        this.isFrozenPosition = isFrozenPosition == null ? null : isFrozenPosition.trim();
    }
    /**add by sunhui 20210719 重庆银行：中标结果维护，对应明细中，增加【是否头寸冻结】字段   end**/


    /**
     * 本方
     *
     * @return OU_PARTY_ID 本方
     */
    public String getOuPartyId() {
        return ouPartyId;
    }

    /**
     * 本方
     *
     * @param ouPartyId 本方
     */
    public void setOuPartyId(String ouPartyId) {
        this.ouPartyId = ouPartyId == null ? null : ouPartyId.trim();
    }

    /**
     * 本方交易员
     *
     * @return OUR_TRADER 本方交易员
     */
    public String getOurTrader() {
        return ourTrader;
    }

    /**
     * 本方交易员
     *
     * @param ourTrader 本方交易员
     */
    public void setOurTrader(String ourTrader) {
        this.ourTrader = ourTrader == null ? null : ourTrader.trim();
    }

    /**
     * 认购量
     *
     * @return SUBSCRIPTION_AMOUNT 认购量
     */
    public BigDecimal getSubscriptionAmount() {
        return subscriptionAmount;
    }

    /**
     * 认购量
     *
     * @param subscriptionAmount 认购量
     */
    public void setSubscriptionAmount(BigDecimal subscriptionAmount) {
        this.subscriptionAmount = subscriptionAmount;
    }

    /**
     * 应缴款金额
     *
     * @return PAYABLE_AMOUNT 应缴款金额
     */
    public BigDecimal getPayableAmount() {
        return payableAmount;
    }

    /**
     * 应缴款金额
     *
     * @param payableAmount 应缴款金额
     */
    public void setPayableAmount(BigDecimal payableAmount) {
        this.payableAmount = payableAmount;
    }

    /**
     * 结算服务费
     *
     * @return SETT_FEE_AMT 结算服务费
     */
    public BigDecimal getSettFeeAmt() {
        return settFeeAmt;
    }

    /**
     * 认购量
     *
     * @param settFeeAmt 结算服务费
     */
    public void setSettFeeAmt(BigDecimal settFeeAmt) {
        this.settFeeAmt = settFeeAmt;
    }

    /**
     * 前置投标编
     *
     * @return P_BID_ID 前置投标编
     */
    public String getpBidId() {
        return pBidId;
    }

    /**
     * 前置投标编
     *
     * @param pBidId 前置投标编
     */
    public void setpBidId(String pBidId) {
        this.pBidId = pBidId == null ? null : pBidId.trim();
    }
    /**
     * 是否撤销
     *
     * @return IS_CANCEL 是否撤销
     */
    public String getIsCancel() {
        return isCancel;
    }

    /**
     * 是否撤销
     *
     * @param isCancel 是否撤销
     */
    public void setIsCancel(String isCancel) {
        this.isCancel = isCancel == null ? null : isCancel.trim();
    }

    /**
     * 交易状态
     *
     * @return TRADE_STATUS 交易状态
     */
    public String getTradeStatus() {
        return tradeStatus;
    }

    /**
     * 交易状态
     *
     * @param tradeStatus 交易状态
     */
    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus == null ? null : tradeStatus.trim();
    }

    //中标结果维护中缴款日
    private Date payDate;

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }
}