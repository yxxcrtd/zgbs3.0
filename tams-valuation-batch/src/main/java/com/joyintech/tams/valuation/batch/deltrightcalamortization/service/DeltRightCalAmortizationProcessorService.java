/*
 * 文件名：DeltRightCalAmortizationProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年6月7日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.deltrightcalamortization.service;

import com.joyintech.calculation.CalculatorFactory;
import com.joyintech.calculation.bonds.PaperIrReset;
import com.joyintech.calculation.bonds.SecIntSchedule;
import com.joyintech.calculation.bonds.base.param.SecCalYearDayCountOutputParameter;
import com.joyintech.calculation.bonds.base.param.SecCalYearInputParameter;
import com.joyintech.calculation.bonds.constant.*;
import com.joyintech.calculation.bonds.interest.BasisIntCalculator;
import com.joyintech.calculation.bonds.interest.param.BasisIntInputParameter;
import com.joyintech.calculation.bonds.interest.param.BasisIntOutputParameter;
import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.shell.AccurIntForBookCalculator;
import com.joyintech.calculation.shell.SecDscYieldCalculator;
import com.joyintech.calculation.shell.param.*;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.calculation.util.BasicUtil;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokStatDetPosiiton;
import com.joyintech.tams.common.base.entity.BokStatLossPosiiton;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.BookLatestInfoDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalance;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.accounting.service.LossCommonBatchService;
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
import com.joyintech.tams.valuation.common.batch.finproduct.entity.*;
import com.joyintech.tams.valuation.common.batch.losscommon.service.LossCommonService;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.*;
import com.joyintech.tams.valuation.common.utils.DBUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

/**
 * 债权类资产计提摊销 Processor 对应的逻辑Service
 *
 * @author weipeng
 * @version 1.0 2018年6月7日
 * @see DeltRightCalAmortizationProcessorService
 * @since 1.0
 */
@Service
public class DeltRightCalAmortizationProcessorService {

    /**
     * 记账引擎
     */
    @Autowired
    private BookAccountGenLogic bookLogic;

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
     * 计算类共通方法
     */
    @Autowired
    private CalculationCommonService calculationCommonService;

    @Autowired
    private LossCommonService lossCommonService;

    @Autowired
    private LossCommonBatchService lossCommonBatchService;

    /**
     * process 数据处理
     *
     * @param params 参数
     * @return BookGenerateResult
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public BookGenerateResultWithBokSummary execute(Map<String, Object> params) {
        // 获取reader返回值
        DrawBaseInfoSelectComResult acctEntity = (DrawBaseInfoSelectComResult)params.get(
            GlobalConstant.READER_RESULT);

        // 若reader传递的参数为空，不进行后续处理
        if (acctEntity == null) {
            return null;
        }
        // 会计场景
        TreeNode bookSetTreeNode = acctEntity.getBookSetTreeNode();
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

        /**add by wujie on 20201020 start remark:资产减值**/
        if (returnResult.getBokStatLossPosiitonList() == null) {
            returnResult.setBokStatLossPosiitonList(new ArrayList<BokStatLossPosiiton>());
        }
        /**add by wujie on 20201020 start remark:资产减值**/

        // 3. 处理交易
        for (DrawBaseInfoSelectComDTO acctEntityEx : acctEntityList) {

            // 获取从reader中拿到的值
            String booksetId = acctEntity.getBooksetId();// 计提类账务处理结果.账套代码
            String bokDetailId = acctEntity.getBokDetailId();// 计提类账务处理结果.账务明细代码
            Date happenDate = acctEntity.getHappenDate();// 计提类账务处理结果.发生日期
            Date bookDate = acctEntity.getBookDate(); // 计提类账务处理结果.入账日期
            // 1.数据准备
            // 1.1 定义变量
            // 变量.成本科目余额 = 计提类账务处理结果.科目余额
            BigDecimal costBalance = acctEntityEx.getoAmtHappenDate();
            // 变量.成本科目数量 = 计提类账务处理结果.数量
            BigDecimal costQuantity = acctEntityEx.getNumAmtHappenDate();
            // 变量.应计利息科目余额 = 0
            BigDecimal interestBalance = BigDecimal.ZERO;
            // 变量.应计利息科目余额 _本币= 0
            BigDecimal interestBalanceBccy = BigDecimal.ZERO;
            // 变量.溢折价科目余额 = 0
            BigDecimal spilledPriceBalance = BigDecimal.ZERO;
            // 变量.币种 = 计提类账务处理结果.原币种
            String ccy = acctEntityEx.getoCcy();
            BigDecimal faceValue = new BigDecimal("100");// 百元券面 = 100

            /**add by wujie on 20201017 start remark:资产减值改造**/
            // 定义 变量.减值准备类科目余额 = 0
            BigDecimal 减值准备类科目余额 = BigDecimal.ZERO;

            /**add by wujie on 20201017 end**/

            // 1.2 汇总信息查询
            // 变量.昨日统计分析账务明细汇总信息 = 调用共通方法[账务类共通方法设计 - 统计分析账务明细汇总查询]
            BokStatDetPosiiton bokSummaryTableYestdy = accountingCommonService.getCountAnalysis(
                DateUtil.addDay(acctEntity.getHappenDate(), -1),
                DateUtil.addDay(acctEntity.getHappenDate(), -1), acctEntity.getBookDate(),
                acctEntity.getBooksetId(), acctEntity.getBokDetailId(), null, null);

            // 变量.买入交易列表
            List<BookLatestInfoDto> buyDealList = null;
            // 2. 调用共通查询发生日最新余额
            String expBalanceDirect = DictCodeList.余额方向_借方.getValue();
            /**modify by wujie on 20201016 start remark:资产减值:计划类收益型资产
            // 2.1 应计利息类科目余额
            interestBalance = this.getSubjectBalance(booksetId, bokDetailId, happenDate, bookDate,
                expBalanceDirect, DictCodeBaseSubjectNatrue.应计利息.getValue(),
                acctEntityEx.getTaskPri(),batchExecuteInfo.getBatchExedetailSeqNo());
            //应计利息类科目余额-本币 20200104 taoran add
            interestBalanceBccy = this.getSubjectBamt(booksetId, bokDetailId, happenDate, bookDate,
                expBalanceDirect, DictCodeBaseSubjectNatrue.应计利息.getValue(),
                acctEntityEx.getTaskPri(),batchExecuteInfo.getBatchExedetailSeqNo());
            // 2.2 溢折价类科目余额
            spilledPriceBalance = this.getSubjectBalance(booksetId, bokDetailId, happenDate, null,
                expBalanceDirect, DictCodeBaseSubjectNatrue.计划类资产_利息调整.getValue(),
                acctEntityEx.getTaskPri(),batchExecuteInfo.getBatchExedetailSeqNo());
            **/

            /** modify by yanya on 20201222 会计场景分支判定需要用到标的资产信息，从下面代码移上来 start**/
            // 2.4.标的类金融产品信息
            List<TargetSearchDto> queryCriteria = new ArrayList<>();
            // 查询 标的信息
            TargetSearchDto targetSearchDto1 = new TargetSearchDto();
            targetSearchDto1.setQueryType(ValDictCodeList.标的_信息.getValue());
            queryCriteria.add(targetSearchDto1);

            // 查询 付息计划
            TargetSearchDto targetSearchDto2 = new TargetSearchDto();
            // 数据代码 = RST_CASH_PLAN
            targetSearchDto2.setQueryType(ValDictCodeList.标的_现金流计划.getValue());
            // 现金流类型 = 利息
            targetSearchDto2.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
            queryCriteria.add(targetSearchDto2);

            // 报价计划
            TargetSearchDto targetSearchDto3 = new TargetSearchDto();
            // 数据代码 = RST_CASH_CALCRULE
            targetSearchDto3.setQueryType(ValDictCodeList.标的_现金流计算规则.getValue());// 现金流类型
            // 现金流类型 = 付息
            // targetSearchDto3.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());// 利息
            // 针对估值一体化：计提式批处理，获取现金流计算规则，不支持投管【计提式费用】，故只取利息。
            targetSearchDto3.setCashType2(DictCodeListExt1.现金流二级类型_利息.getValue());
            queryCriteria.add(targetSearchDto3);


            // 现金流支付规则
            TargetSearchDto targetSearchDto4 = new TargetSearchDto();
            // 数据代码 = RST_CASH_PAYRULE
            targetSearchDto4.setQueryType(ValDictCodeList.标的_现金流支付规则.getValue());
            // 现金流类型 = 付息
            targetSearchDto4.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
            queryCriteria.add(targetSearchDto4);

            // 调用标的信息共通方法
            Map<TargetSearchDto, Object> targetMap = finProductCommonService.getTargetInfo(
                    acctEntityEx.getBusiId(), queryCriteria);

            // 定义变量 标的信息
            TargetFinProduct targetFinProduct = targetMap.get(
                    targetSearchDto1) == null ? null : (TargetFinProduct)targetMap.get(
                    targetSearchDto1);
            // 定义变量 付息列表
            List<TargetCashPlan> interestPayList = targetMap.get(
                    targetSearchDto2) == null ? null : (List<TargetCashPlan>)targetMap.get(
                    targetSearchDto2);
            // 定义变量 报价列表
            List<TargetCashCalcRule> quotationList = targetMap.get(
                    targetSearchDto3) == null ? null : (List<TargetCashCalcRule>)targetMap.get(
                    targetSearchDto3);
            // 定义变量 支付规则
            List<TargetCashPayRule> payRuleList = targetMap.get(
                    targetSearchDto4) == null ? null : (List<TargetCashPayRule>)targetMap.get(
                    targetSearchDto4);
            // 保证获取的标的交易信息不为空
            if (targetFinProduct == null) {
                return null;
            }
            /** modify by yanya on 20201222 会计场景分支判定需要用到标的资产信息，从下面代码移上来 end**/
            String tableId = DictCodeListTableId.会计场景代码_收益型资产_利息计提.getValue();
            String remark = "摊余成本类计划类(收益型)资产";
            if (DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue().equals(acctEntity.getInvAim())) {
            	// 2.1 应计利息类科目余额
                interestBalance = this.getSubjectBalance(booksetId, bokDetailId, happenDate, bookDate,
                    expBalanceDirect, DictCodeBaseSubjectNatrue.公允价值类类计划类收益型资产_应计利息.getValue(),
                    acctEntityEx.getTaskPri());
                //应计利息类科目余额-本币
                interestBalanceBccy = this.getSubjectBamt(booksetId, bokDetailId, happenDate, bookDate,
                    expBalanceDirect, DictCodeBaseSubjectNatrue.公允价值类类计划类收益型资产_应计利息.getValue(),
                    acctEntityEx.getTaskPri());

                /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
                //如果为其他债券
                if(StringUtil.equals(DictCodeList.是否_是.getValue(), targetFinProduct.getIsQtzq())) {
                    buyDealList = accountingCommonService.getBookLatestInfo(
                            acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntityEx.getBokDetailId(),
                            null, DictCodeListTableId.会计场景代码_公允价值变动类其他债权投资_买入.getValue(), null, acctEntity.getHappenDate(),
                            null, null, null);

                    tableId = DictCodeListTableId.会计场景代码_公允价值变动类其他债权投资_利息计提.getValue();
                    remark = "公允价值变动类其他债权投资资产";
                }
                else {
                    buyDealList = accountingCommonService.getBookLatestInfo(
                            acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntityEx.getBokDetailId(),
                            null, DictCodeListTableId.会计场景代码_公允价值变动类收益型资产_买入.getValue(), null, acctEntity.getHappenDate(),
                            null, null, null);

                    tableId = DictCodeListTableId.会计场景代码_公允价值变动类收益型资产_利息计提.getValue();
                    remark = "公允价值变动类计划类(收益型)资产";
                }
                /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
            }
            else if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(acctEntity.getInvAim())) {
            	// 2.1 应计利息类科目余额
                interestBalance = this.getSubjectBalance(booksetId, bokDetailId, happenDate, bookDate,
                    expBalanceDirect, DictCodeBaseSubjectNatrue.应计利息.getValue(),
                    acctEntityEx.getTaskPri());
                //应计利息类科目余额-本币
                interestBalanceBccy = this.getSubjectBamt(booksetId, bokDetailId, happenDate, bookDate,
                    expBalanceDirect, DictCodeBaseSubjectNatrue.应计利息.getValue(),
                    acctEntityEx.getTaskPri());
                // 2.2 溢折价类科目余额
                spilledPriceBalance = this.getSubjectBalance(booksetId, bokDetailId, happenDate, null,
                    expBalanceDirect, DictCodeBaseSubjectNatrue.计划类资产_利息调整.getValue(),
                    acctEntityEx.getTaskPri());
                /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
                //如果为其他债券
                if(StringUtil.equals(DictCodeList.是否_是.getValue(), targetFinProduct.getIsQtzq())) {
                    buyDealList = accountingCommonService.getBookLatestInfo(
                            acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntityEx.getBokDetailId(),
                            null, DictCodeListTableId.会计场景代码_摊余成本类其他债权投资_买入.getValue(), null, acctEntity.getHappenDate(),
                            null, null, null);
                    tableId = DictCodeListTableId.会计场景代码_摊余成本类其他债权投资_利息计提.getValue();
                    remark = "摊余成本类其他债权投资资产";
                }
                else {
                    buyDealList = accountingCommonService.getBookLatestInfo(
                            acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntityEx.getBokDetailId(),
                            null, DictCodeListTableId.会计场景代码_收益型资产_买入.getValue(), null, acctEntity.getHappenDate(),
                            null, null, null);
                    tableId = DictCodeListTableId.会计场景代码_收益型资产_利息计提.getValue();
                    remark = "摊余成本类计划类(收益型)资产";
                }
                /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 end**/
                /**add by wujie on 20201017 start remark:资产减值改造**/
                //减值准备类科目余额
                减值准备类科目余额 = this.getSubjectBalance(booksetId, bokDetailId, happenDate, bookDate,
                        expBalanceDirect, DictCodeBaseSubjectNatrue.摊余成本类资产减值.getValue(),
                        acctEntityEx.getTaskPri());
                /**add by wujie on 20201017 end**/
            }
            else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(acctEntity.getInvAim())) {
            	// 2.1 应计利息类科目余额
                interestBalance = this.getSubjectBalance(booksetId, bokDetailId, happenDate, bookDate,
                    expBalanceDirect, DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_应计利息.getValue(),
                    acctEntityEx.getTaskPri());
                //应计利息类科目余额-本币
                interestBalanceBccy = this.getSubjectBamt(booksetId, bokDetailId, happenDate, bookDate,
                    expBalanceDirect, DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_应计利息.getValue(),
                    acctEntityEx.getTaskPri());
                // 2.2 溢折价类科目余额
                spilledPriceBalance = this.getSubjectBalance(booksetId, bokDetailId, happenDate, null,
                    expBalanceDirect, DictCodeBaseSubjectNatrue.综合收益类计划类收益型资产_利息调整.getValue(),
                    acctEntityEx.getTaskPri());
                /** modify by yanya on 20201222 增加是否其他债券分支处理逻辑 start**/
                //如果为其他债券
                if(StringUtil.equals(DictCodeList.是否_是.getValue(), targetFinProduct.getIsQtzq())) {
                    buyDealList = accountingCommonService.getBookLatestInfo(
                            acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntityEx.getBokDetailId(),
                            null, DictCodeListTableId.会计场景代码_综合收益类其他债权投资_买入.getValue(), null, acctEntity.getHappenDate(),
                            null, null, null);
                    tableId = DictCodeListTableId.会计场景代码_综合收益类其他债权投资_计提摊销.getValue();
                    remark = "综合收益类其他债权投资资产";
                }
                else {
                    buyDealList = accountingCommonService.getBookLatestInfo(
                            acctEntity.getBooksetId(), acctEntity.getBookDate(), acctEntityEx.getBokDetailId(),
                            null, DictCodeListTableId.会计场景代码_综合收益类收益型资产_买入.getValue(), null, acctEntity.getHappenDate(),
                            null, null, null);
                    tableId = DictCodeListTableId.会计场景代码_综合收益类收益型资产_计提摊销.getValue();
                    remark = "综合收益类计划类(收益型)资产";
                }
                /**add by wujie on 20201017 start remark:资产减值改造**/
                //减值准备类科目余额
                减值准备类科目余额 = this.getSubjectBalance(booksetId, bokDetailId, happenDate, bookDate,
                        expBalanceDirect, DictCodeBaseSubjectNatrue.其它综合收益_减值.getValue(),
                        acctEntityEx.getTaskPri());
                /**add by wujie on 20201017 end**/

            }
            /**modify by wujie on 20201016 end**/



            //账套核算参数查询-调用账务类共通

            List<String> paramCodeList = new ArrayList<>();
            paramCodeList.add(ValDictCodeList.账套配置表参数项_计划类利率型资产计提方式.getValue());

            /**modify by wujie on 20201020 start remark:资产减值
            List<BokBooksetExt> valBokBooksetExt = accountingCommonService.getValBokBooksetExt(booksetId, paramCodeList,batchExecuteInfo.getBatchExedetailSeqNo());
            //定义 变量.计提方式 = 返回值
            String makeWay = null;
            if(!CollectionUtils.isEmpty(valBokBooksetExt)){
            	makeWay = valBokBooksetExt.get(0).getParamValue();
            }
            **/
            paramCodeList.add(ValDictCodeList.账套配置表参数项_本位币差额处理方式.getValue());
            paramCodeList.add(ValDictCodeList.账套配置表参数项_减值资产第三阶段是否重新计算实际利率.getValue());
            paramCodeList.add(ValDictCodeList.账套配置表参数项_减值资产第三阶段利息计提摊销差额计入科目.getValue());


            Map<String, Object> getValBokBooksetExtMap = accountingCommonService
                    .getValBokBooksetExtForMap(acctEntity.getBooksetId(), paramCodeList);

            // 变量.计提方式
            String makeWay = null;
            if(getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_计划类利率型资产计提方式.getValue()) != null){
            	makeWay = (String)getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_计划类利率型资产计提方式.getValue());
            }
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
            /**modify by wujie on 20201020 end**/

            // 3.1 利息计提
            // if (标的信息.到期日 <= 计提类账务处理结果.发生日 or 标的信息.起息日 > 计提类账务处理结果.发生日 )
            if (DateUtil.compareDate(targetFinProduct.getMdate(), happenDate) <= 0
                || DateUtil.compareDate(targetFinProduct.getVdate(), happenDate) > 0) {
                return null;
            }
            // 汇率
            BigDecimal changeRate = BigDecimal.ONE;
            if(StringUtil.isNotEmpty(targetFinProduct.getCcy()) && !targetFinProduct.getCcy().equals(acctEntityEx.getStandardCcy())){
                // 调用共通汇率查询
                List<CurrencyMarketDto> currencyMarketList = finProductCommonService.getCurrencyMarket(bookDate, booksetId,
                        acctEntityEx.getStandardCcy(), targetFinProduct.getCcy());
                if(!StringUtil.isEmptyList(currencyMarketList) && currencyMarketList.get(0) != null){
                    changeRate = currencyMarketList.get(0).getClosePrice();
                }
            }
            // 组装记账元素
            // 初始化定义 - 记账场景要素管理
            AccountDriver ad = new AccountDriver();

            // 初始化定义 - 存放该笔记账的场景要素 map定义
            Map<String, Object> accountSceneMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额要素 map定义
            Map<String, BigDecimal> accountAmountMap = new HashMap<>();
            // 初始化定义 - 存放该笔记账的相关金额对应的币种 map定义
            Map<String, Object> accountCurrencyMap = new HashMap<>();

            ad.setTreeNode(bookSetTreeNode);// 塞入记账场景树节点 = 会计场景
            // 【weipeng-note: 债权类资产每日计提 缺少对应数据字典】
            ad.setTableId(tableId);// ad.会计场景代码
                                                                     // =
                                                                     // 债权类资产每日计提;
            // 记账场景要素 accountSceneMap设置如下：
            // 调用共通场景元素赋值-计提类场景元素
            accountSceneMap = bookCommonLogic.accrualElementAssign(acctEntity, acctEntityEx);
            // 备注 <- 债券类资产每日计提
            accountSceneMap.put(BookConstant.BOOK_REMARK, remark+"每日计提");
            // 境内外 <- 变量.标的信息.境内外
            accountSceneMap.put(BookConstant.BLN_AREA, targetFinProduct.getBlnArea());

            // 定义 变量.摊销日利率
            BigDecimal varDayRate = BigDecimal.ZERO;
            //定义变量 摊销日利率的计算是否是由于折溢价为0时，计算值用于减值计提的时候用的（统计分析明细表（账务明细）不存该值） modify by yanya on 20210105
            boolean 折溢价为零计算标识 = false;

            // accountAmountMap设置如下：
            // 定义 变量.当日计提利息金额 = 0
            BigDecimal calInterestBalanceNow = BigDecimal.ZERO;
            // 定义 变量.本日应计利息余额
            BigDecimal interestBalanceToday = BigDecimal.ZERO;
            // IF 变量.成本科目数量 > 0
            if (costBalance.compareTo(BigDecimal.ZERO) > 0) {

            	//计提类账务处理结果.发生日+1
            	Date addHappenDate = DateUtil.addDay(happenDate, 1);
            	boolean flag = false;
            	if(interestPayList != null && interestPayList.size() > 0){
            		for (int i = 0; i < interestPayList.size(); i++ ) {
            			TargetCashPlan targetCashPlan = interestPayList.get(i);
            			if(targetCashPlan.getMdate().compareTo(addHappenDate) == 0){
            				flag = true;
            				break;
            			}
            		}
            	}
            	//if（变量.计提方式 = 【数据字典】.计提方式."区间倒减" || 计提类账务处理结果.发生日+1 in (变量.付息计划列表.调整后的计算结束日)）
            	if(DictCodeList.计提方式_区间倒减.getValue().equals(makeWay) || flag == true){
                    // 调用债权类资产应计利息共通
                    AccurIntCalOutParameter accurIntCalOutParameter = this.getCalculate(
                        acctEntityEx, targetFinProduct, interestPayList, quotationList,
                        payRuleList, faceValue, acctEntity);
                    // 变量.本日应计利息余额 = 返回值 调用金额共通方法设置有效小数位
                    interestBalanceToday = ArithmeticUtil.multiply(
                        accurIntCalOutParameter.getInterest(), costQuantity);
                    interestBalanceToday = ArithmeticUtil.divide(interestBalanceToday, faceValue);
                    // 调用金额共通方法设置有效小数位
                    interestBalanceToday = ArithmeticUtil.round(interestBalanceToday,
                        NumberEnum.MONEY);
                // 变量.当日计提利息金额 = 变量.本日应计利息余额 - 变量.应计利息科目余额
                calInterestBalanceNow = ArithmeticUtil.doSub(interestBalanceToday,
                    interestBalance);

            	}else{
            	    //modify by yanya on 20201229 添加计算金额参数
                    calInterestBalanceNow = this.calcAddInt(targetFinProduct,interestPayList,payRuleList,quotationList,costBalance,happenDate,true);
            	}
            	List<Date> dateList=new ArrayList<Date>();
            	if(interestPayList!=null && interestPayList.size()>0){
            		interestPayList.forEach(x ->{
                    	dateList.add(x.getMdate());
                    });
            	}
                if(dateList.contains(DateUtil.addDay(happenDate, 1))){
                	//当计提日为付息截止日-1时，且存在该付息区间结清方式为结清的收息到账交易(支付日<付息截止日)：应收利息余额=该区间收息到账交易交易金额之和。
                	Map<String,Object> arrivalAccountMap =accountingCommonService.intervalArrivalAccount(DateUtil.addDay(happenDate, 1),acctEntityEx.getBooksetId(),targetFinProduct,interestPayList);
                	if(arrivalAccountMap!=null){
                      	String isBeforeClean=(String) arrivalAccountMap.get("isBeforeClean");
                      	if(DictCodeList.是否_是.getValue().equals(isBeforeClean)){
                      		//返回值余额
                      		BigDecimal backInterest=arrivalAccountMap.get("interestVar")==null?BigDecimal.ZERO:(BigDecimal) arrivalAccountMap.get("interestVar");
                      		//变量.本日应计利息=返回值.应计利息 - 变量.应计利息科目余额
                            calInterestBalanceNow=ArithmeticUtil.doSub(backInterest,interestBalance);;
                      	}
                	}
                }
            }
            else {
                // 变量.当日计提利息金额 = 变量.应计利息科目余额*-1
                calInterestBalanceNow = ArithmeticUtil.multiply(interestBalance,
                    new BigDecimal(-1));
            }

            // 应计利息 <- 变量.当日计提利息金额
            accountAmountMap.put(BookConstant.INTAMT,
                ArithmeticUtil.round(calInterestBalanceNow, NumberEnum.MONEY));

            // accountCurrencyMap设置如下：
            // 应计利息 = 变量.币种
            accountCurrencyMap.put(BookConstant.INTAMT, ccy);

            // 定义 变量.利息调整
            BigDecimal varLxtz = BigDecimal.ZERO;

            // 定义      变量.减值计提 = 0
            BigDecimal jzzb = BigDecimal.ZERO;

            // 3.2 摊销计提
            // if(标的信息.到期日 - 1= 计提类账务处理结果.发生日)
            // 结果集发生日 大于 标的到期日前一天
            if (happenDate.compareTo(DateUtil.addDay(targetFinProduct.getMdate(), -1)) == 0) {
                // accountAmountMap设置如下：
                // 定义 变量.利息调整 = 变量.溢折价科目余额*-1
                BigDecimal interestAdjust = ArithmeticUtil.multiply(spilledPriceBalance,
                    new BigDecimal("-1"));
                // 定义 变量.投资收益 = 变量.溢折价科目余额*-1 + 3.1 处理的应计利息
                BigDecimal invIncome = ArithmeticUtil.doAdd(interestAdjust, calInterestBalanceNow);

                // if 变量.利息调整 > 0
                if (interestAdjust.compareTo(BigDecimal.ZERO) > 0) {
                    // 利息调整（借） <- 变量.利息调整
                    accountAmountMap.put(BookConstant.DSCINC_D,
                        ArithmeticUtil.round(interestAdjust, NumberEnum.MONEY));
                }
                else {
                    // 利息调整（贷） <- 变量.利息调整*-1
                    accountAmountMap.put(BookConstant.DSCINC_C,
                        ArithmeticUtil.round(
                            ArithmeticUtil.multiply(interestAdjust, new BigDecimal("-1")),
                            NumberEnum.MONEY));
                }

                // 投资收益（借） <- 变量.投资收益
                accountAmountMap.put(BookConstant.INCOMEAMT,
                    ArithmeticUtil.round(invIncome, NumberEnum.MONEY));

                // 定义 本币金额元素Map
                Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
                for(String key : accountAmountMap.keySet()){
                	bccyAmountMap.put(key + BookConstant.BCCYAMT,
                	    calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
                }
                accountAmountMap.putAll(bccyAmountMap);

                // 变量.投资收益_本位币 = round(变量.利息调整,共通金额位数) * 变量.汇率 + 应计利息_本位币
                BigDecimal 投资收益本位币 = ArithmeticUtil.doAdd(
                    ArithmeticUtil.round(calculationCommonService.doMultiply(interestAdjust, changeRate), NumberEnum.MONEY),
                    bccyAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT));
                投资收益本位币 = ArithmeticUtil.doSub(投资收益本位币, ArithmeticUtil.round(
                    calculationCommonService.doMultiply(jzzb, changeRate), NumberEnum.MONEY));
                accountAmountMap.put(BookConstant.INCOMEAMT + BookConstant.BCCYAMT,
                    ArithmeticUtil.round(投资收益本位币, NumberEnum.MONEY));

                // accountCurrencyMap设置如下：
                for (String key : accountAmountMap.keySet()) {
                    // key = 变量.币种
                    accountCurrencyMap.put(key, ccy);
                }
                accountCurrencyMap.put(BookConstant.B_CCY, acctEntityEx.getStandardCcy());
                varLxtz = interestAdjust;
                // accountSceneMap设置如下：
                // 利率 <- 摊销日利率 (已经摊销完毕，设为0)
                accountSceneMap.put(BookConstant.INTEREST_RATE, BigDecimal.ZERO);
            }
            else {
                BigDecimal currDayAmount = BigDecimal.ZERO;
                // 定义 变量.最新报价
                BigDecimal latestPrice = BigDecimal.ZERO;
                // 是否利率变更
                boolean isLlbg = false;
                BigDecimal varCash = ArithmeticUtil.doAdd(spilledPriceBalance, costBalance);
                varCash = ArithmeticUtil.subtract(varCash, acctEntityEx.getNumAmtHappenDate());
                // （(变量.溢折价科目余额 + 变量.当日成本科目余额) - (计提类账务处理结果.数量 * 变量.百元券面面值) != 0
                if (varCash.compareTo(BigDecimal.ZERO) != 0) {
                    // 报价 报价temp ：变量.报价列表
                    if (quotationList != null){
	                    for (TargetCashCalcRule calRule : quotationList) {
	                        // if（报价.生效日 > 计提类账务处理结果.发生日)
	                        if (DateUtil.compareDate(calRule.getEffDate(), happenDate) > 0) {
	                            break;
	                        }
	                        // 报价.生效日 = 计提类账务处理结果.发生日
	                        if (calRule.getEffDate().compareTo(acctEntity.getHappenDate()) == 0
	                            && latestPrice.compareTo(calRule.getYield()) != 0) {
	                            isLlbg = true;
	                        }
	                        // 变量.最新报价 = 报价temp.生效利率
	                        latestPrice = calRule.getYield();
	                    }
                    }
                    // 变量.买入交易列表 ！=NULL
                    if ((buyDealList != null && !buyDealList.isEmpty()) || isLlbg
                        || bokSummaryTableYestdy == null
                        || StringUtils.isEmpty(bokSummaryTableYestdy.getBooksetId())) {
                        SecDscYieldOutParameter secDscYieldOutParameter = this.getSecDscYieldCalculator(
                            acctEntityEx, targetFinProduct, interestPayList, quotationList,
                            payRuleList, spilledPriceBalance, costBalance, interestBalance,
                            faceValue, latestPrice, acctEntity);
//                        currDayAmount = secDscYieldOutParameter.getCurrDayAmount();
                        // 调用金额共通方法设置有效小数位
//                        currDayAmount = ArithmeticUtil.round(currDayAmount, NumberEnum.MONEY);
                        varDayRate = secDscYieldOutParameter.getDayRate();
                        currDayAmount = ArithmeticUtil.subtract(
                                ArithmeticUtil.multiply(
                                        varDayRate,
                                        ArithmeticUtil.doAdd(spilledPriceBalance, costBalance)),
                                ArithmeticUtil.round(calInterestBalanceNow, NumberEnum.MONEY),
                                NumberEnum.MONEY.getCode(), null);
                    }
                    /**add by wujie on 20201125 start remark:资产减值**/
                    else if (DictCodeListExt3.减值资产第三阶段是否重新计算实际利率_重算.getValue().equals(
                            recalcType)) {
                            if (lossCommonService.isP3InOrOut(happenDate,
                                acctEntityEx.getBusiId(), acctEntityEx.getNumP3(), null)) {
                            	SecDscYieldOutParameter secDscYieldOutParameter = this.getSecDscYieldCalculator(
                                        acctEntityEx, targetFinProduct, interestPayList, quotationList,
                                        payRuleList, spilledPriceBalance, costBalance, interestBalance,
                                        faceValue, latestPrice, acctEntity);
//                                    currDayAmount = secDscYieldOutParameter.getCurrDayAmount();
//                                    // 调用金额共通方法设置有效小数位
//                                    currDayAmount = ArithmeticUtil.round(currDayAmount, NumberEnum.MONEY);
                                    varDayRate = secDscYieldOutParameter.getDayRate();
                                    currDayAmount = ArithmeticUtil.subtract(
                                            ArithmeticUtil.multiply(
                                                    varDayRate,
                                                    ArithmeticUtil.doAdd(spilledPriceBalance, costBalance)),
                                            ArithmeticUtil.round(calInterestBalanceNow, NumberEnum.MONEY),
                                            NumberEnum.MONEY.getCode(), null);
                            }
                    }
                    /**add by wujie on 20201125 end remark:资产减值**/
                    else {
                        // 变量.当日摊销发生额 = 变量.昨日统计分析账务明细汇总信息.日摊销利率 * (变量.溢折价科目余额 + 变量.成本科目余额) -
                        // 金额元素.应计利息
                        if (bokSummaryTableYestdy != null
                            && StringUtils.isNotEmpty(bokSummaryTableYestdy.getBooksetId())) {
                            currDayAmount = ArithmeticUtil.subtract(
                                ArithmeticUtil.multiply(
                                    bokSummaryTableYestdy.getDailydscYield(),
                                    ArithmeticUtil.doAdd(spilledPriceBalance, costBalance)),
                                ArithmeticUtil.round(calInterestBalanceNow, NumberEnum.MONEY),
                                NumberEnum.MONEY.getCode(), null);
                            // 变量.摊销日利率 = 变量.昨日统计分析账务明细汇总信息.日摊销利率
                            varDayRate = bokSummaryTableYestdy.getDailydscYield();
                        }
                    }
                }
                /** modify by yanya on 20201229 折溢价为空的时候，根据票面利率计算 start**/
                else {
                    //modify by yanya on 20201229 添加计算金额参数
                    varDayRate = this.calcAddInt(targetFinProduct,interestPayList,payRuleList,quotationList,costBalance,happenDate,false);
                    //modify by yanya on 20210105 由于折溢价为0时，计算摊销日利率值用于减值计提，统计分析明细表（账务明细）不存该值
                    折溢价为零计算标识 = true;
                }
                /** modify by yanya on 20201229 折溢价为空的时候，根据票面利率计算 end**/

                // 投资收益 <- 当日摊销发生额 + 3.1 处理的应计利息
                BigDecimal dscloss = ArithmeticUtil.doAdd(currDayAmount, calInterestBalanceNow);


                /**ADD BY CAOGUO ON 20201019 减值相关修改 START **/
                //公允价值变动无需处理减值计提
                // 定义      变量.减值计提 = 0
                if (!acctEntityEx.getInvAim().equals(
                        DictCodeListExt1.投资目的_以出售该金融资产为目的.getValue())) {
                    // if（计提类账务处理结果.减值阶段3数量 >0 && 变量.投资收益！=0）
                    if (acctEntityEx.getNumP3() > 0
                        && dscloss.compareTo(BigDecimal.ZERO) != 0) {
                        // 变量.投资收益 = 变量.摊销日利率 *（变量.减值准备科目余额 + 变量.摊销溢折价科目余额 + 变量.成本科目余额）
                    	dscloss = ArithmeticUtil.round(ArithmeticUtil.multiply(
                            ArithmeticUtil.doAdd(减值准备类科目余额, spilledPriceBalance, costBalance),
                            varDayRate), NumberEnum.MONEY);
                        // if ( 减值资产第三阶段利息计提摊销差额计入= 01：减值准备)）
                        if (DictCodeListExt3.减值资产第三阶段利息计提摊销差额计入科目_减值准备.getValue().equals(
                            subjectNoType)) {
                            // 变量.减值计提 = 变量.利息调整 + 3.1 处理的应计利息 - 变量.投资收益
                            jzzb = ArithmeticUtil.doSub(ArithmeticUtil.doAdd(currDayAmount, calInterestBalanceNow),
                            		dscloss);
                        }
                        else {
                            //变量.利息调整 = 变量.投资收益 - 3.1 处理的应计利息
                        	currDayAmount = ArithmeticUtil.doSub(dscloss, calInterestBalanceNow);
                        }
                    }
                    // if（变量.减值计提 >= 0）
                    if (jzzb.compareTo(BigDecimal.ZERO) >= 0) {
                        // 减值计提（贷） = 变量.减值计提
                        accountAmountMap.put(BookConstant.IMPAMT_C,
                            ArithmeticUtil.round(jzzb, NumberEnum.MONEY));
                        accountCurrencyMap.put(BookConstant.IMPAMT_C, ccy);
                    }
                    else {
                        // 减值计提（借） = 变量.减值计提 * -1
                        accountAmountMap.put(BookConstant.IMPAMT_D,
                            ArithmeticUtil.round(jzzb.negate(), NumberEnum.MONEY));
                        accountCurrencyMap.put(BookConstant.IMPAMT_D, ccy);
                    }
                }
                /**ADD BY CAOGUO ON 20201019 END**/

                // 利息调整 <- 当日摊销发生额
                if (currDayAmount.compareTo(BigDecimal.ZERO) >= 0) {
                    // 利息调整（借） = 变量.利息调整
                    accountAmountMap.put(BookConstant.DSCINC_D,
                        ArithmeticUtil.round(currDayAmount, NumberEnum.MONEY));
                }
                else {
                    // 利息调整（贷） = 变量.利息调整 * -1
                    accountAmountMap.put(BookConstant.DSCINC_C,
                        ArithmeticUtil.multiply(new BigDecimal(-1),
                            ArithmeticUtil.round(currDayAmount, NumberEnum.MONEY)));
                }

                // 投资收益（借） = 变量.投资收益 * -1
                accountAmountMap.put(BookConstant.INCOMEAMT,
                    ArithmeticUtil.round(dscloss, NumberEnum.MONEY));
                // 定义 本币金额元素Map
                Map<String, BigDecimal> bccyAmountMap = new HashMap<>();
                for(String key : accountAmountMap.keySet()){
                	bccyAmountMap.put(key + BookConstant.BCCYAMT, calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null, accountAmountMap.get(key), changeRate));
                }
                accountAmountMap.putAll(bccyAmountMap);


                /** ADD BY CAOGUO ON 20201019 减值相关修改 START **/
                // if (本位币差额处理方式=02 减值准备 && 减值资产第三阶段利息计提摊销差额计入= 01：减值准备)
                if (DictCodeListExt3.本位币差额处理方式_减值准备.getValue().equals(bccyadjType)
                    && DictCodeListExt3.减值资产第三阶段利息计提摊销差额计入科目_减值准备.getValue().equals(subjectNoType)) {
                    // 定义 变量.本币减值计提 = 变量.利息调整 * 变量.汇率 + 应计利息_本位币 - 投资收益_本位币
                    BigDecimal jzzbB = ArithmeticUtil.round(
                        calculationCommonService.doMultiply(jzzb, changeRate), NumberEnum.MONEY);
                    jzzbB = ArithmeticUtil.doSub(
                        ArithmeticUtil.doAdd(
                            ArithmeticUtil.round(calculationCommonService.doMultiply(currDayAmount, changeRate),
                                NumberEnum.MONEY),
                            bccyAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT)),
                        jzzbB);
                    // if（变量.减值计提 >= 0）
                    if (jzzbB.compareTo(BigDecimal.ZERO) >= 0) {
                        // 减值计提本币（贷） = 变量.本币减值计提
                        accountAmountMap.put(BookConstant.IMPAMT_C + BookConstant.BCCYAMT,
                            ArithmeticUtil.round(jzzbB, NumberEnum.MONEY));
                        accountCurrencyMap.put(BookConstant.IMPAMT_C, ccy);
                    }
                    else {
                        // 减值计提本币（借） = 变量.本币减值计提 * -1
                        accountAmountMap.put(BookConstant.IMPAMT_D + BookConstant.BCCYAMT,
                            ArithmeticUtil.round(jzzbB.negate(), NumberEnum.MONEY));
                        accountCurrencyMap.put(BookConstant.IMPAMT_D, ccy);
                    }
                }
                else {
                    // 变量.投资收益_本位币 = round(变量.利息调整,共通金额位数) * 变量.汇率 + 应计利息_本位币
                    BigDecimal 投资收益本位币 = ArithmeticUtil.doAdd(
                        ArithmeticUtil.round(calculationCommonService.doMultiply(currDayAmount, changeRate), NumberEnum.MONEY),
                        bccyAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT));
                    投资收益本位币 = ArithmeticUtil.doSub(投资收益本位币, ArithmeticUtil.round(
                        calculationCommonService.doMultiply(jzzb, changeRate), NumberEnum.MONEY));
                    accountAmountMap.put(BookConstant.INCOMEAMT + BookConstant.BCCYAMT,
                        ArithmeticUtil.round(投资收益本位币, NumberEnum.MONEY));
                }
                /** ADD BY CAOGUO ON 20201019 减值相关修改 START **/


                // accountCurrencyMap设置如下：
                for (String key : accountAmountMap.keySet()) {
                    // key = 变量.币种
                    accountCurrencyMap.put(key, ccy);
                }
                accountCurrencyMap.put(BookConstant.B_CCY, acctEntityEx.getStandardCcy());
                varLxtz = currDayAmount;
            }

            // map塞入场景要素管理中
            ad.setAccountSceneMap(accountSceneMap);// 记账场景要素
            ad.setAccountAmountMap(accountAmountMap);// 记账金额要素
            ad.setAccountCurrencyMap(accountCurrencyMap);// 记账金额币种要素

            // 记账凭证生成
            BookGenerateResult resultNowNow = bookLogic.genAccount(ad, new SysBatchExecInfo());
            // 因为当前属于同一个维度，故将生成的账务名中的账务明细（当前表）数据，累加到返回数据中
            returnResult.getValBokBookdetailBatch().addAll(
                resultNowNow.getValBokBookdetailBatch());

            // 3.3 统计分析账务明细汇总信息赋值
            // 定义：变量.计提利率
            BigDecimal jTrate = BigDecimal.ZERO;
            if (quotationList != null) {
                for (TargetCashCalcRule rule : quotationList) {
                    // 报价.生效日 > 计提类账务处理结果.发生日
                    if (rule.getEffDate().compareTo(acctEntity.getHappenDate()) > 0) {
                        break;
                    }

                    // 变量.计提利率 = 报价temp.生效利率
                    jTrate = rule.getYield();
                }
            }

            //modify by yanya on 20210105 由于折溢价为0时，计算摊销日利率值用于减值计提，统计分析明细表（账务明细）不存该值，这里清楚
            if(折溢价为零计算标识) {
                varDayRate = BigDecimal.ZERO;
            }

            // 定义当日统计分析账务明细汇总信息
            BokStatDetPosiiton bokSummaryTableNew = new BokStatDetPosiiton();
            // 变量.当日统计分析账务明细汇总信息.当前持仓 = 变量.当日成本科目数量
            bokSummaryTableNew.setPosAmt(costQuantity);
            // 变量.当日统计分析账务明细汇总信息.计提利率 = 变量.计提利率
            bokSummaryTableNew.setIntRate(jTrate);
            // 变量.当日统计分析账务明细汇总信息.日摊销收益率 =变量.摊销日利率
            bokSummaryTableNew.setDailydscYield(varDayRate);
            // 变量.当日统计分析账务明细汇总信息.当日发生应计利息 = 变量.应计利息
            bokSummaryTableNew.setTdyIntincexpAdd(calInterestBalanceNow);
            // 变量.当日统计分析账务明细汇总信息.当日累计应计利息余额 = 变量.应计利息余额+ 变量.应计利息
            bokSummaryTableNew.setTdyIntincexp(ArithmeticUtil.doAdd(interestBalance, calInterestBalanceNow));
            // 变量.当日统计分析账务明细汇总信息.当日份额 = 变量.数量余额
            bokSummaryTableNew.setTdyPosition(costQuantity);
            // 变量.当日统计分析账务明细汇总信息.投资目的 = 计提类账务处理结果.投资目的
            bokSummaryTableNew.setInvAim(acctEntity.getInvAim());
            // 变量.当日统计分析账务明细汇总信息.当日成本 = 变量.当日成本科目余额 + 变量.溢折价科目余额 + 变量.利息调整
            bokSummaryTableNew.setTdyCostAmt(ArithmeticUtil.doAdd(costBalance, spilledPriceBalance, varLxtz));
            // 变量.当日统计分析账务明细汇总信息.金融产品类型 = 交易标的DTO.金融产品类型
            bokSummaryTableNew.setFinprodType(targetFinProduct.getFinprodType());
            // 账套代码
            bokSummaryTableNew.setBooksetId(acctEntity.getBooksetId());
            // 发生日期
            bokSummaryTableNew.setHappenDate(acctEntity.getHappenDate());
            // 入账日期
            bokSummaryTableNew.setBookDate(acctEntity.getBookDate());
            // 账务明细代码
            bokSummaryTableNew.setBokDetailId(bokDetailId);
            // 账套日期
            bokSummaryTableNew.setBooksetDate(acctEntityEx.getBooksetDate());
            // 处理序列
            bokSummaryTableNew.setBookSummaryOrder(
                new BigDecimal(dBUtil.getSequenceIndex(DataIndexSequenceType.账务汇总表_处理序列)));
            // 起息日
            bokSummaryTableNew.setVdate(targetFinProduct.getVdate());
            // 到期日
            bokSummaryTableNew.setMdate(targetFinProduct.getActmdate());
            // 变量.当日统计分析账务明细汇总信息.剩余期限 = 变量.标的信息.实际到期日-计提类账务处理结果.发生日
            bokSummaryTableNew.setEndDays1(new BigDecimal(DateUtil.getBetweenDayNumber(
                acctEntity.getHappenDate(), targetFinProduct.getActmdate())));
            // 变量.当日统计分析账务明细汇总信息.剩余存续期 = 变量.标的信息.实际到期日-计提类账务处理结果.发生日
            bokSummaryTableNew.setEndDays2(new BigDecimal(DateUtil.getBetweenDayNumber(
                acctEntity.getHappenDate(), targetFinProduct.getActmdate())));
            // 变量.当日统计分析账务明细汇总信息.业务明细代码 = 计提类账务处理结果.业务明细代码
            bokSummaryTableNew.setBusiId(acctEntity.getBusiId());

            bokSummaryTableNew.setCcy(targetFinProduct.getCcy());


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

            //本位币
            bokSummaryTableNew.setbCcy(acctEntityEx.getStandardCcy());
            //当日发生应计利息_本位币
            bokSummaryTableNew.setTdyIntincexpAddB(accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT));
            //当日累计应计利息余额 _本币= 变量.应计利息余额+ 变量.应计利息
            BigDecimal tdyIntincexpB = ArithmeticUtil.doAdd(interestBalanceBccy,
    										accountAmountMap.get(BookConstant.INTAMT + BookConstant.BCCYAMT));
            bokSummaryTableNew.setTdyIntincexpB(ArithmeticUtil.round(tdyIntincexpB, NumberEnum.MONEY.getCode(), null));


            /**add by wujie on 20201017 start remark:资产减值**/
            //变量.当日统计分析账务明细汇总信息.减值余额 = 变量.减值准备科目余额 + 变量.减值计提*-1
            bokSummaryTableNew.setTdyLossAmt(ArithmeticUtil.round(ArithmeticUtil.doSub(减值准备类科目余额,jzzb), NumberEnum.MONEY));
            //变量.当日统计分析账务明细汇总信息.减值余额_本位币 = 变量.当日统计分析账务明细汇总信息.减值余额*变量.汇率
            bokSummaryTableNew.setTdyLossAmtB(calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null,
            		bokSummaryTableNew.getTdyLossAmt(), changeRate));
            /**add by wujie on 20201017 end**/

            // 添加到返回值中
            returnResult.getBokSummaryList().add(bokSummaryTableNew);

            /**add by wujie on 20201017 start remark:资产减值**/
            if(jzzb.compareTo(BigDecimal.ZERO) != 0){
            	List<ValBokBookdetailBatch> valBokBookdetailBatch = resultNowNow.getValBokBookdetailBatch();
            	List<ValBokBookdetailBatch> valBokBookdetailBatchResult = new ArrayList<ValBokBookdetailBatch>();

            	if (DictCodeListExt1.投资目的_以收取该金融资产合同现金流量为目的.getValue().equals(acctEntity.getInvAim())) {
            		for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
            			if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrue.摊余成本类资产减值.getValue())) {
            				valBokBookdetailBatchResult.add(dto);
            			}
            		}
            	}
            	else if (DictCodeListExt1.投资目的_以收取该合同现金流量及出售金融资产为目的.getValue().equals(acctEntity.getInvAim())) {
            		for (ValBokBookdetailBatch dto : valBokBookdetailBatch) {
            			if (dto.getBaseSubjectNature().equals(DictCodeBaseSubjectNatrue.其它综合收益_减值.getValue())) {
            				valBokBookdetailBatchResult.add(dto);
            			}
            		}
            	}
            	if (CollectionUtils.isNotEmpty(valBokBookdetailBatchResult)) {
            		ValBokBookdetailBatch valBokBookdetailBatchDto = valBokBookdetailBatchResult.get(0);
            		BokStatLossPosiiton bokStatLossPosiiton = lossCommonService.setLossInfoAccr(valBokBookdetailBatchDto,
            				jzzb,
            				calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null,
            						ArithmeticUtil.round(jzzb, NumberEnum.MONEY), changeRate),
            				ArithmeticUtil.round(ArithmeticUtil.doSub(减值准备类科目余额,jzzb), NumberEnum.MONEY),
            				calculationCommonService.doMultiply(NumberEnum.MONEY.getCode(), null,
            						ArithmeticUtil.round(ArithmeticUtil.doSub(减值准备类科目余额,jzzb), NumberEnum.MONEY), changeRate)
            				,null,null);
            		// 统计分析明细表（资产减值）
            		returnResult.getBokStatLossPosiitonList().add(bokStatLossPosiiton);
            	}
            }
            /**add by wujie on 20201017 end**/
        }

        return returnResult;
    }

    /**
     * 每日新增计算计划类资产得利息
     *
     *
     *
     * @param targetFinProduct 标的信息
     * @param interestPayList 付息计划
     * @param payRuleList 支付规则列表
     * @param quotationList 现金流计算规则
     * @param costBalance   计算本金
     * @param happenDate    计算日
     * @param amountFlag    是否金额
     * @return
     */
    private BigDecimal calcAddInt(TargetFinProduct targetFinProduct, List<TargetCashPlan> interestPayList, List<TargetCashPayRule> payRuleList, List<TargetCashCalcRule> quotationList, BigDecimal costBalance, Date happenDate, boolean amountFlag) {
        //计算结果，计算日每日新增得计划类资产利息
        BigDecimal calInterestBalanceNow = null;
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

        // 对于非A/A的计息基础
        if(!Basis.A_A.equals(basis)) {
            // 计息基础计算单位利率单位应计利息
            BasisIntCalculator basisCal = CalculatorFactory.createBasisIntCalculator();
            BasisIntInputParameter basisInput = new BasisIntInputParameter();
            basisInput.setCalDate(BasicUtil.dateToLocal(happenDate).plusDays(1));
            basisInput.setCalStartDate(BasicUtil.dateToLocal(happenDate));
            // 计算日所在付息计划
            if (Basis._30_360.equals(basis) && cashPlan != null) {
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
            /** modify by yanya on 20201229 计算利息金额还是利率**/
            if(amountFlag) {
                calInterestBalanceNow = ArithmeticUtil.multiply(basisOutput.getInterest(), costBalance, targetCashCalcRuleDto.getYield());
            }
            else {
                return ArithmeticUtil.multiply(basisOutput.getInterest(), targetCashCalcRuleDto.getYield());
            }
        }
        // A/A的计息基础
        else {
            if (targetCashPayRule != null){
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
            else{
                secIntSchCalMethod = SecIntSchCalMethod.起息日向后;
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
            LocalDate theoryStartDate = null;
            if (cashPlan != null){
                theoryStartDate = cashPlan.getVdateTerm() == null ? BasicUtil.dateToLocal(
                        cashPlan.getVdate()) : BasicUtil.dateToLocal(
                        cashPlan.getVdateTerm());
            }
            if (cashPlan != null){
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
	                    if(targetCashPayRule != null){
	            secInput.setFirstPayDate(BasicUtil.dateToLocal(firstPayDate));
	                    }
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

	            /** modify by yanya on 20201229 计算利息金额还是利率**/
	            if(amountFlag) {
	                calInterestBalanceNow = ArithmeticUtil.multiply(basisOutput.getInterest(), costBalance, targetCashCalcRuleDto.getYield());
	            }
	            else {
	                return ArithmeticUtil.multiply(basisOutput.getInterest(), targetCashCalcRuleDto.getYield());
	            }
			}
        }}
        // 调用金额共通方法设置有效小数位
        calInterestBalanceNow = ArithmeticUtil.round(calInterestBalanceNow,NumberEnum.MONEY);
        return calInterestBalanceNow;
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


    /**
     * 调用公用方法：【科目余额查询 - 期待方向】，获取科目余额信息中的科目余额数据
     *
     * @param booksetId 账套代码
     * @param bokDetailId 账务明细代码
     * @param happenDate 发生日
     * @param expBalanceDirect 期待余额方向
     * @param baseSubjectNature 基础科目性质
     * @return BigDecimal 科目余额
     * @see
     * @since 1.0
     */
    private BigDecimal getSubjectBalance(String booksetId, String bokDetailId, Date happenDate,
                                         Date bookDate, String expBalanceDirect,
                                         String baseSubjectNature, Long taskPri) {
        // 定义返回的科目余额
        BigDecimal balance = BigDecimal.ZERO;

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
        searchDto.setTaskPri(taskPri);
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDto, expBalanceDirect);

        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            balance = subjectBalanceList.get(0).getBalance();
        }

        return balance;
    }

    /**
     * 调用公用方法：【本币科目余额查询 - 期待方向】，获取科目余额信息中的科目余额数据
     *
     * @param booksetId 账套代码
     * @param bokDetailId 账务明细代码
     * @param happenDate 发生日
     * @param expBalanceDirect 期待余额方向
     * @param baseSubjectNature 基础科目性质
     * @return BigDecimal 科目余额
     * @see
     * @since 1.0
     */
    private BigDecimal getSubjectBamt(String booksetId, String bokDetailId, Date happenDate,
                                         Date bookDate, String expBalanceDirect,
                                         String baseSubjectNature, Long taskPri) {
        // 定义返回的科目余额
        BigDecimal balance = BigDecimal.ZERO;

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
        searchDto.setTaskPri(taskPri);
        // 调用公用方法：【科目余额查询 - 期待方向】,获取科目余额信息
        List<SubjectBalance> subjectBalanceList = accountingCommonService.getSubjectBalanceExpectDirection(
            searchDto, expBalanceDirect);

        // 获取科目余额
        if (subjectBalanceList != null && subjectBalanceList.size() > 0) {
            balance = subjectBalanceList.get(0).getbAmt();
        }

        return balance;
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
                                                             DrawBaseInfoSelectComResult acctEntity) {
        SecDscYieldInParameter parameter = new SecDscYieldInParameter();
        // 计算日
        parameter.setCalDate(BasicUtil.dateToLocal(acctEntity.getHappenDate()));
        // 券面总额 = 计提类账务处理结果.数量*100
        parameter.setTotalMoney(acctEntityEx.getNumAmtHappenDate());
        // 摊余成本净价总额
        // 变量.溢折价科目余额 + 变量.成本科目余额
        parameter.setTotalNetPrice(ArithmeticUtil.doAdd(varSpilledPriceAccBal, varOAmtHappenDate));
        // 当日百元券面面值
        parameter.setAmount(varFaceValue);

        //减值比例
  		BigDecimal eclVaule = lossCommonBatchService.getStage3Vaule(acctEntity.getHappenDate(), rstBaseinfo.getFinprodMarketId());
        //折算比例
  		BigDecimal zhesuanbili = BigDecimal.ONE;
  		if(acctEntityEx.getNumP3() >0 && eclVaule!=null){
  		    zhesuanbili = zhesuanbili.subtract(eclVaule);
        }
  		// 摊销目标额
        parameter.setTargetAmt(ArithmeticUtil.multiply(acctEntityEx.getNumAmtHappenDate(),zhesuanbili));

        /**modify by wujie on 20201130 end**/


        // 票面利率
        parameter.setPaperRate(newCash);
        // 零息贴现计算方案
        // parameter.setPlan(GlobalConstant.DIS_DEBT_SCHEME_A);
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
        else {
            bondsType = Variety.附息;
        }
        secInfoModel.setBondsType(bondsType);
        // 计息基础
        Basis basis = null;
        String basisString = listCashinfo != null
                             && listCashinfo.size() > 0 ? listCashinfo.get(0).getBasis() : null;
        basis = getBasis(basisString);
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

        SecDscYieldCalculator calculator = CalculatorFactory.createSecDscYieldCalculator();
        SecDscYieldOutParameter output = calculator.calculate(parameter);

        return output;
    }

    /**
     * 利息计提-计算百元面值的债券的利息
     *
     * @param acctEntityEx 计提基础信息DTO
     * @param rstBaseinfo 标的信息
     * @param listIntinfo 付息计划列表
     * @param listCashinfo 报价信息列表
     * @param listPayinfo 支付规则
     * @param faceValue 百元面值
     * @return AccurIntCalOutParameter
     * @see
     * @since 1.0
     */
    private AccurIntCalOutParameter getCalculate(DrawBaseInfoSelectComDTO acctEntityEx,
                                                 TargetFinProduct rstBaseinfo,
                                                 List<TargetCashPlan> listIntinfo,
                                                 List<TargetCashCalcRule> listCashinfo,
                                                 List<TargetCashPayRule> listPayinfo,
                                                 BigDecimal faceValue,
                                                 DrawBaseInfoSelectComResult acctEntity) {
        AccurIntCalInParameter parameter = new AccurIntCalInParameter();
        parameter.setCalDate(rstBaseinfo.getVdate().compareTo(acctEntity.getHappenDate()) > 0
            ? BasicUtil.dateToLocal(rstBaseinfo.getVdate()) : BasicUtil.dateToLocal(acctEntity.getHappenDate()));
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
        else {
            bondsType = Variety.附息;
        }
        secInfoModel.setBondsType(bondsType);
        // 计息基础
        Basis basis = null;
        String basisString = listCashinfo != null
                             && listCashinfo.size() > 0 ? listCashinfo.get(0).getBasis() : null;
        basis = getBasis(basisString);
        secInfoModel.setBasis(basis);
        // 利率类型
        secInfoModel.setIntRateType(IntRateType.浮动利率);
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
        }
        // add by weipeng on 20220312:默认起息日向后,否则报错
        else{
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
        secInfoModel.setYearIntRate(yearIntRate);
        parameter.setSecInfoModel(secInfoModel);

        // 付息计划列表
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

        // 债券利率重置信息
        List<PaperIrReset> paperIrResetList = new ArrayList<PaperIrReset>();
        if (listCashinfo != null && listCashinfo.size() > 0) {
            for (TargetCashCalcRule targetCashCalcRule : listCashinfo) {
                PaperIrReset paperIrReset = new PaperIrReset();
                // 票面利率生效日
                paperIrReset.setParRateEffDate(
                    BasicUtil.dateToLocal(targetCashCalcRule.getEffDate()));
                // 票面利率
                paperIrReset.setParRate(targetCashCalcRule.getYield());
                paperIrResetList.add(paperIrReset);
            }
            parameter.setPaperIrResetList(paperIrResetList);
        }

        // 当前面值
        parameter.setPaperAmt(faceValue);
        // 计算百元面值的债券的利息
        AccurIntForBookCalculator calculator2 = CalculatorFactory.createAccurIntForBookCalculator();
        AccurIntCalOutParameter output2 = calculator2.calculate(parameter);
        return output2;
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
    private Date addDate(Date nowDate, int days) {
        // 获取日历实例
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(nowDate);
        calendar.add(Calendar.DAY_OF_MONTH, days);
        Date day = calendar.getTime();

        return day;
    }
}
