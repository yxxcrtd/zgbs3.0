/*
 * 文件名：FixedFeeCalBookProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年6月4日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.fixedfeecalbook.service;

import com.google.common.collect.Lists;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.valuation.batch.common.accounting.entity.StayDrawSubprdEntity;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.*;
import com.joyintech.tams.valuation.batch.common.base.entity.BokStatBokPositionDto;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.calculation.entity.CalCommonResult;
import com.joyintech.tams.valuation.batch.common.calculation.entity.PrincipalInfo;
import com.joyintech.tams.valuation.batch.common.calculation.entity.QuoteInfo;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetCashCalcRule;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetPerfRewardRule;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.constants.*;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * TA固定费用计提入账批处理
 * 
 * @author HuangJian
 * @version 1.0 2018年6月4日
 * @see FixedFeeCalBookProcessorNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class FixedFeeCalBookProcessorNewService {

    /**
     * 批处理账务相关公用方法
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 标的类共通
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 记账凭证生成类
     */
    @Autowired
    private BookAccountGenLogicNew bookAccountGenLogicNew;

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
     * process 数据处理
     * 
     * @param params 参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public BookGenerateResultWithBokStatFeePosition execute(Map<String, Object> params) {
        // 获取reader返回值
        StayDrawSubprdEntity acctEntity = (StayDrawSubprdEntity)params.get(GlobalConstant.READER_RESULT);

        // 若reader传递的参数为空，不进行后续处理
        if (acctEntity == null) {
            return null;
        }


        // 定义此process的返回变量
        BookGenerateResultWithBokStatFeePosition rtnResult = new BookGenerateResultWithBokStatFeePosition();

        // 账务明细若为空赋初始值
        if (rtnResult.getValBokBookdetailBatch() == null) {
            rtnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }
        //账套
        params.put("booksetId", acctEntity.getBooksetId());
        //赋值级别序号
        params.put("lev", acctEntity.getLev());
        //调用共通设计->【详细设计-账务类共通方法设计.xlsx】->查询分层结构子结构
        List<StayDrawSubprdEntity> sublist=accountingCommonService.querySubBokAcctInfo(params);
        //获取账务明细代码
        List<String> bokDetailIdList=Lists.newArrayList();
        for(StayDrawSubprdEntity entity:sublist){
            bokDetailIdList.add(entity.getBokDetailId());
        }
        // 获取从task拿到的参数
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)params.get("BATCHINFO");// 获取待处理任务信息
        Date happenDate = (Date)params.get(BookConstant.HAPPEN_DATE); // 发生日：批处理任务执行信息.处理日（发生日）
        String taskId = batchExecuteInfo.getTaskId();

        // 获取从reader中拿到的值
        String booksetId = acctEntity.getBooksetId();// 账套代码
        String finprodId = acctEntity.getFinprodId();// 金融产品代码
        String bokDetailId = acctEntity.getBokDetailId(); // 账务明细代码
        String cashId = acctEntity.getCashId();// 现金流代码
        String cashType1 = acctEntity.getCashType1();// 现金流一级类型
        String cashType2 = acctEntity.getCashType2();// 现金流二级类型
        Date vdate = acctEntity.getVdate();// 起息日
        Date mdate = acctEntity.getActmdate(); // 到日期
        Date bookDate = acctEntity.getBookDate();// 入账日
        String standardCcy = acctEntity.getStandardCcy(); // 本位币
        TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();// 会计场景
        Long taskPri = acctEntity.getTaskPri();// 任务优先级
        Date mdateNow = happenDate;// 本次计提结束日
        /**add by wujie on 20181127 start remark:子产品费用的基数日期类型为前一日时，取实收资本错误问题处理**/
        String layeringId = acctEntity.getLayeringId();
        /**add by wujie on 20181127 end**/


        // 设计变更：到期日(包含)之后或起息日之前不计提
        // 当【DTO.到期日 <= 批处理参数.发生日 or DTO.起息日 > 批处理参数.发生日】，进行后续处理
        // 2019年10月23日  经确认，到期当天计提，故改为DTO.到期日 < 批处理参数.发生日
        if(mdate.compareTo(happenDate) < 0 || vdate.compareTo(happenDate) > 0){
            return null;
        }


        //定义变量.当日统计分析费用明细汇总信息
        BokStatFeePosiiton bokStatFeePosiiton = new BokStatFeePosiiton();

        // 获取标的信息
        Map<String, Object> targetInfo = this.getTargetInfo(finprodId, cashId, cashType1,
            cashType2, vdate, happenDate, acctEntity.getBranch());

        // if(批处理参数.发生日 > MAP.标的_业绩报酬计提规则.计提结束日) || 批处理参数.发生日<MAP.标的_业绩报酬计提规则.首次计提日
        TargetPerfRewardRule targetPerfRewardRule = null;
        if (targetInfo.get(ValDictCodeList.标的_业绩报酬计提规则.getValue()) != null) {
            List<TargetPerfRewardRule> targetPerfRewardRuleList = (List<TargetPerfRewardRule>)targetInfo.get(
                ValDictCodeList.标的_业绩报酬计提规则.getValue());
            if (!targetPerfRewardRuleList.isEmpty()) {
                targetPerfRewardRule = targetPerfRewardRuleList.get(0);
            }
            if (targetPerfRewardRule != null && (happenDate.compareTo(targetPerfRewardRule.geteAccrDate()) > 0  || happenDate.compareTo(targetPerfRewardRule.getfAccrDate()) < 0 )) {
                return null;
            }
        }

        // 获取 标的现金流计划list
        // List<TargetCashPlan> listCashPlan =
        // (List<TargetCashPlan>)targetInfo.get(ValDictCodeList.标的_现金流计划.getValue());
        // 查询标的现金流计算规则
        List<TargetCashCalcRule> listTargetCashCalcRule = (List<TargetCashCalcRule>)targetInfo.get(
            ValDictCodeList.标的_现金流计算规则.getValue());
        
        /**ADD BY WUJIE ON 20210125 START REMARK:费用无对应现金流计算规则数据时不做计提处理**/
        if(listTargetCashCalcRule==null || listTargetCashCalcRule.size()==0){
        	return null;
        }
        /**ADD BY WUJIE ON 20210125 END REMARK:费用无对应现金流计算规则数据时不做计提处理**/
        
        // 查询标的信息
        TargetFinProduct targetFinProduct = (TargetFinProduct)targetInfo.get(
            ValDictCodeList.标的_信息.getValue());
        // 定义变量 本次计提开始日(默认为reader返回值起息日)
        List<String> paramCodeList = new ArrayList<String>();
        // PRD_FEE_ACCR_TYPE_C03 产品费用计提方式_固定管理费
        paramCodeList.add(ValDictCodeList.账套配置表参数项_产品费用计提方式_固定管理费.getValue());
        // PRD_FEE_ACCR_TYPE_C04 产品费用计提方式_浮动管理费
        //paramCodeList.add(ValDictCodeList.账套配置表参数项_产品费用计提方式_浮动管理费.getValue());
        // PRD_FEE_ACCR_TYPE_C05 产品费用计提方式_托管费
        paramCodeList.add(ValDictCodeList.账套配置表参数项_产品费用计提方式_托管费.getValue());
        // PRD_FEE_ACCR_TYPE_C06 产品费用计提方式_销售费
        paramCodeList.add(ValDictCodeList.账套配置表参数项_产品费用计提方式_销售费.getValue());
        // PRD_FEE_ACCR_TYPE_C09 产品费用计提方式_外包服务费
        paramCodeList.add(ValDictCodeList.账套配置表参数项_产品费用计提方式_外包服务费.getValue());
        // PRD_FEE_ACCR_TYPE_C10 产品费用计提方式_代销费
        paramCodeList.add(ValDictCodeList.账套配置表参数项_产品费用计提方式_代销费.getValue());
        // PRD_FEE_ACCR_TYPE_C18 产品费用计提方式_投资顾问费
        paramCodeList.add(ValDictCodeList.账套配置表参数项_产品费用计提方式_投资顾问费.getValue());
        // 调用账务类共通（DTO.账套代码, 参数项：如下）
        List<BokBooksetExt> listBooksetExt = accountingCommonService.getValBokBooksetExt(booksetId,
            paramCodeList);

        // 获取日期信息
        // 定义变量 本次计提开始日(默认为reader返回值起息日)
        Date vdateNow;
        // 变量.计提方式 = 根据DTO.现金流二级类型 对应的 产品费用计提方式_XX费
        String calType = "";
        if (!CollectionUtils.isEmpty(listBooksetExt)) {
            // 根据现金流二级类型 筛选 对应的账套配置参数List
            if (DictCodeListExt1.现金流二级类型_固定管理费.getValue().equals(cashType2)) {
                listBooksetExt = listBooksetExt.stream().filter(
                    item -> ValDictCodeList.账套配置表参数项_产品费用计提方式_固定管理费.getValue().equals(
                        item.getParamCode())).collect(Collectors.toList());
            }
/*            else if (DictCodeListExt1.现金流二级类型_浮动管理费.getValue().equals(cashType2)) {
                listBooksetExt = listBooksetExt.stream().filter(
                    item -> ValDictCodeList.账套配置表参数项_产品费用计提方式_浮动管理费.getValue().equals(
                        item.getParamCode())).collect(Collectors.toList());
            }*/
            else if (DictCodeListExt1.现金流二级类型_托管费.getValue().equals(cashType2)) {
                listBooksetExt = listBooksetExt.stream().filter(
                    item -> ValDictCodeList.账套配置表参数项_产品费用计提方式_托管费.getValue().equals(
                        item.getParamCode())).collect(Collectors.toList());
            }
            else if (DictCodeListExt1.现金流二级类型_销售费.getValue().equals(cashType2)) {
                listBooksetExt = listBooksetExt.stream().filter(
                    item -> ValDictCodeList.账套配置表参数项_产品费用计提方式_销售费.getValue().equals(
                        item.getParamCode())).collect(Collectors.toList());
            }
            else if (DictCodeListExt1.现金流二级类型_外包服务费.getValue().equals(cashType2)) {
                listBooksetExt = listBooksetExt.stream().filter(
                    item -> ValDictCodeList.账套配置表参数项_产品费用计提方式_外包服务费.getValue().equals(
                        item.getParamCode())).collect(Collectors.toList());
            }
            else if (DictCodeListExt1.现金流二级类型_代销费.getValue().equals(cashType2)) {
                listBooksetExt = listBooksetExt.stream().filter(
                    item -> ValDictCodeList.账套配置表参数项_产品费用计提方式_代销费.getValue().equals(
                        item.getParamCode())).collect(Collectors.toList());
            } 
            else if (DictCodeListExt2.现金流二级类型_投资顾问费.getValue().equals(cashType2)) {
                listBooksetExt = listBooksetExt.stream().filter(
                    item -> ValDictCodeList.账套配置表参数项_产品费用计提方式_投资顾问费.getValue().equals(
                        item.getParamCode())).collect(Collectors.toList());
            }
            // filt筛选后的List若不为空
            if(!CollectionUtils.isEmpty(listBooksetExt)){
                // 获取对应的计提方式
                calType = listBooksetExt.get(0).getParamValue();
            }
        }

        // add by weipeng on 20220304:会计引擎升级改造，增加辅助核算项入参 start
        // 定义 变量.辅助核算项
        SubjectMappingConfig subjectMappingConfig = new SubjectMappingConfig();
        subjectMappingConfig.setCashType2(cashType2);
        // 定义 变量.费用对应科目信息
        BokSubject feeSubject = new BokSubject();
        // add by weipeng on 20220304:会计引擎升级改造，增加辅助核算项入参 end

        // if（变量.计提方式 = 【数据字典】.计提方式."区间倒减"）
        if (DictCodeList.计提方式_区间倒减.getValue().equals(calType)) {

            // add by weipeng on 20220304：由于费用科目对应基础科目性质为一个，查询余额需要按照科目查询 start
            List<BokSubject> bokSubjectList = bookAccountGenLogicNew.getFeeSubjectByCode(DictCodeBaseSubjectNatrueNew.运营费用.getValue(), booksetId, standardCcy);
            feeSubject = bookAccountGenLogicNew.getSubjectByInnerCode(bokSubjectList, subjectMappingConfig);
            // 调用共通方法-科目余额查询，获取计息日
            SubjectBalance subjectBalanceForVdate = this.getSubjectLatestBalanceCommon(booksetId,
                bokDetailId, happenDate, bookDate, taskPri, cashType2, feeSubject.getSubjectNo());
            // add by weipeng on 20220304：由于费用科目对应基础科目性质为一个，查询余额需要按照科目查询 end

            // if （账务明细DTO == Null or 账务明细DTO.结息日 为空是）
            if ((subjectBalanceForVdate == null || subjectBalanceForVdate.getMdate() == null)
                && targetPerfRewardRule != null && targetPerfRewardRule.getfAccrDate() != null ) {
                /*// 本次计提开始日 =DTO.起息日
                 vdateNow = acctEntity.getVdate();*/
                // 本次计提开始日 = MAP.标的_业绩报酬计提规则.首次计提日
                vdateNow = targetPerfRewardRule.getfAccrDate();
            }
            else {
                // 以返回值的结息日，作为本次起息日
                vdateNow = subjectBalanceForVdate.getMdate();
            }
        }
        else {
            // 本次计提开始日 = 批处理参数.发生日
            vdateNow = happenDate;
        }

        // 费用计提
        QuoteInfo quoteInfo = new QuoteInfo();// 定义利率变更
        List<QuoteInfo> listQuoteInfo = new ArrayList<>();// 定义利率变更列表

        // 利率变更列表处理
        for (TargetCashCalcRule rule : listTargetCashCalcRule) {
        	/** add by dongjun on 20190214  Remark:利率为0的报价不能过滤  start**/
            //if (yield.compareTo(rule.getYield()) != 0) {
                quoteInfo = new QuoteInfo();// 定义利率变更
                quoteInfo.setIssueDate(rule.getEffDate());
                quoteInfo.setInterest(rule.getYield());
                quoteInfo.setAccountBasis(rule.getBasis());
                listQuoteInfo.add(quoteInfo);
            //}
            /** add by dongjun on 20190214  Remark:利率为0的报价不能过滤  end**/
        }

        // 计费基数变更列表处理
        // 定义变量：上次计费基数
        BigDecimal feeBaseLast = BigDecimal.ZERO;
        // 定义变量：计费基数变更
        PrincipalInfo principalInfo = new PrincipalInfo();
        // 定义变量：计费基数变更列表
        List<PrincipalInfo> listPrincipalInfo = new ArrayList<>();
//        /** 增加计算规则是否为空判定 add by yanya on 20200218**/

        //BS-1964
        //【批处理】批处理报错
        if(listTargetCashCalcRule == null || listTargetCashCalcRule.size() <= 0){
            return null;
        }
        // 计费基数日期类型 = MAP.GET(现金流计算规则).GET(0).基数日期类型
        String baseDateType = listTargetCashCalcRule.get(0).getBaseDateType();
        String baseType = null;
        // if(MAP.标的_业绩报酬计提规则.首次计提日=DTO标的.起息日){
        // 且发生日为起息日当天时，基数日期类型为当日
        if (targetPerfRewardRule != null && targetPerfRewardRule.getfAccrDate() != null
            && targetPerfRewardRule.getfAccrDate().equals(vdate)
            && DateUtil.compareDate(happenDate,vdate) == 0) {
            // 计费基数类型 = MAP.标的_业绩报酬计提规则.首次计费基数类型
            if(targetPerfRewardRule.getFirstBaseType() == null){
                targetPerfRewardRule.setFirstBaseType(DictCodeList.基数类型_实收资本.getValue());
            }
            baseType = targetPerfRewardRule.getFirstBaseType();
            baseDateType = DictCodeList.基数日期类型_当日.getValue();
        }
        else {
            // 计费基数类型 = MAP.GET(现金流计算规则).GET(0).基数类型
             baseType = listTargetCashCalcRule.get(0).getBaseType();
        }
        // 初始日 = 调用方法：【详细设计-计算类共通方法设计】计算基数日期（本次计提开始日，DTO.交易日历，计费基数日期类型，DTO.金融产品代码）
        Date datebegin = calculationCommonService.calcBasicDate(vdateNow, acctEntity.getCalendarId(), baseDateType, acctEntity.getFinprodId());
        BokStatBokPositionDto position=new BokStatBokPositionDto();
          //查询日开始
          position.setSdateStart(datebegin);
          //查询结束日
          position.setSdateEnd(mdateNow);
          //入账日
          position.setBookDate(bookDate);
          //账套
          position.setBooksetId(booksetId);
          
          /**add by wujie on 20181127 start remark:子产品费用的基数日期类型为前一日时，取实收资本错误问题处理**/
          position.setLayeringId(layeringId);
          /**add by wujie on 20181127 end**/
          
        // 帐套资产负债信息集合 = 调用共通方法查询：帐套资产负债查询（初始日，本次计提结束日，DTO.帐套代码）
/*          List<BokStatBokPosition> valStatBokPositionList = accountingCommonService.getValStatBokPosition(
              datebegin, mdateNow, bookDate, booksetId, null, null);
*/
          List<BokStatBokPosition> valStatBokPositionList = accountingCommonService.getValStatBokPosition(position);

        // FOR(计提日 <- 本次计提开始日 ： 本次计提结束日)
        // 定义 变量计提日 = 初始赋值本次计提开始日
        Date calDate = vdateNow;
        do {
            
            // 计费基数日期 =【详细设计-计算类共通方法设计】计算基数日期（计提日，DTO.交易日历，计费基数日期类型，DTO.金融产品代码）
            Date baseDate = calculationCommonService.calcBasicDate(calDate,acctEntity.getCalendarId(), baseDateType, acctEntity.getFinprodId());

            // 获取帐套资产负债信息集合(以发生日作为key的帐套资产负债信息)
            Map<Date, BokStatBokPosition> valStatBokPositionMap = new HashMap<>();
            for (BokStatBokPosition item : valStatBokPositionList) {
                valStatBokPositionMap.put(item.getHappenDate(), item);
            }
            // 调用公用方法【产品费用计提基数计算】，获取计费基础
            BigDecimal feeBase = calculationCommonService.calProductFee(baseType,
                valStatBokPositionMap.get(baseDate), booksetId, bookDate, standardCcy, happenDate,
                baseDate, taskPri,bokDetailIdList);

            // [上次计费基数]!=[本次计费基数]
            if (feeBase != null && feeBaseLast.compareTo(feeBase) != 0) {
                // 发生便跟，重新实例化
                principalInfo = new PrincipalInfo();

                // 设计变更：每日新增时计费基数<0时当0计算  yangjian 2019/06/17 ----being
                //if（变量.计提方式 = 【数据字典】.计提方式."每日新增"）
                if(DictCodeList.计提方式_每日新增.getValue().equals(calType)){
                    //if（计费基数 <0 ）
                    if(feeBase.compareTo(BigDecimal.ZERO) < 0){
                        // 计费基数变更.变更金额 = 0
                        principalInfo.setPrinAmount(BigDecimal.ZERO);
                    }else{
                        // 计费基数变更.变更金额 = 计费基数
                        principalInfo.setPrinAmount(feeBase);
                    }
                }
                else{
                    // 计费基数变更.变更金额 = 计费基数
                    principalInfo.setPrinAmount(feeBase);
                }
                // 设计变更：每日新增时计费基数<0时当0计算  yangjian 2019/06/17 ----end

                // 计费基数变更.变更日 = 计提日
                principalInfo.setChangeDate(calDate);
                // 上次计费基数 = 计费基数
                feeBaseLast = feeBase;
                // 添加到技术变更列表中
                listPrincipalInfo.add(principalInfo);
            }

            // 变量.计提日+1
            calDate = DateUtil.addDay(calDate, 1);
        }
        while (DateUtil.compareDate(mdateNow, calDate) >= 0);

        // 调用共通：通用计算算法（根据【算头不算尾】的原则，所以到期日加一天。）
        CalCommonResult calCommonResult = calculationCommonService.calCommon(vdateNow,
            DateUtil.addDay(mdateNow, 1), listPrincipalInfo, listQuoteInfo);
        // 费用计提 = 返回值.总利息金额
        BigDecimal feeCal = calCommonResult.getTotalInterestAmt();
        // 调用金额共通方法设置有效小数位
        feeCal = ArithmeticUtil.round(feeCal, NumberEnum.MONEY);

        // 组装记账元素
        // 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();

        // 共通元素设值 - 设置账务引擎参数
        ad.setTreeNode(bookSetTreeNodeNow);// 塞入记账场景树节点 = 会计场景

        // add by weipeng on 20220304:会计引擎升级改造，增加辅助核算项入参 start
        ad.setSubjectMappingConfigDefault(subjectMappingConfig);
        // add by weipeng on 20220304:会计引擎升级改造，增加辅助核算项入参 end


        // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值)
        accountSceneMap = bookCommonLogic.accrualElementAssignForTaFee(taskId, happenDate,
            acctEntity);
        
        if(StringUtil.isNotEmpty(targetFinProduct)){
            accountSceneMap.put(BookConstant.TYPE_1, targetFinProduct.getType1());// 分类1 - 变量.交易信息.分类1
            accountSceneMap.put(BookConstant.TYPE_2, targetFinProduct.getType2());// 分类2 - 变量.交易信息.分类2
        }
        // 当是通道费其他三种类型时，是否累计余额设为否
        if (DictCodeListExt1.现金流二级类型_通道管理费.getValue().equals(cashType2)
            || DictCodeListExt1.现金流二级类型_通道托管费.getValue().equals(cashType2)
            || DictCodeListExt1.现金流二级类型_通道其他费.getValue().equals(cashType2)) {
            // 是否累计余额
            accountSceneMap.put(BookConstant.IS_ACC_BALANCE, DictCodeList.是否_否.getValue());
        }
        
        //查询费用配置信息
        Map<String,Object> map=new HashMap<String,Object>();
        map.put("isCalculation", DictCodeList.是否_是.getValue());
        List<BokFeeTableSet> bokFeeTableSetList=accountingCommonService.getFeeTableSetQuery(map);
        // 变量.计提费用配置entity
        BokFeeTableSet bokFeeTableSetEntity=new BokFeeTableSet();
        for(BokFeeTableSet entity:bokFeeTableSetList){
        	if(cashType2.equals(entity.getFinprodType())){
        		bokFeeTableSetEntity=entity;
        		break;
        	}
        		
        }
        // 定义基础科目性质
        String baseSubjectNature = bokFeeTableSetEntity.getBaseSubjectNature();
        if(DictCodeList.计提方式_区间倒减.getValue().equals(calType)){
            // 设值科目余额查询参数Dto
            SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
            searchDto.setBooksetId(booksetId);
            searchDto.setBokDetailId(bokDetailId);
            searchDto.setHappenDate(happenDate);
            searchDto.setBookDate(bookDate);
            searchDto.setTaskPri(taskPri);
            searchDto.setBaseSubjectNatureList(Arrays.asList(baseSubjectNature));
            // add by weipeng on 20220304: 查询运营费用需要带科目号 start
            searchDto.setSubjectNo(feeSubject.getSubjectNo());
            // 调用公用方法【科目余额查询-期待方向】，获取科目科目余额
            SubjectBalance subjectBalanceForProfit = this.getSubjectBalance(searchDto, DictCodeList.余额方向_贷方.getValue());
            // add by weipeng on 20220304: 查询运营费用需要带科目号 end
            if (subjectBalanceForProfit != null && subjectBalanceForProfit.getBalance() != null) {
                // 费用计提 = 费用计提 - 科目余额
                feeCal = ArithmeticUtil.doSub(feeCal, subjectBalanceForProfit.getBalance());
            }
        }
        String tableId=bokFeeTableSetEntity.getTableId();
        // 设置账务引擎参数
        ad.setTableId(tableId);
        // 记账场景要素map塞值
        accountSceneMap.put(BookConstant.BOOK_REMARK, SysDataUtil.getNameFromDataDict(DictCode.会计场景代码, tableId));// 凭证说明
//        if(cashType2.equals(DictCodeListExt1.现金流二级类型_通道管理费.getValue())
//        		||cashType2.equals(DictCodeListExt1.现金流二级类型_通道托管费.getValue())
//        		||cashType2.equals(DictCodeListExt1.现金流二级类型_通道其他费.getValue())){
//            // 记账金额元素map塞值
//            accountAmountMap.put(BookConstant.CHLAMT_CHARDES, feeCal);// 费用计提
//        }else{
//            // 记账金额元素map塞值
            accountAmountMap.put(BookConstant.ACCRUED_CHARDES, feeCal);// 费用计提
//        }


        // 记账金额元素币种信息塞值
        // 因为与记账金额元素map的key及数量一致，故采用循环
        for (String keyNow : accountAmountMap.keySet()) {
            accountCurrencyMap.put(keyNow, standardCcy);// value值塞入当前交易币种
        }

        // map塞入场景要素管理中
        ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
        ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
        ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

        // 记账凭证生成
        BookGenerateResult resultNowNow = bookAccountGenLogicNew.genAccount(ad, batchExecuteInfo);
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        rtnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());

        
        //3.3 统计分析费用汇总信息赋值
        //定义变量 费用利率
        BigDecimal feeRate = BigDecimal.ZERO; 
        for(QuoteInfo temp:listQuoteInfo){
        	//if（报价.生效日 > 计提类账务处理结果.发生日)
        	if(DateUtil.compareDate(temp.getIssueDate(),temp.getIssueDate())==1){
        		break;
        	}else{
        		//变量.费用利率 = 报价temp.生效利率
        		feeRate =temp.getInterest();
        	}
        }
        //变量.当日统计分析费用明细汇总信息.当日费率 = 变量.费用利率
        bokStatFeePosiiton.setTdyFeeRate(feeRate); 
        //变量.当日统计分析费用明细汇总信息.当日份额 = 变量.计费基数变更列表.变更金额 ※变量.计费基数变更列表取变更日最大的记录
        BigDecimal maxAmt = BigDecimal.ZERO;
        for(PrincipalInfo item:listPrincipalInfo){
        	if(maxAmt.compareTo(item.getPrinAmount())==-1){
        		maxAmt=item.getPrinAmount();
        	}
        }
        bokStatFeePosiiton.setTdyPosition(maxAmt);
        //变量.当日统计分析费用明细汇总信息.费用类型 = DTO.现金流二级类型
        bokStatFeePosiiton.setFeeType(acctEntity.getCashType2());
        //变量.当日统计分析费用明细汇总信息.当日新增费用 = 变量.费用计提
        bokStatFeePosiiton.setTdyIntincexpAdd(feeCal);
        //变量.当日统计分析费用明细汇总信息.金融产品类型 = DTO.金融产品类型
        bokStatFeePosiiton.setFinprodType(acctEntity.getFinprodType());
        //变量.当日统计分析费用明细汇总信息.金融产品代码 = DTO.金融产品代码
        bokStatFeePosiiton.setFinprodId(acctEntity.getFinprodId());
        //变量.当日统计分析费用明细汇总信息.分支序号 = DTO.分支序号
        bokStatFeePosiiton.setBranch(new BigDecimal(acctEntity.getBranch()));
        //变量.当日统计分析费用明细汇总信息.分层代码 = DTO.分层代码
        bokStatFeePosiiton.setLayeringId(acctEntity.getLayeringId());
        //变量.当日统计分析费用明细汇总信息.费用类型 = DTO.现金流类型
        
        //账套代码
        bokStatFeePosiiton.setBooksetId(acctEntity.getBooksetId());
        //发生日期
        bokStatFeePosiiton.setHappenDate(happenDate);
        //入账日期
        bokStatFeePosiiton.setBookDate(acctEntity.getBookDate());
        //账套日期
        bokStatFeePosiiton.setBooksetDate(acctEntity.getBooksetDate());
        
        rtnResult.getBokStatFeePosiitonList().add(bokStatFeePosiiton);
        return rtnResult;
    }

    /**
     * 获取标的相关信息
     * 
     * @param finprodId 金融产品代码
     * @param cashId 现金流代码
     * @param cashType1 现金流一级类型
     * @param cashType2 现金流二级类型
     * @param vdate 起息日
     * @param happenDate 发生日
     * @return Map<String, Object>
     * @see
     * @since 1.0
     */
    private Map<String, Object> getTargetInfo(String finprodId, String cashId, String cashType1,
                                              String cashType2, Date vdate, Date happenDate,
                                              Long branch) {
        // 定义返回值
        Map<String, Object> returnMap = new HashMap<String, Object>();

        // 定义查询类型
        List<TargetSearchDto> queryCriteria = new ArrayList<TargetSearchDto>();

        // 传入[标的_现金流计算规则]Dto
        TargetSearchDto targetSearchDto1 = new TargetSearchDto();
        targetSearchDto1.setCashId(cashId);
        targetSearchDto1.setCashType1(cashType1);
        targetSearchDto1.setCashType2(cashType2);
        targetSearchDto1.setEffdateStart(vdate);
        targetSearchDto1.setEffdateEnd(happenDate);
        targetSearchDto1.setBranch(branch);
        targetSearchDto1.setQueryType(ValDictCodeList.标的_现金流计算规则.getValue());
        queryCriteria.add(targetSearchDto1);

        // 传入[标的_现金流计划]Dto
        TargetSearchDto targetSearchDto2 = new TargetSearchDto();
        targetSearchDto2.setCashId(cashId);
        targetSearchDto2.setCashType1(cashType1);
        targetSearchDto2.setCashType2(cashType2);
        targetSearchDto2.setEffdateStart(vdate);
        targetSearchDto2.setEffdateEnd(happenDate);
        targetSearchDto2.setQueryType(ValDictCodeList.标的_现金流计划.getValue());
        queryCriteria.add(targetSearchDto2);

        // 传入[标的_信息]Dto
        TargetSearchDto targetSearchDto3 = new TargetSearchDto();
        targetSearchDto3.setQueryType(ValDictCodeList.标的_信息.getValue());
        queryCriteria.add(targetSearchDto3);

        // 传入[标的_业绩报酬计提规则]Dto
        TargetSearchDto targetSearchDto4 = new TargetSearchDto();
        targetSearchDto4.setCashId(cashId);
        targetSearchDto4.setCashType1(cashType1);
        targetSearchDto4.setCashType2(cashType2);
        targetSearchDto4.setEffdateStart(vdate);
        targetSearchDto4.setEffdateEnd(happenDate);
        targetSearchDto4.setQueryType(ValDictCodeList.标的_业绩报酬计提规则.getValue());
        queryCriteria.add(targetSearchDto4);

        // 获取标的信息Map 标的信息查询-现金流计算规则
        Map<TargetSearchDto, Object> dealInfoForTarget = finProductCommonService.getTargetInfo(
            finprodId, queryCriteria);

        // 有返回值数据
        if (!MapUtils.isEmpty(dealInfoForTarget)) {
            returnMap.put(ValDictCodeList.标的_现金流计算规则.getValue(),
                    dealInfoForTarget.get(targetSearchDto1)); // 标的现金流计算规则list
            returnMap.put(ValDictCodeList.标的_现金流计划.getValue(),
                    dealInfoForTarget.get(targetSearchDto2)); // 获取 标的现金流计划list 获取
            returnMap.put(ValDictCodeList.标的_信息.getValue(),
                    dealInfoForTarget.get(targetSearchDto3)); // 获取 标的信息
            returnMap.put(ValDictCodeList.标的_业绩报酬计提规则.getValue(),
                    dealInfoForTarget.get(targetSearchDto4)); // 获取 标的_业绩报酬计提规则
        }

        return returnMap;
    }

    /**
     * 调用公用方法：【科目余额查询 - 期待方向】，获取科目余额信息中的科目余额数据
     * 
     * @param booksetId 账套代码
     * @param bokDetailId 账务明细代码
     * @param happenDate 发生日
     * @param bookDate 入账日
     * @param expBalanceDirect 期待余额方向
     * @param baseSubjectNature 基础科目性质
     * @param taskPri 任务优先级
     * @return subjectBalance 科目余额信息
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalance(SubjectBalanceSearchDto searchDto, String expBalanceDirect) {
        // 定义返回实体类
        SubjectBalance subjectBalance = null;
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDto, expBalanceDirect);

        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            subjectBalance = subjectBalanceList.get(0);
        }

        return subjectBalance;
    }

    /**
     * 调用公用方法：【科目余额查询共通】，获取科目余额信息中的科目余额数据
     * 
     * @param booksetId 账套代码
     * @param bokDetailId 账务明细代码
     * @param happenDate 发生日
     * @param bookDate 入账日
     * @param taskPri 任务优先级
     * @param cashType2 现金流二级类型
     * @return subjectBalance 科目余额信息
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectLatestBalanceCommon(String booksetId, String bokDetailId,
                                                         Date happenDate, Date bookDate,
                                                         Long taskPri, String cashType2, String subjectNo) {

        // 定义变量
        String baseSubjectNature =DictCodeBaseSubjectNatrueNew.运营费用.getValue();// 基础科目性质
        String tableId = null;// 场景代码

        // 根据现金流二级类型，获取对应的基础科目性质
        if (DictCodeListExt1.现金流二级类型_托管费.getValue().equals(cashType2)) {
            // 场景代码
            tableId = DictCodeListTableId.会计场景代码_产品_托管费支付.getValue();
        }
        else if (DictCodeListExt1.现金流二级类型_销售费.getValue().equals(cashType2)) {
            // 场景代码
            tableId = DictCodeListTableId.会计场景代码_产品_销售服务费支付.getValue();
        }
        else if (DictCodeListExt1.现金流二级类型_固定管理费.getValue().equals(cashType2)) {
            // 场景代码
            tableId = DictCodeListTableId.会计场景代码_产品_固定资产管理费支付.getValue();
        }
        else if (DictCodeListExt1.现金流二级类型_外包服务费.getValue().equals(cashType2)) {
            // 场景代码
            tableId = DictCodeListTableId.会计场景代码_产品_外包服务费支付.getValue();
        }
        else if (DictCodeListExt1.现金流二级类型_代销费.getValue().equals(cashType2)) {
            // 场景代码
            tableId = DictCodeListTableId.会计场景代码_产品_代销费支付.getValue();
        }
        else if (DictCodeListExt2.现金流二级类型_投资顾问费.getValue().equals(cashType2)) {
            // 场景代码
            tableId = DictCodeListTableId.会计场景代码_产品_投资顾问费支付.getValue();
        }


        // 塞入基础科目性质
        List<String> baseSubjectNatureList = null;
        if (baseSubjectNature != null && baseSubjectNature.length() > 0) {
            baseSubjectNatureList = new ArrayList<String>();
            baseSubjectNatureList.add(baseSubjectNature);
        }

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setHappenDate(happenDate);// 发生日
        searchDto.setBookDate(bookDate);// 入账日
        searchDto.setBokDetailId(bokDetailId);// 账务明细代码
        searchDto.setBooksetId(booksetId);// 账套代码
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);// 基础科目性质
        searchDto.setSubjectNo(subjectNo);// 科目号
        searchDto.setTableId(tableId);// 场景代码
        searchDto.setTaskPri(taskPri);// 任务优先级

        // 调用公用方法 - 科目余额查询共通
        List<SubjectBalance> subjectLatestBalance = accountingCommonService.getSubjectLatestBalance(
            searchDto);

        // 定义返回实体类
        SubjectBalance subjectBalance = null;

        // 获取科目余额
        if (subjectLatestBalance != null && subjectLatestBalance.size() > 0) {
            subjectBalance = subjectLatestBalance.get(0);
        }

        return subjectBalance;
    }

}
