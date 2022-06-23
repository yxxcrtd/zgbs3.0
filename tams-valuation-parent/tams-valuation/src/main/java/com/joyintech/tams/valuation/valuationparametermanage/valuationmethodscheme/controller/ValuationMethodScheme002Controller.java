/*
 * 文件名：ValuationMethodScheme002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangwei
 * 修改时间：2018年8月28日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
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
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.dto.ValuationMethodScheme002Dto;
import com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.service.ValuationMethodScheme001Service;
import com.joyintech.tams.valuation.valuationparametermanage.valuationmethodscheme.service.ValuationMethodScheme002Service;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 估值方案管理-左侧
 * 
 * @author zhangwei
 * @version 1.0 2018年8月28日
 * @see ValuationMethodScheme002Controller
 * @since 1.0
 */
@Controller
@PageId("ValuationMethodScheme002")
@RequestMapping("/valuation/valuationparametermanage/valuationmethodscheme/ValuationMethodScheme002")
public class ValuationMethodScheme002Controller extends BaseController {

	@Autowired
	private ValuationMethodScheme002Service valuationMethodScheme002Service;

	@Autowired
	private ValuationMethodScheme001Service valuationMethodScheme001Service;

	/**
	 * 页面初始化 {@inheritDoc}
	 */
	@Override
	public String init(Model model) {
		String schemeId = getParam("schemeId");
		String operateType = getParam("operateType");
		ValuationMethodScheme002Dto dto = new ValuationMethodScheme002Dto();
		
		
        String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);// 工作流ID
        
        // 操作判断
		if (StringUtils.isNotEmpty(schemeId)) { // 新建
			dto = valuationMethodScheme002Service.findBySchemeId(schemeId);
		} else if(StringUtil.isNotEmpty(workflowId)) { // 流程查看页面
		    dto = valuationMethodScheme002Service.getBussinessEntityByWorkflowId(workflowId);
		} 
		
        dto.setOldSchemeId(schemeId);
        dto.setOperateType(operateType);
		model.addAttribute("entity", dto);
		super.setSelectOptionToModel(model, DictCode.是否);
		return super.init(model);
	}

	/**
	 * 新增/修改验证：基础验证、唯一性
	 * 
	 * @param dto
	 * @param bindingResult
	 * @return
	 * @throws Exception
	 */
	@ResponseBody
	@RequestMapping("check")
	public AjaxResult check(@RequestBody @Validated({ValidateGroupEdit.class}) ValuationMethodScheme002Dto dto, BindingResult bindingResult)
			{
		String msg = StringUtil.EMPTY;
		String operateType = dto.getOperateType();

//		 唯一性验证：方案代码、方案名称
		if ( !bindingResult.hasErrors()) {// new 事件 && 基础验证通过
			Map<String, Object> params = Maps.newHashMap();
			params.put("schemeId", dto.getSchemeId());
			params.put("schemeName", dto.getSchemeName());
			if (GlobalConstant.DETAIL_PAGE_MODEL_EDIT.equals(dto.getPageModel())) {// 修改时忽略本身
                params.put("owner", dto.getSchemeId());
			}
			List<SelectOptionDto> list = valuationMethodScheme001Service.findScheme(params);
			if (CollectionUtils.isNotEmpty(list)) {
				msg = "方案代码或方案名称已存在！请重新输入。";
				return new AjaxResult(msg, MessageType.错误消息);
			}
			boolean isExistDefault = valuationMethodScheme001Service.getIsdefault();
			if (isExistDefault && DictCodeList.是否_是.getValue().equals(dto.getIsdefault())) {
				msg = "已存在默认估值方案！请重新选择。";
				return new AjaxResult(msg, MessageType.错误消息);
			}
		}

		return commonCheck(bindingResult, operateType);
	}

	/**
	 * 新建或修改 并启动流程
	 * @param dto
	 * @param bindingResult
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = PAGE_SAVE, method = RequestMethod.POST)
	public AjaxResult save(@RequestBody @Validated({ValidateGroupEdit.class}) ValuationMethodScheme002Dto dto, BindingResult bindingResult) {
	    String msg = StringUtil.EMPTY;
        if (StringUtils.isEmpty(dto.getSchemeId())) {
            dto.setSchemeId(IDGenerator.generate());
        }
        
		ProcessParameter<ValuationMethodScheme002Dto> processParameter = new ProcessParameter<>();
		WorkFlowParameter<ValuationMethodScheme002Dto> workFlowParameter = new WorkFlowParameter<>();
		
		try {
			workFlowParameter.setPageId(this.getPageId()); // 页面id
			workFlowParameter.setElementId(this.getParam(GlobalConstant.ELEMENT_ID)); // 元素id
			workFlowParameter.setBusinessCode(dto.getSchemeId());// 业务代码编号
			workFlowParameter.setBusinessType(DictCodeList.共通业务类型_估值方法方案.getValue());// 业务类型
			workFlowParameter.setComment(entity -> WorkFlowUtil.concatComment(entity.getSchemeName(),
					entity.getSchemeId(), entity.getSchemeDescription()));// 当前工作流备注
			processParameter.setWorkFlowParameter(workFlowParameter);// 塞入流程参数
			
			valuationMethodScheme002Service.start(processParameter, dto);
			msg = getMessage("COMMON_I_0001"); // 处理正常结束
		} catch (Exception e) {
			this.getLogger().error("启动估值方案流程失败：", e);
            msg = e.getMessage();
		}
		
		return new AjaxResult(msg);
	}


}
