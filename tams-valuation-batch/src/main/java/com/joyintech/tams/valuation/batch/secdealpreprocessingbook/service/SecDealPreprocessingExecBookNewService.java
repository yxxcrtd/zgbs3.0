/*
 * 文件名：SecDealPreprocessingBookService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V2.0
 * 修改人：chengzhong
 * 修改时间：2018年6月7日
 * 修改内容：新建
 * 系统名称：估值核算系统V2.0
 */

package com.joyintech.tams.valuation.batch.secdealpreprocessingbook.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokStatLossPosiiton;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.BookLatestInfoDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.LossAmtInputDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accounting.service.LossCommonBatchService;
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
import com.joyintech.tams.valuation.batch.secdealaccounts.dto.DealParamterDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.CurrencyMarketDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.batch.losscommon.service.LossCommonService;
import com.joyintech.tams.valuation.common.batch.tradecommon.service.ValTradeCommonService;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrueNew;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 债券交易账务预处理-行权相关service
 * @author yanya
 * @version 1.0 2021年3月11日
 * @see SecDealPreprocessingExecBookNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class SecDealPreprocessingExecBookNewService {

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
     * 记账引擎
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;
    
    @Autowired
    private LossCommonBatchService lossCommonBatchService;
    
    @Autowired
    private LossCommonService lossCommonService;
    
    @Autowired
    private CalculationCommonService calculationCommonService;

    @Autowired
    ValTradeCommonService ValTradeCommonService;


    /**
     * 公允价值损益类 行权回售申请
     * @param rstBaseinfo
     * @param returnResult
     * @param dealNow
     * @param acctEntity
     * @param param
     */
    public void createExecHSFvtpl(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
                              FinProductDealInfoComDTO dealNow,
                              FinProductDealInfoResult acctEntity, DealParamterDto param) {
        // 账套核算参数查询
        // 定义账套参数查询参数项list
        List<String> paramCodeList = new ArrayList<String>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_债券交易费用损益计入方式.getValue());// 债券交易费用损益计入方式
        Map<String, Object> getValBokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(
                acctEntity.getBooksetId(), paramCodeList);
        // 定义 变量.费用损益计入方式
        String varSecDealFeeSubject = (String)getValBokBooksetExtMap.get(
                ValDictCodeList.账套配置表参数项_债券交易费用损益计入方式.getValue());// 债券交易费用损益计入方式
        // 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();
        // 汇率
        BigDecimal changeRate = BigDecimal.ONE;
        if(StringUtil.isNotEmpty(dealNow.getCcy()) && !dealNow.getCcy().equals(acctEntity.getStandardCcy())){
            // 调用共通汇率查询
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                    acctEntity.getStandardCcy(), dealNow.getCcy());
            if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                changeRate = currencyMarketList.get(0).getClosePrice();
            }
        }
        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());
        ad.setTableId(DictCodeListTableId.会计场景代码_公允价值损益类债券_回售申请.getValue());
        // accountSceneMap设置
        // 备注
        accountSceneMap.put(BookConstant.BOOK_REMARK, "公允价值损益类债券资产" + DictCodeListExt3.交易方向_行权回售申请.getCodeName() + "交易登账");
        // 公共场景赋值
        getAccountSceneMap(accountSceneMap, dealNow, rstBaseinfo);
        //定义		变量.结转日应计利息余额 = 变量.结转日应计利息余额Map.get(交易明细.到期日)
        BigDecimal 结转日应计利息余额 = param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日成本科目余额 = 变量.结转日成本科目余额Map.get(交易明细.到期日）
        BigDecimal 结转日成本科目余额 = param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日成本科目数量 = 变量.结转日成本科目数量Map.get(交易明细.到期日)
        BigDecimal 结转日成本科目数量 = param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日公允价值变动科目余额 = 变量.结转日公允价值变动科目余额Map.get(交易明细.到期日)
        BigDecimal 结转日公允价值变动科目余额 = param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate()));
        List<SubjectBalance> listSubjectBalance = new ArrayList<SubjectBalance>();
        // 科目余额查询参数
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        searchDao.setBooksetId(acctEntity.getBooksetId());
        searchDao.setBokDetailId(acctEntity.getBokDetailId());
        searchDao.setHappenDate(addDate(dealNow.getMdate(), -1));
        searchDao.setBookDate(acctEntity.getBookDate());
        //IF 变量.结转日应计利息余额Map.get(交易明细.到期日) == NULL
        if(结转日应计利息余额 == null) {
            //查询交易的到期日对应的应计利息
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.应计利息余额 = 科目余额
                结转日应计利息余额 = listSubjectBalance.get(0).getBalance();
                /** modify by yanya on 20210325 增加利息本位币处理**/
                param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalance.get(0).getbAmt());
            }
            else {
                结转日应计利息余额 = BigDecimal.ZERO;
                /** modify by yanya on 20210325 增加利息本位币处理**/
                param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzryjlxyeMap().put(DateUtil.format(dealNow.getMdate()), 结转日应计利息余额);
        }
        //IF 变量.结转日成本科目余额Map.get(交易明细.到期日） IS NULL
        if(结转日成本科目余额 == null) {
            searchDao.setBaseSubjectNatureList(
                    new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.结转日成本科目余额 = 科目余额
                结转日成本科目余额 = listSubjectBalance.get(0).getBalance();
                // 定义 变量.结转日成本科目数量 = 数量
                结转日成本科目数量 = listSubjectBalance.get(0).getNum();
                /** modify by yanya on 20210325 增加科目成本本位币处理**/
                param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalance.get(0).getbAmt());
            }
            else {
                结转日成本科目余额 = BigDecimal.ZERO;
                结转日成本科目数量 = BigDecimal.ZERO;
                /** modify by yanya on 20210325 增加科目成本本位币处理**/
                param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzrcbkmyeMap().put(DateUtil.format(dealNow.getMdate()), 结转日成本科目余额);
            param.getJzrcbkmslMap().put(DateUtil.format(dealNow.getMdate()), 结转日成本科目数量);
        }
        //BigDecimal varGyjzbdye = BigDecimal.ZERO;
        if(结转日公允价值变动科目余额 == null) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue())));
            List<SubjectBalance> listSubjectBalanceEx = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalanceEx != null && listSubjectBalanceEx.size() > 0) {
                // 变量.结转日公允价值变动科目余额 = 科目余额
                结转日公允价值变动科目余额 = listSubjectBalanceEx.get(0).getBalance();
                /** modify by yanya on 20210326 公允价值变动本位币处理**/
                param.getJzrgyjzbdMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalanceEx.get(0).getbAmt());
            }
            else {
                结转日公允价值变动科目余额 = BigDecimal.ZERO;
                /** modify by yanya on 20210326 公允价值变动本位币处理**/
                param.getJzrgyjzbdMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzrgyjzbdMap().put(DateUtil.format(dealNow.getMdate()), 结转日公允价值变动科目余额);
        }
        if (!DictCodeListExt1.估值交易市场_上交所.getValue().equals(rstBaseinfo.getTradeMarket())
                && !DictCodeListExt1.估值交易市场_深交所.getValue().equals(rstBaseinfo.getTradeMarket())) {
            // 买入场景
            // 调用账务共通设计->【凭证最新账务查询】
            List<BookLatestInfoDto> listBookLatestInfoDto = getBookLatestInfo(dealNow.getInvAim(),true, dealNow, acctEntity);
            // LOOP 变量.买入账务明细列表 -> 变量.账务明细
            if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                for (BookLatestInfoDto var : listBookLatestInfoDto) {
                    // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.结转日成本科目数量 -= 变量.账务明细.发生数量
                        结转日成本科目数量 = ArithmeticUtil.subtract(结转日成本科目数量, var.getHappenNumber());
                        // 变量.结转日成本科目余额 -= 变量.账务明细.原币发生额
                        结转日成本科目余额 = ArithmeticUtil.subtract(结转日成本科目余额, var.getoHappenAmt());
                    }
                    // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_公允价值变动.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日公允价值变动科目余额 -= 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.subtract(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日公允价值变动科目余额 += 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.doAdd(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                    }
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue().equals(var.getBaseSubjectNature())) {
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日应计利息余额 -= 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.subtract(结转日应计利息余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日应计利息余额 += 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.doAdd(结转日应计利息余额, var.getoHappenAmt());
                        }
                    }
                }
            }
            // 卖出场景
            // 调用账务共通设计->【凭证最新账务查询】
            listBookLatestInfoDto = getBookLatestInfo(dealNow.getInvAim(), false, dealNow, acctEntity);
            // LOOP 变量.卖出账务明细列表 -> 变量.账务明细
            if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                for (BookLatestInfoDto var : listBookLatestInfoDto) {
                    // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.结转日成本科目数量 += 变量.账务明细.发生数量
                        结转日成本科目数量 = ArithmeticUtil.doAdd(结转日成本科目数量, var.getHappenNumber());
                        // 变量.结转日成本科目余额 += 变量.账务明细.原币发生额
                        结转日成本科目余额 = ArithmeticUtil.doAdd(结转日成本科目余额, var.getoHappenAmt());
                    }
                    // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_公允价值变动.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日公允价值变动科目余额 -= 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.subtract(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日公允价值变动科目余额 += 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.doAdd(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                    }
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue().equals(var.getBaseSubjectNature())) {
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日应计利息余额 -= 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.subtract(结转日应计利息余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日应计利息余额 += 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.doAdd(结转日应计利息余额, var.getoHappenAmt());
                        }
                    }
                }
            }
        }
        //定义 		变量.冲减公允价值变动								=		0
        BigDecimal 冲减公允价值变动 = BigDecimal.ZERO;
        //定义 		变量.冲减应计利息								=		0
        BigDecimal 冲减应计利息 = BigDecimal.ZERO;
        //定义 		变量.冲减数量								=		交易明细.交易份额
        BigDecimal 冲减数量 = dealNow.getShareAmt();
        //定义 		变量.冲减成本								=		0
        BigDecimal 冲减成本 = BigDecimal.ZERO;
        //定义 		变量.利息差额								=		0
        BigDecimal 利息差额 = BigDecimal.ZERO;
        //定义 		变量.买卖价差								=		0
        BigDecimal 买卖价差 = BigDecimal.ZERO;
        //IF 变量.结转日成本科目数量 - 变量.冲减数量 == 0
        if(ArithmeticUtil.doSub(结转日成本科目数量, 冲减数量).compareTo(BigDecimal.ZERO) == 0) {
            //变量.冲减公允价值变动	=		变量.结转日公允价值变动科目余额
            冲减公允价值变动 = 结转日公允价值变动科目余额;
            //变量.冲减成本		=		变量.结转日成本科目余额
            冲减成本 = 结转日成本科目余额;
            //变量.冲减应计利息		=		变量.结转日应计利息余额
            冲减应计利息 = 结转日应计利息余额;
        }
        //ELSE
        else {
            //变量.冲减成本		=		变量.结转日成本科目余额 * 交易明细.交易份额 / 变量.结转日成本科目数量
            冲减成本 = ArithmeticUtil.divide(
                    ArithmeticUtil.multiply(结转日成本科目余额, 冲减数量), 结转日成本科目数量);
            //变量.冲减公允价值变动	=		变量.结转日公允价值变动科目余额 * 交易明细.交易份额 / 变量.结转日成本科目数量
            冲减公允价值变动 = ArithmeticUtil.divide(
                    ArithmeticUtil.multiply(结转日公允价值变动科目余额, 冲减数量), 结转日成本科目数量);
            //变量.冲减应计利息		=		变量.应计利息科目余额 * 交易明细.交易份额 / 变量.结转日成本科目数量
            冲减应计利息 = ArithmeticUtil.divide(
                    ArithmeticUtil.multiply(结转日应计利息余额, 冲减数量), 结转日成本科目数量);
        }
        //变量.利息差额	=		交易明细.利息总额 - 变量.冲减应计利息
        利息差额 = ArithmeticUtil.doSub(dealNow.getIntAmt(), 冲减应计利息);
        //调用 共通【费用处理共通】  参考本设计书sheet【费用处理共通】
        DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
                dealNow.getValTradeFeeDetailList(), dealNow.getProdId(), dealNow.getPortfolioId());
        // 印花税
        accountAmountMap.put(BookConstant.STAMPTAX, dealTypesFeeDto.getStampDuty());
        // 经手费
        accountAmountMap.put(BookConstant.HANDFEE, dealTypesFeeDto.getBrokerage());
        // 证管费
        accountAmountMap.put(BookConstant.CERTIFEE, dealTypesFeeDto.getCertificateFee());
        // 结算费
        accountAmountMap.put(BookConstant.SETTLEFEE, dealTypesFeeDto.getClearingFee());
        // 佣金
        accountAmountMap.put(BookConstant.COMMISSION, dealTypesFeeDto.getCommission());
        // 交易手续费
        accountAmountMap.put(BookConstant.TRADEFEE, dealTypesFeeDto.getTransactionFee());
        // 结算手续费
        accountAmountMap.put(BookConstant.SETTLEMENTFEE, dealTypesFeeDto.getSettlementFee());
        //实付通道费
        accountAmountMap.put(BookConstant.CHLAMT, dealTypesFeeDto.getChlFee());
        // 实收金额
        accountAmountMap.put(BookConstant.DEALAMT, ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));
        //成本金额 = 变量.冲减成本
        accountAmountMap.put(BookConstant.FACEVALUE, ArithmeticUtil.round(冲减成本, NumberEnum.MONEY));
        //应计利息 = 冲减应计利息
        accountAmountMap.put(BookConstant.INTAMT, ArithmeticUtil.round(冲减应计利息, NumberEnum.MONEY));
        // 变量.费用损益计入方式 = '计入投资收益'情况
        if (DictCodeList.债券交易费用损益计入方式_计入投资收益.getValue().equals(varSecDealFeeSubject)) {
            // 变量.买卖价差 <- 0
            买卖价差 = BigDecimal.ZERO;
        }
        else {
            // 印花税支出 <- 变量.交易各种费用.印花税 + 变量.交易各种费用.随交易支付_印花税
            BigDecimal VAR_STAMPTAX_OUT = ArithmeticUtil.doAdd(dealTypesFeeDto.getStampDuty(),dealTypesFeeDto.getStampDutyForDealPay());
            accountAmountMap.put(BookConstant.STAMPTAX_OUT, VAR_STAMPTAX_OUT);
            // 经手费支出 <- 变量.交易各种费用.经手费 + 变量.交易各种费用.随交易支付_经手费
            BigDecimal VAR_HANDFEE_OUT = ArithmeticUtil.doAdd(dealTypesFeeDto.getBrokerage(),dealTypesFeeDto.getBrokerageForDealPay());
            accountAmountMap.put(BookConstant.HANDFEE_OUT, VAR_HANDFEE_OUT);
            // 证管费支出 <- 变量.交易各种费用.证管费 + 变量.交易各种费用.随交易支付_证管费
            BigDecimal VAR_CERTIFEE_OUT = ArithmeticUtil.doAdd(dealTypesFeeDto.getCertificateFee(),dealTypesFeeDto.getCertificateFeeForDealPay());
            accountAmountMap.put(BookConstant.CERTIFEE_OUT, VAR_CERTIFEE_OUT);
            // 结算费支出 <- 变量.交易各种费用.结算费 + 变量.交易各种费用.随交易支付_结算费
            BigDecimal VAR_SETTLEFEE_OUT = ArithmeticUtil.doAdd(dealTypesFeeDto.getClearingFee(),dealTypesFeeDto.getClearingFeeForDealPay());
            accountAmountMap.put(BookConstant.SETTLEFEE_OUT, VAR_SETTLEFEE_OUT);
            // 佣金支出 <- 变量.交易各种费用.佣金 + 变量.交易各种费用.随交易支付_佣金
            BigDecimal VAR_COMMISSION_OUT = ArithmeticUtil.doAdd(dealTypesFeeDto.getCommission(),dealTypesFeeDto.getCommissionForDealPay());
            accountAmountMap.put(BookConstant.COMMISSION_OUT, VAR_COMMISSION_OUT);
            // 交易手续费支出 <- 变量.交易各种费用.交易手续费 + 变量.交易各种费用.随交易支付_交易手续费
            BigDecimal VAR_TRADEFEE_OUT = ArithmeticUtil.doAdd(dealTypesFeeDto.getTransactionFee(), dealTypesFeeDto.getTransactionFeeForDealPay());
            accountAmountMap.put(BookConstant.TRADEFEE_OUT, VAR_TRADEFEE_OUT);
            // 结算手续费支出 <- 变量.交易各种费用.结算手续费 + 变量.交易各种费用.随交易支付_结算手续费
            BigDecimal VAR_SETTLEMENTFEE_OUT = ArithmeticUtil.doAdd(dealTypesFeeDto.getSettlementFee(), dealTypesFeeDto.getSettlementFeeForDealPay());
            accountAmountMap.put(BookConstant.SETTLEMENTFEE_OUT, VAR_SETTLEMENTFEE_OUT);
            // 变量.买卖价差 <- 印花税支出 + 经手费支出 + 证管费支出 + 结算费支出 + 佣金支出 + 交易手续费支出 + 结算手续费支出
            买卖价差 = ArithmeticUtil.doAdd(VAR_STAMPTAX_OUT, VAR_HANDFEE_OUT, VAR_CERTIFEE_OUT,VAR_SETTLEFEE_OUT,
                    VAR_COMMISSION_OUT, VAR_TRADEFEE_OUT, VAR_SETTLEMENTFEE_OUT);
        }
        //变量.买卖价差=变量.买卖价差+交易明细.交易金额-变量.冲减成本-变量.交易各种费用.不随交易费用+变量.交易各种费用.通道费-变量.冲减应计利息-变量.利息差额
        买卖价差 = ArithmeticUtil.doSub(
                ArithmeticUtil.doAdd(买卖价差, dealNow.getTradeAmt(), dealTypesFeeDto.getChlFee()),
                        冲减成本, dealTypesFeeDto.getNoFeeAmt(), 冲减应计利息, 利息差额);
        //if(变量.利息差额 >= 0)
        if(利息差额.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.利息差额（贷）		<-变量.利息差额
            accountAmountMap.put(BookConstant.INCOMEAMT_C, ArithmeticUtil.round(利息差额, NumberEnum.MONEY));
        }
        else {
            //金额元素.利息差额（借）								<-变量.利息差额 * -1
            accountAmountMap.put(BookConstant.INCOMEAMT_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(利息差额, new BigDecimal(-1)), NumberEnum.MONEY));
        }

        //if（变量.买卖价差>= 0）
        if(买卖价差.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.买卖价差（贷）								<-变量.买卖价差
            accountAmountMap.put(BookConstant.DSCLOSS_C, ArithmeticUtil.round(买卖价差, NumberEnum.MONEY));
        }
        else {
            //金额元素.买卖价差（借）								<-变量.买卖价差 * -1
            accountAmountMap.put(BookConstant.DSCLOSS_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(买卖价差, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        //if(变量.冲减公允价值变动 >= 0 )
        if(冲减公允价值变动.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.公允价值变动(贷) < -变量.冲减公允价值变动
            accountAmountMap.put(BookConstant.FLOAT_C, ArithmeticUtil.round(冲减公允价值变动, NumberEnum.MONEY));
            //金额元素.冲减公允价值变动(借) < -变量.冲减公允价值变动
            accountAmountMap.put(BookConstant.FLOAT_1_D, ArithmeticUtil.round(冲减公允价值变动, NumberEnum.MONEY));
        }
        else {
            //金额元素.公允价值变动(借) < -变量.冲减公允价值变动 * -1
            accountAmountMap.put(BookConstant.FLOAT_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(冲减公允价值变动, new BigDecimal(-1)), NumberEnum.MONEY));
            //金额元素.冲减公允价值变动(贷) < -变量.冲减公允价值变动 * -1
            accountAmountMap.put(BookConstant.FLOAT_1_C, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(冲减公允价值变动, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        Map<String, BigDecimal> accountAmountBCCYAMTMap = new HashMap<>();
        //本位币金额元素赋值 循环金额元素
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());// value值塞入当前交易币种
            accountAmountBCCYAMTMap.put(key + BookConstant.BCCYAMT,
                    ArithmeticUtil.round(calculationCommonService.doMultiply(accountAmountMap.get(key), changeRate),NumberEnum.MONEY));
        }
        /** 本位币 币种**/
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
        //本位币金额元素都放到金额元素map中
        accountAmountMap.putAll(accountAmountBCCYAMTMap);
        //定义		变量.利息差额(贷)_本位币 = round(变量.利息差额,共通金额小数位) * 变量.汇率
        BigDecimal 利息差额贷_本位币 = ArithmeticUtil.round(
                calculationCommonService.doMultiply(利息差额, changeRate),NumberEnum.MONEY);
        /**
        定义 变量.买卖价差_本位币	=	(金额元素.实收金额_本位币 + 金额元素.实付通道费_本位币 + 金额元素.结算手续费支出_本位币 + 金额元素.印花税支出_本位币
                + 金额元素.经手费支出_本位币 + 金额元素.证管费支出_本位币 + 金额元素.结算费支出_本位币 + 金额元素.佣金支出_本位币 + 金额元素.交易手续费支出_本位币)
                - 	(金额元素.成本金额_本位币 + 金额元素.印花税_本位币 + 金额元素.经手费_本位币 + 金额元素.证管费_本位币
                + 金额元素.结算费_本位币 + 金额元素.佣金_本位币 + 金额元素.交易手续费_本位币
                + 金额元素.结算手续费_本位币 + 变量.利息差额(贷)_本位币 + 金额元素.利息金额_本位币)
         **/
        BigDecimal 买卖价差_本位币 = ArithmeticUtil.doSub(
                ArithmeticUtil.doAdd(
                        accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.CHLAMT + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.STAMPTAX_OUT + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.HANDFEE_OUT + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.CERTIFEE_OUT + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.SETTLEFEE_OUT + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.COMMISSION_OUT + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.TRADEFEE_OUT + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.SETTLEMENTFEE_OUT + BookConstant.BCCYAMT)
                ),
                ArithmeticUtil.doAdd(
                        accountAmountMap.get(BookConstant.FACEVALUE + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.STAMPTAX + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.HANDFEE + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.CERTIFEE + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.SETTLEFEE + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.COMMISSION + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.TRADEFEE + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.SETTLEMENTFEE + BookConstant.BCCYAMT),
                        利息差额贷_本位币, accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)
                )
        );
        //if（变量.买卖价差_本位币>= 0）
        if(买卖价差_本位币.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.买卖价差（贷）_本位币								<-	变量.买卖价差_本位币
            accountAmountMap.put(BookConstant.DSCLOSS_C + BookConstant.BCCYAMT,
                    ArithmeticUtil.round(买卖价差_本位币, NumberEnum.MONEY));
        }
        //else
        else {
            //金额元素.买卖价差（借）_本位币								<-	变量.买卖价差_本位币*-1
            accountAmountMap.put(BookConstant.DSCLOSS_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(买卖价差_本位币, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        // 印花税账务明细代码 <- 变量.交易各种费用.印花税账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_STAMPTAX, dealTypesFeeDto.getStampDutyCode());
        // 经手费账务明细代码 <- 变量.交易各种费用.经手费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_HANDFEE, dealTypesFeeDto.getBrokerageCode());
        // 证管费账务明细代码 <- 变量.交易各种费用.证管费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_CERTIFEE,dealTypesFeeDto.getCertificateFeeCode());
        // 结算费账务明细代码 <- 变量.交易各种费用.结算费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEFEE,dealTypesFeeDto.getClearingFeeCode());
        // 佣金账务明细代码 <- 变量.交易各种费用.佣金账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_COMMISSION,dealTypesFeeDto.getCommissionCode());
        // 交易手续费账务明细代码 <- 变量.交易各种费用.交易手续费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_TRADEFEE, dealTypesFeeDto.getTransactionFeeCode());
        // 结算手续费账务明细代码 <- 变量.交易各种费用.结算手续费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEMENTFEE,dealTypesFeeDto.getSettlementFeeCode());
        // 通道费账务明细代码 <- 变量.交易各种费用.通道费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_ID2, dealTypesFeeDto.getChlFeeCode());

        /** modify by yanya on 20210325 科目余额计算成本、利息使用变量计算：避免同一批处理处理金额错误 start**/
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE,
                param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE_NUMBER,
                param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate())));//余额数量_本次发生前
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE+BookConstant.BCCYAMT,
                param.getJzrcbkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.FACEVALUE+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE,
                param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE+BookConstant.BCCYAMT,
                param.getJzryjlxyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.INTAMT+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        /** modify by yanya on 20210325 科目余额计算成本、利息使用变量计算：避免同一批处理处理金额错误 end**/
        /** modify by yanya on 20210326 公允价值变动使用变量计算：避免同一批处理处理金额错误 start**/
        if (冲减公允价值变动.compareTo(BigDecimal.ZERO) >= 0) {
            accountAmountMap.put(BookConstant.FLOAT_C+BookConstant.BALANCE,
                    param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_C+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_C+BookConstant.BALANCE+BookConstant.BCCYAMT,
                    param.getJzrgyjzbdMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.FLOAT_C+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        else {
            accountAmountMap.put(BookConstant.FLOAT_D+BookConstant.BALANCE,
                    param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_D+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_D+BookConstant.BALANCE+BookConstant.BCCYAMT,
                    param.getJzrgyjzbdMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.FLOAT_D+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        /** modify by yanya on 20210326 公允价值变动使用变量计算：避免同一批处理处理金额错误 end**/

        // map塞入场景要素管理中
        // 记账场景要素
        ad.setAccountSceneMap(accountSceneMap);
        // 记账金额要素
        ad.setAccountAmountMap(accountAmountMap);
        // 记账金额币种要素
        ad.setAccountCurrencyMap(accountCurrencyMap);
        // 基础科目性质
        Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
        Map<String, Object> accountNatrueBranchMap = new HashMap<>();
        accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
        accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(), accountNatrueBranchMap);
        ad.setAccountNatrueMap(accountNatrueMap);
        //add by weipeng on 20220325：增加辅助核算项配置
        ad.setSubjectMappingConfigDefault(dealNow.getSubjectMappingConfig());
        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        //全局变量的更新
        //变量.成本科目余额 								=	变量.成本科目余额 - 变量.冲减成本
        // 变量.成本科目余额 = 变量.成本科目余额 - 变量.冲减成本
        param.setVarCostSubjectAmt(ArithmeticUtil.subtract(param.getVarCostSubjectAmt(), 冲减成本));
        // 变量.成本科目数量 = 变量.成本科目数量 - 变量.冲减数量
        param.setVarCostSubjectNum(ArithmeticUtil.subtract(param.getVarCostSubjectNum(), 冲减数量));
        // 变量.公允价值变动科目余额	=	变量.溢折价科目余额 - 变量.冲减溢折价
        param.setVarGySubjectAmt(ArithmeticUtil.subtract(param.getVarGySubjectAmt(), 冲减公允价值变动));
        //变量.应计利息科目余额								=	变量.应计利息科目余额 - 变量.冲减应计利息
        param.setVarLxSubjectAmt(ArithmeticUtil.subtract(param.getVarLxSubjectAmt(), 冲减应计利息));


        //变量.结转日应计利息余额Map.put(交易明细.到期日,变量.结转日应计利息余额 - 变量.冲减应计利息)
        param.getJzryjlxyeMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate())), 冲减应计利息));
        //变量.结转日成本科目余额Map.put(交易明细.到期日,变量.结转日成本科目余额 -变量.冲减成本)
        param.getJzrcbkmyeMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate())), 冲减成本));
        //变量.结转日成本科目数量Map.put(交易明细.到期日,变量.结转日成本科目数量 - 变量.冲减数量)
        param.getJzrcbkmslMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate())), 冲减数量));
        //变量.结转日公允价值变动科目余额Map.put(交易明细.到期日,变量.结转日公允价值变动科目余额 - 变量.冲减公允价值变动)
        param.getJzrgyjzbdMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate())), 冲减公允价值变动));

        /** modify by yanya on 20210325 增加科目成本、利息本位币处理**/
        param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzryjlxyeMapBccy().get(DateUtil.format(dealNow.getMdate())),
                        accountAmountMap.get(BookConstant.INTAMT+BookConstant.BCCYAMT)));
        param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrcbkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())),
                        accountAmountMap.get(BookConstant.FACEVALUE+BookConstant.BCCYAMT)));
        /** modify by yanya on 20210326 增加公允价值变动本位币处理**/
        param.getJzrgyjzbdMapBccy().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrgyjzbdMapBccy().get(DateUtil.format(dealNow.getMdate())),
                        ArithmeticUtil.round(calculationCommonService.doMultiply(冲减公允价值变动, changeRate),NumberEnum.MONEY)));
    }

    /**
     * 公允价值损益类 行权赎回结转
     * @param rstBaseinfo
     * @param returnResult
     * @param dealNow
     * @param acctEntity
     * @param param
     */
    public void createExecJZFvtpl(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
                              FinProductDealInfoComDTO dealNow,
                              FinProductDealInfoResult acctEntity, DealParamterDto param) {
        // 账套核算参数查询
        // 定义账套参数查询参数项list
        List<String> paramCodeList = new ArrayList<String>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_债券交易费用损益计入方式.getValue());// 债券交易费用损益计入方式
        Map<String, Object> getValBokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(
                acctEntity.getBooksetId(), paramCodeList);
        // 定义 变量.费用损益计入方式
        String varSecDealFeeSubject = (String)getValBokBooksetExtMap.get(
                ValDictCodeList.账套配置表参数项_债券交易费用损益计入方式.getValue());// 债券交易费用损益计入方式
        // 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();
        // 汇率
        BigDecimal changeRate = BigDecimal.ONE;
        if(StringUtil.isNotEmpty(dealNow.getCcy()) && !dealNow.getCcy().equals(acctEntity.getStandardCcy())){
            // 调用共通汇率查询
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                    acctEntity.getStandardCcy(), dealNow.getCcy());
            if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                changeRate = currencyMarketList.get(0).getClosePrice();
            }
        }
        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());
        ad.setTableId(DictCodeListTableId.会计场景代码_公允价值损益类债券_赎回结转.getValue());
        // accountSceneMap设置
        // 备注
        accountSceneMap.put(BookConstant.BOOK_REMARK, "公允价值损益类债券资产" + DictCodeListExt3.交易方向_行权赎回结转.getCodeName() + "交易登账");
        // 公共场景赋值
        getAccountSceneMap(accountSceneMap, dealNow, rstBaseinfo);
        //定义		变量.结转日应计利息余额 = 变量.结转日应计利息余额Map.get(交易明细.到期日)
        BigDecimal 结转日应计利息余额 = param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日成本科目余额 = 变量.结转日成本科目余额Map.get(交易明细.到期日）
        BigDecimal 结转日成本科目余额 = param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日成本科目数量 = 变量.结转日成本科目数量Map.get(交易明细.到期日)
        BigDecimal 结转日成本科目数量 = param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日公允价值变动科目余额 = 变量.结转日公允价值变动科目余额Map.get(交易明细.到期日)
        BigDecimal 结转日公允价值变动科目余额 = param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate()));
        List<SubjectBalance> listSubjectBalance = new ArrayList<SubjectBalance>();
        // 科目余额查询参数
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        searchDao.setBooksetId(acctEntity.getBooksetId());
        searchDao.setBokDetailId(acctEntity.getBokDetailId());
        searchDao.setHappenDate(addDate(dealNow.getMdate(), -1));
        searchDao.setBookDate(acctEntity.getBookDate());
        //IF 变量.结转日应计利息余额Map.get(交易明细.到期日) == NULL
        if(结转日应计利息余额 == null) {
            //查询交易的到期日对应的应计利息
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.应计利息余额 = 科目余额
                结转日应计利息余额 = listSubjectBalance.get(0).getBalance();
                /** modify by yanya on 20210325 增加利息本位币处理**/
                param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalance.get(0).getbAmt());
            }
            else {
                结转日应计利息余额 = BigDecimal.ZERO;
                /** modify by yanya on 20210325 增加利息本位币处理**/
                param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzryjlxyeMap().put(DateUtil.format(dealNow.getMdate()), 结转日应计利息余额);
        }
        //IF 变量.结转日成本科目余额Map.get(交易明细.到期日） IS NULL
        if(结转日成本科目余额 == null) {
            searchDao.setBaseSubjectNatureList(
                    new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.结转日成本科目余额 = 科目余额
                结转日成本科目余额 = listSubjectBalance.get(0).getBalance();
                // 定义 变量.结转日成本科目数量 = 数量
                结转日成本科目数量 = listSubjectBalance.get(0).getNum();
                /** modify by yanya on 20210325 增加科目成本本位币处理**/
                param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalance.get(0).getbAmt());
            }
            else {
                结转日成本科目余额 = BigDecimal.ZERO;
                结转日成本科目数量 = BigDecimal.ZERO;
                /** modify by yanya on 20210325 增加科目成本本位币处理**/
                param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzrcbkmyeMap().put(DateUtil.format(dealNow.getMdate()), 结转日成本科目余额);
            param.getJzrcbkmslMap().put(DateUtil.format(dealNow.getMdate()), 结转日成本科目数量);
        }
        //BigDecimal varGyjzbdye = BigDecimal.ZERO;
        if(结转日公允价值变动科目余额 == null) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue())));
            List<SubjectBalance> listSubjectBalanceEx = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalanceEx != null && listSubjectBalanceEx.size() > 0) {
                // 变量.结转日公允价值变动科目余额 = 科目余额
                结转日公允价值变动科目余额 = listSubjectBalanceEx.get(0).getBalance();
                /** modify by yanya on 20210326 公允价值变动本位币处理**/
                param.getJzrgyjzbdMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalanceEx.get(0).getbAmt());
            }
            else {
                结转日公允价值变动科目余额 = BigDecimal.ZERO;
                /** modify by yanya on 20210326 公允价值变动本位币处理**/
                param.getJzrgyjzbdMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzrgyjzbdMap().put(DateUtil.format(dealNow.getMdate()), 结转日公允价值变动科目余额);
        }
        if (!DictCodeListExt1.估值交易市场_上交所.getValue().equals(rstBaseinfo.getTradeMarket())
                && !DictCodeListExt1.估值交易市场_深交所.getValue().equals(rstBaseinfo.getTradeMarket())) {
            // 买入场景
            // 调用账务共通设计->【凭证最新账务查询】
            List<BookLatestInfoDto> listBookLatestInfoDto = getBookLatestInfo(dealNow.getInvAim(),true, dealNow, acctEntity);
            // LOOP 变量.买入账务明细列表 -> 变量.账务明细
            if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                for (BookLatestInfoDto var : listBookLatestInfoDto) {
                    // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.结转日成本科目数量 -= 变量.账务明细.发生数量
                        结转日成本科目数量 = ArithmeticUtil.subtract(结转日成本科目数量, var.getHappenNumber());
                        // 变量.结转日成本科目余额 -= 变量.账务明细.原币发生额
                        结转日成本科目余额 = ArithmeticUtil.subtract(结转日成本科目余额, var.getoHappenAmt());
                    }
                    // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_公允价值变动.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日公允价值变动科目余额 -= 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.subtract(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日公允价值变动科目余额 += 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.doAdd(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                    }
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue().equals(var.getBaseSubjectNature())) {
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日应计利息余额 -= 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.subtract(结转日应计利息余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日应计利息余额 += 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.doAdd(结转日应计利息余额, var.getoHappenAmt());
                        }
                    }
                }
            }
            // 卖出场景
            // 调用账务共通设计->【凭证最新账务查询】
            listBookLatestInfoDto = getBookLatestInfo(dealNow.getInvAim(), false, dealNow, acctEntity);
            // LOOP 变量.卖出账务明细列表 -> 变量.账务明细
            if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                for (BookLatestInfoDto var : listBookLatestInfoDto) {
                    // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.结转日成本科目数量 += 变量.账务明细.发生数量
                        结转日成本科目数量 = ArithmeticUtil.doAdd(结转日成本科目数量, var.getHappenNumber());
                        // 变量.结转日成本科目余额 += 变量.账务明细.原币发生额
                        结转日成本科目余额 = ArithmeticUtil.doAdd(结转日成本科目余额, var.getoHappenAmt());
                    }
                    // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_公允价值变动.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日公允价值变动科目余额 -= 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.subtract(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日公允价值变动科目余额 += 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.doAdd(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                    }
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue().equals(var.getBaseSubjectNature())) {
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日应计利息余额 -= 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.subtract(结转日应计利息余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日应计利息余额 += 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.doAdd(结转日应计利息余额, var.getoHappenAmt());
                        }
                    }
                }
            }
        }
        //定义 		变量.冲减公允价值变动			= 变量.结转日公允价值变动科目余额
        BigDecimal 冲减公允价值变动 = 结转日公允价值变动科目余额;
        //定义 		变量.冲减应计利息				= 变量.结转日应计利息科目余额
        BigDecimal 冲减应计利息 = 结转日应计利息余额;
        //定义 		变量.冲减数量								=		变量.结转日成本科目数量
        BigDecimal 冲减数量 = 结转日成本科目数量;
        //定义 		变量.冲减成本								=		变量.结转日成本科目余额
        BigDecimal 冲减成本 = 结转日成本科目余额;
        //定义 变量.交易金额_清算款	=		变量.结转日应计利息科目余额 + 变量.结转日成本科目余额
        BigDecimal 交易金额清算款 = ArithmeticUtil.doAdd(结转日应计利息余额, 结转日成本科目余额);
        //变量.买卖价差	=		变量.交易金额_清算款 - 变量.冲减成本 - 变量.冲减应计利息
        BigDecimal 买卖价差 =  ArithmeticUtil.doSub(交易金额清算款, 冲减成本, 冲减应计利息);

        // 实收金额
        accountAmountMap.put(BookConstant.DEALAMT, ArithmeticUtil.round(交易金额清算款, NumberEnum.MONEY));
        //成本金额 = 变量.冲减成本
        accountAmountMap.put(BookConstant.FACEVALUE, ArithmeticUtil.round(冲减成本, NumberEnum.MONEY));
        //应计利息 = 冲减应计利息
        accountAmountMap.put(BookConstant.INTAMT, ArithmeticUtil.round(冲减应计利息, NumberEnum.MONEY));
        //if(变量.冲减公允价值变动 >= 0 )
        if(冲减公允价值变动.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.公允价值变动(贷) < -变量.冲减公允价值变动
            accountAmountMap.put(BookConstant.FLOAT_C, ArithmeticUtil.round(冲减公允价值变动, NumberEnum.MONEY));
            //金额元素.冲减公允价值变动(借) < -变量.冲减公允价值变动
            accountAmountMap.put(BookConstant.FLOAT_1_D, ArithmeticUtil.round(冲减公允价值变动, NumberEnum.MONEY));
        }
        else {
            //金额元素.公允价值变动(借) < -变量.冲减公允价值变动 * -1
            accountAmountMap.put(BookConstant.FLOAT_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(冲减公允价值变动, new BigDecimal(-1)), NumberEnum.MONEY));
            //金额元素.冲减公允价值变动(贷) < -变量.冲减公允价值变动 * -1
            accountAmountMap.put(BookConstant.FLOAT_1_C, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(冲减公允价值变动, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        //if（变量.买卖价差>= 0）
        if(买卖价差.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.买卖价差（贷）								<-变量.买卖价差
            accountAmountMap.put(BookConstant.DSCLOSS_C, ArithmeticUtil.round(买卖价差, NumberEnum.MONEY));
        }
        else {
            //金额元素.买卖价差（借）								<-变量.买卖价差 * -1
            accountAmountMap.put(BookConstant.DSCLOSS_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(买卖价差, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        Map<String, BigDecimal> accountAmountBCCYAMTMap = new HashMap<>();
        //本位币金额元素赋值 循环金额元素
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());// value值塞入当前交易币种
            accountAmountBCCYAMTMap.put(key + BookConstant.BCCYAMT,
                    ArithmeticUtil.round(calculationCommonService.doMultiply(accountAmountMap.get(key), changeRate),NumberEnum.MONEY));
        }
        /** 本位币 币种**/
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
        //本位币金额元素都放到金额元素map中
        accountAmountMap.putAll(accountAmountBCCYAMTMap);
        //定义 变量.买卖价差_本位币	=	金额元素.实收金额_本位币 - 金额元素.成本金额_本位币 - 金额元素.应计利息_本位币

        BigDecimal 买卖价差_本位币 = ArithmeticUtil.doSub(
                accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT),
                accountAmountMap.get(BookConstant.FACEVALUE + BookConstant.BCCYAMT),
                accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)
        );
        //if（变量.买卖价差_本位币>= 0）
        if(买卖价差_本位币.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.买卖价差（贷）_本位币								<-	变量.买卖价差_本位币
            accountAmountMap.put(BookConstant.DSCLOSS_C + BookConstant.BCCYAMT,
                    ArithmeticUtil.round(买卖价差_本位币, NumberEnum.MONEY));
        }
        //else
        else {
            //金额元素.买卖价差（借）_本位币								<-	变量.买卖价差_本位币*-1
            accountAmountMap.put(BookConstant.DSCLOSS_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(买卖价差_本位币, new BigDecimal(-1)), NumberEnum.MONEY));
        }

        // modify by yanya on 20210326 特殊处理赎回结转数量
        accountSceneMap.put(BookConstant.HAPPEN_NUMBER, ArithmeticUtil.round(冲减数量, NumberEnum.QTY));

        /** modify by yanya on 20210325 科目余额计算成本、利息使用变量计算：避免同一批处理处理金额错误 start**/
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE,
                param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE_NUMBER,
                param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate())));//余额数量_本次发生前
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE+BookConstant.BCCYAMT,
                param.getJzrcbkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.FACEVALUE+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE,
                param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE+BookConstant.BCCYAMT,
                param.getJzryjlxyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.INTAMT+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        /** modify by yanya on 20210325 科目余额计算成本、利息使用变量计算：避免同一批处理处理金额错误 end**/
        /** modify by yanya on 20210326 公允价值变动使用变量计算：避免同一批处理处理金额错误 start**/
        if (冲减公允价值变动.compareTo(BigDecimal.ZERO) >= 0) {
            accountAmountMap.put(BookConstant.FLOAT_C+BookConstant.BALANCE,
                    param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_C+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_C+BookConstant.BALANCE+BookConstant.BCCYAMT,
                    param.getJzrgyjzbdMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.FLOAT_C+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        else {
            accountAmountMap.put(BookConstant.FLOAT_D+BookConstant.BALANCE,
                    param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_D+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_D+BookConstant.BALANCE+BookConstant.BCCYAMT,
                    param.getJzrgyjzbdMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.FLOAT_D+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        /** modify by yanya on 20210326 公允价值变动使用变量计算：避免同一批处理处理金额错误 end**/

        // map塞入场景要素管理中
        // 记账场景要素
        ad.setAccountSceneMap(accountSceneMap);
        // 记账金额要素
        ad.setAccountAmountMap(accountAmountMap);
        // 记账金额币种要素
        ad.setAccountCurrencyMap(accountCurrencyMap);
        // 基础科目性质
        Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
        Map<String, Object> accountNatrueBranchMap = new HashMap<>();
        accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
        accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(), accountNatrueBranchMap);
        ad.setAccountNatrueMap(accountNatrueMap);
        //add by weipeng on 20220325：增加辅助核算项配置
        ad.setSubjectMappingConfigDefault(dealNow.getSubjectMappingConfig());
        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        //全局变量的更新
        //变量.成本科目余额 								=	变量.成本科目余额 - 变量.冲减成本
        // 变量.成本科目余额 = 变量.成本科目余额 - 变量.冲减成本
        param.setVarCostSubjectAmt(ArithmeticUtil.subtract(param.getVarCostSubjectAmt(), 冲减成本));
        // 变量.成本科目数量 = 变量.成本科目数量 - 变量.冲减数量
        param.setVarCostSubjectNum(ArithmeticUtil.subtract(param.getVarCostSubjectNum(), 冲减数量));
        // 变量.公允价值变动科目余额 = 变量.溢折价科目余额 - 变量.冲减公允价值变动
        param.setVarGySubjectAmt(ArithmeticUtil.subtract(param.getVarGySubjectAmt(), 冲减公允价值变动));
        //变量.结转日应计利息余额Map.put(交易明细.到期日,变量.结转日应计利息余额 - 变量.冲减应计利息)
        param.getJzryjlxyeMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate())), 冲减应计利息));
        //变量.结转日成本科目余额Map.put(交易明细.到期日,变量.结转日成本科目余额 -变量.冲减成本)
        param.getJzrcbkmyeMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate())), 冲减成本));
        //变量.结转日成本科目数量Map.put(交易明细.到期日,变量.结转日成本科目数量 - 变量.冲减数量)
        param.getJzrcbkmslMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate())), 冲减数量));
        //变量.结转日公允价值变动科目余额Map.put(交易明细.到期日,变量.结转日公允价值变动科目余额 - 变量.冲减公允价值变动)
        param.getJzrgyjzbdMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate())), 冲减公允价值变动));

        /** modify by yanya on 20210325 增加科目成本、利息本位币处理**/
        param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzryjlxyeMapBccy().get(DateUtil.format(dealNow.getMdate())),
                        accountAmountMap.get(BookConstant.INTAMT+BookConstant.BCCYAMT)));
        param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrcbkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())),
                        accountAmountMap.get(BookConstant.FACEVALUE+BookConstant.BCCYAMT)));
        /** modify by yanya on 20210326 增加公允价值变动本位币处理**/
        param.getJzrgyjzbdMapBccy().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrgyjzbdMapBccy().get(DateUtil.format(dealNow.getMdate())),
                        ArithmeticUtil.round(calculationCommonService.doMultiply(冲减公允价值变动, changeRate),NumberEnum.MONEY)));
    }


    /**
     * 摊余成本类 行权回售申请
     * @param rstBaseinfo
     * @param returnResult
     * @param dealNow
     * @param acctEntity
     * @param param
     */
    public void createExecHSAC(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
                                   FinProductDealInfoComDTO dealNow,
                                   FinProductDealInfoResult acctEntity, DealParamterDto param) {
        // 账套核算参数查询
        // 定义账套参数查询参数项list
        List<String> paramCodeList = new ArrayList<String>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_还本收息冲抵减值准备处理日.getValue());
        Map<String, Object> getValBokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(
                acctEntity.getBooksetId(), paramCodeList);
        String 还本收息冲抵减值准备处理日 = (String)getValBokBooksetExtMap.get(
                ValDictCodeList.账套配置表参数项_还本收息冲抵减值准备处理日.getValue());// 账套配置表参数项_还本收息冲抵减值准备处理日
        // 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();
        // 汇率
        BigDecimal changeRate = BigDecimal.ONE;
        if(StringUtil.isNotEmpty(dealNow.getCcy()) && !dealNow.getCcy().equals(acctEntity.getStandardCcy())){
            // 调用共通汇率查询
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                    acctEntity.getStandardCcy(), dealNow.getCcy());
            if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                changeRate = currencyMarketList.get(0).getClosePrice();
            }
        }
        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());
        ad.setTableId(DictCodeListTableId.会计场景代码_摊余成本债券_回售申请.getValue());
        // accountSceneMap设置
        // 备注
        accountSceneMap.put(BookConstant.BOOK_REMARK, "摊余成本类债券资产" + DictCodeListExt3.交易方向_行权回售申请.getCodeName() + "交易登账");
        // 公共场景赋值
        getAccountSceneMap(accountSceneMap, dealNow, rstBaseinfo);
        //定义		变量.结转日应计利息余额 = 变量.结转日应计利息余额Map.get(交易明细.到期日)
        BigDecimal 结转日应计利息余额 = param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日成本科目余额 = 变量.结转日成本科目余额Map.get(交易明细.到期日）
        BigDecimal 结转日成本科目余额 = param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日成本科目数量 = 变量.结转日成本科目数量Map.get(交易明细.到期日)
        BigDecimal 结转日成本科目数量 = param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日溢折价科目余额 = 变量.结转日溢折价科目余额Map.get(交易明细.到期日)
        BigDecimal 结转日溢折价科目余额 = param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate()));
        List<SubjectBalance> listSubjectBalance = new ArrayList<SubjectBalance>();
        // 科目余额查询参数
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        searchDao.setBooksetId(acctEntity.getBooksetId());
        searchDao.setBokDetailId(acctEntity.getBokDetailId());
        searchDao.setHappenDate(addDate(dealNow.getMdate(), -1));
        searchDao.setBookDate(acctEntity.getBookDate());
        //IF 变量.结转日应计利息余额Map.get(交易明细.到期日) == NULL
        if(结转日应计利息余额 == null) {
            //查询交易的到期日对应的应计利息
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.应计利息余额 = 科目余额
                结转日应计利息余额 = listSubjectBalance.get(0).getBalance();
                /** modify by yanya on 20210325 增加利息本位币处理**/
                param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalance.get(0).getbAmt());
            }
            else {
                结转日应计利息余额 = BigDecimal.ZERO;
                /** modify by yanya on 20210325 增加利息本位币处理**/
                param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzryjlxyeMap().put(DateUtil.format(dealNow.getMdate()), 结转日应计利息余额);
        }
        //IF 变量.结转日成本科目余额Map.get(交易明细.到期日） IS NULL
        if(结转日成本科目余额 == null) {
            searchDao.setBaseSubjectNatureList(
                    new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.结转日成本科目余额 = 科目余额
                结转日成本科目余额 = listSubjectBalance.get(0).getBalance();
                // 定义 变量.结转日成本科目数量 = 数量
                结转日成本科目数量 = listSubjectBalance.get(0).getNum();
                /** modify by yanya on 20210325 增加科目成本本位币处理**/
                param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalance.get(0).getbAmt());
            }
            else {
                结转日成本科目余额 = BigDecimal.ZERO;
                结转日成本科目数量 = BigDecimal.ZERO;
                /** modify by yanya on 20210325 增加科目成本、利息本位币处理**/
                param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzrcbkmyeMap().put(DateUtil.format(dealNow.getMdate()), 结转日成本科目余额);
            param.getJzrcbkmslMap().put(DateUtil.format(dealNow.getMdate()), 结转日成本科目数量);
        }
        //BigDecimal varGyjzbdye = BigDecimal.ZERO;
        if(结转日溢折价科目余额 == null) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_利息调整.getValue())));
            List<SubjectBalance> listSubjectBalanceEx = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalanceEx != null && listSubjectBalanceEx.size() > 0) {
                // 变量.结转日公允价值变动科目余额 = 科目余额
                结转日溢折价科目余额 = listSubjectBalanceEx.get(0).getBalance();
                /** modify by yanya on 20210326 溢折价科目本位币处理**/
                param.getJzryzjkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalanceEx.get(0).getbAmt());
            }
            else {
                结转日溢折价科目余额 = BigDecimal.ZERO;
                /** modify by yanya on 20210326 溢折价科目本位币处理**/
                param.getJzryzjkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzryzjkmyeMap().put(DateUtil.format(dealNow.getMdate()), 结转日溢折价科目余额);
        }
        if (!DictCodeListExt1.估值交易市场_上交所.getValue().equals(rstBaseinfo.getTradeMarket())
                && !DictCodeListExt1.估值交易市场_深交所.getValue().equals(rstBaseinfo.getTradeMarket())) {
            // 买入场景
            // 调用账务共通设计->【凭证最新账务查询】
            List<BookLatestInfoDto> listBookLatestInfoDto = getBookLatestInfo(dealNow.getInvAim(),true, dealNow, acctEntity);
            // LOOP 变量.买入账务明细列表 -> 变量.账务明细
            if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                for (BookLatestInfoDto var : listBookLatestInfoDto) {
                    // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.结转日成本科目数量 -= 变量.账务明细.发生数量
                        结转日成本科目数量 = ArithmeticUtil.subtract(结转日成本科目数量, var.getHappenNumber());
                        // 变量.结转日成本科目余额 -= 变量.账务明细.原币发生额
                        结转日成本科目余额 = ArithmeticUtil.subtract(结转日成本科目余额, var.getoHappenAmt());
                    }
                    // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.摊余成本类_利息调整.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日公允价值变动科目余额 -= 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.subtract(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日公允价值变动科目余额 += 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.doAdd(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                    }
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue().equals(var.getBaseSubjectNature())) {
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日应计利息余额 -= 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.subtract(结转日应计利息余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日应计利息余额 += 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.doAdd(结转日应计利息余额, var.getoHappenAmt());
                        }
                    }
                }
            }
            // 卖出场景
            // 调用账务共通设计->【凭证最新账务查询】
            listBookLatestInfoDto = getBookLatestInfo(dealNow.getInvAim(), false, dealNow, acctEntity);
            // LOOP 变量.卖出账务明细列表 -> 变量.账务明细
            if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                for (BookLatestInfoDto var : listBookLatestInfoDto) {
                    // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.结转日成本科目数量 += 变量.账务明细.发生数量
                        结转日成本科目数量 = ArithmeticUtil.doAdd(结转日成本科目数量, var.getHappenNumber());
                        // 变量.结转日成本科目余额 += 变量.账务明细.原币发生额
                        结转日成本科目余额 = ArithmeticUtil.doAdd(结转日成本科目余额, var.getoHappenAmt());
                    }
                    // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.摊余成本类_利息调整.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日公允价值变动科目余额 -= 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.subtract(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日公允价值变动科目余额 += 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.doAdd(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                    }
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue().equals(var.getBaseSubjectNature())) {
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日应计利息余额 -= 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.subtract(结转日应计利息余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日应计利息余额 += 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.doAdd(结转日应计利息余额, var.getoHappenAmt());
                        }
                    }
                }
            }
        }
        //定义 		变量.冲减溢折价		=		变量.结转日溢折价科目余额  * 交易明细.交易份额 / 变量.成本科目数量
        BigDecimal 冲减溢折价 = ArithmeticUtil.divide(
                ArithmeticUtil.multiply(结转日溢折价科目余额, dealNow.getShareAmt()), param.getVarCostSubjectNum());
        //定义 		变量.冲减应计利息		=		变量.结转日应计利息科目余额 * 交易明细.交易份额 / 变量.成本科目数量
        BigDecimal 冲减应计利息 = ArithmeticUtil.divide(
                ArithmeticUtil.multiply(结转日应计利息余额, dealNow.getShareAmt()), param.getVarCostSubjectNum());
        //定义 		变量.冲减成本		=		交易明细.交易份额 * 交易明细.单位面值
        BigDecimal 冲减成本 = ArithmeticUtil.multiply(dealNow.getShareAmt(), dealNow.getParValue());
        //定义 		变量.冲减数量		=		交易明细.交易份额
        BigDecimal 冲减数量 = dealNow.getShareAmt();
        //定义 		变量.利息差额		=		0
        BigDecimal 利息差额 = BigDecimal.ZERO;
        //定义 		变量.买卖价差		=		0
        BigDecimal 买卖价差 = BigDecimal.ZERO;
        //IF 变量.结转日成本科目数量 - 变量.冲减数量 == 0
        if(ArithmeticUtil.doSub(结转日成本科目数量, 冲减数量).compareTo(BigDecimal.ZERO) == 0) {
            //变量.冲减溢折价							=	变量.结转日溢折价科目余额
            冲减溢折价 = 结转日溢折价科目余额;
            //变量.冲减应计利息							=	变量.结转日应计利息科目余额
            冲减应计利息 = 结转日应计利息余额;
        }
        if (StringUtil.equals(DictCodeList.息票品种_零息.getValue(), rstBaseinfo.getCouponSpecies())
                || StringUtil.equals(DictCodeList.息票品种_贴现.getValue(),rstBaseinfo.getCouponSpecies())) {
            冲减应计利息 = BigDecimal.ZERO;
        }
        else {
            //变量.利息差额	=		交易明细.利息总额 - 变量.冲减应计利息
            利息差额 = ArithmeticUtil.doSub(dealNow.getIntAmt(), 冲减应计利息);
        }
        BigDecimal 冲抵减值金额 = BigDecimal.ZERO;
        //IF 数据字典：还本收息冲抵减值准备处理日.理论日 eq 变量.还本收息冲抵减值准备处理日
        if(StringUtil.equals(DictCodeListExt3.还本收息冲抵减值准备处理日_理论日.getValue(), 还本收息冲抵减值准备处理日)) {
           // 调用减值相关共通-冲减减值计算(卖出)
            LossAmtInputDto dto = this.getLossAmtInputDto(acctEntity, dealNow,
                    DictCodeListExt1.现金流一级类型_利息.getValue(),
                    ArithmeticUtil.doAdd(冲减应计利息, 冲减成本),
                    param.getVarJzzbSubjectAmt(),
                    /** modify by yanya on 20210326发生数量**/
                    dealNow.getShareAmt(),
                    //当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
                   ArithmeticUtil.doAdd(param.getVarCostSubjectAmt(),
                                    param.getVarLxSubjectAmt(),
                                    param.getVarQtyskBjSubjectSumAmt(),
                                    param.getVarQtyskLxSubjectSumAmt(),
                                    param.getVarYqfxSubjectSumAmt()),
                    param.getVarCostSubjectNum());

            /** modify by yanya on 20210326调用卖出场景**/
            冲抵减值金额 = lossCommonBatchService.calcLossAmtForDeal(dto);
        }
        //调用 共通【费用处理共通】  参考本设计书sheet【费用处理共通】
        DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
                dealNow.getValTradeFeeDetailList(), dealNow.getProdId(), dealNow.getPortfolioId());
        // 印花税
        accountAmountMap.put(BookConstant.STAMPTAX, dealTypesFeeDto.getStampDuty());
        // 经手费
        accountAmountMap.put(BookConstant.HANDFEE, dealTypesFeeDto.getBrokerage());
        // 证管费
        accountAmountMap.put(BookConstant.CERTIFEE, dealTypesFeeDto.getCertificateFee());
        // 结算费
        accountAmountMap.put(BookConstant.SETTLEFEE, dealTypesFeeDto.getClearingFee());
        // 佣金
        accountAmountMap.put(BookConstant.COMMISSION, dealTypesFeeDto.getCommission());
        // 交易手续费
        accountAmountMap.put(BookConstant.TRADEFEE, dealTypesFeeDto.getTransactionFee());
        // 结算手续费
        accountAmountMap.put(BookConstant.SETTLEMENTFEE, dealTypesFeeDto.getSettlementFee());
        //实付通道费
        accountAmountMap.put(BookConstant.CHLAMT, dealTypesFeeDto.getChlFee());
        // 实收金额
        accountAmountMap.put(BookConstant.DEALAMT, ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));
        //成本金额 = 变量.冲减成本
        accountAmountMap.put(BookConstant.FACEVALUE, ArithmeticUtil.round(冲减成本, NumberEnum.MONEY));
        //应计利息 = 冲减应计利息
        accountAmountMap.put(BookConstant.INTAMT, ArithmeticUtil.round(冲减应计利息, NumberEnum.MONEY));

        //减值准备  <- 变量.冲抵减值金额 * -1
        accountAmountMap.put(BookConstant.IMPAMT,
                ArithmeticUtil.round(ArithmeticUtil.multiply(冲抵减值金额,new BigDecimal(-1)), NumberEnum.MONEY));

        /**变量.买卖价差 =	交易明细.交易金额 - (变量.冲减成本 + 变量.冲减溢折价 ) - 变量.冲减应计利息 - 变量.交易各种费用.不随交易费用
                + 各种变量.交易各种费用.通道费 - 变量.利息差额**/
        买卖价差 = ArithmeticUtil.doSub(
                    ArithmeticUtil.doAdd(
                            dealNow.getTradeAmt(),
                            dealTypesFeeDto.getChlFee()
                ),
                冲减成本, 冲减溢折价, 冲减应计利息, dealTypesFeeDto.getNoFeeAmt(), 利息差额);

        //if(变量.利息差额 >= 0)
        if(利息差额.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.利息差额（贷）		<-变量.利息差额
            accountAmountMap.put(BookConstant.INCOMEAMT_C, ArithmeticUtil.round(利息差额, NumberEnum.MONEY));
        }
        else {
            //金额元素.利息差额（借）								<-变量.利息差额 * -1
            accountAmountMap.put(BookConstant.INCOMEAMT_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(利息差额, new BigDecimal(-1)), NumberEnum.MONEY));
        }

        //if（变量.买卖价差>= 0）
        if(买卖价差.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.买卖价差（贷）								<-变量.买卖价差
            accountAmountMap.put(BookConstant.DSCLOSS_C, ArithmeticUtil.round(买卖价差, NumberEnum.MONEY));
        }
        else {
            //金额元素.买卖价差（借）								<-变量.买卖价差 * -1
            accountAmountMap.put(BookConstant.DSCLOSS_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(买卖价差, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        //if(变量.冲减公允价值变动 >= 0 )
        if(冲减溢折价.compareTo(BigDecimal.ZERO) >= 0) {
            // 剩余溢折价(贷) <- 变量.冲减溢折价
            accountAmountMap.put(BookConstant.DSCINC_C,
                    ArithmeticUtil.round(冲减溢折价, NumberEnum.MONEY));
        }
        else {
            // 剩余溢折价(借) <- 变量.冲减溢折价 * -1
            accountAmountMap.put(BookConstant.DSCINC_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(冲减溢折价, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        Map<String, BigDecimal> accountAmountBCCYAMTMap = new HashMap<>();
        //本位币金额元素赋值 循环金额元素
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());// value值塞入当前交易币种
            accountAmountBCCYAMTMap.put(key + BookConstant.BCCYAMT,
                    ArithmeticUtil.round(calculationCommonService.doMultiply(accountAmountMap.get(key), changeRate),NumberEnum.MONEY));
        }
        /** 本位币 币种**/
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
        //本位币金额元素都放到金额元素map中
        accountAmountMap.putAll(accountAmountBCCYAMTMap);
        //accountAmountMap再次设置如下：
        //定义		变量.利息差额(贷)_本位币 = round(变量.利息差额,共通金额小数位) * 变量.汇率																																		保留金额元素小数位数
        BigDecimal 利息差额贷_本位币 = ArithmeticUtil.round(
                calculationCommonService.doMultiply(利息差额, changeRate),NumberEnum.MONEY);
        //定义		变量.冲减溢折价（贷）_本位币 = round(变量.冲减溢折价,共通金额小数位) * 变量.汇率
        BigDecimal 冲减溢折价贷_本位币 = ArithmeticUtil.round(
                calculationCommonService.doMultiply(冲减溢折价, changeRate),NumberEnum.MONEY);
        // 保留金额元素小数位数
        /**定义 变量.买卖价差_本位币 =	(金额元素.实收金额_本位币 + 金额元素.实付通道费_本位币)
                - (金额元素.面值_本位币 + 金额元素.印花税_本位币 + 金额元素.经手费_本位币 + 金额元素.证管费_本位币
                + 金额元素.结算费_本位币 + 金额元素.佣金_本位币 + 金额元素.交易手续费_本位币
                + 金额元素.结算手续费_本位币 + 金额元素.冲减溢折价（贷）_本位币 + 变量.利息差额(贷)_本位币 + 金额元素.利息金额_本位币)
         **/

        BigDecimal 买卖价差_本位币 = ArithmeticUtil.doSub(
                ArithmeticUtil.doAdd(
                        accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.CHLAMT + BookConstant.BCCYAMT)
                ),
                ArithmeticUtil.doAdd(
                        accountAmountMap.get(BookConstant.FACEVALUE + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.STAMPTAX + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.HANDFEE + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.CERTIFEE + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.SETTLEFEE + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.COMMISSION + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.TRADEFEE + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.SETTLEMENTFEE + BookConstant.BCCYAMT),
                        利息差额贷_本位币, 冲减溢折价贷_本位币, accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)
                )
        );
        //if（变量.买卖价差_本位币>= 0）
        if(买卖价差_本位币.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.买卖价差（贷）_本位币								<-	变量.买卖价差_本位币
            accountAmountMap.put(BookConstant.DSCLOSS_C + BookConstant.BCCYAMT,
                    ArithmeticUtil.round(买卖价差_本位币, NumberEnum.MONEY));
        }
        //else
        else {
            //金额元素.买卖价差（借）_本位币								<-	变量.买卖价差_本位币*-1
            accountAmountMap.put(BookConstant.DSCLOSS_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(买卖价差_本位币, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        // 印花税账务明细代码 <- 变量.交易各种费用.印花税账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_STAMPTAX, dealTypesFeeDto.getStampDutyCode());
        // 经手费账务明细代码 <- 变量.交易各种费用.经手费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_HANDFEE, dealTypesFeeDto.getBrokerageCode());
        // 证管费账务明细代码 <- 变量.交易各种费用.证管费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_CERTIFEE,dealTypesFeeDto.getCertificateFeeCode());
        // 结算费账务明细代码 <- 变量.交易各种费用.结算费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEFEE,dealTypesFeeDto.getClearingFeeCode());
        // 佣金账务明细代码 <- 变量.交易各种费用.佣金账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_COMMISSION,dealTypesFeeDto.getCommissionCode());
        // 交易手续费账务明细代码 <- 变量.交易各种费用.交易手续费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_TRADEFEE, dealTypesFeeDto.getTransactionFeeCode());
        // 结算手续费账务明细代码 <- 变量.交易各种费用.结算手续费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEMENTFEE,dealTypesFeeDto.getSettlementFeeCode());
        // 通道费账务明细代码 <- 变量.交易各种费用.通道费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_ID2, dealTypesFeeDto.getChlFeeCode());

        /** modify by yanya on 20210325 科目余额计算成本、利息使用变量计算：避免同一批处理处理金额错误 start**/
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE,
                param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE_NUMBER,
                param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate())));//余额数量_本次发生前
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE+BookConstant.BCCYAMT,
                param.getJzrcbkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.FACEVALUE+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE,
                param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE+BookConstant.BCCYAMT,
                param.getJzryjlxyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.INTAMT+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE, param.getVarJzzbSubjectAmt());//科目余额_本次发生前
        accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
        accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE+BookConstant.BCCYAMT, param.getVarJzzbSubjectAmtBccy());//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.IMPAMT+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        /** modify by yanya on 20210325 科目余额计算成本、利息使用变量计算：避免同一批处理处理金额错误 end**/
        /** modify by yanya on 20210326 溢折价使用变量计算：避免同一批处理处理金额错误 start**/
        if (冲减溢折价.compareTo(BigDecimal.ZERO) >= 0) {
            accountAmountMap.put(BookConstant.DSCINC_C+BookConstant.BALANCE,
                    param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_C+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_C+BookConstant.BALANCE+BookConstant.BCCYAMT,
                    param.getJzryzjkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.DSCINC_C+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        else {
            accountAmountMap.put(BookConstant.DSCINC_D+BookConstant.BALANCE,
                    param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_D+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_D+BookConstant.BALANCE+BookConstant.BCCYAMT,
                    param.getJzryzjkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.DSCINC_D+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        /** modify by yanya on 20210326 溢折价使用变量计算：避免同一批处理处理金额错误 end**/

        // map塞入场景要素管理中
        // 记账场景要素
        ad.setAccountSceneMap(accountSceneMap);
        // 记账金额要素
        ad.setAccountAmountMap(accountAmountMap);
        // 记账金额币种要素
        ad.setAccountCurrencyMap(accountCurrencyMap);
        // 基础科目性质
        Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
        Map<String, Object> accountNatrueBranchMap = new HashMap<>();
        accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
        accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(), accountNatrueBranchMap);
        ad.setAccountNatrueMap(accountNatrueMap);
        //add by weipeng on 20220325：增加辅助核算项配置
        ad.setSubjectMappingConfigDefault(dealNow.getSubjectMappingConfig());
        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        //全局变量的更新
        //变量.成本科目余额 								=	变量.成本科目余额 - 变量.冲减成本
        // 变量.成本科目余额 = 变量.成本科目余额 - 变量.冲减成本
        param.setVarCostSubjectAmt(ArithmeticUtil.subtract(param.getVarCostSubjectAmt(), 冲减成本));
        // 变量.成本科目数量 = 变量.成本科目数量 - 变量.冲减数量
        param.setVarCostSubjectNum(ArithmeticUtil.subtract(param.getVarCostSubjectNum(), 冲减数量));
        // 变量.公允价值变动科目余额 = 变量.溢折价科目余额 - 变量.冲减公允价值变动
        param.setVarYzjSubjectAmt(ArithmeticUtil.subtract(param.getVarYzjSubjectAmt(), 冲减溢折价));
        //变量.应计利息科目余额								=	变量.应计利息科目余额 - 变量.冲减应计利息
        param.setVarLxSubjectAmt(ArithmeticUtil.subtract(param.getVarLxSubjectAmt(), 冲减应计利息));
        //变量.减值准备科目余额								=	变量.减值准备科目余额 - 变量.减值使用
        param.setVarJzzbSubjectAmt(ArithmeticUtil.subtract(param.getVarJzzbSubjectAmt(), 冲抵减值金额));

        //变量.结转日应计利息余额Map.put(交易明细.到期日,变量.结转日应计利息余额 - 变量.冲减应计利息)
        param.getJzryjlxyeMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate())), 冲减应计利息));
        //变量.结转日成本科目余额Map.put(交易明细.到期日,变量.结转日成本科目余额 -变量.冲减成本)
        param.getJzrcbkmyeMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate())), 冲减成本));
        //变量.结转日成本科目数量Map.put(交易明细.到期日,变量.结转日成本科目数量 - 变量.冲减数量)
        param.getJzrcbkmslMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate())), 冲减数量));
        //变量.结转日溢折价科目余额.put(交易明细.到期日,变量.结转日溢折价科目余额 - 变量.冲减溢折价)
        param.getJzryzjkmyeMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate())), 冲减溢折价));

        /** modify by yanya on 20210325 增加科目成本、利息、减值本位币处理**/
        param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrcbkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())),
                        accountAmountMap.get(BookConstant.FACEVALUE+BookConstant.BCCYAMT)));
        param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzryjlxyeMapBccy().get(DateUtil.format(dealNow.getMdate())),
                        accountAmountMap.get(BookConstant.INTAMT+BookConstant.BCCYAMT)));
        param.setVarJzzbSubjectAmtBccy(ArithmeticUtil.doAdd(param.getVarJzzbSubjectAmtBccy(),
                accountAmountMap.get(BookConstant.IMPAMT+BookConstant.BCCYAMT)));
        /** modify by yanya on 20210326 增加溢折价本位币处理**/
        param.getJzryzjkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzryzjkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())),
                        ArithmeticUtil.round(calculationCommonService.doMultiply(冲减溢折价, changeRate),NumberEnum.MONEY)));

        //if(金额元素.减值使用 != 0)
        if(冲抵减值金额 != null && 冲抵减值金额.compareTo(BigDecimal.ZERO) != 0) {
            //变量.账务明细 = 调用账务明细生成共通返回结果中 基础科目性质 ="摊余成本类资产减值"的第一条数据
            //调用【减值相关共通-减值使用明细数据生成】
            //参数名：					减值使用明细数据生成
            //账务明细					变量.账务明细
            //减值金额					金额元素.减值使用
            //减值金额本币					金额元素.减值使用_本位币
            //减值余额					变量.减值准备科目余额
            //减值余额本币					变量.减值准备科目余额* 变量.汇率
            //返回值:					统计分析明细表（资产减值）entity
            // 变量.统计分析明细表（资产减值）减值使用列表.add(上述返回值)
            List<ValBokBookdetailBatch> valBokBookdetailBatch = resultNowNow.getValBokBookdetailBatch();
            List<ValBokBookdetailBatch> valBokBookdetailBatchResult = new ArrayList<ValBokBookdetailBatch>();
            for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
                if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrueNew.摊余成本类_减值准备.getValue())) {
                    valBokBookdetailBatchResult.add(dto);
                }
            }
            if (CollectionUtils.isNotEmpty(valBokBookdetailBatchResult)) {
                ValBokBookdetailBatch valBokBookdetailBatchDto = valBokBookdetailBatchResult.get(0);
                BokStatLossPosiiton bokStatLossPosiiton = lossCommonService.setLossInfoUsed(valBokBookdetailBatchDto,
                        accountAmountMap.get(BookConstant.IMPAMT),
                        accountAmountMap.get(BookConstant.IMPAMT+ BookConstant.BCCYAMT),
                        param.getVarJzzbSubjectAmt(),
                        calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null,
                                param.getVarJzzbSubjectAmt(), changeRate)
                        ,dealNow.getTradeId());
                // 统计分析明细表（资产减值）
                returnResult.getBokStatLossPosiitonList().add(bokStatLossPosiiton);
            }
        }

    }

    /**
     * 摊余成本类 行权赎回结转
     * @param rstBaseinfo
     * @param returnResult
     * @param dealNow
     * @param acctEntity
     * @param param
     */
    public void createExecJZAC(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
                                   FinProductDealInfoComDTO dealNow,
                                   FinProductDealInfoResult acctEntity, DealParamterDto param) {
        // 账套核算参数查询
        // 定义账套参数查询参数项list
        List<String> paramCodeList = new ArrayList<String>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_还本收息冲抵减值准备处理日.getValue());
        Map<String, Object> getValBokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(
                acctEntity.getBooksetId(), paramCodeList);
        String 还本收息冲抵减值准备处理日 = (String)getValBokBooksetExtMap.get(
                ValDictCodeList.账套配置表参数项_还本收息冲抵减值准备处理日.getValue());// 账套配置表参数项_还本收息冲抵减值准备处理日
        // 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();
        // 汇率
        BigDecimal changeRate = BigDecimal.ONE;
        if(StringUtil.isNotEmpty(dealNow.getCcy()) && !dealNow.getCcy().equals(acctEntity.getStandardCcy())){
            // 调用共通汇率查询
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                    acctEntity.getStandardCcy(), dealNow.getCcy());
            if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                changeRate = currencyMarketList.get(0).getClosePrice();
            }
        }
        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());
        ad.setTableId(DictCodeListTableId.会计场景代码_摊余成本债券_赎回结转.getValue());
        // accountSceneMap设置
        // 备注
        accountSceneMap.put(BookConstant.BOOK_REMARK, "摊余成本类债券资产" + DictCodeListExt3.交易方向_行权赎回结转.getCodeName() + "交易登账");
        // 公共场景赋值
        getAccountSceneMap(accountSceneMap, dealNow, rstBaseinfo);
        //定义		变量.结转日应计利息余额 = 变量.结转日应计利息余额Map.get(交易明细.到期日)
        BigDecimal 结转日应计利息余额 = param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日成本科目余额 = 变量.结转日成本科目余额Map.get(交易明细.到期日）
        BigDecimal 结转日成本科目余额 = param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日成本科目数量 = 变量.结转日成本科目数量Map.get(交易明细.到期日)
        BigDecimal 结转日成本科目数量 = param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日溢折价科目余额 = 变量.结转日溢折价科目余额Map.get(交易明细.到期日)
        BigDecimal 结转日溢折价科目余额 = param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate()));
        List<SubjectBalance> listSubjectBalance = new ArrayList<SubjectBalance>();
        // 科目余额查询参数
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        searchDao.setBooksetId(acctEntity.getBooksetId());
        searchDao.setBokDetailId(acctEntity.getBokDetailId());
        searchDao.setHappenDate(addDate(dealNow.getMdate(), -1));
        searchDao.setBookDate(acctEntity.getBookDate());
        //IF 变量.结转日应计利息余额Map.get(交易明细.到期日) == NULL
        if(结转日应计利息余额 == null) {
            //查询交易的到期日对应的应计利息
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.应计利息余额 = 科目余额
                结转日应计利息余额 = listSubjectBalance.get(0).getBalance();
                /** modify by yanya on 20210325 增加科目成本、利息、减值本位币处理**/
                param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalance.get(0).getbAmt());
            }
            else {
                结转日应计利息余额 = BigDecimal.ZERO;
                /** modify by yanya on 20210325 增加科目成本、利息、减值本位币处理**/
                param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzryjlxyeMap().put(DateUtil.format(dealNow.getMdate()), 结转日应计利息余额);
        }
        //IF 变量.结转日成本科目余额Map.get(交易明细.到期日） IS NULL
        if(结转日成本科目余额 == null) {
            searchDao.setBaseSubjectNatureList(
                    new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.结转日成本科目余额 = 科目余额
                结转日成本科目余额 = listSubjectBalance.get(0).getBalance();
                // 定义 变量.结转日成本科目数量 = 数量
                结转日成本科目数量 = listSubjectBalance.get(0).getNum();
                /** modify by yanya on 20210325 增加科目成本、利息、减值本位币处理**/
                param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalance.get(0).getbAmt());
            }
            else {
                结转日成本科目余额 = BigDecimal.ZERO;
                结转日成本科目数量 = BigDecimal.ZERO;
                /** modify by yanya on 20210325 增加科目成本、利息、减值本位币处理**/
                param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzrcbkmyeMap().put(DateUtil.format(dealNow.getMdate()), 结转日成本科目余额);
            param.getJzrcbkmslMap().put(DateUtil.format(dealNow.getMdate()), 结转日成本科目数量);
        }
        //BigDecimal varGyjzbdye = BigDecimal.ZERO;
        if(结转日溢折价科目余额 == null) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_利息调整.getValue())));
            List<SubjectBalance> listSubjectBalanceEx = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalanceEx != null && listSubjectBalanceEx.size() > 0) {
                // 变量.结转日公允价值变动科目余额 = 科目余额
                结转日溢折价科目余额 = listSubjectBalanceEx.get(0).getBalance();
                /** modify by yanya on 20210326 溢折价科目本位币处理**/
                param.getJzryzjkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalanceEx.get(0).getbAmt());
            }
            else {
                结转日溢折价科目余额 = BigDecimal.ZERO;
                /** modify by yanya on 20210326 溢折价科目本位币处理**/
                param.getJzryzjkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzryzjkmyeMap().put(DateUtil.format(dealNow.getMdate()), 结转日溢折价科目余额);
        }
        if (!DictCodeListExt1.估值交易市场_上交所.getValue().equals(rstBaseinfo.getTradeMarket())
                && !DictCodeListExt1.估值交易市场_深交所.getValue().equals(rstBaseinfo.getTradeMarket())) {
            // 买入场景
            // 调用账务共通设计->【凭证最新账务查询】
            List<BookLatestInfoDto> listBookLatestInfoDto = getBookLatestInfo(dealNow.getInvAim(),true, dealNow, acctEntity);
            // LOOP 变量.买入账务明细列表 -> 变量.账务明细
            if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                for (BookLatestInfoDto var : listBookLatestInfoDto) {
                    // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.结转日成本科目数量 -= 变量.账务明细.发生数量
                        结转日成本科目数量 = ArithmeticUtil.subtract(结转日成本科目数量, var.getHappenNumber());
                        // 变量.结转日成本科目余额 -= 变量.账务明细.原币发生额
                        结转日成本科目余额 = ArithmeticUtil.subtract(结转日成本科目余额, var.getoHappenAmt());
                    }
                    // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.摊余成本类_利息调整.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日溢折价科目余额 -= 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.subtract(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日溢折价科目余额 += 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.doAdd(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                    }
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue().equals(var.getBaseSubjectNature())) {
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日应计利息余额 -= 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.subtract(结转日应计利息余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日应计利息余额 += 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.doAdd(结转日应计利息余额, var.getoHappenAmt());
                        }
                    }
                }
            }
            // 卖出场景
            // 调用账务共通设计->【凭证最新账务查询】
            listBookLatestInfoDto = getBookLatestInfo(dealNow.getInvAim(), false, dealNow, acctEntity);
            // LOOP 变量.卖出账务明细列表 -> 变量.账务明细
            if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                for (BookLatestInfoDto var : listBookLatestInfoDto) {
                    // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.结转日成本科目数量 += 变量.账务明细.发生数量
                        结转日成本科目数量 = ArithmeticUtil.doAdd(结转日成本科目数量, var.getHappenNumber());
                        // 变量.结转日成本科目余额 += 变量.账务明细.原币发生额
                        结转日成本科目余额 = ArithmeticUtil.doAdd(结转日成本科目余额, var.getoHappenAmt());
                    }
                    // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.摊余成本类_利息调整.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日溢折价科目余额 -= 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.subtract(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日溢折价科目余额 += 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.doAdd(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                    }
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue().equals(var.getBaseSubjectNature())) {
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日应计利息余额 -= 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.subtract(结转日应计利息余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日应计利息余额 += 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.doAdd(结转日应计利息余额, var.getoHappenAmt());
                        }
                    }
                }
            }
        }
        //定义 		变量.冲减溢折价								=		变量.结转日溢折价科目余额
        BigDecimal 冲减溢折价 = 结转日溢折价科目余额;
        //定义 		变量.冲减应计利息								=		变量.结转日应计利息科目余额
        BigDecimal 冲减应计利息 = 结转日应计利息余额;
        //定义 		变量.冲减成本								=		变量.结转日成本科目余额
        BigDecimal 冲减成本 = 结转日成本科目余额;
        //定义 		变量.冲减数量								=		变量.结转日成本科目数量
        BigDecimal 冲减数量 = 结转日成本科目数量;
        //定义 		变量.交易金额_清算款								=		变量.溢折价科目余额 + 变量.应计利息科目余额 + 变量.成本科目余额
        BigDecimal 交易金额_清算款 = ArithmeticUtil.doAdd(param.getVarYzjSubjectAmt(), param.getVarLxSubjectAmt(), param.getVarCostSubjectAmt());

        BigDecimal 冲抵减值金额 = BigDecimal.ZERO;
        //IF 数据字典：还本收息冲抵减值准备处理日.理论日 eq 变量.还本收息冲抵减值准备处理日
        if(StringUtil.equals(DictCodeListExt3.还本收息冲抵减值准备处理日_理论日.getValue(), 还本收息冲抵减值准备处理日)) {
            // 调用减值相关共通-冲减减值计算(卖出)
            LossAmtInputDto dto = this.getLossAmtInputDto(acctEntity, dealNow,
                    DictCodeListExt1.现金流一级类型_利息.getValue(),
                    ArithmeticUtil.doAdd(冲减应计利息, 冲减成本),
                    param.getVarJzzbSubjectAmt(),
                    冲减数量,
                    //当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
                    ArithmeticUtil.doAdd(param.getVarCostSubjectAmt(),
                            param.getVarLxSubjectAmt(),
                            param.getVarQtyskBjSubjectSumAmt(),
                            param.getVarQtyskLxSubjectSumAmt(),
                            param.getVarYqfxSubjectSumAmt()),
                    param.getVarCostSubjectNum());
            /** modify by yanya on 20210326调用卖出场景**/
            冲抵减值金额 = lossCommonBatchService.calcLossAmtForDeal(dto);
        }
        //变量.买卖价差 =	变量.交易金额_清算款 - (变量.冲减成本 + 变量.冲减溢折价 ) - 变量.冲减应计利息
        BigDecimal 买卖价差 =  ArithmeticUtil.doSub(交易金额_清算款, 冲减成本, 冲减溢折价, 冲减应计利息);

        // 实收金额
        accountAmountMap.put(BookConstant.DEALAMT, ArithmeticUtil.round(交易金额_清算款, NumberEnum.MONEY));
        //成本金额 = 变量.冲减成本
        accountAmountMap.put(BookConstant.FACEVALUE, ArithmeticUtil.round(冲减成本, NumberEnum.MONEY));
        //应计利息 = 冲减应计利息
        accountAmountMap.put(BookConstant.INTAMT, ArithmeticUtil.round(冲减应计利息, NumberEnum.MONEY));
        //减值准备  <- 变量.冲抵减值金额 * -1
        accountAmountMap.put(BookConstant.IMPAMT,
                ArithmeticUtil.round(ArithmeticUtil.multiply(冲抵减值金额,new BigDecimal(-1)), NumberEnum.MONEY));
        //if(变量.冲减公允价值变动 >= 0 )
        if(冲减溢折价.compareTo(BigDecimal.ZERO) >= 0) {
            // 剩余溢折价(贷) <- 变量.冲减溢折价
            accountAmountMap.put(BookConstant.DSCINC_C,
                    ArithmeticUtil.round(冲减溢折价, NumberEnum.MONEY));
        }
        else {
            // 剩余溢折价(借) <- 变量.冲减溢折价 * -1
            accountAmountMap.put(BookConstant.DSCINC_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(冲减溢折价, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        //if（变量.买卖价差>= 0）
        if(买卖价差.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.买卖价差（贷）								<-变量.买卖价差
            accountAmountMap.put(BookConstant.DSCLOSS_C, ArithmeticUtil.round(买卖价差, NumberEnum.MONEY));
        }
        else {
            //金额元素.买卖价差（借）								<-变量.买卖价差 * -1
            accountAmountMap.put(BookConstant.DSCLOSS_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(买卖价差, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        Map<String, BigDecimal> accountAmountBCCYAMTMap = new HashMap<>();
        //本位币金额元素赋值 循环金额元素
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());// value值塞入当前交易币种
            accountAmountBCCYAMTMap.put(key + BookConstant.BCCYAMT,
                    ArithmeticUtil.round(calculationCommonService.doMultiply(accountAmountMap.get(key), changeRate),NumberEnum.MONEY));
        }
        /** 本位币 币种**/
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
        //本位币金额元素都放到金额元素map中
        accountAmountMap.putAll(accountAmountBCCYAMTMap);
        /**定义 变量.买卖价差_本位币	=	金额元素.实收金额_本位币 + 金额元素.冲减溢折价(借)_本位币 -
            (金额元素.面值_本位币 + 金额元素.冲减溢折价(贷)_本位币 + 金额元素.应计利息_本位币)**/

        BigDecimal 买卖价差_本位币 = ArithmeticUtil.doSub(
                ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.DSCINC_D + BookConstant.BCCYAMT)
                ),
                accountAmountMap.get(BookConstant.FACEVALUE + BookConstant.BCCYAMT),
                accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT),
                accountAmountMap.get(BookConstant.DSCINC_C + BookConstant.BCCYAMT)
        );
        //if（变量.买卖价差_本位币>= 0）
        if(买卖价差_本位币.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.买卖价差（贷）_本位币								<-	变量.买卖价差_本位币
            accountAmountMap.put(BookConstant.DSCLOSS_C + BookConstant.BCCYAMT,
                    ArithmeticUtil.round(买卖价差_本位币, NumberEnum.MONEY));
        }
        //else
        else {
            //金额元素.买卖价差（借）_本位币								<-	变量.买卖价差_本位币*-1
            accountAmountMap.put(BookConstant.DSCLOSS_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(买卖价差_本位币, new BigDecimal(-1)), NumberEnum.MONEY));
        }

        // modify by yanya on 20210326 特殊处理赎回结转数量
        accountSceneMap.put(BookConstant.HAPPEN_NUMBER, ArithmeticUtil.round(冲减数量, NumberEnum.QTY));
        /** modify by yanya on 20210325 科目余额计算成本、利息、减值使用变量计算：避免同一批处理处理金额错误 start**/
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE,
                param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE_NUMBER,
                param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate())));//余额数量_本次发生前
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE+BookConstant.BCCYAMT,
                param.getJzrcbkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.FACEVALUE+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE,
                param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE+BookConstant.BCCYAMT,
                param.getJzryjlxyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.INTAMT+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE, param.getVarJzzbSubjectAmt());//科目余额_本次发生前
        accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
        accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE+BookConstant.BCCYAMT, param.getVarJzzbSubjectAmtBccy());//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.IMPAMT+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        /** modify by yanya on 20210325 科目余额计算成本、利息、减值使用变量计算：避免同一批处理处理金额错误 end**/
        /** modify by yanya on 20210326 溢折价使用变量计算：避免同一批处理处理金额错误 start**/
        if (冲减溢折价.compareTo(BigDecimal.ZERO) >= 0) {
            accountAmountMap.put(BookConstant.DSCINC_C+BookConstant.BALANCE,
                    param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_C+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_C+BookConstant.BALANCE+BookConstant.BCCYAMT,
                    param.getJzryzjkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.DSCINC_C+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        else {
            accountAmountMap.put(BookConstant.DSCINC_D+BookConstant.BALANCE,
                    param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_D+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_D+BookConstant.BALANCE+BookConstant.BCCYAMT,
                    param.getJzryzjkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.DSCINC_D+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        /** modify by yanya on 20210326 溢折价使用变量计算：避免同一批处理处理金额错误 end**/

        // map塞入场景要素管理中
        // 记账场景要素
        ad.setAccountSceneMap(accountSceneMap);
        // 记账金额要素
        ad.setAccountAmountMap(accountAmountMap);
        // 记账金额币种要素
        ad.setAccountCurrencyMap(accountCurrencyMap);
        // 基础科目性质
        Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
        Map<String, Object> accountNatrueBranchMap = new HashMap<>();
        accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
        accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(), accountNatrueBranchMap);
        ad.setAccountNatrueMap(accountNatrueMap);
        //add by weipeng on 20220325：增加辅助核算项配置
        ad.setSubjectMappingConfigDefault(dealNow.getSubjectMappingConfig());
        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        //全局变量的更新
        //变量.成本科目余额 								=	变量.成本科目余额 - 变量.冲减成本
        // 变量.成本科目余额 = 变量.成本科目余额 - 变量.冲减成本
        param.setVarCostSubjectAmt(ArithmeticUtil.subtract(param.getVarCostSubjectAmt(), 冲减成本));
        // 变量.成本科目数量 = 变量.成本科目数量 - 变量.冲减数量
        param.setVarCostSubjectNum(ArithmeticUtil.subtract(param.getVarCostSubjectNum(), 冲减数量));
        // 变量.公允价值变动科目余额 = 变量.溢折价科目余额 - 变量.冲减公允价值变动
        param.setVarYzjSubjectAmt(ArithmeticUtil.subtract(param.getVarYzjSubjectAmt(), 冲减溢折价));
        //变量.应计利息科目余额								=	变量.应计利息科目余额 - 变量.冲减应计利息
        param.setVarLxSubjectAmt(ArithmeticUtil.subtract(param.getVarLxSubjectAmt(), 冲减应计利息));
        //变量.减值准备科目余额								=	变量.减值准备科目余额 - 变量.减值使用
        param.setVarJzzbSubjectAmt(ArithmeticUtil.subtract(param.getVarJzzbSubjectAmt(), 冲抵减值金额));

        //变量.结转日应计利息余额Map.put(交易明细.到期日,变量.结转日应计利息余额 - 变量.冲减应计利息)
        param.getJzryjlxyeMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate())), 冲减应计利息));
        //变量.结转日成本科目余额Map.put(交易明细.到期日,变量.结转日成本科目余额 -变量.冲减成本)
        param.getJzrcbkmyeMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate())), 冲减成本));
        //变量.结转日成本科目数量Map.put(交易明细.到期日,变量.结转日成本科目数量 - 变量.冲减数量)
        param.getJzrcbkmslMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate())), 冲减数量));
        //变量.结转日溢折价科目余额.put(交易明细.到期日,变量.结转日溢折价科目余额 - 变量.冲减溢折价)
        param.getJzryzjkmyeMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate())), 冲减溢折价));

        /** modify by yanya on 20210325 增加科目成本、利息、减值本位币处理**/
        param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrcbkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())),
                        accountAmountMap.get(BookConstant.FACEVALUE+BookConstant.BCCYAMT)));
        param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzryjlxyeMapBccy().get(DateUtil.format(dealNow.getMdate())),
                        accountAmountMap.get(BookConstant.INTAMT+BookConstant.BCCYAMT)));
        param.setVarJzzbSubjectAmtBccy(ArithmeticUtil.doAdd(param.getVarJzzbSubjectAmtBccy(),
                accountAmountMap.get(BookConstant.IMPAMT+BookConstant.BCCYAMT)));
        /** modify by yanya on 20210326 增加溢折价本位币处理**/
        param.getJzryzjkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzryzjkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())),
                        ArithmeticUtil.round(calculationCommonService.doMultiply(冲减溢折价, changeRate),NumberEnum.MONEY)));
        //if(金额元素.减值使用 != 0)
        if(冲抵减值金额 != null && 冲抵减值金额.compareTo(BigDecimal.ZERO) != 0) {
            //变量.账务明细 = 调用账务明细生成共通返回结果中 基础科目性质 ="摊余成本类资产减值"的第一条数据
            //调用【减值相关共通-减值使用明细数据生成】
            //参数名：					减值使用明细数据生成
            //账务明细					变量.账务明细
            //减值金额					金额元素.减值使用
            //减值金额本币					金额元素.减值使用_本位币
            //减值余额					变量.减值准备科目余额
            //减值余额本币					变量.减值准备科目余额* 变量.汇率
            //返回值:					统计分析明细表（资产减值）entity
            // 变量.统计分析明细表（资产减值）减值使用列表.add(上述返回值)
            List<ValBokBookdetailBatch> valBokBookdetailBatch = resultNowNow.getValBokBookdetailBatch();
            List<ValBokBookdetailBatch> valBokBookdetailBatchResult = new ArrayList<ValBokBookdetailBatch>();
            for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
                if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrueNew.摊余成本类_减值准备.getValue())) {
                    valBokBookdetailBatchResult.add(dto);
                }
            }
            if (CollectionUtils.isNotEmpty(valBokBookdetailBatchResult)) {
                ValBokBookdetailBatch valBokBookdetailBatchDto = valBokBookdetailBatchResult.get(0);
                BokStatLossPosiiton bokStatLossPosiiton = lossCommonService.setLossInfoUsed(valBokBookdetailBatchDto,
                        accountAmountMap.get(BookConstant.IMPAMT),
                        accountAmountMap.get(BookConstant.IMPAMT+ BookConstant.BCCYAMT),
                        param.getVarJzzbSubjectAmt(),
                        calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null,
                                param.getVarJzzbSubjectAmt(), changeRate)
                        ,dealNow.getTradeId());
                // 统计分析明细表（资产减值）
                returnResult.getBokStatLossPosiitonList().add(bokStatLossPosiiton);
            }
        }

    }

    /**
     * 综合收益类 行权回售申请
     * @param rstBaseinfo
     * @param returnResult
     * @param dealNow
     * @param acctEntity
     * @param param
     */
    public void createExecHSFvoci(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
                                FinProductDealInfoComDTO dealNow,
                                FinProductDealInfoResult acctEntity, DealParamterDto param) {
        // 账套核算参数查询
        // 定义账套参数查询参数项list
        List<String> paramCodeList = new ArrayList<String>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_还本收息冲抵减值准备处理日.getValue());
        Map<String, Object> getValBokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(
                acctEntity.getBooksetId(), paramCodeList);
        String 还本收息冲抵减值准备处理日 = (String)getValBokBooksetExtMap.get(
                ValDictCodeList.账套配置表参数项_还本收息冲抵减值准备处理日.getValue());// 账套配置表参数项_还本收息冲抵减值准备处理日
        // 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();
        // 汇率
        BigDecimal changeRate = BigDecimal.ONE;
        if(StringUtil.isNotEmpty(dealNow.getCcy()) && !dealNow.getCcy().equals(acctEntity.getStandardCcy())){
            // 调用共通汇率查询
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                    acctEntity.getStandardCcy(), dealNow.getCcy());
            if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                changeRate = currencyMarketList.get(0).getClosePrice();
            }
        }
        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());
        ad.setTableId(DictCodeListTableId.会计场景代码_综合收益债券_回售申请.getValue());
        // accountSceneMap设置
        // 备注
        accountSceneMap.put(BookConstant.BOOK_REMARK, "综合收益类债券资产" + DictCodeListExt3.交易方向_行权回售申请.getCodeName() + "交易登账");
        // 公共场景赋值
        getAccountSceneMap(accountSceneMap, dealNow, rstBaseinfo);
        //定义		变量.结转日应计利息余额 = 变量.结转日应计利息余额Map.get(交易明细.到期日)
        BigDecimal 结转日应计利息余额 = param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日成本科目余额 = 变量.结转日成本科目余额Map.get(交易明细.到期日）
        BigDecimal 结转日成本科目余额 = param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日成本科目数量 = 变量.结转日成本科目数量Map.get(交易明细.到期日)
        BigDecimal 结转日成本科目数量 = param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日溢折价科目余额 = 变量.结转日溢折价科目余额Map.get(交易明细.到期日)
        BigDecimal 结转日溢折价科目余额 = param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate()));
        //变量.结转日公允价值变动科目余额 = 变量.结转日公允价值变动科目余额Map.get(交易明细.到期日)
        BigDecimal 结转日公允价值变动科目余额 = param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate()));
        List<SubjectBalance> listSubjectBalance = new ArrayList<SubjectBalance>();
        // 科目余额查询参数
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        searchDao.setBooksetId(acctEntity.getBooksetId());
        searchDao.setBokDetailId(acctEntity.getBokDetailId());
        searchDao.setHappenDate(addDate(dealNow.getMdate(), -1));
        searchDao.setBookDate(acctEntity.getBookDate());
        //IF 变量.结转日应计利息余额Map.get(交易明细.到期日) == NULL
        if(结转日应计利息余额 == null) {
            //查询交易的到期日对应的应计利息
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.应计利息余额 = 科目余额
                结转日应计利息余额 = listSubjectBalance.get(0).getBalance();
                /** modify by yanya on 20210325 增加科目成本、利息、减值本位币处理**/
                param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalance.get(0).getbAmt());
            }
            else {
                结转日应计利息余额 = BigDecimal.ZERO;
                /** modify by yanya on 20210325 增加科目成本、利息、减值本位币处理**/
                param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzryjlxyeMap().put(DateUtil.format(dealNow.getMdate()), 结转日应计利息余额);
        }
        //IF 变量.结转日成本科目余额Map.get(交易明细.到期日） IS NULL
        if(结转日成本科目余额 == null) {
            searchDao.setBaseSubjectNatureList(
                    new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.结转日成本科目余额 = 科目余额
                结转日成本科目余额 = listSubjectBalance.get(0).getBalance();
                // 定义 变量.结转日成本科目数量 = 数量
                结转日成本科目数量 = listSubjectBalance.get(0).getNum();
                /** modify by yanya on 20210325 增加科目成本、利息、减值本位币处理**/
                param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalance.get(0).getbAmt());
            }
            else {
                结转日成本科目余额 = BigDecimal.ZERO;
                结转日成本科目数量 = BigDecimal.ZERO;
                /** modify by yanya on 20210325 增加科目成本、利息、减值本位币处理**/
                param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzrcbkmyeMap().put(DateUtil.format(dealNow.getMdate()), 结转日成本科目余额);
            param.getJzrcbkmslMap().put(DateUtil.format(dealNow.getMdate()), 结转日成本科目数量);
        }
        //BigDecimal varGyjzbdye = BigDecimal.ZERO;
        if(结转日溢折价科目余额 == null) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.结转日公允价值变动科目余额 = 科目余额
                结转日溢折价科目余额 = listSubjectBalance.get(0).getBalance();
                /** modify by yanya on 20210326 溢折价科目本位币处理**/
                param.getJzryzjkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalance.get(0).getbAmt());
            }
            else {
                结转日溢折价科目余额 = BigDecimal.ZERO;
                /** modify by yanya on 20210326 溢折价科目本位币处理**/
                param.getJzryzjkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzryzjkmyeMap().put(DateUtil.format(dealNow.getMdate()), 结转日溢折价科目余额);
        }
        if(结转日公允价值变动科目余额 == null) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_公允价值变动.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.结转日公允价值变动科目余额 = 科目余额
                结转日公允价值变动科目余额 = listSubjectBalance.get(0).getBalance();
                /** modify by yanya on 20210326 公允价值变动本位币处理**/
                param.getJzrgyjzbdMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalance.get(0).getbAmt());
            }
            else {
                结转日公允价值变动科目余额 = BigDecimal.ZERO;
                /** modify by yanya on 20210326 公允价值变动本位币处理**/
                param.getJzrgyjzbdMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzrgyjzbdMap().put(DateUtil.format(dealNow.getMdate()), 结转日公允价值变动科目余额);
        }
        if (!DictCodeListExt1.估值交易市场_上交所.getValue().equals(rstBaseinfo.getTradeMarket())
                && !DictCodeListExt1.估值交易市场_深交所.getValue().equals(rstBaseinfo.getTradeMarket())) {
            // 买入场景
            // 调用账务共通设计->【凭证最新账务查询】
            List<BookLatestInfoDto> listBookLatestInfoDto = getBookLatestInfo(dealNow.getInvAim(),true, dealNow, acctEntity);
            // LOOP 变量.买入账务明细列表 -> 变量.账务明细
            if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                for (BookLatestInfoDto var : listBookLatestInfoDto) {
                    // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.结转日成本科目数量 -= 变量.账务明细.发生数量
                        结转日成本科目数量 = ArithmeticUtil.subtract(结转日成本科目数量, var.getHappenNumber());
                        // 变量.结转日成本科目余额 -= 变量.账务明细.原币发生额
                        结转日成本科目余额 = ArithmeticUtil.subtract(结转日成本科目余额, var.getoHappenAmt());
                    }
                    // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日公允价值变动科目余额 -= 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.subtract(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日公允价值变动科目余额 += 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.doAdd(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                    }
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue().equals(var.getBaseSubjectNature())) {
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日应计利息余额 -= 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.subtract(结转日应计利息余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日应计利息余额 += 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.doAdd(结转日应计利息余额, var.getoHappenAmt());
                        }
                    }
                    if (DictCodeBaseSubjectNatrueNew.综合收益类_公允价值变动.getValue().equals(var.getBaseSubjectNature())) {
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日应计利息余额 -= 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.subtract(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日应计利息余额 += 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.doAdd(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                    }
                }
            }
            // 卖出场景
            // 调用账务共通设计->【凭证最新账务查询】
            listBookLatestInfoDto = getBookLatestInfo(dealNow.getInvAim(), false, dealNow, acctEntity);
            // LOOP 变量.卖出账务明细列表 -> 变量.账务明细
            if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                for (BookLatestInfoDto var : listBookLatestInfoDto) {
                    // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.结转日成本科目数量 += 变量.账务明细.发生数量
                        结转日成本科目数量 = ArithmeticUtil.doAdd(结转日成本科目数量, var.getHappenNumber());
                        // 变量.结转日成本科目余额 += 变量.账务明细.原币发生额
                        结转日成本科目余额 = ArithmeticUtil.doAdd(结转日成本科目余额, var.getoHappenAmt());
                    }
                    // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日公允价值变动科目余额 -= 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.subtract(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日公允价值变动科目余额 += 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.doAdd(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                    }
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue().equals(var.getBaseSubjectNature())) {
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日应计利息余额 -= 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.subtract(结转日应计利息余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日应计利息余额 += 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.doAdd(结转日应计利息余额, var.getoHappenAmt());
                        }
                    }
                    if (DictCodeBaseSubjectNatrueNew.综合收益类_公允价值变动.getValue().equals(var.getBaseSubjectNature())) {
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日应计利息余额 -= 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.subtract(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日应计利息余额 += 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.doAdd(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                    }
                }
            }
        }
       // 定义 变量.冲减公允价值变动		=		变量.结转日公允价值变动科目余额 * 交易明细.交易份额*/变量.成本科目数量
        BigDecimal 冲减公允价值变动 = ArithmeticUtil.divide(
                ArithmeticUtil.multiply(结转日公允价值变动科目余额, dealNow.getShareAmt()), param.getVarCostSubjectNum());
        //定义 		变量.冲减溢折价		=		变量.结转日溢折价科目余额  * 交易明细.交易份额 / 变量.成本科目数量
        BigDecimal 冲减溢折价 = ArithmeticUtil.divide(
                ArithmeticUtil.multiply(结转日溢折价科目余额, dealNow.getShareAmt()), param.getVarCostSubjectNum());
        //定义 		变量.冲减应计利息		=		变量.结转日应计利息科目余额 * 交易明细.交易份额 / 变量.成本科目数量
        BigDecimal 冲减应计利息 = ArithmeticUtil.divide(
                ArithmeticUtil.multiply(结转日应计利息余额, dealNow.getShareAmt()), param.getVarCostSubjectNum());
        //定义 		变量.冲减成本		=		交易明细.交易份额 * 交易明细.单位面值
        BigDecimal 冲减成本 = ArithmeticUtil.multiply(dealNow.getShareAmt(), dealNow.getParValue());
        //定义 		变量.冲减数量		=		交易明细.交易份额
        BigDecimal 冲减数量 = dealNow.getShareAmt();
        //定义 		变量.利息差额		=		0
        BigDecimal 利息差额 = BigDecimal.ZERO;
        //定义 		变量.买卖价差		=		0
        BigDecimal 买卖价差 = BigDecimal.ZERO;
        //IF 变量.结转日成本科目数量 - 变量.冲减数量 == 0
        if(ArithmeticUtil.doSub(结转日成本科目数量, 冲减数量).compareTo(BigDecimal.ZERO) == 0) {
            //变量.冲减公允价值变动									=		变量.结转日公允价值变动科目余额
            冲减公允价值变动 = 结转日公允价值变动科目余额;
            //变量.冲减溢折价							=	变量.结转日溢折价科目余额
            冲减溢折价 = 结转日溢折价科目余额;
            //变量.冲减应计利息							=	变量.结转日应计利息科目余额
            冲减应计利息 = 结转日应计利息余额;
        }
        if (StringUtil.equals(DictCodeList.息票品种_零息.getValue(), rstBaseinfo.getCouponSpecies())
                || StringUtil.equals(DictCodeList.息票品种_贴现.getValue(),rstBaseinfo.getCouponSpecies())) {
            冲减应计利息 = BigDecimal.ZERO;
        }
        else {
            //变量.利息差额	=		交易明细.利息总额 - 变量.冲减应计利息
            利息差额 = ArithmeticUtil.doSub(dealNow.getIntAmt(), 冲减应计利息);
        }
        BigDecimal 冲抵减值金额 = BigDecimal.ZERO;
        //IF 数据字典：还本收息冲抵减值准备处理日.理论日 eq 变量.还本收息冲抵减值准备处理日
        if(StringUtil.equals(DictCodeListExt3.还本收息冲抵减值准备处理日_理论日.getValue(), 还本收息冲抵减值准备处理日)) {
            // 调用减值相关共通-冲减减值计算(卖出)
            LossAmtInputDto dto = this.getLossAmtInputDto(acctEntity, dealNow,
                    DictCodeListExt1.现金流一级类型_利息.getValue(),
                    ArithmeticUtil.doAdd(冲减应计利息, 冲减成本),
                    param.getVarJzzbSubjectAmt(),
                    /** modify by yanya on 20210326发生数量**/
                    dealNow.getShareAmt(),
                    //当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
                    ArithmeticUtil.doAdd(param.getVarCostSubjectAmt(),
                            param.getVarLxSubjectAmt(),
                            param.getVarQtyskBjSubjectSumAmt(),
                            param.getVarQtyskLxSubjectSumAmt(),
                            param.getVarYqfxSubjectSumAmt()),
                    param.getVarCostSubjectNum());
            /** modify by yanya on 20210326调用卖出场景**/
            冲抵减值金额 = lossCommonBatchService.calcLossAmtForDeal(dto);

        }
        //调用 共通【费用处理共通】  参考本设计书sheet【费用处理共通】
        DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
                dealNow.getValTradeFeeDetailList(), dealNow.getProdId(), dealNow.getPortfolioId());
        // 印花税
        accountAmountMap.put(BookConstant.STAMPTAX, dealTypesFeeDto.getStampDuty());
        // 经手费
        accountAmountMap.put(BookConstant.HANDFEE, dealTypesFeeDto.getBrokerage());
        // 证管费
        accountAmountMap.put(BookConstant.CERTIFEE, dealTypesFeeDto.getCertificateFee());
        // 结算费
        accountAmountMap.put(BookConstant.SETTLEFEE, dealTypesFeeDto.getClearingFee());
        // 佣金
        accountAmountMap.put(BookConstant.COMMISSION, dealTypesFeeDto.getCommission());
        // 交易手续费
        accountAmountMap.put(BookConstant.TRADEFEE, dealTypesFeeDto.getTransactionFee());
        // 结算手续费
        accountAmountMap.put(BookConstant.SETTLEMENTFEE, dealTypesFeeDto.getSettlementFee());
        //实付通道费
        accountAmountMap.put(BookConstant.CHLAMT, dealTypesFeeDto.getChlFee());
        // 实收金额
        accountAmountMap.put(BookConstant.DEALAMT, ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));
        //成本金额 = 变量.冲减成本
        accountAmountMap.put(BookConstant.FACEVALUE, ArithmeticUtil.round(冲减成本, NumberEnum.MONEY));
        //应计利息 = 冲减应计利息
        accountAmountMap.put(BookConstant.INTAMT, ArithmeticUtil.round(冲减应计利息, NumberEnum.MONEY));
        //减值准备  <- 变量.冲抵减值金额 * -1
        accountAmountMap.put(BookConstant.IMPAMT,
                ArithmeticUtil.round(ArithmeticUtil.multiply(冲抵减值金额,new BigDecimal(-1)), NumberEnum.MONEY));

        /**变量.买卖价差	=	交易明细.交易金额 - (变量.冲减成本 + 变量.冲减溢折价 ) - 变量.冲减应计利息 - 变量.交易各种费用.不随交易费用
            + 各种变量.交易各种费用.通道费 - 变量.利息差额	**/
        买卖价差 = ArithmeticUtil.doSub(
                ArithmeticUtil.doAdd(
                        dealNow.getTradeAmt(),
                        dealTypesFeeDto.getChlFee()
                ),
                冲减成本, 冲减溢折价, 冲减应计利息, dealTypesFeeDto.getNoFeeAmt(), 利息差额);


        //if(变量.利息差额 >= 0)
        if(利息差额.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.利息差额（贷）		<-变量.利息差额
            accountAmountMap.put(BookConstant.INCOMEAMT_C, ArithmeticUtil.round(利息差额, NumberEnum.MONEY));
        }
        else {
            //金额元素.利息差额（借）								<-变量.利息差额 * -1
            accountAmountMap.put(BookConstant.INCOMEAMT_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(利息差额, new BigDecimal(-1)), NumberEnum.MONEY));
        }

        //if（变量.买卖价差>= 0）
        if(买卖价差.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.买卖价差（贷）								<-变量.买卖价差
            accountAmountMap.put(BookConstant.DSCLOSS_C, ArithmeticUtil.round(买卖价差, NumberEnum.MONEY));
        }
        else {
            //金额元素.买卖价差（借）								<-变量.买卖价差 * -1
            accountAmountMap.put(BookConstant.DSCLOSS_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(买卖价差, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        //if(变量.冲减公允价值变动 >= 0 )
        if(冲减溢折价.compareTo(BigDecimal.ZERO) >= 0) {
            // 剩余溢折价(贷) <- 变量.冲减溢折价
            accountAmountMap.put(BookConstant.DSCINC_C,
                    ArithmeticUtil.round(冲减溢折价, NumberEnum.MONEY));
        }
        else {
            // 剩余溢折价(借) <- 变量.冲减溢折价 * -1
            accountAmountMap.put(BookConstant.DSCINC_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(冲减溢折价, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        //if(变量.冲减公允价值变动 >= 0 )
        if(冲减公允价值变动.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.公允价值变动(贷) < -变量.冲减公允价值变动
            accountAmountMap.put(BookConstant.FLOAT_C, ArithmeticUtil.round(冲减公允价值变动, NumberEnum.MONEY));
            //金额元素.冲减公允价值变动(借) < -变量.冲减公允价值变动
            accountAmountMap.put(BookConstant.FLOAT_1_D, ArithmeticUtil.round(冲减公允价值变动, NumberEnum.MONEY));
        }
        else {
            //金额元素.公允价值变动(借) < -变量.冲减公允价值变动 * -1
            accountAmountMap.put(BookConstant.FLOAT_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(冲减公允价值变动, new BigDecimal(-1)), NumberEnum.MONEY));
            //金额元素.冲减公允价值变动(贷) < -变量.冲减公允价值变动 * -1
            accountAmountMap.put(BookConstant.FLOAT_1_C, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(冲减公允价值变动, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        Map<String, BigDecimal> accountAmountBCCYAMTMap = new HashMap<>();
        //本位币金额元素赋值 循环金额元素
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());// value值塞入当前交易币种
            accountAmountBCCYAMTMap.put(key + BookConstant.BCCYAMT,
                    ArithmeticUtil.round(calculationCommonService.doMultiply(accountAmountMap.get(key), changeRate),NumberEnum.MONEY));
        }
        /** 本位币 币种**/
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
        //本位币金额元素都放到金额元素map中
        accountAmountMap.putAll(accountAmountBCCYAMTMap);
        //accountAmountMap再次设置如下：
        //定义		变量.利息差额(贷)_本位币 = round(变量.利息差额,共通金额小数位) * 变量.汇率																																		保留金额元素小数位数
        BigDecimal 利息差额贷_本位币 = ArithmeticUtil.round(
                calculationCommonService.doMultiply(利息差额, changeRate),NumberEnum.MONEY);
        //定义		变量.冲减溢折价（贷）_本位币 = round(变量.冲减溢折价,共通金额小数位) * 变量.汇率
        BigDecimal 冲减溢折价贷_本位币 = ArithmeticUtil.round(
                calculationCommonService.doMultiply(冲减溢折价, changeRate),NumberEnum.MONEY);
        // 保留金额元素小数位数
        /**定义 变量.买卖价差_本位币=	(金额元素.交易金额_本位币  + 金额元素.实付通道费_本位币)
         - 	(金额元素.成本金额_本位币 + 金额元素.印花税_本位币 + 金额元素.经手费_本位币 + 金额元素.证管费_本位币 + 金额元素.结算费_本位币
         + 金额元素.佣金_本位币 + 金额元素.交易手续费_本位币
         + 金额元素.结算手续费_本位币 + 变量.利息差额(贷)_本位币 + 金额元素.应计利息_本位币 + 金额元素.冲减溢折价（贷）_本位币)
         **/

        BigDecimal 买卖价差_本位币 = ArithmeticUtil.doSub(
                ArithmeticUtil.doAdd(
                        accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.CHLAMT + BookConstant.BCCYAMT)
                ),
                ArithmeticUtil.doAdd(
                        accountAmountMap.get(BookConstant.FACEVALUE + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.STAMPTAX + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.HANDFEE + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.CERTIFEE + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.SETTLEFEE + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.COMMISSION + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.TRADEFEE + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.SETTLEMENTFEE + BookConstant.BCCYAMT),
                        利息差额贷_本位币, 冲减溢折价贷_本位币, accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)
                )
        );
        //if（变量.买卖价差_本位币>= 0）
        if(买卖价差_本位币.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.买卖价差（贷）_本位币								<-	变量.买卖价差_本位币
            accountAmountMap.put(BookConstant.DSCLOSS_C + BookConstant.BCCYAMT,
                    ArithmeticUtil.round(买卖价差_本位币, NumberEnum.MONEY));
        }
        //else
        else {
            //金额元素.买卖价差（借）_本位币								<-	变量.买卖价差_本位币*-1
            accountAmountMap.put(BookConstant.DSCLOSS_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(买卖价差_本位币, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        // 印花税账务明细代码 <- 变量.交易各种费用.印花税账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_STAMPTAX, dealTypesFeeDto.getStampDutyCode());
        // 经手费账务明细代码 <- 变量.交易各种费用.经手费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_HANDFEE, dealTypesFeeDto.getBrokerageCode());
        // 证管费账务明细代码 <- 变量.交易各种费用.证管费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_CERTIFEE,dealTypesFeeDto.getCertificateFeeCode());
        // 结算费账务明细代码 <- 变量.交易各种费用.结算费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEFEE,dealTypesFeeDto.getClearingFeeCode());
        // 佣金账务明细代码 <- 变量.交易各种费用.佣金账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_COMMISSION,dealTypesFeeDto.getCommissionCode());
        // 交易手续费账务明细代码 <- 变量.交易各种费用.交易手续费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_TRADEFEE, dealTypesFeeDto.getTransactionFeeCode());
        // 结算手续费账务明细代码 <- 变量.交易各种费用.结算手续费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEMENTFEE,dealTypesFeeDto.getSettlementFeeCode());
        // 通道费账务明细代码 <- 变量.交易各种费用.通道费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_ID2, dealTypesFeeDto.getChlFeeCode());

        /** modify by yanya on 20210325 科目余额计算成本、利息、减值使用变量计算：避免同一批处理处理金额错误 start**/
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE,
                param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE_NUMBER,
                param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate())));//余额数量_本次发生前
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE+BookConstant.BCCYAMT,
                param.getJzrcbkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.FACEVALUE+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE,
                param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE+BookConstant.BCCYAMT,
                param.getJzryjlxyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.INTAMT+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE, param.getVarJzzbSubjectAmt());//科目余额_本次发生前
        accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
        accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE+BookConstant.BCCYAMT, param.getVarJzzbSubjectAmtBccy());//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.IMPAMT+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        /** modify by yanya on 20210325 科目余额计算成本、利息、减值使用变量计算：避免同一批处理处理金额错误 end**/
        /** modify by yanya on 20210326 公允价值变动使用变量计算：避免同一批处理处理金额错误 start**/
        if (冲减公允价值变动.compareTo(BigDecimal.ZERO) >= 0) {
            accountAmountMap.put(BookConstant.FLOAT_C+BookConstant.BALANCE,
                    param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_C+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_C+BookConstant.BALANCE+BookConstant.BCCYAMT,
                    param.getJzrgyjzbdMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.FLOAT_C+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        else {
            accountAmountMap.put(BookConstant.FLOAT_D+BookConstant.BALANCE,
                    param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_D+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_D+BookConstant.BALANCE+BookConstant.BCCYAMT,
                    param.getJzrgyjzbdMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.FLOAT_D+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        /** modify by yanya on 20210326 公允价值变动使用变量计算：避免同一批处理处理金额错误 end**/
        /** modify by yanya on 20210326 溢折价使用变量计算：避免同一批处理处理金额错误 start**/
        if (冲减溢折价.compareTo(BigDecimal.ZERO) >= 0) {
            accountAmountMap.put(BookConstant.DSCINC_C+BookConstant.BALANCE,
                    param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_C+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_C+BookConstant.BALANCE+BookConstant.BCCYAMT,
                    param.getJzryzjkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.DSCINC_C+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        else {
            accountAmountMap.put(BookConstant.DSCINC_D+BookConstant.BALANCE,
                    param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_D+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_D+BookConstant.BALANCE+BookConstant.BCCYAMT,
                    param.getJzryzjkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.DSCINC_D+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        /** modify by yanya on 20210326 溢折价使用变量计算：避免同一批处理处理金额错误 end**/

        // map塞入场景要素管理中
        // 记账场景要素
        ad.setAccountSceneMap(accountSceneMap);
        // 记账金额要素
        ad.setAccountAmountMap(accountAmountMap);
        // 记账金额币种要素
        ad.setAccountCurrencyMap(accountCurrencyMap);
        // 基础科目性质
        Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
        Map<String, Object> accountNatrueBranchMap = new HashMap<>();
        accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
        accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(), accountNatrueBranchMap);
        ad.setAccountNatrueMap(accountNatrueMap);
        //add by weipeng on 20220325：增加辅助核算项配置
        ad.setSubjectMappingConfigDefault(dealNow.getSubjectMappingConfig());
        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        //全局变量的更新
        //变量.成本科目余额 								=	变量.成本科目余额 - 变量.冲减成本
        // 变量.成本科目余额 = 变量.成本科目余额 - 变量.冲减成本
        param.setVarCostSubjectAmt(ArithmeticUtil.subtract(param.getVarCostSubjectAmt(), 冲减成本));
        // 变量.成本科目数量 = 变量.成本科目数量 - 变量.冲减数量
        param.setVarCostSubjectNum(ArithmeticUtil.subtract(param.getVarCostSubjectNum(), 冲减数量));
        // 变量.溢折价科目余额 = 变量.溢折价科目余额 - 变量.冲减公允价值变动
        param.setVarYzjSubjectAmt(ArithmeticUtil.subtract(param.getVarYzjSubjectAmt(), 冲减溢折价));
        //变量.应计利息科目余额								=	变量.应计利息科目余额 - 变量.冲减应计利息
        param.setVarLxSubjectAmt(ArithmeticUtil.subtract(param.getVarLxSubjectAmt(), 冲减应计利息));
        //变量.公允价值变动科目余额	=	变量.公允价值变动科目余额 - 变量.冲减溢折价
        param.setVarGySubjectAmt(ArithmeticUtil.subtract(param.getVarGySubjectAmt(), 冲减公允价值变动));
        //变量.减值准备科目余额								=	变量.减值准备科目余额 - 变量.减值使用
        param.setVarJzzbSubjectAmt(ArithmeticUtil.subtract(param.getVarJzzbSubjectAmt(), 冲抵减值金额));

        //变量.结转日应计利息余额Map.put(交易明细.到期日,变量.结转日应计利息余额 - 变量.冲减应计利息)
        param.getJzryjlxyeMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate())), 冲减应计利息));
        //变量.结转日成本科目余额Map.put(交易明细.到期日,变量.结转日成本科目余额 -变量.冲减成本)
        param.getJzrcbkmyeMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate())), 冲减成本));
        //变量.结转日成本科目数量Map.put(交易明细.到期日,变量.结转日成本科目数量 - 变量.冲减数量)
        param.getJzrcbkmslMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate())), 冲减数量));
        //变量.结转日溢折价科目余额.put(交易明细.到期日,变量.结转日溢折价科目余额 - 变量.冲减溢折价)
        param.getJzryzjkmyeMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate())), 冲减溢折价));
        //变量.结转日公允价值变动科目余额Map.put(交易明细.到期日,变量.结转日公允价值变动科目余额 - 变量.冲减公允价值变动)
        param.getJzrgyjzbdMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate())), 冲减公允价值变动));

        /** modify by yanya on 20210325 增加科目成本、利息、减值本位币处理**/
        param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrcbkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())),
                        accountAmountMap.get(BookConstant.FACEVALUE+BookConstant.BCCYAMT)));
        param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzryjlxyeMapBccy().get(DateUtil.format(dealNow.getMdate())),
                        accountAmountMap.get(BookConstant.INTAMT+BookConstant.BCCYAMT)));
        param.setVarJzzbSubjectAmtBccy(ArithmeticUtil.doAdd(param.getVarJzzbSubjectAmtBccy(),
                accountAmountMap.get(BookConstant.IMPAMT+BookConstant.BCCYAMT)));

        /** modify by yanya on 20210326 增加公允价值变动本位币处理**/
        param.getJzrgyjzbdMapBccy().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrgyjzbdMapBccy().get(DateUtil.format(dealNow.getMdate())),
                        ArithmeticUtil.round(calculationCommonService.doMultiply(冲减公允价值变动, changeRate),NumberEnum.MONEY)));
        /** modify by yanya on 20210326 增加溢折价本位币处理**/
        param.getJzryzjkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzryzjkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())),
                        ArithmeticUtil.round(calculationCommonService.doMultiply(冲减溢折价, changeRate),NumberEnum.MONEY)));

        //if(金额元素.减值使用 != 0)
        if(冲抵减值金额 != null && 冲抵减值金额.compareTo(BigDecimal.ZERO) != 0) {
            //变量.账务明细 = 调用账务明细生成共通返回结果中 基础科目性质 ="摊余成本类资产减值"的第一条数据
            //调用【减值相关共通-减值使用明细数据生成】
            //参数名：					减值使用明细数据生成
            //账务明细					变量.账务明细
            //减值金额					金额元素.减值使用
            //减值金额本币					金额元素.减值使用_本位币
            //减值余额					变量.减值准备科目余额
            //减值余额本币					变量.减值准备科目余额* 变量.汇率
            //返回值:					统计分析明细表（资产减值）entity
            // 变量.统计分析明细表（资产减值）减值使用列表.add(上述返回值)
            List<ValBokBookdetailBatch> valBokBookdetailBatch = resultNowNow.getValBokBookdetailBatch();
            List<ValBokBookdetailBatch> valBokBookdetailBatchResult = new ArrayList<ValBokBookdetailBatch>();
            for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
                if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrueNew.其他综合收益_减值准备.getValue())) {
                    valBokBookdetailBatchResult.add(dto);
                }
            }
            if (CollectionUtils.isNotEmpty(valBokBookdetailBatchResult)) {
                ValBokBookdetailBatch valBokBookdetailBatchDto = valBokBookdetailBatchResult.get(0);
                BokStatLossPosiiton bokStatLossPosiiton = lossCommonService.setLossInfoUsed(valBokBookdetailBatchDto,
                        accountAmountMap.get(BookConstant.IMPAMT),
                        accountAmountMap.get(BookConstant.IMPAMT+ BookConstant.BCCYAMT),
                        param.getVarJzzbSubjectAmt(),
                        calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null,
                                param.getVarJzzbSubjectAmt(), changeRate)
                        ,dealNow.getTradeId());
                // 统计分析明细表（资产减值）
                returnResult.getBokStatLossPosiitonList().add(bokStatLossPosiiton);
            }
        }

    }

    /**
     * 综合收益类 行权赎回结转
     * @param rstBaseinfo
     * @param returnResult
     * @param dealNow
     * @param acctEntity
     * @param param
     */
    public void createExecJZFvoci(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
                                FinProductDealInfoComDTO dealNow,
                                FinProductDealInfoResult acctEntity, DealParamterDto param) {
        // 账套核算参数查询
        // 定义账套参数查询参数项list
        List<String> paramCodeList = new ArrayList<String>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_还本收息冲抵减值准备处理日.getValue());
        Map<String, Object> getValBokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(
                acctEntity.getBooksetId(), paramCodeList);
        String 还本收息冲抵减值准备处理日 = (String)getValBokBooksetExtMap.get(
                ValDictCodeList.账套配置表参数项_还本收息冲抵减值准备处理日.getValue());// 账套配置表参数项_还本收息冲抵减值准备处理日
        // 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();
        // 汇率
        BigDecimal changeRate = BigDecimal.ONE;
        if(StringUtil.isNotEmpty(dealNow.getCcy()) && !dealNow.getCcy().equals(acctEntity.getStandardCcy())){
            // 调用共通汇率查询
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                    acctEntity.getStandardCcy(), dealNow.getCcy());
            if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                changeRate = currencyMarketList.get(0).getClosePrice();
            }
        }
        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());
        ad.setTableId(DictCodeListTableId.会计场景代码_综合收益债券_赎回结转.getValue());
        // accountSceneMap设置
        // 备注
        accountSceneMap.put(BookConstant.BOOK_REMARK, "综合收益类债券资产" + DictCodeListExt3.交易方向_行权赎回结转.getCodeName() + "交易登账");
        // 公共场景赋值
        getAccountSceneMap(accountSceneMap, dealNow, rstBaseinfo);
        //定义		变量.结转日应计利息余额 = 变量.结转日应计利息余额Map.get(交易明细.到期日)
        BigDecimal 结转日应计利息余额 = param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日成本科目余额 = 变量.结转日成本科目余额Map.get(交易明细.到期日）
        BigDecimal 结转日成本科目余额 = param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日成本科目数量 = 变量.结转日成本科目数量Map.get(交易明细.到期日)
        BigDecimal 结转日成本科目数量 = param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日溢折价科目余额 = 变量.结转日溢折价科目余额Map.get(交易明细.到期日)
        BigDecimal 结转日溢折价科目余额 = param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日公允价值变动科目余额 = 变量.结转日公允价值变动科目余额Map.get(交易明细.到期日)
        BigDecimal 结转日公允价值变动科目余额 = param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate()));
        List<SubjectBalance> listSubjectBalance = new ArrayList<SubjectBalance>();
        // 科目余额查询参数
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        searchDao.setBooksetId(acctEntity.getBooksetId());
        searchDao.setBokDetailId(acctEntity.getBokDetailId());
        searchDao.setHappenDate(addDate(dealNow.getMdate(), -1));
        searchDao.setBookDate(acctEntity.getBookDate());
        //IF 变量.结转日应计利息余额Map.get(交易明细.到期日) == NULL
        if(结转日应计利息余额 == null) {
            //查询交易的到期日对应的应计利息
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.应计利息余额 = 科目余额
                结转日应计利息余额 = listSubjectBalance.get(0).getBalance();
                /** modify by yanya on 20210325 增加科目成本、利息、减值本位币处理**/
                param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalance.get(0).getbAmt());
            }
            else {
                结转日应计利息余额 = BigDecimal.ZERO;
                /** modify by yanya on 20210325 增加科目成本、利息、减值本位币处理**/
                param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzryjlxyeMap().put(DateUtil.format(dealNow.getMdate()), 结转日应计利息余额);
        }
        //IF 变量.结转日成本科目余额Map.get(交易明细.到期日） IS NULL
        if(结转日成本科目余额 == null) {
            searchDao.setBaseSubjectNatureList(
                    new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.结转日成本科目余额 = 科目余额
                结转日成本科目余额 = listSubjectBalance.get(0).getBalance();
                // 定义 变量.结转日成本科目数量 = 数量
                结转日成本科目数量 = listSubjectBalance.get(0).getNum();
                /** modify by yanya on 20210325 增加科目成本、利息、减值本位币处理**/
                param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalance.get(0).getbAmt());
            }
            else {
                结转日成本科目余额 = BigDecimal.ZERO;
                结转日成本科目数量 = BigDecimal.ZERO;
                /** modify by yanya on 20210325 增加科目成本、利息、减值本位币处理**/
                param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzrcbkmyeMap().put(DateUtil.format(dealNow.getMdate()), 结转日成本科目余额);
            param.getJzrcbkmslMap().put(DateUtil.format(dealNow.getMdate()), 结转日成本科目数量);
        }
        //BigDecimal varGyjzbdye = BigDecimal.ZERO;
        if(结转日溢折价科目余额 == null) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.结转日公允价值变动科目余额 = 科目余额
                结转日溢折价科目余额 = listSubjectBalance.get(0).getBalance();
                /** modify by yanya on 20210326 溢折价科目本位币处理**/
                param.getJzryzjkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalance.get(0).getbAmt());
            }
            else {
                结转日溢折价科目余额 = BigDecimal.ZERO;
                /** modify by yanya on 20210326 溢折价科目本位币处理**/
                param.getJzryzjkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzryzjkmyeMap().put(DateUtil.format(dealNow.getMdate()), 结转日溢折价科目余额);
        }
        if(结转日公允价值变动科目余额 == null) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_公允价值变动.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.结转日公允价值变动科目余额 = 科目余额
                结转日公允价值变动科目余额 = listSubjectBalance.get(0).getBalance();
                /** modify by yanya on 20210326 公允价值变动本位币处理**/
                param.getJzrgyjzbdMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalance.get(0).getbAmt());

            }
            else {
                结转日公允价值变动科目余额 = BigDecimal.ZERO;
                /** modify by yanya on 20210326 公允价值变动本位币处理**/
                param.getJzrgyjzbdMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);

            }
            //添加余额
            param.getJzrgyjzbdMap().put(DateUtil.format(dealNow.getMdate()), 结转日公允价值变动科目余额);
        }
        if (!DictCodeListExt1.估值交易市场_上交所.getValue().equals(rstBaseinfo.getTradeMarket())
                && !DictCodeListExt1.估值交易市场_深交所.getValue().equals(rstBaseinfo.getTradeMarket())) {
            // 买入场景
            // 调用账务共通设计->【凭证最新账务查询】
            List<BookLatestInfoDto> listBookLatestInfoDto = getBookLatestInfo(dealNow.getInvAim(),true, dealNow, acctEntity);
            // LOOP 变量.买入账务明细列表 -> 变量.账务明细
            if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                for (BookLatestInfoDto var : listBookLatestInfoDto) {
                    // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.结转日成本科目数量 -= 变量.账务明细.发生数量
                        结转日成本科目数量 = ArithmeticUtil.subtract(结转日成本科目数量, var.getHappenNumber());
                        // 变量.结转日成本科目余额 -= 变量.账务明细.原币发生额
                        结转日成本科目余额 = ArithmeticUtil.subtract(结转日成本科目余额, var.getoHappenAmt());
                    }
                    // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日溢折价科目余额 -= 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.subtract(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日溢折价科目余额 += 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.doAdd(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                    }
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue().equals(var.getBaseSubjectNature())) {
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日应计利息余额 -= 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.subtract(结转日应计利息余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日应计利息余额 += 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.doAdd(结转日应计利息余额, var.getoHappenAmt());
                        }
                    }
                    // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.综合收益类_公允价值变动.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日溢折价科目余额 -= 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.subtract(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日溢折价科目余额 += 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.doAdd(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                    }
                }
            }
            // 卖出场景
            // 调用账务共通设计->【凭证最新账务查询】
            listBookLatestInfoDto = getBookLatestInfo(dealNow.getInvAim(), false, dealNow, acctEntity);
            // LOOP 变量.卖出账务明细列表 -> 变量.账务明细
            if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                for (BookLatestInfoDto var : listBookLatestInfoDto) {
                    // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.结转日成本科目数量 += 变量.账务明细.发生数量
                        结转日成本科目数量 = ArithmeticUtil.doAdd(结转日成本科目数量, var.getHappenNumber());
                        // 变量.结转日成本科目余额 += 变量.账务明细.原币发生额
                        结转日成本科目余额 = ArithmeticUtil.doAdd(结转日成本科目余额, var.getoHappenAmt());
                    }
                    // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日溢折价科目余额 -= 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.subtract(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日溢折价科目余额 += 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.doAdd(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                    }
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue().equals(var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue().equals(var.getBaseSubjectNature())) {
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日应计利息余额 -= 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.subtract(结转日应计利息余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日应计利息余额 += 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.doAdd(结转日应计利息余额, var.getoHappenAmt());
                        }
                    }
                    // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.综合收益类_公允价值变动.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日溢折价科目余额 -= 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.subtract(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日溢折价科目余额 += 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.doAdd(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                    }
                }
            }
        }
        //定义 		变量.冲减公允价值变动								=		变量.结转日公允价值变动科目余额
        BigDecimal 冲减公允价值变动 = 结转日公允价值变动科目余额;
        //定义 		变量.冲减溢折价								=		变量.结转日溢折价科目余额
        BigDecimal 冲减溢折价 = 结转日溢折价科目余额;
        //定义 		变量.冲减应计利息								=		变量.结转日应计利息科目余额
        BigDecimal 冲减应计利息 = 结转日应计利息余额;
        //定义 		变量.冲减成本								=		变量.结转日成本科目余额
        BigDecimal 冲减成本 = 结转日成本科目余额;
        //定义 		变量.冲减数量								=		变量.结转日成本科目数量
        BigDecimal 冲减数量 = 结转日成本科目数量;
        //定义 		变量.交易金额_清算款								=		变量.溢折价科目余额 + 变量.应计利息科目余额 + 变量.成本科目余额
        BigDecimal 交易金额_清算款 = ArithmeticUtil.doAdd(param.getVarYzjSubjectAmt(), param.getVarLxSubjectAmt(), param.getVarCostSubjectAmt());

        BigDecimal 冲抵减值金额 = BigDecimal.ZERO;
        //IF 数据字典：还本收息冲抵减值准备处理日.理论日 eq 变量.还本收息冲抵减值准备处理日
        if(StringUtil.equals(DictCodeListExt3.还本收息冲抵减值准备处理日_理论日.getValue(), 还本收息冲抵减值准备处理日)) {
            // 调用减值相关共通-冲减减值计算(卖出)
            LossAmtInputDto dto = this.getLossAmtInputDto(acctEntity, dealNow,
                    DictCodeListExt1.现金流一级类型_利息.getValue(),
                    ArithmeticUtil.doAdd(冲减应计利息, 冲减成本),
                    param.getVarJzzbSubjectAmt(),
                    /** modify by yanya on 20210326发生数量**/
                    冲减数量,
                    //当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额 + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
                    ArithmeticUtil.doAdd(param.getVarCostSubjectAmt(),
                            param.getVarLxSubjectAmt(),
                            param.getVarQtyskBjSubjectSumAmt(),
                            param.getVarQtyskLxSubjectSumAmt(),
                            param.getVarYqfxSubjectSumAmt()),
                    param.getVarCostSubjectNum());
            /** modify by yanya on 20210326调用卖出场景**/
            冲抵减值金额 = lossCommonBatchService.calcLossAmtForDeal(dto);
        }
        //变量.买卖价差 =	变量.交易金额_清算款 - (变量.冲减成本 + 变量.冲减溢折价 ) - 变量.冲减应计利息
        BigDecimal 买卖价差 =  ArithmeticUtil.doSub(交易金额_清算款, 冲减成本, 冲减溢折价, 冲减应计利息);

        // 实收金额
        accountAmountMap.put(BookConstant.DEALAMT, ArithmeticUtil.round(交易金额_清算款, NumberEnum.MONEY));
        //成本金额 = 变量.冲减成本
        accountAmountMap.put(BookConstant.FACEVALUE, ArithmeticUtil.round(冲减成本, NumberEnum.MONEY));
        //应计利息 = 冲减应计利息
        accountAmountMap.put(BookConstant.INTAMT, ArithmeticUtil.round(冲减应计利息, NumberEnum.MONEY));
        //减值准备  <- 变量.冲抵减值金额 * -1
        accountAmountMap.put(BookConstant.IMPAMT,
                ArithmeticUtil.round(ArithmeticUtil.multiply(冲抵减值金额,new BigDecimal(-1)), NumberEnum.MONEY));
        //if(变量.冲减公允价值变动 >= 0 )
        if(冲减溢折价.compareTo(BigDecimal.ZERO) >= 0) {
            // 剩余溢折价(贷) <- 变量.冲减溢折价
            accountAmountMap.put(BookConstant.DSCINC_C,
                    ArithmeticUtil.round(冲减溢折价, NumberEnum.MONEY));
        }
        else {
            // 剩余溢折价(借) <- 变量.冲减溢折价 * -1
            accountAmountMap.put(BookConstant.DSCINC_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(冲减溢折价, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        //if(变量.冲减公允价值变动 >= 0 )
        if(冲减公允价值变动.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.公允价值变动(贷) < -变量.冲减公允价值变动
            accountAmountMap.put(BookConstant.FLOAT_C, ArithmeticUtil.round(冲减公允价值变动, NumberEnum.MONEY));
            //金额元素.冲减公允价值变动(借) < -变量.冲减公允价值变动
            accountAmountMap.put(BookConstant.FLOAT_1_D, ArithmeticUtil.round(冲减公允价值变动, NumberEnum.MONEY));
        }
        else {
            //金额元素.公允价值变动(借) < -变量.冲减公允价值变动 * -1
            accountAmountMap.put(BookConstant.FLOAT_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(冲减公允价值变动, new BigDecimal(-1)), NumberEnum.MONEY));
            //金额元素.冲减公允价值变动(贷) < -变量.冲减公允价值变动 * -1
            accountAmountMap.put(BookConstant.FLOAT_1_C, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(冲减公允价值变动, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        //if（变量.买卖价差>= 0）
        if(买卖价差.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.买卖价差（贷）								<-变量.买卖价差
            accountAmountMap.put(BookConstant.DSCLOSS_C, ArithmeticUtil.round(买卖价差, NumberEnum.MONEY));
        }
        else {
            //金额元素.买卖价差（借）								<-变量.买卖价差 * -1
            accountAmountMap.put(BookConstant.DSCLOSS_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(买卖价差, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        Map<String, BigDecimal> accountAmountBCCYAMTMap = new HashMap<>();
        //本位币金额元素赋值 循环金额元素
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());// value值塞入当前交易币种
            accountAmountBCCYAMTMap.put(key + BookConstant.BCCYAMT,
                    ArithmeticUtil.round(calculationCommonService.doMultiply(accountAmountMap.get(key), changeRate),NumberEnum.MONEY));
        }
        /** 本位币 币种**/
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
        //本位币金额元素都放到金额元素map中
        accountAmountMap.putAll(accountAmountBCCYAMTMap);
        /**定义 定义 变量.买卖价差_本位币	=	(金额元素.实收金额_本位币 + 金额元素.冲减溢折价（借）_本位币)-(金额元素.面值_本位币
         * + 金额元素.应计利息_本位币 + 金额元素.冲减溢折价（贷）_本位币)
         **/

        BigDecimal 买卖价差_本位币 = ArithmeticUtil.doSub(
                ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.DSCINC_D + BookConstant.BCCYAMT)
                ),
                accountAmountMap.get(BookConstant.FACEVALUE + BookConstant.BCCYAMT),
                accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT),
                accountAmountMap.get(BookConstant.DSCINC_C + BookConstant.BCCYAMT)
        );
        //if（变量.买卖价差_本位币>= 0）
        if(买卖价差_本位币.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.买卖价差（贷）_本位币								<-	变量.买卖价差_本位币
            accountAmountMap.put(BookConstant.DSCLOSS_C + BookConstant.BCCYAMT,
                    ArithmeticUtil.round(买卖价差_本位币, NumberEnum.MONEY));
        }
        //else
        else {
            //金额元素.买卖价差（借）_本位币								<-	变量.买卖价差_本位币*-1
            accountAmountMap.put(BookConstant.DSCLOSS_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(买卖价差_本位币, new BigDecimal(-1)), NumberEnum.MONEY));
        }

        // modify by yanya on 20210326 特殊处理赎回结转数量
        accountSceneMap.put(BookConstant.HAPPEN_NUMBER, ArithmeticUtil.round(冲减数量, NumberEnum.QTY));
        /** modify by yanya on 20210325 科目余额计算成本、利息、减值使用变量计算：避免同一批处理处理金额错误 start**/
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE,
                param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE_NUMBER,
                param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate())));//余额数量_本次发生前
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE+BookConstant.BCCYAMT,
                param.getJzrcbkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.FACEVALUE+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE,
                param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE+BookConstant.BCCYAMT,
                param.getJzryjlxyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.INTAMT+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE, param.getVarJzzbSubjectAmt());//科目余额_本次发生前
        accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
        accountAmountMap.put(BookConstant.IMPAMT+BookConstant.BALANCE+BookConstant.BCCYAMT, param.getVarJzzbSubjectAmtBccy());//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.IMPAMT+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        /** modify by yanya on 20210325 科目余额计算成本、利息、减值使用变量计算：避免同一批处理处理金额错误 end**/
        /** modify by yanya on 20210326 公允价值变动使用变量计算：避免同一批处理处理金额错误 start**/
        if (冲减公允价值变动.compareTo(BigDecimal.ZERO) >= 0) {
            accountAmountMap.put(BookConstant.FLOAT_C+BookConstant.BALANCE,
                    param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_C+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_C+BookConstant.BALANCE+BookConstant.BCCYAMT,
                    param.getJzrgyjzbdMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.FLOAT_C+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        else {
            accountAmountMap.put(BookConstant.FLOAT_D+BookConstant.BALANCE,
                    param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_D+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_D+BookConstant.BALANCE+BookConstant.BCCYAMT,
                    param.getJzrgyjzbdMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.FLOAT_D+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        /** modify by yanya on 20210326 公允价值变动使用变量计算：避免同一批处理处理金额错误 end**/
        /** modify by yanya on 20210326 溢折价使用变量计算：避免同一批处理处理金额错误 start**/
        if (冲减溢折价.compareTo(BigDecimal.ZERO) >= 0) {
            accountAmountMap.put(BookConstant.DSCINC_C+BookConstant.BALANCE,
                    param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_C+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_C+BookConstant.BALANCE+BookConstant.BCCYAMT,
                    param.getJzryzjkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.DSCINC_C+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        else {
            accountAmountMap.put(BookConstant.DSCINC_D+BookConstant.BALANCE,
                    param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_D+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_D+BookConstant.BALANCE+BookConstant.BCCYAMT,
                    param.getJzryzjkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.DSCINC_D+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        /** modify by yanya on 20210326 溢折价使用变量计算：避免同一批处理处理金额错误 end**/

        // map塞入场景要素管理中
        // 记账场景要素
        ad.setAccountSceneMap(accountSceneMap);
        // 记账金额要素
        ad.setAccountAmountMap(accountAmountMap);
        // 记账金额币种要素
        ad.setAccountCurrencyMap(accountCurrencyMap);
        // 基础科目性质
        Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
        Map<String, Object> accountNatrueBranchMap = new HashMap<>();
        accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
        accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(), accountNatrueBranchMap);
        ad.setAccountNatrueMap(accountNatrueMap);
        //add by weipeng on 20220325：增加辅助核算项配置
        ad.setSubjectMappingConfigDefault(dealNow.getSubjectMappingConfig());
        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        //全局变量的更新
        //变量.成本科目余额 								=	变量.成本科目余额 - 变量.冲减成本
        // 变量.成本科目余额 = 变量.成本科目余额 - 变量.冲减成本
        param.setVarCostSubjectAmt(ArithmeticUtil.subtract(param.getVarCostSubjectAmt(), 冲减成本));
        // 变量.成本科目数量 = 变量.成本科目数量 - 变量.冲减数量
        param.setVarCostSubjectNum(ArithmeticUtil.subtract(param.getVarCostSubjectNum(), 冲减数量));
        // 变量.溢折价科目余额 = 变量.溢折价科目余额 - 变量.冲减公允价值变动
        param.setVarYzjSubjectAmt(ArithmeticUtil.subtract(param.getVarYzjSubjectAmt(), 冲减溢折价));
        //变量.应计利息科目余额								=	变量.应计利息科目余额 - 变量.冲减应计利息
        param.setVarLxSubjectAmt(ArithmeticUtil.subtract(param.getVarLxSubjectAmt(), 冲减应计利息));
        //变量.减值准备科目余额								=	变量.减值准备科目余额 - 变量.减值使用
        param.setVarJzzbSubjectAmt(ArithmeticUtil.subtract(param.getVarJzzbSubjectAmt(), 冲抵减值金额));
        //变量.公允价值变动科目余额 								=	变量.公允价值变动科目余额 - 变量.冲减公允价值变动
        param.setVarGySubjectAmt(ArithmeticUtil.subtract(param.getVarGySubjectAmt(), 冲减公允价值变动));

        //变量.结转日应计利息余额Map.put(交易明细.到期日,变量.结转日应计利息余额 - 变量.冲减应计利息)
        param.getJzryjlxyeMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate())), 冲减应计利息));
        //变量.结转日成本科目余额Map.put(交易明细.到期日,变量.结转日成本科目余额 -变量.冲减成本)
        param.getJzrcbkmyeMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate())), 冲减成本));
        //变量.结转日成本科目数量Map.put(交易明细.到期日,变量.结转日成本科目数量 - 变量.冲减数量)
        param.getJzrcbkmslMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate())), 冲减数量));
        //变量.结转日溢折价科目余额.put(交易明细.到期日,变量.结转日溢折价科目余额 - 变量.冲减溢折价)
        param.getJzryzjkmyeMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate())), 冲减溢折价));
        //变量.结转日公允价值变动科目余额Map.put(交易明细.到期日,变量.结转日公允价值变动科目余额 - 变量.冲减公允价值变动)
        param.getJzrgyjzbdMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate())), 冲减公允价值变动));

        /** modify by yanya on 20210325 增加科目成本、利息、减值本位币处理**/
        param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrcbkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())),
                        accountAmountMap.get(BookConstant.FACEVALUE+BookConstant.BCCYAMT)));
        param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzryjlxyeMapBccy().get(DateUtil.format(dealNow.getMdate())),
                        accountAmountMap.get(BookConstant.INTAMT+BookConstant.BCCYAMT)));
        param.setVarJzzbSubjectAmtBccy(ArithmeticUtil.doAdd(param.getVarJzzbSubjectAmtBccy(),
                accountAmountMap.get(BookConstant.IMPAMT+BookConstant.BCCYAMT)));

        /** modify by yanya on 20210326 增加公允价值变动本位币处理**/
        param.getJzrgyjzbdMapBccy().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrgyjzbdMapBccy().get(DateUtil.format(dealNow.getMdate())),
                        ArithmeticUtil.round(calculationCommonService.doMultiply(冲减公允价值变动, changeRate),NumberEnum.MONEY)));
        /** modify by yanya on 20210326 增加溢折价本位币处理**/
        param.getJzryzjkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzryzjkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())),
                        ArithmeticUtil.round(calculationCommonService.doMultiply(冲减溢折价, changeRate),NumberEnum.MONEY)));

        //if(金额元素.减值使用 != 0)
        if(冲抵减值金额 != null && 冲抵减值金额.compareTo(BigDecimal.ZERO) != 0) {
            //变量.账务明细 = 调用账务明细生成共通返回结果中 基础科目性质 ="摊余成本类资产减值"的第一条数据
            //调用【减值相关共通-减值使用明细数据生成】
            //参数名：					减值使用明细数据生成
            //账务明细					变量.账务明细
            //减值金额					金额元素.减值使用
            //减值金额本币					金额元素.减值使用_本位币
            //减值余额					变量.减值准备科目余额
            //减值余额本币					变量.减值准备科目余额* 变量.汇率
            //返回值:					统计分析明细表（资产减值）entity
            // 变量.统计分析明细表（资产减值）减值使用列表.add(上述返回值)
            List<ValBokBookdetailBatch> valBokBookdetailBatch = resultNowNow.getValBokBookdetailBatch();
            List<ValBokBookdetailBatch> valBokBookdetailBatchResult = new ArrayList<ValBokBookdetailBatch>();
            for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
                if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrueNew.其他综合收益_减值准备.getValue())) {
                    valBokBookdetailBatchResult.add(dto);
                }
            }
            if (CollectionUtils.isNotEmpty(valBokBookdetailBatchResult)) {
                ValBokBookdetailBatch valBokBookdetailBatchDto = valBokBookdetailBatchResult.get(0);
                BokStatLossPosiiton bokStatLossPosiiton = lossCommonService.setLossInfoUsed(valBokBookdetailBatchDto,
                        accountAmountMap.get(BookConstant.IMPAMT),
                        accountAmountMap.get(BookConstant.IMPAMT+ BookConstant.BCCYAMT),
                        param.getVarJzzbSubjectAmt(),
                        calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null,
                                param.getVarJzzbSubjectAmt(), changeRate)
                        ,dealNow.getTradeId());
                // 统计分析明细表（资产减值）
                returnResult.getBokStatLossPosiitonList().add(bokStatLossPosiiton);
            }
        }

    }

    /**
     * 凭证最新账务查询
     * @param invAim 投资目的
     * @param isBuyDeal 是否是买入交易
     * @param dealNow 交易明细
     * @param acctEntity read结果
     * @return
     * @see
     * @since 1.0
     */
    private List<BookLatestInfoDto> getBookLatestInfo(String invAim, boolean isBuyDeal,
                                                      FinProductDealInfoComDTO dealNow,
                                                      FinProductDealInfoResult acctEntity) {
        String tableId = null;
        List<String> baseSubjectNatureList = new ArrayList<String>();
        // 买入场景
        if (isBuyDeal) {
            if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(
                    acctEntity.getInvAim())) {
                tableId = DictCodeListTableId.会计场景代码_公允价值损益类债券_买入.getValue();
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue());
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue());
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue());
            }
            else if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(acctEntity.getInvAim())) {
                tableId = DictCodeListTableId.会计场景代码_摊余成本类债券_买入.getValue();
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue());
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.摊余成本类_利息调整.getValue());
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue());
            }
            else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(
                    acctEntity.getInvAim())) {
                tableId = DictCodeListTableId.会计场景代码_综合收益类债券_买入.getValue();
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue());
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue());
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue());
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.综合收益类_公允价值变动.getValue());
            }
        }
        else {
            if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(
                    acctEntity.getInvAim())) {
                tableId = DictCodeListTableId.会计场景代码_公允价值损益类债券_卖出.getValue();
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue());
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue());
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue());
            }
            else if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(acctEntity.getInvAim())) {
                tableId = DictCodeListTableId.会计场景代码_摊余成本类债券_卖出.getValue();
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue());
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.摊余成本类_利息调整.getValue());
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue());
            }
            else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(
                    acctEntity.getInvAim())) {
                tableId = DictCodeListTableId.会计场景代码_综合收益类债券_卖出.getValue();
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue());
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue());
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue());
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.综合收益类_公允价值变动.getValue());
            }
        }
        // 调用账务共通设计->【凭证最新账务查询】
        List<BookLatestInfoDto> listBookLatestInfoDto = accountingCommonService.getBookLatestInfo(
                acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntity.getBokDetailId(),
                baseSubjectNatureList, tableId, null, null, addDate(dealNow.getMdate(), -1), null,
                null);
        return listBookLatestInfoDto;
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

    /**
     * 公共场景赋值
     *
     * @param accountSceneMap 场景要素 map
     * @param dealNow 金融产品交易信息
     * @param rstBaseinfo 标的信息
     * @see
     * @since 1.0
     */
    private void getAccountSceneMap(Map<String, Object> accountSceneMap,
                                    FinProductDealInfoComDTO dealNow,
                                    TargetFinProduct rstBaseinfo) {
        // 境内外
        accountSceneMap.put(BookConstant.BLN_AREA, rstBaseinfo.getBlnArea());
        // 分类1
        accountSceneMap.put(BookConstant.TYPE_1, rstBaseinfo.getType1());
        // 分类2
        accountSceneMap.put(BookConstant.TYPE_2, rstBaseinfo.getType2());
        // 分类3
        accountSceneMap.put(BookConstant.TYPE_3, rstBaseinfo.getType3());
        // 分类4
        accountSceneMap.put(BookConstant.TYPE_4, rstBaseinfo.getType4());
        // 分类5
        accountSceneMap.put(BookConstant.TYPE_5, rstBaseinfo.getType5());
        // 分类6
        accountSceneMap.put(BookConstant.TYPE_6, rstBaseinfo.getType6());
        // 交易编号
        accountSceneMap.put(BookConstant.TRADE_ID, dealNow.getTradeId());

        // 冲正标识
        accountSceneMap.put(BookConstant.OFFSET_FLAG, DictCodeList.冲正标识_正常交易.getValue());
        // 发生数量->交易明细.交易份额
        accountSceneMap.put(BookConstant.HAPPEN_NUMBER, ArithmeticUtil.round(dealNow.getShareAmt(), NumberEnum.QTY));
        if (DictCodeList.交易方向_买入.getValue().equals(dealNow.getPs())
                || DictCodeList.交易方向_卖出.getValue().equals(dealNow.getPs())) {
            if (DictCodeListExt1.估值交易市场_银行间.getValue().equals(dealNow.getTradeMarket())) {
                accountSceneMap.put(BookConstant.ONWAY_DATE, dealNow.getTradeDate().compareTo(
                        dealNow.getSettleDate()) == 0 ? null : dealNow.getSettleDate());
            }
            else {
                accountSceneMap.put(BookConstant.ONWAY_DATE,
                        DateUtils.addDays(dealNow.getTradeDate(), 1));
            }
        }
        // 是否累计余额->是
        accountSceneMap.put(BookConstant.IS_ACC_BALANCE, DictCodeList.是否_是.getValue());

        // 登记托管机构 <- 变量.标的信息.登记托管机构
        accountSceneMap.put(BookConstant.REGIST_ORG, rstBaseinfo.getRegistOrg());
    }

    /**
     * 冲减减值计算DTO初始化
     * @param acctEntity
     * @param dealNow
     * @param flag
     * @param happenAmt
     * @param lossBalAmt
     * @param happenNum
     * @param EADAmt
     * @param totalNum
     * @return
     */
    private LossAmtInputDto getLossAmtInputDto(FinProductDealInfoResult acctEntity,FinProductDealInfoComDTO dealNow,
                                               String flag,//01 本金  02 利息
                                               BigDecimal happenAmt,//发生金额
                                               BigDecimal lossBalAmt,//减值准备余额
                                               BigDecimal happenNum,//减值准备余额
                                               BigDecimal EADAmt, //当前EAD金额
                                               BigDecimal totalNum //总份额
    ){
        LossAmtInputDto dto = new LossAmtInputDto();
        //本息标识
        dto.setFlag(flag);
        //账套代码
        dto.setBooksetId(acctEntity.getBooksetId());
        //账务明细代码
        dto.setBokDetailId(acctEntity.getBokDetailId());
        //批处理优先级
        dto.setTaskPri(dealNow.getTaskPri());
        //分支序号
        dto.setBranch(dealNow.getBranch());
        //投资目的
        dto.setInvAim(dealNow.getInvAim());
        //发生日
        dto.setHappenDate(acctEntity.getHappenDate());
        //记账日
        dto.setBookDate(acctEntity.getBookDate());
        //发生份额
        //dto.setHappenNum(dealNow.getShareAmt());
        dto.setHappenNum(happenNum);
        //总份额
        //dto.setTotalNum(dealNow.getShareAmt());
        dto.setTotalNum(totalNum);
        //发生金额
        dto.setHappenAmt(ArithmeticUtil.round(happenAmt, NumberEnum.MONEY));
        //减值准备余额
        dto.setLossBalAmt(lossBalAmt);
        //当前EAD金额 = 变量.成本科目余额 + 变量.应计利息科目余额  + 变量.其他应收款本金汇总余额 + 变量.其他应收款利息汇总月 + 变量.逾期利息汇总余额
        dto.setEADAmt(EADAmt);
        //业务类型
        dto.setBusiType(dealNow.getFinprodType());
        return dto;
    }

    /**
     * 公允价值损益类 行权回售申请--摊余成本法（现管类）
     * @param rstBaseinfo
     * @param returnResult
     * @param dealNow
     * @param acctEntity
     * @param param
     */
    public void createExecHSFvtplCashManage(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
                               FinProductDealInfoComDTO dealNow,
                               FinProductDealInfoResult acctEntity, DealParamterDto param){
        // 账套核算参数查询
        // 定义账套参数查询参数项list
        List<String> paramCodeList = new ArrayList<String>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_债券交易费用损益计入方式.getValue());// 债券交易费用损益计入方式
        Map<String, Object> getValBokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(
                acctEntity.getBooksetId(), paramCodeList);
        // 定义 变量.费用损益计入方式
        String varSecDealFeeSubject = (String)getValBokBooksetExtMap.get(
                ValDictCodeList.账套配置表参数项_债券交易费用损益计入方式.getValue());// 债券交易费用损益计入方式
        // 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();
        // 汇率
        BigDecimal changeRate = BigDecimal.ONE;
        if(StringUtil.isNotEmpty(dealNow.getCcy()) && !dealNow.getCcy().equals(acctEntity.getStandardCcy())){
            // 调用共通汇率查询
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                acctEntity.getStandardCcy(), dealNow.getCcy());
            if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                changeRate = currencyMarketList.get(0).getClosePrice();
            }
        }
        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());
        ad.setTableId(DictCodeListTableId.会计场景代码_公允价值损益类债券_回售申请.getValue());
        // accountSceneMap设置
        // 备注
        accountSceneMap.put(BookConstant.BOOK_REMARK, "公允价值损益类债券资产" + DictCodeListExt3.交易方向_行权回售申请.getCodeName() + "交易登账");
        // 公共场景赋值
        getAccountSceneMap(accountSceneMap, dealNow, rstBaseinfo);
        //定义		变量.结转日应计利息余额 = 变量.结转日应计利息余额Map.get(交易明细.到期日)
        BigDecimal 结转日应计利息余额 = param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日成本科目余额 = 变量.结转日成本科目余额Map.get(交易明细.到期日）
        BigDecimal 结转日成本科目余额 = param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日成本科目数量 = 变量.结转日成本科目数量Map.get(交易明细.到期日)
        BigDecimal 结转日成本科目数量 = param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日公允价值变动科目余额 = 变量.结转日公允价值变动科目余额Map.get(交易明细.到期日)
        BigDecimal 结转日公允价值变动科目余额 = param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日溢折价科目余额 = 变量.结转日溢折价科目余额Map.get(交易明细.到期日)
        BigDecimal 结转日溢折价科目余额 = param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate()));
        List<SubjectBalance> listSubjectBalance = new ArrayList<SubjectBalance>();
        // 科目余额查询参数
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        searchDao.setBooksetId(acctEntity.getBooksetId());
        searchDao.setBokDetailId(acctEntity.getBokDetailId());
        searchDao.setHappenDate(addDate(dealNow.getMdate(), -1));
        searchDao.setBookDate(acctEntity.getBookDate());
        //IF 变量.结转日应计利息余额Map.get(交易明细.到期日) == NULL
        if(结转日应计利息余额 == null) {
            //查询交易的到期日对应的应计利息
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.应计利息余额 = 科目余额
                结转日应计利息余额 = listSubjectBalance.get(0).getBalance();
                /** modify by yanya on 20210325 增加利息本位币处理**/
                param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalance.get(0).getbAmt());
            }
            else {
                结转日应计利息余额 = BigDecimal.ZERO;
                /** modify by yanya on 20210325 增加利息本位币处理**/
                param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzryjlxyeMap().put(DateUtil.format(dealNow.getMdate()), 结转日应计利息余额);
        }
        //IF 变量.结转日成本科目余额Map.get(交易明细.到期日） IS NULL
        if(结转日成本科目余额 == null) {
            searchDao.setBaseSubjectNatureList(
                    new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.结转日成本科目余额 = 科目余额
                结转日成本科目余额 = listSubjectBalance.get(0).getBalance();
                // 定义 变量.结转日成本科目数量 = 数量
                结转日成本科目数量 = listSubjectBalance.get(0).getNum();
                /** modify by yanya on 20210325 增加科目成本本位币处理**/
                param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalance.get(0).getbAmt());
            }
            else {
                结转日成本科目余额 = BigDecimal.ZERO;
                结转日成本科目数量 = BigDecimal.ZERO;
                /** modify by yanya on 20210325 增加科目成本、利息本位币处理**/
                param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzrcbkmyeMap().put(DateUtil.format(dealNow.getMdate()), 结转日成本科目余额);
            param.getJzrcbkmslMap().put(DateUtil.format(dealNow.getMdate()), 结转日成本科目数量);
        }
        //BigDecimal varGyjzbdye = BigDecimal.ZERO;
        if(结转日公允价值变动科目余额 == null) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue())));
            List<SubjectBalance> listSubjectBalanceEx = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalanceEx != null && listSubjectBalanceEx.size() > 0) {
                // 变量.结转日公允价值变动科目余额 = 科目余额
                结转日公允价值变动科目余额 = listSubjectBalanceEx.get(0).getBalance();
                /** modify by yanya on 20210326 公允价值变动本位币处理**/
                param.getJzrgyjzbdMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalanceEx.get(0).getbAmt());
            }
            else {
                结转日公允价值变动科目余额 = BigDecimal.ZERO;
                /** modify by yanya on 20210326 公允价值变动本位币处理**/
                param.getJzrgyjzbdMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzrgyjzbdMap().put(DateUtil.format(dealNow.getMdate()), 结转日公允价值变动科目余额);
        }
        //BigDecimal varGyjzbdye = BigDecimal.ZERO;
        if(结转日溢折价科目余额 == null) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_利息调整.getValue())));
            List<SubjectBalance> listSubjectBalanceEx = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalanceEx != null && listSubjectBalanceEx.size() > 0) {
                // 变量.结转日公允价值变动科目余额 = 科目余额
                结转日溢折价科目余额 = listSubjectBalanceEx.get(0).getBalance();
                /** modify by yanya on 20210326 溢折价科目本位币处理**/
                param.getJzryzjkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalanceEx.get(0).getbAmt());
            }
            else {
                结转日溢折价科目余额 = BigDecimal.ZERO;
                /** modify by yanya on 20210326 溢折价科目本位币处理**/
                param.getJzryzjkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzryzjkmyeMap().put(DateUtil.format(dealNow.getMdate()), 结转日溢折价科目余额);
        }
        if (!DictCodeListExt1.估值交易市场_上交所.getValue().equals(rstBaseinfo.getTradeMarket())
            && !DictCodeListExt1.估值交易市场_深交所.getValue().equals(rstBaseinfo.getTradeMarket())) {
            // 买入场景
            // 调用账务共通设计->【凭证最新账务查询】
            List<BookLatestInfoDto> listBookLatestInfoDto = getBookLatestInfo(dealNow.getInvAim(),true, dealNow, acctEntity);
            // LOOP 变量.买入账务明细列表 -> 变量.账务明细
            if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                for (BookLatestInfoDto var : listBookLatestInfoDto) {
                    // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.结转日成本科目数量 -= 变量.账务明细.发生数量
                        结转日成本科目数量 = ArithmeticUtil.subtract(结转日成本科目数量, var.getHappenNumber());
                        // 变量.结转日成本科目余额 -= 变量.账务明细.原币发生额
                        结转日成本科目余额 = ArithmeticUtil.subtract(结转日成本科目余额, var.getoHappenAmt());
                    }
                    // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日公允价值变动科目余额 -= 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.subtract(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日公允价值变动科目余额 += 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.doAdd(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                    }
                    // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_利息调整.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日公允价值变动科目余额 -= 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.subtract(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日公允价值变动科目余额 += 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.doAdd(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                    }
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue().equals(var.getBaseSubjectNature())) {
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日应计利息余额 -= 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.subtract(结转日应计利息余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日应计利息余额 += 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.doAdd(结转日应计利息余额, var.getoHappenAmt());
                        }
                    }
                }
            }
            // 卖出场景
            // 调用账务共通设计->【凭证最新账务查询】
            listBookLatestInfoDto = getBookLatestInfo(dealNow.getInvAim(), false, dealNow, acctEntity);
            // LOOP 变量.卖出账务明细列表 -> 变量.账务明细
            if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                for (BookLatestInfoDto var : listBookLatestInfoDto) {
                    // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.结转日成本科目数量 += 变量.账务明细.发生数量
                        结转日成本科目数量 = ArithmeticUtil.doAdd(结转日成本科目数量, var.getHappenNumber());
                        // 变量.结转日成本科目余额 += 变量.账务明细.原币发生额
                        结转日成本科目余额 = ArithmeticUtil.doAdd(结转日成本科目余额, var.getoHappenAmt());
                    }
                    // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日公允价值变动科目余额 -= 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.subtract(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日公允价值变动科目余额 += 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.doAdd(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                    }
                    // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.摊余成本类_利息调整.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日公允价值变动科目余额 -= 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.subtract(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日公允价值变动科目余额 += 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.doAdd(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                    }
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue().equals(var.getBaseSubjectNature())) {
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日应计利息余额 -= 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.subtract(结转日应计利息余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日应计利息余额 += 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.doAdd(结转日应计利息余额, var.getoHappenAmt());
                        }
                    }
                }
            }
        }
        //定义 变量.冲减公允价值变动	=		变量.结转日公允价值变动科目余额 * 交易明细.交易份额 / 变量.结转日成本科目数量
        BigDecimal 冲减公允价值变动 = ArithmeticUtil.divide(
                ArithmeticUtil.multiply(结转日公允价值变动科目余额, dealNow.getShareAmt()), 结转日成本科目数量);

        //定义 		变量.冲减溢折价		=		变量.结转日溢折价科目余额  * 交易明细.交易份额 / 变量.成本科目数量
        BigDecimal 冲减溢折价 = ArithmeticUtil.divide(
            ArithmeticUtil.multiply(结转日溢折价科目余额, dealNow.getShareAmt()), param.getVarCostSubjectNum());
        //定义 		变量.冲减应计利息		=		变量.结转日应计利息科目余额 * 交易明细.交易份额 / 变量.成本科目数量
        BigDecimal 冲减应计利息 = ArithmeticUtil.divide(
            ArithmeticUtil.multiply(结转日应计利息余额, dealNow.getShareAmt()), param.getVarCostSubjectNum());
        //定义 		变量.冲减成本		=		交易明细.交易份额 * 交易明细.单位面值
        BigDecimal 冲减成本 = ArithmeticUtil.multiply(dealNow.getShareAmt(), dealNow.getParValue());
        //定义 		变量.冲减数量		=		交易明细.交易份额
        BigDecimal 冲减数量 = dealNow.getShareAmt();
        //定义 		变量.利息差额		=		0
        BigDecimal 利息差额 = BigDecimal.ZERO;
        //定义 		变量.买卖价差		=		0
        BigDecimal 买卖价差 = BigDecimal.ZERO;
        //IF 变量.结转日成本科目数量 - 变量.冲减数量 == 0
        if(ArithmeticUtil.doSub(结转日成本科目数量, 冲减数量).compareTo(BigDecimal.ZERO) == 0) {
            //变量.冲减公允价值变动	=		变量.结转日公允价值变动科目余额
            冲减公允价值变动 = 结转日公允价值变动科目余额;
            //变量.冲减溢折价							=	变量.结转日溢折价科目余额
            冲减溢折价 = 结转日溢折价科目余额;
            //变量.冲减应计利息							=	变量.结转日应计利息科目余额
            冲减应计利息 = 结转日应计利息余额;
        }
        if (StringUtil.equals(DictCodeList.息票品种_零息.getValue(), rstBaseinfo.getCouponSpecies())
            || StringUtil.equals(DictCodeList.息票品种_贴现.getValue(),rstBaseinfo.getCouponSpecies())) {
            冲减应计利息 = BigDecimal.ZERO;
        }
        else {
            //变量.利息差额	=		交易明细.利息总额 - 变量.冲减应计利息
            利息差额 = ArithmeticUtil.doSub(dealNow.getIntAmt(), 冲减应计利息);
        }
        //调用 共通【费用处理共通】  参考本设计书sheet【费用处理共通】
        DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
            dealNow.getValTradeFeeDetailList(), dealNow.getProdId(), dealNow.getPortfolioId());
        // 印花税
        accountAmountMap.put(BookConstant.STAMPTAX, dealTypesFeeDto.getStampDuty());
        // 经手费
        accountAmountMap.put(BookConstant.HANDFEE, dealTypesFeeDto.getBrokerage());
        // 证管费
        accountAmountMap.put(BookConstant.CERTIFEE, dealTypesFeeDto.getCertificateFee());
        // 结算费
        accountAmountMap.put(BookConstant.SETTLEFEE, dealTypesFeeDto.getClearingFee());
        // 佣金
        accountAmountMap.put(BookConstant.COMMISSION, dealTypesFeeDto.getCommission());
        // 交易手续费
        accountAmountMap.put(BookConstant.TRADEFEE, dealTypesFeeDto.getTransactionFee());
        // 结算手续费
        accountAmountMap.put(BookConstant.SETTLEMENTFEE, dealTypesFeeDto.getSettlementFee());
        //实付通道费
        accountAmountMap.put(BookConstant.CHLAMT, dealTypesFeeDto.getChlFee());
        // 实收金额
        accountAmountMap.put(BookConstant.DEALAMT, ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));
        //成本金额 = 变量.冲减成本
        accountAmountMap.put(BookConstant.FACEVALUE, ArithmeticUtil.round(冲减成本, NumberEnum.MONEY));
        //应计利息 = 冲减应计利息
        accountAmountMap.put(BookConstant.INTAMT, ArithmeticUtil.round(冲减应计利息, NumberEnum.MONEY));
        // 变量.费用损益计入方式 = '计入投资收益'情况
        if (DictCodeList.债券交易费用损益计入方式_计入投资收益.getValue().equals(varSecDealFeeSubject)) {
            // 变量.买卖价差 <- 0
            买卖价差 = BigDecimal.ZERO;
        }
        else {
            // 印花税支出 <- 变量.交易各种费用.印花税 + 变量.交易各种费用.随交易支付_印花税
            BigDecimal VAR_STAMPTAX_OUT = ArithmeticUtil.doAdd(dealTypesFeeDto.getStampDuty(),dealTypesFeeDto.getStampDutyForDealPay());
            accountAmountMap.put(BookConstant.STAMPTAX_OUT, VAR_STAMPTAX_OUT);
            // 经手费支出 <- 变量.交易各种费用.经手费 + 变量.交易各种费用.随交易支付_经手费
            BigDecimal VAR_HANDFEE_OUT = ArithmeticUtil.doAdd(dealTypesFeeDto.getBrokerage(),dealTypesFeeDto.getBrokerageForDealPay());
            accountAmountMap.put(BookConstant.HANDFEE_OUT, VAR_HANDFEE_OUT);
            // 证管费支出 <- 变量.交易各种费用.证管费 + 变量.交易各种费用.随交易支付_证管费
            BigDecimal VAR_CERTIFEE_OUT = ArithmeticUtil.doAdd(dealTypesFeeDto.getCertificateFee(),dealTypesFeeDto.getCertificateFeeForDealPay());
            accountAmountMap.put(BookConstant.CERTIFEE_OUT, VAR_CERTIFEE_OUT);
            // 结算费支出 <- 变量.交易各种费用.结算费 + 变量.交易各种费用.随交易支付_结算费
            BigDecimal VAR_SETTLEFEE_OUT = ArithmeticUtil.doAdd(dealTypesFeeDto.getClearingFee(),dealTypesFeeDto.getClearingFeeForDealPay());
            accountAmountMap.put(BookConstant.SETTLEFEE_OUT, VAR_SETTLEFEE_OUT);
            // 佣金支出 <- 变量.交易各种费用.佣金 + 变量.交易各种费用.随交易支付_佣金
            BigDecimal VAR_COMMISSION_OUT = ArithmeticUtil.doAdd(dealTypesFeeDto.getCommission(),dealTypesFeeDto.getCommissionForDealPay());
            accountAmountMap.put(BookConstant.COMMISSION_OUT, VAR_COMMISSION_OUT);
            // 交易手续费支出 <- 变量.交易各种费用.交易手续费 + 变量.交易各种费用.随交易支付_交易手续费
            BigDecimal VAR_TRADEFEE_OUT = ArithmeticUtil.doAdd(dealTypesFeeDto.getTransactionFee(), dealTypesFeeDto.getTransactionFeeForDealPay());
            accountAmountMap.put(BookConstant.TRADEFEE_OUT, VAR_TRADEFEE_OUT);
            // 结算手续费支出 <- 变量.交易各种费用.结算手续费 + 变量.交易各种费用.随交易支付_结算手续费
            BigDecimal VAR_SETTLEMENTFEE_OUT = ArithmeticUtil.doAdd(dealTypesFeeDto.getSettlementFee(), dealTypesFeeDto.getSettlementFeeForDealPay());
            accountAmountMap.put(BookConstant.SETTLEMENTFEE_OUT, VAR_SETTLEMENTFEE_OUT);
            // 变量.买卖价差 <- 印花税支出 + 经手费支出 + 证管费支出 + 结算费支出 + 佣金支出 + 交易手续费支出 + 结算手续费支出
            买卖价差 = ArithmeticUtil.doAdd(VAR_STAMPTAX_OUT, VAR_HANDFEE_OUT, VAR_CERTIFEE_OUT,VAR_SETTLEFEE_OUT,
                    VAR_COMMISSION_OUT, VAR_TRADEFEE_OUT, VAR_SETTLEMENTFEE_OUT);
        }
        /**变量.买卖价差 =	交易明细.交易金额 - (变量.冲减成本 + 变量.冲减溢折价 ) - 变量.冲减应计利息 - 变量.交易各种费用.不随交易费用
         + 各种变量.交易各种费用.通道费 - 变量.利息差额**/
       买卖价差 = ArithmeticUtil.doSub(
                ArithmeticUtil.doAdd(买卖价差, dealNow.getTradeAmt(), dealTypesFeeDto.getChlFee()),
                        冲减成本, dealTypesFeeDto.getNoFeeAmt(), 冲减应计利息, 冲减溢折价, 利息差额);
        //if(变量.利息差额 >= 0)
        if(利息差额.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.利息差额（贷）		<-变量.利息差额
            accountAmountMap.put(BookConstant.INCOMEAMT_C, ArithmeticUtil.round(利息差额, NumberEnum.MONEY));
        }
        else {
            //金额元素.利息差额（借）								<-变量.利息差额 * -1
            accountAmountMap.put(BookConstant.INCOMEAMT_D, ArithmeticUtil.round(
                ArithmeticUtil.multiply(利息差额, new BigDecimal(-1)), NumberEnum.MONEY));
        }

        //if（变量.买卖价差>= 0）
        if(买卖价差.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.买卖价差（贷）								<-变量.买卖价差
            accountAmountMap.put(BookConstant.DSCLOSS_C, ArithmeticUtil.round(买卖价差, NumberEnum.MONEY));
        }
        else {
            //金额元素.买卖价差（借）								<-变量.买卖价差 * -1
            accountAmountMap.put(BookConstant.DSCLOSS_D, ArithmeticUtil.round(
                ArithmeticUtil.multiply(买卖价差, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        //if(变量.冲减公允价值变动 >= 0 )
        if(冲减公允价值变动.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.公允价值变动(贷) < -变量.冲减公允价值变动
            accountAmountMap.put(BookConstant.FLOAT_C, ArithmeticUtil.round(冲减公允价值变动, NumberEnum.MONEY));
            //金额元素.冲减公允价值变动(借) < -变量.冲减公允价值变动
            accountAmountMap.put(BookConstant.FLOAT_1_D, ArithmeticUtil.round(冲减公允价值变动, NumberEnum.MONEY));
        }
        else {
            //金额元素.公允价值变动(借) < -变量.冲减公允价值变动 * -1
            accountAmountMap.put(BookConstant.FLOAT_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(冲减公允价值变动, new BigDecimal(-1)), NumberEnum.MONEY));
            //金额元素.冲减公允价值变动(贷) < -变量.冲减公允价值变动 * -1
            accountAmountMap.put(BookConstant.FLOAT_1_C, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(冲减公允价值变动, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        //if(变量.冲减溢折价 >= 0 )
        if(冲减溢折价.compareTo(BigDecimal.ZERO) >= 0) {
            // 剩余溢折价(贷) <- 变量.冲减溢折价
            accountAmountMap.put(BookConstant.DSCINC_C,
                ArithmeticUtil.round(冲减溢折价, NumberEnum.MONEY));
        }
        else {
            // 剩余溢折价(借) <- 变量.冲减溢折价 * -1
            accountAmountMap.put(BookConstant.DSCINC_D, ArithmeticUtil.round(
                ArithmeticUtil.multiply(冲减溢折价, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        Map<String, BigDecimal> accountAmountBCCYAMTMap = new HashMap<>();
        //本位币金额元素赋值 循环金额元素
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());// value值塞入当前交易币种
            accountAmountBCCYAMTMap.put(key + BookConstant.BCCYAMT,
                ArithmeticUtil.round(calculationCommonService.doMultiply(accountAmountMap.get(key), changeRate),NumberEnum.MONEY));
        }
        /** 本位币 币种**/
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
        //本位币金额元素都放到金额元素map中
        accountAmountMap.putAll(accountAmountBCCYAMTMap);
        //accountAmountMap再次设置如下：
        //定义		变量.利息差额(贷)_本位币 = round(变量.利息差额,共通金额小数位) * 变量.汇率																																		保留金额元素小数位数
        BigDecimal 利息差额贷_本位币 = ArithmeticUtil.round(
            calculationCommonService.doMultiply(利息差额, changeRate),NumberEnum.MONEY);
        //定义		变量.冲减溢折价（贷）_本位币 = round(变量.冲减溢折价,共通金额小数位) * 变量.汇率
        BigDecimal 冲减溢折价贷_本位币 = ArithmeticUtil.round(
            calculationCommonService.doMultiply(冲减溢折价, changeRate),NumberEnum.MONEY);
        // 保留金额元素小数位数
        /**定义 变量.买卖价差_本位币 =	(金额元素.实收金额_本位币 + 金额元素.实付通道费_本位币)
         - (金额元素.面值_本位币 + 金额元素.印花税_本位币 + 金额元素.经手费_本位币 + 金额元素.证管费_本位币
         + 金额元素.结算费_本位币 + 金额元素.佣金_本位币 + 金额元素.交易手续费_本位币
         + 金额元素.结算手续费_本位币 + 金额元素.冲减溢折价（贷）_本位币 + 变量.利息差额(贷)_本位币 + 金额元素.利息金额_本位币)
         **/

            BigDecimal 买卖价差_本位币 = ArithmeticUtil.doSub(
                ArithmeticUtil.doAdd(
                        accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.CHLAMT + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.STAMPTAX_OUT + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.HANDFEE_OUT + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.CERTIFEE_OUT + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.SETTLEFEE_OUT + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.COMMISSION_OUT + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.TRADEFEE_OUT + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.SETTLEMENTFEE_OUT + BookConstant.BCCYAMT)
                ),
                ArithmeticUtil.doAdd(
                        accountAmountMap.get(BookConstant.FACEVALUE + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.STAMPTAX + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.HANDFEE + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.CERTIFEE + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.SETTLEFEE + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.COMMISSION + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.TRADEFEE + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.SETTLEMENTFEE + BookConstant.BCCYAMT),
                        利息差额贷_本位币, 冲减溢折价贷_本位币, accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)
                )
        );
        //if（变量.买卖价差_本位币>= 0）
        if(买卖价差_本位币.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.买卖价差（贷）_本位币								<-	变量.买卖价差_本位币
            accountAmountMap.put(BookConstant.DSCLOSS_C + BookConstant.BCCYAMT,
                ArithmeticUtil.round(买卖价差_本位币, NumberEnum.MONEY));
        }
        //else
        else {
            //金额元素.买卖价差（借）_本位币								<-	变量.买卖价差_本位币*-1
            accountAmountMap.put(BookConstant.DSCLOSS_D, ArithmeticUtil.round(
                ArithmeticUtil.multiply(买卖价差_本位币, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        // 印花税账务明细代码 <- 变量.交易各种费用.印花税账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_STAMPTAX, dealTypesFeeDto.getStampDutyCode());
        // 经手费账务明细代码 <- 变量.交易各种费用.经手费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_HANDFEE, dealTypesFeeDto.getBrokerageCode());
        // 证管费账务明细代码 <- 变量.交易各种费用.证管费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_CERTIFEE,dealTypesFeeDto.getCertificateFeeCode());
        // 结算费账务明细代码 <- 变量.交易各种费用.结算费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEFEE,dealTypesFeeDto.getClearingFeeCode());
        // 佣金账务明细代码 <- 变量.交易各种费用.佣金账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_COMMISSION,dealTypesFeeDto.getCommissionCode());
        // 交易手续费账务明细代码 <- 变量.交易各种费用.交易手续费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_TRADEFEE, dealTypesFeeDto.getTransactionFeeCode());
        // 结算手续费账务明细代码 <- 变量.交易各种费用.结算手续费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEMENTFEE,dealTypesFeeDto.getSettlementFeeCode());
        // 通道费账务明细代码 <- 变量.交易各种费用.通道费账务明细代码
        accountSceneMap.put(BookConstant.BOK_DETAIL_ID2, dealTypesFeeDto.getChlFeeCode());

        /** modify by yanya on 20210325 科目余额计算成本、利息使用变量计算：避免同一批处理处理金额错误 start**/
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE,
            param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE_NUMBER,
            param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate())));//余额数量_本次发生前
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE+BookConstant.BCCYAMT,
            param.getJzrcbkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.FACEVALUE+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE,
            param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE+BookConstant.BCCYAMT,
            param.getJzryjlxyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.INTAMT+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        /** modify by yanya on 20210325 科目余额计算成本、利息使用变量计算：避免同一批处理处理金额错误 end**/
        /** modify by yanya on 20210326 公允价值变动使用变量计算：避免同一批处理处理金额错误 start**/
        if (冲减公允价值变动.compareTo(BigDecimal.ZERO) >= 0) {
            accountAmountMap.put(BookConstant.FLOAT_C+BookConstant.BALANCE,
                    param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_C+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_C+BookConstant.BALANCE+BookConstant.BCCYAMT,
                    param.getJzrgyjzbdMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.FLOAT_C+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        else {
            accountAmountMap.put(BookConstant.FLOAT_D+BookConstant.BALANCE,
                    param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_D+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_D+BookConstant.BALANCE+BookConstant.BCCYAMT,
                    param.getJzrgyjzbdMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.FLOAT_D+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        /** modify by yanya on 20210326 公允价值变动使用变量计算：避免同一批处理处理金额错误 end**/
        /** modify by yanya on 20210326 溢折价使用变量计算：避免同一批处理处理金额错误 start**/
        if (冲减溢折价.compareTo(BigDecimal.ZERO) >= 0) {
            accountAmountMap.put(BookConstant.DSCINC_C+BookConstant.BALANCE,
                param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_C+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_C+BookConstant.BALANCE+BookConstant.BCCYAMT,
                param.getJzryzjkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.DSCINC_C+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        else {
            accountAmountMap.put(BookConstant.DSCINC_D+BookConstant.BALANCE,
                param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_D+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_D+BookConstant.BALANCE+BookConstant.BCCYAMT,
                param.getJzryzjkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.DSCINC_D+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        /** modify by yanya on 20210326 溢折价使用变量计算：避免同一批处理处理金额错误 end**/

        // map塞入场景要素管理中
        // 记账场景要素
        ad.setAccountSceneMap(accountSceneMap);
        // 记账金额要素
        ad.setAccountAmountMap(accountAmountMap);
        // 记账金额币种要素
        ad.setAccountCurrencyMap(accountCurrencyMap);
        // 基础科目性质
        Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
        Map<String, Object> accountNatrueBranchMap = new HashMap<>();
        accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
        accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(), accountNatrueBranchMap);
        ad.setAccountNatrueMap(accountNatrueMap);
        //add by weipeng on 20220325：增加辅助核算项配置
        ad.setSubjectMappingConfigDefault(dealNow.getSubjectMappingConfig());
        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        //全局变量的更新
        //变量.成本科目余额 								=	变量.成本科目余额 - 变量.冲减成本
        // 变量.成本科目余额 = 变量.成本科目余额 - 变量.冲减成本
        param.setVarCostSubjectAmt(ArithmeticUtil.subtract(param.getVarCostSubjectAmt(), 冲减成本));
        // 变量.成本科目数量 = 变量.成本科目数量 - 变量.冲减数量
        param.setVarCostSubjectNum(ArithmeticUtil.subtract(param.getVarCostSubjectNum(), 冲减数量));
        // 变量.公允价值变动科目余额	=	变量.公允价值变动科目余额 - 变量.公允价值变动
        param.setVarGySubjectAmt(ArithmeticUtil.subtract(param.getVarGySubjectAmt(), 冲减公允价值变动));
        // 变量.溢折价科目余额 = 变量.溢折价科目余额 - 变量.冲减溢折价
        param.setVarYzjSubjectAmt(ArithmeticUtil.subtract(param.getVarYzjSubjectAmt(), 冲减溢折价));
        //变量.应计利息科目余额								=	变量.应计利息科目余额 - 变量.冲减应计利息
        param.setVarLxSubjectAmt(ArithmeticUtil.subtract(param.getVarLxSubjectAmt(), 冲减应计利息));


        //变量.结转日应计利息余额Map.put(交易明细.到期日,变量.结转日应计利息余额 - 变量.冲减应计利息)
        param.getJzryjlxyeMap().put(DateUtil.format(dealNow.getMdate()),
            ArithmeticUtil.doSub(param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate())), 冲减应计利息));
        //变量.结转日成本科目余额Map.put(交易明细.到期日,变量.结转日成本科目余额 -变量.冲减成本)
        param.getJzrcbkmyeMap().put(DateUtil.format(dealNow.getMdate()),
            ArithmeticUtil.doSub(param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate())), 冲减成本));
        //变量.结转日成本科目数量Map.put(交易明细.到期日,变量.结转日成本科目数量 - 变量.冲减数量)
        param.getJzrcbkmslMap().put(DateUtil.format(dealNow.getMdate()),
            ArithmeticUtil.doSub(param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate())), 冲减数量));
        //变量.结转日公允价值变动科目余额Map.put(交易明细.到期日,变量.结转日公允价值变动科目余额 - 变量.冲减公允价值变动)
        param.getJzrgyjzbdMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate())), 冲减公允价值变动));
        //变量.结转日溢折价科目余额.put(交易明细.到期日,变量.结转日溢折价科目余额 - 变量.冲减溢折价)
        param.getJzryzjkmyeMap().put(DateUtil.format(dealNow.getMdate()),
            ArithmeticUtil.doSub(param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate())), 冲减溢折价));

        /** modify by yanya on 20210325 增加科目成本、利息、减值本位币处理**/
        param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()),
            ArithmeticUtil.doSub(param.getJzrcbkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())),
                accountAmountMap.get(BookConstant.FACEVALUE+BookConstant.BCCYAMT)));
        param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()),
            ArithmeticUtil.doSub(param.getJzryjlxyeMapBccy().get(DateUtil.format(dealNow.getMdate())),
                accountAmountMap.get(BookConstant.INTAMT+BookConstant.BCCYAMT)));
        param.setVarJzzbSubjectAmtBccy(ArithmeticUtil.doAdd(param.getVarJzzbSubjectAmtBccy(),
            accountAmountMap.get(BookConstant.IMPAMT+BookConstant.BCCYAMT)));
        /** modify by yanya on 20210326 增加溢折价本位币处理**/
        param.getJzryzjkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()),
            ArithmeticUtil.doSub(param.getJzryzjkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())),
                ArithmeticUtil.round(calculationCommonService.doMultiply(冲减溢折价, changeRate),NumberEnum.MONEY)));
        /** modify by yanya on 20210326 增加公允价值变动本位币处理**/
        param.getJzrgyjzbdMapBccy().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzrgyjzbdMapBccy().get(DateUtil.format(dealNow.getMdate())),
                        ArithmeticUtil.round(calculationCommonService.doMultiply(冲减公允价值变动, changeRate),NumberEnum.MONEY)));
    }

    /**
     * 公允价值损益类 行权赎回结转--摊余成本法（现管类）
     * @param rstBaseinfo
     * @param returnResult
     * @param dealNow
     * @param acctEntity
     * @param param
     */
    public void createExecJZFvtplCashManage(TargetFinProduct rstBaseinfo, BookGenerateResult returnResult,
                                  FinProductDealInfoComDTO dealNow,
                                  FinProductDealInfoResult acctEntity, DealParamterDto param) {
        // 账套核算参数查询
        // 定义账套参数查询参数项list
        List<String> paramCodeList = new ArrayList<String>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_债券交易费用损益计入方式.getValue());// 债券交易费用损益计入方式
        Map<String, Object> getValBokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(
            acctEntity.getBooksetId(), paramCodeList);
        // 定义 变量.费用损益计入方式
        String varSecDealFeeSubject = (String)getValBokBooksetExtMap.get(
            ValDictCodeList.账套配置表参数项_债券交易费用损益计入方式.getValue());// 债券交易费用损益计入方式
        // 初始化定义 - 记账场景要素管理
        AccountDriver ad = new AccountDriver();
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap = new HashMap<>();
        accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap = new HashMap<>();
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap = new HashMap<>();
        // 汇率
        BigDecimal changeRate = BigDecimal.ONE;
        if(StringUtil.isNotEmpty(dealNow.getCcy()) && !dealNow.getCcy().equals(acctEntity.getStandardCcy())){
            // 调用共通汇率查询
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                acctEntity.getStandardCcy(), dealNow.getCcy());
            if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                changeRate = currencyMarketList.get(0).getClosePrice();
            }
        }
        // 会计场景
        ad.setTreeNode(acctEntity.getBookSetTreeNode());
        ad.setTableId(DictCodeListTableId.会计场景代码_公允价值损益类债券_赎回结转.getValue());
        // accountSceneMap设置
        // 备注
        accountSceneMap.put(BookConstant.BOOK_REMARK, "公允价值损益类债券资产" + DictCodeListExt3.交易方向_行权赎回结转.getCodeName() + "交易登账");
        // 公共场景赋值
        getAccountSceneMap(accountSceneMap, dealNow, rstBaseinfo);
        //定义		变量.结转日应计利息余额 = 变量.结转日应计利息余额Map.get(交易明细.到期日)
        BigDecimal 结转日应计利息余额 = param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日成本科目余额 = 变量.结转日成本科目余额Map.get(交易明细.到期日）
        BigDecimal 结转日成本科目余额 = param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日成本科目数量 = 变量.结转日成本科目数量Map.get(交易明细.到期日)
        BigDecimal 结转日成本科目数量 = param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日溢折价科目余额 = 变量.结转日溢折价科目余额Map.get(交易明细.到期日)
        BigDecimal 结转日溢折价科目余额 = param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate()));
        //定义		变量.结转日公允价值变动科目余额 = 变量.结转日公允价值变动科目余额Map.get(交易明细.到期日)
        BigDecimal 结转日公允价值变动科目余额 = param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate()));
        List<SubjectBalance> listSubjectBalance = new ArrayList<SubjectBalance>();
        // 科目余额查询参数
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        searchDao.setBooksetId(acctEntity.getBooksetId());
        searchDao.setBokDetailId(acctEntity.getBokDetailId());
        searchDao.setHappenDate(addDate(dealNow.getMdate(), -1));
        searchDao.setBookDate(acctEntity.getBookDate());
        //IF 变量.结转日应计利息余额Map.get(交易明细.到期日) == NULL
        if(结转日应计利息余额 == null) {
            //查询交易的到期日对应的应计利息
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.应计利息余额 = 科目余额
                结转日应计利息余额 = listSubjectBalance.get(0).getBalance();
                /** modify by yanya on 20210325 增加利息本位币处理**/
                param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalance.get(0).getbAmt());
            }
            else {
                结转日应计利息余额 = BigDecimal.ZERO;
                /** modify by yanya on 20210325 增加利息本位币处理**/
                param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzryjlxyeMap().put(DateUtil.format(dealNow.getMdate()), 结转日应计利息余额);
        }
        //IF 变量.结转日成本科目余额Map.get(交易明细.到期日） IS NULL
        if(结转日成本科目余额 == null) {
            searchDao.setBaseSubjectNatureList(
                new ArrayList<String>(Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.结转日成本科目余额 = 科目余额
                结转日成本科目余额 = listSubjectBalance.get(0).getBalance();
                // 定义 变量.结转日成本科目数量 = 数量
                结转日成本科目数量 = listSubjectBalance.get(0).getNum();
                /** modify by yanya on 20210325 增加科目成本本位币处理**/
                param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalance.get(0).getbAmt());
            }
            else {
                结转日成本科目余额 = BigDecimal.ZERO;
                结转日成本科目数量 = BigDecimal.ZERO;
                /** modify by yanya on 20210325 增加科目成本本位币处理**/
                param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzrcbkmyeMap().put(DateUtil.format(dealNow.getMdate()), 结转日成本科目余额);
            param.getJzrcbkmslMap().put(DateUtil.format(dealNow.getMdate()), 结转日成本科目数量);
        }
        //BigDecimal varGyjzbdye = BigDecimal.ZERO;
        if(结转日溢折价科目余额 == null) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_利息调整.getValue())));
            List<SubjectBalance> listSubjectBalanceEx = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalanceEx != null && listSubjectBalanceEx.size() > 0) {
                // 变量.结转日公允价值变动科目余额 = 科目余额
                结转日溢折价科目余额 = listSubjectBalanceEx.get(0).getBalance();
                /** modify by yanya on 20210326 溢折价科目本位币处理**/
                param.getJzryzjkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalanceEx.get(0).getbAmt());
            }
            else {
                结转日溢折价科目余额 = BigDecimal.ZERO;
                /** modify by yanya on 20210326 溢折价科目本位币处理**/
                param.getJzryzjkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzryzjkmyeMap().put(DateUtil.format(dealNow.getMdate()), 结转日溢折价科目余额);
        }
        //BigDecimal varGyjzbdye = BigDecimal.ZERO;
        if(结转日公允价值变动科目余额 == null) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue())));
            List<SubjectBalance> listSubjectBalanceEx = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
            if (listSubjectBalanceEx != null && listSubjectBalanceEx.size() > 0) {
                // 变量.结转日公允价值变动科目余额 = 科目余额
                结转日公允价值变动科目余额 = listSubjectBalanceEx.get(0).getBalance();
                /** modify by yanya on 20210326 公允价值变动本位币处理**/
                param.getJzrgyjzbdMapBccy().put(DateUtil.format(dealNow.getMdate()), listSubjectBalanceEx.get(0).getbAmt());
            }
            else {
                结转日公允价值变动科目余额 = BigDecimal.ZERO;
                /** modify by yanya on 20210326 公允价值变动本位币处理**/
                param.getJzrgyjzbdMapBccy().put(DateUtil.format(dealNow.getMdate()), BigDecimal.ZERO);
            }
            //添加余额
            param.getJzrgyjzbdMap().put(DateUtil.format(dealNow.getMdate()), 结转日公允价值变动科目余额);
        }
        if (!DictCodeListExt1.估值交易市场_上交所.getValue().equals(rstBaseinfo.getTradeMarket())
            && !DictCodeListExt1.估值交易市场_深交所.getValue().equals(rstBaseinfo.getTradeMarket())) {
            // 买入场景
            // 调用账务共通设计->【凭证最新账务查询】
            List<BookLatestInfoDto> listBookLatestInfoDto = getBookLatestInfo(dealNow.getInvAim(),true, dealNow, acctEntity);
            // LOOP 变量.买入账务明细列表 -> 变量.账务明细
            if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                for (BookLatestInfoDto var : listBookLatestInfoDto) {
                    // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.结转日成本科目数量 -= 变量.账务明细.发生数量
                        结转日成本科目数量 = ArithmeticUtil.subtract(结转日成本科目数量, var.getHappenNumber());
                        // 变量.结转日成本科目余额 -= 变量.账务明细.原币发生额
                        结转日成本科目余额 = ArithmeticUtil.subtract(结转日成本科目余额, var.getoHappenAmt());
                    }
                    // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_利息调整.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日溢折价科目余额 -= 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.subtract(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日溢折价科目余额 += 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.doAdd(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                    }
                    // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日公允价值变动科目余额 -= 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.subtract(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日公允价值变动科目余额 += 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.doAdd(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                    }
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue().equals(var.getBaseSubjectNature())) {
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日应计利息余额 -= 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.subtract(结转日应计利息余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日应计利息余额 += 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.doAdd(结转日应计利息余额, var.getoHappenAmt());
                        }
                    }
                }
            }
            // 卖出场景
            // 调用账务共通设计->【凭证最新账务查询】
            listBookLatestInfoDto = getBookLatestInfo(dealNow.getInvAim(), false, dealNow, acctEntity);
            // LOOP 变量.卖出账务明细列表 -> 变量.账务明细
            if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                for (BookLatestInfoDto var : listBookLatestInfoDto) {
                    // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.结转日成本科目数量 += 变量.账务明细.发生数量
                        结转日成本科目数量 = ArithmeticUtil.doAdd(结转日成本科目数量, var.getHappenNumber());
                        // 变量.结转日成本科目余额 += 变量.账务明细.原币发生额
                        结转日成本科目余额 = ArithmeticUtil.doAdd(结转日成本科目余额, var.getoHappenAmt());
                    }
                    // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_利息调整.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日溢折价科目余额 -= 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.subtract(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日溢折价科目余额 += 变量.账务明细.原币发生额
                            结转日溢折价科目余额 = ArithmeticUtil.doAdd(结转日溢折价科目余额, var.getoHappenAmt());
                        }
                    }
                    // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue().equals(var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日公允价值变动科目余额 -= 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.subtract(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日公允价值变动科目余额 += 变量.账务明细.原币发生额
                            结转日公允价值变动科目余额 = ArithmeticUtil.doAdd(结转日公允价值变动科目余额, var.getoHappenAmt());
                        }
                    }
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue().equals(var.getBaseSubjectNature())) {
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.结转日应计利息余额 -= 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.subtract(结转日应计利息余额, var.getoHappenAmt());
                        }
                        else {
                            // 变量.结转日应计利息余额 += 变量.账务明细.原币发生额
                            结转日应计利息余额 = ArithmeticUtil.doAdd(结转日应计利息余额, var.getoHappenAmt());
                        }
                    }
                }
            }
        }
        //定义 		变量.冲减溢折价								=		变量.结转日溢折价科目余额
        BigDecimal 冲减溢折价 = 结转日溢折价科目余额;
        //定义 		变量.冲减公允价值变动			= 变量.结转日公允价值变动科目余额
        BigDecimal 冲减公允价值变动 = 结转日公允价值变动科目余额;
        //定义 		变量.冲减应计利息				= 变量.结转日应计利息科目余额
        BigDecimal 冲减应计利息 = 结转日应计利息余额;
        //定义 		变量.冲减数量								=		变量.结转日成本科目数量
        BigDecimal 冲减数量 = 结转日成本科目数量;
        //定义 		变量.冲减成本								=		变量.结转日成本科目余额
        BigDecimal 冲减成本 = 结转日成本科目余额;
        //定义 变量.交易金额_清算款	=		变量.溢折价科目余额 + 变量.结转日应计利息科目余额 + 变量.结转日成本科目余额
        BigDecimal 交易金额清算款 = ArithmeticUtil.doAdd(param.getVarYzjSubjectAmt(), param.getVarLxSubjectAmt(), param.getVarCostSubjectAmt());
        //变量.买卖价差	=		变量.交易金额_清算款 - 变量.冲减成本 - 变量.冲减溢折价 - 变量.冲减应计利息
        BigDecimal 买卖价差 =  ArithmeticUtil.doSub(交易金额清算款, 冲减成本, 冲减溢折价, 冲减应计利息);

        // 实收金额
        accountAmountMap.put(BookConstant.DEALAMT, ArithmeticUtil.round(交易金额清算款, NumberEnum.MONEY));
        //成本金额 = 变量.冲减成本
        accountAmountMap.put(BookConstant.FACEVALUE, ArithmeticUtil.round(冲减成本, NumberEnum.MONEY));
        //应计利息 = 冲减应计利息
        accountAmountMap.put(BookConstant.INTAMT, ArithmeticUtil.round(冲减应计利息, NumberEnum.MONEY));
        //if(变量.冲减溢折价 >= 0 )
        if(冲减溢折价.compareTo(BigDecimal.ZERO) >= 0) {
            // 剩余溢折价(贷) <- 变量.冲减溢折价
            accountAmountMap.put(BookConstant.DSCINC_C,
                    ArithmeticUtil.round(冲减溢折价, NumberEnum.MONEY));
        }
        else {
            // 剩余溢折价(借) <- 变量.冲减溢折价 * -1
            accountAmountMap.put(BookConstant.DSCINC_D, ArithmeticUtil.round(
                    ArithmeticUtil.multiply(冲减溢折价, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        //if(变量.冲减公允价值变动 >= 0 )
        if(冲减公允价值变动.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.公允价值变动(贷) < -变量.冲减公允价值变动
            accountAmountMap.put(BookConstant.FLOAT_C, ArithmeticUtil.round(冲减公允价值变动, NumberEnum.MONEY));
            //金额元素.冲减公允价值变动(借) < -变量.冲减公允价值变动
            accountAmountMap.put(BookConstant.FLOAT_1_D, ArithmeticUtil.round(冲减公允价值变动, NumberEnum.MONEY));
        }
        else {
            //金额元素.公允价值变动(借) < -变量.冲减公允价值变动 * -1
            accountAmountMap.put(BookConstant.FLOAT_D, ArithmeticUtil.round(
                ArithmeticUtil.multiply(冲减公允价值变动, new BigDecimal(-1)), NumberEnum.MONEY));
            //金额元素.冲减公允价值变动(贷) < -变量.冲减公允价值变动 * -1
            accountAmountMap.put(BookConstant.FLOAT_1_C, ArithmeticUtil.round(
                ArithmeticUtil.multiply(冲减公允价值变动, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        //if（变量.买卖价差>= 0）
        if(买卖价差.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.买卖价差（贷）								<-变量.买卖价差
            accountAmountMap.put(BookConstant.DSCLOSS_C, ArithmeticUtil.round(买卖价差, NumberEnum.MONEY));
        }
        else {
            //金额元素.买卖价差（借）								<-变量.买卖价差 * -1
            accountAmountMap.put(BookConstant.DSCLOSS_D, ArithmeticUtil.round(
                ArithmeticUtil.multiply(买卖价差, new BigDecimal(-1)), NumberEnum.MONEY));
        }
        Map<String, BigDecimal> accountAmountBCCYAMTMap = new HashMap<>();
        //本位币金额元素赋值 循环金额元素
        for (String key : accountAmountMap.keySet()) {
            accountCurrencyMap.put(key, dealNow.getCcy());// value值塞入当前交易币种
            accountAmountBCCYAMTMap.put(key + BookConstant.BCCYAMT,
                ArithmeticUtil.round(calculationCommonService.doMultiply(accountAmountMap.get(key), changeRate),NumberEnum.MONEY));
        }
        /** 本位币 币种**/
        accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
        //本位币金额元素都放到金额元素map中
        accountAmountMap.putAll(accountAmountBCCYAMTMap);
        //定义 变量.买卖价差_本位币	=	金额元素.实收金额_本位币 - 金额元素.成本金额_本位币 - 金额元素.应计利息_本位币

        BigDecimal 买卖价差_本位币 = ArithmeticUtil.doSub(
            ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT),
                        accountAmountMap.get(BookConstant.DSCINC_D + BookConstant.BCCYAMT)
            ),
            accountAmountMap.get(BookConstant.FACEVALUE + BookConstant.BCCYAMT),
            accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT),
            accountAmountMap.get(BookConstant.DSCINC_C + BookConstant.BCCYAMT)
        );
        //if（变量.买卖价差_本位币>= 0）
        if(买卖价差_本位币.compareTo(BigDecimal.ZERO) >= 0) {
            //金额元素.买卖价差（贷）_本位币								<-	变量.买卖价差_本位币
            accountAmountMap.put(BookConstant.DSCLOSS_C + BookConstant.BCCYAMT,
                ArithmeticUtil.round(买卖价差_本位币, NumberEnum.MONEY));
        }
        //else
        else {
            //金额元素.买卖价差（借）_本位币								<-	变量.买卖价差_本位币*-1
            accountAmountMap.put(BookConstant.DSCLOSS_D, ArithmeticUtil.round(
                ArithmeticUtil.multiply(买卖价差_本位币, new BigDecimal(-1)), NumberEnum.MONEY));
        }

        // modify by yanya on 20210326 特殊处理赎回结转数量
        accountSceneMap.put(BookConstant.HAPPEN_NUMBER, ArithmeticUtil.round(冲减数量, NumberEnum.QTY));
        /** modify by yanya on 20210325 科目余额计算成本、利息使用变量计算：避免同一批处理处理金额错误 start**/
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE,
            param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE_NUMBER,
            param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate())));//余额数量_本次发生前
        accountAmountMap.put(BookConstant.FACEVALUE+BookConstant.BALANCE+BookConstant.BCCYAMT,
            param.getJzrcbkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.FACEVALUE+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE,
            param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
        accountAmountMap.put(BookConstant.INTAMT+BookConstant.BALANCE+BookConstant.BCCYAMT,
            param.getJzryjlxyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
        accountSceneMap.put(BookConstant.INTAMT+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        /** modify by yanya on 20210325 科目余额计算成本、利息使用变量计算：避免同一批处理处理金额错误 end**/
        /** modify by yanya on 20210326 溢折价使用变量计算：避免同一批处理处理金额错误 start**/
        if (冲减溢折价.compareTo(BigDecimal.ZERO) >= 0) {
            accountAmountMap.put(BookConstant.DSCINC_C+BookConstant.BALANCE,
                    param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_C+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_C+BookConstant.BALANCE+BookConstant.BCCYAMT,
                    param.getJzryzjkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.DSCINC_C+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        else {
            accountAmountMap.put(BookConstant.DSCINC_D+BookConstant.BALANCE,
                    param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_D+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.DSCINC_D+BookConstant.BALANCE+BookConstant.BCCYAMT,
                    param.getJzryzjkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.DSCINC_D+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        /** modify by yanya on 20210326 溢折价使用变量计算：避免同一批处理处理金额错误 end**/
        /** modify by yanya on 20210326 公允价值变动使用变量计算：避免同一批处理处理金额错误 start**/
        if (冲减公允价值变动.compareTo(BigDecimal.ZERO) >= 0) {
            accountAmountMap.put(BookConstant.FLOAT_C+BookConstant.BALANCE,
                param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_C+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_C+BookConstant.BALANCE+BookConstant.BCCYAMT,
                param.getJzrgyjzbdMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.FLOAT_C+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        else {
            accountAmountMap.put(BookConstant.FLOAT_D+BookConstant.BALANCE,
                param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate())));//科目余额_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_D+BookConstant.BALANCE_NUMBER, BigDecimal.ZERO);//余额数量_本次发生前
            accountAmountMap.put(BookConstant.FLOAT_D+BookConstant.BALANCE+BookConstant.BCCYAMT,
                param.getJzrgyjzbdMapBccy().get(DateUtil.format(dealNow.getMdate())));//科目本位币余额_本次发生前
            accountSceneMap.put(BookConstant.FLOAT_D+BookConstant.BAL_FLAG, DictCodeList.余额方向_借方.getValue());//余额方向_本次发生前
        }
        /** modify by yanya on 20210326 公允价值变动使用变量计算：避免同一批处理处理金额错误 end**/

        // map塞入场景要素管理中
        // 记账场景要素
        ad.setAccountSceneMap(accountSceneMap);
        // 记账金额要素
        ad.setAccountAmountMap(accountAmountMap);
        // 记账金额币种要素
        ad.setAccountCurrencyMap(accountCurrencyMap);
        // 基础科目性质
        Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
        Map<String, Object> accountNatrueBranchMap = new HashMap<>();
        accountNatrueBranchMap.put(BookConstant.BRANCH, dealNow.getBranch());
        accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(), accountNatrueBranchMap);
        ad.setAccountNatrueMap(accountNatrueMap);
        //add by weipeng on 20220325：增加辅助核算项配置
        ad.setSubjectMappingConfigDefault(dealNow.getSubjectMappingConfig());
        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        //全局变量的更新
        //变量.成本科目余额 								=	变量.成本科目余额 - 变量.冲减成本
        // 变量.成本科目余额 = 变量.成本科目余额 - 变量.冲减成本
        param.setVarCostSubjectAmt(ArithmeticUtil.subtract(param.getVarCostSubjectAmt(), 冲减成本));
        // 变量.成本科目数量 = 变量.成本科目数量 - 变量.冲减数量
        param.setVarCostSubjectNum(ArithmeticUtil.subtract(param.getVarCostSubjectNum(), 冲减数量));
        // 变量.溢折价科目余额 = 变量.溢折价科目余额 - 变量.冲减溢折价
        param.setVarYzjSubjectAmt(ArithmeticUtil.subtract(param.getVarYzjSubjectAmt(), 冲减溢折价));
        // 变量.公允价值变动科目余额 = 变量.公允价值变动科目余额 - 变量.冲减公允价值变动
        param.setVarGySubjectAmt(ArithmeticUtil.subtract(param.getVarGySubjectAmt(), 冲减公允价值变动));
        //变量.结转日应计利息余额Map.put(交易明细.到期日,变量.结转日应计利息余额 - 变量.冲减应计利息)
        param.getJzryjlxyeMap().put(DateUtil.format(dealNow.getMdate()),
            ArithmeticUtil.doSub(param.getJzryjlxyeMap().get(DateUtil.format(dealNow.getMdate())), 冲减应计利息));
        //变量.结转日成本科目余额Map.put(交易明细.到期日,变量.结转日成本科目余额 -变量.冲减成本)
        param.getJzrcbkmyeMap().put(DateUtil.format(dealNow.getMdate()),
            ArithmeticUtil.doSub(param.getJzrcbkmyeMap().get(DateUtil.format(dealNow.getMdate())), 冲减成本));
        //变量.结转日成本科目数量Map.put(交易明细.到期日,变量.结转日成本科目数量 - 变量.冲减数量)
        param.getJzrcbkmslMap().put(DateUtil.format(dealNow.getMdate()),
            ArithmeticUtil.doSub(param.getJzrcbkmslMap().get(DateUtil.format(dealNow.getMdate())), 冲减数量));
        //变量.结转日溢折价科目余额.put(交易明细.到期日,变量.结转日溢折价科目余额 - 变量.冲减溢折价)
        param.getJzryzjkmyeMap().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzryzjkmyeMap().get(DateUtil.format(dealNow.getMdate())), 冲减溢折价));
        //变量.结转日公允价值变动科目余额Map.put(交易明细.到期日,变量.结转日公允价值变动科目余额 - 变量.冲减公允价值变动)
        param.getJzrgyjzbdMap().put(DateUtil.format(dealNow.getMdate()),
            ArithmeticUtil.doSub(param.getJzrgyjzbdMap().get(DateUtil.format(dealNow.getMdate())), 冲减公允价值变动));

        /** modify by yanya on 20210325 增加科目成本、利息本位币处理**/
        param.getJzryjlxyeMapBccy().put(DateUtil.format(dealNow.getMdate()),
            ArithmeticUtil.doSub(param.getJzryjlxyeMapBccy().get(DateUtil.format(dealNow.getMdate())),
                accountAmountMap.get(BookConstant.INTAMT+BookConstant.BCCYAMT)));
        param.getJzrcbkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()),
            ArithmeticUtil.doSub(param.getJzrcbkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())),
                accountAmountMap.get(BookConstant.FACEVALUE+BookConstant.BCCYAMT)));
        /** modify by yanya on 20210326 增加溢折价本位币处理**/
        param.getJzryzjkmyeMapBccy().put(DateUtil.format(dealNow.getMdate()),
                ArithmeticUtil.doSub(param.getJzryzjkmyeMapBccy().get(DateUtil.format(dealNow.getMdate())),
                        ArithmeticUtil.round(calculationCommonService.doMultiply(冲减溢折价, changeRate),NumberEnum.MONEY)));
        /** modify by yanya on 20210326 增加公允价值变动本位币处理**/
        param.getJzrgyjzbdMapBccy().put(DateUtil.format(dealNow.getMdate()),
            ArithmeticUtil.doSub(param.getJzrgyjzbdMapBccy().get(DateUtil.format(dealNow.getMdate())),
                ArithmeticUtil.round(calculationCommonService.doMultiply(冲减公允价值变动, changeRate),NumberEnum.MONEY)));

    }

}
