/*
 * 文件名：DeltRightFairValeAlterationsProcessorService.java
 * 版权：Copyright 2020-2021 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V2.1.4
 * 修改人：wujie
 * 修改时间：2020年10月15日
 * 修改内容：新建
 * 系统名称：估值核算系统V2.1.4
 */

package com.joyintech.tams.valuation.batch.deltrightfairvalealterations.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.common.base.entity.BokAssetMarketInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokBatchMarketwarningInfo;
import com.joyintech.tams.common.base.entity.BokBooksetExt;
import com.joyintech.tams.common.base.entity.BokStatDetPosiiton;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.BookLatestInfoDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookAccountGenLogic;
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
import com.joyintech.tams.valuation.common.batch.finproduct.entity.CurrencyMarketDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.MarketPriceDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetFinProduct;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.DataIndexSequenceType;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import com.joyintech.tams.valuation.common.utils.DBUtil;

/**
 * 债权类资产公允价值批处理的业务处理
 * 
 * @author wujie
 * @version 1.0 2020年10月15日
 * @see DeltRightFairValeAlterationsProcessorService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class DeltRightFairValeAlterationsProcessorService {

    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogic bookLogic;

    /**
     * 记账引擎
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * 标的类共通方法类
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 账务类共通方法类
     */
    @Autowired
    private AccountingCommonService accountingCommonService;

    /**
     * DBUtil
     */
    @Autowired
    private DBUtil dBUtil;
    
    @Autowired
    private CalculationCommonService calculationCommonService;//add by wujie on 20201119 remark:公允价值变动本位币处理改造

    /**
     * 债权类资产公允价值批处理的业务处理
     * 
     * @param drawBaseInfoSelectComResult 计提基础信息取得结果DTO
     * @param param1 参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    public BookGenerateResultWithBokSummary execute(DrawBaseInfoSelectComResult drawBaseInfoSelectComResult,
                                                    Map<String, Object> param1) {
		// 定义变量
        BookGenerateResultWithBokSummary rtnResult = new BookGenerateResultWithBokSummary();
        // 账务明细若为空赋初始值
        if (rtnResult.getValBokBookdetailBatch() == null) {
            rtnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
        }
        List<DrawBaseInfoSelectComDTO> acctEntityList = drawBaseInfoSelectComResult.getListDrawBaseInfoSelectComDTO();
        // 没有明细
        if (acctEntityList == null || acctEntityList.size() == 0) {
            return null;
        }
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)param1.get("BATCHINFO");     
        if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(
            drawBaseInfoSelectComResult.getInvAim())) {
            return null;
        }

        // 汇总信息查询
        // 变量.统计分析账务明细汇总信息 = 调用共通方法[账务类共通方法设计 - 统计分析账务明细汇总查询]
        BokStatDetPosiiton bokSummaryTableYestdy = accountingCommonService.getCountAnalysis(
            drawBaseInfoSelectComResult.getHappenDate(),
            drawBaseInfoSelectComResult.getHappenDate(), drawBaseInfoSelectComResult.getBookDate(),
            drawBaseInfoSelectComResult.getBooksetId(),
            drawBaseInfoSelectComResult.getBokDetailId(), null, null,null);
        
        
         // 账套核算参数查询
        // 定义账套参数查询参数项list
        List<String> paramCodeList = new ArrayList<String>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_债权类资产公允价值变动计算过程小数位.getValue());// 债券交易费用损益计入方式
        Map<String, Object> getValBokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(
        		drawBaseInfoSelectComResult.getBooksetId(), paramCodeList);
        Integer 债权类资产公允价值变动计算过程小数位 = null;
        if(getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_债权类资产公允价值变动计算过程小数位.getValue())!=null){
        	债权类资产公允价值变动计算过程小数位 = Integer.valueOf((String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_债权类资产公允价值变动计算过程小数位.getValue()));
        }
        

        for (DrawBaseInfoSelectComDTO acctEntityEx : acctEntityList) {
            // 成本科目余额
            BigDecimal cbkmBalancce = BigDecimal.ZERO;
            // 成本科目数量
            BigDecimal cbkmNum = BigDecimal.ZERO;
            // 公允价值变动科目余额
            BigDecimal secFairValeAltBalance = BigDecimal.ZERO;
            
            // 溢折价科目余额
            BigDecimal spilledPriceBalance = BigDecimal.ZERO;
            // 原币中
            String oCcy = null;
            // 市价
            BigDecimal marketPrice = BigDecimal.ZERO;
            // 公允价值变动
            BigDecimal secFairValeAlt = BigDecimal.ZERO;
            
            /**add by wujie on 20201119 start remark:公允价值变动本位币处理改造**/
            // 公允价值变动科目余额_本位币
            BigDecimal secFairValeAltBalance_bccy = BigDecimal.ZERO;
            
            // 公允价值变动_本位币
            BigDecimal secFairValeAlt_bccy = BigDecimal.ZERO;
            
            /**add by wujie on 20201119 end remark:公允价值变动本位币处理改造**/
            
            // 估值日期
            Date valDate = null;

            // 防止报空指针异常
            if (rtnResult.getValBokBookdetailBatch() == null) {
                rtnResult.setValBokBookdetailBatch(new ArrayList<ValBokBookdetailBatch>());
            }

            // 处理结果中取出公用属性
            // 发生日
            Date happenDate = drawBaseInfoSelectComResult.getHappenDate();
            // 入账日
            Date bookDate = drawBaseInfoSelectComResult.getBookDate();
            // 账套代码
            String booksetId = drawBaseInfoSelectComResult.getBooksetId();
            // 金融产品代码
            String bokDetailId = drawBaseInfoSelectComResult.getBokDetailId();
            // 投资目的
            String invAim = drawBaseInfoSelectComResult.getInvAim();
            
            TreeNode bookSetTreeNodNow = drawBaseInfoSelectComResult.getBookSetTreeNode();// 会计场景

            // 调用共通方法【市价查询】取债券最新市价
            MarketPriceDto marketPriceDto = finProductCommonService.gettDeltRightMarketPrice(happenDate, booksetId,
                drawBaseInfoSelectComResult.getBusiId(), invAim);
            // 市价 = 返回值.市价
            marketPrice = marketPriceDto.getPrice();
            // 估值日期 = 返回值.估值日期
            valDate = marketPriceDto.getValDate();

            String baseSubjectNature = null;
            Long taskPri = acctEntityEx.getTaskPri();
            String expBalanceDirect = DictCodeList.余额方向_借方.getValue();
            // 调用共通方法【科目余额查询-期待方向】,获取成本类科目余额
            if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(invAim)) {
                baseSubjectNature = DictCodeBaseSubjectNatrue.计划类资产_成本.getValue();
            }
            else if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(invAim)) {
                baseSubjectNature = DictCodeBaseSubjectNatrue.公允价值类计划类收益型资产_成本.getValue();
            }
            else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(invAim)) {
                baseSubjectNature = DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_成本.getValue();
            }
            SubjectBalance subjectBalance = this.getSubjectBalanceExpectDirection(booksetId,
                bokDetailId, happenDate, baseSubjectNature, expBalanceDirect, taskPri,batchExecuteInfo.getBatchExedetailSeqNo());
            cbkmBalancce = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
            cbkmNum = subjectBalance.getNum() == null ? BigDecimal.ZERO : subjectBalance.getNum();
            oCcy = subjectBalance.getOCcy();
            

            // 调用共通方法【科目余额查询-期待方向】,获取公允价值变动类科目余额
            if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(invAim)) {
                baseSubjectNature = DictCodeBaseSubjectNatrue.公允价值类计划类收益型资产_公允价值变动.getValue();
                subjectBalance = this.getSubjectBalanceExpectDirection(booksetId, bokDetailId,
                    happenDate, baseSubjectNature, expBalanceDirect, taskPri,batchExecuteInfo.getBatchExedetailSeqNo());
                secFairValeAltBalance = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
                secFairValeAltBalance_bccy = subjectBalance.getbAmt() == null ? BigDecimal.ZERO : subjectBalance.getbAmt();//add by wujie on 20201119 remark:公允价值变动本位币处理改造
            }
            else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(invAim)) {
                baseSubjectNature = DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_公允价值变动.getValue();
                subjectBalance = this.getSubjectBalanceExpectDirection(booksetId, bokDetailId,
                    happenDate, baseSubjectNature, expBalanceDirect, taskPri,batchExecuteInfo.getBatchExedetailSeqNo());
                secFairValeAltBalance = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
                secFairValeAltBalance_bccy = subjectBalance.getbAmt() == null ? BigDecimal.ZERO : subjectBalance.getbAmt();//add by wujie on 20201119 remark:公允价值变动本位币处理改造
            }

            // 调用共通方法【科目余额查询-期待方向】,获取溢折价科目余额
            if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(invAim)) {
                baseSubjectNature = DictCodeBaseSubjectNatrue.计划类资产_利息调整.getValue();
                subjectBalance = this.getSubjectBalanceExpectDirection(booksetId, bokDetailId,
                    happenDate, baseSubjectNature, expBalanceDirect, taskPri,batchExecuteInfo.getBatchExedetailSeqNo());
                spilledPriceBalance = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
            }
            else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(invAim)) {
                baseSubjectNature = DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_利息调整.getValue();
                subjectBalance = this.getSubjectBalanceExpectDirection(booksetId, bokDetailId,
                    happenDate, baseSubjectNature, expBalanceDirect, taskPri,batchExecuteInfo.getBatchExedetailSeqNo());
                spilledPriceBalance = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
            }

            /** 资产标的信息查询增加其他债券分录配置需要做区分，故从下面代码移上来 start**/
            // 查询条件
            List<TargetSearchDto> queryCriteria = new ArrayList<TargetSearchDto>();
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
            /** 资产标的信息查询增加其他债券分录配置需要做区分，故从下面代码移上来 end**/
            // 2.4.应计利息类科目余额
            if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(invAim)) {
                baseSubjectNature = DictCodeBaseSubjectNatrue.应计利息.getValue();
            }
            else if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(invAim)) {
                baseSubjectNature = DictCodeBaseSubjectNatrue.公允价值类类计划类收益型资产_应计利息.getValue();
            }
            else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(invAim)) {
                baseSubjectNature = DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_应计利息.getValue();
            }
            subjectBalance = this.getSubjectBalanceExpectDirection(booksetId, bokDetailId,
                happenDate, baseSubjectNature, expBalanceDirect, taskPri,batchExecuteInfo.getBatchExedetailSeqNo());
            
            // 变量.应计利息科目余额 = 科目余额
            BigDecimal varYjlxKmye = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();

            // 2.7 T+1相关发生额统计
            // 变量.当日计算成本科目数量 = 变量.当日成本科目数量
            BigDecimal varDrjscbKmsl = cbkmNum;
            // 变量.当日计算成本科目余额 = 变量.当日成本科目余额
            BigDecimal varDrjscbKmye = cbkmBalancce;
            // 变量.当日计算摊销溢折价科目余额 = 变量.溢折价科目余额
            BigDecimal varDrjstxzyjKmye = spilledPriceBalance;
            // 变量.当日计算应计利息科目余额 = 变量.应计利息科目余额
            BigDecimal varDrjsyjlxKmye = varYjlxKmye;
            // 变量.当日计算公允价值变动科目余额 = 变量.公允价值变动科目余额
            BigDecimal varDrjsgyjzbdKmye = secFairValeAltBalance;
            
            // 变量.当日计算公允价值变动科目余额 _本位币= 变量.公允价值变动科目余额 _本位币
            BigDecimal varDrjsgyjzbdKmye_bccy = secFairValeAltBalance_bccy;//add by wujie on 20201119 remark:公允价值变动本位币处理改造
            
            // 调用账务类共通【账套参数查询】
            String gyjzbdJtlx = DictCodeListExt1.公允价值变动计提日类型_交易日.getValue();
            List<BokBooksetExt> listBokBooksetExt = accountingCommonService.getValBokBooksetExt(
                booksetId, new ArrayList<String>(Arrays.asList(DictCode.公允价值变动计提日类型.getValue())));
            if (listBokBooksetExt != null && listBokBooksetExt.size() > 0) {
                gyjzbdJtlx = listBokBooksetExt.get(0).getParamValue();
            }
            if (DictCodeListExt1.公允价值变动计提日类型_交割日.getValue().equals(gyjzbdJtlx)) {
                // 买入场景
                String tableId = null;
                List<String> baseSubjectNatureList = new ArrayList<String>();
                if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(invAim)) {
                    /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
                    //如果为其他债券
                    if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                        tableId = DictCodeListTableId.会计场景代码_公允价值变动类其他债权投资_买入.getValue();
                    }
                    else {
                        tableId = DictCodeListTableId.会计场景代码_公允价值变动类收益型资产_买入.getValue();
                    }
                    /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.公允价值类计划类收益型资产_成本.getValue());
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.公允价值类类计划类收益型资产_应计利息.getValue());
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.公允价值类计划类收益型资产_公允价值变动.getValue());
                }
                else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(invAim)) {
                    /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
                    //如果为其他债券
                    if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                        tableId = DictCodeListTableId.会计场景代码_综合收益类其他债权投资_买入.getValue();
                    }
                    else {
                        tableId = DictCodeListTableId.会计场景代码_综合收益类收益型资产_买入.getValue();
                    }
                    /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_成本.getValue());
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_利息调整.getValue());
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_应计利息.getValue());
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_公允价值变动.getValue());
                }
                // 调用账务共通设计->【凭证最新账务查询】
                List<BookLatestInfoDto> listBookLatestInfoDto = accountingCommonService.getBookLatestInfo(
                    booksetId, bookDate, bokDetailId, baseSubjectNatureList, tableId, null, null,
                    happenDate, null, null);

                // LOOP 变量.买入账务明细列表 -> 变量.账务明细
                if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                    for (BookLatestInfoDto var : listBookLatestInfoDto) {
                        // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                        if (DictCodeBaseSubjectNatrue.公允价值类计划类收益型资产_成本.getValue().equals(
                            var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrue.计划类资产_成本.getValue().equals(
                                var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_成本.getValue().equals(
                                var.getBaseSubjectNature())) {
                            // 变量.当日计算成本科目数量 -= 变量.账务明细.发生数量
                            varDrjscbKmsl = ArithmeticUtil.subtract(varDrjscbKmsl,
                                var.getHappenNumber());
                            // 变量.当日计算成本科目余额 -= 变量.账务明细.原币发生额
                            varDrjscbKmye = ArithmeticUtil.subtract(varDrjscbKmye,
                                var.getoHappenAmt());
                        }
                        // if(变量.账务明细.基础科目性质 = XX利息调整的账务明细 )
                        if (DictCodeBaseSubjectNatrue.计划类资产_利息调整.getValue().equals(
                            var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_利息调整.getValue().equals(
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
                        if (DictCodeBaseSubjectNatrue.公允价值类类计划类收益型资产_应计利息.getValue().equals(
                            var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrue.应计利息.getValue().equals(
                                var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_应计利息.getValue().equals(
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
                        // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                        if (DictCodeBaseSubjectNatrue.公允价值类计划类收益型资产_公允价值变动.getValue().equals(
                            var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_公允价值变动.getValue().equals(
                                var.getBaseSubjectNature())) {
                            // 变量.账务明细.借贷方向为借时
                            if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                                // 变量.当日计算公允价值变动科目余额 -= 变量.账务明细.原币发生额
                                varDrjsgyjzbdKmye = ArithmeticUtil.subtract(varDrjsgyjzbdKmye,
                                    var.getoHappenAmt());
                                
                                /**add by wujie on 20201119 start remark:公允价值变动本位币处理改造**/
                                // 变量.当日计算公允价值变动科目余额_本位币 -= 变量.账务明细.本币发生额
                                varDrjsgyjzbdKmye_bccy = ArithmeticUtil.subtract(varDrjsgyjzbdKmye_bccy,
                                    var.getbHappenAmt());
                                /**add by wujie on 20201119 end remark:公允价值变动本位币处理改造**/
                                
                            }
                            else {
                                // 变量.当日计算公允价值变动科目余额 += 变量.账务明细.原币发生额
                                varDrjsgyjzbdKmye = ArithmeticUtil.doAdd(varDrjsgyjzbdKmye,
                                    var.getoHappenAmt());
                                
                                /**add by wujie on 20201119 start remark:公允价值变动本位币处理改造**/
                                // 变量.当日计算公允价值变动科目余额_本位币 += 变量.账务明细.本币发生额
                                varDrjsgyjzbdKmye_bccy = ArithmeticUtil.doAdd(varDrjsgyjzbdKmye_bccy,
                                    var.getbHappenAmt());
                                /**add by wujie on 20201119 end remark:公允价值变动本位币处理改造**/
                            }
                        }
                    }
                }

                // 卖出场景
                if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(invAim)) {
                    /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
                    //如果为其他债券
                    if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                        tableId = DictCodeListTableId.会计场景代码_公允价值变动类其他债权投资_卖出.getValue();
                    }
                    else {
                        tableId = DictCodeListTableId.会计场景代码_公允价值变动类收益型资产_卖出.getValue();
                    }
                    /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
                }
                else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(invAim)) {
                    /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
                    //如果为其他债券
                    if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                        tableId = DictCodeListTableId.会计场景代码_综合收益类其他债权投资_卖出.getValue();
                    }
                    else {
                        tableId = DictCodeListTableId.会计场景代码_综合收益类收益型资产_卖出.getValue();
                    }
                    /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
                }
                // 调用账务共通设计->【凭证最新账务查询】
                listBookLatestInfoDto = accountingCommonService.getBookLatestInfo(booksetId,
                    bookDate, bokDetailId, baseSubjectNatureList, tableId, null, null, happenDate,
                    null, null);
                // LOOP 变量.卖出账务明细列表 -> 变量.账务明细
                if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                    for (BookLatestInfoDto var : listBookLatestInfoDto) {
                        // if(变量.账务明细.基础科目性质 = XX成本的账务明细 )
                        if (DictCodeBaseSubjectNatrue.公允价值类计划类收益型资产_成本.getValue().equals(
                            var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrue.计划类资产_成本.getValue().equals(
                                var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_成本.getValue().equals(
                                var.getBaseSubjectNature())) {
                            // 变量.当日计算成本科目数量 += 变量.账务明细.发生数量
                            varDrjscbKmsl = ArithmeticUtil.doAdd(varDrjscbKmsl,
                                var.getHappenNumber());
                            // 变量.当日计算成本科目余额 += 变量.账务明细.原币发生额
                            varDrjscbKmye = ArithmeticUtil.doAdd(varDrjscbKmye,
                                var.getoHappenAmt());
                        }
                        // if(变量.账务明细.基础科目性质 = XX利息调整的账务明细 )
                        if (DictCodeBaseSubjectNatrue.计划类资产_利息调整.getValue().equals(
                            var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_利息调整.getValue().equals(
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
                        if (DictCodeBaseSubjectNatrue.公允价值类类计划类收益型资产_应计利息.getValue().equals(
                            var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrue.应计利息.getValue().equals(
                                var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_应计利息.getValue().equals(
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
                        // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                        if (DictCodeBaseSubjectNatrue.公允价值类计划类收益型资产_公允价值变动.getValue().equals(
                            var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_公允价值变动.getValue().equals(
                                var.getBaseSubjectNature())) {
                            // 变量.账务明细.借贷方向为借时
                            if (DictCodeList.借贷方向_借.getValue().equals(var.getCdFlag())) {
                                // 变量.当日计算公允价值变动科目余额 -= 变量.账务明细.原币发生额
                                varDrjsgyjzbdKmye = ArithmeticUtil.subtract(varDrjsgyjzbdKmye,
                                    var.getoHappenAmt());
                                
                                /**add by wujie on 20201119 start remark:公允价值变动本位币处理改造**/
                                // 变量.当日计算公允价值变动科目余额_本位币 -= 变量.账务明细.本币发生额
                                varDrjsgyjzbdKmye_bccy = ArithmeticUtil.subtract(varDrjsgyjzbdKmye_bccy,
                                    var.getbHappenAmt());
                                /**add by wujie on 20201119 end remark:公允价值变动本位币处理改造**/
                                
                            }
                            else {
                                // 变量.当日计算公允价值变动科目余额 += 变量.账务明细.原币发生额
                                varDrjsgyjzbdKmye = ArithmeticUtil.doAdd(varDrjsgyjzbdKmye,
                                    var.getoHappenAmt());
                                
                                /**add by wujie on 20201119 start remark:公允价值变动本位币处理改造**/
                                // 变量.当日计算公允价值变动科目余额_本位币 += 变量.账务明细.本币发生额
                                varDrjsgyjzbdKmye_bccy = ArithmeticUtil.doAdd(varDrjsgyjzbdKmye_bccy,
                                    var.getbHappenAmt());
                                /**add by wujie on 20201119 end remark:公允价值变动本位币处理改造**/
                            }
                        }
                    }
                }
            }

            /**add by wujie on 20201119 start remark:公允价值变动本位币处理改造**/
            // 汇率
            BigDecimal changeRate = BigDecimal.ONE;
            if(StringUtil.isNotEmpty(rstBaseinfo.getCcy()) && !rstBaseinfo.getCcy().equals(acctEntityEx.getStandardCcy())){
                // 调用共通汇率查询
                List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(bookDate, booksetId,
                        acctEntityEx.getStandardCcy(), rstBaseinfo.getCcy());
                if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                    changeRate = currencyMarketList.get(0).getClosePrice();
                }
            }
            /**add by wujie on 20201119 end remark:公允价值变动本位币处理改造**/
            

            // else if（变量.当日成本科目数量 = 0 and 变量.当日计算成本科目数量 = 0 ）
            if (cbkmNum.compareTo(BigDecimal.ZERO) == 0
                && varDrjscbKmsl.compareTo(BigDecimal.ZERO) == 0) {
                // 变量.公允价值变动 = -1*变量.公允价值变动科目余额
                secFairValeAlt = secFairValeAltBalance.negate();
                
                /**add by wujie on 20201119 start remark:公允价值变动本位币处理改造**/
                // 变量.公允价值变动 _本位币= -1*变量.公允价值变动科目余额_本位币
                secFairValeAlt_bccy = secFairValeAltBalance_bccy.negate();
                /**add by wujie on 20201119 end remark:公允价值变动本位币处理改造**/
            }
            else {
                // if（变量.市价 = null）
                if (marketPrice == null || marketPrice.compareTo(BigDecimal.ZERO) == 0) {
                    // 变量.市价 = （变量.当日计算成本科目余额+变量.当日计算溢折价科目余额）/变量.当日计算成本科目数量
                    marketPrice = ArithmeticUtil.divide(
                        ArithmeticUtil.doAdd(varDrjscbKmye, varDrjstxzyjKmye), varDrjscbKmsl,
                        NumberEnum.HIGH_PREC_NUM.getCode());
                }
                secFairValeAlt = ArithmeticUtil.multiply(marketPrice, varDrjscbKmsl);
                
                if(债权类资产公允价值变动计算过程小数位!=null && 债权类资产公允价值变动计算过程小数位.compareTo(-1)!=0){
                	secFairValeAlt = ArithmeticUtil.round(secFairValeAlt,债权类资产公允价值变动计算过程小数位, RoundingMode.HALF_UP);
                }
                
                /**modify by wujie on 20201119 start remark:公允价值变动本位币处理改造
                secFairValeAlt = ArithmeticUtil.subtract(secFairValeAlt,
                        ArithmeticUtil.doAdd(varDrjscbKmye, varDrjstxzyjKmye, varDrjsgyjzbdKmye));
                **/
                secFairValeAlt = ArithmeticUtil.doSub(secFairValeAlt, varDrjscbKmye,varDrjstxzyjKmye);
                		
                secFairValeAlt_bccy = calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, secFairValeAlt, changeRate);
                
                secFairValeAlt = ArithmeticUtil.doSub(secFairValeAlt,varDrjsgyjzbdKmye);
                
                secFairValeAlt_bccy = ArithmeticUtil.doSub(secFairValeAlt_bccy,varDrjsgyjzbdKmye_bccy);
                
                /**modify by wujie on 20201119 end remark:公允价值变动本位币处理改造**/
                
                
                
            }

            if (!DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(
                drawBaseInfoSelectComResult.getInvAim())) {
                AccountDriver ad = new AccountDriver();
                ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
                if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(invAim)) {
                    /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
                    //如果为其他债券
                    if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                        ad.setTableId(DictCodeListTableId.会计场景代码_公允价值变动类其他债权投资_公允价值变动.getValue());
                    }
                    else {
                        ad.setTableId(DictCodeListTableId.会计场景代码_公允价值变动类收益型资产_公允价值变动.getValue());
                    }
                    /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
                }
                else {
                    /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
                    //如果为其他债券
                    if(StringUtil.equals(DictCodeList.是否_是.getValue(), rstBaseinfo.getIsQtzq())) {
                        ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类其他债权投资_公允价值变动.getValue());
                    }
                    else {
                        ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类收益型资产_公允价值变动.getValue());
                    }
                    /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
                }

                Map<String, Object> accountSceneMap = new HashMap<>();
                Map<String, BigDecimal> accountAmountMap = new HashMap<>();
                Map<String, Object> accountCurrencyMap = new HashMap<>();

                // 共通元素记账场景要素map设值 accountSceneMap
                accountSceneMap = bookCommonLogic.accrualElementAssign(drawBaseInfoSelectComResult,
                    acctEntityEx);

                accountSceneMap.put(BookConstant.BOOK_REMARK, "债权类资产每日公允价值变动");// 备注
                // 境内外 <- 变量.标的信息.境内外
                accountSceneMap.put(BookConstant.BLN_AREA, rstBaseinfo.getBlnArea());
                // 账套代码
                accountSceneMap.put(BookConstant.BOOKSET_ID, acctEntityEx.getBooksetId());
                // 冲正标识
                accountSceneMap.put(BookConstant.OFFSET_FLAG, DictCodeList.冲正标识_正常交易.getValue());
                
                // 记账金额元素map塞值
                // 变量.公允价值变动 > 0
                if (secFairValeAlt.compareTo(BigDecimal.ZERO) >= 0) {
                    accountAmountMap.put(BookConstant.FLOAT_D,
                        ArithmeticUtil.round(secFairValeAlt, NumberEnum.MONEY));
                    accountCurrencyMap.put(BookConstant.FLOAT_D, oCcy);
                    accountAmountMap.put(BookConstant.FLOAT_1_C,
                        ArithmeticUtil.round(secFairValeAlt, NumberEnum.MONEY));
                    accountCurrencyMap.put(BookConstant.FLOAT_1_C, oCcy);
                    
                    
                    /**add by wujie on 20201119 start remark:公允价值变动本位币处理改造**/
                    accountAmountMap.put(BookConstant.FLOAT_D+ BookConstant.BCCYAMT,ArithmeticUtil.round(secFairValeAlt_bccy, NumberEnum.MONEY));
                    accountAmountMap.put(BookConstant.FLOAT_1_C+ BookConstant.BCCYAMT,ArithmeticUtil.round(secFairValeAlt_bccy, NumberEnum.MONEY));
                    
                    accountCurrencyMap.put(BookConstant.FLOAT_D+ BookConstant.BCCYAMT, acctEntityEx.getStandardCcy());
                    accountCurrencyMap.put(BookConstant.FLOAT_1_C+ BookConstant.BCCYAMT, acctEntityEx.getStandardCcy());
                    
                    /**add by wujie on 20201119 end remark:公允价值变动本位币处理改造**/
                    
                    
                    
                    
                }
                else {
                    accountAmountMap.put(BookConstant.FLOAT_C,
                        ArithmeticUtil.round(
                            ArithmeticUtil.multiply(secFairValeAlt, new BigDecimal(-1)),
                            NumberEnum.MONEY));
                    accountCurrencyMap.put(BookConstant.FLOAT_C, oCcy);
                    accountAmountMap.put(BookConstant.FLOAT_1_D,
                        ArithmeticUtil.round(
                            ArithmeticUtil.multiply(secFairValeAlt, new BigDecimal(-1)),
                            NumberEnum.MONEY));
                    accountCurrencyMap.put(BookConstant.FLOAT_1_D, oCcy);
                    
                    /**add by wujie on 20201119 start remark:公允价值变动本位币处理改造**/
                    accountAmountMap.put(BookConstant.FLOAT_C+ BookConstant.BCCYAMT,ArithmeticUtil.round(secFairValeAlt_bccy.negate(), NumberEnum.MONEY));
                    accountAmountMap.put(BookConstant.FLOAT_1_D+ BookConstant.BCCYAMT,ArithmeticUtil.round(secFairValeAlt_bccy.negate(), NumberEnum.MONEY));
                    
                    accountCurrencyMap.put(BookConstant.FLOAT_C+ BookConstant.BCCYAMT, acctEntityEx.getStandardCcy());
                    accountCurrencyMap.put(BookConstant.FLOAT_1_D+ BookConstant.BCCYAMT, acctEntityEx.getStandardCcy());
                    
                    /**add by wujie on 20201119 end remark:公允价值变动本位币处理改造**/
                    
                }

                // 全局变量 公允价值变动科目余额 更新
                secFairValeAltBalance = ArithmeticUtil.doAdd(secFairValeAltBalance,
                    secFairValeAlt);
                /**add by wujie on 20201119 start remark:公允价值变动本位币处理改造**/
                secFairValeAltBalance_bccy = ArithmeticUtil.doAdd(secFairValeAltBalance_bccy,secFairValeAlt_bccy);
                
                accountCurrencyMap.put(BookConstant.B_CCY, acctEntityEx.getStandardCcy());
                
                /**add by wujie on 20201119 end remark:公允价值变动本位币处理改造**/

                // map塞入场景要素管理中
                ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
                ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
                ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

                // 记账凭证生成
                BookGenerateResult resultNowNow = bookLogic.genAccount(ad, batchExecuteInfo);
                // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
                rtnResult.getValBokBookdetailBatch().addAll(
                    resultNowNow.getValBokBookdetailBatch());

                // 3.3 统计分析账务明细汇总信息赋值
                // 定义当日统计分析账务明细汇总信息
                BokStatDetPosiiton bokSummaryTableNew = new BokStatDetPosiiton();
                if (bokSummaryTableYestdy == null
                		|| StringUtils.isEmpty(bokSummaryTableYestdy.getBooksetId())) {
                    // 变量.当日统计分析账务明细汇总信息.当前持仓 = 变量.当日成本科目数量
                    bokSummaryTableNew.setPosAmt(cbkmNum);
                    // 变量.当日统计分析账务明细汇总信息.计提利率 = 变量.计提利率
                    bokSummaryTableNew.setIntRate(null);
                    // 变量.当日统计分析账务明细汇总信息.日摊销收益率 =NULL
                    bokSummaryTableNew.setDailydscYield(null);
                    // 变量.当日统计分析账务明细汇总信息.当日发生应计利息 = 变量.应计利息
                    bokSummaryTableNew.setTdyIntincexpAdd(null);
                    // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额 = 变量.昨日统计分析账务明细汇总信息.当日累计应计利息余 + 变量.应计利息
                    bokSummaryTableNew.setTdyIntincexp(null);
                    // 变量.当日统计分析账务明细汇总信息.当日份额 = 变量.当日计算成本科目数量
                    bokSummaryTableNew.setTdyPosition(varDrjscbKmsl);
                    // 变量.当日统计分析账务明细汇总信息.投资目的 = 计提类账务处理结果.投资目的
                    bokSummaryTableNew.setInvAim(drawBaseInfoSelectComResult.getInvAim());
                    // 变量.当日统计分析账务明细汇总信息.当日成本 = 变量.成本科目余额
                    bokSummaryTableNew.setTdyCostAmt(ArithmeticUtil.doAdd(cbkmBalancce, spilledPriceBalance));
                    // 变量.当日统计分析账务明细汇总信息.金融产品类型 = 交易标的DTO.金融产品类型
                    bokSummaryTableNew.setFinprodType(rstBaseinfo.getFinprodType());
                    // 账套代码
                    bokSummaryTableNew.setBooksetId(drawBaseInfoSelectComResult.getBooksetId());
                    // 发生日期
                    bokSummaryTableNew.setHappenDate(drawBaseInfoSelectComResult.getHappenDate());
                    // 入账日期
                    bokSummaryTableNew.setBookDate(drawBaseInfoSelectComResult.getBookDate());
                    // 账务明细代码
                    bokSummaryTableNew.setBokDetailId(bokDetailId);
                    // 账套日期
                    bokSummaryTableNew.setBooksetDate(acctEntityEx.getBooksetDate());
                    // 处理序列
                    bokSummaryTableNew.setBookSummaryOrder(
                        new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.账务汇总表_处理序列)));
                    // 起息日
                    bokSummaryTableNew.setVdate(rstBaseinfo.getVdate());
                    // 到期日
                    bokSummaryTableNew.setMdate(rstBaseinfo.getActmdate());
                    // 变量.当日统计分析账务明细汇总信息.当日公允价值变动 = 变量.公允价值变动
                    bokSummaryTableNew.setTdyFloatIngpl(secFairValeAlt);
                    //变量.当日统计分析账务明细汇总信息.当日累计公允价值变动 = 变量.公允价值变动科目余额 + 变量.公允价值变动 
                    bokSummaryTableNew.setTdyFloatIngplExp(secFairValeAltBalance);
                    
                    // 变量.当日统计分析账务明细汇总信息.剩余期限 = 返回.剩余期限
                    bokSummaryTableNew.setEndDays1(new BigDecimal(DateUtil.getBetweenDayNumber(
                    		acctEntityEx.getHappenDate(), rstBaseinfo.getActmdate())));
                    
                    // 变量.当日统计分析账务明细汇总信息.剩余存续期 = 返回.剩余存续期限
                    bokSummaryTableNew.setEndDays2(new BigDecimal(DateUtil.getBetweenDayNumber(
                    		acctEntityEx.getHappenDate(), rstBaseinfo.getActmdate())));
                    
                    // 变量.当日统计分析账务明细汇总信息.剩余期限用成本 = 变量.当日成本科目余额 + 变量.溢折价科目余额
                    bokSummaryTableNew.setEndDaysCostAmt(
                        ArithmeticUtil.doAdd(cbkmBalancce, spilledPriceBalance));
                    // 变量.当日统计分析账务明细汇总信息.行情日期= 计提类账务处理结果.估值日期
                    bokSummaryTableNew.setValDate(valDate);
                    // 变量.当日统计分析账务明细汇总信息.业务明细代码 = 计提类账务处理结果.业务明细代码
                    bokSummaryTableNew.setBusiId(drawBaseInfoSelectComResult.getBusiId());
               
                    bokSummaryTableNew.setCcy(rstBaseinfo.getCcy());
                    
                    /**add by wujie on 20201119 start remark:公允价值变动本位币处理改造**/
                    // 变量.当日统计分析账务明细汇总信息.当日公允价值变动_本位币 = 变量.公允价值变动_本位币
                    bokSummaryTableNew.setTdyFloatIngplB(secFairValeAlt_bccy);
                    
                    //变量.当日统计分析账务明细汇总信息.当日累计公允价值变动_本位币 = 变量.公允价值变动科目余额_本位币 + 变量.公允价值变动_本位币 
                    bokSummaryTableNew.setTdyFloatIngplExp(secFairValeAltBalance_bccy);
                    
                    /**add by wujie on 20201119 end remark:公允价值变动本位币处理改造**/
                }
                else {
                    BeanUtil.copyPropertiesIgnoreNull(bokSummaryTableNew, bokSummaryTableYestdy);
                    // 变量.当日统计分析账务明细汇总信息.当日公允价值变动 = 变量.公允价值变动
                    bokSummaryTableNew.setTdyFloatIngpl(secFairValeAlt);
                    //变量.当日统计分析账务明细汇总信息.当日累计公允价值变动 = 变量.公允价值变动科目余额 + 变量.公允价值变动 
                    bokSummaryTableNew.setTdyFloatIngplExp(secFairValeAltBalance);
                    // 变量.当日统计分析账务明细汇总信息.金融产品类型 = 变量.标的信息.金融产品类型
                    bokSummaryTableNew.setFinprodType(acctEntityEx.getBusiDetailType());
                    // 变量.当日统计分析账务明细汇总信息.行情日期= 计提类账务处理结果.估值日期
                    bokSummaryTableNew.setValDate(valDate);
                    
                    /**add by wujie on 20201119 start remark:公允价值变动本位币处理改造**/
                    // 变量.当日统计分析账务明细汇总信息.当日公允价值变动_本位币 = 变量.公允价值变动_本位币
                    bokSummaryTableNew.setTdyFloatIngplB(secFairValeAlt_bccy);
                    
                    //变量.当日统计分析账务明细汇总信息.当日累计公允价值变动_本位币 = 变量.公允价值变动科目余额_本位币 + 变量.公允价值变动_本位币 
                    bokSummaryTableNew.setTdyFloatIngplExp(secFairValeAltBalance_bccy);
                    
                    /**add by wujie on 20201119 end remark:公允价值变动本位币处理改造**/
                }

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

                // 添加到返回值中
                rtnResult.getBokSummaryList().add(bokSummaryTableNew);
            }
            
            // 3.3 行情价格警告信息赋值    
            BokBatchMarketwarningInfo bokBatchMarketwarningInfo =new BokBatchMarketwarningInfo();
            //1.获取批处理信息 
            SysBatchExecInfo sysBatchExecInfo = (SysBatchExecInfo) param1.get("BATCHINFO");
             //变量.行情价格警告信息DTO.流水号
            bokBatchMarketwarningInfo.setSeqNo(IDGenerator.generate());
            //变量.行情价格警告信息DTO.批处理任务组执行ID
            bokBatchMarketwarningInfo.setBatchExeSeqNo(sysBatchExecInfo.getBatchExedetailSeqNo());
            //变量.行情价格警告信息DTO.任务代码
            bokBatchMarketwarningInfo.setTaskId(sysBatchExecInfo.getTaskId());
            // 变量.行情价格警告信息DTO.账套代码
            bokBatchMarketwarningInfo.setBooksetId(booksetId);
            // 变量.行情价格警告信息DTO.发生日期
            bokBatchMarketwarningInfo.setHappenDate(happenDate);
            // 变量.行情价格警告信息DTO.账套日期
            bokBatchMarketwarningInfo.setBooksetDate(bookDate);
            // 变量.行情价格警告信息DTO.入账日期
            bokBatchMarketwarningInfo.setBookDate(bookDate);
			//变量.行情价格警告信息DTO.金融产品代码 = 变量.标的信息.金融产品代码		
            bokBatchMarketwarningInfo.setFinprodId(rstBaseinfo.getFinprodId());
			//变量.行情价格警告信息DTO.金融产品类型 = 变量.标的信息.金融产品类型		
            bokBatchMarketwarningInfo.setFinprodType(rstBaseinfo.getFinprodType());
			//变量.行情价格警告信息DTO.投资目的 = 计提类账务处理结果.投资目的	
            bokBatchMarketwarningInfo.setInvAim(invAim);  
			//变量.行情价格警告信息DTO.市场代码 = 变量.标的信息.市场代码	
            bokBatchMarketwarningInfo.setMarketCode(rstBaseinfo.getFinprodMarketId());
            
			//变量.行情价格警告信息DTO.估值来源 = 市价共通返回.估值来源	
            bokBatchMarketwarningInfo.setValueSource(marketPriceDto.getValueSource());
			//变量.行情价格警告信息DTO.理论行情日期 = 市价共通返回.理论行情日期			
            bokBatchMarketwarningInfo.setTheValueDate(marketPriceDto.getSchemeValDate()); 
			//变量.行情价格警告信息DTO.实际行情日期 = 市价共通返回.实际行情日期	
            bokBatchMarketwarningInfo.setActValueDate(marketPriceDto.getValDate());
            //变量.行情价格警告信息DTO.实际行情价格 = 市价共通返回.实际行情价格
            bokBatchMarketwarningInfo.setActMarketPrice(marketPriceDto.getPrice());
            
			//变量.行情价格警告信息DTO.是否一致 = 市价共通返回.是否一致	，如果不一致 添加到返回值中
            // bokBatchMarketwarningInfo.setYesOrNo(DictCodeList.是否_是.getValue());
            if(marketPriceDto.getValDate()==null || marketPriceDto.getSchemeValDate().compareTo(marketPriceDto.getValDate())!=0){
            	bokBatchMarketwarningInfo.setYesOrNo(DictCodeList.是否_否.getValue());
            	rtnResult.getBokBatchMarketwarningInfoList().add(bokBatchMarketwarningInfo);
            }

            // add by weipeng 20210807：增加资产行情信息入库 start
            // 变量.资产行情信息DTO
            BokAssetMarketInfo bokAssetMarketInfo = new BokAssetMarketInfo();
            //变量.资产行情信息DTO.流水号
            bokAssetMarketInfo.setSeqNo(IDGenerator.generate());
            // 变量.资产行情信息DTO.账套代码
            bokAssetMarketInfo.setBooksetId(acctEntityEx.getBooksetId());
            // 变量.资产行情信息DTO.发生日期
            bokAssetMarketInfo.setHappenDate(acctEntityEx.getHappenDate());
            // 变量.资产行情信息DTO.入账日期
            bokAssetMarketInfo.setBookDate(bookDate);
            // 变量.资产行情信息DTO.金融产品代码 = 变量.标的信息.金融产品代码
            bokAssetMarketInfo.setFinprodId(rstBaseinfo.getFinprodId());
            // 变量.资产行情信息DTO.金融产品类型 = 变量.标的信息.金融产品类型
            bokAssetMarketInfo.setFinprodType(rstBaseinfo.getFinprodType());
            // 变量.资产行情信息DTO.投资目的 = 计提类账务处理结果.投资目的
            bokAssetMarketInfo.setInvAim(acctEntityEx.getInvAim());
            // 变量.资产行情信息DTO.市场代码 = 变量.标的信息.市场代码
            bokAssetMarketInfo.setMarketCode(rstBaseinfo.getFinprodMarketId());
            // 变量.资产行情信息DTO.市价 = 变量.市价
            bokAssetMarketInfo.setMarketPrice(marketPrice);
            // 变量.资产行情信息DTO.是否来源于行情数据
            // if 市价共通返回.市价== null || 变量.市价 != 市价共通返回.市价
            if(marketPriceDto == null || marketPriceDto.getPrice() == null ||  marketPrice.compareTo(marketPriceDto.getPrice()) != 0){
                // 变量.资产行情信息DTO.行情日期 = 变量.发生日期
                bokAssetMarketInfo.setValueDate(happenDate);
                bokAssetMarketInfo.setDataSource(DictCodeListExt1.行情价格数据来源_系统生成.getValue());
            }
            else{
                // 变量.资产行情信息DTO.估值来源 = 市价共通返回.估值来源
                bokAssetMarketInfo.setValueSource(marketPriceDto.getValueSource());
                // 变量.资产行情信息DTO.行情日期 = 市价共通返回.行情日期
                bokAssetMarketInfo.setValueDate(marketPriceDto.getValDate());
                // 变量.资产行情信息DTO.估值方案代码 = 市价共通返回.估值方案代码
                bokAssetMarketInfo.setSchemeId(marketPriceDto.getSchemeId());
                // 变量.资产行情信息DTO.估值方案明细代码 = 市价共通返回.估值方案明细代码
                bokAssetMarketInfo.setSchemeDetailId(marketPriceDto.getSchemeDetailId());
                bokAssetMarketInfo.setDataSource(DictCodeListExt1.行情价格数据来源_行情信息.getValue());
            }
            // 添加到返回值中
            rtnResult.getBokAssetMarketInfoList().add(bokAssetMarketInfo);
            // add by weipeng 20210807：增加资产行情信息入库 end
        }
        return rtnResult;
    }

    /**
     * 科目余额查询--期待方向
     * 
     * @param booksetId 账套代码
     * @param bokDetailId 账务明细代码
     * @param happenDate 发生日期
     * @param baseSubjectNature 基础科目性质
     * @param expBalanceDirect 期待方向
     * @return 科目余额实体
     * @see
     * @since 1.0
     */
    private SubjectBalance getSubjectBalanceExpectDirection(String booksetId, String bokDetailId,
                                                            Date happenDate,
                                                            String baseSubjectNature,
                                                            String expBalanceDirect,
                                                            Long taskPri,String batchSeqno) {
        List<String> baseSubjectNatureList = new ArrayList<>();
        SubjectBalance subjectBalance = new SubjectBalance();
        baseSubjectNatureList.add(baseSubjectNature);

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId);
        searchDto.setBokDetailId(bokDetailId);
        searchDto.setHappenDate(happenDate);
        searchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        searchDto.setTaskPri(taskPri);
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDto, expBalanceDirect);
        if (!CollectionUtils.isEmpty(subjectBalanceList)) {
            subjectBalance = subjectBalanceList.get(0);
        }
        return subjectBalance;
    }

}
