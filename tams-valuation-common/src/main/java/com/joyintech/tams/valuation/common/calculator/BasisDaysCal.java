/*
 * 文件名：BasisDaysCal.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.common.calculator;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

/**
 * 计息基础天数
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年10月17日
 * @see BasisDaysCal
 * @since 1.0
 */
public class BasisDaysCal {

    /**
     * 计算30/360计息基础
     * @param basis 计息基础
     * @param calcMethod 推算方法  A/A 必须输入 
     * @param a2ainfo   A/A场合时，需要计算的要素 Date
                                                                                 付息开始日                   A/A 必须输入(0) 
                                                                                 资产起息日                   A/A 必须输入(1)  
                                                                                 资产到期日                   A/A 必须输入(2)  
                                                                                 首次付息日                   A/A 且 推算方法 = "首次付息日"时，必须输入 (3) 
     * @return 计息天数
     * @see
     * @since 1.0
     */
    public static BigDecimal cal(String basis, String calcMethod, Date[] a2ainfo) {
        BigDecimal res = new BigDecimal(365);
        
        // 计息基础 = A/A
        if(StringUtil.equals(DictCodeList.计息基础_A_A.getValue(), basis)
        ||StringUtil.equals(DictCodeList.计息基础_A_A_按实际天数分配.getValue(), basis)){
            // 付息开始日
            Date svDate = a2ainfo[0];
            // 资产起息日
            Date vDate = a2ainfo[1];
            // 资产到期日
            Date mDate = a2ainfo[2];
            // 首次付息日
            Date fDate = a2ainfo[3];
            
            // 计算开始日
            Date fromDate = null;
            // 计算结束日
            Date endDate = null;
            // 推算顺序
            int order = 1;
            if(StringUtil.equals(DictCodeList.推算方式_起息日向后.getValue(), calcMethod)){
                fromDate = vDate;
                endDate = mDate;
            } 
            else if (StringUtil.equals(DictCodeList.推算方式_到期日向前.getValue(), calcMethod)) {
                fromDate = mDate;
                endDate = vDate;
                order = -1;
            } 
            else if (StringUtil.equals(DictCodeList.推算方式_首次支付日.getValue(), calcMethod)) {
                fromDate = fDate;
                endDate = mDate;
                //首次支付日 默认取了365天不对，第一条计划天数计算逻辑取值不对
                if(CalendarUtil.calendarcal(svDate, Calendar.YEAR, order).compareTo(fDate)==0){
                    BigDecimal days = CalendarUtil.calendarDays(svDate, fDate);
                    return days.abs();
                }
            }
            while (fromDate != null && (order == 1 && fromDate.compareTo(endDate) <= 0
                    || order == -1 && fromDate.compareTo(endDate) >= 0)) {
                Date tempDate = CalendarUtil.calendarcal(fromDate, Calendar.YEAR, order);
                if (order == 1 && svDate.compareTo(fromDate) >=0 && svDate.compareTo(tempDate) < 0
                    || order == -1 && svDate.compareTo(tempDate) >=0 && svDate.compareTo(fromDate) < 0
                ) {
                    BigDecimal days = CalendarUtil.calendarDays(fromDate, tempDate);
                    if (days != null) {
                        return days.abs();
                    }
                    return days;
                }
                else {
                    fromDate = tempDate;
                }
            }
        } 
        // 计息基础 = A/360 或者 30/360
        else if(StringUtil.equals(DictCodeList.计息基础_30_360.getValue(), basis)
            ||StringUtil.equals(DictCodeList.计息基础_A_360.getValue(), basis)){
            res = new BigDecimal(360);
        }
        return res;
    }
}
