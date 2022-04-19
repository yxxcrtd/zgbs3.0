package com.joyintech.tams.cfets.manualsettle.dto;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

//组合交收明细DTO
public class PortSettleinfoDto {

    @ApiModelProperty(name="portfolioId",value="组合代码",example="A1")
    private String portfolioId;

    @ApiModelProperty(name="portfolioName",value="组合名称",example="A1")
    private String portfolioName;

    @ApiModelProperty(name="settleNum",value="交收数量",example="A1")
    private BigDecimal settleNum;

    @ApiModelProperty(name="settleAmt",value="交收金额",example="A1")
    private BigDecimal settleAmt;

    @ApiModelProperty(name="tradeId",value="交易编号",example="A1")
    private String tradeId;

    @ApiModelProperty(name="entrustId",value="委托编号",example="A1")
    private String entrustId;

    @ApiModelProperty(name="ccy",value="交易币种",example="A1")
    private String ccy;

    @ApiModelProperty(name="finprodId",value="金融产品代码",example="A1")
    private String finprodId;

    @ApiModelProperty(name="invAim",value="投资目的",example="A1")
    private String invAim;

    @ApiModelProperty(name="ourShareAmt",value="本方券交易份额",example="A1")
    private BigDecimal ourShareAmt;

    @ApiModelProperty(name="lendinShareAmt",value="融入券交易份额",example="A1")
    private BigDecimal lendinShareAmt;

    @ApiModelProperty(name="settleSpeed",value="清算速度",example="A1")
    private String settleSpeed;

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

    public BigDecimal getSettleNum() {
        return settleNum;
    }

    public void setSettleNum(BigDecimal settleNum) {
        this.settleNum = settleNum;
    }

    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    public BigDecimal getOurShareAmt() {
        return ourShareAmt;
    }

    public void setOurShareAmt(BigDecimal ourShareAmt) {
        this.ourShareAmt = ourShareAmt;
    }

    public BigDecimal getLendinShareAmt() {
        return lendinShareAmt;
    }

    public void setLendinShareAmt(BigDecimal lendinShareAmt) {
        this.lendinShareAmt = lendinShareAmt;
    }

    public String getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
    }
}
