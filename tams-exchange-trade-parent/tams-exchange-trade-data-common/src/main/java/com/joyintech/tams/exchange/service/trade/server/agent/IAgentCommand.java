package com.joyintech.tams.exchange.service.trade.server.agent;

import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.bean.request.RepurchaseQuoteReceiveQryRequest;
import com.joyintech.tams.exchange.common.bean.response.quote.repurchase.RepurchaseQuoteReceiveResponse;
import com.joyintech.tams.exchange.common.db.domain.*;

import java.util.List;

public interface IAgentCommand {

    /**
     * 请求委托
     *
     * @param accountId
     * @return
     * @throws Exception
     */
    List<Entrust> reqEntrusts(String accountId) throws Exception;

    /**
     * 请求综合业务委托
     *
     * @param accountId
     * @return
     * @throws Exception
     */
    List<IntegratedEntrust> reqIntegratedEntrusts(String accountId) throws Exception;

    /**
     * 请求成交
     *
     * @param accountId
     * @return
     * @throws Exception
     */
    List<Trade> reqTrades(String accountId) throws Exception;

    /**
     * 下单
     *
     * @param entrust
     */
    void entrust(Entrust entrust);

    /**
     * 综合业务下单
     *
     * @param integratedEntrust
     */
    void integratedEntrust(IntegratedEntrust integratedEntrust);

    /**
     * 撤单
     *
     * @param entrust
     */
    void cancelEntrust(Entrust entrust, EntrustInterface cancelEntrust);

    /**
     * 综合业务撤单
     *
     * @param integratedEntrust
     */
    void cancelIntegratedEntrust(IntegratedEntrust integratedEntrust, IntegratedEntrust cancelEntrust);

    /**
     * 账号登录
     *
     * @param accountLoginReqBean
     */
    ResultBean reqLoginAccount(AccountLoginReqBean accountLoginReqBean);

    /**
     * 账号登出
     *
     * @param account
     */
    void reqLogoutAccount(Account account);

    /**
     * 查询大宗交易行情
     *
     * @param quoteQryBean
     * @return
     * @throws Exception
     */
    List<IntegratedQuote> reqIntegratedQuotes(QuoteQryBean quoteQryBean) throws Exception;

    /**
     * 查询最大可交易数量
     *
     * @param qryBean
     * @return
     */
    int reqEnable(EnableQryBean qryBean) throws Exception;

    /**
     * 历史成交明细查询
     *
     * @throws Exception
     */
    List<HistoryTrade> reqHistoryTrade(HistoryQryBean historyQryBean) throws Exception;

    /**
     * 查询账户资金信息
     *
     * @throws Exception
     */
    List<AccountFinance> reqCustomerFinance(String accountId) throws Exception;


    /**
     * 查询账户信息
     *
     * @throws Exception
     */
    List<PositionBean> reqAccountPosition(PositionQueryBean positionQueryBean) throws Exception;

    /**
     * 查询合约信息
     *
     * @throws Exception
     */
    List<Instrument> reqInstrumentInfo(InstrumentQryBean instrumentQryBean) throws Exception;

    /**
     * 请求综合业务委托
     *
     * @param accountId
     * @param market
     * @param stockCode
     * @return
     * @throws Exception
     */
    List<PledgeDiscount> reqPledgeDiscount(String accountId, String market, String stockCode) throws Exception;

    /**
     * 查询可用标准券
     *
     * @param standardBondQryBean
     * @return
     * @throws Exception
     */
    List<StandardBonds> reqStandardBonds(StandardBondQryBean standardBondQryBean) throws Exception;

    /**
     * @param integratedBulkQuoteQryBean
     * @return
     * @description: 大宗行情
     * @date: 2020/9/22 9:31
     */
    List<IntegratedBulkQuoteBean> reqIntegratedBulkQuote(IntegratedBulkQuoteQryBean integratedBulkQuoteQryBean) throws Exception;

    /**
     * @param integratedBulkCloseQuoteQryBean
     * @return
     * @description: 盘后大宗行情
     * @date: 2020/9/22 9:31
     */
    List<IntegratedBulkCloseQuoteBean> reqIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean) throws Exception;

    /**
     * @param integratedRepurchaseQuoteQryBean
     * @return
     * @description: 回购行情
     * @date: 2020/9/22 9:31
     */
    List<IntegratedRepurchaseQuoteBean> reqIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean) throws Exception;

    /**
     * 我收到的协议回购行情
     *
     * @param bean 查询对象
     * @return 我收到的协议回购行情
     * @throws Exception 异常
     */
    List<RepurchaseQuoteReceiveResponse> reqIntegratedRepurchaseQuoteReceive(RepurchaseQuoteReceiveQryRequest bean) throws Exception;

    /**
     * @param integratedFixedIncomeQuoteQryBean
     * @return
     * @description: 固收行情
     * @date: 2020/9/22 9:31
     */
    List<IntegratedFixedIncomeQuoteBean> reqIntegratedFixedIncomeQuote(IntegratedFixedIncomeQuoteQryBean integratedFixedIncomeQuoteQryBean) throws Exception;

    /**
     * @param reissueEntrustQryBean
     * @return
     * @description: 委托重传
     * @date: 2020/9/22 9:31
     */
    void reqReissueEntrust(ReissueEntrustQryBean reissueEntrustQryBean) throws Exception;

    /**
     * @param reissueTradeQryBean
     * @return
     * @description: 成交重传
     * @date: 2020/9/22 9:31
     */
    void reqReissueTrade(ReissueTradeQryBean reissueTradeQryBean) throws Exception;

    /**
     * @param futuresInstrumentQryBean
     * @return
     * @description: 查询期货合约
     * @date: 2020/10/12 9:31
     */
    List<FuturesInstrument> reqFuturesInstrument(FuturesInstrumentQryBean futuresInstrumentQryBean) throws Exception;

    /**
     * @Description: 查询期货持仓
     * @Author: liujianping
     * @Param: futuresPositionQryBean
     * @return: List<FuturesPositionBean>
     * @Date: 2020/11/3
     **/
    List<FuturesPositionBean> reqAccountFuturesPosition(FuturesPositionQryBean futuresPositionQryBean) throws Exception;

    /**
     * @Description: 查询期货持仓明细
     * @Author: liujianping
     * @Param: futuresPositionDetailQryBean
     * @return: List<FuturesPositionDetailBean>
     * @Date: 2020/11/3
     **/
    List<FuturesPositionDetailBean> reqAccountFuturesPositionDetail(FuturesPositionDetailQryBean futuresPositionDetailQryBean) throws Exception;

    /**
     * @Description: 查询期货保证金
     * @Author: liujianping
     * @Param: futuresInstrumentMarginRateQryBean
     * @return: void
     * @Date: 2020/11/23
     **/
    void reqAccountFuturesMarginRate(FuturesInstrumentMarginRateQryBean futuresInstrumentMarginRateQryBean) throws Exception;

    /**
     * @Description: 查询期货手续费
     * @Author: liujianping
     * @Param: futuresInstrumentCommissionRateQryBean
     * @return: void
     * @Date: 2020/11/23
     **/
    void reqAccountFuturesCommissionRate(FuturesInstrumentCommissionRateQryBean futuresInstrumentCommissionRateQryBean) throws Exception;

    /**
     * @param negotiateQuoteQueryBean
     * @return
     * @throws Exception
     * @description: 新债协商成交我收到的行情
     */
    List<NegotiateQuoteReceiveBean> reqNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean) throws Exception;

}
