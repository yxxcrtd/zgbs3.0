/*
 * 文件名：DateCalService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：何阳阳
 * 修改时间：2018年9月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.service;

import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.calculator.AdjustedTradeDayCal;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.common.DateDivisionDto;
import com.joyintech.tams.invest.common.service.dao.InvestDateCalcMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 付息计划，开放计划，利率重置，申购计划，赎回计划计算用和日期关联的Service
 *
 * @author 何阳阳
 * @version 1.0 2018年9月21日
 * @see InvestDateCalService
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class InvestDateCalService {

    /**
     * 付息计划，开放计划，利率重置，申购计划，赎回计划计算用和日期关联的Mapper
     */
    @Autowired(required = false)
    private InvestDateCalcMapper dateCalcMapper;

    /**
     * 交易日历的节假日取得
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param dealCalId 交易日历代码
     * @return 节假日列表
     */
    public List<String> getDealCalendarHoliday(Date startDate, Date endDate, String dealCalId) {
        Map<String, Object> paraMap = new HashMap<>();
        int days = StringUtil.toInt(CacheUtil.getSysParam(BussinessConstant.HOLIDAY_POSTPONE_DAYS));
        paraMap.put("startDate", DateUtil.addDay(startDate, -days));
        paraMap.put("endDate", DateUtil.addDay(endDate, days));
        paraMap.put("dealCalId", dealCalId);
        return dateCalcMapper.getDealCalendarHoliday(paraMap);
    }
//
//    /**
//     * 交易日历的节假日取得
//     *
//     * @param calendarId 交易日历代码
//     * @return 节假日列表
//     * @see
//     */
//    public List<String> getDealCalendarHoliday(String calendarId) {
//        return dateCalcMapper.getHolidayDealCalendarId(calendarId);
//    }

    /**
     * 判断日期参数是否交易日历的节假日
     *
     * @param dateStr 参数日期
     * @param calId   交易日历id
     * @return 是否交易日历的节假日判断结果
     */
    public boolean isDealCalendarHoliday(String dateStr, String calId) {
        if (StringUtil.isEmpty(dateStr) || StringUtil.isEmpty(calId)) {
            return false;
        }
        int dataCount = dateCalcMapper.selectDealCalendarHolidayCount(dateStr, calId);
        if (dataCount > 0) {
            return true;
        }
        return false;
    }

    /**
     * 判断日期参数是否交易日历的节假日 <一句话功能简述> <功能详细描述>
     *
     * @param date  参数日期
     * @param calId 交易日历id
     * @return 是否交易日历的节假日判断结果
     * @see
     * @since 1.0
     */
    public boolean isDealCalendarHoliday(Date date, String calId) {
        if (date == null || StringUtil.isEmpty(calId)) {
            return false;
        }
        String dateStr = DateUtil.format(date);
        return this.isDealCalendarHoliday(dateStr, calId);
    }
//
//    /**
//     * 市场的节假日取得
//     *
//     * @param startDate 开始日期
//     * @param endDate 结束日期
//     * @param marketCode 市场代码
//     * @return 节假日列表
//     * @see
//     */
//    public List<String> getMarketHoliday(String startDate, String endDate, String marketCode) {
//        Map<String, Object> paraMap = new HashMap<String, Object>();
//        paraMap.put("startDate", startDate);
//        paraMap.put("endDate", endDate);
//        paraMap.put("marketCode", marketCode);
//        return dateCalcMapper.getMarketHoliday(paraMap);
//    }

    /**
     * 自然日日期计算
     *
     * @param date 日期计算基准日期 (yyyyMMdd)
     * @param role 日期计算单位
     * @param calc 日期计算量
     * @return 计算后日期
     * @throws Exception 例外
     * @see
     */
    public Date naturalDateCalc(Date date, String role, int calc) {
        String result = null;
        String naturalDate = DateUtil.format(date);
        // 一体化对应etliu start
//        if (role.equals(DictCodeListExt1.周期单位_日.getValue())) {
        if (role.equals(DictCodeList.频率_日.getValue())) {
            // 一体化对应etliu end
            if (calc < 0) {
                result = AdjustedTradeDayCal.getAfterOrBeforeDay(naturalDate, calc * -1, false);
            } else {
                result = AdjustedTradeDayCal.getAfterOrBeforeDay(naturalDate, calc, true);
            }
        }
        if (role.equals(DictCodeList.频率_周.getValue())) {
            // 一体化对应etliu end
            if (calc < 0) {
                result = AdjustedTradeDayCal.getAfterOrBeforeDay(naturalDate, calc * -7, false);
            } else {
                result = AdjustedTradeDayCal.getAfterOrBeforeDay(naturalDate, calc * 7, true);
            }
        }
        // 一体化对应etliu start
//        if (role.equals(DictCodeListExt1.周期单位_月.getValue())) {
        if (role.equals(DictCodeList.频率_月.getValue())) {
            // 一体化对应etliu end
            if (calc < 0) {
                result = getAfterOrBeforeMonth(naturalDate, calc * -1, false);
            } else {
                result = getAfterOrBeforeMonth(naturalDate, calc, true);
            }
        }
        if (role.equals(DictCodeList.频率_季.getValue())) {
            // 一体化对应etliu end
            if (calc < 0) {
                result = getAfterOrBeforeMonth(naturalDate, calc * -1 * 3, false);
            } else {
                result = getAfterOrBeforeMonth(naturalDate, calc * 3, true);
            }
        }
        // 一体化对应etliu start
//        if (role.equals(DictCodeListExt1.周期单位_年.getValue())) {
        if (role.equals(DictCodeList.频率_年.getValue())) {
            // 一体化对应etliu end
            if (calc < 0) {
                result = getAfterOrBeforeMonth(naturalDate, calc * -12, false);
            } else {
                result = getAfterOrBeforeMonth(naturalDate, calc * 12, true);
            }
        }
        if (StringUtil.isEmpty(result)) {
            return null;
        }
        return DateUtil.parse(result);
    }

    /**
     * 日期区间分割
     *
     * @param startDate  区间开始日
     * @param endDate    区间结束日
     * @param calcMethod 推算方法
     * @param period     分割周期
     * @param periodUnit 分割周期单位
     * @return 分割后开始日结束日的DTO列表
     * @throws Exception 例外
     * @see
     * @since 1.0
     */
    public List<DateDivisionDto> dateIntervalDivision(Date startDate, Date endDate,
                                                      String calcMethod, String period,
                                                      String periodUnit) {
        Date startDateTemp = null;
        List<DateDivisionDto> list = new ArrayList<DateDivisionDto>();

        if (calcMethod != null && calcMethod.equals(DictCodeList.营业日准则_向后调整.getValue())) {
            startDateTemp = startDate;
            int count = 1;
            // 一体化对应etliu start
//            while (DateUtil.compareDate(startDateTemp, endDate) <= 0) {
            while (startDateTemp != null && DateUtil.compareDate(startDateTemp, endDate) <= 0) {
                // 一体化对应etliu end
                Date calcDate = naturalDateCalc(startDate, periodUnit,
                        Integer.parseInt(period) * count);
                if (DateUtil.compareDate(calcDate, endDate) < 0) {
                    DateDivisionDto dto = new DateDivisionDto();
                    dto.setStartDate(startDateTemp);
                    dto.setEndDate(calcDate);
                    list.add(dto);
                    startDateTemp = calcDate;

                    count++;
                } else {
                    DateDivisionDto dto = new DateDivisionDto();
                    dto.setStartDate(startDateTemp);
                    dto.setEndDate(endDate);
                    list.add(dto);
                    break;
                }
            }
        } else if (calcMethod != null && calcMethod.equals(DictCodeList.营业日准则_向前调整.getValue())) {
            startDateTemp = startDate;
            while (DateUtil.compareDate(startDateTemp, endDate) > 0) {
                Date calcDate = naturalDateCalc(startDateTemp, periodUnit,
                        Integer.parseInt(period));
                if (DateUtil.compareDate(calcDate, endDate) > 0) {
                    DateDivisionDto dto = new DateDivisionDto();
                    dto.setStartDate(startDateTemp);
                    dto.setEndDate(calcDate);
                    list.add(dto);
                    startDateTemp = calcDate;
                } else {
                    DateDivisionDto dto = new DateDivisionDto();
                    dto.setStartDate(startDateTemp);
                    dto.setEndDate(endDate);
                    list.add(dto);
                    break;
                }
            }
            // 倒序再次排列
            Collections.reverse(list);
        }
        return list;
    }

    /**
     * 日期区间分割
     *
     * @param startDate  区间开始日
     * @param endDate    区间结束日
     * @param calcMethod 推算方法
     * @param period     分割周期
     * @param periodUnit 分割周期单位
     * @return 分割后节点的列表
     * @throws Exception 例外
     * @see
     * @since 1.0
     */
    public List<Date> dateIntervalNodeList(Date startDate, Date endDate, String calcMethod,
                                           String period, String periodUnit) {
        List<DateDivisionDto> dateIntervalDivisionList = this.dateIntervalDivision(startDate,
                endDate, calcMethod, period, periodUnit);
        List<Date> resultDivisionList = new ArrayList<>();
        if (!StringUtil.isEmptyList(dateIntervalDivisionList)) {
            for (int i = 0; i < dateIntervalDivisionList.size(); i++) {
                resultDivisionList.add(dateIntervalDivisionList.get(i).getStartDate());
//              if (i == dateIntervalDivisionList.size() - 1 && dateIntervalDivisionList.size() != 1) {
                if (i == dateIntervalDivisionList.size() - 1 && DateUtil.compareDate(startDate, endDate) != 0) {
                    resultDivisionList.add(dateIntervalDivisionList.get(i).getEndDate());
                }
            }
        }

        return resultDivisionList;
    }

    /**
     * 日期区间分割
     *
     * @param startDate  区间开始日
     * @param endDate    区间结束日
     * @param calcMethod 推算方法
     * @param period     分割周期
     * @param periodUnit 分割周期单位
     * @return 分割后节点的列表
     * @throws Exception 例外
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    public List<Date> dateIntervalNodeList(List<Date> startDateList, Date endDate, String calcMethod,
                                           String period, String periodUnit) {
        List<Date> resultDivisionList = new ArrayList<>();
        if (!StringUtil.isEmptyList(startDateList)) {
            for (Date startDate : startDateList) {
                List<DateDivisionDto> dateIntervalDivisionList = this.dateIntervalDivision(startDate,
                        endDate, calcMethod, period, periodUnit);
                if (!StringUtil.isEmptyList(dateIntervalDivisionList)) {
                    for (int i = 0; i < dateIntervalDivisionList.size(); i++) {
                        resultDivisionList.add(dateIntervalDivisionList.get(i).getStartDate());
                        if (i == dateIntervalDivisionList.size() - 1) {
                            resultDivisionList.add(dateIntervalDivisionList.get(i).getEndDate());
                        }
                    }
                }
            }
        }

        Set h = new HashSet(resultDivisionList);
        resultDivisionList.clear();
        resultDivisionList.addAll(h);
        // 升序排序
        Collections.sort(resultDivisionList);

        return resultDivisionList;
    }
    /**
     * 交易日推算 夸月返回空
     *
     * @param beforeAdjustDate 调整前日期
     * @param days             调整天数
     * @param holidayList      节假日列表
     * @param flag             是否跨月 1：是、2：否
     * @return 调整后日期
     * @throws Exception 异常
     */
    public Date calculationTradingDayEx(Date beforeAdjustDate, int days,
                                        List<String> holidayList, String flag) {
        // 交易日计数
        int dayCount = 0;
        // 调整后日期
        Date afterAdjustDate = beforeAdjustDate;

        while (dayCount < days) {
            // 中间参数.调整后日期 = 临时变量.调整后日期 + 1日
            Date tempAfterAdjustDate = DateUtil.addDay(afterAdjustDate, 1);
            while (holidayList.indexOf(DateUtil.format(tempAfterAdjustDate)) > -1) {
                tempAfterAdjustDate = DateUtil.addDay(tempAfterAdjustDate, 1);
            }

            if (StringUtil.equals(DictCodeList.是否_否.getValue(), flag) && !StringUtil.equals(
                    DateUtil.format(tempAfterAdjustDate, "yyyyMM"),
                    DateUtil.format(beforeAdjustDate, "yyyyMM"))) {
                return null;
            } else {
                afterAdjustDate = tempAfterAdjustDate;
                dayCount++;
            }
        }
        return afterAdjustDate;
    }

    /**
     * 交易日推算(向前)
     *
     * @param beforeAdjustDate 调整前日期
     * @param days             调整天数
     * @param holidayList      节假日列表
     * @param flag             是否跨月 1：是、2：否
     * @return 调整后日期
     * @throws Exception 异常
     */
    public String calculationTradingDayForBefore(String beforeAdjustDate, int days,
                                                 List<String> holidayList, String flag){
        // 交易日计数
        int dayCount = 0;
        // 调整后非跨月日期
        String noCrossDate = null;
        // 调整后日期
        String afterAdjustDate = beforeAdjustDate;

        while (dayCount < days) {

            // 中间参数.调整后日期 = 临时变量.调整后日期 - 1日
            String tempAfterAdjustDate = DateUtil.addDay(afterAdjustDate, -1);
            while (holidayList.indexOf(tempAfterAdjustDate) > 0) {
                tempAfterAdjustDate = DateUtil.addDay(tempAfterAdjustDate, -1);
            }

            if (StringUtil.equals(DictCodeList.是否_否.getValue(), flag) && !StringUtil.equals(
                    DateUtil.format(DateUtil.parse(tempAfterAdjustDate), "yyyyMM"),
                    DateUtil.format(DateUtil.parse(beforeAdjustDate), "yyyyMM"))) {
                noCrossDate = afterAdjustDate;
                return noCrossDate;
            } else {
                afterAdjustDate = tempAfterAdjustDate;
                dayCount++;
            }
        }
        return afterAdjustDate;
    }

    /**
     * 取得指定日期的前N月或者后N月
     *
     * @param date         日期
     * @param months       指定月数
     * @param isAfterMonth 取得指定日期的前N月:false,取得指定日期的后N月:true
     * @return 指定日期的前N月或者后N月
     * @throws Exception 例外
     * @see
     * @since 1.0
     */
    private String getAfterOrBeforeMonth(String date, int months, boolean isAfterMonth) {
        if (!isAfterMonth) {
            return DateUtil.format(DateUtil.addMonthDate(DateUtil.parse(date), months * -1));
        } else {
            return DateUtil.format(DateUtil.addMonthDate(DateUtil.parse(date), months));
        }
    }

    // modify by etliu 20210517 债券二级指令T+1场景全价计算错误问题修正 start
    /**
     * 交易日推算
     *
     * @param beforeAdjustDate 调整前日期
     * @param days 调整天数
     * @param holidayList 节假日列表
     * @param flag 是否跨月 1：是、2：否
     * @return 调整后日期
     * @exception Exception 异常
     */
    public Date calculationTradingDay(Date beforeAdjustDate, int days,
                                      List<String> holidayList, String flag) {
        // 交易日计数
        int dayCount = 0;
        // 调整后非跨月日期
        Date noCrossDate = null;
        // 调整后日期
        Date afterAdjustDate = beforeAdjustDate;

        while (dayCount < days) {
            // 中间参数.调整后日期 = 临时变量.调整后日期 + 1日
            Date tempAfterAdjustDate = DateUtil.addDay(afterAdjustDate, 1);
            while (holidayList.indexOf(DateUtil.format(tempAfterAdjustDate)) > -1) {
                tempAfterAdjustDate = DateUtil.addDay(tempAfterAdjustDate, 1);
            }

            if (StringUtil.equals(DictCodeList.是否_否.getValue(), flag) && !StringUtil.equals(
                    DateUtil.format(tempAfterAdjustDate, "yyyyMM"),
                    DateUtil.format(beforeAdjustDate, "yyyyMM"))) {
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
    // modify by etliu 20210517 债券二级指令T+1场景全价计算错误问题修正 end


}
