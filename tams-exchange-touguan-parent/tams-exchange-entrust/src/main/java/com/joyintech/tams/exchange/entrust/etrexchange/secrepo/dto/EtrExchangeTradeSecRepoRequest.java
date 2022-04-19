package com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="查询计算数据共通请求参数",description="查询计算数据共通请求参数")
public class EtrExchangeTradeSecRepoRequest {

    /**
     * 资产代码
     */
    @ApiModelProperty(name="finprodId",value="资产代码")
    private String finprodId;

    /**
     * 组合代码
     */
    @ApiModelProperty(name="portfolioId",value="组合代码")
    private String portfolioId;
    /**
     * 资金账户
     */
    @ApiModelProperty(name="accId",value="资金账户")
    private String accId;
    /**
     * 证券账户
     */
    @ApiModelProperty(name="secAcctId",value="证券账户")
    private String secAcctId;
    /**
     * 证券账户
     */
    @ApiModelProperty(name="acctId",value="资金账户")
    private String acctId;

    /**
     * 业务类型
     */
    @ApiModelProperty(name="othType",value="业务类型")
    private String othType;

    @ApiModelProperty(name="finprodType2",value="金融产品类型")
    private String finprodType2;

    @ApiModelProperty(name="ps",value="交易方向")
    private String ps;

    @ApiModelProperty(name="finprodMarketId",value="市场代码")
    private String finprodMarketId;

    @ApiModelProperty(name="unitCprice",value="净价")
    private String unitCprice;

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getOthType() {
        return othType;
    }

    public void setOthType(String othType) {
        this.othType = othType;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getUnitCprice() {
        return unitCprice;
    }

    public void setUnitCprice(String unitCprice) {
        this.unitCprice = unitCprice;
    }

    public String getAcctId() {
        return acctId;
    }

    public void setAcctId(String acctId) {
        this.acctId = acctId;
    }

    public String getAccId() {
        return accId;
    }

    public void setAccId(String accId) {
        this.accId = accId;
    }
}
