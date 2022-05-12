package com.joyintech.tams.cfets.common.aop;

import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.exception.BusinessException;
import com.joyintech.tams.business.common.dto.CfetsOprHisInfoRequest;
import com.joyintech.tams.business.common.service.JoyinbusinessCommonService;
import com.joyintech.tams.cfets.common.servive.CfetsCommonOprHisHandlerAopService;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.workflow.engine.constant.OperationType;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * cfets 公共AOP
 *
 * @author jiangpeng
 * @date 2022/4/26 18:40
 */
@Component
@Aspect
public class CfetsCommonOprHisHandlerAop {

    /**
     * 公共方法service
     */
    @Autowired
    private JoyinbusinessCommonService joyinbusinessCommonService;

    @Autowired
    private CfetsCommonOprHisHandlerAopService cfetsCommonOprHisHandlerAopService;

    @Pointcut("execution(* com.joyintech.tams.cfets..*.*.doInsert(..))")
    protected void insertPoint() {
    }

    @Pointcut("execution(* com.joyintech.tams.cfets..*.*.doUpdate(..))")
    protected void updatePoint() {
    }

    @Pointcut("execution(* com.joyintech.tams.cfets..*.*.giveUpEntrust(..))")
    protected void giveUpEntrustPoint() {
    }

    @Pointcut("execution(* com.joyintech.tams.cfets..*.*.receiveEntrust(..))")
    protected void receiveEntrustPoint() {
    }

    @Pointcut("execution(* com.joyintech.tams.cfets..*.*.entrustRefuce(..))")
    protected void entrustRefucePoint() {
    }

    /**
     * 提交及更新时更新细操作履历表
     */
    @AfterReturning("insertPoint() || updatePoint()")
    public void updateCfetsOprHis(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        OperationType operationType = OperationType.NEW;
        if (args.length > 1) {
            operationType = (OperationType) args[1];
        }
        Map<String, Object> resultMap = cfetsCommonOprHisHandlerAopService.getCfetsOprHisInfo(args[0], operationType);
        String trdId = (String) resultMap.get("trdId");
        String busiDes = (String) resultMap.get("busiDes");
        String type = (String) resultMap.get("type");
        String workflowId = (String) resultMap.get("workflowId");
        if (StringUtil.isNotEmpty(trdId) && StringUtil.isNotEmpty(busiDes) && StringUtil.isNotEmpty(type)) {
            this.updateCfetsOprHis(busiDes, trdId, type, workflowId);
        }
    }

    /**
     * 放弃指令时更新细操作履历表
     */
    @AfterReturning(value = "giveUpEntrustPoint()")
    public void updateCfetsOprHisForGiveUpEntrust(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        String entrustId = (String) args[0];
        String busiDes = "指令放弃";
        String type = DictCodeListExt3.操作类型_放弃.getValue();
        if (StringUtil.isNotEmpty(entrustId) && StringUtil.isNotEmpty(busiDes) && StringUtil.isNotEmpty(type)) {
            this.updateCfetsOprHis(busiDes, entrustId, type, null);
        }
    }

    /**
     * 接受指令时更新细操作履历表
     */
    @AfterReturning(value = "receiveEntrustPoint()")
    public void updateCfetsOprHisForReceiveEntrust(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        Map param = (Map) args[0];
        List<String> entrustIds = StringUtil.splitToList(GlobalConstant.COMMA, (String) param.get("entrustIds"));
        String busiDes = "指令接受";
        String type = DictCodeListExt3.操作类型_接收.getValue();
        if (StringUtil.isNotEmpty(busiDes) && StringUtil.isNotEmpty(type)) {
            entrustIds.forEach(entrustId -> this.updateCfetsOprHis(busiDes, entrustId, type, null));
        }
    }

    /**
     * 退回指令时更新细操作履历表
     */
    @AfterReturning(value = "entrustRefucePoint()")
    public void updateCfetsOprHisForRntrustRefucePoint(JoinPoint joinPoint) throws BusinessException {
        Object[] args = joinPoint.getArgs();
        Map param = (Map) args[0];
        List<String> entrustIdList = com.joyintech.framework.util.StringUtil.splitToList(",", (String) param.get("entrustId"));
        String busiDes = (String) param.get("rekReason");
        String type = DictCodeListExt3.操作类型_退回.getValue();
        //更新操作记录日志
        if (StringUtil.isNotEmpty(busiDes) && StringUtil.isNotEmpty(type)) {
            entrustIdList.forEach(entrustId -> this.updateCfetsOprHis(busiDes, entrustId, type, null));
        }
    }

    /**
     * 更新操作履历
     *
     * @param busiDes 业务说明
     * @param trdId   业务编号
     * @param type    操作类型
     * @author jiangpeng
     * @date 2022/4/26 10:35
     */
    private void updateCfetsOprHis(String busiDes, String trdId, String type, String workflowId) {
        CfetsOprHisInfoRequest oprHisReq = new CfetsOprHisInfoRequest();
        oprHisReq.setEntrustId(trdId);
        oprHisReq.setBusiDes(busiDes);
        oprHisReq.setOperType(type);
        oprHisReq.setWorkflowId(workflowId);
        joyinbusinessCommonService.updateCfetsOprHis(oprHisReq);
    }

}
