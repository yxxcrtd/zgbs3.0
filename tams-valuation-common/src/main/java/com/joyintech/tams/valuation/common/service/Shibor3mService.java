/*
 * 文件名：CalculationCommonService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangzhiang
 * 修改时间：2018年5月31日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.valuation.common.dto.BootstrapDto;
import com.joyintech.tams.valuation.common.dto.DfDealDto;
import com.joyintech.tams.valuation.common.dto.DfMarketDataDto;
import com.joyintech.tams.valuation.common.dto.DfScheduleDto;
import com.joyintech.tams.valuation.common.dto.Shibor3mRateDto;
import com.joyintech.tams.valuation.common.dto.BootstrapResultDto;
import com.joyintech.tams.valuation.common.dto.DfCashPlanDto;
import com.joyintech.tams.valuation.common.utils.CalendarUtil;

/**
 * Shibor3M互换算法
 * 
 * @author chengzhong
 * @version 1.0 2019年12月24日
 * @see Shibor3mService
 * @since 1.0
 */
@Service
public class Shibor3mService {

	/**
	 * Shibor3M使用bootstrap(剥靴法)对互换曲线进行转换
	 * 
	 * @param today
	 *            当前日期
	 * @param shibor3mRateDto
	 *            利率曲线信息
	 * @param holidayList
	 *            节假日信息（根据日历获取未来133月的节假日"yyyy-MM-dd"）
	 */
	public List<BootstrapDto> calBootstrapShibor3m(Date today, Shibor3mRateDto shibor3mRateDto,
			List<String> holidayList) {

		// 定义曲线利率
		double shiboron_rate = shibor3mRateDto.getShiboron_rate().doubleValue();
		double shibor3m_rate = shibor3mRateDto.getShibor3m_rate().doubleValue();
		double shibor3m_6m_rate = shibor3mRateDto.getShibor3m_6m_rate().doubleValue();
		double shibor3m_9m_rate = shibor3mRateDto.getShibor3m_9m_rate().doubleValue();
		double shibor3m_12m_rate = shibor3mRateDto.getShibor3m_12m_rate().doubleValue();
		double shibor3m_24m_rate = shibor3mRateDto.getShibor3m_24m_rate().doubleValue();
		double shibor3m_36m_rate = shibor3mRateDto.getShibor3m_36m_rate().doubleValue();
		double shibor3m_48m_rate = shibor3mRateDto.getShibor3m_48m_rate().doubleValue();
		double shibor3m_60m_rate = shibor3mRateDto.getShibor3m_60m_rate().doubleValue();
		double shibor3m_84m_rate = shibor3mRateDto.getShibor3m_84m_rate().doubleValue();
		double shibor3m_120m_rate = shibor3mRateDto.getShibor3m_120m_rate().doubleValue();

		// 定义区间结束日期
		// 规则如：shibor3m计息结束日
		// 日期P=当前日期+3个月，P向后取大于等于当前日期的最小工作日X，X如果是当前月则最终日期取X，否则取小于等于当前日期最大工作日
		Date spot_date = CalendarUtil.getDateF(DateUtil.addDay(today, 1), holidayList);
		Date shibor3m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 3), holidayList);
		Date shibor3m_6m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 6), holidayList);
		Date shibor3m_9m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 9), holidayList);
		Date shibor3m_12m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 12), holidayList);
		Date shibor3m_15m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 15), holidayList);
		Date shibor3m_18m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 18), holidayList);
		Date shibor3m_21m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 21), holidayList);
		Date shibor3m_24m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 24), holidayList);
		Date shibor3m_27m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 27), holidayList);
		Date shibor3m_30m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 30), holidayList);
		Date shibor3m_33m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 33), holidayList);
		Date shibor3m_36m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 36), holidayList);
		Date shibor3m_39m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 39), holidayList);
		Date shibor3m_42m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 42), holidayList);
		Date shibor3m_45m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 45), holidayList);
		Date shibor3m_48m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 48), holidayList);
		Date shibor3m_51m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 51), holidayList);
		Date shibor3m_54m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 54), holidayList);
		Date shibor3m_57m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 57), holidayList);
		Date shibor3m_60m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 60), holidayList);
		Date shibor3m_63m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 63), holidayList);
		Date shibor3m_66m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 66), holidayList);
		Date shibor3m_69m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 69), holidayList);
		Date shibor3m_72m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 72), holidayList);
		Date shibor3m_75m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 75), holidayList);
		Date shibor3m_78m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 78), holidayList);
		Date shibor3m_81m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 81), holidayList);
		Date shibor3m_84m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 84), holidayList);
		Date shibor3m_87m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 87), holidayList);
		Date shibor3m_90m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 90), holidayList);
		Date shibor3m_93m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 93), holidayList);
		Date shibor3m_96m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 96), holidayList);
		Date shibor3m_99m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 99), holidayList);
		Date shibor3m_102m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 102), holidayList);
		Date shibor3m_105m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 105), holidayList);
		Date shibor3m_108m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 108), holidayList);
		Date shibor3m_111m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 111), holidayList);
		Date shibor3m_114m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 114), holidayList);
		Date shibor3m_117m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 117), holidayList);
		Date shibor3m_120m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 120), holidayList);
		Date shibor3m_123m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 123), holidayList);
		Date shibor3m_126m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 126), holidayList);
		Date shibor3m_129m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 129), holidayList);
		Date shibor3m_132m_end_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(spot_date, 132), holidayList);

		// # 区间计息天数
		long shibor3m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_end_date, spot_date);
		long shibor3m_6m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_6m_end_date, shibor3m_end_date);
		long shibor3m_9m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_9m_end_date, shibor3m_6m_end_date);
		long shibor3m_12m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_12m_end_date, shibor3m_9m_end_date);
		long shibor3m_15m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_15m_end_date, shibor3m_12m_end_date);
		long shibor3m_18m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_18m_end_date, shibor3m_15m_end_date);
		long shibor3m_21m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_21m_end_date, shibor3m_18m_end_date);
		long shibor3m_24m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_24m_end_date, shibor3m_21m_end_date);
		long shibor3m_27m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_27m_end_date, shibor3m_24m_end_date);
		long shibor3m_30m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_30m_end_date, shibor3m_27m_end_date);
		long shibor3m_33m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_33m_end_date, shibor3m_30m_end_date);
		long shibor3m_36m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_36m_end_date, shibor3m_33m_end_date);
		long shibor3m_39m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_39m_end_date, shibor3m_36m_end_date);
		long shibor3m_42m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_42m_end_date, shibor3m_39m_end_date);
		long shibor3m_45m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_45m_end_date, shibor3m_42m_end_date);
		long shibor3m_48m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_48m_end_date, shibor3m_45m_end_date);
		long shibor3m_51m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_51m_end_date, shibor3m_48m_end_date);
		long shibor3m_54m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_54m_end_date, shibor3m_51m_end_date);
		long shibor3m_57m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_57m_end_date, shibor3m_54m_end_date);
		long shibor3m_60m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_57m_end_date);
		long shibor3m_63m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_63m_end_date, shibor3m_60m_end_date);
		long shibor3m_66m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_66m_end_date, shibor3m_63m_end_date);
		long shibor3m_69m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_69m_end_date, shibor3m_66m_end_date);
		long shibor3m_72m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_72m_end_date, shibor3m_69m_end_date);
		long shibor3m_75m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_75m_end_date, shibor3m_72m_end_date);
		long shibor3m_78m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_78m_end_date, shibor3m_75m_end_date);
		long shibor3m_81m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_81m_end_date, shibor3m_78m_end_date);
		long shibor3m_84m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_81m_end_date);
		long shibor3m_87m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_87m_end_date, shibor3m_84m_end_date);
		long shibor3m_90m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_90m_end_date, shibor3m_87m_end_date);
		long shibor3m_93m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_93m_end_date, shibor3m_90m_end_date);
		long shibor3m_96m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_96m_end_date, shibor3m_93m_end_date);
		long shibor3m_99m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_99m_end_date, shibor3m_96m_end_date);
		long shibor3m_102m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_102m_end_date, shibor3m_99m_end_date);
		long shibor3m_105m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_105m_end_date, shibor3m_102m_end_date);
		long shibor3m_108m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_108m_end_date, shibor3m_105m_end_date);
		long shibor3m_111m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_111m_end_date, shibor3m_108m_end_date);
		long shibor3m_114m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_114m_end_date, shibor3m_111m_end_date);
		long shibor3m_117m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_117m_end_date, shibor3m_114m_end_date);
		long shibor3m_120m_accrual_days = -DateUtil.getBetweenDayNumber(shibor3m_120m_end_date, shibor3m_117m_end_date);

		// # 区间结束日到开始日期计息天数
		long shibor3m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_end_date, spot_date);
		long shibor3m_6m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_6m_end_date, spot_date);
		long shibor3m_9m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_9m_end_date, spot_date);
		long shibor3m_12m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_12m_end_date, spot_date);
		long shibor3m_15m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_15m_end_date, spot_date);
		long shibor3m_18m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_18m_end_date, spot_date);
		long shibor3m_21m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_21m_end_date, spot_date);
		long shibor3m_24m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_24m_end_date, spot_date);
		long shibor3m_27m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_27m_end_date, spot_date);
		long shibor3m_30m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_30m_end_date, spot_date);
		long shibor3m_33m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_33m_end_date, spot_date);
		long shibor3m_36m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_36m_end_date, spot_date);
		long shibor3m_39m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_39m_end_date, spot_date);
		long shibor3m_42m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_42m_end_date, spot_date);
		long shibor3m_45m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_45m_end_date, spot_date);
		long shibor3m_48m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_48m_end_date, spot_date);
		long shibor3m_51m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_51m_end_date, spot_date);
		long shibor3m_54m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_54m_end_date, spot_date);
		long shibor3m_57m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_57m_end_date, spot_date);
		long shibor3m_60m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, spot_date);
		long shibor3m_63m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_63m_end_date, spot_date);
		long shibor3m_66m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_66m_end_date, spot_date);
		long shibor3m_69m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_69m_end_date, spot_date);
		long shibor3m_72m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_72m_end_date, spot_date);
		long shibor3m_75m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_75m_end_date, spot_date);
		long shibor3m_78m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_78m_end_date, spot_date);
		long shibor3m_81m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_81m_end_date, spot_date);
		long shibor3m_84m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, spot_date);
		long shibor3m_87m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_87m_end_date, spot_date);
		long shibor3m_90m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_90m_end_date, spot_date);
		long shibor3m_93m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_93m_end_date, spot_date);
		long shibor3m_96m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_96m_end_date, spot_date);
		long shibor3m_99m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_99m_end_date, spot_date);
		long shibor3m_102m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_102m_end_date, spot_date);
		long shibor3m_105m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_105m_end_date, spot_date);
		long shibor3m_108m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_108m_end_date, spot_date);
		long shibor3m_111m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_111m_end_date, spot_date);
		long shibor3m_114m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_114m_end_date, spot_date);
		long shibor3m_117m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_117m_end_date, spot_date);
		long shibor3m_120m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_120m_end_date, spot_date);
		long shibor3m_123m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_123m_end_date, spot_date);
		long shibor3m_126m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_126m_end_date, spot_date);
		long shibor3m_129m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_129m_end_date, spot_date);
		long shibor3m_132m_to_today = -DateUtil.getBetweenDayNumber(shibor3m_132m_end_date, spot_date);

		// 使用bootstrap(剥靴法)对互换曲线进行转换，并计算贴现因子
		// 对于3M来说， 使用单利计算贴现因子；
		// 〖DF〗_3M=1/(1+〖shibor〗_3M*〖Day〗_3m/360)
		double shibor3m_df = 1 / (1 + shibor3m_rate * shibor3m_accrual_days / 360);
		// 令k=2,3,4,分别对应6M，9M，1Y的情况，使用利率互换曲线上的利率R_k可计算出各期限的贴现因子，计算公式如下
		// 〖DF〗_k=(1-R_k*∑_(i=1)^(k-1)▒〖(〖Day〗_i-〖Day〗_(i-1))/365*〖DF〗_i
		// 〗)/(1+R_k*(〖Day〗_k-〖Day〗_(k-1))/365)
		double shibor3m_6m_df = (1 - shibor3m_6m_rate * shibor3m_accrual_days / 365 * shibor3m_df)
				/ (1 + shibor3m_6m_rate * shibor3m_6m_accrual_days / 365);
		double shibor3m_9m_df = (1 - shibor3m_9m_rate * shibor3m_accrual_days / 365 * shibor3m_df
				- shibor3m_9m_rate * shibor3m_6m_accrual_days / 365 * shibor3m_6m_df)
				/ (1 + shibor3m_9m_rate * shibor3m_9m_accrual_days / 365);
		double shibor3m_12m_df = (1 - shibor3m_12m_rate * shibor3m_accrual_days / 365 * shibor3m_df
				- shibor3m_12m_rate * shibor3m_6m_accrual_days / 365 * shibor3m_6m_df
				- shibor3m_12m_rate * shibor3m_9m_accrual_days / 365 * shibor3m_9m_df)
				/ (1 + shibor3m_12m_rate * shibor3m_12m_accrual_days / 365);

		// 对于1年以上的情况， 收益率曲线上包含了1Y、2Y、3Y、4Y、5Y、7Y、10Y的点，在两个点之间的收益率按照线性插值算法进行估计

		// 第二年bootstrap_2y
		// 以1-2年为例，令k=5,6,7,8，分别对应15M，18M，21M，2Y的情况，则
		// 〖SC〗_k=〖SC〗_1Y+(〖Day〗_k-〖Day〗_1Y)/(〖Day〗_2Y-〖Day〗_1Y
		// )*〖(SC〗_2Y-〖SC〗_1Y)
		// 〖DF〗_k=e^(-〖SC〗_k*〖Day〗_k/365)
		// 令k=2Y， 代入公式1， 可通过插值法求出〖SC〗_2Y; 进而可求出15M，18M，21M，2Y的贴现因子和即期利率。
		double cashflow_3m = shibor3m_24m_rate * shibor3m_accrual_days / 365;
		double cashflow_6m = shibor3m_24m_rate * shibor3m_6m_accrual_days / 365;
		double cashflow_9m = shibor3m_24m_rate * shibor3m_9m_accrual_days / 365;
		double cashflow_12m = shibor3m_24m_rate * shibor3m_12m_accrual_days / 365;
		double cashflow_15m = shibor3m_24m_rate * shibor3m_15m_accrual_days / 365;
		double cashflow_18m = shibor3m_24m_rate * shibor3m_18m_accrual_days / 365;
		double cashflow_21m = shibor3m_24m_rate * shibor3m_21m_accrual_days / 365;
		double cashflow_24m = shibor3m_24m_rate * shibor3m_24m_accrual_days / 365 + 1;

		double cashflow_known = (cashflow_3m * shibor3m_df + cashflow_6m * shibor3m_6m_df + cashflow_9m * shibor3m_9m_df
				+ cashflow_12m * shibor3m_12m_df);

		long base_days = DateUtil.getBetweenDayNumber(spot_date, shibor3m_12m_end_date);
		double base_yield = Math.log(1 / shibor3m_12m_df) * 365 / base_days;

		//// 牛顿插值法计算2年的利率互换即期利率曲线〖SC〗_2Y
		double x = getNewton2y(0.000001, cashflow_15m, cashflow_18m, cashflow_21m, cashflow_24m, base_yield,
				shibor3m_15m_end_date, shibor3m_18m_end_date, shibor3m_21m_end_date, shibor3m_24m_end_date,
				shibor3m_12m_end_date, shibor3m_15m_to_today, shibor3m_18m_to_today, shibor3m_21m_to_today,
				shibor3m_24m_to_today, cashflow_known);

		double shibor3m_15m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_12m_end_date, shibor3m_15m_end_date))
						/ 365 * shibor3m_15m_to_today);
		double shibor3m_18m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_12m_end_date, shibor3m_18m_end_date))
						/ 365 * shibor3m_18m_to_today);
		double shibor3m_21m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_12m_end_date, shibor3m_21m_end_date))
						/ 365 * shibor3m_21m_to_today);
		double shibor3m_24m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_12m_end_date, shibor3m_24m_end_date))
						/ 365 * shibor3m_24m_to_today);

		// # bootstrap_3y
		cashflow_3m = shibor3m_36m_rate * shibor3m_accrual_days / 365;
		cashflow_6m = shibor3m_36m_rate * shibor3m_6m_accrual_days / 365;
		cashflow_9m = shibor3m_36m_rate * shibor3m_9m_accrual_days / 365;
		cashflow_12m = shibor3m_36m_rate * shibor3m_12m_accrual_days / 365;
		cashflow_15m = shibor3m_36m_rate * shibor3m_15m_accrual_days / 365;
		cashflow_18m = shibor3m_36m_rate * shibor3m_18m_accrual_days / 365;
		cashflow_21m = shibor3m_36m_rate * shibor3m_21m_accrual_days / 365;
		cashflow_24m = shibor3m_36m_rate * shibor3m_24m_accrual_days / 365;
		double cashflow_27m = shibor3m_36m_rate * shibor3m_27m_accrual_days / 365;
		double cashflow_30m = shibor3m_36m_rate * shibor3m_30m_accrual_days / 365;
		double cashflow_33m = shibor3m_36m_rate * shibor3m_33m_accrual_days / 365;
		double cashflow_36m = shibor3m_36m_rate * shibor3m_36m_accrual_days / 365 + 1;

		cashflow_known = (cashflow_3m * shibor3m_df + cashflow_6m * shibor3m_6m_df + cashflow_9m * shibor3m_9m_df
				+ cashflow_12m * shibor3m_12m_df + cashflow_15m * shibor3m_15m_df + cashflow_18m * shibor3m_18m_df
				+ cashflow_21m * shibor3m_21m_df + cashflow_24m * shibor3m_24m_df);

		base_days = DateUtil.getBetweenDayNumber(spot_date, shibor3m_24m_end_date);
		base_yield = Math.log(1 / shibor3m_24m_df) * 365 / base_days;

		// 牛顿插值法计算3年的利率互换即期利率曲线〖SC〗_3Y
		x = getNewton2y(0.000001, cashflow_27m, cashflow_30m, cashflow_33m, cashflow_36m, base_yield,
				shibor3m_27m_end_date, shibor3m_30m_end_date, shibor3m_33m_end_date, shibor3m_36m_end_date,
				shibor3m_24m_end_date, shibor3m_27m_to_today, shibor3m_30m_to_today, shibor3m_33m_to_today,
				shibor3m_36m_to_today, cashflow_known);

		double shibor3m_27m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_24m_end_date, shibor3m_27m_end_date))
						/ 365 * shibor3m_27m_to_today);
		double shibor3m_30m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_24m_end_date, shibor3m_30m_end_date))
						/ 365 * shibor3m_30m_to_today);
		double shibor3m_33m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_24m_end_date, shibor3m_33m_end_date))
						/ 365 * shibor3m_33m_to_today);
		double shibor3m_36m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_24m_end_date, shibor3m_36m_end_date))
						/ 365 * shibor3m_36m_to_today);

		// bootstrap_4y
		cashflow_3m = shibor3m_48m_rate * shibor3m_accrual_days / 365;
		cashflow_6m = shibor3m_48m_rate * shibor3m_6m_accrual_days / 365;
		cashflow_9m = shibor3m_48m_rate * shibor3m_9m_accrual_days / 365;
		cashflow_12m = shibor3m_48m_rate * shibor3m_12m_accrual_days / 365;
		cashflow_15m = shibor3m_48m_rate * shibor3m_15m_accrual_days / 365;
		cashflow_18m = shibor3m_48m_rate * shibor3m_18m_accrual_days / 365;
		cashflow_21m = shibor3m_48m_rate * shibor3m_21m_accrual_days / 365;
		cashflow_24m = shibor3m_48m_rate * shibor3m_24m_accrual_days / 365;
		cashflow_27m = shibor3m_48m_rate * shibor3m_27m_accrual_days / 365;
		cashflow_30m = shibor3m_48m_rate * shibor3m_30m_accrual_days / 365;
		cashflow_33m = shibor3m_48m_rate * shibor3m_33m_accrual_days / 365;
		cashflow_36m = shibor3m_48m_rate * shibor3m_36m_accrual_days / 365;
		double cashflow_39m = shibor3m_48m_rate * shibor3m_39m_accrual_days / 365;
		double cashflow_42m = shibor3m_48m_rate * shibor3m_42m_accrual_days / 365;
		double cashflow_45m = shibor3m_48m_rate * shibor3m_45m_accrual_days / 365;
		double cashflow_48m = shibor3m_48m_rate * shibor3m_48m_accrual_days / 365 + 1;

		cashflow_known = (cashflow_3m * shibor3m_df + cashflow_6m * shibor3m_6m_df + cashflow_9m * shibor3m_9m_df
				+ cashflow_12m * shibor3m_12m_df + cashflow_15m * shibor3m_15m_df + cashflow_18m * shibor3m_18m_df
				+ cashflow_21m * shibor3m_21m_df + cashflow_24m * shibor3m_24m_df + cashflow_27m * shibor3m_27m_df
				+ cashflow_30m * shibor3m_30m_df + cashflow_33m * shibor3m_33m_df + cashflow_36m * shibor3m_36m_df);

		base_days = DateUtil.getBetweenDayNumber(spot_date, shibor3m_36m_end_date);
		base_yield = Math.log(1 / shibor3m_36m_df) * 365 / base_days;

		// 牛顿插值法计算4年的利率互换即期利率曲线〖SC〗_4Y
		x = getNewton2y(0.000001, cashflow_39m, cashflow_42m, cashflow_45m, cashflow_48m, base_yield,
				shibor3m_39m_end_date, shibor3m_42m_end_date, shibor3m_45m_end_date, shibor3m_48m_end_date,
				shibor3m_36m_end_date, shibor3m_39m_to_today, shibor3m_42m_to_today, shibor3m_45m_to_today,
				shibor3m_48m_to_today, cashflow_known);

		double shibor3m_39m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_36m_end_date, shibor3m_39m_end_date))
						/ 365 * shibor3m_39m_to_today);
		double shibor3m_42m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_36m_end_date, shibor3m_42m_end_date))
						/ 365 * shibor3m_42m_to_today);
		double shibor3m_45m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_36m_end_date, shibor3m_45m_end_date))
						/ 365 * shibor3m_45m_to_today);
		double shibor3m_48m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_36m_end_date, shibor3m_48m_end_date))
						/ 365 * shibor3m_48m_to_today);

		// bootstrap_5y
		cashflow_3m = shibor3m_60m_rate * shibor3m_accrual_days / 365;
		cashflow_6m = shibor3m_60m_rate * shibor3m_6m_accrual_days / 365;
		cashflow_9m = shibor3m_60m_rate * shibor3m_9m_accrual_days / 365;
		cashflow_12m = shibor3m_60m_rate * shibor3m_12m_accrual_days / 365;
		cashflow_15m = shibor3m_60m_rate * shibor3m_15m_accrual_days / 365;
		cashflow_18m = shibor3m_60m_rate * shibor3m_18m_accrual_days / 365;
		cashflow_21m = shibor3m_60m_rate * shibor3m_21m_accrual_days / 365;
		cashflow_24m = shibor3m_60m_rate * shibor3m_24m_accrual_days / 365;
		cashflow_27m = shibor3m_60m_rate * shibor3m_27m_accrual_days / 365;
		cashflow_30m = shibor3m_60m_rate * shibor3m_30m_accrual_days / 365;
		cashflow_33m = shibor3m_60m_rate * shibor3m_33m_accrual_days / 365;
		cashflow_36m = shibor3m_60m_rate * shibor3m_36m_accrual_days / 365;
		cashflow_39m = shibor3m_60m_rate * shibor3m_39m_accrual_days / 365;
		cashflow_42m = shibor3m_60m_rate * shibor3m_42m_accrual_days / 365;
		cashflow_45m = shibor3m_60m_rate * shibor3m_45m_accrual_days / 365;
		cashflow_48m = shibor3m_60m_rate * shibor3m_48m_accrual_days / 365;
		double cashflow_51m = shibor3m_60m_rate * shibor3m_51m_accrual_days / 365;
		double cashflow_54m = shibor3m_60m_rate * shibor3m_54m_accrual_days / 365;
		double cashflow_57m = shibor3m_60m_rate * shibor3m_57m_accrual_days / 365;
		double cashflow_60m = shibor3m_60m_rate * shibor3m_60m_accrual_days / 365 + 1;

		cashflow_known = (cashflow_3m * shibor3m_df + cashflow_6m * shibor3m_6m_df + cashflow_9m * shibor3m_9m_df
				+ cashflow_12m * shibor3m_12m_df + cashflow_15m * shibor3m_15m_df + cashflow_18m * shibor3m_18m_df
				+ cashflow_21m * shibor3m_21m_df + cashflow_24m * shibor3m_24m_df + cashflow_27m * shibor3m_27m_df
				+ cashflow_30m * shibor3m_30m_df + cashflow_33m * shibor3m_33m_df + cashflow_36m * shibor3m_36m_df
				+ cashflow_39m * shibor3m_39m_df + cashflow_42m * shibor3m_42m_df + cashflow_45m * shibor3m_45m_df
				+ cashflow_48m * shibor3m_48m_df);

		base_days = DateUtil.getBetweenDayNumber(spot_date, shibor3m_48m_end_date);
		base_yield = Math.log(1 / shibor3m_48m_df) * 365 / base_days;

		// 牛顿插值法计算5年的利率互换即期利率曲线〖SC〗_5Y
		x = getNewton2y(0.000001, cashflow_51m, cashflow_54m, cashflow_57m, cashflow_60m, base_yield,
				shibor3m_51m_end_date, shibor3m_54m_end_date, shibor3m_57m_end_date, shibor3m_60m_end_date,
				shibor3m_48m_end_date, shibor3m_51m_to_today, shibor3m_54m_to_today, shibor3m_57m_to_today,
				shibor3m_60m_to_today, cashflow_known);

		double shibor3m_51m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_48m_end_date, shibor3m_51m_end_date))
						/ 365 * shibor3m_51m_to_today);
		double shibor3m_54m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_48m_end_date, shibor3m_54m_end_date))
						/ 365 * shibor3m_54m_to_today);
		double shibor3m_57m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_48m_end_date, shibor3m_57m_end_date))
						/ 365 * shibor3m_57m_to_today);
		double shibor3m_60m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_48m_end_date, shibor3m_60m_end_date))
						/ 365 * shibor3m_60m_to_today);

		// bootstrap_7y
		cashflow_3m = shibor3m_84m_rate * shibor3m_accrual_days / 365;
		cashflow_6m = shibor3m_84m_rate * shibor3m_6m_accrual_days / 365;
		cashflow_9m = shibor3m_84m_rate * shibor3m_9m_accrual_days / 365;
		cashflow_12m = shibor3m_84m_rate * shibor3m_12m_accrual_days / 365;
		cashflow_15m = shibor3m_84m_rate * shibor3m_15m_accrual_days / 365;
		cashflow_18m = shibor3m_84m_rate * shibor3m_18m_accrual_days / 365;
		cashflow_21m = shibor3m_84m_rate * shibor3m_21m_accrual_days / 365;
		cashflow_24m = shibor3m_84m_rate * shibor3m_24m_accrual_days / 365;
		cashflow_27m = shibor3m_84m_rate * shibor3m_27m_accrual_days / 365;
		cashflow_30m = shibor3m_84m_rate * shibor3m_30m_accrual_days / 365;
		cashflow_33m = shibor3m_84m_rate * shibor3m_33m_accrual_days / 365;
		cashflow_36m = shibor3m_84m_rate * shibor3m_36m_accrual_days / 365;
		cashflow_39m = shibor3m_84m_rate * shibor3m_39m_accrual_days / 365;
		cashflow_42m = shibor3m_84m_rate * shibor3m_42m_accrual_days / 365;
		cashflow_45m = shibor3m_84m_rate * shibor3m_45m_accrual_days / 365;
		cashflow_48m = shibor3m_84m_rate * shibor3m_48m_accrual_days / 365;
		cashflow_51m = shibor3m_84m_rate * shibor3m_51m_accrual_days / 365;
		cashflow_54m = shibor3m_84m_rate * shibor3m_54m_accrual_days / 365;
		cashflow_57m = shibor3m_84m_rate * shibor3m_57m_accrual_days / 365;
		cashflow_60m = shibor3m_84m_rate * shibor3m_60m_accrual_days / 365;
		double cashflow_63m = shibor3m_84m_rate * shibor3m_63m_accrual_days / 365;
		double cashflow_66m = shibor3m_84m_rate * shibor3m_66m_accrual_days / 365;
		double cashflow_69m = shibor3m_84m_rate * shibor3m_69m_accrual_days / 365;
		double cashflow_72m = shibor3m_84m_rate * shibor3m_72m_accrual_days / 365;
		double cashflow_75m = shibor3m_84m_rate * shibor3m_75m_accrual_days / 365;
		double cashflow_78m = shibor3m_84m_rate * shibor3m_78m_accrual_days / 365;
		double cashflow_81m = shibor3m_84m_rate * shibor3m_81m_accrual_days / 365;
		double cashflow_84m = shibor3m_84m_rate * shibor3m_84m_accrual_days / 365 + 1;

		cashflow_known = (cashflow_3m * shibor3m_df + cashflow_6m * shibor3m_6m_df + cashflow_9m * shibor3m_9m_df
				+ cashflow_12m * shibor3m_12m_df + cashflow_15m * shibor3m_15m_df + cashflow_18m * shibor3m_18m_df
				+ cashflow_21m * shibor3m_21m_df + cashflow_24m * shibor3m_24m_df + cashflow_27m * shibor3m_27m_df
				+ cashflow_30m * shibor3m_30m_df + cashflow_33m * shibor3m_33m_df + cashflow_36m * shibor3m_36m_df
				+ cashflow_39m * shibor3m_39m_df + cashflow_42m * shibor3m_42m_df + cashflow_45m * shibor3m_45m_df
				+ cashflow_48m * shibor3m_48m_df + cashflow_51m * shibor3m_51m_df + cashflow_54m * shibor3m_54m_df
				+ cashflow_57m * shibor3m_57m_df + cashflow_60m * shibor3m_60m_df);

		base_days = DateUtil.getBetweenDayNumber(spot_date, shibor3m_60m_end_date);
		base_yield = Math.log(1 / shibor3m_60m_df) * 365 / base_days;

		// 牛顿插值法计算7年的利率互换即期利率曲线〖SC〗_7Y
		x = getNewton7y(0.000001, cashflow_63m, cashflow_66m, cashflow_69m, cashflow_72m, cashflow_75m, cashflow_78m,
				cashflow_81m, cashflow_84m, base_yield, shibor3m_63m_end_date, shibor3m_66m_end_date,
				shibor3m_69m_end_date, shibor3m_72m_end_date, shibor3m_75m_end_date, shibor3m_78m_end_date,
				shibor3m_81m_end_date, shibor3m_84m_end_date, shibor3m_60m_end_date, shibor3m_63m_to_today,
				shibor3m_66m_to_today, shibor3m_69m_to_today, shibor3m_72m_to_today, shibor3m_75m_to_today,
				shibor3m_78m_to_today, shibor3m_81m_to_today, shibor3m_84m_to_today, cashflow_known);

		// while(Math.abs(cashflow_known +
		// cashflow_63m / Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_63m_end_date)) / 365 * shibor3m_63m_to_today) +
		// cashflow_66m / Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_66m_end_date)) / 365 * shibor3m_66m_to_today) +
		// cashflow_69m / Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_69m_end_date)) / 365 * shibor3m_69m_to_today) +
		// cashflow_72m / Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_72m_end_date)) / 365 * shibor3m_72m_to_today) +
		// cashflow_75m / Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_75m_end_date)) / 365 * shibor3m_75m_to_today) +
		// cashflow_78m / Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_78m_end_date)) / 365 * shibor3m_78m_to_today) +
		// cashflow_81m / Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_81m_end_date)) / 365 * shibor3m_81m_to_today) +
		// cashflow_84m / Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_84m_end_date)) / 365 * shibor3m_84m_to_today) - 1) >
		// 0.000000001) {
		// x -= ((cashflow_known +
		// cashflow_63m / Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_63m_end_date)) / 365 * shibor3m_63m_to_today) +
		// cashflow_66m / Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_66m_end_date)) / 365 * shibor3m_66m_to_today) +
		// cashflow_69m / Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_69m_end_date)) / 365 * shibor3m_69m_to_today) +
		// cashflow_72m / Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_72m_end_date)) / 365 * shibor3m_72m_to_today) +
		// cashflow_75m / Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_75m_end_date)) / 365 * shibor3m_75m_to_today) +
		// cashflow_78m / Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_78m_end_date)) / 365 * shibor3m_78m_to_today) +
		// cashflow_81m / Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_81m_end_date)) / 365 * shibor3m_81m_to_today) +
		// cashflow_84m / Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_84m_end_date)) / 365 * shibor3m_84m_to_today) -
		// 1 - 0.000000001) /
		// (cashflow_63m * -1 / Math.pow((Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_63m_end_date)) / 365 * shibor3m_63m_to_today)), 2) *
		// Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_63m_end_date)) / 365 * shibor3m_63m_to_today) *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_63m_end_date) / 365 * shibor3m_63m_to_today +
		// cashflow_66m * -1 / Math.pow((Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_66m_end_date)) / 365 * shibor3m_66m_to_today)), 2) *
		// Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_66m_end_date)) / 365 * shibor3m_66m_to_today) *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_66m_end_date) / 365 * shibor3m_66m_to_today +
		// cashflow_69m * -1 / Math.pow((Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_69m_end_date)) / 365 * shibor3m_69m_to_today)), 2) *
		// Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_69m_end_date)) / 365 * shibor3m_69m_to_today) *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_69m_end_date) / 365 * shibor3m_69m_to_today +
		// cashflow_72m * -1 / Math.pow((Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_72m_end_date)) / 365 * shibor3m_72m_to_today)), 2) *
		// Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_72m_end_date)) / 365 * shibor3m_72m_to_today) *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_72m_end_date) / 365 * shibor3m_72m_to_today +
		// cashflow_75m * -1 / Math.pow((Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_75m_end_date)) / 365 * shibor3m_75m_to_today)), 2) *
		// Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_75m_end_date)) / 365 * shibor3m_75m_to_today) *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_75m_end_date) / 365 * shibor3m_75m_to_today +
		// cashflow_78m * -1 / Math.pow((Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_78m_end_date)) / 365 * shibor3m_78m_to_today)), 2) *
		// Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_78m_end_date)) / 365 * shibor3m_78m_to_today) *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_78m_end_date) / 365 * shibor3m_78m_to_today +
		// cashflow_81m * -1 / Math.pow((Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_81m_end_date)) / 365 * shibor3m_81m_to_today)), 2) *
		// Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_81m_end_date)) / 365 * shibor3m_81m_to_today) *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_81m_end_date) / 365 * shibor3m_81m_to_today +
		// cashflow_84m * -1 / Math.pow((Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_84m_end_date)) / 365 * shibor3m_84m_to_today)), 2) *
		// Math.exp((base_yield + x *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_84m_end_date)) / 365 * shibor3m_84m_to_today) *
		// DateUtil.getBetweenDayNumber(shibor3m_60m_end_date,
		// shibor3m_84m_end_date) / 365 * shibor3m_84m_to_today));
		// }

		double shibor3m_63m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_63m_end_date))
						/ 365 * shibor3m_63m_to_today);
		double shibor3m_66m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_66m_end_date))
						/ 365 * shibor3m_66m_to_today);
		double shibor3m_69m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_69m_end_date))
						/ 365 * shibor3m_69m_to_today);
		double shibor3m_72m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_72m_end_date))
						/ 365 * shibor3m_72m_to_today);
		double shibor3m_75m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_75m_end_date))
						/ 365 * shibor3m_75m_to_today);
		double shibor3m_78m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_78m_end_date))
						/ 365 * shibor3m_78m_to_today);
		double shibor3m_81m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_81m_end_date))
						/ 365 * shibor3m_81m_to_today);
		double shibor3m_84m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_84m_end_date))
						/ 365 * shibor3m_84m_to_today);

		// bootstrap_10y
		cashflow_3m = shibor3m_120m_rate * shibor3m_accrual_days / 365;
		cashflow_6m = shibor3m_120m_rate * shibor3m_6m_accrual_days / 365;
		cashflow_9m = shibor3m_120m_rate * shibor3m_9m_accrual_days / 365;
		cashflow_12m = shibor3m_120m_rate * shibor3m_12m_accrual_days / 365;
		cashflow_15m = shibor3m_120m_rate * shibor3m_15m_accrual_days / 365;
		cashflow_18m = shibor3m_120m_rate * shibor3m_18m_accrual_days / 365;
		cashflow_21m = shibor3m_120m_rate * shibor3m_21m_accrual_days / 365;
		cashflow_24m = shibor3m_120m_rate * shibor3m_24m_accrual_days / 365;
		cashflow_27m = shibor3m_120m_rate * shibor3m_27m_accrual_days / 365;
		cashflow_30m = shibor3m_120m_rate * shibor3m_30m_accrual_days / 365;
		cashflow_33m = shibor3m_120m_rate * shibor3m_33m_accrual_days / 365;
		cashflow_36m = shibor3m_120m_rate * shibor3m_36m_accrual_days / 365;
		cashflow_39m = shibor3m_120m_rate * shibor3m_39m_accrual_days / 365;
		cashflow_42m = shibor3m_120m_rate * shibor3m_42m_accrual_days / 365;
		cashflow_45m = shibor3m_120m_rate * shibor3m_45m_accrual_days / 365;
		cashflow_48m = shibor3m_120m_rate * shibor3m_48m_accrual_days / 365;
		cashflow_51m = shibor3m_120m_rate * shibor3m_51m_accrual_days / 365;
		cashflow_54m = shibor3m_120m_rate * shibor3m_54m_accrual_days / 365;
		cashflow_57m = shibor3m_120m_rate * shibor3m_57m_accrual_days / 365;
		cashflow_60m = shibor3m_120m_rate * shibor3m_60m_accrual_days / 365;
		cashflow_63m = shibor3m_120m_rate * shibor3m_63m_accrual_days / 365;
		cashflow_66m = shibor3m_120m_rate * shibor3m_66m_accrual_days / 365;
		cashflow_69m = shibor3m_120m_rate * shibor3m_69m_accrual_days / 365;
		cashflow_72m = shibor3m_120m_rate * shibor3m_72m_accrual_days / 365;
		cashflow_75m = shibor3m_120m_rate * shibor3m_75m_accrual_days / 365;
		cashflow_78m = shibor3m_120m_rate * shibor3m_78m_accrual_days / 365;
		cashflow_81m = shibor3m_120m_rate * shibor3m_81m_accrual_days / 365;
		cashflow_84m = shibor3m_120m_rate * shibor3m_84m_accrual_days / 365;
		double cashflow_87m = shibor3m_120m_rate * shibor3m_87m_accrual_days / 365;
		double cashflow_90m = shibor3m_120m_rate * shibor3m_90m_accrual_days / 365;
		double cashflow_93m = shibor3m_120m_rate * shibor3m_93m_accrual_days / 365;
		double cashflow_96m = shibor3m_120m_rate * shibor3m_96m_accrual_days / 365;
		double cashflow_99m = shibor3m_120m_rate * shibor3m_99m_accrual_days / 365;
		double cashflow_102m = shibor3m_120m_rate * shibor3m_102m_accrual_days / 365;
		double cashflow_105m = shibor3m_120m_rate * shibor3m_105m_accrual_days / 365;
		double cashflow_108m = shibor3m_120m_rate * shibor3m_108m_accrual_days / 365;
		double cashflow_111m = shibor3m_120m_rate * shibor3m_111m_accrual_days / 365;
		double cashflow_114m = shibor3m_120m_rate * shibor3m_114m_accrual_days / 365;
		double cashflow_117m = shibor3m_120m_rate * shibor3m_117m_accrual_days / 365;
		double cashflow_120m = shibor3m_120m_rate * shibor3m_120m_accrual_days / 365 + 1;

		cashflow_known = (cashflow_3m * shibor3m_df + cashflow_6m * shibor3m_6m_df + cashflow_9m * shibor3m_9m_df
				+ cashflow_12m * shibor3m_12m_df + cashflow_15m * shibor3m_15m_df + cashflow_18m * shibor3m_18m_df
				+ cashflow_21m * shibor3m_21m_df + cashflow_24m * shibor3m_24m_df + cashflow_27m * shibor3m_27m_df
				+ cashflow_30m * shibor3m_30m_df + cashflow_33m * shibor3m_33m_df + cashflow_36m * shibor3m_36m_df
				+ cashflow_39m * shibor3m_39m_df + cashflow_42m * shibor3m_42m_df + cashflow_45m * shibor3m_45m_df
				+ cashflow_48m * shibor3m_48m_df + cashflow_51m * shibor3m_51m_df + cashflow_54m * shibor3m_54m_df
				+ cashflow_57m * shibor3m_57m_df + cashflow_60m * shibor3m_60m_df + cashflow_63m * shibor3m_63m_df
				+ cashflow_66m * shibor3m_66m_df + cashflow_69m * shibor3m_69m_df + cashflow_72m * shibor3m_72m_df
				+ cashflow_75m * shibor3m_75m_df + cashflow_78m * shibor3m_78m_df + cashflow_81m * shibor3m_81m_df
				+ cashflow_84m * shibor3m_84m_df);

		base_days = DateUtil.getBetweenDayNumber(spot_date, shibor3m_84m_end_date);
		base_yield = Math.log(1 / shibor3m_84m_df) * 365 / base_days;

		// 牛顿插值法计算10年的利率互换即期利率曲线〖SC〗_10Y
		x = getNewton10y(0.000001, cashflow_87m, cashflow_90m, cashflow_93m, cashflow_96m, cashflow_99m, cashflow_102m,
				cashflow_105m, cashflow_108m, cashflow_111m, cashflow_114m, cashflow_117m, cashflow_120m, base_yield,
				shibor3m_87m_end_date, shibor3m_90m_end_date, shibor3m_93m_end_date, shibor3m_96m_end_date,
				shibor3m_99m_end_date, shibor3m_102m_end_date, shibor3m_105m_end_date, shibor3m_108m_end_date,
				shibor3m_111m_end_date, shibor3m_114m_end_date, shibor3m_117m_end_date, shibor3m_120m_end_date,
				shibor3m_84m_end_date, shibor3m_87m_to_today, shibor3m_90m_to_today, shibor3m_93m_to_today,
				shibor3m_96m_to_today, shibor3m_99m_to_today, shibor3m_102m_to_today, shibor3m_105m_to_today,
				shibor3m_108m_to_today, shibor3m_111m_to_today, shibor3m_114m_to_today, shibor3m_117m_to_today,
				shibor3m_120m_to_today, cashflow_known);

		double shibor3m_87m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_87m_end_date))
						/ 365 * shibor3m_87m_to_today);
		double shibor3m_90m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_90m_end_date))
						/ 365 * shibor3m_90m_to_today);
		double shibor3m_93m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_93m_end_date))
						/ 365 * shibor3m_93m_to_today);
		double shibor3m_96m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_96m_end_date))
						/ 365 * shibor3m_96m_to_today);
		double shibor3m_99m_df = 1
				/ Math.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_99m_end_date))
						/ 365 * shibor3m_99m_to_today);
		double shibor3m_102m_df = 1 / Math
				.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_102m_end_date))
						/ 365 * shibor3m_102m_to_today);
		double shibor3m_105m_df = 1 / Math
				.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_105m_end_date))
						/ 365 * shibor3m_105m_to_today);
		double shibor3m_108m_df = 1 / Math
				.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_108m_end_date))
						/ 365 * shibor3m_108m_to_today);
		double shibor3m_111m_df = 1 / Math
				.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_111m_end_date))
						/ 365 * shibor3m_111m_to_today);
		double shibor3m_114m_df = 1 / Math
				.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_114m_end_date))
						/ 365 * shibor3m_114m_to_today);
		double shibor3m_117m_df = 1 / Math
				.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_117m_end_date))
						/ 365 * shibor3m_117m_to_today);
		double shibor3m_120m_df = 1 / Math
				.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_120m_end_date))
						/ 365 * shibor3m_120m_to_today);
		double shibor3m_123m_df = 1 / Math
				.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_120m_end_date))
						/ 365 * shibor3m_123m_to_today);
		double shibor3m_126m_df = 1 / Math
				.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_120m_end_date))
						/ 365 * shibor3m_126m_to_today);
		double shibor3m_129m_df = 1 / Math
				.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_120m_end_date))
						/ 365 * shibor3m_129m_to_today);
		double shibor3m_132m_df = 1 / Math
				.exp((base_yield + x * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_120m_end_date))
						/ 365 * shibor3m_132m_to_today);

		// 计算FORWARD_RATE
		// 计息结束日集合
		Date[] end_date_grp = { shibor3m_end_date, shibor3m_6m_end_date, shibor3m_9m_end_date, shibor3m_12m_end_date,
				shibor3m_15m_end_date, shibor3m_18m_end_date, shibor3m_21m_end_date, shibor3m_24m_end_date,
				shibor3m_27m_end_date, shibor3m_30m_end_date, shibor3m_33m_end_date, shibor3m_36m_end_date,
				shibor3m_39m_end_date, shibor3m_42m_end_date, shibor3m_45m_end_date, shibor3m_48m_end_date,
				shibor3m_51m_end_date, shibor3m_54m_end_date, shibor3m_57m_end_date, shibor3m_60m_end_date,
				shibor3m_63m_end_date, shibor3m_66m_end_date, shibor3m_69m_end_date, shibor3m_72m_end_date,
				shibor3m_75m_end_date, shibor3m_78m_end_date, shibor3m_81m_end_date, shibor3m_84m_end_date,
				shibor3m_87m_end_date, shibor3m_90m_end_date, shibor3m_93m_end_date, shibor3m_96m_end_date,
				shibor3m_99m_end_date, shibor3m_102m_end_date, shibor3m_105m_end_date, shibor3m_108m_end_date,
				shibor3m_111m_end_date, shibor3m_114m_end_date, shibor3m_117m_end_date, shibor3m_120m_end_date,
				shibor3m_123m_end_date, shibor3m_126m_end_date, shibor3m_129m_end_date, shibor3m_132m_end_date };
		List<Date> end_date_list = Arrays.asList(end_date_grp);
		// 计息区间天数集合
		List<Long> to_today_days_list = Arrays.asList(shibor3m_to_today, shibor3m_6m_to_today, shibor3m_9m_to_today,
				shibor3m_12m_to_today, shibor3m_15m_to_today, shibor3m_18m_to_today, shibor3m_21m_to_today,
				shibor3m_24m_to_today, shibor3m_27m_to_today, shibor3m_30m_to_today, shibor3m_33m_to_today,
				shibor3m_36m_to_today, shibor3m_39m_to_today, shibor3m_42m_to_today, shibor3m_45m_to_today,
				shibor3m_48m_to_today, shibor3m_51m_to_today, shibor3m_54m_to_today, shibor3m_57m_to_today,
				shibor3m_60m_to_today, shibor3m_63m_to_today, shibor3m_66m_to_today, shibor3m_69m_to_today,
				shibor3m_72m_to_today, shibor3m_75m_to_today, shibor3m_78m_to_today, shibor3m_81m_to_today,
				shibor3m_84m_to_today, shibor3m_87m_to_today, shibor3m_90m_to_today, shibor3m_93m_to_today,
				shibor3m_96m_to_today, shibor3m_99m_to_today, shibor3m_102m_to_today, shibor3m_105m_to_today,
				shibor3m_108m_to_today, shibor3m_111m_to_today, shibor3m_114m_to_today, shibor3m_117m_to_today,
				shibor3m_120m_to_today, shibor3m_123m_to_today, shibor3m_126m_to_today, shibor3m_129m_to_today,
				shibor3m_132m_to_today);
		// 区间利率集合
		List<Double> df_list = Arrays.asList(shibor3m_df, shibor3m_6m_df, shibor3m_9m_df, shibor3m_12m_df,
				shibor3m_15m_df, shibor3m_18m_df, shibor3m_21m_df, shibor3m_24m_df, shibor3m_27m_df, shibor3m_30m_df,
				shibor3m_33m_df, shibor3m_36m_df, shibor3m_39m_df, shibor3m_42m_df, shibor3m_45m_df, shibor3m_48m_df,
				shibor3m_51m_df, shibor3m_54m_df, shibor3m_57m_df, shibor3m_60m_df, shibor3m_63m_df, shibor3m_66m_df,
				shibor3m_69m_df, shibor3m_72m_df, shibor3m_75m_df, shibor3m_78m_df, shibor3m_81m_df, shibor3m_84m_df,
				shibor3m_87m_df, shibor3m_90m_df, shibor3m_93m_df, shibor3m_96m_df, shibor3m_99m_df, shibor3m_102m_df,
				shibor3m_105m_df, shibor3m_108m_df, shibor3m_111m_df, shibor3m_114m_df, shibor3m_117m_df,
				shibor3m_120m_df, shibor3m_123m_df, shibor3m_126m_df, shibor3m_129m_df, shibor3m_132m_df);
		// 即期利率集合计算
		// python转换：spot_yield_list = (np.log(1 / np.array(df_list)) * 365 /
		// np.array(to_today_days_list)).tolist()
		List<Double> spot_yield_list = new ArrayList<Double>();
		for (int i = 0; i < df_list.size(); i++) {
			spot_yield_list.add(Math.log(1 / df_list.get(i)) * 365 / to_today_days_list.get(i));
		}
		// python转换：df_tmp01 = pd.DataFrame([end_date_list, to_today_days_list,
		// spot_yield_list],
		// ['END_DATE', 'DAYS', 'SPOT_YIELD']).T
		List<BootstrapResultDto> df_tmp01 = new ArrayList<BootstrapResultDto>();
		for (int i = 0; i < end_date_list.size(); i++) {
			BootstrapResultDto tmp01 = new BootstrapResultDto();
			tmp01.setEnd_date(end_date_list.get(i));
			tmp01.setDsys(to_today_days_list.get(i));
			tmp01.setSpot_yield(spot_yield_list.get(i));
			df_tmp01.add(tmp01);
		}

		// python转换：date_list = [spot_date + datetime.timedelta(days=y) for y in
		// range(365 * 11)]
		List<Date> df_tmp02 = new ArrayList<Date>();
		for (int i = 0; i < 365 * 11; i++) {
			df_tmp02.add(DateUtil.addDay(spot_date, i));
		}
		// python转换：df_tmp03 = df_tmp01[['END_DATE']]
		List<Date> df_tmp03 = new ArrayList<Date>();
		for (int i = 0; i < df_tmp01.size(); i++) {
			df_tmp03.add(df_tmp01.get(i).getEnd_date());
		}
		// python转换：df_tmp04 = pd.merge(df_tmp02, df_tmp03, left_on=['MARK'],
		// right_on=['MARK'], how='inner')
		// df_tmp04 = df_tmp04[df_tmp04.DATE >= df_tmp04.END_DATE]
		// df_tmp04 = df_tmp04.groupby(['DATE']).max().reset_index()
		List<BootstrapResultDto> df_tmp04 = new ArrayList<BootstrapResultDto>();
		for (int i = 0; i < df_tmp02.size(); i++) {
			// df_tmp02的>=df_tmp03最小值才开始计算
			if (df_tmp02.get(i).compareTo(df_tmp03.get(0)) >= 0) {
				for (int j = df_tmp03.size() - 1; j >= 0; j--) {
					if (df_tmp02.get(i).compareTo(df_tmp03.get(j)) >= 0) {
						BootstrapResultDto tmp04 = new BootstrapResultDto();
						tmp04.setDate(df_tmp02.get(i));
						tmp04.setEnd_date(df_tmp03.get(j));
						df_tmp04.add(tmp04);
						break;
					}
				}
			}
		}

		// python转换：df_result = pd.merge(df_tmp02, df_tmp04, left_on=['DATE'],
		// right_on=['DATE'], how='left')
		// df_result = df_result[['DATE', 'END_DATE']]
		List<BootstrapResultDto> df_result = new ArrayList<BootstrapResultDto>();
		for (Date tmp02 : df_tmp02) {
			BootstrapResultDto result = new BootstrapResultDto();
			result.setDate(tmp02);
			for (BootstrapResultDto tmp04 : df_tmp04) {
				if (tmp02.compareTo(tmp04.getDate()) == 0) {
					result.setEnd_date(tmp04.getEnd_date());
					break;
				}
			}
			df_result.add(result);
		}
		// python转换：df_result = pd.merge(df_result, df_tmp01,
		// left_on=['END_DATE'], right_on=['END_DATE'], how='left')
		// df_result.rename(columns={'END_DATE': 'END_DATE_1', 'DAYS': 'DAYS_1',
		// 'SPOT_YIELD': 'SPOT_YIELD_1'}, inplace=True)
		for (BootstrapResultDto result : df_result) {
			for (BootstrapResultDto tmp01 : df_tmp01) {
				if (result.getEnd_date() != null && result.getEnd_date().equals(tmp01.getEnd_date())) {
					result.setEnd_date1(result.getEnd_date());
					result.setDsys1(tmp01.getDsys());
					result.setSpot_yield1(tmp01.getSpot_yield());
					result.setEnd_date(null);
					break;
				}
			}
		}
		// python转换
		// df_tmp04 = pd.merge(df_tmp02, df_tmp03, left_on=['MARK'],
		// right_on=['MARK'], how='inner')
		// df_tmp04 = df_tmp04[df_tmp04.DATE <= df_tmp04.END_DATE]
		// df_tmp04 = df_tmp04.groupby(['DATE']).min().reset_index()
		List<BootstrapResultDto> df2_tmp04 = new ArrayList<BootstrapResultDto>();
		for (int i = 0; i < df_tmp02.size(); i++) {
			for (int j = 0; j < df_tmp03.size(); j++) {
				if (df_tmp02.get(i).compareTo(df_tmp03.get(j)) <= 0) {
					BootstrapResultDto tmp04 = new BootstrapResultDto();
					tmp04.setDate(df_tmp02.get(i));
					tmp04.setEnd_date(df_tmp03.get(j));
					df2_tmp04.add(tmp04);
					break;
				}
			}
		}
		// python转换：
		// df_result = pd.merge(df_result, df_tmp04, left_on=['DATE'],
		// right_on=['DATE'], how='left')
		// df_result = df_result[['DATE', 'END_DATE_1', 'DAYS_1',
		// 'SPOT_YIELD_1', 'END_DATE']]
		for (BootstrapResultDto result : df_result) {
			for (BootstrapResultDto tmp04 : df2_tmp04) {
				if (result.getDate().compareTo(tmp04.getDate()) == 0) {
					result.setEnd_date(tmp04.getEnd_date());
					break;
				}
			}
		}
		// python转换：
		// df_result = pd.merge(df_result, df_tmp01, left_on=['END_DATE'],
		// right_on=['END_DATE'], how='left')
		// df_result.rename(columns={'END_DATE': 'END_DATE_2', 'DAYS': 'DAYS_2',
		// 'SPOT_YIELD': 'SPOT_YIELD_2'}, inplace=True)
		for (BootstrapResultDto result : df_result) {
			for (BootstrapResultDto tmp01 : df_tmp01) {
				if (result.getEnd_date().equals(tmp01.getEnd_date())) {
					result.setEnd_date2(result.getEnd_date());
					result.setDsys2(tmp01.getDsys());
					result.setSpot_yield2(tmp01.getSpot_yield());
					result.setEnd_date(null);
					break;
				}
			}
		}

		// python转换：
		for (BootstrapResultDto result : df_result) {
			// [df_result.END_DATE_1.isnull(), 'END_DATE_1'] = df_result.DATE
			if (result.getEnd_date1() == null) {
				result.setEnd_date1(result.getDate());
			}
			// [df_result.DAYS_1.isnull(), 'DAYS_1'] = 0
			// [df_result.SPOT_YIELD_1.isnull(), 'SPOT_YIELD_1'] =
			// df_result.SPOT_YIELD_2
			if (result.getSpot_yield1() == 0L) {
				result.setSpot_yield1(result.getSpot_yield2());
			}
			// [df_result.DAYS_1 == df_result.DAYS_2, 'DAYS_1'] = 0
			if (result.getDsys2() == result.getDsys1()) {
				result.setDsys1(0L);
			}
			// python转换：
			// df_result['DISTANCE_1'] = df_result.DATE - df_result.END_DATE_1
			// df_result.DISTANCE_1 = [y.days for y in df_result.DISTANCE_1]
			result.setDistance_1(DateUtil.getBetweenDayNumber(result.getEnd_date1(), result.getDate()));
			// df_result['SPOT_YIELD'] = (df_result.SPOT_YIELD_2 -
			// df_result.SPOT_YIELD_1) / (df_result.DAYS_2 - df_result.DAYS_1) *
			// df_result.DISTANCE_1 + df_result.SPOT_YIELD_1
			result.setSpot_yield((result.getSpot_yield2() - result.getSpot_yield1())
					/ (result.getDsys2() - result.getDsys1()) * result.getDistance_1() + result.getSpot_yield1());

			// df_result['DAYS'] = [(y - spot_date).days for y in
			// df_result.DATE]
			result.setDsys(DateUtil.getBetweenDayNumber(spot_date, result.getDate()));
			// df_result['DF'] = 1 / np.exp((np.array(df_result.SPOT_YIELD) *
			// np.array(df_result.DAYS) / 365).tolist())
			result.setDf(1 / Math.exp((result.getSpot_yield() * result.getDsys() / 365)));

			// df_result['SPOT_DATE'] = [date_f(y + datetime.timedelta(days=1),
			// df_calendar) for y in df_result.DATE]
			result.setSpot_date(CalendarUtil.getDateF(DateUtil.addDay(result.getDate(), 1), holidayList));
			// df_result['DATE_3M'] = [date_mf(date_add_month(date_f(y +
			// datetime.timedelta(days=1), df_calendar), 3), df_calendar) for y
			// in df_result.DATE]
			result.setDate_3m(CalendarUtil.getDateFm(DateUtil.addMonthDate(result.getSpot_date(), 3), holidayList));

		}
		// python转换：
		// df_tmp01 = df_result[['DATE', 'DF']]
		// df_tmp01.columns = ['SPOT_DATE', 'DF_SPOT']
		// df_tmp01 = df_result[['DATE', 'DF']]
		// df_tmp01.columns = ['DATE_3M', 'DF_3M']
		Map<String, Double> df_01 = new HashMap<>();
		for (BootstrapResultDto result : df_result) {
			df_01.put(DateUtil.format(result.getDate(), "yyyy-MM-dd"), result.getDf());
		}

		// df_result = pd.merge(df_result, df_tmp01, left_on=['SPOT_DATE'],
		// right_on=['SPOT_DATE'], how='left')
		// df_result = pd.merge(df_result, df_tmp01, left_on=['DATE_3M'],
		// right_on=['DATE_3M'], how='left')
		for (BootstrapResultDto result : df_result) {
			if (df_01.get(DateUtil.format(result.getSpot_date(), "yyyy-MM-dd")) != null) {
				result.setDf_spot(df_01.get(DateUtil.format(result.getSpot_date(), "yyyy-MM-dd")));
			}
			if (df_01.get(DateUtil.format(result.getDate_3m(), "yyyy-MM-dd")) != null) {
				result.setDf_3m(df_01.get(DateUtil.format(result.getDate_3m(), "yyyy-MM-dd")));
			}
		}

		// df_result = df_result[df_result.DF_3M.notnull()]
		Iterator<BootstrapResultDto> iterator = df_result.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getDf_3m() == 0L) {
				iterator.remove();
			}
		}

		List<BootstrapDto> rstList = new ArrayList<BootstrapDto>();
		// df_result['FORWARD_RATE'] = (df_result.DF_SPOT / df_result.DF_3M - 1)
		// / [y.days for y in (df_result.DATE_3M - df_result.SPOT_DATE)] * 360
		for (BootstrapResultDto result : df_result) {
			result.setForward_rate((result.getDf_spot() / result.getDf_3m() - 1)
					/ DateUtil.getBetweenDayNumber(result.getSpot_date(), result.getDate_3m()) * 360);
			BootstrapDto dto = new BootstrapDto();
			dto.setDate(result.getDate());
			dto.setSpot_yield(result.getSpot_yield());
			dto.setDf(result.getDf());
			dto.setForward_rate(result.getForward_rate());
			rstList.add(dto);
		}
		return rstList;
	}

	/**
	 * 牛顿插值法计算10年的利率互换即期利率曲线〖SC〗_10Y
	 *
	 *
	 */
	
	private double getNewton10y(double x0, double cashflow_87m, double cashflow_90m, double cashflow_93m,
			double cashflow_96m, double cashflow_99m, double cashflow_102m, double cashflow_105m, double cashflow_108m,
			double cashflow_111m, double cashflow_114m, double cashflow_117m, double cashflow_120m, double base_yield,
			Date shibor3m_87m_end_date, Date shibor3m_90m_end_date, Date shibor3m_93m_end_date,
			Date shibor3m_96m_end_date, Date shibor3m_99m_end_date, Date shibor3m_102m_end_date,
			Date shibor3m_105m_end_date, Date shibor3m_108m_end_date, Date shibor3m_111m_end_date,
			Date shibor3m_114m_end_date, Date shibor3m_117m_end_date, Date shibor3m_120m_end_date,
			Date shibor3m_84m_end_date, double shibor3m_87m_to_today, double shibor3m_90m_to_today,
			double shibor3m_93m_to_today, double shibor3m_96m_to_today, double shibor3m_99m_to_today,
			double shibor3m_102m_to_today, double shibor3m_105m_to_today, double shibor3m_108m_to_today,
			double shibor3m_111m_to_today, double shibor3m_114m_to_today, double shibor3m_117m_to_today,
			double shibor3m_120m_to_today, double cashflow_known) {
		// 精度
		// double precision =
		// String.valueOf(EngineContext.getInstance().getConfig().dicPrecision());
		double p0 = 1.0 * x0;
		int funcalls = 0;
		double eps = 1e-4;
		// 最大循环次数
		int totalTimes = 50;
		double atol = 1.48e-08;
		double rtol = 0.0;
		double p1 = x0 * (1 + eps);
		p1 += (p1 >= 0 ? eps : -eps);

		double q0 = cashflow_known
				+ cashflow_87m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_87m_end_date))
								/ 365 * shibor3m_87m_to_today)
				+ cashflow_90m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_90m_end_date))
								/ 365 * shibor3m_90m_to_today)
				+ cashflow_93m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_93m_end_date))
								/ 365 * shibor3m_93m_to_today)
				+ cashflow_96m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_96m_end_date))
								/ 365 * shibor3m_96m_to_today)
				+ cashflow_99m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_99m_end_date))
								/ 365 * shibor3m_99m_to_today)
				+ cashflow_102m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_102m_end_date))
								/ 365 * shibor3m_102m_to_today)
				+ cashflow_105m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_105m_end_date))
								/ 365 * shibor3m_105m_to_today)
				+ cashflow_108m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_108m_end_date))
								/ 365 * shibor3m_108m_to_today)
				+ cashflow_111m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_111m_end_date))
								/ 365 * shibor3m_111m_to_today)
				+ cashflow_114m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_114m_end_date))
								/ 365 * shibor3m_114m_to_today)
				+ cashflow_117m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_117m_end_date))
								/ 365 * shibor3m_117m_to_today)
				+ cashflow_120m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_120m_end_date))
								/ 365 * shibor3m_120m_to_today)
				- 1;
		funcalls += 1;
		double q1 = cashflow_known
				+ cashflow_87m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_87m_end_date))
								/ 365 * shibor3m_87m_to_today)
				+ cashflow_90m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_90m_end_date))
								/ 365 * shibor3m_90m_to_today)
				+ cashflow_93m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_93m_end_date))
								/ 365 * shibor3m_93m_to_today)
				+ cashflow_96m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_96m_end_date))
								/ 365 * shibor3m_96m_to_today)
				+ cashflow_99m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_99m_end_date))
								/ 365 * shibor3m_99m_to_today)
				+ cashflow_102m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_102m_end_date))
								/ 365 * shibor3m_102m_to_today)
				+ cashflow_105m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_105m_end_date))
								/ 365 * shibor3m_105m_to_today)
				+ cashflow_108m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_108m_end_date))
								/ 365 * shibor3m_108m_to_today)
				+ cashflow_111m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_111m_end_date))
								/ 365 * shibor3m_111m_to_today)
				+ cashflow_114m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_114m_end_date))
								/ 365 * shibor3m_114m_to_today)
				+ cashflow_117m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_117m_end_date))
								/ 365 * shibor3m_117m_to_today)
				+ cashflow_120m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_120m_end_date))
								/ 365 * shibor3m_120m_to_today)
				- 1;
		funcalls += 1;
		if (Math.abs(q1) < Math.abs(q0)) {
			double aa = p0;
			double bb = p1;
			p0 = p1;
			p1 = p0;
			q0 = aa;
			q1 = bb;
		}
		double p = 0L;
		for (int i = 0; i < totalTimes; i++) {
			if (Math.abs(q1) > Math.abs(q0)) {
				p = (-q0 / q1 * p1 + p0) / (1 - q0 / q1);
			} else {
				p = (-q1 / q0 * p0 + p1) / (1 - q1 / q0);
			}
//			if (Math.abs(p - p1) <= Math.max(rel_tol * Math.max(Math.abs(p), Math.abs(p1)), abs_tol)) {
//			return p;
//		}
			//absolute(a - b) <= (atol + rtol * absolute(b))
			if (Math.abs(p - p1) <= (atol + rtol * Math.abs(p1))) {
				return p;
			}
			p0 = p1;
			q0 = q1;
			p1 = p;
			q1 = cashflow_known + cashflow_87m / Math
					.exp((base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_87m_end_date))
							/ 365 * shibor3m_87m_to_today)
					+ cashflow_90m / Math.exp((base_yield
							+ p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_90m_end_date)) / 365
							* shibor3m_90m_to_today)
					+ cashflow_93m / Math.exp((base_yield
							+ p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_93m_end_date)) / 365
							* shibor3m_93m_to_today)
					+ cashflow_96m / Math.exp((base_yield
							+ p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_96m_end_date)) / 365
							* shibor3m_96m_to_today)
					+ cashflow_99m / Math.exp((base_yield
							+ p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_99m_end_date)) / 365
							* shibor3m_99m_to_today)
					+ cashflow_102m / Math.exp((base_yield
							+ p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_102m_end_date)) / 365
							* shibor3m_102m_to_today)
					+ cashflow_105m / Math.exp((base_yield
							+ p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_105m_end_date)) / 365
							* shibor3m_105m_to_today)
					+ cashflow_108m / Math.exp((base_yield
							+ p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_108m_end_date)) / 365
							* shibor3m_108m_to_today)
					+ cashflow_111m / Math.exp((base_yield
							+ p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_111m_end_date)) / 365
							* shibor3m_111m_to_today)
					+ cashflow_114m / Math.exp((base_yield
							+ p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_114m_end_date)) / 365
							* shibor3m_114m_to_today)
					+ cashflow_117m / Math.exp((base_yield
							+ p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_117m_end_date)) / 365
							* shibor3m_117m_to_today)
					+ cashflow_120m / Math.exp((base_yield
							+ p1 * DateUtil.getBetweenDayNumber(shibor3m_84m_end_date, shibor3m_120m_end_date)) / 365
							* shibor3m_120m_to_today)
					- 1;
			funcalls += 1;
		}
		return p;
	}

	/**
	 * 牛顿插值法计算7年的利率互换即期利率曲线〖SC〗_7Y
	 *
	 *
	 */
	private double getNewton7y(double x0, double cashflow_63m, double cashflow_66m, double cashflow_69m,
			double cashflow_72m, double cashflow_75m, double cashflow_78m, double cashflow_81m, double cashflow_84m,
			double base_yield, Date shibor3m_63m_end_date, Date shibor3m_66m_end_date, Date shibor3m_69m_end_date,
			Date shibor3m_72m_end_date, Date shibor3m_75m_end_date, Date shibor3m_78m_end_date,
			Date shibor3m_81m_end_date, Date shibor3m_84m_end_date, Date shibor3m_60m_end_date,
			long shibor3m_63m_to_today, long shibor3m_66m_to_today, long shibor3m_69m_to_today,
			long shibor3m_72m_to_today, long shibor3m_75m_to_today, long shibor3m_78m_to_today,
			long shibor3m_81m_to_today, long shibor3m_84m_to_today, double cashflow_known) {
		// 精度
		// double precision =
		// String.valueOf(EngineContext.getInstance().getConfig().dicPrecision());
		double p0 = 1.0 * x0;
		int funcalls = 0;
		double eps = 1e-4;
		// 最大循环次数
		int totalTimes = 50;
		double atol = 1.48e-08;
		double rtol = 0.0;
		double p1 = x0 * (1 + eps);
		p1 += (p1 >= 0 ? eps : -eps);

		double q0 = cashflow_known
				+ cashflow_63m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_63m_end_date))
								/ 365 * shibor3m_63m_to_today)
				+ cashflow_66m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_66m_end_date))
								/ 365 * shibor3m_66m_to_today)
				+ cashflow_69m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_69m_end_date))
								/ 365 * shibor3m_69m_to_today)
				+ cashflow_72m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_72m_end_date))
								/ 365 * shibor3m_72m_to_today)
				+ cashflow_75m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_75m_end_date))
								/ 365 * shibor3m_75m_to_today)
				+ cashflow_78m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_78m_end_date))
								/ 365 * shibor3m_78m_to_today)
				+ cashflow_81m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_81m_end_date))
								/ 365 * shibor3m_81m_to_today)
				+ cashflow_84m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_84m_end_date))
								/ 365 * shibor3m_84m_to_today)
				- 1;
		funcalls += 1;
		double q1 = cashflow_known
				+ cashflow_63m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_63m_end_date))
								/ 365 * shibor3m_63m_to_today)
				+ cashflow_66m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_66m_end_date))
								/ 365 * shibor3m_66m_to_today)
				+ cashflow_69m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_69m_end_date))
								/ 365 * shibor3m_69m_to_today)
				+ cashflow_72m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_72m_end_date))
								/ 365 * shibor3m_72m_to_today)
				+ cashflow_75m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_75m_end_date))
								/ 365 * shibor3m_75m_to_today)
				+ cashflow_78m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_78m_end_date))
								/ 365 * shibor3m_78m_to_today)
				+ cashflow_81m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_81m_end_date))
								/ 365 * shibor3m_81m_to_today)
				+ cashflow_84m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_84m_end_date))
								/ 365 * shibor3m_84m_to_today)
				- 1;
		funcalls += 1;
		if (Math.abs(q1) < Math.abs(q0)) {
			double aa = p0;
			double bb = p1;
			p0 = p1;
			p1 = p0;
			q0 = aa;
			q1 = bb;
		}
		double p = 0L;
		for (int i = 0; i < totalTimes; i++) {
			if (Math.abs(q1) > Math.abs(q0)) {
				p = (-q0 / q1 * p1 + p0) / (1 - q0 / q1);
			} else {
				p = (-q1 / q0 * p0 + p1) / (1 - q1 / q0);
			}
//			if (Math.abs(p - p1) <= Math.max(rel_tol * Math.max(Math.abs(p), Math.abs(p1)), abs_tol)) {
//			return p;
//		}
			//absolute(a - b) <= (atol + rtol * absolute(b))
			if (Math.abs(p - p1) <= (atol + rtol * Math.abs(p1))) {
				return p;
			}
			p0 = p1;
			q0 = q1;
			p1 = p;
			q1 = cashflow_known + cashflow_63m / Math
					.exp((base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_63m_end_date))
							/ 365 * shibor3m_63m_to_today)
					+ cashflow_66m / Math.exp((base_yield
							+ p1 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_66m_end_date)) / 365
							* shibor3m_66m_to_today)
					+ cashflow_69m / Math.exp((base_yield
							+ p1 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_69m_end_date)) / 365
							* shibor3m_69m_to_today)
					+ cashflow_72m / Math.exp((base_yield
							+ p1 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_72m_end_date)) / 365
							* shibor3m_72m_to_today)
					+ cashflow_75m / Math.exp((base_yield
							+ p1 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_75m_end_date)) / 365
							* shibor3m_75m_to_today)
					+ cashflow_78m / Math.exp((base_yield
							+ p1 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_78m_end_date)) / 365
							* shibor3m_78m_to_today)
					+ cashflow_81m / Math.exp((base_yield
							+ p1 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_81m_end_date)) / 365
							* shibor3m_81m_to_today)
					+ cashflow_84m / Math.exp((base_yield
							+ p1 * DateUtil.getBetweenDayNumber(shibor3m_60m_end_date, shibor3m_84m_end_date)) / 365
							* shibor3m_84m_to_today)
					- 1;
			funcalls += 1;
		}
		return p;
	}

	/**
	 * 牛顿插值法计算2年的利率互换即期利率曲线〖SC〗_2Y
	 *
	 */
	private double getNewton2y(double x0, double cashflow_15m, double cashflow_18m, double cashflow_21m,
			double cashflow_24m, double base_yield, Date shibor3m_15m_end_date, Date shibor3m_18m_end_date,
			Date shibor3m_21m_end_date, Date shibor3m_24m_end_date, Date shibor3m_12m_end_date,
			long shibor3m_15m_to_today, long shibor3m_18m_to_today, long shibor3m_21m_to_today,
			long shibor3m_24m_to_today, double cashflow_known) {
		// 精度
		// double precision =
		// String.valueOf(EngineContext.getInstance().getConfig().dicPrecision());
		double p0 = 1.0 * x0;
		int funcalls = 0;
		double eps = 1e-4;
		// 最大循环次数
		int totalTimes = 50;
		double atol = 1.48e-08;
		double rtol = 0.0;
		double p1 = x0 * (1 + eps);
		p1 += (p1 >= 0 ? eps : -eps);

		double q0 = cashflow_known
				+ cashflow_15m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_12m_end_date, shibor3m_15m_end_date))
								/ 365 * shibor3m_15m_to_today)
				+ cashflow_18m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_12m_end_date, shibor3m_18m_end_date))
								/ 365 * shibor3m_18m_to_today)
				+ cashflow_21m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_12m_end_date, shibor3m_21m_end_date))
								/ 365 * shibor3m_21m_to_today)
				+ cashflow_24m / Math.exp(
						(base_yield + p0 * DateUtil.getBetweenDayNumber(shibor3m_12m_end_date, shibor3m_24m_end_date))
								/ 365 * shibor3m_24m_to_today)
				- 1;
		funcalls += 1;
		double q1 = cashflow_known
				+ cashflow_15m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_12m_end_date, shibor3m_15m_end_date))
								/ 365 * shibor3m_15m_to_today)
				+ cashflow_18m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_12m_end_date, shibor3m_18m_end_date))
								/ 365 * shibor3m_18m_to_today)
				+ cashflow_21m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_12m_end_date, shibor3m_21m_end_date))
								/ 365 * shibor3m_21m_to_today)
				+ cashflow_24m / Math.exp(
						(base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_12m_end_date, shibor3m_24m_end_date))
								/ 365 * shibor3m_24m_to_today)
				- 1;
		funcalls += 1;
		if (Math.abs(q1) < Math.abs(q0)) {
			double aa = p0;
			double bb = p1;
			p0 = p1;
			p1 = p0;
			q0 = aa;
			q1 = bb;
		}
		double p = 0L;
		for (int i = 0; i < totalTimes; i++) {
			if (Math.abs(q1) > Math.abs(q0)) {
				p = (-q0 / q1 * p1 + p0) / (1 - q0 / q1);
			} else {
				p = (-q1 / q0 * p0 + p1) / (1 - q1 / q0);
			}
//			if (Math.abs(p - p1) <= Math.max(rel_tol * Math.max(Math.abs(p), Math.abs(p1)), abs_tol)) {
//				return p;
//			}
			//absolute(a - b) <= (atol + rtol * absolute(b))
			if (Math.abs(p - p1) <= (atol + rtol * Math.abs(p1))) {
				return p;
			}
			p0 = p1;
			q0 = q1;
			p1 = p;
			q1 = cashflow_known + cashflow_15m / Math
					.exp((base_yield + p1 * DateUtil.getBetweenDayNumber(shibor3m_12m_end_date, shibor3m_15m_end_date))
							/ 365 * shibor3m_15m_to_today)
					+ cashflow_18m / Math.exp((base_yield
							+ p1 * DateUtil.getBetweenDayNumber(shibor3m_12m_end_date, shibor3m_18m_end_date)) / 365
							* shibor3m_18m_to_today)
					+ cashflow_21m / Math.exp((base_yield
							+ p1 * DateUtil.getBetweenDayNumber(shibor3m_12m_end_date, shibor3m_21m_end_date)) / 365
							* shibor3m_21m_to_today)
					+ cashflow_24m / Math.exp((base_yield
							+ p1 * DateUtil.getBetweenDayNumber(shibor3m_12m_end_date, shibor3m_24m_end_date)) / 365
							* shibor3m_24m_to_today)
					- 1;
			funcalls += 1;
		}
		return p;
	}

	/**
	 * Shibor3m利率互换估值计算
	 * @param today
	 *            当前日期
	 * @param df_deal_shibor3m
	 *            交易数据
	 * @param df_market_data_shibor3m
	 *            市场数据
	 * @param df_his_shibor3m
	 *            历史利率
	 * @param holidayList
	 *            节假日信息（根据日历获取未来133月的节假日"yyyy-MM-dd"）
	 */
	public List<DfDealDto> holdingCalcShibor3m(Date today, List<DfDealDto> df_deal_shibor3m,
			Shibor3mRateDto df_market_data_shibor3m, List<DfMarketDataDto> df_his_shibor3m, List<String> holidayList) {
		// 符合条件的交易：交易日期 <= 当前日期
		List<DfDealDto> df_deal_shibor3m_new = new ArrayList<DfDealDto>();
		for (DfDealDto deal : df_deal_shibor3m) {
			if (deal.getTradeDate().compareTo(today) <= 0) {
				df_deal_shibor3m_new.add(deal);
			}
		}
		// 没有符合的交易数据则返回
		if (df_deal_shibor3m_new.size() == 0) {
			return df_deal_shibor3m_new;
		}
		// Shibor3M使用bootstrap(剥靴法)对互换曲线进行转换
		List<BootstrapDto> df_forward_rate_shibor3m = calBootstrapShibor3m(today, df_market_data_shibor3m, holidayList);

		// 补足没有现金流计划的交易的现金流计划
		fixedScheduleShibor3m(df_deal_shibor3m_new, holidayList);
		// 计算现金流_折现
		calcShibor3mCf(today, df_deal_shibor3m_new, df_his_shibor3m, df_forward_rate_shibor3m);

		// 循环交易
		for (DfDealDto deal : df_deal_shibor3m_new) {
			double yishixianGuding = 0L;
			double yishixianFudong = 0L;
			double weishixianGuding = 0L;
			double weishixianFudong = 0L;
			for (DfCashPlanDto plan : deal.getDfCashPlanList()) {
				// df_tmp03 = df_tmp03[df_tmp03.PAY_DATE < df_tmp03.终止日期]
				// df_tmp04 = df_tmp03[df_tmp03.是否已支付 == '是']
				if (plan.getPayDate().compareTo(deal.getEndDate()) < 0) {
					if (plan.isPay()) {
						// df_tmp04 = df_tmp04[['交易编号', '固定端现金流_折现',
						// '浮动端现金流_折现']]
						// df_tmp04 =
						// df_tmp04.groupby(['交易编号']).sum().reset_index()
						yishixianGuding = yishixianGuding + plan.getGudingduanXianjinliuZx();
						yishixianFudong = yishixianFudong + plan.getFudongduanXianjinliuZx();
					} else {
						// df_tmp05 = df_tmp05[['交易编号', '固定端现金流_折现',
						// '浮动端现金流_折现']]
						// df_tmp05 =
						// df_tmp05.groupby(['交易编号']).sum().reset_index()
						weishixianGuding = weishixianGuding + plan.getGudingduanXianjinliuZx();
						weishixianFudong = weishixianFudong + plan.getFudongduanXianjinliuZx();
					}
				}
			}
			// df_deal_shibor3m.已实现固定 = df_deal_shibor3m.已实现固定 *
			// df_deal_shibor3m['交易金额'] * df_deal_shibor3m['利率'] / 100 / 0.01
			yishixianGuding = yishixianGuding * deal.getTradeAmt().doubleValue() * deal.getRate().doubleValue() / 100
					/ 0.01;
			// df_deal_shibor3m.未实现固定 = df_deal_shibor3m.未实现固定 *
			// df_deal_shibor3m['交易金额'] * df_deal_shibor3m['利率'] / 100 / 0.01
			weishixianGuding = weishixianGuding * deal.getTradeAmt().doubleValue() * deal.getRate().doubleValue() / 100
					/ 0.01;
			// df_deal_shibor3m.已实现浮动 = df_deal_shibor3m.已实现浮动 *
			// df_deal_shibor3m['交易金额'] / 100 / 0.01
			yishixianFudong = yishixianFudong * deal.getTradeAmt().doubleValue() / 100 / 0.01;
			// df_deal_shibor3m.未实现浮动 = df_deal_shibor3m.未实现浮动 *
			// df_deal_shibor3m['交易金额'] / 100 / 0.01
			weishixianFudong = weishixianFudong * deal.getTradeAmt().doubleValue() / 100 / 0.01;
			// df_deal_shibor3m.loc[df_deal_shibor3m.交易方向 == '互换支付固定', '已实现固定']
			// = -df_deal_shibor3m.已实现固定
			// df_deal_shibor3m.loc[df_deal_shibor3m.交易方向 == '互换支付固定', '未实现固定']
			// = -df_deal_shibor3m.未实现固定
			// df_deal_shibor3m.loc[df_deal_shibor3m.交易方向 == '互换支付浮动', '已实现浮动']
			// = -df_deal_shibor3m.已实现浮动
			// df_deal_shibor3m.loc[df_deal_shibor3m.交易方向 == '互换支付浮动', '未实现浮动']
			// = -df_deal_shibor3m.未实现浮动
			//if (DictCodeListExt2.互换方式_收取固定.getValue().equals(deal.getPs())) {
			if (DictCodeListExt2.互换方式_收取浮动.getValue().equals(deal.getPs())) {
				yishixianGuding = -yishixianGuding;
				weishixianGuding = -weishixianGuding;
			} else {
				yishixianFudong = -yishixianFudong;
				weishixianFudong = -weishixianFudong;
			}
			deal.setYishixianGuding( BigDecimal.valueOf(yishixianGuding));
			deal.setYishixianFudong( BigDecimal.valueOf(yishixianFudong));
			deal.setWeishixianGuding( BigDecimal.valueOf(weishixianGuding));
			deal.setWeishixianFudong( BigDecimal.valueOf(weishixianFudong));

			// df_deal_shibor3m['已实现损益'] = df_deal_shibor3m.已实现固定 +
			// df_deal_shibor3m.已实现浮动
			deal.setYishixianSunyi( BigDecimal.valueOf(yishixianGuding + yishixianFudong));
			// df_deal_shibor3m['浮动盈亏'] = df_deal_shibor3m.未实现固定 +
			// df_deal_shibor3m.未实现浮动
			deal.setFudongYinkui( BigDecimal.valueOf(weishixianGuding + weishixianFudong));

			// shiboron_date = date_f(today_date + datetime.timedelta(days=1),
			// df_calendar)
			Date shiboron_date = CalendarUtil.getDateF(DateUtil.addDay(today, 1), holidayList);
			// df_shiboron = 1 / (1 + shiboron_rate * (shiboron_date -
			// today_date).days / 360)
			double df_shiboron = 1 / (1 + df_market_data_shibor3m.getShiboron_rate().doubleValue()
					* DateUtil.getBetweenDayNumber(today, shiboron_date) / 360);
			// df_deal_shibor3m.浮动盈亏 = df_deal_shibor3m.浮动盈亏 * df_shiboron
			deal.setFudongYinkui(new BigDecimal((weishixianGuding + weishixianFudong) * df_shiboron));
		}
		return df_deal_shibor3m_new;
	}

	/**
	 * 补足没有现金流计划的交易的现金流计划
	 * 
	 * @param df_deal_shibor3m
	 * @param holidayList
	 */
	private void fixedScheduleShibor3m(List<DfDealDto> df_deal_shibor3m, List<String> holidayList) {
		for (DfDealDto deal : df_deal_shibor3m) {
			if (deal.getDfCashPlanList() == null || deal.getDfCashPlanList().size() < 1) {
				// 补足没有现金流计划的交易的现金流计划
				List<DfCashPlanDto> list = new ArrayList<DfCashPlanDto>();
				Date acc_start = deal.getVdate();
				// 起息日不等于首次起息日
				if (deal.getVdate().compareTo(deal.getFirstVdate()) != 0) {
					int count = 1;
					while (true) {
						Date pay_date = null;
						// if date_mf(date_add_month(start_date, count*3),
						// df_calendar) < date_mf(first_period_date,
						// df_calendar):
						if (CalendarUtil.getDateFm(DateUtil.addMonthDate(deal.getVdate(), count * 3), holidayList)
								.compareTo(CalendarUtil.getDateFm(deal.getFirstVdate(), holidayList)) < 0) {
							// pay_date = date_mf(date_add_month(start_date,
							// count*3), df_calendar)
							pay_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(deal.getVdate(), count * 3),
									holidayList);
							count += 1;
						} else {
							// pay_date = date_mf(first_period_date,
							// df_calendar)
							pay_date = CalendarUtil.getDateFm(deal.getFirstVdate(), holidayList);
						}
						DfCashPlanDto plan = new DfCashPlanDto();
						// 起息日
						plan.setVdate(deal.getVdate());
						// 首次起息日
						plan.setFirstVdate(deal.getFirstVdate());
						// 到期日
						plan.setMdate(deal.getMdate());
						// 利率日期RATE_DATE_LIST.append(date_p(acc_start -
						// datetime.timedelta(days=1), df_calendar))
						plan.setRateDate(CalendarUtil.getDateP(DateUtil.addDay(acc_start, -1), holidayList));
						// 区间开始日
						plan.setAccStart(acc_start);
						// 区间结束日
						plan.setAccEnd(pay_date);
						// 支付日
						plan.setPayDate(pay_date);
						list.add(plan);
						// 下一个开始日等于支付日
						acc_start = pay_date;
						// if pay_date == date_mf(first_period_date,
						// df_calendar)
						if (pay_date.compareTo(CalendarUtil.getDateFm(deal.getFirstVdate(), holidayList)) == 0) {
							break;
						}
					}

				}

				int count = 1;
				while (true) {
					Date pay_date = null;
					// if date_mf(date_add_month(first_period_date,
					// count*3), df_calendar) < date_mf(end_date,
					// df_calendar):
					if (CalendarUtil.getDateFm(DateUtil.addMonthDate(deal.getFirstVdate(), count * 3), holidayList)
							.compareTo(CalendarUtil.getDateFm(deal.getMdate(), holidayList)) < 0) {
						// pay_date =
						// date_mf(date_add_month(first_period_date,
						// count*3), df_calendar)
						pay_date = CalendarUtil.getDateFm(DateUtil.addMonthDate(deal.getFirstVdate(), count * 3),
								holidayList);
						count += 1;
					} else {
						// pay_date = date_mf(end_date, df_calendar)
						pay_date = CalendarUtil.getDateFm(deal.getMdate(), holidayList);
					}
					DfCashPlanDto plan = new DfCashPlanDto();
					// 起息日
					plan.setVdate(deal.getVdate());
					// 首次起息日
					plan.setFirstVdate(deal.getFirstVdate());
					// 到期日
					plan.setMdate(deal.getMdate());
					// 利率日期date_p(acc_start - datetime.timedelta(days=1),
					// df_calendar)
					plan.setRateDate(CalendarUtil.getDateP(DateUtil.addDay(acc_start, -1), holidayList));
					// 区间开始日
					plan.setAccStart(acc_start);
					// 区间结束日
					plan.setAccEnd(pay_date);
					// 支付日
					plan.setPayDate(pay_date);
					list.add(plan);
					// 下一个开始日等于支付日
					acc_start = pay_date;
					// if pay_date == date_mf(end_date, df_calendar)
					if (pay_date.compareTo(CalendarUtil.getDateFm(deal.getMdate(), holidayList)) == 0) {
						break;
					}
				}
				deal.setDfCashPlanList(list);
			}
		}
	}

	/**
	 * 计算现金流_折现
	 * 
	 * @param today
	 * @param df_deal_shibor3m
	 * @param df_his_shibor3m
	 * @param df_forward_rate_shibor3m
	 */
	private void calcShibor3mCf(Date today, List<DfDealDto> df_deal_shibor3m, List<DfMarketDataDto> df_his_shibor3m,
			List<BootstrapDto> df_forward_rate_shibor3m) {
		// 循环交易
		for (DfDealDto deal : df_deal_shibor3m) {
			for (DfCashPlanDto plan : deal.getDfCashPlanList()) {
				// 判断是否历史利率
				if (plan.getRateDate().compareTo(today) <= 0) {
					plan.setHisRate(true);
				} else {
					plan.setHisRate(false);
				}
				if (plan.isHisRate()) {
					// 如果是历史利率，则使用历史利率
					for (DfMarketDataDto mark : df_his_shibor3m) {
						if (mark.getDate().compareTo(plan.getRateDate()) == 0) {
							plan.setRate(mark.getRate());
							break;
						}
					}
				} else {
					// 不是历史利率的话，则用远期利率
					for (BootstrapDto boot : df_forward_rate_shibor3m) {
						if (boot.getDate().compareTo(plan.getRateDate()) == 0) {
							plan.setRate(boot.getForward_rate());
							break;
						}
					}
				}

				// 支付日 <= 当前日期
				if (today.compareTo(plan.getPayDate()) >= 0) {
					plan.setPay(true);
				} else {
					plan.setPay(false);
				}

				// 区间天数 = df_result.ACC_END - df_result.ACC_START
				plan.setAccrualDays(DateUtil.getBetweenDayNumber(plan.getAccStart(), plan.getAccEnd()));

				// df_result['固定端现金流'] = 0.01 * df_result.ACCRUAL_DAYS / 365
				plan.setGudingduanXianjinliu(0.01 * plan.getAccrualDays() / 365);
				// df_result['浮动端现金流'] = df_result.RATE * df_result.ACCRUAL_DAYS
				// / 360
				plan.setFudongduanXianjinliu(plan.getRate() * plan.getAccrualDays() / 360);
				// 取支付日当天的贴现因子
				for (BootstrapDto boot : df_forward_rate_shibor3m) {
					if (boot.getDate().compareTo(plan.getPayDate()) == 0) {
						plan.setDf(boot.getDf());
						break;
					}
				}
				// [df_result.是否已支付 == '是', '固定端现金流_折现'] = df_result.固定端现金流
				// [df_result.是否已支付 == '是', '浮动端现金流_折现'] = df_result.浮动端现金流
				// [df_result.是否已支付 == '否', '固定端现金流_折现'] = df_result.固定端现金流 *
				// df_result.DF
				// [df_result.是否已支付 == '否', '浮动端现金流_折现'] = df_result.浮动端现金流 *
				// df_result.DF
				if (plan.isPay()) {
					plan.setGudingduanXianjinliuZx(plan.getGudingduanXianjinliu());
					plan.setFudongduanXianjinliuZx(plan.getFudongduanXianjinliu());
				} else {
					plan.setGudingduanXianjinliuZx(plan.getGudingduanXianjinliu() * plan.getDf());
					plan.setFudongduanXianjinliuZx(plan.getFudongduanXianjinliu() * plan.getDf());
				}

			}
		}
	}

	/**
	 * 生成定息数据
	 * 
	 * @param today
	 * @param df_deal_shibor3m
	 * @return
	 */
	public List<DfScheduleDto> get_float_schedule(Date today, List<DfDealDto> df_deal_shibor3m) {
		// 交易日期 <= 当前日期) & (终止日期 > 当前日期)]
		List<DfDealDto> df_deal_shibor3m_new = new ArrayList<DfDealDto>();
		for (DfDealDto deal : df_deal_shibor3m) {
			if (deal.getTradeDate().compareTo(today) <= 0 && deal.getEndDate().compareTo(today) > 0) {
				// 交易方向 == '互换支付浮动'时， '交易金额' = -交易金额
				if (DictCodeListExt2.互换方式_收取浮动.getValue().equals(deal.getPs())) {
					deal.setTradeAmt(deal.getTradeAmt().negate());
				}
				df_deal_shibor3m_new.add(deal);
			}
		}
		// 没有符合的交易数据则返回
		if (df_deal_shibor3m_new.size() == 0) {
			return new ArrayList<DfScheduleDto>();
		}
		Map<String, DfScheduleDto> accDfMap = new HashMap<>();
		for (DfDealDto deal : df_deal_shibor3m_new) {
			for (DfCashPlanDto plan : deal.getDfCashPlanList()) {
				// 利率日期 > 当前日期
				if (plan.getRateDate().compareTo(today) > 0) {
					// 账号+日期为主键组合交易信息
					String key = deal.getAcctNo() + DateUtil.format(plan.getRateDate(), "yyyy-MM-dd");
					if (accDfMap.get(key) != null) {
						DfScheduleDto dto = accDfMap.get(key);
						// 累加金额信息
						dto.setTradeAmt(dto.getTradeAmt().add(deal.getTradeAmt()));
						accDfMap.put(key, dto);
					} else {
						// 组合定息数据
						DfScheduleDto dto = new DfScheduleDto();
						// 账号
						dto.setAcctNo(deal.getAcctNo());
						// 利率日期
						dto.setRateDate(plan.getRateDate());
						// 交易金额
						dto.setTradeAmt(deal.getTradeAmt());
						accDfMap.put(key, dto);
					}
				}
			}
		}
		List<DfScheduleDto> list = new ArrayList<DfScheduleDto>();
		for (String key : accDfMap.keySet()) {
			list.add(accDfMap.get(key));
		}
		return list;
	}
}
