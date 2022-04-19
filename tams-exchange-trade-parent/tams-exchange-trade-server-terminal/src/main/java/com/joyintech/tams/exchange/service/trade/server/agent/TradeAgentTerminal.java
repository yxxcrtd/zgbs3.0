package com.joyintech.tams.exchange.service.trade.server.agent;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.bean.terminal.RTGSStatusBean;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.net.agent.Agent;
import com.joyintech.tams.exchange.common.net.agent.AgentStatus;
import com.joyintech.tams.exchange.common.net.protocol.FunctionCode;
import com.joyintech.tams.exchange.common.net.protocol.ProtocolMessage;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.service.trade.processor.TradeHelper;
import com.joyintech.tams.exchange.service.trade.processor.account.AccountPositionHelper;
import com.joyintech.tams.exchange.service.trade.processor.msg.MsgReceiverManager;
import com.joyintech.tams.exchange.service.trade.processor.msg.MsgType;
import com.joyintech.tams.exchange.service.trade.processor.msg.TradeMsgReceiver;
import com.joyintech.tams.exchange.service.trade.processor.others.TerminalHelper;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Scope("prototype")
public class TradeAgentTerminal extends Agent implements TradeAgent, IAgentMsgHandler {

    private static final Logger logger = LoggerFactory.getLogger(TradeAgent.class);

    @Autowired
    private TradeAgentManager tradeAgentManager;
    @Autowired
    private TradeHelper tradeHelper;
    @Autowired
    private MsgReceiverManager msgReceiverManager;
    @Autowired
    private TerminalHelper terminalHelper;
    @Autowired
    private AccountPositionHelper accountPositionHelper;
    @Autowired
    private AccountManager accountManager;

    private String terminalName;

    private Map<String, Account> accountMap;

    public String getTerminalName() {
        return terminalName;
    }

    public Map<String, Account> getAccountMap() {
        return accountMap;
    }


    @Override
    public void connEstablished(Channel channel) {
        agentStatus = AgentStatus.CONNECTED;
        this.channel = channel;
        accountMap = new HashMap<>();
    }

    @Override
    public void connClosed() {
        agentStatus = AgentStatus.DISCONNECTED;
        channel.close();
        logger.info("remote address:{} disconnected, close accounts:{}", channel.remoteAddress().toString(), JsonUtils.getJsonFromObject(accountMap));
        tradeAgentManager.removeTradeAgent(this);
        Set<String> accountIds = accountMap.keySet();
        tradeAgentManager.removeAccounts(accountIds);
        accountManager.clearAccountMaps(accountIds);
        accountMap.clear();
    }

    @Override
    protected void handleAsyncMessage(long requestId, int functionCode, Object message) {
        logger.debug("TradeAgentTerminal handleAsyncMessage: functionCode={}，requestId={}，message{}", functionCode, requestId, JsonUtils.getJsonFromObject(message));
        if (functionCode == FunctionCode.NOTIFY_ENTRUST.value) {
            onRtnEntrust((Entrust) message);
        } else if (functionCode == FunctionCode.NOTIFY_INTEGRATED_ENTRUST.value) {
            onRtnIntegratedEntrust((IntegratedEntrust) message);
        } else if (functionCode == FunctionCode.NOTIFY_TRADE.value) {
            onRtnTrade((Trade) message);
        } else if (functionCode == FunctionCode.NOTIFY_RTGS_STATUS.value) {
            onRtnRTGSStatus((RTGSStatusBean) message);
        } else if (functionCode == FunctionCode.RESP_ENTRUST.value) {
            onRspEntrust((TerminalId) message);
        } else if (functionCode == FunctionCode.RESP_INTEGRATED_ENTRUST.value) {
            onRspIntegratedEntrust((TerminalId) message);
        } else if (functionCode == FunctionCode.RESP_CANCEL_ENTRUST.value) {
            onRspCancelEntrust((CancelEntrustBean) message);
        } else if (functionCode == FunctionCode.RESP_CANCEL_INTEGRATED_ENTRUST.value) {
            onRspCancelIntegratedEntrust((CancelEntrustBean) message);
        } else if (functionCode == FunctionCode.RESP_QRY_FINANCE.value) {
            onRspQryCustomerFinance((List<AccountFinance>) message);
        } else if (functionCode == FunctionCode.TERMINAL_LOGIN.value) {
            onTerminalLogin((TerminalLogin) message);
        } else if (functionCode == FunctionCode.HEARTBEAT.value) {
            asyncWrite(FunctionCode.ACK, "");
        } else if (functionCode == FunctionCode.RESP_ACCOUNT_LOGIN.value) {
            onRspAccountLogin((ResultBean) message);
        } else if (functionCode == FunctionCode.RESP_QRY_FUTURES_MARGIN_RATE.value) {
            tradeHelper.handleMarginRate((List<FuturesInstrumentMarginRateBean>) message);
        } else if (functionCode == FunctionCode.RESP_QRY_FUTURES_COMMISSION_RATE.value) {
            // TODO: 2020/11/24
        } else if (functionCode == FunctionCode.ACK.value) {
        } else {
            logger.info("unknown functionCode:{} message:{}", functionCode, JsonUtils.getJsonFromObject(message));
        }
    }

    @Override
    protected Object getResponseObjectFromMessage(ProtocolMessage message) throws Exception {
    	logger.debug("TradeAgentTerminal getResponseObjectFromMessage: functionCode={}，requestId={}，message{}", message.getFc(), message.getReqId(), JsonUtils.getJsonFromObject(message));
        if (message.getFc() == FunctionCode.NOTIFY_ENTRUST.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), Entrust.class);
        } else if (message.getFc() == FunctionCode.NOTIFY_TRADE.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), Trade.class);
        } else if (message.getFc() == FunctionCode.NOTIFY_INTEGRATED_ENTRUST.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), IntegratedEntrust.class);
        } else if (message.getFc() == FunctionCode.NOTIFY_RTGS_STATUS.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), RTGSStatusBean.class);
        } else if (message.getFc() == FunctionCode.RESP_ENTRUST.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), TerminalId.class);
        } else if (message.getFc() == FunctionCode.RESP_INTEGRATED_ENTRUST.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), TerminalId.class);
        } else if (message.getFc() == FunctionCode.RESP_CANCEL_ENTRUST.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), CancelEntrustBean.class);
        } else if (message.getFc() == FunctionCode.RESP_CANCEL_INTEGRATED_ENTRUST.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), CancelEntrustBean.class);
        } else if (message.getFc() == FunctionCode.RESP_ACCOUNT_LOGIN.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), ResultBean.class);
        } else if (message.getFc() == FunctionCode.RESP_QRY_ENTRUST.value) {
            return Strings.isNullOrEmpty(message.getBody()) ? Lists.newArrayList() :
                    JsonUtils.getObjectListFromJson(message.getBody(), Entrust.class);
        } else if (message.getFc() == FunctionCode.RESP_QRY_TRADE.value) {
            return Strings.isNullOrEmpty(message.getBody()) ? Lists.newArrayList() :
                    JsonUtils.getObjectListFromJson(message.getBody(), Trade.class);
        } else if (message.getFc() == FunctionCode.RESP_QRY_INTEGRATED_QUOTE.value) {
            return Strings.isNullOrEmpty(message.getBody()) ? Lists.newArrayList() :
                    JsonUtils.getObjectListFromJson(message.getBody(), IntegratedQuote.class);
        } else if (message.getFc() == FunctionCode.RESP_QRY_ENABLE.value) {
            return Strings.isNullOrEmpty(message.getBody()) ? 0 :
                    Integer.parseInt(message.getBody());
        } else if (message.getFc() == FunctionCode.RESP_QRY_HISTORY_TRADE.value) {
            return JsonUtils.getObjectListFromJson(message.getBody(), HistoryTrade.class);
        } else if (message.getFc() == FunctionCode.RESP_QRY_FINANCE.value) {
            return JsonUtils.getObjectListFromJson(message.getBody(), AccountFinance.class);
        } else if (message.getFc() == FunctionCode.RESP_QRY_POSITION.value) {
            return JsonUtils.getObjectListFromJson(message.getBody(), PositionBean.class);
        } else if (message.getFc() == FunctionCode.RESP_QRY_INSTRUMENT_INFO.value) {
            return JsonUtils.getObjectListFromJson(message.getBody(), Instrument.class);
        } else if (message.getFc() == FunctionCode.TERMINAL_LOGIN.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), TerminalLogin.class);
        } else if (message.getFc() == FunctionCode.RESP_PLEDGE_DISCOUNT.value) {
            return JsonUtils.getObjectListFromJson(message.getBody(), PledgeDiscount.class);
        } else if (message.getFc() == FunctionCode.RESP_STANDARD_BONDS.value) {
            return JsonUtils.getObjectListFromJson(message.getBody(), StandardBonds.class);
        } else if (message.getFc() == FunctionCode.HEARTBEAT.value || message.getFc() == FunctionCode.ACK.value) {
            return null;
        } else if (message.getFc() == FunctionCode.RESP_QRY_INTEGRATED_BULK_QUOTE.value) {
            return JsonUtils.getObjectListFromJson(message.getBody(), IntegratedBulkQuoteBean.class);
        } else if (message.getFc() == FunctionCode.RESP_QRY_INTEGRATED_BULK_CLOSE_QUOTE.value) {
            return JsonUtils.getObjectListFromJson(message.getBody(), IntegratedBulkCloseQuoteBean.class);
        } else if (message.getFc() == FunctionCode.RESP_QRY_INTEGRATED_REPURCHASE_QUOTE.value) {
            return JsonUtils.getObjectListFromJson(message.getBody(), IntegratedRepurchaseQuoteBean.class);
        } else if (message.getFc() == FunctionCode.RESP_QRY_INTEGRATED_FIXED_INCOME_QUOTE.value) {
            return JsonUtils.getObjectListFromJson(message.getBody(), IntegratedFixedIncomeQuoteBean.class);
        } else if (message.getFc() == FunctionCode.RESP_QRY_FUTURES_INSTRUMENT.value) {
            return JsonUtils.getObjectListFromJson(message.getBody(), FuturesInstrument.class);
        } else if (message.getFc() == FunctionCode.RESP_QRY_FUTURES_POSITION.value) {
            return JsonUtils.getObjectListFromJson(message.getBody(), FuturesPositionBean.class);
        } else if (message.getFc() == FunctionCode.RESP_QRY_FUTURES_POSITION_DETAIL.value) {
            return JsonUtils.getObjectListFromJson(message.getBody(), FuturesPositionDetailBean.class);
        } else if (message.getFc() == FunctionCode.RESP_QRY_FUTURES_MARGIN_RATE.value) {
            return JsonUtils.getObjectListFromJson(message.getBody(), FuturesInstrumentMarginRateBean.class);
        } else if (message.getFc() == FunctionCode.RESP_QRY_FUTURES_COMMISSION_RATE.value) {
            return JsonUtils.getObjectListFromJson(message.getBody(), FuturesInstrumentCommissionRateBean.class);
        } else if (message.getFc() == FunctionCode.RESP_QRY_INTEGRATED_NEGOTIATE_QUOTE_RECEIVE.value) {
            return JsonUtils.getObjectListFromJson(message.getBody(), NegotiateQuoteReceiveBean.class);
        } else
            throw new Exception("unknown protocol function code:" + message.getFc());
    }
    /*******************************************************************************/
    /***************************** request *****************************************/
    /*******************************************************************************/

    @Override
    public List<Entrust> reqEntrusts(String accountId) throws Exception {
        return (List<Entrust>) syncWrite(FunctionCode.REQ_QRY_ENTRUST, accountId);
    }

    @Override
    public List<IntegratedEntrust> reqIntegratedEntrusts(String accountId) throws Exception {
        return (List<IntegratedEntrust>) syncWrite(FunctionCode.REQ_QRY_INTEGRATED_ENTRUST, accountId);
    }

    @Override
    public List<Trade> reqTrades(String accountId) throws Exception {
        return (List<Trade>) syncWrite(FunctionCode.REQ_QRY_TRADE, accountId);
    }

    @Override
    public void entrust(Entrust entrust) {
        logger.info("send entrust:{}", entrust.getId());
        asyncWrite(FunctionCode.REQ_ENTRUST, entrust);
    }

    @Override
    public void integratedEntrust(IntegratedEntrust integratedEntrust) {
        logger.info("send integratedEntrust:{}", integratedEntrust.getId());
        asyncWrite(FunctionCode.REQ_INTEGRATED_ENTRUST, integratedEntrust);
    }

    @Override
    public void cancelEntrust(Entrust entrust, EntrustInterface cancelEntrust) {
        CancelEntrustBean cancelEntrustBean = getCancelEntrustBean(entrust, cancelEntrust);
        asyncWrite(FunctionCode.REQ_CANCEL_ENTRUST, cancelEntrustBean);
    }

    private CancelEntrustBean getCancelEntrustBean(EntrustInterface entrust, EntrustInterface cancelEntrust) {
        CancelEntrustBean cancelEntrustBean = new CancelEntrustBean();
        cancelEntrustBean.setId(entrust.getId());
        cancelEntrustBean.setBrokerEntrustId(entrust.getBrokerEntrustId());
        cancelEntrustBean.setMarket(entrust.getMarket());
        cancelEntrustBean.setEntrustProp(entrust.getEntrustProp());
        cancelEntrustBean.setTradingDay(entrust.getTradingDay());
        cancelEntrustBean.setEntrustStatus(entrust.getEntrustStatus());
        cancelEntrustBean.setBsFlag(entrust.getBsFlag());
        cancelEntrustBean.setEntrustBs(entrust.getEntrustBs());
        cancelEntrustBean.setAccountId(entrust.getAccountId());
        cancelEntrustBean.setStockCode(entrust.getStockCode());
        cancelEntrustBean.setEntrustVolume(entrust.getEntrustVolume());
        cancelEntrustBean.setTradedVolume(entrust.getTradedVolume());
        cancelEntrustBean.setIsFullPrice(entrust.getIsFullPrice());
        cancelEntrustBean.setEntrustPrice(entrust.getEntrustPrice());
        cancelEntrustBean.setFullPrice(entrust.getFullPrice());
        cancelEntrustBean.setCancelId(cancelEntrust.getId());
        cancelEntrustBean.setCancelBrokerEntrustId(cancelEntrust.getBrokerEntrustId());
        return cancelEntrustBean;
    }

    @Override
    public void cancelIntegratedEntrust(IntegratedEntrust integratedEntrust, IntegratedEntrust cancelEntrust) {
        CancelEntrustBean cancelEntrustBean = getCancelEntrustBean(integratedEntrust, cancelEntrust);
        asyncWrite(FunctionCode.REQ_CANCEL_INTEGRATED_ENTRUST, cancelEntrustBean);
    }

    @Override
    public ResultBean reqLoginAccount(AccountLoginReqBean accountLoginReqBean) {
        try {
            ResultBean resultBean = (ResultBean) syncWrite(FunctionCode.REQ_ACCOUNT_LOGIN, accountLoginReqBean);
            // 账户登录成功，记录下登录成功的账户
            JSONObject data = (JSONObject) resultBean.getData();
            Account account = JsonUtils.getObjectFromJson(data.toString(), Account.class);
            if (resultBean.getErrorCode() == ErrorCode.SUCCESS.value) {
                logger.info("account {} login success", account.getId());
                tradeAgentManager.addAccounts(this, account.getId());
                accountMap.put(account.getId(), account);
                accountManager.addAccounts(account);
                //登录成功后发送重传请求
                terminalHelper.reqReissueEntrust(account.getId());
                terminalHelper.reqReissueTrade(account.getId());
                //账户持仓缓存
                accountPositionHelper.positionCache(this, account.getId());
            } else {
                logger.info("account {} login failed {} -- {}", account.getId(), resultBean.getErrorCode(), resultBean.getErrorMsg());
            }
            return resultBean;
        } catch (Exception e) {
            logger.error("reqLoginAccount: {} error:{}", JsonUtils.getJsonFromObject(accountLoginReqBean), e);
            return new ResultBean(ErrorCode.TERMINAL_API_ERROR, e.getMessage());
        }
    }

    @Override
    public void reqLogoutAccount(Account account) {
        asyncWrite(FunctionCode.REQ_ACCOUNT_LOGOUT, account);
    }

    @Override
    public List<IntegratedQuote> reqIntegratedQuotes(QuoteQryBean quoteQryBean) throws Exception {
        return (List<IntegratedQuote>) syncWrite(FunctionCode.REQ_QRY_INTEGRATED_QUOTE, quoteQryBean);
    }

    @Override
    public int reqEnable(EnableQryBean qryBean) throws Exception {
        return (int) syncWrite(FunctionCode.REQ_QRY_TRADE_ENABLE, qryBean);
    }

    @Override
    public List<HistoryTrade> reqHistoryTrade(HistoryQryBean historyQryBean) throws Exception {
        return (List<HistoryTrade>) syncWrite(FunctionCode.REQ_QRY_HISTORY_TRADE, historyQryBean);
    }

    @Override
    public List<AccountFinance> reqCustomerFinance(String accountId) throws Exception {
        return (List<AccountFinance>) syncWrite(FunctionCode.REQ_QRY_FINANCE, accountId);
    }

    @Override
    public List<PositionBean> reqAccountPosition(PositionQueryBean positionQueryBean) throws Exception {
        return (List<PositionBean>) syncWrite(FunctionCode.REQ_QRY_POSITION, positionQueryBean);
    }

    @Override
    public List<Instrument> reqInstrumentInfo(InstrumentQryBean instrumentQryBean) throws Exception {
        return (List<Instrument>) syncWrite(FunctionCode.REQ_QRY_INSTRUMENT_INFO, instrumentQryBean);
    }

    @Override
    public List<PledgeDiscount> reqPledgeDiscount(String accountId, String market, String stockCode) throws Exception {
        QuoteQryBean quoteQryBean = new QuoteQryBean();
        quoteQryBean.setAccountId(accountId);
        quoteQryBean.setMarket(market);
        quoteQryBean.setStockCode(stockCode);
        return (List<PledgeDiscount>) syncWrite(FunctionCode.REQ_PLEDGE_DISCOUNT, quoteQryBean);
    }

    @Override
    public List<StandardBonds> reqStandardBonds(StandardBondQryBean standardBondQryBean) throws Exception {
        return (List<StandardBonds>) syncWrite(FunctionCode.REQ_STANDARD_BONDS, standardBondQryBean);
    }
    /*******************************************************************************/
    /***************************** response ****************************************/
    /*******************************************************************************/

    @Override
    public void onTerminalLogin(TerminalLogin terminalLogin) {
        logger.info("receive connect onTerminalLogin {}", JsonUtils.getJsonFromObject(terminalLogin));
        TerminalLoginRet terminalLoginRet = new TerminalLoginRet();
        asyncWrite(FunctionCode.TERMINAL_LOGIN, terminalLoginRet);
        // 保存登陆终端
        terminalName = terminalLogin.getTerminalName();
        agentStatus = AgentStatus.LOGIN;
        tradeAgentManager.addTradeAgent(this);
    }

    @Override
    public void onRtnEntrust(Entrust entrust) {
        if (Strings.isNullOrEmpty(entrust.getId())) {
            if (Objects.nonNull(entrust.getLocalEntrustId()) || Objects.nonNull(entrust.getBrokerEntrustId())) {
                String id = tradeHelper.getEntrustId(entrust);
                if (Objects.nonNull(id)) {
                    entrust.setId(id);
                } else {
                    // 缓存对当前委托--- 处理委托回报在主推之后回来的情况， 委托回报回来之后再次补发通知（ufx）
                    String accountId = entrust.getAccountId();
                    String localEntrustId = entrust.getLocalEntrustId();
                    String brokerEntrustId = entrust.getBrokerEntrustId();
                    tradeHelper.setEntrustNotifyMap(accountId + "_" + brokerEntrustId, entrust);
                    logger.warn("entrust not has id, accountId - localEntrustId - brokerEntrustId {} - {} - {}", accountId, localEntrustId, brokerEntrustId);
                }
            } else {
                logger.warn("entrust not has id and localEntrustId, accountId - brokerEntrustId {} - {}", entrust.getAccountId(), entrust.getBrokerEntrustId());
            }
        }
        if (Objects.nonNull(entrust.getId())) {
            TradeMsgReceiver msgReceiver = msgReceiverManager.getReceiverByEntrustId(entrust.getId());
            if (Objects.nonNull(msgReceiver)) {
                msgReceiver.handleRtnEntrustDetail(entrust);
            } else {
                logger.warn("entrust not found receiver: id - brokerEntrustId {} - {}", entrust.getId(), entrust.getBrokerEntrustId());
            }
        }
    }

    @Override
    public void onRtnIntegratedEntrust(IntegratedEntrust integratedEntrust) {
        if (Strings.isNullOrEmpty(integratedEntrust.getId())) {
            if (Objects.nonNull(integratedEntrust.getLocalEntrustId()) || Objects.nonNull(integratedEntrust.getBrokerEntrustId())) {
                String id = tradeHelper.getIntegratedEntrustId(integratedEntrust);
                if (Objects.nonNull(id)) {
                    integratedEntrust.setId(id);
                } else {
                    String accountId = integratedEntrust.getAccountId();
                    String localEntrustId = integratedEntrust.getLocalEntrustId();
                    String brokerEntrustId = integratedEntrust.getBrokerEntrustId();
                    tradeHelper.setEntrustNotifyMap(accountId + "_" + brokerEntrustId, integratedEntrust);
                    logger.warn("integratedEntrust not has id, accontId - localEntrustId - brokerEntrustId {} - {} - {}", accountId, localEntrustId, brokerEntrustId);
                }
            } else {
                logger.warn("integratedEntrust not has id and localEntrustId, accountId - brokerEntrustId {} - {}", integratedEntrust.getAccountId(), integratedEntrust.getBrokerEntrustId());
            }
        }
        if (Objects.nonNull(integratedEntrust.getId())) {
            TradeMsgReceiver msgReceiver = msgReceiverManager.getReceiverByIntegratedEntrustId(integratedEntrust.getId());
            if (Objects.nonNull(msgReceiver)) {
                msgReceiver.handleRtnIntegratedEntrust(integratedEntrust);
            } else {
                logger.warn("integratedEntrust not found receiver: id - brokerEntrustId {} - {}", integratedEntrust.getId(), integratedEntrust.getBrokerEntrustId());
            }
        }
    }

    @Override
    public void onRtnTrade(Trade tradeRecord) {
        logger.info("onRtnTrade()，成交回报信息：{}-{}-{}", tradeRecord.getBrokerEntrustId(), tradeRecord.getBrokerTradeId(),
                JsonUtils.getJsonFromObject(tradeRecord));
        String key = tradeRecord.getAccountId() + "_" + tradeRecord.getBrokerEntrustId();
        String entrustId = tradeHelper.getEntrustMap().get(key);
        logger.info("onRtnTrade()，委托编号：{}",  entrustId);
        if (!Strings.isNullOrEmpty(entrustId)) {
            TradeMsgReceiver msgReceiver = msgReceiverManager.getTradeReceiverByEntrustId(entrustId);
            if (Objects.nonNull(msgReceiver)) {
                logger.info("处理成交回报信息：{} - {}", tradeRecord.getBrokerEntrustId(), JsonUtils.getJsonFromObject(tradeRecord));
                msgReceiver.handleRtnTradeRecord(tradeRecord);
            } else {
                logger.warn("tradeRecord not found receiver: {}", JsonUtils.getJsonFromObject(tradeRecord));
            }
        } else {
            logger.info("tradeRecord return before entrust {} - {}", tradeRecord.getAccountId(), tradeRecord.getBrokerEntrustId());
            Map<String, List<Trade>> tradeMap = tradeHelper.getTradeMap();
            List<Trade> tradeList = tradeMap.getOrDefault(key, new ArrayList<>());
            tradeList.add(tradeRecord);
            tradeMap.put(key, tradeList);
        }
    }

    @Override
    public void onRtnRTGSStatus(RTGSStatusBean rtgsStatusBean) {
        tradeHelper.pushRTGSStatusToInvest(rtgsStatusBean);
    }


    @Override
    public void onRspEntrust(TerminalId terminalId) {
        // 缓存已经回来且有柜台委托编号的委托
        if (!Strings.isNullOrEmpty(terminalId.getBrokerEntrustId())) {
            String key = terminalId.getAccountId() + "_" + terminalId.getBrokerEntrustId();
            tradeHelper.getEntrustMap().put(key, terminalId.getId());
        }
        TradeMsgReceiver msgReceiver = msgReceiverManager.getReceiverByEntrustId(terminalId.getId());
        if (Objects.nonNull(msgReceiver)) {
            msgReceiver.handleRthTerminalId(terminalId);
            // 处理主推委托 在回报之前的情况, 通知补发
            String key = terminalId.getAccountId() + "_" + terminalId.getBrokerEntrustId();
            EntrustInterface entrustInterface = tradeHelper.getEntrustNotifyByAccountIdAndBrokerEntrustId(key);
            if (Objects.nonNull(entrustInterface)) {
                tradeHelper.handleTradeNotifyEntrustRspBeforeOnEntrustRsp(msgReceiver, key, MsgType.NOTIFY_ENTRUST);
            }

            //  处理成交回报 在委托回报之前的情况
            if (tradeHelper.getTradeMap().size() > 0)
                tradeHelper.handleTradeRspBeforeEntrustRsp(msgReceiver, tradeHelper.getTradeMap(), terminalId);
        } else {
            logger.warn("onRspEntrust terminalId not found receiver: id - brokerEntrustId {} - {}", terminalId.getId(), terminalId.getBrokerEntrustId());
        }
    }

    @Override
    public void onRspIntegratedEntrust(TerminalId terminalId) {
        // 缓存已经回来且有柜台委托编号的委托
        if (!Strings.isNullOrEmpty(terminalId.getBrokerEntrustId())) {
            String key = terminalId.getAccountId() + "_" + terminalId.getBrokerEntrustId();
            tradeHelper.getEntrustMap().put(key, terminalId.getId());
        }
        TradeMsgReceiver msgReceiver = msgReceiverManager.getReceiverByIntegratedEntrustId(terminalId.getId());
        if (Objects.nonNull(msgReceiver)) {
            msgReceiver.addMsg(MsgType.RESP_INTEGRATED, terminalId);
            // 处理主推委托 在回报之前的情况, 通知补发
            String key = terminalId.getAccountId() + "_" + terminalId.getBrokerEntrustId();
            EntrustInterface entrustInterface = tradeHelper.getEntrustNotifyByAccountIdAndBrokerEntrustId(key);
            if (Objects.nonNull(entrustInterface)) {
                tradeHelper.handleTradeNotifyEntrustRspBeforeOnEntrustRsp(msgReceiver, key, MsgType.NOTIFY_INTEGRATED);
            }

            //  处理成交回报 在委托回报之前的情况
            if (tradeHelper.getTradeMap().size() > 0)
                tradeHelper.handleTradeRspBeforeEntrustRsp(msgReceiver, tradeHelper.getTradeMap(), terminalId);
        } else {
            logger.warn("onRspIntegratedEntrust terminalId not found receiver: id - brokerEntrustId {} - {}", terminalId.getId(), terminalId.getBrokerEntrustId());
        }
    }

    @Override
    public void onRspCancelEntrust(CancelEntrustBean cancelEntrustBean) {
        if (!Strings.isNullOrEmpty(cancelEntrustBean.getCancelBrokerEntrustId())) {
            String key = cancelEntrustBean.getAccountId() + "_" + cancelEntrustBean.getCancelBrokerEntrustId();
            tradeHelper.getEntrustMap().put(key, cancelEntrustBean.getCancelId());
        }
        TradeMsgReceiver msgReceiver = msgReceiverManager.getReceiverByEntrustId(cancelEntrustBean.getCancelId());
        if (Objects.nonNull(msgReceiver)) {
            msgReceiver.addMsg(MsgType.RESP_CANCEL, cancelEntrustBean);
            // 处理主推委托 在回报之前的情况, 通知补发
            String key = cancelEntrustBean.getAccountId() + "_" + cancelEntrustBean.getCancelBrokerEntrustId();
            EntrustInterface entrustInterface = tradeHelper.getEntrustNotifyByAccountIdAndBrokerEntrustId(key);
            if (Objects.nonNull(entrustInterface)) {
                tradeHelper.handleTradeNotifyEntrustRspBeforeOnEntrustRsp(msgReceiver, key, MsgType.NOTIFY_ENTRUST);
            }

            //  处理成交回报 在委托回报之前的情况
            if (tradeHelper.getTradeMap().size() > 0) {
                tradeHelper.handleCancelTradeRspBeforeEntrustRsp(msgReceiver, tradeHelper.getTradeMap(), cancelEntrustBean);
            }
        } else {
            logger.warn("onRspCancelEntrust terminalId not found receiver: id - brokerEntrustId {} - {}", cancelEntrustBean.getId(), cancelEntrustBean.getBrokerEntrustId());
        }
    }

    @Override
    public void onRspCancelIntegratedEntrust(CancelEntrustBean cancelEntrustBean) {
        if (!Strings.isNullOrEmpty(cancelEntrustBean.getCancelBrokerEntrustId())) {
            String key = cancelEntrustBean.getAccountId() + "_" + cancelEntrustBean.getCancelBrokerEntrustId();
            tradeHelper.getEntrustMap().put(key, cancelEntrustBean.getCancelId());
        }
        TradeMsgReceiver msgReceiver = msgReceiverManager.getReceiverByIntegratedEntrustId(cancelEntrustBean.getCancelId());
        if (Objects.nonNull(msgReceiver)) {
            msgReceiver.addMsg(MsgType.RESP_CANCEL, cancelEntrustBean);
            // 处理主推委托 在回报之前的情况, 通知补发
            String key = cancelEntrustBean.getAccountId() + "_" + cancelEntrustBean.getCancelBrokerEntrustId();
            EntrustInterface entrustInterface = tradeHelper.getEntrustNotifyByAccountIdAndBrokerEntrustId(key);
            if (Objects.nonNull(entrustInterface)) {
                tradeHelper.handleTradeNotifyEntrustRspBeforeOnEntrustRsp(msgReceiver, key, MsgType.NOTIFY_INTEGRATED);
            }
            //  处理成交回报 在委托回报之前的情况
            if (tradeHelper.getTradeMap().size() > 0) {
                tradeHelper.handleCancelTradeRspBeforeEntrustRsp(msgReceiver, tradeHelper.getTradeMap(), cancelEntrustBean);
            }
        } else {
            logger.warn("onRspCancelIntegratedEntrust terminalId not found receiver: id - brokerEntrustId {} - {}", cancelEntrustBean.getId(), cancelEntrustBean.getBrokerEntrustId());
        }
    }

    @Override
    public void onRspAccountLogin(ResultBean resultBean) {
    }

    @Override
    public void onRspQryCustomerFinance(List<AccountFinance> accountFinanceList) {
    }

    @Override
    public void OnRspQryMarginRate(List<FuturesInstrumentMarginRateBean> futuresInstrumentMarginRateBeans) {

    }

    @Override
    public List<IntegratedBulkQuoteBean> reqIntegratedBulkQuote(IntegratedBulkQuoteQryBean integratedBulkQuoteQryBean) throws Exception {
        return (List<IntegratedBulkQuoteBean>) syncWrite(FunctionCode.REQ_QRY_INTEGRATED_BULK_QUOTE, integratedBulkQuoteQryBean);
    }

    @Override
    public List<IntegratedBulkCloseQuoteBean> reqIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean) throws Exception {
        return (List<IntegratedBulkCloseQuoteBean>) syncWrite(FunctionCode.REQ_QRY_INTEGRATED_BULK_CLOSE_QUOTE, integratedBulkCloseQuoteQryBean);
    }

    @Override
    public List<IntegratedRepurchaseQuoteBean> reqIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean) throws Exception {
        return (List<IntegratedRepurchaseQuoteBean>) syncWrite(FunctionCode.REQ_QRY_INTEGRATED_REPURCHASE_QUOTE, integratedRepurchaseQuoteQryBean);
    }

    @Override
    public List<IntegratedFixedIncomeQuoteBean> reqIntegratedFixedIncomeQuote(IntegratedFixedIncomeQuoteQryBean integratedFixedIncomeQuoteQryBean) throws Exception {
        return (List<IntegratedFixedIncomeQuoteBean>) syncWrite(FunctionCode.REQ_QRY_INTEGRATED_FIXED_INCOME_QUOTE, integratedFixedIncomeQuoteQryBean);
    }

    @Override
    public void reqReissueEntrust(ReissueEntrustQryBean reissueEntrustQryBean) {
        asyncWrite(FunctionCode.REQ_ACCOUNT_ENTRUST_REISSUE, reissueEntrustQryBean);
    }

    @Override
    public void reqReissueTrade(ReissueTradeQryBean reissueTradeQryBean) {
        asyncWrite(FunctionCode.REQ_ACCOUNT_TRADE_REISSUE, reissueTradeQryBean);
    }

    @Override
    public List<FuturesInstrument> reqFuturesInstrument(FuturesInstrumentQryBean futuresInstrumentQryBean) throws Exception {
        return (List<FuturesInstrument>) syncWrite(FunctionCode.REQ_QRY_FUTURES_INSTRUMENT, futuresInstrumentQryBean);
    }

    @Override
    public List<FuturesPositionBean> reqAccountFuturesPosition(FuturesPositionQryBean futuresPositionQryBean) throws Exception {
        return (List<FuturesPositionBean>) syncWrite(FunctionCode.REQ_QRY_FUTURES_POSITION, futuresPositionQryBean);
    }

    @Override
    public List<FuturesPositionDetailBean> reqAccountFuturesPositionDetail(FuturesPositionDetailQryBean futuresPositionDetailQryBean) throws Exception {
        return (List<FuturesPositionDetailBean>) syncWrite(FunctionCode.REQ_QRY_FUTURES_POSITION_DETAIL, futuresPositionDetailQryBean);
    }

    @Override
    public void reqAccountFuturesMarginRate(FuturesInstrumentMarginRateQryBean futuresInstrumentMarginRateQryBean) throws Exception {
        asyncWrite(FunctionCode.REQ_QRY_FUTURES_MARGIN_RATE, futuresInstrumentMarginRateQryBean);
    }

    @Override
    public void reqAccountFuturesCommissionRate(FuturesInstrumentCommissionRateQryBean futuresInstrumentCommissionRateQryBean) throws Exception {
        asyncWrite(FunctionCode.REQ_QRY_FUTURES_COMMISSION_RATE, futuresInstrumentCommissionRateQryBean);
    }

	@Override
	public List<NegotiateQuoteReceiveBean> reqNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean)
			throws Exception {
		return (List<NegotiateQuoteReceiveBean>) syncWrite(FunctionCode.REQ_QRY_INTEGRATED_NEGOTIATE_QUOTE_RECEIVE, negotiateQuoteQueryBean);
	}
}
