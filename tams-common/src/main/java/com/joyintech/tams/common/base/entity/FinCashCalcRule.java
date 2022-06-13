package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import java.math.BigDecimal;

public class FinCashCalcRule extends FinCashCalcRuleKey {
    /**
     * 计算类型，固定利率、浮动利率
     */
    private String calcType;

    /**
     * 基数类型，本金、资产净值、实收资本等
     */
    private String baseType;

    /**
     * 基数日期类型，上一工作日、上一自然日、当日、上一开放日等
     */
    private String baseDateType;

    /**
     * 计息基础，A/360，A/365等
     */
    private String basis;

    /**
     * 生效利率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal yield;

    /**
     * 是否初始利率
     */
    private String isInitial;

    /**
     * 金融产品代码
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
     * 分支序号
     */
    private Long branch;

    /**
     * 备注
     */
    private String remark;

    /**
     * 交易市场计息基础
     */
    private String marketBasis;

    /**
     * 取得 计算类型，固定利率、浮动利率
     * @return 计算类型，固定利率、浮动利率
     */
    public String getCalcType() {
        return calcType;
    }

    /**
     * 设置 计算类型，固定利率、浮动利率
     * @param calcType 计算类型，固定利率、浮动利率
     */
    public void setCalcType(String calcType) {
        this.calcType = calcType == null ? null : calcType.trim();
    }

    /**
     * 取得 基数类型，本金、资产净值、实收资本等
     * @return 基数类型，本金、资产净值、实收资本等
     */
    public String getBaseType() {
        return baseType;
    }

    /**
     * 设置 基数类型，本金、资产净值、实收资本等
     * @param baseType 基数类型，本金、资产净值、实收资本等
     */
    public void setBaseType(String baseType) {
        this.baseType = baseType == null ? null : baseType.trim();
    }

    /**
     * 取得 基数日期类型，上一工作日、上一自然日、当日、上一开放日等
     * @return 基数日期类型，上一工作日、上一自然日、当日、上一开放日等
     */
    public String getBaseDateType() {
        return baseDateType;
    }

    /**
     * 设置 基数日期类型，上一工作日、上一自然日、当日、上一开放日等
     * @param baseDateType 基数日期类型，上一工作日、上一自然日、当日、上一开放日等
     */
    public void setBaseDateType(String baseDateType) {
        this.baseDateType = baseDateType == null ? null : baseDateType.trim();
    }

    /**
     * 取得 计息基础，A/360，A/365等
     * @return 计息基础，A/360，A/365等
     */
    public String getBasis() {
        return basis;
    }

    /**
     * 设置 计息基础，A/360，A/365等
     * @param basis 计息基础，A/360，A/365等
     */
    public void setBasis(String basis) {
        this.basis = basis == null ? null : basis.trim();
    }

    /**
     * 取得 生效利率
     * @return 生效利率
     */
    public BigDecimal getYield() {
        return yield;
    }

    /**
     * 设置 生效利率
     * @param yield 生效利率
     */
    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

    /**
     * 取得 是否初始利率
     * @return 是否初始利率
     */
    public String getIsInitial() {
        return isInitial;
    }

    /**
     * 设置 是否初始利率
     * @param isInitial 是否初始利率
     */
    public void setIsInitial(String isInitial) {
        this.isInitial = isInitial == null ? null : isInitial.trim();
    }

    /**
     * 取得 金融产品代码
     * @return 金融产品代码
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 设置 金融产品代码
     * @param finprodId 金融产品代码
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId == null ? null : finprodId.trim();
    }

    /**
     * 取得 金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
     * @return 金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    public String getFinprodType() {
        return finprodType;
    }

    /**
     * 设置 金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
     * @param finprodType 金融产品类型（估值核算），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType == null ? null : finprodType.trim();
    }

    /**
     * 取得 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     * @return 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    public String getFinprodType2() {
        return finprodType2;
    }

    /**
     * 设置 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     * @param finprodType2 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2 == null ? null : finprodType2.trim();
    }

    /**
     * 取得 分支序号
     * @return 分支序号
     */
    public Long getBranch() {
        return branch;
    }

    /**
     * 设置 分支序号
     * @param branch 分支序号
     */
    public void setBranch(Long branch) {
        this.branch = branch;
    }

    /**
     * 取得 备注
     * @return 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置 备注
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }


    /**
     * 获取 交易市场计息基础
     *
     * @return marketBasis 交易市场计息基础
     */
    public String getMarketBasis() {
        return this.marketBasis;
    }

    /**
     * 设置 交易市场计息基础
     *
     * @param marketBasis 交易市场计息基础
     */
    public void setMarketBasis(String marketBasis) {
        this.marketBasis = marketBasis;
    }
}