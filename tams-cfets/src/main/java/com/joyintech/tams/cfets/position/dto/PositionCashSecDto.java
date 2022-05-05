package com.joyintech.tams.cfets.position.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 组合数据集
 */
public class PositionCashSecDto {

    @ApiModelProperty(name = "portfolioId", value = "组合代码", example = "AAA")
    private String portfolioId; // 组合代码

    @ApiModelProperty(name = "acctId", value = "资金账号", example = "111")
    private String acctId; // 资金账号

    @ApiModelProperty(name = "secAcctId", value = "证券管理账户查询持仓时必传", example = "111")
    private String secAcctId; // 证券管理账户查询持仓时必传

    @ApiModelProperty(name = "cdate", value = "日期", example = "2022-05-05")
    private String cdate; // 日期

    @ApiModelProperty(name = "finprodId", value = "资产代码 查询持仓时必传", example = "F01_11.IB")
    private String finprodId; // 资产代码 查询持仓时必传

    @ApiModelProperty(name = "dateType", value = "处理速度", example = "")
    private String dateType; // 处理速度

    @ApiModelProperty(name = "ps", value = "委托方向", example = "")
    private String ps; //委托方向

    @ApiModelProperty(name = "tradeMarket", value = "交易场所 查询资金时必传", example = "01")
    private String tradeMarket; //交易场所 查询资金时必传

    @ApiModelProperty(name = "ccy", value = "币种", example = "CNY")
    private String ccy; //币种

    @ApiModelProperty(name = "holdingType", value = "持仓类型", example = "A01")
    private String holdingType;//持仓类型

    @ApiModelProperty(name = "tradePlat", value = "交易平台 可用持仓必传 非交易", example = "")
    private String tradePlat; //交易平台 可用持仓必传 非交易:

    @ApiModelProperty(name = "finprodType2", value = "资产类型投管 可用持仓必传", example = "F01")
    private String finprodType2; //资产类型投管 可用持仓必传


    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getAcctId() {
        return acctId;
    }

    public void setAcctId(String acctId) {
        this.acctId = acctId;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getCdate() {
        return cdate;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }


    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
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


    public String getHoldingType() {
        return holdingType;
    }

    public void setHoldingType(String holdingType) {
        this.holdingType = holdingType;
    }

    public String getTradePlat() {
        return tradePlat;
    }

    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }
}
