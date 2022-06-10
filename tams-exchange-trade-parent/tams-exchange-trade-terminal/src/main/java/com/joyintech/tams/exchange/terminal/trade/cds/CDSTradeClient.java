package com.joyintech.tams.exchange.terminal.trade.cds;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.constant.EntrustStatus;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.TradeType;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt3;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import com.joyintech.tams.exchange.common.net.agent.Agent;
import com.joyintech.tams.exchange.common.utils.IntegratedEntrustUtils;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.common.utils.TradeUtils;
import com.joyintech.tams.exchange.terminal.agent.ITerminalMsgNotify;
import com.joyintech.tams.exchange.terminal.agent.TerminalAgent;
import com.joyintech.tams.exchange.terminal.constant.AccountLoginStatus;
import com.joyintech.tams.exchange.terminal.trade.ITradeRequest;
import com.joyintech.tams.exchange.terminal.trade.TradeResponseImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * create By MAXX on 2020/6/8
 */
@Component
public class CDSTradeClient implements ITradeRequest {
    private static final Logger logger = LoggerFactory.getLogger(CDSTradeClient.class);
    private static Map<String, EntrustInterface> entrustMap = Maps.newConcurrentMap();//可撤单的委托
    private static List<IntegratedEntrust> integratedEntrustQuoteList = new ArrayList<>();//可查询行情map
    private static List<Trade> tradeArray = Lists.newArrayList();//已成部成

    @Autowired
    private TradeResponseImpl mockTradeResponse;

    private AtomicInteger brokerTradeId = new AtomicInteger(0);
    private ITerminalMsgNotify iTerminalMsgNotify;

    @Override
    public Set<String> getLoginAccount() {
        return new HashSet<>();
    }

    @Override
    public void setITerminalMsgNotify(ITerminalMsgNotify iTerminalMsgNotify) {
        this.iTerminalMsgNotify = iTerminalMsgNotify;
    }

    @Override
    public void reqLogin(Account account, ServerExtendInfo serverExtendInfo) {
    }

    @Override
    public void reqEntrusts(String accountId, Long requestId) {
    }

    @Override
    public void init(TerminalAgent agent, ServerExtendInfo serverExtendInfo, ITerminalMsgNotify iTerminalMsgNotify) {
        this.iTerminalMsgNotify = iTerminalMsgNotify;
    }

    @Override
    public void reqTrades(String accountId, Long requestId) {
        mockTradeResponse.resQueryTrade(tradeArray, requestId);
    }

    @Override
    public void entrust(Entrust entrust, Long requestId) {

    }

    @Override
    public void integratedEntrust(IntegratedEntrust integratedEntrust, Long requestId) {
        if (StringUtil.equals(DictCodeListExt3.投管业务类型_信用保护凭证.getValue(), integratedEntrust.getBusiTypeTg())) {
            integratedEntrust.setBrokerEntrustId(integratedEntrust.getId().substring(2));
            mockTradeResponse.resIntegratedEntrust(integratedEntrust);
            integratedEntrustQuoteList.add(integratedEntrust);

//            integratedEntrust.setTradedVolume(integratedEntrust.getEntrustVolume());
//            integratedEntrust.setTradedPrice(integratedEntrust.getEntrustPrice());
//            integratedEntrust.setTradedBalance(integratedEntrust.getEntrustBalance());
            entrustMap.put(integratedEntrust.getId(), integratedEntrust);
            onIntegratedEntrust(integratedEntrust);

            Trade trade = getTrade(integratedEntrust, Calendar.getInstance(), TradeType.NORMAL.value);

            tradeArray.add(trade);
            onTrade(trade);
        } else {
            integratedEntrust.setErrorCode(ErrorCode.TERMINAL_RETURN_ERROR.value);
            integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
            integratedEntrust.setErrorMsg("非信用保护凭证交易，废单");
            mockTradeResponse.resIntegratedEntrust(integratedEntrust);
        }
    }

    private Trade getTrade(EntrustInterface entrust, Calendar now, int tradeType) {
        Trade trade = new Trade();
        trade.setAccountId(entrust.getAccountId());
        trade.setTradingDay(entrust.getTradingDay());
        trade.setMarket(entrust.getMarket());
        trade.setFinanceAccount(entrust.getFinanceAccount());
        trade.setEntrustBs(entrust.getEntrustBs());
        trade.setIsFullPrice(entrust.getIsFullPrice());
        trade.setTradedPrice(entrust.getEntrustPrice());
        trade.setTradedVolume(entrust.getEntrustVolume());
        trade.setTradedBalance(entrust.getEntrustBalance());
        trade.setBsFlag(entrust.getBsFlag());
        trade.setAssetType(entrust.getAssetType());
        trade.setTradeType(tradeType);
        trade.setBrokerEntrustId(entrust.getBrokerEntrustId());
        trade.setStockCode(StockCodeUtils.getStockCode(entrust.getStockCode()));
        trade.setStockName(entrust.getStockName());
        trade.setEntrustProp(entrust.getEntrustProp());
        trade.setTradeTime(now.get(Calendar.HOUR_OF_DAY) + "" + now.get(Calendar.MINUTE) + "" + now.get(Calendar.SECOND));
        trade.setBrokerTradeId(trade.getTradeTime() + brokerTradeId.incrementAndGet());
        trade.setLocalEntrustId(entrust.getLocalEntrustId());
        trade.setEntrustId(entrust.getId());
        trade.setPs(entrust.getPs());
        return trade;
    }

    public void onTrade(Trade trade) {
        if (Strings.isNullOrEmpty(trade.getBrokerTradeId())) {
            trade.setBrokerTradeId(trade.getEntrustId());
        }
        iTerminalMsgNotify.onTrade(trade);
    }

    public void onIntegratedEntrust(IntegratedEntrust integratedEntrust) {
        iTerminalMsgNotify.onIntegratedEntrust(integratedEntrust);
    }

    @Override
    public void cancelEntrust(CancelEntrustBean cancelEntrustBean, Long requestId) {

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

    }

    @Override
    public void reqReissueTrade(String accountId, Long requestId) {

    }

    @Override
    public int getStatus(String accountId) {
        return 0;
    }

    @Override
    public void setTradeStatus(String accountId, AccountLoginStatus status) {

    }

    @Override
    public void queryNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean, Long requestId) {
    }
}
