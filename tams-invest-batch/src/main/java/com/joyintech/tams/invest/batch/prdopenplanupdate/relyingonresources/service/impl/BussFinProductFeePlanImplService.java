package com.joyintech.tams.invest.batch.prdopenplanupdate.relyingonresources.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.batch.prdopenplanupdate.relyingonresources.dto.BussFinProductFeePlanDto;
import com.joyintech.tams.invest.batch.prdopenplanupdate.relyingonresources.dto.DateDiviDto;
import com.joyintech.tams.invest.batch.prdopenplanupdate.relyingonresources.service.BussFinProductFeePlanService;
import com.joyintech.tams.invest.batch.susprdfutureopenplangen.relyingonresources.InvDateCalService;
import com.joyintech.tams.invest.common.calculator.AdjustedTradeDayCal;


@Service("batchBussFinProductFeePlanImplService")
public class BussFinProductFeePlanImplService extends BaseService<BussFinProductFeePlanDto> implements BussFinProductFeePlanService{
	/**
     * 付息计划，开放计划，利率重置，申购计划，赎回计划计算用和日期关联的Service
     */
    @Autowired
    private InvDateCalService invDateCalService;
    
    @Override
    public List<FinCashPlan> getProductFeePlanData(BussFinProductFeePlanDto finProductFeePlanDto) {
        // 上记以外的情况，代表是画面上点了【预览】按钮触发的事件，需要重新计算付费计划
        List<FinCashPlan> resultList = new ArrayList<FinCashPlan>();

        // 分割周期
        String cycle = "";
        // 分割周期单位
        String cycleUnit = "";

        // 根据付费规则判断分割周期、分割周期单位
     // 一体化对应etliu start
//        if (StringUtil.equals(finProductFeePlanDto.getPayFeeRule(),
//            DictCodeList.付费规则_月.getValue())) {
//            cycle = "1";
//            cycleUnit = DictCodeListExt1.周期单位_月.getValue();
//        }
//        else if (StringUtil.equals(finProductFeePlanDto.getPayFeeRule(),
//            DictCodeList.付费规则_季.getValue())) {
//            cycle = "3";
//            cycleUnit = DictCodeListExt1.周期单位_月.getValue();
//        }
//        else if (StringUtil.equals(finProductFeePlanDto.getPayFeeRule(),
//            DictCodeList.付费规则_半年.getValue())) {
//            cycle = "6";
//            cycleUnit = DictCodeListExt1.周期单位_月.getValue();
//        }
//        else if (StringUtil.equals(finProductFeePlanDto.getPayFeeRule(),
//            DictCodeList.付费规则_年.getValue())) {
//            cycle = "1";
//            cycleUnit = DictCodeListExt1.周期单位_年.getValue();
//        }
//        else if (StringUtil.equals(finProductFeePlanDto.getPayFeeRule(),
//            DictCodeList.付费规则_到期.getValue())) {
//            cycle = "0";
//        }
        if(StringUtil.equals(finProductFeePlanDto.getPayFeeRule(),
            DictCodeList.频率_周.getValue())){
            cycle = "1";
            cycleUnit = DictCodeListExt1.周期单位_周.getValue();
        }
        else if (StringUtil.equals(finProductFeePlanDto.getPayFeeRule(),
            DictCodeList.频率_月.getValue())) {
            cycle = "1";
            cycleUnit = DictCodeListExt1.周期单位_月.getValue();
        }
        else if (StringUtil.equals(finProductFeePlanDto.getPayFeeRule(),
            DictCodeList.频率_季.getValue())) {
            cycle = "3";
            cycleUnit = DictCodeListExt1.周期单位_月.getValue();
        }
        else if (StringUtil.equals(finProductFeePlanDto.getPayFeeRule(),
            DictCodeList.频率_半年.getValue())) {
            cycle = "6";
            cycleUnit = DictCodeListExt1.周期单位_月.getValue();
        }
        else if (StringUtil.equals(finProductFeePlanDto.getPayFeeRule(),
            DictCodeList.频率_年.getValue())) {
            cycle = "1";
            cycleUnit = DictCodeListExt1.周期单位_年.getValue();
        }
        else if (StringUtil.equals(finProductFeePlanDto.getPayFeeRule(),
            DictCodeList.频率_到期一次.getValue())) {
            cycle = "0";
        }
     // 一体化对应etliu end

        // 获取节假日 到期日前后顺延30天
        List<String> dealCalendarHolidayList = invDateCalService.getDealCalendarHoliday(
            DateUtil.addDay(finProductFeePlanDto.getVdate(), -30),
            DateUtil.addDay(finProductFeePlanDto.getMdate(), 30),
            finProductFeePlanDto.getDealCalId());
        //因为计提结束日也计提,划分区间时需要 计提结束日+1 add by chendijun 2019/10/28
        Date adjustEAccrDate = DateUtil.parse(AdjustedTradeDayCal.getAfterOrBeforeDay(
            DateUtil.format(finProductFeePlanDto.geteAccrDate()),1,true));
        //首次计费起始日到首次计费结束日
        if (StringUtil.isNotEmpty(finProductFeePlanDto.getFirstCalEndDate())) {
            
            // 首次计提结束日根据营业日准则进行调整
            Date adjustFirstCalEndDate = DateUtil.parse(
                AdjustedTradeDayCal.getAdjustedTradeDay(DateUtil.format(finProductFeePlanDto.getFirstCalEndDate()),
                    finProductFeePlanDto.getCalcFeeTradeRule(), dealCalendarHolidayList));
            finProductFeePlanDto.setFirstCalEndDate(adjustFirstCalEndDate);

            // 支付日根据营业日准则进行调整
            Date adjustPayDate = DateUtil.parse(
                AdjustedTradeDayCal.getAdjustedTradeDay(DateUtil.format(finProductFeePlanDto.getFirstCalEndDate()),
                    finProductFeePlanDto.getPayFeeTradeRule(), dealCalendarHolidayList));
            
            FinCashPlan resultDto = new FinCashPlan();
            resultDto.setCashNum((long)(1));
            resultDto.setCashType(DictCodeListExt1.现金流一级类型_费用.getValue());
            resultDto.setVdate(finProductFeePlanDto.getVdate());
            resultDto.setMdate(finProductFeePlanDto.getFirstCalEndDate());
            resultDto.setVdateUnadjust(finProductFeePlanDto.getVdate());
            resultDto.setMdateUnadjust(finProductFeePlanDto.getFirstCalEndDate());
            
            resultDto.setPayDate(adjustPayDate);
            resultDto.setPayDateUnadjust(adjustPayDate);
            resultDto.setCashId(finProductFeePlanDto.getCashId());
            resultDto.setFinprodId(finProductFeePlanDto.getFinprodId());
            resultDto.setFinprodType("F16");
            resultDto.setFinprodType2("F16");
            resultDto.setBranch(0L);
            resultList.add(resultDto);
            
            finProductFeePlanDto.setVdate(finProductFeePlanDto.getFirstCalEndDate());
            //如果首次计费结束日大于等于计提结束日+1天时 只取第一条（大于计提结束日即可）
            if(DateUtil.compareDate(finProductFeePlanDto.getFirstCalEndDate(), adjustEAccrDate) >= 0){
                return resultList;
            }
        }

        // 日期区间分割
        List<DateDiviDto> dateIntervalDivisionList = this.dateIntervalDivision(
            finProductFeePlanDto.getVdate(), adjustEAccrDate, cycle, cycleUnit);
        Date endDateTmp  = null;
        // 根据调整规则调整
        for (int count = 0; count < dateIntervalDivisionList.size(); count++ ) {
            DateDiviDto dto = dateIntervalDivisionList.get(count);
            // add by yangjiaming 20180314 关于产品付费计划的计算方法调整 start
            /*
             * 如果不是最后一条付费计划: 浮动管理费时： 计划中计提截止日 = 区间结束日,计划支付日 = 区间结束日的下一工作日 非浮动管理费时：计划中计提截止日 =
             * 区间结束日,计划支付日 = 区间结束日根据营业日准则调整
             */

            // 计提结束日不会根据营业日准则顺延
            Date startDate = dto.getStartDate();
            Date endDate = dto.getEndDate();
            Date tradePaymentDay = dto.getEndDate();
            if(count > 0 && startDate.compareTo(finProductFeePlanDto.getVdate()) != 0 
                && (!StringUtil.isNotEmpty(finProductFeePlanDto.getFirstCalEndDate())||startDate.compareTo(finProductFeePlanDto.getFirstCalEndDate()) != 0)){
                startDate = DateUtil.parse(
                    AdjustedTradeDayCal.getAdjustedTradeDay(DateUtil.format(startDate),
                        finProductFeePlanDto.getCalcFeeTradeRule(), dealCalendarHolidayList));
            }
            if (!StringUtil.isNotEmpty(finProductFeePlanDto.getFirstCalEndDate())||endDate.compareTo(finProductFeePlanDto.getFirstCalEndDate()) != 0) {
                endDate = DateUtil.parse(
                    AdjustedTradeDayCal.getAdjustedTradeDay(DateUtil.format(endDate),
                        finProductFeePlanDto.getCalcFeeTradeRule(), dealCalendarHolidayList));
            }
            
            //如果调整后的区间开始大于计提结束日+1,就直接跳出  add by chendijun 2019/10/28
            if(startDate.compareTo(adjustEAccrDate)>=0) {
                return resultList;
            }
            
            //当调整后的结束日与上一条结束日相等时，跳过 add by wm start 2019-10-28
            if(endDateTmp!= null && endDateTmp.compareTo(endDate)>=0){
                continue;
            } 
            //当调整后的开始日与结束日相等时，跳过 add by wm end 2019-10-28
            
            //如果调整后的区间结束日大于计提结束日+1，那么区间结束日就等于计提结束日+1
            if(endDate.compareTo(adjustEAccrDate)>0) {
                endDate = adjustEAccrDate;
            }
            endDateTmp = endDate;
            

            // 支付日根据营业日准则进行调整
            tradePaymentDay = DateUtil.parse(
                AdjustedTradeDayCal.getAdjustedTradeDay(DateUtil.format(endDate),
                    finProductFeePlanDto.getPayFeeTradeRule(), dealCalendarHolidayList));

            FinCashPlan resultDto = new FinCashPlan();
            resultDto.setCashNum((long) (resultList.size() + 1));
            resultDto.setCashType(DictCodeListExt1.现金流一级类型_费用.getValue());
            resultDto.setVdate(startDate);
            resultDto.setMdate(endDate);
            resultDto.setPayDate(tradePaymentDay);
            resultDto.setVdateUnadjust(startDate);
            resultDto.setMdateUnadjust(endDate);
            resultDto.setPayDateUnadjust(tradePaymentDay);
            resultDto.setCashId(finProductFeePlanDto.getCashId());
            resultDto.setFinprodId(finProductFeePlanDto.getFinprodId());
            resultDto.setFinprodType("F16");
            resultDto.setFinprodType2("F16");
            resultDto.setBranch(0L);
            resultList.add(resultDto);
        }

        return resultList;
    }
    
    /**
     * 日期区间分割
     *
     * @param paraMap 计算参数map
     * @return 分割后开始日结束日的DTO列表
     * @throws Exception 例外
     * @see
     * @since 1.0
     */
    public List<DateDiviDto> dateIntervalDivision(Date startDate, Date endDate, String cycle,
                                                      String cycleUnit) {
        List<DateDiviDto> list = new ArrayList<DateDiviDto>();

        // 如果cycle为‘0’，则表示为到期一次性付费
        if (StringUtil.equals("0", cycle)) {
            DateDiviDto dto = new DateDiviDto();
            dto.setStartDate(startDate);
            dto.setEndDate(endDate);
            list.add(dto);
        }
        // 其他情况按周期分割
        else {
            Date startDateTemp = startDate;
            int count = 1;
            while (true) {
                if (DateUtil.compareDate(startDateTemp, endDate) < 0) {
                    Date calcDate = invDateCalService.naturalDateCalc(startDate, cycleUnit,
                        Integer.parseInt(cycle) * count);
                    if (DateUtil.compareDate(calcDate, endDate) < 0) {
                        DateDiviDto dto = new DateDiviDto();
                        dto.setStartDate(startDateTemp);
                        dto.setEndDate(calcDate);
                        list.add(dto);
                        startDateTemp = calcDate;

                        count++ ;
                    }
                    else {
                        DateDiviDto dto = new DateDiviDto();
                        dto.setStartDate(startDateTemp);
                        dto.setEndDate(endDate);
                        list.add(dto);
                        break;
                    }
                } else {
                    break; 
                }
            }
            // 按区间结束日升序排序
            Collections.sort(list);
        }

        return list;
    }

}
