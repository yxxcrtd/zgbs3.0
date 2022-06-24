/*
 * 文件名：ValNetPlanQuotation003Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年10月15日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.basedata.marketdata.netplanquotation.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.valuation.basedata.marketdata.netplanquotation.dto.ValNetPlanQuotation003Dto;
import com.joyintech.tams.valuation.basedata.marketdata.netplanquotation.service.ValNetPlanQuotation001Service;
import com.joyintech.tams.valuation.basedata.marketdata.netplanquotation.service.ValNetPlanQuotation003Service;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 计划类资产净值信息新建
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author lishicheng
 * @version 1.0 2018年10月15日
 * @see ValNetPlanQuotation003Controller
 * @since 1.0
 */
@Controller
@PageId("ValNetPlanQuotation003")
@RequestMapping("/valuation/basedata/marketdata/netplanquotation/ValNetPlanQuotation003")
public class ValNetPlanQuotation003Controller extends BaseController {
	
	@Autowired
	private ValNetPlanQuotation001Service valNetPlanQuotation001Service;
	
	@Autowired
	private ValNetPlanQuotation003Service valNetPlanQuotation003Service;
		
	@Override
	public String init(Model model) {
		this.setSelectOptionToModel(model, DictCode.录入方式);
		// 工作流ID
        String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);
		
		// 判断是修改操作还是新建操作
        String finprodId = getParam("finprodId");	
        //行情日期
      	String valueDate = this.getParam("valueDate");
      	model.addAttribute("finprodId", finprodId);
 	    model.addAttribute("valueDate",(DateUtil.format(DateUtil.parse(valueDate),"yyyy-MM-dd")));
        if (StringUtil.isNotEmpty(finprodId)) {
        	Map<String, Object> map = new HashMap<String, Object>();
			map.put("finprodId", finprodId);
			map.put("valueDate", valueDate);
        	ValNetPlanQuotation003Dto entity = valNetPlanQuotation003Service.findAllById(map);
            model.addAttribute("entity", entity);
        }
        else if(StringUtil.isNotEmpty(workflowId)){
        	ValNetPlanQuotation003Dto entity= valNetPlanQuotation003Service.getBussinessEntityByWorkflowId(workflowId);
            model.addAttribute("entity", entity);           
            model.addAttribute("workflowId", workflowId);                    
        }
        String editFlag = this.getParam("editFlag");
		model.addAttribute("editFlag", editFlag);
		
		return super.init(model);
	}
	
	   /**
     * 下拉框获取：资产
     * @return
     * @throws Exception 
     * @see
     * @since 1.0
     */
     @RequestMapping("/getFinprodId")
     @ResponseBody
     public List<SuggestDto> getFinprodId()
             {
        String param = super.getRequest().getParameter("param");
        return valNetPlanQuotation001Service.getFinprodId(param);
     }
     
     /**
      * 联动获取资产名称
      * <一句话功能简述>
      * <功能详细描述>
      * @return
      */
     @RequestMapping("getFinprodAbbr")
     @ResponseBody
     public AjaxResult getFinprodAbbr(){
         Map<String,Object> map = RequestUtil.getPageParam();
         String msg = valNetPlanQuotation003Service.getFinprodAbbr(map);
         return new AjaxResult(msg);
     }
	
     /**
      * 新建----计划类资产净值信息 ---走流程
      * <一句话功能简述>
      * <功能详细描述>
      * @param dto
      * @param br
      * @return
      */
     @ResponseBody
     @RequestMapping(value = PAGE_SAVE, method=RequestMethod.POST)
     public AjaxResult doSave(@RequestBody @Valid ValNetPlanQuotation003Dto dto, BindingResult br) {
         
         if(br.hasErrors()){
             return new AjaxResult(br);
         }        
         // 启动流程
         ProcessParameter<ValNetPlanQuotation003Dto> proParam = new ProcessParameter<>();
         WorkFlowParameter<ValNetPlanQuotation003Dto> params = new WorkFlowParameter<>();                 
         
         // 业务代码（资产代码）
         params.setBusinessCode(dto.getFinprodId());
         // 业务类型
          params.setBusinessType(DictCodeList.共通业务类型_计划类资产净值信息.getValue());
         // 页面id
         params.setPageId(this.getPageId());
         // 元素id
         params.setElementId(this.getParam(GlobalConstant.ELEMENT_ID));
         
         //设置流程业务说明, entity即传入的dto
         params.setComment(entity -> WorkFlowUtil.concatComment("计划类资产净值新建：",
             entity.getFinprodId(),//资产代码
             DateUtil.format(DateUtil.parse(entity.getValueDate()),"yyyy-MM-dd"),//估值日期
             entity.getFinprodType()//资产类型
             ));
         
         // 塞入流程参数
         proParam.setWorkFlowParameter(params);
         
         String msg = getMessage("COMMON_I_0001");
         try {
             msg = valNetPlanQuotation003Service.start(proParam, dto);
         }
         catch (Exception e) {
             getLogger().error("启动流程失败：", e);
             return new AjaxResult(e.getMessage());
         }

         return new AjaxResult(msg);
     }
     
     
 	/**
 	 * 新建
 	 * 
 	 * @param dto
 	 * @param bindingResult
 	 * @return
 	 */
/* 	@ResponseBody
 	@RequestMapping(value = PAGE_SAVE, method = RequestMethod.POST)
 	public AjaxResult save(@RequestBody @Valid ValNetPlanQuotation003Dto dto, BindingResult bindingResult) {
 		String msg = StringUtil.EMPTY;
 		if (StringUtils.isEmpty(dto.getFinprodId())) {
 			dto.setFinprodId(IDGenerator.generate());
 		}

 		String operateType = dto.getOperateType();
 		//dto.setValueDate(dto.getValueDateFmt());
 		
        dto.setFinprodType(DictCodeListExt1.金融产品类型_计划类资产.getValue());
 		if ("submit".equals(operateType)) {
 			valNetPlanQuotation003Service.insert(dto);
 			msg = "新建成功！";
 		} else if ("save".equals(operateType)) {
 			valNetPlanQuotation003Service.updateByPrimaryKeySelective(dto);
 			msg = "修改成功！";
 		}		

 		return new AjaxResult(msg);
 	}*/
 	
 	
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
	public AjaxResult check(@RequestBody @Valid ValNetPlanQuotation003Dto dto, BindingResult bindingResult)
			 {
		String msg = StringUtil.EMPTY;
		String operateType = dto.getOperateType();

//		 唯一性验证 资产代码、估值日期
		if (OPERATE_TYPE_SUBMIT.equals(dto.getOperateType()) && !bindingResult.hasErrors()) {// submit事件 && 基础验证通过
			//资产代码
			String finprodId = dto.getFinprodId();
		    //行情日期
			String valueDate = dto.getValueDate();
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("finprodId", finprodId);
			map.put("valueDate", valueDate);
			List<ValNetPlanQuotation003Dto> list = valNetPlanQuotation003Service.searchList(map);
			if (CollectionUtils.isNotEmpty(list)) {
				msg = " 资产代码或行情日期已存在，不允许提交！请重新输入。";
				return new AjaxResult(msg, MessageType.错误消息);
			}
			 //资产净值校验			
	        if( dto.getValueAsset()==null){
	        	msg = "资产净值不能为空";
				return new AjaxResult(msg, MessageType.错误消息);		
	        }
	        //资产代码输入验证
	        List<ValNetPlanQuotation003Dto> list1 = valNetPlanQuotation003Service.searchfinprodId(finprodId);
	    	if (CollectionUtils.isEmpty(list1)) {
				msg = " 资产代码输入有误！请重新输入。";
				return new AjaxResult(msg, MessageType.错误消息);
			}
	    	
	        
		}
		//修改save时校验
		if (OPERATE_TYPE_SAVE.equals(dto.getOperateType()) && !bindingResult.hasErrors()) {
			 //判断当前流程是否存在
			 ProcessMark processMark = WorkflowContext.getProcessMarkService().getParameterByAnnotation(dto);
			 List<String> resultList = valNetPlanQuotation001Service.getBusinessDataInFlow(processMark);
	        if(!StringUtil.isEmptyList(resultList)){
	            //流程存在情况
	        	return new AjaxResult("该条数据在流程中，不能修改", MessageType.错误消息);
	        }
	        //资产净值校验			
	        if( dto.getValueAsset()==null){
	        	msg = "资产净值不能为空";
				return new AjaxResult(msg, MessageType.错误消息);		
	        }
		}
		return commonCheck(bindingResult, operateType);
	}
	  /**
	   * 计划类资产净值信息---修改--走流程
	   * <一句话功能简述>
	   * <功能详细描述>
	   * @param dto
	   * @return
	   */
	    @ResponseBody
	    @RequestMapping(value = "doUpdate", method=RequestMethod.POST)
	    public AjaxResult doUpdate(@RequestBody @Valid ValNetPlanQuotation003Dto dto, BindingResult br) {
	         // 启动流程
	         ProcessParameter<ValNetPlanQuotation003Dto> proParam = new ProcessParameter<>();
	         WorkFlowParameter<ValNetPlanQuotation003Dto> params = new WorkFlowParameter<>();                 
	         
	         // 业务代码（资产代码）
	         params.setBusinessCode(dto.getFinprodId());
	         // 业务类型
	          params.setBusinessType(DictCodeList.共通业务类型_计划类资产净值信息.getValue());
	         // 页面id
	         params.setPageId(this.getPageId());
	         // 元素id
	         params.setElementId(this.getParam(GlobalConstant.ELEMENT_ID));
	        //设置流程业务说明, entity即传入的dto
	         params.setComment(entity -> WorkFlowUtil.concatComment("计划类资产净值信息修改：",
	                 entity.getFinprodId(),//资产代码
	                 DateUtil.format(DateUtil.parse(entity.getValueDate()),"yyyy-MM-dd"),//估值日期
	                 entity.getFinprodType()//资产类型
	                 ));
	        
	        proParam.setWorkFlowParameter(params);
	        
	        String msg = getMessage("COMMON_I_0001");
	        try {
	            msg = valNetPlanQuotation003Service.start(proParam, dto);
	        }
	        catch (Exception e) {
	            getLogger().error("启动流程失败：", e);
	            return new AjaxResult(e.getMessage());
	        }
	        return new AjaxResult(msg);
	    }
}
