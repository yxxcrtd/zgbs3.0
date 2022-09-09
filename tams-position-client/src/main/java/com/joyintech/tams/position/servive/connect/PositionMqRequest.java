package com.joyintech.tams.position.servive.connect;

import com.joyintech.framework.security.LoginUserUtil;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.tams.position.exception.PositionException;
import com.joyintech.tams.position.pojo.BaseMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PositionMqRequest {

    private static final Logger logger = LoggerFactory.getLogger(PositionMqRequest.class);

//    @Autowired
//    private static RabbitTemplate rabbitTemplate;

//    protected void roll(Object result) {
////		logger.info("发送mq数据："+result.toString());
////        MessageProperties properties = new MessageProperties();
////        properties.setMessageId(UUID.randomUUID().toString().replaceAll("-",""));
//        rabbitTemplate.convertAndSend("exchange.position.roll.balance", "exchange.position.roll", JsonUtils.object2Json(result));
//    }

//    protected  void roll(String revokeId, String modelType, String errorMsg) {
//        BaseMap bm = BaseMap.newMap() .put("revokeId", revokeId) .put("modelType", modelType) .put("userId", LoginUserUtil.getUserName());
//        logger.info("发送回滚头寸报文mq:" + JsonUtils.object2Json(bm));
//        try {
//            roll(bm);
//        } catch (Exception e) {
//            logger.error("回滚头寸报错mq", e);
//        }
//        // 页面端根据异常信息返回前端
//        throw new PositionException(errorMsg);
//    }
//
//    protected  void sendRevoke(String revokeId, String modelType, String errorMsg) {
//        BaseMap bm = BaseMap.newMap() .put("revokeId", revokeId) .put("modelType", modelType) .put("userId", LoginUserUtil.getUserName());
//        logger.info("发送头寸报文:" + JsonUtils.object2Json(bm));
//        try {
//            roll(bm);
//        } catch (Exception e) {
//            logger.error("回滚头寸报错", e);
//        }
//    }



}
