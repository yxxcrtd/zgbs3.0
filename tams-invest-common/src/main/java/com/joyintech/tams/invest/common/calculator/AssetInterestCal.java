/*
 * 文件名：AssetInterestCal.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月16日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.common.calculator;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.joyintech.tams.common.base.entity.FinCashPayRule;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.common.asset.dto.AssetCommonInfoDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;

/**
 * 资产区间计算
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年10月16日
 * @see AssetInterestCal
 * @since 1.0
 */
public class AssetInterestCal {

    /**
     * 资产区间计算
     * @param is  计划规则
     * @param scai 资产基本信息
     * @param holidayList 节假日列表
     * @param isFull 区间是否完整
     * @param class1 付息区间传 InvScheduleDto.class
     *               等额本息区间传 InvRepayScheduleDto.class
     * @param <T> 泛型
     * @return 计划列表
     * @see
     * @since 1.0
     */
    public static <T extends FinCashPlan> List<T> cal(FinCashPayRule is, AssetCommonInfoDto scai, List<String> holidayList,
                                                         boolean isFull, Class<T> class1) {
        // 付息频率转换
        String payCycle = is.getPayCycle();
        int cycle = 0;
        int cycleUnit = Calendar.MONTH;
        
        // 一体化对应etliu start
//        if (DictCodeList.支付频率_付息频率_月付.getValue().equals(payCycle)
//            ||StringUtil.equals(DictCodeList.支付频率_付息频率_每月等额还本.getValue(), payCycle)) {
//            cycle = 1;
//        }
//        else if (DictCodeList.支付频率_付息频率_季付.getValue().equals(payCycle)
//            ||StringUtil.equals(DictCodeList.支付频率_付息频率_每季等额还本.getValue(), payCycle)) {
//            cycle = 3;
//        }
//        else if (DictCodeList.支付频率_付息频率_半年付.getValue().equals(payCycle)
//            ||StringUtil.equals(DictCodeList.支付频率_付息频率_每半年等额还本.getValue(), payCycle)) {
//            cycle = 6;
//        }
//        else if (DictCodeList.支付频率_付息频率_年付.getValue().equals(payCycle)
//            ||StringUtil.equals(DictCodeList.支付频率_付息频率_每年等额还本.getValue(), payCycle)) {
//            cycle = 12;
//        }
        if (DictCodeList.频率_月.getValue().equals(payCycle)
            ||StringUtil.equals(DictCodeList.频率_每月等额还本.getValue(), payCycle)) {
            cycle = 1;
        }
        else if (DictCodeList.频率_季.getValue().equals(payCycle)
            ||StringUtil.equals(DictCodeList.频率_每季等额还本.getValue(), payCycle)) {
            cycle = 3;
        }
        else if (DictCodeList.频率_半年.getValue().equals(payCycle)
            ||StringUtil.equals(DictCodeList.频率_每半年等额还本.getValue(), payCycle)) {
            cycle = 6;
        }
        else if (DictCodeList.频率_年.getValue().equals(payCycle)
            ||StringUtil.equals(DictCodeList.频率_每年等额还本.getValue(), payCycle)) {
            cycle = 12;
        }
     // 一体化对应etliu end
        else {
            cycle = 0;
        }
        // 计算未调整付息节点
        List<Date[]> dates = IntervalDateSplit.cal(scai.getVdate(), scai.getMdate(), 
            is.getFirstPayDate(), is.getCalcRule(), cycle, cycleUnit, isFull);
        
        return cal2(scai, dates, is, holidayList, class1);
    }

    /**
     * 资产区间计算
     * @param scai 
     * @param dates 未调整付息节点
     * @param is  计划规则
     * @param holidayList 节假日列表
     * @param class1 付息区间传 InvScheduleDto.class
     *               等额本息区间传 InvRepayScheduleDto。class
     * @param <T> 泛型
     * @return 计划列表
     * @see
     * @since 1.0
     */
    public static <T extends FinCashPlan> List<T> cal2(AssetCommonInfoDto scai, List<Date[]> dates, FinCashPayRule is, 
                                                          List<String> holidayList, Class<T> class1) {
        // 付息计划列表
        List<T> res = new ArrayList<T>();
        // 循环处理
        T isd = InstanceOfClass.createInstance(class1);
        // 资产代码
        isd.setFinprodId(scai.getFinprodId());
        if(dates.size() > 1) {
            // 理论起始日
            isd.setVdateTerm(dates.get(0)[0]);
            // 实际起始日
            isd.setVdate(dates.get(0)[1]);
        }
        for(int i = 1; i<dates.size();i++) {
            Date[] date = dates.get(i);
            Date mdate = DateUtil.parse(AdjustedTradeDayCal.getAdjustedTradeDay(DateUtil.format(date[1]), is.getEndTdayRule(), holidayList));
            // 实际计息结束日调整
            isd.setMdate(mdate);
            // 理论付息区间不调整
            isd.setMdateTerm(date[0]);
            // 支付日调整
            if (is.getEndTdayRule().equals(is.getPayTdayRule())) {
                isd.setPayDate(mdate);
            }
            else {
                isd.setPayDate(DateUtil.parse(AdjustedTradeDayCal.getAdjustedTradeDay(DateUtil.format(date[1]), 
                    is.getPayTdayRule(), holidayList)));
            }
            // 调整前结束日
            isd.setMdateUnadjust(date[1]);
            res.add(isd);
            if (i == dates.size() - 1) {
                // 最后一条结束
                // 实际计息结束日调整
                isd.setMdate(date[1]);
                // 调整前结束日
                isd.setMdateUnadjust(date[1]);
                continue;
            }
            
            isd = InstanceOfClass.createInstance(class1);
            
            // 资产代码
            isd.setFinprodId(scai.getFinprodId());
            // 理论起始日
            isd.setVdateTerm(res.get(res.size()-1).getMdateTerm());
            // 实际起始日
            isd.setVdate(res.get(res.size()-1).getMdate());
            //计息基础
            if (!StringUtil.isNotEmpty(isd.getBasis())) {
                isd.setBasis(scai.getBasis());
            }
        }

        return res;
    }
}
