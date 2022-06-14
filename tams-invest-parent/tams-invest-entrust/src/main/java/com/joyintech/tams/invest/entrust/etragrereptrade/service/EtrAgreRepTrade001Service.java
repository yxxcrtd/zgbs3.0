/*
 * 文件名：EtrTradePlaceService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月1日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.entrust.etragrereptrade.service;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.FinProductConvertibleMapper;
import com.joyintech.tams.common.base.dao.FinTradeProductMapper;
import com.joyintech.tams.common.base.entity.FinTradeProduct;
import com.joyintech.tams.common.base.entity.FinTradeProductKey;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.component.business.common.service.DateCalService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.component.base.group.BasePtlCashPositionInfoService;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.constant.ServiceApplication;
import com.joyintech.tams.invest.common.dto.BaseEtrInvestInfoDto;
import com.joyintech.tams.invest.common.dto.PtlCashPositionFlowDto;
import com.joyintech.tams.invest.common.position.dto.PostionParameter;
import com.joyintech.tams.invest.common.position.service.CommonPositionService;
import com.joyintech.tams.invest.common.trade.service.BaseEtrCashPositionCommService;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.invest.entrust.etragrereptrade.dto.EtrAgreRepTradeDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.service.BaseProcessService;


/**
 * 交易所指令交易（债券股票回购基金场内指令交易）
 * 
 * @author chendijun
 * @version 1.0 2020年7月1日
 * @see EtrAgreRepTrade001Service
 * @since 1.0
 */
@Service
public class EtrAgreRepTrade001Service extends CommonExportService<EtrAgreRepTradeDto>
     implements BaseProcessService<EtrAgreRepTradeDto> {

	private static final Logger logger = LoggerFactory.getLogger(EtrAgreRepTrade001Service.class);

	public static String queryIntegratedQuote = "/joyin/entrust/integrated/quote";
    
    @Autowired
    FinProductConvertibleMapper finProductConvertibleMapper;
    
    /**
     * {@linkplain}
     */
    @Autowired
    private DateCalService dateCalService;

    @Autowired
	private BasePtlCashPositionInfoService basePtlCashPositionInfoService;

    @Autowired
	private BaseEtrCashPositionCommService baseEtrCashPositionCommService;

	@Autowired
	private CommonPositionService commonPositionService;

	/**
	 * 交易类金融产品表
	 */
	@Autowired
	private FinTradeProductMapper finTradeProductMapper;

	// add by etliu 20220116 start
	@Autowired
	private TrdCommonBaseService trdCommonBaseService;
	// add by etliu 20220116 end

    /**
     * 对手方交易员联想文本框初始化。
     * 
     * @param map 检索参数
     * @return 联想文本框
     */
    public List<SuggestDto> selectCounterTrader(Map<String, Object> map) {
		map.put("counterTraderId",map.get("param"));
		if (StringUtil.isNotEmpty(map.get("homeTrader"))){
			map.put("counterId",map.get("homeTrader"));
		}
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
        return restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrAgreRepTrade/selectTrader",
                map, SuggestDto.class);
    }
    /**
     * 本方交易员联想文本框初始化。
     * 
     * @param map 检索参数
     * @return 联想文本框
     */
    public Map<String,Object> addDate(Map<String, Object> map) {
    	String fristSettleDate = (String)map.get("fristSettleDate");

		BigDecimal termDic = new BigDecimal((String) map.get("term"));
		int term = termDic.setScale(0,BigDecimal.ROUND_HALF_UP).intValue();

    	String tradeMarket = (String)map.get("tradeMarket");
    	Date mDate = DateUtil.addDay(DateUtil.parse(fristSettleDate), term);
    	map.put("mDate", DateUtil.format(mDate));
    	String dealCalId = "04";
    	if(DictCodeListExt1.交易场所_上海证券交易所.getValue().equals(tradeMarket)){
    		 dealCalId = "05";
    	}
    	List<String> holidayList =  dateCalService.getDealCalendarHoliday(mDate, DateUtil.addDay(mDate,30), dealCalId);
    	
    	while(holidayList.contains(DateUtil.format(mDate))){
    		mDate = DateUtil.addDay(mDate,1);
    	}
    	map.put("expireSettleDate", DateUtil.format(mDate));
    	return map;
    }
    /**
     * 本方交易员联想文本框初始化。
     * 
     * @param map 检索参数
     * @return 联想文本框
     */
    public EtrAgreRepTradeDto getEntrustInfoByTradeId(Map<String, Object> map) {
    	List<EtrAgreRepTradeDto>  result =  restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrAgreRepTrade/getEntrustInfoByTradeId",
                map, EtrAgreRepTradeDto.class);
    	if(StringUtil.isEmptyList(result)){
    		return null;
    	}
    	return result.get(0);
    }
    /**
     * 本方交易员联想文本框初始化。
     * 
     * @param map 检索参数
     * @return 联想文本框
     */
    public EtrAgreRepTradeDto getEntrustInfoByEntrustId(Map<String, Object> map) {
    	List<EtrAgreRepTradeDto>  result =  restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrAgreRepTrade/getEntrustInfoByEntrustId",
                map, EtrAgreRepTradeDto.class);
    	if(StringUtil.isEmptyList(result)){
    		return null;
    	}
    	return result.get(0);
    }
    /**
     * 保存check
     * @param entity
     */
    public AjaxResult checkSave(EtrAgreRepTradeDto entity){
        return restTemplate.postForObject(
                ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrAgreRepTrade/checkSave",entity,
                AjaxResult.class);
    }
    /**
	 * 根据实际指令类型来生成流程信息 <一句话功能简述> <功能详细描述>
	 * 
	 * @param dto
	 * @return
	 * @see
	 * @since 1.0
	 */
	public WorkFlowParameter<EtrAgreRepTradeDto> getWorkFlowParameter(EtrAgreRepTradeDto dto) {
		WorkFlowParameter<EtrAgreRepTradeDto> parameter = new WorkFlowParameter<EtrAgreRepTradeDto>();
	
		return parameter;
	}

	@Override
	public void doComplete(EtrAgreRepTradeDto entity, OperationType type) {
		preEntity(entity);
		restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrAgreRepTrade/doComplete", entity,
				Integer.class);
		doDeleteWflEntity(entity.getWorkflowId());
	}
	
	@Override
	public void doInsert(EtrAgreRepTradeDto entity, OperationType type) {
		if ("save".equals(entity.getOperateType())) {
            return;
        }
		doInsertWflEntity(entity);
	}
	
	@Override
	public void doUpdate(EtrAgreRepTradeDto entity) {
		if (!StringUtil.equals("save", entity.getOperateType()) && !StringUtil.equals("revoke", entity.getOperateType()) && !StringUtil.equals("delete", entity.getOperateType())) {
			doInsertWflEntity(entity);
		}
	}
	
	/**
	 * 代办提交修改操作类型
	 * @param dto
	 * @return
	 */
	public EtrAgreRepTradeDto preEntity(EtrAgreRepTradeDto dto) {
		if(!StringUtil.isNotEmpty(dto.getEntrustBussType())) {
			if(DictCodeListExt3.交易方向_提前购回.getValue().equals(dto.getPs())){
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购提前购回指令.getValue());
				dto.setBusinessTypeName(DictCodeListExt3.上农商_指令业务类型_协议回购提前购回指令.getValue());
			}else if(DictCodeListExt3.交易方向_到期购回.getValue().equals(dto.getPs())){
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购到期购回指令.getValue());
				dto.setBusinessTypeName(DictCodeListExt3.上农商_指令业务类型_协议回购到期购回指令.getValue());
			}else if(DictCodeListExt3.交易方向_中途换券.getValue().equals(dto.getPs())){
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购中途换券指令.getValue());
				dto.setBusinessTypeName(DictCodeListExt3.上农商_指令业务类型_协议回购中途换券指令.getValue());
			}else if(DictCodeListExt3.交易方向_解除质押.getValue().equals(dto.getPs())){
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购解除质押指令.getValue());
				dto.setBusinessTypeName(DictCodeListExt3.上农商_指令业务类型_协议回购解除质押指令.getValue());
			}else if(DictCodeListExt3.交易方向_到期续做.getValue().equals(dto.getPs())){
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购到期续做指令.getValue());
				dto.setBusinessTypeName(DictCodeListExt3.上农商_指令业务类型_协议回购到期续做指令.getValue());
			}else{
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_协议回购首期指令.getValue());
				dto.setBusinessTypeName(DictCodeListExt3.上农商_指令业务类型_协议回购首期指令.getValue());
			}

			if(!StringUtil.isNotEmpty(dto.getFromEtrExchange())) {
				dto.setFromEtrExchange("Y");
			}
			if(!StringUtil.isNotEmpty(dto.getBusiPs())) {
				dto.setBusiPs(dto.getRepPs());
			}

			if(!StringUtil.isNotEmpty(dto.getAssetType2())) {
				dto.setAssetType2(DictCodeList.回购类型_质押式协议回购.getValue());
			}

			if(!StringUtil.isNotEmpty(dto.getFinprodType2())) {
				if(StringUtil.equals(dto.getRepPs(),DictCodeList.回购拆借方向_正回购.getValue())) {
					dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
				}
				else {
					dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
				}

			}
		}
		return dto;
	}
	
	@Override
	public void doBack(EtrAgreRepTradeDto entity) {
		doDeleteWflEntity(entity.getWorkflowId());

		// add by etliu 20220116 start
		// GZYHTZJY-1145: 【场内对账清结算1231】深交所协议回购首期交易，日终头寸更新有误【解决日期：2022-01-14】】
		if (StringUtil.isNotBlank(entity.getFinprodId())) {
			entity.setPledgeInvAim(commonPositionService.selectHoldInvAim(entity.getPortfolioId(), entity.getFinprodId()));
		}
		if (StringUtil.isNotBlank(entity.getNewfinprodId())) {
			entity.setNewPledgeInvAim(commonPositionService.selectHoldInvAim(entity.getPortfolioId(), entity.getNewfinprodId()));
		}
		// add by etliu 20220116 end

		// 释放指令头寸
		revokeOrCloseEntrust(entity);
	}
	
	@Override
	public void doDelete(String workflowId) {
		doDeleteWflEntity(workflowId);
	}
	
	private void doInsertWflEntity(EtrAgreRepTradeDto dto) {
		preEntity(dto);
		BaseEtrInvestInfoDto entity = restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrAgreRepTrade/buildWflEntity", dto,
				BaseEtrInvestInfoDto.class);
		entity.getEtrEntrust().setEntrustStatus(DictCodeListExt3.委托状态_流程中.getValue());
		entity.setWorkFlowId(dto.getWorkflowId());
		// 设置指令创建来源
		entity.getEtrEntrustAdd().setEntrustCreateSource(DictCodeListExt3.指令创建来源_场内实时.getValue());
		restTemplate.postForObject(
				ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/doInsertEtrFlow", entity,
				Boolean.class);
		// 新增交易对手交易员
		if (StringUtil.isNotEmpty(dto.getCounterId()) && StringUtil.isNotEmpty(dto.getCounterTrader())){
			Map<String,Object> map = new HashMap<>();
			map.put("counterId",dto.getCounterId());
			map.put("counterTraderId",dto.getCounterTrader());
			restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/insertCounterTraderId", map,
					Object.class);
		}
		// 新增本方交易员
		if (StringUtil.isNotEmpty(dto.getTrader())){
			Map<String,Object> map = new HashMap<>();
			map.put("counterId","homeTrader");
			map.put("counterTraderId",dto.getTrader());
			restTemplate.postForObject(ServiceApplication.BASICDATA_SERVICE_NAME + "/InterfSysTrdConfig/insertCounterTraderId", map,
					Object.class);
		}
		// 指令持仓流水、现金流水处理
		PostionParameter parameter = new PostionParameter();
		parameter.setPs(dto.getPs());
		parameter.setTradeMarket(dto.getTradeMarket());
		parameter.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
		parameter.setOperationType("NEW");
		parameter.setTradeType("01");
		if(DictCodeList.回购拆借方向_正回购.getValue().equals(dto.getRepPs())){
			String businessType = DictCodeList.回购类型_质押式协议回购.getValue().concat(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
			parameter.setBusinessType(businessType);
		}else{
			parameter.setBusinessType(DictCodeList.回购类型_质押式协议回购.getValue().concat(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue()));
		}
		// 查询首期交易类金融产品数据
		FinTradeProductKey finTradeProductKey = new FinTradeProductKey();
		finTradeProductKey.setFinprodId(dto.getTradeId());
		finTradeProductKey.setBranch(0L);
		FinTradeProduct firstFinTradeProduct = finTradeProductMapper.selectByPrimaryKey(finTradeProductKey);
		// add by etliu 20220116 start
		if (firstFinTradeProduct == null){
			String hgsqFinprodId = trdCommonBaseService.selectHgsqFinprodId(dto.getPortfolioId(), dto.getTradeId());
			if (StringUtil.isNotBlank(hgsqFinprodId)) {
				finTradeProductKey.setFinprodId(hgsqFinprodId);
				firstFinTradeProduct = finTradeProductMapper.selectByPrimaryKey(finTradeProductKey);
				dto.setTradeId(hgsqFinprodId);
			}
		}
		// add by etliu 20220116 end

		if (firstFinTradeProduct != null){
			dto.setFirstFinTradeProduct(firstFinTradeProduct);
		}

		if (StringUtil.equals(dto.getPs(),DictCodeListExt3.交易方向_到期续做.getValue()) && DictCodeList.回购拆借方向_逆回购.getValue().equals(dto.getRepPs())){
			// 三方续做
			if (StringUtil.equals(dto.getRedoType(),"1") || firstFinTradeProduct == null){
				parameter.setPs(dto.getPs()+"_1");
			}else {
				// 非三方续做
				parameter.setPs(dto.getPs()+"_0");
			}
		}
		dto.setTradeDate(DateUtil.parse(dto.getVdate()));

		// add by etliu 20220116 start
		// GZYHTZJY-1145: 【场内对账清结算1231】深交所协议回购首期交易，日终头寸更新有误【解决日期：2022-01-14】】
		if (StringUtil.isNotBlank(dto.getFinprodId())) {
			dto.setPledgeInvAim(commonPositionService.selectHoldInvAim(dto.getPortfolioId(), dto.getFinprodId()));
		}
		if (StringUtil.isNotBlank(dto.getNewfinprodId())) {
			dto.setNewPledgeInvAim(commonPositionService.selectHoldInvAim(dto.getPortfolioId(), dto.getNewfinprodId()));
		}
		// add by etliu 20220116 end

		parameter.setCustom(dto);
//		try {
		logger.info("持仓请求报文:" + JsonUtils.object2Json(parameter));
		commonPositionService.process(parameter);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	public void revokeOrCloseEntrust(Object object){
		// 指令持仓流水、现金流水处理
		PostionParameter parameter = new PostionParameter();
		parameter.setPs("ALL");
		parameter.setTradeMarket("ALL");
		parameter.setWorkflowStatus(DictCodeListExt1.交易流程状态_有效.getValue());
		parameter.setOperationType("REVOKE_CLOSE");
		parameter.setTradeType("01");
		parameter.setBusinessType(DictCodeList.回购类型_质押式协议回购.getValue());
		parameter.setCustom(object);
//		try {
		commonPositionService.process(parameter);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
	
	private void doDeleteWflEntity(String workflowId) {
        Map<String,Object> param = new HashMap<>();
        param.put("workflowId",workflowId);
        restTemplate.postForObject(
            ServiceApplication.ENTRUST_SERVICE_NAME + "/common/entrustcommonbase/doDeleteEtrFlow", param,
            Boolean.class);
    }
	
	@Override
	public Map<String, Object> getRuntimeVariables(ProcessMark entity, String taskId) throws WorkFlowException {
		EtrAgreRepTradeDto dto = this.getBussinessEntity(entity);
    	CommonBusinessService commonBusinessService = SpringContextUtil.getBean(CommonBusinessService.class);
    	//回购金额
    	BigDecimal amount = new BigDecimal(0);
    	//交易方向
    	String ps = "";
    	//逆回购和正回购按正常交易金额和交易方向授权
    	if(StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_正回购.getValue())
    			|| StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_逆回购.getValue())) {
    		ps = dto.getPs();
    		amount = dto.getTradeAmt();
    	}
    	//提前购回、到期购回、中途换券、解除质押根据原始回购的交易方向和回购金额进行授权判断
    	else if (StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_提前购回.getValue())
    				|| StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_到期购回.getValue())
    				|| StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_中途换券.getValue())
    				|| StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_解除质押.getValue())) {
    		//获得初始活够金额和交易方向
    		Map<String, Object> map = new HashMap<>();
    		// 上交所、深交所取值来源不一致
    		map.put("tradeId", (dto.getIntiTradeId() == null || dto.getIntiTradeId().equals("")) ? dto.getTradeId() : dto.getIntiTradeId());
    		List<EtrAgreRepTradeDto>  result =  restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrAgreRepTrade/getEntrustInfoByTradeId",
                    map, EtrAgreRepTradeDto.class);
    		if(!StringUtil.isEmptyList(result)) {
    			ps = result.get(0).getPs();
    			amount = result.get(0).getTradeAmt();
    		}
    	}
    	//到期续作使用原始的回购金额和续做的回购金额中的较大值进行判断
    	else if (StringUtil.equals(dto.getPs(), DictCodeListExt3.交易方向_到期续做.getValue())){
    		//获得初始回购金额和交易方向
    		Map<String, Object> map = new HashMap<>();
    		map.put("tradeId", (dto.getIntiTradeId() == null || dto.getIntiTradeId().equals("")) ? dto.getTradeId() : dto.getIntiTradeId());
    		List<EtrAgreRepTradeDto>  result =  restTemplate.postForList(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrAgreRepTrade/getEntrustInfoByTradeId",
                    map, EtrAgreRepTradeDto.class);
    		if(!StringUtil.isEmptyList(result)) {
    			ps = result.get(0).getPs();
    			amount = result.get(0).getTradeAmt();
    			if(dto.getTradeAmt().compareTo(amount)>0) {
    				amount = dto.getTradeAmt();
    			}
    		}else {
    			ps = DictCodeListExt3.交易方向_逆回购.getValue();
    			amount = dto.getTradeAmt();
    		}
    	}
    	if(taskId != null) {
    		try {
	    		Map<String, Object> params = CommonBusinessService.setParams(dto.getCounterId(), amount, LoginUserUtil.getUserName(),
	    				BussinessDictCodeList.平安资产类型_回购.getValue(), DictCodeList.回购类型_质押式协议回购.getValue(), dto.getTradeMarket(), ps
	                    , "", entity.getWorkflowId()
	                    );
	    		//params.put("riskTrialRetDto", dto.getRiskTrialRetDto());
	    		return commonBusinessService.是否需要上级审批_委托用(params);
    		}catch (WorkFlowException e) {
                // 未设置授权或设置成不授权时，抛出异常，当前用户只能退回
                throw new WorkFlowException(e);
            }
    	}
    	return null;
	}
	
    public BigDecimal queryOtherSecPosition(Map<String, Object> param) {
		return restTemplate.postForObject(ServiceApplication.ENTRUST_SERVICE_NAME + "/EtrAgreRepTrade/queryOtherSecPosition", param,
			BigDecimal.class);
    }

    public Map<String,Object> getCashAmount(Map<String,Object> param){
		Map<String, Object> result = new HashMap<>();
		PtlCashPositionFlowDto dto = new PtlCashPositionFlowDto();
		Date vdate = DateUtil.parse((String)param.get("sysDate"));
		dto.setCdate(vdate);
		List<String> acctTypeList = new ArrayList<>();
		dto.setAcctType(Arrays.asList(DictCodeListExt1.资金账户类型_产品托管户.getValue(),DictCodeListExt1.资金账户类型_券商资金账户.getValue()));
		dto.setPortfolioId((String)param.get("portfolioId"));
		dto.setCcy(DictCodeList.币种_人民币_CNY.getValue());
		dto.setAcctId((String)param.get("acctNo"));
		//可用现金
		//BigDecimal cashUse = basePtlCashPositionInfoService.queryUsableCashPosition(dto);
		BigDecimal cashUse = baseEtrCashPositionCommService.qryCommandAvailableCash(dto);
		BigDecimal cashGet = basePtlCashPositionInfoService.queryUsableCashPositionOther(dto);
		//可取现金
		result.put("cashUse",cashUse);
		result.put("cashGet",cashGet);
		return result;
	}
}
