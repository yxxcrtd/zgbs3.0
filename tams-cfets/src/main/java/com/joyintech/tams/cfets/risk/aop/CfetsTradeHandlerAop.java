package com.joyintech.tams.cfets.risk.aop;

import com.google.common.collect.Lists;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.system.base.dao.SysWflBusinessListMapper;
import com.joyintech.system.base.entity.SysWflBusinessList;
import com.joyintech.tams.business.common.pojo.BaseMap;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribBondInfo003Dto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribSaveRequest;
import com.joyintech.tams.cfets.manualsettle.dto.CfetsManualSettleDto;
import com.joyintech.tams.cfets.manualsettle.dto.CfetsManualSettleInsertDto;
import com.joyintech.tams.cfets.manualsettle.service.CfetsManualSettleService;
import com.joyintech.tams.cfets.risk.service.RiskInputParamSerivce;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondDealQuoteCommonDto;
import com.joyintech.tams.cfets.trd.bond.dto.CfetsBondDealRegisterQuoteDto;
import com.joyintech.tams.cfets.trd.bond.service.CfetsBondDealQuotedPrice004Service;
import com.joyintech.tams.cfets.trd.primarybidmanage.dto.CfetsPrimaryBidManageSaveRequest;
import com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdBondInvestTradeDto;
import com.joyintech.tams.component.business.riskall.constant.NodeConfigConstant;
import com.joyintech.tams.component.business.riskall.dto.RiskCommonOutputDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListRsk;
import com.joyintech.tams.invest.trade.bond.dto.TrdBondInvestTradeDto;
import com.joyintech.tams.invest.trade.common.service.TradeCommonService;
import com.joyintech.workflow.engine.constant.OperationType;
import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.joyintech.tams.constant.DictCodeListExt3.*;
import static com.joyintech.tams.constant.DictCodeListExt3.交易场所_银行间;
import static com.joyintech.tams.invest.common.constant.BussinessDictCodeList.*;

//@Component
//@Aspect
public class CfetsTradeHandlerAop {
    /**
     * 日志
     */
    private static Logger logger = LoggerFactory.getLogger(CfetsEntrustHanderAop.class);

    @Autowired
    private RiskInputParamSerivce inputParamSerivce;
    @Resource
    private SysWflBusinessListMapper sysWflBusinessListMapper;



    /**
     * 回售登记check
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.trd.bond.controller.IbuQuoteBond004Controller.check(..))" +
            " || execution(* com.joyintech.tams.cfets.trd.primarybidmanage.controller.CfetsPrimaryBidManage002Controller.check(..))")
    protected void doCheck() {
    }

    /**
     * 交收/回溯/失败
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.manualsettle.service.CfetsManualSettleService.doComplete(..)) " +
            "|| execution(* com.joyintech.tams.cfets.trd.primarybidmanage.service.CfetsPrimaryBidManageService.doComplete(..))")
    protected void doCompleteSettle() {
    }

    /**
     * 回售登记更新
     */
    @Pointcut("execution(* com.joyintech.tams.cfets..trd.bond..*.*.doUpdate(..))" +
            "|| execution(* com.joyintech.tams.cfets..trd.primarybidmanage..*.*.doUpdate(..)) ")
    protected void updatePoint() {
    }

    /**
     * 回售登记新增
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.trd.bond..*.*.doInsert(..))" +
            "|| execution(* com.joyintech.tams.cfets.trd.primarybidmanage..*.*.doInsert(..))")
    protected void insertPoint() {
    }

    /**
     * 作废操作,释放占用
     */
    @Pointcut("execution(* com.joyintech.tams.cfets..trd.bond..*.*.doDelete(..))")
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
    @AfterReturning(pointcut = "doCheck()", returning = "ajaxResultMessage")
    public void doCheck(JoinPoint joinPoint, Message<AjaxResult> ajaxResultMessage) throws BusinessException {
        AjaxResult ajaxResult=ajaxResultMessage.getData();
        if (ajaxResult != null && !StringUtil.isEmptyList(ajaxResult.getErrorList())){
            return;
        }
        Object[] args = joinPoint.getArgs();
        AjaxResult result = new AjaxResult();
        try {
            RiskCommonOutputDto outputDto = inputParamSerivce.buildParamThenRisk(args[0], 系统动作_新建.getValue(), 流程节点_流程提交.getValue(), null);
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
    @AfterReturning(pointcut = "doCompleteSettle()")
    public void doCompleteSettle(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        CfetsManualSettleDto dto = (CfetsManualSettleDto) args[0];
        inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_新建.getValue(), 流程节点_流程提交.getValue(), null);
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
        inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_新建.getValue(), 流程节点_流程提交.getValue(), null);
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
        inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_修改.getValue(), 流程节点_流程提交.getValue(), null);
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
        SysWflBusinessList sysBus = sysWflBusinessListMapper.selectByPrimaryKey((String)args[0]);
        if (null != sysBus && StringUtil.equals(sysBus.getProcessState(), DictCodeList.流程状态_已退回.getValue()) ) {
            inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_作废.getValue(), 流程节点_流程提交.getValue(), null);
        }
    }


    @AfterReturning("completePoint()")
    public void occupyOnComplete(JoinPoint joinPoint) throws BusinessException {
        if(!StringUtil.isNotEmpty(joinPoint.getArgs())){
            return;
        }
        Object[] args = joinPoint.getArgs();
        inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_新建.getValue(), 流程节点_流程提交.getValue(), null);
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
        inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_撤销.getValue(), 流程节点_审批生效.getValue(), null);
    }
    /**
     * 风险试算
     *
     * @param dto 委托指令内容
     * @param infCode 接口代码
     * @param busiType 业务类型
     * @return 执行结果
     * @throws Exception 异常
     */
    public static final <T> AjaxResult doRiskcal(T dto, String infCode, String busiType) throws Exception {
        //风险试算check
        RiskInputParamSerivce riskInputParamSerivce = getRiskInputParamSerivce();
        //风险试算组装dto
        RiskCommonOutputDto riskCommonOutput = riskInputParamSerivce.buildParamThenRisk(dto, 系统动作_新建.getValue(), 流程节点_流程提交.getValue(), null);
        return getAjaxResult(riskCommonOutput,null,null);
    }

    @SuppressWarnings("unchecked")
    public static RiskInputParamSerivce getRiskInputParamSerivce() {
        return SpringContextUtil.getApplicationContext().getBean(RiskInputParamSerivce.class);
    }

    /**
     * 获取返回的信息
     * @param message
     * @return
     */
    public static AjaxResult getAjaxResult(Object data,String message, MessageType messageType){
        AjaxResult ar = new AjaxResult();
        ar.setData(data);
        List<ValidMessage> warnList = new ArrayList<>();
        warnList.add(new ValidMessage(message));
        ar.setErrorList(warnList);
        ar.setMessageType(messageType);
        return ar;
    }
}
