/*
 * 文件名：ResetRateProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.batch.resetrate.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.common.base.entity.InterfFloatingcouponsrate;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.valuation.batch.resetbaseruleinfo.dto.MstIntRateInfoDto;
import com.joyintech.tams.valuation.batch.resetrate.dao.ValResetRateMapper;
import com.joyintech.tams.valuation.batch.resetrate.dto.ResetRateReaderResult;
import com.joyintech.tams.valuation.batch.resetrate.dto.ResetRateResult;
import com.joyintech.tams.valuation.common.asset.dto.FinCashCalcRuleDto;
import com.joyintech.tams.valuation.common.asset.dto.FinRateInfoDto;
import com.joyintech.tams.valuation.common.asset.dto.RateAdjDto;
import com.joyintech.tams.valuation.common.calculator.AdjustedTradeDayCal;
import com.joyintech.tams.valuation.common.calculator.CalendarUtil;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.service.RateAdjService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;


/**
 * processor相关业务处理
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月21日
 * @see ResetRateProcessorService
 * @since 1.0
 */
@SuppressWarnings("rawtypes")
@Service
@Scope("prototype")
public class ResetRateProcessorService extends BaseService{

    /**
     * 利率调整公共Service
     */
    @Autowired
    private RateAdjService rateAdjService;
    
    @Autowired
    private ValResetRateMapper valResetRateMapper;
    
    /*@Autowired(required = false)
    private PinganBatchPlatformDateImpl platformDateService;*/
    
    public ResetRateResult execute(Map<String, Object> param1) throws ParseException {
        // 获取reader返回值
        ResetRateReaderResult readerResult = (ResetRateReaderResult)param1.get(
            GlobalConstant.READER_RESULT);

        // 获取待处理任务信息
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param1.get(GlobalConstant.BATCH_INFO);
        
        // 若reader传递的参数为空，不进行后续处理
        if (readerResult == null) {
            return null;
        }
        if(batchExecuteInfo == null){
            return null;
        }
        // 待处理利率数据参数
        FinRateInfoDto  finRateInfoDto = readerResult.getFinRateInfoDto();
        List<FinCashCalcRuleDto> finCashCalcRuleList = new ArrayList<FinCashCalcRuleDto>();
        //债券
        if(StringUtil.equals(finRateInfoDto.getFinprodType(), DictCodeList.业务类型_银行间债券交易.getValue())){
            finCashCalcRuleList = encapFinCashCalcRule1(finRateInfoDto,batchExecuteInfo);
        }else{
            if(StringUtil.equals(finRateInfoDto.getResetType(), DictCodeList.重置类型_基准利率调整规则_基准利率变动后固定间隔生效.getValue())){
                finCashCalcRuleList = encapFinCashCalcRule2(finRateInfoDto,batchExecuteInfo);
            }else if(StringUtil.equals(finRateInfoDto.getResetType(), DictCodeList.重置类型_基准利率调整规则_付息后下一计息期生效.getValue())){
                finCashCalcRuleList = encapFinCashCalcRule3(finRateInfoDto,batchExecuteInfo);
            }else if(StringUtil.equals(finRateInfoDto.getResetType(), DictCodeListExt2.重置类型_随计划重置.getValue())){
                finCashCalcRuleList = encapFinCashCalcRule4(finRateInfoDto,batchExecuteInfo);
            }//zhangnian  add 20191022 利率重置批處理需求修改 start
            else if(StringUtil.equals(finRateInfoDto.getResetType(), DictCodeListExt2.重置类型_随调.getValue())){
                finCashCalcRuleList = encapFinCashCalcRule5(finRateInfoDto,batchExecuteInfo);
            }
            //zhangnian add 20191022 利率重置批處理需求修改 end
        }
        ResetRateResult resultDto = new ResetRateResult();
        //报价信息
        RateAdjDto rateAdjDto = new RateAdjDto();
        List<FinCashCalcRule> finCashCalcRuleList2 = new ArrayList<FinCashCalcRule>();
        Long branch =StringUtil.isEmpty(CacheUtil.getSysParam(GlobalConstant.NOBRANCHNOVALUE))?0:Long.parseLong(CacheUtil.getSysParam(GlobalConstant.NOBRANCHNOVALUE));
        if(null!=finCashCalcRuleList&&!finCashCalcRuleList.isEmpty()){
            rateAdjDto.setFinprodId(finRateInfoDto.getFinprodId());
            //update by zhangnian  20191024 start
            rateAdjDto.setCashId(finRateInfoDto.getCashId());
            //update by zhangnian  20191024 end
            List<RateAdjDto> rateAdjList = getHistoryAssetRateAdj(finRateInfoDto);
            for(RateAdjDto rateAdjDtoEntity : rateAdjList){
                FinCashCalcRule FinCashCalcRule1 = new FinCashCalcRule();
                BeanUtil.copyPropertiesIgnoreNull(FinCashCalcRule1, rateAdjDtoEntity);
                FinCashCalcRule1.setBranch(branch);
                //------add by 裴家俊 2019/04/04--------查询乘以100，再除掉----
                FinCashCalcRule1.setYield(ArithmeticUtil.divide(rateAdjDtoEntity.getYield(),new BigDecimal(100)));
                //------add by 裴家俊 2019/04/04--------查询乘以100，再除掉----
                finCashCalcRuleList2.add(FinCashCalcRule1);
            }
            for(FinCashCalcRuleDto finCashCalcRuleEntity : finCashCalcRuleList) {
                rateAdjDto.setCashId(finCashCalcRuleEntity.getCashId());
                FinCashCalcRule FinCashCalcRule2 = new FinCashCalcRule();
                BeanUtil.copyPropertiesIgnoreNull(FinCashCalcRule2, finCashCalcRuleEntity);
                FinCashCalcRule2.setBranch(branch);
                FinCashCalcRule2.setYield(ArithmeticUtil.divide(finCashCalcRuleEntity.getYield(), new BigDecimal(100)));
                finCashCalcRuleList2.add(FinCashCalcRule2);
            } 
            rateAdjDto.setFinCashCalcRuleList(finCashCalcRuleList2);
            //重新计算现金流
            rateAdjDto.setNowSystemDate(batchExecuteInfo.getExecuteDate());
            /*rateAdjDto.setNowSystemDate(platformDateService.getSysDate());*/
            //sjj add 20190423 利率重置批處理需求修改 start
            if(StringUtil.equals(finRateInfoDto.getResetType(), DictCodeListExt2.重置类型_随调.getValue())){
                rateAdjDto.setFinCashPlanList(new ArrayList<FinCashPlan>());
            }else{
                rateAdjDto =  rateAdjService.againCalCashPlan(rateAdjDto);
            }
//        	rateAdjDto =  rateAdjService.againCalCashPlan(rateAdjDto);
            //sjj add 20190423 利率重置批處理需求修改 end
            resultDto.setRateAdjDto(rateAdjDto);
        }
        resultDto.setProdId("利率重置处理");
        return resultDto;
    }
    
    /**
     * 获取历史计算规则(报价信息)
     * <功能详细描述>
     * @param finRateInfoDto
     * @return
     * @see
     * @since 1.0
     */
    private List<RateAdjDto> getHistoryAssetRateAdj(FinRateInfoDto  finRateInfoDto){
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("finprodId", finRateInfoDto.getFinprodId());
        param.put("finprodType", DictCodeListExt1.现金流业务类型_金融产品.getValue());
        param.put("cashType1", DictCodeListExt1.现金流一级类型_利息.getValue());
        param.put("cashType2", DictCodeListExt1.现金流二级类型_利息.getValue());
        return  rateAdjService.selectInitAssetRateAdj(param);
    }
    /**
     *  利率重置--构建计算规则(债券)
     * <功能详细描述>
     * @param finRateInfoDto
     * @return
     * @see
     * @since 1.0
     */
    private List<FinCashCalcRuleDto> encapFinCashCalcRule1(FinRateInfoDto  finRateInfoDto,SysBatchExecInfo batchExecuteInfo){
        List<FinCashCalcRuleDto> resultList = new ArrayList<FinCashCalcRuleDto>();
        Map<String,Object> paramMap = new HashMap<String, Object>();
        paramMap.put("finprodId", finRateInfoDto.getFinprodId());
        paramMap.put("sysDate",DateUtil.format(batchExecuteInfo.getExecuteDate()));
        //paramMap.put("sysDate",DateUtil.formatDate(DateUtil.format(platformDateService.getSysDate())));
        List<InterfFloatingcouponsrate> interfFloatingcouponsrateList = valResetRateMapper.selectInterfFloatingcouponsrate(paramMap);
        for(InterfFloatingcouponsrate interfFloatingcouponsrate : interfFloatingcouponsrateList){
            FinCashCalcRuleDto finCashCalcRule = setFinCashCalcRuleEntity(finRateInfoDto,batchExecuteInfo);
            finCashCalcRule.setEffDate(DateUtil.parse(interfFloatingcouponsrate.getChangeDt()));
            finCashCalcRule.setYield(ArithmeticUtil.divide(interfFloatingcouponsrate.getRate(), new BigDecimal(100)));
            resultList.add(finCashCalcRule);
        }
        return resultList;
    }
    
    /**
     * 利率重置--基准利率变动固定间隔生效(非债券)
     * <一句话功能简述>
     * <功能详细描述>
     * @param finRateInfoDto
     * @return
     * @see
     * @since 1.0
     */
    private List<FinCashCalcRuleDto> encapFinCashCalcRule2(FinRateInfoDto  finRateInfoDto,SysBatchExecInfo batchExecuteInfo){
        List<FinCashCalcRuleDto> resultList = new ArrayList<FinCashCalcRuleDto>();
        Date D = new Date();
        Map<String,Object> paramMap = new HashMap<String, Object>();
        //获取节假日
        List<String> holidayList = getDealCalendarHoliday(finRateInfoDto.getVdate(),finRateInfoDto.getMdate(),finRateInfoDto.getCalendarId());
        Integer ObserveBefDay = Integer.parseInt(finRateInfoDto.getObserveBefDay().toString());
        if(StringUtil.equals(DictCodeList.提前观察单位_延后时间单位_自然日.getValue(), finRateInfoDto.getObserveBefUnit())){
            //D = CalendarUtil.calendarcal(platformDateService.getSysDate(),Calendar.DATE, ObserveBefDay);
            D = CalendarUtil.calendarcal(batchExecuteInfo.getExecuteDate(),Calendar.DATE, ObserveBefDay);
        }
        else if (DictCodeList.提前观察单位_延后时间单位_自然月.getValue().equals(finRateInfoDto.getObserveBefUnit())) {
            //D = CalendarUtil.calendarcal(platformDateService.getSysDate(),Calendar.MONTH, ObserveBefDay);
            D = CalendarUtil.calendarcal(batchExecuteInfo.getExecuteDate(),Calendar.MONTH, ObserveBefDay);
        }
        else if (DictCodeList.提前观察单位_延后时间单位_工作日.getValue().equals(finRateInfoDto.getObserveBefUnit())) {
            /*D = DateUtil.parse(AdjustedTradeDayCal.calculationTradingDay(
                DateUtil.format(platformDateService.getSysDate()), ObserveBefDay,
                holidayList, DictCodeList.是否_是.getValue()));*/
            D = DateUtil.parse(AdjustedTradeDayCal.calculationTradingDay(
                DateUtil.format(batchExecuteInfo.getExecuteDate()), ObserveBefDay,
                holidayList, DictCodeList.是否_是.getValue()));
        }else{ D = null;}
        if(null!=D){
            paramMap.put("effDate", DateUtil.format(D));
            // 一体化对应etliu start
//            paramMap.put("intRateId", finRateInfoDto.getIndexId());
            paramMap.put("intRateId", finRateInfoDto.getBenchmarkId());
            // 一体化对应etliu end
            MstIntRateInfoDto mstIntRateInfoDto = valResetRateMapper.getMstIntRateInfo(paramMap);
            if(null!=mstIntRateInfoDto){
                FinCashCalcRuleDto finCashCalcRule = setFinCashCalcRuleEntity(finRateInfoDto,batchExecuteInfo);
                //-------modify by 裴家俊----2019/04/04--------start---------
                BigDecimal preRateYield=ArithmeticUtil.doAdd(ArithmeticUtil.multiply(StringUtil.isNotEmpty(mstIntRateInfoDto.getIntRate())?mstIntRateInfoDto.getIntRate():BigDecimal.ZERO,StringUtil.isNotEmpty(finRateInfoDto.getCoefficient())?finRateInfoDto.getCoefficient():new BigDecimal(1)),StringUtil.isNotEmpty(finRateInfoDto.getSpreadRate())?finRateInfoDto.getSpreadRate():BigDecimal.ZERO);
                BigDecimal yieldPre=StringUtil.isNotEmpty(finRateInfoDto.getLowestRate())?preRateYield.compareTo(finRateInfoDto.getLowestRate())>=0?preRateYield:finRateInfoDto.getLowestRate():preRateYield;
                BigDecimal yieldNext=StringUtil.isNotEmpty(finRateInfoDto.getHighestRate())?yieldPre.compareTo(finRateInfoDto.getHighestRate())<=0?yieldPre:finRateInfoDto.getHighestRate():yieldPre;
                //finCashCalcRule.setYield(yieldNext);
                //-------modify by 裴家俊----2019/04/04--------start---------
                finCashCalcRule.setYield(ArithmeticUtil.multiply(yieldNext,new BigDecimal(100)));
                resultList.add(finCashCalcRule);
            }
        }
        return resultList;
    }
    
    /**
     * 利率重置--付息后下个计息期生效(非债券)
     * <功能详细描述>
     * @param finRateInfoDto
     * @return
     * @see
     * @since 1.0
     */
    private List<FinCashCalcRuleDto> encapFinCashCalcRule3(FinRateInfoDto  finRateInfoDto,SysBatchExecInfo batchExecuteInfo){
        List<FinCashCalcRuleDto> resultList = new ArrayList<FinCashCalcRuleDto>();
        Map<String,Object> paramMap = new HashMap<String, Object>();
        paramMap.put("sysDate",DateUtil.format(batchExecuteInfo.getExecuteDate()));
        paramMap.put("cashId", finRateInfoDto.getCashId());
        //paramMap.put("sysDate", DateUtil.format(platformDateService.getSysDate()));
        Date planVdate = valResetRateMapper.getPlanVdateBySysDate(paramMap);
        if(null!=planVdate){
            // 一体化对应etliu start
//          paramMap.put("paramMap", finRateInfoDto.getIndexId());
            paramMap.put("intRateId", finRateInfoDto.getBenchmarkId());
            // 一体化对应etliu end
            MstIntRateInfoDto mstIntRateInfoDto = valResetRateMapper.getMstIntRateInfo2(paramMap);
            if(null!=mstIntRateInfoDto){
                FinCashCalcRuleDto finCashCalcRule = setFinCashCalcRuleEntity(finRateInfoDto,batchExecuteInfo);
                //-------modify by 裴家俊----2019/04/04--------start---------
                BigDecimal preRateYield=ArithmeticUtil.doAdd(ArithmeticUtil.multiply(StringUtil.isNotEmpty(mstIntRateInfoDto.getIntRate())?mstIntRateInfoDto.getIntRate():BigDecimal.ZERO,StringUtil.isNotEmpty(finRateInfoDto.getCoefficient())?finRateInfoDto.getCoefficient():new BigDecimal(1)),StringUtil.isNotEmpty(finRateInfoDto.getSpreadRate())?finRateInfoDto.getSpreadRate():BigDecimal.ZERO);
                BigDecimal yieldPre=StringUtil.isNotEmpty(finRateInfoDto.getLowestRate())?preRateYield.compareTo(finRateInfoDto.getLowestRate())>=0?preRateYield:finRateInfoDto.getLowestRate():preRateYield;
                BigDecimal yieldNext=StringUtil.isNotEmpty(finRateInfoDto.getHighestRate())?yieldPre.compareTo(finRateInfoDto.getHighestRate())<=0?yieldPre:finRateInfoDto.getHighestRate():yieldPre;
                //finCashCalcRule.setYield(yieldNext);
                //-------modify by 裴家俊----2019/04/04--------start---------
                finCashCalcRule.setYield(ArithmeticUtil.multiply(yieldNext,new BigDecimal(100)));
                resultList.add(finCashCalcRule);
            }
        }
        return resultList;
    }
    
    /**
     * 利率重置--随计划重置
     * <功能详细描述>
     * @param finRateInfoDto
     * @return
     * @see
     * @since 1.0
     */
    private List<FinCashCalcRuleDto> encapFinCashCalcRule4(FinRateInfoDto  finRateInfoDto,SysBatchExecInfo batchExecuteInfo){
        List<FinCashCalcRuleDto> resultList = new ArrayList<FinCashCalcRuleDto>();
        Map<String,Object> paramMap = new HashMap<String, Object>();
        paramMap.put("sysDate",DateUtil.format(batchExecuteInfo.getExecuteDate()));
//        paramMap.put("cashId", finRateInfoDto.getCashId());
        // 一体化对应etliu start
        paramMap.put("intRateId", finRateInfoDto.getBenchmarkId());
        // 一体化对应etliu end
        MstIntRateInfoDto mstIntRateInfoDto = valResetRateMapper.getMstIntRateInfo2(paramMap);
        if(null!=mstIntRateInfoDto){
            FinCashCalcRuleDto finCashCalcRule = setFinCashCalcRuleEntity(finRateInfoDto,batchExecuteInfo);
            //有算法，MIN(MAX(#利率行情.生效利率# * 现金流利率信息.系数 + 现金流利率信息.利差，现金流利率信息.利率下限）, 现金流利率信息.利率上限）
            BigDecimal yield = (mstIntRateInfoDto.getIntRate()==null?BigDecimal.ZERO:mstIntRateInfoDto.getIntRate())
            		.multiply(finRateInfoDto.getCoefficient()==null?BigDecimal.ONE:finRateInfoDto.getCoefficient())
            		.add(finRateInfoDto.getSpreadRate()==null?BigDecimal.ZERO:finRateInfoDto.getSpreadRate())
            		.max(finRateInfoDto.getLowestRate()==null?BigDecimal.ZERO:finRateInfoDto.getLowestRate());
            yield = finRateInfoDto.getLowestRate()==null?yield:yield.min(finRateInfoDto.getLowestRate());
            finCashCalcRule.setYield(yield);
//            finCashCalcRule.setYield(mstIntRateInfoDto.getIntRate());
            
            resultList.add(finCashCalcRule);
        }
        return resultList;
    }

    /**
     * 利率重置,重置类型是"随调"情况
     * <功能详细描述>
     * @param finRateInfoDto
     * @return
     * @see
     * @since 1.0
     */
    private List<FinCashCalcRuleDto> encapFinCashCalcRule5(FinRateInfoDto  finRateInfoDto,SysBatchExecInfo batchExecuteInfo){
        List<FinCashCalcRuleDto> resultList = new ArrayList<FinCashCalcRuleDto>();
        Map<String,Object> paramMap = new HashMap<String, Object>();
        paramMap.put("effDate", DateUtil.format(batchExecuteInfo.getExecuteDate()));
        paramMap.put("intRateId", finRateInfoDto.getBenchmarkId());
        MstIntRateInfoDto mstIntRateInfoDto = valResetRateMapper.getMstIntRateInfo(paramMap);
        if(null!=mstIntRateInfoDto){
            FinCashCalcRuleDto finCashCalcRule = setFinCashCalcRuleEntity(finRateInfoDto,batchExecuteInfo);
            BigDecimal preRateYield=ArithmeticUtil.doAdd(ArithmeticUtil.multiply(StringUtil.isNotEmpty(mstIntRateInfoDto.getIntRate())?mstIntRateInfoDto.getIntRate():BigDecimal.ZERO,StringUtil.isNotEmpty(finRateInfoDto.getCoefficient())?finRateInfoDto.getCoefficient():new BigDecimal(1)),StringUtil.isNotEmpty(finRateInfoDto.getSpreadRate())?finRateInfoDto.getSpreadRate():BigDecimal.ZERO);
            BigDecimal yieldPre=StringUtil.isNotEmpty(finRateInfoDto.getLowestRate())?preRateYield.compareTo(finRateInfoDto.getLowestRate())>=0?preRateYield:finRateInfoDto.getLowestRate():preRateYield;
            BigDecimal yieldNext=StringUtil.isNotEmpty(finRateInfoDto.getHighestRate())?yieldPre.compareTo(finRateInfoDto.getHighestRate())<=0?yieldPre:finRateInfoDto.getHighestRate():yieldPre;
            finCashCalcRule.setYield(ArithmeticUtil.multiply(yieldNext,new BigDecimal(100)));
            resultList.add(finCashCalcRule);
        }
        return resultList;
    }

    
    /**
     * setEntity
     * <功能详细描述>
     * @param finRateInfoDto
     * @return
     * @see
     * @since 1.0
     */
    private FinCashCalcRuleDto setFinCashCalcRuleEntity(FinRateInfoDto  finRateInfoDto,SysBatchExecInfo batchExecuteInfo){
        FinCashCalcRuleDto finCashCalcRule = new FinCashCalcRuleDto();
        finCashCalcRule.setCashId(finRateInfoDto.getCashId());
        finCashCalcRule.setEffDate(batchExecuteInfo.getExecuteDate());
        
        // 一体化对应etliu start
//      finCashCalcRule.setCalcType(finRateInfoDto.getRateType());
        finCashCalcRule.setCalcType(finRateInfoDto.getIntType());
        // 一体化对应etliu end
        
        finCashCalcRule.setBaseType(DictCodeList.基数类型_实收资本.getValue());
        finCashCalcRule.setBaseDateType(DictCodeList.基数日期类型_当日.getValue());
        finCashCalcRule.setBasis(finRateInfoDto.getBasis());
        finCashCalcRule.setIsInitial(DictCodeList.是否_否.getValue());
        finCashCalcRule.setFinprodId(finRateInfoDto.getFinprodId());
        finCashCalcRule.setFinprodType(finRateInfoDto.getFinprodType());
        return finCashCalcRule;
    }
    
    public List<String> getDealCalendarHoliday(Date startDate, Date endDate, String dealCalId) {
        Map<String, Object> paraMap = new HashMap<>();
        int days = StringUtil.toInt(CacheUtil.getSysParam(GlobalConstant.HOLIDAY_POSTPONE_DAYS));
        paraMap.put("startDate", DateUtil.addDay(startDate, -days));
        paraMap.put("endDate", DateUtil.addDay(endDate, days));
        paraMap.put("dealCalId", dealCalId);
        return valResetRateMapper.getDealCalendarHoliday(paraMap);
    }
}
