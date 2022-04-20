/*
 * 文件名：SecLendFinDrawBookDetailService.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：估值核算系统V1.0 修改人：ZhangYangFei
 * 修改时间：2018年10月25日 修改内容：新建 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.seclendfindrawbookdetail.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.shell.param.AccurIntCalOutParameter;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokStatDetPosiiton;
import com.joyintech.tams.common.base.entity.FinExercisePlan;
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
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResultWithBokSummary;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.TreeNode;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.*;
import com.joyintech.tams.valuation.common.constants.*;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 债券借贷标的债券计提处理Service
 * 
 * @author ZhangYangFei
 * @version 1.0 2018年10月25日
 * @see SecLendFinDrawBookDetailNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class SecLendFinDrawBookDetailNewService {

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
     * DBUtil
     */
    @Autowired
    private DBUtil dBUtil;

    /**
     * process 数据处理
     * 
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public BookGenerateResultWithBokSummary execute(Map<String, Object> param) {
        // 获取reader返回值
        DrawBaseInfoSelectComResult acctEntity = (DrawBaseInfoSelectComResult)param.get(GlobalConstant.READER_RESULT);
        // 若reader传递的参数或交易明细为空，不进行后续处理
        if (acctEntity == null || CollectionUtils.isEmpty(acctEntity.getListDrawBaseInfoSelectComDTO())) {
            return null;
        }
        // 定义返回值
        BookGenerateResultWithBokSummary returnResult = new BookGenerateResultWithBokSummary();
        // 账务明细若为空赋初始值
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }

        // 获取待处理任务信息
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param.get("BATCHINFO");
        // 获取从reader中拿到的值:计提基础信息列表
        List<DrawBaseInfoSelectComDTO> listDrawBaseInfoSelectComDTONow = acctEntity.getListDrawBaseInfoSelectComDTO();
        Long taskPri = listDrawBaseInfoSelectComDTONow.get(0).getTaskPri();// 任务优先级（信息列表的任务优先级都是一致的，故取第一个）

        TreeNode bookSetTreeNodeNow = acctEntity.getBookSetTreeNode();// 会计场景
        AccountDriver ad;// 记账标准参数类
        Map<String, Object> accountSceneMap;// 记账场景要素map
        Map<String, BigDecimal> accountAmountMap;// 记账的相关金额要素 map
        Map<String, Object> accountCurrencyMap;// 记账的相关金额对应的币种 map

        // 1.数据准备
        // 1.1 变量定义
        // 定义 变量.百元券面面值
        BigDecimal varFaceValue = new BigDecimal(100);
        // 定义 变量.当日成本科目余额 = 计提类账务处理结果.科目余额
        BigDecimal varOAmtHappenDate = BigDecimal.ZERO;
        // 定义 变量.当日成本科目数量 = 计提类账务处理结果.数量
        BigDecimal varNumAmtHappenDate = BigDecimal.ZERO;
        // 定义 变量.应计利息科目余额 = 0
        BigDecimal varInterestAccBal = BigDecimal.ZERO;
        // 定义 变量.公允价值变动科目余额 = 0
        // BigDecimal varFairValueAccBal = BigDecimal.ZERO;
        // 定义 变量.币种 = 计提类账务处理结果.原币种
        String varCcy = StringUtil.EMPTY;
        // 定义 变量.上次结息日
        // Date varOInterestDay = null;
        // 定义 变量.昨日统计分析账务明细汇总信息
        BokStatDetPosiiton bokSummaryTableYestdy;
        // 定义 变量.当日统计分析账务明细汇总信息
        BokStatDetPosiiton bokSummaryTableNew;

        // 遍历计提基础信息，并记账
        for (DrawBaseInfoSelectComDTO acctEntityEx : listDrawBaseInfoSelectComDTONow) {
            varOAmtHappenDate = acctEntityEx.getoAmtHappenDate();
            varNumAmtHappenDate = ifNull2Zero(acctEntityEx.getNumAmtHappenDate());
            varCcy = acctEntityEx.getoCcy();
            varFaceValue = finProductCommonService.getFaceValue(acctEntityEx.getBusiId(), acctEntity.getHappenDate());
            if (null == varFaceValue) {
                varFaceValue = new BigDecimal(100);
            }

            // 1.3.汇总信息查询
            // 变量.昨日统计分析账务明细汇总信息 = 调用共通方法[账务类共通方法设计 - 统计分析账务明细汇总查询]
            bokSummaryTableYestdy = accountingCommonService.getCountAnalysis(DateUtil.addDay(acctEntity.getHappenDate(), -1),
                DateUtil.addDay(acctEntity.getHappenDate(), -1), acctEntity.getBookDate(), acctEntity.getBooksetId(),
                acctEntityEx.getBokDetailId(), null, null);

            /*
             * // 买入场景(调用账务共通设计->【凭证最新账务查询 (在途日)】) List<String> baseSubjectNatureList = new ArrayList<String>();//基础科目性质 = 金融负债成本
             * baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.交易性金融负债_成本.getValue()); // 变量.买入交易数量 int count = accountingCommonService.getBookLatestInfoOnway(
             * acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntityEx.getBokDetailId(), baseSubjectNatureList,
             * DictCodeListTableId.会计场景代码_债券借贷_借入债券卖出.getValue(),//场景代码 = 债券借贷-融入债券卖出 acctEntity.getHappenDate());
             */

            // 2.调用共通查询发生日最新余额
            // 2.1.标的类金融产品信息
            // 标的信息查询 ------------ start
            // 定义查询条件，但在myGetTargetInfo()赋值
            // 第1条记录 //标的信息
            TargetSearchDto searchBaseinfo = new TargetSearchDto();
            // 第2条记录 //还本计划
            TargetSearchDto searchRedinfo = new TargetSearchDto();
            // 第3条记录 //付息计划
            TargetSearchDto searchIntinfo = new TargetSearchDto();
            // 第4条记录 //报价计划
            TargetSearchDto searchCashinfo = new TargetSearchDto();
            // 第5条记录 //现金流支付规则
            TargetSearchDto searchPayinfo = new TargetSearchDto();

            // 标的信息查询，调用共通
            Map<TargetSearchDto, Object> targetInfoMap = myGetTargetInfo(acctEntityEx, searchBaseinfo, searchIntinfo,
                searchRedinfo, searchCashinfo, searchPayinfo);

            // 变量.标的信息 = 上述共通返回标的信息
            TargetFinProduct rstBaseinfo = targetInfoMap.get(
                searchBaseinfo) == null ? new TargetFinProduct() : (TargetFinProduct)targetInfoMap.get(searchBaseinfo);
            if (null == rstBaseinfo) {
                rstBaseinfo = new TargetFinProduct();
            }
            /*
             * // 变量.还本列表 = 上述共通返回还本列表
             * 
             * @SuppressWarnings("unchecked") List<TargetCashPlan> listRedinfo = targetInfoMap.get(searchRedinfo) == null ? new ArrayList<TargetCashPlan>() :
             * (List<TargetCashPlan>)targetInfoMap.get(searchRedinfo);
             */
            // 变量.付息列表 = 上述共通返回的计划列表
            @SuppressWarnings("unchecked")
            List<TargetCashPlan> listIntinfo = targetInfoMap.get(
                searchIntinfo) == null ? new ArrayList<TargetCashPlan>() : (List<TargetCashPlan>)targetInfoMap.get(searchIntinfo);
            // 变量.报价列表 = 上述共通返回报价列表
            @SuppressWarnings("unchecked")
            List<TargetCashCalcRule> listCashinfo = targetInfoMap.get(
                searchCashinfo) == null ? new ArrayList<TargetCashCalcRule>() : (List<TargetCashCalcRule>)targetInfoMap.get(
                    searchCashinfo);
            // 变量.支付规则 = 上述共通返回支付规则列表
            @SuppressWarnings("unchecked")
            List<TargetCashPayRule> listPayinfo = targetInfoMap.get(
                searchPayinfo) == null ? new ArrayList<TargetCashPayRule>() : (List<TargetCashPayRule>)targetInfoMap.get(
                    searchPayinfo);
            // 标的信息查询 ------------ end

            // 2.2.应计利息余额
            // 变量.应计利息余额 = 科目余额
          
            SubjectBalance subjectBalanceForfit = this.getSubjectBalance(acctEntity, DictCodeList.余额方向_贷方.getValue(),
                DictCodeBaseSubjectNatrueNew.交易性金融负债_应计利息.getValue(), taskPri);
            if (subjectBalanceForfit != null && subjectBalanceForfit.getBalance() != null) {
             	varInterestAccBal = subjectBalanceForfit.getBalance();
            }

            // 2.3.T+1相关发生额统计。调用账务共通设计->【凭证最新账务查询】
            // 买入场景 ------------ start
            // 场景代码 = 债券借贷-融入债券卖出
            List<BookLatestInfoDto> bookDetialListBuy = myGetBookLatestInfo(acctEntity,
                DictCodeListTableId.会计场景代码_债券借贷_借入债券卖出.getValue());

            // 变量.当日计算成本科目数量 = 变量.当日成本科目数量
            BigDecimal varDrjscbKmsl = varNumAmtHappenDate;
            // 变量.当日计算成本科目余额 = 变量.当日成本科目余额
            BigDecimal varDrjscbKmye = varOAmtHappenDate;
            // 变量.当日计算应计利息余额 = 变量.应计利息余额
            BigDecimal varDrjsyjlxKmye = varInterestAccBal;

            if (!CollectionUtils.isEmpty(bookDetialListBuy)) {
                for (BookLatestInfoDto bookinfo : bookDetialListBuy) {
                    // 变量.账务明细.基础科目性质 = 金融负债成本
                    if (DictCodeBaseSubjectNatrueNew.交易性金融负债_成本.getValue().equals(bookinfo.getBaseSubjectNature())) {
                        // 变量.当日计算成本科目数量 -= 变量.账务明细.发生数量
                        varDrjscbKmsl = ArithmeticUtil.subtract(varDrjscbKmsl, bookinfo.getHappenNumber());
                        // 变量.当日计算成本科目余额 -= 变量.账务明细.原币发生额
                        varDrjscbKmye = ArithmeticUtil.subtract(varDrjscbKmye, bookinfo.getoHappenAmt());
                    }
                    // 变量.账务明细.基础科目性质 = 应计利息
                    else if (DictCodeBaseSubjectNatrueNew.交易性金融负债_应计利息.getValue().equals(bookinfo.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为贷时
                        if (DictCodeList.余额方向_贷方.getValue().equals(bookinfo.getCdFlag())) {
                            // 变量.当日计算应计利息余额 -= 变量.账务明细.原币发生额
                            varDrjsyjlxKmye = ArithmeticUtil.subtract(varDrjsyjlxKmye, bookinfo.getoHappenAmt());
                        }
                        // 变量.账务明细.借贷方向为借时
                        else if (DictCodeList.余额方向_借方.getValue().equals(bookinfo.getCdFlag())) {
                            // 变量.当日计算应计利息余额 += 变量.账务明细.原币发生额
                            varDrjsyjlxKmye = ArithmeticUtil.doAdd(varDrjsyjlxKmye, bookinfo.getoHappenAmt());
                        }
                    }
                }
            }
            // 买入场景 ------------ end

            // 卖出场景 ------------ start
            // 场景代码 = 债券借贷-融入债券卖出后购回
            List<BookLatestInfoDto> bookDetialListSell = myGetBookLatestInfo(acctEntity,
                DictCodeListTableId.会计场景代码_债券借贷_融入债券卖出后购回.getValue());
            if (!CollectionUtils.isEmpty(bookDetialListSell)) {
                for (BookLatestInfoDto bookinfo : bookDetialListSell) {
                    // 变量.账务明细.基础科目性质 = 金融负债成本
                    if (DictCodeBaseSubjectNatrueNew.交易性金融负债_成本.getValue().equals(bookinfo.getBaseSubjectNature())) {
                        // 变量.当日计算成本科目数量 += 变量.账务明细.发生数量
                        varDrjscbKmsl = ArithmeticUtil.doAdd(varDrjscbKmsl, bookinfo.getHappenNumber());
                        // 变量.当日计算成本科目余额 += 变量.账务明细.原币发生额
                        varDrjscbKmye = ArithmeticUtil.doAdd(varDrjscbKmye, bookinfo.getoHappenAmt());
                    }
                    // 变量.账务明细.基础科目性质 = 应计利息
                    else if (DictCodeBaseSubjectNatrueNew.交易性金融负债_应计利息.getValue().equals(bookinfo.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.余额方向_借方.getValue().equals(bookinfo.getCdFlag())) {
                            // 变量.当日计算应计利息余额 -= 变量.账务明细.原币发生额
                            varDrjsyjlxKmye = ArithmeticUtil.subtract(varDrjsyjlxKmye, bookinfo.getoHappenAmt());
                        }
                        // 变量.账务明细.借贷方向为贷时
                        else if (DictCodeList.余额方向_贷方.getValue().equals(bookinfo.getCdFlag())) {
                            // 变量.当日计算应计利息余额 += 变量.账务明细.原币发生额
                            varDrjsyjlxKmye = ArithmeticUtil.doAdd(varDrjsyjlxKmye, bookinfo.getoHappenAmt());
                        }
                    }
                }
            }
            // 卖出场景 ------------ end

            // 3.出账处理
            // 3.1利息计提
            // 标的信息.到期日 <= 计提类账务处理结果.发生日 or 标的信息.起息日 > 计提类账务处理结果.发生日
            if (DateUtil.compareDate(rstBaseinfo.getMdate(), acctEntity.getHappenDate()) <= 0
                || DateUtil.compareDate(rstBaseinfo.getVdate(), acctEntity.getHappenDate()) > 0) {
                continue;
            }

            ad = new AccountDriver();
            ad.setTreeNode(bookSetTreeNodeNow);// 塞入记账场景树节点 = 会计场景
            // ad.会计场景代码="债券借贷-融入债券利息计提"
            ad.setTableId(DictCodeListTableId.会计场景代码_债券借贷_借入债券利息计提.getValue());
            // ad.凭证类型=#正常记账#;
            // ad.账套代码=计提类账务处理结果.账套代码;
            // ad.交易编号= 空

            // 定义 变量.本日应计利息余额 = 0
            BigDecimal tdyInterestAccBal = BigDecimal.ZERO;
            // 变量.应计利息
            BigDecimal intAmt = BigDecimal.ZERO;

            // 变量.当日成本科目数量 = 0 and 变量.当日计算成本科目数量 = 0
            if (varNumAmtHappenDate.compareTo(BigDecimal.ZERO) == 0 && varDrjscbKmsl.compareTo(BigDecimal.ZERO) == 0) {
                // 变量.应计利息 = 变量.应计利息余额 * -1
                intAmt = varInterestAccBal.negate();
            }
            else {
                // 调用债券应计利息共通：利息计提-计算百元面值的债券的利息
                AccurIntCalOutParameter accurIntCalOutParameter = calculationCommonService.getCalculate(acctEntityEx, rstBaseinfo,
                    listIntinfo, listCashinfo, listPayinfo, varFaceValue, acctEntity);

                // 变量.本日应计利息余额 =上述返回值 * 变量.变量.当日计算成本科目数量 * 变量.百元券面面值 /100
                tdyInterestAccBal = ArithmeticUtil.multiply(ifNull2Zero(accurIntCalOutParameter.getInterest()), varDrjscbKmsl);
                tdyInterestAccBal = ArithmeticUtil.multiply(tdyInterestAccBal, varFaceValue);
                tdyInterestAccBal = ArithmeticUtil.divide(tdyInterestAccBal, new BigDecimal(100),
                    NumberEnum.HIGH_PREC_NUM.getCode());
                tdyInterestAccBal = ArithmeticUtil.round(tdyInterestAccBal, NumberEnum.MONEY);// 调用金额共通方法设置有效小数位
                // 变量.应计利息 = 变量.本日应计利息余额 - 变量.当日计算应计利息余额
                intAmt = ArithmeticUtil.subtract(tdyInterestAccBal, varDrjsyjlxKmye);
            }

            // 场景要素管理----start
            // 分类1 <- 变量.标的信息.分类1
            // 分类2 <- 变量.标的信息.分类2
            // 分类3 <- 变量.标的信息.分类3
            // 分类4 <- 变量.标的信息.分类4
            // 分类5 <- 变量.标的信息.分类5
            // 分类6 <- 变量.标的信息.分类6
            // 备注 <- "债券借贷-融入债券利息计提"
            accountSceneMap = bookCommonLogic.accrualElementAssign(acctEntity, acctEntityEx);// (调用共通方法-交易类场景元素赋值),(其他场景元素)
            accountSceneMap.put(BookConstant.TYPE_1, rstBaseinfo.getType1());
            accountSceneMap.put(BookConstant.TYPE_2, rstBaseinfo.getType2());
            accountSceneMap.put(BookConstant.TYPE_3, rstBaseinfo.getType3());
            accountSceneMap.put(BookConstant.TYPE_4, rstBaseinfo.getType4());
            accountSceneMap.put(BookConstant.TYPE_5, rstBaseinfo.getType5());
            accountSceneMap.put(BookConstant.TYPE_6, rstBaseinfo.getType6());
            accountSceneMap.put(BookConstant.BOOK_REMARK, "债券借贷-融入债券利息计提");

            accountAmountMap = new HashMap<String, BigDecimal>();
            accountAmountMap.put(BookConstant.INTAMT, ArithmeticUtil.round(intAmt, NumberEnum.MONEY)); // 利息计提 <- 变量.应计利息

            accountCurrencyMap = new HashMap<String, Object>();
            accountCurrencyMap.put(BookConstant.INTAMT, varCcy);// 利息计提 <-变量.币种

            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);
            ad.setAccountAmountMap(accountAmountMap);
            ad.setAccountCurrencyMap(accountCurrencyMap);
            // 场景要素管理----end

            // add by weipeng on 20220321：增加辅助核算项配置 start
            ad.setSubjectMappingConfigDefault(acctEntityEx.getSubjectMappingConfig());
            // add by weipeng on 20220321：增加辅助核算项配置 end

            // 记账凭证生成
            BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, batchExecuteInfo);
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            returnResult.getValBokBookdetailBatch().addAll(resultNowNow.getValBokBookdetailBatch());

            // 3.2统计分析账务明细汇总信息赋值
            // 变量.计提利率
            BigDecimal drawYield = BigDecimal.ZERO;
            if (!CollectionUtils.isEmpty(listCashinfo)) {
                for (TargetCashCalcRule targetCashCalcRule : listCashinfo) {
                    // 报价.生效日 > 计提类账务处理结果.发生日
                    if (acctEntity.getHappenDate().compareTo(targetCashCalcRule.getEffDate()) < 0) {
                        break;
                    }
                    // 变量.计提利率 = 报价temp.生效利率
                    drawYield = targetCashCalcRule.getYield();
                }
            }

            // 当日统计分析账务明细汇总信息 -------------------- start
            bokSummaryTableNew = new BokStatDetPosiiton();

            bokSummaryTableNew.setBooksetId(acctEntity.getBooksetId()); // 账套代码
            bokSummaryTableNew.setHappenDate(acctEntity.getHappenDate()); // 发生日期
            bokSummaryTableNew.setBookDate(acctEntity.getBookDate()); // 入账日期
            bokSummaryTableNew.setBokDetailId(acctEntityEx.getBokDetailId()); // 账务明细代码
            bokSummaryTableNew.setBookSummaryOrder(new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.账务汇总表_处理序列))); // 处理序列
            bokSummaryTableNew.setBooksetDate(acctEntityEx.getBooksetDate()); // 账套日期

            // 变量.当日统计分析账务明细汇总信息.当前持仓 = 变量.当日成本科目数量
            bokSummaryTableNew.setPosAmt(varNumAmtHappenDate);
            // 变量.当日统计分析账务明细汇总信息.计提利率 = 变量.计提利率
            bokSummaryTableNew.setIntRate(drawYield);
            // 变量.当日统计分析账务明细汇总信息.日摊销收益率 = null
            bokSummaryTableNew.setDailydscYield(null);
            // 变量.当日统计分析账务明细汇总信息.当日发生应计利息 = 变量.应计利息
            bokSummaryTableNew.setTdyIntincexpAdd(intAmt);
            // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额 = 变量.昨日统计分析账务明细汇总信息.当日累计应计利息余额 + 变量.应计利息
            BigDecimal tdyintincexpNow = BigDecimal.ZERO;
            if (bokSummaryTableYestdy != null) {
                tdyintincexpNow = ArithmeticUtil.doAdd(bokSummaryTableYestdy.getTdyIntincexp(), intAmt);
            }
            else {
                tdyintincexpNow = intAmt;
            }
            bokSummaryTableNew.setTdyIntincexp(tdyintincexpNow);
            // 变量.当日统计分析账务明细汇总信息.当日份额 = 变量.当日计算成本科目数量
            bokSummaryTableNew.setTdyPosition(varDrjscbKmsl);
            // 变量.当日统计分析账务明细汇总信息.投资目的 = 计提类账务处理结果.投资目的
            bokSummaryTableNew.setInvAim(acctEntity.getInvAim());
            // 变量.当日统计分析账务明细汇总信息.当日成本 = 变量.当日成本科目余额
            bokSummaryTableNew.setTdyCostAmt(varOAmtHappenDate);
            // 变量.当日统计分析账务明细汇总信息.金融产品类型 = 变量.标的信息.金融产品类型
            bokSummaryTableNew.setFinprodType(rstBaseinfo.getFinprodType());
            // 变量.当日统计分析账务明细汇总信息.起息日 = 变量.标的信息.起息日
            bokSummaryTableNew.setVdate(rstBaseinfo.getVdate());
            // 变量.当日统计分析账务明细汇总信息.到期日 = 变量.标的信息.实际到期日
            bokSummaryTableNew.setMdate(rstBaseinfo.getMdate());

            // 行权计划查询-----start
            // 行权计划
            FinExercisePlan finExercisePlan = null;
            // 变量.行权日百元券面
            // BigDecimal varPlanFaceValue = BigDecimal.ZERO;
            // if（变量.标的信息.是否含权 = "是"）
            if (rstBaseinfo != null && DictCodeList.是否_是.getValue().equals(rstBaseinfo.getIsRight())) {
                String exerciseType = null;
                if (DictCodeList.含权债摊销日_最近赎回行权日.getValue().equals(rstBaseinfo.getExeDate())) {
                    exerciseType = DictCodeList.行权方式_赎回.getValue();
                }
                else if (DictCodeList.含权债摊销日_最近回售行权日.getValue().equals(rstBaseinfo.getExeDate())) {
                    exerciseType = DictCodeList.行权方式_回售.getValue();
                }
                // 调用标的类共通下一行权计划查询
                finExercisePlan = finProductCommonService.queryFinExercisePlan(acctEntity.getBusiId(),
                    DictCodeListExt1.金融产品类型_债券.getValue(), acctEntity.getHappenDate(), exerciseType);
                /*
                 * if (finExercisePlan != null) { // 百元券面查询 varPlanFaceValue = finProductCommonService.getFaceValue(acctEntity.getBusiId(),
                 * finExercisePlan.getExerciseDate()); }
                 */
            }
            // 行权计划查询-----end

            // 定义 变量.计算类型
            String calType = "";
            if (!CollectionUtils.isEmpty(listCashinfo)) {
                // 变量.计算类型 = 变量.报价列表.get(0).计算类型
                calType = listCashinfo.get(0).getCalcType();
            }
            Date happenDate = (Date)param.get(BookConstant.HAPPEN_DATE);
            BigDecimal[] remainResult = calculationCommonService.calSecRemTermAndDuration(rstBaseinfo, listIntinfo, calType,
                finExercisePlan == null ? null : finExercisePlan.getExerciseDate(), happenDate);

            // 变量.当日统计分析账务明细汇总信息.剩余期限 = 返回.剩余期限
            bokSummaryTableNew.setEndDays1(remainResult[0]);
            // 变量.当日统计分析账务明细汇总信息.剩余存续期 = 返回.剩余存续期限
            bokSummaryTableNew.setEndDays2(remainResult[1]);
            // 变量.当日统计分析账务明细汇总信息.剩余期限用成本 = 变量.当日成本科目余额
            bokSummaryTableNew.setEndDaysCostAmt(varOAmtHappenDate);
            // 变量.当日统计分析账务明细汇总信息.业务明细代码 = 计提类账务处理结果.业务明细代码
            bokSummaryTableNew.setBusiId(acctEntity.getBusiId());

            bokSummaryTableNew.setCcy(rstBaseinfo.getCcy());


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
            // 当日统计分析账务明细汇总信息 -------------------- end
        }
        return returnResult;
    }

    /**
     * 凭证最新账务查询
     * 
     * @param acctEntity
     * @param tableId 场景代码
     * @return
     * @see
     * @since 1.0
     */
    private List<BookLatestInfoDto> myGetBookLatestInfo(DrawBaseInfoSelectComResult acctEntity, String tableId) {
        // 定义基础科目性质list
        List<String> baseSubjectNatureList = new ArrayList<String>();
        baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.交易性金融负债_成本.getValue());
        baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.交易性金融负债_应计利息.getValue());
        baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.交易性金融负债_公允价值变动.getValue());
        // 变量.买入账务明细列表
        List<BookLatestInfoDto> bookDetialListBuy = accountingCommonService.getBookLatestInfo(acctEntity.getBooksetId(),
            acctEntity.getBookDate(), acctEntity.getBokDetailId(), baseSubjectNatureList,// 基础科目性质=金融负债成本，应计利息，公允价值变动
            tableId,// 场景代码
            null,// 交易编号
            null,// 发生日
            acctEntity.getHappenDate(),// 在途日期 = 计提类账务处理结果.发生日
            null,// 发生周期开始日
            null);// 发生周期结束日
        return bookDetialListBuy;
    }

    /**
     * 调用公用方法：【科目余额查询 - 期待方向】，获取科目余额信息中的科目余额数据
     * 
     * @param expBalanceDirect 期待余额方向
     * @param baseSubjectNature 基础科目性质
     * @param taskPri 任务优先级
     * @return subjectBalance 科目余额信息
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalance(DrawBaseInfoSelectComResult acctEntity, String expBalanceDirect,
                                             String baseSubjectNature, Long taskPri) {
        // 获取从reader中拿到的值
        String booksetIdNow = acctEntity.getBooksetId();// 账套代码
        String bokDetailIdNow = acctEntity.getBokDetailId();// 账务明细代码
        Date happenDateNow = acctEntity.getHappenDate();// 发生日期
        Date bookDateNow = acctEntity.getBookDate();// 入账日期

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
        searchDto.setBooksetId(booksetIdNow);
        searchDto.setBokDetailId(bokDetailIdNow);
        searchDto.setHappenDate(happenDateNow);
        searchDto.setBookDate(bookDateNow);
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
     * 标的信息查询
     * 
     * @param acctEntityEx 计提基础信息
     * @param searchBaseinfo 标的信息条件
     * @param searchIntinfo 付息计划条件
     * @param searchRedinfo 还本计划条件
     * @param searchCashinfo 报价计划条件
     * @param searchPayinfo 现金流支付规则条件
     * @return Map<TargetSearchDto, Object> 标的信息
     * @see
     * @since 1.0
     */
    private Map<TargetSearchDto, Object> myGetTargetInfo(DrawBaseInfoSelectComDTO acctEntityEx, TargetSearchDto searchBaseinfo,
                                                         TargetSearchDto searchIntinfo, TargetSearchDto searchRedinfo,
                                                         TargetSearchDto searchCashinfo, TargetSearchDto searchPayinfo) {
        // 查询类型【必输项】
        List<TargetSearchDto> queryCriteria = new ArrayList<TargetSearchDto>();
        // 数据代码 RST_FINPRODUCT
        searchBaseinfo.setQueryType(ValDictCodeList.标的_信息.getValue());
        queryCriteria.add(searchBaseinfo);
        // 第2条记录 //还本计划
        // 数据代码 RST_CASH_PLAN
        // 现金流类型 本金
        searchRedinfo.setQueryType(ValDictCodeList.标的_现金流计划.getValue());
        searchRedinfo.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());// TODO用哪个字段，是一级吗 ？
        queryCriteria.add(searchRedinfo);

        // 第3条记录 //付息计划
        // 数据代码 RST_CASH_PLAN
        // 现金流类型 利息
        searchIntinfo.setQueryType(ValDictCodeList.标的_现金流计划.getValue());
        searchIntinfo.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        queryCriteria.add(searchIntinfo);

        // 第4条记录 //报价计划
        // 数据代码 RST_CASH_CALCRULE
        // 现金流类型=利息
        searchCashinfo.setQueryType(ValDictCodeList.标的_现金流计算规则.getValue());
        // searchCashinfo.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        // 针对估值一体化：计提式批处理，获取现金流计算规则，不支持投管【计提式费用】，故只取利息。
        searchCashinfo.setCashType2(DictCodeListExt1.现金流二级类型_利息.getValue());
        queryCriteria.add(searchCashinfo);

        // 第5条记录 //现金流支付规则
        // 数据代码 RST_CASH_PAYRULE
        // 现金流类型 利息
        searchPayinfo.setQueryType(ValDictCodeList.标的_现金流支付规则.getValue());
        searchPayinfo.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        queryCriteria.add(searchPayinfo);

        return finProductCommonService.getTargetInfo(acctEntityEx.getBusiId(), queryCriteria);
    }

    private BigDecimal ifNull2Zero(BigDecimal b) {
        return b == null ? BigDecimal.ZERO : b;
    }
}