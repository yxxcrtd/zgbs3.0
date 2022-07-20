/*
 * 文件名：BussFinProductOpenPlanImplService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：张亮
 * 修改时间：2018年9月3日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.susprdfutureopenplangen.relyingonresources.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.FinOpenPlan;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.batch.susprdfutureopenplangen.relyingonresources.InvDateCalService;
import com.joyintech.tams.invest.batch.susprdfutureopenplangen.relyingonresources.dto.BussFinProductOpenPlanDto;
import com.joyintech.tams.invest.batch.susprdfutureopenplangen.relyingonresources.service.BussFinProductOpenPlanService;
import com.joyintech.tams.invest.common.calculator.AdjustedTradeDayCal;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;

/**
 * 产品开放计划实现类
 *
 * @author 张亮
 * @version 1.0 2018年9月3日
 * @see InvestBussFinProductOpenPlanImplService
 * @since 1.0
 */
@Service
public class InvestBussFinProductOpenPlanImplService extends BaseService<BussFinProductOpenPlanDto>
		implements BussFinProductOpenPlanService {

	/**
	 * {@linkplain}
	 */
	@Autowired
	private InvDateCalService invDateCalService;

	@Override
	public List<FinOpenPlan> genOpenPlanList(BussFinProductOpenPlanDto inputDto) {
		List<FinOpenPlan> resultList = new ArrayList<>();

		if (inputDto == null) {
			return resultList;
		}

		// 区间开始日
		Date startDate = inputDto.getVdate();
		// 区间结束日
		Date endDate = inputDto.getMdate();
		// 产品首次付息日最多允许推迟年数
		int year = StringUtil.toInt(CacheUtil.getSysParam(BussinessConstant.MAX_AFTER_NOW_YEAR));
		// 允许可生成最大区间结束日
		Date endDateMax = DateUtil.addYear(inputDto.getVdate(), year);
			// 如果到期日大于允许可生成最大区间结束日 取允许可生成最大区间结束日
			if (DateUtil.compareDate(endDate, endDateMax) > 0) {
				inputDto.setMdate(endDateMax);
			}else {
				inputDto.setMdate(endDate);
			}
		// 开放计划区间生成
		if (StringUtil.equals(DictCodeList.开放模式_定期开放.getValue(), inputDto.getOpenMode())
				|| StringUtil.equals(DictCodeList.开放模式_周期开放.getValue(), inputDto.getOpenMode())) {
			// 交易日历取得
			List<String> dealCalendarHolidayList = invDateCalService.getDealCalendarHoliday(startDate,
					DateUtil.addDay(endDate, 8), inputDto.getTradCalender());

			// 开放计划生成共通调用
			resultList = this.calcProductOpenPlan(inputDto, dealCalendarHolidayList);
		}

		return resultList;
	}

	/**
	 * 生成开放计划
	 *
	 * @param inputDto
	 *            入参dto
	 * @param dealCalendarHolidayList
	 *            节假日列表
	 * @return 开放计划
	 * @see
	 * @since 1.0
	 */
	private List<FinOpenPlan> calcProductOpenPlan(BussFinProductOpenPlanDto inputDto,
			List<String> dealCalendarHolidayList) {
		// 开放计划日期调整前计算节点（根据推算规则对应）
		List<Date> adjustBeforeNodeDateList = new ArrayList<Date>();
		// 开放计划日期调整前计算节点（根据推算规则对应）
		List<Date> adjustAfterNodeDateList = new ArrayList<Date>();

		// List<Date> adjustBeforeEndDatePeriodList = new ArrayList<Date>(); //
		// 临时变量.调整前结束日区间列表
		// List<Date> adjustBeforeEndDateList = new ArrayList<Date>(); // 临时变量.调整前结束日列表
		List<Date> openCycleStartDateList = new ArrayList<Date>(); // 临时变量.开放周期开始日列表
		List<Date> openCycleEndDateList = new ArrayList<Date>(); // 临时变量.调整后结束日区间列表
		List<Date> investStartDateList = new ArrayList<Date>(); // 临时变量.投资周期开始日列表
		List<Date> investEndDateList = new ArrayList<Date>(); // 临时变量.投资周期到期日列表

		Date startDate = inputDto.getVdate(); // 临时变量.区间开始日

		// 一体化对应etliu
		// if (StringUtil.equals(DictCodeList.推算方法_首次开放日.getValue(),
		if (StringUtil.equals(DictCodeList.推算方式_首次支付日.getValue(), inputDto.getCalcMethod())) {
			startDate = inputDto.getFirstOpenDate();
		}
		Date endDate = inputDto.getMdate(); // 临时变量.区间结束日
		if (StringUtil.equals(DictCodeListExt1.周期单位_日.getValue(), inputDto.getOpenCycleUnit())) {
			// 3.1 输入.开放周期单位 = "日" 时

			// 日期区间分割共通调用
			List<Date> tempDivisionList = invDateCalService.dateIntervalNodeList(startDate, endDate,
					DictCodeList.营业日准则_向后调整.getValue(), String.valueOf(inputDto.getOpenCycle()),
					DictCodeListExt1.周期单位_日.getValue());

			// 临时变量.调整前结束日区间列表 = 上述共通返回.分割后区间列表去掉第一个节点和最后一个节点
			if (!StringUtil.isEmptyList(tempDivisionList)) {
				tempDivisionList.remove(tempDivisionList.size() - 1);
				if (!StringUtil.equals(DictCodeList.推算方式_首次支付日.getValue(), inputDto.getCalcMethod())) {
					tempDivisionList.remove(0);
				}
			}
			adjustBeforeNodeDateList = tempDivisionList;
		} else if (StringUtil.equals(DictCodeListExt1.周期单位_周.getValue(), inputDto.getOpenCycleUnit())) {
			if (StringUtil.equals(DictCodeList.推算方式_首次支付日.getValue(), inputDto.getCalcMethod())) {
				adjustBeforeNodeDateList.add(startDate);
			}
			// 3.2 输入.开放周期单位 = "周" 时
			// 3.2.1 临时变量.区间开始日对应的星期日期取得
			// 临时变量.区间星期开始日 = 临时变量.区间开始日所在的下一周中输入.固定周所对应的日期
			if (StringUtil.isNotEmpty(inputDto.getDayOfWeek())) {
				List<Date> weekStartDateList = new ArrayList<Date>(); // 临时变量.区间星期开始日
				Date weekEndDate = inputDto.getVdate(); // 临时变量.区间星期结束日
				List<String> dayOfWeeks = StringUtil.splitToList(",", inputDto.getDayOfWeek());
				// 将输入的周几列表按顺序排序
				Collections.sort(dayOfWeeks);
				// 遍历列表节点
				for (String dayOfWeek : dayOfWeeks) {
					weekStartDateList.add(this.getWeekDayByDate(startDate, inputDto.getOpenCycle().intValue(),
							Integer.parseInt(dayOfWeek)));

					// 3.2.2 临时变量.区间结束日对应的星期日期取得
					// 临时变量.区间星期结束日 = 临时变量.区间结束日之前最大的输入.固定开放周所对应的日期
					Date tempDate = this.getFirstWeekDayByDate(endDate, dayOfWeek);
					if (DateUtil.compareDate(tempDate, weekEndDate) > 0) {
						// update by leipeng 20190814 BSTWO-1410
						weekEndDate = DateUtil.addDay(tempDate, 1);
					}
				}

				// 将输入的周几列表按顺序排序
				Collections.sort(weekStartDateList);

				// 3.2.3 日期区间分割共通调用
				// 分割周期(输入.开放周期*7)
				int tempOpenCycle = inputDto.getOpenCycle().intValue() * 7;
				// 分割周期单位(日)
				String tempOpenCycleUnit = DictCodeListExt1.周期单位_日.getValue();

				List<Date> tempDivisionList = new ArrayList<>();

				tempDivisionList = invDateCalService.dateIntervalNodeList(weekStartDateList, weekEndDate,
						DictCodeList.营业日准则_向后调整.getValue(), String.valueOf(tempOpenCycle), tempOpenCycleUnit);

				// 当开始日和终了日相同的情况，dateCalService.dateIntervalNodeList方法返回的是空
				if (DateUtil.isSameDate(weekStartDateList.get(0), weekEndDate)) {
					tempDivisionList.add(weekStartDateList.get(0));
				}

				// update by leipeng 20190814
				// 定期开放的产品起息日到到期日只有3天，开放计划推算的是周，导致计划生成不出来，系统直接报异常，应该是给提示 start
				if (tempDivisionList.size() > 0) {
					int length = tempDivisionList.size() - 1;
					if (DateUtil.getBetweenDayNumber(tempDivisionList.get(length), endDate) < tempOpenCycle) {
						tempDivisionList.remove(length);
					}
				}
				// update by leipeng 20190814
				// 定期开放的产品起息日到到期日只有3天，开放计划推算的是周，导致计划生成不出来，系统直接报异常，应该是给提示 end
				// 临时变量.调整前结束日区间列表 = 上述共通返回.分割后结束日列表
				adjustBeforeNodeDateList.addAll(tempDivisionList);
			} else {
				// 3.2.3 日期区间分割共通调用
				// 分割周期(输入.开放周期*7)
				int tempOpenCycle = inputDto.getOpenCycle().intValue() * 7;
				// 分割周期单位(日)
				String tempOpenCycleUnit = DictCodeListExt1.周期单位_日.getValue();

				List<Date> tempDivisionList = new ArrayList<>();

				tempDivisionList = invDateCalService.dateIntervalNodeList(startDate, endDate,
						DictCodeList.营业日准则_向后调整.getValue(), String.valueOf(tempOpenCycle), tempOpenCycleUnit);
				if (!StringUtil.isEmptyList(tempDivisionList)) {
					tempDivisionList.remove(0);
				}
				// 临时变量.调整前结束日区间列表 = 上述共通返回.分割后结束日列表
				adjustBeforeNodeDateList.addAll(tempDivisionList);
			}
		} else if (StringUtil.equals(DictCodeListExt1.周期单位_月.getValue(), inputDto.getOpenCycleUnit())) {
			if (StringUtil.equals(DictCodeList.推算方式_首次支付日.getValue(), inputDto.getCalcMethod())) {
				adjustBeforeNodeDateList.add(startDate);
			}
			// 3.3 输入.开放周期单位 = "月" 时
			if (StringUtil.isNotEmpty(inputDto.getDayOfMonth())) {

				List<String> dayOfMonthList = StringUtil.splitToList(",", inputDto.getDayOfMonth());

				// 将输入的几号列表按顺序排序
				Collections.sort(dayOfMonthList);

				Date nodeEndDate = startDate; // 临时变量.区间节点结束日 = 临时变量.区间开始日
				// 3.3.1 固定开放日 != null 时
				// 区间节点结束日 < 区间结束日时
				while (DateUtil.compareDate(nodeEndDate, endDate) < 0) {
					// // 日期计算基准日期: 临时变量.区间节点结束日.年月 + 01
					// String basicDateStr = DateUtil.format(DateUtil.getFirstDay(nodeEndDate));
					// 日期计算基准日期: 临时变量.区间节点结束日
					// 自然日日期计算共通调用
					Date calcDate = invDateCalService.naturalDateCalc(nodeEndDate, DictCodeListExt1.周期单位_月.getValue(),
							inputDto.getOpenCycle().intValue());

					// 临时变量.区间节点结束日 = 上述共通返回.计算后日期.年月 + 输入.固定开放日 ※超过当月最大天数时，按当月月末处理
					for (String dayOfMonth : dayOfMonthList) {
						nodeEndDate = DateUtil.getDateOfMonthYearByDay(calcDate, StringUtil.toInt(dayOfMonth));
						// 临时变量.调整前结束日区间列表.add(临时变量.区间节点结束日)
						adjustBeforeNodeDateList.add(nodeEndDate);
					}
				}
			} else if (StringUtil.isNotEmpty(inputDto.getWorkdayOfMonth())) {
				List<String> workdayOfMonthList = StringUtil.splitToList(",", inputDto.getWorkdayOfMonth());

				// 将输入的第几工作日列表按顺序排序
				Collections.sort(workdayOfMonthList);

				// 3.3.2 固定开放工作日 != null 时
				// 临时变量.区间节点结束日 = 临时变量.区间开始日
				Date nodeEndDate = startDate;
				// 当临时变量.区间节点结束日 < 临时变量.区间结束日时，循环以下操作
				while (DateUtil.compareDate(nodeEndDate, endDate) < 0) {
					// 3.3.2.1 自然日日期计算共通调用
					// // 日期计算基准日期: 临时变量.区间节点结束日.年月 + 01
					// String basicDateStr = DateUtil.format(DateUtil.getFirstDay(nodeEndDate));
					// 日期计算基准日期: 临时变量.区间节点结束日
					// 自然日日期计算共通调用
					Date calcDate = DateUtil.getDateOfMonthYearByDay(invDateCalService.naturalDateCalc(nodeEndDate,
							DictCodeListExt1.周期单位_月.getValue(), inputDto.getOpenCycle().intValue()), 1);
					// 3.3.2.2 交易日推算共通调用
					// 调整天数(输入.固定开放工作日)
					for (String workdayOfMonth : workdayOfMonthList) {
						int adjustDayCount = StringUtil.toInt(workdayOfMonth);
						// 上述共通返回.计算后日期在输入参数.交易日历列表中不存在时 输入.固定开放工作日 - 1
						if (dealCalendarHolidayList.indexOf(DateUtil.format(calcDate)) < 0) {
							adjustDayCount = adjustDayCount - 1;
						}

						// 调整后日期
						// 临时变量.区间节点结束日 = 上述3.3.2.2共通返回.调整后日期
						nodeEndDate = invDateCalService.calculationTradingDay(calcDate, adjustDayCount,
								dealCalendarHolidayList, DictCodeList.是否_否.getValue());
						// 临时变量.调整前结束日区间列表.add(临时变量.区间节点结束日)
						adjustBeforeNodeDateList.add(nodeEndDate);
					}
				}
			}
			// 第几周周几
			else if (StringUtil.isNotEmpty(inputDto.getWeekOfMonth())
					&& StringUtil.isNotEmpty(inputDto.getWorkdayOfWeek())) {
				// 第几周
				List<String> weekOfMonthList = StringUtil.splitToList(",", inputDto.getWeekOfMonth());
				// 周几
				List<String> workdayOfWeekList = StringUtil.splitToList(",", inputDto.getWorkdayOfWeek());
				// 将输入的第几工作日列表按顺序排序
				Collections.sort(weekOfMonthList);
				// 对周几排序
				Collections.sort(workdayOfWeekList);
				// 3.3.2 固定开放工作日 != null 时
				// 临时变量.区间节点结束日 = 临时变量.区间开始日
				Date nodeEndDate = startDate;
				// 当临时变量.区间节点结束日 < 临时变量.区间结束日时，循环以下操作
				while (DateUtil.compareDate(nodeEndDate, endDate) < 0) {
					// 日期计算基准日期: 临时变量.区间节点结束日
					// 自然日日期计算共通调用
					Date calcDate = DateUtil.getDateOfMonthYearByDay(invDateCalService.naturalDateCalc(nodeEndDate,
							DictCodeListExt1.周期单位_月.getValue(), inputDto.getOpenCycle().intValue()), 1);
					// 循环 第几周 计算当月每周的星期日历
					for (String weekOfMonth : weekOfMonthList) {
						int adjustWeekCount = StringUtil.toInt(weekOfMonth);
						// 周几
						for (String workdayOfWeek : workdayOfWeekList) {
							int adjustdayOfWeekCount = StringUtil.toInt(workdayOfWeek);
							Date dayOfWeek = this.weekdatetodata(calcDate, adjustWeekCount, adjustdayOfWeekCount);
							if (dayOfWeek == null) {
								continue;
							}
							// 推算第几周周几日期
							nodeEndDate = DateUtil.parse(AdjustedTradeDayCal.getAdjustedTradeDay(
									DateUtil.format(dayOfWeek), inputDto.getTradedayRule(), dealCalendarHolidayList));
							// 临时变量.调整前结束日区间列表.add(临时变量.区间节点结束日)
							adjustBeforeNodeDateList.add(nodeEndDate);
						}
					}
				}
			} else {
				Date nodeEndDate = startDate; // 临时变量.区间节点结束日 = 临时变量.区间开始日
				// 3.3.1 固定开放日 != null 时
				// 区间节点结束日 < 区间结束日时
				adjustBeforeNodeDateList.add(nodeEndDate);

				int count = 1;
				while (DateUtil.compareDate(nodeEndDate, endDate) < 0) {
					// // 日期计算基准日期: 临时变量.区间节点结束日.年月 + 01
					// String basicDateStr = DateUtil.format(DateUtil.getFirstDay(nodeEndDate));

					nodeEndDate = DateUtil.addMonthDate(startDate, inputDto.getOpenCycle().intValue() * count);

					adjustBeforeNodeDateList.add(nodeEndDate);

					count++;
				}
				if (!StringUtil.isEmptyList(adjustBeforeNodeDateList)) {
					adjustBeforeNodeDateList.remove(0);
				}
			}
		} else if (StringUtil.equals(DictCodeListExt1.周期单位_年.getValue(), inputDto.getOpenCycleUnit())) {

			if (StringUtil.equals(DictCodeList.推算方式_首次支付日.getValue(), inputDto.getCalcMethod())) {
				adjustBeforeNodeDateList.add(startDate);
			}
			// 3.5 输入.开放周期单位 = "年" 时

			if (StringUtil.isNotEmpty(inputDto.getDayOfMonth())) {
				// 临时变量.区间节点结束日
				Date nodeEndDate = startDate;
				List<String> dayOfMonthList = StringUtil.splitToList(",", inputDto.getDayOfMonth());

				// 将输入的几号列表按顺序排序
				Collections.sort(dayOfMonthList);
				// 3.5.1 固定开放日 != null 时

				// 当临时变量.区间节点结束日 < 临时变量.区间结束日时，循环以下操作
				while (DateUtil.compareDate(nodeEndDate, endDate) < 0) {
					// 自然日日期计算共通调用
					Date calcDate = invDateCalService.naturalDateCalc(nodeEndDate, DictCodeListExt1.周期单位_年.getValue(),
							inputDto.getOpenCycle().intValue());
					for (String dayOfMonth : dayOfMonthList) {
						// 临时变量.区间节点结束日 = 上述共通返回.计算后日期.年月 + 输入.固定开放日 ※超过当月最大天数时，按当月月末处理
						nodeEndDate = DateUtil.getDateOfMonthYearByDay(calcDate, StringUtil.toInt(dayOfMonth));
						// 临时变量.调整前结束日区间列表.add(临时变量.区间节点结束日)
						adjustBeforeNodeDateList.add(nodeEndDate);
					}
				}
			} else if (StringUtil.isNotEmpty(inputDto.getWorkdayOfMonth())) {
				// 临时变量.区间节点结束日 = 临时变量.区间开始日所对应的年度的首日
				// Date nodeEndDate = this.getFirstDayOfYear(startDate);
				Date nodeEndDate = startDate;
				List<String> workdayOfMonthList = StringUtil.splitToList(",", inputDto.getWorkdayOfMonth());

				// 将输入的第几工作日列表按顺序排序
				Collections.sort(workdayOfMonthList);

				// 3.5.2 固定开放工作日 != null 时
				// 当临时变量.区间节点结束日 < 临时变量.区间结束日时，循环以下操作
				while (DateUtil.compareDate(nodeEndDate, endDate) < 0) {
					// 3.5.2.1 自然日日期计算共通调用
					Date calcDate = DateUtil.getDateOfMonthYearByDay(invDateCalService.naturalDateCalc(nodeEndDate,
							DictCodeListExt1.周期单位_年.getValue(), inputDto.getOpenCycle().intValue()), 1);

					// 3.5.2.2 交易日推算共通调用
					// 调整天数(输入.固定开放工作日)
					for (String workdayOfMonth : workdayOfMonthList) {
						int adjustDayCount = StringUtil.toInt(workdayOfMonth);
						// 上述共通返回.计算后日期在输入参数.交易日历列表中不存在时 输入.固定开放工作日 - 1
						if (dealCalendarHolidayList.indexOf(DateUtil.format(calcDate)) < 0) {
							adjustDayCount = adjustDayCount - 1;
						}
						// 调整后日期
						nodeEndDate = invDateCalService.calculationTradingDay(calcDate, adjustDayCount,
								dealCalendarHolidayList, DictCodeList.是否_否.getValue());
						// nodeEndDate = DateUtil.parse(AdjustedTradeDayCal.getAdjustedTradeDay(
						// DateUtil.format(DateUtil.addDay(calcDate,
						// adjustDayCount)),inputDto.getTradedayRule(), dealCalendarHolidayList));
						// 临时变量.区间节点结束日 = 上述3.3.2.2共通返回.调整后日期
						// 临时变量.调整前结束日区间列表.add(临时变量.区间节点结束日)
						adjustBeforeNodeDateList.add(nodeEndDate);
					}
				}
			} else {
				Date nodeEndDate = startDate; // 临时变量.区间节点结束日 = 临时变量.区间开始日
				// 3.3.1 固定开放日 != null 时
				// 区间节点结束日 < 区间结束日时
				adjustBeforeNodeDateList.add(nodeEndDate);

				int count = 1;
				while (DateUtil.compareDate(nodeEndDate, endDate) < 0) {

					nodeEndDate = DateUtil.addYear(startDate, inputDto.getOpenCycle().intValue() * count);

					adjustBeforeNodeDateList.add(nodeEndDate);

					count++;
				}
				if (!StringUtil.isEmptyList(adjustBeforeNodeDateList)) {
					adjustBeforeNodeDateList.remove(0);
				}
			}
		}

		// // 4.输入.推算方法 = "首次开放日" 的场合
		// if (StringUtil.equals(DictCodeList.推算方法_首次开放日.getValue(),
		// inputDto.getCalcMethod())) {
		// // 临时变量.调整前结束日区间列表.add(首次开放日)
		// adjustBeforeNodeDateList.add(0, inputDto.getFirstOpenDate());
		// }

		// 4.开放日的营业日准则调整
		if (!StringUtil.equals(DictCodeList.营业日准则_不调整.getValue(), inputDto.getTradedayRule())) {
			// 输入.营业日准则 != "不调整" 的场合
			// 输入.推算方法 = "起息日向后" 的场合 遍历临时变量.调整前结束日区间列表的第一个节点至最后一个节点
			int beginIndex = 0;
			// 一体化对应etliu
			// if (StringUtil.equals(DictCodeList.推算方法_首次开放日.getValue(),
			// inputDto.getCalcMethod())) {
			if (StringUtil.equals(DictCodeList.推算方式_首次支付日.getValue(), inputDto.getCalcMethod())) {
				// 输入.推算方法 = "首次开放日" 的场合 遍历临时变量.调整前结束日区间列表的第二个节点至最后一个节点
				beginIndex = 1;

				// 临时变量.调整后结束日区间列表.add(首次开放日)
				adjustAfterNodeDateList.add(0, inputDto.getFirstOpenDate());
				// adjustBeforeEndDateList.add(首次开放日)
				// adjustBeforeEndDateList.add(0, inputDto.getFirstOpenDate());
			}

			// 根据 临时变量.调整前结束日区间列表做循环处理
			for (int i = beginIndex; i < adjustBeforeNodeDateList.size(); i++) {
				// 调整后交易日(调整后交易日取得)
				String tempAdjustAfterTradeDate = AdjustedTradeDayCal.getAdjustedTradeDay(
						DateUtil.format(adjustBeforeNodeDateList.get(i)), inputDto.getTradedayRule(),
						dealCalendarHolidayList);

				if (i != adjustBeforeNodeDateList.size() - 1) {
					// 上述返回值.调整后日期 < 临时变量.调整前结束日区间列表.下一节点日期
					if (DateUtil.compareDate(DateUtil.parse(tempAdjustAfterTradeDate),
							adjustBeforeNodeDateList.get(i + 1)) < 0) {
						// 临时变量.调整前结束日列表.add(临时变量.调整前结束日区间列表.节点日期)
						// adjustBeforeEndDateList.add(adjustBeforeEndDatePeriodList.get(i));
						// 临时变量.调整后结束日区间列表.add(上述共通返回调整后日期)
						adjustAfterNodeDateList.add(DateUtil.parse(tempAdjustAfterTradeDate));
					}
				} else {
					// 临时变量.调整前结束日列表.add(临时变量.调整前结束日区间列表.节点日期)
					// adjustBeforeEndDateList.add(adjustBeforeEndDatePeriodList.get(i));
					// 临时变量.调整后结束日区间列表.add(上述共通返回调整后日期)
					adjustAfterNodeDateList.add(DateUtil.parse(tempAdjustAfterTradeDate));
				}
			}
		} else {
			// 以上条件以外的场合
			// 临时变量.调整前结束日列表=临时变量.调整前结束日区间列表
			// adjustBeforeEndDateList.addAll(adjustBeforeEndDatePeriodList);
			// 临时变量.调整后结束日区间列表=临时变量.调整前结束日区间列表
			adjustAfterNodeDateList.addAll(adjustBeforeNodeDateList);
		}

		// 一体化对应etliu
		// if (StringUtil.equals(inputDto.getCalcRule(),
		// DictCodeList.推算规则_开放周期起息日.getValue())) {
		if (StringUtil.equals(inputDto.getCalcRule(), DictCodeList.推算方法_开放确认日.getValue())) {
			// 截取掉大于到期日的节点
			for (Date investStartDate : adjustAfterNodeDateList) {
				if (endDate != null && DateUtil.compareDate(investStartDate, endDate) > 0) {
					// 当上述调整后日期 >= 临时变量.区间结束日时
					// 循环结束
					break;
				} else {					
					// 临时变量.投资周期开始日列表.add(上述调整后日期）
					investStartDateList.add(investStartDate);
				}
			}
			for (Date currentNodeDate : investStartDateList) {
				// 5.2 投资周期开始日生成
				// lijun modify 生成开放计划新需求 20190626 start
				// 交易日推算
				// Date tempOpenCycleEndDate =
				// DateUtil.parse(AdjustedTradeDayCal.getAdjustedTradeDay(
				// DateUtil.format(
				// DateUtil.addDay(currentNodeDate, -1 * inputDto.getPurConfDays())),
				// inputDto.getTradedayRule(), dealCalendarHolidayList));
				// 本周期开放开结束日 = 开放确认日 - 确认天数
				Date tempOpenCycleEndDate = DateUtil.addDay(currentNodeDate, -1 * inputDto.getPurConfDays());
				// 临时变量.投资周期开始日列表.add(上述调整后日期）
				openCycleEndDateList.add(tempOpenCycleEndDate);

				// 开放周期结束日生成
				// 本周期开放开始日+开放天数-1
				// Date tempOpenCycleStartDate = DateUtil.parse(
				// AdjustedTradeDayCal.getAdjustedTradeDay(
				// DateUtil.format(DateUtil.addDay(tempOpenCycleEndDate,
				// 1 - inputDto.getOpenDays().intValue())),
				// inputDto.getTradedayRule(), dealCalendarHolidayList));
				if (StringUtil.equals(DictCodeList.开放天数类型_固定天数.getValue(), inputDto.getOpenDaysType())) {
					// 开放周期结束日生成 = 本周期开放开始日+开放天数-1
					Date tempOpenCycleStartDate = DateUtil.addDay(tempOpenCycleEndDate,
							1 - inputDto.getOpenDays().intValue());
					openCycleStartDateList.add(tempOpenCycleStartDate);
				}
				// lijun modify 生成开放计划新需求 20190626 end
			}
		}
		// 一体化对应etliu
		// else if (StringUtil.equals(inputDto.getCalcRule(),
		// DictCodeList.推算规则_开放周期开始日.getValue())) {
		else if (StringUtil.equals(inputDto.getCalcRule(), DictCodeList.推算方法_开放开始日.getValue())) {
			openCycleStartDateList.addAll(adjustAfterNodeDateList);

			for (Date currentNodeDate : openCycleStartDateList) {
				// 开放周期结束日生成
				// 本周期开放开始日+开放天数-1
				// Date tempOpenCycleEndDate =
				// DateUtil.parse(AdjustedTradeDayCal.getAdjustedTradeDay(
				// DateUtil.format(
				// DateUtil.addDay(currentNodeDate, inputDto.getOpenDays().intValue() - 1)),
				// inputDto.getTradedayRule(), dealCalendarHolidayList));
				// 开放结束日 = 开放周期结束日 + 开放天数-1
				Date tempOpenCycleEndDate = DateUtil.addDay(currentNodeDate, inputDto.getOpenDays().intValue() - 1);
				openCycleEndDateList.add(tempOpenCycleEndDate);

				// 5.2 投资周期开始日生成
				// 交易日推算
				// Date tempAdjustAfterDate = dateCalService.calculationTradingDay(
				// tempOpenCycleEndDate, inputDto.getPurConfDays(), dealCalendarHolidayList,
				// DictCodeList.是否_是.getValue());
				Date tempAdjustAfterDate = DateUtil.parse(AdjustedTradeDayCal.getAdjustedTradeDay(
						DateUtil.format(DateUtil.addDay(tempOpenCycleEndDate, inputDto.getPurConfDays().intValue())),
						inputDto.getTradedayRule(), dealCalendarHolidayList));
				if (endDate != null && DateUtil.compareDate(tempAdjustAfterDate, endDate) > 0) {
					// 当上述调整后日期 >= 临时变量.区间结束日时
					// 循环结束
					break;
				} else {
					// 临时变量.投资周期开始日列表.add(上述调整后日期）
					investStartDateList.add(tempAdjustAfterDate);
				}
			}
		}
		// 一体化对应etliu
		// else if (StringUtil.equals(inputDto.getCalcRule(),
		// DictCodeList.推算规则_开放周期结束日.getValue())) {
		else if (StringUtil.equals(inputDto.getCalcRule(), DictCodeList.推算方法_开放结束日.getValue())) {
			openCycleEndDateList.addAll(adjustAfterNodeDateList);

			// 5.开放周期开始日列表、投资周期开始日列表生成
			// 遍历临时变量.调整后结束日区间列表
			for (Date currentNodeDate : openCycleEndDateList) {
				// 5.1 开放周期开始日生成
				// 临时变量.调整后结束日区间列表.节点日期 - 输入.开放天数 + 1
				// Date tempOpenCycleStartDate = DateUtil.parse(
				// AdjustedTradeDayCal.getAdjustedTradeDay(
				// DateUtil.format(DateUtil.addDay(currentNodeDate,
				// 1 - inputDto.getOpenDays().intValue())),
				// inputDto.getTradedayRule(), dealCalendarHolidayList));
				if (StringUtil.equals(DictCodeList.开放天数类型_固定天数.getValue(), inputDto.getOpenDaysType())) {
					Date tempOpenCycleStartDate = DateUtil.addDay(currentNodeDate,
							1 - inputDto.getOpenDays().intValue());
					// TODO 开放周期开始日如果比起息日小如何处理（=currentNodeDate？）
					openCycleStartDateList.add(tempOpenCycleStartDate);
				}

				// 5.2 投资周期开始日生成
				// 交易日推算
				// Date tempAdjustAfterDate =
				// dateCalService.calculationTradingDay(currentNodeDate,
				// inputDto.getPurConfDays(), dealCalendarHolidayList,
				// DictCodeList.是否_是.getValue());
				Date tempAdjustAfterDate = DateUtil.parse(AdjustedTradeDayCal.getAdjustedTradeDay(
						DateUtil.format(DateUtil.addDay(currentNodeDate, inputDto.getPurConfDays().intValue())),
						inputDto.getTradedayRule(), dealCalendarHolidayList));

				if (endDate != null && DateUtil.compareDate(tempAdjustAfterDate, endDate) > 0) {
					// 当上述调整后日期 >= 临时变量.区间结束日时
					// 循环结束
					break;
				} else {
					// 临时变量.投资周期开始日列表.add(上述调整后日期）
					investStartDateList.add(tempAdjustAfterDate);
				}
			}
		}

		// 根据临时变量.投资周期开始日列表的长度，调整 临时变量.调整前结束日列表、临时变量.开放周期开始日列表
		int dataLength = investStartDateList.size();
		// 删除临时变量.调整前结束日列表节点日期，从最后一个节点开始删除，最终保持与临时变量.投资周期开始日列表的长度一致
		// if (adjustBeforeEndDateList.size() > dataLength) {
		// for (int i = adjustBeforeEndDateList.size() - 1; i > dataLength - 1; i--) {
		// adjustBeforeEndDateList.remove(i);
		// }
		// }

		if (StringUtil.equals(DictCodeList.开放天数类型_每天.getValue(), inputDto.getOpenDaysType())) {
			openCycleStartDateList.add(inputDto.getVdate());
			for (Date CycleEndDate : openCycleEndDateList) {
				openCycleStartDateList.add(DateUtil.addDay(CycleEndDate, 1));
			}
		}

		// 删除临时变量.开放周期开始日列表节点日期，从最后一个节点开始删除，最终保持与临时变量.投资周期开始日列表的长度一致
		if (openCycleStartDateList.size() > dataLength) {
			for (int i = openCycleStartDateList.size() - 1; i > dataLength - 1; i--) {
				openCycleStartDateList.remove(i);
			}
		}

		// 处理模式为“滚动”或 （处理模式为“净值型”、“货币型” 且 是否周期型为“否”）时
		// 临时变量.投资周期结束日列表 = 临时变量.投资周期开始日列表
		investEndDateList.addAll(investStartDateList);
		// 处理模式为“普通N天”或“滚动N天”或 （处理模式为“净值型”、“货币型” 且 是否周期型为“是”）时
		if (StringUtil.equals(inputDto.getDealMode(), BussinessDictCodeList.处理模式_普通N天.getValue())
				|| StringUtil.equals(inputDto.getDealMode(), BussinessDictCodeList.处理模式_滚动N天.getValue())
				|| ((StringUtil.equals(inputDto.getDealMode(), BussinessDictCodeList.处理模式_净值型.getValue())
						|| StringUtil.equals(inputDto.getDealMode(), BussinessDictCodeList.处理模式_货币型.getValue()))
						&& StringUtil.equals(inputDto.getIsCycle(), DictCodeList.是否_是.getValue()))) {
			investEndDateList.clear();
			for (Date investStartDate : investStartDateList) {
				// 临时变量.开放周期起息日列表.节点日期 + 封闭期限
				String investEndDate = AdjustedTradeDayCal.getAdjustedTradeDay(
						DateUtil.format(DateUtil.addDay(investStartDate, inputDto.getCloseTerm())),
						inputDto.getTradedayRule(), dealCalendarHolidayList);
				investEndDateList.add(DateUtil.parse(investEndDate));
			}
		}

		// 6.生成输出参数
		// 将空添加至临时变量.调整前结束日列表第一个节点
		// adjustBeforeEndDateList.add(0, null);
		// 将输入.募集开始日添加至临时变量.开放周期开始日列表第一个节点
		// openCycleStartDateList.add(0, inputDto.getRaiseStrDate());
		// // 将输入.募集结束日添加至临时变量.开放周期结束日列表第一个节点
		// openCycleEndDateList.add(0, inputDto.getRaiseEndDate());
		// // 将输入.产品起息日添加至临时变量.投资周期开始日列表第一个节点
		// investStartDateList.add(0, inputDto.getVdate());
		// // 将输入.产品到期日添加至临时变量.投资周期结束日列表最后一个节点
		// if (StringUtil.equals(inputDto.getDealMode(),
		// BussinessDictCodeList.处理模式_普通N天.getValue())
		// || StringUtil.equals(inputDto.getDealMode(),
		// BussinessDictCodeList.处理模式_滚动N天.getValue())
		// || ((StringUtil.equals(inputDto.getDealMode(),
		// BussinessDictCodeList.处理模式_净值型.getValue())
		// || StringUtil.equals(inputDto.getDealMode(),
		// BussinessDictCodeList.处理模式_货币型.getValue()))
		// && StringUtil.equals(inputDto.getIsCycle(),
		// DictCodeList.是否_是.getValue()))) {
		// investEndDateList.add(0, null);
		// }
		// investEndDateList.add(inputDto.getMdate());
		// 净值日准则 生成净值日
		if (StringUtil.isNotEmpty(inputDto.getCpriceRule())) {

		}
		List<FinOpenPlan> resultList = new ArrayList<FinOpenPlan>();
		FinOpenPlan resultDto;
		for (int i = 0; i < investStartDateList.size(); i++) {
			resultDto = new FinOpenPlan();
			Date openCycleStartDate = openCycleStartDateList.get(i);
			Date openCycleEndDat = openCycleEndDateList.get(i);
			// 前五条数据比较
			if (i <= 5) {
				// 开放周期开始日 与产品起息日比较
				if (DateUtil.compareDate(inputDto.getVdate(), openCycleStartDate) > 0) {
					openCycleStartDate = inputDto.getVdate();
				}
				// 开放周期结束日 与产品起息日比较
				if (DateUtil.compareDate(inputDto.getVdate(), openCycleEndDat) > 0) {
					openCycleEndDat = inputDto.getVdate();
				}
			}
			
			// 调整前结束日
			// resultDto.setOpenDateEndUn(adjustBeforeEndDateList.get(i));
			// 开放周期开始日
			resultDto.setOpenDateStr(openCycleStartDate);
			// 开放周期结束日
			resultDto.setOpenDateEnd(openCycleEndDat);			
			
			//modify by yujiaping 2020-1-9 ZGBSS-45 解决节假日更新开放计划批，当营业日准则为不调整时，开放确认日未根据节假日顺延start
				Date openVdate = investStartDateList.get(i);	
				Date openMdate = investEndDateList.get(i);	
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String vdateString = simpleDateFormat.format(openVdate);
                String mdateString = simpleDateFormat.format(openMdate);
                boolean isHoliday = invDateCalService.isDealCalendarHoliday(vdateString, inputDto.getTradCalender());
                boolean isHolidaym = invDateCalService.isDealCalendarHoliday(mdateString, inputDto.getTradCalender());
                if(isHoliday) {
                openVdate = invDateCalService.calculationTradingDay(openVdate, 1, dealCalendarHolidayList, "1");
               }  
                if(isHolidaym) {
                	openMdate = invDateCalService.calculationTradingDay(openMdate, 1, dealCalendarHolidayList, "1");
                   }  
             // 投资周期开始日
		    resultDto.setOpenVdate(openVdate);	
			// 投资周期到期日
			resultDto.setOpenMdate(openMdate);			
			//modify by yujiaping 2020-1-9 ZGBSS-45 解决节假日更新开放计划批，当营业日准则为不调整时，开放确认日未根据节假日顺延end
			
			// 净值日
			resultDto.setOpenNdate(this.getOpenNdate(inputDto.getCpriceRule(), resultDto.getOpenDateEnd(),
					resultDto.getOpenVdate(), dealCalendarHolidayList));
			
			resultList.add(resultDto);
		}
		// 去重
		if (!StringUtil.isEmptyList(resultList)) {
			for (int i = resultList.size() - 1; i > 0; i--) {
				resultDto = resultList.get(i);
				if (DateUtil.compareDate(resultDto.getOpenDateStr(), resultList.get(i - 1).getOpenDateStr()) == 0
						&& DateUtil.compareDate(resultDto.getOpenDateEnd(), resultList.get(i - 1).getOpenDateEnd()) == 0
						&& DateUtil.compareDate(resultDto.getOpenVdate(), resultList.get(i - 1).getOpenVdate()) == 0) {
					resultList.remove(i);
				}
			}
		}
		return resultList;
	}

	private Date getWeekDayByDate(Date date, int weekOfMonth, int dayOfWeek) {
		Calendar c = Calendar.getInstance();
		// 设置日期
		c.setTime(date);
		int m = c.get(Calendar.WEEK_OF_MONTH);
		// 设置月初日期
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.WEEK_OF_MONTH, weekOfMonth + m);
		c.set(Calendar.DAY_OF_WEEK, 1 + dayOfWeek);
		return c.getTime();
	}

	/**
	 * 推算日期当月第几周星期几的 日期
	 * 
	 * @param date
	 *            所在日期
	 * @param weekOfMonth
	 *            这个月的第几周
	 * @param dayOfWeek
	 *            星期几
	 */
	private Date weekdatetodata(Date date, int weekOfMonth, int dayOfWeek) {
		Calendar c = Calendar.getInstance();
		// 设置日期
		c.setTime(date);
		if(c.getTime().getDay()>=dayOfWeek) {
			weekOfMonth = weekOfMonth+1;
			c.set(Calendar.WEEK_OF_MONTH, weekOfMonth);
		}
		// 设置月初日期
		c.set(Calendar.DAY_OF_MONTH, 1);
		Date dateTemp = c.getTime();
		c.set(Calendar.WEEK_OF_MONTH, weekOfMonth);
		c.set(Calendar.DAY_OF_WEEK, 1 + dayOfWeek);		
		if (dateTemp.compareTo(c.getTime()) > 0) {
			return null;
		}
		return c.getTime();
	}

	/**
	 * @param cpriceRule
	 *            净值日准则
	 * @param openDateEnd
	 *            开放结束日
	 * @param openVdate
	 *            开放确认日
	 * @since 1.0
	 */
	private Date getOpenNdate(String cpriceRule, Date openDateEnd, Date openVdate,
			List<String> dealCalendarHolidayList) {
		if (!StringUtil.isNotEmpty(cpriceRule)) {
			return null;
		}
		if (StringUtil.equals(DictCodeListExt1.净值日规则_开放确认日_1自然日.getValue(), cpriceRule)) {
			return DateUtil.addDay(openVdate, -1);
		} else if (StringUtil.equals(DictCodeListExt1.净值日规则_开放确认日_1工作日.getValue(), cpriceRule)) {
			Date tempdate = DateUtil.addDay(openVdate, -1);
			String dateStr = DateUtil.format(tempdate);
			while (dealCalendarHolidayList.indexOf(dateStr) > 0) {
				tempdate = DateUtil.addDay(tempdate, -1);
				dateStr = DateUtil.format(tempdate);
			}
			return tempdate;
		}
		return openDateEnd;
	}

	/**
	 * 获取指定日期之前最迟的【周几】所对应的日期
	 * 
	 * @param date
	 *            日期
	 * @param dayOfWeek
	 *            周几
	 * @return 周几所在日期
	 */
	private Date getFirstWeekDayByDate(Date date, String dayOfWeek) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		// 判断要计算的日期是否是周日，如果是则减一天计算周六的，否则会出问题，计算到下一周去了
		int dayWeek = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		if (1 == dayWeek) {
			cal.add(Calendar.DAY_OF_MONTH, -1);
		}
		cal.setFirstDayOfWeek(Calendar.MONDAY);// 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
		int day = cal.get(Calendar.DAY_OF_WEEK);// 获得当前日期是一个星期的第几天
		cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);// 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
		cal.add(Calendar.DATE, StringUtil.toInt(dayOfWeek) - 1);
		Date result = cal.getTime();
		return result;
	}
}
