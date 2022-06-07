package com.joyintech.tams.cfets.trd.primarybidmanage.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value="计算价格和到期收益率返回参数",description="计算价格和到期收益率返回参数")
public class CalcPriceAndYieldResponse {

    @ApiModelProperty(name="unitInt",value="金融产品代码",example="A1")
    private BigDecimal unitInt;

    @ApiModelProperty(name="intAmt",value="默认使用百元券面计算",example="100")
    private BigDecimal intAmt;

    @ApiModelProperty(name="cprice",value="交易场所",example="A1")
    private BigDecimal cprice;


    @ApiModelProperty(name="cpriceAmt",value="交易方向",example="A1")
    private BigDecimal cpriceAmt;

    @ApiModelProperty(name="fprice",value="计算方式",example="A1")
    private BigDecimal fprice;

    @ApiModelProperty(name="fpriceAmt",value="净价",example="A1")
    private BigDecimal fpriceAmt;

    @ApiModelProperty(name="yield",value="全价",example="A1")
    private BigDecimal yield;
    @ApiModelProperty(name="exerYield",value="到期收益率",example="A1")
    private BigDecimal exerYield;

    public BigDecimal getUnitInt() {
        return unitInt;
    }

    public void setUnitInt(BigDecimal unitInt) {
        this.unitInt = unitInt;
    }

    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    public BigDecimal getCprice() {
        return cprice;
    }

    public void setCprice(BigDecimal cprice) {
        this.cprice = cprice;
    }

    public BigDecimal getCpriceAmt() {
        return cpriceAmt;
    }

    public void setCpriceAmt(BigDecimal cpriceAmt) {
        this.cpriceAmt = cpriceAmt;
    }

    public BigDecimal getFprice() {
        return fprice;
    }

    public void setFprice(BigDecimal fprice) {
        this.fprice = fprice;
    }

    public BigDecimal getFpriceAmt() {
        return fpriceAmt;
    }

    public void setFpriceAmt(BigDecimal fpriceAmt) {
        this.fpriceAmt = fpriceAmt;
    }

    public BigDecimal getYield() {
        return yield;
    }

    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

    public BigDecimal getExerYield() {
        return exerYield;
    }

    public void setExerYield(BigDecimal exerYield) {
        this.exerYield = exerYield;
    }
}