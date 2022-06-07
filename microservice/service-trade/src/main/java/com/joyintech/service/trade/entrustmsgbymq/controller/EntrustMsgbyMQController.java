package com.joyintech.service.trade.entrustmsgbymq.controller;

import com.alibaba.fastjson.JSON;
import com.google.common.base.MoreObjects;
import com.joyintech.service.trade.entrustmsgbymq.dto.OrderFinishAmtMessage;
import com.joyintech.service.trade.entrustmsgbymq.dto.TradeMessage;
import com.joyintech.service.trade.entrustmsgbymq.service.EntrustMsgbyMQService;
import com.joyintech.tams.business.common.pojo.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author lifangpeng
 * @date 2022/4/12 15:14
 */
@RestController("backEntrustMsgbyMQController")
@RequestMapping("EntrustMsgbyMQ")
public class EntrustMsgbyMQController {
    private static final Logger logger = LoggerFactory.getLogger(EntrustMsgbyMQController.class);

    @Autowired
    private EntrustMsgbyMQService entrustMsgbyMQService;


    @PostMapping("/processFanout")
    public Message<Object> processFanout(@RequestBody Map<String, Object> map) {
        String tradeMessageStr = (String) map.get("mesInfo");
        Message<Object> message = new Message<>();
        try {
            TradeMessage tradeMessage = JSON.parseObject(tradeMessageStr, TradeMessage.class);
            if (tradeMessage != null) {
                logger.info("【成交消息接收】processFanout entrustId {}", tradeMessage.getEntrustId());
                entrustMsgbyMQService.insertEntrustMsg(tradeMessage);
            } else {
                logger.info("【成交消息接收】the message get from interface is null");
            }
            return message.success();
        } catch (Exception e) {
            logger.error("成交消息接收失败-错误信息:{}", e.getMessage(), e);
            logger.error("成交消息接收失败-错误信息:{},消息内容:{}", e.getLocalizedMessage(), tradeMessageStr);
            message.setMessage(MoreObjects.firstNonNull(e.getMessage(), e.getLocalizedMessage()));
        }
        return message.fail();
    }

    @PostMapping("/processManualSettleTrade")
    public Message<Object> processManualSettleTrade(@RequestBody Map<String, Object> map) {
        String tradeMessageStr = (String) map.get("mesInfo");
        Message<Object> message = new Message<>();
        try {
            TradeMessage tradeMessage = JSON.parseObject(tradeMessageStr, TradeMessage.class);
            if (tradeMessage != null) {
                logger.info("【手工交收消息接收】getTradeMsgFromMQ tradeMessage {}", tradeMessageStr);
                entrustMsgbyMQService.insertEntrustMsg(tradeMessage);
            } else {
                logger.info("【手工交收消息接收】the message get from MQ is null");
            }
            return message.success();
        } catch (Exception e) {
            logger.error("手工交收消息接收失败-错误信息:{}" + e.getMessage(), e);
            logger.error("手工交收消息接收失败-错误信息:{},消息内容:{}", e.getLocalizedMessage(), tradeMessageStr);
        }
        return message.fail();
    }

    @PostMapping("/updateOrderFinishAmt")
    public Message<Object> updateOrderFinishAmt(@RequestBody Map<String, Object> map) {
        String tradeMessageStr = (String) map.get("mesInfo");
        Message<Object> message = new Message<>();
        try {
            OrderFinishAmtMessage tradeMessage = JSON.parseObject(tradeMessageStr, OrderFinishAmtMessage.class);
            if (tradeMessage != null) {
                logger.info("【执行进度接收】updateOrderFinishAmt entrustId {}", tradeMessage.getEntrustId());
                entrustMsgbyMQService.insertEntrustMsg(tradeMessage);
            } else {
                logger.info("【执行进度接收】the message get from interface is null");
            }
            return message.success();
        } catch (Exception e) {
            logger.error("成交消息接收失败-错误信息:{}" + e.getMessage(), e);
            logger.error("执行进度接收失败-错误信息:{},消息内容:{}", e.getLocalizedMessage(), tradeMessageStr);
        }
        return message.fail();
    }


}
