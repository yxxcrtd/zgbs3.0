package com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.dto.CfetsExportRequestDto;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.business.common.util.ValidUtils;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.common.ExcelValidDto;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dto.FinProductSpecialDto;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dto.FinProductSpecialSelectOptionDto;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dto.req.FinProductSpecialQueryRequest;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dto.req.FinProductSpecialSelectOptionQueryRequest;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.service.FinProductSpecial001Service;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.service.FinProductSpecialValidatorService;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 特殊债券信息
 *
 * @author zhuys
 * @date 2022-05-06
 */
@Controller
@PageId("CfetsBasicInfo001")
@RequestMapping("/business/param/FinProductSpecial001")
@Api(tags = "银行间基础信息-特殊债券信息")
public class FinProductSpecial001Controller extends BaseController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FinProductSpecial001Service finProductSpecial001Service;
    @Autowired
    private FinProductSpecialValidatorService finProductSpecialValidatorService;


    @ApiOperation(value = "查询页面初始化参数", notes = "查询页面初始化参数")
    @PostMapping("/init")
    @ResponseBody
    public Message<Map<String, Object>> init() {
        Map<String, Object> model = new HashMap<>(1);
        model.put("pageId", this.getPageId());
        model.put("YES_OR_NO", this.getSelectDataFromDataDic(DictCode.是否, null));
        model.put("EXCEL_TEMPLATE_URL", "");

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

    @ResponseBody
    @PostMapping("/findPage")
    @ApiOperation(value = "特殊债券信息分页查询", notes = "特殊债券信息分页查询")
    public Message<Page<FinProductSpecialDto>> findPage(@RequestBody FinProductSpecialQueryRequest request) {
        logger.info("特殊债券信息分页查询参数：{}", JsonUtils.object2Json(request));
        return Message.success("success", finProductSpecial001Service.queryForPage(request));
    }


    @ResponseBody
    @PostMapping("/selectOption")
    @ApiOperation(value = "特殊债券信息下拉框信息查询", notes = "特殊债券信息下拉框信息查询")
    public Message<Page<FinProductSpecialSelectOptionDto>> selectOption(@RequestBody FinProductSpecialSelectOptionQueryRequest request) {
        logger.info("特殊债券信息简单查询参数：{}", JsonUtils.object2Json(request));
        return Message.success("success", finProductSpecial001Service.querySelectOption(request));
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
        params.setPageId("CfetsBasicInfo001");
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
     * 特殊债券信息提交检查
     *
     * @param dto
     * @param result
     * @return
     */
    @ApiOperation(value = "特殊债券信息提交检查", notes = "特殊债券信息提交检查")
    @PostMapping("/check")
    @ResponseBody
    public Message<AjaxResult> check(@RequestBody FinProductSpecialDto dto, BindingResult result) {
        if (result != null && result.hasErrors()) {
            return new Message<AjaxResult>().success().setData(new AjaxResult(result));
        }
        List<ValidMessage> valid = finProductSpecialValidatorService.valid(dto);
        if (CollectionUtils.isNotEmpty(valid)) {
            return Message.fail(ValidUtils.getValidMessage(valid));
        } else {
            return Message.success("校验成功", null);
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


    /**
     * 特殊债券信息
     *
     * @param req
     * @param response
     */
    @ResponseBody
    @PostMapping(value = "/export")
    @ApiOperation(value = "特殊债券信息列表导出", notes = "特殊债券信息列表导出")
    public void export(@RequestBody CfetsExportRequestDto req, HttpServletResponse response) {
        ExcelUitl.doExport(req, response, "特殊债券信息", () ->
                finProductSpecial001Service.queryForPage(req.getDto(FinProductSpecialQueryRequest.class)).getRoot()
        );
    }


    /**
     * 导入数据文件之前检查数据
     *
     * @param fileName 导入的文件名
     * @return 处理结果
     */
    @ResponseBody
    @PostMapping(value = "checkConfirmMsgForImportData")
    @ApiOperation(value = "导入数据文件之前检查数据", notes = "导入数据文件之前检查数据")
    public Message<AjaxResult> checkConfirmMsgForImportData(String fileName) {
        return Message.success("处理成功", finProductSpecialValidatorService.checkConfirmMsgForImportData(fileName));
    }

    /**
     * 导入数据文件
     *
     * @param fileName 导入的文件名
     * @return 导入结果，包含验证内容，如果验证失败，返回message
     */
    @ResponseBody
    @PostMapping("/importFile")
    @ApiOperation(value = "导入数据文件", notes = "导入数据文件")
    public Message<List<ExcelValidDto>> importData(String fileName) {
        return Message.success("处理成功", finProductSpecial001Service.importData(fileName));
    }

}
