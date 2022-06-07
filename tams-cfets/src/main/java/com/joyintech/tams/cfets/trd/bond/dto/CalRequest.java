package com.joyintech.tams.cfets.trd.bond.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zznn
 * @date 2021/11/12
 */
@ApiModel(value="计算联动请求参数",description="计算联动请求参数")
public class CalRequest {

    @ApiModelProperty(required = true,name="calculateField",value="计算类型")
    private String calculateField;

    @ApiModelProperty(required = true,name="finprodId",value="金融产品代码")
    private String finprodId;

    @ApiModelProperty(required = true,name="shareAmt",value="券面总额")
    private BigDecimal shareAmt;

    @ApiModelProperty(required = true,name="settleDate",value="结算日期")
    private Date settleDate;

    @ApiModelProperty(name="price",value="净价")
    private BigDecimal price;

    @ApiModelProperty(required = true,name="fpriceAmt",value="全价总额")
    private BigDecimal fpriceAmt;

    @ApiModelProperty(name="matureYield",value="到期收益率(%)")
    private BigDecimal matureYield;

    @ApiModelProperty(name="exerYield",value="行权收益率(%)")
    private BigDecimal exerYield;

    @ApiModelProperty(required = true,name="ps",value="交易方向")
    private String ps;

    @ApiModelProperty(required = true,name="secManageAcctId",value="证券账户代码")
    private String secManageAcctId;

    @ApiModelProperty(name="unitInt",value="应计利息")
    private BigDecimal unitInt;

    @ApiModelProperty(name="cpriceAmt",value="净价总额")
    private BigDecimal cpriceAmt;


    public String getCalculateField() {
        return calculateField;
    }

    public void setCalculateField(String calculateField) {
        this.calculateField = calculateField;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

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

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    public BigDecimal getUnitInt() {
        return unitInt;
    }

    public void setUnitInt(BigDecimal unitInt) {
        this.unitInt = unitInt;
    }

    public BigDecimal getCpriceAmt() {
        return cpriceAmt;
    }

    public void setCpriceAmt(BigDecimal cpriceAmt) {
        this.cpriceAmt = cpriceAmt;
    }
}

