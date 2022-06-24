package com.joyintech.tams.invest.product.product.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.component.business.evaluation.dto.FinancialProductInformationDto;
import com.joyintech.tams.component.business.evaluation.dto.SubportfolioFundProportionDetails;
import com.joyintech.tams.component.business.evaluation.dto.SubportfolioRangeIncomeContributionRateDetails;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.product.evaluationentrance.EvaluationEntrance;
import com.joyintech.tams.invest.product.evaluationentrance.dto.EvaluationRateDto;
import com.joyintech.tams.invest.product.product.dto.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author guyunfei
 * @see
 * @since
 */
@Service
public class FinProductEvaluate001Service extends BaseService {

    @Autowired
    private EvaluationEntrance entrance;

    /**
     * 产品名称联想下拉框
     * @param params
     * @return
     */
    public List<SelectOptionDto> getProNames(Map<String,Object> params){
        //起始日和评价日为必填
        if(StringUtil.isNotEmpty(params.get("vdate")) && StringUtil.isNotEmpty(params.get("mdate"))){
            return restTemplate.postForList(ServiceApplication.PRODUCT_SERVICE_NAME
                    + "/FinProductEvaluate/getProNames",params,SelectOptionDto.class);
        }else {
            return new ArrayList<>();
        }

    }

    /**
     * 产品发行前信息查询
     * @param params
     * @return
     */
    public Page<FinProductEvaluate001TabDto> selectForPage(Map<String,Object> params){
        return restTemplate.postForPage(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductEvaluate/selectForPage",
                params,
                FinProductEvaluate001TabDto.class
        );
    }

    /**
     * 评价日资产配置情况查询
     * @param params
     * @return
     */
    public Page<FinProductEvaluate001Tab2Dto> selectForPage2(Map<String,Object> params){
        Page<FinProductEvaluate001Tab2Dto> resultList = restTemplate.postForPage(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductEvaluate/selectForPage2",
                params,
                FinProductEvaluate001Tab2Dto.class
        );

        //组合份额查询
        List<FinProductEvaluate001Tab2Dto> portfolioShareList = restTemplate.postForList(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductEvaluate/queryCombinedShare",
                params,
                FinProductEvaluate001Tab2Dto.class
        );
        Map<String, BigDecimal> portfolioShareMap = portfolioShareList.stream()
                .collect(Collectors.toMap(FinProductEvaluate001Tab2Dto::getPortfolioId,
                        FinProductEvaluate001Tab2Dto::getPortfolioShare, (k1, k2) -> k1));
        List<FinProductEvaluate001Tab2Dto> root = resultList.getRoot();
        root.forEach(e->{
            BigDecimal data = portfolioShareMap.get(e.getPortfolioId());
            if(data != null){
                //设置组合份额
                e.setPortfolioShare(data);
            }
        });
        resultList.setRoot(root);

        return resultList;
    }

    /**
     * 产品净值表现查询
     * @param params
     * @return
     */
    public List<FinProductEvaluate001Tab3Dto> selectForPage3(Map<String,Object> params){
        return restTemplate.postForList(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductEvaluate/selectForPage3",
                params,
                FinProductEvaluate001Tab3Dto.class
        );
    }

    /**
     * 收益指标查询
     * @param params
     * @return
     */
    public List<FinProductEvaluate001Tab4Dto> selectForPage4(Map<String,Object> params) throws ParseException {
        translate(params);
        Date startDate = (Date) params.get("startDate");
        Date endDate = (Date) params.get("endDate");
        FinancialProductInformationDto informationDto = (FinancialProductInformationDto)params.get("dto");

        List<FinProductEvaluate001Tab4Dto> resultList = new ArrayList<>();

        //区间年化收益率
        List<EvaluationRateDto> eRateList = entrance.yearYield(startDate, endDate, informationDto);
        Map<Date, EvaluationRateDto> sayListMap = eRateList.stream()
                .collect(Collectors.toMap(EvaluationRateDto::getEndDate, Function.identity(), (k1, k2) -> k1));

        //净值增长率
        List<EvaluationRateDto> iilList = entrance.netValueGrowthRate(startDate, endDate, informationDto);
        Map<Date, EvaluationRateDto> iilListMap = iilList.stream()
                .collect(Collectors.toMap(EvaluationRateDto::getEndDate, Function.identity(), (k1, k2) -> k1));

        //胜率
        List<EvaluationRateDto> wdList = entrance.winRate(startDate, endDate, informationDto);
        Map<Date, EvaluationRateDto> wdListMap = wdList.stream()
                .collect(Collectors.toMap(EvaluationRateDto::getEndDate, Function.identity(), (k1, k2) -> k1));

        //正负收益周期比
        List<EvaluationRateDto> pmepList = entrance.plusMinusEarningsPeriodRatio(startDate, endDate, informationDto);
        Map<Date, EvaluationRateDto> pmepListMap = pmepList.stream()
                .collect(Collectors.toMap(EvaluationRateDto::getEndDate, Function.identity(), (k1, k2) -> k1));

        Date date = DateUtil.addDay(startDate, 1);
        while (DateUtil.compareDate(date, endDate) <= 0){
            FinProductEvaluate001Tab4Dto dto = new FinProductEvaluate001Tab4Dto();
            //产品代码
            dto.setFinprodId(informationDto.getFinProdId());
            //产品名称
            dto.setFinprodName((String) params.get("proName"));
            //区间开始日
            dto.setAccrStrDate(startDate);
            //区间结束日
            dto.setAccrEndDate(date);

            //年化收益率
            EvaluationRateDto say = sayListMap.get(date);
            if( say != null && say.getRate() != null){
                dto.setYearYieldDec(say.getRate());
            }
            //净值增长率
            EvaluationRateDto iils = iilListMap.get(date);
            if(iils != null && iils.getRate() != null){
                dto.setRate(iils.getRate());
            }
            //胜率
            EvaluationRateDto wd = wdListMap.get(date);
            if(wd != null && wd.getRate() != null){
                dto.setWinRate(wd.getRate());
            }
            //正负收益周期比
            EvaluationRateDto pmepr = pmepListMap.get(date);
            if(pmepr != null && pmepr.getRate() != null){
                dto.setPeriodRadio(pmepr.getRate());
            }

            resultList.add(dto);

            date = DateUtil.addDay(date, 1);
        }
        return resultList;
    }

    /**
     * 收益结构指标
     * @param params
     * @return
     * @throws Exception
     */
    public List<FinProductEvaluate001Tab5Dto> selectForPage5(Map<String,Object> params) throws ParseException {
        translate(params);
        Date startDate = (Date) params.get("startDate");
        Date endDate = (Date) params.get("endDate");
        FinancialProductInformationDto informationDto = (FinancialProductInformationDto)params.get("dto");

        List<FinProductEvaluate001Tab5Dto> resultList = new ArrayList<>();

        //根据产品代码查询子组合代码
        List<PtlPortfolio> list = restTemplate.postForList(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductEvaluate/getsubCombinations",
                params,
                PtlPortfolio.class
        );

        //子组合区间收益目标
        List<EvaluationRateDto> cportfolioIdList = entrance.rangeReturnTarget(startDate, endDate, informationDto);
        Map<String, EvaluationRateDto> cportfolioIdMap = cportfolioIdList.stream()
                .collect(Collectors.toMap(EvaluationRateDto::getSubcombinedCode, Function.identity(), (k1, k2) -> k1));

        //子组合收益贡献率、子组合区间年化收益率
        List<SubportfolioFundProportionDetails> contributionList = entrance
                .subportfolioIncomeContribution(startDate, endDate, informationDto);
        Map<String, SubportfolioFundProportionDetails> contributionMap = contributionList.stream()
                .collect(Collectors.toMap(SubportfolioFundProportionDetails::getSubcombinedCode, Function.identity(), (k1, k2) -> k1));

        //子组合目标收益贡献率
        List<SubportfolioRangeIncomeContributionRateDetails> targetContributionList = entrance
                .subportfolioGoalIncomeContribution(startDate, endDate, informationDto);
        Map<String, SubportfolioRangeIncomeContributionRateDetails> targetContributionMap = targetContributionList.stream()
                .collect(Collectors.toMap(SubportfolioRangeIncomeContributionRateDetails::getSubcombinedCode, Function.identity(), (k1, k2) -> k1));

        if(CollectionUtils.isNotEmpty(list) && list.size() > 0){
            for(int i = 0; i < list.size(); i++){
                PtlPortfolio ptlPortfolio = list.get(i);

                FinProductEvaluate001Tab5Dto dto = new FinProductEvaluate001Tab5Dto();
                //产品代码
                dto.setFinprodId(informationDto.getFinProdId());
                //产品名称
                dto.setFinprodName((String) params.get("proName"));
                //评价日
                dto.setMdate(endDate);
                //子组合代码
                dto.setSubPortfolioId(ptlPortfolio.getPortfolioId());
                //子组合名称
                dto.setSubPortfolioName(ptlPortfolio.getPortfolioName());

                //子组合区间收益目标
                EvaluationRateDto cportfolioId = cportfolioIdMap.get(ptlPortfolio.getPortfolioId());
                if(cportfolioId != null){
                    dto.setCportfolioId(cportfolioId.getRate());
                }

                //子组合收益贡献率、子组合区间年化收益率
                SubportfolioFundProportionDetails contribution = contributionMap.get(ptlPortfolio.getPortfolioId());
                if(contribution != null){
                    dto.setSectionAnnualYield(contribution.getSubportfolioIntervalAnnualized());
                    dto.setContribution(contribution.getSubportfolioIncomeContribution());
                }else{
                    dto.setSectionAnnualYield(BigDecimal.ZERO);
                    dto.setContribution(BigDecimal.ZERO);
                }

                //子组合目标收益贡献率
                SubportfolioRangeIncomeContributionRateDetails targetContribution = targetContributionMap.get(ptlPortfolio.getPortfolioId());
                if(targetContribution != null){
                    dto.setTargetContribution(targetContribution.getSubportfolioTargetReturnContribution());
                }

                resultList.add(dto);
            }
            return resultList;
        }else {
            return new ArrayList<>();
        }
    }

    /**
     * 风险指标
     * @param params
     * @return
     * @throws Exception
     */
    public List<FinProductEvaluate001Tab6Dto> selectForPage6(Map<String,Object> params) throws ParseException {
        translate(params);
        Date startDate = (Date) params.get("startDate");
        Date endDate = (Date) params.get("endDate");
        FinancialProductInformationDto informationDto = (FinancialProductInformationDto)params.get("dto");

        List<FinProductEvaluate001Tab6Dto> resultList = new ArrayList<>();

        //波动率、年化波动率
        List<EvaluationRateDto> volatilityList = entrance.fluctuateRate(startDate, endDate, informationDto);
        Map<Date, EvaluationRateDto> volatilityMap = volatilityList.stream()
                .collect(Collectors.toMap(EvaluationRateDto::getEndDate, Function.identity(), (k1, k2) -> k1));
        //下行标准差
        List<EvaluationRateDto> calcDownStdDevList = entrance.descendingStandardDeviation(startDate, endDate, informationDto);
        Map<Date, EvaluationRateDto> calcDownStdDevMap = calcDownStdDevList.stream()
                .collect(Collectors.toMap(EvaluationRateDto::getEndDate, Function.identity(), (k1, k2) -> k1));

        //年化特累诺比率
        List<EvaluationRateDto> yearTerenoRatioList = entrance.annualizedTraynorRatio(startDate, endDate, informationDto);
        Map<Date, EvaluationRateDto> yearTerenoRatioMap = yearTerenoRatioList.stream()
                .collect(Collectors.toMap(EvaluationRateDto::getEndDate, Function.identity(), (k1, k2) -> k1));

        //期间最大净值回撤
        List<EvaluationRateDto> periodMaximumReversionList = entrance.maximumRangeRetracement(startDate, endDate, informationDto);
        Map<Date, EvaluationRateDto> periodMaximumReversionMap = periodMaximumReversionList.stream()
                .collect(Collectors.toMap(EvaluationRateDto::getEndDate, Function.identity(), (k1, k2) -> k1));

        //历史最大净值回撤
        List<EvaluationRateDto> historyMaximumReversionList = entrance.historyBiggistWorthBack(startDate, endDate, informationDto);
        Map<Date, EvaluationRateDto> historyMaximumReversionMap = historyMaximumReversionList.stream()
                .collect(Collectors.toMap(EvaluationRateDto::getEndDate, Function.identity(), (k1, k2) -> k1));

        Date date = DateUtil.addDay(startDate, 1);
        while (DateUtil.compareDate(date,endDate) <= 0){
            FinProductEvaluate001Tab6Dto dto = new FinProductEvaluate001Tab6Dto();
            //产品代码
            dto.setFinprodId(informationDto.getFinProdId());
            //产品名称
            dto.setFinprodName((String) params.get("proName"));
            //区间开始日
            dto.setAccrStrDate(startDate);
            //区间结束日
            dto.setAccrEndDate(date);

            //波动率、年化波动率
            EvaluationRateDto volatility = volatilityMap.get(date);
            if(volatility != null){
                if(volatility.getRate() != null){
                    dto.setCalcYearVolatility(volatility.getRate());
                }
                if(volatility.getRate2() != null){
                    dto.setVolatility(volatility.getRate2());
                }
            }

            //下行标准差
            EvaluationRateDto calcDownStdDev = calcDownStdDevMap.get(date);
            if(calcDownStdDev != null){
                dto.setCalcDownStdDev(calcDownStdDev.getRate());
            }

            //年化特累诺比率
            EvaluationRateDto yearTerenoRatio = yearTerenoRatioMap.get(date);
            if(yearTerenoRatio != null){
                dto.setYearTerenoRatio(yearTerenoRatio.getRate());
            }

            //期间最大净值回撤
            EvaluationRateDto periodMaximumReversion = periodMaximumReversionMap.get(date);
            if(periodMaximumReversion != null){
                dto.setPeriodMaximumReversion(periodMaximumReversion.getRate());
            }

            //历史最大净值回撤
            EvaluationRateDto historyMaximumReversion = historyMaximumReversionMap.get(date);
            if(historyMaximumReversion != null){
                dto.setHistoryMaximumReversion(historyMaximumReversion.getRate());
            }
            resultList.add(dto);

            date = DateUtil.addDay(date,1);
        }
        return resultList;
    }

    /**
     * 风险调整收益指标
     * @param params
     * @return
     * @throws Exception
     */
    public List<FinProductEvaluate001Tab7Dto> selectForPage7(Map<String,Object> params) throws ParseException {
        translate(params);
        Date startDate = (Date) params.get("startDate");
        Date endDate = (Date) params.get("endDate");
        FinancialProductInformationDto informationDto = (FinancialProductInformationDto)params.get("dto");

        List<FinProductEvaluate001Tab7Dto> resultList = new ArrayList<>();

        //年化夏普比率
        List<EvaluationRateDto> yearSharpRatioList = entrance.annualizedSharpe(startDate, endDate, informationDto);
        Map<Date, EvaluationRateDto> yearSharpRatioMap = yearSharpRatioList.stream()
                .collect(Collectors.toMap(EvaluationRateDto::getEndDate, Function.identity(), (k1, k2) -> k1));

        //年化卡玛比率
        List<EvaluationRateDto> yearKamaRatioList = entrance.annualKarmaRatio(startDate, endDate, informationDto);
        Map<Date, EvaluationRateDto> yearKamaRatioMap = yearKamaRatioList.stream()
                .collect(Collectors.toMap(EvaluationRateDto::getEndDate, Function.identity(), (k1, k2) -> k1));

        //年化索提诺比率
        List<EvaluationRateDto> calcSortinoRatioList = entrance.annualizedSotinoRatio(startDate, endDate, informationDto);
        Map<Date, EvaluationRateDto> calcSortinoRatioMap = calcSortinoRatioList.stream()
                .collect(Collectors.toMap(EvaluationRateDto::getEndDate, Function.identity(), (k1, k2) -> k1));

        //詹森指数
        List<EvaluationRateDto> calcJensenIndexList = entrance.jensenIndex(startDate, endDate, informationDto);
        Map<Date, EvaluationRateDto> calcJensenIndexMap = calcJensenIndexList.stream()
                .collect(Collectors.toMap(EvaluationRateDto::getEndDate, Function.identity(), (k1, k2) -> k1));

        Date date = DateUtil.addDay(startDate, 1);
        while (DateUtil.compareDate(date,endDate) <= 0){
            FinProductEvaluate001Tab7Dto dto = new FinProductEvaluate001Tab7Dto();
            //产品代码
            dto.setFinprodId(informationDto.getFinProdId());
            //产品名称
            dto.setFinprodName((String) params.get("proName"));
            //区间开始日
            dto.setAccrStrDate(startDate);
            //区间结束日
            dto.setAccrEndDate(date);
            //年化夏普比率
            EvaluationRateDto yearSharpRatio = yearSharpRatioMap.get(date);
            if(yearSharpRatio.getRate() != null){
                dto.setYearSharpRatio(yearSharpRatio.getRate());
            }
            //年化卡玛比率
            EvaluationRateDto yearKamaRatio = yearKamaRatioMap.get(date);
            if(yearKamaRatio != null){
                dto.setYearKamaRatio(yearKamaRatio.getRate());
            }
            //年化索提诺比率
            EvaluationRateDto calcSortinoRatio = calcSortinoRatioMap.get(date);
            if(calcSortinoRatio != null){
                dto.setCalcSortinoRatio(calcSortinoRatio.getRate());
            }
            //詹森指数
            EvaluationRateDto calcJensenIndex = calcJensenIndexMap.get(date);
            if(calcJensenIndex != null){
                dto.setCalcJensenIndex(calcJensenIndex.getRate());
            }
            resultList.add(dto);

            date = DateUtil.addDay(date,1);
        }
        return resultList;
    }

    /**
     * 处理计算参数
     * @param params
     */
    private void translate(Map<String,Object> params){
        FinProductVarDto product = restTemplate.postForObject(
                ServiceApplication.PRODUCT_SERVICE_NAME + "/FinProductEvaluate/queryProduct",
                params,
                FinProductVarDto.class);
        Date startDate = DateUtil.parse((String) params.get("vdate"));
        Date endDate = DateUtil.parse((String) params.get("mdate"));
        FinancialProductInformationDto dto = new FinancialProductInformationDto();
        dto.setFinProdId(product.getFinprodId());
        dto.setFinProdType(product.getFinprodType2());
        dto.setRevenueType(product.getProfitType());
        dto.setProcessingMode(product.getDealMode());
        //跟校长确认 分支序号为0
        dto.setBranch("0");
        dto.setValueDate(product.getVdate());
        params.put("startDate",startDate);
        params.put("endDate",endDate);
        params.put("dto",dto);
        params.put("proName",product.getFinprodName());
    }

}
