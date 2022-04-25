package com.joyintech.tams.common.base.entity;

import com.joyintech.framework.base.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述:WFL_BID_ORDER表的实体类
 *
 * @author: jp
 * @创建时间: 2022-04-25
 */
public class WflBidOrder extends BaseEntity {
    /**
     * 投标编号
     */
    private String bidId;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 投标日期
     */
    private Date bidDate;

    /**
     * 缴款方式，一级分销、一级缴款、二级交易
     */
    private String payType;

    /**
     * 缴款日
     */
    private Date payDate;

    /**
     * 是否中标
     */
    private String isWin;

    /**
     * 中标券面总额
     */
    private BigDecimal winPrinAmt;

    /**
     * 中标标位
     */
    private BigDecimal winPrice;

    /**
     * 中标方式，单标位、多标位
     */
    private String winType;

    /**
     * 备注
     */
    private String remark;


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
     * 流程id
     */
    private String workflowId;

    /**
     * 数据状态
     */
    private String workflowStatus;

    /**
     * 投标编号
     *
     * @return BID_ID 投标编号
     */
    public String getBidId() {
        return bidId;
    }

    /**
     * 投标编号
     *
     * @param bidId 投标编号
     */
    public void setBidId(String bidId) {
        this.bidId = bidId == null ? null : bidId.trim();
    }

    /**
     * 金融产品代码
     *
     * @return FINPROD_ID 金融产品代码
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 金融产品代码
     *
     * @param finprodId 金融产品代码
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId == null ? null : finprodId.trim();
    }

    /**
     * 投标日期
     *
     * @return BID_DATE 投标日期
     */
    public Date getBidDate() {
        return bidDate;
    }

    /**
     * 投标日期
     *
     * @param bidDate 投标日期
     */
    public void setBidDate(Date bidDate) {
        this.bidDate = bidDate;
    }

    /**
     * 缴款方式，一级分销、一级缴款、二级交易
     *
     * @return PAY_TYPE 缴款方式，一级分销、一级缴款、二级交易
     */
    public String getPayType() {
        return payType;
    }

    /**
     * 缴款方式，一级分销、一级缴款、二级交易
     *
     * @param payType 缴款方式，一级分销、一级缴款、二级交易
     */
    public void setPayType(String payType) {
        this.payType = payType == null ? null : payType.trim();
    }

    /**
     * 缴款日
     *
     * @return PAY_DATE 缴款日
     */
    public Date getPayDate() {
        return payDate;
    }

    /**
     * 缴款日
     *
     * @param payDate 缴款日
     */
    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

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
     * 中标券面总额
     *
     * @return WIN_PRIN_AMT 中标券面总额
     */
    public BigDecimal getWinPrinAmt() {
        return winPrinAmt;
    }

    /**
     * 中标券面总额
     *
     * @param winPrinAmt 中标券面总额
     */
    public void setWinPrinAmt(BigDecimal winPrinAmt) {
        this.winPrinAmt = winPrinAmt;
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
     * 中标方式，单标位、多标位
     *
     * @return WIN_TYPE 中标方式，单标位、多标位
     */
    public String getWinType() {
        return winType;
    }

    /**
     * 中标方式，单标位、多标位
     *
     * @param winType 中标方式，单标位、多标位
     */
    public void setWinType(String winType) {
        this.winType = winType == null ? null : winType.trim();
    }

    /**
     * 备注
     *
     * @return REMARK 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }


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

    /**
     * 流程id
     *
     * @return WORKFLOW_ID 流程id
     */
    public String getWorkflowId() {
        return workflowId;
    }

    /**
     * 流程id
     *
     * @param workflowId 流程id
     */
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId == null ? null : workflowId.trim();
    }

    /**
     * 数据状态
     *
     * @return WORKFLOW_STATUS 数据状态
     */
    public String getWorkflowStatus() {
        return workflowStatus;
    }

    /**
     * 数据状态
     *
     * @param workflowStatus 数据状态
     */
    public void setWorkflowStatus(String workflowStatus) {
        this.workflowStatus = workflowStatus == null ? null : workflowStatus.trim();
    }
}