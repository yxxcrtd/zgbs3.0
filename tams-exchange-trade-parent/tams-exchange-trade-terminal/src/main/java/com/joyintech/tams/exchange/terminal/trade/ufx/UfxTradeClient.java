package com.joyintech.tams.exchange.terminal.trade.ufx;

import com.google.common.collect.Maps;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.db.domain.Account;
import com.joyintech.tams.exchange.common.db.domain.CancelEntrustBean;
import com.joyintech.tams.exchange.common.db.domain.Entrust;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.net.agent.Agent;
import com.joyintech.tams.exchange.terminal.agent.ITerminalMsgNotify;
import com.joyintech.tams.exchange.terminal.agent.TerminalAgent;
import com.joyintech.tams.exchange.terminal.constant.AccountLoginStatus;
import com.joyintech.tams.exchange.terminal.trade.ITradeRequest;
import com.joyintech.tams.exchange.terminal.trade.TradeResponseImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

@Component("ufx")
@ConditionalOnProperty(value = "ufx.active", matchIfMissing = false, havingValue = "true")
public class UfxTradeClient implements ITradeRequest {
    private static final Logger logger = LoggerFactory.getLogger(UfxTradeClient.class);
    private Agent agent = null;

    @Autowired
    private TradeResponseImpl ufxTradeResponse;
    @Autowired(required = false)
    private String op_station;
    @Value("${agent.query.count:2}")
    private int tradeCount;//查询次数

    private ITerminalMsgNotify iTerminalMsgNotify;
    // 查询线程
    private List<ThreadPoolExecutor> poolExecutors;
    private Map<ServerExtendInfo, UfxTradeClientImpl> ufxTradeClientMap = Maps.newConcurrentMap();
    private Map<String, UfxTradeClientImpl> counterClientMap = Maps.newConcurrentMap();

    public void setITerminalMsgNotify(ITerminalMsgNotify iTerminalMsgNotify) {
        this.iTerminalMsgNotify = iTerminalMsgNotify;
        ufxTradeClientMap.values().forEach(c -> c.setITerminalMsgNotify(iTerminalMsgNotify));
    }

    @Override
    public Set<String> getLoginAccount() {
        Set<String> res = new HashSet<>();
        ufxTradeClientMap.values().forEach(c -> res.addAll(c.getLoginAccount()));
        return res;
    }

    /***
     * @Description: 登录
     * @Param: [account]
     * @return: com.joyintech.tams.exchange.common.bean.ResultBean
     * @Author: MAXX
     * @Date: 2020/3/13 
     */
    @Override
    public void reqLogin(Account account, ServerExtendInfo serverExtendInfo) {
        logger.info("req login, account = {}", account.getId());
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(account.getId(), null);
        if (accountClient != null) {
            accountClient.reqLogin(account, serverExtendInfo);
            return;
        }
        if (serverExtendInfo == null) {
            logger.warn("client not exist, accountId = {}", account.getId());
            return;
        }
        UfxTradeClientImpl client = ufxTradeClientMap.getOrDefault(serverExtendInfo, null);
        if (client == null) {
            logger.warn("client not exist, extendInfo = {}, accountId = {}", serverExtendInfo, account.getId());
            return;
        }
        counterClientMap.put(account.getId(), client);
        client.reqLogin(account, serverExtendInfo);
    }

    /***
     * @Description: 初始化
     * @Param: [accountMap, iTerminalMsgNotify]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/20
     */
    @Override
    public void init(TerminalAgent agent, ServerExtendInfo serverExtendInfo, ITerminalMsgNotify iTerminalMsgNotify) {
        UfxTradeClientImpl client = ufxTradeClientMap.getOrDefault(serverExtendInfo, null);
        if (client == null) {
            client = new UfxTradeClientImpl();
            client.setITerminalMsgNotify(iTerminalMsgNotify);
            client.setOp_station(op_station);
            client.setTradeCount(tradeCount);
            client.setUfxTradeResponse(ufxTradeResponse);
            client.init(agent, serverExtendInfo, iTerminalMsgNotify);
            ufxTradeClientMap.put(serverExtendInfo, client);
        } else {
            if (!client.isConnectionFlag()) {
                client.init(agent, serverExtendInfo, iTerminalMsgNotify);
            }
        }
    }

    @Override
    public void reqEntrusts(String accountId, Long requestId) {
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            accountClient.reqEntrusts(accountId, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
    }

    @Override
    public void reqTrades(String accountId, Long requestId) {
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            accountClient.reqTrades(accountId, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
    }

    //普通买卖
    @Override
    public void entrust(Entrust entrust, Long requestId) {
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(entrust.getAccountId(), null);
        if (accountClient != null) {
            accountClient.entrust(entrust, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", entrust.getAccountId());
        }
    }

    //大宗、协议回购
    @Override
    public void integratedEntrust(IntegratedEntrust integratedEntrust, Long requestId) {
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(integratedEntrust.getAccountId(), null);
        if (accountClient != null) {
            accountClient.integratedEntrust(integratedEntrust, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", integratedEntrust.getAccountId());
        }
    }

    @Override
    public void cancelEntrust(CancelEntrustBean cancelEntrustBean, Long requestId) {
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(cancelEntrustBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.cancelEntrust(cancelEntrustBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", cancelEntrustBean.getAccountId());
        }
    }

    @Override
    public void cancelIntegratedEntrust(CancelEntrustBean cancelEntrustBean, Long requestId) {
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(cancelEntrustBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.cancelIntegratedEntrust(cancelEntrustBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", cancelEntrustBean.getAccountId());
        }
    }

    @Override
    public void queryIntegratedQuotes(QuoteQryBean quoteQryBean, Long requestId) {
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(quoteQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryIntegratedQuotes(quoteQryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", quoteQryBean.getAccountId());
        }
    }

    @Override
    public void queryEnable(EnableQryBean qryBean, Long requestId) {
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(qryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryEnable(qryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", qryBean.getAccountId());
        }
    }

    @Override
    public void queryHistoryTrade(HistoryQryBean historyQryBean, Long requestId) {
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(historyQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryHistoryTrade(historyQryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", historyQryBean.getAccountId());
        }
    }

    @Override
    public void queryCustomerFinance(String accountId, Long requestId) {
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            accountClient.queryCustomerFinance(accountId, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
    }

    //证券持仓快速查询
    @Override
    public void queryAccountPosition(PositionQueryBean positionQueryBean, Long requestId) {
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(positionQueryBean.getAccountId(), null);
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
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
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
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(standardBondQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryStandardBonds(standardBondQryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", standardBondQryBean.getAccountId());
        }
    }

    public void queryInstrumentInfo(InstrumentQryBean instrumentQryBean, Long requestId) {
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(instrumentQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryInstrumentInfo(instrumentQryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", instrumentQryBean.getAccountId());
        }
    }

    @Override
    public void queryNextTrade() {
        ufxTradeClientMap.values().forEach(a -> a.queryNextTrade());
    }

    @Override
    public void queryIntegratedBulkQuote(IntegratedBulkQuoteQryBean integratedBulkQuoteQryBean, Long requestId) {
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(integratedBulkQuoteQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryIntegratedBulkQuote(integratedBulkQuoteQryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", integratedBulkQuoteQryBean.getAccountId());
        }
    }

    @Override
    public void queryIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean, Long requestId) {
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(integratedBulkCloseQuoteQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryIntegratedBulkCloseQuote(integratedBulkCloseQuoteQryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", integratedBulkCloseQuoteQryBean.getAccountId());
        }
    }

    @Override
    public void queryIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean, Long requestId) {
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(integratedRepurchaseQuoteQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryIntegratedRepurchaseQuote(integratedRepurchaseQuoteQryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", integratedRepurchaseQuoteQryBean.getAccountId());
        }
    }

    @Override
    public void queryIntegratedFixedIncomeQuote(IntegratedFixedIncomeQuoteQryBean integratedFixedIncomeQuoteQryBean, Long requestId) {
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(integratedFixedIncomeQuoteQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryIntegratedFixedIncomeQuote(integratedFixedIncomeQuoteQryBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", integratedFixedIncomeQuoteQryBean.getAccountId());
        }
    }

    @Override
    public void reqReissueEntrust(String accountId, Long requestId) {
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            accountClient.reqReissueEntrust(accountId, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
    }

    @Override
    public void reqReissueTrade(String accountId, Long requestId) {
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            accountClient.reqReissueTrade(accountId, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
    }

    @Override
    public int getStatus(String accountId) {
        int res = AccountLoginStatus.NOT_LOGGED.value;
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            res = accountClient.getStatus(accountId);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
        return res;
    }


    @Override
    public void setTradeStatus(String accountId, AccountLoginStatus status) {
        UfxTradeClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
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
