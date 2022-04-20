/*
 * 文件名：TrdBondInvestTrade002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.trdbondvaluationduration.controller;


import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.component.business.service.CommonBusinessService;
import com.joyintech.tams.valuation.trademanage.trdbondvaluationduration.dto.TrdBondValuationDurationDto;
import com.joyintech.tams.valuation.trademanage.trdbondvaluationduration.service.TrdBondValuationDuration002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 债券存续期管理
 *
 * @author sunshuaishuai
 * @version 1.0 2022年02月11日
 * @see TrdBondValuationDuration002Controller
 * @since 1.0
 */
@Controller
@PageId("TrdBondValuationDuration002")
@RequestMapping("/valuation/trademanage/trdbondvaluationduration/TrdBondValuationDuration002")
public class TrdBondValuationDuration002Controller extends BaseController {

    @Autowired
    private TrdBondValuationDuration002Service trdBondValuationDuration002Service;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向, DictCode.币种, DictCode.是否, DictCode.投资目的);
        model.addAttribute("YES_OR_NO_N", DictCodeList.是否_否.getValue());
        // 工作流ID
        String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);

        String tradeId = getParam("tradeId");

        if(StringUtil.isNotEmpty(workflowId)){
            TrdBondValuationDurationDto entity= trdBondValuationDuration002Service.getBussinessEntityByWorkflowId(workflowId);
            if(!StringUtil.isNotEmpty(entity.getTradeId())){
                entity.setTradeId("提交后自动生成，无需填写");
            }
            model.addAttribute("entity", entity);
            model.addAttribute("workflowId", workflowId);

        }else if (!StringUtil.isNotEmpty(workflowId) && StringUtil.isNotEmpty(tradeId)){
            TrdBondValuationDurationDto entity = trdBondValuationDuration002Service.queryTradeInfoByTradeId(tradeId);
            model.addAttribute("entity", entity);
        }


        return super.init(model);
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
        String[] accTypeArr =  {DictCodeListExt1.资金账户类型_产品托管户.getValue(),
                    DictCodeListExt1.资金账户类型_券商资金账户.getValue(),
                    DictCodeListExt1.资金账户类型_结算备付金账户.getValue(),};
        param.put("finprodId",param.get("prodId"));
        param.put("accTypeArr",accTypeArr);
        return commonBusinessService.getTrdAcctId(param);
    }

    /**
     * 确认校验
     *
     * @param trdBondValuationDurationDto 参数
     * @param result 校验结果
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupEdit.class}) TrdBondValuationDurationDto trdBondValuationDurationDto,
                            BindingResult result) {
        return commonCheck(result, trdBondValuationDurationDto.getOperateType());
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
            ValidateGroupEdit.class}) TrdBondValuationDurationDto dto, BindingResult br)
            throws Exception {

        if (br.hasErrors()) {
            return new AjaxResult(br);
        }

        ProcessParameter<TrdBondValuationDurationDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdBondValuationDurationDto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId(this.getPageId());

        workFlowParameter.setPageId(this.getPageId());
        workFlowParameter.setElementId(this.getParam("elementId"));
        workFlowParameter.setBusinessCode(dto.getAssetId());
        workFlowParameter.setBusinessType(DictCodeList.共通业务类型_债券类资产存续期.getValue());
        workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
                getMessage("资产代码") + GlobalConstant.COLON + entity.getAssetId(),
                getMessage("交易日期") + DateUtil.format(DateUtil.parse(DateUtil.getSysDate()))));

        String msg = trdBondValuationDuration002Service.start(processParameter, dto);

        return new AjaxResult(msg);
    }

}
