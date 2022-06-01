package com.joyintech.tams.cfets.trd.primarybidmanage.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 *
 * 指令下拉框模糊查询
 */
@ApiModel(value="新建指令金额计算功能返回参数",description="新建指令金额计算功能返回参数")
public class CfetsBidManageCalcAmountResponse {


    @ApiModelProperty(name="winCprice",value="中标净价")
    private BigDecimal winCprice;

    @ApiModelProperty(name="intAmt",value="应计利息")
    private BigDecimal intAmt;

    @ApiModelProperty(name="payAmt",value="缴款金额")
    private BigDecimal payAmt;

    @ApiModelProperty(name="entrustOwnInt",value="应计利息总额")
    private BigDecimal entrustOwnInt;

    @ApiModelProperty(name="unitFpriceAmt",value="全价总额")
    private BigDecimal unitFpriceAmt;

    @ApiModelProperty(name="tradeAmt",value="交易金额")
    private BigDecimal tradeAmt;

    public BigDecimal getWinCprice() {
        return winCprice;
    }

    public void setWinCprice(BigDecimal winCprice) {
        this.winCprice = winCprice;
    }

    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    public BigDecimal getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(BigDecimal payAmt) {
        this.payAmt = payAmt;
    }

    public BigDecimal getEntrustOwnInt() {
        return entrustOwnInt;
    }

    public void setEntrustOwnInt(BigDecimal entrustOwnInt) {
        this.entrustOwnInt = entrustOwnInt;
    }

    public BigDecimal getUnitFpriceAmt() {
        return unitFpriceAmt;
    }

    public void setUnitFpriceAmt(BigDecimal unitFpriceAmt) {
        this.unitFpriceAmt = unitFpriceAmt;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }
}
