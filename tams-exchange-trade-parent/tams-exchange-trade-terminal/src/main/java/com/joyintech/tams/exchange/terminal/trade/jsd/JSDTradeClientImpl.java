package com.joyintech.tams.exchange.terminal.trade.jsd;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.constant.AgentType;
import com.joyintech.tams.exchange.common.constant.EnableAmount;
import com.joyintech.tams.exchange.common.constant.EntrustStatus;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.terminal.agent.ITerminalMsgNotify;
import com.joyintech.tams.exchange.terminal.agent.TerminalAgent;
import com.joyintech.tams.exchange.terminal.constant.AccountLoginStatus;
import com.joyintech.tams.exchange.terminal.trade.EntrustIdManager;
import com.joyintech.tams.exchange.terminal.trade.ITradeRequest;
import com.joyintech.tams.exchange.terminal.trade.TradeResponseImpl;
import com.joyintech.tams.exchange.terminal.trade.jsd.protocol.MessageManager;
import com.joyintech.tams.exchange.terminal.trade.jsd.protocol.resp.RespLoginData;
import com.joyintech.tams.exchange.terminal.trade.jsd.protocol.resp.RespLoginHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class JSDTradeClientImpl implements ITradeRequest {
    private static final Logger logger = LoggerFactory.getLogger(JSDTradeClientImpl.class);

    private int tradeCount;//查询次数
    private int connectTimeout;
    private int delay;
    private ITerminalMsgNotify iTerminalMsgNotify;
    private TradeResponseImpl tradeResponse;
    private EntrustIdManager entrustIdManager;

    // 查询线程
    private List<ThreadPoolExecutor> poolExecutors;
    private JSDTradeApi jsdTradeApi;
    private JSDMsgReceiver jsdMsgReceiver;
    private JSDMsgHandler jsdMsgHandler;
    private MessageManager messageManager;
    private Map<String, Account> serverAccountMap = Maps.newConcurrentMap();//accountId-Account
    private Map<String, Account> financeAccountMap = Maps.newConcurrentMap();//financeAccount-Account
    private Map<String, RespLoginHeader> loginMap = Maps.newConcurrentMap();//accountId-RespLoginHeader
    private Map<String, List<RespLoginData>> loginDataMap = Maps.newConcurrentMap();//accountId-RespLoginData

    public void addLoginMap(String accountId, RespLoginHeader respLoginHeader) {
        loginMap.put(accountId, respLoginHeader);
    }

    public RespLoginHeader getLoginHeaderByLoginMap(String accountId) {
        return loginMap.get(accountId);
    }

    public void addLoginDataMap(String accountId, List<RespLoginData> dataList) {
        loginDataMap.put(accountId, dataList);
    }

    public List<RespLoginData> getLoginDataById(String accountId) {
        return loginDataMap.get(accountId);
    }

    public void addServerAccountMap(String id, Account account) {
        serverAccountMap.put(id, account);
    }

    public void addFinanceAccountMap(String fundAccount, Account account) {
        financeAccountMap.put(fundAccount, account);
    }

    public Map<String, Account> getAccounts() {
        return financeAccountMap;
    }

    public boolean hadInit() {
        return jsdTradeApi.hadInit();
    }

    @Override
    public Set<String> getLoginAccount() {
        return serverAccountMap.keySet();
    }

    @Override
    public void setITerminalMsgNotify(ITerminalMsgNotify iTerminalMsgNotify) {
        this.iTerminalMsgNotify = iTerminalMsgNotify;
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
        logger.info("jsd reqLogin begin:{}", account.getId());
        jsdMsgReceiver.reqLogin(account);
        //jsdTradeApi.reqClientLogin(account);
    }

    /***
     * @Description: 证券委托查询
     * @Param: [accountId, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/6/23
     */
    @Override
    public void reqEntrusts(String accountId, Long requestId) {
        Account account = serverAccountMap.get(accountId);
        if (Objects.isNull(account)) {
            logger.error("reqEntrusts accountId:{} login failed,account is null", accountId);
            tradeResponse.resQueryEntrusts(null, requestId);
            return;
        }
        RespLoginHeader respLoginHeader = loginMap.get(accountId);
        if (Objects.isNull(respLoginHeader)) {
            logger.error("reqEntrusts accountId:{} login failed,respLoginHeader is null", accountId);
            tradeResponse.resQueryEntrusts(null, requestId);
            return;
        }
        jsdMsgReceiver.reqEntrusts(account, requestId, respLoginHeader);
        //jsdTradeApi.reqQryEntrust(account, requestId, respLoginHeader);
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
        logger.info("jsd init start begin");
        messageManager = new MessageManager();
        jsdTradeApi = new JSDTradeApi();
        jsdTradeApi.setConnectTimeout(connectTimeout);
        jsdTradeApi.setDelay(delay);
        jsdTradeApi.setIp(serverExtendInfo.getSzAddress());
        jsdTradeApi.setPort(Integer.valueOf(serverExtendInfo.getnPort()));
        jsdTradeApi.setTradeResponseImpl(tradeResponse);
        jsdTradeApi.setJsdTradeClient(this);
        jsdTradeApi.setMessageManager(messageManager);
        jsdTradeApi.setEntrustIdManager(entrustIdManager);
        jsdTradeApi.init();
        jsdMsgHandler = new JSDMsgHandler();
        jsdMsgHandler.setJsdTradeApi(jsdTradeApi);
        jsdMsgReceiver = new JSDMsgReceiver();
        jsdMsgReceiver.setJsdMsgHandler(jsdMsgHandler);
        jsdMsgReceiver.init();
        tradeResponse.setTerminalAgent(agent);
        jsdTradeApi.connect();
        poolExecutors = Lists.newArrayListWithCapacity(tradeCount);
        for (int i = 0; i < tradeCount; i++) {
            poolExecutors.add(new ThreadPoolExecutor(1, 1, 0,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingDeque<>(100),
                    new ThreadPoolExecutor.DiscardPolicy()));
        }
        this.iTerminalMsgNotify = iTerminalMsgNotify;
    }

    /***
     * @Description: 证券成交查询
     * @Param: [accountId, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/6/28
     */
    @Override
    public void reqTrades(String accountId, Long requestId) {
        Account account = serverAccountMap.get(accountId);
        if (Objects.isNull(account)) {
            logger.error("reqTrades accountId:{} login failed,account is null", accountId);
            tradeResponse.resQueryTrade(null, requestId);
            return;
        }
        RespLoginHeader respLoginHeader = loginMap.get(accountId);
        if (Objects.isNull(respLoginHeader)) {
            logger.error("reqTrades accountId:{} login failed,respLoginHeader is null", accountId);
            tradeResponse.resQueryTrade(null, requestId);
            return;
        }
        jsdMsgReceiver.reqQryTrade(account, requestId, respLoginHeader);
        //jsdTradeApi.reqQryTrade(account, requestId, respLoginHeader);
    }

    /***
     * @Description: 普通委托
     * @Param: [entrust, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/6/28
     */
    @Override
    public void entrust(Entrust entrust, Long requestId) {
        Account account = serverAccountMap.get(entrust.getAccountId());
        if (Objects.isNull(account)) {
            logger.error("entrust accountId:{} login failed,account is null", entrust.getAccountId());
            entrust.setErrorCode(ErrorCode.ACCOUNT_NOT_LOGIN.value);
            entrust.setEntrustStatus(EntrustStatus.INVALID.value);
            entrust.setErrorMsg("帐号" + entrust.getAccountId() + "登录失败");
            tradeResponse.resEntrust(entrust);
            return;
        }
        RespLoginHeader respLoginHeader = loginMap.get(entrust.getAccountId());
        if (Objects.isNull(respLoginHeader)) {
            logger.error("entrust accountId:{} login failed,respLoginHeader is null", entrust.getAccountId());
            entrust.setErrorCode(ErrorCode.ACCOUNT_NOT_LOGIN.value);
            entrust.setEntrustStatus(EntrustStatus.INVALID.value);
            entrust.setErrorMsg("帐号" + entrust.getAccountId() + "登录失败");
            tradeResponse.resEntrust(entrust);
            return;
        }
        List<RespLoginData> dataList = loginDataMap.get(entrust.getAccountId());
        if (Objects.isNull(dataList)) {
            logger.error("entrust accountId:{} login failed,dataList is null", entrust.getAccountId());
            entrust.setErrorCode(ErrorCode.ACCOUNT_NOT_LOGIN.value);
            entrust.setEntrustStatus(EntrustStatus.INVALID.value);
            entrust.setErrorMsg("帐号" + entrust.getAccountId() + "登录失败");
            tradeResponse.resEntrust(entrust);
            return;
        }
        Map<Character, RespLoginData> dataMap = dataList.stream().collect(Collectors.toMap(RespLoginData::getMarket, respLoginData -> respLoginData));
        //jsdTradeApi.entrust(entrust, account, requestId, respLoginHeader, dataMap);
        jsdMsgReceiver.entrust(entrust, account, requestId, respLoginHeader, dataMap);
    }

    /***
     * @Description: 综合业务委托确认 固收 债券质押式协议回购
     * @Param: [integratedEntrust, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/6/30
     */
    @Override
    public void integratedEntrust(IntegratedEntrust integratedEntrust, Long requestId) {
        Account account = serverAccountMap.get(integratedEntrust.getAccountId());
        if (Objects.isNull(account)) {
            logger.error("integratedEntrust accountId:{} login failed,account is null", integratedEntrust.getAccountId());
            integratedEntrust.setErrorCode(ErrorCode.ACCOUNT_NOT_LOGIN.value);
            integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
            integratedEntrust.setErrorMsg("帐号" + integratedEntrust.getAccountId() + "登录失败");
            tradeResponse.resIntegratedEntrust(integratedEntrust);
            return;
        }
        RespLoginHeader respLoginHeader = loginMap.get(integratedEntrust.getAccountId());
        if (Objects.isNull(respLoginHeader)) {
            logger.error("integratedEntrust accountId:{} login failed,respLoginHeader is null", integratedEntrust.getAccountId());
            integratedEntrust.setErrorCode(ErrorCode.ACCOUNT_NOT_LOGIN.value);
            integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
            integratedEntrust.setErrorMsg("帐号" + integratedEntrust.getAccountId() + "登录失败");
            tradeResponse.resIntegratedEntrust(integratedEntrust);
            return;
        }
        List<RespLoginData> dataList = loginDataMap.get(integratedEntrust.getAccountId());
        if (Objects.isNull(dataList)) {
            logger.error("integratedEntrust accountId:{} login failed,dataList is null", integratedEntrust.getAccountId());
            integratedEntrust.setErrorCode(ErrorCode.ACCOUNT_NOT_LOGIN.value);
            integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
            integratedEntrust.setErrorMsg("帐号" + integratedEntrust.getAccountId() + "登录失败");
            tradeResponse.resIntegratedEntrust(integratedEntrust);
            return;
        }
        Map<Character, RespLoginData> dataMap = dataList.stream().collect(Collectors.toMap(RespLoginData::getMarket, respLoginData -> respLoginData));
        //jsdTradeApi.integratedEntrust(integratedEntrust, requestId, account, respLoginHeader, dataMap);
        jsdMsgReceiver.integratedEntrust(integratedEntrust, requestId, account, respLoginHeader, dataMap);
    }

    /***
     * @Description: 委托撤单
     * @Param: [entrust, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/6/30
     */
    @Override
    public void cancelEntrust(CancelEntrustBean cancelEntrustBean, Long requestId) {
        cancelEntrustBean.setAgentType(AgentType.JSD.value);
        Account account = serverAccountMap.get(cancelEntrustBean.getAccountId());
        if (Objects.isNull(account)) {
            logger.error("cancelEntrust accountId:{} login failed,account is null", cancelEntrustBean.getAccountId());
            cancelEntrustBean.setEntrustStatus(EntrustStatus.INVALID.value);
            cancelEntrustBean.setErrorCode(ErrorCode.ACCOUNT_NOT_LOGIN.value);
            cancelEntrustBean.setErrorMsg("帐号" + cancelEntrustBean.getAccountId() + "登录失败");
            tradeResponse.resCancelEntrust(cancelEntrustBean);
            return;
        }
        RespLoginHeader respLoginHeader = loginMap.get(cancelEntrustBean.getAccountId());
        if (Objects.isNull(respLoginHeader)) {
            logger.error("cancelEntrust accountId:{} login failed,respLoginHeader is null", cancelEntrustBean.getAccountId());
            cancelEntrustBean.setEntrustStatus(EntrustStatus.INVALID.value);
            cancelEntrustBean.setErrorCode(ErrorCode.ACCOUNT_NOT_LOGIN.value);
            cancelEntrustBean.setErrorMsg("帐号" + cancelEntrustBean.getAccountId() + "登录失败");
            tradeResponse.resCancelEntrust(cancelEntrustBean);
            return;
        }
        //jsdTradeApi.cancelEntrust(entrust, account, respLoginHeader);
        jsdMsgReceiver.cancelEntrust(cancelEntrustBean, account, respLoginHeader);
    }

    /***
     * @Description: 综合业务委托撤单
     * @Param: [integratedEntrust, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/6/30
     */
    @Override
    public void cancelIntegratedEntrust(CancelEntrustBean cancelEntrustBean, Long requestId) {
        cancelEntrustBean.setAgentType(AgentType.JSD.value);
        Account account = serverAccountMap.get(cancelEntrustBean.getAccountId());
        if (Objects.isNull(account)) {
            logger.error("cancelIntegratedEntrust accountId:{} login failed,account is null", cancelEntrustBean.getAccountId());
            cancelEntrustBean.setEntrustStatus(EntrustStatus.INVALID.value);
            cancelEntrustBean.setErrorCode(ErrorCode.ACCOUNT_NOT_LOGIN.value);
            cancelEntrustBean.setErrorMsg("帐号" + cancelEntrustBean.getAccountId() + "登录失败");
            tradeResponse.resCancelIntegratedEntrust(cancelEntrustBean);
            return;
        }
        RespLoginHeader respLoginHeader = loginMap.get(cancelEntrustBean.getAccountId());
        if (Objects.isNull(respLoginHeader)) {
            logger.error("cancelIntegratedEntrust accountId:{} login failed,respLoginHeader is null", cancelEntrustBean.getAccountId());
            cancelEntrustBean.setEntrustStatus(EntrustStatus.INVALID.value);
            cancelEntrustBean.setErrorCode(ErrorCode.ACCOUNT_NOT_LOGIN.value);
            cancelEntrustBean.setErrorMsg("帐号" + cancelEntrustBean.getAccountId() + "登录失败");
            tradeResponse.resCancelIntegratedEntrust(cancelEntrustBean);
            return;
        }
        //jsdTradeApi.cancelIntegratedEntrust(integratedEntrust, account, respLoginHeader);
        jsdMsgReceiver.cancelIntegratedEntrust(cancelEntrustBean, account, respLoginHeader);
    }

    /***
     * @Description: 大宗交易行情查询
     * @Param: [quoteQryBean, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/7/2
     */
    @Override
    public void queryIntegratedQuotes(QuoteQryBean quoteQryBean, Long requestId) {
        Account account = serverAccountMap.get(quoteQryBean.getAccountId());
        if (Objects.isNull(account)) {
            logger.error("queryIntegratedQuotes accountId:{} login failed,account is null", quoteQryBean.getAccountId());
            tradeResponse.resQueryIntegratedQuotes(null, requestId);
            return;
        }
        RespLoginHeader respLoginHeader = loginMap.get(quoteQryBean.getAccountId());
        if (Objects.isNull(respLoginHeader)) {
            logger.error("queryIntegratedQuotes accountId:{} login failed,respLoginHeader is null", quoteQryBean.getAccountId());
            tradeResponse.resQueryIntegratedQuotes(null, requestId);
            return;
        }
        // jsdTradeApi.queryIntegratedQuotes(quoteQryBean, requestId, account, respLoginHeader);
        jsdMsgReceiver.queryIntegratedQuotes(quoteQryBean, requestId, account, respLoginHeader);
    }

    /***
     * @Description: 最大交易数量
     * @Param: [qryBean, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/7/2
     */
    @Override
    public void queryEnable(EnableQryBean qryBean, Long requestId) {
        Account account = serverAccountMap.get(qryBean.getAccountId());
        if (Objects.isNull(account)) {
            logger.error("queryEnable accountId:{} login failed,account is null", qryBean.getAccountId());
            tradeResponse.resQueryEnable(EnableAmount.ERROR.value, requestId);
            return;
        }
        RespLoginHeader respLoginHeader = loginMap.get(qryBean.getAccountId());
        if (Objects.isNull(respLoginHeader)) {
            logger.error("queryEnable accountId:{} login failed,respLoginHeader is null", qryBean.getAccountId());
            tradeResponse.resQueryEnable(EnableAmount.ERROR.value, requestId);
            return;
        }
        List<RespLoginData> dataList = loginDataMap.get(qryBean.getAccountId());
        if (Objects.isNull(dataList)) {
            logger.error("queryEnable accountId:{} login failed,dataList is null", qryBean.getAccountId());
            tradeResponse.resQueryEnable(EnableAmount.ERROR.value, requestId);
            return;
        }
        Map<Character, RespLoginData> dataMap = dataList.stream().collect(Collectors.toMap(RespLoginData::getMarket, respLoginData -> respLoginData));
        // jsdTradeApi.queryEnable(qryBean, requestId, account, respLoginHeader, dataMap);
        jsdMsgReceiver.queryEnable(qryBean, requestId, account, respLoginHeader, dataMap);
    }

    /***
     * @Description: 历史证券成交查询
     * @Param: [historyQryBean, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/7/2
     */
    @Override
    public void queryHistoryTrade(HistoryQryBean historyQryBean, Long requestId) {
        Account account = serverAccountMap.get(historyQryBean.getAccountId());
        if (Objects.isNull(account)) {
            logger.error("queryHistoryTrade accountId:{} login failed,account is null", historyQryBean.getAccountId());
            tradeResponse.resQueryHistoryTrade(null, requestId);
            return;
        }
        RespLoginHeader respLoginHeader = loginMap.get(historyQryBean.getAccountId());
        if (Objects.isNull(respLoginHeader)) {
            logger.error("queryHistoryTrade accountId:{} login failed,respLoginHeader is null", historyQryBean.getAccountId());
            tradeResponse.resQueryHistoryTrade(null, requestId);
            return;
        }
        // jsdTradeApi.queryHistoryTrade(historyQryBean, requestId, account, respLoginHeader);
        jsdMsgReceiver.queryHistoryTrade(historyQryBean, requestId, account, respLoginHeader);
    }

    /***
     * @Description: 客户资金快速查询
     * @Param: [accountId, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/7/2
     */
    @Override
    public void queryCustomerFinance(String accountId, Long requestId) {
        Account account = serverAccountMap.get(accountId);
        if (Objects.isNull(account)) {
            logger.error("queryCustomerFinance accountId:{} login failed,account is null", accountId);
            tradeResponse.resQueryCustomerFinance(null, requestId);
            return;
        }
        RespLoginHeader respLoginHeader = loginMap.get(accountId);
        if (Objects.isNull(respLoginHeader)) {
            logger.error("queryCustomerFinance accountId:{} login failed,respLoginHeader is null", accountId);
            tradeResponse.resQueryCustomerFinance(null, requestId);
            return;
        }
        // jsdTradeApi.queryCustomerFinance(requestId, account, respLoginHeader);
        jsdMsgReceiver.queryCustomerFinance(requestId, account, respLoginHeader);
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
        Account account = serverAccountMap.get(positionQueryBean.getAccountId());
        if (Objects.isNull(account)) {
            logger.error("queryAccountPosition accountId:{} login failed,account is null", positionQueryBean.getAccountId());
            tradeResponse.resQueryAccountPosition(null, requestId);
            return;
        }
        RespLoginHeader respLoginHeader = loginMap.get(positionQueryBean.getAccountId());
        if (Objects.isNull(respLoginHeader)) {
            logger.error("queryAccountPosition accountId:{} login failed,respLoginHeader is null", positionQueryBean.getAccountId());
            tradeResponse.resQueryAccountPosition(null, requestId);
            return;
        }
        List<RespLoginData> dataList = loginDataMap.get(positionQueryBean.getAccountId());
        if (Objects.isNull(dataList)) {
            logger.error("queryAccountPosition accountId:{} login failed,dataList is null", positionQueryBean.getAccountId());
            tradeResponse.resQueryAccountPosition(null, requestId);
            return;
        }
        Map<Character, RespLoginData> dataMap = dataList.stream().collect(Collectors.toMap(RespLoginData::getMarket, respLoginData -> respLoginData));
        jsdMsgReceiver.queryAccountPosition(requestId, account, respLoginHeader, positionQueryBean, dataMap);
    }

    @Override
    public void queryPledgeDiscount(String accountId, String market, String stockCode, Long requestId) {
        //todo
    }

    @Override
    public void queryStandardBonds(StandardBondQryBean standardBondQryBean, Long requestId) {
        Account account = serverAccountMap.get(standardBondQryBean.getAccountId());
        if (Objects.isNull(account)) {
            logger.error("查询标准券可用失败， 账户:{} 账户登录失败,未找到已登录的账户", standardBondQryBean.getAccountId());
            tradeResponse.resQueryStandardBonds(null, requestId);
            return;
        }
        RespLoginHeader respLoginHeader = loginMap.get(standardBondQryBean.getAccountId());
        if (Objects.isNull(respLoginHeader)) {
            logger.error("查询标准券可用失败， 账户:{} 账户登录失败,未找到已登录的账户回复头信息", standardBondQryBean.getAccountId());
            tradeResponse.resQueryStandardBonds(null, requestId);
            return;
        }
//         jsdTradeApi.queryStandardBonds(requestId, account, respLoginHeader, standardBondQryBean);
        jsdMsgReceiver.queryStandardBonds(requestId, account, respLoginHeader, standardBondQryBean);
    }

    @Override
    public void queryInstrumentInfo(InstrumentQryBean instrumentQryBean, Long requestId) {
        //todo
    }

    @Override
    public void queryNextTrade() {
        Map<String, Account> loginInfoMap = serverAccountMap;//登录成功用户
        if (loginInfoMap.size() > 0 && jsdTradeApi.isLogin()) {
            loginInfoMap.forEach((accountId, account) -> {
                int index = (account.getFinanceAccount().hashCode() & Integer.MAX_VALUE) % tradeCount;
                if (poolExecutors.size() > 1) {
                    poolExecutors.get(index).submit(() -> {
                        try {
                            //只有进入交易状态才开始轮询
                            if(jsdTradeApi.getStatus(accountId) == AccountLoginStatus.TRADING.value){
                                RespLoginHeader respLoginHeader = loginMap.get(accountId);
                                if (Objects.isNull(respLoginHeader)) {
                                    logger.error("queryNextTrade accountId:{} login failed,respLoginHeader is null", accountId);
                                    return;
                                }
                                jsdMsgReceiver.qryEntrust(account, respLoginHeader);//委托主动获取
                                jsdMsgReceiver.qryTrade(account, respLoginHeader);//成交主动获取
                                jsdMsgReceiver.qryIntegratedEntrust(account, respLoginHeader);//大宗委托主动拉取
                                jsdMsgReceiver.qryIntegratedTrade(account, respLoginHeader);//大宗成交主动拉取
                            }
                        } catch (Exception e) {
                            logger.error("queryNextTrade failed. reason:{}", e.getMessage());
                        }
                    });
                }
            });
        }
    }

    //大宗交易行情查询
    @Override
    public void queryIntegratedBulkQuote(IntegratedBulkQuoteQryBean integratedBulkQuoteQryBean, Long requestId) {
        Account account = serverAccountMap.get(integratedBulkQuoteQryBean.getAccountId());
        if (Objects.isNull(account)) {
            logger.error("queryIntegratedBulkQuote accountId:{} login failed,account is null", integratedBulkQuoteQryBean.getAccountId());
            tradeResponse.resQueryIntegratedBulkQuote(null, requestId);
            return;
        }
        RespLoginHeader respLoginHeader = loginMap.get(integratedBulkQuoteQryBean.getAccountId());
        if (Objects.isNull(respLoginHeader)) {
            logger.error("queryIntegratedBulkQuote accountId:{} login failed,respLoginHeader is null", integratedBulkQuoteQryBean.getAccountId());
            tradeResponse.resQueryIntegratedBulkQuote(null, requestId);
            return;
        }
        jsdMsgReceiver.queryIntegratedBulkQuote(requestId, account, respLoginHeader, integratedBulkQuoteQryBean);
    }

    //盘后大宗交易行情查询
    @Override
    public void queryIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean, Long requestId) {
        Account account = serverAccountMap.get(integratedBulkCloseQuoteQryBean.getAccountId());
        if (Objects.isNull(account)) {
            logger.error("queryIntegratedBulkCloseQuote accountId:{} login failed,account is null", integratedBulkCloseQuoteQryBean.getAccountId());
            tradeResponse.resQueryIntegratedBulkCloseQuote(null, requestId);
            return;
        }
        RespLoginHeader respLoginHeader = loginMap.get(integratedBulkCloseQuoteQryBean.getAccountId());
        if (Objects.isNull(respLoginHeader)) {
            logger.error("queryIntegratedBulkCloseQuote accountId:{} login failed,respLoginHeader is null", integratedBulkCloseQuoteQryBean.getAccountId());
            tradeResponse.resQueryIntegratedBulkCloseQuote(null, requestId);
            return;
        }
        jsdMsgReceiver.queryIntegratedBulkCloseQuote(requestId, account, respLoginHeader, integratedBulkCloseQuoteQryBean);
    }

    @Override
    public void queryIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean, Long requestId) {
    }

    @Override
    public void queryIntegratedFixedIncomeQuote(IntegratedFixedIncomeQuoteQryBean integratedFixedIncomeQuoteQryBean, Long requestId) {
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

    public void setTradeCount(int tradeCount) {
        this.tradeCount = tradeCount;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setTradeResponse(TradeResponseImpl tradeResponse) {
        this.tradeResponse = tradeResponse;
    }

    @Override
    public void reqReissueEntrust(String accountId, Long requestId) {
        Account account = serverAccountMap.get(accountId);
        if (Objects.isNull(account)) {
            logger.error("reqReissueEntrust accountId:{} login failed,account is null", accountId);
            return;
        }
        RespLoginHeader respLoginHeader = loginMap.get(accountId);
        if (Objects.isNull(respLoginHeader)) {
            logger.error("reqReissueEntrust accountId:{} login failed,respLoginHeader is null", accountId);
            return;
        }
        jsdMsgReceiver.reqReissueEntrust(requestId, account, respLoginHeader);
    }

    @Override
    public void reqReissueTrade(String accountId, Long requestId) {
        Account account = serverAccountMap.get(accountId);
        if (Objects.isNull(account)) {
            logger.error("reqReissueTrade accountId:{} login failed,account is null", accountId);
            return;
        }
        RespLoginHeader respLoginHeader = loginMap.get(accountId);
        if (Objects.isNull(respLoginHeader)) {
            logger.error("reqReissueTrade accountId:{} login failed,respLoginHeader is null", accountId);
            return;
        }
        jsdMsgReceiver.reqReissueTrade(requestId, account, respLoginHeader);
    }

    @Override
    public int getStatus(String accountId) {
        return jsdTradeApi.getStatus(accountId);
    }

    @Override
    public void setTradeStatus(String accountId, AccountLoginStatus status) {
        jsdTradeApi.setStatus(accountId, status);
    }


    public void setEntrustIdManager(EntrustIdManager entrustIdManager) {
        this.entrustIdManager = entrustIdManager;
    }

	@Override
	public void queryNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean, Long requestId) {
		// TODO Auto-generated method stub
		
	}
}
