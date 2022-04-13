package com.joyintech.tams.exchange.terminal.trade;

import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.db.domain.Account;
import com.joyintech.tams.exchange.common.db.domain.CancelEntrustBean;
import com.joyintech.tams.exchange.common.db.domain.Entrust;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.terminal.agent.ITerminalMsgNotify;
import com.joyintech.tams.exchange.terminal.agent.TerminalAgent;
import com.joyintech.tams.exchange.terminal.constant.AccountLoginStatus;

import java.util.Set;

/**
 * create By MAXX on 2020/3/25
 */
public interface ITradeRequest {
    Set<String> getLoginAccount();

    void setITerminalMsgNotify(ITerminalMsgNotify iTerminalMsgNotify);

    /*** 
     * @Description: 登录请求
     * @Param: [account]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/25 
     */
    void reqLogin(Account account, ServerExtendInfo serverExtendInfo);

    /*** 
     * @Description: 证券委托查询
     * @Param: [accountId, requestId] 
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/27 
     */
    void reqEntrusts(String accountId, Long requestId);

    /*** 
     * @Description: 初始化
     * @Param: [accountMap, iTerminalMsgNotify] 
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/25 
     */
    void init(
            TerminalAgent agent,
            ServerExtendInfo serverExtendInfo,
            ITerminalMsgNotify iTerminalMsgNotify);

    /*** 
     * @Description: 证券成交查询
     * @Param: [accountId, requestId] 
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/25 
     */
    void reqTrades(String accountId, Long requestId);

    /***
     * @Description: 普通委托
     * @Param: [entrust, requestId] 
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/27 
     */
    void entrust(Entrust entrust, Long requestId);

    /***
     * @Description: 综合业务委托确认
     * @Param: [integratedEntrust]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/25
     */
    void integratedEntrust(IntegratedEntrust integratedEntrust, Long requestId);

    /*** 
     * @Description: 委托撤单
     * @Param: [entrust, requestId] 
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/27 
     */
    void cancelEntrust(CancelEntrustBean cancelEntrustBean, Long requestId);

    /*** 
     * @Description: 综合业务委托撤单
     * @Param: [integratedEntrust, requestId] 
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/26 
     */
    void cancelIntegratedEntrust(CancelEntrustBean cancelEntrustBean, Long requestId);

    /***
     * @Description: 大宗交易行情查询
     * @Param: [integratedQuote, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/27
     */
    void queryIntegratedQuotes(QuoteQryBean quoteQryBean, Long requestId);

    /*** 
     * @Description: 最大交易数量
     * @Param: [qryBean, requestId] 
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/26 
     */
    void queryEnable(EnableQryBean qryBean, Long requestId);

    /***
     * @Description: 历史证券成交查询
     * @Param: [historyQryBean, requestId] 
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/26 
     */
    void queryHistoryTrade(HistoryQryBean historyQryBean, Long requestId);

    /***
     * @Description: 客户资金快速查询请求
     * @Param: [accountId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/25
     */
    void queryCustomerFinance(String accountId, Long requestId);

    /***
     * @Description: 客户资金快速查询请求
     * @Param: [accountId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/25
     */
    void queryAccountPosition(PositionQueryBean positionQueryBean, Long requestId);

    /*** 
     * @Description: 债券质押率
     * @Param: [accountId, market, stockCode, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/5/9
     */
    void queryPledgeDiscount(String accountId, String market, String stockCode, Long requestId);

    /*** 
     * @Description: 标准券查询
     * @Param: [accountId, market, stockCode, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/5/9
     */
    void queryStandardBonds(StandardBondQryBean standardBondQryBean, Long requestId);

    /**
     * @param instrumentQryBean 合约查询Bean
     * @param requestId         请求编号
     * @author swwan
     * @Description 合约信息查询
     * @Date 2020/6/15
     **/
    void queryInstrumentInfo(InstrumentQryBean instrumentQryBean, Long requestId);

    /***
     * @Description: 成交查询，委托查询主动拉取
     * @Param: []
     * @return: void
     * @Author: MAXX
     * @Date: 2020/6/9
     */
    void queryNextTrade();

    /***
     * @Description: 大宗交易行情查询
     * @Param: [integratedBulkQuoteQryBean, requestId]
     * @return: void
     * @Author:liujianping
     * @Date: 2020/9/22
     */
    void queryIntegratedBulkQuote(IntegratedBulkQuoteQryBean integratedBulkQuoteQryBean, Long requestId);

    /***
     * @Description: 盘后大宗交易行情查询
     * @Param: [integratedBulkCloseQuoteQryBean, requestId]
     * @return: void
     * @Author:liujianping
     * @Date: 2020/9/22
     */
    void queryIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean, Long requestId);

    /***
     * @Description: 上海协议回购行情查询
     * @Param: [integratedRepurchaseQuoteQryBean, requestId]
     * @return: void
     * @Author:liujianping
     * @Date: 2020/9/22
     */
    void queryIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean, Long requestId);

    /***
     * @Description: 上海固收行情查询
     * @Param: [integratedFixedIncomeQuoteQryBean, requestId]
     * @return: void
     * @Author:liujianping
     * @Date: 2020/9/22
     */
    void queryIntegratedFixedIncomeQuote(IntegratedFixedIncomeQuoteQryBean integratedFixedIncomeQuoteQryBean, Long requestId);
    
    /**
     * @Description: 深圳我收到的协商成交行情查询
     * @param negotiateQuoteQueryBean
     * @param requestId
     */
    void queryNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean, Long requestId);

    /***
     * @Description: 委托重传
     * @Param: [reissueEntrustQryBean, requestId]
     * @return: void
     * @Author:liujianping
     * @Date: 2020/9/22
     */
    void reqReissueEntrust(String accountId, Long requestId);

    /***
     * @Description: 成交重传
     * @Param: [reissueTradeQryBean, requestId]
     * @return: void
     * @Author:liujianping
     * @Date: 2020/9/22
     */
    void reqReissueTrade(String accountId, Long requestId);

    /***
     * @Description: 获取账户登录状态
     * @Param: [AccountId]
     * @return: int
     * @Author:huxin
     * @Date: 2020/9/22
     */
    int getStatus(String accountId);

    void setTradeStatus(String accountId, AccountLoginStatus status);
}
