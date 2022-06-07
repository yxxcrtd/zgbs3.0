package com.joyintech.tams.cfets.etr.bond.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 *
 * 指令下拉框模糊查询
 */
@ApiModel(value="新建指令金额计算功能请求参数",description="新建指令金额计算功能请求参数")
public class CfetsEtrCalcAmountRequest {

    @ApiModelProperty(name="shareNum",value="券面总额",example="10000")
    private BigDecimal shareNum;

    @ApiModelProperty(name="entrustOwnNum",value="本方券券面总额",example="10000")
    private BigDecimal entrustOwnNum;

    @ApiModelProperty(name="entrustIntegrateNum",value="融入券/卖空购回券面总额",example="0")
    private BigDecimal entrustIntegrateNum;

    @ApiModelProperty(name="price",value="净价",example="99.99")
    private BigDecimal price;

    @ApiModelProperty(name="unitFprice",value="全价",example="101")
    private BigDecimal unitFprice;

    public BigDecimal getShareNum() {
        return shareNum;
    }

    public void setShareNum(BigDecimal shareNum) {
        this.shareNum = shareNum;
    }

    public BigDecimal getEntrustOwnNum() {
        return entrustOwnNum;
    }

    public void setEntrustOwnNum(BigDecimal entrustOwnNum) {
        this.entrustOwnNum = entrustOwnNum;
    }

    public BigDecimal getEntrustIntegrateNum() {
        return entrustIntegrateNum;
    }

    public void setEntrustIntegrateNum(BigDecimal entrustIntegrateNum) {
        this.entrustIntegrateNum = entrustIntegrateNum;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }


    public BigDecimal getUnitFprice() {
        return unitFprice;
    }

    public void setUnitFprice(BigDecimal unitFprice) {
        this.unitFprice = unitFprice;
    }
}
