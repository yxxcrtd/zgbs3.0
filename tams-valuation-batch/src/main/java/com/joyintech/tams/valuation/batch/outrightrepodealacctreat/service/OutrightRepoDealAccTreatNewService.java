/*
 * 文件名：DepositeAssetTransBookService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V2.0
 * 修改人：weipeng
 * 修改时间：2018年5月23日
 * 修改内容：新建
 * 系统名称：估值核算系统V2.0
 */

package com.joyintech.tams.valuation.batch.outrightrepodealacctreat.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.BokStatLossPosiiton;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListTableId;
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
import com.joyintech.tams.valuation.common.constants.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 买断式回购交易账务处理 process相关业务处理
 * 
 * @author chufengjie
 * @version 1.0 2018年7月26日
 * @see OutrightRepoDealAccTreatNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class OutrightRepoDealAccTreatNewService {


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

    /**
     * 标的共通方法类
     */
    @Autowired
    private FinProductCommonService finProductCommonService;
    
    /**
     * 计算类共通方法service
     */
    @Autowired
    private CalculationCommonService calculationCommonService;

    /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-买断式逆回购减值场景增加 start**/
    @Autowired
    private LossCommonService lossCommonService;

    @Autowired
    private LossCommonBatchService lossCommonBatchService;
    /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-买断式逆回购减值场景增加 end**/

    /**
     * process 数据处理
     * 
     * @param param1 参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public BookGenerateResult execute(Map<String, Object> param1) {
        //获取reader返回值
        FinProductDealInfoResult acctEntity = (FinProductDealInfoResult)param1.get(GlobalConstant.READER_RESULT);
        
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
        Long taskPri = listFinProductDealInfoComDTONow.get(0).getTaskPri();//任务优先级（信息列表的任务优先级都是一致的，故取第一个）
        //金融产品代码
        String finprodIdNow = listFinProductDealInfoComDTONow.get(0).getFinprodId();
        String finprodTypeNow = listFinProductDealInfoComDTONow.get(0).getFinprodType();//金融产品类型
        
        // 1. 数据准备
        // 1.1 定义变量
        // 定义 变量.成本科目余额
        BigDecimal costBalance = BigDecimal.ZERO;
        // 定义 变量.成本科目数量
        BigDecimal costQuantity = BigDecimal.ZERO;
        // 定义 变量.应计利息科目余额
        BigDecimal interestBalance = BigDecimal.ZERO;
        

        // 2.调用共通查询发生前一科目余额查询 - 期待方向）,获取成本类科目余额
        // 交易期待方向——借方（默认）
        String expBalanceDirect = DictCodeList.余额方向_借方.getValue();
        // 若为正回购，则设置为贷方
        if(finprodTypeNow.equals(DictCodeListExt1.金融产品类型_债券买断式正回购.getValue())){
            expBalanceDirect = DictCodeList.余额方向_贷方.getValue();
        }

        /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-买断式逆回购减值场景增加 start**/
        //定义  变量.统计分析明细表（资产减值）减值使用列表
        if (returnResult.getBokStatLossPosiitonList() == null) {
            returnResult.setBokStatLossPosiitonList(new ArrayList<BokStatLossPosiiton>());
        }
        //2.5.减值准备类科目余额
        BigDecimal glb_subjectjz = BigDecimal.ZERO;//减值准备类科目余额
        //调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
        SubjectBalance jzBalance = this.getSubjectBalance(booksetIdNow, bokDetailId, happenDateNow, bookDateNow,
                DictCodeList.余额方向_借方.getValue(), DictCodeBaseSubjectNatrueNew.买入返售金融资产_减值准备.getValue(), null, taskPri);
        if (jzBalance != null) {
            // 变量.减值准备类科目余额 = DTO.科目余额
            glb_subjectjz = jzBalance.getBalance() == null ? BigDecimal.ZERO : jzBalance.getBalance();
        }
        //定义 汇率 = 1
        BigDecimal 汇率 = BigDecimal.ONE;
        /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-买断式逆回购减值场景增加 end**/
        
        // 2.3 买断式回购费用处理方式 
        List<String> paramCodeList = new ArrayList<>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_交易所买断式回购费用处理方式.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_银行间买断式回购费用处理方式.getValue());
        //调用共通设计->【详细设计-账务类共通方法设计.xlsx】->账套参数查询                                                                     
        Map<String,Object> booksetMap = accountingCommonService.getValBokBooksetExtForMap(booksetIdNow, paramCodeList);
        
        //定义 【变量.交易所费用处理方式】
        String dealFeetype = booksetMap.get(ValDictCodeList.账套配置表参数项_交易所买断式回购费用处理方式.getValue()).toString();
        //定义 【变量.银行间费用处理方式】
        String bankFeeType = booksetMap.get(ValDictCodeList.账套配置表参数项_银行间买断式回购费用处理方式.getValue()).toString();
        
        //质押品信息查询
        List<PledgeInfoDto> pledgeInfoDtoList = finProductCommonService.getPledgeInfo(finprodIdNow, finprodTypeNow);
        //定义 变量.登记托管机构
        String registOrg = null;
        if (pledgeInfoDtoList != null && pledgeInfoDtoList.size() > 0) {
            registOrg = pledgeInfoDtoList.get(0).getRegistOrg();
        }
        
        // 3. 交易处理
        // 循环 交易类账务处理结果.交易明细列表,进行逻辑处理
        for (FinProductDealInfoComDTO dealNow : listFinProductDealInfoComDTONow) {
           // if(dealNow.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券买断式正回购.getValue())){
            // 2.1 获取成本类科目余额 -【 调用共通方法：科目余额查询,获取成本类科目余额 】
            // 基础科目性质 : 成本
            String baseSubjectNatureCB = DictCodeBaseSubjectNatrueNew.买入返售金融资产_成本.getValue();
            String baseSubjectNatureYjlx = DictCodeBaseSubjectNatrueNew.买入返售金融资产_应计利息.getValue();
            if(dealNow.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券买断式正回购.getValue())){
                baseSubjectNatureCB = DictCodeBaseSubjectNatrueNew.卖出回购金融资产_成本.getValue();
                baseSubjectNatureYjlx = DictCodeBaseSubjectNatrueNew.卖出回购金融资产_应计利息.getValue();
            }
            SubjectBalance subjectBalance = this.getSubjectBalance(booksetIdNow, bokDetailId,
                happenDateNow, bookDateNow, expBalanceDirect, baseSubjectNatureCB,
                null,taskPri);
            if(subjectBalance!=null){
                // 成本类科目余额赋值
                costBalance = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
                // 成本类科目数量赋值
                costQuantity = subjectBalance.getNum() == null ? BigDecimal.ZERO : subjectBalance.getNum();
            }

            // 2.2 获取应收利息类科目余额 -【 调用共通方法：科目余额查询,获取成本类科目余额 】
            // 基础科目性质 : 应计利息
            subjectBalance = this.getSubjectBalance(booksetIdNow, bokDetailId, happenDateNow,
                bookDateNow, expBalanceDirect, baseSubjectNatureYjlx, null,taskPri);
            if (subjectBalance != null) {
                // 变量.应计利息余额 = DTO.科目余额
                interestBalance = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
            }

            // 调用共通设计 ：标的类共通-交易信息查询
            TradeInfoDto tradeInfoDto = this.getTargetInfo(dealNow.getFinprodId(),dealNow.getFinprodType(),dealNow.getBranch());
            
            // 保证获取的标的交易信息不为空
            if (tradeInfoDto == null) {
                tradeInfoDto = new TradeInfoDto();
            }
            
            AccountDriver ad = new AccountDriver();// 凭证类型、账套代码、交易编号全部去掉了。
            ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
            //ad.setTableId(this.getTableId(dealNow.getPs(), dealNow.getFinprodType()));// ad.会计场景代码 = 根据交易方向和金融产品类型区分
            
            // 初始化定义 - 存放该笔记账的场景要素 map定义
            Map<String, Object> accountSceneMap = new HashMap<>();
            // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值)
            accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
            accountSceneMap.put("TRADE_MARKET", dealNow.getTradeMarket());

            // 金融产品类型+交易方向+"交易登账"
            String finprodTypeName = DictCodeListExt1.from(DictCode.金融产品类型, dealNow.getFinprodType()).getCodeName();
            /** modify by yanya on 20191122 交易方向从缓存中取数据字典**/
            //String psName = DictCodeList.from(DictCode.交易方向, dealNow.getPs()).getCodeName();
            String psName = SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealNow.getPs());
            accountSceneMap.put(BookConstant.BOOK_REMARK, finprodTypeName + psName + "交易登账");

            // 分类1 <- 变量.交易信息.分类1
            accountSceneMap.put(BookConstant.TYPE_1, tradeInfoDto.getType1());
            // 分类2 <- 变量.交易信息.分类2
            accountSceneMap.put(BookConstant.TYPE_2, tradeInfoDto.getType2());
            //登记托管机构                    <-      变量.登记托管机构
            accountSceneMap.put(BookConstant.REGIST_ORG, registOrg);
                        
            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountMap = new HashMap<>();                   
            //计算类共通【交易各类费用处理】
            DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(dealNow.getValTradeFeeDetailList(),dealNow.getProdId(),dealNow.getPortfolioId());
            //变量.交易各种费用 = 上述返回值  
            accountAmountMap.put(BookConstant.STAMPTAX, dealTypesFeeDto.getStampDuty());//变量.交易各种费用.印花税
            accountAmountMap.put(BookConstant.HANDFEE, dealTypesFeeDto.getBrokerage());//变量.交易各种费用.经手费
            accountAmountMap.put(BookConstant.CERTIFEE, dealTypesFeeDto.getCertificateFee());//变量.交易各种费用.证管费
            accountAmountMap.put(BookConstant.SETTLEFEE, dealTypesFeeDto.getClearingFee());//变量.交易各种费用.结算费
            accountAmountMap.put(BookConstant.COMMISSION, dealTypesFeeDto.getCommission());//变量.交易各种费用.佣金
            accountAmountMap.put(BookConstant.TRADEFEE, dealTypesFeeDto.getTransactionFee());//变量.交易各种费用.交易手续费
            accountAmountMap.put(BookConstant.SETTLEMENTFEE, dealTypesFeeDto.getSettlementFee());//变量.交易各种费用.结算手续费
            
            // 场景元素设值各种费用的账务明细代码
            // 印花税账务明细代码 <- 变量.交易各种费用.印花税账务明细代码
            if(dealTypesFeeDto.getStampDutyCode()!=null)accountSceneMap.put(BookConstant.BOK_DETAIL_STAMPTAX, dealTypesFeeDto.getStampDutyCode());
            // 经手费账务明细代码 <- 变量.交易各种费用.经手费账务明细代码
            if(dealTypesFeeDto.getBrokerageCode()!=null)accountSceneMap.put(BookConstant.BOK_DETAIL_HANDFEE, dealTypesFeeDto.getBrokerageCode());
            // 证管费账务明细代码 <- 变量.交易各种费用.证管费账务明细代码
            if(dealTypesFeeDto.getCertificateFeeCode()!=null)accountSceneMap.put(BookConstant.BOK_DETAIL_CERTIFEE,dealTypesFeeDto.getCertificateFeeCode());
            // 结算费账务明细代码 <- 变量.交易各种费用.结算费账务明细代码
            if(dealTypesFeeDto.getClearingFeeCode()!=null)accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEFEE,dealTypesFeeDto.getClearingFeeCode());
            // 佣金账务明细代码 <- 变量.交易各种费用.佣金账务明细代码
            if(dealTypesFeeDto.getCommissionCode()!=null)accountSceneMap.put(BookConstant.BOK_DETAIL_COMMISSION,dealTypesFeeDto.getCommissionCode());
            // 交易手续费账务明细代码 <- 变量.交易各种费用.交易手续费账务明细代码
            if(dealTypesFeeDto.getTransactionFeeCode()!=null)accountSceneMap.put(BookConstant.BOK_DETAIL_TRADEFEE,dealTypesFeeDto.getTransactionFeeCode());
            // 结算手续费账务明细代码 <- 变量.交易各种费用.结算手续费账务明细代码
            if(dealTypesFeeDto.getSettlementFeeCode()!=null)accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEMENTFEE,dealTypesFeeDto.getSettlementFeeCode());
           
            
         // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
            Map<String, Object> accountCurrencyMap = new HashMap<>();
            
            BigDecimal repo = BigDecimal.ZERO;
            //交易明细.金融产品类型 = “债券买断式逆回购”
            BigDecimal feeAmt = dealTypesFeeDto.getFeeAmt()!=null?dealTypesFeeDto.getFeeAmt():BigDecimal.ZERO;
            if(dealNow.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue())){
                repo = ArithmeticUtil.doAdd(dealNow.getPrinAmt(),feeAmt);//交易明细.交易本金 + 变量.交易各种费用.交易费用
            }else{
                repo = ArithmeticUtil.doSub(dealNow.getPrinAmt(),feeAmt);//交易明细.交易本金 - 变量.交易各种费用.交易费用                                                  
            }

            /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-买断式逆回购减值场景增加 start**/
            //定义 变量.冲减减值金额 = 0
            BigDecimal 冲减减值金额 = BigDecimal.ZERO;
            /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-买断式逆回购减值场景增加 end**/
            // if（交易方向 = "买入"）
            if (DictCodeList.交易方向_买入.getValue().equals(dealNow.getPs())) {

                //场景代码
                // 分录场景为【债券质押式逆回购】
                if (DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue().equals(dealNow.getFinprodType())) {
                    // ad.会计场景代码="质押式逆回购-起息"
                    ad.setTableId(DictCodeListTableId.会计场景代码_买断式逆回购_起息.getValue());
                    
                } 
                // 分录场景为【债券买断式正回购】
                else {
                    // ad.会计场景代码="质押式正回购-起息"
                    ad.setTableId(DictCodeListTableId.会计场景代码_买断式正回购_起息.getValue());
                }
                
                
                //定义【变量.费用处理方式】
                String feeTypeNow = "";
                //根据 交易明细.交易场所IN （“上海证券交易所”，“深圳证券交易所”） 获取费用处理方式
                if(DictCodeList.交易流通场所_上海证券交易所.getValue().equals(dealNow.getTradeMarket()) || DictCodeList.交易流通场所_深圳证券交易所.getValue().equals(dealNow.getTradeMarket())){
                    //变量.费用处理方式 = 变量.交易所费用处理方式
                    feeTypeNow = dealFeetype;
                }
                else{
                    //变量.费用处理方式 = 变量.银行间费用处理方式
                    feeTypeNow = bankFeeType;
                }
                
                //if（变量.费用处理方式 = "不计入成本"）
                if(feeTypeNow.equals(DictCodeList.回购费用处理方式_不计入成本.getValue())){
                    accountAmountMap.put(BookConstant.COST, dealNow.getPrinAmt());//交易明细.交易本金
                    //accountAmountMap.put(BookConstant.FEEAMT, dealTypesFeeDto.getFeeAmt());//变量.交易各种费用.交易费用
                    
                    // 印花税支出 = 变量.交易各种费用.印花税+变量.交易各种费用.随交易支付_印花税
                    accountAmountMap.put(BookConstant.STAMPTAX_OUT,  ArithmeticUtil.doAdd(dealTypesFeeDto.getStampDuty(),dealTypesFeeDto.getStampDutyForDealPay()));
                    // 经手费支出 = 变量.交易各种费用.经手费+变量.交易各类费用.随交易支付_经手费
                    accountAmountMap.put(BookConstant.HANDFEE_OUT, ArithmeticUtil.doAdd(dealTypesFeeDto.getBrokerage(),dealTypesFeeDto.getBrokerageForDealPay()));
                    // 证管费支出 = 变量.交易各种费用.证管费+变量.交易各种费用.随交易支付_证管费                    
                    accountAmountMap.put(BookConstant.CERTIFEE_OUT, ArithmeticUtil.doAdd(dealTypesFeeDto.getCertificateFee(),dealTypesFeeDto.getCertificateFeeForDealPay()));
                    // 结算费支出 = 变量.交易各种费用.结算费+变量.交易各种费用.随交易支付_结算费            
                    accountAmountMap.put(BookConstant.SETTLEFEE_OUT, ArithmeticUtil.doAdd(dealTypesFeeDto.getClearingFee(),dealTypesFeeDto.getClearingFeeForDealPay()));
                    // 佣金支出 = 变量.交易各种费用.佣金+变量.交易各种费用.随交易支付_佣金       
                    accountAmountMap.put(BookConstant.COMMISSION_OUT, ArithmeticUtil.doAdd(dealTypesFeeDto.getCommission(),dealTypesFeeDto.getCommissionForDealPay()));
                    // 交易手续费支出 = 变量.交易各种费用.交易手续费+变量.交易各种费用.随交易支付_交易手续费                                 
                    accountAmountMap.put(BookConstant.TRADEFEE_OUT, ArithmeticUtil.doAdd(dealTypesFeeDto.getTransactionFee(),dealTypesFeeDto.getTransactionFeeForDealPay()));
                    // 结算手续费支出 = 变量.交易各种费用.结算手续费+变量.交易各种费用.随交易支付_结算手续费 
                    accountAmountMap.put(BookConstant.SETTLEMENTFEE_OUT, ArithmeticUtil.doAdd(dealTypesFeeDto.getSettlementFee(),dealTypesFeeDto.getSettlementFeeForDealPay()));

                    // 场景元素设值各种费用的账务明细代码
                    // 印花税账务明细代码 <- 变量.交易各种费用.印花税账务明细代码
                    if(dealTypesFeeDto.getStampDutyCodeForDealPay()!=null)accountSceneMap.put(BookConstant.BOK_DETAIL_STAMPTAX, dealTypesFeeDto.getStampDutyCodeForDealPay());
                    // 经手费账务明细代码 <- 变量.交易各种费用.经手费账务明细代码
                    if(dealTypesFeeDto.getBrokerageCodeForDealPay()!=null)accountSceneMap.put(BookConstant.BOK_DETAIL_HANDFEE, dealTypesFeeDto.getBrokerageCodeForDealPay());
                    // 证管费账务明细代码 <- 变量.交易各种费用.证管费账务明细代码
                    if(dealTypesFeeDto.getCertificateFeeCodeForDealPay()!=null)accountSceneMap.put(BookConstant.BOK_DETAIL_CERTIFEE,dealTypesFeeDto.getCertificateFeeCodeForDealPay());
                    // 结算费账务明细代码 <- 变量.交易各种费用.结算费账务明细代码
                    if(dealTypesFeeDto.getClearingFeeCodeForDealPay()!=null)accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEFEE,dealTypesFeeDto.getClearingFeeCodeForDealPay());
                    // 佣金账务明细代码 <- 变量.交易各种费用.佣金账务明细代码
                    if(dealTypesFeeDto.getCommissionCodeForDealPay()!=null)accountSceneMap.put(BookConstant.BOK_DETAIL_COMMISSION,dealTypesFeeDto.getCommissionCodeForDealPay());
                    // 交易手续费账务明细代码 <- 变量.交易各种费用.交易手续费账务明细代码
                    if(dealTypesFeeDto.getTransactionFeeCodeForDealPay()!=null)accountSceneMap.put(BookConstant.BOK_DETAIL_TRADEFEE,dealTypesFeeDto.getTransactionFeeCodeForDealPay());
                    // 结算手续费账务明细代码 <- 变量.交易各种费用.结算手续费账务明细代码
                    if(dealTypesFeeDto.getSettlementFeeCodeForDealPay()!=null)accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEMENTFEE,dealTypesFeeDto.getSettlementFeeCodeForDealPay());
                    
                }else{
                    accountAmountMap.put(BookConstant.COST, repo);//逆回购:交易明细.交易本金 + 变量.交易各种费用.交易费用,正回购:交易明细.交易本金 - 变量.交易各种费用.交易费用
                    accountAmountMap.put(BookConstant.FEEAMT, BigDecimal.ZERO);//0
                }
                accountAmountMap.put(BookConstant.DEALAMT, dealNow.getTradeAmt());//交易明细.交易金额
                accountAmountMap.put(BookConstant.INTAMT, dealNow.getIntAmt());//交易明细.利息总额
                
                costBalance = costBalance.add(dealNow.getPrinAmt());// 变量.成本科目余额 + 交易明细.交易本金
                costQuantity = costQuantity.add(dealNow.getShareAmt());//变量.成本科目数量 + 交易明细.交易份额
                interestBalance = interestBalance.add(dealNow.getIntAmt());// 变量.应计利息科目余额 + 交易明细.利息总额
            }
            //交易方向 = 债券派息
//            else if (DictCodeList.交易方向_债券派息.getValue().equals(dealNow.getPs())
//            		&&dealNow.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue())) {
//                
//                //场景代码
//                // 分录场景为【债券质押式逆回购】
//                if (DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue().equals(dealNow.getFinprodType())) {
//                    // ad.会计场景代码="买断式逆回购-期间债券还本派息"
//                    ad.setTableId(DictCodeListTableId.会计场景代码_买断式逆回购_期间债券还本派息.getValue());
//                    
//                } 
//                // 分录场景为【债券买断式正回购】
//                else {
//                }
//                
//                accountAmountMap.put("DEALAMT", dealNow.getTradeAmt());//交易明细.交易金额
//                accountAmountMap.put("DEALAMT2", dealNow.getTradeAmt());//交易明细.到期金额
//                
//                //全局变量更新
//                costBalance = costBalance.subtract(dealNow.getTradeAmt());// 变量.成本科目余额 - 金额元素.交易金额
//                costQuantity = costQuantity.subtract(dealNow.getTradeAmt());//变量.成本科目数量 - 金额元素.交易金额               
//            }
            //交易方向 = 卖出 or 到期
            else if (DictCodeList.交易方向_卖出.getValue().equals(dealNow.getPs())
                ||DictCodeList.交易方向_到期.getValue().equals(dealNow.getPs())) {
                
                //场景代码
                // 分录场景为【债券质押式逆回购】
                if (DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue().equals(dealNow.getFinprodType())) {
                    // ad.会计场景代码="质押式逆回购-到期"
                    ad.setTableId(DictCodeListTableId.会计场景代码_买断式逆回购_到期.getValue());
                    
                } 
                // 分录场景为【债券买断式正回购】
                else {
                    // ad.会计场景代码="质押式正回购-到期"
                    ad.setTableId(DictCodeListTableId.会计场景代码_买断式正回购_到期.getValue());
                }
                
                //设置公用金额元素
                // 印花税支出 = 变量.交易各种费用.随交易支付_印花税
                accountAmountMap.put(BookConstant.STAMPTAX_OUT, dealTypesFeeDto.getStampDutyForDealPay());
                // 经手费支出 = 变量.交易各类费用.随交易支付_经手费
                accountAmountMap.put(BookConstant.HANDFEE_OUT, dealTypesFeeDto.getBrokerageForDealPay());
                // 证管费支出 = 变量.交易各种费用.随交易支付_证管费                    
                accountAmountMap.put(BookConstant.CERTIFEE_OUT, dealTypesFeeDto.getCertificateFeeForDealPay());
                // 结算费支出 = 变量.交易各种费用.随交易支付_结算费            
                accountAmountMap.put(BookConstant.SETTLEFEE_OUT, dealTypesFeeDto.getClearingFeeForDealPay());
                // 佣金支出 = 变量.交易各种费用.随交易支付_佣金       
                accountAmountMap.put(BookConstant.COMMISSION_OUT, dealTypesFeeDto.getCommissionForDealPay());
                // 交易手续费支出 = 变量.交易各种费用.随交易支付_交易手续费                                 
                accountAmountMap.put(BookConstant.TRADEFEE_OUT, dealTypesFeeDto.getTransactionFeeForDealPay());
                // 结算手续费支出 = 变量.交易各种费用.随交易支付_结算手续费 
                accountAmountMap.put(BookConstant.SETTLEMENTFEE_OUT, dealTypesFeeDto.getSettlementFeeForDealPay());
                
                // 场景元素设值各种费用的账务明细代码
                // 印花税账务明细代码 <- 变量.交易各种费用.印花税账务明细代码
                if(dealTypesFeeDto.getStampDutyCodeForDealPay()!=null)accountSceneMap.put(BookConstant.BOK_DETAIL_STAMPTAX, dealTypesFeeDto.getStampDutyCodeForDealPay());
                // 经手费账务明细代码 <- 变量.交易各种费用.经手费账务明细代码
                if(dealTypesFeeDto.getBrokerageCodeForDealPay()!=null)accountSceneMap.put(BookConstant.BOK_DETAIL_HANDFEE, dealTypesFeeDto.getBrokerageCodeForDealPay());
                // 证管费账务明细代码 <- 变量.交易各种费用.证管费账务明细代码
                if(dealTypesFeeDto.getCertificateFeeCodeForDealPay()!=null)accountSceneMap.put(BookConstant.BOK_DETAIL_CERTIFEE,dealTypesFeeDto.getCertificateFeeCodeForDealPay());
                // 结算费账务明细代码 <- 变量.交易各种费用.结算费账务明细代码
                if(dealTypesFeeDto.getClearingFeeCodeForDealPay()!=null)accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEFEE,dealTypesFeeDto.getClearingFeeCodeForDealPay());
                // 佣金账务明细代码 <- 变量.交易各种费用.佣金账务明细代码
                if(dealTypesFeeDto.getCommissionCodeForDealPay()!=null)accountSceneMap.put(BookConstant.BOK_DETAIL_COMMISSION,dealTypesFeeDto.getCommissionCodeForDealPay());
                // 交易手续费账务明细代码 <- 变量.交易各种费用.交易手续费账务明细代码
                if(dealTypesFeeDto.getTransactionFeeCodeForDealPay()!=null)accountSceneMap.put(BookConstant.BOK_DETAIL_TRADEFEE,dealTypesFeeDto.getTransactionFeeCodeForDealPay());
                // 结算手续费账务明细代码 <- 变量.交易各种费用.结算手续费账务明细代码
                if(dealTypesFeeDto.getSettlementFeeCodeForDealPay()!=null)accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEMENTFEE,dealTypesFeeDto.getSettlementFeeCodeForDealPay());

                
            	BigDecimal adjustAmt ;
            	// 定义 变量.冲减份额 = 0
            	BigDecimal cjShareAmt = BigDecimal.ZERO;
            	if(DictCodeList.交易方向_到期.getValue().equals(dealNow.getPs())){
            	    
            		accountAmountMap.put(BookConstant.COST, costBalance);//回购本金	<-	变量.成本科目余额	
                    accountAmountMap.put(BookConstant.INTAMT, interestBalance);//应计利息<-	变量.应计利息科目余额					
                    accountAmountMap.put(BookConstant.DEALAMT, dealNow.getTradeAmt());//到期/转出金额<-交易明细.交易金额
                    
                    if(dealNow.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue())){
                    ////变量.调整金额 = 金额元素.到期/转出金额 - 金额元素.回购本金 - 金额元素.应计利息 
                    	adjustAmt = dealNow.getTradeAmt().subtract(costBalance).subtract(interestBalance)
								.subtract(accountAmountMap.get(BookConstant.STAMPTAX)).add(accountAmountMap.get(BookConstant.STAMPTAX_OUT))// - 金额元素.印花税  + 金额元素.印花税支出						
								.subtract(accountAmountMap.get(BookConstant.HANDFEE)).add(accountAmountMap.get(BookConstant.HANDFEE_OUT))// - 金额元素.经手费  + 金额元素.经手费支出
								.subtract(accountAmountMap.get(BookConstant.CERTIFEE)).add(accountAmountMap.get(BookConstant.CERTIFEE_OUT))// - 金额元素.证管费 + 金额元素.证管费 支出   
								.subtract(accountAmountMap.get(BookConstant.SETTLEFEE)).add(accountAmountMap.get(BookConstant.SETTLEFEE_OUT))// - 金额元素.结算费 + 金额元素.结算费支出   
								.subtract(accountAmountMap.get(BookConstant.COMMISSION)).add(accountAmountMap.get(BookConstant.COMMISSION_OUT))// - 金额元素.佣金 + 金额元素.佣金支出    
								.subtract(accountAmountMap.get(BookConstant.TRADEFEE)).add(accountAmountMap.get(BookConstant.TRADEFEE_OUT))// - 金额元素.交易手续费 + 金额元素.交易手续费 支出   
								.subtract(accountAmountMap.get(BookConstant.SETTLEMENTFEE)).add(accountAmountMap.get(BookConstant.SETTLEMENTFEE_OUT));// - 金额元素.结算手续费 + 金额元素.结算手续费支出  
                    }else{
                    ////变量.调整金额 = 金额元素.到期/转出金额 - 金额元素.回购本金 - 金额元素.应计利息 
                    	adjustAmt = dealNow.getTradeAmt().subtract(costBalance).subtract(interestBalance)
								.add(accountAmountMap.get(BookConstant.STAMPTAX)).subtract(accountAmountMap.get(BookConstant.STAMPTAX_OUT))// + 金额元素.印花税 - 金额元素.印花税支出  					
								.add(accountAmountMap.get(BookConstant.HANDFEE)).subtract(accountAmountMap.get(BookConstant.HANDFEE_OUT))// + 金额元素.经手费 - 金额元素.经手费支出  
								.add(accountAmountMap.get(BookConstant.CERTIFEE)).subtract(accountAmountMap.get(BookConstant.CERTIFEE_OUT))// + 金额元素.证管费 - 金额元素.证管费支出 
								.add(accountAmountMap.get(BookConstant.SETTLEFEE)).subtract(accountAmountMap.get(BookConstant.SETTLEFEE_OUT))// + 金额元素.结算费 - 金额元素.结算费支出    
								.add(accountAmountMap.get(BookConstant.COMMISSION)).subtract(accountAmountMap.get(BookConstant.COMMISSION_OUT))// + 金额元素.佣金 - 金额元素.佣金支出 
								.add(accountAmountMap.get(BookConstant.TRADEFEE)).subtract(accountAmountMap.get(BookConstant.TRADEFEE_OUT))// + 金额元素.交易手续费 - 金额元素.交易手续费支出 
								.add(accountAmountMap.get(BookConstant.SETTLEMENTFEE)).subtract(accountAmountMap.get(BookConstant.SETTLEMENTFEE_OUT));// + 金额元素.结算手续费 - 金额元素.结算手续费支出   
                    }
                    // 变量.冲减份额  =  变量.成本科目数量
                    cjShareAmt = costQuantity;

            	}else{
            		/*//if（变量.费用处理方式 = "不计入成本"）
                    if(feeTypeNow.equals(DictCodeList.回购费用处理方式_不计入成本.getValue())){
                        accountAmountMap.put(BookConstant.COST, dealNow.getPrinAmt());//回购本金       交易明细.交易本金
                    }else{
                        //逆回购:交易明细.交易本金 + 变量.交易各种费用.交易费用,正回购:交易明细.交易本金 - 变量.交易各种费用.交易费用
                        accountAmountMap.put(BookConstant.COST, repo);
                    }*/
            	    
            	    accountAmountMap.put(BookConstant.COST, dealNow.getPrinAmt());//回购本金       交易明细.交易本金
                    accountAmountMap.put(BookConstant.DEALAMT, dealNow.getTradeAmt());//到期/转出金额<-交易明细.交易金额
                    //变量.应计利息科目余额 *  交易份额/变量.成本科目数量
                    BigDecimal intAmt =  ArithmeticUtil.divide(ArithmeticUtil.multiply(interestBalance,dealNow.getShareAmt()),costQuantity);
                    //按照金额小数位处理方式，处理小数位
                    intAmt = ArithmeticUtil.round(intAmt, NumberEnum.MONEY);
                    //塞值
                    accountAmountMap.put(BookConstant.INTAMT, intAmt);
                    //变量.调整金额 = 交易明细.应计利息总额 - 金额元素.应计利息                                                             
                    adjustAmt = dealNow.getIntAmt().subtract(intAmt);
                    // 变量.冲减份额  =  交易明细.交易份额
                    cjShareAmt =  dealNow.getShareAmt();
            	}
            	// 发生数量 <- 冲减份额
            	accountSceneMap.put(BookConstant.HAPPEN_NUMBER, cjShareAmt);
                if(adjustAmt.compareTo(BigDecimal.ZERO)>=0){//调整金额_贷 <-     变量.调整金额         
                	
                	if(dealNow.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue())){
                        accountAmountMap.put(BookConstant.INCOMEAMT_C, adjustAmt);
                    }else{
                        accountAmountMap.put(BookConstant.INCOMEAMT_D, adjustAmt);
                    }
                	
                }else{//调整金额_借  <-  变量.调整金额 * -1   
                	if(dealNow.getFinprodType().equals(DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue())){
                        accountAmountMap.put(BookConstant.INCOMEAMT_D, adjustAmt.multiply(new BigDecimal(-1)));
                    }else{
                        accountAmountMap.put(BookConstant.INCOMEAMT_C, adjustAmt.multiply(new BigDecimal(-1)));
                    }
                }

                /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-买断式逆回购减值场景增加 start**/
                //只有逆回购出减值
                if(StringUtil.equals(DictCodeListExt1.金融产品类型_债券买断式逆回购.getValue(), dealNow.getFinprodType())){
                    //IF 变量.减值准备科目余额 != 0
                    if(glb_subjectjz.compareTo(BigDecimal.ZERO) != 0) {
                        LossAmtInputDto inputdto = this.getLossAmtInputDto(acctEntity, dealNow,
                                null,
                                //本次发生额 = 金额元素.回购本金 + 金额元素.应计利息
                                ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.COST), accountAmountMap.get(BookConstant.INTAMT)),
                                //减值准备余额
                                glb_subjectjz,
                                //当前EAD金额 = 成本科目余额 + 应计利息科目余额
                                ArithmeticUtil.doAdd(costBalance, interestBalance),
                                //应收款项余额
                                null,
                                //总份额 = 变量.成本科目数量
                                costQuantity,
                                //发生份额 = 交易明细.交易份额
                                dealNow.getShareAmt()
                        );
                        冲减减值金额 = lossCommonBatchService.calcLossAmtForDeal(inputdto);
                    }
                    accountAmountMap.put(BookConstant.IMPAMT, 冲减减值金额.negate());
                }
                glb_subjectjz = glb_subjectjz.subtract(冲减减值金额);// 变量.减值准备科目余额- 变量.冲减减值准备
                /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-买断式逆回购减值场景增加 end**/
                
                costBalance = costBalance.subtract(dealNow.getPrinAmt());// 变量.成本科目余额 - 交易明细.交易本金
                costQuantity = ArithmeticUtil.subtract(costQuantity,cjShareAmt);//变量.成本科目数量 - 变量.冲减份额
                interestBalance = interestBalance.subtract(dealNow.getIntAmt());// 变量.应计利息科目余额 - 交易明细.利息总额  
            }
            
            
            for (Map.Entry<String, BigDecimal> entity : accountAmountMap.entrySet()) {
            	accountCurrencyMap.put(entity.getKey(), dealNow.getCcy());//交易明细.交易币种
			}

            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
            ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
            ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

            /*** modify by weipeng on 20220317 传入辅助核算项 start **/
            ad.setSubjectMappingConfigDefault(dealNow.getSubjectMappingConfig());
            /*** modify by weipeng on 20220317 传入辅助核算项 end **/

            // 记账凭证生成
            BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, batchExecuteInfo);
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            returnResult.getValBokBookdetailBatch().addAll(
                resultNowNow.getValBokBookdetailBatch());
        //   }
            /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-买断式逆回购减值场景增加 start**/
            //if(金额元素.减值使用 != 0)
            if (冲减减值金额.compareTo(BigDecimal.ZERO) != 0) {
                BokStatLossPosiiton statLoss = null;
                for (ValBokBookdetailBatch detail : resultNowNow.getValBokBookdetailBatch()) {
                    // 变量.账务明细 = 调用账务明细生成共通返回结果中 基础科目性质 ="买入返售资产减值"的第一条数据
                    if (StringUtil.equals(DictCodeBaseSubjectNatrueNew.买入返售金融资产_减值准备.getValue(), detail.getBaseSubjectNature())) {
                        // 调用【减值相关共通-减值使用明细数据生成】
                        statLoss = lossCommonService.setLossInfoUsed(detail,
                                accountAmountMap.get(BookConstant.IMPAMT),
                                accountAmountMap.get(BookConstant.IMPAMT),
                                glb_subjectjz,
                                ArithmeticUtil.multiply(glb_subjectjz, 汇率),
                                dealNow.getTradeId());
                        break;
                    }
                }
                if (statLoss != null) {
                    // 变量.统计分析明细表（资产减值）减值使用列表.add(上述返回值)
                    returnResult.getBokStatLossPosiitonList().add(statLoss);
                }
            }
            /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-买断式逆回购减值场景增加 end**/
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
        SubjectBalance balance = new SubjectBalance();

        // 塞入要查询的基础科目性质
        List<String> baseSubjectNatureList = null;
        if (baseSubjectNature != null && baseSubjectNature.length() > 0) {
            baseSubjectNatureList = new ArrayList<String>();
            baseSubjectNatureList.add(baseSubjectNature);
        }
        
        //设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetIdNow);
        searchDto.setBokDetailId(bokDetailId);
        searchDto.setHappenDate(happenDateNow);
        searchDto.setBookDate(bookDateNow);
        searchDto.setTaskPri(taskPri);
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
        List<TradeInfoDto> tradeInfoDtoList = finProductCommonService.getTradeInfoDtoList(finprodId, finprodType, branch);

        // 当查询结果不为空
        if (tradeInfoDtoList != null && tradeInfoDtoList.size() > 0) {
            // 默认取第一条标的信息
            tradeInfoDtoForReturn = tradeInfoDtoList.get(0);
        }

        return tradeInfoDtoForReturn;
    }

    /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-买断式逆回购减值场景增加 start**/
    /**
     * 冲减减值计算DTO初始化
     * @param acctEntity
     * @param dealNow
     * @param flag
     * @param happenAmt
     * @param lossBalAmt
     * @param EADAmt
     * @param receAmt
     * @param totalNum
     * @param happenNum
     * @return
     */
    private LossAmtInputDto getLossAmtInputDto(FinProductDealInfoResult acctEntity, FinProductDealInfoComDTO dealNow,
                                               String flag,//01 本金  02 利息
                                               BigDecimal happenAmt,//发生金额
                                               BigDecimal lossBalAmt,//减值准备余额
                                               BigDecimal EADAmt, //当前EAD金额
                                               BigDecimal receAmt, //应收款项余额
                                               BigDecimal totalNum, //总份额
                                               BigDecimal happenNum //发生份额
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
        //发生日
        dto.setHappenDate(acctEntity.getHappenDate());
        //记账日
        dto.setBookDate(acctEntity.getBookDate());
        //发生金额 = 金额元素.回购本金 = + 金额元素.应计利息
        dto.setHappenAmt(ArithmeticUtil.round(happenAmt, NumberEnum.MONEY));
        //减值准备余额
        dto.setLossBalAmt(lossBalAmt);
        //当前EAD金额 = null
        dto.setEADAmt(EADAmt);
        //应收款余额
        dto.setReceAmt(receAmt);
        //总份额
        dto.setTotalNum(totalNum);
        //发生份额
        dto.setHappenNum(happenNum);
        //业务类型
        dto.setBusiType(dealNow.getFinprodType());
        return dto;
    }
    /** add by chufengjie on 20210927 估值核算系统VAS3.0-930-基线需求-买断式逆回购减值场景增加 end**/
}
