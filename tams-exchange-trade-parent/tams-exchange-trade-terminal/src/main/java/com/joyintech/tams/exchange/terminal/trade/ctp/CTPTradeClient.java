package com.joyintech.tams.exchange.terminal.trade.ctp;

import com.google.common.collect.Maps;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.terminal.agent.ITerminalMsgNotify;
import com.joyintech.tams.exchange.terminal.agent.TerminalAgent;
import com.joyintech.tams.exchange.terminal.constant.AccountLoginStatus;
import com.joyintech.tams.exchange.terminal.trade.EntrustIdManager;
import com.joyintech.tams.exchange.terminal.trade.IFuturesTradeRequest;
import com.joyintech.tams.exchange.terminal.trade.TradeResponseImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("ctp")
@ConditionalOnProperty(value = "ctp.active", matchIfMissing = false, havingValue = "true")
public class CTPTradeClient implements IFuturesTradeRequest {
    private static final Logger logger = LoggerFactory.getLogger(CTPTradeClient.class);
    @Autowired
    private EntrustIdManager entrustIdManager;
    private TerminalAgent agent;

    private Map<String, CTPTradeClientImpl> counterClientMap = Maps.newConcurrentMap();

    private Map<String, FuturesInstrument> instrumentMap = Maps.newConcurrentMap();

    private ITerminalMsgNotify iTerminalMsgNotify;

    public void addInstruments(List<FuturesInstrument> futuresInstruments) {
        futuresInstruments.forEach(futuresInstrument -> {
                    instrumentMap.put(futuresInstrument.getInnerCode(), futuresInstrument);
                }
        );
    }

    public Map<String, FuturesInstrument> getInstrumentMap() {
        return instrumentMap;
    }

    private void syncFillInstrumentMap(CTPTradeClientImpl client) {
        List<FuturesInstrument> instruments = client.getCtpTradeApi().syncQryInstrument(" ", null);
        addInstruments(instruments);
    }

    @Override
    public Set<String> getLoginAccount() {
        Set<String> res = new HashSet<>();
        counterClientMap.values().forEach(c -> res.addAll(c.getLoginAccount()));
        return res;
    }

    @Autowired
    private TradeResponseImpl ctpTradeResponse;

    @Override
    public void setITerminalMsgNotify(ITerminalMsgNotify iTerminalMsgNotify) {
        this.iTerminalMsgNotify = iTerminalMsgNotify;
        counterClientMap.values().forEach(c -> c.setITerminalMsgNotify(iTerminalMsgNotify));
    }

    @Override
    public void reqLogin(Account account, ServerExtendInfo serverExtendInfo) {
        logger.info("req login, account = {}", account.getId());
        account.setBrokerId(serverExtendInfo.getBrockerId());
        account.setBrokerName(serverExtendInfo.getBrockerName());

        CTPTradeClientImpl client = counterClientMap.getOrDefault(account.getId(), null);
        if (client == null) {
            client = new CTPTradeClientImpl();
            client.setCtpTradeClient(this);
            client.setEntrustIdManager(entrustIdManager);
            client.setCtpTradeResponse(ctpTradeResponse);

            ResultBean resultBean = client.initTradeApi(this.agent, serverExtendInfo, this.agent);
            if (ErrorCode.SUCCESS.value == resultBean.getErrorCode()) {
                client.reqLogin(account, serverExtendInfo);
            } else {
                ctpTradeResponse.resLogin(resultBean, account.getId());
            }
            counterClientMap.put(account.getId(), client);
        } else {
            if (!client.isConnectionFlag()) {
                ResultBean resultBean = client.initTradeApi(this.agent, serverExtendInfo, this.agent);
                if (ErrorCode.SUCCESS.value != resultBean.getErrorCode()) {
                    ctpTradeResponse.resLogin(resultBean, account.getId());
                }
            } else {
                if (getStatus(account.getId()) == AccountLoginStatus.NOT_LOGGED.value) {
                    client.reqLogin(account, serverExtendInfo);
                } else {
                    ctpTradeResponse.resLogin(new ResultBean(ErrorCode.SUCCESS, account), account.getId());
                }
            }
        }
    }

    @Override
    public void reqEntrusts(String accountId, Long requestId) {

    }

    @Override
    public void init(TerminalAgent agent, ServerExtendInfo serverExtendInfo, ITerminalMsgNotify iTerminalMsgNotify) {
        logger.info("trade client init: extendInfo = {}", serverExtendInfo);
        this.agent = agent;
        this.iTerminalMsgNotify = iTerminalMsgNotify;
    }

    @Override
    public void reqTrades(String accountId, Long requestId) {

    }

    @Override
    public void entrust(Entrust entrust, Long requestId) {
        CTPTradeClientImpl accountClient = counterClientMap.getOrDefault(entrust.getAccountId(), null);
        if (accountClient != null) {
            accountClient.entrust(entrust, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", entrust.getAccountId());
        }
    }

    @Override
    public void integratedEntrust(IntegratedEntrust integratedEntrust, Long requestId) {

    }

    @Override
    public void cancelEntrust(CancelEntrustBean cancelEntrustBean, Long requestId) {
        CTPTradeClientImpl accountClient = counterClientMap.getOrDefault(cancelEntrustBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.cancelEntrust(cancelEntrustBean, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", cancelEntrustBean.getAccountId());
        }
    }

    @Override
    public void cancelIntegratedEntrust(CancelEntrustBean cancelEntrustBean, Long requestId) {

    }

    @Override
    public void queryIntegratedQuotes(QuoteQryBean quoteQryBean, Long requestId) {

    }

    @Override
    public void queryEnable(EnableQryBean qryBean, Long requestId) {

    }

    @Override
    public void queryHistoryTrade(HistoryQryBean historyQryBean, Long requestId) {

    }

    @Override
    public void queryCustomerFinance(String accountId, Long requestId) {

    }

    @Override
    public void queryAccountPosition(PositionQueryBean positionQueryBean, Long requestId) {

    }

    @Override
    public void queryPledgeDiscount(String accountId, String market, String stockCode, Long requestId) {

    }

    @Override
    public void queryStandardBonds(StandardBondQryBean standardBondQryBean, Long requestId) {

    }

    @Override
    public void queryInstrumentInfo(InstrumentQryBean instrumentQryBean, Long requestId) {

    }

    @Override
    public void queryNextTrade() {

    }

    @Override
    public void queryIntegratedBulkQuote(IntegratedBulkQuoteQryBean integratedBulkQuoteQryBean, Long requestId) {

    }

    @Override
    public void queryIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean, Long requestId) {

    }

    @Override
    public void queryIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean, Long requestId) {

    }

    @Override
    public void queryIntegratedFixedIncomeQuote(IntegratedFixedIncomeQuoteQryBean integratedFixedIncomeQuoteQryBean, Long requestId) {

    }

    @Override
    public void reqReissueEntrust(String accountId, Long requestId) {
        CTPTradeClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            accountClient.reqReissueEntrust(accountId, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
    }

    @Override
    public void reqReissueTrade(String accountId, Long requestId) {
        CTPTradeClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            accountClient.reqReissueTrade(accountId, requestId);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
    }

    @Override
    public int getStatus(String accountId) {
        int res = AccountLoginStatus.NOT_LOGGED.value;
        CTPTradeClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            res = accountClient.getStatus(accountId);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
        return res;
    }

    @Override
    public void setTradeStatus(String accountId, AccountLoginStatus status) {
        CTPTradeClientImpl accountClient = counterClientMap.getOrDefault(accountId, null);
        if (accountClient != null) {
            accountClient.setTradeStatus(accountId, status);
        } else {
            logger.warn("client not exist, accountId = {}", accountId);
        }
    }

    @Override
    public void queryFuturesInstrument(FuturesInstrumentQryBean futuresInstrumentQryBean, Long requestId) {
        logger.info("queryFuturesInstrument: {}", futuresInstrumentQryBean);
        CTPTradeClientImpl accountClient = counterClientMap.getOrDefault(futuresInstrumentQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryFuturesInstrument(futuresInstrumentQryBean, requestId);
            return;
        }
    }

    @Override
    public void queryFuturesPosition(FuturesPositionQryBean futuresPositionQryBean, Long requestId) {
        logger.info("queryFuturesPosition: {}", futuresPositionQryBean);
        CTPTradeClientImpl accountClient = counterClientMap.getOrDefault(futuresPositionQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryFuturesPosition(futuresPositionQryBean, requestId);
            return;
        }
    }

    @Override
    public void queryFuturesPositionDetail(FuturesPositionDetailQryBean futuresPositionDetailQryBean, Long requestId) {
        logger.info("queryFuturesPositionDetail: {}", futuresPositionDetailQryBean);
        CTPTradeClientImpl accountClient = counterClientMap.getOrDefault(futuresPositionDetailQryBean.getAccountId(), null);
        if (accountClient != null) {
            accountClient.queryFuturesPositionDetail(futuresPositionDetailQryBean, requestId);
            return;
        }
    }

    @Override
    public void queryFuturesInstrumentMarginRate(FuturesInstrumentMarginRateQryBean futuresInstrumentMarginRateQryBean, Long requestId) {
        logger.info("queryFuturesInstrumentMarginRate,accountId:{}", futuresInstrumentMarginRateQryBean.getAccountId());
        CTPTradeClientImpl accountClient = counterClientMap.getOrDefault(futuresInstrumentMarginRateQryBean.getAccountId(), null);
        if (accountClient != null) {
            List<FuturesInstrumentMarginRateBean> marginRateBeanList = new ArrayList<>();
            if (0 == instrumentMap.size()) {
                syncFillInstrumentMap(accountClient);
            }
            accountClient.queryAllFuturesInstrumentMarginRate(instrumentMap, futuresInstrumentMarginRateQryBean.getAccountId(), requestId);
        }
    }

    @Override
    public void queryFuturesInstrumentCommissionRate(FuturesInstrumentCommissionRateQryBean futuresInstrumentCommissionRateQryBean, Long requestId) {
        logger.info("queryFuturesInstrumentCommissionRate: {}", futuresInstrumentCommissionRateQryBean);
        CTPTradeClientImpl accountClient = counterClientMap.getOrDefault(futuresInstrumentCommissionRateQryBean.getAccountId(), null);
        if (accountClient != null) {

            return;
        }
    }

	@Override
	public void queryNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean, Long requestId) {
		// TODO Auto-generated method stub
		
	}
}
