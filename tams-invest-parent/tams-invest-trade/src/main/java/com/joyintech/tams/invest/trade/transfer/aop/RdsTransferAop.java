package com.joyintech.tams.invest.trade.transfer.aop;

import com.joyintech.framework.ajax.AjaxResult;
import com.joyintech.framework.ajax.MessageType;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.framework.util.*;
import com.joyintech.framework.validation.ValidMessage;
import com.joyintech.tams.component.business.riskrds.constant.InterfUtil;
import com.joyintech.tams.component.business.riskrds.dto.InterfRiskRdsDto;
import com.joyintech.tams.component.business.riskrds.service.InterfRiskRdsService;
import com.joyintech.tams.invest.common.CommonUtil;
import com.joyintech.tams.invest.common.client.ClientData;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.invest.common.constantrds.BussinessDictCodeListRds;
import com.joyintech.tams.invest.common.risktrialrds.RiskTrialComm;
import com.joyintech.tams.invest.trade.common.aop.TradeRdsCommonAop;
import com.joyintech.workflow.engine.constant.OperationType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 转仓调用风险逻辑aop
 *
 * @author gwl
 * @version 1.0 2022年8月16日
 * @see RdsTransferAop
 * @since 1.0
 */
@Component
@Aspect
public class RdsTransferAop {
    private static Logger logger = LoggerFactory.getLogger(RdsTransferAop.class);
    /**
     * 线程缓存
     */
    private static ThreadLocal<List<Map<String, Object>>> localCache = ThreadLocal.withInitial(ArrayList::new);

    @Autowired
    private InterfRiskRdsService<InterfRiskRdsDto> interfRiskRdsService;

    @Autowired
    private RiskTrialComm riskTrialComm;

    /**
     * 待办中提交
     *
     * @param joinPoint
     * @throws BusinessException
     */
    @AfterReturning("updatePoint()")
    public void updateMethod(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        if (this.check(args)) {
            return;
        }
        Map<String, Object> mapTrd = JsonUtils.object2Map(args[0]);
        if (StringUtil.isNotEmpty(mapTrd.get("operateType")) && mapTrd.get("operateType").equals("submit")) {
            if (!checkRevokeAction(mapTrd.get("workflowId"))) {//如果是撤销的退回再提交直接退去，不需要调实占
                return;
            }
            //直接调用这个，报文在这里组装好传入sendListDataCommon
            List<Map<String, Object>> dtoList = RskInputParam.buildOccupyParam(args[0], BussinessDictCodeListRds.接口系统ID_实占批量接口.getValue());
            // 执行占用
            ClientData resp = getInterfRiskRdsService().sendListDataCommon(dtoList, BussinessDictCodeListRds.接口系统ID_实占批量接口.getValue());
            AjaxResult result = getAjaxResult(resp);
            if (result == null) {
                setLocalCache(dtoList);
            } else {
                throw new BusinessException(InterfUtil.MSG + resp.getHead().get(InterfUtil.stdrtninfo).toString());
            }
        }
    }

    /**
     * 撤销操作
     *
     * @param joinPoint
     * @throws Exception
     */
    @AfterReturning("revokePoint()")
    public void revokeMethod(JoinPoint joinPoint) throws Exception {
        Object[] args = joinPoint.getArgs();
        if (this.check(args)) {
            return;
        }
        OperationType operationType = (OperationType) args[1];
        Map<String, Object> mapTrd = JsonUtils.object2Map(args[0]);
        if (operationType.equals(OperationType.REVOKE)) {
            List<Map<String, Object>> dtoList = RskInputParam.buildOccupyParam(args[0], BussinessDictCodeListRds.接口系统ID_实占释放批量接口.getValue());
            returnError(getInterfRiskRdsService().sendListDataCommon(dtoList, BussinessDictCodeListRds.接口系统ID_实占释放批量接口.getValue()));
        }
    }

    @AfterReturning("backPoint()")
    public void backMethod(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        if (this.check(args)) {
            return;
        }
        Map<String, Object> mapTrd = JsonUtils.object2Map(args[0]);
        if (StringUtil.isNotEmpty(mapTrd) && StringUtil.isNotEmpty(mapTrd.get("operateType")) && !StringUtil.equals(mapTrd.get("operateType").toString(), "revoke")) {
            if (!checkRevokeAction(mapTrd.get("workflowId"))) { //如果是撤销的退回不需要处理
                return;
            }
            List<Map<String, Object>> dtoList = RskInputParam.buildOccupyParam(args[0], BussinessDictCodeListRds.接口系统ID_实占释放批量接口.getValue());
            returnError(getInterfRiskRdsService().sendListDataCommon(dtoList, BussinessDictCodeListRds.接口系统ID_实占释放批量接口.getValue()));
        }
    }

    /**
     * 清除线程缓存
     *
     * @param joinPoint 切面参数
     */
    @AfterReturning(value = "savePoint()")
    protected void afterSaveTradeMethod(JoinPoint joinPoint) {
        cleanLocalCache(); // 清除线程缓存
    }

    /**
     * 若提交流程出错则需要将实占的数据撤销掉
     *
     * @param joinPoint 切面参数
     * @param exception 抛出的异常
     */
    @AfterThrowing(value = "savePoint()", throwing = "exception")
    protected void saveTradeMethod(JoinPoint joinPoint, Throwable exception) {
        Object[] args = joinPoint.getArgs();
        if (this.check(args)) {
            return;
        }
        Map<String, Object> mapTrd = JsonUtils.object2Map(args[0]);
        if (CommonUtil.findValue(mapTrd, "operateType").equals(BussinessConstant.SUBMIT)) {
            getInterfRiskRdsService().sendListDataCommon(getTarget(), BussinessDictCodeListRds.接口系统ID_实占释放批量接口.getValue());
        }
        // 清除线程缓存
        cleanLocalCache();
    }

    /**
     * 批量接口调用
     *
     * @param obj 占用对象
     * @return 风控计算结果
     * @throws Exception 异常
     */
    public static AjaxResult doListOccupy(Object obj) throws Exception {
        if (!CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)) {
            return null;
        }
        List<Map<String, Object>> dtoList = RskInputParam.buildOccupyParam(obj, BussinessDictCodeListRds.接口系统ID_实占批量接口.getValue());
        // 执行占用
        ClientData resp = getInterfRiskRdsService().sendListDataCommon(dtoList, BussinessDictCodeListRds.接口系统ID_实占批量接口.getValue());
        AjaxResult result = getAjaxResult(resp);
        if (result == null) {
            setLocalCache(dtoList);
        }
        return result;
    }

    /**
     * 批量接口调用
     *
     * @param obj 占用对象
     * @return 风控计算结果
     * @throws Exception 异常
     */
    public static void releaseOccupy(String tradeId, String eventDate) {
        if (!CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)) {
            return;
        }
        List<Map<String, Object>> dtoList = RskInputParam.buildReleaseParam(tradeId, eventDate, BussinessDictCodeListRds.接口系统ID_实占释放批量接口.getValue());
        // 执行占用
        returnError(getInterfRiskRdsService().sendListDataCommon(dtoList, BussinessDictCodeListRds.接口系统ID_实占释放批量接口.getValue()));

        TradeRdsCommonAop.setLocalCache(dtoList);
    }

    /**
     * 占用失败组装失败消息
     *
     * @param resp 占用结果
     * @return 错误信息
     */
    public static AjaxResult getAjaxResult(ClientData resp) {
        AjaxResult result = null;
        if (resp != null && StringUtil.equals(resp.getHeadField(InterfUtil.std400msgt), InterfUtil.E)
                && StringUtil.isNotEmpty(resp.getHeadField(InterfUtil.stdrtninfo))) {
            result = new AjaxResult();
            List<ValidMessage> warnList = new ArrayList<>();
            warnList.add(new ValidMessage(InterfUtil.MSG + resp.getHeadField(InterfUtil.stdrtninfo)));
            result.setErrorList(warnList);
            result.setMessageType(MessageType.错误消息);
        }
        return result;
    }

    /**
     * 根据占用结果返回错误信息
     *
     * @param result
     */
    public static void returnError(ClientData result) {
        if (StringUtil.isNotEmpty(result) && StringUtil.isNotEmpty(result.getHead()) && StringUtil.isNotEmpty(result.getHead().get(InterfUtil.std400msgt))) {
            if (StringUtil.equals(result.getHead().get(InterfUtil.std400msgt).toString(), InterfUtil.E) && StringUtil.isNotEmpty(result.getHead().get(InterfUtil.stdrtninfo))) {
                throw new BusinessException(InterfUtil.MSG + result.getHead().get(InterfUtil.stdrtninfo).toString());
            }
        }
    }

    /**
     * 校验风险开关是否打开
     *
     * @param args
     * @return
     */
    private boolean check(Object[] args) {
        return !CommonUtil.isType(CacheUtil.getSysParam("pageRedisFlag"), BussinessDictCodeListRds.是否_是)
                || Objects.isNull(args) || args.length <= 0;
        // return false;
    }

    /**
     * 如果是撤销的退回再提交直接退去，不需要调实占
     *
     * @param workflowId
     * @param <T>
     * @return
     */
    private <T> boolean checkRevokeAction(T workflowId) {
        if (StringUtil.isNotEmpty(workflowId)) {
            String actionType = riskTrialComm.selectWorkflowActionType(workflowId.toString());
            if (actionType.equals("revoke") || actionType.equals("delete")) {
                return false;
            }
        }
        return true;
    }

    public static final void setLocalCache(List<Map<String, Object>> list) {
        localCache.set(list);
    }

    private static final List<Map<String, Object>> getTarget() {
        return localCache.get();
    }

    private static final void cleanLocalCache() {
        localCache.remove();
    }

    @SuppressWarnings("unchecked")
    public static InterfRiskRdsService<InterfRiskRdsDto> getInterfRiskRdsService() {
        return SpringContextUtil.getApplicationContext().getBean(InterfRiskRdsService.class);
    }

    /**
     * 退回时在待办中再提交
     */
    @Pointcut("execution(* com.joyintech.tams.invest.trade.transfer..*.*.doUpdate(..))")
    protected void updatePoint() {
    }

    /**
     * 撤销操作
     */
    @Pointcut("execution(* com.joyintech.tams.invest.trade.transfer..*.*.doComplete(..))")
    protected void revokePoint() {
    }

    /**
     * 退回操作
     */
    @Pointcut("execution(* com.joyintech.tams.invest.trade.transfer..*.*.doBack(..))")
    protected void backPoint() {
    }

    /**
     * 提交流程操作
     */
    @Pointcut("execution(* com.joyintech.tams.invest.trade.transfer..*.controller..*.save(..))")
    protected void savePoint() {
    }

}
