/*
 * 文件名：CalendarUtil.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月16日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.common.calculator;

import com.joyintech.tams.constant.DictCodeListExt1;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;


/**
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年10月16日
 * @see CalendarUtil
 * @since 1.0
 */
public class CalendarUtil {

    /**
     * 日期 年月日 加减
     * @param date 传入日期
     * @param type 年月日类型 (Calendar.YEAR。。。)
     * @param num 加减数
     * @return 处理后的日期
     * @see
     * @since 1.0
     */
    public static Date calendarcal(Date date, int type, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(type, num);
        return calendar.getTime();
    }

    /**
     * 获取年月日信息
     * @param date 传入日期
     * @param type 年月日类型 (Calendar.YEAR。。。)
     * @return 处理后的日期
     * @see
     * @since 1.0
     */
    public static int get(Date date, int type) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(type);
    }

    /**
     * 设置年月日信息
     * @param date 传入日期
     * @param type 年月日类型 (Calendar.YEAR。。。)
     * @param num 设置的值
     * @return 处理后的日期
     * @see
     * @since 1.0
     */
    public static Date set(Date date, int type, int num) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(type, num);
        return calendar.getTime();
    }


    /**
     * 两个日期之前的天数
     * @param fDate 开始时间
     * @param eDate 结束时间
     * @return 天数
     * @see
     * @since 1.0
     */
    public static BigDecimal calendarDays(Date fDate, Date eDate) {
        if (fDate == null || eDate == null) {
            return BigDecimal.ZERO;
        }

        return  new BigDecimal((eDate.getTime()-fDate.getTime())/(24*60*60*1000));
    }

    /**
     * 判断是否是闰年
     * @param d 时间
     * @return 是否
     * @see
     * @since 1.0
     */
    public static boolean isleapyear(Date d) {
        int year = get(d, Calendar.YEAR);
        if (year % 100 == 0) {
            if (year % 400 == 0) {
                return true;
            }
        }
        else {
            if (year % 4 == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 系统参数转化为Calendar常量
     *
     * @param dict
     *            系统参数
     * @return Calendar常量
     * @see
     * @since 1.0
     */
    public static Integer convertUnit(String dict) {
        if (DictCodeListExt1.周期单位_日.getValue().equals(dict)) {
            return Calendar.DATE;
        }
        else if (DictCodeListExt1.周期单位_月.getValue().equals(dict)) {
            return Calendar.MONTH;
        }
        else if (DictCodeListExt1.周期单位_年.getValue().equals(dict)) {
            return Calendar.YEAR;
        }
        else {
            return Calendar.DATE;
        }
    }
}
