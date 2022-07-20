package com.joyintech.tams.invest.batch.cfetssettle.dto;


import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value="交易查询质押券返回数据",description="交易查询质押券返回数据")
public class CfetsPledgeInfoResponse {

    @ApiModelProperty(name="portfolioId",value="组合代码",example="A1")
    private String portfolioId;

    @ApiModelProperty(name="portfolioName",value="组合名称",example="A1")
    private String portfolioName;

    @ApiModelProperty(name="finprodId",value="资产代码Id",example="ZC1")
    private String finprodId;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",example="ZC1")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="ZC名")
    private String finprodAbbr;

    @ApiModelProperty(name="avaPledgePrin",value="可质押券面总额（万元）",example="ZC名")
    private BigDecimal avaPledgePrin;

    @ApiModelProperty(name="pledgePrin",value="质押券面额（万元）",example="ZC名")
    private BigDecimal pledgePrin;

    @ApiModelProperty(name="firstNetPrice",value="首次净价（元）",example="ZC名")
    private BigDecimal firstNetPrice;

    @ApiModelProperty(name="pledgeRate",value="折算比例（%）",example="ZC名")
    private BigDecimal pledgeRate;

    @ApiModelProperty(name="repRegistOrg",value="登记托管机构",example="ZC名")
    private String repRegistOrg;

    @ApiModelProperty(name="repRegistOrgStr",value="登记托管机构Str",example="ZC名")
    private String repRegistOrgStr;

    @ApiModelProperty(name="pledgeFaceValue",value="券面总额（万元）",example="11")
    private BigDecimal  pledgeFaceValue;

    @ApiModelProperty(name="disFaceValue",value="可质押券面（万元）",example="11")
    private BigDecimal disFaceValue;


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

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
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

    public BigDecimal getAvaPledgePrin() {
        return avaPledgePrin;
    }

    public void setAvaPledgePrin(BigDecimal avaPledgePrin) {
        this.avaPledgePrin = avaPledgePrin;
    }

    public BigDecimal getPledgePrin() {
        return pledgePrin;
    }

    public void setPledgePrin(BigDecimal pledgePrin) {
        this.pledgePrin = pledgePrin;
    }

    public BigDecimal getFirstNetPrice() {
        return firstNetPrice;
    }

    public void setFirstNetPrice(BigDecimal firstNetPrice) {
        this.firstNetPrice = firstNetPrice;
    }

    public BigDecimal getPledgeRate() {
        return pledgeRate;
    }

    public void setPledgeRate(BigDecimal pledgeRate) {
        this.pledgeRate = pledgeRate;
    }

    public String getRepRegistOrg() {
        return repRegistOrg;
    }

    public void setRepRegistOrg(String repRegistOrg) {
        this.repRegistOrg = repRegistOrg;
    }

    public String getRepRegistOrgStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.登记托管机构, getRepRegistOrg());
    }

    public void setRepRegistOrgStr(String repRegistOrgStr) {
        this.repRegistOrgStr = repRegistOrgStr;
    }

    public BigDecimal getPledgeFaceValue() {
        return pledgeFaceValue;
    }

    public void setPledgeFaceValue(BigDecimal pledgeFaceValue) {
        this.pledgeFaceValue = pledgeFaceValue;
    }

    public BigDecimal getDisFaceValue() {
        return disFaceValue;
    }

    public void setDisFaceValue(BigDecimal disFaceValue) {
        this.disFaceValue = disFaceValue;
    }
}
