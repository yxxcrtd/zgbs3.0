/*
 * 文件名：secDealAccountsProcessServices.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：yangjian
 * 修改时间：2018年5月23日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.nonmonetaryfunddealbook.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokDetailId;
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
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.calculation.entity.DealTypesFeeDto;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.batch.common.fund.service.FundCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.CurrencyMarketDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.constants.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 非货币基金交易 process相关业务处理
 * 
 * @author wangwei1
 * @version 1.0 2018年6月04日
 * @see NonMonetaryFundDealProcessorNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class NonMonetaryFundDealProcessorNewService {

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
     * 公用方法services
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

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

    /**ADD BY WUJIE ON 20191120 START**/
    @Autowired
    private  FundCommonService fundCommonService;
    /**ADD BY WUJIE ON 20191120 END**/

    /**
     * process 数据处理
     * 
     * @param param1 传递参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public BookGenerateResult execute(Map<String, Object> param1) {
        // 获取reader返回值
        FinProductDealInfoResult acctEntity = (FinProductDealInfoResult)param1.get(GlobalConstant.READER_RESULT);

        // 若reader传递的参数或交易明细为空，不进行后续处理
        if (acctEntity == null || acctEntity.getListFinProductDealInfoComDTO() == null || acctEntity.getListFinProductDealInfoComDTO().isEmpty()) {
            return null;
        }

        // 定义返回值
        BookGenerateResult returnResult = new BookGenerateResult();

        // 获取从reader中拿到的值
        String booksetIdNow = acctEntity.getBooksetId();// 账套代码
        String bokDetailIdNow = acctEntity.getBokDetailId();// 账务明细代码
        String invAimNow = acctEntity.getInvAim(); // 投资目的
        TreeNode bookSetTreeNodNow = acctEntity.getBookSetTreeNode();// 会计场景
        Date happenDateNow = acctEntity.getHappenDate();// 发生日期
        Date bookDateNow = acctEntity.getBookDate();
        List<FinProductDealInfoComDTO> listFinProductDealInfoComDTONow = acctEntity.getListFinProductDealInfoComDTO(); // 金融产品交易信息list
        Long taskPri = listFinProductDealInfoComDTONow.get(0).getTaskPri();// 任务优先级（信息列表的任务优先级都是一致的，故取第一个）
          
        // 定义变量，获取科目余额相关数据
        BigDecimal varCostSubjectAmt = BigDecimal.ZERO;// 变量.成本科目余额
        BigDecimal varCostSubjectNum = BigDecimal.ZERO;// 变量.成本科目数量
        BigDecimal varYshlSubjectAmt = BigDecimal.ZERO;// 变量.应收红利科目余额
        BigDecimal varGyjzxbdSubjectAmt = BigDecimal.ZERO;// 变量.公允价值变动科目余额
        
        // 获取标的信息(相同账务明细对应相同的金融产品代码)
        TargetFinProduct targetFinProduct = this.getTargetFinProduct(listFinProductDealInfoComDTONow.get(0).getFinprodId()); 
        //保证标的信息不为空
        if(targetFinProduct == null){
            return null;
        }
        
        //获取金融产品类型
        String finprodTypeNow = targetFinProduct.getFinprodType();

        // 调用共通查询发生日前一日余额
        // 1.成本科目余额 成本科目数量 （根据金融产品类型区分【非货基公允价值变动】【计划类净值型资产】）
//        String baseSubjectNatureForCB = DictCodeBaseSubjectNatrue.基金投资_成本.getValue();//默认为【非货基公允价值变动】
//        if(DictCodeListExt1.金融产品类型_计划类资产.getValue().equals(finprodTypeNow)){
//            baseSubjectNatureForCB = DictCodeBaseSubjectNatrue.公允价值损益类_计划类资产_成本.getValue();
//        }
        String baseSubjectNatureForCB = DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue();//默认为【非货基公允价值变动】
        SubjectBalance subjectBalance1 = this.getSubjectBalance(booksetIdNow, bokDetailIdNow,happenDateNow, bookDateNow, DictCodeList.余额方向_借方.getValue(),baseSubjectNatureForCB, invAimNow, taskPri);
        if (subjectBalance1 != null) {
            varCostSubjectAmt = subjectBalance1.getBalance() == null ? BigDecimal.ZERO : subjectBalance1.getBalance();
            varCostSubjectNum = subjectBalance1.getNum() == null ? BigDecimal.ZERO : subjectBalance1.getNum();
        }
        
        // 2.应收红利科目余额
        SubjectBalance subjectBalance2 = this.getSubjectBalance(booksetIdNow, bokDetailIdNow,happenDateNow, bookDateNow, DictCodeList.余额方向_借方.getValue(),DictCodeBaseSubjectNatrueNew.应收股利.getValue(), invAimNow, taskPri);
        if (subjectBalance2 != null) {
            varYshlSubjectAmt = subjectBalance2.getBalance() == null ? BigDecimal.ZERO : subjectBalance2.getBalance();
        }
        
        // 3.公允价值变动科目余额（根据金融产品类型区分【非货基公允价值变动】【计划类净值型资产】）
//        String baseSubjectNatureForGyjz = DictCodeBaseSubjectNatrue.基金投资_估值增值.getValue();//默认为【非货基公允价值变动】
//        if(DictCodeListExt1.金融产品类型_计划类资产.getValue().equals(finprodTypeNow)){
//            baseSubjectNatureForGyjz = DictCodeBaseSubjectNatrue.公允价值损益类_计划类资产_公允价值变动.getValue();
//        }
        String baseSubjectNatureForGyjz = DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue();//默认为【非货基公允价值变动】
        SubjectBalance subjectBalance3 = this.getSubjectBalance(booksetIdNow, bokDetailIdNow,happenDateNow, bookDateNow, DictCodeList.余额方向_借方.getValue(),baseSubjectNatureForGyjz, invAimNow, taskPri);
        if (subjectBalance3 != null) {
            varGyjzxbdSubjectAmt = subjectBalance3.getBalance() == null ? BigDecimal.ZERO : subjectBalance3.getBalance();
        }

        Collections.sort(listFinProductDealInfoComDTONow, new Comparator<FinProductDealInfoComDTO>() {
            @Override
            public int compare(FinProductDealInfoComDTO o1, FinProductDealInfoComDTO o2) {
                if(StringUtil.equals(o1.getPs(),DictCodeList.交易方向_除权登记.getValue())//    T13
                        ||StringUtil.equals(o1.getPs(),DictCodeList.交易方向_红利再投.getValue())//T12
                        ||StringUtil.equals(o1.getPs(),DictCodeList.交易方向_现金分红.getValue())){//T11
                    return o2.getPs().compareTo(o1.getPs());
                }
                return 0;
            }
        });
        // 循环金融产品交易信息list,进行逻辑处理
        for (FinProductDealInfoComDTO dealNow : listFinProductDealInfoComDTONow) {
            
            // 获取交易方向
            String dealPsNow = dealNow.getPs();

            // 初始化定义 - 记账场景要素管理
            AccountDriver ad = new AccountDriver();
            // 初始化定义 - 存放该笔记账的场景要素 map定义
            Map<String, Object> accountSceneMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
            Map<String, Object> accountCurrencyMap = new HashMap<>();
            
            /**add by taoran 20191231 start**/
            // 汇率
            BigDecimal changeRate = BigDecimal.ONE;
            if(StringUtil.isNotEmpty(dealNow.getCcy()) && !dealNow.getCcy().equals(acctEntity.getStandardCcy())){
                // 调用共通汇率查询
            	//changeRate = calculationCommonService.queryExchangeRateForTrade(dealNow.getCcy(), acctEntity.getStandardCcy(), acctEntity.getHappenDate());
                /** modify by yanya on 20200108 汇率查询方法变更调用标的类共通汇率查询方法 start**/
                List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(bookDateNow, booksetIdNow,
                        acctEntity.getStandardCcy(), dealNow.getCcy());
                if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                    changeRate = currencyMarketList.get(0).getClosePrice();
                }
                /** modify by yanya on 20200108 汇率查询方法变更调用标的类共通汇率查询方法 end**/
            }
            /**add by taoran 20191231 end**/
            
            // ********** accountSceneMap设置如下**********
            accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);
            getAccountSceneMap(accountSceneMap, dealNow, targetFinProduct);

            // add by weipeng on 20220317：增加资金账户金额元素区分不同资金账户
            String 资金账户金额元素 = BookConstant.DEALAMT_HQ ; //默认托管户
            if(DictCodeListExt1.资金账户类型_结算备付金账户.getValue().equals(dealNow.getAcctType())
               || DictCodeListExt1.资金账户类型_券商资金账户.getValue().equals(dealNow.getAcctType())){
                资金账户金额元素 = BookConstant.DEALAMT_JS ;
            }

            if (DictCodeList.交易方向_认购确认.getValue().equals(dealPsNow)) {

                // **********共通元素设值 - 设置账务引擎参数**********
                ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
                ad.setTableId(DictCodeListTableId.会计场景代码_非货币基金及净值型资产_认购确认.getValue());//
                // ad.账套代码=交易类账务处理结果.账套代码;

                // ********** accountSceneMap设置如下**********
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "非货币基金" + DictCodeList.交易方向_认购确认.getCodeName() + "交易登账");

                // 调用 共通【交易各类费用处理】
                DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
                        dealNow.getValTradeFeeDetailList(), dealNow.getProdId(), dealNow.getPortfolioId());
                // 变量.交易各种费用 = 上述返回值
                // 佣金    <-   变量.交易各种费用.随交易支付_佣金
                accountAmountMap.put(BookConstant.COMMISSION_OUT,dealTypesFeeDto.getCommissionForDealPay());
                // 应付佣金	<-	变量.交易各种费用.佣金
                accountAmountMap.put(BookConstant.COMMISSION,dealTypesFeeDto.getCommission());
                // 认购费用	<-	变量.交易各种费用.随交易支付_认购费
                accountAmountMap.put(BookConstant.FEEAMT,dealTypesFeeDto.getOfferFeeForDealPay());
                
                /**MODIFY BY WUJIE ON 20191204 START
                // 确认金额+利息金额	<-	交易明细.交易本金 + 交易明细.利息金额
                accountAmountMap.put(BookConstant.DEALAMT, ArithmeticUtil.doAdd(dealNow.getPrinAmt(),dealNow.getIntAmt()));
                **/
                // 确认金额+利息金额	<-	交易明细.交易本金
                accountAmountMap.put(BookConstant.DEALAMT, dealNow.getPrinAmt());
                /**MODIFY BY WUJIE ON 20191204 END**/
                // 确认金额+认购费用（或+佣金）<-	 交易明细.交易金额
                accountAmountMap.put(BookConstant.TOTALAMT,dealNow.getTradeAmt());
                
                BigDecimal intAmt = BigDecimal.ZERO;
//              利息收入 = 交易明细.交易本金 + 变量.交易各种费用.随交易支付_佣金 + 变量.交易各种费用.随交易支付_认购费																			
//              - 交易明细.交易金额-变量.交易各种费用.佣金		
              intAmt = ArithmeticUtil.doSub(
              			ArithmeticUtil.doAdd(dealNow.getPrinAmt(),
              					dealTypesFeeDto.getCommissionForDealPay(),
              					dealTypesFeeDto.getOfferFeeForDealPay()),
              			dealNow.getTradeAmt(),
              			dealTypesFeeDto.getCommission());                
                
                if(BigDecimal.ZERO.compareTo(intAmt)!=0){
                    // 调用 共通【认购确认(利息)账务明细代码处理】
                    BokDetailId bokDetailId = fundCommonService.accountDetailProcessing(dealNow.getTradeId(),booksetIdNow);
                    if(bokDetailId!=null){
                   	   //账务明细代码2					<-		DTO.账务明细代码					
                       accountSceneMap.put(BookConstant.BOK_DETAIL_ID2, bokDetailId);
                       //资金账户类型				<-		DTO.账务明细代码.业务明细类型					
                       accountSceneMap.put(BookConstant.ACCT_TYPE, bokDetailId.getBusiDetailType());
                   }
                }
                
                // 利息金额	<-	交易明细.利息总额
                accountAmountMap.put(BookConstant.INTAMT,intAmt);
                
                // 发生数量	<-	交易明细.交易份额
                accountAmountMap.put(BookConstant.ACCQUA,dealNow.getShareAmt());

                //accountSceneMap设置如下：
                /** add by yanya on 20200113 增加佣金账务明细代码 start**/
                //佣金账务明细	<-	变量.交易各种费用.佣金账务明细
                accountSceneMap.put(BookConstant.BOK_DETAIL_COMMISSION, dealTypesFeeDto.getCommissionCode());
                /** add by yanya on 20200113 增加佣金账务明细代码 end**/

                /**add by taoran 20200102 start**/
                // 定义 本币金额元素Map
                Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
                for(String key : accountAmountMap.keySet()){
                	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
                }
                accountAmountMap.putAll(bccyAmountMap);
                // 确认金额+认购费用（或+佣金）  <-  (确认金额+利息金额)_本位币 + 本币_佣金 + 交易费用_本位币 - 利息收入_本位币 - 应付佣金_本位币        
                //  变量.(确认金额+利息金额)_本位币
                BigDecimal dealAmt_bccy = accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT);
                //  变量.本币_佣金
                BigDecimal commissionOut_bccy = accountAmountMap.get(BookConstant.COMMISSION_OUT + BookConstant.BCCYAMT);
                //  变量.交易费用_本位币
                BigDecimal fee_bccy = accountAmountMap.get(BookConstant.FEEAMT + BookConstant.BCCYAMT);
                //  变量.利息收入_本位币
                BigDecimal intAmt_bccy = accountAmountMap.get("INTAMT" + BookConstant.BCCYAMT);
                //  变量.应付佣金_本位币  
                BigDecimal commission_bccy = accountAmountMap.get(BookConstant.COMMISSION + BookConstant.BCCYAMT);
                // 变量.确认金额_本位币
                BigDecimal totalAmtBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(dealAmt_bccy, commissionOut_bccy, fee_bccy)
                    , intAmt_bccy, commission_bccy);
                accountAmountMap.put(BookConstant.TOTALAMT + BookConstant.BCCYAMT, ArithmeticUtil.round(totalAmtBccy, NumberEnum.MONEY.getCode(), null));
                /**add by taoran 20200102 end**/
               
                // ********* 全局变量更新 **********
                // 变量.成本科目余额 	=	变量.成本科目余额 + 交易明细.交易本金 + 交易明细.利息总额
                varCostSubjectAmt = ArithmeticUtil.doAdd(varCostSubjectAmt,ArithmeticUtil.doAdd(dealNow.getPrinAmt(),dealNow.getIntAmt()));
                // 变量.成本科目数量  	=	变量.成本科目数量 + 交易明细.交易份额
                varCostSubjectNum = ArithmeticUtil.doAdd(varCostSubjectNum,dealNow.getShareAmt());

            }
            else if (DictCodeList.交易方向_申购确认.getValue().equals(dealPsNow)) {
                // ********** 共通元素设值 - 设置账务引擎参数**********
                if(DictCodeListExt1.交易场所_OTC场外.getValue().equals(dealNow.getTradeMarket())
                    || DictCodeListExt1.金融产品类型_计划类资产.getValue().equals(dealNow.getFinprodType())){
                    ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
                    // ad.会计场景代码="非货币基金及净值型资产-场外申购确认"
                    ad.setTableId(DictCodeListTableId.会计场景代码_非货币基金及净值型资产_申购确认.getValue());//
                    // ********** accountSceneMap设置如下，共通**********
                    accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "非货币基金及净值型资产" + DictCodeList.交易方向_申购确认.getCodeName() + "交易登账");
                } else {
                    ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
                    // ad.会计场景代码="非货币基金-场内申购确认"
                    ad.setTableId(DictCodeListTableId.会计场景代码_非货币基金_场内申购确认.getValue());//
                    // ********** accountSceneMap设置如下，共通**********
                    accountSceneMap.put(BookConstant.BOOK_REMARK,
                        "非货币基金" + DictCodeList.交易方向_申购确认.getCodeName() + "交易登账");
                }
               
                // ********** accountAmountMap设置如下**********
                // 调用 共通【交易各类费用处理】
                DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
                        dealNow.getValTradeFeeDetailList(), dealNow.getProdId(), dealNow.getPortfolioId());
                // 变量.交易各种费用 = 上述返回值
                // 佣金	  <-  变量.交易各种费用.随交易支付_佣金
                accountAmountMap.put(BookConstant.COMMISSION_OUT,dealTypesFeeDto.getCommissionForDealPay());
                // 应付佣金	<-	变量.交易各种费用.佣金
                accountAmountMap.put(BookConstant.COMMISSION,dealTypesFeeDto.getCommission());
                // 交易费用	<-	变量.交易各种费用.随交易支付_申购费
                accountAmountMap.put(BookConstant.FEEAMT,dealTypesFeeDto.getSubscriptionFeeForDealPay());
                //  交易金额  <-  交易明细.交易金额
                accountAmountMap.put(BookConstant.TOTALAMT,dealNow.getTradeAmt());
                //  交易成本  <-  交易明细.交易本金
                accountAmountMap.put(BookConstant.DEALAMT,dealNow.getPrinAmt());
                //  发生数量  <-  交易明细.交易份额
                accountAmountMap.put(BookConstant.ACCQUA,dealNow.getShareAmt());

                //accountSceneMap设置如下：
                /** add by yanya on 20200113 增加佣金账务明细代码 start**/
                //佣金账务明细	<-	变量.交易各种费用.佣金账务明细
                accountSceneMap.put(BookConstant.BOK_DETAIL_COMMISSION, dealTypesFeeDto.getCommissionCode());
                /** add by yanya on 20200113 增加佣金账务明细代码 end**/
                
                /**add by taoran 20191231 start**/
                // 定义 本币金额元素Map
                Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
                for(String key : accountAmountMap.keySet()){
                	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
                }
                accountAmountMap.putAll(bccyAmountMap);
                
                // 交易金额_本位币<-交易成本_本位币 + 佣金_本位币 + 交易费用_本位币 - 应付佣金_本位币
                //  变量.确认金额_本位币
                BigDecimal dealAmt_bccy = accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT);
                //  变量.佣金_本位币
                BigDecimal commissionOut_bccy = accountAmountMap.get(BookConstant.COMMISSION_OUT + BookConstant.BCCYAMT);
                //  变量.交易费用_本位币
                BigDecimal fee_bccy = accountAmountMap.get(BookConstant.FEEAMT + BookConstant.BCCYAMT);
                //  变量.应付佣金_本位币  
                BigDecimal commission_bccy = accountAmountMap.get(BookConstant.COMMISSION + BookConstant.BCCYAMT);
                
                // 变量.交易总额_本位币
                BigDecimal totalAmtBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(dealAmt_bccy, commissionOut_bccy, fee_bccy)
                    , commission_bccy);
                accountAmountMap.put(BookConstant.TOTALAMT + BookConstant.BCCYAMT, ArithmeticUtil.round(totalAmtBccy, NumberEnum.MONEY.getCode(), null));
                /**add by taoran 20191231 end**/
                
                // ********** 全局变量更新**********
                // 	变量.成本科目余额  =	变量.成本科目余额 + 交易明细.交易本金
                varCostSubjectAmt = ArithmeticUtil.doAdd(varCostSubjectAmt, dealNow.getPrinAmt());
                // 变量.成本科目数量=变量.成本科目数量 + 交易明细.交易份额
                varCostSubjectNum = ArithmeticUtil.doAdd(varCostSubjectNum, dealNow.getShareAmt());

            }
            else if (DictCodeList.交易方向_买入.getValue().equals(dealPsNow)) {

                // ********** 共通元素设值 - 设置账务引擎参数**********
                ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
                ad.setTableId(DictCodeListTableId.会计场景代码_非货币基金及净值型资产_转仓买入.getValue());//

                // ********** accountSceneMap设置如下，共通**********
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                    "非货币基金" + DictCodeList.交易方向_买入.getCodeName() + "交易登账");

                // ********** accountAmountMap设置如下**********
                // 调用 共通【交易各类费用处理】
                DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
                        dealNow.getValTradeFeeDetailList(), dealNow.getProdId(), dealNow.getPortfolioId());
                // 变量.交易各种费用 = 上述返回值

                // 佣金	  <-  变量.交易各种费用.随交易支付_佣金
                accountAmountMap.put(BookConstant.COMMISSION_OUT,dealTypesFeeDto.getCommissionForDealPay());
                // 应付佣金	<-	变量.交易各种费用.佣金
                accountAmountMap.put(BookConstant.COMMISSION,dealTypesFeeDto.getCommission());
                // 交易费用	<-	变量.交易各种费用.随交易支付_其他费用
                accountAmountMap.put(BookConstant.FEEAMT,dealTypesFeeDto.getOtherFeeForDealPay());
                //  交易金额  <-  交易明细.交易金额
                accountAmountMap.put(BookConstant.DEALAMT,dealNow.getTradeAmt());
                //  交易成本  <-  交易明细.交易本金
                accountAmountMap.put(BookConstant.COST,dealNow.getPrinAmt());
                //  发生数量  <-  交易明细.交易份额
                accountAmountMap.put(BookConstant.ACCQUA,dealNow.getShareAmt());

                //accountSceneMap设置如下：
                /** add by yanya on 20200113 增加佣金账务明细代码 start**/
                //佣金账务明细	<-	变量.交易各种费用.佣金账务明细
                accountSceneMap.put(BookConstant.BOK_DETAIL_COMMISSION, dealTypesFeeDto.getCommissionCode());
                /** add by yanya on 20200113 增加佣金账务明细代码 end**/
                
                /**20200102 taoran add start**/
                // 定义 本币金额元素Map
                Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
                for(String key : accountAmountMap.keySet()){
                	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
                }
                accountAmountMap.putAll(bccyAmountMap);
                //	利息收入_本位币<-交易成本_本位币 + 佣金_本位币 + 交易费用_本位币 - 应付佣金_本位币									
                BigDecimal costBccy = accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT);
                BigDecimal commissionOutBccy = accountAmountMap.get(BookConstant.COMMISSION_OUT + BookConstant.BCCYAMT);
                BigDecimal feeAmtBccy = accountAmountMap.get(BookConstant.FEEAMT + BookConstant.BCCYAMT);
                BigDecimal commissionBccy = accountAmountMap.get(BookConstant.COMMISSION + BookConstant.BCCYAMT);
                BigDecimal intAmtBccy = ArithmeticUtil.doSub(
                		ArithmeticUtil.doAdd(costBccy,commissionOutBccy,feeAmtBccy), commissionBccy);
                accountAmountMap.put(BookConstant.INTAMT + BookConstant.BCCYAMT,ArithmeticUtil.round(intAmtBccy, NumberEnum.MONEY.getCode(), null));
                /**20200102 taoran add end**/
                
                // ********** 全局变量更新**********
                // 变量.成本科目余额=变量.成本科目余额 + 交易明细.交易本金
                varCostSubjectAmt = ArithmeticUtil.doAdd(varCostSubjectAmt, dealNow.getPrinAmt());
                // 变量.成本科目数量=变量.成本科目数量 + 交易明细.交易份额
                varCostSubjectNum = ArithmeticUtil.doAdd(varCostSubjectNum, dealNow.getShareAmt());

            }
            else if (DictCodeList.交易方向_份额强增.getValue().equals(dealPsNow)) {

                // ********** 共通元素设值 - 设置账务引擎参数**********
                // ad.setAccountType(DictCodeList.账务记账类型_正常记账.getValue());//ad.凭证类型=#正常记账#;
                // ad.setTradeId(dealNow.getTradeId());//ad.交易编号=交易编号
                ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
                // ad.setAccountType(DictCodeList.账务记账类型_正常记账.getValue());//ad.凭证类型=#正常记账#;
                ad.setTableId(DictCodeListTableId.会计场景代码_非货币基金及净值型资产_份额调增.getValue());//

                // ********** accountSceneMap设置如下，共通**********
                /**MODIFY BY WUJIE ON 20210129 START REMARK:金额为0也出凭证
                accountSceneMap.put(BookConstant.IS_ZERO_ACCOUNT,DictCodeList.是否_否.getValue() );
                **/
                accountSceneMap.put(BookConstant.IS_ZERO_ACCOUNT,DictCodeList.是否_是.getValue());
                /**MODIFY BY WUJIE ON 20210129 END **/
                
                accountSceneMap.put(BookConstant.ACCT_TYPE,dealNow.getAcctType());

                // ********** accountAmountMap设置如下**********
                // 发生数量	<-	交易明细.交易份额
                accountAmountMap.put(BookConstant.ACCQUA,dealNow.getShareAmt()==null?BigDecimal.ZERO:dealNow.getShareAmt());
                // 成本金额	<-	交易明细.交易本金
                accountAmountMap.put(资金账户金额元素,dealNow.getTradeAmt()==null?BigDecimal.ZERO:dealNow.getTradeAmt());
                // 交易金额	<-	交易明细.交易金额
                accountAmountMap.put(BookConstant.DEALAMT,dealNow.getTradeAmt()==null?BigDecimal.ZERO:dealNow.getTradeAmt());
                
                /**20200102 taoran add start**/
                //成本金额_本位币<-	成本金额 * 变量.汇率						
                accountAmountMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT, 
                		calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(BookConstant.DEALAMT), changeRate));
                //交易金额_本位币<-	成本金额_本位币
                accountAmountMap.put(资金账户金额元素 + BookConstant.BCCYAMT,
                		accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT));
                /**20200102 taoran add end**/
                
                // ********** 全局变量更新**********
                // 变量.成本科目余额=变量.成本科目余额 + 交易明细.交易本金
                varCostSubjectAmt = ArithmeticUtil.doAdd(varCostSubjectAmt, dealNow.getPrinAmt());
                // 变量.成本科目数量=变量.成本科目数量 + 交易明细.交易份额
                varCostSubjectNum = ArithmeticUtil.doAdd(varCostSubjectNum, dealNow.getShareAmt());

            }
            else if (DictCodeList.交易方向_份额强减.getValue().equals(dealPsNow)) {

                // ********** 共通元素设值 - 设置账务引擎参数**********
                // ad.setAccountType(DictCodeList.账务记账类型_正常记账.getValue());//ad.凭证类型=#正常记账#;
                // ad.setTradeId(dealNow.getTradeId());//ad.交易编号=交易编号
                ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
                // ad.setAccountType(DictCodeList.账务记账类型_正常记账.getValue());//ad.凭证类型=#正常记账#;
                ad.setTableId(DictCodeListTableId.会计场景代码_非货币基金及净值型资产_份额调减.getValue());//
                // ad.账套代码=交易类账务处理结果.账套代码;
                // ad.交易编号=交易明细.交易编号;

                // ********** accountSceneMap设置如下，共通**********
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                    "非货币基金" + DictCodeList.交易方向_份额强减.getCodeName() + "交易登账");
                
                accountSceneMap.put(BookConstant.ACCT_TYPE,dealNow.getAcctType());
                
                /**ADD BY WUJIE ON 20210129 START REMARK:金额为0也出凭证**/
                accountSceneMap.put(BookConstant.IS_ZERO_ACCOUNT,DictCodeList.是否_是.getValue());
                /**ADD BY WUJIE ON 20210129 END **/

                // ********** accountAmountMap设置如下**********
                // 发生数量	<-	交易明细.交易份额
                accountAmountMap.put(BookConstant.ACCQUA,dealNow.getShareAmt()==null?BigDecimal.ZERO:dealNow.getShareAmt());
                // 成本金额	<-	交易明细.交易本金
                accountAmountMap.put(BookConstant.DEALAMT,dealNow.getTradeAmt()==null?BigDecimal.ZERO:dealNow.getTradeAmt());
                // 交易金额	<-	交易明细.交易金额
                accountAmountMap.put(资金账户金额元素,dealNow.getTradeAmt()==null?BigDecimal.ZERO:dealNow.getTradeAmt());
                
                /**20200102 taoran add start**/
                //成本金额_本位币<-	成本金额 * 变量.汇率						
                accountAmountMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT, 
                		calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(BookConstant.DEALAMT), changeRate));
                //交易金额_本位币<-	成本金额_本位币		debug					
                accountAmountMap.put(资金账户金额元素 + BookConstant.BCCYAMT,
                		accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT));
                /**20200102 taoran add end**/
                
                // ********** 全局变量更新**********
                // 变量.成本科目余额=变量.成本科目余额 + 交易明细.交易本金
                varCostSubjectAmt = ArithmeticUtil.doSub(varCostSubjectAmt, dealNow.getPrinAmt());
                // 变量.成本科目数量=变量.成本科目数量 + 交易明细.交易份额
                varCostSubjectNum = ArithmeticUtil.doSub(varCostSubjectNum, dealNow.getShareAmt());

            }
            else if (DictCodeList.交易方向_赎回确认.getValue().equals(dealPsNow)) {
                // ********** 共通元素设值 - 设置账务引擎参数**********
                if(DictCodeListExt1.交易场所_OTC场外.getValue().equals(dealNow.getTradeMarket())
                		|| DictCodeListExt1.金融产品类型_计划类资产.getValue().equals(dealNow.getFinprodType())){
                    ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
                    // ad.会计场景代码="非货币基金及净值型资产-场外赎回确认"
                    ad.setTableId(DictCodeListTableId.会计场景代码_非货币基金及净值型资产_赎回确认.getValue());//
                } else {
                    ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
                    // ad.会计场景代码="非货币基金-场内赎回确认"
                    ad.setTableId(DictCodeListTableId.会计场景代码_非货币基金_场内赎回确认.getValue());//
                }

                BigDecimal varCjgyjzbdSubjectAmt = BigDecimal.ZERO; // 变量.冲减公允价值变动
                BigDecimal varCjcbSubjectAmt = BigDecimal.ZERO;// 变量.冲减成本
                BigDecimal varCjslSubjectNum = BigDecimal.ZERO;// 变量.冲减数量

                // ********** accountSceneMap设置如下，共通**********
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                    "非货币基金" + DictCodeList.交易方向_赎回确认.getCodeName() + "交易登账");

                // ********** accountAmountMap设置如下**********
                // 调用 共通【交易各类费用处理】
                DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
                        dealNow.getValTradeFeeDetailList(), dealNow.getProdId(), dealNow.getPortfolioId());
                // 变量.交易各种费用 = 上述返回值
                // 佣金	 <-	变量.交易各种费用.随交易支付_佣金
                accountAmountMap.put(BookConstant.COMMISSION_OUT,dealTypesFeeDto.getCommissionForDealPay());
                // 应付佣金	<-	变量.交易各种费用.佣金
                accountAmountMap.put(BookConstant.COMMISSION,dealTypesFeeDto.getCommission());
                // 交易费用	<-	变量.交易各种费用.随交易支付_赎回费
                accountAmountMap.put(BookConstant.FEEAMT,dealTypesFeeDto.getRedemptionFeeForDealPay());

                // 变量.冲减公允价值变动= 变量.公允价值变动科目余额 * 交易明细.交易份额/变量.成本科目数量
                varCjgyjzbdSubjectAmt = ArithmeticUtil.multiply(varGyjzxbdSubjectAmt,(ArithmeticUtil.divide(dealNow.getShareAmt(), varCostSubjectNum)));
                //调用金额共通方法设置有效小数位
                varCjgyjzbdSubjectAmt = ArithmeticUtil.round(varCjgyjzbdSubjectAmt, NumberEnum.MONEY);
                
                // 变量.冲减成本=变量.成本科目余额 * 交易明细.交易份额/变量.成本科目数量
                varCjcbSubjectAmt = ArithmeticUtil.multiply(varCostSubjectAmt,(ArithmeticUtil.divide(dealNow.getShareAmt(), varCostSubjectNum)));
                //调用金额共通方法设置有效小数位
                varCjcbSubjectAmt = ArithmeticUtil.round(varCjcbSubjectAmt, NumberEnum.MONEY);
                
                // 变量.冲减数量=NVL（交易明细.交易份额，0）
                varCjslSubjectNum = dealNow.getShareAmt() == null ? BigDecimal.ZERO : dealNow.getShareAmt();
                //调用金额共通方法设置有效小数位
                varCjslSubjectNum = ArithmeticUtil.round(varCjslSubjectNum, NumberEnum.MONEY);

                // 实收金额<- 交易明细.交易金额
                accountAmountMap.put(BookConstant.DEALAMT,dealNow.getTradeAmt());
                // 交易费用<- 交易明细.交易费用
                accountAmountMap.put(BookConstant.REFEE_AMT,dealNow.getFeeAmt());
                // 成本金额<- 变量.冲减成本
                accountAmountMap.put(BookConstant.DCOST_AMT,varCjcbSubjectAmt);
                // 发生数量  <-  变量.冲减数量
                accountAmountMap.put(BookConstant.ACCQUA,varCjslSubjectNum);

                if (BigDecimal.ZERO.compareTo(varCjgyjzbdSubjectAmt) < 0) {
                    // 公允价值变动_贷<- 变量.冲减公允价值变动 FLOAT_C
                    accountAmountMap.put(BookConstant.FLOAT_C,varCjgyjzbdSubjectAmt);
                    // 公允价值变动损益_借<- 变量.冲减公允价值变动 FLOAT_INGPL_D
                    accountAmountMap.put(BookConstant.FLOAT_INGPL_D,varCjgyjzbdSubjectAmt);
                }
                else {
                    // 公允价值变动_借<-变量.公允价值变动*-1 FLOAT_D
                    accountAmountMap.put(BookConstant.FLOAT_D,varCjgyjzbdSubjectAmt.negate());
                    // 公允价值变动损益_贷<-变量.公允价值变动*-1 FLOAT_INGPL_C
                    accountAmountMap.put(BookConstant.FLOAT_INGPL_C,varCjgyjzbdSubjectAmt.negate());
                }

                // 买卖价差<-交易明细.交易金额 + 交易明细.交易费用 - 变量.冲减成本
                /* BigDecimal varMmjc = ArithmeticUtil.doSub(
                    ArithmeticUtil.doAdd(dealNow.getTradeAmt(), dealNow.getFeeAmt()),
                    varCjcbSubjectAmt);*/

                // 定义 变量.买卖价差 =	交易明细.交易金额 + 变量.交易各种费用.随交易支付_赎回费 + 变量.交易各种费用.随交易支付_佣金
                        // - 变量.交易各种费用.佣金 - 变量.冲减成本
                BigDecimal varMmjc =  ArithmeticUtil.doSub(
                        ArithmeticUtil.doAdd(
                            ArithmeticUtil.doAdd(dealNow.getTradeAmt(),dealTypesFeeDto.getRedemptionFeeForDealPay()),
                            ArithmeticUtil.doSub(dealTypesFeeDto.getCommissionForDealPay(),dealTypesFeeDto.getCommission())
                        ),varCjcbSubjectAmt);

                if (varMmjc.compareTo(BigDecimal.ZERO) >= 0) {
                    accountAmountMap.put(BookConstant.DSCLOSS_C,varMmjc);
                }
                else {
                    accountAmountMap.put(BookConstant.DSCLOSS_D,varMmjc.negate());
                }

                //accountSceneMap设置如下：
                /** add by yanya on 20200113 增加佣金账务明细代码 start**/
                //佣金账务明细	<-	变量.交易各种费用.佣金账务明细
                accountSceneMap.put(BookConstant.BOK_DETAIL_COMMISSION, dealTypesFeeDto.getCommissionCode());
                /** add by yanya on 20200113 增加佣金账务明细代码 end**/

                /**20200102 taoran add start**/
                // 定义 本币金额元素Map
                Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
                for(String key : accountAmountMap.keySet()){
                	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
                }
                accountAmountMap.putAll(bccyAmountMap);
                //定义 本币_变量.买卖价差
            	BigDecimal varMmjcBccy = BigDecimal.ZERO;
            	//变量.买卖价差_本位币 = (实收金额_本位币 + 随交易支付_佣金_本位币 + 交易费用_本位币) -(成本金额_本位币 + 应付佣金_本位币)                                                                                                    
                BigDecimal dealAmtBccy = accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT);
                BigDecimal commissionOutBccy = accountAmountMap.get(BookConstant.COMMISSION_OUT + BookConstant.BCCYAMT);
                BigDecimal feeAmtBccy = accountAmountMap.get(BookConstant.FEEAMT + BookConstant.BCCYAMT);
                BigDecimal dcostAmtBccy = accountAmountMap.get(BookConstant.DCOST_AMT + BookConstant.BCCYAMT);
                BigDecimal commissionBccy = accountAmountMap.get(BookConstant.COMMISSION + BookConstant.BCCYAMT);
                varMmjcBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(dealAmtBccy, commissionOutBccy, feeAmtBccy)
                        , ArithmeticUtil.doAdd(dcostAmtBccy, commissionBccy));
            	if(varMmjcBccy.compareTo(BigDecimal.ZERO)>0) {
            		accountAmountMap.put(BookConstant.DSCLOSS_C + BookConstant.BCCYAMT, ArithmeticUtil.round(varMmjcBccy, NumberEnum.MONEY.getCode(), null));
            	}
            	else {
            		accountAmountMap.put(BookConstant.DSCLOSS_D + BookConstant.BCCYAMT, ArithmeticUtil.round(varMmjcBccy.negate(), NumberEnum.MONEY.getCode(), null));
            	}
                /**20200102 taoran add end**/
                
                // ********** 全局变量更新**********
                // 变量.成本科目余额= 变量.成本科目余额 - 变量.冲减成本
                varCostSubjectAmt = ArithmeticUtil.doSub(varCostSubjectAmt, varCjcbSubjectAmt);
                // 变量.成本科目数量 = 变量.成本科目数量 - 变量.冲减数量
                varCostSubjectNum = ArithmeticUtil.doSub(varCostSubjectNum, varCjslSubjectNum);
                // 变量.公允价值变动科目余额 = 变量.公允价值变动科目余额 - 变量.冲减公允价值变动
                varGyjzxbdSubjectAmt = ArithmeticUtil.doSub(varGyjzxbdSubjectAmt,
                    varCjgyjzbdSubjectAmt);

            }

            /** modify by yanya on 20200111 非货币基金增加转换交易处理，转入方向变更为转换申购确认 start**/
            //else if (DictCodeList.交易方向_转入.getValue().equals(dealPsNow)) {
            else if (StringUtil.equals(dealPsNow, DictCodeListExt3.交易方向_转换申购确认.getValue())) {
//                // ********** 共通元素设值 - 设置账务引擎参数**********
//                // ad.setAccountType(DictCodeList.账务记账类型_正常记账.getValue());//ad.凭证类型=#正常记账#;
//                // ad.setTradeId(dealNow.getTradeId());//ad.交易编号=交易编号
//                ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
//                // ad.setAccountType(DictCodeList.账务记账类型_正常记账.getValue());//ad.凭证类型=#正常记账#;
//                ad.setTableId("会计场景代码_非货币基金转入交易");//
//                // ********** accountSceneMap设置如下，共通**********
//                accountSceneMap.put(BookConstant.BOOK_REMARK,
//                    "非货币基金" + DictCodeList.交易方向_转入.getCodeName() + "交易登账");
//
//                // ********** accountAmountMap设置如下**********
//                // 发生数量<-交易明细.交易份额
//                accountAmountMap.put(BookConstant.ACCQUA, dealNow.getShareAmt());
//                // 成本金额<-交易明细.交易本金
//                accountAmountMap.put(BookConstant.COST, dealNow.getPrinAmt());
//                // 交易金额<-交易明细.交易金额
//                accountAmountMap.put(BookConstant.DEALAMT, dealNow.getTradeAmt());
//
//                // ********** 全局变量更新**********
//                // 变量.成本科目余额=变量.成本科目余额 + 交易明细.交易本金
//                varCostSubjectAmt = ArithmeticUtil.doAdd(varCostSubjectAmt, dealNow.getPrinAmt());
//                // 变量.成本科目数量=变量.成本科目数量 + 交易明细.交易份额
//                varCostSubjectNum = ArithmeticUtil.doAdd(varCostSubjectNum, dealNow.getShareAmt());
                ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
                //ad.会计场景代码="货币基金-基金转换入确认交易"
                ad.setTableId(DictCodeListTableId.会计场景代码_非货币基金及净值型资产_转换入确认交易.getValue());
                //accountAmountMap设置如下
                //调用 共通【交易各类费用处理】
                /**
                 参数名：					费用处理共通
                 交易费用列表					交易明细.交易费用列表
                 金融产品代码					交易明细.理财产品代码
                 投资组合代码					交易明细.投资组合代码
                 返回值:					交易各类费用
                 **/
                //定义 变量.交易各种费用 = 上述返回值
                DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(dealNow.getValTradeFeeDetailList(),
                        dealNow.getProdId(), dealNow.getPortfolioId());
                //佣金		<-	变量.交易各种费用.随交易支付_佣金
                accountAmountMap.put(BookConstant.COMMISSION_OUT, dealTypesFeeDto.getCommissionForDealPay());
                //应付佣金		<-		变量.交易各种费用.佣金
                accountAmountMap.put(BookConstant.COMMISSION, dealTypesFeeDto.getCommission());
                //交易费用		<-		变量.交易各种费用.随交易支付_申购费
                accountAmountMap.put(BookConstant.FEEAMT, dealTypesFeeDto.getSubscriptionFeeForDealPay());
                //发生数量		<-		交易明细.交易份额
                accountAmountMap.put(BookConstant.ACCQUA, dealNow.getShareAmt());
                //转入金额		<-		交易明细.交易本金
                accountAmountMap.put(BookConstant.COST_INTO, dealNow.getPrinAmt());
                //应收红利		<-		交易明细.利息总额
                //accountAmountMap.put(BookConstant.ACCQUA, dealDto.getIntAmt());
                //转出金额		<-		交易明细.交易金额
                accountAmountMap.put(BookConstant.TRADE_QTY, dealNow.getTradeAmt());

                //accountSceneMap设置如下：
                /** add by yanya on 20200113 增加佣金账务明细代码 start**/
                //佣金账务明细	<-	变量.交易各种费用.佣金账务明细
                accountSceneMap.put(BookConstant.BOK_DETAIL_COMMISSION, dealTypesFeeDto.getCommissionCode());
                /** add by yanya on 20200113 增加佣金账务明细代码 end**/

                //定义 本币金额元素Map
                Map<String, BigDecimal> accountAmountBCCYAMTMap = new HashMap<>();
                for(String key : accountAmountMap.keySet()) {
                    accountAmountBCCYAMTMap.put(key + BookConstant.BCCYAMT,
                            calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
                    accountCurrencyMap.put(key + BookConstant.BCCYAMT, acctEntity.getStandardCcy());
                }
                accountAmountMap.putAll(accountAmountBCCYAMTMap);
                //转出金额_本位币	<-		转入金额_本位币 + 佣金_本位币 + 交易费用_本位币 - 应付佣金_本位币
                accountAmountMap.put(BookConstant.TRADE_QTY + BookConstant.BCCYAMT,
                        ArithmeticUtil.round(
                                ArithmeticUtil.doSub(
                                        ArithmeticUtil.doAdd(
                                                accountAmountMap.get(BookConstant.COST_INTO + BookConstant.BCCYAMT),
                                                accountAmountMap.get(BookConstant.COMMISSION_OUT + BookConstant.BCCYAMT),
                                                accountAmountMap.get(BookConstant.FEEAMT + BookConstant.BCCYAMT)
                                        ),
                                        ArithmeticUtil.doAdd(
                                                accountAmountMap.get(BookConstant.COMMISSION + BookConstant.BCCYAMT)
                                                //accountAmountMap.get(BookConstant.BONUSNET_D + BookConstant.BCCYAMT)
                                        )
                                ),
                                NumberEnum.MONEY));
                //调用 共通【基金转换_账务明细代码处理】  参考本设计书sheet【基金转换_账务明细代码处理】
                /**参数名：					基金转换_账务明细代码处理
                 交易编号					交易明细.交易编号
                 返回值:					DTO(账务明细代码)**/

                //IF DTO.账务明细代码 IS NOT NULL
                BokDetailId bokDetailId = fundCommonService.getFundBokDetailId(dealNow.getTradeId());
                //账务明细代码2					<-		DTO.账务明细代码
                if(bokDetailId != null){
                    //账务明细代码2					<-		DTO.账务明细代码
                    accountSceneMap.put(BookConstant.BOK_DETAIL_ID2, bokDetailId);
                }
                //本位币	<-		交易类账务处理结果.本位币
                accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
                //全局变量的更新
                //变量.成本科目余额 	=	变量.成本科目余额 + 交易明细.交易本金
                varCostSubjectAmt = ArithmeticUtil.doAdd(varCostSubjectAmt, dealNow.getPrinAmt());
                //变量.成本科目数量		=	变量.成本科目数量 + 交易明细.交易份额
                varCostSubjectNum = ArithmeticUtil.doAdd(varCostSubjectNum, dealNow.getShareAmt());

            }
            /** modify by yanya on 20200111 非货币基金增加转换交易处理，转入方向变更为转换申购确认 end**/
            /** modify by yanya on 20200111 非货币基金增加转换交易处理，转出方向变更为转换赎回确认 start**/
            //else if (DictCodeList.交易方向_转出.getValue().equals(dealPsNow)) {
            else if (StringUtil.equals(dealPsNow, DictCodeListExt3.交易方向_转换赎回确认.getValue())) {

//                // ********** 共通元素设值 - 设置账务引擎参数**********
//                // ad.setAccountType(DictCodeList.账务记账类型_正常记账.getValue());//ad.凭证类型=#正常记账#;
//                // ad.setTradeId(dealNow.getTradeId());//ad.交易编号=交易编号
//                ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
//                // ad.setAccountType(DictCodeList.账务记账类型_正常记账.getValue());//ad.凭证类型=#正常记账#;
//                ad.setTableId("会计场景代码_非货币基金转出交易");//
//                // ad.账套代码=交易类账务处理结果.账套代码;
//                // ad.交易编号=交易明细.交易编号;
//
//                // ********** accountSceneMap设置如下，共通**********
//                accountSceneMap.put(BookConstant.BOOK_REMARK,
//                    "非货币基金" + DictCodeList.交易方向_转出.getCodeName() + "交易登账");
//
//                // ********** accountAmountMap设置如下**********
//                // 发生数量<-交易明细.交易份额
//                accountAmountMap.put(BookConstant.HAPPEN_NUMBER, dealNow.getShareAmt());
//                // 转出本金  <-	交易明细.交易本金
//                accountAmountMap.put(BookConstant.COST, dealNow.getPrinAmt());
//                // 交易费用<-交易明细.交易费用 注：随交易支付的。
//                accountAmountMap.put(BookConstant.FEEAMT, dealNow.getFeeAmt());
//                // 交易金额<-交易明细.交易金额
//                accountAmountMap.put(BookConstant.DEALAMT, dealNow.getTradeAmt());
//
//                // ********** 全局变量更新**********
//                // 变量.成本科目余额=变量.成本科目余额 + 交易明细.交易本金
//                varCostSubjectAmt = ArithmeticUtil.doSub(varCostSubjectAmt, dealNow.getPrinAmt());
//                // 变量.成本科目数量=变量.成本科目数量 + 交易明细.交易份额
//                varCostSubjectNum = ArithmeticUtil.doSub(varCostSubjectNum, dealNow.getShareAmt());

                ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
                //ad.会计场景代码="货币基金-基金转换出确认交易"
                ad.setTableId(DictCodeListTableId.会计场景代码_非货币基金及净值型资产_转换出确认交易.getValue());
                //accountAmountMap设置如下
                //调用 共通【交易各类费用处理】
                /**
                 参数名：					费用处理共通
                 交易费用列表					交易明细.交易费用列表
                 金融产品代码					交易明细.理财产品代码
                 投资组合代码					交易明细.投资组合代码
                 返回值:					交易各类费用
                 **/
                //定义 变量.交易各种费用 = 上述返回值
                DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(dealNow.getValTradeFeeDetailList(),
                        dealNow.getProdId(), dealNow.getPortfolioId());
                //佣金		<-	变量.交易各种费用.随交易支付_佣金
                accountAmountMap.put(BookConstant.COMMISSION_OUT, dealTypesFeeDto.getCommissionForDealPay());
                //应付佣金		<-		变量.交易各种费用.佣金
                accountAmountMap.put(BookConstant.COMMISSION, dealTypesFeeDto.getCommission());
                //交易费用		<-		变量.交易各种费用.随交易支付_赎回费
                accountAmountMap.put(BookConstant.REFEE_AMT, dealTypesFeeDto.getRedemptionFeeForDealPay());
                //定义 变量.冲减公允价值变动	=	变量.公允价值变动科目 * 交易明细.交易份额/变量.成本科目数量
                BigDecimal 冲减公允价值变动 = ArithmeticUtil.multiply(varGyjzxbdSubjectAmt,
                        (ArithmeticUtil.divide(dealNow.getShareAmt(),varCostSubjectNum)));
                冲减公允价值变动 = ArithmeticUtil.round(冲减公允价值变动, NumberEnum.MONEY);
                //定义 变量.冲减成本	=	变量.成本科目余额 * 交易明细.交易份额/变量.成本科目数量
                BigDecimal 冲减成本 = ArithmeticUtil.multiply(varCostSubjectAmt,
                        (ArithmeticUtil.divide(dealNow.getShareAmt(), varCostSubjectNum)));
                //调用金额共通方法设置有效小数位
                冲减成本 = ArithmeticUtil.round(冲减成本, NumberEnum.MONEY);
                //定义 变量.冲减数量	=	NVL（交易明细.交易份额，0）
                BigDecimal 冲减数量 = dealNow.getShareAmt() == null ? BigDecimal.ZERO : dealNow.getShareAmt();
                冲减数量 = ArithmeticUtil.round(冲减数量, NumberEnum.MONEY);
                //转出金额		<-		交易明细.交易金额
                accountAmountMap.put(BookConstant.TRADE_QTY, dealNow.getTradeAmt());
                //发生数量	<-	变量.冲减数量
                accountAmountMap.put(BookConstant.ACCQUA, 冲减数量);
                //转出本金	<-		变量.冲减成本
                accountAmountMap.put(BookConstant.COST_OUT, 冲减成本);
                //IF 变量.冲减公允价值变动 > 0
                if(冲减公允价值变动.compareTo(BigDecimal.ZERO) > 0) {
                    //公允价值变动_贷	<-	变量.冲减公允价值变动		FLOAT_C
                    accountAmountMap.put(BookConstant.FLOAT_C, 冲减公允价值变动);
                    //公允价值变动损益_借	<-		变量.冲减公允价值变动		FLOAT_INGPL_D
                    accountAmountMap.put(BookConstant.FLOAT_INGPL_D, 冲减公允价值变动);
                }
                else {
                    //公允价值变动_借		<-		变量.公允价值变动*-1	FLOAT_D
                    accountAmountMap.put(BookConstant.FLOAT_D, ArithmeticUtil.multiply(冲减公允价值变动, new BigDecimal(-1)));
                    //公允价值变动损益_贷	<-	变量.公允价值变动*-1		FLOAT_INGPL_C
                    accountAmountMap.put(BookConstant.FLOAT_INGPL_C, ArithmeticUtil.multiply(冲减公允价值变动, new BigDecimal(-1)));
                }
                /**
                 定义 变量.买卖价差 =	交易明细.交易金额 + 变量.交易各种费用.随交易支付_赎回费 + 变量.交易各种费用.随交易支付_佣金
                 - 变量.交易各种费用.佣金 - 变量.冲减成本
                 **/
                BigDecimal 买卖价差 = ArithmeticUtil.doSub(
                        ArithmeticUtil.doAdd(dealNow.getTradeAmt(),
                                dealTypesFeeDto.getCommissionForDealPay(), dealTypesFeeDto.getRedemptionFeeForDealPay()),
                        ArithmeticUtil.doAdd(冲减成本, dealTypesFeeDto.getCommission()));
                // IF 变量.买卖价差 > 0
                if (买卖价差.compareTo(BigDecimal.ZERO) > 0) {
                    //买卖价差_贷	<-		变量.买卖价差
                    accountAmountMap.put(BookConstant.NETAMT_C, 买卖价差);
                }
                else {
                    //买卖价差_借	<-	变量.买卖价差*-1
                    accountAmountMap.put(BookConstant.NETAMT_D, ArithmeticUtil.multiply(买卖价差, new BigDecimal(-1)));
                }

                //accountSceneMap设置如下：
                /** add by yanya on 20200113 增加佣金账务明细代码 start**/
                //佣金账务明细	<-	变量.交易各种费用.佣金账务明细
                accountSceneMap.put(BookConstant.BOK_DETAIL_COMMISSION, dealTypesFeeDto.getCommissionCode());
                /** add by yanya on 20200113 增加佣金账务明细代码 end**/

                //定义 本币金额元素Map
                Map<String, BigDecimal> accountAmountBCCYAMTMap = new HashMap<>();
                for(String key : accountAmountMap.keySet()) {
                    accountAmountBCCYAMTMap.put(key + BookConstant.BCCYAMT,
                            calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
                    accountCurrencyMap.put(key + BookConstant.BCCYAMT, acctEntity.getStandardCcy());
                }
                accountAmountMap.putAll(accountAmountBCCYAMTMap);
                //定义 变量.买卖价差_本位币 = (转出金额_本位币 + 佣金_本位币 + 交易费用_本位币) - (转出本金_本位币 + 应付佣金_本位币)
                BigDecimal 买卖价差本位币 = ArithmeticUtil.doSub(
                        ArithmeticUtil.doAdd(
                                accountAmountMap.get(BookConstant.TRADE_QTY + BookConstant.BCCYAMT),
                                accountAmountMap.get(BookConstant.COMMISSION_OUT + BookConstant.BCCYAMT),
                                accountAmountMap.get(BookConstant.REFEE_AMT + BookConstant.BCCYAMT)
                        ),
                        ArithmeticUtil.doAdd(
                                accountAmountMap.get(BookConstant.COMMISSION + BookConstant.BCCYAMT),
                                accountAmountMap.get(BookConstant.COST_OUT + BookConstant.BCCYAMT)
                        )
                );
                //IF 变量.买卖价差 > 0
                if(买卖价差本位币.compareTo(BigDecimal.ZERO) > 0) {
                    //买卖价差_贷_本位币	<-	变量.买卖价差_本位币
                    accountAmountMap.put(BookConstant.NETAMT_C + BookConstant.BCCYAMT, 买卖价差本位币);
                }
                else {
                    //买卖价差_借_本位币	<-	变量.买卖价差_本位币 * -1
                    accountAmountMap.put(BookConstant.NETAMT_D + BookConstant.BCCYAMT, ArithmeticUtil.multiply(买卖价差本位币, new BigDecimal(-1)));
                }
                //本位币	<-		交易类账务处理结果.本位币
                accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
                //全局变量的更新
                //变量.成本科目余额  =	变量.成本科目余额 - 变量.冲减成本
                varCostSubjectAmt = ArithmeticUtil.doSub(varCostSubjectAmt, 冲减成本);
                // 变量.成本科目数量	=	变量.成本科目数量 - 变量.冲减数量
                varCostSubjectNum = ArithmeticUtil.doSub(varCostSubjectNum, 冲减数量);
                //变量.公允价值变动科目余额	=	变量.公允价值变动科目余额 - 变量.冲减公允价值变动
                varGyjzxbdSubjectAmt = ArithmeticUtil.doSub(varGyjzxbdSubjectAmt, 冲减公允价值变动);

            }
            /** modify by yanya on 20200111 非货币基金增加转换交易处理，转出方向变更为转换赎回确认 end**/

            else if (DictCodeList.交易方向_除权登记.getValue().equals(dealPsNow)) { // wanwgei1note

                // ********** 共通元素设值 - 设置账务引擎参数**********
                ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
                ad.setTableId(DictCodeListTableId.会计场景代码_非货币基金及净值型资产_分红除权.getValue());//

                // ********** accountSceneMap设置如下，共通**********
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                    "非货币基金" + DictCodeList.交易方向_除权登记.getCodeName() + "交易登账");

                // ********** accountAmountMap设置如下**********
                // 分红金额 <-交易明细.交易金额
                accountAmountMap.put(BookConstant.INTAMT,dealNow.getIntAmt());
                
                //分红金额_本位币<-	分红金额 * 变量.汇率						
                accountAmountMap.put(BookConstant.INTAMT + BookConstant.BCCYAMT, 
                		calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(BookConstant.INTAMT), changeRate));
                
                // ********** 全局变量更新**********
                // 变量.应收红利科目余额 = 变量.应收红利科目余额 + 交易明细.利息总额
                varYshlSubjectAmt = ArithmeticUtil.doAdd(varYshlSubjectAmt, dealNow.getIntAmt());

            }
            else if (DictCodeList.交易方向_现金分红.getValue().equals(dealPsNow)) {

                // ********** 共通元素设值 - 设置账务引擎参数**********
                ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
                ad.setTableId(DictCodeListTableId.会计场景代码_非货币基金及净值型资产_现金分红.getValue());//

                // ********** accountSceneMap设置如下，共通**********
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                    "非货币基金" + DictCodeList.交易方向_现金分红.getCodeName() + "交易登账");

                // ********** accountAmountMap设置如下**********
                BigDecimal varYsfghAmt = varYshlSubjectAmt; // 定义 变量.应收分红 = 变量.应收红利科目余额
                // 定义 变量.轧差金额 = 交易明细.交易金额 - 变量.应收分红
                BigDecimal varYcAmt = ArithmeticUtil.doSub(dealNow.getTradeAmt(), varYsfghAmt);

                // 实收金额 <- 交易明细.交易金额
                accountAmountMap.put(资金账户金额元素,dealNow.getTradeAmt());
                // 分红金额 <- 变量.应收红利科目余额
                accountAmountMap.put(BookConstant.BONUSAMT,varYsfghAmt);
                // 分红金额 <- 变量.应收红利科目余额
                if (BigDecimal.ZERO.compareTo(varYcAmt) < 0) {
                    // 扎差金额(贷) = 变量.扎差金额
                    accountAmountMap.put(BookConstant.NETAMT_C,varYcAmt);
                }
                else {
                    // 扎差金额（借） = 变量.扎差金额*-1
                    accountAmountMap.put(BookConstant.NETAMT_D,varYcAmt.negate());
                }

                /**20200102 taoran add start**/
                // 定义 本币金额元素Map
                Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
                for(String key : accountAmountMap.keySet()){
                	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
                }
                accountAmountMap.putAll(bccyAmountMap);
                
            	//定义 变量.轧差金额_本位币 = 实收金额_本位币 - 分红金额_本位币
                BigDecimal varYcAmtBccy = ArithmeticUtil.doSub(accountAmountMap.get(资金账户金额元素 + BookConstant.BCCYAMT),
                		accountAmountMap.get(BookConstant.BONUSAMT + BookConstant.BCCYAMT));
            	if(varYcAmtBccy.compareTo(BigDecimal.ZERO)>0) {
            		accountAmountMap.put(BookConstant.NETAMT_C + BookConstant.BCCYAMT, ArithmeticUtil.round(varYcAmtBccy, NumberEnum.MONEY.getCode(), null));
            	}
            	else {
            		accountAmountMap.put(BookConstant.NETAMT_D + BookConstant.BCCYAMT, ArithmeticUtil.round(varYcAmtBccy.negate(), NumberEnum.MONEY.getCode(), null));
            	}
                /**20200102 taoran add end**/
            	
                // ********** 全局变量更新**********
                // 变量.应收红利科目余额 = 变量.应收红利科目余额 - 交易明细.应收分红
                varYshlSubjectAmt = ArithmeticUtil.doSub(varYshlSubjectAmt, varYsfghAmt);

            }
            else if (DictCodeList.交易方向_红利再投.getValue().equals(dealPsNow)) {

                // ********** 共通元素设值 - 设置账务引擎参数**********
                ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
                ad.setTableId(DictCodeListTableId.会计场景代码_非货币基金及净值型资产_红利再投.getValue());//

                BigDecimal varLxtzSubjectAmt = BigDecimal.ZERO;// 变量.利息调整

                // ********** accountSceneMap设置如下，共通**********
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                    "非货币基金" + DictCodeList.交易方向_红利再投.getCodeName() + "交易登账");

                // ********** accountAmountMap设置如下**********
                // 利息金额 <-交易明细.交易金额
//                // 定义 变量.利息调整 = 交易明细.交易本金 - 变量.应收红利科目余额
//                varLxtzSubjectAmt = ArithmeticUtil.doSub(dealNow.getPrinAmt(), varYshlSubjectAmt);
                // 成本金额<-交易明细.交易本金
                accountAmountMap.put(BookConstant.DEALAMT,dealNow.getPrinAmt());
                // 利息金额<-变量.应收红利科目余额
                accountAmountMap.put(BookConstant.BONUSAMT,dealNow.getPrinAmt());
//                // 利息金额<-变量.应收红利科目余额
//                accountAmountMap.put(BookConstant.BONUSAMT,varYshlSubjectAmt);
//                // 如果变量.利息调整 > 0
//                if (BigDecimal.ZERO.compareTo(varLxtzSubjectAmt) < 0) {
//                    // 利息调整（贷）<- 变量.利息调整DSCINC_C
//                    accountAmountMap.put(BookConstant.NETAMT_C,varLxtzSubjectAmt);
//                }
//                else {
//                    // 利息调整（借） <- 变量.利息调整*-1 DSCINC_D
//                    accountAmountMap.put(BookConstant.NETAMT_D,varLxtzSubjectAmt.negate());
//                }
                
                /**20200102 taoran add start**/
                // 定义 本币金额元素Map
                Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
                for(String key : accountAmountMap.keySet()){
                	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
                }
                accountAmountMap.putAll(bccyAmountMap);
                
//            	//定义 变量.利息调整_本位币 = 成本金额_本位币 - 利息金额_本位币
//                BigDecimal varLxtzSubjectAmtBccy = ArithmeticUtil.doSub(accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT),
//                		accountAmountMap.get(BookConstant.BONUSAMT + BookConstant.BCCYAMT));
//            	if(varLxtzSubjectAmtBccy.compareTo(BigDecimal.ZERO)>0) {
//            		//利息调整（贷）_本位币<-变量.利息调整_本位币
//            		accountAmountMap.put(BookConstant.NETAMT_C + BookConstant.BCCYAMT, ArithmeticUtil.round(varLxtzSubjectAmtBccy, NumberEnum.MONEY.getCode(), null));
//            	}
//            	else {
//            		//利息调整（借）_本位币<-变量.利息调整_本位币 * -1
//            		accountAmountMap.put(BookConstant.NETAMT_D + BookConstant.BCCYAMT, ArithmeticUtil.round(varLxtzSubjectAmtBccy.negate(), NumberEnum.MONEY.getCode(), null));
//            	}
//                /**20200102 taoran add end**/
            	
                // ********** 全局变量更新**********
                // 变量.成本科目余额=变量.成本科目余额 + 交易明细.交易本金
                varCostSubjectAmt = ArithmeticUtil.doSub(varCostSubjectAmt, dealNow.getPrinAmt());
                // 变量.成本科目数量=变量.成本科目数量 + 交易明细.交易份额
                varCostSubjectNum = ArithmeticUtil.doSub(varCostSubjectNum, dealNow.getShareAmt());
                // 变量.应收红利科目余额=变量.应收红利科目余额 - (交易明细.利息总额 - 变量.利息调整)
                varYshlSubjectAmt = ArithmeticUtil.doSub(varYshlSubjectAmt, dealNow.getPrinAmt());

            }
            else if (DictCodeList.交易方向_卖出.getValue().equals(dealPsNow)) {

                // ********** 共通元素设值 - 设置账务引擎参数**********
                ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
                ad.setTableId(DictCodeListTableId.会计场景代码_非货币基金及净值型资产_转仓卖出.getValue());//

                BigDecimal varCjgyjzbdSubjectAmt = BigDecimal.ZERO; // 变量.冲减公允价值变动
                BigDecimal varCjcbSubjectAmt = BigDecimal.ZERO;// 变量.冲减成本
                BigDecimal varCjslSubjectNum = BigDecimal.ZERO;// 变量.冲减数量

                // ********** accountSceneMap设置如下，共通**********
                accountSceneMap.put(BookConstant.BOOK_REMARK,"非货币基金" + DictCodeList.交易方向_卖出.getCodeName() + "交易登账");

                // ********** accountAmountMap设置如下**********
                // 调用 共通【交易各类费用处理】
                DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
                        dealNow.getValTradeFeeDetailList(), dealNow.getProdId(), dealNow.getPortfolioId());
                // 变量.交易各种费用 = 上述返回值
                // 佣金	<- 变量.交易各种费用.随交易支付_佣金
                accountAmountMap.put(BookConstant.COMMISSION_OUT,dealTypesFeeDto.getCommissionForDealPay());
                // 应付佣金 <- 变量.交易各种费用.佣金
                accountAmountMap.put(BookConstant.COMMISSION,dealTypesFeeDto.getCommission());
                // 交易费用 <- 变量.交易各种费用.随交易支付_其他费用
                accountAmountMap.put(BookConstant.FEEAMT,dealTypesFeeDto.getOtherFeeForDealPay());
                // 变量.冲减公允价值变动= 变量.公允价值变动科目余额 * 交易明细.交易份额/变量.成本科目数量
                varCjgyjzbdSubjectAmt = ArithmeticUtil.multiply(varGyjzxbdSubjectAmt,(ArithmeticUtil.divide(dealNow.getShareAmt(), varCostSubjectNum)));
                // 调用金额共通方法设置有效小数位
                varCjgyjzbdSubjectAmt = ArithmeticUtil.round(varCjgyjzbdSubjectAmt, NumberEnum.MONEY);
                
                // 变量.冲减成本=变量.成本科目余额 * 交易明细.交易份额/变量.成本科目数量
                varCjcbSubjectAmt = ArithmeticUtil.multiply(varCostSubjectAmt,(ArithmeticUtil.divide(dealNow.getShareAmt(), varCostSubjectNum)));
                // 调用金额共通方法设置有效小数位
                varCjcbSubjectAmt = ArithmeticUtil.round(varCjcbSubjectAmt, NumberEnum.MONEY);
                
                // 变量.冲减数量=NVL（交易明细.交易份额，0）
                varCjslSubjectNum = dealNow.getShareAmt() == null ? BigDecimal.ZERO : dealNow.getShareAmt();
                // 调用金额共通方法设置有效小数位
                varCjslSubjectNum = ArithmeticUtil.round(varCjslSubjectNum, NumberEnum.MONEY);

                // 实收金额<- 交易明细.交易金额
                accountAmountMap.put(BookConstant.DEALAMT,dealNow.getTradeAmt());
                // 成本金额<- 变量.冲减成本
                accountAmountMap.put(BookConstant.DCOST_AMT,varCjcbSubjectAmt);

                if (BigDecimal.ZERO.compareTo(varCjgyjzbdSubjectAmt) < 0) {
                    // 公允价值变动_贷<- 变量.冲减公允价值变动 FLOAT_C
                    accountAmountMap.put(BookConstant.FLOAT_C,varCjgyjzbdSubjectAmt);
                    // 公允价值变动损益_借<- 变量.冲减公允价值变动 FLOAT_INGPL_D
                    accountAmountMap.put(BookConstant.FLOAT_INGPL_D,varCjgyjzbdSubjectAmt);
                }
                else {
                    // 公允价值变动_借<-变量.公允价值变动*-1 FLOAT_D
                    accountAmountMap.put(BookConstant.FLOAT_D,varCjgyjzbdSubjectAmt.negate());
                    // 公允价值变动损益_贷<-变量.公允价值变动*-1 FLOAT_INGPL_C
                    accountAmountMap.put(BookConstant.FLOAT_INGPL_C,varCjgyjzbdSubjectAmt.negate());
                }

                // 买卖价差<-交易明细.交易金额 - 变量.冲减成本 - 变量.冲减公允价值变动
                // BigDecimal carMmjc = ArithmeticUtil.doSub(dealNow.getTradeAmt(), varCjcbSubjectAmt);
                // 买卖价差     <-	交易明细.交易金额 + 变量.交易各种费用.随交易支付_其他费用 + 变量.交易各种费用.随交易支付_佣金
                // - 变量.交易各种费用.佣金 - 变量.冲减成本
                BigDecimal carMmjc = ArithmeticUtil.doSub(
                        ArithmeticUtil.doAdd(
                                ArithmeticUtil.doAdd(dealNow.getTradeAmt(),dealTypesFeeDto.getOtherFeeForDealPay()),
                                ArithmeticUtil.doSub(dealTypesFeeDto.getCommissionForDealPay(),dealTypesFeeDto.getCommission())
                        ),varCjcbSubjectAmt);
                // 发生数量	<-	变量.冲减数量
                accountAmountMap.put(BookConstant.ACCQUA,varCjslSubjectNum);
                if (carMmjc.compareTo(BigDecimal.ZERO) >= 0) {
                    // 买卖价差（贷） <-变量.买卖价差
                    accountAmountMap.put(BookConstant.DSCLOSS_C,carMmjc);
                }
                else {
                    // 买卖价差（借） <-变量.买卖价差*-1
                    accountAmountMap.put(BookConstant.DSCLOSS_D, ArithmeticUtil.multiply(carMmjc, new BigDecimal(-1)));
                }

                //accountSceneMap设置如下：
                /** add by yanya on 20200113 增加佣金账务明细代码 start**/
                //佣金账务明细	<-	变量.交易各种费用.佣金账务明细
                accountSceneMap.put(BookConstant.BOK_DETAIL_COMMISSION, dealTypesFeeDto.getCommissionCode());
                /** add by yanya on 20200113 增加佣金账务明细代码 end**/
                
                /**20200102 taoran add start**/
                // 定义 本币金额元素Map
                Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
                for(String key : accountAmountMap.keySet()){
                	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
                }
                accountAmountMap.putAll(bccyAmountMap);
            	//定义 本币_变量.买卖价差
               	BigDecimal varMmjcBccy = BigDecimal.ZERO;
            	//            	//冲减公允价值变动 > 0
//            	if(BigDecimal.ZERO.compareTo(varCjgyjzbdSubjectAmt) < 0) {
            	//变量.买卖价差_本位币 = (实收金额_本位币 + 随交易支付_佣金_本位币 + 交易费用_本位币) -(成本金额_本位币 + 应付佣金_本位币)                                                                                                   
                BigDecimal dealAmtBccy = accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT);
                BigDecimal commissionOutBccy = accountAmountMap.get(BookConstant.COMMISSION_OUT + BookConstant.BCCYAMT);
                BigDecimal feeAmtBccy = accountAmountMap.get(BookConstant.FEEAMT + BookConstant.BCCYAMT);
                BigDecimal dcostAmtBccy = accountAmountMap.get(BookConstant.DCOST_AMT + BookConstant.BCCYAMT);
                BigDecimal commissionBccy = accountAmountMap.get(BookConstant.COMMISSION + BookConstant.BCCYAMT);
                varMmjcBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(dealAmtBccy, commissionOutBccy, feeAmtBccy)
                        , ArithmeticUtil.doAdd(dcostAmtBccy, commissionBccy));
//            	}
//            	else {
//            		//变量.买卖价差_本位币 =(本币_实收金额 + 本币_佣金 + 交易费用_本位币 + 公允价值变动_借_本位币)-(本币_本币_卖出份额 + 本币_应付佣金 + 公允价值变动损益_贷_本位币)
//            		BigDecimal dealAmtBccy = accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT);
//            		BigDecimal commissionOutBccy = accountAmountMap.get(BookConstant.COMMISSION_OUT + BookConstant.BCCYAMT);
//            		BigDecimal feeAmtBccy = accountAmountMap.get(BookConstant.FEEAMT + BookConstant.BCCYAMT);
//            		BigDecimal floatBccy = accountAmountMap.get(BookConstant.FLOAT_D + BookConstant.BCCYAMT);
//            		BigDecimal redeemQtyBccy = accountAmountMap.get(BookConstant.REDEEM_QTY + BookConstant.BCCYAMT);
//            		BigDecimal commissionBccy = accountAmountMap.get(BookConstant.COMMISSION + BookConstant.BCCYAMT);
//            		BigDecimal floatIngplBccy = accountAmountMap.get(BookConstant.FLOAT_INGPL_C + BookConstant.BCCYAMT);
//            		varMmjcBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(dealAmtBccy, commissionOutBccy, feeAmtBccy, floatBccy)
//                    		, ArithmeticUtil.doAdd(redeemQtyBccy, commissionBccy, floatIngplBccy));
//            	}
            	
				if(varMmjcBccy.compareTo(BigDecimal.ZERO)>0) {
            		//买卖价差_贷_本位币<-变量.买卖价差_本位币
            		accountAmountMap.put(BookConstant.DSCLOSS_C + BookConstant.BCCYAMT, ArithmeticUtil.round(varMmjcBccy, NumberEnum.MONEY.getCode(), null));
            	}
            	else {
            		accountAmountMap.put(BookConstant.DSCLOSS_D + BookConstant.BCCYAMT, ArithmeticUtil.round(varMmjcBccy.negate(), NumberEnum.MONEY.getCode(), null));
            	}
                /**20200102 taoran add end**/
                
                // ********** 全局变量更新**********
                // 变量.成本科目余额= 变量.成本科目余额 - 变量.冲减成本
                varCostSubjectAmt = ArithmeticUtil.doSub(varCostSubjectAmt, varCjcbSubjectAmt);
                // 变量.成本科目数量 = 变量.成本科目数量 - 变量.冲减数量
                varCostSubjectNum = ArithmeticUtil.doSub(varCostSubjectNum, varCjslSubjectNum);
                // 变量.公允价值变动科目余额 = 变量.公允价值变动科目余额 - 变量.冲减公允价值变动
                varGyjzxbdSubjectAmt = ArithmeticUtil.doSub(varGyjzxbdSubjectAmt,
                    varCjgyjzbdSubjectAmt);
            }

            // 记账金额元素币种信息塞值
            // 因为与记账金额元素map的key及数量一致，故采用循环
            for (String keyNow : accountAmountMap.keySet()) {
                accountCurrencyMap.put(keyNow, dealNow.getCcy());// value值塞入当前交易币种
            }
            
            // 本位币 <- 交易类账务处理结果.本位币
            accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
            
            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
            ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
            ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素
            // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 start
            ad.setSubjectMappingConfigDefault(dealNow.getSubjectMappingConfig());
            // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 end
            // 记账凭证生成
            BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            returnResult.getValBokBookdetailBatch().addAll(
                resultNowNow.getValBokBookdetailBatch());
        }

        return returnResult;
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
     * @param invAim 投资目的
     * @param taskPri 任务优先级
     * @return BigDecimal 科目余额
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalance(String booksetId, String bokDetailId, Date happenDate,
                                             Date bookDate, String expBalanceDirect,
                                             String baseSubjectNature, String invAim,
                                             Long taskPri) {

        // 定义返回的model
        SubjectBalance subjectBalance = new SubjectBalance();

        // 塞入要查询的基础科目性质
        List<String> baseSubjectNatureList = null;
        if (baseSubjectNature != null && baseSubjectNature.length() > 0) {
            baseSubjectNatureList = new ArrayList<String>();
            baseSubjectNatureList.add(baseSubjectNature);
        }

        // 塞入投资目的
        List<String> invAimList = null;
        if (invAim != null && invAim.length() > 0) {
            invAimList = new ArrayList<String>();
            invAimList.add(invAim);
        }

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId);
        searchDto.setBokDetailId(bokDetailId);
        searchDto.setHappenDate(happenDate);
        searchDto.setBookDate(bookDate);
        searchDto.setInvAimList(invAimList);
        searchDto.setTaskPri(taskPri);
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDto, expBalanceDirect);
        if (subjectBalanceList.size() > 0) {
            subjectBalance = subjectBalanceList.get(0);
        }

        return subjectBalance;
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
        // 资产类型
        accountSceneMap.put(BookConstant.ASSET_TYPE, dealNow.getFinprodType());
        
        // 发生数量->交易明细.交易份额
        accountSceneMap.put(BookConstant.HAPPEN_NUMBER, dealNow.getShareAmt());

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
        Map<TargetSearchDto, Object> targetMap = finProductCommonService.getTargetInfo(bokDetailId, targetSearchDtoList);
        TargetFinProduct targetFinProduct = (TargetFinProduct)targetMap.get(targetSearchDto);

        return targetFinProduct;
    }
    
    
}
