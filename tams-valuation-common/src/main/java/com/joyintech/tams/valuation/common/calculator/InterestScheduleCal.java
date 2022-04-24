/*
 * 文件名：InterestScheduleCal.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.common.calculator;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinCashPayRule;
import com.joyintech.tams.common.base.entity.FinRateInfo;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.asset.dto.AssetCommonInfoDto;
import com.joyintech.tams.valuation.common.asset.dto.FinCashPlanDetailDto;
import com.joyintech.tams.valuation.common.asset.dto.FinCashPlanDto;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;


/**
 * 付息计划生成
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年10月17日
 * @see InterestScheduleCal
 * @since 1.0
 */
public class InterestScheduleCal {

    /**
     * 付息计划生成
     * @param lis 付息计划列表 （传入的list会被修改）
     * @param scai 资产基本信息
                                                                                   资产代码
                                                                                    起息日
                                                                                    到期日
                                                                                    计息基础
                                                                                    利率类型
                                                                                    初始利率（或称票面利率）
                                                                                    币种
                                                                                    资产类型
                                                                                    本金
                                                                                    交易日历
     * @param lirs  还本计划列表
     * @param irr 利率重置规则
     * @param liy 利率报价列表
     * @param isr 付息规则
     * @param holidayList 节假日列表
     * @param papi 初始本金
     * @param classdetail 付息区间详细传 InvScheduleDetail.class
     * @param <T> 泛型
     * @return 付息计划
     * @throws ParseException 
     * @see
     * @since 1.0
     */
    public static <T extends FinCashPlanDto> List<T> interestScheduleCalA(List<T> lis, AssetCommonInfoDto scai,
                                                       List<FinCashPlanDto> lirs,
                                                       FinRateInfo irr, List<? extends FinCashCalcRule> liy, FinCashPayRule isr,
                                                       List<String> holidayList, BigDecimal papi, Class<? extends FinCashPlanDetailDto> classdetail) throws ParseException {
        // 分段区间计算
        for (T is :lis) {
            List<? extends FinCashPlanDetailDto> lisd = InterestSubCal.cal(is, liy, lirs, scai, papi, classdetail);
            papi = papi.subtract(is.getRepayAmt());
            //papi = papi.subtract(is.getIntPrinAmt());
            BigDecimal isamt = BigDecimal.ZERO;
            BigDecimal isRepayIntAmt = BigDecimal.ZERO;
            BigDecimal yield = BigDecimal.ZERO;
            BigDecimal intPrinAmt = BigDecimal.ZERO;
            for (FinCashPlanDetailDto isd : lisd) {
                InterestSubAmtCal.cal(is, isd, isr, scai);
                //计息本金
                intPrinAmt = isd.getIntPrinAmt();
                //区间利率
                yield = isd.getYield();
                isamt = ArithmeticUtil.doAdd(12,null,isamt,isd.getIntAmt());
                //intPrinAmt = ArithmeticUtil.subtract(isd.getIntPrinAmt(),intPrinAmt,12,null);
                if(isd.getIsPayInt()!=null && isd.getIsPayInt().equals(DictCodeList.是否_否.getValue())){//还本带息的
                    isRepayIntAmt=  ArithmeticUtil.doAdd(12,null,isRepayIntAmt,isd.getIntAmt());
                }
            }
            is.setYield(yield);
            is.setLirs(lisd);
            is.setIntAmt(isamt);
            is.setIntPrinAmt(intPrinAmt);
            is.setRepayWithoutInt(isRepayIntAmt);//累计还本不带息场景的利息
        }
        //减去区间内还本的利息
        /*for (T resultIs : lis) {
          //还本利息
            BigDecimal intAmt = BigDecimal.ZERO;
            for(FinCashPlanDto ir :lirs){
                if (resultIs.getVdate().compareTo(ir.getMdate()) <= 0 
                    && resultIs.getMdate().compareTo(ir.getMdate()) > 0) {
                    intAmt  = ArithmeticUtil.doAdd(12,null,intAmt,ir.getIntAmt());
                }
            }
            resultIs.setIntAmt(ArithmeticUtil.subtract(resultIs.getIntAmt(),intAmt,12,null));
        }*/
        return lis;
    }
}
