package com.joyintech.tams.exchange.basicdata.bussiness.traderinfo.dto;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="交易商信息维护返回参数",description="交易商信息维护返回参数")
public class TradeInfoProResponse {

    @ApiModelProperty(name="dealer",value="交易商",example="A1", required = true)
    private String dealer;

    @ApiModelProperty(name="tradeMarket",value="交易场所",example="A1", required = true)
    private String tradeMarket;

    @ApiModelProperty(name="dealerNo",value="交易商代码",example="A1", required = true)
    private String dealerNo;

    @ApiModelProperty(name="dealerPartyNo",value="交易主体代码",example="A1", required = true)
    private String dealerPartyNo;

    @ApiModelProperty(name="dealerPartyType",value="交易主体类型",example="A1", required = true)
    private String dealerPartyType;

    @ApiModelProperty(name="dealerPartyName",value="交易主体名称",example="A1", required = true)
    private String dealerPartyName;

    @ApiModelProperty(name="trader",value="交易员",example="A1", required = true)
    private String trader;

    @ApiModelProperty(name="traderNo",value="交易员代码",example="A1", required = true)
    private String traderNo;

    @ApiModelProperty(name="isOur",value="本方标识",example="A1", required = true)
    private String isOur;

    public String getDealer() {
        return dealer;
    }

    public void setDealer(String dealer) {
        this.dealer = dealer;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getDealerNo() {
        return dealerNo;
    }

    public void setDealerNo(String dealerNo) {
        this.dealerNo = dealerNo;
    }

    public String getDealerPartyType() {
        return dealerPartyType;
    }

    public void setDealerPartyType(String dealerPartyType) {
        this.dealerPartyType = dealerPartyType;
    }

    public String getDealerPartyName() {
        return dealerPartyName;
    }

    public void setDealerPartyName(String dealerPartyName) {
        this.dealerPartyName = dealerPartyName;
    }

    public String getTrader() {
        return trader;
    }

    public void setTrader(String trader) {
        this.trader = trader;
    }

    public String getTraderNo() {
        return traderNo;
    }

    public void setTraderNo(String traderNo) {
        this.traderNo = traderNo;
    }

    public String getIsOur() {
        return isOur;
    }

    public void setIsOur(String isOur) {
        this.isOur = isOur;
    }
}
