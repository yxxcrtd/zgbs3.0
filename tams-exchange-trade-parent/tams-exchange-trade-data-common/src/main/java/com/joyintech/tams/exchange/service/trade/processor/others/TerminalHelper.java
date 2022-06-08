package com.joyintech.tams.exchange.service.trade.processor.others;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Strings;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondBean;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.bean.investment.CapitalAccountBean;
import com.joyintech.tams.exchange.common.bean.request.RepurchaseQuoteReceiveQryRequest;
import com.joyintech.tams.exchange.common.bean.request.TradeInfoProRequest;
import com.joyintech.tams.exchange.common.bean.response.TradeInfoProResponse;
import com.joyintech.tams.exchange.common.bean.response.quote.repurchase.RepurchaseQuoteReceiveResponse;
import com.joyintech.tams.exchange.common.constant.AgentType;
import com.joyintech.tams.exchange.common.constant.EntrustSide;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.constant.Market;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.utils.ConvertListUtils;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.datacommon.db.service.*;
import com.joyintech.tams.exchange.service.trade.management.service.CommonManagement;
import com.joyintech.tams.exchange.service.trade.management.service.InvestmentManagement;
import com.joyintech.tams.exchange.service.trade.scheduled.LoginJob;
import com.joyintech.tams.exchange.service.trade.scheduled.quote.QuoteJob;
import com.joyintech.tams.exchange.service.trade.server.agent.AccountManager;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgent;
import com.joyintech.tams.exchange.service.trade.server.agent.TradeAgentManager;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class TerminalHelper {
    private static final Logger logger = LoggerFactory.getLogger(TerminalHelper.class);

    @Autowired
    private TradeAgentManager tradeAgentManager;
    @Autowired
    private InstrumentService instrumentService;
    @Autowired
    private EntrustService entrustService;
    @Autowired
    private IntegratedEntrustService integratedEntrustService;
    @Autowired
    private TradeService tradeService;
    @Autowired
    private AccountManager accountManager;
    @Autowired
    private InvestmentManagement investmentManagement;
    @Autowired
    private LoginJob loginJob;
    @Autowired
    private QuoteJob quoteJob;
    @Autowired
    private TerminalMappingService terminalMappingService;
    @Autowired
    private CommonManagement commonManagement;

    private final int batchSize = 1000; // 委托成交比对时，分页发送数据大小

    /**
     * 账户手动连接
     *
     * @param accountId     账户编号
     * @param connectStatus 连接状态选择 1: 请求登录  0：退出账户
     * @return ResultBean
     */
    public ResultBean connectAccount(String accountId, int connectStatus) {
        List<CapitalAccountBean> capitalAccountBeans = investmentManagement.queryCapitalAccountByIdOrAgentType(accountId, null);
        if (Objects.isNull(capitalAccountBeans) || capitalAccountBeans.size() == 0) {
            return new ResultBean(ErrorCode.ACCOUNT_NOT_EXIT);
        }
        TradeAgent tradeAgent = tradeAgentManager.findAgentByAccountId(accountId);
        if (Objects.nonNull(tradeAgent)) {
            if (connectStatus != 1) {
                Account account = accountManager.getAccountById(accountId);
                tradeAgent.reqLogoutAccount(account);
            }
            return new ResultBean(ErrorCode.SUCCESS);
        } else {
            if (connectStatus == 1) {
                return loginAccount(capitalAccountBeans.iterator().next());
            } else {
                return new ResultBean(ErrorCode.SUCCESS);
            }
        }
    }

    private ResultBean loginAccount(CapitalAccountBean capitalAccountBean) {
        Account account = capitalAccountBean.createAccount();
        if (Objects.isNull(account)) {
            return new ResultBean(ErrorCode.ACCOUNT_TRANSFORM_ERROR);
        }
        List<TerminalMapping> terminalMappingList = terminalMappingService.findAll();
        Map<String, TradeAgent> tradingAgents = tradeAgentManager.getTradingAgents();
        return loginJob.loginAccount(tradingAgents, terminalMappingList, account);
    }

    public List<Entrust> queryEntrustFromTerminal(String accountId) throws Exception {
        TradeAgent tradeAgent = tradeAgentManager.findAgentByAccountId(accountId);
        if (Objects.nonNull(tradeAgent)) {
            return tradeAgent.reqEntrusts(accountId);
        } else throw new Exception(ErrorCode.TERMINAL_NOT_ONLINE.errorMsg);
    }

    public List<Trade> queryTradeFromTerminal(String accountId) throws Exception {
        TradeAgent tradeAgent = tradeAgentManager.findAgentByAccountId(accountId);
        if (Objects.nonNull(tradeAgent)) {
            return tradeAgent.reqTrades(accountId);
        } else throw new Exception(ErrorCode.TERMINAL_NOT_ONLINE.errorMsg);
    }

    public List<IntegratedQuote> queryIntegratedQuote(int quoteType, String stockCode, String accountId, String oppositeLiaisonTel, String liaisonTel, int pageSize, String posStr) {
        QuoteQryBean quoteQryBean = new QuoteQryBean();
        // 投管没有账号入参，需要单独处理
        TradeAgent tradeAgent = null;
        if (!Strings.isNullOrEmpty(accountId)) {
            quoteQryBean.setAccountId(accountId);
            tradeAgent = tradeAgentManager.findAgentByAccountId(accountId);
        } else {
            Map<String, TradeAgent> accountAgents = tradeAgentManager.getAccountAgents();
            if (accountAgents.size() > 0) {
                String accountId1 = accountAgents.keySet().iterator().next();
                quoteQryBean.setAccountId(accountId1);
                tradeAgent = accountAgents.get(accountId1);
            } else {
                logger.warn("not found agent from accountAgents because no terminal login with trade !!!");
            }
        }
        if (!Strings.isNullOrEmpty(stockCode)) quoteQryBean.setStockCode(stockCode);
        if (!Strings.isNullOrEmpty(oppositeLiaisonTel)) quoteQryBean.setOppositeLiaisonTel(oppositeLiaisonTel);
        if (!Strings.isNullOrEmpty(liaisonTel)) quoteQryBean.setOppositeLiaisonTel(liaisonTel);
        if (pageSize > 0) {
            if (pageSize > 1000) pageSize = 1000;
            quoteQryBean.setPageSize(pageSize);
        }
        if (!Strings.isNullOrEmpty(posStr)) quoteQryBean.setPostStr(posStr);
        quoteQryBean.setQuoteType(quoteType);
        if (Objects.nonNull(tradeAgent)) {
            try {
                List<IntegratedQuote> integratedQuotes = tradeAgent.reqIntegratedQuotes(quoteQryBean);
                if (Objects.nonNull(integratedQuotes)) {
                    integratedQuotes.forEach(integratedQuote -> {
                        if (Strings.isNullOrEmpty(integratedQuote.getStockName())) {
                            integratedQuote.setStockCode(StockCodeUtils.formatStockCode(integratedQuote.getStockCode(), integratedQuote.getMarket()));
                            Instrument instrument = instrumentService.findById(integratedQuote.getStockCode());
                            if (Objects.nonNull(instrument)) {
                                integratedQuote.setStockName(instrument.getStockName());
                            } else {
                                logger.info("未找到匹配的合约代码信息！！ {}", integratedQuote.getStockCode());
                            }
                        }
                    });
                }
                return integratedQuotes;
            } catch (Exception e) {
                logger.error("queryIntegratedQuote error {}", "\r\n" + e);
                return null;
            }
        }
        return new ArrayList<>();
    }

    public int queryEnable(String stockCode, String entrustProp, String entrustBs, double entrustPrice, String accountId) {
        EnableQryBean qryBean = new EnableQryBean(stockCode, entrustProp, entrustBs, entrustPrice, accountId);
        TradeAgent tradeAgent = tradeAgentManager.findAgentByAccountId(accountId);
        if (Objects.nonNull(tradeAgent)) {
            try {
                return tradeAgent.reqEnable(qryBean);
            } catch (Exception e) {
                logger.error("queryEnable error {}", "\r\n" + e);
            }
        }
        return -1;
    }

    public List<HistoryTrade> queryHistoryTrade(String startDay, String endDay, String accountId, String postStr, int pageSize) {
        if (pageSize >= 1000) {
            pageSize = 1000;
        }
        HistoryQryBean historyQryBean = new HistoryQryBean(startDay, endDay, accountId, postStr, pageSize);
        TradeAgent tradeAgent = tradeAgentManager.findAgentByAccountId(accountId);
        if (Objects.nonNull(tradeAgent)) {
            try {
                return tradeAgent.reqHistoryTrade(historyQryBean);
            } catch (Exception e) {
                logger.error("queryHistoryTrade error {}", "\r\n" + e);
            }
        }
        return new ArrayList<>();
    }

    public ResultBean queryAccountDiscount(String accountId, String market, String stockCode) {
        TradeAgent agent = tradeAgentManager.findAgentByAccountId(accountId);
        if (Objects.isNull(agent)) {
            return new ResultBean(ErrorCode.TERMINAL_NOT_ONLINE);
        }
        try {
            List<PledgeDiscount> discountList = agent.reqPledgeDiscount(accountId, market, stockCode);
            return new ResultBean(ErrorCode.SUCCESS, discountList);
        } catch (Exception e) {
            logger.info("queryAccountDiscount exception :{}", e);
            return new ResultBean(ErrorCode.SYSTEM_ERROR);
        }
    }

    public ResultBean queryAccountStandardBonds(String accountId, String market, String stockCode) {
        TradeAgent agent = tradeAgentManager.findAgentByAccountId(accountId);
        if (Objects.isNull(agent)) {
            return new ResultBean(ErrorCode.TERMINAL_NOT_ONLINE);
        }
        try {
            List<StandardBonds> standardBonds = agent.reqStandardBonds(new StandardBondQryBean(accountId, market, stockCode));
            return new ResultBean(ErrorCode.SUCCESS, standardBonds);
        } catch (Exception e) {
            logger.info("查询标准券错误 :{}", e.getMessage());
            return new ResultBean(ErrorCode.QUERY_STANDARD_BOND_ERROR, null);
        }
    }


    public ResultBean queryAccountAvailableStandardBonds(String accountId, String market) {
        TradeAgent agent = tradeAgentManager.findAgentByAccountId(accountId);
        if (Objects.isNull(agent)) {
            return new ResultBean(ErrorCode.TERMINAL_NOT_ONLINE);
        }
        try {
            List<StandardBonds> standardBonds = agent.reqStandardBonds(new StandardBondQryBean(accountId, market, null));
            if (Objects.nonNull(standardBonds)) {
                StandardBondBean standardBondBean = new StandardBondBean();
                standardBonds.forEach(standardBond -> {
                    if (Market.SZSE.value.equals(standardBond.getMarket())) {
                        standardBondBean.setSzStandardBondTotal(standardBondBean.getSzStandardBondTotal() + standardBond.getBondEnable());
                    } else {
                        standardBondBean.setShStandardBondTotal(standardBondBean.getShStandardBondTotal() + standardBond.getBondEnable());
                    }
                });
                return new ResultBean(ErrorCode.SUCCESS, standardBondBean);
            }
            return new ResultBean(ErrorCode.SUCCESS, null);
        } catch (Exception e) {
            logger.info("查询标准券总数错误 :{}", e.getMessage());
            return new ResultBean(ErrorCode.QUERY_STANDARD_BOND_ERROR, null);
        }
    }

    public List<Instrument> queryInstrumentInfo(String accountId) {
        List<Instrument> instruments = new ArrayList<>();
        String postStr = "";
        int rowCount;
        try {
            TradeAgent agent = tradeAgentManager.findAgentByAccountId(accountId);
            if (Objects.nonNull(agent)) {
                String agentType = agent.getAccountMap().get(accountId).getAgentType();
                if (AgentType.UFX.value.equals(agentType)) rowCount = 1000;
                else rowCount = 100;
                List<Instrument> instrumentList;
                for (Market market : Market.values()) {
                    do {
                        instrumentList = agent.reqInstrumentInfo(new InstrumentQryBean(market.value, accountId, postStr, rowCount));
                        if (Objects.isNull(instrumentList) || instrumentList.size() == 0) {
                            break;
                        }
                        postStr = instrumentList.get(instrumentList.size() - 1).getPostStr();
                        instruments.addAll(instrumentList);
                    } while (instrumentList.size() == rowCount);
                }
            }
        } catch (Exception e) {
            logger.info("queryInstrumentInfo account error {} -{}", accountId, e);
            return null;
        }
        return instruments;
    }

    public List<IntegratedBulkQuoteBean> queryIntegratedBulkQuote(IntegratedBulkQuoteQryBean integratedBulkQuoteQryBean) {
        int pageSize = integratedBulkQuoteQryBean.getPageSize();
        if (pageSize > 1000) {
            integratedBulkQuoteQryBean.setPageSize(1000);
        }

        List<IntegratedBulkQuoteBean> list = new ArrayList<>();
        TradeAgent tradeAgent = getAgent(integratedBulkQuoteQryBean.getAccountId());
        if (Objects.nonNull(tradeAgent)) {
            try {
                List<IntegratedBulkQuoteBean> quoteBeanList = tradeAgent.reqIntegratedBulkQuote(integratedBulkQuoteQryBean);
                if (Objects.nonNull(quoteBeanList) && quoteBeanList.size() > 0) {
                    quoteBeanList.forEach(quoteBean -> {
                        Instrument instrument = instrumentService.findById(quoteBean.getStockCode());
                        if (Objects.nonNull(instrument)) {
                            quoteBean.setStockName(instrument.getStockName());
                            quoteBean.setAssetType(instrument.getAssetType());
                        } else {
                            logger.info("未找到匹配的合约代码信息！！ {}", quoteBean.getStockCode());
                        }
                    });
                    list.addAll(quoteBeanList);
                }
            } catch (Exception e) {
                logger.error("reqIntegratedBulkQuote error {}", "\r\n" + e);
            }
        }
        return list;
    }

    public List<IntegratedBulkCloseQuoteBean> queryIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean) {
        int pageSize = integratedBulkCloseQuoteQryBean.getPageSize();
        if (pageSize > 1000) {
            integratedBulkCloseQuoteQryBean.setPageSize(1000);
        }

        List<IntegratedBulkCloseQuoteBean> list = new ArrayList<>();
        TradeAgent tradeAgent = getAgent(integratedBulkCloseQuoteQryBean.getAccountId());
        if (Objects.nonNull(tradeAgent)) {
            try {
                List<IntegratedBulkCloseQuoteBean> beanList = tradeAgent.reqIntegratedBulkCloseQuote(integratedBulkCloseQuoteQryBean);
                if (Objects.nonNull(beanList) && beanList.size() > 0) {
                    beanList.forEach(quoteBean -> {
                        Instrument instrument = instrumentService.findById(quoteBean.getStockCode());
                        if (Objects.nonNull(instrument)) {
                            quoteBean.setStockName(instrument.getStockName());
                            quoteBean.setAssetType(instrument.getAssetType());
                        } else {
                            logger.info("未找到匹配的合约代码信息！！ {}", quoteBean.getStockCode());
                        }
                    });
                    list.addAll(beanList);
                }
            } catch (Exception e) {
                logger.error("reqIntegratedBulkCloseQuote error {}", "\r\n" + e);
            }
        }
        return list;
    }

    public List<IntegratedRepurchaseQuoteBean> queryIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean) {
        int pageSize = integratedRepurchaseQuoteQryBean.getPageSize();
        if (pageSize > 1000) {
            pageSize = 1000;
        }
        String accountId = integratedRepurchaseQuoteQryBean.getAccountId();
        int actionIn = integratedRepurchaseQuoteQryBean.getActionIn();
        String entrustProp = integratedRepurchaseQuoteQryBean.getEntrustProp();
        // 投管查询行情时，不会传入账户信息，需要查询到所有券商的行情信息， 故选择当前已连接券商，并选择其中一个账户进行查询
        if (Strings.isNullOrEmpty(accountId)) {
            return quoteJob.getIntegratedRepurchaseQuoteBeanList(actionIn, entrustProp);
        } else {
            return sendIntegratedRepurchaseQuote(actionIn, entrustProp, accountId, pageSize);
        }
    }

    public List<RepurchaseQuoteReceiveResponse> queryRepurchaseQuoteReceive(RepurchaseQuoteReceiveQryRequest request) {
        int pageSize = request.getPageSize();
        if (pageSize > 1000) {
            pageSize = 1000;
        }
        String accountId = request.getAccountId();
        String entrustProp = request.getEntrustProp();
        // 投管查询行情时，不会传入账户信息，需要查询到所有券商的行情信息， 故选择当前已连接券商，并选择其中一个账户进行查询
        if (StrUtil.isEmpty(accountId)) {
            return quoteJob.getRepurchaseQuoteReceiveList(entrustProp);
        }
        return sendRepurchaseQuoteReceive(entrustProp, accountId, pageSize);
    }

    @SuppressWarnings("all")
    public List<RepurchaseQuoteReceiveResponse> sendRepurchaseQuoteReceive(String entrustProp, String accountId, int pageSize) {

        List<RepurchaseQuoteReceiveResponse> list = ListUtil.list(false);

        TradeAgent tradeAgent = getAgent(accountId);
        if (ObjectUtil.isNull(tradeAgent)) {
            logger.warn("未获取到资金账号对应的终端 => 【account = {}】", accountId);
            return list;
        }

        try {
            RepurchaseQuoteReceiveQryRequest request = new RepurchaseQuoteReceiveQryRequest(entrustProp, accountId, pageSize);
            List<RepurchaseQuoteReceiveResponse> beanList = tradeAgent.reqIntegratedRepurchaseQuoteReceive(request);
            if (CollUtil.isNotEmpty(beanList)) {
                list.addAll(beanList);
            }
        } catch (Exception e) {
            logger.error("reqIntegratedRepurchaseQuote error {}", "\r\n" + e);
        }
        return list;
    }

    public List<IntegratedRepurchaseQuoteBean> sendIntegratedRepurchaseQuote(int actionIn, String entrustProp, String accountId, int pageSize) {

        List<IntegratedRepurchaseQuoteBean> list = new ArrayList<>();
        TradeAgent tradeAgent = getAgent(accountId);
        if (Objects.nonNull(tradeAgent)) {
            try {
                IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean = new IntegratedRepurchaseQuoteQryBean(actionIn, entrustProp, accountId, pageSize);
                List<IntegratedRepurchaseQuoteBean> beanList = tradeAgent.reqIntegratedRepurchaseQuote(integratedRepurchaseQuoteQryBean);
                if (Objects.nonNull(beanList) && beanList.size() > 0) {
                    list.addAll(beanList);
                }
            } catch (Exception e) {
                logger.error("reqIntegratedRepurchaseQuote error {}", "\r\n" + e);
            }
        }
        return list;
    }

    public List<IntegratedFixedIncomeQuoteBean> queryIntegratedFixedIncomeQuote(IntegratedFixedIncomeQuoteQryBean integratedFixedIncomeQuoteQryBean) {
        int pageSize = integratedFixedIncomeQuoteQryBean.getPageSize();
        if (pageSize > 1000) {
            integratedFixedIncomeQuoteQryBean.setPageSize(1000);
        }

        List<IntegratedFixedIncomeQuoteBean> list = new ArrayList<>();
        TradeAgent tradeAgent = getAgent(integratedFixedIncomeQuoteQryBean.getAccountId());
        if (Objects.nonNull(tradeAgent)) {
            try {
                List<IntegratedFixedIncomeQuoteBean> beanList = tradeAgent.reqIntegratedFixedIncomeQuote(integratedFixedIncomeQuoteQryBean);
                if (Objects.nonNull(beanList) && beanList.size() > 0) {
                    list.addAll(beanList);
                }
            } catch (Exception e) {
                logger.error("reqIntegratedFixedIncomeQuote error {}", "\r\n" + e);
            }
        }
        return list;
    }

    public List<FuturesInstrument> queryFuturesInstrument(String stockCode, String exchangeId, String accountId) {
        FuturesInstrumentQryBean futuresInstrumentQryBean = new FuturesInstrumentQryBean(accountId, stockCode);

        TradeAgent tradeAgent = null;
        if (!Strings.isNullOrEmpty(accountId)) {
            tradeAgent = tradeAgentManager.findAgentByAccountId(accountId);
            try {
                if (Objects.nonNull(tradeAgent)) {
                    return tradeAgent.reqFuturesInstrument(futuresInstrumentQryBean);
                } else {
                    logger.error("queryFuturesInstrument can not find tradeAgent,account:{}", accountId);
                }
            } catch (Exception e) {
                logger.error("queryFuturesInstrument by account:{} error {}", accountId, "\r\n" + e);
            }
        } else {
            //查找ctp终端
            List<Account> accountList = accountManager.getAccounts();
            for (Account acc : accountList) {
                if (AgentType.CTP.value.equals(acc.getAgentType())) {
                    TradeAgent agent = tradeAgentManager.findAgentByAccountId(acc.getId());
                    if (Objects.nonNull(agent)) {
                        futuresInstrumentQryBean.setAccountId(acc.getId());
                        try {
                            List<FuturesInstrument> resultList = agent.reqFuturesInstrument(futuresInstrumentQryBean);
                            if (Objects.nonNull(resultList)) {
                                return resultList;
                            } else {
                                logger.error("queryFuturesInstrument by account:{},Terminal return null list", accountId);
                            }
                        } catch (Exception e) {
                            logger.error("queryFuturesInstrument by account:{} error {}", accountId, "\r\n" + e);
                        }
                    } else {
                        logger.error("queryFuturesInstrument can not find tradeAgent,account:{}", accountId);
                    }
                }
            }
            logger.warn("not found ctp agent from Accounts because no ctp terminal login with trade !!!");
        }
        return new ArrayList<>();
    }

    private TradeAgent getAgent(String accountId) {
        TradeAgent tradeAgent = null;
        if (!Strings.isNullOrEmpty(accountId)) {
            tradeAgent = tradeAgentManager.findAgentByAccountId(accountId);
        } else {
            Map<String, TradeAgent> accountAgents = tradeAgentManager.getAccountAgents();
            if (accountAgents.size() > 0) {
                String key = accountAgents.keySet().iterator().next();
                tradeAgent = accountAgents.get(key);
            } else {
                logger.warn("not found agent from accountAgents because no terminal login with trade !!!");
            }
        }
        return tradeAgent;
    }

    public void reqReissueEntrust(String accountId) {
        // 为避免数据量过大， 采用分页发送的处理方法， 每次发送1000条数据
        ReissueEntrustQryBean entrustsForReissue = this.getEntrustsForReissue(accountId);
        List<ReissueEntrustBean> reissueEntrustBeanList = entrustsForReissue.getReissueEntrustBeans();
        int totalSize = reissueEntrustBeanList.size();
        int batchCount = totalSize / batchSize + (totalSize % batchSize == 0 ? 0 : 1);
        int pageNum = 0;
        int count = batchCount;
        do {
            pageNum += 1;
            List<ReissueEntrustBean> reissueEntrustBeanBatchList = ConvertListUtils.convertList(pageNum, batchSize, reissueEntrustBeanList);
            entrustsForReissue.setReissueEntrustBeans(reissueEntrustBeanBatchList);
            entrustsForReissue.setPageNum(pageNum);
            if (count == 0 || count == pageNum) {
                entrustsForReissue.setLast(true);
            }
            TradeAgent tradeAgent = tradeAgentManager.findAgentByAccountId(accountId);
            if (Objects.nonNull(tradeAgent)) {
                try {
                    logger.info("委托信息比对数 size {}, batchCount {}, pageNum {}", reissueEntrustBeanBatchList.size(), batchCount, pageNum);
                    tradeAgent.reqReissueEntrust(entrustsForReissue);
                } catch (Exception e) {
                    logger.error("reqReissueEntrust error {}", e.getMessage(), e);
                }
            } else {
                logger.error("not found agent {} from accountAgents  when  reqReissueEntrust!!!", accountId);
            }
            batchCount--;
        } while (batchCount > 0);
    }

    public void reqReissueTrade(String accountId) {
        ReissueTradeQryBean tradesForReissue = this.getTradesForReissue(accountId);
        List<ReissueTradeBean> reissueTradeBeanList = tradesForReissue.getReissueTradeBeans();
        int totalSize = reissueTradeBeanList.size();
        int batchCount = totalSize / batchSize + (totalSize % batchSize == 0 ? 0 : 1);
        int pageNum = 0;
        int count = batchCount;
        do {
            pageNum += 1;
            List<ReissueTradeBean> reissueTradeBeanBatchList = ConvertListUtils.convertList(pageNum, batchSize, reissueTradeBeanList);
            tradesForReissue.setReissueTradeBeans(reissueTradeBeanBatchList);
            tradesForReissue.setPageNum(pageNum);
            if (count == 0 || count == pageNum) {
                tradesForReissue.setLast(true);
            }
            TradeAgent tradeAgent = tradeAgentManager.findAgentByAccountId(accountId);
            if (Objects.nonNull(tradeAgent)) {
                try {
                    logger.info("成交信息比对数 size {}, batchCount {}, pageNum {}", reissueTradeBeanBatchList.size(), batchCount, pageNum);
                    tradeAgent.reqReissueTrade(tradesForReissue);
                } catch (Exception e) {
                    logger.error("reqReissueEntrust error {}", e.getMessage(), e);
                }
            } else {
                logger.error("not found agent {} from accountAgents  when  reqReissueEntrust!!!", accountId);
            }
            batchCount--;
        } while (batchCount > 0);
    }

    /**
     * @param accountId
     * @return List<ReissueTradeQryBean>
     * @author: liujianping
     * @description: 获取缓存中委托订单
     * @date: 2020/9/29 18:54
     */
    private ReissueEntrustQryBean getEntrustsForReissue(String accountId) {
        List<ReissueEntrustBean> reissueEntrustBeans = new ArrayList<>();
        List<Entrust> entrustList = entrustService.queryEntrustByAccountId(accountId);
        entrustList.forEach(entrust -> {
            ReissueEntrustBean reissueEntrustBean = new ReissueEntrustBean();
            reissueEntrustBean.setBulk(false);
            reissueEntrustBean.setBrokerEntrustId(entrust.getBrokerEntrustId());
            reissueEntrustBean.setId(entrust.getId());
            reissueEntrustBean.setLocalEntrustId(entrust.getLocalEntrustId());
            reissueEntrustBean.setEntrustProp(entrust.getEntrustProp());
            reissueEntrustBean.setEntrustStatus(entrust.getEntrustStatus());
            reissueEntrustBeans.add(reissueEntrustBean);
        });
        List<IntegratedEntrust> integratedEntrusts = integratedEntrustService.queryIntegratedEntrustByAccountId(accountId);
        integratedEntrusts.forEach(entrust -> {
            ReissueEntrustBean reissueEntrustBean = new ReissueEntrustBean();
            reissueEntrustBean.setBulk(true);
            reissueEntrustBean.setBrokerEntrustId(entrust.getBrokerEntrustId());
            reissueEntrustBean.setId(entrust.getId());
            reissueEntrustBean.setLocalEntrustId(entrust.getLocalEntrustId());
            reissueEntrustBean.setEntrustProp(entrust.getEntrustProp());
            reissueEntrustBean.setEntrustStatus(entrust.getEntrustStatus());
            reissueEntrustBeans.add(reissueEntrustBean);
        });
        ReissueEntrustQryBean reissueEntrustQryBean = new ReissueEntrustQryBean();
        reissueEntrustQryBean.setAccountId(accountId);
        reissueEntrustQryBean.setReissueEntrustBeans(reissueEntrustBeans);
        return reissueEntrustQryBean;
    }

    /**
     * @param accountId
     * @return List<ReissueTradeQryBean>
     * @author: liujianping
     * @description: 获取缓存中成交订单
     * @date: 2020/9/29 18:54
     */
    private ReissueTradeQryBean getTradesForReissue(String accountId) {
        List<ReissueTradeBean> reissueEntrustQryBeans = new ArrayList<ReissueTradeBean>();
        List<Trade> tradeList = tradeService.queryTradeByAccountId(accountId);
        tradeList.forEach(trade -> {
            ReissueTradeBean reissueTradeBean = new ReissueTradeBean();
            reissueTradeBean.setBrokerEntrustId(trade.getBrokerEntrustId());
            reissueTradeBean.setId(trade.getId());
            reissueTradeBean.setLocalEntrustId(trade.getLocalEntrustId());
            reissueTradeBean.setEntrustProp(trade.getEntrustProp());
            reissueTradeBean.setBrokerTradeId(trade.getBrokerTradeId());
            reissueEntrustQryBeans.add(reissueTradeBean);
        });
        ReissueTradeQryBean reissueTradeQryBean = new ReissueTradeQryBean();
        reissueTradeQryBean.setAccountId(accountId);
        reissueTradeQryBean.setReissueTradeBeans(reissueEntrustQryBeans);
        return reissueTradeQryBean;
    }

    public void queryFuturesInstrumentMarginRate(String accountId, String stockCode) {
        FuturesInstrumentMarginRateQryBean futuresInstrumentMarginRateQryBean = new FuturesInstrumentMarginRateQryBean(accountId, stockCode);
        TradeAgent tradeAgent = null;
        tradeAgent = tradeAgentManager.findAgentByAccountId(accountId);
        if (!Objects.isNull(tradeAgent)) {
            try {
                tradeAgent.reqAccountFuturesMarginRate(futuresInstrumentMarginRateQryBean);
            } catch (Exception e) {
                logger.error("queryFuturesInstrumentMargin error {}", "\r\n" + e);
            }
        } else {
            logger.error("not found agent {} from accountAgents  when  queryFuturesInstrumentMargin!!!", accountId);
        }
    }

    public void queryFuturesInstrumentCommissionRate(String accountId, String stockCode) {
        FuturesInstrumentCommissionRateQryBean futuresInstrumentCommissionRateQryBean = new FuturesInstrumentCommissionRateQryBean(accountId, stockCode);

        TradeAgent tradeAgent = null;
        tradeAgent = tradeAgentManager.findAgentByAccountId(accountId);
        if (!Objects.isNull(tradeAgent)) {
            try {
                tradeAgent.reqAccountFuturesCommissionRate(futuresInstrumentCommissionRateQryBean);
            } catch (Exception e) {
                logger.error("FuturesInstrumentCommissionRateBean error {}", "\r\n" + e);
            }
        } else {
            logger.error("not found agent {} from accountAgents  when  FuturesInstrumentCommissionRateBean!!!", accountId);
        }
    }

    public List<NegotiateQuoteReceiveBean> queryNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean) {
        int pageSize = negotiateQuoteQueryBean.getPageSize();
        if (pageSize > 1000) {
            negotiateQuoteQueryBean.setPageSize(1000);
        }
        List<NegotiateQuoteReceiveBean> list = new ArrayList<>();

        final String side = negotiateQuoteQueryBean.getSide();
        if (StringUtil.isNotEmpty(side)) {
            negotiateQuoteQueryBean.setSide(EntrustSide.getOpEntrustBs(side));
        }

        TradeInfoProRequest dto = new TradeInfoProRequest();
        dto.setAcctNo(negotiateQuoteQueryBean.getAccountId());
        dto.setTradePlace(Market.SZSE.investValue);
        dto.setIsOur("Y");
        List<TradeInfoProResponse> dealerTraderList = commonManagement.getDealerTraderInfo(dto);
        if (CollectionUtils.isEmpty(dealerTraderList)) {
            logger.error("queryNegotiateQuoteReceive error: 本方交易商、交易主体、交易员信息不存在");
            return list;
        }
        TradeInfoProResponse tradeInfoProResponse = dealerTraderList.get(0);
        if (StringUtil.isEmpty(negotiateQuoteQueryBean.getMemberId())) {
            negotiateQuoteQueryBean.setMemberId(tradeInfoProResponse.getDealerNo());
        }
        if (StringUtil.isEmpty(negotiateQuoteQueryBean.getInvestorId())) {
            negotiateQuoteQueryBean.setInvestorId(tradeInfoProResponse.getDealerPartyNo());
        }
        if (StringUtil.isEmpty(negotiateQuoteQueryBean.getTraderCode())) {
            negotiateQuoteQueryBean.setTraderCode(tradeInfoProResponse.getTraderNo());
        }

        TradeAgent tradeAgent = getAgent(negotiateQuoteQueryBean.getAccountId());
        if (Objects.nonNull(tradeAgent)) {
            try {
                List<NegotiateQuoteReceiveBean> beanList = tradeAgent.reqNegotiateQuoteReceive(negotiateQuoteQueryBean);
                if (Objects.nonNull(beanList) && beanList.size() > 0) {
                    list.addAll(beanList.stream()
                            .filter(o -> {
                                if (negotiateQuoteQueryBean.getRequestStatus() != 0) {
                                    return negotiateQuoteQueryBean.getRequestStatus() == o.getRequestStatus();
                                }
                                return true;
                            }).collect(Collectors.toList()));

                    list.forEach(o -> {
                        o.setMemberName(tradeInfoProResponse.getDealerName());
                        o.setInvestorName(tradeInfoProResponse.getDealerPartyName());
                        o.setTraderName(tradeInfoProResponse.getTraderName());
                    });
                }
            } catch (Exception e) {
                logger.error("reqIntegratedFixedIncomeQuote error {}", "\r\n" + e);
            }
        }
        return list;
    }
}
