package com.joyintech.tams.invest.product.rddesign.feasibilityanalysis.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.component.business.core.dto.WflValidInputParameter;
import com.joyintech.tams.component.business.core.service.MstWorkflowCommonService;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.product.rddesign.feasibilityanalysis.dto.FinProductFeaAna002Dto;
import com.joyintech.tams.invest.product.rddesign.feasibilityanalysis.service.FinProductFeaAna002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 可行性分析
 *
 * @author guyunfei
 * @version 1.0 2019年09月24日
 * @see FinProductFeaAna002Controller
 * @since 1.0
 */
@PageId("FinProductFeaAna002")
@Controller
@RequestMapping("/invest/product/rddesign/feasibilityanalysis/FinProductFeaAna002")
public class FinProductFeaAna002Controller extends BaseController {

    @Autowired
    private FinProductFeaAna002Service finProductFeaAna002Service;

    /**
     * 附件上传类
     */
    @Autowired
    private FileAttachmentProcess fileAttachmentProcess;

    /**
     * 流程组件
     */
    @Autowired(required = false)
    private MstWorkflowCommonService mstWorkflowCommonService;


    @Override
    public String init(Model model) {
        setSelectOptionToModel(model,DictCode.可行性状态,DictCode.可行需求分类);
        model.addAttribute("analyse_status",DictCodeListExt2.可行性状态_可行.getValue());

        String requirementId = this.getParam("requirementId");
        String workflowId = this.getParam("workflowId");
        if(WorkFlowUtil.isNotFromToDoList() && !StringUtil.isNotEmpty(requirementId)){
            FinProductFeaAna002Dto entity = new FinProductFeaAna002Dto();
            model.addAttribute("entity",entity);
        }else if(WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(requirementId)){
            FinProductFeaAna002Dto entity = finProductFeaAna002Service.queryByRequirementId(requirementId);
            model.addAttribute("entity",entity);
        }else if(!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)){
            FinProductFeaAna002Dto entity = finProductFeaAna002Service.getBussinessEntityByWorkflowId(workflowId);
            model.addAttribute("entity",entity);
        }
        return super.init(model);
    }

    /**
     * 可行性分析提交时验证
     * @param dto
     * @param br
     * @return
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated FinProductFeaAna002Dto dto, BindingResult br){
        if (br != null && br.hasErrors()) {
            return new AjaxResult(br);
        }
        return this.commonCheck(br, dto.getOperateType());
    }

    /**
     * 保存、提交
     * @param dto
     * @param br
     * @return
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody FinProductFeaAna002Dto dto,BindingResult br) throws WorkFlowException {
        if (CollectionUtils.isNotEmpty(dto.getUploadFileList())) {
            fileAttachmentProcess.updateBussinessNo(
                    dto.getUploadFileList().stream().map(UploadFile::getFileNo).collect(
                            Collectors.toList()),
                    dto.getRequirementId());
        }
        ProcessParameter<FinProductFeaAna002Dto> processParameter = new ProcessParameter<FinProductFeaAna002Dto>();
        WorkFlowParameter<FinProductFeaAna002Dto> params = new WorkFlowParameter<FinProductFeaAna002Dto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getRequirementId());
        params.setBusinessType(DictCodeListExt2.共通业务类型_产品需求分析.getValue());
        params.setComment(entity->"产品需求编号" + "(" + entity.getRequirementId() + ")"
                + "可行性分析结论" + "(" + entity.getFeasibilityResult() + ")");
        processParameter.setWorkFlowParameter(params);

        String msg = finProductFeaAna002Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }

    @ResponseBody
    @RequestMapping("delete")
    public AjaxResult delete(@RequestBody FinProductFeaAna002Dto dto){
        String requirementId = dto.getRequirementId();
        //是否处于流程判断check
        WflValidInputParameter parameter = new WflValidInputParameter();
        parameter.setPageId(getPageId());
        parameter.setBusinessDataPk1(requirementId);
        if(mstWorkflowCommonService.isInWflProcess(parameter)){
            AjaxResult result = new AjaxResult();
            result.setMessage("不能删除流程中的数据！");
            result.setMessageType(MessageType.错误消息);
            return result;
        }
        else if(dto.getFeasibilityStatus() == null){
            AjaxResult result = new AjaxResult();
            result.setMessage("未进行过可行性分析的数据不可删除！");
            result.setMessageType(MessageType.错误消息);
            return result;
        }
        else {
            //删除附件
            List<UploadFile> uploadFiles = fileAttachmentProcess.fetchByBusinessCode(requirementId);
            if(uploadFiles.size() > 0){
                uploadFiles.forEach(file ->{
                    fileAttachmentProcess.remove(file.getFileNo());
                });
            }
            return finProductFeaAna002Service.delete(requirementId);
        }
    }
}
