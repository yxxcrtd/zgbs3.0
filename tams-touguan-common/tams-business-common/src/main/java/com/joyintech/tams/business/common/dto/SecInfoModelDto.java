/*
 * 文件名：SecInfoModelDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2019年06月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.business.common.dto;

import java.math.BigDecimal;
import java.util.Date;

public class SecInfoModelDto {

    /**
     * 债券起息日
     */
    private Date startDate;

    /**
     * 债券到期日
     */
    private Date endDate;

    /**
     * 债券首次付息日
     */
    private Date firstPayDate;

    /**
     * 发行价
     */
    private BigDecimal issuePrice;

    /**
     * 债券类型
     */
    private String bondsType;

    /**
     * 计息基础
     */
    private String basis;

    /**
     * 利率类型
     */
    private String intRateType;

    /**
     * 利息分配方式
     */
    private String allocationType;

    /**
     * 付息计划推算方式
     */
    private String calMethod;

    /**
     * 付息频率
     */
    private String yearIntRate;

    /**
     * 下一行权日
     */
    private Date nextExeDate;

    /**
     * 下一行权价
     */
    private BigDecimal nextExercisePrice;

    /**
     * 含权标识
     */
    private String isRight;

    /**
     * 获取 startDate
     *
     * @return startDate
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * 设置 startDate
     *
     * @param startDate startDate
     */
    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    /**
     * 获取 endDate
     *
     * @return endDate
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * 设置 endDate
     *
     * @param endDate endDate
     */
    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    /**
     * 获取 firstPayDate
     *
     * @return firstPayDate
     */
    public Date getFirstPayDate() {
        return firstPayDate;
    }

    /**
     * 设置 firstPayDate
     *
     * @param firstPayDate firstPayDate
     */
    public void setFirstPayDate(Date firstPayDate) {
        this.firstPayDate = firstPayDate;
    }

    /**
     * 获取 issuePrice
     *
     * @return issuePrice
     */
    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    /**
     * 设置 issuePrice
     *
     * @param issuePrice issuePrice
     */
    public void setIssuePrice(BigDecimal issuePrice) {
        this.issuePrice = issuePrice;
    }

    /**
     * 获取 bondsType
     *
     * @return bondsType
     */
    public String getBondsType() {
        return bondsType;
    }

    /**
     * 设置 bondsType
     *
     * @param bondsType bondsType
     */
    public void setBondsType(String bondsType) {
        this.bondsType = bondsType;
    }

    /**
     * 获取 basis
     *
     * @return basis
     */
    public String getBasis() {
        return basis;
    }

    /**
     * 设置 basis
     *
     * @param basis basis
     */
    public void setBasis(String basis) {
        this.basis = basis;
    }

    /**
     * 获取 intRateType
     *
     * @return intRateType
     */
    public String getIntRateType() {
        return intRateType;
    }

    /**
     * 设置 intRateType
     *
     * @param intRateType intRateType
     */
    public void setIntRateType(String intRateType) {
        this.intRateType = intRateType;
    }

    /**
     * 获取 allocationType
     *
     * @return allocationType
     */
    public String getAllocationType() {
        return allocationType;
    }

    /**
     * 设置 allocationType
     *
     * @param allocationType allocationType
     */
    public void setAllocationType(String allocationType) {
        this.allocationType = allocationType;
    }

    /**
     * 获取 calMethod
     *
     * @return calMethod
     */
    public String getCalMethod() {
        return calMethod;
    }

    /**
     * 设置 calMethod
     *
     * @param calMethod calMethod
     */
    public void setCalMethod(String calMethod) {
        this.calMethod = calMethod;
    }

    /**
     * 获取 yearIntRate
     *
     * @return yearIntRate
     */
    public String getYearIntRate() {
        return yearIntRate;
    }

    /**
     * 设置 yearIntRate
     *
     * @param yearIntRate yearIntRate
     */
    public void setYearIntRate(String yearIntRate) {
        this.yearIntRate = yearIntRate;
    }

    /**
     * 获取 nextExeDate
     *
     * @return nextExeDate
     */
    public Date getNextExeDate() {
        return nextExeDate;
    }

    /**
     * 设置 nextExeDate
     *
     * @param nextExeDate nextExeDate
     */
    public void setNextExeDate(Date nextExeDate) {
        this.nextExeDate = nextExeDate;
    }

    /**
     * 获取 nextExercisePrice
     *
     * @return nextExercisePrice
     */
    public BigDecimal getNextExercisePrice() {
        return nextExercisePrice;
    }

    /**
     * 设置 nextExercisePrice
     *
     * @param nextExercisePrice nextExercisePrice
     */
    public void setNextExercisePrice(BigDecimal nextExercisePrice) {
        this.nextExercisePrice = nextExercisePrice;
    }

    public String getIsRight() {
        return isRight;
    }

    public void setIsRight(String isRight) {
        this.isRight = isRight;
    }
}
