package com.joyintech.tams.exchange.service.trade.management.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.joyintech.framework.annotation.DTXTransaction;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.constant.ApproveType;
import com.joyintech.tams.exchange.common.constant.EntrustStatus;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.IDPrefix;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeList;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt3;
import com.joyintech.tams.exchange.common.db.domain.Entrust;
import com.joyintech.tams.exchange.common.db.domain.EntrustInterface;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.datacommon.db.service.EntrustService;
import com.joyintech.tams.exchange.datacommon.db.service.IntegratedEntrustService;
import com.joyintech.tams.exchange.datacommon.db.service.TrdExchangeWorkflowService;
import com.joyintech.tams.exchange.service.trade.processor.business.common.EntrustCancelProcessor;
import com.joyintech.tams.exchange.service.trade.processor.constants.Constants;
import com.joyintech.tams.exchange.service.trade.processor.msg.MsgReceiverManager;
import com.joyintech.tams.exchange.service.trade.processor.msg.MsgType;
import com.joyintech.tams.exchange.service.trade.processor.msg.TradeMsgReceiver;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.dto.ProcessParameter;
import com.joyintech.workflow.engine.dto.WorkFlowParameter;
import com.joyintech.workflow.engine.exception.WorkFlowException;
import com.joyintech.workflow.engine.util.WorkFlowUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @ClassName RevokeManagement
 * @Description TODO
 * @Author swwan
 * @Date 2020/7/29 17:16
 * @Version 1.0
 **/
@Lazy
@Service("RevokeManagement_dtx")
public class RevokeManagement {
    private static final Logger logger = LoggerFactory.getLogger(RevokeManagement.class);

    @Autowired
    private EntrustService entrustService;

    @Autowired
    private IntegratedEntrustService integratedEntrustService;

    @Autowired
    private MsgReceiverManager msgReceiverManager;

    @Autowired
    private TrdExchangeWorkflowService trdExchangeWorkflowService;

    @Autowired
    private EntrustCancelProcessor entrustCancelProcessor;
    // 记录已经登录的账号
    private static final Map<String, WorkflowBean> revokeMap = Maps.newConcurrentMap();

    public static WorkflowBean getRevokeMap(String id) {
        return revokeMap.get(id);
    }

    public static void putRevokeMap(String id, WorkflowBean workflowBean) {
        revokeMap.put(id, workflowBean);
    }

    public static void removeRevokeMap(String id) {
        revokeMap.remove(id);
    }

    private ResultBean handleNormal(EntrustInterface entrust, String approveStatus, String approveReason, int type, EntrustInterface cancelEntrust) {
        TradeMsgReceiver tradeMsgReceiver = msgReceiverManager.getMsgReceiverByInstruction(entrust.getInstructionId());
        tradeMsgReceiver.addMsg(MsgType.EXAMINE_REVOKE, new ExamineOrder(approveStatus, approveReason, cancelEntrust, type));
        return new ResultBean(ErrorCode.SUCCESS, entrust);
    }

    /**
     * 处理撤审审批结果
     *
     * @param
     * @return
     */
    public ResultBean handleExamine(String eventId, String approveStatus, String approveReason) {
        EntrustInterface cancelEntrust = null;
        if (eventId.startsWith(IDPrefix.NORMAL.value)) {
            cancelEntrust = entrustService.findById(eventId);
        } else if (eventId.startsWith(IDPrefix.BULK.value)) {
            cancelEntrust = integratedEntrustService.findById(eventId);
        }
        if (Objects.isNull(cancelEntrust)) {
            return new ResultBean(ErrorCode.INSTRUCTIONS_NOT_EXIST, eventId);
        }
        String entrustId = cancelEntrust.getOrigEntrustId();
        EntrustInterface entrust = null;
        List<Entrust> entrusts = null;
        int type = 0;
        if (entrustId.startsWith(IDPrefix.NORMAL.value)) {
            entrust = entrustService.findById(entrustId);
            type = Constants.PROCESS_TYPE_ENTRUST;
        } else if (entrustId.startsWith(IDPrefix.BULK.value)) {
            entrust = integratedEntrustService.findById(entrustId);
            type = Constants.PROCESS_TYPE_INTEGRATED;
        } else if (entrustId.startsWith(IDPrefix.ORDER.value)) {//处理公平交易
            entrusts = entrustService.queryEntrustByOrderId(entrustId);
            type = Constants.PROCESS_TYPE_FIRE_ORDER;
        }
        if (Objects.isNull(entrust) && Objects.isNull(entrusts)) {
            return new ResultBean(ErrorCode.INSTRUCTIONS_NOT_EXIST, eventId);
        }
        if (ApproveType.EXAMINE_APPROVAL.value.equals(approveStatus) && EntrustStatus.isFinished(entrust.getEntrustStatus())) {
            cancelEntrust.setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
            cancelEntrust.setErrorMsg("委托已完成，不允许撤销");
            if (EntrustInterface.isIntegratedEntrust(entrustId)) {
                integratedEntrustService.asyncUpdate((IntegratedEntrust) cancelEntrust);
            } else {
                entrustService.asyncUpdate((Entrust) cancelEntrust);
            }
            return new ResultBean(ErrorCode.SUCCESS, entrust);
        }
        if (ApproveType.EXAMINE_REFUSE.value.equals(approveStatus) && Strings.isNullOrEmpty(approveReason)) {
            //todo 此处原委托状态没回滚
            cancelEntrust.setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
            cancelEntrust.setErrorMsg("拒绝原因不可以为空");
            if (EntrustInterface.isIntegratedEntrust(entrustId)) {
                integratedEntrustService.asyncUpdate((IntegratedEntrust) cancelEntrust);
            } else {
                entrustService.asyncUpdate((Entrust) cancelEntrust);
            }
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR, entrust);
        }
        return handleNormal(entrust, approveStatus, approveReason, type, cancelEntrust);
        //        // 根据类型分别处理委托
        //        if (type == Constants.PROCESS_TYPE_FIRE_ORDER) {// 公平
        //            return handleOrder(entrusts,approveStatus,approveReason);
        //        } else {
        //            return handleNormal(entrust,approveStatus,approveReason,type);
        //        }
    }

    @DTXTransaction(propagation = Propagation.REQUIRES_NEW)
    @Transactional(rollbackFor = {Exception.class})
    public boolean startWorkflow(TrdExchangeIntegratedEntrustDto dto) {
        logger.info("start revoke workflow [{}]", JsonUtils.getJsonFromObject(dto));
        ProcessParameter<TrdExchangeIntegratedEntrustDto> workFlowPatameter = getWorkFlowPatameter(dto);
        try {
            WorkFlowUtil.startProcess(workFlowPatameter.getWorkFlowParameter(), dto.getUsers(), dto.getRoles(), dto.getEntrustList().get(0).getUserId(),
                    OperationType.NEW, true, dto);
            logger.info("start revoke workflow success.");
            return true;
        } catch (Exception e) {
            logger.error("start revoke workflow failed with error : [{}]", e.getMessage());
            return false;
        }
    }

    /**
     * 设置流程参数
     *
     * @param dto
     * @return
     */
    private ProcessParameter<TrdExchangeIntegratedEntrustDto> getWorkFlowPatameter(TrdExchangeIntegratedEntrustDto dto) {
        dto.setWorkflowId(null);
        dto.setOperateType("submit");
        ProcessParameter<TrdExchangeIntegratedEntrustDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdExchangeIntegratedEntrustDto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        // 画面ID
        processParameter.setPageId("TrdExchange005");
        // 画面元素ID
        workFlowParameter.setElementId(dto.getElementId());
        processParameter.setOperationType(OperationType.NEW);
        workFlowParameter.setBusinessCode(dto.getEntrustList().get(0).getId());
        // 处理Service名
        workFlowParameter.setServiceName("TrdExchangePage005Service");
        workFlowParameter.setPageId("TrdExchange005");
        workFlowParameter.setBusinessType(DictCodeListExt3.共通业务类型_场内交易撤销.getValue());
        // 组合名称 资金账号 证券代码  证券名称  委托方向  委托价格 委托数量  委托金额  申报类型  清算速度  净价价格  全价价格 到期收益率  本方交易员 对手交易商 对手交易员  交易对手 约定号
        workFlowParameter.setComment(
                entity -> (StringUtil.isNotEmpty(entity.getEntrustList().get(0).getStockCode()) ?
                        (entity.getEntrustList().get(0).getStockName()
                                + GlobalConstant.VERTICAL_LINE + entity.getEntrustList().get(0).getStockCode()) : "")
                        + (StringUtil.isNotEmpty(entity.getEntrustList().get(0).getId()) ?
                        (GlobalConstant.SPLIT + "委托编号"
                                + GlobalConstant.OPEN_PARENTHESIS + entity.getEntrustList().get(0).getId() + GlobalConstant.CLOSE_PARENTHESIS) : "")
                        + (StringUtil.isNotEmpty(entity.getEntrustList().get(0).getParentOrderId()) ?
                        (GlobalConstant.SPLIT + "订单编号"
                                + GlobalConstant.OPEN_PARENTHESIS + entity.getEntrustList().get(0).getParentOrderId() + GlobalConstant.CLOSE_PARENTHESIS) : "")
                        + (StringUtil.isNotEmpty(entity.getEntrustList().get(0).getEntrustVolume()) ?
                        (GlobalConstant.SPLIT + "委托数量"
                                + GlobalConstant.OPEN_PARENTHESIS + entity.getEntrustList().get(0).getEntrustVolume() + GlobalConstant.CLOSE_PARENTHESIS) : "")
                        + (StringUtil.isNotEmpty(entity.getEntrustList().get(0).getEntrustPrice()) ?
                        (GlobalConstant.SPLIT + "委托价格"
                                + GlobalConstant.OPEN_PARENTHESIS + entity.getEntrustList().get(0).getEntrustPrice() + GlobalConstant.CLOSE_PARENTHESIS) : ""));
        return processParameter;
    }

    @DTXTransaction(propagation = Propagation.REQUIRES_NEW)
    @Transactional(rollbackFor = {Exception.class})
    public boolean startWorkflow(TrdExchangeEntrustDto dto) {
        logger.info("start review workflow [{}]", JsonUtils.getJsonFromObject(dto));
        ProcessParameter<TrdExchangeEntrustDto> workFlowPatameter = getWorkFlowPatameter(dto);
        try {
            WorkFlowUtil.startProcess(workFlowPatameter.getWorkFlowParameter(), dto.getUsers(), dto.getRoles(), dto.getEntrustList().get(0).getUserId(),
                    OperationType.NEW, true, dto);
            logger.info("start revoke workflow success.");
            return true;
        } catch (Exception e) {
            logger.error("start revoke workflow failed with error : [{}]", e.getMessage());
            return false;
        }
    }

    /**
     * 设置流程参数
     *
     * @param dto
     * @return
     */
    private ProcessParameter<TrdExchangeEntrustDto> getWorkFlowPatameter(TrdExchangeEntrustDto dto) {
        dto.setWorkflowId(null);
        dto.setOperateType("submit");
        ProcessParameter<TrdExchangeEntrustDto> processParameter = new ProcessParameter<>();
        WorkFlowParameter<TrdExchangeEntrustDto> workFlowParameter = new WorkFlowParameter<>();
        processParameter.setWorkFlowParameter(workFlowParameter);
        // 画面ID
        processParameter.setPageId("TrdExchange004");
        // 画面元素ID
        workFlowParameter.setElementId(dto.getElementId());
        processParameter.setOperationType(OperationType.NEW);
        workFlowParameter.setBusinessCode(dto.getEntrustList().get(0).getId());
        // 处理Service名
        workFlowParameter.setServiceName("TrdExchangePage004Service");
        workFlowParameter.setPageId("TrdExchange004");
        workFlowParameter.setBusinessType(DictCodeListExt3.共通业务类型_场内交易撤销.getValue());
        // 组合名称 资金账号 证券代码  证券名称  委托方向  委托价格 委托数量  委托金额  申报类型  清算速度  净价价格  全价价格 到期收益率  本方交易员 对手交易商 对手交易员  交易对手 约定号
        workFlowParameter.setComment(
                entity -> (StringUtil.isNotEmpty(entity.getEntrustList().get(0).getStockCode()) ?
                        (entity.getEntrustList().get(0).getStockName()
                                + GlobalConstant.VERTICAL_LINE + entity.getEntrustList().get(0).getStockCode()) : "")
                        + (StringUtil.isNotEmpty(entity.getEntrustList().get(0).getId()) ?
                        (GlobalConstant.SPLIT + "委托编号"
                                + GlobalConstant.OPEN_PARENTHESIS + entity.getEntrustList().get(0).getId() + GlobalConstant.CLOSE_PARENTHESIS) : "")
                        + (StringUtil.isNotEmpty(entity.getEntrustList().get(0).getParentOrderId()) ?
                        (GlobalConstant.SPLIT + "订单编号"
                                + GlobalConstant.OPEN_PARENTHESIS + entity.getEntrustList().get(0).getParentOrderId() + GlobalConstant.CLOSE_PARENTHESIS) : "")
                        + (StringUtil.isNotEmpty(entity.getEntrustList().get(0).getEntrustVolume()) ?
                        (GlobalConstant.SPLIT + "委托数量"
                                + GlobalConstant.OPEN_PARENTHESIS + entity.getEntrustList().get(0).getEntrustVolume() + GlobalConstant.CLOSE_PARENTHESIS) : "")
                        + (StringUtil.isNotEmpty(entity.getEntrustList().get(0).getEntrustPrice()) ?
                        (GlobalConstant.SPLIT + "委托价格"
                                + GlobalConstant.OPEN_PARENTHESIS + entity.getEntrustList().get(0).getEntrustPrice() + GlobalConstant.CLOSE_PARENTHESIS) : ""));
        return processParameter;
    }

    public List<WorkFlowDto> selectWorkflow(String entrustId) {
        Map<String, Object> param = new HashMap<>();
        param.put("state", DictCodeList.流程状态_流程中.getValue());
        param.put("entrustId", entrustId);
        return trdExchangeWorkflowService.selectWorkflow(param);
    }

    // 流程作废
    @DTXTransaction(propagation = Propagation.REQUIRES_NEW)
    @Transactional(rollbackFor = {Exception.class})
    public boolean doRevoke(String entrustId) {
        logger.info("开始流程作废, 委托编号:[{}]", entrustId);

        boolean result = false;
        List<WorkFlowDto> workflowIds = selectWorkflow(entrustId);
        if (workflowIds != null && workflowIds.size() > 0) {
            for (WorkFlowDto flow : workflowIds) {
                try {
                    WorkFlowUtil.deleteProcess(flow.getSeqNo(), flow.getWorkflowId());
                    result = true;
                    logger.info("流程作废成功, 委托编号:[{}]", flow.getWorkflowId());
                } catch (WorkFlowException e) {
                    logger.error("流程作废异常, 委托编号:[{}] 异常信息: [{}]", flow.getWorkflowId(), e.getMessage());
                    return false;
                }
            }
        } else {
            logger.info("流程作废失败, 流程中无此委托:[{}]", entrustId);
        }
        return result;
    }

    @DTXTransaction(propagation = Propagation.REQUIRES_NEW)
    @Transactional(rollbackFor = {Exception.class})
    public void handleRevokeWorkflow(EntrustInterface entrust, WorkflowBean workflow, EntrustInterface cancelEntrust) {
        logger.debug("handleRevokeWorkflow entrust [{}], workflow [{}]", JSONUtil.toJsonStr(entrust), JSONUtil.toJsonStr(workflow));
        String entrustId = entrust.getId();
        boolean processResult = false;
        if (EntrustInterface.isIntegratedEntrust(entrustId)) {
            // 设置撤销数量
            IntegratedEntrust entrustCopy = new IntegratedEntrust();
            BeanUtil.copyProperties(entrust, entrustCopy);
            entrustCopy.setCancelVolume(entrustCopy.getEntrustVolume() - entrustCopy.getTradedVolume());
            TrdExchangeIntegratedEntrustDto dto = BeanUtil.toBean(workflow, TrdExchangeIntegratedEntrustDto.class);
            dto.setEntrustList(Arrays.asList(entrustCopy));
            dto.setEventId(cancelEntrust.getId());
            processResult = startWorkflow(dto);
        } else {
            Entrust entrustCopy = new Entrust();
            BeanUtil.copyProperties(entrust, entrustCopy);
            entrustCopy.setCancelVolume(entrustCopy.getEntrustVolume() - entrustCopy.getTradedVolume());
            TrdExchangeEntrustDto dto = BeanUtil.toBean(workflow, TrdExchangeEntrustDto.class);
            dto.setEntrustList(Arrays.asList(entrustCopy));
            dto.setEventId(cancelEntrust.getId());
            processResult = startWorkflow(dto);
        }
        removeRevokeMap(entrustId);
        if (processResult) {
            // 修改委托状态
            entrustCancelProcessor.setCancelStatus(entrust);
            cancelEntrust.setEntrustStatus(EntrustStatus.PENDING_REVOKE.value);
            logger.info("handleRevokeWorkflow start workflow [{}] success,cancelId:{}", entrustId, cancelEntrust.getId());
        } else {
            cancelEntrust.setEntrustStatus(EntrustStatus.SYSTEM_INVALID.value);
            cancelEntrust.setErrorMsg("撤审任务启动失败");
            logger.warn("handleRevokeWorkflow start workflow [{}] failed", entrustId);
        }
    }
}
