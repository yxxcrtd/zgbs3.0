/*
 * 文件名：InterestDaysCal30T365.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.common.calculator;

import com.joyintech.framework.util.DateUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 30_360年化计息天数
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年10月17日
 * @see InterestDaysCal30T365
 * @since 1.0
 */
public class InterestDaysCal30T365 {

    /**
     * 计算30/360计息基础
     * 
     * @param stadate 起始日
     * @param enddate 截止日
     * @return 计息天数
     * @throws ParseException 
     */
    public static BigDecimal basis30T365(Date stadate, Date enddate) throws ParseException {

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(stadate);
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(enddate);
        // 年
        int y1 = calendar1.get(Calendar.YEAR);
        int y2 = calendar2.get(Calendar.YEAR);
        // 月
        int m1 = calendar1.get(Calendar.MONTH);
        int m2 = calendar2.get(Calendar.MONTH);
        // 日
        int d1 = calendar1.get(Calendar.DATE);
        int d2 = calendar2.get(Calendar.DATE);
        // 日期+1 判断是否是2月最后一天
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date dateTmp1 = sdf.parse(DateUtil.plusDays(sdf.format(stadate), 1));
        Date dateTmp2 = sdf.parse(DateUtil.plusDays(sdf.format(enddate), 1));
        Calendar calendar3 = Calendar.getInstance();
        calendar3.setTime(dateTmp1);
        Calendar calendar4 = Calendar.getInstance();
        calendar4.setTime(dateTmp2);
        int m3 = calendar3.get(Calendar.MONTH);
        int m4 = calendar4.get(Calendar.MONTH);

        // 如果计息期首日为31号，将首日作为第30天处理
        if (31 == d1) {
            d1 = 30;
        }
        if (31 == d2) {
            d2 = 30;
        }
        // 如果计息期的首日和尾日都是2月最后一天，将最后一个日期作为第30日处理
        if (m1 == 1 && m2 == 1 && m3 == 2 && m4 == 2) {
            d2 = 30;
        }
        // 如果计息期的首日为30号或31号，而尾日为2月的最后一天，则将最后一个日期作为30号处理
        else if (d1 == 30 && m2 == 1 && m4 == 2) {
            d2 = 30;
        }
        // 1.29-2.28 特殊处理 如果是平年 按照1个月计息 如果是闰年 得按天计息
        else if (d1 == 29 && m1 == 0 && m2 == 1 && d2 == 28 && m4 == 2) {
            d2 = 29;
        }
        // 如果计息期的首日为2月的最后一天，将起始日期作为30号处理
        else if (m1 == 1 && m3 == 2) {
            d1 = 30;
        }
        // // 如果计息期的首日为30或31号，而尾日为31号，则将尾日作为第30天处理
        // else if (d1 == 30 && d2 == 31) {
        // d2 = 30;
        // }
        BigDecimal res = BigDecimal.valueOf(360 * (y2 - y1) + 30 * (m2 - m1) + (d2 - d1))
            .divide(BigDecimal.valueOf(360.0), 12, RoundingMode.HALF_UP);
        return res;
    }
}
