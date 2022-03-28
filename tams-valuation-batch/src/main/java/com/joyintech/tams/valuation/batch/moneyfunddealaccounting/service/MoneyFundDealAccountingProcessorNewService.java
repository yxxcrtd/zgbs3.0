/*
 * 文件名：MoneyFundDealAccountingProcessorService.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：估值核算系统V1.0 修改人：yangjian
 * 修改时间：2018年7月9日 修改内容：新建 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.moneyfunddealaccounting.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokBookdetailCurrentExt;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.BokStatDetPosiiton;
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
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
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
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;


/**
 * 货币基金交易-账务明细批处理 - process逻辑处理
 * 
 * @author yangjian
 * @version 1.0 2018年7月9日
 * @see MoneyFundDealAccountingProcessorNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class MoneyFundDealAccountingProcessorNewService {

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
     * 共通方法service
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 共通方法service
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

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
     * DBUtil
     */
    @Autowired
    private DBUtil dBUtil;

    /**
     * process 数据处理
     * 
     * @param param 传递参数
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

        // 定义变量
        BigDecimal glb_subjectAmt = BigDecimal.ZERO;// 变量.成本科目余额
        BigDecimal glb_subjectNum = BigDecimal.ZERO;// 变量.成本科目数量
        BigDecimal glb_subjectProfit = BigDecimal.ZERO;// 变量.应收红利科目余额
        BigDecimal glb_varGyjzxbdSubject = BigDecimal.ZERO;// 定义  变量.公允价值变动科目

        // 定义基础科目性质
//        String baseSubjectNatrue = DictCodeListExt1.金融产品类型_基金.getValue().equals(acctEntity.getListFinProductDealInfoComDTO().get(
//            0).getFinprodType()) ? DictCodeBaseSubjectNatrue.基金投资_成本.getValue() : DictCodeBaseSubjectNatrue.计划类_货币型资产_成本.getValue();
        String baseSubjectNatrue =DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue();
        // 调用公用方法：（科目余额查询 - 期待方向）,获取成本类科目余额
        SubjectBalance subjectBalanceForAmt = this.getSubjectBalance(booksetIdNow, bokDetailIdNow, happenDateNow, bookDateNow,
            DictCodeList.余额方向_借方.getValue(), baseSubjectNatrue, taskPri);
        if (subjectBalanceForAmt != null) {
            glb_subjectAmt = subjectBalanceForAmt.getBalance() == null ? BigDecimal.ZERO : subjectBalanceForAmt.getBalance();// 赋值.成本科目余额
            glb_subjectNum = subjectBalanceForAmt.getNum() == null ? BigDecimal.ZERO : subjectBalanceForAmt.getNum();// 赋值.成本科目数量
        }

        // 调用公用方法：（科目余额查询 - 期待方向）,获取应收红利科目余额
        SubjectBalance subjectBalanceForProfit = this.getSubjectBalance(booksetIdNow, bokDetailIdNow, happenDateNow, bookDateNow,
            DictCodeList.余额方向_借方.getValue(), DictCodeBaseSubjectNatrueNew.应收股利.getValue(), taskPri);
        if (subjectBalanceForProfit != null) {
            glb_subjectProfit = subjectBalanceForProfit.getBalance() == null ? BigDecimal.ZERO : subjectBalanceForProfit.getBalance();// 赋值.应计利息科目余额
        }
        // 公允价值变动科目余额
        // 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
        SubjectBalance varGyjzxbdSubjectAmt = this.getSubjectBalance(booksetIdNow, bokDetailIdNow, happenDateNow, bookDateNow,
                DictCodeList.余额方向_借方.getValue(), DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue(), taskPri);
        if(varGyjzxbdSubjectAmt != null){
            glb_varGyjzxbdSubject = varGyjzxbdSubjectAmt.getBalance() == null ?  BigDecimal.ZERO : varGyjzxbdSubjectAmt.getBalance();// 赋值  变量.公允价值变动科目
        }

        // 获取金融产品代码（同账务明细代码下对应的金融产品类型是一样的，故金融产品类型取交易明细中的第一个，避免循环内重复查询，降低效率。）
        String finprodIdFirst = listFinProductDealInfoComDTONow.get(0).getFinprodId();

        // 获取标的信息.金融产品类型
        TargetFinProduct targetFinProduct = this.getTargetFinProduct(finprodIdFirst);

        // 获取标的信息.境内外
        String blnAreaNow = targetFinProduct.getBlnArea();
        // 交易信息.分类1
        String type1Now = targetFinProduct.getType1();
        // 交易信息.分类2
        String type2Now = targetFinProduct.getType2();

        // 循环 交易基础信息列表 进行逻辑处理
        for (FinProductDealInfoComDTO dealDto : listFinProductDealInfoComDTONow) {

            // 组装记账元素
            // 初始化定义 - 记账场景要素管理
            AccountDriver ad = new AccountDriver();
            // 初始化定义 - 存放该笔记账的场景要素 map定义
            Map<String, Object> accountSceneMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
            Map<String, Object> accountCurrencyMap = new HashMap<>();

            // 设置账务引擎参数
            ad.setTreeNode(bookSetTreeNodeNow);// 塞入记账场景树节点 = 会计场景
            // ad.setTableId(this.getTableId(dealDto.getPs(), dealDto.getFinprodType()));//
            // ad.会计场景代码 = 根据交易方向和金融产品类型区分

            // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值)
            accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealDto);
            accountSceneMap.put(BookConstant.BLN_AREA, blnAreaNow);// 境内外
            accountSceneMap.put(BookConstant.TYPE_1, type1Now);// 分类1 - 变量.交易信息.分类1
            accountSceneMap.put(BookConstant.TYPE_2, type2Now);// 分类2 - 变量.交易信息.分类2
            
            /**add by taoran 20191231 start**/
            // 汇率
            BigDecimal changeRate = BigDecimal.ONE;
            if(StringUtil.isNotEmpty(dealDto.getCcy()) && !dealDto.getCcy().equals(acctEntity.getStandardCcy())){
                // 调用共通汇率查询
            	//changeRate = calculationCommonService.queryExchangeRateForTrade(dealDto.getCcy(), acctEntity.getStandardCcy(), acctEntity.getHappenDate());
                /** modify by yanya on 20200108 汇率查询方法变更调用标的类共通汇率查询方法 start**/
                List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(bookDateNow, booksetIdNow,
                        acctEntity.getStandardCcy(), dealDto.getCcy());
                if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                    changeRate = currencyMarketList.get(0).getClosePrice();
                }
                /** modify by yanya on 20200108 汇率查询方法变更调用标的类共通汇率查询方法 end**/
            }
            /**add by taoran 20191231 end**/

            // add by weipeng on 20220317：增加资金账户金额元素区分不同资金账户
            String 资金账户金额元素 = BookConstant.DEALAMT_HQ ; //默认托管户
            if(DictCodeListExt1.资金账户类型_结算备付金账户.getValue().equals(dealDto.getAcctType())
               || DictCodeListExt1.资金账户类型_券商资金账户.getValue().equals(dealDto.getAcctType())){
                资金账户金额元素 = BookConstant.DEALAMT_JS ;
            }

            // 根据交易方向，设置不同的记账元素
            if (StringUtil.equals(dealDto.getPs(), DictCodeList.交易方向_认购确认.getValue())) {
                // 会计场景代码
                ad.setTableId(DictCodeListTableId.会计场景代码_货币基金及货币型资产_认购确认.getValue());
                accountSceneMap.put(BookConstant.BOOK_REMARK, DictCodeListTableId.会计场景代码_货币基金及货币型资产_认购确认.getCodeName() + "登账");// 备注
                
                // 调用 共通【交易各类费用处理】
                DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
                        dealDto.getValTradeFeeDetailList(), dealDto.getProdId(), dealDto.getPortfolioId());
                // 变量.交易各种费用 = 上述返回值
                // 佣金  <-	变量.交易各种费用.随交易支付_佣金
                accountAmountMap.put(BookConstant.COMMISSION_OUT,dealTypesFeeDto.getCommissionForDealPay());
                // 应付佣金  <-	变量.交易各种费用.佣金
                accountAmountMap.put(BookConstant.COMMISSION,dealTypesFeeDto.getCommission());
                // 交易费用	<-	变量.交易各种费用.随交易支付_认购费
                accountAmountMap.put(BookConstant.FEEAMT,dealTypesFeeDto.getOfferFeeForDealPay());
                
                /**MODIFY BY WUJIE ON 20191204 START
                // 确认金额+利息金额	 <-	交易明细.交易本金 + 交易明细.利息总额
                accountAmountMap.put(BookConstant.DEALAMT, ArithmeticUtil.doAdd(dealDto.getPrinAmt(),dealDto.getIntAmt()));
                **/
                // 确认金额+利息金额	 <-	交易明细.交易本金 
                accountAmountMap.put(BookConstant.DEALAMT, dealDto.getPrinAmt());
                /**MODIFY BY WUJIE ON 20191204 END**/
                
                // 发生数量  <-	交易明细.交易份额
                accountAmountMap.put(BookConstant.ACCQUA,dealDto.getShareAmt());

                accountAmountMap.put("COST", dealDto.getTradeAmt());// 确认金额 - 交易明细.交易金额
                
                BigDecimal intAmt = BigDecimal.ZERO;
//                利息收入 = 交易明细.交易本金 + 变量.交易各种费用.随交易支付_佣金 + 变量.交易各种费用.随交易支付_认购费																			
//                - 交易明细.交易金额-变量.交易各种费用.佣金		
                intAmt = ArithmeticUtil.doSub(
                			ArithmeticUtil.doAdd(dealDto.getPrinAmt(),
                					dealTypesFeeDto.getCommissionForDealPay(),
                					dealTypesFeeDto.getOfferFeeForDealPay()),
                			dealDto.getTradeAmt(),
                			dealTypesFeeDto.getCommission());
                // 记账金额要素 map塞值
                if(BigDecimal.ZERO.compareTo(intAmt)!=0){
                    BokDetailId bokDetailId = fundCommonService.accountDetailProcessing(dealDto.getTradeId(),booksetIdNow);
                    if(bokDetailId!=null){
                    	 //账务明细代码2					<-		DTO.账务明细代码					
                        accountSceneMap.put(BookConstant.BOK_DETAIL_ID2, bokDetailId);
                        //资金账户类型				<-		DTO.账务明细代码.业务明细类型					
                        accountSceneMap.put(BookConstant.ACCT_TYPE, bokDetailId.getBusiDetailType());
                    }
                }
                accountAmountMap.put("INTAMT", intAmt);//变量.利息收入

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
                /**add by taoran 20191231 end**/
                
                // 确认金额_本位币  <-  (确认金额+利息金额)_本位币 + 本币_佣金 + 交易费用_本位币 - 利息收入_本位币 - 应付佣金_本位币        
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
                BigDecimal costBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(dealAmt_bccy, commissionOut_bccy, fee_bccy)
                    , intAmt_bccy, commission_bccy);
                accountAmountMap.put("COST" + BookConstant.BCCYAMT, ArithmeticUtil.round(costBccy, NumberEnum.MONEY.getCode(), null));
                /**add by taoran 20191231 end**/
                
                // 变量.成本科目余额 =	变量.成本科目余额 + 交易明细.交易本金 + 交易明细.利息总额
                glb_subjectAmt = ArithmeticUtil.doAdd(glb_subjectAmt,ArithmeticUtil.doAdd(dealDto.getPrinAmt(),dealDto.getIntAmt()));
                // 变量.成本科目数量 =	变量.成本科目数量 + 交易明细.交易份额
                glb_subjectNum = ArithmeticUtil.doAdd(glb_subjectNum,dealDto.getShareAmt());

            }
            else if (StringUtil.equals(dealDto.getPs(), DictCodeList.交易方向_申购确认.getValue())) {
                // // 会计场景代码
                // ad.setTableId(DictCodeListTableId.会计场景代码_货币基金及货币型资产_申购确认.getValue());
                if (DictCodeListExt1.金融产品类型_基金.getValue().equals(dealDto.getFinprodType())) {
                    if(DictCodeListExt1.交易场所_OTC场外.getValue().equals(dealDto.getTradeMarket())){
                        ad.setTableId(DictCodeListTableId.会计场景代码_货币基金及货币型资产_申购确认.getValue());
                    } else {
                        ad.setTableId(DictCodeListTableId.会计场景代码_货币基金_场内申购确认.getValue());
                    }
//                    ad.setTableId(DictCodeListTableId.会计场景代码_货币基金及货币型资产_申购确认.getValue());
                    accountSceneMap.put(BookConstant.BOOK_REMARK, DictCodeList.交易方向_申购确认.getCodeName() + "登账");// 备注

                    // ********** accountAmountMap设置如下**********
                    // 调用 共通【交易各类费用处理】
                    DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
                            dealDto.getValTradeFeeDetailList(), dealDto.getProdId(), dealDto.getPortfolioId());
                    // 变量.交易各种费用 = 上述返回值
                    // 佣金	  <-  变量.交易各种费用.随交易支付_佣金
                    accountAmountMap.put(BookConstant.COMMISSION_OUT,dealTypesFeeDto.getCommissionForDealPay());
                    // 应付佣金	<-	变量.交易各种费用.佣金
                    accountAmountMap.put(BookConstant.COMMISSION,dealTypesFeeDto.getCommission());
                    // 交易费用	<-	变量.交易各种费用.随交易支付_申购费
                    accountAmountMap.put(BookConstant.FEEAMT,dealTypesFeeDto.getSubscriptionFeeForDealPay());
                    // 记账金额要素 map塞值
                    // 发生数量 - 交易明细.交易份额
                    accountAmountMap.put("ACCQUA", dealDto.getShareAmt());
                    // 确认金额 - 交易明细.交易本金
                    accountAmountMap.put("COST", dealDto.getPrinAmt());
                    // 交易费用 - 交易明细.随交易支付费用
                   // accountAmountMap.put("FEEAMT", dealDto.getFeeAmtPay());
                    // 交易总额 - 交易明细.交易金额
                    accountAmountMap.put("DEALAMT", dealDto.getTradeAmt());

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
                    
                    // 交易总额_本位币	<-	确认金额_本位币 + 佣金_本位币 + 交易费用_本位币 - 应付佣金_本位币
                    //  变量.确认金额_本位币
                    BigDecimal cost_bccy = accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT);
                    //  变量.佣金_本位币
                    BigDecimal commissionOut_bccy = accountAmountMap.get(BookConstant.COMMISSION_OUT + BookConstant.BCCYAMT);
                    //  变量.交易费用_本位币
                    BigDecimal fee_bccy = accountAmountMap.get(BookConstant.FEEAMT + BookConstant.BCCYAMT);
                    //  变量.应付佣金_本位币  
                    BigDecimal commission_bccy = accountAmountMap.get(BookConstant.COMMISSION + BookConstant.BCCYAMT);
                    
                    // 变量.交易总额_本位币
                    BigDecimal dealAmtBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(cost_bccy, commissionOut_bccy, fee_bccy)
                        , commission_bccy);
                    accountAmountMap.put("DEALAMT" + BookConstant.BCCYAMT, ArithmeticUtil.round(dealAmtBccy, NumberEnum.MONEY.getCode(), null));
                    /**add by taoran 20191231 end**/
                    
                    // 全局变量的更新
                    // 变量.成本科目余额  =	变量.成本科目余额 + 交易明细.交易本金
                    glb_subjectAmt = ArithmeticUtil.doAdd(glb_subjectAmt, dealDto.getPrinAmt());
                    // 变量.成本科目数量	 =	变量.成本科目数量 + 交易明细.交易份额
                    glb_subjectNum = ArithmeticUtil.doAdd(glb_subjectNum, dealDto.getShareAmt());

                }
                else {
                    ad.setTableId(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_申购确认.getValue());
                    accountSceneMap.put(BookConstant.BOOK_REMARK, DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_申购确认.getCodeName() + "登账");// 备注

                    // 记账金额要素 map塞值
                    accountAmountMap.put(BookConstant.ACCQUA, dealDto.getShareAmt());// 发生数量 - 交易明细.交易份额
                    accountAmountMap.put(BookConstant.COST, dealDto.getPrinAmt());// 交易金额- 交易明细.交易本金
                    accountAmountMap.put(BookConstant.FEEAMT, dealDto.getFeeAmtPay());// 交易费用- 交易明细.随交易支付费用
                    accountAmountMap.put(BookConstant.DEALAMT, dealDto.getTradeAmt());// 交易总额- 交易明细.交易金额
                    
                    // 定义 本币金额元素Map
                    Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
                    for(String key : accountAmountMap.keySet()){
                        bccyAmountMap.put(key + BookConstant.BCCYAMT, 
                            calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
                    }
                    accountAmountMap.putAll(bccyAmountMap);
                    
                    // 交易总额_本位币 <-  确认金额_本位币 + 交易费用_本位币
                    //  变量.确认金额_本位币
                    BigDecimal cost_bccy = accountAmountMap.get(BookConstant.COST + BookConstant.BCCYAMT);
                    //  变量.交易费用_本位币
                    BigDecimal fee_bccy = accountAmountMap.get(BookConstant.FEEAMT + BookConstant.BCCYAMT);
                    // 变量.交易总额_本位币
                    BigDecimal dealAmtBccy = ArithmeticUtil.doAdd(NumberEnum.MONEY.getCode(), null, cost_bccy, fee_bccy);
                    accountAmountMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT, dealAmtBccy);
                    // 全局变量的更新
                    // 变量.成本科目余额  =	变量.成本科目余额 + 交易明细.交易本金
                    glb_subjectAmt = ArithmeticUtil.doAdd(glb_subjectAmt, dealDto.getPrinAmt());
                    // 变量.成本科目数量	 =	变量.成本科目数量 + 交易明细.交易份额
                    glb_subjectNum = ArithmeticUtil.doAdd(glb_subjectNum, dealDto.getShareAmt());

                }
            }
            else if (StringUtil.equals(dealDto.getPs(), DictCodeList.交易方向_赎回确认.getValue())) {
                // // 会计场景代码
                // ad.setTableId(DictCodeListTableId.会计场景代码_货币基金及货币型资产_赎回确认.getValue());

                if (DictCodeListExt1.金融产品类型_基金.getValue().equals(dealDto.getFinprodType())) {
                    if(DictCodeListExt1.交易场所_OTC场外.getValue().equals(dealDto.getTradeMarket())){
                        ad.setTableId(DictCodeListTableId.会计场景代码_货币基金及货币型资产_赎回确认.getValue());
                        accountSceneMap.put(BookConstant.BOOK_REMARK, DictCodeListTableId.会计场景代码_货币基金及货币型资产_赎回确认.getCodeName() + "登账");// 备注
                    } else {
                        ad.setTableId(DictCodeListTableId.会计场景代码_货币基金_场内赎回确认.getValue());
                        accountSceneMap.put(BookConstant.BOOK_REMARK,DictCodeListTableId.会计场景代码_货币基金_场内赎回确认.getCodeName() + "登账");// 备注
                    }

                    BigDecimal varCjgyjzbdSubjectAmt = BigDecimal.ZERO; // 变量.冲减公允价值变动
                    BigDecimal varCjcbSubjectAmt = BigDecimal.ZERO;// 变量.冲减成本
                    BigDecimal varCjslSubjectNum = BigDecimal.ZERO;// 变量.冲减数量
                    BigDecimal varCjyslxSubjectAmt = BigDecimal.ZERO;// 变量.冲减应收利息
                    BigDecimal varLxtzSubjectAmt = BigDecimal.ZERO;// 变量.利息调整

                    // accountAmountMap设置如下：
                    // 调用 共通【交易各类费用处理】
                    DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
                            dealDto.getValTradeFeeDetailList(), dealDto.getProdId(), dealDto.getPortfolioId());
                    // 变量.交易各种费用 = 上述返回值
                    // 佣金	<-		变量.交易各种费用.随交易支付_佣金
                    accountAmountMap.put(BookConstant.COMMISSION_OUT,dealTypesFeeDto.getCommissionForDealPay());
                    // 应付佣金  <-		变量.交易各种费用.佣金
                    accountAmountMap.put(BookConstant.COMMISSION,dealTypesFeeDto.getCommission());
                    // 赎回金额  <-		交易明细.交易金额
                    accountAmountMap.put(BookConstant.DEALAMT,dealDto.getTradeAmt());
                    // 交易费用  <-		变量.交易各种费用.随交易支付_赎回费
                    accountAmountMap.put(BookConstant.FEEAMT,dealTypesFeeDto.getRedemptionFeeForDealPay());
                    // 变量.冲减公允价值变动= 变量.公允价值变动科目余额 * 交易明细.交易份额/变量.成本科目数量
                    varCjgyjzbdSubjectAmt = ArithmeticUtil.multiply(glb_varGyjzxbdSubject,(ArithmeticUtil.divide(dealDto.getShareAmt(),glb_subjectNum)));
                    //调用金额共通方法设置有效小数位
                    varCjgyjzbdSubjectAmt = ArithmeticUtil.round(varCjgyjzbdSubjectAmt, NumberEnum.MONEY);

                    // 变量.冲减成本=变量.成本科目余额 * 交易明细.交易份额/变量.成本科目数量
                    varCjcbSubjectAmt = ArithmeticUtil.multiply(glb_subjectAmt,(ArithmeticUtil.divide(dealDto.getShareAmt(), glb_subjectNum)));
                    //调用金额共通方法设置有效小数位
                    varCjcbSubjectAmt = ArithmeticUtil.round(varCjcbSubjectAmt, NumberEnum.MONEY);

                    // 变量.冲减数量=NVL（交易明细.交易份额，0）
                    varCjslSubjectNum = dealDto.getShareAmt() == null ? BigDecimal.ZERO : dealDto.getShareAmt();
                    //调用金额共通方法设置有效小数位
                    varCjslSubjectNum = ArithmeticUtil.round(varCjslSubjectNum, NumberEnum.MONEY);
                    // IF 变量.成本科目余额 - 变量.冲减成本 = 0
                    if(ArithmeticUtil.round(glb_subjectAmt, NumberEnum.MONEY).equals(varCjcbSubjectAmt)){
                        // 变量.冲减应收利息 = 变量.应收红利科目余额
                        varCjyslxSubjectAmt = glb_subjectProfit;
                    } else {
                        // 变量.冲减应收利息 = 交易明细.利息总额
                        varCjyslxSubjectAmt = dealDto.getIntAmt();
                    }
                    // 变量.利息调整 = 交易明细.利息总额 - 变量.冲减应收利息
                    varLxtzSubjectAmt = ArithmeticUtil.doSub(dealDto.getIntAmt(),varCjyslxSubjectAmt);
                    // 发生数量	<-	变量.冲减数量
                    accountAmountMap.put(BookConstant.ACCQUA,varCjslSubjectNum);
                    // 赎回份额  <-  变量.冲减成本
                    accountAmountMap.put(BookConstant.REDEEM_QTY,varCjcbSubjectAmt);
                    // 冲销的应收红利  <-  变量.冲减应收利息
                    accountAmountMap.put(BookConstant.INTAMT,varCjyslxSubjectAmt);

                    // IF 变量.利息调整 > 0
                    if(varLxtzSubjectAmt.compareTo(BigDecimal.ZERO) > 0){
                        accountAmountMap.put(BookConstant.INCOMEAMT_C,varLxtzSubjectAmt);
                    } else {
                        accountAmountMap.put(BookConstant.INCOMEAMT_D,varLxtzSubjectAmt.negate());
                    }
                    // IF 变量.冲减公允价值变动 > 0
                    if(BigDecimal.ZERO.compareTo(varCjgyjzbdSubjectAmt) < 0){
                        // 公允价值变动_贷<- 变量.冲减公允价值变动 FLOAT_C
                        accountAmountMap.put(BookConstant.FLOAT_C,varCjgyjzbdSubjectAmt);
                        // 公允价值变动损益_借<- 变量.冲减公允价值变动 FLOAT_INGPL_D
                        accountAmountMap.put(BookConstant.FLOAT_INGPL_D,varCjgyjzbdSubjectAmt);
                    } else {
                        // 公允价值变动_借<-变量.公允价值变动*-1 FLOAT_D
                        accountAmountMap.put(BookConstant.FLOAT_D,varCjgyjzbdSubjectAmt.negate());
                        // 公允价值变动损益_贷<-变量.公允价值变动*-1 FLOAT_INGPL_C
                        accountAmountMap.put(BookConstant.FLOAT_INGPL_C,varCjgyjzbdSubjectAmt.negate());
                    }
                    // 定义 变量.买卖价差 = 交易明细.交易金额 + 变量.交易各种费用.随交易支付_佣金 + 变量.交易各种费用.随交易支付_赎回费
                        // -变量.冲减成本- 变量.交易各种费用.佣金 - 交易明细.利息总额
                    BigDecimal varMmjc =  ArithmeticUtil.doSub(
                            ArithmeticUtil.doAdd(dealDto.getTradeAmt(),dealTypesFeeDto.getCommissionForDealPay(),dealTypesFeeDto.getRedemptionFeeForDealPay()
                            ),ArithmeticUtil.doAdd(dealTypesFeeDto.getCommission(),varCjcbSubjectAmt,dealDto.getIntAmt()));
                    // IF 变量.买卖价差 > 0
                    if (varMmjc.compareTo(BigDecimal.ZERO) > 0) {
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

                    /**add by taoran 20191231 start**/
                    // 定义 本币金额元素Map
                    Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
                    for(String key : accountAmountMap.keySet()){
                    	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
                    }
                    accountAmountMap.putAll(bccyAmountMap);
                    
                    if(DictCodeListExt1.交易场所_OTC场外.getValue().equals(dealDto.getTradeMarket())){
                    	 //变量.利息调整 > 0
                    	if(varLxtzSubjectAmt.compareTo(BigDecimal.ZERO) > 0){
                            //赎回金额_本位币<-	赎回份额_本位币 + 冲销的应收红利_本位币 + 利息调整_贷_本位币 - 交易费用_本位币
                    		//  变量.赎回份额_本位币
                    		BigDecimal redeemQtyBccy = accountAmountMap.get(BookConstant.REDEEM_QTY + BookConstant.BCCYAMT);
                    		//  变量.冲销的应收红利_本位币
                    		BigDecimal intAmtBccy = accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT);
                    		/**MODIFY BY WUJIE ON 20200519 START
                    		//  变量.利息调整_贷_本位币
                    		BigDecimal dsclosscBccy = accountAmountMap.get(BookConstant.DSCLOSS_C + BookConstant.BCCYAMT);
                    		**/
                    		//  变量.利息调整_贷_本位币
                    		BigDecimal dsclosscBccy = accountAmountMap.get(BookConstant.INCOMEAMT_C + BookConstant.BCCYAMT);
                    		/**MODIFY BY WUIE ON 20200519 END**/
                    		
                    		//  变量.交易费用_本位币
                    		BigDecimal feeAmtBccy = accountAmountMap.get(BookConstant.FEEAMT + BookConstant.BCCYAMT);
                    		
                    		// 赎回金额_本位币
                            BigDecimal dealAmtBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(redeemQtyBccy, intAmtBccy, dsclosscBccy)
            							, feeAmtBccy);
                            
                            accountAmountMap.put("DEALAMT" + BookConstant.BCCYAMT, ArithmeticUtil.round(dealAmtBccy, NumberEnum.MONEY.getCode(), null));
                        } else {
                        	//赎回金额_本位币<-	赎回份额_本位币 + 冲销的应收红利_本位币 - 本币_利息调整_借 - 交易费用_本位币																								
                        	//  变量.赎回份额_本位币
                    		BigDecimal redeemQtyBccy = accountAmountMap.get(BookConstant.REDEEM_QTY + BookConstant.BCCYAMT);
                    		//  变量.冲销的应收红利_本位币
                    		BigDecimal intAmtBccy = accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT);
                    		/**MODIFY BY WUIE ON 20200519 START
                    		//  变量.利息调整_借 _本位币
                    		BigDecimal dsclossdBccy = accountAmountMap.get(BookConstant.DSCLOSS_D + BookConstant.BCCYAMT);
                    		**/
                    		//  变量.利息调整_借 _本位币
                    		BigDecimal dsclossdBccy = accountAmountMap.get(BookConstant.INCOMEAMT_D + BookConstant.BCCYAMT);
                    		/**MODIFY BY WUIE ON 20200519 END**/
                    		
                    		//  变量.交易费用_本位币
                    		BigDecimal feeAmtBccy = accountAmountMap.get(BookConstant.FEEAMT + BookConstant.BCCYAMT);
                    		
                    		// 赎回金额_本位币
                            BigDecimal dealAmtBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(redeemQtyBccy, intAmtBccy),dsclossdBccy
            							, feeAmtBccy);
                            
                            accountAmountMap.put("DEALAMT" + BookConstant.BCCYAMT, ArithmeticUtil.round(dealAmtBccy, NumberEnum.MONEY.getCode(), null));
                        }
                    } else {
                    	/**modify by wujie on 20201228 start
                    	BigDecimal varMmjcBccy;
                    	// 变量.冲减公允价值变动 > 0
                        if(BigDecimal.ZERO.compareTo(varCjgyjzbdSubjectAmt) < 0){
                        	//变量.买卖价差_本位币 =(赎回金额_本位币 + 佣金_本位币 + 交易费用_本位币 + 本币_公允价值变动_借)-(赎回份额_本位币 + 应付佣金_本位币 + 本币_公允价值变动_贷)	
                        	//  变量.赎回金额_本位币
                    		BigDecimal dealAmtBccy = accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT);
                        	//  变量.赎回份额_本位币
                    		BigDecimal redeemQtyBccy = accountAmountMap.get(BookConstant.REDEEM_QTY + BookConstant.BCCYAMT);
                    		//  变量.佣金_本位币 
                    		BigDecimal commissionOutBccy = accountAmountMap.get(BookConstant.COMMISSION_OUT + BookConstant.BCCYAMT);
                    		//  变量.应付佣金 _本位币 
                    		BigDecimal commissionBccy = accountAmountMap.get(BookConstant.COMMISSION + BookConstant.BCCYAMT);
                    		//  变量.交易费用_本位币
                    		BigDecimal feeAmtBccy = accountAmountMap.get(BookConstant.FEEAMT + BookConstant.BCCYAMT);
                    		//  变量.公允价值变动_借_本位币
                    		BigDecimal floatdBccy = accountAmountMap.get(BookConstant.FLOAT_D + BookConstant.BCCYAMT);
                    		//  变量.公允价值变动_贷_本位币
                    		BigDecimal floatcBccy = accountAmountMap.get(BookConstant.FLOAT_C + BookConstant.BCCYAMT);
                    		
                        	varMmjcBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(dealAmtBccy, commissionOutBccy, feeAmtBccy, floatdBccy)
        							, ArithmeticUtil.doAdd(redeemQtyBccy, commissionBccy, floatcBccy));
                        	
                        } else {
                        	//变量.买卖价差_本位币 =(赎回金额_本位币 + 佣金_本位币 + 交易费用_本位币 + 本币_公允价值变动_借)-(赎回份额_本位币 + 应付佣金_本位币 + 本币_公允价值变动损益_贷)	
                        	//  变量.赎回金额_本位币
                    		BigDecimal dealAmtBccy = accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT);
                        	//  变量.赎回份额_本位币
                    		BigDecimal redeemQtyBccy = accountAmountMap.get(BookConstant.REDEEM_QTY + BookConstant.BCCYAMT);
                    		//  变量.佣金_本位币 
                    		BigDecimal commissionOutBccy = accountAmountMap.get(BookConstant.COMMISSION_OUT + BookConstant.BCCYAMT);
                    		//  变量.应付佣金 _本位币 
                    		BigDecimal commissionBccy = accountAmountMap.get(BookConstant.COMMISSION + BookConstant.BCCYAMT);
                    		//  变量.交易费用_本位币
                    		BigDecimal feeAmtBccy = accountAmountMap.get(BookConstant.FEEAMT + BookConstant.BCCYAMT);
                    		//  变量.公允价值变动_借_本位币
                    		BigDecimal floatdBccy = accountAmountMap.get(BookConstant.FLOAT_D + BookConstant.BCCYAMT);
                    		//  变量.公允价值变动损益_贷_本位币
                    		BigDecimal floatIngplcBccy = accountAmountMap.get(BookConstant.FLOAT_INGPL_C + BookConstant.BCCYAMT);
                    		
                        	varMmjcBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(dealAmtBccy, commissionOutBccy, feeAmtBccy, floatdBccy)
        							, ArithmeticUtil.doAdd(redeemQtyBccy, commissionBccy, floatIngplcBccy));
                        }
                        **/
                    	//  变量.赎回金额_本位币
                		BigDecimal dealAmtBccy = accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT);
                    	//  变量.赎回份额_本位币
                		BigDecimal redeemQtyBccy = accountAmountMap.get(BookConstant.REDEEM_QTY + BookConstant.BCCYAMT);
                		// 变量.利息_本位币
                		BigDecimal intamtBccy = accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT);
                		//  变量.佣金_本位币 
                		BigDecimal commissionOutBccy = accountAmountMap.get(BookConstant.COMMISSION_OUT + BookConstant.BCCYAMT);
                		//  变量.应付佣金 _本位币 
                		BigDecimal commissionBccy = accountAmountMap.get(BookConstant.COMMISSION + BookConstant.BCCYAMT);
                		//  变量.交易费用_本位币
                		BigDecimal feeAmtBccy = accountAmountMap.get(BookConstant.FEEAMT + BookConstant.BCCYAMT);
                		// 变量.利息调整_借方
                		BigDecimal incomeamtdBccy = accountAmountMap.get(BookConstant.INCOMEAMT_D + BookConstant.BCCYAMT);
                		// 变量.利息调整_贷方
                		BigDecimal incomeamtcBccy = accountAmountMap.get(BookConstant.INCOMEAMT_C + BookConstant.BCCYAMT);
                		
                		//变量.买卖价差_本位币 =(赎回金额_本位币 + 佣金_本位币 + 交易费用_本位币 + 利息调整_本位币_借)-(赎回份额_本位币 + 应付佣金_本位币 + 利息金额_本位币 + 利息调整_本位币_贷)	
                		BigDecimal varMmjcBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(dealAmtBccy, commissionOutBccy, feeAmtBccy, incomeamtdBccy)
    							, ArithmeticUtil.doAdd(redeemQtyBccy, commissionBccy,intamtBccy, incomeamtcBccy));
                    	/**modify by wujie on 20201228 end**/
                        if (varMmjcBccy.compareTo(BigDecimal.ZERO) > 0) {
                            accountAmountMap.put(BookConstant.DSCLOSS_C + BookConstant.BCCYAMT, ArithmeticUtil.round(varMmjcBccy, NumberEnum.MONEY.getCode(), null));
                        }
                        else {
                            accountAmountMap.put(BookConstant.DSCLOSS_D + BookConstant.BCCYAMT, ArithmeticUtil.round(varMmjcBccy.negate(), NumberEnum.MONEY.getCode(), null));
                        }
                    }
                    
                    // 变量.成本科目余额 =	变量.成本科目余额 - 变量.冲减成本
                    glb_subjectAmt = ArithmeticUtil.doSub(glb_subjectAmt,varCjcbSubjectAmt);
                    // 变量.成本科目数量	=	变量.成本科目数量 - 变量.冲减数量
                    glb_subjectNum = ArithmeticUtil.doSub(glb_subjectNum,varCjslSubjectNum);
                    // 变量.公允价值变动科目余额 =	变量.公允价值变动科目余额 - 变量.冲减公允价值变动
                    glb_varGyjzxbdSubject = ArithmeticUtil.doSub(glb_varGyjzxbdSubject,varCjgyjzbdSubjectAmt);
                    // 变量.应收红利科目余额 =	变量.应收红利科目余额 - 变量.冲减应收利息
                    glb_subjectProfit = ArithmeticUtil.doSub(glb_subjectProfit,varCjyslxSubjectAmt);

                }
                else {
                    ad.setTableId(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_赎回到期.getValue());
                    accountSceneMap.put(BookConstant.BOOK_REMARK, DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_赎回到期.getCodeName() + "登账");// 备注


                // 记账金额要素 map塞值
                accountAmountMap.put("DEALAMT", dealDto.getTradeAmt());// 赎回金额 - 交易明细.交易金额
                accountAmountMap.put("REDEEM_QTY", dealDto.getPrinAmt());// 赎回份额 - 交易明细.交易本金
                accountAmountMap.put("ACCQUA", dealDto.getShareAmt());// 核算数量 - 交易明细.交易份额
                accountAmountMap.put("FEEAMT", dealDto.getFeeAmtPay());// 交易费用- 交易明细.随交易支付费用
                
                // 定义利息调整
                BigDecimal lxtz = BigDecimal.ZERO;
                // 变量.应收利息 = 0
                BigDecimal yslx = BigDecimal.ZERO;
                // 成本余额 = 变量.成本科目余额 - 交易明细.交易本金
                BigDecimal cbye = ArithmeticUtil.doSub(glb_subjectAmt, dealDto.getPrinAmt());
                // 若 成本余额 为 0
                if (cbye.compareTo(BigDecimal.ZERO) == 0) {
                    // 变量.应收利息 = 变量.应收红利科目余额
                    yslx = glb_subjectProfit;
                }
                else {
                    // 变量.应收利息 = 交易明细.利息总额
                    yslx = dealDto.getIntAmt();
                }
                accountAmountMap.put("INTAMT", yslx);// 冲销的应收红利 - 交易明细.利息总额
                /**20191231 taoran add start**/
                //赎回份额_本位币  <- 赎回份额 * 变量.汇率		
                accountAmountMap.put("REDEEM_QTY" + BookConstant.BCCYAMT
                		, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get("REDEEM_QTY"), changeRate));
                //交易费用_本位币  <- 交易费用 * 变量.汇率
                accountAmountMap.put("FEEAMT" + BookConstant.BCCYAMT
                		, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get("FEEAMT"), changeRate));
                //冲销的应收红利_本位币     <-	变量.应收利息 * 变量.汇率	
                accountAmountMap.put("INTAMT" + BookConstant.BCCYAMT
                		, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get("INTAMT"), changeRate));
                /**20191231 taoran add end**/
                // 变量.利息调整 = 交易明细.利息总额 - 变量.应收利息
                lxtz = ArithmeticUtil.doSub(dealDto.getIntAmt(), yslx);
                //赎回金额_本位币
                BigDecimal dealAmtBccy = BigDecimal.ZERO;
                // 根据利息调整 设置【应收红利和实收红利的差额（借）】和【应收红利和实收红利的差额（贷）】
                if (lxtz.compareTo(BigDecimal.ZERO) > 0) {
                    accountAmountMap.put("INCOMEAMT_C", lxtz);// 应收红利和实收红利的差额（贷）
                    // 应收红利和实收红利的差额（贷）_本币
                    accountAmountMap.put("INCOMEAMT_C" + BookConstant.BCCYAMT
                    		, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get("INCOMEAMT_C"), changeRate));
                    dealAmtBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(accountAmountMap.get("REDEEM_QTY" + BookConstant.BCCYAMT)
		                    		, accountAmountMap.get("INTAMT" + BookConstant.BCCYAMT)
		                    			, accountAmountMap.get("INCOMEAMT_C" + BookConstant.BCCYAMT))
		                    				, accountAmountMap.get("FEEAMT" + BookConstant.BCCYAMT));
                }
                else {
                    accountAmountMap.put("INCOMEAMT_D", lxtz.abs());// 应收红利和实收红利的差额（借）
                    // 应收红利和实收红利的差额（借）_本币
                    accountAmountMap.put("INCOMEAMT_D" + BookConstant.BCCYAMT
                    		, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get("INCOMEAMT_D"), changeRate));
                    dealAmtBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(accountAmountMap.get("REDEEM_QTY" + BookConstant.BCCYAMT)
		                    		, accountAmountMap.get("INTAMT" + BookConstant.BCCYAMT))
		                    			,accountAmountMap.get("INCOMEAMT_D" + BookConstant.BCCYAMT)
		                    				, accountAmountMap.get("FEEAMT" + BookConstant.BCCYAMT));
                }
                //赎回金额_本位币
                accountAmountMap.put("DEALAMT" + BookConstant.BCCYAMT
                		, ArithmeticUtil.round(dealAmtBccy, NumberEnum.MONEY.getCode(), null));
                // 全局变量的更新
                glb_subjectAmt = ArithmeticUtil.doSub(glb_subjectAmt, dealDto.getPrinAmt());// 变量.成本科目余额
                                                                                            // =
                                                                                            // 变量.成本科目余额
                                                                                            // -
                                                                                            // 交易明细.交易本金
                glb_subjectNum = ArithmeticUtil.doSub(glb_subjectNum, dealDto.getShareAmt());// 变量.成本科目数量
                                                                                             // =
                                                                                             // 变量.成本科目数量
                                                                                             // -
                                                                                             // 交易明细.交易份额
                }
            }
            /** modify by yanya on 20200111 货币基金增加转换交易处理，转出方向变更为转换赎回确认 start**/
            //else if (StringUtil.equals(dealDto.getPs(), DictCodeList.交易方向_转出.getValue())) {// 【转出，转入】和设计讨论后，已没有该类型，暂时使用【买入，卖出】的。
            else if (StringUtil.equals(dealDto.getPs(), DictCodeListExt3.交易方向_转换赎回确认.getValue())) {
//                // 会计场景代码
//                ad.setTableId(DictCodeListTableId.会计场景代码_货币基金及货币型资产_转仓卖出.getValue());
//                accountSceneMap.put(BookConstant.BOOK_REMARK, DictCodeListTableId.会计场景代码_货币基金及货币型资产_转仓卖出.getCodeName() + "登账");// 备注
//
//                // 记账金额要素 map塞值
//                accountAmountMap.put("DEALAMT", dealDto.getTradeAmt());// 实收金额 - 交易明细.交易金额
//                accountAmountMap.put("REDEEM_QTY", dealDto.getPrinAmt());// 转出本金  <-	 交易明细.交易本金
//                accountAmountMap.put("BONUSAMT", dealDto.getIntAmt());// 应收红利 -交易明细.利息总额
//                accountAmountMap.put("ACCQUA", dealDto.getShareAmt());// 核算数量 - 交易明细.交易份额
//
//                // 全局变量的更新
//                // 变量.成本科目余额   = 变量.成本科目余额 - 交易明细.交易本金
//                glb_subjectAmt = ArithmeticUtil.doSub(glb_subjectAmt, dealDto.getPrinAmt());
//                // 变量.成本科目数量 =  变量.成本科目数量 -交易明细.交易份额
//                glb_subjectNum = ArithmeticUtil.doSub(glb_subjectNum, dealDto.getShareAmt());
                //ad.会计场景代码="货币基金-基金转换出确认交易"
                ad.setTableId(DictCodeListTableId.会计场景代码_货币基金及货币型资产_基金转换出确认交易.getValue());
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
                DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(dealDto.getValTradeFeeDetailList(),
                        dealDto.getProdId(), dealDto.getPortfolioId());
                //佣金		<-	变量.交易各种费用.随交易支付_佣金
                accountAmountMap.put(BookConstant.COMMISSION_OUT, dealTypesFeeDto.getCommissionForDealPay());
                //应付佣金		<-		变量.交易各种费用.佣金
                accountAmountMap.put(BookConstant.COMMISSION, dealTypesFeeDto.getCommission());
                //转出金额		<-		交易明细.交易金额
                accountAmountMap.put(BookConstant.TRADE_QTY, dealDto.getTradeAmt());
                //交易费用		<-		变量.交易各种费用.随交易支付_赎回费
                accountAmountMap.put(BookConstant.REFEE_AMT, dealTypesFeeDto.getRedemptionFeeForDealPay());
                //定义 变量.冲减公允价值变动	=	变量.公允价值变动科目 * 交易明细.交易份额/变量.成本科目数量
                BigDecimal 冲减公允价值变动 = ArithmeticUtil.multiply(glb_varGyjzxbdSubject,
                        (ArithmeticUtil.divide(dealDto.getShareAmt(),glb_subjectNum)));
                冲减公允价值变动 = ArithmeticUtil.round(冲减公允价值变动, NumberEnum.MONEY);
                //定义 变量.冲减成本	=	变量.成本科目余额 * 交易明细.交易份额/变量.成本科目数量
                BigDecimal 冲减成本 = ArithmeticUtil.multiply(glb_subjectAmt,
                        (ArithmeticUtil.divide(dealDto.getShareAmt(), glb_subjectNum)));
                //调用金额共通方法设置有效小数位
                冲减成本 = ArithmeticUtil.round(冲减成本, NumberEnum.MONEY);
                //定义 变量.冲减数量	=	NVL（交易明细.交易份额，0）
                BigDecimal 冲减数量 = dealDto.getShareAmt() == null ? BigDecimal.ZERO : dealDto.getShareAmt();
                冲减数量 = ArithmeticUtil.round(冲减数量, NumberEnum.MONEY);
                //定义 变量.冲减应收利息 = 0
                BigDecimal 冲减应收利息 = BigDecimal.ZERO;
                //变量.利息调整 = 0
                BigDecimal 利息调整 = BigDecimal.ZERO;
                //IF 变量.成本科目余额 - 变量.冲减成本 = 0
                if(ArithmeticUtil.round(glb_subjectAmt, NumberEnum.MONEY).compareTo(冲减成本) == 0){
                    // 变量.冲减应收利息 = 变量.应收红利科目余额
                    冲减应收利息 = glb_subjectProfit;
                } else {
                    // 变量.冲减应收利息 = 交易明细.利息总额
                    冲减应收利息 = dealDto.getIntAmt();
                }
                //变量.利息调整 = 交易明细.利息总额 - 变量.冲减应收利息
                利息调整 = ArithmeticUtil.doSub(dealDto.getIntAmt(), 冲减应收利息);
                //发生数量	<-	变量.冲减数量
                accountAmountMap.put(BookConstant.ACCQUA, 冲减数量);
                //转出本金	<-		变量.冲减成本
                accountAmountMap.put(BookConstant.COST_OUT, 冲减成本);
                //冲销的应收红利		<-		变量.冲减应收利息
                accountAmountMap.put(BookConstant.INTAMT, 冲减应收利息);

                // IF 变量.利息调整 > 0
                if(利息调整.compareTo(BigDecimal.ZERO) > 0) {
                    //利息调整_贷	<-	变量.利息调整
                    accountAmountMap.put(BookConstant.BONUSNET_C, 利息调整);
                } else {
                    //利息调整_借 <- 变量.利息调整*-1
                    accountAmountMap.put(BookConstant.BONUSNET_D, ArithmeticUtil.multiply(利息调整, new BigDecimal(-1)));
                }
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
                定义 变量.买卖价差 = 交易明细.交易金额 + 变量.交易各种费用.随交易支付_佣金 + 变量.交易各种费用.随交易支付_赎回费
                        -变量.冲减成本- 变量.交易各种费用.佣金  - 交易明细.利息总额**/
                BigDecimal 买卖价差 = ArithmeticUtil.doSub(
                        ArithmeticUtil.doAdd(dealDto.getTradeAmt(),
                                dealTypesFeeDto.getCommissionForDealPay(), dealTypesFeeDto.getRedemptionFeeForDealPay()),
                        ArithmeticUtil.doAdd(冲减成本, dealTypesFeeDto.getCommission(), dealDto.getIntAmt()));
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
                //IF 数据字典：估值交易场所.OTC场外 eq 变量.标的信息.交易场所
                if(DictCodeListExt1.交易场所_OTC场外.getValue().equals(dealDto.getTradeMarket())) {
                    //IF 变量.利息调整 > 0
                    if(利息调整.compareTo(BigDecimal.ZERO) > 0) {
                        //转出金额_本位币	<-	转出本金_本位币 + 冲销的应收红利_本位币 + 利息调整_贷_本位币 - 交易费用_本位币
                        accountAmountMap.put(BookConstant.TRADE_QTY + BookConstant.BCCYAMT,
                                ArithmeticUtil.round(
                                        ArithmeticUtil.doSub(
                                                ArithmeticUtil.doAdd(
                                                        accountAmountMap.get(BookConstant.COST_OUT + BookConstant.BCCYAMT),
                                                        accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT),
                                                        accountAmountMap.get(BookConstant.BONUSNET_C + BookConstant.BCCYAMT)
                                                ),
                                                accountAmountMap.get(BookConstant.REFEE_AMT + BookConstant.BCCYAMT)
                                        ),
                                        NumberEnum.MONEY));
                    }
                    else {
                        //转出金额_本位币	<-	转出本金_本位币 + 冲销的应收红利_本位币 - 利息调整_借_本位币 - 交易费用_本位币
                        accountAmountMap.put(BookConstant.TRADE_QTY + BookConstant.BCCYAMT,
                                ArithmeticUtil.round(
                                        ArithmeticUtil.doSub(
                                                ArithmeticUtil.doAdd(
                                                        accountAmountMap.get(BookConstant.COST_OUT + BookConstant.BCCYAMT),
                                                        accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)
                                                ),
                                                ArithmeticUtil.doAdd(
                                                        accountAmountMap.get(BookConstant.REFEE_AMT + BookConstant.BCCYAMT),
                                                        accountAmountMap.get(BookConstant.BONUSNET_D + BookConstant.BCCYAMT)
                                                )
                                        ),
                                        NumberEnum.MONEY));
                    }
                }
                else {
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
                        accountAmountMap.put(BookConstant.NETAMT_D, 买卖价差本位币);
                    }
                    else {
                        //买卖价差_借_本位币	<-	变量.买卖价差_本位币 * -1
                        accountAmountMap.put(BookConstant.NETAMT_D, ArithmeticUtil.multiply(买卖价差本位币, new BigDecimal(-1)));
                    }
                }
                //本位币	<-		交易类账务处理结果.本位币
                accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
                //全局变量的更新
                //变量.成本科目余额  =	变量.成本科目余额 - 变量.冲减成本
                glb_subjectAmt = ArithmeticUtil.doSub(glb_subjectAmt, 冲减成本);
                // 变量.成本科目数量	=	变量.成本科目数量 - 变量.冲减数量
                glb_subjectNum = ArithmeticUtil.doSub(glb_subjectNum, 冲减数量);
                //变量.应收红利科目余额	=	变量.应收红利科目余额 - 变量.冲减应收利息
                glb_subjectProfit = ArithmeticUtil.doSub(glb_subjectProfit, 冲减应收利息);
                //变量.公允价值变动科目余额	=	变量.公允价值变动科目余额 - 变量.冲减公允价值变动
                glb_varGyjzxbdSubject = ArithmeticUtil.doSub(glb_subjectProfit, 冲减公允价值变动);
            }
            /** modify by yanya on 20200111 货币基金增加转换交易处理，转出方向变更为转换赎回确认 end**/
            /** modify by yanya on 20200111 货币基金增加转换交易处理，转入方向变更为转换申购确认 start**/
            //else if (StringUtil.equals(dealDto.getPs(), DictCodeList.交易方向_转入.getValue())) {// 【转出，转入】和设计讨论后，已没有该类型，暂时使用【买入，卖出】的。
            else if (StringUtil.equals(dealDto.getPs(), DictCodeListExt3.交易方向_转换申购确认.getValue())) {
//                // 会计场景代码
//                ad.setTableId(DictCodeListTableId.会计场景代码_货币基金及货币型资产_转仓买入.getValue());
//                accountSceneMap.put(BookConstant.BOOK_REMARK, DictCodeListTableId.会计场景代码_货币基金及货币型资产_转仓买入.getCodeName() + "登账");// 备注
//
//                // 记账金额要素 map塞值
//                accountAmountMap.put("TRADE_QTY", dealDto.getPrinAmt());// 交易份额 - 交易明细.交易本金
//                accountAmountMap.put("BONUSAMT", dealDto.getIntAmt());// 应收红利 -交易明细.利息总额
//                accountAmountMap.put("DEALAMT", dealDto.getTradeAmt());// 交易金额 - 交易明细.交易金额
//                accountAmountMap.put("ACCQUA", dealDto.getShareAmt());// 核算数量 - 交易明细.交易份额
//
//                // 全局变量的更新
//                glb_subjectAmt = ArithmeticUtil.doAdd(glb_subjectAmt, dealDto.getPrinAmt());// 变量.成本科目余额
//                                                                                            // =
//                                                                                            // 变量.成本科目余额
//                                                                                            // +
//                                                                                            // 交易明细.交易本金
//                glb_subjectNum = ArithmeticUtil.doAdd(glb_subjectNum, dealDto.getShareAmt());// 变量.成本科目数量
//                                                                                             // =
//                                                                                             // 变量.成本科目数量
//                                                                                             // +
//                                                                                             // 交易明细.交易份额
                //ad.会计场景代码="货币基金-基金转换入确认交易"
                ad.setTableId(DictCodeListTableId.会计场景代码_货币基金及货币型资产_基金转换入确认交易.getValue());
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
                DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(dealDto.getValTradeFeeDetailList(),
                        dealDto.getProdId(), dealDto.getPortfolioId());
                //佣金		<-	变量.交易各种费用.随交易支付_佣金
                accountAmountMap.put(BookConstant.COMMISSION_OUT, dealTypesFeeDto.getCommissionForDealPay());
                //应付佣金		<-		变量.交易各种费用.佣金
                accountAmountMap.put(BookConstant.COMMISSION, dealTypesFeeDto.getCommission());
                //交易费用		<-		变量.交易各种费用.随交易支付_申购费
                accountAmountMap.put(BookConstant.FEEAMT, dealTypesFeeDto.getSubscriptionFeeForDealPay());
                //发生数量		<-		交易明细.交易份额
                accountAmountMap.put(BookConstant.ACCQUA, dealDto.getShareAmt());
                //转入金额		<-		交易明细.交易本金
                accountAmountMap.put(BookConstant.COST_INTO, dealDto.getPrinAmt());
                //应收红利		<-		交易明细.利息总额
                //accountAmountMap.put(BookConstant.ACCQUA, dealDto.getIntAmt());
                //转出金额		<-		交易明细.交易金额
                accountAmountMap.put(BookConstant.TRADE_QTY, dealDto.getTradeAmt());

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
                //转出金额_本位币	<-		转入金额_本位币 + 佣金_本位币 + 交易费用_本位币 - 应付佣金_本位币 - 应收红利_本位币
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
                BokDetailId bokDetailId = fundCommonService.getFundBokDetailId(dealDto.getTradeId());
                //账务明细代码2					<-		DTO.账务明细代码
                if(bokDetailId != null){
                    //账务明细代码2					<-		DTO.账务明细代码
                    accountSceneMap.put(BookConstant.BOK_DETAIL_ID2, bokDetailId);
                }
                //本位币	<-		交易类账务处理结果.本位币
                accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
                //全局变量的更新
                //变量.成本科目余额 	=	变量.成本科目余额 + 交易明细.交易本金
                glb_subjectAmt = ArithmeticUtil.doAdd(glb_subjectAmt, dealDto.getPrinAmt());
                //变量.成本科目数量		=	变量.成本科目数量 + 交易明细.交易份额
                glb_subjectNum = ArithmeticUtil.doAdd(glb_subjectNum, dealDto.getShareAmt());
                //变量.应收红利科目余额	=	变量.应收红利科目余额 + 交易明细.利息总额
                glb_subjectProfit = ArithmeticUtil.doAdd(glb_subjectNum, dealDto.getIntAmt());
            }
            /** modify by yanya on 20200111 货币基金增加转换交易处理，转入方向变更为转换申购确认 end**/
            else if (StringUtil.equals(dealDto.getPs(), DictCodeList.交易方向_现金分红.getValue())) {
                // // 会计场景代码
                // ad.setTableId(DictCodeListTableId.会计场景代码_货币基金及货币型资产_现金分红.getValue());
                if (DictCodeListExt1.金融产品类型_基金.getValue().equals(dealDto.getFinprodType())) {
                    ad.setTableId(DictCodeListTableId.会计场景代码_货币基金及货币型资产_现金分红.getValue());
                    accountSceneMap.put(BookConstant.BOOK_REMARK, DictCodeListTableId.会计场景代码_货币基金及货币型资产_现金分红.getCodeName() + "登账");// 备注
                }
                else {
                    ad.setTableId(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_红利发放.getValue());
                    accountSceneMap.put(BookConstant.BOOK_REMARK, DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_红利发放.getCodeName() + "登账");// 备注
                }

                Date mDate = null;
                if (dealDto.getMdate() != null) {
                    // 结息日 <- 交易.到期日
                    mDate = dealDto.getMdate();
                }
                else {
                    mDate = DateUtils.addDays(happenDateNow, -1);
                }
                accountSceneMap.put(BookConstant.MDATE, mDate);
                // 变量.冲减应计利息
                BigDecimal varCjyjlx = BigDecimal.ZERO;
                // 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->账务明细辅助查询SHEET
                BokBookdetailCurrentExt bokBookdetailCurrentExt = accountingCommonService.queryBokBookdetailCurrentExt(mDate,
                    bookDateNow, booksetIdNow, bokDetailIdNow);
                if (StringUtil.isNotEmpty(bokBookdetailCurrentExt)) {
                    varCjyjlx = bokBookdetailCurrentExt.getTdyIntincexp();
                }
                else {
                    // 优先级
                    Long taskPriEx = mDate.equals(happenDateNow) ? taskPri : null;
                    // 应计利息
                    SubjectBalance subjectBalanceYjlx = this.getSubjectBalance(booksetIdNow, bokDetailIdNow, mDate, bookDateNow,
                        DictCodeList.余额方向_借方.getValue(), DictCodeBaseSubjectNatrueNew.应收股利.getValue(), taskPriEx);
                    if (subjectBalanceYjlx != null) {
                        varCjyjlx = subjectBalanceYjlx.getBalance() == null ? BigDecimal.ZERO : subjectBalanceYjlx.getBalance();
                    }
                }
                // 记账金额要素 map塞值
                // add by weipeng on 20220221 区分资金账户金额元素 start
//                // 记账金额要素 map塞值
//                accountAmountMap.put("BONUSAMT", dealDto.getTradeAmt());// 分红金额 - 交易明细.交易金额
//                //分红金额 _本币
//                accountAmountMap.put("BONUSAMT" + BookConstant.BCCYAMT
//                		,calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get("BONUSAMT"), changeRate));
                // 定义 本位币金额
                accountAmountMap.put(资金账户金额元素, dealDto.getTradeAmt());
                BigDecimal bonusAmtBccy = calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(资金账户金额元素), changeRate);
                accountAmountMap.put(资金账户金额元素 + BookConstant.BCCYAMT, bonusAmtBccy);
                // add by weipeng on 20220221 区分资金账户金额元素 end
                // accountAmountMap.put("NETAMT_D", null);// 轧差金额（借）
                // accountAmountMap.put("NETAMT_C", null);// 轧差金额（贷）
                accountAmountMap.put("ACCQUA", dealDto.getShareAmt());// 核算数量 - 交易明细.交易份额
                // 应收金额 <- 变量.冲减应计利息
                accountAmountMap.put(BookConstant.INTAMT, varCjyjlx);
                //应收金额 _本币
                accountAmountMap.put(BookConstant.INTAMT + BookConstant.BCCYAMT 
                		,calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(BookConstant.INTAMT), changeRate));
                // 轧差金额
                BigDecimal netAmt = ArithmeticUtil.subtract(dealDto.getTradeAmt(), varCjyjlx);
                if (netAmt.compareTo(BigDecimal.ZERO) >= 0) {
                    // 轧差金额（贷） <- 变量.轧差
                    accountAmountMap.put(BookConstant.NETAMT_C, netAmt);
                    //轧差金额（贷） _本币
                    accountAmountMap.put(BookConstant.NETAMT_C + BookConstant.BCCYAMT 
                    		,ArithmeticUtil.round(ArithmeticUtil.doSub(accountAmountMap.get(资金账户金额元素 + BookConstant.BCCYAMT)
		                    		, accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)), NumberEnum.MONEY.getCode(), null));
                }
                else {
                    accountAmountMap.put(BookConstant.NETAMT_D, netAmt.negate());
                    //轧差金额（借） _本币
                    accountAmountMap.put(BookConstant.NETAMT_C + BookConstant.BCCYAMT 
                    		,ArithmeticUtil.round(ArithmeticUtil.doSub(accountAmountMap.get(资金账户金额元素 + BookConstant.BCCYAMT)
		                    		, accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT).negate()), NumberEnum.MONEY.getCode(), null));
                }
                
                // 全局变量的更新
                glb_subjectProfit = ArithmeticUtil.doSub(glb_subjectProfit, varCjyjlx);// 变量.应收红利科目余额
                                                                                       // =
                                                                                       // 变量.应收红利科目余额
                                                                                       // -
                                                                                       // 交易明细.利息总额

            }
            else if (StringUtil.equals(dealDto.getPs(), DictCodeList.交易方向_红利再投.getValue())) {
                // // 会计场景代码
                // ad.setTableId(DictCodeListTableId.会计场景代码_货币基金及货币型资产_红利再投.getValue());
                if (DictCodeListExt1.金融产品类型_基金.getValue().equals(dealDto.getFinprodType())) {
                    ad.setTableId(DictCodeListTableId.会计场景代码_货币基金及货币型资产_红利再投.getValue());
                    accountSceneMap.put(BookConstant.BOOK_REMARK, DictCodeListTableId.会计场景代码_货币基金及货币型资产_红利再投.getCodeName() + "登账");// 备注

                }
                else {
                    ad.setTableId(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_红利再投.getValue());
                    accountSceneMap.put(BookConstant.BOOK_REMARK, DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_红利再投.getCodeName() + "登账");// 备注

                }
                Date mDate = null;
                if (dealDto.getMdate() != null) {
                    // 结息日 <- 交易.到期日
                    mDate = dealDto.getMdate();
                }
                else {
                    mDate = DateUtils.addDays(happenDateNow, -1);
                }
                accountSceneMap.put(BookConstant.MDATE, mDate);
                // 变量.冲减应计利息
                BigDecimal varCjyjlx = BigDecimal.ZERO;

                // 调用共通方法：账务类共通方法设计 - 账务明细辅助查询
                BokBookdetailCurrentExt bokBookdetailCurrentExtNow = accountingCommonService.queryBokBookdetailCurrentExt(mDate,
                    bookDateNow, booksetIdNow, bokDetailIdNow);

                // 分情况获取【变量.冲减应计利息】值
                if (bokBookdetailCurrentExtNow != null) {
                    // 变量.冲减应计利息 = 账务明细辅助信息.当日累计应计利息余额
                    varCjyjlx = bokBookdetailCurrentExtNow.getTdyIntincexp();
                }
                else {
                    // 优先级
                    Long taskPriEx = mDate.equals(happenDateNow) ? taskPri : null;
                    // 应计利息
                    SubjectBalance subjectBalanceYjlx = this.getSubjectBalance(booksetIdNow, bokDetailIdNow, mDate, bookDateNow,
                        DictCodeList.余额方向_借方.getValue(), DictCodeBaseSubjectNatrueNew.应收股利.getValue(), taskPriEx);
                    if (subjectBalanceYjlx != null) {
                        varCjyjlx = subjectBalanceYjlx.getBalance() == null ? BigDecimal.ZERO : subjectBalanceYjlx.getBalance();
                    }
                }

                // 记账金额要素 map塞值
                accountAmountMap.put(BookConstant.TRADE_QTY, dealDto.getFpriceAmt());// 分红金额 -
                                                                                     // 交易明细.交易金额
                // accountAmountMap.put("NETAMT_D", null);// 轧差金额（借）
                // accountAmountMap.put("NETAMT_C", null);// 轧差金额（贷）
                accountAmountMap.put("ACCQUA", dealDto.getShareAmt());// 核算数量 - 交易明细.交易份额
                // 应收金额 <- 变量.应收红利科目余额
                accountAmountMap.put(BookConstant.INTAMT, varCjyjlx);
                /**20200102 taoran add**/
                accountAmountMap.put(BookConstant.TRADE_QTY + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode()
                		, null, accountAmountMap.get(BookConstant.TRADE_QTY), changeRate));// 分红金_本位币
                accountAmountMap.put(BookConstant.INTAMT + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode()
                		, null, accountAmountMap.get(BookConstant.INTAMT), changeRate));// 应收金额_本位币
                accountAmountMap.put(BookConstant.TRADE_QTY + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode()
                		, null, accountAmountMap.get(BookConstant.TRADE_QTY), changeRate));// 分红金_本位币
                /**20200102 taoran add end**/
                // 轧差金额
                BigDecimal netAmt = ArithmeticUtil.subtract(dealDto.getFpriceAmt(), varCjyjlx);
                // 轧差金额_本位币	20190102
                BigDecimal netAmtBccy = ArithmeticUtil.subtract(accountAmountMap.get(BookConstant.TRADE_QTY + BookConstant.BCCYAMT)
                		, accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT));
                if (netAmt.compareTo(BigDecimal.ZERO) >= 0) {
                    // 轧差金额（贷） <- 变量.轧差
                    accountAmountMap.put(BookConstant.NETAMT_C, netAmt);
                    // 轧差金额（贷）_本位币 <- 变量.轧差 20190102
                    accountAmountMap.put(BookConstant.NETAMT_C + BookConstant.BCCYAMT, ArithmeticUtil.round(netAmtBccy, NumberEnum.MONEY.getCode(), null));
                }
                else {
                    accountAmountMap.put(BookConstant.NETAMT_D, netAmt.negate());
                    accountAmountMap.put(BookConstant.NETAMT_D + BookConstant.BCCYAMT, ArithmeticUtil.round(netAmtBccy.negate(), NumberEnum.MONEY.getCode(), null));
                }

                // 全局变量的更新
                glb_subjectAmt = ArithmeticUtil.doAdd(glb_subjectAmt, dealDto.getPrinAmt());// 变量.成本科目余额
                                                                                            // =
                                                                                            // 变量.成本科目余额
                                                                                            // +
                                                                                            // 交易明细.交易本金
                glb_subjectNum = ArithmeticUtil.doAdd(glb_subjectNum, dealDto.getShareAmt());// 变量.成本科目数量
                                                                                             // =
                                                                                             // 变量.成本科目数量
                                                                                             // +
                                                                                             // 交易明细.交易份额
                glb_subjectProfit = ArithmeticUtil.doSub(glb_subjectProfit, varCjyjlx);// 变量.应收红利科目余额
                                                                                       // =
                                                                                       // 变量.应收红利科目余额
                                                                                       // -
                                                                                       // 交易明细.交易金额

            }
            else if (StringUtil.equals(dealDto.getPs(), DictCodeList.交易方向_买入.getValue())) {
                // // 会计场景代码
                // ad.setTableId(DictCodeListTableId.会计场景代码_货币基金及货币型资产_转仓买入.getValue());

                if (DictCodeListExt1.金融产品类型_基金.getValue().equals(dealDto.getFinprodType())) {
                    ad.setTableId(DictCodeListTableId.会计场景代码_货币基金及货币型资产_转仓买入.getValue());
                    accountSceneMap.put(BookConstant.BOOK_REMARK, DictCodeListTableId.会计场景代码_货币基金及货币型资产_转仓买入.getCodeName() + "登账");// 备注

                    // ********** accountAmountMap设置如下**********
                    // 调用 共通【交易各类费用处理】
                    DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
                            dealDto.getValTradeFeeDetailList(), dealDto.getProdId(), dealDto.getPortfolioId());
                    // 变量.交易各种费用 = 上述返回值
                    // 佣金	  <-  变量.交易各种费用.随交易支付_佣金
                    accountAmountMap.put(BookConstant.COMMISSION_OUT,dealTypesFeeDto.getCommissionForDealPay());
                    // 应付佣金	<-	变量.交易各种费用.佣金
                    accountAmountMap.put(BookConstant.COMMISSION,dealTypesFeeDto.getCommission());
                    // 交易费用	<-	变量.交易各种费用.随交易支付_其他费用
                    accountAmountMap.put(BookConstant.FEEAMT,dealTypesFeeDto.getOtherFeeForDealPay());
                    // 发生数量  <-  交易明细.交易份额
                    accountAmountMap.put(BookConstant.ACCQUA,dealDto.getShareAmt());
                    // 交易份额	<-	交易明细.交易本金
                    accountAmountMap.put(BookConstant.TRADE_QTY,dealDto.getPrinAmt());
                    // 应收红利	<-	交易明细.利息总额
                    accountAmountMap.put(BookConstant.BONUSAMT,dealDto.getIntAmt());
                    // 交易金额  <-  交易明细.交易金额
                    accountAmountMap.put(BookConstant.DEALAMT,dealDto.getTradeAmt());

                    //accountSceneMap设置如下：
                    /** add by yanya on 20200113 增加佣金账务明细代码 start**/
                    //佣金账务明细	<-	变量.交易各种费用.佣金账务明细
                    accountSceneMap.put(BookConstant.BOK_DETAIL_COMMISSION, dealTypesFeeDto.getCommissionCode());
                    /** add by yanya on 20200113 增加佣金账务明细代码 end**/

                    // 定义 本币金额元素Map
                    Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
                    for(String key : accountAmountMap.keySet()){
                    	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
                    }
                    accountAmountMap.putAll(bccyAmountMap);
                    //	交易金额_本位币	<- 	交易份额_本位币 + 应收红利_本位币 + 应付佣金_本位币 + 交易费用_本位币 - 佣金_本位币																									
                    BigDecimal tradeQtyBccy = accountAmountMap.get(BookConstant.TRADE_QTY + BookConstant.BCCYAMT);
                    BigDecimal bonusAmtBccy = accountAmountMap.get(BookConstant.BONUSAMT + BookConstant.BCCYAMT);
                    BigDecimal commissionBccy = accountAmountMap.get(BookConstant.COMMISSION + BookConstant.BCCYAMT);
                    BigDecimal feeAmtBccy = accountAmountMap.get(BookConstant.FEEAMT + BookConstant.BCCYAMT);
                    BigDecimal commissionOutBccy = accountAmountMap.get(BookConstant.COMMISSION_OUT + BookConstant.BCCYAMT);
                    BigDecimal dealAmtBccy = ArithmeticUtil.doSub(
                    		ArithmeticUtil.doAdd(tradeQtyBccy,bonusAmtBccy,commissionBccy,feeAmtBccy), commissionOutBccy);
                    accountAmountMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT,ArithmeticUtil.round(dealAmtBccy, NumberEnum.MONEY.getCode(), null));
                    
                    // 全局变量的更新
                    // 变量.成本科目余额  =	变量.成本科目余额 + 交易明细.交易本金
                    glb_subjectAmt = ArithmeticUtil.doAdd(glb_subjectAmt, dealDto.getPrinAmt());
                    // 变量.成本科目数量  =	变量.成本科目数量 + 交易明细.交易份额
                    glb_subjectNum = ArithmeticUtil.doAdd(glb_subjectNum, dealDto.getShareAmt());
                    // 变量.应收红利科目余额  =	变量.应收红利科目余额  + 交易明细.利息总额
                    glb_subjectProfit = ArithmeticUtil.doAdd(glb_subjectProfit, dealDto.getIntAmt());

                }
                else {
                    ad.setTableId(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_转仓买入.getValue());
                    accountSceneMap.put(BookConstant.BOOK_REMARK, DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_转仓买入.getCodeName() + "登账");// 备注

                    // 记账金额要素 map塞值
                    accountAmountMap.put("TRADE_QTY", dealDto.getPrinAmt());// 交易份额 - 交易明细.交易本金
                    accountAmountMap.put("BONUSAMT", dealDto.getIntAmt());// 应收红利 -交易明细.利息总额
                    accountAmountMap.put("DEALAMT", dealDto.getTradeAmt());// 交易金额 - 交易明细.交易金额
                    accountAmountMap.put("ACCQUA", dealDto.getShareAmt());// 核算数量 - 交易明细.交易份额
                    
                    /**20200102 taoran add start**/
                    // 定义 本币金额元素Map
                    Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
                    for(String key : accountAmountMap.keySet()){
                    	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
                    }
                    accountAmountMap.putAll(bccyAmountMap);
                    //交易金额_本位币<- 交易份额_本位币 + 应收红利_本位币											
                    BigDecimal dealAmtBccy = ArithmeticUtil.doAdd(bccyAmountMap.get("TRADE_QTY" + BookConstant.BCCYAMT)
                    		,bccyAmountMap.get("BONUSAMT" + BookConstant.BCCYAMT));
                    accountAmountMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT,ArithmeticUtil.round(dealAmtBccy, NumberEnum.MONEY.getCode(), null));
                    /**20200102 taoran add end**/
                    
                    // 全局变量的更新
                    glb_subjectAmt = ArithmeticUtil.doAdd(glb_subjectAmt, dealDto.getPrinAmt());// 变量.成本科目余额
                    // =
                    // 变量.成本科目余额
                    // +
                    // 交易明细.交易本金
                    glb_subjectNum = ArithmeticUtil.doAdd(glb_subjectNum, dealDto.getShareAmt());// 变量.成本科目数量
                    // =
                    // 变量.成本科目数量
                    // +
                    // 交易明细.交易份额
                    glb_subjectProfit = ArithmeticUtil.doAdd(glb_subjectProfit, dealDto.getIntAmt());// 变量.应收红利科目余额
                    // =
                    // 变量.应收红利科目余额
                    // +
                    // 交易明细.利息总额
                }
            }
            else if (StringUtil.equals(dealDto.getPs(), DictCodeList.交易方向_卖出.getValue())) {
                // // 会计场景代码
                // ad.setTableId(DictCodeListTableId.会计场景代码_货币基金及货币型资产_转仓卖出.getValue());

                if (DictCodeListExt1.金融产品类型_基金.getValue().equals(dealDto.getFinprodType())) {
                    ad.setTableId(DictCodeListTableId.会计场景代码_货币基金及货币型资产_转仓卖出.getValue());
                    accountSceneMap.put(BookConstant.BOOK_REMARK, DictCodeListTableId.会计场景代码_货币基金及货币型资产_转仓卖出.getCodeName() + "登账");// 备注

                    // ********** accountAmountMap设置如下**********
                    BigDecimal varCjgyjzbdSubjectAmt = BigDecimal.ZERO; // 变量.冲减公允价值变动
                    BigDecimal varCjcbSubjectAmt = BigDecimal.ZERO;// 变量.冲减成本
                    BigDecimal varCjslSubjectNum = BigDecimal.ZERO;// 变量.冲减数量
                    BigDecimal varCjyslxSubjectAmt = BigDecimal.ZERO;// 变量.冲减应收利息
                    BigDecimal varLxceSubjectAmt = BigDecimal.ZERO;// 变量.利息差额

                    // 调用 共通【交易各类费用处理】
                    DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
                            dealDto.getValTradeFeeDetailList(), dealDto.getProdId(), dealDto.getPortfolioId());
                    // 变量.交易各种费用 = 上述返回值
                    // 佣金	  <-  变量.交易各种费用.随交易支付_佣金
                    accountAmountMap.put(BookConstant.COMMISSION_OUT,dealTypesFeeDto.getCommissionForDealPay());
                    // 应付佣金	<-	变量.交易各种费用.佣金
                    accountAmountMap.put(BookConstant.COMMISSION,dealTypesFeeDto.getCommission());
                    
                    // 交易费用	<-	变量.交易各种费用.随交易支付_其他费用
                    accountAmountMap.put(BookConstant.FEEAMT,dealTypesFeeDto.getOtherFeeForDealPay());
                    // 实收金额  <-  交易明细.交易金额
                    accountAmountMap.put(BookConstant.DEALAMT,dealDto.getTradeAmt());
                    // 变量.冲减公允价值变动= 变量.公允价值变动科目余额 * 交易明细.交易份额/变量.成本科目数量
                    varCjgyjzbdSubjectAmt = ArithmeticUtil.multiply(glb_varGyjzxbdSubject,(ArithmeticUtil.divide(dealDto.getShareAmt(),glb_subjectNum)));
                    //调用金额共通方法设置有效小数位
                    varCjgyjzbdSubjectAmt = ArithmeticUtil.round(varCjgyjzbdSubjectAmt, NumberEnum.MONEY);

                    // 变量.冲减成本=变量.成本科目余额 * 交易明细.交易份额/变量.成本科目数量
                    varCjcbSubjectAmt = ArithmeticUtil.multiply(glb_subjectAmt,(ArithmeticUtil.divide(dealDto.getShareAmt(), glb_subjectNum)));
                    //调用金额共通方法设置有效小数位
                    varCjcbSubjectAmt = ArithmeticUtil.round(varCjcbSubjectAmt, NumberEnum.MONEY);

                    // 变量.冲减数量=NVL（交易明细.交易份额，0）
                    varCjslSubjectNum = dealDto.getShareAmt() == null ? BigDecimal.ZERO : dealDto.getShareAmt();
                    //调用金额共通方法设置有效小数位
                    varCjslSubjectNum = ArithmeticUtil.round(varCjslSubjectNum, NumberEnum.MONEY);
                    // 卖出份额  <-	变量.冲减成本
                    accountAmountMap.put(BookConstant.REDEEM_QTY,varCjcbSubjectAmt);
                    // 发生数量  <-	变量.冲减数量
                    accountAmountMap.put(BookConstant.ACCQUA,varCjslSubjectNum);
                    // IF 变量.成本科目余额 - 变量.冲减成本 = 0
                    if(ArithmeticUtil.round(glb_subjectAmt, NumberEnum.MONEY).equals(varCjcbSubjectAmt)){
                        // 变量.冲减应收利息 = 变量.应收红利科目余额
                        varCjyslxSubjectAmt = glb_subjectProfit;
                    } else {
                        // 变量.冲减应收利息 = 交易明细.利息总额
                        varCjyslxSubjectAmt = dealDto.getIntAmt();
                    }
                   
                    // 变量.利息差额 = 交易明细.利息总额 - 变量.冲减应收利息
                    varLxceSubjectAmt = ArithmeticUtil.doSub(dealDto.getIntAmt(),varCjyslxSubjectAmt);
                    
                    // 应收红利  <-	变量.利息差额
                    accountAmountMap.put(BookConstant.INTAMT,varCjyslxSubjectAmt);

                    // IF 变量.利息差额 > 0
                    if(varLxceSubjectAmt.compareTo(BigDecimal.ZERO) > 0){
                        accountAmountMap.put(BookConstant.BONUSNET_C,varLxceSubjectAmt);
                    } else {
                        accountAmountMap.put(BookConstant.BONUSNET_D,varLxceSubjectAmt.negate());
                    }
                    // IF 变量.冲减公允价值变动 > 0
                    if(BigDecimal.ZERO.compareTo(varCjgyjzbdSubjectAmt) < 0){
                        // 公允价值变动_贷<- 变量.冲减公允价值变动 FLOAT_C
                        accountAmountMap.put(BookConstant.FLOAT_C,varCjgyjzbdSubjectAmt);
                        // 公允价值变动损益_借<- 变量.冲减公允价值变动 FLOAT_INGPL_D
                        accountAmountMap.put(BookConstant.FLOAT_INGPL_D,varCjgyjzbdSubjectAmt);
                    } else {
                        // 公允价值变动_借<-变量.公允价值变动*-1 FLOAT_D
                        accountAmountMap.put(BookConstant.FLOAT_D,varCjgyjzbdSubjectAmt.negate());
                        // 公允价值变动损益_贷<-变量.公允价值变动*-1 FLOAT_INGPL_C
                        accountAmountMap.put(BookConstant.FLOAT_INGPL_C,varCjgyjzbdSubjectAmt.negate());
                    }

                    // 定义 变量.买卖价差 = 交易明细.交易金额 + 变量.交易各种费用.随交易支付_佣金 + 变量.交易各种费用.随交易支付_其他费用
                    //								 -变量.冲减成本- 变量.交易各种费用.佣金 - 变量.利息差额
                    BigDecimal varMmjc =  ArithmeticUtil.doSub(
                                    ArithmeticUtil.doAdd(dealDto.getTradeAmt(),dealTypesFeeDto.getCommissionForDealPay(),dealTypesFeeDto.getOtherFeeForDealPay()),
                                    ArithmeticUtil.doAdd(varCjcbSubjectAmt,dealTypesFeeDto.getCommission(),dealDto.getIntAmt()));
                    // IF 变量.买卖价差 > 0
                    if (varMmjc.compareTo(BigDecimal.ZERO) > 0) {
                        accountAmountMap.put(BookConstant.NETAMT_C,varMmjc);
                    }
                    else {
                        accountAmountMap.put(BookConstant.NETAMT_D,varMmjc.negate());
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

                    if(DictCodeListExt1.交易场所_OTC场外.getValue().equals(dealDto.getTradeMarket())){
                    	//利息调整
                    	if(varLxceSubjectAmt.compareTo(BigDecimal.ZERO) > 0){
                    		//本币_实收金额<-本币_卖出份额 + 本币_应收红利 + 利息调整_贷_本位币 - 交易费用_本位币																				
                    		BigDecimal redeemQtyBccy = accountAmountMap.get(BookConstant.REDEEM_QTY + BookConstant.BCCYAMT);
                    		BigDecimal intAmtBccy = accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT); 
                    		BigDecimal bonusNetBccy = accountAmountMap.get(BookConstant.BONUSNET_C + BookConstant.BCCYAMT); 
                    		BigDecimal feeAmtBccy = accountAmountMap.get(BookConstant.FEEAMT + BookConstant.BCCYAMT);
                    		BigDecimal dealAmtBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(redeemQtyBccy, intAmtBccy, bonusNetBccy), feeAmtBccy);
                    		accountAmountMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT, ArithmeticUtil.round(dealAmtBccy, NumberEnum.MONEY.getCode(), null));
                    	}
                    	else {
                    		//本币_实收金额<-本币_卖出份额 + 本币_应收红利 - 本币_利息调整_借 - 交易费用_本位币																				
                    		BigDecimal redeemQtyBccy = accountAmountMap.get(BookConstant.REDEEM_QTY + BookConstant.BCCYAMT);
                    		BigDecimal intAmtBccy = accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT); 
                    		BigDecimal bonusNetBccy = accountAmountMap.get(BookConstant.BONUSNET_D + BookConstant.BCCYAMT); 
                    		BigDecimal feeAmtBccy = accountAmountMap.get(BookConstant.FEEAMT + BookConstant.BCCYAMT);
                    		BigDecimal dealAmtBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(redeemQtyBccy, intAmtBccy, bonusNetBccy), feeAmtBccy);
                    		accountAmountMap.put(BookConstant.DEALAMT + BookConstant.BCCYAMT, ArithmeticUtil.round(dealAmtBccy, NumberEnum.MONEY.getCode(), null));
                    	}
                    }										
                    else{
                    	//定义 本币_变量.买卖价差
                    	BigDecimal varMmjcBccy = BigDecimal.ZERO;
                    	//冲减公允价值变动 > 0
                    	if(BigDecimal.ZERO.compareTo(varCjgyjzbdSubjectAmt) < 0) {
                    		//变量.买卖价差_本位币 =(本币_实收金额 + 本币_应付佣金 + 交易费用_本位币 + 公允价值变动损益_借_本位币)-(本币_本币_卖出份额 + 本币_佣金 + 公允价值变动_贷_本位币)																									
                    		BigDecimal dealAmtBccy = accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT);
                    		BigDecimal commissionBccy = accountAmountMap.get(BookConstant.COMMISSION + BookConstant.BCCYAMT);
                    		BigDecimal feeAmtBccy = accountAmountMap.get(BookConstant.FEEAMT + BookConstant.BCCYAMT);
                    		BigDecimal floatIngplBccy = accountAmountMap.get(BookConstant.FLOAT_INGPL_D + BookConstant.BCCYAMT);
                    		BigDecimal redeemQtyBccy = accountAmountMap.get(BookConstant.REDEEM_QTY + BookConstant.BCCYAMT);
                    		BigDecimal commissionOutBccy = accountAmountMap.get(BookConstant.COMMISSION_OUT + BookConstant.BCCYAMT);
                    		BigDecimal floatBccy = accountAmountMap.get(BookConstant.FLOAT_C + BookConstant.BCCYAMT);
                    		varMmjcBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(dealAmtBccy, commissionBccy, feeAmtBccy, floatIngplBccy)
		                    		, ArithmeticUtil.doAdd(redeemQtyBccy, commissionOutBccy, floatBccy));
                    	}
                    	else {
                    		//变量.买卖价差_本位币 =(本币_实收金额 + 本币_应付佣金 + 交易费用_本位币 + 公允价值变动_借_本位币)-(本币_本币_卖出份额 + 本币_佣金 + 公允价值变动损益_贷_本位币)																						
                    		BigDecimal dealAmtBccy = accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT);
                    		BigDecimal commissionBccy = accountAmountMap.get(BookConstant.COMMISSION + BookConstant.BCCYAMT);
                    		BigDecimal feeAmtBccy = accountAmountMap.get(BookConstant.FEEAMT + BookConstant.BCCYAMT);
                    		BigDecimal floatBccy = accountAmountMap.get(BookConstant.FLOAT_D + BookConstant.BCCYAMT);
                    		BigDecimal redeemQtyBccy = accountAmountMap.get(BookConstant.REDEEM_QTY + BookConstant.BCCYAMT);
                    		BigDecimal commissionOutBccy = accountAmountMap.get(BookConstant.COMMISSION_OUT + BookConstant.BCCYAMT);
                    		BigDecimal floatIngplBccy = accountAmountMap.get(BookConstant.FLOAT_INGPL_C + BookConstant.BCCYAMT);
                    		varMmjcBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(dealAmtBccy, commissionBccy, feeAmtBccy, floatBccy)
		                    		, ArithmeticUtil.doAdd(redeemQtyBccy, commissionOutBccy, floatIngplBccy));
                    	}
                    	if(varMmjcBccy.compareTo(BigDecimal.ZERO)>0) {
                    		accountAmountMap.put(BookConstant.NETAMT_C + BookConstant.BCCYAMT, ArithmeticUtil.round(varMmjcBccy, NumberEnum.MONEY.getCode(), null));
                    	}
                    	else {
                    		accountAmountMap.put(BookConstant.NETAMT_D + BookConstant.BCCYAMT, ArithmeticUtil.round(varMmjcBccy.negate(), NumberEnum.MONEY.getCode(), null));
                    	}
                    }
                    /**20200102 taoran add end**/

                    // 全局变量的更新
                    // 变量.成本科目余额   =	变量.成本科目余额 - 变量.冲减成本
                    glb_subjectAmt = ArithmeticUtil.doSub(glb_subjectAmt,varCjcbSubjectAmt);
                    // 变量.成本科目数量   =	变量.成本科目数量 - 变量.冲减数量
                    glb_subjectNum = ArithmeticUtil.doSub(glb_subjectNum,varCjslSubjectNum);
                    // 变量.应收红利科目余额  =	变量.应收红利科目余额  - 变量.冲减应收利息
                    glb_subjectProfit = ArithmeticUtil.doSub(glb_subjectProfit,varCjyslxSubjectAmt);
                }
                else {
                    ad.setTableId(DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_卖出转仓.getValue());
                    accountSceneMap.put(BookConstant.BOOK_REMARK,
                        DictCodeListTableId.会计场景代码_公允价值损益类货币型资产_卖出转仓.getCodeName() + "登账");// 备注

	                // 记账金额要素 map塞值
	                accountAmountMap.put("DEALAMT", dealDto.getTradeAmt());// 实收金额 - 交易明细.交易金额
	                accountAmountMap.put("REDEEM_QTY", dealDto.getPrinAmt());// 卖出份额 - 交易明细.交易本金
	                accountAmountMap.put("ACCQUA", dealDto.getShareAmt());// 核算数量 - 交易明细.交易份额
	                // MODIFY JINMAODONG 从杭州分支移植，处理了基金全部卖出的冲销不对，应该冲应计
	                // accountAmountMap.put("INTAMT", dealDto.getIntAmt());// 应收红利 - 交易明细.利息总额
	                BigDecimal intamt = dealDto.getIntAmt(); // 应收红利 = 交易明细.利息总额
	                // 如果是全部卖出，需要全部冲销
	                if (glb_subjectNum.compareTo(dealDto.getShareAmt()) == 0) { // 变量.成本科目数量 =
	                                                                            // 交易明细.交易份额
	                    intamt = glb_subjectProfit; // 应收红利 - 变量.应收红利科目余额
	                }
	                accountAmountMap.put("INTAMT", intamt);// 应收红利
	                BigDecimal netamt = ArithmeticUtil.doSub(dealDto.getIntAmt(), intamt); // 变量.轧差金额 =
	                                                                                       // 交易明细.利息总额
	                                                                                       // - 应收红利
	                if (netamt.compareTo(BigDecimal.ZERO) >= 0) { // （变量.轧差金额>=0）
	                    // 轧差金额（贷） <- 变量.轧差
	                    accountAmountMap.put(BookConstant.NETAMT_C, netamt);
	                }
	                else {
	                    // 轧差金额(借) <- 变量.轧差金额*-1
	                    accountAmountMap.put(BookConstant.NETAMT_D, netamt.negate());
	                }
	                /**20200102 taoran add start**/
                    // 定义 本币金额元素Map
                    Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
                    for(String key : accountAmountMap.keySet()){
                    	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
                    }
                    accountAmountMap.putAll(bccyAmountMap);
                    //变量.轧差金额_本位币 = 卖出份额_本位币 + 应收红利_本位币 - 实收金额_本位币																						
                    BigDecimal netamtBccy = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(accountAmountMap.get(BookConstant.REDEEM_QTY + BookConstant.BCCYAMT)
                    		,accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)), accountAmountMap.get(BookConstant.DEALAMT + BookConstant.BCCYAMT));
                    if (netamtBccy.compareTo(BigDecimal.ZERO) >= 0) {
	                    // 轧差金额（贷）_本位币
	                    accountAmountMap.put(BookConstant.NETAMT_C + BookConstant.BCCYAMT, ArithmeticUtil.round(netamtBccy, NumberEnum.MONEY.getCode(), null));
	                }
	                else {
	                    // 轧差金额(借) <- 变量.轧差金额*-1
	                    accountAmountMap.put(BookConstant.NETAMT_D + BookConstant.BCCYAMT , ArithmeticUtil.round(netamtBccy.negate(), NumberEnum.MONEY.getCode(), null));
	                }
                    /**20200102 taoran add end**/
	                // 全局变量的更新
	                glb_subjectAmt = ArithmeticUtil.doSub(glb_subjectAmt, dealDto.getPrinAmt());// 变量.成本科目余额
	                                                                                            // =
	                                                                                            // 变量.成本科目余额
	                                                                                            // -
	                                                                                            // 交易明细.交易本金
	                glb_subjectNum = ArithmeticUtil.doSub(glb_subjectNum, dealDto.getShareAmt());// 变量.成本科目数量
	                                                                                             // =
	                                                                                             // 变量.成本科目数量
	                                                                                             // -
	                                                                                             // 交易明细.交易份额
	                glb_subjectProfit = ArithmeticUtil.doSub(glb_subjectProfit, dealDto.getIntAmt());// 变量.应收红利科目余额
	                                                                                                 // =
	                                                                                                 // 变量.应收红利科目余额
	                                                                                                 // -
	                                                                                                 // 交易明细.利息总额
                }
            }
            else if (StringUtil.equals(dealDto.getPs(), DictCodeList.交易方向_份额强增.getValue())) {
                // 会计场景代码
                ad.setTableId(DictCodeListTableId.会计场景代码_货币基金及货币型资产_份额调增.getValue());
                accountSceneMap.put(BookConstant.BOOK_REMARK, DictCodeListTableId.会计场景代码_货币基金及货币型资产_份额调增.getCodeName() + "登账");// 备注
                accountSceneMap.put(BookConstant.IS_ZERO_ACCOUNT,DictCodeList.是否_是.getValue());

//                BigDecimal prinamt = dealDto.getPrinAmt()==null?BigDecimal.ZERO:dealDto.getPrinAmt();
                // modify by weipeng on 20220328:货币基金默认单位净值为1，份额调增只存份额字段
                BigDecimal prinamt = dealDto.getShareAmt()==null?BigDecimal.ZERO:dealDto.getShareAmt();
                // 记账金额要素 map塞值
                accountAmountMap.put("IN_DEALAMT",prinamt);// 调增金额 - 交易明细.交易本金
                accountAmountMap.put("ACCQUA", dealDto.getShareAmt()==null?BigDecimal.ZERO:dealDto.getShareAmt());// 核算数量 - 交易明细.交易份额
                
                accountSceneMap.put(BookConstant.ACCT_TYPE,dealDto.getAcctType());
                
                /**20200102 taoran add start**/
                //成本金额_本位币<-	成本金额 * 变量.汇率
                accountAmountMap.put("IN_DEALAMT" + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, prinamt, changeRate));
                accountAmountMap.put("DEALAMT" + BookConstant.BCCYAMT, accountAmountMap.get("IN_DEALAMT" + BookConstant.BCCYAMT));
                // add by weipeng on 20220221 区分资金账户金额元素 start
                // 本方资金账户类型 或 对方资金账户类型对应资金账户类型，赋值对应金额元素
                accountAmountMap.put(资金账户金额元素, prinamt);
                accountAmountMap.put(资金账户金额元素 + BookConstant.BCCYAMT, accountAmountMap.get("IN_DEALAMT" + BookConstant.BCCYAMT));
                // add by weipeng on 20220221 区分资金账户金额元素 end
                /**20200102 taoran add end**/
                // 全局变量的更新
                glb_subjectAmt = ArithmeticUtil.doAdd(glb_subjectAmt, prinamt);// 变量.成本科目余额
                                                                                            // =
                                                                                            // 变量.成本科目余额
                                                                                            // +
                                                                                            // 交易明细.交易本金
                glb_subjectNum = ArithmeticUtil.doAdd(glb_subjectNum, dealDto.getShareAmt());// 变量.成本科目数量
                                                                                             // =
                                                                                             // 变量.成本科目数量
                                                                                             // +
                                                                                             // 交易明细.交易份额

            }
            else if (StringUtil.equals(dealDto.getPs(), DictCodeList.交易方向_份额强减.getValue())) {
                // 会计场景代码
                ad.setTableId(DictCodeListTableId.会计场景代码_货币基金及货币型资产_份额调减.getValue());
                accountSceneMap.put(BookConstant.BOOK_REMARK, DictCodeListTableId.会计场景代码_货币基金及货币型资产_份额调减.getCodeName() + "登账");// 备注
                accountSceneMap.put(BookConstant.IS_ZERO_ACCOUNT,DictCodeList.是否_是.getValue());
                // 记账金额要素 map塞值
                BigDecimal prinamt = dealDto.getShareAmt()==null?BigDecimal.ZERO:dealDto.getShareAmt();
                
                accountAmountMap.put("OUT_DEALAMT", prinamt);// 调减金额 - 交易明细.交易本金
                accountAmountMap.put("ACCQUA", dealDto.getShareAmt()==null?BigDecimal.ZERO:dealDto.getShareAmt());// 核算数量 - 交易明细.交易份额
                // add by weipeng on 20220221 区分资金账户金额元素 start
                accountAmountMap.put(资金账户金额元素, prinamt);
                accountAmountMap.put(资金账户金额元素 + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, prinamt, changeRate));
                // add by weipeng on 20220221 区分资金账户金额元素 end
                
                accountSceneMap.put(BookConstant.ACCT_TYPE,dealDto.getAcctType());
                
                /**20200102 taoran add start**/
                //成本金额_本位币<-	成本金额 * 变量.汇率
                accountAmountMap.put("IN_DEALAMT" + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, prinamt, changeRate));
                accountAmountMap.put("DEALAMT" + BookConstant.BCCYAMT, accountAmountMap.get("IN_DEALAMT" + BookConstant.BCCYAMT));
                /**20200102 taoran add end**/
                
                // 全局变量的更新
                glb_subjectAmt = ArithmeticUtil.doSub(glb_subjectAmt, prinamt);// 变量.成本科目余额
                                                                                            // =
                                                                                            // 变量.成本科目余额
                                                                                            // -
                                                                                            // 交易明细.交易本金
                glb_subjectNum = ArithmeticUtil.doSub(glb_subjectNum, dealDto.getShareAmt());// 变量.成本科目数量
                                                                                             // =
                                                                                             // 变量.成本科目数量
                                                                                             // -
                                                                                             // 交易明细.交易份额

            }

            // 记账金额元素币种信息塞值
            // 因为与记账金额元素map的key及数量一致，故采用循环
            for (String keyNow : accountAmountMap.keySet()) {
                accountCurrencyMap.put(keyNow, dealDto.getCcy());// 币种 - 交易明细.交易币种
            }
            
            // 本位币 <- 交易类账务处理结果.本位币
            accountCurrencyMap.put(BookConstant.B_CCY, acctEntity.getStandardCcy());
            
            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
            ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
            ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素
            // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 start
            ad.setSubjectMappingConfigDefault(dealDto.getSubjectMappingConfig());
            // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 end
            // 记账凭证生成
            BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, batchExecuteInfo);
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            rtnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        }

        //4.统计分析账务明细汇总信息赋值
        // 调用共通：统计分析账务明细汇总查询
        // 变量.当日统计分析账务明细汇总信息
        BokStatDetPosiiton bokSummaryTableNew = accountingCommonService
                .getCountAnalysis(acctEntity.getHappenDate(), acctEntity.getHappenDate(), acctEntity.getBookDate(), acctEntity.getBooksetId(), acctEntity.getBokDetailId(), null, null);
        if(bokSummaryTableNew == null) {
            bokSummaryTableNew = new BokStatDetPosiiton();
            // 变量.当日统计分析账务明细汇总信息.账套代码 = 交易类账务处理结果.账套代码
            bokSummaryTableNew.setBooksetId(acctEntity.getBooksetId());
            // 变量.当日统计分析账务明细汇总信息.发生日期 = 交易类账务处理结果.发生日期
            bokSummaryTableNew.setHappenDate(acctEntity.getHappenDate());
            // 变量.当日统计分析账务明细汇总信息.入账日期 = 交易类账务处理结果.入账日期
            bokSummaryTableNew.setBookDate(acctEntity.getBookDate());
            // 变量.当日统计分析账务明细汇总信息.账务明细代码 = 交易类账务处理结果.账务明细代码
            bokSummaryTableNew.setBokDetailId(acctEntity.getBokDetailId());
            // 变量.当日统计分析账务明细汇总信息.处理序列 = 调用序列生成共通
            bokSummaryTableNew.setBookSummaryOrder(
                    new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.账务汇总表_处理序列)));
            // 变量.当日统计分析账务明细汇总信息.账套日期 = 交易类账务处理结果.账套日期
            bokSummaryTableNew.setBooksetDate(acctEntity.getBooksetDate());
            // 变量.当日统计分析账务明细汇总信息.币种 = 变量.标的信息.币种
            bokSummaryTableNew.setCcy(targetFinProduct.getCcy());
            // 变量.当日统计分析账务明细汇总信息.业务明细代码 = 变量.标的信息.金融产品代码
            bokSummaryTableNew.setBusiId(targetFinProduct.getFinprodId());
            // 变量.当日统计分析账务明细汇总信息.本位币 = 交易类账务处理结果.本位币
            bokSummaryTableNew.setbCcy(acctEntity.getStandardCcy());
            // 变量.当日统计分析账务明细汇总信息.金融产品类型 = 变量.标的信息.金融产品类型
            bokSummaryTableNew.setFinprodType(targetFinProduct.getFinprodType());
            // 变量.当日统计分析账务明细汇总信息.起息日 = 变量.标的信息.起息日
            bokSummaryTableNew.setVdate(targetFinProduct.getVdate());
            // 变量.当日统计分析账务明细汇总信息.到期日 = 变量.标的信息.实际到期日
            bokSummaryTableNew.setMdate(targetFinProduct.getActmdate());
        }

        // 汇率
        BigDecimal changeRate = BigDecimal.ONE;
        if (StringUtil.isNotEmpty(targetFinProduct.getCcy())
                && !targetFinProduct.getCcy().equals(acctEntity.getStandardCcy())) {
            List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(
                    bookDateNow, acctEntity.getBooksetId(), acctEntity.getStandardCcy(), targetFinProduct.getCcy());
            if (!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null) {
                changeRate = currencyMarketList.get(0).getClosePrice();
            }
        }

        // 变量.当日统计分析账务明细汇总信息.当前持仓 = 变量.成本科目数量
        bokSummaryTableNew.setPosAmt(glb_subjectNum);
        // 变量.当日统计分析账务明细汇总信息.当日份额 = 变量.成本科目数量
        bokSummaryTableNew.setTdyPosition(glb_subjectNum);
        // 变量.当日统计分析账务明细汇总信息.当日成本 = 变量.成本科目余额
        bokSummaryTableNew.setTdyCostAmt(glb_subjectAmt);
        // 变量.当日统计分析账务明细汇总信息.当日成本_本位币 = 变量.成本科目余额*变量.汇率
        bokSummaryTableNew.setTdyCostAmtB(calculationCommonService.doMultiply(
                NumberEnum.MONEY.getCode(), null, glb_subjectAmt, changeRate));
        // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额 = 变量.应收红利科目余额
        bokSummaryTableNew.setTdyIntincexp(glb_subjectProfit);
        // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额_本位币 =	变量.应收红利科目余额*变量.汇率
        bokSummaryTableNew.setTdyIntincexpB(calculationCommonService.doMultiply(
                NumberEnum.MONEY.getCode(), null, glb_subjectProfit, changeRate));
        // 变量.当日统计分析账务明细汇总信息.当日累计公允价值变动 =	变量.公允价值变动科目
        bokSummaryTableNew.setTdyFloatIngplExp(glb_varGyjzxbdSubject);
        // 变量.当日统计分析账务明细汇总信息.当日累计公允价值变动_本位币 =	变量.公允价值变动科目*变量.汇率
        bokSummaryTableNew.setTdyFloatIngplExpB(calculationCommonService.doMultiply(
                NumberEnum.MONEY.getCode(), null, glb_varGyjzxbdSubject, changeRate));

        rtnResult.getValBokStatDetPosiitonBatch().add(bokSummaryTableNew);

        return rtnResult;
    }

    /**
     * 调用公用方法：【科目余额查询 - 期待方向】，获取科目余额信息中的科目余额数据
     * 
     * @param booksetId 账套代码
     * @param bokDetailId 账务明细代码
     * @param happenDate 发生日
     * @param expBalanceDirect 期待余额方向
     * @param bookDate 入账日
     * @param baseSubjectNature 基础科目性质
     * @param taskPri 任务优先级
     * @return subjectBalance 科目余额信息
     * @see
     * @since 1.0
     */
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
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(searchDto,
            expBalanceDirect);

        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            subjectBalance = subjectBalanceList.get(0);
        }

        return subjectBalance;
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

        // 确保不为null
        if (targetFinProduct == null) {
            targetFinProduct = new TargetFinProduct();
        }

        return targetFinProduct;
    }

}
