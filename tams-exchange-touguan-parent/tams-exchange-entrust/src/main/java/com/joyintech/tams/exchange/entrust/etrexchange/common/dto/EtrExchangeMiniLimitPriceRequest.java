package com.joyintech.tams.exchange.entrust.etrexchange.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 组合数据集
 */
@ApiModel(value="债券指令头寸信息查询请求参数",description="债券指令头寸信息查询请求参数")
public class EtrExchangeMiniLimitPriceRequest {


    @ApiModelProperty(name="finprodId",value="资产代码",example="xxx", required = true)
    private String finprodId ;

    @ApiModelProperty(name="finprodId",value="交易方向",example="xxx", required = true)
    private String ps;
    @ApiModelProperty(name="tradePlat",value="交易平台(场内现券指令传'指定交易方式')",example="xxx", required = true)
    private String tradePlat;
    @ApiModelProperty(name="finprodType2",value="资产类型投管",example="xxx", required = true)
    private String finprodType2;

    @ApiModelProperty(name="tradeMarket",value="交易场所",example="xxx", required = true)
    private String tradeMarket;


    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
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

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }
}
