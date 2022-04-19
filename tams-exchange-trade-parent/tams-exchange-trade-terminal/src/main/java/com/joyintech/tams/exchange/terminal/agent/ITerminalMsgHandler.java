package com.joyintech.tams.exchange.terminal.agent;

import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.db.domain.*;

import java.util.List;
import java.util.Map;

public interface ITerminalMsgHandler {

    /**
     * 请求委托
     *
     * @param accountId 账户编号
     * @param requestId 请求编号
     */
    void onReqQryEntrusts(String accountId, long requestId);

    /**
     * 请求成交
     *
     * @param accountId 账户编号
     * @param requestId 请求编号
     */
    void onReqQryTrades(String accountId, long requestId);

    /**
     * 下单
     *
     * @param entrust   委托
     * @param requestId 请求编号
     */
    void onReqEntrust(Entrust entrust, long requestId);

    /**
     * 大宗、协议回购下单
     *
     * @param integratedEntrust 大宗、协议回购委托
     * @param requestId         请求编号
     */
    void onReqIntegratedEntrust(IntegratedEntrust integratedEntrust, long requestId);

    /**
     * 撤单
     *
     * @param entrust   委托
     * @param requestId 请求编号
     */
    void onReqCancelEntrust(CancelEntrustBean entrust, long requestId);

    /**
     * 综合业务撤单
     *
     * @param cancelEntrustBean 大宗、协议回购委托
     * @param requestId         请求编号
     */
    void onReqCancelIntegratedEntrust(CancelEntrustBean cancelEntrustBean, long requestId);

    /**
     * 账号登录
     *
     * @param accountLoginReqBean 账号
     * @param requestId           请求编号
     */
    void onReqLoginAccount(AccountLoginReqBean accountLoginReqBean, long requestId);

    /**
     * 账号登录
     *
     * @param nodeList
     * @param requestId
     */
    void onServiceNode(List<ServiceNode> nodeList, long requestId);

    /**
     * 账号登出
     *
     * @param account   账号
     * @param requestId 请求编号
     */
    void onReqLogoutAccount(Account account, long requestId);

    /**
     * 登录返回所有的账号信息
     *
     * @param accountMap 账号及服务器信息Map
     */
    void onTerminalLogin(Map<ServerExtendInfo, List<Account>> accountMap);

    /**
     * 查询大宗交易行情
     *
     * @param quoteQryBean 查询大宗交易行情Bean
     */
    void onReqIntegratedQuotes(QuoteQryBean quoteQryBean, long requestId);

    /**
     * 查询最大交易数量
     *
     * @param qryBean   查询最大交易数量Bean
     * @param requestId 请求编号
     */
    void onReqEnable(EnableQryBean qryBean, long requestId);

    /**
     * 查询历史成交明细
     *
     * @param historyQryBean 查询历史成交Bean
     * @param requestId      请求编号
     */
    void onReqHistoryTrade(HistoryQryBean historyQryBean, long requestId);

    /**
     * 查询客户资金
     *
     * @param accountId 账户编号
     * @param requestId 请求编号
     */
    void onReqAccountFinance(String accountId, long requestId);

    /**
     * 查询客户资金
     *
     * @param positionQueryBean 持仓查询bean
     * @param requestId         请求编号
     */
    void onReqAccountPosition(PositionQueryBean positionQueryBean, long requestId);

    /**
     * 查询合约信息
     *
     * @param instrumentQryBean 查询合约Bean
     * @param requestId         请求编号
     */
    void onReqInstrumentInfo(InstrumentQryBean instrumentQryBean, long requestId);

    /**
     * 查询债券质押率
     *
     * @param accountId 账户编号
     * @param market    交易场所
     * @param stockCode 证券代码
     * @param requestId 请求编号
     */
    void onReqPledgeDiscount(String accountId, String market, String stockCode, long requestId);

    /**
     * 标准券信息查询
     *
     * @param standardBondQryBean
     * @param requestId
     */
    void onReqStandardBonds(StandardBondQryBean standardBondQryBean, long requestId);

    /*** 
     * @Description: 大宗交易行情查询
     * @Param: [integratedBulkQuoteQryBean, requestId] 
     * @return: void
     * @Author: MAXX
     * @Date: 2020/9/23 
     */
    void queryIntegratedBulkQuote(IntegratedBulkQuoteQryBean integratedBulkQuoteQryBean, Long requestId);

    /***
     * @Description: 盘后大宗交易行情查询
     * @Param: [integratedBulkCloseQuoteQryBean, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/9/23
     */
    void queryIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean, Long requestId);

    /*** 
     * @Description: 上海协议回购行情查询
     * @Param: [integratedRepurchaseQuoteQryBean, requestId] 
     * @return: void
     * @Author: MAXX
     * @Date: 2020/9/23 
     */
    void queryIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean, Long requestId);

    /*** 
     * @Description: 上海固收行情查询
     * @Param: [integratedFixedIncomeQuoteQryBean, requestId] 
     * @return: void
     * @Author: MAXX
     * @Date: 2020/9/23 
     */
    void queryIntegratedFixedIncomeQuote(IntegratedFixedIncomeQuoteQryBean integratedFixedIncomeQuoteQryBean, Long requestId);
    
    /**
     * @Description: 深圳我收到的协商成交行情查询
     * @param negotiateQuoteQueryBean
     * @param requestId
     */
    public void queryNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean, Long requestId) ;

    void reqReissueEntrust(ReissueEntrustQryBean reissueEntrustQryBean, Long requestId);

    void reqReissueTrade(ReissueTradeQryBean reissueTradeQryBean, Long requestId);

    /***
     * @Description: 查询期货合约
     * @Param: [futuresInstrumentQryBean, requestId]
     * @return: void
     * @Author: liujianping
     * @Date: 2020/9/23
     */
    void queryFuturesInstrument(FuturesInstrumentQryBean futuresInstrumentQryBean, Long requestId);

    /**
     * @Description: 查询期货持仓
     * @Author: liujianping
     * @Param: [futuresPositionQryBean, requestId]
     * @return: void
     * @Date: 2020/11/3
     **/
    void queryFuturesPosition(FuturesPositionQryBean futuresPositionQryBean, Long requestId);

    /**
     * @Description: 查询期货持仓明细
     * @Author: liujianping
     * @Param: [FuturesPositionDetailQryBean, requestId]
     * @return: void
     * @Date: 2020/11/3
     **/
    void queryFuturesPositionDetail(FuturesPositionDetailQryBean futuresPositionDetailQryBean, Long requestId);

    /**
     * @Description: 查询期货保证金
     * @Author: liujianping
     * @Param: [futuresInstrumentMarginRateQryBean, requestId]
     * @return: void
     * @Date: 2020/11/23
     **/
    void queryFuturesInstrumentMarginRate(FuturesInstrumentMarginRateQryBean futuresInstrumentMarginRateQryBean, Long requestId);

    /**
     * @Description: 查询期货手续费
     * @Author: liujianping
     * @Param: [futuresInstrumentCommissionRateQryBean, requestId]
     * @return: void
     * @Date: 2020/11/23
     **/
    void queryFuturesInstrumentCommissionRate(FuturesInstrumentCommissionRateQryBean futuresInstrumentCommissionRateQryBean, Long requestId);
}
