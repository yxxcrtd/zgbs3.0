package com.joyintech.tams.component.business.common.aop;

import com.alibaba.fastjson.JSON;
import com.joyintech.background.component.common.pack.common.util.JsonUtils;
import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.tams.component.business.common.service.CommonBusinessService;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.common.risktrial.RiskTrialComm;
import com.joyintech.tams.invest.common.risktrial.dto.RiskTrialRetDto;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 处理风险审批参数
 * author：代志伟
 * date：2022-05-24
 */
@Component
@Aspect
public class RskApprovalHandlerAop {

    @Autowired
    private RiskTrialComm riskTrialComm;

    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(RskApprovalHandlerAop.class);

    @Pointcut("execution(* com.joyintech.tams.invest..*.*.getRuntimeVariables(..))")
    protected void rskApprovalPoint(){

    }

    @AfterReturning(pointcut = "rskApprovalPoint()", returning = "variables")
    public void creditOccupyCheck(JoinPoint joinPoint, Map<String, Object> variables) {
        Object[] args = joinPoint.getArgs();
        if (args[0] instanceof ProcessMark && StringUtil.isNotEmpty(args[1])){
            ProcessMark entity = (ProcessMark)args[0];
            try {
                // 获取画面流程对象
                Map<String,Object> params = JsonUtils.stringToMap(entity.getBusinessData());
                // 获取是否需要走风险审批参数（适用于新规则）,依赖新风险配置的规则
                String rskApproval = String.valueOf(params.get("rskApproval"));
                if (StringUtil.equals(rskApproval, DictCodeList.是否_是.getValue())){
                    variables.put("rskApproval",true);
                }else if (StringUtil.equals(rskApproval, DictCodeList.是否_否.getValue())){
                    variables.put("rskApproval",false);
                }else {
                    // 适用于老的业务规则（不受新的配置规则控制），依赖风险试算结果
                    String riskList = JsonUtils.object2Json(params.get("riskTrialRetDto"));
                    if(StringUtil.isEmpty(riskList)){
                        return;
                    }
                    List<RiskTrialRetDto> riskTrialRetDtoList = new ArrayList<>();
                    riskTrialRetDtoList = JsonUtils.json2List(riskList, RiskTrialRetDto.class);
                    // 提取风险试算结果，是否需要走风险审批
                    setRiskVariables(entity.getWorkflowId(),riskTrialRetDtoList,variables);
                    boolean approveLineOne = (boolean) variables.get(RiskUtil.APPROVE_LINE_ONE);
                    variables.put("rskApproval",approveLineOne);
                }
            } catch (IOException e) {
                logger.error("提取风险试算结果错误:{}",e.getMessage());
            }
        }

    }
    @SuppressWarnings("unchecked")
    private void setRiskVariables(String workflowId, List<RiskTrialRetDto> riskTrialRetDtoList, Map<String, Object> variables) {
        variables.put(RiskUtil.APPROVE_LINE_ONE, false);
        variables.put(RiskUtil.APPROVE_LINE_TWO, false);
        variables.put(RiskUtil.APPROVE_LINE_THR, false);
        if (StringUtil.isEmptyList(riskTrialRetDtoList)){
            return;
        }
        Map<String, List<RiskTrialRetDto>> riskMap = new HashMap<>();
        String key = StringUtil.isNotEmpty(workflowId) ? workflowId : IDGenerator.generate();
        riskMap.put(key, riskTrialRetDtoList);
        Map<String, Object> riskExceedApproveMap = riskTrialComm.getRiskExceedApproveByEntity(riskMap);
        Map<String, Object> resultMap = (Map<String, Object>)riskExceedApproveMap.get(key);
        if (StringUtil.isNotEmpty(resultMap)) {
            variables.putAll(resultMap);
        }
    }


}
