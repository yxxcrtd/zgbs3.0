package com.joyintech.tams.cfets.risk.aop;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.StringUtil;
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
        AjaxResult ajaxResult=ajaxResultMessage.getData();
        String code=ajaxResultMessage.getCode();
        if (ajaxResult != null && !StringUtil.isEmptyList(ajaxResult.getErrorList())){
            return;
        }
        Object[] args = joinPoint.getArgs();
        if (args[0] instanceof CfetsEtribSaveRequest) {
            CfetsEtribSaveRequest allDto = (CfetsEtribSaveRequest) args[0];
            CfetsEtribBondInfo003Dto dto = cfetsEtrIbBondinfoService.packageEquity(allDto);
            if (StringUtil.isNotEmpty(dto.getOperateType()) && (StringUtil.equals("submit", dto.getOperateType()) || StringUtil.equals("SUBMIT", dto.getOperateType()))) {
                BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                        .put(NodeConfigConstant.BUSI_ACT, 业务动作_投资指令.getValue())
                        .put(NodeConfigConstant.DECISION_ACT, 系统动作_新建.getValue())
                        .put(NodeConfigConstant.FLOW_STATUS, 流程节点_流程提交.getValue())
                        .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_现券交易.getValue())
                        .put("tradePlace", 交易场所_银行间.getValue())
                        .put("ps", dto.getPs())
                        .put("isCheck", true)
                        .put("data", dto);
                RiskCommonOutputDto outputDto = inputParamSerivce.doRisk(map);
            }
        }
    }


    /**
     * 买入、申购等交易方向提交时需要处理风险占用，操作类型submit
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning("insertPoint()")
    public void occupyOnInsert(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        if (args[0] instanceof CfetsEtribBondInfo003Dto) {
            CfetsEtribBondInfo003Dto dto = (CfetsEtribBondInfo003Dto) args[0];
            if (StringUtil.isNotEmpty(dto.getOperateType()) && (StringUtil.equals("submit", dto.getOperateType()) || StringUtil.equals("SUBMIT", dto.getOperateType()))) {
                BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                        .put(NodeConfigConstant.BUSI_ACT, 业务动作_投资指令.getValue())
                        .put(NodeConfigConstant.DECISION_ACT, 系统动作_新建.getValue())
                        .put(NodeConfigConstant.FLOW_STATUS, 流程节点_流程提交.getValue())
                        .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_现券交易.getValue())
                        .put("tradePlace", 交易场所_银行间.getValue())
                        .put("ps", dto.getPs())
                        .put("isCheck", false)
                        .put("data", dto);
                inputParamSerivce.doOccupy(map);
            }
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
        if (args[0] instanceof CfetsEtribBondInfo003Dto) {
            CfetsEtribBondInfo003Dto dto = (CfetsEtribBondInfo003Dto) args[0];
            if (StringUtil.isNotEmpty(dto.getOperateType()) && (StringUtil.equals("submit", dto.getOperateType()) || StringUtil.equals("SUBMIT", dto.getOperateType()))) {
                BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                        .put(NodeConfigConstant.BUSI_ACT, 业务动作_投资指令.getValue())
                        .put(NodeConfigConstant.DECISION_ACT, 系统动作_修改.getValue())
                        .put(NodeConfigConstant.FLOW_STATUS, 流程节点_流程提交.getValue())
                        .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_现券交易.getValue())
                        .put("tradePlace", 交易场所_银行间.getValue())
                        .put("ps", dto.getPs())
                        .put("isCheck", false)
                        .put("data", dto);
                inputParamSerivce.doOccupy(map);
            }
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
        String typeName = joinPoint.getSignature().getDeclaringTypeName();
        if (typeName.equals(CfetsEtribBondInfo003Service.class.getName())) {
            SysWflBusinessList sysBus = sysWflBusinessListMapper.selectByPrimaryKey((String)args[0]);
            if (null != sysBus && StringUtil.equals(sysBus.getProcessState(), DictCodeList.流程状态_已退回.getValue())) {
                CfetsEtribBondInfo003Dto dto = cfetsEtribBondInfo003Service.getBussinessEntityByWorkflowId((String) args[0]);
                BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                        .put(NodeConfigConstant.BUSI_ACT, 业务动作_投资指令.getValue())
                        .put(NodeConfigConstant.DECISION_ACT, 系统动作_作废.getValue())
                        .put(NodeConfigConstant.FLOW_STATUS, 流程节点_流程提交.getValue())
                        .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_现券交易.getValue())
                        .put("tradePlace", 交易场所_银行间.getValue())
                        .put("ps", dto.getPs())
                        .put("isCheck", false)
                        .put("data", dto);
                inputParamSerivce.doOccupy(map);
            }
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
        if (args[0] instanceof CfetsEtribBondInfo003Dto) {
            CfetsEtribBondInfo003Dto dto = (CfetsEtribBondInfo003Dto) args[0];
            List<CfetsEtribBondInfo003Dto> list = dto.getSelectedItem();
            if(StringUtil.isEmpty(dto.getPs()) && CollectionUtils.isNotEmpty(list)) {
                dto = list.get(0);
            }
            BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_投资指令.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, 系统动作_关闭.getValue())
                    .put(NodeConfigConstant.FLOW_STATUS, 流程节点_流程提交.getValue())
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_现券交易.getValue())
                    .put("tradePlace", 交易场所_银行间.getValue())
                    .put("ps", dto.getPs())
                    .put("isCheck", false)
                    .put("data", dto);
            inputParamSerivce.doOccupy(map);
        }
    }

}
