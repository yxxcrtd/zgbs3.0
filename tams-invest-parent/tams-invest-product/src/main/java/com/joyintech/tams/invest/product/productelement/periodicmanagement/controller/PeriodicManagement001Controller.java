package com.joyintech.tams.invest.product.productelement.periodicmanagement.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.JoyinMessage;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupDelete;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.product.productelement.periodicmanagement.dto.PeriodicManagement001Dto;
import com.joyintech.tams.invest.product.productelement.periodicmanagement.service.PeriodicManagement001Service;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;


/**
 * 期次管理
 *
 * @param <PeriodicManagement001Dto>
 * @author zhaojintao
 */
@Controller("periodicmanagement001Controller")
@PageId("PeriodicManagement001")
@RequestMapping("/invest/product/productelement/periodicmanagement/PeriodicManagement001")
public class PeriodicManagement001Controller extends BaseController {

    @Autowired
    private PeriodicManagement001Service periodicManagement001Service;

    /**
     * 页面初始化
     */
    @Override
    public String init(Model model) {
        this.setSelectOptionToModel(model, BussinessDictCode.有效状态);

        return super.init(model);
    }

    /**
     * 产品代码联想框
     * <功能详细描述>
     *
     * @param search String
     * @return List<SelectOptionDto>
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("getFinprodIds")
    public Page<SelectOptionDto> getFinprodid() {
        return periodicManagement001Service.getFinprodIds(RequestUtil.getPageParam());
    }

    @ResponseBody
    @RequestMapping("selectForPage")
    public Page<PeriodicManagement001Dto> selectForPage() {
        return periodicManagement001Service.selectForPage(RequestUtil.getPageParam());
    }

    /**
     * 删除按钮点击时的check
     *
     * @param dto    验证的实体类
     * @param result 验证的结果
     * @return 错误信息
     */
    @ResponseBody
    @RequestMapping("check")
    public AjaxResult check(@RequestBody @Validated({
            ValidateGroupDelete.class}) PeriodicManagement001Dto dto, BindingResult br) {
//        AjaxResult result = commonCheck(br, dto.getOperateType());
        /*// 提示信息检查 此判断移至Validator验证类中
        List<JoyinMessage> joyinMessages = new ArrayList<>();
        if (periodicManagement001Service.getTrdProductDealByPeriodId(dto.getPeriodId())) {
            joyinMessages.add(
                new JoyinMessage(this.getMessage("已存在交易的期次不允许删除"), MessageType.错误消息));
            result.setJoyinMessages(joyinMessages);
        } */

        AjaxResult result = new AjaxResult();
        if (StringUtil.equals(dto.getOperateType(), "confrim")) {
            if (periodicManagement001Service.isInWflProcess(dto)) {
                List<JoyinMessage> messages = Lists.newArrayList();
                messages.add(new JoyinMessage(this.getMessage("已存在流程中数据，不能重复确认"), MessageType.错误消息));
                result.setJoyinMessages(messages);
            }
        } else {
            result = commonCheck(br, dto.getOperateType());
        }
        return result;
    }

    /**
     * 删除操作
     *
     * @param dto 验证的实体类
     * @param br  错误结果
     * @return 错误信息
     * @throws WorkFlowException 异常
     */
    @ResponseBody
    @RequestMapping("delete")
    public AjaxResult delete(@RequestBody @Validated({
            ValidateGroupDelete.class}) PeriodicManagement001Dto dto, BindingResult br) throws WorkFlowException {
        if (br.hasErrors()) {
            return new AjaxResult(br);
        }
        ProcessParameter<PeriodicManagement001Dto> processParameter = new ProcessParameter<PeriodicManagement001Dto>();
        WorkFlowParameter<PeriodicManagement001Dto> params = new WorkFlowParameter<PeriodicManagement001Dto>();
        params.setPageId("PeriodicManagement002");
        params.setElementId(dto.getElementId());
        params.setBusinessCode(dto.getPeriodId());
        params.setBusinessType(DictCodeListExt2.共通业务类型_产品期次管理.getValue());
        //业务说明：画面.金融产品代码 +“（” + 画面.金融产品代码+“）” +“投资周期（”+画面.投资周期 +“）”
        params.setComment(
                entity -> this.getMessage("FinSubmittedModule001.finprodId") + "(" + entity.getFinprodId() + ")-"
                        + this.getMessage("FinProductBmQuotation001.investmentCycle")
                        + "(" + DateUtil.getBetweenDayNumber(entity.getStartDate(), entity.getEndDate()) + "天)");
        processParameter.setWorkFlowParameter(params);
        String msg = periodicManagement001Service.start(processParameter, dto);
        return new AjaxResult(msg);
    }

    /**
     * 导出信息excel
     *
     * @return excel
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    @ResponseBody
    @RequestMapping("export")
    public ResponseEntity<byte[]> export() {
        return periodicManagement001Service.export(this.getPageId(),
                RequestUtil.getParameters(getRequest()));
    }
}

