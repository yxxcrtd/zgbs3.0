package com.joyintech.tams.exchange.terminal.job;

import com.joyintech.tams.exchange.common.constant.AgentType;
import com.joyintech.tams.exchange.common.utils.SpringContextUtils;
import com.joyintech.tams.exchange.terminal.trade.EntrustIdManager;
import com.joyintech.tams.exchange.terminal.trade.ITradeRequest;
import com.joyintech.tams.exchange.terminal.trade.kcbp.KCBPTradeClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;


@Component
@EnableScheduling
public class TerminalJob {
    private static final Logger logger = LoggerFactory.getLogger(TerminalJob.class);

    @Value("${kcbp.active:false}")
    private boolean isActive_kcbp;

    @Scheduled(fixedRateString = "${agent.query.interval:1000}")
    public void queryTrade() {
        if (Objects.isNull(SpringContextUtils.getApplicationContext())) {
            logger.info("applicationContext 为空");
            return;
        }
        Map<String, ITradeRequest> tradeRequestMap = SpringContextUtils.getApplicationContext().getBeansOfType(ITradeRequest.class);
        if (Objects.nonNull(tradeRequestMap)) {
            tradeRequestMap.values().forEach(ITradeRequest::queryNextTrade);
        }
    }

    @Scheduled(cron = "${agent.cache.clean:0 0 16 ? * MON-FRI}")
    public void cleanEntrustCache() {
        EntrustIdManager manager = SpringContextUtils.getApplicationContext().getBean(EntrustIdManager.class);
        try {
            manager.clearCache();
        } catch (Exception e) {
            logger.info("cleanEntrustCache error: {}", e);
        }
    }

    @Scheduled(fixedRateString = "${kcbp.keep.fixRate:30000}")
    public void keepClientConnection() {
        if (!isActive_kcbp) return;
        if (Objects.isNull(SpringContextUtils.getApplicationContext())) {
            logger.info("applicationContext 为空");
            return;
        }
        try {
            KCBPTradeClient kcbpTradeClient = SpringContextUtils.getBean(AgentType.KINGDOM_KCBPW.value);
            if (Objects.nonNull(kcbpTradeClient)) {
                kcbpTradeClient.querySystemStatus();
            }
        } catch (Exception e) {
            logger.warn("获取kcbp代理失败，{}", e);
        }
    }
}
