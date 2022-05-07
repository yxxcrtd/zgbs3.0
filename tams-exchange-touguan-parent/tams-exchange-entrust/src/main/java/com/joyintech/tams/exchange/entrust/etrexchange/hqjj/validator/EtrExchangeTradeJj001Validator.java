/*
 * 文件名：EtrExchangeTrade001Validator.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * @author meifaxin
 * @version 1.0 2021年12月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.etrexchange.hqjj.validator;

import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.common.base.dao.EtrEntrustMapper;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.common.base.entity.WflEtrEntrust;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeTradeSaveRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommon001Service;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.entrust.common.validator.EtrCommonValidator;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.MinTrdUnitLimitDto;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.SubEqutityDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易所指令交易（债券股票回购基金场内指令交易）
 * 
 * @author meifaxin
 * @version 1.0 2021年12月21日
 * @see EtrExchangeTradeJj001Validator
 * @since 1.0
 */
@Component
public class EtrExchangeTradeJj001Validator implements EtrCommonValidator {

	@Autowired
	EtrExchangeTradeCommon001Service etrExchangeTrade001Service;
	
	 /**
     * 委托指令Mapper
     */
    @Autowired
    private EtrEntrustMapper etrEntrustMapper;
    
	
	List<ValidMessage> resultList = new ArrayList<ValidMessage>();
	

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<ValidMessage> bussValid(Object value) {
		List<ValidMessage> errorList = new ArrayList<>();
		// TODO Auto-generated method stub
		EtrExchangeTradeSaveRequest dto = (EtrExchangeTradeSaveRequest) value;
//		if(dto.getOperateType().equals("save")) {
//			if (!StringUtil.isNotEmpty(dto.getStockId())&&!StringUtil.isNotEmpty(dto.getFinprodId())) {
//				errorList.add(new ValidMessage("stockId", "资产名称不可为空！"));
//			}
//		}
		if (StringUtil.isNotEmpty(dto.getOperateType())&&dto.getOperateType().equals("submit")) {
//			Boolean isNonTradingTime = etrExchangeTrade001Service.isNonTradingTime(dto);
//			if (isNonTradingTime) {
//				errorList.add(new ValidMessage(this.getMessage("ETR_ERROR_0056")));
//				return errorList;
//			}
//			if (!StringUtil.isNotEmpty(dto.getPortfolioId())&&
//					(StringUtil.equals(DictCodeList.交易方向_申购.getValue(),dto.getPs())
//							||StringUtil.equals(DictCodeListExt2.交易方向_配股.getValue(),dto.getPs())
//							||StringUtil.equals(DictCodeListExt3.交易方向_配债.getValue(),dto.getPs()))) {
//				errorList.add(new ValidMessage("portfolioId", this.getMessage("ETR_ERROR_0015")));
//			}
//			if(StringUtil.equals(CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL"),"02")){
//				if(!StringUtil.isNotEmpty(dto.getBusinessModel())
//				         &&dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
//					errorList.add(new ValidMessage("businessModel", "请输入业务模式"));
//				}
//			}else{
//				if(!StringUtil.isNotEmpty(dto.getInvAim())
//						&&dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
//					errorList.add(new ValidMessage("invAim", this.getMessage("ETR_ERROR_0060")));
//				}
//			}
//			if (!StringUtil.isNotEmpty(dto.getStockId())&&!StringUtil.isNotEmpty(dto.getFinprodId())) {
//				errorList.add(new ValidMessage("stockId", this.getMessage("ETR_ERROR_0016")));
//			}
//			if (!StringUtil.isNotEmpty(dto.getPs())) {
//				errorList.add(new ValidMessage("ps", this.getMessage("ETR_ERROR_0017")));
//			}else{
//				if (!StringUtil.isNotEmpty(dto.getAcctNo())&&
//						(StringUtil.equals(DictCodeList.交易方向_申购.getValue(),dto.getPs())
//				 ||StringUtil.equals(DictCodeListExt2.交易方向_配股.getValue(),dto.getPs())
//						||StringUtil.equals(DictCodeListExt3.交易方向_配债.getValue(),dto.getPs()))) {
//					errorList.add(new ValidMessage("acctNo", this.getMessage("ETR_ERROR_0020")));
//				}
//			}
//			if (!StringUtil.isNotEmpty(dto.getPriceModel())) {
//				errorList.add(new ValidMessage("priceModel", this.getMessage("ETR_ERROR_0018")));
//			}
//			if (!StringUtil.isNotEmpty(dto.getTargetType())) {
//				errorList.add(new ValidMessage("targetType", this.getMessage("ETR_ERROR_0022")));
//			}
//			//资金账户不为空、证券账户为空才check 账户不匹配
//			if (StringUtil.isNotEmpty(dto.getAcctNo()) && !StringUtil.isNotEmpty(dto.getSecAcctId())&&
//					(StringUtil.equals(DictCodeList.交易方向_申购.getValue(),dto.getPs())
//							||StringUtil.equals(DictCodeListExt2.交易方向_配股.getValue(),dto.getPs())
//							||StringUtil.equals(DictCodeListExt3.交易方向_配债.getValue(),dto.getPs()))) {
//				errorList.add(new ValidMessage("secAcctId", this.getMessage("ETR_ERROR_0023")));
//			}
//			if (null == dto.getEntrustNum() || dto.getEntrustNum().compareTo(BigDecimal.ZERO) == 0) {
//				errorList.add(new ValidMessage("entrustNum", this.getMessage("ETR_ERROR_0027")));
//			}
//			if (StringUtil.isNotEmpty(dto.getPs()) && !dto.getPs().equals(DictCodeListExt3.交易方向_质押券入库.getValue())
//					&& !dto.getPs().equals(DictCodeListExt3.交易方向_质押券出库.getValue())
//					&& (null == dto.getEntrustAmt() || dto.getEntrustAmt().compareTo(BigDecimal.ZERO) == 0)
//					&& !StringUtil.equals(DictCodeListExt2.价格模式_市价指令.getValue(), dto.getPriceModel())) {
//				errorList.add(new ValidMessage("entrustAmt", this.getMessage("ETR_ERROR_0032")));
//			}
//			// 大宗交易增加涨跌幅限制 add by 代志伟 20201015 start
//			// 债券交易、债券质押式回购，指令价格需要在前收盘价格的上下30%或当日已成交的最高、最低价格之间（min（最低价（债券五档行情中的最低价），收盘价x70%）<=price<=max（最高价（债券五档行情中的最高价），收盘价x130%））；
//			// 股票交易、基金交易，指令价格需要满足涨跌停限制；
//			if (dto.getTradePlat().equals(DictCodeList.交易平台_大宗交易平台.getValue()) && !DictCodeListExt2.价格模式_市价指令.getValue().equals(dto.getPriceModel())){
//				Map<String, Object> map = new HashMap<>();
//				map.put("secId",dto.getStockId());
//				Map<String, Object> resultMap  = etrExchangeTrade001Service.findQuotations(map);
//				if (resultMap != null){
//					BigDecimal price = dto.getEntrustPrice();
//					if (dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue())
//							|| dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_基金.getValue())){
//						// 涨停
//						BigDecimal limitUp = new BigDecimal(resultMap.get("upperLimitPrice").toString()).setScale(2,BigDecimal.ROUND_HALF_UP);
//						// 跌停
//						BigDecimal limitDown = new BigDecimal(resultMap.get("lowerLimitPrice").toString()).setScale(2,BigDecimal.ROUND_HALF_UP);
//						if (price.compareTo(limitUp) > 0 || price.compareTo(limitDown) < 0){
//							errorList.add(new ValidMessage("price", this.getMessage("ETR_ERROR_0034")));
//						}
//					}
//					if (dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue())
//							|| (dto.getPs().equals(DictCodeListExt3.交易方向_正回购.getValue()) || dto.getPs().equals(DictCodeListExt3.交易方向_逆回购.getValue()))){
//						// 昨收盘价
//						BigDecimal preClosePrice = new BigDecimal(resultMap.get("preClosePrice").toString()).setScale(2,BigDecimal.ROUND_HALF_UP);
//						// 最高价
//						BigDecimal highPrice = new BigDecimal(resultMap.get("highPrice").toString()).setScale(2,BigDecimal.ROUND_HALF_UP);
//						// 最低价
//						BigDecimal lowPrice = new BigDecimal(resultMap.get("lowPrice").toString()).setScale(2,BigDecimal.ROUND_HALF_UP);
//						BigDecimal max = highPrice.max(preClosePrice.multiply(new BigDecimal(1.3)));
//						BigDecimal min = lowPrice.min(preClosePrice.multiply(new BigDecimal(0.7)));
//						if (price.compareTo(max) > 0 || price.compareTo(min) < 0){
//							errorList.add(new ValidMessage("price", this.getMessage("ETR_ERROR_0055")));
//						}
//					}
//
//				}
//			}
//			// 大宗交易增加涨跌幅限制 add by 代志伟 20201015 end
//			if (StringUtil.isNotEmpty(dto.getFinprodId()) && StringUtil.isNotEmpty(dto.getPs())
//					&& StringUtil.isNotEmpty(dto.getTradeMarket())) {
//				if (StringUtil.isNotEmpty(dto.getEntrustAmt()) && dto.getEntrustAmt().compareTo(BigDecimal.ZERO) > 0
//						&& StringUtil.isNotEmpty(dto.getEntrustNum())
//						&& dto.getEntrustNum().compareTo(BigDecimal.ZERO) > 0) {
//					String finprodType2 = dto.getFinprodType2();
//					if (finprodType2.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
//							|| finprodType2.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
//							|| finprodType2.equals("ZYSHG")) {
//						finprodType2 = DictCodeList.平安资产类型_质押式回购.getValue();
//					}
//					MinTrdUnitLimitDto minTrdUnitLimitDto = etrExchangeTrade001Service.getInterfSysMinTrdUnit(
//							dto.getFinprodId(), dto.getTradePlat(), dto.getTradeMarket(), dto.getFinprodType2(),
//							dto.getPs());
//					if (null != minTrdUnitLimitDto) {
//						boolean flag1 = false;
//						boolean flag2 = false;
//						if (null == minTrdUnitLimitDto.getMinAmt() || (minTrdUnitLimitDto.getMinAmt() != null
//								&& minTrdUnitLimitDto.getMinAmt().compareTo(BigDecimal.ZERO) > 0
//								&& dto.getEntrustAmt().compareTo(minTrdUnitLimitDto.getMinAmt()) < 0)) {
//							flag1 = true;
//						}
//						if (null == minTrdUnitLimitDto.getMinNum() || (minTrdUnitLimitDto.getMinNum() != null
//								&& minTrdUnitLimitDto.getMinNum().compareTo(BigDecimal.ZERO) > 0
//								&& dto.getEntrustNum().compareTo(minTrdUnitLimitDto.getMinNum()) < 0)) {
//							flag2 = true;
//							if (StringUtils.equals(dto.getTradePlat(), DictCodeList.交易平台_竞价撮合平台.getValue())
//									&& (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType2)
//											|| DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(finprodType2)
//											|| DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(finprodType2))
//									&& StringUtils.equals(dto.getPs(), DictCodeList.交易方向_卖出.getValue())
//									&& etrExchangeTrade001Service.isSalesOfSmallShares(dto.getAvailableNum(),
//											minTrdUnitLimitDto.getMinStep(), dto.getEntrustNum())) {
//								flag2 = false;
//							}
//						}
//						if (flag1 && flag2 && !(null == minTrdUnitLimitDto.getMinNum()
//								&& null == minTrdUnitLimitDto.getMinAmt())) {
//							String prompt = "";
//							if (flag1 && null != minTrdUnitLimitDto.getMinNum()) {
//								prompt += "最低交易数量" + etrExchangeTrade001Service.getNumAndUnit(
//										minTrdUnitLimitDto.getMinNum(), minTrdUnitLimitDto.getMinNumUnit());
//							}
//							if (flag1 && flag2 && flag1 && null != minTrdUnitLimitDto.getMinNum()
//									&& null != minTrdUnitLimitDto.getMinAmt()) {
//								prompt += "，";
//							}
//							if (flag2 && null != minTrdUnitLimitDto.getMinAmt()) {
//								prompt += "最低交易金额"
//										+ etrExchangeTrade001Service.getNumAndUnit(minTrdUnitLimitDto.getMinAmt());
//							}
//							if (flag1 && flag2 && flag1 && null != minTrdUnitLimitDto.getMinNum()
//									&& null != minTrdUnitLimitDto.getMinAmt()) {
//								prompt += "，应至少满足一个！";
//							}
//							errorList.add(new ValidMessage(prompt));
//						}
//					}
//				}
//			}
//			// 如果是债券(交易方向非配债)、check 净价、全价、收益率 不可为空 输入某项,其他两项会计算,如果计算不出来，说明债券不符合
//			if (StringUtil.isNotEmpty(dto.getFinprodType2())
//					&& StringUtils.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
//					&& (!StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_配债.getValue())
//							&& !StringUtils.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue())
//							&& !StringUtils.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())
//							&& !StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_债转股.getValue())
//							&& !StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_质押券入库.getValue())
//							&& !StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_质押券出库.getValue()))) {
//				if (!StringUtil.equals(DictCodeListExt2.价格模式_市价指令.getValue(), dto.getPriceModel())) {
//					if (null == dto.getPrice()) {
//						errorList.add(new ValidMessage("price", this.getMessage("ETR_ERROR_0028")));
//					}
//					if (null == dto.getFprice()) {
//						errorList.add(new ValidMessage("fprice", this.getMessage("ETR_ERROR_0029")));
//					}
//					if (null == dto.getYield()) {
//						errorList.add(new ValidMessage("yield", this.getMessage("ETR_ERROR_0030")));
//					}
//				}
//			}
//			// 如果是债券 质押券入库 质押券数量要>0
//			if (StringUtil.isNotEmpty(dto.getFinprodType2())
//					&& StringUtils.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())
//					&& StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_质押券入库.getValue())) {
//				if (null == dto.getEntrustAmt() || dto.getEntrustAmt().compareTo(BigDecimal.ZERO) == 0) {
//					errorList.add(new ValidMessage("toShareAvailableNum", this.getMessage("ETR_ERROR_0033")));
//				}
//			}
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("portfolioId", dto.getPortfolioId());
//			map.put("tradeMarket", dto.getTradeMarket());
//			map.put("acctNo", dto.getAcctNo());
//			List<SelectOptionDto> ptlSecAcctInfo = etrExchangeTrade001Service.getPtlSecAcctInfo(map);
//			if (ptlSecAcctInfo == null || ptlSecAcctInfo.size() == 0) {
//				if (DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(dto.getTradeMarket())) {
//					errorList.add(new ValidMessage("tradeMarket",getMessage("ETR_ERROR_0014", DictCodeListExt1.交易场所_上海证券交易所.getCodeName())));
//				} else if (DictCodeListExt1.交易场所_深圳证券交易所.getValue().equals(dto.getTradeMarket())) {
//					errorList.add(new ValidMessage("tradeMarket",getMessage("ETR_ERROR_0014", DictCodeListExt1.交易场所_深圳证券交易所.getCodeName())));
//				}
//			}
//
//			// etr_check_cash_switch 01 不提示 02 提示 禁止交易
//			if (CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_CASH_SWITCH).equals(DictCodeListExt1.是或否_否.getValue())
//					&& (DictCodeList.交易方向_买入.getValue().equals(dto.getPs()) || DictCodeListExt3.交易方向_配债.getValue().equals(dto.getPs())
//					|| DictCodeListExt2.交易方向_配股.getValue().equals(dto.getPs()) || DictCodeList.交易方向_申购.getValue().equals(dto.getPs())
//					|| DictCodeListExt3.交易方向_逆回购.getValue().equals(dto.getPs()))) {
//				Map<String,Object> param = new HashMap<String,Object>();
//				param.put("portfolioId",dto.getPortfolioId());
//				param.put("ccy",DictCodeList.币种_人民币_CNY.getValue());
//				dto.setAccAvailableCash(etrExchangeTrade001Service.selectAccAvailableCash(param));
//				if (StringUtil.isNotEmpty(dto.getAccAvailableCash()) && StringUtil.isNotEmpty(dto.getEntrustAmt())) {
//					if (dto.getAccAvailableCash().compareTo(dto.getEntrustAmt()) == -1) {
//						errorList.add(new ValidMessage("accAvailableCash", this.getMessage("ETR_ERROR_0019",dto.getPortfolioId())));
//					}
//				}
//			}
//
//			// etr_check_pos_switch 01 不提示 02 提示 禁止交易
//			if (CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH).equals(DictCodeListExt1.是或否_否.getValue()) && (
//					DictCodeList.交易方向_卖出.getValue().equals(dto.getPs())
//					|| DictCodeList.交易方向_行权_回售.getValue().equals(dto.getPs())
//					|| DictCodeListExt3.交易方向_债转股.getValue().equals(dto.getPs())
//					|| DictCodeListExt3.交易方向_质押券入库.getValue().equals(dto.getPs()))) {
//				Map<String,Object> param = new HashMap<String,Object>();
//				param.put("portfolioId",dto.getPortfolioId());
//				param.put("finprodId",dto.getFinprodId());
//				param.put("ccy",DictCodeList.币种_人民币_CNY.getValue());
//				param.put("secAcctId",dto.getSecAcctId());
//				dto.setAccAvailablePos(etrExchangeTrade001Service.selectAvailableNum(param));
//				if (StringUtil.isNotEmpty(dto.getAccAvailablePos()) && StringUtil.isNotEmpty(dto.getEntrustNum())) {
//					if (dto.getAccAvailablePos().compareTo(dto.getEntrustNum()) == -1) {
//						errorList.add(new ValidMessage("accAvailablePos", this.getMessage("ETR_ERROR_0021")));
//					}
//				}
//			}
//
//			//行权回售
//			if (DictCodeList.交易方向_行权_回售.getValue().equals(dto.getPs())) {
//				Map<String, Object> param = new HashMap<String, Object>();
//				param.put("finprodId", dto.getFinprodId());//金融产品代码
//				param.put("startDate", DateUtil.format(dto.getMdate()));
//				param.put("endDate", DateUtil.format(dto.getVdate()));
//				int record = etrExchangeTrade001Service.selectBondExecPlan(param);
//				if (record == 0) {
//					errorList.add(new ValidMessage("vdate", this.getMessage("ETR_ERROR_0024")));
//				}
//			}
//
//			//配股
//			if (DictCodeListExt2.交易方向_配股.getValue().equals(dto.getPs())) {
//				Map<String, Object> param = new HashMap<String, Object>();
//				param.put("tradePlat", dto.getTradePlat());//交易平台
//				param.put("secId", dto.getStockId());//证券代码
//				param.put("othCodeType", DictCodeListExt3.金融产品其他代码类型_配股代码.getValue());
//				Page<SelectOptionDto> page = etrExchangeTrade001Service.getFinprodMarketInfo(param);
//				if (page == null || page.getRecords() == 0) {
//					errorList.add(new ValidMessage("stockId", getMessage("ETR_ERROR_0025",DictCodeListExt2.交易方向_配股.getCodeName())));
//				}
//			}
//
//			//配债
//			if (DictCodeListExt3.交易方向_配债.getValue().equals(dto.getPs())) {
//				Map<String, Object> param = new HashMap<String, Object>();
//				param.put("tradePlat", "");//dto.getTradePlat());//交易平台
//				param.put("secId", dto.getStockId());//证券代码
//				param.put("othCodeType", DictCodeListExt3.金融产品其他代码类型_配债代码.getValue());
//				Page<SelectOptionDto> page = etrExchangeTrade001Service.getFinprodMarketInfo(param);
//				if (page == null || page.getRecords() == 0) {
//					errorList.add(new ValidMessage("stockId", getMessage("ETR_ERROR_0025",DictCodeListExt3.交易方向_配债.getCodeName())));
//				}
//			}
//
//			//申购
//			if (DictCodeList.交易方向_申购.getValue().equals(dto.getPs()) && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(dto.getFinprodType2())) {
//				Map<String, Object> param = new HashMap<String, Object>();
//				param.put("finprodId", dto.getFinprodId());//金融产品代码
//				String customerTypeTwo = etrExchangeTrade001Service.getCustomerTypeTwo(param);
//				if (!StringUtils.equals(customerTypeTwo,DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue()) && !StringUtils.equals(customerTypeTwo,DictCodeList.WIND一级分类_含债券一级分类等_可交换债.getValue())) {
//					errorList.add(new ValidMessage("stockId", getMessage("ETR_ERROR_0025",DictCodeList.交易方向_申购.getCodeName())));
//				}
//			}
//
//			//债转股
//			if (DictCodeListExt3.交易方向_债转股.getValue().equals(dto.getPs()) && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(dto.getFinprodType2())) {
//				Map<String, Object> param = new HashMap<String, Object>();
//				param.put("finprodId", dto.getFinprodId());//金融产品代码
//				String customerTypeTwo = etrExchangeTrade001Service.getCustomerTypeTwo(param);
//				if (!StringUtils.equals(customerTypeTwo,DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue())) {
//					errorList.add(new ValidMessage("stockId", getMessage("ETR_ERROR_0025",DictCodeListExt3.交易方向_债转股.getCodeName())));
//				}
//			}
//
//			//正回购、质押券出库
//			// 委托指令组合的可用持仓不足时  01 提醒 02 禁止
//			if ((DictCodeListExt3.交易方向_正回购.getValue().equals(dto.getPs()) || DictCodeListExt3.交易方向_质押券出库.getValue().equals(dto.getPs()))
//					&& CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH).equals(DictCodeListExt1.是或否_否.getValue())) {
//				Map<String,Object> param = new HashMap<String,Object>();
//				param.put("portfolioId",dto.getPortfolioId());
//				param.put("finprodId","XN_PRODID999");
//				if(null != dto.getSecAcctId()){
//					param.put("secAcctId", dto.getSecAcctId());
//				}
//				param.put("ccy",DictCodeList.币种_人民币_CNY.getValue());
//				dto.setAccAvailablePos(etrExchangeTrade001Service.selectAvailableNum(param));
//				if (StringUtil.isNotEmpty(dto.getAccAvailablePos()) && StringUtil.isNotEmpty(dto.getEntrustNum())) {
//					if (dto.getAccAvailablePos().compareTo(dto.getEntrustNum()) == -1) {
//						errorList.add(new ValidMessage("accAvailablePos", this.getMessage("ETR_ERROR_0026")));
//					}
//				}
//			}
			//新股申购
			if (DictCodeList.交易方向_申购.getValue().equals(dto.getPs()) && DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(dto.getFinprodType2())) {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("finprodId", dto.getFinprodId());//金融产品代码
				param.put("secAcctId",dto.getSecAcctId());
				param.put("ps",dto.getPs());
				param.put("portfolioId",dto.getPortfolioId());
				param.put("cDate",DateUtil.getSysDate());
				param.put("finprodType2",dto.getFinprodType2());
				param.put("othType",DictCodeListExt3.金融产品其他代码类型_新股申购代码.getValue());
				param.put("entrustId",dto.getEntrustId());
				param.put("tradePlat",dto.getTradePlat());
//				List<WflEtrEntrust>  list = etrExchangeTrade001Service.selectWflEntrust(param);
//				if (list.size()>0){
//					errorList.add(new ValidMessage(getMessage("ETR_ERROR_0061")));
//				}

				// 同一股东只允许下达一次同一只新股申购指令
				List<String> strList = etrExchangeTrade001Service.selectWflEntrustAddAndEtrEntrustAdd(param);
				if (strList.size()>0){
					errorList.add(new ValidMessage(getMessage("ETR_ERROR_0064")));
				}

//				BigDecimal entrustNum = etrExchangeTrade001Service.getEntrustNum(param);
//				if (etrExchangeTrade001Service.getFinProductQutoInfo(param).size()>0&&entrustNum.compareTo(dto.getEntrustNum())<0) {
//					errorList.add(new ValidMessage("entrustNum", getMessage("ETR_ERROR_0059")));
//				}
			}
			// 可转债申购
			if (DictCodeList.交易方向_申购.getValue().equals(dto.getPs()) && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(dto.getFinprodType2())) {
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("finprodId", dto.getFinprodId());//金融产品代码
				param.put("entrustId",dto.getEntrustId());
				param.put("portfolioId",dto.getPortfolioId());
				param.put("cDate",DateUtil.getSysDate());

				param.put("secAcctId",dto.getSecAcctId());
				param.put("ps",dto.getPs());
				param.put("finprodType2",dto.getFinprodType2());
				param.put("tradePlat",dto.getTradePlat());

				// 同一股东只允许下达一次可转债信用申购指令
				List<String> list = etrExchangeTrade001Service.selectWflEntrustAddAndEtrEntrustAdd(param);
				if (list.size()>0){
					if(list.contains(dto.getSecAcctId())){
						errorList.add(new ValidMessage(getMessage("ETR_ERROR_0062")));
					}
				}

				// 可转债申购下指令数量不能大于实际可转债总数量
//				BigDecimal entrustNum = etrExchangeTrade001Service.getEntrustNum(param);
//				if (entrustNum.compareTo(dto.getEntrustNum())<0) {
//					errorList.add(new ValidMessage("entrustNum", getMessage("ETR_ERROR_0063")));
//				}
			}

			Boolean isNonTradingTime = etrExchangeTrade001Service.isNonTradingTime(dto);
			if (isNonTradingTime) {
				errorList.add(new ValidMessage("当日已闭市，不允许下指令"));
			}

//			//配股配债权益数量check
//			if (DictCodeListExt2.交易方向_配股.getValue().equals(dto.getPs())||DictCodeListExt3.交易方向_配债.getValue().equals(dto.getPs())) {
//				Map<String, Object> param = new HashMap<String, Object>();
//				param.put("finprodId", dto.getFinprodId());//金融产品代码
//				param.put("secAcctId",dto.getSecAcctId());
//				param.put("ps",dto.getPs());
//				param.put("cDate",DateUtil.getSysDate());
//				param.put("portfolioId",dto.getPortfolioId());
//				param.put("finprodType2",dto.getFinprodType2());
//				param.put("entrustType",dto.getEntrustType());
//				param.put("entrustId",dto.getEntrustId());
//				param.put("othType",DictCodeListExt2.交易方向_配股.getValue().equals(dto.getPs())?DictCodeListExt3.金融产品其他代码类型_配股代码.getValue():DictCodeListExt3.金融产品其他代码类型_配债代码.getValue());
//				BigDecimal entrustNum = etrExchangeTrade001Service.getEntrustNum(param);
//				List<SubEqutityDto> entrusts = etrExchangeTrade001Service.getsubEqutity(param);
//				BigDecimal equtity = new BigDecimal(0);
//				if (entrusts.size()>0){
//					equtity = entrusts.get(0).getEntrustNum();
//				}
//				if (etrExchangeTrade001Service.getFinProductQutoInfo(param).size()>0&&dto.getEntrustNum().add(equtity).compareTo(entrustNum)>0) {
//					//errorList.add(new ValidMessage("entrustNum", getMessage("ETR_ERROR_0060",DictCodeListExt3.交易方向_配债.getValue())));
//				}
//			}
//
//			//提交的时候还要判断指令状态、避免关闭后再提交指令
//			if(null != dto.getEntrustId()){
//				EtrEntrust etrEntrust = etrEntrustMapper.selectByPrimaryKey(dto.getEntrustId());
//				if(null != etrEntrust && !DictCodeList.委托状态_已退回.getValue().equals(etrEntrust.getEntrustStatus())
//						&& !DictCodeList.委托状态_已撤回.getValue().equals(etrEntrust.getEntrustStatus()) ){
//					String entrustStatusStr = SysDataUtil.getNameFromDataDict(DictCode.委托状态, etrEntrust.getEntrustStatus());
//					errorList.add(new ValidMessage(entrustStatusStr + "的指令不允许修改!"));
//				}
//			}
		}
		return errorList;
	}
}
