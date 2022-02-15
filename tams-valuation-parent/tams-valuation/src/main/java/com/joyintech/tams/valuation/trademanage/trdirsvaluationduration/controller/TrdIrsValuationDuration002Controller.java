/*
 * 文件名：TrdBondInvestTrade002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.trdirsvaluationduration.controller;


import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.component.business.service.CommonBusinessService;
import com.joyintech.tams.valuation.trademanage.trdirsvaluationduration.dto.TrdIrsValuationDuration002Dto;
import com.joyintech.tams.valuation.trademanage.trdirsvaluationduration.dto.TrdIrsValuationDuration003Dto;
import com.joyintech.tams.valuation.trademanage.trdirsvaluationduration.service.TrdIrsValuationDuration002Service;
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
import java.util.List;
import java.util.Map;

/**
 * 利率互换存续期管理
 *
 * @author sunshuaishuai
 * @version 1.0 2022年02月11日
 * @see TrdIrsValuationDuration002Controller
 * @since 1.0
 */
@Controller
@PageId("TrdIrsValuationDuration002")
@RequestMapping("/valuation/trademanage/trdirsvaluationduration/TrdIrsValuationDuration002")
public class TrdIrsValuationDuration002Controller extends BaseController {

    @Autowired
    private TrdIrsValuationDuration002Service trdIrsValuationDuration002Service;

    @Autowired
    private CommonBusinessService commonBusinessService;

    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.利率互换存续期类型, DictCode.币种, DictCode.交易方向, DictCode.利率互换收付息类型);
        // 工作流ID
        String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);

        String operateType = getParam("operateType");

        String tradeId = getParam("tradeId");

        if(StringUtil.isNotEmpty(workflowId)){
            TrdIrsValuationDuration002Dto entity= trdIrsValuationDuration002Service.getBussinessEntityByWorkflowId(workflowId);
            if(StringUtil.isNotEmpty(entity.getTradeId())) {
                entity = trdIrsValuationDuration002Service.queryTradeInfoByTradeId(entity.getTradeId());
            }
            model.addAttribute("entity", entity);
            model.addAttribute("workflowId", workflowId);

        }else if (!StringUtil.isNotEmpty(workflowId) && StringUtil.isNotEmpty(tradeId)){
            TrdIrsValuationDuration002Dto entity = trdIrsValuationDuration002Service.queryTradeInfoByTradeId(tradeId);
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
     * 原始交易编号下拉框
     *
     * @return 交易编号
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getTradeId")
    public List<SelectOptionDto> getTradeId() {
        Map<String, Object> param = RequestUtil.getPageParam();
        return trdIrsValuationDuration002Service.getTradeId(param);
    }

    /**
     * 根据原始交易编查询交易信息
     *
     * @return 查询结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("findInfoByTradeId")
    public TrdIrsValuationDuration003Dto findInfoByTradeId() {
        Map<String,Object> map=RequestUtil.getPageParam();
        String pTradeId = (String)map.get("pTradeId");
        return trdIrsValuationDuration002Service.findInfoByTradeId(pTradeId);
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
     * @param trdIrsValuationDuration002Dto 参数
     * @param result 校验结果
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@Valid @RequestBody TrdIrsValuationDuration002Dto trdIrsValuationDuration002Dto,
                            BindingResult result) {
        return commonCheck(result, trdIrsValuationDuration002Dto.getOperateType());
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
    AjaxResult save(@RequestBody @Valid TrdIrsValuationDuration002Dto dto, BindingResult br)
            throws Exception {

        if (br.hasErrors()) {
            return new AjaxResult(br);
        }

        ProcessParameter<TrdIrsValuationDuration002Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdIrsValuationDuration002Dto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId(this.getPageId());

        //设置主键
        if(!StringUtil.isNotEmpty(dto.getTradeId())){
            Date sysDate= DateUtil.platDateFromCache();
            dto.setTradeId(getTradeId("JYBONDBONUS", sysDate, "A"));
        }

        workFlowParameter.setPageId(this.getPageId());
        workFlowParameter.setElementId(this.getParam("elementId"));
        workFlowParameter.setBusinessCode(dto.getTradeId());
        workFlowParameter.setBusinessType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
                getMessage("交易编号") + GlobalConstant.COLON + entity.getTradeId() ));

        String msg = trdIrsValuationDuration002Service.start(processParameter, dto);

        return new AjaxResult(msg);
    }

    /**
     * 编号生成
     */
    public String getTradeId(String str, Date date, String flag) {
        int  sequences=0;
        // 查询数据库sequences
        if ("A".equals(flag)) {
            sequences = trdIrsValuationDuration002Service.querySequences();
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
