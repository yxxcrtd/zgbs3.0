/*
 * 文件名：ExpectIncomeSubProductFixedFeeCalBookService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年9月29日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.batch.expectincomesubproductfixedfeecalbook.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.tams.constant.*;
import com.joyintech.tams.constant.DictCodeListExt2;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.valuation.batch.common.accounting.entity.PrdCashEntity;
import com.joyintech.tams.valuation.batch.common.accounting.entity.StayDrawSubprdEntity;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.base.entity.BokStatBokPositionDto;
import com.joyintech.tams.valuation.batch.common.calculation.entity.CalCommonResult;
import com.joyintech.tams.valuation.batch.common.calculation.entity.PrincipalInfo;
import com.joyintech.tams.valuation.batch.common.calculation.entity.QuoteInfo;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetCashCalcRule;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import com.joyintech.tams.common.base.entity.BokBooksetExt;
import com.joyintech.tams.common.base.entity.BokFeeTableSet;
import com.joyintech.tams.common.base.entity.BokStatBokPosition;

/**
 * 预期收益型子产品 TA 固定费用计提入账 Processor 的 Service
 * 
 * @author zhangwei
 * @version 1.0 2018年9月29日
 * @see ExpectIncomeSubProductFixedFeeCalBookService
 * @since 1.0
 */
@Service
public class ExpReturnSubProductFixedFeeCalBookService {

	/**
	 * 标的类共通
	 */
	@Autowired
	private FinProductCommonService finProductCommonService;

	/**
	 * 批处理账务相关公用方法
	 */
	@Autowired
	private AccountingCommonService accountingCommonService;

	/**
	 * 计算类共通
	 */
	@Autowired
	private CalculationCommonService calculationCommonService;

	/**
	 * 记账公共类
	 */
	@Autowired
	private BookCommonLogic bookCommonLogic;

	/**
	 * 记账引擎
	 */
	@Autowired
	private BookAccountGenLogic bookLogic;

	/**
	 * process 数据处理
	 * 
	 * @param params 参数
	 * @return BookGenerateResult
	 */
	@SuppressWarnings("unchecked")
    public BookGenerateResult execute(Map<String, Object> params) {
        BookGenerateResult rtnResult = new BookGenerateResult(); // 此 process 返回值
        
	    StayDrawSubprdEntity acctEntity = (StayDrawSubprdEntity) params.get(GlobalConstant.READER_RESULT); // 获取 reader 返回值

		if (acctEntity == null) {// 若reader传递的参数为 null，不进行后续处理
			return null;
		}
		//赋值金融产品代码finprodId
		params.put("finprodId", acctEntity.getBusiId());
	    //赋值账套代码booksetId
        params.put("booksetId", acctEntity.getBooksetId());
        if(StringUtils.isEmpty(acctEntity.getBusiDetailType())){
                return null;
        }
        //赋值业务明细类型
        params.put("busiDetailType", acctEntity.getBusiDetailType());
        //分支序号
        params.put("branch", acctEntity.getsBranch());
        //查询子产品待计提现金流查询_理财产品
        List<StayDrawSubprdEntity> acctEntityList=Lists.newArrayList();
        if(DictCodeListExt1.金融产品类型_理财产品.getValue().equals(acctEntity.getBusiDetailType())){
            acctEntityList=accountingCommonService.getStayDrawSubprdInfoByPrd(params);
        }else if(DictCodeListExt1.金融产品类型_理财子产品.getValue().equals(acctEntity.getBusiDetailType())){
            acctEntityList=accountingCommonService.getStayDrawSubprdInfoByPrd(params);
        }
		//赋值级别序号
	    params.put("lev", acctEntity.getLev());
		//调用共通设计->【详细设计-账务类共通方法设计.xlsx】->查询分层结构子结构
		List<StayDrawSubprdEntity> sublist=accountingCommonService.querySubBokAcctInfo(params);
		//获取账务明细代码
		List<String> bokDetailIdList=Lists.newArrayList();
		for(StayDrawSubprdEntity entity:sublist){
		    bokDetailIdList.add(entity.getBokDetailId());
		}
		//循环处理结果
		for(StayDrawSubprdEntity acctEntity_new:acctEntityList){
		    
	        if (rtnResult.getValBokBookdetailBatch()== null) {// 账务明细若为null赋初始值
	            rtnResult.setValBokBookdetailBatch(Lists.newArrayList());
	        }

	        // 获取从 task 拿到的参数
	        SysBatchExecInfo batchExecInfo = (SysBatchExecInfo) params.get("BATCHINFO"); // 待处理任务信息
	        String taskId = batchExecInfo.getTaskId();
	        Date happenDate = (Date) params.get(BookConstant.HAPPEN_DATE); // 发生日：批处理任务执行信息.处理日（发生日）
	        Date mdateNow = happenDate; // 本次计提结束日


	        // 标的信息查询
	        Map<String, Object> targetInfoMap = this.getTargetInfo(acctEntity_new, happenDate);

	        // 标的——现金流计算规则
	        List<TargetCashCalcRule> targetCashCalcRuleList = (List<TargetCashCalcRule>) targetInfoMap
	                .get(ValDictCodeList.标的_现金流计算规则.getValue());

/*	        // 标的_业绩报酬计提规则
	        List<TargetPerfRewardCalRule> targetPerfRewardCalRuleList = (List<TargetPerfRewardCalRule>) targetInfoMap.get(ValDictCodeList.标的_业绩报酬计提规则.getValue());
	        
	        TargetPerfRewardCalRule targetPerfRewardCalRule = targetPerfRewardCalRuleList.get(targetPerfRewardCalRuleList.size() - 1);
	        // 标的_业绩报酬计提计划
	        List<TargetPerfRewardAccrsche> targetPerfRewardAccrscheList = (List<TargetPerfRewardAccrsche>) targetInfoMap
	                .get(ValDictCodeList.标的_业绩报酬计提计划.getValue());*/
	        // 查询标的信息
	        TargetFinProduct targetFinProduct = (TargetFinProduct) targetInfoMap.get(ValDictCodeList.标的_信息.getValue());

	        // 账套核算参数查询
	        List<String> paramCodeList = Lists.newArrayList();
	        paramCodeList.add(ValDictCodeList.账套配置表参数项_产品费用计提方式_固定管理费.getValue());
	        paramCodeList.add(ValDictCodeList.账套配置表参数项_产品费用计提方式_托管费.getValue());
	        paramCodeList.add(ValDictCodeList.账套配置表参数项_产品费用计提方式_销售费.getValue());
	        paramCodeList.add(ValDictCodeList.账套配置表参数项_产品费用计提方式_外包服务费.getValue());
	        paramCodeList.add(ValDictCodeList.账套配置表参数项_产品费用计提方式_代销费.getValue());
	        List<BokBooksetExt> bokBooksetExtList = accountingCommonService.getValBokBooksetExt(acctEntity.getBooksetId(),
	                paramCodeList);

	        /*2018-10-26由于不确定固定管理费计提评率数据存放位置,暂时不进行校验*/
/*	        
	        // IF(业绩报酬计提规则.计提类型 != [数据字典].[计提类型].[高频]）
	        if (!DictCodeList.计提类型_高频.getValue().equals(targetPerfRewardCalRule.getAstVdateType())) {
	            // IF (MAP.GET(业绩报酬计提计划).SIZE == 0)
	            if (CollectionUtils.isEmpty(targetPerfRewardAccrscheList)) { 
	                // //判断【发生日】=业绩报酬计提计划.计提日，若非计划计提日则不计提
	                return null;
	            }
	        }
*/
	        // 获取日期信息
            Date calDate=new Date(); // 本次计提日
	        Date vdateNow=new Date(); // 本次计提开始日
	        // 变量.计提方式 = 根据DTO.现金流二级类型 对应的 产品费用计提方式_XX费
	        String calType = StringUtils.EMPTY;
	        // 现金流二级类型
	        String cashType2 = acctEntity_new.getCashType2(); 
	        if (CollectionUtils.isNotEmpty(bokBooksetExtList)) {
	            // 根据现金流二级类型 筛选 对应的账套配置参数List
	            if (DictCodeListExt1.现金流二级类型_固定管理费.getValue().equals(cashType2)) {
	                bokBooksetExtList = bokBooksetExtList.stream()
	                        .filter(item -> ValDictCodeList.账套配置表参数项_产品费用计提方式_固定管理费.getValue().equals(item.getParamCode()))
	                        .collect(Collectors.toList());
	            } else if (DictCodeListExt1.现金流二级类型_托管费.getValue().equals(cashType2)) {
	                bokBooksetExtList = bokBooksetExtList.stream()
	                        .filter(item -> ValDictCodeList.账套配置表参数项_产品费用计提方式_托管费.getValue().equals(item.getParamCode()))
	                        .collect(Collectors.toList());
	            }else if (DictCodeListExt1.现金流二级类型_销售费.getValue().equals(cashType2)) {
	                bokBooksetExtList = bokBooksetExtList.stream()
	                        .filter(item -> ValDictCodeList.账套配置表参数项_产品费用计提方式_销售费.getValue().equals(item.getParamCode()))
	                        .collect(Collectors.toList());
	            } else if (DictCodeListExt1.现金流二级类型_外包服务费.getValue().equals(cashType2)) {
	                bokBooksetExtList = bokBooksetExtList.stream()
	                        .filter(item -> ValDictCodeList.账套配置表参数项_产品费用计提方式_外包服务费.getValue().equals(item.getParamCode()))
	                        .collect(Collectors.toList());
	            } else if (DictCodeListExt1.现金流二级类型_代销费.getValue().equals(cashType2)) {
	                bokBooksetExtList = bokBooksetExtList.stream()
	                        .filter(item -> ValDictCodeList.账套配置表参数项_产品费用计提方式_代销费.getValue().equals(item.getParamCode()))
	                        .collect(Collectors.toList());
	            }
	            if (CollectionUtils.isNotEmpty(bokBooksetExtList)) {// 筛选后若 list 不空， 获取对应的计提方式
	                calType = bokBooksetExtList.get(0).getParamValue();
	            }
	        }

	        // if（变量.计提方式 = 【数据字典】.计提方式."区间倒减"）
	        if (DictCodeList.计提方式_区间倒减.getValue().equals(calType)) {
	            // 调用共通方法-科目余额查询（具体类型根据DTO.现金流类型判断）
	            SubjectBalance subjectBalance = this.getSubjectLatestBalanceCommon(acctEntity, happenDate);
//	          if （账务明细DTO == Null or 账务明细DTO.结息日 为空）
	            if (subjectBalance == null || subjectBalance.getMdate() == null) {
	                vdateNow = acctEntity_new.getVdate(); // 本次计提开始日 = DTO.起息日
	            } else {
	                vdateNow = subjectBalance.getMdate(); // 本次计提开始日 = 账务明细DTO.结息日
	            }
	        } else {
	            vdateNow = happenDate; // 本次计提开始日 = 批处理参数.发生日
	        }

	        // 费用计提
	        // 费用计提 - 1）利率变更列表处理
	        BigDecimal yield = BigDecimal.ZERO; // 生效利率
	        QuoteInfo quoteInfo = new QuoteInfo(); // 利率变更
	        List<QuoteInfo> quoteInfoList = Lists.newArrayList(); // 利率变更列表
	        for (TargetCashCalcRule rule : targetCashCalcRuleList) {
	            if (yield.compareTo(rule.getYield()) != 0) {
	                quoteInfo = new QuoteInfo(); // 定义利率变更。
	                quoteInfo.setIssueDate(rule.getEffDate());
	                quoteInfo.setInterest(rule.getYield());
	                quoteInfo.setAccountBasis(rule.getBasis());
	                quoteInfoList.add(quoteInfo);
	            }
	        }
	        // 费用计提 - 2）计费基数变更列表处理
	        List<PrincipalInfo> principalInfoList = Lists.newArrayList();// 计费基数变更列表
	        BigDecimal feeBaseLast = BigDecimal.ZERO;// 上次计费基数
	        PrincipalInfo principalInfo = new PrincipalInfo();// 计费基数变更
	        //String baseDateType = DictCodeList.基数日期类型_当日.getValue();// 计费基数日期类型
	        String feebaseType = DictCodeList.基数类型_实收资本.getValue();// 计费基础类型 - 预期收益型产品固定为当日实收资本
	        BokStatBokPositionDto position=new BokStatBokPositionDto();
	        //查询日开始
	        position.setSdateStart(vdateNow);
	        //查询结束日
	        position.setSdateEnd(mdateNow);
	        //入账日
	        position.setBookDate( acctEntity.getBookDate());
	        //账套
	        position.setBooksetId(acctEntity.getBooksetId());
	        //分层代码
	        position.setLayeringId(acctEntity.getLayeringId());
	        //分层代码
	        position.setIsSubPrd(DictCodeList.是否_是.getValue());
	        // 帐套资产负债信息集合 = 调用共通方法查询：帐套资产负债查询（本次计提开始日，本次计提结束日，DTO.帐套代码，DTO.金融产品代码） 
/*            List<BokStatBokPosition> valStatBokPositionList = accountingCommonService.getValStatBokPosition(vdateNow,
                mdateNow, null, acctEntity.getBooksetId(), null, acctEntity_new.getFinprodId());
*/
	        List<BokStatBokPosition> valStatBokPositionList = accountingCommonService.getValStatBokPosition(position);

	        Map<Date, BokStatBokPosition> valStatBokPositionMap = Maps.newHashMap();
	        for (BokStatBokPosition item : valStatBokPositionList) {// 以发生日作为key的帐套资产负债信息
	            valStatBokPositionMap.put(item.getHappenDate(), item);
	        }

	        // FOR(计提日 <- 本次计提开始日 ： 本次计提结束日)
	        calDate = vdateNow; // 计提日：出事赋值本次计提开始日
	        do {
	            // 调用【详细设计-计算类共通方法设计】产品费用计提基数计算（根据发生日=计提日匹配）
	            BigDecimal feeBase = calculationCommonService.calProductFee(feebaseType, valStatBokPositionMap.get(calDate),
	                    acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntity.getStandardCcy(), happenDate,
	                    happenDate, acctEntity.getTaskPri(),bokDetailIdList);

	            if (feeBaseLast != null && feeBaseLast.compareTo(feeBase) != 0) {// IF(上次计费基数 != 计费基数）
	                principalInfo = new PrincipalInfo(); // 计费基数变更
	                principalInfo.setChangeDate(calDate); // 计费基数变更.变更日 = 计提日
	                principalInfo.setPrinAmount(feeBase); // 计费基数变更.变更金额 = 计费基数
	                feeBaseLast = feeBase; // 上次计费基数 = 计费基数
	                principalInfoList.add(principalInfo); // 添加到技术变更列表中
	            }

	            calDate = DateUtil.addDay(calDate, 1); // 变量.计提日+1

	        } while (DateUtil.compareDate(mdateNow, calDate) >= 0);

	        // 调用共通方法：通用计算
	        CalCommonResult calCommonResult = calculationCommonService.calCommon(vdateNow, DateUtil.addDay(mdateNow, 1), principalInfoList,
	                quoteInfoList);
	        //调用共通设计.费用场景配置查询
	        BokFeeTableSet feeSetEntiry=null;
	        Map<String,Object> map=new HashMap<String,Object>(); 
	        map.put("isCalculation", DictCodeList.是否_是.getValue());
	        //查询费用场景配置查询
	        List<BokFeeTableSet> feeList=accountingCommonService.getFeeTableSetQuery(map);
	        for(BokFeeTableSet feeSet:feeList){
	            if(cashType2.equals(feeSet.getFinprodType())){
	                feeSetEntiry=feeSet;
	                break;
	            }
	        }
	        // 费用计提 = 返回值.总利息金额
	        BigDecimal feeCal = calCommonResult.getTotalInterestAmt(); 
	        // 调用金额共通方法设置有效小数位
	        feeCal = ArithmeticUtil.round(feeCal, NumberEnum.MONEY); 

	        if (DictCodeList.计提方式_区间倒减.getValue().equals(calType)) {
	            SubjectBalance subjectBalance = this.getSubjectBalanceExpectDirectionCommon(acctEntity, happenDate,feeSetEntiry,
	                    DictCodeList.余额方向_贷方.getValue(),acctEntity_new);
	            // 费用计提 = 费用计提 - DTO.科目余额
	            feeCal = ArithmeticUtil.subtract(feeCal, subjectBalance.getBalance()); 
	        }

	        // 费用计提 - 3）生成记账凭证
	        AccountDriver ad = new AccountDriver(); // 记账场景要素管理
	        Map<String, Object> accountSceneMap = new HashMap<>(); // 存放该笔记账的场景要素 map定义
	        Map<String, BigDecimal> accountAmountMap = new HashMap<>(); // 存放该笔记账的相关金额要素 map定义
	        Map<String, Object> accountCurrencyMap = new HashMap<>(); // 存放该笔记账的相关金额对应的币种 map定义

	        // 共通元素设值 - 设置账务引擎参数
	        ad.setTreeNode(acctEntity.getBookSetTreeNode());// 塞入记账场景树节点 = 会计场景
	        // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值) 
	        accountSceneMap = bookCommonLogic.accrualElementAssignForTaFee(taskId, happenDate, acctEntity);
	        accountSceneMap.put(BookConstant.TYPE_1, targetFinProduct.getType1());// 分类1 - 变量.交易信息.分类1
	        accountSceneMap.put(BookConstant.TYPE_2, targetFinProduct.getType2());// 分类2 - 变量.交易信息.分类2
	        if(feeSetEntiry!=null){
	            ad.setTableId(feeSetEntiry.getTableId());//设置账务引擎参数：ad.会计场景代码;
	            //备注 <- "TA销售服务费计提登账"
	            accountSceneMap.put(BookConstant.BOOK_REMARK, SysDataUtil.getNameFromDataDict(DictCode.会计场景代码, feeSetEntiry.getTableId()));
	        }else{
	            if (DictCodeListExt1.现金流二级类型_销售费.getValue().equals(cashType2)) {
	                ad.setTableId(DictCodeListTableId.会计场景代码_产品_销售服务费计提.getValue());// // 设置账务引擎参数：ad.会计场景代码;
	                accountSceneMap.put(BookConstant.BOOK_REMARK, "TA销售服务费计提登账");// 备注 <- "TA销售服务费计提登账"
	            } else if (DictCodeListExt1.现金流二级类型_托管费.getValue().equals(cashType2)) {
	                ad.setTableId(DictCodeListTableId.会计场景代码_产品_托管费计提.getValue());// 设置账务引擎参数：ad.会计场景代码;
	                accountSceneMap.put(BookConstant.BOOK_REMARK, "TA托管费计提登账");// 备注 <- "TA托管费计提登账"
	            }else if (DictCodeListExt1.现金流二级类型_固定管理费.getValue().equals(cashType2)) {
	                ad.setTableId(DictCodeListTableId.会计场景代码_产品_固定资产管理费计提.getValue());// 设置账务引擎参数：ad.会计场景代码;
	                accountSceneMap.put(BookConstant.BOOK_REMARK, "TA固定管理费计提登账");// 备注 <- "TA固定管理费计提登账"
	            } else if (DictCodeListExt1.现金流二级类型_代销费.getValue().equals(cashType2)) {
	                ad.setTableId(DictCodeListTableId.会计场景代码_产品_代销费计提.getValue());// 设置账务引擎参数：ad.会计场景代码;
	                accountSceneMap.put(BookConstant.BOOK_REMARK, "TA代销费计提登账");// 备注 <- "TA代销费计提登账"
	            } else if (DictCodeListExt1.现金流二级类型_外包服务费.getValue().equals(cashType2)) {
	                ad.setTableId(DictCodeListTableId.会计场景代码_产品_外包服务费计提.getValue());// 设置账务引擎参数：ad.会计场景代码;
	                accountSceneMap.put(BookConstant.BOOK_REMARK, "TA外包服务费计提登账");// 备注 <- "TA外包服务费计提登账"
	            }
	        }
	        accountAmountMap.put(BookConstant.ACCRUED_CHARDES, feeCal); // 费用计提 <- 最终计提金额

	        // 记账金额元素币种信息塞值
	        // 因为与记账金额元素map的key及数量一致，故采用循环
	        for (String keyNow : accountAmountMap.keySet()) {
	            accountCurrencyMap.put(keyNow, acctEntity.getStandardCcy()); // 费用计提 <- 原币
	        }

	        // map塞入场景要素管理中
	        ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
	        ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
	        ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

	        // 记账凭证生成 : 调用记账引擎公共记账方法，生成记账凭证
	        BookGenerateResult bookGenerateResult = bookLogic.genAccount(ad, batchExecInfo);
	        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
	        rtnResult.getValBokBookdetailBatch().addAll(bookGenerateResult.getValBokBookdetailBatch());
		}
		return rtnResult;
	}

	/**
	 * 调用共通方法-科目余额查询 - 期待方向 注：具体类型根据DTO.现金流类型判断
	 * 
	 * @param acctEntity reader 返回值
	 * @param happenDate 发生日：批处理任务执行信息.处理日（发生日）
	 * @return
	 */
	private SubjectBalance getSubjectBalanceExpectDirectionCommon(PrdCashEntity acctEntity, Date happenDate, BokFeeTableSet feeSet,
			String expBalanceDirect,StayDrawSubprdEntity acctEntity_new) {

		String baseSubjectNature = null; // 基础科目性质
		if(feeSet!=null){
		    baseSubjectNature=feeSet.getBaseSubjectNature();
		}else{
		    String cashType2 = acctEntity_new.getCashType2();
	        // 根据现金流二级类型，获取对应的基础科目性质
	        if (DictCodeListExt1.现金流二级类型_销售费.getValue().equals(cashType2)) {
	            baseSubjectNature = DictCodeBaseSubjectNatrue.应付销售服务费.getValue();// 设置基础科目性质
	        } else if (DictCodeListExt1.现金流二级类型_托管费.getValue().equals(cashType2)) {
                baseSubjectNature = DictCodeBaseSubjectNatrue.托管费.getValue();// 设置基础科目性质
            } else if (DictCodeListExt1.现金流二级类型_固定管理费.getValue().equals(cashType2)) {
	            baseSubjectNature = DictCodeBaseSubjectNatrue.应付管理人报酬.getValue();// 设置基础科目性质
	        } else if (DictCodeListExt1.现金流二级类型_代销费.getValue().equals(cashType2)) {
	            baseSubjectNature = DictCodeBaseSubjectNatrue.应付代销费.getValue();// 设置基础科目性质
	        } else if (DictCodeListExt1.现金流二级类型_外包服务费.getValue().equals(cashType2)) {
	            baseSubjectNature = DictCodeBaseSubjectNatrue.应付外包服务费.getValue();// 设置基础科目性质
	        }
		}
		//塞入 基础科目性质
		List<String> baseSubjectNatureList = null;
		if (StringUtils.isNotEmpty(baseSubjectNature)) {
			baseSubjectNatureList = Lists.newArrayList();
			baseSubjectNatureList.add(baseSubjectNature);
		}

		SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
		searchDto.setHappenDate(happenDate);// 发生日
		searchDto.setBookDate(acctEntity.getBookDate());// 入账日
		searchDto.setBokDetailId(acctEntity_new.getBokDetailId());// 账务明细代码
		searchDto.setBooksetId(acctEntity.getBooksetId());// 账套代码
		searchDto.setBaseSubjectNatureList(baseSubjectNatureList);// 基础科目性质
		searchDto.setTaskPri(acctEntity.getTaskPri());// 任务优先级

		//调用共通方法 - 科目余额查询
		List<SubjectBalance> subjectLatestBalanceList = accountingCommonService
				.getSubjectBalanceExpectDirection(searchDto, expBalanceDirect);

		SubjectBalance subjectBalance = new SubjectBalance(); // 返回的实体类
		if (CollectionUtils.isNotEmpty(subjectLatestBalanceList)) {
			subjectBalance = subjectLatestBalanceList.get(0);
		}

		return subjectBalance;
	}

	/**
	 * 调用共通方法-科目余额查询 注：具体类型根据DTO.现金流类型判断
	 * 
	 * @param acctEntity reader 返回值
	 * @param happenDate 发生日：批处理任务执行信息.处理日（发生日）
	 * @return
	 */
	private SubjectBalance getSubjectLatestBalanceCommon(PrdCashEntity acctEntity, Date happenDate) {

		String baseSubjectNature = null; // 基础科目性质
		String tableId = null; // 场景代码

		String cashType2 = acctEntity.getCashType2();
		// 根据现金流二级类型，获取对应的基础科目性质
		if (DictCodeListExt1.现金流二级类型_销售费.getValue().equals(cashType2)) {

			tableId = DictCodeListTableId.会计场景代码_产品_销售服务费计提.getValue();// 场景代码
			baseSubjectNature = DictCodeBaseSubjectNatrue.应付销售服务费.getValue();// 设置基础科目性质

		} else if (DictCodeListExt1.现金流二级类型_固定管理费.getValue().equals(cashType2)) {

			tableId = DictCodeListTableId.会计场景代码_产品_固定资产管理费计提.getValue();// 场景代码
			baseSubjectNature = DictCodeBaseSubjectNatrue.应付管理人报酬.getValue();// 设置基础科目性质

		} else if (DictCodeListExt1.现金流二级类型_代销费.getValue().equals(cashType2)) {

			tableId = DictCodeListTableId.会计场景代码_产品_代销费计提.getValue();// 场景代码
			baseSubjectNature = DictCodeBaseSubjectNatrue.应付代销费.getValue();// 设置基础科目性质

		} else if (DictCodeListExt1.现金流二级类型_外包服务费.getValue().equals(cashType2)) {

			tableId = DictCodeListTableId.会计场景代码_产品_外包服务费计提.getValue();// 场景代码
			baseSubjectNature = DictCodeBaseSubjectNatrue.应付外包服务费.getValue();// 设置基础科目性质
		}

//		塞入 基础科目性质
		List<String> baseSubjectNatureList = null;
		if (StringUtils.isNotEmpty(baseSubjectNature)) {
			baseSubjectNatureList = Lists.newArrayList();
			baseSubjectNatureList.add(baseSubjectNature);
		}

		SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
		searchDto.setHappenDate(happenDate);// 发生日
		searchDto.setBookDate(acctEntity.getBookDate());// 入账日
		searchDto.setBokDetailId(acctEntity.getBokDetailId());// 账务明细代码
		searchDto.setBooksetId(acctEntity.getBooksetId());// 账套代码
		searchDto.setBaseSubjectNatureList(baseSubjectNatureList);// 基础科目性质
		searchDto.setTableId(tableId);// 场景代码
		searchDto.setTaskPri(acctEntity.getTaskPri());// 任务优先级

//		调用共通方法 - 科目余额查询
		List<SubjectBalance> subjectLatestBalanceList = accountingCommonService.getSubjectLatestBalance(searchDto);

		SubjectBalance subjectBalance = null; // 返回的实体类
		if (CollectionUtils.isNotEmpty(subjectLatestBalanceList)) {
			subjectBalance = subjectLatestBalanceList.get(0);
		}

		return subjectBalance;
	}

	/**
	 * 获取标的相关信息
	 * 
	 * @param acctEntity reader 返回值
	 * @param happenDate 发生日：批处理任务执行信息.处理日（发生日）
	 * @return Map<String, Object>
	 */
	private Map<String, Object> getTargetInfo(PrdCashEntity acctEntity, Date happenDate) {
		Map<String, Object> returnMap = Maps.newHashMap(); // 返回值

		List<TargetSearchDto> queryCriteria = Lists.newArrayList(); // 共同查询类型

		// 传入 标的_现金流计算规则 Dto
		TargetSearchDto targetSearchDto1 = new TargetSearchDto();
		targetSearchDto1.setQueryType(ValDictCodeList.标的_现金流计算规则.getValue());
		targetSearchDto1.setCashType1(acctEntity.getCashType1());// 现金流一级类型
		targetSearchDto1.setCashType2(acctEntity.getCashType2());// 现金流二级类型
		targetSearchDto1.setCashId(acctEntity.getCashId());// 现金流代码
		targetSearchDto1.setEffdateStart(acctEntity.getVdate());// 起息日
		targetSearchDto1.setEffdateEnd(happenDate);// 发生日：批处理任务执行信息.处理日（发生日）
		queryCriteria.add(targetSearchDto1);

		// 传入 标的_现金流计划 Dto
		TargetSearchDto targetSearchDto2 = new TargetSearchDto();
		targetSearchDto2.setQueryType(ValDictCodeList.标的_现金流计划.getValue());
		targetSearchDto2.setCashType1(acctEntity.getCashType1());// 现金流一级类型
		targetSearchDto2.setCashType2(acctEntity.getCashType2());// 现金流二级类型
		targetSearchDto2.setCashId(acctEntity.getCashId());// 现金流代码
		targetSearchDto2.setEffdateStart(acctEntity.getVdate());// 起息日
		targetSearchDto2.setEffdateEnd(happenDate);// 发生日：批处理任务执行信息.处理日（发生日）
		queryCriteria.add(targetSearchDto2);

		// 传入 标的_业绩报酬计提规则
		TargetSearchDto targetSearchDto3 = new TargetSearchDto();
		targetSearchDto3.setQueryType(ValDictCodeList.标的_业绩报酬计提规则.getValue());
		targetSearchDto3.setCashType1(acctEntity.getCashType1());// 现金流一级类型
		targetSearchDto3.setCashType2(acctEntity.getCashType2());// 现金流二级类型
		targetSearchDto3.setCashId(acctEntity.getCashId());// 现金流代码
		targetSearchDto3.setEffdateStart(acctEntity.getVdate());// 起息日
		targetSearchDto3.setEffdateEnd(happenDate);// 发生日：批处理任务执行信息.处理日（发生日）
		queryCriteria.add(targetSearchDto3);

		// 传入 标的_业绩报酬计提计划
		TargetSearchDto targetSearchDto4 = new TargetSearchDto();
		targetSearchDto4.setQueryType(ValDictCodeList.标的_业绩报酬计提计划.getValue());
		targetSearchDto4.setCashType1(acctEntity.getCashType1());// 现金流一级类型
		targetSearchDto4.setCashType2(acctEntity.getCashType2());// 现金流二级类型
		targetSearchDto4.setCashId(acctEntity.getCashId());// 现金流代码
		targetSearchDto4.setEffdateStart(acctEntity.getVdate());// 起息日
		targetSearchDto4.setEffdateEnd(happenDate);// 发生日：批处理任务执行信息.处理日（发生日）
		queryCriteria.add(targetSearchDto4);

		// 传入[标的_信息]Dto
		TargetSearchDto targetSearchDto5 = new TargetSearchDto();
		targetSearchDto5.setQueryType(ValDictCodeList.标的_信息.getValue());
		queryCriteria.add(targetSearchDto5);

		// 获取标的信息Map，共通 标的信息查询-现金流计算规则
		Map<TargetSearchDto, Object> map = finProductCommonService.getTargetInfo(acctEntity.getFinprodId(),
				queryCriteria);

		if (MapUtils.isNotEmpty(map)) {
			returnMap.put(ValDictCodeList.标的_现金流计算规则.getValue(), map.get(targetSearchDto1)); // 标的现金流计算规则list
			returnMap.put(ValDictCodeList.标的_现金流计划.getValue(), map.get(targetSearchDto2)); // 标的现金流计划list
			returnMap.put(ValDictCodeList.标的_业绩报酬计提规则.getValue(), map.get(targetSearchDto3)); // 标的业绩报酬计提规则list
			returnMap.put(ValDictCodeList.标的_业绩报酬计提计划.getValue(), map.get(targetSearchDto4)); // 标的业绩报酬计提计划list
			returnMap.put(ValDictCodeList.标的_信息.getValue(), map.get(targetSearchDto5)); // 获取标的信息
		}

		return returnMap;
	}

}
