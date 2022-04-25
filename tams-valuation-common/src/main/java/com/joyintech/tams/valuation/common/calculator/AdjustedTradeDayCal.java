/*
 * 文件名：AdjustedTradeDayCal.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：何阳阳
 * 修改时间：2018年9月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.common.calculator;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 调整后交易日取得
 * 
 * @author 何阳阳
 * @version 1.0 2018年9月21日
 * @see AdjustedTradeDayCal
 * @since 1.0
 */
public class AdjustedTradeDayCal {

    /**
     * 调整后交易日取得(Date型)
     *
     * @param adjustBeforeDate 调整前日期 (yyyy-MM-dd)
     * @param adjustRole 调整规则
     * @param holidayList 节假日列表(Date型)
     * @throws Exception 例外
     * @return 调整后日期
     * @see
     */
    public static Date getAdjustedTradeDayByDate(Date adjustBeforeDate, String adjustRole,
                                                 List<Date> holidayList) {
        if (adjustBeforeDate != null && !StringUtil.isEmptyList(holidayList)) {
            List<String> holidayStringList = new ArrayList<>();
            for (Date holidate : holidayList) {
                String newHolidayStr = DateUtil.format(holidate);
                holidayStringList.add(newHolidayStr);
            }
            String adjustDate = getAdjustedTradeDay(DateUtil.format(adjustBeforeDate), adjustRole,
                holidayStringList);
            return DateUtil.parse(adjustDate);
        }
        return adjustBeforeDate;
    }

    /**
     * 调整后交易日取得
     *
     * @param adjustBeforeDate 调整前日期 (yyyy-MM-dd)
     * @param adjustRole 调整规则
     * @param holidayList 节假日列表 日期格式必须是 yyyy-MM-dd
     * @throws Exception 例外
     * @return 调整后日期
     * @see
     */
    public static String getAdjustedTradeDay(String adjustBeforeDate, String adjustRole,
                                             List<String> holidayList) {
        if (StringUtil.isBlank(adjustBeforeDate) || StringUtil.isEmptyList(holidayList)) {
            return adjustBeforeDate;
        }

        // 不管传进来的adjustBeforeDate是什么格式的日期字符串，将其转化为yyyy-MM-dd格式的日期字符串
        String adjustDate = adjustBeforeDate;

        // 将传进来的holidayList的日期也转化成yyyy-MM-dd格式的日期字符串
        List<String> newHolidList = holidayList;
//        for (String holidayStr : holidayList) {
//            newHolidList.add(holidayStr);
//        }

        // 输入参数.调整规则 = “不调整”时,调整后日期 = 输入参数.调整前日期
        if (adjustRole != null && adjustRole.equals(DictCodeList.营业日准则_不调整.getValue())) {
            return adjustBeforeDate;
        }

        // 调整前日期在输入参数.节假日列表中不存在时,调整后日期 = 输入参数.调整前日期
        if (newHolidList.indexOf(adjustDate) == -1) {
            return adjustBeforeDate;
        }
        else {
            if (adjustRole != null && adjustRole.equals(DictCodeList.营业日准则_向前调整.getValue())) {
                while (true) {
                    if (!isHoliday(adjustDate, newHolidList)) {
                        break;
                    }
                    else {
                        adjustDate = getAfterOrBeforeDay(adjustDate, 1, false);
                    }
                }
            }
            else if (adjustRole != null && adjustRole.equals(DictCodeList.营业日准则_向后调整.getValue())) {
                while (true) {
                    if (!isHoliday(adjustDate, newHolidList)) {
                        break;
                    }
                    else {
                        adjustDate = getAfterOrBeforeDay(adjustDate, 1, true);
                    }
                }
            }
            else if (adjustRole != null
                     && adjustRole.equals(DictCodeList.营业日准则_调整的向后调整.getValue())) {
                while (true) {
                    if (!isHoliday(adjustDate, newHolidList)) {
                        break;
                    }
                    else {
                        adjustDate = getAfterOrBeforeDay(adjustDate, 1, true);
                    }
                }
                if (adjustDate.substring(0, 7).equals(adjustBeforeDate.substring(0, 7))) {
                    return adjustDate;
                }
                else {
                    adjustDate = getAfterOrBeforeDay(adjustDate.substring(0, 7) + "-01", 1, false);
                    while (true) {
                        if (!isHoliday(adjustDate, holidayList)) {
                            break;
                        }
                        else {
                            adjustDate = getAfterOrBeforeDay(adjustDate, 1, false);
                        }
                    }
                }
            }
            return adjustDate;
        }
    }

    /**
     * 判断是否节假日
     *
     * @param date 日期
     * @param holidayList 节假日列表
     * @return true:节假日, false:非节假日
     * @see
     * @since 1.0
     */
    public static boolean isHoliday(String date, List<String> holidayList) {
        if (holidayList.indexOf(date) == -1) {
            return false;
        }
        return true;
    }

    /**
     * 取得指定日期的前N天或者后N天
     *
     * @param date 日期
     * @param days 指定天数
     * @param isAfterDay 取得指定日期的前N天:false,取得指定日期的后N天:true
     * @return 指定日期的前N天或者后N天
     * @throws Exception 例外
     * @see
     * @since 1.0
     */
    public static String getAfterOrBeforeDay(String date, int days, boolean isAfterDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DateUtil.parse(date));
        if (!isAfterDay) {
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) - days);
        }
        else {
            calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + days);
        }
        return DateUtil.format(calendar.getTime());
    }

    /**
     * 交易日推算日期
     *
     * @param beforeAdjustDate 调整前日期
     * @param days 调整天数
     * @param holidayList 节假日列表（日期）
     * @param flag 是否跨月 01：是、02：否
     * @return 调整后日期
     * @exception Exception 异常
     */
    public static Date calculationTradingDayByDate(Date beforeAdjustDate, int days,
                                                   List<Date> holidayList, String flag) {
        if (beforeAdjustDate != null && !StringUtil.isEmptyList(holidayList)) {
            List<String> holidayStringList = new ArrayList<>();
            for (Date holidate : holidayList) {
                String newHolidayStr = DateUtil.format(holidate);
                holidayStringList.add(newHolidayStr);
            }
            String adjustDate = calculationTradingDay(DateUtil.format(beforeAdjustDate), days,
                holidayStringList, flag);
            return DateUtil.parse(adjustDate);
        }
        return beforeAdjustDate;
    }

    /**
     * 交易日推算
     *
     * @param beforeAdjustDate 调整前日期
     * @param days 调整天数
     * @param holidayList 节假日列表
     * @param flag 是否跨月 01：是、02：否
     * @return 调整后日期
     * @exception Exception 异常
     */
    public static String calculationTradingDay(String beforeAdjustDate, int days,
                                               List<String> holidayList, String flag) {

        // 交易日计数
        int dayCount = 0;
        // 调整后非跨月日期
        String noCrossDate = null;
        // 调整后日期
        String afterAdjustDate = beforeAdjustDate;

        while (dayCount < days) {

            // 中间参数.调整后日期 = 临时变量.调整后日期 + 1日
            String tempAfterAdjustDate = DateUtil.addDay(afterAdjustDate, 1);
            while (holidayList.indexOf(tempAfterAdjustDate) > 0) {
                tempAfterAdjustDate = DateUtil.addDay(tempAfterAdjustDate, 1);
            }

            if (StringUtil.equals(DictCodeList.是否_否.getValue(), flag) && !StringUtil.equals(
                DateUtil.format(DateUtil.parse(tempAfterAdjustDate), "yyyyMM"),
                DateUtil.format(DateUtil.parse(beforeAdjustDate), "yyyyMM"))) {
                noCrossDate = afterAdjustDate;
                return noCrossDate;
            }
            else {
                afterAdjustDate = tempAfterAdjustDate;
                dayCount++ ;
            }
        }
        return afterAdjustDate;
    }
}
