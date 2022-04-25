/*
 * 文件名：secLendFinFloatBookDetailService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年10月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.batch.seclendfinfloatbookdetail.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.tams.valuation.batch.common.accounting.entity.BookLatestInfoDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResultWithBokSummary;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.MarketPriceDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DataIndexSequenceType;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import com.joyintech.tams.common.base.entity.BokBooksetExt;
import com.joyintech.tams.common.base.entity.BokStatDetPosiiton;

/**
 * 债券借贷标的债券公允价值变动处理 processor相关业务处理
 * 
 * @author zhangwei
 * @version 1.0 2018年10月25日
 * @see SecLendFinFloatBookDetailService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class SecLendFinFloatBookDetailService {

    /**
     * 共通方法service
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 共通方法service
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogic bookLogic;

    /**
     * 记账公共类
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * DBUtil
     */
    @Autowired
    private DBUtil dBUtil;

    /**
     * process 数据处理
     * 
     * @param param
     * @return
     */
    public BookGenerateResultWithBokSummary execute(Map<String, Object> param) {

        // 获取reader返回值
        DrawBaseInfoSelectComResult acctEntity = (DrawBaseInfoSelectComResult)param.get(GlobalConstant.READER_RESULT);

        // 定义返回值
        BookGenerateResultWithBokSummary returnResult = new BookGenerateResultWithBokSummary();

        // 账务明细若为空赋初始值
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(Lists.newArrayList());
        }
        List<DrawBaseInfoSelectComDTO> listDrawBaseInfoSelectComDTONow = acctEntity.getListDrawBaseInfoSelectComDTO(); // 计提基础信息列表
        if (CollectionUtils.isEmpty(listDrawBaseInfoSelectComDTONow)) {
            return null;
        }

        // 获取待处理任务信息
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param.get("BATCHINFO");
        // 获取从reader中拿到的值
        Long taskPri = listDrawBaseInfoSelectComDTONow.get(0).getTaskPri();// 任务优先级（信息列表的任务优先级都是一致的，故取第一个）

        // 1. 数据准备
        // 1.1 定义变量
        BigDecimal costSubjectBalance = BigDecimal.ZERO;// 变量.成本科目余额
        BigDecimal costSubjectNum = BigDecimal.ZERO;// 变量.成本科目数量
        BigDecimal finFloatSubjectBalance = BigDecimal.ZERO;// 变量.公允价值变动科目余额
        String oCcy = null; // 变量.原币种
        // BigDecimal finFloatSubjectBalanceYestdy = BigDecimal.ZERO;// 变量.公允价值变动科目余额（昨日）
        BigDecimal marketPrice = null;// 变量.市价
        BigDecimal finFloat = BigDecimal.ZERO;// 变量.公允价值变动
        BigDecimal spilledPriceBalance = BigDecimal.ZERO; // 溢折价科目余额

        // 1.2 债券行情处理
        // 调用共通方法【市价查询-债券】取债券最新市价
        MarketPriceDto marketPriceDto = finProductCommonService.getBondMarketPrice(acctEntity.getHappenDate(),
            acctEntity.getBooksetId(), acctEntity.getBusiId(), DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue());//设计变更：投资目的固定值=FVTPL。 zhangyf 2018-11-29
        // 市价 = 返回值.市价
        marketPrice = marketPriceDto.getPrice();
        // 估值日期 = 返回值.估值日期
        Date valDate = marketPriceDto.getValDate();

        // 1.3 汇总信息查询
        // 变量.当日统计分析账务明细汇总信息 = 调用共通方法[账务类共通方法设计 - 统计分析账务明细汇总查询]
        BokStatDetPosiiton bokSummaryTableIntraday = accountingCommonService.getCountAnalysis(
            acctEntity.getHappenDate(), acctEntity.getHappenDate(), acctEntity.getBookDate(), acctEntity.getBooksetId(),
            acctEntity.getBokDetailId(), null, null);

        // 2.调用共通查询发生日最新余额
        // 2.1 调用公用方法：【科目余额查询 - 期待方向】
        SubjectBalance subjectBalance = this.getSubjectBalanceEx(acctEntity.getBooksetId(), acctEntity.getBokDetailId(),
            acctEntity.getHappenDate(), acctEntity.getBookDate(), DictCodeList.余额方向_贷方.getValue(),
            DictCodeBaseSubjectNatrue.金融负债成本.getValue(), taskPri);

        costSubjectBalance = subjectBalance.getBalance(); // 变量.成本科目余额 = 科目余额
        costSubjectNum = subjectBalance.getNum(); // 变量.成本科目数量 = 数量
        oCcy = subjectBalance.getOCcy(); // 变量.原币种 = 原币种

        // 2.2 公允价值变动类科目余额
        // 调用公用方法：【科目余额查询 - 期待方向】, 变量.公允价值变动科目余额 = 科目余额
        finFloatSubjectBalance = this.getSubjectBalance(acctEntity.getBooksetId(), acctEntity.getBokDetailId(),
            acctEntity.getHappenDate(), acctEntity.getBookDate(), acctEntity.getInvAim(),
            DictCodeList.余额方向_贷方.getValue(), DictCodeBaseSubjectNatrue.公允价值变动.getValue(), taskPri);

        // 2.4 应计利息类科目余额
        // 调用公用方法：【科目余额查询 - 期待方向】, 变量.应计利息科目余额 = 科目余额
        BigDecimal intSubjectBalance = BigDecimal.ZERO; // 变量.应计利息科目余额
        intSubjectBalance = this.getSubjectBalanceEx(acctEntity.getBooksetId(), acctEntity.getBokDetailId(),
            acctEntity.getHappenDate(), acctEntity.getBookDate(), DictCodeList.余额方向_贷方.getValue(),
            DictCodeBaseSubjectNatrue.应计利息.getValue(), taskPri).getBalance();

        // 2.7 T+1相关发生额统计
        // 变量.当日计算成本科目数量 = 变量.当日成本科目数量
        BigDecimal costSubjectNumIntrady = costSubjectNum;
        // 变量.当日计算成本科目余额 = 变量.当日成本科目余额
        BigDecimal costSubjectBalanceIntrady = costSubjectBalance;
        // 变量.当日计算摊销溢折价科目余额 = 变量.溢折价科目余额
        // BigDecimal spilledPriceBalanceIntrady = spilledPriceBalance;
        // 变量.当日计算应计利息科目余额 = 变量.应计利息科目余额
        BigDecimal intSubjectBalanceIntrady = intSubjectBalance;
        // 变量.当日计算公允价值变动科目余额 = 变量.公允价值变动科目余额
        BigDecimal finFloatSubjectBalanceIntrady = finFloatSubjectBalance;

        // 调用账务类共通【账套参数查询】
        // 变量.公允价值变动计提日类型
        String fvCalcdateType = DictCodeListExt1.公允价值变动计提日类型_交易日.getValue();
        List<BokBooksetExt> listBokBooksetExt = accountingCommonService.getValBokBooksetExt(acctEntity.getBooksetId(),
            Arrays.asList(DictCode.公允价值变动计提日类型.getValue()));
        if (CollectionUtils.isNotEmpty(listBokBooksetExt)) {
            fvCalcdateType = listBokBooksetExt.get(0).getParamValue();
        }

        // if (变量.公允价值变动计提日类型 = "交割日")
        if (DictCodeListExt1.公允价值变动计提日类型_交割日.getValue().equals(fvCalcdateType)) {
            // 买入场景
            // 调用账务共通设计->【凭证最新账务查询】
            List<String> baseSubjectNatureList = Arrays.asList(DictCodeBaseSubjectNatrue.金融负债成本.getValue(),
                DictCodeBaseSubjectNatrue.应计利息.getValue(), DictCodeBaseSubjectNatrue.公允价值变动.getValue());
            List<BookLatestInfoDto> listBookLatestInfoDto = accountingCommonService.getBookLatestInfo(
                acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntity.getBokDetailId(), baseSubjectNatureList,
                DictCodeListExt1.指令类型_债券借贷_融入债券卖出.getValue(), null, null, acctEntity.getHappenDate(), null, null);

            // LOOP 变量.买入账务明细列表 -> 变量.账务明细
            if (CollectionUtils.isNotEmpty(listBookLatestInfoDto)) {
                for (BookLatestInfoDto item : listBookLatestInfoDto) {
                    // if(变量.账务明细.基础科目性质 = 金融负债成本 )
                    if (DictCodeBaseSubjectNatrue.金融负债成本.getValue().equals(item.getBaseSubjectNature())) {
                        // 变量.当日计算成本科目数量 -= 变量.账务明细.发生数量
                        costSubjectNumIntrady = ArithmeticUtil.subtract(costSubjectNumIntrady, item.getHappenNumber());
                        // 变量.当日计算成本科目余额 -= 变量.账务明细.原币发生额
                        costSubjectBalanceIntrady = ArithmeticUtil.subtract(costSubjectBalanceIntrady,
                            item.getoHappenAmt());
                    }
                    // if(变量.账务明细.基础科目性质 = 应计利息 )
                    if (DictCodeBaseSubjectNatrue.应计利息.getValue().equals(item.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(item.getCdFlag())) {
                            // 变量.当日计算应计利息科目余额 -= 变量.账务明细.原币发生额
                            intSubjectBalanceIntrady = ArithmeticUtil.subtract(intSubjectBalanceIntrady,
                                item.getoHappenAmt());
                        }
                        else {
                            // 变量.当日计算应计利息科目余额 += 变量.账务明细.原币发生额
                            intSubjectBalanceIntrady = ArithmeticUtil.doAdd(intSubjectBalanceIntrady,
                                item.getoHappenAmt());
                        }
                    }
                    // if(变量.账务明细.基础科目性质 = 公允价值变动 )
                    if (DictCodeBaseSubjectNatrue.公允价值变动.getValue().equals(item.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(item.getCdFlag())) {
                            // 变量.当日计算公允价值变动科目余额 -= 变量.账务明细.原币发生额
                            finFloatSubjectBalanceIntrady = ArithmeticUtil.subtract(finFloatSubjectBalanceIntrady,
                                item.getoHappenAmt());
                        }
                        else {
                            // 变量.当日计算公允价值变动科目余额 += 变量.账务明细.原币发生额
                            finFloatSubjectBalanceIntrady = ArithmeticUtil.doAdd(finFloatSubjectBalanceIntrady,
                                item.getoHappenAmt());
                        }
                    }

                }
            }

            // 卖出场景
            // 调用账务共通设计->【凭证最新账务查询】
            baseSubjectNatureList = Arrays.asList(DictCodeBaseSubjectNatrue.金融负债成本.getValue(),
                DictCodeBaseSubjectNatrue.应计利息.getValue(), DictCodeBaseSubjectNatrue.公允价值变动.getValue());
            listBookLatestInfoDto = accountingCommonService.getBookLatestInfo(acctEntity.getBooksetId(),
                acctEntity.getBookDate(), acctEntity.getBokDetailId(), baseSubjectNatureList,
                DictCodeListTableId.会计场景代码_债券借贷_融入债券卖出后购回.getValue(), null, null, acctEntity.getHappenDate(), null, null);

            // LOOP 变量.买入账务明细列表 -> 变量.账务明细
            if (CollectionUtils.isNotEmpty(listBookLatestInfoDto)) {
                for (BookLatestInfoDto item : listBookLatestInfoDto) {
                    // if(变量.账务明细.基础科目性质 = 金融负债成本 )
                    if (DictCodeBaseSubjectNatrue.金融负债成本.getValue().equals(item.getBaseSubjectNature())) {
                        // 变量.当日计算成本科目数量 += 变量.账务明细.发生数量
                        costSubjectNumIntrady = ArithmeticUtil.doAdd(costSubjectNumIntrady, item.getHappenNumber());
                        // 变量.当日计算成本科目余额 += 变量.账务明细.原币发生额
                        costSubjectBalanceIntrady = ArithmeticUtil.doAdd(costSubjectBalanceIntrady,
                            item.getoHappenAmt());
                    }
                    // if(变量.账务明细.基础科目性质 = 应计利息 )
                    if (DictCodeBaseSubjectNatrue.应计利息.getValue().equals(item.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(item.getCdFlag())) {
                            // 变量.当日计算应计利息科目余额 -= 变量.账务明细.原币发生额
                            intSubjectBalanceIntrady = ArithmeticUtil.subtract(intSubjectBalanceIntrady,
                                item.getoHappenAmt());
                        }
                        else {
                            // 变量.当日计算应计利息科目余额 += 变量.账务明细.原币发生额
                            intSubjectBalanceIntrady = ArithmeticUtil.doAdd(intSubjectBalanceIntrady,
                                item.getoHappenAmt());
                        }
                    }
                    // if(变量.账务明细.基础科目性质 = 公允价值变动 )
                    if (DictCodeBaseSubjectNatrue.公允价值变动.getValue().equals(item.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(item.getCdFlag())) {
                            // 变量.当日计算公允价值变动科目余额 -= 变量.账务明细.原币发生额
                            finFloatSubjectBalanceIntrady = ArithmeticUtil.subtract(finFloatSubjectBalanceIntrady,
                                item.getoHappenAmt());
                        }
                        else {
                            // 变量.当日计算公允价值变动科目余额 += 变量.账务明细.原币发生额
                            finFloatSubjectBalanceIntrady = ArithmeticUtil.doAdd(finFloatSubjectBalanceIntrady,
                                item.getoHappenAmt());
                        }
                    }

                }
            }

        }
        // if（变量.当日成本科目数量 = 0 and 变量.当日计算成本科目数量 = 0 ）
        if (costSubjectNum.compareTo(BigDecimal.ZERO) == 0
            && costSubjectNumIntrady.compareTo(BigDecimal.ZERO) == 0) {
            // 变量.公允价值变动 = -1*变量.公允价值变动科目余额
            finFloat = finFloatSubjectBalance.negate();
        }
        else {
            // if（变量.市价 = null）
            if (marketPrice == null || marketPrice.compareTo(BigDecimal.ZERO) == 0) {
                finFloat = BigDecimal.ZERO; // 变量.公允价值变动 = 0
            }
            else {
                // 变量.公允价值变动 = （变量.市价*变量.当日计算成本科目数量 - 变量.当日计算成本科目余额） -变量.当日计算公允价值变动科目余额
                finFloat = ArithmeticUtil.subtract(
                    ArithmeticUtil.subtract(ArithmeticUtil.multiply(marketPrice, costSubjectNumIntrady),
                        costSubjectBalanceIntrady),
                    finFloatSubjectBalanceIntrady);
            }
            
        }

        for (DrawBaseInfoSelectComDTO acctEntityEx : listDrawBaseInfoSelectComDTONow) {
            // 查询条件
            List<TargetSearchDto> queryCriteria = Lists.newArrayList();
            // 标的信息条件
            TargetSearchDto searchBaseinfo = new TargetSearchDto();
            searchBaseinfo.setQueryType(ValDictCodeList.标的_信息.getValue());
            queryCriteria.add(searchBaseinfo);
            Map<TargetSearchDto, Object> targetSearchMap = finProductCommonService.getTargetInfo(
                acctEntityEx.getBusiId(), queryCriteria);
            // 变量.标的信息 = 上述共通返回标的信息
            TargetFinProduct rstBaseinfo = (TargetFinProduct)targetSearchMap.get(searchBaseinfo);
            if (rstBaseinfo == null) {
                break;
            }

            // 3.1 账务处理
            AccountDriver ad = new AccountDriver();
            // ad.会计场景代码="债券借贷-融入券卖出后重估"
            ad.setTableId(DictCodeListTableId.会计场景代码_债券借贷_融入券卖出后重估.getValue());
            // ad.记账场景树节点 = 会计场景
            ad.setTreeNode(acctEntity.getBookSetTreeNode());

            // 初始化定义 - 存放该笔记账的场景要素 map定义
            Map<String, Object> accountSceneMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
            Map<String, Object> accountCurrencyMap = new HashMap<>();

            // 共通元素记账场景要素map设值 accountSceneMap
            accountSceneMap = bookCommonLogic.accrualElementAssign(acctEntity, acctEntityEx);

            // 备注 <- 会计场景代码(名称) + "记账"
            accountSceneMap.put(BookConstant.BOOK_REMARK, DictCodeListTableId.会计场景代码_债券借贷_融入券卖出后重估.getCodeName() + "记账");
            accountSceneMap.put(BookConstant.TYPE_1, rstBaseinfo.getType1()); // 分类1 <- 变量.标的信息.分类1
            accountSceneMap.put(BookConstant.TYPE_2, rstBaseinfo.getType2()); // 分类2 <- 变量.标的信息.分类2
            accountSceneMap.put(BookConstant.TYPE_3, rstBaseinfo.getType3()); // 分类3 <- 变量.标的信息.分类3
            accountSceneMap.put(BookConstant.TYPE_4, rstBaseinfo.getType4()); // 分类4 <- 变量.标的信息.分类4
            accountSceneMap.put(BookConstant.TYPE_5, rstBaseinfo.getType5()); // 分类5 <- 变量.标的信息.分类5
            accountSceneMap.put(BookConstant.TYPE_6, rstBaseinfo.getType6()); // 分类6 <- 变量.标的信息.分类6

            // 记账金额元素map塞值
            // 变量.公允价值变动 > 0
            // 公允价值变动 <- 变量.原币种
            if (finFloat.compareTo(BigDecimal.ZERO) >= 0) {
                // 公允价值变动损益_借 <- 变量.公允价值变动 FLOAT_1_D
                accountAmountMap.put(BookConstant.FLOAT_1_D, ArithmeticUtil.round(finFloat, NumberEnum.MONEY));
                accountCurrencyMap.put(BookConstant.FLOAT_1_D, oCcy);

                // 公允价值变动_贷 <- 变量.公允价值变动 FVTPL_INTADJ_C
                accountAmountMap.put(BookConstant.FVTPL_INTADJ_C, ArithmeticUtil.round(finFloat, NumberEnum.MONEY));
                accountCurrencyMap.put(BookConstant.FVTPL_INTADJ_C, oCcy);
            }
            else {
                // 公允价值变动损益_贷 <- 变量.公允价值变动*-1 FLOAT_1_C
                accountAmountMap.put(BookConstant.FLOAT_1_C,
                    ArithmeticUtil.round(ArithmeticUtil.multiply(finFloat, new BigDecimal(-1)), NumberEnum.MONEY));
                accountCurrencyMap.put(BookConstant.FLOAT_1_C, oCcy);

                // 公允价值变动_借 <- 变量.公允价值变动*-1 FVTPL_INTADJ_D
                accountAmountMap.put(BookConstant.FVTPL_INTADJ_D,
                    ArithmeticUtil.round(ArithmeticUtil.multiply(finFloat, new BigDecimal(-1)), NumberEnum.MONEY));
                accountCurrencyMap.put(BookConstant.FVTPL_INTADJ_D, oCcy);
            }

            // 全局变量的更新
            // 变量.公允价值变动科目余额 = 变量.公允价值变动科目余额 + 变量.公允价值变动
            finFloatSubjectBalance = ArithmeticUtil.doAdd(finFloatSubjectBalance, finFloat);

            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
            ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
            ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

            // 记账凭证生成
            BookGenerateResult resultNowNow = bookLogic.genAccount(ad, batchExecuteInfo);
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());

            // 3.2 统计分析账务明细汇总信息赋值
            // 定义当日统计分析账务明细汇总信息
            BokStatDetPosiiton bokSummaryTableNew = new BokStatDetPosiiton();
            // if 变量.当日统计分析账务明细汇总信息 is null
            if (bokSummaryTableIntraday == null || StringUtils.isEmpty(bokSummaryTableIntraday.getBooksetId())) {
                // 变量.当日统计分析账务明细汇总信息.当前持仓 = 变量.当日成本科目数量
                bokSummaryTableNew.setPosAmt(costSubjectNum);
                // 变量.当日统计分析账务明细汇总信息.当日份额 = 变量.当日计算成本科目数量
                bokSummaryTableNew.setTdyPosition(costSubjectNumIntrady);
                // 变量.当日统计分析账务明细汇总信息.投资目的 = 计提类账务处理结果.投资目的
                bokSummaryTableNew.setInvAim(acctEntity.getInvAim());
                // 变量.当日统计分析账务明细汇总信息.当日成本 = 变量.当日成本科目余额
                // bokSummaryTableNew.setTdyCostAmt(costSubjectBalance);
                //modify jinmaodong 20190517 修改当日成本 = 变量.当日成本科目余额 + 变量.溢折价科目余额+变量.利息调整
                bokSummaryTableNew.setTdyCostAmt(ArithmeticUtil.doAdd(costSubjectBalance, spilledPriceBalance));

                // 变量.当日统计分析账务明细汇总信息.当日公允价值变动 = 变量.公允价值变动
                bokSummaryTableNew.setTdyFloatIngpl(finFloat);
                // 变量.当日统计分析账务明细汇总信息.金融产品类型 = 交易标的DTO.金融产品类型
                bokSummaryTableNew.setFinprodType(rstBaseinfo.getFinprodType());
                // 变量.当日统计分析账务明细汇总信息.剩余期限 = 空
                bokSummaryTableNew.setEndDays1(null);
                // 变量.当日统计分析账务明细汇总信息.剩余存续期 = 空
                bokSummaryTableNew.setEndDays2(null);
                // 变量.当日统计分析账务明细汇总信息.剩余期限用成本 = 变量.当日成本科目余额 + 变量.溢折价科目余额
                bokSummaryTableNew.setEndDaysCostAmt(ArithmeticUtil.doAdd(costSubjectBalance, spilledPriceBalance));
                // 变量.当日统计分析账务明细汇总信息.业务明细代码 = 计提类账务处理结果.业务明细代码
                bokSummaryTableNew.setBusiId(acctEntity.getBusiId());
                // 变量.当日统计分析账务明细汇总信息.行情日期= 计提类账务处理结果.估值日期
                bokSummaryTableNew.setValDate(valDate);
                // 变量.当日统计分析账务明细汇总信息.起息日 = 变量.标的信息.起息日
                bokSummaryTableNew.setVdate(rstBaseinfo.getVdate());
                // 变量.当日统计分析账务明细汇总信息.到期日 = 变量.标的信息.实际到期日
                bokSummaryTableNew.setMdate(rstBaseinfo.getActmdate());

                // 账套代码
                bokSummaryTableNew.setBooksetId(acctEntity.getBooksetId());
                // 发生日期
                bokSummaryTableNew.setHappenDate(acctEntity.getHappenDate());
                // 入账日期
                bokSummaryTableNew.setBookDate(acctEntity.getBookDate());
                // 账务明细代码
                bokSummaryTableNew.setBokDetailId(acctEntity.getBokDetailId());
                // 账套日期
                bokSummaryTableNew.setBooksetDate(acctEntityEx.getBooksetDate());
                // 处理序列
                bokSummaryTableNew.setBookSummaryOrder(
                    new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.账务汇总表_处理序列)));
                
                bokSummaryTableNew.setCcy(rstBaseinfo.getCcy());
            }
            else {
                BeanUtil.copyPropertiesIgnoreNull(bokSummaryTableNew, bokSummaryTableIntraday);
                // 变量.当日统计分析账务明细汇总信息.当日公允价值变动 = 变量.公允价值变动
                bokSummaryTableNew.setTdyFloatIngpl(finFloat);
                // 变量.当日统计分析账务明细汇总信息.金融产品类型 = 交易标的DTO.金融产品类型
                bokSummaryTableNew.setFinprodType(rstBaseinfo.getFinprodType());
                // 变量.当日统计分析账务明细汇总信息.行情日期= 计提类账务处理结果.估值日期
                bokSummaryTableNew.setValDate(valDate);
            }

            // 与金茂东确认：当前【统计分析明细表（账务明细）】 相关本位币字段值 = 对应原币字段值 yangjian 2019/08/13 ======== beign
            //本位币
            bokSummaryTableNew.setbCcy(bokSummaryTableNew.getCcy());
            //当日发生应计利息_本位币
            bokSummaryTableNew.setTdyIntincexpAddB(bokSummaryTableNew.getTdyIntincexpAdd());
            // 当日成本_本位币
            bokSummaryTableNew.setTdyCostAmtB(bokSummaryTableNew.getTdyCostAmt());
            // 当日公允价值变动_本位币
            bokSummaryTableNew.setTdyFloatIngplB(bokSummaryTableNew.getTdyFloatIngpl());
            // 当日累计公允价值变动_本位币
            bokSummaryTableNew.setTdyFloatIngplExpB(bokSummaryTableNew.getTdyFloatIngplExp());
            // 当日应计利息_本位币
            bokSummaryTableNew.setTdyIntincexpB(bokSummaryTableNew.getTdyIntincexp());
            // 与金茂东确认：当前【统计分析明细表（账务明细）】 相关本位币字段值 = 对应原币字段值 yangjian 2019/08/13 ======== end


            // 添加到返回值中
            returnResult.getBokSummaryList().add(bokSummaryTableNew);
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
     * @param invAim 投资目的
     * @param expBalanceDirect 期待余额方向
     * @param baseSubjectNature 基础科目性质
     * @param taskPri 任务优先级
     * @return BigDecimal 科目余额
     * @see
     * @since 1.0
     */
    private BigDecimal getSubjectBalance(String booksetId, String bokDetailId, Date happenDate, Date bookDate,
                                         String invAim, String expBalanceDirect, String baseSubjectNature,
                                         Long taskPri) {
        // 定义返回的科目余额
        BigDecimal balance = BigDecimal.ZERO;

        // 塞入要查询的基础科目性质
        List<String> baseSubjectNatureList = null;
        if (StringUtils.isNotEmpty(baseSubjectNature)) {
            baseSubjectNatureList = Lists.newArrayList();
            baseSubjectNatureList.add(baseSubjectNature);
        }

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId);
        searchDto.setBokDetailId(bokDetailId);
        searchDto.setHappenDate(happenDate);
        searchDto.setBookDate(bookDate);
        if(StringUtils.isNotEmpty(invAim)){
            searchDto.setInvAimList(Arrays.asList(invAim)); // 投资目的
        }
        searchDto.setTaskPri(taskPri);
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(searchDto,
            expBalanceDirect);

        // 获取科目余额
        if (CollectionUtils.isNotEmpty(subjectBalanceList)) {
            balance = subjectBalanceList.get(0).getBalance();
        }

        return balance;
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
     * @return BigDecimal 科目余额
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalanceEx(String booksetId, String bokDetailId, Date happenDate, Date bookDate,
                                               String expBalanceDirect, String baseSubjectNature, Long taskPri) {
        // 定义返回的科目余额
        SubjectBalance balance = new SubjectBalance();

        // 塞入要查询的基础科目性质
        List<String> baseSubjectNatureList = null;
        if (StringUtils.isNotEmpty(baseSubjectNature)) {
            baseSubjectNatureList = Lists.newArrayList();
            baseSubjectNatureList.add(baseSubjectNature);
        }

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId); // 账套代码
        searchDto.setBokDetailId(bokDetailId); // 账务明细代码
        searchDto.setHappenDate(happenDate); // 发生日
        searchDto.setBookDate(bookDate); // 入账日
        searchDto.setTaskPri(taskPri); // 任务优先级
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList); // 基础科目性质
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(searchDto,
            expBalanceDirect);

        // 获取科目余额
        if (CollectionUtils.isNotEmpty(subjectBalanceList)) {
            balance = subjectBalanceList.get(0);
        }
        else {
            balance.setBalance(BigDecimal.ZERO);
            balance.setNum(BigDecimal.ZERO);
        }

        return balance;
    }

}
