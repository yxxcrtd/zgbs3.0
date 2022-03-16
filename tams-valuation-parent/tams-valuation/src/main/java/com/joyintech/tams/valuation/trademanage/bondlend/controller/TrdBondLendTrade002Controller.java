/*
 * 文件名：TrdBondInvestTrade002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.bondlend.controller;


import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.component.business.service.CommonBusinessService;
import com.joyintech.tams.valuation.trademanage.bondlend.dto.TrdBondLendTradeDto;
import com.joyintech.tams.valuation.trademanage.bondlend.service.TrdBondLendTrade002Service;
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
 * 债券借贷交易新建
 *
 * @author sunshuaishuai
 * @version 1.0 2022年01月06日
 * @see TrdBondLendTrade002Controller
 * @since 1.0
 */
@Controller
@PageId("TrdBondLendTrade002")
@RequestMapping("/valuation/trademanage/bondlend/TrdBondLendTrade002")
public class TrdBondLendTrade002Controller extends BaseController {

    @Autowired
    private TrdBondLendTrade002Service trdBondLendTrade002Service;

    @Autowired
    private CommonBusinessService commonBusinessService;
    
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易场所,DictCode.交易方向, DictCode.币种, DictCode.投资目的);

        // 工作流ID
        String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);

        String operateType = getParam("operateType");

        String tradeId = getParam("tradeId");

        //工作流
        if(StringUtil.isNotEmpty(workflowId)){
            TrdBondLendTradeDto entity= trdBondLendTrade002Service.getBussinessEntityByWorkflowId(workflowId);
            model.addAttribute("entity", entity);
            model.addAttribute("workflowId", workflowId);

        }else if (!StringUtil.isNotEmpty(workflowId) && StringUtil.isNotEmpty(tradeId)){//编辑页面
            TrdBondLendTradeDto entity = trdBondLendTrade002Service.queryTradeInfoByTradeId(tradeId);
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
    public Page<SelectOptionDto> getTrdAcctId() {
        Map<String, Object> param = RequestUtil.getPageParam();
        param.put("finprodId",param.get("finprodId"));
        return trdBondLendTrade002Service.getTrdAcctId(param);
    }

    /**
     * 确认校验
     *
     * @param trdBondLendTradeDto 参数
     * @param result 校验结果
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupEdit.class}) TrdBondLendTradeDto trdBondLendTradeDto,
                              BindingResult result) {
        return commonCheck(result, trdBondLendTradeDto.getOperateType());
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
            ValidateGroupEdit.class}) TrdBondLendTradeDto dto, BindingResult br)
        throws Exception {

        if (br.hasErrors()) {
            return new AjaxResult(br);
        }

        ProcessParameter<TrdBondLendTradeDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdBondLendTradeDto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId(this.getPageId());

        //设置主键
        if(!StringUtil.isNotEmpty(dto.getTradeId())){
            Date sysDate= DateUtil.platDateFromCache();
            dto.setTradeId(getTradeId("JYSL", sysDate, "A"));
        }

        workFlowParameter.setPageId(this.getPageId());
        workFlowParameter.setElementId(this.getParam("elementId"));
        workFlowParameter.setBusinessCode(dto.getTradeId());
        workFlowParameter.setBusinessType(DictCodeListExt2.共通业务类型_债券借贷交易.getValue());
        workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
            getMessage("交易编号") + GlobalConstant.COLON + entity.getTradeId(),
            getMessage("交易日") + GlobalConstant.COLON + DateUtil.format(entity.getTradeDate())));

        String msg = trdBondLendTrade002Service.start(processParameter, dto);

        return new AjaxResult(msg);
    }

    /**
     * 编号生成
     */
    public String getTradeId(String str, Date date, String flag) {
        int  sequences=0;
        // 查询数据库sequences
        if ("A".equals(flag)) {
            sequences = trdBondLendTrade002Service.querySequences();
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
