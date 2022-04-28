/*
 * 文件名：ValTaxBokBalanceDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年8月2日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.vatdraw.dto;

/**
 * 税额计提分类配置Dto
 * 
 * @author weipeng
 * @version 1.0 2018年8月2日
 * @see ValTaxCalConfigDto
 * @since 1.0
 */
public class ValTaxCalConfigDto {

    /**
     * 配置代码
     */
    private String configId;

    /**
     * 记账场景(会计事件编码)
     */
    private String tableId;

    /**
     * 发生额科目性质
     */
    private String subjectNature;

    /**
     * 增值税类型
     */
    private String vatType;

    /**
     * 确认类型
     */
    private String taxPType;

    /**
     * 发生场景
     */
    private String incomeSource;

    /**
     * 增项/减项
     */
    private String inOut;

    /**
     * 计算方式
     */
    private String calcType;

    /**
     * 发生科目借贷方向
     */
    private String cdFlag;

    /**
     * 占比方式
     */
    private String calcScale;

    /**
     * 占比科目科目性质
     */
    private String calcSnature;

    /**
     * 占比基数
     */
    private String calcBase;

    /**
     * 金额计入方式
     */
    private String isCarrt;

    /**
     * 收息处理方式
     */
    private String intRepayHandle;
    
    /**
     * 处理方式
     */
    private String condition;

    /**
     * 账套
     */
    private String booksetId;

    private String schemeType;

    /**
     * 取得 配置代码
     * 
     * @return 配置代码
     */
    public String getConfigId() {
        return configId;
    }

    /**
     * 设置 配置代码
     * 
     * @param configId 配置代码
     */
    public void setConfigId(String configId) {
        this.configId = configId;
    }

    /**
     * 取得 记账场景(会计事件编码)
     * 
     * @return 记账场景(会计事件编码)
     */
    public String getTableId() {
        return tableId;
    }

    /**
     * 设置 记账场景(会计事件编码)
     * 
     * @param tableId 记账场景(会计事件编码)
     */
    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    /**
     * 取得 发生额科目性质
     * 
     * @return 发生额科目性质
     */
    public String getSubjectNature() {
        return subjectNature;
    }

    /**
     * 设置 发生额科目性质
     * 
     * @param subjectNature 发生额科目性质
     */
    public void setSubjectNature(String subjectNature) {
        this.subjectNature = subjectNature;
    }

    /**
     * 取得 增值税类型
     * 
     * @return 增值税类型
     */
    public String getVatType() {
        return vatType;
    }

    /**
     * 设置 增值税类型
     * 
     * @param vatType 增值税类型
     */
    public void setVatType(String vatType) {
        this.vatType = vatType;
    }

    /**
     * 取得 确认类型
     * 
     * @return 确认类型
     */
    public String getTaxPType() {
        return taxPType;
    }

    /**
     * 设置 确认类型
     * 
     * @param taxPType 确认类型
     */
    public void setTaxPType(String taxPType) {
        this.taxPType = taxPType;
    }

    /**
     * 取得 发生场景
     * 
     * @return 发生场景
     */
    public String getIncomeSource() {
        return incomeSource;
    }

    /**
     * 设置 发生场景
     * 
     * @param incomeSource 发生场景
     */
    public void setIncomeSource(String incomeSource) {
        this.incomeSource = incomeSource;
    }

    /**
     * 取得 增项/减项
     * 
     * @return 增项/减项
     */
    public String getInOut() {
        return inOut;
    }

    /**
     * 设置 增项/减项
     * 
     * @param inOut 增项/减项
     */
    public void setInOut(String inOut) {
        this.inOut = inOut;
    }

    /**
     * 取得 计算方式
     * 
     * @return 计算方式
     */
    public String getCalcType() {
        return calcType;
    }

    /**
     * 设置 计算方式
     * 
     * @param calcType 计算方式
     */
    public void setCalcType(String calcType) {
        this.calcType = calcType;
    }

    /**
     * 取得 发生科目借贷方向
     * 
     * @return 发生科目借贷方向
     */
    public String getCdFlag() {
        return cdFlag;
    }

    /**
     * 设置 发生科目借贷方向
     * 
     * @param cdFlag 发生科目借贷方向
     */
    public void setCdFlag(String cdFlag) {
        this.cdFlag = cdFlag;
    }

    /**
     * 取得 占比基数
     * 
     * @return 占比基数
     */
    public String getCalcBase() {
        return calcBase;
    }

    /**
     * 设置 占比基数
     * 
     * @param calcBase 占比基数
     */
    public void setCalcBase(String calcBase) {
        this.calcBase = calcBase;
    }

    /**
     * 取得 金额计入方式
     * 
     * @return 金额计入方式
     */
    public String getIsCarrt() {
        return isCarrt;
    }

    /**
     * 设置 金额计入方式
     * 
     * @param isCarrt 金额计入方式
     */
    public void setIsCarrt(String isCarrt) {
        this.isCarrt = isCarrt;
    }

    /**
     * 获取 收息处理方式
     * 
     * @return 收息处理方式.
     */
    public String getIntRepayHandle() {
        return intRepayHandle;
    }

    /**
     * 设置 收息处理方式
     * 
     * @param intRepayHandle 收息处理方式
     */
    public void setIntRepayHandle(String intRepayHandle) {
        this.intRepayHandle = intRepayHandle;
    }

    /**
     * 获取 占比方式
     * 
     * @return 占比方式
     */
    public String getCalcScale() {
        return calcScale;
    }

    /**
     * 设置 占比方式
     * 
     * @param calcScale 占比方式
     */
    public void setCalcScale(String calcScale) {
        this.calcScale = calcScale;
    }

    /**
     * 获取 占比科目科目性质
     * 
     * @return 占比科目科目性质
     */
    public String getCalcSnature() {
        return calcSnature;
    }

    /**
     * 设置 占比科目科目性质
     * 
     * @param calcSnature 占比科目科目性质
     */
    public void setCalcSnature(String calcSnature) {
        this.calcSnature = calcSnature;
    }

	/**
	 * @return the condition
	 */
	public String getCondition() {
		return condition;
	}

	/**
	 * @param condition the condition to set
	 */
	public void setCondition(String condition) {
		this.condition = condition;
	}

    public String getSchemeType() {
        return schemeType;
    }

    public void setSchemeType(String schemeType) {
        this.schemeType = schemeType;
    }

    public String getBooksetId() {
        return booksetId;
    }

    public void setBooksetId(String booksetId) {
        this.booksetId = booksetId;
    }
}
