/*
 * 文件名：FinInterestFactorDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.business.asset.bond.dto;

import com.joyintech.framework.validation.annotation.Rate;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinCashInfo;
import com.joyintech.tams.common.base.entity.FinRateInfo;
import com.joyintech.tams.common.base.entity.PaFinCashCalcRule;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 计息要素信息
 * @author wangyang1
 * @version 1.0 2022年4月16日
 * @see BondFinInterestFactorDto
 * @since 1.0
 */
@ApiModel(value="计息要素",description="计息要素")
public class BondFinInterestFactorDto {
    
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

    /**
     * 金融产品代码，组装规则：金融产品类型（投管分类） _ 市场代码 _通道代码，如重复，则加流水后缀
     */

    private String finprodId;

    /**
     * 金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    private String finprodType;

    /**
     * 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    private String finprodType2;

    /**
     * 息票品种
     */
    @ApiModelProperty(name="couponSpecies",value="息票品种",required=true)
    private String couponSpecies;

    /**
     * 利率类型
     */
    @ApiModelProperty(name="calcType",value="利率类型(条件必填)")
    private String calcType;

    @ApiModelProperty(name="yield",value="参考收益率/初始利率(条件必填)")
    private BigDecimal yield;

    @ApiModelProperty(name="benchmarkIdAccuracy",value="基准利率精度(条件必填)")
    private BigDecimal benchmarkIdAccuracy;

    @ApiModelProperty(name="basicRate",value="基准利率(条件必填)")
    private String basicRate;

    @ApiModelProperty(name="coefficient",value="系数(条件必填)")
    private BigDecimal coefficient;

    @ApiModelProperty(name="spreadRate",value="利差(条件必填)")
    private BigDecimal spreadRate;

    @ApiModelProperty(name="highestRate",value="利率上限")
    private BigDecimal highestRate;

    @ApiModelProperty(name="lowestRate",value="利率下限")
    private BigDecimal lowestRate;

    @ApiModelProperty(name="resetType",value="基准利率调整规则(条件必填)")
    private String resetType;

    @ApiModelProperty(name="observeBefDay",value="延后时间(条件必填)")
    private BigDecimal observeBefDay;

    @ApiModelProperty(name="observeBefUnit",value="延后时间单位(条件必填)")
    private String observeBefUnit;

    @ApiModelProperty(name="firstConfirmDate",value="首次利率确定日(条件必填)")
    private Date firstConfirmDate;

    @ApiModelProperty(name="basis",value="发行人计息基础",example = "A/360，A/365",required=true)
    private String basis;

    @ApiModelProperty(name="marketBasis",value="交易市场计息基础",required=true)
    private String marketBasis;

    /**
     * 利息分配方式
     */
    @ApiModelProperty(name="intpayRule",value="利息分配方式(条件必填)")
    private String intpayRule;

    /**
     * 利息分配方式
     */
    private String basisStyle;
    
    /**
     * 借款利率
     */
    private BigDecimal loadRate;
    
    /**
     * 资产推荐费率
     */
    private BigDecimal referRate;
    
    /**
     * 买断利率
     */
    private BigDecimal buyOutRate;
    
    /**
     * 净投资收益率
     */
    private BigDecimal investRate;

    private BigDecimal Rate;
    /**
     * 现金流信息entity
     */
    private FinCashInfo finCashInfo;
    /**
     * 现金流利率信息entity
     */
    private FinRateInfo finRateInfo;
    
    /**
     * 利率信息list
     */
    private List<FinRateInfo> rateList;
    
    
    /**
     * 现金流计算规则entity
     */
    private FinCashCalcRule finCashCalcRule;
    
    /**
     * 计算规则list
     */
    private List<FinCashCalcRule> calcRuleList;
    
    /**
     * 现金流计算规则扩展
     */
    private PaFinCashCalcRule paFinCashCalcRule;

    /**
     *  计息利率方式
     */
    @ApiModelProperty(name="calRateType",value="计息利率方式 ")
    private String calRateType;

    /**
     * 获取 finprodId
     * @return finprodId.
     */
    public String getFinprodId() {
        return finprodId;
    }


    
    /**
     * 设置 finprodId
     * @param finprodId finprodId
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }


    
    /**
     * 获取 finprodType
     * @return finprodType.
     */
    public String getFinprodType() {
        return finprodType;
    }


    
    /**
     * 设置 finprodType
     * @param finprodType finprodType
     */
    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType;
    }


    
    /**
     * 获取 finprodType2
     * @return finprodType2.
     */
    public String getFinprodType2() {
        return finprodType2;
    }


    
    /**
     * 设置 finprodType2
     * @param finprodType2 finprodType2
     */
    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    
    /**
     * 获取 finRateInfo
     * @return finRateInfo.
     */
    public FinRateInfo getFinRateInfo() {
        return finRateInfo;
    }



    
    /**
     * 设置 finRateInfo
     * @param finRateInfo finRateInfo
     */
    public void setFinRateInfo(FinRateInfo finRateInfo) {
        this.finRateInfo = finRateInfo;
    }



    
    /**
     * 获取 finCashCalcRule
     * @return finCashCalcRule.
     */
    public FinCashCalcRule getFinCashCalcRule() {
        return finCashCalcRule;
    }



    
    /**
     * 设置 finCashCalcRule
     * @param finCashCalcRule finCashCalcRule
     */
    public void setFinCashCalcRule(FinCashCalcRule finCashCalcRule) {
        this.finCashCalcRule = finCashCalcRule;
    }



    
    /**
     * 获取 finCashInfo
     * @return finCashInfo.
     */
    public FinCashInfo getFinCashInfo() {
        return finCashInfo;
    }



    
    /**
     * 设置 finCashInfo
     * @param finCashInfo finCashInfo
     */
    public void setFinCashInfo(FinCashInfo finCashInfo) {
        this.finCashInfo = finCashInfo;
    }



    
    /**
     * 获取 loadRate
     * @return loadRate.
     */
    @Rate(fieldName = "FinInterestFactor.loadRate", maxIntBit = 30,maxDecBit=14,groups={ValidateGroupEdit.class})
    public BigDecimal getLoadRate() {
        return loadRate;
    }



    
    /**
     * 设置 loadRate
     * @param loadRate loadRate
     */
    public void setLoadRate(BigDecimal loadRate) {
        this.loadRate = loadRate;
    }



    
    /**
     * 获取 referRate
     * @return referRate.
     */
    public BigDecimal getReferRate() {
        return referRate;
    }



    
    /**
     * 设置 referRate
     * @param referRate referRate
     */
    public void setReferRate(BigDecimal referRate) {
        this.referRate = referRate;
    }



    
    /**
     * 获取 buyOutRate
     * @return buyOutRate.
     */
    public BigDecimal getBuyOutRate() {
        return buyOutRate;
    }



    
    /**
     * 设置 buyOutRate
     * @param buyOutRate buyOutRate
     */
    public void setBuyOutRate(BigDecimal buyOutRate) {
        this.buyOutRate = buyOutRate;
    }

    /**
     * 获取 basisStyle
     * @return basisStyle.
     */
    public String getBasisStyle() {
        return basisStyle;
    }



    
    /**
     * 设置 basisStyle
     * @param basisStyle basisStyle
     */
    public void setBasisStyle(String basisStyle) {
        this.basisStyle = basisStyle;
    }

    
    /**
     * 获取 paFinCashCalcRule
     * @return paFinCashCalcRule.
     */
    public PaFinCashCalcRule getPaFinCashCalcRule() {
        return paFinCashCalcRule;
    }
    
    /**
     * 设置 paFinCashCalcRule
     * @param paFinCashCalcRule paFinCashCalcRule
     */
    public void setPaFinCashCalcRule(PaFinCashCalcRule paFinCashCalcRule) {
        this.paFinCashCalcRule = paFinCashCalcRule;
    }

    /**
     * 设置 investRate
     * @param investRate investRate
     */
    public void setInvestRate(BigDecimal investRate) {
        this.investRate = investRate;
    }



    
    /**
     * 获取 couponSpecies
     * @return couponSpecies.
     */
    public String getCouponSpecies() {
        return couponSpecies;
    }



    
    /**
     * 设置 couponSpecies
     * @param couponSpecies couponSpecies
     */
    public void setCouponSpecies(String couponSpecies) {
        this.couponSpecies = couponSpecies;
    }



    
    /**
     * 获取 rateList
     * @return rateList.
     */
    public List<FinRateInfo> getRateList() {
        return rateList;
    }



    
    /**
     * 设置 rateList
     * @param rateList rateList
     */
    public void setRateList(List<FinRateInfo> rateList) {
        this.rateList = rateList;
    }

    /**
     * 获取 calcRuleList
     * @return calcRuleList.
     */
    public List<FinCashCalcRule> getCalcRuleList() {
        return calcRuleList;
    }

    /**
     * 设置 calcRuleList
     * @param calcRuleList calcRuleList
     */
    public void setCalcRuleList(List<FinCashCalcRule> calcRuleList) {
        this.calcRuleList = calcRuleList;
    }

    public String getCalRateType() {
        return calRateType;
    }

    public void setCalRateType(String calRateType) {
        this.calRateType = calRateType;
    }


    public BigDecimal getYield() {
        return yield;
    }

    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

    public String getCalcType() {
        return calcType;
    }

    public void setCalcType(String calcType) {
        this.calcType = calcType;
    }

    public BigDecimal getBenchmarkIdAccuracy() {
        return benchmarkIdAccuracy;
    }

    public void setBenchmarkIdAccuracy(BigDecimal benchmarkIdAccuracy) {
        this.benchmarkIdAccuracy = benchmarkIdAccuracy;
    }

    public String getBasicRate() {
        return basicRate;
    }

    public void setBasicRate(String basicRate) {
        this.basicRate = basicRate;
    }

    public BigDecimal getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(BigDecimal coefficient) {
        this.coefficient = coefficient;
    }

    public BigDecimal getSpreadRate() {
        return spreadRate;
    }

    public void setSpreadRate(BigDecimal spreadRate) {
        this.spreadRate = spreadRate;
    }

    public BigDecimal getHighestRate() {
        return highestRate;
    }

    public void setHighestRate(BigDecimal highestRate) {
        this.highestRate = highestRate;
    }

    public BigDecimal getLowestRate() {
        return lowestRate;
    }

    public void setLowestRate(BigDecimal lowestRate) {
        this.lowestRate = lowestRate;
    }

    public String getResetType() {
        return resetType;
    }

    public void setResetType(String resetType) {
        this.resetType = resetType;
    }

    public BigDecimal getObserveBefDay() {
        return observeBefDay;
    }

    public void setObserveBefDay(BigDecimal observeBefDay) {
        this.observeBefDay = observeBefDay;
    }

    public String getObserveBefUnit() {
        return observeBefUnit;
    }

    public void setObserveBefUnit(String observeBefUnit) {
        this.observeBefUnit = observeBefUnit;
    }

    public Date getFirstConfirmDate() {
        return firstConfirmDate;
    }

    public void setFirstConfirmDate(Date firstConfirmDate) {
        this.firstConfirmDate = firstConfirmDate;
    }

    public String getBasis() {
        return basis;
    }

    public void setBasis(String basis) {
        this.basis = basis;
    }

    public String getMarketBasis() {
        return marketBasis;
    }

    public void setMarketBasis(String marketBasis) {
        this.marketBasis = marketBasis;
    }

    public String getIntpayRule() {
        return intpayRule;
    }

    public void setIntpayRule(String intpayRule) {
        this.intpayRule = intpayRule;
    }

    public BigDecimal getInvestRate() {
        return investRate;
    }

    public BigDecimal getRate() {
        return Rate;
    }

    public void setRate(BigDecimal rate) {
        Rate = rate;
    }
}
