package com.joyintech.tams.exchange.terminal.trade.kdgateway;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.constant.AgentType;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.terminal.agent.ITerminalMsgNotify;
import com.joyintech.tams.exchange.terminal.agent.TerminalAgent;
import com.joyintech.tams.exchange.terminal.constant.AccountLoginStatus;
import com.joyintech.tams.exchange.terminal.trade.EntrustIdManager;
import com.joyintech.tams.exchange.terminal.trade.ITradeRequest;
import com.joyintech.tams.exchange.terminal.trade.TradeResponseImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class KDGatewayClientImpl implements ITradeRequest {

    private static final Logger logger = LoggerFactory.getLogger(KDGatewayClientImpl.class);

    private KDGatewayApi kdGatewayApi;

    private TradeResponseImpl kdGatewayResponse;

    private String terminalInfo;

    private Map<String, Account> serverAccountMap = Maps.newConcurrentMap();

    // 记录已经登录的账号
    private Map<String, KDGatewayConnection> kdGatewayAccounts = Maps.newConcurrentMap();

    // 查询线程
    private List<ThreadPoolExecutor> poolExecutors;

    // 记录查询的连接
    private List<KDGatewayApi> queryKDTradeApis;

    private ITerminalMsgNotify iTerminalMsgNotify;

    private EntrustIdManager entrustIdManager;

    public Map<String, KDGatewayConnection> getKdGatewayAccounts() {
        return kdGatewayAccounts;
    }

    public void setITerminalMsgNotify(ITerminalMsgNotify iTerminalMsgNotify) {
        this.iTerminalMsgNotify = iTerminalMsgNotify;
    }

    private int kdGatewayTradeCount;

    public ServerExtendInfo getServerExtendInfo() {
        return serverExtendInfo;
    }

    private ServerExtendInfo serverExtendInfo;

    public void addKDGatewayAccount(KDGatewayConnection kdGatewayConnection) {
        kdGatewayAccounts.put(kdGatewayConnection.getAccountId(), kdGatewayConnection);
    }

    public KDGatewayConnection getKdGatewayAccounts(String accountId) {
        KDGatewayConnection kdGatewayAccount = kdGatewayAccounts.get(accountId);
        if (Objects.isNull(kdGatewayAccount)) {
            Account account = serverAccountMap.get(accountId);
            // todo 有新增账号或者启用的账号没有通知到终端
            if (Objects.nonNull(account)) {
                KDGatewayConnection connection = kdGatewayApi.accountLogin(account, terminalInfo);
                if (Objects.nonNull(connection)) {
                    kdGatewayAccount = connection;
                } else {
                    logger.error("accountId:{} finance account:{} login failed", accountId, account.getFinanceAccount());
                }
            }
        }
        return kdGatewayAccount;
    }

    @Override
    public Set<String> getLoginAccount() {
        return kdGatewayAccounts.keySet();
    }

    @Override
    public void reqLogin(Account account, ServerExtendInfo serverExtendInfo) {
        KDGatewayConnection connection = kdGatewayApi.accountLogin(account, terminalInfo);
        ResultBean resultBean;
        if (Objects.nonNull(connection)) {
            serverAccountMap.put(account.getId(), account);
            resultBean = new ResultBean(ErrorCode.SUCCESS, account);
        } else {
            resultBean = new ResultBean(ErrorCode.TERMINAL_API_ERROR, account);
        }
        kdGatewayResponse.resLogin(resultBean, account.getId());
    }

    @Override
    public void reqEntrusts(String accountId, Long requestId) {
        List<Entrust> entrustList = kdGatewayApi.reqEntrusts(accountId);
        kdGatewayResponse.resQueryEntrusts(entrustList, requestId);
    }

    @Override
    public void init(TerminalAgent agent, ServerExtendInfo serverExtendInfo, ITerminalMsgNotify iTerminalMsgNotify) {
        kdGatewayResponse.setTerminalAgent(agent);
        kdGatewayApi = new KDGatewayApi(this);
        kdGatewayApi.setEntrustIdManager(entrustIdManager);
        poolExecutors = Lists.newArrayListWithCapacity(kdGatewayTradeCount);
        queryKDTradeApis = Lists.newArrayListWithCapacity(kdGatewayTradeCount);
        serverExtendInfo = serverExtendInfo;
        for (int i = 0; i < kdGatewayTradeCount; i++) {
            KDGatewayApi kdGatewayApi = new KDGatewayApi(this);
            kdGatewayApi.setEntrustIdManager(entrustIdManager);
            queryKDTradeApis.add(kdGatewayApi);
            poolExecutors.add(new ThreadPoolExecutor(1, 1, 0,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingDeque<>(100),
                    new ThreadPoolExecutor.DiscardPolicy()));
        }
        this.iTerminalMsgNotify = iTerminalMsgNotify;
    }

    @Override
    public void reqTrades(String accountId, Long requestId) {
        List<Trade> tradeList = kdGatewayApi.reqTrades(accountId);
        kdGatewayResponse.resQueryTrade(tradeList, requestId);
    }

    @Override
    public void entrust(Entrust entrust, Long requestId) {
        Entrust backEntrust = kdGatewayApi.reqEntrust(entrust);
        kdGatewayResponse.resEntrust(backEntrust);
    }

    @Override
    public void integratedEntrust(IntegratedEntrust integratedEntrust, Long requestId) {
        IntegratedEntrust backIntegratedEntrust = kdGatewayApi.reqIntegratedEntrust(integratedEntrust);
        kdGatewayResponse.resIntegratedEntrust(backIntegratedEntrust);
    }

    @Override
    public void cancelEntrust(CancelEntrustBean cancelEntrustBean, Long requestId) {
        cancelEntrustBean.setAgentType(AgentType.KD_GATEWAY.value);
        CancelEntrustBean backEntrust = kdGatewayApi.reqCancelOrder(cancelEntrustBean);
        kdGatewayResponse.resCancelEntrust(backEntrust);
    }

    @Override
    public void cancelIntegratedEntrust(CancelEntrustBean cancelEntrustBean, Long requestId) {
        cancelEntrustBean.setAgentType(AgentType.KD_GATEWAY.value);
        CancelEntrustBean backEntrust = kdGatewayApi.reqCancelIntegratedEntrust(cancelEntrustBean);
        kdGatewayResponse.resCancelIntegratedEntrust(backEntrust);
    }

    @Override
    public void queryIntegratedQuotes(QuoteQryBean quoteQryBean, Long requestId) {
        List<IntegratedQuote> backIntegratedQuote = kdGatewayApi.reqIntegratedQuotes(quoteQryBean);
        kdGatewayResponse.resQueryIntegratedQuotes(backIntegratedQuote, requestId);
    }

    @Override
    public void queryEnable(EnableQryBean qryBean, Long requestId) {
        int enableAmount = kdGatewayApi.reqEnable(qryBean);
        kdGatewayResponse.resQueryEnable(enableAmount, requestId);
    }

    @Override
    public void queryHistoryTrade(HistoryQryBean historyQryBean, Long requestId) {
        List<HistoryTrade> historyTrades = kdGatewayApi.reqHistoryTrade(historyQryBean);
        kdGatewayResponse.resQueryHistoryTrade(historyTrades, requestId);
    }

    @Override
    public void queryCustomerFinance(String accountId, Long requestId) {
        List<AccountFinance> backAccountFinances = kdGatewayApi.reqCustomerFinance(accountId);
        kdGatewayResponse.resQueryCustomerFinance(backAccountFinances, requestId);
    }

    @Override
    public void queryAccountPosition(PositionQueryBean positionQueryBean, Long requestId) {
        List<PositionBean> positionList = kdGatewayApi.queryAccountPosition(positionQueryBean);
        kdGatewayResponse.resQueryAccountPosition(positionList, requestId);
    }

    /***
     * @Description: 债券质押率返回
     * @Param: [accountId, market, stockCode, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/5/14
     */
    @Override
    public void queryPledgeDiscount(String accountId, String market, String stockCode, Long requestId) {
        List<PledgeDiscount> pledgeDiscountList = kdGatewayApi.queryPledgeDiscount(accountId, market, stockCode);
        kdGatewayResponse.resQueryPledgeDiscount(pledgeDiscountList, requestId);
    }

    /***
     * @Description: 标准券查询
     * @Param: [accountId, market, stockCode, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/5/14
     */
    @Override
    public void queryStandardBonds(StandardBondQryBean standardBondQryBean, Long requestId) {
        List<StandardBonds> list = kdGatewayApi.queryStandardBonds(standardBondQryBean);
        kdGatewayResponse.resQueryStandardBonds(list, requestId);
    }

    @Override
    public void queryInstrumentInfo(InstrumentQryBean instrumentQryBean, Long requestId) {
    }

    public void onEntrust(Entrust entrust) {
        entrust.setNoCancelEntrust(true);
        iTerminalMsgNotify.onEntrust(entrust);
    }

    public void onIntegratedEntrust(IntegratedEntrust integratedEntrust) {
        integratedEntrust.setNoCancelEntrust(true);
        iTerminalMsgNotify.onIntegratedEntrust(integratedEntrust);
    }

    public void onTrade(Trade trade) {
        if (Strings.isNullOrEmpty(trade.getBrokerTradeId())) {
            trade.setBrokerTradeId(trade.getEntrustId());
        }
        trade.setNoCancelEntrust(true);
        iTerminalMsgNotify.onTrade(trade);
    }

    @Override
    public void queryNextTrade() {
        // 获取已经登录账号 和 连接
        if (kdGatewayAccounts.size() > 0) {
            for (Map.Entry<String, KDGatewayConnection> entry : kdGatewayAccounts.entrySet()) {
                KDGatewayConnection kdGatewayConnection = entry.getValue();
                int index = (kdGatewayConnection.getAccountId().hashCode() & Integer.MAX_VALUE) % kdGatewayTradeCount;
                if (poolExecutors.size() > 1 && queryKDTradeApis.size() > 1) {
                    poolExecutors.get(index).submit(() -> {
                        if (kdGatewayApi.getStatus(kdGatewayConnection.getAccountId()) == AccountLoginStatus.TRADING.value) {
                            queryKDTradeApis.get(index).queryIncrementKDTrade(kdGatewayConnection);
                            queryKDTradeApis.get(index).queryIncrementKDEntrust(kdGatewayConnection);
                        }
                    });
                }
            }
        }
    }

    @Override
    public void queryIntegratedBulkQuote(IntegratedBulkQuoteQryBean integratedBulkQuoteQryBean, Long requestId) {
        List<IntegratedBulkQuoteBean> backIntegratedBulkQuote = kdGatewayApi.reqIntegratedBulkQuotes(integratedBulkQuoteQryBean);
        kdGatewayResponse.resQueryIntegratedBulkQuote(backIntegratedBulkQuote, requestId);
    }

    @Override
    public void queryIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean, Long requestId) {
        List<IntegratedBulkCloseQuoteBean> backIntegratedBulkCloseQuote = kdGatewayApi.reqIntegratedBulkCloseQuotes(integratedBulkCloseQuoteQryBean);
        kdGatewayResponse.resQueryIntegratedBulkCloseQuote(backIntegratedBulkCloseQuote, requestId);
    }

    @Override
    public void queryIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean, Long requestId) {
        List<IntegratedRepurchaseQuoteBean> backIntegratedRepurchaseQuote = kdGatewayApi.reqIntegratedRepurchaseQuotes(integratedRepurchaseQuoteQryBean);
        kdGatewayResponse.resQueryIntegratedRepurchaseQuote(backIntegratedRepurchaseQuote, requestId);
    }

    @Override
    public void queryIntegratedFixedIncomeQuote(IntegratedFixedIncomeQuoteQryBean integratedFixedIncomeQuoteQryBean, Long requestId) {
        List<IntegratedFixedIncomeQuoteBean> backIntegratedFixedIncomeQuote = kdGatewayApi.reqIntegratedFixedIncomeQuotes(integratedFixedIncomeQuoteQryBean);
        kdGatewayResponse.resQueryIntegratedFixedIncomeQuote(backIntegratedFixedIncomeQuote, requestId);
    }

    @Override
    public void reqReissueEntrust(String accountId, Long requestId) {
        Map<String, EntrustInterface> entrustInterfaceMap = kdGatewayApi.reqReissueEntrust(accountId, requestId);
        kdGatewayResponse.resQueryReissueEntrust(accountId, entrustInterfaceMap, requestId);
    }

    @Override
    public void reqReissueTrade(String accountId, Long requestId) {
        List<Trade> tradeList = kdGatewayApi.reqReissueTrade(accountId, requestId);
        kdGatewayResponse.resQueryReissueTrade(accountId, tradeList, requestId);
    }

    public void setKdGatewayResponse(TradeResponseImpl kdGatewayResponse) {
        this.kdGatewayResponse = kdGatewayResponse;
    }

    public void setTerminalInfo(String terminalInfo) {
        this.terminalInfo = terminalInfo;
    }

    public void setKdGatewayTradeCount(int kdGatewayTradeCount) {
        this.kdGatewayTradeCount = kdGatewayTradeCount;
    }

    @Override
    public int getStatus(String accountId) {
        return kdGatewayApi.getStatus(accountId);
    }

    @Override
    public void setTradeStatus(String accountId, AccountLoginStatus status) {
        kdGatewayApi.setStatus(accountId, status);
    }


    public void setEntrustIdManager(EntrustIdManager entrustIdManager) {
        this.entrustIdManager = entrustIdManager;
    }

	@Override
	public void queryNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean, Long requestId) {
		// TODO Auto-generated method stub
		
	}
}
