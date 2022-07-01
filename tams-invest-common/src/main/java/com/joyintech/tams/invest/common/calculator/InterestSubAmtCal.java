/*
 * 文件名：InterestSubAmtCal.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.common.calculator;

import com.joyintech.calculation.CalculatorFactory;
import com.joyintech.calculation.bonds.PaperIrReset;
import com.joyintech.calculation.bonds.SecIntSchedule;
import com.joyintech.calculation.bonds.constant.*;
import com.joyintech.calculation.shell.AccurIntForBookCalculator;
import com.joyintech.calculation.shell.param.AccurIntCalInParameter;
import com.joyintech.calculation.shell.param.AccurIntCalOutParameter;
import com.joyintech.calculation.shell.param.SecInfoModel;
import com.joyintech.calculation.util.BasicUtil;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.FinCashPayRule;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.asset.dto.AssetCommonInfoDto;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDetailDto;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 *  分段区间利息计算
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年10月17日
 * @see InterestSubAmtCal
 * @since 1.0
 */
public class InterestSubAmtCal {

    /**
     * 分段区间利息计算
     *
     * @param is 付息计划
     * @param isd 分段付息计划
     * @param scai 资产基本信息 资产代码 起息日 到期日 计息基础 利率类型 初始利率（或称票面利率） 币种 资产类型 本金 交易日历
     * @param isr 付息规则
     * @return 金额
     * @throws ParseException 
     * @see
     * @since 1.0
     */
    public static BigDecimal cal(FinCashPlanDto is, FinCashPlanDetailDto isd, FinCashPayRule isr,
                                 AssetCommonInfoDto scai) throws ParseException {

        BigDecimal amt = null;
        // 分段区间总天数
        BigDecimal dayCount = new BigDecimal(0);
        if (null != isd) {
            dayCount = CalendarUtil.calendarDays(isd.getVdate(), isd.getMdate());
        }
        // 计息基础
        String basis = "";
        if (null != is) {
            basis = StringUtil.isNotEmpty(is.getBasis())?is.getBasis():scai.getBasis();
        }
        // 计息基础天数计算
        BigDecimal basisDayCount = BasisDaysCal.cal(basis,
            isr == null ? null : isr.getCalcRule(),
            new Date[] {is == null ? null : is.getVdate(), scai.getVdate(), scai.getMdate(),
                isr == null ? null : isr.getFirstPayDate()});

        // 利息分配方式
        //add by  zhangnian 20191017 start
        //计划类利率标的和项目类标的 增加计息基础A_B_DAY
        //add by zhangnian 20191017  end
        String intpayRule = scai.getIntpayRule();
        if(StringUtil.equals(DictCodeList.计息基础_A_A.getValue(), basis)
        ||StringUtil.equals(DictCodeList.计息基础_A_A_按实际天数分配.getValue(), basis)){
            // 利息分配方式 == 平均分配
            if (DictCodeListExt1.利息分配方式_平均分配.getValue().equals(intpayRule)) {
                // 【变量】 理论计息区间总天数 = 【入参】分段付息区间.理论起始日 - 【入参】分段付息区间.理论结束日
                BigDecimal jxtsll = CalendarUtil.calendarDays(is==null?null:is.getVdateTerm(), is==null?null:is.getMdateTerm());
                if(jxtsll.compareTo(BigDecimal.ZERO)==0){
                    amt = BigDecimal.ZERO;
                }else{
                 // 计息频率
                    // 付息频率转换
                    String intpayCycle = isr==null?"":isr.getPayCycle();
                    BigDecimal cycle = BigDecimal.ZERO;
                    // 一体化对应etliu start
//                    if(StringUtil.equals(DictCodeList.支付频率_付息频率_月付.getValue(), intpayCycle)
//                        ||StringUtil.equals(DictCodeList.支付频率_付息频率_每月等额还本.getValue(), intpayCycle)){
//                        cycle = new BigDecimal(12);
//                    }
//                    else if (StringUtil.equals(DictCodeList.支付频率_付息频率_季付.getValue(), intpayCycle)
//                        ||StringUtil.equals(DictCodeList.支付频率_付息频率_每季等额还本.getValue(), intpayCycle)) {
//                        cycle = new BigDecimal(4);
//                    }
//                    else if (StringUtil.equals(DictCodeList.支付频率_付息频率_半年付.getValue(), intpayCycle)
//                        ||StringUtil.equals(DictCodeList.支付频率_付息频率_每半年等额还本.getValue(), intpayCycle)) {
//                        cycle = new BigDecimal(2);
//                    }
//                    else if (StringUtil.equals(DictCodeList.支付频率_付息频率_年付.getValue(), intpayCycle)
//                        ||StringUtil.equals(DictCodeList.支付频率_付息频率_每年等额还本.getValue(), intpayCycle)) {
//                        cycle = new BigDecimal(1);
//                    }
                    if(StringUtil.equals(DictCodeList.频率_月.getValue(), intpayCycle)
                        ||StringUtil.equals(DictCodeList.频率_每月等额还本.getValue(), intpayCycle)){
                        cycle = new BigDecimal(12);
                    }
                    else if (StringUtil.equals(DictCodeList.频率_季.getValue(), intpayCycle)
                        ||StringUtil.equals(DictCodeList.频率_每季等额还本.getValue(), intpayCycle)) {
                        cycle = new BigDecimal(4);
                    }
                    else if (StringUtil.equals(DictCodeList.频率_半年.getValue(), intpayCycle)
                        ||StringUtil.equals(DictCodeList.频率_每半年等额还本.getValue(), intpayCycle)) {
                        cycle = new BigDecimal(2);
                    }
                    else if (StringUtil.equals(DictCodeList.频率_年.getValue(), intpayCycle)
                        ||StringUtil.equals(DictCodeList.频率_每年等额还本.getValue(), intpayCycle)) {
                        cycle = new BigDecimal(1);
                    }
                 // 一体化对应etliu end
                    // 利息金额 = 【变量】 分段区间总天数/【变量】 理论计息区间总天数/【入参】分段付息区间规则.付息频率*计息本金*分段利率
                    //债券利随本清--到期还本
                    if(cycle.compareTo(BigDecimal.ZERO)==0){
                        // 利息金额 = 【变量】 分段区间总天数/【变量】计息基础天数*计息本金*分段利率
                        amt = dayCount.multiply(isd.getIntPrinAmt()).multiply(isd.getYield()).divide(
                            basisDayCount, 12, RoundingMode.HALF_UP); 
                    }else{
                        amt = dayCount.multiply(isd.getIntPrinAmt()).multiply(isd.getYield()).divide(cycle, 12,
                            RoundingMode.HALF_UP).divide(jxtsll, 12, RoundingMode.HALF_UP); 
                    }
                }
            }
            else if (DictCodeListExt1.利息分配方式_按照实际天数分配.getValue().equals(intpayRule)) {
                // 利息金额 = 【变量】 分段区间总天数/【变量】计息基础天数*计息本金*分段利率
                amt = dayCount.multiply(isd.getIntPrinAmt()).multiply(isd.getYield()).divide(
                    basisDayCount, 12, RoundingMode.HALF_UP);
            }
        }
        else if (DictCodeList.计息基础_A_360.getValue().equals(basis)
                 || DictCodeList.计息基础_A_365.getValue().equals(basis)) {
            // 利息金额 = 【变量】 分段区间总天数/【变量】计息基础天数*计息本金*分段利率
//            amt = dayCount.multiply(isd.getCapi()).multiply(isd.getYield()).divide(basisDayCount,
//                12, RoundingMode.HALF_UP);
            amt = ArithmeticUtil.multiply(dayCount, isd.getIntPrinAmt());
            amt = ArithmeticUtil.multiply(amt, isd.getYield());
            amt = ArithmeticUtil.divide(amt, basisDayCount, 12);
        }
        else if (DictCodeList.计息基础_A_365F.getValue().equals(basis)) {
            // 利息金额 = 【变量】 分段区间总天数/【变量】计息基础天数*计息本金*分段利率
//            Date tempDate1 = isd.getVdate(); 
//            Date tempDate2 = isd.getMdate();
//            if (CalendarUtil.isleapyear(isd.getVdate())) {
//                tempDate1 = CalendarUtil.set(isd.getVdate(), Calendar.DATE, 29);
//                tempDate1 = CalendarUtil.set(isd.getVdate(), Calendar.MONTH, 1);
//            }
//            else if (CalendarUtil.isleapyear(isd.getMdate())) {
//                tempDate2 = CalendarUtil.set(isd.getMdate(), Calendar.DATE, 29);
//                tempDate2 = CalendarUtil.set(isd.getMdate(), Calendar.MONTH, 1);
//            }
            // 分段区间总天数 = 分段区间总天数 -1
//            if (tempDate1.compareTo(isd.getVdate()) != 0 || tempDate2.compareTo(isd.getMdate()) != 0) {
//                dayCount = dayCount.subtract(BigDecimal.ONE);
//            }
        	Date tempDate1 = null;
			if (CalendarUtil.isleapyear(isd.getVdate())) {
				tempDate1 = CalendarUtil.set(isd.getVdate(), Calendar.DATE, 29);
				tempDate1 = CalendarUtil.set(tempDate1, Calendar.MONTH, 1);
			} else if (CalendarUtil.isleapyear(isd.getMdate())) {
				tempDate1 = CalendarUtil.set(isd.getMdate(), Calendar.DATE, 29);
				tempDate1 = CalendarUtil.set(tempDate1, Calendar.MONTH, 1);
			}
			if (tempDate1 != null && tempDate1.compareTo(isd.getVdate()) >= 0
					&& tempDate1.compareTo(isd.getMdate()) < 0) {
				dayCount = dayCount.subtract(BigDecimal.ONE);
			}
            
            // 利息金额 = 【变量】分段区间总天数/【变量】计息基础天数*计息本金*分段利率
            amt = dayCount.multiply(isd.getIntPrinAmt()).multiply(isd.getYield()).divide(
                basisDayCount, 12, RoundingMode.HALF_UP);
        }
        else if (DictCodeList.计息基础_30_360.getValue().equals(basis)) {
            // 实际起始日到分段起始日年化计息天数计算
            BigDecimal temp1 = InterestDaysCal30T365.basis30T365(is==null?null:is.getVdate(), isd==null?null:isd.getVdate());
            // 实际起始日到分段结束日年化计息天数计算
            BigDecimal temp2 = InterestDaysCal30T365.basis30T365(is==null?null:is.getVdate(), isd==null?null:isd.getMdate());

            // 利息金额 = （分段结束年化计算天数 - 分段起始年化计算天数 ）*计息本金*分段利率
            amt = (temp2.subtract(temp1)).multiply(isd.getIntPrinAmt()).multiply(isd.getYield());
        }
        //add by zhangnian 20191121 start 调用算法包
        else if(StringUtil.equals(DictCodeListExt1.计息基础_A_A_D.getValue(), basis)
        ||StringUtil.equals(DictCodeListExt3.计息基础_30U_360.getValue(), basis)){
            if(dayCount.compareTo(BigDecimal.ZERO)==0){
                amt =  BigDecimal.ZERO;
            }else{
                AccurIntCalOutParameter out = getCalculate(is,isd,isr,scai,basis);
                if(null!=out){
                    amt = out.getInterest();
                }
            }
        }
        //add by  zhangnian 20191121 end
        else {
            // 利息金额 = 【变量】 分段区间总天数/【变量】计息基础天数*计息本金*分段利率
            BigDecimal  tempYield = isd.getYield();
            if (tempYield == null) {
                tempYield = BigDecimal.ZERO;
            }
            amt = dayCount.multiply(isd.getIntPrinAmt()).multiply(tempYield).divide(basisDayCount,
                12, RoundingMode.HALF_UP);
        }
        if (amt != null) {
            amt = amt.setScale(12, RoundingMode.HALF_UP);
        }
        isd.setIntAmt(amt);
        return amt;
    }

    /**
     * A_A_D 计算利息 调用算法包
     * @return
     */
    public static AccurIntCalOutParameter getCalculate(FinCashPlanDto is, FinCashPlanDetailDto isd, FinCashPayRule isr,
                                                 AssetCommonInfoDto scai,String basis){
        BigDecimal faceValue = new BigDecimal("100");// 百元券面 = 100
        AccurIntCalInParameter parameter = new AccurIntCalInParameter();
        parameter.setCalDate(scai.getVdate().compareTo(isd.getMdate()) > 0
                ? BasicUtil.dateToLocal(scai.getVdate()) : BasicUtil.dateToLocal(isd.getMdate()).minusDays(1L));
        // 资产信息
        SecInfoModel secInfoModel = new SecInfoModel();
        secInfoModel.setStartDate(BasicUtil.dateToLocal(scai.getVdate()));
        secInfoModel.setEndDate(BasicUtil.dateToLocal(scai.getMdate()));
        secInfoModel.setBondsType(Variety.附息);
        secInfoModel.setFirstPayDate(BasicUtil.dateToLocal(null == isr ? null : isr.getFirstPayDate()));
        if(StringUtil.equals(DictCodeListExt1.计息基础_A_A_D.getValue(), basis)){
            secInfoModel.setBasis(Basis.A_A_D);
        }
        else if(StringUtil.equals(DictCodeListExt3.计息基础_30U_360.getValue(), basis)){
            secInfoModel.setBasis(Basis._30U_360);
        }
        // 利率类型
        secInfoModel.setIntRateType(IntRateType.浮动利率);
        // 利息分配方式
        AllocationType allocationType =  AllocationType.按实际天数分配;
        secInfoModel.setAllocationType(allocationType);
        // 付息计划推算方式
        SecIntSchCalMethod secIntSchCalMethod = null;
        if (DictCodeList.推算方式_起息日向后.getValue().equals(isr.getCalcRule())) {
            secIntSchCalMethod = SecIntSchCalMethod.起息日向后;
        }
        else if (DictCodeList.推算方式_到期日向前.getValue().equals(isr.getCalcRule())) {
            secIntSchCalMethod = SecIntSchCalMethod.到期日向前;
        }
        else if (DictCodeList.推算方式_首次支付日.getValue().equals(isr.getCalcRule())) {
            secIntSchCalMethod = SecIntSchCalMethod.首次付息日;
        }
        secInfoModel.setCalMethod(secIntSchCalMethod);
        // 付息频率
        IntFrequency yearIntRate = null;
        if (DictCodeList.频率_月.getValue().equals(isr.getPayCycle())) {
            yearIntRate = IntFrequency.月付;
        }
        else if (DictCodeList.频率_季.getValue().equals(isr.getPayCycle())) {
            yearIntRate = IntFrequency.季付;
        }
        else if (DictCodeList.频率_年.getValue().equals(isr.getPayCycle())) {
            yearIntRate = IntFrequency.年付;
        }
        else if (DictCodeList.频率_半年.getValue().equals(isr.getPayCycle())) {
            yearIntRate = IntFrequency.半年付;
        }
        secInfoModel.setYearIntRate(yearIntRate);
        parameter.setSecInfoModel(secInfoModel);
        // 付息计划列表
        List<SecIntSchedule> secIntScheduleList = new ArrayList<SecIntSchedule>();
        SecIntSchedule secIntSchedule = new SecIntSchedule();
        // 理论起始日
        secIntSchedule.setTheoryStartDate(
                isd.getVdateTerm() == null ? BasicUtil.dateToLocal(
                        isd.getVdate()) : BasicUtil.dateToLocal(
                        isd.getVdateTerm()));
        // 理论截止日
        secIntSchedule.setTheoryCutoffDate(
                isd.getMdateTerm() == null ? BasicUtil.dateToLocal(
                        isd.getMdate()) : BasicUtil.dateToLocal(
                        isd.getMdateTerm()));
        // 实际起始日
        secIntSchedule.setActualStartDate(
                BasicUtil.dateToLocal(isd.getVdate()));
        // 实际截止日
        secIntSchedule.setActualCutoffDate(
                BasicUtil.dateToLocal(isd.getMdate()));
        // 计息本金
        secIntSchedule.setCorpus(new BigDecimal(100));
        // 利息金额
        secIntSchedule.setAmount(isd.getIntPrinAmt());
        secIntScheduleList.add(secIntSchedule);
        parameter.setSecIntScheduleList(secIntScheduleList);
        // 报价信息
        List<PaperIrReset> paperIrResetList = new ArrayList<PaperIrReset>();
        PaperIrReset paperIrReset = new PaperIrReset();
        // 票面利率生效日
        paperIrReset.setParRateEffDate(
                BasicUtil.dateToLocal(isd.getVdate()));
        // 票面利率
        paperIrReset.setParRate(isd.getYield());
        paperIrResetList.add(paperIrReset);
        parameter.setPaperIrResetList(paperIrResetList);
        // 当前面值
        parameter.setPaperAmt(faceValue);
        // 计算百元面值的债券的利息
        AccurIntForBookCalculator calculator2 = CalculatorFactory.createAccurIntForBookCalculator();
        AccurIntCalOutParameter output2 = calculator2.calculate(parameter);
        if(null!=output2){
            BigDecimal amt = ArithmeticUtil.multiply(
                    output2.getInterest(), isd.getIntPrinAmt());
            amt = ArithmeticUtil.divide(amt, faceValue,12).setScale(12, RoundingMode.HALF_UP);
            output2.setInterest(amt);
        }
        return output2;
    }
}
