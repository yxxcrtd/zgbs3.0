/*
 * 文件名：BondDealBookService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：ZhangYangFei
 * 修改时间：2018年10月22日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.seclenddealbook.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.*;
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
import com.joyintech.tams.valuation.batch.common.calculation.entity.DealTypesFeeDto;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TradeInfoDto;
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
 * 债券借贷交易账务处理Service
 * 
 * @author ZhangYangFei
 * @version 1.0 2018年10月22日
 * @see BondDealBookService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class SecLendDealBookDetailNewService {

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
     * 计算类共通
     */
    @Autowired
    private CalculationCommonService calculationCommonService;
    
    /**
     * process 数据处理
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public BookGenerateResult execute(Map<String, Object> param) {
        // 获取reader返回值
        FinProductDealInfoResult acctEntity = (FinProductDealInfoResult)param.get(
            GlobalConstant.READER_RESULT);

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
        //String invAimNow = acctEntity.getInvAim();// 投资目的
        TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();// 会计场景
        Date happenDateNow = acctEntity.getHappenDate();// 发生日期
        Date bookDateNow = acctEntity.getBookDate();// 入账日期
        List<FinProductDealInfoComDTO> listFinProductDealInfoComDTONow = acctEntity.getListFinProductDealInfoComDTO(); // 交易基础信息列表
        Long taskPri = listFinProductDealInfoComDTONow.get(0).getTaskPri();// 任务优先级（交易基础信息列表的任务优先级都是一致的，故取第一个）
        // 金融产品代码
//        String finprodIdNow = listFinProductDealInfoComDTONow.get(0).getFinprodId();
        String finprodTypeNow = "";// 金融产品类型

        // 定义变量
//        BigDecimal glb_subjectAmt = BigDecimal.ZERO;// 变量.成本科目余额
//        BigDecimal glb_subjectNum = BigDecimal.ZERO;// 变量.成本科目数量
        BigDecimal glb_subjectProfit = BigDecimal.ZERO;// 变量.应计利息科目余额

        // 调用共通查询发生日最新余额
        
        // 交易期待方向：case 融入 then 贷方 else 借方
        String expBalanceDirect = DictCodeList.余额方向_借方.getValue();
        // 基础科目性质:case 融入 then '应付债券借贷借入费' else '应收债券借贷费用'
        String baseSubjectNature = DictCodeBaseSubjectNatrueNew.应收利息_融出.getValue();

        /** add by yanya on 20200122 债券借贷交易增加结算手续费处理 start**/
        //2.2.债券借贷费用处理方式
        /**调用共通设计->【详细设计-账务类共通方法设计.xlsx】->账套参数查询
            参数名					账套参数查询
            账套代码					交易类账务处理结果.账套代码
            参数项					债券借贷费用损益计入方式
            返回值：					账套配置表DTO
         **/
        List<String> paramCodeList = new ArrayList<String>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_债券借贷费用损益计入方式.getValue());
        //账套参数查询
        Map<String, Object> valBokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(acctEntity.getBooksetId(), paramCodeList);
        //账套配置表参数项_债券借贷费用损益计入方式
        Object bondLendingTypeObj = valBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_债券借贷费用损益计入方式.getValue());
        String bondLendingType = bondLendingTypeObj == null ? "" : bondLendingTypeObj.toString();
        /** add by yanya on 20200122 债券借贷交易增加结算手续费处理 end**/
        
        // 循环 交易基础信息列表 进行逻辑处理
        for (FinProductDealInfoComDTO dealDto : listFinProductDealInfoComDTONow) {
            finprodTypeNow = dealDto.getFinprodType();
            if(finprodTypeNow.equals(DictCodeListExt1.金融产品类型_债券借贷融入.getValue())){
                expBalanceDirect = DictCodeList.余额方向_贷方.getValue();
                baseSubjectNature = DictCodeBaseSubjectNatrueNew.应付利息_融入.getValue();
            }
            // 应计利息类科目余额
            SubjectBalance subjectBalanceForfit = this.getSubjectBalance(booksetIdNow, bokDetailIdNow,
                happenDateNow, bookDateNow, expBalanceDirect,baseSubjectNature, taskPri);
            if (subjectBalanceForfit != null && subjectBalanceForfit.getBalance() != null) {
                glb_subjectProfit = subjectBalanceForfit.getBalance() == null?BigDecimal.ZERO:subjectBalanceForfit.getBalance();// 变量.应计利息科目余额 = DTO.科目余额
            }
            
            // 变量 - 交易信息（标的交易信息）
            TradeInfoDto dealInfoForTarget = this.getTargetInfo(dealDto.getFinprodId(),dealDto.getFinprodType(),dealDto.getBranch());
            // 保证获取的标的交易信息不为空
            if (dealInfoForTarget == null) {
                dealInfoForTarget = new TradeInfoDto();
            }
            
            AccountDriver ad = new AccountDriver();
            // 设置账务引擎参数
            ad.setTreeNode(bookSetTreeNodeNow);// 塞入记账场景树节点 = 会计场景
            
            // 记账场景要素map
            Map<String, Object> accountSceneMap = new HashMap<String, Object>();
            // 记账的相关金额要素 map
            Map<String, BigDecimal> accountAmountMap = new HashMap<String,BigDecimal>();
            
            // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值),(其他场景元素)
            accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealDto);
            accountSceneMap.put("TRADE_MARKET", dealDto.getTradeMarket());
            accountSceneMap.put(BookConstant.TYPE_1, dealInfoForTarget.getType1());// 分类1 - 变量.交易信息.分类1
            accountSceneMap.put(BookConstant.TYPE_2, dealInfoForTarget.getType2());// 分类2 - 变量.交易信息.分类2
            
            String finprodTypeName = SysDataUtil.getNameFromDataDict(DictCode.金融产品类型, dealDto.getFinprodType());
            String psName = SysDataUtil.getNameFromDataDict(DictCode.交易方向, dealDto.getPs());

            if (DictCodeList.交易方向_买入.getValue().equals(dealDto.getPs())) {// 交易方向 - 买入
                ad.setTableId(getTableId(dealDto.getPs(), finprodTypeNow, null));// ad.会计场景代码
                accountSceneMap.put(BookConstant.BOOK_REMARK, finprodTypeName + psName + "交易登账"); // 金融产品类型+交易方向+"交易登账"
                accountAmountMap.put(BookConstant.COST, dealDto.getPrinAmt());//标的债券<-交易明细.交易本金
                /** add by yanya on 20200122 债券借贷交易增加结算手续费处理 start**/
                //登记托管机构	<-	变量.交易信息.登记托管机构
                accountSceneMap.put(BookConstant.REGIST_ORG, dealInfoForTarget.getRegistOrg());
                /**
                    调用 计算类共通【交易各类费用处理】
                    参数名：					交易各类费用处理
                    交易费用列表					交易明细.交易费用列表
                    金融产品代码					交易明细.理财产品代码
                    投资组合代码					交易明细.投资组合代码
                    返回值:					交易各类费用
                 **/
                // 交易各类费用处理
                DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
                        dealDto.getValTradeFeeDetailList(), dealDto.getProdId(), dealDto.getPortfolioId());
                //结算手续费	<-		变量.交易各种费用.结算手续费
                accountAmountMap.put(BookConstant.SETTLEMENTFEE, dealTypesFeeDto.getSettlementFee());
                //IF (变量.费用损益计入方式 ！= '计入投资收益') {
                if(!DictCodeListExt3.债券借贷费用损益计入方式_计入投资收益.getValue().equals(bondLendingType)) {
                    // 结算手续费支出 <- 变量.交易各种费用.结算手续费 + 变量.交易各种费用.随交易支付_结算手续费
                    //结算手续费支出 <-		变量.交易各种费用.结算手续费 + 变量.交易各种费用.随交易支付_结算手续费
//                    accountAmountMap.put(BookConstant.SETTLEMENTFEE_OUT, ArithmeticUtil.doAdd(
//                            dealTypesFeeDto.getSettlementFee(), dealTypesFeeDto.getSettlementFeeForDealPay()));

                    accountAmountMap.put(BookConstant.SETTLEFEE_OUT, ArithmeticUtil.doAdd(
                            dealTypesFeeDto.getSettlementFee(), dealTypesFeeDto.getSettlementFeeForDealPay()));
                }
                //结算手续费账务明细代码		<-		变量.交易各种费用.结算手续费账务明细代码
                accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEMENTFEE, dealTypesFeeDto.getSettlementFeeCode());
                /** add by yanya on 20200122 债券借贷交易增加结算手续费处理 end**/
                rtnResult = doGenAccount(rtnResult, dealDto, batchExecuteInfo, ad, accountSceneMap,accountAmountMap);
            }else{// 卖出、到期
                accountAmountMap.put(BookConstant.REALAMT, dealDto.getIntAmt());//实收金额:交易明细.利息总额 
//                //变量.借贷费差额:实收收入 - 应收收入(应收支出)
//                BigDecimal jdfce = glb_subjectProfit.subtract(dealDto.getIntAmt()==null?BigDecimal.ZERO:dealDto.getIntAmt());
//                if(null==jdfce){
//                    jdfce=BigDecimal.ZERO;
//                }
                AccountDriver ad2 = new AccountDriver();
                // 设置账务引擎参数
                ad2.setTreeNode(bookSetTreeNodeNow);// 塞入记账场景树节点 = 会计场景
                // 记账场景要素map
                Map<String, Object> accountSceneMap2 = new HashMap<String, Object>();
                // 记账的相关金额要素 map
                Map<String, BigDecimal> accountAmountMap2 = new HashMap<String,BigDecimal>();
                // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值),(其他场景元素)
                accountSceneMap2 = bookCommonLogic.transactionElementAssign(acctEntity, dealDto);
                accountSceneMap2.put("TRADE_MARKET", dealDto.getTradeMarket());
                accountSceneMap2.put(BookConstant.TYPE_1, dealInfoForTarget.getType1());// 分类1 - 变量.交易信息.分类1
                accountSceneMap2.put(BookConstant.TYPE_2, dealInfoForTarget.getType2());// 分类2 - 变量.交易信息.分类2
                
                //交易明细.金融产品类型 = “债券借贷融入”                                               
                if(finprodTypeNow.equals(DictCodeListExt1.金融产品类型_债券借贷融入.getValue())){
                    // -------------------- 账务处理1:
                    // 实付金额 <- 交易明细.利息总额
                    BigDecimal sfje = dealDto.getIntAmt()==null?BigDecimal.ZERO:dealDto.getIntAmt();
                    // 应收支出 <- 变量.应计利息科目余额
                    BigDecimal yszc = glb_subjectProfit;
                    // 变量.借贷费差额 <- 实付金额 - 应收支出
                    BigDecimal jdfce = sfje.subtract(yszc);
                    ad.setTableId(getTableId(dealDto.getPs(), finprodTypeNow, ValDictCodeList.账务处理_1.getValue()));// ad.会计场景代码
                    accountSceneMap.put(BookConstant.BOOK_REMARK, finprodTypeName + psName + "支付费用交易登账"); // 金融产品类型+交易方向+"支付费用交易登账"
                    accountAmountMap.put(BookConstant.EXPAMT, glb_subjectProfit);// 应收支出:变量.应计利息科目余额     
                    if(jdfce.compareTo(BigDecimal.ZERO) > 0){
                        accountAmountMap.put(BookConstant.DSCLOSS_D, jdfce); // 借贷费差额_借
                    }else{
                        accountAmountMap.put(BookConstant.DSCLOSS_C, jdfce.multiply(new BigDecimal("-1")));// 借贷费差额_贷 
                    }

                    rtnResult = doGenAccount(rtnResult, dealDto, batchExecuteInfo, ad, accountSceneMap, accountAmountMap);
                    
                    // -------------------- 账务处理2:
                    ad2.setTableId(getTableId(dealDto.getPs(), finprodTypeNow, ValDictCodeList.账务处理_2.getValue()));// ad.会计场景代码
                    accountSceneMap2.put(BookConstant.BOOK_REMARK, finprodTypeName + psName + "交易登账"); // 金融产品类型+交易方向+"交易登账"
                    accountAmountMap2.put(BookConstant.COST, dealDto.getPrinAmt());//负债成本金额:交易明细.交易本金  
                    rtnResult = doGenAccount(rtnResult, dealDto, batchExecuteInfo, ad2, accountSceneMap2, accountAmountMap2);
                }else if(finprodTypeNow.equals(DictCodeListExt1.金融产品类型_债券借贷融出.getValue())){ // 融出
                    // -------------------- 账务处理1:
                    // 实收金额 <- 交易明细.利息总额
                    BigDecimal ssje = dealDto.getIntAmt()==null?BigDecimal.ZERO:dealDto.getIntAmt();
                    // 应收收入 <- 变量.应计利息科目余额
                    BigDecimal yssr = glb_subjectProfit;
                    // 变量.借贷费差额 <- 实收金额 - 应收收入
                    BigDecimal jdfce = ssje.subtract(yssr);
                    ad.setTableId(getTableId(dealDto.getPs(), finprodTypeNow, ValDictCodeList.账务处理_1.getValue()));// ad.会计场景代码
                    accountSceneMap.put(BookConstant.BOOK_REMARK, finprodTypeName + psName + "收取费用交易登账"); // 金融产品类型+交易方向+"收取费用交易登账"
                    accountAmountMap.put(BookConstant.INCAMT, glb_subjectProfit);//应收收入:变量.应计利息科目余额 
                    if(jdfce.compareTo(BigDecimal.ZERO) > 0){
                        accountAmountMap.put(BookConstant.DSCLOSS_C, jdfce); // 借贷费差额_贷
                    }else{
                        accountAmountMap.put(BookConstant.DSCLOSS_D, jdfce.multiply(new BigDecimal("-1"))); // 借贷费差额_借
                    }
                    rtnResult = doGenAccount(rtnResult, dealDto, batchExecuteInfo, ad,accountSceneMap, accountAmountMap);
                    
                    // -------------------- 账务处理2:
                    ad2.setTableId(getTableId(dealDto.getPs(), finprodTypeNow, ValDictCodeList.账务处理_2.getValue()));// ad.会计场景代码
                    accountSceneMap2.put(BookConstant.BOOK_REMARK, finprodTypeName + psName + "交易登账"); // 金融产品类型+交易方向+"交易登账"
                    accountAmountMap2.put(BookConstant.COST, dealDto.getPrinAmt());//标的债券<-交易明细.交易本金   
                    rtnResult = doGenAccount(rtnResult, dealDto, batchExecuteInfo, ad2,accountSceneMap2, accountAmountMap2);
                }
            }
        } 
        return rtnResult;
    }

    /**
     * 记账凭证生成
     * 
     * @param rtnResult
     * @param batchExecuteInfo
     * @param ad
     * @param accountSceneMap
     * @param accountAmountMap
     * @see
     * @since 1.0
     */
    private BookGenerateResult doGenAccount(BookGenerateResult rtnResult,
                                            FinProductDealInfoComDTO dealDto,
                                            SysBatchExecInfo batchExecuteInfo, 
                                            AccountDriver ad,
                                            Map<String, Object> accountSceneMap,
                                            Map<String, BigDecimal> accountAmountMap) {
        // 记账的相关金额对应的币种 map
        Map<String, Object> accountCurrencyMap = new HashMap<String,Object>();
        //所有金额元素 <-交易明细.交易币种
        for (Map.Entry<String, BigDecimal> entity : accountAmountMap.entrySet()) {
            accountCurrencyMap.put(entity.getKey(), dealDto.getCcy());
        }
        // map塞入场景要素管理中
        ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
        ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
        ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

        // add by weipeng on 20220323 增加辅助核算项配置项 start
        SubjectMappingConfig mappingConfig = dealDto.getSubjectMappingConfig();
        if (DictCodeList.交易方向_买入.getValue().equals(dealDto.getPs())) {
            //辅助核算项配置.费用类型 = 结算手续费
            mappingConfig.setFeeType(DictCodeListExt1.一次性费用类型_结算手续费.getValue());
            //辅助核算项配置.登记托管机构 = 登记托管机构
            mappingConfig.setRegistOrg(dealDto.getRegistOrg());
        }
        ad.setSubjectMappingConfigDefault(mappingConfig);
        // add by weipeng on 20220323 增加辅助核算项配置项 end

        // 记账凭证生成
        BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, batchExecuteInfo);
        // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
        rtnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        return rtnResult;
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
    private SubjectBalance getSubjectBalance(String booksetId, String bokDetailId, Date happenDate, Date bookDate, String expBalanceDirect, String baseSubjectNature, Long taskPri) {
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
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(searchDto, expBalanceDirect);

        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            subjectBalance = subjectBalanceList.get(0);
        }

        return subjectBalance;
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

    /**
     * 获取对应的会计场景代码
     * 
     * @param ps 交易方向
     * @param finprodType 金融产品类型
     * @param bookDoType 账务处理:1,2
     * @return String 会计场景代码
     * @see
     * @since 1.0
     */
    private String getTableId(String ps, String finprodType, String bookDoType) {
        // 设置 产检场景代码
        String tableId = "";
        if (DictCodeList.交易方向_买入.getValue().equals(ps)) {// 交易方向 - 买入
            if (finprodType.equals(DictCodeListExt1.金融产品类型_债券借贷融入.getValue())) {
                tableId = DictCodeListTableId.会计场景代码_债券借贷_借入.getValue();// 会计场景代码="债券借贷-融入" 2508
            } else {// 融出
                tableId = DictCodeListTableId.会计场景代码_债券借贷_借出.getValue();// 会计场景代码="债券借贷-融出" 2501
            }
        } else {// 卖出、到期
            if (finprodType.equals(DictCodeListExt1.金融产品类型_债券借贷融入.getValue())) {
                if (ValDictCodeList.账务处理_1.getValue().equals(bookDoType)) {
                    tableId = DictCodeListTableId.会计场景代码_债券借贷_借入到期_支付费用.getValue();// 会计场景代码="债券借贷-融入到期-支付费用" 2512
                } else {
                    tableId = DictCodeListTableId.会计场景代码_债券借贷_借入到期_非现金交割_还券.getValue();// 会计场景代码="债券借贷-融入到期" 2513
                }
            }else { // 融出
                if (ValDictCodeList.账务处理_1.getValue().equals(bookDoType)) {
                    tableId = DictCodeListTableId.会计场景代码_债券借贷_借出到期_收取费用.getValue(); // 会计场景代码="债券借贷-融出到期-收取费用" 2504
                } else {
                    tableId = DictCodeListTableId.会计场景代码_债券借贷_借出到期_非现金交割_还券.getValue(); // 会计场景代码="债券借贷-融出到期" 2507
                }
            }
        }
        return tableId;
    }
    /**
     * AccountDriver 克隆
     * 
     * @param source
     * @return
     * @see
     * @since 1.0
     */
//    private AccountDriver cloneAccountDriver(AccountDriver source) {
//        AccountDriver clone = new AccountDriver();
//        BeanUtil.copyPropertiesIgnoreNull(clone, source);
//        return clone;
//    }
}
