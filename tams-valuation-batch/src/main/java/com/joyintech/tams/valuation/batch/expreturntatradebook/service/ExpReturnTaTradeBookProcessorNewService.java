/*
 * 文件名：ExpReturnTaTradeBookProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：zhangzhao
 * 修改时间：2018年5月17日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.expreturntatradebook.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.SubjectMappingConfig;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoComDTO;
import com.joyintech.tams.valuation.batch.common.dealinfoselectcom.dto.FinProductDealInfoResult;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrueNew;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 预期收益型TA交易入账批处理 process相关业务处理
 * 
 * @author zhangzhao
 * @version 1.0 2018年5月17日
 * @see ExpReturnTaTradeBookProcessorNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ExpReturnTaTradeBookProcessorNewService {

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
//    @Autowired
//    private FinProductCommonService finProductCommonService;

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
//        BigDecimal varYjlx = BigDecimal.ZERO;
        // 1.2 标的信息查询
        List<FinProductDealInfoComDTO> listFinProductDealInfoComDTONow = acctEntity.getListFinProductDealInfoComDTO();
        // String finprodId = listFinProductDealInfoComDTONow.get(0).getFinprodId();
        // TargetFinProduct targetFinProduct = this.getTargetFinProduct(finprodId);

        // 1.3 参数获取
        // 分红尾差处理方式
        /*
         * String bonusTailMode = null; List<BokBooksetExt> listValBokBooksetExt =
         * accountingCommonService.getValBokBooksetExt( acctEntity.getBooksetId(), new
         * ArrayList<String>(Arrays.asList(DictCodeList.账套配置表参数项_分红尾差处理方式.getValue()))); if
         * (listValBokBooksetExt != null && listValBokBooksetExt.size() > 0) { bonusTailMode =
         * listValBokBooksetExt.get(0).getParamValue(); }
         */
        // 最新实收资本
        // 调用共通方法：科目余额查询-期待方向
        SubjectBalance costBalance = this.getSubjectBalance(acctEntity.getBooksetId(),
            acctEntity.getBokDetailId(), acctEntity.getHappenDate(),
            acctEntity.getBookDate(), DictCodeList.余额方向_贷方.getValue(),
            DictCodeBaseSubjectNatrueNew.实收资本.getValue(),
            null);
        if (costBalance != null) {
            varSszb = costBalance.getBalance() == null ? BigDecimal.ZERO : costBalance.getBalance();
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
            // 交易方向 - 产品认购、产品申购、份额强增
            if (DictCodeList.交易方向_产品认购.getValue().equals(dealNow.getPs())
                || DictCodeList.交易方向_产品申购.getValue().equals(dealNow.getPs())
                || DictCodeList.交易方向_份额强增.getValue().equals(dealNow.getPs())) {
                // 设置账务引擎参数
                ad.setTableId(DictCodeListTableId.会计场景代码_产品_预期收益型_认购申购.getValue());

                // 全局变量处理
                // 变量：实收资本 += 金额元素.申购金额
                // varSszb = ArithmeticUtil.doAdd(varSszb, dealNow.getTradeAmt());

                // 记账金额元素map塞值
                // 申购金额 <- 金额元素.申购金额
                accountAmountMap.put(BookConstant.DEALAMT,
                    ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));
                // 发生数量 <- 金额元素.交易份额
                accountAmountMap.put(BookConstant.ACCQUA,
                    ArithmeticUtil.round(dealNow.getShareAmt(), NumberEnum.MONEY));

                // 记账场景要素map塞值
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                    DictCodeListTableId.会计场景代码_产品_预期收益型_认购申购.getCodeName());// 凭证说明

            } // 交易方向 - 产品到期、份额强减
            else if (DictCodeList.交易方向_产品到期.getValue().equals(dealNow.getPs())
                     || DictCodeList.交易方向_份额强减.getValue().equals(dealNow.getPs())
                     || DictCodeList.交易方向_产品赎回.getValue().equals(dealNow.getPs())
                     
               		// 与王之昂金强确认：所有产品增加（1、交易方向加提前赎回、违约赎回，估值走赎回分录，2、交易方向加提前终止，估值走到期分录。）
                    || DictCodeList.交易方向_产品提前赎回.getValue().equals(dealNow.getPs()) 
               		|| DictCodeList.交易方向_产品违约赎回.getValue().equals(dealNow.getPs())
               		|| DictCodeList.交易方向_提前终止.getValue().equals(dealNow.getPs())
            		
            		) {
                // 设置账务引擎参数
                ad.setTableId(DictCodeListTableId.会计场景代码_产品_预期收益型_赎回到期.getValue());

                // 全局变量处理
                // 变量：实收资本 = 变量：实收资本 - 金额元素.赎回金额
                // varSszb = ArithmeticUtil.subtract(varSszb, dealNow.getPrinAmt());

                // 记账金额元素map塞值
                // 金额元素.交易份额
                accountAmountMap.put(BookConstant.ACCQUA,
                    ArithmeticUtil.round(dealNow.getShareAmt(), NumberEnum.MONEY));
                // 金额元素.赎回金额
                accountAmountMap.put(BookConstant.DEALAMT,
                    ArithmeticUtil.round(dealNow.getPrinAmt(), NumberEnum.MONEY));

                // 记账场景要素map塞值
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                    DictCodeListTableId.会计场景代码_产品_预期收益型_赎回到期.getCodeName());// 凭证说明

            } // 交易方向 -中间付息
            else if (DictCodeList.交易方向_中间付息.getValue().equals(dealNow.getPs())||DictCodeList.交易方向_赎回收益确认.getValue().equals(dealNow.getPs())) {
                //应付利润
                BigDecimal varYflr = BigDecimal.ZERO;
                // 设置账务引擎参数
                ad.setTableId(DictCodeListTableId.会计场景代码_产品_预期收益型_兑付客户收益.getValue());
                if(DictCodeList.是否_否.getValue().equals(dealNow.getIsClean())||(DictCodeList.交易方向_赎回收益确认.getValue().equals(dealNow.getPs())
                    && !ArithmeticUtil.equals(varSszb, dealNow.getShareAmt()))){
                    varYflr=dealNow.getTradeAmt();
                }else{
                    Long taskpri=null;
                    Date happenDate=DateUtils.addDays(dealNow.getMdate()==null?dealNow.getTradeDate():dealNow.getMdate(),-1);
                    if(happenDate.compareTo(acctEntity.getBookDate())==0){
                        taskpri=listFinProductDealInfoComDTONow.get(0).getTaskPri();
                    }else{
                        // 调用共通方法：科目余额汇总查询(接口不接到期日，取交易日)
                        SubjectBalance subjectBalance = this.getSubjectBalance(acctEntity.getBooksetId(),
                            acctEntity.getBokDetailId(), happenDate,
                            acctEntity.getBookDate(), DictCodeList.余额方向_贷方.getValue(),
                            DictCodeBaseSubjectNatrueNew.应付利润.getValue(),
                            taskpri);
                        if (subjectBalance != null) {
                            varYflr = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
                        }
                        // 记账场景要素map塞值
                        accountSceneMap.put(BookConstant.MDATE,dealNow.getMdate()==null?dealNow.getTradeDate():dealNow.getMdate());// 凭证说明
                    }
                }

     
                // 全局变量处理
                // 变量：应计利息 = 变量：应计利息 - 金额元素.客户收益
                // varYjlx = ArithmeticUtil.subtract(varYjlx, dealNow.getTradeAmt());
                // 金额元素.应付客户收益金额 = 上述返回科目余额 subjectBalance.getBalance();
                accountAmountMap.put(BookConstant.DEALAMT,
                        ArithmeticUtil.round(varYflr, NumberEnum.MONEY));
                // 金额元素.确认客户收益金额 = [待处理交易].[交易金额] dealNow.getTradeAmt();
                accountAmountMap.put(BookConstant.CUSTREV,
                        ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));
                // 差额 = 金额元素.确认客户收益金额 - 金额元素.应付客户收益金额
                BigDecimal dif = BigDecimal.ZERO;
                dif = ArithmeticUtil.subtract(dealNow.getTradeAmt(), varYflr);
                // IF(差额>=0) 金额元素.借贷差额_借 = 差额
                if (dif.compareTo(BigDecimal.ZERO) >= 0) {
                    accountAmountMap.put(BookConstant.DSCINC_D,
                        ArithmeticUtil.round(dif, NumberEnum.MONEY));
                } // ELSE 金额元素.借贷差额_贷 = -差额 （取反）
                else {
                    accountAmountMap.put(BookConstant.DSCINC_C,
                        ArithmeticUtil.round(dif.negate(), NumberEnum.MONEY));
                }

                // 记账金额元素map塞值
                // 金额元素.客户收益
                // accountAmountMap.put(BookConstant.CUSTREV,
                // ArithmeticUtil.round(dealNow.getTradeAmt(), NumberEnum.MONEY));

                // 记账场景要素map塞值
                accountSceneMap.put(BookConstant.BOOK_REMARK,
                    DictCodeListTableId.会计场景代码_产品_预期收益型_兑付客户收益.getCodeName());// 凭证说明

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

            // 辅助核算项配置信息
            SubjectMappingConfig mappingConfig = new SubjectMappingConfig();
            mappingConfig.setAccountType(dealNow.getAcctType());
            ad.setSubjectMappingConfigDefault(mappingConfig);
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
    /*
     * private TargetFinProduct getTargetFinProduct(String bokDetailId) { // 定义查询list
     * List<TargetSearchDto> targetSearchDtoList = new ArrayList<TargetSearchDto>(); // 定义查询类型 实体类
     * TargetSearchDto targetSearchDto = new TargetSearchDto();
     * targetSearchDto.setQueryType(ValDictCodeList.标的_信息.getValue()); // 查询实体类塞入查询实体类List中
     * targetSearchDtoList.add(targetSearchDto); // 获取标的信息 Map<TargetSearchDto, Object> targetMap =
     * finProductCommonService.getTargetInfo(bokDetailId, targetSearchDtoList); TargetFinProduct
     * targetFinProduct = (TargetFinProduct)targetMap.get(targetSearchDto); // 确保不为null if
     * (targetFinProduct == null) { targetFinProduct = new TargetFinProduct(); } return
     * targetFinProduct; }
     */

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
        if (StringUtils.isNotEmpty(baseSubjectNature)) {
            baseSubjectNatureList = new ArrayList<String>();
            baseSubjectNatureList.add(baseSubjectNature);
        }

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
//        if (searchDto.getSpecialAccount() != null) {
            searchDto.setBokDetailId(bokDetailId);
//        }
        searchDto.setBooksetId(booksetId);
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
    // private Date addDate(Date nowDate, int days) {
    // // 获取日历实例
    // Calendar calendar = Calendar.getInstance();
    // calendar.setTime(nowDate);
    // calendar.add(Calendar.DAY_OF_MONTH, days);
    // Date day = calendar.getTime();
    //
    // return day;
    // }
}
