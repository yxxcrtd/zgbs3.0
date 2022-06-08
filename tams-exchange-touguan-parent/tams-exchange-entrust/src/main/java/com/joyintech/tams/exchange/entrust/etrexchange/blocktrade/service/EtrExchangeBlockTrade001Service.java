/*
 * 文件名：EtrTradePlaceService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：meifaxin
 * 修改时间：2021年12月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.etrexchange.blocktrade.service;

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.*;
import com.joyintech.tams.business.common.dao.JoyinbusinessCommonMapper;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.common.base.dao.FinProductAddMapper;
import com.joyintech.tams.common.base.dao.FinProductConvertibleMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.FinProductAdd;
import com.joyintech.tams.component.business.common.dto.FinStockInfoDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.entrust.etrexchange.blocktrade.dto.EtrExchangeBlockTradeSaveRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.blocktrade.dto.EtrExchangeTradeQuotationResponse;
import com.joyintech.tams.exchange.entrust.etrexchange.blocktrade.dto.SelectOptionsRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dao.EtrExchangeTradeCommonMapper;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeTradeEtrExchangeCommonRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeTradeEtrExchangeCommonResponse;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeTradeSaveRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommon001Service;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommon002Service;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.MinPirceDto;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.MinTrdUnitLimitDto;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.TrdOthCodeInfoDto;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.TrdTimeConfigDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.systemtools.util.CommonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 大宗交易所指令交易（债券股票回购基金场内指令交易）
 * 
 * @author wangyang1
 * @version 1.0 2021年12月20日
 * @see EtrExchangeBlockTrade001Service
 * @since 1.0
 */
@Service
public class EtrExchangeBlockTrade001Service extends BaseService<EtrExchangeBlockTradeSaveRequest> {

	public static String queryIntegratedQuote = "/joyin/entrust/integrated/quote";

//	public static String queryIntegratedTick = "/joyin/quote/tick";

    @Autowired
    FinProductConvertibleMapper finProductConvertibleMapper;

    @Autowired
    FinProductAddMapper finProductAddMapper;

	@Autowired
	FinProductMapper finProductMapper;

	@Autowired
	IPlatformDate iPlatformDate;

	@Autowired
	EtrExchangeTradeCommonMapper etrExchangeTradeCommonMapper;

	@Autowired
	private EtrExchangeTradeCommon002Service etrExchangeTradeCommon002Service;

	@Autowired
	private EtrExchangeTradeCommon001Service etrExchangeTradeCommon001Service;

	@Autowired
	private JoyinbusinessCommonMapper joyinbusinessCommonMapper;

	@Autowired
	private FinProductAddMapper finProductAddMappers;


	/**
	 * 查询大宗交易行情信息
	 * @param secId
	 * @return
	 */
	public Page<EtrExchangeTradeQuotationResponse> findTradeQuotationsPage(String secId) {
		Page<EtrExchangeTradeQuotationResponse> page = new Page<EtrExchangeTradeQuotationResponse>();
		List<EtrExchangeTradeQuotationResponse> list = new ArrayList<EtrExchangeTradeQuotationResponse>();
		String param = "?quote_type=1&stock_code="+secId;
		boolean flag = isBuyback(secId);
		Map quoteMap = restTemplate.getForObject(ServiceApplication.EXCHANGE_SERVICE_NAME + queryIntegratedQuote + param, Map.class);
		if(quoteMap != null && quoteMap.get("data") !=null) {
			List<Map<String, Object>> quoteList = (List<Map<String, Object>>) quoteMap.get("data");
			if(!StringUtil.isEmptyList(quoteList)) {
				quoteList.forEach(tmp -> {
					EtrExchangeTradeQuotationResponse dto =new EtrExchangeTradeQuotationResponse();
					dto.setSecId((String)tmp.get("stockCode"));
					dto.setSecName((String)tmp.get("stockName"));
                    dto.setMarket((String)tmp.get("market"));
					//行情类型
					//意向申报
					if(StringUtil.equals((String)tmp.get("entrustProp"),"a")) {
						dto.setQuotationType(DictCodeListExt3.申报类型_意向申报.getValue());
					} else if(StringUtil.equals((String)tmp.get("entrustProp"),"b")) {
						//定价申报
						dto.setQuotationType(DictCodeListExt3.申报类型_定价申报.getValue());
					}
					//买卖方向
					// 买入
					if (StringUtil.equals((String) tmp.get("entrustBs"), "1")) {
						dto.setEntrustPs(DictCodeList.交易方向_买入.getValue());
						if (flag) {
							dto.setEntrustPs(DictCodeListExt3.交易方向_正回购.getValue());
						}
					} else if (StringUtil.equals((String) tmp.get("entrustBs"), "2")) {
						// 卖出
						// 定价申报
						dto.setEntrustPs(DictCodeList.交易方向_卖出.getValue());
						if (flag) {
							dto.setEntrustPs(DictCodeListExt3.交易方向_逆回购.getValue());
						}

					}
					//数量
					if((Integer)tmp.get("entrustVolume") !=null) {
						dto.setEntrustNum(new BigDecimal((Integer)tmp.get("entrustVolume")));
					}
					//价格（净价） / 回购利率
					if((Double)tmp.get("entrustPrice") !=null) {
						dto.setEntrustPrice(ArithmeticUtil.round(BigDecimal.valueOf((Double)tmp.get("entrustPrice")),4,null));
					}
					//对手方交易席位
					dto.setOppositeSeat((String)tmp.get("propSeatNo"));
					//订单编号
					dto.setAgreeNum((String)tmp.get("contractId"));
					//对手方交易员姓名
					dto.setContacts((String)tmp.get("liaisonName"));
					//对手方交易员联系方式
					dto.setContactNumber((String)tmp.get("liaisonTel"));
					list.add(dto);
				});
			}
		}
		page.setRoot(list);
		return page;
	}

	/**
	 * 查询大宗盘后行情信息
	 * @param secId
	 * @return
	 */
	public Page<EtrExchangeTradeQuotationResponse> findAfterHoursPage(String secId) {
		Page<EtrExchangeTradeQuotationResponse> page = new Page<EtrExchangeTradeQuotationResponse>();
		List<EtrExchangeTradeQuotationResponse> list = new ArrayList<EtrExchangeTradeQuotationResponse>();
//		Map<String, Object> map = BeanUtil.object2Map(request);
//        String secId= (String)map.get("secId");
		String param = "?quote_type=2&stock_code="+secId;
	    Map quoteMap = restTemplate.getForObject(ServiceApplication.EXCHANGE_SERVICE_NAME + queryIntegratedQuote + param, Map.class);
		if(quoteMap != null && quoteMap.get("data") !=null) {
			List<Map<String, Object>> quoteList = (List<Map<String, Object>>) quoteMap.get("data");
			if(!StringUtil.isEmptyList(quoteList)) {
				quoteList.forEach(tmp -> {
					EtrExchangeTradeQuotationResponse dto =new EtrExchangeTradeQuotationResponse();
					dto.setSecId((String)tmp.get("stockCode"));
					dto.setSecName((String)tmp.get("stockName"));
                    dto.setMarket((String)tmp.get("market"));
					//收盘价
					if(tmp.get("entrustPrice") !=null) {
						dto.setClosingPrice(ArithmeticUtil.round(BigDecimal.valueOf((Double)tmp.get("entrustPrice")),4,null));
					}
					//日均价
					if(tmp.get("fullPrice") !=null) {
						dto.setDailyAvgPrice(ArithmeticUtil.round(BigDecimal.valueOf((Double)tmp.get("fullPrice")),4,null));
					}
					list.add(dto);
				});
			}
		}
		page.setRoot(list);
		return page;
	}

	/**
     * 根据画面的信息计算委托金额和委托数量。<br/>
     * 如果目标类型为“数量”，则根据数量计算金额<br/>
     * 如果目标类型为“金额”，则根据数量计算数量<br/>
     * @param param
     * @return 计算结果
     * @author dubaojun
     */
    public Map<String, BigDecimal> calcEntrustNumAndAmt(Map<String, Object> param) {
        String resetFlag = param.get("resetFlag") == null ? null : param.get("resetFlag").toString();
        if (resetFlag != null) {
            Map<String, BigDecimal> resultMap = new HashMap<>();
            resultMap.put("entrustNum", null);
            resultMap.put("entrustAmt", null);
            return resultMap;
        }
        Map<String, BigDecimal> resultMap = new HashMap<>();
        BigDecimal entrustAmt = this.toBigDecimal(param.get("entrustAmt"));
        BigDecimal entrustNum = this.toBigDecimal(param.get("entrustNum"));
        String ps = param.get("ps") == null ? null : param.get("ps").toString();
        String currentTab = param.get("currentTab") == null ? null : param.get("currentTab").toString();
        BigDecimal entrustPrice = this.toBigDecimal(param.get("entrustPrice"));
        String targetType = (String) param.get("targetType");
        String finprodId = (String) param.get("finprodId");
        String finprodType2 = (String) param.get("finprodType2");
        BigDecimal availableNum = param.get("availableNum") == null ? null : new BigDecimal(param.get("availableNum").toString());
        // 判断指令价格是否为百分比
        if (entrustPrice != null && (StringUtil.equals(ps, DictCodeListExt3.交易方向_质押券入库.getValue())
                || StringUtil.equals(ps, DictCodeListExt3.交易方向_质押券出库.getValue()))) {
            entrustPrice = ArithmeticUtil.divide(entrustPrice, new BigDecimal(100), entrustPrice.scale() + 2);
        }
        // 如果是质押式回购，则默认为价格为100
        else if (StringUtil.equals(finprodType2, "ZYSHG")) {
            entrustPrice = new BigDecimal(100);
        }
        String tradeMarket = (String) param.get("tradeMarket");
        if (StringUtil.equals(targetType, DictCodeListExt3.目标类型_金额.getValue())) {
            entrustNum = ArithmeticUtil.divide(entrustAmt, entrustPrice,0,RoundingMode.DOWN);
        }
        
		if (null != param.get("minTrdUnitLimitDto")) {
			MinTrdUnitLimitDto unit = (MinTrdUnitLimitDto) param.get("minTrdUnitLimitDto");
			entrustNum = this.formatEntrustNum(entrustNum, unit);
		} else {
			BigDecimal minNum = new BigDecimal(1);
			MinTrdUnitLimitDto minTrdUnitLimitDto = getInterfSysMinTrdUnit(finprodId, currentTab, tradeMarket, finprodType2, ps);
			if (null != minTrdUnitLimitDto && null != minTrdUnitLimitDto.getMinStep()) {
				minNum = minTrdUnitLimitDto.getMinStep();
			}
			if (null != availableNum && availableNum.compareTo(BigDecimal.ZERO) > 0) {
				resultMap.put("minNum", minNum);
			}
			if (entrustNum == null || entrustNum.compareTo(BigDecimal.ZERO) == 0) {
			} else {
				if (StringUtils.equals(currentTab, "jj")
						&& (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType2)
								|| DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(finprodType2)
								|| DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(finprodType2))
						&& StringUtils.equals(ps, DictCodeList.交易方向_卖出.getValue())
						&& isSalesOfSmallShares(availableNum, minNum, entrustNum)) {
				} else {
					entrustNum = ArithmeticUtil
							.multiply(ArithmeticUtil.divide(entrustNum, minNum, 0, RoundingMode.DOWN), minNum);
				}
			}
		}
        // int rs = entrustNum.compareTo(BigDecimal.ZERO);
        // rs == -1 entrustNum小于0 例如：entrustNum = -10.00
        // rs == 0 entrustNum等于0 例如：entrustNum = 0.00
        // rs == 1 entrustNum大于0 例如：entrustNum = 10.00
        if (StringUtil.equals(targetType, DictCodeListExt3.目标类型_数量.getValue())) {
            entrustAmt = ArithmeticUtil.multiply(entrustNum, entrustPrice);
            if(StringUtils.equals(DictCodeListExt3.交易方向_质押券入库.getValue(), ps) || 
            		StringUtils.equals(DictCodeListExt3.交易方向_质押券出库.getValue(), ps)){
            	if(null != entrustAmt){//entrustAmt 代表标准券数量
            		entrustAmt = new BigDecimal(entrustAmt.intValue());
            	}
            }
        }/* else {
            if ((entrustPrice != null && entrustPrice.compareTo(BigDecimal.ZERO) != 0)
                    && (entrustNum != null && entrustNum.compareTo(BigDecimal.ZERO) != 0)) {// 价格和数量均不为0,重新计算指令金额
                entrustAmt = ArithmeticUtil.multiply(entrustNum, entrustPrice);
            }
        }*/
        
        resultMap.put("entrustNum", entrustNum);
        resultMap.put("entrustAmt", entrustAmt);
        return resultMap;
    }

    /**
     * 判断是否零股
     * @param availableNum
     * @param minNum
     * @param entrustNum
     * @return
     */
	public Boolean isSalesOfSmallShares(BigDecimal availableNum, BigDecimal minNum, BigDecimal entrustNum) {
		if (null != availableNum && availableNum.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal multiple = (availableNum.subtract(entrustNum)).divide(minNum);
			// 零股卖出 、整数倍+零股卖出 可以卖出，
			if (new BigDecimal(multiple.intValue()).compareTo(multiple) == 0
					|| (availableNum.intValue() == entrustNum.intValue() && availableNum.compareTo(minNum) <= 0)) {
				return true;
			}
		}
		return false;
	}
    
	/**
	 * 
	 * @param entrustNum 指令数量
	 * @param minTrdUnitLimitDto 最小交易单位对象
	 * @return 格式化指令数量
	 */
	private BigDecimal formatEntrustNum(BigDecimal entrustNum, MinTrdUnitLimitDto minTrdUnitLimitDto) {
		if (entrustNum == null || entrustNum.compareTo(BigDecimal.ZERO) == 0) {
			return entrustNum;
		}
		BigDecimal minNum = new BigDecimal(1);
		if (null != minTrdUnitLimitDto && null != minTrdUnitLimitDto.getMinStep()) {
			minNum = minTrdUnitLimitDto.getMinStep();
		}
		entrustNum = ArithmeticUtil.multiply(ArithmeticUtil.divide(entrustNum, minNum, 0, RoundingMode.DOWN), minNum);
		return entrustNum;
	}
    
	/**
	 * 获取最小交易单元
	 * @param finprodId 金融产品代码
	 * @param tradePlat 交易平台
	 * @param assetType 资产大类
	 * @return 获取最小交易单元
	 */
	@SuppressWarnings({ "unchecked" })
	public MinTrdUnitLimitDto getInterfSysMinTrdUnit(String finprodId, String tradePlat, String tradeMarket,
			String assetType, String ps) {
		MinTrdUnitLimitDto minTrdUnitLimitDto = null;
		tradePlat = getTradePlat(tradePlat, tradeMarket);
		String PINANAssetType = getPAAssetType(assetType);
		// 质押式回购 没有资产代码，虚拟的资产代码
		if (StringUtils.equals(PINANAssetType, DictCodeList.平安资产类型_质押式回购.getValue())) {
			finprodId = null;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("finprodId", finprodId);
		map.put("tradeMarket", tradeMarket);
		map.put("tradePlat", tradePlat);
		map.put("assetType", PINANAssetType);
		map.put("ps", ps);
		Map<String, Object> res = restTemplate.postForObject(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/getMinTrdUnitLimit", map, Map.class);
		if (res != null && res.get("data") != null) {
			List<MinTrdUnitLimitDto> list = (List<MinTrdUnitLimitDto>) res.get("data");
			if (list != null && list.size() > 0) {
				minTrdUnitLimitDto = JSON.parseObject(JSON.toJSONString(list.get(0)), MinTrdUnitLimitDto.class);
			}
		}
		return minTrdUnitLimitDto;
	}
	
	/**
	 * 
	 * @param finprodId 金融产品代码
	 * @param tradePlat 交易平台
	 * @param assetType 资产大类
	 * @return 获取最小价差
	 */
	@SuppressWarnings("unchecked")
	public MinPirceDto getInterfSysMinPrice(String finprodId, String tradePlat, String tradeMarket, String assetType, BigDecimal ...price) {
		MinPirceDto minPirceDto = null;
		tradePlat = getTradePlat(tradePlat, tradeMarket);
		String PINANAssetType = getPAAssetType(assetType);
		// 质押式回购 没有资产代码，虚拟的资产代码
		if (StringUtils.equals(PINANAssetType, DictCodeList.平安资产类型_质押式回购.getValue())) {
			finprodId = null;
		}
		Map<String, Object> map = new HashMap<>();
		map.put("finprodId", finprodId);
		map.put("tradePlat", tradePlat);
		map.put("tradeMarket", tradeMarket);
		map.put("assetType", PINANAssetType);
		if (price.length > 0 && null != price[0]) {
			map.put("currPrice", new BigDecimal(price[0].toString()));
		}
		Map<String, Object> res = restTemplate.postForObject(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/getMinPrice", map, Map.class);
		if (res != null && res.get("data") != null) {
			List<MinPirceDto> list = (List<MinPirceDto>) res.get("data");
			if (list != null && list.size() > 0) {
				minPirceDto = JSON.parseObject(JSON.toJSONString(list.get(0)), MinPirceDto.class);
			}
		}
		return minPirceDto;
	}

	/**
	 * 
	 * @param currentTab 交易平台
	 * @param tradeMarket 交易场所
	 * @return 交易平台
	 */
	private String getTradePlat(String currentTab, String tradeMarket) {
		String tradePlat = DictCodeList.交易平台_竞价撮合平台.getValue();
		if (StringUtils.isNotEmpty(currentTab)) {
			if ((currentTab.equals("dz") || currentTab.equals(DictCodeList.交易平台_大宗交易平台.getValue()))
					&& DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(tradeMarket)) {
				tradePlat = DictCodeList.交易平台_综合协议交易平台.getValue();
			} else if ((currentTab.equals("dz") || currentTab.equals(DictCodeList.交易平台_大宗交易平台.getValue()))
					&& DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(tradeMarket)) {
				tradePlat = DictCodeList.交易平台_大宗交易平台.getValue();
			} else if (currentTab.equals("gd") || currentTab.equals(DictCodeList.交易平台_固定收益平台.getValue())) {
				tradePlat = DictCodeList.交易平台_固定收益平台.getValue();
			}
		}
		return tradePlat;
	}
	

	/**
	 * 
	 * @param finprodType2
	 * @return
	 */
	private String getPAAssetType(String finprodType2) {
		String assetType = "";
		if (StringUtils.isNotEmpty(finprodType2)) {
			if (StringUtils.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
				assetType = DictCodeList.平安资产类型_债券.getValue();
			} else if (StringUtils.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_基金.getValue())) {
				assetType = DictCodeList.平安资产类型_基金.getValue();
			} else if (StringUtils.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
				assetType = DictCodeListExt2.平安资产类型_股票.getValue();
			} else if (StringUtils.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
					|| StringUtils.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())
					|| StringUtils.equals(finprodType2, "ZYSHG")) {
				assetType = DictCodeList.平安资产类型_质押式回购.getValue();
			}
		}
		return assetType;
	}

    /**
     * 转为数值类型
     * 
     * @param object Object
     * @return 数值
     */
    private BigDecimal toBigDecimal(Object object) {
        if(object == null || object == "") {
            return null;
        }
        return new BigDecimal(object.toString());
    }

	/**
	 * 获取最小交易单元
	 * @param unit
	 * @param <T>
	 * @return
	 */
	private <T extends EtrEntrust> MinTrdUnitLimitDto getMinTrdUnitLimit(T unit){
		return StringUtil.isEmptyList(Arrays.asList(unit.getFinprodId(),unit.getPs()))?null:getInterfSysMinTrdUnit(unit.getFinprodId(), unit.getTradePlat(),
				unit.getTradeMarket(), unit.getFinprodType2(), unit.getPs());
	}

    /**
     * 指令数量与金额相互反算
     * @param unit
     * @param consumer
     * @param <T>
     * @return
     */
    private <T extends EtrExchangeBlockTradeSaveRequest> Map<String, BigDecimal> calNumAndAmt(T unit, Consumer<Map> consumer){
        // 重新计算指令金额
        Map<String, Object> paramMap = BeanUtil.object2Map(unit);
		paramMap.put("currentTab", unit.getTradePlat());
        consumer.accept(paramMap);
        Map<String, BigDecimal> result = calcEntrustNumAndAmt(paramMap);
       return result;
    }

	/**
	 * check后的保存 <功能详细描述>
	 * 
	 * @param equityList
	 *            需要保存的数据
	 * @see
	 * @since 1.0
	 */
	public void dealAfterCheck(List<EtrExchangeBlockTradeSaveRequest> equityList) {
		List<EtrExchangeBlockTradeSaveRequest> listN = new ArrayList<EtrExchangeBlockTradeSaveRequest>();
		for (int i = 0; i < equityList.size(); i++) {
			EtrExchangeBlockTradeSaveRequest assetDeclarationCodeMaintenanceDto = equityList.get(i);
			EtrExchangeBlockTradeSaveRequest equityInfoCheck = null;
			if (equityInfoCheck == null) {
				listN.add(assetDeclarationCodeMaintenanceDto);
			}
		}
/*		if (listN != null && listN.size() > 0) {
			this.insertBtachEquityInfoByPart(listN);
		}
		if (listA != null && listA.size() > 0) {
			this.updateBtachEquityInfoByPart(listA);
		}*/
	}

	public boolean isBuyback(String secId) {
		boolean flag = false;
		switch (secId) {
		case "131800.SZ":
		case "131801.SZ":
		case "131802.SZ":
		case "131803.SZ":
		case "131805.SZ":
		case "131806.SZ":
		case "131809.SZ":
		case "131810.SZ":
		case "131811.SZ":
		case "204001.SH":
		case "204002.SH":
		case "204003.SH":
		case "204004.SH":
		case "204007.SH":
		case "204014.SH":
		case "204028.SH":
		case "204091.SH":
		case "204182.SH":
			flag = true;
			break;
		default:
			flag = false;
		}
		return flag;
	}

	/**
	 * 获取交易方向
	 *
	 * @param  dto 画面参数 SelectDto selectDto
	 * @return 偏离度
	 * @see
	 * @since 1.0
	 */
	public List<SelectOptionDto> getPsSelectOptions(SelectOptionsRequest dto, SelectDto selectDto) {
		List<SelectOptionDto> selectOptionDtos=new ArrayList<>();
		String tradePlat=dto.getTradePlat();
		String finprodType2=dto.getFinprodType2();
		String othCodeType=dto.getOthCodeType();
		if(StringUtil.equals(tradePlat,DictCodeList.交易平台_大宗交易平台.getValue())){
			if(StringUtil.equals(finprodType2,DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
				//债券默认买入、卖出
				selectOptionDtos = selectDto.getOptions().stream().filter(option -> {
					return StringUtil.equals(option.getId(), DictCodeList.交易方向_买入.getValue())
							|| StringUtil.equals(option.getId(), DictCodeList.交易方向_卖出.getValue()) ;
				}).collect(Collectors.toList());
				Map<String, Object> map = BeanUtil.object2Map(dto);
				Map<String, Object> returnMap=setPsExt(map) ;
				for(String key: returnMap.keySet()){
					if (StringUtil.equals(key,DictCodeList.交易方向_申购.getValue())) {
						selectOptionDtos = selectDto.getOptions().stream().filter(option -> {
							return StringUtil.equals(option.getId(), DictCodeList.交易方向_申购.getValue());
						}).collect(Collectors.toList());
						break;
					} else {
						List<SelectOptionDto> tempList = selectDto.getOptions().stream().filter(option -> {
							return StringUtil.equals(option.getId(), key);
						}).collect(Collectors.toList());
						selectOptionDtos.addAll(tempList)   ;
					}
				}
			}else if(StringUtil.equals(finprodType2,DictCodeListExt1.金融产品类型_投管用_基金.getValue())) {
				selectOptionDtos = selectDto.getOptions().stream().filter(option -> {
					return StringUtil.equals(option.getId(), DictCodeList.交易方向_买入.getValue())
							|| StringUtil.equals(option.getId(), DictCodeList.交易方向_卖出.getValue()) ;
				}).collect(Collectors.toList());

			}else if(StringUtil.equals(finprodType2,DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
				selectOptionDtos = selectDto.getOptions().stream().filter(option -> {
					return StringUtil.equals(option.getId(), DictCodeList.交易方向_买入.getValue())
							|| StringUtil.equals(option.getId(), DictCodeList.交易方向_卖出.getValue()) ;
				}).collect(Collectors.toList());
			}else if(StringUtil.equals(finprodType2,"ZYSHG")) {
				selectOptionDtos = selectDto.getOptions().stream().filter(option -> {
					return StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_正回购.getValue())
							||StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_逆回购.getValue()) ;
				}).collect(Collectors.toList());
			}else if(StringUtil.equals(othCodeType,DictCodeListExt3.金融产品其他代码类型_配股代码.getValue())) {
				selectOptionDtos = selectDto.getOptions().stream().filter(option -> {
					return StringUtil.equals(option.getId(), DictCodeListExt2.交易方向_配股.getValue())  ;
				}).collect(Collectors.toList());
			}else if(StringUtil.equals(othCodeType,DictCodeListExt3.金融产品其他代码类型_新股申购代码.getValue())) {
				selectOptionDtos = selectDto.getOptions().stream().filter(option -> {
					return StringUtil.equals(option.getId(), DictCodeList.交易方向_申购.getValue()) ;
				}).collect(Collectors.toList());
			}
		}
		return selectOptionDtos;
	}

	private  Map<String, Object> setPsExt(Map<String, Object> param){
		Map<String, Object> map = new HashMap<String, Object>();
		Date sysDate = DateUtil.platDateFromCache();
		param.put("date", DateUtil.format(sysDate));
		List<TrdOthCodeInfoDto> list = getTrdOthCodeInfos(param);
		if (null != list && list.size() > 0) {
			for (TrdOthCodeInfoDto dto : list) {
				if (!StringUtils.equals(dto.getOthCodeType(), DictCodeListExt3.金融产品其他代码类型_配股代码.getValue())
						&& !StringUtils.equals(dto.getOthCodeType(), DictCodeListExt3.金融产品其他代码类型_配债代码.getValue())) {
					// 只能选择申购，其他委托方向不可选
					String ps = OthTypeMapToPs(dto.getOthCodeType());
					if (StringUtils.equals(ps, DictCodeList.交易方向_申购.getValue())) {
						map.clear();
						map.put(ps, dto.getDeclaredPrice());
						return map;
					} else if (StringUtils.equals(ps, DictCodeListExt3.交易方向_质押券入库.getValue())) {
						BigDecimal converseRatio = null;
						try {
							converseRatio = getStdsecConverseRatio(param);
						} catch (Exception e) {
						}
						if (null != converseRatio) {
							map.put(DictCodeListExt3.交易方向_质押券入库.getValue(), converseRatio);
							map.put(DictCodeListExt3.交易方向_质押券出库.getValue(), converseRatio);
						}
					} else {
						map.put(ps, dto.getDeclaredPrice());
					}
				}
			}
		}
		return map;
	}

	/**
	 *
	 * @param map
	 *            入参
	 * @return 获取申报信息列表
	 */
	@SuppressWarnings("unchecked")
	public List<TrdOthCodeInfoDto> getTrdOthCodeInfos(Map<String, Object> map) {
		List<TrdOthCodeInfoDto> arr = new ArrayList<TrdOthCodeInfoDto>();
		Map<String, Object> res = restTemplate.postForObject(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/getTrdOthCodeInfo", map, Map.class);
		if (res != null && res.get("data") != null) {
			List<TrdOthCodeInfoDto> list = (List<TrdOthCodeInfoDto>) res.get("data");
			for (int i = 0; i < list.size(); i++) {
				TrdOthCodeInfoDto trdOthCodeInfo = JSON.parseObject(JSON.toJSONString(list.get(i)),
						TrdOthCodeInfoDto.class);
				arr.add(trdOthCodeInfo);
			}
			return arr;
		}
		return null;
	}

	/**
	 * 申报类型-》交易方向的映射
	 * @param othCodeType
	 * @return
	 */
	private String OthTypeMapToPs(String othCodeType) {
		if (StringUtils.equals(othCodeType, DictCodeListExt3.金融产品其他代码类型_可转债信用申购代码.getValue())) {
			return DictCodeList.交易方向_申购.getValue();
		}else if(StringUtils.equals(othCodeType, DictCodeListExt3.金融产品其他代码类型_回售代码.getValue())){
			return DictCodeList.交易方向_行权_回售.getValue();
		}else if(StringUtils.equals(othCodeType, DictCodeListExt3.金融产品其他代码类型_可转债转股代码.getValue())){
			return DictCodeListExt1.交易方向_债转股.getValue();
		}else if(StringUtils.equals(othCodeType, DictCodeListExt3.金融产品其他代码类型_质押代码.getValue())){
			return DictCodeListExt3.交易方向_质押券入库.getValue();
		}
		return null;
	}

	private BigDecimal getStdsecConverseRatio(Map<String, Object> param) throws Exception {
		String converseRatio = selectStdsecConverseRatio(param);
		if (StringUtils.isNotEmpty(converseRatio)) {
			// 画面上乘以100后展示
			return ArithmeticUtil.multiply(new BigDecimal(converseRatio), BigDecimal.valueOf(100));
		}
		return null;
	}

	/**
	 *
	 * @param param
	 * @return
	 */
	public String selectStdsecConverseRatio(Map<String, Object> param) {
		param.put("date", iPlatformDate.getSysDate());
		return etrExchangeTradeCommonMapper.selectStdsecConverseRatio(param);
	}

	public List<SelectOptionDto> getDeclaTypeSelectOptions(SelectOptionsRequest dto, SelectDto selectDto) {
		List<SelectOptionDto> selectOptionDtos=new ArrayList<>();
		String finprodId = dto.getFinprodId();
		String tradePlat=dto.getTradePlat();
		String finprodType2=dto.getFinprodType2();
		String othCodeType=dto.getOthCodeType();
		String tradeMarket=dto.getTradeMarket();
		if(StringUtil.equals(tradePlat,DictCodeList.交易平台_大宗交易平台.getValue())){
			if(StringUtil.equals(tradeMarket,DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
				selectOptionDtos = selectDto.getOptions().stream().filter(option ->
						      StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_意向申报.getValue()) //6
					//	    || StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_点击成交申报.getValue()) //8
							|| StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_成交申报.getValue()) //9
						    || StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_收盘价固定价格申报.getValue()) //10
							|| StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_不指定.getValue()) //5
				 ).collect(Collectors.toList());
			}
			else if(StringUtil.equals(finprodType2,DictCodeListExt1.金融产品类型_股票.getValue())
			  || StringUtil.equals(finprodType2,DictCodeListExt1.金融产品类型_基金.getValue())) {
				selectOptionDtos = selectDto.getOptions().stream().filter(option ->
						StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_日均价固定价格申报.getValue()) //11
								|| StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_收盘价固定价格申报.getValue()) //10
								|| StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_不指定.getValue()) //5
				).collect(Collectors.toList());
			}
			else if(StringUtil.equals(finprodType2,DictCodeListExt1.金融产品类型_债券.getValue())) {
				selectOptionDtos = selectDto.getOptions().stream().filter(option ->
						StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_意向申报.getValue()) //6
								|| StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_点击成交申报.getValue()) //8
								|| StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_成交申报.getValue()) //9
								|| StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_定价申报.getValue()) //7
								|| StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_收盘价固定价格申报.getValue()) //10
								|| StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_日均价固定价格申报.getValue()) //11
								|| StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_不指定.getValue()) //5
				).collect(Collectors.toList());
			}
			else if(StringUtil.equals(finprodType2,"ZYSHG")) {
				selectOptionDtos = selectDto.getOptions().stream().filter(option ->
						StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_意向申报.getValue()) //6
								|| StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_成交申报.getValue()) //9
								|| StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_点击成交申报.getValue()) //8
								|| StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_定价申报.getValue()) //7
								|| StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_收盘价固定价格申报.getValue()) //10
								|| StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_日均价固定价格申报.getValue()) //11
								|| StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_不指定.getValue()) //5
				).collect(Collectors.toList());
			}
		}
		if (StringUtil.isNotEmpty(finprodId)){
			Map<String,Object> param = new HashMap<String,Object>();
			if(iPlatformDate != null){
				param.put("sysDate", iPlatformDate.getSysDateStr());
			}
			param.put("finprodId", DictCodeListExt1.金融产品类型_投管用_股票.getValue() + "_" + finprodId);
			param.put("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
			FinStockInfoDto finStockInfoDto = etrExchangeTradeCommonMapper.selectByFinprodId(param);
			if (StringUtil.isNotEmpty(finStockInfoDto) &&
					StringUtil.equals(finStockInfoDto.getBoard(),DictCodeList.上市板_科创板.getValue())){
				selectOptionDtos = selectDto.getOptions().stream().filter(option ->
						!StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_收盘价固定价格申报.getValue()) //10
				).collect(Collectors.toList());
			}
		}
		return  selectOptionDtos;
	}

	public Page<EtrExchangeTradeEtrExchangeCommonResponse> selectEtrExchange(EtrExchangeTradeEtrExchangeCommonRequest request) {
		request.setEntrustTypeList(Arrays.asList(DictCodeListExt3.上农商_指令业务类型_大宗债券质押式回购指令.getValue(),
				                                 DictCodeListExt3.上农商_指令业务类型_大宗股票指令.getValue(),
												 DictCodeListExt3.上农商_指令业务类型_大宗基金指令.getValue(),
												 DictCodeListExt3.上农商_指令业务类型_大宗债券指令.getValue()));
	    Page<EtrExchangeTradeEtrExchangeCommonResponse> page = etrExchangeTradeCommon002Service.selectEtrExchange(request);
		return page;
	}

	/* 指令导出履历处理
	 *
	 * @param dataList 数据列表
	 * @author jiangpeng
	 * @date 2021/12/20 14:33
	 */
	public void handExportTimes(List<EtrExchangeTradeEtrExchangeCommonRequest> dataList) {
		List<CfetsOprHisInfoRequest> result = new ArrayList<>();
		dataList.forEach(item -> {
			if (StringUtil.isNotEmpty(item.getEntrustId())) {
				CfetsOprHisInfoRequest cfetsOprHisInfoRequest = new CfetsOprHisInfoRequest();
				cfetsOprHisInfoRequest.setEntrustId(item.getEntrustId());
				cfetsOprHisInfoRequest.setOperType(DictCodeListExt3.操作类型_导出.getValue());
				cfetsOprHisInfoRequest.setSeqNo(IDGenerator.generate(32));
				cfetsOprHisInfoRequest.setOperDate(DateUtil.platDateFromCache());
				cfetsOprHisInfoRequest.setOperTime(DateUtil.platDateFromCache());
				cfetsOprHisInfoRequest.setUserId(SecurityUtil.getLoginUser().getUserName());
				cfetsOprHisInfoRequest.setCreateUser(SecurityUtil.getLoginUser().getUserName());
				cfetsOprHisInfoRequest.setCreateDept(CommonUtil.getDeptCode());
				cfetsOprHisInfoRequest.setUpdateUser(SecurityUtil.getLoginUser().getUserName());
				result.add(cfetsOprHisInfoRequest);
			}
		});
		joyinbusinessCommonMapper.mergeCfetsOprHis(result);
	}


	/**
	 * 判断是否交易时间段
	 *
	 * @param dto
	 * @return true 不是交易时段，不可交易 false 可以交易
	 */
	public String isNonTradingTime(EtrExchangeTradeSaveRequest dto) {
		if (CacheUtil.getSysParam(BussinessConstant.ETR_TRADE_TIME_SWITCH).equals(DictCodeListExt1.是否_是.getValue())) {
			if (StringUtil.isNotEmpty(dto.getTradeMarket()) && StringUtil.isNotEmpty(dto.getFinprodType2())) {
				TrdTimeConfigDto trdTimeConfigDto = etrExchangeTradeCommon001Service.getTrdTimeConfig(dto.getTradePlat(), dto.getTradeMarket(),
						dto.getFinprodType2(), dto.getDeclaType());
				if (null != trdTimeConfigDto && null != trdTimeConfigDto.getNoonCloseTime()) {
					Date date = new Date();
					SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					if (null != trdTimeConfigDto.getNoonOpenTime()){
						try {
							Date noonOpenTime = sdf.parse(sd.format(date) + " " + trdTimeConfigDto.getNoonOpenTime().trim());
							if (noonOpenTime.after(new Date())) {
								return "01";
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
					try {
						Date noonCloseTime = sdf.parse(sd.format(date) + " " + trdTimeConfigDto.getNoonCloseTime().trim());
						if (noonCloseTime.before(new Date())) {
							return "02";
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return null;
	}


	public String getIsFullPrice(String finprodId){
		FinProductAdd finProductAdd = finProductAddMappers.selectByPrimaryKey(finprodId);
		if (StringUtil.isNotEmpty(finProductAdd)){
			return finProductAdd.getIsNetprice();
		}
		return null;
	}

	/**
	 * 1.6计算偏离度
	 *
	 * @param
	 * @return 偏离度
	 * @see
	 * @since 1.0
	 */
	public String calcDeviationDegree(Map<String, Object> param) {
		// 参数.资产代码(市场代码)
		String 市场代码 = (String) param.get("finprodMarketId");
		// 参数.净价
		String 净价 = (String) param.get("unitCprice");
		if (StringUtil.isBlank(市场代码) || StringUtil.isBlank(净价)) {
			return null;
		}
		String sysDate = DateUtil.format(iPlatformDate.getSysDate());
		param.put("sysDate", DateUtil.parse(sysDate));
		// 查询债券登记托管机构
		FinProduct finProduct = finProductMapper.selectByPrimaryKey((String) param.get("finprodId"));
		if (finProduct != null) {
			if (StringUtil.equals(DictCodeList.登记托管机构_中债_中央国债登记结算有限责任公司.getValue(),
					finProduct.getRegistOrg())) {
				param.put("VALUATION_SOURCE", DictCodeList.估值来源_中债登_CBC.getValue());
			}
			else if (StringUtil.equals(DictCodeList.登记托管机构_中债_上海清算所.getValue(),
					finProduct.getRegistOrg())) {
				param.put("VALUATION_SOURCE", DictCodeList.估值来源_上清所_SCH.getValue());
			}
			else if (StringUtil.equals(DictCodeList.登记托管机构_中债_中国证券登记结算有限责任公司.getValue(),
					finProduct.getRegistOrg())) {
				param.put("VALUATION_SOURCE", DictCodeList.估值来源_中证登_CSD.getValue());
			}
			else {
				param.put("VALUATION_SOURCE", DictCodeList.估值来源_中债登_CBC.getValue());
			}
		}
		else {
			param.put("VALUATION_SOURCE", DictCodeList.估值来源_中债登_CBC.getValue());
		}

		// 查询上一估值日的净价
		BigDecimal 上一估值日净价 = etrExchangeTradeCommonMapper.getLastNetPrice(param);
		if (上一估值日净价 == null || BigDecimal.ZERO.compareTo(上一估值日净价) == 0) {
			//估值行情取不到值就需要取当前标的剩余单位面值
			param.put("CASH_CLASS_TYPE_02",DictCodeList.现金流类型_中间还本.getValue());
			param.put("CASH_CLASS_TYPE_03",DictCodeList.现金流类型_到期还本.getValue());
			上一估值日净价 = etrExchangeTradeCommonMapper.getResidualFace(param);
			if (上一估值日净价 == null || BigDecimal.ZERO.compareTo(上一估值日净价) == 0) {
				return "0%";
			}
		}
		try {
			BigDecimal 所输净价 = new BigDecimal(净价);
			// 计算公式：（所输净价-上一估值净价）/上一估值净价*100%
			BigDecimal result = ArithmeticUtil.subtract(所输净价, 上一估值日净价);
			result = ArithmeticUtil.multiply(result, new BigDecimal(100));
			result = ArithmeticUtil.divide(result, 上一估值日净价, 2);
			return String.valueOf(result) + "%";
		}
		catch (Exception e) {
		}
		return null;
	}
}
