/*
 * 文件名：InterestSubCal.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.common.calculator;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.asset.dto.AssetCommonInfoDto;
import com.joyintech.tams.valuation.common.asset.dto.FinCashPlanDetailDto;
import com.joyintech.tams.valuation.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.valuation.common.service.InstanceOfClass;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * 分段区间计算（付息）
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年10月17日
 * @see InterestSubCal
 * @since 1.0
 */
public class InterestSubCal {
    
    /**
     * 分段区间计算（付息）
     * @param is 付息计划
     * @param liy 利率报价列表
     * @param lirs 还本计划列表 计息截至日从小到大排序
     * @param scai 资产基本信息 资产代码 起息日 到期日 计息基础 利率类型 初始利率（或称票面利率） 币种 资产类型 本金 交易日历
     * @param papi 初始本金
     * @param <T> 泛型
     * @param class1 需要返回的类型
     * @return 付息计划分段列表
     * @see
     * @since 1.0
     */
    public static <T extends FinCashPlanDetailDto> List<T> cal(FinCashPlanDto is,
                                                              List<? extends FinCashCalcRule> liy,
                                                              List<FinCashPlanDto> lirs,
                                                              AssetCommonInfoDto scai,
                                                              BigDecimal papi, Class<T> class1) {
        // 【变量】本金 = 【入参】资产基本信息.本金 - 【入参】已还本金额
        BigDecimal papiSrc = papi;
        // 付息最后区间本金计算
        if (lirs != null) {
            for (FinCashPlanDto rs : lirs) {
                if (is.getVdate().compareTo(rs.getMdate()) <= 0 
                    && is.getMdate().compareTo(rs.getMdate()) > 0) {
                    papi = papi.subtract(rs.getPrinAmt());
                }
            }
        }
        List<T> lisd = new ArrayList<T>();
        T isd = InstanceOfClass.createInstance(class1);
        isd.setCashId(is.getCashId());
        isd.setVdate(is.getVdate());
        isd.setIntPrinAmt(papi);
        // 根据报价，拆分付息计划
        for (FinCashCalcRule iy : liy) {
            if (iy.getEffDate().compareTo(is.getVdate()) <= 0) {
                isd.setYield(iy.getYield().setScale(12, RoundingMode.HALF_UP));
            }
            else if (iy.getEffDate().compareTo(is.getVdate()) > 0 
                && iy.getEffDate().compareTo(is.getMdate()) < 0) {
                isd.setMdate(iy.getEffDate());
                lisd.add(isd);
                isd = InstanceOfClass.createInstance(class1);
                isd.setIntPrinAmt(papi);
                isd.setCashId(is.getCashId());
                isd.setVdate(iy.getEffDate());
                isd.setYield(iy.getYield().setScale(12, RoundingMode.HALF_UP));
            }
            else if(iy.getEffDate().compareTo(is.getMdate()) >= 0) {
                break;
            }
        }
        isd.setMdate(is.getMdate());
        lisd.add(isd);
        List<T> lisd2 = new ArrayList<T>();
        BigDecimal isRepayAmt=BigDecimal.ZERO;//初始化还本不带息区间的还本金额
        if (lirs != null) {
            for (FinCashPlanDto irs : lirs) {
                if (irs.getMdate().compareTo(is.getVdate()) > 0
                    && irs.getMdate().compareTo(is.getMdate()) < 0) {
                    if(StringUtil.equals(DictCodeList.是否_否.getValue(), irs.getIsPayInt())){
                        for (T t: lisd) {
                            if (t.getMdate().compareTo(irs.getMdate()) <=0) {
                                isd = InstanceOfClass.createInstance(class1);
                                isd.setIntPrinAmt(irs.getPrinAmt());
                                isd.setCashId(is.getCashId());
                                isd.setVdate(t.getVdate());
                                isd.setYield(t.getYield().setScale(12, RoundingMode.HALF_UP));
                                isd.setMdate(t.getMdate());
                                isRepayAmt=isRepayAmt.add(irs.getPrinAmt());//累计还本不带息区间的还本金额
                                isd.setRepayWithoutInt(isRepayAmt);
                                isd.setIsPayInt(DictCodeList.是否_否.getValue());
                                lisd2.add(isd);

                            } 
                            else if (t.getMdate().compareTo(irs.getMdate()) > 0) {
                                isd = InstanceOfClass.createInstance(class1);
                                isd.setIntPrinAmt(irs.getPrinAmt());
                                //isd.setIntPrinAmt(irs.getRepayAmt());
                                isd.setCashId(is.getCashId());
                                isd.setVdate(t.getVdate());
                                isd.setYield(t.getYield().setScale(12, RoundingMode.HALF_UP));
                                isd.setMdate(irs.getMdate());
                                isRepayAmt=isRepayAmt.add(irs.getPrinAmt());//累计还本不带息区间的还本金额
                                isd.setRepayWithoutInt(isRepayAmt);
                                isd.setIsPayInt(DictCodeList.是否_否.getValue());
                                lisd2.add(isd);
                            }
                            else {
                                break;
                            }
                        }
                    }
                }
                else if(irs.getMdate().compareTo(is.getMdate()) >= 0) {
                    break;
                }
            }
        }
        // 计息本金
        is.setIntPrinAmt(papi);
        is.setRepayAmt(papiSrc.subtract(papi));
       // is.setRepayWithoutInt(isRepayAmt);//累计还本不带息的本金
        // 当期利率
        is.setNewRate(lisd.get(lisd.size()-1).getYield());
        lisd.addAll(lisd2);
        return lisd;
    }

}
