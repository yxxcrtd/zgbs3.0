package com.joyintech.tams.invest.product.evaluationentrance;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.joyintech.tams.invest.common.constant.DictCodeBaseSubjectNatrue;
import com.joyintech.tams.invest.common.constant.ValDictCodeList;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.component.business.evaluation.dto.AverageStrategyDto;
import com.joyintech.tams.component.business.evaluation.dto.BenchmarkDto;
import com.joyintech.tams.component.business.evaluation.dto.BokPositionPrd;
import com.joyintech.tams.component.business.evaluation.dto.BookLatestInfoDto;
import com.joyintech.tams.component.business.evaluation.dto.BooksetAndBokDetailDto;
import com.joyintech.tams.component.business.evaluation.dto.ChildCombination;
import com.joyintech.tams.component.business.evaluation.dto.DailyYieldDto;
import com.joyintech.tams.component.business.evaluation.dto.EvalIndexAlgorithmCalcParam;
import com.joyintech.tams.component.business.evaluation.dto.FinancialProductInformationDto;
import com.joyintech.tams.component.business.evaluation.dto.MstIntRateInfoDto;
import com.joyintech.tams.component.business.evaluation.dto.PtlTargetReturnDto;
import com.joyintech.tams.component.business.evaluation.dto.RevenueTargetListDetails;
import com.joyintech.tams.component.business.evaluation.dto.StepProducts;
import com.joyintech.tams.component.business.evaluation.dto.SubportfolioFundProportionDetails;
import com.joyintech.tams.component.business.evaluation.dto.SubportfolioIncomeCapitalRatio;
import com.joyintech.tams.component.business.evaluation.dto.SubportfolioRangeIncomeContributionRateDetails;
import com.joyintech.tams.component.business.evaluation.impl.EvalIndexAlgorithmImplService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.product.evaluationentrance.dto.EvaluationRateDto;

/**
 * @author yangxiaofeng 2019年10月9日 评价指标计算 方法调用入口
 */
@Component
public class EvaluationEntrance {

	@Autowired
	private EvalIndexAlgorithmImplService evalIndexAlgorithmService;

	/**
	 * 区间最大回撤列表
	 */
	private List<EvaluationRateDto> rangeMaximumRetracementDetailsList;

	/**
	 * 区间年化收益率明细列表
	 */
	private List<EvaluationRateDto> sectionAnnualYieldDtoList ;

	/**
	 * 估值信息列表
	 */
	private List<BokPositionPrd> bokPositionPrdList;

    /**
     * 区间市场基准日收益率列表
     */
	private List<DailyYieldDto> marketBaiscYieldList;

	/**
	 * 初始化
	 */
	public void init(Date startDate, Date endDate, FinancialProductInformationDto financialProductInformationDto)
			throws ParseException {

		//利率行情列表
		List<MstIntRateInfoDto> mstIntRateInfoDtoList;

		//区间市场基准日收益率列表
		Map<Date, BigDecimal> mstIntRateInfoDtoMap = new HashMap<>();

		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("startDate", DateUtil.format(startDate,DateUtil.DEFAULT_DATE_FORMAT_EN));
		paraMap.put("endDate", DateUtil.format(endDate,DateUtil.DEFAULT_DATE_FORMAT_EN));
		paraMap.put("finProdId", financialProductInformationDto.getFinProdId());
		paraMap.put("branch", financialProductInformationDto.getBranch());
		bokPositionPrdList = evalIndexAlgorithmService.getBokPositionPrdList(paraMap);

		//估值列表Map
		Map<Date, BokPositionPrd> collect = bokPositionPrdList.stream()
				.collect(Collectors.toMap(BokPositionPrd::getCdate, Function.identity(), (k1, k2) -> k1));

		if(financialProductInformationDto.getRevenueType().equals(DictCodeList.收益类型_净值型.getValue())){
			Map<Date,BigDecimal> tdyYieldMap = getTdyYieldList(startDate, endDate, financialProductInformationDto,collect);
			//更新当日收益率
			bokPositionPrdList.forEach(e->{
                BigDecimal decimal = tdyYieldMap.get(e.getCdate());
                if(decimal != null){
                	e.setTdyYield(decimal);
				}
			});
		}

		Map<String, Object> paraMap2 = new HashMap<String, Object>();
		paraMap2.put("startDate", DateUtil.format(startDate,DateUtil.DEFAULT_DATE_FORMAT_EN));
		paraMap2.put("endDate", DateUtil.format(endDate,DateUtil.DEFAULT_DATE_FORMAT_EN));
		paraMap2.put("intRateType", DictCodeList.行情类型_利率.getValue());
		paraMap2.put("intRateId", DictCodeList.利率行情_银行间1天同业拆借利率.getValue());
		mstIntRateInfoDtoList = evalIndexAlgorithmService.getMstIntRateInfoDtoList(paraMap2);
		List<Date> emptyRateDays = new ArrayList<Date>();
		// 组装日收益列表，如果利率为空，将日期记录下来，使用语句查出最近的利率并赋值，如果利率不为空，直接和日期组对
		mstIntRateInfoDtoMap = mstIntRateInfoDtoList.stream()
				.collect(Collectors.toMap(MstIntRateInfoDto::getEffDate,MstIntRateInfoDto::getIntRate,(k1,k2)->k1));

		Date date = startDate;
		while (DateUtil.compareDate(date,endDate) <= 0){
			if(mstIntRateInfoDtoMap.containsKey(date)){
				BigDecimal decimal = mstIntRateInfoDtoMap.get(date);
				if(decimal == null){
					emptyRateDays.add(date);
				}
			}else {
				emptyRateDays.add(date);
			}
			date = DateUtil.addDay(date,1);
		}

		for (Date emptyRateDate : emptyRateDays) {
			Map<String,String> map = new HashMap<>();
			map.put("rateEmptyDay",DateUtil.format(emptyRateDate,DateUtil.DEFAULT_DATE_FORMAT_EN));
			map.put("intRateType", DictCodeList.行情类型_利率.getValue());
			map.put("intRateId", DictCodeList.利率行情_银行间1天同业拆借利率.getValue());
			// 向从前查询最近的非空利率
			BigDecimal dateRate = evalIndexAlgorithmService.nearistNotEmptyRateDayRate(map);
			// 重新将当天利率插入日收益率列表
			mstIntRateInfoDtoMap.put(emptyRateDate, dateRate);
		}

		marketBaiscYieldList = new ArrayList<>();
		for (Date key : mstIntRateInfoDtoMap.keySet()) {
			DailyYieldDto dyd = new DailyYieldDto();
			dyd.setDate(key);
			dyd.setYield(mstIntRateInfoDtoMap.get(key));
			marketBaiscYieldList.add(dyd);
		}
	}

	/**
	 * 净值型日收益率计算
	 * @param startDate
	 * @param date
	 * @param financialProductInformationDto
	 * @param collect
	 * @return
	 */
	private Map<Date,BigDecimal> getTdyYieldList(Date startDate, Date date,FinancialProductInformationDto financialProductInformationDto,Map<Date, BokPositionPrd> collect){

		Map<Date,BigDecimal> resultMap = new HashMap<>();
		//期间日
		Date innerDate = startDate;
		while (DateUtil.compareDate(innerDate,date) <= 0){
			BigDecimal sectionStartingDayTotalNav = BigDecimal.ZERO;
			BigDecimal sectionStartingDayNav = BigDecimal.ZERO;
			BigDecimal sectionEndDayTotalNav = BigDecimal.ZERO;
			BigDecimal sectionEndDayNav = BigDecimal.ZERO;
			//金融产品起息日
			Date valueDate = financialProductInformationDto.getValueDate();
			//起息日前默认为1计算
			if(DateUtil.compareDate(DateUtil.addDay(innerDate,-1),valueDate) < 0){
				//区间开始日累计净值
				sectionStartingDayTotalNav = BigDecimal.ONE;
				//区间开始日单位净值
				sectionStartingDayNav = BigDecimal.ONE;
			}else if (collect.get(DateUtil.addDay(innerDate,-1)) != null) {
				BokPositionPrd bokPositionPrd = collect.get(DateUtil.addDay(innerDate, -1));

				//区间开始日累计净值
				sectionStartingDayTotalNav = bokPositionPrd.getTdyUnitValueSum();
				//区间开始日单位净值
				sectionStartingDayNav = bokPositionPrd.getTdyUnitValue();
			}else{
				Map<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("cdate", DateUtil.addDay(innerDate,-1));
				paraMap.put("finProdId", financialProductInformationDto.getFinProdId());
				paraMap.put("branch", financialProductInformationDto.getBranch());
				BokPositionPrd bokPositionPrdOfDay = evalIndexAlgorithmService.getBokPositionPrdOfDay(paraMap);
				if(bokPositionPrdOfDay != null){
					//区间开始日累计净值
					sectionStartingDayTotalNav = bokPositionPrdOfDay.getTdyUnitValueSum();
					//区间开始日单位净值
					sectionStartingDayNav = bokPositionPrdOfDay.getTdyUnitValue();
				}
			}
			if(collect.get(innerDate) != null){
				BokPositionPrd bokPositionPrd = collect.get(innerDate);
				//区间结束日累计单位净值
				sectionEndDayTotalNav = bokPositionPrd.getTdyUnitValueSum();
				//区间结束日单位净值
				sectionEndDayNav = bokPositionPrd.getTdyUnitValue();
			}

			//调用共通方法：区间年化收益率
			EvalIndexAlgorithmCalcParam param = new EvalIndexAlgorithmCalcParam();//参数
			param.setStartDate(DateUtil.addDay(innerDate, -1));
			param.setEndDate(innerDate);
			param.setsAccuNetValue(sectionStartingDayTotalNav);
			param.seteAccuNetValue(sectionEndDayTotalNav);
			param.setsNetValue(sectionStartingDayNav);
			//区间年化收益率
			BigDecimal sectionAnnualYield = evalIndexAlgorithmService.calcYearYield(param);

			resultMap.put(innerDate,sectionAnnualYield);

			innerDate = DateUtil.addDay(innerDate,1);
		}
		return resultMap;
	}

	/**
	 * 获取有效估值信息
	 * @param bokPositionPrdList
	 * @param date
	 * @return
	 */
	private List<DailyYieldDto> getEffectiveList(List<BokPositionPrd> bokPositionPrdList,Date date){
		List<DailyYieldDto> dtoList = new ArrayList<>();

		for (BokPositionPrd bokPositionPrd : bokPositionPrdList) {
			if (dateIsOrNotLessAndEqual(bokPositionPrd.getCdate(), date)) {
				DailyYieldDto dyd = new DailyYieldDto();
				dyd.setDate(bokPositionPrd.getCdate());
				dyd.setYield(bokPositionPrd.getTdyYield());
				dtoList.add(dyd);
			}
		}
		return dtoList;
	}

	/**
	 * 获取有效的区间市场基准日收益率列表
	 * @param marketBaiscYieldList
	 * @param date
	 * @return
	 */
	public List<DailyYieldDto> getEffctiveMarketBaiscYields(List<DailyYieldDto>marketBaiscYieldList,Date date){
		List<DailyYieldDto> list = marketBaiscYieldList.stream()
				.filter(e -> DateUtil.compareDate(e.getDate(), date) <= 0).collect(Collectors.toList());
		return list;
	}

	/**
	 * 业绩基准列表  胜率、年化特雷诺、詹森指数
	 * @param startDate
	 * @param date
	 * @param endDate
	 * @param financialProductInformationDto
	 */
	private List<DailyYieldDto> getBenchmarks(Date startDate, Date date, Date endDate,FinancialProductInformationDto financialProductInformationDto){
		//业绩基准列表
		List<BenchmarkDto> benchmarkDtoList = new ArrayList<BenchmarkDto>();

		if (financialProductInformationDto.getFinProdType().equals(DictCodeListExt1.金融产品类型_理财产品.getValue())) {

			if (!financialProductInformationDto.getProcessingMode().equals(DictCodeList.处理模式_分档.getValue())) {

				// 变量.基准信息列表 = 返回值
				benchmarkDtoList = evalIndexAlgorithmService.calcBenchmark(startDate, date,
						financialProductInformationDto.getFinProdId(), financialProductInformationDto.getBranch(),
						"1");
			}else if (financialProductInformationDto.getProcessingMode().equals(DictCodeList.处理模式_分档.getValue())) {

				// 调用分档查询
				List<StepProducts> spList = evalIndexAlgorithmService
						.fileProductQuery(financialProductInformationDto.getFinProdId());

				List<BenchmarkDto> bmList = new ArrayList<>();
				for (StepProducts sp : spList) { // 分档列表
					// 业绩基准计算
					List<BenchmarkDto> bdList = evalIndexAlgorithmService.calcBenchmark(startDate, date,
							sp.getFinancialProductCode(),
							sp.getBranchNumber(), null);

					bmList.addAll(bdList);
				}

				if(CollectionUtils.isNotEmpty(bmList)){
					Map<Date, List<BenchmarkDto>> collect = bmList.stream().collect(Collectors.groupingBy(BenchmarkDto::getEffDate));

					Date innerDate = startDate;
					while (DateUtil.compareDate(innerDate,date) <= 0){
						BigDecimal decimal = BigDecimal.ZERO;
						List<BenchmarkDto> benchmarkDtos = collect.get(innerDate);
						if(benchmarkDtos != null){
							//空指针处理
							benchmarkDtos.forEach(e->{
								if(e.getLowestBen() == null){
									e.setLowestBen(BigDecimal.ZERO);
								}
							});
							decimal = benchmarkDtos.stream().map(BenchmarkDto::getLowestBen).reduce(BigDecimal::add).get();
						}

						BenchmarkDto bdto = new BenchmarkDto();
						bdto.setLowestBen(ArithmeticUtil.divide(decimal, spList.size(), 16));
						bdto.setEffDate(innerDate);

						benchmarkDtoList.add(bdto);

						innerDate = DateUtil.addDay(innerDate,1);
					}
				}
			}
		} else if (financialProductInformationDto.getFinProdType()
				.equals(DictCodeListExt1.金融产品类型_投资组合.getValue())) {

			Date innerDate = DateUtil.addDay(startDate, 1);
			while (DateUtil.compareDate(innerDate, endDate) <= 0) {

				// 收益指标计算
				List<PtlTargetReturnDto> ptrdList = evalIndexAlgorithmService.calcTargetReturn(innerDate,
						financialProductInformationDto.getFinProdId(), null);

				// 业绩基准列表.add 业绩基准列表明细
				BenchmarkDto benchmarkDto = new BenchmarkDto();
				benchmarkDto.setEffDate(ptrdList.get(0).getEffDate());
				benchmarkDto.setLowestBen(ptrdList.get(0).getIncomeTarget());
				benchmarkDtoList.add(benchmarkDto);

				innerDate = DateUtil.addDay(innerDate, 1);
			}

		}
		//业绩基准列表转换
		List<DailyYieldDto> standardList = new ArrayList<>();
		benchmarkDtoList.forEach(e->{
			DailyYieldDto dto = new DailyYieldDto();
			dto.setDate(e.getEffDate());
			if(e.getLowestBen() == null){
				dto.setYield(BigDecimal.ZERO);
			}else{
				dto.setYield(e.getLowestBen());
			}
			standardList.add(dto);
		});
		return standardList;
	}

	/**
	 * 一、区间年化收益率 1开始日期 2结束日期 3金融产品代码 4分支序号 5金融产品类型 6收益类型
	 */
	public List<EvaluationRateDto> yearYield(Date startDate, Date endDate,
											 FinancialProductInformationDto financialProductInformationDto) throws ParseException {
		// 数据初始化
		this.init(startDate, endDate, financialProductInformationDto);
		sectionAnnualYieldDtoList= new ArrayList<>();

		Date date = DateUtil.addDay(startDate, 1);
		while (DateUtil.compareDate(date, endDate) <= 0) {

            //红利再投信息
            List<BookLatestInfoDto> dividendReinvestmentInformation = new ArrayList<BookLatestInfoDto>();

            //区间开始日累计单位净值
            BigDecimal sectionStartingDayTotalNav = BigDecimal.ZERO;

            //区间开始日单位净值
            BigDecimal sectionStartingDayNav = BigDecimal.ZERO;

            //区间结束日累计单位净值
            BigDecimal sectionEndDayTotalNav = BigDecimal.ZERO;

            //区间结束日单位净值
            BigDecimal sectionEndDayNav = BigDecimal.ZERO;

			if (DictCodeList.收益类型_净值型.getValue().equals(financialProductInformationDto.getRevenueType())) {
				for (BokPositionPrd bokPositionPrd : bokPositionPrdList) {
					if (dateIsOrNotEquality(bokPositionPrd.getCdate(), startDate)) { // 日期等于开始日期
						sectionStartingDayTotalNav = bokPositionPrd.getTdyUnitValueSum();
						sectionStartingDayNav = bokPositionPrd.getTdyUnitValue();
					}
					if (dateIsOrNotEquality(bokPositionPrd.getCdate(), date)) { // 日期等于评价日
						sectionEndDayTotalNav = bokPositionPrd.getTdyUnitValueSum();
						sectionEndDayNav = bokPositionPrd.getTdyUnitValue();
					}
				}

				EvalIndexAlgorithmCalcParam param = new EvalIndexAlgorithmCalcParam();
				param.setStartDate(startDate);
				param.setEndDate(date);
				param.setsAccuNetValue(sectionStartingDayTotalNav);
				param.seteAccuNetValue(sectionEndDayTotalNav);
				param.setsNetValue(sectionStartingDayNav);
                // 区间年化收益率
				BigDecimal sectionAnnualYield = evalIndexAlgorithmService.calcYearYield(param);

				EvaluationRateDto sectionAnnualYieldDto = new EvaluationRateDto(startDate, date, sectionAnnualYield);
				sectionAnnualYieldDtoList.add(sectionAnnualYieldDto);
			}else  if (DictCodeList.收益类型_货币型.getValue().equals(financialProductInformationDto.getRevenueType())) { // 收益类型为货币型
				// 周期内发生的红利再投查询

				List<String> baseSubjectNatureList = new ArrayList<String>();
				baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.应付利润.getValue());

				Map<String, Object> paraMap = new HashMap<String, Object>();
				paraMap.put("finProdId", financialProductInformationDto.getFinProdId());
				paraMap.put("expectedReturnType", DictCodeList.收益类型_收益型.getValue());

				//凭证最新账务查询公共方法 获取这个方法的参数
				List<BooksetAndBokDetailDto> cysYearRateParam = evalIndexAlgorithmService.getCysYearRateParam(paraMap);

				if(cysYearRateParam.size() > 0){
					BooksetAndBokDetailDto booksetAndBokDetailDto = cysYearRateParam.get(0);

					//套账代码
					String booksetId = booksetAndBokDetailDto.getBooksetId();
					//账务明细代码
					String bokDetailId = booksetAndBokDetailDto.getBokDetailId();

					//凭证最新账务查询
					dividendReinvestmentInformation = evalIndexAlgorithmService.getBookLatestInfo(booksetId, date,
							bokDetailId, baseSubjectNatureList, "8034", null, null, null, startDate, date);

				}
				//获取有效估值信息
				final Date cdate = date;
				List<BokPositionPrd> newList = bokPositionPrdList.stream()
						.filter(e -> DateUtil.compareDate(e.getCdate(), cdate) <= 0).collect(Collectors.toList());
				EvaluationRateDto sectionAnnualYieldDto = new EvaluationRateDto(startDate, date,
						evalIndexAlgorithmService.getCysYearRate(newList, startDate, date,
								dividendReinvestmentInformation, 365, ValDictCodeList.费前费后区分_费后.getValue()));

				// 区间年化收益率明细加入到区间年化收益率列表
				sectionAnnualYieldDtoList.add(sectionAnnualYieldDto);

			}// 区间年化收益率明细


			date = DateUtil.addDay(date, 1);
		}

		return sectionAnnualYieldDtoList;
	}

	/**
	 * 二、净值增长率 1开始日期 2结束日期 3金融产品代码 4分支序号 5金融产品类型 6收益类型
	 */
	public List<EvaluationRateDto> netValueGrowthRate(Date startDate, Date endDate,
													  FinancialProductInformationDto financialProductInformationDto) throws ParseException {

		this.init(startDate, endDate, financialProductInformationDto);

		List<EvaluationRateDto> list = new ArrayList<EvaluationRateDto>();

		Date date = DateUtil.addDay(startDate, 1);
		while (DateUtil.compareDate(date, endDate) <= 0) {

            //区间开始日单位净值
            BigDecimal sectionStartingDayNav = BigDecimal.ZERO;
            //区间开始日累计单位净值
            BigDecimal sectionStartingDayTotalNav = BigDecimal.ZERO;
            //区间结束日累计单位净值
            BigDecimal sectionEndDayTotalNav = BigDecimal.ZERO;
            //区间结束日单位净值
            BigDecimal sectionEndDayNav = BigDecimal.ZERO;
			// 净值增长率
			BigDecimal rate = BigDecimal.ZERO;

			if (DictCodeList.收益类型_净值型.getValue().equals(financialProductInformationDto.getRevenueType())) {

				for (BokPositionPrd bokPositionPrd : bokPositionPrdList) {

					if (dateIsOrNotEquality(bokPositionPrd.getCdate(), startDate)) {
						sectionStartingDayTotalNav = bokPositionPrd.getTdyUnitValueSum();
						sectionStartingDayNav = bokPositionPrd.getTdyUnitValue();
					}

					if (dateIsOrNotEquality(bokPositionPrd.getCdate(), date)) {
						sectionEndDayTotalNav = bokPositionPrd.getTdyUnitValueSum();
						sectionEndDayNav = bokPositionPrd.getTdyUnitValue();
					}
				}
				EvalIndexAlgorithmCalcParam param = new EvalIndexAlgorithmCalcParam();
				param.setStartDate(startDate);
				param.setEndDate(date);
				param.setsAccuNetValue(sectionStartingDayTotalNav);
				param.seteAccuNetValue(sectionEndDayTotalNav);
				param.setsNetValue(sectionStartingDayNav);

				// 净值增长率
				rate = evalIndexAlgorithmService.calcNetValueGrowthRate(param);
			}else if (DictCodeList.收益类型_货币型.getValue().equals(financialProductInformationDto.getRevenueType())) {

				for (BokPositionPrd bokPositionPrd : bokPositionPrdList) {
					if (dateIsOrNotLessAndEqual(bokPositionPrd.getCdate(), date)) {
                        // 净值增长率
						rate = ArithmeticUtil.doAdd(rate,
								ArithmeticUtil.divide(bokPositionPrd.getTdyUnitValue(), 10000, 16));
					}
				}
			}

			EvaluationRateDto dto = new EvaluationRateDto(startDate, date, rate);
			list.add(dto);
			date = DateUtil.addDay(date, 1);
		}
		return list;
	}

	/**
	 * 三、胜率 1开始日期 2结束日期 3金融产品代码 4分支序号 5金融产品类型 6收益类型 7处理模式
	 */
	public List<EvaluationRateDto> winRate(Date startDate, Date endDate,
										   FinancialProductInformationDto financialProductInformationDto) throws ParseException {

		this.init(startDate, endDate, financialProductInformationDto);

		//胜率列表
		List<EvaluationRateDto> winningDtoList = new ArrayList<EvaluationRateDto>();

		Date date = DateUtil.addDay(startDate, 1);
		while (DateUtil.compareDate(date, endDate) <= 0) {

			//新的估值信息列表
			List<DailyYieldDto> dydList = getEffectiveList(bokPositionPrdList,date);
            //业绩基准列表
            List<DailyYieldDto> newBenchmarks = getBenchmarks(startDate,date,endDate,financialProductInformationDto);

			EvalIndexAlgorithmCalcParam param = new EvalIndexAlgorithmCalcParam();
			param.setStartDate(startDate);
			param.setEndDate(date);

			param.setDailyBasicRateList(newBenchmarks);
			param.setDailyInvestYieldList(dydList);

			BigDecimal winRate = evalIndexAlgorithmService.calcWinRate(param);

			EvaluationRateDto winningDto = new EvaluationRateDto(startDate,date,winRate);

			winningDtoList.add(winningDto);

			date = DateUtil.addDay(date, 1);
		}

		return winningDtoList;
	}

	/**
	 * 四、正负收益周期比 1开始日期 2结束日期 3金融产品代码 4分支序号 5金融产品类型 6收益类型
	 */
	public List<EvaluationRateDto> plusMinusEarningsPeriodRatio(Date startDate, Date endDate,
																FinancialProductInformationDto financialProductInformationDto) throws ParseException {

		//正负收益周期比列表
		List<EvaluationRateDto> plusMinusEarningsPeriodRatioDtoList = new ArrayList<EvaluationRateDto>();

		this.init(startDate, endDate, financialProductInformationDto);

		Date date = DateUtil.addDay(startDate, 1);
		while (DateUtil.compareDate(date, endDate) <= 0) {

			List<BokPositionPrd> bppList = new ArrayList<BokPositionPrd>();

			for (BokPositionPrd bokPositionPrd : bokPositionPrdList) {

				if (dateIsOrNotLessAndEqual(bokPositionPrd.getCdate(), date)) {

					bppList.add(bokPositionPrd);
				}
			}
			//按照日期排序
			bppList.sort(Comparator.comparing(BokPositionPrd::getCdate));

			BigDecimal periodRadio = evalIndexAlgorithmService.calcPeriodRadio(bppList);

			EvaluationRateDto plusMinusEarningsPeriodRatioDto = new EvaluationRateDto(startDate,date,periodRadio);

			plusMinusEarningsPeriodRatioDtoList.add(plusMinusEarningsPeriodRatioDto);

			date = DateUtil.addDay(date, 1);
		}

		return plusMinusEarningsPeriodRatioDtoList;
	}

	/**
	 * 五、波动率 1开始日期 2结束日期 3金融产品代码 4分支序号 5金融产品类型 6收益类型
	 */
	public List<EvaluationRateDto> fluctuateRate(Date startDate, Date endDate,
												 FinancialProductInformationDto financialProductInformationDto) throws ParseException {

		//波动率列表
		List<EvaluationRateDto> volatilityDetailDtoList = new ArrayList<EvaluationRateDto>();

		this.init(startDate, endDate, financialProductInformationDto);

		Date date = DateUtil.addDay(startDate, 1);
		while (DateUtil.compareDate(date, endDate) <= 0) {
            // 共通方法 波动率 返回 波动率
            BigDecimal volatility =BigDecimal.ZERO;
            // 共通方法 年化波动率 返回 年化波动率
            BigDecimal yearVolatility=BigDecimal.ZERO;

			//获取有效估值信息
			List<DailyYieldDto> dtoList = getEffectiveList(bokPositionPrdList,date);
			if (dtoList.size() > 0) {
				// 共通方法 波动率 返回 波动率
				volatility = evalIndexAlgorithmService.calcVolatility(dtoList);
				// 共通方法 年化波动率 返回 年化波动率
				yearVolatility = evalIndexAlgorithmService.calcYearVolatility(dtoList);
			}

			EvaluationRateDto vdd = new EvaluationRateDto(startDate,date,yearVolatility,volatility);

			volatilityDetailDtoList.add(vdd);

			date = DateUtil.addDay(date, 1);
		}

		return volatilityDetailDtoList;
	}

	/**
	 * 六、下行标准差 1开始日期 2结束日期 3金融产品代码 4分支序号 5金融产品类型 6收益类型
	 */
	public List<EvaluationRateDto> descendingStandardDeviation(Date startDate, Date endDate,
															   FinancialProductInformationDto financialProductInformationDto) throws ParseException {

		//下行标准差列表
		List<EvaluationRateDto> descendingStandardDeviationDetailsList = new ArrayList<EvaluationRateDto>();

		this.init(startDate, endDate, financialProductInformationDto);

		Date date = DateUtil.addDay(startDate, 1);
		while (DateUtil.compareDate(date, endDate) <= 0) {

			List<DailyYieldDto> dydList = getEffectiveList(bokPositionPrdList,date);
			// 调用共通方法 下行标准差
			BigDecimal yearDeviation=BigDecimal.ZERO;
			if(dydList.size()>0){
					yearDeviation = evalIndexAlgorithmService.calcYearDownStdDev(dydList);
			}
			EvaluationRateDto dsdd = new EvaluationRateDto(startDate,date,yearDeviation);

			descendingStandardDeviationDetailsList.add(dsdd);
			date = DateUtil.addDay(date, 1);
		}

		return descendingStandardDeviationDetailsList;
	}

	/**
	 * 七、年化特雷诺比例 1开始日期 2结束日期 3金融产品代码 4分支序号 5金融产品类型 6收益类型
	 */
	public List<EvaluationRateDto> annualizedTraynorRatio(Date startDate, Date endDate,
														  FinancialProductInformationDto financialProductInformationDto) throws ParseException {

		this.init(startDate, endDate, financialProductInformationDto);

		List<EvaluationRateDto> yearRateList = new ArrayList<EvaluationRateDto>();

		// 循环日期区间的每一天
		Date date = DateUtil.addDay(startDate, 1);
		while (DateUtil.compareDate(date, endDate) <= 0) {

			//估值信息列表
			List<DailyYieldDto> yieldList = getEffectiveList(bokPositionPrdList,date);

			//业绩基准列表
			List<DailyYieldDto> standardList = getBenchmarks(startDate,date,endDate,financialProductInformationDto);

			// 年化特雷诺比例
            BigDecimal yearRate=BigDecimal.ZERO;

			List<DailyYieldDto> effctiveMarketBaiscYields = getEffctiveMarketBaiscYields(marketBaiscYieldList,date);

			if (yieldList.size()>0 && effctiveMarketBaiscYields.size()>0 && standardList.size()>0){
				yearRate = evalIndexAlgorithmService.calcTreynorRatio(yieldList, standardList,effctiveMarketBaiscYields);
			}
			EvaluationRateDto evaluationRateDto=new EvaluationRateDto(startDate,date,yearRate);
			yearRateList.add(evaluationRateDto);

			date = DateUtil.addDay(date, 1);
		}

		return yearRateList;
	}

	/**
	 * 八、区间最大回撤 1开始日期 2结束日期 3金融产品代码 4分支序号 5金融产品类型 6收益类型
	 */
	public List<EvaluationRateDto> maximumRangeRetracement(Date startDate, Date endDate,
														   FinancialProductInformationDto financialProductInformationDto) throws ParseException {

		this.init(startDate, endDate, financialProductInformationDto);
		rangeMaximumRetracementDetailsList=new ArrayList<>();
		Date date = DateUtil.addDay(startDate, 1);
		while (DateUtil.compareDate(date, endDate) <= 0) {

			//单位净值列表
			List<DailyYieldDto> dydList = new ArrayList<DailyYieldDto>();

			for (BokPositionPrd bokPositionPrd : bokPositionPrdList) {
				if (dateIsOrNotLessAndEqual(bokPositionPrd.getCdate(), date)) {
					DailyYieldDto dyd = new DailyYieldDto();
					dyd.setDate(bokPositionPrd.getCdate());
					dyd.setYield(bokPositionPrd.getTdyUnitValue());
					dydList.add(dyd);
				}
			}
			BigDecimal rate = BigDecimal.ZERO;
			if(dydList.size() > 0){
				//最大回撤
				rate = evalIndexAlgorithmService.calcMaxRetreat(dydList);
			}
			EvaluationRateDto rmrd = new EvaluationRateDto(startDate, date, rate);
			rangeMaximumRetracementDetailsList.add(rmrd);

			date = DateUtil.addDay(date, 1);
		}

		return rangeMaximumRetracementDetailsList;
	}

	/**
	 * 九、年化夏普比 1开始日期 2结束日期 3金融产品代码 4分支序号 5金融产品类型 6收益类型
	 */
	public List<EvaluationRateDto> annualizedSharpe(Date startDate, Date endDate,
													FinancialProductInformationDto financialProductInformationDto) throws ParseException {

		this.init(startDate, endDate, financialProductInformationDto);

		List<EvaluationRateDto> yearRateList = new ArrayList<EvaluationRateDto>();

		Date date = DateUtil.addDay(startDate, 1);
		while (DateUtil.compareDate(date, endDate) <= 0) {

			//估值信息列表
			List<DailyYieldDto> yieldList = getEffectiveList(bokPositionPrdList,date);

			// 获得年化夏普比
            BigDecimal yearRate = BigDecimal.ZERO;

			List<DailyYieldDto> effctiveMarketBaiscYields = getEffctiveMarketBaiscYields(marketBaiscYieldList, date);

			if(yieldList.size() > 0 && effctiveMarketBaiscYields.size() > 0) {
				yearRate = evalIndexAlgorithmService.calcSharpeRatio(yieldList, effctiveMarketBaiscYields);
			}
			EvaluationRateDto evaluationRateDto=new EvaluationRateDto(startDate,date,yearRate);
			date = DateUtil.addDay(date, 1);

			yearRateList.add(evaluationRateDto);
		}

		return yearRateList;
	}

	/**
	 * 十、年化卡玛比率 1开始日期 2结束日期 3金融产品代码 4分支序号 5金融产品类型 6收益类型
	 */
	public List<EvaluationRateDto> annualKarmaRatio(Date startDate, Date endDate,
													FinancialProductInformationDto financialProductInformationDto) throws ParseException {

		this.init(startDate, endDate, financialProductInformationDto);

        //区间年化收益率
        sectionAnnualYieldDtoList=this.yearYield(startDate,endDate,financialProductInformationDto);
		Map<Date, BigDecimal> yieldMap = sectionAnnualYieldDtoList.stream()
				.collect(Collectors.toMap(EvaluationRateDto::getEndDate, EvaluationRateDto::getRate));


		//区间最大回撤
        rangeMaximumRetracementDetailsList = maximumRangeRetracement(startDate,endDate,financialProductInformationDto);
		Map<Date, BigDecimal> returnMap = rangeMaximumRetracementDetailsList.stream()
				.collect(Collectors.toMap(EvaluationRateDto::getEndDate, EvaluationRateDto::getRate));


		List<EvaluationRateDto> rateList = new ArrayList<EvaluationRateDto>();

		Date date = DateUtil.addDay(startDate, 1);
		while (DateUtil.compareDate(date, endDate) <= 0) {
            BigDecimal rate = BigDecimal.ZERO;

			BigDecimal decimalYield = yieldMap.get(date);

			BigDecimal decimalReturn = returnMap.get(date);

			//年化卡玛比率 = 区间年化收益率/区间最大回撤
			if(decimalYield != null && decimalReturn != null){
				rate = ArithmeticUtil.divide(decimalYield, decimalReturn, 16);
			}
			EvaluationRateDto evaluationRateDto=new EvaluationRateDto(startDate,date,rate);
			rateList.add(evaluationRateDto);

			date = DateUtil.addDay(date, 1);
		}

		return rateList;
	}

	/**
	 * 十一、年化索提诺比率 1开始日期 2结束日期 3金融产品代码 4分支序号 5金融产品类型 6收益类型
	 */
	public List<EvaluationRateDto> annualizedSotinoRatio(Date startDate, Date endDate,
														 FinancialProductInformationDto financialProductInformationDto) throws ParseException {

		this.init(startDate, endDate, financialProductInformationDto);

		List<EvaluationRateDto> yearRateList = new ArrayList<EvaluationRateDto>();

		Date date = DateUtil.addDay(startDate, 1);
		while (DateUtil.compareDate(date, endDate) <= 0) {

			List<DailyYieldDto> yieldList = getEffectiveList(bokPositionPrdList,date);

			// 年化索提诺比率
            BigDecimal yearRate=BigDecimal.ZERO;
			List<DailyYieldDto> effctiveMarketBaiscYields = getEffctiveMarketBaiscYields(marketBaiscYieldList, date);
			if (yieldList.size()>0 && effctiveMarketBaiscYields.size()>0){
				yearRate = evalIndexAlgorithmService.calcSortinoRatio(yieldList, effctiveMarketBaiscYields);}
			EvaluationRateDto evaluationRateDto=new EvaluationRateDto(startDate,date,yearRate);
			yearRateList.add(evaluationRateDto);

			date = DateUtil.addDay(date, 1);
		}
		return yearRateList;
	}

	/**
	 * 十二、詹森指数 1开始日期 2结束日期 3金融产品代码 4分支序号 5金融产品类型 6收益类型
	 */
	public List<EvaluationRateDto> jensenIndex(Date startDate, Date endDate,
											   FinancialProductInformationDto financialProductInformationDto) throws ParseException {

		this.init(startDate, endDate, financialProductInformationDto);

		List<EvaluationRateDto> jensenRateList = new ArrayList<EvaluationRateDto>();

		Date date = DateUtil.addDay(startDate, 1);
		while (DateUtil.compareDate(date, endDate) <= 0) {

			List<DailyYieldDto> yieldList = getEffectiveList(bokPositionPrdList,date);

			//业绩基准列表
			List<DailyYieldDto> standardList = getBenchmarks(startDate,date,endDate,financialProductInformationDto);

			// 詹森指数
            BigDecimal jensenRate=BigDecimal.ZERO;
			List<DailyYieldDto> effctiveMarketBaiscYields = getEffctiveMarketBaiscYields(marketBaiscYieldList, date);
			if (yieldList.size()>0 && effctiveMarketBaiscYields.size()>0 && standardList.size()>0){
				jensenRate = evalIndexAlgorithmService.calcJensenIndex(yieldList,standardList, effctiveMarketBaiscYields);}
			EvaluationRateDto evaluationRateDto=new EvaluationRateDto(startDate,date,jensenRate);
			jensenRateList.add(evaluationRateDto);

			date = DateUtil.addDay(date, 1);
		}
		return jensenRateList;
	}

	/**
	 * 十三、历史最大净值回撤 1开始日期 2结束日期 3金融产品代码 4分支序号 5金融产品类型 6收益类型
	 */
	public List<EvaluationRateDto> historyBiggistWorthBack(Date startDate, Date endDate,
														   FinancialProductInformationDto financialProductInformationDto) throws ParseException {

		this.init(startDate, endDate, financialProductInformationDto);

		List<EvaluationRateDto> yearBackRateList = new ArrayList<EvaluationRateDto>();

		Date date = DateUtil.addDay(startDate, 1);
		while (DateUtil.compareDate(date, endDate) <= 0) {

			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("startDate", DateUtil.format(financialProductInformationDto.getValueDate(),DateUtil.DEFAULT_DATE_FORMAT_EN)); // 开始日
			paraMap.put("endDate", DateUtil.format(date,DateUtil.DEFAULT_DATE_FORMAT_EN)); // 结束日
			paraMap.put("finProdId", financialProductInformationDto.getFinProdId()); // 金融产品代码
			paraMap.put("branch", financialProductInformationDto.getBranch()); // 分支序号
			//估值信息查询
			List<BokPositionPrd> bokPositionPrdList = evalIndexAlgorithmService.getBokPositionPrdList(paraMap);
			//区间净值化列表
			List<DailyYieldDto> netValueList = new ArrayList<DailyYieldDto>();
			for (BokPositionPrd bpp : bokPositionPrdList) {

				DailyYieldDto dyd = new DailyYieldDto();
				if (dateIsOrNotLessAndEqual(bpp.getCdate(), date)) {
					dyd.setDate(bpp.getCdate());
					dyd.setYield(bpp.getTdyUnitValue());
					netValueList.add(dyd);
				}
			}
			BigDecimal yearBackRate = BigDecimal.ZERO;
			//非空判断
			if(netValueList.size() > 0){
				yearBackRate = evalIndexAlgorithmService.calcMaxRetreat(netValueList);
			}
			EvaluationRateDto evaluationRateDto=new EvaluationRateDto(startDate,date,yearBackRate);
			yearBackRateList.add(evaluationRateDto);

			date = DateUtil.addDay(date, 1);
		}
		return yearBackRateList;
	}

	/**
	 * 十四、区间收益目标 1开始日期 2结束日期 3金融产品代码 4分支序号 5金融产品类型 6收益类型
	 */
	public List<EvaluationRateDto> rangeReturnTarget(Date startDate, Date endDate,
													 FinancialProductInformationDto financialProductInformationDto) throws ParseException {

		this.init(startDate, endDate, financialProductInformationDto);

		List<EvaluationRateDto> targetRateList = new ArrayList<EvaluationRateDto>();

		// 子组合查询
		List<ChildCombination> ccList = evalIndexAlgorithmService
				.subcombinedQuery(financialProductInformationDto.getFinProdId());

		if(CollectionUtils.isNotEmpty(ccList)){

			for (ChildCombination cc : ccList) {

				//收益目标列表明细list
				List<RevenueTargetListDetails> revenueTargetListDetailsList = new ArrayList<>();

				Date date = startDate;
				while (DateUtil.compareDate(date, endDate) <= 0) {

					// 收益指标计算
					List<PtlTargetReturnDto> ptrdList = evalIndexAlgorithmService.calcTargetReturn(date,
							cc.getSubcombinedCode(), null);
					if(ptrdList.size() > 0){
						RevenueTargetListDetails rtld = new RevenueTargetListDetails();
						rtld.setDate(ptrdList.get(0).getEffDate());
						rtld.setEarningsTarget(ptrdList.get(0).getIncomeTarget());
						revenueTargetListDetailsList.add(rtld);
					}

					date = DateUtil.addDay(date, 1);
				}

				// 区间收益目标
                BigDecimal targetRate = evalIndexAlgorithmService.calcRangeReturnTarget(startDate, endDate,
						revenueTargetListDetailsList);

				EvaluationRateDto dto = new EvaluationRateDto();
				dto.setCombinedCode(cc.getCombinedCode());
				dto.setSubcombinedCode(cc.getSubcombinedCode());
				dto.setRate(targetRate);

				targetRateList.add(dto);
			}
		}

		return targetRateList;
	}

	/**
	 * 十五、子组合收益贡献 1开始日期 2结束日期 3金融产品代码 4分支序号 5金融产品类型 6收益类型
	 */
	public List<SubportfolioFundProportionDetails> subportfolioIncomeContribution(Date startDate, Date endDate,
																				  FinancialProductInformationDto financialProductInformationDto) throws ParseException {

		this.init(startDate, endDate, financialProductInformationDto);

		//子组合资金占比明细列表
		List<SubportfolioFundProportionDetails> sfpdList = new ArrayList<SubportfolioFundProportionDetails>();

		//子组合列表
		List<ChildCombination> childCombinationList = evalIndexAlgorithmService
				.subcombinedQuery(financialProductInformationDto.getFinProdId());

		if(CollectionUtils.isNotEmpty(childCombinationList)){

			List<String> subCodes = childCombinationList.stream()
					.map(ChildCombination::getSubcombinedCode).collect(Collectors.toList());

			Map<String, Object> paraMap = new HashMap<String, Object>();
			paraMap.put("yes_no",DictCodeList.是否_否.getValue());
			paraMap.put("tradeDirectionSubscribe", DictCodeList.交易方向_产品申购.getValue());
			paraMap.put("tradeDirectionRedemption", DictCodeList.交易方向_产品赎回.getValue());
			paraMap.put("transactionStatusInitial", DictCodeListExt1.交易状态_撤销.getValue());
			paraMap.put("productTradingCapitalAllocation", DictCodeListExt1.平安业务类型_产品交易资金分配.getValue());
			paraMap.put("productCapitalAdjustment", DictCodeListExt1.平安业务类型_子组合资金调配.getValue());
			paraMap.put("endDate", DateUtil.format(endDate,DateUtil.DEFAULT_DATE_FORMAT_EN));
			paraMap.put("subCodes",subCodes);
			//子组合收益资金占比计算
			List<SubportfolioIncomeCapitalRatio> sficrList = evalIndexAlgorithmService.queryShareAmts(paraMap);

			if(CollectionUtils.isNotEmpty(sficrList)){
				//总金额
				BigDecimal tatolMoney = sficrList.stream().map(SubportfolioIncomeCapitalRatio::getTransactionAmount).reduce(BigDecimal::add).get();

				sficrList.forEach(e->{
					SubportfolioFundProportionDetails sfpd = new SubportfolioFundProportionDetails();
					sfpd.setSubportfolioCapitalProportion(ArithmeticUtil.divide(e.getTransactionAmount(), tatolMoney, 16));
					sfpd.setSubcombinedCode(e.getSubcombinedCode());
					sfpdList.add(sfpd);
				});
			}

			for (SubportfolioFundProportionDetails sfpd : sfpdList) {

				//红利再投信息
				List<BookLatestInfoDto> dividendReinvestmentInformation = new ArrayList<BookLatestInfoDto>();

				//区间开始日累计单位净值
				BigDecimal sectionStartingDayTotalNav = BigDecimal.ZERO;

				//区间开始日单位净值
				BigDecimal sectionStartingDayNav = BigDecimal.ZERO;

				//区间结束日累计单位净值
				BigDecimal sectionEndDayTotalNav = BigDecimal.ZERO;

				//区间结束日单位净值
				BigDecimal sectionEndDayNav = BigDecimal.ZERO;

				Map<String, Object> parMap = new HashMap<String, Object>();
				parMap.put("startDate", DateUtil.format(startDate,DateUtil.DEFAULT_DATE_FORMAT_EN));
				parMap.put("endDate", DateUtil.format(endDate,DateUtil.DEFAULT_DATE_FORMAT_EN));
				parMap.put("finProdId", sfpd.getSubcombinedCode()); // 金融产品代码
				parMap.put("branch", 0); // 分支序号

				// 估值信息list
				bokPositionPrdList = evalIndexAlgorithmService.getBokPositionPrdList(parMap);

				for (BokPositionPrd bpp : bokPositionPrdList) {

					if (dateIsOrNotEquality(bpp.getCdate(), startDate)) {
						sectionStartingDayTotalNav = bpp.getTdyUnitValueSum();
						sectionStartingDayNav = bpp.getTdyUnitValue();
					}

					if (dateIsOrNotEquality(bpp.getCdate(), endDate)) {
						sectionEndDayTotalNav = bpp.getTdyUnitValueSum();
						sectionEndDayNav = bpp.getTdyUnitValue();
					}
				}

				BigDecimal rate = BigDecimal.ZERO;

				if (DictCodeList.收益类型_净值型.getValue().equals(financialProductInformationDto.getRevenueType())) {

					EvalIndexAlgorithmCalcParam param = new EvalIndexAlgorithmCalcParam();
					param.setStartDate(startDate);
					param.setEndDate(endDate);
					param.setsAccuNetValue(sectionStartingDayTotalNav);
					param.seteAccuNetValue(sectionEndDayTotalNav);
					param.setsNetValue(sectionStartingDayNav);
					// 区间年化收益率
					rate = evalIndexAlgorithmService.calcYearYield(param);
				}

				if (DictCodeList.收益类型_货币型.getValue().equals(financialProductInformationDto.getRevenueType())) {

					List<String> baseSubjectNatureList = new ArrayList<String>();
					baseSubjectNatureList.add(DictCodeBaseSubjectNatrue.应付利润.getValue());

					Map<String, Object> paMap = new HashMap<String, Object>();
					paMap.put("finProdId", financialProductInformationDto.getFinProdId());
					paMap.put("expectedReturnType", DictCodeList.收益类型_收益型.getValue());
					BooksetAndBokDetailDto booksetAndBokDetailDto = evalIndexAlgorithmService.getCysYearRateParam(paMap)
							.get(0);

					// 周期内发生的红利再投查询
					dividendReinvestmentInformation = evalIndexAlgorithmService.getBookLatestInfo(
							booksetAndBokDetailDto.getBooksetId(), endDate, booksetAndBokDetailDto.getBokDetailId(),
							baseSubjectNatureList, "8034", null, null, null, startDate, endDate);

					// 周期年化收益率
					rate = evalIndexAlgorithmService.getCysYearRate(bokPositionPrdList, startDate, endDate,
							dividendReinvestmentInformation, 365, ValDictCodeList.费前费后区分_费后.getValue());
				}
				//子组合区间年化收益率
				sfpd.setSubportfolioIntervalAnnualized(rate); // 更新子组合资金占比列表
			}

			// 子组合资金占比设置值 子组合收益贡献
			for (SubportfolioFundProportionDetails sfpd : sfpdList) {
				//子组合收益贡献
				sfpd.setSubportfolioIncomeContribution(ArithmeticUtil.multiply(sfpd.getSubportfolioCapitalProportion(),
						sfpd.getSubportfolioIntervalAnnualized()));
			}
		}

		return sfpdList;
	}

	/**
	 * 十六、子组合目标收益贡献 1开始日期 2结束日期 3金融产品代码 4分支序号 5金融产品类型 6收益类型
	 */
	public List<SubportfolioRangeIncomeContributionRateDetails> subportfolioGoalIncomeContribution(Date startDate,
																								   Date endDate, FinancialProductInformationDto financialProductInformationDto) throws ParseException {

		this.init(startDate, endDate, financialProductInformationDto);

		//区间收益贡献率列表
		List<SubportfolioRangeIncomeContributionRateDetails> subportfolioRangeIncomeContributionRateDetailsList = new ArrayList<SubportfolioRangeIncomeContributionRateDetails>();

		//子组合列表
		List<ChildCombination> childCombinationList = new ArrayList<ChildCombination>();

		//总继承比例
		BigDecimal totalInheritanceRatio = BigDecimal.ZERO;

		// 1、子组合查询
		childCombinationList = evalIndexAlgorithmService
				.subcombinedQuery(financialProductInformationDto.getFinProdId());

		if(CollectionUtils.isNotEmpty(childCombinationList) && childCombinationList.size() > 0){
			// 2、继承比例占比计算
			for (ChildCombination cc : childCombinationList) {

				totalInheritanceRatio = ArithmeticUtil.doAdd(totalInheritanceRatio, cc.getDisPercent());
			}
			for (ChildCombination cc : childCombinationList) {
				//变量.子组合继承比例 = 子组合列表.继承比例/变量.总继承比例
				BigDecimal subcombinationInheritanceRatio = ArithmeticUtil.divide(cc.getDisPercent(), totalInheritanceRatio, 16);
				cc.setDisPercent(subcombinationInheritanceRatio);
			}

			//调用【区间收益目标】逻辑 子组合的区间收益目标
			List<EvaluationRateDto> evaluationRates = rangeReturnTarget(startDate, endDate, financialProductInformationDto);

			Map<String, BigDecimal> evaluationRateMap = evaluationRates.stream()
					.collect(Collectors.toMap(EvaluationRateDto::getSubcombinedCode, EvaluationRateDto::getRate, (k1, k2) -> k1));


			// 子组合列表
			for (ChildCombination cc : childCombinationList) {
				//子组合目标收益贡献
				BigDecimal subportfolioTargetReturnContribution = BigDecimal.ZERO;
				if (evaluationRateMap.containsKey(cc.getSubcombinedCode())) {
					BigDecimal decimal = evaluationRateMap.get(cc.getSubcombinedCode());

					// 子组合目标收益贡献
					subportfolioTargetReturnContribution = ArithmeticUtil.multiply(cc.getDisPercent(),
							decimal);
				}
				SubportfolioRangeIncomeContributionRateDetails sricrd = new SubportfolioRangeIncomeContributionRateDetails();
				sricrd.setSubportfolioTargetReturnContribution(subportfolioTargetReturnContribution);
				sricrd.setCombinedCode(cc.getCombinedCode());
				sricrd.setSubcombinedCode(cc.getSubcombinedCode());

				subportfolioRangeIncomeContributionRateDetailsList.add(sricrd);

			}
		}
		return subportfolioRangeIncomeContributionRateDetailsList;
	}

	/**
	 * 十七、时点策略执行有效性 1开始日期 2结束日期 3金融产品代码 4分支序号 5金融产品类型 6收益类型
	 */
	public void effectivenessPilotStrategyImplementation(Date startDate, Date endDate,
														 FinancialProductInformationDto financialProductInformationDto) throws ParseException {

		this.init(startDate, endDate, financialProductInformationDto);

		/*
		 * 子组合列表
		 */
		List<ChildCombination> childCombinationList = new ArrayList<ChildCombination>();

		childCombinationList = new ArrayList<ChildCombination>(); // 从画面选择组合列表

		for (ChildCombination cc : childCombinationList) {

			// List<TimePointStrategyDto> tpsdList =
			// evalIndexAlgorithmService.calcTimePointStrategy(startDate, endDate,
			// portfolioId);
		}
	}

	/**
	 * 十八、平均策略执行有效性 1开始日期 2结束日期 3金融产品代码 4分支序号 5金融产品类型 6收益类型
	 */
	public void averagePolicyExecutionEffectiveness(Date startDate, Date endDate,
													FinancialProductInformationDto financialProductInformationDto) throws ParseException {

		this.init(startDate, endDate, financialProductInformationDto);

		/*
		 * 子组合列表
		 */
		List<ChildCombination> childCombinationList = new ArrayList<ChildCombination>();

		// 子组合列表 = 画面选择组合列表
		childCombinationList = new ArrayList<ChildCombination>(); // 从画面选择组合列表

		// 平均策略执行有效性列表
		List<AverageStrategyDto> asdList = evalIndexAlgorithmService.calcAverageStrategy(startDate, endDate,
				childCombinationList.get(0).getSubcombinedCode());
	}

	/**
	 * 判断日期是否相等
	 */
	public static boolean dateIsOrNotEquality(Date dateOne, Date dateTwo) {

		// 小于返回-1 大于返回1 等于返回0
		int compareTo = dateOne.compareTo(dateTwo);
		if (compareTo == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断日期小于等于
	 */
	public static boolean dateIsOrNotLessAndEqual(Date dateOne,Date dateTwo) {

		// 小于返回-1 大于返回1 等于返回0
		boolean flag = false;
		int compareTo = dateOne.compareTo(dateTwo);
		if (compareTo <= 0) {
			flag = true;
		} 
//		else if (compareTo == 0) {
//			flag = true;
//		}

		return flag;
	}

	/**
	 * 判断日期大于等于
	 */
	public static boolean dateIsOrNotBigAndEqual(Date dateOne, Date dateTwo) {

		// 小于返回-1 大于返回1 等于返回0
		boolean flag = false;
		int compareTo = dateOne.compareTo(dateTwo);
		if (compareTo == 1) {
			flag = true;
		} else if (compareTo == 0) {
			flag = true;
		}

		return flag;
	}

}
