/*
 * 文件名：DepositeAssetTransBookService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V2.0
 * 修改人：weipeng
 * 修改时间：2018年5月23日
 * 修改内容：新建
 * 系统名称：估值核算系统V2.0
 */

package com.joyintech.tams.valuation.batch.depositeassetcalbook.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokStatDetPosiiton;
import com.joyintech.tams.common.base.entity.BokStatLossPosiiton;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResultWithBokSummary;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.calculation.entity.CalCommonResult;
import com.joyintech.tams.valuation.batch.common.calculation.entity.PrincipalInfo;
import com.joyintech.tams.valuation.batch.common.calculation.entity.QuoteInfo;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.*;
import com.joyintech.tams.valuation.common.batch.losscommon.service.LossCommonService;
import com.joyintech.tams.valuation.common.constants.*;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 存款类资产计提账务处理（新） - processor
 * 按照新的内部科目编码体系账务处理
 * @author weipeng
 * @version 1.0 2022年1月3日
 * @see DepositeAssetCalBookNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class DepositeAssetCalBookNewService {

    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogicNew bookLogicNew;

    /**
     * 公用方法services
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 标的共通方法service
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * 计算类共通
     */
    @Autowired
    private CalculationCommonService calculationCommonService;
    
    /**
     * 减值共通
     */
    @Autowired
    private LossCommonService lossCommonService;
    /**
     * DBUtil
     */
    @Autowired
    private DBUtil dBUtil;
    /**
     * process 数据处理
     * 
     * @param param1 参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public BookGenerateResultWithBokSummary execute(Map<String, Object> param1) {
        // 获取reader返回值
        DrawBaseInfoSelectComResult acctEntity = (DrawBaseInfoSelectComResult)param1.get(
            GlobalConstant.READER_RESULT);

        // 定义返回值
        BookGenerateResultWithBokSummary returnResult = new BookGenerateResultWithBokSummary();

        // 账务明细若为空赋初始值
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }

        // 获取从reader中拿到的值
        String booksetIdNow = acctEntity.getBooksetId();// 账套代码
        String bokDetailIdNow = acctEntity.getBokDetailId();// 账务明细代码
        TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();// 会计场景
        Date happenDateNow = acctEntity.getHappenDate();// 发生日期
        Date bookDateNow = acctEntity.getBookDate();// 入账日期
        List<DrawBaseInfoSelectComDTO> listDrawBaseInfoSelectComDTONow = acctEntity.getListDrawBaseInfoSelectComDTO(); // 计提基础信息列表
        Long taskPri = listDrawBaseInfoSelectComDTONow.get(0).getTaskPri();// 任务优先级（信息列表的任务优先级都是一致的，故取第一个）
        String standardCcy = listDrawBaseInfoSelectComDTONow.get(0).getStandardCcy(); // 本位币币种

        // 1. 数据准备
        // 1.1 定义变量
        BigDecimal costBalance = BigDecimal.ZERO;// 变量.成本科目余额
        BigDecimal interest = BigDecimal.ZERO;// 变量.应收利息
        BigDecimal interestBlanceForYstVar = BigDecimal.ZERO;// 变量.应收利息前一日余额

        // 1.2 汇总信息查询
        // 变量.昨日统计分析账务明细汇总信息 = 调用共通方法[账务类共通方法设计 - 统计分析账务明细汇总查询]
//        BokStatDetPosiiton bokSummaryTableYestdy = accountingCommonService.getCountAnalysis(
//            DateUtil.addDay(acctEntity.getHappenDate(), -1),
//            DateUtil.addDay(acctEntity.getHappenDate(), -1), acctEntity.getBookDate(),
//            acctEntity.getBooksetId(), acctEntity.getBokDetailId(), null, null);

        // 2. 调用共通查询
        // 获取初始成本科目余额 (科目性质为"成本")
        SubjectBalance subjectBalance = this.getSubjectBalanceEx(booksetIdNow, bokDetailIdNow,
            happenDateNow, bookDateNow, DictCodeList.余额方向_借方.getValue(),
            DictCodeBaseSubjectNatrueNew.定期存款_本金.getValue(), taskPri);
        costBalance = subjectBalance.getBalance();
        // 变量.当日成本科目数量
        BigDecimal costNum = subjectBalance.getNum();
        /**ADD BY CAOGUO ON 20201020 减值相关修改 START **/
        // 减值准备类科目余额
        subjectBalance = this.getSubjectBalanceEx(booksetIdNow, bokDetailIdNow,
            happenDateNow, bookDateNow, DictCodeList.余额方向_借方.getValue(),
            DictCodeBaseSubjectNatrueNew.定期存款_减值准备.getValue(), taskPri);
        // 变量.当日成本科目数量
        BigDecimal jzzbYe = subjectBalance.getBalance();
        /**ADD BY CAOGUO ON 20201020 END**/
        // 账套核算参数查询
        List<String> paramCodeList = new ArrayList<String>();
        // DEPO_ACCR_TYPE 账套配置表参数项_存款计提方式
        paramCodeList.add(ValDictCodeList.账套配置表参数项_存款计提方式.getValue());
        /**modify by caoguo on 20201020 start 修改调用获取账套参数方法*/
        // 调用账务类共通（DTO.账套代码, 参数项：如下）
        /*List<BokBooksetExt> listBooksetExt = accountingCommonService.getValBokBooksetExt(
            booksetIdNow, paramCodeList,batchExecuteInfo.getBatchExedetailSeqNo());*/
        Map<String, Object> getValBokBooksetExtMap = accountingCommonService
            .getValBokBooksetExtForMap(acctEntity.getBooksetId(), paramCodeList);
        // 变量.计提方式 = 账套配置表参数项_拆借计提方式 对应的计提方式
        String calType = "";
        String calTypetab = "";
        /*if (!CollectionUtils.isEmpty(listBooksetExt)) {
            // 获取第一条即可
            calType = listBooksetExt.get(0).getParamValue();
            calTypetab =listBooksetExt.get(0).getParamValue();
        }*/
        if(getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_拆借计提方式.getValue()) != null){
            calType = (String)getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_拆借计提方式.getValue());
            calTypetab =calType;
        }
        String bccyadjType = "";
        if(getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_本位币差额处理方式.getValue()) != null){
            bccyadjType = (String)getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_本位币差额处理方式.getValue());
        }
        /**modify by caoguo on 20201020 end**/
        // 3. 处理交易
        // 3.1、循环处理计提明细
        // 循环 计提基础信息列表 进行逻辑处理
        for (DrawBaseInfoSelectComDTO dto : listDrawBaseInfoSelectComDTONow) {

            // 获取标的信息.金融产品类型
            TargetFinProduct targetFinProduct = this.getTargetFinProduct(dto.getBusiId());
            
            
            //活期存款类资产判断
            if(DictCodeList.存款类资产类型_GZ_活期类资产.getValue().equals(targetFinProduct.getType1())){
                calType =DictCodeList.计提方式_每日新增.getValue();
            }else{
                calType =calTypetab; 
            }
            Date mdateYsdWithTarget = this.getYesterday(targetFinProduct.getMdate());
            // 调用共通设计-> 标的类共通：标的信息查询（现金流计算规则）
            TargetSearchDto targetSearchDto = new TargetSearchDto();
            targetSearchDto.setQueryType(ValDictCodeList.标的_现金流计算规则.getValue());
            // targetSearchDto.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
            // 针对估值一体化：计提式批处理，获取现金流计算规则，不支持投管【计提式费用】，故只取利息。
            targetSearchDto.setCashType2(DictCodeListExt1.现金流二级类型_利息.getValue());
            List<TargetSearchDto> queryCriteria = new ArrayList<>();
            queryCriteria.add(targetSearchDto);
            // 获取 现金流计算规则列表（报价列表）
            Map<TargetSearchDto, Object> targetInfoMap = finProductCommonService.getTargetInfo(
                dto.getBusiId(), queryCriteria);
            List<TargetCashCalcRule> targetCashCalcRuleList = new ArrayList<TargetCashCalcRule>();
            if (targetInfoMap != null && targetInfoMap.size() > 0
                && targetInfoMap.get(targetSearchDto) != null) {
                targetCashCalcRuleList = (List<TargetCashCalcRule>)targetInfoMap.get(
                    targetSearchDto);
            }
            // 3.1 利息计提
            // if (标的信息.起息日 > 计提类账务处理结果.发生日 )
            if (DateUtil.compareDate(targetFinProduct.getVdate(), happenDateNow) > 0) {
                return null;
            }

            /** 如果是活期存放，不考虑到期日，只要成本余额有值则会计提。modify by chendijun 20200424 start **/
            if(DictCodeList.存款类资产类型_GZ_活期类资产.getValue().equals(targetFinProduct.getType1())) {
                interestBlanceForYstVar = this.getSubjectBalance(dto.getBooksetId(),
                        dto.getBokDetailId(), happenDateNow, bookDateNow,
                        DictCodeList.余额方向_借方.getValue(), DictCodeBaseSubjectNatrueNew.定期存款_应计利息.getValue(),
                        taskPri);
                if(costBalance != null && BigDecimal.ZERO.compareTo(costBalance)<0) {
                    // 定义 变量.本金列表
                    List<PrincipalInfo> prinList = new ArrayList<>();
                    // 本金列表赋值 【参数.发生日，变量.成本科目余额】
                    PrincipalInfo principalInfo = new PrincipalInfo();
                    principalInfo.setChangeDate(happenDateNow);
                    principalInfo.setPrinAmount(costBalance);
                    prinList.add(principalInfo);
                    // 定义 小于等于参数.发生日的最新报价信息
                    QuoteInfo latestQuoteInfo = new QuoteInfo();
                    // 定义 变量.报价列表
                    List<QuoteInfo> quoteList = new ArrayList<>();
                    // 报价列表赋值
                    // 小于等于参数.发生日的最新变量.报价列表组成【参数.发生日，变量.报价.利率】
                    targetCashCalcRuleList.forEach(item -> {
                        if (DateUtil.compareDate(happenDateNow, item.getEffDate()) >= 0) {
                            if (latestQuoteInfo.getIssueDate() == null) {
                                latestQuoteInfo.setInterest(item.getYield());
                                latestQuoteInfo.setIssueDate(item.getEffDate());
                                latestQuoteInfo.setAccountBasis(item.getBasis());
                            }
                            else if (DateUtil.compareDate(latestQuoteInfo.getIssueDate(),
                                item.getEffDate()) <= 0) {
                                latestQuoteInfo.setInterest(item.getYield());
                                latestQuoteInfo.setIssueDate(item.getEffDate());
                                latestQuoteInfo.setAccountBasis(item.getBasis());
                            }
                        }
                    });
                    quoteList.add(latestQuoteInfo);
                    CalCommonResult calCommonResult = calculationCommonService.calCommon(
                        happenDateNow, DateUtil.addDay(happenDateNow, 1), prinList, quoteList);
                    // 变量.应收利息 = 返回值.总利息金额
                    interest = calCommonResult.getTotalInterestAmt();
                    interest = ArithmeticUtil.round(interest,
                            NumberEnum.MONEY);
                } 
                else {
                    interest = BigDecimal.ZERO; 
                }
            }
            /** 如果是活期存放，不考虑到期日，只要成本余额有值则会计提。modify by chendijun 20200424 end **/
            // 结果集发生日 大于 标的到期日前一天
            else if (DateUtil.compareDate(happenDateNow, mdateYsdWithTarget) <= 0) {
                interestBlanceForYstVar = this.getSubjectBalance(dto.getBooksetId(),
                        dto.getBokDetailId(), happenDateNow, bookDateNow,
                        DictCodeList.余额方向_借方.getValue(), DictCodeBaseSubjectNatrueNew.定期存款_应计利息.getValue(),
                        taskPri);
                if (DictCodeList.计提方式_区间倒减.getValue().equals(calType)) {
                    // 1、计算上次付息日到发生日的应收利息
                    // 调用共通设计->【标的类共通方法】->当前付息区间查询
                    List<TargetCashPlan> listCashPlan = finProductCommonService.getCurrentIntRange(
                        dto.getBusiId(), happenDateNow);
                    TargetCashPlan targetCashPlan = listCashPlan.get(0);
                    // 变量.计息开始日 = 返回值.计息开始日
                    Date vdate = targetCashPlan.getVdate();
                    // 变量.计息结束日 = 返回值.计息结束日
//                    Date mDate = targetCashPlan.getMdate();

                    // 定义 变量.本金列表
                    List<PrincipalInfo> prinList = new ArrayList<>();
                    // 本金列表赋值
                    PrincipalInfo principalInfo = new PrincipalInfo();
                    principalInfo.setChangeDate(vdate);
                    principalInfo.setPrinAmount(costBalance);
                    prinList.add(principalInfo);
                    // 定义 变量.报价列表
                    List<QuoteInfo> quoteList = new ArrayList<>();
                    // 报价列表赋值
                    targetCashCalcRuleList.forEach(item -> {
                        QuoteInfo quoteInfo = new QuoteInfo();
                        quoteInfo.setAccountBasis(item.getBasis());
                        quoteInfo.setInterest(item.getYield());
                        quoteInfo.setIssueDate(item.getEffDate());;
                        quoteList.add(quoteInfo);
                    });
                    CalCommonResult calCommonResult = calculationCommonService.calCommon(vdate,
                        DateUtil.addDay(happenDateNow, 1), prinList, quoteList);
                    // 变量.应收利息 = 返回值.总利息金额
                    interest = ArithmeticUtil.round(calCommonResult.getTotalInterestAmt(),
                        NumberEnum.MONEY);
//                    interestBlanceForYstVar = this.getSubjectBalance(dto.getBooksetId(),
//                        dto.getBokDetailId(), happenDateNow, bookDateNow,
//                        DictCodeList.余额方向_借方.getValue(), DictCodeBaseSubjectNatrue.应计利息.getValue(),
//                        taskPri);
                    interest = ArithmeticUtil.doSub(interest, interestBlanceForYstVar);
                }
                else {
                    // 定义 变量.本金列表
                    List<PrincipalInfo> prinList = new ArrayList<>();
                    // 本金列表赋值 【参数.发生日，变量.成本科目余额】
                    PrincipalInfo principalInfo = new PrincipalInfo();
                    principalInfo.setChangeDate(happenDateNow);
                    principalInfo.setPrinAmount(costBalance);
                    prinList.add(principalInfo);
                    // 定义 小于等于参数.发生日的最新报价信息
                    QuoteInfo latestQuoteInfo = new QuoteInfo();
                    // 定义 变量.报价列表
                    List<QuoteInfo> quoteList = new ArrayList<>();
                    // 报价列表赋值
                    // 小于等于参数.发生日的最新变量.报价列表组成【参数.发生日，变量.报价.利率】
                    targetCashCalcRuleList.forEach(item -> {
                        if (DateUtil.compareDate(happenDateNow, item.getEffDate()) >= 0) {
                            if (latestQuoteInfo.getIssueDate() == null) {
                                latestQuoteInfo.setInterest(item.getYield());
                                latestQuoteInfo.setIssueDate(item.getEffDate());
                                latestQuoteInfo.setAccountBasis(item.getBasis());
                            }
                            else if (DateUtil.compareDate(latestQuoteInfo.getIssueDate(),
                                item.getEffDate()) <= 0) {
                                latestQuoteInfo.setInterest(item.getYield());
                                latestQuoteInfo.setIssueDate(item.getEffDate());
                                latestQuoteInfo.setAccountBasis(item.getBasis());
                            }
                        }
                    });
                    quoteList.add(latestQuoteInfo);
                    CalCommonResult calCommonResult = calculationCommonService.calCommon(
                        happenDateNow, DateUtil.addDay(happenDateNow, 1), prinList, quoteList);
                    // 变量.应收利息 = 返回值.总利息金额
                    interest = calCommonResult.getTotalInterestAmt();
                    interest = ArithmeticUtil.round(interest,
                            NumberEnum.MONEY);
                }
            }
            else {
                interest = BigDecimal.ZERO;
            }

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
            ad.setTableId(DictCodeListTableId.会计场景代码_存放同业_利息计提.getValue());// ad.会计场景代码 = 存款类资产每日计提;

            // 记账场景要素map塞值
            // 调用共通场景元素赋值-计提类场景元素
            accountSceneMap = bookCommonLogic.accrualElementAssign(acctEntity, dto);
            // 备注 <- "存款类资产每日计提"
            accountSceneMap.put(BookConstant.BOOK_REMARK, "存款类资产每日计提");
            // 分类1 <- 变量.交易信息.分类1
            accountSceneMap.put(BookConstant.TYPE_1, targetFinProduct.getType1());
            // 分类2 <- 变量.交易信息.分类2
            accountSceneMap.put(BookConstant.TYPE_2, targetFinProduct.getType2());

            // 记账金额元素map塞值
            // 定义 变量.汇率 = 1
            BigDecimal 汇率 = BigDecimal.ONE;
            // 本币与原币不等
            if(!StringUtil.equals(dto.getoCcy(),standardCcy)) {
                //调用计算类 方法共通【汇率查询】
                //基础币种 = 交易明细.币种 and 报价币种 = 交易类账务处理结果.本位币 and 日期 = 交易类账务处理结果.发生日
                /** modify by yanya on 20200108 汇率查询方法变更调用标的类共通汇率查询方法 start**/
                //汇率 = calculationCommonService.queryExchangeRateForTrade(dealNow.getCcy(), standardCcyNow, happenDateNow);
                List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(bookDateNow, booksetIdNow,
                    standardCcy, targetFinProduct.getCcy());
                if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                    汇率 = currencyMarketList.get(0).getClosePrice();
                }
            }
            
            /**ADD BY CAOGUO ON 20201019 减值相关修改 START **/
            BigDecimal interestB = ArithmeticUtil.multiply(NumberEnum.MONEY.getCode(), null, interest, 汇率);
                
            // 变量.利息收入 = 变量.应计利息
            BigDecimal lxsr = interest;
            // 定义 变量.减值计提 = 0
            BigDecimal jzzb = BigDecimal.ZERO;
            // if（计提类账务处理结果.减值阶段 = "P3买入后出现减值" && 变量.投资收益！=0）
            if (dto.getNumP3() > 0 && interest.compareTo(BigDecimal.ZERO) != 0) {
                // 定义 变量.本金列表
                List<PrincipalInfo> prinList = new ArrayList<>();
                // 本金列表赋值 【参数.发生日，变量.成本科目余额】
                PrincipalInfo principalInfo = new PrincipalInfo();
                principalInfo.setChangeDate(happenDateNow);
                principalInfo.setPrinAmount(ArithmeticUtil.doAdd(costBalance,jzzbYe));
                prinList.add(principalInfo);
                // 定义 小于等于参数.发生日的最新报价信息
                QuoteInfo latestQuoteInfo = new QuoteInfo();
                // 定义 变量.报价列表
                List<QuoteInfo> quoteList = new ArrayList<>();
                // 报价列表赋值
                // 小于等于参数.发生日的最新变量.报价列表组成【参数.发生日，变量.报价.利率】
                targetCashCalcRuleList.forEach(item -> {
                    if (DateUtil.compareDate(happenDateNow, item.getEffDate()) >= 0) {
                        if (latestQuoteInfo.getIssueDate() == null) {
                            latestQuoteInfo.setInterest(item.getYield());
                            latestQuoteInfo.setIssueDate(item.getEffDate());
                            latestQuoteInfo.setAccountBasis(item.getBasis());
                        }
                        else if (DateUtil.compareDate(latestQuoteInfo.getIssueDate(),
                            item.getEffDate()) <= 0) {
                            latestQuoteInfo.setInterest(item.getYield());
                            latestQuoteInfo.setIssueDate(item.getEffDate());
                            latestQuoteInfo.setAccountBasis(item.getBasis());
                        }
                    }
                });
                quoteList.add(latestQuoteInfo);
                CalCommonResult calCommonResult = calculationCommonService.calCommon(
                    happenDateNow, DateUtil.addDay(happenDateNow, 1), prinList, quoteList);
                // 变量.利息收入 = 变量.利率 *（变量.成本科目余额 + 变量.减值准备科目余额）
                lxsr = calCommonResult.getTotalInterestAmt();
                // 变量.减值计提 = 变量.应计利息 - 变量.利息收入
                jzzb = ArithmeticUtil.doSub(interest, lxsr);
            }
            // if（变量.减值计提 >= 0）
            if (jzzb.compareTo(BigDecimal.ZERO) >= 0) {
                // 减值计提（贷） = 变量.减值计提
                accountAmountMap.put(BookConstant.IMPAMT_C,
                    ArithmeticUtil.round(jzzb, NumberEnum.MONEY));
                accountCurrencyMap.put(BookConstant.IMPAMT_C, dto.getoCcy());
            }
            else {
                // 减值计提（借） = 变量.减值计提 * -1
                accountAmountMap.put(BookConstant.IMPAMT_D,
                    ArithmeticUtil.round(jzzb.negate(), NumberEnum.MONEY));
                accountCurrencyMap.put(BookConstant.IMPAMT_D, dto.getoCcy());
            }
            
            // 利息收入 <- 变量.利息收入
            accountAmountMap.put(BookConstant.INCOMEAMT, lxsr);
            accountCurrencyMap.put(BookConstant.INCOMEAMT, dto.getoCcy());
            if (DictCodeListExt3.本位币差额处理方式_减值准备.getValue().equals(bccyadjType)) {
                BigDecimal jzzbB = ArithmeticUtil.doSub(interestB, ArithmeticUtil.round(
                    calculationCommonService.doMultiply(lxsr, 汇率), NumberEnum.MONEY));      
                if (jzzbB.compareTo(BigDecimal.ZERO) >= 0) {
                    // 减值计提（贷） = 变量.减值计提
                    accountAmountMap.put(BookConstant.IMPAMT_C + BookConstant.BCCYAMT,
                        ArithmeticUtil.round(jzzbB, NumberEnum.MONEY));
                }
                else {
                    // 减值计提（借） = 变量.减值计提 * -1
                    accountAmountMap.put(BookConstant.IMPAMT_D + BookConstant.BCCYAMT,
                        ArithmeticUtil.round(jzzbB.negate(), NumberEnum.MONEY));
                }
            }else {
                // 利息收入本位币 <- 变量.利息收入*汇率 - 减值准备*汇率
                BigDecimal lxsrB = ArithmeticUtil.doSub(interestB, ArithmeticUtil.round(
                    calculationCommonService.doMultiply(jzzb, 汇率), NumberEnum.MONEY));
                accountAmountMap.put(BookConstant.INCOMEAMT + BookConstant.BCCYAMT,
                    ArithmeticUtil.round(lxsrB, NumberEnum.MONEY));            
            }
            
            /** ADD BY CAOGUO ON 20201019 END **/
            // 应收利息 = 变量.应收利息
            accountAmountMap.put(BookConstant.INTAMT, interest);
            // 应收利息_本位币 = 变量.应收利息
            accountAmountMap.put(BookConstant.INTAMT+BookConstant.BCCYAMT, interestB);  // modify BY CAOGUO ON 20201019 变量修改 
            // 记账金额元素币种信息塞值
            // 应计利息 值塞入当前交易币种（当前为获取计提里面的原币）
            accountCurrencyMap.put(BookConstant.INTAMT, dto.getoCcy());
            // 本位币 <- 变量.本位币
            accountCurrencyMap.put(BookConstant.B_CCY, standardCcy);
            
            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
            ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
            ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素
            // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 start
            ad.setSubjectMappingConfigDefault(dto.getSubjectMappingConfig());
            // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 end
            // 记账凭证生成
            BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            returnResult.getValBokBookdetailBatch().addAll(
                resultNowNow.getValBokBookdetailBatch());

            // 3.3 统计分析账务明细汇总信息赋值
            // 定义：变量.计提利率
            BigDecimal jTrate = BigDecimal.ZERO;
            if (targetCashCalcRuleList != null) {
                for (TargetCashCalcRule rule : targetCashCalcRuleList) {
                    // 报价.生效日 > 计提类账务处理结果.发生日
                    if (rule.getEffDate().compareTo(acctEntity.getHappenDate()) > 0) {
                        break;
                    }

                    // 变量.计提利率 = 报价temp.生效利率
                    jTrate = rule.getYield();
                }
            }
            // 定义当日统计分析账务明细汇总信息
            BokStatDetPosiiton bokSummaryTableNew = new BokStatDetPosiiton();
            // 变量.当日统计分析账务明细汇总信息.当前持仓 = 变量.当日成本科目数量
            bokSummaryTableNew.setPosAmt(costNum);
            // 变量.当日统计分析账务明细汇总信息.计提利率 = 变量.计提利率
            bokSummaryTableNew.setIntRate(jTrate);
            // 变量.当日统计分析账务明细汇总信息.日摊销收益率 =NULL
            bokSummaryTableNew.setDailydscYield(null);
            // 变量.当日统计分析账务明细汇总信息.当日发生应计利息 = 变量.应计利息
            bokSummaryTableNew.setTdyIntincexpAdd(interest);
            // // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额 = 变量.昨日统计分析账务明细汇总信息.当日累计应计利息余 + 变量.应计利息
            // BigDecimal tdyintincexpNow = BigDecimal.ZERO;
            // if (bokSummaryTableYestdy != null) {
            // tdyintincexpNow = ArithmeticUtil.doAdd(bokSummaryTableYestdy.getTdyIntincexp(),
            // interest);
            // }
            // else {
            // tdyintincexpNow = interest;
            // }
            // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额 = 变量.应收利息前一日余额 + 变量.应计利息
            BigDecimal tdyintincexpNow = ArithmeticUtil.doAdd(interestBlanceForYstVar, interest);
            bokSummaryTableNew.setTdyIntincexp(tdyintincexpNow);
            // 变量.当日统计分析账务明细汇总信息.当日份额 = 变量.数量余额
            bokSummaryTableNew.setTdyPosition(costNum);
            // 变量.当日统计分析账务明细汇总信息.投资目的 = 计提类账务处理结果.投资目的
            bokSummaryTableNew.setInvAim(acctEntity.getInvAim());
            // 变量.当日统计分析账务明细汇总信息.当日成本 = 变量.成本科目余额
            bokSummaryTableNew.setTdyCostAmt(costBalance);
            // 变量.当日统计分析账务明细汇总信息.金融产品类型 = 交易标的DTO.金融产品类型
            bokSummaryTableNew.setFinprodType(dto.getBusiDetailType());
            // 账套代码
            bokSummaryTableNew.setBooksetId(acctEntity.getBooksetId());
            // 发生日期
            bokSummaryTableNew.setHappenDate(acctEntity.getHappenDate());
            // 入账日期
            bokSummaryTableNew.setBookDate(acctEntity.getBookDate());
            // 账务明细代码
            bokSummaryTableNew.setBokDetailId(bokDetailIdNow);
            // 账套日期
            bokSummaryTableNew.setBooksetDate(dto.getBooksetDate());
            // 处理序列
            bokSummaryTableNew.setBookSummaryOrder(
                new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.账务汇总表_处理序列)));
            // 变量.当日统计分析账务明细汇总信息.起息日 = 交易标的DTO.起息日
            bokSummaryTableNew.setVdate(targetFinProduct.getVdate());
            // 变量.当日统计分析账务明细汇总信息.到期日 = 交易标的DTO.实际到期日
            bokSummaryTableNew.setMdate(targetFinProduct.getMdate());
            /** modify by yanya on 20200115 判断到期日是否为空 start**/
            // 变量.当日统计分析账务明细汇总信息.剩余期限 = 变量.标的信息.实际到期日-计提类账务处理结果.发生日
//            bokSummaryTableNew.setEndDays1(new BigDecimal(DateUtil.getBetweenDayNumber(
//                acctEntity.getHappenDate(), targetFinProduct.getMdate())));
            // 变量.当日统计分析账务明细汇总信息.剩余存续期 = 变量.标的信息.实际到期日-计提类账务处理结果.发生日
//            bokSummaryTableNew.setEndDays2(new BigDecimal(DateUtil.getBetweenDayNumber(
//                acctEntity.getHappenDate(), targetFinProduct.getMdate())));
            if(targetFinProduct.getMdate() != null) {
                //变量.当日统计分析账务明细汇总信息.剩余期限 = 变量.标的信息.实际到期日-计提类账务处理结果.发生日
                bokSummaryTableNew.setEndDays1(new BigDecimal(DateUtil.getBetweenDayNumber(
                    acctEntity.getHappenDate(), targetFinProduct.getMdate())));
                // 变量.当日统计分析账务明细汇总信息.剩余存续期 = 变量.标的信息.实际到期日-计提类账务处理结果.发生日
                bokSummaryTableNew.setEndDays2(new BigDecimal(DateUtil.getBetweenDayNumber(
                    acctEntity.getHappenDate(), targetFinProduct.getMdate())));
            }
            /** modify by yanya on 20200115 判断到期日是否为空 end**/
            // 变量.当日统计分析账务明细汇总信息.业务明细代码 = 计提类账务处理结果.业务明细代码
            bokSummaryTableNew.setBusiId(acctEntity.getBusiId());
            
            bokSummaryTableNew.setCcy(targetFinProduct.getCcy());


            // 与金茂东确认：当前【统计分析明细表（账务明细）】 相关本位币字段值 = 对应原币字段值 yangjian 2019/08/13 ======== beign
            //本位币
            bokSummaryTableNew.setbCcy(bokSummaryTableNew.getCcy());
            //当日发生应计利息_本位币
            bokSummaryTableNew.setTdyIntincexpAddB(bokSummaryTableNew.getTdyIntincexpAdd());
            // 当日成本_本位币
            bokSummaryTableNew.setTdyCostAmtB(bokSummaryTableNew.getTdyCostAmt());
            // 当日公允价值变动_本位币
            bokSummaryTableNew.setTdyFloatIngplB(bokSummaryTableNew.getTdyFloatIngpl());
            // 当日累计公允价值变动_本位币
            bokSummaryTableNew.setTdyFloatIngplExpB(bokSummaryTableNew.getTdyFloatIngplExp());
            // 当日应计利息_本位币
            bokSummaryTableNew.setTdyIntincexpB(bokSummaryTableNew.getTdyIntincexp());
            // 与金茂东确认：当前【统计分析明细表（账务明细）】 相关本位币字段值 = 对应原币字段值 yangjian 2019/08/13 ======== end
            /**ADD BY CAOGUO ON 20201015 减值相关修改 START **/
            bokSummaryTableNew.setTdyLossAmt(ArithmeticUtil.doAdd(jzzbYe, jzzb.negate()));
            bokSummaryTableNew.setTdyLossAmtB(bokSummaryTableNew.getTdyLossAmt());
            // if（金额元素.减值准备 ！= 0）
            if (jzzb.compareTo(BigDecimal.ZERO) != 0) {
                BokStatLossPosiiton statLoss = null;
                for (ValBokBookdetailBatch detail : resultNowNow.getValBokBookdetailBatch()) {
                    // 变量.账务明细 = 调用账务明细生成共通返回结果中 基础科目性质 ="存放同业减值准备"的第一条数据
                    if (DictCodeBaseSubjectNatrueNew.定期存款_减值准备.getValue().equals(
                        detail.getBaseSubjectNature())) {
                        // 调用【减值相关共通-减值计提明细数据生成】
                        statLoss = lossCommonService.setLossInfoAccr(detail, jzzb.negate(),
                            ArithmeticUtil.round(
                                calculationCommonService.doMultiply(jzzb.negate(), 汇率),
                                NumberEnum.MONEY),
                            ArithmeticUtil.doAdd(jzzbYe, jzzb.negate()),
                            ArithmeticUtil.round(
                                ArithmeticUtil.multiply(
                                    ArithmeticUtil.doAdd(jzzbYe, jzzb.negate()), 汇率),
                                NumberEnum.MONEY),
                            null, null, DictCodeListExt3.减值生成方式_计提批处理.getValue());
                        break;
                    }
                }
                if (statLoss != null) {
                    // 变量.统计分析明细表（资产减值）减值使用列表.add(上述返回值)
                    returnResult.getBokStatLossPosiitonList().add(statLoss);
                }
            }
            /**ADD BY CAOGUO ON 20201015 END**/

            // 添加到返回值中
            returnResult.getBokSummaryList().add(bokSummaryTableNew);
        }

        return returnResult;
    }

    /**
     * 调用共通方法：[标的类共通方法-查询交易信息]，获取标的交易信息
     * 
     * @param finprodId 金融产品代码【必输项】
     * @param finprodType 金融产品类型【必输项】
     * @param branch 分支序号
     * @return ValFinProductDeal 标的查询
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unused")
    private TradeInfoDto getTargetInfo(String finprodId, String finprodType, Long branch) {
        // 定义返回值 - 标的交易信息
        TradeInfoDto tradeInfoDtoForReturn = null;

        // 调用共通方法：[标的类共通方法 - 交易信息查询]，获取标的交易信息
        List<TradeInfoDto> tradeInfoDtoList = finProductCommonService.getTradeInfoDtoList(
            finprodId, finprodType, branch);

        // 当查询结果不为空
        if (tradeInfoDtoList != null && tradeInfoDtoList.size() > 0) {
            // 默认取第一条标的信息
            tradeInfoDtoForReturn = tradeInfoDtoList.get(0);
        }

        return tradeInfoDtoForReturn;
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
     * @return BigDecimal 科目余额
     * @see
     * @since 1.0
     */
    private BigDecimal getSubjectBalance(String booksetId, String bokDetailId, Date happenDate,
                                         Date bookDate, String expBalanceDirect,
                                         String baseSubjectNature, Long taskPri) {
        // 定义返回的科目余额
        BigDecimal balance = BigDecimal.ZERO;

        // 塞入要查询的基础科目性质
        List<String> baseSubjectNatureList = null;
        if (baseSubjectNature != null && baseSubjectNature.length() > 0) {
            baseSubjectNatureList = new ArrayList<String>();
            baseSubjectNatureList.add(baseSubjectNature);
        }

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId);
        searchDto.setBokDetailId(bokDetailId);
        searchDto.setHappenDate(happenDate);
        searchDto.setBookDate(bookDate);
        searchDto.setTaskPri(taskPri);
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDto, expBalanceDirect);

        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            balance = subjectBalanceList.get(0).getBalance();
        }

        return ArithmeticUtil.round(balance, NumberEnum.MONEY);
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
     * @return BigDecimal 科目余额
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalanceEx(String booksetId, String bokDetailId,
                                               Date happenDate, Date bookDate,
                                               String expBalanceDirect, String baseSubjectNature,
                                               Long taskPri) {
        // 定义返回的科目余额
        SubjectBalance balance = new SubjectBalance();

        // 塞入要查询的基础科目性质
        List<String> baseSubjectNatureList = null;
        if (baseSubjectNature != null && baseSubjectNature.length() > 0) {
            baseSubjectNatureList = new ArrayList<String>();
            baseSubjectNatureList.add(baseSubjectNature);
        }

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId);
        searchDto.setBokDetailId(bokDetailId);
        searchDto.setHappenDate(happenDate);
        searchDto.setBookDate(bookDate);
        searchDto.setTaskPri(taskPri);
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDto, expBalanceDirect);

        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            balance = subjectBalanceList.get(0);
        }
        else {
            balance.setBalance(BigDecimal.ZERO);
            balance.setNum(BigDecimal.ZERO);
        }

        return balance;
    }

    /**
     * 获取前一天时间
     * 
     * @param nowDate 传递时间
     * @return Date
     * @see
     * @since 1.0
     */
    private Date getYesterday(Date nowDate) {
        // 非空判断
        if (nowDate == null) {
            return null;
        }

        // 获取科目余额数据
        Calendar calendar = Calendar.getInstance();// 获取日历实例
        calendar.setTime(nowDate);
        calendar.add(Calendar.DAY_OF_MONTH, -1); // 设置为前一天
        Date yesterday = calendar.getTime();// 获得前一天

        return yesterday;
    }

    /**
     * 获取标的基本信息
     * 
     * @param bokDetailId 账务明细代码
     * @return TargetFinProduct 标的基本信息
     * @see
     * @since 1.0
     */
    private TargetFinProduct getTargetFinProduct(String bokDetailId) {
        // 定义查询list
        List<TargetSearchDto> targetSearchDtoList = new ArrayList<TargetSearchDto>();
        // 定义查询类型 实体类
        TargetSearchDto targetSearchDto = new TargetSearchDto();
        targetSearchDto.setQueryType(ValDictCodeList.标的_信息.getValue());
        // 查询实体类塞入查询实体类List中
        targetSearchDtoList.add(targetSearchDto);

        // 获取标的信息
        Map<TargetSearchDto, Object> targetMap = finProductCommonService.getTargetInfo(bokDetailId,
            targetSearchDtoList);
        TargetFinProduct targetFinProduct = (TargetFinProduct)targetMap.get(targetSearchDto);

        // 确保不为null
        if (targetFinProduct == null) {
            targetFinProduct = new TargetFinProduct();
        }

        return targetFinProduct;
    }

}
