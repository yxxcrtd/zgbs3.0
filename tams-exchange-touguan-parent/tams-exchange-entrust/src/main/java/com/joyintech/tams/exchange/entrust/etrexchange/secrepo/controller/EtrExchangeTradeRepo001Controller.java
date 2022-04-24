/*
 * 文件名：EtrTradePlace001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：meifaxin
 * 修改时间：2021年12月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.etrexchange.secrepo.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.common.base.entity.FinExercisePlan;
import com.joyintech.tams.component.business.bond.BusiFinExercisePlanService;
import com.joyintech.tams.component.business.common.dto.FinStockInfoDto;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.entrust.common.aop.ExchangeEntrustRdsCommonAop;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.CalcDeviationDegreeRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.CalcPriceAndYieldRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.CalcPriceAndYieldResponse;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeMiniLimitPriceRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.FinprodMarketInfoRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.MinNumStepRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.MinPriceRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.PsOptionsRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.PtlSecAcctInfoRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.SecFaceValueRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.SelectOptionRequestDto;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.StdsecConverseRatioRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.hqjj.validator.EtrExchangeTradeJj001Validator;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dao.EtrExchangeTradeSecRepoMapper;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradePortfolioSecRepoRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeQuotationHqSecRepoResponse;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradeSaveSecRepoRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.service.EtrExchangeTradeSecRepo001Service;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.service.EtrExchangeTradeSecRepoValidatorService;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.position.PositionCashSecDto;
import com.joyintech.tams.invest.common.dto.position.PositionCashSecRequest;
import com.joyintech.tams.invest.common.entrust.inter.EtrTradeCommonervice;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.MinPirceDto;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.MinTrdUnitLimitDto;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 场内债券指令（通用质押式回购 下单及审批）
 * @author qmh
 * @version 1.0 2022年4月15日
 * @see EtrExchangeTradeRepo001Controller
 * @since 1.0
 */
@Controller
@PageId("etrExchangeTradeRepo")
@RequestMapping("/etrexchange/secrepo/etrExchangeTradeRepo001")
@Api(tags = "场内交易-现券指令—通用质押式回购下单审批接口")
public class EtrExchangeTradeRepo001Controller extends BaseController{

	/**
	 * 日志
	 */
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private IPlatformDate iPlatformDate;

	/**
	 * 业务编号组件
	 */
	@Autowired(required=false)
	private BaseGetBusinessId bussGetBussinessIdService;

	@Autowired
	private EtrExchangeTradeSecRepo001Service etrExchangeTradeSecRepo001Service;

	@Autowired
	private BusiFinExercisePlanService busiFinExercisePlanService;

	public static final String ENTRUST_RECORD_NEW = "new";

	public static final String ENTRUST_RECORD_MODIFY = "modify";

	@Value("${quote.flag:false}")
	private boolean useInterfQuoteFlag;

	@Value("${quote.ws.host}")
	private String wsHost;

	@Value("${quote.ws.url}")
	private String wsUrl;

	@Autowired
	private EtrExchangeTradeSecRepoMapper etrExchangeTradeSecRepoMapper;

	@Autowired
	private EtrTradeCommonervice etrTradeCommonervice;

	@Autowired
	private EtrExchangeTradeSecRepoValidatorService etrExchangeTradeSecRepoValidatorService;

	@Autowired
	private EtrExchangeTradeJj001Validator etrExchangeTradeJj001Validator;

	@ApiOperation(value = "场内现券通用质押式回购下单参数", notes = "场内现券通用质押式回购下单参数")
	@PostMapping("/getPageParams")
	@ResponseBody
	public Message<Map<String, Object>> getPageParams(){
		Map<String, Object> initPageParams = new HashMap<>();
		initPageParams.put("TRADE_PLACE", this.getSelectDataFromDataDic(DictCode.交易场所, null));
		initPageParams.put("BUSINESS_MODEL", this.getSelectDataFromDataDic(DictCode.业务模式, null));
		this.setInvAimSelectOption(initPageParams);
		this.setPsSelectOptions(initPageParams);
		this.setPriceModelSelectOptions(initPageParams);
		// 设置固定的数据字典明细参数
		this.setConstSelectOption(initPageParams);
		Date platformDate = DateUtil.platDateFromCache();
		initPageParams.put("iPlatformDate", DateUtil.format(platformDate));
		initPageParams.put("bond_traded_unit", BussinessConstant.BOND_TRADED_UNIT_DEFAULT);

		//流程id
		String workflowId = "null".equals(getParam(GlobalConstant.WORKFLOW_ID))?null:getParam(GlobalConstant.WORKFLOW_ID);
		initPageParams.put("workflowId", workflowId);

		//委托代码
		String entrustId = "null".equals(getParam("entrustId"))?null:getParam("entrustId");
		initPageParams.put("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());

		//母页面的pageModel
		String parentPageModel = getParam(GlobalConstant.DETAIL_PAGE_MODEL_KEY);
		initPageParams.put("parentPageModel", parentPageModel);
		initPageParams.put("PAGE_MODEL_NEW", GlobalConstant.DETAIL_PAGE_MODEL_NEW);
		initPageParams.put("PAGE_MODEL_EDIT", GlobalConstant.DETAIL_PAGE_MODEL_EDIT);
		//是否_是
		initPageParams.put("YES_OR_NO_Y", DictCodeList.是否_是.getValue());

		String isFromToDoList = getParam("isFromToDoList");
		EtrExchangeTradeSaveSecRepoRequest entity = new EtrExchangeTradeSaveSecRepoRequest();
		if (StringUtil.isNotEmpty(workflowId)) {
			//流程代办checkImportData
			entity = etrExchangeTradeSecRepo001Service.getBussinessEntityByWorkflowId(
					workflowId);
			initPageParams.put(GlobalConstant.ENTITY, entity);
			initPageParams.put("workflowId", workflowId);
		}
		else if (StringUtil.isNotEmpty(entrustId) && !"true".equals(isFromToDoList)) {
			//查看
			entity = etrExchangeTradeSecRepo001Service.getEtrExchangeTradeInfo(entrustId);
		}
		else{
			//新建页面初始化
			entity.setEntrustDate(platformDate);
			entity.setEntrustStatus(BussinessDictCodeList.委托状态_新建.getValue());
		}

		initPageParams.put("ENTRUST_RECORD_NEW", ENTRUST_RECORD_NEW);
		initPageParams.put("ENTRUST_RECORD_MODIFY", ENTRUST_RECORD_MODIFY);
		initPageParams.put(GlobalConstant.ENTITY, entity);
		if (null != entity && null != entity.getEntrustPrice()) {
			initPageParams.put("BOND_SCALE", entity.getEntrustPrice().scale());
		} else {
			initPageParams.put("BOND_SCALE", 3);
		}
		//最小价差List
		List<MinPirceDto> minPriceWhole = getInterfSysMinPriceAll(new HashMap<>());
		initPageParams.put("minPriceWhole", minPriceWhole);
		initPageParams.put("CONTROL_TYPE_01", BussinessDictCodeListRds.控制类型_大于.getValue());
		initPageParams.put("CONTROL_TYPE_02", BussinessDictCodeListRds.控制类型_大于等于.getValue());
		initPageParams.put("CONTROL_TYPE_03", BussinessDictCodeListRds.控制类型_小于.getValue());
		initPageParams.put("CONTROL_TYPE_04", BussinessDictCodeListRds.控制类型_小于等于.getValue());

		//清算速度
		setSettleSpreedSelectOptions(initPageParams);
		initPageParams.put("ACCOUNT_SPEED_01", DictCodeList.清算速度_T_0.getValue());
		initPageParams.put("ACCOUNT_SPEED_02", DictCodeList.清算速度_T_1.getValue());
		//展示投资目的或业务模式参数
		String SHOW_INVAIM_OR_BUSSINSSMODEL = CacheUtil.getSysParam("SHOW_INVAIM_OR_BUSSINSSMODEL");
		initPageParams.put("SHOW_INVAIM_OR_BUSSINSSMODEL", SHOW_INVAIM_OR_BUSSINSSMODEL);

		// 场内交易指令画面行情区域自动刷新时间（毫秒）
		long etrExchange_HqArea_refresh_period = 20000;
		String tempSysParaVal = CacheUtil.getSysParam(
				BussinessConstant.ETREXCHANGE_HQAREA_REFRESH_PERIOD);
		if (StringUtil.isNotBlank(tempSysParaVal)
				&& !StringUtil.equals(BussinessConstant.ETREXCHANGE_HQAREA_REFRESH_PERIOD,
				tempSysParaVal)) {
			try {
				etrExchange_HqArea_refresh_period = Long.parseLong(tempSysParaVal);
			}
			catch (Exception e) {
			}
		}
		initPageParams.put("etrExchange_HqArea_refresh_period", etrExchange_HqArea_refresh_period);
		initPageParams.put("riskMathSwitch", CacheUtil.getSysParam("risk_math_switch"));
		initPageParams.put("pageRedisFlag", CacheUtil.getSysParam("pageRedisFlag"));
		initPageParams.put("IS_RISKMATH_SOFTCONTROL", CacheUtil.getSysParam("IS_RISKMATH_SOFTCONTROL"));
		initPageParams.put("ETR_TRADE_TIME_SWITCH", CacheUtil.getSysParam("etr_trade_time_switch"));
		initPageParams.put("ETR_CHECK_POS_SWITCH",CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_POS_SWITCH));
		return new Message<Map<String, Object>>(initPageParams).success();
	}

	/**
	 * 由于交易平台不同，设置不同交易平台对应的委托方向
	 * @param initPageParams initPageParams
	 */
	private void setSettleSpreedSelectOptions(Map<String, Object> initPageParams) {
		SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.清算速度, null);
		// 固定收益
		List<SelectOptionDto> settleSpreed_gd = selectDto.getOptions().stream().filter(option -> {
			return StringUtil.equals(option.getId(), DictCodeList.清算速度_T_0.getValue())
					|| StringUtil.equals(option.getId(), DictCodeList.清算速度_T_1.getValue());
		}).collect(Collectors.toList());
		initPageParams.put("ACCOUNT_SPEED_GD", JsonUtils.objectArray2Json(settleSpreed_gd.toArray()));
	}

	/**
	 * 由于交易平台不同，设置不同交易平台对应的委托方向
	 * @param initPageParams initPageParams
	 */
	private void setPsSelectOptions(Map<String, Object> initPageParams) {
		SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.交易方向, null);
		List<SelectOptionDto> ps_repo = selectDto.getOptions().stream().filter(option -> {
			return  StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_正回购.getValue())
					|| StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_逆回购.getValue());
		}).collect(Collectors.toList());
		selectDto.setOptions(ps_repo);
		initPageParams.put("PS", selectDto);
	}

	/**
	 * 价格模式
	 * @param initPageParams initPageParams
	 */
	private void setPriceModelSelectOptions(Map<String, Object> initPageParams) {
		SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.价格模式, null);
		List<SelectOptionDto> priceModel = selectDto.getOptions().stream().filter(option -> {
			return StringUtil.equals(option.getId(), DictCodeListExt2.价格模式_限价指令.getValue())
					|| StringUtil.equals(option.getId(), DictCodeListExt2.价格模式_市价指令.getValue());
		}).collect(Collectors.toList());
		initPageParams.put("PRICE_MODEL", JsonUtils.objectArray2Json(priceModel.toArray()));

	}

	/**
	 * 设置画面的一些固定的数据字典明细
	 * @param initPageParams initPageParams
	 */
	private void setConstSelectOption(Map<String, Object> initPageParams) {
		// 金融产品类型(投管用)
		initPageParams.put("FINPROD_TYPE_TG_F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
		initPageParams.put("FINPROD_TYPE_TG_F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
		initPageParams.put("FINPROD_TYPE_TG_ZYSHG", "ZYSHG");
		// 交易方向
		initPageParams.put("PS_03", DictCodeListExt3.交易方向_正回购.getValue());
		initPageParams.put("PS_04", DictCodeListExt3.交易方向_逆回购.getValue());
		// 价格模式
		initPageParams.put("PRICE_MODEL_PRM02", DictCodeListExt2.价格模式_限价指令.getValue());
		initPageParams.put("PRICE_MODEL_PRM03", DictCodeListExt2.价格模式_市价指令.getValue());
		// 交易场所
		initPageParams.put("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
		initPageParams.put("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
		// 指令业务类型
		initPageParams.put("ENTRUST_BUSS_TYPE_29", DictCodeListExt3.上农商_指令业务类型_通用式质押回购指令.getValue());
	}

	/**
	 * 设置投资目的
	 * @param initPageParams initPageParams
	 */
	private void setInvAimSelectOption(Map<String, Object> initPageParams) {
		SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.投资目的, null);
		List<SelectOptionDto> invAimList = selectDto.getOptions().stream().filter(option -> {
			return StringUtil.equals(option.getId(), DictCodeListExt1.投资目的_公允价值变动计入其他综合收益.getValue())
					|| StringUtil.equals(option.getId(), DictCodeListExt1.投资目的_公允价值变动计入当期损益.getValue())
					|| StringUtil.equals(option.getId(), DictCodeListExt1.投资目的_摊余成本类.getValue());
		}).collect(Collectors.toList());
		selectDto.setOptions(invAimList);
		initPageParams.put("INV_AIM", selectDto);
	}

	@ResponseBody
	@PostMapping("/selectCounterIdByCounterType")
	public Message<List<SelectOptionDto>> selectCounterIdByCounterType(@RequestBody SelectOptionRequestDto dto){
		try {
			List<SelectOptionDto> selectOptionDtos = etrExchangeTradeSecRepo001Service.selectCounterIdByCounterType(dto);
			return new Message<List<SelectOptionDto>>().success().setData(selectOptionDtos);
		} catch (Exception e) {
			logger.error("查询功能异常", e);
			return new Message<List<SelectOptionDto>>().fail().setMessage("查询异常");
		}
	}

	@ResponseBody
	@RequestMapping("/getSecAcctInfo")
	public List<SelectOptionDto> getSecAcctInfo(){
		return etrExchangeTradeSecRepo001Service.getSecAcctInfo(RequestUtil.getPageParam());
	}

	@ResponseBody
	@RequestMapping("/getFinprodId")
	public List<SelectOptionDto> getFinprodId(@RequestBody FinprodMarketInfoRequest dto){
		return etrExchangeTradeSecRepo001Service.getFinprodId(RequestUtil.getPageParam());
	}

	@ResponseBody
	@PostMapping("/getFinprodMarketInfo")
	@ApiOperation(value = "查询回购资产列表", notes = "查询回购资产列表")
	public Message<Page<SelectOptionDto>> getFinprodMarketInfo(@RequestBody FinprodMarketInfoRequest dto){
		logger.debug("getFinprodMarketInfo() called with parameters => 【dto = {}】",JsonUtils.object2Json(dto));
		try {
			Map<String, Object> map = BeanUtil.object2Map(dto);
			map.put("acctTradeMarket", dto.getTradeMarket());
			map.put("FINPROD_TYPE2","ZYSHG");
			Page<SelectOptionDto> tempList = etrExchangeTradeSecRepo001Service.getFinprodMarketInfo(map);
			return new Message<Page<SelectOptionDto>>().success().setData(tempList);
		} catch (Exception e) {
			logger.error("查询功能异常", e);
			return new Message<Page<SelectOptionDto>>().fail().setMessage("查询异常");
		}
	}

	@ResponseBody
	@RequestMapping("/getFinprodMarketInfoPosition")
	public Page<SelectOptionDto> getFinprodMarketInfoPosition(){
		return etrExchangeTradeSecRepo001Service.getFinprodMarketInfoPosition(RequestUtil.getPageParam());
	}

	@ApiOperation(value = "获取组合资产账户列表", notes = "获取组合资产账户列表")
	@ResponseBody
	@PostMapping("/getPtlSecAcctInfo")
	public Message<List<SelectOptionDto>> getPtlSecAcctInfo(@RequestBody PtlSecAcctInfoRequest dto){
		try {
			Map<String, Object> map = BeanUtil.object2Map(dto);
			List<SelectOptionDto> tempList= etrExchangeTradeSecRepo001Service.getPtlSecAcctInfo(map);
			return new Message<List<SelectOptionDto>>().success().setData(tempList);
		} catch (Exception e) {
			logger.error("查询功能异常", e);
			return new Message<List<SelectOptionDto>>().fail().setMessage("查询异常");
		}
	}

	/**
	 * 对手方交易商联想文本框初始化。
	 *
	 * @return 联想文本框
	 */
	@ResponseBody
	@RequestMapping("/selectCounterDealer")
	public List<SuggestDto> selectCounterDealer() {
		Map<String, Object> pageParam = RequestUtil.getPageParam();
		return etrExchangeTradeSecRepo001Service.selectCounterDealer(pageParam);
	}

	/**
	 * 对手方交易员联想文本框初始化。
	 *
	 * @return 联想文本框
	 */
	@ResponseBody
	@RequestMapping("/selectCounterTrader")
	public Message<List<SelectOptionDto>> selectCounterTrader(@RequestBody  SelectOptionRequestDto dto){
		try {
			Map<String, Object> pageParam = RequestUtil.getPageParam();
			if(!Objects.isNull(dto.getCounterId())) {
				pageParam.put("counterId", dto.getCounterId());
			}
			List<SelectOptionDto> selectOptionDtos = etrExchangeTradeSecRepo001Service.selectCounterTrader(pageParam);
			return new Message<List<SelectOptionDto>>().success().setData(selectOptionDtos);
		} catch (Exception e) {
			logger.error("查询功能异常", e);
			return new Message<List<SelectOptionDto>>().fail().setMessage("查询异常");
		}
	}


	/**
	 * 本方交易员联想文本框初始化。
	 *
	 * @return 联想文本框
	 */
	@ResponseBody
	@RequestMapping("/selectTrader")
	public Message<List<SelectOptionDto>> selectTrader(@RequestBody SelectOptionRequestDto dto){
		try {
			Map<String, Object> pageParam = RequestUtil.getPageParam();
			List<SelectOptionDto> selectOptionDtos = etrExchangeTradeSecRepo001Service.selectTrader(pageParam);
			return new Message<List<SelectOptionDto>>().success().setData(selectOptionDtos);
		} catch (Exception e) {
			logger.error("查询功能异常", e);
			return new Message<List<SelectOptionDto>>().fail().setMessage("查询异常");
		}
	}

//	/**
//	 * 查询资产对应组合的可用持仓信息
//	 *
//	 * @param dto
//	 * @return 可用持仓
//	 * @author dubaojun
//	 */
//	@ApiOperation(value = "查询资产对应组合的可用持仓信息", notes = "查询资产对应组合的可用持仓信息")
//	@ResponseBody
//	@PostMapping("/selectAvailableNum")
//	public Message<String> selectAvailableNum(@RequestBody AvailableEtrSecPositionRequest dto){
//		try{
//			Map<String, Object> param = BeanUtil.object2Map(dto);
//			BigDecimal availableNum = etrExchangeTradeSecRepo001Service.selectAvailableNum(param);
//			if(availableNum == null) {
//				availableNum= BigDecimal.ZERO;
//			}
//			String availableNumStr=availableNum.setScale(2,RoundingMode.HALF_UP).toString();
//			return new Message<>(availableNumStr);
//		} catch (Exception e) {
//			logger.error("查询功能异常", e);
//			return new Message<String>().fail().setMessage("查询异常");
//		}
//	}
	/**
	 * 查询资产对应组合的可用持仓信息
	 *
	 * @param dto
	 * @return 可用持仓
	 * @author dubaojun
	 */
	@ApiOperation(value = "查询资产对应组合的可用持仓信息", notes = "查询资产对应组合的可用持仓信息")
	@ResponseBody
	@PostMapping("/selectAvailableNum")
	public Message<String> selectAvailableNum(@RequestBody PositionCashSecDto dto){
		try{
			BigDecimal availableNum = etrTradeCommonervice.selectAvailableNum(dto);
			if(availableNum == null) {
				availableNum= BigDecimal.ZERO;
			}
			String availableNumStr=availableNum.setScale(2,RoundingMode.HALF_UP).toString();
			return new Message<>(availableNumStr);
		} catch (Exception e) {
			logger.error("查询功能异常", e);
			return new Message<String>().fail().setMessage("查询异常");
		}
	}

	/**
	 * 查询组合对应账户的可用现金信息
	 *
	 * @param dto
	 * @return 可用现金
	 * @author dubaojun
	 */
	@ApiOperation(value = "查询组合对应账户的可用现金信息", notes = "查询组合对应账户的可用现金信息")
	@ResponseBody
	@PostMapping("/selectAccAvailableCash")
	public Message<String>  selectAccAvailableCash(@RequestBody PositionCashSecDto dto){
		try{
			BigDecimal accAvailableCash = etrTradeCommonervice.selectAccAvailableCash(dto);
			if(accAvailableCash == null) {
				accAvailableCash= BigDecimal.ZERO;
			}
			String accAvailableCashStr=accAvailableCash.setScale(2).toString();
			return new Message<>(accAvailableCashStr);
		} catch (Exception e) {
			logger.error("查询功能异常", e);
			return new Message<String>().fail().setMessage("查询异常");
		}
	}

	/**
	 * 查询债券对应的最新面值
	 *
	 * @param param
	 * @return 最新面值
	 * @author dubaojun
	 */
	@ResponseBody
	@RequestMapping("/selectSecFaceValue")
	public Message<BigDecimal> selectSecFaceValue(@RequestBody SecFaceValueRequest request){
		Map<String, Object> param = BeanUtil.object2Map(request);
		BigDecimal faceValue = etrExchangeTradeSecRepo001Service.selectSecFaceValue(param);
		if(faceValue == null) {
			faceValue= BigDecimal.ZERO;
		}
		return new Message<BigDecimal>().success().setData(faceValue);
	}

	/**
	 * 1.6计算偏离度
	 *
	 * @param request 画面参数
	 * @return 偏离度
	 * @see
	 * @since 1.0
	 */
	@PostMapping("/calcDeviationDegree")
	@ResponseBody
	public Message<String> calcDeviationDegree(@RequestBody CalcDeviationDegreeRequest request) {
		String deviationDegree = etrExchangeTradeSecRepo001Service.calcDeviationDegree(request);
		return new Message<String>().success().setData(deviationDegree);
	}


	/**
	 * 获取最小交易单元
	 * @param request
	 * @return
	 */
	@ApiOperation(value = "获取最小交易单元", notes = "获取最小交易单元")
	@ResponseBody
	@PostMapping("/getMinNumStep")
	public Message<String> getMinNumStep(@RequestBody MinNumStepRequest request){
		try{
			Map<String, Object> param = BeanUtil.object2Map(request);
			BigDecimal minNum = etrExchangeTradeSecRepo001Service.getMinNumStep(param);
			if(minNum == null) {
				minNum= BigDecimal.ZERO;
			}
			String minNumStr=minNum.setScale(2).toString();
			return new Message<>(minNumStr);
		} catch (Exception e) {
			logger.error("查询功能异常", e);
			return new Message<String>().fail().setMessage("查询异常");
		}
	}

	/**
	 * 保存按钮点击时的check
	 *
	 * @param  dto
	 * @param  br
	 * @return check结果
	 * @see
	 * @since 1.0
	 */
	@ApiOperation(value = "保存提交check", notes = "保存提交check")
	@PostMapping("checkSaveError")
	@ResponseBody
	public Message<AjaxResult> checkSaveError(@RequestBody @Validated({ ValidateGroupEdit.class }) EtrExchangeTradeSaveSecRepoRequest dto, BindingResult br)
			throws Exception {
		AjaxResult ajax=null;
		try {
			if("dbclick".equals(dto.getOptType())){
				ajax=etrExchangeTradeSecRepo001Service.dbClickCheck(dto);
			}
			List<EtrExchangeTradePortfolioSecRepoRequest> portfolioList=dto.getPortfolioList();
			if(!CollectionUtils.isEmpty(portfolioList)) {
				for (int i = 0; i < portfolioList.size(); i++) {
					EtrExchangeTradePortfolioSecRepoRequest item = portfolioList.get(i);
					dto.setVdate(dto.getEntrustDate());
					dto.setMdate(dto.getEntrustDate());
					dto.setPortfolioId(item.getPortfolioId());
					dto.setAcctNo(item.getAcctNo());
					dto.setEntrustNum(item.getEntrustNum());
					dto.setEntrustAmt(item.getEntrustAmt());
					dto.setSecAcctId(item.getSecAcctId()); // 证券账户
					dto.setStockId(item.getSecAcctId());
					ajax = check(dto, br);
					if (null != ajax) {
						break;
					}
				}
			}
			if (null != ajax) {
				return new Message<AjaxResult>().success().setData(ajax);
			} else {
				return new Message<AjaxResult>().success().setMessage("校验通过");
			}
		}catch (Exception e) {
			e.printStackTrace();
			logger.error("多组合指令新建保存/提交校验异常",e);
			return new Message<AjaxResult>().fail().setMessage("校验失败");
		}
	}

	/**
	 * 单组合保存按钮点击时的check
	 *
	 * @param  dto
	 * @param  br
	 * @return check结果
	 * @see
	 * @since 1.0
	 */
	@ResponseBody
	@RequestMapping("check")
	public AjaxResult check(@RequestBody EtrExchangeTradeSaveSecRepoRequest dto, BindingResult br)
			throws Exception {
		AjaxResult ajax= etrExchangeTradeSecRepo001Service.checkSave(dto,br);
		List<ValidMessage> validList = etrExchangeTradeSecRepoValidatorService.valid(dto);
		List<ValidMessage> validListPriceRange = etrExchangeTradeSecRepo001Service.checkSavePriceRange(dto,br);
		if (!CollectionUtils.isEmpty(validList)){
			ajax.setErrorList(validList);
		}

		String confirmMsg = "";
		// 有效价格申报范围控制参数
		String priceScopeControllEtr = CacheUtil.getSysParam("PRICE_SCOPE_CONTROLL_ETR");
		if (!CollectionUtils.isEmpty(validListPriceRange)){
			// 禁止
			if(StringUtil.equals(priceScopeControllEtr,"01")){
				ajax.setErrorList(validListPriceRange);
			}
			// 仅提示
			if(StringUtil.equals(priceScopeControllEtr,"02")){
				for(ValidMessage validMessage:validListPriceRange){
					confirmMsg += validMessage.getMessage();
					confirmMsg += "<br />";
				}
			}
		}
		if (ajax != null && !StringUtil.isEmptyList(ajax.getErrorList()) && !StringUtil.equals(dto.getOperateType(),"save")) {
			return ajax;
		}
		AjaxResult ajaxResult = new AjaxResult();
		if (StringUtil.isNotEmpty(ajax) && StringUtil.isNotEmpty(ajax.getMessageType())
				&& StringUtil.isNotEmpty(ajax.getData())) {
			if (ajax.getMessageType().equals("错误消息")){
				ajaxResult.setMessageType(MessageType.错误消息);
			}
			ajaxResult.setData(ajax.getData());
			return ajaxResult;
		}
		if (StringUtil.isNotEmpty(confirmMsg)) {
			ajaxResult.setMessageType(MessageType.确认消息);
			ajaxResult.setData(confirmMsg);
		}
		return ajaxResult;
	}
	/**
	 * 保存/提交
	 *
	 * @param dto
	 *            dto
	 * @param br
	 *            br
	 * @return 结果
	 * @throws Exception
	 *             异常
	 */
	@ApiOperation(value = "保存提交", notes = "保存提交")
	@PostMapping("save")
	@ResponseBody
	public Message<AjaxResult> save(@RequestBody EtrExchangeTradeSaveSecRepoRequest dto, BindingResult br){
		try {
			if (br.hasErrors()) {
				return new Message<AjaxResult>().fail().setData(new AjaxResult(br));
			}
			String msg ="";
			String businessType = etrExchangeTradeSecRepo001Service.setEntrustBussType(dto);
			dto.setBusinessTypeName(SysDataUtil.getNameFromDataDict(DictCode.共通业务类型, businessType));
			if (StringUtil.equals("save", dto.getOperateType())) {  //保存没有check和任何限制
				if(!CollectionUtils.isEmpty(dto.getPortfolioList())){
					boolean flag=false;
					for(EtrExchangeTradePortfolioSecRepoRequest temp:dto.getPortfolioList()){
						if(!StringUtil.isNotEmpty(temp.getAcctNo())){
							flag=true;
							break;
						}
					}
					Map<String,List<EtrExchangeTradePortfolioSecRepoRequest>> groupMap=null;
					if(!flag){
						groupMap= dto.getPortfolioList().stream().collect(
								Collectors.groupingBy(item->item.getAcctNo()));
					}
							//如果分组Map，key的数量>1,说明是多组合指令，key的数量=1,则为系列户指令
					if(groupMap!=null&&groupMap.keySet().size() == 1 && dto.getPortfolioList().size()>1) {
						//业务编号生成规则参数
						if (!StringUtil.isNotEmpty(dto.getEntrustId())) {
							BussinessCodeParameter param = etrExchangeTradeSecRepo001Service.genBussinessCodeParameter(dto);
							String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
							dto.setEntrustId(code);
						}
						if (StringUtil.isNotEmpty(dto.getIsDoRkRedisFlag()) && StringUtil.equals(dto.getIsDoRkRedisFlag(),"true")) {
							// ----------add by zhangrui1 20200703 预占功能 start----------
							String finprodType2 = DictCodeList.平安资产类型_质押式回购.getValue();
							//债转股需要走批量接口
							String infCode = BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue();
							dto.setFromEtrExchange("Y");//场内指令
							AjaxResult ar = ExchangeEntrustRdsCommonAop.doPreOccupy(dto, infCode, finprodType2);
							if (StringUtil.isNotEmpty(ar)) {
								return new Message<AjaxResult>().success().setData(ar);
							}
						}
						ProcessParameter<EtrExchangeTradeSaveSecRepoRequest> processParameter = new ProcessParameter<EtrExchangeTradeSaveSecRepoRequest>();
						WorkFlowParameter<EtrExchangeTradeSaveSecRepoRequest> params = new WorkFlowParameter<EtrExchangeTradeSaveSecRepoRequest>();
						params.setPageId(dto.getPageId());
						params.setElementId(dto.getElementId());
						params.setBusinessType(businessType);
						params.setBusinessCode(dto.getEntrustId());
						String tempBusinessTypeName=StringUtils.isNotEmpty(dto.getBusinessTypeName())?dto.getBusinessTypeName():"";
						params.setComment(entity -> getMessage("EtrExchangeTrade003.entrustId") + "(" + entity.getEntrustId() + ")"
								+ "-" + "业务类型" + "(" + tempBusinessTypeName + ")-"
								+ "平台日" + "(" + DateUtil.format(iPlatformDate.getSysDate()) + ")");
						processParameter.setWorkFlowParameter(params);
						msg = etrExchangeTradeSecRepo001Service.start(processParameter, dto);
					}
					//多组合指令
					else {
						List<EtrExchangeTradeSaveSecRepoRequest> etrExchangeTrade001DtoList = new ArrayList<>();
						for(EtrExchangeTradePortfolioSecRepoRequest item:dto.getPortfolioList()) {
							EtrExchangeTradeSaveSecRepoRequest etrExchangeTrade001Dto = etrExchangeTradeSecRepo001Service.genEtrExchangeTradeSaveSecRepoRequest(dto,item);
							etrExchangeTrade001Dto.setFinprodType2(DictCodeList.平安资产类型_质押式回购.getValue());
							if (!StringUtil.isNotEmpty(etrExchangeTrade001Dto.getEntrustId())) {
								BussinessCodeParameter param = etrExchangeTradeSecRepo001Service.genBussinessCodeParameter(dto);
								String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
								etrExchangeTrade001Dto.setEntrustId(code);
							}
							etrExchangeTrade001Dto.setInvManagerId(LoginUserUtil.getUserName());// 投资经理
							etrExchangeTrade001Dto.setCreateUser(LoginUserUtil.getUserName());// 用户名
							etrExchangeTrade001Dto.setCreateDept(LoginUserUtil.getDeptCode());// 用户部门
							etrExchangeTrade001Dto.setCreateTime(new Date());// 创建时间
							etrExchangeTrade001Dto.setBusinessTypeName(businessType);//此处存的是类型
							etrExchangeTrade001Dto.setIsMulti(DictCodeList.是否_否.getValue());
							etrExchangeTrade001Dto.setFromEtrExchange("Y");
							etrExchangeTrade001Dto.setDisRate(item.getDisRate());
							etrExchangeTrade001Dto.setOptType(dto.getOptType());
							List<EtrExchangeTradePortfolioSecRepoRequest> portfolioList=new ArrayList<>();
							EtrExchangeTradePortfolioSecRepoRequest portfolioRequest=new EtrExchangeTradePortfolioSecRepoRequest();
							BeanUtil.copyPropertiesIgnoreNull(portfolioRequest,etrExchangeTrade001Dto);
							portfolioList.add(portfolioRequest);
							etrExchangeTrade001Dto.setPortfolioList(portfolioList);

							etrExchangeTrade001DtoList.add(etrExchangeTrade001Dto);
						}
						if (StringUtil.isNotEmpty(dto.getIsDoRkRedisFlag()) && StringUtil.equals(dto.getIsDoRkRedisFlag(),"true")) {
							// ----------预占功能 start----------
							String infCode = BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue();
							// add by zhangrui1 start
							AjaxResult ar = ExchangeEntrustRdsCommonAop.doListPreOccupy("EtrExchangeTradeMulti001", etrExchangeTrade001DtoList,
									infCode);
							ExchangeEntrustRdsCommonAop.riskListToChildren(dto.getRiskTrialRetDto(), etrExchangeTrade001DtoList,
									(etrExchangeTrade001Dto, list) -> etrExchangeTrade001Dto.setRiskTrialRetDto(list));
							// ----------预占功能 end----------
						}
						ProcessParameter<EtrExchangeTradeSaveSecRepoRequest> processParameter = new ProcessParameter<EtrExchangeTradeSaveSecRepoRequest>();
						WorkFlowParameter<EtrExchangeTradeSaveSecRepoRequest> params = etrExchangeTradeSecRepo001Service.getWorkFlowParameter(dto);
						params.setPageId(dto.getPageId());
						params.setElementId(dto.getElementId());
						params.setBusinessType(businessType);
						params.setBusinessCode(item->item.getEntrustId());
						params.setComment(entity -> getMessage("EtrExchangeTrade003.entrustId") + "(" + entity.getEntrustId() + ")"
								+ "-" + "业务类型" + "(" + dto.getBusinessTypeName() + ")-"
								+ "平台日" + "(" + DateUtil.format(iPlatformDate.getSysDate()) + ")");
						processParameter.setWorkFlowParameter(params);
						msg = etrExchangeTradeSecRepo001Service.start(processParameter, etrExchangeTrade001DtoList.toArray(new EtrExchangeTradeSaveSecRepoRequest[etrExchangeTrade001DtoList.size()]));
					}
					return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
				}else{
						if (!StringUtil.isNotEmpty(dto.getEntrustId())) {
							BussinessCodeParameter param = etrExchangeTradeSecRepo001Service.genBussinessCodeParameter(dto);
							String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
							dto.setEntrustId(code);
						}
						//业务编号生成规则参数
						ProcessParameter<EtrExchangeTradeSaveSecRepoRequest> processParameter = new ProcessParameter<EtrExchangeTradeSaveSecRepoRequest>();
						WorkFlowParameter<EtrExchangeTradeSaveSecRepoRequest> params = etrExchangeTradeSecRepo001Service.getWorkFlowParameter(dto);
						params.setPageId(dto.getPageId());
						params.setElementId(dto.getElementId());
						params.setBusinessType(businessType);
						params.setBusinessCode(dto.getEntrustId());
						String entrustIdName=StringUtils.isNotEmpty(dto.getEntrustId())?dto.getEntrustId():"";
						params.setComment(entity -> getMessage("EtrExchangeTrade003.entrustId") + "(" + entrustIdName + ")"
								+ "-" + "业务类型"+ "(" + entity.getBusinessTypeName() + ")-"
								+ "平台日" + "(" + DateUtil.format(iPlatformDate.getSysDate()) + ")");
						processParameter.setWorkFlowParameter(params);
						msg = etrExchangeTradeSecRepo001Service.start(processParameter, dto);
						return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
					}

			}
			Map<String,List<EtrExchangeTradePortfolioSecRepoRequest>> groupMap= dto.getPortfolioList().stream().collect(
					Collectors.groupingBy(item->item.getAcctNo()));			//如果分组Map，key的数量>1,说明是多组合指令，key的数量=1,则为系列户指令
			if(groupMap.keySet().size() == 1 && dto.getPortfolioList().size()>1) {
				EtrExchangeTradePortfolioSecRepoRequest item=dto.getPortfolioList().get(0);
				//业务编号生成规则参数
				if (!StringUtil.isNotEmpty(dto.getEntrustId())) {
					BussinessCodeParameter param = etrExchangeTradeSecRepo001Service.genBussinessCodeParameter(dto);
					String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
					dto.setEntrustId(code);
				}
				BigDecimal totalEntrustAmt = null;
				if(dto.getPortfolioList().get(0).getEntrustAmt()!=null){
					totalEntrustAmt = dto.getPortfolioList().stream().map(EtrExchangeTradePortfolioSecRepoRequest::getEntrustAmt).reduce(BigDecimal::add).get();
				}
				BigDecimal totalEntrustNum = dto.getPortfolioList().stream().map(EtrExchangeTradePortfolioSecRepoRequest::getEntrustNum).reduce(BigDecimal::add).get();
				//算比例
				BigDecimal tempSumDisRate=BigDecimal.ZERO;
				for(int i=0;i<dto.getPortfolioList().size();i++){
					EtrExchangeTradePortfolioSecRepoRequest tempItem=dto.getPortfolioList().get(i);
					BigDecimal disRate=BigDecimal.ZERO;
					if(i<dto.getPortfolioList().size()-1) {
						disRate=ArithmeticUtil.divide(tempItem.getEntrustNum(),totalEntrustNum,2);
						tempSumDisRate=tempSumDisRate.add(disRate);
					}else {
						disRate=ArithmeticUtil.subtract(new BigDecimal("1"),tempSumDisRate);
					}
					tempItem.setDisRate(disRate);
				}
				dto.setEntrustAmt(totalEntrustAmt);
				dto.setEntrustNum(totalEntrustNum);
				dto.setIsMulti(DictCodeList.是否_是.getValue());
				dto.setVdate(dto.getEntrustDate());
				dto.setMdate(dto.getEntrustDate());
				dto.setAcctNo(item.getAcctNo());
				dto.setSecAcctId(item.getSecAcctId());
				dto.setStockId(dto.getStockId());
				dto.setInvManagerId(LoginUserUtil.getUserName());// 投资经理
				dto.setCreateUser(LoginUserUtil.getUserName());// 用户名
				dto.setCreateDept(LoginUserUtil.getDeptCode());// 用户部门
				dto.setCreateTime(new Date());// 创建时间
				dto.setPortfolioId(item.getPortfolioId()); //让指令分发能查出来
				// ----------add by zhangrui1 20200703 预占功能 start----------
				if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
					List<EtrExchangeTradeSaveSecRepoRequest> etrExchangeTrade001DtoList = new ArrayList<>();
					for(EtrExchangeTradePortfolioSecRepoRequest item2:dto.getPortfolioList()) {
						EtrExchangeTradeSaveSecRepoRequest etrExchangeTrade001Dto = etrExchangeTradeSecRepo001Service.genEtrExchangeTradeSaveSecRepoRequest(dto,item2);
						if (dto.getPs().equals(DictCodeListExt3.交易方向_正回购.getValue())) {
							etrExchangeTrade001Dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
						} else if (dto.getPs().equals(DictCodeListExt3.交易方向_逆回购.getValue())) {
							etrExchangeTrade001Dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
						}
						if (!StringUtil.isNotEmpty(etrExchangeTrade001Dto.getEntrustId())) {
							BussinessCodeParameter param = etrExchangeTradeSecRepo001Service.genBussinessCodeParameter(dto);
							String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
							etrExchangeTrade001Dto.setEntrustId(code);
						}
						etrExchangeTrade001Dto.setInvManagerId(LoginUserUtil.getUserName());// 投资经理
						etrExchangeTrade001Dto.setCreateUser(LoginUserUtil.getUserName());// 用户名
						etrExchangeTrade001Dto.setCreateDept(LoginUserUtil.getDeptCode());// 用户部门
						etrExchangeTrade001Dto.setCreateTime(new Date());// 创建时间
						etrExchangeTrade001Dto.setBusinessTypeName(businessType);//此处存的是类型
						etrExchangeTrade001Dto.setIsMulti(DictCodeList.是否_否.getValue());
						etrExchangeTrade001Dto.setFromEtrExchange("Y");
						etrExchangeTrade001Dto.setDisRate(item2.getDisRate());
						List<EtrExchangeTradePortfolioSecRepoRequest> portfolioList=new ArrayList<>();
						EtrExchangeTradePortfolioSecRepoRequest portfolioRequest=new EtrExchangeTradePortfolioSecRepoRequest();
						BeanUtil.copyPropertiesIgnoreNull(portfolioRequest,etrExchangeTrade001Dto);
						portfolioList.add(portfolioRequest);
						etrExchangeTrade001Dto.setPortfolioList(portfolioList);

						etrExchangeTrade001DtoList.add(etrExchangeTrade001Dto);
					}
					String finprodType2 = DictCodeList.平安资产类型_质押式回购.getValue();
					String infCode;
					infCode = BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue();
					dto.setFromEtrExchange("Y");//场内指令
					AjaxResult ar = ExchangeEntrustRdsCommonAop.doListPreOccupy("EtrExchangeTradeMulti001", etrExchangeTrade001DtoList,
							infCode);
					if (StringUtil.isNotEmpty(ar)) {
						return new Message<AjaxResult>().success().setData(ar);
					}
					ExchangeEntrustRdsCommonAop.riskListToChildren(dto.getRiskTrialRetDto(), etrExchangeTrade001DtoList,
							(etrExchangeTrade001Dto, list) -> etrExchangeTrade001Dto.setRiskTrialRetDto(list));
				}
				ProcessParameter<EtrExchangeTradeSaveSecRepoRequest> processParameter = new ProcessParameter<EtrExchangeTradeSaveSecRepoRequest>();
				WorkFlowParameter<EtrExchangeTradeSaveSecRepoRequest> params = new WorkFlowParameter<EtrExchangeTradeSaveSecRepoRequest>();
				params.setPageId(dto.getPageId());
				params.setElementId(dto.getElementId());
				params.setBusinessType(businessType);
				params.setBusinessCode(dto.getEntrustId());
				params.setComment(entity -> getMessage("EtrExchangeTrade003.entrustId") + "(" + entity.getEntrustId() + ")"
						+ "-" + "业务类型" + "(" + dto.getBusinessTypeName() + ")-"
						+ "平台日" + "(" + DateUtil.format(iPlatformDate.getSysDate()) + ")");
				processParameter.setWorkFlowParameter(params);
				try {
		        	msg = etrExchangeTradeSecRepo001Service.start(processParameter, dto);
		            AjaxResult m =new AjaxResult(msg);
		            return new Message<AjaxResult>().success().setData(m);
		        }catch (Exception e) {
		            logger.error("场内交易指令新建失败",e);
		            return PositionUtil.ajaxErrorReturn(e);
		        }
			}
			//多组合指令
			else {
				List<EtrExchangeTradeSaveSecRepoRequest> etrExchangeTrade001DtoList = new ArrayList<>();
				for(EtrExchangeTradePortfolioSecRepoRequest item:dto.getPortfolioList()) {
					EtrExchangeTradeSaveSecRepoRequest etrExchangeTrade001Dto = etrExchangeTradeSecRepo001Service.genEtrExchangeTradeSaveSecRepoRequest(dto,item);
					if (dto.getPs().equals(DictCodeListExt3.交易方向_正回购.getValue())) {
						etrExchangeTrade001Dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
					} else if (dto.getPs().equals(DictCodeListExt3.交易方向_逆回购.getValue())) {
						etrExchangeTrade001Dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
					}
					if (!StringUtil.isNotEmpty(etrExchangeTrade001Dto.getEntrustId())) {
						BussinessCodeParameter param = etrExchangeTradeSecRepo001Service.genBussinessCodeParameter(dto);
						String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
						etrExchangeTrade001Dto.setEntrustId(code);
					}
					etrExchangeTrade001Dto.setInvManagerId(LoginUserUtil.getUserName());// 投资经理
					etrExchangeTrade001Dto.setCreateUser(LoginUserUtil.getUserName());// 用户名
					etrExchangeTrade001Dto.setCreateDept(LoginUserUtil.getDeptCode());// 用户部门
					etrExchangeTrade001Dto.setCreateTime(new Date());// 创建时间
					etrExchangeTrade001Dto.setBusinessTypeName(businessType);//此处存的是类型
					etrExchangeTrade001Dto.setIsMulti(DictCodeList.是否_否.getValue());
					etrExchangeTrade001Dto.setFromEtrExchange("Y");
					etrExchangeTrade001Dto.setDisRate(item.getDisRate());
					List<EtrExchangeTradePortfolioSecRepoRequest> portfolioList=new ArrayList<>();
					EtrExchangeTradePortfolioSecRepoRequest portfolioRequest=new EtrExchangeTradePortfolioSecRepoRequest();
					BeanUtil.copyPropertiesIgnoreNull(portfolioRequest,etrExchangeTrade001Dto);
					portfolioList.add(portfolioRequest);
					etrExchangeTrade001Dto.setPortfolioList(portfolioList);
					//设置头寸使用序列号
					Long positionSeqno = (long) Arrays.deepHashCode(new Object[] { etrExchangeTrade001Dto.getEntrustId(), "ETR",new Date().getTime(),Thread.currentThread().getId() });
					etrExchangeTrade001Dto.setPositionSeqno(positionSeqno);
					etrExchangeTrade001DtoList.add(etrExchangeTrade001Dto);
				}

				// ----------预占功能 start----------
				if (StringUtil.equals(BussinessConstant.SUBMIT, dto.getOperateType())) {
					String infCode = BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue();
					// add by zhangrui1 start
					AjaxResult ar = ExchangeEntrustRdsCommonAop.doListPreOccupy("EtrExchangeTradeMulti001", etrExchangeTrade001DtoList,
							infCode);
					ExchangeEntrustRdsCommonAop.riskListToChildren(dto.getRiskTrialRetDto(), etrExchangeTrade001DtoList,
							(etrExchangeTrade001Dto, list) -> etrExchangeTrade001Dto.setRiskTrialRetDto(list));
				}
				// ----------预占功能 end----------

				ProcessParameter<EtrExchangeTradeSaveSecRepoRequest> processParameter = new ProcessParameter<EtrExchangeTradeSaveSecRepoRequest>();
				WorkFlowParameter<EtrExchangeTradeSaveSecRepoRequest> params = etrExchangeTradeSecRepo001Service.getWorkFlowParameter(dto);
				params.setPageId(dto.getPageId());
				params.setElementId(dto.getElementId());
				params.setBusinessType(businessType);
				params.setBusinessCode(item->item.getEntrustId());
				params.setComment(entity -> getMessage("EtrExchangeTrade003.entrustId") + "(" + entity.getEntrustId() + ")"
						+ "-" + "业务类型" + "(" + dto.getBusinessTypeName() + ")-"
						+ "平台日" + "(" + DateUtil.format(iPlatformDate.getSysDate()) + ")");
				processParameter.setWorkFlowParameter(params);
				try {
					msg = etrExchangeTradeSecRepo001Service.start(processParameter, etrExchangeTrade001DtoList.toArray(new EtrExchangeTradeSaveSecRepoRequest[etrExchangeTrade001DtoList.size()]));
		            AjaxResult m =new AjaxResult(msg);
		            return new Message<AjaxResult>().success().setData(m);
		        }catch (Exception e) {
		        	for(EtrExchangeTradeSaveSecRepoRequest etrExchangeTradeSaveSecRepoRequest :etrExchangeTrade001DtoList) {
		        		logger.warn("场内交易多组合指令头寸发送失败，EntrustId:{},PositionSeqno:{}", etrExchangeTradeSaveSecRepoRequest.getEntrustId(), etrExchangeTradeSaveSecRepoRequest.getPositionSeqno().toString());
		        		PositionHttpRequest.sendRevoke(etrExchangeTradeSaveSecRepoRequest.getPositionSeqno().toString(), "ETR", "场内交易多组合指令头寸发送失败");
		        	}
		            logger.error("场内交易多组合指令新建失败",e);
		            return PositionUtil.ajaxErrorReturn(e);
		        }
			}
		}catch (Exception e){
			e.printStackTrace();
			return new Message<AjaxResult>().fail().setData(new AjaxResult("保存失败"));
		}
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
	@ResponseBody
	@RequestMapping("/calcEntrustNumAndAmt")
	public Map<String, BigDecimal> calcEntrustNumAndAmt(@RequestBody Map<String, Object> param){
		return etrExchangeTradeSecRepo001Service.calcEntrustNumAndAmt(param);
	}

	/**
	 * 获取1/最小价差的倍数
	 * @param param
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getInterfSysMinPriceAll")
	public List<MinPirceDto> getInterfSysMinPriceAll(@RequestBody Map<String, Object> param) {
		List<MinPirceDto> minPirceList = etrExchangeTradeSecRepo001Service.getInterfSysMinPriceAll(param);
		return minPirceList;
	}

	/***
	 * 取更新时间最新记录.投资目的  指令业务类型 = 数据字典.委托类型.大宗债券指令
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getInvAim")
	public String getInvAim() {
		return etrExchangeTradeSecRepo001Service.getInvAim();
	}

	/**
	 *
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/selectBondExecPlan")
	public int selectBondExecPlan() throws Exception {
		String param = super.getRequest().getParameter("param");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("param", param);
		return etrExchangeTradeSecRepo001Service.selectBondExecPlan(map);
	}

	/**
	 * 折算率如果取不到值，默认100%
	 *
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("/selectStdsecConverseRatio")
	public Message<BigDecimal> selectStdsecConverseRatio(@RequestBody StdsecConverseRatioRequest request) throws Exception {
		try{
			Map<String, Object> param = BeanUtil.object2Map(request);
			String converseRatio = etrExchangeTradeSecRepo001Service.selectStdsecConverseRatio(param);
			return new Message<BigDecimal>().success().setData(new BigDecimal(converseRatio));
		}catch (Exception e){
			return new Message<BigDecimal>().fail().setData(null);
		}
	}

	/***
	 * 初始化行情
	 * @return
	 */
	@RequestMapping(value = "/initHq")
	public String initHq(Model model){
		return "invest/entrust/etrexchangetrade/EtrExchangeTradeHq";
	}

	/**
	 * 查询交易行情信息
	 * <一句话功能简述>
	 * @return
	 * @see
	 * @since 1.0
	 */
	@ApiOperation(value="查询交易行情信息",notes = "查询交易行情信息")
	@ResponseBody
	@PostMapping("findQuotations")
	public Message<EtrExchangeTradeQuotationHqSecRepoResponse> findQuotations(@RequestBody String secId) {
		try {
			secId = secId.replaceAll("\"","");
			EtrExchangeTradeQuotationHqSecRepoResponse quotations = etrExchangeTradeSecRepo001Service.findQuotations(secId);
			return new Message<EtrExchangeTradeQuotationHqSecRepoResponse>().success().setData(quotations);
		}catch (Exception e) {
			logger.error("查询交易行情数据异常",e);
			return new Message<EtrExchangeTradeQuotationHqSecRepoResponse>().fail().setMessage("查询交易行情信息数据异常");
		}
	}


	/**
	 * 查询指令组合列表
	 *
	 * @param dto dto
	 * @return 处理结果
	 * @throws Exception
	 *             异常
	 */
	@ApiOperation(value = "查询指令组合列表", notes = "查询指令组合列表")
	@PostMapping("/selectEtrPortfolioList")
	@ResponseBody
	public Message<List<EtrExchangeTradeSaveSecRepoRequest>>  selectEtrPortfolioList(@RequestBody EtrExchangeTradeSaveSecRepoRequest dto )  {
		try{
			List<EtrExchangeTradeSaveSecRepoRequest> list=etrExchangeTradeSecRepo001Service.selectEtrPortfolioList(dto);
			return new Message<List<EtrExchangeTradeSaveSecRepoRequest>>().success().setData(list);
		}catch (Exception e){
			return new Message<List<EtrExchangeTradeSaveSecRepoRequest>>().fail().setData(null);
		}
	}

	/**
	 * 审核画面查询多组合明细
	 * @param
	 * @return
	 */
	@ResponseBody
	@PostMapping("selectEtrPortfolioDetail")
	public List<EtrExchangeTradeSaveSecRepoRequest> selectEtrPortfolioDetail() {
		Map<String,Object> pageParam = RequestUtil.getPageParam();
		EtrExchangeTradeSaveSecRepoRequest dto=BeanUtil.mapToObject(pageParam, EtrExchangeTradeSaveSecRepoRequest.class);
		return etrExchangeTradeSecRepo001Service.selectWflEtrPortfolioList(dto);
	}

	@ApiOperation(value = "获取服务器时间", notes = "获取服务器时间")
	@ResponseBody
	@RequestMapping(value = "queryLocalTime", method = RequestMethod.POST)
	public Message<Date> queryLocalTime() {
		return new Message<Date>(DateUtil.now());
	}

	/**
	 * 获取1/最小价差的倍数
	 * @param request
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/getMinPrice")
	public Message<List<MinPirceDto>> getMinPrice(@RequestBody MinPriceRequest request) {
		try{
			Map<String,Object> param= JsonUtils.object2Map(request);
			List<MinPirceDto> minPirceList = etrExchangeTradeSecRepo001Service.getInterfSysMinPriceAll(param);
			return new Message<List<MinPirceDto>>().success().setData(minPirceList);
		}catch (Exception e){
			return new Message<List<MinPirceDto>>().fail().setData(null);
		}
	}

	@ApiOperation(value = "头寸信息查询", notes = "头寸信息查询")
	@PostMapping("/queryPositionInfo")
	@ResponseBody
	public Message<List<BaseMap>> queryPositionInfo(@RequestBody PositionCashSecRequest requestDto) {
		try {
			for(PositionCashSecDto positionCashSecDto:requestDto.getPtlList()){
				positionCashSecDto.setFinprodId("XN_PRODID999");
			}
			List<BaseMap> m = etrTradeCommonervice.queryPositionInfo(requestDto);
			if(m.get(3)!=null){
				m.get(3).put("name","标准券可用额度（张）");
			}
			if(m.get(4)!=null){
				m.get(4).put("name","标准券总额度（张）");
			}

			return new Message<List<BaseMap>>().success().setData(m);
		} catch (Exception e) {
			e.printStackTrace();
			return new Message<List<BaseMap>>().fail().setMessage("查询失败");
		}
	}
	@ApiOperation(value = "最小价格", notes = "最小价格")
	@PostMapping("/getMinTrdUnitLimit")
	@ResponseBody
	public Message<MinTrdUnitLimitDto> getMinTrdUnitLimit(@RequestBody EtrExchangeMiniLimitPriceRequest request){
		try {
			MinTrdUnitLimitDto minNum= etrExchangeTradeSecRepo001Service.getMinTrdUnitLimitPrice(request);
			return new Message<MinTrdUnitLimitDto>().success().setData(minNum);
		} catch (Exception e) {
			e.printStackTrace();
			return new Message<MinTrdUnitLimitDto>().fail().setMessage("查询失败");
		}
	}
}
	
