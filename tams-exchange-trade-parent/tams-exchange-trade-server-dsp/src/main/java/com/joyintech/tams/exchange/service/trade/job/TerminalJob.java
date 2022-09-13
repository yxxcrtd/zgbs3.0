package com.joyintech.tams.exchange.service.trade.job;

import com.joyintech.tams.exchange.common.utils.SpringContextUtils;
import com.joyintech.tams.exchange.service.trade.server.agent.EntrustIdManager;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgent;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgentManager;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;


@Component
@EnableScheduling
public class TerminalJob {
    private static final Logger logger = LoggerFactory.getLogger(TerminalJob.class);

    @Autowired
    private TradeAgentManager tradeAgentManager;

    @Scheduled(fixedRateString = "${agent.query.interval:100000}")
    public void queryTrade() {
        if (Objects.isNull(SpringContextUtils.getApplicationContext())) {
            logger.info("applicationContext 为空");
            return;
        }
        Map<String, TradeAgent> tradeRequestMap = tradeAgentManager.getAccountAgents();
        if (MapUtils.isNotEmpty(tradeRequestMap)) {
            tradeRequestMap.values().forEach(TradeAgent::queryNextTrade);
        }
    }

    @Scheduled(cron = "${agent.cache.clean:0 0 16 ? * MON-FRI}")
    public void cleanEntrustCache() {
        EntrustIdManager manager = SpringContextUtils.getApplicationContext().getBean(EntrustIdManager.class);
        try {
            manager.clearCache();
        } catch (Exception e) {
            logger.error("cleanEntrustCache error: {}", e.getMessage(), e);
        }
    }

//    @Scheduled(fixedRateString = "${agent.keep.fixRate:30000}")
//    public void keepClientConnection() {
//        if (Objects.isNull(SpringContextUtils.getApplicationContext())) {
//            logger.info("applicationContext 为空");
//            return;
//        }
//        try {
//            KCBPTradeAgent tradeAgent = SpringContextUtils.getBean(AgentType.KINGDOM_KCBP.value);
//            if (Objects.nonNull(tradeAgent)) {
//                tradeAgent.querySystemStatus();
//            }
//        } catch (Exception e) {
//            logger.error("获取kcbp代理失败，{}", e.getMessage(), e);
//        }
//    }
}
