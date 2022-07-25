package com.joyintech.tams.cfets.etrdispatcher.dto;

import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModelProperty;

public class CfetsEtrDispatcherBaseInfo {

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

    @ApiModelProperty(name="entrustId",value="指令代码",example="字符串1")
    private String entrustId;

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

    public void setBusiModel(String busiModel) {
        this.busiModel = busiModel;
    }

    public String getBusiModelStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.业务模式, this.getBusiModel());
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

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }
}
