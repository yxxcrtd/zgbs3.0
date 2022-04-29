/*
 * 文件名：ValuationMethodScheme003Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年8月28日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.joyintech.tams.constant.ModelDictCodeList;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.collect.Maps;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.dto.SelectPageOptionDto;
import com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.dto.ValuationMethodScheme001Dto;
import com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.dto.ValuationMethodScheme003Dto;
import com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.service.ValuationMethodScheme001Service;
import com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.service.ValuationMethodScheme00301Service;
import com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.service.ValuationMethodScheme003Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 估值方案管理-左侧
 * 
 * @author zhangwei
 * @version 1.0 2018年8月28日
 * @see ValuationMethodScheme003Controller
 * @since 1.0
 */
@Controller
@PageId("ValuationMethodScheme003")
@RequestMapping("/valuation/valuationparametermanage/valuationmethodscheme/ValuationMethodScheme003")
public class ValuationMethodScheme003Controller extends BaseController {

	@Autowired
	private ValuationMethodScheme003Service valuationMethodScheme003Service;

	@Autowired
	private ValuationMethodScheme001Service valuationMethodScheme001Service;
	
	/** modify by dongjun on 20181218 start**/
	@Autowired
	private ValuationMethodScheme00301Service valuationMethodScheme00301Service;
    /** modify by dongjun on 20181218 end**/
	
	/**
	 * 页面初始化 {@inheritDoc}
	 */
	@Override
	public String init(Model model) {
		String schemeDetailId = getParam("schemeDetailId");
		String schemeId = getParam("schemeId");
		String operateType = getParam("operateType");
		String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);// 工作流ID
		ValuationMethodScheme003Dto dto = new ValuationMethodScheme003Dto();
		
		if (StringUtils.isNotEmpty(schemeDetailId)) {// 新建
			dto = valuationMethodScheme003Service.findBySchemeDetailId(schemeDetailId);
		}else if(StringUtil.isNotEmpty(workflowId)) { // 流程查看页面
		    dto = valuationMethodScheme003Service.getBussinessEntityByWorkflowId(workflowId);
		    
		    schemeDetailId = dto.getSchemeDetailId(); // 避免流程，再生成主键
		}
//		if (StringUtils.isEmpty(schemeDetailId)) {// 新建  自动生成 主键
//			dto.setSchemeDetailId(IDGenerator.generate());
//			dto.setSchemeId(schemeId);
//		}
//		if ("copy".equals(operateType)) {// 复制 自动生成 主键
//			dto.setSchemeDetailId(IDGenerator.generate());
//		}
		if (StringUtils.isEmpty(schemeDetailId) || "copy".equals(operateType)) {
			dto.setSchemeDetailId(IDGenerator.generate());// 新建 和 复制 自动生成 主键
			dto.setSchemeId(schemeId);
		}
		dto.setOperateType(operateType);
		model.addAttribute("entity", dto);

//		super.setSelectOptionToModel(model, DictCode.是否);

		super.setSelectOptionToModel(model, DictCode.估值交易市场);// 交易市场
		super.setSelectOptionToModel(model, DictCode.证券品种);// 债券分类
		super.setSelectOptionToModel(model, DictCode.债券类型);// 债券细分类（需求要求）
		super.setSelectOptionToModel(model, DictCode.投资目的);
		super.setSelectOptionToModel(model, DictCode.估值方法);
		super.setSelectOptionToModel(model, DictCode.估值市价); // 第一估值市价 和 第二估值市价
		super.setSelectOptionToModel(model, DictCode.估值日期);
		super.setSelectOptionToModel(model, DictCode.估值舍入规则);
		super.setSelectOptionToModel(model, DictCode.价格类别);
		
		 
		super.setSelectOptionToModel(model, DictCode.估值来源);
		super.setSelectOptionToModel(model, DictCode.计划类资产类型);
		super.setSelectOptionToModel(model, DictCode.利率互换估值模型);

		return super.init(model);
	}

	/**
	 * 下拉框 ： 方案
	 * 
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("findScheme")
	public List<SelectOptionDto> findScheme() {
		return valuationMethodScheme001Service.findScheme(RequestUtil.getPageParam());
	}

	@ResponseBody
	@RequestMapping("check")
	public AjaxResult check(@RequestBody @Validated({ValidateGroupEdit.class}) ValuationMethodScheme003Dto dto, BindingResult bindingResult) {
//        List<ValuationMethodScheme003Dto> list = listdto.getListValuationMethodSchemeDto();
//        ValuationMethodScheme003Dto dto = list.get(0);
		String msg = StringUtil.EMPTY;
		String operateType = dto.getOperateType();
		
		if(StringUtils.isEmpty(dto.getSchemeDetailId())) {
			bindingResult.rejectValue("schemeDetailId", "NotBlank", "方案明细代码不能为空！");
		}
		
		if(StringUtils.isEmpty(dto.getSchemeId())) {
			bindingResult.rejectValue("schemeId", "NotBlank", "方案代码不能为空！");
		}
		
		if(StringUtils.isEmpty(dto.getSecType())) {
			bindingResult.rejectValue("secType", "NotBlank", "证券品种不能为空！");
		}
		
		if(StringUtils.isEmpty(dto.getValuationMethod())) {
			bindingResult.rejectValue("valuationMethod", "NotBlank", "估值方法不能为空！");
		}
		
		if(StringUtils.isEmpty(dto.getVdate())) {
			bindingResult.rejectValue("vdate", "NotBlank", "开始日不能为空！");
		}

		if(DictCodeListExt1.证券品种_债券.getValue().equals(dto.getSecType())
				|| DictCodeListExt1.证券品种_计划类资产_收益型.getValue().equals(dto.getSecType())) {
			if(StringUtils.isEmpty(dto.getInvAim())) {
				bindingResult.rejectValue("invAim", "NotBlank", "投资目的不能为空！");
			}
		}
		
		if(DictCodeListExt1.证券品种_利率互换.getValue().equals(dto.getSecType())
				&& DictCodeListExt1.证券品种_外汇.getValue().equals(dto.getSecType())) {
			if(StringUtils.isEmpty(dto.getValuationSource())) {
				bindingResult.rejectValue("valuationSource", "NotBlank", "估值来源不能为空！");
			}
		}

		if(!DictCodeListExt1.证券品种_利率互换.getValue().equals(dto.getSecType())
				&& !DictCodeListExt1.证券品种_外汇.getValue().equals(dto.getSecType())
				&& !DictCodeListExt1.证券品种_换汇.getValue().equals(dto.getSecType())
				&& !DictCodeListExt1.证券品种_计划类资产_净值型.getValue().equals(dto.getSecType())
				&& !DictCodeListExt1.证券品种_计划类资产_货币型.getValue().equals(dto.getSecType())
				&& !DictCodeListExt1.证券品种_计划类资产_收益型.getValue().equals(dto.getSecType())) {
//			if(StringUtils.isEmpty(dto.getTradeMarket())) {
//				bindingResult.rejectValue("tradeMarket", "NotBlank", "交易场所不能为空！");
//			}
		}

		if (DictCodeListExt1.估值方法_市值成本孰低法.getValue().equals(dto.getValuationMethod())
				|| DictCodeListExt1.估值方法_市值法.getValue().equals(dto.getValuationMethod())) {
			if(DictCodeListExt1.证券品种_换汇.getValue().equals(dto.getSecType())) {
				if(StringUtils.isEmpty(dto.getValuationPrice())) {
					bindingResult.rejectValue("valuationPrice", "NotBlank", "估值第一市价不能为空！");
				}
			}else{
				if(StringUtils.isEmpty(dto.getValuationPrice())) {
					bindingResult.rejectValue("valuationPrice", "NotBlank", "估值第一市价不能为空！");
				}
				if(StringUtils.isEmpty(dto.getValuationDate())) {
					bindingResult.rejectValue("valuationDate", "NotBlank", "行情日期不能为空！");
				}
				if(dto.getQuotaDigit() == null) {
					bindingResult.rejectValue("quotaDigit", "NotBlank", "行情位数不能为空！");
				}
				if(StringUtils.isEmpty(dto.getPriceTailMode())) {
					bindingResult.rejectValue("priceTailMode", "NotBlank", "行情尾数处理方式不能为空！");
				}
				if(DictCodeListExt1.证券品种_债券.getValue().equals(dto.getSecType())){
				    if(DictCodeList.估值市价_收盘价.getValue().equals(dto.getValuationPrice())
				    || DictCodeList.估值市价_平均价.getValue().equals(dto.getValuationPrice())
				    || DictCodeList.估值市价_最高价.getValue().equals(dto.getValuationPrice())
				    || DictCodeList.估值市价_最低价.getValue().equals(dto.getValuationPrice())
				    || DictCodeList.估值市价_卖一价.getValue().equals(dto.getValuationPrice())
				    || DictCodeList.估值市价_买一价.getValue().equals(dto.getValuationPrice())
				    || DictCodeList.估值市价_收盘价.getValue().equals(dto.getValuationPrice2())
				    || DictCodeList.估值市价_平均价.getValue().equals(dto.getValuationPrice2())
				    || DictCodeList.估值市价_最高价.getValue().equals(dto.getValuationPrice2())
				    || DictCodeList.估值市价_最低价.getValue().equals(dto.getValuationPrice2())
				    || DictCodeList.估值市价_卖一价.getValue().equals(dto.getValuationPrice2())
				    || DictCodeList.估值市价_买一价.getValue().equals(dto.getValuationPrice2())){
	                    if(StringUtils.isEmpty(dto.getPriceClass())) {
	                        bindingResult.rejectValue("priceClass", "NotBlank", "价格类型不能为空！");
	                    }
				    }
				}
			}
			if(StringUtils.isNotEmpty(msg)) {
				return new AjaxResult(msg, MessageType.错误消息);
			}
		}

		//add  by yinya on 20220427 模型法必须选择估值模型
		if(dto.getValuationMethod().equals(DictCodeListExt1.估值方法_模型法.getValue())){
			if(StringUtils.isEmpty(dto.getValuationId())) {
				bindingResult.rejectValue("valuationId", "NotBlank", "估值模型不能为空！");
			}
			if(dto.getQuotaDigit() == null) {
				bindingResult.rejectValue("quotaDigit", "NotBlank", "行情位数不能为空！");
			}
			if(StringUtils.isEmpty(dto.getPriceTailMode())) {
				bindingResult.rejectValue("priceTailMode", "NotBlank", "行情尾数处理方式不能为空！");
			}
		}


		if (GlobalConstant.DETAIL_PAGE_MODEL_NEW.equals(dto.getPageModel()) && !bindingResult.hasErrors()) {// 新建唯一性验证：主键
			ValuationMethodScheme003Dto entity = valuationMethodScheme003Service
					.findBySchemeDetailId(dto.getSchemeDetailId());
			if (entity != null) {
				msg = "方案明细代码已存在！";
				return new AjaxResult(msg, MessageType.错误消息);
			}
		}

		if (!bindingResult.hasErrors()) {// 新建、修改都有验证
//			唯一性验证：证券品种、交易市场、证券细分类、投资目的和开始日五者相同就违背数据的唯一性(同一方案代码下)
			Map<String, Object> params = Maps.newHashMap();
			params.put("schemeId", dto.getSchemeId());
			params.put("secType", dto.getSecType());
			params.put("tradeMarket", dto.getTradeMarket());
			params.put("secDetailType", dto.getSecDetailType());
			params.put("invAim", dto.getInvAim());
			params.put("vdate", dto.getVdate());
			if (GlobalConstant.DETAIL_PAGE_MODEL_EDIT.equals(dto.getPageModel())) {// 修改时忽略本身
				params.put("owner", dto.getSchemeDetailId());
			}
			List<ValuationMethodScheme001Dto> list1 = valuationMethodScheme001Service.findSchemeDetailByPage(params)
					.getRoot();
			if (CollectionUtils.isNotEmpty(list1)) {
				msg = "该资产估值配置在同一开始日已经存在，不得重复配置！";
				return new AjaxResult(msg, MessageType.错误消息);
			}
		}

		return commonCheck(bindingResult, operateType);
	}

	/**
	 * 新建/修改, 并启动流程
	 * @param dto
	 * @param bindingResult
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = PAGE_SAVE, method = RequestMethod.POST)
	public AjaxResult save(@RequestBody ValuationMethodScheme003Dto dto, BindingResult bindingResult) {
//        List<ValuationMethodScheme003Dto> list = listdto.getListValuationMethodSchemeDto();
//        ValuationMethodScheme003Dto dto = list.get(0);
        
	    String msg = StringUtil.EMPTY;

		ProcessParameter<ValuationMethodScheme003Dto> processParameter = new ProcessParameter<>();
		WorkFlowParameter<ValuationMethodScheme003Dto> workFlowParameter = new WorkFlowParameter<>();
		
		try {
		    workFlowParameter.setPageId(this.getPageId()); // 页面id
            workFlowParameter.setElementId(this.getParam(GlobalConstant.ELEMENT_ID)); // 元素id
            workFlowParameter.setBusinessCode(dto.getSchemeId());// 业务代码编号
            workFlowParameter.setBusinessType(DictCodeList.共通业务类型_估值方法方案.getValue());// 业务类型
            workFlowParameter.setComment(entity -> WorkFlowUtil.concatComment(entity.getSchemeDetailId(),
                entity.getSchemeId(), "一般资产估值配置"));// 当前工作流备注
            processParameter.setWorkFlowParameter(workFlowParameter);// 塞入流程参数
            
            valuationMethodScheme003Service.start(processParameter, dto);
            msg = getMessage("COMMON_I_0001"); // 处理正常结束
		} catch (Exception e) {
			this.getLogger().error("启动估值方案流程失败：", e);
            msg = e.getMessage();
		}
		
		return new AjaxResult(msg);
	}

	/**
	 * 联动：证券品种 - 交易流通场所 获得交易市场
	 *
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("getTradeMarket")
	public List<SelectOptionDto> getTradeMarket() {
		Map<String, Object> param = RequestUtil.getPageParam();
		param.put("dict_code_parent", DictCode.证券品种.getValue());
		param.put("dict_code_child", DictCode.估值交易市场.getValue());
		param.put("item_code_parent", param.get("secType"));
		return valuationMethodScheme001Service.getLinkSelectOption(param);
	}
	
	/**
	 * 联动：证券品种 - 获得投资目的
	 * 
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("getInvAim")
	public List<SelectOptionDto> getInvAim() {
		Map<String, Object> param = RequestUtil.getPageParam();
		param.put("dict_code_parent", DictCode.证券品种.getValue());
		param.put("dict_code_child", DictCode.投资目的.getValue());
		param.put("item_code_parent", param.get("secType"));
		return valuationMethodScheme001Service.getLinkSelectOption(param);
	}

	/**
	 * 联动：交易市场 - 证券明细分类 获得证券明细分类
	 * 
	 * @return
	 * @throws Exception
	 */
    /** modify by dongjun on 20181218 start**/
	@ResponseBody
	@RequestMapping("getSecDetailType")
	public Page<SelectPageOptionDto> getSecDetailType() {
		Map<String, Object> param = RequestUtil.getPageParam();
		param.put("dict_code_parent", DictCode.估值交易市场.getValue());
		param.put("dict_code_child", DictCode.债券类型.getValue());
		param.put("item_code_parent", param.get("tradeMarket"));
		return valuationMethodScheme00301Service.getLinkSelectOption(param);
	}
    /** modify by dongjun on 20181218 end**/
	
	/**
	 * 联动：证券品种 - 估值方法 获得估值方法
	 * 
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("getValuationMethod")
	public List<SelectOptionDto> getValuationMethod(){
		List<SelectOptionDto> linkSelectOption = new ArrayList<>();
		Map<String, Object> param = RequestUtil.getPageParam();
		if(StringUtil.isNotEmpty(param.get("invAim")) && StringUtil.isNotEmpty(param.get("secType"))){
			//如果估值方法需要根据证券品种和投资目的共同联动的，故使用配置不方便，直接后台处理
			linkSelectOption = valuationMethodScheme003Service.getResultByInvAimAndSecType(linkSelectOption,param);
		}else{
			////估值方法根据投资目的联动
			if(StringUtil.isNotEmpty(param.get("invAim"))){
				param.put("dict_code_parent", DictCode.投资目的.getValue());
				param.put("dict_code_child", DictCode.估值方法.getValue());
				param.put("item_code_parent", param.get("invAim"));
				linkSelectOption =valuationMethodScheme003Service.getLinkSelectOption(param);
			}

			//估值方法根据证券品种联动
			//当证券品种为01债券时，使用投资目的进行联动
			if(StringUtil.isNotEmpty(param.get("secType")) ){
				param.put("dict_code_parent", DictCode.证券品种.getValue());
				param.put("dict_code_child", DictCode.估值方法.getValue());
				param.put("item_code_parent", param.get("secType"));
				linkSelectOption =valuationMethodScheme003Service.getLinkSelectOption(param);
			}
		}
		//投资目的联动处理end
		return linkSelectOption;
	}

	/**
	 * 联动：证券品种/交易市场 - 估值市价 获得估值市价
	 * 
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("getValuationPrice")
	public List<SelectOptionDto> getValuationPrice() {
		List<SelectOptionDto> selectOptionDtoList = new ArrayList<>();
		Map<String, Object> param = RequestUtil.getPageParam();
		String tradeMarket = (String) param.get("tradeMarket");
		String secType = (String)param.get("secType");
		//但证券品种为基金时，且交易场所为场外，市价来源特殊处理为 管理人报价07
		if(StringUtil.isNotEmpty(secType) ){
			if(DictCodeListExt1.证券品种_基金.getValue().equals(secType) && StringUtil.isNotEmpty(tradeMarket) &&
					StringUtil.equals(tradeMarket,DictCodeListExt1.估值交易市场_场外.getValue())){
				//管理人报价07
				SelectOptionDto selectOptionDto = new SelectOptionDto();
				selectOptionDto.setId(DictCodeList.估值市价_管理人报价.getValue());
				selectOptionDto.setText(DictCodeList.估值市价_管理人报价.getCodeName());
				selectOptionDtoList.add(selectOptionDto);
				return selectOptionDtoList;
			}
			param.put("dict_code_parent", DictCode.证券品种.getValue());
			param.put("dict_code_child", DictCode.估值市价.getValue());
			param.put("item_code_parent", param.get("secType"));
			selectOptionDtoList = valuationMethodScheme003Service.getLinkSelectOption(param);
		}
        return selectOptionDtoList;
	}

	/**
	 * 联动：证券品种 - 证券明细分类 获得证券明细分类
	 *
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("getSecDetailTypeBySecType")
	public Page<SelectPageOptionDto> getSecDetailTypeBySecType() throws Exception {
		Map<String, Object> param = RequestUtil.getPageParam();
		param.put("dict_code_parent", DictCode.证券品种.getValue());
		param.put("dict_code_child", DictCode.计划类资产类型.getValue());
		param.put("item_code_parent", param.get("secType"));
		return valuationMethodScheme00301Service.getLinkSelectOption(param);
	}

}
