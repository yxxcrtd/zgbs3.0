/*
 * 文件名：RepayScheduleSubCal.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.common.calculator;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.asset.dto.FinCashPlanDetailDto;
import com.joyintech.tams.valuation.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.valuation.common.service.InstanceOfClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 还本计划分段区间计算
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年10月17日
 * @see RepayScheduleSubCal
 * @since 1.0
 */
public class RepayScheduleSubCal {

    /**
     * 分段区间计算
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param liy 利率报价列表
     * @param lirs 还本计划列表 计息截至日从小到大排序
     * @param amt 初始本金
     * @param <T> 泛型
     * @param class1 需要返回的类型
     * @return 计划分段列表
     * @see
     * @since 1.0
     */
    public static <T extends FinCashPlanDetailDto> List<T> cal(Date startDate, Date endDate,
                                                              List<? extends FinCashCalcRule> liy,
                                                              List<FinCashPlanDto> lirs,
                                                              BigDecimal amt,
                                                              String isUp, Class<T> class1) {
        // 【变量】本金 =
        BigDecimal papi = amt;

        List<T> res = new ArrayList<T>();
        List<T> lisd = new ArrayList<T>();
        T isd = InstanceOfClass.createInstance(class1);
        isd.setVdate(startDate);
        isd.setPrinAmt(papi);
        // 根据报价，拆分付息计划
        for (FinCashCalcRule iy : liy) {

//            if (iy.getStartDate().compareTo(startDate) <= 0) {
            if (DateUtil.compareDate(iy.getEffDate(), startDate) <= 0) {
                isd.setYield(iy.getYield().setScale(12, RoundingMode.HALF_UP));
            }
//            else if (iy.getStartDate().compareTo(startDate) > 0
//                && iy.getStartDate().compareTo(endDate) < 0) {
            else if (DateUtil.compareDate(iy.getEffDate(), startDate) > 0
                && DateUtil.compareDate(iy.getEffDate(), endDate) < 0) {
                isd.setMdate(iy.getEffDate());
                lisd.add(isd);
                isd = InstanceOfClass.createInstance(class1);
                isd.setIntPrinAmt(papi);
                isd.setVdate(iy.getEffDate());
                isd.setYield(iy.getYield().setScale(12, RoundingMode.HALF_UP));
            }
//            else if(iy.getStartDate().compareTo(endDate) >= 0) {
            else if (DateUtil.compareDate(iy.getEffDate(), endDate) >= 0) {
                break;
            }
        }

        isd.setMdate(endDate);
        lisd.add(isd);
        // 根据还本计划，拆分“已拆分的付息计划”
        for (T tempisd : lisd) {
            T isd2 = InstanceOfClass.createInstance(class1);
            isd2.setVdate(tempisd.getVdate());
            isd2.setYield(ArithmeticUtil.round(tempisd.getYield(), 12, RoundingMode.HALF_UP));
            isd2.setIntPrinAmt(papi);
            // 根据还本，拆分付息计划
            if (lirs != null) {
                for (FinCashPlanDto irs : lirs) {
                    if (irs.getMdate().compareTo(tempisd.getVdate()) == 0) {
                        if("0".equals(isUp)) {
                            papi = papi.subtract(irs.getRepayAmt());
                        }
                        else {
                            papi = irs.getRepayAmt();
                        }
                    }
                    if (irs.getMdate().compareTo(tempisd.getVdate()) > 0
                        && irs.getMdate().compareTo(tempisd.getMdate()) < 0) {
                        if (irs.getIsPayInt() == null ||StringUtil.equals(DictCodeList.是否_否.getValue(), irs.getIsPayInt())) {
                            isd2.setPrinAmt(papi);
                            isd2.setYield(ArithmeticUtil.round(tempisd.getYield(), 12, RoundingMode.HALF_UP));
                            isd2.setMdate(irs.getMdate());
                            res.add(isd2);
                            isd2 = InstanceOfClass.createInstance(class1);
                            isd2.setVdate(irs.getMdate());
                        }
                        if("0".equals(isUp)) {
                            papi = papi.subtract(irs.getRepayAmt());
                        }
                        else {
                            papi = irs.getRepayAmt();
                        }
                    }
                    else if(irs.getMdate().compareTo(tempisd.getMdate()) >= 0) {
                        break;
                    }
                }
            }
            isd2.setYield(ArithmeticUtil.round(tempisd.getYield(), 12, RoundingMode.HALF_UP));
            isd2.setPrinAmt(papi);
            isd2.setMdate(tempisd.getMdate());
            res.add(isd2);
        }
        return res;
    }
    /**
     * 分段区间计算
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param liy 利率报价列表
     * @param lirs 还本计划列表 计息截至日从小到大排序
     * @param amt 初始本金
     * @param <T> 泛型
     * @param class1 需要返回的类型
     * @return 计划分段列表
     * @see
     * @since 1.0
     */
    public static <T extends FinCashPlanDetailDto> List<T> cal(Date startDate, Date endDate,
                                                              List<? extends FinCashCalcRule> liy,
                                                              List<FinCashPlanDto> lirs,
                                                              BigDecimal amt, Class<T> class1) {
        return cal(startDate, endDate, liy, lirs, amt, "0", class1);
    }
}
