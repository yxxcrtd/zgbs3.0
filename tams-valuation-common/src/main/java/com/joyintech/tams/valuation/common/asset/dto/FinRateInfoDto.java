/*
 * 文件名：FinCashCalcRuleDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.common.asset.dto;

import com.joyintech.tams.common.base.entity.FinRateInfo;

import java.util.Date;

/**
 *  现金流利率
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月21日
 * @see FinRateInfoDto
 * @since 1.0
 */
public class FinRateInfoDto extends FinRateInfo{

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 交易日历
     */
    private String calendarId;
    
    /**
     * 起息日
     */
    private Date vdate;

    /**
     * 到期日
     */
    private Date mdate;

    
    /**
     * 获取 calendarId
     * @return calendarId.
     */
    public String getCalendarId() {
        return calendarId;
    }

    
    /**
     * 设置 calendarId
     * @param calendarId calendarId
     */
    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }


    
    /**
     * 获取 vdate
     * @return vdate.
     */
    public Date getVdate() {
        return vdate;
    }


    
    /**
     * 设置 vdate
     * @param vdate vdate
     */
    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }


    
    /**
     * 获取 mdate
     * @return mdate.
     */
    public Date getMdate() {
        return mdate;
    }


    
    /**
     * 设置 mdate
     * @param mdate mdate
     */
    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }
    
    

}
