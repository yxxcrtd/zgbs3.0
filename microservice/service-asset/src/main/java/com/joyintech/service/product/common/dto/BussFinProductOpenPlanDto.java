/*
 * 文件名：BussFinProductOpenPlanDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：张亮
 * 修改时间：2018年9月4日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.product.common.dto;

import java.util.Date;
import java.util.List;

import com.joyintech.tams.common.base.entity.FinOpenPlan;
import com.joyintech.tams.common.base.entity.FinOpenRule;

/**
 * 开放计划信息
 * 
 * @author 张亮
 * @version 1.0 2018年9月4日
 * @see BussFinProductOpenPlanDto
 * @since 1.0
 */
public class BussFinProductOpenPlanDto extends FinOpenRule {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -4991093987765911764L;
    
    /**
     * 处理模式
     */
    private String dealMode;

    /**
     * 是否周期型
     */
    private String isCycle;

    /**
     * 封闭期限
     */
    private Integer closeTerm;

    /**
     * 起息日
     */
    private Date vdate;

    /**
     * 到期日
     */
    private Date mdate;

    /**
     * 交易日历
     */
    private String tradCalender;

    /**
     * 募集开始日期
     */
    private Date raiseStrDate;

    /**
     * 募集结束日期
     */
    private Date raiseEndDate;

    /**
     * 申购确认天数
     */
    private Integer purConfDays;

    /**
     * 周几
     */
    private String dayOfWeek;
    
    /**
     * 一个月中的第几周
     */
    private String weekOfMonth;
    /**
     * 一个月中第几周的周几
     */
    private String workdayOfWeek;
    /**
     * 几号
     */
    private String dayOfMonth;

    /**
     * 第几工作日
     */
    private String workdayOfMonth;

    /**
     * 几号/第几工作日 标识
     */
    private String openDate;

    /**
     * 净值日规则  01 开放确认自然日-1 ；02开放确认工作日-1 03 开放到期日
     */
    private String cpriceRule;
    /**
     * 开发类型 01:申购  02:赎回  03：申赎
     */
    
    private String openType;
    /**
     * 开放计划列表
     */
    private transient List<FinOpenPlan> finOpenPlanList;

    public String getDealMode() {
        return dealMode;
    }

    public void setDealMode(String dealMode) {
        this.dealMode = dealMode;
    }

    public Date getVdate() {
        return vdate;
    }

    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public String getTradCalender() {
        return tradCalender;
    }

    public void setTradCalender(String tradCalender) {
        this.tradCalender = tradCalender;
    }

    public Date getRaiseStrDate() {
        return raiseStrDate;
    }

    public void setRaiseStrDate(Date raiseStrDate) {
        this.raiseStrDate = raiseStrDate;
    }

    public Date getRaiseEndDate() {
        return raiseEndDate;
    }

    public void setRaiseEndDate(Date raiseEndDate) {
        this.raiseEndDate = raiseEndDate;
    }

    public Integer getPurConfDays() {
        return purConfDays;
    }

    public void setPurConfDays(Integer purConfDays) {
        this.purConfDays = purConfDays;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getDayOfMonth() {
        return dayOfMonth;
    }

    public void setDayOfMonth(String dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    public String getWorkdayOfMonth() {
        return workdayOfMonth;
    }

    public void setWorkdayOfMonth(String workdayOfMonth) {
        this.workdayOfMonth = workdayOfMonth;
    }

    public String getOpenDate() {
        return openDate;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public List<FinOpenPlan> getFinOpenPlanList() {
        return finOpenPlanList;
    }

    public void setFinOpenPlanList(List<FinOpenPlan> finOpenPlanList) {
        this.finOpenPlanList = finOpenPlanList;
    }

    public String getIsCycle() {
        return isCycle;
    }

    public void setIsCycle(String isCycle) {
        this.isCycle = isCycle;
    }

    public Integer getCloseTerm() {
        return closeTerm;
    }

    public void setCloseTerm(Integer closeTerm) {
        this.closeTerm = closeTerm;
    }

	public String getCpriceRule() {
		return cpriceRule;
	}

	public void setCpriceRule(String cpriceRule) {
		this.cpriceRule = cpriceRule;
	}

	public String getOpenType() {
		return openType;
	}

	public void setOpenType(String openType) {
		this.openType = openType;
	}

    
    /**
     * 获取 weekOfMonth
     * @return weekOfMonth.
     */
    public String getWeekOfMonth() {
        return weekOfMonth;
    }

    
    /**
     * 设置 weekOfMonth
     * @param weekOfMonth weekOfMonth
     */
    public void setWeekOfMonth(String weekOfMonth) {
        this.weekOfMonth = weekOfMonth;
    }

    
    /**
     * 获取 workdayOfWeek
     * @return workdayOfWeek.
     */
    public String getWorkdayOfWeek() {
        return workdayOfWeek;
    }

    
    /**
     * 设置 workdayOfWeek
     * @param workdayOfWeek workdayOfWeek
     */
    public void setWorkdayOfWeek(String workdayOfWeek) {
        this.workdayOfWeek = workdayOfWeek;
    }
    
    
}
