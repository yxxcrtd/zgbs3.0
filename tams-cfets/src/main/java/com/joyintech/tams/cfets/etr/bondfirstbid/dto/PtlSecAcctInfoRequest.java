package com.joyintech.tams.cfets.etr.bondfirstbid.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="组合资金账户请求参数",description="组合资金账户请求参数")
public class PtlSecAcctInfoRequest {
    @ApiModelProperty(name="acctNo",value="资金账户",example="A1", required = true)
    private String acctNo;

    @ApiModelProperty(name="portfolioId",value="组合代码",example="A1")
    private String portfolioId;

    @ApiModelProperty(required = true,name="tradeMarket",value="交易场所",example="A1")
    private String tradeMarket;

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }
}
