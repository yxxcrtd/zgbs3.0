package com.joyintech.tams.valuation.common.interf.tempTables.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;

import java.math.BigDecimal;
import java.util.Date;

public class ValStockDeal extends BaseEntity {
    /**
     * 产品代码
     */
    private String prodId;

    /**
     * 资产代码
     */
    private String assetId;

    /**
     * 资产代码
     */
    private String assetAbbr;

    /**
     * 新增、修改、删除
     */
    private String dataActionSign;

    /**
     * 交易场所
     */
    private String tradeMarket;

    /**
     * 交易日
     */
    private Date dealDate;

    /**
     * 交割日
     */
    private Date settdateDate;

    /**
     * 股票交易类型
     */
    private String stockTradeType;

    /**
     * 交易发行方式
     */
    private String dealIssueType;

    /**
     * 成交数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal dealNum;

    /**
     * 成交价格
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal unitCprice;

    /**
     * 中签金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal cprice;

    /**
     * 未随交易支付费用
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal feeAmtUnpay;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 交易编号
     */
    private String tradeId;

    /**
     * 印花税
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal stampDuty;

    /**
     * 印花税是否随交易支付
     */
    private String isStampDuty;

    /**
     * 经手费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal brokerage;

    /**
     * 经手费是否随交易支付
     */
    private String isBrokerage;

    /**
     * 证管费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal certfFee;

    /**
     * 证管费是否随交易支付
     */
    private String isCertfFee;

    /**
     * 结算费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal stlAmt;

    /**
     * 结算费是否随交易支付
     */
    private String isStlAmt;


    /**
     * 过户费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal transferAmt;

    /**
     * 过户费是否随交易支付
     */
    private String isTransferAmt;

    /**
     * 佣金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal commission;

    /**
     * 佣金是否随交易支付
     */
    private String isCommission;

    /**
     * 交易手续费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal dealFeeAmt;

    /**
     * 交易手续费是否随交易支付
     */
    private String isDealFeeAmt;

    /**
     * 其他费用
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal otherFeeAmt;

    /**
     * 其他费用是否随交易支付
     */
    private String isOtherFeeAmt;

    /**
     * 交易金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tradeAmt;

    /**
     * 有无清算指令
     */
    private String isOrders;

    /**
     * 资金账户
     */
    private String myAcctNo;

    /**
     * 外部成交编号
     */
    private String outTradeId;

    /**
     * 数据来源
     */
    private String dataSource;


    /**
     * 是否流通受限
     */
    private String isLimited;

    /**
     * 受限开始日期
     */
    private Date limitBeginDate;

    /**
     * 受限结束日期
     */
    private Date limitEndDate;

    /**
     * 预期上市流通日
     */
    private Date expCirculateDate;

    /**
     * 锁定天数
     */
    private BigDecimal limitDays;

    /**
     *  证券管理户
     */
    private String secManageAcctId;

    /**
     * 取得 产品代码
     * @return 产品代码
     */
    public String getProdId() {
        return prodId;
    }

    /**
     * 设置 产品代码
     * @param prodId 产品代码
     */
    public void setProdId(String prodId) {
        this.prodId = prodId == null ? null : prodId.trim();
    }

    /**
     * 取得 资产代码
     * @return 资产代码
     */
    public String getAssetId() {
        return assetId;
    }

    /**
     * 设置 资产代码
     * @param assetId 资产代码
     */
    public void setAssetId(String assetId) {
        this.assetId = assetId == null ? null : assetId.trim();
    }

    /**
     * 取得 新增、修改、删除
     * @return 新增、修改、删除
     */
    public String getDataActionSign() {
        return dataActionSign;
    }

    /**
     * 设置 新增、修改、删除
     * @param dataActionSign 新增、修改、删除
     */
    public void setDataActionSign(String dataActionSign) {
        this.dataActionSign = dataActionSign == null ? null : dataActionSign.trim();
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

    /**
     * 取得 交易日
     * @return 交易日
     */
    public Date getDealDate() {
        return dealDate;
    }

    /**
     * 设置 交易日
     * @param dealDate 交易日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setDealDate(Date dealDate) {
        this.dealDate = dealDate;
    }

    /**
     * 取得 交割日
     * @return 交割日
     */
    public Date getSettdateDate() {
        return settdateDate;
    }

    /**
     * 设置 交割日
     * @param settdateDate 交割日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setSettdateDate(Date settdateDate) {
        this.settdateDate = settdateDate;
    }

    /**
     * 取得 成交数量
     * @return 成交数量
     */
    public BigDecimal getDealNum() {
        return dealNum;
    }

    /**
     * 设置 成交数量
     * @param dealNum 成交数量
     */
    public void setDealNum(BigDecimal dealNum) {
        this.dealNum = dealNum;
    }

    /**
     * 取得 未随交易支付费用
     * @return 未随交易支付费用
     */
    public BigDecimal getFeeAmtUnpay() {
        return feeAmtUnpay;
    }

    /**
     * 设置 未随交易支付费用
     * @param feeAmtUnpay 未随交易支付费用
     */
    public void setFeeAmtUnpay(BigDecimal feeAmtUnpay) {
        this.feeAmtUnpay = feeAmtUnpay;
    }

    /**
     * 取得 成交价格
     * @return 成交价格
     */
    public BigDecimal getUnitCprice() {
        return unitCprice;
    }

    /**
     * 设置 成交价格
     * @param unitCprice 成交价格
     */
    public void setUnitCprice(BigDecimal unitCprice) {
        this.unitCprice = unitCprice;
    }

    /**
     * 取得 币种
     * @return 币种
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * 设置 币种
     * @param ccy 币种
     */
    public void setCcy(String ccy) {
        this.ccy = ccy == null ? null : ccy.trim();
    }

    /**
     * 取得 交易编号
     * @return 交易编号
     */
    public String getTradeId() {
        return tradeId;
    }

    /**
     * 设置 交易编号
     * @param tradeId 交易编号
     */
    public void setTradeId(String tradeId) {
        this.tradeId = tradeId == null ? null : tradeId.trim();
    }

    /**
     * 取得 印花税
     * @return 印花税
     */
    public BigDecimal getStampDuty() {
        return stampDuty;
    }

    /**
     * 设置 印花税
     * @param stampDuty 印花税
     */
    public void setStampDuty(BigDecimal stampDuty) {
        this.stampDuty = stampDuty;
    }

    /**
     * 取得 印花税是否随交易支付
     * @return 印花税是否随交易支付
     */
    public String getIsStampDuty() {
        return isStampDuty;
    }

    /**
     * 设置 印花税是否随交易支付
     * @param isStampDuty 印花税是否随交易支付
     */
    public void setIsStampDuty(String isStampDuty) {
        this.isStampDuty = isStampDuty == null ? null : isStampDuty.trim();
    }

    /**
     * 取得 经手费
     * @return 经手费
     */
    public BigDecimal getBrokerage() {
        return brokerage;
    }

    /**
     * 设置 经手费
     * @param brokerage 经手费
     */
    public void setBrokerage(BigDecimal brokerage) {
        this.brokerage = brokerage;
    }

    /**
     * 取得 经手费是否随交易支付
     * @return 经手费是否随交易支付
     */
    public String getIsBrokerage() {
        return isBrokerage;
    }

    /**
     * 设置 经手费是否随交易支付
     * @param isBrokerage 经手费是否随交易支付
     */
    public void setIsBrokerage(String isBrokerage) {
        this.isBrokerage = isBrokerage == null ? null : isBrokerage.trim();
    }

    /**
     * 取得 证管费
     * @return 证管费
     */
    public BigDecimal getCertfFee() {
        return certfFee;
    }

    /**
     * 设置 证管费
     * @param certfFee 证管费
     */
    public void setCertfFee(BigDecimal certfFee) {
        this.certfFee = certfFee;
    }

    /**
     * 取得 证管费是否随交易支付
     * @return 证管费是否随交易支付
     */
    public String getIsCertfFee() {
        return isCertfFee;
    }

    /**
     * 设置 证管费是否随交易支付
     * @param isCertfFee 证管费是否随交易支付
     */
    public void setIsCertfFee(String isCertfFee) {
        this.isCertfFee = isCertfFee == null ? null : isCertfFee.trim();
    }

    /**
     * 取得 结算费
     * @return 结算费
     */
    public BigDecimal getStlAmt() {
        return stlAmt;
    }

    /**
     * 设置 结算费
     * @param stlAmt 结算费
     */
    public void setStlAmt(BigDecimal stlAmt) {
        this.stlAmt = stlAmt;
    }

    /**
     * 取得 结算费是否随交易支付
     * @return 结算费是否随交易支付
     */
    public String getIsStlAmt() {
        return isStlAmt;
    }

    /**
     * 设置 结算费是否随交易支付
     * @param isStlAmt 结算费是否随交易支付
     */
    public void setIsStlAmt(String isStlAmt) {
        this.isStlAmt = isStlAmt == null ? null : isStlAmt.trim();
    }

    /**
     * 取得 佣金
     * @return 佣金
     */
    public BigDecimal getCommission() {
        return commission;
    }

    /**
     * 设置 佣金
     * @param commission 佣金
     */
    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    /**
     * 取得 佣金是否随交易支付
     * @return 佣金是否随交易支付
     */
    public String getIsCommission() {
        return isCommission;
    }

    /**
     * 设置 佣金是否随交易支付
     * @param isCommission 佣金是否随交易支付
     */
    public void setIsCommission(String isCommission) {
        this.isCommission = isCommission == null ? null : isCommission.trim();
    }

    /**
     * 取得 交易手续费
     * @return 交易手续费
     */
    public BigDecimal getDealFeeAmt() {
        return dealFeeAmt;
    }

    /**
     * 设置 交易手续费
     * @param dealFeeAmt 交易手续费
     */
    public void setDealFeeAmt(BigDecimal dealFeeAmt) {
        this.dealFeeAmt = dealFeeAmt;
    }

    /**
     * 取得 交易手续费是否随交易支付
     * @return 交易手续费是否随交易支付
     */
    public String getIsDealFeeAmt() {
        return isDealFeeAmt;
    }

    /**
     * 设置 交易手续费是否随交易支付
     * @param isDealFeeAmt 交易手续费是否随交易支付
     */
    public void setIsDealFeeAmt(String isDealFeeAmt) {
        this.isDealFeeAmt = isDealFeeAmt == null ? null : isDealFeeAmt.trim();
    }

    /**
     * 取得 其他费用
     * @return 其他费用
     */
    public BigDecimal getOtherFeeAmt() {
        return otherFeeAmt;
    }

    /**
     * 设置 其他费用
     * @param otherFeeAmt 其他费用
     */
    public void setOtherFeeAmt(BigDecimal otherFeeAmt) {
        this.otherFeeAmt = otherFeeAmt;
    }

    /**
     * 取得 其他费用是否随交易支付
     * @return 其他费用是否随交易支付
     */
    public String getIsOtherFeeAmt() {
        return isOtherFeeAmt;
    }

    /**
     * 设置 其他费用是否随交易支付
     * @param isOtherFeeAmt 其他费用是否随交易支付
     */
    public void setIsOtherFeeAmt(String isOtherFeeAmt) {
        this.isOtherFeeAmt = isOtherFeeAmt == null ? null : isOtherFeeAmt.trim();
    }

    /**
     * 取得 交易金额
     * @return 交易金额
     */
    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    /**
     * 设置 交易金额
     * @param tradeAmt 交易金额
     */
    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    /**
     * 取得 有无清算指令
     * @return 有无清算指令
     */
    public String getIsOrders() {
        return isOrders;
    }

    /**
     * 设置 有无清算指令
     * @param isOrders 有无清算指令
     */
    public void setIsOrders(String isOrders) {
        this.isOrders = isOrders == null ? null : isOrders.trim();
    }

    /**
     * 取得 资金账户
     * @return 资金账户
     */
    public String getMyAcctNo() {
        return myAcctNo;
    }

    /**
     * 设置 资金账户
     * @param myAcctNo 资金账户
     */
    public void setMyAcctNo(String myAcctNo) {
        this.myAcctNo = myAcctNo == null ? null : myAcctNo.trim();
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public String getStockTradeType() {
        return stockTradeType;
    }

    public void setStockTradeType(String stockTradeType) {
        this.stockTradeType = stockTradeType;
    }

    public String getDealIssueType() {
        return dealIssueType;
    }

    public void setDealIssueType(String dealIssueType) {
        this.dealIssueType = dealIssueType;
    }

    public BigDecimal getCprice() {
        return cprice;
    }

    public void setCprice(BigDecimal cprice) {
        this.cprice = cprice;
    }

    public String getIsLimited() {
        return isLimited;
    }

    public void setIsLimited(String isLimited) {
        this.isLimited = isLimited;
    }

    public Date getLimitBeginDate() {
        return limitBeginDate;
    }

    public void setLimitBeginDate(Date limitBeginDate) {
        this.limitBeginDate = limitBeginDate;
    }

    public Date getLimitEndDate() {
        return limitEndDate;
    }

    public void setLimitEndDate(Date limitEndDate) {
        this.limitEndDate = limitEndDate;
    }

    public Date getExpCirculateDate() {
        return expCirculateDate;
    }

    public void setExpCirculateDate(Date expCirculateDate) {
        this.expCirculateDate = expCirculateDate;
    }

    public BigDecimal getLimitDays() {
        return limitDays;
    }

    public void setLimitDays(BigDecimal limitDays) {
        this.limitDays = limitDays;
    }

    public String getAssetAbbr() {
        return assetAbbr;
    }

    public void setAssetAbbr(String assetAbbr) {
        this.assetAbbr = assetAbbr;
    }

    public String getOutTradeId() {
        return outTradeId;
    }

    public void setOutTradeId(String outTradeId) {
        this.outTradeId = outTradeId;
    }

    public BigDecimal getTransferAmt() {
        return transferAmt;
    }

    public void setTransferAmt(BigDecimal transferAmt) {
        this.transferAmt = transferAmt;
    }

    public String getIsTransferAmt() {
        return isTransferAmt;
    }

    public void setIsTransferAmt(String isTransferAmt) {
        this.isTransferAmt = isTransferAmt;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }
}