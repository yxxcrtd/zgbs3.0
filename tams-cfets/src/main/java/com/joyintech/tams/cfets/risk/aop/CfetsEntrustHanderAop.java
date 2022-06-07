package com.joyintech.tams.cfets.risk.aop;

import com.google.common.collect.Lists;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.system.base.dao.SysWflBusinessListMapper;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribBondInfo003Dto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribSaveRequest;
import com.joyintech.tams.cfets.etr.bond.service.CfetsEtrIbBondinfoService;
import com.joyintech.tams.cfets.etr.bond.service.CfetsEtribBondInfo003Service;
import com.joyintech.tams.cfets.risk.service.RiskInputParamSerivce;
import com.joyintech.tams.component.business.riskall.constant.NodeConfigConstant;
import com.joyintech.tams.component.business.riskall.dto.RiskCommonOutputDto;
import com.joyintech.tams.constant.DictCodeList;
import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import static com.joyintech.tams.constant.DictCodeListExt3.*;

//@Component
//@Aspect
public class CfetsEntrustHanderAop {
    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(CfetsEntrustHanderAop.class);

    @Autowired
    private RiskInputParamSerivce inputParamSerivce;

    @Autowired
    private CfetsEtribBondInfo003Service cfetsEtribBondInfo003Service;

    @Resource
    private SysWflBusinessListMapper sysWflBusinessListMapper;

    @Autowired
    private CfetsEtrIbBondinfoService cfetsEtrIbBondinfoService;

    /**
     * 保存至代办，从代办中提交到流程中，需要执行风险占用
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.etr..*.*.checkSaveError(..))")
    protected void checkSaveError() {
    }


    /**
     * 保存至代办，从代办中提交到流程中，需要执行风险占用
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.etr..*.*.doUpdate(..))")
    protected void updatePoint() {
    }

    /**
     * 不保存至代办，提交到流程中，需要执行风险占用
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.etr..*.*.doInsert(..))")
    protected void insertPoint() {
    }

    /**
     * 不保存至代办，提交到流程中，需要执行风险占用
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.etr..*.*.doComplete(..))")
    protected void completePoint() {
    }

    /**
     * 执行指令关闭操作需要释放授信
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.etr..service.*.closeDelegate(..))")
    protected void closePoint() {
    }

    /**
     * 作废操作,释放授信
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.etr..*.*.doDelete(..))")
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
        AjaxResult result = new AjaxResult();
        try {
            RiskCommonOutputDto outputDto = inputParamSerivce.buildParamThenRisk(args[0], 系统动作_新建.getValue(), 流程节点_流程提交.getValue(), true);
            if (StringUtil.isNotEmpty(outputDto.getErrorMsg())) {
                result.setErrorList(Lists.newArrayList(new ValidMessage(outputDto.getErrorMsg())));
                ajaxResultMessage.setData(result);
            } else if (StringUtil.isNotEmpty(outputDto.getConfirmMsg())) {
                result.setMessage(outputDto.getConfirmMsg(), MessageType.确认消息);
                ajaxResultMessage.setData(result);
            }
        } catch (Exception e) {
            logger.error("风险校验异常:", e);
            result.setErrorList(Lists.newArrayList(new ValidMessage("风险校验异常")));
            ajaxResultMessage.setData(result);
        }

    }


    /**
     * 买入、申购等交易方向提交时需要处理风险占用，操作类型submit
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning(pointcut = "insertPoint()")
    public void occupyOnInsert(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        Map<String, Object> map = JsonUtils.object2Map(args[0]);
        String operateType = String.valueOf(map.get("operateType"));
        if (StringUtil.equals("submit", operateType) || StringUtil.equals("SUBMIT", operateType)) {
            inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_新建.getValue(), 流程节点_流程提交.getValue(), false);
        }
    }

    /**
     * 买入、申购等交易方向提交时需要处理风险占用，操作类型submit
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning("updatePoint()")
    public void occupyOnUpdate(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        Map<String, Object> map = JsonUtils.object2Map(args[0]);
        String operateType = String.valueOf(map.get("operateType"));
        if (StringUtil.equals("submit", operateType) || StringUtil.equals("SUBMIT", operateType)) {
            inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_修改.getValue(), 流程节点_流程提交.getValue(), false);
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
        if (StringUtil.equals("submit", operateType) || StringUtil.equals("SUBMIT", operateType)) {
            inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_新建.getValue(), 流程节点_审批生效.getValue(), false);
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
        SysWflBusinessList sysBus = sysWflBusinessListMapper.selectByPrimaryKey((String) args[0]);
        if (null != sysBus && StringUtil.equals(sysBus.getProcessState(), DictCodeList.流程状态_已退回.getValue())) {
            inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_作废.getValue(), 流程节点_流程提交.getValue(), false);
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
        inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_关闭.getValue(), 流程节点_流程提交.getValue(), false);
    }

}
