package com.joyintech.tams.exchange.period.risk.aop;

import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.business.common.service.PositionHttpRequest;
import com.joyintech.tams.business.common.util.ThreadLocalUtil;
import com.joyintech.tams.exchange.period.risk.service.PeriodRiskInputParamService;
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

import java.util.List;
import java.util.Map;

import static com.joyintech.tams.constant.DictCodeListExt3.*;

@Component
@Aspect
public class PeriodRiskHandlerAop {


    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(PeriodRiskHandlerAop.class);

    @Autowired
    private PeriodRiskInputParamService inputParamSerivce;


    /**
     * 保存至代办，从代办中提交到流程中，需要执行风险占用(暂时只切了信用保护凭证，后续新增其他业务时，需要调整切点表达式)
     */
    @Pointcut("execution(* com.joyintech.tams.exchange.period.creditprotectioncertificate..*.*.doUpdate(..))")
    protected void updatePoint() {
    }

    /**
     * 不保存至代办，提交到流程中，需要执行风险占用(暂时只切了信用保护凭证，后续新增其他业务时，需要调整切点表达式)
     */
    @Pointcut("execution(* com.joyintech.tams.exchange.period.creditprotectioncertificate..*.*.doInsert(..))")
    protected void insertPoint() {
    }

    /**
     * 不保存至代办，提交到流程中，需要执行风险占用(暂时只切了信用保护凭证，后续新增其他业务时，需要调整切点表达式)
     */
    @Pointcut("execution(* com.joyintech.tams.exchange.period.creditprotectioncertificate..*.*.doComplete(..))")
    protected void completePoint() {
    }

    /**
     * 执行指令关闭操作需要释放授信(暂时只切了信用保护凭证，后续新增其他业务时，需要调整切点表达式)
     */
    @Pointcut("execution(* com.joyintech.tams.exchange.period.creditprotectioncertificate.service.*.doRevokeComplete(..))")
    protected void revokeCompletePoint() {
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
    @AfterReturning("revokeCompletePoint()")
    public void occupyOnRevoke(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        try {
            inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_撤销.getValue(), 流程节点_审批生效.getValue(), null);
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


}
