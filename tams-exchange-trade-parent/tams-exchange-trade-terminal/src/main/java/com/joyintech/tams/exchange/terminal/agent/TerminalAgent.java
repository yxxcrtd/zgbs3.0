package com.joyintech.tams.exchange.terminal.agent;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.constant.joyin.DictCodeListExt3;
import com.joyintech.tams.exchange.common.enums.invest.InvestAssetTypeEnum;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.terminal.trade.cds.CDSTradeClient;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.util.TypeUtils;
import com.google.common.collect.Maps;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.bean.terminal.RTGSStatusBean;
import com.joyintech.tams.exchange.common.db.domain.Account;
import com.joyintech.tams.exchange.common.db.domain.CancelEntrustBean;
import com.joyintech.tams.exchange.common.db.domain.Entrust;
import com.joyintech.tams.exchange.common.db.domain.EntrustInterface;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.db.domain.ServiceNode;
import com.joyintech.tams.exchange.common.db.domain.Trade;
import com.joyintech.tams.exchange.common.net.agent.Agent;
import com.joyintech.tams.exchange.common.net.protocol.FunctionCode;
import com.joyintech.tams.exchange.common.net.protocol.ProtocolMessage;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.common.utils.SpringContextUtils;
import com.joyintech.tams.exchange.terminal.client.TcpClient;
import com.joyintech.tams.exchange.terminal.constant.AccountLoginStatus;
import com.joyintech.tams.exchange.terminal.constant.BrokerReissueStatus;
import com.joyintech.tams.exchange.terminal.constant.ServiceReissueStatus;
import com.joyintech.tams.exchange.terminal.trade.EntrustIdManager;
import com.joyintech.tams.exchange.terminal.trade.IFuturesTradeRequest;
import com.joyintech.tams.exchange.terminal.trade.ITradeRequest;
import com.joyintech.tams.exchange.terminal.trade.TradeResponseImpl;
import com.joyintech.tams.exchange.terminal.trade.kcbp.KCBPTradeClient;

import io.netty.channel.Channel;

@Component
@Scope("prototype")
public class TerminalAgent extends Agent implements ITerminalMsgHandler, ITerminalMsgNotify {

    private static final Logger logger = LoggerFactory.getLogger(TerminalAgent.class);

    private static Map<String, Long> requestIdAccountMap = Maps.newConcurrentMap();//AccountId-requestId
    private static Map<String, Long> requestIdEntrustMap = Maps.newConcurrentMap();//EntrustId-requestId
    private AtomicInteger localOffset = new AtomicInteger(0);
    private static int timeZoneOffset = 0;

    private Map<String, Map<String, ReissueEntrustBean>> reissueEntrustBeanMap = Maps.newConcurrentMap();
    private Map<String, Map<String, ReissueTradeBean>> reissueTradeBeanMap = Maps.newConcurrentMap();

    private Map<String, ServiceReissueStatus> serviceReissueStatusMap = Maps.newConcurrentMap();
    private Map<String, BrokerReissueStatus> brokerReissueStatusMap = Maps.newConcurrentMap();

    private Map<String, Map<String, EntrustInterface>> entrustInterfaceMapByBroker = Maps.newConcurrentMap();
    private Map<String, List<Trade>> tradeListMapByBroker = new HashMap<>();

    public void clearReissueCache(String accountId) {
        reissueEntrustBeanMap.remove(accountId);
        reissueTradeBeanMap.remove(accountId);
        serviceReissueStatusMap.remove(accountId);
        brokerReissueStatusMap.remove(accountId);
        entrustInterfaceMapByBroker.remove(accountId);
        tradeListMapByBroker.remove(accountId);
    }

    static {
        timeZoneOffset = TimeZone.getDefault().getRawOffset();
    }

    public void addRequestIdEntrustMap(String entrustId, Long requestId) {
        requestIdEntrustMap.put(entrustId, requestId);
    }

    private String genLocalId() {
        int tempOffSet = localOffset.incrementAndGet();
        if (tempOffSet > 8999) {
            tempOffSet = 0;
            localOffset.set(0);
        }
        long beginMill = System.currentTimeMillis() / (1000 * 3600 * 24) * (1000 * 3600 * 24) - timeZoneOffset;
        return String.format("%s%04d", (System.currentTimeMillis() - beginMill) / 1000, tempOffSet);
    }

    public Long getRequestIdByEntrustId(String id) {
        return requestIdEntrustMap.get(id);
    }

    public static void deleteRequestIdByEntrustId(String id) {
        requestIdEntrustMap.remove(id);
    }

    @Value("${agent.name}")
    private String terminalName;

    @Value("${agent.type}")
    private String agentType;

    @Autowired
    private TradeResponseImpl tradeResponse;

    @Autowired
    private TcpClient tcpClient;

    @Autowired
    private EntrustIdManager entrustIdManager;

    private List<ServiceNode> serviceNodeList;

    private Map<String, ITradeRequest> tradeRequestMap = new HashMap<>();//记录账户对应的ITradeRequest

    private Set<ServerExtendInfo> serverExtendInfoSet = new HashSet<>();//记录营业部信息

    private Map<String, ITradeRequest> agentTypeMap = new HashMap<>();//记录终端对应的ITradeRequest

    private List<Account> accountList = new ArrayList<>();//记录账号

    // 记录server掉线后，未回复的trade回报
    private List<RepairConnectionMsg> respList = new ArrayList<>();

    public static Long getRequestId(String accountId) {
        if (accountId == null) {
            return null;
        }
        return requestIdAccountMap.get(accountId);
    }

    public List<RepairConnectionMsg> getRespList() {
        return respList;
    }

    @Override
    public void connEstablished(Channel channel) {
        logger.info("terminal connEstablished start connect");
        this.channel = channel;
        TerminalLogin terminalLogin = new TerminalLogin();
        terminalLogin.setTerminalName(terminalName);
        asyncWrite(FunctionCode.TERMINAL_LOGIN, terminalLogin);
        sendHeartBeat();
        logger.info("terminal connEstablished finish connect");
    }

    @Override
    public void connClosed() {
        channel.close();
        logger.info("remote address:{} disconnected", channel.remoteAddress().toString());
    }

    /**
     * 发送心跳包
     */
    protected void sendHeartBeat() {
        if (channel.isOpen()) {
            asyncWrite(FunctionCode.HEARTBEAT, null);
            this.channel.eventLoop().schedule(this::sendHeartBeat, 10, TimeUnit.SECONDS);
        } else {
            logger.info("------连接已断开----- 正在准备重连----");
            tcpClient.reStart();
        }
    }

    /**
     * 处理异步消息
     *
     * @param requestId    请求编号
     * @param functionCode 功能编号
     * @param message      消息
     */
    @Override
    protected void handleAsyncMessage(long requestId, int functionCode, Object message) {
        logger.debug("TerminalAgent handleAsyncMessage requestId:{} functionCode:{} message:{}", requestId, functionCode, JsonUtils.getJsonFromObject(message));
        if (functionCode == FunctionCode.REQ_ACCOUNT_LOGIN.value) { //终端登陆
            onReqLoginAccount((AccountLoginReqBean) message, requestId);
        } else if (functionCode == FunctionCode.REQ_ACCOUNT_LOGOUT.value) { // 终端退出
            onReqLogoutAccount((Account) message, requestId);
        } else if (functionCode == FunctionCode.REQ_ENTRUST.value) {    // 下达订单
            onReqEntrust((Entrust) message, requestId);
        } else if (functionCode == FunctionCode.REQ_INTEGRATED_ENTRUST.value) { // 下达综合业务订单
            onReqIntegratedEntrust((IntegratedEntrust) message, requestId);
        } else if (functionCode == FunctionCode.REQ_CANCEL_ENTRUST.value) { // 取消订单
            onReqCancelEntrust((CancelEntrustBean) message, requestId);
        } else if (functionCode == FunctionCode.REQ_CANCEL_INTEGRATED_ENTRUST.value) {  // 取消综合业务订单
            onReqCancelIntegratedEntrust((CancelEntrustBean) message, requestId);
        } else if (functionCode == FunctionCode.REQ_QRY_TRADE.value) {  // 查询成交
            onReqQryTrades((String) message, requestId);
        } else if (functionCode == FunctionCode.REQ_QRY_ENTRUST.value) {    //查询订单
            onReqQryEntrusts((String) message, requestId);
        } else if (functionCode == FunctionCode.REQ_QRY_INTEGRATED_QUOTE.value) {   // 查询大宗行情
            onReqIntegratedQuotes((QuoteQryBean) message, requestId);
        } else if (functionCode == FunctionCode.REQ_QRY_TRADE_ENABLE.value) { // 查询最大交易数量
            onReqEnable((EnableQryBean) message, requestId);
        } else if (functionCode == FunctionCode.REQ_QRY_HISTORY_TRADE.value) {  // 查询历史交易明细
            onReqHistoryTrade((HistoryQryBean) message, requestId);
        } else if (functionCode == FunctionCode.REQ_QRY_FINANCE.value) {   // 查询客户资产
            onReqAccountFinance((String) message, requestId);
        } else if (functionCode == FunctionCode.REQ_QRY_POSITION.value) {   // 查询客户资产
            onReqAccountPosition((PositionQueryBean) message, requestId);
        } else if (functionCode == FunctionCode.REQ_QRY_INSTRUMENT_INFO.value) {    // 查询合约数据
            onReqInstrumentInfo((InstrumentQryBean) message, requestId);
        } else if (functionCode == FunctionCode.TERMINAL_LOGIN.value) { // 终端登录
            TerminalLoginRet terminalLoginRet = (TerminalLoginRet) message;
            onTerminalLogin(terminalLoginRet.getAccountMap());
        } else if (functionCode == FunctionCode.HEARTBEAT.value) {  // 心跳
            asyncWrite(FunctionCode.ACK, 0, 0, (String) null);
        } else if (functionCode == FunctionCode.REQ_PLEDGE_DISCOUNT.value) {    // 债券质押率
            QuoteQryBean quoteQryBean = (QuoteQryBean) message;
            onReqPledgeDiscount(quoteQryBean.getAccountId(), quoteQryBean.getMarket(), quoteQryBean.getStockCode(), requestId);
        } else if (functionCode == FunctionCode.REQ_STANDARD_BONDS.value) { // 标准券查询
            onReqStandardBonds((StandardBondQryBean) message, requestId);
        } else if (functionCode == FunctionCode.SERVICE_NODE.value) {
            onServiceNode((List<ServiceNode>) message, requestId);
        } else if (functionCode == FunctionCode.REQ_QRY_INTEGRATED_BULK_QUOTE.value) {
            queryIntegratedBulkQuote((IntegratedBulkQuoteQryBean) message, requestId);
        } else if (functionCode == FunctionCode.REQ_QRY_INTEGRATED_BULK_CLOSE_QUOTE.value) {
            queryIntegratedBulkCloseQuote((IntegratedBulkCloseQuoteQryBean) message, requestId);
        } else if (functionCode == FunctionCode.REQ_QRY_INTEGRATED_REPURCHASE_QUOTE.value) {
            queryIntegratedRepurchaseQuote((IntegratedRepurchaseQuoteQryBean) message, requestId);
        } else if (functionCode == FunctionCode.REQ_QRY_INTEGRATED_FIXED_INCOME_QUOTE.value) {
            queryIntegratedFixedIncomeQuote((IntegratedFixedIncomeQuoteQryBean) message, requestId);
        } else if (functionCode == FunctionCode.REQ_ACCOUNT_ENTRUST_REISSUE.value) {
            reqReissueEntrust((ReissueEntrustQryBean) message, requestId);
        } else if (functionCode == FunctionCode.REQ_ACCOUNT_TRADE_REISSUE.value) {
            reqReissueTrade((ReissueTradeQryBean) message, requestId);
        } else if (functionCode == FunctionCode.REQ_QRY_FUTURES_INSTRUMENT.value) {
            queryFuturesInstrument((FuturesInstrumentQryBean) message, requestId);
        } else if (functionCode == FunctionCode.REQ_QRY_FUTURES_POSITION.value) {
            queryFuturesPosition((FuturesPositionQryBean) message, requestId);
        } else if (functionCode == FunctionCode.REQ_QRY_FUTURES_POSITION_DETAIL.value) {
            queryFuturesPositionDetail((FuturesPositionDetailQryBean) message, requestId);
        } else if (functionCode == FunctionCode.REQ_QRY_FUTURES_MARGIN_RATE.value) {
            queryFuturesInstrumentMarginRate((FuturesInstrumentMarginRateQryBean) message, requestId);
        } else if (functionCode == FunctionCode.REQ_QRY_FUTURES_COMMISSION_RATE.value) {
            queryFuturesInstrumentCommissionRate((FuturesInstrumentCommissionRateQryBean) message, requestId);
        } else if (functionCode == FunctionCode.REQ_QRY_INTEGRATED_NEGOTIATE_QUOTE_RECEIVE.value) {
            queryNegotiateQuoteReceive((NegotiateQuoteQueryBean) message, requestId);
        } else if (functionCode != FunctionCode.ACK.value) {    // 未知消息
            logger.warn("unknown functionCode:{} message:{}", functionCode, JsonUtils.getJsonFromObject(message));
        }
    }

    /**
     * 获取响应JSON数据
     *
     * @param message 消息
     * @return object 响应的JSON数据
     * @throws RuntimeException 未知功能编号
     */
    @Override
    protected Object getResponseObjectFromMessage(ProtocolMessage message) throws Exception {
        logger.debug("TerminalAgent getResponseObjectFromMessage functionCode:{} message:{}", message.getFc(), message.getBody());
        if (message.getFc() == FunctionCode.REQ_ACCOUNT_LOGIN.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), AccountLoginReqBean.class);
        } else if (message.getFc() == FunctionCode.REQ_ACCOUNT_LOGOUT.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), Account.class);
        } else if (message.getFc() == FunctionCode.REQ_ENTRUST.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), Entrust.class);
        } else if (message.getFc() == FunctionCode.REQ_INTEGRATED_ENTRUST.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), IntegratedEntrust.class);
        } else if (message.getFc() == FunctionCode.REQ_CANCEL_ENTRUST.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), CancelEntrustBean.class);
        } else if (message.getFc() == FunctionCode.REQ_CANCEL_INTEGRATED_ENTRUST.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), CancelEntrustBean.class);
        } else if (message.getFc() == FunctionCode.REQ_QRY_TRADE.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), String.class);
        } else if (message.getFc() == FunctionCode.REQ_QRY_ENTRUST.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), String.class);
        } else if (message.getFc() == FunctionCode.REQ_QRY_INTEGRATED_QUOTE.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), QuoteQryBean.class);
        } else if (message.getFc() == FunctionCode.REQ_QRY_TRADE_ENABLE.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), EnableQryBean.class);
        } else if (message.getFc() == FunctionCode.REQ_QRY_HISTORY_TRADE.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), HistoryQryBean.class);
        } else if (message.getFc() == FunctionCode.REQ_QRY_CUSTOMER_FINANCE.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), String.class);
        } else if (message.getFc() == FunctionCode.REQ_QRY_INSTRUMENT_INFO.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), InstrumentQryBean.class);
        } else if (message.getFc() == FunctionCode.TERMINAL_LOGIN.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), TerminalLoginRet.class);
        } else if (message.getFc() == FunctionCode.ACK.value || message.getFc() == FunctionCode.HEARTBEAT.value) {
            return null;
        } else if (message.getFc() == FunctionCode.REQ_PLEDGE_DISCOUNT.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), QuoteQryBean.class);
        } else if (message.getFc() == FunctionCode.REQ_STANDARD_BONDS.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), StandardBondQryBean.class);
        } else if (message.getFc() == FunctionCode.SERVICE_NODE.value) {
            return JsonUtils.getObjectListFromJson(message.getBody(), ServiceNode.class);
        } else if (message.getFc() == FunctionCode.REQ_QRY_INTEGRATED_BULK_QUOTE.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), IntegratedBulkQuoteQryBean.class);
        } else if (message.getFc() == FunctionCode.REQ_QRY_INTEGRATED_BULK_CLOSE_QUOTE.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), IntegratedBulkCloseQuoteQryBean.class);
        } else if (message.getFc() == FunctionCode.REQ_QRY_INTEGRATED_REPURCHASE_QUOTE.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), IntegratedRepurchaseQuoteQryBean.class);
        } else if (message.getFc() == FunctionCode.REQ_QRY_INTEGRATED_FIXED_INCOME_QUOTE.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), IntegratedFixedIncomeQuoteQryBean.class);
        } else if (message.getFc() == FunctionCode.REQ_QRY_POSITION.value) {   // 查询客户资产
            return JsonUtils.getObjectFromJson(message.getBody(), PositionQueryBean.class);
        } else if (message.getFc() == FunctionCode.REQ_ACCOUNT_ENTRUST_REISSUE.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), ReissueEntrustQryBean.class);
        } else if (message.getFc() == FunctionCode.REQ_ACCOUNT_TRADE_REISSUE.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), ReissueTradeQryBean.class);
        } else if (message.getFc() == FunctionCode.REQ_QRY_FUTURES_INSTRUMENT.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), FuturesInstrumentQryBean.class);
        } else if (message.getFc() == FunctionCode.REQ_QRY_FUTURES_POSITION.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), FuturesPositionQryBean.class);
        } else if (message.getFc() == FunctionCode.REQ_QRY_FUTURES_POSITION_DETAIL.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), FuturesPositionDetailQryBean.class);
        } else if (message.getFc() == FunctionCode.REQ_QRY_FUTURES_MARGIN_RATE.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), FuturesInstrumentMarginRateQryBean.class);
        } else if (message.getFc() == FunctionCode.REQ_QRY_FUTURES_COMMISSION_RATE.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), FuturesInstrumentCommissionRateQryBean.class);
        } else if (message.getFc() == FunctionCode.REQ_QRY_INTEGRATED_NEGOTIATE_QUOTE_RECEIVE.value) {
            return JsonUtils.getObjectFromJson(message.getBody(), NegotiateQuoteQueryBean.class);
        } else {
            logger.warn("unknown functionCode:{} message:{}", message.getFc(), message.getBody());
            throw new Exception("unknown functionCode:" + message.getFc());
        }
    }

    @Override
    public void onReqQryEntrusts(String accountId, long requestId) {
        tradeRequestMap.get(accountId).reqEntrusts(accountId, requestId);
    }

    @Override
    public void onReqQryTrades(String accountId, long requestId) {
        tradeRequestMap.get(accountId).reqTrades(accountId, requestId);
    }

    @Override
    public void onReqEntrust(Entrust entrust, long requestId) {
        ITradeRequest request = tradeRequestMap.get(entrust.getAccountId());
        entrust.setLocalEntrustId(genLocalId());
        entrustIdManager.setLocalEntrustIdMapping(entrust.getAccountId(), entrust.getLocalEntrustId(), entrust.getId());
        if (request.getStatus(entrust.getAccountId()) != AccountLoginStatus.TRADING.value) {
            entrust.setErrorCode(ErrorCode.TERMINAL_API_ERROR.value);
            entrust.setEntrustStatus(EntrustStatus.INVALID.value);
            entrust.setErrorMsg("账户未处于交易状态");
            asyncWrite(FunctionCode.RESP_ENTRUST, requestId, 0, entrust);
        } else {
            requestIdEntrustMap.put(entrust.getId(), requestId);
            request.entrust(entrust, requestId);
        }
    }

    @Override
    public void onReqIntegratedEntrust(IntegratedEntrust integratedEntrust, long requestId) {
        //信用保护凭证不走交易直接，走交易登记逻辑
//        if (StringUtil.equals(DictCodeListExt3.投管业务类型_信用保护凭证.getValue(), integratedEntrust.getBusiTypeTg())) {
//            CDSTradeClient cdsTradeClient = SpringContextUtils.getBean(CDSTradeClient.class);
//            cdsTradeClient.init(this,null,this);
//            requestIdEntrustMap.put(integratedEntrust.getId(), requestId);
//            cdsTradeClient.integratedEntrust(integratedEntrust, requestId);
//
//            return;
//        }

        ITradeRequest request = tradeRequestMap.get(integratedEntrust.getAccountId());
        integratedEntrust.setLocalEntrustId(genLocalId());
        entrustIdManager.setLocalEntrustIdMapping(integratedEntrust.getAccountId(), integratedEntrust.getLocalEntrustId(), integratedEntrust.getId());
        if (request.getStatus(integratedEntrust.getAccountId()) != AccountLoginStatus.TRADING.value) {
            integratedEntrust.setErrorCode(ErrorCode.TERMINAL_API_ERROR.value);
            integratedEntrust.setEntrustStatus(EntrustStatus.INVALID.value);
            integratedEntrust.setErrorMsg("账户未处于交易状态");
            asyncWrite(FunctionCode.RESP_INTEGRATED_ENTRUST, requestId, 0, integratedEntrust);
        } else {
            requestIdEntrustMap.put(integratedEntrust.getId(), requestId);
            request.integratedEntrust(integratedEntrust, requestId);
        }
    }

    @Override
    public void onReqCancelEntrust(CancelEntrustBean cancelEntrustBean, long requestId) {
        requestIdEntrustMap.put(cancelEntrustBean.getCancelId(), requestId);
        tradeRequestMap.get(cancelEntrustBean.getAccountId()).cancelEntrust(cancelEntrustBean, requestId);
    }

    @Override
    public void onReqCancelIntegratedEntrust(CancelEntrustBean cancelEntrustBean, long requestId) {
        requestIdEntrustMap.put(cancelEntrustBean.getCancelId(), requestId);
        tradeRequestMap.get(cancelEntrustBean.getAccountId()).cancelIntegratedEntrust(cancelEntrustBean, requestId);
    }

    @Override
    public void onReqLoginAccount(AccountLoginReqBean accountLoginReqBean, long requestId) {
        ServerExtendInfo serverExtendInfo = accountLoginReqBean.getServerExtendInfo();
        // 记录营业部终端类型
        agentType = AgentType.MOCK.value.equals(agentType) ? agentType : serverExtendInfo.getAgentType();
        ITradeRequest tradeRequest = SpringContextUtils.getBean(agentType);
        tradeRequest.setITerminalMsgNotify(this);
        if (AgentType.KD_GATEWAY.value.equals(agentType)) {
            TypeUtils.compatibleWithFieldName = true;
        }
        agentTypeMap.put(agentType, tradeRequest);
        // 若营业部信息已经连接过，则不重复连接,否则连接
        if (!serverExtendInfoSet.contains(serverExtendInfo)) {
            tradeRequest.init(this, serverExtendInfo, this);
            // 如果是金正柜台，则判断连接是否建立，若没有建立，则账号下次登录也需要重新建立连接
            if (AgentType.KINGDOM_KCBP.value.equals(agentType)) {
                KCBPTradeClient kcbpTradeClient = (KCBPTradeClient) tradeRequest;
                if (kcbpTradeClient.isConnectionFlag(serverExtendInfo)) {
                    serverExtendInfoSet.add(serverExtendInfo);
                } else {
                    logger.warn("kcbp 连接建立失败！！！券商连接信息为 {}", JsonUtils.getJsonFromObject(serverExtendInfo));
                }
            } else {
                serverExtendInfoSet.add(serverExtendInfo);
            }
        }
        Account account = accountLoginReqBean.getAccount();
        // 账户交易密码解密
        account.setPwd(new String(Base64.decodeBase64(account.getPwd())));
        account.setTradePwd(new String(Base64.decodeBase64(account.getTradePwd())));
        tradeRequestMap.put(account.getId(), tradeRequest);
        requestIdAccountMap.put(account.getId(), requestId);
        accountList.add(account);
        //if (!loginAccounts.contains(account.getId())) {
        tradeRequest.reqLogin(account, serverExtendInfo);
        //}
    }

    @Override
    public void onServiceNode(List<ServiceNode> nodeList, long requestId) {
        serviceNodeList = nodeList;
        serviceNodeList.sort(Comparator.comparingInt(ServiceNode::getNodeWeight).reversed());
    }

    @Override
    public void onReqLogoutAccount(Account account, long requestId) {
    }

    @Override
    public void onEntrust(Entrust entrust) {
        asyncWrite(FunctionCode.NOTIFY_ENTRUST, 2000000000 + generateReqId(), entrust);
    }

    @Override
    public void onIntegratedEntrust(IntegratedEntrust integratedEntrust) {
        asyncWrite(FunctionCode.NOTIFY_INTEGRATED_ENTRUST, 2000000000 + generateReqId(), integratedEntrust);
    }

    @Override
    public void onTrade(Trade trade) {
        logger.info("onTrade()，{}-{}，{}", trade.getBrokerEntrustId(), trade.getBrokerTradeId(),
                JsonUtils.getJsonFromObject(trade));
        asyncWrite(FunctionCode.NOTIFY_TRADE, 2000000000 + generateReqId(), trade);
    }

    @Override
    public void onRTGSStatus(RTGSStatusBean rtgsStatusBean) {
        asyncWrite(FunctionCode.NOTIFY_RTGS_STATUS, 2000000000 + generateReqId(), rtgsStatusBean);
    }

    @Override
    public void onTerminalLogin(Map<ServerExtendInfo, List<Account>> accountMap) {
        logger.info("TerminalLogin  terminalName {}  success", terminalName);
        //        accountMap.forEach((serverExtendInfo, accounts) -> {
        //            ITradeRequest tradeRequest = SpringContextUtils.getBean(AgentType.MOCK.value.equals(agentType) ? agentType : serverExtendInfo.getAgentType());
        //            accounts.forEach(account -> {
        //                tradeRequestMap.put(account.getId(), tradeRequest);
        //            });
        tradeResponse.setTerminalAgent(this);
        if (agentTypeMap.size() > 0) {
            agentTypeMap.values().forEach(tradeRequest -> {
                Set<String> loginAccounts = tradeRequest.getLoginAccount();
                if (loginAccounts.size() > 0) {
                    tradeResponse.setTerminalAgent(this);
                    tradeRequest.setITerminalMsgNotify(this);
                    logger.info("loginInfoMaps is not null and login new account");
                    accountList.forEach(account -> {
                        tradeRequestMap.put(account.getId(), tradeRequest);
                        if (!loginAccounts.contains(account.getId())) {
                            logger.info("terminal login reconnect new account {}", account.getId());
                            tradeRequest.reqLogin(account, null);
                        }
                    });
                    repairMsgLostServerConnection();
                }
            });
        }
        //        });
    }

    private void repairMsgLostServerConnection() {
        if (respList.size() > 0) {
            respList.forEach(repairConnectionMsg -> {
                if (repairConnectionMsg.getRequestId() == -1) {
                    asyncWrite(repairConnectionMsg.getFunctionCode(), repairConnectionMsg.getObj());
                    logger.info("repair tradeResp -- {}", JsonUtils.getJsonFromObject(repairConnectionMsg.getObj()));
                } else {
                    asyncWrite(repairConnectionMsg.getFunctionCode(), repairConnectionMsg.getRequestId(), repairConnectionMsg.getDateType(), repairConnectionMsg.getObj());
                    logger.info("repair entrustResp -- {} ", JsonUtils.getJsonFromObject(repairConnectionMsg.getObj()));
                }
                respList.remove(repairConnectionMsg);
            });
        }
    }

    @Override
    public void onReqIntegratedQuotes(QuoteQryBean quoteQryBean, long requestId) {
        tradeRequestMap.get(quoteQryBean.getAccountId()).queryIntegratedQuotes(quoteQryBean, requestId);
    }

    @Override
    public void onReqEnable(EnableQryBean qryBean, long requestId) {
        tradeRequestMap.get(qryBean.getAccountId()).queryEnable(qryBean, requestId);
    }

    @Override
    public void onReqHistoryTrade(HistoryQryBean historyQryBean, long requestId) {
        tradeRequestMap.get(historyQryBean.getAccountId()).queryHistoryTrade(historyQryBean, requestId);
    }

    @Override
    public void onReqAccountFinance(String accountId, long requestId) {
        tradeRequestMap.get(accountId).queryCustomerFinance(accountId, requestId);
    }

    @Override
    public void onReqAccountPosition(PositionQueryBean positionQueryBean, long requestId) {
        tradeRequestMap.get(positionQueryBean.getAccountId()).queryAccountPosition(positionQueryBean, requestId);
    }

    @Override
    public void onReqInstrumentInfo(InstrumentQryBean instrumentQryBean, long requestId) {
        tradeRequestMap.get(instrumentQryBean.getAccountId()).queryInstrumentInfo(instrumentQryBean, requestId);
    }

    @Override
    public void onReqPledgeDiscount(String accountId, String market, String stockCode, long requestId) {
        logger.info("accountId:{} ,market:{} ,stockCode:{}", accountId, market, stockCode);
        tradeRequestMap.get(accountId).queryPledgeDiscount(accountId, market, stockCode, requestId);
    }

    @Override
    public void onReqStandardBonds(StandardBondQryBean standardBondQryBean, long requestId) {
        logger.info("查询标准券信息： {}", JsonUtils.getJsonFromObject(standardBondQryBean));
        tradeRequestMap.get(standardBondQryBean.getAccountId()).queryStandardBonds(standardBondQryBean, requestId);
    }

    //大宗交易行情查询
    @Override
    public void queryIntegratedBulkQuote(IntegratedBulkQuoteQryBean integratedBulkQuoteQryBean, Long requestId) {
        logger.info("queryIntegratedBulkQuote accountId:{}", integratedBulkQuoteQryBean.getAccountId());
        tradeRequestMap.get(integratedBulkQuoteQryBean.getAccountId()).queryIntegratedBulkQuote(integratedBulkQuoteQryBean, requestId);
    }

    //盘后大宗交易行情查询
    @Override
    public void queryIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean, Long requestId) {
        logger.info("queryIntegratedBulkQuote accountId:{}", integratedBulkCloseQuoteQryBean.getAccountId());
        tradeRequestMap.get(integratedBulkCloseQuoteQryBean.getAccountId()).queryIntegratedBulkCloseQuote(integratedBulkCloseQuoteQryBean, requestId);
    }

    //上海协议回购行情查询
    @Override
    public void queryIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean, Long requestId) {
        logger.info("queryIntegratedRepurchaseQuote accountId:{}", integratedRepurchaseQuoteQryBean.getAccountId());
        tradeRequestMap.get(integratedRepurchaseQuoteQryBean.getAccountId()).queryIntegratedRepurchaseQuote(integratedRepurchaseQuoteQryBean, requestId);
    }

    //上海固收行情查询
    @Override
    public void queryIntegratedFixedIncomeQuote(IntegratedFixedIncomeQuoteQryBean integratedFixedIncomeQuoteQryBean, Long requestId) {
        logger.info("queryIntegratedFixedIncomeQuote accountId:{}", integratedFixedIncomeQuoteQryBean.getAccountId());
        tradeRequestMap.get(integratedFixedIncomeQuoteQryBean.getAccountId()).queryIntegratedFixedIncomeQuote(integratedFixedIncomeQuoteQryBean, requestId);
    }

    //深圳我收到的协商成交行情查询
    @Override
    public void queryNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean, Long requestId) {
        logger.info("queryNegotiateQuoteReceive accountId:{}", negotiateQuoteQueryBean.getAccountId());
        tradeRequestMap.get(negotiateQuoteQueryBean.getAccountId()).queryNegotiateQuoteReceive(negotiateQuoteQueryBean, requestId);
    }

    @Override
    public void reqReissueEntrust(ReissueEntrustQryBean reissueEntrustQryBean, Long requestId) {
        logger.info("委托对比开始 reqId:{}", requestId);
        String accountId = reissueEntrustQryBean.getAccountId();
        List<ReissueEntrustBean> reissueEntrustBeanList = reissueEntrustQryBean.getReissueEntrustBeans();
        ITradeRequest iTradeRequest = tradeRequestMap.get(accountId);
        String entrustKey = getKey(accountId, "ENTRUST");
        serviceReissueStatusMap.put(entrustKey, ServiceReissueStatus.SERVICE_REISSUING_START);
        if (Objects.nonNull(reissueEntrustBeanList) && !reissueEntrustBeanList.isEmpty()) {
            iTradeRequest.setTradeStatus(accountId, AccountLoginStatus.ENTRUST_REISSUING);
            boolean isLast = reissueEntrustQryBean.isLast();
            int pageNum = reissueEntrustQryBean.getPageNum();
            Map<String, ReissueEntrustBean> reissueEntrustBeanAccountMap = reissueEntrustBeanMap.get(accountId);
            if (Objects.isNull(reissueEntrustBeanAccountMap)) {
                reissueEntrustBeanAccountMap = Maps.newConcurrentMap();
                reissueEntrustBeanMap.put(accountId, reissueEntrustBeanAccountMap);
            }
            for (ReissueEntrustBean reissueEntrustBean : reissueEntrustBeanList) {
                reissueEntrustBeanAccountMap.put(reissueEntrustBean.getId(), reissueEntrustBean);
            }
            // 当第一笔数据过来时，就开始查询柜台委托
            if (pageNum == 1) {
                brokerReissueStatusMap.put(entrustKey, BrokerReissueStatus.BROKER_REISSUING_START);
                iTradeRequest.reqReissueEntrust(accountId, requestId);
            }
            if (isLast) {
                serviceReissueStatusMap.put(entrustKey, ServiceReissueStatus.SERVICE_REISSUING_FINNISH);
                if (brokerReissueStatusMap.get(entrustKey).value == BrokerReissueStatus.BROKER_REISSUING_FINNISH.value) {
                    compareReissueEntrust(accountId, entrustInterfaceMapByBroker.get(accountId), iTradeRequest);
                }
            }
        } else {
            serviceReissueStatusMap.put(entrustKey, ServiceReissueStatus.SERVICE_REISSUING_FINNISH);
            iTradeRequest.setTradeStatus(accountId, AccountLoginStatus.ENTRUST_REISSUING_SUCCESS);
        }
    }

    public void compareReissueEntrust(String accountId, Map<String, EntrustInterface> entrustInterfaceMap, Long requestId) {
        String entrustKey = getKey(accountId, "ENTRUST");
        brokerReissueStatusMap.put(entrustKey, BrokerReissueStatus.BROKER_REISSUING_FINNISH);
        if (serviceReissueStatusMap.get(entrustKey).value == ServiceReissueStatus.SERVICE_REISSUING_FINNISH.value) {
            compareReissueEntrust(accountId, entrustInterfaceMap, tradeRequestMap.get(accountId));
        } else {
            entrustInterfaceMapByBroker.put(accountId, entrustInterfaceMap);
        }
    }

    public void compareReissueEntrust(String accountId, Map<String, EntrustInterface> entrustInterfaceMap, ITradeRequest iTradeRequest) {
        if (!reissueEntrustBeanMap.isEmpty()) {
            Map<String, ReissueEntrustBean> reissueEntrustBeanAccountMap = this.reissueEntrustBeanMap.get(accountId);
            logger.info("开始比对委托信息 accountId {} entrustInterfaceMapByBroker {} reissueEntrustBeanMapCache {} reissueEntrustBeanAccountMap {}", accountId, entrustInterfaceMap.size(), reissueEntrustBeanMap.size(), reissueEntrustBeanAccountMap.size());
            reissueEntrustBeanAccountMap.forEach((s, reissueEntrustBean) -> {
                if (!reissueEntrustBean.isBulk()) {
                    Entrust entrust = (Entrust) entrustInterfaceMap.get(s);
                    if (Objects.nonNull(entrust) && entrust.getEntrustStatus() != reissueEntrustBean.getEntrustStatus()) {
                        onEntrust(entrust);
                    }
                } else {
                    // modify by lifangpeng 20211230  start
                    //通过接口410411下单的大宗交易， 调用接口410510查询委托状态，和普通竞价的查询接口一致，410510接口在组装查询结果时用是Entrust
                    //因此(IntegratedEntrust) entrustInterfaceMap.get(s) 转换会报错
//                    IntegratedEntrust integratedEntrust = (IntegratedEntrust) entrustInterfaceMap.get(s);
                    EntrustInterface entrustInterface = entrustInterfaceMap.get(s);
                    IntegratedEntrust integratedEntrust;
                    if (entrustInterface instanceof Entrust) {
                        integratedEntrust = new IntegratedEntrust();
                        BeanUtils.copyProperties(entrustInterface, integratedEntrust);
                    } else {
                        integratedEntrust = (IntegratedEntrust) entrustInterfaceMap.get(s);
                    }
                    // modify by lifangpeng 20211230 XXX功能开发 end
                    if (Objects.nonNull(integratedEntrust) && integratedEntrust.getEntrustStatus() != reissueEntrustBean.getEntrustStatus()) {
                        onIntegratedEntrust(integratedEntrust);
                    }
                }
            });
        }
        updateTradeStatus(accountId, iTradeRequest, AccountLoginStatus.TRADE_REISSUING_SUCCESS, AccountLoginStatus.ENTRUST_REISSUING_SUCCESS);
    }

    @Override
    public void reqReissueTrade(ReissueTradeQryBean reissueTradeQryBean, Long requestId) {
        logger.info("成交对比开始 reqId:{}", requestId);
        String accountId = reissueTradeQryBean.getAccountId();
        ITradeRequest iTradeRequest = tradeRequestMap.get(accountId);
        List<ReissueTradeBean> ReissueTradeBeanList = reissueTradeQryBean.getReissueTradeBeans();
        String tradeKey = getKey(accountId, "TRADE");
        serviceReissueStatusMap.put(tradeKey, ServiceReissueStatus.SERVICE_REISSUING_START);
        if (Objects.nonNull(ReissueTradeBeanList) && !ReissueTradeBeanList.isEmpty()) {
            boolean isLast = reissueTradeQryBean.isLast();
            int pageNum = reissueTradeQryBean.getPageNum();
            Map<String, ReissueTradeBean> reissueTradeBeanAccountMap = reissueTradeBeanMap.get(accountId);
            if (Objects.isNull(reissueTradeBeanAccountMap)) {
                reissueTradeBeanAccountMap = Maps.newConcurrentMap();
                reissueTradeBeanMap.put(accountId, reissueTradeBeanAccountMap);
            }
            for (ReissueTradeBean reissueTradeBean : ReissueTradeBeanList) {
                reissueTradeBeanAccountMap.put(reissueTradeBean.getBrokerTradeId(), reissueTradeBean);
            }
            // 当第一笔数据过来时，就开始查询柜台成交
            if (pageNum == 1) {
                brokerReissueStatusMap.put(tradeKey, BrokerReissueStatus.BROKER_REISSUING_START);
                iTradeRequest.reqReissueTrade(accountId, requestId);
            }
            if (isLast) {
                // service数据组装完成， 可以进行比对
                serviceReissueStatusMap.put(tradeKey, ServiceReissueStatus.SERVICE_REISSUING_FINNISH);
                if (brokerReissueStatusMap.get(tradeKey).value == BrokerReissueStatus.BROKER_REISSUING_FINNISH.value) {
                    compareReissueTrade(accountId, tradeListMapByBroker.get(accountId), iTradeRequest);
                }
            }
        } else {
            serviceReissueStatusMap.put(tradeKey, ServiceReissueStatus.SERVICE_REISSUING_FINNISH);
            updateTradeStatus(accountId, iTradeRequest, AccountLoginStatus.ENTRUST_REISSUING_SUCCESS, AccountLoginStatus.TRADE_REISSUING_SUCCESS);
        }
    }

    public void compareReissueTrade(String accountId, List<Trade> tradeList, Long requestId) {
        String tradeKey = getKey(accountId, "TRADE");
        brokerReissueStatusMap.put(tradeKey, BrokerReissueStatus.BROKER_REISSUING_FINNISH);
        if (serviceReissueStatusMap.get(tradeKey).value == ServiceReissueStatus.SERVICE_REISSUING_FINNISH.value) {
            compareReissueTrade(accountId, tradeList, tradeRequestMap.get(accountId));
        } else {
            tradeListMapByBroker.put(accountId, tradeList);
        }
    }

    public void compareReissueTrade(String accountId, List<Trade> tradeList, ITradeRequest iTradeRequest) {
        // 成交比对时， 判断委托是否比对完成
        int status = iTradeRequest.getStatus(accountId);
        boolean reissueEntrustSuccess = status == AccountLoginStatus.ENTRUST_REISSUING_SUCCESS.value;
        iTradeRequest.setTradeStatus(accountId, AccountLoginStatus.TRADE_REISSUING);
        Map<String, ReissueTradeBean> reissueTradeBeanAccountMap = this.reissueTradeBeanMap.get(accountId);
        logger.info("开始比对成交信息 accountId {} tradeListByBroker {} reissueTradeBeanMapCache {} reissueTradeBeanAccountMap {}", accountId, tradeList.size(), reissueTradeBeanMap.size(), reissueTradeBeanAccountMap.size());
        tradeList.forEach(trade -> {
            ReissueTradeBean reissueTradeBean = reissueTradeBeanAccountMap.get(trade.getBrokerTradeId());
            if (Objects.isNull(reissueTradeBean)) {
                onTrade(trade);
            }
        });
        updateTradeStatus(accountId, iTradeRequest, reissueEntrustSuccess ? AccountLoginStatus.TRADE_REISSUING : AccountLoginStatus.ENTRUST_REISSUING_SUCCESS, AccountLoginStatus.TRADE_REISSUING_SUCCESS);
    }

    private void updateTradeStatus(String accountId, ITradeRequest iTradeRequest, AccountLoginStatus beforeReissuingSuccess, AccountLoginStatus afterReissuingSuccess) {
        if (iTradeRequest.getStatus(accountId) == beforeReissuingSuccess.value) {
            iTradeRequest.setTradeStatus(accountId, afterReissuingSuccess);
            iTradeRequest.setTradeStatus(accountId, AccountLoginStatus.TRADING);
            clearReissueCache(accountId);
        } else {
            iTradeRequest.setTradeStatus(accountId, afterReissuingSuccess);
        }
    }

    private String getKey(String accountId, String type) {
        return accountId + "|" + type;
    }

    @Override
    public void queryFuturesInstrument(FuturesInstrumentQryBean futuresInstrumentQryBean, Long requestId) {
        logger.info("queryFuturesInstrument reqId:{}", requestId);
        ((IFuturesTradeRequest) tradeRequestMap.get(futuresInstrumentQryBean.getAccountId())).queryFuturesInstrument(futuresInstrumentQryBean, requestId);
    }

    @Override
    public void queryFuturesPosition(FuturesPositionQryBean futuresPositionQryBean, Long requestId) {
        logger.info("queryFuturesPosition reqId:{}", requestId);
        ((IFuturesTradeRequest) tradeRequestMap.get(futuresPositionQryBean.getAccountId())).queryFuturesPosition(futuresPositionQryBean, requestId);
    }

    @Override
    public void queryFuturesPositionDetail(FuturesPositionDetailQryBean futuresPositionDetailQryBean, Long requestId) {
        logger.info("queryFuturesPositionDetail reqId:{}", requestId);
        ((IFuturesTradeRequest) tradeRequestMap.get(futuresPositionDetailQryBean.getAccountId())).queryFuturesPositionDetail(futuresPositionDetailQryBean, requestId);
    }

    @Override
    public void queryFuturesInstrumentMarginRate(FuturesInstrumentMarginRateQryBean futuresInstrumentMarginRateQryBean, Long requestId) {
        logger.info("queryFuturesInstrumentMarginRate reqId:{}", requestId);
        ((IFuturesTradeRequest) tradeRequestMap.get(futuresInstrumentMarginRateQryBean.getAccountId())).queryFuturesInstrumentMarginRate(futuresInstrumentMarginRateQryBean, requestId);
    }

    @Override
    public void queryFuturesInstrumentCommissionRate(FuturesInstrumentCommissionRateQryBean futuresInstrumentCommissionRateQryBean, Long requestId) {
        logger.info("queryFuturesInstrumentCommissionRate reqId:{}", requestId);
        ((IFuturesTradeRequest) tradeRequestMap.get(futuresInstrumentCommissionRateQryBean.getAccountId())).queryFuturesInstrumentCommissionRate(futuresInstrumentCommissionRateQryBean, requestId);
    }
}