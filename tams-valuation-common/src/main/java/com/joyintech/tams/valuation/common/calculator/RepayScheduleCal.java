/*
 * 文件名：RepayScheduleCal.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.common.calculator;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinCashPayRule;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.asset.dto.AssetCommonInfoDto;
import com.joyintech.tams.valuation.common.asset.dto.FinCashPlanDetailDto;
import com.joyintech.tams.valuation.common.asset.dto.FinCashPlanDto;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.List;

/**
 * 还本计划利息计算
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年10月17日
 * @see RepayScheduleCal
 * @since 1.0
 */
public class RepayScheduleCal {

    /**
     * 还本计划利息计算
     *
     * @param scai 资产基本信息
     * @param lirs 还本计划列表
     * @param is 付息规则
     * @param lyield 利率报价列表 （注：利率重置计划处理后的利率生效日利率值）
     * @param amt 初始金额
     * @param <T> 泛型
     * @param class1 需要返回的类型
     * @param classdetail 还本区间详细传 InvRpayscheDetail.class
     * @return 生成好的还本计划
     * @throws ParseException 
     * @see
     * @since 1.0
     */
    public static <T extends FinCashPlanDto> List<T> cal(AssetCommonInfoDto scai, List<T> lirs, FinCashPayRule is,
                           List<? extends FinCashCalcRule> lyield, BigDecimal amt, Class<T> class1, Class<? extends FinCashPlanDetailDto> classdetail) throws ParseException {

        BigDecimal curAmt = new BigDecimal(amt.toPlainString());
        for (FinCashPlanDto rsd : lirs) {
            // // 当期本金
            rsd.setCurPrin(curAmt);
            // 还本区间根据利率报价分段
            List<? extends FinCashPlanDetailDto> lisd = RepayScheduleSubCal.cal(rsd.getVdate(), rsd.getMdate(),
                lyield, null, rsd.getPrinAmt(), classdetail);
            rsd.setLirs(lisd);
            BigDecimal isamt = BigDecimal.ZERO;
            for (FinCashPlanDetailDto isd : lisd) {
                // 计算分段区间利息
                FinCashPlanDto isd2 = new FinCashPlanDto();
                isd2.setMdate(rsd.getMdate());
                isd2.setVdate(rsd.getVdate());
                isd2.setMdateTerm(rsd.getMdateTerm());
                isd2.setVdateTerm(rsd.getVdateTerm());
                FinCashPlanDetailDto isd3 = new FinCashPlanDetailDto();
                isd3.setIntPrinAmt(rsd.getPrinAmt());
                isd3.setYield(isd.getYield());
                isd3.setVdate(isd.getVdate());
                isd3.setMdate(isd.getMdate());
                BigDecimal isamtsub = BigDecimal.ZERO;
                // 【变量】还本计划.是否带息 = "否"
 //               if (StringUtil.equals(DictCodeList.是否_否.getValue(), rsd.getIsPayInt())) {
//                    isamtsub = BigDecimal.ZERO;
//                }
 //               else {
                    isamtsub = InterestSubAmtCal.cal(isd2, isd3, is, scai);
//                }
                isd.setIntAmt(isamtsub);
                isamt = ArithmeticUtil.doAdd(12, null, isamt, isamtsub);
            }
            if(StringUtil.equals(DictCodeList.是否_否.getValue(), rsd.getIsPayInt())){//业务场景，是还本不带息的
                rsd.setIntAmt(BigDecimal.ZERO);
                rsd.setRepayWithoutInt(isamt);
            }else {
            // 还本区间应计利息
            rsd.setIntAmt(isamt);
            rsd.setRepayWithoutInt(BigDecimal.ZERO);
            }
            rsd.setNewRate(lisd.get(lisd.size()-1).getYield());
            //本息和
            rsd.setCashAmt(ArithmeticUtil.doAdd(12, null, rsd.getPrinAmt(), rsd.getIntAmt()));
            // 当期本金
            curAmt = curAmt.subtract(rsd.getPrinAmt());
        }
        return lirs;
    }
}
