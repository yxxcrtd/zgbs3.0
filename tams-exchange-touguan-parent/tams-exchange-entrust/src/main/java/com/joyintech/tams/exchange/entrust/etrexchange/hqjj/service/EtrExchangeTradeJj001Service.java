/*
 * 文件名：EtrTradePlaceService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：meifaxin
 * 修改时间：2021年12月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.etrexchange.hqjj.service;

import com.alibaba.fastjson.JSON;
import com.joyintech.background.component.common.pack.common.security.User;
import com.joyintech.background.component.order.exchange.business.dto.*;
import com.joyintech.background.component.order.exchange.common.constant.UnitEnum;
import com.joyintech.background.component.order.exchange.common.dto.EtrBaseDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrParameterDto;
import com.joyintech.background.component.order.exchange.common.dto.EtrSinglePortfolioDto;
import com.joyintech.background.component.order.exchange.common.service.impl.EtrComponentFactory;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.FinProductAddMapper;
import com.joyintech.tams.common.base.dao.FinProductConvertibleMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.FinProductTypeMapper;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.common.base.entity.FinProductTypeKey;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.entrust.etrexchange.hqjj.dto.EtrExchangeTradeJjSaveRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.hqjj.dto.EtrExchangeTradeQuotationHqResponse;
import com.joyintech.tams.exchange.entrust.etrexchange.hqjj.dto.EtrExchangeTradeQuotationRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.hqjj.dto.EtrExchangeTradeQuotationResponse;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.EtrExchangeTradeQuotation001Dto;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.MinPirceDto;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.MinTrdUnitLimitDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.function.Consumer;

/**
 * 交易所指令交易（债券股票回购基金场内指令交易）
 * 
 * @author meifaxin
 * @version 1.0 2021年12月20日
 * @see EtrExchangeTradeJj001Service
 * @since 1.0
 */
@Service
public class EtrExchangeTradeJj001Service extends CommonExportService<EtrExchangeTradeJjSaveRequest>
		implements BaseProcessService<EtrExchangeTradeJjSaveRequest> {

	public static String queryIntegratedQuote = "/joyin/entrust/integrated/quote";
	
	public static String queryIntegratedTick = "/joyin/quote/tick";

    @Autowired
    FinProductConvertibleMapper finProductConvertibleMapper;

    @Autowired
    FinProductAddMapper finProductAddMapper;

	@Autowired
	FinProductMapper finProductMapper;
	/**
	 * 委托指令共通
	 */
	@Autowired(required = false)
	private FinProductTypeMapper finProductTypeMapper;
	/**
	 * 平台日期
	 */
	@Autowired(required = false)
	private IPlatformDate iPlatformDate;


	/**
     * 查询证券代码
     * @return
     */
	public Page<SelectOptionDto> getFinprodMarketInfo(Map<String,Object> map) {
		return restTemplate.postForPage(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/MstSecuritiesAccountInfo/getFinprodMarketInfo", map,
				SelectOptionDto.class);
	}

	/** 查询大宗交易行情信息 **/
	public Page<EtrExchangeTradeQuotationResponse> findTradeQuotationsPage(EtrExchangeTradeQuotationRequest request) {
		Map<String, Object> map = BeanUtil.object2Map(request);
		Page<EtrExchangeTradeQuotationResponse> page = new Page<EtrExchangeTradeQuotationResponse>();
		List<EtrExchangeTradeQuotationResponse> list = new ArrayList<EtrExchangeTradeQuotationResponse>();
        String secId = (String)map.get("secId");
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
						dto.setEntrustPrice(BigDecimal.valueOf((Double)tmp.get("entrustPrice")));
					}

					//对手方交易席位
					dto.setOppositeSeat((String)tmp.get("propSeatNo"));

					//订单编号
					dto.setAgreeNum((String)tmp.get("contractId"));

					//对手方交易员姓名
					dto.setContacts((String)tmp.get("liaisonName"));

					//对手方交易员联系方式
					dto.setContactNumber((String)tmp.get("liaisonTel"));

					//list.add(dto);
				});
			}
		}
		page.setRoot(list);
		return page;
	}

	/** 查询固定收益行情数据 **/
	public Page<EtrExchangeTradeQuotation001Dto> findFixedIncomePage(EtrExchangeTradeQuotationRequest request) {
        Page<EtrExchangeTradeQuotation001Dto> page = new Page<EtrExchangeTradeQuotation001Dto>();
        List<EtrExchangeTradeQuotation001Dto> list = new ArrayList<EtrExchangeTradeQuotation001Dto>();
		Map<String, Object> map = BeanUtil.object2Map(request);
		String secId= (String)map.get("secId");
        String param = "?quote_type=5&stockId="+secId;
        Map quoteMap = restTemplate.getForObject(ServiceApplication.EXCHANGE_SERVICE_NAME + queryIntegratedQuote + param, Map.class);
        if(quoteMap != null && quoteMap.get("data") !=null) {
            List<Map<String, Object>> quoteList = (List<Map<String, Object>>) quoteMap.get("data");
            if(!StringUtil.isEmptyList(quoteList)) {
                quoteList.forEach(tmp -> {
                    EtrExchangeTradeQuotation001Dto dto =new EtrExchangeTradeQuotation001Dto();
                    dto.setSecId((String)tmp.get("stockCode"));
                    dto.setSecName((String)tmp.get("stockName"));
                    dto.setMarket((String)tmp.get("market"));
                    //行情类型
                    //买卖方向
                    //买入
                    if(StringUtil.equals((String)tmp.get("entrustBs"),"1")) {
                        dto.setEntrustPs(DictCodeList.交易方向_买入.getValue());
                    } else if(StringUtil.equals((String)tmp.get("entrustBs"),"2")) {
                        //卖出
                        //定价申报
                        dto.setEntrustPs(DictCodeList.交易方向_卖出.getValue());
                    }
                    //数量
                    if(tmp.get("entrustVolume") !=null) {
                        dto.setEntrustNum(new BigDecimal((Integer)tmp.get("entrustVolume")));
                    }
                    //价格（净价） / 回购利率
                    if(tmp.get("entrustPrice") !=null) {
                        dto.setnPrice(BigDecimal.valueOf((Double)tmp.get("entrustPrice")));
                    }
                    //全价
                    if(tmp.get("fullPrice") !=null) {
                        dto.setfPrice(BigDecimal.valueOf((Double)tmp.get("fullPrice")));
                    }

                    //收益率
                    if(tmp.get("expireIncomeRate") !=null) {
                        dto.setYield(BigDecimal.valueOf((Double)tmp.get("expireIncomeRate")));
                    }

                    //订单编号
                    dto.setOrderId((String)tmp.get("contractId"));

                    list.add(dto);
                });
            }
        }
        page.setRoot(list);
        return page;
	}

	/** 查询大宗盘后行情信息 **/
	public Page<EtrExchangeTradeQuotation001Dto> findAfterHoursPage(EtrExchangeTradeQuotationRequest request) {
		Page<EtrExchangeTradeQuotation001Dto> page = new Page<EtrExchangeTradeQuotation001Dto>();
		List<EtrExchangeTradeQuotation001Dto> list = new ArrayList<EtrExchangeTradeQuotation001Dto>();
		Map<String, Object> map = BeanUtil.object2Map(request);
        String secId= (String)map.get("secId");
		String param = "?quote_type=2&stock_code="+secId;
	    Map quoteMap = restTemplate.getForObject(ServiceApplication.EXCHANGE_SERVICE_NAME + queryIntegratedQuote + param, Map.class);
		if(quoteMap != null && quoteMap.get("data") !=null) {
			List<Map<String, Object>> quoteList = (List<Map<String, Object>>) quoteMap.get("data");
			if(!StringUtil.isEmptyList(quoteList)) {
				quoteList.forEach(tmp -> {
					EtrExchangeTradeQuotation001Dto dto =new EtrExchangeTradeQuotation001Dto();
					dto.setSecId((String)tmp.get("stockCode"));
					dto.setSecName((String)tmp.get("stockName"));
                    dto.setMarket((String)tmp.get("market"));
					//收盘价
					if(tmp.get("entrustPrice") !=null) {
						dto.setClosingPrice(BigDecimal.valueOf((Double)tmp.get("entrustPrice")));
					}
					//日均价
					if(tmp.get("fullPrice") !=null) {
						dto.setDailyAvgPrice(BigDecimal.valueOf((Double)tmp.get("fullPrice")));
					}
					list.add(dto);
				});
			}
		}
		page.setRoot(list);
		return page;
	}

	/** 查询竞价交易行情信息 **/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public EtrExchangeTradeQuotationHqResponse findQuotations(String secId) {
		String param = "?stock_code=" + secId;
		Map<String, Object> quoteList;
		Map quoteMap = restTemplate.getForObject(ServiceApplication.QUOTE_SERVICE_NAME + queryIntegratedTick + param, Map.class);
		if (quoteMap != null && quoteMap.get("data") != null) {
			quoteList = (Map<String, Object>) quoteMap.get("data");
			return BeanUtil.mapToObject(quoteList,EtrExchangeTradeQuotationHqResponse.class);
		}
		return new EtrExchangeTradeQuotationHqResponse();
	}



	/**
     * 根据画面的信息计算委托金额和委托数量。<br/>
     * 如果目标类型为“数量”，则根据数量计算金额<br/>
     * 如果目标类型为“金额”，则根据数量计算数量<br/>
     * 
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
    private <T extends EtrExchangeTradeJjSaveRequest> Map<String, BigDecimal> calNumAndAmt(T unit, Consumer<Map> consumer){
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
	public void dealAfterCheck(List<EtrExchangeTradeJjSaveRequest> equityList) {
		List<EtrExchangeTradeJjSaveRequest> listN = new ArrayList<EtrExchangeTradeJjSaveRequest>();
		for (int i = 0; i < equityList.size(); i++) {
			EtrExchangeTradeJjSaveRequest assetDeclarationCodeMaintenanceDto = equityList.get(i);
			EtrExchangeTradeJjSaveRequest equityInfoCheck = null;
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
	 * 保存check
	 * @param entity
	 * @return
	 */
	public BindingResult checkSave(EtrExchangeTradeJjSaveRequest entity, BindingResult result)
			throws IllegalAccessException, InvocationTargetException {

		EtrParameterDto paraDto = new EtrParameterDto();
		setUser(paraDto);
		if(null != entity.getEntrustPrice()){
			entity.setPrice(entity.getEntrustPrice());// 指令价格 或者净价
		}

		// 构造单组合单资产DTO
		EtrSinglePortfolioDto etrSinglePortfolioDto = getEtrSinglePortfolio(entity);

		//设置交易买卖默认参数
		setCheckParams(entity,paraDto);

		if (DictCodeListExt3.上农商_指令业务类型_股票竞价指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
			//股票竞价指令
			EtrStockBiddingDto etrStockBiddingDto = new EtrStockBiddingDto();
			BeanUtil.copyProperties(etrStockBiddingDto, etrSinglePortfolioDto);
			etrStockBiddingDto.setAssetType(DictCodeListExt2.平安资产类型_股票.getValue());// 资产大类

			EtrComponentFactory.getComponentService(EtrStockBiddingDto.class).updateSettings(paraDto);
			EtrComponentFactory.getComponentService(EtrStockBiddingDto.class).entrustCheck(etrStockBiddingDto, result);
		}
		if (DictCodeListExt3.上农商_指令业务类型_基金竞价指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
			//基金竞价指令
			EtrFundBiddingDto etrFundBiddingDto = new EtrFundBiddingDto();
			BeanUtil.copyProperties(etrFundBiddingDto, entity);
			etrFundBiddingDto.setAssetType(DictCodeList.平安资产类型_基金.getValue());// 资产大类

			EtrComponentFactory.getComponentService(EtrFundBiddingDto.class).updateSettings(paraDto);
			EtrComponentFactory.getComponentService(EtrFundBiddingDto.class).entrustCheck(etrFundBiddingDto, result);
		}
		if (DictCodeListExt3.上农商_指令业务类型_大宗债券质押式回购指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
			//大宗质押式回购指令
			EtrBlockTradePledgeRepoDto etrBlockTradePledgeRepoDto = new EtrBlockTradePledgeRepoDto();
			BeanUtil.copyProperties(etrBlockTradePledgeRepoDto, entity);
			etrBlockTradePledgeRepoDto.setCounterSeat(entity.getSeatId());
			etrBlockTradePledgeRepoDto.setCounterConNo(entity.getAgreeOnId());// 约定号
			etrBlockTradePledgeRepoDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());// 资产大类

			EtrComponentFactory.getComponentService(EtrBlockTradePledgeRepoDto.class).updateSettings(paraDto);
			EtrComponentFactory.getComponentService(EtrBlockTradePledgeRepoDto.class).entrustCheck(etrBlockTradePledgeRepoDto, result);
		}
		if (DictCodeListExt3.上农商_指令业务类型_大宗股票指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
			//大宗股票指令
			EtrBlockTradeStockDto etrBlockTradeStockDto = new EtrBlockTradeStockDto();
			BeanUtil.copyProperties(etrBlockTradeStockDto, entity);
			etrBlockTradeStockDto.setCounterSeat(entity.getSeatId());
			etrBlockTradeStockDto.setCounterConNo(entity.getAgreeOnId());// 约定号
			etrBlockTradeStockDto.setAssetType(DictCodeListExt2.平安资产类型_股票.getValue());// 资产大类

			EtrComponentFactory.getComponentService(EtrBlockTradeStockDto.class).updateSettings(paraDto);
			EtrComponentFactory.getComponentService(EtrBlockTradeStockDto.class).entrustCheck(etrBlockTradeStockDto,result);
		}
		if (DictCodeListExt3.上农商_指令业务类型_大宗基金指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
			//大宗基金指令
			EtrBlockTradeFundDto etrBlockTradeFundDto = new EtrBlockTradeFundDto();
			BeanUtil.copyProperties(etrBlockTradeFundDto, entity);
			etrBlockTradeFundDto.setCounterSeat(entity.getSeatId());
			etrBlockTradeFundDto.setCounterConNo(entity.getAgreeOnId());// 约定号
			etrBlockTradeFundDto.setAssetType(DictCodeList.平安资产类型_基金.getValue());// 资产大类

			EtrComponentFactory.getComponentService(EtrBlockTradeFundDto.class).updateSettings(paraDto);
			EtrComponentFactory.getComponentService(EtrBlockTradeFundDto.class).entrustCheck(etrBlockTradeFundDto,result);
		}
		if (DictCodeListExt3.上农商_指令业务类型_大宗债券指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
			//大宗债券指令
			EtrBlockTradeBondDto etrBlockTradeBondDto = new EtrBlockTradeBondDto();
			BeanUtil.copyProperties(etrBlockTradeBondDto, entity);
			etrBlockTradeBondDto.setCounterSeat(entity.getSeatId());
			etrBlockTradeBondDto.setCounterConNo(entity.getAgreeOnId());// 约定号
			etrBlockTradeBondDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类

			EtrComponentFactory.getComponentService(EtrBlockTradeBondDto.class).updateSettings(paraDto);
			EtrComponentFactory.getComponentService(EtrBlockTradeBondDto.class).entrustCheck(etrBlockTradeBondDto,result);
		}
		if (DictCodeListExt3.上农商_指令业务类型_债券竞价指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
			// 债券竞价指令
			EtrBondBiddingDto etrBondBiddingDto = new EtrBondBiddingDto();
			BeanUtil.copyPropertiesIgnoreNull(etrBondBiddingDto, etrSinglePortfolioDto);
			etrBondBiddingDto.setMatureYield(ArithmeticUtil.divide(entity.getYield(),new BigDecimal(100),8)); // 到期收益率
			etrBondBiddingDto.setFullPrice(entity.getFprice()); // 全价
			etrBondBiddingDto.setCalMode(entity.getCalculateField());// 计算方式
			etrBondBiddingDto.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管）
			etrBondBiddingDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类

			EtrComponentFactory.getComponentService(EtrBondBiddingDto.class).updateSettings(paraDto);
			EtrComponentFactory.getComponentService(EtrBondBiddingDto.class).entrustCheck(etrBondBiddingDto,result);
		}
		if (DictCodeListExt3.上农商_指令业务类型_上交所固定收益平台现券交易指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
			// 上交所固定收益平台现券交易指令
			EtrBondFxdIncmPlatSHDto etrBondFxdIncmPlatSHDto = new EtrBondFxdIncmPlatSHDto();
			BeanUtil.copyPropertiesIgnoreNull(etrBondFxdIncmPlatSHDto, etrSinglePortfolioDto);
			etrBondFxdIncmPlatSHDto.setMatureYield(ArithmeticUtil.divide(entity.getYield(),new BigDecimal(100),8)); // 到期收益率
			etrBondFxdIncmPlatSHDto.setFullPrice(entity.getFprice()); // 全价
			etrBondFxdIncmPlatSHDto.setCounterId(entity.getCounterId());// 交易对手
			etrBondFxdIncmPlatSHDto.setTrader(entity.getTrader());// 本方交易员
			etrBondFxdIncmPlatSHDto.setCounterTrader(entity.getCounterTrader());// 对手方交易员
			etrBondFxdIncmPlatSHDto.setCounterOrg(entity.getCounterOrg());// 对手方交易商
			etrBondFxdIncmPlatSHDto.setCounterConNo(entity.getAgreeOnId());// 约定号
			etrBondFxdIncmPlatSHDto.setCalMode(entity.getCalculateField());// 计算方式
			etrBondFxdIncmPlatSHDto.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管）
			etrBondFxdIncmPlatSHDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类
			etrBondFxdIncmPlatSHDto.setSettleSpreed(Long.parseLong(entity.getSettleSpeed()));//清算速度

			EtrComponentFactory.getComponentService(EtrBondFxdIncmPlatSHDto.class).updateSettings(paraDto);
			EtrComponentFactory.getComponentService(EtrBondFxdIncmPlatSHDto.class).entrustCheck(etrBondFxdIncmPlatSHDto,result);
		}
		if (DictCodeListExt3.上农商_指令业务类型_债券回售指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
			// 债券回售指令
			EtrBondRepurchaseDto etrBondRepurchaseDto = new EtrBondRepurchaseDto();
			BeanUtil.copyPropertiesIgnoreNull(etrBondRepurchaseDto, etrSinglePortfolioDto);
			etrBondRepurchaseDto.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管）
			etrBondRepurchaseDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类

			EtrComponentFactory.getComponentService(EtrBondRepurchaseDto.class).updateSettings(paraDto);
			EtrComponentFactory.getComponentService(EtrBondRepurchaseDto.class).entrustCheck(etrBondRepurchaseDto,result);
		}
		if (DictCodeListExt3.上农商_指令业务类型_配股指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
			// 配股指令
			EtrStockRightDto etrStockRightDto = new EtrStockRightDto();
			BeanUtil.copyPropertiesIgnoreNull(etrStockRightDto, etrSinglePortfolioDto);
			etrStockRightDto.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管）
			etrStockRightDto.setAssetType(DictCodeListExt2.平安资产类型_股票.getValue());// 资产大类

			EtrComponentFactory.getComponentService(EtrStockRightDto.class).updateSettings(paraDto);
			EtrComponentFactory.getComponentService(EtrStockRightDto.class).entrustCheck(etrStockRightDto,result);
		}
		if (DictCodeListExt3.上农商_指令业务类型_新股申购指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
			// 新股申购指令
			EtrStockNewDto etrStockNewDto = new EtrStockNewDto();
			BeanUtil.copyPropertiesIgnoreNull(etrStockNewDto, etrSinglePortfolioDto);
			etrStockNewDto.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管）
			etrStockNewDto.setAssetType(DictCodeListExt2.平安资产类型_股票.getValue());// 资产大类

			EtrComponentFactory.getComponentService(EtrStockNewDto.class).updateSettings(paraDto);
			EtrComponentFactory.getComponentService(EtrStockNewDto.class).entrustCheck(etrStockNewDto,result);
		}
		if (DictCodeListExt3.上农商_指令业务类型_配债指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
			// 配债指令
			EtrBondRightDto etrBondRightDto = new EtrBondRightDto();
			BeanUtil.copyPropertiesIgnoreNull(etrBondRightDto, etrSinglePortfolioDto);
			etrBondRightDto.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管）
			etrBondRightDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类

			EtrComponentFactory.getComponentService(EtrBondRightDto.class).updateSettings(paraDto);
			EtrComponentFactory.getComponentService(EtrBondRightDto.class).entrustCheck(etrBondRightDto,result);
		}
		if (DictCodeListExt3.上农商_指令业务类型_可转债申购指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
			// 可转债申购指令
			EtrConvBondApplyDto etrConvBondApplyDto = new EtrConvBondApplyDto();
			BeanUtil.copyPropertiesIgnoreNull(etrConvBondApplyDto, etrSinglePortfolioDto);
			etrConvBondApplyDto.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管）
			etrConvBondApplyDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类

			EtrComponentFactory.getComponentService(EtrConvBondApplyDto.class).updateSettings(paraDto);
			EtrComponentFactory.getComponentService(EtrConvBondApplyDto.class).entrustCheck(etrConvBondApplyDto,result);

		}
		if (DictCodeListExt3.上农商_指令业务类型_债转股指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
			// 债转股指令
			EtrBondToStockDto etrBondToStockDto = new EtrBondToStockDto();
			BeanUtil.copyPropertiesIgnoreNull(etrBondToStockDto, etrSinglePortfolioDto);
			etrBondToStockDto.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管）
			etrBondToStockDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类
			etrBondToStockDto.setExpConNum(entity.getToShareAvailableNum());// 可转股数量

			EtrComponentFactory.getComponentService(EtrBondToStockDto.class).updateSettings(paraDto);
			EtrComponentFactory.getComponentService(EtrBondToStockDto.class).entrustCheck(etrBondToStockDto,result);
		}
		if (DictCodeListExt3.上农商_指令业务类型_标准券出入库指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
			// 标准券出入库指令
			EtrStandardBondInOutDto etrStandardBondInOutDto = new EtrStandardBondInOutDto();
			BeanUtil.copyPropertiesIgnoreNull(etrStandardBondInOutDto, etrSinglePortfolioDto);
			// etrStandardBondInOutDto.setFinprodType2(dto.getFinprodType2()); //金融产品类型（投管）
			etrStandardBondInOutDto.setAssetType(DictCodeList.平安资产类型_债券.getValue());// 资产大类

			EtrComponentFactory.getComponentService(EtrStandardBondInOutDto.class).updateSettings(paraDto);
			EtrComponentFactory.getComponentService(EtrStandardBondInOutDto.class).entrustCheck(etrStandardBondInOutDto,result);
		}
		if (DictCodeListExt3.上农商_指令业务类型_债券质押式回购竞价指令.getValue().equals(etrSinglePortfolioDto.getEntrustType())) {
			// 债券质押式回购竞价指令
			EtrBondRepoBiddingDto etrBondRepoBiddingDto = new EtrBondRepoBiddingDto();
			BeanUtil.copyPropertiesIgnoreNull(etrBondRepoBiddingDto, etrSinglePortfolioDto);
			etrBondRepoBiddingDto.setFinprodType2(entity.getFinprodType2());// 金融产品类型（投管）
			etrBondRepoBiddingDto.setAssetType(DictCodeList.平安资产类型_质押式回购.getValue());// 资产大类

			EtrComponentFactory.getComponentService(EtrBondRepoBiddingDto.class).updateSettings(paraDto);
			EtrComponentFactory.getComponentService(EtrBondRepoBiddingDto.class).entrustCheck(etrBondRepoBiddingDto, result);
		}
		return result;
	}

	/**
	 * 配置用户
	 */
	public void setUser(EtrParameterDto paraDto){
		com.joyintech.background.component.common.pack.common.security.User user = new User();
		user.setUserName(LoginUserUtil.getUserName());
		user.setDeptCode(LoginUserUtil.getDeptCode());
		paraDto.setUser(user);
	}

	/**
	 * 构造单组合单资产DTO
	 * @param dto
	 * @return
	 */
	public EtrSinglePortfolioDto getEtrSinglePortfolio(EtrExchangeTradeJjSaveRequest dto) {
		//构造投资指令DTO
		EtrBaseDto baseDto = getEtrBaseDto(dto);
		// 构造单组合单资产DTO start
		EtrSinglePortfolioDto etrSinglePortfolioDto = new EtrSinglePortfolioDto();
		BeanUtil.copyPropertiesIgnoreNull(etrSinglePortfolioDto, baseDto);
		etrSinglePortfolioDto.setPortfolioId(dto.getPortfolioId());// 组合代码
		etrSinglePortfolioDto.setStockId(dto.getStockId());// 证券代码
		etrSinglePortfolioDto.setFinprodId(dto.getFinprodId());// 证券代码对应的金融产品代码
		etrSinglePortfolioDto.setSecAcctId(dto.getSecAcctId());// 证券账户
		etrSinglePortfolioDto.setDealCurAcct(dto.getAcctNo());// 资金账户
		etrSinglePortfolioDto.setPs(dto.getPs());// 委托方向
		etrSinglePortfolioDto.setPriceModel(dto.getPriceModel());// 价格模式
		etrSinglePortfolioDto.setPrice(dto.getEntrustPrice() != null ? dto.getEntrustPrice() : dto.getPrice());// 指令价格
		etrSinglePortfolioDto.setEntrustNum(dto.getEntrustNum());// 指令数量
		etrSinglePortfolioDto.setEntrustAmt(dto.getEntrustAmt());// 指令金额
		etrSinglePortfolioDto.setInvAim(dto.getInvAim());// 投资目的
		etrSinglePortfolioDto.setTradeMarket(dto.getTradeMarket());// 交易场所
		etrSinglePortfolioDto.setTradePlat(dto.getTradePlat());// 交易平台
		/*
		 * if 画面.交易平台 = 数据字典.交易平台.大宗交易平台 and 画面.交易场所 = 数据字典.交易场所.深圳证券交易所
		 * 交易平台 = 数据字典.交易平台.综合协议平台
		 * else if 画面.交易平台 = 数据字典.交易平台.大宗交易平台 and 画面.交易场所 = 数据字典.交易场所.上海证券交易所
		 * 交易平台 = 数据字典.交易平台.大宗交易平台 end
		 */
		if (DictCodeList.交易平台_大宗交易平台.getValue().equals(etrSinglePortfolioDto.getTradePlat())
				&& DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(dto.getTradeMarket())) {
			etrSinglePortfolioDto.setTradePlat(DictCodeList.交易平台_综合协议交易平台.getValue());

		} else if (DictCodeList.交易平台_大宗交易平台.getValue().equals(etrSinglePortfolioDto.getTradePlat())
				&& DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(dto.getTradeMarket())) {
			etrSinglePortfolioDto.setTradePlat(DictCodeList.交易平台_大宗交易平台.getValue());
		}

		/***********************************************************************/
		etrSinglePortfolioDto.setFinprodType2(dto.getFinprodType2());// 金融产品分类
		etrSinglePortfolioDto.setDealCurAcct(dto.getAcctNo());// 资金账户
		/***********************************************************************/
		etrSinglePortfolioDto.setDealMode(dto.getDeclaType());//申报类型-db 交易方式
		// 构造单组合单资产DTO end
		return etrSinglePortfolioDto;
	}

	/**
	 * 构造投资指令DTO
	 * @param dto
	 * @return
	 */
	public EtrBaseDto getEtrBaseDto(EtrExchangeTradeJjSaveRequest dto) {
		// 构造投资指令DTO start
		EtrBaseDto baseDto = new EtrBaseDto();
		baseDto.setMdate(dto.getMdate());// 指令起始日
		baseDto.setVdate(dto.getVdate());// 指令结束日
		baseDto.setEntrustStatus(DictCodeList.委托状态_待接收.getValue());// 指令状态
		// 指令接收日期 NULL
		// 指令关闭日期 NULL
		baseDto.setInvManagerId(dto.getInvManagerId());// 登录用户.用户ID
		baseDto.setCreateUser(dto.getCreateUser());// 创建用户
		baseDto.setCreateDept(dto.getCreateDept());// 创建部门
		baseDto.setCreateTime(dto.getCreateTime());// 创建时间
		baseDto.setUpdateUser(LoginUserUtil.getUserName());// 更新用户
		baseDto.setUpdateTime(new Date());// 更新时间
		baseDto.setEntrustClass(DictCodeListExt3.投资指令类型_单组合单资产指令.getValue());// 数据字典.指令类型.单组合单资产
		baseDto.setEntrustType(dto.getEntrustBussType());// 指令业务类型
		baseDto.setEntrustId(dto.getEntrustId());// 指令编号
		if (iPlatformDate != null) {
			baseDto.setEntrustDate(iPlatformDate.getSysDate());// 委托日期
		}
		baseDto.setTargetType(dto.getTargetType());// 目标类型
		// 构造投资指令DTO end
		return baseDto;
	}

	public void setCheckParams(EtrExchangeTradeJjSaveRequest entity, EtrParameterDto paraDto) {

		/********************************/
		setAssetTypeById(entity);

		//设置默认最小价差、最小交易单位；否则指令组件抛异常；
		paraDto.setMinDiffPrice(BigDecimal.valueOf(0.001).setScale(3,BigDecimal.ROUND_HALF_UP));
		paraDto.setMinDealUnit(new BigDecimal(1));
/*        BigDecimal minPrice = getInterfSysMinPrice(entity.getFinprodId(), entity.getTradePlat(),
        		entity.getTradeMarket(), entity.getFinprodType2());
        if(null != minPrice){
        	paraDto.setMinDiffPrice(minPrice);
        }
		BigDecimal minNumStep = getInterfSysMinTrdUnit(entity.getFinprodId(), entity.getTradePlat(),
				entity.getTradeMarket(), entity.getFinprodType2(), entity.getPs());
		if (null != minNumStep) {
			if (StringUtils.equals(entity.getTradePlat(), DictCodeList.交易平台_竞价撮合平台.getValue())
					&& DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(entity.getFinprodType2())
					|| DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(entity.getFinprodType2())
					|| DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(entity.getFinprodType2())) {
				if (null != entity.getAvailableNum()
						&& entity.getEntrustNum().compareTo(entity.getAvailableNum()) == 0) {
					paraDto.setMinDealUnit(entity.getEntrustNum());
				} else if (null != entity.getAvailableNum() && new BigDecimal(
						entity.getAvailableNum().subtract(entity.getEntrustNum()).divide(minNumStep).intValue())
								.compareTo(entity.getAvailableNum().subtract(entity.getEntrustNum())
										.divide(minNumStep)) == 0) {
					// 设置最小交易单位
					paraDto.setMinDealUnit(entity.getEntrustNum());
				} else {
					// 设置最小交易单位
					paraDto.setMinDealUnit(minNumStep);
				}
			} else {
				// 设置最小交易单位
				paraDto.setMinDealUnit(minNumStep);
			}
		}*/

		Map<String, UnitEnum> numUnit = new HashMap<String, UnitEnum>();
		if (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(entity.getFinprodType2())) {
			numUnit.put(entity.getTradeMarket(), UnitEnum.份);
		} else if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(entity.getFinprodType2())) {
			numUnit.put(entity.getTradeMarket(), UnitEnum.股);
		} else {
			numUnit.put(entity.getTradeMarket(), UnitEnum.张);
		}
		paraDto.setNumUnit(numUnit);


       /* // 设置最小价差、设置最小买卖单位、设置指令数量单位
        if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(entity.getTradeMarket())) {
            if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(entity.getFinprodType2()) || DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(entity.getFinprodType2())) {
                paraDto.setMinDiffPrice(new BigDecimal(0.001).setScale(6,BigDecimal.ROUND_HALF_UP));
                if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(entity.getFinprodType2())) {
                    if (null != entity.getAssetsType() && DictCodeList.WIND一级分类_含债券一级分类等_国债.getValue().equals(entity.getAssetsType())) {
                    	paraDto.setMinDiffPrice(new BigDecimal(0.000001).setScale(6,BigDecimal.ROUND_HALF_UP));
                    }

                    Map<String,UnitEnum> map = new HashMap<String,UnitEnum>();
                    map.put(entity.getTradeMarket(), UnitEnum.张);
                    paraDto.setNumUnit(map);
                } else {
					if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(entity.getFinprodType2())
							&& entity.getEntrustNum().compareTo(entity.getAvailableNum()) == 0) {
						paraDto.setMinDealUnit(entity.getEntrustNum());
					} else if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(entity.getFinprodType2())
							&& null != entity.getAvailableNum() && new BigDecimal(entity.getAvailableNum().subtract(entity.getEntrustNum())
									.divide(new BigDecimal(100)).intValue())
											.compareTo(entity.getAvailableNum().subtract(entity.getEntrustNum())
													.divide(new BigDecimal(100))) == 0) {
						// 不设置最小交易单位
						paraDto.setMinDealUnit(entity.getEntrustNum());
					}
					else {
						paraDto.setMinDealUnit(new BigDecimal(100));
					}
                    Map<String,UnitEnum> map = new HashMap<String,UnitEnum>();
                    map.put(entity.getTradeMarket(), UnitEnum.股);
                    paraDto.setNumUnit(map);
                }
            } else if (DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(entity.getFinprodType2())
                       || DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(entity.getFinprodType2())) {
            	paraDto.setMinDiffPrice(new BigDecimal(0.000001).setScale(6,BigDecimal.ROUND_HALF_UP));
                paraDto.setMinDealUnit(new BigDecimal(10));
                Map<String,UnitEnum> map = new HashMap<String,UnitEnum>();
                map.put(entity.getTradeMarket(), UnitEnum.张);
                paraDto.setNumUnit(map);
            } else if (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(entity.getFinprodType2())) {
            	paraDto.setMinDiffPrice(new BigDecimal(0.000001).setScale(6,BigDecimal.ROUND_HALF_UP));
            	if (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(entity.getFinprodType2())
						&& entity.getEntrustNum().compareTo(entity.getAvailableNum()) == 0) {
					paraDto.setMinDealUnit(entity.getEntrustNum());
				} else if (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(entity.getFinprodType2())
						&& null != entity.getAvailableNum() && new BigDecimal(entity.getAvailableNum().subtract(entity.getEntrustNum())
								.divide(new BigDecimal(100)).intValue())
										.compareTo(entity.getAvailableNum().subtract(entity.getEntrustNum())
												.divide(new BigDecimal(100))) == 0) {
					// 不设置最小交易单位
					paraDto.setMinDealUnit(entity.getEntrustNum());
				}
				else {
					paraDto.setMinDealUnit(new BigDecimal(100));
				}
                Map<String,UnitEnum> map = new HashMap<String,UnitEnum>();
                map.put(entity.getTradeMarket(), UnitEnum.份);
                paraDto.setNumUnit(map);
            }
        }
        if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
            if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(entity.getFinprodType2())) {
            	paraDto.setMinDiffPrice(new BigDecimal(0.000001).setScale(6,BigDecimal.ROUND_HALF_UP));
                if (StringUtils.equals(entity.getPs(), DictCodeListExt3.交易方向_配债.getValue())) {
					paraDto.setMinDealUnit(new BigDecimal(1));
				} else {
					if (null != entity.getAvailableNum() && entity.getEntrustNum().compareTo(entity.getAvailableNum()) == 0) {
						paraDto.setMinDealUnit(entity.getEntrustNum());
					} else if (null != entity.getAvailableNum() && new BigDecimal(entity.getAvailableNum().subtract(entity.getEntrustNum())
									.divide(new BigDecimal(100)).intValue())
											.compareTo(entity.getAvailableNum().subtract(entity.getEntrustNum())
													.divide(new BigDecimal(100))) == 0) {
						// 不设置最小交易单位
						paraDto.setMinDealUnit(entity.getEntrustNum());
					}
					else {
						paraDto.setMinDealUnit(new BigDecimal(10));
					}
				}
                if (null != entity.getAssetsType() && DictCodeList.WIND一级分类_含债券一级分类等_资产支持证券.getValue().equals(entity.getAssetsType())) {
                    paraDto.setMinDealUnit(new BigDecimal(10000));
                    Map<String,UnitEnum> map = new HashMap<String,UnitEnum>();
                    map.put(entity.getTradeMarket(), UnitEnum.份);
                    paraDto.setNumUnit(map);
                }
                Map<String,UnitEnum> map = new HashMap<String,UnitEnum>();
                map.put(entity.getTradeMarket(), UnitEnum.张);
                paraDto.setNumUnit(map);
            } else if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(entity.getFinprodType2())
                       || DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(entity.getFinprodType2())
                       || DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(entity.getFinprodType2())
                       || DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(entity.getFinprodType2())) {
                if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(entity.getFinprodType2())) {
                	paraDto.setMinDiffPrice(new BigDecimal(0.000001).setScale(6,BigDecimal.ROUND_HALF_UP));
                } else {
                	paraDto.setMinDiffPrice(new BigDecimal(0.000001).setScale(6,BigDecimal.ROUND_HALF_UP));
                }
                if (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(entity.getFinprodType2())) {
                    paraDto.setMinDealUnit(new BigDecimal(100));
					if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(entity.getFinprodType2())
							&& null != entity.getAvailableNum() && entity.getEntrustNum().compareTo(entity.getAvailableNum()) == 0) {
						paraDto.setMinDealUnit(entity.getEntrustNum());
					} else if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(entity.getFinprodType2())
							&& null != entity.getAvailableNum() && new BigDecimal(entity.getAvailableNum().subtract(entity.getEntrustNum())
									.divide(new BigDecimal(100)).intValue())
											.compareTo(entity.getAvailableNum().subtract(entity.getEntrustNum())
													.divide(new BigDecimal(100))) == 0) {
						// 不设置最小交易单位
						paraDto.setMinDealUnit(entity.getEntrustNum());
					}
					else {
						paraDto.setMinDealUnit(new BigDecimal(100));
					}
                    if(DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(entity.getFinprodType2())){
                        Map<String,UnitEnum> map = new HashMap<String,UnitEnum>();
                        map.put(entity.getTradeMarket(), UnitEnum.股);
                        paraDto.setNumUnit(map);
                    }else{
                        Map<String,UnitEnum> map = new HashMap<String,UnitEnum>();
                        map.put(entity.getTradeMarket(), UnitEnum.份);
                        paraDto.setNumUnit(map);
                    }
                } else {
                    paraDto.setMinDealUnit(new BigDecimal(10));
                    Map<String,UnitEnum> map = new HashMap<String,UnitEnum>();
                    map.put(entity.getTradeMarket(), UnitEnum.张);
                    paraDto.setNumUnit(map);
                }
            }
        }*/



		// 竞价交易平台 设置涨跌停
		if (DictCodeList.交易平台_竞价撮合平台.getValue().equals(entity.getTradePlat())) {
			if (null != entity.getLimitUp()) {
				paraDto.setLimitUpAmt(entity.getLimitUp());
			}
			if (null != entity.getLimitDown()) {
				paraDto.setLimitDnAmt(entity.getLimitDown());
			}
			paraDto.setMinReportUnit(new BigDecimal(1));
		}


		// 大宗交易平台
		if (DictCodeList.交易平台_大宗交易平台.getValue().equals(entity.getTradePlat())) {
			/*
			 * A股单笔交易不低于30W股，或者交易金额不低于200W人民币；
			 * 基金单笔交易数量不低于200W份，或者交易金额不低于200W人民币；
			 * 深交所债券买卖或者回购单笔不得低于5千张，或者就交易金额不低于50W人民币；上交所不低于1000手（10000张），
			 * 或者交易金额不低于100W人民币； 股票、基金交易场所均为上交所,深交所不支持
			 */
			if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(entity.getFinprodType2())) {
				paraDto.putLimitAmtNum(entity.getTradeMarket(), new BigDecimal(300000), null, UnitEnum.万股,
						new BigDecimal(2000000), null);
			}
			if (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(entity.getFinprodType2())) {
				paraDto.putLimitAmtNum(entity.getTradeMarket(), new BigDecimal(2000000), null, UnitEnum.万份,
						new BigDecimal(2000000), null);
			}
			if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(entity.getFinprodType2())
					|| DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(entity.getFinprodType2())
					|| DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(entity.getFinprodType2())) {
				if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(entity.getTradeMarket())) {
					paraDto.putLimitAmtNum(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), new BigDecimal(5000), null,
							UnitEnum.张, new BigDecimal(500000), null);
				} else {
					paraDto.putLimitAmtNum(DictCodeListExt1.交易场所_上海证券交易所.getValue(), new BigDecimal(10000), null,
							UnitEnum.张, new BigDecimal(1000000), null);
				}
			}
		}
	}

	/**
	 * //根据金融产品代码获取投管一级分类
	 * @param entity
	 * @return
	 */
	public EtrExchangeTradeJjSaveRequest setAssetTypeById(EtrExchangeTradeJjSaveRequest entity) {
		// 根据资产获取资产类型
		if (StringUtil.isNotEmpty(entity.getFinprodId())) {
			FinProductTypeKey key = new FinProductTypeKey();
			key.setFinprodId(entity.getFinprodId());
			key.setBranch(new Long(0));
			FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(key);
			if (null != finProductType) {//获取资产一级分类
				entity.setAssetsType(finProductType.getType6());
			}
		}
		return entity;
	}
	/**
	 * 根据实际指令类型来生成流程信息 <一句话功能简述> <功能详细描述>
	 *
	 * @param dto
	 * @return
	 * @see
	 * @since 1.0
	 */
	public WorkFlowParameter<EtrExchangeTradeJjSaveRequest> getWorkFlowParameter(EtrExchangeTradeJjSaveRequest dto) {
		WorkFlowParameter<EtrExchangeTradeJjSaveRequest> parameter = new WorkFlowParameter<EtrExchangeTradeJjSaveRequest>();
		// TODO 根据实际指令类型来生成流程信息
		return parameter;
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doComplete(EtrExchangeTradeJjSaveRequest entity, OperationType arg1) {

	}
}
