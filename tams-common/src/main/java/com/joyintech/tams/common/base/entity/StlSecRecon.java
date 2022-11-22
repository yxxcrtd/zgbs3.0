package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;

import java.math.BigDecimal;
import java.util.Date;

public class StlSecRecon extends BaseEntity {
    /**
     * 券商对账单流水号
     */
    private String seqNo;

    /**
     * 券商代码
     */
    private String customerId;

    //券商名称
    private String customerName;

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    /**
     * 组合代码
     */
    private String portfolioId;

    private String portfolioName;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 市场代码
     */
    private String marketId;

    /**
     * 清算日期
     */
    private Date settleDate;

    /**
     * 交易场所
     */
    private String tradeMarket;

    /**
     * 资金账户
     */
    private String account;

    /**
     * 对账结果
     */
    private String checkResult;

    /**
     * 对账维度
     */
    private String stlSecDeme;

    /**
     * 清算原佣金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal commissionAmt;

    /**
     * 清算原经手费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal jsfAmt;

    /**
     * 清算原证管费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal zgfAmt;

    /**
     * 清算原印花税
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal stampFee;

    /**
     * 清算原结算费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal settleFee;

    /**
     * 清算原过户费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal ghfAmt;

    /**
     * 清算原手续费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal sxfAmt;

    /**
     * 清算原全价总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal fpriceAmt;

    /**
     * 清算原成交数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tradeNum;

    /**
     * 对账信息
     */
    private byte[] reconInfo;

    /**
     * 委托编号/成交编号
     */
    private String entrustTradeId;

    /**
     * 资金账号（截取:后的部分）
     */
    private String accout;

    /**
     * 资金账号
     */
    private String accouts;

    public String getAccout() {
        return accout;
    }

    public void setAccout(String accout) {
        this.accout = accout;
    }

    public String getAccouts() {
        return accouts;
    }

    public void setAccouts(String accouts) {
        this.accouts = accouts;
    }

    /**
     * 取得 券商对账单流水号
     * @return 券商对账单流水号
     */
    public String getSeqNo() {
        return seqNo;
    }

    /**
     * 设置 券商对账单流水号
     * @param seqNo 券商对账单流水号
     */
    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo == null ? null : seqNo.trim();
    }

    /**
     * 取得 券商代码
     * @return 券商代码
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * 设置 券商代码
     * @param customerId 券商代码
     */
    public void setCustomerId(String customerId) {
        this.customerId = customerId == null ? null : customerId.trim();
    }

    /**
     * 取得 组合代码
     * @return 组合代码
     */
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * 设置 组合代码
     * @param portfolioId 组合代码
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId == null ? null : portfolioId.trim();
    }

    /**
     * 取得 金融产品代码
     * @return 金融产品代码
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 设置 金融产品代码
     * @param finprodId 金融产品代码
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId == null ? null : finprodId.trim();
    }

    /**
     * 取得 市场代码
     * @return 市场代码
     */
    public String getMarketId() {
        return marketId;
    }

    /**
     * 设置 市场代码
     * @param marketId 市场代码
     */
    public void setMarketId(String marketId) {
        this.marketId = marketId == null ? null : marketId.trim();
    }

    /**
     * 取得 清算日期
     * @return 清算日期
     */
    public Date getSettleDate() {
        return settleDate;
    }

    /**
     * 设置 清算日期
     * @param settleDate 清算日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    /**
     * 取得 交易场所
     * @return 交易场所
     */
    public String getTradeMarket() {
        return tradeMarket;
    }

    /**
     * 设置 交易场所
     * @param tradeMarket 交易场所
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket == null ? null : tradeMarket.trim();
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account == null ? null : account.trim();
    }

    /**
     * 取得 对账结果
     * @return 对账结果
     */
    public String getCheckResult() {
        return checkResult;
    }

    /**
     * 设置 对账结果
     * @param checkResult 对账结果
     */
    public void setCheckResult(String checkResult) {
        this.checkResult = checkResult == null ? null : checkResult.trim();
    }

    /**
     * 取得 对账维度
     * @return 对账维度
     */
    public String getStlSecDeme() {
        return stlSecDeme;
    }

    /**
     * 设置 对账维度
     * @param stlSecDeme 对账维度
     */
    public void setStlSecDeme(String stlSecDeme) {
        this.stlSecDeme = stlSecDeme == null ? null : stlSecDeme.trim();
    }

    /**
     * 取得 清算原佣金
     * @return 清算原佣金
     */
    public BigDecimal getCommissionAmt() {
        return commissionAmt;
    }

    /**
     * 设置 清算原佣金
     * @param commissionAmt 清算原佣金
     */
    public void setCommissionAmt(BigDecimal commissionAmt) {
        this.commissionAmt = commissionAmt;
    }

    /**
     * 取得 清算原经手费
     * @return 清算原经手费
     */
    public BigDecimal getJsfAmt() {
        return jsfAmt;
    }

    /**
     * 设置 清算原经手费
     * @param jsfAmt 清算原经手费
     */
    public void setJsfAmt(BigDecimal jsfAmt) {
        this.jsfAmt = jsfAmt;
    }

    /**
     * 取得 清算原证管费
     * @return 清算原证管费
     */
    public BigDecimal getZgfAmt() {
        return zgfAmt;
    }

    /**
     * 设置 清算原证管费
     * @param zgfAmt 清算原证管费
     */
    public void setZgfAmt(BigDecimal zgfAmt) {
        this.zgfAmt = zgfAmt;
    }

    /**
     * 取得 清算原印花税
     * @return 清算原印花税
     */
    public BigDecimal getStampFee() {
        return stampFee;
    }

    /**
     * 设置 清算原印花税
     * @param stampFee 清算原印花税
     */
    public void setStampFee(BigDecimal stampFee) {
        this.stampFee = stampFee;
    }

    /**
     * 取得 清算原结算费
     * @return 清算原结算费
     */
    public BigDecimal getSettleFee() {
        return settleFee;
    }

    /**
     * 设置 清算原结算费
     * @param settleFee 清算原结算费
     */
    public void setSettleFee(BigDecimal settleFee) {
        this.settleFee = settleFee;
    }

    /**
     * 取得 清算原过户费
     * @return 清算原过户费
     */
    public BigDecimal getGhfAmt() {
        return ghfAmt;
    }

    /**
     * 设置 清算原过户费
     * @param ghfAmt 清算原过户费
     */
    public void setGhfAmt(BigDecimal ghfAmt) {
        this.ghfAmt = ghfAmt;
    }

    /**
     * 取得 清算原手续费
     * @return 清算原手续费
     */
    public BigDecimal getSxfAmt() {
        return sxfAmt;
    }

    /**
     * 设置 清算原手续费
     * @param sxfAmt 清算原手续费
     */
    public void setSxfAmt(BigDecimal sxfAmt) {
        this.sxfAmt = sxfAmt;
    }

    /**
     * 取得 清算原全价总额
     * @return 清算原全价总额
     */
    public BigDecimal getFpriceAmt() {
        return fpriceAmt;
    }

    /**
     * 设置 清算原全价总额
     * @param fpriceAmt 清算原全价总额
     */
    public void setFpriceAmt(BigDecimal fpriceAmt) {
        this.fpriceAmt = fpriceAmt;
    }

    /**
     * 取得 清算原成交数量
     * @return 清算原成交数量
     */
    public BigDecimal getTradeNum() {
        return tradeNum;
    }

    /**
     * 设置 清算原成交数量
     * @param tradeNum 清算原成交数量
     */
    public void setTradeNum(BigDecimal tradeNum) {
        this.tradeNum = tradeNum;
    }

    /**
     * 取得 对账信息
     * @return 对账信息
     */
    public byte[] getReconInfo() {
        return reconInfo;
    }

    /**
     * 设置 对账信息
     * @param reconInfo 对账信息
     */
    public void setReconInfo(byte[] reconInfo) {
        this.reconInfo = reconInfo;
    }

    public String getEntrustTradeId() {
        return entrustTradeId;
    }

    public void setEntrustTradeId(String entrustTradeId) {
        this.entrustTradeId = entrustTradeId == null ? null : entrustTradeId.trim();
    }

    private String finprodName;

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    //证券账户账号
    private String secAcctId;
    //资金账户账号
    private String busiAcctNo;
    //资金账户名称
    private String acctName;
    //系统持仓
    private BigDecimal xiTongAmount;
    //券商持仓
    private BigDecimal quanShangAmount;

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getBusiAcctNo() {
        return busiAcctNo;
    }

    public void setBusiAcctNo(String busiAcctNo) {
        this.busiAcctNo = busiAcctNo;
    }

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }

    public BigDecimal getXiTongAmount() {
        return xiTongAmount;
    }

    public void setXiTongAmount(BigDecimal xiTongAmount) {
        this.xiTongAmount = xiTongAmount;
    }

    public BigDecimal getQuanShangAmount() {
        return quanShangAmount;
    }

    public void setQuanShangAmount(BigDecimal quanShangAmount) {
        this.quanShangAmount = quanShangAmount;
    }

    /**
     * 是否有子节点
     */
    private String hasChildren;

    /**
     * 父节点
     */
    private String parentInfo;
    /**
     * 子节点
     */
    private String childInfo;

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(String hasChildren) {
        this.hasChildren = hasChildren;
    }

    public String getParentInfo() {
        return parentInfo;
    }

    public void setParentInfo(String parentInfo) {
        this.parentInfo = parentInfo;
    }

    public String getChildInfo() {
        return childInfo;
    }

    public void setChildInfo(String childInfo) {
        this.childInfo = childInfo;
    }
}