/*
 * 文件名：ShadowPriceScheme001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.valuationparametermanage.shadowpricescheme.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.ExportUtil;
import com.joyintech.framework.util.FileNameUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.system.constant.SystemGlobalConstant;
import com.joyintech.tams.valuation.valuationparametermanage.shadowpricescheme.dto.ShadowPriceScheme001Dto;
import com.joyintech.tams.valuation.valuationparametermanage.shadowpricescheme.dto.ShadowPriceScheme002Dto;
import com.joyintech.tams.valuation.valuationparametermanage.shadowpricescheme.service.ShadowPriceScheme001Service;
import com.joyintech.tams.valuation.valuationparametermanage.shadowpricescheme.service.ShadowPriceScheme002Service;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 影子定价方案左侧controller
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author lishicheng
 * @version 1.0 2018年11月22日
 * @see ShadowPriceScheme001Controller
 * @since 1.0
 */
@Controller
@PageId("ShadowPriceScheme001")
@RequestMapping("/valuation/valuationparametermanage/shadowpricescheme/ShadowPriceScheme001")
public class ShadowPriceScheme001Controller extends BaseController {
	
		@Autowired
		private ShadowPriceScheme001Service shadowPriceScheme001Service;
		
		@Autowired
		private ShadowPriceScheme002Service shadowPriceScheme002Service;
	
	    @Override
	    public String init(Model model) {
	    	
	    	// 工作流ID
	        String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);
	        if(StringUtil.isNotEmpty(workflowId)){
			   model.addAttribute("workflowId", workflowId);
	        }
	        return super.init(model);
	    }
	    
	    /**
	     * 左侧影子方案查询
	     * <一句话功能简述>
	     * <功能详细描述>
	     * @return
	     * @throws Exception
	     */
	    @ResponseBody
		@RequestMapping("findShadowPrice")
		public Page<ShadowPriceScheme001Dto> findShadowPrice() {
			Page<ShadowPriceScheme001Dto> page = shadowPriceScheme001Service
					.findShadowPrice(RequestUtil.getPageParam());
			return page;		
		}
	    
	    /**
	     * 右侧影子方案明细查询
	     * <一句话功能简述>
	     * <功能详细描述>
	     * @return
	     * @throws Exception
	     */
        @ResponseBody
  		@RequestMapping("findShadowPriceDetial")
  		public Page<ShadowPriceScheme002Dto> findShadowPriceDetial() {
  			Page<ShadowPriceScheme002Dto> page = shadowPriceScheme002Service
  					.findShadowPriceDetial(RequestUtil.getPageParam());
  			return page;
  		}
	    
	    
		/**
		 * 左侧删除检验
		 * <一句话功能简述>
		 * <功能详细描述>
		 * @param dto
		 * @param result
		 * @return
		 * @throws Exception
		 */
		@ResponseBody
		@RequestMapping("check")
		public AjaxResult check(@RequestBody ShadowPriceScheme001Dto dto, BindingResult result) {
			Map<String, Object> params = Maps.newHashMap();
			params.put("schId", dto.getSchId());
			if (StringUtils.isNotEmpty(dto.getSchId())) {// 删除影子定价方案验证
				List<ShadowPriceScheme001Dto> list = shadowPriceScheme001Service.findScheme(params);
				if (CollectionUtils.isNotEmpty(list)) {
					return new AjaxResult("当前方案已被使用，不能删除！", MessageType.错误消息);
				}
			}
			 //判断当前流程是否存在
	        if(shadowPriceScheme001Service.checkBusinessDataInFlow(dto)){
	            //流程存在情况
	        	return new AjaxResult("该条数据在流程中，不能删除", MessageType.错误消息);
	        }
			return new AjaxResult();
		}
		
		/**
		 * 左侧影子方案删除
		 * <一句话功能简述>
		 * <功能详细描述>
		 * @param dto
		 * @return
		 */
/*		@ResponseBody
		@RequestMapping(value = "deleteShadowPriceSch", method = RequestMethod.POST)
		public AjaxResult deleteShadowPriceSch(@RequestBody ShadowPriceScheme001Dto dto) {
			String msg = StringUtils.EMPTY;

			int row = shadowPriceScheme001Service.deleteShadowPriceSch(dto.getSchId());
			if (row > 0) {
				msg = "删除成功";
				return new AjaxResult(msg);
			}
			msg = "删除失败";
			return new AjaxResult(msg, MessageType.错误消息);
		}*/
		
		/**
		 * 
		 * 左侧影子方案删除走流程
		 * <一句话功能简述>
		 * <功能详细描述>
		 * @param dto
		 * @param br
		 * @return
		 * @throws Exception
		 */
	    @ResponseBody
	    @RequestMapping(value = "deleteShadowPriceSch", method = RequestMethod.POST)
	    public AjaxResult deleteShadowPriceSch(@RequestBody @Valid ShadowPriceScheme001Dto dto, BindingResult br){
	        
	        if(br.hasErrors()){
	            return new AjaxResult(br);
	        }
	        
	        // 启动流程
	        ProcessParameter<ShadowPriceScheme001Dto> proParam = new ProcessParameter<>();
	        WorkFlowParameter<ShadowPriceScheme001Dto> params = new WorkFlowParameter<>();
	        
	        // 业务代码（方案代码）
	        params.setBusinessCode(dto.getSchId());
	        // 业务类型
	        params.setBusinessType(DictCodeList.共通业务类型_影子定价方案.getValue());
	        // 页面id
	        params.setPageId("ShadowPriceScheme001");
	        // 元素id
	        params.setElementId(this.getParam(GlobalConstant.ELEMENT_ID));
	        
	        //设置流程业务说明, entity即传入的dto
	        params.setComment(entity -> WorkFlowUtil.concatComment("影子定价方案删除：",
    		    entity.getSchId(),//方案明细代码
 	             entity.getCreateUser()//创建人
	            ));
	        
	        // 塞入流程参数
	        proParam.setWorkFlowParameter(params);
	        
	        String msg = getMessage("COMMON_I_0001");
	        try {
	            msg = shadowPriceScheme001Service.start(proParam, dto);
	        }
	        catch (Exception e) {
	            getLogger().error("启动流程失败：", e);
	            return new AjaxResult(e.getMessage());
	        }

	        return new AjaxResult(msg);
	    }
	    
	    /**
	     * 
	     * 左侧影子方案设为默认方案走流程
	     * @param dto
	     * @param br
	     * @return
	     * @throws Exception
	     */
	    @ResponseBody
	    @RequestMapping(value = "setDefaultScheme", method = RequestMethod.POST)
	    public AjaxResult setDefaultScheme(@RequestBody @Valid ShadowPriceScheme001Dto dto, BindingResult br) {
	        
	        if(br.hasErrors()){
	            return new AjaxResult(br);
	        }
	        // 查询全部信息
	        ShadowPriceScheme001Dto shadowPriceScheme001Dto = shadowPriceScheme001Service.selectSchemeBySchId(dto.getSchId());
	        // 是否默认设为是
	        shadowPriceScheme001Dto.setIsDefault(DictCodeList.是否_是.getValue());
	        // 操作类型赋值
	        shadowPriceScheme001Dto.setOperateType(dto.getOperateType());
            // 查询原默认方案
            ShadowPriceScheme001Dto defaultScheme = shadowPriceScheme001Service.getDefaultScheme();
            if(defaultScheme != null){
                // 原默认方案Id
                shadowPriceScheme001Dto.setOrgSchId(defaultScheme.getSchId());
            }
            
            //判断当前流程是否存在
//            ProcessMark processMark = WorkflowContext.getProcessMarkService().getParameterByAnnotation(shadowPriceScheme001Dto);
//            List<String> resultList = shadowPriceScheme001Service.getBusinessDataInFlow(processMark);
//            if(!StringUtil.isEmptyList(resultList)){
//               //流程存在情况
//               return new AjaxResult("该条数据在流程中，不能再次设为默认", MessageType.错误消息);
//            }
            
            boolean resultList = shadowPriceScheme001Service.checkBusinessDataInFlow(dto);
            if(resultList){
               //流程存在情况
               return new AjaxResult("该条数据在流程中，不能再次设为默认", MessageType.错误消息);
            }
            
	        // 启动流程
	        ProcessParameter<ShadowPriceScheme001Dto> proParam = new ProcessParameter<>();
	        WorkFlowParameter<ShadowPriceScheme001Dto> params = new WorkFlowParameter<>();
	        
	        // 业务代码（方案代码）
	        params.setBusinessCode(dto.getSchId());
	        // 业务类型
	        params.setBusinessType(DictCodeList.共通业务类型_影子定价方案.getValue());
	        // 页面id
	        params.setPageId("ShadowPriceScheme002");
	        // 元素id
	        params.setElementId(this.getParam(GlobalConstant.ELEMENT_ID));
	        //设置流程业务说明, entity即传入的dto
	        params.setComment(entity -> WorkFlowUtil.concatComment("影子定价原默认方案已修改为【非默认】,新的默认方案设为："+entity.getSchId(),
	            entity.getSchName(),//方案明细代码
	            entity.getCreateUser()//创建人
	            ));
	        
	        // 塞入流程参数
	        proParam.setWorkFlowParameter(params);
	        
	        String msg = getMessage("COMMON_I_0001");
	        try {
	            msg = shadowPriceScheme001Service.start(proParam, shadowPriceScheme001Dto);
	        }
	        catch (Exception e) {
	            getLogger().error("启动流程失败：", e);
	            return new AjaxResult(e.getMessage());
	        }
	        
	        return new AjaxResult(msg);
	    }
		
		
		/**
		 * 右侧删除检验
		 * <一句话功能简述>
		 * <功能详细描述>
		 * @param dto
		 * @param result
		 * @return
		 * @throws Exception
		 */
		@ResponseBody
		@RequestMapping("deleteCheck")
		public AjaxResult deleteCheck(@RequestBody @Validated({ValidateGroupDelete.class}) ShadowPriceScheme002Dto dto, BindingResult result){
//			 //判断当前流程是否存在
//			 ProcessMark processMark = WorkflowContext.getProcessMarkService().getParameterByAnnotation(dto);
//			 List<String> resultList = shadowPriceScheme001Service.getBusinessDataInFlow(processMark);
//	        if(!StringUtil.isEmptyList(resultList)){
//	            //流程存在情况
//	        	return new AjaxResult("该条数据在流程中，不能删除", MessageType.错误消息);
//	        }
//			return new AjaxResult();
			
			//判断当前流程是否存在
			 boolean resultList = shadowPriceScheme002Service.checkBusinessDataInFlow(dto);
	        if(resultList){
	            //流程存在情况
	        	return new AjaxResult("该条数据在流程中，不能删除", MessageType.错误消息);
	        }
			return new AjaxResult();
		}
		/**
		 * 影子方案明细批量删除
		 * <一句话功能简述>
		 * <功能详细描述>
		 * @param dto
		 * @return
		 */
	/*	@ResponseBody
		@RequestMapping(value = "deleteShadowPriceDetailBatch", method = RequestMethod.POST)
		public AjaxResult deleteShadowPriceDetailBatch(@RequestBody ShadowPriceScheme002Dto dto) {
			String msg = StringUtils.EMPTY;

			int row = shadowPriceScheme002Service.deleteShadowPriceDetailBatch(dto.getSchemeDetailId());
			if (row > 0) {
				msg = "批量删除成功";
				return new AjaxResult(msg);
			}
			msg = "批量删除失败";
			return new AjaxResult(msg, MessageType.错误消息);
		}*/
		
		/**
		 * 明细删除走流程
		 * <一句话功能简述>
		 * <功能详细描述>
		 * @param dto
		 * @param br
		 * @return
		 * @throws Exception
		 */
	    @ResponseBody
	    @RequestMapping(value = "deleteShadowPriceDetailBatch", method = RequestMethod.POST)
	    public AjaxResult delete(@RequestBody @Validated({ValidateGroupDelete.class}) ShadowPriceScheme002Dto dto, BindingResult br){
	        
	        if(br.hasErrors()){
	            return new AjaxResult(br);
	        }
	        
	        // 启动流程
	        ProcessParameter<ShadowPriceScheme002Dto> proParam = new ProcessParameter<>();
	        WorkFlowParameter<ShadowPriceScheme002Dto> params = new WorkFlowParameter<>();
	        
	        // 业务代码（方案代码）
	        params.setBusinessCode(dto.getSchemeDetailId());
	        // 业务类型
	        params.setBusinessType(DictCodeList.共通业务类型_影子定价方案.getValue());
	        // 页面id
	        params.setPageId("ShadowPriceScheme001");
	        // 元素id
	        params.setElementId(this.getParam(GlobalConstant.ELEMENT_ID));
	        
	        //设置流程业务说明, entity即传入的dto
	        params.setComment(entity -> WorkFlowUtil.concatComment("影子定价方案明细删除：",
    		    entity.getSchemeDetailId(),//方案明细代码
 	             entity.getCreateUser()//创建人
	            ));
	        
	        // 塞入流程参数
	        proParam.setWorkFlowParameter(params);
	        
	        String msg = getMessage("COMMON_I_0001");
	        try {
	            msg = shadowPriceScheme002Service.start(proParam, dto);
	        }
	        catch (Exception e) {
	            getLogger().error("启动流程失败：", e);
	            return new AjaxResult(e.getMessage());
	        }

	        return new AjaxResult(msg);
	    }
		
		/**
		 * 导出
		 * <一句话功能简述>
		 * <功能详细描述>
		 * @return
		 * @throws Exception
		 */
		@RequestMapping(ACTION_EXPORT)
		public ResponseEntity<byte[]> exportTypeAssets() throws IOException {
			Map<String, Object> paraMap = RequestUtil.getPageParam();
			String datas = this.getParam("datas");
			List<ShadowPriceScheme002Dto> listdto;
			if (datas == null) {
				listdto = shadowPriceScheme002Service.findShadowDetial(paraMap);
			} else {
				listdto = JsonUtils.json2List(datas, ShadowPriceScheme002Dto.class);
			}
			File outFile = shadowPriceScheme002Service.makeFile(
					"/com/joyintech/tams/valuation/valuationparametermanage/shadowpricescheme/config/export_shadowpricescheme.xml",
					listdto);
			return ExportUtil.export(this.getMessage(this.getPageId()) + SystemGlobalConstant.OPEN_PARENTHESIS
					+ DateUtil.format(new Date()) + SystemGlobalConstant.CLOSE_PARENTHESIS + SystemGlobalConstant.POINT
					+ FileNameUtil.getFileExtName(outFile), outFile, true);
		}

}
