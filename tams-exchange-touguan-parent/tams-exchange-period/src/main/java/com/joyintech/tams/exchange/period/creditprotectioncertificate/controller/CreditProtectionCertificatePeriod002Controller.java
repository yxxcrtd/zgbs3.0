package com.joyintech.tams.exchange.period.creditprotectioncertificate.controller;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseController;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.framework.validation.groups.ValidateGroupEdit;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt4;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dao.CreditProtectionCertificatePeriodMapper;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.BillingDetailsDto;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.CreditProtectionCertificatePeriod001Request;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodSaveDto;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.FinprodLinkDto;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.service.CreditProtectionCertificatePeriod002Service;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.validator.CreditProtectionCertificatePeriodSaveDtoValidator;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@PageId("CreditProtectionCertificatePeriod002")
@RequestMapping("/exchange/period/CreditProtectionCertificatePeriod002")
@Api(tags = "信用凭证保护凭证存续期-新建画面")
public class CreditProtectionCertificatePeriod002Controller extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(CreditProtectionCertificatePeriod002Controller.class);

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
    private CreditProtectionCertificatePeriod002Service creditProtectionCertificatePeriod002Service;

    @Autowired
    private CreditProtectionCertificatePeriodSaveDtoValidator creditProtectionCertificatePeriodSaveDtoValidator;

    @Autowired
    private CreditProtectionCertificatePeriodMapper creditProtectionCertificatePeriodMapper;


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

        Date platformDate = DateUtil.platDateFromCache();
        initPageParams.put("iPlatformDate", DateUtil.format(platformDate));

        //流程id
        String businessCode = "null".equals(getParam("businessCode")) ? null : getParam("businessCode");
        if (StringUtil.isNotEmpty(businessCode)) {
            ProcessMark processMark = new ProcessMark();
            processMark.setPageId(this.getPageId());
            processMark.setBusinessDataPk1(businessCode);
            CreditProtectionCertificatePeriodSaveDto dto = creditProtectionCertificatePeriod002Service.getBussinessEntityByPk(processMark);
//            CreditProtectionCertificatePeriodSaveDto dto = creditProtectionCertificatePeriod002Service.getEntityByBusinessCode(businessCode);
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
        try {
            AjaxResult ajaxResult = new AjaxResult();
            List<ValidMessage> validMessages = creditProtectionCertificatePeriodSaveDtoValidator.valid(dto);
            if (!validMessages.isEmpty()) {
                ajaxResult.setErrorList(validMessages);
                return new Message<AjaxResult>().success().setData(ajaxResult);
            }
            //保护凭证的名义本金数量超过组合下可用数量
            //债券交付数量不超过组合下的债券可用数量
            boolean isError = StringUtil.equals(CacheUtil.getSysParam("AMOUNT_CONTROL_STL"), "01");
            List<BillingDetailsDto> billingDetails = dto.getBillingDetails();
            StringBuilder confirmMessage = new StringBuilder();
            String newSecPositionSwitch = CacheUtil.getSysParam("NEW_SEC_POSITION_SWITCH");
            Date platformDate = DateUtil.platDateFromCache();
            for (BillingDetailsDto billingDetail : billingDetails) {
                BigDecimal amount;
                if (DictCodeList.是否_否.getValue().equals(newSecPositionSwitch)) {
                    amount = creditProtectionCertificatePeriodMapper.getAmountByPortfolioIdOld(dto.getFinprodMarketId(), billingDetail.getPortfolioId(), platformDate);
                } else { // 默认查新头寸
                    amount = creditProtectionCertificatePeriodMapper.getAmountByPortfolioId(dto.getFinprodMarketId(), billingDetail.getPortfolioId(), platformDate);
                }
                if (billingDetail.getRepNomCapi().compareTo(amount) > 0) {
                    if (isError) {
                        ValidMessage validMessage = new ValidMessage();
                        validMessage.setField("repNomCapi");
                        validMessage.setMessage("保护凭证的名义本金数量不可超过组合下可用数量");
                        validMessages.add(validMessage);
                    } else {
                        confirmMessage.append("保护凭证的名义本金数量不可超过组合下可用数量;");
                    }
                }
                if (billingDetail.getDeliverBondAmount() != null && billingDetail.getDeliveryAmount().compareTo(amount) > 0) {
                    if (isError) {
                        ValidMessage validMessage = new ValidMessage();
                        validMessage.setField("deliverBondAmount");
                        validMessage.setMessage("债券交付数量不超过该组合下的债券可用数量");
                        validMessages.add(validMessage);
                    } else {
                        confirmMessage.append("债券交付数量不超过该组合下的债券可用数量;");
                    }
                }
                if (confirmMessage.length() > 0 || !validMessages.isEmpty()) {
                    break;
                }
            }

            if (!validMessages.isEmpty()) {
                ajaxResult.setErrorList(validMessages);
            } else if (confirmMessage.length() > 0) {
                ajaxResult.setMessageType(MessageType.确认消息);
                ajaxResult.setData(confirmMessage);
            } else {
                ajaxResult.setMessage("校验成功");
            }
            return new Message<AjaxResult>().success().setData(ajaxResult);
        } catch (Exception e) {
            logger.error("check异常", e);
            return new Message<AjaxResult>().fail().setMessage("check异常");
        }
    }

    @ApiOperation(value = "保存提交", notes = "保存提交")
    @PostMapping("save")
    @ResponseBody
    public Message<AjaxResult> save(@RequestBody CreditProtectionCertificatePeriodSaveDto dto, BindingResult br)
            throws Exception {
        try {
            dto.setPaBussType(dto.getSettleMethod());

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
                return new Message<AjaxResult>().success().setData(new AjaxResult("保存成功"));
            }
            return new Message<AjaxResult>().success().setData(new AjaxResult(msg));
        } catch (Exception e) {
            logger.error("保存失败", e);
            return new Message<AjaxResult>().fail().setData(new AjaxResult("保存失败",MessageType.错误消息));
        }
    }

    @ApiOperation(value = "资产信息联动", notes = "资产信息联动")
    @ResponseBody
    @PostMapping("finprodIdLink")
    public Message<FinprodLinkDto> finprodIdLink(@RequestBody CreditProtectionCertificatePeriod001Request params) {
        try {
            if (StringUtil.isEmpty(params.getFinprodId())) {
                return new Message<FinprodLinkDto>().fail().setMessage("缺少参数 finprodId");
            }
            return new Message<>(creditProtectionCertificatePeriod002Service.finprodIdLink(params));
        } catch (BusinessException e1) {
            logger.error(e1.getMessage(), e1);
            return new Message<FinprodLinkDto>().fail().setMessage("资产信息不存在");
        } catch (Exception e) {
            logger.error("资产信息联动异常", e);
            return new Message<FinprodLinkDto>().fail().setMessage("资产信息联动异常");
        }
    }


}
