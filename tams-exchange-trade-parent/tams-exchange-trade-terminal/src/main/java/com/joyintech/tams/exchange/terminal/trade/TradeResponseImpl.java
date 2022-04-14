package com.joyintech.tams.exchange.terminal.trade;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.joyintech.tams.exchange.common.bean.AccountFinance;
import com.joyintech.tams.exchange.common.bean.FuturesInstrumentCommissionRateBean;
import com.joyintech.tams.exchange.common.bean.FuturesPositionBean;
import com.joyintech.tams.exchange.common.bean.FuturesPositionDetailBean;
import com.joyintech.tams.exchange.common.bean.HistoryTrade;
import com.joyintech.tams.exchange.common.bean.IntegratedBulkCloseQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedBulkQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedFixedIncomeQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedQuote;
import com.joyintech.tams.exchange.common.bean.IntegratedRepurchaseQuoteBean;
import com.joyintech.tams.exchange.common.bean.NegotiateQuoteReceiveBean;
import com.joyintech.tams.exchange.common.bean.PledgeDiscount;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.StandardBonds;
import com.joyintech.tams.exchange.common.db.domain.CancelEntrustBean;
import com.joyintech.tams.exchange.common.db.domain.Entrust;
import com.joyintech.tams.exchange.common.db.domain.EntrustInterface;
import com.joyintech.tams.exchange.common.db.domain.FuturesInstrument;
import com.joyintech.tams.exchange.common.db.domain.FuturesInstrumentMarginRateBean;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.db.domain.PositionBean;
import com.joyintech.tams.exchange.common.db.domain.Trade;
import com.joyintech.tams.exchange.common.net.protocol.FunctionCode;
import com.joyintech.tams.exchange.common.utils.JsonUtils;
import com.joyintech.tams.exchange.terminal.agent.TerminalAgent;

@Component
public class TradeResponseImpl implements ITradeResponse {
    private static final Logger logger = LoggerFactory.getLogger(TradeResponseImpl.class);

    private TerminalAgent terminalAgent;

    public void setTerminalAgent(TerminalAgent terminalAgent) {
        this.terminalAgent = terminalAgent;
    }

    /***
     * @Description: 登录返回
     * @Param: [resultBean]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/25
     */
    @Override
    public void resLogin(ResultBean resultBean, String accountId) {
        Long requestId = TerminalAgent.getRequestId(accountId);
        if (requestId == null) {
            requestId = terminalAgent.generateReqId();
        }
        terminalAgent.asyncWrite(FunctionCode.RESP_ACCOUNT_LOGIN, requestId, 0, resultBean);
    }

    /***
     * @Description: 证券成交查询返回
     * @Param: [list, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/25
     */
    @Override
    public void resQueryTrade(List<Trade> list, Long requestId) {
        terminalAgent.asyncWrite(FunctionCode.RESP_QRY_TRADE, requestId, 0, list);
    }

    /***
     * @Description: 证券委托查询返回
     * @Param: [list, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/27
     */
    @Override
    public void resQueryEntrusts(List<Entrust> list, Long requestId) {
        terminalAgent.asyncWrite(FunctionCode.RESP_QRY_ENTRUST, requestId, 0, list);
    }

    /***
     * @Description: 普通委托返回
     * @Param: [entrust]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/27
     */
    @Override
    public void resEntrust(Entrust entrust) {
        logger.info("resEntrust param Entrust: {}", "\r\n" + JsonUtils.getJsonFromObject(entrust));
        Long requestId = terminalAgent.getRequestIdByEntrustId(entrust.getId());
        terminalAgent.asyncWrite(FunctionCode.RESP_ENTRUST, requestId, 0, entrust);
        TerminalAgent.deleteRequestIdByEntrustId(entrust.getId());
    }

    /***
     * @Description: 综合业务委托确认返回
     * @Param: [integratedEntrust]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/26
     */
    @Override
    public void resIntegratedEntrust(IntegratedEntrust integratedEntrust) {
        logger.info("resIntegratedEntrust param IntegratedEntrust is {}", "\r\n" + JsonUtils.getJsonFromObject(integratedEntrust));
        Long requestId = terminalAgent.getRequestIdByEntrustId(integratedEntrust.getId());
        terminalAgent.asyncWrite(FunctionCode.RESP_INTEGRATED_ENTRUST, requestId, 0, integratedEntrust);
        TerminalAgent.deleteRequestIdByEntrustId(integratedEntrust.getId());
    }

    /***
     * @Description: 委托撤单返回
     * @Param: [entrust, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/27
     */
    @Override
    public void resCancelEntrust(CancelEntrustBean cancelEntrustBean) {
        Long requestId = terminalAgent.getRequestIdByEntrustId(cancelEntrustBean.getCancelId());
        terminalAgent.asyncWrite(FunctionCode.RESP_CANCEL_ENTRUST, requestId, 0, cancelEntrustBean);
        TerminalAgent.deleteRequestIdByEntrustId(cancelEntrustBean.getCancelId());
    }

    /***
     * @Description: 综合业务委托撤单返回
     * @Param: [integratedEntrust, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/26
     */
    @Override
    public void resCancelIntegratedEntrust(CancelEntrustBean cancelEntrustBean) {
        Long requestId = terminalAgent.getRequestIdByEntrustId(cancelEntrustBean.getCancelId());
        terminalAgent.asyncWrite(FunctionCode.RESP_CANCEL_INTEGRATED_ENTRUST, requestId, 0, cancelEntrustBean);
        TerminalAgent.deleteRequestIdByEntrustId(cancelEntrustBean.getCancelId());
    }

    /***
     * @Description: 大宗交易行情查询返回
     * @Param: [list, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/27
     */
    @Override
    public void resQueryIntegratedQuotes(List<IntegratedQuote> list, Long requestId) {
        if (Objects.nonNull(list))
            logger.info("resQueryIntegratedQuotes param list size is {}", list.size());
        terminalAgent.asyncWrite(FunctionCode.RESP_QRY_INTEGRATED_QUOTE, requestId, 0, list);
    }

    /***
     * @Description: 最大交易数量返回
     * @Param: [enableAmount, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/26
     */
    @Override
    public void resQueryEnable(int enableAmount, Long requestId) {
        terminalAgent.asyncWrite(FunctionCode.RESP_QRY_ENABLE, requestId, 0, enableAmount);
    }

    /***
     * @Description: 历史证券成交查询返回
     * @Param: [list, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/26
     */
    @Override
    public void resQueryHistoryTrade(List<HistoryTrade> list, Long requestId) {
        terminalAgent.asyncWrite(FunctionCode.RESP_QRY_HISTORY_TRADE, requestId, 0, list);
    }

    /***
     * @Description: 客户资金快速查询返回
     * @Param: [list]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/25
     */
    @Override
    public void resQueryCustomerFinance(List<AccountFinance> list, Long requestId) {
        terminalAgent.asyncWrite(FunctionCode.RESP_QRY_FINANCE, requestId, 0, list);
    }

    @Override
    public void resQueryAccountPosition(List<PositionBean> positionList, Long requestId) {
        terminalAgent.asyncWrite(FunctionCode.RESP_QRY_POSITION, requestId, 0, positionList);
    }

    /***
     * @Description: 债券质押率返回
     * @Param: [list, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/5/9
     */
    @Override
    public void resQueryPledgeDiscount(List<PledgeDiscount> list, Long requestId) {
        terminalAgent.asyncWrite(FunctionCode.RESP_PLEDGE_DISCOUNT, requestId, 0, list);
    }

    /***
     * @Description: 标准券查询返回
     * @Param: [list, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/5/9
     */
    @Override
    public void resQueryStandardBonds(List<StandardBonds> list, Long requestId) {
        terminalAgent.asyncWrite(FunctionCode.RESP_STANDARD_BONDS, requestId, 0, list);
    }

    /***
     * @Description: 证券委托回报
     * @Param: [entrust]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/4/7
     */
    @Override
    public void entrustWriteBack(Entrust entrust) {
        terminalAgent.asyncWrite(FunctionCode.NOTIFY_ENTRUST, 2000000000 + terminalAgent.generateReqId(), entrust);
    }

    /***
     * @Description: 大宗证券委托回报
     * @Param: [entrust]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/4/7
     */
    @Override
    public void integratedEntrustWriteBack(IntegratedEntrust entrust) {
        terminalAgent.asyncWrite(FunctionCode.NOTIFY_INTEGRATED_ENTRUST, 2000000000 + terminalAgent.generateReqId(), entrust);
    }

    /***
     * @Description: 证券成交回报
     * @Param: [trade]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/4/7
     */
    @Override
    public void dealReturn(Trade trade) {
        terminalAgent.asyncWrite(FunctionCode.NOTIFY_TRADE, 2000000000 + terminalAgent.generateReqId(), trade);
    }

    /**
     * 合约信息查询
     *
     * @param instrumentList 合约列表
     * @param requestId      请求编号
     */
    @Override
    public void resInstrumentInfo(List<Instrument> instrumentList, Long requestId) {
        terminalAgent.asyncWrite(FunctionCode.RESP_QRY_INSTRUMENT_INFO, requestId, 0, instrumentList);
    }

    //大宗交易行情查询返回
    @Override
    public void resQueryIntegratedBulkQuote(List<IntegratedBulkQuoteBean> list, Long requestId) {
        terminalAgent.asyncWrite(FunctionCode.RESP_QRY_INTEGRATED_BULK_QUOTE, requestId, 0, list);
    }

    //盘后大宗交易行情查询返回
    @Override
    public void resQueryIntegratedBulkCloseQuote(List<IntegratedBulkCloseQuoteBean> list, Long requestId) {
        terminalAgent.asyncWrite(FunctionCode.RESP_QRY_INTEGRATED_BULK_CLOSE_QUOTE, requestId, 0, list);
    }

    //上交所协议回购行情查询返回
    @Override
    public void resQueryIntegratedRepurchaseQuote(List<IntegratedRepurchaseQuoteBean> list, Long requestId) {
        terminalAgent.asyncWrite(FunctionCode.RESP_QRY_INTEGRATED_REPURCHASE_QUOTE, requestId, 0, list);
    }

    //上交所固定收益行情查询返回
    @Override
    public void resQueryIntegratedFixedIncomeQuote(List<IntegratedFixedIncomeQuoteBean> list, Long requestId) {
        terminalAgent.asyncWrite(FunctionCode.RESP_QRY_INTEGRATED_FIXED_INCOME_QUOTE, requestId, 0, list);
    }

    //期货合约查询返回
    @Override
    public void resQueryFuturesInstrument(List<FuturesInstrument> list, Long requestId) {
        terminalAgent.asyncWrite(FunctionCode.RESP_QRY_FUTURES_INSTRUMENT, requestId, 0, list);
    }

    //期货持仓查询返回
    @Override
    public void resQueryFuturesPosition(List<FuturesPositionBean> list, Long requestId) {
        terminalAgent.asyncWrite(FunctionCode.RESP_QRY_FUTURES_POSITION, requestId, 0, list);
    }

    //期货详细持仓查询返回
    @Override
    public void resQueryFuturesPositionDetail(List<FuturesPositionDetailBean> list, Long requestId) {
        terminalAgent.asyncWrite(FunctionCode.RESP_QRY_FUTURES_POSITION_DETAIL, requestId, 0, list);
    }

    //期货保证金查询返回
    @Override
    public void resQueryFuturesInstrumentMarginRate(List<FuturesInstrumentMarginRateBean> list, Long requestId) {
        terminalAgent.asyncWrite(FunctionCode.RESP_QRY_FUTURES_MARGIN_RATE, requestId, 0, list);
    }

    //期货手续费查询返回
    @Override
    public void resQueryFuturesInstrumentCommissionRate(List<FuturesInstrumentCommissionRateBean> list, Long requestId) {
        terminalAgent.asyncWrite(FunctionCode.RESP_QRY_FUTURES_COMMISSION_RATE, requestId, 0, list);
    }

    @Override
    public void resQueryReissueEntrust(String accountId, Map<String, EntrustInterface> entrustInterfaceMap, Long requestId) {
        terminalAgent.compareReissueEntrust(accountId, entrustInterfaceMap, requestId);
    }

    @Override
    public void resQueryReissueTrade(String accountId, List<Trade> tradeList, Long requestId) {
        terminalAgent.compareReissueTrade(accountId, tradeList, requestId);
    }
    
    //我收到的协商成交行情查询返回
    @Override
    public void resQueryNegotiateQuoteReceive(List<NegotiateQuoteReceiveBean> list, Long requestId) {
        terminalAgent.asyncWrite(FunctionCode.REQ_QRY_INTEGRATED_NEGOTIATE_QUOTE_RECEIVE, requestId, 0, list);
    }
}
