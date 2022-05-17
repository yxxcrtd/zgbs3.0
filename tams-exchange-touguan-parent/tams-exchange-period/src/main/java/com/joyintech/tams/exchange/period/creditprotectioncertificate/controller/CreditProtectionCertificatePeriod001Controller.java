package com.joyintech.tams.exchange.period.creditprotectioncertificate.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.dto.BaseExportRequest;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.CreditProtectionCertificatePeriod001Request;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.CreditProtectionCertificatePeriod001Response;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.service.CreditProtectionCertificatePeriod001Service;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@PageId("CreditProtectionCertificate001")
@RequestMapping("/exchange/period/CreditProtectionCertificate001")
@Api(tags = "信用凭证保护凭证存续期-查询画面")
public class CreditProtectionCertificatePeriod001Controller extends BaseController {

    private final List<String> PINAN_BUSINESS_TYPE_CODES = new ArrayList<String>(4) {{
        add(DictCodeListExt1.平安业务类型_破产.getValue());
        add(DictCodeListExt1.平安业务类型_支付违约.getValue());
        add(DictCodeListExt1.平安业务类型_债务重组.getValue());
        add(DictCodeListExt1.平安业务类型_到期.getValue());
    }};

    private final List<String> STATUS_VALID_CODES = new ArrayList<String>(3) {{
        add(DictCodeListExt1.有效状态_有效.getValue());
        add(DictCodeListExt1.有效状态_撤销.getValue());
        add(DictCodeListExt1.有效状态_流程中.getValue());
    }};

    private final List<String> SETTLE_METHOD_CODES = new ArrayList<String>(2) {{
        add(DictCodeListExt4.结算方式_标的_实物结算.getValue());
        add(DictCodeListExt4.结算方式_标的_现金结算.getValue());
    }};

    @Autowired
    private CreditProtectionCertificatePeriod001Service creditProtectionCertificatePeriod001Service;


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
        //有效状态
        List<SelectOptionDto> statusValidOptions = this.getSelectDataFromDataDic(DictCode.有效状态, null).getOptions().stream()
                .filter(selectOptionDto -> STATUS_VALID_CODES.contains(selectOptionDto.getId()))
                .collect(Collectors.toList());

        initPageParams.put(DictCode.平安业务类型.getValue(), pinanBusinessTypeOption);
        initPageParams.put(DictCode.结算方式_标的.getValue(), settleMethodOptions);
        initPageParams.put(DictCode.有效状态.getValue(), statusValidOptions);
        return new Message<>(initPageParams).success();
    }

    @ApiOperation(value = "列表分页查询", notes = "列表分页查询")
    @ResponseBody
    @PostMapping("findPage")
    public Message<Page<CreditProtectionCertificatePeriod001Response>> findPage(@RequestBody CreditProtectionCertificatePeriod001Request params) {
        return new Message<>(creditProtectionCertificatePeriod001Service.findPage(params));
    }

    @ApiOperation(value = "业务编号下拉框", notes = "业务编号下拉框")
    @ResponseBody
    @PostMapping("findBusiCode")
    public Message<Page<SelectOptionDto>> findBusiCode(@RequestBody CreditProtectionCertificatePeriod001Request params) {
        return new Message<>(creditProtectionCertificatePeriod001Service.findBusiCode(params));
    }

    @ApiOperation(value = "资产代码下拉框", notes = "资产代码下拉框")
    @ResponseBody
    @PostMapping("findFinprodId")
    public Message<Page<SelectOptionDto>> findFinprodId(@RequestBody CreditProtectionCertificatePeriod001Request params) {
        return new Message<>(creditProtectionCertificatePeriod001Service.findFinprodId(params));
    }

    @PostMapping(value = "/export")
    @ResponseBody
    @ApiOperation(value = "列表导出", notes = "列表导出")
    public void export(@RequestBody BaseExportRequest req, HttpServletResponse response) {
        String platDateStr = DateUtil.format(DateUtil.platDateFromCache());
        ExcelUitl.doExport(req, response, "信用保护凭证存续期(" + platDateStr + ")",
                () -> creditProtectionCertificatePeriod001Service.findPage(req.getDto(CreditProtectionCertificatePeriod001Request.class)).getRoot());
    }

    @PostMapping(value = "/revokeCheck")
    @ResponseBody
    @ApiOperation(value = "撤回check", notes = "撤回check")
    public Message<AjaxResult> revokeCheck(@RequestBody List<CreditProtectionCertificatePeriod001Response> dtoList) {
        if (CollectionUtils.isEmpty(dtoList)) {
            return new Message<AjaxResult>().fail().setMessage("请选择一条数据！");
        }
        for (CreditProtectionCertificatePeriod001Response dto : dtoList) {
            if (StringUtil.equals(dto.getPaBussType(), DictCodeListExt1.平安业务类型_到期.getValue())
                    || !DateUtil.isSameDay(dto.getCreditEventHappenedDate(), DateUtil.now())) {
                return new Message<AjaxResult>().fail().setMessage("只有当日的信用事件结算的存续期记录允许撤销！");
            }
            if (!StringUtil.equals(dto.getValidStatus(), DictCodeListExt1.有效状态_有效.getValue())) {
                return new Message<AjaxResult>().fail().setMessage("有效状态为“有效”的数据，才允许撤销！");
            }
            //选择存在流程中撤销交易的数据。
        }
        return new Message<AjaxResult>().success().setMessage("校验通过");
    }

}
