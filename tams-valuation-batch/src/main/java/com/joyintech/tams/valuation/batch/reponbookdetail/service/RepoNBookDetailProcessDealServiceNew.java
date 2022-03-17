/*
 * 文件名：LendBookDetailProcessDealService.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0 修改人：wudebao 修改时间：2018年5月24日 修改内容：新建 系统名称：估值核算系统V1.0
 */
package com.joyintech.tams.valuation.batch.reponbookdetail.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.dao.TrdTradeFeeDetailMapper;
import com.joyintech.tams.common.base.entity.BokStatLossPosiiton;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetailKey;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.valuation.batch.common.accounting.entity.LossAmtInputDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accounting.service.LossCommonBatchService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.calculation.entity.DealTypesFeeDto;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.PledgeInfoDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TradeInfoDto;
import com.joyintech.tams.valuation.common.batch.losscommon.service.LossCommonService;
import com.joyintech.tams.valuation.common.batch.tradecommon.dto.ValTrdProductDealInfoDto;
import com.joyintech.tams.valuation.common.batch.tradecommon.service.ValTradeCommonService;
import com.joyintech.tams.valuation.common.constants.*;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 质押式回购交易账务处理 process逻辑处理
 *
 * @author leipeng
 * @version 1.0 2018年7月25日
 * @see RepoNBookDetailProcessDealServiceNew
 * @since 1.0
 */
@Service
@Scope("prototype")
public class RepoNBookDetailProcessDealServiceNew {

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
     * 计算类共通方法service
     */
    @Autowired
    private CalculationCommonService calculationCommonService;

    /**
     * 账务类共通方法service
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 标的类共通方法service
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    @Autowired
    private ValTradeCommonService valTradeCommonService;

    @Autowired
    private TrdTradeFeeDetailMapper trdTradeFeeDetailMapper;

    /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-质押式逆回购减值场景增加 start **/
    @Autowired
    private LossCommonService lossCommonService;

    @Autowired
    private LossCommonBatchService lossCommonBatchService;

    /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-质押式逆回购减值场景增加 end **/

    /**
     * process 数据处理
     *
     * @param param
     *            传递参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public BookGenerateResult execute(Map<String, Object> param) {
        // 获取reader返回值
        FinProductDealInfoResult acctEntity = (FinProductDealInfoResult)param.get(GlobalConstant.READER_RESULT);

        // 若reader传递的参数或交易明细为空，不进行后续处理
        if (acctEntity == null || acctEntity.getListFinProductDealInfoComDTO() == null
            || acctEntity.getListFinProductDealInfoComDTO().isEmpty()) {
            return null;
        }

        // 定义此process的返回变量
        BookGenerateResult rtnResult = new BookGenerateResult();

        // 账务明细若为空赋初始值
        if (rtnResult.getValBokBookdetailBatch() == null) {
            rtnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }

        // 获取待处理任务信息
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param.get("BATCHINFO");

        // 获取从reader中拿到的值
        String booksetIdNow = acctEntity.getBooksetId();// 账套代码
        String bokDetailIdNow = acctEntity.getBokDetailId();// 账务明细代码
        // String invAimNow = acctEntity.getInvAim();// 投资目的
        TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();// 会计场景
        Date happenDateNow = acctEntity.getHappenDate();// 发生日期
        Date bookDateNow = acctEntity.getBookDate();// 入账日期
        List<FinProductDealInfoComDTO> listFinProductDealInfoComDTONow = acctEntity.getListFinProductDealInfoComDTO(); // 交易基础信息列表
        Long taskPri = listFinProductDealInfoComDTONow.get(0).getTaskPri();// 任务优先级（交易基础信息列表的任务优先级都是一致的，故取第一个）
        // 金融产品代码
        String finprodIdNow = listFinProductDealInfoComDTONow.get(0).getFinprodId();
        String finprodTypeNow = listFinProductDealInfoComDTONow.get(0).getFinprodType();// 金融产品类型

        // 定义变量
        BigDecimal glb_subjectAmt = BigDecimal.ZERO;// 变量.成本科目余额
        BigDecimal glb_subjectNum = BigDecimal.ZERO;// 变量.成本科目数量
        BigDecimal glb_subjectProfit = BigDecimal.ZERO;// 变量.应计利息科目余额

        // 2.调用共通查询发生日最新余额

        // 交易期待方向——借方（默认）
        String expBalanceDirect = DictCodeList.余额方向_借方.getValue();
        // 基础科目性质 : 成本
        String baseSubjectNatureCB = DictCodeBaseSubjectNatrueNew.买入返售金融资产_成本.getValue();
        String baseSubjectNatureYjlx = DictCodeBaseSubjectNatrueNew.买入返售金融资产_应计利息.getValue();
        // 若为正回购，则设置为贷方
        if (finprodTypeNow.equals(DictCodeListExt1.金融产品类型_债券质押式正回购.getValue())) {
            expBalanceDirect = DictCodeList.余额方向_贷方.getValue();
            baseSubjectNatureCB = DictCodeBaseSubjectNatrueNew.卖出回购金融资产_成本.getValue();
            baseSubjectNatureYjlx = DictCodeBaseSubjectNatrueNew.卖出回购金融资产_应计利息.getValue();
        }

        // 2.1.成本类科目余额
        SubjectBalance subjectBalanceForAmt = this.getSubjectBalance(booksetIdNow, bokDetailIdNow, happenDateNow,
            bookDateNow, expBalanceDirect, baseSubjectNatureCB, taskPri);
        if (subjectBalanceForAmt != null) {
            glb_subjectAmt =
                subjectBalanceForAmt.getBalance() == null ? BigDecimal.ZERO : subjectBalanceForAmt.getBalance();// 变量.成本科目余额
                                                                                                                // =
                                                                                                                // DTO.科目余额
            glb_subjectNum = subjectBalanceForAmt.getNum() == null ? BigDecimal.ZERO : subjectBalanceForAmt.getNum();// 变量.成本科目数量
                                                                                                                     // =
                                                                                                                     // DTO.数量
        }

        // 2.2.应计利息类科目余额
        SubjectBalance subjectBalanceForfit = this.getSubjectBalance(booksetIdNow, bokDetailIdNow, happenDateNow,
            bookDateNow, expBalanceDirect, baseSubjectNatureYjlx, taskPri);
        if (subjectBalanceForfit != null) {
            glb_subjectProfit =
                subjectBalanceForfit.getBalance() == null ? BigDecimal.ZERO : subjectBalanceForfit.getBalance();// 变量.应计利息科目余额
                                                                                                                // =
                                                                                                                // DTO.科目余额
        }

        /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-质押式逆回购减值场景增加 start **/
        // 定义 变量.统计分析明细表（资产减值）减值使用列表
        if (rtnResult.getBokStatLossPosiitonList() == null) {
            rtnResult.setBokStatLossPosiitonList(new ArrayList<BokStatLossPosiiton>());
        }
        // 2.5.减值准备类科目余额
        BigDecimal glb_subjectjz = BigDecimal.ZERO;// 减值准备类科目余额
        // 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
        SubjectBalance jzBalance = this.getSubjectBalance(booksetIdNow, bokDetailIdNow, happenDateNow, bookDateNow,
            DictCodeList.余额方向_借方.getValue(), DictCodeBaseSubjectNatrueNew.买入返售金融资产_减值准备.getValue(), taskPri);
        if (jzBalance != null) {
            // 变量.减值准备类科目余额 = DTO.科目余额
            glb_subjectjz = jzBalance.getBalance() == null ? BigDecimal.ZERO : jzBalance.getBalance();
        }
        // 定义 汇率 = 1
        BigDecimal 汇率 = BigDecimal.ONE;
        /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-质押式逆回购减值场景增加 end **/

        // 2.3.质押式回购费用处理方式
        List<String> paramCodeList = new ArrayList<>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_银行间质押式回购费用处理方式.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_交易所质押式回购费用处理方式.getValue());
        // 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->账套参数查询
        Map<String, Object> booksetMap = accountingCommonService.getValBokBooksetExtForMap(booksetIdNow, paramCodeList);
        // 定义 【变量.银行间费用处理方式】
        String bankFeeType = booksetMap.get(ValDictCodeList.账套配置表参数项_银行间质押式回购费用处理方式.getValue()).toString();
        // 定义 【变量.交易所费用处理方式】
        String dealFeeType = booksetMap.get(ValDictCodeList.账套配置表参数项_交易所质押式回购费用处理方式.getValue()).toString();

        // 质押品信息查询
        // edit by chengzhong for 回购交易登记托管机构取质押品的登记托管机构，而不是交易本身，2019-10-30 start
        // List<TradeInfoDto> pledgeInfoDtoList = finProductCommonService.
        // getTradeInfoDtoList(finprodIdNow, finprodTypeNow, BookConstant.DEFALUT_BRANCH);
        List<PledgeInfoDto> pledgeInfoDtoList = finProductCommonService.getPledgeInfo(finprodIdNow, finprodTypeNow);
        // edit by chengzhong for 回购交易登记托管机构取质押品的登记托管机构，而不是交易本身，2019-10-30 end
        // 定义 变量.登记托管机构
        String registOrg = null;
        if (pledgeInfoDtoList != null && pledgeInfoDtoList.size() > 0) {
            registOrg = pledgeInfoDtoList.get(0).getRegistOrg();
        }

        // 循环 交易基础信息列表 进行逻辑处理
        for (FinProductDealInfoComDTO dealDto : listFinProductDealInfoComDTONow) {

            // 变量 - 交易信息（标的交易信息）
            TradeInfoDto dealInfoForTarget =
                this.getTargetInfo(dealDto.getFinprodId(), dealDto.getFinprodType(), dealDto.getBranch());
            // 保证获取的标的交易信息不为空
            if (dealInfoForTarget == null) {
                dealInfoForTarget = new TradeInfoDto();
            }

            // 组装记账元素
            // 初始化定义 - 记账场景要素管理
            AccountDriver ad = new AccountDriver();

            // 设置账务引擎参数
            ad.setTreeNode(bookSetTreeNodeNow);// 塞入记账场景树节点 = 会计场景
            ad.setTableId(this.getTableId(dealDto.getPs(), dealDto.getFinprodType()));// ad.会计场景代码 = 根据交易方向和金融产品类型区分

            // 初始化定义 - 存放该笔记账的场景要素 map定义
            Map<String, Object> accountSceneMap = new HashMap<>();
            // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值)
            accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealDto);
            accountSceneMap.put("TRADE_MARKET", dealDto.getTradeMarket());

            // 金融产品类型+交易方向+"交易登账"
            String finprodTypeName = DictCodeListExt1.from(DictCode.金融产品类型, dealDto.getFinprodType()).getCodeName();
            /** modify by yanya on 20191122 交易方向从缓存中取数据字典 **/
            // String psName = DictCodeList.from(DictCode.交易方向, dealDto.getPs()).getCodeName();
            String psName = SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealDto.getPs());
            accountSceneMap.put(BookConstant.BOOK_REMARK, finprodTypeName + psName + "交易登账");

            accountSceneMap.put(BookConstant.TYPE_1, dealInfoForTarget.getType1());// 分类1 - 变量.交易信息.分类1
            accountSceneMap.put(BookConstant.TYPE_2, dealInfoForTarget.getType2());// 分类2 - 变量.交易信息.分类2
            // 登记托管机构 <- 变量.登记托管机构
            accountSceneMap.put(BookConstant.REGIST_ORG, registOrg);

            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountMap = new HashMap<>();
            // 定义 变量.交易各种费用 = 上述返回值
            DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
                dealDto.getValTradeFeeDetailList(), dealDto.getProdId(), dealDto.getPortfolioId());

            accountAmountMap.put(BookConstant.STAMPTAX, dealTypesFeeDto.getStampDuty());// 变量.交易各种费用.印花税
            accountAmountMap.put(BookConstant.HANDFEE, dealTypesFeeDto.getBrokerage());// 变量.交易各种费用.经手费
            accountAmountMap.put(BookConstant.CERTIFEE, dealTypesFeeDto.getCertificateFee());// 变量.交易各种费用.证管费
            accountAmountMap.put(BookConstant.SETTLEFEE, dealTypesFeeDto.getClearingFee());// 变量.交易各种费用.结算费
            accountAmountMap.put(BookConstant.COMMISSION, dealTypesFeeDto.getCommission());// 变量.交易各种费用.佣金
            accountAmountMap.put(BookConstant.TRADEFEE, dealTypesFeeDto.getTransactionFee());// 变量.交易各种费用.交易手续费
            accountAmountMap.put(BookConstant.SETTLEMENTFEE, dealTypesFeeDto.getSettlementFee());// 变量.交易各种费用.结算手续费

            // 场景元素设值各种费用的账务明细代码
            // 印花税账务明细代码 <- 变量.交易各种费用.印花税账务明细代码
            if (dealTypesFeeDto.getStampDutyCode() != null)
                accountSceneMap.put(BookConstant.BOK_DETAIL_STAMPTAX, dealTypesFeeDto.getStampDutyCode());
            // 经手费账务明细代码 <- 变量.交易各种费用.经手费账务明细代码
            if (dealTypesFeeDto.getBrokerageCode() != null)
                accountSceneMap.put(BookConstant.BOK_DETAIL_HANDFEE, dealTypesFeeDto.getBrokerageCode());
            // 证管费账务明细代码 <- 变量.交易各种费用.证管费账务明细代码
            if (dealTypesFeeDto.getCertificateFeeCode() != null)
                accountSceneMap.put(BookConstant.BOK_DETAIL_CERTIFEE, dealTypesFeeDto.getCertificateFeeCode());
            // 结算费账务明细代码 <- 变量.交易各种费用.结算费账务明细代码
            if (dealTypesFeeDto.getClearingFeeCode() != null)
                accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEFEE, dealTypesFeeDto.getClearingFeeCode());
            // 佣金账务明细代码 <- 变量.交易各种费用.佣金账务明细代码
            if (dealTypesFeeDto.getCommissionCode() != null)
                accountSceneMap.put(BookConstant.BOK_DETAIL_COMMISSION, dealTypesFeeDto.getCommissionCode());
            // 交易手续费账务明细代码 <- 变量.交易各种费用.交易手续费账务明细代码
            if (dealTypesFeeDto.getTransactionFeeCode() != null)
                accountSceneMap.put(BookConstant.BOK_DETAIL_TRADEFEE, dealTypesFeeDto.getTransactionFeeCode());
            // 结算手续费账务明细代码 <- 变量.交易各种费用.结算手续费账务明细代码
            if (dealTypesFeeDto.getSettlementFeeCode() != null)
                accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEMENTFEE, dealTypesFeeDto.getSettlementFeeCode());

            BigDecimal repo = BigDecimal.ZERO;
            BigDecimal feeAmt = dealTypesFeeDto.getFeeAmt() != null ? dealTypesFeeDto.getFeeAmt() : BigDecimal.ZERO;
            // 交易明细.金融产品类型 = “债券质押式逆回购”
            if (dealDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue())) {
                repo = ArithmeticUtil.doAdd(dealDto.getPrinAmt(), feeAmt);// 交易明细.交易本金 + 变量.交易各种费用.交易费用
            } else { // 交易明细.金融产品类型 = “债券质押式正回购”
                repo = ArithmeticUtil.subtract(dealDto.getPrinAmt(), feeAmt);// 交易明细.交易本金 - 变量.交易各种费用.交易费用
            }

            /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-质押式逆回购减值场景增加 start **/
            // 定义 变量.冲减减值金额 = 0
            BigDecimal 冲减减值金额 = BigDecimal.ZERO;
            /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-质押式逆回购减值场景增加 end **/
            if (DictCodeList.交易方向_买入.getValue().equals(dealDto.getPs())) {// 交易方向 - 买入

                // 定义【变量.费用处理方式】
                String feeTypeNow = "";
                // 根据 交易明细.交易场所IN （“上海证券交易所”，“深圳证券交易所”） 获取费用处理方式
                if (DictCodeList.交易流通场所_上海证券交易所.getValue().equals(dealDto.getTradeMarket())
                    || DictCodeList.交易流通场所_深圳证券交易所.getValue().equals(dealDto.getTradeMarket())) {
                    // 变量.费用处理方式 = 变量.交易所费用处理方式
                    feeTypeNow = dealFeeType;
                    // 在途日期
                    Date onwayDate;
                    // 交易所回购计提天数方式
                    String seRepDayTypeNow = dealInfoForTarget.getSeRepDayType() == null
                        ? CacheUtil.getSysParam("SE_REP_DAY_TYPE") : dealInfoForTarget.getSeRepDayType();
                    if (seRepDayTypeNow != null
                        && seRepDayTypeNow.equals(DictCodeListExt1.交易所回购计提天数方式_交易天数.getValue())) {
                        // 在途日期 <- 交易明细.交易日 + 1
                        onwayDate = DateUtil.addDay(dealDto.getTradeDate(), 1);
                    } else {
                        // 在途日期 <- 交易明细.交易日
                        onwayDate = dealDto.getPayDate();
                    }
                    accountSceneMap.put(BookConstant.ONWAY_DATE, onwayDate);

                } else {
                    // 变量.费用处理方式 = 变量.银行间费用处理方式
                    feeTypeNow = bankFeeType;
                }

                // if（变量.费用处理方式 = "不计入成本"）
                if (feeTypeNow.equals(DictCodeList.回购费用处理方式_不计入成本.getValue())) {
                    accountAmountMap.put(BookConstant.COST, dealDto.getPrinAmt());// 交易明细.交易本金
                    // accountAmountMap.put(BookConstant.FEEAMT, dealTypesFeeDto.getFeeAmt());//变量.交易各种费用.交易费用

                    // 印花税支出 = 变量.交易各种费用.印花税+变量.交易各种费用.随交易支付_印花税
                    accountAmountMap.put(BookConstant.STAMPTAX_OUT,
                        ArithmeticUtil.doAdd(dealTypesFeeDto.getStampDuty(), dealTypesFeeDto.getStampDutyForDealPay()));
                    // 经手费支出 = 变量.交易各种费用.经手费+变量.交易各类费用.随交易支付_经手费
                    accountAmountMap.put(BookConstant.HANDFEE_OUT,
                        ArithmeticUtil.doAdd(dealTypesFeeDto.getBrokerage(), dealTypesFeeDto.getBrokerageForDealPay()));
                    // 证管费支出 = 变量.交易各种费用.证管费+变量.交易各种费用.随交易支付_证管费
                    accountAmountMap.put(BookConstant.CERTIFEE_OUT, ArithmeticUtil
                        .doAdd(dealTypesFeeDto.getCertificateFee(), dealTypesFeeDto.getCertificateFeeForDealPay()));
                    // 结算费支出 = 变量.交易各种费用.结算费+变量.交易各种费用.随交易支付_结算费
                    accountAmountMap.put(BookConstant.SETTLEFEE_OUT, ArithmeticUtil
                        .doAdd(dealTypesFeeDto.getClearingFee(), dealTypesFeeDto.getClearingFeeForDealPay()));
                    // 佣金支出 = 变量.交易各种费用.佣金+变量.交易各种费用.随交易支付_佣金
                    accountAmountMap.put(BookConstant.COMMISSION_OUT, ArithmeticUtil
                        .doAdd(dealTypesFeeDto.getCommission(), dealTypesFeeDto.getCommissionForDealPay()));
                    // 交易手续费支出 = 变量.交易各种费用.交易手续费+变量.交易各种费用.随交易支付_交易手续费
                    accountAmountMap.put(BookConstant.TRADEFEE_OUT, ArithmeticUtil
                        .doAdd(dealTypesFeeDto.getTransactionFee(), dealTypesFeeDto.getTransactionFeeForDealPay()));
                    // 结算手续费支出 = 变量.交易各种费用.结算手续费+变量.交易各种费用.随交易支付_结算手续费
                    accountAmountMap.put(BookConstant.SETTLEMENTFEE_OUT, ArithmeticUtil
                        .doAdd(dealTypesFeeDto.getSettlementFee(), dealTypesFeeDto.getSettlementFeeForDealPay()));

                    // 场景元素设值各种费用的账务明细代码
                    // 印花税账务明细代码 <- 变量.交易各种费用.印花税账务明细代码
                    if (dealTypesFeeDto.getStampDutyCodeForDealPay() != null)
                        accountSceneMap.put(BookConstant.BOK_DETAIL_STAMPTAX,
                            dealTypesFeeDto.getStampDutyCodeForDealPay());
                    // 经手费账务明细代码 <- 变量.交易各种费用.经手费账务明细代码
                    if (dealTypesFeeDto.getBrokerageCodeForDealPay() != null)
                        accountSceneMap.put(BookConstant.BOK_DETAIL_HANDFEE,
                            dealTypesFeeDto.getBrokerageCodeForDealPay());
                    // 证管费账务明细代码 <- 变量.交易各种费用.证管费账务明细代码
                    if (dealTypesFeeDto.getCertificateFeeCodeForDealPay() != null)
                        accountSceneMap.put(BookConstant.BOK_DETAIL_CERTIFEE,
                            dealTypesFeeDto.getCertificateFeeCodeForDealPay());
                    // 结算费账务明细代码 <- 变量.交易各种费用.结算费账务明细代码
                    if (dealTypesFeeDto.getClearingFeeCodeForDealPay() != null)
                        accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEFEE,
                            dealTypesFeeDto.getClearingFeeCodeForDealPay());
                    // 佣金账务明细代码 <- 变量.交易各种费用.佣金账务明细代码
                    if (dealTypesFeeDto.getCommissionCodeForDealPay() != null)
                        accountSceneMap.put(BookConstant.BOK_DETAIL_COMMISSION,
                            dealTypesFeeDto.getCommissionCodeForDealPay());
                    // 交易手续费账务明细代码 <- 变量.交易各种费用.交易手续费账务明细代码
                    if (dealTypesFeeDto.getTransactionFeeCodeForDealPay() != null)
                        accountSceneMap.put(BookConstant.BOK_DETAIL_TRADEFEE,
                            dealTypesFeeDto.getTransactionFeeCodeForDealPay());
                    // 结算手续费账务明细代码 <- 变量.交易各种费用.结算手续费账务明细代码
                    if (dealTypesFeeDto.getSettlementFeeCodeForDealPay() != null)
                        accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEMENTFEE,
                            dealTypesFeeDto.getSettlementFeeCodeForDealPay());

                } else {
                    accountAmountMap.put(BookConstant.COST, repo);// 逆回购:交易明细.交易本金 + 变量.交易各种费用.交易费用,正回购:交易明细.交易本金 -
                                                                  // 变量.交易各种费用.交易费用
                    accountAmountMap.put(BookConstant.FEEAMT, BigDecimal.ZERO);// 0
                }
                accountAmountMap.put(BookConstant.DEALAMT, dealDto.getTradeAmt());// 交易明细.交易金额
                accountAmountMap.put(BookConstant.INTAMT, dealDto.getIntAmt());// 交易明细.利息总额

                glb_subjectAmt = ArithmeticUtil.doAdd(glb_subjectAmt, dealDto.getPrinAmt());// 变量.成本科目余额 + 交易明细.交易本金
                glb_subjectNum = ArithmeticUtil.doAdd(glb_subjectNum, dealDto.getShareAmt());// 变量.成本科目数量 + 交易明细.交易份额
                glb_subjectProfit = ArithmeticUtil.doAdd(glb_subjectProfit, dealDto.getIntAmt());// 变量.应计利息科目余额 +
                                                                                                 // 交易明细.利息总额
            }
            /** modify by yanya on 20191111 增加 提前终止、逾期返售 **/
            else {
                // 卖出、到期、提前终止、逾期返售

                // 设置公用金额元素
                // 印花税支出 = 变量.交易各种费用.随交易支付_印花税+ 变量.交易各种费用.印花税
                accountAmountMap.put(BookConstant.STAMPTAX_OUT,
                    ArithmeticUtil.doAdd(dealTypesFeeDto.getStampDutyForDealPay(), dealTypesFeeDto.getStampDuty()));
                // 经手费支出 = 变量.交易各类费用.随交易支付_经手费+ 变量.交易各种费用.经手费
                accountAmountMap.put(BookConstant.HANDFEE_OUT,
                    ArithmeticUtil.doAdd(dealTypesFeeDto.getBrokerageForDealPay(), dealTypesFeeDto.getBrokerage()));
                // 证管费支出 = 变量.交易各种费用.随交易支付_证管费 + 变量.交易各种费用.证管费
                accountAmountMap.put(BookConstant.CERTIFEE_OUT, ArithmeticUtil
                    .doAdd(dealTypesFeeDto.getCertificateFeeForDealPay(), dealTypesFeeDto.getCertificateFee()));
                // 结算费支出 = 变量.交易各种费用.随交易支付_结算费+ 变量.交易各种费用.结算费
                accountAmountMap.put(BookConstant.SETTLEFEE_OUT,
                    ArithmeticUtil.doAdd(dealTypesFeeDto.getClearingFeeForDealPay(), dealTypesFeeDto.getClearingFee()));
                // 佣金支出 = 变量.交易各种费用.随交易支付_佣金+ 变量.交易各种费用.佣金
                accountAmountMap.put(BookConstant.COMMISSION_OUT,
                    ArithmeticUtil.doAdd(dealTypesFeeDto.getCommissionForDealPay(), dealTypesFeeDto.getCommission()));
                // 交易手续费支出 = 变量.交易各种费用.随交易支付_交易手续费 + 变量.交易各种费用.交易手续费
                accountAmountMap.put(BookConstant.TRADEFEE_OUT, ArithmeticUtil
                    .doAdd(dealTypesFeeDto.getTransactionFeeForDealPay(), dealTypesFeeDto.getTransactionFee()));
                // 结算手续费支出 = 变量.交易各种费用.随交易支付_结算手续费 + 变量.交易各种费用.结算手续费
                accountAmountMap.put(BookConstant.SETTLEMENTFEE_OUT, ArithmeticUtil
                    .doAdd(dealTypesFeeDto.getSettlementFeeForDealPay(), dealTypesFeeDto.getSettlementFee()));

                // 场景元素设值各种费用的账务明细代码
                // 印花税账务明细代码 <- 变量.交易各种费用.印花税账务明细代码
                if (dealTypesFeeDto.getStampDutyCodeForDealPay() != null)
                    accountSceneMap.put(BookConstant.BOK_DETAIL_STAMPTAX, dealTypesFeeDto.getStampDutyCodeForDealPay());
                // 经手费账务明细代码 <- 变量.交易各种费用.经手费账务明细代码
                if (dealTypesFeeDto.getBrokerageCodeForDealPay() != null)
                    accountSceneMap.put(BookConstant.BOK_DETAIL_HANDFEE, dealTypesFeeDto.getBrokerageCodeForDealPay());
                // 证管费账务明细代码 <- 变量.交易各种费用.证管费账务明细代码
                if (dealTypesFeeDto.getCertificateFeeCodeForDealPay() != null)
                    accountSceneMap.put(BookConstant.BOK_DETAIL_CERTIFEE,
                        dealTypesFeeDto.getCertificateFeeCodeForDealPay());
                // 结算费账务明细代码 <- 变量.交易各种费用.结算费账务明细代码
                if (dealTypesFeeDto.getClearingFeeCodeForDealPay() != null)
                    accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEFEE,
                        dealTypesFeeDto.getClearingFeeCodeForDealPay());
                // 佣金账务明细代码 <- 变量.交易各种费用.佣金账务明细代码
                if (dealTypesFeeDto.getCommissionCodeForDealPay() != null)
                    accountSceneMap.put(BookConstant.BOK_DETAIL_COMMISSION,
                        dealTypesFeeDto.getCommissionCodeForDealPay());
                // 交易手续费账务明细代码 <- 变量.交易各种费用.交易手续费账务明细代码
                if (dealTypesFeeDto.getTransactionFeeCodeForDealPay() != null)
                    accountSceneMap.put(BookConstant.BOK_DETAIL_TRADEFEE,
                        dealTypesFeeDto.getTransactionFeeCodeForDealPay());
                // 结算手续费账务明细代码 <- 变量.交易各种费用.结算手续费账务明细代码
                if (dealTypesFeeDto.getSettlementFeeCodeForDealPay() != null)
                    accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEMENTFEE,
                        dealTypesFeeDto.getSettlementFeeCodeForDealPay());
                // 变量.调整金额
                BigDecimal adjustAmt = BigDecimal.ZERO;
                // 根据 交易明细.交易场所IN （“上海证券交易所”，“深圳证券交易所”） 获取费用处理方式
                if (DictCodeList.交易流通场所_上海证券交易所.getValue().equals(dealDto.getTradeMarket())
                    || DictCodeList.交易流通场所_深圳证券交易所.getValue().equals(dealDto.getTradeMarket())) {
                    // 变量.费用处理方式 = 变量.交易所费用处理方式
                    // 在途日期
                    Date onwayDate;

                    // 交易所回购计提天数方式
                    String seRepDayTypeNow = dealInfoForTarget.getSeRepDayType() == null
                        ? CacheUtil.getSysParam("SE_REP_DAY_TYPE") : dealInfoForTarget.getSeRepDayType();
                    if (seRepDayTypeNow != null
                        && seRepDayTypeNow.equals(DictCodeListExt1.交易所回购计提天数方式_交易天数.getValue())) {

                        // 在途日期 <- 交易明细.交易日 + 1
                        onwayDate = DateUtil.addDay(dealDto.getTradeDate(), 1);
                    } else {
                        // 在途日期 <- 交易明细.交易日
                        onwayDate = dealDto.getPayDate();
                    }
                    accountSceneMap.put(BookConstant.ONWAY_DATE, onwayDate);
                    // 变量.应计利息
                    BigDecimal intAmt = BigDecimal.ZERO;

                    /** add by wujie on 20210528 start remark:临时处理 **/
                    intAmt = dealDto.getIntAmt();

                    if (dealFeeType.equals(DictCodeList.回购费用处理方式_计入成本.getValue())) {
                        Map<String, Object> paramsMap = new HashMap<String, Object>();
                        paramsMap.put("finprodId", dealDto.getFinprodId());
                        paramsMap.put("finprodType", dealDto.getFinprodType());
                        paramsMap.put("busiType", dealDto.getBusiType());
                        paramsMap.put("booksetId", booksetIdNow);
                        paramsMap.put("ps", DictCodeList.交易方向_买入.getValue());

                        List<ValTrdProductDealInfoDto> dealList = valTradeCommonService.findEntryTradeInfo(paramsMap);
                        if (CollectionUtils.isNotEmpty(dealList)) {
                            TrdTradeFeeDetailKey trdTradeFeeDetailkey = new TrdTradeFeeDetailKey();
                            trdTradeFeeDetailkey.setTradeId(dealDto.getFinprodId());
                            trdTradeFeeDetailkey.setFeeType(DictCodeListExt3.费用类型_佣金.getValue());
                            TrdTradeFeeDetail trdTradeFeeDetail =
                                trdTradeFeeDetailMapper.selectByPrimaryKey(trdTradeFeeDetailkey);
                            if (trdTradeFeeDetail != null) {
                                intAmt = ArithmeticUtil.doAdd(intAmt, trdTradeFeeDetail.getFeeAmt());
                            }
                        }
                    }
                    if (dealInfoForTarget.getMdate().compareTo(dealDto.getTradeDate()) > 0) {
                        intAmt = glb_subjectProfit;
                    }
                    accountAmountMap.put(BookConstant.INTAMT, intAmt);// 应计利息<- 交易.理论应计利息总额
                    /** add by wujie on 20210528 end **/

                    if (dealDto.getPs().equals(DictCodeList.交易方向_到期.getValue())
                        || DictCodeListExt3.交易方向_投资端_提前终止.getValue().equals(dealDto.getPs())
                        || DictCodeListExt3.交易方向_投资端_逾期返售.getValue().equals(dealDto.getPs())) {
                        accountAmountMap.put(BookConstant.COST, glb_subjectAmt);// 回购本金 <- 变量.成本科目余额
                        accountAmountMap.put(BookConstant.DEALAMT, dealDto.getTradeAmt());// 到期/转出金额<-交易明细.交易金额

                        /** add by wujie on 20210528 start remark:临时处理 **/
                        intAmt = ArithmeticUtil.doSub(dealDto.getTradeAmt(), glb_subjectAmt, intAmt);
                        /** add by wujie on 20210528 end **/
                        // 变量.应计利息 = 金额元素.到期/转出金额 - 金额元素.回购本金
                        if (dealDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue())) {
                            /**
                             * modify by wujie on 20210528 remark:临时处理 intAmt =
                             * ArithmeticUtil.doSub(dealDto.getTradeAmt(), glb_subjectAmt);
                             **/
                            intAmt = ArithmeticUtil.doSub(intAmt, accountAmountMap.get(BookConstant.STAMPTAX),
                                accountAmountMap.get(BookConstant.HANDFEE), accountAmountMap.get(BookConstant.CERTIFEE),
                                accountAmountMap.get(BookConstant.SETTLEFEE),
                                accountAmountMap.get(BookConstant.COMMISSION),
                                accountAmountMap.get(BookConstant.TRADEFEE),
                                accountAmountMap.get(BookConstant.SETTLEMENTFEE));
                            intAmt = ArithmeticUtil.doAdd(intAmt, accountAmountMap.get(BookConstant.STAMPTAX_OUT),
                                accountAmountMap.get(BookConstant.HANDFEE_OUT),
                                accountAmountMap.get(BookConstant.CERTIFEE_OUT),
                                accountAmountMap.get(BookConstant.SETTLEFEE_OUT),
                                accountAmountMap.get(BookConstant.COMMISSION_OUT),
                                accountAmountMap.get(BookConstant.TRADEFEE_OUT),
                                accountAmountMap.get(BookConstant.SETTLEMENTFEE_OUT));
                        } else {
                            /**
                             * modify by wujie on 20210528 start remark:临时处理 intAmt =
                             * ArithmeticUtil.doSub(dealDto.getTradeAmt(), glb_subjectAmt);
                             **/
                            intAmt = ArithmeticUtil.doAdd(intAmt, accountAmountMap.get(BookConstant.STAMPTAX),
                                accountAmountMap.get(BookConstant.HANDFEE), accountAmountMap.get(BookConstant.CERTIFEE),
                                accountAmountMap.get(BookConstant.SETTLEFEE),
                                accountAmountMap.get(BookConstant.COMMISSION),
                                accountAmountMap.get(BookConstant.TRADEFEE),
                                accountAmountMap.get(BookConstant.SETTLEMENTFEE));
                            intAmt = ArithmeticUtil.doSub(intAmt, accountAmountMap.get(BookConstant.STAMPTAX_OUT),
                                accountAmountMap.get(BookConstant.HANDFEE_OUT),
                                accountAmountMap.get(BookConstant.CERTIFEE_OUT),
                                accountAmountMap.get(BookConstant.SETTLEFEE_OUT),
                                accountAmountMap.get(BookConstant.COMMISSION_OUT),
                                accountAmountMap.get(BookConstant.TRADEFEE_OUT),
                                accountAmountMap.get(BookConstant.SETTLEMENTFEE_OUT));
                        }
                        /** add by wujie on 20210528 start remark:临时处理 **/
                        adjustAmt = intAmt;
                        /** add by wujie on 20210528 end remark:临时处理 **/

                    } else {
                        accountAmountMap.put(BookConstant.DEALAMT, dealDto.getTradeAmt());// 交易明细.交易金额
                        // 变量.应计利息 = 金额元素.到期/转出金额 - 金额元素.回购本金
                        if (dealDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue())) {
                            intAmt = ArithmeticUtil.doSub(dealDto.getTradeAmt(), glb_subjectAmt);
                            intAmt = ArithmeticUtil.doAdd(intAmt, accountAmountMap.get(BookConstant.STAMPTAX),
                                accountAmountMap.get(BookConstant.HANDFEE), accountAmountMap.get(BookConstant.CERTIFEE),
                                accountAmountMap.get(BookConstant.SETTLEFEE),
                                accountAmountMap.get(BookConstant.COMMISSION),
                                accountAmountMap.get(BookConstant.TRADEFEE),
                                accountAmountMap.get(BookConstant.SETTLEMENTFEE));
                            intAmt = ArithmeticUtil.doSub(intAmt, accountAmountMap.get(BookConstant.STAMPTAX_OUT),
                                accountAmountMap.get(BookConstant.HANDFEE_OUT),
                                accountAmountMap.get(BookConstant.CERTIFEE_OUT),
                                accountAmountMap.get(BookConstant.SETTLEFEE_OUT),
                                accountAmountMap.get(BookConstant.COMMISSION_OUT),
                                accountAmountMap.get(BookConstant.TRADEFEE_OUT),
                                accountAmountMap.get(BookConstant.SETTLEMENTFEE_OUT));
                        } else {
                            intAmt = ArithmeticUtil.doSub(dealDto.getTradeAmt(), glb_subjectAmt);
                            intAmt = ArithmeticUtil.doSub(intAmt, accountAmountMap.get(BookConstant.STAMPTAX),
                                accountAmountMap.get(BookConstant.HANDFEE), accountAmountMap.get(BookConstant.CERTIFEE),
                                accountAmountMap.get(BookConstant.SETTLEFEE),
                                accountAmountMap.get(BookConstant.COMMISSION),
                                accountAmountMap.get(BookConstant.TRADEFEE),
                                accountAmountMap.get(BookConstant.SETTLEMENTFEE));
                            intAmt = ArithmeticUtil.doAdd(intAmt, accountAmountMap.get(BookConstant.STAMPTAX_OUT),
                                accountAmountMap.get(BookConstant.HANDFEE_OUT),
                                accountAmountMap.get(BookConstant.CERTIFEE_OUT),
                                accountAmountMap.get(BookConstant.SETTLEFEE_OUT),
                                accountAmountMap.get(BookConstant.COMMISSION_OUT),
                                accountAmountMap.get(BookConstant.TRADEFEE_OUT),
                                accountAmountMap.get(BookConstant.SETTLEMENTFEE_OUT));
                        }
                        /** add by wujie on 20210528 start remark:临时处理 **/
                        accountAmountMap.put(BookConstant.INTAMT, intAmt);// 应计利息<- 交易明细.利息总额
                        /** add by wujie on 20210528 end **/
                    }
                    /**
                     * add by wujie on 20210528 remark:临时处理 accountAmountMap.put(BookConstant.INTAMT, intAmt);//应计利息<-
                     * 交易明细.利息总额
                     **/
                } else {
                    if (DictCodeList.交易方向_到期.getValue().equals(dealDto.getPs())
                        || DictCodeListExt3.交易方向_投资端_提前终止.getValue().equals(dealDto.getPs())
                        || DictCodeListExt3.交易方向_投资端_逾期返售.getValue().equals(dealDto.getPs())) {
                        accountAmountMap.put(BookConstant.COST, glb_subjectAmt);// 回购本金 <- 变量.成本科目余额
                        accountAmountMap.put(BookConstant.INTAMT, glb_subjectProfit);// 应计利息<- 变量.应计利息科目余额
                        accountAmountMap.put(BookConstant.DEALAMT, dealDto.getTradeAmt());// 到期/转出金额<-交易明细.交易金额

                        if (dealDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue())) {
                            // 变量.调整金额 = 金额元素.到期/转出金额 - 金额元素.回购本金 - 金额元素.应计利息
                            adjustAmt = ArithmeticUtil.doSub(dealDto.getTradeAmt(), glb_subjectAmt, glb_subjectProfit);
                            adjustAmt = ArithmeticUtil.doSub(adjustAmt, accountAmountMap.get(BookConstant.STAMPTAX),
                                accountAmountMap.get(BookConstant.HANDFEE), accountAmountMap.get(BookConstant.CERTIFEE),
                                accountAmountMap.get(BookConstant.SETTLEFEE),
                                accountAmountMap.get(BookConstant.COMMISSION),
                                accountAmountMap.get(BookConstant.TRADEFEE),
                                accountAmountMap.get(BookConstant.SETTLEMENTFEE));
                            adjustAmt = ArithmeticUtil.doAdd(adjustAmt, accountAmountMap.get(BookConstant.STAMPTAX_OUT),
                                accountAmountMap.get(BookConstant.HANDFEE_OUT),
                                accountAmountMap.get(BookConstant.CERTIFEE_OUT),
                                accountAmountMap.get(BookConstant.SETTLEFEE_OUT),
                                accountAmountMap.get(BookConstant.COMMISSION_OUT),
                                accountAmountMap.get(BookConstant.TRADEFEE_OUT),
                                accountAmountMap.get(BookConstant.SETTLEMENTFEE_OUT));
                        } else {
                            // 变量.调整金额 = 金额元素.到期/转出金额 - 金额元素.回购本金 - 金额元素.应计利息
                            adjustAmt = ArithmeticUtil.doSub(dealDto.getTradeAmt(), glb_subjectAmt, glb_subjectProfit);
                            adjustAmt = ArithmeticUtil.doAdd(adjustAmt, accountAmountMap.get(BookConstant.STAMPTAX),
                                accountAmountMap.get(BookConstant.HANDFEE), accountAmountMap.get(BookConstant.CERTIFEE),
                                accountAmountMap.get(BookConstant.SETTLEFEE),
                                accountAmountMap.get(BookConstant.COMMISSION),
                                accountAmountMap.get(BookConstant.TRADEFEE),
                                accountAmountMap.get(BookConstant.SETTLEMENTFEE));
                            adjustAmt = ArithmeticUtil.doSub(adjustAmt, accountAmountMap.get(BookConstant.STAMPTAX_OUT),
                                accountAmountMap.get(BookConstant.HANDFEE_OUT),
                                accountAmountMap.get(BookConstant.CERTIFEE_OUT),
                                accountAmountMap.get(BookConstant.SETTLEFEE_OUT),
                                accountAmountMap.get(BookConstant.COMMISSION_OUT),
                                accountAmountMap.get(BookConstant.TRADEFEE_OUT),
                                accountAmountMap.get(BookConstant.SETTLEMENTFEE_OUT));
                        }

                    } else {
                        accountAmountMap.put(BookConstant.COST, dealDto.getPrinAmt());// 回购本金 交易明细.交易本金

                        accountAmountMap.put(BookConstant.DEALAMT, dealDto.getTradeAmt());// 交易明细.交易金额
                        // 变量.应计利息科目余额 * 交易份额/变量.成本科目数量
                        /*保留两位小数 ----wanggengyun 2018-08-29*/
                        BigDecimal intAmt = ArithmeticUtil
                            .divide(ArithmeticUtil.multiply(glb_subjectProfit, dealDto.getShareAmt()), glb_subjectNum);
                        // 按照金额小数位处理方式，处理小数位
                        intAmt = ArithmeticUtil.round(intAmt, NumberEnum.MONEY);
                        // 塞值
                        accountAmountMap.put(BookConstant.INTAMT, intAmt);

                        // 变量.调整金额 = 交易明细.应计利息总额 - 金额元素.应计利息
                        adjustAmt = ArithmeticUtil.subtract(dealDto.getIntAmt(), intAmt);
                    }

                }

                if (adjustAmt.compareTo(BigDecimal.ZERO) >= 0) {// 调整金额_贷 <- 变量.调整金额

                    if (dealDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue())) {
                        accountAmountMap.put(BookConstant.INCOMEAMT_C, adjustAmt);
                    } else {
                        accountAmountMap.put(BookConstant.INCOMEAMT_D, adjustAmt);
                    }

                } else {// 调整金额_借 <- 变量.调整金额 * -1
                    if (dealDto.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue())) {
                        accountAmountMap.put(BookConstant.INCOMEAMT_D, adjustAmt.multiply(new BigDecimal(-1)));
                    } else {
                        accountAmountMap.put(BookConstant.INCOMEAMT_C, adjustAmt.multiply(new BigDecimal(-1)));
                    }
                }

                /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-质押式逆回购减值场景增加 start **/ // 只有逆回购出减值
                if (StringUtil.equals(DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue(), dealDto.getFinprodType())) {
                    // IF 变量.减值准备科目余额 != 0
                    if (glb_subjectjz.compareTo(BigDecimal.ZERO) != 0) {
                        LossAmtInputDto inputdto = this.getLossAmtInputDto(acctEntity, dealDto, null,
                            // 本次发生额 = 金额元素.回购本金 + 金额元素.应计利息
                            ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.COST),
                                accountAmountMap.get(BookConstant.INTAMT)),
                            // 减值准备余额
                            glb_subjectjz,
                            // 当前EAD金额 = 成本科目余额 + 应计利息科目余额
                            ArithmeticUtil.doAdd(glb_subjectAmt, glb_subjectProfit),
                            // 应收款项余额
                            null,
                            // 总份额 = 变量.成本科目数量
                            glb_subjectNum,
                            // 发生份额 = 交易明细.交易份额
                            dealDto.getShareAmt());
                        冲减减值金额 = lossCommonBatchService.calcLossAmtForDeal(inputdto);
                    }
                    accountAmountMap.put(BookConstant.IMPAMT, 冲减减值金额.negate());
                }

                glb_subjectAmt = ArithmeticUtil.subtract(glb_subjectAmt, dealDto.getPrinAmt());// 变量.成本科目余额 - 交易明细.交易本金
                glb_subjectNum = ArithmeticUtil.subtract(glb_subjectNum, dealDto.getShareAmt());// 变量.成本科目数量 - 交易明细.交易份额
                glb_subjectProfit = ArithmeticUtil.subtract(glb_subjectProfit, dealDto.getIntAmt());// 变量.应计利息科目余额 -
                                                                                                    // 交易明细.利息总额

                glb_subjectjz = glb_subjectjz.subtract(冲减减值金额);// 变量.减值准备科目余额- 变量.冲减减值准备
                /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-质押式逆回购减值场景增加 end **/
            }

            // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
            Map<String, Object> accountCurrencyMap = new HashMap<>();
            for (Map.Entry<String, BigDecimal> entity : accountAmountMap.entrySet()) {
                accountCurrencyMap.put(entity.getKey(), dealDto.getCcy());
            }

            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
            ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
            ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素
            /*** modify by yinya on 20220211 传入辅助核算项 start **/
            ad.setSubjectMappingConfigDefault(dealDto.getSubjectMappingConfig());
            // 记账凭证生成
            BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, batchExecuteInfo);
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            rtnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());

            /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-质押式逆回购减值场景增加 start **/
            // if(金额元素.减值使用 != 0)
            if (冲减减值金额.compareTo(BigDecimal.ZERO) != 0) {
                BokStatLossPosiiton statLoss = null;
                for (ValBokBookdetailBatch detail : resultNowNow.getValBokBookdetailBatch()) {
                    // 变量.账务明细 = 调用账务明细生成共通返回结果中 基础科目性质 ="买入返售资产减值"的第一条数据
                    if (StringUtil.equals(DictCodeBaseSubjectNatrueNew.买入返售金融资产_减值准备.getValue(),
                        detail.getBaseSubjectNature())) {
                        // 调用【减值相关共通-减值使用明细数据生成】
                        statLoss = lossCommonService.setLossInfoUsed(detail, accountAmountMap.get(BookConstant.IMPAMT),
                            accountAmountMap.get(BookConstant.IMPAMT), glb_subjectjz,
                            ArithmeticUtil.multiply(glb_subjectjz, 汇率), dealDto.getTradeId());
                        break;
                    }
                }
                if (statLoss != null) {
                    // 变量.统计分析明细表（资产减值）减值使用列表.add(上述返回值)
                    rtnResult.getBokStatLossPosiitonList().add(statLoss);
                }
            }
            /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-质押式逆回购减值场景增加 end **/
        }

        return rtnResult;
    }

    private SubjectBalance getSubjectBalance(String booksetId, String bokDetailId, Date happenDate, Date bookDate,
        String expBalanceDirect, String baseSubjectNature, Long taskPri) {
        // 定义返回实体类
        SubjectBalance subjectBalance = null;

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
        List<SubjectBalance> subjectBalanceList =
            accountingCommonService.getSubjectBalanceExpectDirection(searchDto, expBalanceDirect);

        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            subjectBalance = subjectBalanceList.get(0);
        }

        return subjectBalance;
    }

    /**
     * 调用共通方法：[标的类共通方法-查询交易信息]，获取标的交易信息
     *
     * @param finprodId
     *            金融产品代码【必输项】
     * @param finprodType
     *            金融产品类型【必输项】
     * @param branch
     *            分支序号
     * @return ValFinProductDeal 标的查询
     * @see
     * @since 1.0
     */
    private TradeInfoDto getTargetInfo(String finprodId, String finprodType, Long branch) {
        // 定义返回值 - 标的交易信息
        TradeInfoDto tradeInfoDtoForReturn = null;

        // 调用共通方法：[标的类共通方法 - 交易信息查询]，获取标的交易信息
        List<TradeInfoDto> tradeInfoDtoList =
            finProductCommonService.getTradeInfoDtoList(finprodId, finprodType, branch);

        // 当查询结果不为空
        if (tradeInfoDtoList != null && tradeInfoDtoList.size() > 0) {
            // 默认取第一条标的信息
            tradeInfoDtoForReturn = tradeInfoDtoList.get(0);
        }

        return tradeInfoDtoForReturn;
    }

    /**
     * 质押式回购交易，根据交易方向和金融产品类型，获取对应的会计场景代码。
     *
     * @param ps
     *            交易方向
     * @param finprodType
     *            金融产品类型
     * @return String 会计场景代码
     * @see
     * @since 1.0
     */
    private String getTableId(String ps, String finprodType) {
        // 设置 产检场景代码
        String tableIdForReturn = null;

        // 根据金融产品类型，判断当前分录场景是【债券质押式逆回购】还是【债券质押式正回购】
        if (DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue().equals(finprodType)) {// 分录场景为【债券质押式逆回购】
            // 区分交易方向
            if (DictCodeList.交易方向_买入.getValue().equals(ps)) {// 交易方向 - 买入
                tableIdForReturn = DictCodeListTableId.会计场景代码_质押式逆回购_起息.getValue();// ad.会计场景代码="质押式逆回购-起息"
            }
            /** modify by yanya on 20191111 增加提前终止、逾期返售start **/
            else if (DictCodeListExt3.交易方向_投资端_提前终止.getValue().equals(ps)) {// 交易方向 - 提前终止
                tableIdForReturn = DictCodeListTableId.会计场景代码_质押式逆回购_到期.getValue();// ad.会计场景代码="质押式逆回购-到期/提前终止"
            } else if (DictCodeListExt3.交易方向_投资端_逾期返售.getValue().equals(ps)) {// 交易方向 - 逾期返售
                tableIdForReturn = DictCodeListTableId.会计场景代码_质押式逆回购_逾期返售.getValue();// ad.会计场景代码="质押式逆回购-逾期返售"
            }
            /** modify by yanya on 20191111 增加提前终止、逾期返售end **/
            else {// 其他情况（卖出、到期）
                tableIdForReturn = DictCodeListTableId.会计场景代码_质押式逆回购_到期.getValue();// ad.会计场景代码="质押式逆回购-到期"
            }
        } else {// 分录场景为【债券质押式正回购】
            // 区分交易方向
            if (DictCodeList.交易方向_买入.getValue().equals(ps)) {// 交易方向 - 买入
                tableIdForReturn = DictCodeListTableId.会计场景代码_质押式正回购_起息.getValue();// ad.会计场景代码="质押式正回购-起息"
            }
            /** modify by yanya on 20191111 增加提前终止、逾期返售start **/
            else if (DictCodeListExt3.交易方向_投资端_提前终止.getValue().equals(ps)) {// 交易方向 - 提前终止
                tableIdForReturn = DictCodeListTableId.会计场景代码_质押式正回购_到期.getValue();// ad.会计场景代码="质押式正回购-到期/提前终止"
            } else if (DictCodeListExt3.交易方向_投资端_逾期返售.getValue().equals(ps)) {// 交易方向 - 逾期返售
                tableIdForReturn = DictCodeListTableId.会计场景代码_质押式正回购_逾期返售.getValue();// ad.会计场景代码="质押式正回购-逾期返售"
            }
            /** modify by yanya on 20191111 增加提前终止、逾期返售end **/
            else {// 其他情况（卖出、到期）
                tableIdForReturn = DictCodeListTableId.会计场景代码_质押式正回购_到期.getValue();// ad.会计场景代码="质押式正回购-到期"

            }
        }

        return tableIdForReturn;
    }

    private LossAmtInputDto getLossAmtInputDto(FinProductDealInfoResult acctEntity, FinProductDealInfoComDTO dealNow,
        String flag, // 01 本金 02 利息
        BigDecimal happenAmt, // 发生金额
        BigDecimal lossBalAmt, // 减值准备余额
        BigDecimal EADAmt, // 当前EAD金额
        BigDecimal receAmt, // 应收款项余额
        BigDecimal totalNum, // 总份额
        BigDecimal happenNum // 发生份额
    ) {
        LossAmtInputDto dto = new LossAmtInputDto();
        // 本息标识
        dto.setFlag(flag);
        // 账套代码
        dto.setBooksetId(acctEntity.getBooksetId());
        // 账务明细代码
        dto.setBokDetailId(acctEntity.getBokDetailId());
        // 批处理优先级
        dto.setTaskPri(dealNow.getTaskPri());
        // 分支序号
        dto.setBranch(dealNow.getBranch());
        // 发生日
        dto.setHappenDate(acctEntity.getHappenDate());
        // 记账日
        dto.setBookDate(acctEntity.getBookDate());
        // 发生金额 = 金额元素.回购本金 = + 金额元素.应计利息
        dto.setHappenAmt(ArithmeticUtil.round(happenAmt, NumberEnum.MONEY));
        // 减值准备余额
        dto.setLossBalAmt(lossBalAmt);
        // 当前EAD金额 = null
        dto.setEADAmt(EADAmt);
        // 应收款余额
        dto.setReceAmt(receAmt);
        // 总份额
        dto.setTotalNum(totalNum);
        // 发生份额
        dto.setHappenNum(happenNum);
        // 业务类型
        dto.setBusiType(dealNow.getFinprodType());
        return dto;
    }
    /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-质押式逆回购减值场景增加 end **/
}
