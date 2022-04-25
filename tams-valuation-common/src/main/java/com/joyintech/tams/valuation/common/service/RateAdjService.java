/*
 * 文件名：RateAdjService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.common.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.FinCashCalcRuleMapper;
import com.joyintech.tams.common.base.dao.FinCashPlanMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.calculator.InterestScheduleCalReset;
import com.joyintech.tams.valuation.common.calculator.RepayScheduleCal;
import com.joyintech.tams.valuation.common.calculator.WeightedInterestRateCal;
import com.joyintech.tams.valuation.common.asset.dao.RateAdjMapper;
import com.joyintech.tams.valuation.common.asset.dto.*;
import com.joyintech.tams.valuation.common.calculator.WeightedInterestRateCal;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 *  利率调整
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月22日
 * @see RateAdjService
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Service
public class RateAdjService extends BaseService{

    @Autowired
    private RateAdjMapper rateAdjMapper;
    
    @Autowired
    private FinCashPlanMapper finCashPlanMapper;
    
    @Autowired
    private FinCashCalcRuleMapper finCashCalcRuleMapper;
    /**
     * 利率调整后重新计算计划 
     * <功能详细描述>
     * @param entity
     * @return
     * @throws ParseException 
     * @see
     * @since 1.0
     */
    public RateAdjDto againCalCashPlan(RateAdjDto entity) throws ParseException{
      //分支序号
        Long branch =StringUtil.isEmpty(CacheUtil.getSysParam(GlobalConstant.NOBRANCHNOVALUE))?0:Long.parseLong(CacheUtil.getSysParam(GlobalConstant.NOBRANCHNOVALUE));
        // 资产公共DTO
        AssetCommonInfoDto scai =  getAssetBasicInfoByFinprodId(entity.getFinprodId());
        if (scai == null ) {
            return null;
        }
        // 利息计划
        List<FinCashPlan> finCashPlanList = new ArrayList<>();
        // 还本计划
        List<FinCashPlanDto> invRepayList = new ArrayList<>();
        // 参与计算付息计划
        List<FinCashPlanDto> scheList = new ArrayList<>();
        // 报价信息
        List<RateAdjDto> rateList = new ArrayList<RateAdjDto>();
        for (FinCashCalcRule finCashCalcRule : entity.getFinCashCalcRuleList()) {
            RateAdjDto invRateQuo = new RateAdjDto();
            invRateQuo.setEffDate(finCashCalcRule.getEffDate());
            invRateQuo.setYield(finCashCalcRule.getYield());
            invRateQuo.setBasis(scai.getBasis());
            rateList.add(invRateQuo);
        }
        scheList = getFinCashPlanByProductId(entity.getFinprodId(),
            DictCodeListExt1.现金流二级类型_利息.getValue(), DictCodeListExt1.现金流业务类型_金融产品.getValue(),
            null);
        invRepayList = getFinCashPlanByProductId(entity.getFinprodId(),
            DictCodeListExt1.现金流二级类型_本金.getValue(), DictCodeListExt1.现金流业务类型_金融产品.getValue(),null);
        // 计算付息区间利息
        if(StringUtil.equals(scai.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
            scai.setAmount(new BigDecimal("100"));
        }else{
            scai.setAmount(scai.getIssueAmt());
        }
        //平台日
        scai.setSysDate(entity.getNowSystemDate());
        scai.setInvList(scheList);scai.setInvRepayList(invRepayList);
        scai = againCalCashPlan(scai,rateList);
        finCashPlanList.clear();
        //付息计划
        for(FinCashPlan entity1:scai.getInvList()){
            entity1.setBranch(branch);
            entity1.setFinprodId(scai.getFinprodId());
            entity1.setFinprodType(scai.getFinprodType());
            entity1.setFinprodType2(scai.getFinprodType2());
            entity1.setCashBaseamt(scai.getIssueAmt());
            entity1.setCashAmt(entity1.getIntAmt());
            entity1.setVdateUnadjust(entity1.getVdate());
            entity1.setMdateUnadjust(entity1.getMdate());
            entity1.setPayDateUnadjust(entity1.getPayDate());
            entity1.setPayType(DictCodeList.分红方式_现金分红.getValue());
            //项目类资产理论开始日、理论结束日不参与计算逻辑 
            entity1.setVdateTerm(entity1.getVdate());
            entity1.setMdateTerm(entity1.getMdateTerm());
            if(DateUtil.compareDate(scai.getMdate(), entity1.getMdate())==0){
                entity1.setCashType(DictCodeList.现金流类型_到期收息.getValue());
            }else{
                entity1.setCashType(DictCodeList.现金流类型_中间收息.getValue());
            }
            entity1.setPayType(DictCodeList.分红方式_现金分红.getValue());
            if(StringUtil.equals(scai.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                ||StringUtil.equals(scai.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())
                ||StringUtil.equals(scai.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
                //计算区间净投资收益率%(加权利率)
                BigDecimal rangeYield = WeightedInterestRateCal.cal(rateList, entity1.getVdate(), entity1.getMdate());
                entity1.setRangeYield(rangeYield);
            }
            finCashPlanList.add(entity1);
        }
        //还本计划
        for(FinCashPlan entity2:scai.getInvRepayList()){
            entity2.setBranch(branch);
            entity2.setFinprodId(scai.getFinprodId());
            entity2.setFinprodType(scai.getFinprodType());
            entity2.setFinprodType2(scai.getFinprodType2());
            entity2.setVdateUnadjust(entity2.getVdate());
            entity2.setMdateUnadjust(entity2.getMdate());
            entity2.setPayDateUnadjust(entity2.getMdate());
            //项目类资产理论开始日、理论结束日不参与计算逻辑 
            entity2.setVdateTerm(entity2.getVdate());
            entity2.setMdateTerm(entity2.getMdateTerm());
            entity2.setIsPayInt(DictCodeList.是否_是.getValue());
            entity2.setCashBaseamt(scai.getIssueAmt());
            entity2.setCashUnitType(DictCodeList.现金流单位_百元法.getValue());
            if(DateUtil.compareDate(entity2.getMdate(), scai.getMdate())==0){
                entity2.setCashType(DictCodeList.现金流类型_到期还本.getValue());
            }else{
                entity2.setCashType(DictCodeList.现金流类型_中间还本.getValue());
            }
            if(StringUtil.equals(scai.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue())
                ||StringUtil.equals(scai.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue())
                ||StringUtil.equals(scai.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
                //计算区间净投资收益率%(加权利率)
                BigDecimal rangeYield = WeightedInterestRateCal.cal(rateList, entity2.getVdate(), entity2.getMdate());
                entity2.setRangeYield(rangeYield);
            }
            finCashPlanList.add(entity2);
        }
        entity.setFinCashPlanList(finCashPlanList);
        return entity;
    }
    
    /**
     * 计算现金流
     * <功能详细描述>
     * @param scai
     * @param finAssetRateAdjList
     * @return
     * @throws ParseException
     * @see
     * @since 1.0
     */
    public  AssetCommonInfoDto againCalCashPlan(AssetCommonInfoDto scai,List<RateAdjDto> finAssetRateAdjList) throws ParseException{
        // 付息计划(return)
        List<FinCashPlan> finCashPlanList = new ArrayList<>();
        // 还本计划(return)
        List<FinCashPlan> finCashPlanRepayList = new ArrayList<>();
        // 参与计算还本计划
        List<FinCashPlanDto> invRepayList = new ArrayList<>();
        // 参与计算付息计划
        List<FinCashPlanDto> scheList = new ArrayList<>();
        // 付息规则
        FinCashPayRule is = new FinCashPayRule();
        //获取节假日
        List<String> holidayList = getDealCalendarHoliday(scai.getVdate(),scai.getMdate(),scai.getCalendarId());
        // 平台日期
        Date platDate = scai.getSysDate();
        // 计息基础
        //债券
        if(StringUtil.equals(scai.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
            if(StringUtil.equals(scai.getBasis(), DictCodeList.计息基础_A_A_平均分配.getValue())){
                scai.setBasis(DictCodeList.计息基础_A_A.getValue());
                scai.setIntpayRule(DictCodeListExt1.利息分配方式_平均分配.getValue());
            }else if(StringUtil.equals(scai.getBasis(), DictCodeList.计息基础_A_A_按实际天数分配.getValue())){
                scai.setBasis(DictCodeList.计息基础_A_A.getValue());
                scai.setIntpayRule(DictCodeListExt1.利息分配方式_按照实际天数分配.getValue());
            }
        }
        for (FinCashCalcRule finCashCalcRule : finAssetRateAdjList) {
            if(DateUtil.compareDate(scai.getVdate(), finCashCalcRule.getEffDate())==0){
                scai.setRate(finCashCalcRule.getYield());
            }
        }
        //付息规则
        FinCashPayRuleDto finPayInterestStdInfoDto = getFinPayInterestStdInfoByProductId(
            scai.getFinprodId(), DateUtil.format(scai.getVdate()), DictCodeListExt1.现金流二级类型_利息.getValue(),
            DictCodeListExt1.现金流业务类型_金融产品.getValue());
        if(null!=finPayInterestStdInfoDto){
            BeanUtil.copyPropertiesIgnoreNull(is, finPayInterestStdInfoDto);
            scai.setIntpayRule(DictCodeListExt1.利息分配方式_按照实际天数分配.getValue());
            //付息信息
            scheList = scai.getInvList();
            //还本信息
            invRepayList  = scai.getInvRepayList(); 
            //利息--现金流代码
            String intCashId = finPayInterestStdInfoDto.getCashId();
            //本金--现金流代码
            String repayCashId = StringUtil.EMPTY;
            //根据付息计划求出还本计划的计息开始日(注:还本计划计息截止日所在付息区间的计息开始日作为还本计划的计息开始日)
            if(null!=invRepayList&&!invRepayList.isEmpty()){
                repayCashId = invRepayList.get(0).getCashId();
                for(FinCashPlan entity:invRepayList){
                    FinCashPlanDto finCashPlanDto = new FinCashPlanDto();
                    BeanUtil.copyPropertiesIgnoreNull(finCashPlanDto, entity);
                    for(FinCashPlan entity2:scheList){
                        if(DateUtil.compareDate(entity.getMdate(), entity2.getVdate())==1
                            &&DateUtil.compareDate(entity.getMdate(), entity2.getMdate())==-1){
                            entity.setVdate(entity2.getVdate());
                            break;
                        }else if(DateUtil.compareDate(finCashPlanDto.getMdate(), entity2.getMdate())==0){
                            entity.setVdate(entity2.getMdate());
                            break;
                        }
                    }
                }
            }
            //1.重新计算还本区间利息
            invRepayList = RepayScheduleCal.cal(scai, invRepayList, is, finAssetRateAdjList, scai.getAmount(), FinCashPlanDto.class, FinCashPlanDetailDto.class);
            finCashPlanList.clear();finCashPlanRepayList.clear();
            //2.重新计算付息区间利息
            scheList = InterestScheduleCalReset.interestScheduleCalA(scheList, scai, invRepayList, null,
                finAssetRateAdjList, is, holidayList,platDate, null, FinCashPlanDetailDto.class);
            // 计算完整周期天数，年付息次数
            for (FinCashPlanDto entity2 : scheList) {
                entity2.setCashId(intCashId);
                entity2.setTermdays(DateUtil.getBetweenDayNumber(entity2.getVdate(), entity2.getMdate()));
                entity2.setFrequency((long)scheList.size());
            }
            for (FinCashPlanDto entity3 : invRepayList) {
                entity3.setCashId(repayCashId);
                entity3.setTermdays(DateUtil.getBetweenDayNumber(entity3.getVdate(), entity3.getMdate()));
                entity3.setFrequency((long)scheList.size());
            }
        }
        scai.setInvList(scheList);
        scai.setInvRepayList(invRepayList);
        return scai;
    }
    
    /**
     * GET现金流支付规则 
     * <功能详细描述>
     * @param finprodId
     * @param vdate
     * @param cashType
     * @param finprodType
     * @return
     * @see
     * @since 1.0
     */
    public FinCashPayRuleDto getFinPayInterestStdInfoByProductId(String finprodId,String vdate,String cashType,String finprodType){
        Map<String,Object> param = new HashMap<>();
        param.put("finprodId", finprodId);
        param.put("vdate", vdate);
        param.put("cashType", cashType);
        param.put("finprodType", finprodType);
        return rateAdjMapper.getFinPayInterestStdInfoByProductId(param);
    }
    
    /**
     * GET现金流计划
     * <功能详细描述>
     * @param finprodId
     * @param cashType
     * @param finprodType
     * @param finprodType2
     * @return
     * @see
     * @since 1.0
     */
    public List<FinCashPlanDto> getFinCashPlanByProductId(String finprodId, String cashType, String finprodType, String finprodType2){
        Map<String,Object> param = new HashMap<>();
        param.put("finprodId", finprodId);
        param.put("cashType", cashType);
        param.put("finprodType", finprodType);
        param.put("finprodType2", finprodType2);
        return rateAdjMapper.getFinCashPlanByProductId(param);
    }
    /**
     * GET资产公共信息
     * <功能详细描述>
     * @param finprodId
     * @return
     * @see
     * @since 1.0
     */
    public AssetCommonInfoDto getAssetBasicInfoByFinprodId(String finprodId){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("finprodId", finprodId);
        List<String> cashTypeList=new ArrayList<String>();
        cashTypeList.add(DictCodeListExt1.现金流二级类型_利息.getValue());
        cashTypeList.add(DictCodeListExt1.现金流二级类型_收取.getValue());
        cashTypeList.add(DictCodeListExt1.现金流二级类型_支付.getValue());
        map.put("cashTypeList", cashTypeList);
        return rateAdjMapper.getAssetBasicInfoByFinprodId(map);
    }
    
    /**
     * 重新查询利率调整初始化信息
     * <功能详细描述>
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<RateAdjDto> selectInitAssetRateAdj(Map<String,Object> param){
        return rateAdjMapper.selectInitAssetRateAdj(param);
    }
    
    /**
     * 入库
     * <功能详细描述>
     * @param dto RateAdjDto
     * @see
     * @since 1.0
     */
    public void insert(RateAdjDto dto){
      //根据cashId删除计算规则、计算规则扩展
        //FinCashCalcRuleKey finCashCalcRuleKey = new FinCashCalcRuleKey();
       /* PaFinCashCalcRuleKey paFinCashCalcRuleKey = new PaFinCashCalcRuleKey();*/
        //finCashCalcRuleKey.setCashId(dto.getCashId());
        //rateAdjMapper.deleteCashCalcRuleByCashId(finCashCalcRuleKey);
        /*paFinCashCalcRuleKey.setCashId(dto.getCashId());*/
       /* finAssetRateAdjMapper.deleteCashCalcRuleAddByCashId(paFinCashCalcRuleKey);*/
        //新增计算规则、计算规则扩展
        dto.getFinCashCalcRuleList().forEach(entity ->{
            //add by zhangnian 20191024 start
            FinCashCalcRuleKey finCashCalcRuleKey = new FinCashCalcRuleKey();
            finCashCalcRuleKey.setCashId(dto.getCashId());
            finCashCalcRuleKey.setEffDate(entity.getEffDate());
            rateAdjMapper.deleteCashCalcRuleByCashId(finCashCalcRuleKey);
            //add by zhangnian 20191024 end
            finCashCalcRuleMapper.insert(entity);
        });
        //付息计划
        dto.getFinCashPlanList().forEach(entity->{
            FinCashPlanKey key = new FinCashPlanKey();
            key.setCashId(entity.getCashId());
            key.setMdate(entity.getMdate());
            finCashPlanMapper.deleteByPrimaryKey(key);
        });
        dto.getFinCashPlanList().forEach(entity->{
            finCashPlanMapper.insert(entity);
        });
        /*dto.getPaFinCashCalcRuleList().forEach(entity ->{
            paFinCashCalcRuleMapper.insert(entity);
        });*/
        //restTemplate.postForObject(ServiceApplication.ASSET_SERVICE_NAME + "/FinAssetRateAdj/insert", entity, Integer.class);
    }
    
    /**
     * 节假日
     * <功能详细描述>
     * @param startDate
     * @param endDate
     * @param dealCalId
     * @return
     * @see
     * @since 1.0
     */
    public List<String> getDealCalendarHoliday(Date startDate, Date endDate, String dealCalId) {
        Map<String, Object> paraMap = new HashMap<>();
        int days = StringUtil.toInt(CacheUtil.getSysParam(GlobalConstant.HOLIDAY_POSTPONE_DAYS));
        paraMap.put("startDate", DateUtil.addDay(startDate, -days));
        paraMap.put("endDate", DateUtil.addDay(endDate, days));
        paraMap.put("dealCalId", dealCalId);
        return rateAdjMapper.getDealCalendarHoliday(paraMap);
    }
    
}
