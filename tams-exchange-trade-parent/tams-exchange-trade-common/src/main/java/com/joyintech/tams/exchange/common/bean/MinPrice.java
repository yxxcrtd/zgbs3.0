package com.joyintech.tams.exchange.common.bean;

import com.google.common.base.MoreObjects;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@ApiModel(value="最小价差控制信息",description="最小价差控制信息")
public class MinPrice implements Serializable {
    private static final long serialVersionUID = 5477488588173744320L;

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

    @ApiModelProperty(required = false, name = "isGrade", value = "是否分档")
    private String isGrade;

    @ApiModelProperty(required = false, name = "gradeCondition", value = "分档条件")
    private String gradeCondition;

    @ApiModelProperty(required = false, name = "gradeAmt", value = "分档金额")
    private Integer gradeAmt = 0;

    @SuppressWarnings("all")
    @ApiModelProperty(required = false, name = "minPrice", value = "最小价差")
    private BigDecimal minPrice = new BigDecimal("0.0001");

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

    public String getIsGrade() {
        return isGrade;
    }

    public void setIsGrade(String isGrade) {
        this.isGrade = isGrade;
    }

    public String getGradeCondition() {
        return gradeCondition;
    }

    public void setGradeCondition(String gradeCondition) {
        this.gradeCondition = gradeCondition;
    }

    public Integer getGradeAmt() {
        return gradeAmt;
    }

    public void setGradeAmt(Integer gradeAmt) {
        this.gradeAmt = gradeAmt;
    }

    public BigDecimal getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(BigDecimal minPrice) {
        this.minPrice = minPrice;
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
                .add("isGrade", isGrade)
                .add("gradeCondition", gradeCondition)
                .add("gradeAmt", gradeAmt)
                .add("minPrice", minPrice)
                .add("effDate", effDate)
                .add("market", market)
                .toString();
    }
}
