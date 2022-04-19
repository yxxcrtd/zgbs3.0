package com.joyintech.tams.exchange.terminal.trade.kdgateway;

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

@Component("KDGateway")
@ConditionalOnProperty(value = "KDGateway.active", matchIfMissing = false, havingValue = "true")
public class KDGatewayClient implements ITradeRequest {
    private static final Logger logger = LoggerFactory.getLogger(KDGatewayClient.class);

    @Autowired
    private TradeResponseImpl kdGatewayResponse;
    @Autowired
    private EntrustIdManager entrustIdManager;
    @Autowired
    private String terminalInfo;
    @Value("${agent.query.count:2}")
    private int kdGatewayTradeCount;

    private ITerminalMsgNotify iTerminalMsgNotify;

    private Map<ServerExtendInfo, KDGatewayClientImpl> kdGatewayClientMap = Maps.newConcurrentMap();

    private Map<String, KDGatewayClientImpl> counterClientMap = Maps.newConcurrentMap();

    public void setITerminalMsgNotify(ITerminalMsgNotify iTerminalMsgNotify) {
        this.iTerminalMsgNotify = iTerminalMsgNotify;
        kdGatewayClientMap.values().forEach(c->c.setITerminalMsgNotify(iTerminalMsgNotify));
    }

    @Override
    public Set<String> getLoginAccount() {
        Set<String> res = new HashSet<>();
        kdGatewayClientMap.values().forEach(c -> res.addAll(c.getLoginAccount()));
        return res;
    }

    @Override
    public void reqLogin(Account account, ServerExtendInfo serverExtendInfo) {
        logger.info("req login, account = {}", account.getId());
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(account.getId(), null);
        if (accountClient != null) {
            accountClient.reqLogin(account, serverExtendInfo);
            return;
        }

        if (serverExtendInfo == null) {
            logger.warn("client not exist, accountId = {}", account.getId());
            return;
        }

        KDGatewayClientImpl client = kdGatewayClientMap.getOrDefault(serverExtendInfo, null);
        if (client == null) {
            logger.warn("client not exist, extendInfo = {}, accountId = {}", serverExtendInfo, account.getId());
            return;
        }

        counterClientMap.put(account.getId(), client);
        client.reqLogin(account, serverExtendInfo);
    }


    @Override
    public void init(TerminalAgent agent, ServerExtendInfo serverExtendInfo, ITerminalMsgNotify iTerminalMsgNotify) {
        logger.info("trade client init: extendInfo = {}", serverExtendInfo);
        if (!kdGatewayClientMap.containsKey(serverExtendInfo)) {
            KDGatewayClientImpl client = new KDGatewayClientImpl();
            client.setTerminalInfo(terminalInfo);
            client.setKdGatewayResponse(kdGatewayResponse);
            client.setKdGatewayTradeCount(kdGatewayTradeCount);
            client.setEntrustIdManager(entrustIdManager);
            client.init(agent, serverExtendInfo, iTerminalMsgNotify);
            kdGatewayClientMap.put(serverExtendInfo, client);
        }
    }

    @Override
    public void reqEntrusts(String accountId, Long requestId) {
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            accountClient.reqEntrusts(accountId, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
    }

    @Override
    public void reqTrades(String accountId, Long requestId) {
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            accountClient.reqTrades(accountId, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
    }

    //普通买卖
    @Override
    public void entrust(Entrust entrust, Long requestId) {
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(entrust.getAccountId(), null);
        if (accountClient != null) {
            accountClient.entrust(entrust, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", entrust.getAccountId());
        }
    }

    //大宗、协议回购
    @Override
    public void integratedEntrust(IntegratedEntrust integratedEntrust, Long requestId) {
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(integratedEntrust.getAccountId(), null);
        if (accountClient != null) {
            accountClient.integratedEntrust(integratedEntrust, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", integratedEntrust.getAccountId());
        }
    }

    @Override
    public void cancelEntrust(CancelEntrustBean cancelEntrustBean, Long requestId) {
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(cancelEntrustBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.cancelEntrust(cancelEntrustBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", cancelEntrustBean.getAccountId());
        }
    }

    @Override
    public void cancelIntegratedEntrust(CancelEntrustBean cancelEntrustBean, Long requestId) {
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(cancelEntrustBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.cancelIntegratedEntrust(cancelEntrustBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", cancelEntrustBean.getAccountId());
        }
    }

    @Override
    public void queryIntegratedQuotes(QuoteQryBean quoteQryBean, Long requestId) {
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(quoteQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryIntegratedQuotes(quoteQryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", quoteQryBean.getAccountId());
        }
    }

    @Override
    public void queryEnable(EnableQryBean qryBean, Long requestId) {
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(qryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryEnable(qryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", qryBean.getAccountId());
        }
    }

    @Override
    public void queryHistoryTrade(HistoryQryBean historyQryBean, Long requestId) {
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(historyQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryHistoryTrade(historyQryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", historyQryBean.getAccountId());
        }
    }

    @Override
    public void queryCustomerFinance(String accountId, Long requestId) {
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            accountClient.queryCustomerFinance(accountId, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
    }

    @Override
    public void queryAccountPosition(PositionQueryBean positionQueryBean, Long requestId) {
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(positionQueryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryAccountPosition(positionQueryBean, requestId);
        } else {
            logger.warn("client not exist, requestId = {}, accountId = {}", requestId, positionQueryBean.getAccountId());
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
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
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
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(standardBondQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryStandardBonds(standardBondQryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", standardBondQryBean.getAccountId());
        }
    }

    public void queryInstrumentInfo(InstrumentQryBean instrumentQryBean, Long requestId) {
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(instrumentQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryInstrumentInfo(instrumentQryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", instrumentQryBean.getAccountId());
        }
    }

    @Override
    public void queryNextTrade() {
        kdGatewayClientMap.values().forEach(a -> a.queryNextTrade());
    }

    @Override
    public void queryIntegratedBulkQuote(IntegratedBulkQuoteQryBean integratedBulkQuoteQryBean, Long requestId) {
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(integratedBulkQuoteQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryIntegratedBulkQuote(integratedBulkQuoteQryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", integratedBulkQuoteQryBean.getAccountId());
        }
    }

    @Override
    public void queryIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean, Long requestId) {
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(integratedBulkCloseQuoteQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryIntegratedBulkCloseQuote(integratedBulkCloseQuoteQryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", integratedBulkCloseQuoteQryBean.getAccountId());
        }
    }

    @Override
    public void queryIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean, Long requestId) {
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(integratedRepurchaseQuoteQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryIntegratedRepurchaseQuote(integratedRepurchaseQuoteQryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", integratedRepurchaseQuoteQryBean.getAccountId());
        }
    }

    @Override
    public void queryIntegratedFixedIncomeQuote(IntegratedFixedIncomeQuoteQryBean integratedFixedIncomeQuoteQryBean, Long requestId) {
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(integratedFixedIncomeQuoteQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryIntegratedFixedIncomeQuote(integratedFixedIncomeQuoteQryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", integratedFixedIncomeQuoteQryBean.getAccountId());
        }
    }


    @Override
    public void reqReissueEntrust(String accountId, Long requestId) {
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            accountClient.reqReissueEntrust(accountId, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
    }

    @Override
    public void reqReissueTrade(String accountId, Long requestId) {
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            accountClient.reqReissueTrade(accountId, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
    }

    @Override
    public int getStatus(String accountId) {
        int res = AccountLoginStatus.NOT_LOGGED.value;
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            res = accountClient.getStatus(accountId);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
        return res;
    }

    @Override
    public void setTradeStatus(String accountId, AccountLoginStatus status) {
        KDGatewayClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
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
