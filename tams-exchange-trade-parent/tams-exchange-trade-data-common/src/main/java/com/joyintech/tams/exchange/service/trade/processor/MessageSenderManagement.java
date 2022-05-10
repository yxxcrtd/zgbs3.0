package com.joyintech.tams.exchange.service.trade.processor;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.framework.JoyinRestTemplate;
import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.investment.BasicMessage;
import com.joyintech.tams.exchange.common.bean.investment.ProgressMessage;
import com.joyintech.tams.exchange.common.bean.investment.TradeMessage;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.db.domain.EntrustInterface;
import com.joyintech.tams.exchange.common.db.domain.Instruction;
import com.joyintech.tams.exchange.common.db.domain.RecordLog;
import com.joyintech.tams.exchange.common.db.domain.Trade;
import com.joyintech.tams.exchange.common.domain.Message;
import com.joyintech.tams.exchange.common.microservice.ServiceApplication;
import com.joyintech.tams.exchange.common.microservice.TradeServiceURL;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.datacommon.db.service.RecordLogService;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 消息推送：支持MQ和 http
 *
 * @author lifangpeng
 * @date 2022/4/12 11:12
 */
@Component
public class MessageSenderManagement extends BaseService {
    private static final Logger logger = LoggerFactory.getLogger(MessageSenderManagement.class);

    @Autowired
    private MessageSenderService messageSenderService;
    @Autowired
    private RecordLogService recordLogService;
    @Autowired
    private MessageSenderHelper messageSenderHelper;

    @Autowired(required = false)
    @Qualifier("simpleRestTemplate")
    protected JoyinRestTemplate simpleRestTemplate;

    /**
     * 是否单体服务
     */
    @Value("${application.micorservice.contextPath.single:false}")
    private boolean isSingle;

    @Value("${trd-info-push-mode:1}")
    private int trdInfoPushMode;

    private static final int MQ = 1;
    private static final int INTERFACE = 2;

    /**
     * 成交推送
     */
    public void pushTrade(Trade trade, Instruction instruction, EntrustInterface entrust) {
        logger.info("【pushTrade】 parameters：{}", JsonUtils.getJsonFromObject(trade));
        if (Objects.isNull(trade) || Strings.isNullOrEmpty(trade.getId())) {
            logger.error("【pushTrade】 check parameter failed");
            return;
        }
        //组装推送消息
        TradeMessage tradeMessage = messageSenderHelper.createTradeMessage(trade, instruction, entrust);
        //保存记录日志
        RecordLog recordLog = saveRecordLog(trade.getBrokerTradeId(), entrust.getId(), instruction.getEntrustId(), 1, tradeMessage);
        //推送消息
        sendMessage(recordLog, tradeMessage, 1);
    }

    /**
     * 成交手动交收推送
     */
    public void pushTradeForFixedIncome(Trade trade, Instruction instruction, EntrustInterface entrust) {
        logger.info("【pushTradeForFixedIncome】 parameters：{}", JsonUtils.getJsonFromObject(trade));
        if (Objects.isNull(trade) || StringUtil.isEmpty(trade.getId()) || Objects.isNull(instruction)) {
            logger.error("【pushTradeForFixedIncome】 check parameter failed");
            return;
        }
        //组装推送消息
        TradeMessage tradeMessage = messageSenderHelper.createTradeMsg(trade, instruction, entrust);
        //保存记录日志
        RecordLog recordLog = saveRecordLog(trade.getBrokerTradeId(), entrust.getId(), instruction.getEntrustId(), 2, tradeMessage);
        //推送消息
        sendMessage(recordLog, tradeMessage, 2);
    }

    /**
     * 指令进度推送
     */
    public void pushProgress(Instruction instruction) {
        logger.info("【pushProgress】 parameters：{}", JsonUtils.getJsonFromObject(instruction));
        if (Objects.isNull(instruction) || Strings.isNullOrEmpty(instruction.getEntrustId())) {
            logger.error("【pushProgress】 check parameter failed");
            return;
        }
        //组装推送信息
        ProgressMessage progressMessage = ProgressMessage.createTradeMessage(instruction);
        //保存记录日志
        RecordLog recordLog = saveRecordLog(instruction.getEntrustId(), null, null, 3, progressMessage);
        //推送消息
        sendMessage(recordLog, progressMessage, 3);
    }


    /**
     * 保存记录日志
     *
     * @param code1   业务编号1
     * @param code2   业务编号2
     * @param code3   业务编号3
     * @param logType 日志类型
     * @param msg     推送消息
     */
    private RecordLog saveRecordLog(String code1, String code2, String code3, int logType, Object msg) {
        RecordLog record = new RecordLog();
        record.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        record.setBusiCode1(code1);
        record.setBusiCode2(code2);
        record.setBusiCode3(code3);

        String recordInfo = JsonUtils.getJsonFromObject(msg);
        List<String> remark = Lists.newArrayList();
        if (recordInfo.length() > 4000) {
            recordInfo = recordInfo.substring(0, 4000);
            remark.add("推送信息长度超出4000");
        }
        record.setRecordInfo(recordInfo);
        record.setLogType(logType);
        record.setStatus(0);
        record.setCreateUser("system");
        record.setCreateTime(new Date());
        record.setUpdateUser("system");
        record.setUpdateTime(record.getCreateTime());
        if (CollectionUtils.isNotEmpty(remark)) {
            record.setRemark(String.join(";", remark));
        }

        recordLogService.save(record);

        return record;
    }

    private void sendMessage(RecordLog recordLog, BasicMessage basicMessage, int pushType) {
        //消息推送方式trdInfoPushMode:1-MQ；2-接口
        //消息推送方式pushType:1-成交；2-手工交收；3-指令进度
        logger.info("【sendMessage】 parameters：pushType={}，trdInfoPushMode={}，basicMessage={}", pushType, trdInfoPushMode, JsonUtils.getJsonFromObject(basicMessage));
        //推送MQ
        if (trdInfoPushMode == 0 || trdInfoPushMode == MQ) {
            try {
                recordLog.setStatus(1);
                if (pushType == 1) {
                    messageSenderService.pushTradeToMQ(basicMessage);
                } else if (pushType == 2) {
                    messageSenderService.pushTradeToMQForFixedIncome(basicMessage);
                } else if (pushType == 3) {
                    messageSenderService.pushProgressToMQ(basicMessage);
                } else {
                    logger.error("不支持当前推送业务：pushType={}", pushType);
                    recordLog.setStatus(2);
                    recordLog.setRemark("不支持当前推送业务");
                }
            } catch (Exception e) {
                logger.error("推送MQ失败：【{}】", JsonUtils.getJsonFromObject(basicMessage));
                recordLog.setStatus(2);
                recordLog.setRemark("推送MQ失败");
            }
        }
        //推送接口
        else if (trdInfoPushMode == INTERFACE) {
            Message<String> result = null;
            if (pushType == 1) {
                result = sendMessage(basicMessage, TradeServiceURL.pushTrade);
            } else if (pushType == 2) {
                result = sendMessage(basicMessage, TradeServiceURL.pushTradeForFixedIncome);
            } else if (pushType == 3) {
                result = sendMessage(basicMessage, TradeServiceURL.pushProgress);
            } else {
                logger.error("不支持当前推送业务：pushType={}", pushType);
                recordLog.setStatus(2);
                recordLog.setRemark("不支持当前推送业务");
            }

            if (Objects.nonNull(result)) {
                if (StringUtil.equals(Message.SUCCESS, result.getCode())) {
                    recordLog.setStatus(1);
                } else {
                    recordLog.setStatus(2);
                    recordLog.setRemark(result.getMessage());
                }
            } else {
                recordLog.setStatus(2);
                recordLog.setRemark("推送接口失败");
            }
        } else {
            logger.error("不支持当前推送方式：trdInfoPushMode={}", trdInfoPushMode);
            recordLog.setStatus(2);
            recordLog.setRemark("不支持当前推送方式");
        }
        recordLogService.update(recordLog);
    }

    private Message<String> sendMessage(Object msg, String url) {
        try {
            String sendUrl = ServiceApplication.TRADE_SERVICE_NAME + url;
            Map<String, Object> param = Maps.newHashMap();
            param.put("mesInfo", JsonUtils.getJsonFromObject(msg));
            Message<String> result;
            if (isSingle) {
                result = simpleRestTemplate.postForObject(sendUrl, param, Message.class);
            } else {
                result = restTemplate.postForObject(sendUrl, param, Message.class);
            }
            logger.info("【推送消息】接口成功：result：{}，msg：{}", JsonUtils.getJsonFromObject(result), JsonUtils.getJsonFromObject(msg));
            return result;
        } catch (Exception e) {
            logger.error("【推送消息】接口失败，" + e.getMessage(), e);
            return new Message<>(ResultBean.error(ErrorCode.API_RETURN_ERROR));
        }
    }

}
