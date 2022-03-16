/*
 * 文件名：TrdBondInvestTrade002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.trdstockvaluationduration.controller;


import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.component.business.service.CommonBusinessService;
import com.joyintech.tams.valuation.trademanage.trdstockvaluationduration.dto.TrdStockValuationDurationDto;
import com.joyintech.tams.valuation.trademanage.trdstockvaluationduration.service.TrdStockValuationDuration002Service;
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

import java.util.List;
import java.util.Map;

/**
 * 股票存续期管理
 *
 * @author sunshuaishuai
 * @version 1.0 2022年02月14日
 * @see TrdStockValuationDuration002Controller
 * @since 1.0
 */
@Controller
@PageId("TrdStockValuationDuration002")
@RequestMapping("/valuation/trademanage/trdstockvaluationduration/TrdStockValuationDuration002")
public class TrdStockValuationDuration002Controller extends BaseController {

    @Autowired
    private TrdStockValuationDuration002Service trdStockValuationDuration002Service;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.股票分红类型, DictCode.币种);
        // 工作流ID
        String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);

        String operateType = getParam("operateType");

        String tradeId = getParam("tradeId");

        if(StringUtil.isNotEmpty(workflowId)){
            TrdStockValuationDurationDto entity= trdStockValuationDuration002Service.getBussinessEntityByWorkflowId(workflowId);
            if(StringUtil.isNotEmpty(entity.getTradeId())) {
                entity = trdStockValuationDuration002Service.queryTradeInfoByTradeId(entity.getTradeId());
            }
            model.addAttribute("entity", entity);
            model.addAttribute("workflowId", workflowId);

        }else if (!StringUtil.isNotEmpty(workflowId) && StringUtil.isNotEmpty(tradeId)){
            TrdStockValuationDurationDto entity = trdStockValuationDuration002Service.queryTradeInfoByTradeId(tradeId);
            model.addAttribute("entity", entity);
        }

        return super.init(model);
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
        param.put("assetType","F10");
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
     * @param trdStockValuationDurationDto 参数
     * @param result 校验结果
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupEdit.class}) TrdStockValuationDurationDto trdStockValuationDurationDto,
                            BindingResult result) {
        return commonCheck(result, trdStockValuationDurationDto.getOperateType());
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
    AjaxResult save(@RequestBody @Validated({
            ValidateGroupEdit.class}) TrdStockValuationDurationDto dto, BindingResult br)
            throws Exception {

        if (br.hasErrors()) {
            return new AjaxResult(br);
        }

        ProcessParameter<TrdStockValuationDurationDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdStockValuationDurationDto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId(this.getPageId());

        workFlowParameter.setPageId(this.getPageId());
        workFlowParameter.setElementId(this.getParam("elementId"));
        workFlowParameter.setBusinessCode(dto.getTradeId());
        workFlowParameter.setBusinessType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
                getMessage("交易编号") + GlobalConstant.COLON + entity.getTradeId() ));

        String msg = trdStockValuationDuration002Service.start(processParameter, dto);

        return new AjaxResult(msg);
    }

}
