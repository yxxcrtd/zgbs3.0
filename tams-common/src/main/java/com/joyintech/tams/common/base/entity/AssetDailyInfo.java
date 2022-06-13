package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;

import java.math.BigDecimal;
import java.util.Date;

public class AssetDailyInfo extends AssetDailyInfoKey {

    /**
     * 到期日待偿期
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mPendingPeriod;

    /**
     * 到期日待偿期单位（D/Y）
     */
    private String mPendingPeriodUnit;

    /**
     * 未来行权日待偿期
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal exPendingPeriod;

    /**
     * 未来行权日待偿期单位（D/Y）
     */
    private String exPendingPeriodUnit;

    /**
     * 获取 到期日待偿期
     *
     * @return mPendingPeriod 到期日待偿期
     */
    public BigDecimal getMPendingPeriod() {
        return this.mPendingPeriod;
    }

    /**
     * 设置 到期日待偿期
     *
     * @param mPendingPeriod 到期日待偿期
     */
    public void setMPendingPeriod(BigDecimal mPendingPeriod) {
        this.mPendingPeriod = mPendingPeriod;
    }

    /**
     * 获取 到期日待偿期单位（DY）
     *
     * @return mPendingPeriodUnit 到期日待偿期单位（DY）
     */
    public String getMPendingPeriodUnit() {
        return this.mPendingPeriodUnit;
    }

    /**
     * 设置 到期日待偿期单位（DY）
     *
     * @param mPendingPeriodUnit 到期日待偿期单位（DY）
     */
    public void setMPendingPeriodUnit(String mPendingPeriodUnit) {
        this.mPendingPeriodUnit = mPendingPeriodUnit;
    }

    /**
     * 获取 未来行权日待偿期
     *
     * @return exPendingPeriod 未来行权日待偿期
     */
    public BigDecimal getExPendingPeriod() {
        return this.exPendingPeriod;
    }

    /**
     * 设置 未来行权日待偿期
     *
     * @param exPendingPeriod 未来行权日待偿期
     */
    public void setExPendingPeriod(BigDecimal exPendingPeriod) {
        this.exPendingPeriod = exPendingPeriod;
    }

    /**
     * 获取 未来行权日待偿期单位（DY）
     *
     * @return exPendingPeriodUnit 未来行权日待偿期单位（DY）
     */
    public String getExPendingPeriodUnit() {
        return this.exPendingPeriodUnit;
    }

    /**
     * 设置 未来行权日待偿期单位（DY）
     *
     * @param exPendingPeriodUnit 未来行权日待偿期单位（DY）
     */
    public void setExPendingPeriodUnit(String exPendingPeriodUnit) {
        this.exPendingPeriodUnit = exPendingPeriodUnit;
    }

}