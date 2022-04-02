/*
 * 文件名：SecAbsCalBookService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V2.0
 * 修改人：chengzhong
 * 修改时间：2018年6月4日
 * 修改内容：新建
 * 系统名称：估值核算系统V2.0
 */

package com.joyintech.tams.valuation.batch.secabscalbook.service;

import com.joyintech.calculation.CalculatorFactory;
import com.joyintech.calculation.bonds.SecIntSchedule;
import com.joyintech.calculation.bonds.base.param.SecCalYearDayCountOutputParameter;
import com.joyintech.calculation.bonds.base.param.SecCalYearInputParameter;
import com.joyintech.calculation.bonds.constant.*;
import com.joyintech.calculation.bonds.interest.AccurIntForDisCalculator;
import com.joyintech.calculation.bonds.interest.BasisIntCalculator;
import com.joyintech.calculation.bonds.interest.param.AccurIntForDisInputParameter;
import com.joyintech.calculation.bonds.interest.param.AccurInterestOutputParameter;
import com.joyintech.calculation.bonds.interest.param.BasisIntInputParameter;
import com.joyintech.calculation.bonds.interest.param.BasisIntOutputParameter;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.shell.SecDscYieldCalculator;
import com.joyintech.calculation.shell.param.AccurIntCalOutParameter;
import com.joyintech.calculation.shell.param.SecDscYieldInParameter;
import com.joyintech.calculation.shell.param.SecDscYieldOutParameter;
import com.joyintech.calculation.shell.param.SecInfoModel;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.calculation.util.BasicUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.BokFinProductValuationParaMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.valuation.batch.common.accounting.entity.BookLatestInfoDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accounting.service.LossCommonBatchService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogicNew;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.AccountDriver;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResult;
import com.joyintech.tams.valuation.batch.common.accountingmethod.entity.BookGenerateResultWithBokSummary;
import com.joyintech.tams.valuation.batch.common.base.entity.extcommon.ValBokBookdetailBatch;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.batch.secabscalbook.dto.SecSubjectBalanceDto;
import com.joyintech.tams.valuation.batch.secabscalbook.processor.SecAbsCalBookProcessor;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.*;
import com.joyintech.tams.valuation.common.batch.losscommon.service.LossCommonService;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.*;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.service.baseinfo.sec.SecBaseInfoRecService;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

/**
 * 债券/ABS计提账务处理
 * 
 * @author chengzhong
 * @version 1.0 2018年6月4日
 * @see SecAbsCalBookProcessor
 * @since 1.0
 */
@Service
@Scope("prototype")
public class SecAbsCalBookNewService {

    /**
     * DBUtil
     */
    @Autowired
    private DBUtil dBUtil;

    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogicNew bookLogicNew;

    /**
     * 记账引擎
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

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

    @Autowired
    private CalculationCommonService calculationCommonService;
    
    @Autowired
    private BokFinProductValuationParaMapper bokFinProductValuationParaKeyMapper;
    
    @Autowired
    private LossCommonService lossCommonService;
    
    @Autowired
    private LossCommonBatchService lossCommonBatchService;
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SecBaseInfoRecService secBaseInfoRecService;

    /**
     * process 数据处理
     * 
     * @param param 参数
     * @return BookGenerateResultWithBokSummary
     * @see
     * @since 1.0
     */
    public BookGenerateResultWithBokSummary execute(Map<String, Object> param) {
        // 获取reader返回值
        DrawBaseInfoSelectComResult acctEntity = (DrawBaseInfoSelectComResult)param.get(
            GlobalConstant.READER_RESULT);

        Date happenDate = (Date)param.get(BookConstant.HAPPEN_DATE);
        // 若reader传递的参数为空，不进行后续处理
        if (acctEntity == null) {
            return null;
        }
        // reader传递的参数的明细信息
        List<DrawBaseInfoSelectComDTO> acctEntityList = acctEntity.getListDrawBaseInfoSelectComDTO();
        // 没有明细
        if (acctEntityList == null || acctEntityList.size() == 0) {
            return null;
        }

        // 定义返回值
        BookGenerateResultWithBokSummary returnResult = new BookGenerateResultWithBokSummary();
        // 账务明细若为空赋初始值
        if (returnResult.getValBokBookdetailBatch() == null) {
            returnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }
        
        /**ADD BY CAOGUO ON 20201016 减值相关修改 START **/        
        if (returnResult.getBokStatLossPosiitonList() == null) {
            returnResult.setBokStatLossPosiitonList(new ArrayList<BokStatLossPosiiton>());
        }
        /**ADD BY CAOGUO ON 20201016 END**/
        
        for (DrawBaseInfoSelectComDTO acctEntityEx : acctEntityList) {

            // 1.数据准备
            // 1.1 变量定义
            // 变量.百元券面面值
            BigDecimal varFaceValue = BigDecimal.valueOf(100);

            // 变量.当日成本科目余额
            BigDecimal varOAmtHappenDate = acctEntityEx.getoAmtHappenDate();

            // 变量.当日成本科目数量
            BigDecimal varNumAmtHappenDate = acctEntityEx.getNumAmtHappenDate();

            // 变量.应计利息科目余额
            BigDecimal varInterestAccBal = BigDecimal.ZERO;

            // 变量.应计利息科目余额本位币
            BigDecimal varInterestAccBalBCCY = BigDecimal.ZERO;

            // 变量.公允价值变动科目余额
            // BigDecimal varFairValueAccBal = BigDecimal.ZERO;

            // 变量.溢折价科目余额
            BigDecimal varSpilledPriceAccBal = BigDecimal.ZERO;

            // 变量.币种
            String varCcy = acctEntityEx.getoCcy();

            // 变量.上次结息日
            // Date varOInterestDay = null;
            // 2.4.标的类金融产品信息
            // 查询条件
            List<TargetSearchDto> queryCriteria = new ArrayList<TargetSearchDto>();
            // 标的信息条件
            TargetSearchDto searchBaseinfo = new TargetSearchDto();
            searchBaseinfo.setQueryType(ValDictCodeList.标的_信息.getValue());
            queryCriteria.add(searchBaseinfo);

            // 还本计划条件
            TargetSearchDto searchRedinfo = new TargetSearchDto();
            searchRedinfo.setQueryType(ValDictCodeList.标的_现金流计划.getValue());
            searchRedinfo.setCashType1(DictCodeListExt1.现金流一级类型_本金.getValue());
            queryCriteria.add(searchRedinfo);

            // 付息计划条件
            TargetSearchDto searchIntinfo = new TargetSearchDto();
            searchIntinfo.setQueryType(ValDictCodeList.标的_现金流计划.getValue());
            searchIntinfo.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
            queryCriteria.add(searchIntinfo);

            // 报价计划条件
            TargetSearchDto searchCashinfo = new TargetSearchDto();
            searchCashinfo.setQueryType(ValDictCodeList.标的_现金流计算规则.getValue());
            // searchCashinfo.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
            // 针对估值一体化：计提式批处理，获取现金流计算规则，不支持投管【计提式费用】，故只取利息。
            searchCashinfo.setCashType2(DictCodeListExt1.现金流二级类型_利息.getValue());
            queryCriteria.add(searchCashinfo);

            // 现金流支付规则条件
            TargetSearchDto searchPayinfo = new TargetSearchDto();
            searchPayinfo.setQueryType(ValDictCodeList.标的_现金流支付规则.getValue());
            searchPayinfo.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
            queryCriteria.add(searchPayinfo);

            Map<TargetSearchDto, Object> targetSearchMap = finProductCommonService.getTargetInfo(
                acctEntityEx.getBusiId(), queryCriteria);

            // 变量.标的信息 = 上述共通返回标的信息
            TargetFinProduct rstBaseinfo = (TargetFinProduct)targetSearchMap.get(searchBaseinfo);

            // 变量.付息列表 = 上述共通返回的计划列表
            @SuppressWarnings("unchecked")
            List<TargetCashPlan> listIntinfo = targetSearchMap.get(
                searchIntinfo) == null ? null : (List<TargetCashPlan>)targetSearchMap.get(
                searchIntinfo);

            // 变量.还本列表 = 上述共通返回还本列表
            // @SuppressWarnings("unchecked")
            // List<TargetCashPlan> listRedinfo = targetSearchMap.get(searchRedinfo) == null ? null
            // : (List<TargetCashPlan>)targetSearchMap.get(searchRedinfo);

            // 变量.报价列表 = 上述共通返回报价列表
            @SuppressWarnings("unchecked")
            List<TargetCashCalcRule> listCashinfo = targetSearchMap.get(
                searchCashinfo) == null ? null : (List<TargetCashCalcRule>)targetSearchMap.get(
                searchCashinfo);

            // 变量.支付规则 = 上述共通返回支付规则列表
            @SuppressWarnings("unchecked")
            List<TargetCashPayRule> listPayinfo = targetSearchMap.get(
                searchPayinfo) == null ? null : (List<TargetCashPayRule>)targetSearchMap.get(
                searchPayinfo);

            // 是否使用摊余成本法计算
            boolean isAcFlag =acctEntityEx.getIsAcMethod();

            // 1.2 百元券面处理
            varFaceValue = finProductCommonService.getFaceValue(acctEntityEx.getBusiId(),
                acctEntity.getHappenDate());

            // 1.3 汇总信息查询
            // 变量.昨日统计分析账务明细汇总信息 = 调用共通方法[账务类共通方法设计 - 统计分析账务明细汇总查询]
            BokStatDetPosiiton bokSummaryTableYestdy = accountingCommonService.getCountAnalysis(
                DateUtil.addDay(acctEntity.getHappenDate(), -1),
                DateUtil.addDay(acctEntity.getHappenDate(), -1), acctEntity.getBookDate(),
                acctEntity.getBooksetId(), acctEntityEx.getBokDetailId(), null, null);

            // 变量.买入交易列表
            int count = 0;
            // 买入场景代码
            String buyTableId = null;
            List<String> natureList = new ArrayList<String>();
            if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(
                acctEntity.getInvAim())) {
                buyTableId = DictCodeListTableId.会计场景代码_公允价值损益类债券_买入.getValue();
                natureList.add(DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue());
            }
            else if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(acctEntity.getInvAim())) {
                buyTableId = DictCodeListTableId.会计场景代码_摊余成本类债券_买入.getValue();
                natureList.add(DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue());
            }
            else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(
                acctEntity.getInvAim())) {
                buyTableId = DictCodeListTableId.会计场景代码_综合收益类债券_买入.getValue();
                natureList.add(DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue());
            }
            // 变量.买入交易列表 = 调用共通方法[账务类共通方法设计 - 凭证最新账务查询]
            if (buyTableId != null) {
                count = accountingCommonService.getBookLatestInfoOnway(acctEntity.getBooksetId(),
                    acctEntity.getBookDate(), acctEntityEx.getBokDetailId(), natureList,
                    buyTableId, acctEntity.getHappenDate());
            }

            // 科目余额查询参数
            SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
            searchDao.setBooksetId(acctEntity.getBooksetId());
            searchDao.setBokDetailId(acctEntity.getBokDetailId());
            searchDao.setHappenDate(acctEntity.getHappenDate());
            searchDao.setBookDate(acctEntity.getBookDate());
            searchDao.setTaskPri(acctEntityEx.getTaskPri());
            searchDao.setoCcy(acctEntityEx.getoCcy());
            // 2.3.溢折价类科目余额
            List<SubjectBalance> listSubjectBalanceEX = new ArrayList<SubjectBalance>();
            if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(acctEntityEx.getInvAim())) {
                searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_利息调整.getValue())));
                listSubjectBalanceEX = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            }
            else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(
                acctEntityEx.getInvAim())) {
                searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue())));
                listSubjectBalanceEX = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            }
            else if(isAcFlag && DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(
                acctEntityEx.getInvAim())) {
                searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_利息调整.getValue())));
                listSubjectBalanceEX = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            }
            if (listSubjectBalanceEX != null && listSubjectBalanceEX.size() > 0) {
                // 变量.溢折价科目余额 = 科目余额
                varSpilledPriceAccBal = listSubjectBalanceEX.get(0).getBalance();
            }
            /**ADD BY CAOGUO ON 20201019 减值相关修改 START **/
            // 变量.减值准备科目余额
            BigDecimal varJzzbYe = BigDecimal.ZERO;
            // 2.3.1.减值准备科目余额查询
            List<SubjectBalance> listSubjectBalanceJZ = new ArrayList<SubjectBalance>();
            if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(acctEntityEx.getInvAim())) {
                searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_减值准备.getValue())));
                listSubjectBalanceJZ = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            }
            else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(
                acctEntityEx.getInvAim())) {
                searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.其他综合收益_减值准备.getValue())));
                listSubjectBalanceJZ = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            }
            if (listSubjectBalanceJZ != null && listSubjectBalanceJZ.size() > 0) {
                // 变量.溢折价科目余额 = 科目余额
                varJzzbYe = listSubjectBalanceJZ.get(0).getBalance();
            }
            /**ADD BY CAOGUO ON 20201019 END**/

            // 2.6.应计利息余额
            List<SubjectBalance> listSubjectBalance = new ArrayList<SubjectBalance>();
            if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(acctEntityEx.getInvAim())) {
                searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue())));
                listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            }
            else if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(
                acctEntityEx.getInvAim())) {
                searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue())));
                listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            }
            else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(
                acctEntityEx.getInvAim())) {
                searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                    Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue())));
                listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                    searchDao, DictCodeList.余额方向_借方.getValue());
            }

            if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
                // 变量.应计利息余额 = 科目余额
                varInterestAccBal = listSubjectBalance.get(0).getBalance();
                varInterestAccBalBCCY = listSubjectBalance.get(0).getbAmt();
            }

            // 2.7 T+1相关发生额统计
            // 买入场景
            String tableId = null;
            List<String> baseSubjectNatureList = new ArrayList<String>();
            if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(
                acctEntity.getInvAim())) {
                tableId = DictCodeListTableId.会计场景代码_公允价值损益类债券_买入.getValue();
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue());
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue());
                if(isAcFlag) {
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.公允价值损益类_利息调整.getValue());
                }
            }
            else if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(acctEntity.getInvAim())) {
                tableId = DictCodeListTableId.会计场景代码_摊余成本类债券_买入.getValue();
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue());
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.摊余成本类_利息调整.getValue());
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue());
                /**ADD BY CAOGUO ON 20201019 减值相关修改 START **/
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.摊余成本类_减值准备.getValue());
                /**ADD BY CAOGUO ON 20201019 END**/
            }
            else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(
                acctEntity.getInvAim())) {
                tableId = DictCodeListTableId.会计场景代码_综合收益类债券_买入.getValue();
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue());
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue());
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue());
                /**ADD BY CAOGUO ON 20201019 减值相关修改 START **/
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.其他综合收益_减值准备.getValue());
                /**ADD BY CAOGUO ON 20201019 END**/
            }
            // 调用账务共通设计->【凭证最新账务查询】
            List<BookLatestInfoDto> listBookLatestInfoDto = accountingCommonService.getBookLatestInfo(
                acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntity.getBokDetailId(),
                baseSubjectNatureList, tableId, null, null, acctEntity.getHappenDate(), null,
                null);

            // 变量.当日计算成本科目数量 = 变量.当日成本科目数量
            BigDecimal varDrjscbKmsl = varNumAmtHappenDate;
            // 变量.当日计算成本科目余额 = 变量.当日成本科目余额
            BigDecimal varDrjscbKmye = varOAmtHappenDate;
            // 变量.当日计算摊销溢折价科目余额 = 变量.溢折价科目余额
            BigDecimal varDrjstxzyjKmye = varSpilledPriceAccBal;
            // 变量.当日计算应计利息余额 = 变量.应计利息余额
            BigDecimal varDrjsyjlxKmye = varInterestAccBal;
            /**ADD BY CAOGUO ON 20201019 减值相关修改 START **/
            // 变量.当日计算减值准备科目余额 = 变量.减值准备科目余额
            BigDecimal varDrjJzzbYe = varJzzbYe;
            /**ADD BY CAOGUO ON 20201019 END**/
            // LOOP 变量.买入账务明细列表 -> 变量.账务明细
            if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                for (BookLatestInfoDto var : listBookLatestInfoDto) {
                    // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue().equals(
                        var.getBaseSubjectNature())
                        || DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue().equals(
                            var.getBaseSubjectNature())
                        || DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue().equals(
                            var.getBaseSubjectNature())) {
                        // 变量.当日计算成本科目数量 -= 变量.账务明细.发生数量
                        varDrjscbKmsl = ArithmeticUtil.subtract(varDrjscbKmsl,
                            var.getHappenNumber());
                        // 变量.当日计算成本科目余额 -= 变量.账务明细.原币发生额
                        varDrjscbKmye = ArithmeticUtil.subtract(varDrjscbKmye,
                            var.getoHappenAmt());
                    }
                    // if(变量.账务明细.基础科目性质 = XX利息调整的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.摊余成本类_利息调整.getValue().equals(
                        var.getBaseSubjectNature())
                        || DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue().equals(
                            var.getBaseSubjectNature())
                        || DictCodeBaseSubjectNatrueNew.公允价值损益类_利息调整.getValue().equals(
                            var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.当日计算摊销溢折价科目余额 -= 变量.账务明细.原币发生额
                            varDrjstxzyjKmye = ArithmeticUtil.subtract(varDrjstxzyjKmye,
                                var.getoHappenAmt());
                        }
                        else {
                            // 变量.当日计算摊销溢折价科目余额 += 变量.账务明细.原币发生额
                            varDrjstxzyjKmye = ArithmeticUtil.doAdd(varDrjstxzyjKmye,
                                var.getoHappenAmt());
                        }
                    }
                    // if(变量.账务明细.基础科目性质 = XX应计利息的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue().equals(
                        var.getBaseSubjectNature())
                        || DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue().equals(
                            var.getBaseSubjectNature())
                        || DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue().equals(
                            var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.当日计算应计利息余额 -= 变量.账务明细.原币发生额
                            varDrjsyjlxKmye = ArithmeticUtil.subtract(varDrjsyjlxKmye,
                                var.getoHappenAmt());
                        }
                        else {
                            // 变量.当日计算应计利息余额 += 变量.账务明细.原币发生额
                            varDrjsyjlxKmye = ArithmeticUtil.doAdd(varDrjsyjlxKmye,
                                var.getoHappenAmt());
                        }
                    }
                    /**ADD BY CAOGUO ON 20201019 减值相关修改 START **/
                    // if(变量.账务明细.基础科目性质 = XX减值准备的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.摊余成本类_减值准备.getValue().equals(
                        var.getBaseSubjectNature())
                        || DictCodeBaseSubjectNatrueNew.其他综合收益_减值准备.getValue().equals(
                            var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.当日计算应计利息余额 -= 变量.账务明细.原币发生额
                            varDrjJzzbYe = ArithmeticUtil.subtract(varDrjJzzbYe,
                                var.getoHappenAmt());
                        }
                        else {
                            // 变量.当日计算应计利息余额 += 变量.账务明细.原币发生额
                            varDrjJzzbYe = ArithmeticUtil.doAdd(varDrjJzzbYe, var.getoHappenAmt());
                        }
                    }
                    /**ADD BY CAOGUO ON 20201019 END**/
                }
            }
            // 卖出场景
            if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(
                acctEntity.getInvAim())) {
                tableId = DictCodeListTableId.会计场景代码_公允价值损益类债券_卖出.getValue();
            }
            else if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(acctEntity.getInvAim())) {
                tableId = DictCodeListTableId.会计场景代码_摊余成本类债券_卖出.getValue();
            }
            else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(
                acctEntity.getInvAim())) {
                tableId = DictCodeListTableId.会计场景代码_综合收益类债券_卖出.getValue();
            }
            // 调用账务共通设计->【凭证最新账务查询】
            listBookLatestInfoDto = accountingCommonService.getBookLatestInfo(
                acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntity.getBokDetailId(),
                baseSubjectNatureList, tableId, null, null, acctEntity.getHappenDate(), null,
                null);
            // LOOP 变量.卖出账务明细列表 -> 变量.账务明细
            if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                for (BookLatestInfoDto var : listBookLatestInfoDto) {
                    // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue().equals(
                        var.getBaseSubjectNature())
                        || DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue().equals(
                            var.getBaseSubjectNature())
                        || DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue().equals(
                            var.getBaseSubjectNature())) {
                        // 变量.当日计算成本科目数量 += 变量.账务明细.发生数量
                        varDrjscbKmsl = ArithmeticUtil.doAdd(varDrjscbKmsl, var.getHappenNumber());
                        // 变量.当日计算成本科目余额 += 变量.账务明细.原币发生额
                        varDrjscbKmye = ArithmeticUtil.doAdd(varDrjscbKmye, var.getoHappenAmt());
                    }
                    // if(变量.账务明细.基础科目性质 = XX利息调整的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.摊余成本类_利息调整.getValue().equals(
                        var.getBaseSubjectNature())
                        || DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue().equals(
                            var.getBaseSubjectNature())
                        || DictCodeBaseSubjectNatrueNew.公允价值损益类_利息调整.getValue().equals(
                            var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.当日计算摊销溢折价科目余额 -= 变量.账务明细.原币发生额
                            varDrjstxzyjKmye = ArithmeticUtil.subtract(varDrjstxzyjKmye,
                                var.getoHappenAmt());
                        }
                        else {
                            // 变量.当日计算摊销溢折价科目余额 += 变量.账务明细.原币发生额
                            varDrjstxzyjKmye = ArithmeticUtil.doAdd(varDrjstxzyjKmye,
                                var.getoHappenAmt());
                        }
                    }
                    // if(变量.账务明细.基础科目性质 = XX应计利息的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue().equals(
                        var.getBaseSubjectNature())
                        || DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue().equals(
                            var.getBaseSubjectNature())
                        || DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue().equals(
                            var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.当日计算应计利息余额 -= 变量.账务明细.原币发生额
                            varDrjsyjlxKmye = ArithmeticUtil.subtract(varDrjsyjlxKmye,
                                var.getoHappenAmt());
                        }
                        else {
                            // 变量.当日计算应计利息余额 += 变量.账务明细.原币发生额
                            varDrjsyjlxKmye = ArithmeticUtil.doAdd(varDrjsyjlxKmye,
                                var.getoHappenAmt());
                        }
                    }
                    /**ADD BY CAOGUO ON 20201019 减值相关修改 START **/
                    // if(变量.账务明细.基础科目性质 = XX减值准备的账务明细 )
                    if (DictCodeBaseSubjectNatrueNew.摊余成本类_减值准备.getValue().equals(
                        var.getBaseSubjectNature())
                        || DictCodeBaseSubjectNatrueNew.其他综合收益_减值准备.getValue().equals(
                            var.getBaseSubjectNature())) {
                        // 变量.账务明细.借贷方向为借时
                        if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                            // 变量.当日计算应计利息余额 -= 变量.账务明细.原币发生额
                            varDrjJzzbYe = ArithmeticUtil.subtract(varDrjJzzbYe,
                                var.getoHappenAmt());
                        }
                        else {
                            // 变量.当日计算应计利息余额 += 变量.账务明细.原币发生额
                            varDrjJzzbYe = ArithmeticUtil.doAdd(varDrjJzzbYe, var.getoHappenAmt());
                        }
                    }
                    /**ADD BY CAOGUO ON 20201019 END**/
                }
            }
            // // 次日T+1买入场景
            // baseSubjectNatureList = new ArrayList<String>();
            // if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(
            // acctEntity.getInvAim())) {
            // tableId = DictCodeListTableId.会计场景代码_公允价值损益类债券_买入.getValue();
            // baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue());
            // }
            // else if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(acctEntity.getInvAim())) {
            // tableId = DictCodeListTableId.会计场景代码_摊余成本类债券_买入.getValue();
            // baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue());
            // }
            // else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(
            // acctEntity.getInvAim())) {
            // tableId = DictCodeListTableId.会计场景代码_综合收益类债券_买入.getValue();
            // baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue());
            // }
            // // 调用账务共通设计->【凭证最新账务查询】
            // listBookLatestInfoDto = accountingCommonService.getBookLatestInfo(
            // acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntity.getBokDetailId(),
            // baseSubjectNatureList, tableId, null, null,
            // DateUtils.addDays(acctEntity.getHappenDate(), 1), null,
            // acctEntity.getHappenDate());
            //
            // // 变量.次日计算成本科目数量 = 变量.当日成本科目数量
            // BigDecimal varCrjscbKmsl = varNumAmtHappenDate;
            // // 变量.次日计算成本科目余额 = 变量.当日成本科目余额
            // BigDecimal varCrjscbKmye = varOAmtHappenDate;
            // if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
            // for (BookLatestInfoDto var : listBookLatestInfoDto) {
            // // 变量.次日计算成本科目数量 -= 变量.成本账务明细.发生数量
            // varCrjscbKmsl = ArithmeticUtil.subtract(varCrjscbKmsl, var.getHappenNumber());
            // // 变量.次日计算成本科目余额 -= 变量.成本账务明细.原币发生额
            // varCrjscbKmye = ArithmeticUtil.subtract(varCrjscbKmye, var.getoHappenAmt());
            // }
            // }
            // // 次日T+1卖出场景
            // if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(
            // acctEntity.getInvAim())) {
            // tableId = DictCodeListTableId.会计场景代码_公允价值损益类债券_卖出.getValue();
            // }
            // else if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(acctEntity.getInvAim())) {
            // tableId = DictCodeListTableId.会计场景代码_摊余成本类债券_卖出.getValue();
            // }
            // else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(
            // acctEntity.getInvAim())) {
            // tableId = DictCodeListTableId.会计场景代码_综合收益类债券_卖出.getValue();
            // }
            // // 调用账务共通设计->【凭证最新账务查询】
            // listBookLatestInfoDto = accountingCommonService.getBookLatestInfo(
            // acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntity.getBokDetailId(),
            // baseSubjectNatureList, tableId, null, null,
            // DateUtils.addDays(acctEntity.getHappenDate(), 1), null,
            // acctEntity.getHappenDate());
            // if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
            // for (BookLatestInfoDto var : listBookLatestInfoDto) {
            // // 变量.次日计算成本科目数量 += 变量.成本账务明细.发生数量
            // varCrjscbKmsl = ArithmeticUtil.doAdd(varCrjscbKmsl, var.getHappenNumber());
            // // 变量.次日计算成本科目余额 += 变量.成本账务明细.原币发生额
            // varCrjscbKmye = ArithmeticUtil.doAdd(varCrjscbKmye, var.getoHappenAmt());
            // }
            // }

            // 2.8.行权计划查询
            // 行权计划
            FinExercisePlan finExercisePlan = null;
            // 变量.行权日百元券面
            BigDecimal varPlanFaceValue = BigDecimal.ZERO;
            // if（变量.标的信息.是否含权 = "是"）
            if (rstBaseinfo != null
                && DictCodeList.是否_是.getValue().equals(rstBaseinfo.getIsRight())) {
                
                /* 判断债券日终计算摊销时，优先级为账套加资产维度>资产维度>账套维度(暂时不存在 )   add wangwei1  2019/03/02 */
                BokFinProductValuationParaKey bokFinProductValuationParaKey=new BokFinProductValuationParaKey();
                bokFinProductValuationParaKey.setBoksetId(acctEntity.getBooksetId());
                bokFinProductValuationParaKey.setFinprodId(rstBaseinfo.getFinprodId());
                bokFinProductValuationParaKey.setBranch(InterfCommonConstant.分支序号_默认);
                BokFinProductValuationPara bokFinProductValuationPara=bokFinProductValuationParaKeyMapper.selectByPrimaryKey(bokFinProductValuationParaKey);
                
                //2.查询结果不为空，重新赋值含权债摊销日
                if(bokFinProductValuationPara !=null){
                	rstBaseinfo.setExeDate(bokFinProductValuationPara.getExeDateType());
                }
                /* 判断债券日终计算摊销时，优先级为账套加资产维度>资产维度>账套维度(暂时不存在 )   end wangwei1  2019/03/02 */

                String exerciseType = null;
                if (DictCodeList.含权债摊销日_最近赎回行权日.getValue().equals(rstBaseinfo.getExeDate())) {
                    exerciseType = DictCodeList.行权方式_赎回.getValue();
                }
                else if (DictCodeList.含权债摊销日_最近回售行权日.getValue().equals(rstBaseinfo.getExeDate())) {
                    exerciseType = DictCodeList.行权方式_回售.getValue();
                }
                // 调用标的类共通下一行权计划查询
                finExercisePlan = finProductCommonService.queryFinExercisePlan(
                    acctEntity.getBusiId(), DictCodeListExt1.金融产品类型_债券.getValue(),
                    acctEntity.getHappenDate(), exerciseType);
                if (finExercisePlan != null) {
                    // 百元券面查询
                    varPlanFaceValue = finProductCommonService.getFaceValue(acctEntity.getBusiId(),
                        finExercisePlan.getExerciseDate());
                }
            }
            /**ADD BY WUJIE ON 20191216 START**/
            //2.9 债券实际成本数量
            //变量.账目科目数量 
            BigDecimal  账目科目数量 = accountingCommonService.findBondActualCostNum(acctEntity.getBooksetId(), 
            		acctEntity.getBookDate(), acctEntity.getHappenDate(),
            		acctEntity.getBokDetailId(), rstBaseinfo.getTradeMarket(), 
            		acctEntity.getInvAim(), acctEntityEx.getTaskPri());
            账目科目数量 = 账目科目数量==null?BigDecimal.ZERO:账目科目数量;
            /**ADD BY WUJIE ON 20191216 END**/
            // 3.处理交易
            // 3.1 利息计提
            // 标的信息.到期日 <= 计提类账务处理结果.发生日 or 标的信息.起息日 > 计提类账务处理结果.发生日
            if (rstBaseinfo == null
                || (rstBaseinfo.getMdate() != null
                    && rstBaseinfo.getMdate().compareTo(acctEntity.getHappenDate()) <= 0)
                || (rstBaseinfo.getVdate() != null
                    && rstBaseinfo.getVdate().compareTo(acctEntity.getHappenDate()) > 0)) {
                break;
            }
            // 初始化定义 - 记账场景要素管理
            AccountDriver ad = new AccountDriver();
            // 会计场景
            ad.setTreeNode(acctEntity.getBookSetTreeNode());
            if (acctEntityEx.getInvAim().equals(
                DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue())) {
                ad.setTableId(DictCodeListTableId.会计场景代码_公允价值损益类债券_每日计提.getValue());
            }
            else if (acctEntityEx.getInvAim().equals(
                DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue())) {
                ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类债券_每日计提摊销.getValue());
            }
            else if (acctEntityEx.getInvAim().equals(DictCodeListExt1.投资目的_摊余成本类.getValue())) {
                ad.setTableId(DictCodeListTableId.会计场景代码_摊余成本类债券_每日计提摊销.getValue());
            }

            // 初始化定义 - 存放该笔记账的场景要素 map定义
            Map<String, Object> accountSceneMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
            Map<String, Object> accountCurrencyMap = new HashMap<>();

            // accountSceneMap设置
            accountSceneMap = bookCommonLogic.accrualElementAssign(acctEntity, acctEntityEx);
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
            // 账套代码
            accountSceneMap.put(BookConstant.BOOKSET_ID, acctEntityEx.getBooksetId());
            // 冲正标识
            accountSceneMap.put(BookConstant.OFFSET_FLAG, DictCodeList.冲正标识_正常交易.getValue());
            // 备注
            accountSceneMap.put(BookConstant.BOOK_REMARK, "债券类资产每日计提");
            List<String> paramCodeList = new ArrayList<String>();
            // 账套配置表参数项_本位币差额处理方式
            paramCodeList.add(ValDictCodeList.账套配置表参数项_本位币差额处理方式.getValue()); 
            paramCodeList.add(ValDictCodeList.账套配置表参数项_减值资产第三阶段利息计提摊销差额计入科目.getValue()); 
            paramCodeList.add(ValDictCodeList.账套配置表参数项_减值资产第三阶段是否重新计算实际利率.getValue()); 
            Map<String, Object> getValBokBooksetExtMap = accountingCommonService
                    .getValBokBooksetExtForMap(acctEntity.getBooksetId(), paramCodeList);
            
            String bccyadjType = "";
            if(getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_本位币差额处理方式.getValue()) != null){
                bccyadjType = (String)getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_本位币差额处理方式.getValue());
            }
            String subjectNoType = "";
            if(getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_减值资产第三阶段利息计提摊销差额计入科目.getValue()) != null){
                subjectNoType = (String)getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_减值资产第三阶段利息计提摊销差额计入科目.getValue());
            }
            String recalcType = "";
            if(getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_减值资产第三阶段是否重新计算实际利率.getValue()) != null){
                recalcType = (String)getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_减值资产第三阶段是否重新计算实际利率.getValue());
            }
            // accountAmountMap设置
            // 3.1 上次付息日到发生日+1日的应计利息
            // 变量.本日应计利息余额
            BigDecimal tdyInterestAccBal = BigDecimal.ZERO;
            BigDecimal intAmt = BigDecimal.ZERO;
            // ！变量.标的信息.息票品种为零息或贴现时 or 投资目的 = 以公允价值变动计量
            if ((!DictCodeList.息票品种_零息.getValue().equals(rstBaseinfo.getCouponSpecies())
                 && !DictCodeList.息票品种_贴现.getValue().equals(rstBaseinfo.getCouponSpecies()))
                || acctEntityEx.getInvAim().equals(
                    DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue())) {
                // else if（变量.当日成本科目数量 = 0 and 变量.当日计算成本科目数量 = 0 ）
                if (varNumAmtHappenDate.compareTo(BigDecimal.ZERO) == 0
                		/**MODIFY BY WUJIE ON 20191217 START
                        && varDrjscbKmsl.compareTo(BigDecimal.ZERO) == 0**/
                        &&  账目科目数量.compareTo(BigDecimal.ZERO) == 0
                        /**MODIFY BY WUJIE ON 20191217 END**/
                        ) {
                    // 变量.应计利息 = 变量.应计利息余额 * -1
                    intAmt = varInterestAccBal.negate();
                }
                else {
                    // update by caiqun 2021-02-05 修复ABS债付息计划调整时取因年度付息天数取值错误，导致计提应收利息不对问题  start
                    AccurIntCalOutParameter accurIntCalOutParameter = null;
                    if(DictCodeList.WIND一级分类_含债券一级分类等_资产支持证券.getValue().equals(rstBaseinfo.getType4())){
                        accurIntCalOutParameter = calculationCommonService.getCalculateForAbs(
                                acctEntityEx, rstBaseinfo, listIntinfo, listCashinfo, listPayinfo,
                                varFaceValue, acctEntity);
                    }else{
                        accurIntCalOutParameter = calculationCommonService.getCalculate(
                                acctEntityEx, rstBaseinfo, listIntinfo, listCashinfo, listPayinfo,
                                varFaceValue, acctEntity);
                    }
                    // update by caiqun 2021-02-05 修复ABS债付息计划调整时取因年度付息天数取值错误，导致计提应收利息不对问题  start
                    // 返回值.应计利息做小数位处理
                    BigDecimal interest = accurIntCalOutParameter == null ? BigDecimal.ZERO : accurIntCalOutParameter.getInterest();
                    //MODIFY JINMAODONG 20190517 START
                    //增加了一个开关SEC_INT_SCALE用于控制应计利息的舍位方式，是先做舍位还是先做计算再做舍位；参数为空或者为"Y"的时候则先做舍位再做计算
                    // 返回值.应计利息做小数位处理
                    //银行间债券在计算应计利息时的舍位位数
                    String SEC_INT_SCALE_BANK = CacheUtil.getSysParam("SEC_INT_SCALE_BANK");
                    //交易所债券在计算应计利息时的舍位位数
                    String SEC_INT_SCALE_EXCHANGE = CacheUtil.getSysParam("SEC_INT_SCALE_EXCHANGE");
                    //控制是先计算利息还是先对应计利息进行舍位：Y-先舍位；N-先计算
                    String SEC_INT_SCALE = CacheUtil.getSysParam("SEC_INT_SCALE");
                    if(SEC_INT_SCALE==null||SEC_INT_SCALE.equals(DictCodeList.是否_是.getValue())){
                        // if（标的信息.交易场所 = "银行间"）
                        if (DictCodeListExt1.估值交易市场_银行间.getValue().equals(rstBaseinfo.getTradeMarket())) {
                            // 调用金额共通方法设置有效小数位,小数位数取值共通参数 SEC_INT_SCALE_BANK
                            interest = ArithmeticUtil.round(interest,
                                    Integer.parseInt(StringUtils.isEmpty(SEC_INT_SCALE_BANK) ? "12" : SEC_INT_SCALE_BANK),null);
                        }
                        /**add by taoran 20200110 start **/
                        // else if（标的信息.交易场所 = "上交所" or "深交所"）
                        else if (DictCodeListExt1.估值交易市场_上交所.getValue().equals(rstBaseinfo.getTradeMarket()) || 
                        			DictCodeListExt1.估值交易市场_深交所.getValue().equals(rstBaseinfo.getTradeMarket())) {
                            // 调用金额共通方法设置有效小数位,小数位数取值共通参数 SEC_INT_SCALE_EXCHANGE	
                            interest = ArithmeticUtil.round(interest,
                                    Integer.parseInt(StringUtils.isEmpty(SEC_INT_SCALE_EXCHANGE) ? "8" : SEC_INT_SCALE_EXCHANGE),null);
                        }
                        else {
                            // 调用金额共通方法设置有效小数位,小数位数取值共通参数 SEC_INT_SCALE_BANK
                            interest = ArithmeticUtil.round(interest,
                                    Integer.parseInt(StringUtils.isEmpty(SEC_INT_SCALE_BANK) ? "12" : SEC_INT_SCALE_BANK),null);
                        }
                        /**add by taoran 20200110 end**/
                        // 变量.本日应计利息余额 =round(上述返回值,舍位参数) * 变量.百元券面面值 /100 * 变量.变量.当日计算成本科目数量
                        tdyInterestAccBal = ArithmeticUtil.multiply(interest, varDrjscbKmsl);
                        tdyInterestAccBal = ArithmeticUtil.multiply(tdyInterestAccBal, varFaceValue);
                        tdyInterestAccBal = ArithmeticUtil.divide(tdyInterestAccBal,new BigDecimal(100), NumberEnum.HIGH_PREC_NUM.getCode());
                    }else{
                        // 变量.本日应计利息余额 =round (上述返回值  * 变量.百元券面面值 /100,舍位参数)* 变量.变量.当日计算成本科目数量
                        tdyInterestAccBal = ArithmeticUtil.multiply(interest, varFaceValue);
                        tdyInterestAccBal = ArithmeticUtil.divide(tdyInterestAccBal,new BigDecimal(100), NumberEnum.HIGH_PREC_NUM.getCode());
                        // if（标的信息.交易场所 = "银行间"）
                        if (DictCodeListExt1.估值交易市场_银行间.getValue().equals(rstBaseinfo.getTradeMarket())) {
                            // 调用金额共通方法设置有效小数位,小数位数取值共通参数 SEC_INT_SCALE_BANK
                            tdyInterestAccBal = ArithmeticUtil.round(tdyInterestAccBal,
                                    Integer.parseInt(StringUtils.isEmpty(SEC_INT_SCALE_BANK) ? "12" : SEC_INT_SCALE_BANK),null);
                        }
                        /**add by taoran 20200110 start **/
                        // else if（标的信息.交易场所 = "上交所" or "深交所"）
                        else if (DictCodeListExt1.估值交易市场_上交所.getValue().equals(rstBaseinfo.getTradeMarket()) || 
                        			DictCodeListExt1.估值交易市场_深交所.getValue().equals(rstBaseinfo.getTradeMarket())) {
                            // 调用金额共通方法设置有效小数位,小数位数取值共通参数 SEC_INT_SCALE_EXCHANGE	
                        	tdyInterestAccBal = ArithmeticUtil.round(tdyInterestAccBal,
                                    Integer.parseInt(StringUtils.isEmpty(SEC_INT_SCALE_EXCHANGE) ? "8" : SEC_INT_SCALE_EXCHANGE),null);
                        }
                        else {
                            // 调用金额共通方法设置有效小数位,小数位数取值共通参数 SEC_INT_SCALE_BANK
                        	tdyInterestAccBal = ArithmeticUtil.round(tdyInterestAccBal,
                                    Integer.parseInt(StringUtils.isEmpty(SEC_INT_SCALE_BANK) ? "12" : SEC_INT_SCALE_BANK),null);
                        }
                        /**add by taoran 20200110 end**/
                        tdyInterestAccBal = ArithmeticUtil.multiply(tdyInterestAccBal, varDrjscbKmsl);
                    }
                    //modify jinmaodong 2020-03-03
                    //BUG修复：A/365F，2月29日不计提得场景，如果当日计提金额第三位小数是0.005得话，会存在一分钱得尾差
                    //只影响此场景，其他场景暂未发现存在影响
                    tdyInterestAccBal = ArithmeticUtil.round(tdyInterestAccBal,NumberEnum.MONEY);
                    // 变量.应计利息 = 变量.本日应计利息余额 - 变量.当日计算应计利息余额
                    intAmt = ArithmeticUtil.subtract(tdyInterestAccBal, varDrjsyjlxKmye);

                    /**
                     BigDecimal interest = accurIntCalOutParameter.getInterest();

                     // if（标的信息.交易场所 = "银行间"）
                    if (DictCodeListExt1.估值交易市场_银行间.getValue().equals(rstBaseinfo.getTradeMarket())) {
                        // 调用金额共通方法设置有效小数位,小数位数取值共通参数 SEC_INT_SCALE_BANK
                        interest = ArithmeticUtil.round(interest,
                            Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam(
                                "SEC_INT_SCALE_BANK")) ? "12" : CacheUtil.getSysParam(
                                    "SEC_INT_SCALE_BANK")),
                            null);
                    }
                    else {
                        // 调用金额共通方法设置有效小数位,小数位数取值共通参数 SEC_INT_SCALE_EXCHANGE
                        interest = ArithmeticUtil.round(interest,
                            Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam(
                                "SEC_INT_SCALE_EXCHANGE")) ? "8" : CacheUtil.getSysParam(
                                    "SEC_INT_SCALE_EXCHANGE")),
                            null);
                    }
                    // 变量.本日应计利息余额 =上述返回值 * 变量.变量.当日计算成本科目数量 * 变量.百元券面面值 /100
                    tdyInterestAccBal = ArithmeticUtil.multiply(interest, varDrjscbKmsl);
                    tdyInterestAccBal = ArithmeticUtil.multiply(tdyInterestAccBal, varFaceValue);
                    tdyInterestAccBal = ArithmeticUtil.divide(tdyInterestAccBal,
                        new BigDecimal(100), NumberEnum.HIGH_PREC_NUM.getCode());
                    // 变量.应计利息 = 变量.本日应计利息余额 - 变量.当日计算应计利息余额
                    intAmt = ArithmeticUtil.subtract(tdyInterestAccBal, varDrjsyjlxKmye);
                     **/
                    //MODIFY JINMAODONG 20190517 END
                }
            }

            accountAmountMap.put(BookConstant.INTAMT,
                ArithmeticUtil.round(intAmt, NumberEnum.MONEY));
            // 应计利息 <-变量.币种
            accountCurrencyMap.put(BookConstant.INTAMT, varCcy);

            // 3.2 摊销计提
            // 定义 变量.利息调整
            BigDecimal varLxtz = BigDecimal.ZERO;
            // 定义 变量.投资收益
            BigDecimal varTzsy = BigDecimal.ZERO;
            // 定义 变量.摊销日利率
            BigDecimal varDayRate = BigDecimal.ZERO;
            //定义变量 摊销日利率的计算是否是由于折溢价为0时，计算值用于减值计提的时候用的（统计分析明细表（账务明细）不存该值） modify by yanya on 20210105
            boolean 折溢价为零计算标识 = false;

            // 持有目的! = 以公允价值变动计量 ---> 改为估值方法只要是摊余成本法时
            if (isAcFlag) {
                // else if（变量.当日成本科目数量 = 0 and 变量.当日计算成本科目数量 = 0 ）
                if (varNumAmtHappenDate.compareTo(BigDecimal.ZERO) == 0
                		/**MODIFY BY WUJIE ON 20191216 START
                         * && varDrjscbKmsl.compareTo(BigDecimal.ZERO) == 0
                         **/
                        && 账目科目数量.compareTo(BigDecimal.ZERO) == 0
                        /**MODIFY BY WUJIE ON 20191216 END**/
                        ) {
                    // 变量.利息调整 = 变量.溢折价科目余额*-1
                    varLxtz = varSpilledPriceAccBal.negate();
                }
                else {
                    // 标的信息.到期日 - 1= 计提类账务处理结果.发生日
                    Date ystMdate = addDate(rstBaseinfo.getMdate(), -1);
                    if (ystMdate.compareTo(acctEntity.getHappenDate()) == 0) {
                        // 变量.利息调整 = 变量.溢折价科目余额*-1
                        varLxtz = varSpilledPriceAccBal.negate();
                    }
                    // else if（变量.标的信息.含权债摊销日！=到期日 && 变量.行权计划不为空时 && 变量.行权计划.计划行权日 - 1=
                    // 计提类账务处理结果.发生日）
                    else if (!DictCodeList.含权债摊销日_到期日.getValue().equals(rstBaseinfo.getExeDate())
                             && finExercisePlan != null
                             && DateUtil.compareDate(
                                 DateUtil.addDay(finExercisePlan.getExerciseDate(), -1),
                                 acctEntity.getHappenDate()) == 0) {
                        // 变量.利息调整 = 变量.溢折价科目余额*-1
                        varLxtz = varSpilledPriceAccBal.negate();
                    }
                    else {
                        BigDecimal varCash = ArithmeticUtil.doAdd(varDrjstxzyjKmye, varDrjscbKmye);
                        varCash = ArithmeticUtil.subtract(varCash,
                            ArithmeticUtil.multiply(varDrjscbKmsl, varFaceValue));
                        // else if（(变量.当日计算摊销溢折价科目余额 + 变量.当日计算摊销成本科目余额) - (变量.当日计算摊销成本数量余额 *
                        // 变量.百元券面面值) != 0 ）
                        if (varCash.compareTo(BigDecimal.ZERO) != 0) {
                            // 变量.最新报价
                            BigDecimal newCash = null;
                            // 是否利率变更
                            boolean isLlbg = false;
                            if (listCashinfo != null && listCashinfo.size() > 0) {
                                newCash = BigDecimal.ZERO;
                                for (TargetCashCalcRule targetCashCalcRule : listCashinfo) {
                                    // 报价.生效日 > 计提类账务处理结果.发生日
                                    if (targetCashCalcRule.getEffDate().compareTo(
                                        acctEntity.getHappenDate()) > 0) {
                                        break;
                                    }
                                    // 报价.生效日 = 计提类账务处理结果.发生日
                                    if (targetCashCalcRule.getEffDate().compareTo(
                                        acctEntity.getHappenDate()) == 0
                                        && newCash.compareTo(targetCashCalcRule.getYield()) != 0) {
                                        isLlbg = true;
                                    }
                                    // 变量.最新报价 = 报价temp.生效利率
                                    newCash = targetCashCalcRule.getYield();
                                }
                            }
							// 变量.摊销重算配置DTO = 调用计算类共通【摊销重算配置查询
							List<BokAmoConfig> bokAmoConfigList = calculationCommonService.getBokAmoConfig(
									acctEntity.getBooksetId(), acctEntity.getBusiId(), acctEntity.getHappenDate());
							BokAmoConfig bokAmoConfig = bokAmoConfigList != null && bokAmoConfigList.size() > 0
									? bokAmoConfigList.get(0) : null;
							// 变量.重算Flag = false
							boolean amoFlg = false;
							// 变量.昨日统计分析账务明细汇总信息.日摊销利率 == null )
							if (bokSummaryTableYestdy == null
									|| StringUtils.isEmpty(bokSummaryTableYestdy.getBooksetId())) {
								amoFlg = true;
							} else if (bokAmoConfig == null) {
								if (count > 0 || isLlbg) {
									amoFlg = true;
								}
								/**ADD BY CAOGUO ON 20201113 减值相关增加 START */
	                            // if (减值资产第三阶段是否重新计算实际利率 = 02：重算 ）
	                            if (DictCodeListExt3.减值资产第三阶段是否重新计算实际利率_重算.getValue().equals(
	                                recalcType)) {
	                                if (lossCommonService.isP3InOrOut(happenDate,
	                                    acctEntityEx.getBusiId(), acctEntityEx.getNumP3(), null)) {
	                                    amoFlg = true;
	                                }
	                            }
	                            /**ADD BY CAOGUO ON 20201113 减值相关增加 end */
//	                        } else if (DictCodeList.是否_是.getValue().equals(bokAmoConfig.getIsRecal())) {
                            // 变量.摊销重算配置DTO.是否重算 = "是" 重算方式 = “重算” or “指定实际利率” //设计变更-计提摊销重算逻辑修改 zhangyf 20181205
                            } else if (DictCodeList.重算方式_重算.getValue().equals(bokAmoConfig.getRecalType())
                                || DictCodeList.重算方式_指定实际利率.getValue().equals(bokAmoConfig.getRecalType())) {
                                amoFlg = true;
                            }
                            //设计变更-计提摊销重算逻辑修改 zhangyf 20181205 start
                            //if (变量.重算Flag = true and 变量.摊销重算配置DTO.实际日利率 ！=null)
                            if (amoFlg && null!= bokAmoConfig && null != bokAmoConfig.getDailydscYield()) {
                                //变量.利息调整 = 变量.摊销重算配置DTO.实际日利率 * (变量.当日计算摊销溢折价科目余额 + 变量.当日计算摊销成本科目余额) - 金额元素.应计利息  
                                varLxtz = ArithmeticUtil.subtract(
                                    ArithmeticUtil.multiply(
                                        bokAmoConfig.getDailydscYield(),
                                        ArithmeticUtil.doAdd(varDrjstxzyjKmye, varDrjscbKmye)),
                                    ArithmeticUtil.round(intAmt, NumberEnum.MONEY),
                                    NumberEnum.MONEY.getCode(), null);
                                //变量.摊销日利率 = 变量.摊销重算配置DTO.实际日利率   
                                varDayRate = bokAmoConfig.getDailydscYield();
                            }
                            // else (变量.重算Flag = true and 变量.摊销重算配置DTO.实际日利率 =null)
                            else if (amoFlg && (null==bokAmoConfig ||null == bokAmoConfig.getDailydscYield())) {
                            // 变量.买入交易列表 ！=NULL
                            //if (amoFlg) {
                            //设计变更-计提摊销重算逻辑修改 zhangyf 20181205 end
                            	SecDscYieldOutParameter secDscYieldOutParameter = new SecDscYieldOutParameter();
                            	String basisString = listCashinfo != null
                                        && listCashinfo.size() > 0 ? listCashinfo.get(0).getBasis() : null;
                                if (DictCodeList.计息基础_30_360.getValue().equals(basisString)
                                		&& ArithmeticUtil.equals(ArithmeticUtil.round(intAmt, NumberEnum.MONEY), BigDecimal.ZERO)) {
                                	secDscYieldOutParameter = null;
                                } else {
                                    // 计算债券的日摊销收益率
                                    secDscYieldOutParameter = getSecDscYieldCalculator(
                                        acctEntityEx, rstBaseinfo, listIntinfo, listCashinfo,
                                        listPayinfo, varDrjstxzyjKmye, varDrjscbKmye, varDrjsyjlxKmye,
                                        varFaceValue, newCash, acctEntity, finExercisePlan,
                                        varPlanFaceValue, varDrjscbKmsl);
                                }

                                // //变量.利息调整 = 当日摊销发生额
                                // varLxtz = secDscYieldOutParameter.getCurrDayAmount();
                                // 变量.利息调整 = 摊销日利率 * (变量.当日计算摊销溢折价科目余额 + 变量.当日计算摊销成本科目余额) -
                                // 金额元素.应计利息
                                if (secDscYieldOutParameter != null) {
                                    varLxtz = ArithmeticUtil.subtract(
                                        ArithmeticUtil.multiply(
                                            secDscYieldOutParameter.getDayRate(),
                                            ArithmeticUtil.doAdd(varDrjstxzyjKmye, varDrjscbKmye)),
                                        ArithmeticUtil.round(intAmt, NumberEnum.MONEY),
                                        NumberEnum.MONEY.getCode(), null);
                                    logger.info("资产代码: " + rstBaseinfo.getFinprodId() + " 发生日："
                                                + DateUtil.format(happenDate, "yyyy-MM-dd")
                                                + " 摊销日利率: " + secDscYieldOutParameter.getDayRate()
                                                + " 应计利息: "
                                                + ArithmeticUtil.round(intAmt, NumberEnum.MONEY)
                                                + " 日摊销额: " + varLxtz);

                                    // 赋值 变量.摊销日利率
                                    varDayRate = secDscYieldOutParameter.getDayRate();
                                }

                            }
                            else {
                                if (bokSummaryTableYestdy != null && StringUtils.isNotEmpty(
                                        bokSummaryTableYestdy.getBooksetId())) {
                                	String basisString = listCashinfo != null
                                            && listCashinfo.size() > 0 ? listCashinfo.get(0).getBasis() : null;
                                    if (DictCodeList.计息基础_30_360.getValue().equals(basisString)
                                    		&& ArithmeticUtil.equals(ArithmeticUtil.round(intAmt, NumberEnum.MONEY), BigDecimal.ZERO)) {
                                    	varLxtz = BigDecimal.ZERO;
                                    } else {
                                        // 变量.利息调整 = 变量.昨日统计分析账务明细汇总信息.日摊销利率 * (变量.当日计算摊销溢折价科目余额 +
                                        // 变量.当日计算摊销成本科目余额) - 金额元素.应计利息
                                        varLxtz = ArithmeticUtil.subtract(
                                            ArithmeticUtil.multiply(
                                                bokSummaryTableYestdy.getDailydscYield(),
                                                ArithmeticUtil.doAdd(varDrjstxzyjKmye, varDrjscbKmye)),
                                            ArithmeticUtil.round(intAmt, NumberEnum.MONEY),
                                            NumberEnum.MONEY.getCode(), null);
                                    }
                                    // 赋值 变量.摊销日利率
                                    varDayRate = bokSummaryTableYestdy.getDailydscYield();
                                  }
                            }
                            // if（"A/365F" = 计提规则.get(0).计算基础 && 计提类账务处理结果.发生日 为 2/29日时） 
                            String basisString = listCashinfo != null
                                && listCashinfo.size() > 0 ? listCashinfo.get(
                                    0).getBasis() : null;
                           if (DictCodeList.计息基础_A_365F.getValue().equals(basisString)
                               && "02-29".equals(
                                   DateUtil.format(happenDate, "MM-dd"))) {
                               // 变量.利息调整 = 0
                               varLxtz = BigDecimal.ZERO;
                           }
                        }
                        //modify by yanya on 20201229 折溢价为空的时候，根据票面利率计算
                        else {
                            varDayRate = this.calSecInterest(rstBaseinfo, listIntinfo, listPayinfo, listCashinfo, happenDate);
                            //modify by yanya on 20210105 由于折溢价为0时，计算摊销日利率值用于减值计提，统计分析明细表（账务明细）不存该值
                            折溢价为零计算标识 = true;
                        }
                    }
                }
            }
            else {
                varLxtz = varSpilledPriceAccBal.negate();
            }
            // 变量.投资收益 = 变量.利息调整 + 3.1 处理的应计利息
            varTzsy = ArithmeticUtil.doAdd(varLxtz, intAmt);
            /**ADD BY CAOGUO ON 20201019 减值相关修改 START **/
            //公允价值变动无需处理减值计提
            // 定义      变量.减值计提 = 0
            BigDecimal jzzb = BigDecimal.ZERO;
            if (!acctEntityEx.getInvAim().equals(DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue())) {
                // if（计提类账务处理结果.减值阶段3数量 >0 && 变量.投资收益！=0）
                if (acctEntityEx.getNumP3() > 0
                    && varTzsy.compareTo(BigDecimal.ZERO) != 0) {
                    // 变量.投资收益 = 变量.摊销日利率 *（变量.当日计算减值准备科目余额 + 变量.当日计算摊销溢折价科目余额 + 变量.当日计算摊销成本科目余额）
                    varTzsy = ArithmeticUtil.round(ArithmeticUtil.multiply(
                        ArithmeticUtil.doAdd(varDrjstxzyjKmye, varDrjscbKmye, varDrjJzzbYe),
                        varDayRate), NumberEnum.MONEY);
                    // if ( 减值资产第三阶段利息计提摊销差额计入= 01：减值准备)）
                    if (DictCodeListExt3.减值资产第三阶段利息计提摊销差额计入科目_减值准备.getValue().equals(
                        subjectNoType)) {
                        // 变量.减值计提 = 变量.利息调整 + 3.1 处理的应计利息 - 变量.投资收益
                        jzzb = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(varLxtz, intAmt),
                            varTzsy);
                    }
                    else {
                        //变量.利息调整 = 变量.投资收益 - 3.1 处理的应计利息
                        varLxtz = ArithmeticUtil.doSub(varTzsy, intAmt);
                    }                    
                }
                // if（变量.减值计提 >= 0）
                if (jzzb.compareTo(BigDecimal.ZERO) >= 0) {
                    // 减值计提（贷） = 变量.减值计提
                    accountAmountMap.put(BookConstant.IMPAMT_C,
                        ArithmeticUtil.round(jzzb, NumberEnum.MONEY));
                    accountCurrencyMap.put(BookConstant.IMPAMT_C, varCcy);
                }
                else {
                    // 减值计提（借） = 变量.减值计提 * -1
                    accountAmountMap.put(BookConstant.IMPAMT_D,
                        ArithmeticUtil.round(jzzb.negate(), NumberEnum.MONEY));
                    accountCurrencyMap.put(BookConstant.IMPAMT_D, varCcy);
                }
            }
            /**ADD BY CAOGUO ON 20201019 END**/
            // 变量.利息调整 > 0
            if (varLxtz.compareTo(BigDecimal.ZERO) >= 0) {
                // 利息调整（借） = 变量.利息调整
                accountAmountMap.put(BookConstant.DSCINC_D,
                    ArithmeticUtil.round(varLxtz, NumberEnum.MONEY));
                accountCurrencyMap.put(BookConstant.DSCINC_D, varCcy);
            }
            else {
                // 利息调整（贷） = 变量.利息调整 * -1
                accountAmountMap.put(BookConstant.DSCINC_C, ArithmeticUtil.multiply(
                    new BigDecimal(-1), ArithmeticUtil.round(varLxtz, NumberEnum.MONEY)));
                accountCurrencyMap.put(BookConstant.DSCINC_C, varCcy);
            }
            // 投资收益（贷） = 变量.投资收益
            accountAmountMap.put(BookConstant.INCOME_C,
                ArithmeticUtil.round(varTzsy, NumberEnum.MONEY));
            accountCurrencyMap.put(BookConstant.INCOME_C, varCcy);
//            // 投资收益大于0
//            if (varTzsy.compareTo(BigDecimal.ZERO) >= 0) {
//                // 投资收益（贷） = 变量.投资收益
//                accountAmountMap.put(BookConstant.INCOME_C,
//                    ArithmeticUtil.round(varTzsy, NumberEnum.MONEY));
//                accountCurrencyMap.put(BookConstant.INCOME_C, varCcy);
//            }
//            else {
//                // 投资收益（借） = 变量.投资收益 * -1
//                accountAmountMap.put(BookConstant.INCOME_D, ArithmeticUtil.multiply(
//                    new BigDecimal(-1), ArithmeticUtil.round(varTzsy, NumberEnum.MONEY)));
//                accountCurrencyMap.put(BookConstant.INCOME_D, varCcy);
//            }

            /** modify by yanya on 20200109 增加本位币处理 start**/
            // 初始化定义 - 存放该笔本位币记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountBCCYAMTMap = new HashMap<>();
            BigDecimal 汇率 = BigDecimal.ONE;
            //if(交易明细.币种 != 交易类账务处理结果..本位币){
            if(StringUtil.isNotEmpty(varCcy) && !varCcy.equals(acctEntityEx.getStandardCcy())){
                List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(acctEntity.getBookDate(), acctEntity.getBooksetId(),
                        acctEntityEx.getStandardCcy(), rstBaseinfo.getCcy());
                if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                    汇率 = currencyMarketList.get(0).getClosePrice();
                }
            }
            // 本位币记账金额元素
            for (String key : accountAmountMap.keySet()) {
                if(accountAmountMap.get(key) != null) {
                    accountAmountBCCYAMTMap.put(key + BookConstant.BCCYAMT,
                            calculationCommonService.doMultiply(accountAmountMap.get(key), 汇率));
                }
                else {
                    accountAmountBCCYAMTMap.put(key + BookConstant.BCCYAMT, BigDecimal.ZERO);
                }
            }
            // 记账金额元素币种信息塞值--key是随记账本位币金额map的
            for (String key : accountAmountBCCYAMTMap.keySet()) {
                accountCurrencyMap.put(key, acctEntityEx.getStandardCcy());
            }
            /** 本位币 币种**/
            accountCurrencyMap.put(BookConstant.B_CCY, acctEntityEx.getStandardCcy());
            //记账金额元素 putALl 随记账本位币金额map
            accountAmountMap.putAll(accountAmountBCCYAMTMap);
            //accountAmountMap再次设置如下：
            
            /** MODIFY BY CAOGUO ON 20201019 修改了精度保留地方 START **/
            /** ADD BY CAOGUO ON 20201019 减值相关修改 START **/
            // if (本位币差额处理方式=02 减值准备 && 减值资产第三阶段利息计提摊销差额计入= 01：减值准备)
            if (DictCodeListExt3.本位币差额处理方式_减值准备.getValue().equals(bccyadjType)
                && DictCodeListExt3.减值资产第三阶段利息计提摊销差额计入科目_减值准备.getValue().equals(subjectNoType)) {
                // 定义 变量.本币减值计提 = 变量.利息调整 * 变量.汇率 + 应计利息_本位币 - 投资收益_本位币
                BigDecimal jzzbB = ArithmeticUtil.round(
                    calculationCommonService.doMultiply(jzzb, 汇率), NumberEnum.MONEY);
                jzzbB = ArithmeticUtil.doSub(
                    ArithmeticUtil.doAdd(
                        ArithmeticUtil.round(calculationCommonService.doMultiply(varLxtz, 汇率),
                            NumberEnum.MONEY),
                        accountAmountBCCYAMTMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)),
                    jzzbB);
                // if（变量.减值计提 >= 0）
                if (jzzbB.compareTo(BigDecimal.ZERO) >= 0) {
                    // 减值计提本币（贷） = 变量.本币减值计提
                    accountAmountMap.put(BookConstant.IMPAMT_C + BookConstant.BCCYAMT,
                        ArithmeticUtil.round(jzzbB, NumberEnum.MONEY));
                    accountCurrencyMap.put(BookConstant.IMPAMT_C, varCcy);
                }
                else {
                    // 减值计提本币（借） = 变量.本币减值计提 * -1
                    accountAmountMap.put(BookConstant.IMPAMT_D + BookConstant.BCCYAMT,
                        ArithmeticUtil.round(jzzbB.negate(), NumberEnum.MONEY));
                    accountCurrencyMap.put(BookConstant.IMPAMT_D, varCcy);
                }
            }
            else {
                // 变量.投资收益_本位币 = round(变量.利息调整,共通金额位数) * 变量.汇率 + 应计利息_本位币
                BigDecimal 投资收益本位币 = ArithmeticUtil.doAdd(
                    ArithmeticUtil.round(calculationCommonService.doMultiply(varLxtz, 汇率), NumberEnum.MONEY),
                    accountAmountBCCYAMTMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT));
                投资收益本位币 = ArithmeticUtil.doSub(投资收益本位币, ArithmeticUtil.round(
                    calculationCommonService.doMultiply(jzzb, 汇率), NumberEnum.MONEY));
                accountAmountMap.put(BookConstant.INCOME_C + BookConstant.BCCYAMT,
                    ArithmeticUtil.round(投资收益本位币, NumberEnum.MONEY));
            }
            /** ADD BY CAOGUO ON 20201019 减值相关修改 START **/
                
            //if（变量.投资收益 > 0）
//            if(投资收益本位币.compareTo(BigDecimal.ZERO) > 0) {
//                //投资收益（贷）_本位币  	<-		变量.投资收益_本位币
//                accountAmountBCCYAMTMap.put(BookConstant.INCOME_C + BookConstant.BCCYAMT,
//                        ArithmeticUtil.round(投资收益本位币, NumberEnum.MONEY));
//            }
//            else {
//                //投资收益（借）_本位币  	<-		变量.投资收益_本位币 * -1
//                accountAmountBCCYAMTMap.put(BookConstant.INCOME_D + BookConstant.BCCYAMT,
//                        ArithmeticUtil.round(ArithmeticUtil.multiply(投资收益本位币, new BigDecimal(-1)), NumberEnum.MONEY));
//            }

            /** modify by yanya on 20200109 增加本位币处理 end**/
            // map塞入场景要素管理中
            // 记账场景要素
            ad.setAccountSceneMap(accountSceneMap);
            // 记账金额要素
            ad.setAccountAmountMap(accountAmountMap);
            // 记账金额币种要素
            ad.setAccountCurrencyMap(accountCurrencyMap);
            // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 start
            ad.setSubjectMappingConfigDefault(acctEntityEx.getSubjectMappingConfig());
            // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 end
            // 记账凭证生成
            BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            returnResult.getValBokBookdetailBatch().addAll(
                resultNowNow.getValBokBookdetailBatch());

            // SysBatchTask task = (SysBatchTask) param.get("BATCHTASK");
            // logger.error("【调查】债券计提Processor:[name = " + task.getBeanName() + "| id="
            // + task.getTaskId() + "| pri = " + task.getTaskPri()
            // + "| asset = " + acctEntity.getBusiId()
            // + "| count = " + resultNowNow.getValBokBookdetailBatch().size()
            // + ad.getAccountSceneMap() + ad.getAccountAmountMap()
            // + "]");

            // 3.3 统计分析账务明细汇总信息赋值
            // 定义：变量.计提利率
            BigDecimal jTrate = BigDecimal.ZERO;
            if (listCashinfo != null) {
                for (TargetCashCalcRule rule : listCashinfo) {
                    // 报价.生效日 > 计提类账务处理结果.发生日
                    if (rule.getEffDate().compareTo(acctEntity.getHappenDate()) > 0) {
                        break;
                    }

                    // 变量.计提利率 = 报价temp.生效利率
                    jTrate = rule.getYield();
                }
            }

            //modify by yanya on 20210105 由于折溢价为0时，计算摊销日利率值用于减值计提，统计分析明细表（账务明细）不存该值，这里清除
            if(折溢价为零计算标识) {
                varDayRate = BigDecimal.ZERO;
            }

            // 定义当日统计分析账务明细汇总信息
            BokStatDetPosiiton bokSummaryTableNew = new BokStatDetPosiiton();
            bokSummaryTableNew.setBooksetId(acctEntity.getBooksetId()); // 账套代码
            bokSummaryTableNew.setHappenDate(acctEntity.getHappenDate()); // 发生日期
            bokSummaryTableNew.setBookDate(acctEntity.getBookDate()); // 入账日期
            bokSummaryTableNew.setBokDetailId(acctEntityEx.getBokDetailId()); // 账务明细代码
            bokSummaryTableNew.setBookSummaryOrder(
                new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.账务汇总表_处理序列))); // 处理序列
            bokSummaryTableNew.setBooksetDate(acctEntityEx.getBooksetDate()); // 账套日期

            // 当前持仓 = 变量.当日成本科目数量
            bokSummaryTableNew.setPosAmt(varNumAmtHappenDate);
            // 计提利率 = 变量.计提利率
            bokSummaryTableNew.setIntRate(jTrate);
            // 日摊销收益率 = 摊销日利率
            bokSummaryTableNew.setDailydscYield(varDayRate);
            // 当日发生应计利息 = 变量.应计利息
            bokSummaryTableNew.setTdyIntincexpAdd(intAmt);
            // // 当日累计应计利息余额 = 变量.昨日统计分析账务明细汇总信息.当日累计应计利息余 + 变量.应计利息
            // BigDecimal tdyintincexpNow = BigDecimal.ZERO;
            // if (bokSummaryTableYestdy != null) {
            // tdyintincexpNow = ArithmeticUtil.doAdd(bokSummaryTableYestdy.getTdyIntincexp(),
            // intAmt);
            // }
            // else {
            // tdyintincexpNow = intAmt;
            // }
            // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额 = 变量.应计利息余额+ 变量.应计利息
            bokSummaryTableNew.setTdyIntincexp(ArithmeticUtil.doAdd(varInterestAccBal, intAmt));
            // 变量.当日统计分析账务明细汇总信息.当日份额 = 变量.当日计算成本科目数量
            bokSummaryTableNew.setTdyPosition(varDrjscbKmsl);
            // 变量.当日统计分析账务明细汇总信息.投资目的 = 计提类账务处理结果.投资目的
            bokSummaryTableNew.setInvAim(acctEntity.getInvAim());
            // 变量.当日统计分析账务明细汇总信息.当日成本 = 变量.成本科目余额
//            bokSummaryTableNew.setTdyCostAmt(varOAmtHappenDate);
            //modify jinmaodong 20190517 修改当日成本 = 变量.当日成本科目余额 + 变量.溢折价科目余额+变量.利息调整
            bokSummaryTableNew.setTdyCostAmt(ArithmeticUtil.doAdd(varOAmtHappenDate, varSpilledPriceAccBal, varLxtz));
            /**add by taoran 20200110 start**/									
            // if（标的信息.交易场所 = "银行间"）
//            if (DictCodeListExt1.估值交易市场_银行间.getValue().equals(rstBaseinfo.getTradeMarket())) {
//                //变量.当日统计分析账务明细汇总信息.当日份额_实际 = 变量.当日计算成本科目数量	
//            	bokSummaryTableNew.setTdyPosition(varDrjscbKmsl);
//            	//变量.当日统计分析账务明细汇总信息.当日成本_实际 = 变量.当日计算摊销溢折价科目余额 + 变量.当日计算摊销成本科目余额
//            	bokSummaryTableNew.setTdyCostAmt(ArithmeticUtil.doAdd(varDrjstxzyjKmye, varDrjscbKmye));
//            }
//            // else if（标的信息.交易场所 = "上交所" or "深交所"）
//            else if (DictCodeListExt1.估值交易市场_上交所.getValue().equals(rstBaseinfo.getTradeMarket()) || 
//            			DictCodeListExt1.估值交易市场_深交所.getValue().equals(rstBaseinfo.getTradeMarket())) {
//                //变量.当日统计分析账务明细汇总信息.当日份额_实际 = 变量.当日成本科目数量
//            	bokSummaryTableNew.setTdyPosition(varNumAmtHappenDate);
//            	//变量.当日统计分析账务明细汇总信息.当日成本_实际 = 变量.当日成本科目余额 + 变量.溢折价科目余额
//            	bokSummaryTableNew.setTdyPosition(ArithmeticUtil.doAdd(varOAmtHappenDate, varSpilledPriceAccBal));
//            }
//            else {
//            	//变量.当日统计分析账务明细汇总信息.当日份额_实际 = 变量.当日计算成本科目数量	
//            	bokSummaryTableNew.setTdyPosition(varDrjscbKmsl);
//            	//变量.当日统计分析账务明细汇总信息.当日成本_实际 = 变量.当日计算摊销溢折价科目余额 + 变量.当日计算摊销成本科目余额
//            	bokSummaryTableNew.setTdyCostAmt(ArithmeticUtil.doAdd(varDrjstxzyjKmye, varDrjscbKmye));
//            }
            /**add by taoran 20200110 end**/
            
            // 变量.当日统计分析账务明细汇总信息.金融产品类型 = 交易标的DTO.金融产品类型
            bokSummaryTableNew.setFinprodType(rstBaseinfo.getFinprodType());
            // 起息日
            bokSummaryTableNew.setVdate(rstBaseinfo.getVdate());
            // 到期日
            bokSummaryTableNew.setMdate(rstBaseinfo.getActmdate());
            // 定义 变量.计算类型
            String calType = "";
            if (!CollectionUtils.isEmpty(listCashinfo)) {
                // 变量.计算类型 = 变量.报价列表.get(0).计算类型
                calType = listCashinfo.get(0).getCalcType();
            }
            BigDecimal[] remainResult = calculationCommonService.calSecRemTermAndDuration(
                rstBaseinfo, listIntinfo, calType,
                finExercisePlan == null ? null : finExercisePlan.getExerciseDate(), happenDate);
            // 变量.当日统计分析账务明细汇总信息.剩余期限 = 返回.剩余期限
            bokSummaryTableNew.setEndDays1(remainResult[0]);
            // 变量.当日统计分析账务明细汇总信息.剩余存续期 = 返回.剩余存续期限
            bokSummaryTableNew.setEndDays2(remainResult[1]);
            // 变量.当日统计分析账务明细汇总信息.剩余期限用成本 = 变量.当日成本科目余额 + 变量.溢折价科目余额 + 变量.利息调整
            bokSummaryTableNew.setEndDaysCostAmt(
                ArithmeticUtil.doAdd(varOAmtHappenDate, varSpilledPriceAccBal, varLxtz));
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
            /** add by yanya on 20200109 本位币处理 start**/
            //本位币
            //变量.当日统计分析账务明细汇总信息.本位币 = 计提类账务处理结果.本位币
            bokSummaryTableNew.setbCcy(acctEntityEx.getStandardCcy());
            //变量.当日统计分析账务明细汇总信息.当日发生应计利息_本位币 = 应收利息_本位币
            bokSummaryTableNew.setTdyIntincexpAddB(accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT));
            //变量.当日统计分析账务明细汇总信息.当日累计应计利息余额_本位币 = 变量.应计利息余额_本位币 + 当日发生应计利息_本位币
            BigDecimal tdyIntincexpB = ArithmeticUtil.doAdd(varInterestAccBalBCCY,
                    accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT));
            bokSummaryTableNew.setTdyIntincexpB(ArithmeticUtil.round(tdyIntincexpB, NumberEnum.MONEY.getCode(), null));
            /** add by yanya     20200109 本位币处理 end**/

            /**ADD BY CAOGUO ON 20201015 减值相关修改 START **/
            bokSummaryTableNew.setTdyIntadjexpAdd(varLxtz);
            bokSummaryTableNew.setTdyIntadjexpAddB(varLxtz);
            bokSummaryTableNew.setTdyLossAmt(ArithmeticUtil.doAdd(varJzzbYe, jzzb.negate()));
            bokSummaryTableNew.setTdyLossAmtB(bokSummaryTableNew.getTdyLossAmt());
            // if（金额元素.减值准备 ！= 0）
            if (jzzb.compareTo(BigDecimal.ZERO) != 0) {
                BokStatLossPosiiton statLoss = null;
                for (ValBokBookdetailBatch detail : resultNowNow.getValBokBookdetailBatch()) {
                    // 变量.账务明细 = 调用账务明细生成共通返回结果中 基础科目性质 ="摊余成本类资产减值"的第一条数据
                    if (DictCodeBaseSubjectNatrueNew.摊余成本类_减值准备.getValue().equals(
                        detail.getBaseSubjectNature()) || DictCodeBaseSubjectNatrueNew.其他综合收益_减值准备.getValue().equals(
                            detail.getBaseSubjectNature())) {
                        // 调用【减值相关共通-减值计提明细数据生成】
                        statLoss = lossCommonService.setLossInfoAccr(detail, jzzb.negate(),
                            ArithmeticUtil.round(calculationCommonService.doMultiply(jzzb.negate(), 汇率),
                                NumberEnum.MONEY),
                            ArithmeticUtil.doAdd(varJzzbYe, jzzb.negate()),
                            ArithmeticUtil.round(
                                ArithmeticUtil.multiply(ArithmeticUtil.doAdd(varJzzbYe, jzzb.negate()), 汇率),
                                NumberEnum.MONEY),
                            null, null, DictCodeListExt3.减值生成方式_计提批处理.getValue());
                        break;
                    }
                }
                if (statLoss != null) {
                    // 变量.统计分析明细表（资产减值）减值使用列表.add(上述返回值)
                    returnResult.getBokStatLossPosiitonList().add(statLoss);
                }
            }
            /**ADD BY CAOGUO ON 20201015 END**/
            // 添加到返回值中
            returnResult.getBokSummaryList().add(bokSummaryTableNew);
        }
        return returnResult;
    }

    /**
     * 计算债券的日摊销收益率
     * 
     * @param acctEntityEx 计提基础信息DTO
     * @param rstBaseinfo 标的信息
     * @param listIntinfo 付息列表
     * @param listCashinfo 报价列表
     * @param listPayinfo 支付规则
     * @param varSpilledPriceAccBal 溢折价科目余额
     * @param varOAmtHappenDate 成本科目余额
     * @param varInterestAccBal 应计利息余额
     * @param varFaceValue 百元面值
     * @param newCash 最新报价
     * @param isAcFlag 是否摊余成本法计算
     * @return SecDscYieldOutParameter
     * @see
     * @since 1.0
     */
    private SecDscYieldOutParameter getSecDscYieldCalculator(DrawBaseInfoSelectComDTO acctEntityEx,
                                                             TargetFinProduct rstBaseinfo,
                                                             List<TargetCashPlan> listIntinfo,
                                                             List<TargetCashCalcRule> listCashinfo,
                                                             List<TargetCashPayRule> listPayinfo,
                                                             BigDecimal varSpilledPriceAccBal,
                                                             BigDecimal varOAmtHappenDate,
                                                             BigDecimal varInterestAccBal,
                                                             BigDecimal varFaceValue,
                                                             BigDecimal newCash,
                                                             DrawBaseInfoSelectComResult acctEntity,
                                                             FinExercisePlan finExercisePlan,
                                                             BigDecimal varPlanFaceValue,
                                                             BigDecimal varDrjscbKmsl) {
        // 查询摊销收益率计算日期
//        Date calcDate = getSecDscYieldCalcDay(acctEntity, listCashinfo);
    	Date calcDate = acctEntity.getHappenDate();
        SecDscYieldInParameter parameter = new SecDscYieldInParameter();
        // 计算日
		parameter.setCalDate(rstBaseinfo.getVdate().compareTo(calcDate) > 0
				? BasicUtil.dateToLocal(rstBaseinfo.getVdate()) : BasicUtil.dateToLocal(calcDate));
		//减值比例
		BigDecimal eclVaule = BigDecimal.ZERO;
        if(StringUtil.isNotEmpty(CacheUtil.getSysParam(DictCode.减值方案类型.getValue()))){
            eclVaule = lossCommonBatchService.getStage3VauleMod(acctEntity.getHappenDate(), rstBaseinfo.getFinprodId(), acctEntityEx.getBooksetId());
        }
        else{
            eclVaule = lossCommonBatchService.getStage3Vaule(acctEntity.getHappenDate(), rstBaseinfo.getFinprodMarketId());
        }
        //折算比例
		BigDecimal zhesuanbili = BigDecimal.ONE;
		if(acctEntityEx.getNumP3() >0 && eclVaule!=null){
		    zhesuanbili = zhesuanbili.subtract(eclVaule);
        }
		if (calcDate.compareTo(acctEntity.getHappenDate()) == 0) {
            // 券面总额
            parameter.setTotalMoney(ArithmeticUtil.multiply(new BigDecimal(100), varDrjscbKmsl));
            // 摊销目标额
            parameter.setTargetAmt(ArithmeticUtil.multiply(new BigDecimal(100), varDrjscbKmsl,zhesuanbili));
            // 摊余成本净价总额
            // 变量.溢折价科目余额 + 变量.成本科目余额
            parameter.setTotalNetPrice(
                ArithmeticUtil.doAdd(varSpilledPriceAccBal, varOAmtHappenDate));
            // 当日百元券面面值
            parameter.setAmount(varFaceValue);
        }
        else {
            SecSubjectBalanceDto dto = getSubjectBalanceByDate(acctEntity, acctEntityEx, calcDate);
            // 券面总额
            parameter.setTotalMoney(ArithmeticUtil.multiply(new BigDecimal(100), dto.getNumT1()));
            // 券面总额
            parameter.setTargetAmt(ArithmeticUtil.multiply(new BigDecimal(100), dto.getNumT1(),zhesuanbili));
            // 摊余成本净价总额
            // 变量.溢折价科目余额 + 变量.成本科目余额
            parameter.setTotalNetPrice(
                ArithmeticUtil.doAdd(dto.getSpilledPriceT1(), dto.getCbT1()));
            // 当日百元券面面值
            parameter.setAmount(dto.getFaceValue());
        }
        // 票面利率
        parameter.setPaperRate(newCash);
        // 零息贴现计算方案
        parameter.setPlan(
            com.joyintech.calculation.bonds.constant.GlobalConstant.DIS_DEBT_SCHEME_A);
        // 资产信息
        SecInfoModel secInfoModel = new SecInfoModel();
        // 债券起息日
        secInfoModel.setStartDate(BasicUtil.dateToLocal(rstBaseinfo.getVdate()));
        // 债券到期日
        secInfoModel.setEndDate(BasicUtil.dateToLocal(rstBaseinfo.getActmdate()));
        // 债券首次付息日
        Date firstPayDate = listPayinfo != null && listPayinfo.size() > 0 ? listPayinfo.get(
            0).getFirstPayDate() : null;
        secInfoModel.setFirstPayDate(BasicUtil.dateToLocal(firstPayDate));
        // 发行价
        secInfoModel.setIssuePrice(rstBaseinfo.getIssuePrice());
        // 债券类型
        Variety bondsType = null;
        if (DictCodeList.息票品种_贴现.getValue().equals(rstBaseinfo.getCouponSpecies())) {
            bondsType = Variety.贴现;
        }
        else if (DictCodeList.息票品种_零息.getValue().equals(rstBaseinfo.getCouponSpecies())) {
            bondsType = Variety.零息;
        }
        else if (DictCodeList.息票品种_附息.getValue().equals(rstBaseinfo.getCouponSpecies())) {
            bondsType = Variety.附息;
        }
        else if (DictCodeList.息票品种_利随本清.getValue().equals(rstBaseinfo.getCouponSpecies())) {
            bondsType = Variety.利随本清;
        }
        secInfoModel.setBondsType(bondsType);
        // 计息基础
        Basis basis = null;
        String basisString = listCashinfo != null
                             && listCashinfo.size() > 0 ? listCashinfo.get(0).getBasis() : null;
        if (DictCodeList.计息基础_A_360.getValue().equals(basisString)) {
            basis = Basis.A_360;
        }
        else if (DictCodeList.计息基础_A_365.getValue().equals(basisString)) {
            basis = Basis.A_365;
        }
        else if (DictCodeList.计息基础_A_A_平均分配.getValue().equals(basisString)
                 || DictCodeList.计息基础_A_A_按实际天数分配.getValue().equals(basisString)) {
            basis = Basis.A_A;
        }
        else if (DictCodeList.计息基础_A_365F.getValue().equals(basisString)) {
            basis = Basis.A_365F;
        }
        else if (DictCodeList.计息基础_30_360.getValue().equals(basisString)) {
            basis = Basis._30_360;
        }
        else if (DictCodeListExt3.计息基础_30U_360.getValue().equals(basisString)) {
            basis = Basis._30U_360;
        }
        secInfoModel.setBasis(basis);
        // 利率类型
        IntRateType intRateType = null;
        String intRateTypeString = listCashinfo != null
                                   && listCashinfo.size() > 0 ? listCashinfo.get(
                                       0).getCalcType() : null;
        if (DictCodeList.利率类型_固定利率.getValue().equals(intRateTypeString)) {
            intRateType = IntRateType.固定利率;
        }
        else if (DictCodeList.利率类型_浮动利率.getValue().equals(intRateTypeString)) {
            intRateType = IntRateType.浮动利率;
        }
        secInfoModel.setIntRateType(intRateType);
        // 利息分配方式
        AllocationType allocationType = null;
        if (DictCodeList.计息基础_A_A_平均分配.getValue().equals(basisString)) {
            allocationType = AllocationType.平均分配;
        }
        else if (DictCodeList.计息基础_A_A_按实际天数分配.getValue().equals(basisString)) {
            allocationType = AllocationType.按实际天数分配;
        }
        else {
            allocationType = AllocationType.按实际天数分配;
        }
        secInfoModel.setAllocationType(allocationType);
        // 付息计划推算方式
        SecIntSchCalMethod secIntSchCalMethod = null;
        String secIntSchCalMethodString = listPayinfo != null
                                          && listPayinfo.size() > 0 ? listPayinfo.get(
                                              0).getCalcRule() : null;
        if (DictCodeList.推算方式_起息日向后.getValue().equals(secIntSchCalMethodString)) {
            secIntSchCalMethod = SecIntSchCalMethod.起息日向后;
        }
        else if (DictCodeList.推算方式_到期日向前.getValue().equals(secIntSchCalMethodString)) {
            secIntSchCalMethod = SecIntSchCalMethod.到期日向前;
        }
        else if (DictCodeList.推算方式_首次支付日.getValue().equals(secIntSchCalMethodString)) {
            secIntSchCalMethod = SecIntSchCalMethod.首次付息日;
        } else {
        	secIntSchCalMethod = SecIntSchCalMethod.起息日向后;
        }
        secInfoModel.setCalMethod(secIntSchCalMethod);
        // 付息频率
        IntFrequency yearIntRate = null;
        String yearIntRateString = listPayinfo != null && listPayinfo.size() > 0 ? listPayinfo.get(
            0).getPayCycle() : null;
        if (DictCodeList.频率_月.getValue().equals(yearIntRateString)) {
            yearIntRate = IntFrequency.月付;
        }
        else if (DictCodeList.频率_季.getValue().equals(yearIntRateString)) {
            yearIntRate = IntFrequency.季付;
        }
        else if (DictCodeList.频率_年.getValue().equals(yearIntRateString)) {
            yearIntRate = IntFrequency.年付;
        }
        else if (DictCodeList.频率_半年.getValue().equals(yearIntRateString)) {
            yearIntRate = IntFrequency.半年付;
        }
        else {
            yearIntRate = IntFrequency.年付;
        }
        secInfoModel.setYearIntRate(yearIntRate);
        parameter.setSecInfoModel(secInfoModel);

        // 付息计划
        List<SecIntSchedule> secIntScheduleList = new ArrayList<SecIntSchedule>();
        if (listIntinfo != null && listIntinfo.size() > 0) {
            for (TargetCashPlan targetCashPlan : listIntinfo) {
                SecIntSchedule secIntSchedule = new SecIntSchedule();
                // 理论起始日
                secIntSchedule.setTheoryStartDate(
                    targetCashPlan.getVdateTerm() == null ? BasicUtil.dateToLocal(
                        targetCashPlan.getVdate()) : BasicUtil.dateToLocal(
                            targetCashPlan.getVdateTerm()));
                // 理论截止日
                secIntSchedule.setTheoryCutoffDate(
                    targetCashPlan.getMdateTerm() == null ? BasicUtil.dateToLocal(
                        targetCashPlan.getMdate()) : BasicUtil.dateToLocal(
                            targetCashPlan.getMdateTerm()));
                // 实际起始日
                secIntSchedule.setActualStartDate(
                    BasicUtil.dateToLocal(targetCashPlan.getVdate()));
                // 实际截止日
                secIntSchedule.setActualCutoffDate(
                    BasicUtil.dateToLocal(targetCashPlan.getMdate()));
                // 计息本金
                secIntSchedule.setCorpus(new BigDecimal(100));
                // 利息金额
                secIntSchedule.setAmount(targetCashPlan.getCashAmt());
                secIntScheduleList.add(secIntSchedule);
            }
            parameter.setSecIntScheduleList(secIntScheduleList);
        }
        // String isExe = CacheUtil.getSysParam("isSecToExeDate");
        String exeDate = rstBaseinfo.getExeDate();
        if (StringUtils.isNotEmpty(exeDate)
            && !DictCodeList.含权债摊销日_到期日.getValue().equals(exeDate)) {
            // 变量.行权计划不为空时传入
            if (finExercisePlan != null) {
                // 下一行权日 变量.行权计划.计划行权日
                parameter.setNextExeDate(BasicUtil.dateToLocal(finExercisePlan.getExerciseDate()));
                // 行权价 变量.行权计划.行权价
                parameter.setNextExePrice(finExercisePlan.getExercisePrice());
                // 行权日百元券面 变量.行权日百元券面
                parameter.setNextExeAmount(varPlanFaceValue);
                // 摊销价格模式 系统参数：DSC_TRAGEPRICE_MODE
                parameter.setExePlan(
                    NextExeCalMethod.parse(CacheUtil.getSysParam("DSC_TRAGEPRICE_MODE")));
            }
        }
        SecDscYieldCalculator calculator = CalculatorFactory.createSecDscYieldCalculator();
        SecDscYieldOutParameter output = calculator.calculate(parameter);
        output.setDayRate(output.getDayRate().setScale(12, RoundingMode.HALF_UP));
        return output;
    }

    /**
     * 获取债券属性科目余额
     * 
     * @param acctEntity
     * @param acctEntityEx
     * @return
     * @see
     * @since 1.0
     */
    private SecSubjectBalanceDto getSubjectBalanceByDate(DrawBaseInfoSelectComResult acctEntity,
                                                         DrawBaseInfoSelectComDTO acctEntityEx,
                                                         Date calcDate) {

        boolean isAcFlag = acctEntityEx.getIsAcMethod();
        SecSubjectBalanceDto dto = new SecSubjectBalanceDto();
        // 变量.百元券面面值
        BigDecimal varFaceValue = BigDecimal.valueOf(100);

        // 变量.当日成本科目余额
        BigDecimal varOAmtHappenDate = BigDecimal.ZERO;

        // 变量.当日成本科目数量
        BigDecimal varNumAmtHappenDate = BigDecimal.ZERO;

        // 变量.应计利息科目余额
        BigDecimal varInterestAccBal = BigDecimal.ZERO;

        // 变量.溢折价科目余额
        BigDecimal varSpilledPriceAccBal = BigDecimal.ZERO;

        // 1.2 百元券面处理
        varFaceValue = finProductCommonService.getFaceValue(acctEntity.getBusiId(),
            acctEntity.getHappenDate());

        // 科目余额查询参数
        SubjectBalanceSearchDto searchDao = new SubjectBalanceSearchDto();
        searchDao.setBooksetId(acctEntity.getBooksetId());
        searchDao.setBokDetailId(acctEntity.getBokDetailId());
        searchDao.setHappenDate(calcDate);
        searchDao.setBookDate(acctEntity.getBookDate());
        searchDao.setTaskPri(acctEntityEx.getTaskPri());

        // 2.6.成本余额
        List<SubjectBalance> listcbSubjectBalance = new ArrayList<SubjectBalance>();
        if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(acctEntityEx.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue())));
            listcbSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(
            acctEntityEx.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue())));
            listcbSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(
            acctEntityEx.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue())));
            listcbSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }

        if (listcbSubjectBalance != null && listcbSubjectBalance.size() > 0) {
            // 成本余额 = 科目余额
            varOAmtHappenDate = listcbSubjectBalance.get(0).getBalance();
            varNumAmtHappenDate = listcbSubjectBalance.get(0).getNum();
        }
        // 2.3.溢折价类科目余额
        List<SubjectBalance> listSubjectBalanceEX = new ArrayList<SubjectBalance>();
        if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_利息调整.getValue())));
            listSubjectBalanceEX = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(
            acctEntity.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue())));
            listSubjectBalanceEX = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(
            acctEntity.getInvAim()) && isAcFlag) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_利息调整.getValue())));
            listSubjectBalanceEX = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        if (listSubjectBalanceEX != null && listSubjectBalanceEX.size() > 0) {
            // 变量.溢折价科目余额 = 科目余额
            varSpilledPriceAccBal = listSubjectBalanceEX.get(0).getBalance();
        }

        // 2.6.应计利息余额
        List<SubjectBalance> listSubjectBalance = new ArrayList<SubjectBalance>();
        if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(acctEntityEx.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(
            acctEntityEx.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(
            acctEntityEx.getInvAim())) {
            searchDao.setBaseSubjectNatureList(new ArrayList<String>(
                Arrays.asList(DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue())));
            listSubjectBalance = accountingCommonService.getSubjectBalanceExpectDirection(
                searchDao, DictCodeList.余额方向_借方.getValue());
        }

        if (listSubjectBalance != null && listSubjectBalance.size() > 0) {
            // 变量.应计利息余额 = 科目余额
            varInterestAccBal = listSubjectBalance.get(0).getBalance();
        }

        // 2.7 T+1相关发生额统计
        // 买入场景
        String tableId = null;
        List<String> baseSubjectNatureList = new ArrayList<String>();
        if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(acctEntity.getInvAim())) {
            tableId = DictCodeListTableId.会计场景代码_公允价值损益类债券_买入.getValue();
            baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue());
            baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue());
            if(isAcFlag){
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.公允价值损益类_利息调整.getValue());
            }
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
        }
        // 调用账务共通设计->【凭证最新账务查询】
        List<BookLatestInfoDto> listBookLatestInfoDto = accountingCommonService.getBookLatestInfo(
            acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntity.getBokDetailId(),
            baseSubjectNatureList, tableId, null, null, calcDate, null, null);

        // 变量.当日计算成本科目数量 = 变量.当日成本科目数量
        BigDecimal varDrjscbKmsl = varNumAmtHappenDate;
        // 变量.当日计算成本科目余额 = 变量.当日成本科目余额
        BigDecimal varDrjscbKmye = varOAmtHappenDate;
        // 变量.当日计算摊销溢折价科目余额 = 变量.溢折价科目余额
        BigDecimal varDrjstxzyjKmye = varSpilledPriceAccBal;
        // 变量.当日计算应计利息余额 = 变量.应计利息余额
        BigDecimal varDrjsyjlxKmye = varInterestAccBal;

        // LOOP 变量.买入账务明细列表 -> 变量.账务明细
        if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
            for (BookLatestInfoDto var : listBookLatestInfoDto) {
                // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                if (DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue().equals(
                    var.getBaseSubjectNature())
                    || DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue().equals(
                        var.getBaseSubjectNature())
                    || DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue().equals(
                        var.getBaseSubjectNature())) {
                    // 变量.当日计算成本科目数量 -= 变量.账务明细.发生数量
                    varDrjscbKmsl = ArithmeticUtil.subtract(varDrjscbKmsl, var.getHappenNumber());
                    // 变量.当日计算成本科目余额 -= 变量.账务明细.原币发生额
                    varDrjscbKmye = ArithmeticUtil.subtract(varDrjscbKmye, var.getoHappenAmt());
                }
                // if(变量.账务明细.基础科目性质 = XX利息调整的账务明细 )
                if (DictCodeBaseSubjectNatrueNew.摊余成本类_利息调整.getValue().equals(
                    var.getBaseSubjectNature())
                    || DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue().equals(
                        var.getBaseSubjectNature())
                    || (isAcFlag && DictCodeBaseSubjectNatrueNew.公允价值损益类_利息调整.getValue().equals(
                        var.getBaseSubjectNature()))) {
                    // 变量.账务明细.借贷方向为借时
                    if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                        // 变量.当日计算摊销溢折价科目余额 -= 变量.账务明细.原币发生额
                        varDrjstxzyjKmye = ArithmeticUtil.subtract(varDrjstxzyjKmye,
                            var.getoHappenAmt());
                    }
                    else {
                        // 变量.当日计算摊销溢折价科目余额 += 变量.账务明细.原币发生额
                        varDrjstxzyjKmye = ArithmeticUtil.doAdd(varDrjstxzyjKmye,
                            var.getoHappenAmt());
                    }
                }
                // if(变量.账务明细.基础科目性质 = XX应计利息的账务明细 )
                if (DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue().equals(
                    var.getBaseSubjectNature())
                    || DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue().equals(
                        var.getBaseSubjectNature())
                    || DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue().equals(
                        var.getBaseSubjectNature())) {
                    // 变量.账务明细.借贷方向为借时
                    if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                        // 变量.当日计算应计利息余额 -= 变量.账务明细.原币发生额
                        varDrjsyjlxKmye = ArithmeticUtil.subtract(varDrjsyjlxKmye,
                            var.getoHappenAmt());
                    }
                    else {
                        // 变量.当日计算应计利息余额 += 变量.账务明细.原币发生额
                        varDrjsyjlxKmye = ArithmeticUtil.doAdd(varDrjsyjlxKmye,
                            var.getoHappenAmt());
                    }
                }
            }
        }
        // 卖出场景
        if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(acctEntity.getInvAim())) {
            tableId = DictCodeListTableId.会计场景代码_公允价值损益类债券_卖出.getValue();
        }
        else if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(acctEntity.getInvAim())) {
            tableId = DictCodeListTableId.会计场景代码_摊余成本类债券_卖出.getValue();
        }
        else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(
            acctEntity.getInvAim())) {
            tableId = DictCodeListTableId.会计场景代码_综合收益类债券_卖出.getValue();
        }
        // 调用账务共通设计->【凭证最新账务查询】
        listBookLatestInfoDto = accountingCommonService.getBookLatestInfo(
            acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntity.getBokDetailId(),
            baseSubjectNatureList, tableId, null, null, calcDate, null, null);
        // LOOP 变量.卖出账务明细列表 -> 变量.账务明细
        if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
            for (BookLatestInfoDto var : listBookLatestInfoDto) {
                // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                if (DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue().equals(
                    var.getBaseSubjectNature())
                    || DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue().equals(
                        var.getBaseSubjectNature())
                    || DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue().equals(
                        var.getBaseSubjectNature())) {
                    // 变量.当日计算成本科目数量 += 变量.账务明细.发生数量
                    varDrjscbKmsl = ArithmeticUtil.doAdd(varDrjscbKmsl, var.getHappenNumber());
                    // 变量.当日计算成本科目余额 += 变量.账务明细.原币发生额
                    varDrjscbKmye = ArithmeticUtil.doAdd(varDrjscbKmye, var.getoHappenAmt());
                }
                // if(变量.账务明细.基础科目性质 = XX利息调整的账务明细 )
                if (DictCodeBaseSubjectNatrueNew.摊余成本类_利息调整.getValue().equals(
                    var.getBaseSubjectNature())
                    || DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue().equals(
                        var.getBaseSubjectNature())
                    || (isAcFlag &&DictCodeBaseSubjectNatrueNew.公允价值损益类_利息调整.getValue().equals(
                    var.getBaseSubjectNature()))) {
                    // 变量.账务明细.借贷方向为借时
                    if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                        // 变量.当日计算摊销溢折价科目余额 -= 变量.账务明细.原币发生额
                        varDrjstxzyjKmye = ArithmeticUtil.subtract(varDrjstxzyjKmye,
                            var.getoHappenAmt());
                    }
                    else {
                        // 变量.当日计算摊销溢折价科目余额 += 变量.账务明细.原币发生额
                        varDrjstxzyjKmye = ArithmeticUtil.doAdd(varDrjstxzyjKmye,
                            var.getoHappenAmt());
                    }
                }
                // if(变量.账务明细.基础科目性质 = XX应计利息的账务明细 )
                if (DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue().equals(
                    var.getBaseSubjectNature())
                    || DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue().equals(
                        var.getBaseSubjectNature())
                    || DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue().equals(
                        var.getBaseSubjectNature())) {
                    // 变量.账务明细.借贷方向为借时
                    if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                        // 变量.当日计算应计利息余额 -= 变量.账务明细.原币发生额
                        varDrjsyjlxKmye = ArithmeticUtil.subtract(varDrjsyjlxKmye,
                            var.getoHappenAmt());
                    }
                    else {
                        // 变量.当日计算应计利息余额 += 变量.账务明细.原币发生额
                        varDrjsyjlxKmye = ArithmeticUtil.doAdd(varDrjsyjlxKmye,
                            var.getoHappenAmt());
                    }
                }
            }
        }
        // 面值
        dto.setFaceValue(varFaceValue);
        // 成本
        dto.setCb(varOAmtHappenDate);
        // 数量
        dto.setNum(varNumAmtHappenDate);
        // 应计利息
        dto.setInterest(varInterestAccBal);
        // 折溢价
        dto.setSpilledPrice(varSpilledPriceAccBal);
        // 成本
        dto.setCbT1(varDrjscbKmye);
        // 数量
        dto.setNumT1(varDrjscbKmsl);
        // 应计利息
        dto.setInterestT1(varDrjsyjlxKmye);
        // 折溢价
        dto.setSpilledPriceT1(varDrjstxzyjKmye);
        return dto;
    }

    /**
     * 获取当前账务明细代码摊销收益率最新日期
     * 
     * @param acctEntity
     * @return
     * @see
     * @since 1.0
     */
//    private Date getSecDscYieldCalcDay(DrawBaseInfoSelectComResult acctEntity,
//                                       List<TargetCashCalcRule> listCashinfo) {
//
//        // 调用账务共通设计->【凭证最新账务查询】
//        // 买入场景
//        String tableId = null;
//        List<String> baseSubjectNatureList = new ArrayList<String>();
//        if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(acctEntity.getInvAim())) {
//            tableId = DictCodeListTableId.会计场景代码_公允价值损益类债券_买入.getValue();
//        }
//        else if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(acctEntity.getInvAim())) {
//            tableId = DictCodeListTableId.会计场景代码_摊余成本类债券_买入.getValue();
//            baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue());
//        }
//        else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(
//            acctEntity.getInvAim())) {
//            tableId = DictCodeListTableId.会计场景代码_综合收益类债券_买入.getValue();
//            baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue());
//        }
//        List<BookLatestInfoDto> listBookLatestInfoDto = accountingCommonService.getBookLatestInfo(
//            acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntity.getBokDetailId(),
//            baseSubjectNatureList, tableId, null, null, null, null, null);
//
//        Date earlyDate = acctEntity.getHappenDate();
//        if (listBookLatestInfoDto != null && !listBookLatestInfoDto.isEmpty()) {
//            if (listBookLatestInfoDto.get(0).getOnwayDate() != null
//                && listBookLatestInfoDto.get(0).getOnwayDate().compareTo(
//                    acctEntity.getHappenDate()) <= 0) {
//                earlyDate = listBookLatestInfoDto.get(0).getOnwayDate();
//            }
//            else {
//                earlyDate = listBookLatestInfoDto.get(0).getHappenDate();
//            }
//            for (int i = 0; i < listBookLatestInfoDto.size(); i++ ) {
//                if (listBookLatestInfoDto.get(i).getOnwayDate() != null) {
//                    Date itemOnwayDate = listBookLatestInfoDto.get(i).getOnwayDate();
//                    if (itemOnwayDate.compareTo(acctEntity.getHappenDate()) <= 0
//                        && earlyDate.compareTo(itemOnwayDate) < 0) {
//                        earlyDate = itemOnwayDate;
//                    }
//                }
//                else {
//                    Date itemHappenDate = listBookLatestInfoDto.get(i).getHappenDate();
//                    if (earlyDate.compareTo(itemHappenDate) < 0) {
//                        earlyDate = itemHappenDate;
//                    }
//                }
//            }
//        }
//        if (listCashinfo != null && listCashinfo.size() > 0) {
//            for (TargetCashCalcRule targetCashCalcRule : listCashinfo) {
//                // 报价.生效日 > 计提类账务处理结果.发生日
//                if (earlyDate.compareTo(targetCashCalcRule.getEffDate()) < 0
//                    && targetCashCalcRule.getEffDate().compareTo(
//                        acctEntity.getHappenDate()) <= 0) {
//                    earlyDate = targetCashCalcRule.getEffDate();
//                }
//            }
//        }
//        return earlyDate;
//    }

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
     * 计算债券的利率
     * @param targetFinProduct 标的信息
     * @param interestPayList 付息计划
     * @param payRuleList 支付规则列表
     * @param quotationList 现金流计算规则
     * @param happenDate    计算日
     * @return
     */
    private BigDecimal calSecInterest(TargetFinProduct targetFinProduct, List<TargetCashPlan> interestPayList, List<TargetCashPayRule> payRuleList, List<TargetCashCalcRule> quotationList, Date happenDate) {
        //获取小于等于计算日得报价
        TargetCashCalcRule targetCashCalcRuleDto = null;
        //默认取最新的一条
        int num = quotationList.size() - 1;
        for(int i = 0; i < quotationList.size(); i++){
            TargetCashCalcRule targetCashCalcRule = quotationList.get(i);
            if(targetCashCalcRule.getEffDate().compareTo(happenDate) > 0){
                num = i - 1;
                break;
            }
        }
        targetCashCalcRuleDto = quotationList.get(num);

        BigDecimal interest = BigDecimal.ZERO;
        //取计算日所在得付息计划
        TargetCashPlan cashPlan = null;
        if (null != interestPayList) {
            for (TargetCashPlan sec : interestPayList) {
                if (BasicUtil.dateBefore(BasicUtil.dateToLocal(happenDate),BasicUtil.dateToLocal(sec.getMdate()))
                        && BasicUtil.dateAfter(BasicUtil.dateToLocal(happenDate), BasicUtil.dateToLocal(sec.getVdate()))) {
                    cashPlan = sec;
                }
            }
        }

        //取最新得支付规则
        TargetCashPayRule targetCashPayRule = null;
        if(!payRuleList.isEmpty()){
            targetCashPayRule = payRuleList.get(0);
        }

        //处理计息基础
        String basisString = targetCashCalcRuleDto.getBasis();
        // 计息基础
        Basis basis = null;
        //将计息基础转为算法可识别得计息基础
        basis = getBasis(basisString);

        // 根据计息基础确定利息分配方式
        AllocationType allocationType = null;
        if (DictCodeList.计息基础_A_A_平均分配.getValue().equals(basisString)) {
            allocationType = AllocationType.平均分配;
        }
        else if (DictCodeList.计息基础_A_A_按实际天数分配.getValue().equals(basisString)) {
            allocationType = AllocationType.按实际天数分配;
        }
        else {
            allocationType = AllocationType.按实际天数分配;
        }
        
        if (DictCodeList.息票品种_贴现.getValue().equals(targetFinProduct.getCouponSpecies())
        		|| DictCodeList.息票品种_零息.getValue().equals(targetFinProduct.getCouponSpecies())) {
        	AccurIntForDisInputParameter parameter= new AccurIntForDisInputParameter();
        	parameter.setIssuePrice(targetFinProduct.getIssuePrice());
        	parameter.setStartDate(BasicUtil.dateToLocal(targetFinProduct.getVdate()));
        	parameter.setEndDate(BasicUtil.dateToLocal(targetFinProduct.getMdate()));
        	parameter.setCalDate(BasicUtil.dateToLocal(happenDate).plusDays(1));
        	
        	AccurIntForDisCalculator calculator = new AccurIntForDisCalculator();
        	AccurInterestOutputParameter accurOutput = calculator.calculate(parameter);
        	interest = ArithmeticUtil.divide(accurOutput.getInterest(), 
        			new BigDecimal(BasicUtil.days(parameter.getCalDate(),parameter.getStartDate())));
        }
        else{
        	// 对于非A/A的计息基础
            if(!Basis.A_A.equals(basis)) {
                // 计息基础计算单位利率单位应计利息
                BasisIntCalculator basisCal = CalculatorFactory.createBasisIntCalculator();
                BasisIntInputParameter basisInput = new BasisIntInputParameter();
                basisInput.setCalDate(BasicUtil.dateToLocal(happenDate).plusDays(1));
                basisInput.setCalStartDate(BasicUtil.dateToLocal(happenDate));
                // 计算日所在付息计划
                if (Basis._30_360.equals(basis)) {
                    basisInput.setSecIntStartDate(BasicUtil.dateToLocal(cashPlan.getVdate()));
                }
                basisInput.setBasis(basis);
                // 利息分配方式
                basisInput.setAllocationType(allocationType);
                // 首次付息日
                Date firstPayDate = targetCashPayRule.getFirstPayDate();
                //推算方式
                SecIntSchCalMethod secIntSchCalMethod = null;

                String secIntSchCalMethodString = targetCashPayRule.getCalcRule();

                if (DictCodeList.推算方式_起息日向后.getValue().equals(secIntSchCalMethodString)) {
                    secIntSchCalMethod = SecIntSchCalMethod.起息日向后;
                }
                else if (DictCodeList.推算方式_到期日向前.getValue().equals(secIntSchCalMethodString)) {
                    secIntSchCalMethod = SecIntSchCalMethod.到期日向前;
                }
                else if (DictCodeList.推算方式_首次支付日.getValue().equals(secIntSchCalMethodString)) {
                    secIntSchCalMethod = SecIntSchCalMethod.首次付息日;
                }
                // 计息年度计算
                SecCalYearInputParameter secInput = new SecCalYearInputParameter();
                secInput.setCalDate(BasicUtil.dateToLocal(happenDate));
                secInput.setVariety(Variety.附息);
                secInput.setCalMethod(secIntSchCalMethod);
                // 标的基本资料.起息日 -> 债券起息日
                secInput.setStartDate(BasicUtil.dateToLocal(targetFinProduct.getVdate()));
                // 标的基本资料.到期日 -> 债券到期日
                secInput.setEndDate(BasicUtil.dateToLocal(targetFinProduct.getMdate()));
                // 标的基本资料.首次付息日-> 债券首次付息日
                secInput.setFirstPayDate(BasicUtil.dateToLocal(firstPayDate));
                // 计息年度天数计算
                SecCalYearDayCountOutputParameter yearDayCount = CalculatorFactory.createSecCalYearDayCountCalculator().calculate(secInput);
                // 计息年度天数
                basisInput.setYearTotalDays((int)yearDayCount.getDay());
                BasisIntOutputParameter basisOutput = basisCal.calculate(basisInput);
                interest = basisOutput.getInterest();
            }
            // A/A的计息基础
            else {
                // 首次付息日
                Date firstPayDate = targetCashPayRule.getFirstPayDate();
                //推算方式
                SecIntSchCalMethod secIntSchCalMethod = null;

                String secIntSchCalMethodString = targetCashPayRule.getCalcRule();

                if (DictCodeList.推算方式_起息日向后.getValue().equals(secIntSchCalMethodString)) {
                    secIntSchCalMethod = SecIntSchCalMethod.起息日向后;
                }
                else if (DictCodeList.推算方式_到期日向前.getValue().equals(secIntSchCalMethodString)) {
                    secIntSchCalMethod = SecIntSchCalMethod.到期日向前;
                }
                else if (DictCodeList.推算方式_首次支付日.getValue().equals(secIntSchCalMethodString)) {
                    secIntSchCalMethod = SecIntSchCalMethod.首次付息日;
                }

                //付息频率
                String yearIntRateString = targetCashPayRule.getPayCycle();
                //将付息频率转为算法可识别频率
                IntFrequency yearIntRate = null;
                if (DictCodeList.频率_月.getValue().equals(yearIntRateString)) {
                    yearIntRate = IntFrequency.月付;
                }
                else if (DictCodeList.频率_季.getValue().equals(yearIntRateString)) {
                    yearIntRate = IntFrequency.季付;
                }
                else if (DictCodeList.频率_年.getValue().equals(yearIntRateString)) {
                    yearIntRate = IntFrequency.年付;
                }
                else if (DictCodeList.频率_半年.getValue().equals(yearIntRateString)) {
                    yearIntRate = IntFrequency.半年付;
                }
                else {
                    yearIntRate = IntFrequency.年付;
                }

                //区间理论起始日
                LocalDate theoryStartDate =
                        cashPlan.getVdateTerm() == null ? BasicUtil.dateToLocal(
                                cashPlan.getVdate()) : BasicUtil.dateToLocal(
                                cashPlan.getVdateTerm());
                //区间理论结束日
                LocalDate theoryCutoffDate =
                        cashPlan.getMdateTerm() == null ? BasicUtil.dateToLocal(
                                cashPlan.getMdate()) : BasicUtil.dateToLocal(
                                cashPlan.getMdateTerm());

                // 计息年度计算
                SecCalYearInputParameter secInput = new SecCalYearInputParameter();
                secInput.setCalDate(BasicUtil.dateToLocal(happenDate));
                secInput.setVariety(Variety.附息);
                secInput.setCalMethod(secIntSchCalMethod);
                // 标的基本资料.起息日 -> 债券起息日
                secInput.setStartDate(BasicUtil.dateToLocal(targetFinProduct.getVdate()));
                // 标的基本资料.到期日 -> 债券到期日
                secInput.setEndDate(BasicUtil.dateToLocal(targetFinProduct.getMdate()));
                // 标的基本资料.首次付息日-> 债券首次付息日
                secInput.setFirstPayDate(BasicUtil.dateToLocal(firstPayDate));
                // 计息年度天数计算
                SecCalYearDayCountOutputParameter yearDayCount = CalculatorFactory.createSecCalYearDayCountCalculator().calculate(secInput);
                // 计息基础
                BasisIntCalculator basisCal = CalculatorFactory.createBasisIntCalculator();
                BasisIntInputParameter basisInput = new BasisIntInputParameter();
                // 计算日
                basisInput.setCalDate(BasicUtil.dateToLocal(happenDate).plusDays(1));
                // 计算起始日
                basisInput.setCalStartDate(BasicUtil.dateToLocal(happenDate));
                // 计息基础
                basisInput.setBasis(basis);
                // 付息频率
                basisInput.setYearIntRate(yearIntRate);
                // 利息分配方式
                basisInput.setAllocationType(allocationType);
                // 计息年度天数
                basisInput.setYearTotalDays((int)yearDayCount.getDay());
                // 理论计息周期天数
                basisInput.setCycleDays(BasicUtil.days(theoryCutoffDate, theoryStartDate));
                BasisIntOutputParameter basisOutput = basisCal.calculate(basisInput);
                interest = basisOutput.getInterest();
            }
        }
        String SEC_INT_SCALE_BANK = CacheUtil.getSysParam("SEC_INT_SCALE_BANK");
        //交易所债券在计算应计利息时的舍位位数
        String SEC_INT_SCALE_EXCHANGE = CacheUtil.getSysParam("SEC_INT_SCALE_EXCHANGE");
        //控制是先计算利息还是先对应计利息进行舍位：Y-先舍位；N-先计算
        String SEC_INT_SCALE = CacheUtil.getSysParam("SEC_INT_SCALE");
        if(SEC_INT_SCALE==null||SEC_INT_SCALE.equals(DictCodeList.是否_是.getValue())){
            // if（标的信息.交易场所 = "银行间"）
            if (DictCodeListExt1.估值交易市场_银行间.getValue().equals(targetFinProduct.getTradeMarket())) {
                // 调用金额共通方法设置有效小数位,小数位数取值共通参数 SEC_INT_SCALE_BANK
                interest = ArithmeticUtil.round(interest,
                        Integer.parseInt(StringUtils.isEmpty(SEC_INT_SCALE_BANK) ? "12" : SEC_INT_SCALE_BANK),null);
            }
            /**add by taoran 20200110 start **/
            // else if（标的信息.交易场所 = "上交所" or "深交所"）
            else if (DictCodeListExt1.估值交易市场_上交所.getValue().equals(targetFinProduct.getTradeMarket()) ||
                    DictCodeListExt1.估值交易市场_深交所.getValue().equals(targetFinProduct.getTradeMarket())) {
                // 调用金额共通方法设置有效小数位,小数位数取值共通参数 SEC_INT_SCALE_EXCHANGE
                interest = ArithmeticUtil.round(interest,
                        Integer.parseInt(StringUtils.isEmpty(SEC_INT_SCALE_EXCHANGE) ? "8" : SEC_INT_SCALE_EXCHANGE),null);
            }
            else {
                // 调用金额共通方法设置有效小数位,小数位数取值共通参数 SEC_INT_SCALE_BANK
                interest = ArithmeticUtil.round(interest,
                        Integer.parseInt(StringUtils.isEmpty(SEC_INT_SCALE_BANK) ? "12" : SEC_INT_SCALE_BANK),null);
            }
        }
        return ArithmeticUtil.multiply(interest, targetCashCalcRuleDto.getYield());
    }

    /**
     * 将计息基础 转为 算法能识别得计息基础
     * @param basisString
     * @return
     */
    private Basis getBasis(String basisString) {
        Basis basis = null;
        if (DictCodeList.计息基础_A_360.getValue().equals(basisString)) {
            basis = Basis.A_360;
        } else if (DictCodeList.计息基础_A_365.getValue().equals(basisString)) {
            basis = Basis.A_365;
        } else if (DictCodeList.计息基础_A_A_平均分配.getValue().equals(basisString)
                || DictCodeList.计息基础_A_A_按实际天数分配.getValue().equals(basisString)) {
            basis = Basis.A_A;
        } else if (DictCodeList.计息基础_A_365F.getValue().equals(basisString)) {
            basis = Basis.A_365F;
        } else if (DictCodeList.计息基础_30_360.getValue().equals(basisString)) {
            basis = Basis._30_360;
        } else if (DictCodeListExt1.计息基础_A_A_D.getValue().equals(basisString)) {
            basis = Basis.A_A_D;
        }
        /**ADD BY WUJIE ON 20200208 START**/
        else if (DictCodeListExt3.计息基础_30U_360.getValue().equals(basisString)) {
            basis = Basis._30U_360;
        }
        /**ADD BY WUJIE ON 20200208 END**/
        return basis;
    }
}
