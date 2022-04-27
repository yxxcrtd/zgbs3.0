/*
 * 文件名：MoneyTaAutoCalProcessorProcessorService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：May 25, 2019
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.batch.investratechange.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.tams.constant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.common.base.entity.MstValIrs;
import com.joyintech.tams.valuation.batch.investratechange.dao.InvestRateChangeMapper;
import com.joyintech.tams.valuation.batch.investratechange.dto.InvestRateChangeReaderDto;
import com.joyintech.tams.valuation.batch.investratechange.dto.InvestRateChangeResult;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.common.dto.DfDealDto;
import com.joyintech.tams.valuation.common.dto.DfMarketDataDto;
import com.joyintech.tams.valuation.common.dto.Fr007RateDto;
import com.joyintech.tams.valuation.common.dto.Shibor3mRateDto;
import com.joyintech.tams.valuation.common.service.Fr007Service;
import com.joyintech.tams.valuation.common.service.Shibor3mService;

/**
 * 利率互换
 * 
 * @author taoran
 * @version 200200114
 * @see InvestRateChangeProcessorService
 * @since 1.0
 */
@Service
@Scope("prototype")
public class InvestRateChangeProcessorService {

	@Autowired
    private InvestRateChangeMapper investRateChangeMapper;
	
	@Autowired
    private Shibor3mService shibor3mService;
	
	@Autowired
    private Fr007Service fr007Service;
	
    /**
     * process 数据处理
     * @param param 传递参数
     * @since 1.0
     */
    public InvestRateChangeResult execute(Map<String, Object> param) {
    	
    	InvestRateChangeReaderDto acctEntity = (InvestRateChangeReaderDto)param.get(GlobalConstant.READER_RESULT);
    	//定义 利率互换估值集合
    	List<DfDealDto> resultList = new ArrayList<DfDealDto>();
    	//定义 SHIBOR-3M交易集合
    	List<InvestRateChangeReaderDto> shiBor3mList = new ArrayList<>();
    	//定义 FR007交易集合								
    	List<InvestRateChangeReaderDto> fr007List = new ArrayList<>();
    	
    	for(InvestRateChangeReaderDto investChange : acctEntity.getInvestRateChangeReaderList()) {
    		//（交易.基准编号 = SHIBOR-3M）									
    		if("SHIBOR3M.IR".equals(investChange.getBenchMarkId())) {
    			shiBor3mList.add(investChange);
    		}
    		//（交易.基准编号 = FR007）		
    		else if("FR007".equals(investChange.getBenchMarkId())) {
    			fr007List.add(investChange);
    		}
    	}
    	//if SHIBOR-3M交易集合 不为空
    	if(!shiBor3mList.isEmpty()) {
    		//定义 当日利率信息					
    		List<DfMarketDataDto> dfMarketDataList = new ArrayList<>();
    		//	查询SHIBOR-3M基准利率
    		Map<String, Object> intRateMap = new HashMap<String, Object>();
    		intRateMap.put("happenDate", acctEntity.getHappenDate());
    		intRateMap.put("intRateId01", "SHIBORON.IR");
    		intRateMap.put("intRateId02", "SHIBOR3M.IR");
    		List<DfMarketDataDto> intRateInfo = investRateChangeMapper.getShiborRateInfo(intRateMap);
    		if(intRateInfo != null && intRateInfo.size() == 2) {
    			dfMarketDataList.addAll(intRateInfo);
    			//	查询利率互换曲线信息YIELD_CURVE_INFO
        		Map<String, Object> yieldCurveMap = new HashMap<String, Object>();
        		yieldCurveMap.put("happenDate", acctEntity.getHappenDate());	//发生日
        		yieldCurveMap.put("baseIntCode", "SHIBOR3M.IR");	//基准利率代码
        		yieldCurveMap.put("curveIntType02", "02");		//利率互换曲线-收盘	debugger
        		yieldCurveMap.put("priceType", DictCodeListExt2.价格类型_均值.getValue());		//价格类型-均值
        		List<DfMarketDataDto> yieldCurvefo = investRateChangeMapper.getYieldCurveInfo(yieldCurveMap);
        		if(intRateInfo != null && intRateInfo.size() > 0) {
        			dfMarketDataList.addAll(yieldCurvefo);
        		}
    		}
    		//查询利率行情集合
    		Map<String, Object> mstIntRateMap = new HashMap<String, Object>();
    		mstIntRateMap.put("intRateId", "SHIBOR3M.IR");
    		//定义 利率行情集合
    		List<DfMarketDataDto> dfMarketDataInfo = investRateChangeMapper.getShibor3mRateInfo(mstIntRateMap);
    		//定义 日历信息集合 = SHIBOR-3M交易集合 group by 交易日历
    		Map<String, List<InvestRateChangeReaderDto>> shiBor3mListByCalendarId = 
    				shiBor3mList.stream().collect(Collectors.groupingBy(InvestRateChangeReaderDto :: getCalendarId));
    		//循环日历信息集合
    		for(String calendarId : shiBor3mListByCalendarId.keySet()) {
    			//定义 节假日信息				
    			List<String> holidayList = new ArrayList<>();
    			Map<String, Object> holidayMap = new HashMap<>();
    			holidayMap.put("calendarId", calendarId);		//日历编号
    			holidayMap.put("happenDate01", DateUtil.addMonthDate(acctEntity.getHappenDate(), -1));		//发生日期-1个月
    			holidayMap.put("happenDate02", DateUtil.addMonthDate(acctEntity.getHappenDate(), 133));		//发生日期+133个月
    			holidayMap.put("isHoliday", BigDecimal.ONE);		//是否节假日 1 是
    			holidayList = investRateChangeMapper.getHolidayList(holidayMap);
    			//shiBor3m交易数据
    			List<DfDealDto> shiBor3mDfDealList = getDfDealList(shiBor3mListByCalendarId.get(calendarId));
    			Shibor3mRateDto df_market_data_shibor3m = getMarketDataShibor3m(dfMarketDataList);
    			if(!dfMarketDataList.isEmpty() && dfMarketDataList.size() == 11) {
    				List<DfDealDto> tradeList = shibor3mService.holdingCalcShibor3m(
        					acctEntity.getHappenDate(), shiBor3mDfDealList, df_market_data_shibor3m, dfMarketDataInfo, holidayList);
        			resultList.addAll(tradeList);
    			}
    		}
    	}
    	//if FR007交易集合 不为空
    	if(!fr007List.isEmpty()) {
    		//定义 当日利率信息					
    		List<DfMarketDataDto> dfMarketDataList = new ArrayList<>();
    		//	查询FR007基准利率
    		Map<String, Object> intRateMap = new HashMap<String, Object>();
    		intRateMap.put("happenDate", acctEntity.getHappenDate());
    		intRateMap.put("intRateId01", "FR001");
    		intRateMap.put("intRateId02", "FR007");
    		List<DfMarketDataDto> intRateInfo = investRateChangeMapper.getShiborRateInfo(intRateMap);
    		if(intRateInfo != null && intRateInfo.size() == 2) {
    			dfMarketDataList.addAll(intRateInfo);
    			//	查询利率互换曲线信息YIELD_CURVE_INFO
        		Map<String, Object> yieldCurveMap = new HashMap<String, Object>();
        		yieldCurveMap.put("happenDate", acctEntity.getHappenDate());	//发生日
        		yieldCurveMap.put("baseIntCode", "FR007");	//基准利率代码
        		yieldCurveMap.put("curveIntType02", "02");		//利率互换曲线-收盘	debugger
        		yieldCurveMap.put("priceType", DictCodeListExt2.价格类型_均值.getValue());		//价格类型-均值
        		List<DfMarketDataDto> yieldCurvefo = investRateChangeMapper.getYieldCurveInfo(yieldCurveMap);
        		if(intRateInfo != null && intRateInfo.size() > 0) {
        			dfMarketDataList.addAll(yieldCurvefo);
        		}
    		}
    		//查询利率行情集合
    		Map<String, Object> mstIntRateMap = new HashMap<String, Object>();
    		mstIntRateMap.put("intRateId", "FR007");
    		//定义 利率行情集合
    		List<DfMarketDataDto> dfMarketDataInfo = investRateChangeMapper.getShibor3mRateInfo(mstIntRateMap);
    		//定义 日历信息集合 = SHIBOR-3M交易集合 group by 交易日历
    		Map<String, List<InvestRateChangeReaderDto>> fr007ListByCalendarId = 
    				fr007List.stream().collect(Collectors.groupingBy(InvestRateChangeReaderDto :: getCalendarId));
    		//循环日历信息集合
    		for(String calendarId : fr007ListByCalendarId.keySet()) {
    			//定义 节假日信息				
    			List<String> holidayList = new ArrayList<>();
    			Map<String, Object> holidayMap = new HashMap<>();
    			holidayMap.put("calendarId", calendarId);		//日历编号
    			holidayMap.put("happenDate01", DateUtil.addMonthDate(acctEntity.getHappenDate(), -1));		//发生日期-1个月
    			holidayMap.put("happenDate02", DateUtil.addMonthDate(acctEntity.getHappenDate(), 133));		//发生日期+133个月
    			holidayMap.put("isHoliday", BigDecimal.ONE);		//是否节假日 1 是
    			holidayList = investRateChangeMapper.getHolidayList(holidayMap);
    			//shiBor3m交易数据
    			List<DfDealDto> fr007DfDealList = getDfDealList(fr007ListByCalendarId.get(calendarId));
    			Fr007RateDto df_market_data_fr007 = getMarketDataFr007(dfMarketDataList);
    			if(!dfMarketDataList.isEmpty() && dfMarketDataList.size() == 13) {
	    			List<DfDealDto> tradeList = fr007Service.holdingCalcFr007(
	    					acctEntity.getHappenDate(), fr007DfDealList, df_market_data_fr007, dfMarketDataInfo, holidayList);
	    			resultList.addAll(tradeList);
    			}
    		}
    	}
    	List<MstValIrs> mstValIrsList = getMstValIrsList(resultList, acctEntity.getHappenDate());
    	InvestRateChangeResult investRateChangeResult = new InvestRateChangeResult();
    	investRateChangeResult.setMstValIrsList(mstValIrsList);
    	return investRateChangeResult;
    }
    
    private List<DfDealDto> getDfDealList(List<InvestRateChangeReaderDto> list){
    	List<DfDealDto> dfDealDtoList = new ArrayList<>();
    	for(InvestRateChangeReaderDto rateChange : list) {
    		DfDealDto dfDealDto = new DfDealDto();
    		dfDealDto.setTradeDate(rateChange.getTradeDate()); 	//交易日期
    		dfDealDto.setTradeAmt(rateChange.getTradeAmt()); 	//交易金额
    		dfDealDto.setVdate(rateChange.getvDate());		//起息日
    		dfDealDto.setMdate(rateChange.getmDate());		//到期日
    		dfDealDto.setTerms(rateChange.getTermType());		//期限
    		dfDealDto.setRate(rateChange.getYield());		//利率
    		dfDealDto.setFirstVdate(rateChange.getFirstvDate());	//首次起息日
    		dfDealDto.setEndDate(rateChange.getEndDate());		//终止日期
    		dfDealDto.setSettleEndDate(rateChange.getSureEndDate());	//确认终止日期
    		dfDealDto.setTradeId(rateChange.getFinprodId()); 	//交易编号
    		dfDealDto.setCcy(rateChange.getCcy());	//币种
    		dfDealDto.setPs(rateChange.getPs()); 		//交易方向
    		dfDealDto.setMdate( DictCodeList.是否_是.getValue().equals(rateChange.getIsOverTime()));		//是否到期
    		dfDealDtoList.add(dfDealDto);
    	}
    	return dfDealDtoList;
    }
    
    private Shibor3mRateDto getMarketDataShibor3m(List<DfMarketDataDto> list){
    	Shibor3mRateDto shibor3mRateDto = new Shibor3mRateDto();
    	for(DfMarketDataDto marketData : list) {
    		if("SHIBORON.IR".equals(marketData.getCode())) {
    			shibor3mRateDto.setShiboron_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    		if("SHIBOR3M.IR".equals(marketData.getCode())) {
    			shibor3mRateDto.setShibor3m_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    		if("0.5".equals(marketData.getCode())) {
    			shibor3mRateDto.setShibor3m_6m_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    		if("0.75".equals(marketData.getCode())) {
    			shibor3mRateDto.setShibor3m_9m_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    		if("1".equals(marketData.getCode())) {
    			shibor3mRateDto.setShibor3m_12m_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    		if("2".equals(marketData.getCode())) {
    			shibor3mRateDto.setShibor3m_24m_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    		if("3".equals(marketData.getCode())) {
    			shibor3mRateDto.setShibor3m_36m_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    		if("4".equals(marketData.getCode())) {
    			shibor3mRateDto.setShibor3m_48m_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    		if("5".equals(marketData.getCode())) {
    			shibor3mRateDto.setShibor3m_60m_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    		if("7".equals(marketData.getCode())) {
    			shibor3mRateDto.setShibor3m_84m_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    		if("10".equals(marketData.getCode())) {
    			shibor3mRateDto.setShibor3m_120m_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    		
    	}
    	return shibor3mRateDto;
    }
    
    private Fr007RateDto getMarketDataFr007(List<DfMarketDataDto> list){
    	Fr007RateDto fr007RateDto = new Fr007RateDto();
    	for(DfMarketDataDto marketData : list) {
    		if("FR001".equals(marketData.getCode())) {
    			fr007RateDto.setFr001_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    		else if("FR007".equals(marketData.getCode())) {
    			fr007RateDto.setFr007_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    		else if ("0.25".equals(marketData.getCode())) {
    			fr007RateDto.setFr007_3m_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    		else if("0.5".equals(marketData.getCode())) {
    			fr007RateDto.setFr007_6m_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    		else if("0.75".equals(marketData.getCode())) {
    			fr007RateDto.setFr007_9m_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    		else if("1".equals(marketData.getCode())) {
    			fr007RateDto.setFr007_12m_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    		else if("2".equals(marketData.getCode())) {
    			fr007RateDto.setFr007_24m_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    		else if("3".equals(marketData.getCode())) {
    			fr007RateDto.setFr007_36m_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    		else if("4".equals(marketData.getCode())) {
    			fr007RateDto.setFr007_48m_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    		else if("5".equals(marketData.getCode())) {
    			fr007RateDto.setFr007_60m_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    		else if("7".equals(marketData.getCode())) {
    			fr007RateDto.setFr007_84m_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    		else if("10".equals(marketData.getCode())) {
    			fr007RateDto.setFr007_120m_rate(BigDecimal.valueOf(marketData.getRate()));
    		} else {
    			fr007RateDto.setFr007_1m_rate(BigDecimal.valueOf(marketData.getRate()));
    		}
    	}
    	return fr007RateDto;
    }
    
    private List<MstValIrs> getMstValIrsList(List<DfDealDto> list, Date happenDate){
    	List<MstValIrs> mstValIrsList = new ArrayList<>();
    	for(DfDealDto dfDealDto : list) {
    		MstValIrs mstValIrs = new MstValIrs();
    		mstValIrs.setFinprodId2(dfDealDto.getTradeId());
    		mstValIrs.setDatasourceType(DictCodeListRsk.估值来源_行内.getValue());		//数据来源行内
    		mstValIrs.setValDate(happenDate);	//估值日期
    		mstValIrs.setValuation(dfDealDto.getFudongYinkui());
    		mstValIrs.setValmodelId(DictCodeListExt4.利率互换估值模型_单曲贴现.getValue());		//估值模型-单曲贴现
    		mstValIrs.setCcy(dfDealDto.getCcy());		//币种
    		mstValIrs.setInputType(DictCodeListExt1.录入方式_批处理生成.getValue());
    		mstValIrsList.add(mstValIrs);
    	}
    	return mstValIrsList;
    }
    
}
