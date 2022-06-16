package com.joyintech.tams.cfets.trd.bond.controller;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.security.SecurityUtil;
import com.joyintech.framework.security.User;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.dto.CfetsExportRequestDto;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ExcelTplUitl;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.cfets.trd.bond.dto.*;
import com.joyintech.tams.cfets.trd.bond.service.*;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribdoRiskcalResponse;
import com.joyintech.tams.cfets.etr.bond.service.CfetsEtrIbBondinfoService;
import com.joyintech.tams.component.business.riskrds.controller.InterfRiskRdsController;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.bankuplink.bond.dto.BondQuoteInfoDto;
import com.joyintech.tams.invest.common.client.ClientData;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.systemtools.dao.TamsBaseMapper;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *  初始化查询页面
 */
@Controller
@PageId("IbuQuoteBond")
@RequestMapping("/cfets/bankuplink/bond/IbuQuoteBond")
@Api(tags = "CFETS债券交易-常用查询类接口")
public class CfetsIbuQuoteBondController extends BaseController {

	private static final Logger logger = LoggerFactory.getLogger(CfetsIbuQuoteBondController.class);

	@Value("${cmdsWebSocketUrl}")
	private String cmdsWebSocketUrl;

	@Autowired
	private IbuQuoteBondService ibuQuoteBondService;

	@Autowired
	private IbuQuoteBondCalService ibuQuoteBondCalService;

	@Autowired
	private CfetsEtrIbBondinfoService cfetsEtrIbBondinfoService;

	@Autowired
	private CfetsBondDealQuotedPrice001Service cfetsBondDealQuotedPrice001Service;

	/**
	 * 风险接口组件Controller
	 */
	@Autowired
	private InterfRiskRdsController interfRiskRdsController;

	@Autowired
	private CfetsQuotedCommonService cfetsQuotedCommonService;

	@Autowired
	private IPlatformDate iPlatformDate;

	@Autowired
	private TamsBaseMapper tamsBaseMapper;

	@Autowired
	private EntrustForcedFinishService entrustForcedFinishService;

	@ApiOperation(value = "初始化参数", notes = "初始化参数")
	@PostMapping("/initParams")
	@ResponseBody
	public Message<Map<String, Object>> initParams(){
		Map<String, Object> initPageParams = new HashMap<>();
		initPageParams.put("PS", this.getSelectDataFromDataDic(DictCode.交易方向, null));
		initPageParams.put("ACCOUNT_SPEED", this.getSelectDataFromDataDic(DictCode.清算速度, null));
		initPageParams.put("SETT_MODE", this.getSelectDataFromDataDic(DictCode.结算方式, null));
		initPageParams.put("CLEAR_TYPE", this.getSelectDataFromDataDic(DictCode.清算类型, null));
		initPageParams.put("QUOTE_TYPE", this.getSelectDataFromDataDic(BussinessDictCode.报价方式, null));
		initPageParams.put("QUOTE_STYLE", this.getSelectDataFromDataDic(BussinessDictCode.报价方式_登记, null));
		initPageParams.put("TRADE_PLACE", this.getSelectDataFromDataDic(DictCode.交易场所, null));
		initPageParams.put("PRICE_MODEL", this.getSelectDataFromDataDic(DictCode.价格模式, null));
		initPageParams.put("CCY", this.getSelectDataFromDataDic(DictCode.币种, null));
		initPageParams.put("ACCOUNT_SPEED_0",BussinessDictCodeList.清算速度_T加0.getValue());
		initPageParams.put("ACCOUNT_SPEED_1",BussinessDictCodeList.清算速度_T加1.getValue());
		initPageParams.put("ACCOUNT_SPEED_2",BussinessDictCodeList.清算速度_T加2.getValue());
		initPageParams.put("yesNoFlag_yes",BussinessDictCodeList.是否_是.getValue());
		initPageParams.put("assetTypeOne_abs",BussinessDictCodeList.平安资产一级分类_资产支持证券.getValue());
		initPageParams.put("calcTypeUnitCprice",BussinessDictCodeList.计算选择方式_净价.getValue());
		initPageParams.put("calcTypeYield",BussinessDictCodeList.计算选择方式_到期收益率.getValue());
		initPageParams.put("calcTypeExecYield",BussinessDictCodeList.计算选择方式_行权收益率.getValue());
		initPageParams.put("PRICE_MODEL_PRM01",DictCodeListExt2.价格模式_指定价格.getValue());
		initPageParams.put("ouTraderIdCanEditFlag",CacheUtil.getSysParam("ibuFlag_ouTraderIdCanEdit"));
		initPageParams.put("ibuFlag_startDialogueQuoteWithNoEntrust",CacheUtil.getSysParam("ibuFlag_startDialogueQuoteWithNoEntrust"));
		initPageParams.put("SHORT_SELLING_SUPPORTED",CacheUtil.getSysParam("SHORT_SELLING_SUPPORTED"));
		//CMDS地址
		initPageParams.put("cmdsWebSocketUrl", cmdsWebSocketUrl);
		initPageParams.put("EXPORT_SHOW",CacheUtil.getSysParam("EXPORT_SHOW"));
		initPageParams.put("PAGE_ID_REGIT","IbuQuoteBond004");
		initPageParams.put("PAGE_ID_DIALOG","IbuQuoteBond002");
		initPageParams.put("PAGE_ID_REQUEST","IbuQuoteBond003");
		//01: 开启银行间报价 02:关闭银行间报价（事后录入）
		initPageParams.put("ibuFlag_quote_switch",CacheUtil.getSysParam("ibuFlag_quote_switch"));
		initPageParams.put("is_sys_support_quote",CacheUtil.getSysParam("is_sys_support_quote"));
		//刷新时间
		initPageParams.put("ibuQuoteBond_etrTable_refresh_period", CacheUtil.getSysParam("ibuQuoteBond_etrTable_refresh_period"));
		initPageParams.put("effDate", CacheUtil.getSysParam("ibuVar_quoteEffTime"));
		return new Message<Map<String, Object>>(initPageParams).success();
	}

	@ApiOperation(value = "查询指令列表分页数据", notes = "查询指令列表分页数据")
	@PostMapping("/pageEntrust")
	@ResponseBody
	public Message<Page<EntrustDto>> pageEntrust(@RequestBody EntrustRequest dto){
		try {
			Page<EntrustDto> list = ibuQuoteBondService.pageEntrust(dto);
			return new Message<Page<EntrustDto>>().success().setData(list);
		}catch (Exception e) {
			logger.error("查询指令列表分页数据异常",e);
			return new Message<Page<EntrustDto>>().fail().setMessage("查询指令列表分页数据异常");
		}
	}

	@ApiOperation(value = "查询指令详细信息", notes = "查询指令详细信息")
	@PostMapping("/getEntrust/{entrustId}")
	@ResponseBody
	public Message<EntrustDto> getEntrust(@PathVariable("entrustId") String entrustId){
		try {
			EntrustDto result = ibuQuoteBondService.gettEntrust(entrustId);
			return new Message<EntrustDto>().success().setData(result);
		}catch (Exception e) {
			logger.error("查询指令详细信息异常",e);
			return new Message<EntrustDto>().fail().setMessage("查询指令详细信息异常");
		}
	}

	@ApiOperation(value = "接收指令(批量)", notes = "接收指令(批量,key=entrustIds 传数组)")
	@PostMapping("receiveEntrust")
	@ResponseBody
	public Message<AjaxResult> recallDelegate(@RequestBody Map<String,Object> param){
		try{
			AjaxResult result = new AjaxResult();
			List<ValidMessage> validMessages = ibuQuoteBondService.receiveEntrust(param);
			result.setErrorList(validMessages);
			return new Message<AjaxResult>().success().setData(result);
		}catch (Exception e){
			logger.error("接收指令(批量)异常",e);
			return new Message<AjaxResult>().fail().setMessage("接收指令(批量)异常");
		}
	}

	@ApiOperation(value = "放弃委托(批量)", notes = "放弃委托(批量,key=entrustIds 传字符串用逗号隔开)")
	@PostMapping("giveUpEntrust")
	@ResponseBody
	public Message<AjaxResult> entrustGiveUpHandle(@RequestBody Map<String,Object> param){
		try{
			AjaxResult result = new AjaxResult();
			List<String> entrustIds = com.joyintech.framework.util.StringUtil.splitToList(",",(String)param.get("entrustIds"));
			entrustIds.forEach(item ->{
				String flag = ibuQuoteBondService.giveUpEntrust(item);
				if(StringUtil.equals("1", flag)){
					result.appendMessage(new ValidMessage("已接收且完成度为0指令,才能执行放弃操作！"));
					return;
				}
				else if(StringUtil.equals("2", flag)){
					result.appendMessage(new ValidMessage("存在流程中的报价，不可放弃！"));
					return;
				}
			});
			return new Message<AjaxResult>().success().setData(result);
		}catch (Exception e){
			logger.error("放弃指令异常",e);
			return new Message<AjaxResult>().fail().setMessage("放弃指令异常");
		}
	}

	@ApiOperation(value = "指令退回(批量)", notes = "指令退回(批量,key=entrustIds指令代码, key=remark退回原因)")
	@PostMapping("entrustRefuce")
	@ResponseBody
	public Message<AjaxResult> entrustRefuce(@RequestBody Map<String,Object> param){
		try{
			AjaxResult result = new AjaxResult();
			if(com.joyintech.framework.util.StringUtil.isNotEmpty(param.get("entrustId"))){
				param.put("entrustIdList", com.joyintech.framework.util.StringUtil.splitToList(",",(String)param.get("entrustIds")));
			}
			result = ibuQuoteBondService.entrustRefuceCheck(param);
			if(CollectionUtils.isNotEmpty(result.getErrorList())){
				return new Message<AjaxResult>().success().setData(result);
			}
			ibuQuoteBondService.entrustRefuce(param);
			return new Message<AjaxResult>().success().setMessage("退回成功");
		}catch (Exception e){
			logger.error("指令退回异常",e);
			return new Message<AjaxResult>().fail().setMessage("指令退回异常");
		}
	}

	@ApiOperation(value = "交易执行初始化检查", notes = "交易执行初始化检查")
	@PostMapping("/dealexecutioncheck")
	@ResponseBody
	public Message<AjaxResult> dealexecutioncheck(@RequestBody CfetsBondDealQuoteRequest cfetsBondDealQuoteRequest){
		AjaxResult ajaxResult = ibuQuoteBondService.dealexecutioncheck(cfetsBondDealQuoteRequest);
		return new Message<AjaxResult>().success().setData(ajaxResult);
	}


	@ApiOperation(value = "交易执行初始化", notes = "交易执行初始化")
	@PostMapping("/dealexecutioninit")
	@ResponseBody
	public Message<CfetsBondDealQuoteCommonDto> dealexecutioninit(@RequestBody CfetsBondDealQuoteRequest cfetsBondDealQuoteRequest){
		CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto = ibuQuoteBondService.queryEntrustInfoById(cfetsBondDealQuoteRequest);
		return new Message<CfetsBondDealQuoteCommonDto>().success().setData(cfetsBondDealQuoteCommonDto);
	}


	@ApiOperation(value = "组合分配添加初始化", notes = "组合分配添加初始化")
	@PostMapping("/ptlAllocationAdd")
	@ResponseBody
	public Message<List<EntrustDto>> ptlAllocationAdd(@RequestBody PtlAllocationAddRequest ptlAllocationAddRequest) {
		try {
			List<EntrustDto> list = ibuQuoteBondService.ptlAllocationAdd(ptlAllocationAddRequest);
			return new Message<List<EntrustDto>>().success().setData(list);
		} catch (Exception e) {
			logger.error("组合分配添加初始化异常", e);
			return new Message<List<EntrustDto>>().fail().setMessage("组合分配添加初始化异常");
		}
	}

	@ApiOperation(value = "数据计算", notes = "净价、到期收益率、行权收益率联动计算")
	@PostMapping("/calculation")
	@ResponseBody
	public Message<CalRespone> calculation(@RequestBody CalRequest calRequest) {
		try {
			CalRespone calculation = ibuQuoteBondCalService.calculation(calRequest);
			return new Message<CalRespone>().success().setData(calculation);
		} catch (Exception e) {
			logger.error("数据计算异常", e);
			return new Message<CalRespone>().fail().setMessage("数据计算异常");
		}
	}

	@ApiOperation(value = "结算金额反算", notes = "结算金额反算全价、应计利息、应计利息总额")
	@PostMapping("/calculationByFpriceAmt")
	@ResponseBody
	public Message<CalRespone> calculationByFpriceAmt(@RequestBody CalRequest calRequest) {
		try {
			CalRespone calculation = ibuQuoteBondCalService.calculationByFpriceAmt(calRequest);
			return new Message<CalRespone>().success().setData(calculation);
		} catch (Exception e) {
			logger.error("结算金额反算异常", e);
			return new Message<CalRespone>().fail().setMessage("结算金额反算异常");
		}
	}


	@ApiOperation(value = "自动分配", notes = "自动分配")
	@PostMapping("/autoAllocation")
	@ResponseBody
	public Message<List<PtlAllocationDto>> autoAllocation(@RequestBody AutoAllocationRequest autoAllocationRequest) {
		try {
			List<PtlAllocationDto> list = ibuQuoteBondCalService.autoAllocation(autoAllocationRequest);
			return new Message<List<PtlAllocationDto>>().success().setData(list);
		} catch (Exception e) {
			logger.error("自动分配异常", e);
			return new Message<List<PtlAllocationDto>>().fail().setMessage("自动分配异常");
		}
	}

	@ResponseBody
	@ApiOperation(value = "风险试算check", notes = "风险试算check")
	@PostMapping("doRiskcalCheck")
	public Message<AjaxResult> doRiskcalCheck(@RequestBody RiskCalRequest riskCalRequest){
		AjaxResult ajaxResult = ibuQuoteBondService.riskCheckNotEmpty(riskCalRequest);
		return new Message<AjaxResult>().success().setData(ajaxResult);
	}

	@ResponseBody
	@ApiOperation(value = "风险试算", notes = "风险试算")
	@PostMapping("doRiskcal")
	public Message<AjaxResult> doRiskcal(@RequestBody RiskCalRequest riskCalRequest){
		CfetsEtribdoRiskcalResponse result=new CfetsEtribdoRiskcalResponse();
		try{
			AjaxResult ajaxResult = ibuQuoteBondService.riskCheckNotEmpty(riskCalRequest);
			if(CollectionUtils.isNotEmpty(ajaxResult.getErrorList())){
				return new Message<AjaxResult>().success().setData(ajaxResult);
			}
			Map<String, Object> dataMap = ibuQuoteBondService.doRiskcalPackage(riskCalRequest);
			List<String> resultArrList=interfRiskRdsController.doInfBatchData(dataMap,"0", "RSK002","F01");
			result.setRiskMathFlag("02");
			Boolean forbidFlag=false;
			Boolean cluflag=true;
			//是否全部正常
			Boolean allNormalFlag = true;
			List<CfetsEtribdoRiskcalResponse> resultlist=new ArrayList<>();
			if(!StringUtil.isEmptyList(resultArrList) ){
				for(String row:resultArrList){
					ClientData resultArr = JsonUtils.json2Object(row, ClientData.class);
					if(StringUtil.isNotEmpty(resultArr) && StringUtil.isNotEmpty(resultArr.getHead())
							&& StringUtil.isNotEmpty(resultArr.getHead().get("std400msgt"))
					){
						if (StringUtil.equals(resultArr.getHead().get("std400msgt"),"E")) {
							return new Message<AjaxResult>().fail().setMessage(resultArr.getHead().get("stdrtninfo").toString());
						}
					}
					if (!StringUtil.isNotEmpty(resultArr) || !StringUtil.isNotEmpty(resultArr.getBody())
							|| !StringUtil.isNotEmpty(resultArr.getBody().get("RSK_LIST"))
							|| !StringUtil.isNotEmpty(resultArr.getBody().get("rskStatus"))) {
						return new Message<AjaxResult>().success().setMessage("无");
					}
					List<Map<String, Object>> riskList = (List<Map<String, Object>>)resultArr.getBody().get("RSK_LIST");
					for(Map<String, Object> map: riskList){
						if(allNormalFlag&&!StringUtil.equals(map.get("kriStatus"),"0")){
							allNormalFlag = false;
						}
						if(StringUtil.equals(map.get("kriStatus"),"0") && !StringUtil.equals(map.get("kriTrend"),"向好")){
							forbidFlag=true;
						}
						map.put("kriStatusCn",cfetsEtrIbBondinfoService.getKriStatusCnByKriStatus(map.get("kriStatus").toString()));
						resultlist.add(BeanUtil.mapToObject(map, CfetsEtribdoRiskcalResponse.class));
					}
					if(!allNormalFlag){
						result.setRiskcalReturnList(resultlist);
					}
					result.setStdrtninfo(resultArr.getHead().get("stdrtninfo").toString());
					//当置为不通过时就不再进来了
					if(cluflag) {
						if(!StringUtil.equals(resultArr.getBody().get("rskStatus"),"3")){
							if(StringUtil.equals(resultArr.getBody().get("rskStatus"),"4")&&forbidFlag){
								cluflag=false;
								resultArr.getBody().put("rskStatus","3");
								result.setRskStatus("3");
							}
						}else{
							cluflag=false;
						}
						if (!cluflag) {
							result.setRiskTrialSpan("试算不通过");
						} else {
							result.setRiskTrialSpan("试算通过");
						}
					}

				}

			}
			AjaxResult m =new AjaxResult();
			m.setData(result);
			return new Message<AjaxResult>().success().setData(m);
		}
		catch (Exception e){
            logger.error("风险试算异常",e);
			return new Message<AjaxResult>().fail().setMessage("风险试算异常");
		}
	}

	@ApiOperation(value = "查询交易成交信息", notes = "查询交易成交信息")
	@PostMapping("/listDealTransactionDetails/{entrustId}")
	@ResponseBody
	public Message<Map<String,Object>> listDealTransactionDetails(@PathVariable("entrustId") String entrustId){
		try {
			Map<String,Object> result = new HashMap<>();
			result.put("root",ibuQuoteBondService.listDealTransactionDetails(entrustId));
			return new Message<Map<String,Object>>().success().setData(result);
		}catch (Exception e) {
			logger.error("查询交易成交信息异常",e);
			return new Message<Map<String,Object>>().fail().setMessage("查询交易成交信息异常");
		}
	}


	@ApiOperation(value = "报价批量撤回检查", notes = "报价批量撤回检查")
	@ResponseBody
	@PostMapping("/checkOperationAllowList")
	public Message<AjaxResult> checkOperationAllowList(@RequestBody List<CfetsBondCheckRequst> cfetsBondCheckRequsts) {
		AjaxResult aj = null;
		for(CfetsBondCheckRequst cfetsBondCheckRequst:cfetsBondCheckRequsts){
			BondQuoteInfoDto bondQuoteInfoDto = new BondQuoteInfoDto();
			BeanUtil.copyPropertiesIgnoreNull(bondQuoteInfoDto,cfetsBondCheckRequst);
			bondQuoteInfoDto.setOperationType("recall");
			aj =cfetsQuotedCommonService.checkOperationAllow(bondQuoteInfoDto);
			if(!com.joyintech.framework.util.StringUtil.isEmptyList(aj.getErrorList())){
				return new Message<AjaxResult>().success().setData(aj);
			}
		}
		return new Message<AjaxResult>().success().setData(aj);
	}

	@ApiOperation(value = "报价检查", notes = "报价修改、复制、回退、回复、删除、撤回检查")
	@ResponseBody
	@PostMapping("/checkOperationAllow")
	public Message<AjaxResult> checkOperationAllow(@RequestBody CfetsBondCheckRequst cfetsBondCheckRequst) {
		BondQuoteInfoDto bondQuoteInfoDto = new BondQuoteInfoDto();
		BeanUtil.copyPropertiesIgnoreNull(bondQuoteInfoDto,cfetsBondCheckRequst);
		return new Message<AjaxResult>().success().setData(cfetsQuotedCommonService.checkOperationAllow(bondQuoteInfoDto));
	}

	@ApiOperation(value = "报价撤回", notes = "报价撤回")
	@ResponseBody
	@PostMapping("doReCall")
	public Message<AjaxResult> doReCall(@RequestBody CfetsBondCheckRequst cfetsBondCheckRequst) throws Exception{
		AjaxResult ar = null;
		BondQuoteInfoDto bondQuote = new BondQuoteInfoDto();
		BeanUtil.copyPropertiesIgnoreNull(bondQuote,cfetsBondCheckRequst);
		try{
			if (ibuQuoteBondService.doReCall(bondQuote)) {
				ar = new AjaxResult(this.getMessage("CFETS_COMMON_SUCSESS"));
			}
		}catch (Exception e){
			e.printStackTrace();
			return PositionUtil.ajaxErrorReturn(e);
		}
		return new Message<AjaxResult>().success().setData(ar);
	}


	@ApiOperation(value = "报价批量撤回", notes = "报价批量撤回")
	@ResponseBody
	@PostMapping("doReCallList")
	public Message<AjaxResult> doReCallList(@RequestBody List<CfetsBondCheckRequst> cfetsBondCheckRequsts) throws Exception{
		AjaxResult ar = null;
		try{
			for(CfetsBondCheckRequst cfetsBondCheckRequst:cfetsBondCheckRequsts){
				BondQuoteInfoDto bondQuote = new BondQuoteInfoDto();
				BeanUtil.copyPropertiesIgnoreNull(bondQuote,cfetsBondCheckRequst);
				if (ibuQuoteBondService.doReCall(bondQuote)) {
					ar = new AjaxResult(this.getMessage("CFETS_COMMON_SUCSESS"));
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			return PositionUtil.ajaxErrorReturn(e);
		}
		return new Message<AjaxResult>().success().setData(ar);
	}

	@ApiOperation(value = "回退", notes = "回退")
	@ResponseBody
	@PostMapping("doBack")
	public Message<AjaxResult> doBack(@RequestBody CfetsBondCheckRequst cfetsBondCheckRequst) {
		AjaxResult ar = null;
		BondQuoteInfoDto bondQuote = new BondQuoteInfoDto();
		BeanUtil.copyPropertiesIgnoreNull(bondQuote,cfetsBondCheckRequst);
		if (ibuQuoteBondService.doBack(bondQuote)) {
			ar = new AjaxResult(this.getMessage("CFETS_COMMON_SUCSESS"));
		} ;

		return new Message<AjaxResult>().success().setData(ar);
	}

	@ApiOperation(value = "报价删除", notes = "报价删除")
	@ResponseBody
	@PostMapping("doDel")
	public Message<AjaxResult> doDel(@RequestBody CfetsBondCheckRequst cfetsBondCheckRequst) {
		AjaxResult ar = null;
		BondQuoteInfoDto bondQuote = new BondQuoteInfoDto();
		BeanUtil.copyPropertiesIgnoreNull(bondQuote,cfetsBondCheckRequst);
		if (ibuQuoteBondService.doDel(bondQuote)) {
			ar = new AjaxResult(this.getMessage("CFETS_COMMON_SUCSESS"));
		} ;

		return new Message<AjaxResult>().success().setData(ar);
	}



	@ApiOperation(value = "强制完成检查", notes = "强制完成检查")
	@ResponseBody
	@PostMapping("/doForcedFinishCheck")
	public Message<AjaxResult> doForcedFinishCheck(@RequestBody Map<String,Object> param) {
		BondQuoteInfoDto bondQuoteInfoDto = new BondQuoteInfoDto();
		AjaxResult ar = null;
		List<String> entrustIds = StringUtil.splitToList(",",(String)param.get("entrustIds"));
		param.put("entrustIds",entrustIds);
		return new Message<AjaxResult>().success().setData(ibuQuoteBondService.doForcedFinishCheck(param,false));
	}


	@ApiOperation(value = "强制完成", notes = "强制完成")
	@ResponseBody
	@PostMapping("doForcedFinish")
	public Message<AjaxResult> doForcedFinish(@RequestBody Map<String,Object> param) {
		AjaxResult ar = null;
		List<String> entrustIds = StringUtil.splitToList(",",(String)param.get("entrustIds"));
		param.put("entrustIds",entrustIds);
		ar = ibuQuoteBondService.doForcedFinishCheck(param,true);
		if(CollectionUtils.isNotEmpty(ar.getErrorList())){
			return new Message<AjaxResult>().success().setData(ar);
		}
		try{
			ibuQuoteBondService.doForcedFinish(param);
			ar = new AjaxResult(this.getMessage("强制完成成功"));
		}
		catch (Exception e){
			ar = new AjaxResult(this.getMessage("强制完成异常"));
		}
		return new Message<AjaxResult>().success().setData(ar);
	}

	@ApiOperation(value = "失败", notes = "失败")
	@ResponseBody
	@PostMapping("/selectErrlog")
	public Message<AjaxResult> selectErrlog(String quoteId) {
		AjaxResult ar = new AjaxResult();
		ar.setData(cfetsQuotedCommonService.selectErrlog(quoteId));
		return new Message<AjaxResult>().success().setData(ar);
	}

	@ApiOperation(value = "指令列表导出", notes = "指令列表导出")
	@ResponseBody
	@PostMapping("exportEntrust")
	public void export(@RequestBody CfetsExportRequestDto requestDto, HttpServletResponse response){
		// 导出
		ExcelUitl.doExport(requestDto, response, "指令列表", () -> {
			EntrustRequest  entrustRequest = requestDto.getDto(EntrustRequest.class);
			List dataList = ibuQuoteBondService.pageEntrust(entrustRequest).getRoot();
			return dataList;
		});
	}


	@ApiOperation(value = "当日报价", notes = "当日报价")
	@PostMapping("/listDayPrice")
	@ResponseBody
	public Message<List<CfetsBondDayPriceDto>> pageDayPrice(@RequestBody DayPriceRequest dto){
		try {
			Map<String, Object> stringObjectMap = JsonUtils.object2Map(dto);
			stringObjectMap.put("keywords",dto.getSearch());
			User user = SecurityUtil.getLoginUser();
			if(StringUtil.isNotEmpty(user.getIbuTrader())){
				stringObjectMap.put("mySideTrader", user.getIbuTrader());
			}
			List<CfetsBondDayPriceDto> result = cfetsBondDealQuotedPrice001Service.listDayPrice(stringObjectMap);
			return new Message<List<CfetsBondDayPriceDto>>().success().setData(result);
		}catch (Exception e) {
			logger.error("查询当日报价数据异常",e);
			return new Message<List<CfetsBondDayPriceDto>>().fail().setMessage("查询当日报价数据异常");
		}
	}

	@ApiOperation(value = "当日报价导出", notes = "当日报价导出")
	@ResponseBody
	@PostMapping("exportDayPrice")
	public void exportDayPrice(@RequestBody CfetsExportRequestDto requestDto, HttpServletResponse response){
		// 导出
		ExcelUitl.doExport(requestDto, response, "当日报价", () -> {
			Map<String,Object>  stringObjectMap = requestDto.getDto(Map.class);
			List dataList = cfetsBondDealQuotedPrice001Service.listDayPrice(stringObjectMap);
			return dataList;
		});
	}

	@ApiOperation(value = "查询当日登记列表分页数据", notes = "查询当日登记列表分页数据")
	@PostMapping("/pageDayReg")
	@ResponseBody
	public Message<Page<CfetsBondDayRegDto>> pageDayReg(@RequestBody DayRegRequest dto){
		try {
			Page<CfetsBondDayRegDto> list = ibuQuoteBondService.pageDayReg(dto);
			return new Message<Page<CfetsBondDayRegDto>>().success().setData(list);
		}catch (Exception e) {
			logger.error("查询当日登记列表分页数据异常",e);
			return new Message<Page<CfetsBondDayRegDto>>().fail().setMessage("查询当日登记列表分页数据异常");
		}
	}


	@ApiOperation(value = "当日登记导出", notes = "当日登记导出")
	@ResponseBody
	@PostMapping("exportDayRegDeal")
	public void exportDayRegDeal(@RequestBody CfetsExportRequestDto requestDto, HttpServletResponse response){
		// 导出
		ExcelUitl.doExport(requestDto, response, "当日登记", () -> {
			Map<String,Object>  param = requestDto.getDto(Map.class);
			List dataList = ibuQuoteBondService.listDayReg(param);
			return dataList;
		});
	}

	@ApiOperation(value = "本币交易导出检查", notes = "本币交易导出检查")
	@PostMapping("/exportDayRegCheck")
	@ResponseBody
	public Message<Map<String,Object>> exportDayRegCheck(@RequestBody CfetsExportRequestDto requestDto){
		Map<String,Object> param = new HashMap<>();
		List<String> quoteIds = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(requestDto.getData())){
			for (BaseMap item : requestDto.getData()) {
				quoteIds.add((String)item.get("quoteId"));
			}
			param.put("quoteIds",quoteIds);
		}
		//目前只导出对话登记
		/*param.put("quoteTypes", Arrays.asList("01"));*/
		List<CfetsBondDayRegDto> dataList = ibuQuoteBondService.listDayReg(param);
		return new Message<Map<String,Object>>().success().setData(ibuQuoteBondService.exportDayRegCheck(dataList));
	}

	@ApiOperation(value = "本币交易导出", notes = "本币交易导出")
	@PostMapping("/exportDayReg")
	@ResponseBody
	public Message<List<Map<String,String>>> exportDayReg(@RequestBody CfetsExportRequestDto requestDto, HttpServletResponse response){
		List<Map<String,String>> result = new ArrayList<>();
		Map<String,Object> param = new HashMap<>();
		List<String> quoteIds = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(requestDto.getData())){
			for (BaseMap item : requestDto.getData()) {
				quoteIds.add((String)item.get("quoteId"));
			}
			param.put("quoteIds",quoteIds);
		}
		if(null!=requestDto.getParams()){
			String operationFlag = (String) requestDto.getParams().get("operationFlag");
			param.put("operationFlag",operationFlag);
		}
		//目前只导出对话登记
		/*param.put("quoteTypes", Arrays.asList("01"));*/
		List<CfetsBondDayRegDto> dataList = ibuQuoteBondService.listDayReg(param);
		String ibuExportCountMax = CacheUtil.getSysParam("IBU_EXPORT_COUNT_MAX");
		String prefix = StringUtil.join("债券对话报价本币交易导出_",iPlatformDate.getSysDateStr());
		requestDto.setData(null);
		//净价、到期收益率、行权收益率，只需导出对应锚定值数据，其他数据应为空
		if(CollectionUtils.isNotEmpty(dataList)){
			dataList.forEach(item ->{
				//债券代码不需要后缀
				item.setFinprodMarketId(item.getFinprodMarketId().substring(0,item.getFinprodMarketId().indexOf(".")));
				//清算速度不需要显示T，只需要显示+0或+1
				if(StringUtil.isNotEmpty(item.getSettleSpeedStr())){
					item.setSettleSpeedStr(item.getSettleSpeedStr().substring(item.getSettleSpeedStr().indexOf("+"),item.getSettleSpeedStr().length()));
				}
				item.setSettCcyStr(item.getSettCcy());
				if(StringUtil.equals(item.getCalMode(),"01")){item.setMatureYield(null);item.setExerYield(null);}
				if(StringUtil.equals(item.getCalMode(),"02")){item.setMatureYield(ArithmeticUtil.multiply(item.getMatureYield(),new BigDecimal(100)));item.setPrice(null);item.setExerYield(null);}
				if(StringUtil.equals(item.getCalMode(),"03")){item.setExerYield(ArithmeticUtil.multiply(item.getExerYield(),new BigDecimal(100)));item.setPrice(null);item.setMatureYield(null);}
			});
		}
		List<File> files = new ArrayList<>();
		if(StringUtil.isNotEmpty(ibuExportCountMax)&&CollectionUtils.isNotEmpty(dataList)){
			int max = Integer.parseInt(ibuExportCountMax);
			int fileCount = (dataList.size()+max-1)/ max;
			List<List<CfetsBondDayRegDto>> collect = Stream.iterate(0, n -> n + 1).limit(fileCount).map(
					a -> dataList.stream().skip(a * max).limit(max).collect(Collectors.toList())
			).collect(Collectors.toList());
			for(int i=0;i<collect.size();i++){
				List<CfetsBondDayRegDto> cfetsBondDayRegDtos = collect.get(i);
				//获取递增序号
				Map<String,String> sequenceIndex = new HashMap<>();
				sequenceIndex.put("databaseType","Oracle");
				sequenceIndex.put("sequenceName","DAYREGEXPORT_SEQUENCE");
				Long sequenceIndexData = tamsBaseMapper.getSequenceIndexData(sequenceIndex);
				if(null==sequenceIndexData){sequenceIndexData = 1L;}
				String appendString = collect.size()>=1?String.format("%04d", sequenceIndexData):StringUtil.EMPTY;
				String excelAndSheetName = StringUtil.join(prefix,"_",appendString);
				files.add(ExcelTplUitl.createFile(requestDto, excelAndSheetName,"本币交易","DAYREGEXPORTTPL.xlsx",() -> {
					return cfetsBondDayRegDtos;
				}));
			}
		}
		else{
			//获取递增序号
			Map<String,String> sequenceIndex = new HashMap<>();
			sequenceIndex.put("databaseType","Oracle");
			sequenceIndex.put("sequenceName","DAYREGEXPORT_SEQUENCE");
			Long sequenceIndexData = tamsBaseMapper.getSequenceIndexData(sequenceIndex);
			if(null==sequenceIndexData){sequenceIndexData = 1L;}
			String excelAndSheetName = StringUtil.join(prefix,"_",sequenceIndexData);
			files.add(ExcelTplUitl.createFile(requestDto, excelAndSheetName,"本币交易","DAYREGEXPORTTPL.xlsx",() -> {
				return dataList;
			}));
		}
		for (File file : files) {
			Map<String,String> map = new HashMap<>();
			map.put("fileName",file.getName());
			map.put("filePath",file.getAbsolutePath());
			result.add(map);
		}
		//处理导出次数
		if(CollectionUtils.isNotEmpty(dataList)){
			ibuQuoteBondService.handExportTimes(dataList);
		}
		return new Message<List<Map<String,String>>>().success().setData(result);
	}

	@ApiOperation(value = "当日登记文件下载", notes = "当日登记文件下载")
	@PostMapping("/downDayReg")
	@ResponseBody
	public void downDayReg(@RequestBody Map<String,String> param, HttpServletResponse response){
		ExcelUitl.downFile(response,new File(param.get("filePath")));
	}

	@ApiOperation(value = "计算偏离度", notes = "计算偏离度(参数资产代码finprodId和净价unitCprice)")
	@PostMapping("/calcDeviationDegree")
	@ResponseBody
	public Message<String> calcDeviationDegree(@RequestBody Map<String,Object> param){
		String deviationDegree =  ibuQuoteBondCalService.calcDeviationDegree(param);
		return new Message<String>().success().setData(deviationDegree);
	}


	@ApiOperation(value = "交易类型按钮点击检查", notes = "交易类型按钮点击检查")
	@ResponseBody
	@PostMapping("/radioPartyIdCheck")
	public Message<AjaxResult> radioPartyIdCheck(@RequestBody  CfetsBondDealQuoteCommonDto cfetsBondDealQuoteCommonDto) {
		return new Message<AjaxResult>().success().setData(cfetsQuotedCommonService.radioPartyIdCheck(cfetsBondDealQuoteCommonDto));
	}

}
