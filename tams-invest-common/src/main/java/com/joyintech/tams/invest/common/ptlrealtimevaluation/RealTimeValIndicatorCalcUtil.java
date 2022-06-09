/*
 * 文件名：RealTimeValIndicatorCalcUtil.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：etliu
 * 修改时间：2020年4月15日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.ptlrealtimevaluation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

import com.joyintech.tams.constant.DictCodeListExt1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinOpenPlan;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.dto.mock.asset.MockAssetInfoDto;
import com.joyintech.tams.invest.common.dto.mock.asset.MockFinCashPlanDto;
import com.joyintech.tams.invest.common.dto.mock.basicdata.MockMstExchargeQuoteDto;
import com.joyintech.tams.invest.common.dto.mock.calcindicator.IndicatorName;
import com.joyintech.tams.invest.common.dto.mock.portfolio.MockPtlCashPositionDto;
import com.joyintech.tams.invest.common.dto.mock.portfolio.MockPtlPortfolioDto;
import com.joyintech.tams.invest.common.dto.mock.portfolio.MockPtlPortfolioValuationDto;
import com.joyintech.tams.invest.common.dto.mock.portfolio.MockPtlSecValuationDto;
import com.joyintech.tools.collectors.JoyinCollectors;

/**
 * 组合实时估值-计算指标工具类
 * 
 * @author etliu
 * @version 1.0 2020年4月15日
 * @see RealTimeValIndicatorCalcUtil
 * @since 1.0
 */
public class RealTimeValIndicatorCalcUtil {

    private static Logger logger = LoggerFactory.getLogger(RealTimeValIndicatorCalcUtil.class);
    /**
     * 计算指标-主方法
     * 
     * @param mockPtlPortfolioDto 组合信息
     * @param mockPtlSecValuationDtoList 组合资产估值列表
     * @param mockPtlPortfolioValuationDtoList 组合估值列表
     * @param mockPtlCashPositionDto 组合剩余现金
     * @param mockAssetInfoDtoList 资产基本信息列表
     * @param exchargeQuoteList 汇率行情列表
     * @param indicatorNameList 指标名称列表
     * @param calcDate 计算日期
     * @return 指标计算结果Map
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public static Map<IndicatorName, Object> calcIndicator(MockPtlPortfolioDto mockPtlPortfolioDto,
                                                           List<MockPtlSecValuationDto> mockPtlSecValuationDtoList,
                                                           List<MockPtlPortfolioValuationDto> mockPtlPortfolioValuationDtoList,
                                                           MockPtlCashPositionDto mockPtlCashPositionDto,
                                                           List<MockAssetInfoDto> mockAssetInfoDtoList,
                                                           List<MockMstExchargeQuoteDto> exchargeQuoteList,
                                                           List<IndicatorName> indicatorNameList,
                                                           Date calcDate) {
        if (StringUtil.isEmptyList(indicatorNameList) || calcDate == null) {
            return null;
        }

        // 将入参.组合估值列表转换成变量.组合估值Map
        Map<Date, MockPtlPortfolioValuationDto> ptlValuationDtoMap = mockPtlPortfolioValuationDtoList.stream().collect(
            JoyinCollectors.toMap(x -> x.getCdate(), x -> x));

        // 取变量.组合估值Map 中日期为入参.计算日期的数据→计算日期组合估值Dto
        MockPtlPortfolioValuationDto ptlValuationDto = ptlValuationDtoMap.get(calcDate);

        // 指标计算结果Map
        Map<IndicatorName, Object> calcResultMap = new HashMap<IndicatorName, Object>();

        // 计息基础
        int basisNum = 365;
        if (mockPtlPortfolioDto != null && StringUtil.equals(DictCodeList.计息基础_A_360.getValue(),
            mockPtlPortfolioDto.getBasis())) {
            basisNum = 360;
        }

        // 循环：指标名称列表
        for (IndicatorName indicatorName : indicatorNameList) {
            if (IndicatorName.单位净值.equals(indicatorName)) {
                if (ptlValuationDto != null && StringUtil.equals(DictCodeList.收益类型_净值型.getValue(),
                    mockPtlPortfolioDto.getProfitType())) {
                    // 指标计算结果Map.put(单位净值, 计算日期组合估值Dto.当日单位净值)
                    calcResultMap.put(indicatorName, ptlValuationDto.getTdyUnitValue());
                }
            }
            else if (IndicatorName.万份收益.equals(indicatorName)) {
                if (ptlValuationDto != null && StringUtil.equals(DictCodeList.收益类型_货币型.getValue(),
                    mockPtlPortfolioDto.getProfitType())) {
                    // 指标计算结果Map.put(单位净值, 计算日期组合估值Dto.当日单位净值)
                    calcResultMap.put(indicatorName, ptlValuationDto.getTdyUnitValue());
                }
            }
            else if (IndicatorName.剩余现金.equals(indicatorName)) {
                if (mockPtlCashPositionDto != null) {
                    // 指标计算结果Map.put(剩余现金, 入参.组合剩余现金.金额)
                    calcResultMap.put(indicatorName, mockPtlCashPositionDto.getAmount());
                }
            }
            else if (IndicatorName.涨跌幅.equals(indicatorName)) {
                if (StringUtil.equals(DictCodeList.收益类型_净值型.getValue(),
                    mockPtlPortfolioDto.getProfitType())) {
                    // 取变量.组合估值Map 中日期为入参.计算日期前一天的数据→计算日期前一天组合估值Dto
                    MockPtlPortfolioValuationDto lastDayPtlValuationDto = ptlValuationDtoMap.get(
                        DateUtil.addDay(calcDate, -1));
                    if (ptlValuationDto != null) {
                        BigDecimal 计算日期前一天单位净值 = BigDecimal.ONE;
                        if (lastDayPtlValuationDto != null&&lastDayPtlValuationDto.getTdyUnitValue()!=null) {
                            计算日期前一天单位净值 = lastDayPtlValuationDto.getTdyUnitValue();
                        }

                        // 指标值 = （计算日期组合估值Dto.当日单位净值/计算日期前一天组合估值Dto.当日单位净值 - 1）*100%
                        // 保留2位小数
                        BigDecimal 指标值 = ArithmeticUtil.subtract(ArithmeticUtil.divide(
                            ptlValuationDto.getTdyUnitValue()==null?BigDecimal.ONE:ptlValuationDto.getTdyUnitValue(), 计算日期前一天单位净值, 4), BigDecimal.ONE);
                        calcResultMap.put(indicatorName, 指标值);
                    }
                }
            }
            else if (IndicatorName.七日年化收益率.equals(indicatorName)) {
                calcResultMap.put(indicatorName,
                    calc七日年化收益率(mockPtlPortfolioDto.getProfitType(),
                        mockPtlPortfolioDto.getVdate(), mockPtlPortfolioDto.getBonusPayCycle(),
                        basisNum, ptlValuationDtoMap, calcDate));
            }
            else if (IndicatorName.总资产.equals(indicatorName)) {
                if (ptlValuationDto != null) {
                    // 指标计算结果Map.put(总资产, 计算日期组合估值Dto.当日资产总值)
                    calcResultMap.put(indicatorName, ptlValuationDto.getTdyAssetValue());
                }
            }
            else if (IndicatorName.净资产.equals(indicatorName)) {
                if (ptlValuationDto != null) {
                    // 指标计算结果Map.put(净资产, 计算日期组合估值Dto.当日资产净值)
                    calcResultMap.put(indicatorName, ptlValuationDto.getTdyAssetNetvalue());
                }
            }
            else if (IndicatorName.杠杆率.equals(indicatorName)) {
                if (ptlValuationDto != null) {
                    // 指标值 = 计算日期组合估值Dto.当日资产总值/计算日期组合估值Dto.当日资产净值 * 100%
                    // 保留2位小数
                    BigDecimal 指标值 = ArithmeticUtil.divide(ptlValuationDto.getTdyAssetValue(),
                        ptlValuationDto.getTdyAssetNetvalue(), 4);
                    calcResultMap.put(indicatorName, 指标值);
                }
            }
            else if (IndicatorName.基点价值.equals(indicatorName)) {
                // TODO
            }
            else if (IndicatorName.平均剩余期限.equals(indicatorName)) {
                if (ptlValuationDto != null && StringUtil.equals(DictCodeList.收益类型_货币型.getValue(),
                    mockPtlPortfolioDto.getProfitType())) {
                    calcResultMap.put(indicatorName,
                        calc久期_common(ptlValuationDto, mockPtlSecValuationDtoList,
                            mockAssetInfoDtoList, exchargeQuoteList, mockPtlPortfolioDto.getCcy(),
                            calcDate, indicatorName));
                }
            }
            else if (IndicatorName.平均剩余存续期限.equals(indicatorName)) {
                if (ptlValuationDto != null && StringUtil.equals(DictCodeList.收益类型_货币型.getValue(),
                    mockPtlPortfolioDto.getProfitType())) {
                    calcResultMap.put(indicatorName,
                        calc久期_common(ptlValuationDto, mockPtlSecValuationDtoList,
                            mockAssetInfoDtoList, exchargeQuoteList, mockPtlPortfolioDto.getCcy(),
                            calcDate, indicatorName));
                }
            }
            else if (IndicatorName.组合久期.equals(indicatorName)) {
                calcResultMap.put(indicatorName,
                    calc久期_common(ptlValuationDto, mockPtlSecValuationDtoList,
                        mockAssetInfoDtoList, exchargeQuoteList, mockPtlPortfolioDto.getCcy(),
                        calcDate, indicatorName));
            }
            else if (IndicatorName.一月年化收益率.equals(indicatorName)) {
                calcResultMap.put(indicatorName,
                    calc一月年化收益率(mockPtlPortfolioDto.getProfitType(),
                        mockPtlPortfolioDto.getVdate(), mockPtlPortfolioDto.getBonusPayCycle(),
                        basisNum, ptlValuationDtoMap, calcDate));
            }
            else if (IndicatorName.三月年化收益率.equals(indicatorName)) {
                calcResultMap.put(indicatorName,
                    calc三月年化收益率(mockPtlPortfolioDto.getProfitType(),
                        mockPtlPortfolioDto.getVdate(), mockPtlPortfolioDto.getBonusPayCycle(),
                        basisNum, ptlValuationDtoMap, calcDate));
            }
            else if (IndicatorName.至下一季报日收益率.equals(indicatorName)) {
                calcResultMap.put(indicatorName,
                    calc至下一季报日收益率(basisNum,mockPtlPortfolioDto.getProfitType(),
                        mockPtlPortfolioDto.getVdate(), mockPtlPortfolioDto.getMdate(),
                        ptlValuationDtoMap, calcDate));
            }
            else if (IndicatorName.一年收益率.equals(indicatorName)) {
                calcResultMap.put(indicatorName,
                    calc一年收益率(mockPtlPortfolioDto.getProfitType(), mockPtlPortfolioDto.getVdate(),
                        mockPtlPortfolioDto.getBonusPayCycle(), basisNum, ptlValuationDtoMap,
                        calcDate));
            }
            else if (IndicatorName.到期收益率.equals(indicatorName)) {
                calcResultMap.put(indicatorName,
                    calc到期收益率(basisNum,mockPtlPortfolioDto.getProfitType(),
                        mockPtlPortfolioDto.getPaPtlPortfolio().getOpenType(),
                        mockPtlPortfolioDto.getVdate(), mockPtlPortfolioDto.getMdate(),
                        mockPtlPortfolioDto.getFinOpenPlanList(), ptlValuationDtoMap, calcDate));
            }
        }

        return calcResultMap;
    }

    /**
     * 计算区间指标-主方法
     * 
     * @param mockPtlPortfolioDto 组合信息
     * @param mockPtlSecValuationDtoList 组合资产估值列表
     * @param mockPtlPortfolioValuationDtoList 组合估值列表
     * @param mockPtlCashPositionDto 组合剩余现金
     * @param mockAssetInfoDtoList 资产基本信息列表
     * @param exchargeQuoteList 汇率行情列表
     * @param indicatorNameList 指标名称列表
     * @param calcDateStart 计算日期起始日
     * @param calcDateEnd 计算日期终止日
     * @return 指标计算结果Map
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public static Map<IndicatorName, Object> calcPeriodIndicator(MockPtlPortfolioDto mockPtlPortfolioDto,
                                                                 List<MockPtlSecValuationDto> mockPtlSecValuationDtoList,
                                                                 List<MockPtlPortfolioValuationDto> mockPtlPortfolioValuationDtoList,
                                                                 MockPtlCashPositionDto mockPtlCashPositionDto,
                                                                 List<MockAssetInfoDto> mockAssetInfoDtoList,
                                                                 List<MockMstExchargeQuoteDto> exchargeQuoteList,
                                                                 List<IndicatorName> indicatorNameList,
                                                                 Date calcDateStart,
                                                                 Date calcDateEnd) {
        if (StringUtil.isEmptyList(indicatorNameList) || calcDateStart == null
            || calcDateEnd == null) {
            return null;
        }

        // 将入参.组合估值列表转换成变量.组合估值Map
        Map<Date, MockPtlPortfolioValuationDto> ptlValuationDtoMap = mockPtlPortfolioValuationDtoList.stream().collect(
            JoyinCollectors.toMap(x -> x.getCdate(), x -> x));

        // 指标计算结果Map
        Map<IndicatorName, Object> calcResultMap = new HashMap<IndicatorName, Object>();

        // 计息基础
        int basisNum = 365;
        if (mockPtlPortfolioDto != null && StringUtil.equals(DictCodeList.计息基础_A_360.getValue(),
                mockPtlPortfolioDto.getBasis())) {
            basisNum = 360;
        }
        if (mockPtlPortfolioDto != null){
            // 循环：指标名称列表
            for (IndicatorName indicatorName : indicatorNameList) {
                if (IndicatorName.区间收益率.equals(indicatorName)) {
                    calcResultMap.put(indicatorName, calc区间收益率(basisNum,mockPtlPortfolioDto.getProfitType(),
                            calcDateStart, calcDateEnd, ptlValuationDtoMap));
                }
            }
        }
        return calcResultMap;
    }

    /**
     * calc久期_common 平均剩余期限、平均剩余存续期限、组合久期 计算共通
     * 
     * @param mockPtlPortfolioValuationDto 组合估值
     * @param mockPtlSecValuationDtoList 组合资产估值列表
     * @param mockAssetInfoDtoList 资产基本信息列表
     * @param exchargeQuoteList 汇率行情列表
     * @param ccy 组合募集币种
     * @param calcDate 计算日期
     * @param indicatorName 计算指标名称
     * @return 平均剩余期限、平均剩余存续期限、组合久期
     */
    public static BigDecimal calc久期_common(MockPtlPortfolioValuationDto mockPtlPortfolioValuationDto,
                                           List<MockPtlSecValuationDto> mockPtlSecValuationDtoList,
                                           List<MockAssetInfoDto> mockAssetInfoDtoList,
                                           List<MockMstExchargeQuoteDto> exchargeQuoteList,
                                           String ccy, Date calcDate, IndicatorName indicatorName) {
        if (StringUtil.isEmptyList(mockPtlSecValuationDtoList)) {
            return null;
        }

        // 计算日当天的组合资产估值列表
        List<MockPtlSecValuationDto> currSecValuationDtoList = mockPtlSecValuationDtoList.stream().filter(
            x -> DateUtil.isSameDate(calcDate, x.getCdate())).collect(Collectors.toList());
        if (StringUtil.isEmptyList(currSecValuationDtoList)) {
            return null;
        }

        // 资产基本信息列表转成map
        Map<String, MockAssetInfoDto> assetInfoMap = mockAssetInfoDtoList.stream().collect(
            JoyinCollectors.toMap(x -> x.getFinprodId(), x -> x));

        BigDecimal 组合总资产 = mockPtlPortfolioValuationDto!=null?mockPtlPortfolioValuationDto.getTdyAssetValue():null;
        BigDecimal 分子 = BigDecimal.ZERO;
        for (MockPtlSecValuationDto mockPtlSecValuationDto : currSecValuationDtoList) {
            MockAssetInfoDto currAssetInfo = assetInfoMap.get(
                mockPtlSecValuationDto.getFinprodId());
            if (currAssetInfo == null) {
                continue;
            }

            String 资产负债标识 = RealTimeValCommonUtil.judge资产负债标识(currAssetInfo.getFinProductType());
            if (StringUtil.equals(RealTimeValCommonUtil.资产负债标识_资产, 资产负债标识)) {
                BigDecimal 计算期限 = null;
                if (IndicatorName.平均剩余期限.equals(indicatorName)) {
                    计算期限 = calc剩余期限(currAssetInfo, calcDate);
                }
                else if (IndicatorName.平均剩余存续期限.equals(indicatorName)) {
                    计算期限 = calc剩余存续期限(currAssetInfo, calcDate);
                }
                else if (IndicatorName.组合久期.equals(indicatorName)) {
                    计算期限 = calc资产久期(currAssetInfo, mockPtlSecValuationDto.getYield(), calcDate);
                }

                BigDecimal 资产估值 = mockPtlSecValuationDto.getAssetValue();
                if (资产估值 == null) {
                    // 调用共通计算资产价值
                    MockPtlSecValuationDto tempPtlSecValuationDto = new MockPtlSecValuationDto();
                    BeanUtil.copyPropertiesIgnoreNull(tempPtlSecValuationDto, mockPtlSecValuationDto);
                    RealTimeValCommonUtil.calc资产价值(tempPtlSecValuationDto, ccy, exchargeQuoteList);
                    资产估值 = tempPtlSecValuationDto.getAssetValue();
                }
                if (IndicatorName.组合久期.equals(indicatorName)) {
                    logger.info(currAssetInfo.getFinprodId() + "计算期限" + 计算期限 + "资产估值" + 资产估值);
                }
//                组合总资产 = ArithmeticUtil.doAdd(组合总资产, 资产估值);
                分子 = ArithmeticUtil.doAdd(分子,ArithmeticUtil.multiply(计算期限, 资产估值));
            }

        }

        BigDecimal 计算结果 = null;
        if (组合总资产 != null&&BigDecimal.ZERO.compareTo(组合总资产)!=0) {
            计算结果 = ArithmeticUtil.divide(分子, 组合总资产, 8);
        }

        return 计算结果;
    }

    /**
     * 计算剩余期限
     * 
     * @param assetInfo 资产基本信息
     * @param calcDate 计算日
     * @return 剩余期限
     */
    public static BigDecimal calc剩余期限(MockAssetInfoDto assetInfo, Date calcDate) {
        BigDecimal 计算结果 = null;

        Date endDate = calc到期日(assetInfo, calcDate);

        // 默认：(到期日-计算日)/365；
        if (计算结果 == null && endDate != null && DateUtil.compareDate(endDate, calcDate) >= 0) {
            计算结果 = ArithmeticUtil.divide(
                    BigDecimal.valueOf(DateUtil.getBetweenDayNumber(calcDate, endDate)),
                    BigDecimal.valueOf(365), 12);
        }

        return 计算结果;
    }

    /**
     * 计算到期日
     *
     * @param assetInfo 资产基本信息
     * @param calcDate 计算日
     * @return 到期日
     */
    public static Date calc到期日(MockAssetInfoDto assetInfo, Date calcDate) {
        // 资产到期日
        Date endDate = null;
        if (assetInfo.getFinProduct() != null) {
            endDate = assetInfo.getFinProduct().getMdate();
        } else if (assetInfo.getFinTradeProduct() != null) {
            endDate = assetInfo.getFinTradeProduct().getMdate();
        }
        String 投管资产大类 = assetInfo.getTgAssetType();

        if (StringUtil.equals(DictCodeList.平安资产类型_债券.getValue(), 投管资产大类)) {
            String calType = null; // 利率类型
            BigDecimal 当期票面利率 = null;
            Date 下一重定价日 = null;
            if (!StringUtil.isEmptyList(assetInfo.getFinCashCalcRuleList())) {
                calType = assetInfo.getFinCashCalcRuleList().get(0).getCalcType(); // 利率类型
                当期票面利率 = assetInfo.getFinCashCalcRuleList().get(0).getYield();
                下一重定价日 = assetInfo.getFinCashCalcRuleList().get(0).getEffDate();
            }
            if (!StringUtil.isEmptyList(assetInfo.getFinCashCalcRuleList())
                    && assetInfo.getFinCashCalcRuleList().size() > 1
                    && DateUtil.compareDate(下一重定价日, calcDate) <= 0) {
                calType = assetInfo.getFinCashCalcRuleList().get(1).getCalcType(); // 利率类型
                当期票面利率 = assetInfo.getFinCashCalcRuleList().get(1).getYield();
                下一重定价日 = assetInfo.getFinCashCalcRuleList().get(1).getEffDate();
            }
            // 永续债（X+N) TODO
            if (StringUtil.equals(DictCodeList.是否_是.getValue(),
                    assetInfo.getFinProduct().getIsSus())) {
                if (!StringUtil.isEmptyList(assetInfo.getResaleFinExercisePlanList())) {
                    // 回售行权的时候取下一行权日
                    endDate = assetInfo.getResaleFinExercisePlanList().get(0).getExerciseDate();
                }
            }
            // 含权债（回售、赎回）(X+Y)
            else if (StringUtil.equals(DictCodeList.是否_是.getValue(),
                    assetInfo.getFinProduct().getIsRight())) {
                if (!StringUtil.isEmptyList(assetInfo.getResaleFinExercisePlanList())) {
                    // 按行权日计算的久期：
                    // 含回售权：久期=（下一行权日-计算日）/365；如无下一行权日，则取到期日；
                    // 含赎回权：久期=（（到期日-计算日）/365；
                    // 若同时含两个权，优先采用回售权的下一个行权日 
                    endDate = assetInfo.getResaleFinExercisePlanList().get(0).getExerciseDate();
                }
            }

            // 浮息债
            else if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(),
                    assetInfo.getFinProduct().getCouponSpecies())
                    && StringUtil.equals(DictCodeList.利率类型_浮动利率.getValue(), calType)) {
                endDate = 下一重定价日;
                //如果下一重定价日 <= 当前计算日，则 endDate = 资产到期日
                if (DateUtil.compareDate(endDate, calcDate) <= 0) {
                    endDate = assetInfo.getFinProduct().getMdate();
                }
            }

        }
        // 衍生品资产
        else if (StringUtil.equals(DictCodeListExt2.平安资产类型_利率互换.getValue(), 投管资产大类)
                || StringUtil.equals(DictCodeListExt2.平安资产类型_债券借贷.getValue(), 投管资产大类)
                || StringUtil.equals(DictCodeListExt3.平安资产类型_期权.getValue(), 投管资产大类)
                || StringUtil.equals(DictCodeListExt2.平安资产类型_外汇即期.getValue(), 投管资产大类)
                || StringUtil.equals(DictCodeListExt2.平安资产类型_外汇远期.getValue(), 投管资产大类)
                || StringUtil.equals(DictCodeListExt2.平安资产类型_外汇掉期.getValue(), 投管资产大类)) {
            // 无需计算
            return null;
        }

        return endDate;
    }

    /**
     * 计算剩余存续期限
     * 
     * @param assetInfo 资产基本信息
     * @param calcDate 计算日
     * @return 剩余存续期限
     */
    public static BigDecimal calc剩余存续期限(MockAssetInfoDto assetInfo, Date calcDate) {
        BigDecimal 计算结果 = null;

        Date endDate = null;
        if (assetInfo.getFinProduct() != null) {
            endDate = assetInfo.getFinProduct().getMdate();
        }
        else if (assetInfo.getFinTradeProduct() != null) {
            endDate = assetInfo.getFinTradeProduct().getMdate();
        }

        // 变量.剩余存续期限 = (参数.标的信息.到期日-参数.计算日)/365
        if (计算结果 == null && endDate != null && DateUtil.compareDate(endDate, calcDate) >= 0) {
            计算结果 = ArithmeticUtil.divide(
                    BigDecimal.valueOf(DateUtil.getBetweenDayNumber(calcDate, endDate)),
                    BigDecimal.valueOf(365), 12);
        }

        return 计算结果;
    }

    /**
     * 计算资产久期
     * 
     * @param assetInfo 资产基本信息
     * @param ytm 到期收益率
     * @param calcDate 计算日
     * @return 资产久期
     */
    public static BigDecimal calc资产久期(MockAssetInfoDto assetInfo, BigDecimal ytm, Date calcDate) {
        BigDecimal 计算结果 = null;
        int scale = 12;

        // 资产到期日
        Date endDate = null;
        if (assetInfo.getFinProduct() != null) {
            endDate = assetInfo.getFinProduct().getMdate();
        }
        else if (assetInfo.getFinTradeProduct() != null) {
            endDate = assetInfo.getFinTradeProduct().getMdate();
        }

        String 投管资产大类 = assetInfo.getTgAssetType();
        if (StringUtil.equals(DictCodeList.平安资产类型_债券.getValue(), 投管资产大类)) {
            String calType = null; // 利率类型
            BigDecimal 当期票面利率 = null;
            Date 下一重定价日 = null;
            if (!StringUtil.isEmptyList(assetInfo.getFinCashCalcRuleList())) {
                List<FinCashCalcRule> 小于等于计算日的数据 = assetInfo.getFinCashCalcRuleList().stream().filter(
                    finCashCalcRule -> calcDate.compareTo(finCashCalcRule.getEffDate()) >= 0).collect(Collectors.toList());
                if (!小于等于计算日的数据.isEmpty()) { // 取最后一条生效日最大的一条
                    calType = 小于等于计算日的数据.get(小于等于计算日的数据.size() - 1).getCalcType(); // 利率类型
                    当期票面利率 = 小于等于计算日的数据.get(小于等于计算日的数据.size() - 1).getYield();
                    下一重定价日 = 小于等于计算日的数据.get(小于等于计算日的数据.size() - 1).getEffDate();
                }
                else {
                    List<FinCashCalcRule> 大于计算日的数据 = assetInfo.getFinCashCalcRuleList().stream().filter(
                        finCashCalcRule -> calcDate.compareTo(finCashCalcRule.getEffDate()) < 0).collect(Collectors.toList());
                    if (!大于计算日的数据.isEmpty()) { // 取第一条生效日最小的一条
                        calType = 大于计算日的数据.get(0).getCalcType(); // 利率类型
                        当期票面利率 = 大于计算日的数据.get(0).getYield();
                        下一重定价日 = 大于计算日的数据.get(0).getEffDate();
                    }
                }

                List<FinCashCalcRule> 大于计算日的数据 = assetInfo.getFinCashCalcRuleList().stream().filter(
                        finCashCalcRule -> calcDate.compareTo(finCashCalcRule.getEffDate()) < 0).collect(Collectors.toList());
                if (!大于计算日的数据.isEmpty()) {
                    下一重定价日 = 大于计算日的数据.get(0).getEffDate();
                }

            }
            // 付息计划
            List<MockFinCashPlanDto> listIntinfo = null;
            if (!StringUtil.isEmptyList(assetInfo.getMockFinCashPlanDtoList())) {
                listIntinfo = assetInfo.getMockFinCashPlanDtoList().stream().filter(
                    x -> (StringUtil.equals(DictCodeList.现金流类型_到期收息.getValue(),
                        x.getFinCashPlan().getCashType()) || StringUtil.equals(
                            DictCodeList.现金流类型_中间收息.getValue(),
                            x.getFinCashPlan().getCashType()))).collect(Collectors.toList());
            }
            // 付息频率(天数)
            Long payFreq = null;
            if (!StringUtil.isEmptyList(assetInfo.getFinCashPayRuleList())) {
                String payCycle = assetInfo.getFinCashPayRuleList().get(0).getPayCycle();
                Long payCycleVal = assetInfo.getFinCashPayRuleList().get(0).getPayCycleVal();
                if (payCycleVal == null) {
                    payCycleVal = 1L;
                }
                if (StringUtil.equals(DictCodeList.频率_月.getValue(), payCycle)) {
                    payFreq = 12 / payCycleVal;
                }
                else if (StringUtil.equals(DictCodeList.频率_季.getValue(), payCycle)) {
                    payFreq = 4 / payCycleVal;
                }
                else if (StringUtil.equals(DictCodeList.频率_年.getValue(), payCycle)) {
                    payFreq = 1 / payCycleVal;
                }
                else if (StringUtil.equals(DictCodeList.频率_半年.getValue(), payCycle)) {
                    payFreq = 2 / payCycleVal;
                }
            }

            // 优先股
            if (StringUtil.equals(DictCodeList.WIND一级分类_含债券一级分类等_优先股.getValue(),
                assetInfo.getFinProductType().getType6())) {
                // 优先股的久期计算方式为1/r，r为当期票面利率
                计算结果 = ArithmeticUtil.divide(BigDecimal.ONE, 当期票面利率, scale);
                return 计算结果;
            }
            // 永续债（X+N) TODO
            else if (StringUtil.equals(DictCodeList.是否_是.getValue(),
                assetInfo.getFinProduct().getIsSus())) {
                // 直接取估价估值久期 如取不到估价久期，则取剩余期限
                if (!StringUtil.isEmptyList(assetInfo.getMockQuotationInfoDtoList())) {
                    计算结果 = assetInfo.getMockQuotationInfoDtoList().get(0).getMarketPriceDuration();
                }
                else {
                    // 永续债无固定期限
                    if (DictCodeList.是否_是.getValue().equals(assetInfo.getFinProductAdd().getNoFixedTerm())) {
                        计算结果 = ArithmeticUtil.divide(BigDecimal.ONE, 当期票面利率, scale);
                    }
                    else if (!StringUtil.isEmptyList(assetInfo.getResaleFinExercisePlanList())) {
                        // 回售行权的时候取下一行权日
                        endDate = assetInfo.getResaleFinExercisePlanList().get(0).getExerciseDate();
                    }
                    //计算结果 = ArithmeticUtil.divide(calc剩余期限(assetInfo, calcDate), BigDecimal.valueOf(365), scale);
                }
            }
            // 无固定期限资本债 TODO
            else if (StringUtil.equals(DictCodeList.是否_否.getValue(),
                assetInfo.getFinProduct().getIsSus())) {
                // 无固定期限资本债的久期计算方式为1/r，r为当期票面利率
                计算结果 = ArithmeticUtil.divide(BigDecimal.ONE, 当期票面利率, scale);
                return 计算结果;
            }
            // 贴现、零息、一次还本付息
            else if ((StringUtil.equals(DictCodeList.息票品种_贴现.getValue(),
                assetInfo.getFinProduct().getCouponSpecies())
                      || StringUtil.equals(DictCodeList.息票品种_零息.getValue(),
                          assetInfo.getFinProduct().getCouponSpecies())
                      || StringUtil.equals(DictCodeList.息票品种_利随本清.getValue(),
                          assetInfo.getFinProduct().getCouponSpecies()))
                     || (StringUtil.equals(DictCodeList.息票品种_附息.getValue(),
                         assetInfo.getFinProduct().getCouponSpecies())
                         && StringUtil.equals(DictCodeList.频率_到期一次.getValue(),
                             assetInfo.getFinCashPayRuleList().get(0).getPayCycle()))) {
                // 直接取中债估值久期；如果取不到中债久期，则取剩余期限
                if (!StringUtil.isEmptyList(assetInfo.getMockQuotationInfoDtoList())) {
                    计算结果 = assetInfo.getMockQuotationInfoDtoList().get(0).getMarketPriceDuration();
                }
            }
            // 含权债（回售、赎回）(X+Y)
            else if (StringUtil.equals(DictCodeList.是否_是.getValue(),
                assetInfo.getFinProduct().getIsRight())) {
                //优先取第三方估值机构推荐的估价修正久期
                if (!StringUtil.isEmptyList(assetInfo.getMockQuotationInfoDtoList())) {
                    计算结果 = assetInfo.getMockQuotationInfoDtoList().get(0).getMarketPriceDuration();
                }else if (!StringUtil.isEmptyList(assetInfo.getResaleFinExercisePlanList())) {
                    // 按行权日计算的久期：
                    // 含回售权：久期=（下一行权日-计算日）/365；如无下一行权日，则取到期日；
                    // 含赎回权：久期=（（到期日-计算日）/365；
                    // 若同时含两个权，优先采用回售权的下一个行权日
                   /* if (GlobalConstant.DEAL_TYPE_01.equals(
                            CacheUtil.getSysParam("SEC_END_DAYS_SWITCH"))) {
                        endDate = assetInfo.getResaleFinExercisePlanList().get(0).getExerciseDate();
                    }*/

                    endDate = assetInfo.getResaleFinExercisePlanList().get(0).getExerciseDate();
                }
            }
            // 固定利率付息债
            else if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(),
                assetInfo.getFinProduct().getCouponSpecies())
                     && StringUtil.equals(DictCodeList.利率类型_固定利率.getValue(), calType)) {
                // 直接取估价估值久期 如取不到估价久期，则取剩余期限
                if (!StringUtil.isEmptyList(assetInfo.getMockQuotationInfoDtoList())) {
                    计算结果 = assetInfo.getMockQuotationInfoDtoList().get(0).getMarketPriceDuration();
                }
            }
            // 浮息债
            else if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(),
                assetInfo.getFinProduct().getCouponSpecies())
                     && StringUtil.equals(DictCodeList.利率类型_浮动利率.getValue(), calType)) {
                // 先取中债估值的利差久期，若取不到，则取修正久期，若仍取不到，则取距离下一重定价日的剩余期限。
                if (!StringUtil.isEmptyList(assetInfo.getMockQuotationInfoDtoList())) {
                    // 利差久期 + 利率久期
                    计算结果 = ArithmeticUtil.doAdd(assetInfo.getMockQuotationInfoDtoList().get(0).getSduration(),
                            assetInfo.getMockQuotationInfoDtoList().get(0).getInterestDuration());
                    if (assetInfo.getMockQuotationInfoDtoList().get(0).getSduration() == null
                        || assetInfo.getMockQuotationInfoDtoList().get(0).getInterestDuration() == null){
                        计算结果 = assetInfo.getMockQuotationInfoDtoList().get(0).getMarketPriceDuration();
                    }
                }
                if (计算结果 != null) {
                    return 计算结果;
                }
                if (计算结果 == null) {
                    // 取距离下一重定价日的剩余期限。
                    endDate = 下一重定价日;
                    //如果下一重定价日 <= 当前计算日，则 endDate = 资产到期日
                    if (DateUtil.compareDate(endDate, calcDate) <= 0){
                        endDate = assetInfo.getFinProduct().getMdate();
                    }
                }
            }
        }
        else if (StringUtil.equals(DictCodeListExt2.平安资产类型_股票.getValue(), 投管资产大类)
            || StringUtil.equals(DictCodeList.平安资产类型_计划类资产_净值型.getValue(), 投管资产大类)) {
            return ArithmeticUtil.divide(BigDecimal.ONE, new BigDecimal(365), scale);
        }
        else if (StringUtil.equals(DictCodeList.平安资产类型_基金.getValue(), 投管资产大类)) {
            FinOpenPlan 下一赎回开始日计划 = null;
            FinOpenPlan 下一开放日计划 = null;
            if (!StringUtil.isEmptyList(assetInfo.getFundOpenPlanList())) {
                Collections.sort(assetInfo.getFundOpenPlanList(), new Comparator<FinOpenPlan>() {

                    @Override
                    public int compare(FinOpenPlan arg0, FinOpenPlan arg1) {
                        return DateUtil.compareDate(arg0.getOpenDateStr(), arg1.getOpenDateStr());
                    }
                });
                下一开放日计划 = assetInfo.getFundOpenPlanList().get(0);

                // 赎回计划
                List<FinOpenPlan> tempOpenPlanList = assetInfo.getFundOpenPlanList().stream().filter(
                    x -> (StringUtil.equals(DictCodeList.申赎开放类型_赎回开放.getValue(), x.getOpenType())
                          || StringUtil.equals(DictCodeList.申赎开放类型_申赎开放.getValue(),
                              x.getOpenType()))).collect(Collectors.toList());
                if (!StringUtil.isEmptyList(tempOpenPlanList)) {
                    Collections.sort(tempOpenPlanList, new Comparator<FinOpenPlan>() {

                        @Override
                        public int compare(FinOpenPlan arg0, FinOpenPlan arg1) {
                            return DateUtil.compareDate(arg0.getOpenDateStr(),
                                arg1.getOpenDateStr());
                        }
                    });
                    下一赎回开始日计划 = tempOpenPlanList.get(0);
                }
            }

            // 货币基金
            if (StringUtil.equals(DictCodeList.基金类资产类型_货币基金.getValue(),
                assetInfo.getFinProductType().getType6())) {
                // 优先取万德数据表（下一赎回开始日-计算日）/365计算久期；若取不到，按剩余期限为1处理
                if (下一赎回开始日计划 != null) {
                    计算结果 = ArithmeticUtil.divide(
                        BigDecimal.valueOf(
                            DateUtil.getBetweenDayNumber(calcDate, 下一赎回开始日计划.getOpenDateStr())),
                        BigDecimal.valueOf(365), scale);
                }
                else {
                    计算结果 = ArithmeticUtil.divide(BigDecimal.ONE, BigDecimal.valueOf(365), scale);
                }
                return 计算结果;
            }

            // 非货币基金 || 股票基金
            else if (StringUtil.equals(DictCodeList.基金类资产类型_非货币基金.getValue(),
                assetInfo.getFinProductType().getType6())) {
                // 优先取万德数据表（下一赎回开始日-计算日）/365计算久期；取不到，按计算日到下一开放日计算剩余期限
                if (下一赎回开始日计划 != null) {
                    计算结果 = ArithmeticUtil.divide(
                        BigDecimal.valueOf(
                            DateUtil.getBetweenDayNumber(calcDate, 下一赎回开始日计划.getOpenDateStr())),
                        BigDecimal.valueOf(365), scale);
                }
//                else if (下一开放日计划 != null) {
//                    计算结果 = ArithmeticUtil.divide(
//                        BigDecimal.valueOf(
//                            DateUtil.getBetweenDayNumber(calcDate, 下一开放日计划.getOpenDateStr())),
//                        BigDecimal.valueOf(365), scale);
//                }
                else {
                    计算结果 = ArithmeticUtil.divide(BigDecimal.ONE, BigDecimal.valueOf(365), scale);
                }
                return 计算结果;
            }
        }
        // 衍生品资产
        else if (StringUtil.equals(DictCodeListExt2.平安资产类型_利率互换.getValue(), 投管资产大类)
                 || StringUtil.equals(DictCodeListExt2.平安资产类型_债券借贷.getValue(), 投管资产大类)
                 || StringUtil.equals(DictCodeListExt3.平安资产类型_期权.getValue(), 投管资产大类)
                 || StringUtil.equals(DictCodeListExt2.平安资产类型_外汇即期.getValue(), 投管资产大类)
                 || StringUtil.equals(DictCodeListExt2.平安资产类型_外汇远期.getValue(), 投管资产大类)
                 || StringUtil.equals(DictCodeListExt2.平安资产类型_外汇掉期.getValue(), 投管资产大类)) {
            // 无需计算
            return null;
        }

        // 默认：(到期日-计算日)/365；
        if (计算结果 == null && endDate != null && DateUtil.compareDate(endDate, calcDate) >= 0) {
            计算结果 = ArithmeticUtil.divide(
                BigDecimal.valueOf(DateUtil.getBetweenDayNumber(calcDate, endDate)),
                BigDecimal.valueOf(365), scale);
        }

        return 计算结果;
    }

    /**
     * 七日年化收益率计算
     * 
     * @param profitType 收益类型
     * @param vdate 起息日
     * @param bonusPayCycle 分红规则
     * @param basisNum 计息基础
     * @param ptlValuationDtoMap 组合估值列表
     * @param calcDate 计算日期
     * @return 七日年化收益率
     */
    public static BigDecimal calc七日年化收益率(String profitType, Date vdate, String bonusPayCycle,
                                         int basisNum,
                                         Map<Date, MockPtlPortfolioValuationDto> ptlValuationDtoMap,
                                         Date calcDate) {
        if (ptlValuationDtoMap == null || ptlValuationDtoMap.isEmpty()) {
            return null;
        }

        BigDecimal 计算结果 = null;
        int scale = 12;

        // 产品存续天数
        long productSurvivingDays = DateUtil.getBetweenDayNumber(vdate, calcDate) + 1;

        // 货币型产品
        if (StringUtil.equals(DictCodeList.收益类型_货币型.getValue(), profitType)) {
            // 产品存续天数<7（含计算日）天时：
            if (productSurvivingDays < 7) {

                // 日日复利
                if (StringUtil.equals(DictCodeList.分红规则_日付.getValue(), bonusPayCycle)) {
                    // 收益率%={[∏_(i=1)^N▒(1+r_i/10000) ]^(计息基础/N)-1}×100%
                    计算结果 = calc年化收益率_日日复利(basisNum, ptlValuationDtoMap, vdate, calcDate,
                        productSurvivingDays, scale);
                }
                // 月月复利
                else {
                    // 收益率%=((∑_(i=1)^N▒r_i )/N)×计息基础/10000×100%
                    计算结果 = calc年化收益率_月月复利(basisNum, ptlValuationDtoMap, vdate, calcDate,
                        productSurvivingDays, scale);
                }
            }
            // 产品存续天数N≥7天时：
            else {
                // 日日复利
                if (StringUtil.equals(DictCodeList.分红规则_日付.getValue(), bonusPayCycle)) {
                    // 收益率%={[∏_(i=1)^7▒(1+r_i/10000) ]^(计息基础/7)-1}×100%
                    计算结果 = calc年化收益率_日日复利(basisNum, ptlValuationDtoMap,
                        DateUtil.addDay(calcDate, -6), calcDate, 7, scale);
                }
                // 月月复利
                else {
                    // 收益率%=((∑_(i=1)^7▒r_i )/7)×计息基础/10000×100%
                    计算结果 = calc年化收益率_月月复利(basisNum, ptlValuationDtoMap,
                        DateUtil.addDay(calcDate, -6), calcDate, 7, scale);
                }
            }
        }
        // 净值型产品
        else if (StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), profitType)) {
            // 产品存续天数<7（含计算日）天时：
            if (productSurvivingDays < 7) {
                // 7日年化收益率R= （计算日的累计净值-起息日的累计净值）/起息日的累计净值*计息基础/计息天数
                计算结果 = calc年化收益率_净值型(basisNum, ptlValuationDtoMap, DateUtil.addDay(calcDate, -7), calcDate,
                    productSurvivingDays, scale);
            }
            // 产品存续天数N≥7天时：
            else {
                // 7日年化收益率R= （计算日的累计净值-7日前的累计净值）/7日前的累计净值*计息基础/计息天数
                计算结果 = calc年化收益率_净值型(basisNum, ptlValuationDtoMap, DateUtil.addDay(calcDate, -7),
                    calcDate, 7, scale);
            }
        }

        if (计算结果 != null) {
            计算结果 = ArithmeticUtil.round(计算结果, 6, RoundingMode.HALF_UP);
        }

        return 计算结果;
    }

    /**
     * 一月年化收益率计算
     * 
     * @param profitType 收益类型
     * @param vdate 起息日
     * @param bonusPayCycle 分红规则
     * @param basisNum 计息基础
     * @param ptlValuationDtoMap 组合估值列表
     * @param calcDate 计算日期
     * @return 一月年化收益率
     */
    public static BigDecimal calc一月年化收益率(String profitType, Date vdate, String bonusPayCycle,
                                         int basisNum,
                                         Map<Date, MockPtlPortfolioValuationDto> ptlValuationDtoMap,
                                         Date calcDate) {
        if (ptlValuationDtoMap == null || ptlValuationDtoMap.isEmpty()) {
            return null;
        }

        BigDecimal 计算结果 = null;
        int scale = 12;

        // 产品存续天数
        long productSurvivingDays = DateUtil.getBetweenDayNumber(vdate, calcDate) + 1;
        // 计算日至计算日前一个月同日期实际天数
        Date 计算日前一个月同日期 = DateUtil.addMonthDate(calcDate, -1);
        long 计算日至计算日前一个月同日期实际天数 = DateUtil.getBetweenDayNumber(计算日前一个月同日期, calcDate) + 1;

        // 货币型产品
        if (StringUtil.equals(DictCodeList.收益类型_货币型.getValue(), profitType)) {
            // ①    若产品存续天数＜计算日至计算日前一个月同日期实际天数
            if (productSurvivingDays < 计算日至计算日前一个月同日期实际天数) {
                // 日日复利
                if (StringUtil.equals(DictCodeList.分红规则_日付.getValue(), bonusPayCycle)) {
                    // 收益率%={[∏_(i=1)^N▒(1+r_i/10000) ]^(计息基础/N)-1}×100%
                    计算结果 = calc年化收益率_日日复利(basisNum, ptlValuationDtoMap, vdate, calcDate,
                        productSurvivingDays, scale);
                }
                // 月月复利
                else {
                    // 收益率%=((∑_(i=1)^N▒r_i )/N)×计息基础/10000×100%
                    计算结果 = calc年化收益率_月月复利(basisNum, ptlValuationDtoMap, vdate, calcDate,
                        productSurvivingDays, scale);
                }
            }
            // ②    若产品存续天数≥计算日至计算日前一个月同日期实际天数
            else {
                // 日日复利
                if (StringUtil.equals(DictCodeList.分红规则_日付.getValue(), bonusPayCycle)) {
                    // 收益率%={[∏_(i=1)^days▒(1+r_i/10000) ]^(计息基础/days)-1}×100%
                    计算结果 = calc年化收益率_日日复利(basisNum, ptlValuationDtoMap, 计算日前一个月同日期, calcDate,
                        计算日至计算日前一个月同日期实际天数, scale);
                }
                // 月月复利
                else {
                    // 收益率%={[∏_(i=1)^days▒(1+r_i/10000) ]^(12/1)-1}×100%
                    计算结果 = calc年化收益率_复利_basic(ptlValuationDtoMap, 计算日前一个月同日期, calcDate,
                        计算日至计算日前一个月同日期实际天数, 12, 1, scale);
                }
            }
        }
        // 净值型产品
        else if (StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), profitType)) {
            // 产品存续天数＜计算日至计算日前一个月同日期实际天数
            if (productSurvivingDays < 计算日至计算日前一个月同日期实际天数) {
                // 一月年化收益率R= （计算日的累计净值-起息日的累计净值）/起息日的累计净值/起息日至计算日天数*计息基础
                计算结果 = calc年化收益率_净值型(basisNum, ptlValuationDtoMap, 计算日前一个月同日期, calcDate,
                    productSurvivingDays, scale);
            }
            // 产品存续天数≥计算日至计算日前一个月同日期实际天数
            else {
                // 一月年化收益率R= （计算日的累计净值-一月前同日的累计净值）/一月前同日的累计净值/计算日至计算日前一个月同日期实际天数*计息基础
                计算结果 = calc年化收益率_净值型(basisNum, ptlValuationDtoMap, 计算日前一个月同日期, calcDate,
                    计算日至计算日前一个月同日期实际天数-1, scale);
            }
        }

        if (计算结果 != null) {
            计算结果 = ArithmeticUtil.round(计算结果, 6, RoundingMode.HALF_UP);
        }

        return 计算结果;
    }

    /**
     * 三月年化收益率计算
     * 
     * @param profitType 收益类型
     * @param vdate 起息日
     * @param bonusPayCycle 分红规则
     * @param basisNum 计息基础
     * @param ptlValuationDtoMap 组合估值列表
     * @param calcDate 计算日期
     * @return 三月年化收益率
     */
    public static BigDecimal calc三月年化收益率(String profitType, Date vdate, String bonusPayCycle,
                                         int basisNum,
                                         Map<Date, MockPtlPortfolioValuationDto> ptlValuationDtoMap,
                                         Date calcDate) {
        if (ptlValuationDtoMap == null || ptlValuationDtoMap.isEmpty()) {
            return null;
        }

        BigDecimal 计算结果 = null;
        int scale = 12;

        // 产品存续天数
        long productSurvivingDays = DateUtil.getBetweenDayNumber(vdate, calcDate) + 1;
        // 计算日至计算日前三个月同日期实际天数
        Date 计算日前三个月同日期 = DateUtil.addMonthDate(calcDate, -3);
        long 计算日至计算日前三个月同日期实际天数 = DateUtil.getBetweenDayNumber(计算日前三个月同日期, calcDate) + 1;

        // 货币型产品
        if (StringUtil.equals(DictCodeList.收益类型_货币型.getValue(), profitType)) {
            // ①    若产品存续天数＜计算日至计算日前三个月同日期实际天数
            if (productSurvivingDays < 计算日至计算日前三个月同日期实际天数) {
                // 日日复利
                if (StringUtil.equals(DictCodeList.分红规则_日付.getValue(), bonusPayCycle)) {
                    // 收益率%={[∏_(i=1)^N▒(1+r_i/10000) ]^(计息基础/N)-1}×100%
                    计算结果 = calc年化收益率_日日复利(basisNum, ptlValuationDtoMap, vdate, calcDate,
                        productSurvivingDays, scale);
                }
                // 月月复利
                else {
                    // 收益率%=((∑_(i=1)^N▒r_i )/N)×计息基础/10000×100%
                    计算结果 = calc年化收益率_月月复利(basisNum, ptlValuationDtoMap, vdate, calcDate,
                        productSurvivingDays, scale);
                }
            }
            // ②    若产品存续天数≥计算日至计算日前三个月同日期实际天数
            else {
                // 日日复利
                if (StringUtil.equals(DictCodeList.分红规则_日付.getValue(), bonusPayCycle)) {
                    // 收益率%={[∏_(i=1)^days▒(1+r_i/10000) ]^(计息基础/days)-1}×100%
                    计算结果 = calc年化收益率_日日复利(basisNum, ptlValuationDtoMap, 计算日前三个月同日期, calcDate,
                        计算日至计算日前三个月同日期实际天数, scale);
                }
                // 月月复利
                else {
                    // 收益率%={[∏_(i=1)^days▒(1+r_i/10000) ]^(12/3)-1}×100%
                    计算结果 = calc年化收益率_复利_basic(ptlValuationDtoMap, 计算日前三个月同日期, calcDate,
                        计算日至计算日前三个月同日期实际天数, 12, 3, scale);
                }
            }
        }
        // 净值型产品
        else if (StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), profitType)) {
            // 产品存续天数＜计算日至计算日前三个月同日期实际天数
            if (productSurvivingDays < 计算日至计算日前三个月同日期实际天数) {
                // 一月年化收益率R= （计算日的累计净值-起息日的累计净值）/起息日的累计净值/起息日至计算日天数*计息基础
                计算结果 = calc年化收益率_净值型(basisNum, ptlValuationDtoMap, 计算日前三个月同日期, calcDate,
                    productSurvivingDays, scale);
            }
            // 产品存续天数≥计算日至计算日前三个月同日期实际天数
            else {
                // 一月年化收益率R= （计算日的累计净值-三月前同日的累计净值）/三月前同日的累计净值*计息基础/计算日至计算日前三个月同日期实际天数
                计算结果 = calc年化收益率_净值型(basisNum, ptlValuationDtoMap, 计算日前三个月同日期, calcDate,
                    计算日至计算日前三个月同日期实际天数-1, scale);
            }
        }

        if (计算结果 != null) {
            计算结果 = ArithmeticUtil.round(计算结果, 6, RoundingMode.HALF_UP);
        }

        return 计算结果;
    }

    /**
     * 一年收益率计算
     * 
     * @param profitType 收益类型
     * @param vdate 起息日
     * @param bonusPayCycle 分红规则
     * @param basisNum 计息基础
     * @param ptlValuationDtoMap 组合估值列表
     * @param calcDate 计算日期
     * @return 一年收益率
     */
    public static BigDecimal calc一年收益率(String profitType, Date vdate, String bonusPayCycle,
                                       int basisNum,
                                       Map<Date, MockPtlPortfolioValuationDto> ptlValuationDtoMap,
                                       Date calcDate) {
        if (ptlValuationDtoMap == null || ptlValuationDtoMap.isEmpty()) {
            return null;
        }

        BigDecimal 计算结果 = null;
        int scale = 12;

        // 产品存续天数
        long productSurvivingDays = DateUtil.getBetweenDayNumber(vdate, calcDate) + 1;

        // 货币型产品
        if (StringUtil.equals(DictCodeList.收益类型_货币型.getValue(), profitType)) {
            // 当计息基础为A/365时：
            if (basisNum == 365) {
                // ①    若产品存续天数＜365天
                if (productSurvivingDays < 365) {
                    // 日日复利
                    if (StringUtil.equals(DictCodeList.分红规则_日付.getValue(), bonusPayCycle)) {
                        // 收益率%={[∏_(i=1)^N▒(1+r_i/10000) ]^(365/N)-1}×100%
                        计算结果 = calc年化收益率_日日复利(basisNum, ptlValuationDtoMap, vdate, calcDate,
                            productSurvivingDays, scale);
                    }
                    // 月月复利
                    else {
                        // 收益率%=((∑_(i=1)^N▒r_i )/N)×365/10000×100%
                        计算结果 = calc年化收益率_月月复利(basisNum, ptlValuationDtoMap, vdate, calcDate,
                            productSurvivingDays, scale);
                    }
                }
                // ②    若产品存续天数≥365天，日日复利、月月复利均为：
                else {
                    // 收益率%={[∏_(i=1)^365▒(1+r_i/10000) ]^(365/365)-1}×100%
                    计算结果 = calc年化收益率_复利_basic(ptlValuationDtoMap, DateUtil.addDay(calcDate, -364),
                        calcDate, 365, 365, 365, scale);
                }
            }
            // 当计息基础为A/360时：
            else {
                // ① 若产品存续天数＜360天
                if (productSurvivingDays < 360) {
                    // 日日复利
                    if (StringUtil.equals(DictCodeList.分红规则_日付.getValue(), bonusPayCycle)) {
                        // 收益率%={[∏_(i=1)^N▒(1+r_i/10000) ]^(360/N)-1}×100%
                        计算结果 = calc年化收益率_日日复利(basisNum, ptlValuationDtoMap, vdate, calcDate,
                            productSurvivingDays, scale);
                    }
                    // 月月复利
                    else {
                        // 收益率%=((∑_(i=1)^N▒r_i )/N)×360/10000×100%
                        计算结果 = calc年化收益率_月月复利(basisNum, ptlValuationDtoMap, vdate, calcDate,
                            productSurvivingDays, scale);
                    }
                }
                // ② 若产品存续天数≥360天，日日复利、月月复利均为：
                else {
                    // 收益率%={[∏_(i=1)^360▒(1+r_i/10000) ]^(360/360)-1}×100%
                    计算结果 = calc年化收益率_复利_basic(ptlValuationDtoMap, DateUtil.addDay(calcDate, -359),
                        calcDate, 360, 360, 360, scale);
                }
            }
        }
        // 净值型产品
        else if (StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), profitType)) {
            // 当计息基础为A/365时：
            if (basisNum == 365) {
                Date 一年前同日期 = DateUtil.addYear(calcDate, -1);
                // ①    若产品存续天数＜365天
                if (productSurvivingDays < 365) {
                    // 一年收益率R= （计算日的累计净值-起息日的累计净值）/起息日累计净值/起息日至计算日天数*365
                    计算结果 = calc年化收益率_净值型(basisNum, ptlValuationDtoMap, 一年前同日期, calcDate,
                        productSurvivingDays, scale);
                }
                // ②    若产品存续天数≥365天，
                else {
                    // 一年收益率R= （计算日的累计净值-一年前同日期的累计净值）/一年前同日期累计净值
                    计算结果 = calc年化收益率_净值型(basisNum, ptlValuationDtoMap, 一年前同日期, calcDate, basisNum,
                        scale);
                }
            }
            // 当计息基础为A/360时：
            else {
                Date 一年前同日期 = DateUtil.addYear(calcDate, -1);
                // ①    若产品存续天数＜360天
                if (productSurvivingDays < 360) {
                    // 一年收益率R= （计算日的累计净值-起息日的累计净值）/起息日累计净值/起息日至计算日天数*360
                    计算结果 = calc年化收益率_净值型(basisNum, ptlValuationDtoMap, 一年前同日期, calcDate,
                        productSurvivingDays, scale);
                }
                // ②    若产品存续天数≥360天，
                else {
                    // 一年收益率R= （计算日的累计净值-一年前同日期的累计净值）/一年前同日期累计净值
                    计算结果 = calc年化收益率_净值型(basisNum, ptlValuationDtoMap, 一年前同日期, calcDate, basisNum,
                        scale);
                }
            }
        }

        if (计算结果 != null) {
            计算结果 = ArithmeticUtil.round(计算结果, 6, RoundingMode.HALF_UP);
        }

        return 计算结果;
    }

    /**
     * 到期收益率计算
     * 
     * @param profitType 收益类型
     * @param openType 开放类型
     * @param vdate 起息日
     * @param mdate 到期日
     * @param openPlanList 开放计划
     * @param ptlValuationDtoMap 组合估值列表
     * @param calcDate 计算日期
     * @return 一年收益率
     */
    public static BigDecimal calc到期收益率(int basisNum,String profitType, String openType, Date vdate, Date mdate,
                                       List<FinOpenPlan> openPlanList,
                                       Map<Date, MockPtlPortfolioValuationDto> ptlValuationDtoMap,
                                       Date calcDate) {
        if (ptlValuationDtoMap == null || ptlValuationDtoMap.isEmpty()) {
            return null;
        }

        // 确定模拟试算的试算区间
        List<Date> periodList = genMockDatePeriod(openType, vdate, mdate, openPlanList, calcDate);

        // 净值型特殊处理
        if (StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), profitType)) {
            if (StringUtil.equals(DictCodeListExt3.组合开放类型_封闭式.getValue(), openType)
                || StringUtil.equals(DictCodeListExt3.组合开放类型_高频开放.getValue(), openType)) {
                // 封闭型产品：收益率%=（组合到期日累计单位净值/1-1）*100%
                // 高频开放型产品：收益率%=（一季度后组合累计单位净值/1-1）*100%
//                BigDecimal 区间终点累计单位净值 = ptlValuationDtoMap.get(
//                    periodList.get(1)).getTdyUnitValueSum();

                BigDecimal 计算结果 = null;
                MockPtlPortfolioValuationDto portfolioValuationDtoEnd =ptlValuationDtoMap.get(periodList.get(1));

                if(portfolioValuationDtoEnd!=null){

                    BigDecimal 区间终点累计单位净值 = portfolioValuationDtoEnd.getTdyUnitValueSum();

                    BigDecimal 区间起点累计单位净值 = BigDecimal.ONE;
                    Date calcDate_1 = calcDate;
                    if(StringUtil.equals(DictCodeListExt3.组合开放类型_封闭式.getValue(), openType)){
                        calcDate_1 = DateUtil.addDay(periodList.get(0),-1);
                        if(ptlValuationDtoMap.get(
                                calcDate_1)!=null&& ptlValuationDtoMap.get(
                                calcDate_1).getTdyUnitValueSum()!=null){
                            区间起点累计单位净值 =
                                    ptlValuationDtoMap.get(
                                            calcDate_1).getTdyUnitValueSum();
                        }
                    } else{
                        if(ptlValuationDtoMap.get(
                                calcDate_1)!=null&& ptlValuationDtoMap.get(
                                calcDate_1).getTdyUnitValueSum()!=null){
                            区间起点累计单位净值 =
                                    ptlValuationDtoMap.get(
                                            calcDate).getTdyUnitValueSum();
                        }
                    }

                    计算结果 = ArithmeticUtil.subtract(
                            ArithmeticUtil.divide(区间终点累计单位净值, 区间起点累计单位净值, 12), BigDecimal.ONE);

                    计算结果 = ArithmeticUtil.multiply(计算结果, BigDecimal.valueOf(basisNum));
                    /*if(StringUtil.equals(DictCodeListExt3.组合开放类型_封闭式.getValue(), openType)){
                        计算结果 = ArithmeticUtil.divide(计算结果, BigDecimal.valueOf(DateUtil.getBetweenDayNumber(calcDate_1,periodList.get(1))), 6);
                    }else{
                        计算结果 = ArithmeticUtil.divide(计算结果, BigDecimal.valueOf(DateUtil.getBetweenDayNumber(calcDate_1,periodList.get(1))), 6);
                    }*/
                    计算结果 = ArithmeticUtil.divide(计算结果, BigDecimal.valueOf(DateUtil.getBetweenDayNumber(calcDate_1,periodList.get(1))), 6);
                }

               return 计算结果;
            }
        }

//        Date bigenDate = periodList.get(0);
//        if (StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), profitType)) {
//            bigenDate = DateUtil.addDay(bigenDate,-1);
//        }
        return calc区间收益率(basisNum,profitType, periodList.get(0), periodList.get(1), ptlValuationDtoMap);
    }

    /**
     * 至下一季报日收益率计算
     * 
     * @param profitType 收益类型
     * @param vdate 起息日
     * @param mdate 到期日
     * @param ptlValuationDtoMap 组合估值列表
     * @param calcDate 计算日期
     * @return 至下一季报日收益率
     */
    public static BigDecimal calc至下一季报日收益率(int basisNum,String profitType, Date vdate, Date mdate,
                                           Map<Date, MockPtlPortfolioValuationDto> ptlValuationDtoMap,
                                           Date calcDate) {
        if (ptlValuationDtoMap == null || ptlValuationDtoMap.isEmpty()) {
            return null;
        }

        // 确定模拟试算的试算区间(上一季报日、下一季报日)
        List<Date> periodList = genMockQuarterDatePeriod(vdate, mdate, calcDate);
        return calc区间收益率(basisNum,profitType, periodList.get(0), periodList.get(1), ptlValuationDtoMap);
    }

    /**
     * 确定模拟试算的试算区间 展示区间为：封闭型组合为起息日至到期日、定开型为上一开放日至下一开放日、高频开放型为起息日至三个月后
     * 
     * @param openType 开放类型
     * @param vdate 组合起息日
     * @param mdate 组合到期日
     * @param openPlanList 开放计划
     * @param calcDate 计算日期
     * @return 模拟试算的试算区间
     */
    public static List<Date> genMockDatePeriod(String openType, Date vdate, Date mdate,
                                               List<FinOpenPlan> openPlanList, Date calcDate) {
        List<Date> periodList = new ArrayList<Date>();

        if (StringUtil.equals(DictCodeList.开放类型_封闭式.getValue(), openType)) {
            // 封闭型组合为起息日至到期日
            periodList.add(vdate);
            periodList.add(mdate);
        }
        else if (StringUtil.equals(DictCodeListExt3.组合开放类型_定期开放.getValue(), openType)) {
            // 定开型为上一开放日至下一开放日
            Date 上一开放日 = vdate;
            Date 下一开放日 = mdate;
            if (!StringUtil.isEmptyList(openPlanList)) {
                for (FinOpenPlan finOpenPlan : openPlanList) {
                    if (DateUtil.compareDate(finOpenPlan.getOpenMdate(), calcDate) <= 0
                        && DateUtil.compareDate(finOpenPlan.getOpenMdate(), 上一开放日) > 0) {
                        上一开放日 = finOpenPlan.getOpenMdate();
                    }
                    if (DateUtil.compareDate(finOpenPlan.getOpenMdate(), calcDate) > 0
                        && DateUtil.compareDate(finOpenPlan.getOpenMdate(), 下一开放日) < 0) {
                        下一开放日 = finOpenPlan.getOpenMdate();
                    }
                }
            }
            periodList.add(上一开放日);
            periodList.add(下一开放日);
        }
        else {
            // 高频开放型为起息日至三个月后
            periodList.add(vdate);
            Date 三个月后日期 = DateUtil.addMonthDate(calcDate, 3);
            if (DateUtil.compareDate(三个月后日期, mdate) > 0) {
                三个月后日期 = mdate;
            }
            periodList.add(三个月后日期);
        }

        return periodList;
    }

    /**
     * 确定模拟试算的试算区间(上一季报日、下一季报日)
     * 
     * @param vdate 组合起息日
     * @param mdate 组合到期日
     * @param calcDate 计算日期
     * @return 模拟试算的试算区间
     */
    public static List<Date> genMockQuarterDatePeriod(Date vdate, Date mdate, Date calcDate) {
        List<Date> periodList = new ArrayList<Date>();

        String yearStr = DateUtil.getYearStr(DateUtil.format(calcDate));

        Date quarterStart1 = DateUtil.parse(yearStr + "-01-01");
        Date quarterStart2 = DateUtil.parse(yearStr + "-04-01");
        Date quarterStart3 = DateUtil.parse(yearStr + "-07-01");
        Date quarterStart4 = DateUtil.parse(yearStr + "-10-01");

        Date startDate = null;
        Date endDate = null;
        if (DateUtil.compareDate(calcDate, quarterStart2) < 0) {
            startDate = DateUtil.addDay(quarterStart1, -1);
            endDate = DateUtil.addDay(quarterStart2, -1);
        }
        else if (DateUtil.compareDate(calcDate, quarterStart3) < 0) {
            startDate = DateUtil.addDay(quarterStart2, -1);
            endDate = DateUtil.addDay(quarterStart3, -1);
        }
        else if (DateUtil.compareDate(calcDate, quarterStart4) < 0) {
            startDate = DateUtil.addDay(quarterStart3, -1);
            endDate = DateUtil.addDay(quarterStart4, -1);
        }
        else {
            startDate = DateUtil.addDay(quarterStart4, -1);
            endDate = DateUtil.parse(yearStr + "-12-31");
        }

        if (DateUtil.compareDate(startDate, vdate) < 0) {
            startDate = vdate;
        }
        if (DateUtil.compareDate(endDate, mdate) > 0) {
            endDate = mdate;
        }

        periodList.add(startDate);
        periodList.add(endDate);

        return periodList;
    }

    /**
     * 区间收益率计算
     * 
     * @param profitType 收益类型
     * @param calcDateStart 区间开始日
     * @param calcDateEnd 区间结束日
     * @param ptlValuationDtoMap 组合估值列表
     * @return 区间收益率
     */
    public static BigDecimal calc区间收益率(int basisNum,String profitType, Date calcDateStart, Date calcDateEnd,
                                       Map<Date, MockPtlPortfolioValuationDto> ptlValuationDtoMap) {
        if (ptlValuationDtoMap == null || ptlValuationDtoMap.isEmpty() || calcDateStart == null
            || calcDateEnd == null) {
            return null;
        }

        BigDecimal 计算结果 = null;
        int scale = 12;

        // 货币型产品
        if (StringUtil.equals(DictCodeList.收益类型_货币型.getValue(), profitType)) {
            // 收益率%={[∏_(i=1)^N▒(1+r_i/10000) ]-1}×100%
            计算结果 = BigDecimal.ONE;
            BigDecimal 一万 = BigDecimal.valueOf(10000);
            Date tempCalcDate = calcDateStart;

            while (DateUtil.compareDate(calcDateEnd, tempCalcDate) >= 0) {
                if (ptlValuationDtoMap.containsKey(tempCalcDate)) {
                    计算结果 = ArithmeticUtil.multiply(计算结果,
                        ArithmeticUtil.doAdd(BigDecimal.ONE, ArithmeticUtil.divide(
                            ptlValuationDtoMap.get(tempCalcDate).getTdyUnitValue(), 一万, scale)));
                }

                tempCalcDate = DateUtil.addDay(tempCalcDate, 1);
            }
            计算结果 = ArithmeticUtil.subtract(计算结果, BigDecimal.ONE);
        }
        // 净值型产品
        else if (StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), profitType)) {
            // 收益率%=（区间终点累计单位净值/区间起点累计单位净值-1）*100%
            calcDateStart = DateUtil.addDay(calcDateStart,-1);
            MockPtlPortfolioValuationDto 区间起点组合估值 = ptlValuationDtoMap.get(calcDateStart);
            MockPtlPortfolioValuationDto 区间终点组合估值 = ptlValuationDtoMap.get(calcDateEnd);
            BigDecimal 区间起点组合净值 = BigDecimal.ONE;
            if(区间起点组合估值!=null&&区间起点组合估值.getTdyUnitValueSum()!=null){
                区间起点组合净值 = 区间起点组合估值.getTdyUnitValueSum();
            }
            if (区间终点组合估值 != null) {
                计算结果 = ArithmeticUtil.subtract(ArithmeticUtil.divide(区间终点组合估值.getTdyUnitValueSum(),
                        区间起点组合净值, scale), BigDecimal.ONE);
                计算结果 = ArithmeticUtil.multiply(计算结果, BigDecimal.valueOf(basisNum));
                计算结果 = ArithmeticUtil.divide(计算结果, BigDecimal.valueOf(DateUtil.getBetweenDayNumber(calcDateStart,calcDateEnd)), 6);

            }
        }

        if (计算结果 != null) {
            计算结果 = ArithmeticUtil.round(计算结果, 6, RoundingMode.HALF_UP);
        }

        return 计算结果;
    }

    /**
     * 年化收益率计算_日日复利 收益率%={[∏_(i=1)^N▒(1+r_i/10000) ]^(计息基础/N)-1}×100%
     * 
     * @param basis 计息基础
     * @param ptlValuationDtoMap 组合估值列表
     * @param calcDateStart 计算日期起始日
     * @param calcDateEnd 计算日期结束日
     * @param calcDay 计算天数
     * @param scale 小数位
     * @return 年化收益率_日日复利
     */
    private static BigDecimal calc年化收益率_日日复利(int basis,
                                             Map<Date, MockPtlPortfolioValuationDto> ptlValuationDtoMap,
                                             Date calcDateStart, Date calcDateEnd, long calcDay,
                                             int scale) {
        return calc年化收益率_复利_basic(ptlValuationDtoMap, calcDateStart, calcDateEnd, calcDay, basis,
            calcDay, scale);
    }

    /**
     * 年化收益率计算_月月复利 收益率%=((∑_(i=1)^N▒r_i )/N)×计息基础/10000×100%
     * 
     * @param basis 计息基础
     * @param ptlValuationDtoMap 组合估值列表
     * @param calcDateStart 计算日期起始日
     * @param calcDateEnd 计算日期结束日
     * @param calcDay 计算天数
     * @param scale 小数位
     * @return 年化收益率_月月复利
     */
    private static BigDecimal calc年化收益率_月月复利(int basis,
                                             Map<Date, MockPtlPortfolioValuationDto> ptlValuationDtoMap,
                                             Date calcDateStart, Date calcDateEnd, long calcDay,
                                             int scale) {
        if (ptlValuationDtoMap == null || ptlValuationDtoMap.isEmpty()) {
            return null;
        }
        BigDecimal 计算结果 = BigDecimal.ZERO;
        BigDecimal 一万 = BigDecimal.valueOf(10000);
        Date tempCalcDate = calcDateStart;

        while (DateUtil.compareDate(calcDateEnd, tempCalcDate) >= 0) {
            if (ptlValuationDtoMap.containsKey(tempCalcDate)) {
                计算结果 = ArithmeticUtil.doAdd(计算结果,
                    ptlValuationDtoMap.get(tempCalcDate).getTdyUnitValue());
            }

            tempCalcDate = DateUtil.addDay(tempCalcDate, 1);
        }

        计算结果 = ArithmeticUtil.divide(ArithmeticUtil.multiply(
            ArithmeticUtil.divide(计算结果, BigDecimal.valueOf(calcDay), scale),
            BigDecimal.valueOf(basis)), 一万, scale);

        return 计算结果;
    }

    /**
     * 年化收益率计算_复利_basic（基础算法） 收益率%={[∏_(i=1)^days▒(1+r_i/10000) ]^(指数分子/指数分母)-1}×100%
     * 
     * @param ptlValuationDtoMap 组合估值列表
     * @param calcDateStart 计算日期起始日
     * @param calcDateEnd 计算日期结束日
     * @param calcDay 计算天数
     * @param indexNumerator 指数分子
     * @param indexDenominator 指数分母
     * @param scale 小数位
     * @return 年化收益率
     */
    private static BigDecimal calc年化收益率_复利_basic(Map<Date, MockPtlPortfolioValuationDto> ptlValuationDtoMap,
                                                 Date calcDateStart, Date calcDateEnd,
                                                 long calcDay, long indexNumerator,
                                                 long indexDenominator, int scale) {
        if (ptlValuationDtoMap == null || ptlValuationDtoMap.isEmpty()) {
            return null;
        }
        BigDecimal 计算结果 = BigDecimal.ONE;
        BigDecimal 一万 = BigDecimal.valueOf(10000);
        Date tempCalcDate = calcDateStart;

        while (DateUtil.compareDate(calcDateEnd, tempCalcDate) >= 0) {
            if (ptlValuationDtoMap.containsKey(tempCalcDate)) {
                计算结果 = ArithmeticUtil.multiply(计算结果,
                    ArithmeticUtil.doAdd(BigDecimal.ONE, ArithmeticUtil.divide(
                        ptlValuationDtoMap.get(tempCalcDate).getTdyUnitValue(), 一万, scale)));
            }

            tempCalcDate = DateUtil.addDay(tempCalcDate, 1);
        }

        // 解决double  NaN转BigDecimal报错
        if(!Double.isNaN(Math.pow(计算结果.doubleValue(), (double)indexNumerator / indexDenominator)))
        计算结果 = BigDecimal.valueOf(
            Math.pow(计算结果.doubleValue(), (double)indexNumerator / indexDenominator));
        计算结果 = ArithmeticUtil.subtract(计算结果, BigDecimal.ONE);

        return 计算结果;
    }

    /**
     * calc年化收益率_净值型 收益率%=（计算日的累计净值-起始日的累计净值）/起始日的累计净值*计息基础/计息天数
     * 
     * @param basis 计息基础
     * @param ptlValuationDtoMap 组合估值列表
     * @param calcDateStart 计算日期起始日
     * @param calcDateEnd 计算日期结束日
     * @param calcDay 计算天数
     * @param scale 小数位
     * @return 年化收益率_净值型
     */
    private static BigDecimal calc年化收益率_净值型(int basis,
                                            Map<Date, MockPtlPortfolioValuationDto> ptlValuationDtoMap,
                                            Date calcDateStart, Date calcDateEnd, long calcDay,
                                            int scale) {
        if (ptlValuationDtoMap == null || ptlValuationDtoMap.isEmpty()) {
            return null;
        }
        BigDecimal 开始日净值 = BigDecimal.ONE;
        BigDecimal 结束日净值 = BigDecimal.ONE;
        if(ptlValuationDtoMap.get(calcDateEnd)==null||ptlValuationDtoMap.get(calcDateEnd).getTdyUnitValueSum()==null){
            logger.info(DateUtil.format(calcDateEnd)+"当天没有组合估值信息...取值为1");
        }else{
            结束日净值 =
                    ptlValuationDtoMap.get(calcDateEnd).getTdyUnitValueSum();
        }
        if(ptlValuationDtoMap.get(calcDateStart)==null||
                ptlValuationDtoMap.get(calcDateStart).getTdyUnitValueSum()==null){
            logger.info(DateUtil.format(calcDateStart)+"当天没有组合估值信息...取值为1");
        }else{
            // 计算日期为1时 说明是起息日当天  取期初净值1
            开始日净值 =
                    ptlValuationDtoMap.get(calcDateStart).getTdyUnitValueSum();
        }
        BigDecimal 计算结果 = ArithmeticUtil.subtract(结束日净值,开始日净值);
        计算结果 = ArithmeticUtil.divide(计算结果,开始日净值, scale);
        计算结果 = ArithmeticUtil.multiply(计算结果, BigDecimal.valueOf(basis));
        计算结果 = ArithmeticUtil.divide(计算结果, BigDecimal.valueOf(calcDay), scale);

        return 计算结果;
    }
}