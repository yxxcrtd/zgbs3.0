/*
 * 文件名：MarketInformationController002.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年7月18日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.select.SuggestDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.dto.MskCreditInfoProSaveRequest;
import com.joyintech.tams.exchange.basicdata.bussiness.mskcreditinfo.service.MskCreditMarketInfoPro001Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@Controller
@PageId("MstCreditMarketInfo002")
@RequestMapping("/exchange/basicdata/bussiness/mskcreditinfo/MstCreditMarketInfo002")
public class MskCreditMarketInfo002Controller extends BaseController {

    @Autowired
    private MskCreditMarketInfoPro001Service mskCreditMarketInfoPro001Service;

    /**
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        String secId = getParam("secid");
        String valueDate = getParam("cate");
        String valueSource = getParam("valueSource");
        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        if (!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)) {
            MskCreditInfoProSaveRequest dto = mskCreditMarketInfoPro001Service.getBussinessEntityByWorkflowId(
                workflowId);
            model.addAttribute("pageModel",dto.getPageMode());
            model.addAttribute(GlobalConstant.ENTITY, dto);
        }
        else if (WorkFlowUtil.isNotFromToDoList() && 
            StringUtil.isNotEmpty(secId) && StringUtil.isNotEmpty(valueDate)
            && StringUtil.isNotEmpty(valueSource)) {
            //通过主键查询数据
            MskCreditInfoProSaveRequest dto = mskCreditMarketInfoPro001Service.selectByKey(
                secId, valueDate,valueSource);
            model.addAttribute(GlobalConstant.ENTITY, dto);
        }
        this.setSelectOptionToModel(model, DictCode.估值来源);
        return super.init(model);
    }

    /**
     * 画面点击保存按钮时check
     * 
     * @param dto 实体类
     * @param result 
     * @return check结果
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupEdit.class}) MskCreditInfoProSaveRequest dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }
    /**
     * 保存
     * @param dto dto
     * @param br br
     * @return 结果
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({
        ValidateGroupEdit.class}) MskCreditInfoProSaveRequest dto, BindingResult br)
        throws Exception {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<MskCreditInfoProSaveRequest> processParameter = new ProcessParameter<MskCreditInfoProSaveRequest>();
        WorkFlowParameter<MskCreditInfoProSaveRequest> params = new WorkFlowParameter<MskCreditInfoProSaveRequest>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        if (StringUtil.isEmpty(dto.getSecid())) {
            dto.setSecid(IDGenerator.generate());
        }
        params.setBusinessCode(dto.getSecid());
        params.setBusinessType(DictCodeList.共通业务类型_信用保护凭证行情.getValue());

        // 设置流程业务说明, entity即传入的dto
        params.setComment(entity -> this.getMessage("MstCreditMarketInfo001.secid") + "(" + entity.getSecid() + ")" + "-"
            + this.getMessage("MstCreditMarketInfo001.valueDate") + "("
            + DateUtil.format(entity.getCdate()) + ")"+ this.getMessage("MstCreditMarketInfo001.valueSource") + "("
            + entity.getValueSourceStr() + ")");
        processParameter.setWorkFlowParameter(params);
        Date sysDate = DateUtil.platDateFromCache();// 获取平台日期
        dto.setCreateTime(sysDate);
        String msg = mskCreditMarketInfoPro001Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }
    
}
