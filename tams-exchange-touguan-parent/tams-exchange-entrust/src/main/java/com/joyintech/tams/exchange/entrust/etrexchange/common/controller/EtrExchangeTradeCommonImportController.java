package com.joyintech.tams.exchange.entrust.etrexchange.common.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.progressbar.ProgressBarService;
import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.business.common.exception.PositionException;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.*;
import com.joyintech.tams.exchange.entrust.common.aop.ExchangeEntrustRdsCommonAop;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeTradePortfolioRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeTradeSaveRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.common.service.EtrExchangeTradeCommon001Service;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tools.util.ValidationUtil;
import com.joyintech.tools.util.ValidationUtil.ErrorMessage;
import com.joyintech.tools.util.ValidationUtil.ValidResult;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 新UI场内交易指令导入
 * 〈功能详细描述〉
 * @version 1.0 2022年3月10日
 * @see EtrExchangeTradeCommonImportController
 * @since 1.0
 */
@Controller
@PageId("EtrExchangeTradeCommonImport")
@RequestMapping("/etrexchange/common/EtrExchangeTradeCommonImport")
public class EtrExchangeTradeCommonImportController extends BaseController{

	@Autowired
	private ProgressBarService progressBarService;

	@Autowired
	private IPlatformDate iPlatformDate;

	/**
	 * 业务编号组件
	 */
	@Autowired(required = false)
	private BaseGetBusinessId bussGetBussinessIdService;

	@Autowired
	private EtrExchangeTradeCommon001Service etrExchangeTradeCommon001Service;
	
	public static final String ENTRUST_RECORD_NEW = "new";
    
	@Override
	public String init(Model model) {
		this.setSelectOptionToModel(model,new DictCode[]{DictCode.投资目的});
		Date sysDate = DateUtil.platDateFromCache();
		model.addAttribute("sysDate", DateUtil.format(sysDate));
		 //流程id
        String workflowId = "null".equals(getParam(GlobalConstant.WORKFLOW_ID))?null:getParam(GlobalConstant.WORKFLOW_ID);
        model.addAttribute("workflowId", workflowId);
        //母页面的pageModel
        String parentPageModel = getParam(GlobalConstant.DETAIL_PAGE_MODEL_KEY);
        model.addAttribute("parentPageModel", parentPageModel);

		EtrExchangeTradeSaveRequest entity = new EtrExchangeTradeSaveRequest();
		if (StringUtil.isNotEmpty(workflowId)) {
			// 流程代办
			entity = etrExchangeTradeCommon001Service.getBussinessEntityByWorkflowId(workflowId);
			EtrExchangeTradeSaveRequest dto = new EtrExchangeTradeSaveRequest();
			List<EtrExchangeTradeSaveRequest> selectedItem = new ArrayList<EtrExchangeTradeSaveRequest>();
			selectedItem.add(entity);
			dto.setSelectedItem(selectedItem);
			model.addAttribute(GlobalConstant.ENTITY, dto);
			model.addAttribute("etrExchangeInfoDtoList",StringUtil.isEmptyList(dto.getSelectedItem()) ? null : JsonUtils.object2Json(dto.getSelectedItem()));
			model.addAttribute("workflowId", workflowId);
		}
        else{
			model.addAttribute("currentTab", getParam("currentTab"));
         }
		 // 金融产品类型(投管用)
        model.addAttribute("FINPROD_TYPE_TG_F01", DictCodeListExt1.金融产品类型_投管用_债券.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F02", DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F03", DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        model.addAttribute("FINPROD_TYPE_TG_F10", DictCodeListExt1.金融产品类型_投管用_股票.getValue());
        model.addAttribute("FINPROD_TYPE_TG_ZYSHG", "ZYSHG");
        model.addAttribute("MY_SUP_TIME_TYPE_02", DictCodeListExt1.监督时分_委托监控.getValue());
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
		// 交易平台
		model.addAttribute("SHARES_TRADE_PLATFORM_02", DictCodeList.交易平台_竞价撮合平台.getValue());
		model.addAttribute("SHARES_TRADE_PLATFORM_03", DictCodeList.交易平台_固定收益平台.getValue());
		model.addAttribute("SHARES_TRADE_PLATFORM_04", DictCodeList.交易平台_大宗交易平台.getValue());
		model.addAttribute("SHARES_TRADE_PLATFORM_05", DictCodeList.交易平台_综合协议交易平台.getValue());
		// 如果是导入
		return super.init(model);
	}
    
	/**
	 * 点击预览时check <功能详细描述>
	 * 
	 * @param allDto
	 * @param result
	 * @return
	 * @see
	 * @since 1.0
	 */
	@ResponseBody
	@RequestMapping("check")
	public AjaxResult check(@RequestBody EtrExchangeTradeSaveRequest allDto, BindingResult result) {
		List<ValidMessage> messages = new ArrayList<>();
		List<EtrExchangeTradeSaveRequest> dtoList = allDto.getSelectedItem();
		for (int i = 0; i < dtoList.size(); i++) {
			ValidResult itemResult = ValidationUtil.validateBean(dtoList.get(i), ValidateGroupEdit.class);
			StringBuffer message = new StringBuffer();
			if (!StringUtil.isEmptyList(itemResult.getAllErrors())) {
				message.append(getMessage("ETR_E_0025", String.valueOf(i + 1)));
				for (ErrorMessage errorMessage : itemResult.getAllErrors()) {
					message.append("<br />");
					message.append(errorMessage.getMessage());
				}
				messages.add(new ValidMessage(message.toString()));
			}
			AjaxResult ajax = etrExchangeTradeCommon001Service.checkSave(dtoList.get(i),result);
			if (ajax != null && !StringUtil.isEmptyList(ajax.getErrorList())) {
				for (ValidMessage validMessage : ajax.getErrorList()) {
					messages.add(new ValidMessage("指令列表第" + (i + 1) + "行:" + validMessage.getMessage()));
				}
			}
		}
		if (messages.size() > 0) {
			AjaxResult errorRs = new AjaxResult();
			errorRs.setErrorList(messages);
			return errorRs;
		}
		// add by yushilong 可用现金软校验 20200713 start
		String confirmMsg = "";
		// 可用现金软校验
//		for (int i = 0; i < dtoList.size(); i++) {
//			if (StringUtil.equals(CacheUtil.getSysParam(BussinessConstant.ETR_CHECK_CASH_SWITCH),
//					DictCodeListExt1.是或否_是.getValue())
//					&& (StringUtil.equals(dtoList.get(i).getPs(), DictCodeList.交易方向_买入.getValue()))
//					&& dtoList.get(i).getProAvailableCash() != null && dtoList.get(i).getEntrustAmt() != null) {
//				if (dtoList.get(i).getEntrustAmt().compareTo(dtoList.get(i).getProAvailableCash()) == 1) {
//					confirmMsg += getMessage("ETR_E_0025", String.valueOf(i + 1));
//					confirmMsg += "<br />";
//					confirmMsg += dtoList.get(i).getPortfolioId() + this.getMessage("TRD_C_0002");
//				}
//			}
//		}
		if (StringUtil.isNotEmpty(confirmMsg)) {
			AjaxResult ajaxResult = new AjaxResult();
			ajaxResult.setData(confirmMsg);
			return ajaxResult;
		}
		// add by yushilong 可用现金软校验 20200713 end
		return commonCheck(result, dtoList.get(0).getOperateType());
	}
    
    @ResponseBody
    @RequestMapping("uploadFile")
    public AjaxResult uploadFile() {
        // 更新文件上传进度为100%
        progressBarService.removePercent(this.getPageId(), ProgressBarService.FILE_UPLOAD_ID);
        return new AjaxResult();
    }

/**
 * 保存/提交
 * 
 * @param allDto
 *            allDto
 * @param br
 *            br
 * @return 结果
 * @throws Exception
 *             异常
 */
@SuppressWarnings("deprecation")
@ResponseBody
@RequestMapping("save")
public AjaxResult save(@RequestBody EtrExchangeTradeSaveRequest allDto, BindingResult br) throws Exception {
	 AjaxResult ajaxResult = this.check(allDto, br);
     if (!StringUtil.isEmptyList(ajaxResult.getErrorList())) {
         return ajaxResult;
     }
	 List<EtrExchangeTradeSaveRequest> dtoList = allDto.getSelectedItem();
	 for(EtrExchangeTradeSaveRequest dto : dtoList){
			// 业务类型
			if (dto.getFinprodType2().equals("ZYSHG")) {
				if (dto.getPs().equals(DictCodeListExt3.交易方向_正回购.getValue())) {
					dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue());
				} else if (dto.getPs().equals(DictCodeListExt3.交易方向_逆回购.getValue())) {
					dto.setFinprodType2(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue());
				}
			}
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
		//add start
		if(dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue()) && dto.getTradePlat().equals(DictCodeList.交易平台_竞价撮合平台.getValue())){
			if(StringUtils.equals(dto.getPs(), DictCodeList.交易方向_行权_回售.getValue())){
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_债券回售指令.getValue());
				businessType = DictCodeListExt3.共通业务类型_债券回售指令.getValue();
			}else if(StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_配债.getValue())){
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_配债指令.getValue());
				businessType = DictCodeListExt3.共通业务类型_配债指令.getValue();
			}else if(StringUtils.equals(dto.getPs(), DictCodeList.交易方向_申购.getValue())){
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_可转债申购指令.getValue());
				businessType = DictCodeListExt3.共通业务类型_可转债申购指令.getValue();
			}else if(StringUtils.equals(dto.getPs(), DictCodeListExt1.交易方向_债转股.getValue())) {
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_债转股指令.getValue());
				businessType = DictCodeListExt3.共通业务类型_债转股指令.getValue();
			} else if(StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_质押券入库.getValue()) || StringUtils.equals(dto.getPs(), DictCodeListExt3.交易方向_质押券出库.getValue())){
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_标准券出入库指令.getValue());
				businessType = DictCodeListExt3.共通业务类型_标准券出入库指令.getValue();
			}else{
				dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_债券竞价指令.getValue());
				businessType = DictCodeListExt3.共通业务类型_债券竞价指令.getValue();
			}
		}
		// 债券质押式回购竞价指令
		if ((dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
				|| dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue()) || "ZYSHG".equals(dto.getFinprodType2()))
				&& dto.getTradePlat().equals(DictCodeList.交易平台_竞价撮合平台.getValue())) {
			dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_债券质押式回购竞价指令.getValue());
			businessType = DictCodeListExt3.共通业务类型_债券质押式回购竞价指令.getValue();
		}
		// 大宗债券质押式回购指令
		if ((dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue())
				|| dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue()))
				&& dto.getTradePlat().equals(DictCodeList.交易平台_大宗交易平台.getValue())) {
			dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_大宗债券质押式回购指令.getValue());
			businessType = DictCodeListExt3.共通业务类型_大宗债券质押式回购指令.getValue();
		}
		if(dto.getFinprodType2().equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue()) && dto.getTradePlat().equals(DictCodeList.交易平台_固定收益平台.getValue())){
			dto.setEntrustBussType(DictCodeListExt3.上农商_指令业务类型_上交所固定收益平台现券交易指令.getValue());
			businessType = DictCodeListExt3.共通业务类型_上交所固定收益平台现券交易指令.getValue();
		}
		//add end
		if (!StringUtil.isNotEmpty(dto.getEntrustId())) {
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
			dto.setFromEtrExchange("Y");
			//add end
			String code = bussGetBussinessIdService != null ? bussGetBussinessIdService.buildBussinessCode(param) : IDGenerator.generate();
			dto.setEntrustId(code);
			dto.setInvManagerId(LoginUserUtil.getUserName());// 投资经理
			dto.setCreateUser(LoginUserUtil.getUserName());// 用户名
			dto.setCreateDept(LoginUserUtil.getDeptCode());// 用户部门
			dto.setCreateTime(new Date());// 创建时间
			dto.setBusinessTypeName(businessType);//此处存的是类型
			dto.setOptType(ENTRUST_RECORD_NEW);
			dto.setSoftSkipMode(allDto.getSoftSkipMode());
		}
		/*
		 * if 画面.金融产品类型（投管）.股票 and 画面.交易平台 = 数据字典.交易平台.竞价交易平台 指令业务类型 =
		 * 数据字典.共通业务类型.股票竞价指令
		 *
		 * if 画面.金融产品类型（投管）.基金 and 画面.交易平台 = 数据字典.交易平台.竞价交易平台 指令业务类型 =
		 * 数据字典.共通业务类型.基金竞价指令
		 *
		 * if 画面.委托方向 = （数据字典.交易方向.正回购 or 逆回购） and 画面.交易平台 = 数据字典.交易平台.大宗交易平台
		 * 指令业务类型 = 数据字典.共通业务类型.大宗债券质押式回购指令
		 *
		 * if 画面.金融产品类型（投管）.股票 and 画面.交易平台 = 数据字典.交易平台.大宗交易平台 指令业务类型 =
		 * 数据字典.共通业务类型.大宗股票指令
		 *
		 * if 画面.金融产品类型（投管）.基金 and 画面.交易平台 = 数据字典.交易平台.大宗交易平台 指令业务类型 =
		 * 数据字典.共通业务类型.大宗基金指令
		 *
		 * if 画面.金融产品类型（投管）.债券and 画面.交易平台 = 数据字典.交易平台.大宗交易平台 指令业务类型 =
		 * 数据字典.共通业务类型.大宗债券指令
		 */
		 //导入兼容
		 EtrExchangeTradePortfolioRequest portfolioRequest = new EtrExchangeTradePortfolioRequest();
		// dto.setSoftSkipMode("Y");
		 BeanUtil.copyPropertiesIgnoreNull(portfolioRequest, dto);
		 List<EtrExchangeTradePortfolioRequest> portfolioList = Lists.newArrayList();
		 portfolioList.add(portfolioRequest);
		 dto.setPortfolioList(portfolioList);

        }
		// add by zhangrui1 start
		AjaxResult ar = ExchangeEntrustRdsCommonAop.doListPreOccupy("EtrExchangeTradeMulti001", dtoList,
			BussinessDictCodeListRds.接口系统ID_预占批量接口.getValue());
		if (null != ar) {
			return ar;
		}
	    ExchangeEntrustRdsCommonAop.riskListToChildren(allDto.getRiskTrialRetDto(), dtoList,
			(dto, list) -> dto.setRiskTrialRetDto(list));
		// add by zhangrui1 end
		ProcessParameter<EtrExchangeTradeSaveRequest> processParameter = new ProcessParameter<EtrExchangeTradeSaveRequest>();
		WorkFlowParameter<EtrExchangeTradeSaveRequest> params = etrExchangeTradeCommon001Service.getWorkFlowParameter(allDto);
		params.setPageId("EtrExchangeTradeCommon004");
		params.setElementId("EtrExchangeImport_btn_submit");
		params.setBusinessType(item->item.getBusinessTypeName());
		params.setBusinessCode(item->item.getEntrustId());
		params.setComment(entity -> getMessage("EtrExchangeTrade003.entrustId") + "(" + entity.getEntrustId() + ")"
				+ "-" + "业务类型"+ "(" + SysDataUtil.getNameFromDataDict(DictCode.共通业务类型, entity.getBusinessTypeName()) + ")-"
				+ "平台日" + "(" + DateUtil.format(iPlatformDate.getSysDate()) + ")");
		processParameter.setWorkFlowParameter(params);
	   ajaxResult =new AjaxResult();
	   String msg="";
		try{
		    msg=etrExchangeTradeCommon001Service.start(processParameter, dtoList.toArray(new EtrExchangeTradeSaveRequest[dtoList.size()]));
		} catch (PositionException e) {
			Message message = PositionUtil.ajaxErrorReturn(e);
			if (StringUtil.isNotBlank(message.getMessage())) {
				WorkFlowUtil.showMessage(message.getMessage(), MessageType.错误消息);
			} else {
				if (!StringUtil.equals("1", RequestUtil.getParam("confirmOk"))) {
					BaseMap bm = (BaseMap) message.getData();
					StringBuffer messageStr = (StringBuffer) bm.get("message");
					ajaxResult.setMessage(messageStr.toString(), MessageType.确认消息);
					return ajaxResult;
				}

			}
		}
	     return new AjaxResult(msg);
	}
}
