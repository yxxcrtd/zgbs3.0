/*
 * 文件名：SecCalUtil.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2020年5月19日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.bankuplink.calc.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.cib.sam.algo.sec.model.AlgInterScheduleBond;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.FinCashPlan;

/**
 * 算法工具类
 * 
 * @author zhangliang
 * @version 1.0 2020年5月19日
 * @see SecCalUtil
 * @since 1.0
 */
public class SecCalUtil {
    
    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
    
    public static final int SCAL_CASH = 2;
    
    public static final int SCAL_PRICE_SEC = 15;
    
    public static final int SCAL_PRICE = 12;

    /**
     * 付息计划类型转换
     * 
     * @param listSecScheduleModel 付息计划
     * @return 转换后的付息计划
     * @see
     * @since 1.0
     */
    public static List<AlgInterScheduleBond> convertSecScheduleModelToAlgInterScheduleBond(List<FinCashPlan> listSecScheduleModel){
        List<AlgInterScheduleBond> list_interScheduleBond = new ArrayList<AlgInterScheduleBond>();
        if (!StringUtil.isEmptyList(listSecScheduleModel)){
            for (FinCashPlan finCashPlan : listSecScheduleModel){
                AlgInterScheduleBond algInterScheduleBond = new AlgInterScheduleBond();
                algInterScheduleBond.setInterestStartDay(DateUtil.format(finCashPlan.getVdate()));
                algInterScheduleBond.setInterestEndDay(DateUtil.format(finCashPlan.getMdate()));
                algInterScheduleBond.setTotalInterestStartDay(DateUtil.format(finCashPlan.getVdateUnadjust()));
                algInterScheduleBond.setTotalInterestEndDay(DateUtil.format(finCashPlan.getMdateUnadjust()));
                algInterScheduleBond.setCashpay(StringUtil.isNotEmpty(finCashPlan.getCashAmt())?finCashPlan.getCashAmt().doubleValue():0);
                algInterScheduleBond.setCoupon(finCashPlan.getRangeYield().doubleValue());
                
                list_interScheduleBond.add(algInterScheduleBond);
            }
        }
        
        return list_interScheduleBond;
    }
}
