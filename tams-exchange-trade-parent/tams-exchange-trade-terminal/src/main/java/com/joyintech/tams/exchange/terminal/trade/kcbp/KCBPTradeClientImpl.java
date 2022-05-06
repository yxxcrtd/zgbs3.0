package com.joyintech.tams.exchange.terminal.trade.kcbp;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.bean.terminal.RTGSStatusBean;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.net.agent.Agent;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.terminal.agent.ITerminalMsgNotify;
import com.joyintech.tams.exchange.terminal.agent.TerminalAgent;
import com.joyintech.tams.exchange.terminal.config.KCBPRegisterConfig;
import com.joyintech.tams.exchange.terminal.constant.AccountLoginStatus;
import com.joyintech.tams.exchange.terminal.trade.EntrustIdManager;
import com.joyintech.tams.exchange.terminal.trade.ITradeRequest;
import com.joyintech.tams.exchange.terminal.trade.TradeResponseImpl;
import com.szkingdom.kcbpcli.tagKCBPConnectOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class KCBPTradeClientImpl implements ITradeRequest {
    private static final Logger logger = LoggerFactory.getLogger(KCBPTradeClientImpl.class);

    private tagKCBPConnectOption tagConnectOption;

    private ITerminalMsgNotify iTerminalMsgNotify;

    private Map<String, Account> serverAccounts = Maps.newConcurrentMap();

    // 记录已经登录的账号
    private Map<String, KCBPAccount> kcbpAccounts = Maps.newConcurrentMap();

    // 查询线程
    private List<ThreadPoolExecutor> poolExecutors;

    // 记录查询的连接
    private List<KCBPTradeApi> queryTradeApis;

    private Agent agent;

    private TradeResponseImpl kcbpTradeResponse;

    private int kcbpTradeCount;

    private KCBPRegisterConfig kcbpRegisterConfig;

    private EntrustIdManager entrustIdManager;

    // 下单查询api
    private KCBPTradeApi kcbpTradeApi;
    // 判断终端连接是否建立
    private boolean connectionFlag = false;

    public boolean isConnectionFlag() {
        return connectionFlag;
    }

    public void setITerminalMsgNotify(ITerminalMsgNotify iTerminalMsgNotify) {
        this.iTerminalMsgNotify = iTerminalMsgNotify;
    }

    @Override
    public void init(TerminalAgent agent, ServerExtendInfo serverExtendInfo, ITerminalMsgNotify iTerminalMsgNotify) {
        this.agent = agent;
        kcbpTradeResponse.setTerminalAgent(agent);
        tagConnectOption = JsonUtils.getObjectFromJson(JsonUtils.getJsonFromObject(serverExtendInfo), tagKCBPConnectOption.class);
        kcbpTradeApi = new KCBPTradeApi(this);
        kcbpTradeApi.setEntrustIdManager(entrustIdManager);
        // 确认终端连接是否建立
        if (kcbpTradeApi.init(kcbpTradeResponse, serverExtendInfo.getOpEntrustWay())) {
            connectionFlag = true;
            this.iTerminalMsgNotify = iTerminalMsgNotify;
            poolExecutors = Lists.newArrayListWithCapacity(kcbpTradeCount);
            queryTradeApis = Lists.newArrayListWithExpectedSize(kcbpTradeCount);
            for (int i = 0; i < kcbpTradeCount; i++) {
                KCBPTradeApi queryTradeApi = new KCBPTradeApi(this);
                queryTradeApi.setEntrustIdManager(entrustIdManager);
                queryTradeApi.init(kcbpTradeResponse, serverExtendInfo.getOpEntrustWay());
                queryTradeApis.add(queryTradeApi);
                poolExecutors.add(new ThreadPoolExecutor(1, 1, 0,
                        TimeUnit.MILLISECONDS,
                        new LinkedBlockingDeque<>(100),
                        new ThreadPoolExecutor.DiscardPolicy()));
            }
        }
    }

    public void addKCBPAccount(KCBPAccount kcbpAccount) {
        kcbpAccounts.put(kcbpAccount.getAccountId(), kcbpAccount);
    }

    public KCBPAccount getKCBPAccount(String accountId) {
        KCBPAccount kcbpAccount = kcbpAccounts.get(accountId);
        if (Objects.isNull(kcbpAccount)) {
            Account account = serverAccounts.get(accountId);
            // todo 有新增账号或者启用的账号没有通知到终端
            if (Objects.nonNull(account)) {
                ResultBean resultBean = kcbpTradeApi.reqLogin(account);
                if (Objects.isNull(resultBean.getData())) {
                    logger.error("accountId:{} finance account:{} login failed", accountId, account.getFinanceAccount());
                } else {
                    kcbpAccount = (KCBPAccount) resultBean.getData();
                }
            }
        }
        return kcbpAccount;
    }

    public tagKCBPConnectOption getTagConnectOption() {
        return tagConnectOption;
    }

    public Map<String, KCBPAccount> getKcbpAccounts() {
        return kcbpAccounts;
    }

    public KCBPRegisterConfig getKcbpRegisterConfig() {
        return kcbpRegisterConfig;
    }

    @Override
    public Set<String> getLoginAccount() {
        return kcbpAccounts.keySet();
    }

    @Override
    public void reqLogin(Account account, ServerExtendInfo serverExtendInfo) {
        ResultBean resultBean = kcbpTradeApi.reqLogin(account);
        if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
            serverAccounts.put(account.getId(), account);
            logger.info("account {} login success", account.getId());
        } else {
            logger.info("account {} login failed {} -- {}", account.getId(), resultBean.getErrorCode(), resultBean.getErrorMsg());
        }
    }

    @Override
    public void reqEntrusts(String accountId, Long requestId) {
        kcbpTradeApi.reqEntrusts(accountId, requestId);
    }

    @Override
    public void reqTrades(String accountId, Long requestId) {
        kcbpTradeApi.reqTrades(accountId, requestId);
    }

    //普通买卖
    @Override
    public void entrust(Entrust entrust, Long requestId) {
        kcbpTradeApi.entrust(entrust, requestId);
    }

    //大宗、协议回购
    @Override
    public void integratedEntrust(IntegratedEntrust integratedEntrust, Long requestId) {
        kcbpTradeApi.integratedEntrust(integratedEntrust, requestId);
    }

    @Override
    public void cancelEntrust(CancelEntrustBean cancelEntrustBean, Long requestId) {
        kcbpTradeApi.cancelEntrust(cancelEntrustBean, requestId);
    }

    @Override
    public void cancelIntegratedEntrust(CancelEntrustBean cancelEntrustBean, Long requestId) {
        kcbpTradeApi.cancelIntegratedEntrust(cancelEntrustBean, requestId);
    }

    @Override
    public void queryIntegratedQuotes(QuoteQryBean quoteQryBean, Long requestId) {
        kcbpTradeApi.queryIntegratedQuote(quoteQryBean, requestId);
    }

    @Override
    public void queryEnable(EnableQryBean qryBean, Long requestId) {
        kcbpTradeApi.queryEnable(qryBean, requestId);
    }

    @Override
    public void queryHistoryTrade(HistoryQryBean historyQryBean, Long requestId) {
        kcbpTradeApi.queryHistoryTrade(historyQryBean, requestId);
    }

    @Override
    public void queryCustomerFinance(String accountId, Long requestId) {
        kcbpTradeApi.queryCustomerFinance(accountId, requestId);
    }

    /***
     * @Description: 证券持仓快速查询
     * @Param: [positionQueryBean, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/9/28
     */
    @Override
    public void queryAccountPosition(PositionQueryBean positionQueryBean, Long requestId) {
        kcbpTradeApi.queryAccountPosition(positionQueryBean, requestId);
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
        kcbpTradeApi.queryPledgeDiscount(accountId, market, stockCode, requestId);
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
        kcbpTradeApi.queryStandardBonds(standardBondQryBean, requestId);
    }

    @Override
    public void queryIntegratedBulkQuote(IntegratedBulkQuoteQryBean integratedBulkQuoteQryBean, Long requestId) {
        kcbpTradeApi.queryIntegratedBulkQuote(integratedBulkQuoteQryBean, requestId);
    }

    @Override
    public void queryIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean, Long requestId) {
        kcbpTradeApi.queryIntegratedBulkCloseQuote(integratedBulkCloseQuoteQryBean, requestId);
    }

    @Override
    public void queryIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean, Long requestId) {
        kcbpTradeApi.queryIntegratedRepurchaseQuote(integratedRepurchaseQuoteQryBean, requestId);
    }

    @Override
    public void queryIntegratedFixedIncomeQuote(IntegratedFixedIncomeQuoteQryBean integratedFixedIncomeQuoteQryBean, Long requestId) {
        kcbpTradeApi.queryIntegratedFixedIncomeQuote(integratedFixedIncomeQuoteQryBean, requestId);
    }

    public void queryInstrumentInfo(InstrumentQryBean instrumentQryBean, Long requestId) {
        kcbpTradeApi.queryInstrumentInfo(instrumentQryBean, requestId);
    }

    public void onEntrust(Entrust entrust) {
        //kcbp有撤单委托，但是撤单成交匹配的原委托
        iTerminalMsgNotify.onEntrust(entrust);
    }

    public void onIntegratedEntrust(IntegratedEntrust integratedEntrust) {
        //kcbp有撤单委托，但是撤单成交匹配的原委托
        iTerminalMsgNotify.onIntegratedEntrust(integratedEntrust);
    }

    public void onTrade(Trade trade) {
        if (Strings.isNullOrEmpty(trade.getBrokerTradeId())) {
            trade.setBrokerTradeId(trade.getEntrustId());
        }
        trade.setNoCancelEntrust(true);
        iTerminalMsgNotify.onTrade(trade);
    }

    public void onRTGSStatus(RTGSStatusBean rtgsStatusBean) {
        iTerminalMsgNotify.onRTGSStatus(rtgsStatusBean);
    }

    @Override
    public void queryNextTrade() {
        if (kcbpAccounts.size() > 0) {
            for (Map.Entry<String, KCBPAccount> entry : kcbpAccounts.entrySet()) {
                KCBPAccount kcbpAccount = entry.getValue();
                int index = (kcbpAccount.getAccountId().hashCode() & Integer.MAX_VALUE) % kcbpTradeCount;
                if (poolExecutors.size() > 1 && queryTradeApis.size() > 1) {
                    poolExecutors.get(index).submit(() -> {
                        if (kcbpTradeApi.getStatus(kcbpAccount.getAccountId()) == AccountLoginStatus.TRADING.value) {
                            queryTradeApis.get(index).queryIncrementEntrust(kcbpAccount);
                            queryTradeApis.get(index).queryProtocolBack_SH_Entrust(kcbpAccount);
                            queryTradeApis.get(index).queryProtocolBack_SZ_Entrust(kcbpAccount);
                            queryTradeApis.get(index).queryFixedIncomeEntrust(kcbpAccount);

                            queryTradeApis.get(index).queryIncrementTrade(kcbpAccount);
                            queryTradeApis.get(index).queryFixedIncomeTrade(kcbpAccount);
                            queryTradeApis.get(index).queryProtocolBack_SZ_Trade(kcbpAccount);
                            queryTradeApis.get(index).queryProtocolBack_SH_Trade(kcbpAccount);
                            queryTradeApis.get(index).queryRTGSStatus(kcbpAccount);

                            //新债改造-深交所 协商成交
                            queryTradeApis.get(index).queryBondNegotiate_SZ_Entrust(kcbpAccount);
                            queryTradeApis.get(index).queryBondNegotiate_SZ_Trade(kcbpAccount);
                        }
                    });
                }
            }
        }
    }

    public void setKcbpTradeResponse(TradeResponseImpl kcbpTradeResponse) {
        this.kcbpTradeResponse = kcbpTradeResponse;
    }

    public void setKcbpRegisterConfig(KCBPRegisterConfig kcbpRegisterConfig) {
        this.kcbpRegisterConfig = kcbpRegisterConfig;
    }

    public void setKcbpTradeCount(int kcbpTradeCount) {
        this.kcbpTradeCount = kcbpTradeCount;
    }

    @Override
    public void reqReissueEntrust(String accountId, Long requestId) {
        kcbpTradeApi.reqReissueEntrust(accountId, requestId);
    }

    @Override
    public void reqReissueTrade(String accountId, Long requestId) {
        kcbpTradeApi.reqReissueTrade(accountId, requestId);
    }

    @Override
    public int getStatus(String accountId) {
        return kcbpTradeApi.getStatus(accountId);
    }

    @Override
    public void setTradeStatus(String accountId, AccountLoginStatus status) {
        kcbpTradeApi.setStatus(accountId, status);
    }

    public void setEntrustIdManager(EntrustIdManager entrustIdManager) {
        this.entrustIdManager = entrustIdManager;
    }

    public void querySystemStatus() {
        if (kcbpAccounts.size() > 0) {
            for (Map.Entry<String, KCBPAccount> entry : kcbpAccounts.entrySet()) {
                kcbpTradeApi.querySystemStatus(entry.getValue());
            }
        }
    }

	@Override
	public void queryNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean, Long requestId) {
		kcbpTradeApi.queryNegotiateQuoteReceive(negotiateQuoteQueryBean, requestId);
	}
}
