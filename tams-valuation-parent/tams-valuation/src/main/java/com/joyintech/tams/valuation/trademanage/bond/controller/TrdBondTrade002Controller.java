/*
 * 文件名：TrdBondInvestTrade002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.bond.controller;


import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.component.business.service.CommonBusinessService;
import com.joyintech.tams.valuation.trademanage.bond.dto.TrdBondTradeDto;
import com.joyintech.tams.valuation.trademanage.bond.service.TrdBondTrade002Service;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 债券交易新建
 *
 * @author Zane.Y
 * @version 1.0 2018年9月7日
 * @see TrdBondTrade002Controller
 * @since 1.0
 */
@Controller
@PageId("TrdBondTrade002")
@RequestMapping("/valuation/trademanage/bond/TrdBondTrade002")
public class TrdBondTrade002Controller extends BaseController {

    @Autowired
    private TrdBondTrade002Service trdBondTrade002Service;

    @Autowired
    private CommonBusinessService commonBusinessService;
    
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易场所,DictCode.交易方向, DictCode.币种, DictCode.投资目的);

        // 工作流ID
        String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);

        String tradeId = getParam("tradeId");

        if(!WorkFlowUtil.isNotFromToDoList() && StringUtil.isNotEmpty(workflowId)){
            TrdBondTradeDto entity= trdBondTrade002Service.getBussinessEntityByWorkflowId(workflowId);
            model.addAttribute("entity", entity);
            model.addAttribute("workflowId", workflowId);

        }else if (!StringUtil.isNotEmpty(workflowId) && StringUtil.isNotEmpty(tradeId)){//明细页面
            TrdBondTradeDto entity = trdBondTrade002Service.queryTradeInfoByTradeId(tradeId);
            model.addAttribute("entity", entity);
        }


        return super.init(model);
    }

    /**
     * 通过资产代码查询资产简称和交易场所
     *
     * @return 产品代码列表
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("selectInfoBySecId")
    public FinProduct selectInfoBySecId() {
        Map<String,Object> map=RequestUtil.getPageParam();
        return trdBondTrade002Service.selectInfoBySecId(map);
    }

    /**
     * 查询证券托管户：下拉框
     *
     * @param params 画面参数
     * @return 证券托管户：下拉框
     * @see
     * @since 1.0
     */
    @RequestMapping("getSecManageAcctId")
    @ResponseBody
    public List<SelectOptionDto> getSecManageAcctId(@RequestParam Map<String, Object> params) {
        return trdBondTrade002Service.getSecManageAcctId(params);
    }

    /**
     * 模糊查询通道代码与通道收取方式
     *
     * @return 通道代码
     * @see
     * @since 1.0
     */
    @RequestMapping("getChannelId")
    @ResponseBody
    public Page<SelectOptionDto> getChannelId() {
        return trdBondTrade002Service.getChannelId(RequestUtil.getPageParam());
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
     * @param dto 参数
     * @param result 校验结果
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupEdit.class}) TrdBondTradeDto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
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
            ValidateGroupEdit.class}) TrdBondTradeDto dto, BindingResult br)
        throws Exception {

        if (br.hasErrors()) {
            return new AjaxResult(br);
        }

        ProcessParameter<TrdBondTradeDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdBondTradeDto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId(this.getPageId());

        //设置主键
        if(!StringUtil.isNotEmpty(dto.getTradeId())){
            Date sysDate= DateUtil.platDateFromCache();
            dto.setTradeId(getTradeId("JYBOND", sysDate, "A"));
        }

        workFlowParameter.setPageId(this.getPageId());
        workFlowParameter.setElementId(this.getParam("elementId"));
        workFlowParameter.setBusinessCode(dto.getTradeId());
        workFlowParameter.setBusinessType(DictCodeList.共通业务类型_债券类资产交易.getValue());
        workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
            getMessage("交易编号") + GlobalConstant.COLON + entity.getTradeId(),
            getMessage("交易日") + GlobalConstant.COLON + DateUtil.format(entity.getTradeDate())));

        String msg = trdBondTrade002Service.start(processParameter, dto);

        return new AjaxResult(msg);
    }

    /**
     * 编号生成
     */
    public String getTradeId(String str, Date date, String flag) {
        int  sequences=0;
        // 查询数据库sequences
        if ("A".equals(flag)) {
            sequences = trdBondTrade002Service.querySequences();
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
