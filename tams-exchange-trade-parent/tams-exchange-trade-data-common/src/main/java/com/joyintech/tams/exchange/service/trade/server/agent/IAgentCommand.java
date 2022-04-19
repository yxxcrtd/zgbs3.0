package com.joyintech.tams.exchange.service.trade.server.agent;

import java.util.List;

import com.joyintech.tams.exchange.common.bean.AccountFinance;
import com.joyintech.tams.exchange.common.bean.AccountLoginReqBean;
import com.joyintech.tams.exchange.common.bean.EnableQryBean;
import com.joyintech.tams.exchange.common.bean.FuturesInstrumentCommissionRateQryBean;
import com.joyintech.tams.exchange.common.bean.FuturesInstrumentMarginRateQryBean;
import com.joyintech.tams.exchange.common.bean.FuturesInstrumentQryBean;
import com.joyintech.tams.exchange.common.bean.FuturesPositionBean;
import com.joyintech.tams.exchange.common.bean.FuturesPositionDetailBean;
import com.joyintech.tams.exchange.common.bean.FuturesPositionDetailQryBean;
import com.joyintech.tams.exchange.common.bean.FuturesPositionQryBean;
import com.joyintech.tams.exchange.common.bean.HistoryQryBean;
import com.joyintech.tams.exchange.common.bean.HistoryTrade;
import com.joyintech.tams.exchange.common.bean.InstrumentQryBean;
import com.joyintech.tams.exchange.common.bean.IntegratedBulkCloseQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedBulkCloseQuoteQryBean;
import com.joyintech.tams.exchange.common.bean.IntegratedBulkQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedBulkQuoteQryBean;
import com.joyintech.tams.exchange.common.bean.IntegratedFixedIncomeQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedFixedIncomeQuoteQryBean;
import com.joyintech.tams.exchange.common.bean.IntegratedQuote;
import com.joyintech.tams.exchange.common.bean.IntegratedRepurchaseQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedRepurchaseQuoteQryBean;
import com.joyintech.tams.exchange.common.bean.NegotiateQuoteQueryBean;
import com.joyintech.tams.exchange.common.bean.NegotiateQuoteReceiveBean;
import com.joyintech.tams.exchange.common.bean.PledgeDiscount;
import com.joyintech.tams.exchange.common.bean.PositionQueryBean;
import com.joyintech.tams.exchange.common.bean.QuoteQryBean;
import com.joyintech.tams.exchange.common.bean.ReissueEntrustQryBean;
import com.joyintech.tams.exchange.common.bean.ReissueTradeQryBean;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.StandardBonds;
import com.joyintech.tams.exchange.common.bean.bond.StandardBondQryBean;
import com.joyintech.tams.exchange.common.db.domain.Account;
import com.joyintech.tams.exchange.common.db.domain.Entrust;
import com.joyintech.tams.exchange.common.db.domain.EntrustInterface;
import com.joyintech.tams.exchange.common.db.domain.FuturesInstrument;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.db.domain.PositionBean;
import com.joyintech.tams.exchange.common.db.domain.Trade;

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
     * @description: 大宗行情
     * @param integratedBulkQuoteQryBean
     * @return
     * @date: 2020/9/22 9:31
     */
    List<IntegratedBulkQuoteBean> reqIntegratedBulkQuote(IntegratedBulkQuoteQryBean integratedBulkQuoteQryBean) throws Exception;

    /**
     * @description: 盘后大宗行情
     * @param integratedBulkCloseQuoteQryBean
     * @return
     * @date: 2020/9/22 9:31
     */
    List<IntegratedBulkCloseQuoteBean> reqIntegratedBulkCloseQuote(IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean) throws Exception;

    /**
     * @description: 回购行情
     * @param integratedRepurchaseQuoteQryBean
     * @return
     * @date: 2020/9/22 9:31
     */
    List<IntegratedRepurchaseQuoteBean> reqIntegratedRepurchaseQuote(IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean) throws Exception;

    /**
     * @description: 固收行情
     * @param integratedFixedIncomeQuoteQryBean
     * @return
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
     * @description: 新债协商成交我收到的行情
     * @param integratedFixedIncomeQuoteQryBean
     * @return
     * @throws Exception
     */
    List<NegotiateQuoteReceiveBean> reqNegotiateQuoteReceive(NegotiateQuoteQueryBean negotiateQuoteQueryBean) throws Exception;

}
