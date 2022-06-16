package com.joyintech.tams.cfets.risk.aop;

import com.google.common.collect.Lists;
import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.service.bankuplink.common.dto.TrdBondInvestTradeDto;
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribBondInfo003Dto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribdoRiskcalResponse;
import com.joyintech.tams.cfets.manualsettle.dto.CfetsManualSettleDto;
import com.joyintech.tams.cfets.risk.service.RiskInputParamSerivce;
import com.joyintech.tams.component.business.riskall.dto.RiskCommonOutputDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListRsk;
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

import static com.joyintech.tams.constant.DictCodeListExt3.*;
import static com.joyintech.tams.constant.DictCodeListRsk.试算结果_风险审批;

@Component
@Aspect
public class CfetsTradeHandlerAop {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(CfetsEntrustHanderAop.class);

    @Autowired
    private RiskInputParamSerivce inputParamSerivce;



    /**
     * 风险试算
     *
     * @param dto 委托指令内容
     * @param pageId 接口代码
     * @return 执行结果
     * @throws Exception 异常
     */
    public static <T> AjaxResult doRiskcal(T dto, String pageId) throws Exception {
        //风险试算check
        RiskInputParamSerivce riskInputParamSerivce = getRiskInputParamSerivce();
        //风险试算组装dto
        RiskCommonOutputDto riskCommonOutput = riskInputParamSerivce.buildParamThenRisk(dto, 系统动作_新建.getValue(), 流程节点_流程提交.getValue(), pageId);
        CfetsEtribdoRiskcalResponse result = new CfetsEtribdoRiskcalResponse();
        result.setRiskMathFlag("02");
        if (riskCommonOutput != null && CollectionUtils.isNotEmpty(riskCommonOutput.getRiskTrialRetDto())) {
            List<CfetsEtribdoRiskcalResponse> resultList = new ArrayList<>();
            result.setRskStatus(riskCommonOutput.getRskTrialStatus());
            if (com.joyintech.background.component.common.pack.common.util.StringUtil.equals(riskCommonOutput.getRskTrialStatus(), "3")) {
                result.setRiskTrialSpan(DictCodeListRsk.试算结果_不通过.getCodeName());
            }else {
                result.setRiskTrialSpan(DictCodeListRsk.试算结果_通过.getCodeName());
            }
            riskCommonOutput.getRiskTrialRetDto().forEach(e->{
                CfetsEtribdoRiskcalResponse riskcalResponse = new CfetsEtribdoRiskcalResponse();
                BeanUtil.copyPropertiesIgnoreNull(riskcalResponse,e);
                resultList.add(riskcalResponse);
            });
            result.setRiskcalReturnList(resultList);
            return getAjaxResult(result,null,null);
        } else {
            result.setRskStatus(DictCodeListRsk.试算结果_通过.getValue());
            result.setRiskTrialSpan(DictCodeListRsk.试算结果_通过.getCodeName());
            return getAjaxResult(result,null,null);
        }
    }

    /**
     * 交收/回溯/失败
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.manualsettle.service.CfetsManualSettleService.doComplete(..))")
    protected void doCompleteSettle() {
    }

    /**
     * 交易登记check、对话报价提交check
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.trd..*.*.check(..))"+
        "|| execution(* com.joyintech.tams.cfets.trd.primarybidmanage.controller.CfetsPrimaryBidManage001Controller.unWinCheck(..))")
    protected void doCheck() {
    }

    /**
     * 对话报价 报价成交提交check
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.trd..*.*.checkRepoDeal(..))")
    protected void checkRepoDeal() {

    }

    /**
     * 交易登记更新
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.trd..*.*.doUpdate(..))")
    protected void updatePoint() {
    }

    /**
     * 交易登记新增
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.trd..*.*.doInsert(..))")
    protected void insertPoint() {
    }

    /**
     * 债券成交确认
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.trdconfirm..*.*.doCompleteMethod(..))" +
            "|| execution(* com.joyintech.tams.cfets.trdconfirm..*.*.doCompleteBidMethod(..))")
    protected void completePoint() {
    }

    /**
     * 交易执行-成交确认 || 手工匹配
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.trdconfirm..*.*.doCompleteRepoMethod(..))")
    protected void completeRepoPoint() {
    }

    /**
     * 债券交易撤销
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.trdquery..*.*.doCompleteRevoke(..))")
    protected void doCompleteRevoke() {
    }

    /**
     * 债券回售登记撤销
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.trd.bond.service.CfetsBondDealQuotedPrice004Service.doComplete(..))")
    protected void doCompleteRegRevoke() {
    }


    /**
     * 交易撤销
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.trdquery..*.*.doRevokeComplete(..))")
    protected void doRevokeComplete() {
    }

    /**
     * 强制完成
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.trd..*.*.doForcedFinish(..))")
    protected void doForcedFinish() {
    }

    /**
     * 交收失败，关闭指令
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.manualsettle.service.CfetsManualSettleService.closeDelegate(..))")
    protected void closeDelegate(){}


    /**
     * 交易新建/修改时提交check
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning(pointcut = "doCheck()", returning = "ajaxResultMessage")
    public void doCheck(JoinPoint joinPoint, Message<AjaxResult> ajaxResultMessage) throws BusinessException {
        checkCommon(joinPoint,ajaxResultMessage,系统动作_新建.getValue(), 流程节点_流程提交.getValue(), null);
    }

    /**
     * 报价成交提交检查
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning(pointcut = "checkRepoDeal()", returning = "ajaxResultMessage")
    public void checkRepoDeal(JoinPoint joinPoint, Message<AjaxResult> ajaxResultMessage) throws BusinessException {
        checkCommon(joinPoint,ajaxResultMessage,系统动作_成交.getValue(), 流程节点_流程提交.getValue(), null);
    }

    /**
     * check共通方法
     * @param joinPoint
     * @param ajaxResultMessage
     * @param decisionAct
     * @param flowStatus
     * @param pageId
     */
    private void checkCommon(JoinPoint joinPoint,Message<AjaxResult> ajaxResultMessage,String decisionAct, String flowStatus, String pageId){
        AjaxResult ajaxResult = ajaxResultMessage.getData();
        if (ajaxResult != null && !StringUtil.isEmptyList(ajaxResult.getErrorList())) {
            return;
        }
        Object[] args = joinPoint.getArgs();
        AjaxResult result = new AjaxResult();
        try {
            RiskCommonOutputDto outputDto = inputParamSerivce.buildParamThenRisk(args[0], decisionAct, flowStatus, null);
            if (StringUtil.isNotEmpty(outputDto.getErrorMsg())) {
                result.setErrorList(Lists.newArrayList(new ValidMessage(outputDto.getErrorMsg())));
                ajaxResultMessage.setData(result);
            } else if (StringUtil.isNotEmpty(outputDto.getConfirmMsg())) {
                result.setMessage(outputDto.getConfirmMsg(), MessageType.确认消息);
                if(StringUtil.equals(outputDto.getRskStatus(),试算结果_风险审批.getValue())) {
                    result.setData(是否_是);
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
     * 交收/回溯
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning(pointcut = "doCompleteSettle()")
    public void doCompleteSettle(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        CfetsManualSettleDto dto = (CfetsManualSettleDto) args[0];
        if (StringUtil.equals(dto.getButtonType(), DictCodeListExt3.交收操作类型_交收.getValue()) || StringUtil.equals(dto.getButtonType(), DictCodeListExt3.交收操作类型_回溯.getValue())) {
            if (StringUtil.equals(dto.getPs(), DictCodeList.交易方向_行权_赎回.getValue()) || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_还本.getValue())
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_收息.getValue()) || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue())) {
                inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_新建.getValue(), 流程节点_审批生效.getValue(), null);
            }
        }
    }


    /**
     * 交易新建
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning("insertPoint()")
    public void occupyOnInsert(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        OperationType op = (OperationType)args[1];
        if(!StringUtil.equals(op.name(), OperationType.REVOKE.name())) {
            inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_新建.getValue(), 流程节点_流程提交.getValue(), null);
        }
    }

    /**
     * 交易更新
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
        inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_作废.getValue(), 流程节点_流程提交.getValue(), null);
    }


    @AfterReturning("completePoint() || completeRepoPoint()")
    public void occupyOnComplete(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_新建.getValue(), 流程节点_审批生效.getValue(), null);
    }

    /**
     * 交易撤销
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning("doRevokeComplete()")
    public void revokeComplete(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_撤销.getValue(), 流程节点_审批生效.getValue(), null);
    }

    /**
     * 债券交易撤销
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning(pointcut = "doCompleteRevoke() ",returning = "tradeDto")
    public void bondRevokeComplete(JoinPoint joinPoint, TrdBondInvestTradeDto tradeDto) throws BusinessException {
        inputParamSerivce.buildParamThenOccupy(tradeDto, 系统动作_撤销.getValue(), 流程节点_审批生效.getValue(), null);
    }

    /**
     * 债券回售交易登记撤销
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning(pointcut = "doCompleteRegRevoke() ")
    public void bondRevokeRegComplete(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        OperationType op = (OperationType)args[1];
        if(StringUtil.equals(op.name(), OperationType.REVOKE.name())) {
            inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_撤销.getValue(), 流程节点_审批生效.getValue(), null);
        }
    }


    /**
     * 指令强制完成
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning(pointcut = "doForcedFinish()", returning = "bondInfo003Dto")
    public void doForcedFinish(JoinPoint joinPoint, CfetsEtribBondInfo003Dto bondInfo003Dto) throws BusinessException {
        inputParamSerivce.buildParamThenOccupy(bondInfo003Dto, 系统动作_关闭.getValue(), 流程节点_审批生效.getValue(), null);
    }

    /**
     * 交收失败
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning(pointcut = "closeDelegate()",returning = "tradeDto")
    public void closeDelegate(JoinPoint joinPoint, CfetsEtribBondInfo003Dto tradeDto) throws BusinessException {
        inputParamSerivce.buildParamThenOccupy(tradeDto, 系统动作_关闭.getValue(), 流程节点_审批生效.getValue(), null);
    }

    /**
     * 作废操作,释放占用
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.trd..*.*.doDelete(..))")
    protected void deletePoint() {
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
