package com.joyintech.tams.exchange.service.trade.server.agent.jsd;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.joyintech.tams.exchange.common.bean.AccountFinance;
import com.joyintech.tams.exchange.common.bean.HistoryQryBean;
import com.joyintech.tams.exchange.common.bean.HistoryTrade;
import com.joyintech.tams.exchange.common.bean.IntegratedBulkCloseQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedBulkQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedQuote;
import com.joyintech.tams.exchange.common.bean.QuoteQryBean;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.StandardBonds;
import com.joyintech.tams.exchange.common.db.domain.Account;
import com.joyintech.tams.exchange.common.db.domain.Entrust;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.db.domain.PositionBean;
import com.joyintech.tams.exchange.common.db.domain.Trade;
import com.joyintech.tams.exchange.service.trade.server.agent.EntrustIdManager;
import com.joyintech.tams.exchange.service.trade.server.agent.jsd.resp.data.RespQryAfterIntegratedQuotes;
import com.joyintech.tams.exchange.service.trade.server.agent.jsd.resp.data.RespQryEntrustData;
import com.joyintech.tams.exchange.service.trade.server.agent.jsd.resp.data.RespQryHistoryTrade;
import com.joyintech.tams.exchange.service.trade.server.agent.jsd.resp.data.RespQryIntegratedOrder;
import com.joyintech.tams.exchange.service.trade.server.agent.jsd.resp.data.RespQryIntegratedQuotes;
import com.joyintech.tams.exchange.service.trade.server.agent.jsd.resp.data.RespQryPositionData;
import com.joyintech.tams.exchange.service.trade.server.agent.jsd.resp.data.RespQryProtocolBackTrade;
import com.joyintech.tams.exchange.service.trade.server.agent.jsd.resp.data.RespQryStandardBondData;
import com.joyintech.tams.exchange.service.trade.server.agent.jsd.resp.data.RespQryTradeData;
import com.joyintech.tams.exchange.service.trade.server.agent.jsd.resp.header.RespQryCustomerFinanceHeader;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author lifangpeng
 * @date 2021/11/9
 */
public class JsdMsgParseHelper {
    private static final Splitter SPLITTER = Splitter.on("|");


    public static List<Entrust> parseEntrusts(ResultBean resultBean, Account account, EntrustIdManager entrustIdManager) {
        List<String> dataList = (List<String>) resultBean.getData();
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }

        List<Entrust> entrustList = Lists.newArrayListWithCapacity(dataList.size());
        dataList.forEach(data -> {
            List<String> messageContents = Lists.newArrayList(SPLITTER.split(data));
            RespQryEntrustData dataBody = MessageManager.getObjectFromStrings(messageContents, RespQryEntrustData.class);
            if (null != dataBody) {
                Entrust entrust = JSDConverter.getEntrust(account, dataBody);

                entrust.setId(entrustIdManager.getIdByBrokeEntrustId(entrust.getAccountId(), entrust.getBrokerEntrustId()));
                entrustList.add(entrust);
            }
        });

        return entrustList;
    }

    public static List<Trade> parseTrades(ResultBean resultBean, Account account) {
        List<String> dataList = (List<String>) resultBean.getData();
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }

        List<Trade> tradeList = Lists.newArrayListWithCapacity(dataList.size());
        dataList.forEach(data -> {
            List<String> messageContents = Lists.newArrayList(SPLITTER.split(data));
            RespQryTradeData dataBody = MessageManager.getObjectFromStrings(messageContents, RespQryTradeData.class);
            if (null != dataBody) {
                tradeList.add(JSDConverter.getTrade(account, dataBody));
            }
        });

        return tradeList;
    }

    public static List<IntegratedQuote> parseIntegratedQuotes(ResultBean resultBean, QuoteQryBean quoteQryBean) {
        List<String> dataList = (List<String>) resultBean.getData();
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }

        List<IntegratedQuote> quoteList = Lists.newArrayListWithCapacity(dataList.size());
        dataList.forEach(data -> {
            List<String> messageContents = Lists.newArrayList(SPLITTER.split(data));
            RespQryIntegratedQuotes dataBody = MessageManager.getObjectFromStrings(messageContents, RespQryIntegratedQuotes.class);
            if (null != dataBody) {
                quoteList.add(JSDConverter.getIntegratedQuote(quoteQryBean, dataBody));
            }
        });

        return quoteList;
    }

    public static List<IntegratedQuote> parseAfterIntegratedQuotes(ResultBean resultBean, QuoteQryBean quoteQryBean) {
        List<String> dataList = (List<String>) resultBean.getData();
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }

        List<IntegratedQuote> quoteList = Lists.newArrayListWithCapacity(dataList.size());
        dataList.forEach(data -> {
            List<String> messageContents = Lists.newArrayList(SPLITTER.split(data));
            RespQryAfterIntegratedQuotes dataBody = MessageManager.getObjectFromStrings(messageContents, RespQryAfterIntegratedQuotes.class);
            if (null != dataBody) {
                quoteList.add(JSDConverter.getAfterIntegratedQuote(quoteQryBean, dataBody));
            }
        });
        return quoteList;
    }


    public static List<HistoryTrade> parseHistoryTrade(Account account, ResultBean resultBean, HistoryQryBean historyQryBean) {
        List<String> dataList = (List<String>) resultBean.getData();
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }

        List<HistoryTrade> tradeList = Lists.newArrayListWithCapacity(dataList.size());
        dataList.forEach(data -> {
            List<String> messageContents = Lists.newArrayList(SPLITTER.split(data));
            RespQryHistoryTrade dataBody = MessageManager.getObjectFromStrings(messageContents, RespQryHistoryTrade.class);
            if (null != dataBody) {
                tradeList.add(JSDConverter.getHistoryTrade(account, historyQryBean, dataBody));
            }
        });

        return tradeList;
    }

    public static List<AccountFinance> parseCustomerFinance(ResultBean resultBean, String accountId) {
        RespQryCustomerFinanceHeader financeHeader = (RespQryCustomerFinanceHeader) resultBean.getData();
        if (Objects.isNull(financeHeader)) {
            return Collections.emptyList();
        }
        List<AccountFinance> financeList = Lists.newArrayList();
        financeList.add(JSDConverter.getCustomerFinance(accountId, financeHeader));

        return financeList;
    }

    public static List<PositionBean> parseAccountPosition(ResultBean resultBean, Account account) {
        List<String> dataList = (List<String>) resultBean.getData();
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }

        List<PositionBean> positionBeanList = Lists.newArrayListWithCapacity(dataList.size());
        dataList.forEach(data -> {
            List<String> messageContents = Lists.newArrayList(SPLITTER.split(data));
            RespQryPositionData dataBody = MessageManager.getObjectFromStrings(messageContents, RespQryPositionData.class);
            if (null != dataBody) {
                positionBeanList.add(JSDConverter.getAccountPosition(account, dataBody));
            }
        });

        return positionBeanList;
    }

    public static List<StandardBonds> parseStandardBonds(ResultBean resultBean, String accountId) {
        List<String> dataList = (List<String>) resultBean.getData();
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }

        List<StandardBonds> standardBondsList = Lists.newArrayListWithCapacity(dataList.size());
        dataList.forEach(data -> {
            List<String> messageContents = Lists.newArrayList(SPLITTER.split(data));
            RespQryStandardBondData dataBody = MessageManager.getObjectFromStrings(messageContents, RespQryStandardBondData.class);
            if (null != dataBody) {
                standardBondsList.add(JSDConverter.getStandardBond(accountId, dataBody));
            }
        });

        return standardBondsList;
    }


    public static List<IntegratedBulkQuoteBean> parseIntegratedBulkQuote(ResultBean resultBean) {
        List<String> dataList = (List<String>) resultBean.getData();
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }

        List<IntegratedBulkQuoteBean> bulkQuoteBeanList = Lists.newArrayListWithCapacity(dataList.size());
        dataList.forEach(data -> {
            List<String> messageContents = Lists.newArrayList(SPLITTER.split(data));
            RespQryIntegratedQuotes dataBody = MessageManager.getObjectFromStrings(messageContents, RespQryIntegratedQuotes.class);
            if (null != dataBody) {
                bulkQuoteBeanList.add(JSDConverter.getIntegratedBulkQuote(dataBody));
            }
        });

        return bulkQuoteBeanList;
    }

    public static List<IntegratedBulkCloseQuoteBean> parseIntegratedBulkCloseQuote(ResultBean resultBean) {
        List<String> dataList = (List<String>) resultBean.getData();
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }

        List<IntegratedBulkCloseQuoteBean> bulkCloseQuoteBeanList = Lists.newArrayListWithCapacity(dataList.size());
        dataList.forEach(data -> {
            List<String> messageContents = Lists.newArrayList(SPLITTER.split(data));
            RespQryAfterIntegratedQuotes dataBody = MessageManager.getObjectFromStrings(messageContents, RespQryAfterIntegratedQuotes.class);
            if (null != dataBody) {
                bulkCloseQuoteBeanList.add(JSDConverter.getIntegratedBulkCloseQuote(dataBody));
            }
        });

        return bulkCloseQuoteBeanList;
    }

    public static List<IntegratedEntrust> parseIntegratedEntrusts(ResultBean resultBean, Account account) {
        List<String> dataList = (List<String>) resultBean.getData();
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }

        List<IntegratedEntrust> entrustList = Lists.newArrayListWithCapacity(dataList.size());
        dataList.forEach(data -> {
            List<String> messageContents = Lists.newArrayList(SPLITTER.split(data));
            RespQryIntegratedOrder dataBody = MessageManager.getObjectFromStrings(messageContents, RespQryIntegratedOrder.class);
            if (null != dataBody) {
                entrustList.add(JSDConverter.getIntegratedEntrust(account, dataBody));
            }
        });

        return entrustList;
    }

    public static List<Trade> parseProtocolBackTrades(ResultBean resultBean, Account account) {
        List<String> dataList = (List<String>) resultBean.getData();
        if (CollectionUtils.isEmpty(dataList)) {
            return Collections.emptyList();
        }

        List<Trade> tradeList = Lists.newArrayListWithCapacity(dataList.size());
        dataList.forEach(data -> {
            List<String> messageContents = Lists.newArrayList(SPLITTER.split(data));
            RespQryProtocolBackTrade dataBody = MessageManager.getObjectFromStrings(messageContents, RespQryProtocolBackTrade.class);
            if (null != dataBody) {
                tradeList.add(JSDConverter.getProtocolBackTrade(account, dataBody));
            }
        });

        return tradeList;
    }
}
