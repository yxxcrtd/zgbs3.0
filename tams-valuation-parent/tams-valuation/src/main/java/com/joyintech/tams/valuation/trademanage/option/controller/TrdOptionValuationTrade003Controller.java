/*
 * 文件名：TrdBondInvestTrade002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.option.controller;


import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.trademanage.option.dto.TrdOptionValuationTrade003Dto;
import com.joyintech.tams.valuation.trademanage.option.service.TrdOptionValuationTrade003Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.Valid;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 场外期权存续期交易
 *
 * @author sunshuaishuai
 * @version 1.0 2022年01月11日
 * @see TrdOptionValuationTrade003Controller
 * @since 1.0
 */
@Controller
@PageId("TrdOptionValuationTrade003")
@RequestMapping("/valuation/trademanage/option/TrdOptionValuationTrade003")
public class TrdOptionValuationTrade003Controller extends BaseController {

    @Autowired
    private TrdOptionValuationTrade003Service trdOptionValuationTrade003Service;
    
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向, DictCode.收付方向,DictCode.期权费支付类型,DictCode.币种, DictCode.行权方式);

        // 工作流ID
        String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);

        String operateType = getParam("operateType");

        String tradeId = getParam("tradeId");

        if(StringUtil.isNotEmpty(workflowId)){
            TrdOptionValuationTrade003Dto entity= trdOptionValuationTrade003Service.getBussinessEntityByWorkflowId(workflowId);
            model.addAttribute("entity", entity);
            model.addAttribute("workflowId", workflowId);

        }else if (!StringUtil.isNotEmpty(workflowId) && StringUtil.isNotEmpty(tradeId)){
            TrdOptionValuationTrade003Dto entity = trdOptionValuationTrade003Service.queryInfoByTradeId(tradeId);
            model.addAttribute("entity", entity);
        }


        return super.init(model);
    }

    /**
     * 确认校验
     *
     * @param trdOptionValuationTrade003Dto 参数
     * @param result 校验结果
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@Valid @RequestBody TrdOptionValuationTrade003Dto trdOptionValuationTrade003Dto,
                              BindingResult result) {
        return commonCheck(result, trdOptionValuationTrade003Dto.getOperateType());
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
    AjaxResult save(@RequestBody @Valid TrdOptionValuationTrade003Dto dto, BindingResult br)
        throws Exception {

        if (br.hasErrors()) {
            return new AjaxResult(br);
        }

        ProcessParameter<TrdOptionValuationTrade003Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdOptionValuationTrade003Dto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId(this.getPageId());

        //设置主键
        if(!StringUtil.isNotEmpty(dto.getTradeId())){
            Date sysDate= DateUtil.platDateFromCache();
            dto.setTradeId(getTradeId("JYOPTION", sysDate, "A"));
        }

        workFlowParameter.setPageId(this.getPageId());
        workFlowParameter.setElementId(this.getParam("elementId"));
        workFlowParameter.setBusinessCode(dto.getTradeId());
        workFlowParameter.setBusinessType(DictCodeListExt3.共通业务类型_期权交易.getValue());
        workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
            getMessage("交易编号") + GlobalConstant.COLON + entity.getTradeId(),
            getMessage("交易日") + GlobalConstant.COLON + entity.getTradeDate()));

        String msg = trdOptionValuationTrade003Service.start(processParameter, dto);

        return new AjaxResult(msg);
    }

    /**
     * 编号生成
     */
    public String getTradeId(String str, Date date, String flag) {
        int  sequences=0;
        // 查询数据库sequences
        if ("A".equals(flag)) {
            sequences = trdOptionValuationTrade003Service.querySequences();
        }
        // 组装4位序列号 不足补0
        String sequence = StringUtils.leftPad(String.valueOf(sequences), 4, '0');
        // 日期格式化yyymmdd
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String formatDate = sdf.format(date);

        String tradeIdStr = str + formatDate + sequence;
        return tradeIdStr;
    }


}
