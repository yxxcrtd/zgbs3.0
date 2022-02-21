/*
 * 文件名：TrdBondInvestTrade002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.trdbondchangevaluationtrade.controller;


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
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import com.joyintech.tams.valuation.component.business.service.CommonBusinessService;
import com.joyintech.tams.valuation.trademanage.trdbondchangevaluationtrade.dto.TrdBondChangeTrade002Dto;
import com.joyintech.tams.valuation.trademanage.trdbondchangevaluationtrade.service.TrdBondChangeTrade001Service;
import com.joyintech.tams.valuation.trademanage.trdbondchangevaluationtrade.service.TrdBondChangeTrade002Service;
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
 * 证券转换交易新建
 *
 * @author sunshuaishuai
 * @version 1.0 2021年01月04日
 * @see TrdBondChangeTrade002Controller
 * @since 1.0
 */
@Controller
@PageId("TrdBondChangeTrade002")
@RequestMapping("/valuation/trademanage/trdbondchangevaluationtrade/TrdBondChangeTrade002")
public class TrdBondChangeTrade002Controller extends BaseController {

    @Autowired
    private TrdBondChangeTrade002Service trdBondChangeTrade002Service;

    @Autowired
    private TrdBondChangeTrade001Service trdBondChangeTrade001Service;

    @Autowired
    private CommonBusinessService commonBusinessService;
    
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, DictCode.交易方向, DictCode.币种, DictCode.投资目的);

        // 工作流ID
        String workflowId = this.getParam(GlobalConstant.WORKFLOW_ID);

        String operateType = this.getParam("operateType");

        String tradeId = getParam("tradeId");

        if(StringUtil.isNotEmpty(workflowId)){
            TrdBondChangeTrade002Dto entity= trdBondChangeTrade002Service.getBussinessEntityByWorkflowId(workflowId);
            model.addAttribute("entity", entity);
            model.addAttribute("workflowId", workflowId);

        }else if (!StringUtil.isNotEmpty(workflowId) && StringUtil.isNotEmpty(tradeId)){
            TrdBondChangeTrade002Dto entity = trdBondChangeTrade002Service.queryTradeInfoByTradeId(tradeId);
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
     *
     * 新建界面，转出证券代码下拉框默认显示可转债资产和基金
     *
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getAssetId")
    public Page<SelectOptionDto> getAssetId() {
        Map<String, Object> param = RequestUtil.getPageParam();
        return trdBondChangeTrade001Service.getAssetId(param);
    }

    /**
     *
     * 交易方向为债转股时，转出证券代码下拉框显示可转债资产
     *
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getSecExgId")
    public Page<SelectOptionDto> getSecExgId() {
        Map<String, Object> param = RequestUtil.getPageParam();
        return trdBondChangeTrade002Service.getSecExgId(param);
    }

    /**
     *
     * 交易方向为基金转换时，转出证券代码下拉框显示基金
     *
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getFundId")
    public Page<SelectOptionDto> getFundId() {
        Map<String, Object> param = RequestUtil.getPageParam();
        return trdBondChangeTrade002Service.getFundId(param);
    }

    /**
     *
     * 展示股票代码和基金
     * 显示股票代码和基金代码
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getCAssetId")
    public Page<SelectOptionDto> getCAssetId(){
        Map<String, Object> param = RequestUtil.getPageParam();
        return trdBondChangeTrade002Service.getCAssetId(RequestUtil.getPageParam());
    }

    /**
     *
     * 获取正股代码联想文本框信息
     * 显示股票代码和基金代码
     * <功能详细描述>
     * @return
     * @throws Exception
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("/getStockId")
    public Page<SelectOptionDto> getStockId(){
        Map<String, Object> param = RequestUtil.getPageParam();
        return trdBondChangeTrade001Service.getStockId(RequestUtil.getPageParam());
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
        return trdBondChangeTrade002Service.selectInfoBySecId(map);
    }


    /**
     * 通过产品代码查询证券账户
     *
     * @return 产品代码列表
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("selectInfoByProdId")
    public List<SelectOptionDto> selectInfoByProdId() {
        Map<String,Object> map=RequestUtil.getPageParam();

        return commonBusinessService.getProdId(map);
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
     * @param trdBondChangeTrade002Dto 参数
     * @param result 校验结果
     * @return AjaxResult
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupEdit.class}) TrdBondChangeTrade002Dto trdBondChangeTrade002Dto,
                              BindingResult result) {
        return commonCheck(result, trdBondChangeTrade002Dto.getOperateType());
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
            ValidateGroupEdit.class}) TrdBondChangeTrade002Dto dto, BindingResult br)
        throws Exception {

        if (br.hasErrors()) {
            return new AjaxResult(br);
        }

        ProcessParameter<TrdBondChangeTrade002Dto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdBondChangeTrade002Dto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        processParameter.setPageId(this.getPageId());

        //设置主键
        if(!StringUtil.isNotEmpty(dto.getTradeId())){
            Date sysDate= DateUtil.platDateFromCache();
            dto.setTradeId(getTradeId("JYZQZH", sysDate, "A"));
        }

        workFlowParameter.setPageId(this.getPageId());
        workFlowParameter.setElementId(this.getParam("elementId"));
        workFlowParameter.setBusinessCode(dto.getTradeId());
        workFlowParameter.setBusinessType(DictCodeListExt2.业务类型_证券转换交易.getValue());
        workFlowParameter.setComment((entity) -> WorkFlowUtil.concatComment(
            getMessage("交易编号") + GlobalConstant.COLON + entity.getTradeId(),
            getMessage("交易日") + GlobalConstant.COLON + DateUtil.format(entity.getTradeDate())));

        String msg = trdBondChangeTrade002Service.start(processParameter, dto);

        return new AjaxResult(msg);
    }

    /**
     * 编号生成
     */
    public String getTradeId(String str, Date date, String flag) {
        int  sequences=0;
        // 查询数据库sequences
        if ("A".equals(flag)) {
            sequences = trdBondChangeTrade002Service.querySequences();
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
