package com.joyintech.tams.exchange.terminal.trade.ufx;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.constant.AgentType;
import com.joyintech.tams.exchange.common.constant.EnableAmount;
import com.joyintech.tams.exchange.common.constant.EntrustStatus;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.net.agent.Agent;
import com.joyintech.tams.exchange.terminal.agent.ITerminalMsgNotify;
import com.joyintech.tams.exchange.terminal.agent.TerminalAgent;
import com.joyintech.tams.exchange.terminal.constant.AccountLoginStatus;
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

public class UfxTradeClientImpl implements ITradeRequest {
    private static final Logger logger = LoggerFactory.getLogger(UfxTradeClientImpl.class);

    private Map<String, Account> serverAccountMap = Maps.newConcurrentMap();//accountId-Account
    private Map<String, Account> financeAccountMap = Maps.newConcurrentMap();//financeAccount-Account
    private Map<String, String> accountEntrustMap = Maps.newConcurrentMap();//entrustId-accountId
    private Map<String, CancelEntrustBean> entrustMap = Maps.newConcurrentMap();//cancelId-cancelEntrust

    private boolean connectionFlag = false;
    private Agent agent = null;
    private UfxTradeApi ufxTradeApi;
    private String op_station;
    private ITerminalMsgNotify iTerminalMsgNotify;
    private UfxTradeHandler ufxTradeHandler;
    private TradeResponseImpl ufxTradeResponse;

    public String getAccountIdByFinanceAccount(String financeAccount) {
        Account account = financeAccountMap.get(financeAccount);
        return Objects.nonNull(account) ? account.getId() : null;
    }

    public Account getAccountById(String id) {
        return serverAccountMap.get(id);
    }

    public void setITerminalMsgNotify(ITerminalMsgNotify iTerminalMsgNotify) {
        this.iTerminalMsgNotify = iTerminalMsgNotify;
    }

    public String getAccountIdByEntrustId(String entrustId) {
        return accountEntrustMap.get(entrustId);
    }

    public CancelEntrustBean getEntrustByCancelId(String cancelId) {
        return entrustMap.get(cancelId);
    }

    // 查询线程
    private List<ThreadPoolExecutor> poolExecutors;

    private int tradeCount;//查询次数

    @Override
    public Set<String> getLoginAccount() {
        return serverAccountMap.keySet();
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
        logger.info("reqLogin begin:{}", JSONObject.toJSONString(account));
        serverAccountMap.put(account.getId(), account);
        String fundAccount = account.getFinanceAccount();//资产账户
        financeAccountMap.put(fundAccount, account);
        try {
            ufxTradeApi.ReqClientLogin(account, account.getPwd(), op_station);
        } catch (Exception e) {
            ufxTradeResponse.resLogin(new ResultBean(ErrorCode.TERMINAL_API_ERROR, account), account.getId());
            logger.error("reqLogin failed. reason:{}", e);
        }
    }

    /***
     * @Description: 初始化
     * @Param: [accountMap, iTerminalMsgNotify]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/20
     */
    @Override
    public void init(TerminalAgent agent, ServerExtendInfo serverExtendInfoMap, ITerminalMsgNotify iTerminalMsgNotify) {
        logger.info("UfxTradeClient init start begin");
        this.ufxTradeHandler = new UfxTradeHandler();
        this.ufxTradeApi = new UfxTradeApi();
        this.ufxTradeHandler.setUfxTradeApi(ufxTradeApi);
        this.ufxTradeHandler.setUfxTradeClient(this);
        this.ufxTradeHandler.setUfxTradeResponse(ufxTradeResponse);
        this.ufxTradeApi.setOp_station(op_station);
        this.ufxTradeApi.setUfxTradeClient(this);
        this.ufxTradeApi.setUfxTradeResponse(ufxTradeResponse);
        UfxTradeCallBack.setUfxTradeHandler(serverExtendInfoMap.hashCode(), this.ufxTradeHandler);
        // 消息主推
        UfxMD ufxMD = new UfxMD(serverExtendInfoMap.getTradeTopicName(), serverExtendInfoMap.getEntrustTopicName(), String.valueOf(serverExtendInfoMap.hashCode()));
        this.ufxTradeHandler.setUfxMD(ufxMD);
        UfxMCHandler ufxMCHandler = new UfxMCHandler(ufxTradeApi, ufxTradeResponse);
        UfxMCCallBack.setUfxMCHandler(String.valueOf(serverExtendInfoMap.hashCode()), ufxMCHandler);
        try {
            ufxMD.init();
            String serverName = serverExtendInfoMap.getSzServerName();
            logger.info("ufx server name: {}", serverName);
            this.ufxTradeApi.init(serverExtendInfoMap.getUfxTradeClientName(), serverExtendInfoMap.hashCode(),
                    serverExtendInfoMap.getUfxBrokerType(), serverExtendInfoMap.getOpEntrustWay());
            TradeResponseImpl ufxTradeResponse = ufxTradeHandler.getUfxTradeResponse();
            ufxTradeResponse.setTerminalAgent(agent);
            poolExecutors = Lists.newArrayListWithCapacity(tradeCount);
            for (int i = 0; i < tradeCount; i++) {
                poolExecutors.add(new ThreadPoolExecutor(1, 1, 0,
                        TimeUnit.MILLISECONDS,
                        new LinkedBlockingDeque<>(100),
                        new ThreadPoolExecutor.DiscardPolicy()));
            }
            connectionFlag = true;
            logger.info("UfxTradeClient init start end,op_station: {}", op_station);
        } catch (Exception e) {
            logger.info("UfxTradeClient init error,op_station: {}, error_info: {}", op_station, e.getMessage());
        }
    }

    public boolean isConnectionFlag() {
        return connectionFlag;
    }

    /***
     * @Description: 证券委托查询
     * @Param: [accountId, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/27
     */
    @Override
    public void reqEntrusts(String accountId, Long requestId) {
        logger.debug("reqEntrusts begin:{}", accountId);
        Account account = serverAccountMap.get(accountId);
        if (Objects.isNull(account)) {
            logger.error("reqEntrusts accountId:{} login failed", accountId);
            ufxTradeResponse.resQueryEntrusts(null, requestId);
            return;
        }
        String fundAccount = account.getFinanceAccount();//资产账户
        LoginInfo loginInfo = ufxTradeHandler.getLoginInfo(fundAccount);
        if (Objects.isNull(loginInfo)) {
            logger.error("reqEntrusts fundAccount:{} login failed", fundAccount);
            ufxTradeResponse.resQueryEntrusts(null, requestId);
            return;
        }
        try {
            ufxTradeApi.ReqQueryOrder(requestId, loginInfo, account.getPwd(), accountId);
        } catch (Exception e) {
            ufxTradeResponse.resQueryEntrusts(null, requestId);
            logger.error("reqEntrusts failed. reason:{}", e.getMessage());
        }
    }

    /***
     * @Description: 证券成交查询
     * @Param: [accountId]
     * @return: java.util.List<com.joyintech.tams.exchange.common.db.domain.Trade>
     * @Author: MAXX
     * @Date: 2020/3/13
     */
    @Override
    public void reqTrades(String accountId, Long requestId) {
        logger.info("reqTrades begin:{}", accountId);
        Account account = serverAccountMap.get(accountId);
        if (Objects.isNull(account)) {
            logger.error("reqTrades accountId:{} login failed", accountId);
            ufxTradeResponse.resQueryTrade(null, requestId);
            return;
        }
        String fundAccount = account.getFinanceAccount();//资产账户
        LoginInfo loginInfo = ufxTradeHandler.getLoginInfo(fundAccount);
        if (Objects.isNull(loginInfo)) {
            logger.error("reqTrades fundAccount:{} login failed", fundAccount);
            ufxTradeResponse.resQueryTrade(null, requestId);
            return;
        }
        try {
            ufxTradeApi.ReqQueryTrade(requestId, accountId, loginInfo.getBranchNo().toString(), loginInfo.getClientId(), loginInfo.getUserToken(), loginInfo.getFundAccount(), account.getPwd(), loginInfo.getSysnodeId().toString());
        } catch (Exception e) {
            ufxTradeResponse.resQueryTrade(null, requestId);
            logger.error("reqTrades failed. reason:{}", e.getMessage());
        }
    }

    /***
     * @Description: 普通委托
     * @Param: [entrust]
     * @return: com.joyintech.tams.exchange.common.db.domain.Entrust
     * @Author: MAXX
     * @Date: 2020/3/27
     */
    @Override
    public void entrust(Entrust entrust, Long requestId) {
        logger.info("entrust begin:{}", JSONObject.toJSONString(entrust));
        Account account = serverAccountMap.get(entrust.getAccountId());
        if (Objects.isNull(account)) {
            logger.error("entrust accountId:{} login failed", entrust.getAccountId());
            entrust.setErrorCode(ErrorCode.ACCOUNT_NOT_LOGIN.value);
            entrust.setEntrustStatus(EntrustStatus.INVALID.value);
            entrust.setErrorMsg("帐号" + entrust.getAccountId() + "登录失败");
            ufxTradeResponse.resEntrust(entrust);
            return;
        }
        String fundAccount = account.getFinanceAccount();//资产账户
        LoginInfo loginInfo = ufxTradeHandler.getLoginInfo(fundAccount);
        if (Objects.isNull(loginInfo)) {
            logger.error("entrust fundAccount:{} login failed", fundAccount);
            entrust.setErrorCode(ErrorCode.ACCOUNT_NOT_LOGIN.value);
            entrust.setEntrustStatus(EntrustStatus.INVALID.value);
            entrust.setErrorMsg("帐号" + entrust.getAccountId() + "登录失败");
            ufxTradeResponse.resEntrust(entrust);
            return;
        }
        try {
            accountEntrustMap.put(entrust.getId(), entrust.getAccountId());
            ufxTradeApi.entrust(requestId, entrust, account.getPwd(), loginInfo);
        } catch (Exception e) {
            entrust.setErrorCode(ErrorCode.TERMINAL_API_ERROR.value);
            entrust.setEntrustStatus(EntrustStatus.INVALID.value);
            entrust.setErrorMsg(e.getMessage());
            ufxTradeResponse.resEntrust(entrust);
            logger.error("entrust failed. reason:{}", e.getMessage());
        }
    }

    /***
     * @Description: 综合业务委托确认 固收 债券质押式协议回购
     * @Param: [integratedEntrust]
     * @return: com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust
     * @Author: MAXX
     * @Date: 2020/3/17
     */
    @Override
    public void integratedEntrust(IntegratedEntrust integratedEntrust, Long requestId) {
        logger.info("integratedEntrust begin:{}", integratedEntrust);
        Account account = serverAccountMap.get(integratedEntrust.getAccountId());
        if (Objects.isNull(account)) {
            logger.error("integratedEntrust accountId:{} login failed", integratedEntrust.getAccountId());
            integratedEntrust.setErrorCode(ErrorCode.ACCOUNT_NOT_LOGIN.value);
            integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
            integratedEntrust.setErrorMsg("帐号" + integratedEntrust.getAccountId() + "登录失败");
            ufxTradeResponse.resIntegratedEntrust(integratedEntrust);
            return;
        }
        String fundAccount = account.getFinanceAccount();//资产账户
        LoginInfo loginInfo = ufxTradeHandler.getLoginInfo(fundAccount);
        if (Objects.isNull(loginInfo)) {
            logger.error("integratedEntrust fundAccount:{} login failed", fundAccount);
            integratedEntrust.setErrorCode(ErrorCode.ACCOUNT_NOT_LOGIN.value);
            integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
            integratedEntrust.setErrorMsg("帐号" + integratedEntrust.getAccountId() + "登录失败");
            ufxTradeResponse.resIntegratedEntrust(integratedEntrust);
            return;
        }
        try {
            accountEntrustMap.put(integratedEntrust.getId(), integratedEntrust.getAccountId());
            ufxTradeApi.integratedEntrust(requestId, integratedEntrust, account.getPwd(), loginInfo);
        } catch (Exception e) {
            integratedEntrust.setErrorCode(ErrorCode.TERMINAL_API_ERROR.value);
            integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
            integratedEntrust.setErrorMsg(e.getMessage());
            ufxTradeResponse.resIntegratedEntrust(integratedEntrust);
            logger.error("integratedEntrust failed. reason:{}", e.getMessage());
        }
    }

    /***
     * @Description: 委托撤单
     * @Param: [entrust]
     * @return: com.joyintech.tams.exchange.common.db.domain.Entrust
     * @Author: MAXX
     * @Date: 2020/3/24
     */
    @Override
    public void cancelEntrust(CancelEntrustBean cancelEntrustBean, Long requestId) {
        logger.info("cancelEntrust begin:{}", JSONObject.toJSONString(cancelEntrustBean));
        cancelEntrustBean.setAgentType(AgentType.UFX.value);
        Account account = serverAccountMap.get(cancelEntrustBean.getAccountId());
        if (Objects.isNull(account)) {
            logger.error("cancelEntrust accountId:{} login failed", cancelEntrustBean.getAccountId());
            cancelEntrustBean.setEntrustStatus(EntrustStatus.INVALID.value);
            cancelEntrustBean.setErrorCode(ErrorCode.ACCOUNT_NOT_LOGIN.value);
            cancelEntrustBean.setErrorMsg("帐号" + cancelEntrustBean.getAccountId() + "登录失败");
            ufxTradeResponse.resCancelEntrust(cancelEntrustBean);
            return;
        }
        String fundAccount = account.getFinanceAccount();//资产账户
        LoginInfo loginInfo = ufxTradeHandler.getLoginInfo(fundAccount);
        if (Objects.isNull(loginInfo)) {
            logger.error("cancelEntrust fundAccount:{} login failed", fundAccount);
            cancelEntrustBean.setEntrustStatus(EntrustStatus.INVALID.value);
            cancelEntrustBean.setErrorCode(ErrorCode.ACCOUNT_NOT_LOGIN.value);
            cancelEntrustBean.setErrorMsg("帐号" + cancelEntrustBean.getAccountId() + "登录失败");
            ufxTradeResponse.resCancelEntrust(cancelEntrustBean);
            return;
        }
        try {
            accountEntrustMap.put(cancelEntrustBean.getCancelId(), cancelEntrustBean.getAccountId());
            entrustMap.put(cancelEntrustBean.getCancelId(), cancelEntrustBean);
            ufxTradeApi.ReqCancelOrder(requestId, cancelEntrustBean, loginInfo, account.getPwd());
        } catch (Exception e) {
            logger.error("cancelEntrust failed. reason:{}", e.getMessage());
            cancelEntrustBean.setEntrustStatus(EntrustStatus.INVALID.value);
            cancelEntrustBean.setErrorCode(ErrorCode.TERMINAL_API_ERROR.value);
            cancelEntrustBean.setErrorMsg(e.getMessage());
            ufxTradeResponse.resCancelEntrust(cancelEntrustBean);
        }
    }

    /***
     * @Description: 综合业务委托撤单
     * @Param: [integratedEntrust]
     * @return: com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust
     * @Author: MAXX
     * @Date: 2020/3/19
     */
    @Override
    public void cancelIntegratedEntrust(CancelEntrustBean cancelEntrustBean, Long requestId) {
        cancelEntrustBean.setAgentType(AgentType.UFX.value);
        logger.info("cancelIntegratedEntrust begin:{}", JSONObject.toJSONString(cancelEntrustBean));
        Account account = serverAccountMap.get(cancelEntrustBean.getAccountId());
        if (Objects.isNull(account)) {
            cancelEntrustBean.setEntrustStatus(EntrustStatus.INVALID.value);
            cancelEntrustBean.setErrorCode(ErrorCode.ACCOUNT_NOT_LOGIN.value);
            cancelEntrustBean.setErrorMsg("帐号" + cancelEntrustBean.getAccountId() + "登录失败");
            ufxTradeResponse.resCancelIntegratedEntrust(cancelEntrustBean);
            logger.error("cancelIntequeryIntegratedQuotes(gratedEntrust accountId:{} login failed", cancelEntrustBean.getAccountId());
            return;
        }
        String fundAccount = account.getFinanceAccount();//资产账户
        LoginInfo loginInfo = ufxTradeHandler.getLoginInfo(fundAccount);
        if (Objects.isNull(loginInfo)) {
            logger.error("cancelIntegratedEntrust fundAccount:{} login failed", fundAccount);
            cancelEntrustBean.setEntrustStatus(EntrustStatus.INVALID.value);
            cancelEntrustBean.setErrorCode(ErrorCode.ACCOUNT_NOT_LOGIN.value);
            cancelEntrustBean.setErrorMsg("帐号" + cancelEntrustBean.getAccountId() + "登录失败");
            ufxTradeResponse.resCancelIntegratedEntrust(cancelEntrustBean);
            return;
        }
        try {
            entrustMap.put(cancelEntrustBean.getCancelId(), cancelEntrustBean);
            ufxTradeApi.cancelIntegratedEntrust(requestId, cancelEntrustBean, account.getPwd(), loginInfo);
        } catch (Exception e) {
            logger.error("cancelIntegratedEntrust failed. reason:{}", e.getMessage());
            cancelEntrustBean.setEntrustStatus(EntrustStatus.INVALID.value);
            cancelEntrustBean.setErrorCode(ErrorCode.TERMINAL_API_ERROR.value);
            cancelEntrustBean.setErrorMsg(e.getMessage());
            ufxTradeResponse.resCancelIntegratedEntrust(cancelEntrustBean);
        }
    }

    /***
     * @Description: 大宗交易行情查询
     * @Param: [integratedQuote]
     * @return: java.util.List<com.joyintech.tams.exchange.common.bean.IntegratedQuote>
     * @Author: MAXX
     * @Date: 2020/3/24
     */
    @Override
    public void queryIntegratedQuotes(QuoteQryBean quoteQryBean, Long requestId) {
        logger.info("queryIntegratedQuotes begin:{}", quoteQryBean);
        Account account = serverAccountMap.get(quoteQryBean.getAccountId());
        if (Objects.isNull(account)) {
            ufxTradeResponse.resQueryIntegratedQuotes(null, requestId);
            logger.error("queryIntegratedQuotes accountId:{} login failed", quoteQryBean.getAccountId());
            return;
        }
        String fundAccount = account.getFinanceAccount();//资产账户
        LoginInfo loginInfo = ufxTradeHandler.getLoginInfo(fundAccount);
        if (Objects.isNull(loginInfo)) {
            logger.error("queryIntegratedQuotes fundAccount:{} login failed", fundAccount);
            ufxTradeResponse.resQueryIntegratedQuotes(null, requestId);
            return;
        }
        try {
            ufxTradeApi.queryIntegratedQuotes(requestId, quoteQryBean, loginInfo, account.getPwd());
        } catch (Exception e) {
            logger.error("queryIntegratedQuotes failed. reason:{}", e);
            ufxTradeResponse.resQueryIntegratedQuotes(null, requestId);
        }
    }

    /***
     * @Description: 最大交易数量
     * @Param: [qryBean]
     * @return: int
     * @Author: MAXX
     * @Date: 2020/3/23
     */
    @Override
    public void queryEnable(EnableQryBean qryBean, Long requestId) {
        logger.info("queryEnable begin:{}", qryBean);
        Account account = serverAccountMap.get(qryBean.getAccountId());
        if (Objects.isNull(account)) {
            logger.error("queryEnable accountId:{} login failed", qryBean.getAccountId());
            ufxTradeResponse.resQueryEnable(EnableAmount.ERROR.value, requestId);
            return;
        }
        String fundAccount = account.getFinanceAccount();//资产账户
        LoginInfo loginInfo = ufxTradeHandler.getLoginInfo(fundAccount);
        if (Objects.isNull(loginInfo)) {
            logger.error("queryEnable fundAccount:{} login failed", fundAccount);
            ufxTradeResponse.resQueryEnable(EnableAmount.ERROR.value, requestId);
            return;
        }
        try {
            ufxTradeApi.queryEnable(requestId, qryBean, account.getPwd(), loginInfo);
        } catch (Exception e) {
            ufxTradeResponse.resQueryEnable(EnableAmount.ERROR.value, requestId);
            logger.error("queryEnable failed. reason:{}", e.getMessage());
        }
    }

    /***
     * @Description: 历史证券成交查询
     * @Param: [historyQryBean]
     * @return: java.util.List<com.joyintech.tams.exchange.common.bean.HistoryTrade>
     * @Author: MAXX
     * @Date: 2020/3/20
     */
    @Override
    public void queryHistoryTrade(HistoryQryBean historyQryBean, Long requestId) {
        logger.info("queryHistoryTrade begin:{}", historyQryBean);
        Account account = serverAccountMap.get(historyQryBean.getAccountId());
        if (Objects.isNull(account)) {
            logger.error("queryHistoryTrade accountId:{} login failed", historyQryBean.getAccountId());
            ufxTradeResponse.resQueryHistoryTrade(null, requestId);
            return;
        }
        String fundAccount = account.getFinanceAccount();//资产账户
        LoginInfo loginInfo = ufxTradeHandler.getLoginInfo(fundAccount);
        if (Objects.isNull(loginInfo)) {
            logger.error("queryHistoryTrade fundAccount:{} login failed", fundAccount);
            ufxTradeResponse.resQueryHistoryTrade(null, requestId);
            return;
        }
        try {
            ufxTradeApi.qryHisBusiness(requestId, historyQryBean, account.getPwd(), loginInfo);
        } catch (Exception e) {
            logger.error("cancelIntegratedEntrust failed. reason:{}", e.getMessage());
            ufxTradeResponse.resQueryHistoryTrade(null, requestId);
        }
    }

    /***
     * @Description: 客户资金快速查询
     * @Param: [accountId]
     * @return: java.util.List<com.joyintech.tams.exchange.common.bean.CustomerFinance>
     * @Author: MAXX
     * @Date: 2020/3/23
     */
    @Override
    public void queryCustomerFinance(String accountId, Long requestId) {
        logger.info("queryCustomerFinance begin:{}", accountId);
        Account account = serverAccountMap.get(accountId);
        if (Objects.isNull(account)) {
            logger.error("queryCustomerFinance accountId:{} login failed", accountId);
            ufxTradeResponse.resQueryCustomerFinance((List<AccountFinance>) null, requestId);
            return;
        }
        String fundAccount = account.getFinanceAccount();//资产账户
        LoginInfo loginInfo = ufxTradeHandler.getLoginInfo(fundAccount);
        if (Objects.isNull(loginInfo)) {
            logger.error("queryCustomerFinance fundAccount:{} login failed", fundAccount);
            ufxTradeResponse.resQueryCustomerFinance((List<AccountFinance>) null, requestId);
            return;
        }
        try {
            ufxTradeApi.queryCustomerFinance(accountId, account.getPwd(), loginInfo, requestId);
        } catch (Exception e) {
            ufxTradeResponse.resQueryCustomerFinance((List<AccountFinance>) null, requestId);
            logger.error("queryCustomerFinance failed. reason:{}", e.getMessage());
        }
    }

    //证券持仓快速查询
    @Override
    public void queryAccountPosition(PositionQueryBean positionQueryBean, Long requestId) {
        logger.info("queryAccountPosition begin:{}", JSONObject.toJSONString(positionQueryBean));
        Account account = serverAccountMap.get(positionQueryBean.getAccountId());
        if (Objects.isNull(account)) {
            logger.error("queryAccountPosition accountId:{} login failed", positionQueryBean.getAccountId());
            ufxTradeResponse.resQueryAccountPosition((List<PositionBean>) null, requestId);
            return;
        }
        String fundAccount = account.getFinanceAccount();//资产账户
        LoginInfo loginInfo = ufxTradeHandler.getLoginInfo(fundAccount);
        if (Objects.isNull(loginInfo)) {
            logger.error("queryAccountPosition fundAccount:{} login failed", fundAccount);
            ufxTradeResponse.resQueryAccountPosition((List<PositionBean>) null, requestId);
            return;
        }
        try {
            ufxTradeApi.queryAccountPosition(requestId, positionQueryBean, account.getPwd(), loginInfo);
        } catch (Exception e) {
            ufxTradeResponse.resQueryAccountPosition((List<PositionBean>) null, requestId);
            logger.error("queryAccountPosition failed. reason:{}", e.getMessage());
        }
    }

    /***
     * @Description: 债券质押率
     * @Param: [accountId, market, stockCode, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/5/9
     */
    @Override
    public void queryPledgeDiscount(String accountId, String market, String stockCode, Long requestId) {
        logger.info("queryPledgeDiscount begin:{}", accountId);
        Account account = serverAccountMap.get(accountId);
        if (Objects.isNull(account)) {
            logger.error("queryPledgeDiscount accountId:{} login failed", accountId);
            ufxTradeResponse.resQueryPledgeDiscount((List<PledgeDiscount>) null, requestId);
            return;
        }
        String fundAccount = account.getFinanceAccount();//资产账户
        LoginInfo loginInfo = ufxTradeHandler.getLoginInfo(fundAccount);
        if (Objects.isNull(loginInfo)) {
            logger.error("queryPledgeDiscount fundAccount:{} login failed", fundAccount);
            ufxTradeResponse.resQueryPledgeDiscount((List<PledgeDiscount>) null, requestId);
            return;
        }
        try {
            //todo
            //ufxTradeApi.queryPledgeDiscount(accountId, account.getPwd(), loginInfo, market, stockCode, requestId);
        } catch (Exception e) {
            ufxTradeResponse.resQueryPledgeDiscount((List<PledgeDiscount>) null, requestId);
            logger.error("queryPledgeDiscount failed. reason:{}", e.getMessage());
        }
    }

    /***
     * @Description: 标准券查询
     * @Param: [accountId, market, stockCode, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/5/9
     */
    @Override
    public void queryStandardBonds(StandardBondQryBean standardBondQryBean, Long requestId) {
        logger.info("queryStandardBonds begin:{}", standardBondQryBean.getAccountId());
        Account account = serverAccountMap.get(standardBondQryBean.getAccountId());
        if (Objects.isNull(account)) {
            logger.error("queryStandardBonds accountId:{} login failed", standardBondQryBean.getAccountId());
            ufxTradeResponse.resQueryStandardBonds((List<StandardBonds>) null, requestId);
            return;
        }
        String fundAccount = account.getFinanceAccount();//资产账户
        LoginInfo loginInfo = ufxTradeHandler.getLoginInfo(fundAccount);
        if (Objects.isNull(loginInfo)) {
            logger.error("queryStandardBonds fundAccount:{} login failed", fundAccount);
            ufxTradeResponse.resQueryStandardBonds((List<StandardBonds>) null, requestId);
            return;
        }
        try {
            ufxTradeApi.queryStandardBonds(account.getPwd(), loginInfo, standardBondQryBean, requestId);
        } catch (Exception e) {
            ufxTradeResponse.resQueryStandardBonds((List<StandardBonds>) null, requestId);
            logger.error("queryStandardBonds failed. reason:{}", e.getMessage());
        }
    }

    /**
     * 合约代码信息查询
     *
     * @param instrumentQryBean
     * @param requestId
     */
    @Override
    public void queryInstrumentInfo(InstrumentQryBean instrumentQryBean, Long requestId) {
        logger.info("queryInstrumentInfo begin:{}", instrumentQryBean.getAccountId());
        Account account = serverAccountMap.get(instrumentQryBean.getAccountId());
        if (Objects.isNull(account)) {
            logger.error("queryInstrumentInfo accountId:{} login failed", instrumentQryBean.getAccountId());
            ufxTradeResponse.resInstrumentInfo(null, requestId);
            return;
        }
        String fundAccount = account.getFinanceAccount();//资产账户
        LoginInfo loginInfo = ufxTradeHandler.getLoginInfo(fundAccount);
        if (Objects.isNull(loginInfo)) {
            logger.error("queryInstrumentInfo fundAccount:{} login failed", fundAccount);
            ufxTradeResponse.resInstrumentInfo(null, requestId);
            return;
        }
        try {
            List<Instrument> instrumentList = ufxTradeApi.queryInstrumentInfo(loginInfo, instrumentQryBean, requestId);
            logger.info("resp instrument {}", instrumentList.size());
            ufxTradeResponse.resInstrumentInfo(instrumentList, requestId);
        } catch (Exception e) {
            ufxTradeResponse.resInstrumentInfo(null, requestId);
            logger.error("queryInstrumentInfo failed. reason:{}", e.getMessage());
        }
    }

    public void onEntrust(Entrust entrust) {
        iTerminalMsgNotify.onEntrust(entrust);
    }

    public void onIntegratedEntrust(IntegratedEntrust integratedEntrust) {
        iTerminalMsgNotify.onIntegratedEntrust(integratedEntrust);
    }

    public void onTrade(Trade trade) {
        iTerminalMsgNotify.onTrade(trade);
    }

    public Account getAccountByFinanceAccount(String financeAccount) {
        return financeAccountMap.get(financeAccount);
    }

    public Agent getAgent() {
        return agent;
    }

    /***
     * @Description: 成交查询主动拉取
     * @Param: []
     * @return: void
     * @Author: MAXX
     * @Date: 2020/4/9
     */
    @Override
    public void queryNextTrade() {
        if (!ufxTradeApi.isQueryNextTrade()) {
            return;
        }
        Map<String, LoginInfo> loginInfoMap = ufxTradeHandler.getLoginInfoMap();//登录成功用户
        if (loginInfoMap.size() > 0) {
            for (Map.Entry<String, LoginInfo> entry : loginInfoMap.entrySet()) {
                LoginInfo loginInfo = entry.getValue();
                Account account = financeAccountMap.get(loginInfo.getFundAccount());
                if (Objects.isNull(account)) {
                    logger.error("queryNextTrade fundAccount:{} login failed", loginInfo.getFundAccount());
                    return;
                }
                int index = (loginInfo.getFundAccount().hashCode() & Integer.MAX_VALUE) % tradeCount;
                if (poolExecutors.size() > 1) {
                    poolExecutors.get(index).submit(() -> {
                        try {
                            if (ufxTradeApi.getStatus(account.getId()) == AccountLoginStatus.TRADING.value) {
                                //ufxTradeApi.qrySecufastStock(loginInfo, account.getPwd());//客户证券查询
                                // ufxTradeApi.ReqQueryPosition(loginInfo, account.getPwd());//证券持仓查询
                                //ufxTradeApi.queryFixedIncomeOrder(loginInfo, account.getPwd());//固收可撤单委托查询
                                ufxTradeApi.qryOrder(loginInfo, account.getPwd(), account.getId());
                                ufxTradeApi.qryProtocolBackTrade(loginInfo, account.getPwd(), account.getId());
                                ufxTradeApi.qryTrade(loginInfo, account.getPwd(), account.getId());
                                ufxTradeApi.queryIntegratedOrder(loginInfo, account.getPwd(), account.getId());//大宗固收委托查询
                            }
                        } catch (Exception e) {
                            logger.error("queryNextTrade failed. reason:{}", e);
                        }
                    });
                }
            }
        }
    }

    /***
     * @Description: 大宗交易行情查询
     * @Param: [integratedBulkQuoteQryBean, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/9/22
     */
    @Override
    public void queryIntegratedBulkQuote(IntegratedBulkQuoteQryBean integratedBulkQuoteQryBean, Long requestId) {
        logger.info("queryIntegratedBulkQuote begin:{}", integratedBulkQuoteQryBean.getAccountId());
        try {
            ufxTradeApi.queryIntegratedBulkQuote(requestId, integratedBulkQuoteQryBean);
        } catch (Exception e) {
            ufxTradeResponse.resQueryIntegratedBulkQuote((List<IntegratedBulkQuoteBean>) null, requestId);
            logger.error("queryIntegratedBulkQuote failed. reason:{}", e.getMessage());
        }
    }

    /***
     * @Description: 盘后大宗交易行情查询
     * @Param: [integratedBulkCloseQuoteQryBean, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/9/22
     */
    @Override
    public void queryIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean, Long requestId) {
        logger.info("queryIntegratedBulkCloseQuote begin:{}", integratedBulkCloseQuoteQryBean.getAccountId());
        try {
            ufxTradeApi.queryIntegratedBulkCloseQuote(requestId, integratedBulkCloseQuoteQryBean);
        } catch (Exception e) {
            ufxTradeResponse.resQueryIntegratedBulkCloseQuote((List<IntegratedBulkCloseQuoteBean>) null, requestId);
            logger.error("queryIntegratedBulkCloseQuote failed. reason:{}", e);
        }
    }

    /***
     * @Description: 协议回购行情查询
     * @Param: [integratedRepurchaseQuoteQryBean, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/9/22
     */
    @Override
    public void queryIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean, Long requestId) {
        logger.info("queryIntegratedRepurchaseQuote begin:{}", integratedRepurchaseQuoteQryBean.getAccountId());
        Account account = serverAccountMap.get(integratedRepurchaseQuoteQryBean.getAccountId());
        if (Objects.isNull(account)) {
            logger.error("queryIntegratedRepurchaseQuote accountId:{} login failed", integratedRepurchaseQuoteQryBean.getAccountId());
            ufxTradeResponse.resQueryIntegratedRepurchaseQuote((List<IntegratedRepurchaseQuoteBean>) null, requestId);
            return;
        }
        String fundAccount = account.getFinanceAccount();//资产账户
        LoginInfo loginInfo = ufxTradeHandler.getLoginInfo(fundAccount);
        if (Objects.isNull(loginInfo)) {
            logger.error("queryIntegratedRepurchaseQuote fundAccount:{} login failed", fundAccount);
            ufxTradeResponse.resQueryIntegratedRepurchaseQuote((List<IntegratedRepurchaseQuoteBean>) null, requestId);
            return;
        }
        try {
            ufxTradeApi.queryRepoQuotes(requestId, integratedRepurchaseQuoteQryBean, loginInfo, account.getPwd());
        } catch (Exception e) {
            ufxTradeResponse.resQueryIntegratedRepurchaseQuote((List<IntegratedRepurchaseQuoteBean>) null, requestId);
            logger.error("queryIntegratedRepurchaseQuote failed. reason:{}", e.getMessage());
        }
    }

    /***
     * @Description: 上海固收行情查询
     * @Param: [integratedFixedIncomeQuoteQryBean, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/9/22
     */
    @Override
    public void queryIntegratedFixedIncomeQuote(IntegratedFixedIncomeQuoteQryBean integratedFixedIncomeQuoteQryBean, Long requestId) {
        logger.info("queryIntegratedFixedIncomeQuote begin:{}", integratedFixedIncomeQuoteQryBean.getAccountId());
        try {
            ufxTradeApi.queryIntegratedFixedIncomeQuote(requestId, integratedFixedIncomeQuoteQryBean);
        } catch (Exception e) {
            ufxTradeResponse.resQueryIntegratedFixedIncomeQuote((List<IntegratedFixedIncomeQuoteBean>) null, requestId);
            logger.error("queryIntegratedFixedIncomeQuote failed. reason:{}", e.getMessage());
        }
    }

    public UfxTradeHandler getUfxTradeHandler() {
        return ufxTradeHandler;
    }

    public void setUfxTradeResponse(TradeResponseImpl ufxTradeResponse) {
        this.ufxTradeResponse = ufxTradeResponse;
    }

    public void setTradeCount(int tradeCount) {
        this.tradeCount = tradeCount;
    }

    public void setOp_station(String op_station) {
        this.op_station = op_station;
    }

    @Override
    public void reqReissueEntrust(String accountId, Long requestId) {
        logger.info("reqReissueEntrust begin:{}", accountId);
        Account account = serverAccountMap.get(accountId);
        if (Objects.isNull(account)) {
            logger.error("reqReissueEntrust accountId:{} login failed", accountId);
            return;
        }
        String fundAccount = account.getFinanceAccount();//资产账户
        LoginInfo loginInfo = ufxTradeHandler.getLoginInfo(fundAccount);
        if (Objects.isNull(loginInfo)) {
            logger.error("reqReissueEntrust fundAccount:{} login failed", fundAccount);
            return;
        }
        try {
            ufxTradeApi.reqReissueEntrust(loginInfo, account.getPwd(), account.getId(), requestId);
        } catch (Exception e) {
            logger.error("reqReissueEntrust failed. reason:{}", e.getMessage());
        }
    }

    @Override
    public void reqReissueTrade(String accountId, Long requestId) {
        logger.info("reqReissueTrade begin:{}", accountId);
        Account account = serverAccountMap.get(accountId);
        if (Objects.isNull(account)) {
            logger.error("reqReissueTrade accountId:{} login failed", accountId);
            return;
        }
        String fundAccount = account.getFinanceAccount();//资产账户
        LoginInfo loginInfo = ufxTradeHandler.getLoginInfo(fundAccount);
        if (Objects.isNull(loginInfo)) {
            logger.error("reqReissueTrade fundAccount:{} login failed", fundAccount);
            return;
        }
        try {
            ufxTradeApi.reqReissueTrade(account, loginInfo, requestId);
        } catch (Exception e) {
            logger.error("reqReissueTrade failed. reason:{}", e.getMessage());
        }
    }

    @Override
    public int getStatus(String accountId) {
        return ufxTradeApi.getStatus(accountId);
    }

    @Override
    public void setTradeStatus(String accountId, AccountLoginStatus status) {
        ufxTradeApi.setStatus(accountId, status);
    }

	@Override
	public void queryNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean, Long requestId) {
		// TODO Auto-generated method stub
		
	}
}
