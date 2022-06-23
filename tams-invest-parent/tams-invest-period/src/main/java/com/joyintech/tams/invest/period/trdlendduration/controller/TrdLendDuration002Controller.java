/*
 * 文件名：TrdLendDuration002Controller.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：dingzhipeng
 * 修改时间：2018年9月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.period.trdlendduration.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.component.business.core.service.BaseGetBusinessId;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.dto.common.BussinessCodeParameter;
import com.joyintech.tams.invest.period.trdlendduration.dto.TrdLendDuration001Dto;
import com.joyintech.tams.invest.period.trdlendduration.service.TrdLendDuration001Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author dingzhipeng
 * @version 1.0 2018年9月17日
 * @see TrdLendDuration002Controller
 * @since 1.0
 */
@Controller
@PageId("TrdLendDuration002")
@RequestMapping("/invest/period/trdlendduration/TrdLendDuration002")
public class TrdLendDuration002Controller extends BaseController {

    /**
     * service
     */
    @Autowired(required = false)
    private BaseGetBusinessId baseGetBussinessId;

    /**
     * service
     */
    @Autowired
    private TrdLendDuration001Service trdLendDuration001Service;

    /**
     * {@inheritDoc}
     */
    @Override
    public String init(Model model) {
        super.setSelectOptionToModel(model, DictCode.币种, DictCode.计息基础,
            DictCode.回购拆借方向,DictCode.交易场所);
        Map<String, Object> map = RequestUtil.getPageParam();
        TrdLendDuration001Dto entity = null;
        String workflowId = (String)map.get(GlobalConstant.WORKFLOW_ID);
        if (StringUtil.isNotEmpty(workflowId)) {
            entity = trdLendDuration001Service.getBussinessEntityByWorkflowId(workflowId);
        }
        else {
            entity = trdLendDuration001Service.getMyEntity(map);
            entity.setIntRate(ArithmeticUtil.multiply(entity.getIntRate(), new BigDecimal(100)));
        }
        model.addAttribute(GlobalConstant.ENTITY,entity );
        if(CollectionUtils.isNotEmpty(entity.getBussTrdTransferOrder())) {
            model.addAttribute("entity_bussTrdTransferOrderDto", JsonUtils.object2Json(entity.getBussTrdTransferOrder().get(0)));
        }
        model.addAttribute("totleTradeId", entity.getTotleTradeId());
        model.addAttribute("YES_OR_NO_Y", DictCodeList.是否_是.getValue());
        model.addAttribute("BACK_LEND_DIRECTION_CR", DictCodeList.回购拆借方向_拆入.getValue());
        model.addAttribute("BACK_LEND_DIRECTION_CC", DictCodeList.回购拆借方向_拆出.getValue());
        model.addAttribute("GEN_PAY_ORDERS_AUTO", CacheUtil.getSysParam(BussinessConstant.GEN_PAY_ORDERS_AUTO));
        model.addAttribute("BACK_LEND_DIRECTION_01",DictCodeList.回购拆借方向_拆入.getValue());
        model.addAttribute("BACK_LEND_DIRECTION_02",DictCodeList.回购拆借方向_拆出.getValue());
        return super.init(model);
    }

    /**
     * 画面点击保存按钮时check
     *
     * @param dto dto
     * @param result BindingResult
     * @return check结果
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
        ValidateGroupEdit.class}) TrdLendDuration001Dto dto, BindingResult result) {
        return commonCheck(result, dto.getOperateType());
    }

    /**
     * 保存
     * 
     * @param dto 实体类
     * @param br 结果
     * @return 结果集
     * @throws Exception 异常
     */
    @ResponseBody
    @RequestMapping("save")
    public AjaxResult save(@RequestBody @Validated({
        ValidateGroupEdit.class}) TrdLendDuration001Dto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<TrdLendDuration001Dto> processParameter = new ProcessParameter<TrdLendDuration001Dto>();
        WorkFlowParameter<TrdLendDuration001Dto> params = new WorkFlowParameter<TrdLendDuration001Dto>();
        params.setPageId(dto.getPageId());
        params.setElementId(dto.getElementId());
        BussinessCodeParameter parameter = new BussinessCodeParameter();
        parameter.setCommonBusinessType(DictCodeList.共通业务类型_资产存续期交易);
        dto.setTotleTradeId(baseGetBussinessId.buildBussinessCode(parameter));
        dto.setBusiCode(dto.getTotleTradeId());
        params.setBusinessCode(dto.getTotleTradeId());
        params.setBusinessType(DictCodeList.共通业务类型_拆借存续期交易.getValue());
        // 设置流程业务说明, entity即传入的dto
        params.setComment(item -> {
            return WorkFlowUtil.concatComment(getMessage("TrdLendDuration001.tradeId"),
                GlobalConstant.COLON, item.getTotleTradeId(), GlobalConstant.OPEN_PARENTHESIS,
                DictCodeList.共通业务类型_拆借存续期交易.getCodeName(),
                GlobalConstant.CLOSE_PARENTHESIS);
        });
        processParameter.setWorkFlowParameter(params);
        String msg = trdLendDuration001Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }
}
