package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;

import java.math.BigDecimal;

public class WflBidResultDetail extends BaseEntity {


    private static final long serialVersionUID = -111997507690103328L;

    /**
     * 中标明细编号
     */
    private String detailWinId;

    /**
     * 汇总中标编号
     */
    private String totalWinId;

    /**
     * 明细中标编号
     */
    private String winId;

    /**
     * 投标编号
     */
    private String bidId;

    /**
     * 指令代码
     */
    private String entrustId;

    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 汇总投标券面总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal totalBidPrinamt;

    /**
     * 理论中标券面总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal theWinPrinamt;

    /**
     * 实际中标券面总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal actWinPrinamt;

    /**
     * 投标标位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal bidPrice;

    /**
     * 中标标位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal winPrice;

    /**
     * 中标净价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal winCprice;

    /**
     * 应计利息
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal intAmt;

    /**
     * 缴款金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal payAmt;

    /**
     * 结算服务费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal settFeeAmt;

    /**
     * 业务模式
     */
    private String invAim;

    /**
     * 证券账户
     */
    private String secAcctId;

    /**
     * 流程id
     */
    private String workflowId;

    /**
     * 数据状态
     */
    private String workflowStatus;


    /**
     * 获取 汇总中标编号
     *
     * @return totalWinId 汇总中标编号
     */
    public String getTotalWinId() {
        return this.totalWinId;
    }

    /**
     * 设置 汇总中标编号
     *
     * @param totalWinId 汇总中标编号
     */
    public void setTotalWinId(String totalWinId) {
        this.totalWinId = totalWinId;
    }

    /**
     * 获取 明细中标编号
     *
     * @return winId 明细中标编号
     */
    public String getWinId() {
        return this.winId;
    }

    /**
     * 设置 明细中标编号
     *
     * @param winId 明细中标编号
     */
    public void setWinId(String winId) {
        this.winId = winId;
    }

    /**
     * 获取 投标编号
     *
     * @return bidId 投标编号
     */
    public String getBidId() {
        return this.bidId;
    }

    /**
     * 设置 投标编号
     *
     * @param bidId 投标编号
     */
    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    /**
     * 获取 指令代码
     *
     * @return entrustId 指令代码
     */
    public String getEntrustId() {
        return this.entrustId;
    }

    /**
     * 设置 指令代码
     *
     * @param entrustId 指令代码
     */
    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    /**
     * 获取 组合代码
     *
     * @return portfolioId 组合代码
     */
    public String getPortfolioId() {
        return this.portfolioId;
    }

    /**
     * 设置 组合代码
     *
     * @param portfolioId 组合代码
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    /**
     * 获取 汇总投标券面总额
     *
     * @return totalBidPrinamt 汇总投标券面总额
     */
    public BigDecimal getTotalBidPrinamt() {
        return this.totalBidPrinamt;
    }

    /**
     * 设置 汇总投标券面总额
     *
     * @param totalBidPrinamt 汇总投标券面总额
     */
    public void setTotalBidPrinamt(BigDecimal totalBidPrinamt) {
        this.totalBidPrinamt = totalBidPrinamt;
    }

    /**
     * 获取 理论中标券面总额
     *
     * @return theWinPrinamt 理论中标券面总额
     */
    public BigDecimal getTheWinPrinamt() {
        return this.theWinPrinamt;
    }

    /**
     * 设置 理论中标券面总额
     *
     * @param theWinPrinamt 理论中标券面总额
     */
    public void setTheWinPrinamt(BigDecimal theWinPrinamt) {
        this.theWinPrinamt = theWinPrinamt;
    }

    /**
     * 获取 实际中标券面总额
     *
     * @return actWinPrinamt 实际中标券面总额
     */
    public BigDecimal getActWinPrinamt() {
        return this.actWinPrinamt;
    }

    /**
     * 设置 实际中标券面总额
     *
     * @param actWinPrinamt 实际中标券面总额
     */
    public void setActWinPrinamt(BigDecimal actWinPrinamt) {
        this.actWinPrinamt = actWinPrinamt;
    }

    /**
     * 获取 投标标位
     *
     * @return bidPrice 投标标位
     */
    public BigDecimal getBidPrice() {
        return this.bidPrice;
    }

    /**
     * 设置 投标标位
     *
     * @param bidPrice 投标标位
     */
    public void setBidPrice(BigDecimal bidPrice) {
        this.bidPrice = bidPrice;
    }

    /**
     * 获取 中标标位
     *
     * @return winPrice 中标标位
     */
    public BigDecimal getWinPrice() {
        return this.winPrice;
    }

    /**
     * 设置 中标标位
     *
     * @param winPrice 中标标位
     */
    public void setWinPrice(BigDecimal winPrice) {
        this.winPrice = winPrice;
    }

    /**
     * 获取 中标净价
     *
     * @return winCprice 中标净价
     */
    public BigDecimal getWinCprice() {
        return this.winCprice;
    }

    /**
     * 设置 中标净价
     *
     * @param winCprice 中标净价
     */
    public void setWinCprice(BigDecimal winCprice) {
        this.winCprice = winCprice;
    }

    /**
     * 获取 应计利息
     *
     * @return intAmt 应计利息
     */
    public BigDecimal getIntAmt() {
        return this.intAmt;
    }

    /**
     * 设置 应计利息
     *
     * @param intAmt 应计利息
     */
    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    /**
     * 获取 缴款金额
     *
     * @return payAmt 缴款金额
     */
    public BigDecimal getPayAmt() {
        return this.payAmt;
    }

    /**
     * 设置 缴款金额
     *
     * @param payAmt 缴款金额
     */
    public void setPayAmt(BigDecimal payAmt) {
        this.payAmt = payAmt;
    }

    /**
     * 获取 结算服务费
     *
     * @return settFeeAmt 结算服务费
     */
    public BigDecimal getSettFeeAmt() {
        return this.settFeeAmt;
    }

    /**
     * 设置 结算服务费
     *
     * @param settFeeAmt 结算服务费
     */
    public void setSettFeeAmt(BigDecimal settFeeAmt) {
        this.settFeeAmt = settFeeAmt;
    }

    /**
     * 获取 业务模式
     *
     * @return invAim 业务模式
     */
    public String getInvAim() {
        return this.invAim;
    }

    /**
     * 设置 业务模式
     *
     * @param invAim 业务模式
     */
    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    /**
     * 获取 证券账户
     *
     * @return secAcctId 证券账户
     */
    public String getSecAcctId() {
        return this.secAcctId;
    }

    /**
     * 设置 证券账户
     *
     * @param secAcctId 证券账户
     */
    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    /**
     * 获取 流程id
     *
     * @return workflowId 流程id
     */
    public String getWorkflowId() {
        return this.workflowId;
    }

    /**
     * 设置 流程id
     *
     * @param workflowId 流程id
     */
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    /**
     * 获取 数据状态
     *
     * @return workflowStatus 数据状态
     */
    public String getWorkflowStatus() {
        return this.workflowStatus;
    }

    /**
     * 设置 数据状态
     *
     * @param workflowStatus 数据状态
     */
    public void setWorkflowStatus(String workflowStatus) {
        this.workflowStatus = workflowStatus;
    }


    /**
     * 获取 中标明细编号
     *
     * @return detailWinId 中标明细编号
     */
    public String getDetailWinId() {
        return this.detailWinId;
    }

    /**
     * 设置 中标明细编号
     *
     * @param detailWinId 中标明细编号
     */
    public void setDetailWinId(String detailWinId) {
        this.detailWinId = detailWinId;
    }
}