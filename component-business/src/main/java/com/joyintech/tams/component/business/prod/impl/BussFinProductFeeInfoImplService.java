/*
 * 文件名：BussFinProductFeeInfoImplService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangliang
 * 修改时间：2018年10月9日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.component.business.prod.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.joyintech.tams.constant.DictCodeListExt1;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.joyintech.framework.base.BaseService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.common.base.entity.MstFeeinfo;
import com.joyintech.tams.common.base.entity.StlCapacctInfo;
import com.joyintech.tams.component.business.common.dto.DateDivisionDto;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.component.business.prod.BussFinProductFeeInfoService;
import com.joyintech.tams.component.business.prod.dto.BussFinProductFeeInfoDto;
import com.joyintech.tams.component.business.prod.dto.FinProductFeeInfoDto;
import com.joyintech.tams.invest.common.calculator.AdjustedTradeDayCal;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.ServiceApplication;

/**
 * 产品费用信息现类
 *
 * @author zhangliang
 * @version 1.0 2018年10月9日
 * @see BussFinProductFeeInfoImplService
 * @since 1.0
 */
@Service
public class BussFinProductFeeInfoImplService extends BaseService<MstFeeinfo> implements BussFinProductFeeInfoService {

    /**
     * 付息计划，开放计划，利率重置，申购计划，赎回计划计算用和日期关联的Service
     */
    @Autowired
    private DateCalService dateCalService;

    @Override
    public List<SelectOptionDto> queryFeeInfoList(Map<String, Object> map) {
        String initValue = (String)map.get("initValue");
        String search = (String)map.get("search");
        map.put("floatFeeTypeFlag", DictCodeList.是否_否.getValue());
        List<SelectOptionDto> list = restTemplate.postForList(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeeInfo/queryFeeInfoList", map,
            SelectOptionDto.class);
        if(StringUtil.equals("Y", map.get("isRateAdj"))) {
        	Map<String,Object> paramMap = new HashMap<String,Object>();
            paramMap.put("finprodId", map.get("finprodId"));
            paramMap.put("search", map.get("search"));
            paramMap.put("initValue", map.get("initValue"));
            paramMap.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
            if (DictCodeListExt1.金融产品类型_投管用_通道.getValue().equals(map.get("finprodType"))) {
            	paramMap.put("finprodType", DictCodeListExt1.金融产品类型_通道.getValue());
            }
            paramMap.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
            paramMap.put("cashType2", DictCodeListExt1.现金流二级类型_利息.getValue());
            paramMap.put("finprodType2", map.get("finprodType"));
        	List<SelectOptionDto> list2 = restTemplate.postForList(
                    ServiceApplication.ASSET_SERVICE_NAME + "/FinAssetRateAdj/queryFeeInfoSel", paramMap,
                    SelectOptionDto.class);
        	return list2;
        }
        if(StringUtil.isNotEmpty(initValue) && CollectionUtils.isNotEmpty(list)) {
            return list;
        }
        FinProductFeeInfoDto finProductFeeInfoDto = CacheUtil.getAppCache(map.get("feeNewAddId"),
            FinProductFeeInfoDto.class);
        if (finProductFeeInfoDto != null
            && !StringUtil.isEmptyList(finProductFeeInfoDto.getSelectedItem())) {
            for (FinProductFeeInfoDto feeDto : finProductFeeInfoDto.getSelectedItem()) {
                if (StringUtil.equals(feeDto.getFeeId(), initValue)
                    || StringUtil.equals(feeDto.getFeeName(), initValue)) {
                    SelectOptionDto dto = new SelectOptionDto();
                    dto.setId(feeDto.getFeeId());
                    dto.setText(feeDto.getFeeName());
                    list.add(dto);
                }
                if (StringUtil.isEmpty(initValue) && (StringUtil.isEmpty(search) || StringUtil.contains(feeDto.getFeeId(), search))) {
                    SelectOptionDto dto = new SelectOptionDto();
                    dto.setId(feeDto.getFeeId());
                    dto.setText(feeDto.getFeeName());
                    list.add(dto);
                }
            }
        }
        return list;
    }

    @Override
    public BussFinProductFeeInfoDto queryMstFeeinfoByFeeId(String feeId, String finprodMarketId) {
        BussFinProductFeeInfoDto bussFinProductFeeInfoDto = restTemplate.getForObject(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeeInfo/queryMstFeeinfoByFeeId?feeId="
                                                                                      + feeId,
            BussFinProductFeeInfoDto.class);
        if (bussFinProductFeeInfoDto == null) {
            FinProductFeeInfoDto finProductFeeInfoDto = CacheUtil.getAppCache(finprodMarketId,
                FinProductFeeInfoDto.class);
            if (finProductFeeInfoDto != null
                && !StringUtil.isEmptyList(finProductFeeInfoDto.getSelectedItem())) {
                for (FinProductFeeInfoDto feeDto : finProductFeeInfoDto.getSelectedItem()) {
                    if (StringUtil.equals(feeDto.getFeeId(), feeId)) {
                        bussFinProductFeeInfoDto = new BussFinProductFeeInfoDto();
                        bussFinProductFeeInfoDto.setFeeType(feeDto.getFeeType());
                        bussFinProductFeeInfoDto.setBasicBill(feeDto.getBasicBill());
                        bussFinProductFeeInfoDto.setBasis(feeDto.getBasis());
                        bussFinProductFeeInfoDto.setChargeType(feeDto.getChargeType());
                        bussFinProductFeeInfoDto.setOrgType(feeDto.getOrgType());
                        bussFinProductFeeInfoDto.setOrgType2(feeDto.getOrgType2());
                        bussFinProductFeeInfoDto.setRemark(feeDto.getRemark());
                        break;
                    }
                }
            }
        }
        return bussFinProductFeeInfoDto;

    }

    @Override
    public List<FinCashPlan> generatePayFeePlan(BussFinProductFeeInfoDto finProductFeeInfoDto) {
        // 上记以外的情况，代表是画面上点了【预览】按钮触发的事件，需要重新计算付费计划
        List<FinCashPlan> resultList = new ArrayList<FinCashPlan>();

        // 分割周期
        String cycle = "";
        // 分割周期单位
        String cycleUnit = "";

        // 根据付费规则判断分割周期、分割周期单位
     // 一体化对应etliu start
//        if (StringUtil.equals(finProductFeeInfoDto.getPayFeeRule(),
//            DictCodeList.付费规则_月.getValue())) {
//            cycle = "1";
//            cycleUnit = DictCodeListExt1.周期单位_月.getValue();
//        }
//        else if (StringUtil.equals(finProductFeeInfoDto.getPayFeeRule(),
//            DictCodeList.付费规则_季.getValue())) {
//            cycle = "3";
//            cycleUnit = DictCodeListExt1.周期单位_月.getValue();
//        }
//        else if (StringUtil.equals(finProductFeeInfoDto.getPayFeeRule(),
//            DictCodeList.付费规则_半年.getValue())) {
//            cycle = "6";
//            cycleUnit = DictCodeListExt1.周期单位_月.getValue();
//        }
//        else if (StringUtil.equals(finProductFeeInfoDto.getPayFeeRule(),
//            DictCodeList.付费规则_年.getValue())) {
//            cycle = "1";
//            cycleUnit = DictCodeListExt1.周期单位_年.getValue();
//        }
//        else if (StringUtil.equals(finProductFeeInfoDto.getPayFeeRule(),
//            DictCodeList.付费规则_到期.getValue())) {
//            cycle = "0";
//        }
        if(StringUtil.equals(finProductFeeInfoDto.getPayFeeRule(),
            DictCodeList.频率_周.getValue())){
            cycle = "1";
            cycleUnit = DictCodeListExt1.周期单位_周.getValue();
        }
        else if (StringUtil.equals(finProductFeeInfoDto.getPayFeeRule(),
            DictCodeList.频率_月.getValue())) {
            cycle = "1";
            cycleUnit = DictCodeListExt1.周期单位_月.getValue();
        }
        else if (StringUtil.equals(finProductFeeInfoDto.getPayFeeRule(),
            DictCodeList.频率_季.getValue())) {
            cycle = "3";
            cycleUnit = DictCodeListExt1.周期单位_月.getValue();
        }
        else if (StringUtil.equals(finProductFeeInfoDto.getPayFeeRule(),
            DictCodeList.频率_半年.getValue())) {
            cycle = "6";
            cycleUnit = DictCodeListExt1.周期单位_月.getValue();
        }
        else if (StringUtil.equals(finProductFeeInfoDto.getPayFeeRule(),
            DictCodeList.频率_年.getValue())) {
            cycle = "1";
            cycleUnit = DictCodeListExt1.周期单位_年.getValue();
        }
        else if (StringUtil.equals(finProductFeeInfoDto.getPayFeeRule(),
            DictCodeList.频率_到期一次.getValue())) {
            cycle = "0";
        }
     // 一体化对应etliu end

        // 获取节假日 到期日前后顺延30天
        List<String> dealCalendarHolidayList = dateCalService.getDealCalendarHoliday(
            DateUtil.addDay(finProductFeeInfoDto.getVdate(), -30),
            DateUtil.addDay(finProductFeeInfoDto.getMdate(), 30),
            finProductFeeInfoDto.getDealCalId());
        //因为计提结束日也计提,划分区间时需要 计提结束日+1 add by chendijun 2019/10/28
        Date adjustEAccrDate = DateUtil.parse(AdjustedTradeDayCal.getAfterOrBeforeDay(
            DateUtil.format(finProductFeeInfoDto.geteAccrDate()),1,true));
        //首次计费起始日到首次计费结束日
        if (StringUtil.isNotEmpty(finProductFeeInfoDto.getFirstCalEndDate())) {
            
            // 首次计提结束日根据营业日准则进行调整
            Date adjustFirstCalEndDate = DateUtil.parse(
                AdjustedTradeDayCal.getAdjustedTradeDay(DateUtil.format(finProductFeeInfoDto.getFirstCalEndDate()),
                    finProductFeeInfoDto.getCalcFeeTradeRule(), dealCalendarHolidayList));
            finProductFeeInfoDto.setFirstCalEndDate(adjustFirstCalEndDate);

            // 支付日根据营业日准则进行调整
            Date adjustPayDate = DateUtil.parse(
                AdjustedTradeDayCal.getAdjustedTradeDay(DateUtil.format(finProductFeeInfoDto.getFirstCalEndDate()),
                    finProductFeeInfoDto.getPayFeeTradeRule(), dealCalendarHolidayList));
            
            FinCashPlan resultDto = new FinCashPlan();
            resultDto.setCashNum((long)(1));
            resultDto.setCashType(DictCodeListExt1.现金流一级类型_费用.getValue());
            resultDto.setVdate(finProductFeeInfoDto.getVdate());
            resultDto.setMdate(finProductFeeInfoDto.getFirstCalEndDate());
            resultDto.setVdateUnadjust(finProductFeeInfoDto.getVdate());
            resultDto.setMdateUnadjust(finProductFeeInfoDto.getFirstCalEndDate());
            
            resultDto.setPayDate(adjustPayDate);
            resultDto.setPayDateUnadjust(adjustPayDate);
            resultList.add(resultDto);
            
            finProductFeeInfoDto.setVdate(finProductFeeInfoDto.getFirstCalEndDate());
            //如果首次计费结束日大于等于计提结束日+1天时 只取第一条（大于计提结束日即可）
            if(DateUtil.compareDate(finProductFeeInfoDto.getFirstCalEndDate(), adjustEAccrDate) >= 0){
                return resultList;
            }
        }

        // 日期区间分割
        List<DateDivisionDto> dateIntervalDivisionList = this.dateIntervalDivision(
            finProductFeeInfoDto.getVdate(), adjustEAccrDate, cycle, cycleUnit);
        Date endDateTmp  = null;
        // 根据调整规则调整
        for (int count = 0; count < dateIntervalDivisionList.size(); count++ ) {
            DateDivisionDto dto = dateIntervalDivisionList.get(count);
            // add by yangjiaming 20180314 关于产品付费计划的计算方法调整 start
            /*
             * 如果不是最后一条付费计划: 浮动管理费时： 计划中计提截止日 = 区间结束日,计划支付日 = 区间结束日的下一工作日 非浮动管理费时：计划中计提截止日 =
             * 区间结束日,计划支付日 = 区间结束日根据营业日准则调整
             */

            // 计提结束日不会根据营业日准则顺延
            Date startDate = dto.getStartDate();
            Date endDate = dto.getEndDate();
            Date tradePaymentDay = dto.getEndDate();
            if(count > 0 && startDate.compareTo(finProductFeeInfoDto.getVdate()) != 0 
                && (!StringUtil.isNotEmpty(finProductFeeInfoDto.getFirstCalEndDate())||startDate.compareTo(finProductFeeInfoDto.getFirstCalEndDate()) != 0)){
                startDate = DateUtil.parse(
                    AdjustedTradeDayCal.getAdjustedTradeDay(DateUtil.format(startDate),
                        finProductFeeInfoDto.getCalcFeeTradeRule(), dealCalendarHolidayList));
            }
            if (!StringUtil.isNotEmpty(finProductFeeInfoDto.getFirstCalEndDate())||endDate.compareTo(finProductFeeInfoDto.getFirstCalEndDate()) != 0) {
                endDate = DateUtil.parse(
                    AdjustedTradeDayCal.getAdjustedTradeDay(DateUtil.format(endDate),
                        finProductFeeInfoDto.getCalcFeeTradeRule(), dealCalendarHolidayList));
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
                    finProductFeeInfoDto.getPayFeeTradeRule(), dealCalendarHolidayList));

            FinCashPlan resultDto = new FinCashPlan();
            resultDto.setCashNum((long) (resultList.size() + 1));
            resultDto.setCashType(DictCodeListExt1.现金流一级类型_费用.getValue());
            resultDto.setVdate(startDate);
            resultDto.setMdate(endDate);
            resultDto.setPayDate(tradePaymentDay);
            resultDto.setVdateUnadjust(startDate);
            resultDto.setMdateUnadjust(endDate);
            resultDto.setPayDateUnadjust(tradePaymentDay);
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
    public List<DateDivisionDto> dateIntervalDivision(Date startDate, Date endDate, String cycle,
                                                      String cycleUnit) {
        List<DateDivisionDto> list = new ArrayList<DateDivisionDto>();

        // 如果cycle为‘0’，则表示为到期一次性付费
        if (StringUtil.equals("0", cycle)) {
            DateDivisionDto dto = new DateDivisionDto();
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
                    Date calcDate = dateCalService.naturalDateCalc(startDate, cycleUnit,
                        Integer.parseInt(cycle) * count);
                    if (DateUtil.compareDate(calcDate, endDate) < 0) {
                        DateDivisionDto dto = new DateDivisionDto();
                        dto.setStartDate(startDateTemp);
                        dto.setEndDate(calcDate);
                        list.add(dto);
                        startDateTemp = calcDate;

                        count++ ;
                    }
                    else {
                        DateDivisionDto dto = new DateDivisionDto();
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

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BussFinProductFeeInfoDto> queryFeeInfoByFinprodId(String finprodId,String finprodType2) {
            List<BussFinProductFeeInfoDto> feeInfoList = restTemplate.getForList(
                    ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeeInfo/queryFeeInfoByFinprodId?finprodId="
                                                                                     + finprodId+"&finprodType2="+finprodType2,
                    BussFinProductFeeInfoDto.class);
            return feeInfoList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<FinCashPlan> queryFeePlanListByFinprodId(String finprodId, String feeId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("finprodId", finprodId);
        map.put("feeId", feeId);
        map.put("branch",
            Long.parseLong(CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE)));
        return restTemplate.postForList(ServiceApplication.PRODUCT_SERVICE_NAME
                                        + "/FinProductFeeInfo/queryFeePlanListByFinprodId",
            map, FinCashPlan.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BussFinProductFeeInfoDto> queryFeeInfoByPtlId(String ptlId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ptlId", ptlId);
        List<BussFinProductFeeInfoDto> feeInfoList = restTemplate.postForList(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeeInfo/queryFeeInfoByPtlId",
            map, BussFinProductFeeInfoDto.class);
//        if (!StringUtil.isEmptyList(feeInfoList)) {
//            for (BussFinProductFeeInfoDto feeInfo : feeInfoList) {
//                feeInfo.setFeeType(
//                    SysDataUtil.getNameFromDataDict(DictCode.费用类型, feeInfo.getFeeType()));
//                feeInfo.setBasicBill(SysDataUtil.getNameFromDataDict(DictCode.计费基数_费用品种,
//                    feeInfo.getBasicBill()));
//                feeInfo.setBasis(
//                    SysDataUtil.getNameFromDataDict(DictCode.计息基础, feeInfo.getBasis()));
//            }
//        }
        return feeInfoList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BussFinProductFeeInfoDto queryMstFeeinfoForRateAdjByFeeId(Map<String, Object> param) {
        return  restTemplate.postForObject(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeeInfo/queryMstFeeinfoForRateAdjByFeeId",
            param,BussFinProductFeeInfoDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<BussFinProductFeeInfoDto> queryFeeInfoByFinprodIdEx(String finprodId) {
        List<BussFinProductFeeInfoDto> feeInfoList = restTemplate.getForList(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeeInfo/queryFeeInfoByFinprodIdEx?finprodId="
                                                                             + finprodId,
            BussFinProductFeeInfoDto.class);
        if (!StringUtil.isEmptyList(feeInfoList)) {
            for (BussFinProductFeeInfoDto feeInfo : feeInfoList) {
                // 一体化对应etliu start
//                feeInfo.setFeeType(
//                    SysDataUtil.getNameFromDataDict(DictCode.费用类型, feeInfo.getFeeType()));
                feeInfo.setFeeType(
                    SysDataUtil.getNameFromDataDict(DictCode.费用品种类型, feeInfo.getFeeType()));
                // 一体化对应etliu end
                feeInfo.setBasicBill(SysDataUtil.getNameFromDataDict(DictCode.计费基数_费用品种,
                    feeInfo.getBasicBill()));
                feeInfo.setBasis(
                    SysDataUtil.getNameFromDataDict(DictCode.计息基础, feeInfo.getBasis()));
            }
        }
        return feeInfoList;
    }

    @Override
    public List<SelectOptionDto> queryFundFeeInfoList(Map<String, Object> map) {
        map.put("floatFeeTypeFlag", DictCodeList.是否_否.getValue());
        List<SelectOptionDto> list = restTemplate.postForList(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeeInfo/queryFundFeeInfoList", map,
            SelectOptionDto.class);

        FinProductFeeInfoDto finProductFeeInfoDto = CacheUtil.getAppCache(map.get("feeNewAddId"),
            FinProductFeeInfoDto.class);
        if (finProductFeeInfoDto != null
            && !StringUtil.isEmptyList(finProductFeeInfoDto.getSelectedItem())) {
            for (FinProductFeeInfoDto feeDto : finProductFeeInfoDto.getSelectedItem()) {
                if (StringUtil.equals(feeDto.getFeeId(), map.get("initValue"))
                    || StringUtil.equals(feeDto.getFeeName(), map.get("initValue"))) {
                    SelectOptionDto dto = new SelectOptionDto();
                    dto.setId(feeDto.getFeeId());
                    dto.setText(feeDto.getFeeName());
                    list.add(dto);
                    break;
                }
                if (StringUtil.isNotEmpty(map.get("search"))) {
                    if (feeDto.getFeeId().indexOf(map.get("search").toString()) > -1
                        || feeDto.getFeeName().indexOf(map.get("search").toString()) > -1) {
                        SelectOptionDto dto = new SelectOptionDto();
                        dto.setId(feeDto.getFeeId());
                        dto.setText(feeDto.getFeeName());
                        list.add(dto);
                    }
                }
                
                if(!StringUtil.isNotEmpty(map.get("initValue")) && !StringUtil.isNotEmpty(map.get("search"))){
                	SelectOptionDto dto = new SelectOptionDto();
                    dto.setId(feeDto.getFeeId());
                    dto.setText(feeDto.getFeeName());
                    list.add(dto);
                }
            }
        }
        return list;
    }
    /**
     * 获取账号信息
     * @return 清算账户表信息
     * @see
     * @since 1.0
     */
    @Override
    public StlCapacctInfo getCctInfo(String acctNo){
        return restTemplate.getForObject(
            ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductFeeInfo/getCctInfo?acctNo="+acctNo,
            StlCapacctInfo.class);
    }

    @Override
    public List<FinCashPlan> finCashPlanAdjustment(BussFinProductFeeInfoDto dto, Date planStartDay,
    		Date planEndDay) {
    	List<FinCashPlan> list = new ArrayList<>();
    	if(planStartDay == null || planEndDay == null) {
    		return generatePayFeePlan(dto);
    	}
    	//产品款型不为空，使用子产品ID
    	String finprodId = dto.getFinprodId();
    	if(StringUtil.isNotEmpty(dto.getProdStyle())) {
    		finprodId = dto.getProdStyle();
    	}
    	//下一付费区间等于产品起息日，直接使用新计划
		if(DateUtil.compareDate(planStartDay, dto.getVdate()) == 0 ) {
			dto.setFirstCalEndDate(planEndDay);
			return generatePayFeePlan(dto);
		}
		List<FinCashPlan> finCashPlans = queryFeePlanListByFinprodId(finprodId,dto.getFeeId());
    	if(!StringUtil.isEmptyList(finCashPlans)) {
        	for(FinCashPlan finCashPlan : finCashPlans) {
        		//下一付费区开始日间大于付费计划开始日继续原规则
        		if(DateUtil.compareDate(planStartDay, finCashPlan.getMdate()) > 0 ) {
        			list.add(finCashPlan);
        		}else if(DateUtil.compareDate(planStartDay, finCashPlan.getVdate()) == 0 ){
        			//下一付费区开始日间等于付费计划结束日，继续原规则
        			list.add(finCashPlan);
        			//下一付费区间开始日作为计提开始日，下一付费区间结束日作为首次付费日生成付费计划
        			dto.setVdate(planStartDay);
        			dto.setFirstCalEndDate(planEndDay);
        			List<FinCashPlan> newPlans = generatePayFeePlan(dto);
        			for(FinCashPlan newPlan: newPlans) {
        				//设置现金流序号与原计划拼接
        				newPlan.setCashNum((long)(list.size()+1));
        				list.add(newPlan);
        			}
        			return list;
        		}else {
        			//首个下一付费区间开始日小于付费计划结束日，下一付费区间开始日作为付费结束日
        			finCashPlan.setMdate(planStartDay);
        			finCashPlan.setPayDate(planStartDay);
        			list.add(finCashPlan);
        			//下一付费区间开始日作为计提开始日，下一付费区间结束日作为首次付费日生成付费计划
        			dto.setVdate(planStartDay);
        			dto.setFirstCalEndDate(planEndDay);
        			List<FinCashPlan> newPlans = generatePayFeePlan(dto);
        			for(FinCashPlan newPlan: newPlans) {
        				//设置现金流序号与原计划拼接
        				newPlan.setCashNum((long)(list.size()+1));
        				list.add(newPlan);
        			}
        			return list;
        		}
        	}
    	}
    	return list;
    
    }
}
