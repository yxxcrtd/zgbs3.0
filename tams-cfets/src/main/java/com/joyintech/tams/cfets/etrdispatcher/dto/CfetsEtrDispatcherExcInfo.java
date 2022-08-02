package com.joyintech.tams.cfets.etrdispatcher.dto;

import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModelProperty;

public class CfetsEtrDispatcherExcInfo {

    @ApiModelProperty(name="busiTypePosition",value="业务类型",example="债券一级投标/现券买卖CODE",required = true)
    private String busiTypePosition;

    @ApiModelProperty(name="entrustDate",value="指令日期",example="2021-01-01")
    private String entrustDate;

    @ApiModelProperty(name="portfolioId",value="组合代码",example="字符串1")
    private String portfolioId;

    @ApiModelProperty(name="portfolioName",value="组合名称",example="字符串1")
    private String portfolioName;

    @ApiModelProperty(name="busiModel",value="业务模式Code",example="01")
    private String busiModel;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",example="字符串1")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="字符串1")
    private String finprodAbbr;

    @ApiModelProperty(name="tradeMarket",value="交易场所",example="字符串1")
    private String tradeMarket;

    @ApiModelProperty(name="ccy",value="币种",example="CNY")
    private String ccy;

    @ApiModelProperty(name="ps",value="交易方向",example="字符串1")
    private String ps;

    @ApiModelProperty(name="assetType",value="资产类型",example="字符串1")
    private String assetType;

    @ApiModelProperty(name="invManagerId",value="投资经理",example="字符串1")
    private String invManagerId;

    @ApiModelProperty(name="targetType",value="目标类型",example="字符串1")
    private String targetType;

    @ApiModelProperty(name="counterId",value="交易对手",example="字符串1")
    private String counterId;

    @ApiModelProperty(name="counterSeat",value="对方席位",example="字符串1")
    private String counterSeat;

    @ApiModelProperty(name="counterConNo",value="约定号",example="字符串1")
    private String counterConNo;

    @ApiModelProperty(name="trader",value="本方交易员",example="字符串1")
    private String trader;

    @ApiModelProperty(name="counterTrader",value="对方交易员",example="字符串1")
    private String counterTrader;

    @ApiModelProperty(name="conId",value="股票代码",example="字符串1")
    private String conId;
    
    @ApiModelProperty(name="conName",value="股票名称",example="字符串1")
    private String conName;

    @ApiModelProperty(name="expConValue",value="转股价值",example="10000")
    private String expConValue;

    @ApiModelProperty(name="expConNum",value="可转股数量",example="10000")
    private String expConNum;

    @ApiModelProperty(name="preb",value="转股溢价率",example="10000")
    private String preb;

    @ApiModelProperty(name="prec",value="纯债溢价率",example="10000")
    private String prec;

    @ApiModelProperty(name="dealCurAcct",value="资金账号",example="字符串1")
    private String dealCurAcct;

    @ApiModelProperty(name="requestId",value="请求编号",example="字符串1")
    private String requestId;

    @ApiModelProperty(name="capDays",value="占款天数",example="A1")
    private String capDays;

    public String getCapDays() {
        return capDays;
    }

    public void setCapDays(String capDays) {
        this.capDays = capDays;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getDealCurAcct() {
        return dealCurAcct;
    }

    public void setDealCurAcct(String dealCurAcct) {
        this.dealCurAcct = dealCurAcct;
    }

    public String getPreb() {
        return preb;
    }

    public void setPreb(String preb) {
        this.preb = preb;
    }

    public String getPrec() {
        return prec;
    }

    public void setPrec(String prec) {
        this.prec = prec;
    }

    public String getConId() {
        return conId;
    }

    public void setConId(String conId) {
        this.conId = conId;
    }

    public String getConName() {
        return conName;
    }

    public void setConName(String conName) {
        this.conName = conName;
    }

    public String getExpConValue() {
        return expConValue;
    }

    public void setExpConValue(String expConValue) {
        this.expConValue = expConValue;
    }

    public String getExpConNum() {
        return expConNum;
    }

    public void setExpConNum(String expConNum) {
        this.expConNum = expConNum;
    }

    public String getCounterTrader() {
        return counterTrader;
    }

    public void setCounterTrader(String counterTrader) {
        this.counterTrader = counterTrader;
    }

    public String getTrader() {
        return trader;
    }

    public void setTrader(String trader) {
        this.trader = trader;
    }

    public String getCounterConNo() {
        return counterConNo;
    }

    public void setCounterConNo(String counterConNo) {
        this.counterConNo = counterConNo;
    }

    public String getCounterSeat() {
        return counterSeat;
    }

    public void setCounterSeat(String counterSeat) {
        this.counterSeat = counterSeat;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getInvManagerId() {
        return invManagerId;
    }

    public void setInvManagerId(String invManagerId) {
        this.invManagerId = invManagerId;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getBusiTypePosition() {
        return busiTypePosition;
    }

    public void setBusiTypePosition(String busiTypePosition) {
        this.busiTypePosition = busiTypePosition;
    }

    public String getEntrustDate() {
        return entrustDate;
    }

    public void setEntrustDate(String entrustDate) {
        this.entrustDate = entrustDate;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getBusiModel() {
        return busiModel;
    }

    public String getBusiModelStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.业务模式, this.getBusiModel());
    }

    public void setBusiModel(String busiModel) {
        this.busiModel = busiModel;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }
}
