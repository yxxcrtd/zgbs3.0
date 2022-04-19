package com.joyintech.tams.exchange.terminal.trade.jsd;

import com.google.common.collect.Maps;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.db.domain.Account;
import com.joyintech.tams.exchange.common.db.domain.CancelEntrustBean;
import com.joyintech.tams.exchange.common.db.domain.Entrust;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.terminal.agent.ITerminalMsgNotify;
import com.joyintech.tams.exchange.terminal.agent.TerminalAgent;
import com.joyintech.tams.exchange.terminal.constant.AccountLoginStatus;
import com.joyintech.tams.exchange.terminal.trade.EntrustIdManager;
import com.joyintech.tams.exchange.terminal.trade.ITradeRequest;
import com.joyintech.tams.exchange.terminal.trade.TradeResponseImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * create By MAXX on 2020/6/19
 */
@Component("jsd")
@ConditionalOnProperty(value = "jsd.active", matchIfMissing = false, havingValue = "true")
public class JSDTradeClient implements ITradeRequest {
    private static final Logger logger = LoggerFactory.getLogger(JSDTradeClient.class);

    @Value("${agent.query.count:2}")
    private int tradeCount;//查询次数
    @Value("${jsd.connectTimeout:10000}")
    private int connectTimeout;
    @Value("${netty.reconnect.delay:2}")
    private int delay;
    private ITerminalMsgNotify iTerminalMsgNotify;
    @Autowired
    private TradeResponseImpl tradeResponse;
    @Autowired
    private EntrustIdManager entrustIdManager;
    private TerminalAgent agent;

    private Map<String, JSDTradeClientImpl> counterClientMap = Maps.newConcurrentMap();

    @Override
    public Set<String> getLoginAccount() {
        Set<String> res = new HashSet<>();
        counterClientMap.values().forEach(c -> res.addAll(c.getLoginAccount()));
        return res;
    }

    @Override
    public void setITerminalMsgNotify(ITerminalMsgNotify iTerminalMsgNotify) {
        this.iTerminalMsgNotify = iTerminalMsgNotify;
        counterClientMap.values().forEach(c -> c.setITerminalMsgNotify(iTerminalMsgNotify));
    }

    /***
     * @Description: 登录
     * @Param: [account]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/6/22
     */
    @Override
    public void reqLogin(Account account, ServerExtendInfo serverExtendInfo) {
        logger.info("req login, account = {}", account.getId());
        JSDTradeClientImpl accountClient = counterClientMap.getOrDefault(account.getId(), null);
        if (accountClient != null) {
            if (!accountClient.hadInit()) {
                accountClient.init(agent, serverExtendInfo, iTerminalMsgNotify);
            }
        } else {
            accountClient = new JSDTradeClientImpl();
            accountClient.setConnectTimeout(connectTimeout);
            accountClient.setDelay(delay);
            accountClient.setITerminalMsgNotify(iTerminalMsgNotify);
            accountClient.setTradeCount(tradeCount);
            accountClient.setTradeResponse(tradeResponse);
            accountClient.setEntrustIdManager(entrustIdManager);
            accountClient.init(agent, serverExtendInfo, iTerminalMsgNotify);
            counterClientMap.put(account.getId(), accountClient);
        }
        accountClient.reqLogin(account, serverExtendInfo);
    }

    /***
     * @Description: 初始化
     * @Param: [agent, accountMap, iTerminalMsgNotify]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/6/22
     */
    @Override
    public void init(TerminalAgent agent, ServerExtendInfo serverExtendInfo, ITerminalMsgNotify iTerminalMsgNotify) {
        logger.info("trade client init: extendInfo = {}", serverExtendInfo);
        this.iTerminalMsgNotify = iTerminalMsgNotify;
        this.agent = agent;
    }

    @Override
    public void reqEntrusts(String accountId, Long requestId) {
        JSDTradeClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            accountClient.reqEntrusts(accountId, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
    }

    @Override
    public void reqTrades(String accountId, Long requestId) {
        JSDTradeClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            accountClient.reqTrades(accountId, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
    }

    //普通买卖
    @Override
    public void entrust(Entrust entrust, Long requestId) {
        JSDTradeClientImpl accountClient = counterClientMap.getOrDefault(entrust.getAccountId(), null);
        if (accountClient != null) {
            accountClient.entrust(entrust, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", entrust.getAccountId());
        }
    }

    //大宗、协议回购
    @Override
    public void integratedEntrust(IntegratedEntrust integratedEntrust, Long requestId) {
        JSDTradeClientImpl accountClient = counterClientMap.getOrDefault(integratedEntrust.getAccountId(), null);
        if (accountClient != null) {
            accountClient.integratedEntrust(integratedEntrust, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", integratedEntrust.getAccountId());
        }
    }

    @Override
    public void cancelEntrust(CancelEntrustBean cancelEntrustBean, Long requestId) {
        JSDTradeClientImpl accountClient = counterClientMap.getOrDefault(cancelEntrustBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.cancelEntrust(cancelEntrustBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", cancelEntrustBean.getAccountId());
        }
    }

    @Override
    public void cancelIntegratedEntrust(CancelEntrustBean cancelEntrustBean, Long requestId) {
        JSDTradeClientImpl accountClient = counterClientMap.getOrDefault(cancelEntrustBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.cancelIntegratedEntrust(cancelEntrustBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", cancelEntrustBean.getAccountId());
        }
    }

    @Override
    public void queryIntegratedQuotes(QuoteQryBean quoteQryBean, Long requestId) {
        JSDTradeClientImpl accountClient = counterClientMap.getOrDefault(quoteQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryIntegratedQuotes(quoteQryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", quoteQryBean.getAccountId());
        }
    }

    @Override
    public void queryEnable(EnableQryBean qryBean, Long requestId) {
        JSDTradeClientImpl accountClient = counterClientMap.getOrDefault(qryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryEnable(qryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", qryBean.getAccountId());
        }
    }

    @Override
    public void queryHistoryTrade(HistoryQryBean historyQryBean, Long requestId) {
        JSDTradeClientImpl accountClient = counterClientMap.getOrDefault(historyQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryHistoryTrade(historyQryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", historyQryBean.getAccountId());
        }
    }

    @Override
    public void queryCustomerFinance(String accountId, Long requestId) {
        JSDTradeClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            accountClient.queryCustomerFinance(accountId, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
    }

    /*** 
     * @Description: 持仓查询
     * @Param: [positionQueryBean, requestId] 
     * @return: void
     * @Author: MAXX
     * @Date: 2020/9/28 
     */
    @Override
    public void queryAccountPosition(PositionQueryBean positionQueryBean, Long requestId) {
        JSDTradeClientImpl accountClient = counterClientMap.getOrDefault(positionQueryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryAccountPosition(positionQueryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", positionQueryBean.getAccountId());
        }
    }

    /***
     * @Description: 债券质押率
     * @Param: [accountId, market, stockCode, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/5/13
     */
    @Override
    public void queryPledgeDiscount(String accountId, String market, String stockCode, Long requestId) {
        JSDTradeClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            accountClient.queryPledgeDiscount(accountId, market, stockCode, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
    }

    /***
     * @Description: 标准券查询
     * @Param: [accountId, market, stockCode, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/5/13
     */
    @Override
    public void queryStandardBonds(StandardBondQryBean standardBondQryBean, Long requestId) {
        JSDTradeClientImpl accountClient = counterClientMap.getOrDefault(standardBondQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryStandardBonds(standardBondQryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", standardBondQryBean.getAccountId());
        }
    }

    public void queryInstrumentInfo(InstrumentQryBean instrumentQryBean, Long requestId) {
        JSDTradeClientImpl accountClient = counterClientMap.getOrDefault(instrumentQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryInstrumentInfo(instrumentQryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", instrumentQryBean.getAccountId());
        }
    }

    @Override
    public void queryNextTrade() {
        counterClientMap.values().forEach(a -> a.queryNextTrade());
    }

    //大宗交易行情查询
    @Override
    public void queryIntegratedBulkQuote(IntegratedBulkQuoteQryBean integratedBulkQuoteQryBean, Long requestId) {
        JSDTradeClientImpl accountClient = counterClientMap.getOrDefault(integratedBulkQuoteQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryIntegratedBulkQuote(integratedBulkQuoteQryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", integratedBulkQuoteQryBean.getAccountId());
        }
    }

    //盘后大宗交易行情查询
    @Override
    public void queryIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean, Long requestId) {
        JSDTradeClientImpl accountClient = counterClientMap.getOrDefault(integratedBulkCloseQuoteQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryIntegratedBulkCloseQuote(integratedBulkCloseQuoteQryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", integratedBulkCloseQuoteQryBean.getAccountId());
        }
    }

    //上海协议回购行情查询
    @Override
    public void queryIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean, Long requestId) {
        //todo
    }

    //上海固收行情查询
    @Override
    public void queryIntegratedFixedIncomeQuote(IntegratedFixedIncomeQuoteQryBean integratedFixedIncomeQuoteQryBean, Long requestId) {
        //todo
    }

    @Override
    public void reqReissueEntrust(String accountId, Long requestId) {
        JSDTradeClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            accountClient.reqReissueEntrust(accountId, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
    }

    @Override
    public void reqReissueTrade(String accountId, Long requestId) {
        JSDTradeClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            accountClient.reqReissueTrade(accountId, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
    }

    @Override
    public int getStatus(String accountId) {
        int res = AccountLoginStatus.NOT_LOGGED.value;
        JSDTradeClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            res = accountClient.getStatus(accountId);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
        return res;
    }

    @Override
    public void setTradeStatus(String accountId, AccountLoginStatus status) {
        JSDTradeClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            accountClient.setTradeStatus(accountId, status);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
    }

	@Override
	public void queryNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean, Long requestId) {
		// TODO Auto-generated method stub
		
	}

}
