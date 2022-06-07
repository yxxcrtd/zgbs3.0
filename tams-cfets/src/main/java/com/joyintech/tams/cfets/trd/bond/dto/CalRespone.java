package com.joyintech.tams.cfets.trd.bond.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zznn
 * @date 2021/11/12
 */
@ApiModel(value="计算联动返回数据",description="计算联动返回数据")
public class CalRespone {

    @ApiModelProperty(name="shareAmt",value="券面总额")
    private BigDecimal shareAmt;

    @ApiModelProperty(name="settleDate",value="结算日期")
    private Date settleDate;

    @ApiModelProperty(name="price",value="净价")
    private BigDecimal price;

    @ApiModelProperty(name="fpriceAmt",value="全价总额")
    private BigDecimal fpriceAmt;

    @ApiModelProperty(name="matureYield",value="到期收益率(%)")
    private BigDecimal matureYield;

    @ApiModelProperty(name="exerYield",value="行权收益率(%)")
    private BigDecimal exerYield;

    @ApiModelProperty(name="cpriceAmt",value="净价总额")
    private BigDecimal cpriceAmt;

    @ApiModelProperty(name="unitInt",value="应计利息")
    private BigDecimal unitInt;

    @ApiModelProperty(name="unitFprice",value="全价")
    private BigDecimal unitFprice;

    @ApiModelProperty(name="intAmt",value="应计利息总额")
    private BigDecimal intAmt;

    @ApiModelProperty(name="dealFeeAmt",value="交易手续费(元)")
    private BigDecimal dealFeeAmt;

    @ApiModelProperty(name="settFeeAmt",value="结算服务费(元)")
    private BigDecimal settFeeAmt;

    @ApiModelProperty(name="parValue",value="每百元本金额(元)")
    private BigDecimal parValue;

    @ApiModelProperty(name="parValueAmt",value="本金额(元)")
    private BigDecimal parValueAmt;

    @ApiModelProperty(name="deviationDegree",value="偏离度")
    private String deviationDegree;

    @ApiModelProperty(name="assetType11",value="资产四级分类")
    private String assetType11;

    public BigDecimal getShareAmt() {
        return shareAmt;
    }

    public void setShareAmt(BigDecimal shareAmt) {
        this.shareAmt = shareAmt;
    }

    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getFpriceAmt() {
        return fpriceAmt;
    }

    public void setFpriceAmt(BigDecimal fpriceAmt) {
        this.fpriceAmt = fpriceAmt;
    }

    public BigDecimal getMatureYield() {
        return matureYield;
    }

    public void setMatureYield(BigDecimal matureYield) {
        this.matureYield = matureYield;
    }

    public BigDecimal getExerYield() {
        return exerYield;
    }

    public void setExerYield(BigDecimal exerYield) {
        this.exerYield = exerYield;
    }

    public BigDecimal getCpriceAmt() {
        return cpriceAmt;
    }

    public void setCpriceAmt(BigDecimal cpriceAmt) {
        this.cpriceAmt = cpriceAmt;
    }

    public BigDecimal getUnitInt() {
        return unitInt;
    }

    public void setUnitInt(BigDecimal unitInt) {
        this.unitInt = unitInt;
    }

    public BigDecimal getUnitFprice() {
        return unitFprice;
    }

    public void setUnitFprice(BigDecimal unitFprice) {
        this.unitFprice = unitFprice;
    }

    public BigDecimal getIntAmt() {
        return intAmt;
    }

    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    public BigDecimal getDealFeeAmt() {
        return dealFeeAmt;
    }

    public void setDealFeeAmt(BigDecimal dealFeeAmt) {
        this.dealFeeAmt = dealFeeAmt;
    }

    public BigDecimal getSettFeeAmt() {
        return settFeeAmt;
    }

    public void setSettFeeAmt(BigDecimal settFeeAmt) {
        this.settFeeAmt = settFeeAmt;
    }

    public String getDeviationDegree() {
        return deviationDegree;
    }

    public void setDeviationDegree(String deviationDegree) {
        this.deviationDegree = deviationDegree;
    }

    public BigDecimal getParValue() {
        return parValue;
    }

    public void setParValue(BigDecimal parValue) {
        this.parValue = parValue;
    }

    public BigDecimal getParValueAmt() {
        return parValueAmt;
    }

    public void setParValueAmt(BigDecimal parValueAmt) {
        this.parValueAmt = parValueAmt;
    }

    public String getAssetType11() {
        return assetType11;
    }

    public void setAssetType11(String assetType11) {
        this.assetType11 = assetType11;
    }
}

