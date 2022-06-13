/*
 * 文件名：ProBenefitPrdValuationFeeBeforeProcessorService.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：估值核算系统V1.0
 * 修改人：niuqianyu 修改时间：2018年9月28日 修改内容：新建 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.expreturnprdvaluationfeebefore.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.entity.BokStatBokPosition;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListTableId;
import com.joyintech.tams.valuation.batch.common.accounting.entity.BookLatestInfoDto;
import com.joyintech.tams.valuation.batch.common.accounting.entity.SubjectBalanceSearchDto;
import com.joyintech.tams.valuation.batch.common.accounting.service.AccountingCommonService;
import com.joyintech.tams.valuation.batch.common.base.entity.BokStatBokPositionDto;
import com.joyintech.tams.valuation.batch.common.calculation.service.CalculationCommonService;
import com.joyintech.tams.valuation.batch.common.finproduct.service.FinProductCommonService;
import com.joyintech.tams.valuation.batch.expreturnprdvaluationfeebefore.dto.ExpReturnPrdValuationFeeBeforeReaderDto;
import com.joyintech.tams.valuation.batch.expreturnprdvaluationfeebefore.dto.ExpReturnPrdValuationFeeBeforeWriterDto;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetCashCalcRule;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.TargetSearchDto;
import com.joyintech.tams.valuation.common.constants.DictCodeBaseSubjectNatrueNew;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * （预期收益型产品估值计算-费前）-process业务处理
 * 
 * @author niuqianyu
 * @version 1.0 2018年9月28日
 * @see ExpReturnPrdValuationFeeBeforeProcessorNewService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ExpReturnPrdValuationFeeBeforeProcessorNewService {

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
     * 记账公共类
     */
    // @Autowired
    // private BookCommonLogic bookCommonLogic;

    /**
     * process 数据处理
     * 
     * @param param 传递参数
     * @return
     * @see
     * @since 1.0
     */
    // @SuppressWarnings("unchecked")
    @SuppressWarnings("unchecked")
    public ExpReturnPrdValuationFeeBeforeWriterDto execute(Map<String, Object> param) {
        // 获取reader返回值
        ExpReturnPrdValuationFeeBeforeReaderDto acctEntity = (ExpReturnPrdValuationFeeBeforeReaderDto)param.get(
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
        /*
         * Map<String, Object> getValBokBooksetExtMap = accountCommonService.getValBokBooksetExtForMap( acctEntity.getBooksetId(), paramCodeList);
         * 
         * // 获取估值小数位处理方式和估值小数位位数 String valDecType = (String)getValBokBooksetExtMap.get( ValDictCodeList.账套配置表参数项_估值小数位处理方式.getValue());// 估值小数位处理方式 String
         * valDecDigits = (String)getValBokBooksetExtMap.get( ValDictCodeList.账套配置表参数项_估值小数位位数.getValue());// 估值小数位位数
         */
        // 获取收益率小数位处理方式和收益率小数位位数
        // String yeildDecType = (String)getValBokBooksetExtMap.get(
        // ValDictCodeList.账套配置表参数项_收益率小数位处理方式.getValue());// 收益率小数位处理方式
        // String yeilDecDigits = (String)getValBokBooksetExtMap.get(
        // ValDictCodeList.账套配置表参数项_收益率小数位位数.getValue());// 收益率小数位位数

        // 获取标的信息
        Map<String, Object> targetInfo = this.getTargetInfo(acctEntity.getFinprodId(), acctEntity.getVdate(),
            acctEntity.getHappenDate());

        // 标的_信息
        // TargetFinProduct targetFinProduct = (TargetFinProduct)targetInfo.get(
        // ValDictCodeList.标的_信息.getValue());

        // 标的_业绩报酬计提规则
        // List<TargetPerfRewardRule> targetPerfRewardRuleList = (List<TargetPerfRewardRule>)targetInfo.get(
        // ValDictCodeList.标的_业绩报酬计提规则.getValue());
        // TargetPerfRewardRule targetPerfRewardRule = new TargetPerfRewardRule();
        // if (targetPerfRewardRuleList != null && targetPerfRewardRuleList.size() > 0) {
        // targetPerfRewardRule = targetPerfRewardRuleList.get(
        // targetPerfRewardRuleList.size() - 1);
        // }

        // 标的_业绩报酬计提计划
        // List<TargetPerfRewardAccrsche> targetPerfRewardAccrscheList = (List<TargetPerfRewardAccrsche>)targetInfo.get(
        // ValDictCodeList.标的_业绩报酬计提计划.getValue());

        // 标的_现金流计算规则
        List<TargetCashCalcRule> targetCashCalcRuleList = (List<TargetCashCalcRule>)targetInfo.get(
            ValDictCodeList.标的_现金流计算规则.getValue());
        TargetCashCalcRule targetCashCalcRule = new TargetCashCalcRule();
        if (targetCashCalcRuleList != null && targetCashCalcRuleList.size() > 0) {
            // 按日期降序排列取现金流一级类型为"利息"的第一条记录(当前sql中按照生效日期顺序排序，故逆序进行循环获取)
            for (int index = targetCashCalcRuleList.size() - 1; index >= 0; index-- ) {
                // 逆序获取第一个现金流一级类型为"利息"的数据
                if (DictCodeListExt1.现金流一级类型_利息.getValue().equals(targetCashCalcRuleList.get(index).getCashType1())) {
                    targetCashCalcRule = targetCashCalcRuleList.get(index);
                    break;
                }
            }
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
        // 分层代码
        position.setLayeringId(acctEntity.getLayeringId());
        // 分层代码
        position.setIsSubPrd(DictCodeList.是否_是.getValue());
        // 昨日帐套资产负债信息 初始化
        BokStatBokPosition valStatBokPositionForLastday = new BokStatBokPosition();
        // 昨日帐套资产负债信息 = 调用共通方法查询：帐套资产负债查询（DTO.发生日-1，DTO.发生日-1，DTO.帐套代码，DTO.入账日）
        List<BokStatBokPosition> valStatBokPositionForYesterDayList = accountCommonService.getValStatBokPosition(position);
        if (valStatBokPositionForYesterDayList != null && valStatBokPositionForYesterDayList.size() > 0) {
            valStatBokPositionForLastday = valStatBokPositionForYesterDayList.get(0);
        }
        // 当日帐套资产负债信息 = 调用共通方法查询：帐套资产负债查询（DTO.发生日，DTO.发生日，DTO.帐套代码，DTO.入账日）
        // 查询日开始
        position.setSdateStart(acctEntity.getHappenDate());
        // 查询结束日
        position.setSdateEnd(acctEntity.getHappenDate());
        List<BokStatBokPosition> valStatBokPositionForTodayList = accountCommonService.getValStatBokPosition(position);
        // 当日帐套资产负债信息 初始化
        BokStatBokPosition valStatBokPositionForToday = new BokStatBokPosition();
        if (valStatBokPositionForYesterDayList != null && valStatBokPositionForYesterDayList.size() > 0) {
            valStatBokPositionForToday = valStatBokPositionForTodayList.get(0);
        }
        // 处理金融产品代码信息：子产品赋值，其他赋为空
        // if (acctEntity.getPfinprodId() == null
        // || acctEntity.getPfinprodId().equals(acctEntity.getFinprodId())) {
        // // 当日帐套资产负债信息.金融产品代码 赋值为空
        // valStatBokPositionForToday.setFinprodId(null);
        // }
        // else {
        // // 当日帐套资产负债信息.金融产品代码 = DTO.金融产品代码
        // valStatBokPositionForToday.setFinprodId(acctEntity.getFinprodId());
        // }
        // 当日帐套资产负债信息.金融产品代码 = DTO.金融产品代码
        valStatBokPositionForToday.setFinprodId(acctEntity.getFinprodId());

        // 计提结束日实收资本余额 - 初始化
        BigDecimal endPlaneForRealCapital = BigDecimal.ZERO;
        // // 子产品账务明细代码为产品代码
        // if (StringUtils.isNotEmpty(valStatBokPositionForToday.getFinprodId())) {
        // // 调用共通设计->【详细设计-账务类共通方法设计.xlsx】->科目余额查询-期待方向SHEET
        // SubjectBalanceSearchDto subjectBalanceSearchDto = new SubjectBalanceSearchDto();
        // subjectBalanceSearchDto.setHappenDate(acctEntity.getHappenDate());
        // subjectBalanceSearchDto.setBookDate(acctEntity.getBookDate());
        // subjectBalanceSearchDto.setBokDetailId(acctEntity.getFinprodId());
        // subjectBalanceSearchDto.setBooksetId(acctEntity.getBooksetId());
        // List<String> baseSubjectNatureList = new ArrayList<>();
        // baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.应付利润.getValue());
        // subjectBalanceSearchDto.setBaseSubjectNatureList(baseSubjectNatureList);
        // subjectBalanceSearchDto.setTaskPri(acctEntity.getTaskPri());
        // List<SubjectBalance> subjectBalanceExpectDirection = accountCommonService.getSubjectBalanceExpectDirection(
        // subjectBalanceSearchDto, DictCodeList.余额方向_贷方.getValue());
        // // 计提结束日实收资本余额 = 上述返回科目余额
        // endPlaneForRealCapital = subjectBalanceExpectDirection.get(0).getBalance();
        // // 当日帐套资产负债信息.实收资本 = 计提结束日实收资本余额
        // valStatBokPositionForToday.setCapital(endPlaneForRealCapital);
        // }
        // else {

        /*
         * //调用共通方法：生成MD5账务明细代码 BookCommonLogic BokDetailId bokDetail = new BokDetailId(); bokDetail.setBooksetId(acctEntity.getBooksetId());
         * bokDetail.setBusiDetailType(acctEntity.getLayeringType()); //定义 变量.账务明细代码 = 返回的账务明细代码
         */ String bokDetailId = acctEntity.getBokDetailId();

        /*
         * // 获取【计提结束日损益类科目余额】 BigDecimal endPlaneForSjtAmt = this.getPlanEndAmt(bokDetailId,DictCodeList.科目类型_损益类.getValue(), null,
         * DictCodeList.余额方向_贷方.getValue(), acctEntity.getBooksetId(), acctEntity.getHappenDate(), acctEntity.getBookDate(), acctEntity.getStandardCcy(),
         * acctEntity.getTaskPri(), null,true);
         *
         * // 获取【计提结束日所有者权益余额】 BigDecimal endPlaneForOwnerProfit = this.getPlanEndAmt(bokDetailId, DictCodeList.科目类型_所有者权益类.getValue(), null,
         * DictCodeList.余额方向_贷方.getValue(), acctEntity.getBooksetId(), acctEntity.getHappenDate(), acctEntity.getBookDate(), acctEntity.getStandardCcy(),
         * acctEntity.getTaskPri(), null,true);
         */

        // 获取【计提结束日实收资本余额】
        endPlaneForRealCapital = this.getPlanEndAmt(bokDetailId, null, DictCodeBaseSubjectNatrueNew.实收资本.getValue(),
            DictCodeList.余额方向_贷方.getValue(), acctEntity.getBooksetId(), acctEntity.getHappenDate(), acctEntity.getBookDate(),
            acctEntity.getStandardCcy(), acctEntity.getTaskPri(), null, false);

        /*
         * // 定义 变量.计提结束日费前资产净值 = (计提结束日损益类余额 + 计提结束日所有者权益余额) BigDecimal endPlanAssetValue = ArithmeticUtil.doAdd(endPlaneForSjtAmt, endPlaneForOwnerProfit);
         * // 定义 变量.计提结束日费前单位净值 = 变量.费前资产净值 / 计提结束日实收资本余额 BigDecimal endPlanUnitValue = ArithmeticUtil.divide(endPlanAssetValue, endPlaneForRealCapital); //
         * 对【计提结束日费前单位净值】进行估值小数位处理:根据 参数.估值小数位处理方式 + 参数.估值小数位位数 处理 endPlanUnitValue = accountCommonService.filterByDecTypeAndDigits(endPlanUnitValue,
         * valDecType, valDecDigits);
         */

        // 当日帐套资产负债信息
        // 当日帐套资产负债信息.实收资本 = 计提结束日实收资本余额
        valStatBokPositionForToday.setCapital(endPlaneForRealCapital);
        /*
         * //当日帐套资产负债信息.费前资产净值 = 变量.计提结束日费前资产净值 valStatBokPositionForToday.setNetAssetValueBef(endPlanAssetValue); //当日帐套资产负债信息.资产净值 = 当日帐套资产负债信息.费前资产净值
         * valStatBokPositionForToday.setNetAssetValue( valStatBokPositionForToday.getNetAssetValueBef()); //当日帐套资产负债信息.费前单位净值 = 计提结束日费前单位净值
         * valStatBokPositionForToday.setNetUnitValueBef(endPlanUnitValue); //当日帐套资产负债信息.单位净值 = 计提结束日费前单位净值
         * valStatBokPositionForToday.setNetUnitValue(endPlanUnitValue);
         */
        // 当日帐套资产负债信息.收益类型=账套.核算方式
        valStatBokPositionForToday.setProfitType(acctEntity.getAcctmode());
        // }'
        /*
         * //前一日应付利润余额 = 上述返回科目余额 BigDecimal varYedYfye = this.getPlanEndAmt(bokDetailId,null, DictCodeBaseSubjectNatrueNew.应付利润.getValue(),
         * DictCodeList.余额方向_贷方.getValue(), acctEntity.getBooksetId(), DateUtil.addDay(acctEntity.getHappenDate(),-1),
         * DateUtil.addDay(acctEntity.getBookDate(), -1), acctEntity.getStandardCcy(), acctEntity.getTaskPri(), null,false);
         *
         * //调用共通方法：科目余额汇总查询 //计提结束日应付利润余额 = 上述返回科目余额 BigDecimal varYfye = this.getPlanEndAmt(bokDetailId,null, DictCodeBaseSubjectNatrueNew.应付利润.getValue(),
         * DictCodeList.余额方向_贷方.getValue(), acctEntity.getBooksetId(), acctEntity.getHappenDate(), acctEntity.getBookDate(), acctEntity.getStandardCcy(),
         * acctEntity.getTaskPri(), null,false);
         *
         * //当日帐套资产负债信息.应付利润 = 计提结束日应付利润余额 - 前一日应付利润余额 valStatBokPositionForToday.setProfitAmt(ArithmeticUtil.subtract(varYfye, varYedYfye));
         */

        BigDecimal profitAmt = BigDecimal.ZERO;// 当日应付利润
        BigDecimal totProfitAmt = BigDecimal.ZERO;// 累计应付利润
        if (DateUtil.compareDate(acctEntity.getHappenDate(), acctEntity.getVdate()) > 0) {

            totProfitAmt = valStatBokPositionForLastday.getTotProfitAmt();
            // 调用共通方法查询：凭证最新账务查询（场景代码=“8005”，发生日=DTO.发生日，入账日=DTO.入账日，账套代码=DTO.账套代码）
            // 定义基础科目性质list
            List<String> baseSubjectNatureList = new ArrayList<String>();
            baseSubjectNatureList.add(DictCodeBaseSubjectNatrueNew.应付利润.getValue());
            List<BookLatestInfoDto> endProfitList = accountCommonService.getBookLatestInfo(acctEntity.getBooksetId(),
                acctEntity.getBookDate(), bokDetailId, baseSubjectNatureList, DictCodeListTableId.会计场景代码_产品_预期收益型_兑付客户收益.getValue(),
                null, acctEntity.getHappenDate(), null, null, null);
            for (BookLatestInfoDto vo : endProfitList) {
                profitAmt = ArithmeticUtil.doAdd(profitAmt, vo.getoHappenAmt());
            }

            // 当日帐套资产负债信息.当日应付利润 = 变量.当日应付利润
            valStatBokPositionForToday.setProfitAmt(profitAmt);
            // 当日帐套资产负债信息.累计应付利润 = 变量.累计应付利润 + 变量.当日应付利润
            valStatBokPositionForToday.setTotProfitAmt(ArithmeticUtil.doAdd(totProfitAmt, profitAmt));

            // 当日帐套资产负债信息.计提利率 = 变量.现金流计算规则.生效利率
            valStatBokPositionForToday.setIntRate(targetCashCalcRule.getYield());
        }

        // IF DTO.核算主体类别 =“理财产品”
        if (DictCodeListExt1.金融产品类型_理财产品.getValue().equals(acctEntity.getBusiDetailType())
            && DictCodeList.收益类型_收益型.getValue().equals(acctEntity.getProfitType())) {
            // 当日帐套资产负债信息.计提利率 = 变量.现金流计算规则.生效利率
            valStatBokPositionForToday.setIntRate(targetCashCalcRule.getYield());
        }

        // 返回当日帐套资产负债信息
        ExpReturnPrdValuationFeeBeforeWriterDto returnVo = new ExpReturnPrdValuationFeeBeforeWriterDto();
        returnVo.setBokStatBokPosition(valStatBokPositionForToday);
        return returnVo;
    }

    /**
     * 获取计提结束日金额
     * 
     * @param bokDetailId 账务明细代码
     * @param SubjectType 科目类别
     * @param SubjectNature 科目性质
     * @param expectedBalFalg 借贷方向
     * @param booksetId 账套代码
     * @param happenDate 发生日
     * @param bookDate 入账日
     * @param standardCcy 本位币
     * @param taskPri 任务优先级
     * @param bookOrder 处理序列
     * @param flag 是否调用汇率折算
     * @return BigDecimal
     * @see
     * @since 1.0
     */
    private BigDecimal getPlanEndAmt(String bokDetailId, String SubjectType, String SubjectNature, String expectedBalFalg,
                                     String booksetId, Date happenDate, Date bookDate, String standardCcy, Long taskPri,
                                     BigDecimal bookOrder, boolean flag) {

        // 设值科目余额查询参数Dto
        SubjectBalanceSearchDto searchDto = new SubjectBalanceSearchDto();
        searchDto.setBooksetId(booksetId);// 账套代码
        searchDto.setHappenDate(happenDate);// 发生日
        searchDto.setBookDate(bookDate);// 入账日
        searchDto.setTaskPri(taskPri);// 任务优先级
        searchDto.setBokDetailId(bokDetailId);// 账务明细代码

        // 设置科目类型集合
        List<String> subjectTypeList = null;
        // 当传递的科目类别有值时，塞入科目类型集合中
        if (StringUtils.isNotEmpty(SubjectType)) {
            subjectTypeList = new ArrayList<>();
            subjectTypeList.add(SubjectType);// 塞入传递的科目类别
        }
        searchDto.setSubjectTypeList(subjectTypeList);// 塞入科目类别列表

        // 设置基础科目性质集合
        List<String> subjectNatureList = null;
        // 当传递的科目性质有值时，塞入科目性质集合中
        if (StringUtils.isNotEmpty(SubjectNature)) {
            subjectNatureList = new ArrayList<>();
            subjectNatureList.add(SubjectNature);// 塞入传递的科目性质
        }
        searchDto.setBaseSubjectNatureList(subjectNatureList);// 科目性质列表
        // 处理序列
        searchDto.setBookOrder(bookOrder);
        // 科目余额汇总查询
        Map<String, BigDecimal> amt = accountCommonService.getSubjectBalanceCollection(searchDto, expectedBalFalg,null);

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
    private Map<String, Object> getTargetInfo(String finprodId, Date vdate, Date happenDate) {

        // 定义返回值
        Map<String, Object> returnMap = new HashMap<String, Object>();

        // 查询类型
        List<TargetSearchDto> queryCriteria = new ArrayList<TargetSearchDto>();

        // 传入[标的_信息]Dto
        TargetSearchDto rstFinProduct = new TargetSearchDto();
        rstFinProduct.setEffdateStart(vdate);
        rstFinProduct.setEffdateEnd(happenDate);
        rstFinProduct.setQueryType(ValDictCodeList.标的_信息.getValue());
        queryCriteria.add(rstFinProduct);

        // 传入[标的_业绩报酬计算规则]Dto
        TargetSearchDto rstPerfRewardRule = new TargetSearchDto();
        rstPerfRewardRule.setEffdateStart(vdate);
        rstPerfRewardRule.setEffdateEnd(happenDate);
        rstPerfRewardRule.setQueryType(ValDictCodeList.标的_业绩报酬计提规则.getValue());
        queryCriteria.add(rstPerfRewardRule);

        // 传入[标的_业绩报酬计提计划]Dto
        TargetSearchDto rstPerfRewardAccrsche = new TargetSearchDto();
        rstPerfRewardAccrsche.setEffdateStart(vdate);
        rstPerfRewardAccrsche.setEffdateEnd(happenDate);
        rstPerfRewardAccrsche.setQueryType(ValDictCodeList.标的_业绩报酬计提计划.getValue());
        queryCriteria.add(rstPerfRewardAccrsche);

        // 传入[标的_现金流计算规则]Dto
        TargetSearchDto rstCashCalcRule = new TargetSearchDto();
        rstCashCalcRule.setEffdateStart(vdate);
        rstCashCalcRule.setEffdateEnd(happenDate);
        rstCashCalcRule.setQueryType(ValDictCodeList.标的_现金流计算规则.getValue());
        queryCriteria.add(rstCashCalcRule);

        // 查询标的信息
        Map<TargetSearchDto, Object> targetInfoMap = finProductCommonService.getTargetInfo(finprodId, queryCriteria);

        // 有返回值数据
        if (!MapUtils.isEmpty(targetInfoMap)) {
            returnMap.put(ValDictCodeList.标的_信息.getValue(), targetInfoMap.get(rstFinProduct)); // 标的_信息list
            returnMap.put(ValDictCodeList.标的_业绩报酬计提规则.getValue(), targetInfoMap.get(rstPerfRewardRule)); // 标的_业绩报酬计提规则list
            returnMap.put(ValDictCodeList.标的_业绩报酬计提计划.getValue(), targetInfoMap.get(rstPerfRewardAccrsche)); // 标的_业绩报酬计提计划list
            returnMap.put(ValDictCodeList.标的_现金流计算规则.getValue(), targetInfoMap.get(rstCashCalcRule)); // 标的_现金流计算规则list
        }

        return returnMap;
    }

}
