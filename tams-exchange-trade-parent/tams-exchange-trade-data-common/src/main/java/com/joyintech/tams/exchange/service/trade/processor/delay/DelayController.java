package com.joyintech.tams.exchange.service.trade.processor.delay;

import com.joyintech.tams.exchange.common.constant.OrderStatus;
import com.joyintech.tams.exchange.common.db.domain.BatchInfo;
import com.joyintech.tams.exchange.common.db.domain.Order;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.datacommon.db.service.BatchInfoService;
import com.joyintech.tams.exchange.datacommon.db.service.OrderService;
import com.joyintech.tams.exchange.service.trade.processor.TradeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Executors;

@Component
@Scope("singleton")
public class DelayController implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(DelayController.class);

    private DelayQueue<DelayMsg> delayQueue = new DelayQueue<>();

    @Autowired
    private TradeHelper tradeHelper;

    @Autowired
    private BatchInfoService batchInfoService;

    @Autowired
    private OrderService orderService;

    public boolean put(BatchInfo batchInfo) {
        try {
            DelayMsg<BatchInfo> delayedMsg = new DelayMsg<>(batchInfo, batchInfo.getStepTime() * 1000);
            delayQueue.put(delayedMsg);
        } catch (Exception e) {
            logger.error("delayQueue put error: {}", e);
            return false;
        }
        return true;
    }

    public void clear() {
        logger.info("batch delayQueue clear: {}", JsonUtils.getJsonFromObject(delayQueue.toArray()));
        delayQueue.clear();
    }

    public void remove(BatchInfo batchInfo) {
        logger.info("delayQueue remove batchInfo: {}", JsonUtils.getJsonFromObject(batchInfo));
        DelayMsg<BatchInfo> delayMsg = new DelayMsg<>(batchInfo, batchInfo.getStepTime() * 1000);
        delayQueue.remove(delayMsg);
    }

    @PostConstruct
    public void startUpPut() {
        try {
            List<BatchInfo> batchInfos = batchInfoService.findAll();
            batchInfos.forEach(batchInfo -> {
                Order order = orderService.findById(batchInfo.getOrderId());
                if (Objects.nonNull(order) &&
                        (order.getOrderStatus() == OrderStatus.PROGRESSING.value
                                && batchInfo.getEntrustTimes() > batchInfo.getCompleteTimes())) {
                    DelayMsg<BatchInfo> delayedMsg = new DelayMsg<>(batchInfo, batchInfo.getStepTime() * 1000);
                    delayQueue.put(delayedMsg);
                }
            });
        } catch (Exception e) {
            logger.error("delayQueue start-up put error: {}", e);
        }


    }

    public void onApplicationEvent(ContextRefreshedEvent event) {
        Executors.newCachedThreadPool().submit(() -> {
            while (true) {
                try {
                    //队列为空时take方法会阻塞
                    DelayMsg delayMsg = delayQueue.take();
                    Object o = delayMsg.getItem();
                    if (o instanceof BatchInfo) {
                        BatchInfo batchInfo = (BatchInfo) o;
                        Order order = orderService.findById(batchInfo.getOrderId());
                        tradeHelper.handleBatchOrder(batchInfo, order);
                    } else {
                        logger.warn("DelayMessage type not found: {}", JsonUtils.getJsonFromObject(o));
                    }
                } catch (Exception e) {
                    logger.info("DelayController Exception:{}", e.getMessage());
                }
            }
        });
    }

    public DelayController() {
        /*Executors.newCachedThreadPool().submit(() -> {
            while (true) {

                try {
                    //队列为空时take方法会阻塞
                    DelayMsg delayMsg = delayQueue.take();
                    Object o = delayMsg.getItem();
                    if (o instanceof BatchInfo) {
                        BatchInfo batchInfo = (BatchInfo) o;
                        tradeHelper.handleBatchOrder(batchInfo);
                    } else {
                        logger.warn("DelayMessage type not found: {}", JsonUtils.getJsonFromObject(o));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("DelayController Exception:{}", e.getMessage());
                }
            }
        });*/
    }
}
