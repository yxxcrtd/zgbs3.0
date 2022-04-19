package com.joyintech.tams.exchange.common.bean;

import com.google.common.base.MoreObjects;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@ApiModel(value="买卖单位及交易上下限控制信息",description="买卖单位及交易上下限控制信息")
public class MinTrdUnitLimit implements Serializable {
    private static final long serialVersionUID = 4144411393550111397L;

    @ApiModelProperty(required = false, name = "tradeMarket", value = "交易场所")
    private String tradeMarket;

    @ApiModelProperty(required = false, name = "tradePlat", value = "交易平台")
    private String tradePlat;

    @ApiModelProperty(required = false, name = "assetType", value = "资产一级分类")
    private String assetType;

    @ApiModelProperty(required = false, name = "assetType1", value = "资产二级分类")
    private String assetType1;

    @ApiModelProperty(required = false, name = "assetType2", value = "资产三级分类")
    private String assetType2;

    @ApiModelProperty(required = false, name = "ps", value = "交易方向")
    private String ps;

    @ApiModelProperty(required = false, name = "marketType", value = "上市板")
    private String marketType;

    @ApiModelProperty(required = false, name = "minStep", value = "最小交易单元")
    private Integer minStep = 0;

    @ApiModelProperty(required = false, name = "minStepUnit", value = "最小交易单元单位")
    private String minStepUnit;

    @ApiModelProperty(required = false, name = "minAmt", value = "最低交易金额")
    private BigDecimal minAmt = new BigDecimal("0.00");

    @ApiModelProperty(required = false, name = "maxAmt", value = "最大交易金额")
    private BigDecimal maxAmt = new BigDecimal("0.00");

    @ApiModelProperty(required = false, name = "minNum", value = "最低交易数量")
    private Integer minNum = 0;

    @ApiModelProperty(required = false, name = "minNumUnit", value = "最低交易数量单位")
    private String minNumUnit;

    @ApiModelProperty(required = false, name = "maxNum", value = "最大交易数量")
    private Integer maxNum = 0;

    @ApiModelProperty(required = false, name = "maxNumUnit", value = "最大交易数量单位")
    private String maxNumUnit;

    @ApiModelProperty(required = false, name = "minNumMarket", value = "最低交易数量（市价）")
    private Integer minNumMarket = 0;

    @ApiModelProperty(required = false, name = "minNumMarketUnit", value = "最低交易数量单位（市价）")
    private String minNumMarketUnit;

    @ApiModelProperty(required = false, name = "maxNumMarket", value = "最大交易数量（市价）")
    private Integer maxNumMarket = 0;

    @ApiModelProperty(required = false, name = "maxNumMarketUnit", value = "最大交易数量单位（市价）")
    private String maxNumMarketUnit;

    @ApiModelProperty(required = false, name = "effDate", value = "生效日")
    private Date effDate;

    @ApiModelProperty(required = false, name = "market", value = "市场")
    private String market;

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getTradePlat() {
        return tradePlat;
    }

    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getAssetType1() {
        return assetType1;
    }

    public void setAssetType1(String assetType1) {
        this.assetType1 = assetType1;
    }

    public String getAssetType2() {
        return assetType2;
    }

    public void setAssetType2(String assetType2) {
        this.assetType2 = assetType2;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getMarketType() {
        return marketType;
    }

    public void setMarketType(String marketType) {
        this.marketType = marketType;
    }

    public Integer getMinStep() {
        return minStep;
    }

    public void setMinStep(Integer minStep) {
        this.minStep = minStep;
    }

    public String getMinStepUnit() {
        return minStepUnit;
    }

    public void setMinStepUnit(String minStepUnit) {
        this.minStepUnit = minStepUnit;
    }

    public BigDecimal getMinAmt() {
        return minAmt;
    }

    public void setMinAmt(BigDecimal minAmt) {
        this.minAmt = minAmt;
    }

    public BigDecimal getMaxAmt() {
        return maxAmt;
    }

    public void setMaxAmt(BigDecimal maxAmt) {
        this.maxAmt = maxAmt;
    }

    public Integer getMinNum() {
        return minNum;
    }

    public void setMinNum(Integer minNum) {
        this.minNum = minNum;
    }

    public String getMinNumUnit() {
        return minNumUnit;
    }

    public void setMinNumUnit(String minNumUnit) {
        this.minNumUnit = minNumUnit;
    }

    public Integer getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(Integer maxNum) {
        this.maxNum = maxNum;
    }

    public String getMaxNumUnit() {
        return maxNumUnit;
    }

    public void setMaxNumUnit(String maxNumUnit) {
        this.maxNumUnit = maxNumUnit;
    }

    public Integer getMinNumMarket() {
        return minNumMarket;
    }

    public void setMinNumMarket(Integer minNumMarket) {
        this.minNumMarket = minNumMarket;
    }

    public String getMinNumMarketUnit() {
        return minNumMarketUnit;
    }

    public void setMinNumMarketUnit(String minNumMarketUnit) {
        this.minNumMarketUnit = minNumMarketUnit;
    }

    public Integer getMaxNumMarket() {
        return maxNumMarket;
    }

    public void setMaxNumMarket(Integer maxNumMarket) {
        this.maxNumMarket = maxNumMarket;
    }

    public String getMaxNumMarketUnit() {
        return maxNumMarketUnit;
    }

    public void setMaxNumMarketUnit(String maxNumMarketUnit) {
        this.maxNumMarketUnit = maxNumMarketUnit;
    }

    public Date getEffDate() {
        return effDate;
    }

    public void setEffDate(Date effDate) {
        this.effDate = effDate;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("tradeMarket", tradeMarket)
                .add("tradePlat", tradePlat)
                .add("assetType", assetType)
                .add("assetType1", assetType1)
                .add("assetType2", assetType2)
                .add("ps", ps)
                .add("marketType", marketType)
                .add("minStep", minStep)
                .add("minStepUnit", minStepUnit)
                .add("minAmt", minAmt)
                .add("maxAmt", maxAmt)
                .add("minNum", minNum)
                .add("minNumUnit", minNumUnit)
                .add("maxNum", maxNum)
                .add("maxNumUnit", maxNumUnit)
                .add("minNumMarket", minNumMarket)
                .add("minNumMarketUnit", minNumMarketUnit)
                .add("maxNumMarket", maxNumMarket)
                .add("maxNumMarketUnit", maxNumMarketUnit)
                .add("effDate", effDate)
                .add("market", market)
                .toString();
    }
}
