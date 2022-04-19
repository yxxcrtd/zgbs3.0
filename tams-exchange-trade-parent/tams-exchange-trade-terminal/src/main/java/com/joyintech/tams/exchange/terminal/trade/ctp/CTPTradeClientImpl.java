package com.joyintech.tams.exchange.terminal.trade.ctp;

import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.constant.EntrustStatus;
import com.joyintech.tams.exchange.common.constant.ErrorCode;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.terminal.agent.ITerminalMsgNotify;
import com.joyintech.tams.exchange.terminal.agent.TerminalAgent;
import com.joyintech.tams.exchange.terminal.constant.AccountLoginStatus;
import com.joyintech.tams.exchange.terminal.trade.EntrustIdManager;
import com.joyintech.tams.exchange.terminal.trade.IFuturesTradeRequest;
import com.joyintech.tams.exchange.terminal.trade.TradeResponseImpl;
import net.jctp.CThostFtdcOrderField;
import net.jctp.CThostFtdcTradeField;
import net.jctp.TraderApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class CTPTradeClientImpl implements IFuturesTradeRequest {
    private static final Logger logger = LoggerFactory.getLogger(CTPTradeClientImpl.class);

    private EntrustIdManager entrustIdManager;

    private CTPTradeApi ctpTradeApi;

    private CTPTradeClient ctpTradeClient;

    public CTPTradeApi getCtpTradeApi() {
        return ctpTradeApi;
    }

    private TradeResponseImpl ctpTradeResponse;

    private ITerminalMsgNotify iTerminalMsgNotify;

    // 判断终端连接是否建立
    private boolean connectionFlag = false;

    private Map<String, AccountLoginStatus> accountLoginStatusMap = Maps.newConcurrentMap();

    public TradeResponseImpl getCtpTradeResponse() {
        return ctpTradeResponse;
    }

    private Map<String, Account> serverAccountMap = Maps.newConcurrentMap();//accountId-Account

    public void setCtpTradeResponse(TradeResponseImpl ctpTradeResponse) {
        this.ctpTradeResponse = ctpTradeResponse;
    }

    public CTPTradeClientImpl() {
    }

    public EntrustIdManager getEntrustIdManager() {
        return entrustIdManager;
    }

    public void setEntrustIdManager(EntrustIdManager entrustIdManager) {
        this.entrustIdManager = entrustIdManager;
    }

    public boolean isConnectionFlag() {
        return connectionFlag;
    }

    public String getAccountId() {
        return serverAccountMap.keySet().iterator().next();
    }

    public CTPTradeClient getCtpTradeClient() {
        return ctpTradeClient;
    }

    public void setCtpTradeClient(CTPTradeClient ctpTradeClient) {
        this.ctpTradeClient = ctpTradeClient;
    }

    public Account getAccount() {
        return serverAccountMap.values().iterator().next();
    }

    @Override
    public Set<String> getLoginAccount() {
        Set<String> res = new HashSet<>();
        serverAccountMap.values().forEach(c -> res.add(c.getId()));
        return res;
    }

    @Override
    public void setITerminalMsgNotify(ITerminalMsgNotify iTerminalMsgNotify) {
        this.iTerminalMsgNotify = iTerminalMsgNotify;
        this.ctpTradeApi.getCtpApi().setListener(new CTPTraderApiAdapter(iTerminalMsgNotify, this));
    }

    @Override
    public void reqLogin(Account account, ServerExtendInfo serverExtendInfo) {
        Account acc = serverAccountMap.getOrDefault(account.getId(), null);
        if (Objects.isNull(acc)) {
            ResultBean resultBean = ctpTradeApi.syncLogin(account, serverExtendInfo);
            resultBean.setData(account);
            if (ErrorCode.SUCCESS.value == resultBean.getErrorCode()) {
                serverAccountMap.put(account.getId(), account);
                accountLoginStatusMap.put(account.getId(), AccountLoginStatus.LOGGED);
            }
            ctpTradeResponse.resLogin(resultBean, account.getId());
        } else {
            ctpTradeResponse.resLogin(new ResultBean(ErrorCode.SUCCESS, account), account.getId());
        }
    }

    @Override
    public void reqEntrusts(String accountId, Long requestId) {
        Account acc = serverAccountMap.getOrDefault(accountId, null);
        if (Objects.isNull(acc)) {
            ctpTradeApi.qryOrder(acc);
        }
    }

    @Override
    public void init(TerminalAgent agent, ServerExtendInfo serverExtendInfo, ITerminalMsgNotify iTerminalMsgNotify) {
        initTradeApi(agent, serverExtendInfo, iTerminalMsgNotify);
    }

    public ResultBean initTradeApi(TerminalAgent agent, ServerExtendInfo serverExtendInfo, ITerminalMsgNotify iTerminalMsgNotify) {
        ctpTradeResponse.setTerminalAgent(agent);
        this.iTerminalMsgNotify = iTerminalMsgNotify;
        try {
            String traderFrontUrl = String.format("tcp://%s:%s", serverExtendInfo.getSzAddress(), serverExtendInfo.getnPort());
            TraderApi api = new TraderApi();
            api.setListener(new CTPTraderApiAdapter(iTerminalMsgNotify, this));
            api.setFlowControl(true);
            api.setSyncReqTimeout(30);

            ctpTradeApi = new CTPTradeApi(api);
            logger.info("连接 TraderApi");
            ResultBean resultBean = ctpTradeApi.init(traderFrontUrl);
            if (ErrorCode.SUCCESS.value == resultBean.getErrorCode()) {
                connectionFlag = true;
            } else {
                connectionFlag = false;
            }
            return resultBean;
        } catch (Throwable throwable) {
            logger.error("throwable:create CTPTradeApi error :{}", throwable);
            return new ResultBean(ErrorCode.TERMINAL_API_ERROR, throwable.getMessage());
        }
    }

    @Override
    public void reqTrades(String accountId, Long requestId) {
        Account acc = serverAccountMap.getOrDefault(accountId, null);
        if (Objects.isNull(acc)) {
            ctpTradeApi.qryTrade(acc);
        }
    }

    @Override
    public void entrust(Entrust entrust, Long requestId) {
        Account acc = serverAccountMap.getOrDefault(entrust.getAccountId(), null);
        if (Objects.nonNull(acc)) {
            ResultBean resultBean = ctpTradeApi.entrust(acc, entrust, requestId);
            if (ErrorCode.SUCCESS.value != resultBean.getErrorCode()) {
                entrust.setErrorCode(resultBean.getErrorCode());
                entrust.setErrorMsg(resultBean.getErrorMsg());
                entrust.setEntrustStatus(EntrustStatus.INVALID.value);
                ctpTradeResponse.resEntrust(entrust);
            }
        }
    }

    @Override
    public void integratedEntrust(IntegratedEntrust integratedEntrust, Long requestId) {

    }

    @Override
    public void cancelEntrust(CancelEntrustBean cancelEntrustBean, Long requestId) {
        cancelEntrustBean.getBrokerEntrustId();
        Account acc = serverAccountMap.getOrDefault(cancelEntrustBean.getAccountId(), null);
        if (Objects.nonNull(acc)) {
            if (ErrorCode.SUCCESS.value == ctpTradeApi.orderAction(acc, cancelEntrustBean).getErrorCode()) {
                cancelEntrustBean.setCancelBrokerEntrustId(cancelEntrustBean.getBrokerEntrustId());
                ctpTradeResponse.resCancelEntrust(cancelEntrustBean);
            }
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
        //ctpApi.SyncAllReqQryInstrument()
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
        logger.info("reqReissueEntrust start: accountId = {}", accountId);
        Account acc = serverAccountMap.getOrDefault(accountId, null);
        Map<String, EntrustInterface> entrustInterfaceMap = new HashMap<>();
        if (Objects.nonNull(acc)) {
            CThostFtdcOrderField[] orderFields = getCtpTradeApi().syncQryOrder(acc);
            if (Objects.nonNull(orderFields)) {
                for (CThostFtdcOrderField item : orderFields) {
                    Entrust entrust = CTPConverter.convertToEntrust(item, entrustIdManager, accountId);
                    entrust.setEntrustStatus(CTPConverter.convertToEntrustStatus(item.OrderStatus).value);
                    if (Strings.isNullOrEmpty(entrust.getId())) {
                        entrust.setId(entrustIdManager.getIdByBrokeEntrustId(entrust.getAccountId(), entrust.getBrokerEntrustId()));
                    }
                    if (!Strings.isNullOrEmpty(entrust.getId())) {
                        entrustInterfaceMap.put(entrust.getId(), entrust);
                    }
                }
            }
        }
        ctpTradeResponse.resQueryReissueEntrust(accountId, entrustInterfaceMap, requestId);
    }

    @Override
    public void reqReissueTrade(String accountId, Long requestId) {
        logger.info("reqReissueTrade start: accountId = {}", accountId);
        Account acc = serverAccountMap.getOrDefault(accountId, null);
        List<Trade> tradeList = new ArrayList<>();
        if (Objects.nonNull(acc)) {
            CThostFtdcTradeField[] tradeFields = getCtpTradeApi().syncQryTrade(acc);
            for (CThostFtdcTradeField tradeField : tradeFields) {
                Trade trade = CTPConverter.convertToTrade(tradeField, entrustIdManager, accountId);
                tradeList.add(trade);
            }
        }
        ctpTradeResponse.resQueryReissueTrade(accountId, tradeList, requestId);
    }

//    @Override
//    public void reqReissueEntrust(ReissueEntrustQryBean reissueEntrustQryBean, Long requestId) {
//        logger.info("reqReissueEntrust start: accountId = {}", reissueEntrustQryBean.getAccountId());
//        accountLoginStatusMap.put(reissueEntrustQryBean.getAccountId(), AccountLoginStatus.REISSUING);
//        Account acc = serverAccountMap.getOrDefault(reissueEntrustQryBean.getAccountId(), null);
//        if (Objects.nonNull(acc)) {
//            Map<String, Entrust> entrustMap = new HashMap<>();
//            CThostFtdcOrderField[] orderFields = getCtpTradeApi().syncQryOrder(acc);
//            if (Objects.nonNull(orderFields)) {
//                for (CThostFtdcOrderField item : orderFields) {
//                    Entrust entrust = CTPConverter.convertToEntrust(item, entrustIdManager, reissueEntrustQryBean.getAccountId());
//                    entrust.setEntrustStatus(CTPConverter.convertToEntrustStatus(item.OrderStatus).value);
//                    if (Strings.isNullOrEmpty(entrust.getId())) {
//                        entrust.setId(entrustIdManager.getIdByBrokeEntrustId(entrust.getAccountId(), entrust.getBrokerEntrustId()));
//                    }
//                    if (!Strings.isNullOrEmpty(entrust.getId())) {
//                        entrustMap.put(entrust.getId(), entrust);
//                    }
//                }
//            }
//
//            reissueEntrustQryBean.getReissueEntrustBeans().forEach(reissueEntrustBean -> {
//                if (!reissueEntrustBean.isBulk()) {
//                    Entrust entrust = entrustMap.get(reissueEntrustBean.getId());
//                    if (Objects.nonNull(entrust) && entrust.getEntrustStatus() != reissueEntrustBean.getEntrustStatus()) {
//                        iTerminalMsgNotify.onEntrust(entrust);
//                    }
//                }
//            });
//        }
//        logger.info("reissueEntrustBeans is success: accountId = {}", reissueEntrustQryBean.getAccountId());
//    }

/*    @Override
    public void reqReissueTrade(ReissueTradeQryBean reissueTradeQryBean, Long requestId) {
        logger.info("reqReissueTrade start: accountId = {}", reissueTradeQryBean.getAccountId());
        Account acc = serverAccountMap.getOrDefault(reissueTradeQryBean.getAccountId(), null);
        if (Objects.nonNull(acc)) {
            CThostFtdcTradeField[] tradeFields = getCtpTradeApi().syncQryTrade(acc);
            if (Objects.nonNull(tradeFields)) {
                Set<String> tradeIds = new HashSet<>();
                List<ReissueTradeBean> reissueTradeBeans = reissueTradeQryBean.getReissueTradeBeans();
                if (Objects.nonNull(reissueTradeBeans)) {
                    reissueTradeBeans.forEach(trade -> tradeIds.add(trade.getBrokerTradeId()));
                }
                for (CThostFtdcTradeField item : tradeFields) {
                    if (!tradeIds.contains(item.TradeID)) {
                        Trade trade = CTPConverter.convertToTrade(item, entrustIdManager, reissueTradeQryBean.getAccountId());
                        iTerminalMsgNotify.onTrade(trade);
                    }
                }
            }
        }
        logger.info("reqReissueTrade success: accountId = {}", reissueTradeQryBean.getAccountId());
        accountLoginStatusMap.put(reissueTradeQryBean.getAccountId(), AccountLoginStatus.TRADING);
    }*/

    @Override
    public int getStatus(String accountId) {
        return accountLoginStatusMap.getOrDefault(accountId, AccountLoginStatus.NOT_LOGGED).value;
    }

    @Override
    public void setTradeStatus(String accountId, AccountLoginStatus status) {
        logger.info("account {} status set to: {}", accountId, status.msg);
        accountLoginStatusMap.put(accountId, status);
    }

    @Override
    public void queryFuturesInstrument(FuturesInstrumentQryBean futuresInstrumentQryBean, Long requestId) {
        logger.info("queryFuturesInstrument start: accountId = {}", futuresInstrumentQryBean.getAccountId());
        Account acc = serverAccountMap.getOrDefault(futuresInstrumentQryBean.getAccountId(), null);
        if (Objects.nonNull(acc)) {
            String inst = " ";
            String exchangId = "";
            if (!Strings.isNullOrEmpty(futuresInstrumentQryBean.getStockCode())) {
                inst = futuresInstrumentQryBean.getStockCode();
                if (inst.contains(".")) {
                    String[] marketAndStockCode = StockCodeUtils.getMarketAndStockCode(inst);
                    exchangId = marketAndStockCode[1];
                    inst = marketAndStockCode[0];
                }
            }
            try {
                ctpTradeApi.qryInstrument(inst, exchangId, requestId);
            } catch (Exception e) {
                logger.info("queryFuturesInstrument error:{}", e);
                ctpTradeResponse.resQueryFuturesInstrument(null, requestId);
            }
        }
    }

    @Override
    public void queryFuturesPosition(FuturesPositionQryBean futuresPositionQryBean, Long requestId) {
        logger.info("queryFuturesPosition start: accountId = {}", futuresPositionQryBean.getAccountId());
        Account acc = serverAccountMap.getOrDefault(futuresPositionQryBean.getAccountId(), null);
        if (Objects.nonNull(acc)) {
            String instrument = "";
            if (!Strings.isNullOrEmpty(futuresPositionQryBean.getStockCode()) && futuresPositionQryBean.getStockCode().contains(".")) {
                instrument = StockCodeUtils.getStockCode(futuresPositionQryBean.getStockCode());
            }
            ctpTradeApi.reqQryInvestorPosition(acc, instrument, requestId);
        }
    }

    @Override
    public void queryFuturesPositionDetail(FuturesPositionDetailQryBean futuresPositionDetailQryBean, Long requestId) {
        logger.info("queryFuturesPositionDetail start: accountId = {}", futuresPositionDetailQryBean.getAccountId());
        Account acc = serverAccountMap.getOrDefault(futuresPositionDetailQryBean.getAccountId(), null);
        if (Objects.nonNull(acc)) {
            String instrument = "";
            if (!Strings.isNullOrEmpty(futuresPositionDetailQryBean.getStockCode()) && futuresPositionDetailQryBean.getStockCode().contains(".")) {
                instrument = StockCodeUtils.getStockCode(futuresPositionDetailQryBean.getStockCode());
            }
            ctpTradeApi.reqQryInvestorPositionDetail(acc, instrument, requestId);
        }
    }

    @Override
    public void queryFuturesInstrumentMarginRate(FuturesInstrumentMarginRateQryBean futuresInstrumentMarginRateQryBean, Long requestId) {
        logger.info("queryFuturesInstrumentMarginRate start: accountId = {}", futuresInstrumentMarginRateQryBean.getAccountId());
    }

    public void queryAllFuturesInstrumentMarginRate(Map<String, FuturesInstrument> instrumentMap, String accountId, Long requestId) {
        logger.info("queryAllFuturesInstrumentMarginRate, accountId:{}", accountId);
        Account acc = serverAccountMap.getOrDefault(accountId, null);
        if (Objects.nonNull(acc)) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<FuturesInstrumentMarginRateBean> marginRateBeanList = new ArrayList<>();
                    instrumentMap.values().forEach((item) -> {
                        try {
                            List<FuturesInstrumentMarginRateBean> marginRateBeans = ctpTradeApi.syncQryInstrumentMarginRate(acc, item.getInstrumentId());
                            marginRateBeans.forEach(marginRateBean -> {
                                        marginRateBean.setStockCode(item.getInnerCode());
                                        marginRateBeanList.add(marginRateBean);
                                    }
                            );
                        } catch (Exception e) {
                            logger.error("queryAllFuturesInstrumentMarginRate,accountId:{},stockCode:{},exception:{}", accountId, item.getInnerCode(), e);
                        }
                    });
                    logger.info("queryAllFuturesInstrumentMarginRate end , accountId:{}", accountId);
                    ctpTradeResponse.resQueryFuturesInstrumentMarginRate(marginRateBeanList, requestId);
                }
            }).start();
        }
    }

    @Override
    public void queryFuturesInstrumentCommissionRate(FuturesInstrumentCommissionRateQryBean futuresInstrumentCommissionRateQryBean, Long requestId) {
        logger.info("queryFuturesInstrumentCommissionRate start: accountId = {}", futuresInstrumentCommissionRateQryBean.getAccountId());
        Account acc = serverAccountMap.getOrDefault(futuresInstrumentCommissionRateQryBean.getAccountId(), null);
        if (Objects.nonNull(acc)) {
            String instrument = "";
            if (!Strings.isNullOrEmpty(futuresInstrumentCommissionRateQryBean.getStockCode()) && futuresInstrumentCommissionRateQryBean.getStockCode().contains(".")) {
                instrument = StockCodeUtils.getStockCode(futuresInstrumentCommissionRateQryBean.getStockCode());
            }
            ctpTradeApi.reqQryInstrumentCommissionRate(acc, instrument, requestId);
        }
    }

	@Override
	public void queryNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean, Long requestId) {
		// TODO Auto-generated method stub
		
	}
}
