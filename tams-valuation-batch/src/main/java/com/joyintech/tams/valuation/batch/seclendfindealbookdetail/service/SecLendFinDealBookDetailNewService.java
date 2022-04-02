/*
 * 文件名：SecLendFinDealBookDetailService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：ZhangYangFei
 * 修改时间：2018年10月29日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.valuation.batch.seclendfindealbookdetail.service;

import com.google.common.collect.Maps;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.BookLatestInfoDto;
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
import com.joyintech.tams.valuation.batch.common.sect1handle.entity.DealHappenAmtDto;
import com.joyintech.tams.valuation.batch.common.sect1handle.entity.SecT1HandleParamterDto;
import com.joyintech.tams.valuation.batch.common.sect1handle.entity.SecT1HandleResultDto;
import com.joyintech.tams.valuation.batch.common.sect1handle.service.SecT1HandleService;
import com.joyintech.tams.valuation.batch.secdealaccounts.dto.DealParamterDto;
import com.joyintech.tams.valuation.batch.seclendfindealbookdetail.dto.AmountElementDTO;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.FinCashPlanDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.constants.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;

/**
 * 债券借贷标的债券交易-账务明细批处理service
 * 
 * @author ZhangYangFei
 * @version 1.0 2018年10月29日
 * @see SecLendFinDealBookDetailNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class SecLendFinDealBookDetailNewService {

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
     * 计算类共通方法
     */
    @Autowired
    private CalculationCommonService calculationCommonService;

    /**
     * 债券共通
     */
    @Autowired
    private SecT1HandleService secT1HandleService;

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

        // 交易基础信息列表
        List<FinProductDealInfoComDTO> listFinProductDealInfoComDTONow = acctEntity.getListFinProductDealInfoComDTO();
        Long taskPri = listFinProductDealInfoComDTONow.get(0).getTaskPri();// 任务优先级（交易基础信息列表的任务优先级都是一致的，故取第一个）

        // 1.数据准备
        // 定义 变量.成本科目余额 = 0
        BigDecimal cbkmye = BigDecimal.ZERO;
        // 定义 变量.成本科目数量 = 0
        BigDecimal cbkmsl = BigDecimal.ZERO;
        // 定义 变量.应计利息科目余额 = 0
        BigDecimal yjlikmye = BigDecimal.ZERO;
        // 定义 变量.公允价值变动科目余额 = 0
        BigDecimal gyjzbdkmye = BigDecimal.ZERO;

        // 2.调用共通查询发生日最新余额
        // 交易期待方向：贷方
        String expBalanceDirect = DictCodeList.余额方向_贷方.getValue();

        // 2.1.调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
        SubjectBalance yfrcflxSubjectBalance = getSubjectBalance(acctEntity, expBalanceDirect,
            DictCodeBaseSubjectNatrueNew.其他应付款_利息.getValue(), taskPri);
        // 变量.应付融出方利息 = DTO.科目余额
        BigDecimal yfrcflx = yfrcflxSubjectBalance.getBalance();

        // 2.1.调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
//        SubjectBalance yfrcfbjSubjectBalance = getSubjectBalance(acctEntity, expBalanceDirect,
//            DictCodeBaseSubjectNatrueNew.其他应付款_本金.getValue(), taskPri);
//        // 变量.应付融出方本金 = DTO.科目余额
//        BigDecimal yfrcfbj = yfrcfbjSubjectBalance.getBalance();

        // 2.2.调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
        SubjectBalance cbkmSubjectBalance = getSubjectBalance(acctEntity, expBalanceDirect,
            DictCodeBaseSubjectNatrueNew.交易性金融负债_成本.getValue(), taskPri);
        // 变量.成本科目余额 = DTO.科目余额
        cbkmye = cbkmSubjectBalance.getBalance();
        // 变量.成本科目数量 = DTO.数量
        cbkmsl = cbkmSubjectBalance.getNum();

        // 2.3.调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
        SubjectBalance gyjzSubjectBalance = getSubjectBalance(acctEntity, expBalanceDirect,
            DictCodeBaseSubjectNatrueNew.交易性金融负债_公允价值变动.getValue(), taskPri);
        // 变量.公允价值变动科目余额 = DTO.科目余额
        gyjzbdkmye = gyjzSubjectBalance.getBalance();

        // 2.4.调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
        SubjectBalance yjlikmyeSubjectBalance = getSubjectBalance(acctEntity, expBalanceDirect,
            DictCodeBaseSubjectNatrueNew.交易性金融负债_应计利息.getValue(), taskPri);
        // 变量.应计利息科目余额 = DTO.科目余额
        yjlikmye = yjlikmyeSubjectBalance.getBalance();

        // 2.5 T+1交易变量定义
        SecT1HandleParamterDto paramT1 = new SecT1HandleParamterDto();

        // 类型定义
            // 发生额集：
                // 发生公允价值变动
                // 发生应计利息
                // 发生成本
                // 发生数量
        // DealHappenAmtDto.java 即类型定义，包含发生额集（发生公允价值变动，发生应计利息，发生成本， 发生数量）
        
        // 定义 变量.T+1买入交易
            // 类型：MAP(Key:日期，Value：发生额集[即DealHappenAmtDto])
        paramT1.setT1BuyDeal(new HashMap<>());
        // 定义 变量.T+1卖出交易
            // 类型：MAP(Key:日期，Value：发生额集)
        paramT1.setT1SellDeal(new HashMap<>());

        // 定义 变量.T+1发生公允价值变动
        paramT1.setGyjzbdSubjectBalance(BigDecimal.ZERO);
        // 定义 变量.T+1发生应计利息
        paramT1.setYjlxSubjectBalance(BigDecimal.ZERO);
        // 定义 变量.T+1发生成本
        paramT1.setCbSubjectBalance(BigDecimal.ZERO);
        // 定义 变量.T+1发生数量
        paramT1.setNumBalance(BigDecimal.ZERO);
        
        // 2.6 账套核算参数查询
        /*
        // 调用账务类共通（DTO.账套代码, 参数项：如下）
        List<String> paramCodeList = new ArrayList<String>();
        paramCodeList.add(DictCodeList.账套配置表参数项_债券交易费用损益计入方式.getValue());
        List<BokBooksetExt> listBooksetExt = accountingCommonService.getValBokBooksetExt(
            acctEntity.getBooksetId(), paramCodeList);
        // 定义 变量.费用损益计入方式 = 返回值
        String calType = "";
        if (!CollectionUtils.isEmpty(listBooksetExt)) {
            // 获取第一条即可
            calType = listBooksetExt.get(0).getParamValue();
        }
        */
        // 定义 变量.费用损益计入方式 = 固定为"计入费用支出"   <-- 设计变更 zhangyf 20181113
        String calType = DictCodeList.债券交易费用损益计入方式_计入费用支出.getValue();
        
        // 3.处理交易
        // 3.1.标的类金融产品信息;标的信息查询，调用共通
        // 定义 变量.标的信息
        TargetFinProduct rstBaseinfo;
        
        String ps;//交易方向
        AccountDriver ad;
        
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        Map<String, Object> accountSceneMap;
        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
        Map<String, BigDecimal> accountAmountMap;
        // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
        Map<String, Object> accountCurrencyMap;
        // 会计场景代码
        String tableId = "";
        // 备注
        String book_remark = "";
        // 返回值.应计利息
        BigDecimal returnYglix = BigDecimal.ZERO;
        // 变量.应计利息余额
        BigDecimal yjlixye = BigDecimal.ZERO;
        
        // 循环【交易类账务处理结果.交易明细列表】进行逻辑处理
        for (FinProductDealInfoComDTO dealDto : listFinProductDealInfoComDTONow) {
            
            rstBaseinfo = myGetTargetInfo(dealDto);
            
            ps = dealDto.getPs();
            accountSceneMap = getAccountSceneMap(acctEntity, dealDto,rstBaseinfo);
            accountAmountMap = new HashMap<>();
            // 基础科目性质
            Map<String, Map<String, Object>> accountNatrueMap = new HashMap<>();
            Map<String, Object> accountNatrueBranchMap = new HashMap<>();
            // if（交易方向 = "融入债券收息" or 交易方向 = '融入债券派息到账' OR 交易方向 = '收息到账’）
            if (DictCodeList.交易方向_融入债券收息.getValue().equals(ps) || 
                DictCodeList.交易方向_融入债券派息到账.getValue().equals(ps) ||
                DictCodeList.交易方向_收息到账.getValue().equals(ps)) {
                if(DictCodeList.交易方向_融入债券收息.getValue().equals(ps)){
                    // ad.会计场景代码="债券借贷-融入期间标的债券收息"
                    tableId = DictCodeListTableId.会计场景代码_债券借贷_借入期间标的债券收息.getValue();
                    // 备注 <- "债券借贷-融入期间标的债券收息交易登账"
                    book_remark = "债券借贷-融入期间标的债券收息交易登账";
                    // 实收金额 <-交易明细.交易金额
                    putAccountAmountMap(accountAmountMap,BookConstant.REALAMT,dealDto.getTradeAmt());

                    accountNatrueBranchMap.put(BookConstant.BRANCH, dealDto.getBranch());
                    accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.其他应付款_利息.getValue(), accountNatrueBranchMap);

                }
                else{
                    // ad.会计场景代码="资金收款-买断式逆回购期间债券派息"
                    tableId = DictCodeListTableId.会计场景代码_资金收款_买断式逆回购期间债券派息.getValue();
                    // 备注 <- "债券借贷-融入期间标的债券收息交易登账"
                    book_remark = "资金收款-买断式逆回购期间债券派息交易登账";
                    // 其他应收款-利息  <- 交易明细.交易金额
                    putAccountAmountMap(accountAmountMap,BookConstant.OTHER_INT_AMT,dealDto.getTradeAmt());
                    accountNatrueBranchMap.put(BookConstant.BRANCH, dealDto.getBranch());
                    accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.应收利息_证券.getValue(), accountNatrueBranchMap);
                    // 实收金额 <-交易明细.交易金额
                    // add by weipeng on 20220225 区分资金账户金额元素 start
//                    putAccountAmountMap(accountAmountMap,BookConstant.DEALAMT,dealDto.getTradeAmt());
                    // 定义 变量.金额元素, 默认 = 活期存款-托管户
                    String 交易金额记账元素 =  BookConstant.DEALAMT_HQ;
                    // 本方资金账户类型 或 对方资金账户类型对应资金账户类型，赋值对应金额元素
                    // 券商资金账户现划分在结算备付金科目性质下
                    if (DictCodeListExt1.资金账户类型_结算备付金账户.getValue().equals(dealDto.getAcctType())
                            || DictCodeListExt1.资金账户类型_券商资金账户.getValue().equals(dealDto.getAcctType())){
                        交易金额记账元素 = BookConstant.DEALAMT_JS;
                    }
                    putAccountAmountMap(accountAmountMap,交易金额记账元素,dealDto.getTradeAmt());
                    // add by weipeng on 20220225 区分资金账户金额元素 end
                }
                // 变量.应付融出方利息 = 变量.应付融出方利息 + 实收金额
                yfrcflx = ArithmeticUtil.doAdd(yfrcflx, dealDto.getTradeAmt());
            }
            // else if（交易方向 = "融入债券利息返还"）
            else if(DictCodeList.交易方向_利息返还.getValue().equals(ps)){
                // ad.会计场景代码="债券借贷-融入期间标的债券付息"
                tableId = DictCodeListTableId.会计场景代码_债券借贷_借入期间标的债券付息.getValue();
                // 备注 <- "债券借贷-融入期间标的债券付息交易登账"
                book_remark = "债券借贷-融入期间标的债券付息交易登账";
                /*
                // 其他应付款 <- 变量.应付融出方利息 // delete，设计变更 zhangyf 20181204
                putAccountAmountMap(accountAmountMap,BookConstant.OTHER_AMT,yfrcflx);
                */
                // 其他应付款 <- 交易信息.交易金额   // add，设计变更 zhangyf 20181204
                putAccountAmountMap(accountAmountMap,BookConstant.OTHER_AMT,dealDto.getTradeAmt());
                
                // 实收金额 <-交易明细.交易金额
                putAccountAmountMap(accountAmountMap,BookConstant.REALAMT,dealDto.getTradeAmt());
                /*
                // 变量.轧差金额<-实付金额 - 其他应付款   // delete，设计变更 zhangyf 20181204
                // BigDecimal zcje = ArithmeticUtil.doSub(dealDto.getTradeAmt(), yfrcflx);
                */
                // 变量.轧差金额 : 0 // add，设计变更 zhangyf 20181204
                BigDecimal zcje = BigDecimal.ZERO;
                zcje = ArithmeticUtil.round(zcje, NumberEnum.MONEY);
                /* // delete， 设计变更 zhangyf 20181204
                if(BigDecimal.ZERO.compareTo(zcje) < 0){
                    // 轧差金额_借<-变量.轧差金额
                    putAccountAmountMap(accountAmountMap,BookConstant.DSCLOSS_D, zcje);
                }else{
                    // 轧差金额_贷 <-变量.轧差金额 * -1
                    putAccountAmountMap(accountAmountMap,BookConstant.DSCLOSS_C, ArithmeticUtil.multiply(ifNull2Zero(zcje),new BigDecimal("-1")));
                }
                */
                // 变量.应付融出方利息 = 变量.应付融出方利息 - 其他应付款
                yfrcflx = ArithmeticUtil.doSub(yfrcflx,yfrcflx);

                accountNatrueBranchMap.put(BookConstant.BRANCH, dealDto.getBranch());
                accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.其他应付款_利息.getValue(), accountNatrueBranchMap);

            }
            // else if（交易方向 = "卖空债券结息" OR 交易方向 = "收息"）
            else if(DictCodeList.交易方向_卖空债券结息.getValue().equals(ps) || DictCodeList.交易方向_收息.getValue().equals(ps)){
                // ad.会计场景代码="债券借贷-融入债券卖出后收息"
                tableId = DictCodeListTableId.会计场景代码_债券借贷_融入债券卖出后收息.getValue();
                // 备注 <- "债券借贷-融入债券卖出后收息交易登账"
                book_remark = "债券借贷-融入债券卖出后收息交易登账";
                // 调用 金融负债收息处理 共通处理。返回值： 应计利息
                returnYglix = BigDecimal.ZERO;
                returnYglix = this.getFinLiabilIntHandle(rstBaseinfo, acctEntity, dealDto);
                /**modify by wujie on 20210109 start remark:全局变量应计利息科目余额计算错误问题修复
                // 变量.应计利息科目余额 = 变量.应计利息余额 - 返回值.应计利息
                yjlikmye = ArithmeticUtil.doSub(yjlixye, returnYglix);
                **/
                // 变量.应计利息科目余额 = 变量.应计利息余额 - 返回值.应计利息
                yjlikmye = ArithmeticUtil.doSub(yjlikmye, returnYglix);
                /**moidfy by wujie on 20210109 end**/
                
                // 应付利息 <- 返回值.应计利息
                putAccountAmountMap(accountAmountMap,BookConstant.INTAMT,returnYglix);
                // 变量.应付融出方利息 = 变量.应付融出方利息 + 返回值.应付利息                                  
                yfrcflx = ArithmeticUtil.doAdd(yfrcflx, returnYglix);
                // 分支序号 <- 交易明细.分支序号
                accountNatrueBranchMap.put(BookConstant.BRANCH, dealDto.getBranch());
                accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.其他应付款_利息.getValue(), accountNatrueBranchMap);
            }
            // else if（交易方向 = "融入债券还本" or 交易方向 = '融入债券派本到账' OR 交易方向 = "还本到账"）
            else if(DictCodeList.交易方向_融入债券还本.getValue().equals(ps) || 
                DictCodeList.交易方向_融入债券派本到账.getValue().equals(ps) ||
                DictCodeList.交易方向_还本到账.getValue().equals(ps)){
                
                if(DictCodeList.交易方向_融入债券还本.getValue().equals(ps)){
                    // ad.会计场景代码="债券借贷-融入期间标的债券还本"
                    tableId = DictCodeListTableId.会计场景代码_债券借贷_融入期间标的债券还本.getValue();
                    // 备注 <- "债券借贷-融入期间标的债券还本交易登账"
                    book_remark = "债券借贷-融入期间标的债券还本交易登账";
                    // 分支序号 <- 交易明细.分支序号
                    accountNatrueBranchMap.put(BookConstant.BRANCH, dealDto.getBranch());
                    accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.其他应付款_本金.getValue(), accountNatrueBranchMap);
                }
                else{
                    // ad.会计场景代码="资金收款-买断式逆回购期间债券派息"
                    tableId = DictCodeListTableId.会计场景代码_资金收款_买断式逆回购期间债券派息.getValue();
                    // 备注 <- "债券借贷-融入期间标的债券还本交易登账"
                    book_remark = "资金收款-买断式逆回购期间债券派息交易登账";
                    // 其他应收款-本金 <- 交易明细.交易金额
                    putAccountAmountMap(accountAmountMap,BookConstant.OTHER_COST_AMT,dealDto.getTradeAmt());
                    accountNatrueBranchMap.put(BookConstant.BRANCH, dealDto.getBranch());
                    accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.清算资金往来.getValue(), accountNatrueBranchMap);
                }
                // 实收金额 <- 交易明细.交易金额
                putAccountAmountMap(accountAmountMap,BookConstant.DEALAMT,dealDto.getTradeAmt());
//                // 变量.应付融出方本金 = 变量.应付融出方本金 + 实收金额
//                yfrcfbj = ArithmeticUtil.doAdd(yfrcfbj, dealDto.getTradeAmt());
            }
            // else if（交易方向 = "本金返还"）
            else if(DictCodeList.交易方向_本金返还.getValue().equals(ps)){
                // ad.会计场景代码="债券借贷-融入期间标的券本金支付"
                tableId = DictCodeListTableId.会计场景代码_债券借贷_融入期间标的券本金支付.getValue();
                // 备注 <- "债券借贷-融入期间标的券本金支付交易登账"
                book_remark = "债券借贷-融入期间标的券本金支付交易登账";
                // 交易金额 <- 交易信息.交易金额
                putAccountAmountMap(accountAmountMap,BookConstant.DEALAMT,dealDto.getTradeAmt());
//                // 变量.应付融出方本金 = 变量.应付融出方本金 - 实收金额
//                yfrcfbj = ArithmeticUtil.doAdd(yfrcfbj, dealDto.getTradeAmt());


                accountNatrueBranchMap.put(BookConstant.BRANCH, dealDto.getBranch());
                accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.其他应付款_本金.getValue(), accountNatrueBranchMap);

            }
            // else if（交易方向 = "卖空债券还本" OR 交易方向 = "还本"）
            else if (DictCodeList.交易方向_卖空债券还本.getValue().equals(ps) ||
            		DictCodeList.交易方向_还本.getValue().equals(ps)
            		//add by wujie on 20210109 start remark:理论结息日结转，标的券处理交易还本交易有区分还本、到期
            		|| DictCodeList.交易方向_到期.getValue().equals(ps)
            		//add by wujie on 20210109 end 
            		) {
                // ad.会计场景代码="债券借贷-融入债券卖出后还本"
                tableId = DictCodeListTableId.会计场景代码_债券借贷_融入债券卖出后还本.getValue();
                // 备注 <- "债券借贷-融入债券卖出后还本交易登账"
                book_remark = "债券借贷-融入债券卖出后还本交易登账";
                // 分支序号 <- 交易明细.分支序号
                accountNatrueBranchMap.put(BookConstant.BRANCH, dealDto.getBranch());
                accountNatrueMap.put(DictCodeBaseSubjectNatrueNew.其他应付款_本金.getValue(), accountNatrueBranchMap);

                // accountAmountMap设置如下：
                // 调用 金融负债还本到期处理 共通处理，返回值:金额元素dto 
                // 定义 金额元素DTO
                AmountElementDTO amountElementDTO = this.getFinLiabilRepayHandle(rstBaseinfo,
                    acctEntity, dealDto);

                // 成本金额 <- DTO.冲减成本
                putAccountAmountMap(accountAmountMap,BookConstant.DEALAMT,amountElementDTO.getVarCjcb());
                // if (DTO.冲减公允价值变动 >= 0 )
                if (BigDecimal.ZERO.compareTo(amountElementDTO.getVarCjgyjzbd()) <= 0) {
                    // 公允价值变动_借 <- DTO.冲减公允价值变动
                    putAccountAmountMap(accountAmountMap,BookConstant.FVTPL_INTADJ_D,amountElementDTO.getVarCjgyjzbd());
                    // 公允价值变动损益_贷 <- DTO.冲减公允价值变动
                    putAccountAmountMap(accountAmountMap,BookConstant.FLOAT_1_C,amountElementDTO.getVarCjgyjzbd());
                }
                else {
                    // 公允价值变动_贷 <- DTO.冲减公允价值变动 * -1
                    putAccountAmountMap(accountAmountMap,BookConstant.FVTPL_INTADJ_C,amountElementDTO.getVarCjgyjzbd().negate());
                    // 公允价值变动损益_借 <- DTO.冲减公允价值变动 * -1
                    putAccountAmountMap(accountAmountMap,BookConstant.FLOAT_1_D,amountElementDTO.getVarCjgyjzbd().negate());
                }
                // if (DTO.投资收益 >= 0 )
                if (BigDecimal.ZERO.compareTo(amountElementDTO.getVarTzsy()) <= 0) {
                    // 转卖差价收益_借 <- DTO.投资收益
                    putAccountAmountMap(accountAmountMap,BookConstant.RESELL_INAMT_D,amountElementDTO.getVarTzsy());
                }
                else {
                    // 转卖差价收益_贷 <- DTO.投资收益 * -1
                    putAccountAmountMap(accountAmountMap,BookConstant.RESELL_INAMT_C,amountElementDTO.getVarTzsy().negate());
                }
                // 本金金额 <- DTO.还本本金
                putAccountAmountMap(accountAmountMap,BookConstant.COST,amountElementDTO.getVarHbbj());
                
//                // 变量.应付融出方本金 = 变量.应付融出方本金 + 本金金额
//                yfrcfbj = ArithmeticUtil.doAdd(yfrcfbj,amountElementDTO.getVarHbbj());
                // 变量.成本科目余额 = 变量.成本科目余额 - 成本金额
                cbkmye = ArithmeticUtil.subtract(cbkmye,amountElementDTO.getVarCjcb());
                // 变量.公允价值变动科目余额 = 变量.公允价值变动科目余额 - DTO.冲减公允价值变动
                gyjzbdkmye = ArithmeticUtil.subtract(gyjzbdkmye,amountElementDTO.getVarCjgyjzbd());
            }
            // else if（交易方向 = "融入债券卖出" OR 交易方向 = "卖出"）
            else if(DictCodeList.交易方向_融入债券卖出.getValue().equals(ps) ||
            		DictCodeList.交易方向_卖出.getValue().equals(ps)){
                // ad.会计场景代码="债券借贷-融入债券卖出"
                tableId = DictCodeListTableId.会计场景代码_债券借贷_借入债券卖出.getValue();
                // 备注 <-"债券借贷-融入债券卖出交易登账"
                book_remark = "债券借贷-融入债券卖出交易登账";
                // 在途日期
                Date onwayDate;
                // if（交易明细.交易场所 = "银行间"）
                if (DictCodeListExt1.估值交易市场_银行间.getValue().equals(dealDto.getTradeMarket())) {
                    // 在途日期 <- 交易明细.交易日 == 交割日？Null：交割日
                    onwayDate = DateUtil.compareDate(dealDto.getTradeDate(), dealDto.getSettleDate())==0 ? null : dealDto.getSettleDate();
                }
                else {
                    // 在途日期 <- 交易明细.交易日 + 1
                    onwayDate = DateUtil.addDay(dealDto.getTradeDate(), 1);
                }
                accountSceneMap.put(BookConstant.ONWAY_DATE,onwayDate);

                //登记托管机构                    <-      变量.登记托管机构
                accountSceneMap.put(BookConstant.REGIST_ORG, dealDto.getRegistOrg());

                // accountAmountMap设置如下：
                // 发生数量 <- 交易明细.交易份额
                putAccountAmountMap(accountAmountMap,BookConstant.HAPPEN_NUMBER,dealDto.getShareAmt());//设计者确认用[HAPPEN_NUMBER]
                // 净价总额 <- 交易明细.交易本金
                // putAccountAmountMap(accountAmountMap,BookConstant.TOTALAMT,dealDto.getPrinAmt());
                putAccountAmountMap(accountAmountMap,BookConstant.TOTALAMT,dealDto.getCpriceAmt());// 净价总额
                // 实收利息 <- 交易明细.利息总额
                putAccountAmountMap(accountAmountMap,BookConstant.INTAMT,dealDto.getIntAmt());
                // 交易金额 <- 交易明细.交易金额
                putAccountAmountMap(accountAmountMap,BookConstant.DEALAMT,dealDto.getTradeAmt());
                
                // 调用 共通【费用处理共通】  参考本设计书sheet【费用处理共通】
                // 定义       变量.交易各种费用 = 上述返回值
                DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
                    dealDto.getValTradeFeeDetailList(),dealDto.getProdId(),dealDto.getPortfolioId());

                //变量.交易各种费用.印花税
                putAccountAmountMap(accountAmountMap,BookConstant.STAMPTAX,dealTypesFeeDto.getStampDuty());
                //变量.交易各种费用.经手费
                putAccountAmountMap(accountAmountMap,BookConstant.HANDFEE,dealTypesFeeDto.getBrokerage());
                //变量.交易各种费用.证管费
                putAccountAmountMap(accountAmountMap,BookConstant.CERTIFEE,dealTypesFeeDto.getCertificateFee());
                //变量.交易各种费用.结算费
                putAccountAmountMap(accountAmountMap,BookConstant.SETTLEFEE,dealTypesFeeDto.getClearingFee());
                //变量.交易各种费用.佣金
                putAccountAmountMap(accountAmountMap,BookConstant.COMMISSION,dealTypesFeeDto.getCommission());
                //变量.交易各种费用.交易手续费
                putAccountAmountMap(accountAmountMap,BookConstant.TRADEFEE,dealTypesFeeDto.getTransactionFee());
                //变量.交易各种费用.结算手续费
                putAccountAmountMap(accountAmountMap,BookConstant.SETTLEMENTFEE,dealTypesFeeDto.getSettlementFee());
                
                // IF (变量.费用损益计入方式 = '计入投资收益') {
                if(DictCodeList.债券交易费用损益计入方式_计入投资收益.getValue().equals(calType)){
                    // 交易费用 <- 变量.交易各种费用.交易费用
                    putAccountAmountMap(accountAmountMap,BookConstant.FEE,dealTypesFeeDto.getFeeAmt());
                }
                else {
                    // 印花税支出 = 变量.交易各种费用.印花税+变量.交易各种费用.随交易支付_印花税
                    putAccountAmountMap(accountAmountMap, BookConstant.STAMPTAX_OUT,
                        ArithmeticUtil.doAdd(dealTypesFeeDto.getStampDuty(),
                            dealTypesFeeDto.getStampDutyForDealPay()));
                    // 经手费支出 = 变量.交易各种费用.经手费+变量.交易各类费用.随交易支付_经手费
                    putAccountAmountMap(accountAmountMap, BookConstant.HANDFEE_OUT,
                        ArithmeticUtil.doAdd(dealTypesFeeDto.getBrokerage(),
                            dealTypesFeeDto.getBrokerageForDealPay()));
                    // 证管费支出 = 变量.交易各种费用.证管费+变量.交易各种费用.随交易支付_证管费
                    putAccountAmountMap(accountAmountMap, BookConstant.CERTIFEE_OUT,
                        ArithmeticUtil.doAdd(dealTypesFeeDto.getCertificateFee(),
                            dealTypesFeeDto.getCertificateFeeForDealPay()));
                    // 结算费支出 = 变量.交易各种费用.结算费+变量.交易各种费用.随交易支付_结算费
                    putAccountAmountMap(accountAmountMap, BookConstant.SETTLEFEE_OUT,
                        ArithmeticUtil.doAdd(dealTypesFeeDto.getClearingFee(),
                            dealTypesFeeDto.getClearingFeeForDealPay()));
                    // 佣金支出 = 变量.交易各种费用.佣金+变量.交易各种费用.随交易支付_佣金
                    putAccountAmountMap(accountAmountMap, BookConstant.COMMISSION_OUT,
                        ArithmeticUtil.doAdd(dealTypesFeeDto.getCommission(),
                            dealTypesFeeDto.getCommissionForDealPay()));
                    // 交易手续费支出 = 变量.交易各种费用.交易手续费+变量.交易各种费用.随交易支付_交易手续费
                    putAccountAmountMap(accountAmountMap, BookConstant.TRADEFEE_OUT,
                        ArithmeticUtil.doAdd(dealTypesFeeDto.getTransactionFee(),
                            dealTypesFeeDto.getTransactionFeeForDealPay()));
                    // 结算手续费支出 = 变量.交易各种费用.结算手续费+变量.交易各种费用.随交易支付_结算手续费
                    putAccountAmountMap(accountAmountMap, BookConstant.SETTLEMENTFEE_OUT,
                        ArithmeticUtil.doAdd(dealTypesFeeDto.getSettlementFee(),
                            dealTypesFeeDto.getSettlementFeeForDealPay()));
                }
                // 场景元素设值各种费用的账务明细代码
                // accountAmountMap再设置如下
                // 印花税账务明细代码 <- 变量.交易各种费用.印花税账务明细代码
                accountSceneMap.put(BookConstant.BOK_DETAIL_STAMPTAX, dealTypesFeeDto.getStampDutyCode());
                // 经手费账务明细代码 <- 变量.交易各种费用.经手费账务明细代码
                accountSceneMap.put(BookConstant.BOK_DETAIL_HANDFEE, dealTypesFeeDto.getBrokerageCode());
                // 证管费账务明细代码 <- 变量.交易各种费用.证管费账务明细代码
                accountSceneMap.put(BookConstant.BOK_DETAIL_CERTIFEE,
                    dealTypesFeeDto.getCertificateFeeCode());
                // 结算费账务明细代码 <- 变量.交易各种费用.结算费账务明细代码
                accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEFEE,
                    dealTypesFeeDto.getClearingFeeCode());
                // 佣金账务明细代码 <- 变量.交易各种费用.佣金账务明细代码
                accountSceneMap.put(BookConstant.BOK_DETAIL_COMMISSION,
                    dealTypesFeeDto.getCommissionCode());
                // 交易手续费账务明细代码 <- 变量.交易各种费用.交易手续费账务明细代码
                accountSceneMap.put(BookConstant.BOK_DETAIL_TRADEFEE,
                    dealTypesFeeDto.getTransactionFeeCode());
                // 结算手续费账务明细代码 <- 变量.交易各种费用.结算手续费账务明细代码
                accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEMENTFEE,
                    dealTypesFeeDto.getSettlementFeeCode());
                
                // 全局变量的更新
                // 变量.成本科目余额 = 变量.成本科目余额 + 交易明细.交易本金 
                cbkmye = ArithmeticUtil.doAdd(cbkmye,dealDto.getPrinAmt());
                // 变量.成本科目数量 = 变量.成本科目数量 + 交易明细.交易份额
                cbkmsl = ArithmeticUtil.doAdd(cbkmsl,dealDto.getShareAmt());
                // 变量.应计利息科目余额 = 变量.应计利息科目余额 + 交易明细.利息总额
                yjlikmye = ArithmeticUtil.doAdd(yjlikmye,dealDto.getIntAmt());
                
                // if（场景元素.在途日期 != 空）                               
                if(null != accountSceneMap.get(BookConstant.ONWAY_DATE)){
                    // 变量.T+1发生应计利息 += 交易明细.利息总额
                    paramT1.setYjlxSubjectBalance(
                        ArithmeticUtil.doAdd(paramT1.getYjlxSubjectBalance(), dealDto.getIntAmt()));
                    // 变量.T+1发生成本 += 交易明细.交易本金
                    paramT1.setCbSubjectBalance(
                        ArithmeticUtil.doAdd(paramT1.getCbSubjectBalance(), dealDto.getPrinAmt()));
                    // 变量.T+1发生数量 += 交易明细.交易份额     
                    paramT1.setNumBalance(ArithmeticUtil.doAdd(paramT1.getNumBalance(), dealDto.getShareAmt()));
                    
                    // 变量.发生额集 = 变量.T+1买入交易.get（场景元素.ONWAY_DATE）
                    DealHappenAmtDto dealHappenAmtDto = paramT1.getT1SellDeal().get(
                        DateUtil.format((Date)accountSceneMap.get(BookConstant.ONWAY_DATE)));
                    
                    // if（变量.发生额集 = null）
                    if (dealHappenAmtDto == null) {
                        // 初始化 变量.发生额集
                        dealHappenAmtDto = new DealHappenAmtDto();
                        dealHappenAmtDto.setHappenCb(BigDecimal.ZERO);
                        dealHappenAmtDto.setHappenGujzbd(BigDecimal.ZERO);
                        dealHappenAmtDto.setHappenLxtz(BigDecimal.ZERO);
                        dealHappenAmtDto.setHappenNum(BigDecimal.ZERO);
                        dealHappenAmtDto.setHappenYjlx(BigDecimal.ZERO);
                        // 变量.T+1买入交易.put（场景元素.在途日期,变量.发生额集）
                        paramT1.getT1BuyDeal().put(
                            DateUtil.format((Date)accountSceneMap.get(BookConstant.ONWAY_DATE)),
                            dealHappenAmtDto);
                    }
                    // 变量.发生额集.发生应计利息 += 交易明细.利息总额
                    dealHappenAmtDto.setHappenYjlx(
                        ArithmeticUtil.doAdd(dealHappenAmtDto.getHappenYjlx(), dealDto.getIntAmt()));
                    // 变量.发生额集.发生成本 += 交易明细.交易本金
                    dealHappenAmtDto.setHappenCb(
                        ArithmeticUtil.doAdd(dealHappenAmtDto.getHappenCb(), dealDto.getPrinAmt()));
                    // 变量.发生额集.发生数量 += 交易明细.交易份额
                    dealHappenAmtDto.setHappenNum(
                        ArithmeticUtil.doAdd(dealHappenAmtDto.getHappenNum(), dealDto.getShareAmt()));
                    
                    paramT1.getT1BuyDeal().put(
                        DateUtil.format((Date)accountSceneMap.get(BookConstant.ONWAY_DATE)),
                        dealHappenAmtDto);
                }
            }  
            // else if（交易方向 = "融入债券购回" or 交易方向 = 本方券交割" OR 交易方向 = "买入"）
            else if (DictCodeList.交易方向_融入债券购回.getValue().equals(ps)
                     || DictCodeList.交易方向_本方券交割.getValue().equals(ps)
                     || DictCodeList.交易方向_买入.getValue().equals(ps)) {
                if (DictCodeList.交易方向_融入债券购回.getValue().equals(ps) || 
                	DictCodeList.交易方向_买入.getValue().equals(ps)) {
                    //ad.会计场景代码="债券借贷-融入债券卖出后购回"
                    tableId = DictCodeListTableId.会计场景代码_债券借贷_融入债券卖出后购回.getValue();
                    // 备注 <-"会计场景代码(名称) + "交易登账"
                    book_remark = DictCodeListTableId.会计场景代码_债券借贷_融入债券卖出后购回.getCodeName() + "交易登账";
                    //登记托管机构                    <-      变量.登记托管机构
                    accountSceneMap.put(BookConstant.REGIST_ORG, dealDto.getRegistOrg());

                }
                else    {
                    //ad.会计场景代码="债券借贷-融入到期-本方券交割"
                    tableId = DictCodeListTableId.会计场景代码_债券借贷_融入到期_本方券交割.getValue();
                    // 备注 <-"会计场景代码(名称) + "交易登账"
                    book_remark = DictCodeListTableId.会计场景代码_债券借贷_融入到期_本方券交割.getCodeName() + "交易登账";
                }

                // accountSceneMap设置如下：
                // 在途日期
                Date onwayDate;
                // if（交易明细.交易场所 = "银行间"）
                if (DictCodeListExt1.估值交易市场_银行间.getValue().equals(dealDto.getTradeMarket())) {
                    // 在途日期 <- 交易明细.交易日 == 交割日？Null：交割日
                    onwayDate = DateUtil.compareDate(dealDto.getTradeDate(), dealDto.getSettleDate())==0 ? null : dealDto.getSettleDate();
                }
                else {
                    // 在途日期 <- 交易明细.交易日 + 1
                    onwayDate = DateUtil.addDay(dealDto.getTradeDate(), 1);
                }
                accountSceneMap.put(BookConstant.ONWAY_DATE,onwayDate);
                Date settleDate = null;
                // if（交易明细.交易场所 = "银行间"）
                if (DictCodeListExt1.估值交易市场_银行间.getValue().equals(dealDto.getTradeMarket())) {
                    // 在途日期 <- 交易明细.交易日 == 交割日？Null：交割日
                    onwayDate = DateUtil.compareDate(dealDto.getTradeDate(), dealDto.getSettleDate())==0 ? null : dealDto.getSettleDate();
                    settleDate = dealDto.getSettleDate();
                }
                else {
                    // 在途日期 <- 交易明细.交易日 + 1
                    onwayDate = DateUtil.addDay(dealDto.getTradeDate(), 1);
                    settleDate = DateUtils.addDays(dealDto.getTradeDate(), 1);
                }
                
                // accountAmountMap设置如下：
                // 变量.冲减公允价值变动
                BigDecimal cjGyJzBd = BigDecimal.ZERO;
                // 变量.冲减应计利息
                BigDecimal cjYjLx = BigDecimal.ZERO;
                // 变量.冲减数量
                BigDecimal cjSl = dealDto.getShareAmt() == null ? BigDecimal.ZERO : dealDto.getShareAmt();
                // 变量.冲减成本
                BigDecimal cjCb = BigDecimal.ZERO;
                // 变量.利息差额
                BigDecimal lxCe = BigDecimal.ZERO;
                // 变量.买卖价差
                BigDecimal mmJc = BigDecimal.ZERO;
                
                // 交易金额计算参数,将[1.数据准备]全局变量存入DealParamterDto
                DealParamterDto dealParam = new DealParamterDto();
                // 变量.成本科目余额
                dealParam.setVarCostSubjectAmt(cbkmye);
                // 变量.成本科目数量
                dealParam.setVarCostSubjectNum(cbkmsl);
                // 变量.应计利息科目余额
                dealParam.setVarLxSubjectAmt(yjlikmye);
                // 变量.公允价值变动科目余额
                dealParam.setVarGySubjectAmt(gyjzbdkmye);
                // 变量.溢折价科目余额
                dealParam.setVarYzjSubjectAmt(BigDecimal.ZERO);// 给空就ok
                // 变量.其他应收款-利息科目余额 = MAP（KEY：分支序号，VALUE:科目余额）
                dealParam.setVarQtyskxSubjectAmt(new HashMap<>());
                // if（场景元素.在途日期 != 空）
                if(null != accountSceneMap.get(BookConstant.ONWAY_DATE)){
                    
                    // 调用共通债券T+1处理
                    // 变量.T+1处理结果集 = 上述共通返回值
                    SecT1HandleResultDto secT1HandleResultDto = doSecT1Handle(rstBaseinfo,
                        acctEntity, dealDto, dealParam, paramT1, settleDate);
                    
                    // 变量.冲减成本 = 变量.T+1处理结果集.成本科目余额 * 交易明细.交易份额 / 变量.T+1处理结果集.成本科目数量
                    cjCb = ArithmeticUtil.divide(
                        ArithmeticUtil.multiply(ifNull2Zero(secT1HandleResultDto.getCbSubjectBalance()),
                            dealDto.getShareAmt()),secT1HandleResultDto.getNumBalance(), NumberEnum.MONEY.getCode());
                    // 变量.冲减公允价值变动 = 变量.T+1处理结果集.公允价值变动科目余额 * 交易明细.交易份额 / 变量.T+1处理结果集.成本科目数量
                    cjGyJzBd = ArithmeticUtil.divide(
                        ArithmeticUtil.multiply(ifNull2Zero(secT1HandleResultDto.getGyjzbdSubjectBalance()),
                            dealDto.getShareAmt()),
                        secT1HandleResultDto.getNumBalance(), NumberEnum.MONEY.getCode());
                    // 变量.冲减应计利息 = 变量.T+1处理结果集.应计利息科目余额 * 交易明细.交易份额 / 变量.T+1处理结果集.成本科目数量
                    cjYjLx = ArithmeticUtil.divide(
                        ArithmeticUtil.multiply(ifNull2Zero(secT1HandleResultDto.getYjlxSubjectBalance()),
                            dealDto.getShareAmt()),
                        secT1HandleResultDto.getNumBalance(), NumberEnum.MONEY.getCode());
                } else {
                    // 变量.冲减成本 = 变量.成本科目余额 * 交易明细.交易份额 / 变量.成本科目数量
                    cjCb = ArithmeticUtil.multiply(ifNull2Zero(dealParam.getVarCostSubjectAmt()), cjSl);
                    cjCb = ArithmeticUtil.divide(cjCb, dealParam.getVarCostSubjectNum(),
                        NumberEnum.MONEY.getCode());
                    // 变量.冲减公允价值变动 = 变量.公允价值变动科目余额 * 交易明细.交易份额 / 变量.成本科目数量
                    cjGyJzBd = ArithmeticUtil.multiply(ifNull2Zero(dealParam.getVarGySubjectAmt()), cjSl);
                    cjGyJzBd = ArithmeticUtil.divide(cjGyJzBd, dealParam.getVarCostSubjectNum(),
                        NumberEnum.MONEY.getCode());
                    // 变量.冲减应计利息 = 变量.应计利息科目余额 * 交易明细.交易份额 / 变量.成本科目数量
                    cjYjLx = ArithmeticUtil.divide(
                        ArithmeticUtil.multiply(ifNull2Zero(dealParam.getVarLxSubjectAmt()), dealDto.getShareAmt()),
                        dealParam.getVarCostSubjectNum(), NumberEnum.MONEY.getCode());
                }

                //冲减应计利息方式   01、按比例冲减  02、按实付利息冲减
                String offsetIntamtMode = CacheUtil.getSysParam("OFFSET_INTAMT_MODE");
                // if （【系统参数】.冲减应计利息方式 != null && 【系统参数】.冲减应计利息方式 = 数据字典.冲减应计利息方式.按实付利息冲减）
                if (StringUtils.isNotEmpty(offsetIntamtMode)
                        && offsetIntamtMode.equals(DictCodeList.冲减应计利息方式_按实付利息冲减.getValue())) {
                    // 变量.冲减应计利息  = 交易明细.利息总额
                    cjYjLx = dealDto.getIntAmt();
                }

                // 调用 共通【费用处理共通】 参考本设计书sheet【费用处理共通】
                // 变量.交易各种费用 = 上述返回值
                DealTypesFeeDto dealTypesFeeDto = calculationCommonService.quaryDealTypesFeeamt(
                    dealDto.getValTradeFeeDetailList(),dealDto.getProdId(),dealDto.getPortfolioId());
             
                // 印花税 <- 变量.交易各种费用.印花税
                putAccountAmountMap(accountAmountMap,BookConstant.STAMPTAX, dealTypesFeeDto.getStampDuty());
                // 经手费 <- 变量.交易各种费用.经手费
                putAccountAmountMap(accountAmountMap,BookConstant.HANDFEE, dealTypesFeeDto.getBrokerage());
                // 证管费 <- 变量.交易各种费用.证管费
                putAccountAmountMap(accountAmountMap,BookConstant.CERTIFEE, dealTypesFeeDto.getCertificateFee());
                // 结算费 <- 变量.交易各种费用.结算费
                putAccountAmountMap(accountAmountMap,BookConstant.SETTLEFEE, dealTypesFeeDto.getClearingFee());
                // 佣金 <- 变量.交易各种费用.佣金
                putAccountAmountMap(accountAmountMap,BookConstant.COMMISSION, dealTypesFeeDto.getCommission());
                // 交易手续费 <- 变量.交易各种费用.交易手续费
                putAccountAmountMap(accountAmountMap,BookConstant.TRADEFEE, dealTypesFeeDto.getTransactionFee());
                // 结算手续费 <- 变量.交易各种费用.结算手续费
                putAccountAmountMap(accountAmountMap,BookConstant.SETTLEMENTFEE, dealTypesFeeDto.getSettlementFee());
                
                // 交易金额 <- 交易明细.交易金额
                putAccountAmountMap(accountAmountMap,BookConstant.DEALAMT, dealDto.getTradeAmt());
                // 本方券交割全价 <- 交易明细.交易金额;本方券交割全价的金额元素为"DEALAMT",同上（交易金额"DEALAMT"）
                // putAccountAmountMap(accountAmountMap,BookConstant.DEALAMT, dealDto.getTradeAmt());
                
                // IF (变量.费用损益计入方式 = '计入投资收益') {
                if(DictCodeList.债券交易费用损益计入方式_计入投资收益.getValue().equals(calType)){
                    // 变量.买卖价差 <- 0
                    mmJc = BigDecimal.ZERO;
                }
                else{
                    // 印花税支出 <- 变量.交易各种费用.印花税 + 变量.交易各种费用.随交易支付_印花税
                    BigDecimal VAR_STAMPTAX_OUT = ArithmeticUtil.doAdd(
                        dealTypesFeeDto.getStampDuty(), dealTypesFeeDto.getStampDutyForDealPay());
                    putAccountAmountMap(accountAmountMap,BookConstant.STAMPTAX_OUT, VAR_STAMPTAX_OUT);
                    // 经手费支出 <- 变量.交易各种费用.经手费 + 变量.交易各种费用.随交易支付_经手费
                    BigDecimal VAR_HANDFEE_OUT = ArithmeticUtil.doAdd(
                        dealTypesFeeDto.getBrokerage(), dealTypesFeeDto.getBrokerageForDealPay());
                    putAccountAmountMap(accountAmountMap,BookConstant.HANDFEE_OUT, VAR_HANDFEE_OUT);
                    // 证管费支出 <- 变量.交易各种费用.证管费 + 变量.交易各种费用.随交易支付_证管费
                    BigDecimal VAR_CERTIFEE_OUT = ArithmeticUtil.doAdd(
                        dealTypesFeeDto.getCertificateFee(),
                        dealTypesFeeDto.getCertificateFeeForDealPay());
                    putAccountAmountMap(accountAmountMap,BookConstant.CERTIFEE_OUT, VAR_CERTIFEE_OUT);
                    // 结算费支出 <- 变量.交易各种费用.结算费 + 变量.交易各种费用.随交易支付_结算费
                    BigDecimal VAR_SETTLEFEE_OUT = ArithmeticUtil.doAdd(
                        dealTypesFeeDto.getClearingFee(),
                        dealTypesFeeDto.getClearingFeeForDealPay());
                    putAccountAmountMap(accountAmountMap,BookConstant.SETTLEFEE_OUT, VAR_SETTLEFEE_OUT);
                    // 佣金支出 <- 变量.交易各种费用.佣金 + 变量.交易各种费用.随交易支付_佣金
                    BigDecimal VAR_COMMISSION_OUT = ArithmeticUtil.doAdd(
                        dealTypesFeeDto.getCommission(),
                        dealTypesFeeDto.getCommissionForDealPay());
                    putAccountAmountMap(accountAmountMap,BookConstant.COMMISSION_OUT, VAR_COMMISSION_OUT);
                    // 交易手续费支出 <- 变量.交易各种费用.交易手续费 + 变量.交易各种费用.随交易支付_交易手续费
                    BigDecimal VAR_TRADEFEE_OUT = ArithmeticUtil.doAdd(
                        dealTypesFeeDto.getTransactionFee(),
                        dealTypesFeeDto.getTransactionFeeForDealPay());
                    putAccountAmountMap(accountAmountMap,BookConstant.TRADEFEE_OUT, VAR_TRADEFEE_OUT);
                    // 结算手续费支出 <- 变量.交易各种费用.结算手续费 + 变量.交易各种费用.随交易支付_结算手续费
                    BigDecimal VAR_SETTLEMENTFEE_OUT = ArithmeticUtil.doAdd(
                        dealTypesFeeDto.getSettlementFee(),
                        dealTypesFeeDto.getSettlementFeeForDealPay());
                    putAccountAmountMap(accountAmountMap,BookConstant.SETTLEMENTFEE_OUT, VAR_SETTLEMENTFEE_OUT);
                    // 变量.买卖价差 <- 印花税支出 + 经手费支出 + 证管费支出 + 结算费支出 + 佣金支出 + 交易手续费支出 + 结算手续费支出
                    mmJc = ArithmeticUtil.doAdd(VAR_STAMPTAX_OUT, VAR_HANDFEE_OUT,
                        VAR_CERTIFEE_OUT, VAR_SETTLEFEE_OUT, VAR_COMMISSION_OUT, VAR_TRADEFEE_OUT,
                        VAR_SETTLEMENTFEE_OUT);
                }
                // 成本金额 <- 变量.冲减成本
                putAccountAmountMap(accountAmountMap,BookConstant.COST, cjCb);
                // 应计利息 <- 变量.冲减应计利息
                putAccountAmountMap(accountAmountMap,BookConstant.INTAMT, cjYjLx);
                
                // 变量.利息差额 = 交易明细.利息总额 - 变量.冲减应计利息
                lxCe = ArithmeticUtil.subtract(dealDto.getIntAmt(), cjYjLx);
                // 变量.买卖价差 = (-1*变量.买卖价差) + 交易明细.交易金额 - 变量.冲减成本 - (-1*变量.交易各种费用.不随交易费用)-变量.冲减应计利息 - 变量.利息差额
                mmJc = ArithmeticUtil.subtract(dealDto.getTradeAmt(), mmJc);
                mmJc = ArithmeticUtil.subtract(mmJc, cjCb);
                mmJc = ArithmeticUtil.doAdd(mmJc, dealTypesFeeDto.getNoFeeAmt());
                mmJc = ArithmeticUtil.subtract(mmJc, cjYjLx);
                mmJc = ArithmeticUtil.subtract(mmJc, lxCe);
                
                // if(变量.利息差额 >= 0)
                if(BigDecimal.ZERO.compareTo(lxCe) <= 0){
                    // 利息差额（借） <- 变量.利息差额
                    putAccountAmountMap(accountAmountMap, BookConstant.INCOMEAMT_D, lxCe);
                }
                else{
                    // 利息差额（贷） <- 变量.利息差额*-1
                    putAccountAmountMap(accountAmountMap,BookConstant.INCOMEAMT_C, lxCe.negate());
                }
                // if（变量.买卖价差>= 0）
                if(BigDecimal.ZERO.compareTo(mmJc) <= 0){
                    // 买卖价差（借） <- 变量.买卖价差
                    putAccountAmountMap(accountAmountMap,BookConstant.RESELL_INAMT_D, mmJc);// 转卖差价收益（借）
                }
                else{
                    // 买卖价差（贷） <- 变量.买卖价差*-1
                    putAccountAmountMap(accountAmountMap,BookConstant.RESELL_INAMT_C, mmJc.negate());//转卖差价收益（贷）
                }
                // if(变量.冲减公允价值变动 >= 0 )
                if(BigDecimal.ZERO.compareTo(cjGyJzBd) <= 0){
                    // 公允价值变动(借) <- 变量.冲减公允价值变动
                    putAccountAmountMap(accountAmountMap,BookConstant.FVTPL_INTADJ_D, cjGyJzBd);
                    // 公允价值变动损益(贷) <- 变量.冲减公允价值变动
                    putAccountAmountMap(accountAmountMap,BookConstant.FLOAT_1_C, cjGyJzBd);
                }
                else{
                    // 公允价值变动(贷) <- 变量.冲减公允价值变动 * -1
                    putAccountAmountMap(accountAmountMap,BookConstant.FVTPL_INTADJ_C, cjGyJzBd.negate());
                    // 公允价值变动损益(借) <- 变量.冲减公允价值变动 * -1
                    putAccountAmountMap(accountAmountMap,BookConstant.FLOAT_1_D, cjGyJzBd.negate());
                }

                // 场景元素设值各种费用的账务明细代码
                // 印花税账务明细代码 <- 变量.交易各种费用.印花税账务明细代码
                accountSceneMap.put(BookConstant.BOK_DETAIL_STAMPTAX, dealTypesFeeDto.getStampDutyCode());
                // 经手费账务明细代码 <- 变量.交易各种费用.经手费账务明细代码
                accountSceneMap.put(BookConstant.BOK_DETAIL_HANDFEE, dealTypesFeeDto.getBrokerageCode());
                // 证管费账务明细代码 <- 变量.交易各种费用.证管费账务明细代码
                accountSceneMap.put(BookConstant.BOK_DETAIL_CERTIFEE,
                    dealTypesFeeDto.getCertificateFeeCode());
                // 结算费账务明细代码 <- 变量.交易各种费用.结算费账务明细代码
                accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEFEE,
                    dealTypesFeeDto.getClearingFeeCode());
                // 佣金账务明细代码 <- 变量.交易各种费用.佣金账务明细代码
                accountSceneMap.put(BookConstant.BOK_DETAIL_COMMISSION,
                    dealTypesFeeDto.getCommissionCode());
                // 交易手续费账务明细代码 <- 变量.交易各种费用.交易手续费账务明细代码
                accountSceneMap.put(BookConstant.BOK_DETAIL_TRADEFEE,
                    dealTypesFeeDto.getTransactionFeeCode());
                // 结算手续费账务明细代码 <- 变量.交易各种费用.结算手续费账务明细代码
                accountSceneMap.put(BookConstant.BOK_DETAIL_SETTLEMENTFEE,
                    dealTypesFeeDto.getSettlementFeeCode());
                // 元素不明  【同设计者确认，废弃】
                /*
                //通道费账务明细代码<-变量.交易各种费用.通道费账务明细代码
                if(dealTypesFeeDto.getSettlementFeeCodeForDealPay()!=null)accountSceneMap.put(BookConstant.通道费账务明细代码,dealTypesFeeDto.getChlFeeCode());
                //通道管理费账务明细代码<-变量.交易各种费用.通道管理费账务明细代码
                if(dealTypesFeeDto.getSettlementFeeCodeForDealPay()!=null)accountSceneMap.put(BookConstant.通道管理费账务明细代码,dealTypesFeeDto.getChlGlFeeCode());
                //通道销售费账务明细代码<-变量.交易各种费用.通道销售费账务明细代码
                if(dealTypesFeeDto.getSettlementFeeCodeForDealPay()!=null)accountSceneMap.put(BookConstant.通道销售费账务明细代码,dealTypesFeeDto.getChlXsFeeCode());
                //通道托管费账务明细代码<-变量.交易各种费用.通道托管费账务明细代码
                if(dealTypesFeeDto.getSettlementFeeCodeForDealPay()!=null)accountSceneMap.put(BookConstant.通道托管费账务明细代码,dealTypesFeeDto.getChlTgFeeCode());
                */
                
                // 变量.成本科目余额 = 变量.成本科目余额 - 变量.冲减成本
                cbkmye = ArithmeticUtil.subtract(dealParam.getVarCostSubjectAmt(), cjCb);
                // 变量.成本科目数量 = 变量.成本科目数量 - 变量.冲减数量
                cbkmsl = ArithmeticUtil.subtract(dealParam.getVarCostSubjectNum(), cjSl);
                // 变量.应计利息科目余额 = 变量.应计利息科目余额 - 变量.冲减应计利息
                yjlikmye = ArithmeticUtil.subtract(dealParam.getVarLxSubjectAmt(), cjYjLx);
                // 变量.公允价值变动科目余额 = 变量.公允价值变动科目余额 - 变量.冲减公允价值变动
                gyjzbdkmye = ArithmeticUtil.subtract(dealParam.getVarGySubjectAmt(), cjGyJzBd);
                
                // if（场景元素.在途日期 != 空）
                if (null != accountSceneMap.get(BookConstant.ONWAY_DATE)) {
                    // 变量.T+1发生公允价值变动 -= 变量.冲减公允价值变动
                    paramT1.setGyjzbdSubjectBalance(
                        ArithmeticUtil.subtract(paramT1.getGyjzbdSubjectBalance(), cjGyJzBd));
                    // 变量.T+1发生应计利息 -= 变量.冲减应计利息
                    paramT1.setYjlxSubjectBalance(
                        ArithmeticUtil.subtract(paramT1.getYjlxSubjectBalance(), cjYjLx));
                    // 变量.T+1发生成本 -= 变量.冲减成本
                    paramT1.setCbSubjectBalance(
                        ArithmeticUtil.subtract(paramT1.getCbSubjectBalance(), cjCb));
                    // 变量.T+1发生数量 -= 变量.冲减数量
                    paramT1.setNumBalance(ArithmeticUtil.subtract(paramT1.getNumBalance(), cjSl));
                    // 变量.发生额集 = 变量.T+1买入交易.get（交易明细.交割日）
                    DealHappenAmtDto dealHappenAmtDto = paramT1.getT1SellDeal().get(
                        DateUtil.format((Date)accountSceneMap.get(BookConstant.ONWAY_DATE)));

                    // if（变量.发生额集 = null）
                    if (dealHappenAmtDto == null) {
                        // 初始化 变量.发生额集
                        dealHappenAmtDto = new DealHappenAmtDto();
                        dealHappenAmtDto.setHappenCb(BigDecimal.ZERO);
                        dealHappenAmtDto.setHappenGujzbd(BigDecimal.ZERO);
                        dealHappenAmtDto.setHappenLxtz(BigDecimal.ZERO);
                        dealHappenAmtDto.setHappenNum(BigDecimal.ZERO);
                        dealHappenAmtDto.setHappenYjlx(BigDecimal.ZERO);
                        // 变量.T+1买入交易.get（交易明细.交割日,变量.发生额集）
                        paramT1.getT1SellDeal().put(
                            DateUtil.format((Date)accountSceneMap.get(BookConstant.ONWAY_DATE)),
                            dealHappenAmtDto);
                    }
                    
                    // 变量.发生额集.发生公允价值变动 += 变量.冲减公允价值变动
                    dealHappenAmtDto.setHappenGujzbd(
                        ArithmeticUtil.doAdd(dealHappenAmtDto.getHappenGujzbd(), cjGyJzBd));
                    // 变量.发生额集.发生应计利息 += 变量.冲减应计利息
                    dealHappenAmtDto.setHappenYjlx(
                        ArithmeticUtil.doAdd(dealHappenAmtDto.getHappenYjlx(), cjYjLx));
                    // 变量.发生额集.发生成本 += 变量.冲减成本
                    dealHappenAmtDto.setHappenCb(
                        ArithmeticUtil.doAdd(dealHappenAmtDto.getHappenCb(), cjCb));
                    // 变量.发生额集.发生数量 += 变量.冲减数量
                    dealHappenAmtDto.setHappenNum(
                        ArithmeticUtil.doAdd(dealHappenAmtDto.getHappenNum(), cjSl));
                    paramT1.getT1SellDeal().put(
                        DateUtil.format((Date)accountSceneMap.get(BookConstant.ONWAY_DATE)),
                        dealHappenAmtDto);
                }

            }
            ad = getAccountDriver(acctEntity,dealDto,tableId);
            accountSceneMap.put(BookConstant.BOOK_REMARK,book_remark);
            accountCurrencyMap = getAccountCurrencyMap(acctEntity, dealDto,accountAmountMap);
            
            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
            ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
            ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素
            ad.setAccountNatrueMap(accountNatrueMap); //基础科目性质元素

            // add by weipeng on 20220321：增加辅助核算项配置 start
            ad.setSubjectMappingConfigDefault(dealDto.getSubjectMappingConfig());
            // add by weipeng on 20220321：增加辅助核算项配置 end

            // 记账凭证生成
            BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, batchExecuteInfo);
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            rtnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());
        }
        return rtnResult;
    }
    
    /**
     * 金融负债收息处理 共通处理
     * 
     * @param rstBaseinfo 标的信息
     * @param acctEntity 交易类账务处理结果
     * @param dealDto 交易明细
     * @return 应计利息
     * @see
     * @since 1.0
     */
    public BigDecimal getFinLiabilIntHandle(TargetFinProduct rstBaseinfo,
                                            FinProductDealInfoResult acctEntity,
                                            FinProductDealInfoComDTO dealDto) {
        // 基础科目性质=金融负债成本
        SubjectBalance subjectBalance = getSubjectBalanceFin(acctEntity, dealDto,
            DictCodeBaseSubjectNatrueNew.交易性金融负债_应计利息.getValue(), DictCodeList.余额方向_贷方.getValue());// 设计变更 :借方-->贷方  zhangyf 20181203
        // 应计利息
        BigDecimal accruedInterest = subjectBalance.getBalance();
        // 数量
        // BigDecimal num = subjectBalance.getNum();
        
        // if（参数.标的信息.交易场所 = "银行间"）
        if (DictCodeListExt1.估值交易市场_银行间.getValue().equals(rstBaseinfo.getTradeMarket())) {
            // 塞入要查询的基础科目性质
            List<String> baseSubjectNatureList = new ArrayList<String>();
            baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.交易性金融负债_应计利息.getValue());
            // 买入场景
            // 调用账务共通设计->【凭证最新账务查询】
            // 变量.买入账务明细列表
            List<BookLatestInfoDto> listBookLatestInfoDtoBuy = accountingCommonService.getBookLatestInfo(
                acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntity.getBokDetailId(),
                baseSubjectNatureList, DictCodeListTableId.会计场景代码_债券借贷_借入债券卖出.getValue(), null, null,
                DateUtil.addDay(dealDto.getMdate(), -1), null, null);
            if (!CollectionUtils.isEmpty(listBookLatestInfoDtoBuy)) {
                for(BookLatestInfoDto dto : listBookLatestInfoDtoBuy){
                    // if(变量.账务明细.基础科目性质 = 应计利息 )
                    if(DictCodeBaseSubjectNatrueNew.交易性金融负债_应计利息.getValue().equals(dto.getBaseSubjectNature())){
                        // 变量.账务明细.借贷方向为借时
                        if(DictCodeList.借贷方向_借.getValue().equals(dto.getCdFlag())){
                            // 变量.应计利息 -= 变量.账务明细.原币发生额
                            accruedInterest = ArithmeticUtil.doSub(accruedInterest,dto.getoHappenAmt());
                        }
                        // 变量.账务明细.借贷方向为贷时
                        else if(DictCodeList.借贷方向_贷.getValue().equals(dto.getCdFlag())){
                            // 变量.应计利息 += 变量.账务明细.原币发生额
                            accruedInterest = ArithmeticUtil.doAdd(accruedInterest,dto.getoHappenAmt());
                        }
                    }
                }
            }
            
            // 卖出场景
            // 调用账务共通设计->【凭证最新账务查询】
            // 变量.卖出账务明细列表
            List<BookLatestInfoDto> listBookLatestInfoDtoSell = accountingCommonService.getBookLatestInfo(
                acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntity.getBokDetailId(),
                baseSubjectNatureList, DictCodeListTableId.会计场景代码_债券借贷_融入债券卖出后购回.getValue(), null, null,
                DateUtil.addDay(dealDto.getMdate(), -1), null, null);
            if (!CollectionUtils.isEmpty(listBookLatestInfoDtoSell)) {
                for(BookLatestInfoDto dto : listBookLatestInfoDtoBuy){
                    // if(变量.账务明细.基础科目性质 = 应计利息 )
                    if(DictCodeBaseSubjectNatrueNew.交易性金融负债_应计利息.getValue().equals(dto.getBaseSubjectNature())){
                        // 变量.账务明细.借贷方向为借时
                        if(DictCodeList.借贷方向_借.getValue().equals(dto.getCdFlag())){
                            // 变量.应计利息 -= 变量.账务明细.原币发生额
                            accruedInterest = ArithmeticUtil.doSub(accruedInterest,dto.getoHappenAmt());
                        }
                        // 变量.账务明细.借贷方向为贷时
                        else if(DictCodeList.借贷方向_贷.getValue().equals(dto.getCdFlag())){
                            // 变量.应计利息 += 变量.账务明细.原币发生额
                            accruedInterest = ArithmeticUtil.doAdd(accruedInterest,dto.getoHappenAmt());
                        }
                    }
                }
            }
        }
        // 查询交易的到期日是否为付息截止日
        // 调用共通方法-【标的类共通方法设计】-付息截止日判断SHEET
        FinCashPlanDto finCashPlanDto = finProductCommonService.payDeadlineJudgment(dealDto.getFinprodId(), dealDto.getMdate());
        // 定义：否付息截止日
        Boolean isfDeadlineNow = true;
        // 赋值
        if (finCashPlanDto != null) {
            isfDeadlineNow = finCashPlanDto.isfDeadline();
        }
        // if（上述返回值为 否）
        // 变量.百元券面
        BigDecimal varFaceValue = BigDecimal.ZERO;
        if(!isfDeadlineNow){
            // 调用共通 ,百元券面查询  
            // 设计者确认：标的代码 = 条件交易类账务处理结果.业务明细代码--->取明细中的金融产品代码
            varFaceValue = finProductCommonService.getFaceValue(dealDto.getFinprodId(),dealDto.getHappenDate());
            // 变量.应计利息 = 变量.应计利息 * (交易明细.单位面值—上述共通返回 百元券面面值) / 交易明细.单位面值
            if(null != dealDto.getParValue() && BigDecimal.ZERO.compareTo(dealDto.getParValue()) != 0){
                accruedInterest = ArithmeticUtil.multiply(ifNull2Zero(accruedInterest),dealDto.getParValue().subtract(varFaceValue))
                    .divide(dealDto.getParValue(), 2, BigDecimal.ROUND_HALF_UP);
            }
        }else{
            // 变量.应计利息 = 变量.应计利息
        }
        return accruedInterest;
    }

    /**
     * 金融负债还本到期处理 共通处理
     * 
     * @param rstBaseinfo 标的信息
     * @param acctEntity 交易类账务处理结果
     * @param dealDto 交易明细
     * @return 金额元素Dto
     * @see
     * @since 1.0
     */
    public AmountElementDTO getFinLiabilRepayHandle(TargetFinProduct rstBaseinfo,
                                        FinProductDealInfoResult acctEntity,
                                        FinProductDealInfoComDTO dealDto) {
        //金额元素
        AmountElementDTO amountElementDTO = new AmountElementDTO();
        // 变量.冲减公允价值变动
        BigDecimal varCjgyjzbd = BigDecimal.ZERO;
        // 变量.冲减成本
        BigDecimal varCjcb = BigDecimal.ZERO;
        // 变量.冲减数量
        //BigDecimal varCjsl = BigDecimal.ZERO;
        // 变量.买卖价差
        //BigDecimal varMmjc = BigDecimal.ZERO;
        // 变量.应计利息
        BigDecimal varYjlx = BigDecimal.ZERO;
        // 变量.投资收益
        BigDecimal varTzsy = BigDecimal.ZERO;
        // 变量.还本本金
        BigDecimal varHbbj = BigDecimal.ZERO;
        
        // 科目余额实体-金融负债成本-贷方
        SubjectBalance subjectBalanceJrfzcb = getSubjectBalanceFin(acctEntity, dealDto,
            DictCodeBaseSubjectNatrueNew.交易性金融负债_成本.getValue(), DictCodeList.余额方向_贷方.getValue());
        // 变量.结转日成本科目余额 = 科目余额
        BigDecimal jzrCbkmYe = subjectBalanceJrfzcb.getBalance();
        // 变量.结转日成本科目数量 = 数量
         BigDecimal jzrCbkmSl = subjectBalanceJrfzcb.getNum();
        
        // 科目余额实体-公允价值变动-贷方
        SubjectBalance subjectBalanceGyjzbd = getSubjectBalanceFin(acctEntity, dealDto,
            DictCodeBaseSubjectNatrueNew.交易性金融负债_公允价值变动.getValue(), DictCodeList.余额方向_贷方.getValue());
        // 变量.结转日公允价值变动科目余额 = 科目余额
        BigDecimal jzrGyjzbdKmye = subjectBalanceGyjzbd.getBalance() == null ? BigDecimal.ZERO : subjectBalanceGyjzbd.getBalance();
        
        // if（参数.标的信息.交易场所 = "银行间"）
        if (DictCodeListExt1.估值交易市场_银行间.getValue().equals(rstBaseinfo.getTradeMarket())) {
            // 塞入要查询的基础科目性质
            List<String> baseSubjectNatureList = new ArrayList<String>();
            baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.交易性金融负债_成本.getValue());
            // 买入场景
            // 调用账务共通设计->【凭证最新账务查询】
            // 变量.买入账务明细列表
            List<BookLatestInfoDto> listBookLatestInfoDtoBuy = accountingCommonService.getBookLatestInfo(
                acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntity.getBokDetailId(),
                baseSubjectNatureList, DictCodeListTableId.会计场景代码_债券借贷_借入债券卖出.getValue(), null, null,
                DateUtil.addDay(dealDto.getMdate(), -1), null, null);
            if (!CollectionUtils.isEmpty(listBookLatestInfoDtoBuy)) {
                for(BookLatestInfoDto dto : listBookLatestInfoDtoBuy){
                    // if(变量.账务明细.基础科目性质 = 金融负债成本 )
                    if(DictCodeBaseSubjectNatrueNew.交易性金融负债_成本.getValue().equals(dto.getBaseSubjectNature())){
                        // 变量.结转日成本科目数量 -= 变量.账务明细.发生数量
                        jzrCbkmSl = ArithmeticUtil.doSub(jzrCbkmSl,dto.getHappenNumber());
                        // 变量.结转日成本科目余额 -= 变量.账务明细.原币发生额
                        jzrCbkmYe = ArithmeticUtil.doSub(jzrCbkmYe,dto.getoHappenAmt());
                    }
                    // if(变量.账务明细.基础科目性质 = 公允价值变动 )
                    else if(DictCodeBaseSubjectNatrueNew.交易性金融负债_公允价值变动.getValue().equals(dto.getBaseSubjectNature())){
                        // 变量.账务明细.借贷方向为借时
                        if(DictCodeList.借贷方向_借.getValue().equals(dto.getCdFlag())){
                            // 变量.结转日公允价值变动科目余额 -= 变量.账务明细.原币发生额
                            jzrGyjzbdKmye = ArithmeticUtil.doSub(jzrGyjzbdKmye,dto.getoHappenAmt());
                        }
                        // 变量.账务明细.借贷方向为贷时
                        else if(DictCodeList.借贷方向_贷.getValue().equals(dto.getCdFlag())){
                            // 变量.结转日公允价值变动科目余额 += 变量.账务明细.原币发生额
                            jzrGyjzbdKmye = ArithmeticUtil.doAdd(jzrGyjzbdKmye,dto.getoHappenAmt());
                        }
                    }
                }
            }
            
            // 卖出场景
            // 调用账务共通设计->【凭证最新账务查询】
            // 变量.卖出账务明细列表
            List<BookLatestInfoDto> listBookLatestInfoDtoSell = accountingCommonService.getBookLatestInfo(
                acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntity.getBokDetailId(),
                baseSubjectNatureList, DictCodeListTableId.会计场景代码_债券借贷_融入债券卖出后购回.getValue(), null, null,
                DateUtil.addDay(dealDto.getMdate(), -1), null, null);
            if (!CollectionUtils.isEmpty(listBookLatestInfoDtoSell)) {
                for(BookLatestInfoDto dto : listBookLatestInfoDtoBuy){
                    // if(变量.账务明细.基础科目性质 = 金融负债成本 )
                    if(DictCodeBaseSubjectNatrueNew.交易性金融负债_成本.getValue().equals(dto.getBaseSubjectNature())){
                        // 变量.结转日成本科目数量 += 变量.账务明细.发生数量
                        jzrCbkmSl = ArithmeticUtil.doAdd(jzrCbkmSl,dto.getHappenNumber());
                        // 变量.结转日成本科目余额 += 变量.账务明细.原币发生额
                        jzrCbkmYe = ArithmeticUtil.doAdd(jzrCbkmYe,dto.getoHappenAmt());
                    }
                    // if(变量.账务明细.基础科目性质 = 公允价值变动 )
                    else if(DictCodeBaseSubjectNatrueNew.交易性金融负债_公允价值变动.getValue().equals(dto.getBaseSubjectNature())){
                        // 变量.账务明细.借贷方向为借时
                        if(DictCodeList.借贷方向_借.getValue().equals(dto.getCdFlag())){
                            // 变量.结转日公允价值变动科目余额 -= 变量.账务明细.原币发生额
                            jzrGyjzbdKmye = ArithmeticUtil.doSub(jzrGyjzbdKmye,dto.getoHappenAmt());
                        }
                        // 变量.账务明细.借贷方向为贷时
                        else if(DictCodeList.借贷方向_贷.getValue().equals(dto.getCdFlag())){
                            // 变量.结转日公允价值变动科目余额 += 变量.账务明细.原币发生额
                            jzrGyjzbdKmye = ArithmeticUtil.doAdd(jzrGyjzbdKmye,dto.getoHappenAmt());
                        }
                    }
                }
            }
        }
        
        // 应计利息
        BigDecimal accruedInterest = BigDecimal.ZERO;
        // 数量
        // BigDecimal num = BigDecimal.ZERO;
        //if（参数.标的信息.息票品种为零息或贴现时）
        if (DictCodeList.息票品种_零息.getValue().equals(rstBaseinfo.getCouponSpecies())
            ||DictCodeList.息票品种_贴现.getValue().equals(rstBaseinfo.getCouponSpecies())) {
            // 查询交易的到期日对应的应计利息
            // 科目余额实体 -应计利息-贷方
            SubjectBalance subjectBalanceYjlx = getSubjectBalanceFin(acctEntity, dealDto,
                DictCodeBaseSubjectNatrueNew.交易性金融负债_应计利息.getValue(), DictCodeList.余额方向_贷方.getValue());

            accruedInterest = subjectBalanceYjlx.getBalance();
            //num = subjectBalanceYjlx.getNum();
            // 金额元素DTO.应计利息 = 上述共通返回的科目余额
            varYjlx = accruedInterest;
        }
        
        // 变量.交易成本 = 变量.结转日成本科目数量 * 交易明细.单位净价
        BigDecimal varJycb = ArithmeticUtil.multiply(ifNull2Zero(jzrCbkmSl),dealDto.getUnitCprice());
        // 金额元素DTO.还本本金 = 变量.结转日成本科目数量 * 交易明细.单位净价
        varHbbj = ArithmeticUtil.multiply(ifNull2Zero(jzrCbkmSl),dealDto.getUnitCprice());
        
        // 变量.结转日成本科目数量*交易明细.单位面值
        BigDecimal varSlmzTmp = ArithmeticUtil.multiply(ifNull2Zero(jzrCbkmSl),dealDto.getParValue());
        if(null != varSlmzTmp && BigDecimal.ZERO.compareTo(varSlmzTmp) != 0){//分母判断
            // 金额元素DTO.冲减公允价值变动 = 变量.结转日公允价值变动科目余额 * 变量.交易成本/（变量.结转日成本科目数量*交易明细.单位面值）
            varCjgyjzbd = ArithmeticUtil.divide(ArithmeticUtil.multiply(ifNull2Zero(jzrGyjzbdKmye),varJycb), varSlmzTmp);
            // 金额元素DTO.冲减成本 = 变量.结转日成本科目余额 * 变量.交易成本/（变量.结转日成本科目数量*交易明细.单位面值） 
            varCjcb = ArithmeticUtil.divide(ArithmeticUtil.multiply(ifNull2Zero(jzrCbkmYe),varJycb), varSlmzTmp);
        }
        // 金额元素DTO.投资收益= 金额元素DTO.还本本金 - 金额元素DTO.冲减成本 - 金额元素DTO.应计利息
        varTzsy = ArithmeticUtil.doSub(varHbbj,varCjcb, varYjlx);
        
        amountElementDTO.setVarHbbj(varHbbj == null ? BigDecimal.ZERO : varHbbj);
        amountElementDTO.setVarYjlx(varYjlx == null ? BigDecimal.ZERO : varYjlx);
        amountElementDTO.setVarCjgyjzbd(varCjgyjzbd == null ? BigDecimal.ZERO : varCjgyjzbd);
        amountElementDTO.setVarCjcb(varCjcb == null ? BigDecimal.ZERO : varCjcb);
        amountElementDTO.setVarTzsy(varTzsy == null ? BigDecimal.ZERO : varTzsy);
        
        return amountElementDTO;
    }

    /**
     * 科目余额查询（getFinLiabilIntHandle、getFinLiabilRepayHandle使用）
     * 
     * @param acctEntity 交易类账务处理结果
     * @param dealDto 交易明细
     * @param baseSubjectNature 基础科目性质
     * @param expBalanceDirect 期待余额方向
     * @return 科目余额
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalanceFin(FinProductDealInfoResult acctEntity,
                                              FinProductDealInfoComDTO dealDto,
                                              String baseSubjectNature,
                                              String expBalanceDirect) {
        Date happenDate = DateUtil.addDay(dealDto.getHappenDate(), -1);// 发生日期
        Date bookDate = acctEntity.getBookDate();// 入账日期
        String bokDetailId = acctEntity.getBokDetailId();// 账务明细代码
        String booksetId = acctEntity.getBooksetId();// 账套代码
        // 塞入要查询的基础科目性质
        List<String> baseSubjectNatureList = new ArrayList<String>();
        baseSubjectNatureList.add(baseSubjectNature);

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setHappenDate(happenDate);
        searchDto.setBokDetailId(bokDetailId);
        searchDto.setBookDate(bookDate);
        searchDto.setBooksetId(booksetId);
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDto, expBalanceDirect);

        // 定义返回实体类
        SubjectBalance subjectBalance = new SubjectBalance();
        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            subjectBalance = subjectBalanceList.get(0);
        }
        return subjectBalance;
    }
    /**
     * 调用公用方法：【科目余额查询 - 期待方向】，获取科目余额信息中的科目余额数据
     * 
     * @param acctEntity 交易类账务处理结果
     * @param expBalanceDirect 期待余额方向
     * @param baseSubjectNature 基础科目性质
     * @param taskPri 任务优先级
     * @return subjectBalance 科目余额信息
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalance(FinProductDealInfoResult acctEntity,
                                             String expBalanceDirect, String baseSubjectNature,
                                             Long taskPri) {
  
        // 获取从reader中拿到的值
        String booksetId = acctEntity.getBooksetId();// 账套代码
        String bokDetailId = acctEntity.getBokDetailId();// 账务明细代码
        Date happenDate = acctEntity.getHappenDate();// 发生日期
        Date bookDate = acctEntity.getBookDate();// 入账日期
        // 定义返回实体类
        SubjectBalance subjectBalance = new SubjectBalance();

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
     * 标的信息查询
     * 
     * @param acctEntity 交易类账务处理结果
     * @return TargetFinProduct 标的信息
     * @see
     * @since 1.0
     */
    private TargetFinProduct myGetTargetInfo(FinProductDealInfoComDTO dealDto) {
        TargetSearchDto searchBaseinfo = new TargetSearchDto();// 标的信息条件
        List<TargetSearchDto> queryCriteria = new ArrayList<TargetSearchDto>();// 查询类型【必输项】
        searchBaseinfo.setQueryType(ValDictCodeList.标的_信息.getValue());// 数据代码 RST_FINPRODUCT
        queryCriteria.add(searchBaseinfo);
        Map<TargetSearchDto, Object> targetInfoMap = finProductCommonService.getTargetInfo(
            dealDto.getFinprodId(), queryCriteria);// 设计变更 : 账务明细代码-->业务明细代码  zhangyf 20181203
        
        // 标的信息
        TargetFinProduct rstBaseinfo = new TargetFinProduct();
        if (null != targetInfoMap.get(searchBaseinfo)) {
            //标的信息 = 上述共通返回标的信息
            rstBaseinfo = (TargetFinProduct)targetInfoMap.get(searchBaseinfo);
        }
        return rstBaseinfo;
    }
    
    /**
     * 记账参数初试化
     * 
     * @param acctEntity 交易类账务处理结果
     * @param dealDto 交易类账务处理结果.交易明细
     * @param tableId 会计场景代码
     * @return 记账参数
     * @see
     * @since 1.0
     */
    private AccountDriver getAccountDriver(FinProductDealInfoResult acctEntity,
                                           FinProductDealInfoComDTO dealDto, 
                                           String tableId) {
        AccountDriver ad = new AccountDriver();
        // 会计场景
        TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();
        // 塞入记账场景树节点 = 会计场景
        ad.setTreeNode(bookSetTreeNodeNow);
        // ad.会计场景代码
        ad.setTableId(tableId);
        return ad;
    }
    
    /**
     * 初始化定义 - 存放该笔记账的场景要素 map定义
     * 
     * @param acctEntity 交易类账务处理结果
     * @param dealDto 交易类账务处理结果.交易明细
     * @param rstBaseinfo 标的信息
     * @return 记账的场景要素 map
     * @see
     * @since 1.0
     */
    private Map<String, Object> getAccountSceneMap(FinProductDealInfoResult acctEntity,
                                                   FinProductDealInfoComDTO dealDto,
                                                   TargetFinProduct rstBaseinfo) {
        // 初始化定义 - 存放该笔记账的场景要素 map定义
        // 记账场景要素map塞值,(调用共通方法-交易类场景元素赋值)
        Map<String, Object> accountSceneMap = bookCommonLogic.transactionElementAssign(acctEntity,dealDto);
        // 分类1 <- 变量.交易信息.分类1
        accountSceneMap.put(BookConstant.TYPE_1, rstBaseinfo.getType1());// 分类1 - 变量.交易信息.分类1
        // 分类2 <- 变量.交易信息.分类2
        accountSceneMap.put(BookConstant.TYPE_2, rstBaseinfo.getType2());// 分类2 - 变量.交易信息.分类2
        return accountSceneMap;
    }

    /**
     * 初始化定义 - 存放该笔记账的相关金额要素 map定义
     * 
     * @param acctEntity 交易类账务处理结果
     * @param dealDto 交易类账务处理结果.交易明细
     * @return 记账的相关金额要素 map
     * @see
     * @since 1.0
     */
//    private Map<String, BigDecimal> getAccountAmountMap(FinProductDealInfoResult acctEntity,
//                                                        FinProductDealInfoComDTO dealDto) {
//        // 初始化定义 - 存放该笔记账的相关金额要素 map定义
//        Map<String, BigDecimal> accountAmountMap = Maps.newHashMap();
//        return accountAmountMap;
//    }

    /**
     * 记账的相关金额要素 map赋值
     * 
     * @param accountAmountMap 记账的相关金额要素 map
     * @param element 金额元素
     * @param elementVal 金额值
     * @see
     * @since 1.0
     */
    private void putAccountAmountMap(Map<String, BigDecimal> accountAmountMap,
                                     String element,
                                     BigDecimal elementVal) {
        accountAmountMap.put(element, ArithmeticUtil.round(elementVal, NumberEnum.MONEY));
    }
    
    /**
     * 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
     * 
     * @param acctEntity 交易类账务处理结果
     * @param dealDto 交易类账务处理结果.交易明细
     * @param accountAmountMap 记账的相关金额要素 map
     * @return 记账的相关金额对应的币种
     * @see
     * @since 1.0
     */
    private Map<String, Object> getAccountCurrencyMap(FinProductDealInfoResult acctEntity,
                                                      FinProductDealInfoComDTO dealDto,
                                                      Map<String, BigDecimal> accountAmountMap) {
        Map<String, Object> accountCurrencyMap = Maps.newHashMap();
        // 所有金额元素 <-交易明细.交易币种
        for (Map.Entry<String, BigDecimal> entity : accountAmountMap.entrySet()) {
            accountCurrencyMap.put(entity.getKey(), dealDto.getCcy());
        }
        return accountCurrencyMap;
    }
    /**
     * 债券T+1方法
     * 
     * @param rstBaseinfo 标的信息
     * @param acctEntity 交易类账务处理结果
     * @param dealNow 交易类账务处理结果.交易明细
     * @param param 交易金额计算参数
     * @param paramT1 T+1交易变量定义
     * @return
     * @see
     * @since 1.0
     */
    private SecT1HandleResultDto doSecT1Handle(TargetFinProduct rstBaseinfo,
                                               FinProductDealInfoResult acctEntity,
                                               FinProductDealInfoComDTO dealNow,
                                               DealParamterDto param,
                                               SecT1HandleParamterDto paramT1,
                                               Date settleDate) {
        // 调用共通债券T+1处理
        SecT1HandleParamterDto secT1HandleParamterDto = new SecT1HandleParamterDto();
        // 数量余额 变量.成本科目余额 - 变量.T+1发生成本
        secT1HandleParamterDto.setNumBalance(
            ArithmeticUtil.subtract(param.getVarCostSubjectNum(), paramT1.getNumBalance()));
        // 成本类科目余额 变量.成本科目数量 - 变量.T+1发生数量
        secT1HandleParamterDto.setCbSubjectBalance(
            ArithmeticUtil.subtract(param.getVarCostSubjectAmt(), paramT1.getCbSubjectBalance()));
        // 应计利息科目余额 变量.应计利息科目余额 - 变量.T+1发生应计利息
        secT1HandleParamterDto.setYjlxSubjectBalance(
            ArithmeticUtil.subtract(param.getVarLxSubjectAmt(), paramT1.getYjlxSubjectBalance()));
        // 公允价值变动类科目余额 变量.公允价值变动科目余额 - 变量.T+1发生公允价值变动
        secT1HandleParamterDto.setGyjzbdSubjectBalance(ArithmeticUtil.subtract(
            param.getVarGySubjectAmt(), paramT1.getGyjzbdSubjectBalance()));
        // 溢折价类科目余额 变量.溢折价科目余额 - 变量.T+1发生利息调整
        secT1HandleParamterDto.setZyjSubjectBalance(
            ArithmeticUtil.subtract(param.getVarYzjSubjectAmt(), paramT1.getZyjSubjectBalance()));
        // 投资目的 交易类账务处理结果.投资目的
        secT1HandleParamterDto.setInvAim(acctEntity.getInvAim());
        // 交易日 交易明细.交易日
        secT1HandleParamterDto.setDealDate(dealNow.getTradeDate());
        // 交割日 交易明细.交割日
        secT1HandleParamterDto.setSettleDate(settleDate);
        // 业务明细代码 交易类账务处理结果.业务明细代码
        secT1HandleParamterDto.setBusiId(dealNow.getFinprodId());
        // 是否零息/贴现 变量.标的信息.息票品种为零息或贴现时 为是，否则 为否
        if (DictCodeList.息票品种_零息.getValue().equals(rstBaseinfo.getCouponSpecies())
            || DictCodeList.息票品种_贴现.getValue().equals(rstBaseinfo.getCouponSpecies())) {
            secT1HandleParamterDto.setNotLxtx(true);
        }
        else {
            secT1HandleParamterDto.setNotLxtx(false);
        }
        // 账套代码 交易类账务处理结果.账套代码
        secT1HandleParamterDto.setBooksetId(acctEntity.getBooksetId());
        // T+1买入交易 变量.T+1买入交易
        secT1HandleParamterDto.setT1BuyDeal(paramT1.getT1BuyDeal());
        // T+1卖出交易 变量.T+1卖出交易
        secT1HandleParamterDto.setT1SellDeal(paramT1.getT1SellDeal());
        //交易场所
        secT1HandleParamterDto.setTradeMarket(rstBaseinfo.getTradeMarket());
        return secT1HandleService.doSecT1Handle(secT1HandleParamterDto);
    }
    
    private BigDecimal ifNull2Zero(BigDecimal b){
        return b == null ? BigDecimal.ZERO : b;
    }
}
