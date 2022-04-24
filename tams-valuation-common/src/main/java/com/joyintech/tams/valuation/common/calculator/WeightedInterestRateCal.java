/*
 * 文件名：WeightedInterestRateCal.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年11月28日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.common.calculator;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 *  加权利率计算
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年11月28日
 * @see WeightedInterestRateCal
 * @since 1.0
 */
public class WeightedInterestRateCal {

    /**
     * 
     * <功能详细描述>
     * @param liy   报价信息
     * @param sdate 区间开始日
     * @param edate 区间结束日
     * @return
     * @see
     * @since 1.0
     */
    public static BigDecimal cal(List<? extends FinCashCalcRule> liy,Date sdate,Date edate){
        Date d1 = null; Date d2 = null; BigDecimal r = BigDecimal.ZERO; BigDecimal x = BigDecimal.ZERO;
        BigDecimal R = BigDecimal.ZERO;
        for (FinCashCalcRule iy : liy) {
            //生效日<开始日
            if(DateUtil.compareDate(iy.getEffDate(), sdate)<1){
                d1 = sdate;
                r  = iy.getYield();
                     //生效日>开始日
            }else if(DateUtil.compareDate(iy.getEffDate(), sdate)==1){
                if(DateUtil.compareDate(iy.getEffDate(), edate)==-1){
                    d2 = iy.getEffDate();
                    x  = ArithmeticUtil.multiply(new BigDecimal(DateUtil.getBetweenDayNumber(d1, d2)),r);
                    R = ArithmeticUtil.doAdd(R,x);
                }else{
                    d2 = edate;
                    x  = ArithmeticUtil.multiply(new BigDecimal(DateUtil.getBetweenDayNumber(d1, d2)),r);
                    R = ArithmeticUtil.doAdd(R,x);
                    break;
                }
                r = iy.getYield();
                d1 = d2;
            }
        }
        if(null==d2){
            return r;
        }else if(DateUtil.compareDate(d2, edate)!=0){
            d2 = edate;
            x  = ArithmeticUtil.multiply(new BigDecimal(DateUtil.getBetweenDayNumber(d1, d2)),r);
            R = ArithmeticUtil.doAdd(R,x);
        }
        return ArithmeticUtil.divide(R, new BigDecimal(DateUtil.getBetweenDayNumber(sdate, edate)), 12);
    }
    
//    public static void main(String[] arg) throws ParseException{
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        ArrayList<FinCashCalcRule> temp = new ArrayList<FinCashCalcRule>();
//        FinCashCalcRule entity1 = new FinCashCalcRule();
//        FinCashCalcRule entity2 = new FinCashCalcRule();
//        FinCashCalcRule entity3 = new FinCashCalcRule();
//        entity1.setYield(new BigDecimal("0.12"));
//        entity2.setYield(new BigDecimal("0.11"));
//        entity3.setYield(new BigDecimal("0.15"));
//        entity1.setEffDate(sdf.parse("2018-12-30"));
//        entity2.setEffDate(sdf.parse("2019-01-03"));
//        entity3.setEffDate(sdf.parse("2018-01-13"));
//        temp.add(entity1);temp.add(entity2);temp.add(entity3);
//        BigDecimal R = cal(temp,sdf.parse("2018-12-30"),sdf.parse("2019-01-30"));
//        System.out.println(R);
//    }
}
