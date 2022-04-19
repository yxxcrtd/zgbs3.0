package com.joyintech.tams.exchange.service.trade.processor.msg;

import com.joyintech.framework.rabbitmq.entity.SimpleRabbitMessage;
import com.joyintech.tams.exchange.common.bean.ExamineOrder;
import com.joyintech.tams.exchange.common.bean.TerminalId;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.service.trade.processor.MessageConsumerService;
import com.joyintech.tams.exchange.service.trade.processor.TradeMsgProcessor;
import com.joyintech.tams.exchange.service.trade.transaction.TransactionHelper;
import com.lmax.disruptor.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MsgBeanHandler implements EventHandler<MsgBean> {
    private static final Logger logger = LoggerFactory.getLogger(MsgBeanHandler.class);
    @Autowired
    private TradeMsgProcessor tradeMsgProcessor;
    @Autowired
    private MessageConsumerService messageConsumerService;
    @Autowired
    private TransactionHelper transactionHelper;

    @Override
    public void onEvent(MsgBean msgBean, long l, boolean b) throws Exception {
        try {
            TradeMsgReceiver.consumerOffset.incrementAndGet();
            transactionHelper.clear();
            switch (msgBean.getMsgType()) {
                case MsgType.REQ_ENTRUST: {//普通下单
                    tradeMsgProcessor.handleEntrust((Entrust) msgBean.getMsg(), msgBean.getTradeAgent(), MsgType.REQ_ENTRUST);
                    break;
                }
                case MsgType.RESP_ENTRUST://普通下单响应
                case MsgType.NOTIFY_ENTRUST_ERROR: {//下单异常通知
                    tradeMsgProcessor.onRspEntrust((TerminalId) msgBean.getMsg());
                    break;
                }
                case MsgType.NOTIFY_ENTRUST: {//普通下单结果通知
                    tradeMsgProcessor.onRtnEntrust((Entrust) msgBean.getMsg());
                    break;
                }
                case MsgType.NOTIFY_INTEGRATED: {//综合下单结果通知
                    tradeMsgProcessor.onRtnIntegratedEntrust((IntegratedEntrust) msgBean.getMsg());
                    break;
                }
                case MsgType.NOTIFY_TRADE_RECORD: {//成交通知
                    tradeMsgProcessor.onTrade((Trade) msgBean.getMsg());
                    break;
                }
                case MsgType.REQ_ENTRUST_CANCEL: {//撤单
                    tradeMsgProcessor.cancelEntrust((EntrustInterface) msgBean.getMsg());
                    break;
                }
                case MsgType.RESP_CANCEL: {//撤单响应
                    tradeMsgProcessor.onRspCancelEntrust((CancelEntrustBean) msgBean.getMsg());
                    break;
                }
                case MsgType.REQ_INTEGRATED: {//综合下单
                    tradeMsgProcessor.handIntegratedEntrust((IntegratedEntrust) msgBean.getMsg(), msgBean.getTradeAgent(), MsgType.REQ_INTEGRATED);
                    break;
                }
                case MsgType.RESP_INTEGRATED: {//综合下单响应
                    tradeMsgProcessor.onRspIntegratedEntrust((TerminalId) msgBean.getMsg());
                    break;
                }
                case MsgType.EXAMINE_ENTRUST: {//委托审核
                    ExamineOrder order = (ExamineOrder) msgBean.getMsg();
                    tradeMsgProcessor.handleExamineEntrust((EntrustInterface) order.getOrder(), order.getInstructionsStatus(), order.getApproveReason(), order.getProcessType());
                    break;
                }
                case MsgType.EXAMINE_REVIEW: {//复核审批
                    ExamineOrder order = (ExamineOrder) msgBean.getMsg();
                    tradeMsgProcessor.handleReviewExamine((EntrustInterface) order.getOrder(), order.getInstructionsStatus(), order.getApproveReason(), order.getProcessType());
                    break;
                }
                case MsgType.EXAMINE_REVOKE: {//撤销审批
                    ExamineOrder order = (ExamineOrder) msgBean.getMsg();
                    tradeMsgProcessor.handleRevokeExamine((EntrustInterface) order.getOrder(), order.getInstructionsStatus(), order.getApproveReason(), order.getProcessType());
                    break;
                }
                case MsgType.MQ_INSTRUCTION: {//推送指令
                    SimpleRabbitMessage instructionKeyMsg = (SimpleRabbitMessage) msgBean.getMsg();
                    messageConsumerService.handleInstructionFromMQ(instructionKeyMsg);
                    break;
                }
                case MsgType.REQ_BATCH_ORDER: {//批量委托
                    tradeMsgProcessor.genFirstBatchEntrust((BatchInfo) msgBean.getMsg());
                    break;
                }
                case MsgType.BATCH_ORDER_JOB: {//批量委托
                    tradeMsgProcessor.handleBatchOrder((BatchInfo) msgBean.getMsg());
                    break;
                }
                case MsgType.FROZEN_PENDING_ENTRUST: {//待审批委托冻结处理
                    tradeMsgProcessor.handleFrozenPendingApprovalEntrust((Entrust) msgBean.getMsg());
                    break;
                }
                case MsgType.FROZEN_PENDING_INTEGRATED: {//待审批综合委托冻结处理
                    tradeMsgProcessor.handleFrozenPendingApprovalIntegratedEntrust((IntegratedEntrust) msgBean.getMsg());
                    break;
                }
                case MsgType.UNFROZEN_INNER: {//撤审内部撤销
                    tradeMsgProcessor.unfrozenForInnerByCancelEntrust((EntrustInterface) msgBean.getMsg());
                    break;
                }
                case MsgType.REQ_CLOSE_ENTRUST: {//强制关闭委托
                    tradeMsgProcessor.closeEntrust((EntrustInterface) msgBean.getMsg());
                    break;
                }
                default: {
                    logger.warn("unknown msg type. msgType:{} msg:{}", msgBean.getMsgType(),
                            JsonUtils.getJsonFromObject(msgBean.getMsg()));
                }
            }
        } catch (Exception e) {
            TradeMsgReceiver.exceptionOffset.incrementAndGet();
            logger.warn("handle msg bean exception:{} ", e);
        }
        transactionHelper.save();
    }
}
