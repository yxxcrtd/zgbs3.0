package com.joyintech.tams.invest.common.trade.dto;

import com.joyintech.framework.base.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Classname MinTrdUnitLimitDto
 * @Description TODO
 * @author zhuhanlu 
 * @date 2020/10/13 19:43 
 * @version 1.0 
 */
public class MinTrdUnitLimitDto extends BaseEntity {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String tradeMarket; //交易场所
    private String  tradePlat; //交易平台
    private String assetType; //一级分类
    private String assetType1; //二级分类
    private String assetType2; //三级分类
    private String ps; //交易方向
    private String marketType; //上市板
    private BigDecimal minNum; //最小交易数量
    private String minNumUnit; //最小交易数量单位
    private BigDecimal minAmt; //最小交易金额
    private BigDecimal maxNum; //最大交易数量
    private String maxNumUnit; //最大交易数量单位
    private BigDecimal maxAmt; //最大交易金额
    private BigDecimal minStep; //最小交易单元
    private String minStepUnit; //最小交易单元单位
    private BigDecimal minNumMarket; //最小交易数量（市价）
    private String minNumMarketUnit; //最小交易数量单位（市价）
    private BigDecimal maxNumMarket; //最大交易数量（市价）
    private String maxNumMarketUnit; //最大交易数量单位（市价）
    private Date effDate; //生效日

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

    public BigDecimal getMinNum() {
        return minNum;
    }

    public void setMinNum(BigDecimal minNum) {
        this.minNum = minNum;
    }

    public String getMinNumUnit() {
        return minNumUnit;
    }

    public void setMinNumUnit(String minNumUnit) {
        this.minNumUnit = minNumUnit;
    }

    public BigDecimal getMinAmt() {
        return minAmt;
    }

    public void setMinAmt(BigDecimal minAmt) {
        this.minAmt = minAmt;
    }

    public BigDecimal getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(BigDecimal maxNum) {
        this.maxNum = maxNum;
    }

    public String getMaxNumUnit() {
        return maxNumUnit;
    }

    public void setMaxNumUnit(String maxNumUnit) {
        this.maxNumUnit = maxNumUnit;
    }

    public BigDecimal getMaxAmt() {
        return maxAmt;
    }

    public void setMaxAmt(BigDecimal maxAmt) {
        this.maxAmt = maxAmt;
    }

    public BigDecimal getMinStep() {
        return minStep;
    }

    public void setMinStep(BigDecimal minStep) {
        this.minStep = minStep;
    }

    public String getMinStepUnit() {
        return minStepUnit;
    }

    public void setMinStepUnit(String minStepUnit) {
        this.minStepUnit = minStepUnit;
    }

    public BigDecimal getMinNumMarket() {
        return minNumMarket;
    }

    public void setMinNumMarket(BigDecimal minNumMarket) {
        this.minNumMarket = minNumMarket;
    }

    public String getMinNumMarketUnit() {
        return minNumMarketUnit;
    }

    public void setMinNumMarketUnit(String minNumMarketUnit) {
        this.minNumMarketUnit = minNumMarketUnit;
    }

    public BigDecimal getMaxNumMarket() {
        return maxNumMarket;
    }

    public void setMaxNumMarket(BigDecimal maxNumMarket) {
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
}
