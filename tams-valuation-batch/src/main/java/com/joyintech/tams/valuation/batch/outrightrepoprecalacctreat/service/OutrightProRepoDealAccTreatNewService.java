/*
 * 文件名：DepositeAssetTransBookService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V2.0
 * 修改人：weipeng
 * 修改时间：2018年5月23日
 * 修改内容：新建
 * 系统名称：估值核算系统V2.0
 */

package com.joyintech.tams.valuation.batch.outrightrepoprecalacctreat.service;

import com.joyintech.calculation.CalculatorFactory;
import com.joyintech.calculation.bonds.PaperIrReset;
import com.joyintech.calculation.bonds.SecIntSchedule;
import com.joyintech.calculation.bonds.constant.*;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.shell.AccurIntForBookCalculator;
import com.joyintech.calculation.shell.param.AccurIntCalInParameter;
import com.joyintech.calculation.shell.param.AccurIntCalOutParameter;
import com.joyintech.calculation.shell.param.SecInfoModel;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.calculation.util.BasicUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.SubjectMappingConfig;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.batch.outrightrepoprecalacctreat.dao.OutrightProRepoDealAccTreatMapper;
import com.joyintech.tams.valuation.batch.outrightrepoprecalacctreat.dto.FinDealPledgeOutRightPro;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.*;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrueNew;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import com.joyintech.tams.valuation.common.service.BookAccountCommonService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 买断式回购预交易账务处理 process相关业务处理
 * 
 * @author leipeng
 * @version 1.0 2018年7月26日
 * @see OutrightProRepoDealAccTreatNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class OutrightProRepoDealAccTreatNewService {

    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogicNew bookLogicNew;

    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * 公用方法services
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    @Autowired
    private BookAccountCommonService bookAccountCommonService;

    /**
     * 标的共通方法类
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    @Autowired
    private OutrightProRepoDealAccTreatMapper outrightProRepoDealAccTreatMapper;

    /**
     * process 数据处理
     * 
     * @param param1 参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    @SuppressWarnings({"unchecked", "unused"})
    public BookGenerateResult execute(Map<String, Object> param1) {
        // 获取reader返回值
        FinProductDealInfoResult acctEntity = (FinProductDealInfoResult)param1.get(
            GlobalConstant.READER_RESULT);

        // 若reader传递的参数为空，不进行后续处理
        if (acctEntity == null) {
            return null;
        }

        // 定义返回值
        BookGenerateResult returnResult = new BookGenerateResult();
        // 防止报空指针异常
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }

        // 获取待处理任务信息
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param1.get("BATCHINFO");

        // 获取从reader中拿到的值
        String booksetIdNow = acctEntity.getBooksetId();// 账套代码
        String bokDetailId = acctEntity.getBokDetailId();// 账务明细代码
        TreeNode bookSetTreeNodNow = acctEntity.getBookSetTreeNode();// 会计场景
        Date happenDateNow = acctEntity.getHappenDate();// 发生日期
        Date bookDateNow = acctEntity.getBookDate();// 入账日期
        List<FinProductDealInfoComDTO> listFinProductDealInfoComDTONow = acctEntity.getListFinProductDealInfoComDTO(); // 金融产品交易信息list
        Long taskPri = listFinProductDealInfoComDTONow.get(0).getTaskPri();// 任务优先级（信息列表的任务优先级都是一致的，故取第一个）

        // 1. 数据准备
        // 1.1 定义变量
        // 定义 变量.成本科目余额
        BigDecimal costBalance = BigDecimal.ZERO;
        // 变量.其他应收款-利息科目余额
        BigDecimal interestBalance = BigDecimal.ZERO;
        // 变量.其他应收款-本金科目余额
        BigDecimal prinamtBalance = BigDecimal.ZERO;

        // 2.调用共通查询发生前一科目余额查询 - 期待方向）,获取成本类科目余额
        // 交易期待方向——借方
        String expBalanceDirect = DictCodeList.余额方向_借方.getValue();

        // 2.1 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
        // 3. 交易处理
        // 循环 交易类账务处理结果.交易明细列表,进行逻辑处理
        for (FinProductDealInfoComDTO dealNow : listFinProductDealInfoComDTONow) {
            // 基础科目性质 : 成本
            String baseSubjectNatureCB = DictCodeBaseSubjectNatrueNew.买入返售金融资产_成本.getValue();
            if(dealNow.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券买断式正回购.getValue())){
                baseSubjectNatureCB = DictCodeBaseSubjectNatrueNew.卖出回购金融资产_成本.getValue();
            }
            SubjectBalance subjectBalance = this.getSubjectBalance(booksetIdNow, bokDetailId,
                happenDateNow, bookDateNow, expBalanceDirect, baseSubjectNatureCB,
                null, taskPri);
            if (subjectBalance != null) {
                // 成本类科目余额赋值
                costBalance = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
            }
            // 调用共通设计 ：标的类共通-交易信息查询
            TradeInfoDto tradeInfoDto = this.getTargetInfo(dealNow.getFinprodId(),
                dealNow.getFinprodType(), BookConstant.DEFALUT_BRANCH);
            // 保证获取的标的交易信息不为空
            if (tradeInfoDto == null) {
                tradeInfoDto = new TradeInfoDto();
            }
//            // if 交易明细.金融产品类型 = “债券买断式逆回购”or 交易明细.金融产品类型 = “债券买断式正回购”
//            if (dealNow.getFinprodType().equals(DictCodeList.金融产品类型_债券买断式逆回购.getValue())
//                || dealNow.getFinprodType().equals(DictCodeList.金融产品类型_债券买断式正回购.getValue())) {
                // 1. 查询付息区间内的买断式逆回购质押品信息
                FinDealPledgeOutRightPro finDealPledge = outrightProRepoDealAccTreatMapper.queryFinDealPledge(
                    dealNow.getFinprodId(), DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue(),DictCodeListExt1.金融产品类型_债券买断式正回购.getValue()
                    , dealNow.getMpFinprodId());
                if (finDealPledge == null) {
                    continue;
                }
                // 定义 变量.金融产品代码 = 质押品信息（交易类金融产品）.金融产品类型
                String finprodType = finDealPledge.getFinprodType();
                // 定义 变量.利息金额
                BigDecimal intAmt = BigDecimal.ZERO;
                // 定义 变量.还本金额
                BigDecimal costAmt = BigDecimal.ZERO;
                // if （交易方向 = 债券派息）
                if (DictCodeList.交易方向_收息.getValue().equals(dealNow.getPs())) {
                    // *单位应计利息计算参考债券计提，应计利息算法（标的信息：质押金融产品代码 ，计算日：交易明细.到期日-1）。
                    // 查询标的信息
                    // 2.4.标的类金融产品信息
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
                        finDealPledge.getPledgeFinprodId(), queryCriteria);

                    // 变量.标的信息 = 上述共通返回标的信息
                    TargetFinProduct rstBaseinfo = (TargetFinProduct)targetSearchMap.get(
                        searchBaseinfo);

                    // 变量.付息列表 = 上述共通返回的计划列表
                    List<TargetCashPlan> listIntinfo = targetSearchMap.get(
                        searchIntinfo) == null ? null : (List<TargetCashPlan>)targetSearchMap.get(
                            searchIntinfo);

                    // 变量.还本列表 = 上述共通返回还本列表
                    // @SuppressWarnings("unchecked")
                    // List<TargetCashPlan> listRedinfo = targetSearchMap.get(searchRedinfo) ==
                    // null ? null
                    // : (List<TargetCashPlan>)targetSearchMap.get(searchRedinfo);

                    // 变量.报价列表 = 上述共通返回报价列表
                    List<TargetCashCalcRule> listCashinfo = targetSearchMap.get(
                        searchCashinfo) == null ? null : (List<TargetCashCalcRule>)targetSearchMap.get(
                            searchCashinfo);

                    // 变量.支付规则 = 上述共通返回支付规则列表
                    List<TargetCashPayRule> listPayinfo = targetSearchMap.get(
                        searchPayinfo) == null ? null : (List<TargetCashPayRule>)targetSearchMap.get(
                            searchPayinfo);
                    // 1.2 百元券面处理
                    BigDecimal varFaceValue = finProductCommonService.getFaceValue(
                        finDealPledge.getPledgeFinprodId(),
                        DateUtil.addDay(dealNow.getMdate(), -1));
                    // *单位应计利息计算参考债券计提，应计利息算法（标的信息：质押金融产品代码 ，计算日：交易明细.到期日-1）。
                    AccurIntCalOutParameter accurIntCalOutParameter = this.getCalculate(
                        rstBaseinfo, listIntinfo, listCashinfo, listPayinfo, varFaceValue,
                        DateUtil.addDay(dealNow.getMdate(), -1));
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
                    else {
                        // 调用金额共通方法设置有效小数位,小数位数取值共通参数 SEC_INT_SCALE_EXCHANGE
                        interest = ArithmeticUtil.round(interest,
                            Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam(
                                "SEC_INT_SCALE_EXCHANGE")) ? "8" : CacheUtil.getSysParam(
                                    "SEC_INT_SCALE_EXCHANGE")),
                            null);
                    }
                    // 变量.本日应计利息余额 = 交易明细.交易份额质押数量 * 单位应计利息 * 变量.百元券面面值 /100
                    intAmt = ArithmeticUtil.multiply(interest, dealNow.getShareAmt());
                    intAmt = ArithmeticUtil.multiply(intAmt, varFaceValue);
                    intAmt = ArithmeticUtil.divide(intAmt, new BigDecimal(100),
                        NumberEnum.HIGH_PREC_NUM.getCode());
                }
                // if （交易方向 = 债券派本）
                if (DictCodeList.交易方向_还本.getValue().equals(dealNow.getPs())) {
                    // 定义 变量.还本金额 = 交易明细.交易份额 * 交易信息.单位全价
                    costAmt = ArithmeticUtil.multiply(NumberEnum.MONEY.getCode(), null,
                        dealNow.getShareAmt(), dealNow.getUnitFprice());
                }
                AccountDriver ad = new AccountDriver();// 凭证类型、账套代码、交易编号全部去掉了。
                ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
                // ad.setTableId(this.getTableId(dealNow.getPs(), dealNow.getFinprodType()));//
                // ad.会计场景代码 = 根据交易方向和金融产品类型区分

                // ad.会计场景代码="买断式逆回购/正回购-债券派息"
                if (DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue().equals(finprodType)) {
                    ad.setTableId(DictCodeListTableId.会计场景代码_买断式逆回购_期间债券还本派息.getValue());
                }
                else {
                    ad.setTableId(DictCodeListTableId.会计场景代码_买断式正回购_期间债券还本派息.getValue());
                }

                // 初始化定义 - 存放该笔记账的场景要素 map定义
                Map<String, Object> accountSceneMap = new HashMap<>();
                // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值)
                accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
                // 金融产品类型+交易方向+"交易登账"
                // 备注 ="债券买断式逆/正回购债券派息交易登账"
                if (DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue().equals(finprodType)) {
                    if (DictCodeList.交易方向_收息.getValue().equals(dealNow.getPs())) {
                        accountSceneMap.put(BookConstant.BOOK_REMARK, "债券买断式逆回购债券派息交易登账");
                    }
                    else if (DictCodeList.交易方向_还本.getValue().equals(dealNow.getPs())) {
                        accountSceneMap.put(BookConstant.BOOK_REMARK, "债券买断式逆回购债券派本交易登账");
                    }
                }
                else {
                    if (DictCodeList.交易方向_收息.getValue().equals(dealNow.getPs())) {
                        accountSceneMap.put(BookConstant.BOOK_REMARK, "债券买断式正回购债券派息交易登账");
                    }
                    else if (DictCodeList.交易方向_还本.getValue().equals(dealNow.getPs())) {
                        accountSceneMap.put(BookConstant.BOOK_REMARK, "债券买断式正回购债券派本交易登账");
                    }
                }
                
                // 分类1 <- 变量.交易信息.分类1
                accountSceneMap.put(BookConstant.TYPE_1, tradeInfoDto.getType1());
                // 分类2 <- 变量.交易信息.分类2
                accountSceneMap.put(BookConstant.TYPE_2, tradeInfoDto.getType2());
                // 交易场所 <- DTO.交易场所
                accountSceneMap.put(BookConstant.TRADE_MARKET, dealNow.getTradeMarket());
                
                accountSceneMap.put(BookConstant.IS_ACC_BALANCE,DictCodeList.是否_否.getValue());

                // 初始化定义 - 存放该笔记账的相关金额要素 map定义
                Map<String, BigDecimal> accountAmountMap = new HashMap<>();

                // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
                Map<String, Object> accountCurrencyMap = new HashMap<>();

                // 基础科目性质
                Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
                Map<String, Object> accountNatrueBranchMap = new HashMap<>();

                // 2.1 账务明细代码查询
                // 定义 变量.账务明细代码
                String varBokDetailId = "";
                // 定义 卖空账务明细代码 = 账务明细共通（回购交易明细.金融产品代码 ，回购交易明细.金融产品类型， '卖空'）
                BokDetailId valBokDetailId = new BokDetailId();
                valBokDetailId.setBusiId(dealNow.getFinprodId());
                valBokDetailId.setBusiDetailType(dealNow.getFinprodType());
                valBokDetailId.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
                String bokDetailIdSell = bookAccountCommonService.genMd5BokDetailId(
                    valBokDetailId);
                // IF 交易明细.金融产品类型 = “债券买断式逆回购”
                if (DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue().equals(finprodType)) {
                    // 变量.账务明细代码 = 卖空账务明细代码
                    varBokDetailId = bokDetailIdSell;
                }
                else {
                    // 定义返回的科目余额
                    SubjectBalance balance = null;
                    // 塞入要查询的基础科目性质
                    List<String> baseSubjectNatureList = null;
                    // 设值科目余额查询参数Dto
                    SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
                    // 交易类账务处理结果.账套代码
                    searchDto.setBooksetId(booksetIdNow);
                    // 交易类账务处理结果.账套代码
                    searchDto.setBusiId(dealNow.getFinprodId());
                    // 交易类账务处理结果.发生日
                    searchDto.setHappenDate(happenDateNow);
                    // 交易类账务处理结果.入账日
                    searchDto.setBookDate(bookDateNow);
                    // 批处理参数.任务优先级
                    searchDto.setTaskPri(taskPri);
                    // 交易类账务处理结果.分支序号
                    searchDto.setBranch(dealNow.getBranch());
                    if (DictCodeList.交易方向_收息.getValue().equals(dealNow.getPs())) {
                        // 其他应收款-利息
                        searchDto.setBaseSubjectNatureList(
                            Arrays.asList(DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue()));
                    }
                    else if (DictCodeList.交易方向_还本.getValue().equals(dealNow.getPs())) {
                        // 其他应收款-本金
                        searchDto.setBaseSubjectNatureList(
                            Arrays.asList(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue()));
                    }
                    // 调用公用方法：【科目余额查询-期待方向 】,获取科目余额信息
                    List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
                        searchDto, DictCodeList.余额方向_借方.getValue());
                    subjectBalanceList = subjectBalanceList.stream().filter(
                        item -> !StringUtil.equals(item.getBokDetailId(),
                            bokDetailIdSell)).collect(Collectors.toList());
                    if(CollectionUtils.isNotEmpty(subjectBalanceList)){
                        varBokDetailId = subjectBalanceList.get(0).getBokDetailId();
                    }
                }
                if (DictCodeList.交易方向_收息.getValue().equals(dealNow.getPs())) {
                    // 账务明细代码为账务明细代码表.账务明细代码，分支序号为交易.分支序号
                    accountSceneMap.put(BookConstant.BOK_DETAIL_ID2,
                        bookCommonLogic.getValBokDetailId(finDealPledge.getPledgeFinprodId(),
                            varBokDetailId, finDealPledge.getPledgeFinprodType(),null));
                    // 付息金额/其他应收利息 <- 变量.利息金额
                    accountAmountMap.put(BookConstant.OTHER_INT_AMT, intAmt);
                    // 基础科目性质
                    accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
                    accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue(),
                        accountNatrueBranchMap);
                    ad.setAccountNatrueMap(accountNatrueMap);
                }
                else if (DictCodeList.交易方向_还本.getValue().equals(dealNow.getPs())) {
                    // 账务明细代码为账务明细代码表.账务明细代码，分支序号为交易.分支序号
                    accountSceneMap.put(BookConstant.BOK_DETAIL_ID2,
                        bookCommonLogic.getValBokDetailId(finDealPledge.getPledgeFinprodId(),
                            varBokDetailId,
                            finDealPledge.getPledgeFinprodType(),null));
                    accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
                    accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(),
                        accountNatrueBranchMap);
                    ad.setAccountNatrueMap(accountNatrueMap);
                    // 还本金额/其他应收本金<- 变量.还本金额
                    accountAmountMap.put(BookConstant.OTHER_COST_AMT, costAmt);
                }
                // 逆回购金额元素为DEALAMT2
                if(DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue().equals(finprodType)){
                    // 交易金额 <- 其他应收利息 + 其他应收本金
                    accountAmountMap.put(BookConstant.DEALAMT2,
                        ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null,
                            accountAmountMap.get(BookConstant.OTHER_INT_AMT),
                            accountAmountMap.get(BookConstant.OTHER_COST_AMT)));
                    // 发生数量 <- 交易金额
                    accountSceneMap.put(BookConstant.HAPPEN_NUMBER,
                        accountAmountMap.get(BookConstant.DEALAMT2));
                    // 全局变量的更新
                    // 变量.成本科目余额 = 变量.成本科目余额 - 金额元素.交易金额
                    costBalance = costBalance.subtract(accountAmountMap.get(BookConstant.DEALAMT2));
                }
                // 正回购金额元素为DEALAMT
                else{
                    // 交易金额 <- 其他应收利息 + 其他应收本金
                    accountAmountMap.put(BookConstant.DEALAMT,
                        ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null,
                            accountAmountMap.get(BookConstant.OTHER_INT_AMT),
                            accountAmountMap.get(BookConstant.OTHER_COST_AMT)));
                    // 发生数量 <- 交易金额
                    accountSceneMap.put(BookConstant.HAPPEN_NUMBER,
                        accountAmountMap.get(BookConstant.DEALAMT));
                    // 全局变量的更新
                    // 变量.成本科目余额 = 变量.成本科目余额 - 金额元素.交易金额
                    costBalance = costBalance.subtract(accountAmountMap.get(BookConstant.DEALAMT));
                }

                // 记账金额元素币种信息塞值
                // 因为与记账金额元素map的key及数量一致，故采用循环
                for (String key : accountAmountMap.keySet()) {
                    accountCurrencyMap.put(key, dealNow.getCcy());// value值塞入当前交易币种 （当前为获取计提里面的原币）
                }

                // map塞入场景要素管理中
                ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
                ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
                ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

                /*** modify by weipeng on 20220317 传入辅助核算项 start **/
                ad.setSubjectMappingConfigDefault(dealNow.getSubjectMappingConfig());
                /*** modify by weipeng on 20220317 传入辅助核算项 end **/
                // add by weipeng on 20220407：买断式逆回购还本派息时，应收利息_证券科目记应收利息-融入券科目，不随回购本身记应收利息科目
                if(DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue().equals(finprodType)){
                    Map<String, SubjectMappingConfig> subjectMappingConfigMap = new HashMap<>();
                    // 增加应收利息-证券的辅助核算项，对应融入券的应收利息科目
                    SubjectMappingConfig mappingConfig = new SubjectMappingConfig();
                    mappingConfig.setSecType(DictCodeListExt1.证券品种_债券.getValue());
                    mappingConfig.setInvAim(DictCodeListExt1.投资目的_卖空.getValue());
                    subjectMappingConfigMap.put(DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue(), mappingConfig);
                    ad.setSubjectMappingConfigMap(subjectMappingConfigMap);
                }
                // 记账凭证生成
                BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, batchExecuteInfo);
                // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
                returnResult.getValBokBookdetailBatch().addAll(
                    resultNowNow.getValBokBookdetailBatch());
//            }
        }
        return returnResult;
    }

    /**
     * 调用公用方法：【科目余额查询 - 期待方向】，获取科目余额信息中的科目余额数据
     * 
     * @param booksetIdNow 账套代码
     * @param bokDetailId 账务明细代码
     * @param happenDateNow 发生日
     * @param bookDateNow 入账日
     * @param expBalanceDirect 期待余额方向
     * @param baseSubjectNature 基础科目性质
     * @param branch 分支号
     * @param taskPri 任务优先级
     * @return SubjectBalance 科目余额
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalance(String booksetIdNow, String bokDetailId,
                                             Date happenDateNow, Date bookDateNow,
                                             String expBalanceDirect, String baseSubjectNature,
                                             Long branch, Long taskPri) {
        // 定义返回的科目余额
        SubjectBalance balance = null;

        // 塞入要查询的基础科目性质
        List<String> baseSubjectNatureList = null;
        if (baseSubjectNature != null && baseSubjectNature.length() > 0) {
            baseSubjectNatureList = new ArrayList<String>();
            baseSubjectNatureList.add(baseSubjectNature);
        }

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetIdNow);
        searchDto.setBokDetailId(bokDetailId);
        searchDto.setHappenDate(happenDateNow);
        searchDto.setBookDate(bookDateNow);
        searchDto.setTaskPri(taskPri);
        searchDto.setBranch(branch);
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        // 调用公用方法：【科目余额查询-期待方向 】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDto, expBalanceDirect);

        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            balance = subjectBalanceList.get(0);
        }

        return balance;
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
     * 拆放同业交易，根据交易方向和金融产品类型，获取对应的会计场景代码。
     * 
     * @param ps 交易方向
     * @param finprodType 金融产品类型
     * @return String 会计场景代码
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unused")
    private String getTableId(String ps, String finprodType) {
        // 设置 产检场景代码
        String tableIdForReturn = null;

        // 根据金融产品类型，判断当前分录场景是【债券买断式逆回购】还是【债券买断式正回购】
        if (DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue().equals(finprodType)) {// 分录场景为【债券质押式逆回购】
            // 区分交易方向
            if (DictCodeList.交易方向_买入.getValue().equals(ps)) {// 交易方向 - 买入
                tableIdForReturn = DictCodeListTableId.会计场景代码_买断式逆回购_起息.getValue();// ad.会计场景代码="质押式逆回购-起息"
            }
            else {// 其他情况（卖出、到期）
                tableIdForReturn = DictCodeListTableId.会计场景代码_买断式逆回购_到期.getValue();// ad.会计场景代码="质押式逆回购-到期"
            }
        }
        else {// 分录场景为【债券买断式正回购】
              // 区分交易方向
            if (DictCodeList.交易方向_买入.getValue().equals(ps)) {// 交易方向 - 买入
                tableIdForReturn = DictCodeListTableId.会计场景代码_买断式正回购_起息.getValue();// ad.会计场景代码="质押式正回购-起息"
            }
            else {// 其他情况（卖出、到期）
                tableIdForReturn = DictCodeListTableId.会计场景代码_买断式正回购_到期.getValue();// ad.会计场景代码="质押式正回购-到期"

            }
        }

        return tableIdForReturn;
    }

    /**
     * 利息计提-计算百元面值的债券的利息
     * 
     * @param rstBaseinfo 标的信息
     * @param listIntinfo 付息计划列表
     * @param listCashinfo 报价信息列表
     * @param listPayinfo 支付规则
     * @param varFaceValue 百元面值
     * @param calDate 计算日
     * @return AccurIntCalOutParameter
     * @see
     * @since 1.0
     */
    private AccurIntCalOutParameter getCalculate(TargetFinProduct rstBaseinfo,
                                                 List<TargetCashPlan> listIntinfo,
                                                 List<TargetCashCalcRule> listCashinfo,
                                                 List<TargetCashPayRule> listPayinfo,
                                                 BigDecimal varFaceValue, Date calDate) {
        AccurIntCalInParameter parameter = new AccurIntCalInParameter();
        // 计算日
        // parameter.setCalDate(BasicUtil.dateToLocal(addDate(acctEntity.getHappenDate(), 1)));
        parameter.setCalDate(rstBaseinfo.getVdate().compareTo(calDate) > 0 ? BasicUtil.dateToLocal(
            rstBaseinfo.getVdate()) : BasicUtil.dateToLocal(calDate));
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
        else {
            yearIntRate = IntFrequency.年付;
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

}
