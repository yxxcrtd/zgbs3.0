package com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dto.FinProductSpecialDto;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.service.FinProductSpecial001Service;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

/**
 * 特殊债券信息
 *
 * @author zhuys
 * @date 2022-05-06
 */
@Controller
@PageId("CfetsBasicInfo002")
@RequestMapping("/business/param/FinProductSpecial002")
@Api(tags = "银行间基础信息-特殊债券信息提交")
public class FinProductSpecial002Controller extends BaseController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FinProductSpecial001Service finProductSpecial001Service;


    @ApiOperation(value = "查询页面初始化参数", notes = "查询页面初始化参数")
    @PostMapping("/init")
    @ResponseBody
    public Message<Map<String, Object>> init() {
        Map<String, Object> model = new HashMap<>(1);
        model.put("pageId", this.getPageId());
        model.put("YES_OR_NO", this.getSelectDataFromDataDic(DictCode.是否, null));

        String workflowId = getParam(GlobalConstant.WORKFLOW_ID);
        FinProductSpecialDto entity = new FinProductSpecialDto();
        if (StringUtil.isNotEmpty(workflowId)) {
            entity = finProductSpecial001Service.getBussinessEntityByWorkflowId(workflowId);
            model.put("businessId", entity.getFinprodMarketId());
        } else {
            String finprodId = getParam("finprodMarketId");
            if (StringUtil.isNotEmpty(finprodId)) {
                //修改
                entity = finProductSpecial001Service.queryByFinprodMarketId(finprodId);
                model.put("businessId", entity.getFinprodMarketId());
            }
        }
        model.put(GlobalConstant.ENTITY, entity);
        return new Message<>(model).success();
    }


    @ApiOperation(value = "特殊债券信息提交(保存)/修改", notes = "特殊债券信息提交(保存)/修改")
    @ResponseBody
    @PostMapping("save")
    public Message save(@RequestBody FinProductSpecialDto dto, BindingResult br) {
        if (br.hasErrors()) {
            return new Message<AjaxResult>().success().setData(new AjaxResult(br));
        }
        ProcessParameter<FinProductSpecialDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<FinProductSpecialDto> params = new WorkFlowParameter<>();
        // 页面id
        params.setPageId(getPageId());
        params.setElementId(dto.getElementId());
        params.setBusinessType(DictCodeListExt2.共通业务类型_银行间基础信息.getValue());
        params.setBusinessCode(dto.getFinprodMarketId());
        String finalCommentStr =
                "债券代码:" + dto.getFinprodMarketId()
                        + "-是否资产支持证券:" + dto.getIsAbsStr()
                        + "-是否特殊债:" + dto.getIsSpecialDebtStr()
                        + "-是否到期违约债:" + dto.getIsDefaultBondIndicatorStr();
        params.setComment(entity -> finalCommentStr);
        processParameter.setWorkFlowParameter(params);
        if (StringUtil.equals(dto.getOperateType(), OperationType.NEW.name())) {
            processParameter.setOperationType(OperationType.NEW);
        } else if (StringUtil.equals(dto.getOperateType(), OperationType.MODIFY.name())) {
            processParameter.setOperationType(OperationType.MODIFY);
        } else {
            processParameter.setOperationType(OperationType.DELETE);
        }
        processParameter.setPageId(params.getPageId());
        try {
            String msg = finProductSpecial001Service.start(processParameter, dto);
            return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
        } catch (Exception e) {
            logger.error("特殊债券信息提交失败", e);
            return PositionUtil.ajaxErrorReturn(e);
        }
    }

    /**
     * 特殊债券信息删除
     *
     * @param finProductSpecialDto
     * @return
     */
    @ApiOperation(value = "特殊债券信息删除", notes = "特殊债券信息删除")
    @PostMapping("/del")
    @ResponseBody
    public Message<AjaxResult> del(@RequestBody FinProductSpecialDto finProductSpecialDto) {
        ProcessParameter<FinProductSpecialDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<FinProductSpecialDto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(finProductSpecialDto.getElementId());
        params.setBusinessCode(FinProductSpecialDto::getFinprodMarketId);
        params.setBusinessType(DictCodeListExt2.共通业务类型_银行间基础信息.getValue());
        finProductSpecialDto.setOperateType(OperationType.DELETE.name());
        processParameter.setOperationType(OperationType.DELETE);
        processParameter.setWorkFlowParameter(params);
        try {
            finProductSpecial001Service.start(processParameter, finProductSpecialDto);
        } catch (Exception e) {
            logger.error("流程发起失败", e);
            return Message.fail("流程发起失败");
        }
        return Message.success("处理成功", null);
    }

}
