package com.joyintech.tams.cfets.etr.bond.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 *
 * 指令下拉框模糊查询
 */
@ApiModel(value="新建指令金额计算功能返回参数",description="新建指令金额计算功能返回参数")
public class CfetsEtrCalcAmountResponse {


    @ApiModelProperty(name="price",value="净价",example="1")
    private BigDecimal price;

    @ApiModelProperty(name="priceAmt",value="净价总额",example="1")
    private BigDecimal priceAmt;

    @ApiModelProperty(name="unitInt",value="应计利息",example="1")
    private BigDecimal unitInt;

    @ApiModelProperty(name="unitIntAmt",value="应计利息总额",example="1")
    private BigDecimal unitIntAmt;

    @ApiModelProperty(name="unitFprice",value="全价",example="1")
    private BigDecimal unitFprice;

    @ApiModelProperty(name="unitFpriceAmt",value="全价总额",example="1")
    private BigDecimal unitFpriceAmt;

    @ApiModelProperty(name="entrustOwnInt",value="本方券应计利息总额",example="11")
    private BigDecimal entrustOwnInt;

    @ApiModelProperty(name="entrustIntegrateInt",value="卖空购回应计利息总额",example="11")
    private BigDecimal entrustIntegrateInt;

    @ApiModelProperty(name="entrustOwnUnitFpriceAmt",value="本方券全价总额",example="11")
    private BigDecimal entrustOwnUnitFpriceAmt;

    @ApiModelProperty(name="entrustIntegrateUnitFpriceAmt",value="卖空购回全价总额",example="11")
    private BigDecimal entrustIntegrateUnitFpriceAmt;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPriceAmt() {
        return priceAmt;
    }

    public void setPriceAmt(BigDecimal priceAmt) {
        this.priceAmt = priceAmt;
    }

    public BigDecimal getUnitInt() {
        return unitInt;
    }

    public void setUnitInt(BigDecimal unitInt) {
        this.unitInt = unitInt;
    }

    public BigDecimal getUnitIntAmt() {
        return unitIntAmt;
    }

    public void setUnitIntAmt(BigDecimal unitIntAmt) {
        this.unitIntAmt = unitIntAmt;
    }



    public BigDecimal getEntrustOwnInt() {
        return entrustOwnInt;
    }

    public void setEntrustOwnInt(BigDecimal entrustOwnInt) {
        this.entrustOwnInt = entrustOwnInt;
    }

    public BigDecimal getEntrustIntegrateInt() {
        return entrustIntegrateInt;
    }

    public void setEntrustIntegrateInt(BigDecimal entrustIntegrateInt) {
        this.entrustIntegrateInt = entrustIntegrateInt;
    }


    public BigDecimal getUnitFprice() {
        return unitFprice;
    }

    public void setUnitFprice(BigDecimal unitFprice) {
        this.unitFprice = unitFprice;
    }

    public BigDecimal getUnitFpriceAmt() {
        return unitFpriceAmt;
    }

    public void setUnitFpriceAmt(BigDecimal unitFpriceAmt) {
        this.unitFpriceAmt = unitFpriceAmt;
    }

    public BigDecimal getEntrustOwnUnitFpriceAmt() {
        return entrustOwnUnitFpriceAmt;
    }

    public void setEntrustOwnUnitFpriceAmt(BigDecimal entrustOwnUnitFpriceAmt) {
        this.entrustOwnUnitFpriceAmt = entrustOwnUnitFpriceAmt;
    }

    public BigDecimal getEntrustIntegrateUnitFpriceAmt() {
        return entrustIntegrateUnitFpriceAmt;
    }

    public void setEntrustIntegrateUnitFpriceAmt(BigDecimal entrustIntegrateUnitFpriceAmt) {
        this.entrustIntegrateUnitFpriceAmt = entrustIntegrateUnitFpriceAmt;
    }
}
