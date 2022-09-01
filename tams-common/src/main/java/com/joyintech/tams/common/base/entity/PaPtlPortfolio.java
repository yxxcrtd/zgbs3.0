package com.joyintech.tams.common.base.entity;

import com.joyintech.framework.base.BaseEntity;

public class PaPtlPortfolio extends BaseEntity {
    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 是否保本，保本、非保本
     */
    private String isGuarantee;

    /**
     * 开放类型，开放式、封闭式
     */
    private String openType;

    /**
     * 募集方式，公募、私募
     */
    private String raiseType;

    /**
     * 投资性质，固定收益类、权益类、商品及金融衍生品类、混合类
     */
    private String investNature;

    /**
     * 组合系列
     */
    private String portfolioSeries;

    /**
     * 自主管理计划
     */
    private String selfManagement;

    /**
     * 取得 组合代码
     * @return 组合代码
     */
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * 设置 组合代码
     * @param portfolioId 组合代码
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId == null ? null : portfolioId.trim();
    }

    /**
     * 取得 是否保本，保本、非保本
     * @return 是否保本，保本、非保本
     */
    public String getIsGuarantee() {
        return isGuarantee;
    }

    /**
     * 设置 是否保本，保本、非保本
     * @param isGuarantee 是否保本，保本、非保本
     */
    public void setIsGuarantee(String isGuarantee) {
        this.isGuarantee = isGuarantee == null ? null : isGuarantee.trim();
    }

    /**
     * 取得 开放类型，开放式、封闭式
     * @return 开放类型，开放式、封闭式
     */
    public String getOpenType() {
        return openType;
    }

    /**
     * 设置 开放类型，开放式、封闭式
     * @param openType 开放类型，开放式、封闭式
     */
    public void setOpenType(String openType) {
        this.openType = openType == null ? null : openType.trim();
    }

    /**
     * 取得 募集方式，公募、私募
     * @return 募集方式，公募、私募
     */
    public String getRaiseType() {
        return raiseType;
    }

    /**
     * 设置 募集方式，公募、私募
     * @param raiseType 募集方式，公募、私募
     */
    public void setRaiseType(String raiseType) {
        this.raiseType = raiseType == null ? null : raiseType.trim();
    }

    /**
     * 取得 投资性质，固定收益类、权益类、商品及金融衍生品类、混合类
     * @return 投资性质，固定收益类、权益类、商品及金融衍生品类、混合类
     */
    public String getInvestNature() {
        return investNature;
    }

    /**
     * 设置 投资性质，固定收益类、权益类、商品及金融衍生品类、混合类
     * @param investNature 投资性质，固定收益类、权益类、商品及金融衍生品类、混合类
     */
    public void setInvestNature(String investNature) {
        this.investNature = investNature == null ? null : investNature.trim();
    }

    /**
     * 获取 组合系列
     * @return 组合系列
     */
    public String getPortfolioSeries() {
        return portfolioSeries;
    }

    /**
     * 设置 组合系列
     * @param portfolioSeries 组合系列
     */
    public void setPortfolioSeries(String portfolioSeries) {
        this.portfolioSeries = portfolioSeries;
    }

    public String getSelfManagement() {
        return selfManagement;
    }

    public void setSelfManagement(String selfManagement) {
        this.selfManagement = selfManagement;
    }
}