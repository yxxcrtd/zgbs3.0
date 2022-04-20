/*
 * 文件名：MoneyTaDealBookProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chengzhong
 * 修改时间：2018年5月17日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.moneytadealbook.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.tams.common.base.entity.BokBooksetExt;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.calculation.entity.DealTypesFeeDto;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrueNew;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 货币型产品TA交易入账 process相关业务处理
 * 
 * @author chengzhong
 * @version 1.0 2018年5月17日
 * @see MoneyTaDealBookProcessorNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class MoneyTaDealBookProcessorNewService {

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
     * 账务类共通方法
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 标的查询共通方法
     */
    @Autowired
    private FinProductCommonService finProductCommonService;
    

    /**
     * 计算类共通
     */
    @Autowired
    private CalculationCommonService calculationCommonService;


    /**
     * process 数据处理
     * 
     * @param param 传递参数
     * @return BookGenerateResultWithBokposition
     * @see
     * @since 1.0
     */
    public BookGenerateResult execute(Map<String, Object> param) {
        // 获取reader返回值
        FinProductDealInfoResult acctEntity = (FinProductDealInfoResult)param.get(
            GlobalConstant.READER_RESULT);

        // 若reader传递的参数或金融产品交易信息列表为空，不进行后续处理
        if (acctEntity == null || acctEntity.getListFinProductDealInfoComDTO() == null
            || acctEntity.getListFinProductDealInfoComDTO().isEmpty()) {
            return null;
        }

        // 定义返回值
        BookGenerateResult returnResult = new BookGenerateResult();

        // 账务明细若为空赋初始值
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }

        // 1.数据准备
        // 1.1 变量定义
        // 变量.实收资本 = 0
        BigDecimal varSszb = BigDecimal.ZERO;
        // 变量.应计利息 = 0
        BigDecimal varYjlx = BigDecimal.ZERO;
        // 1.2 标的信息查询
        List<FinProductDealInfoComDTO> listFinProductDealInfoComDTONow = acctEntity.getListFinProductDealInfoComDTO();
//        String finprodId = listFinProductDealInfoComDTONow.get(0).getFinprodId();
//        TargetFinProduct targetFinProduct = this.getTargetFinProduct(finprodId);

        // 1.3 参数获取
        // 分红尾差处理方式
        String bonusTailMode = null;
        List<BokBooksetExt> listValBokBooksetExt = accountingCommonService.getValBokBooksetExt(
            acctEntity.getBooksetId(),
            new ArrayList<String>(Arrays.asList(ValDictCodeList.账套配置表参数项_分红尾差处理方式.getValue())));
        if (listValBokBooksetExt != null && listValBokBooksetExt.size() > 0) {
            bonusTailMode = listValBokBooksetExt.get(0).getParamValue();
        }

        // 1.4 应计利息科目余额查询
        SubjectBalance subjectBalance = this.getSubjectBalance(acctEntity.getBooksetId(),
            acctEntity.getBokDetailId(), acctEntity.getHappenDate(), acctEntity.getBookDate(),
            DictCodeList.余额方向_贷方.getValue(), DictCodeBaseSubjectNatrueNew.应付利润.getValue(),
            listFinProductDealInfoComDTONow.get(0).getTaskPri());
        if (subjectBalance != null) {
            varYjlx = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
        }

        // 循环金融产品交易信息list,进行逻辑处理
        for (FinProductDealInfoComDTO dealNow : listFinProductDealInfoComDTONow) {

            // 初始化定义 - 记账场景要素管理
            AccountDriver ad = new AccountDriver();
            // 初始化定义 - 存放该笔记账的场景要素 map定义
            Map<String, Object> accountSceneMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
            Map<String, Object> accountCurrencyMap = new HashMap<>();

            // 共通元素设值 - 设置账务引擎参数
            ad.setTreeNode(acctEntity.getBookSetTreeNode());// 塞入记账场景树节点 = 会计场景

            // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值)
            accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);

            // 根据不同的交易方向，设置其他记账凭证要素
            // 交易方向 - 产品认购，产品申购
            if (DictCodeList.交易方向_产品认购.getValue().equals(dealNow.getPs())
                || DictCodeList.交易方向_产品申购.getValue().equals(dealNow.getPs())
                || DictCodeList.交易方向_份额强增.getValue().equals(dealNow.getPs())) {
                // 设置账务引擎参数
                ad.setTableId(DictCodeListTableId.会计场景代码_产品_货币型_认购申购交易.getValue());

                // 记账场景要素map塞值
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                    DictCodeListTableId.会计场景代码_产品_货币型_认购申购交易.getCodeName());// 凭证说明
                
                /*会计分录修改   add by chufengjie 20190801 start*/
                //调用 共通【费用处理共通】 
                DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
                        dealNow.getValTradeFeeDetailList(),dealNow.getProdId(),dealNow.getPortfolioId());
                
                //定义变量 金额元素.通道费
                BigDecimal chlFee = null;
                if (dealTypesFeeDto.getChlFee() != null){
                 //金额元素.通道费 = 变量.交易各种费用.通道费_不随交易支付
                  chlFee = dealTypesFeeDto.getChlFee();
                }else{
                  //金额元素.通道费 = 0
                  chlFee = new BigDecimal(0);
                }
                
                // 记账金额元素map塞值
                //金额元素.通道费   = 变量.交易各种费用.通道费_不随交易支付
                accountAmountMap.put(BookConstant.CHLAMT,
                       ArithmeticUtil.round(chlFee, NumberEnum.MONEY));
                
                //金额元素.申购份额 = [待处理交易].[交易金额]-金额元素.通道费
                accountAmountMap.put(BookConstant.COST,
                        ArithmeticUtil.round(ArithmeticUtil.doSub(dealNow.getTradeAmt(),chlFee), NumberEnum.MONEY));
                /*会计分录修改   add by chufengjie 20190801 end*/               
               
                //申购金额
                accountAmountMap.put(BookConstant.DEALAMT,
                    ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));               
                
                // 全局变量处理
                // 变量：实收资本 += 金额元素.申购份额
                varSszb = ArithmeticUtil.doAdd(varSszb, ArithmeticUtil.doSub(dealNow.getTradeAmt(),chlFee));

            } // 交易方向 - 产品赎回、产品到期、份额强增、份额强减
            else if (DictCodeList.交易方向_产品赎回.getValue().equals(dealNow.getPs())
                     || DictCodeList.交易方向_产品到期.getValue().equals(dealNow.getPs())
                     || DictCodeList.交易方向_份额强减.getValue().equals(dealNow.getPs())
                     
             		// 与王之昂金强确认：所有产品增加（1、交易方向加提前赎回、违约赎回，估值走赎回分录，2、交易方向加提前终止，估值走到期分录。）
                    || DictCodeList.交易方向_产品提前赎回.getValue().equals(dealNow.getPs()) 
             		|| DictCodeList.交易方向_产品违约赎回.getValue().equals(dealNow.getPs())
             		|| DictCodeList.交易方向_提前终止.getValue().equals(dealNow.getPs())
                     
            		) {
                // 设置账务引擎参数
                ad.setTableId(DictCodeListTableId.会计场景代码_产品_货币型_赎回交易.getValue());

                // 记账场景要素map塞值
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                    DictCodeListTableId.会计场景代码_产品_货币型_赎回交易.getCodeName());// 凭证说明

                // 记账金额元素map塞值
                // 金额元素.赎回份额 = [待处理交易].[交易份额]
                accountAmountMap.put(BookConstant.REDEEM_QTY,
                    ArithmeticUtil.round(dealNow.getShareAmt(), NumberEnum.MONEY));
                // 金额元素.赎回费中留存产品部分 = 交易.随交易支付费用
                accountAmountMap.put(BookConstant.OTHER_INTAMT,
                    ArithmeticUtil.round(dealNow.getFeeAmtPay(), NumberEnum.MONEY));
                // 金额元素.赎回净金额 = [待处理交易].[交易金额]
                accountAmountMap.put(BookConstant.REDEEM_NETAMT,
                    ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));

                // 全局变量处理
                // 变量：实收资本 = 变量：实收资本 - 金额元素.赎回份额
                varSszb = ArithmeticUtil.subtract(varSszb, dealNow.getShareAmt());

            } // 交易方向 -产品现金分红
            else if (DictCodeList.交易方向_产品现金分红.getValue().equals(dealNow.getPs())
                    ||DictCodeList.交易方向_赎回收益确认.getValue().equals(dealNow.getPs())
                    ) {
                // 设置账务引擎参数
                ad.setTableId(DictCodeListTableId.会计场景代码_产品_货币型_现金分红.getValue());

                // 记账场景要素map塞值
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                    DictCodeListTableId.会计场景代码_产品_货币型_现金分红.getCodeName());// 凭证说明

                // 变量.冲减应计利息 = 0
                BigDecimal varCjyjlx = BigDecimal.ZERO;
                // if （变量.分红尾差处理方式 = "无需处理"）
                if (DictCodeList.分红尾差处理方式_无需处理.getValue().equals(bonusTailMode)||DictCodeList.交易方向_赎回收益确认.getValue().equals(dealNow.getPs())) {
                    // 变量.冲减应计利息 = [待处理交易].[交易金额]
                    varCjyjlx = dealNow.getTradeAmt();
                }
                else {
                    // 到期日应付利润科目余额查询
                    SubjectBalance subjectBalanceEx = this.getSubjectBalance(
                        acctEntity.getBooksetId(), acctEntity.getBokDetailId(),
                        acctEntity.getHappenDate(), addDate(dealNow.getMdate(), -1),
                        DictCodeList.余额方向_贷方.getValue(), DictCodeBaseSubjectNatrueNew.应付利润.getValue(),
                        dealNow.getTaskPri());
                    if (subjectBalanceEx != null) {
                        varCjyjlx = subjectBalanceEx.getBalance() == null ? BigDecimal.ZERO : subjectBalanceEx.getBalance();
                    }
                }
                // 变量.借贷差额 = [待处理交易].[交易金额] - 变量.冲减应计利息
                BigDecimal varJdce = ArithmeticUtil.subtract(dealNow.getTradeAmt(), varCjyjlx);

                // 金额元素.客户收益 = 变量.冲减应计利息
                accountAmountMap.put(BookConstant.CUSTREV,
                    ArithmeticUtil.round(varCjyjlx, NumberEnum.MONEY));
                // 金额元素.支付分红金额 = [待处理交易].[交易金额]
                accountAmountMap.put(BookConstant.PAY_DIVIDENDAMT,
                    ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));

                // if(变量.借贷差额 >= 0)
                if (varJdce.compareTo(BigDecimal.ZERO) >= 0) {
                    // 金额元素.借贷差额_借 = 变量.借贷差额
                    accountAmountMap.put(BookConstant.PLGAIN_D,
                        ArithmeticUtil.round(varJdce, NumberEnum.MONEY));
                }
                else {
                    // 金额元素.借贷差额_贷 = 变量.借贷差额 * -1
                    accountAmountMap.put(BookConstant.PLGAIN_C,
                        ArithmeticUtil.round(varJdce.negate(), NumberEnum.MONEY));
                }
                // 全局变量处理
                // 变量：应计利息 = 变量：应计利息 - 变量.冲减应计利息
                varYjlx = ArithmeticUtil.subtract(varYjlx, varCjyjlx);

            } // 交易方向 - 产品红利转投
            else if (DictCodeList.交易方向_产品红利转投.getValue().equals(dealNow.getPs())) {
                // 设置账务引擎参数
                ad.setTableId(DictCodeListTableId.会计场景代码_产品_货币型_红利再投.getValue());

                // 记账场景要素map塞值
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                    DictCodeListTableId.会计场景代码_产品_货币型_红利再投.getCodeName());// 凭证说明

                // 变量.冲减应计利息 = 0
                BigDecimal varCjyjlx = BigDecimal.ZERO;
                // if （变量.分红尾差处理方式 = "无需处理"）
                if (DictCodeList.分红尾差处理方式_无需处理.getValue().equals(bonusTailMode)) {
                    // 变量.冲减应计利息 = [待处理交易].[全价总额]
                    varCjyjlx = dealNow.getFpriceAmt();
                }
                else {
                    // 到期日应付利润科目余额查询
                    SubjectBalance subjectBalanceEx = this.getSubjectBalance(
                        acctEntity.getBooksetId(), acctEntity.getBokDetailId(),
                        acctEntity.getHappenDate(), addDate(dealNow.getMdate(), -1),
                        DictCodeList.余额方向_贷方.getValue(), DictCodeBaseSubjectNatrueNew.应付利润.getValue(),
                        dealNow.getTaskPri());
                    if (subjectBalanceEx != null) {
                        varCjyjlx = subjectBalanceEx.getBalance() == null ? BigDecimal.ZERO : subjectBalanceEx.getBalance();
                    }
                }
                // 变量.借贷差额 = [待处理交易].[全价总额] - 变量.冲减应计利息
                BigDecimal varJdce = ArithmeticUtil.subtract(dealNow.getFpriceAmt(), varCjyjlx);

                // if（变量.冲减应计利息 >= 0）
                if (varCjyjlx.compareTo(BigDecimal.ZERO) >= 0) {
                    // 金额元素.客户收益_借 = 变量.冲减应计利息
                    accountAmountMap.put(BookConstant.CUSTREV_D,
                        ArithmeticUtil.round(varCjyjlx, NumberEnum.MONEY));
//                    accountAmountMap.put(BookConstant.CUSTREV_D+BookConstant.BCCYAMT,
//                            ArithmeticUtil.round(varCjyjlx, NumberEnum.MONEY));
                }
                else {
                    // 金额元素.客户收益_贷 = 变量.冲减应计利息 * -1
                    accountAmountMap.put(BookConstant.CUSTREV_C,
                        ArithmeticUtil.round(varCjyjlx.negate(), NumberEnum.MONEY));
//                    accountAmountMap.put(BookConstant.CUSTREV_C +BookConstant.BCCYAMT,
//                            ArithmeticUtil.round(varCjyjlx.negate(), NumberEnum.MONEY));
                }

                // if（[待处理交易].[交易份额] >= 0）
                if (dealNow.getShareAmt().compareTo(BigDecimal.ZERO) >= 0) {
                    // 金额元素.申购份额_贷 = [待处理交易].[交易份额]
                    accountAmountMap.put(BookConstant.TRADE_QTY_C,
                        ArithmeticUtil.round(dealNow.getShareAmt(), NumberEnum.MONEY));
                    accountSceneMap.put(BookConstant.HAPPEN_NUMBER, dealNow.getShareAmt());
                }
                else {
                    // 金额元素.申购份额_借 = [待处理交易].[交易份额] * -1
                    accountAmountMap.put(BookConstant.TRADE_QTY_D,
                        ArithmeticUtil.round(dealNow.getShareAmt().negate(), NumberEnum.MONEY));
                    accountSceneMap.put(BookConstant.HAPPEN_NUMBER, dealNow.getShareAmt().negate());
                }

                // if(变量.借贷差额 >= 0)
                if (varJdce.compareTo(BigDecimal.ZERO) >= 0) {
                    // 金额元素.借贷差额_借 = 变量.借贷差额
                    accountAmountMap.put(BookConstant.PLGAIN_D,
                        ArithmeticUtil.round(varJdce, NumberEnum.MONEY));
                }
                else {
                    // 金额元素.借贷差额_贷 = 变量.借贷差额 * -1
                    accountAmountMap.put(BookConstant.PLGAIN_C,
                        ArithmeticUtil.round(varJdce.negate(), NumberEnum.MONEY));
                }
                // 全局变量处理
                // 变量：应计利息 = 变量：应计利息 - 变量.冲减应计利息
                varYjlx = ArithmeticUtil.subtract(varYjlx, varCjyjlx);
            } // 交易方向 - 产品到期分红
            else if (DictCodeList.交易方向_产品到期分红.getValue().equals(dealNow.getPs())) {
                // 设置账务引擎参数
                ad.setTableId(DictCodeListTableId.会计场景代码_产品_货币型_到期分红.getValue());

                // 记账场景要素map塞值
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                    DictCodeListTableId.会计场景代码_产品_货币型_到期分红.getCodeName());// 凭证说明

                // 变量.冲减应计利息 = 0
                BigDecimal varCjyjlx = BigDecimal.ZERO;
                // if （变量.分红尾差处理方式 = "无需处理"）
                if (DictCodeList.分红尾差处理方式_无需处理.getValue().equals(bonusTailMode)) {
                    // 变量.冲减应计利息 = [待处理交易].[交易金额]
                    varCjyjlx = dealNow.getTradeAmt();
                }
                else {
                    // 到期日应付利润科目余额查询
                    SubjectBalance subjectBalanceEx = this.getSubjectBalance(
                        acctEntity.getBooksetId(), acctEntity.getBokDetailId(),
                        acctEntity.getHappenDate(), addDate(dealNow.getMdate(), -1),
                        DictCodeList.余额方向_贷方.getValue(), DictCodeBaseSubjectNatrueNew.应付利润.getValue(),
                        dealNow.getTaskPri());
                    if (subjectBalanceEx != null) {
                        varCjyjlx = subjectBalanceEx.getBalance() == null ? BigDecimal.ZERO : subjectBalanceEx.getBalance();
                    }
                }
                // 变量.借贷差额 = [待处理交易].[交易金额] - 变量.冲减应计利息
                BigDecimal varJdce = ArithmeticUtil.subtract(dealNow.getTradeAmt(), varCjyjlx);

                // 金额元素.客户收益 = 变量.冲减应计利息
                accountAmountMap.put(BookConstant.PAY_BOPAMT,
                    ArithmeticUtil.round(varCjyjlx, NumberEnum.MONEY));
                // 金额元素.支付分红金额 = [待处理交易].[交易金额]
                accountAmountMap.put(BookConstant.PAY_DIVIDENDAMT,
                    ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));
                if (varJdce.compareTo(BigDecimal.ZERO) >= 0) {
                    // 金额元素.借贷差额 = 变量.借贷差额
                    accountAmountMap.put(BookConstant.PLGAIN_D,
                        ArithmeticUtil.round(varJdce, NumberEnum.MONEY));
                } else {
                    // 金额元素.借贷差额 = 变量.借贷差额
                    accountAmountMap.put(BookConstant.PLGAIN_C,
                        ArithmeticUtil.round(varJdce.negate(), NumberEnum.MONEY));
                }
                // 全局变量处理
                // 变量：应计利息 = 变量：应计利息 - 变量.冲减应计利息
                varYjlx = ArithmeticUtil.subtract(varYjlx, varCjyjlx);
            }

            // 记账金额元素币种信息塞值
            // 因为与记账金额元素map的key及数量一致，故采用循环
            for (String keyNow : accountAmountMap.keySet()) {
                accountCurrencyMap.put(keyNow, dealNow.getCcy());// value值塞入当前交易币种
            }

            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
            ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
            ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

            // 记账凭证生成
            BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            returnResult.getValBokBookdetailBatch().addAll(
                resultNowNow.getValBokBookdetailBatch());
        }

        return returnResult;
    }

    /**
     * 获取标的基本信息
     * 
     * @param bokDetailId 账务明细代码
     * @return TargetFinProduct 标的基本信息
     * @see
     * @since 1.0
     */
//    private TargetFinProduct getTargetFinProduct(String bokDetailId) {
//        // 定义查询list
//        List<TargetSearchDto> targetSearchDtoList = new ArrayList<TargetSearchDto>();
//        // 定义查询类型 实体类
//        TargetSearchDto targetSearchDto = new TargetSearchDto();
//        targetSearchDto.setQueryType(ValDictCodeList.标的_信息.getValue());
//        // 查询实体类塞入查询实体类List中
//        targetSearchDtoList.add(targetSearchDto);
//
//        // 获取标的信息
//        Map<TargetSearchDto, Object> targetMap = finProductCommonService.getTargetInfo(bokDetailId,
//            targetSearchDtoList);
//        TargetFinProduct targetFinProduct = (TargetFinProduct)targetMap.get(targetSearchDto);
//
//        // 确保不为null
//        if (targetFinProduct == null) {
//            targetFinProduct = new TargetFinProduct();
//        }
//
//        return targetFinProduct;
//    }

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
    private SubjectBalance getSubjectBalance(String booksetId, String bokDetailId, Date happenDate,
                                             Date bookDate, String expBalanceDirect,
                                             String baseSubjectNature, Long taskPri) {
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
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDto, expBalanceDirect);

        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            subjectBalance = subjectBalanceList.get(0);
        }

        return subjectBalance;
    }

    /**
     * 日期加减
     * 
     * @param nowDate 传递时间
     * @param days 加减天数
     * @return Date
     * @see
     * @since 1.0
     */
    private Date addDate(Date nowDate, int days) {
        // 获取日历实例
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        Date day = calendar.getTime();

        return day;
    }
}
