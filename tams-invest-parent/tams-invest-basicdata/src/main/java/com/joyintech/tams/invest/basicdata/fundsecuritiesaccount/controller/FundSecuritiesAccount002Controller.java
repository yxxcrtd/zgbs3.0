/*
 * 文件名：MstSecuritiesAccount001Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：lizhi
 * 修改时间：2020年1月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.basicdata.fundsecuritiesaccount.controller;



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
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.dto.UploadFile;
import com.joyintech.framework.listener.FileAttachmentProcess;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.basicdata.fundsecuritiesaccount.dto.FundSecuritiesAccountDto;
import com.joyintech.tams.invest.basicdata.fundsecuritiesaccount.service.FundSecuritiesAccountService;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;


/**
 * 基金账户管理
 * 〈功能详细描述〉
 * @author lizhi
 * @version 1.0 2020年1月14日
 * @see FundSecuritiesAccount002Controller
 * @since 1.0
 */
@Controller
@PageId("FundSecuritiesAccount002")
@RequestMapping("/invest/basicdata/fundsecuritiesaccount/FundSecuritiesAccount002")
public class FundSecuritiesAccount002Controller extends BaseController{
    /**
     * 基金管理Service
     */
    @Autowired
    private FundSecuritiesAccountService fundSecuritiesAccountService;

    /**
     * 附件处理接口
     */
    @Autowired(required = false)
    private FileAttachmentProcess fileAttachmentProcess;

    /**
     * 新建页面初始化
     */
    @Override
    @RequestMapping(PAGE_INIT)
    public String init(Model model) {
        String secAcctId = getParam("secAcctId");
        String workflowId = getParam("workflowId");
      //不来自于代办
        if(WorkFlowUtil.isNotFromToDoList()&& StringUtil.isNotEmpty(secAcctId)){
            //根据主键获取业务实体
            FundSecuritiesAccountDto entity = fundSecuritiesAccountService.selectBySecAcctId(secAcctId);
            model.addAttribute("entity",entity);
        }
        //来自代办
        else if(StringUtil.isNotEmpty(workflowId)){
            //根据工作流与留痕处理提供的共通方法获取业务实体
            FundSecuritiesAccountDto entity = fundSecuritiesAccountService.getBussinessEntityByWorkflowId(workflowId);
            model.addAttribute("entity", entity);
            model.addAttribute("workflowId", workflowId);
        }
        return super.init(model);
    }
    /**
     * 保存按钮点击时的check
     *
     * @param dto 业务实体
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({ValidateGroupEdit.class}) FundSecuritiesAccountDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }
    /**
     * 
     * 保存
     * <功能详细描述>
     * @param dto 业务实体
     * @param br AjaxResult
     * @return AjaxResul
     * @throws Exception Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({ValidateGroupEdit.class}) FundSecuritiesAccountDto dto, BindingResult br) throws WorkFlowException {
        if(br.hasErrors()){
            return new AjaxResult(br);
        }
        dto.setBussinessId(dto.getSeqNo());
        dto.setSecAcctType(DictCodeListExt3.证券账户类型_基金账户.getValue());
        ProcessParameter<FundSecuritiesAccountDto> processParameter = new ProcessParameter<FundSecuritiesAccountDto>();
        WorkFlowParameter<FundSecuritiesAccountDto> params = new WorkFlowParameter<FundSecuritiesAccountDto>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessCode(item->item.getSecAcctId()+item.getManager());
        params.setBusinessType(DictCodeListExt3.共通业务类型_基金账户管理.getValue());

        if (CollectionUtils.isNotEmpty(dto.getFileArray())
            && StringUtil.isNotEmpty(dto.getSecAcctId())) {
            // 将附件和当前业务对象绑定（更新附件的业务代码）
            fileAttachmentProcess.updateBussinessNo(
                dto.getFileArray().stream().map(UploadFile::getFileNo).collect(
                    Collectors.toList()),
                dto.getSecAcctId());
        }
        
        // 设置流程业务说明, entity即传入的dto
        params.setComment(
            entity -> SpringContextUtil.getTextValue("FundSecuritiesAccount001.secAcctId")+ "(" +entity.getSecAcctId()+ ")" 
                +  "-" + SpringContextUtil.getTextValue("FundSecuritiesAccount001.secAcctName")+ "(" + entity.getSecAcctName() + ")");
        processParameter.setWorkFlowParameter(params);
        String msg = fundSecuritiesAccountService.start(processParameter, dto);
        return new AjaxResult(msg);
    }
    
    @ResponseBody
    @RequestMapping("/getSecManagerSelect")
    public List<SelectOptionDto> getSecManagerSelect(String search){
        List<SelectOptionDto> temp = fundSecuritiesAccountService.getSecManagerSelect(search);
        return temp;
    }
}
