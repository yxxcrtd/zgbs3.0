package com.joyintech.tams.exchange.service.trade.scheduled.quote;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.joyintech.tams.exchange.common.bean.IntegratedRepurchaseQuoteBean;
import com.joyintech.tams.exchange.common.bean.response.quote.repurchase.RepurchaseQuoteReceiveResponse;
import com.joyintech.tams.exchange.common.constant.ActionIn;
import com.joyintech.tams.exchange.common.db.domain.Account;
import com.joyintech.tams.exchange.service.trade.processor.others.TerminalHelper;
import com.joyintech.tams.exchange.service.trade.server.agent.AccountManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author: think
 * @Description: 对于投管行情查询，需要查询到全量的行情信息，单柜台接口部分未同步接口，可能存在请求频率过高而阻塞的问题，故采用定时任务缓存行情信息
 * @Date: Created in 14:38 2021/3/9
 */
@Component
public class QuoteJob {
    /**
     * 日志对象
     */
    private static final Logger logger = LoggerFactory.getLogger(QuoteJob.class);

    @Autowired
    private AccountManager accountManager;
    @Autowired
    private TerminalHelper terminalHelper;

    private Map<Integer, List<IntegratedRepurchaseQuoteBean>> quoteMap = new ConcurrentHashMap<>();

    private List<IntegratedRepurchaseQuoteBean> integratedRepurchaseQuoteForMock = new ArrayList<>();// 手动录入行情信息保存

    private List<RepurchaseQuoteReceiveResponse> receiveQuoteList = Lists.newArrayList();// 查询的我收到的协议回购行情信息
    private List<RepurchaseQuoteReceiveResponse> receiveQuoteForMock = Lists.newArrayList();// 手动录入我收到的协议回购行情信息保存

    @PostConstruct
    public void clearQuoteMap() {
        quoteMap.clear();
        integratedRepurchaseQuoteForMock.clear();

        receiveQuoteList.clear();
        receiveQuoteForMock.clear();
    }

    // region 缓存公开、非公开、我收到的行情
    @Scheduled(fixedRateString = "${scheduled.quote.fixedRate:120000}")
    private void cacheIntegratedQuote() {
        cachePublicQuote();
        cacheNonPublicQuote();
        cacheReceiveQuote();
    }

    private void cachePublicQuote() {
        List<Account> accounts = accountManager.getAccounts();
        if (CollUtil.isEmpty(accounts)) {
            logger.info("无已登录资金账户信息，不能查询行情");
            return;
        }
        List<IntegratedRepurchaseQuoteBean> integratedRepurchaseQuoteBeanListPublic = queryRepurchaseQuote(accounts.iterator().next(), ActionIn.PUBLIC_QUOTE.value);
        quoteMap.put(ActionIn.PUBLIC_QUOTE.value, integratedRepurchaseQuoteBeanListPublic);
    }

    private void cacheNonPublicQuote() {
        Map<String, Account> brokerAccountMap = accountManager.getBrokerAccountMap();
        if (CollUtil.isEmpty(brokerAccountMap)) {
            logger.info("无已登录资金账户信息，不能查询行情");
            return;
        }
        List<IntegratedRepurchaseQuoteBean> integratedRepurchaseQuoteBeanList = new ArrayList<>();
        brokerAccountMap.forEach((brokerId, account) -> {
            logger.debug("查询协议回购非公开报价行情 brokerId {}  brokerName {} accountId {}", brokerId, account.getBrokerName(), account.getId());
            List<IntegratedRepurchaseQuoteBean> integratedRepurchaseQuoteBeanBrokerList = queryRepurchaseQuote(account, ActionIn.NON_PUBLIC_QUOTE.value);
            integratedRepurchaseQuoteBeanList.addAll(integratedRepurchaseQuoteBeanBrokerList);
        });
        // mock数据添加到非公开行情
        integratedRepurchaseQuoteBeanList.addAll(integratedRepurchaseQuoteForMock);
        quoteMap.put(ActionIn.NON_PUBLIC_QUOTE.value, integratedRepurchaseQuoteBeanList);
    }

    private void cacheReceiveQuote() {
        Map<String, Account> brokerAccountMap = accountManager.getBrokerAccountMap();
        if (CollUtil.isEmpty(brokerAccountMap)) {
            logger.info("无已登录资金账户信息，不能查询行情");
            return;
        }
        List<RepurchaseQuoteReceiveResponse> receiveQuotes = ListUtil.list(false);
        brokerAccountMap.forEach((brokerId, account) -> {
            logger.debug("查询协议回购非公开报价行情 brokerId {}  brokerName {} accountId {}", brokerId, account.getBrokerName(), account.getId());
            List<RepurchaseQuoteReceiveResponse> list = queryRepurchaseQuoteReceive(account);
            receiveQuotes.addAll(list);
        });
        // mock数据添加到非公开行情
        receiveQuotes.addAll(receiveQuoteForMock);
    }
    private List<IntegratedRepurchaseQuoteBean> queryRepurchaseQuote(Account account, int actionIn) {
        logger.debug("查询协议回购{}公开报价行情 ==> 【accountId = {}】", actionIn == 0 ? StrUtil.EMPTY : "非", account.getId());

        List<IntegratedRepurchaseQuoteBean> repurchaseQuotes = terminalHelper.sendIntegratedRepurchaseQuote(actionIn, null, account.getId(), 1000);
        repurchaseQuotes.forEach(item -> {
            item.setBrokerId(account.getBrokerId());
            item.setBrokerName(account.getBrokerName());
        });
        return repurchaseQuotes;
    }

    private List<RepurchaseQuoteReceiveResponse> queryRepurchaseQuoteReceive(Account account) {
        logger.debug("查询我收到的协议回购报价行情 ==> 【accountId = {}】", account.getId());

        List<RepurchaseQuoteReceiveResponse> list = terminalHelper.sendRepurchaseQuoteReceive( null, account.getId(), 1000);
        list.forEach(item -> {
            item.setBrokerId(account.getBrokerId());
            item.setBrokerName(account.getBrokerName());
        });
        return list;
    }

// endregion

    public List<IntegratedRepurchaseQuoteBean> getIntegratedRepurchaseQuoteBeanList(int actionIn, String entrustProp) {
        List<IntegratedRepurchaseQuoteBean> integratedRepurchaseQuoteBeanList = quoteMap.getOrDefault(actionIn, new ArrayList<>());
        if (!Strings.isNullOrEmpty(entrustProp)) {
            return integratedRepurchaseQuoteBeanList.stream()
                    .filter(integratedRepurchaseQuoteBean -> entrustProp.equals(integratedRepurchaseQuoteBean.getEntrustProp()))
                    .collect(Collectors.toList());
        }
        logger.info("协议回购行情信息查询成功，actionIn {} entrustProp {} integratedRepurchaseQuoteBeanList.size {}", actionIn, entrustProp, integratedRepurchaseQuoteBeanList.size());
        return integratedRepurchaseQuoteBeanList;
    }

    public void addIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteBean repurchaseQuoteBean) {
        integratedRepurchaseQuoteForMock.add(repurchaseQuoteBean);
        List<IntegratedRepurchaseQuoteBean> integratedRepurchaseQuoteBeanList = quoteMap.getOrDefault(ActionIn.NON_PUBLIC_QUOTE.value, new ArrayList<>());
        integratedRepurchaseQuoteBeanList.add(repurchaseQuoteBean);
        quoteMap.put(ActionIn.NON_PUBLIC_QUOTE.value, integratedRepurchaseQuoteBeanList);
    }

    public List<RepurchaseQuoteReceiveResponse> getRepurchaseQuoteReceiveList(String entrustProp) {

        if (StrUtil.isNotEmpty(entrustProp)) {
            logger.info("我收到的协议回购行情信息查询成功 => 【entrustProp = {}】", entrustProp);
            return receiveQuoteList.stream()
                    .filter(item -> entrustProp.equals(item.getRequestType()))
                    .collect(Collectors.toList());
        }

        logger.info("我收到的协议回购行情信息查询成功 => 【size = {}】", receiveQuoteList.size());
        return receiveQuoteList;
    }

    public void addIntegratedRepurchaseQuoteReceive(RepurchaseQuoteReceiveResponse bean) {
        receiveQuoteForMock.add(bean);
        receiveQuoteList.add(bean);
    }
}
