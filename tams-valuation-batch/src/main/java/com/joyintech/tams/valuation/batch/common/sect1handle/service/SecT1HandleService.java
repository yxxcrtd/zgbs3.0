/*
 * 文件名：SecT1HandleService.java
 * 版权：Copyright 2016-2017 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangzhiang
 * 修改时间：2018年5月31日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.common.sect1handle.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListTableId;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.calculation.CalculatorFactory;
import com.joyintech.calculation.bonds.PaperIrReset;
import com.joyintech.calculation.bonds.SecIntSchedule;
import com.joyintech.calculation.bonds.constant.AllocationType;
import com.joyintech.calculation.bonds.constant.Basis;
import com.joyintech.calculation.bonds.constant.IntFrequency;
import com.joyintech.calculation.bonds.constant.IntRateType;
import com.joyintech.calculation.bonds.constant.NextExeCalMethod;
import com.joyintech.calculation.bonds.constant.SecIntSchCalMethod;
import com.joyintech.calculation.bonds.constant.Variety;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.shell.AccurIntForBookCalculator;
import com.joyintech.calculation.shell.SecDscYieldCalculator;
import com.joyintech.calculation.shell.param.AccurIntCalInParameter;
import com.joyintech.calculation.shell.param.AccurIntCalOutParameter;
import com.joyintech.calculation.shell.param.SecDscYieldInParameter;
import com.joyintech.calculation.shell.param.SecDscYieldOutParameter;
import com.joyintech.calculation.shell.param.SecInfoModel;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.calculation.util.BasicUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.batch.common.sect1handle.entity.DealHappenAmtDto;
import com.joyintech.tams.valuation.batch.common.sect1handle.entity.SecT1HandleParamterDto;
import com.joyintech.tams.valuation.batch.common.sect1handle.entity.SecT1HandleResultDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetCashCalcRule;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetCashPayRule;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetCashPlan;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.batch.losscommon.service.LossCommonService;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.common.base.entity.BokAmoConfig;
import com.joyintech.tams.common.base.entity.BokBooksetExt;
import com.joyintech.tams.common.base.entity.BokStatDetPosiiton;
import com.joyintech.tams.common.base.entity.FinExercisePlan;

/**
 * 债券T+1方法
 *
 * @author chengzhong
 * @version 1.0 2018年5月31日
 * @see SecT1HandleService
 * @since 1.0
 */
@Service
public class SecT1HandleService {

    /**
     * 标的共通方法service
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 账务类共通方法类
     */
    @Autowired
    private AccountingCommonService accountingCommonService;
    
    /**
     * 计算类共通方法
     */
    @Autowired
    private CalculationCommonService calculationCommonService;
    
    /**ADD BY CAOGUO ON 20201113 减值相关修改 START **/
    @Autowired
    private LossCommonService lossCommonService;
    /**ADD BY CAOGUO ON 20201113 减值相关修改 END **/

    /**
     * 债券T+1方法 <功能详细描述>
     * 
     * @param secT1HandleParamterDto
     * @return
     * @see
     * @since 1.0
     */
    public SecT1HandleResultDto doSecT1Handle(SecT1HandleParamterDto secT1HandleParamterDto) {
        // 处理结果初始化
        SecT1HandleResultDto result = new SecT1HandleResultDto();
        result.setCbSubjectBalance(
            secT1HandleParamterDto.getCbSubjectBalance() == null ? BigDecimal.ZERO : secT1HandleParamterDto.getCbSubjectBalance());
        result.setGyjzbdSubjectBalance(
            secT1HandleParamterDto.getGyjzbdSubjectBalance() == null ? BigDecimal.ZERO : secT1HandleParamterDto.getGyjzbdSubjectBalance());
        result.setNumBalance(
            secT1HandleParamterDto.getNumBalance() == null ? BigDecimal.ZERO : secT1HandleParamterDto.getNumBalance());
        result.setYjlxSubjectBalance(
            secT1HandleParamterDto.getYjlxSubjectBalance() == null ? BigDecimal.ZERO : secT1HandleParamterDto.getYjlxSubjectBalance());
        result.setZyjSubjectBalance(
            secT1HandleParamterDto.getZyjSubjectBalance() == null ? BigDecimal.ZERO : secT1HandleParamterDto.getZyjSubjectBalance());

        // 二、数据准备
        // 1.标的类金融产品信息
        // 查询条件
        List<TargetSearchDto> queryCriteria = new ArrayList<TargetSearchDto>();
        // 标的信息条件
        TargetSearchDto searchBaseinfo = new TargetSearchDto();
        searchBaseinfo.setQueryType(ValDictCodeList.标的_信息.getValue());
        queryCriteria.add(searchBaseinfo);

        // 还本计划条件
        TargetSearchDto searchRedinfo = new TargetSearchDto();
        searchRedinfo.setQueryType(ValDictCodeList.标的_现金流计划.getValue());
        searchRedinfo.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
        queryCriteria.add(searchRedinfo);

        // 付息计划条件
        TargetSearchDto searchIntinfo = new TargetSearchDto();
        searchIntinfo.setQueryType(ValDictCodeList.标的_现金流计划.getValue());
        searchIntinfo.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        queryCriteria.add(searchIntinfo);

        // 报价计划条件
        TargetSearchDto searchCashinfo = new TargetSearchDto();
        searchCashinfo.setQueryType(ValDictCodeList.标的_现金流计算规则.getValue());
        searchCashinfo.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        queryCriteria.add(searchCashinfo);

        // 现金流支付规则条件
        TargetSearchDto searchPayinfo = new TargetSearchDto();
        searchPayinfo.setQueryType(ValDictCodeList.标的_现金流支付规则.getValue());
        searchPayinfo.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        queryCriteria.add(searchPayinfo);

        Map<TargetSearchDto, Object> targetSearchMap = finProductCommonService.getTargetInfo(
            secT1HandleParamterDto.getBusiId(), queryCriteria);
        // 变量.标的信息 = 上述共通返回标的信息
        TargetFinProduct rstBaseinfo = (TargetFinProduct)targetSearchMap.get(searchBaseinfo);

        // 变量.付息列表 = 上述共通返回的计划列表
        @SuppressWarnings("unchecked")
        List<TargetCashPlan> listIntinfo = targetSearchMap.get(
            searchIntinfo) == null ? null : (List<TargetCashPlan>)targetSearchMap.get(
                searchIntinfo);

        // 变量.还本列表 = 上述共通返回还本列表
        @SuppressWarnings("unchecked")
        List<TargetCashPlan> listRedinfo = targetSearchMap.get(
            searchRedinfo) == null ? null : (List<TargetCashPlan>)targetSearchMap.get(
                searchRedinfo);

        // 变量.报价列表 = 上述共通返回报价列表
        @SuppressWarnings("unchecked")
        List<TargetCashCalcRule> listCashinfo = targetSearchMap.get(
            searchCashinfo) == null ? null : (List<TargetCashCalcRule>)targetSearchMap.get(
                searchCashinfo);

        // 变量.支付规则 = 上述共通返回支付规则列表
        @SuppressWarnings("unchecked")
        List<TargetCashPayRule> listPayinfo = targetSearchMap.get(
            searchPayinfo) == null ? null : (List<TargetCashPayRule>)targetSearchMap.get(
                searchPayinfo);

        // 2.T+1区间类计划查询
        // 定义 变量.T+1区间内付息计划 类型：<key:计息结束日，Value：现金流计划>
        Map<String, TargetCashPlan> intMap = new HashMap<>();
        // 定义 变量.T+1区间内还本计划 类型：<key:计息结束日，Value：现金流计划>
        Map<String, TargetCashPlan> redMap = new HashMap<>();

        if (listIntinfo != null && listIntinfo.size() > 0) {
            for (TargetCashPlan intinfo : listIntinfo) {
                // if（参数.交易日 < 变量.付息计划.计息结束日 and 参数.交割日 >= 变量.付息计划.计息结束日）
                if (secT1HandleParamterDto.getDealDate().compareTo(intinfo.getMdate()) < 0
                    && secT1HandleParamterDto.getSettleDate().compareTo(intinfo.getMdate()) >= 0) {
                    // 变量.T+1区间内付息计划.put(变量.付息计划.计息结束日, 变量.付息计划)
                    intMap.put(DateUtil.format(intinfo.getMdate()), intinfo);
                }
            }
        }

        if (listRedinfo != null && listRedinfo.size() > 0) {
            for (TargetCashPlan redinfo : listRedinfo) {
                // if（参数.交易日 < 变量.还本计划.计息结束日 and 参数.交割日 >= 变量.还本计划.计息结束日）
                if (secT1HandleParamterDto.getDealDate().compareTo(redinfo.getMdate()) < 0
                    && secT1HandleParamterDto.getSettleDate().compareTo(redinfo.getMdate()) >= 0) {
                    // 变量.T+1区间内还本计划.put(变量.还本计划.计息结束日, 变量.还本计划)
                    redMap.put(DateUtil.format(redinfo.getMdate()), redinfo);
                }
            }
        }

        // 3.行权计划查询
        // 行权计划
        FinExercisePlan finExercisePlan = null;
        // 变量.行权日百元券面
        BigDecimal varPlanFaceValue = BigDecimal.ZERO;
        // if（变量.标的信息.是否含权 = "是"）
        if (DictCodeList.是否_是.getValue().equals(rstBaseinfo.getIsRight())) {
            String exerciseType = null;
            if(DictCodeList.含权债摊销日_最近赎回行权日.getValue().equals(rstBaseinfo.getExeDate())){
                exerciseType = DictCodeList.行权方式_赎回.getValue();
            }
            else if (DictCodeList.含权债摊销日_最近回售行权日.getValue().equals(rstBaseinfo.getExeDate())){
                exerciseType = DictCodeList.行权方式_回售.getValue();
            }
            // 调用标的类共通下一行权计划查询
            finExercisePlan = finProductCommonService.queryFinExercisePlan(
                secT1HandleParamterDto.getBusiId(), DictCodeListExt1.金融产品类型_债券.getValue(),
                secT1HandleParamterDto.getDealDate(),exerciseType);
            if (finExercisePlan != null) {
                // 百元券面查询
                varPlanFaceValue = finProductCommonService.getFaceValue(
                    secT1HandleParamterDto.getBusiId(), finExercisePlan.getExerciseDate());
            }
        }
        /**ADD BY WUJIE ON 20191216 START**/
        //汇总信息查
        //变量.昨日统计分析账务明细汇总信息
        BokStatDetPosiiton bokSummaryTableYestdy = accountingCommonService.getCountAnalysis(
                DateUtil.addDay(secT1HandleParamterDto.getDealDate(), -1),
                DateUtil.addDay(secT1HandleParamterDto.getDealDate(), -1), secT1HandleParamterDto.getDealDate(),
                secT1HandleParamterDto.getBooksetId(), secT1HandleParamterDto.getBokDetailId(), null, null);
        //变量.摊销日利率 = null						
        BigDecimal dailydscYield = null;
        if(bokSummaryTableYestdy!=null){
        	dailydscYield = bokSummaryTableYestdy.getDailydscYield();
        }
        /**ADD BY WUJIE ON 20191216 END**/
        
        
        // 三、处理详细
        // 变量.日期 = 参数.交易日
        Date varDate = secT1HandleParamterDto.getDealDate();
        // while (变量.日期 < 交割日)
        while (varDate.compareTo(secT1HandleParamterDto.getSettleDate()) < 0) {
        	/**ADD BY WUJIE ON 20191216 START**/
        	boolean 重算状态 = false;
        	/**ADD BY WUJIE ON 20191216 END**/
        	
        	//变量.应计利息 = 0
        	BigDecimal varIntAmt = BigDecimal.ZERO;

            // 调用共通 百元券面处理
            BigDecimal varFaceValue = finProductCommonService.getFaceValue(
                secT1HandleParamterDto.getBusiId(), varDate);

            // 3.1 计提处理
            // if（是否零息/贴现 != true or 投资目的 = 以公允价值变动计量）
            if (!secT1HandleParamterDto.isNotLxtx()
                || DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(
                    secT1HandleParamterDto.getInvAim())) {
                // 债券应计利息计算
                AccurIntCalOutParameter accurIntCalOutParameter = getCalculate(rstBaseinfo,
                    listIntinfo, listCashinfo, listPayinfo, varFaceValue, varDate);
                // 返回值.应计利息做小数位处理
                BigDecimal interest = accurIntCalOutParameter.getInterest();
                // if（标的信息.交易场所 = "银行间"）
                if (DictCodeListExt1.估值交易市场_银行间.getValue().equals(rstBaseinfo.getTradeMarket())) {
                    // 调用金额共通方法设置有效小数位,小数位数取值共通参数 SEC_INT_SCALE_BANK
                    interest = ArithmeticUtil.round(interest,
                        Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam(
                            "SEC_INT_SCALE_BANK")) ? "12" : CacheUtil.getSysParam(
                                "SEC_INT_SCALE_BANK")),
                        null);
                }
                /**add by taoran 20200110 start**/
                //else if（标的信息.交易场所 = "上交所" or 标的信息.交易场所 = "深交所"）	
                else if(DictCodeListExt1.估值交易市场_上交所.getValue().equals(rstBaseinfo.getTradeMarket()) || 
                			DictCodeListExt1.估值交易市场_深交所.getValue().equals(rstBaseinfo.getTradeMarket())) {
                	//调用金额共通方法设置有效小数位,小数位数取值共通参数 SEC_INT_SCALE_EXCHANGE
                	interest = ArithmeticUtil.round(interest,
                            Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam(
                                "SEC_INT_SCALE_EXCHANGE")) ? "8" : CacheUtil.getSysParam(
                                    "SEC_INT_SCALE_EXCHANGE")),
                            null);
                }
                else {
                    // 调用金额共通方法设置有效小数位,小数位数取值共通参数 SEC_INT_SCALE_EXCHANGE
                    interest = ArithmeticUtil.round(interest,
                        Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam(
                            "SEC_INT_SCALE_BANK")) ? "12" : CacheUtil.getSysParam(
                                "SEC_INT_SCALE_BANK")),
                        null);
                }
                /**add by taoran 20200110 end**/
                BigDecimal temp = result.getYjlxSubjectBalance();
                // 出参.T+1处理结果集.应计利息科目余额 = 上述返回值 * 出参.T+1处理结果集.数量余额 * 变量.百元券面面值 /100
                BigDecimal yjlxSubjectBalance = ArithmeticUtil.multiply(
                		interest, result.getNumBalance());
                yjlxSubjectBalance = ArithmeticUtil.multiply(yjlxSubjectBalance, varFaceValue);
                yjlxSubjectBalance = ArithmeticUtil.divide(yjlxSubjectBalance, new BigDecimal(100),
                    NumberEnum.HIGH_PREC_NUM.getCode());
                result.setYjlxSubjectBalance(yjlxSubjectBalance);
                
                //变量.应计利息 = 出参.T+1处理结果集.应计利息科目余额 - temp
                varIntAmt = ArithmeticUtil.subtract(yjlxSubjectBalance, temp);
            }

            // 3.2 摊销处理
            // if(！投资目的 = 以公允价值变动计量)
            if (!secT1HandleParamterDto.getInvAim().equals(
                DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue())) {
                // if（溢折价类科目余额 != 0 ）
            	if(BigDecimal.ZERO.compareTo(result.getZyjSubjectBalance())!=0){
            		/**ADD BY WUJIE ON 20191216 START**/
            		//if (重算日期集合.get（变量.日期） != null && 重算日期集合.get（变量.日期）= true)																								
            		if(secT1HandleParamterDto.getRetryDateMap().get(varDate) != null
            				&& secT1HandleParamterDto.getRetryDateMap().get(varDate)){
            			重算状态 = true;
            		}
            		/**ADD BY WUJIE ON 20191216 END**/
            		/**ADD BY WUJIE ON 20191230 START**/
            		else if(secT1HandleParamterDto.getRetryDateMap().get(varDate) ==null){
            			int count = 0;
            			String baseSubjectNature = "";
            	        String mrTableId = "";//买入会计场景
            	        
            	        //if(投资目的 = 数据字典.投资目的_公允价值变动计入当期损益){
            	        if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(secT1HandleParamterDto.getInvAim())) {
            	            // 变量.成本基础科目性质 = 基础科目性质数据字典.公允价值损益类_成本;
            	            baseSubjectNature = DictCodeBaseSubjectNatrue.公允价值损益类_成本.getValue();
            	            mrTableId = DictCodeListTableId.会计场景代码_公允价值损益类债券_买入.getValue();
            	        }
            	        // if(投资目的 = 数据字典.投资目的.投资目的_摊余成本类){
            	        else if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(secT1HandleParamterDto.getInvAim())) {
            	            //变量.成本基础科目性质 = 基础科目性质数据字典.摊余成本类成本;
            	            baseSubjectNature = DictCodeBaseSubjectNatrue.摊余成本类成本.getValue();
            	            mrTableId = DictCodeListTableId.会计场景代码_摊余成本类债券_买入.getValue();
            	        }
            	        //if(投资目的 = 数据字典.投资目的_综合收益类成本){
            	        else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(secT1HandleParamterDto.getInvAim())) {
            	            //变量.成本基础科目性质 = 基础科目性质数据字典.公允价值损益类_成本;
            	            baseSubjectNature = DictCodeBaseSubjectNatrue.综合收益类成本.getValue();
            	            mrTableId = DictCodeListTableId.会计场景代码_综合收益类债券_买入.getValue();
            	        }
            	        
            	        // 变量.买入交易列表 = 调用共通方法[账务类共通方法设计 - 凭证最新账务查询]
                        if (mrTableId != null) {
                            count = accountingCommonService.getBookLatestInfoOnway(secT1HandleParamterDto.getBooksetId(),
                            		secT1HandleParamterDto.getDealDate(), 
                            		secT1HandleParamterDto.getBokDetailId(), Arrays.asList(baseSubjectNature),
                            		mrTableId, varDate);
                        }
            	        if(count>0) {
            	        	重算状态 = true;
            	        	secT1HandleParamterDto.getRetryDateMap().put(varDate, true);
            	        }
            		}
            		/**ADD BY WUJIE ON 20191230 END**/
                    // 变量.最新报价
                    BigDecimal newCash = null;
                    if (listCashinfo != null && listCashinfo.size() > 0) {
                        newCash = BigDecimal.ZERO;
                        for (TargetCashCalcRule targetCashCalcRule : listCashinfo) {
                            // 报价.生效日 > 计提类账务处理结果.发生日
                            if (targetCashCalcRule.getEffDate().compareTo(varDate) > 0) {
                                break;
                            }
                            /**ADD BY WUJIE ON 20191216 START**/
                            //if（报价temp.生效日 =  变量.日期 && 变量.最新报价 != 报价temp.生效利率)
                            if(targetCashCalcRule.getEffDate().compareTo(varDate)==0
                            		&& newCash.compareTo(targetCashCalcRule.getYield())!=0){
                            	重算状态 = true;
                            }
                            /**ADD BY WUJIE ON 20191216 END**/
                            // 变量.最新报价 = 报价temp.生效利率
                            newCash = targetCashCalcRule.getYield();
                        }
                    }
                    /**ADD BY CAOGUO ON 20201113 减值相关增加 START */
                    List<String> paramCodeList = new ArrayList<String>();
                    paramCodeList.add(ValDictCodeList.账套配置表参数项_减值资产第三阶段是否重新计算实际利率.getValue()); 
                    Map<String, Object> getValBokBooksetExtMap = accountingCommonService
                            .getValBokBooksetExtForMap(secT1HandleParamterDto.getBooksetId(), paramCodeList);
                    String recalcType = "";
                    if(getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_减值资产第三阶段是否重新计算实际利率.getValue()) != null){
                        recalcType = (String)getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_减值资产第三阶段是否重新计算实际利率.getValue());
                    }
                    // if (减值资产第三阶段是否重新计算实际利率 = 02：重算 ）
                    if (DictCodeListExt3.减值资产第三阶段是否重新计算实际利率_重算.getValue().equals(
                        recalcType)) {
                        if (lossCommonService.isP3InOrOut(varDate,
                            rstBaseinfo.getFinprodId(), null, null)) {
                            重算状态 = true;
                        }
                    }
                    /**ADD BY CAOGUO ON 20201113 减值相关增加  end */
                    /**ADD BY WUJIE ON 20191216 START**/
                    // 变量.摊销重算配置DTO = 调用计算类共通【摊销重算配置查询
					List<BokAmoConfig> bokAmoConfigList = calculationCommonService.getBokAmoConfig(
							secT1HandleParamterDto.getBooksetId(), secT1HandleParamterDto.getBusiId(), 
							varDate);
					BokAmoConfig bokAmoConfig = bokAmoConfigList != null && bokAmoConfigList.size() > 0
							? bokAmoConfigList.get(0) : null;
                    //if （变量.摊销重算配置DTO != null ）										
                    if(bokAmoConfig != null){
                    	//if (变量.摊销重算配置DTO.重算方式 = “重算”)															
                    	if(DictCodeList.重算方式_重算.getValue().equals(bokAmoConfig.getRecalType())){
                    		重算状态 = true;
                    	}
                    	//else if (变量.摊销重算配置DTO.重算方式 = “指定利率” )
                    	else if(DictCodeList.重算方式_指定实际利率.getValue().equals(bokAmoConfig.getRecalType())){
                    		dailydscYield = bokAmoConfig.getDailydscYield();
                    		重算状态 = false;
                    	}
                    	//else if (变量.摊销重算配置DTO.重算方式 = “不重算” )
                    	if(DictCodeList.重算方式_不重算.getValue().equals(bokAmoConfig.getRecalType())){
                    		重算状态 = false;
                    	}	
                    }
                    //if（变量.重算状态  = 重算 || 变量.摊销日利率 = null）
                    if(重算状态 || dailydscYield== null){
                    /**ADD BY WUJIE ON 20191216 END**/
	                    SecDscYieldOutParameter secDscYieldOutParameter = getSecDscYieldCalculator(
	                        varFaceValue, varDate, rstBaseinfo, listIntinfo, listCashinfo, listPayinfo,
	                        result, newCash, finExercisePlan, varPlanFaceValue);
	                    BigDecimal varLxtz = BigDecimal.ZERO;
	                    if (secDscYieldOutParameter != null) {
	                    	varLxtz = ArithmeticUtil.subtract(
	                                ArithmeticUtil.multiply(secDscYieldOutParameter.getDayRate(),
	                                    ArithmeticUtil.doAdd(result.getCbSubjectBalance(), result.getZyjSubjectBalance())),
	                                ArithmeticUtil.round(varIntAmt, NumberEnum.MONEY), NumberEnum.MONEY.getCode(), null);
	                    }
	                    dailydscYield = secDscYieldOutParameter.getDayRate();
	                    // 出参.T+1处理结果集.溢折价类科目余额 += 当日摊销发生额
	                    result.setZyjSubjectBalance(ArithmeticUtil.doAdd(result.getZyjSubjectBalance(),
	                    		varLxtz));
	                /**ADD BY WUJIE ON 20191216 START**/
                    }else{
                    	BigDecimal varLxtz = BigDecimal.ZERO;
	                    	varLxtz = ArithmeticUtil.subtract(
	                                ArithmeticUtil.multiply(dailydscYield,
	                                    ArithmeticUtil.doAdd(result.getCbSubjectBalance(), result.getZyjSubjectBalance())),
	                                ArithmeticUtil.round(varIntAmt, NumberEnum.MONEY), NumberEnum.MONEY.getCode(), null);
	                    // 出参.T+1处理结果集.溢折价类科目余额 += 当日摊销发生额
	                    result.setZyjSubjectBalance(ArithmeticUtil.doAdd(result.getZyjSubjectBalance(),
	                    		varLxtz));
                    }
                    /**ADD BY WUJIE ON 20191216 END**/
                }
            }

            // 3.3.当日公允价值变动处理
            // 调用账务类共通【账套参数查询】
            String gyjzbdJtlx = DictCodeListExt1.公允价值变动计提日类型_交易日.getValue();
            List<BokBooksetExt> listBokBooksetExt = accountingCommonService.getValBokBooksetExt(
                secT1HandleParamterDto.getBooksetId(),
                new ArrayList<String>(Arrays.asList(DictCode.公允价值变动计提日类型.getValue())));
            if (listBokBooksetExt != null && listBokBooksetExt.size() > 0) {
                gyjzbdJtlx = listBokBooksetExt.get(0).getParamValue();
            }
            if (DictCodeListExt1.公允价值变动计提日类型_交割日.getValue().equals(gyjzbdJtlx)) {
                // if(！投资目的 = 以摊余成本计量)
                if (!DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(
                    secT1HandleParamterDto.getInvAim())) {
                    // 调用共通取债券最新市价
                    BigDecimal marketPrice = finProductCommonService.getBondMarketPrice(varDate,
                        secT1HandleParamterDto.getBooksetId(), secT1HandleParamterDto.getBusiId(),
                        secT1HandleParamterDto.getInvAim()).getPrice();
                    // if（参数.是否零息/贴现 = "是"）
//                    if (secT1HandleParamterDto.isNotLxtx()) {
                        // if（变量.市价 = null）
                    if (marketPrice == null || marketPrice.compareTo(BigDecimal.ZERO) == 0) {
                        // 变量.市价 = （变量.当日计算成本科目余额+变量.当日计算溢折价科目余额）/变量.当日计算成本科目数量
                        marketPrice = ArithmeticUtil.divide(
                            ArithmeticUtil.doAdd(result.getCbSubjectBalance(),
                                result.getZyjSubjectBalance()),
                            result.getNumBalance(), NumberEnum.HIGH_PREC_NUM.getCode());
                    }

                    // 出参.T+1处理结果集.公允价值变动类科目余额 =
                    // （变量.市价*出参.T+1处理结果集.数量余额 - （出参.T+1处理结果集.成本类科目余额 + 出参.T+1处理结果集.溢折价类科目余额 ））
                    BigDecimal gyjzbd = ArithmeticUtil.multiply(marketPrice,
                        result.getNumBalance());
                    gyjzbd = ArithmeticUtil.subtract(gyjzbd, ArithmeticUtil.doAdd(
                        result.getCbSubjectBalance(), result.getZyjSubjectBalance()));
                    result.setGyjzbdSubjectBalance(gyjzbd);
//                    }
//                    else {
//                        // if（变量.市价 = null）
//                        if (marketPrice == null || marketPrice.compareTo(BigDecimal.ZERO) == 0) {
//                            // 变量.市价 = （变量.当日计算成本科目余额+变量.当日计算溢折价科目余额）/变量.当日计算成本科目数量
//                            marketPrice = ArithmeticUtil.divide(
//                                ArithmeticUtil.doAdd(result.getCbSubjectBalance(),
//                                    result.getZyjSubjectBalance()),
//                                result.getNumBalance(), NumberEnum.HIGH_PREC_NUM.getCode());
//                        }
//                        // 出参.T+1处理结果集.公允价值变动类科目余额 =
//                        // （变量.市价*出参.T+1处理结果集.数量余额 - （出参.T+1处理结果集.成本类科目余额 + 出参.T+1处理结果集.溢折价类科目余额)）
//                        BigDecimal gyjzbd = ArithmeticUtil.multiply(marketPrice,
//                            result.getNumBalance());
//                        gyjzbd = ArithmeticUtil.subtract(gyjzbd, ArithmeticUtil.doAdd(
//                            result.getCbSubjectBalance(), result.getZyjSubjectBalance()));
//                        result.setGyjzbdSubjectBalance(gyjzbd);
//                    }
                }
            }
            // 3.4 日期加1
            // 变量.日期 = 变量.日期 + 1
            varDate = DateUtil.addDay(varDate, 1);
            /**add by taoran 20200110 start**/
            //if (DictCodeListExt1.估值交易市场_银行间.getValue().equals(secT1HandleParamterDto.getTradeMarket())) {
        	if (!DictCodeListExt1.估值交易市场_上交所.getValue().equals(secT1HandleParamterDto.getTradeMarket()) && 
        			!DictCodeListExt1.估值交易市场_深交所.getValue().equals(secT1HandleParamterDto.getTradeMarket())) {
    		/**add by taoran 20200110 start**/
                // 3.5 付息计划处理
                // if (变量.T+1区间内付息计划.get(变量.日期) != null)
                if (intMap.get(DateUtil.format(varDate)) != null) {
                    // 出参.T+1处理结果集.应计利息科目余额 = 0
                    result.setYjlxSubjectBalance(BigDecimal.ZERO);
                }
    
                // 3.6 还本计划处理
                queryRedPlan(redMap, varDate, varFaceValue, result);
            }
            // 3.7 买入交易
            // if (参数.T+1买入交易.get(变量.日期) != null)
            if (secT1HandleParamterDto.getT1BuyDeal() != null
                && secT1HandleParamterDto.getT1BuyDeal().get(DateUtil.format(varDate)) != null) {
                // 变量.发生额集= 参数.T+1买入交易.get(变量.日期)
                DealHappenAmtDto dealHappenAmtDto = secT1HandleParamterDto.getT1BuyDeal().get(
                    DateUtil.format(varDate));
                // 出参.T+1处理结果集.应计利息科目余额
                result.setYjlxSubjectBalance(ArithmeticUtil.doAdd(result.getYjlxSubjectBalance(),
                    dealHappenAmtDto.getHappenYjlx()));
                // 出参.T+1处理结果集.溢折价类科目余额
                result.setZyjSubjectBalance(ArithmeticUtil.doAdd(result.getZyjSubjectBalance(),
                    dealHappenAmtDto.getHappenLxtz()));
                // 出参.T+1处理结果集.成本类科目余额
                result.setCbSubjectBalance(ArithmeticUtil.doAdd(result.getCbSubjectBalance(),
                    dealHappenAmtDto.getHappenCb()));
                // 出参.T+1处理结果集.数量余额
                result.setNumBalance(
                    ArithmeticUtil.doAdd(result.getNumBalance(), dealHappenAmtDto.getHappenNum()));
            }

            // 3.8 卖出交易
            // if (参数.T+1卖出交易.get(变量.日期) != null)
            if (secT1HandleParamterDto.getT1SellDeal() != null
                && secT1HandleParamterDto.getT1SellDeal().get(DateUtil.format(varDate)) != null) {
                // 变量.发生额集 = 参数.T+1卖出交易.get(变量.日期)
                DealHappenAmtDto dealHappenAmtDto = secT1HandleParamterDto.getT1SellDeal().get(
                    DateUtil.format(varDate));
                // 出参.T+1处理结果集.公允价值变动类科目余额 -= 变量.发生额集.发生公允价值变动
                result.setGyjzbdSubjectBalance(ArithmeticUtil.subtract(
                    result.getGyjzbdSubjectBalance(), dealHappenAmtDto.getHappenGujzbd()));
                // 出参.T+1处理结果集.应计利息科目余额
                result.setYjlxSubjectBalance(ArithmeticUtil.subtract(result.getYjlxSubjectBalance(),
                    dealHappenAmtDto.getHappenYjlx()));
                // 出参.T+1处理结果集.溢折价类科目余额
                result.setZyjSubjectBalance(ArithmeticUtil.subtract(result.getZyjSubjectBalance(),
                    dealHappenAmtDto.getHappenLxtz()));
                // 出参.T+1处理结果集.成本类科目余额
                result.setCbSubjectBalance(ArithmeticUtil.subtract(result.getCbSubjectBalance(),
                    dealHappenAmtDto.getHappenCb()));
                // 出参.T+1处理结果集.数量余额
                result.setNumBalance(
                    ArithmeticUtil.subtract(result.getNumBalance(), dealHappenAmtDto.getHappenNum()));

            }

        }

        // 出参.T+1处理结果集.公允价值变动类科目余额
        result.setGyjzbdSubjectBalance(
            ArithmeticUtil.round(result.getGyjzbdSubjectBalance(), NumberEnum.MONEY));
        // 出参.T+1处理结果集.应计利息科目余额
        result.setYjlxSubjectBalance(
            ArithmeticUtil.round(result.getYjlxSubjectBalance(), NumberEnum.MONEY));
        // 出参.T+1处理结果集.溢折价类科目余额
        result.setZyjSubjectBalance(
            ArithmeticUtil.round(result.getZyjSubjectBalance(), NumberEnum.MONEY));
        // 出参.T+1处理结果集.成本类科目余额
        result.setCbSubjectBalance(
            ArithmeticUtil.round(result.getCbSubjectBalance(), NumberEnum.MONEY));
        // 出参.T+1处理结果集.数量余额
        result.setNumBalance(ArithmeticUtil.round(result.getNumBalance(), NumberEnum.MONEY));
        return result;
    }

    /**
     * 还本计划处理 <功能详细描述>
     * 
     * @param redMap
     * @param varDate
     * @param varFaceValue
     * @param result
     * @see
     * @since 1.0
     */
    private void queryRedPlan(Map<String, TargetCashPlan> redMap, Date varDate,
                              BigDecimal varFaceValue, SecT1HandleResultDto result) {
        // if (变量.T+1区间内还本计划.get(变量.日期) != null)
        if (redMap.get(DateUtil.format(varDate)) != null) {
            // 还本计划 = 变量.T+1区间内还本计划.get(变量.日期)
            TargetCashPlan redPlan = redMap.get(DateUtil.format(varDate));
            // 出参.T+1处理结果集.公允价值变动类科目余额
            // = 出参.T+1处理结果集.公允价值变动类科目余额 -
            // （出参.T+1处理结果集.公允价值变动类科目余额 * 还本计划.计划支付本金/变量.百元券面面值）
            BigDecimal gyjzbd = ArithmeticUtil.divide(
                ArithmeticUtil.multiply(result.getGyjzbdSubjectBalance(), redPlan.getPrinAmt()),
                varFaceValue, NumberEnum.HIGH_PREC_NUM.getCode());
            result.setGyjzbdSubjectBalance(
                ArithmeticUtil.subtract(result.getGyjzbdSubjectBalance(), gyjzbd));

            // 出参.T+1处理结果集.溢折价类科目余额
            // = 出参.T+1处理结果集.溢折价类科目余额 -
            // （出参.T+1处理结果集.溢折价类科目余额 * 还本计划.计划支付本金/变量.百元券面面值）
            BigDecimal zyj = ArithmeticUtil.divide(
                ArithmeticUtil.multiply(result.getZyjSubjectBalance(), redPlan.getPrinAmt()),
                varFaceValue, NumberEnum.HIGH_PREC_NUM.getCode());
            result.setZyjSubjectBalance(
                ArithmeticUtil.subtract(result.getZyjSubjectBalance(), zyj));

            // 出参.T+1处理结果集.应计利息科目余额
            // = 出参.T+1处理结果集.应计利息科目余额 -
            // （出参.T+1处理结果集.应计利息科目余额 * 还本计划.计划支付本金/变量.百元券面面值）
            BigDecimal yjlx = ArithmeticUtil.divide(
                ArithmeticUtil.multiply(result.getYjlxSubjectBalance(), redPlan.getPrinAmt()),
                varFaceValue, NumberEnum.HIGH_PREC_NUM.getCode());
            result.setYjlxSubjectBalance(
                ArithmeticUtil.subtract(result.getYjlxSubjectBalance(), yjlx));

            // 出参.T+1处理结果集.成本类科目余额
            // = 出参.T+1处理结果集.成本类科目余额 -
            // （出参.T+1处理结果集.成本类科目余额 * 还本计划.计划支付本金/变量.百元券面面值）
            BigDecimal cb = ArithmeticUtil.divide(
                ArithmeticUtil.multiply(result.getCbSubjectBalance(), redPlan.getPrinAmt()),
                varFaceValue, NumberEnum.HIGH_PREC_NUM.getCode());
            result.setCbSubjectBalance(ArithmeticUtil.subtract(result.getCbSubjectBalance(), cb));
        }
    }

    /**
     * 利息计提-计算百元面值的债券的利息
     * 
     * @param rstBaseinfo 标的信息
     * @param listIntinfo 付息计划列表
     * @param listCashinfo 报价信息列表
     * @param listPayinfo 支付规则
     * @param varFaceValue 百元面值
     * @return AccurIntCalOutParameter
     * @see
     * @since 1.0
     */
    private AccurIntCalOutParameter getCalculate(TargetFinProduct rstBaseinfo,
                                                 List<TargetCashPlan> listIntinfo,
                                                 List<TargetCashCalcRule> listCashinfo,
                                                 List<TargetCashPayRule> listPayinfo,
                                                 BigDecimal varFaceValue, Date varDate) {
        AccurIntCalInParameter parameter = new AccurIntCalInParameter();
        // 计算日
        parameter.setCalDate(BasicUtil.dateToLocal(varDate));
        // 资产信息
        SecInfoModel secInfoModel = new SecInfoModel();
        // 债券起息日
        secInfoModel.setStartDate(BasicUtil.dateToLocal(rstBaseinfo.getVdate()));
        // 债券到期日
        secInfoModel.setEndDate(BasicUtil.dateToLocal(rstBaseinfo.getActmdate()));
        // 债券首次付息日
        Date firstPayDate = listPayinfo != null && listPayinfo.size() > 0 ? listPayinfo.get(
            0).getFirstPayDate() : null;
        secInfoModel.setFirstPayDate(BasicUtil.dateToLocal(firstPayDate));
        // 发行价
        secInfoModel.setIssuePrice(rstBaseinfo.getIssuePrice());
        // 债券类型
        Variety bondsType = null;
        if (DictCodeList.息票品种_贴现.getValue().equals(rstBaseinfo.getCouponSpecies())) {
            bondsType = Variety.贴现;
        }
        else if (DictCodeList.息票品种_零息.getValue().equals(rstBaseinfo.getCouponSpecies())) {
            bondsType = Variety.零息;
        }
        else if (DictCodeList.息票品种_附息.getValue().equals(rstBaseinfo.getCouponSpecies())) {
            bondsType = Variety.附息;
        }
        else if (DictCodeList.息票品种_利随本清.getValue().equals(rstBaseinfo.getCouponSpecies())) {
            bondsType = Variety.利随本清;
        }
        secInfoModel.setBondsType(bondsType);
        // 计息基础
        Basis basis = null;
        String basisString = listCashinfo != null
                             && listCashinfo.size() > 0 ? listCashinfo.get(0).getBasis() : null;
        if (DictCodeList.计息基础_A_360.getValue().equals(basisString)) {
            basis = Basis.A_360;
        }
        else if (DictCodeList.计息基础_A_365.getValue().equals(basisString)) {
            basis = Basis.A_365;
        }
        else if (DictCodeList.计息基础_A_A_平均分配.getValue().equals(basisString)
                 || DictCodeList.计息基础_A_A_按实际天数分配.getValue().equals(basisString)) {
            basis = Basis.A_A;
        }
        else if (DictCodeList.计息基础_A_365F.getValue().equals(basisString)) {
            basis = Basis.A_365F;
        }
        else if (DictCodeList.计息基础_30_360.getValue().equals(basisString)) {
            basis = Basis._30_360;
        }
        secInfoModel.setBasis(basis);
        // 利率类型
        IntRateType intRateType = null;
        String intRateTypeString = listCashinfo != null
                                   && listCashinfo.size() > 0 ? listCashinfo.get(
                                       0).getCalcType() : null;
        if (DictCodeList.利率类型_固定利率.getValue().equals(intRateTypeString)) {
            intRateType = IntRateType.固定利率;
        }
        else if (DictCodeList.利率类型_浮动利率.getValue().equals(intRateTypeString)) {
            intRateType = IntRateType.浮动利率;
        }
        secInfoModel.setIntRateType(intRateType);
        // 利息分配方式
        AllocationType allocationType = null;
        if (DictCodeList.计息基础_A_A_平均分配.getValue().equals(basisString)) {
            allocationType = AllocationType.平均分配;
        }
        else if (DictCodeList.计息基础_A_A_按实际天数分配.getValue().equals(basisString)) {
            allocationType = AllocationType.按实际天数分配;
        }
        else {
            allocationType = AllocationType.按实际天数分配;
        }
        secInfoModel.setAllocationType(allocationType);
        // 付息计划推算方式
        SecIntSchCalMethod secIntSchCalMethod = null;
        String secIntSchCalMethodString = listPayinfo != null
                                          && listPayinfo.size() > 0 ? listPayinfo.get(
                                              0).getCalcRule() : null;
        if (DictCodeList.推算方式_起息日向后.getValue().equals(secIntSchCalMethodString)) {
            secIntSchCalMethod = SecIntSchCalMethod.起息日向后;
        }
        else if (DictCodeList.推算方式_到期日向前.getValue().equals(secIntSchCalMethodString)) {
            secIntSchCalMethod = SecIntSchCalMethod.到期日向前;
        }
        else if (DictCodeList.推算方式_首次支付日.getValue().equals(secIntSchCalMethodString)) {
            secIntSchCalMethod = SecIntSchCalMethod.首次付息日;
        } else {
        	secIntSchCalMethod = SecIntSchCalMethod.起息日向后;
        }
        secInfoModel.setCalMethod(secIntSchCalMethod);
        // 付息频率
        IntFrequency yearIntRate = null;
        String yearIntRateString = listPayinfo != null && listPayinfo.size() > 0 ? listPayinfo.get(
            0).getPayCycle() : null;
        if (DictCodeList.频率_月.getValue().equals(yearIntRateString)) {
            yearIntRate = IntFrequency.月付;
        }
        else if (DictCodeList.频率_季.getValue().equals(yearIntRateString)) {
            yearIntRate = IntFrequency.季付;
        }
        else if (DictCodeList.频率_年.getValue().equals(yearIntRateString)) {
            yearIntRate = IntFrequency.年付;
        }
        else if (DictCodeList.频率_半年.getValue().equals(yearIntRateString)) {
            yearIntRate = IntFrequency.半年付;
        }
        secInfoModel.setYearIntRate(yearIntRate);
        parameter.setSecInfoModel(secInfoModel);

        // 付息计划列表
        List<SecIntSchedule> secIntScheduleList = new ArrayList<SecIntSchedule>();
        if (listIntinfo != null && listIntinfo.size() > 0) {
            for (TargetCashPlan targetCashPlan : listIntinfo) {
                SecIntSchedule secIntSchedule = new SecIntSchedule();
                // 理论起始日
                secIntSchedule.setTheoryStartDate(
                    targetCashPlan.getVdateTerm() == null ? BasicUtil.dateToLocal(
                        targetCashPlan.getVdate()) : BasicUtil.dateToLocal(
                            targetCashPlan.getVdateTerm()));
                // 理论截止日
                secIntSchedule.setTheoryCutoffDate(
                    targetCashPlan.getMdateTerm() == null ? BasicUtil.dateToLocal(
                        targetCashPlan.getMdate()) : BasicUtil.dateToLocal(
                            targetCashPlan.getMdateTerm()));
                // 实际起始日
                secIntSchedule.setActualStartDate(
                    BasicUtil.dateToLocal(targetCashPlan.getVdate()));
                // 实际截止日
                secIntSchedule.setActualCutoffDate(
                    BasicUtil.dateToLocal(targetCashPlan.getMdate()));
                // 计息本金
                secIntSchedule.setCorpus(new BigDecimal(100));
                // 利息金额
                secIntSchedule.setAmount(targetCashPlan.getCashAmt());
                secIntScheduleList.add(secIntSchedule);
            }
            parameter.setSecIntScheduleList(secIntScheduleList);
        }

        // 债券利率重置信息
        List<PaperIrReset> paperIrResetList = new ArrayList<PaperIrReset>();
        if (listCashinfo != null && listCashinfo.size() > 0) {
            for (TargetCashCalcRule targetCashCalcRule : listCashinfo) {
                PaperIrReset paperIrReset = new PaperIrReset();
                // 票面利率生效日
                paperIrReset.setParRateEffDate(
                    BasicUtil.dateToLocal(targetCashCalcRule.getEffDate()));
                // 票面利率
                paperIrReset.setParRate(targetCashCalcRule.getYield());
                paperIrResetList.add(paperIrReset);
            }
            parameter.setPaperIrResetList(paperIrResetList);
        }

        // 当前面值
        parameter.setPaperAmt(varFaceValue);
        // 计算百元面值的债券的利息
        AccurIntForBookCalculator calculator2 = CalculatorFactory.createAccurIntForBookCalculator();
        AccurIntCalOutParameter output2 = calculator2.calculate(parameter);
        return output2;
    }

    /**
     * 计算债券的日摊销收益率
     * 
     * @param rstBaseinfo 标的信息
     * @param listIntinfo 付息列表
     * @param listCashinfo 报价列表
     * @param listPayinfo 支付规则
     * @param varFaceValue 百元面值
     * @param newCash 最新报价
     * @return SecDscYieldOutParameter
     * @see
     * @since 1.0
     */
    private SecDscYieldOutParameter getSecDscYieldCalculator(BigDecimal varFaceValue, Date varDate,
                                                             TargetFinProduct rstBaseinfo,
                                                             List<TargetCashPlan> listIntinfo,
                                                             List<TargetCashCalcRule> listCashinfo,
                                                             List<TargetCashPayRule> listPayinfo,
                                                             SecT1HandleResultDto result,
                                                             BigDecimal newCash,
                                                             FinExercisePlan finExercisePlan,
                                                             BigDecimal varPlanFaceValue) {
        SecDscYieldInParameter parameter = new SecDscYieldInParameter();
        // 计算日
        parameter.setCalDate(BasicUtil.dateToLocal(varDate));
        // 券面总额
        parameter.setTotalMoney(
            ArithmeticUtil.multiply(new BigDecimal(100), result.getNumBalance()));
        // 摊余成本净价总额
        // 变量.溢折价科目余额 + 变量.成本科目余额
        parameter.setTotalNetPrice(
            ArithmeticUtil.doAdd(result.getCbSubjectBalance(), result.getZyjSubjectBalance()));
        // 当日百元券面面值
        parameter.setAmount(varFaceValue);
        // 票面利率
        parameter.setPaperRate(newCash);
        // 零息贴现计算方案
        parameter.setPlan(
            com.joyintech.calculation.bonds.constant.GlobalConstant.DIS_DEBT_SCHEME_A);
        // 资产信息
        SecInfoModel secInfoModel = new SecInfoModel();
        // 债券起息日
        secInfoModel.setStartDate(BasicUtil.dateToLocal(rstBaseinfo.getVdate()));
        // 债券到期日
        secInfoModel.setEndDate(BasicUtil.dateToLocal(rstBaseinfo.getActmdate()));
        // 债券首次付息日
        Date firstPayDate = listPayinfo != null && listPayinfo.size() > 0 ? listPayinfo.get(
            0).getFirstPayDate() : null;
        secInfoModel.setFirstPayDate(BasicUtil.dateToLocal(firstPayDate));
        // 发行价
        secInfoModel.setIssuePrice(rstBaseinfo.getIssuePrice());
        // 债券类型
        Variety bondsType = null;
        if (DictCodeList.息票品种_贴现.getValue().equals(rstBaseinfo.getCouponSpecies())) {
            bondsType = Variety.贴现;
        }
        else if (DictCodeList.息票品种_零息.getValue().equals(rstBaseinfo.getCouponSpecies())) {
            bondsType = Variety.零息;
        }
        else if (DictCodeList.息票品种_附息.getValue().equals(rstBaseinfo.getCouponSpecies())) {
            bondsType = Variety.附息;
        }
        else if (DictCodeList.息票品种_利随本清.getValue().equals(rstBaseinfo.getCouponSpecies())) {
            bondsType = Variety.利随本清;
        }
        secInfoModel.setBondsType(bondsType);
        // 计息基础
        Basis basis = null;
        String basisString = listCashinfo != null
                             && listCashinfo.size() > 0 ? listCashinfo.get(0).getBasis() : null;
        if (DictCodeList.计息基础_A_360.getValue().equals(basisString)) {
            basis = Basis.A_360;
        }
        else if (DictCodeList.计息基础_A_365.getValue().equals(basisString)) {
            basis = Basis.A_365;
        }
        else if (DictCodeList.计息基础_A_A_平均分配.getValue().equals(basisString)
                 || DictCodeList.计息基础_A_A_按实际天数分配.getValue().equals(basisString)) {
            basis = Basis.A_A;
        }
        else if (DictCodeList.计息基础_A_365F.getValue().equals(basisString)) {
            basis = Basis.A_365F;
        }
        else if (DictCodeList.计息基础_30_360.getValue().equals(basisString)) {
            basis = Basis._30_360;
        }
        secInfoModel.setBasis(basis);
        // 利率类型
        IntRateType intRateType = null;
        String intRateTypeString = listCashinfo != null
                                   && listCashinfo.size() > 0 ? listCashinfo.get(
                                       0).getCalcType() : null;
        if (DictCodeList.利率类型_固定利率.getValue().equals(intRateTypeString)) {
            intRateType = IntRateType.固定利率;
        }
        else if (DictCodeList.利率类型_浮动利率.getValue().equals(intRateTypeString)) {
            intRateType = IntRateType.浮动利率;
        }
        secInfoModel.setIntRateType(intRateType);
        // 利息分配方式
        AllocationType allocationType = null;
        if (DictCodeList.计息基础_A_A_平均分配.getValue().equals(basisString)) {
            allocationType = AllocationType.平均分配;
        }
        else if (DictCodeList.计息基础_A_A_按实际天数分配.getValue().equals(basisString)) {
            allocationType = AllocationType.按实际天数分配;
        }
        else {
            allocationType = AllocationType.按实际天数分配;
        }
        secInfoModel.setAllocationType(allocationType);
        // 付息计划推算方式
        SecIntSchCalMethod secIntSchCalMethod = null;
        String secIntSchCalMethodString = listPayinfo != null
                                          && listPayinfo.size() > 0 ? listPayinfo.get(
                                              0).getCalcRule() : null;
        if (DictCodeList.推算方式_起息日向后.getValue().equals(secIntSchCalMethodString)) {
            secIntSchCalMethod = SecIntSchCalMethod.起息日向后;
        }
        else if (DictCodeList.推算方式_到期日向前.getValue().equals(secIntSchCalMethodString)) {
            secIntSchCalMethod = SecIntSchCalMethod.到期日向前;
        }
        else if (DictCodeList.推算方式_首次支付日.getValue().equals(secIntSchCalMethodString)) {
            secIntSchCalMethod = SecIntSchCalMethod.首次付息日;
        } else {
        	secIntSchCalMethod = SecIntSchCalMethod.起息日向后;
        }
        secInfoModel.setCalMethod(secIntSchCalMethod);
        // 付息频率
        IntFrequency yearIntRate = null;
        String yearIntRateString = listPayinfo != null && listPayinfo.size() > 0 ? listPayinfo.get(
            0).getPayCycle() : null;
        if (DictCodeList.频率_月.getValue().equals(yearIntRateString)) {
            yearIntRate = IntFrequency.月付;
        }
        else if (DictCodeList.频率_季.getValue().equals(yearIntRateString)) {
            yearIntRate = IntFrequency.季付;
        }
        else if (DictCodeList.频率_年.getValue().equals(yearIntRateString)) {
            yearIntRate = IntFrequency.年付;
        }
        else if (DictCodeList.频率_半年.getValue().equals(yearIntRateString)) {
            yearIntRate = IntFrequency.半年付;
        }
        else{
            yearIntRate = IntFrequency.年付;
        }
        secInfoModel.setYearIntRate(yearIntRate);
        parameter.setSecInfoModel(secInfoModel);

        // 付息计划
        List<SecIntSchedule> secIntScheduleList = new ArrayList<SecIntSchedule>();
        if (listIntinfo != null && listIntinfo.size() > 0) {
            for (TargetCashPlan targetCashPlan : listIntinfo) {
                SecIntSchedule secIntSchedule = new SecIntSchedule();
                // 理论起始日
                secIntSchedule.setTheoryStartDate(
                    targetCashPlan.getVdateTerm() == null ? BasicUtil.dateToLocal(
                        targetCashPlan.getVdate()) : BasicUtil.dateToLocal(
                            targetCashPlan.getVdateTerm()));
                // 理论截止日
                secIntSchedule.setTheoryCutoffDate(
                    targetCashPlan.getMdateTerm() == null ? BasicUtil.dateToLocal(
                        targetCashPlan.getMdate()) : BasicUtil.dateToLocal(
                            targetCashPlan.getMdateTerm()));
                // 实际起始日
                secIntSchedule.setActualStartDate(
                    BasicUtil.dateToLocal(targetCashPlan.getVdate()));
                // 实际截止日
                secIntSchedule.setActualCutoffDate(
                    BasicUtil.dateToLocal(targetCashPlan.getMdate()));
                // 计息本金
                secIntSchedule.setCorpus(new BigDecimal(100));
                // 利息金额
                secIntSchedule.setAmount(targetCashPlan.getCashAmt());
                secIntScheduleList.add(secIntSchedule);
            }
            parameter.setSecIntScheduleList(secIntScheduleList);
        }
        //String isExe = CacheUtil.getSysParam("ISSECTOEXEDATE");
        String exeDate = rstBaseinfo.getExeDate();
        if (StringUtils.isNotEmpty(exeDate) && !DictCodeList.含权债摊销日_到期日.getValue().equals(exeDate)) {
	        // 变量.行权计划不为空时传入
	        if (finExercisePlan != null) {
	            // 下一行权日 变量.行权计划.计划行权日
	            parameter.setNextExeDate(BasicUtil.dateToLocal(finExercisePlan.getExerciseDate()));
	            // 行权价 变量.行权计划.行权价
	            parameter.setNextExePrice(finExercisePlan.getExercisePrice());
	            // 行权日百元券面 变量.行权日百元券面
	            parameter.setNextExeAmount(varPlanFaceValue);
	            // 摊销价格模式 系统参数：DSC_TRAGEPRICE_MODE
	            parameter.setExePlan(
	                NextExeCalMethod.parse(CacheUtil.getSysParam("DSC_TRAGEPRICE_MODE")));
	        }
        }
        SecDscYieldCalculator calculator = CalculatorFactory.createSecDscYieldCalculator();
        SecDscYieldOutParameter output = calculator.calculate(parameter);

        return output;
    }
}
