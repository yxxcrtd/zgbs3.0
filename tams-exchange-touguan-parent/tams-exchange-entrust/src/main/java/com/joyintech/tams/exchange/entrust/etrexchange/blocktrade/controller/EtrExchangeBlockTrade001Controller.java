/*
 * 文件名：EtrTradePlace001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2021年12月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.exchange.entrust.etrexchange.blocktrade.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.exchange.entrust.etrexchange.blocktrade.dto.EtrExchangeTradeQuotationRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.blocktrade.dto.EtrExchangeTradeQuotationResponse;
import com.joyintech.tams.exchange.entrust.etrexchange.blocktrade.dto.SelectOptionsRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.blocktrade.service.EtrExchangeBlockTrade001Service;
import com.joyintech.tams.exchange.entrust.etrexchange.blocktrade.service.EtrExchangeBlockTrade001ValidatorService;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dao.EtrExchangeTradeCommonMapper;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommon001Service;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.EtrExchangeTrade002Dto;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.EtrExchangeTradeQuotation001Dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

/**
 * 大宗交易所指令交易（债券股票回购基金场内指令交易）
 * @author wangyang1
 * @version 1.0 2021年12月28日
 * @see EtrExchangeBlockTrade001Controller
 * @since 1.0
 */
@Controller
@PageId("EtrExchangeBlockTrade")
@RequestMapping("/etrexchange/blocktrade/etrExchangeBlockTrade001")
@Api(tags = "大宗交易指令管理")
public class EtrExchangeBlockTrade001Controller extends BaseController{

	/**
	 * 日志
	 */
	private Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired
	private EtrExchangeBlockTrade001Service etrExchangeBlockTrade001Service;

	@Autowired
	EtrExchangeTradeCommon001Service etrExchangeTrade001Service;

	@Autowired
	private EtrExchangeTradeCommon001Service etrExchangeTradeCommon001Service;

	@Autowired
	private EtrExchangeBlockTrade001ValidatorService etrExchangeBlockTrade001ValidatorService;

	@Autowired
	private EtrExchangeTradeCommonMapper etrExchangeTradeCommonMapper;

	@ApiOperation(value="查询页面初始化参数", notes = "查询页面初始化参数")
	@PostMapping("/init")
	@ResponseBody
	public Message<Map<String,Object>> init() {
		Map<String, Object> initPageParams = new HashMap<>();
		initPageParams.put("TRADE_PLACE", this.getSelectDataFromDataDic(BussinessDictCode.交易场所, null));
		initPageParams.put("FINPROD_TYPE_TG", this.getSelectDataFromDataDic(DictCode.金融产品类型_投管用, null));
		initPageParams.put("PS", this.getSelectDataFromDataDic(DictCode.交易方向, null));
		initPageParams.put("DECLA_TYPE", this.getSelectDataFromDataDic(DictCode.申报类型, null));
		EtrExchangeTrade002Dto exchangeTradeDto002 = new EtrExchangeTrade002Dto();
		initPageParams.put("entity",exchangeTradeDto002);
		initPageParams.put("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
		initPageParams.put("DECLA_TYPE_PRM05", DictCodeListExt3.申报类型_不指定.getValue());
		initPageParams.put("DECLA_TYPE_PRM14", DictCodeListExt3.申报类型_指定对手方成交申报.getValue());
		initPageParams.put("ENTRUST_BUSS_TYPE_15", DictCodeListExt3.上农商_指令业务类型_债券质押式回购竞价指令.getValue());
		initPageParams.put("PS_T02", DictCodeList.交易方向_卖出.getValue());
		initPageParams.put("PS_T69", DictCodeListExt3.交易方向_质押券出库.getValue());
		initPageParams.put("TARGET_CLASS_01", DictCodeListExt3.目标类型_数量.getValue());
		initPageParams.put("PRICE_MODEL_PRM01", DictCodeListExt2.价格模式_指定价格.getValue());
		// 场内交易指令画面持仓列表自动刷新时间（毫秒）
		long etrExchange_posTable_refresh_period = 20000;
		String tempSysParaVal = CacheUtil.getSysParam(
				BussinessConstant.ETREXCHANGE_POSTABLE_REFRESH_PERIOD);
		if (StringUtil.isNotBlank(tempSysParaVal)
				&& !StringUtil.equals(BussinessConstant.ETREXCHANGE_POSTABLE_REFRESH_PERIOD,
				tempSysParaVal)) {
			try {
				etrExchange_posTable_refresh_period = Long.parseLong(tempSysParaVal);
			}
			catch (Exception e) {
			}
		}
		initPageParams.put("etrExchange_posTable_refresh_period", etrExchange_posTable_refresh_period);
		/*add by chenjiaxing 更改交易方向枚举值 start*/
		SelectDto selectDto1 = this.getSelectDataFromDataDic(DictCode.交易方向, null);
		for(int i = 0 ; i < selectDto1.getOptions().size() ; i++){
			if(StringUtil.equals(selectDto1.getOptions().get(i).getId(),"T03")){
				selectDto1.getOptions().get(i).setText("债回售");
			}
		}
		List<SelectOptionDto> optionsGradeResult = selectDto1.getOptions();
		initPageParams.put("PS_ETR", JsonUtils.objectArray2Json(optionsGradeResult.toArray()));
		/*add by chenjiaxing 更改交易方向枚举值 end*/
		return new Message<Map<String, Object>>(initPageParams).success();
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
	@PostMapping("findTradeQuotationsPage")
	public Message<Page<EtrExchangeTradeQuotationResponse>> findTradeQuotationsPage(@RequestBody EtrExchangeTradePositionsCommonRequest request){
		try {
			String secId = request.getSecId();
			Page<EtrExchangeTradeQuotationResponse> list = etrExchangeBlockTrade001Service.findTradeQuotationsPage(secId);
			return new Message<Page<EtrExchangeTradeQuotationResponse>>().success().setData(list);
		}catch (Exception e) {
			logger.error("查询指令管理分页数据异常",e);
			return new Message<Page<EtrExchangeTradeQuotationResponse>>().fail().setMessage("查询交易行情信息数据异常");
		}
	}

	/**
	 * 查询盘后行情信息
	 * <一句话功能简述>
	 * @return
	 * @see
	 * @since 1.0
	 */
	@ApiOperation(value="查询盘后行情信息",notes = "查询盘后行情信息")
	@ResponseBody
	@PostMapping("findAfterHoursPage")
	public Message<Page<EtrExchangeTradeQuotationResponse>> findAfterHoursPage(@RequestBody EtrExchangeTradePositionsCommonRequest request){
		try {
			String secId = request.getSecId();
			Page<EtrExchangeTradeQuotationResponse> page = etrExchangeBlockTrade001Service.findAfterHoursPage(secId);
			return new Message<Page<EtrExchangeTradeQuotationResponse>>().success().setData(page);
		}catch (Exception e) {
			logger.error("查询指令管理分页数据异常",e);
			return new Message<Page<EtrExchangeTradeQuotationResponse>>().fail().setMessage("查询盘后行情信息数据异常");
		}
	}

	/**
	 * 查询交易行情信息
	 * <一句话功能简述>
	 * @return
	 * @see
	 * @since 1.0
	 */
	@ApiOperation(value="导出交易行情信息",notes = "导出交易行情信息")
	@ResponseBody
	@PostMapping("exportTradeQuotationsPage")
	public void exportTradeQuotationsPage(@RequestBody EtrExchangeTradeExportCommonRequest requestDto, HttpServletResponse response){
		// 导出
		ExcelUitl.doExport(requestDto, response, "交易行情", () -> {
			EtrExchangeTradeEtrExchangeCommonRequest dto = requestDto.getDto(EtrExchangeTradeEtrExchangeCommonRequest.class);
            String secId = dto.getSecId();
			List dataList = etrExchangeBlockTrade001Service.findTradeQuotationsPage(secId).getRoot();
			return dataList;
		});
	}

	/**
	 * 查询盘后行情信息
	 * <一句话功能简述>
	 * @return
	 * @see
	 * @since 1.0
	 */
	@ApiOperation(value="导出盘后行情信息",notes = "导出盘后行情信息")
	@ResponseBody
	@PostMapping("exportAfterHoursPage")
	public void exportAfterHoursPage(@RequestBody EtrExchangeTradeExportCommonRequest requestDto, HttpServletResponse response){
	    // 导出
		ExcelUitl.doExport(requestDto, response, "盘后行情", () -> {
			EtrExchangeTradeEtrExchangeCommonRequest dto = requestDto.getDto(EtrExchangeTradeEtrExchangeCommonRequest.class);
			String secId = dto.getSecId();
			List dataList = etrExchangeBlockTrade001Service.findAfterHoursPage(secId).getRoot();
			return dataList;
		});
	}


	/**
	 * 查询盘后行情信息
	 * <一句话功能简述>
	 * @return
	 * @see
	 * @since 1.0
	 */
	@ApiOperation(value="查询盘后行情信息",notes = "查询盘后行情信息")
	@ResponseBody
	@PostMapping("findRepoPage")
	public Page<EtrExchangeTradeQuotation001Dto> findRepoPage(@RequestBody EtrExchangeTradeQuotationRequest request){
		Page<EtrExchangeTradeQuotation001Dto> page = new Page<EtrExchangeTradeQuotation001Dto>();
		return page;
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
	 * 查询交易对手
	 * @return
	 */
	@ApiOperation(value="查询交易对手信息",notes = "查询交易对手信息")
	@ResponseBody
	@PostMapping("/selectCounterIdByCounterType")
	public Message<List<SelectOptionDto>> selectCounterIdByCounterType(@RequestBody SelectOptionRequestDto dto){
		try {
			List<SelectOptionDto> list = etrExchangeTradeCommon001Service.selectCounterIdByCounterType(dto);
			return new Message<List<SelectOptionDto>>().success().setData(list);
		}catch (Exception e) {
			logger.error("查询交易对手数据异常",e);
			return new Message<List<SelectOptionDto>>().fail().setMessage("查询交易对手数据异常");
		}
	}

	/**
	 * 由于交易平台不同，设置不同交易平台对应的委托方向
	 * @param selectOptionsRequest
	 */
	@ApiOperation(value = "获取交易方向", notes = "获取交易方向")
	@PostMapping("/getPsSelectOptions")
	@ResponseBody
	public Message<List<SelectOptionDto>> getPsSelectOptions(@RequestBody SelectOptionsRequest selectOptionsRequest) {
		try {
			SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.交易方向, null);
			List<SelectOptionDto> tempList = etrExchangeBlockTrade001Service.getPsSelectOptions(selectOptionsRequest,selectDto);
			return new Message<List<SelectOptionDto>>().success().setData(tempList);
		} catch (Exception e) {
			logger.error("查询功能异常", e);
			return new Message<List<SelectOptionDto>>().fail().setMessage("查询异常");
		}
	}

	/**
	 * 由于交易平台不同，设置不同交易平台对应的申报类型
	 * @param  selectOptionsRequest
	 */
	@ApiOperation(value = "获取申报类型", notes = "获取申报类型")
	@PostMapping("/getDeclaTypeSelectOptions")
	@ResponseBody
	public Message<List<SelectOptionDto>> getDeclaTypeSelectOptions(@RequestBody SelectOptionsRequest selectOptionsRequest) {
		try {
			SelectDto selectDto = this.getSelectDataFromDataDic(DictCode.申报类型, null);
			List<SelectOptionDto> tempList = etrExchangeBlockTrade001Service.getDeclaTypeSelectOptions(selectOptionsRequest,selectDto);
			return new Message<List<SelectOptionDto>>().success().setData(tempList);
		} catch (Exception e) {
			logger.error("查询功能异常", e);
			return new Message<List<SelectOptionDto>>().fail().setMessage("查询异常");
		}
	}

	/**
	 * 指令列表
	 * @return
	 */
	@ApiOperation(value = " 查询指令列表分页数据",notes = "查询指令列表分页数据")
	@PostMapping("selectEtrExchange")
	@ResponseBody
	public Message<Page<EtrExchangeTradeEtrExchangeCommonResponse>> selectEtrExchange(@RequestBody EtrExchangeTradeEtrExchangeCommonRequest request){
		try {
			Page<EtrExchangeTradeEtrExchangeCommonResponse> result = etrExchangeBlockTrade001Service.selectEtrExchange(request);
			return new Message<Page<EtrExchangeTradeEtrExchangeCommonResponse>>().success().setData(result);
		} catch (Exception e) {
			logger.error("查询功能异常", e);
			return new Message<Page<EtrExchangeTradeEtrExchangeCommonResponse>>().fail().setMessage("查询异常");
		}
	}

	/**
	 * 导出信息excel
	 *
	 * @return excel
	 * @throws Exception 异常
	 * @see
	 * @since 1.0
	 */
	@PostMapping("exportEtrExchange")
	@ResponseBody
	@ApiOperation(value = "指令列表导出", notes = "指令列表导出")
	public void exportEtrExchange(@RequestBody EtrExchangeTradeExportCommonRequest requestDto, HttpServletResponse response) {
		// 导出
		ExcelUitl.doExport(requestDto, response, "指令列表", () -> {
			EtrExchangeTradeEtrExchangeCommonRequest dto = requestDto.getDto(EtrExchangeTradeEtrExchangeCommonRequest.class);
			List dataList = etrExchangeBlockTrade001Service.selectEtrExchange(dto).getRoot();
			return dataList;
		});
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
	public Message<AjaxResult> checkSaveError(@RequestBody EtrExchangeTradeSaveRequest dto, BindingResult br)
			throws Exception {
		AjaxResult ajax=null;
		try {
			if("dbclick".equals(dto.getOptType())){
				ajax=etrExchangeTradeCommon001Service.dbClickCheck(dto);
			}
			List<EtrExchangeTradePortfolioRequest> portfolioList=dto.getPortfolioList();
			if(!CollectionUtils.isEmpty(portfolioList)) {
				for (int i = 0; i < portfolioList.size(); i++) {
					EtrExchangeTradePortfolioRequest item = portfolioList.get(i);
					dto.setVdate(dto.getEntrustDate());
					dto.setMdate(dto.getEntrustDate());
					dto.setPortfolioId(item.getPortfolioId());
					dto.setAcctNo(item.getAcctNo());
					dto.setEntrustNum(item.getEntrustNum());
					dto.setEntrustAmt(item.getEntrustAmt());
					if (StringUtil.isNotEmpty(dto.getFinprodId()) && !StringUtil.isNotEmpty(dto.getStockId())) {
						dto.setStockId(dto.getFinprodId());
					}
					//默认证券账户
					if (StringUtil.isNotEmpty(item.getPortfolioId()) && StringUtil.isNotEmpty(dto.getFinprodId())) {
						Map<String, Object> map = new HashMap<String, Object>();
						map.put("portfolioId", item.getPortfolioId());
						map.put("finprodId", dto.getFinprodId());
						map.put("tradePlat", dto.getTradeMarket());
						String secAcctId = etrExchangeTradeCommonMapper.qryPortfolioSecAcctId(map);
						if (StringUtil.isNotEmpty(secAcctId)) {
							dto.setSecAcctId(secAcctId); // 证券账户
							dto.setStockId(secAcctId);
						}
					}
//					ajax= etrExchangeTradeCommon001Service.check(dto, br);
//					if (null != ajax) {
//						break;
//					}



                    List<ValidMessage> validList = etrExchangeBlockTrade001ValidatorService.valid(dto);


					if(validList.size()>0) {
						if (null == ajax) {
							ajax = new AjaxResult(br);
							ajax.setErrorList(validList);
						}
					}
					if (null != ajax) {
						break;
					}
				}
				//DZ校验是否闭市
				Boolean isNonTradingTime = etrExchangeTrade001Service.isNonTradingTime(dto);
				if (isNonTradingTime) {
						ajax = new AjaxResult(br);
						List<ValidMessage> validList = new ArrayList<>();
						validList.add(new ValidMessage("当日已闭市，不允许下指令!"));
						ajax.setErrorList(validList);
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
	 * 查询配股权益数量
	 * <一句话功能简述>
	 * @return
	 * @see
	 * @since 1.0
	 */
	@ApiOperation(value="查询配股权益数量",notes = "查询配股权益数量")
	@ResponseBody
	@PostMapping("getQutoEntrustNum")
	public Message<String> getFinProductQutoInfo(@RequestBody EtrExchangeTradeCommonRequest request) {
		try {
			Map<String,Object> param = BeanUtil.object2Map(request);
			BigDecimal entrustNum = etrExchangeTradeCommon001Service.getEntrustNum(param);
			if(entrustNum == null) {
				entrustNum= BigDecimal.ZERO;
			}
			String entrustNumStr=entrustNum.setScale(2).toString();
			return new Message<String>(entrustNumStr);
		}catch (Exception e) {
			logger.error("查询配股权益数量异常",e);
			return new Message<String>().fail().setMessage("查询配股权益数量异常");
		}
	}


	@ApiOperation(value="查询是否全价报送",notes = "查询是否全价报送")
	@ResponseBody
	@PostMapping("getIsFullPrice")
	public Message<String> getIsFullPrice(@RequestBody EtrExchangeTradeCommonRequest request){
		try {
			String finprodId = request.getFinprodId();
			String isFullPrice = etrExchangeBlockTrade001Service.getIsFullPrice(finprodId);
			return new Message<String>().success().setData(isFullPrice);
		}catch (Exception e) {
			logger.error("查询指令管理分页数据异常",e);
			return new Message<String>().setMessage("查询交易行情信息数据异常");
		}
	}

	@ApiOperation(value="查询净值偏离度",notes = "查询净值偏离度")
	@ResponseBody
	@PostMapping("calcDeviationDegree")
	public Message<String> calcDeviationDegree(@RequestBody EtrExchangeTradeCommonRequest request){
		try {
			Map<String,Object> param= JsonUtils.object2Map(request);
			String deviationDegree = etrExchangeBlockTrade001Service.calcDeviationDegree(param);
			return new Message<String>().success().setData(deviationDegree);
		}catch (Exception e) {
			logger.error("查询净值偏离度数据异常",e);
			return new Message<String>().setMessage("查询净值偏离度信息数据异常");
		}
	}

	/**
	 * 查询交易行情信息
	 * <一句话功能简述>
	 * @return
	 * @see
	 * @since 1.0
	 */
	@ApiOperation(value="查询竞价交易行情信息",notes = "查询竞价交易行情信息")
	@ResponseBody
	@PostMapping("findQuotations")
	public Message<EtrExchangeTradeQuotationHqResponse> findQuotations(@RequestBody EtrExchangeTradeEtrExchangeCommonRequest request) {
		try {
			if (!StringUtil.isNotEmpty(request) || !StringUtil.isNotEmpty(request.getSecId())){
				return new Message<EtrExchangeTradeQuotationHqResponse>().success().setData(new EtrExchangeTradeQuotationHqResponse());
			}
			String secId = request.getSecId();
			EtrExchangeTradeQuotationHqResponse quotations = etrExchangeTradeCommon001Service.findQuotations(secId);
			return new Message<EtrExchangeTradeQuotationHqResponse>().success().setData(quotations);
		}catch (Exception e) {
			logger.error("查询竞价易行情数据异常",e);
			return new Message<EtrExchangeTradeQuotationHqResponse>().fail().setMessage("查询竞价交易行情信息数据异常");
		}
	}
}
	
