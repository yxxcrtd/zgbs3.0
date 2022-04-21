package com.joyintech.tams.exchange.service.trade.server;

import com.joyintech.tams.exchange.service.trade.management.service.CommonManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class InstrumentServer implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * 日志对象
     */
    private static final Logger logger = LoggerFactory.getLogger(InstrumentServer.class);
    /*@Autowired
    private InstrumentManagement instrumentManagement;
    @Autowired
    private InstrumentService instrumentService;*/
    @Autowired
    private CommonManagement commonManagement;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
           /* List<MinPrice> minPriceList = instrumentManagement.getMinPrice();
            instrumentService.saveMinPrice(minPriceList);

            List<MinTrdUnitLimit> minTrdUnitLimitList = instrumentManagement.getMinTrdUnitLimit();
            instrumentService.saveTrdUnitLimit(minTrdUnitLimitList);*/

            commonManagement.minPriceInCache();
            commonManagement.minTrdUnitLimitInCache();
            commonManagement.tradeTimeCache();

        } catch (Exception e) {
            logger.error("flush minPrice and minTrdUnitLimit,", e);
        }
    }
}
