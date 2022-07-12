package com.joyintech.tams.exchange.entrust.risk.aop;

import com.google.common.collect.Lists;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.business.common.util.ThreadLocalUtil;
import com.joyintech.tams.component.business.riskall.constant.NodeConfigConstant;
import com.joyintech.tams.component.business.riskall.dto.RiskCommonOutputDto;
import com.joyintech.tams.exchange.entrust.risk.service.ExchangeRiskInputParamService;
import com.joyintech.workflow.engine.constant.OperationType;
import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.joyintech.framework.base.BaseDictCodeList.是否_是;
import static com.joyintech.tams.constant.DictCodeListExt3.*;
import static com.joyintech.tams.constant.DictCodeListRsk.试算结果_风险审批;

@Component
@Aspect
public class ExchangeEntrustHandlerAop {


    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(ExchangeEntrustHandlerAop.class);

    @Autowired
    private ExchangeRiskInputParamService inputParamSerivce;


    /**
     * 保存至代办，从代办中提交到流程中，需要执行风险占用(暂时只切了信用保护凭证，后续新增其他业务时，需要调整切点表达式)
     */
    @Pointcut("execution(* com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer..*.checkSaveError(..)) ")
    protected void checkSaveError() {

    }

    /**
     * 保存至代办，从代办中提交到流程中，需要执行风险占用(暂时只切了信用保护凭证，后续新增其他业务时，需要调整切点表达式)
     */
    @Pointcut("execution(* com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer..*.*.doUpdate(..))")
    protected void updatePoint() {
    }

    /**
     * 不保存至代办，提交到流程中，需要执行风险占用(暂时只切了信用保护凭证，后续新增其他业务时，需要调整切点表达式)
     */
    @Pointcut("execution(* com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer..*.*.doInsert(..))")
    protected void insertPoint() {
    }

    /**
     * 不保存至代办，提交到流程中，需要执行风险占用(暂时只切了信用保护凭证，后续新增其他业务时，需要调整切点表达式)
     */
    @Pointcut("execution(* com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer..*.*.doComplete(..))")
    protected void completePoint() {
    }

    /**
     * 执行指令关闭操作需要释放授信(暂时只切了信用保护凭证，后续新增其他业务时，需要调整切点表达式)
     */
    @Pointcut("execution(* com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer.service.*.closeDelegate(..))")
    protected void closePoint() {
    }

    /**
     * 作废操作,释放授信(暂时只切了信用保护凭证，后续新增其他业务时，需要调整切点表达式)
     */
    @Pointcut("execution(* com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer..*.*.doDelete(..))")
    protected void deletePoint() {
    }

    /**
     * 买入、申购等交易方向提交时需要处理风险占用，操作类型submit
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning(pointcut = "checkSaveError()", returning = "ajaxResultMessage")
    public void checkSaveError(JoinPoint joinPoint, Message<AjaxResult> ajaxResultMessage) throws BusinessException {
        AjaxResult ajaxResult = ajaxResultMessage.getData();
        if (ajaxResult != null && !StringUtil.isEmptyList(ajaxResult.getErrorList())) {
            return;
        }
        Object[] args = joinPoint.getArgs();
        Map<String, Object> map = JsonUtils.object2Map(args[0]);
        String operateType = String.valueOf(map.get("optType"));
        if(StringUtil.equals("dbclick",operateType)){
            return;
        }
        AjaxResult result = new AjaxResult();
        String message = ajaxResult.getData() == null ? "" : ajaxResult.getData().toString();
        result.setMessage(message, MessageType.确认消息);
        try {
            RiskCommonOutputDto outputDto = inputParamSerivce.buildParamThenRisk(args[0], 系统动作_修改.getValue(), 流程节点_流程提交.getValue(), null, NodeConfigConstant.ENTRUST);
            if (StringUtil.isNotEmpty(outputDto.getErrorMsg())) {
                result.setErrorList(Lists.newArrayList(new ValidMessage(outputDto.getErrorMsg())));
                ajaxResultMessage.setData(result);
            } else if (StringUtil.isNotEmpty(outputDto.getConfirmMsg())) {
                message += outputDto.getConfirmMsg();
                result.setMessage(message, MessageType.确认消息);
                if (StringUtil.equals(outputDto.getRskStatus(), 试算结果_风险审批.getValue())) {
                    result.setData(是否_是.getValue());
                }
                ajaxResultMessage.setData(result);
            }
        } catch (Exception e) {
            logger.error("风险校验异常:", e);
            result.setErrorList(Lists.newArrayList(new ValidMessage("风险校验异常")));
            ajaxResultMessage.setData(result);
        }
    }

    /**
     * 指令新建,操作类型submit
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning(pointcut = "insertPoint()")
    public void occupyOnInsert(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        Map<String, Object> map = JsonUtils.object2Map(args[0]);
        String operateType = String.valueOf(map.get("operateType"));
        try {
            if (StringUtil.equals("submit", operateType) || StringUtil.equals("SUBMIT", operateType)) {
                if (StringUtil.equals(args[1].toString(), OperationType.NEW.toString())) {
                    inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_新建.getValue(), 流程节点_流程提交.getValue(), null);
                } else {
                    inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_修改.getValue(), 流程节点_流程提交.getValue(), null);
                }
            }
        } catch (BusinessException e) {
            List<Map<String, Object>> mapList = (List<Map<String, Object>>) ThreadLocalUtil.getTarget();
            if (CollectionUtils.isEmpty(mapList)) {
                throw new BusinessException(e);
            }
            mapList.forEach(item -> {
                String revokeId = String.valueOf(item.get("seqno"));
                String modelType = (String) item.get("modelType");
                PositionHttpRequest.roll(revokeId, modelType, e.getMessage());
            });
        } finally {
            ThreadLocalUtil.cleanLocalCache();
        }
    }

    /**
     * 指令修改,操作类型submit
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning("updatePoint()")
    public void occupyOnUpdate(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        Map<String, Object> map = JsonUtils.object2Map(args[0]);
        String operateType = String.valueOf(map.get("operateType"));
        try {
            if (StringUtil.equals("submit", operateType) || StringUtil.equals("SUBMIT", operateType)) {
                inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_修改.getValue(), 流程节点_流程提交.getValue(), null);
            }
        } catch (BusinessException e) {
            List<Map<String, Object>> mapList = (List<Map<String, Object>>) ThreadLocalUtil.getTarget();
            if (CollectionUtils.isEmpty(mapList)) {
                throw new BusinessException(e);
            }
            mapList.forEach(item -> {
                String revokeId = String.valueOf(item.get("seqno"));
                String modelType = (String) item.get("modelType");
                PositionHttpRequest.roll(revokeId, modelType, e.getMessage());
            });
        } finally {
            ThreadLocalUtil.cleanLocalCache();
        }
    }

    /**
     * 流程生效时，风险处理
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning("completePoint()")
    public void occupyOnComplete(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        Map<String, Object> map = JsonUtils.object2Map(args[0]);
        String operateType = String.valueOf(map.get("operateType"));
        try {
            if (StringUtil.equals("submit", operateType) || StringUtil.equals("SUBMIT", operateType)) {
                inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_新建.getValue(), 流程节点_审批生效.getValue(), null);
            }
        } catch (BusinessException e) {
            List<Map<String, Object>> mapList = (List<Map<String, Object>>) ThreadLocalUtil.getTarget();
            if (CollectionUtils.isEmpty(mapList)) {
                throw new BusinessException(e);
            }
            mapList.forEach(item -> {
                String revokeId = String.valueOf(item.get("seqno"));
                String modelType = (String) item.get("modelType");
                PositionHttpRequest.roll(revokeId, modelType, e.getMessage());
            });
        } finally {
            ThreadLocalUtil.cleanLocalCache();
        }
    }

    /**
     * 作废时处理风险
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning("deletePoint()")
    public void occupyOnDelete(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        try {
            inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_作废.getValue(), 流程节点_流程提交.getValue(), null);
        } catch (BusinessException e) {
            List<Map<String, Object>> mapList = (List<Map<String, Object>>) ThreadLocalUtil.getTarget();
            if (CollectionUtils.isEmpty(mapList)) {
                throw new BusinessException(e);
            }
            mapList.forEach(item -> {
                String revokeId = String.valueOf(item.get("seqno"));
                String modelType = (String) item.get("modelType");
                PositionHttpRequest.roll(revokeId, modelType, e.getMessage());
            });
        } finally {
            ThreadLocalUtil.cleanLocalCache();
        }
    }

    /**
     * 关闭时处理风险
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning("closePoint()")
    public void occupyOnClose(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        try {
            inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_关闭.getValue(), 流程节点_流程提交.getValue(), null);
        } catch (BusinessException e) {
            List<Map<String, Object>> mapList = (List<Map<String, Object>>) ThreadLocalUtil.getTarget();
            if (CollectionUtils.isEmpty(mapList)) {
                throw new BusinessException(e);
            }
            mapList.forEach(item -> {
                String revokeId = String.valueOf(item.get("seqno"));
                String modelType = (String) item.get("modelType");
                PositionHttpRequest.roll(revokeId, modelType, e.getMessage());
            });
        } finally {
            ThreadLocalUtil.cleanLocalCache();
        }
    }

    /**
     * 风险试算
     * @param pageId 画面代码
     * @return 执行结果
     * @throws Exception 异常
     */
    public  AjaxResult doRiskcal(Object dto, String pageId) throws Exception {
        //风险试算组装dto
        RiskCommonOutputDto riskCommonOutput = inputParamSerivce.buildParamThenRisk(dto, 系统动作_新建.getValue(), 流程节点_流程提交.getValue(), pageId, NodeConfigConstant.ENTRUST);

        return new AjaxResult();
    }

    /**
     * 获取返回的信息
     *
     * @param message
     * @return
     */
    public static AjaxResult getAjaxResult(Object data, String message, MessageType messageType) {
        AjaxResult ar = new AjaxResult();
        ar.setData(data);
        List<ValidMessage> warnList = new ArrayList<>();
        warnList.add(new ValidMessage(message));
        ar.setErrorList(warnList);
        ar.setMessageType(messageType);
        return ar;
    }

}
