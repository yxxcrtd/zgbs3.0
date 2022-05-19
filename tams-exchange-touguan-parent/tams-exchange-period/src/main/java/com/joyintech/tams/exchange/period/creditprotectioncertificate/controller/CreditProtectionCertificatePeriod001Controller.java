package com.joyintech.tams.exchange.period.creditprotectioncertificate.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.RequestUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.dto.BaseExportRequest;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.util.ExcelUitl;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.CreditProtectionCertificatePeriod001Request;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.CreditProtectionCertificatePeriod001Response;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodSaveDto;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.service.CreditProtectionCertificatePeriod001Service;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.service.CreditProtectionCertificatePeriod002Service;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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
@PageId("CreditProtectionCertificatePeriod001")
@RequestMapping("/exchange/period/CreditProtectionCertificatePeriod001")
@Api(tags = "信用凭证保护凭证存续期-查询画面")
public class CreditProtectionCertificatePeriod001Controller extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(CreditProtectionCertificatePeriod001Controller.class);

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

    @Autowired
    private CreditProtectionCertificatePeriod002Service creditProtectionCertificatePeriod002Service;

    @Autowired
    private IPlatformDate iPlatformDate;

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
        try {
            return new Message<>(creditProtectionCertificatePeriod001Service.findPage(params));
        } catch (Exception e) {
            logger.error("列表分页查询数据异常", e);
            return new Message<Page<CreditProtectionCertificatePeriod001Response>>().fail().setMessage("列表分页查询数据异常");
        }
    }

    @ApiOperation(value = "业务编号下拉框", notes = "业务编号下拉框")
    @ResponseBody
    @PostMapping("findBusiCode")
    public Message<Page<SelectOptionDto>> findBusiCode(@RequestBody CreditProtectionCertificatePeriod001Request params) {
        try {
            return new Message<>(creditProtectionCertificatePeriod001Service.findBusiCode(params));
        } catch (Exception e) {
            logger.error("业务编号下拉框数据异常", e);
            return new Message<Page<SelectOptionDto>>().fail().setMessage("业务编号下拉框数据异常");
        }
    }

    @ApiOperation(value = "资产代码下拉框", notes = "资产代码下拉框")
    @ResponseBody
    @PostMapping("findFinprodId")
    public Message<Page<SelectOptionDto>> findFinprodId(@RequestBody CreditProtectionCertificatePeriod001Request params) {
        try {
            return new Message<>(creditProtectionCertificatePeriod001Service.findFinprodId(params));
        } catch (Exception e) {
            logger.error("资产代码下拉框数据异常", e);
            return new Message<Page<SelectOptionDto>>().fail().setMessage("资产代码下拉框数据异常");
        }
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
    public Message<AjaxResult> revokeCheck(@RequestBody CreditProtectionCertificatePeriod001Response dto) {
        try {
            if (null == dto) {
                return new Message<AjaxResult>().fail().setMessage("请选择一条数据！");
            }
            if (StringUtil.equals(dto.getPaBussType(), DictCodeListExt1.平安业务类型_到期.getValue())
                    || !DateUtil.isSameDay(dto.getCreditEventHappenedDate(), DateUtil.now())) {
                return new Message<AjaxResult>().fail().setMessage("只有当日的信用事件结算的存续期记录允许撤销！");
            }
            if (!StringUtil.equals(dto.getValidStatus(), DictCodeListExt1.有效状态_有效.getValue())) {
                return new Message<AjaxResult>().fail().setMessage("有效状态为“有效”的数据，才允许撤销！");
            }
            if (creditProtectionCertificatePeriod001Service.isAlreadyRevoked(dto.getBusiCode())) {
                ValidMessage validMessage = new ValidMessage(getMessage("TRD_W_0001",
                        getMessage("pingan.common.revoke"), getMessage("pingan.common.revoke")));
                ArrayList<ValidMessage> errorList = new ArrayList<ValidMessage>();// 生成错误集合
                errorList.add(validMessage);
                AjaxResult ar = new AjaxResult();
                ar.setErrorList(errorList);
                return new Message<AjaxResult>().success().setData(ar);
            }
            return new Message<AjaxResult>().success().setMessage("校验通过");
        } catch (Exception e) {
            logger.error("撤回check异常", e);
            return new Message<AjaxResult>().fail().setMessage("撤回check异常");
        }
    }

    @PostMapping(value = "/revoke")
    @ResponseBody
    @ApiOperation(value = "撤回", notes = "撤回")
    public Message<AjaxResult> revoke(@RequestBody CreditProtectionCertificatePeriod001Response dto) {
        try {
            if (null == dto) {
                return new Message<AjaxResult>().fail().setMessage("请选择一条数据！");
            }
            ProcessParameter<CreditProtectionCertificatePeriodSaveDto> processParameter = new ProcessParameter<>();
            WorkFlowParameter<CreditProtectionCertificatePeriodSaveDto> params = new WorkFlowParameter<>();
            CreditProtectionCertificatePeriodSaveDto entity = creditProtectionCertificatePeriod002Service.getEntityByBusinessCode(dto.getBusiCode());
            params.setPageId(dto.getPageId());
            params.setElementId(dto.getElementId());
            params.setBusinessCode(dto.getBusiCode());
            params.setBusinessType(DictCodeList.共通业务类型_信用保护凭证存续期.getValue());
            params.setComment(comment -> "业务编号:(" + dto.getBusiCode() + ")-业务类型(" + dto.getPaBussTypeStr() + ")-平台日(" + DateUtil.format(iPlatformDate.getSysDate()) + ")");
            processParameter.setWorkFlowParameter(params);
            String msg = creditProtectionCertificatePeriod002Service.start(processParameter, entity);
            if (StringUtil.isBlank(msg)) {
                return new Message<AjaxResult>().setData(new AjaxResult("撤回成功"));
            }
            return new Message<AjaxResult>().setData(new AjaxResult(msg));
        } catch (Exception e) {
            logger.error("撤回异常", e);
            return new Message<AjaxResult>().fail().setMessage("撤回异常");
        }
    }

}
