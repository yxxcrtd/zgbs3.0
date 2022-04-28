package com.joyintech.tams.exchange.common.bean.investment;

import com.google.common.base.MoreObjects;

import java.math.BigDecimal;
import java.util.List;

/**
 * 风控实占请求bean
 */
public class ReqRiskOccupy {

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
     * 回购类型
     */
    private String repurchaseType;

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
     * 资产代码（有资产代码必填）
     */
    private String assetCode;

    /**
     * 抵押券资产代码
     */
    private List<String> pledgeAssetCodeList;

    /**
     * 系统资产大类(没有资产代码必填)
     */
    private String assetType;

    /**
     * 系统资产细类(没有资产代码必填)
     */
    private String assetType2;

    /**
     * 交易方向
     */
    private String ps;

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
     * 事件类型
     */
    private String eventType;

    /**
     * 资管委托编号
     */
    private String entrustCode;

    /**
     * 交易员
     */
    private String trader;

    /**
     * 投资经理
     */
    private String manager;

    /**
     * 交割日
     */
    private String deliverDate;

    /**
     * 委托开始日
     */
    private String startDate;

    /**
     * 委托结束日
     */
    private String endDate;

    /**
     * 达到预警线是否需要人工确认(人工确认之后需要将该值设置为否)
     */
    private String isWarnNeedConfirm;

    /**
     * 达到审批线是否需要人工确认(人工确认之后需要将该值设置为否)
     */
    private String isApprNeedConfirm;

    /**
     * 预占编号
     */
    private String preOccupyCode;

    /**
     * 预占源系统代码
     */
    private String sourceSys;

    /**
     * 交易对手(转仓必填)
     */
    private String counterId;

    /**
     * 市场代码
     */
    private String marketCode;

    /**
     * 交易场所
     */
    private String tradePlace;

    /**
     * 交易币种
     */
    private String ccy;
    private String tradeId;//交易编号
    /**
     * 交易阶段
     */
    private String tradeStage;
    private String revokeFlg;//撤销实占标志
    private String seqNo;//释放流水号
    private String entrustId;// 委托代码
    private String firstIssueDealNo; // 成交编号  协议回购非首期必填

    public String getConShareId() {
        return conShareId;
    }

    public void setConShareId(String conShareId) {
        this.conShareId = conShareId;
    }

    public BigDecimal getConPrice() {
        return conPrice;
    }

    public void setConPrice(BigDecimal conPrice) {
        this.conPrice = conPrice;
    }

    public BigDecimal getConNum() {
        return conNum;
    }

    public void setConNum(BigDecimal conNum) {
        this.conNum = conNum;
    }

    public String getRepurchaseType() {
        return repurchaseType;
    }

    public void setRepurchaseType(String repurchaseType) {
        this.repurchaseType = repurchaseType;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

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

    public String getAssetCode() {
        return assetCode;
    }

    public void setAssetCode(String assetCode) {
        this.assetCode = assetCode;
    }

    public List<String> getPledgeAssetCodeList() {
        return pledgeAssetCodeList;
    }

    public void setPledgeAssetCodeList(List<String> pledgeAssetCodeList) {
        this.pledgeAssetCodeList = pledgeAssetCodeList;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAssetType2() {
        return assetType2;
    }

    public void setAssetType2(String assetType2) {
        this.assetType2 = assetType2;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public BigDecimal getAmount() {
        return amount;
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

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEntrustCode() {
        return entrustCode;
    }

    public void setEntrustCode(String entrustCode) {
        this.entrustCode = entrustCode;
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

    public String getDeliverDate() {
        return deliverDate;
    }

    public void setDeliverDate(String deliverDate) {
        this.deliverDate = deliverDate;
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

    public String getIsWarnNeedConfirm() {
        return isWarnNeedConfirm;
    }

    public void setIsWarnNeedConfirm(String isWarnNeedConfirm) {
        this.isWarnNeedConfirm = isWarnNeedConfirm;
    }

    public String getIsApprNeedConfirm() {
        return isApprNeedConfirm;
    }

    public void setIsApprNeedConfirm(String isApprNeedConfirm) {
        this.isApprNeedConfirm = isApprNeedConfirm;
    }

    public String getPreOccupyCode() {
        return preOccupyCode;
    }

    public void setPreOccupyCode(String preOccupyCode) {
        this.preOccupyCode = preOccupyCode;
    }

    public String getSourceSys() {
        return sourceSys;
    }

    public void setSourceSys(String sourceSys) {
        this.sourceSys = sourceSys;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getMarketCode() {
        return marketCode;
    }

    public void setMarketCode(String marketCode) {
        this.marketCode = marketCode;
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

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getTradeStage() {
        return tradeStage;
    }

    public void setTradeStage(String tradeStage) {
        this.tradeStage = tradeStage;
    }

    public String getRevokeFlg() {
        return revokeFlg;
    }

    public void setRevokeFlg(String revokeFlg) {
        this.revokeFlg = revokeFlg;
    }

    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("conShareId", conShareId)
                .add("conPrice", conPrice)
                .add("conNum", conNum)
                .add("varietyCode", varietyCode)
                .add("vdate", vdate)
                .add("mdate", mdate)
                .add("repurchaseType", repurchaseType)
                .add("eventDate", eventDate)
                .add("eventId", eventId)
                .add("portfolioCode", portfolioCode)
                .add("assetCode", assetCode)
                .add("pledgeAssetCodeList", pledgeAssetCodeList)
                .add("assetType", assetType)
                .add("assetType2", assetType2)
                .add("ps", ps)
                .add("amount", amount)
                .add("priceAmt", priceAmt)
                .add("tradeNum", tradeNum)
                .add("netPrice", netPrice)
                .add("allPrice", allPrice)
                .add("unitNet", unitNet)
                .add("assetRate", assetRate)
                .add("eventType", eventType)
                .add("entrustCode", entrustCode)
                .add("trader", trader)
                .add("manager", manager)
                .add("deliverDate", deliverDate)
                .add("startDate", startDate)
                .add("endDate", endDate)
                .add("isWarnNeedConfirm", isWarnNeedConfirm)
                .add("isApprNeedConfirm", isApprNeedConfirm)
                .add("preOccupyCode", preOccupyCode)
                .add("sourceSys", sourceSys)
                .add("counterId", counterId)
                .add("marketCode", marketCode)
                .add("tradePlace", tradePlace)
                .add("ccy", ccy)
                .add("tradeId", tradeId)
                .add("tradeStage", tradeStage)
                .add("revokeFlg", revokeFlg)
                .add("seqNo", seqNo)
                .add("entrustId", entrustId)
                .add("firstIssueDealNo", firstIssueDealNo)
                .toString();
    }
}
