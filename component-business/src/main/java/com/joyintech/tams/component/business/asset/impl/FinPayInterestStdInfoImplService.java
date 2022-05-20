/*
 * 文件名：FinPayInterestStdInfoImplService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.component.business.asset.impl;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.util.*;
import com.joyintech.tams.common.base.entity.FinCashPayRule;
import com.joyintech.tams.component.business.asset.FinPayInterestStdInfoService;
import com.joyintech.tams.component.business.asset.dto.FinPayInterestStdInfoDto;
import com.joyintech.tams.component.business.common.dto.AssetCommonInvRateAdjustDto;
import com.joyintech.tams.component.business.common.service.CommonRateAdjustService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.asset.dto.AssetCommonInfoDto;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDetailDto;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.calculator.*;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年9月11日
 * @see FinPayInterestStdInfoImplService
 * @since 1.0
 */
@Service
public class FinPayInterestStdInfoImplService extends BaseService<FinPayInterestStdInfoDto> implements FinPayInterestStdInfoService{
    
    @Autowired(required = false)
    private IPlatformDate iPlatformDate;
    
    @Autowired
    private CommonRateAdjustService commonRateAdjustService;
    
    /**
     * 付息计划，开放计划，利率重置，申购计划，赎回计划计算用和日期关联的Service
     */
    @Autowired
    private DateCalService dateCalService;
    
    @Override
    public IDictCode[] getInitDictCode() {
    	// 一体化对应etliu
//        return new DictCode[] {DictCode.营业日准则,DictCode.支付频率_付息频率,DictCode.推算方法};
    	return new DictCode[] {DictCode.营业日准则,DictCode.频率,DictCode.推算方式,DictCode.日期号,DictCode.月};
    }
    
    /**
     * 输入：FinInterestFactorDto
     * 输出:按要求封装entity--输出 FinInterestFactorDto
     */
    @Override
    public FinPayInterestStdInfoDto getFinPayInterestStdInfoDto(FinPayInterestStdInfoDto dto){
        FinCashPayRule finCashPayRule = new FinCashPayRule();
        BeanUtil.copyPropertiesIgnoreNull(finCashPayRule, dto);
        dto.setFinCashPayRule(finCashPayRule);
        return dto;
    }
    
    
    /**
     * 初始化支付规则
     * {@inheritDoc}
     */
    @Override
    public FinPayInterestStdInfoDto getFinPayInterestStdInfoByProductId(String finprodId,String vdate,String cashType,String finprodType){
        return restTemplate.getForObject(ServiceApplication.ASSET_SERVICE_NAME
            + "/FinPayInterestStdInfo/getFinPayInterestStdInfoByProductId?finprodId="
            + finprodId+"&vdate="+vdate
            +"&cashType="+cashType
            +"&finprodType="+finprodType,
            FinPayInterestStdInfoDto.class);
    }
    
    /**
     * 初始化付息计划
     * {@inheritDoc}
     */
    @Override
    public List<FinCashPlanDto> getFinCashPlanByProductId(String finprodId, String cashType, String finprodType, String finprodType2){
        return restTemplate.getForList(ServiceApplication.COMMON_SERVICE_NAME
            + "/CommonTradeInfo/getFinCashPlanByProductId?finprodId="
            + finprodId+"&cashType="+cashType
            +"&finprodType="+finprodType+"&finprodType2="+finprodType2,
            FinCashPlanDto.class);
    }
   
    /**
     * 生成付息计划
     * {@inheritDoc}
     * @throws ParseException 
     */
    @Override
    public List<FinCashPlanDto> createAssetPayInteresPlan(FinPayInterestStdInfoDto dto) throws ParseException {
        //还本计划
        List<FinCashPlanDto> invRepayList = new ArrayList<FinCashPlanDto>();
        //资产公共DTO
        AssetCommonInfoDto scai = new AssetCommonInfoDto();
        BeanUtil.copyPropertiesIgnoreNull(scai, dto); //复制资产信息
        scai.setFinRateInfo(dto.getFinInterestFactorDto());
        //付息规则
        FinCashPayRule is = new FinCashPayRule();
        BeanUtil.copyPropertiesIgnoreNull(is, dto); //复制付息规则
        //到期日+15天
        Date  mdate = DateUtil.parse(DateUtil.plusDays(DateUtil.format(scai.getMdate()), 15));
        //获取节假日
        List<String> holidayList = dateCalService.getDealCalendarHoliday(scai.getVdate(),mdate,scai.getCalendarId());
        //报价信息
        List<AssetCommonInvRateAdjustDto> rateList = new ArrayList<AssetCommonInvRateAdjustDto>();

        //画面还本计划为空时  构造一条还本计划
        invRepayList = createInvRepayList(scai,dto);
        //存款类资产取借款利率、计划类资产取净投资收益率
        if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_存款.getValue())){
            String isCalLoadRate =  CacheUtil.getSysParam("IS_CAL_LOADRATE");
            if(StringUtil.equals(isCalLoadRate,"01")){
                scai.setRate(ArithmeticUtil.divide(dto.getFinInterestFactorDto().getLoadRate(), new BigDecimal(100), 14));
            }
            else{
                if(StringUtil.equals(dto.getFinInterestFactorDto().getCalRateType(), DictCodeListExt3.计息利率方式_买断利率计提.getValue())){
                    scai.setRate(ArithmeticUtil.divide(dto.getFinInterestFactorDto().getBuyOutRate(), new BigDecimal(100), 14));
                }else{
                    scai.setRate(ArithmeticUtil.divide(dto.getFinInterestFactorDto().getInvestRate(), new BigDecimal(100), 14));
                }
            }
        }else if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())){
            if(StringUtil.equals(dto.getFinInterestFactorDto().getCalRateType(), DictCodeListExt3.计息利率方式_买断利率计提.getValue())){
                scai.setRate(ArithmeticUtil.divide(dto.getFinInterestFactorDto().getBuyOutRate(), new BigDecimal(100), 14));
            }else{
                scai.setRate(ArithmeticUtil.divide(dto.getFinInterestFactorDto().getInvestRate(), new BigDecimal(100), 14));
            }
        }else if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())){
            if(StringUtil.equals(dto.getFinInterestFactorDto().getCalRateType(), DictCodeListExt3.计息利率方式_买断利率计提.getValue())){
                scai.setRate(ArithmeticUtil.divide(dto.getFinInterestFactorDto().getBuyOutRate(), new BigDecimal(100), 14));
            }else{
                scai.setRate(ArithmeticUtil.divide(dto.getFinInterestFactorDto().getInvestRate(), new BigDecimal(100), 14));
            }
        }else{
            scai.setRate(ArithmeticUtil.divide(dto.getFinInterestFactorDto().getRate(), new BigDecimal(100), 14));
        }
        //计息基础
        scai.setBasis(dto.getFinInterestFactorDto().getBasis());
        // 一体化对应etliu
//        scai.setRateType(dto.getFinInterestFactorDto().getRateType());
        scai.setIntType(dto.getFinInterestFactorDto().getIntType());
        scai.setAmount(dto.getIssueAmt());
        //息票品种
        String couponSpecies = dto.getCouponSpecies();
        //计息营业日规则
        String endTdayRule = dto.getEndTdayRule();
        if(StringUtil.isNotEmpty(endTdayRule)){
            is.setEndTdayRule(endTdayRule);
        }else{
            is.setEndTdayRule(DictCodeList.营业日准则_不调整.getValue());
        }
        if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
            if(!StringUtil.equals(couponSpecies, DictCodeList.息票品种_附息.getValue())){
             // 一体化对应etliu
//                is.setPayCycle(DictCodeList.支付频率_付息频率_到期支付.getValue());
                is.setPayCycle(DictCodeList.频率_到期一次.getValue());
                is.setPayTdayRule(dto.getPayTdayRule());
             // 一体化对应etliu
//                is.setCalcRule(DictCodeList.推算方法_起息日向后.getValue());
                is.setCalcRule(DictCodeList.推算方式_起息日向后.getValue());
            }
        }
        if(StringUtil.isEmpty(dto.getFinInterestFactorDto().getBasisStyle())){
            scai.setIntpayRule(DictCodeListExt1.利息分配方式_按照实际天数分配.getValue());
        }else{
            scai.setIntpayRule(dto.getFinInterestFactorDto().getBasisStyle()); 
        }
        // 平台日期
        Date platDate = iPlatformDate.getSysDate();
        // 获取付息区间列表 
        List<FinCashPlanDto> scheList = new ArrayList<FinCashPlanDto>();
        //是否计划区间利息计算(计划明细查看)
        if(Boolean.parseBoolean(dto.getIsSubAmtCal())){
            if(Boolean.parseBoolean(dto.getIsAgainCalInt())){
                //画面所有付息计划传入
//                List<FinCashPlanDto> finCashPlanList = dto.getFinCashPlanList();
                List<FinCashPlanDto> finCashPlanList = this.getFinCashPlanByProductId(dto.getFinprodId(),DictCodeListExt1.现金流二级类型_利息.getValue(), DictCodeListExt1.现金流业务类型_金融产品.getValue(),dto.getFinprodType2());
                scheList = dto.getInvScheduleList();
                rateList = commonRateAdjustService.escapRateAdjust(scai,is,finCashPlanList,holidayList);
                //计息本金计算 计息本金=100-还本面额 yjm start 20190827
                BigDecimal intPrinAmt = scai.getAmount();
                for (FinCashPlanDto fdto: invRepayList) {
                    if(dto.getIsPayIntFlag().equals(DictCodeList.是否_是.getValue())){//是还本不带息的场景
                        fdto.setIsPayInt(DictCodeList.是否_否.getValue());//还本不带息
                    }
                    if (scheList.get(0).getVdate().compareTo(fdto.getMdate()) > 0 ) {
                        intPrinAmt = intPrinAmt.subtract(fdto.getPrinAmt());
                    }
                }
                //计息本金计算 计息本金=100-还本面额 yjm end 20190827
                scheList = InterestScheduleSubCal.interestScheduleSubCalA(scheList, scai, invRepayList, rateList, is,intPrinAmt , FinCashPlanDetailDto.class);
            }
            return scheList;
        }
        //前台传入，已有付息计划，重新计算利息
        if(Boolean.parseBoolean(dto.getIsAgainCalInt())){
            scheList = dto.getInvScheduleList();
            //计算支付日
            for(FinCashPlanDto FinCashPlanDto : scheList){
                if (!StringUtil.isNotEmpty(FinCashPlanDto.getPayDate())) {
                    String payDate = AdjustedTradeDayCal.getAdjustedTradeDay(DateUtil.format(FinCashPlanDto.getMdate()), is.getPayTdayRule(), holidayList);
                    FinCashPlanDto.setPayDate(DateUtil.parse(payDate));
                }
            }
            rateList = commonRateAdjustService.escapRateAdjust(scai,is,scheList,holidayList);
             // 仅计算付息区间利息
            scheList = InterestScheduleCalReset.interestScheduleCalA(scheList, scai, invRepayList, null,
                    rateList, is, holidayList,platDate, null, FinCashPlanDetailDto.class);
            //排序
            Collections.sort(scheList, new Comparator<FinCashPlanDto>() {
                @Override
                public int compare(FinCashPlanDto o1, FinCashPlanDto o2) {
                    return o1.getVdate().compareTo(o2.getVdate());
                }
            });
            for(int i = 0; i<scheList.size();i++) {
                if(i == 0){
                    scheList.get(i).setMdateTerm(scheList.get(i).getMdate());
                }else{
                    scheList.get(i).setVdateTerm(scheList.get(i-1).getMdateTerm());
                    scheList.get(i).setMdateTerm(scheList.get(i).getMdate());
                }
            }
        }else{
            //重新生成计划
            //前台传入的交易中的付息计划
            List<FinCashPlanDto> oldScheList = new ArrayList<FinCashPlanDto>();
            if(null!=dto.getMaxTradeDate()){
                oldScheList = dto.getInvScheduleList();
            //筛选付息计划最大计息截止日(交易前计划不可变更,交易后计划可以重新按规则生成)
                if(null!=oldScheList&&!oldScheList.isEmpty()){
                for(FinCashPlanDto finCashPlanDto:oldScheList){
                        if(DateUtil.compareDate(finCashPlanDto.getVdate(),dto.getMaxTradeDate())==-1
                            &&DateUtil.compareDate(finCashPlanDto.getMdate(), dto.getMaxTradeDate())>-1){
                            /*finCashPlanDto.setMdate(dto.getMaxTradeDate());*/
                            scai.setVdate(finCashPlanDto.getMdate());
                            break;
                    }
                }
                //交易中计划最后一条计息截止日作为起息日 生成剩下的付息计划
                    /*scai.setVdate(dto.getMaxTradeDate());*/
                }
            }
            if(DateUtil.compareDate(dto.getMdate(), scai.getVdate())<=0){
                scai.setVdate(dto.getVdate());
                scheList.addAll(oldScheList);
            } else {
                scheList = AssetInterestCal.cal(is, scai, holidayList, false,FinCashPlanDto.class);
                if(null!=dto.getMaxTradeDate()){
                    scai.setVdate(dto.getVdate());
                    scheList.addAll(oldScheList);
                }
            }
            rateList = commonRateAdjustService.escapRateAdjust(scai,is,scheList,holidayList);
            //计算付息区间利息
            scheList = InterestScheduleCal.interestScheduleCalA(scheList, scai, invRepayList, null,
                rateList, is, holidayList, scai.getAmount(), FinCashPlanDetailDto.class);
            //排序
            Collections.sort(scheList, new Comparator<FinCashPlanDto>() {
                @Override
                public int compare(FinCashPlanDto o1, FinCashPlanDto o2) {
                    return o2.getVdate().compareTo(o1.getVdate());
                }
            });
            //最后一条付息计划
            FinCashPlanDto finCashPlanDtoLast =scheList.get(0);
            if(finCashPlanDtoLast !=null
                    && finCashPlanDtoLast.getMdate() !=null && finCashPlanDtoLast.getMdateTerm() !=null
                    &&finCashPlanDtoLast.getMdate().compareTo(finCashPlanDtoLast.getMdateTerm()) < 0){
                scheList.get(0).setMdateTerm(finCashPlanDtoLast.getMdate());
            }
        }
        //计算完整周期天数，年付息次数
        for(FinCashPlanDto entity : scheList){
            entity.setTermdays(DateUtil.getBetweenDayNumber(entity.getVdate(),entity.getMdate()));
            entity.setFrequency((long)scheList.size());
            entity.setYield(ArithmeticUtil.multiply(entity.getYield(),new BigDecimal("100")));
            if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                ||StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())
                ||StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
                //计算区间净投资收益率%(加权利率)
                BigDecimal rangeYield = ArithmeticUtil.multiply(WeightedInterestRateCal.cal(rateList, entity.getVdate(), entity.getMdate()),new BigDecimal("100"));
                entity.setRangeYield(rangeYield);
            }
        }
        return scheList;
    }
    
    /**
     * 构建还本计划
     * <功能详细描述>
     * @param scai
     * @param dto
     * @return
     * @see
     * @since 1.0
     */
    private List<FinCashPlanDto>  createInvRepayList(AssetCommonInfoDto scai,FinPayInterestStdInfoDto dto ){
        List<FinCashPlanDto> invRepayList = new ArrayList<FinCashPlanDto>();
        if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_存款.getValue())
            /*||StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())*/){
            FinCashPlanDto newFinCashPlan = new FinCashPlanDto();
            newFinCashPlan.setMdateUnadjust(scai.getMdate());
            newFinCashPlan.setPayDateUnadjust(scai.getMdate());
            newFinCashPlan.setMdate(scai.getMdate());
            newFinCashPlan.setPayDate(scai.getMdate());
            newFinCashPlan.setPrinAmt(dto.getIssueAmt());
            invRepayList.add(newFinCashPlan);
        }else{
            if(Boolean.parseBoolean(dto.getIsAgainCalInt())
                ||(Boolean.parseBoolean(dto.getIscallbackPayByRepay()))){
                //add by wm 20211115 付息计划编辑页面无还本计划，为空不赋值 start
                if(!StringUtil.isEmptyList(dto.getInvRepayList())){
                    invRepayList.addAll(dto.getInvRepayList());
                }
                //add by wm 20211115 付息计划编辑页面无还本计划，为空不赋值 end
                for(FinCashPlanDto cashPlan:invRepayList){
                    if(dto.getIsPayIntFlag()!=null && dto.getIsPayIntFlag().equals(DictCodeList.是否_是.getValue())){
                        cashPlan.setIsPayInt(DictCodeList.是否_否.getValue());
                    }else{
                        cashPlan.setIsPayInt(DictCodeList.是否_是.getValue());
                    }
                }
            }else{
                FinCashPlanDto newFinCashPlan = new FinCashPlanDto();
                newFinCashPlan.setMdateUnadjust(scai.getMdate());
                newFinCashPlan.setPayDateUnadjust(scai.getMdate());
                newFinCashPlan.setMdate(scai.getMdate());
                newFinCashPlan.setPayDate(scai.getMdate());
                newFinCashPlan.setPrinAmt(dto.getIssueAmt());
                if(dto.getIsPayIntFlag().equals(DictCodeList.是否_是.getValue())){//是还本不带息的场景
                    newFinCashPlan.setIsPayInt(DictCodeList.是否_否.getValue());//还本不带息
                }
                invRepayList.add(newFinCashPlan);
            }
        }
        return invRepayList;
    }
}
