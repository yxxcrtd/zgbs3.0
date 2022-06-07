package com.joyintech.tams.cfets.risk.aop;

import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.system.base.dao.SysWflBusinessListMapper;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribBondInfo003Dto;
import com.joyintech.tams.cfets.etr.bond.service.CfetsEtribBondInfo003Service;
import com.joyintech.tams.cfets.risk.service.RiskInputParamSerivce;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondDealRegisterQuoteDto;
import com.joyintech.tams.cfets.trd.bond.service.CfetsBondDealQuotedPrice004Service;
import com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdBondInvestTradeDto;
import com.joyintech.tams.component.business.riskall.constant.NodeConfigConstant;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListRsk;
import com.joyintech.tams.invest.trade.bond.dto.TrdBondInvestTradeDto;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.workflow.engine.constant.OperationType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

import static com.joyintech.tams.constant.DictCodeListExt3.*;
import static com.joyintech.tams.invest.common.constant.BussinessDictCodeList.交易方向_行权回售;
import static com.joyintech.tams.invest.common.constant.BussinessDictCodeList.行权方式_回售;

@Component
@Aspect
public class CfetsTradeHandlerAop {

    @Autowired
    private RiskInputParamSerivce inputParamSerivce;
    @Resource
    private SysWflBusinessListMapper sysWflBusinessListMapper;
    @Autowired
    private CfetsBondDealQuotedPrice004Service quotedPrice004Service;

    /**
     * 保存至代办，从代办中提交到流程中，需要执行风险占用
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.trdconfirm..*.*.doUpdate(..))")
    protected void updatePoint() {
    }

    /**
     * 不保存至代办，提交到流程中，需要执行风险占用
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.trdconfirm..*.*.doInsert(..))")
    protected void insertPoint() {
    }

    /**
     * 执行指令关闭操作需要释放授信
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.trdconfirm..service.*.closeDelegate(..))")
    protected void closePoint() {
    }

    /**
     * 作废操作,释放授信
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.trdconfirm..*.*.doDelete(..))")
    protected void deletePoint() {
    }

    @Pointcut("execution(* com.joyintech.tams.cfets.trdconfirm..*.*.doCompleteMethod(..))")
    protected void completePoint() {}

    @Pointcut("execution(* com.joyintech.tams.cfets.trdquery..*.*.doCompleteRevoke(..))")
    protected void doCompleteRevoke() {}



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
            CfetsBondDealRegisterQuoteDto dto = (CfetsBondDealRegisterQuoteDto) args[0];
            if (StringUtil.isNotEmpty(dto.getOperatorType()) && (StringUtil.equals("submit", dto.getOperatorType()) || StringUtil.equals("SUBMIT", dto.getOperatorType()))
               && StringUtil.equals(dto.getPs(),交易方向_行权回售)) {
                BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                        .put(NodeConfigConstant.BUSI_ACT, 业务动作_交易登记.getValue())
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
            CfetsBondDealRegisterQuoteDto dto = (CfetsBondDealRegisterQuoteDto) args[0];
            if (StringUtil.isNotEmpty(dto.getOperatorType()) && (StringUtil.equals("submit", dto.getOperatorType()) || StringUtil.equals("SUBMIT", dto.getOperatorType()))
                    && StringUtil.equals(dto.getPs(),交易方向_行权回售)) {
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
        if (typeName.equals(CfetsBondDealQuotedPrice004Service.class.getName())) {
            SysWflBusinessList sysBus = sysWflBusinessListMapper.selectByPrimaryKey((String)args[0]);
            if (null != sysBus && StringUtil.equals(sysBus.getProcessState(), DictCodeList.流程状态_已退回.getValue()) ) {
                CfetsBondDealRegisterQuoteDto dto = quotedPrice004Service.getBussinessEntityByWorkflowId((String)args[0]);
                if(!StringUtil.equals(dto.getPs(),交易方向_行权回售)) {
                    return;
                }
            }
        }
    }


    @AfterReturning("completePoint()")
    public void occupyOnComplete(JoinPoint joinPoint) throws BusinessException {
        if(!StringUtil.isNotEmpty(joinPoint.getArgs())){
            return;
        }
        Object[] args = joinPoint.getArgs();
        if(args[0] instanceof CfetsTrdBondInvestTradeDto) {//成交确认
            CfetsTrdBondInvestTradeDto dto = (CfetsTrdBondInvestTradeDto)args[0];
            BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_成交确认.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, 系统动作_新建.getValue())
                    .put(NodeConfigConstant.FLOW_STATUS, 流程节点_流程提交.getValue())
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_现券交易.getValue())
                    .put("tradePlace", 交易场所_银行间.getValue())
                    .put("ps", dto.getPs())
                    .put("data", dto);
            if(StringUtil.equals(dto.getPs(),交易方向_行权回售.getValue())) {
                map.put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券行权交易);
            }
            inputParamSerivce.doOccupy(map);
        }
    }

    /**
     * 交易撤销
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning("doCompleteRevoke()")
    public void revokeComplete(JoinPoint joinPoint) throws BusinessException {
        if(!StringUtil.isNotEmpty(joinPoint.getArgs())){
            return;
        }
        Object[] args = joinPoint.getArgs();
        if(args[0] instanceof CfetsTrdBondInvestTradeDto) {//债券撤销
            CfetsTrdBondInvestTradeDto dto = (CfetsTrdBondInvestTradeDto)args[0];
            BaseMap map = BaseMap.newMap().put(NodeConfigConstant.INTERFACE_TYPE, NodeConfigConstant.BOND001)
                    .put(NodeConfigConstant.BUSI_ACT, 业务动作_成交确认.getValue())
                    .put(NodeConfigConstant.DECISION_ACT, 系统动作_撤销.getValue())
                    .put(NodeConfigConstant.FLOW_STATUS, 流程节点_审批生效.getValue())
                    .put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_现券交易.getValue())
                    .put("tradePlace", 交易场所_银行间.getValue())
                    .put("ps", dto.getPs())
                    .put("data", dto);
            if(StringUtil.equals(dto.getPs(),交易方向_行权回售.getValue())) {
                map.put(NodeConfigConstant.BUSI_TYPE, 头寸业务类型_债券行权交易);
            }
            inputParamSerivce.doOccupy(map);
        }
    }
}
