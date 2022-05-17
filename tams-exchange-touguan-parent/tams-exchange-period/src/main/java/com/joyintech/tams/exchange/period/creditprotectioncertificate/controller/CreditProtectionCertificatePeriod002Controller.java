package com.joyintech.tams.exchange.period.creditprotectioncertificate.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodSaveDto;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.service.CreditProtectionCertificatePeriod001Service;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.service.CreditProtectionCertificatePeriod002Service;
import com.joyintech.tams.invest.common.dto.period.common.BaseTotalTradeInfoDto;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@PageId("CreditProtectionCertificate002")
@RequestMapping("/exchange/period/CreditProtectionCertificate002")
@Api(tags = "信用凭证保护凭证存续期-新建画面")
public class CreditProtectionCertificatePeriod002Controller extends BaseController {

    private final List<String> PINAN_BUSINESS_TYPE_CODES = new ArrayList<String>(4) {{
        add(DictCodeListExt1.平安业务类型_破产.getValue());
        add(DictCodeListExt1.平安业务类型_支付违约.getValue());
        add(DictCodeListExt1.平安业务类型_债务重组.getValue());
//        add(DictCodeListExt1.平安业务类型_到期.getValue());
    }};

    private final List<String> SETTLE_METHOD_CODES = new ArrayList<String>(2) {{
        add(DictCodeListExt4.结算方式_标的_实物结算.getValue());
        add(DictCodeListExt4.结算方式_标的_现金结算.getValue());
    }};

    @Autowired
    private CreditProtectionCertificatePeriod001Service creditProtectionCertificatePeriod001Service;

    @Autowired
    private CreditProtectionCertificatePeriod002Service creditProtectionCertificatePeriod002Service;


    @ApiOperation(value = "查询画面初始化参数", notes = "查询画面初始化参数")
    @PostMapping("/getPageParams")
    @ResponseBody
    public Message<Map<String, Object>> getPageParams() {
        Map<String, Object> initPageParams = new HashMap<>();
        //业务类型
        List<SelectOptionDto> pinanBusinessTypeOption = this.getSelectDataFromDataDic(DictCode.平安业务类型, null).getOptions().stream()
                .filter(selectOptionDto -> PINAN_BUSINESS_TYPE_CODES.contains(selectOptionDto.getId()))
                .collect(Collectors.toList());
        //结算方式
        List<SelectOptionDto> settleMethodOptions = this.getSelectDataFromDataDic(DictCode.结算方式_标的, null).getOptions().stream()
                .filter(selectOptionDto -> SETTLE_METHOD_CODES.contains(selectOptionDto.getId()))
                .collect(Collectors.toList());

        initPageParams.put(DictCode.平安业务类型.getValue(), pinanBusinessTypeOption);
        initPageParams.put(DictCode.结算方式_标的.getValue(), settleMethodOptions);

        //流程id
        String workflowId = "null".equals(getParam(GlobalConstant.WORKFLOW_ID)) ? null : getParam(GlobalConstant.WORKFLOW_ID);
        initPageParams.put("workflowId", workflowId);
        if (StringUtil.isNotEmpty(workflowId)) {
            CreditProtectionCertificatePeriodSaveDto dto = creditProtectionCertificatePeriod002Service.getBussinessEntityByWorkflowId(workflowId);
            if (null != dto) {
                initPageParams.put(GlobalConstant.ENTITY, dto);
            }
        }
        return new Message<>(initPageParams).success();
    }

    @ApiOperation(value = "check", notes = "check")
    @PostMapping("check")
    @ResponseBody
    public Message<AjaxResult> check(@RequestBody @Validated({ValidateGroupEdit.class}) CreditProtectionCertificatePeriodSaveDto dto, BindingResult br)
            throws Exception {
        return new Message<>(commonCheck(br, dto.getOperateType()));
    }

    @ApiOperation(value = "保存提交", notes = "保存提交")
    @PostMapping("save")
    @ResponseBody
    public Message<AjaxResult> save(@RequestBody CreditProtectionCertificatePeriodSaveDto dto, BindingResult br)
            throws Exception {
        ProcessParameter<CreditProtectionCertificatePeriodSaveDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<CreditProtectionCertificatePeriodSaveDto> params = new WorkFlowParameter<>();
        params.setPageId(this.getPageId());
        params.setElementId(dto.getElementId());
        if (StringUtil.isBlank(dto.getBusiCode())) {
            String busiCode = creditProtectionCertificatePeriod002Service.generateBusiCode(dto);
            dto.setBusiCode(busiCode);
        }
        params.setBusinessCode(dto.getBusiCode());
        params.setBusinessType(DictCodeList.共通业务类型_信用保护凭证存续期.getValue());
        params.setComment(creditProtectionCertificatePeriod002Service.generateComment(dto));
        processParameter.setWorkFlowParameter(params);
        String msg = creditProtectionCertificatePeriod002Service.start(processParameter, dto);
        if (StringUtil.isBlank(msg)) {
            return new Message<AjaxResult>().success();
        }
        return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
    }


}
