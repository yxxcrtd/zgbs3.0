/*
 * 文件名：BussFinProductPayPlanImplService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：何阳阳
 * 修改时间：2018年9月3日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.prdopenplanupdate.relyingonresources.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.batch.prdopenplanupdate.relyingonresources.dto.BussFinProductPayPlanDto;
import com.joyintech.tams.invest.batch.prdopenplanupdate.relyingonresources.service.BussFinProductPayPlanService;
import com.joyintech.tams.invest.batch.susprdfutureopenplangen.relyingonresources.InvDateCalService;
import com.joyintech.tams.invest.common.calculator.AdjustedTradeDayCal;
import com.joyintech.tams.invest.common.constant.BussinessConstant;

/**
 * 付息计划组件实现类
 * 
 * @author 何阳阳
 * @version 1.0 2018年9月3日
 * @see BussFinProductPayPlanImplService
 * @since 1.0
 */
@Service("batchBussFinProductPayPlanImplService")
public class BussFinProductPayPlanImplService extends BaseService<BussFinProductPayPlanDto> implements BussFinProductPayPlanService {

    /**
     * 付息计划，开放计划，利率重置，申购计划，赎回计划计算用和日期关联的Service
     */
    @Autowired
    private InvDateCalService invDateCalService;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FinCashPlan> getProductInterestPlanData(BussFinProductPayPlanDto inputDto) {

        /*
         * if (StringUtil.isNotEmpty(inputDto.getTempletId())) { //
         * 模板id不为空时，表示是画面的模板下拉框选择值变更后，从模板表查询数据 String sqlId = "selectByTempleteId";
         * paramMap.put("templetId", inputDto.getTempletId()); resultData =
         * super.queryForPage(ProductInterestPlanMapper.class, sqlId, paramMap);
         * resultData.setMessage(null); return resultData; } else if
         * (StringUtil.isNotEmpty(inputDto.getWorkflowId())) { // 工作流id不为空时，代表是画面是修改或者查看模式，从产品表查询数据
         * String sqlId = "selectByWorkflowId"; paramMap.put("workflowId",
         * inputDto.getWorkflowId()); if (StringUtil.isNotEmpty(inputDto.getHisNo())) { //
         * ※当参数.履历No非空时，检索对象表为以下所相应的履历表 sqlId = "selectByWorkflowIdFromHis"; paramMap.put("hisNo",
         * inputDto.getHisNo()); } resultData = super.queryForPage(ProductInterestPlanMapper.class,
         * sqlId, paramMap); resultData.setMessage(null); return resultData; }
         */

        // 上记以外的情况，代表是画面上点了【预览】按钮触发的事件，需要重新计算付息计划
        // check输入参数
        return calcProductInterestPlan(inputDto);
    }

    /**
     * 计算生成付息计划
     * 
     * @param inputDto 画面输入dto
     * @return 付息计划列表
     */
    private List<FinCashPlan> calcProductInterestPlan(BussFinProductPayPlanDto inputDto) {
        List<FinCashPlan> resultList = new ArrayList<>();
        // 区间开始日
        Date startDate = inputDto.getVdate();
        // 区间结束日
        Date endDate = inputDto.getMdate();
        //产品首次付息日最多允许推迟年数
        int year = StringUtil.toInt(CacheUtil.getSysParam(BussinessConstant.MAX_AFTER_NOW_YEAR));
        //允许可生成最大区间结束日
        Date endDateMax = DateUtil.addYear(inputDto.getVdate(), year);      
            //如果到期日大于允许可生成最大区间结束日  取允许可生成最大区间结束日 
            if(DateUtil.compareDate(endDate, endDateMax)>0){
                inputDto.setMdate(endDateMax);
            }else {
            	inputDto.setMdate(endDate);
            }

        // 交易日历取得
        List<String> dealCalendarHolidayList = invDateCalService.getDealCalendarHoliday(startDate,
            DateUtil.addDay(endDate, 8), inputDto.getCalendarId());

        // 付息计划区间生成
        if (StringUtil.equals(DictCodeListExt1.付息周期_到期支付.getValue(), inputDto.getPayCycle())) {
            // 付息规则选择为到期时
            Date intPeriodStartDate = inputDto.getVdate(); // 计息区间开始日
            Date intPeriodEndDate = endDate; // 计息区间结束日
            Date winupDate = inputDto.getWinupDate();// 清盘日
            // 支付日
            String payDate = AdjustedTradeDayCal.getAdjustedTradeDay(
                DateUtil.format(intPeriodEndDate), inputDto.getPayTdayRule(),
                dealCalendarHolidayList);

            FinCashPlan resultDto = new FinCashPlan();
            resultDto.setVdate(intPeriodStartDate);
            resultDto.setMdate(intPeriodEndDate);
            if (StringUtil.isNotEmpty(winupDate)) {
                String tempAdjustDate = AdjustedTradeDayCal.getAdjustedTradeDay(
                    DateUtil.format(winupDate), inputDto.getPayTdayRule(),
                    dealCalendarHolidayList);

                resultDto.setPayDate(DateUtil.parse(tempAdjustDate));
            }
            else {
                resultDto.setPayDate(DateUtil.parse(payDate));
            }
            // 调整前结束日
            resultDto.setMdateUnadjust(intPeriodEndDate);

            resultList.add(resultDto);
        }
        else if (StringUtil.equals(DictCodeListExt1.付息周期_周期支付.getValue(),
            inputDto.getPayCycle())) {
            // 付息规则选择为周期时
            // 付息计划生成共通调用
            resultList = this.commonCalcProductInterestPlan(inputDto, dealCalendarHolidayList);
        }

        return resultList;
    }

    /**
     * 付息计划生成
     * 
     * @param inputDto 参数dto
     * @param dealCalendarHolidayList 节假日列表
     * @return 付息计划数据
     */
    private List<FinCashPlan> commonCalcProductInterestPlan(BussFinProductPayPlanDto inputDto,
                                                            List<String> dealCalendarHolidayList) {
        // 1.临时变量初期化
        Date startDate = null; // 临时变量.区间开始日
        Date endDate = null; // 临时变量.区间结束日
        String calcMethod = null; // 临时变量.推算方法
        List<Date> adjustBeforePeriodList = new ArrayList<>(); // 临时变量.调整前区间列表
        List<Date> adjustAfterPeriodList = new ArrayList<>(); // 临时变量.调整后区间列表
        // List<Date> adjustAfterPayDateList = new ArrayList<>(); // 临时变量.调整后支付日列表
        // String periodNodeEndDate = null; // 临时变量.区间节点结束日
        List<Date> adjustBeforeEndDatePeriodList = new ArrayList<>(); // 临时变量.调整前结束日区间列表

        // 2.调整前付息区间算出
        if (StringUtil.equals(DictCodeListExt1.付息周期_周期支付.getValue(), inputDto.getPayCycle())) {
            // 输入.付息类型 = “2：周期”
            // 区间开始日及推算方法设定
        	// 一体化对应etliu
//            if (StringUtil.equals(DictCodeList.推算方法_起息日向后.getValue(),
//                inputDto.getCalcRule())) {
        	if (StringUtil.equals(DictCodeList.推算方式_起息日向后.getValue(),
                    inputDto.getCalcRule())) {
                // 输入.付息计划推算方法 = "起息日向后" 的场合
                // 临时变量.区间开始日 = 输入.产品起息日
                startDate = inputDto.getVdate();
                // 临时变量.推算方法 = "1:向后调整"
                calcMethod = DictCodeList.营业日准则_向后调整.getValue();
            }
        	// 一体化对应etliu
//            else if (StringUtil.equals(DictCodeList.推算方法_首次付息日.getValue(),
//                inputDto.getCalcRule())) {
        	else if (StringUtil.equals(DictCodeList.推算方式_首次支付日.getValue(),
                    inputDto.getCalcRule())) {
                // 输入.付息计划推算方法 = "首次付息日" 的场合
                // 临时变量.区间开始日 = 输入.首次付息日
                startDate = inputDto.getFirstPayDate();
                // 临时变量.推算方法 = "1:向后调整"
                calcMethod = DictCodeList.营业日准则_向后调整.getValue();
            }

            // 付息区间算出
            // 日期区间分割
            List<Date> tempDivisionList = invDateCalService.dateIntervalNodeList(startDate,
                inputDto.getMdate(), calcMethod, String.valueOf(inputDto.getPayCycleVal()),
                inputDto.getPayCycleUnit());

            // 输入.推算方法 = "起息日向后" 的场合 临时变量.调整前区间列表 = 以上共通输出结果.分割后区间列表
            adjustBeforePeriodList = tempDivisionList;

         // 一体化对应etliu
//            if (StringUtil.equals(DictCodeList.推算方法_首次付息日.getValue(),
//                inputDto.getCalcRule())) {
            if (StringUtil.equals(DictCodeList.推算方式_首次支付日.getValue(),
                    inputDto.getCalcRule())) {
                // 输入.推算方法 = "首次开放日" 的场合 将输入.产品起息日追加至临时变量.调整前区间列表第一个节点
                adjustBeforePeriodList.add(0, inputDto.getVdate());
            }
        }

        // 3. 计息计划区间调整
        // 临时变量.调整后区间列表 = 临时变量.调整前区间列表
        adjustAfterPeriodList.addAll(adjustBeforePeriodList);
        if (StringUtil.equals(DictCodeList.营业日准则_向后调整.getValue(),
            inputDto.getEndTdayRule())) {
            // 输入.计息营业日准则 = “向后调整”

            // 遍历临时变量.调整后区间列表（从第2个节点至倒数第2个节点）
            List<Date> tempAdjustAfterPeriodList = new ArrayList<>();
            for (int i = 0; i < adjustAfterPeriodList.size(); i++ ) {
                if (i == 0 || i == (adjustAfterPeriodList.size() - 1)) {
                    tempAdjustAfterPeriodList.add(adjustAfterPeriodList.get(i));
                    if (i == (adjustAfterPeriodList.size() - 1)) {
                        // 临时变量.调整前结束日区间列表.add(将临时变量.调整后区间列表.节点日期)
                        adjustBeforeEndDatePeriodList.add(adjustAfterPeriodList.get(i));
                    }
                    continue;
                }
                else if (i == 1) {
                	// 一体化对应etliu
//                    if (StringUtil.equals(DictCodeList.推算方法_首次付息日.getValue(),
//                        inputDto.getCalcRule())) {
                	if (StringUtil.equals(DictCodeList.推算方式_首次支付日.getValue(),
                            inputDto.getCalcRule())) {
                        // 输入.推算方法 = "首次开放日" 的场合 遍历临时变量.调整后区间列表（从第3个节点至倒数第2个节点）
                        // add by xiaozhaoqiang 付息计划优化 2018-06-27 start
                        adjustBeforeEndDatePeriodList.add(adjustAfterPeriodList.get(i));
                        // add by xiaozhaoqiang 付息计划优化 2018-06-27 end
                        tempAdjustAfterPeriodList.add(adjustAfterPeriodList.get(i));
                        continue;
                    }
                }

                // 临时变量.区间结束日 = 临时变量.调整后区间列表.节点日期
                endDate = adjustAfterPeriodList.get(i);

                // 计息日调整
                // 调整后交易日(调整后交易日取得)
                String tempAdjustAfterTradeDate = AdjustedTradeDayCal.getAdjustedTradeDay(
                    DateUtil.format(endDate), inputDto.getEndTdayRule(), dealCalendarHolidayList);
                // 上述返回值.调整后交易日 < 临时变量.调整后区间列表.下一节点日期
                if (DateUtil.compareDate(DateUtil.parse(tempAdjustAfterTradeDate),
                    adjustAfterPeriodList.get(i + 1)) < 0) {
                    // 临时变量.调整前结束日区间列表.add(将临时变量.调整后区间列表.节点日期)
                    adjustBeforeEndDatePeriodList.add(endDate);
                    // 将临时变量.调整后区间列表.节点日期 = 上述返回值.调整后交易日
                    adjustAfterPeriodList.set(i, DateUtil.parse(tempAdjustAfterTradeDate));

                    tempAdjustAfterPeriodList.add(adjustAfterPeriodList.get(i));
                }
            }

            adjustAfterPeriodList = tempAdjustAfterPeriodList;
        }
        else {
            // 输入.计息营业日准则 = “不调整”
            adjustBeforeEndDatePeriodList.addAll(adjustAfterPeriodList);
            if (!StringUtil.isEmptyList(adjustBeforeEndDatePeriodList)) {
                adjustBeforeEndDatePeriodList.remove(0);
            }
        }

        // // 4. 支付日调整
        // // 遍历临时变量.调整前结束日区间列表（从第2个节点至最后一个节点）
        // for (int i = 0; i < adjustBeforeEndDatePeriodList.size(); i++ ) {
        // // 4.1 支付日调整(调整后交易日取得)
        // String tempAdjustedPayDate = AdjustedTradeDayCal.getAdjustedTradeDay(
        // DateUtil.format(adjustBeforeEndDatePeriodList.get(i)), inputDto.getPayTdayRule(),
        // dealCalendarHolidayList);
        //
        // // 支付日列表.add(上述返回值.调整后交易日)
        // adjustAfterPayDateList.add(DateUtil.parse(tempAdjustedPayDate));
        // }

        // 5. 开始日、结束日、调整前结束日列表生成
        List<Date> startDateResultList = new ArrayList<>(); // 开始日区间列表
        List<Date> endDateResultList = new ArrayList<>(); // 结束日区间列表
        // 遍历临时变量.调整后区间列表
        for (int i = 0; i < adjustAfterPeriodList.size(); i++ ) {
            if (i == 0 || i == adjustAfterPeriodList.size() - 1) {
                if (i == 0) {
                    // 开始日区间列表.add(临时变量.调整后区间列表.节点日期)
                    startDateResultList.add(adjustAfterPeriodList.get(i));
                }
                if (i == adjustAfterPeriodList.size() - 1) {
                    // 结束日区间列表.add(临时变量.调整后区间列表.节点日期)
                    endDateResultList.add(adjustAfterPeriodList.get(i));
                }
            }
            else {
                // 开始日区间列表.add(临时变量.调整后区间列表.节点日期)
                startDateResultList.add(adjustAfterPeriodList.get(i));

                // 结束日区间列表.add(临时变量.调整后区间列表.节点日期)
                endDateResultList.add(adjustAfterPeriodList.get(i));
            }
        }

        List<FinCashPlan> resultList = new ArrayList<>();
        for (int i = 0; i < startDateResultList.size(); i++ ) {
            FinCashPlan resultDto = new FinCashPlan();
            // 计息区间起始日
            resultDto.setVdate(startDateResultList.get(i));
            try {
                // 计息区间结束日
                resultDto.setMdate(endDateResultList.get(i));
                // 支付日(改为根据调整后的计息区间结束日调整)
                resultDto.setPayDate(DateUtil.parse(AdjustedTradeDayCal.getAdjustedTradeDay(
                    DateUtil.format(endDateResultList.get(i)), inputDto.getPayTdayRule(),
                    dealCalendarHolidayList)));
                // 调整前结束日
                resultDto.setMdateUnadjust(adjustBeforeEndDatePeriodList.get(i));
            }
            catch (IndexOutOfBoundsException e) {
            }
            resultList.add(resultDto);
        }

        return resultList;
    }

}
