package com.joyintech.tams.exchange.service.trade.management.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeList;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.domain.Message;
import com.joyintech.tams.exchange.common.microservice.ServiceApplication;
import com.joyintech.tams.exchange.common.microservice.TradeServiceURL;
import com.joyintech.tams.exchange.common.request.PageEntrustInfoRequest;
import com.joyintech.tams.exchange.common.utils.EntrustUtils;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.datacommon.db.service.*;
import com.joyintech.tams.exchange.service.trade.processor.msg.MsgReceiverManager;
import com.joyintech.tams.exchange.service.trade.processor.msg.MsgType;
import com.joyintech.tams.exchange.service.trade.processor.msg.TradeMsgReceiver;
import com.joyintech.tams.exchange.service.trade.processor.others.TerminalHelper;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.*;
import java.util.stream.Collectors;

import static com.joyintech.tams.exchange.common.constant.EntrustProp.*;
import static com.joyintech.tams.exchange.common.constant.EntrustProp.ETF_APPLY;
import static com.joyintech.tams.exchange.common.constant.OthCodeType.*;

@Component
public class EntrustManagement extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(EntrustManagement.class);

    @Autowired
    private EntrustService entrustService;

    @Autowired
    private IntegratedEntrustService integratedEntrustService;

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
    private IntegratedEntrustHistoryService integratedEntrustHistoryService;

    @Autowired
    private TrdExchangeWorkflowService trdExchangeWorkflowService;

    /**
     * 是否单体服务
     */
    @Value("${application.micorservice.contextPath.single:false}")
    private boolean isSingle;

    @Autowired(required = false)
    @Qualifier("simpleRestTemplate")
    protected JoyinRestTemplate simpleRestTemplate;

    /**
     * 根据userId查询Entrust中数据
     */
    public ResultBean queryEntrust(String userId, String instructionId, String accountId, String orderId) {
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 start
        /*List<String> ptlAuthList = commonManagement.getPtlAuthInfo(userId);//组合权限查询
        if (Objects.isNull(ptlAuthList) || ptlAuthList.size() == 0) {
            if (!Strings.isNullOrEmpty(instructionId)) {
                return new ResultBean(ErrorCode.SUCCESS, new OrderAndEntrustQryResult(new ArrayList<>(), new ArrayList<>()));
            }
            return new ResultBean(ErrorCode.SUCCESS, new ArrayList());
        }*/
        // 查询具体订单下的委托
        if (!Strings.isNullOrEmpty(orderId)) {
            List<Entrust> entrustList = entrustService.queryEntrustByOrderId(orderId);
            entrustList = entrustList.stream().filter(entrust -> !EntrustType.CANCEL.value.equals(entrust.getEntrustType()) /*&& ptlAuthList.contains(entrust.getPortfolioId())*/)
                    .sorted((o1, o2) -> DateUtil.compareDate(o2.getCreateTime(), o1.getCreateTime()))
                    .collect(Collectors.toList());
            List<Entrust> entrusts = fillEntrustPrice(entrustList);
            return new ResultBean(ErrorCode.SUCCESS, new OrderAndEntrustQryResult(new ArrayList<>(), entrusts));
        }
        // 查询指令下的订单和委托
        if (!Strings.isNullOrEmpty(instructionId)) {
            List<Entrust> entrustList = entrustService.queryEntrustByInstructionId(instructionId);
            entrustList = entrustList.stream().filter(entrust -> !EntrustType.CANCEL.value.equals(entrust.getEntrustType())/* && ptlAuthList.contains(entrust.getPortfolioId())*/)
                    .sorted((o1, o2) -> DateUtil.compareDate(o2.getCreateTime(), o1.getCreateTime()))
                    .collect(Collectors.toList());
            setFutureEntrustStockCode(entrustList);
            List<Order> orderListByInstructionId = orderService.queryOrderByInstructionId(instructionId);
            List<Order> orderListWithOutFair = orderListByInstructionId.stream().filter(order -> !BusinessFlag.FAIR.value.equals(order.getBusinessFlag()) /*&& ptlAuthList.contains(order.getPortfolioId())*/)
                    .sorted((o1, o2) -> DateUtil.compareDate(o2.getCreateTime(), o1.getCreateTime()))
                    .collect(Collectors.toList());
            setFutureOrderStockCode(orderListWithOutFair);
            List<Entrust> entrusts = fillEntrustPrice(entrustList);
            return new ResultBean(ErrorCode.SUCCESS, new OrderAndEntrustQryResult(orderListWithOutFair, entrusts));
        }
        // 查询用户的委托
        List<Entrust> entrustList = entrustService.queryByUserId(userId);
        List<Order> orderList = orderService.queryOrder(userId);
        if (!Strings.isNullOrEmpty(accountId)) {
            entrustList = entrustList.stream().filter(entrust -> accountId.equals(entrust.getAccountId()) && !EntrustType.CANCEL.value.equals(entrust.getEntrustType()) /*&& ptlAuthList.contains(entrust.getPortfolioId())*/)
                    .sorted((o1, o2) -> DateUtil.compareDate(o2.getCreateTime(), o1.getCreateTime()))
                    .collect(Collectors.toList());
            orderList = orderList.stream().filter(order -> !BusinessFlag.FAIR.value.equals(order.getBusinessFlag()) && accountId.equals(order.getAccountId()) /*&& ptlAuthList.contains(order.getPortfolioId())*/)
                    .sorted((o1, o2) -> DateUtil.compareDate(o2.getCreateTime(), o1.getCreateTime()))
                    .collect(Collectors.toList());
        } else {
            entrustList = entrustList.stream().filter(entrust -> !EntrustType.CANCEL.value.equals(entrust.getEntrustType())/* && ptlAuthList.contains(entrust.getPortfolioId())*/)
                    .sorted((o1, o2) -> DateUtil.compareDate(o2.getCreateTime(), o1.getCreateTime()))
                    .collect(Collectors.toList());
            orderList = orderList.stream().filter(order -> !BusinessFlag.FAIR.value.equals(order.getBusinessFlag())/* && ptlAuthList.contains(order.getPortfolioId())*/)
                    .sorted((o1, o2) -> DateUtil.compareDate(o2.getCreateTime(), o1.getCreateTime()))
                    .collect(Collectors.toList());
        }
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 end
        setFutureEntrustStockCode(entrustList);
        setFutureOrderStockCode(orderList);
        List<Entrust> entrusts = fillEntrustPrice(entrustList);
        return new ResultBean(ErrorCode.SUCCESS, new OrderAndEntrustQryResult(orderList, entrusts));
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

    private List<Entrust> fillEntrustPrice(List<Entrust> entrustList) {
        List<Entrust> entrusts = Lists.newArrayListWithCapacity(entrustList.size());
        entrustList.forEach(o -> {
            Entrust entrust = new Entrust();
            BeanUtils.copyProperties(o, entrust);
            if ("EX005".equals(entrust.getBusiTypeTg())) {
                entrust.setEntrustPrice(entrust.getEntrustPrice().multiply(BigDecimal.valueOf(100)));
            }
            entrusts.add(entrust);
        });
        return entrusts;
    }

    /**
     * 综合业务查询
     */

    public ResultBean queryIntegratedEntrust(String userId, String instructionId, String accountId, String orderId) {
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 start
        /*List<String> ptlAuthList = commonManagement.getPtlAuthInfo(userId);//组合权限查询
        if (Objects.isNull(ptlAuthList) || ptlAuthList.size() == 0) {
            return new ResultBean(ErrorCode.SUCCESS, new ArrayList());
        }*/
        if (!Strings.isNullOrEmpty(orderId)) {
            List<IntegratedEntrust> entrustList = integratedEntrustService.queryIntegratedEntrustByOrderId(orderId);
            entrustList = entrustList.stream().filter(entrust -> !EntrustType.CANCEL.value.equals(entrust.getEntrustType())/* && ptlAuthList.contains(entrust.getPortfolioId())*/)
                    .sorted((o1, o2) -> DateUtil.compareDate(o2.getCreateTime(), o1.getCreateTime()))
                    .collect(Collectors.toList());
            return new ResultBean(ErrorCode.SUCCESS, entrustList);
        }
        if (!Strings.isNullOrEmpty(instructionId)) {
            List<IntegratedEntrust> entrustList = integratedEntrustService.queryIntegratedEntrustByInstructionId(instructionId);
            entrustList = entrustList.stream().filter(entrust -> !EntrustType.CANCEL.value.equals(entrust.getEntrustType())/* && ptlAuthList.contains(entrust.getPortfolioId())*/)
                    .sorted((o1, o2) -> DateUtil.compareDate(o2.getCreateTime(), o1.getCreateTime()))
                    .collect(Collectors.toList());
            return new ResultBean(ErrorCode.SUCCESS, entrustList);
        }
        List<IntegratedEntrust> entrustList = integratedEntrustService.queryByUserId(userId);
        if (!Strings.isNullOrEmpty(accountId)) {
            entrustList = entrustList.stream().filter(entrust -> accountId == entrust.getAccountId() && !EntrustType.CANCEL.value.equals(entrust.getEntrustType()) /*&& ptlAuthList.contains(entrust.getPortfolioId())*/)
                    .sorted((o1, o2) -> DateUtil.compareDate(o2.getCreateTime(), o1.getCreateTime()))
                    .collect(Collectors.toList());
        } else {
            entrustList = entrustList.stream().filter(entrust -> !EntrustType.CANCEL.value.equals(entrust.getEntrustType())/* && ptlAuthList.contains(entrust.getPortfolioId())*/)
                    .sorted((o1, o2) -> DateUtil.compareDate(o2.getCreateTime(), o1.getCreateTime()))
                    .collect(Collectors.toList());
        }
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 end
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
            if (EntrustUtils.isNoTrade(entrust.getEntrustProp())) {
                // 上交所 【转股、配债、配股、申购】不可撤，【回售】可撤
                // 深交所 【申购】不可撤，【转股、配债、配股、回售】可撤
                if (!((EntrustProp.CHANGE_TO_STOCK.value.equals(entrust.getEntrustProp())
                        || EntrustProp.ALLOTMENT.value.equals(entrust.getEntrustProp())
                        || EntrustProp.PS.value.equals(entrust.getEntrustProp()))
                        && Market.SZSE.value.equals(entrust.getMarket()))
                        && !EntrustProp.HS.value.equals(entrust.getEntrustProp())) {
                    logger.error("该类业务不可撤单：entrustProp={}", entrust.getEntrustProp());
                    return new ResultBean(ErrorCode.CANCEL_ENTRUST_BUSI_ERROR);
                }
            }

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
            if (entrust.getEntrustStatus() == EntrustStatus.PENDING_APPROVAL.value || entrust.getEntrustStatus() == EntrustStatus.PENDING_REVIEW.value) {
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

    public ResultBean queryEnable(EnableQryBean enableQryBean) {
        int maxEntrustVolume = terminalHelper.queryEnable(enableQryBean.getStockCode(), enableQryBean.getEntrustProp(), enableQryBean.getEntrustBs(), enableQryBean.getEntrustPrice(), enableQryBean.getAccountId());
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

    @Deprecated
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

//    public Message<PageInfo<Object>> queryIntegratedEntrustForPage(PageEntrustInfoRequest request) {
//        return new Message<>(queryIntegratedEntrustForPage(request.getPageNum(), request.getPageSize(), request.getHistoryType(), request.getUserId(), request.getStartDay(), request.getEndDay(), request.getAccountId(), request.getPortfolioId(), request.getMarket(), request.getStockCode(), request.getEntrustProp(), request.getEntrustStatus()));
//    }

    public Message<PageInfo<EntrustQueryBean>> queryIntegratedEntrustForPage(PageEntrustInfoRequest request) {
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 start
        List<String> ptlAuthList = null;
        /*String userId = request.getUserId();
        String portfolioId = request.getPortfolioId();
        //组合权限查询
        List<String> ptlAuthList = commonManagement.getPtlAuthInfo(userId);
        if (Objects.isNull(ptlAuthList) || ptlAuthList.size() == 0) {
            logger.info("用户：{}没有组合权限", userId);
            return new Message<PageInfo<EntrustQueryBean>>(new PageInfo());
        }
        if (!Strings.isNullOrEmpty(portfolioId) && !ptlAuthList.contains(portfolioId)) {
            logger.info("用户：{}没有{}的组合权限", userId, portfolioId);
            return new Message<PageInfo<EntrustQueryBean>>(new PageInfo());
        }*/
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 end
        Integer historyType = request.getHistoryType();
        if (historyType == PresentOrHistoryQryType.PRESENT.value) {
            PageInfo<EntrustQueryBean> entrustForPage = integratedEntrustService.queryAllForPage(request, ptlAuthList);
            return new Message<PageInfo<EntrustQueryBean>>(entrustForPage);
        } else if (historyType == PresentOrHistoryQryType.HISTORY.value) {
            PageInfo<EntrustQueryBean> entrustHistoryForPage = integratedEntrustHistoryService.queryAllForPage(request, ptlAuthList);
            return new Message<PageInfo<EntrustQueryBean>>(entrustHistoryForPage);
        }
        return new Message<PageInfo<EntrustQueryBean>>().fail(ErrorCode.REQ_PARAMETER_ERROR.value + "", ErrorCode.REQ_PARAMETER_ERROR.errorMsg);
    }

    @Deprecated
    public ResultBean queryIntegratedEntrustForPage(int pageNum, int pageSize, int historyType, String userId, String startDay, String endDay, String accountId, String portfolioId, String market, String stockCode, String entrustProp, int entrustStatus) {
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 start
        List<String> ptlAuthList = null;
        /*List<String> ptlAuthList = commonManagement.getPtlAuthInfo(userId);//组合权限查询
        if (Objects.isNull(ptlAuthList) || ptlAuthList.size() == 0) {
            logger.info("用户：{}没有组合权限", userId);
            return new ResultBean(ErrorCode.SUCCESS, new PageInfo());
        }
        if (!Strings.isNullOrEmpty(portfolioId) && !ptlAuthList.contains(portfolioId)) {
            logger.info("用户：{}没有{}的组合权限", userId, portfolioId);
            return new ResultBean(ErrorCode.SUCCESS, new PageInfo());
        }*/
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 end
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

    public Message<PageInfo<EntrustQueryBean>> queryAllEntrustForPage(PageEntrustInfoRequest request) {
        String userId = request.getUserId();
        String portfolioId = request.getPortfolioId();
        request.getPageMode().setPageSize(99999);
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 start
        List<String> ptlAuthList = null;
        /*//组合权限查询
        List<String> ptlAuthList = commonManagement.getPtlAuthInfo(userId);
        if (Objects.isNull(ptlAuthList) || ptlAuthList.size() == 0) {
            logger.info("用户：{}没有组合权限", userId);
            return new Message<PageInfo<EntrustQueryBean>>(new PageInfo());
        }
        if (!Strings.isNullOrEmpty(portfolioId) && !ptlAuthList.contains(portfolioId)) {
            logger.info("用户：{}没有{}的组合权限", userId, portfolioId);
            return new Message<PageInfo<EntrustQueryBean>>(new PageInfo());
        }*/
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 end
        Integer historyType = request.getHistoryType();
        if (historyType == PresentOrHistoryQryType.PRESENT.value) {
            PageInfo<EntrustQueryBean> entrustForPage = integratedEntrustService.queryAllEntrustForPage(request, ptlAuthList);
            return new Message<PageInfo<EntrustQueryBean>>(entrustForPage);
        } else if (historyType == PresentOrHistoryQryType.HISTORY.value) {
            PageInfo<EntrustQueryBean> entrustHistoryForPage = integratedEntrustHistoryService.queryAllEntrustForPage(request, ptlAuthList);
            return new Message<PageInfo<EntrustQueryBean>>(entrustHistoryForPage);
        }

        return new Message<PageInfo<EntrustQueryBean>>().fail(ErrorCode.REQ_PARAMETER_ERROR.value + "", ErrorCode.REQ_PARAMETER_ERROR.errorMsg);
    }

    @Deprecated
    public ResultBean queryAllEntrustForPage(int pageNum, int pageSize, int historyType, String userId, String startDay, String endDay, String accountId, String portfolioId, String market, String stockCode, String entrustProp, int entrustStatus) {
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 start
        List<String> ptlAuthList = null;
        /*List<String> ptlAuthList = commonManagement.getPtlAuthInfo(userId);//组合权限查询
        if (Objects.isNull(ptlAuthList) || ptlAuthList.size() == 0) {
            logger.info("用户：{}没有组合权限", userId);
            return new ResultBean(ErrorCode.SUCCESS, new PageInfo());
        }
        if (!Strings.isNullOrEmpty(portfolioId) && !ptlAuthList.contains(portfolioId)) {
            logger.info("用户：{}没有{}的组合权限", userId, portfolioId);
            return new ResultBean(ErrorCode.SUCCESS, new PageInfo());
        }*/
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 end
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
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 start
        List<String> ptlAuthList = null;
        /*List<String> ptlAuthList = commonManagement.getPtlAuthInfo(userId);//组合权限查询
        if (Objects.isNull(ptlAuthList) || ptlAuthList.size() == 0) {
            logger.info("用户：{}没有组合权限", userId);
            return new ResultBean(ErrorCode.SUCCESS, new ArrayList());
        }
        logger.debug("根据委托状态查询委托, 用户id: {}, 委托状态: {}", userId, entrustStatus);*/
        // modify by swwan ZGTFCP-606 只要是交易员下单操作过的都应该能查到 end
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

    /**
     * 查询配股申购等代码
     *
     * @param entrustProp 委托属性
     * @param instruction 指令
     * @return 申报等代码
     */
    public String getOthCodeFromInvestment(@NotBlank String entrustProp, @NotNull Instruction instruction) {
        String othCode = instruction.getFinprodMarketId();
        String othCodeType = "";
        if (HS.value.equals(entrustProp)) {
            othCodeType = HSDM.value;
        } else if (ZYCRK.value.equals(entrustProp)) {
            othCodeType = ZYDM.value;
        } else if (CHANGE_TO_STOCK.value.equals(entrustProp)) {
            othCodeType = KZZZGDM.value;
        } else if (EntrustProp.PS.value.equals(entrustProp)) {
            othCodeType = PZDM.value;
        } else if (ALLOTMENT.value.equals(entrustProp)) {
            othCodeType = PGDM.value;
        }
        //申报代码获取
        ResultBean resultBean = this.getOthCode(instruction.getFinprodId(), othCodeType);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            List<OthCodeBean> list = (List<OthCodeBean>) resultBean.getData();
            if (CollUtil.isNotEmpty(list)) {
                //03-可转债信用申购代码 or 07-新股申购代码
                if (APPLY.value.equals(entrustProp) || ETF_APPLY.value.equals(entrustProp)) {
                    for (OthCodeBean othCodeBean : list) {
                        if (KZZXYSGDM.value.equals(othCodeBean.getOthCodeType()) || XGSGDM.value.equals(othCodeBean.getOthCodeType())) {
                            othCode = othCodeBean.getOthCode();
                            break;
                        }
                    }
                } else {
                    othCode = list.get(0).getOthCode();
                }
            }
        }
        return othCode;
    }
}
