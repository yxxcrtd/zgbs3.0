/*
 * 文件名：EtrTradePlaceService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月1日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etrexchangetrade.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.joyintech.calculation.CalculatorFactory;
import com.joyintech.calculation.bonds.PaperIrReset;
import com.joyintech.calculation.bonds.SecIntSchedule;
import com.joyintech.calculation.bonds.SecPrinSchedule;
import com.joyintech.calculation.bonds.constant.*;
import com.joyintech.calculation.shell.AccurIntCalculator;
import com.joyintech.calculation.shell.AccurIntForSecheduleCalculator;
import com.joyintech.calculation.shell.PriceByYieldCalculator;
import com.joyintech.calculation.shell.YieldByPriceCalculator;
import com.joyintech.calculation.shell.param.*;
import com.joyintech.calculation.util.BasicUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.base.intf.DataDictItem;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SelectDto;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.system.base.entity.SysUser;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.common.base.dao.FinProductAddMapper;
import com.joyintech.tams.common.base.dao.FinProductConvertibleMapper;
import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.component.business.bond.BusiFinExercisePlanService;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.invest.common.asset.dto.AssetGenerateEntry;
import com.joyintech.tams.invest.common.asset.dto.FinCashPlanDto;
import com.joyintech.tams.invest.common.asset.util.AssetCommonUtil;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.common.dto.position.PositionCashSecDto;
import com.joyintech.tams.invest.common.entrust.inter.EtrTradeCommonervice;
import com.joyintech.tams.invest.common.trade.AllTradeCommonUtil;
import com.joyintech.tams.invest.common.trade.dto.EtrSecPositionDto;
import com.joyintech.tams.invest.common.trade.dto.SecInfoModelDto;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.entrust.common.service.EtrCommonService;
import com.joyintech.tams.invest.entrust.etrexchangetrade.dto.*;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.joyintech.tams.invest.common.asset.service.BondAssetCalcService.*;

/**
 * 交易所指令交易（债券股票回购基金场内指令交易）
 *
 * @author chendijun
 * @version 1.0 2020年7月1日
 * @see EtrExchangeTrade001Service
 * @since 1.0
 */
@Service
public class EtrExchangeTrade001Service extends CommonExportService<EtrExchangeTrade001Dto>
		implements BaseProcessService<EtrExchangeTrade001Dto> {

	public static String queryIntegratedQuote = "/joyin/entrust/integrated/quote";

	public static String queryIntegratedTick = "/joyin/quote/tick";
	/**
	 * 指令共通Service
	 */
	@Autowired
	private EtrCommonService etrCommonService;

	@Autowired
	private EtrTradeCommonervice etrTradeCommonervice;

	@Autowired
	private IPlatformDate iPlatformDate;

	@Autowired
	private BusiFinExercisePlanService busiFinExercisePlanService;

	@Autowired
	FinProductConvertibleMapper finProductConvertibleMapper;

	@Autowired
	EtrExchangeTrade002Service etrExchangeTrade002Service;

	@Autowired
	FinProductAddMapper finProductAddMapper;

	@Autowired
	FinProductMapper finProductMapper;

	// modify by etliu 20220303 start
	// 质押式回购出入库计算时，原公式：标准券数量=质押数量*标准折算率，改成：标准券数量=质押数量*标准折算率*面值/100  ---需求张琼
	@Autowired
	private TrdCommonBaseService trdCommonBaseService;
	// modify by etliu 20220303 end

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doComplete(EtrExchangeTrade001Dto entity, OperationType arg1) {
		// TODO Auto-generated method stub
		Map<String,Object> map = new HashMap<>();
		// 新增交易对手席位
		if (StringUtil.isNotEmpty(entity.getCounterId()) && StringUtil.isNotEmpty(entity.getSeatId())){
			map.put("counterId",entity.getCounterId());
			map.put("counterSeatId",entity.getSeatId());
			restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/insertCounterSeatId", map,
					Object.class);
		}
		// 新增交易对手交易员
		if (StringUtil.isNotEmpty(entity.getCounterId()) && StringUtil.isNotEmpty(entity.getCounterTrader())){
			map.put("counterId",entity.getCounterId());
			map.put("counterTraderId",entity.getCounterTrader());
			restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/insertCounterTraderId", map,
					Object.class);
		}
		restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/doComplete", entity,
				Integer.class);
		doDeleteFlow(entity.getWorkflowId());
	}

	@Override
	public void doBack(EtrExchangeTrade001Dto entity){
		// nothing to do just for aspect
		//产品化_所有投资指令预占需求_场内交易  add by zhangpei 20201128 start
		AllTradeCommonUtil.deletePosition(null, entity.getEntrustId());
		//产品化_所有投资指令预占需求_场内交易  add by zhangpei 20201128 end
	}

	@Override
	public void doUpdate(EtrExchangeTrade001Dto entity){
		// nothing to do just for aspect
		Map<String,Object> map = new HashMap<>();
		// 新增交易对手席位
		if (StringUtil.isNotEmpty(entity.getCounterId()) && StringUtil.isNotEmpty(entity.getSeatId())){
			map.put("counterId",entity.getCounterId());
			map.put("counterSeatId",entity.getSeatId());
			restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/insertCounterSeatId", map,
					Object.class);
		}
		// 新增交易对手交易员
		if (StringUtil.isNotEmpty(entity.getCounterId()) && StringUtil.isNotEmpty(entity.getCounterTrader())){
			map.put("counterId",entity.getCounterId());
			map.put("counterTraderId",entity.getCounterTrader());
			restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/insertCounterTraderId", map,
					Object.class);
		}
		if (!StringUtil.equals("revoke", entity.getOperateType()) && !StringUtil.equals("delete", entity.getOperateType())) {
			// 先删除流程中数据
			doDeleteFlow(entity.getWorkflowId());
			// 重新insert
			doInsertFlow(entity);
			//add by zznn 20210824 start
			//保存不需要处理头寸
			if(!StringUtil.equals(entity.getOperateType(),"save")){
				//产品化_所有投资指令预占需求_场内交易  add by zhangpei 20201128 start
				doInsetOrUpdateForEtrPositionHold(entity);
				//产品化_所有投资指令预占需求_场内交易  add by zhangpei 20201128 end
			}
			//add by zznn 20210824 start
			ProcessMark newEntity;
			try {
				newEntity = WorkFlowUtil.getBusinessObjectFromRequest(EtrExchangeTrade001Dto.class, true);
				// 工作流ID
				newEntity.setWorkflowId(entity.getWorkflowId());
				newEntity.setStatus(entity.getStatus());
				newEntity.setBusinessData(JsonUtils.object2Json(entity));
				// 更新业务表数据
				// 操作留痕表
				WorkflowContext.getProcessMarkService().insertIntoProcessMark(newEntity);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 流程判断
	 */
	@Override
	public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
		EtrExchangeTrade001Dto dto = this.getBussinessEntity(entity);
		CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
		String 平安资产类型 = null;
		String finprodId = dto.getFinprodId();
		if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
			平安资产类型 = DictCodeList.平安资产类型_债券.getValue();
		}
		else if(StringUtil.equals(dto.getFinprodType2(), DictCodeListExt1.金融产品类型_投管用_基金.getValue())) {
			平安资产类型 = DictCodeList.平安资产类型_基金.getValue();
		}
		else if(StringUtil.equals(dto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
			平安资产类型 = DictCodeListExt2.平安资产类型_股票.getValue();
		}
		else {
			平安资产类型 = DictCodeList.平安资产类型_质押式回购.getValue();
			finprodId = null;
		}

		if(taskId != null) {
			try {
				Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), dto.getEntrustAmt(), LoginUserUtil.getUserName(),
						平安资产类型, "", dto.getTradeMarket(), dto.getPs()
						, finprodId, entity.getWorkflowId()
				);
				params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
				return commonBusinessService.是否需要上级审批_委托用(params);
			}catch (WorkFlowException e) {
				// 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
				throw new WorkFlowException(e);
			}
		}
		return null;
	}

	@Override
	public void doInsert(EtrExchangeTrade001Dto entity,OperationType type){
		// nothing to do just for aspect
		if ("save".equals(entity.getOperateType())) {
			return;
		}
		doInsertFlow(entity);
		//产品化_所有投资指令预占需求_场内交易  add by zhangpei 20201128 start
		doInsetOrUpdateForEtrPositionHold(entity);
		//产品化_所有投资指令预占需求_场内交易  add by zhangpei 20201128 end
	}

	@Override
	public void doDelete(String workflowId) {
		doDeleteFlow(workflowId);
	}

	private void doDeleteFlow(String workflowId) {
		Map<String,Object> param = new HashMap<>();
		param.put("workflowId",workflowId);
		restTemplate.postForObject(
				ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/doDeleteEtrFlow", param,
				Boolean.class);
	}

	private void doInsertFlow(EtrExchangeTrade001Dto dto) {
		BaseEtrInvestInfoDto entity = restTemplate.postForObject(
				ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/buildEtrFlow", dto,
				BaseEtrInvestInfoDto.class);
		// 设置指令创建来源
		entity.getEtrEntrustAdd().setEntrustCreateSource(DictCodeListExt3.指令创建来源_场内实时.getValue());
		entity.getEtrEntrust().setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
		entity.setWorkFlowId(dto.getWorkflowId());
		restTemplate.postForObject(
				ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/doInsertEtrFlow", entity,
				Boolean.class);
	}
	/**
	 * 根据实际指令类型来生成流程信息 <一句话功能简述> <功能详细描述>
	 *
	 * @param dto
	 * @return
	 * @see
	 * @since 1.0
	 */
	public WorkFlowParameter<EtrExchangeTrade001Dto> getWorkFlowParameter(EtrExchangeTrade001Dto dto) {
		WorkFlowParameter<EtrExchangeTrade001Dto> parameter = new WorkFlowParameter<EtrExchangeTrade001Dto>();
		// TODO 根据实际指令类型来生成流程信息
		return parameter;
	}

	/**
	 * 查询交易对手【非内部交易对手】
	 * @return
	 */
	public List<SelectOptionDto> selectCounterIdByCounterType(Map<String, Object> map) {
		if (map.containsKey("search")){
			map.put("counterId",map.get("search"));
		}
		if (map.containsKey("initValue")){
			map.put("counterId",map.get("initValue"));
		}
		Object object = restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/selectMstCounterParty",
				map, Object.class);
		List<SelectOptionDto> resultList = new ArrayList<>();
		if (object instanceof List<?>){
			List<Map<String,Object>> mapList = (List<Map<String,Object>>)object;
			mapList.forEach(item ->{
				SelectOptionDto dto = new SelectOptionDto();
				dto.setId(item.get("counterId").toString());
				dto.setText(item.get("counterName").toString());
				dto.setObject(item);
				resultList.add(dto);
			});
		}
		return resultList;
	}

	/**
	 * 查询证券代码
	 * @return
	 */
	public Page<SelectOptionDto> getFinprodMarketInfo(Map<String, Object> map) {
		return restTemplate.postForPage(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/MstSecuritiesAccountInfo/getFinprodMarketInfo", map,
				SelectOptionDto.class);
	}


	/**
	 *
	 * @return
	 */
	public String IsBondAsset(Map<String, Object> map) {
		return restTemplate.postForObject(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/MstSecuritiesAccountInfo/IsBondAsset", map,
				String.class);
	}

	/**
	 * 查询标的
	 * @return
	 */
	public List<SelectOptionDto> getFinprodId(Map<String, Object> map) {
		return restTemplate.postForList(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/MstSecuritiesAccountInfo/getFinprodId", map,
				SelectOptionDto.class);
	}

	/**
	 * 查询组合资金账户
	 * @return
	 */
	public List<SelectOptionDto> getPtlSecAcctInfo(Map<String, Object> map) {
		return restTemplate.postForList(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/MstSecuritiesAccountInfo/getPtlSecAcctInfo", map,
				SelectOptionDto.class);
	}

	/**
	 * 查询证券账户
	 * @return
	 */
	public List<SelectOptionDto> getSecAcctInfo(Map<String, Object> map) {
		return restTemplate.postForList(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/MstSecuritiesAccountInfo/getSecAcctInfo", map,
				SelectOptionDto.class);
	}


	public Page<EtrExchangeTrade003Dto> selectEtrExchange(Map<String, Object> param) {
		param.put("userId", LoginUserUtil.getUserName());
		param.put("sysDate",iPlatformDate.getSysDateStr());
		List<SysUser> userList = restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EntrustCommon/getAllUserList",
				param, SysUser.class);
		Page<EtrExchangeTrade003Dto> page = restTemplate.postForPage(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/selectEtrExchange",
				param, EtrExchangeTrade003Dto.class);
		for(EtrExchangeTrade003Dto dto:page.getRoot()){
			if(StringUtil.isNotEmpty(dto.getNextUser())){
				String sysUsers = userList.stream().filter(x-> Arrays.asList(dto.getNextUser().split(",")).contains(x.getUserName()))
						.map(SysUser::getRealName)
						.collect(Collectors.joining(",","",""));
				dto.setNextUser(sysUsers);
			}
		}
		return page;
	}
	//add by chenjiaxing 导出全部数据 202100902 start
	public List<EtrExchangeTrade003Dto> selectEtrExchange2(Map<String, Object> param) {
		param.put("userId", LoginUserUtil.getUserName());
		param.put("sysDate",iPlatformDate.getSysDateStr());
		return restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/selectEtrExchange2",
				param, EtrExchangeTrade003Dto.class);
	}
	//add by chenjiaxing 导出全部数据 202100902 end

	/**
	 * 对手方交易商联想文本框初始化。
	 *
	 * @param map 检索参数
	 * @return 联想文本框
	 */
	public List<SuggestDto> selectCounterDealer(Map<String, Object> map) {
		return restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/selectCounterDealer",
				map, SuggestDto.class);
	}

	/**
	 * 对手方交易员联想文本框初始化。
	 *
	 * @param map 检索参数
	 * @return 联想文本框
	 */
	public List<SuggestDto> selectCounterTrader(Map<String, Object> map) {
		map.put("counterTraderId",map.get("param"));
		Object object = restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/selectCounterTraderId",
				map, Object.class);
		List<SuggestDto> resultList = new ArrayList<>();
		if (object instanceof List<?>){
			List<Map<String,Object>> mapList = (List<Map<String,Object>>)object;
			mapList.forEach(item ->{
				SuggestDto dto = new SuggestDto();
				dto.setCode(item.get("counterTraderId").toString());
				dto.setObject(item);
				resultList.add(dto);
			});
		}
		return resultList;
	}

	/**
	 * 本方交易员联想文本框初始化。
	 *
	 * @param map 检索参数
	 * @return 联想文本框
	 */
	public List<SuggestDto> selectTrader(Map<String, Object> map) {
		return restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/selectTrader",
				map, SuggestDto.class);
	}

	public BigDecimal selectAvailableNums(EtrExchangeTrade001Dto etrExchangeTrade001Dto){
		PositionCashSecDto dto = new PositionCashSecDto();
		dto.setFinprodId(etrExchangeTrade001Dto.getFinprodId());
		dto.setFinprodType2(etrExchangeTrade001Dto.getFinprodType2());
		dto.setPortfolioId(etrExchangeTrade001Dto.getPortfolioId());
		dto.setPs(etrExchangeTrade001Dto.getPs());
		dto.setSecAcctId(etrExchangeTrade001Dto.getSecAcctId());
		dto.setAcctId(etrExchangeTrade001Dto.getAcctNo());
		dto.setTradeMarket(etrExchangeTrade001Dto.getTradeMarket());
		dto.setTradePlat(etrExchangeTrade001Dto.getTradePlat());
		BigDecimal availableNum = etrTradeCommonervice.selectAvailableNum(dto);
		if(availableNum == null) {
			availableNum= BigDecimal.ZERO;
		}
		return availableNum;
	}

	/**
	 * 查询资产对应组合的可用持仓信息
	 *
	 * @param param 查询参数
	 * @return 可用持仓
	 * @author dubaojun
	 */
	public BigDecimal selectAvailableNum(Map<String, Object> param) {
		Date sysDate = DateUtil.platDateFromCache();
		param.put("sysDate", DateUtil.format(sysDate));
		EtrSecPositionDto dto = new EtrSecPositionDto();
		dto.setCdate(sysDate);
		dto.setPortfolioId((String) param.get("portfolioId"));
		dto.setFinprodId((String) param.get("finprodId"));
		dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
		dto.setSecAcctId((String) param.get("secAcctId"));
		dto.setInvAim((String) param.get("invAim"));
		dto.setHodingType(DictCodeListExt1.持仓类型_委托指令可用.getValue());
		String ps = param.get("ps") != null ? (String) param.get("ps") : null;
		if (StringUtils.endsWith(ps, DictCodeListExt3.交易方向_正回购.getValue())
				|| StringUtils.endsWith(ps, DictCodeListExt3.交易方向_质押券出库.getValue())) {
			return queryStandandTicketCanUse(param);
		}
		return etrCommonService.getAssetAvailablePosition(dto);
	}

	/**
	 * 查询组合对应账户的可用现金信息
	 *
	 * @param param
	 * @return 可用现金
	 * @author dubaojun
	 */
	public BigDecimal selectAccAvailableCash(Map<String, Object> param) {
		Date sysDate = DateUtil.platDateFromCache();
		param.put("sysDate", DateUtil.format(sysDate));
		Map<String, Object> map = etrCommonService.getProAvailableCash(param);
		if (StringUtil.isNotEmpty(map)){
			return (BigDecimal)map.get("cashT");
		}
		return BigDecimal.ZERO;
	}

	public BigDecimal selectAccAvailableCashs(EtrExchangeTrade001Dto etrExchangeTrade001Dto){
		PositionCashSecDto dto = new PositionCashSecDto();
		dto.setAcctId(etrExchangeTrade001Dto.getAcctNo());
		dto.setPortfolioId(etrExchangeTrade001Dto.getPortfolioId());
		dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
		dto.setPs(etrExchangeTrade001Dto.getPs());
		dto.setTradeMarket(etrExchangeTrade001Dto.getTradeMarket());
		BigDecimal accAvailableCash = etrTradeCommonervice.selectAccAvailableCash(dto);
		//wsz
		if(accAvailableCash == null) {
			accAvailableCash= BigDecimal.ZERO;
		}
		return accAvailableCash;
	}

	/**
	 * 查询债券对应的最新面值
	 *
	 * @param param 查询参数
	 * @return 最新面值
	 * @author dubaojun
	 */
	public BigDecimal selectSecFaceValue(Map<String, Object> param) {
		Date sysDate = DateUtil.platDateFromCache();
		String finprodId = (String) param.get("finprodId");
		return (BigDecimal) etrCommonService.select券面面值(finprodId, DateUtil.format(sysDate));
	}

	/**
	 * 根据画面的信息计算价格和到期收益率。<br/>
	 * 如果以净价为基准，则计算全价和到期收益率<br/>
	 * 如果以全价为基准，则计算净价和到期收益率<br/>
	 * 如果以到期收益率为基准，则计算净价和全价<br/>
	 * 注意：该方法支持返回结果如下：<br/>
	 * <table style="border: 1px solid;">
	 * <tr>
	 * <th style="border: 1px solid;">字段中文</th>
	 * <th style="border: 1px solid;">字段英文</th>
	 * </tr>
	 * <tr>
	 * <td style="border: 1px solid;">单位应计利息</td>
	 * <td style="border: 1px solid;">unitInt</td>
	 * </tr>
	 * <tr>
	 * <td style="border: 1px solid;">应计利息总额</td>
	 * <td style="border: 1px solid;">intAmt</td>
	 * </tr>
	 * <tr>
	 * <td style="border: 1px solid;">单位净价</td>
	 * <td style="border: 1px solid;">cprice</td>
	 * </tr>
	 * <tr>
	 * <td style="border: 1px solid;">净价总额</td>
	 * <td style="border: 1px solid;">cpriceAmt</td>
	 * </tr>
	 * <tr>
	 * <td style="border: 1px solid;">单位全价</td>
	 * <td style="border: 1px solid;">fprice</td>
	 * </tr>
	 * <tr>
	 * <td style="border: 1px solid;">全价总额</td>
	 * <td style="border: 1px solid;">fprice</td>
	 * </tr>
	 * <tr>
	 * <td style="border: 1px solid;">到期收益率</td>
	 * <td style="border: 1px solid;">yield</td>
	 * </tr>
	 * <tr>
	 * <td style="border: 1px solid;">行权收益率</td>
	 * <td style="border: 1px solid;">exerYield</td>
	 * </tr>
	 * </table>
	 *
	 * @param param
	 * @return 计算结果
	 * @author dubaojun
	 */
	public Map<String, BigDecimal> calcPriceAndYield(Map<String, Object> param) {
		// 获取所有前台取得的参数
		Date settleDate = DateUtil.platDateFromCache();
		String finprodId = (String) param.get("finprodId");
		BigDecimal prinAmt = new BigDecimal(100);
		if (StringUtil.isNotEmpty(param.get("prinAmt"))) {
			prinAmt = new BigDecimal(param.get("prinAmt").toString());
		}
		String tradeMarket = (String) param.get("tradeMarket");
		String ps = (String) param.get("ps");
		String calculateField = (String) param.get("calculateField");

		// 定义需要返回的参数
		BigDecimal unitInt = null;// 单位应计利息
		BigDecimal intAmt = null;// 应计利息总额
		BigDecimal cprice = null;
		if (StringUtil.isNotEmpty(param.get("price"))) {
			cprice = new BigDecimal(param.get("price").toString());// 单位净价
		}
		BigDecimal cpriceAmt = null;// 净价总额
		BigDecimal fprice = null;// 单位全价
		if (StringUtil.isNotEmpty(param.get("fprice"))) {
			fprice = new BigDecimal(param.get("fprice").toString());// 单位全价
		}
		BigDecimal fpriceAmt = null;// 全价总额
		BigDecimal yield = null;// 到期收益率
		if (StringUtil.isNotEmpty(param.get("yield"))) {
			yield = new BigDecimal(param.get("yield").toString()).divide(new BigDecimal(100));// 到期收益率
		}
		BigDecimal exerYield = null;// 行权收益率
		if (StringUtil.isNotEmpty(param.get("exerYield"))) {
			exerYield = new BigDecimal(param.get("exerYield").toString()).divide(new BigDecimal(100));// 行权收益率
		}

		// 如果交易场所为上交所、深交所以外的情况
		boolean isJiaoYiSuo = true;
		if (!StringUtil.equals(DictCodeListExt1.交易场所_上海证券交易所.getValue(), tradeMarket)
				&& !StringUtil.equals(DictCodeListExt1.交易场所_深圳证券交易所.getValue(), tradeMarket)) {
			isJiaoYiSuo = false;
		}
		final int SCALE = 12;

		Map<String, Object> tempParamMap = new HashMap<>();
		tempParamMap.put("finprodId", finprodId);

		// b.获取计息区间投资端利率报价信息（债券的利率重置信息）
		List<FinCashCalcRule> finCashCalcRuleList = restTemplate.postForList(
				ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/selectBondsIntResetList", tempParamMap,
				FinCashCalcRule.class);

		// c.获取债券的付息计划信息
		List<FinCashPlanDto> finCashPlanList = restTemplate.postForList(
				ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/getFinCashPlan", tempParamMap,
				FinCashPlanDto.class);
		List<SecIntSchedule> invScheduleList = new ArrayList<>();
		List<FinCashPlanDto> intSchList = new ArrayList<FinCashPlanDto>();
		for (FinCashPlanDto item : finCashPlanList) {
			if (StringUtil.equals(item.getCashType1(), DictCodeListExt1.现金流一级类型_利息.getValue())
					&& item.getVdateUnadjust() != null) {
				SecIntSchedule schedule = new SecIntSchedule();
				schedule.setTheoryStartDate(toLocalDate(item.getVdateUnadjust()));
				schedule.setTheoryCutoffDate(toLocalDate(item.getMdateUnadjust()));
				schedule.setActualStartDate(toLocalDate(item.getVdate()));
				schedule.setActualCutoffDate(toLocalDate(item.getMdate()));
				schedule.setAmount(item.getIntAmt());
				invScheduleList.add(schedule);
				intSchList.add(item);
			}
		}

		// d.获取债券基本资料信息
		// 计算行权收益率，非行权时，取大于当日的行权日
		tempParamMap.put("ps", ps);
		SecInfoModelDto secInfoModelDto = restTemplate.postForObject(
				ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/getSecInfoModel", tempParamMap,
				SecInfoModelDto.class);
		SecInfoModel secInfoModel = toSecInfoModel(secInfoModelDto);
		if (secInfoModelDto != null && secInfoModelDto.getStartDate() != null
				&& DateUtil.compareTo(secInfoModelDto.getStartDate(), settleDate) > 0) {
			settleDate = secInfoModelDto.getStartDate();
		}
		// 如果交易场所为上交所、深交所的情况
		if (isJiaoYiSuo) {
			// 付息债的计息基础使用A_365F
			if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), secInfoModelDto.getBondsType())) {
				secInfoModel.setBasis(Basis.A_365F);
			}
		}

		// e.获取债券的还本计划信息
		List<SecPrinSchedule> secPrinScheduleList = new ArrayList<SecPrinSchedule>();
		for (FinCashPlanDto item : finCashPlanList) {
			if (StringUtil.equals(item.getCashType1(), DictCodeListExt1.现金流一级类型_本金.getValue())) {
				SecPrinSchedule schedule = new SecPrinSchedule();
				schedule.setAmount(item.getIntAmt());
				schedule.setFlag(StringUtil.equals(item.getIsPayInt(), DictCodeList.是否_是.getValue()));
				schedule.setPrincipal(item.getPrinAmt());
				schedule.setInterestEndDate(toLocalDate(item.getMdate()));
				secPrinScheduleList.add(schedule);
			}
		}

		List<PaperIrReset> paperIrResetList = finCashCalcRuleList.stream().map(item -> {
			PaperIrReset ret = new PaperIrReset();
			ret.setBaseRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
			ret.setParRate(item.getYield());
			ret.setParRateEffDate(BasicUtil.dateToLocal(item.getEffDate()));
			return ret;
		}).collect(Collectors.toList());
		if (paperIrResetList != null && paperIrResetList.size() > 0) {
			paperIrResetList.get(0).setBaseRateEffDate(BasicUtil.dateToLocal(secInfoModelDto.getStartDate()));
		}
		if (paperIrResetList != null && paperIrResetList.size() > 1) {
			// 多重利率则计算按浮动利率算
			secInfoModel.setIntRateType(IntRateType.浮动利率);
		}
		YieldByPriceCalculator yieldByPriceCalculator = new YieldByPriceCalculator();
		PriceByYieldCalculator priceByYieldCalculator = new PriceByYieldCalculator();
		YieldPriceInParameter yieldPriceInParameter = new YieldPriceInParameter();
		yieldPriceInParameter.setCalDate(toLocalDate(settleDate));
		yieldPriceInParameter.setSecInfoModel(secInfoModel);
		yieldPriceInParameter.setSecIntScheduleList(invScheduleList);
		yieldPriceInParameter.setSecPrinScheduleList(secPrinScheduleList);
		yieldPriceInParameter.setPaperIrResetList(paperIrResetList);
		PriceYieldInParameter priceYieldInParameter = new PriceYieldInParameter();
		priceYieldInParameter.setCalDate(toLocalDate(settleDate));
		priceYieldInParameter.setSecInfoModel(secInfoModel);
		priceYieldInParameter.setSecIntScheduleList(invScheduleList);
		priceYieldInParameter.setSecPrinScheduleList(secPrinScheduleList);
		priceYieldInParameter.setPaperIrResetList(paperIrResetList);
		yieldPriceInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
		priceYieldInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
		yieldPriceInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
		priceYieldInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
		if (StringUtil.equals(DictCodeList.息票品种_附息.getValue(), secInfoModelDto.getBondsType())) {
			if (isNotFomartSec(intSchList, secInfoModelDto)) {
				yieldPriceInParameter.setCashScheme(FlowCashScheme.理论付息区间);
				priceYieldInParameter.setCashScheme(FlowCashScheme.理论付息区间);
				yieldPriceInParameter.setDisScheme(DiscountScheme.以付息区间为单位);
				priceYieldInParameter.setDisScheme(DiscountScheme.以付息区间为单位);
			} else {
				yieldPriceInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
				priceYieldInParameter.setCashScheme(FlowCashScheme.付息计划推算计息年度);
				yieldPriceInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
				priceYieldInParameter.setDisScheme(DiscountScheme.以剩余年限为单位);
			}
		}
		yieldPriceInParameter.setCalMethod(secInfoModel.getCalMethod());
		priceYieldInParameter.setCalMethod(secInfoModel.getCalMethod());
		// 后续区间利息计算方案
		if (IntRateType.浮动利率.equals(secInfoModel.getIntRateType())) {
			priceYieldInParameter.setNextIntMethod(NextIntCalMethod.按实际利率重置分段计息计算);
			yieldPriceInParameter.setNextIntMethod(NextIntCalMethod.按实际利率重置分段计息计算);

			yieldPriceInParameter.setCurrIntMethod(CurrIntCalMethod.按实际利率重置分段计息计算);
			priceYieldInParameter.setCurrIntMethod(CurrIntCalMethod.按实际利率重置分段计息计算);
		} else {
			priceYieldInParameter.setNextIntMethod(NextIntCalMethod.使用计算日最新最新票面计算);
			yieldPriceInParameter.setNextIntMethod(NextIntCalMethod.使用计算日最新最新票面计算);

			yieldPriceInParameter.setCurrIntMethod(CurrIntCalMethod.使用区间起始日前最新票面利率计算);
			priceYieldInParameter.setCurrIntMethod(CurrIntCalMethod.使用区间起始日前最新票面利率计算);
		}

		// 百元面值应计利息
		BigDecimal accurInt = BigDecimal.ZERO;
		// 交易所债券特殊处理start----------------------------------------------------------------
		Date tempSettleDate = settleDate;
		// 如果交易场所为上交所、深交所的情况
		if (isJiaoYiSuo) {
			// 行权-回售 行权-赎回 交割日不加1
			if (!StringUtil.equals(ps, DictCodeList.交易方向_行权_回售.getValue())
					&& !StringUtil.equals(ps, DictCodeList.交易方向_行权_赎回.getValue())) {
				tempSettleDate = DateUtil.addDay(tempSettleDate, 1);
			}
		}
		// 交易所债券特殊处理end----------------------------------------------------------------

		// a.当前债券面额取得（根据还本计划计算）
		Map<String, Object> paraMap = new HashMap<>();
		paraMap.put("finprodId", finprodId);
		paraMap.put("settleDate", DateUtil.format(tempSettleDate));
		BigDecimal curSecAmt = restTemplate.postForObject(
				ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/getCurSecAmt", paraMap, BigDecimal.class);

		AccurIntCalInParameter accurIntCalInParameter = new AccurIntCalInParameter();
		// 计算日
		accurIntCalInParameter.setCalDate(toLocalDate(tempSettleDate));
		if (paperIrResetList != null && paperIrResetList.size() > 1) {
			// 多重利率则计算按浮动利率算
			secInfoModel.setIntRateType(IntRateType.浮动利率);
		}
		// 债券基本资料
		accurIntCalInParameter.setSecInfoModel(secInfoModel);
		// 付息计划
		accurIntCalInParameter.setSecIntScheduleList(invScheduleList);
		// 利率重置信息
		accurIntCalInParameter.setPaperIrResetList(paperIrResetList);

		// 交易所债券特殊处理start----------------------------------------------------------------
		AccurIntCalOutParameter out = null;
		// 如果交易场所为上交所、深交所的情况
		if (isJiaoYiSuo) {
			// 付息计划截止日对应的利息
			Map<String, BigDecimal> intMap = new HashMap<>();
			if (null != invScheduleList && !invScheduleList.isEmpty()) {
				for (SecIntSchedule intSche : invScheduleList) {
					intMap.put(intSche.getActualCutoffDate().toString(), intSche.getAmount());
				}
			}

			if (intMap.containsKey(accurIntCalInParameter.getCalDate().toString())
					&& !((Variety.贴现.equals(secInfoModel.getBondsType()))
					|| Variety.零息.equals(secInfoModel.getBondsType()))) {
				out = new AccurIntCalOutParameter();
				out.setInterest(intMap.get(accurIntCalInParameter.getCalDate().toString())
						.multiply(new BigDecimal("100")).divide(curSecAmt, 12, RoundingMode.HALF_UP));
				accurIntCalInParameter.setCalDate(accurIntCalInParameter.getCalDate());
				AccurIntForSecheduleCalculator calculator2 = CalculatorFactory.createAccurIntForSecheduleCalculator();
				if (accurIntCalInParameter.getPaperIrResetList() != null
						&& accurIntCalInParameter.getPaperIrResetList().size() > 1) {
					accurIntCalInParameter.getSecInfoModel().setIntRateType(IntRateType.浮动利率);
				}
				out = calculator2.calculate(accurIntCalInParameter);
			}
		}
		// 交易所债券特殊处理end----------------------------------------------------------------

		if (out != null) {
			accurInt = ArithmeticUtil.round(out.getInterest(), SCALE, RoundingMode.HALF_UP);
		} else {
			// 从计算工厂中创建应计利息计算器
			AccurIntCalculator calc = CalculatorFactory.createAccurIntCalculator();
			// 计算利息
			AccurIntCalOutParameter accurIntCalOutParameter = calc.calculate(accurIntCalInParameter);
			accurInt = ArithmeticUtil.round(accurIntCalOutParameter.getInterest(), SCALE, RoundingMode.HALF_UP);
		}

		// 单位应计利息
		if (isJiaoYiSuo) {
			unitInt = ArithmeticUtil.divide(ArithmeticUtil.multiply(accurInt, curSecAmt), new BigDecimal(100),
					Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam("SEC_INT_SCALE_EXCHANGE")) ? "8"
							: CacheUtil.getSysParam("SEC_INT_SCALE_EXCHANGE")),
					RoundingMode.HALF_UP);
		} else {
			unitInt = ArithmeticUtil.divide(ArithmeticUtil.multiply(accurInt, curSecAmt), new BigDecimal(100),
					Integer.parseInt(StringUtils.isEmpty(CacheUtil.getSysParam("SEC_INT_SCALE_BANK")) ? "12"
							: CacheUtil.getSysParam("SEC_INT_SCALE_BANK")),
					RoundingMode.HALF_UP);
		}
		intAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(unitInt, prinAmt), 100, 2);

		if ((StringUtil.equals(DictCodeList.交易方向_行权_回售.getValue(), ps)
				|| StringUtil.equals(DictCodeList.交易方向_行权_赎回.getValue(), ps))) {
			if (unitInt.compareTo(BigDecimal.ZERO) < 0) {
				unitInt = BigDecimal.ZERO;
			}
			if (intAmt.compareTo(BigDecimal.ZERO) < 0) {
				intAmt = BigDecimal.ZERO;
			}
		}
		try {
			// 单位净价
			if (StringUtil.equals(calculateField, DictCodeListExt3.债券计算模式_单位净价.getValue())) {
				cpriceAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(cprice, prinAmt), new BigDecimal(100), 2);
				// 全价总额 = 净价总额+ 利息总额
				fpriceAmt = ArithmeticUtil.doAdd(intAmt, cpriceAmt);
				// round（全价总额/券面总额，10）*100
				// 全价 = 单位应计利息 + 单位净价 (保持横向的一致性)
				fprice = ArithmeticUtil.doAdd(cprice, unitInt);
			}
			YieldPriceOutParameter yieldPriceOutParameter = null;
			PriceYieldOutParameter priceYieldOutParameter = null;
			switch (calculateField) {
				// 单位净价
				case "01":
					// 全价总额
				case "02":
					// 全价总额==>到期收益率
					yieldPriceInParameter.setFullPrice(fprice);
					yieldPriceOutParameter = yieldByPriceCalculator.calculate(yieldPriceInParameter);
					yield = yieldPriceOutParameter.getMatYield();
					if (secInfoModelDto.getNextExeDate() != null) {
						// 全价总额==>行权收益率
						yieldPriceInParameter.setNextExeDate(toLocalDate(secInfoModelDto.getNextExeDate()));
						yieldPriceInParameter.setNextExePrice(secInfoModelDto.getNextExercisePrice());
						if (yieldPriceInParameter.getNextExePrice() != null) {
							yieldPriceOutParameter = yieldByPriceCalculator.calculate(yieldPriceInParameter);
							exerYield = yieldPriceOutParameter.getExeYield();
						}
					}
					break;
				// 到期收益率
				case "03":
					// 到期收益率==>全价总额
					priceYieldInParameter.setMatYield(yield);
					priceYieldOutParameter = priceByYieldCalculator.calculate(priceYieldInParameter);
					fprice = priceYieldOutParameter.getFullPrice();
					// 全价总额==>行权收益率
					if (secInfoModelDto.getNextExeDate() != null) {
						yieldPriceInParameter.setFullPrice(fprice);
						yieldPriceInParameter.setNextExeDate(toLocalDate(secInfoModelDto.getNextExeDate()));
						yieldPriceInParameter.setNextExePrice(secInfoModelDto.getNextExercisePrice());
						if (yieldPriceInParameter.getNextExePrice() != null) {
							yieldPriceOutParameter = yieldByPriceCalculator.calculate(yieldPriceInParameter);
							exerYield = yieldPriceOutParameter.getExeYield();
						}
					}
					break;
				// 行权收益率（暂时不存在）
				case "04":
					// 行权收益率==>全价总额
					if (secInfoModelDto.getNextExeDate() != null) {
						priceYieldInParameter.setMatYield(exerYield);
						yieldPriceInParameter.setNextExeDate(toLocalDate(secInfoModelDto.getNextExeDate()));
						yieldPriceInParameter.setNextExePrice(secInfoModelDto.getNextExercisePrice());
						priceYieldOutParameter = priceByYieldCalculator.calculate(priceYieldInParameter);
						fprice = priceYieldOutParameter.getFullPrice();
						// 全价总额==>到期收益率
						yieldPriceInParameter.setFullPrice(fprice);
						yieldPriceOutParameter = yieldByPriceCalculator.calculate(yieldPriceInParameter);
						yield = yieldPriceOutParameter.getMatYield();
					}
					break;
				default:
					break;
			}
			// 如果是全价、到期收益率 或 行权收益率
			if (StringUtil.equals(calculateField, DictCodeListExt3.债券计算模式_单位全价.getValue())
					|| StringUtil.equals(calculateField, DictCodeListExt3.债券计算模式_到期收益率.getValue())
				/*
				 * || StringUtil.equals(calculateField,
				 * DictCodeListExt3.债券计算模式_行权收益率.getValue())
				 */) {
				// 全价总额 = 单位全价 * 券面总额 / 100
				fpriceAmt = ArithmeticUtil.divide(ArithmeticUtil.multiply(fprice, prinAmt), new BigDecimal(100), 2);
				cpriceAmt = ArithmeticUtil.subtract(fpriceAmt, intAmt);
				// round（净价总额/券面总额，10）*100
				// 单位净价 = 全价-单位应计利息 (保持横向的一致性)
				cprice = ArithmeticUtil.subtract(fprice, unitInt);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (yield != null) {
			yield = ArithmeticUtil.multiply(yield, new BigDecimal(100));
		}
		if (exerYield != null) {
			exerYield = ArithmeticUtil.multiply(exerYield, new BigDecimal(100));
		}

		Map<String, BigDecimal> result = new HashMap<>();
		result.put("unitInt", unitInt);
		result.put("intAmt", intAmt);
		result.put("cprice", cprice);
		result.put("cpriceAmt", cpriceAmt);
		result.put("fprice", fprice);
		result.put("fpriceAmt", fpriceAmt);
		result.put("yield", yield == null ? new BigDecimal(0.00) : yield);
		result.put("exerYield", exerYield);
		return result;
	}

	/** 查询大宗交易行情信息 **/
	public Page<EtrExchangeTradeQuotation001Dto> findTradeQuotationsPage(Map<String, Object> map) {
		Page<EtrExchangeTradeQuotation001Dto> page = new Page<EtrExchangeTradeQuotation001Dto>();
		List<EtrExchangeTradeQuotation001Dto> list = new ArrayList<EtrExchangeTradeQuotation001Dto>();
		String secId = (String)map.get("secId");
		String param = "?quote_type=1&stock_code="+secId;

		boolean flag = isBuyback(secId);
		Map quoteMap = restTemplate.getForObject(ServiceApplication.EXCHANGE_SERVICE_NAME + queryIntegratedQuote + param, Map.class);

		//Map quoteMap = simpleRestTemplate.getForObject("http://192.168.70.3:8081/tams" + queryIntegratedQuote + param, Map.class);


		if(quoteMap != null && quoteMap.get("data") !=null) {
			List<Map<String, Object>> quoteList = (List<Map<String, Object>>) quoteMap.get("data");
			if(!StringUtil.isEmptyList(quoteList)) {
				quoteList.forEach(tmp -> {
					EtrExchangeTradeQuotation001Dto dto =new EtrExchangeTradeQuotation001Dto();
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
						dto.setEntrustPrice(new BigDecimal((Double)tmp.get("entrustPrice")));
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

	/** 查询固定收益行情数据 **/
	public Page<EtrExchangeTradeQuotation001Dto> findFixedIncomePage(Map<String, Object> map) {
		Page<EtrExchangeTradeQuotation001Dto> page = new Page<EtrExchangeTradeQuotation001Dto>();
		List<EtrExchangeTradeQuotation001Dto> list = new ArrayList<EtrExchangeTradeQuotation001Dto>();
		String secId= (String)map.get("secId");

		String param = "?quote_type=5&stock_code="+secId;

		Map quoteMap = restTemplate.getForObject(ServiceApplication.EXCHANGE_SERVICE_NAME + queryIntegratedQuote + param, Map.class);

		//Map quoteMap = simpleRestTemplate.getForObject("http://192.168.70.3:8081/tams" + queryIntegratedQuote + param, Map.class);

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
						dto.setnPrice(new BigDecimal((Double)tmp.get("entrustPrice")));
					}

					//全价
					if(tmp.get("fullPrice") !=null) {
						dto.setfPrice(new BigDecimal((Double)tmp.get("fullPrice")));
					}

					//收益率
					if(tmp.get("expireIncomeRate") !=null) {
						dto.setYield(new BigDecimal((Double)tmp.get("expireIncomeRate")));
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
	public Page<EtrExchangeTradeQuotation001Dto> findAfterHoursPage(Map<String, Object> map) {
		Page<EtrExchangeTradeQuotation001Dto> page = new Page<EtrExchangeTradeQuotation001Dto>();
		List<EtrExchangeTradeQuotation001Dto> list = new ArrayList<EtrExchangeTradeQuotation001Dto>();
		String secId= (String)map.get("secId");

		String param = "?quote_type=2&stock_code="+secId;

		Map quoteMap = restTemplate.getForObject(ServiceApplication.EXCHANGE_SERVICE_NAME + queryIntegratedQuote + param, Map.class);

		//Map quoteMap = simpleRestTemplate.getForObject("http://192.168.70.3:8081/tams" + queryIntegratedQuote + param, Map.class);

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
						dto.setClosingPrice(new BigDecimal((Double)tmp.get("entrustPrice")));
					}

					//日均价
					if(tmp.get("fullPrice") !=null) {
						dto.setDailyAvgPrice(new BigDecimal((Double)tmp.get("fullPrice")));
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
	public EtrExchangeTrade001Dto findBidQuotationsPage(Map<String, Object> map) {
		String secId = (String) map.get("secId");
		String param = "?stock_code=" + secId;
		int scale = (int) map.get("scale");
		Map quoteMap = restTemplate.getForObject(ServiceApplication.QUOTE_SERVICE_NAME + queryIntegratedTick + param, Map.class);
		//Map quoteMap = simpleRestTemplate.getForObject("http://192.168.70.3:8081/tams/" + queryIntegratedTick + param, Map.class);
		EtrExchangeTrade001Dto dto = new EtrExchangeTrade001Dto();
		if (quoteMap != null && quoteMap.get("data") != null) {
			Map<String, Object> quoteList = (Map<String, Object>) quoteMap.get("data");
			if (null != quoteList) {
				dto.setStockId((String) quoteList.get("instrumentID"));
				dto.setEntrustPrice(new BigDecimal(quoteList.get("lastPrice").toString()).setScale(scale,BigDecimal.ROUND_HALF_UP));
				dto.setLimitUp(new BigDecimal(quoteList.get("upperLimitPrice").toString()).setScale(scale,BigDecimal.ROUND_HALF_UP));
				dto.setLimitDown(new BigDecimal(quoteList.get("lowerLimitPrice").toString()).setScale(scale,BigDecimal.ROUND_HALF_UP));
			}
		}
		return dto;
	}

	/** 查询竞价交易行情信息 **/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map<String, Object> findQuotations(Map<String, Object> map) {
		String secId = (String) map.get("secId");
		String param = "?stock_code=" + secId;
		Map<String, Object> quoteList;
		Map quoteMap = restTemplate.getForObject(ServiceApplication.QUOTE_SERVICE_NAME + queryIntegratedTick + param, Map.class);
//        Map quoteMap = simpleRestTemplate.getForObject("http://192.168.70.3:8081/tams/" + queryIntegratedTick + param, Map.class);
		if (quoteMap != null && quoteMap.get("data") != null) {
			quoteList = (Map<String, Object>) quoteMap.get("data");
			return quoteList;
		}
		return null;
	}
	/**
	 * 修改/双击查看详情查询信息 <功能详细描述>
	 *
	 * @param entrustId
	 * @return
	 * @see
	 * @since 1.0
	 */
	public EtrExchangeTrade001Dto getEtrExchangeTradeInfo(String entrustId) {
		return restTemplate.postForObject(
				ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/getEtrExchangeTradeInfo",entrustId,
				EtrExchangeTrade001Dto.class);
	}

	/**
	 * 委托关闭
	 * @param entity
	 */
	public void closeDelegate(EtrExchangeTrade003Dto entity){
		restTemplate.postForObject(
				ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/entrustClose",entity,
				Integer.class);
	}

	/**
	 * 委托撤销
	 * @param entity
	 */
	public void recallDelegate(EtrExchangeTrade003Dto entity){
		restTemplate.postForObject(
				ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/entrustRevoke",entity,
				Integer.class);
	}

	/**
	 * 保存check
	 * @param entity
	 */
	public AjaxResult checkSave(EtrExchangeTrade001Dto entity){
		return restTemplate.postForObject(
				ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/checkSave",entity,
				AjaxResult.class);
	}

	/***
	 * 取更新时间最新记录.投资目的  指令业务类型 = 数据字典.委托类型.大宗债券指令
	 * @return
	 */
	public String getInvAim() {
		String invAim = restTemplate.postForObject(
				ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/getInvAim", null, String.class);
		return invAim;
	}

	public String linkInvAim(Map<String, Object> param) {
		String invAim = restTemplate.postForObject(
				ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/linkInvAim", param, String.class);
		return invAim;
	}

	/**
	 *
	 * @param map
	 * @return
	 */
	public int selectBondExecPlan(Map<String, Object> map) {
		return restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/selectBondExecPlan",
				map, Integer.class);
	}

	/**
	 *
	 * @param map
	 * @return
	 */
	public String getCustomerTypeTwo(Map<String, Object> map) {
		return restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/getCustomerTypeTwo",
				map, String.class);
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
			entrustPrice = StringUtil.isNotEmpty(entrustPrice)?entrustPrice.setScale(2,RoundingMode.HALF_UP):null;
			entrustAmt = ArithmeticUtil.multiply(entrustNum, entrustPrice);
			if(StringUtils.equals(DictCodeListExt3.交易方向_质押券入库.getValue(), ps) ||
					StringUtils.equals(DictCodeListExt3.交易方向_质押券出库.getValue(), ps)){
				if(null != entrustAmt){//entrustAmt 代表标准券数量
					// modify by etliu 20220303 start
					// 质押式回购出入库计算时，原公式：标准券数量=质押数量*标准折算率，改成：标准券数量=质押数量*标准折算率*面值/100  ---需求张琼
					BigDecimal 百元券面 = trdCommonBaseService.getFaceValue(finprodId, DateUtil.platDateFromCache());
					if (百元券面 == null) {
						百元券面 = BigDecimal.valueOf(100);
					}
					entrustAmt = entrustAmt.multiply(百元券面).divide(BigDecimal.valueOf(100));
					// modify by etliu 20220303 end
//					entrustAmt = new BigDecimal(entrustAmt.intValue());
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
	 * 根据不同的金融产品类型设置不同的最小购买单位。
	 *
	 * @param entrustNum 委托数量
	 * @param minNum
	 * @param availableNum
	 * @return 格式化指令数量
	 */
	private BigDecimal formatEntrustNum(BigDecimal entrustNum, BigDecimal minNum, BigDecimal availableNum) {
		if (entrustNum == null || entrustNum.compareTo(BigDecimal.ZERO) == 0) {
			return entrustNum;
		}
/*        if (StringUtil.equals(tradeMarket, DictCodeListExt1.交易场所_上海证券交易所.getValue())) {
            if (StringUtils.equals(DictCodeListExt2.交易方向_配股.getValue(), ps)) {
                minNum = 1;
            } else {
                if (StringUtil.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                    minNum = 10;
                } else if (StringUtil.equals(finprodType2, "ZYSHG")) {
                    minNum = 1000;
                } else if (StringUtil.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_基金.getValue())) {
                    minNum = 100;
                } else if (StringUtil.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
                    if (StringUtils.equals(DictCodeList.交易方向_申购.getValue(), ps)) {
                        minNum = 1000;
                    } else if(StringUtil.equals(board, DictCodeList.上市板_主板.getValue())) {
                        minNum = 100;
                    } else if(StringUtil.equals(board, DictCodeList.上市板_科创板.getValue())) {
                        minNum = 200;
                    }
                } else {
                    minNum = 1;
                }
            }
        } else if (StringUtil.equals(tradeMarket, DictCodeListExt1.交易场所_深圳证券交易所.getValue())) {
            if (StringUtils.equals(DictCodeListExt2.交易方向_配股.getValue(), ps)
                    || StringUtils.equals(DictCodeListExt3.交易方向_配债.getValue(), ps)
                    || StringUtils.equals(DictCodeListExt3.交易方向_债转股.getValue(), ps)
                    || StringUtils.equals(DictCodeList.交易方向_行权_回售.getValue(), ps)
                    || StringUtils.equals(DictCodeListExt3.交易方向_质押券入库.getValue(), ps)
                    || StringUtils.equals(DictCodeListExt3.交易方向_质押券出库.getValue(), ps)) {
                minNum = 1;
            } else {
                if (StringUtil.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
                    minNum = 10;
                } else if (StringUtil.equals(finprodType2, "ZYSHG")) {
                    minNum = 10;
                } else if (StringUtil.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_基金.getValue())) {
                    minNum = 100;
                } else if (StringUtil.equals(finprodType2, DictCodeListExt1.金融产品类型_投管用_股票.getValue())) {
                    if (StringUtils.equals(DictCodeList.交易方向_申购.getValue(), ps)) {
                        minNum = 500;
                    } else {
                        minNum = 100;
                    }
                } else {
                    minNum = 1;
                }
            }
        }*/
		if (null != availableNum && availableNum.compareTo(BigDecimal.ZERO) > 0) {
			BigDecimal multiple = (availableNum.subtract(entrustNum)).divide(minNum);
			// 零股卖出 、整数倍+零股卖出 可以卖出，
			if (new BigDecimal(multiple.intValue()).compareTo(multiple) != 0
					&& !(availableNum.intValue() == entrustNum.intValue() && availableNum.compareTo(minNum) <= 0)) {
				entrustNum = ArithmeticUtil.multiply(ArithmeticUtil.divide(entrustNum, minNum, 0, RoundingMode.DOWN),
						minNum);
			}
		} else {
			entrustNum = ArithmeticUtil.multiply(ArithmeticUtil.divide(entrustNum, minNum, 0, RoundingMode.DOWN),
					minNum);
		}
		return entrustNum;
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
	public BigDecimal formatEntrustNum(BigDecimal entrustNum, MinTrdUnitLimitDto minTrdUnitLimitDto) {
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
	 * @param map
	 * @return 获取最小价差
	 */
	@SuppressWarnings("unchecked")
	public List<MinPirceDto> getInterfSysMinPriceAll(Map<String, Object> map) {
		List<MinPirceDto> minPirceList = new ArrayList<MinPirceDto>();
		Map<String, Object> res = restTemplate.postForObject(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/getMinPrice", map, Map.class);
		if (res != null && res.get("data") != null) {
			List<MinPirceDto> list = (List<MinPirceDto>) res.get("data");
			if (list != null && list.size() > 0) {
				for (int i = 0; i < list.size(); i++) {
					minPirceList.add(JSON.parseObject(JSON.toJSONString(list.get(i)), MinPirceDto.class));
				}
			}
		}
		return minPirceList;
	}

	/**
	 *
	 * @param map 入参
	 * @return 获取申报信息
	 */
	@SuppressWarnings("unchecked")
	public TrdOthCodeInfoDto getTrdOthCodeInfo(Map<String, Object> map) {
		TrdOthCodeInfoDto trdOthCodeInfo = null;
		Map<String, Object> res = restTemplate.postForObject(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/getTrdOthCodeInfo", map, Map.class);
		if (res != null && res.get("data") != null) {
			List<TrdOthCodeInfoDto> list = (List<TrdOthCodeInfoDto>)res.get("data");
			if (list != null && list.size() > 0) {
				trdOthCodeInfo = JSON.parseObject(JSON.toJSONString(list.get(0)), TrdOthCodeInfoDto.class);
			}
			return trdOthCodeInfo;
		}
		return null;
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
	 *
	 * @param tradePlat
	 * @param tradeMarket
	 * @param finprodType2
	 * @param declarationType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public TrdTimeConfigDto getTrdTimeConfig(String tradePlat, String tradeMarket, String finprodType2,
											 String declarationType) {
		TrdTimeConfigDto trdTimeConfigDto = null;
		tradePlat = getTradePlat(tradePlat, tradeMarket);
		String assetType = getPAAssetType(finprodType2);
		Map<String, Object> map = new HashMap<>();
		map.put("tradeMarket", tradeMarket);
		map.put("tradePlat", tradePlat);
		map.put("assetType", assetType);
		map.put("declarationType", declarationType);
		Map<String, Object> res = restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/getTrdTimeConfig", map, Map.class);
		if (res != null && res.get("data") != null) {
			List<TrdTimeConfigDto> list = (List<TrdTimeConfigDto>) res.get("data");
			if (list != null && list.size() > 0) {
				trdTimeConfigDto = JSON.parseObject(JSON.toJSONString(list.get(0)), TrdTimeConfigDto.class);
			}
			return trdTimeConfigDto;
		}
		return null;
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
	 * 判断指令是否分发
	 * @param entrustId 指令编码
	 * @return 分发结果
	 */
	public EtrDistribute getDistributeByEntrustId(String entrustId) {
		Map<String, Object> map = new HashMap<>();
		map.put("entrustId", entrustId);
		return restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/getDistributeByEntrustId", map,
				EtrDistribute.class);
	}

	private <T> void checkElement(T t, StringBuilder errorMsg, Consumer<T> callback, Function<T, String>... chkFunc){
		StringBuilder confirmMsg=new StringBuilder();
		for(Function<T, String> func:chkFunc){
			String addMsg=func.apply(t);
			etrCommonService.spliceMessage(confirmMsg, addMsg);
			if(!StringUtil.isEmpty(addMsg))break;
		};
		if(StringUtil.isEmpty(confirmMsg)&&null!=callback){
			callback.accept(t);
		}
		etrCommonService.spliceMessage(errorMsg,confirmMsg.toString());
	}

	/**
	 * 大宗与固收指令导入
	 * @param objects
	 * @param function
	 * @return
	 * @throws Exception
	 */
	private <T extends EtrExchangeTrade001Dto> AjaxResult checkDzQyImportData(List<List<String>> objects,Function<List<String>,T> function) throws Exception {
		StringBuilder totalMsg = new StringBuilder();
		JSONArray jsonArrays = new JSONArray();
		// 失败条数
		int errorNum = 0;
		for(List<String> object:objects){
			StringBuilder promptMsg = new StringBuilder();
			int 索引=objects.indexOf(object);
			String lineNo= String.valueOf(索引+2);//行号
			T dzImportDto=function.apply(object);
			dzImportDto.setSeqNo(String.valueOf(索引+1));//序号
			//委托开始与结束日(优先校验日期，防止下面逻辑用到此日期条件)
			checkElement(dzImportDto,promptMsg,null,obj-> {
				return etrCommonService.checkIsBlank(DateUtil.format(dzImportDto.getVdate()), "EtrExchangeTrade001.vdate", lineNo);
			},obj-> {
				return etrCommonService.checkIsBlank(DateUtil.format(dzImportDto.getMdate()), "EtrExchangeTrade001.mdate", lineNo);
			},obj-> {
				return etrCommonService.checkIsPlateDate(DateUtil.format(dzImportDto.getVdate()), DateUtil.format(dzImportDto.getMdate()), lineNo);
			});
			// 组合为空与组合是否存在
			checkElement(dzImportDto,promptMsg,null,obj-> {
				return etrCommonService.checkIsBlank(dzImportDto.getPortfolioId(), "EtrExchangeTrade001.portfolioId", lineNo);
			},obj-> {
				return etrCommonService.checkPortfolioIdExist(dzImportDto.getPortfolioId(), "EtrExchangeTrade001.portfolioId", lineNo);
			});
			//券商资金账户
			checkElement(dzImportDto,promptMsg,null,obj-> {
				//在前面无错误提示情况下才校验此逻辑
				return StringUtil.isNotEmpty(promptMsg.toString())?StringUtil.EMPTY:this.checkSecAcctCount(dzImportDto);
			});
			// 证券代码
			Map<String, Object> map = new HashMap<>();
			map.put("portfolioId",dzImportDto.getPortfolioId());
			map.put("acctNo",dzImportDto.getAcctNo());
			List<SelectOptionDto> secAcctInfoList = this.getPtlSecAcctInfo(map);
			if(secAcctInfoList!=null&&secAcctInfoList.size()>0&&secAcctInfoList.get(0)!=null){
				if(!StringUtil.isNotEmpty(dzImportDto.getSecAcctId())){
					List<LinkedHashMap<String,Object>> secAcctInfoMapList = (ArrayList) secAcctInfoList.get(0).getObject();
					if(secAcctInfoMapList.get(0)!=null)
						dzImportDto.setSecAcctId((String) secAcctInfoMapList.get(0).get("secAcctId"));
				}
			}
			// 资产代码为空与资产是否存在(债券，基金，股票)
			checkElement(dzImportDto,promptMsg,null,obj-> {
				return etrCommonService.checkIsBlank(dzImportDto.getFinprodId(), "EtrExchangeTrade001.secId", lineNo);
			},obj-> {
				//检查资产是否存在且在指令范围资产类型内
				return this.checkAstScope(dzImportDto, "EtrExchangeTrade001.secId");
			});
			//add by chenjiaxing 添加申报类型校验 20210907 start
			if(StringUtil.equals(dzImportDto.getTradePlat(),DictCodeList.交易平台_大宗交易平台.getValue())){
				checkElement(dzImportDto,promptMsg,null,obj-> {
					return etrCommonService.checkIsBlank(dzImportDto.getDeclaType(), "EtrExchangeTrade001.priceModel2", lineNo);
				});
			}
			//add by chenjiaxing 添加申报类型校验 20210907 end
			// 委托方向
			checkElement(dzImportDto,promptMsg,null,obj-> {
				return etrCommonService.checkIsBlank(dzImportDto.getPs(), "EtrExchangeTrade001.ps", lineNo);
			});
			//投资目的
			if(StringUtil.equals(dzImportDto.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
				checkElement(dzImportDto,promptMsg,null,obj-> {
					return etrCommonService.checkIsBlank(dzImportDto.getInvAim(), "EtrExchangeTrade001.invAim", lineNo);
				});
			}
			// 价格模式
			checkElement(dzImportDto,promptMsg,null,obj-> {
				return etrCommonService.checkIsBlank(dzImportDto.getPriceModel(), "EtrExchangeTrade001.priceModel", lineNo);
			});
			//add by chenjiaxing 校验投资目的不能为空 start
			if (!StringUtil.isNotEmpty(dzImportDto.getInvAim()) && StringUtils.isNotEmpty(dzImportDto.getFinprodType2())
					&&  (!dzImportDto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_基金.getValue()) &&
					!dzImportDto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue()) )) {
				checkElement(dzImportDto,promptMsg,null,obj-> {
					return etrCommonService.checkIsBlank(dzImportDto.getInvAim() == null ? "" :dzImportDto.getInvAim().toString(),
							"EtrExchangeTrade001.invAim", lineNo);
				});
			}
			//add by chenjiaxing 校验投资目的不能为空 end
			// 指令价格,全价价格,收益率
			checkElement(dzImportDto,promptMsg,item->{
				//若是债券计算三者(净价全价收益率)字段联动结果
				if(StringUtil.isBlank(promptMsg.toString())&&StringUtil.equals(item.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
					this.calPriceYield(item);;
				}
			},obj-> {
				return this.checkPriceRule(dzImportDto, "EtrExchangeTrade003.price");
			});
			// 目标类型
			checkElement(dzImportDto,promptMsg,null,obj-> {
				return etrCommonService.checkIsBlank(dzImportDto.getTargetType(), "EtrExchangeTrade001.targetType", lineNo);
			});
			// 指令数量
			checkElement(dzImportDto,promptMsg,null,obj-> {
				if(StringUtil.equals(obj.getTargetType(),DictCodeListExt3.目标类型_数量.getValue())) {
					return etrCommonService.checkIsBlank(null==dzImportDto.getEntrustNum()?null:dzImportDto.getEntrustNum().toString(), "EtrExchangeTrade001.entrustNum", lineNo);
				};
				if(StringUtil.equals(obj.getTargetType(),DictCodeListExt3.目标类型_金额.getValue())) {
					return etrCommonService.checkIsBlank(null==dzImportDto.getEntrustAmt()?null:dzImportDto.getEntrustAmt().toString(), "EtrExchangeTrade001.entrustAmt", lineNo);
				}
				return StringUtil.EMPTY;
			},obj-> {
				//指令数量控制在最小交易单位的整数倍（在前面数据无错误的情况下才校验，以防止资产代码为空等缺失条件）
				return StringUtil.isNotEmpty(promptMsg.toString())?StringUtil.EMPTY:this.checkEntrustMinTrdUnit(obj);
			});

			// 交易对手是否存在
			if(StringUtil.isNotEmpty(dzImportDto.getCounterId())){
				checkElement(dzImportDto,promptMsg,null,obj-> {
					Map<String, Object> param = new HashMap();
					param.put("counterIdName",obj.getCounterId());
					List<SelectOptionDto> result = this.selectCounterIdByCounterType(param);
					if(result!=null&&result.size()>0){
						dzImportDto.setCounterId(result.get(0).getId());
					}else{
						dzImportDto.setCounterId("");
					}
					return StringUtil.EMPTY;
				});
			}

			if (StringUtil.isBlank(promptMsg.toString())) {
				dzImportDto.setSeqNo(String.valueOf(jsonArrays.size()+1));
				getEntrustListData(dzImportDto);
				jsonArrays.add(JSONArray.toJSONString(dzImportDto));
			} else {
				etrCommonService.spliceMessage(totalMsg, promptMsg.toString());
				errorNum++;
			}
		};
		etrCommonService.spliceMessage(totalMsg, getMessage("ETR_C_0001", String.valueOf(errorNum)));
		Map<String, Object> resultMap = new HashMap<>();
		if (StringUtil.isNotBlank(totalMsg.toString())) {
			resultMap.put("confirmMsg", totalMsg.toString());
		}
		resultMap.put("correctData", JSONArray.toJSONString(jsonArrays));
		AjaxResult wr = new AjaxResult();
		wr.setData(resultMap);
		return wr;
	}

	/**
	 * 指令数量控制在最小交易单位的整数倍
	 * @param unit
	 * @param <T>
	 * @return
	 */
	private <T extends EtrExchangeTrade001Dto> String checkEntrustMinTrdUnit(T unit){
		MinTrdUnitLimitDto minTrdUnitLimitDto =getMinTrdUnitLimit(unit);
		calEntrustNumOrPrice(unit,minTrdUnitLimitDto);
		if (null != minTrdUnitLimitDto) {
			return checkMinTrdUnit(unit, minTrdUnitLimitDto, String.valueOf(Integer.valueOf(unit.getSeqNo())+1));
		}
		return StringUtil.EMPTY;
	}

	public  <T extends EtrExchangeTrade001Dto> void calEntrustNumOrPrice(T unit,MinTrdUnitLimitDto minTrdUnitLimitDto){
		if (null != unit.getEntrustNum()) {
			//格式化指令数量 最小交易单位整数倍
			BigDecimal entrustNum = this.formatEntrustNum(unit.getEntrustNum(), null==minTrdUnitLimitDto?getMinTrdUnitLimit(unit):minTrdUnitLimitDto);
			unit.setEntrustNum(entrustNum);
			unit.setEntrustAmt(calNumAndAmt(unit,item->{
				item.put("entrustNum", unit.getEntrustNum());
				item.put("targetType", DictCodeListExt3.目标类型_数量.getValue());
				item.put("minTrdUnitLimitDto", null==minTrdUnitLimitDto?getMinTrdUnitLimit(unit):minTrdUnitLimitDto);
			}).get("entrustAmt"));
		} else {
			// 重新计算指令数量
			unit.setEntrustNum(calNumAndAmt(unit,item->{
				item.put("entrustAmt", unit.getEntrustAmt());
				item.put("targetType", DictCodeListExt3.目标类型_金额.getValue());
				item.put("minTrdUnitLimitDto", null==minTrdUnitLimitDto?getMinTrdUnitLimit(unit):minTrdUnitLimitDto);
			}).get("entrustNum"));
		}
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
	private <T extends EtrExchangeTrade001Dto> Map<String, BigDecimal> calNumAndAmt(T unit,Consumer<Map> consumer){
		// 重新计算指令金额
		Map<String, Object> paramMap = BeanUtil.object2Map(unit);
		paramMap.put("currentTab", unit.getTradePlat());
		consumer.accept(paramMap);
		Map<String, BigDecimal> result = calcEntrustNumAndAmt(paramMap);
		return result;
	}

	/**
	 * 检查指令价格，全价收益率等是否符合规则
	 * @param t
	 * @param labelName
	 * @param <T>
	 * @return
	 */
	private <T extends EtrExchangeTrade001Dto> String checkPriceRule(T t,String labelName){
		if(!StringUtil.equals(t.getFinprodType2(),DictCodeListExt1.金融产品类型_投管用_债券.getValue())){
			return this.checkEntrustNumber(t.getEntrustPrice(), labelName, String.valueOf(Integer.valueOf(t.getSeqNo())+1));
		}
		else{
			//若是债券类型，指令价格，全价，收益率三个字段
			if(StringUtil.isEmptyList(Arrays.asList(t.getEntrustPrice(),t.getFprice(),t.getYield()))){
				return getMessage("ETR_E_0039", getMessage(labelName), Integer.valueOf(t.getSeqNo())+1);
			}
		}
		return StringUtil.EMPTY;
	}

	/**
	 * 计算债券净价全价收益率三者联动
	 * @param t
	 * @param <T>
	 */
	private <T extends EtrExchangeTrade001Dto> void calPriceYield(T t){
		Map<String, Object> param = new HashMap<>();
		param.put("finprodId", t.getFinprodId());
		param.put("prinAmt", AssetCommonUtil.getFaceVal(t.getFinprodId()));//面值
		param.put("tradeMarket", t.getTradeMarket());
		param.put("ps", t.getPs());
		if (null != t.getEntrustPrice()) {
			param.put("calculateField", DictCodeListExt3.债券计算模式_单位净价.getValue());
			param.put("price", formatEntrustPrice(t.getEntrustPrice(), calMinPrice(t,t.getEntrustPrice())));
		} else if (null != t.getFprice()) {
			param.put("calculateField", DictCodeListExt3.债券计算模式_单位全价.getValue());
			param.put("fprice", formatEntrustPrice(t.getFprice(), calMinPrice(t,t.getFprice())));
		} else if (null != t.getYield()) {
			param.put("calculateField", DictCodeListExt3.债券计算模式_到期收益率.getValue());
			param.put("yield", t.getYield());
		}
		Map<String, BigDecimal> result = calcPriceAndYield(param);
		t.setPrice(formatEntrustPrice(result.get("cprice"), calMinPrice(t,result.get("cprice"))));
		t.setFprice(formatEntrustPrice(result.get("fprice"), calMinPrice(t,result.get("fprice"))));
		t.setYield(result.get("yield"));
		t.setEntrustPrice(t.getPrice());
	}

	/**
	 * 净价计算最小价差
	 * @param t
	 * @param price
	 * @param <T>
	 * @return
	 */
	private <T extends EtrExchangeTrade001Dto> MinPirceDto calMinPrice(T t,BigDecimal price){
		return getInterfSysMinPrice(t.getFinprodId(), t.getTradePlat(), t.getTradeMarket(), t.getFinprodType2(),price);
	}

	/**
	 * 检查组合关联的券商账户个数
	 * @param t
	 * @param <T>
	 */
	private <T extends EtrExchangeTrade001Dto> String checkSecAcctCount(T t){
		//当组合下绑定多个券商资金账号时需要输入资金账号代码，若不输入则默认取组合下的第一个资金账户；
		// 当组合下只绑定一个资金账户时无需输入
		Map<String, Object> param = new HashMap<>();
		param.put("portfolioId", t.getPortfolioId());
		//add by cehnjiaxing 导入联动券商账户时查询交易场所 20210826 start
		if(StringUtil.isNotEmpty(t.getFinprodId())){
			String tradeMarket = t.getFinprodId();
			String[] arr = StringUtil.split(tradeMarket, ".");
			if(StringUtil.equals(arr[1],"SH")){
				param.put("tradeMarket", DictCodeListExt1.交易场所_上海证券交易所.getValue());
			}else if(StringUtil.equals(arr[1],"SZ")){
				param.put("tradeMarket", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
			}
		}
		//add by cehnjiaxing 导入联动券商账户时查询交易场所 20210826 end
		List<SelectOptionDto> ptlSecAcctList = getPtlSecAcctInfo(param);
		if(StringUtil.isEmptyList(ptlSecAcctList)){
			return getMessage("ETR_E_0037", t.getPortfolioId(), Integer.valueOf(t.getSeqNo())+1);
		}//add by chenjiaxing 添加券商账户的校验，导入组合有多个券商账户是，券商账户字段必填 start
		else if(ptlSecAcctList.size() > 1 && StringUtil.isEmpty(t.getAcctNo())){
			return getMessage("ETR_E_0041", t.getPortfolioId(), Integer.valueOf(t.getSeqNo())+1);
		}
		//add by chenjiaxing 添加券商账户的校验，导入组合有多个券商账户是，券商账户字段必填 end
		else{
			if(StringUtil.isEmpty(t.getAcctNo())||ptlSecAcctList.size()==1){
				t.setAcctNo(ptlSecAcctList.get(0).getId());
			}else if(!ptlSecAcctList.stream().anyMatch(e->StringUtil.equals(e.getId(),t.getAcctNo()))&&ptlSecAcctList.size()>1){
				return getMessage("ETR_E_0038", t.getPortfolioId(), Integer.valueOf(t.getSeqNo())+1);
			}
			return StringUtil.EMPTY;
		}
	}

	/**
	 * 检查资产代码是否符合规则
	 * @param t
	 * @param labelName
	 * @param <T>
	 * @return
	 */
	private <T extends EtrExchangeTrade001Dto> String checkAstScope(T t,String labelName){
		Map<String, Object> map = new HashMap<>();
		map.put("initValue", t.getFinprodId());
		map.put("tradePlat", t.getTradePlat());
		Page<SelectOptionDto> dtoPage = getFinprodMarketInfo(map);
		if(null==dtoPage||StringUtil.isEmptyList(dtoPage.getRoot())){
			return getMessage("ETR_E_0017", getMessage(labelName), Integer.valueOf(t.getSeqNo())+1);
		}
		FinProduct finProduct=BeanUtil.mapToObject((Map)dtoPage.getRoot().get(0).getObject(),FinProduct.class);
		t.setFinprodId(finProduct.getFinprodId());
		t.setFinprodType2(finProduct.getFinprodType2());
		t.setTradeMarket(finProduct.getTradeMarket());

		//债券判断资产日期与平台日
		if(StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue(),finProduct.getFinprodType2())) {
			AssetGenerateEntry assetGenerateEntry=AssetCommonUtil.getAssetInfoDb(t.getFinprodId(), item -> item);
			if (DateUtil.compareDate(DateUtil.platDateFromCache(),assetGenerateEntry.getMdate()) > 0) {
				return getMessage("ETR_E_0040", getMessage("EtrExchangeTradeImport.mdate"),getMessage("TrdOptionTrade001.platDate"), Integer.valueOf(t.getSeqNo()) + 1);
			}
			if (DateUtil.compareDate(DateUtil.platDateFromCache(), assetGenerateEntry.getVdate()) < 0) {
				return getMessage("ETR_E_0040", getMessage("TrdOptionTrade001.platDate"),getMessage("EtrExchangeTradeImport.vdate"), Integer.valueOf(t.getSeqNo()) + 1);
			}
		}
		return StringUtil.EMPTY;
	}

	/**
	 * excel导入功能
	 *
	 * @param paramMap
	 *            文件名称
	 * @return 检验list
	 * @throws Exception
	 *             异常
	 * @see
	 * @since 1.0
	 */
	@SuppressWarnings({ "rawtypes", "static-access", "unchecked" })
	@Transactional(rollbackFor = Exception.class)
	public AjaxResult checkImportData(Map<String,Object> paramMap) throws Exception {
		try {
			ExcelUtil excelUtil = ExcelUtil.build(FileNameUtil.getUploadFilePath(StringUtil.isNotEmpty(paramMap.get("fileName")) ? paramMap.get("fileName").toString() : StringUtil.EMPTY), null);
			List<List<String>> objects = excelUtil.readToList(0, 1, 0);
			// TISHKS-206:导入数据行数判断是否大于100 fuyongsheng 20211105 edit start
			if (objects.size() > 100) {
				// TISHKS-206:导入数据行数判断是否大于100 fuyongsheng 20211105 edit end
				Map<String, Object> resultMap = new HashMap<>();
				resultMap.put("errorMsg", getMessage("ETR_E_0021"));
				AjaxResult wr = new AjaxResult();
				wr.setData(resultMap);
				return wr;
			}
			//---add by 裴家俊 固收与大宗指令导入 20210727 start-------
			String 平台类型 = (String) paramMap.getOrDefault("currentTab", StringUtil.EMPTY);
			if (StringUtil.equals(DictCodeList.交易平台_大宗交易平台.getValue(), 平台类型)) {
				return checkDzQyImportData(objects, rowData -> {
					EtrExchangeTradeDzImportDto dzImportDto = AssetCommonUtil.parseArrayToEntry(rowData.toArray(new String[rowData.size()]), EtrExchangeTradeDzImportDto.class);
					dzImportDto.setTradePlat(平台类型);
					return dzImportDto;
				});
			} else if (StringUtil.equals(DictCodeList.交易平台_固定收益平台.getValue(), 平台类型)) {
				return checkDzQyImportData(objects, rowData -> {
					EtrExchangeTradeGsImportDto dzImportDto = AssetCommonUtil.parseArrayToEntry(rowData.toArray(new String[rowData.size()]), EtrExchangeTradeGsImportDto.class);
					dzImportDto.setTradePlat(平台类型);
					dzImportDto.setEntrustPrice(dzImportDto.getPrice());//指令价格默认为净价
					return dzImportDto;
				});
			}
			//---add by 裴家俊 固收与大宗指令导入 20210727 end-------
			else {
				String totalMsg = StringUtil.EMPTY;
				JSONArray jsonArrays = new JSONArray();
				// 失败条数
				int errorNum = 0;
				for (int i = 0; i < objects.size(); i++) {
					String confirmMsg = "";
					String promptMsg = "";
					final int scale = 3;

					/********* 资产的属性； **********************/
					String marketId = null;
					String assetType = null;
					String othCodeType = null;
					/***************************************/
					MinTrdUnitLimitDto minTrdUnitLimitDto = null;
					MinPirceDto minPirceDto = null;
					EtrExchangeTrade001Dto unit = new EtrExchangeTrade001Dto();
					// excel的行号
					String lineNo = String.valueOf(i + 2);
					List<String> rowData = objects.get(i);
					// 委托开始日日期非空
					confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
							etrCommonService.checkIsBlank(rowData.get(12), "EtrExchangeTradeCommon001.entrustDate", lineNo));
					// 委托开始日日期格式check
					confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
							etrCommonService.checkIsDate(rowData.get(12), "EtrExchangeTradeCommon001.entrustDate", lineNo));
//					// 委托结束日日期非空
//					confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
//							etrCommonService.checkIsBlank(rowData.get(12), "EtrExchangeTrade001.mdate", lineNo));
//					// 委托结束日日期格式check
//					confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
//							etrCommonService.checkIsDate(rowData.get(12), "EtrExchangeTrade001.mdate", lineNo));
					/**** 委托开始日与终止日必须是平台日 ***/
					if (StringUtil.isBlank(confirmMsg)) {
						confirmMsg = etrCommonService.checkIsPlateDate(rowData.get(12), rowData.get(12),
								lineNo);
					}
					if (StringUtil.isBlank(confirmMsg)) {
						// 委托开始日
						unit.setVdate(DateUtil.parse(rowData.get(12)));
						// 委托结束日
						unit.setMdate(DateUtil.parse(rowData.get(12)));
					} else {
						// 设置提示信息
						promptMsg = confirmMsg;
						// 清空confirmMsg
						confirmMsg = "";
					}
					// 组合为空
					confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService
							.checkIsBlank(rowData.get(1), "EtrExchangeTrade001.portfolioId", lineNo));
					// 组合不存在
					confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService
							.checkPortfolioIdExist(rowData.get(1), "EtrExchangeTrade001.portfolioId", lineNo));

					// 设置组合
					if (StringUtil.isBlank(confirmMsg)) {
						unit.setPortfolioId(rowData.get(1));
					} else {
						promptMsg = etrCommonService.StringAddByBr(promptMsg, confirmMsg);
						// 清空confirmMsg
						confirmMsg = "";
					}

					// 资产代码
					confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
							etrCommonService.checkIsBlank(rowData.get(3), "EtrExchangeTrade001.secId", lineNo));
					if (StringUtil.isBlank(confirmMsg)) {
						Map assetMap;
						Map<String, Object> map = new HashMap<>();
						map.put("initValue", rowData.get(3));
						map.put("tradePlat", DictCodeList.交易平台_竞价撮合平台.getValue());
						map.put("page", 1);
						map.put("length", 20);
						Page<SelectOptionDto> finprodIds = getFinprodMarketInfo(map);
						if (finprodIds.getRoot().isEmpty()) {
							confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
									getMessage("ETR_E_0017", getMessage("EtrExchangeTrade001.secId"), lineNo));
						} else {
							assetMap = (Map) finprodIds.getRoot().get(0).getObject();
							if (assetMap != null) {
								unit.setFinprodId(assetMap.get("finprodId") != null ? assetMap.get("finprodId").toString() : "");
								unit.setFinprodName(assetMap.get("finprodName") != null ? assetMap.get("finprodName").toString() : "");
								unit.setTradeMarket(assetMap.get("tradeMarket") != null ? assetMap.get("tradeMarket").toString() : "");
								unit.setFinprodType2(assetMap.get("finprodType2") != null ? assetMap.get("finprodType2").toString() : "");
								unit.setTradePlat(DictCodeList.交易平台_竞价撮合平台.getValue());
								marketId = (assetMap.get("marketId") != null ? assetMap.get("marketId").toString() : "");
								othCodeType = (assetMap.get("othCodeType") != null) ? assetMap.get("othCodeType").toString() : null;
								assetType = (assetMap.get("assetType1") != null) ? assetMap.get("assetType1").toString() : null;
							}
						}
					}

					if (StringUtil.isBlank(confirmMsg)) {
						// 设置资产代码
						unit.setStockId(rowData.get(3));
						// 判断是否交易时间段
						if (isNonTradingTime(unit)) {
							confirmMsg = etrCommonService.StringAddByBr(confirmMsg, this.getMessage("ETR_ERROR_0035", lineNo));
							if (!StringUtil.isBlank(confirmMsg)) {
								promptMsg = etrCommonService.StringAddByBr(promptMsg, confirmMsg);
								confirmMsg = "";
							}
						}
					} else {
						promptMsg = etrCommonService.StringAddByBr(promptMsg, confirmMsg);
						// 清空confirmMsg
						confirmMsg = "";
					}

					// 交易方向非空
					confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
							etrCommonService.checkIsBlank(rowData.get(4), "EtrExchangeTrade001.ps", lineNo));
					if (StringUtil.isBlank(confirmMsg)) {
						String ps = convertToItemCode(DictCode.交易方向.getValue(), rowData.get(4));
						if (StringUtils.isEmpty(ps)) {
							confirmMsg = etrCommonService.StringAddByBr(confirmMsg, this.getMessage("ETR_ERROR_0036", lineNo));
						}
					}

					if (StringUtils.isEmpty(confirmMsg)) {
						// 设置交易方向
						unit.setPs(convertToItemCode(DictCode.交易方向.getValue(), rowData.get(4)));
					} else {
						promptMsg = etrCommonService.StringAddByBr(promptMsg, confirmMsg);
						confirmMsg = "";
					}

					//check账户信息
					if (StringUtils.isNotEmpty(unit.getTradeMarket()) && StringUtils.isNotEmpty(unit.getPortfolioId())) {
						Map<String, Object> param = new HashMap<>();
						param.put("portfolioId", unit.getPortfolioId());
						param.put("tradeMarket", unit.getTradeMarket());
						List<SelectOptionDto> ptlSecAcctList = getPtlSecAcctInfo(param);
						if (null != ptlSecAcctList && ptlSecAcctList.size() > 0) {
							if (StringUtil.isNotEmpty(rowData.get(2))) {
//								if (rowData.get(2).contains(":")) {
//									if (ptlSecAcctList.stream().noneMatch(m -> m.getId().equals(rowData.get(2)))) {
//										confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
//												getMessage("ETR_E_0017", getMessage("EtrExchangeTrade001.acctNo"), lineNo));
//									}
//								} else {
//									confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
//											getMessage("ETR_E_0042", getMessage("EtrExchangeTrade001.acctNo"), getMessage("EtrExchangeTrade001.acctNo"), lineNo));
//								}
								//excel改成输入不带冒号的
								if (ptlSecAcctList.stream().noneMatch(m -> m.getId().split(":")[1].equals(rowData.get(2)))) {
									confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
											getMessage("ETR_E_0017", getMessage("EtrExchangeTrade001.acctNo"), lineNo));
								}
								//add by chenjiaixng 添加多券商资金账户时，资金账号代码必填校验 20210827 start
							} else {
								if (ptlSecAcctList.size() > 1) {
									confirmMsg = etrCommonService.StringAddByBr(confirmMsg, this.getMessage("ETR_ERROR_0061", lineNo));
								}
							}
							//add by chenjiaixng 添加多券商资金账户时，资金账号代码必填校验 20210827 end
						} else {
							confirmMsg = etrCommonService.StringAddByBr(confirmMsg, this.getMessage("ETR_ERROR_0037", lineNo));
						}

						if (!StringUtils.isEmpty(confirmMsg)) {
							promptMsg = etrCommonService.StringAddByBr(promptMsg, confirmMsg);
							// 清空confirmMsg
							confirmMsg = "";
						}
					}

					//check 资产类型与交易方向匹配问题
					if (StringUtils.isNotEmpty(unit.getFinprodType2()) && StringUtil.isNotEmpty(unit.getPs())) {
						confirmMsg = checkAssetPs(unit, assetType, othCodeType, lineNo);

						if (!StringUtils.isEmpty(confirmMsg)) {
							promptMsg = etrCommonService.StringAddByBr(promptMsg, confirmMsg);
							// 清空confirmMsg
							confirmMsg = "";
						}
					}
					//add by chenjiaxing 校验投资目的不能为空 start
					if (!StringUtil.isNotEmpty(rowData.get(11)) && StringUtils.isNotEmpty(unit.getFinprodType2())
							&& unit.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue())) {
						confirmMsg = etrCommonService.StringAddByBr(confirmMsg, etrCommonService
								.checkIsBlank(rowData.get(11), "EtrExchangeTrade001.invAim", lineNo));
					}
					if (StringUtil.isBlank(confirmMsg)) {
//						// 投资目的
//						String priceModel = convertToItemCode(DictCode.投资目的.getValue(), rowData.get(11));
//						unit.setInvAim(priceModel);
						// 业务模式
						String priceModel = convertToItemCode(DictCode.业务模式.getValue(), rowData.get(11));
						unit.setBusinessModel(priceModel);
					} else {
						// 设置提示信息
						promptMsg = confirmMsg;
						// 清空confirmMsg
						confirmMsg = "";
					}
					//add by chenjiaxing 校验投资目的不能为空 end

					//check价格模式
					if (StringUtils.isNotEmpty(unit.getPs())) {
						if (unit.getPs().equals(DictCodeList.交易方向_买入.getValue())
								|| unit.getPs().equals(DictCodeList.交易方向_卖出.getValue())
								|| unit.getPs().equals(DictCodeListExt3.交易方向_正回购.getValue())
								|| unit.getPs().equals(DictCodeListExt3.交易方向_逆回购.getValue())) {
							String priceModelCheck = etrCommonService.checkIsBlank(rowData.get(5),
									"EtrExchangeTrade003.priceModel", lineNo);
							confirmMsg = etrCommonService.StringAddByBr(confirmMsg, priceModelCheck);
							String priceModel = convertToItemCode(DictCode.价格模式.getValue(), rowData.get(5));
							if (StringUtils.isEmpty(priceModelCheck)) {
								if (StringUtils.isEmpty(priceModel)) {
									confirmMsg = etrCommonService.StringAddByBr(confirmMsg, this.getMessage("ETR_ERROR_0038", lineNo));
								}
							}
							if (StringUtils.isEmpty(confirmMsg)) {
								if (DictCodeListExt2.价格模式_指定价格.getValue().equals(priceModel)) {
									/*confirmMsg = etrCommonService.StringAddByBr(confirmMsg, this.getMessage("ETR_ST_007", lineNo));
									promptMsg = etrCommonService.StringAddByBr(promptMsg, confirmMsg);
									confirmMsg = "";*/
									unit.setPriceModel(DictCodeListExt2.价格模式_限价指令.getValue());
								} else {
									unit.setPriceModel(priceModel);
								}
							}
						} else {
							unit.setPriceModel(DictCodeListExt2.价格模式_指定价格.getValue());
						}
					}

					if (!StringUtils.isEmpty(confirmMsg)) {
						promptMsg = etrCommonService.StringAddByBr(promptMsg, confirmMsg);
						// 清空confirmMsg
						confirmMsg = "";
					}
					if (StringUtils.isNotEmpty(unit.getPs())) {
						//非债券买卖，check价格
						if (StringUtils.equals(unit.getPriceModel(), DictCodeListExt2.价格模式_市价指令.getValue())) {
							if (!(unit.getPs().equals(DictCodeList.交易方向_买入.getValue())
									|| unit.getPs().equals(DictCodeList.交易方向_卖出.getValue())
									|| unit.getPs().equals(DictCodeListExt3.交易方向_正回购.getValue())
									|| unit.getPs().equals(DictCodeListExt3.交易方向_逆回购.getValue()))) {
								confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
										this.getMessage("ETR_ERROR_0057", lineNo));
							} else {
								Map<String, Object> map = new HashMap();
								map.put("secId", unit.getStockId());
								Map<String, Object> hqPrice = findQuotations(map);
								if (hqPrice != null) {
									BigDecimal price = null;
									if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(unit.getFinprodType2())
											|| "ZYSHG".equals(unit.getFinprodType2()) && null != hqPrice.get("lastPrice")) {
										price = new BigDecimal(hqPrice.get("lastPrice").toString()).setScale(3, BigDecimal.ROUND_HALF_UP);
									} else if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(unit.getFinprodType2())
											|| DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(unit.getFinprodType2())) {
										if (unit.getPs().equals(DictCodeList.交易方向_买入.getValue()) && null != hqPrice.get("upperLimitPrice")) {
											price = new BigDecimal(hqPrice.get("upperLimitPrice").toString()).setScale(3, BigDecimal.ROUND_HALF_UP);
										} else if (unit.getPs().equals(DictCodeList.交易方向_卖出.getValue()) && null != hqPrice.get("lowerLimitPrice")) {
											price = new BigDecimal(hqPrice.get("lowerLimitPrice").toString()).setScale(3, BigDecimal.ROUND_HALF_UP);
										}
									}
									if (price == null || price.compareTo(BigDecimal.ZERO) == 0) {
										price = new BigDecimal(hqPrice.get("preClosePrice").toString()).setScale(3, BigDecimal.ROUND_HALF_UP);
										//price = hqPrice.ge
								/*if (StringUtils.isNotEmpty(unit.getFinprodId()) && null != unit.getVdate()) {
									Map<String, Object> mapHq = new HashMap();
									mapHq.put("secId", unit.getStockId());
									mapHq.put("date", DateUtil.format(unit.getVdate()));
									if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(unit.getFinprodType2())) {
										String bondHqPrice = qryMstBondMarketPrice(mapHq);
										if (StringUtils.isNotEmpty(bondHqPrice)) {
											price = new BigDecimal(bondHqPrice);
										}
									}
									if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(unit.getFinprodType2())) {
										String stockHqPrice = qryMstStockMarketPrice(mapHq);
										if (StringUtils.isNotEmpty(stockHqPrice)) {
											price = new BigDecimal(stockHqPrice);
										}
									}
									if (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(unit.getFinprodType2())) {
										String fundPrice = qryMstFundMarketPrice(mapHq);
										if (StringUtils.isNotEmpty(fundPrice)) {
											price = new BigDecimal(fundPrice);
										}
									}

								}*/
									}
									if (price == null || price.compareTo(BigDecimal.ZERO) == 0) {
										confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
												this.getMessage("ETR_ERROR_0058", lineNo));
									} else {
										unit.setEntrustPrice(price);
									}
								} else {
									confirmMsg = etrCommonService.StringAddByBr(confirmMsg, this.getMessage("ETR_ERROR_0058", lineNo));
								}
							}
						} else {
							if (!(DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(unit.getFinprodType2())
									&& isStandardPs(unit.getPs()))) {
								// 这些价格取值特殊，不check价格
								if (!(DictCodeList.交易方向_行权_回售.getValue().equals(unit.getPs())
										|| DictCodeListExt3.交易方向_配债.getValue().equals(unit.getPs())
										|| DictCodeListExt2.交易方向_配股.getValue().equals(unit.getPs())
										|| DictCodeListExt3.交易方向_质押券出库.getValue().equals(unit.getPs())
										|| DictCodeListExt3.交易方向_质押券入库.getValue().equals(unit.getPs())
										|| DictCodeListExt1.交易方向_债转股.getValue().equals(unit.getPs())
										|| (DictCodeList.交易方向_申购.getValue().equals(unit.getPs())
										&& DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(unit.getFinprodType2())))) {
									// 指令价格
									confirmMsg = checkEntrustNumber(rowData.get(6), "EtrExchangeTrade003.price", lineNo);

									if (StringUtils.isEmpty(confirmMsg)) {
										unit.setEntrustPrice(new BigDecimal(rowData.get(6)));
									} else {
										promptMsg = etrCommonService.StringAddByBr(promptMsg, confirmMsg);
										// 清空confirmMsg
										confirmMsg = "";
									}
								}
							} else {
								// 债券买卖，check净价、全价、收益率
								if (isNullCell(rowData.get(6)) && isNullCell(rowData.get(7)) && isNullCell(rowData.get(8))) {
									// 指令价格、全价、收益率同时为空
									confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
											this.getMessage("ETR_ERROR_0039", lineNo));
								} else if (StringUtils.isNotEmpty(rowData.get(6))) {
									// 净价
									confirmMsg = checkEntrustNumber(rowData.get(6), "EtrExchangeTrade003.price", lineNo);
									if (StringUtils.isEmpty(confirmMsg)) {
										unit.setEntrustPrice(new BigDecimal(rowData.get(6)));
									}
								} else if (StringUtils.isNotEmpty(rowData.get(7))) {
									// 全价
									confirmMsg = checkEntrustNumber(rowData.get(7), "EtrExchangeTrade001.fprice", lineNo);
									if (StringUtils.isEmpty(confirmMsg)) {
										unit.setFprice(new BigDecimal(rowData.get(7)));
									}
								} else if (StringUtils.isNotEmpty(rowData.get(8))) {
									// 收益率
									confirmMsg = checkEntrustNumber(rowData.get(8), "EtrExchangeTrade001.yield", lineNo);
									if (StringUtils.isEmpty(confirmMsg)) {
										unit.setYield(new BigDecimal(rowData.get(8)));
									}
								}
							}
						}

						if (!StringUtils.isEmpty(confirmMsg)) {
							promptMsg = etrCommonService.StringAddByBr(promptMsg, confirmMsg);
							// 清空confirmMsg
							confirmMsg = "";
						}

						// 以数量下单
						if ((DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(unit.getFinprodType2())
								|| "ZYSHG".equals(unit.getFinprodType2())
								|| DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(unit.getFinprodType2()))
								&& !isStandardPs(unit.getPs())) {
							// 指令数量
							confirmMsg = checkEntrustNumber(rowData.get(9), "EtrExchangeTrade001.entrustNum",
									lineNo);

							if (StringUtils.isEmpty(confirmMsg)) {
								unit.setEntrustNum(BigDecimal.valueOf(Math.floor(Double.parseDouble(rowData.get(9)))));
								unit.setTargetType(DictCodeListExt3.目标类型_数量.getValue());
							} else {
								promptMsg = etrCommonService.StringAddByBr(promptMsg, confirmMsg);
								// 清空confirmMsg
								confirmMsg = "";
							}
						} else {
							if (!isNullCell(rowData.get(9))) {
								// 指令数量
								confirmMsg = checkEntrustNumber(rowData.get(9), "EtrExchangeTrade001.entrustNum",
										lineNo);
								if (StringUtils.isEmpty(confirmMsg)) {
									unit.setEntrustNum(BigDecimal.valueOf(Math.floor(Double.parseDouble(rowData.get(9)))));
									unit.setTargetType(DictCodeListExt3.目标类型_数量.getValue());
								}
							} else if (!isNullCell(rowData.get(10))) {
								// 指令金额
								confirmMsg = checkEntrustNumber(rowData.get(10), "EtrExchangeTrade001.entrustAmt",
										lineNo);
								if (StringUtils.isEmpty(confirmMsg)) {
									unit.setEntrustAmt(new BigDecimal(rowData.get(10)));
									unit.setTargetType(DictCodeListExt3.目标类型_金额.getValue());
								}
							} else {
								// 指令金额、数量之一必填
								confirmMsg = etrCommonService.StringAddByBr(confirmMsg, this.getMessage("ETR_ERROR_0040", lineNo));
							}

							if (!StringUtils.isEmpty(confirmMsg)) {
								promptMsg = etrCommonService.StringAddByBr(promptMsg, confirmMsg);
								// 清空confirmMsg
								confirmMsg = "";
							}
						}

						if (!StringUtils.isEmpty(confirmMsg)) {
							promptMsg = etrCommonService.StringAddByBr(promptMsg, confirmMsg);
							// 清空confirmMsg
							confirmMsg = "";
						}

						if (StringUtils.isEmpty(promptMsg)) {
							if (DictCodeListExt3.交易方向_质押券出库.getValue().equals(unit.getPs())
									|| DictCodeListExt3.交易方向_质押券入库.getValue().equals(unit.getPs())) {
								Map<String, Object> map3 = new HashMap<>();
								map3.put("finprodId", unit.getFinprodId());
								String stdsecConverseRatio = selectStdsecConverseRatio(map3);
								unit.setEntrustPrice(new BigDecimal(stdsecConverseRatio).setScale(scale, BigDecimal.ROUND_HALF_UP));
							} else if (DictCodeListExt1.交易方向_债转股.getValue().equals(unit.getPs())) {
								if (StringUtils.isNotEmpty(unit.getFinprodId())) {
									FinProductConvertible finProductConvertible = finProductConvertibleMapper.selectByPrimaryKey(unit.getFinprodId());
									if (null != finProductConvertible) {
										unit.setEntrustPrice(finProductConvertible.getConPrice().setScale(scale, BigDecimal.ROUND_HALF_UP));
									}
								}
							} else if (DictCodeList.交易方向_申购.getValue().equals(unit.getPs()) && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(unit.getFinprodType2())) {
								unit.setEntrustPrice(new BigDecimal(100).setScale(scale, BigDecimal.ROUND_HALF_UP));
							} else if (DictCodeListExt3.交易方向_配债.getValue().equals(unit.getPs()) || DictCodeListExt2.交易方向_配股.getValue().equals(unit.getPs())) {
								Map mappz = new HashMap<>();
								mappz.put("finprodMarketId", marketId);
								mappz.put("othCodeType", DictCodeListExt3.交易方向_配债.getValue().equals(unit.getPs()) ? DictCodeListExt3.金融产品其他代码类型_配债代码.getValue() : DictCodeListExt3.金融产品其他代码类型_配股代码.getValue());
								TrdOthCodeInfoDto pzDto = getTrdOthCodeInfo(mappz);
								if (null != pzDto && pzDto.getDeclaredPrice() != null && pzDto.getDeclaredPrice().compareTo(BigDecimal.ZERO) > 0) {
									unit.setEntrustPrice(pzDto.getDeclaredPrice().setScale(scale, BigDecimal.ROUND_HALF_UP));
								} else {
									confirmMsg = etrCommonService.StringAddByBr(confirmMsg, "申报信息需配置配股配债价格(行号为" + lineNo + ")。");
									if (!StringUtils.isEmpty(confirmMsg)) {
										promptMsg = etrCommonService.StringAddByBr(promptMsg, confirmMsg);
									}
									unit.setEntrustPrice(new BigDecimal(0));
								}
							} else if (DictCodeList.交易方向_行权_回售.getValue().equals(unit.getPs())) {
								// 查询行权计划
								Map<String, Object> planMap = new HashMap<>();
								planMap.put("finprodId", unit.getFinprodId());
								planMap.put("psT03", DictCodeList.交易方向_行权_回售.getValue());
								planMap.put("cashType2", DictCodeListExt1.现金流二级类型_本金.getValue());
								planMap.put("execType01", DictCodeList.行权状态_未行权.getValue());
								planMap.put("execType02", DictCodeList.行权状态_已行权.getValue());
								List<FinExercisePlan> lst = busiFinExercisePlanService.getfinExercisePlanByProductId(planMap);
								if (null != lst && lst.size() > 0) {
									Date vdate = unit.getVdate();
									if (null != vdate) {
										for (FinExercisePlan finExercisePlan : lst) {
											if (StringUtil.isNotEmpty(finExercisePlan.getVdate()) && StringUtil.isNotEmpty(finExercisePlan.getMdate())
													&& finExercisePlan.getVdate().getTime() - vdate.getTime() <= 0
													&& finExercisePlan.getMdate().getTime() - vdate.getTime() >= 0) {
												unit.setEntrustPrice(finExercisePlan.getExercisePrice().setScale(scale, BigDecimal.ROUND_HALF_UP));
											}
										}
									}
								}
							} else {// 指令价格
								if (null != unit.getEntrustPrice()) {
									if (null == minPirceDto) {
										minPirceDto = getInterfSysMinPrice(unit.getFinprodId(), unit.getTradePlat(), unit.getTradeMarket(), unit.getFinprodType2(), unit.getEntrustPrice());
									}
									BigDecimal entrustPrice = formatEntrustPrice(unit.getEntrustPrice(), minPirceDto);
									unit.setEntrustPrice(entrustPrice);
								}
							}
							if (DictCodeList.交易方向_行权_回售.getValue().equals(unit.getPs())
									|| DictCodeListExt3.交易方向_配债.getValue().equals(unit.getPs())
									|| DictCodeListExt2.交易方向_配股.getValue().equals(unit.getPs())
									|| DictCodeListExt3.交易方向_质押券出库.getValue().equals(unit.getPs())
									|| DictCodeListExt3.交易方向_质押券入库.getValue().equals(unit.getPs())
									|| DictCodeListExt1.交易方向_债转股.getValue().equals(unit.getPs())) {
								confirmMsg = checkEntrustNumber(unit.getEntrustPrice(), "EtrExchangeTrade003.price", lineNo);
								if (!StringUtils.isEmpty(confirmMsg)) {
									promptMsg = etrCommonService.StringAddByBr(promptMsg, confirmMsg);
								}
							}


							// 优先级 净价 > 全价 > 收益率 净价、全价、收益率 联动计算
							if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(unit.getFinprodType2())
									&& isStandardPs(unit.getPs())) {
								Map<String, Object> param = new HashMap<>();
								param.put("finprodId", unit.getFinprodId());
								param.put("prinAmt", new BigDecimal(100));
								param.put("tradeMarket", unit.getTradeMarket());
								param.put("ps", unit.getPs());
								if (null != unit.getEntrustPrice()) {
									//如果净价不为NULL
									unit.setCalculateField(DictCodeListExt3.债券计算模式_单位净价.getValue());
									param.put("calculateField", DictCodeListExt3.债券计算模式_单位净价.getValue());
									//净价计算最小价差
									if (null == minPirceDto) {
										minPirceDto = getInterfSysMinPrice(unit.getFinprodId(), unit.getTradePlat(), unit.getTradeMarket(), unit.getFinprodType2(), unit.getEntrustPrice());
									}
									param.put("price", formatEntrustPrice(unit.getEntrustPrice(), minPirceDto));
								} else if (null != unit.getFprice()) {
									unit.setCalculateField(DictCodeListExt3.债券计算模式_单位全价.getValue());
									param.put("calculateField", DictCodeListExt3.债券计算模式_单位全价.getValue());
									//全价计算最小价差
									if (null == minPirceDto) {
										minPirceDto = getInterfSysMinPrice(unit.getFinprodId(), unit.getTradePlat(), unit.getTradeMarket(), unit.getFinprodType2(), unit.getFprice());
									}
									param.put("fprice", formatEntrustPrice(unit.getFprice(), minPirceDto));
								} else if (null != unit.getYield()) {
									unit.setCalculateField(DictCodeListExt3.债券计算模式_到期收益率.getValue());
									param.put("calculateField", DictCodeListExt3.债券计算模式_到期收益率.getValue());
									param.put("yield", unit.getYield());
								}
								Map<String, BigDecimal> result = calcPriceAndYield(param);
								//净价计算最小价差
								if (null == minPirceDto) {
									minPirceDto = getInterfSysMinPrice(unit.getFinprodId(), unit.getTradePlat(), unit.getTradeMarket(), unit.getFinprodType2(), result.get("cprice"));
								}
								unit.setPrice(formatEntrustPrice(result.get("cprice"), minPirceDto));
								unit.setFprice(formatEntrustPrice(result.get("fprice"), minPirceDto));
								unit.setYield(result.get("yield"));
								unit.setEntrustPrice(unit.getPrice());
							}

							if (StringUtils.isNotEmpty(unit.getFinprodId()) && StringUtils.isNotEmpty(unit.getTradeMarket())
									&& StringUtils.isNotEmpty(unit.getFinprodType2())) {
								minTrdUnitLimitDto = getInterfSysMinTrdUnit(unit.getFinprodId(), unit.getTradePlat(),
										unit.getTradeMarket(), unit.getFinprodType2(), unit.getPs());
							}

							if (null != unit.getEntrustNum()) {
								//格式化指令数量 最小交易单位整数倍
								BigDecimal entrustNum = this.formatEntrustNum(unit.getEntrustNum(), minTrdUnitLimitDto);
								unit.setEntrustNum(entrustNum);
								// 重新计算指令金额
								Map<String, Object> map1 = new HashMap<>();
								map1.put("entrustPrice", unit.getEntrustPrice());
								map1.put("entrustNum", unit.getEntrustNum());
								map1.put("finprodId", unit.getFinprodId());
								map1.put("finprodType2", unit.getFinprodType2());
								map1.put("ps", unit.getPs());
								map1.put("tradeMarket", unit.getTradeMarket());
								map1.put("currentTab", "jj");
								map1.put("targetType", DictCodeListExt3.目标类型_数量.getValue());
								map1.put("minTrdUnitLimitDto", minTrdUnitLimitDto);
								Map<String, BigDecimal> result = calcEntrustNumAndAmt(map1);
								unit.setEntrustAmt(result.get("entrustAmt"));
							} else {
								// 重新计算指令数量
								Map<String, Object> map2 = new HashMap<>();
								map2.put("entrustPrice", unit.getEntrustPrice());
								map2.put("entrustAmt", unit.getEntrustAmt());
								map2.put("finprodId", unit.getFinprodId());
								map2.put("finprodType2", unit.getFinprodType2());
								map2.put("ps", unit.getPs());
								map2.put("tradeMarket", unit.getTradeMarket());
								map2.put("currentTab", "jj");
								map2.put("targetType", DictCodeListExt3.目标类型_金额.getValue());
								map2.put("minTrdUnitLimitDto", minTrdUnitLimitDto);
								Map<String, BigDecimal> result = calcEntrustNumAndAmt(map2);
								unit.setEntrustNum(result.get("entrustNum"));
							}
							if (null != minTrdUnitLimitDto) {
								confirmMsg = checkMinTrdUnit(unit, minTrdUnitLimitDto, lineNo);
							}
							if (!StringUtils.isEmpty(confirmMsg)) {
								promptMsg = etrCommonService.StringAddByBr(promptMsg, confirmMsg);
								// 清空confirmMsg
								confirmMsg = "";
								unit.setEntrustAmt(new BigDecimal(1000));
								unit.setEntrustNum(new BigDecimal(1000));
							}

							if (!(DictCodeList.交易方向_行权_回售.getValue().equals(unit.getPs())
									|| DictCodeListExt3.交易方向_质押券出库.getValue().equals(unit.getPs())
									|| DictCodeListExt3.交易方向_质押券入库.getValue().equals(unit.getPs())
									|| DictCodeListExt1.交易方向_债转股.getValue().equals(unit.getPs())
									|| (DictCodeList.交易方向_申购.getValue().equals(unit.getPs()) && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(unit.getFinprodType2())))
									&& StringUtil.isNotEmpty(unit.getEntrustPrice())) {
								Map map3 = new HashMap<>();
								map3.put("secId", unit.getStockId());
								if (null == minPirceDto) {
									minPirceDto = getInterfSysMinPrice(unit.getFinprodId(), unit.getTradePlat(), unit.getTradeMarket(), unit.getFinprodType2(), unit.getEntrustPrice());
								}
								int precision = getScale(minPirceDto);
								map3.put("scale", precision);
								EtrExchangeTrade001Dto limitDto = findBidQuotationsPage(map3);
								if (null != limitDto) {
									unit.setLimitUp(limitDto.getLimitUp());
									unit.setLimitDown(limitDto.getLimitDown());
								}
							}

							if (null != unit.getTradeMarket() && null != unit.getPortfolioId()) {
								Map<String, Object> param = new HashMap<>();
								param.put("portfolioId", unit.getPortfolioId());
								param.put("tradeMarket", unit.getTradeMarket());
								List<SelectOptionDto> ptlSecAcctList = getPtlSecAcctInfo(param);
								if (null != ptlSecAcctList && ptlSecAcctList.size() > 0) {
									SelectOptionDto sdto = ptlSecAcctList.get(0);
									unit.setAcctNo(sdto.getId());
									if (null != sdto.getObject()) {
										List<EtrExchangeTrade001Dto> ls = (ArrayList<EtrExchangeTrade001Dto>) sdto.getObject();
										if (ls != null && ls.size() > 0) {
											JSONObject json = (JSONObject) JSONObject.toJSON(ls.get(0));
											unit.setSecAcctId(json.get("secAcctId") != null ? json.get("secAcctId").toString() : "");
										}
									}
								}
							}
							//指令可用持仓查询
							Map<String, Object> mapSec = new HashMap<String, Object>();
							mapSec.put("portfolioId", unit.getPortfolioId());
							mapSec.put("finprodId", unit.getFinprodId());
							mapSec.put("ccy", DictCodeList.币种_人民币_CNY.getValue());
							mapSec.put("secAcctId", unit.getSecAcctId());
							BigDecimal vailableNum = selectAvailableNum(mapSec);
							if (StringUtil.isNotEmpty(vailableNum)) {
								unit.setAccAvailablePos(vailableNum);
								if(vailableNum.compareTo(new BigDecimal(0))<1){
									confirmMsg ="可用持仓不足(行号为" + lineNo + ")。";
									promptMsg = etrCommonService.StringAddByBr(promptMsg, confirmMsg);
									// 清空confirmMsg
									confirmMsg = "";
								}
							}
							unit.setTradePlat(DictCodeList.交易平台_竞价撮合平台.getValue());
							settleEntrustType(unit);
							AjaxResult ajax = checkSave(unit);
							if (ajax != null && !StringUtil.isEmptyList(ajax.getErrorList())) {
								for (ValidMessage validMessage : ajax.getErrorList()) {
									confirmMsg = etrCommonService.StringAddByBr(confirmMsg, validMessage.getMessage() + "(行号为" + lineNo + ")。");
								}
							}
							if (!StringUtils.isEmpty(confirmMsg)) {
								promptMsg = etrCommonService.StringAddByBr(promptMsg, confirmMsg);
							}
						}
					}

					if (StringUtil.isBlank(promptMsg)) {
						int size = jsonArrays.size();
						unit.setSeqNo(String.valueOf(size + 1));
						unit = getEntrustListData(unit);
						jsonArrays.add(JSON.toJSONString(unit));
					} else {
						totalMsg = etrCommonService.StringAddByBr(totalMsg, promptMsg);
						errorNum++;
					}
				}
				totalMsg = etrCommonService.StringAddByBr(totalMsg, getMessage("ETR_C_0001", String.valueOf(errorNum)));
				Map<String, Object> resultMap = new HashMap<>();
				if (StringUtil.isNotBlank(totalMsg)) {
					resultMap.put("confirmMsg", totalMsg);
				}
				resultMap.put("correctData", JSONArray.toJSONString(jsonArrays));
				AjaxResult wr = new AjaxResult();
				wr.setData(resultMap);
				return wr;
			}
		}catch (ArrayIndexOutOfBoundsException e){
			Map<String, Object> resultMap = new HashMap<>();
			resultMap.put("errorMsg", "文件格式不正确!");
			AjaxResult wr = new AjaxResult();
			wr.setData(resultMap);
			return wr;
		}
	}
	// add by gaopeng 20191219 多组合多资产导入 end

	/**
	 *
	 * @param etrExchangeTrade001Dto 指令对象
	 * @return 返回指令对象
	 */
	@SuppressWarnings(value = {"unchecked"})
	public <T extends EtrExchangeTrade001Dto> T getEntrustListData(T etrExchangeTrade001Dto) {
		//根据模板中固定的下标来赋值
		//etrExchangeTrade001Dto.setSeqNo(String.valueOf(i+1));
		//原excel中的行号
		//etrExchangeTrade001Dto.setLineNo(data.get(0).toString().trim());
		//组合代码
		//etrExchangeTrade001Dto.setPortfolioId(data.get(1).toString().trim());
		//资金账户
		//etrExchangeTrade001Dto.setAcctNo(data.get(2).toString().trim());
		//市场代码
		//etrExchangeTrade001Dto.setStockId(data.get(3).toString().trim());
		//根据资产代码查出联动值
            /*Map<String, Object> map = new HashMap<>();
            map.put("initValue", data.get(3).toString());
            map.put("tradePlat", DictCodeList.交易平台_竞价撮合平台.getValue());
            Page<SelectOptionDto> finprodIds = getFinprodMarketInfo(map);
            SelectOptionDto selectOptionDto = finprodIds.getRoot().get(0);
            String board = null;
            String marketId = null;
            if(selectOptionDto.getObject() != null){
				JSONObject json = (JSONObject) JSONObject.toJSON(selectOptionDto.getObject());
				etrExchangeTrade001Dto.setFinprodId(json.get("finprodId") != null ? json.get("finprodId").toString() :"");
				etrExchangeTrade001Dto.setFinprodName(json.get("finprodName") != null ? json.get("finprodName").toString() :"");
				etrExchangeTrade001Dto.setTradeMarket(json.get("tradeMarket") != null ? json.get("tradeMarket").toString() :"");
				etrExchangeTrade001Dto.setFinprodType2(json.get("finprodType2") != null ? json.get("finprodType2").toString() :"");
				board = (json.get("board") != null ? json.get("board").toString() :"");
				marketId = (json.get("marketId") != null ? json.get("marketId").toString() :"");
			}
            //交易方向
            etrExchangeTrade001Dto.setPs(convertToItemCode(DictCode.交易方向.getValue(), data.get(4).toString()));
			// 价格模式
			if (!etrExchangeTrade001Dto.getPs().equals(DictCodeList.交易方向_买入.getValue())
					&& !etrExchangeTrade001Dto.getPs().equals(DictCodeList.交易方向_卖出.getValue())
					&& !etrExchangeTrade001Dto.getPs().equals(DictCodeListExt3.交易方向_正回购.getValue())
					&& !etrExchangeTrade001Dto.getPs().equals(DictCodeListExt3.交易方向_逆回购.getValue())) {
				etrExchangeTrade001Dto.setPriceModel(DictCodeListExt2.价格模式_指定价格.getValue());
			} else {
				if (DictCodeListExt2.价格模式_指定价格.getValue().equals(convertToItemCode(DictCode.价格模式.getValue(), data.get(5).toString()))) {
					etrExchangeTrade001Dto.setPriceModel(DictCodeListExt2.价格模式_限价指令.getValue());
				} else {
					etrExchangeTrade001Dto.setPriceModel(convertToItemCode(DictCode.价格模式.getValue(), data.get(5).toString()));
				}
			}
			if ((!(DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(etrExchangeTrade001Dto.getFinprodType2())
					&& (etrExchangeTrade001Dto.getPs().equals(DictCodeListExt3.交易方向_债转股.getValue())
							|| etrExchangeTrade001Dto.getPs().equals(DictCodeListExt3.交易方向_质押券入库.getValue())
							|| etrExchangeTrade001Dto.getPs().equals(DictCodeListExt3.交易方向_质押券出库.getValue())
							|| etrExchangeTrade001Dto.getPs().equals(DictCodeList.交易方向_行权_回售.getValue())
							|| etrExchangeTrade001Dto.getPs().equals(DictCodeList.交易方向_申购.getValue())))
					        || etrExchangeTrade001Dto.getPs().equals(DictCodeListExt3.交易方向_配债.getValue()))
					&& !etrExchangeTrade001Dto.getPs().equals(DictCodeListExt2.交易方向_配股.getValue())) {
				if(!isNullCell(data.get(6))){
					etrExchangeTrade001Dto.setEntrustPrice(formatEntrustPrice(new BigDecimal(data.get(6).toString()),etrExchangeTrade001Dto.getFinprodType2(), etrExchangeTrade001Dto.getTradeMarket()));
				}
			}*/
		// 优先级 净价 > 全价 > 收益率 净价、全价、收益率 联动计算
/*			if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(etrExchangeTrade001Dto.getFinprodType2())
					&& isStandardPs(etrExchangeTrade001Dto.getPs())) {
				if (isNullCell(data.get(6))) {
					if (!isNullCell(data.get(7))) {
						etrExchangeTrade001Dto.setFprice(new BigDecimal(data.get(7).toString()));
					} else if (!isNullCell(data.get(8))) {
						etrExchangeTrade001Dto.setYield(new BigDecimal(data.get(8).toString()));
					}
				}
				Map<String, Object> param = new HashMap<>();
				param.put("finprodId", etrExchangeTrade001Dto.getFinprodId());
				param.put("prinAmt", new BigDecimal(100));
				param.put("tradeMarket", etrExchangeTrade001Dto.getTradeMarket());
				param.put("ps", etrExchangeTrade001Dto.getPs());
				etrExchangeTrade001Dto.setPrice(etrExchangeTrade001Dto.getEntrustPrice());
				if (null != etrExchangeTrade001Dto.getPrice()) {
					etrExchangeTrade001Dto.setCalculateField(DictCodeListExt3.债券计算模式_单位净价.getValue());
					param.put("calculateField", DictCodeListExt3.债券计算模式_单位净价.getValue());
					param.put("price", etrExchangeTrade001Dto.getPrice());
				} else if (null != etrExchangeTrade001Dto.getFprice()) {
					etrExchangeTrade001Dto.setCalculateField(DictCodeListExt3.债券计算模式_单位全价.getValue());
					param.put("calculateField", DictCodeListExt3.债券计算模式_单位全价.getValue());
					param.put("fprice", formatEntrustPrice(etrExchangeTrade001Dto.getFprice(),etrExchangeTrade001Dto.getFinprodType2(), etrExchangeTrade001Dto.getTradeMarket()));
				} else if (null != etrExchangeTrade001Dto.getYield()) {
					etrExchangeTrade001Dto.setCalculateField(DictCodeListExt3.债券计算模式_到期收益率.getValue());
					param.put("calculateField", DictCodeListExt3.债券计算模式_到期收益率.getValue());
					param.put("yield", etrExchangeTrade001Dto.getYield());
				}
				Map<String, BigDecimal> result = calcPriceAndYield(param);
				etrExchangeTrade001Dto.setPrice(formatEntrustPrice(result.get("cprice"),etrExchangeTrade001Dto.getFinprodType2(), etrExchangeTrade001Dto.getTradeMarket()));
				etrExchangeTrade001Dto.setFprice(formatEntrustPrice(result.get("fprice"),etrExchangeTrade001Dto.getFinprodType2(), etrExchangeTrade001Dto.getTradeMarket()));
				etrExchangeTrade001Dto.setYield(result.get("yield"));
				etrExchangeTrade001Dto.setEntrustPrice(etrExchangeTrade001Dto.getPrice());	
			}*/
			
			/*if (DictCodeListExt3.交易方向_质押券出库.getValue().equals(etrExchangeTrade001Dto.getPs())
					|| DictCodeListExt3.交易方向_质押券入库.getValue().equals(etrExchangeTrade001Dto.getPs())) {
				Map<String, Object> map3 = new HashMap<>();
				map3.put("finprodId", etrExchangeTrade001Dto.getFinprodId());
				String stdsecConverseRatio = selectStdsecConverseRatio(map3);
				etrExchangeTrade001Dto.setEntrustPrice(new BigDecimal(stdsecConverseRatio));
			} else if (DictCodeListExt1.交易方向_债转股.getValue().equals(etrExchangeTrade001Dto.getPs())) {
				if (StringUtils.isNotEmpty(etrExchangeTrade001Dto.getFinprodId())) {
					FinProductConvertible finProductConvertible = finProductConvertibleMapper.selectByPrimaryKey(etrExchangeTrade001Dto.getFinprodId());
					if (null != finProductConvertible) {
						etrExchangeTrade001Dto.setEntrustPrice(finProductConvertible.getConPrice());
					}
				}
			}else if (DictCodeList.交易方向_申购.getValue().equals(etrExchangeTrade001Dto.getPs()) && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(etrExchangeTrade001Dto.getFinprodType2())) {
				etrExchangeTrade001Dto.setEntrustPrice(new BigDecimal(100));
			}
			else if (DictCodeListExt3.交易方向_配债.getValue().equals(etrExchangeTrade001Dto.getPs()) || DictCodeListExt2.交易方向_配股.getValue().equals(etrExchangeTrade001Dto.getPs())) {
				Map mappz = new HashMap<>();
				mappz.put("secId", marketId);
				int scale = getScale(etrExchangeTrade001Dto.getFinprodType2(),etrExchangeTrade001Dto.getTradeMarket());
				mappz.put("scale", scale);
				EtrExchangeTrade001Dto pzDto = findBidQuotationsPage(mappz);
				etrExchangeTrade001Dto.setEntrustPrice(pzDto.getEntrustPrice());
			}
			else if(DictCodeList.交易方向_行权_回售.getValue().equals(etrExchangeTrade001Dto.getPs())){
				// 查询行权计划
				Map<String, Object> planMap = new HashMap<>();
				planMap.put("finprodId", etrExchangeTrade001Dto.getFinprodId());
				planMap.put("psT03", DictCodeList.交易方向_行权_回售.getValue());
				planMap.put("cashType2", DictCodeListExt1.现金流二级类型_本金.getValue());
				planMap.put("execType01", DictCodeList.行权状态_未行权.getValue());
				planMap.put("execType02", DictCodeList.行权状态_已行权.getValue());
				List<FinExercisePlan> lst = busiFinExercisePlanService.getfinExercisePlanByProductId(planMap);
				if (null != lst && lst.size() > 0) {
					String vdate = data.get(11).toString();
					if (StringUtils.isNotEmpty(vdate)) {
						for (FinExercisePlan finExercisePlan : lst) {
							if (StringUtil.isNotEmpty(finExercisePlan.getVdate()) && StringUtil.isNotEmpty(finExercisePlan.getMdate())
							    && finExercisePlan.getVdate().getTime() -  DateUtil.parse(vdate).getTime() <= 0
									&& finExercisePlan.getMdate().getTime() - DateUtil.parse(vdate).getTime() >= 0) {
								etrExchangeTrade001Dto.setEntrustPrice(finExercisePlan.getExercisePrice());
							}
						}
					}
				}
			}else {// 指令价格
				BigDecimal entrustPrice = formatEntrustPrice(etrExchangeTrade001Dto.getEntrustPrice(),
						etrExchangeTrade001Dto.getFinprodType2(), etrExchangeTrade001Dto.getTradeMarket());
				etrExchangeTrade001Dto.setEntrustPrice(entrustPrice);
			}*/
		//指令数量
		//etrExchangeTrade001Dto.setEntrustNum(new BigDecimal(data.get(9).toString()));
		//指令金额
		//etrExchangeTrade001Dto.setEntrustAmt(StringUtils.isNotEmpty(data.get(10).toString()) ? new BigDecimal(data.get(10).toString()) : null);
			/*if (StringUtils.isNotEmpty(data.get(9).toString())) {
				// 以数量下指令
				// 格式化指令数量
				etrExchangeTrade001Dto.setEntrustNum(new BigDecimal(data.get(9).toString()));
				BigDecimal entrustNum = this.formatEntrustNum(etrExchangeTrade001Dto.getEntrustNum(),
						etrExchangeTrade001Dto.getFinprodType2(), etrExchangeTrade001Dto.getPs(),
						etrExchangeTrade001Dto.getTradeMarket(), etrExchangeTrade001Dto.getTradePlat(), board);
				etrExchangeTrade001Dto.setEntrustNum(entrustNum);
				// 重新计算指令金额
				Map<String, Object> map1 = new HashMap<>();
				map1.put("entrustPrice", etrExchangeTrade001Dto.getEntrustPrice());
				map1.put("entrustNum", etrExchangeTrade001Dto.getEntrustNum());
				map1.put("finprodType2", etrExchangeTrade001Dto.getFinprodType2());
				map1.put("ps", etrExchangeTrade001Dto.getPs());
				map1.put("tradeMarket", etrExchangeTrade001Dto.getTradeMarket());
				map1.put("currentTab", "jj");
				map1.put("targetType", DictCodeListExt3.目标类型_数量.getValue());
				map1.put("board", board);
				Map<String, BigDecimal> result = calcEntrustNumAndAmt(map1);
				etrExchangeTrade001Dto.setEntrustAmt(result.get("entrustAmt"));
			} else {
				// 重新计算指令数量
				Map<String, Object> map2 = new HashMap<>();
				etrExchangeTrade001Dto.setEntrustAmt(StringUtils.isNotEmpty(data.get(10).toString()) ? new BigDecimal(data.get(10).toString()) : null);
				map2.put("entrustPrice", etrExchangeTrade001Dto.getEntrustPrice());
				map2.put("entrustAmt", etrExchangeTrade001Dto.getEntrustAmt());
				map2.put("finprodType2", etrExchangeTrade001Dto.getFinprodType2());
				map2.put("ps", etrExchangeTrade001Dto.getPs());
				map2.put("tradeMarket", etrExchangeTrade001Dto.getTradeMarket());
				map2.put("currentTab", "jj");
				map2.put("targetType", DictCodeListExt3.目标类型_金额.getValue());
				map2.put("board", board);
				Map<String, BigDecimal> result = calcEntrustNumAndAmt(map2);
				etrExchangeTrade001Dto.setEntrustNum(result.get("entrustNum"));
			}*/
		//委托开始日
		//etrExchangeTrade001Dto.setVdate(DateUtil.parse(data.get(11).toString()));
		//委托结束日
		//etrExchangeTrade001Dto.setMdate(DateUtil.parse(data.get(12).toString()));
		// 默认设置竞价平台
		//etrExchangeTrade001Dto.setTradePlat(DictCodeList.交易平台_竞价撮合平台.getValue());
			
			/*if (!isStandardPs(etrExchangeTrade001Dto.getPs())) {
				etrExchangeTrade001Dto.setTargetType(DictCodeListExt3.目标类型_数量.getValue());
			} else {
				if (null != etrExchangeTrade001Dto.getEntrustNum()) {
					etrExchangeTrade001Dto.setTargetType(DictCodeListExt3.目标类型_数量.getValue());
				} else if (StringUtils.isNotEmpty(data.get(10).toString().trim())) {
					etrExchangeTrade001Dto.setTargetType(DictCodeListExt3.目标类型_金额.getValue());
				}
			}*/

		Map<String, Object> param = new HashMap<>();
		param.put("portfolioId", etrExchangeTrade001Dto.getPortfolioId());
		param.put("ccy", DictCodeList.币种_人民币_CNY.getValue());
		//add by chenjiaxing 20210824 指令当日可用现金不对 start
		param.put("acctId", etrExchangeTrade001Dto.getAcctNo());
		//add by chenjiaxing 20210824 指令当日可用现金不对 end
//		BigDecimal cashAvailable = this.selectAccAvailableCash(param);
		BigDecimal cashAvailable = this.selectAccAvailableCashs(etrExchangeTrade001Dto);
		// 指令可用现金
		if (cashAvailable != null) {
			//etrExchangeTrade001Dto.setProAvailableCash(cashAvailable);
			etrExchangeTrade001Dto.setAccAvailableCash(cashAvailable);
		}
		else {
			etrExchangeTrade001Dto.setAccAvailableCash(BigDecimal.ZERO);
		}

		//指令可用持仓查询
		Map<String, Object> mapSec = new HashMap<String, Object>();
		mapSec.put("portfolioId", etrExchangeTrade001Dto.getPortfolioId());
		mapSec.put("finprodId", etrExchangeTrade001Dto.getFinprodId());
		mapSec.put("ccy", DictCodeList.币种_人民币_CNY.getValue());
		mapSec.put("secAcctId", etrExchangeTrade001Dto.getSecAcctId());
//		BigDecimal vailableNum = selectAvailableNum(mapSec);
		BigDecimal vailableNum = selectAvailableNums(etrExchangeTrade001Dto);
		if (StringUtil.isNotEmpty(vailableNum)) {
			etrExchangeTrade001Dto.setAccAvailablePos(vailableNum);
		}
		//交易方向 价格模式
		if (StringUtil.equals(DictCodeListExt3.交易方向_正回购.getCodeName(),etrExchangeTrade001Dto.getPs())){
			etrExchangeTrade001Dto.setPs(DictCodeListExt3.交易方向_正回购.getValue());
		}
		else if (StringUtil.equals(DictCodeListExt3.交易方向_逆回购.getCodeName(),etrExchangeTrade001Dto.getPs())){
			etrExchangeTrade001Dto.setPs(DictCodeListExt3.交易方向_逆回购.getValue());
		}
		else if (StringUtil.equals(DictCodeList.交易方向_买入.getCodeName(),etrExchangeTrade001Dto.getPs())){
			etrExchangeTrade001Dto.setPs(DictCodeList.交易方向_买入.getValue());
		}
		else if (StringUtil.equals(DictCodeList.交易方向_卖出.getCodeName(),etrExchangeTrade001Dto.getPs())){
			etrExchangeTrade001Dto.setPs(DictCodeList.交易方向_卖出.getValue());
		}

		if (StringUtil.equals(DictCodeListExt2.价格模式_指定价格.getCodeName(),etrExchangeTrade001Dto.getPriceModel())){
			etrExchangeTrade001Dto.setPriceModel(DictCodeListExt2.价格模式_指定价格.getValue());
		}
		else if (StringUtil.equals(DictCodeListExt2.价格模式_市价指令.getCodeName(),etrExchangeTrade001Dto.getPriceModel())){
			etrExchangeTrade001Dto.setPriceModel(DictCodeListExt2.价格模式_市价指令.getValue());
		}
		else if (StringUtil.equals(DictCodeListExt2.价格模式_限价指令.getCodeName(),etrExchangeTrade001Dto.getPriceModel())){
			etrExchangeTrade001Dto.setPriceModel(DictCodeListExt2.价格模式_限价指令.getValue());
		}


            /*if(StringUtil.isNotEmpty(etrExchangeTrade001Dto.getPs()) && (etrExchangeTrade001Dto.getPs().equals(DictCodeListExt3.交易方向_质押券入库.getValue())
					|| etrExchangeTrade001Dto.getPs().equals(DictCodeListExt3.交易方向_质押券出库.getValue()))){
            	etrExchangeTrade001Dto.setEntrustAmt(etrExchangeTrade001Dto.getEntrustNum().multiply(etrExchangeTrade001Dto.getEntrustPrice().divide(new BigDecimal(100))));
            }*/
/*			if (StringUtil.isNotEmpty(etrExchangeTrade001Dto.getEntrustPrice())) {
				Map map3 = new HashMap<>();
				map3.put("secId", etrExchangeTrade001Dto.getStockId());
				int scale = getScale(etrExchangeTrade001Dto.getFinprodType2(),etrExchangeTrade001Dto.getTradeMarket());
				map3.put("scale", scale);
				EtrExchangeTrade001Dto limitDto = findBidQuotationsPage(map3);
				if (null != limitDto) {
					etrExchangeTrade001Dto.setLimitUp(limitDto.getLimitUp());
					etrExchangeTrade001Dto.setLimitDown(limitDto.getLimitDown());
				}
			}
			settleEntrustType(etrExchangeTrade001Dto);*/
		return etrExchangeTrade001Dto;
	}

	/**
	 * 标准的交易方向  【买入、卖出】
	 * @return
	 */
	private boolean isStandardPs(String ps) {
		if (StringUtils.isNotEmpty(ps)) {
			if (ps.equals(DictCodeListExt3.交易方向_配债.getValue())
					|| ps.equals(DictCodeListExt2.交易方向_配股.getValue())
					|| ps.equals(DictCodeListExt3.交易方向_债转股.getValue())
					|| ps.equals(DictCodeListExt3.交易方向_质押券入库.getValue())
					|| ps.equals(DictCodeListExt3.交易方向_质押券出库.getValue())
					|| ps.equals(DictCodeList.交易方向_行权_回售.getValue())
					|| ps.equals(DictCodeList.交易方向_申购.getValue())
					|| ps.equals(DictCodeListExt3.交易方向_正回购.getValue())
					|| ps.equals(DictCodeListExt3.交易方向_逆回购.getValue())) {
				return false;
			}
		}
		return true;
	}
	private EtrExchangeTrade001Dto settleEntrustType(EtrExchangeTrade001Dto dto){
		if (dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue()) && dto.getTradePlat().equals(DictCodeList.交易平台_竞价撮合平台.getValue())) {
			if (StringUtils.equals(dto.getPs(), DictCodeListExt2.交易方向_配股.getValue())) {
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_配股指令.getValue());
			} else if (StringUtils.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())) {
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_新股申购指令.getValue());
			} else {
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_股票竞价指令.getValue());
			}
		}
		if (dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_基金.getValue()) && dto.getTradePlat().equals(DictCodeList.交易平台_竞价撮合平台.getValue())) {
			dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_基金竞价指令.getValue());
		}
		if (dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue()) && dto.getTradePlat().equals(DictCodeList.交易平台_竞价撮合平台.getValue())) {
			if (StringUtils.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue())) {
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_债券回售指令.getValue());
			} else if (StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_配债.getValue())) {
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_配债指令.getValue());
			} else if (StringUtils.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())) {
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_可转债申购指令.getValue());
			} else if (StringUtils.equals(dto.getPs(), DictCodeListExt1.交易方向_债转股.getValue())) {
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_债转股指令.getValue());
			} else if (StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_质押券入库.getValue()) || StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_质押券出库.getValue())) {
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_标准券出入库指令.getValue());
			}else {
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_债券竞价指令.getValue());
			}
		}
		if ((dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue()) || dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) && dto.getTradePlat().equals(DictCodeList.交易平台_竞价撮合平台.getValue())) {
			dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_债券质押式回购竞价指令.getValue());
		}
		return dto;
	}

	private String convertToItemCode(String dictCode, String listIndex) {
		List<? extends DataDictItem> dataDictItemList = SysDataUtil.getDataDictItem(dictCode);
		if (dataDictItemList != null && dataDictItemList.size() > 0) {
			for (DataDictItem item : dataDictItemList) {
				if (item.getItemCode() == listIndex || item.getItemValue().equals(listIndex)) {
					return item.getItemCode();
				}
			}
		}
		return null;
	}

	/**
	 * check后的保存 <功能详细描述>
	 *
	 * @param equityList
	 *            需要保存的数据
	 * @see
	 * @since 1.0
	 */
	public void dealAfterCheck(List<EtrExchangeTrade001Dto> equityList) {
		List<EtrExchangeTrade001Dto> listN = new ArrayList<EtrExchangeTrade001Dto>();
		for (int i = 0; i < equityList.size(); i++) {
			EtrExchangeTrade001Dto assetDeclarationCodeMaintenanceDto = equityList.get(i);
			EtrExchangeTrade001Dto equityInfoCheck = null;
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

	public List<SuggestDto> getCommonSelect(String selectTarget, Map<String, Object> param) {
		return restTemplate.postForList(
				ServiceApplication.COMMON_SERVICE_NAME + "/CommonSelect/get" + selectTarget, param,
				SuggestDto.class);
	}

	/**
	 *
	 * @param param
	 * @return
	 */
	public String selectStdsecConverseRatio(Map<String, Object> param) {
		return restTemplate.postForObject(
				ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/selectStdsecConverseRatio", param,
				String.class);
	}
	/**
	 * 转股价
	 * @param param
	 * @return
	 */
	public BigDecimal selectConverseSharePrice(Map<String, Object> param) {
		String finprodId = (String) param.get("finprodId");
		if (StringUtils.isNotEmpty(finprodId)) {
			FinProductConvertible finProductConvertible = finProductConvertibleMapper.selectByPrimaryKey(finprodId);
			if (finProductConvertible != null) {
				return finProductConvertible.getConPrice();
			}
		}
		return new BigDecimal(0.00);
	}


	/**
	 * 格式化指令价格
	 *
	 * @param entrustPrice
	 * @param minPirceDto
	 * @return
	 */
	private BigDecimal formatEntrustPrice(BigDecimal entrustPrice, MinPirceDto minPirceDto) {
		if (minPirceDto != null && null != minPirceDto.getMinPrice()) {
			final BigDecimal FACTOR = new BigDecimal(1).divide(minPirceDto.getMinPrice(),6, BigDecimal.ROUND_HALF_UP);
			int scale = String.valueOf(FACTOR.intValue() - 1).length();
			entrustPrice = (entrustPrice.divide(minPirceDto.getMinPrice(),6, BigDecimal.ROUND_HALF_UP).setScale(0, BigDecimal.ROUND_HALF_UP)
					.multiply(minPirceDto.getMinPrice())).setScale(scale, BigDecimal.ROUND_HALF_UP);
		}
		return entrustPrice;
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
	 * 设置小数位
	 * @param minPirceDto
	 * @return
	 */
	private int getScale(MinPirceDto minPirceDto) {
		int scale = 3;
		if (null != minPirceDto && null != minPirceDto.getMinPrice()) {
			final BigDecimal FACTOR = new BigDecimal(1).divide(minPirceDto.getMinPrice(), 6, BigDecimal.ROUND_UP);
			scale = String.valueOf(FACTOR.intValue() - 1).length();
		}
		return scale;
	}

	private boolean isNullCell(Object cellData) {
		if (null == cellData) {
			return true;
		} else if (StringUtils.isEmpty(cellData.toString())) {
			return true;
		}
		return false;
	}

	private String checkEntrustNumber(Object cellData, String label, String lineNo) {
		// 按数量check
		String confirmMsg = "";

		// 指令数量非空
		confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
				etrCommonService.checkIsBlank(null!=cellData?cellData.toString():null, label, lineNo));

		// 指令数量是数值
		confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
				etrCommonService.checkIsNumber(null!=cellData?cellData.toString():null, label, lineNo));

		// 指令数量是大于0
		confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
				etrCommonService.checkIsGtZeroWithNum(null!=cellData?cellData.toString():null, label, lineNo));

/*		if (StringUtils.isEmpty(confirmMsg) && new BigDecimal(cellData.toString()).intValue() > 999999999) {
			// 指令金额max
			confirmMsg = etrCommonService.StringAddByBr(confirmMsg, getMessage(label) + "不超过最大999999999(行号为" + lineNo + ")。");
		}*/

		return confirmMsg;
	}

	private String checkAssetPs(EtrExchangeTrade001Dto unit, String assetType, String othCodeType, String lineNo) {
		String finprodType2 = unit.getFinprodType2();
		String ps = unit.getPs();
		String confirmMsg = "";
		if (StringUtils.isNotEmpty(ps)) {
			if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType2)) {
				if (DictCodeListExt3.金融产品其他代码类型_配债代码.getValue().equals(othCodeType)) {
					if (!ps.equals(DictCodeListExt3.交易方向_配债.getValue())) {
						confirmMsg = etrCommonService.StringAddByBr(confirmMsg, this.getMessage("ETR_ERROR_0042", lineNo));
					}
				} else if (!ps.equals(DictCodeList.交易方向_买入.getValue()) && !ps.equals(DictCodeList.交易方向_卖出.getValue())
						&& !ps.equals(DictCodeListExt3.交易方向_债转股.getValue())
						&& !ps.equals(DictCodeListExt3.交易方向_质押券入库.getValue())
						&& !ps.equals(DictCodeListExt3.交易方向_质押券出库.getValue())
						&& !ps.equals(DictCodeList.交易方向_行权_回售.getValue())
						&& !ps.equals(DictCodeList.交易方向_申购.getValue())) {
					confirmMsg = etrCommonService.StringAddByBr(confirmMsg, this.getMessage("ETR_ERROR_0041", lineNo));
				}
			} else if (DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(finprodType2)) {
				if (DictCodeListExt3.金融产品其他代码类型_配股代码.getValue().equals(othCodeType)) {
					if (!ps.equals(DictCodeListExt2.交易方向_配股.getValue())) {
						confirmMsg = etrCommonService.StringAddByBr(confirmMsg, this.getMessage("ETR_ERROR_0043", lineNo));
					}
				} else if (!ps.equals(DictCodeList.交易方向_买入.getValue()) && !ps.equals(DictCodeList.交易方向_卖出.getValue())
						&& !ps.equals(DictCodeList.交易方向_申购.getValue())) {
					confirmMsg = etrCommonService.StringAddByBr(confirmMsg, this.getMessage("ETR_ERROR_0041", lineNo));
				}
			} else if (DictCodeListExt1.金融产品类型_投管用_基金.getValue().equals(finprodType2)) {
				if (!ps.equals(DictCodeList.交易方向_买入.getValue()) && !ps.equals(DictCodeList.交易方向_卖出.getValue())) {
					confirmMsg = etrCommonService.StringAddByBr(confirmMsg, this.getMessage("ETR_ERROR_0041", lineNo));
				}
			} else if ("ZYSHG".equals(unit.getFinprodType2())) {
				if (!ps.equals(DictCodeListExt3.交易方向_正回购.getValue())
						&& !ps.equals(DictCodeListExt3.交易方向_逆回购.getValue())) {
					confirmMsg = etrCommonService.StringAddByBr(confirmMsg, this.getMessage("ETR_ERROR_0041", lineNo));
				}
			}

			if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType2)
					&& ps.equals(DictCodeList.交易方向_行权_回售.getValue())) {
				Map<String, Object> param = new HashMap<>();
				param.put("finprodId", unit.getFinprodId());// 金融产品代码
				if (null != unit.getVdate()) {
					param.put("startDate", DateUtil.format(unit.getVdate()));
					param.put("endDate", DateUtil.format(unit.getVdate()));
					int record = selectBondExecPlan(param);
					if (record == 0) {
						confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
								this.getMessage("ETR_ERROR_0044", lineNo));
					}
				}
			} else if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType2)
					&& ps.equals(DictCodeList.交易方向_申购.getValue())) {
				if(null != assetType && !assetType.equals(DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue()) && !assetType.equals(DictCodeList.WIND一级分类_含债券一级分类等_可交换债.getValue())){
					confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
							getMessage("ETR_ERROR_0025", DictCodeList.交易方向_申购.getCodeName()) + "(行号为" + lineNo + ")。");
				}
			}
			else if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType2)
					&& ps.equals(DictCodeListExt1.交易方向_债转股.getValue())) {
				if (null != assetType && !assetType.equals(DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue())) {
					confirmMsg = etrCommonService.StringAddByBr(confirmMsg, this.getMessage("ETR_ERROR_0045", lineNo));
				} else if (StringUtils.isNotEmpty(unit.getFinprodId())) {
					FinProductConvertible finProductConvertible = finProductConvertibleMapper
							.selectByPrimaryKey(unit.getFinprodId());
					if (finProductConvertible == null) {
						confirmMsg = etrCommonService.StringAddByBr(confirmMsg, this.getMessage("ETR_ERROR_0046", lineNo));
					} else if (null == finProductConvertible.getConPrice()) {
						confirmMsg = etrCommonService.StringAddByBr(confirmMsg, this.getMessage("ETR_ERROR_0047", lineNo));
					}
				}
			} else if (DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType2)
					&& (DictCodeListExt3.交易方向_质押券出库.getValue().equals(ps)
					|| DictCodeListExt3.交易方向_质押券入库.getValue().equals(ps))) {
				Map<String, Object> map3 = new HashMap<>();
				map3.put("finprodId", unit.getFinprodId());
				String stdsecConverseRatio = selectStdsecConverseRatio(map3);
				if (StringUtils.isEmpty(stdsecConverseRatio)) {
					confirmMsg = etrCommonService.StringAddByBr(confirmMsg, this.getMessage("ETR_ERROR_0048", lineNo));
				}
			}
		}
		return confirmMsg;
	}

	/**
	 * 最小交易单元 交易数量、金额check
	 * @param dto
	 * @param minTrdUnitLimitDto
	 * @param lineNo
	 * @return
	 */
	public String checkMinTrdUnit(EtrExchangeTrade001Dto dto, MinTrdUnitLimitDto minTrdUnitLimitDto, String lineNo) {
		// 按数量check
		String confirmMsg = "";
		boolean flag1 = false;
		boolean flag2 = false;
		boolean flag3 = false;
		boolean flag4 = false;
		final BigDecimal maxNum = new BigDecimal(999999999);
		final BigDecimal maxAmt = new BigDecimal(999999999);
		if (null == minTrdUnitLimitDto.getMinAmt() || (minTrdUnitLimitDto.getMinAmt() != null
				&& minTrdUnitLimitDto.getMinAmt().compareTo(BigDecimal.ZERO) > 0
				&& dto.getEntrustAmt().compareTo(minTrdUnitLimitDto.getMinAmt()) < 0)) {
			flag1 = true;
		}
		if (null == minTrdUnitLimitDto.getMinNum() || (minTrdUnitLimitDto.getMinNum() != null
				&& minTrdUnitLimitDto.getMinNum().compareTo(BigDecimal.ZERO) > 0
				&& dto.getEntrustNum().compareTo(minTrdUnitLimitDto.getMinNum()) < 0)) {
			flag2 = true;
		}
		if (flag1 && flag2 && !(null == minTrdUnitLimitDto.getMinNum() && null == minTrdUnitLimitDto.getMinAmt())) {
			if (flag1 && null != minTrdUnitLimitDto.getMinNum()) {
				confirmMsg += "最低交易数量"
						+ getNumAndUnit(minTrdUnitLimitDto.getMinNum(), minTrdUnitLimitDto.getMinNumUnit());
			}
			if (flag1 && flag2 && flag1 && null != minTrdUnitLimitDto.getMinNum()
					&& null != minTrdUnitLimitDto.getMinAmt()) {
				confirmMsg += "，";
			}
			if (flag2 && null != minTrdUnitLimitDto.getMinAmt()) {
				confirmMsg += "最低交易金额" + getNumAndUnit(minTrdUnitLimitDto.getMinAmt());
			}
			if (flag1 && flag2 && flag1 && null != minTrdUnitLimitDto.getMinNum()
					&& null != minTrdUnitLimitDto.getMinAmt()) {
				confirmMsg += "，应至少不低于其中一个！";
			}
		}
		// TISHKS-151:修改最大交易金额和最大交易量使用查询配置值而非固定值 fuyongsheng edit start
		if (null == minTrdUnitLimitDto.getMaxAmt() || (minTrdUnitLimitDto.getMaxAmt() != null
				&& minTrdUnitLimitDto.getMaxAmt().compareTo(BigDecimal.ZERO) > 0
				&& dto.getEntrustAmt().compareTo(minTrdUnitLimitDto.getMaxAmt()) > 0)) {
			flag3 = true;
		}
		if (null == minTrdUnitLimitDto.getMaxNum() || (minTrdUnitLimitDto.getMaxNum() != null
				&& minTrdUnitLimitDto.getMaxNum().compareTo(BigDecimal.ZERO) > 0
				&& dto.getEntrustNum().compareTo(minTrdUnitLimitDto.getMaxNum()) > 0)) {
			flag4 = true;
		}
		if (flag3 && flag4 && !(null == minTrdUnitLimitDto.getMaxNum() && null == minTrdUnitLimitDto.getMaxAmt())) {
			if (flag3 && null != minTrdUnitLimitDto.getMaxNum()) {
				confirmMsg += "最大交易数量"
						+ getNumAndUnit(minTrdUnitLimitDto.getMaxNum(), minTrdUnitLimitDto.getMaxNumUnit());
			}
			if (flag3 && flag4 && flag3 && null != minTrdUnitLimitDto.getMaxNum()
					&& null != minTrdUnitLimitDto.getMaxAmt()) {
				confirmMsg += "，";
			}
			if (flag4 && null != minTrdUnitLimitDto.getMaxAmt()) {
				confirmMsg += "最大交易金额" + getNumAndUnit(minTrdUnitLimitDto.getMaxAmt());
			}
			// TISHKS-151:修改最大交易金额和最大交易量使用查询配置值而非固定值 fuyongsheng edit end
			if (flag3 && flag4 && flag3 && null != minTrdUnitLimitDto.getMaxNum()
					&& null != minTrdUnitLimitDto.getMaxAmt()) {
				confirmMsg += "，应至少不超过其中一个！";
			}
		}

		if (StringUtils.isNotEmpty(confirmMsg) && StringUtil.isNotEmpty(lineNo)) {
			confirmMsg += "(行号为" + lineNo + ")。";
		}

		if ((DictCodeListExt3.交易方向_质押券出库.getValue().equals(dto.getPs()) || DictCodeListExt3.交易方向_质押券入库.getValue().equals(dto.getPs()))
				&& dto.getEntrustAmt().compareTo(new BigDecimal(0)) == 0) {
			if(StringUtil.isEmpty(lineNo)){
				confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
						this.getMessage("ETR_ERROR_0033"));
			}else{
				confirmMsg = etrCommonService.StringAddByBr(confirmMsg,
						this.getMessage("ETR_ERROR_0033") + "(行号为" + lineNo + ")。");
			}
		}
		/*
		if (dto.getEntrustNum().compareTo(new BigDecimal(0)) == 0) {
			confirmMsg = etrCommonService.StringAddByBr(confirmMsg, "指令金额可下单的数量应是最小交易单位整数倍(行号为" + lineNo + ")。");
		}*/
		return confirmMsg;
	}

	/**
	 * 根据数值和单位返回拼接字符串
	 * @param number
	 * @param unit
	 * @return
	 */
	public String getNumAndUnit(BigDecimal number, String... unit) {
		String msg = "";
		if (number != null && number.compareTo(new BigDecimal(10000)) > 0) {
			msg += number.divide(new BigDecimal(10000)) + "万";
		} else {
			msg += number;
		}
		int length = unit.length;
		if (length > 0) {
			msg += SysDataUtil.getNameFromDataDict(DictCode.交易单位, unit[0]);
		} else {
			msg += "元";
		}
		return msg;
	}

	/**
	 * 判断是否交易时间段
	 *
	 * @param dto
	 * @return true 不是交易时段，不可交易 false 可以交易
	 */
	public Boolean isNonTradingTime(EtrExchangeTrade001Dto dto) {
		if (CacheUtil.getSysParam(BussinessConstant.ETR_TRADE_TIME_SWITCH).equals(DictCodeListExt1.是否_是.getValue())) {
			if (StringUtil.isNotEmpty(dto.getTradeMarket()) && StringUtil.isNotEmpty(dto.getFinprodType2())) {
				TrdTimeConfigDto trdTimeConfigDto = getTrdTimeConfig(dto.getTradePlat(), dto.getTradeMarket(),
						dto.getFinprodType2(), dto.getDeclaType());
				if (null != trdTimeConfigDto && null != trdTimeConfigDto.getNoonCloseTime()) {
					Date date = new Date();
					SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					try {
						Date noonCloseTime = sdf.parse(sd.format(date) + " " + trdTimeConfigDto.getNoonCloseTime().trim());
						if (noonCloseTime.before(new Date())) {
							return true;
						}
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return false;
	}

	/**
	 * 查询最小交易单元
	 * @param param
	 * @return
	 */
	public Map<String, BigDecimal> getMinNumStep(Map<String, Object> param) {
		String ps = param.get("ps") == null ? null : param.get("ps").toString();
		String currentTab = param.get("currentTab") == null ? null : param.get("currentTab").toString();
		String finprodId = (String) param.get("finprodId");
		String finprodType2 = (String) param.get("finprodType2");
		String tradeMarket = (String) param.get("tradeMarket");

		BigDecimal minNum = new BigDecimal(1);
		MinTrdUnitLimitDto minTrdUnitLimitDto = getInterfSysMinTrdUnit(finprodId, currentTab, tradeMarket, finprodType2, ps);
		if(null != minTrdUnitLimitDto && null != minTrdUnitLimitDto.getMinStep()){
			minNum = minTrdUnitLimitDto.getMinStep();
		}
		Map<String, BigDecimal> resultMap = new HashMap<>();
		resultMap.put("minNum",minNum);
		return resultMap;
	}

	/**
	 * 场内指令-查询资产代码信息
	 * @param pageParam
	 * @return
	 */
	public Page<SelectOptionDto> getFinprodMarketInfoPosition(Map<String, Object> pageParam) {
		return restTemplate.postForPage(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/MstSecuritiesAccountInfo/getFinprodMarketInfoPosition", pageParam,
				SelectOptionDto.class);
	}

	/**
	 * 场内指令-查询质押券再折算率
	 * @param pageParam
	 * @return
	 */
	public TrdMoreConvrateDto getMoreConvrate(Map<String, Object> pageParam) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("acctNo", (String) pageParam.get("acctNo"));
		Map<String, Object> res = restTemplate.postForObject(
				ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/getMoreConvrate", param, Map.class);
		if (res != null && res.get("data") != null) {
			List<TrdMoreConvrateDto> list = (List<TrdMoreConvrateDto>) res.get("data");
			if (list != null && list.size() > 0) {
				return JSON.parseObject(JSON.toJSONString(list.get(0)), TrdMoreConvrateDto.class);
			}
		}
		return null;
	}

	/**
	 * 场内指令-标准券持仓可用查询
	 *
	 * @param pageParam
	 * @return
	 */
	public BigDecimal queryStandandTicketCanUse(Map<String, Object> pageParam) {
		return restTemplate.postForObject(
				ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/queryStandandTicketCanUse", pageParam,
				BigDecimal.class);
	}

	/**
	 * 场内指令-债券昨平均价、昨收盘价查询
	 *
	 * @param pageParam
	 * @return
	 */
	public String qryMstBondMarketPrice(Map<String, Object> pageParam) {
		return restTemplate.postForObject(
				ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/qryMstBondMarketPrice", pageParam,
				String.class);
	}

	/**
	 * 场内指令-基金昨平均价、昨收盘价查询
	 *
	 * @param pageParam
	 * @return
	 */
	public String qryMstFundMarketPrice(Map<String, Object> pageParam) {
		return restTemplate.postForObject(
				ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/qryMstFundMarketPrice", pageParam,
				String.class);
	}


	/**
	 * 场内指令-股票昨平均价、昨收盘价查询
	 *
	 * @param pageParam
	 * @return
	 */
	public String qryMstStockMarketPrice(Map<String, Object> pageParam) {
		return restTemplate.postForObject(
				ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/qryMstStockMarketPrice", pageParam,
				String.class);
	}
	/**
	 * 插入和修改时，指令持仓流水 、现金流水处理
	 * add by zhangpei 
	 * 20201128
	 * @param dto
	 */
	public void doInsetOrUpdateForEtrPositionHold(EtrExchangeTrade001Dto dto) {
		restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/doInsetOrUpdateForEtrPositionHold", dto,
				Integer.class);
	}



	/**
	 *
	 * @Description //获得金融产品及其他信息
	 * @Param [map]
	 * @return java.util.List<com.joyintech.tams.invest.entrust.etrexchangetrade.dto.FinProductOthCodeDto>
	 * @version 1.0
	 * @see
	 **/
	public List<FinProductOthCodeDto> getFinprodOthCodeInfo(Map<String,Object> map){
		return restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/getFinprodOthCodeInfo", map,
				FinProductOthCodeDto.class);
	}

	/**
	 *
	 * @Description //获得权益额度
	 * @Param [map]
	 * @return java.util.List<com.joyintech.tams.common.base.entity.FinProductQuto>
	 * @version 1.0
	 * @see
	 **/
	public List<FinProductQuto> getFinProductQutoInfo(Map<String,Object> map){
		return restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/getFinProductQutoInfo", map,
				FinProductQuto.class);
	}



	/**
	 *
	 * @Description //获得已认购权益
	 * @Param [map]
	 * @return java.util.List<com.joyintech.tams.common.base.entity.EtrEntrust>
	 * @version 1.0
	 * @see
	 **/
	public List<SubEqutityDto> getsubEqutity(Map<String,Object> map){
		return restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/getsubEqutity", map,
				SubEqutityDto.class);
	}


	/**
	 *
	 * @Description //配债、配股、新股申购额度
	 * @Param []
	 * @return java.math.BigDecimal
	 * @version 1.0
	 * @see
	 **/
	public BigDecimal getEntrustNum(Map<String,Object> map){
		BigDecimal entrustNum = new BigDecimal(0);
		String ps = map.get("ps")== null? null:map.get("ps").toString();
		String finprodType2 = map.get("finprodType2") == null? null: map.get("finprodType2").toString();
		String finprodId = map.get("finprodId") == null? null: map.get("finprodId").toString();
		String tradeMarket = map.get("tradeMarket") == null? null: map.get("tradeMarket").toString();
		BigDecimal quto = new BigDecimal(0);
		BigDecimal ipoQuto = new BigDecimal(0);
		Map<String,Object> paramMap = new HashMap<String,Object>();
		Map<String,Object> paramMap1 = new HashMap<String,Object>();
		paramMap1.put("finprodId",finprodId);
		paramMap1.put("portfolioId",map.get("portfolioId"));
		paramMap1.put("secAcctId",map.get("secAcctId"));
		paramMap1.put("cDate",map.get("cDate"));
		if (DictCodeList.交易方向_申购.getValue().equals(ps) && DictCodeListExt1.金融产品类型_投管用_股票.getValue().equals(finprodType2)){

			List<String> othTypeList = new ArrayList<String>();
			othTypeList.add(DictCodeListExt3.金融产品其他代码类型_新股申购代码.getValue());
			paramMap.put("othTypeList",othTypeList);
			paramMap.put("finprodId",finprodId);
			String othType = "";              //业务类型
			List<FinProductOthCodeDto> codeDtos = getFinprodOthCodeInfo(paramMap);
			if (codeDtos.size()>0){
				othType = codeDtos.get(0).getOthType();
				tradeMarket = codeDtos.get(0).getTradeMarket();
			}
			paramMap1.put("othType",othType);

			List<FinProductQuto> qutos = getFinProductQutoInfo(paramMap1);
			if (qutos.size()>0){
				quto = qutos.get(0).getQuto();
				ipoQuto = qutos.get(0).getIpoQuto();
			}
			FinProductAdd finProductAdd = finProductAddMapper.selectByPrimaryKey(finprodId);
			if (finProductAdd.getBoard().equals(DictCodeList.上市板_主板.getValue()) && DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(tradeMarket))
			{
				entrustNum = quto;
			}
			else if (finProductAdd.getBoard().equals(DictCodeList.上市板_科创板.getValue()) && DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(tradeMarket))
			{
				entrustNum = ipoQuto;
			}
			else {
				entrustNum = quto;
			}
		}
		else if (DictCodeList.交易方向_申购.getValue().equals(ps) && DictCodeListExt1.金融产品类型_投管用_债券.getValue().equals(finprodType2)){

			FinProduct finProduct = finProductMapper.selectByPrimaryKey(finprodId);
			if (Objects.nonNull(finProduct.getIssueAmt()) && Objects.nonNull(finProduct.getIssuePrice())) {
				entrustNum = ArithmeticUtil.divide(finProduct.getIssueAmt(), finProduct.getIssuePrice(), 2);
			}
		}
		else if(DictCodeListExt2.交易方向_配股.getValue().equals(ps) || DictCodeListExt3.交易方向_配债.getValue().equals(ps)){

			List<String> othTypeList = new ArrayList<String>();
			if(DictCodeListExt2.交易方向_配股.getValue().equals(ps)){
				othTypeList.add(DictCodeListExt3.金融产品其他代码类型_配股代码.getValue());
			}
			if(DictCodeListExt3.交易方向_配债.getValue().equals(ps)){
				othTypeList.add(DictCodeListExt3.金融产品其他代码类型_配债代码.getValue());
			}
			paramMap.put("othTypeList",othTypeList);
			paramMap.put("finprodId",finprodId);
			String othType = "";              //业务类型
			List<FinProductOthCodeDto> codeDtos = getFinprodOthCodeInfo(paramMap);
			if (codeDtos.size()>0){
				othType = codeDtos.get(0).getOthType();
			}
			paramMap1.put("entrustType",map.get("entrustType"));
			paramMap1.put("othType",othType);
			paramMap1.put("entrustId",map.get("entrustId"));
			List<FinProductQuto> qutos = getFinProductQutoInfo(paramMap1);
			if (qutos.size()>0){
				quto = qutos.get(0).getQuto();
			}
			entrustNum = quto;
		}

		return entrustNum;

	}
	/**
	 *
	 * @Description //查询是否有流程中指令
	 * @Param [map]
	 * @return java.util.List<com.joyintech.tams.common.base.entity.WflEtrEntrust>
	 * @version 1.0
	 * @see
	 **/
	public List<WflEtrEntrust> selectWflEntrust(Map<String,Object> map){
		return restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/selectWflEntrust", map,
				WflEtrEntrust.class);
	}
	/**
	 *
	 * @Description 查询流程及委托表中是否有同一股东下达的可转债申购指令
	 * @Param [map]
	 * @return java.util.List<java.lang.String>
	 * @version 1.0
	 * @see
	 **/
	public List<String> selectWflEntrustAddAndEtrEntrustAdd(Map<String,Object> map){
		return restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/selectWflEntrustAddAndEtrEntrustAdd", map,
				List.class);
	}

	//add by chenjiaxing 债券竞价交易计算偏离度 20210907 start
	/**
	 * 1.6计算偏离度
	 *
	 * @param parameters 画面参数
	 * @return 偏离度
	 * @see
	 * @since 1.0
	 */
	public String calcDeviationDegree(Map<String, Object> parameters) {
		// 平台日
		parameters.put("sysDate", DateUtil.format(iPlatformDate.getSysDate()));

		return restTemplate.postForObject(ServiceApplication.TRADE_SERVICE_NAME + "/EtrExchangeTrade/calcDeviationDegree",
				parameters, String.class);
	}
	//add by chenjiaxing 债券竞价交易计算偏离度 20210907 end

	// add by etliu 20211013 start
	// TISHKS-5 bug修复：在场内下指令时，资产选了万科之后，出现申购选项，但理论上不应该出现(万科很早就上市了)，我在申报信息维护中也没有看到万科的新股申报
	/**
	 * 当前选中股票是否处在新股申购期
	 *
	 * @param params 画面参数
	 * @return 当前选中股票是否存在新股申购期
	 * @see
	 * @since 1.0
	 */
	public String isXinGuShenGou(Map<String, Object> params) {
		Map<String, Object> paramMap = new HashMap<String, Object>();
		paramMap.put("finprodId", params.get("finprodId"));//金融产品代码
		List<String> othTypeList = new ArrayList<String>();
		othTypeList.add(DictCodeListExt3.金融产品其他代码类型_新股申购代码.getValue());
		paramMap.put("othTypeList", othTypeList);

		List<FinProductOthCodeDto> codeDtos = this.getFinprodOthCodeInfo(paramMap);

		if (StringUtil.isEmptyList(codeDtos)) {
			return DictCodeList.是否_否.getValue();
		}

		Date sysdate = DateUtil.parse(DateUtil.getSysDate());
		boolean isXinGuShenGou = codeDtos.stream().filter(x -> DateUtil.compareDate(x.getDeclaredStartDate(), sysdate) <= 0
				&& DateUtil.compareDate(x.getDeclaredEndDate(), sysdate) >= 0).findFirst().isPresent();
		if (isXinGuShenGou) {
			return DictCodeList.是否_是.getValue();
		}

		return DictCodeList.是否_否.getValue();
	}
	// add by etliu 20211013 end

	// add by etliu 20211207 start
	// 交易所债券非交易类业务迁移
	/**
	 * 查询转股信息
	 * @param finprodId finprodId
	 * @param paramDate paramDate
	 * @return 转股信息
	 */
	public FinProductConvertible selectConverseShareData(String finprodId, Date paramDate) {
		if (StringUtils.isNotEmpty(finprodId)) {
			FinProductConvertible finProductConvertible = finProductConvertibleMapper.selectByPrimaryKey(finprodId);
			if (finProductConvertible != null && finProductConvertible.getConVdate() != null
					&& DateUtil.compareDate(finProductConvertible.getConVdate(), paramDate) <= 0) {
				return finProductConvertible;
			}
		}
		return null;
	}

	/**
	 *
	 * @Description //查询行权回售数据
	 * @Param [map]
	 * @return 行权回售数据
	 * @version 1.0
	 * @see
	 **/
	public List<FinExercisePlan> selectFinExercisePlanData(String finprodId, Date paramDate){
		Map<String, Object> map = new HashMap<>();
		map.put("finprodId", finprodId);
		map.put("sysDate", DateUtil.format(paramDate));
		return restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrExchangeTrade/selectFinExercisePlanData", map,
				FinExercisePlan.class);
	}
	// add by etliu 20211207 end

	/**
	 * 由于交易平台不同，设置不同交易平台对应的委托方向
	 *
	 * @param model Model
	 */
	public void setSettleSpreedSelectOptions(Model model, SelectDto selectDto) {
		// 固定收益
		List<SelectOptionDto> settleSpreed_gd = selectDto.getOptions().stream().filter(option -> {
			return StringUtil.equals(option.getId(), DictCodeList.清算速度_T_0.getValue())
					|| StringUtil.equals(option.getId(), DictCodeList.清算速度_T_1.getValue());
		}).collect(Collectors.toList());
		model.addAttribute("ACCOUNT_SPEED_GD", JsonUtils.objectArray2Json(settleSpreed_gd.toArray()));
	}

	/**
	 * 由于交易平台不同，设置不同交易平台对应的委托方向
	 *
	 * @param model Model
	 */
	public void setPsSelectOptions(Model model,SelectDto selectDto,boolean isMul) {
		// 竞价平台
		List<SelectOptionDto> ps_jj = selectDto.getOptions().stream().filter(option -> {
			return StringUtil.equals(option.getId(), DictCodeList.交易方向_买入.getValue())
					|| StringUtil.equals(option.getId(), DictCodeList.交易方向_卖出.getValue())
					|| StringUtil.equals(option.getId(), DictCodeList.交易方向_行权_回售.getValue())
					|| StringUtil.equals(option.getId(), DictCodeListExt2.交易方向_配股.getValue())
					|| StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_配债.getValue())
					|| StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_债转股.getValue())
					|| StringUtil.equals(option.getId(), DictCodeList.交易方向_申购.getValue())
					|| StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_质押券入库.getValue())
					|| StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_质押券出库.getValue())
					|| StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_正回购.getValue())
					|| StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_逆回购.getValue())
					|| (StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_调整到.getValue()) && isMul);
		}).collect(Collectors.toList());
		model.addAttribute("PS_JJ", JsonUtils.objectArray2Json(ps_jj.toArray()));
		// 大宗平台
		List<SelectOptionDto> ps_dz = selectDto.getOptions().stream().filter(option -> {
			return StringUtil.equals(option.getId(), DictCodeList.交易方向_买入.getValue())
					|| StringUtil.equals(option.getId(), DictCodeList.交易方向_卖出.getValue())
					|| StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_正回购.getValue())
					|| StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_逆回购.getValue())
					|| (StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_调整到.getValue()) && isMul);
		}).collect(Collectors.toList());
		model.addAttribute("PS_DZ", JsonUtils.objectArray2Json(ps_dz.toArray()));
		// 固定收益
		List<SelectOptionDto> ps_gd = selectDto.getOptions().stream().filter(option -> {
			return StringUtil.equals(option.getId(), DictCodeList.交易方向_买入.getValue())
					|| StringUtil.equals(option.getId(), DictCodeList.交易方向_卖出.getValue())
					|| (StringUtil.equals(option.getId(), DictCodeListExt3.交易方向_调整到.getValue()) && isMul);
		}).collect(Collectors.toList());
		model.addAttribute("PS_GD", JsonUtils.objectArray2Json(ps_gd.toArray()));
	}

	/**
	 * 价格模式
	 *
	 * @param model Model
	 */
	public void setPriceModelSelectOptions(Model model,SelectDto selectDto) {
		List<SelectOptionDto> priceModel_jj = selectDto.getOptions().stream().filter(option -> {
			return StringUtil.equals(option.getId(), DictCodeListExt2.价格模式_限价指令.getValue())
					|| StringUtil.equals(option.getId(), DictCodeListExt2.价格模式_市价指令.getValue())
					|| StringUtil.equals(option.getId(), DictCodeListExt2.价格模式_指定价格.getValue());
		}).collect(Collectors.toList());
		model.addAttribute("PRICE_MODEL_JJ", JsonUtils.objectArray2Json(priceModel_jj.toArray()));

		List<SelectOptionDto> priceModel = selectDto.getOptions().stream().filter(option -> {
			return StringUtil.equals(option.getId(), DictCodeListExt2.价格模式_限价指令.getValue())
					|| StringUtil.equals(option.getId(), DictCodeListExt2.价格模式_市价指令.getValue());
		}).collect(Collectors.toList());
		model.addAttribute("PRICE_MODEL_DZ", JsonUtils.objectArray2Json(priceModel.toArray()));
		model.addAttribute("PRICE_MODEL_GD", JsonUtils.objectArray2Json(priceModel.toArray()));
	}

	/**
	 * 由于交易平台不同，设置不同交易平台对应的申报类型
	 *
	 * @param model Model
	 */
	public void setDeclaTypeSelectOptions(Model model,SelectDto selectDto) {
		// 大宗平台
		List<SelectOptionDto> declaType_dz = selectDto.getOptions().stream().filter(option -> {
			return StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_不指定.getValue())
					|| StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_意向申报.getValue())
					|| StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_定价申报.getValue())
					|| StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_点击成交申报.getValue())
					|| StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_成交申报.getValue())
					|| StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_收盘价固定价格申报.getValue())
					|| StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_日均价固定价格申报.getValue());
		}).collect(Collectors.toList());
		model.addAttribute("DECLA_TYPE_DZ", JsonUtils.objectArray2Json(declaType_dz.toArray()));
		// 固定收益
		List<SelectOptionDto> declaType_gd = selectDto.getOptions().stream().filter(option -> {
			return StringUtil.equals(option.getId(), DictCodeListExt3.申报类型_指定对手方成交申报.getValue());
		}).collect(Collectors.toList());
		model.addAttribute("DECLA_TYPE_GD", JsonUtils.objectArray2Json(declaType_gd.toArray()));
	}


	/**
	 * 设置画面的一些固定的数据字典明细
	 *
	 * @param model Model
	 */
	public void setConstSelectOption(Model model) {
		// 金融产品类型(投管用)
		model.addAttribute("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
		model.addAttribute("FINPROD_TYPE_TG_F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
		model.addAttribute("FINPROD_TYPE_TG_F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
		model.addAttribute("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
		model.addAttribute("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
		model.addAttribute("FINPROD_TYPE_TG_ZYSHG", "ZYSHG");
		// 交易方向
		model.addAttribute("PS_T01", DictCodeList.交易方向_买入.getValue());
		model.addAttribute("PS_T02", DictCodeList.交易方向_卖出.getValue());
		model.addAttribute("PS_03", DictCodeListExt3.交易方向_正回购.getValue());
		model.addAttribute("PS_04", DictCodeListExt3.交易方向_逆回购.getValue());
		model.addAttribute("PS_T46", DictCodeListExt2.交易方向_配股.getValue());
		model.addAttribute("PS_T48", DictCodeListExt2.交易方向_新股中签.getValue());
		model.addAttribute("PS_T49", DictCodeListExt3.交易方向_债转股.getValue());
		model.addAttribute("PS_T97", DictCodeListExt3.交易方向_配债.getValue());
		model.addAttribute("PS_T03", DictCodeList.交易方向_行权_回售.getValue());
		model.addAttribute("PS_T04", DictCodeList.交易方向_申购.getValue());
		model.addAttribute("PS_T68", DictCodeListExt3.交易方向_质押券入库.getValue());
		model.addAttribute("PS_T69", DictCodeListExt3.交易方向_质押券出库.getValue());
		model.addAttribute("PS_T100", DictCodeListExt3.交易方向_调整到.getValue());
		// 价格模式
		model.addAttribute("PRICE_MODEL_PRM02", DictCodeListExt2.价格模式_限价指令.getValue());
		model.addAttribute("PRICE_MODEL_PRM03", DictCodeListExt2.价格模式_市价指令.getValue());
		model.addAttribute("PRICE_MODEL_PRM01", DictCodeListExt2.价格模式_指定价格.getValue());
		model.addAttribute("DECLA_TYPE_PRM05", DictCodeListExt3.申报类型_不指定.getValue());
		model.addAttribute("DECLA_TYPE_PRM06", DictCodeListExt3.申报类型_意向申报.getValue());
		model.addAttribute("DECLA_TYPE_PRM07", DictCodeListExt3.申报类型_定价申报.getValue());
		model.addAttribute("DECLA_TYPE_PRM08", DictCodeListExt3.申报类型_点击成交申报.getValue());
		model.addAttribute("DECLA_TYPE_PRM09", DictCodeListExt3.申报类型_成交申报.getValue());
		model.addAttribute("DECLA_TYPE_PRM10", DictCodeListExt3.申报类型_收盘价固定价格申报.getValue());
		model.addAttribute("DECLA_TYPE_PRM11", DictCodeListExt3.申报类型_日均价固定价格申报.getValue());
		model.addAttribute("DECLA_TYPE_PRM12", DictCodeListExt3.申报类型_最优价成交申报.getValue());
		model.addAttribute("DECLA_TYPE_PRM13", DictCodeListExt3.申报类型_可转换成交申报.getValue());
		model.addAttribute("DECLA_TYPE_PRM14", DictCodeListExt3.申报类型_指定对手方成交申报.getValue());
		// 交易场所
		model.addAttribute("TRADE_PLACE_03", DictCodeListExt1.交易场所_上海证券交易所.getValue());
		model.addAttribute("TRADE_PLACE_04", DictCodeListExt1.交易场所_深圳证券交易所.getValue());
		// 债券计算模式
		model.addAttribute("SEC_CALC_MODE_01", DictCodeListExt3.债券计算模式_单位净价.getValue());
		model.addAttribute("SEC_CALC_MODE_02", DictCodeListExt3.债券计算模式_单位全价.getValue());
		model.addAttribute("SEC_CALC_MODE_03", DictCodeListExt3.债券计算模式_到期收益率.getValue());
		// 指令业务类型
		model.addAttribute("ENTRUST_BUSS_TYPE_01", DictCodeListExt3.上农商_指令业务类型_股票竞价指令.getValue());
		model.addAttribute("ENTRUST_BUSS_TYPE_02", DictCodeListExt3.上农商_指令业务类型_基金竞价指令.getValue());
		model.addAttribute("ENTRUST_BUSS_TYPE_03", DictCodeListExt3.上农商_指令业务类型_大宗债券质押式回购指令.getValue());
		model.addAttribute("ENTRUST_BUSS_TYPE_04", DictCodeListExt3.上农商_指令业务类型_大宗股票指令.getValue());
		model.addAttribute("ENTRUST_BUSS_TYPE_05", DictCodeListExt3.上农商_指令业务类型_大宗基金指令.getValue());
		model.addAttribute("ENTRUST_BUSS_TYPE_06", DictCodeListExt3.上农商_指令业务类型_大宗债券指令.getValue());
		model.addAttribute("ENTRUST_BUSS_TYPE_07", DictCodeListExt3.上农商_指令业务类型_债券竞价指令.getValue());
		model.addAttribute("ENTRUST_BUSS_TYPE_08", DictCodeListExt3.上农商_指令业务类型_上交所固定收益平台现券交易指令.getValue());
		model.addAttribute("ENTRUST_BUSS_TYPE_09", DictCodeListExt3.上农商_指令业务类型_债券回售指令.getValue());
		model.addAttribute("ENTRUST_BUSS_TYPE_10", DictCodeListExt3.上农商_指令业务类型_配股指令.getValue());
		model.addAttribute("ENTRUST_BUSS_TYPE_11", DictCodeListExt3.上农商_指令业务类型_新股申购指令.getValue());
		model.addAttribute("ENTRUST_BUSS_TYPE_12", DictCodeListExt3.上农商_指令业务类型_配债指令.getValue());
		model.addAttribute("ENTRUST_BUSS_TYPE_13", DictCodeListExt3.上农商_指令业务类型_可转债申购指令.getValue());
		// 目标类型
		model.addAttribute("TARGET_CLASS_01", DictCodeListExt3.目标类型_数量.getValue());
		model.addAttribute("TARGET_CLASS_02", DictCodeListExt3.目标类型_金额.getValue());

		// 交易平台
		model.addAttribute("SHARES_TRADE_PLATFORM_02", DictCodeList.交易平台_竞价撮合平台.getValue());
		model.addAttribute("SHARES_TRADE_PLATFORM_03", DictCodeList.交易平台_固定收益平台.getValue());
		model.addAttribute("SHARES_TRADE_PLATFORM_04", DictCodeList.交易平台_大宗交易平台.getValue());
		model.addAttribute("SHARES_TRADE_PLATFORM_05", DictCodeList.交易平台_综合协议交易平台.getValue());

		// 金融产品其他代码类型
		model.addAttribute("FINPROD_OTH_CODE_TYPE_06", DictCodeListExt3.金融产品其他代码类型_配股代码.getValue());
		model.addAttribute("FINPROD_OTH_CODE_TYPE_05", DictCodeListExt3.金融产品其他代码类型_配债代码.getValue());

		// 金融产品其他代码类型
		model.addAttribute("WINDTYPEONE_B07", DictCodeList.WIND一级分类_含债券一级分类等_公司债.getValue());
		model.addAttribute("WINDTYPEONE_B13", DictCodeList.WIND一级分类_含债券一级分类等_资产支持证券.getValue());
		model.addAttribute("WINDTYPEONE_B14", DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue());
		model.addAttribute("WINDTYPEONE_B15", DictCodeList.WIND一级分类_含债券一级分类等_可交换债.getValue());

		model.addAttribute("BOARD_BD03", DictCodeList.上市板_科创板.getValue());

		//场内多组合分配方式
		model.addAttribute("MUL_DISTRI_CLASS_01", DictCodeListExt3.场内多组合分配方式_单笔数量.getValue());
		model.addAttribute("MUL_DISTRI_CLASS_02", DictCodeListExt3.场内多组合分配方式_单笔金额.getValue());
		model.addAttribute("MUL_DISTRI_CLASS_03", DictCodeListExt3.场内多组合分配方式_总数量.getValue());
		model.addAttribute("MUL_DISTRI_CLASS_04", DictCodeListExt3.场内多组合分配方式_总金额.getValue());
		model.addAttribute("MUL_DISTRI_CLASS_05", DictCodeListExt3.场内多组合分配方式_持仓数量比例.getValue());
		model.addAttribute("MUL_DISTRI_CLASS_06", DictCodeListExt3.场内多组合分配方式_可用金额比例.getValue());
		model.addAttribute("MUL_DISTRI_CLASS_07", DictCodeListExt3.场内多组合分配方式_可用标准券比例.getValue());
		model.addAttribute("MUL_DISTRI_CLASS_08", DictCodeListExt3.场内多组合分配方式_产品净资产比例.getValue());
		model.addAttribute("MUL_DISTRI_CLASS_09", DictCodeListExt3.场内多组合分配方式_目标可用金额.getValue());
		model.addAttribute("MUL_DISTRI_CLASS_10", DictCodeListExt3.场内多组合分配方式_目标标准券使用率.getValue());

		//权重方式
		model.addAttribute("WEIGH_TYPE_01", DictCodeListExt3.权重方式_平均分配.getValue());
		model.addAttribute("WEIGH_TYPE_02", DictCodeListExt3.权重方式_组合净资产比例.getValue());
		model.addAttribute("WEIGH_TYPE_03", DictCodeListExt3.权重方式_组合可用金额比例.getValue());
		model.addAttribute("WEIGH_TYPE_04", DictCodeListExt3.权重方式_组合持仓数量比例.getValue());
		model.addAttribute("WEIGH_TYPE_05", DictCodeListExt3.权重方式_自定义.getValue());

		//目标值单位
		model.addAttribute("MUL_TARGET_UNIT_01", DictCodeListExt3.目标值单位_股.getValue());
		model.addAttribute("MUL_TARGET_UNIT_02", DictCodeListExt3.目标值单位_份.getValue());
		model.addAttribute("MUL_TARGET_UNIT_03", DictCodeListExt3.目标值单位_张.getValue());
		model.addAttribute("MUL_TARGET_UNIT_04", DictCodeListExt3.目标值单位_手.getValue());
		model.addAttribute("MUL_TARGET_UNIT_05", DictCodeListExt3.目标值单位_百分比.getValue());
		model.addAttribute("MUL_TARGET_UNIT_06", DictCodeListExt3.目标值单位_元.getValue());
		model.addAttribute("MUL_TARGET_UNIT_07", DictCodeListExt3.目标值单位_万元.getValue());
	}


	public String setEntrustBussType(EtrExchangeTrade001Dto dto){
		//业务类型
		String businessType = String.valueOf("");
		if (dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue()) && dto.getTradePlat().equals(DictCodeList.交易平台_竞价撮合平台.getValue())) {
			// add  start
			if (StringUtils.equals(dto.getPs(), DictCodeListExt2.交易方向_配股.getValue())) {
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_配股指令.getValue());
				businessType = DictCodeListExt3.共通业务类型_股票竞价指令.getValue();
			} else if (StringUtils.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())) {
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_新股申购指令.getValue());
				businessType = DictCodeListExt3.共通业务类型_新股申购指令.getValue();
			} else {//add  end
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_股票竞价指令.getValue());
				businessType = DictCodeListExt3.共通业务类型_股票竞价指令.getValue();
			}
		}
		if (dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_基金.getValue()) && dto.getTradePlat().equals(DictCodeList.交易平台_竞价撮合平台.getValue())) {
			dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_基金竞价指令.getValue());
			businessType = DictCodeListExt3.共通业务类型_基金竞价指令.getValue();
		}
		if ((dto.getPs().equals(DictCodeListExt3.交易方向_正回购.getValue()) || dto.getPs().equals(DictCodeListExt3.交易方向_逆回购.getValue())) && dto.getTradePlat().equals(DictCodeList.交易平台_大宗交易平台.getValue())) {
			dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_大宗债券质押式回购指令.getValue());
			businessType = DictCodeListExt3.共通业务类型_大宗债券质押式回购指令.getValue();
		}
		if (dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue()) && dto.getTradePlat().equals(DictCodeList.交易平台_大宗交易平台.getValue())) {
			dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_大宗股票指令.getValue());
			businessType = DictCodeListExt3.共通业务类型_大宗股票指令.getValue();
		}
		if (dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_基金.getValue()) && dto.getTradePlat().equals(DictCodeList.交易平台_大宗交易平台.getValue())) {
			dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_大宗基金指令.getValue());
			businessType = DictCodeListExt3.共通业务类型_大宗基金指令.getValue();
		}
		if (dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue()) && dto.getTradePlat().equals(DictCodeList.交易平台_大宗交易平台.getValue())) {
			dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_大宗债券指令.getValue());
			businessType = DictCodeListExt3.共通业务类型_大宗债券指令.getValue();
		}
		// add start
		if (dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue()) && dto.getTradePlat().equals(DictCodeList.交易平台_竞价撮合平台.getValue())) {
			if (StringUtils.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue())) {
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_债券回售指令.getValue());
				businessType = DictCodeListExt3.共通业务类型_债券回售指令.getValue();
			} else if (StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_配债.getValue())) {
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_配债指令.getValue());
				businessType = DictCodeListExt3.共通业务类型_配债指令.getValue();
			} else if (StringUtils.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())) {
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_可转债申购指令.getValue());
				businessType = DictCodeListExt3.共通业务类型_可转债申购指令.getValue();
			} else if (StringUtils.equals(dto.getPs(), DictCodeListExt1.交易方向_债转股.getValue())) {
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_债转股指令.getValue());
				businessType = DictCodeListExt3.共通业务类型_债转股指令.getValue();
			} else if (StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_质押券入库.getValue()) || StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_质押券出库.getValue())) {
				/*
				 * else if 画面.委托方向 = 数据字典.交易方向.质押入库        or 质押出库
				 * 指令业务类型     =  数据字典.共通业务类型.标准券出入库指令
				 */
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_标准券出入库指令.getValue());
				businessType = DictCodeListExt3.共通业务类型_标准券出入库指令.getValue();
			}else {
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_债券竞价指令.getValue());
				businessType = DictCodeListExt3.共通业务类型_债券竞价指令.getValue();
			}
		}
		// 债券质押式回购竞价指令
		if ((dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue()) || dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) && dto.getTradePlat().equals(DictCodeList.交易平台_竞价撮合平台.getValue())) {
			dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_债券质押式回购竞价指令.getValue());
			businessType = DictCodeListExt3.共通业务类型_债券质押式回购竞价指令.getValue();
		}
		// 大宗债券质押式回购指令
		if ((dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue()) || dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue())) && dto.getTradePlat().equals(DictCodeList.交易平台_大宗交易平台.getValue())) {
			dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_大宗债券质押式回购指令.getValue());
			businessType = DictCodeListExt3.共通业务类型_大宗债券质押式回购指令.getValue();
		}
		if (dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue()) && dto.getTradePlat().equals(DictCodeList.交易平台_固定收益平台.getValue())) {
			dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_上交所固定收益平台现券交易指令.getValue());
			businessType = DictCodeListExt3.共通业务类型_上交所固定收益平台现券交易指令.getValue();
		}

		return businessType;
	}

	public BussinessCodeParameter genBussinessCodeParameter(EtrExchangeTrade001Dto dto){
		BussinessCodeParameter param = new BussinessCodeParameter();
		if (dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue()) && dto.getTradePlat().equals(DictCodeList.交易平台_竞价撮合平台.getValue())) {
			// add  start
			if (StringUtils.equals(dto.getPs(), DictCodeListExt2.交易方向_配股.getValue())) {
				param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_配股指令));
			} else if (StringUtils.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())) {
				param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_新股申购指令));
			} else {
				param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_股票竞价指令));
			}
		}
		if (dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_基金.getValue()) && dto.getTradePlat().equals(DictCodeList.交易平台_竞价撮合平台.getValue())) {
			param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_基金竞价指令));
		}
		if ((dto.getPs().equals(DictCodeListExt3.交易方向_正回购.getValue()) || dto.getPs().equals(DictCodeListExt3.交易方向_逆回购.getValue())) && dto.getTradePlat().equals(DictCodeList.交易平台_大宗交易平台.getValue())) {
			param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_大宗债券质押式回购指令));
		}
		if (dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue()) && dto.getTradePlat().equals(DictCodeList.交易平台_大宗交易平台.getValue())) {
			param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_大宗股票指令));
		}
		if (dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_基金.getValue()) && dto.getTradePlat().equals(DictCodeList.交易平台_大宗交易平台.getValue())) {
			param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_大宗基金指令));
		}
		if (dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue()) && dto.getTradePlat().equals(DictCodeList.交易平台_大宗交易平台.getValue())) {
			param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_大宗债券指令));
		}
		//add start
		if(dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue()) && dto.getTradePlat().equals(DictCodeList.交易平台_竞价撮合平台.getValue())){
			if(StringUtils.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue())){
				param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_债券回售指令));
			}else if(StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_配债.getValue())){
				param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_配债指令));
			}else if(StringUtils.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())){
				param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_可转债申购指令));
			}else if(StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_债转股.getValue())){
				param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_债转股指令));
			}else if(StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_质押券入库.getValue()) || StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_质押券出库.getValue())){
				param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_标准券出入库指令));
			}else{
				param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_债券竞价指令));
			}
		}
		// 债券质押式回购竞价指令
		if ((dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
				|| dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue()))
				&& dto.getTradePlat().equals(DictCodeList.交易平台_竞价撮合平台.getValue())) {
			param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_债券质押式回购竞价指令));
		}
		// 大宗债券质押式回购指令
		if ((dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
				|| dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue()))
				&& dto.getTradePlat().equals(DictCodeList.交易平台_大宗交易平台.getValue())) {
			param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_大宗债券质押式回购指令));
		}
		if(dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue()) && dto.getTradePlat().equals(DictCodeList.交易平台_固定收益平台.getValue())){
			param.setCommonBusinessExtType(DictCodeListExt3.fromDictCodeList(DictCodeListExt3.共通业务类型_上交所固定收益平台现券交易指令));
		}

		return param;
	}}
