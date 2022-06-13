package com.joyintech.tams.cfets.risk.controller;


import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribdoRiskcalResponse;
import com.joyintech.tams.cfets.risk.service.RiskInputParamSerivce;
import com.joyintech.tams.component.business.riskall.dto.RiskCommonOutputDto;
import com.joyintech.tams.constant.DictCodeListRsk;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.joyintech.tams.constant.DictCodeListExt3.*;

/**
 * 风险升级 4合一接口
 */
@RestController
@RequestMapping("/riskTrial")
public class RiskTrialController {

    private static final Logger logger = LoggerFactory.getLogger(RiskTrialController.class);

    @Autowired
    private RiskInputParamSerivce paramSerivce;


    @ApiOperation(value = "风险试算", notes = "风险试算")
    @PostMapping("/doRiskcal/{pageId}")
    public Message<AjaxResult> doRiskcal(@RequestBody Map<String, Object> param, @PathVariable("pageId") String pageId) {
        try {
            RiskCommonOutputDto outputDto = paramSerivce.buildParamThenRisk(param, 系统动作_修改.getValue(), 流程节点_流程提交.getValue(), pageId);
            CfetsEtribdoRiskcalResponse result = new CfetsEtribdoRiskcalResponse();
            result.setRiskMathFlag("02");
            AjaxResult m = new AjaxResult();
            //TODO 目前只支持指标的试算结果返回,后期支持4种类型的试算结果返回
            if (outputDto != null && CollectionUtils.isNotEmpty(outputDto.getRiskTrialRetDto())) {
                List<CfetsEtribdoRiskcalResponse> resultList = new ArrayList<>();
                result.setRskStatus(outputDto.getRskTrialStatus());
                if (StringUtil.equals(outputDto.getRskTrialStatus(), "3")) {
                    result.setRiskTrialSpan(DictCodeListRsk.试算结果_不通过.getCodeName());
                } else {
                    result.setRiskTrialSpan(DictCodeListRsk.试算结果_通过.getCodeName());
                }
                outputDto.getRiskTrialRetDto().forEach(e -> {
                    CfetsEtribdoRiskcalResponse riskcalResponse = new CfetsEtribdoRiskcalResponse();
                    BeanUtil.copyPropertiesIgnoreNull(riskcalResponse, e);
                    resultList.add(riskcalResponse);
                });
                result.setRiskcalReturnList(resultList);
                m.setData(result);
                return new Message<AjaxResult>().success().setData(m);
            } else {//没有试算结果时默认也给通过，针对没有配置指标试算的场景
                result.setRskStatus(DictCodeListRsk.试算结果_通过.getValue());
                result.setRiskTrialSpan(DictCodeListRsk.试算结果_通过.getCodeName());
                return new Message<AjaxResult>().success().setData(m);
            }
        } catch (Exception e) {
            logger.error("风险试算异常", e);
            return new Message<AjaxResult>().fail().setMessage("风险试算异常");
        }

    }
}
