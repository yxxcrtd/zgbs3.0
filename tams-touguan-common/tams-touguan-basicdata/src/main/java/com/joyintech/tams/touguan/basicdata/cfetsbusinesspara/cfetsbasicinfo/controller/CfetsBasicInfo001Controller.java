package com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.cfetsbasicinfo.controller;

import com.alibaba.fastjson.JSON;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.PositionUtil;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.invest.common.dto.common.ExcelValidDto;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.cfetsbasicinfo.service.FinProductSpecialService;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.cfetsbasicinfo.service.FinProductSpecialValidatorService;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.cfetsbasicinfo.dto.FinProductSpecialDto;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.cfetsbasicinfo.dto.FinProductSpecialSelectOptionDto;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.cfetsbasicinfo.dto.req.FinProductSpecialQueryRequest;
import com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.cfetsbasicinfo.dto.req.FinProductSpecialSelectOptionQueryRequest;
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
@RequestMapping("/business/param/CfetsBasicInfo001")
@Api(tags = "银行间基础信息-特殊债券信息")
public class CfetsBasicInfo001Controller extends BaseController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private FinProductSpecialService finProductSpecialService;
    @Autowired
    private FinProductSpecialValidatorService finProductSpecialValidatorService;


    @ApiOperation(value = "查询页面初始化参数", notes = "查询页面初始化参数")
    @PostMapping("/init")
    @ResponseBody
    public Message<Map<String, Object>> init() {
        Map<String, Object> model = new HashMap<>(1);
        model.put("YES_NO", DictCodeList.是否_是.getValue());
        return new Message<>(model).success();
    }

    @ResponseBody
    @PostMapping("/findPage")
    @ApiOperation(value = "特殊债券信息分页查询", notes = "特殊债券信息分页查询")
    public Message<Page<FinProductSpecialDto>> findPage(@RequestBody FinProductSpecialQueryRequest request) {
        logger.info("特殊债券信息分页查询参数：{}", JSON.toJSONString(request));
        return Message.success("success", finProductSpecialService.queryForPage(request));
    }


    @ResponseBody
    @PostMapping("/selectOption")
    @ApiOperation(value = "特殊债券信息简单查询", notes = "特殊债券信息简单查询")
    public Message<Page<FinProductSpecialSelectOptionDto>> selectOption(@RequestBody FinProductSpecialSelectOptionQueryRequest request) {
        logger.info("特殊债券信息简单查询参数：{}", JSON.toJSONString(request));
        return Message.success("success", finProductSpecialService.querySelectOption(request));
    }


    @ApiOperation(value = "特殊债券信息提交(保存)", notes = "特殊债券信息提交(保存)")
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
        params.setBusinessType(BussinessDictCodeList.特殊债券信息业务类型.getValue());
        params.setBusinessCode(dto.getFinprodMarketId());
        String finalCommentStr = "债券代码:" + dto.getFinprodMarketId() + "是否资产支持证券：" + dto.getIsAbs() + "是否特殊债：" + dto.getIsSpecialDebt() + "是否到期违约债：" + dto.getIsDefaultBondIndicator();
        params.setComment(entity -> finalCommentStr);
        processParameter.setWorkFlowParameter(params);
        processParameter.setOperationType(OperationType.NEW);
        processParameter.setPageId(params.getPageId());
        try {
            String msg = finProductSpecialService.start(processParameter, dto);
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
        AjaxResult ajax = new AjaxResult();
        List<ValidMessage> valid = finProductSpecialValidatorService.valid(dto);
        ajax.setErrorList(valid);
        return new Message<AjaxResult>().success().setData(ajax);
    }

    /**
     * 导入数据文件之前检查数据
     *
     * @param fileName 导入的文件名
     * @return 处理结果
     */
    @ResponseBody
    @RequestMapping(value = "checkConfirmMsgForImportData")
    public AjaxResult checkConfirmMsgForImportData(String fileName) {
        return finProductSpecialValidatorService.checkConfirmMsgForImportData(fileName);
    }

    /**
     * 导入数据文件
     *
     * @param fileName 导入的文件名
     * @return 导入结果，包含验证内容，如果验证失败，返回message
     */
    @ResponseBody
    @RequestMapping("/importFile")
    public List<ExcelValidDto> importData(String fileName) {
        return finProductSpecialService.importData(fileName);
    }

}
