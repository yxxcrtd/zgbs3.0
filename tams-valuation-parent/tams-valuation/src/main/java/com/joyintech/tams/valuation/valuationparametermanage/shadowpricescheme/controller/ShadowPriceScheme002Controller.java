/*
 * 文件名：ShadowPriceScheme002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.valuationparametermanage.shadowpricescheme.controller;

import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
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
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.valuation.valuationparametermanage.shadowpricescheme.dto.ShadowPriceScheme001Dto;
import com.joyintech.tams.valuation.valuationparametermanage.shadowpricescheme.service.ShadowPriceScheme001Service;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

@Controller
@PageId("ShadowPriceScheme002")
@RequestMapping("/valuation/valuationparametermanage/shadowpricescheme/ShadowPriceScheme002")
public class ShadowPriceScheme002Controller extends BaseController {

    @Autowired
    private ShadowPriceScheme001Service ShadowPriceScheme001Service;

    @Override
    public String init(Model model) {
        // 工作流ID
        String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);

        String schId = getParam("schId");
        String operateType = getParam("operateType");
        ShadowPriceScheme001Dto dto = new ShadowPriceScheme001Dto();
        // 新建时，默认是否默认为否
        dto.setIsDefault(DictCodeList.是否_否.getValue());
        if (StringUtil.isNotEmpty(schId)) {
            dto = ShadowPriceScheme001Service.selectSchemeBySchId(schId);
            // 复制操作
            if(StringUtil.equals(operateType, "copy")){
                dto.setOrgSchId(schId);
            }
        }
        else if (StringUtil.isNotEmpty(workflowId)) {
            dto = ShadowPriceScheme001Service.getBussinessEntityByWorkflowId(
                workflowId);
            model.addAttribute("workflowId", workflowId);
        }
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
    public AjaxResult check(@RequestBody @Validated({ValidateGroupEdit.class}) ShadowPriceScheme001Dto dto,
                            BindingResult bindingResult)
         {
        if (bindingResult.hasErrors()) {
            return new AjaxResult(bindingResult);
        }
        String msg = StringUtil.EMPTY;
        String operateType = "";
        if (StringUtil.isEmpty(dto.getSchName())) {
            msg = "请输入方案名称。";
            return new AjaxResult(msg, MessageType.错误消息);
        }
        // 修改唯一性验证
        if (OPERATE_TYPE_SAVE.equals(dto.getOperateType())) {
            operateType = OPERATE_TYPE_SAVE;                                                                                    
            Map<String, Object> params = Maps.newHashMap();
            params.put("schName", dto.getSchName());
            List<ShadowPriceScheme001Dto> list = ShadowPriceScheme001Service.findShadowPrice(
                params).getRoot();
            if (CollectionUtils.isNotEmpty(list)) {
                // 查出的方案不是当前方案
                if (!StringUtil.equals(dto.getSchId(), list.get(0).getSchId())) {
                    msg = "方案名称已存在！请重新输入。";
                    return new AjaxResult(msg, MessageType.错误消息);
                }
            }
            // 判断当前流程是否存在
            if (ShadowPriceScheme001Service.checkBusinessDataInFlow(dto)) {
                // 流程存在情况
                return new AjaxResult("该条数据在流程中，不能修改", MessageType.错误消息);
            }
        }
        // 新建唯一性验证：方案代码、方案名称
        else {// submit事件
            operateType = OPERATE_TYPE_SUBMIT;
            Map<String, Object> params = Maps.newHashMap();
            params.put("schId", dto.getSchId());
            params.put("schName", dto.getSchName());
            List<ShadowPriceScheme001Dto> list = ShadowPriceScheme001Service.findShadowPrice(
                params).getRoot();
            if (CollectionUtils.isNotEmpty(list)) {
                msg = "方案代码或方案名称已存在！请重新输入。";
                return new AjaxResult(msg, MessageType.错误消息);
            }

        }
        // 若当前方案是否默认选择是
        if(DictCodeList.是否_是.getValue().equals(dto.getIsDefault())){
            // 查询原默认方案
            ShadowPriceScheme001Dto defaultScheme = ShadowPriceScheme001Service.getDefaultScheme();
            // 若存在默认方案
            if(defaultScheme != null){
                // 且非当前方案
                if(!StringUtil.equals(defaultScheme.getSchId(), dto.getSchId())){
                    // 流程存在情况
                    return new AjaxResult("已存在默认方案，不可设为默认", MessageType.错误消息);
                }
            }
        }

        return commonCheck(bindingResult, operateType);
    }

    /**
     * 新建或修改
     * 
     * @param dto
     * @param bindingResult
     * @return
     */
    /*
     * @ResponseBody
     * @RequestMapping(value = PAGE_SAVE, method = RequestMethod.POST) public AjaxResult
     * save(@RequestBody @Valid ShadowPriceScheme001Dto dto, BindingResult bindingResult) { String
     * msg = StringUtil.EMPTY; if (StringUtils.isEmpty(dto.getSchId())) {
     * dto.setSchId(IDGenerator.generate()); } String operateType = dto.getOperateType(); if
     * ("submit".equals(operateType)) { ShadowPriceScheme001Service.insert(dto); msg = "新建成功！"; }
     * else if ("save".equals(operateType)) {
     * ShadowPriceScheme001Service.updateByPrimaryKeySelective(dto); msg = "修改成功！"; } return new
     * AjaxResult(msg); }
     */

    /**
     * 方案新建走流程 <一句话功能简述> <功能详细描述>
     * 
     * @param dto
     * @param br
     * @return
     */
    @ResponseBody
    @RequestMapping(value = PAGE_SAVE, method = RequestMethod.POST)
    public AjaxResult doSave(@RequestBody @Validated({ValidateGroupEdit.class}) ShadowPriceScheme001Dto dto, BindingResult br) {

        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        // 启动流程
        ProcessParameter<ShadowPriceScheme001Dto> proParam = new ProcessParameter<>();
        WorkFlowParameter<ShadowPriceScheme001Dto> params = new WorkFlowParameter<>();

        // 业务代码（资产代码）
        params.setBusinessCode(dto.getSchId());
        // 业务类型
        params.setBusinessType(DictCodeList.共通业务类型_影子定价方案.getValue());
        // 页面id
        params.setPageId(this.getPageId());
        // 元素id
        params.setElementId(this.getParam(GlobalConstant.ELEMENT_ID));

        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> WorkFlowUtil.concatComment("影子定价方案新建：", entity.getSchId(), // 方案代码
            entity.getSchName(), // 方案名称
            entity.getCreateUser()// 创建人
        ));

        // 塞入流程参数
        proParam.setWorkFlowParameter(params);

        String msg = getMessage("COMMON_I_0001");
        try {
            msg = ShadowPriceScheme001Service.start(proParam, dto);
        }
        catch (Exception e) {
            getLogger().error("启动流程失败：", e);
            return new AjaxResult(e.getMessage());
        }

        return new AjaxResult(msg);
    }

    /**
     * 方案修改走流程 <一句话功能简述> <功能详细描述>
     * 
     * @param dto
     * @param br
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "doUpdate", method = RequestMethod.POST)
    public AjaxResult doUpdate(@RequestBody @Validated({ValidateGroupEdit.class}) ShadowPriceScheme001Dto dto, BindingResult br) {

        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        // 启动流程
        ProcessParameter<ShadowPriceScheme001Dto> proParam = new ProcessParameter<>();
        WorkFlowParameter<ShadowPriceScheme001Dto> params = new WorkFlowParameter<>();

        // 业务代码（资产代码）
        params.setBusinessCode(dto.getSchId());
        // 业务类型
        params.setBusinessType(DictCodeList.共通业务类型_影子定价方案.getValue());
        // 页面id
        params.setPageId(this.getPageId());
        // 元素id
        params.setElementId(this.getParam(GlobalConstant.ELEMENT_ID));

        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> WorkFlowUtil.concatComment("影子定价方案修改：", entity.getSchId(), // 方案代码
            entity.getSchName(), // 方案名称
            entity.getCreateUser()// 创建人
        ));

        // 塞入流程参数
        proParam.setWorkFlowParameter(params);

        String msg = getMessage("COMMON_I_0001");
        try {
            msg = ShadowPriceScheme001Service.start(proParam, dto);
        }
        catch (Exception e) {
            getLogger().error("启动流程失败：", e);
            return new AjaxResult(e.getMessage());
        }

        return new AjaxResult(msg);
    }
}
