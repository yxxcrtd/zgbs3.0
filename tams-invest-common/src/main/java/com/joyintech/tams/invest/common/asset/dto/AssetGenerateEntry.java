package com.joyintech.tams.invest.common.asset.dto;

import com.joyintech.tams.common.base.entity.FinProduct;

/**
 * @author 裴家俊
 * @version 1.0 2021/7/26
 * @see AssetGenerateEntry
 * @since 1.0
 **/
public class AssetGenerateEntry extends FinProduct {
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;

    /**
     * 面值
     */
    private String faceValue;

    /**
     * 行内四级类型
     */
    private String type11;

    /**
     * 行内五级类型
     */
    private String type12;

    /**
     * 交易编号
     */
    private String tradeId;

    /**
     * 是否锁定
     */
    private String withLockPeriod;

    /**
     * 产品处理模式
     */
    private String dealMode;

    ///**
    // * 产品类型
    // */
    //private String prodType;
    //
    ///**
    // * 产品最小持有期（天）
    // */
    //private String minProductHold;

    /**
     * 期限
     */
    private String termType;

    /**
     * 投资周期
     */
    private String investmentCycle;

    public String getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(String faceValue) {
        this.faceValue = faceValue;
    }

    public String getType11() {
        return type11;
    }

    public void setType11(String type11) {
        this.type11 = type11;
    }

    public String getType12() {
        return type12;
    }

    public void setType12(String type12) {
        this.type12 = type12;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getWithLockPeriod() {
        return withLockPeriod;
    }

    public void setWithLockPeriod(String withLockPeriod) {
        this.withLockPeriod = withLockPeriod;
    }

    public String getDealMode() {
        return dealMode;
    }

    public void setDealMode(String dealMode) {
        this.dealMode = dealMode;
    }

    //public String getProdType() {
    //    return prodType;
    //}
    //
    //public void setProdType(String prodType) {
    //    this.prodType = prodType;
    //}
    //
    //public String getMinProductHold() {
    //    return minProductHold;
    //}
    //
    //public void setMinProductHold(String minProductHold) {
    //    this.minProductHold = minProductHold;
    //}

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

    public String getInvestmentCycle() {
        return investmentCycle;
    }

    public void setInvestmentCycle(String investmentCycle) {
        this.investmentCycle = investmentCycle;
    }
}
