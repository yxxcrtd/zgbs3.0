/*
 * 文件名：SecFairValeAlterationsProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wudebao
 * 修改时间：2018年5月24日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.secfairvalealterations.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCode;
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
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * 债券公允价值批处理的业务处理
 * 
 * @author wudebao
 * @version 1.0 2018年5月24日
 * @see SecFairValeAlterationsProcessorNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class SecFairValeAlterationsProcessorNewService {

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

    /**
     * 计算类共通
     */
    @Autowired
    private CalculationCommonService calculationCommonService;

    /**
     * 债券公允价值批处理的业务处理
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

        if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(
            drawBaseInfoSelectComResult.getInvAim())) {
            return null;
        }

        // 汇总信息查询
        // 变量.统计分析账务明细汇总信息 = 调用共通方法[账务类共通方法设计 - 统计分析账务明细汇总查询]
        BokStatDetPosiiton bokSummaryTableYestdy = accountingCommonService.getCountAnalysis(
            drawBaseInfoSelectComResult.getHappenDate(),
            drawBaseInfoSelectComResult.getHappenDate(), drawBaseInfoSelectComResult.getBookDate(),
            drawBaseInfoSelectComResult.getBooksetId(),
            drawBaseInfoSelectComResult.getBokDetailId(), null, null);
        /**
         * ADD BY WUJIE ON 20200527 START
         */
         // 账套核算参数查询
        // 定义账套参数查询参数项list
        List<String> paramCodeList = new ArrayList<String>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_债券公允价值变动计算过程小数位.getValue());// 债券交易费用损益计入方式
        Map<String, Object> getValBokBooksetExtMap = accountingCommonService.getValBokBooksetExtForMap(
        		drawBaseInfoSelectComResult.getBooksetId(), paramCodeList);
        Integer 债券公允价值变动计算过程小数位 = null;
        if(getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_债券公允价值变动计算过程小数位.getValue())!=null){
        	债券公允价值变动计算过程小数位 = Integer.valueOf((String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_债券公允价值变动计算过程小数位.getValue()));
        }
        
        
        /**ADD BY WUJIE ON 20200527 END**/

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
            // 公允价值变动科目余额（昨日）
            // BigDecimal secFairValeAltBalanceYesterday = BigDecimal.ZERO;
            // 市价
            BigDecimal marketPrice = BigDecimal.ZERO;
            // 公允价值变动
            BigDecimal secFairValeAlt = BigDecimal.ZERO;
            // 估值日期
            Date valDate = null;
            
            /**add by wujie on 20201119 start remark:公允价值变动本位币处理改造**/
            // 公允价值变动科目余额_本位币
            BigDecimal secFairValeAltBalance_bccy = BigDecimal.ZERO;
            // 公允价值变动_本位币
            BigDecimal secFairValeAlt_bccy = BigDecimal.ZERO;
            /**add by wujie on 20201119 end remark:公允价值变动本位币处理改造**/
            
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
            // 【weipeng-note： 金融产品代码字段已经没有， 暂时取账务明细代码】
            String bokDetailId = drawBaseInfoSelectComResult.getBokDetailId();
            // 投资目的
            String invAim = drawBaseInfoSelectComResult.getInvAim();
            TreeNode bookSetTreeNodNow = drawBaseInfoSelectComResult.getBookSetTreeNode();// 会计场景

            // 调用共通方法【市价查询-债券】取债券最新市价
            MarketPriceDto marketPriceDto = finProductCommonService.getBondMarketPrice(happenDate, booksetId,
                drawBaseInfoSelectComResult.getBusiId(), invAim);
            // 市价 = 返回值.市价
            marketPrice = marketPriceDto.getPrice();
            // 估值日期 = 返回值.估值日期
            valDate = marketPriceDto.getValDate();

            // 【weipeng-note: 暂时用字符串代替，后续改为数据字典】
            String baseSubjectNature = null;
            Long taskPri = acctEntityEx.getTaskPri();
            String expBalanceDirect = DictCodeList.余额方向_借方.getValue();
            // 调用共通方法【科目余额查询-期待方向】,获取成本类科目余额
            if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(invAim)) {
                baseSubjectNature = DictCodeBaseSubjectNatrueNew.摊余成本类_成本.getValue();
            }
            else if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(invAim)) {
                baseSubjectNature = DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue();
            }
            else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(invAim)) {
                baseSubjectNature = DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue();
            }
            SubjectBalance subjectBalance = this.getSubjectBalanceExpectDirection(booksetId,
                bokDetailId, happenDate, baseSubjectNature, expBalanceDirect, taskPri);
            cbkmBalancce = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
            cbkmNum = subjectBalance.getNum() == null ? BigDecimal.ZERO : subjectBalance.getNum();
            oCcy = subjectBalance.getOCcy();

            // 调用共通方法【科目余额查询-期待方向】,获取公允价值变动类科目余额
            // 【weipeng-note: 暂时用字符串代替，后续改为数据字典】
            if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(invAim)) {
                baseSubjectNature = DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue();
                subjectBalance = this.getSubjectBalanceExpectDirection(booksetId, bokDetailId,
                    happenDate, baseSubjectNature, expBalanceDirect, taskPri);
                secFairValeAltBalance = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
                
                secFairValeAltBalance_bccy = subjectBalance.getbAmt() == null ? BigDecimal.ZERO : subjectBalance.getbAmt();//add by wujie on 20201119 remark:公允价值变动本位币处理改造
                
            }
            else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(invAim)) {
                baseSubjectNature = DictCodeBaseSubjectNatrueNew.综合收益类_公允价值变动.getValue();
                subjectBalance = this.getSubjectBalanceExpectDirection(booksetId, bokDetailId,
                    happenDate, baseSubjectNature, expBalanceDirect, taskPri);
                secFairValeAltBalance = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
                
                secFairValeAltBalance_bccy = subjectBalance.getbAmt() == null ? BigDecimal.ZERO : subjectBalance.getbAmt();//add by wujie on 20201119 remark:公允价值变动本位币处理改造
            }

            // 调用共通方法【科目余额查询-期待方向】,获取溢折价科目余额
            // 【weipeng-note: 暂时用字符串代替，后续改为数据字典】
            if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(invAim)) {
                baseSubjectNature = DictCodeBaseSubjectNatrueNew.摊余成本类_利息调整.getValue();
                subjectBalance = this.getSubjectBalanceExpectDirection(booksetId, bokDetailId,
                    happenDate, baseSubjectNature, expBalanceDirect, taskPri);
                spilledPriceBalance = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
            }
            if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(invAim)) {
                baseSubjectNature = DictCodeBaseSubjectNatrueNew.公允价值损益类_利息调整.getValue();
                subjectBalance = this.getSubjectBalanceExpectDirection(booksetId, bokDetailId,
                    happenDate, baseSubjectNature, expBalanceDirect, taskPri);
                spilledPriceBalance = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
            }
            else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(invAim)) {
                baseSubjectNature = DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue();
                subjectBalance = this.getSubjectBalanceExpectDirection(booksetId, bokDetailId,
                    happenDate, baseSubjectNature, expBalanceDirect, taskPri);
                spilledPriceBalance = subjectBalance.getBalance() == null ? BigDecimal.ZERO : subjectBalance.getBalance();
            }

            // 2.4.应计利息类科目余额
            if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(invAim)) {
                baseSubjectNature = DictCodeBaseSubjectNatrueNew.摊余成本类_应计利息.getValue();
            }
            else if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(invAim)) {
                baseSubjectNature = DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue();
            }
            else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(invAim)) {
                baseSubjectNature = DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue();
            }
            subjectBalance = this.getSubjectBalanceExpectDirection(booksetId, bokDetailId,
                happenDate, baseSubjectNature, expBalanceDirect, taskPri);
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
            
            // 变量.当日计算公允价值变动科目余额_本位币 = 变量.公允价值变动科目余额_本位币
            BigDecimal varDrjsgyjzbdKmye_bccy = secFairValeAltBalance_bccy;//add by wujie on 20201119 remark:公允价值变动本位币处理改造
            
            // // 变量.次日计算成本科目数量 = 变量.当日成本科目数量
            // BigDecimal varCrjscbKmsl = cbkmNum;
            // // 变量.次日计算成本科目余额 = 变量.当日成本科目余额
            // BigDecimal varCrjscbKmye = cbkmBalancce;
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
                if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(invAim)) {
                    tableId = DictCodeListTableId.会计场景代码_公允价值损益类债券_买入.getValue();
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue());
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.公允价值损益类_利息调整.getValue());
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.公允价值损益类_应计利息.getValue());
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue());
                }
                else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(invAim)) {
                    tableId = DictCodeListTableId.会计场景代码_综合收益类债券_买入.getValue();
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue());
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue());
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.综合收益类_应计利息.getValue());
                    baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.综合收益类_公允价值变动.getValue());
                }
                // 调用账务共通设计->【凭证最新账务查询】
                List<BookLatestInfoDto> listBookLatestInfoDto = accountingCommonService.getBookLatestInfo(
                    booksetId, bookDate, bokDetailId, baseSubjectNatureList, tableId, null, null,
                    happenDate, null, null);

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
                            || DictCodeBaseSubjectNatrueNew.公允价值损益类_利息调整.getValue().equals(
                            var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue().equals(
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
                        // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                        if (DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue().equals(
                            var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_公允价值变动.getValue().equals(
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
                if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(invAim)) {
                    tableId = DictCodeListTableId.会计场景代码_公允价值损益类债券_卖出.getValue();
                }
                else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(invAim)) {
                    tableId = DictCodeListTableId.会计场景代码_综合收益类债券_卖出.getValue();
                }
                // 调用账务共通设计->【凭证最新账务查询】
                listBookLatestInfoDto = accountingCommonService.getBookLatestInfo(booksetId,
                    bookDate, bokDetailId, baseSubjectNatureList, tableId, null, null, happenDate,
                    null, null);
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
                            varDrjscbKmsl = ArithmeticUtil.doAdd(varDrjscbKmsl,
                                var.getHappenNumber());
                            // 变量.当日计算成本科目余额 += 变量.账务明细.原币发生额
                            varDrjscbKmye = ArithmeticUtil.doAdd(varDrjscbKmye,
                                var.getoHappenAmt());
                        }
                        // if(变量.账务明细.基础科目性质 = XX利息调整的账务明细 )
                        if (DictCodeBaseSubjectNatrueNew.摊余成本类_利息调整.getValue().equals(
                            var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.公允价值损益类_利息调整.getValue().equals(
                            var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_利息调整.getValue().equals(
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
                        // if(变量.账务明细.基础科目性质 = XX公允价值变动的账务明细 )
                        if (DictCodeBaseSubjectNatrueNew.公允价值损益类_公允价值变动.getValue().equals(
                            var.getBaseSubjectNature())
                            || DictCodeBaseSubjectNatrueNew.综合收益类_公允价值变动.getValue().equals(
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

                // // 次日T+1买入场景
                // baseSubjectNatureList = new ArrayList<String>();
                // if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(invAim)) {
                // tableId = DictCodeListTableId.会计场景代码_公允价值损益类债券_买入.getValue();
                // baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.公允价值损益类_成本.getValue());
                // }
                // else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(invAim)) {
                // tableId = DictCodeListTableId.会计场景代码_综合收益类债券_买入.getValue();
                // baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.综合收益类_成本.getValue());
                // }
                // // 调用账务共通设计->【凭证最新账务查询】
                // listBookLatestInfoDto = accountingCommonService.getBookLatestInfo(booksetId,
                // bookDate, bokDetailId, baseSubjectNatureList, tableId, null, null,
                // DateUtils.addDays(happenDate, 1), null, null);
                //
                // if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                // for (BookLatestInfoDto var : listBookLatestInfoDto) {
                // // 变量.次日计算成本科目数量 -= 变量.成本账务明细.发生数量
                // varCrjscbKmsl = ArithmeticUtil.subtract(varCrjscbKmsl,
                // var.getHappenNumber());
                // // 变量.次日计算成本科目余额 -= 变量.成本账务明细.原币发生额
                // varCrjscbKmye = ArithmeticUtil.subtract(varCrjscbKmye,
                // var.getoHappenAmt());
                // }
                // }
                // // 次日T+1卖出场景
                // if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(invAim)) {
                // tableId = DictCodeListTableId.会计场景代码_公允价值损益类债券_卖出.getValue();
                // }
                // else if (DictCodeListExt1.投资目的_摊余成本类.getValue().equals(invAim)) {
                // tableId = DictCodeListTableId.会计场景代码_摊余成本类债券_卖出.getValue();
                // }
                // else if (DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue().equals(invAim)) {
                // tableId = DictCodeListTableId.会计场景代码_综合收益类债券_卖出.getValue();
                // }
                // // 调用账务共通设计->【凭证最新账务查询】
                // listBookLatestInfoDto = accountingCommonService.getBookLatestInfo(booksetId,
                // bookDate, bokDetailId, baseSubjectNatureList, tableId, null, null,
                // DateUtils.addDays(happenDate, 1), null, null);
                // if (listBookLatestInfoDto != null && listBookLatestInfoDto.size() > 0) {
                // for (BookLatestInfoDto var : listBookLatestInfoDto) {
                // // 变量.次日计算成本科目数量 += 变量.成本账务明细.发生数量
                // varCrjscbKmsl = ArithmeticUtil.doAdd(varCrjscbKmsl, var.getHappenNumber());
                // // 变量.次日计算成本科目余额 += 变量.成本账务明细.原币发生额
                // varCrjscbKmye = ArithmeticUtil.doAdd(varCrjscbKmye, var.getoHappenAmt());
                // }
                // }
            }
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
                // 变量.公允价值变动 = -1*变量.公允价值变动科目余额
                secFairValeAlt_bccy = secFairValeAltBalance_bccy.negate();
                /**add by wujie on 20201119 end remark:公允价值变动本位币处理改造**/
            }
            else {
                // 变量.标的信息.息票品种为零息或贴现时
                if (DictCodeList.息票品种_零息.getValue().equals(rstBaseinfo.getCouponSpecies())
                    || DictCodeList.息票品种_贴现.getValue().equals(rstBaseinfo.getCouponSpecies())) {
                    // if（变量.市价 = null）
                    if (marketPrice == null || marketPrice.compareTo(BigDecimal.ZERO) == 0) {
                        // 变量.市价 = （变量.当日计算成本科目余额 + 变量.当日计算溢折价余额）/变量.当日计算成本科目数量
                        marketPrice = ArithmeticUtil.divide(
                            ArithmeticUtil.doAdd(varDrjscbKmye, varDrjstxzyjKmye), varDrjscbKmsl,
                            NumberEnum.HIGH_PREC_NUM.getCode());
                    }
                    // 变量.公允价值变动 =
                    // （变量.市价*变量.当日计算成本科目数量 - （变量.当日计算成本科目余额 + 变量.当日计算溢折价余额 ））
                    // -变量.当日计算公允价值变动科目余额
                    secFairValeAlt = ArithmeticUtil.multiply(marketPrice, varDrjscbKmsl);
                    if(债券公允价值变动计算过程小数位!=null && 债券公允价值变动计算过程小数位.compareTo(-1)!=0){
                    	secFairValeAlt = ArithmeticUtil.round(secFairValeAlt,债券公允价值变动计算过程小数位, RoundingMode.HALF_UP);
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

                    
                } // 变量.公允价值变动 = （变量.市价*变量.当日计算成本科目数量 -
                  // （变量.当日计算成本科目余额+变量.当日计算溢折价科目余额））-变量.当日计算公允价值变动科目余额
                else {
                    // if（变量.市价 = null）
                    if (marketPrice == null || marketPrice.compareTo(BigDecimal.ZERO) == 0) {
                        // 变量.市价 = （变量.当日计算成本科目余额+变量.当日计算溢折价科目余额）/变量.当日计算成本科目数量
                        marketPrice = ArithmeticUtil.divide(
                            ArithmeticUtil.doAdd(varDrjscbKmye, varDrjstxzyjKmye), varDrjscbKmsl,
                            NumberEnum.HIGH_PREC_NUM.getCode());
                    }
                   secFairValeAlt = ArithmeticUtil.multiply(marketPrice, varDrjscbKmsl);
                    
                    if(债券公允价值变动计算过程小数位!=null && 债券公允价值变动计算过程小数位.compareTo(-1)!=0){
                    	secFairValeAlt = ArithmeticUtil.round(secFairValeAlt,债券公允价值变动计算过程小数位, RoundingMode.HALF_UP);
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
            }

            if (!DictCodeListExt1.投资目的_摊余成本类.getValue().equals(
                drawBaseInfoSelectComResult.getInvAim())) {
                AccountDriver ad = new AccountDriver();
                ad.setTreeNode(bookSetTreeNodNow);// 塞入记账场景树节点 = 会计场景
                if (DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue().equals(invAim)) {
                    ad.setTableId(DictCodeListTableId.会计场景代码_公允价值损益类债券_每日公允价值变动.getValue());
                }
                else {
                    ad.setTableId(DictCodeListTableId.会计场景代码_综合收益类债券_每日公允价值变动.getValue());
                }

                // 【weipeng-note：AccountDriver中没有对应的 账套代码 字段】

                Map<String, Object> accountSceneMap = new HashMap<>();
                Map<String, BigDecimal> accountAmountMap = new HashMap<>();
                Map<String, Object> accountCurrencyMap = new HashMap<>();

                // 共通元素记账场景要素map设值 accountSceneMap
                accountSceneMap = bookCommonLogic.accrualElementAssign(drawBaseInfoSelectComResult,
                    acctEntityEx);

                accountSceneMap.put(BookConstant.BOOK_REMARK, "债券类资产公允价值变动");// 备注
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
                // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 start
                ad.setSubjectMappingConfigDefault(acctEntityEx.getSubjectMappingConfig());
                // add by weipeng on 20220302:会计引擎升级改造，增加辅助核算项入参 end
                // 记账凭证生成
                BookGenerateResult resultNowNow = bookLogicNew.genAccount(ad, new SysBatchExecInfo());
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
//                    bokSummaryTableNew.setTdyCostAmt(cbkmBalancce);
                    //modify jinmaodong 20190517 修改当日成本 = 变量.当日成本科目余额 + 变量.溢折价科目余额+变量.利息调整
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
                    // 2.4.标的类金融产品信息
                    // 查询条件
                    queryCriteria = new ArrayList<TargetSearchDto>();
                    // 付息计划条件
                    TargetSearchDto searchIntinfo = new TargetSearchDto();
                    searchIntinfo.setQueryType(ValDictCodeList.标的_现金流计划.getValue());
                    searchIntinfo.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
                    queryCriteria.add(searchIntinfo);
                    // 报价计划条件
                    TargetSearchDto searchCashinfo = new TargetSearchDto();
                    searchCashinfo.setQueryType(ValDictCodeList.标的_现金流计算规则.getValue());
                    searchCashinfo.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
                    queryCriteria.add(searchCashinfo);

                    Map<TargetSearchDto, Object> targetSearchMap2 = finProductCommonService.getTargetInfo(
                        acctEntityEx.getBusiId(), queryCriteria);

                    // 变量.付息列表 = 上述共通返回的计划列表
                    @SuppressWarnings("unchecked")
                    List<TargetCashPlan> listIntinfo = targetSearchMap2.get(
                        searchIntinfo) == null ? null : (List<TargetCashPlan>)targetSearchMap2.get(
                            searchIntinfo);
                    // 变量.报价列表 = 上述共通返回报价列表
                    @SuppressWarnings("unchecked")
                    List<TargetCashCalcRule> listCashinfo = targetSearchMap2.get(
                        searchCashinfo) == null ? null : (List<TargetCashCalcRule>)targetSearchMap2.get(
                            searchCashinfo);
                    // 定义 变量.计算类型
                    String calType = "";
                    if (!CollectionUtils.isEmpty(listCashinfo)) {
                        // 变量.计算类型 = 变量.报价列表.get(0).计算类型
                        calType = listCashinfo.get(0).getCalcType();
                    }
                    // if（变量.标的信息.是否含权 = "是"）
                    // 定义 变量.行权计划
                    FinExercisePlan finExercisePlan = new FinExercisePlan();
                    if (DictCodeList.是否_是.getValue().equals(rstBaseinfo.getIsRight())) {
                        // 调用标的类共通下一行权计划查询
                        finExercisePlan = finProductCommonService.queryFinExercisePlan(
                            drawBaseInfoSelectComResult.getBusiId(),
                            DictCodeListExt1.金融产品类型_债券.getValue(), happenDate,null);
                        if (finExercisePlan == null) {
                        	finExercisePlan = new FinExercisePlan();
                        }
                    }
                    BigDecimal[] remainResult = calculationCommonService.calSecRemTermAndDuration(
                        rstBaseinfo, listIntinfo, calType, finExercisePlan.getExerciseDate(),
                        happenDate);
                    // 变量.当日统计分析账务明细汇总信息.剩余期限 = 返回.剩余期限
                    bokSummaryTableNew.setEndDays1(remainResult[0]);
                    // 变量.当日统计分析账务明细汇总信息.剩余存续期 = 返回.剩余存续期限
                    bokSummaryTableNew.setEndDays2(remainResult[1]);
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
                rtnResult.getBokSummaryList().add(bokSummaryTableNew);
            }
            // 3.3 行情价格警告信息赋值      ------add wangwei1 20190131
            //modify by mashukai 20201123  TISHKBS-1631	start
            if (!DictCodeList.息票品种_零息.getValue().equals(rstBaseinfo.getCouponSpecies())
                    && !DictCodeList.息票品种_贴现.getValue().equals(rstBaseinfo.getCouponSpecies())
                    && (marketPriceDto.getSchemeValDate()!= null)  ) {
            //modify by mashukai 20201123  TISHKBS-1631	end
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


                }

            // add by weipeng 20210807：增加资产行情信息入库 start
            // 变量.资产行情信息DTO
            BokAssetMarketInfo bokAssetMarketInfo = new BokAssetMarketInfo();
            //变量.资产行情信息DTO.流水号
            bokAssetMarketInfo.setSeqNo(IDGenerator.generate());
            // 变量.资产行情信息DTO.账套代码
            bokAssetMarketInfo.setBooksetId(acctEntityEx.getBooksetId());
            // 变量.资产行情信息DTO.发生日期
            bokAssetMarketInfo.setHappenDate(happenDate);
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
            // 变量.资产行情信息DTO.是否来源于行情数据，
            // if 市价共通返回.市价== null || 变量.市价!=市价共通返回.市价
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
                                                            Long taskPri) {
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
