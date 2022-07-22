package com.joyintech.tams.exchange.service.trade.scheduled.instrument;

import com.joyintech.tams.exchange.common.cache.impl.QuoteTickServiceImpl;
import com.joyintech.tams.exchange.service.trade.management.service.InstrumentManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
@EnableScheduling
public class InstrumentCache {

    private static final Logger logger = LoggerFactory.getLogger(InstrumentCache.class);

    @Autowired
    private QuoteTickServiceImpl quoteTickService;
    @Autowired
    private InstrumentManagement instrumentManagement;

    @Scheduled(cron = "${scheduled.instrument.price}")
    public void updateInstrumentLimitPrice() {
        //定时缓存行情到内存，为了验证涨跌停价不取redis
        logger.info("update tick cache...");
        quoteTickService.coverCache();
    }

    @Scheduled(cron = "${scheduled.instrument.flushData:0 0 9 ? * MON-FRI}")
    public void flushInstrument() {
        //从投管调用合约接口更新数据
        logger.info("flushInstrument begin...");
        instrumentManagement.flushInstrumentData(new HashMap<String, Object>());
    }

    @Scheduled(cron = "${scheduled.instrument.flushCache:0 0 */1 * * ?}")
    public void flushInstrumentCache() {
        logger.info("从投管调同步基础数据到交易端 开始...");
        instrumentManagement.flushInstrumentData(new HashMap<String, Object>());
        logger.info("从投管调同步基础数据到交易端 结束...");
    }

}
