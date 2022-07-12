package com.joyintech.tams.exchange.period.risk.controller;


import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.dto.CreditProtectionCertificatePeriodSaveDto;
import com.joyintech.tams.exchange.period.creditprotectioncertificate.service.CreditProtectionCertificatePeriod002Service;
import com.joyintech.tams.exchange.period.risk.service.PeriodRiskInputParamService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 风险升级 4合一接口
 */
@RestController
@RequestMapping("/")
public class PeriodRiskTrialController {

    private static final Logger logger = LoggerFactory.getLogger(PeriodRiskTrialController.class);

    @Autowired
    private PeriodRiskInputParamService inputParamSerivce;

    @Autowired
    private CreditProtectionCertificatePeriod002Service creditProtectionCertificatePeriod002Service;

    @ApiOperation(value = "风险占用", notes = "风险占用")
    @PostMapping("interf/risk/riskTrial/doOccupy/exchangeTrd/{pageId}/{decisionAct}/{flowStatus}")
    public Message<AjaxResult> doOccupy(@RequestBody Map<String, Object> param,
                                        @PathVariable("pageId") String pageId,
                                        @PathVariable("decisionAct") String decisionAct,
                                        @PathVariable("flowStatus") String flowStatus) {
        try {
            if(StringUtil.equals(pageId,"CreditProtectionCertificatePeriod002")){
                String tradeId = String.valueOf(param.get("totleTradeId"));
                CreditProtectionCertificatePeriodSaveDto entity = creditProtectionCertificatePeriod002Service.getEntityByBusinessCode(tradeId);
                if (entity != null){
                    inputParamSerivce.buildParamThenOccupy(entity, decisionAct, flowStatus, pageId);
                }
                return new Message<AjaxResult>().success();
            }else {
                inputParamSerivce.buildParamThenOccupy(param, decisionAct, flowStatus, pageId);
            }
            return new Message<AjaxResult>().success();
        } catch (Exception e) {
            logger.error("风险占用更新异常", e);
            return new Message<AjaxResult>().fail().setMessage("风险占用更新异常");
        }
    }

}
