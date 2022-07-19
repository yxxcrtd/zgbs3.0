/*
 * 文件名：WorkflowDobackAspect.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2019年10月16日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.common.workflow.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.base.entity.ProcessMark;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.microservice.ServiceApplication;
import com.joyintech.tams.exchange.common.microservice.TradeServiceURL;
import com.joyintech.workflow.engine.WorkflowContext;
import com.joyintech.workflow.engine.entity.FlowEntity;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * 监视工作流退回
 *
 */
@Component("TrdExchangeWorkflowDobackAspect")
@Aspect
public class TrdExchangeWorkflowDobackAspect extends BaseService {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Pointcut("execution(public * com.joyintech.workflow.engine.service.ProcessService.rollbackProcess(..))")
    protected void doBack() {
        // just point
    }

    @Pointcut("execution(public * com.joyintech.workflow.engine.controller.WorkFlowController.doCancel(..))")
    protected void doCancel() {
        // just point
    }

    @Pointcut("execution(public * com.joyintech.workflow.engine.controller.WorkFlowController.doAbandon(..))")
    protected void doAbandon() {
        // just point
    }

    @Pointcut("execution(public * com.joyintech.workflow.engine.controller.WorkFlowController.revoke(..))")
    protected void revoke() {
        // just point
    }

    @After("doBack()")
    public void afterDoBack(JoinPoint joinPoint) throws WorkFlowException {
        Object[] args = joinPoint.getArgs();
        String processInstanceId = (String) args[0];
        String operatorUserId = (String) args[1];
        String rejectReason = (String) args[2];
        String selectActivitiId = (String) args[3];
        String selectActivitiName = (String) args[4];
        logger.info(
                "流程退回:[processInstanceId:{},operatorUserId:{},rejectReason:{},selectActivitiId:{},selectActivitiName:{}]",
                processInstanceId, operatorUserId, rejectReason, selectActivitiId, selectActivitiName);

//        if (StringUtil.isNoneEmpty(rejectReason) || "无".equals(rejectReason) ){
//            logger.error("退回原因不能为空");
//            return;
//        }

        ProcessMark mark = null;
        try {
            FlowEntity entity = WorkflowContext.getProcessService().getRuntimeEntity(
                    processInstanceId);
            String wkfId = entity.getWorkflowId();
            mark = WorkflowContext.getProcessMarkService().getBusinessInfo(wkfId);
        } catch (WorkFlowException e) {
            logger.error("流程取值异常", e);
            throw e;
        }
        if (mark == null) {
            logger.info("流程数据不存在");
            return;
        }
        String pageId = mark.getPageId();
        String eventId = mark.getBusinessDataPk1();
        // 下单审批
        if (StringUtil.equals(pageId, "TrdExchange002")) {
            sendApprovalResultToExchange(eventId, rejectReason, TradeServiceURL.examineOrderReview);
        } else if (StringUtil.equals(pageId, "TrdExchange003")) {
            sendApprovalResultToExchange(eventId, rejectReason, TradeServiceURL.examineIntegratedEntrustReview);
        } else if (StringUtil.equals(pageId, "TrdExchange004")) {
            sendApprovalResultToExchange(eventId, rejectReason, TradeServiceURL.examineEntrustRevoke);
        } else if (StringUtil.equals(pageId, "TrdExchange005")) {
            sendApprovalResultToExchange(eventId, rejectReason, TradeServiceURL.examineEntrustRevoke);
        } else {

        }
    }

    protected void sendApprovalResultToExchange(String eventId, String rejectReason, String url) {
        logger.info("通知交易系统审批状态接口URL:[{}{}{}] " , ServiceApplication.EXCHANGE_TRADE_SERVICE_NAME,
                TradeServiceURL.prefix, url);

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("eventId", eventId);
        paramMap.put("approveResult", "2");
        logger.info("请求报文:[eventId = {}; approveResult = 2; approveReason = {}]", eventId, rejectReason);
        Map<String, Object> result = restTemplate.postForObject(
                ServiceApplication.EXCHANGE_TRADE_SERVICE_NAME + TradeServiceURL.prefix + url + "?eventId=" + eventId
                        +"&approveResult=2&approveReason=" + rejectReason,
                    paramMap,
                    Map.class);
        logger.info("应答报文:[errorCode:{};errorMsg:{}]", result.get("errorCode"), result.get("errorMsg"));

    }

    @After("doAbandon()")
    public void afterDoAbandon(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String taskId = (String) args[0];
        logger.info("放弃任务:[taskId:{}]", taskId);
    }

    @After("revoke()")
    public void afterRevoke(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String processInstanceId = (String) args[0];
        logger.info("流程撤回:[processInstanceId:{}]", processInstanceId);
    }

    @After("doCancel()")
    public void afterDoCancel(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String workflowId = (String) args[0];
        String processInstanceId = (String) args[1];
        String comment = (String) args[2];
        logger.info("流程作废:[processInstanceId:{},workflowId:{},comment:{}]", processInstanceId,
                workflowId, comment);
    }
}
