package com.joyintech.tams.exchange.common.bean.investment;

import java.math.BigDecimal;

/**
 * 触发风险请求bean
 */
public class ReqRiskTrigger {

    /**
     * 转股代码
     */
    private String conShareId;

    /**
     * 转股价格
     */
    private BigDecimal conPrice;

    /**
     * 转股数量
     */
    private BigDecimal conNum;

    /**
     * 系统资产细类(没有资产代码必填)
     */
    private String assetType2;

    /**
     * 回购类型
     */
    private String repurchaseType;

    /**
     * 交易币种
     */
    private String ccy;

    /**
     * 交易场所
     */
    private String tradePlace;

    /**
     * 系统资产大类(没有资产代码必填)
     */
    private String assetType;

    /**
     * 交易日期
     */
    private String eventDate;

    /**
     * 交易代码
     */
    private String eventId;

    /**
     * 产品代码
     */
    private String portfolioCode;

    /**
     * 委托开始日
     */
    private String startDate;

    /**
     * 委托结束日
     */
    private String endDate;
    private String tradeDate;

    /**
     * 交割日
     */
    private String deliveryDate;

    /**
     * 交易方向
     */
    private String ps;

    /**
     * 资产代码（有资产代码必填）
     */
    private String assetCode;

    /**
     * 资产名称
     */
    private String assetName;
    private String market;

    /**
     * 金额
     */
    private BigDecimal amount;

    /**
     * 价格
     */
    private BigDecimal priceAmt;

    /**
     * 交易数量
     */
    private Long tradeNum;

    /**
     * 净价
     */
    private BigDecimal netPrice;

    /**
     * 全价
     */
    private BigDecimal allPrice;

    /**
     * 单位净值
     */
    private BigDecimal unitNet;

    /**
     * 资产利率
     */
    private BigDecimal assetRate;

    /**
     * 交易员
     */
    private String trader;

    /**
     * 投资经理
     */
    private String manager;

    /**
     * 审批发起人
     */
    private String createUser;

    /**
     * 审批人
     */
    private String approveUser;

    /**
     * 市场代码
     */
    private String marketCode;

    /**
     * 事件类型
     */
    private String eventType;

    /**
     * 期限品种
     */
    private String varietyCode;

    /**
     * 起息日
     */
    private String vdate;

    /**
     * 到期日
     */
    private String mdate;

    /**
     * 成交编号  协议回购非首期必填
     */
    private String firstIssueDealNo;

    /**
     * 资管委托编号
     */
    private String entrustCode;

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getPortfolioCode() {
        return portfolioCode;
    }

    public void setPortfolioCode(String portfolioCode) {
        this.portfolioCode = portfolioCode;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setConPrice(BigDecimal conPrice) {
        this.conPrice = conPrice;
    }

    public void setConNum(BigDecimal conNum) {
        this.conNum = conNum;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getPriceAmt() {
        return priceAmt;
    }

    public void setPriceAmt(BigDecimal priceAmt) {
        this.priceAmt = priceAmt;
    }

    public Long getTradeNum() {
        return tradeNum;
    }

    public void setTradeNum(Long tradeNum) {
        this.tradeNum = tradeNum;
    }

    public BigDecimal getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(BigDecimal netPrice) {
        this.netPrice = netPrice;
    }

    public BigDecimal getAllPrice() {
        return allPrice;
    }

    public void setAllPrice(BigDecimal allPrice) {
        this.allPrice = allPrice;
    }

    public BigDecimal getUnitNet() {
        return unitNet;
    }

    public void setUnitNet(BigDecimal unitNet) {
        this.unitNet = unitNet;
    }

    public BigDecimal getAssetRate() {
        return assetRate;
    }

    public void setAssetRate(BigDecimal assetRate) {
        this.assetRate = assetRate;
    }

    public String getTrader() {
        return trader;
    }

    public void setTrader(String trader) {
        this.trader = trader;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getCreateUser() {
        return createUser;
    }

    public void setCreateUser(String createUser) {
        this.createUser = createUser;
    }

    public String getApproveUser() {
        return approveUser;
    }

    public void setApproveUser(String approveUser) {
        this.approveUser = approveUser;
    }

    public String getMarketCode() {
        return marketCode;
    }

    public void setMarketCode(String marketCode) {
        this.marketCode = marketCode;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getTradePlace() {
        return tradePlace;
    }

    public void setTradePlace(String tradePlace) {
        this.tradePlace = tradePlace;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getAssetType2() {
        return assetType2;
    }

    public void setAssetType2(String assetType2) {
        this.assetType2 = assetType2;
    }

    public String getRepurchaseType() {
        return repurchaseType;
    }

    public void setRepurchaseType(String repurchaseType) {
        this.repurchaseType = repurchaseType;
    }

    public String getConShareId() {
        return conShareId;
    }

    public void setConShareId(String conShareId) {
        this.conShareId = conShareId;
    }

    public BigDecimal getConPrice() {
        return conPrice;
    }

    public BigDecimal getConNum() {
        return conNum;
    }

    public String getEntrustCode() {
        return entrustCode;
    }

    public void setEntrustCode(String entrustCode) {
        this.entrustCode = entrustCode;
    }

    public String getVarietyCode() {
        return varietyCode;
    }

    public void setVarietyCode(String varietyCode) {
        this.varietyCode = varietyCode;
    }

    public String getVdate() {
        return vdate;
    }

    public void setVdate(String vdate) {
        this.vdate = vdate;
    }

    public String getMdate() {
        return mdate;
    }

    public void setMdate(String mdate) {
        this.mdate = mdate;
    }

    public String getFirstIssueDealNo() {
        return firstIssueDealNo;
    }

    public void setFirstIssueDealNo(String firstIssueDealNo) {
        this.firstIssueDealNo = firstIssueDealNo;
    }
}
