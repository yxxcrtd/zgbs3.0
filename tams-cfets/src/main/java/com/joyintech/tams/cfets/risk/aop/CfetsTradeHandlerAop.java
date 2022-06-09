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
import com.joyintech.tams.business.common.pojo.Message;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribBondInfo003Dto;
import com.joyintech.tams.cfets.etr.bond.dto.CfetsEtribdoRiskcalResponse;
import com.joyintech.tams.cfets.manualsettle.dto.CfetsManualSettleDto;
import com.joyintech.tams.cfets.risk.service.RiskInputParamSerivce;
import com.joyintech.tams.component.business.riskall.dto.RiskCommonOutputDto;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.constant.DictCodeListRsk;
import org.apache.commons.collections.CollectionUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

import static com.joyintech.tams.constant.DictCodeList.交易方向_赎回;
import java.util.ArrayList;
import java.util.List;

import static com.joyintech.tams.constant.DictCodeListExt3.*;

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
     * 交易登记check
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.trd.bond.controller.IbuQuoteBond004Controller.check(..))" +
            "|| execution(* com.joyintech.tams.cfets.trd.primarybidmanage.controller.CfetsPrimaryBidManage002Controller.check(..))" +
            "|| execution(* com.joyintech.tams.cfets.trd.primarybidmanage.controller.CfetsPrimaryBidManage001Controller.check(..))" +
            "|| execution(* com.joyintech.tams.cfets.trd.primarybidmanage.controller.CfetsPrimaryBidManage001Controller.unWinCheck(..))")
    protected void doCheck() {
    }

    /**
     * 交收/回溯/失败
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.manualsettle.service.CfetsManualSettleService.doComplete(..))")
    protected void doCompleteSettle() {
    }

    /**
     * 交易登记更新
     */
    @Pointcut("execution(* com.joyintech.tams.cfets..trd.bond..*.*.doUpdate(..))" +
            "|| execution(* com.joyintech.tams.cfets..trd.primarybidmanage..*.*.doUpdate(..))")
    protected void updatePoint() {
    }

    /**
     * 交易登记新增
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.trd.bond..*.*.doInsert(..))" +
            "|| execution(* com.joyintech.tams.cfets.trd.primarybidmanage..*.*.doInsert(..))")
    protected void insertPoint() {
    }

    /**
     * 作废操作,释放占用
     */
    @Pointcut("execution(* com.joyintech.tams.cfets..trd.bond..*.*.doDelete(..))" +
            "execution(* com.joyintech.tams.cfets..trd.primarybidmanage..*.*.doDelete(..))")
    protected void deletePoint() {
    }

    /**
     * 成交确认
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.trdconfirm..*.*.doCompleteMethod(..))")
    protected void completePoint() {
    }

    /**
     * 交易撤销
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.trdquery..*.*.doCompleteRevoke(..))")
    protected void doCompleteRevoke() {
    }

    /**
     * 交易撤销
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.trd.bond..*.*.doForcedFinish(..))")
    protected void doForcedFinish() {
    }

    /**
     * 交收失败，关闭指令
     */
    @Pointcut("execution(* com.joyintech.tams.cfets.manualsettle.service..*.*.closeDelegate(..))")
    protected void closeDelegate(){}


    /**
     * 交易新建/修改时提交check
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning(pointcut = "doCheck()", returning = "ajaxResultMessage")
    public void doCheck(JoinPoint joinPoint, Message<AjaxResult> ajaxResultMessage) throws BusinessException {
        AjaxResult ajaxResult = ajaxResultMessage.getData();
        if (ajaxResult != null && !StringUtil.isEmptyList(ajaxResult.getErrorList())) {
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
     * 交收/回溯/交收失败
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
                    || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_付息.getValue()) || StringUtil.equals(dto.getPs(), DictCodeList.交易方向_到期.getValue())) {
                inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_新建.getValue(), 流程节点_审批生效.getValue(), null);
            }
        }
        if (StringUtil.equals(dto.getButtonType(), DictCodeListExt3.交收操作类型_交收失败.getValue())) {
            inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_新建.getValue(), 流程节点_审批生效.getValue(), null);
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
        inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_新建.getValue(), 流程节点_流程提交.getValue(), null);
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
        SysWflBusinessList sysBus = sysWflBusinessListMapper.selectByPrimaryKey((String) args[0]);
        if (null != sysBus && StringUtil.equals(sysBus.getProcessState(), DictCodeList.流程状态_已退回.getValue())) {
            inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_作废.getValue(), 流程节点_流程提交.getValue(), null);
        }
    }


    @AfterReturning("completePoint()")
    public void occupyOnComplete(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_新建.getValue(), 流程节点_流程提交.getValue(), null);
    }

    /**
     * 交易撤销
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning("doCompleteRevoke()")
    public void revokeComplete(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        inputParamSerivce.buildParamThenOccupy(args[0], 系统动作_撤销.getValue(), 流程节点_审批生效.getValue(), null);
    }

    /**
     * 指令强制完成
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning(pointcut = "doForcedFinish()", returning = "bondInfo003Dto")
    public void doForcedFinish(JoinPoint joinPoint, CfetsEtribBondInfo003Dto bondInfo003Dto) throws BusinessException {
        inputParamSerivce.buildParamThenOccupy(bondInfo003Dto, 系统动作_撤销.getValue(), 流程节点_审批生效.getValue(), null);
    }

    /**
     * 交收失败
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning(pointcut = "closeDelegate()")
    public void closeDelegate(JoinPoint joinPoint, CfetsEtribBondInfo003Dto bondInfo003Dto) throws BusinessException {
        inputParamSerivce.buildParamThenOccupy(bondInfo003Dto, 系统动作_撤销.getValue(), 流程节点_审批生效.getValue(), null);
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
    public static final <T> AjaxResult doRiskcal(T dto, String pageId) throws Exception {
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
