/*
 * 文件名：PrdValuationFeeBeforeProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：yangjian
 * 修改时间：2018年8月8日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.prdvaluationfeebefore.service;

import com.joyintech.calculation.config.NumberEnum;
import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.BokStatBokPosition;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.BookLatestInfoDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.StayDrawSubprdEntity;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.base.entity.BokStatBokPositionDto;
import com.joyintech.tams.valuation.batch.common.calculation.entity.InterestBasisDaysParameter;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.batch.common.prdvaluationfeecommon.dto.BokstatbokpositionReaderDto;
import com.joyintech.tams.valuation.batch.common.prdvaluationfeecommon.dto.BokstatbokpositionWriterDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.*;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrueNew;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * (产品估值计算-费前) - process业务处理
 * 
 * @author yangjian
 * @version 1.0 2018年8月7日
 * @see PrdValuationFeeBeforeProcessorNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class PrdValuationFeeBeforeProcessorNewService {

    /**
     * 标的类共通方法
     */
    @Autowired
    private FinProductCommonService finProductCommonService;

    /**
     * 批处理账务相关公用方法
     */
    @Autowired
    private AccountingCommonService accountCommonService;

    /**
     * 计算类共通方法
     */
    @Autowired
    private CalculationCommonService calculationCommonService;

    /**
     * process 数据处理
     * 
     * @param param 传递参数
     * @return
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public BokstatbokpositionWriterDto execute(Map<String, Object> param) {

        // 获取reader返回值
        BokstatbokpositionReaderDto acctEntity = (BokstatbokpositionReaderDto)param.get(
            GlobalConstant.READER_RESULT);

        // 若reader传递的参数或金融产品交易信息列表为空，不进行后续处理
        if (acctEntity == null) {
            return null;
        }

        // 调用公用方法 - 账套参数查询，获取估值小数位处理方式和估值小数位位数
        // 定义账套参数查询参数项list
        List<String> paramCodeList = new ArrayList<String>();
        paramCodeList.add(ValDictCodeList.账套配置表参数项_估值小数位处理方式.getValue());// 估值小数位处理方式
        paramCodeList.add(ValDictCodeList.账套配置表参数项_估值小数位位数.getValue());// 估值小数位位数
        paramCodeList.add(ValDictCodeList.账套配置表参数项_收益率小数位处理方式.getValue());// 收益率小数位处理方式
        paramCodeList.add(ValDictCodeList.账套配置表参数项_收益率小数位位数.getValue());// 收益率小数位位数
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_期初单位净值小数位数.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_期初单位净值尾数处理方式.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_周期年化收益率小数位数.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_周期年化收益率位数处理方式.getValue());
     	paramCodeList.add(ValDictCodeList.账套配置表参数项_产品费前估值小数位数_估值表展示.getValue());
     	paramCodeList.add(ValDictCodeList.账套配置表参数项_产品费前估值小数尾数处理方式_估值表展示.getValue());
     	paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_估值小数位.getValue());
        paramCodeList.add(ValDictCodeList.账套配置表参数项_浮动管理费_估值小数尾数处理方式.getValue());
        
        Map<String, Object> getValBokBooksetExtMap = accountCommonService.getValBokBooksetExtForMap(
            acctEntity.getBooksetId(), paramCodeList);

        // 获取收益率小数位处理方式和收益率小数位位数
        String yeildDecType = (String)getValBokBooksetExtMap.get(
            ValDictCodeList.账套配置表参数项_收益率小数位处理方式.getValue());// 收益率小数位处理方式
        String yeilDecDigits = (String)getValBokBooksetExtMap.get(
            ValDictCodeList.账套配置表参数项_收益率小数位位数.getValue());// 收益率小数位位数
        String floatCyclyearYieldDigits = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_浮动管理费_周期年化收益率小数位数.getValue());
        String floatCyclyearYieldType = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_浮动管理费_周期年化收益率位数处理方式.getValue());
     	String showBeforeDecDigits = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_产品费前估值小数位数_估值表展示.getValue());
     	String showBeforeDecType = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_产品费前估值小数尾数处理方式_估值表展示.getValue());
     	String floatDecDigits = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_浮动管理费_估值小数位.getValue());
     	String floatDecType = (String) getValBokBooksetExtMap.get(ValDictCodeList.账套配置表参数项_浮动管理费_估值小数尾数处理方式.getValue());

        // 获取标的信息
        Map<String, Object> targetInfo = this.getTargetInfo(acctEntity.getFinprodId(),
            acctEntity.getVdate(), acctEntity.getHappenDate(), acctEntity.getFinprodType(),
            acctEntity.getBranch());

        /**
        // 标的_信息
        TargetFinProduct targetFinProduct = (TargetFinProduct)targetInfo.get(
            ValDictCodeList.标的_信息.getValue());
            **/
        // 标的_业绩报酬计提规则
        List<TargetPerfRewardRule> targetPerfRewardRuleList = (List<TargetPerfRewardRule>)targetInfo.get(
            ValDictCodeList.标的_业绩报酬计提规则.getValue());
        TargetPerfRewardRule targetPerfRewardRule = null;
        if (targetPerfRewardRuleList != null && targetPerfRewardRuleList.size() > 0) {
            targetPerfRewardRule = targetPerfRewardRuleList.get(
                targetPerfRewardRuleList.size() - 1);
        }

        // 标的_业绩报酬计提计划
        List<TargetPerfRewardAccrsche> targetPerfRewardAccrscheList = (List<TargetPerfRewardAccrsche>)targetInfo.get(
            ValDictCodeList.标的_业绩报酬计提计划.getValue());

        // 标的_现金流计算规则
        List<TargetCashCalcRule> targetCashCalcRuleList = (List<TargetCashCalcRule>)targetInfo.get(
            ValDictCodeList.标的_现金流计算规则.getValue());
        // 变量.现金流计算规则
        TargetCashCalcRule targetCashCalcRule = new TargetCashCalcRule();
        if (targetCashCalcRuleList != null && targetCashCalcRuleList.size() > 0) {
            // 按日期降序排列取现金流一级类型为"利息"的第一条记录(当前sql中按照生效日期顺序排序，故逆序进行循环获取)
            for (int index = targetCashCalcRuleList.size() - 1; index >= 0; index-- ) {
                // 逆序获取第一个现金流一级类型为"利息"的数据
                if (DictCodeListExt1.现金流一级类型_利息.getValue().equals(
                    targetCashCalcRuleList.get(index).getCashType1())) {
                    targetCashCalcRule = targetCashCalcRuleList.get(index);
                    break;
                }
            }
        }
        // 获取支付规则
        List<TargetCashPayRule> listTargetCashPayRule = (List<TargetCashPayRule>)targetInfo.get(
                ValDictCodeList.标的_现金流支付规则.getValue());
        TargetCashPayRule targetCashPayRule = new TargetCashPayRule();
        if (!CollectionUtils.isEmpty(listTargetCashPayRule)) {
            targetCashPayRule = listTargetCashPayRule.get(0);
        }

        BokStatBokPositionDto position = new BokStatBokPositionDto();
        // 查询日开始
        position.setSdateStart(DateUtil.addDay(acctEntity.getHappenDate(), -1));
        // 查询结束日
        position.setSdateEnd(DateUtil.addDay(acctEntity.getHappenDate(), -1));
        // 入账日
        position.setBookDate(acctEntity.getBookDate());
        // 账套
        position.setBooksetId(acctEntity.getBooksetId());
        
        position.setLayeringId(acctEntity.getLayeringId());
        
        List<BokStatBokPosition> valStatBokPositionForYesterDayList = accountCommonService.getValStatBokPosition(
            position);
        BokStatBokPosition valStatBokPositionForYesterDay = new BokStatBokPosition();
        if (valStatBokPositionForYesterDayList != null
            && valStatBokPositionForYesterDayList.size() > 0) {
            valStatBokPositionForYesterDay = valStatBokPositionForYesterDayList.get(0);
        }
        // 查询日开始
        position.setSdateStart(acctEntity.getHappenDate());
        // 查询结束日
        position.setSdateEnd(acctEntity.getHappenDate());
        // 当日帐套资产负债信息 = 调用共通方法查询：帐套资产负债查询（DTO.发生日，DTO.发生日，DTO.帐套代码，DTO.入账日）
        List<BokStatBokPosition> valStatBokPositionForTodayList = accountCommonService.getValStatBokPosition(
            position);
        BokStatBokPosition valStatBokPositionForToday = new BokStatBokPosition();
        if (valStatBokPositionForTodayList != null && valStatBokPositionForTodayList.size() > 0) {
            valStatBokPositionForToday = valStatBokPositionForTodayList.get(0);
        }
        else {
            // 当日帐套资产负债信息 初始化
            valStatBokPositionForToday.setBooksetId(acctEntity.getBooksetId());// 账套代码
            valStatBokPositionForToday.setHappenDate(acctEntity.getHappenDate());// 发生日
            valStatBokPositionForToday.setBookDate(acctEntity.getBookDate());// 入账日
            valStatBokPositionForToday.setBooksetDate(acctEntity.getBooksetDate());// 账套日期
        }

        // 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->查询分层结构子结构
        Map<String, Object> params = new HashMap<>();
        // 账套代码 = DTO.账套代码
        params.put("booksetId", acctEntity.getBooksetId());
        // 级别序号 = DTO.级别序号
        params.put("lev", acctEntity.getLev());
        // 查询分层结构子结构
        List<StayDrawSubprdEntity> listStayDrawSubprdEntity = accountCommonService.querySubBokAcctInfo(
            params);
        // 定义 变量.子分层信息.账务明细代码
        List<String> listBokDetailId = new ArrayList<String>();
        if (listStayDrawSubprdEntity != null) {
            listStayDrawSubprdEntity.forEach(item -> {
                listBokDetailId.add(item.getBokDetailId());
            });
        }
        // dto.核算主体代码 = dto.核算分层代码 && dto.核算主体类型 = dto.核算分层类型?null:子分层信息.账务明细代码
        // 定义 变量.List<账务明细代码>
        List<String> bokDetailIds = null;
        if (!"1".equals(acctEntity.getLev())) {
            bokDetailIds = listBokDetailId;
        }

        // 获取【计提结束日损益类科目余额】
        BigDecimal endPlaneForSjtAmt = this.getPlanEndAmt(DictCodeList.科目类型_损益类.getValue(), null,
            DictCodeList.余额方向_贷方.getValue(), acctEntity.getBooksetId(), acctEntity.getHappenDate(),
            acctEntity.getBookDate(), acctEntity.getStandardCcy(), acctEntity.getTaskPri(), null,
            bokDetailIds);

        // 获取【计提结束日所有者权益余额】
        BigDecimal endPlaneForOwnerProfit = this.getPlanEndAmt(DictCodeList.科目类型_所有者权益类.getValue(),
            null, DictCodeList.余额方向_贷方.getValue(), acctEntity.getBooksetId(),
            acctEntity.getHappenDate(), acctEntity.getBookDate(), acctEntity.getStandardCcy(),
            acctEntity.getTaskPri(), null, bokDetailIds);

        // 获取【计提结束日实收资本余额】
        BigDecimal endPlaneForRealCapital = this.getPlanEndAmt(null,
            DictCodeBaseSubjectNatrueNew.实收资本.getValue(), DictCodeList.余额方向_贷方.getValue(),
            acctEntity.getBooksetId(), acctEntity.getHappenDate(), acctEntity.getBookDate(),
            acctEntity.getStandardCcy(), acctEntity.getTaskPri(), null, bokDetailIds);

        // 变量.计提结束日费前资产净值 = (计提结束日损益类余额 + 计提结束日所有者权益余额)
        BigDecimal endPlanAssetValue = ArithmeticUtil.doAdd(endPlaneForSjtAmt,
            endPlaneForOwnerProfit);

        // 当日帐套资产负债信息.实收资本 = 计提结束日实收资本余额
        valStatBokPositionForToday.setCapital(endPlaneForRealCapital);
        // 当日帐套资产负债信息.费前资产净值 = 变量.计提结束日费前资产净值
        valStatBokPositionForToday.setNetAssetValueBef(endPlanAssetValue);
        // 当日帐套资产负债信息.资产净值 = 当日帐套资产负债信息.费前资产净值
        valStatBokPositionForToday.setNetAssetValue(
            valStatBokPositionForToday.getNetAssetValueBef());
        // 当日帐套资产负债信息.收益类型=账套.核算方式
        valStatBokPositionForToday.setProfitType(acctEntity.getAcctmode());
        // 当日帐套资产负债信息.是否子产品 = dto.核算主体代码 = dto.核算分层代码 && dto.核算主体类型 = dto.核算分层类型?否【N】:是【Y】
        if (StringUtil.equals(acctEntity.getAcctSubjectId(), acctEntity.getFinprodId())
            && StringUtil.equals(acctEntity.getAcctSubjectType(),
                acctEntity.getFinprodType())) {
            valStatBokPositionForToday.setIsSubPrd(DictCodeList.是否_否.getValue());
        }
        else{
            valStatBokPositionForToday.setIsSubPrd(DictCodeList.是否_是.getValue());
        }
        // 当日帐套资产负债信息.分层代码 = DTO.分层代码
        valStatBokPositionForToday.setLayeringId(acctEntity.getLayeringId());

        // IF（DTO.核算方式 != "预期收益"）
        if (!DictCodeList.收益类型_收益型.getValue().equals(acctEntity.getAcctmode())) {
            // 变量.计提结束日费前单位净值 = 变量.费前资产净值 / 计提结束日实收资本余额
            BigDecimal endPlanUnitValue = ArithmeticUtil.divide(endPlanAssetValue,
                endPlaneForRealCapital,14, RoundingMode.HALF_UP);

            /*MODIFY BY WUJIE ON 20190819
            // 对【计提结束日费前单位净值】进行估值小数位处理:根据 参数.估值小数位处理方式 + 参数.估值小数位位数 处理
            if(DictCodeList.收益类型_净值型.getValue().equals(acctEntity.getAcctmode())){
                endPlanUnitValue = accountCommonService.filterByDecTypeAndDigits(endPlanUnitValue, floatStarttotalUnitnetType, floatStarttotalUnitnetDigits);
            }
            */

            // 变量.计提结束日分红金额列表 =
            // 调用共通方法查询：凭证最新账务查询（场景代码=“8012”，发生日=DTO.发生日，基础科目性质=“待分配利润”，入账日=DTO.入账日，账套代码=DTO.账套代码）
            // 定义基础科目性质list
            List<String> baseSubjectNatureList = new ArrayList<String>();
//            baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.待分配利润.getValue());// 基础科目性质=“待分配利润”
            baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.应付利润.getValue());// 基础科目性质=“待分配利润”
            List<BookLatestInfoDto> endPlanProfitShareList = accountCommonService.getBookLatestInfo(
                acctEntity.getBooksetId(), acctEntity.getBookDate(), null, baseSubjectNatureList,
                DictCodeListTableId.会计场景代码_产品_净值型_分红除权处理.getValue(), null, acctEntity.getHappenDate(),
                null, null, null);

            // 变量.计提结束日分红金额 - 初始化
            BigDecimal endPlanProfitShareAmt = BigDecimal.ZERO;
            // 变量.分红净值 - 初始化
            BigDecimal profitShareNet = BigDecimal.ZERO;

            // 循环计提结束日分红金额列表
            for (BookLatestInfoDto vo : endPlanProfitShareList) {
            	if (bokDetailIds == null
            			|| (bokDetailIds != null && bokDetailIds.contains(vo.getBokDetailId()))) {
	                // 调用共通方法：科目余额汇总查询
	                // 实收资本余额
	                BigDecimal varSszb = this.getPlanEndAmt(null,
	                    DictCodeBaseSubjectNatrueNew.实收资本.getValue(), DictCodeList.余额方向_贷方.getValue(),
	                    acctEntity.getBooksetId(), acctEntity.getHappenDate(),
	                    acctEntity.getBookDate(), acctEntity.getStandardCcy(), acctEntity.getTaskPri(),
	                    vo.getBookOrder(), bokDetailIds);
	
	                // 变量.计提结束日分红金额 = 变量.计提结束日分红金额 + 变量.计提结束日分红金额列表.原币发生额
	                endPlanProfitShareAmt = ArithmeticUtil.doAdd(endPlanProfitShareAmt,
	                    vo.getbHappenAmt());
	
	                // 变量.分红净值 = 变量.分红净值 + 变量.计提结束日分红金额列表.原币发生额/实收资本余额
	                profitShareNet = ArithmeticUtil.doAdd(profitShareNet,
	                    ArithmeticUtil.divide(vo.getbHappenAmt(), varSszb));
	                /**MODIFY BY WUJIE ON 20190821
	                // 对【变量.分红净值】进行估值小数位处理:根据 参数.估值小数位处理方式 + 参数.估值小数位位数 处理
                    profitShareNet = accountCommonService.filterByDecTypeAndDigits(profitShareNet,
                            floatStarttotalUnitnetType, floatStarttotalUnitnetDigits);
                            **/
            	}
            }

            // 当日帐套资产负债信息.费前单位净值 = 计提结束日费前单位净值
            valStatBokPositionForToday.setNetUnitValueBef(endPlanUnitValue);
            // 当日帐套资产负债信息.单位净值 = 计提结束日费前单位净值
            valStatBokPositionForToday.setNetUnitValue(endPlanUnitValue);
            // 当日帐套资产负债信息.当日分红金额 = 当日帐套资产负债信息.当日分红金额 + 变量.计提结束日分红金额
            valStatBokPositionForToday.setTdyBounsAmt(ArithmeticUtil.doAdd(
                valStatBokPositionForToday.getTdyBounsAmt(), endPlanProfitShareAmt));
            // 当日帐套资产负债信息.累计分红金额 = 昨日当日帐套资产负债信息.累计分红金额 + 变量.计提结束日分红金额
            valStatBokPositionForToday.setTotBounsAmt(ArithmeticUtil.doAdd(
                valStatBokPositionForYesterDay.getTotBounsAmt(), endPlanProfitShareAmt));
            // 当日帐套资产负债信息.当日分红净值= 当日帐套资产负债信息.当日分红净值 + 变量.分红净值
            valStatBokPositionForToday.setTdyBounsNet(
                ArithmeticUtil.doAdd(valStatBokPositionForToday.getTdyBounsNet(), profitShareNet));
            // 当日帐套资产负债信息.累计分红净值 = 昨日帐套资产负债信息.累计分红净值+ 变量.分红净值
            valStatBokPositionForToday.setTotBounsNet(ArithmeticUtil.doAdd(
                valStatBokPositionForYesterDay.getTotBounsNet(), profitShareNet));

        }
        // IF（DTO.核算方式 = "预期收益型"）
        if (DictCodeList.收益类型_收益型.getValue().equals(acctEntity.getAcctmode())) {
            BigDecimal profitAmt = BigDecimal.ZERO;// 当日应付利润
            BigDecimal totProfitAmt = BigDecimal.ZERO;// 累计应付利润
            if (DateUtil.compareDate(acctEntity.getHappenDate(), acctEntity.getVdate()) >= 0) {
                // 变量.累计应付利润 = 昨日账套资产负债信息.累计应付利润
                totProfitAmt = valStatBokPositionForYesterDay.getTotProfitAmt();

                // 调用共通方法查询：凭证最新账务查询（场景代码=“8004”，发生日=DTO.发生日，入账日=DTO.入账日，账套代码=DTO.账套代码）
                // 定义基础科目性质list
                List<String> baseSubjectNatureList = new ArrayList<String>();
                baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.应付利润.getValue());
                List<BookLatestInfoDto> endProfitList = accountCommonService.getBookLatestInfo(
                    acctEntity.getBooksetId(), acctEntity.getBookDate(), null,
                    baseSubjectNatureList, DictCodeListTableId.会计场景代码_产品_预期收益型_客户收益计提.getValue(), null,
                    acctEntity.getHappenDate(), null, null, null);
                for (BookLatestInfoDto vo : endProfitList) {
                    // IF (账务明细.账务明细代码 in 子分层信息.账务明细代码)
                    if (listBokDetailId.contains(vo.getBokDetailId())) {
                        profitAmt = ArithmeticUtil.doAdd(profitAmt, vo.getbHappenAmt());
                    }
                }

                // 当日帐套资产负债信息.当日应付利润 = 变量.当日应付利润
                valStatBokPositionForToday.setProfitAmt(profitAmt);
                // 当日帐套资产负债信息.累计应付利润 = 变量.累计应付利润 + 变量.当日应付利润
                valStatBokPositionForToday.setTotProfitAmt(
                    ArithmeticUtil.doAdd(totProfitAmt, profitAmt));

                // 当日帐套资产负债信息.计提利率 = 变量.现金流计算规则.生效利率
                valStatBokPositionForToday.setIntRate(targetCashCalcRule.getYield());
            }
        }

        // IF（DTO.核算方式 = "货币" ）
        if (DictCodeList.收益类型_货币型.getValue().equals(acctEntity.getAcctmode())) {
            // 变量.计算基础天数 = 调用共通方法：计息基础天数
            int BasisDays = calculationCommonService.calcInterestBasisDays(
                new InterestBasisDaysParameter(targetCashCalcRule.getBasis()));

            // 变量.单位收益 = (当日帐套资产负债信息.资产净值 - 当日帐套资产负债信息.实收资本)/当日帐套资产负债信息.实收资本
            BigDecimal unitProfit = ArithmeticUtil.divide(
                ArithmeticUtil.doSub(valStatBokPositionForToday.getNetAssetValue(),
                    valStatBokPositionForToday.getCapital()),
                valStatBokPositionForToday.getCapital());
            // 变量.单位收益 ,按照中间变量小数位处理方式(全精度)
            unitProfit = ArithmeticUtil.round(unitProfit, NumberEnum.ALL_PRECISION);

            // 当日帐套资产负债信息.万份收益_费前 = (当日帐套资产负债信息.资产净值 - 当日帐套资产负债信息.实收资本)/当日帐套资产负债信息.实收资本*10000
            BigDecimal pYieldBefNow = ArithmeticUtil.multiply(ArithmeticUtil.divide(
                ArithmeticUtil.doSub(valStatBokPositionForToday.getNetAssetValue(),
                    valStatBokPositionForToday.getCapital()),
                valStatBokPositionForToday.getCapital()), new BigDecimal(10000));
            /**MODIFY BY WUJIE ON 20190819
            // 按照账套参数.万份收益的小数位处理方式 (已与曹国沟通，此处使用估值小数位处理:根据 参数.估值小数位处理方式 + 参数.估值小数位位数 处理)
            pYieldBefNow = accountCommonService.filterByDecTypeAndDigits(pYieldBefNow, floatDecType,
                    floatDecDigits);
                    **/
            // 塞值[当日帐套资产负债信息.万份收益_费前]
            valStatBokPositionForToday.setpYieldBef(pYieldBefNow);

            // 当日帐套资产负债信息.万份收益 = 当日帐套资产负债信息.万份收益_费前
            valStatBokPositionForToday.setpYield(valStatBokPositionForToday.getpYieldBef());

            // 当日帐套资产负债信息.当日年化收益率_费前 = 变量.单位收益 * 变量.计算基础天数
            BigDecimal tdyYieldBefNow = ArithmeticUtil.multiply(unitProfit,
                new BigDecimal(BasisDays));
            /**在账套资产负债表中按全精度存储 MODIFY BY WUJIE ON 20200327 REMARK:ZGBUG-50
            // 按照账套参数.收益率的小数位处理方式 （即使用收益率小数位处理:根据 参数.收益率小数位处理方式 + 参数.收益率小数位位数 处理）
            tdyYieldBefNow = accountCommonService.filterByDecTypeAndDigits(tdyYieldBefNow,
                yeildDecType, yeilDecDigits);
                **/
            // 塞值[当日帐套资产负债信息.当日年化收益率_费前]
            valStatBokPositionForToday.setTdyYieldBef(tdyYieldBefNow);

            // 当日帐套资产负债信息.当日年化收益率 = 当日帐套资产负债信息.当日年化收益率_费前
            valStatBokPositionForToday.setTdyYield(valStatBokPositionForToday.getTdyYieldBef());

            // 定义 变量.区间开始日
            Date partBeginDate = acctEntity.getVdate();
            // 定义 变量.周期开始日
            Date cycBeginDate = null;
            // 定义 变量.周期结束日
            Date cycEndDate = null;

            if(targetPerfRewardRule != null){
	            // 根据[变量.业绩报酬规则（标的_业绩报酬计提规则）.计提频率]进行判断
	            if (DictCodeList.计提类型_高频.getValue().equals(targetPerfRewardRule.getAccrType())) {
	                // 变量.区间开始日 = DTO.发生日
	                partBeginDate = acctEntity.getHappenDate();
	            }
	            else {
	                // 若[变量.业绩报酬计提计划]有值
	                if (targetPerfRewardAccrscheList != null
	                    && targetPerfRewardAccrscheList.size() > 0) {
	                    // 变量.区间开始日 = 变量.业绩报酬计提计划.get(size - 1).区间开始日+1
	                    partBeginDate = DateUtil.addDay(targetPerfRewardAccrscheList.get(
	                        targetPerfRewardAccrscheList.size() - 1).getAccrStrDate(), 1);
	                }
	            }
            }

			// 区间开始日
			position.setSdateStart(partBeginDate);
			// 区间结束日
			position.setSdateEnd(DateUtil.addDay(acctEntity.getHappenDate(), -1));
			// 变量.区间内帐套资产负债信息列表 =
			// 调用共通方法查询：帐套资产负债查询（变量.区间开始日，DTO.发生日-1，DTO.帐套代码，DTO.入账日）
			List<BokStatBokPosition> bokStatBokPositionList = accountCommonService.getValStatBokPosition(position);
			// 变量.区间内帐套资产负债信息列表.add（ 当日帐套资产负债信息）
			bokStatBokPositionList.add(valStatBokPositionForToday);
            
            // 定义 变量.7日区间内帐套资产负债信息列表
            List<BokStatBokPosition> bokStatBokPositionList7 = null;
            // if DTO.发生日 -DTO.开始日 >=6
            if (partBeginDate != null
                && acctEntity.getHappenDate().compareTo(DateUtil.addDay(acctEntity.getVdate(), 6)) > 0) {
                // 变量.周期开始日 = DTO.发生日-6
                cycBeginDate = DateUtil.addDay(acctEntity.getHappenDate(), -6);
                position.setSdateStart(cycBeginDate);
                // 变量.周期结束日 = DTO.发生日
                cycEndDate = acctEntity.getHappenDate();
                position.setSdateEnd(DateUtil.addDay(acctEntity.getHappenDate(), -1));
                if (!DateUtils.isSameDay(partBeginDate, cycBeginDate)) {
                    // 其他参数与初始化定义position时相同
                    // 变量.区间内帐套资产负债信息列表 = 调用共通方法查询：帐套资产负债查询（变量.区间开始日，DTO.发生日-1，DTO.帐套代码，DTO.入账日）
                	bokStatBokPositionList7 = accountCommonService.getValStatBokPosition(position);
                    // 变量.7日区间内帐套资产负债信息列表.add（ 当日帐套资产负债信息）
                	bokStatBokPositionList7.add(valStatBokPositionForToday);
                } else {
                	bokStatBokPositionList7 = bokStatBokPositionList;
                }
            }
            else {
                // 变量.周期开始日 = DTO.开始日
                cycBeginDate = acctEntity.getVdate();
                position.setSdateStart(cycBeginDate);
                // 变量.周期结束日 = DTO.发生日
                cycEndDate = acctEntity.getHappenDate();
                position.setSdateEnd(DateUtil.addDay(acctEntity.getHappenDate(), -1));
                if (!DateUtils.isSameDay(partBeginDate, cycBeginDate)) {
                    // 其他参数与初始化定义position时相同
                    // 变量.区间内帐套资产负债信息列表 = 调用共通方法查询：帐套资产负债查询（DTO.发生日-6，DTO.发生日-1，DTO.帐套代码，DTO.入账日）
                	bokStatBokPositionList7 = accountCommonService.getValStatBokPosition(position);
                    // 变量.7日区间内帐套资产负债信息列表.add（ 当日帐套资产负债信息）
                	bokStatBokPositionList7.add(valStatBokPositionForToday);
                } else {
                	bokStatBokPositionList7 = bokStatBokPositionList;
                }

            }
            
            // 周期内发生的红利再投查询（红利再投信息按照发生日升序排列，sql中实现的）
            // 定义 变量.红利再投信息 =
            // 调用共通方法查询：凭证最新账务查询（场景代码=“8034”，发生开始日=“变量.周期开始日”，发生结束日=“变量.周期结束日”，基础科目性质=“应付利润”，入账日=DTO.入账日，账套代码=DTO.账套代码）
            List<String> baseSubjectNatureListForHLZT = new ArrayList<String>();
            baseSubjectNatureListForHLZT.add(DictCodeBaseSubjectNatrueNew.应付利润.getValue());// 基础科目性质=“应付利润”
            List<BookLatestInfoDto> bookLatestInfoDtoListForHLZT = accountCommonService.getBookLatestInfo(
                acctEntity.getBooksetId(), acctEntity.getBookDate(), null,
                baseSubjectNatureListForHLZT, DictCodeListTableId.会计场景代码_产品_货币型_红利再投.getValue(), null,
                null, null, cycBeginDate, cycEndDate);

            // 组装区间年化收益率计算用数据
            // 当日帐套资产负债信息.周期年化收益率_费前 =
            // 当日帐套资产负债信息.周期年化收益率_费前 = 调用共通方法计算：周期年化收益率计算（变量.区间内帐套资产负债信息列表，变量.区间开始日，变量.区间结束日，变量.红利再投信息列表，变量.计算基础天数，费前费后区分=“费前”）
            List<BokStatBokPosition> bokStatBokPositionList_zq = null;
            if(bokStatBokPositionList!=null && bokStatBokPositionList.size()>0){
            	bokStatBokPositionList_zq = new ArrayList<BokStatBokPosition>();
            	for(BokStatBokPosition obj :bokStatBokPositionList){
            		BokStatBokPosition bokStatBokPosition = new BokStatBokPosition();
            		BeanUtil.copyPropertiesIgnoreNull(bokStatBokPosition, obj);
            		bokStatBokPosition.setpYield(accountCommonService.filterByDecTypeAndDigits(bokStatBokPosition.getpYield(), floatDecType,
            				floatDecDigits));
            		bokStatBokPositionList_zq.add(bokStatBokPosition);
            	}
            }
            BigDecimal yieldTermBefnow = calculationCommonService.getCysYearRate(
            		bokStatBokPositionList_zq, partBeginDate, acctEntity.getHappenDate(), bookLatestInfoDtoListForHLZT,
                BasisDays, ValDictCodeList.费前费后区分_费前.getValue());
            
            
            yieldTermBefnow = accountCommonService.filterByDecTypeAndDigits(yieldTermBefnow, floatCyclyearYieldType,
                    floatCyclyearYieldDigits);
            
            valStatBokPositionForToday.setYieldTermBef(yieldTermBefnow);
            
            // 当日帐套资产负债信息.周期年化收益率 = 当日帐套资产负债信息.周期年化收益率_费前
            valStatBokPositionForToday.setYieldTerm(valStatBokPositionForToday.getYieldTermBef());

            List<BokStatBokPosition> bokStatBokPositionList_7 = null;
            if(bokStatBokPositionList7!=null && bokStatBokPositionList7.size()>0){
            	bokStatBokPositionList_7 = new ArrayList<BokStatBokPosition>();
            	for(BokStatBokPosition obj :bokStatBokPositionList7){
            		BokStatBokPosition bokStatBokPosition = new BokStatBokPosition();
            		BeanUtil.copyPropertiesIgnoreNull(bokStatBokPosition, obj);
            		bokStatBokPosition.setpYield(accountCommonService.filterByDecTypeAndDigits(bokStatBokPosition.getpYield(), showBeforeDecType,
            				showBeforeDecDigits));
            		bokStatBokPositionList_7.add(bokStatBokPosition);
            	}
            }
            
            String carryoverFreq = targetCashPayRule!=null?targetCashPayRule.getPayCycle():null;
            // 组装区间七天年化计算用数据
            // 当日帐套资产负债信息.7日年化收益率_费前 =
            // 调用共通方法计算：7日年化收益率计算（变量.7日区间内帐套资产负债信息列表，变量.周期开始日，变量.周期结束日，变量.计算基础天数,标的信息.基金结转频率，费前费后区分=“费前”）
        	BigDecimal yield7BefNow = calculationCommonService.getSevenDayYearRate(bokStatBokPositionList_7,
                cycBeginDate, cycEndDate, BasisDays,carryoverFreq,
                ValDictCodeList.费前费后区分_费前.getValue());


            valStatBokPositionForToday.setYield7Bef(yield7BefNow);
            
            // 当日帐套资产负债信息.7日年化收益率 = 当日帐套资产负债信息.7日年化收益率_费前
            valStatBokPositionForToday.setYield7(valStatBokPositionForToday.getYield7Bef());
        }
        // 返回当日帐套资产负债信息
        BokstatbokpositionWriterDto returnVo = new BokstatbokpositionWriterDto();
        // 塞入账务明细数据（单条）
        returnVo.setBokStatBokPosition(valStatBokPositionForToday);

        return returnVo;
    }

    /**
     * 获取计提结束日金额
     * 
     * @param SubjectType 科目类别
     * @param SubjectNature 科目性质
     * @param expectedBalFalg 借贷方向
     * @param booksetId 账套代码
     * @param happenDate 发生日
     * @param bookDate 入账日
     * @param standardCcy 本位币
     * @param taskPri 任务优先级
     * @param bookOrder 处理序列
     * @param bokDetailIdList dto.核算主体代码 = dto.分层代码?null:子分层信息.账务明细代码
     * @return BigDecimal
     * @see
     * @since 1.0
     */
    private BigDecimal getPlanEndAmt(String SubjectType, String SubjectNature,
                                     String expectedBalFalg, String booksetId, Date happenDate,
                                     Date bookDate, String standardCcy, Long taskPri,
                                     BigDecimal bookOrder, List<String> bokDetailIdList) {

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId);// 账套代码
        searchDto.setHappenDate(happenDate);// 发生日
        searchDto.setBookDate(bookDate);// 入账日
        searchDto.setTaskPri(taskPri);// 任务优先级
        // 设置科目类型集合
        List<String> subjectTypeList = null;
        // 当传递的科目类别有值时，塞入科目类型集合中
        if (SubjectType != null && SubjectType.length() > 0) {
            subjectTypeList = new ArrayList<>();
            subjectTypeList.add(SubjectType);// 塞入传递的科目类别
        }
        searchDto.setSubjectTypeList(subjectTypeList);// 塞入科目类别列表

        // 设置基础科目性质集合
        List<String> subjectNatureList = null;
        // 当传递的科目性质有值时，塞入科目性质集合中
        if (SubjectNature != null && SubjectNature.length() > 0) {
            subjectNatureList = new ArrayList<>();
            subjectNatureList.add(SubjectNature);// 塞入传递的科目性质
        }
        searchDto.setBaseSubjectNatureList(subjectNatureList);// 科目性质列表
        // 处理序列
        searchDto.setBookOrder(bookOrder);
        // List<账务明细代码> = dto.核算主体代码 = dto.分层代码?null:子分层信息.账务明细代码
        searchDto.setBokDetailIdList(bokDetailIdList);
        // 科目余额汇总查询
        Map<String, BigDecimal> amt = accountCommonService.getSubjectBalanceCollection(searchDto,
            expectedBalFalg,DictCodeList.是否_否.getValue());

        // 定义计提结束日金额(默认为0)
        BigDecimal endPlaneAmt = BigDecimal.ZERO;

        // 赋值科目余额
        if (amt != null && amt.size() > 0) {
            // 调用方法：【详细设计-计算类共通方法设计】汇率折算（余额Map，DTO.本位币，DTO.发生日）
            endPlaneAmt = calculationCommonService.calcExchangeAmt(amt, standardCcy, happenDate);
        }

        return endPlaneAmt;
    }

    /**
     * 获取标的相关信息
     * 
     * @param finprodId 金融产品代码
     * @param vdate 起息日
     * @param happenDate 发生日
     * @return Map<String, Object>
     * @see
     * @since 1.0
     */
    private Map<String, Object> getTargetInfo(String finprodId, Date vdate, Date happenDate,
                                              String finprodType, Long branch) {

        // 定义返回值
        Map<String, Object> returnMap = new HashMap<String, Object>();

        // 查询类型
        List<TargetSearchDto> queryCriteria = new ArrayList<TargetSearchDto>();

        // 传入[标的_信息]Dto
        TargetSearchDto rstFinProduct = new TargetSearchDto();
        rstFinProduct.setFinprodId(finprodId);
        rstFinProduct.setFinprodType(finprodType);
        rstFinProduct.setBranch(branch);
        rstFinProduct.setEffdateStart(vdate);
        rstFinProduct.setEffdateEnd(happenDate);
        rstFinProduct.setQueryType(ValDictCodeList.标的_信息.getValue());
        queryCriteria.add(rstFinProduct);

        // 传入[标的_业绩报酬计算规则]Dto
        TargetSearchDto rstPerfRewardRule = new TargetSearchDto();
        rstPerfRewardRule.setFinprodId(finprodId);
        rstPerfRewardRule.setFinprodType(finprodType);
        rstPerfRewardRule.setCashType2(DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
        rstPerfRewardRule.setBranch(branch);
        rstPerfRewardRule.setEffdateStart(vdate);
        rstPerfRewardRule.setEffdateEnd(happenDate);
        rstPerfRewardRule.setQueryType(ValDictCodeList.标的_业绩报酬计提规则.getValue());
        queryCriteria.add(rstPerfRewardRule);

        // 传入[标的_业绩报酬计提计划]Dto
        TargetSearchDto rstPerfRewardAccrsche = new TargetSearchDto();
        rstPerfRewardAccrsche.setFinprodId(finprodId);
        rstPerfRewardAccrsche.setFinprodType(finprodType);
        rstPerfRewardAccrsche.setBranch(branch);
        rstPerfRewardAccrsche.setCashType2(DictCodeListExt1.现金流二级类型_浮动管理费.getValue());
        rstPerfRewardAccrsche.setEffdateStart(vdate);
        rstPerfRewardAccrsche.setEffdateEnd(happenDate);
        rstPerfRewardAccrsche.setQueryType(ValDictCodeList.标的_业绩报酬计提计划.getValue());
        queryCriteria.add(rstPerfRewardAccrsche);

        // 传入[标的_现金流计算规则]Dto
        TargetSearchDto rstCashCalcRule = new TargetSearchDto();
        rstCashCalcRule.setFinprodId(finprodId);
        rstCashCalcRule.setFinprodType(finprodType);
        rstCashCalcRule.setBranch(branch);
        rstCashCalcRule.setEffdateStart(vdate);
        rstCashCalcRule.setEffdateEnd(happenDate);
        rstCashCalcRule.setQueryType(ValDictCodeList.标的_现金流计算规则.getValue());
        queryCriteria.add(rstCashCalcRule);
        
        // 查询类型：现金流支付规则
        TargetSearchDto rstCashPayRule = new TargetSearchDto();
        rstCashPayRule.setFinprodId(finprodId);
        rstCashPayRule.setFinprodType(finprodType);
        rstCashPayRule.setBranch(branch);
        rstCashPayRule.setEffdateStart(vdate);
        rstCashPayRule.setEffdateEnd(happenDate);
        rstCashPayRule.setCashType1(DictCodeListExt1.现金流一级类型_利息.getValue());
        rstCashPayRule.setQueryType(ValDictCodeList.标的_现金流支付规则.getValue());
        // 查询实体类塞入查询实体类List中
        queryCriteria.add(rstCashPayRule);

        // 查询标的信息
        Map<TargetSearchDto, Object> targetInfoMap = finProductCommonService.getTargetInfo(
            finprodId, queryCriteria);

        // 有返回值数据
        if (!MapUtils.isEmpty(targetInfoMap)) {
            returnMap.put(ValDictCodeList.标的_信息.getValue(), targetInfoMap.get(rstFinProduct)); // 标的_信息list
            returnMap.put(ValDictCodeList.标的_业绩报酬计提规则.getValue(),
                targetInfoMap.get(rstPerfRewardRule)); // 标的_业绩报酬计提规则list
            returnMap.put(ValDictCodeList.标的_业绩报酬计提计划.getValue(),
                targetInfoMap.get(rstPerfRewardAccrsche)); // 标的_业绩报酬计提计划list
            returnMap.put(ValDictCodeList.标的_现金流计算规则.getValue(), targetInfoMap.get(rstCashCalcRule)); // 标的_现金流计算规则list
            returnMap.put(ValDictCodeList.标的_现金流支付规则.getValue(), targetInfoMap.get(rstCashPayRule)); // 标的_现金流支付规则list
        }

        return returnMap;
    }

}
