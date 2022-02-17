/*
 * 文件名：TrdBondInvestTrade002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.trdsecovertransfervaluationduration.controller;


import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.component.business.service.CommonBusinessService;
import com.joyintech.tams.valuation.trademanage.trdsecovertransfervaluationduration.dto.TrdSecOverTransferValuationDuration002Dto;
import com.joyintech.tams.valuation.trademanage.trdsecovertransfervaluationduration.service.TrdSecOverTransferValuationDuration002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * 应收款转让管理
 *
 * @author sunshuaishuai
 * @version 1.0 2022年02月17日
 * @see TrdSecOverTransferValuationDuration002Controller
 * @since 1.0
 */
@Controller
@PageId("TrdSecOverTransferValuationDuration002")
@RequestMapping("/valuation/trademanage/trdsecovertransfervaluationduration/TrdSecOverTransferValuationDuration002")
public class TrdSecOverTransferValuationDuration002Controller extends BaseController {

    @Autowired
    private TrdSecOverTransferValuationDuration002Service trdSecOverTransferValuationDuration002Service;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向, DictCode.币种);
        // 工作流ID
        String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);

        String operateType = getParam("operateType");

        String tradeId = getParam("tradeId");

        if(StringUtil.isNotEmpty(workflowId)){
            TrdSecOverTransferValuationDuration002Dto entity= trdSecOverTransferValuationDuration002Service.getBussinessEntityByWorkflowId(workflowId);
            if(StringUtil.isNotEmpty(entity.getTradeId())) {
                entity = trdSecOverTransferValuationDuration002Service.queryTradeInfoByTradeId(entity.getTradeId());
            }
            model.addAttribute("entity", entity);
            model.addAttribute("workflowId", workflowId);

        }else if (!StringUtil.isNotEmpty(workflowId) && StringUtil.isNotEmpty(tradeId)){
            TrdSecOverTransferValuationDuration002Dto entity = trdSecOverTransferValuationDuration002Service.queryTradeInfoByTradeId(tradeId);
            model.addAttribute("entity", entity);
        }

        return super.init(model);
    }

    /**
     * 产品代码下拉框
     *
     * @return 产品代码
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getFinprodId")
    public List<SelectOptionDto> getFinprodId() {
        Map<String, Object> param = RequestUtil.getPageParam();
        param.put("finprodType","F16");
        return commonBusinessService.getProdId(param);
    }

    /**
     * 资产代码下拉框
     *
     * @return 资产代码
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getAssetId")
    public List<SelectOptionDto> getAssetId() {
        Map<String, Object> param = RequestUtil.getPageParam();
        param.put("assetType","F01");
        return commonBusinessService.getAssetId(param);
    }

    /**
     * 资金账户下拉框
     *
     * @return 资产代码
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getTrdAcctId")
    public List<SelectOptionDto> getTrdAcctId() {
        Map<String, Object> param = RequestUtil.getPageParam();
        param.put("portfolioId",param.get("finprodId"));
        return commonBusinessService.getTrdAcctId(param);
    }

    /**
     * 确认校验
     *
     * @param trdSecOverTransferValuationDuration002Dto 参数
     * @param result 校验结果
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@Valid @RequestBody TrdSecOverTransferValuationDuration002Dto trdSecOverTransferValuationDuration002Dto,
                            BindingResult result) {
        return commonCheck(result, trdSecOverTransferValuationDuration002Dto.getOperateType());
    }


    /**
     * 保存提交
     *
     * @param dto
     * @return 画面输入参数
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @RequestMapping("save")
    @ResponseBody
    AjaxResult save(@RequestBody @Valid TrdSecOverTransferValuationDuration002Dto dto, BindingResult br)
            throws Exception {

        if (br.hasErrors()) {
            return new AjaxResult(br);
        }

        ProcessParameter<TrdSecOverTransferValuationDuration002Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdSecOverTransferValuationDuration002Dto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId(this.getPageId());

        workFlowParameter.setPageId(this.getPageId());
        workFlowParameter.setElementId(this.getParam("elementId"));
        workFlowParameter.setBusinessCode(dto.getTradeId());
        workFlowParameter.setBusinessType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
                getMessage("交易编号") + GlobalConstant.COLON + entity.getTradeId() ));

        String msg = trdSecOverTransferValuationDuration002Service.start(processParameter, dto);

        return new AjaxResult(msg);
    }

}
