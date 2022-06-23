/*
 * 文件名：CePreOccupancyManagement002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：dongzhiling
 * 修改时间：2019年1月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.risk.creditmanagement.preoccupancymanagement.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.base.intf.IDictCode;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.select.TreeNode;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.risk.creditmanagement.ceorg.dto.CreditMstInfoDto;
import com.joyintech.tams.invest.risk.creditmanagement.preoccupancymanagement.dto.CePreOccupancyManagementDto;
import com.joyintech.tams.invest.risk.creditmanagement.preoccupancymanagement.service.CePreOccupancyManagement002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * 〈一句话功能简述〉
 * 预占用管理新建
 * @author 裴家俊
 * @version 1.0 2019年1月24日
 * @see CePreOccupancyManagement002Controller
 * @since 1.0
 */
@Controller
@PageId("CePreOccupancyManagement002")
@RequestMapping("/invest/risk/creditmanagement/preoccupancymanagement/CePreOccupancyManagement002")
public class CePreOccupancyManagement002Controller extends BaseController{
    
	 @Autowired(required = false)
	 private BaseGetBusinessId baseGetBusinessId;
	  /**
     * cePreOccupancyManagement002Service
     */
    @Autowired(required = false)
    private CePreOccupancyManagement002Service cePreOccupancyManagement002Service;
    
    @Autowired 
    private FileAttachmentProcess fileAttachmentProcess;
    /**
     * 初始化的查询条件
     */
    private static final IDictCode[] DICT_CODES = new DictCode[] {DictCode.指标类型,
        DictCode.监督类型, DictCode.运算符, DictCode.连接符,DictCode.操作符,
        DictCode.单位, DictCode.监督时分, DictCode.监督频率, DictCode.组合层次,
        DictCode.分类维度,DictCode.申万行业一级类型
       };

    @Override
    public String init(Model model) {
        setSelectOptionToModel(model, DICT_CODES);
       
        String workflowId = getParam("workflowId");
       // Page<CreditMstInfoDto> page = cePreOccupancyManagement002Service.getTreeCeOrgQuote(RequestUtil.getParameters(getRequest()));
        // 待办跳转
        if (!WorkFlowUtil.isNotFromToDoList()&&StringUtil.isNotEmpty(workflowId)) {
            model.addAttribute("workflowId", workflowId);
            model.addAttribute("isTodoList", getParam("isTodoList"));
            CePreOccupancyManagementDto creditLinesOrgDto = cePreOccupancyManagement002Service.getBussinessEntityByWorkflowId(workflowId);
            model.addAttribute("entity", creditLinesOrgDto);
        } else if (WorkFlowUtil.isNotFromToDoList() &&StringUtil.isNotEmpty(this.getParam("seqno"))){
            String seqno = this.getParam("seqno");
            CePreOccupancyManagementDto creditLinesOrgDto = cePreOccupancyManagement002Service.getBussinessEntityByIndId(seqno);
            model.addAttribute(GlobalConstant.ENTITY, creditLinesOrgDto);
        }
        //model.addAttribute("creditQuoteItem", page.getRoot());
        return super.init(model);
    }
  
    /**
     * 获取组合树结构数据
     * @return 画面元素
     * @throws Exception Exception
     * @since 1.0
     */
    @SuppressWarnings("rawtypes")
	@ResponseBody
    @RequestMapping("/getAllCeOrgQuote")
    public List<TreeNode> getAllCeOrgQuote(@RequestParam(name="type", required=false) String type,@RequestParam(name="indId", required=false) String indId)
    {
        return cePreOccupancyManagement002Service.getAllCeOrgQuote(type,indId);
        
    }
    
    /**
     * 获取组合树结构数据
     * @return 画面元素
     * @throws Exception Exception
     * @since 1.0
     */
	@ResponseBody
    @RequestMapping("/getTreeCeOrgQuote")
    public Page<CreditMstInfoDto> getTreeCeOrgQuote()
    {
		Page<CreditMstInfoDto> page = cePreOccupancyManagement002Service.getTreeCeOrgQuote(RequestUtil.getParameters(getRequest()));
		String workflowId = getParam("workflowId");
		// 待办跳转
        if (!WorkFlowUtil.isNotFromToDoList()&&StringUtil.isNotEmpty(workflowId)) {
        	CePreOccupancyManagementDto creditLinesOrgDto = cePreOccupancyManagement002Service.getBussinessEntityByWorkflowId(workflowId);
            if(StringUtil.isNotEmpty(creditLinesOrgDto.getCreditQuoteItem())){
	            creditLinesOrgDto.getCreditQuoteItem().forEach(item->{
	            	List<CreditMstInfoDto> mstEntry=page.getRoot().stream().filter( itemtree ->StringUtil.isEmpty(item.getCreditMstId()) || StringUtil.equals(item.getCreditMstId(), itemtree.getCreditMstId())).collect(Collectors.toList());
	            	if(mstEntry.size()>0){
	            		mstEntry.get(0).setSeqno(item.getSeqno());
	            		mstEntry.get(0).setCreditAmt(item.getCreditAmt());
	            		mstEntry.get(0).setCreditAmtInit(item.getCreditAmt());
	            	}
	            });
            }
        } else if (WorkFlowUtil.isNotFromToDoList() &&StringUtil.isNotEmpty(this.getParam("seqno"))){
            String seqno = this.getParam("seqno");
            CePreOccupancyManagementDto creditLinesOrgDto = cePreOccupancyManagement002Service.getBussinessEntityByIndId(seqno);
            if(StringUtil.isNotEmpty(creditLinesOrgDto.getCreditQuoteItem())){
            	creditLinesOrgDto.getCreditQuoteItem().forEach(item->{
	            	List<CreditMstInfoDto> mstEntry=page.getRoot().stream().filter( itemtree ->StringUtil.isEmpty(item.getCreditMstId()) || StringUtil.equals(item.getCreditMstId(), itemtree.getCreditMstId())).collect(Collectors.toList());
	            	if(mstEntry.size()>0){
	            		mstEntry.get(0).setSeqno(item.getSeqno());
	            		mstEntry.get(0).setCreditAmt(item.getCreditAmt());
	            		mstEntry.get(0).setCreditAmtInit(item.getCreditAmt());
	            	}
            	});
            }
        }
		return page;
        
    }
    
    /**
     * 保存按钮点击时的check
     * @return check结果
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupEdit.class}) CePreOccupancyManagementDto dto, BindingResult result ) {
        return commonCheck(result, dto.getOperateType());
    }
    
    /**
     * 保存
     * @param dto
     * @throws Exception
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping(PAGE_SAVE)
    public AjaxResult save(@RequestBody CePreOccupancyManagementDto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        if(StringUtil.equals(dto.getPageModel(), GlobalConstant.DETAIL_PAGE_MODEL_NEW)){
       	 if (baseGetBusinessId != null) {
                BussinessCodeParameter codeParameter = new BussinessCodeParameter();
                codeParameter.setCommonBusinessType(DictCodeList.共通业务类型_预占用管理);
                dto.setSeqno(baseGetBusinessId.buildBussinessCode(codeParameter));
            }
            else {
                dto.setSeqno(IDGenerator.generate());
            }
       }
        //文件信息
        if (CollectionUtils.isNotEmpty(dto.getUploadFileList())
            && StringUtil.isNotEmpty(dto.getSeqno())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                dto.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
                    Collectors.toList()),
                dto.getSeqno());
        }
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_预占用管理);
        ProcessParameter<CePreOccupancyManagementDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<CePreOccupancyManagementDto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        // 授权代码作为业务编号
        String businessCode = dto.getSeqno();
        params.setBusinessCode(businessCode);
        params.setBusinessType(DictCodeList.共通业务类型_预占用管理.getValue());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> {
     	   return WorkFlowUtil.concatComment(getMessage("CePreOccupancyManagement002.seqno"),
                    GlobalConstant.OPEN_PARENTHESIS, entity.getSeqno(), GlobalConstant.CLOSE_PARENTHESIS,
                    getMessage("CePreOccupancyManagement002.customerId"), GlobalConstant.OPEN_PARENTHESIS,
                    entity.getCustomerId(), GlobalConstant.CLOSE_PARENTHESIS);
     });
        processParameter.setWorkFlowParameter(params);
        String msg = cePreOccupancyManagement002Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }
}
