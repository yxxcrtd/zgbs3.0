/**  
 * @Project     service-customer-report  
 * @File        PalOpenMarketDealersMonthlyService.java  
 * @Package     com.joyintech.service.report.regulatoryreporting.palopenmarketdealersmonthly.service  
 * @Version     V1.0  
 * @Date        2019年8月3日 下午3:19:43  
 * @Author      高健  
 * Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.  
 */  
    
package com.joyintech.service.report.regulatoryreporting.palopenmarketdealersmonthly.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.report.regulatoryreporting.palopenmarketdealersmonthly.dao.ChbBankMonthReportMapper;
import com.joyintech.service.report.regulatoryreporting.palopenmarketdealersmonthly.dto.ChbBankMonthReportModelDto;
import com.joyintech.service.report.regulatoryreporting.palopenmarketdealersmonthly.dto.ChbMonthReportViewModelDto;
import com.joyintech.service.report.regulatoryreporting.palopenmarketdealersmonthly.dto.RemainingMaturityDto;

/**  
 * @Description 一级交易商月报表微服务业务逻辑层  
 * @ClassName   PalOpenMarketDealersMonthlyService  
 * @Date        2019年8月3日 下午3:19:43  
 * @Author      高健  
 * Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.  
 */
@Service
public class PalOpenMarketDealersMonthlyService extends BaseService<ChbBankMonthReportModelDto>{
	
	// 日志处理
	private static final Logger logger = LoggerFactory.getLogger(PalOpenMarketDealersMonthlyService.class);
		
	// 存储属性代码和展示代码的映射关系
	private static Map<String, String> porpertyMap = new HashMap<String, String>(1 << 6);
	
	// 同业存单资产类型集合
	private static List<String> tamt22List = new ArrayList<String>(1 << 1);
	
	// 债券资产类型集合
	private static List<String> tamt23List = new ArrayList<String>(11);
	
	// 公募基金资产类型集合
	private static List<String> tamt24List = new ArrayList<String>(6);
	
	// 私募基金资产类型集合
	private static List<String> tamt25List = new ArrayList<String>(17);
	
	// 金融机构资产管理计划或受益权资产类型集合
	private static List<String> tamt262List = new ArrayList<String>(5);
	
	//  4.2 同业拆入及卖出回购
	private static List<String> tamt42List = new ArrayList<String>(2);
	
	// 列名集合
	private static List<String> cellList = new ArrayList<String>(11);
	
	// 行名集合
	private static List<String> rowList = new ArrayList<String>(10);
	
	// 额外列名集合
	private static List<String> cellListEx = new ArrayList<String>();
	
	static {
		// 2.1.1存放同业
		porpertyMap.put("amt211A", "AMT1_1_3");  
		// 2.1.2拆放同业
		porpertyMap.put("amt212A", "AMT1_3_1");
		// 2.1.3买入返售
		porpertyMap.put("amt213A", "AMT1_3_2");
		// 2.2.1投资同业存单
		porpertyMap.put("amt221A", "AMT1_2_3");
		// 2.3.1国债
		porpertyMap.put("amt231A", "AMT1_4_1");
		// 2.3.2地方政府债
		porpertyMap.put("amt232A", "AMT1_4_2");
		// 2.3.3政策性金融债
		porpertyMap.put("amt233A", "AMT1_4_4");
		// 2.3.4公司类信用债
		porpertyMap.put("amt234A", "AMT1_4_7,AMT1_4_8,AMT1_4_9");
		// 2.3.5资产支持证券
		porpertyMap.put("amt235A", "AMT1_4_10");
		// 2.3.6其他债券
		porpertyMap.put("amt236A", "AMT1_4_3,AMT1_4_5,AMT1_4_6,AMT1_4_11,AMT1_4_12");
		// 2.4.1债券基金
		porpertyMap.put("amt241A", "AMT1_13_1");
		// 2.4.2货币市场基金
		porpertyMap.put("amt242A", "AMT1_13_2");
		// 2.4.3股票基金
		porpertyMap.put("amt243A", "AMT1_13_3");
		// 2.4.4其他公募基金
		porpertyMap.put("amt244A", "AMT1_13_4,AMT1_13_5,AMT1_13_6");
		// 2.6.1购买信托产品
		porpertyMap.put("amt261A", "AMT1_15_1");
		// 2.6.2购买金融机构资产管理计划或受益权
		porpertyMap.put("amt262A", "AMT1_15_2,AMT1_15_3,AMT1_15_4,AMT1_15_5,AMT1_15_6");
		// 2.6.x自主管理
		porpertyMap.put("amt26xA", "AMT1_15_1_1,AMT1_15_2_1,AMT1_15_3_1,AMT1_15_4_1,AMT1_15_5_1,AMT1_15_6_1");
		// 2.6.y委托管理
		porpertyMap.put("amt26yA", "AMT1_15_1_2,AMT1_15_2_2,AMT1_15_3_2,AMT1_15_4_2,AMT1_15_5_2,AMT1_15_6_2");
		// 2.7委外投资——协议方式
		porpertyMap.put("amt27A", "AMT1_16_8");
		// 2.8权益类资产
		porpertyMap.put("amt28A", "AMT1_8_5");
		// 2.9新增可投资资产
		porpertyMap.put("amt29A", "AMT1_6_3");
		// 2.9.1其中：信贷资产流转项目
		porpertyMap.put("amt291A", "AMT1_6_1");
		// 3.4金融衍生品
		porpertyMap.put("amt34A", "AMT1_9_8");
		// 5.1零售非保本理财
		porpertyMap.put("amt51A", "amt51A");
		// 5.2对公非保本理财
		porpertyMap.put("amt52A", "amt52A");
		// 5.3同业非保本理财
		porpertyMap.put("amt53A", "amt53A");
		// 5.4同业拆入及卖出回购
		porpertyMap.put("amt54A", "AMT2_1_3");
		
		tamt22List.add("2501");
		tamt22List.add("2502");
		
		tamt23List.add("1101");
		tamt23List.add("1110");
		tamt23List.add("1109");
		tamt23List.add("1102");
		tamt23List.add("1111");
		tamt23List.add("1112");
		tamt23List.add("1113");
		tamt23List.add("1114");
		tamt23List.add("1115");
		tamt23List.add("1116");
		tamt23List.add("1117");
		
		tamt24List.add("1106");
		tamt24List.add("2401");
		tamt24List.add("1303");
		tamt24List.add("2403");
		tamt24List.add("2402");
		tamt24List.add("2499");
		
		tamt25List.add("2601");
		tamt25List.add("2602");
		tamt25List.add("2604");
		tamt25List.add("2605");
		tamt25List.add("2606");
		tamt25List.add("2607");
		tamt25List.add("2610");
		tamt25List.add("2611");
		tamt25List.add("2612");
		tamt25List.add("2617");
		tamt25List.add("2613");
		tamt25List.add("2614");
		tamt25List.add("2616");
		tamt25List.add("2618");
		tamt25List.add("2619");
		tamt25List.add("2620");
		tamt25List.add("2699");
		
		tamt262List.add("1702");
		tamt262List.add("1703");
		tamt262List.add("1704");
		tamt262List.add("1705");
		tamt262List.add("1706");
		
		tamt42List.add("1901");
		tamt42List.add("1902");
		
		cellList.add("tamt211");
		cellList.add("tamt212");
		cellList.add("tamt213");
		cellList.add("tamt22");
		cellList.add("tamt23");
		cellList.add("tamt24");
		cellList.add("tamt25");
		cellList.add("tamt261");
		cellList.add("tamt262");
		cellList.add("tamt42");
		cellList.add("samt014");
		
		rowList.add("A");
		rowList.add("B");
		rowList.add("C");
		rowList.add("D");
		rowList.add("E");
		rowList.add("F");
		rowList.add("G");
		rowList.add("H");
		rowList.add("I");
		rowList.add("J");
		
		cellListEx.add("tamt411");
		cellListEx.add("tamt412");
		cellListEx.add("samt16");
	}

	@Autowired
	private ChbBankMonthReportMapper chbBankMonthReportMapper;
	
	public ChbBankMonthReportModelDto queryOpenMarketDealersMonthlyData(Map<String, Object> pageParam){
		// 统计月份
		String statisticsMonth = (String) pageParam.get("statisticsMonth");
		// 月初日期
		Date datestart = new Date();
		// 月末日期
		Date dateend = new Date();
		try {
			datestart = DateUtil.parse(statisticsMonth + "-01");
			// 获取当前月最后一天
			Calendar ca = Calendar.getInstance();
			ca.setTime(datestart);
			ca.set(Calendar.DAY_OF_MONTH,
					ca.getActualMaximum(Calendar.DAY_OF_MONTH));
			dateend = ca.getTime();
		} catch (Exception e) {
			logger.error("", e);
		}
		String orgname = "一级交易商中银行类机构";
		ChbBankMonthReportModelDto resultmodel = new ChbBankMonthReportModelDto();
		resultmodel.setDate(DateUtil.format(dateend).replace("-",""));
		resultmodel.setOrgname(orgname);
		List<ChbMonthReportViewModelDto> chbMonthReportViewModelDtoList = new ArrayList<ChbMonthReportViewModelDto>();
		// 表Ⅰ数据统计
		chbMonthReportViewModelDtoList.addAll(queryAssetEndingBalance(statisticsMonth.replace("-",""), resultmodel.getDate()));
		// 表Ⅱ表外理财业务资产数据统计
		chbMonthReportViewModelDtoList.addAll(queryAssetRemainingMaturity(dateend));
		// 表Ⅱ表外理财业务负债数据统计
		chbMonthReportViewModelDtoList.addAll(queryBusinessRemainingMaturity(dateend));
		resultmodel.setAssets(chbMonthReportViewModelDtoList);
		return resultmodel;
	}
	
	
	/**  
	 * @Description 表Ⅰ数据统计 资产端 期末余额 
	 * @Author      高健  
	 * @Date        2019年8月5日 下午4:49:30  
	 * @param @param statisticsMonth
	 * @param @param dateend
	 * @param @return 参数  
	 * @return List<ChbMonthReportViewModelDto> 返回类型   
	 * @throws  
	 */  
	public List<ChbMonthReportViewModelDto> queryAssetEndingBalance(String statisticsMonth, String dateend) {
		List<ChbMonthReportViewModelDto> chbMonthReportViewModelDtoList = new ArrayList<ChbMonthReportViewModelDto>();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("termId", statisticsMonth);
		paraMap.put("dateend", dateend);
		List<ChbMonthReportViewModelDto> orgChbMonthReportViewList = chbBankMonthReportMapper.queryEndingBalance(paraMap);
		// 讲查询出的原始数据放入Map<属性代码, 期末余额>
		Map<String, BigDecimal> orgDataMap = new HashMap<String, BigDecimal>();
		for (ChbMonthReportViewModelDto chbMonthReportViewModelDto : orgChbMonthReportViewList) {
			orgDataMap.put(chbMonthReportViewModelDto.getVcode(), chbMonthReportViewModelDto.getValue());
		}
		// 按表I模板重新赋值
		for (String key : porpertyMap.keySet()) {
			ChbMonthReportViewModelDto chbMonthReportViewModelDto = new ChbMonthReportViewModelDto();
			chbMonthReportViewModelDto.setVcode(key);
			String valueList = porpertyMap.get(key);
			String[] valueArr = valueList.split(",");
			BigDecimal value = BigDecimal.ZERO;
			int length = valueArr.length;
			for (int i = 0; i < length; i ++) {
				value = value.add(orgDataMap.get(valueArr[i]) == null ? BigDecimal.ZERO : orgDataMap.get(valueArr[i]));
			}
			chbMonthReportViewModelDto.setValue(getHundredMillion(value));
			chbMonthReportViewModelDtoList.add(chbMonthReportViewModelDto);
		}
		return chbMonthReportViewModelDtoList;
	}
	
	
	/**  
	 * @Description 表Ⅱ表外理财业务资产数据统计  （包含表Ⅲ1.4拆放同业数据）
	 * @Author      高健  
	 * @Date        2019年8月6日 上午11:01:44  
	 * @param @param enddate
	 * @param @return 参数  
	 * @return List<ChbMonthReportViewModelDto> 返回类型   
	 * @throws  
	 */  
	private List<ChbMonthReportViewModelDto> queryAssetRemainingMaturity(Date enddate) {
		List<ChbMonthReportViewModelDto> chbMonthReportViewModelDtoList = new ArrayList<ChbMonthReportViewModelDto>();
		List<RemainingMaturityDto> PalOpenMarketAssetInfoList = chbBankMonthReportMapper.queryAssetRemainingMaturity(enddate);
		// 表Ⅱ表外理财业务资产数据统计 Map<单元格代码， 单元格数据>
		Map<String, BigDecimal> cellMap = new HashMap<String, BigDecimal>(1 << 8);
		// 所有字段值初始化为 0
		for (String cell : cellList) {
			for (String row : rowList) {
				cellMap.put(cell + row, BigDecimal.ZERO);
			}
		}
		for (RemainingMaturityDto remainingMaturityDto : PalOpenMarketAssetInfoList) {
			String key = null;
			// 2.1.1 存放同业
			if (StringUtil.equals("1003", remainingMaturityDto.getAttributeType())) {
				key = "tamt211" + remainingMaturityDto.geteDays();
				// 2.1.2 拆放同业
			} else if (StringUtil.equals("1107", remainingMaturityDto.getAttributeType())) {
				key = "tamt212" + remainingMaturityDto.geteDays();
				// 2.1.3 买入返售
			} else if (StringUtil.equals("1108", remainingMaturityDto.getAttributeType())) {
				key = "tamt213" + remainingMaturityDto.geteDays();
				// 2.2 同业存单
			} else if (tamt22List.contains(remainingMaturityDto.getAttributeType())) {
				key = "tamt22" + remainingMaturityDto.geteDays();
				// 2.3 债券资产
			} else if (tamt23List.contains(remainingMaturityDto.getAttributeType())) {
				key = "tamt23" + remainingMaturityDto.geteDays();
				// 2.4 公募基金
			} else if (tamt24List.contains(remainingMaturityDto.getAttributeType())) {
				key = "tamt24" + remainingMaturityDto.geteDays();
				// 2.5 私募基金
			} else if (tamt25List.contains(remainingMaturityDto.getAttributeType())) {
				key = "tamt25" + remainingMaturityDto.geteDays();
				// 2.6.1 信托产品
			} else if (StringUtil.equals("1701", remainingMaturityDto.getAttributeType())) {
				key = "tamt261" + remainingMaturityDto.geteDays();
				// 2.6.2 金融机构资产管理计划或受益权
			} else if (tamt262List.contains(remainingMaturityDto.getAttributeType())) {
				key = "tamt262" + remainingMaturityDto.geteDays();
				// 4.2 同业拆入及卖出回购
			} else if (tamt42List.contains(remainingMaturityDto.getAttributeType())) {
				key = "tamt42" + remainingMaturityDto.geteDays();
			}
			if (StringUtil.isNotEmpty(key)) {
				cellMap.put(key, remainingMaturityDto.getPrinamt().add(cellMap.get(key)));
			}
			// 表Ⅲ1.4拆放同业数据单独处理
			if (StringUtil.equals("1107", remainingMaturityDto.getAttributeType())) {
				String keyEx = "samt014" + remainingMaturityDto.geteDays();
				cellMap.put(keyEx, remainingMaturityDto.getPrinamt().add(cellMap.get(keyEx)));
			}
		}
		// 处理完成的数据按表Ⅱ模板重新赋值
		for (String key : cellMap.keySet()) {
			ChbMonthReportViewModelDto chbMonthReportViewModelDto = new ChbMonthReportViewModelDto();
			chbMonthReportViewModelDto.setVcode(key);
			// 表Ⅲ金额单位为 万元
			if (key.startsWith("samt")) {
				chbMonthReportViewModelDto.setValue(getMillion(cellMap.get(key)));
			} else {
				chbMonthReportViewModelDto.setValue(getHundredMillion(cellMap.get(key)));
			}
			chbMonthReportViewModelDtoList.add(chbMonthReportViewModelDto);
		}
		return chbMonthReportViewModelDtoList;
	}
	
	/**  
	 * @Description 表Ⅱ表外理财业务负债数据统计  （包含表Ⅲ16.发行的非保本理财产品数据）
	 * @Author      高健  
	 * @Date        2019年8月6日 下午2:16:48  
	 * @param @param enddate
	 * @param @return 参数  
	 * @return List<ChbMonthReportViewModelDto> 返回类型   
	 * @throws  
	 */  
	private List<ChbMonthReportViewModelDto> queryBusinessRemainingMaturity(Date enddate) {
		List<ChbMonthReportViewModelDto> chbMonthReportViewModelDtoList = new ArrayList<ChbMonthReportViewModelDto>();
		List<RemainingMaturityDto> PalOpenMarketAssetInfoList = chbBankMonthReportMapper.queryBusinessRemainingMaturity(enddate);
		// 表Ⅱ表外理财业务负债数据统计 Map<单元格代码， 单元格数据>
		Map<String, BigDecimal> cellMap = new HashMap<String, BigDecimal>(1 << 5);
		// 所有字段值初始化为 0
		for (String cell : cellListEx) {
			for (String row : rowList) {
				cellMap.put(cell + row, BigDecimal.ZERO);
			}
		}
		for (RemainingMaturityDto remainingMaturityDto : PalOpenMarketAssetInfoList) {
			String key = null;
			// 2.1.1 存放同业
			if (StringUtil.equals("02", remainingMaturityDto.getAttributeType())) {
				key = "tamt411" + remainingMaturityDto.geteDays();
				// 2.1.2 拆放同业
			} else if (StringUtil.equals("01", remainingMaturityDto.getAttributeType())) {
				key = "tamt412" + remainingMaturityDto.geteDays();
			} 
			if (StringUtil.isNotEmpty(key)) {
				cellMap.put(key, remainingMaturityDto.getPrinamt().add(cellMap.get(key)));
			}
			// // 表Ⅲ16.发行的非保本理财产品数据单独处理
			String keyEx = "samt16"+ remainingMaturityDto.geteDays();
			cellMap.put(keyEx, remainingMaturityDto.getPrinamt().add(cellMap.get(keyEx)));
		}
		// 处理好的数据按表Ⅱ模板重新赋值
		for (String key : cellMap.keySet()) {
			ChbMonthReportViewModelDto chbMonthReportViewModelDto = new ChbMonthReportViewModelDto();
			chbMonthReportViewModelDto.setVcode(key);
			// 表Ⅲ金额单位为 万元
			if (key.startsWith("samt")) {
				chbMonthReportViewModelDto.setValue(getMillion(cellMap.get(key)));
			} else {
				chbMonthReportViewModelDto.setValue(getHundredMillion(cellMap.get(key)));
			}
			chbMonthReportViewModelDtoList.add(chbMonthReportViewModelDto);
		}
		return chbMonthReportViewModelDtoList;
	}
	
	/**  
	 * @Description 将金额转化为亿元
	 * @Author      高健  
	 * @Date        2019年8月5日 下午4:53:15  
	 * @param @param tranBigDecimal 
	 * @param @return 参数  
	 * @return BigDecimal 返回类型   
	 * @throws  
	 */  
	private BigDecimal getHundredMillion(BigDecimal tranBigDecimal) {
		tranBigDecimal = tranBigDecimal.divide(new BigDecimal(100000000), 10,
				RoundingMode.HALF_UP);
		return tranBigDecimal;
	}
	
	/**  
	 * @Description 将金额转化为万元, 
	 * @Author      高健  
	 * @Date        2019年8月6日 下午4:34:51  
	 * @param @param tranBigDecimal
	 * @param @return 参数  
	 * @return BigDecimal 返回类型   
	 * @throws  
	 */  
	private BigDecimal getMillion(BigDecimal tranBigDecimal) {
		tranBigDecimal = tranBigDecimal.divide(new BigDecimal(10000), 10,
				RoundingMode.HALF_UP);
		return tranBigDecimal;
	}
}
