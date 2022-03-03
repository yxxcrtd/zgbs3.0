/*
 * 文件名：PlanCurrencyProAccountingProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：niuqianyu
 * 修改时间：2019年1月24日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 * 
 * 
 */

package com.joyintech.tams.valuation.batch.deltrightcurcalamort.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokAssetMarketInfo;
import com.joyintech.tams.common.base.entity.BokStatDetPosiiton;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResultWithBokSummary;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.*;
import com.joyintech.tams.valuation.common.constants.*;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 计划类（货币型）计提账务处理 processor相关业务处理
 * 
 * @author niuqianyu
 * @version 1.0 2019年1月24日
 * @see DeltRightCurCalAmortProcessorNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class DeltRightCurCalAmortProcessorNewService {

    /**
     * 账务类共通方法service
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
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogicNew bookLogicNew;

    @Autowired
    private CalculationCommonService calculationCommonService;

    /**
     * DBUtil
     */
    @Autowired
    private DBUtil dBUtil;

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
        // String booksetIdNow = acctEntity.getBooksetId();// 账套代码
        String bokDetailIdNow = acctEntity.getBokDetailId();// 账务明细代码
        TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();// 会计场景
        Date happenDateNow = acctEntity.getHappenDate();// 发生日期
        Date bookDateNow = acctEntity.getBookDate();// 入账日期

        List<DrawBaseInfoSelectComDTO> listDrawBaseInfoSelectComDTONow = acctEntity.getListDrawBaseInfoSelectComDTO(); // 计提基础信息列表
        Long taskPri = listDrawBaseInfoSelectComDTONow.get(0).getTaskPri();// 任务优先级（信息列表的任务优先级都是一致的，故取第一个）

        /** 20200102 taoran add 本位币标识 start **/
        String standardCcy = listDrawBaseInfoSelectComDTONow.get(0).getStandardCcy();
        /** 20200102 taoran add end **/

        // 1、数据准备
        // 1.1、变量定义
        BigDecimal costBalance = acctEntity.getListDrawBaseInfoSelectComDTO().get(
            0).getoAmtHappenDate();// 变量.成本科目余额
        BigDecimal interestBalance = BigDecimal.ZERO;// 变量.应计利息科目余额
        BigDecimal interestBalanceBccy = BigDecimal.ZERO;// 变量.应计利息科目余额_本位币
        // BigDecimal interestBefBalance = BigDecimal.ZERO;// 变量.应计利息前一日余额
        BigDecimal interest = BigDecimal.ZERO;// 变量.应计利息
        // List<BokBookdetailCurrentExt> bokBookdetailCurrentExtList = new ArrayList<>();//
        // 变量.账务明细辅助列表
        List<String> sceneCodeList = new ArrayList<>();
        sceneCodeList.add(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_申购划款.getValue());
        sceneCodeList.add(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_申购确认.getValue());
        sceneCodeList.add(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_转仓买入.getValue());
        sceneCodeList.add(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_每日计提红利.getValue());
        sceneCodeList.add(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_分红除权.getValue());
        sceneCodeList.add(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_红利发放.getValue());
        sceneCodeList.add(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_红利再投.getValue());
        sceneCodeList.add(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_到期赎回到账.getValue());
        sceneCodeList.add(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_赎回到期.getValue());
        sceneCodeList.add(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_卖出转仓.getValue());

        // 1.2 汇总信息查询
        // 调用共通：统计分析账务明细汇总查询
        // // 查询开始日
        // Date beginDate = DateUtil.addDay(acctEntity.getHappenDate(), -1);
        // // 查询结束日
        // Date endDate = DateUtil.addDay(acctEntity.getHappenDate(), -1);
        // // 变量.昨日统计分析账务明细汇总信息
        // BokStatDetPosiiton bokStatDetPosiitonYestInfo =
        // accountingCommonService.getCountAnalysis(
        // beginDate, endDate, acctEntity.getBookDate(), acctEntity.getBooksetId(),
        // acctEntity.getBokDetailId(), null,
        // null);

        // 2.调用共通查询
        // 2.1.标的类金融产品信息
        // 调用共通方法：查询标的信息（标的信息 、现金流支付规则）
        List<TargetSearchDto> queryCriteria = new ArrayList<>();

        TargetSearchDto targetSearchDto = new TargetSearchDto();
        targetSearchDto.setQueryType(ValDictCodeList.标的_信息.getValue());
        // 生效日期-开始 = 计提类账务处理结果.发生日
        targetSearchDto.setEffdateStart(happenDateNow);
        queryCriteria.add(targetSearchDto);

        TargetSearchDto targetSearchDto2 = new TargetSearchDto();
        targetSearchDto2.setQueryType(ValDictCodeList.标的_现金流支付规则.getValue());
        targetSearchDto2.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        // 生效日期-开始 = 计提类账务处理结果.发生日
        targetSearchDto2.setEffdateStart(happenDateNow);
        queryCriteria.add(targetSearchDto2);

        Map<TargetSearchDto, Object> targetInfoMap = finProductCommonService.getTargetInfo(
            acctEntity.getBusiId(), queryCriteria);
        // 定义 变量.标的信息
        TargetFinProduct targetFinProduct = new TargetFinProduct();
        // 定义 变量.支付规则
        TargetCashPayRule finCashPayRule = new TargetCashPayRule();

        if (targetInfoMap != null && targetInfoMap.size() > 0
            && targetInfoMap.get(targetSearchDto) != null) {
            targetFinProduct = (TargetFinProduct)targetInfoMap.get(targetSearchDto);
        }

        // 获取支付规则
        /**
         * modify by wujie on 20190706 start List<TargetCashPayRule> listTargetCashPayRule =
         * (List<TargetCashPayRule>)targetInfoMap.get( targetSearchDto2); if (targetInfoMap != null
         * && targetInfoMap.size() > 0 && !CollectionUtils.isEmpty(listTargetCashPayRule)) {
         * finCashPayRule = listTargetCashPayRule.get(0);; }
         **/
        if (targetInfoMap != null && targetInfoMap.get(targetSearchDto2) != null) {
            List<TargetCashPayRule> listTargetCashPayRule = (List<TargetCashPayRule>)targetInfoMap.get(
                targetSearchDto2);
            if (!CollectionUtils.isEmpty(listTargetCashPayRule)) {
                finCashPayRule = listTargetCashPayRule.get(0);
            }
        }
        /** modify by wujie on 20190706 end **/

        // // 2.2.净值数据延迟天数查询
        // // 调用标的共通方法：金融产品估值参数查询
        // // 定义 变量.净值数据延迟天数
        // String paramValue = finProductCommonService.queryParamValue(acctEntity.getBusiId(),
        // new Long(0), DictCode.净值数据延迟天数.getValue());

        // 3.处理交易
        // 循环 计提基础信息列表 进行逻辑处理
        // 定义 变量.万分收益
        BigDecimal valuation = BigDecimal.ZERO;
        // 定义 变量.估值日期
        Date valDate = null;
        for (DrawBaseInfoSelectComDTO dto : listDrawBaseInfoSelectComDTONow) {

            // todo : 与王之昂确认，此处使用市场代码，不使用业务明细代码(金融产品代码) update by yangjian 2019/02/26
            // 调用共通设计->【详细设计-标的类共通方法设计.xlsx】->万分收益-计划类货币型
            // ValFinProductValuationDto finProductValuation =
            // finProductCommonService.queryFinProductValuation(dto.getBusiId(),
            // targetFinProduct.getCalendarId(), happenDateNow,0);
            ValFinProductValuationDto finProductValuation = finProductCommonService.queryFinProductValuation(
                targetFinProduct.getFinprodMarketId(), targetFinProduct.getCalendarId(),
                happenDateNow, dto.getBooksetId());

            // 定义 变量.万分收益 = DTO.万份收益
            if (finProductValuation != null) {
                // valuation = ArithmeticUtil.round(finProductValuation.getValuation(),
                // NumberEnum.MONEY);
                valuation = finProductValuation.getValuation();
                // 定义 变量.估值日期 = DTO.估值日
                valDate = finProductValuation.getValDate();
            }

            // 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
            // 估值日期 = 参数.发生日时，传入处理参数.任务优先级，否则为空
            Long taskPri2 = null;
            if (DateUtil.compareDate(valDate, happenDateNow) == 0) {
                taskPri2 = taskPri;
            }
            BigDecimal balance = this.getSubjectBalance(dto.getBooksetId(), dto.getBokDetailId(),
                valDate, bookDateNow, DictCodeList.余额方向_借方.getValue(),
                DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue(), taskPri2);
            // 变量.计算基数 = DTO.科目余额
            BigDecimal calculateBase = balance;

            // modified by wangzhiang 【一体化改造】基金结转频率数据字典更换 201800413 start
            if (DictCodeList.收益结转频率_按日结转.getValue().equals(finCashPayRule.getPayCycle())
                || (!DictCodeList.收益结转频率_按日结转.getValue().equals(finCashPayRule.getPayCycle())
                    && DictCodeList.是否_是.getValue().equals(targetFinProduct.getIsCompoundInt()))) {
                // modified by wangzhiang 【一体化改造】基金结转频率数据字典更换 201800413 end

                // 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
                // 变量.应收红利余额
                BigDecimal divBalance = this.getSubjectBalance(dto.getBooksetId(),
                    dto.getBokDetailId(), valDate, bookDateNow, DictCodeList.余额方向_借方.getValue(),
                    DictCodeBaseSubjectNatrueNew.应收股利.getValue(), taskPri);
                // 变量.计算基数 = 变量.计算基数 + 变量.应收红利余额
                calculateBase = ArithmeticUtil.doAdd(calculateBase, divBalance);
            }
            // 变量.应计利息 = 变量.计算基数*变量.万分收益/10000
            interest = ArithmeticUtil.divide(ArithmeticUtil.multiply(calculateBase, valuation),
                10000, NumberEnum.MONEY.getCode());

            // add by weipeng 20210807：增加资产行情信息入库
            // 变量.资产行情信息DTO
            BokAssetMarketInfo bokAssetMarketInfo = new BokAssetMarketInfo();
            //变量.资产行情信息DTO.流水号
            bokAssetMarketInfo.setSeqNo(IDGenerator.generate());
            // 变量.资产行情信息DTO.账套代码
            bokAssetMarketInfo.setBooksetId(acctEntity.getBooksetId());
            // 变量.资产行情信息DTO.发生日期
            bokAssetMarketInfo.setHappenDate(acctEntity.getHappenDate());
            // 变量.资产行情信息DTO.入账日期
            bokAssetMarketInfo.setBookDate(acctEntity.getBookDate());
            // 变量.资产行情信息DTO.金融产品代码 = 变量.标的信息.金融产品代码
            bokAssetMarketInfo.setFinprodId(targetFinProduct.getFinprodId());
            // 变量.资产行情信息DTO.金融产品类型 = 变量.标的信息.金融产品类型
            bokAssetMarketInfo.setFinprodType(targetFinProduct.getFinprodType());
            // 变量.资产行情信息DTO.投资目的 = 计提类账务处理结果.投资目的
            bokAssetMarketInfo.setInvAim(acctEntity.getInvAim());
            // 变量.资产行情信息DTO.市场代码 = 变量.标的信息.市场代码
            bokAssetMarketInfo.setMarketCode(targetFinProduct.getFinprodMarketId());
            // 变量.资产行情信息DTO.估值来源 = 市价共通返回.估值来源
            bokAssetMarketInfo.setValueSource(null);
            // 变量.资产行情信息DTO.市价 = 变量.市价
            bokAssetMarketInfo.setMarketPrice(valuation);
            // 变量.资产行情信息DTO.是否来源于行情数据
            // if 市价共通返回.市价== null
            if(finProductValuation ==null || finProductValuation.getValuation() == null){
                // 变量.资产行情信息DTO.行情日期 = 市价共通返回.行情日期
                bokAssetMarketInfo.setValueDate(happenDateNow);
                bokAssetMarketInfo.setDataSource(DictCodeListExt1.行情价格数据来源_系统生成.getValue());
            }
            else{
                // 变量.资产行情信息DTO.行情日期 = 市价共通返回.行情日期
                bokAssetMarketInfo.setValueDate(finProductValuation.getValDate());
                // 变量.资产行情信息DTO.估值方案代码 = 市价共通返回.估值方案代码
                bokAssetMarketInfo.setSchemeId(finProductValuation.getSchemeId());
                // 变量.资产行情信息DTO.估值方案明细代码 = 市价共通返回.估值方案明细代码
                bokAssetMarketInfo.setSchemeDetailId(finProductValuation.getSchemeDetailId());
                bokAssetMarketInfo.setDataSource(DictCodeListExt1.行情价格数据来源_行情信息.getValue());
            }
            // 添加到返回值中
            returnResult.getBokAssetMarketInfoList().add(bokAssetMarketInfo);
            // 组装记账元素
            // 初始化定义 - 记账场景要素管理
            AccountDriver ad = new AccountDriver();

            // 初始化定义 - 存放该笔记账的场景要素 map定义
            Map<String, Object> accountSceneMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
            Map<String, Object> accountCurrencyMap = new HashMap<>();

            /** add by taoran 20191231 start **/
            // 汇率
            BigDecimal changeRate = BigDecimal.ONE;
            if (StringUtil.isNotEmpty(targetFinProduct.getCcy())
                    && !targetFinProduct.getCcy().equals(standardCcy)) {
                // 调用共通汇率查询
                // changeRate =
                // calculationCommonService.queryExchangeRateForTrade(targetFinProduct.getCcy(),
                // standardCcy, acctEntity.getHappenDate());
                /** modify by yanya on 20200108 汇率查询方法变更调用标的类共通汇率查询方法 start **/
                List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(
                        bookDateNow, acctEntity.getBooksetId(), standardCcy, targetFinProduct.getCcy());
                if (!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null) {
                    changeRate = currencyMarketList.get(0).getClosePrice();
                }
                /** modify by yanya on 20200108 汇率查询方法变更调用标的类共通汇率查询方法 end **/
            }
            /** add by taoran 20191231 end **/

            // 共通元素设值 - 设置账务引擎参数
            ad.setTreeNode(bookSetTreeNodeNow);// 塞入记账场景树节点 = 会计场景
            ad.setTableId(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_每日计提红利.getValue());// ad.会计场景代码 =
            // 货币基金每日分红计提;

            // 记账场景要素map塞值
            // 调用共通场景元素赋值-计提类场景元素
            accountSceneMap = bookCommonLogic.accrualElementAssign(acctEntity,
                    listDrawBaseInfoSelectComDTONow.get(0));
            // 备注 <- "存款类资产每日计提"
            accountSceneMap.put(BookConstant.BOOK_REMARK, "计划类（货币型）资产每日红利计提");
            // 境内外 <- 变量.标的信息.境内外
            accountSceneMap.put(BookConstant.BLN_AREA, targetFinProduct.getBlnArea());

            // 记账金额元素map塞值
            // 变量.应计利息 > 0
            if (interest.compareTo(BigDecimal.ZERO) > 0) {
                // 红利计提（借) <- 变量.应计利息
                accountAmountMap.put(BookConstant.INTAMT_D, interest);
                // 红利计提1（贷) <- 变量.应计利息
                accountAmountMap.put(BookConstant.INTAMT1_C, interest);
                // 本币红利计提（借) <- 变量.应计利息*汇率
                accountAmountMap.put(BookConstant.INTAMT_D + BookConstant.BCCYAMT,
                        calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, interest,
                                changeRate));
                // 本币红利计提1（贷) <- 变量.应计利息*汇率
                accountAmountMap.put(BookConstant.INTAMT1_C + BookConstant.BCCYAMT,
                        accountAmountMap.get(BookConstant.INTAMT_D + BookConstant.BCCYAMT));
            }
            else {
                // 红利计提（贷) <- 变量.应计利息*-1
                accountAmountMap.put(BookConstant.INTAMT_C,
                        ArithmeticUtil.multiply(interest, new BigDecimal(-1)));
                // 红利计提1（借) <- 变量.应计利息*-1
                accountAmountMap.put(BookConstant.INTAMT1_D,
                        calculationCommonService.doMultiply(interest, new BigDecimal(-1)));
                // 本币红利计提（贷) <- 红利计提（贷)*汇率
                accountAmountMap.put(BookConstant.INTAMT_C + BookConstant.BCCYAMT,
                        calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null,
                                accountAmountMap.get(BookConstant.INTAMT_C), changeRate));
                // 本币红利计提1（借) <- 本币红利计提（贷)
                accountAmountMap.put(BookConstant.INTAMT1_D + BookConstant.BCCYAMT,
                        accountAmountMap.get(BookConstant.INTAMT_C + BookConstant.BCCYAMT));
            }

            // 记账金额元素币种信息塞值
            // 因为与记账金额元素map的key及数量一致，故采用循环
            for (String keyNow : accountAmountMap.keySet()) {
                accountCurrencyMap.put(keyNow, listDrawBaseInfoSelectComDTONow.get(0).getoCcy());// value值塞入当前交易币种
                // （当前为获取计提里面的原币）
            }
            /** add by taoran 20191231 start **/
            accountCurrencyMap.put(BookConstant.B_CCY, standardCcy);
            /** add by taoran 20191231 end **/
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
            returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());

            // 3.3 统计分析账务明细汇总信息赋值
            // 调用共通：统计分析账务明细汇总查询
            // 变量.当日统计分析账务明细汇总信息
            BokStatDetPosiiton bokSummaryTableNew = accountingCommonService
                    .getCountAnalysis(acctEntity.getHappenDate(), acctEntity.getHappenDate(), acctEntity.getBookDate(), acctEntity.getBooksetId(), acctEntity.getBokDetailId(), null, null);
            if(bokSummaryTableNew == null) {
                bokSummaryTableNew = new BokStatDetPosiiton();
                // 变量.当日统计分析账务明细汇总信息.账套代码 = 计提类账务处理结果.账套代码
                bokSummaryTableNew.setBooksetId(acctEntity.getBooksetId());
                // 变量.当日统计分析账务明细汇总信息.发生日期 = 计提类账务处理结果.发生日期
                bokSummaryTableNew.setHappenDate(acctEntity.getHappenDate());
                // 变量.当日统计分析账务明细汇总信息.入账日期 = 计提类账务处理结果.入账日期
                bokSummaryTableNew.setBookDate(acctEntity.getBookDate());
                // 变量.当日统计分析账务明细汇总信息.账务明细代码 = 计提类账务处理结果.账务明细代码
                bokSummaryTableNew.setBokDetailId(acctEntity.getBokDetailId());
                // 变量.当日统计分析账务明细汇总信息.处理序列 = 调用序列生成共通
                bokSummaryTableNew.setBookSummaryOrder(
                        new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.账务汇总表_处理序列)));
                // 变量.当日统计分析账务明细汇总信息.账套日期 = 计提类账务处理结果.账套日期
                bokSummaryTableNew.setBooksetDate(listDrawBaseInfoSelectComDTONow.get(0).getBooksetDate());
                // 变量.当日统计分析账务明细汇总信息.币种 = 变量.标的信息.币种
                bokSummaryTableNew.setCcy(targetFinProduct.getCcy());
                // 变量.当日统计分析账务明细汇总信息.业务明细代码 = 计提类账务处理结果.业务明细代码
                bokSummaryTableNew.setBusiId(acctEntity.getBusiId());
                // 变量.当日统计分析账务明细汇总信息.本位币 = 计提类账务处理结果.本位币
                bokSummaryTableNew.setbCcy(listDrawBaseInfoSelectComDTONow.get(0).getStandardCcy());
                // 变量.当日统计分析账务明细汇总信息.金融产品类型 = 变量.标的信息.金融产品类型
                bokSummaryTableNew.setFinprodType(targetFinProduct.getFinprodType());
                // 变量.当日统计分析账务明细汇总信息.起息日 = 变量.标的信息.起息日
                bokSummaryTableNew.setVdate(targetFinProduct.getVdate());
                // 变量.当日统计分析账务明细汇总信息.到期日 = 变量.标的信息.实际到期日
                bokSummaryTableNew.setMdate(targetFinProduct.getActmdate());
            }
            // 变量.当日统计分析账务明细汇总信息.当前持仓 = 计提类账务处理结果.成本科目数量
            bokSummaryTableNew.setPosAmt(
                    acctEntity.getListDrawBaseInfoSelectComDTO().get(0).getNumAmtHappenDate());
            // 变量.当日统计分析账务明细汇总信息.计提利率 = 变量.万份收益
            bokSummaryTableNew.setIntRate(valuation);
            // 变量.当日统计分析账务明细汇总信息.日摊销收益率 =NULL
            bokSummaryTableNew.setDailydscYield(null);
            // 变量.当日统计分析账务明细汇总信息.当日发生应计利息 = 变量.应计利息
            bokSummaryTableNew.setTdyIntincexpAdd(interest);
            // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额 = 变量.应计利息科目余额 + 变量.应计利息
            BigDecimal tdyintincexpNow = ArithmeticUtil.doAdd(interestBalance, interest);
            bokSummaryTableNew.setTdyIntincexp(tdyintincexpNow);
            // 变量.当日统计分析账务明细汇总信息.当日份额 = 变量.当日成本科目数量
            bokSummaryTableNew.setTdyPosition(
                    acctEntity.getListDrawBaseInfoSelectComDTO().get(0).getNumAmtHappenDate());
            // 变量.当日统计分析账务明细汇总信息.投资目的 = 计提类账务处理结果.投资目的
            bokSummaryTableNew.setInvAim(acctEntity.getInvAim());
            // 变量.当日统计分析账务明细汇总信息.当日成本 = 变量.当日成本科目余额
            bokSummaryTableNew.setTdyCostAmt(costBalance);
            // 变量.当日统计分析账务明细汇总信息.金融产品类型 = 变量.标的信息.金融产品类型
            // bokSummaryTableNew.setFinprodType(targetFinProduct.getFinprodType());
            // 变量.当日统计分析账务明细汇总信息.起息日 = 变量.标的信息.起息日
            // bokSummaryTableNew.setVdate(targetFinProduct.getVdate());
            // 变量.当日统计分析账务明细汇总信息.到期日 = 变量.标的信息.实际到期日
            // bokSummaryTableNew.setMdate(targetFinProduct.getActmdate());
            // 变量.当日统计分析账务明细汇总信息.行情日期 = 变量.估值日期
            bokSummaryTableNew.setValDate(valDate);
            // 变量.当日统计分析账务明细汇总信息.业务明细代码 = 计提类账务处理结果.业务明细代码
            // bokSummaryTableNew.setBusiId(acctEntity.getBusiId());
            // 变量.当日统计分析账务明细汇总信息.账套日期 = 变量.账套日期 add by zhangzhao 2020/4/14 BSTOT-2128
            bokSummaryTableNew.setBooksetDate(acctEntity.getListDrawBaseInfoSelectComDTO().get(0).getBooksetDate());
            // 账套代码
            bokSummaryTableNew.setBooksetId(acctEntity.getBooksetId());
            // 发生日期
            bokSummaryTableNew.setHappenDate(acctEntity.getHappenDate());
            // 入账日期
            bokSummaryTableNew.setBookDate(acctEntity.getBookDate());
            // 账务明细代码
            bokSummaryTableNew.setBokDetailId(bokDetailIdNow);
            // 处理序列
            bokSummaryTableNew.setBookSummaryOrder(
                    new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.账务汇总表_处理序列)));

            /** modify by yanya on 20190924 start **/
            bokSummaryTableNew.setCcy(targetFinProduct.getCcy());
            /** modify by yanya on 20190924 end **/

            // 与金茂东确认：当前【统计分析明细表（账务明细）】 相关本位币字段值 = 对应原币字段值 yangjian 2019/08/13 ======== beign
            // 本位币
            bokSummaryTableNew.setbCcy(bokSummaryTableNew.getCcy());
            // 当日发生应计利息_本位币
            bokSummaryTableNew.setTdyIntincexpAddB(bokSummaryTableNew.getTdyIntincexpAdd());
            // 当日成本_本位币
            bokSummaryTableNew.setTdyCostAmtB(bokSummaryTableNew.getTdyCostAmt());
            // 当日公允价值变动_本位币
            bokSummaryTableNew.setTdyFloatIngplB(bokSummaryTableNew.getTdyFloatIngpl());
            // 当日累计公允价值变动_本位币
            bokSummaryTableNew.setTdyFloatIngplExpB(bokSummaryTableNew.getTdyFloatIngplExp());
            // 当日应计利息_本位币
            // bokSummaryTableNew.setTdyIntincexpB(bokSummaryTableNew.getTdyIntincexp());
            // 与金茂东确认：当前【统计分析明细表（账务明细）】 相关本位币字段值 = 对应原币字段值 yangjian 2019/08/13 ======== end
            /** add by taoran 20200103 start **/
            // 本位币
            bokSummaryTableNew.setbCcy(standardCcy);
            // 当日发生应计利息_本位币interestBalance
            bokSummaryTableNew.setTdyIntincexpAddB(calculationCommonService.doMultiply(
                    NumberEnum.MONEY.getCode(), null, interest, changeRate));
            // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额_本位币 = 变量.应计利息科目余额本位币 + 变量.应计利息 * 汇率
            BigDecimal tdyintincexpNowBccy = ArithmeticUtil.doAdd(interestBalanceBccy,
                    calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, interest,
                            changeRate));
            bokSummaryTableNew.setTdyIntincexp(tdyintincexpNowBccy);
            /** add by taoran 20200103 end **/

            // 变量.当日统计分析账务明细汇总信息.当日成本_本位币 = 变量.当日成本科目余额*变量.汇率
            bokSummaryTableNew.setTdyCostAmtB(calculationCommonService.doMultiply(
                    NumberEnum.MONEY.getCode(), null, costBalance, changeRate));
            // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额_本位币 = 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额*变量.汇率
            bokSummaryTableNew.setTdyIntincexpB(calculationCommonService.doMultiply(
                    NumberEnum.MONEY.getCode(), null, bokSummaryTableNew.getTdyIntincexp(), changeRate));

            // 添加到返回值中
            returnResult.getBokSummaryList().add(bokSummaryTableNew);
        }
        return returnResult;
    }

    // /**
    // * 调用共通查询：查询标的信息
    // *
    // * @param bokDetailId
    // * @return
    // * @see
    // * @since 1.0
    // */
    // private TargetFinProduct getTargetFinProduct(String bokDetailId, Date happenDateNow) {
    // // 定义查询list
    // List<TargetSearchDto> targetSearchDtoList = new ArrayList<TargetSearchDto>();
    // // 定义查询类型 实体类
    // TargetSearchDto targetSearchDto = new TargetSearchDto();
    // targetSearchDto.setQueryType(ValDictCodeList.标的_信息.getValue());
    // // 生效日期-开始 = 计提类账务处理结果.发生日
    // targetSearchDto.setEffdateStart(happenDateNow);
    // // 查询实体类塞入查询实体类List中
    // targetSearchDtoList.add(targetSearchDto);
    //
    // // 获取标的信息
    // Map<TargetSearchDto, Object> targetMap = finProductCommonService.getTargetInfo(bokDetailId,
    // targetSearchDtoList);
    // TargetFinProduct targetFinProduct = (TargetFinProduct)targetMap.get(targetSearchDto);
    //
    // // 确保不为null
    // if (targetFinProduct == null) {
    // targetFinProduct = new TargetFinProduct();
    // }
    //
    // return targetFinProduct;
    // }

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

}
