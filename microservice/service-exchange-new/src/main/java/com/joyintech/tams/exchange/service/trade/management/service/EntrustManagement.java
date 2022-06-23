package com.joyintech.tams.exchange.service.trade.management.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Strings;
import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeList;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.microservice.ServiceApplication;
import com.joyintech.tams.exchange.common.microservice.TradeServiceURL;
import com.joyintech.tams.exchange.common.utils.EntrustUtils;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.datacommon.db.service.*;
import com.joyintech.tams.exchange.service.trade.processor.TradeHelper;
import com.joyintech.tams.exchange.service.trade.processor.msg.MsgReceiverManager;
import com.joyintech.tams.exchange.service.trade.processor.msg.MsgType;
import com.joyintech.tams.exchange.service.trade.processor.msg.TradeMsgReceiver;
import com.joyintech.tams.exchange.service.trade.processor.others.TerminalHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class EntrustManagement extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(EntrustManagement.class);

    @Autowired
    private EntrustService entrustService;

    @Autowired
    private IntegratedEntrustService integratedEntrustService;

    @Autowired
    private TradeHelper tradeHelper;

    @Autowired
    private TerminalHelper terminalHelper;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MsgReceiverManager msgReceiverManager;

    @Autowired
    private EntrustHistoryService entrustHistoryService;

    @Autowired
    private RevokeManagement revokeManagement;

    @Autowired
    private InvestmentManagement investmentManagement;

    @Autowired
    private IntegratedEntrustHistoryService integratedEntrustHistoryService;

    @Autowired
    private TrdExchangeWorkflowService trdExchangeWorkflowService;
    @Autowired
    private CommonManagement commonManagement;

    @Autowired(required = false)
    @Qualifier("simpleRestTemplate")
    protected JoyinRestTemplate simpleRestTemplate;

    /**
     * 根据userId查询Entrust中数据
     */
    public ResultBean queryEntrust(String userId, String instructionId, String accountId, String orderId) {
        List<String> ptlAuthList = commonManagement.getPtlAuthInfo(userId);//组合权限查询
        if (Objects.isNull(ptlAuthList) || ptlAuthList.size() == 0) {
            if (!Strings.isNullOrEmpty(instructionId)) {
                return new ResultBean(ErrorCode.SUCCESS, new OrderAndEntrustQryResult(new ArrayList<>(), new ArrayList<>()));
            }
            return new ResultBean(ErrorCode.SUCCESS, new ArrayList());
        }
        // 查询具体订单下的委托
        if (!Strings.isNullOrEmpty(orderId)) {
            List<Entrust> entrustList = entrustService.queryEntrustByOrderId(orderId);
            entrustList = entrustList.stream().filter(entrust -> !EntrustType.CANCEL.value.equals(entrust.getEntrustType()) && ptlAuthList.contains(entrust.getPortfolioId())).collect(Collectors.toList());
            return new ResultBean(ErrorCode.SUCCESS, new OrderAndEntrustQryResult(new ArrayList<>(), entrustList));
        }
        // 查询指令下的订单和委托
        if (!Strings.isNullOrEmpty(instructionId)) {
            List<Entrust> entrustList = entrustService.queryEntrustByInstructionId(instructionId);
            entrustList = entrustList.stream().filter(entrust -> !EntrustType.CANCEL.value.equals(entrust.getEntrustType()) && ptlAuthList.contains(entrust.getPortfolioId())).collect(Collectors.toList());
            setFutureEntrustStockCode(entrustList);
            List<Order> orderListByInstructionId = orderService.queryOrderByInstructionId(instructionId);
            List<Order> orderListWithOutFair = orderListByInstructionId.stream().filter(order -> !BusinessFlag.FAIR.value.equals(order.getBusinessFlag()) && ptlAuthList.contains(order.getPortfolioId())).collect(Collectors.toList());
            setFutureOrderStockCode(orderListWithOutFair);
            return new ResultBean(ErrorCode.SUCCESS, new OrderAndEntrustQryResult(orderListWithOutFair, entrustList));
        }
        // 查询用户的委托
        List<Entrust> entrustList = entrustService.queryByUserId(userId);
        List<Order> orderList = orderService.queryOrder(userId);
        if (!Strings.isNullOrEmpty(accountId)) {
            entrustList = entrustList.stream().filter(entrust -> accountId.equals(entrust.getAccountId()) && !EntrustType.CANCEL.value.equals(entrust.getEntrustType()) && ptlAuthList.contains(entrust.getPortfolioId())).collect(Collectors.toList());
            orderList = orderList.stream().filter(order -> !BusinessFlag.FAIR.value.equals(order.getBusinessFlag()) && accountId.equals(order.getAccountId()) && ptlAuthList.contains(order.getPortfolioId())).collect(Collectors.toList());
        } else {
            entrustList = entrustList.stream().filter(entrust -> !EntrustType.CANCEL.value.equals(entrust.getEntrustType()) && ptlAuthList.contains(entrust.getPortfolioId())).collect(Collectors.toList());
            orderList = orderList.stream().filter(order -> !BusinessFlag.FAIR.value.equals(order.getBusinessFlag()) && ptlAuthList.contains(order.getPortfolioId())).collect(Collectors.toList());
        }
        setFutureEntrustStockCode(entrustList);
        setFutureOrderStockCode(orderList);
        return new ResultBean(ErrorCode.SUCCESS, new OrderAndEntrustQryResult(orderList, entrustList));
    }

    private void setFutureOrderStockCode(List<Order> orderListWithOutFair) {
        orderListWithOutFair.forEach(order -> {
            if (InvestmentAssetType.isFutures(order.getAssetType())) {
                order.setStockCode(StockCodeUtils.getStockCode(order.getStockCode()));
            }
        });
    }

    private void setFutureEntrustStockCode(List<Entrust> entrustList) {
        entrustList.forEach(entrust -> {
            if (InvestmentAssetType.isFutures(entrust.getAssetType())) {
                entrust.setStockCode(StockCodeUtils.getStockCode(entrust.getStockCode()));
            }
        });
    }

    /**
     * 综合业务查询
     */

    public ResultBean queryIntegratedEntrust(String userId, String instructionId, String accountId, String orderId) {
        List<String> ptlAuthList = commonManagement.getPtlAuthInfo(userId);//组合权限查询
        if (Objects.isNull(ptlAuthList) || ptlAuthList.size() == 0) {
            return new ResultBean(ErrorCode.SUCCESS, new ArrayList());
        }
        if (!Strings.isNullOrEmpty(orderId)) {
            List<IntegratedEntrust> entrustList = integratedEntrustService.queryIntegratedEntrustByOrderId(orderId);
            entrustList = entrustList.stream().filter(entrust -> !EntrustType.CANCEL.value.equals(entrust.getEntrustType()) && ptlAuthList.contains(entrust.getPortfolioId())).collect(Collectors.toList());
            return new ResultBean(ErrorCode.SUCCESS, entrustList);
        }
        if (!Strings.isNullOrEmpty(instructionId)) {
            List<IntegratedEntrust> entrustList = integratedEntrustService.queryIntegratedEntrustByInstructionId(instructionId);
            entrustList = entrustList.stream().filter(entrust -> !EntrustType.CANCEL.value.equals(entrust.getEntrustType()) && ptlAuthList.contains(entrust.getPortfolioId())).collect(Collectors.toList());
            return new ResultBean(ErrorCode.SUCCESS, entrustList);
        }
        List<IntegratedEntrust> entrustList = integratedEntrustService.queryByUserId(userId);
        if (!Strings.isNullOrEmpty(accountId)) {
            entrustList = entrustList.stream().filter(entrust -> accountId == entrust.getAccountId() && !EntrustType.CANCEL.value.equals(entrust.getEntrustType()) && ptlAuthList.contains(entrust.getPortfolioId())).collect(Collectors.toList());
        } else {
            entrustList = entrustList.stream().filter(entrust -> !EntrustType.CANCEL.value.equals(entrust.getEntrustType()) && ptlAuthList.contains(entrust.getPortfolioId())).collect(Collectors.toList());
        }
        return new ResultBean(ErrorCode.SUCCESS, entrustList);
    }

    public ResultBean cancelEntrust(String userId, String entrustId) {
        EntrustInterface entrust;
        if (entrustId.startsWith(IDPrefix.NORMAL.value)) {
            entrust = entrustService.findById(entrustId);
        } else {
            entrust = integratedEntrustService.findById(entrustId);
        }
        if (Objects.nonNull(entrust)) {
            if (EntrustUtils.hasNoCancelEntrust(entrust.getEntrustStatus())) {
                logger.info("entrust entrustStatus is {} ,no cancelEntrust", entrust.getEntrustStatus());
                return new ResultBean(ErrorCode.CANCEL_ENTRUST_STATUS_ERROR);
            }
            EntrustInterface cancelEntrust = getCancelEntrust(entrust);
            TradeMsgReceiver msgReceiver = msgReceiverManager.getMsgReceiverByInstruction(entrust.getInstructionId());
            msgReceiver.addMsg(MsgType.REQ_ENTRUST_CANCEL, cancelEntrust);
            return new ResultBean(ErrorCode.SUCCESS);
        }
        return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR);
    }

    public ResultBean cancelExceptionEntrust(String userId, String userName, String entrustId) {
        logger.debug("异常委托撤销, 用户id: {}, 用户名称: {}, 委托id: {}", userId, userName, entrustId);
        EntrustInterface entrust;
        if (EntrustInterface.isIntegratedEntrust(entrustId)) {
            entrust = integratedEntrustService.findById(entrustId);
        } else {
            entrust = entrustService.findById(entrustId);
        }
        if (Objects.nonNull(entrust)) {
            if (!EntrustStatus.isExceptionEntrust(entrust.getEntrustStatus())) {
                logger.info("异常委托撤销失败, 委托状态错误: {}", entrust.getEntrustStatus());
                return ResultBean.error(ErrorCode.ENTRUST_STATUS_ERROR);
            }
            EntrustInterface cancelEntrust = getCancelEntrust(entrust, userId, userName);
            TradeMsgReceiver msgReceiver = msgReceiverManager.getMsgReceiverByInstruction(entrust.getInstructionId());
            msgReceiver.addMsg(MsgType.REQ_CLOSE_ENTRUST, cancelEntrust);
            logger.debug("异常委托撤销成功");
            return ResultBean.success();
        }
        logger.info("异常委托撤销失败, 委托不存在: {}", entrustId);
        return ResultBean.error(ErrorCode.ENTRUST_NOT_EXITS);
    }

    public boolean isFairEntrust(EntrustInterface entrust) {
        String orderId = entrust.getParentOrderId();
        if (StringUtil.isNotEmpty(orderId) && !EntrustInterface.isIntegratedEntrust(entrust.getId())) {
            Order order = orderService.findById(orderId);
            return ObjectUtil.isNotNull(order) && BusinessFlag.FAIR.value.equals(order.getBusinessFlag());
        }
        return false;
    }

    private void asyncEntrustOrIntegrateEntrust(EntrustInterface entrust) {
        logger.debug("async entrust [{}]", JSONUtil.toJsonStr(entrust));
        String entrustId = entrust.getId();
        if (EntrustInterface.isIntegratedEntrust(entrustId)) {
            integratedEntrustService.asyncUpdate((IntegratedEntrust) entrust);
        } else {
            entrustService.asyncUpdate((Entrust) entrust);
        }
    }

    private void innerCannelEntrust(EntrustInterface entrust) {
        EntrustInterface cancelEntrust = getCancelEntrust(entrust);
        TradeMsgReceiver msgReceiver = msgReceiverManager.getMsgReceiverByInstruction(entrust.getInstructionId());
        msgReceiver.addMsg(MsgType.UNFROZEN_INNER, cancelEntrust);
        asyncEntrustOrIntegrateEntrust(entrust);
    }

    private boolean checkRevokeWithRiskOrReview(EntrustInterface entrust) {
        String entrustId = entrust.getId();
        boolean result = false;
        if (entrust.getEntrustStatus() == EntrustStatus.PENDING_APPROVAL.value) {
            // 查询流程中状态
            String processState = trdExchangeWorkflowService.qureyStateByEntrustId(entrustId);
            logger.info("checkRevoke entrust [{}], risk workflow state [{}]", entrustId, processState);
            if (DictCodeList.流程状态_流程结束.getValue().equals(processState)) {
                // 风险通过，查询是否需要再次复核
                WorkflowBean workflow = RevokeManagement.getRevokeMap(entrustId);
                if (ObjectUtil.isNotEmpty(workflow)) {
                    // 内部撤单
                    innerCannelEntrust(entrust);
                    result = true;
                }
                // 走下面的撤审流程
            } else if (DictCodeList.流程状态_已退回.getValue().equals(processState)) {
                // 风险拒绝，直接返回
                result = true;
            } else if (DictCodeList.流程状态_流程中.getValue().equals(processState)) {
                // 风险暂未处理，撤销风险流程，
                // 作废流程
                boolean isRevoke = revokeManagement.doRevoke(entrustId);
                if (isRevoke) {
                    innerCannelEntrust(entrust);
                    result = true;
                }
            }
            // 待复核单独处理
        } else if (entrust.getEntrustStatus() == EntrustStatus.PENDING_REVIEW.value) {
            // 查询流程中状态
            String processState = trdExchangeWorkflowService.qureyStateByEntrustId(entrustId);
            logger.info("checkRevoke entrust [{}], review workflow state [{}]", entrustId, processState);
            if (DictCodeList.流程状态_流程结束.getValue().equals(processState)) {
                // 复核同意，
                // 走下面的撤审流程
            } else if (DictCodeList.流程状态_已退回.getValue().equals(processState)) {
                // 复核拒绝，直接返回
                result = true;
            } else if (DictCodeList.流程状态_流程中.getValue().equals(processState)) {
                // 复核暂未处理，撤销复核流程，
                // 作废流程
                boolean isRevoke = revokeManagement.doRevoke(entrustId);
                if (isRevoke) {
                    innerCannelEntrust(entrust);
                    result = true;
                }
            }
        }
        return result;
    }

    private boolean checkRevoke(EntrustInterface entrust) {
        String entrustId = entrust.getId();
        if (EntrustStatus.isFinished(entrust.getEntrustStatus())) {
            logger.info("委托是完结状态，不允许发起撤销审批");
            return false;
        }
        // 公平委托且还处于待复核或待审批状态时，不进行撤审
        if (isFairEntrust(entrust)) {
            if (entrust.getEntrustStatus() == EntrustStatus.PENDING_APPROVAL.value
                    || entrust.getEntrustStatus() == EntrustStatus.PENDING_REVIEW.value) {
                logger.info("公平委托且处在流程中，不允许发起撤销审批");
                return false;
            }
        }
        // 单独处理待审批和待复核
        if (checkRevokeWithRiskOrReview(entrust)) {
            return true;
        }
        WorkflowBean workflow = RevokeManagement.getRevokeMap(entrustId);
        if (ObjectUtil.isNotEmpty(workflow)) {
            logger.info("handle cancelEntrust need revoke {}", entrustId);
            EntrustInterface cancelEntrust = getCancelEntrust(entrust);
            revokeManagement.handleRevokeWorkflow(entrust, workflow, cancelEntrust);
            asyncEntrustOrIntegrateEntrust(entrust);
            asyncEntrustOrIntegrateEntrust(cancelEntrust);
            logger.debug("checkRevoke entrust {}, cancelEntrust {}", JSONUtil.toJsonStr(entrust), JSONUtil.toJsonStr(cancelEntrust));
            return true;
        }
        return false;
    }

    private boolean checkParamsOfRevokeDto(RevokeDto dto) {
        if (ObjectUtil.hasEmpty(dto.getOrderID(), dto.getPageId(), dto.getElementId(), dto.getUsers(), dto.getWorkflowParameter())) {
            logger.error("cancelEntrustWithRevoke error: workflow params is null or empty");
            return true;
        }
        return false;
    }

    public ResultBean cancelEntrustWithRevoke(String userId, RevokeDto dto) {
        logger.debug("cancelEntrustWithRevoke revokeDto [{}]", JSONUtil.toJsonStr(dto));
        if (checkParamsOfRevokeDto(dto)) {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR);
        }
        String entrustId = dto.getOrderID();
        WorkflowBean workflow = BeanUtil.toBean(dto, WorkflowBean.class);
        RevokeManagement.putRevokeMap(entrustId, workflow);
        EntrustInterface entrust;
        if (entrustId.startsWith(IDPrefix.NORMAL.value)) {
            entrust = entrustService.findById(entrustId);
        } else {
            entrust = integratedEntrustService.findById(entrustId);
        }
        if (ObjectUtil.isEmpty(entrust)) {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR);
        }
        if (EntrustUtils.hasNoCancelEntrust(entrust.getEntrustStatus())) {
            logger.info("entrust entrustStatus is {} ,no cancelEntrust", entrust.getEntrustStatus());
            return new ResultBean(ErrorCode.CANCEL_ENTRUST_STATUS_ERROR);
        }
        List<EntrustInterface> cancelEntrustOther = new ArrayList<EntrustInterface>();//当前委托新增撤单委托
        if (entrustId.startsWith(IDPrefix.NORMAL.value)) {
            cancelEntrustOther.addAll(entrustService.queryCancelEntrustByOrigEntrustIdAndAccount(entrustId, entrust.getAccountId()));
        } else {
            cancelEntrustOther.addAll(integratedEntrustService.queryCancelIntegratedEntrustByOrigEntrustIdAndAccount(entrustId, entrust.getAccountId()));
        }
        if (Objects.nonNull(cancelEntrustOther) && cancelEntrustOther.size() > 0) {
            for (EntrustInterface cancelEntrust : cancelEntrustOther) {
                if (EntrustStatus.PENDING_REVOKE.value == cancelEntrust.getEntrustStatus()) {
                    logger.info("cancelEntrust entrustStatus is {} ,no cancelEntrust", cancelEntrust.getEntrustStatus());
                    return new ResultBean(ErrorCode.CANCEL_OTHER_ENTRUST_STATUS_ERROR);
                }
            }
        }
        if (!checkRevoke(entrust)) {
            EntrustInterface cancelEntrust = getCancelEntrust(entrust);
            TradeMsgReceiver msgReceiver = msgReceiverManager.getMsgReceiverByInstruction(entrust.getInstructionId());
            msgReceiver.addMsg(MsgType.REQ_ENTRUST_CANCEL, cancelEntrust);
        }
        return new ResultBean(ErrorCode.SUCCESS);
    }

    public ResultBean queryIntegratedQuote(String userId, int quoteType, String stockCode, String accountId, String oppositeLiaisonTel, String liaisonTel, int pageSize, String posStr) {
        List<IntegratedQuote> integratedQuotes = terminalHelper.queryIntegratedQuote(quoteType, stockCode, accountId, oppositeLiaisonTel, liaisonTel, pageSize, posStr);
        return new ResultBean(ErrorCode.SUCCESS, integratedQuotes);
    }

    public ResultBean queryEnable(String userId, String stockCode, String entrustProp, String entrustBs, double entrustPrice, String accountId) {
        int maxEntrustVolume = terminalHelper.queryEnable(stockCode, entrustProp, entrustBs, entrustPrice, accountId);
        return new ResultBean(ErrorCode.SUCCESS, maxEntrustVolume);
    }

    /***
     * @Description: 申报代码获取
     * @Param: [finprodId, othCodeType]
     * @return: com.joyintech.tams.exchange.common.bean.ResultBean
     * @Author: MAXX
     * @Date: 2020/6/5
     */
    public ResultBean getOthCode(String finprodId, String othCodeType) {
        try {
            Map<String, Object> param = new HashMap<String, Object>();
            if (!Strings.isNullOrEmpty(othCodeType)) {
                param.put("othCodeType", othCodeType);
            }
            if (!Strings.isNullOrEmpty(finprodId)) {
                param.put("finprodId", finprodId);
            }
            List<OthCodeBean> list = null;
            if (isSingle) {
                list = simpleRestTemplate.postForList(ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.selectForInterf, param, OthCodeBean.class);
            } else {
                list = restTemplate.postForList(ServiceApplication.BASIC_DATA_SERVICE + TradeServiceURL.selectForInterf, param, OthCodeBean.class);
            }
            return new ResultBean(ErrorCode.SUCCESS, list);
        } catch (Exception e) {
            logger.info("getOthCode error:{}", e);
        }
        return new ResultBean(ErrorCode.SYSTEM_ERROR);
    }

    public ResultBean queryAllForPage(int pageNum, int pageSize, int historyType, String userId, String startDay, String endDay, String accountId, String portfolioId, String market, String stockCode, String entrustProp, int entrustStatus) {
        if (historyType == PresentOrHistoryQryType.PRESENT.value) {
            PageInfo<Entrust> entrustPageInfo = entrustService.queryAllForPage(pageNum, pageSize, accountId, portfolioId, market, stockCode, entrustProp, entrustStatus);
            return new ResultBean(ErrorCode.SUCCESS, entrustPageInfo);
        } else if (historyType == PresentOrHistoryQryType.HISTORY.value) {
            PageInfo<EntrustHistory> entrustHistoryPageInfo = entrustHistoryService.queryAllForPage(pageNum, pageSize, startDay, endDay, accountId, portfolioId, market, stockCode, entrustProp, entrustStatus);
            return new ResultBean(ErrorCode.SUCCESS, entrustHistoryPageInfo);
        } else {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR);
        }
    }

    public ResultBean queryIntegratedEntrustForPage(int pageNum, int pageSize, int historyType, String userId, String startDay, String endDay, String accountId, String portfolioId, String market, String stockCode, String entrustProp, int entrustStatus) {
        List<String> ptlAuthList = commonManagement.getPtlAuthInfo(userId);//组合权限查询
        if (Objects.isNull(ptlAuthList) || ptlAuthList.size() == 0) {
            logger.info("用户：{}没有组合权限", userId);
            return new ResultBean(ErrorCode.SUCCESS, new PageInfo());
        }
        if (!Strings.isNullOrEmpty(portfolioId) && !ptlAuthList.contains(portfolioId)) {
            logger.info("用户：{}没有{}的组合权限", userId, portfolioId);
            return new ResultBean(ErrorCode.SUCCESS, new PageInfo());
        }
        if (historyType == PresentOrHistoryQryType.PRESENT.value) {
            PageInfo<IntegratedEntrust> integratedEntrustPageInfo = integratedEntrustService.queryAllForPage(pageNum, pageSize, accountId, portfolioId, market, stockCode, entrustProp, entrustStatus, ptlAuthList);
            return new ResultBean(ErrorCode.SUCCESS, integratedEntrustPageInfo);
        } else if (historyType == PresentOrHistoryQryType.HISTORY.value) {
            PageInfo<IntegratedEntrustHistory> integratedEntrustHistoryPageInfo = integratedEntrustHistoryService.queryAllForPage(pageNum, pageSize, userId, startDay, endDay, accountId, portfolioId, market, stockCode, entrustProp, entrustStatus, ptlAuthList);
            return new ResultBean(ErrorCode.SUCCESS, integratedEntrustHistoryPageInfo);
        } else {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR);
        }
    }

    public ResultBean queryAllEntrustForPage(int pageNum, int pageSize, int historyType, String userId, String startDay, String endDay, String accountId, String portfolioId, String market, String stockCode, String entrustProp, int entrustStatus) {
        List<String> ptlAuthList = commonManagement.getPtlAuthInfo(userId);//组合权限查询
        if (Objects.isNull(ptlAuthList) || ptlAuthList.size() == 0) {
            logger.info("用户：{}没有组合权限", userId);
            return new ResultBean(ErrorCode.SUCCESS, new PageInfo());
        }
        if (!Strings.isNullOrEmpty(portfolioId) && !ptlAuthList.contains(portfolioId)) {
            logger.info("用户：{}没有{}的组合权限", userId, portfolioId);
            return new ResultBean(ErrorCode.SUCCESS, new PageInfo());
        }
        if (historyType == PresentOrHistoryQryType.PRESENT.value) {
            PageInfo<IntegratedEntrust> integratedEntrustPageInfo = integratedEntrustService.queryAllEntrustForPage(pageNum, pageSize, userId, accountId, portfolioId, market, stockCode, entrustProp, entrustStatus, ptlAuthList);
            return new ResultBean(ErrorCode.SUCCESS, integratedEntrustPageInfo);
        } else if (historyType == PresentOrHistoryQryType.HISTORY.value) {
            PageInfo<IntegratedEntrustHistory> integratedEntrustHistoryPageInfo = integratedEntrustHistoryService.queryAllEntrustForPage(pageNum, pageSize, userId, startDay, endDay, accountId, portfolioId, market, stockCode, entrustProp, entrustStatus, ptlAuthList);
            return new ResultBean(ErrorCode.SUCCESS, integratedEntrustHistoryPageInfo);
        } else {
            return new ResultBean(ErrorCode.REQ_PARAMETER_ERROR);
        }
    }

    public ResultBean queryEntrustByStatus(String userId, String entrustStatus) {
        List<String> ptlAuthList = commonManagement.getPtlAuthInfo(userId);//组合权限查询
        if (Objects.isNull(ptlAuthList) || ptlAuthList.size() == 0) {
            logger.info("用户：{}没有组合权限", userId);
            return new ResultBean(ErrorCode.SUCCESS, new ArrayList());
        }
        logger.debug("根据委托状态查询委托, 用户id: {}, 委托状态: {}", userId, entrustStatus);
        List<IntegratedEntrust> integratedEntrustList = integratedEntrustService.queryIntegratedEntrustByStatus(userId, entrustStatus, ptlAuthList);
        List<Entrust> entrustList = entrustService.queryEntrustByStatus(userId, entrustStatus, ptlAuthList);
        List<EntrustInterface> list = new ArrayList<>();
        list.addAll(entrustList);
        list.addAll(integratedEntrustList);
        logger.debug("根据委托状态查询委托结果: {}", list.toString());
        return new ResultBean(ErrorCode.SUCCESS, list);
    }

    public EntrustInterface getCancelEntrust(EntrustInterface entrust) {
        EntrustInterface cancelEntrust;
        if (entrust.getId().startsWith(IDPrefix.NORMAL.value)) {
            cancelEntrust = new Entrust();
            cancelEntrust.setCreateTime(new Date(System.currentTimeMillis()));
            BeanUtils.copyProperties(entrust, cancelEntrust, "id", "entrustStatus", "frozenBalance", "frozenVolume", "tradedBalance", "tradedPrice", "tradedVolume", "cancelVolume", "entrustType", "localEntrustId", "brokerEntrustId", "origEntrustId");
            if (EntrustStatus.PART_SUCCESS.value == entrust.getEntrustStatus()) {
                cancelEntrust.setEntrustVolume(entrust.getEntrustVolume() - entrust.getTradedVolume());
            }
            cancelEntrust.setOrigEntrustId(entrust.getId());
            cancelEntrust.setEntrustType(EntrustType.CANCEL.value);
            cancelEntrust.setEntrustStatus(EntrustStatus.CREATED.value);
            entrustService.asyncUpdate((Entrust) cancelEntrust);
            logger.info("cancelEntrust new cancelEntrust:{}", JSONObject.toJSONString(cancelEntrust));
        } else {
            cancelEntrust = new IntegratedEntrust();
            cancelEntrust.setCreateTime(new Date(System.currentTimeMillis()));
            BeanUtils.copyProperties(entrust, cancelEntrust, "id", "entrustStatus", "frozenBalance", "frozenVolume", "tradedBalance", "tradedPrice", "tradedVolume", "cancelVolume", "entrustType", "localEntrustId", "brokerEntrustId", "origEntrustId");
            if (EntrustStatus.PART_SUCCESS.value == entrust.getEntrustStatus()) {
                cancelEntrust.setEntrustVolume(entrust.getEntrustVolume() - entrust.getTradedVolume());
            }
            cancelEntrust.setOrigEntrustId(entrust.getId());
            cancelEntrust.setEntrustType(EntrustType.CANCEL.value);
            cancelEntrust.setEntrustStatus(EntrustStatus.CREATED.value);
            integratedEntrustService.asyncUpdate((IntegratedEntrust) cancelEntrust);
            logger.info("cancelIntegratedEntrust new cancelEntrust:{}", JSONObject.toJSONString(cancelEntrust));
        }
        return cancelEntrust;
    }

    // 为撤销委托设置用户编号和用户名称
    public EntrustInterface getCancelEntrust(EntrustInterface entrust, String userId, String userName) {
        EntrustInterface cancelEntrust = getCancelEntrust(entrust);
        cancelEntrust.setUserId(userId);
        cancelEntrust.setUsername(userName);
        if (EntrustInterface.isIntegratedEntrust(cancelEntrust.getOrigEntrustId())) {
            integratedEntrustService.asyncUpdate((IntegratedEntrust) cancelEntrust);
        } else {
            entrustService.asyncUpdate((Entrust) cancelEntrust);
        }
        return cancelEntrust;
    }
}
