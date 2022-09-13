/*
 * 文件名：PtlDvpFundAllocation002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：etliu
 * 修改时间：2019年3月29日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.portfolio.ptldvpfundallocation.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.constant.DictCodeList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.PtlPortfolioMapper;
import com.joyintech.tams.common.base.entity.PtlPortfolio;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.portfolio.ptldvpfundallocation.dto.PtlDvpFundAllocationDto;
import com.joyintech.tams.invest.portfolio.ptldvpfundallocation.service.PtlDvpFundAllocation002Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;

/**
 * DVP资金调拨新建Controller
 *
 * @author etliu
 * @version 1.0 2019年3月29日
 * @see PtlDvpFundAllocation002Controller
 * @since 1.0
 */
@Controller
@PageId("PtlDvpFundAllocation002")
@RequestMapping("/invest/portfolio/ptldvpfundallocation/PtlDvpFundAllocation002")
public class PtlDvpFundAllocation002Controller extends BaseController {
    
    /**
     * PtlDvpFundAllocation002Service
     */
    @Autowired
    private PtlDvpFundAllocation002Service ptlDvpFundAllocation002Service;
    
    /**
     * 基础Mapper代理Service
     */
    @Autowired
    private PtlPortfolioMapper ptlPortfolioMapper;
    
    /**
     * 业务编号生成类
     */
    @Autowired(required =false)
    public BaseGetBusinessId baseGetBussinessId;

    /**
     * 重写init方法
     * 
     * @param model 模型
     * @return 返回pageId
     */
    @Override
    public String init(Model model) {
        super.setSelectOptionToModel(model, BussinessDictCode.交易方向, BussinessDictCode.币种);
        model.addAttribute("Fin_Type", BussinessDictCodeList.共同业务类型_DVP资金调拨管理.getValue());
        model.addAttribute("YES_OR_NO_N", BussinessDictCodeList.是否_否.getValue());

        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        String orderId = getParam("orderId");
        if (StringUtil.isNotEmpty(workflowId)) {
            PtlDvpFundAllocationDto dto = ptlDvpFundAllocation002Service.getBussinessEntityByWorkflowId(
                workflowId);
            if (dto.getBussTrdTransferOrder() != null) {
                model.addAttribute("stlPayOrdersList",
                    JsonUtils.object2Json(dto.getBussTrdTransferOrder().get(0)));
            }
            else {
                model.addAttribute("tradeId", dto.getTradeId());
                dto.setBusiNo(dto.getOrderId());
            }
            model.addAttribute("entity", dto);
        }
        else if (StringUtil.isNotEmpty(orderId)) {
            PtlDvpFundAllocationDto dto = ptlDvpFundAllocation002Service.getTradeDetailData(
                orderId);
            if (!StringUtil.isEmptyList(dto.getBussTrdTransferOrder())) {
                model.addAttribute("stlPayOrdersList",
                    JsonUtils.object2Json(dto.getBussTrdTransferOrder().get(0)));
            }
            model.addAttribute("tradeId", dto.getTradeId());
            model.addAttribute("entity", dto);
        }
        return super.init(model);
    }
    
    /**
     * 查询收款账号
     * @return 收款账号集合
     * @see
     * @since 1.0
     */
    @RequestMapping("/getCounterAcctNo")
    @ResponseBody
    public List<SelectOptionDto> getCounterAcctNo(){
        return ptlDvpFundAllocation002Service.getCounterAcctNo(RequestUtil.getPageParam());
    }
    
    /**
     * 查询付款账号
     * @return 付款账号集合
     * @see
     * @since 1.0
     */
    @RequestMapping("/getMyAcctNo")
    @ResponseBody
    public List<SelectOptionDto> getMyAcctNo(){
        return ptlDvpFundAllocation002Service.getMyAcctNo(RequestUtil.getPageParam());
    }
    
    /**
     * 获取账户余额
     * @return 账户余额
     * @see
     * @since 1.0
     */
    @RequestMapping("/getAmount")
    @ResponseBody
    public BigDecimal getAmount() {
        return ptlDvpFundAllocation002Service.getAmount(RequestUtil.getPageParam());
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
    public AjaxResult check(@RequestBody @Valid PtlDvpFundAllocationDto dto, BindingResult result) {
        // 划拨金额不能大于付款账户余额
        if (dto.getMyAcctAmount() != null
                && dto.getSettleAmt() != null
                && dto.getMyAcctAmount().compareTo(dto.getSettleAmt()) < 0) {
            AjaxResult ajaxResult = new AjaxResult();
            ValidMessage message = new ValidMessage(
                    getMessage("BUSS_COM_E_0003", getMessage("PtlDvpFundAllocation001.settleAmts"),
                            getMessage("PtlDvpFundAllocation001.myAcctAmount")));
            ajaxResult.setMessage(message.getMessage(), MessageType.确认消息);
            return ajaxResult;
        }
        return commonCheck(result, dto.getOperateType());
    }
    
    /**
     * 保存
     * @param dto dto
     * @param br br
     * @return 结果
     * @throws WorkFlowException 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Valid PtlDvpFundAllocationDto dto, BindingResult br)
        throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        if(StringUtil.isEmpty(dto.getBusiNo())){
            BussinessCodeParameter bussinessCodeParameter = new BussinessCodeParameter(); 
            bussinessCodeParameter.setCommonBusinessType(DictCodeList.共通业务类型_DVP资金调拨管理);
            dto.setTradeId(baseGetBussinessId != null ? baseGetBussinessId.buildBussinessCode(
                bussinessCodeParameter) : IDGenerator.generate());
            if(dto.getBussTrdTransferOrder() != null && dto.getBussTrdTransferOrder().get(0) != null){
                dto.setBusiNo(dto.getBussTrdTransferOrder().get(0).getOrderId());
            }
        }
        dto.setOrderId(dto.getBussTrdTransferOrder().get(0).getOrderId());
        PtlPortfolio ptlPortfolio = ptlPortfolioMapper.selectByPrimaryKey(dto.getPortfolioId());
        if(ptlPortfolio != null){
            dto.setPortfolioName(ptlPortfolio.getPortfolioName());
        }
        ProcessParameter<PtlDvpFundAllocationDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<PtlDvpFundAllocationDto> params = new WorkFlowParameter<>();
        params.setPageId("PtlDvpFundAllocation002");
        params.setElementId(dto.getElementId());
         
        params.setBusinessCode(dto.getTradeId());
        params.setBusinessType(BussinessDictCodeList.共同业务类型_DVP资金调拨管理.getValue());
        // 设置流程业务说明, 组合名称 + "(" + 组合名称 + ")" + "-"  + "组合代码" + "("  + "组合代码"+")"
        params.setComment(
                entity -> this.getMessage("PtlDvpFundAllocation001.portfolioId") + "("
                + entity.getPortfolioId() + ")" + "-"
                + this.getMessage("PtlDvpFundAllocation001.portfolioName") + "("
                + entity.getPortfolioName() + ")"/* + "-"
                + this.getMessage("PtlDvpFundAllocation001.orderId") + "("
                + entity.getBussTrdTransferOrder().get(0).getOrderId() + ")"*/ );
        processParameter.setWorkFlowParameter(params);
        String msg = ptlDvpFundAllocation002Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }
}
