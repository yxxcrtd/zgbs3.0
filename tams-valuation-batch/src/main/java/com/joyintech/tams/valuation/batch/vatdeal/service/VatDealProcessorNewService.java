/*
 * 文件名：ValAddedTaxDealProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年7月31日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.vatdeal.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrueNew;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 增值税交易批处理Processor逻辑Service
 * 
 * @author weipeng
 * @version 1.0 2018年7月31日
 * @see VatDealProcessorNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class VatDealProcessorNewService {

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
     * process 数据处理
     * 
     * @param param1 参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
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
        // 获取从reader中拿到的值
        TreeNode bookSetTreeNodNow = acctEntity.getBookSetTreeNode();// 会计场景
        List<FinProductDealInfoComDTO> listFinProductDealInfoComDTONow = acctEntity.getListFinProductDealInfoComDTO(); // 金融产品交易信息list
        
        // 3. 交易处理
        // 循环 交易类账务处理结果.交易明细列表,进行逻辑处理
        for (FinProductDealInfoComDTO dealNow : listFinProductDealInfoComDTONow) {

            AccountDriver ad = new AccountDriver();// 凭证类型、账套代码、交易编号全部去掉了。
            ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景

            // 初始化定义 - 存放该笔记账的场景要素 map定义
            Map<String, Object> accountSceneMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
            Map<String, Object> accountCurrencyMap = new HashMap<>();

            // 调用共通场景元素赋值-交易类场景元素
            accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity, dealNow);

            // IF(交易.交易方向 == "增值税缴纳"）
            if (dealNow.getPs().equals(DictCodeList.交易方向_税费缴税.getValue())) {
                // 20191122 魏鹏：分录变更，通道与产品分开，分录变更
//                // TableId = DictCodeListTableId.会计场景代码_增值税缴纳-缴纳日-增值税缴纳.getValue())
//                ad.setTableId(DictCodeListTableId.会计场景代码_增值税缴纳_交税处理.getValue());
//                // KeyEnum.REMARK.getCode() = "增值税缴纳-缴纳日-增值税缴纳"
//                accountSceneMap.put(BookConstant.BOOK_REMARK, "增值税缴纳-交税处理");
                if (DictCodeListExt1.金融产品类型_通道.getValue().equals(dealNow.getFinprodType())) {
                    // TableId = DictCodeListTableId.会计场景代码_增值税缴纳-通道-增值税缴纳.getValue())
                    ad.setTableId(DictCodeListTableId.会计场景代码_增值税缴纳_通道_交税处理.getValue());
                    // KeyEnum.REMARK.getCode() = "增值税缴纳-通道-增值税缴纳"
                    accountSceneMap.put(BookConstant.BOOK_REMARK, "增值税缴纳-通道-交税处理");
                } else {
                    // TableId = DictCodeListTableId.会计场景代码_增值税缴纳-直投-增值税缴纳.getValue())
                    ad.setTableId(DictCodeListTableId.会计场景代码_增值税缴纳_直投_交税处理.getValue());
                    // KeyEnum.REMARK.getCode() = "增值税缴纳-直投增值税缴纳"
                    accountSceneMap.put(BookConstant.BOOK_REMARK, "增值税缴纳-直投-交税处理");
                }
                // 金额元素.实际缴税金额 = 交易.[交易金额]
                accountAmountMap.put(BookConstant.AMT_PAY, dealNow.getTradeAmt());
            }
            // ELSE IF(交易.交易方向 == "增值税退税"）
            if (dealNow.getPs().equals(DictCodeList.交易方向_税费退税.getValue())) {
//                // TableId = DictCodeList.增值税缴纳-退税日-退税.getValue())
//                ad.setTableId(DictCodeListTableId.会计场景代码_增值税缴纳_退税处理.getValue());
//                // KeyEnum.REMARK.getCode() = "增值税退税"
//                accountSceneMap.put(BookConstant.BOOK_REMARK, "增值税缴纳-退税处理");
                if (DictCodeListExt1.金融产品类型_通道.getValue().equals(dealNow.getFinprodType())) {
                    // TableId = DictCodeListTableId.会计场景代码_增值税缴纳_通道_退税处理.getValue())
                    ad.setTableId(DictCodeListTableId.会计场景代码_增值税缴纳_通道_退税处理.getValue());
                    // KeyEnum.REMARK.getCode() = "增值税缴纳-通道-退税处理"
                    accountSceneMap.put(BookConstant.BOOK_REMARK, "增值税缴纳-通道-退税处理");
                } else {
                    // TableId = DictCodeListTableId.会计场景代码_增值税缴纳_直投_退税处理.getValue())
                    ad.setTableId(DictCodeListTableId.会计场景代码_增值税缴纳_直投_退税处理.getValue());
                    // KeyEnum.REMARK.getCode() = "增值税缴纳-直投-退税处理"
                    accountSceneMap.put(BookConstant.BOOK_REMARK, "增值税缴纳-直投-退税处理");
                }
                // 金额元素.实际退税金额 = 交易.[交易金额]
                accountAmountMap.put(BookConstant.AMT_REFUND, dealNow.getTradeAmt());
            }
            
            // FOR (交易费用明细 明细 :交易.交易费用明细) {
            //定义 变量.缴税差额-负 = 0	add by taoran 20200107
            BigDecimal feeAmt_f = BigDecimal.ZERO;
            for (TrdTradeFeeDetail feeDetail : dealNow.getValTradeFeeDetailList()) {
                // IF(明细.一次性费用类型 = "利息收入增值税")
                if (feeDetail.getFeeType().equals(DictCodeList.增值税类型_利息收入.getValue())) {
                    // 金额元素.利息收入税额 = 费用金额
                    //accountAmountMap.put(BookConstant.VAT_INT, feeDetail.getFeeAmt());
                	String baseSubjectNature = DictCodeBaseSubjectNatrueNew.应交税费_应交增值税_利息.getValue();
                    /**add by taoran 20200107 start**/
                    //调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
                    SubjectBalance subjectBalance = this.getSubjectBalance(acctEntity.getBooksetId(), acctEntity.getBokDetailId(), acctEntity.getBookDate(), 
                    		dealNow.getMdate(), DictCodeList.余额方向_贷方.getValue(), baseSubjectNature);


                    //定义 变量.应交利息收入税 = 返回值.科目余额.本币余额
                    BigDecimal lxIncomeFee = BigDecimal.ZERO;
                    if(subjectBalance != null) {
                    	//定义 变量.应交利息收入税 = 返回值.科目余额.本币余额															
                        lxIncomeFee = subjectBalance.getbAmt();
                        /**add by taoran 20200120 start**/
                        // 金额元素.利息收入税额 = 变量.应交利息收入税
                        accountAmountMap.put(BookConstant.VAT_INT, lxIncomeFee);
                        /**add by taoran 20200120 end**/
                    }
                    //(变量.应交利息收入税 > 费用金额)
                    if(lxIncomeFee.compareTo(feeDetail.getFeeAmt()) > 0) {
                        //变量.缴税差额-负 += （变量.应交利息收入税 - 费用金额）
                        feeAmt_f = ArithmeticUtil.doAdd(feeAmt_f, ArithmeticUtil.doSub(lxIncomeFee, feeDetail.getFeeAmt()));
                    }
                    else if(lxIncomeFee.compareTo(feeDetail.getFeeAmt()) < 0){
                        // add by weipeng on 20220308：增加增值税备抵科目冲减的处理逻辑，区分利息收入备抵科目 start
                        // 查询 利息收入_增值税备抵科目余额
                        baseSubjectNature = DictCodeBaseSubjectNatrueNew.利息收入_增值税备抵.getValue();
                        //调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
                        SubjectBalance 利息收入_增值税备抵余额 = this.getSubjectBalance(acctEntity.getBooksetId(), acctEntity.getBokDetailId(), acctEntity.getBookDate(),
                                dealNow.getMdate(), DictCodeList.余额方向_贷方.getValue(), baseSubjectNature);
                        // 查询 投资收益_利息收入_增值税备抵科目余额
                        baseSubjectNature = DictCodeBaseSubjectNatrueNew.投资收益_利息收入_增值税备抵.getValue();
                        /**add by taoran 20200107 start**/
                        //调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
                        SubjectBalance 投资收益_利息收入_增值税备抵余额 = this.getSubjectBalance(acctEntity.getBooksetId(), acctEntity.getBokDetailId(), acctEntity.getBookDate(),
                                dealNow.getMdate(), DictCodeList.余额方向_贷方.getValue(), baseSubjectNature);
                        // 定义 变量.利息收入增值税抵扣方式 = 调用账套参数【利息收入增值税抵扣方式】：01 优先利息收入增值税备抵；02 优先投资收益利息收入增值税备抵
                        // 调用共通获取账套配置表中估值表模板字段
                        List<String> paramCodeList = new ArrayList<String>();
                        // 估值表模板
                        paramCodeList.add(DictCode.增值税缴纳利息收入备抵科目.getValue());
                        Map<String, Object> getValBokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(
                                acctEntity.getBooksetId(), paramCodeList);
                        String intTaxDeductType = (String)getValBokBooksetExtMap.get(DictCode.增值税缴纳利息收入备抵科目.getValue());
                        // 若2个科目余额都不为空，则说明2中备抵科目都出现过，则按账套参数配置
                        if(利息收入_增值税备抵余额 != null && 投资收益_利息收入_增值税备抵余额 != null){
                            if(DictCodeListExt4.利息收入备抵科目_投资收益_利息收入备抵.getValue().equals(intTaxDeductType)){
                                //金额元素.利息收入税差额 = 费用金额 - 变量.应交利息收入税
                                accountAmountMap.put(BookConstant.NETAMT_D_TZSY_LXSR_ZZSBD, ArithmeticUtil.round(ArithmeticUtil.doSub(
                                        feeDetail.getFeeAmt(), lxIncomeFee), NumberEnum.MONEY.getCode(), null));
                            }
                            else{
                                //金额元素.利息收入税差额 = 费用金额 - 变量.应交利息收入税
                                accountAmountMap.put(BookConstant.NETAMT_D_LXSR_ZZSBD, ArithmeticUtil.round(ArithmeticUtil.doSub(
                                        feeDetail.getFeeAmt(), lxIncomeFee), NumberEnum.MONEY.getCode(), null));
                            }
                        }
                        // 否则说明只出现过一种备抵科目，若利息收入_增值税备抵余额不为空
                        else if(利息收入_增值税备抵余额 != null){
                            //金额元素.利息收入税差额 = 费用金额 - 变量.应交利息收入税
                            accountAmountMap.put(BookConstant.NETAMT_D_LXSR_ZZSBD, ArithmeticUtil.round(ArithmeticUtil.doSub(
                                    feeDetail.getFeeAmt(), lxIncomeFee), NumberEnum.MONEY.getCode(), null));
                        }
                        // 否则，若利息收入_增值税备抵余额不为空
                        else if(投资收益_利息收入_增值税备抵余额 != null){
                            //金额元素.利息收入税差额 = 费用金额 - 变量.应交利息收入税
                            accountAmountMap.put(BookConstant.NETAMT_D_TZSY_LXSR_ZZSBD, ArithmeticUtil.round(ArithmeticUtil.doSub(
                                    feeDetail.getFeeAmt(), lxIncomeFee), NumberEnum.MONEY.getCode(), null));
                        }
                        // add by weipeng on 20220308：增加增值税备抵科目冲减的处理逻辑，区分利息收入备抵科目 end
                    }
                    /**add by taoran 20200107 end**/
                }
                // ELSE IF(明细.一次性费用类型 == "红利收入增值税"）
                else if (feeDetail.getFeeType().equals(DictCodeList.增值税类型_红利收入.getValue())) {
                    // 金额元素.红利收入税额 = 费用金额
                    //accountAmountMap.put(BookConstant.VAT_BONUS, feeDetail.getFeeAmt());
                	String baseSubjectNature = DictCodeBaseSubjectNatrue.红利收入税.getValue();
                	if (DictCodeListExt1.金融产品类型_通道.getValue().equals(dealNow.getFinprodType())) {
                		baseSubjectNature = DictCodeBaseSubjectNatrue.合作方红利收入税.getValue();
                    }
                    /**add by taoran 20200107 start**/
                    //调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
                    SubjectBalance subjectBalance = this.getSubjectBalance(acctEntity.getBooksetId(), acctEntity.getBokDetailId(), acctEntity.getBookDate(), 
                    		dealNow.getMdate(), DictCodeList.余额方向_贷方.getValue(), baseSubjectNature);
                    //定义 变量.应交红利收入税 = 返回值.科目余额.本币余额
                    BigDecimal hlIncomeFee =BigDecimal.ZERO;
                    if(subjectBalance != null) {
                    	//定义 变量.应交红利收入税 = 返回值.科目余额.本币余额
                        hlIncomeFee = subjectBalance.getbAmt();
                        /**add by taoran 20200120 start**/
                        // 金额元素.红利收入税额 =  变量.应交红利收入税 
                        accountAmountMap.put(BookConstant.VAT_BONUS, hlIncomeFee);
                        /**add by taoran 20200120 end**/
                    }
                    //(变量.应交红利收入税 > 费用金额)
                    if(hlIncomeFee.compareTo(feeDetail.getFeeAmt()) > 0) {
                        //变量.缴税差额-负 += （变量.应交红利收入税- 费用金额）
                        feeAmt_f = ArithmeticUtil.doAdd(feeAmt_f, ArithmeticUtil.doSub(hlIncomeFee, feeDetail.getFeeAmt()));
                    }
                    else if(hlIncomeFee.compareTo(feeDetail.getFeeAmt()) < 0){
                        //金额元素.红利收入税差额 = 费用金额 - 变量.应交红利收入税
                        accountAmountMap.put(BookConstant.NETAMT_D_TZSY_GLSR_ZZSBD, ArithmeticUtil.round(ArithmeticUtil.doSub(
                                feeDetail.getFeeAmt(), hlIncomeFee), NumberEnum.MONEY.getCode(), null));
                    }
                    /**add by taoran 20200107 end**/
                }
                // ELSE IF(明细.一次性费用类型 == "价差收入增值税"）
                else if (feeDetail.getFeeType().equals(DictCodeList.增值税类型_价差收入.getValue())) {
                    // 金额元素.价差收入税额 = 费用金额
                    //key值 = 投资收益税？
                    //accountAmountMap.put(BookConstant.VAT_INV, feeDetail.getFeeAmt());
                	String baseSubjectNature = DictCodeBaseSubjectNatrueNew.应交税费_应交增值税_价差.getValue();
//                	if (DictCodeListExt1.金融产品类型_通道.getValue().equals(dealNow.getFinprodType())) {
//                		baseSubjectNature = DictCodeBaseSubjectNatrueNew.应交税费_应交增值税_价差.getValue();
//                    }
                    /**add by taoran 20200107 start**/
                    //调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
                    SubjectBalance subjectBalance = this.getSubjectBalance(acctEntity.getBooksetId(), acctEntity.getBokDetailId(), acctEntity.getBookDate(), 
                    		dealNow.getMdate(), DictCodeList.余额方向_贷方.getValue(), baseSubjectNature);
                    //定义 变量.应交投资收益税 = 返回值.科目余额.本币余额
                    BigDecimal tzIncomeFee =BigDecimal.ZERO;
                    if(subjectBalance != null) {
                    	//定义 变量.应交投资收益税 = 返回值.科目余额.本币余额
                        tzIncomeFee = subjectBalance.getbAmt();
                        /**add by taoran 20200120 start**/
                        // 金额元素.价差收入税额 = 应交投资收益税
                        accountAmountMap.put(BookConstant.VAT_INV, tzIncomeFee);
                        /**add by taoran 20200120 end**/
                    }
                    //(变量.应交投资收益税 > 费用金额)
                    if(tzIncomeFee.compareTo(feeDetail.getFeeAmt()) > 0) {
                        //变量.缴税差额-负 += （变量.应交投资收益税- 费用金额）
                        feeAmt_f = ArithmeticUtil.doAdd(feeAmt_f, ArithmeticUtil.doSub(tzIncomeFee, feeDetail.getFeeAmt()));
                    }
                    else if(tzIncomeFee.compareTo(feeDetail.getFeeAmt()) < 0){
                        //金额元素.投资收益税差额 = 费用金额 - 变量.应交投资收益税
                        accountAmountMap.put(BookConstant.NETAMT_D_TZSY_JCSR_ZZSBD, ArithmeticUtil.round(ArithmeticUtil.doSub(
                                feeDetail.getFeeAmt(), tzIncomeFee), NumberEnum.MONEY.getCode(), null));
                    }
                    /**add by taoran 20200107 end**/
                }
                // ELSE IF(明细.一次性费用类型 == "城建附加税"）
                else if (feeDetail.getFeeType().equals(DictCodeList.附加税类型_城建税.getValue())) {
                    // 金额元素.城建税税额 = 费用金额
                    //accountAmountMap.put(BookConstant.VAT_URBAN, feeDetail.getFeeAmt());
                	String baseSubjectNature = DictCodeBaseSubjectNatrueNew.应交税费_应交附加税.getValue();
                    /**add by taoran 20200107 start**/
                    //调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
                    SubjectBalance subjectBalance = this.getSubjectBalance(acctEntity.getBooksetId(), acctEntity.getBokDetailId(), acctEntity.getBookDate(), 
                    		dealNow.getMdate(), DictCodeList.余额方向_贷方.getValue(), baseSubjectNature);
                    //定义 变量.应交城建附加税 = 返回值.科目余额.本币余额
                    BigDecimal cjIncomeFee = BigDecimal.ZERO;
                    if(subjectBalance != null) {
                    	//定义 变量.应交城建附加税 = 返回值.科目余额.本币余额
                        cjIncomeFee = subjectBalance.getbAmt();
                        /**add by taoran 20200120 start**/
                        // 金额元素.城建税税额 = 费用金额
                        accountAmountMap.put(BookConstant.VAT_URBAN, cjIncomeFee);
                        /**add by taoran 20200120 end**/
                    }
                    //(变量.应交城建附加税 > 费用金额)
                    if(cjIncomeFee.compareTo(feeDetail.getFeeAmt()) > 0) {
                        //变量.缴税差额-负 += （变量.应交城建附加税- 费用金额）
                        feeAmt_f = ArithmeticUtil.doAdd(feeAmt_f, ArithmeticUtil.doSub(cjIncomeFee, feeDetail.getFeeAmt()));
                    }
                    else if(cjIncomeFee.compareTo(feeDetail.getFeeAmt()) < 0){
                        //金额元素.城建附加税差额 = 费用金额 - 变量.应交城建附加税
                        	/*   accountAmountMap.put(BookConstant.NETAMT_D_64140101, ArithmeticUtil.round(ArithmeticUtil.doSub(
                        	              feeDetail.getFeeAmt(), cjIncomeFee), NumberEnum.MONEY.getCode(), null));*/
                        /*modify by hxy 20200324 start 通道和直投增值税的科目不一致*/
                        if (DictCodeListExt1.金融产品类型_通道.getValue().equals(dealNow.getFinprodType())) {
                            accountAmountMap.put(BookConstant.NETAMT_D_64140101, ArithmeticUtil.round(ArithmeticUtil.doSub(
                                    feeDetail.getFeeAmt(), cjIncomeFee), NumberEnum.MONEY.getCode(), null));
                        } else {
                            accountAmountMap.put(BookConstant.NETAMT_D_ALT0102, ArithmeticUtil.round(ArithmeticUtil.doSub(
                                    feeDetail.getFeeAmt(), cjIncomeFee), NumberEnum.MONEY.getCode(), null));
                        }
                        /*modify by hxy 20200324 end 通道和直投增值税的科目不一致*/
                    }
                    /**add by taoran 20200107 end**/
                }
                // ELSE IF(明细.一次性费用类型 == "教育附加税"）
                else if (feeDetail.getFeeType().equals(DictCodeList.附加税类型_教育税.getValue())) {
                    // 金额元素.教育税税额 = 费用金额
                    //accountAmountMap.put(BookConstant.VAT_EDU, feeDetail.getFeeAmt());
                	String baseSubjectNature = DictCodeBaseSubjectNatrueNew.应交税费_应交附加税.getValue();
                    /**add by taoran 20200107 start**/
                    //调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
                    SubjectBalance subjectBalance = this.getSubjectBalance(acctEntity.getBooksetId(), acctEntity.getBokDetailId(), acctEntity.getBookDate(), 
                    		dealNow.getMdate(), DictCodeList.余额方向_贷方.getValue(), baseSubjectNature);
                    //定义 变量.应交教育附加税 = 返回值.科目余额.本币余额
                    BigDecimal jyIncomeFee =BigDecimal.ZERO;
                    if(subjectBalance != null) {
                    	//定义 变量.应交教育附加税 = 返回值.科目余额.本币余额
                        jyIncomeFee = subjectBalance.getbAmt();
                        /**add by taoran 20200120 start**/
                        // 金额元素.教育税税额 =  变量.应交教育附加税
                        accountAmountMap.put(BookConstant.VAT_EDU, jyIncomeFee);
                        /**add by taoran 20200120 end**/
                    }
                    //(变量.应交教育附加税 > 费用金额)
                    if(jyIncomeFee.compareTo(feeDetail.getFeeAmt()) > 0) {
                        //变量.缴税差额-负 += （变量.应交教育附加税- 费用金额）
                        feeAmt_f = ArithmeticUtil.doAdd(feeAmt_f, ArithmeticUtil.doSub(jyIncomeFee, feeDetail.getFeeAmt()));
                    }
                    else if(jyIncomeFee.compareTo(feeDetail.getFeeAmt()) < 0){
                        //金额元素.教育附加税差额 = 费用金额 - 变量.应交教育附加税
                        	/*accountAmountMap.put(BookConstant.NETAMT_D_64140102, ArithmeticUtil.round(ArithmeticUtil.doSub(
                        			feeDetail.getFeeAmt(), jyIncomeFee), NumberEnum.MONEY.getCode(), null));*/
                        /*modify by hxy 20200324 start 通道和直投增值税的科目不一致*/
                        if (DictCodeListExt1.金融产品类型_通道.getValue().equals(dealNow.getFinprodType())) {
                            accountAmountMap.put(BookConstant.NETAMT_D_64140102, ArithmeticUtil.round(ArithmeticUtil.doSub(
                                    feeDetail.getFeeAmt(), jyIncomeFee), NumberEnum.MONEY.getCode(), null));
                        } else {
                            accountAmountMap.put(BookConstant.NETAMT_D_ALT0202, ArithmeticUtil.round(ArithmeticUtil.doSub(
                                    feeDetail.getFeeAmt(), jyIncomeFee), NumberEnum.MONEY.getCode(), null));
                        }
                        /*modify by hxy 20200324 end 通道和直投增值税的科目不一致*/
                    }
                    /**add by taoran 20200107 end**/
                }
                // ELSE IF(明细.一次性费用类型 == "地方教育附加税"）
                else if (feeDetail.getFeeType().equals(DictCodeList.附加税类型_地方教育税.getValue())) {
                    // 金额元素.地方教育税税额 = 费用金额
                    //accountAmountMap.put(BookConstant.VAT_LEDU, feeDetail.getFeeAmt());
                	String baseSubjectNature = DictCodeBaseSubjectNatrueNew.应交税费_应交附加税.getValue();
                    /**add by taoran 20200107 start**/
                    //调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
                    SubjectBalance subjectBalance = this.getSubjectBalance(acctEntity.getBooksetId(), acctEntity.getBokDetailId(), acctEntity.getBookDate(), 
                    		dealNow.getMdate(), DictCodeList.余额方向_贷方.getValue(), baseSubjectNature);
                    //定义 变量.应交地方教育附加税 = 返回值.科目余额.本币余额
                    BigDecimal dfIncomeFee = BigDecimal.ZERO;
                    if(subjectBalance != null) {
                    	//定义 变量.应交地方教育附加税 = 返回值.科目余额.本币余额
                        dfIncomeFee = subjectBalance.getbAmt();
                        /**add by taoran 20200120 start**/
                        // 金额元素.地方教育税税额 = 变量.应交地方教育附加税
                        accountAmountMap.put(BookConstant.VAT_LEDU, dfIncomeFee);
                        /**add by taoran 20200120 end**/
                    }
                    //(变量.应交地方教育附加税 > 费用金额)
                    if(dfIncomeFee.compareTo(feeDetail.getFeeAmt()) > 0) {
                        //变量.缴税差额-负 += （变量.应交地方教育附加税- 费用金额）
                        feeAmt_f = ArithmeticUtil.doAdd(feeAmt_f, ArithmeticUtil.doSub(dfIncomeFee, feeDetail.getFeeAmt()));
                    }
                    else if(dfIncomeFee.compareTo(feeDetail.getFeeAmt()) < 0){
                        //金额元素.地方教育附加税差额 = 费用金额 - 变量.应交地方教育附加税
                        	/*accountAmountMap.put(BookConstant.NETAMT_D_64140103, ArithmeticUtil.round(ArithmeticUtil.doSub(
                        			feeDetail.getFeeAmt(), dfIncomeFee), NumberEnum.MONEY.getCode(), null));*/
                        /*modify by hxy 20200324 start 通道和直投增值税的科目不一致*/
                        if (DictCodeListExt1.金融产品类型_通道.getValue().equals(dealNow.getFinprodType())) {
                            accountAmountMap.put(BookConstant.NETAMT_D_64140103, ArithmeticUtil.round(ArithmeticUtil.doSub(
                                    feeDetail.getFeeAmt(), dfIncomeFee), NumberEnum.MONEY.getCode(), null));
                        } else {
                            accountAmountMap.put(BookConstant.NETAMT_D_ALT0302, ArithmeticUtil.round(ArithmeticUtil.doSub(
                                    feeDetail.getFeeAmt(), dfIncomeFee), NumberEnum.MONEY.getCode(), null));
                        }
                        /*modify by hxy 20200324 end 通道和直投增值税的科目不一致*/
                    }
                    /**add by taoran 20200107 end**/
                }
                /**ADD BY YANYA ON 20190918 增加附加税类型_水利基金税 **/
                else if (feeDetail.getFeeType().equals(DictCodeList.附加税类型_水利基金税.getValue())) {
                    // 金额元素.水利基金税税额 = 费用金额
                    //accountAmountMap.put(BookConstant.VAT_WATER, feeDetail.getFeeAmt());
                	String baseSubjectNature = DictCodeBaseSubjectNatrueNew.应交税费_应交附加税.getValue();
                    /**add by taoran 20200107 start**/
                    //调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
                    SubjectBalance subjectBalance = this.getSubjectBalance(acctEntity.getBooksetId(), acctEntity.getBokDetailId(), acctEntity.getBookDate(), 
                    		dealNow.getMdate(), DictCodeList.余额方向_贷方.getValue(), baseSubjectNature);
                    //定义 变量.应交水利基金税 = 返回值.科目余额.本币余额
                    BigDecimal slIncomeFee = BigDecimal.ZERO;
                    if(subjectBalance != null) {
                    	//定义 变量.应交水利基金税 = 返回值.科目余额.本币余额
                        slIncomeFee = subjectBalance.getbAmt();
                        /**add by taoran 20200120 start**/
                        // 金额元素.水利基金税税额 = 变量.应交水利基金税
                        accountAmountMap.put(BookConstant.VAT_WATER, slIncomeFee);
                        /**add by taoran 20200120 end**/
                    }
                    //(变量.应交水利基金税 > 费用金额)
                    if(slIncomeFee.compareTo(feeDetail.getFeeAmt()) > 0) {
                        //变量.缴税差额-负 += （变量.应交水利基金税- 费用金额）
                        feeAmt_f = ArithmeticUtil.doAdd(feeAmt_f, ArithmeticUtil.doSub(slIncomeFee, feeDetail.getFeeAmt()));
                    }
                    else if(slIncomeFee.compareTo(feeDetail.getFeeAmt()) < 0){
                        //金额元素.水利基金税差额 = 费用金额 - 变量.应交水利基金税
                        /*	accountAmountMap.put(BookConstant.NETAMT_D_64140104, ArithmeticUtil.round(ArithmeticUtil.doSub(
                        			feeDetail.getFeeAmt(), slIncomeFee), NumberEnum.MONEY.getCode(), null));*/
                        /*modify by hxy 20200324 start 通道和直投增值税的科目不一致*/
                        if (DictCodeListExt1.金融产品类型_通道.getValue().equals(dealNow.getFinprodType())) {
                            accountAmountMap.put(BookConstant.NETAMT_D_64140104, ArithmeticUtil.round(ArithmeticUtil.doSub(
                                    feeDetail.getFeeAmt(), slIncomeFee), NumberEnum.MONEY.getCode(), null));
                        } else {
                            accountAmountMap.put(BookConstant.NETAMT_D_ALT0402, ArithmeticUtil.round(ArithmeticUtil.doSub(
                                    feeDetail.getFeeAmt(), slIncomeFee), NumberEnum.MONEY.getCode(), null));
                        }
                        /*modify by hxy 20200324 end 通道和直投增值税的科目不一致*/
                    }
                    /**add by taoran 20200107 end**/
                }
            }
            
            /**add by taoran 20200107 start**/
            //金额元素.其他收入 = 变量.缴税差额-负
            accountAmountMap.put(BookConstant.NETAMT_C_QTYWSR_QT, ArithmeticUtil.round(feeAmt_f, NumberEnum.MONEY.getCode(), null));
            /**add by taoran 20200107 end**/
            
            // 币种元素场景赋值
            for (String key : accountAmountMap.keySet()) {
            	accountCurrencyMap.put(key, dealNow.getCcy());
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
     * 调用公用方法：【科目余额查询 - 期待方向】，获取科目余额信息中的科目余额数据
     * 
     * @param booksetId 账套代码
     * @param bokDetailId 账务明细代码
     * @param happenDate 发生日
     * @param expBalanceDirect 期待余额方向
     * @param bookDate 入账日
     * @param baseSubjectNature 基础科目性质
     * @return subjectBalance 科目余额信息
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalance(String booksetId, String bokDetailId, Date happenDate, Date bookDate,
                                             String expBalanceDirect, String baseSubjectNature) {
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

}
