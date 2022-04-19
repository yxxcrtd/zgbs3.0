package com.joyintech.tams.exchange.service.trade.server;

import com.joyintech.tams.exchange.common.bean.MinPrice;
import com.joyintech.tams.exchange.common.bean.MinTrdUnitLimit;
import com.joyintech.tams.exchange.datacommon.db.service.InstrumentService;
import com.joyintech.tams.exchange.service.trade.management.service.CommonManagement;
import com.joyintech.tams.exchange.service.trade.management.service.InstrumentManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InstrumentServer implements ApplicationListener<ApplicationReadyEvent> {

    /**
     * 日志对象
     */
    private static final Logger logger = LoggerFactory.getLogger(InstrumentServer.class);
    @Autowired
    private InstrumentManagement instrumentManagement;
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private CommonManagement commonManagement;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        try {
            List<MinPrice> minPriceList = instrumentManagement.getMinPrice();
            instrumentService.saveMinPrice(minPriceList);

            List<MinTrdUnitLimit> minTrdUnitLimitList = instrumentManagement.getMinTrdUnitLimit();
            instrumentService.saveTrdUnitLimit(minTrdUnitLimitList);

            commonManagement.tradeTimeCache();
        } catch (Exception e) {
            logger.error("flush minPrice and minTrdUnitLimit,", e);
        }
    }
}
