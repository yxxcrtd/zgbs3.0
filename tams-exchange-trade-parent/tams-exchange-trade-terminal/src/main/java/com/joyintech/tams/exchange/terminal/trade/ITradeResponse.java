package com.joyintech.tams.exchange.terminal.trade;

import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.db.domain.*;

import java.util.List;
import java.util.Map;

/**
 * create By MAXX on 2020/3/25
 */
public interface ITradeResponse {
    /*** 
     * @Description: 登录返回
     * @Param: [resultBean]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/25 
     */
    void resLogin(ResultBean resultBean, String accountId);

    /*** 
     * @Description: 证券委托查询返回
     * @Param: [list, requestId] 
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/27 
     */
    void resQueryEntrusts(List<Entrust> list, Long requestId);

    /*** 
     * @Description: 证券成交查询返回
     * @Param: [list, requestId] 
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/25 
     */
    void resQueryTrade(List<Trade> list, Long requestId);

    /***
     * @Description: 普通委托返回
     * @Param: [entrust]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/27
     */
    void resEntrust(Entrust entrust);

    /***
     * @Description: 综合业务委托确认返回
     * @Param: [integratedEntrust]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/25
     */
    void resIntegratedEntrust(IntegratedEntrust integratedEntrust);

    /*** 
     * @Description: 委托撤单返回
     * @Param: [entrust, requestId] 
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/27 
     */
    void resCancelEntrust(CancelEntrustBean cancelEntrustBean);

    /***
     * @Description: 综合业务委托撤单返回
     * @Param: [integratedEntrust, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/26
     */
    void resCancelIntegratedEntrust(CancelEntrustBean cancelEntrustBean);

    /*** 
     * @Description: 大宗交易行情查询返回
     * @Param: [list, requestId] 
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/27 
     */
    void resQueryIntegratedQuotes(List<IntegratedQuote> list, Long requestId);

    /*** 
     * @Description: 最大交易数量返回
     * @Param: [enableAmount, requestId] 
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/26 
     */
    void resQueryEnable(int enableAmount, Long requestId);

    /*** 
     * @Description: 历史证券成交查询返回
     * @Param: [list, requestId] 
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/26 
     */
    void resQueryHistoryTrade(List<HistoryTrade> list, Long requestId);

    /***
     * @Description: 客户资金快速查询返回
     * @Param: [list]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/25 
     */
    void resQueryCustomerFinance(List<AccountFinance> list, Long requestId);

    /***
     * @Description: 客户资金快速查询返回
     * @Param: [list]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/25
     */
    void resQueryAccountPosition(List<PositionBean> positionList, Long requestId);


    /***
     * @Description: 债券质押率返回
     * @Param: [list, requestId]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/5/9
     */
    void resQueryPledgeDiscount(List<PledgeDiscount> list, Long requestId);

    /***
     * @Description: 标准券查询返回
     * @Param: [list, requestId] 
     * @return: void
     * @Author: MAXX
     * @Date: 2020/5/9 
     */
    void resQueryStandardBonds(List<StandardBonds> list, Long requestId);

    /***
     * @Description: 证券委托回报
     * @Param: [entrust]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/4/7
     */
    void entrustWriteBack(Entrust entrust);

    void integratedEntrustWriteBack(IntegratedEntrust entrust);

    /***
     * @Description: 证券成交回报
     * @Param: [trade]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/4/7
     */
    void dealReturn(Trade trade);

    /**
     * @param instrumentList 合约列表
     * @param requestId      请求编号
     * @author swwan
     * @Description 合约信息返回
     * @Date 2020/6/15
     **/
    void resInstrumentInfo(List<Instrument> instrumentList, Long requestId);

    /***
     * @Description: 大宗交易行情查询返回
     * @Param: [list, requestId]
     * @return: void
     * @Author: liujianping
     * @Date: 2020/9/22
     */
    void resQueryIntegratedBulkQuote(List<IntegratedBulkQuoteBean> list, Long requestId);

    /***
     * @Description: 盘后大宗交易行情查询返回
     * @Param: [list, requestId]
     * @return: void
     * @Author: liujianping
     * @Date: 2020/9/22
     */
    void resQueryIntegratedBulkCloseQuote(List<IntegratedBulkCloseQuoteBean> list, Long requestId);

    /***
     * @Description: 上交所协议回购行情查询返回
     * @Param: [list, requestId]
     * @return: void
     * @Author: liujianping
     * @Date: 2020/9/22
     */
    void resQueryIntegratedRepurchaseQuote(List<IntegratedRepurchaseQuoteBean> list, Long requestId);

    /***
     * @Description: 上交所固定收益行情查询返回
     * @Param: [list, requestId]
     * @return: void
     * @Author: liujianping
     * @Date: 2020/9/22
     */
    void resQueryIntegratedFixedIncomeQuote(List<IntegratedFixedIncomeQuoteBean> list, Long requestId);

    /***
     * @Description: 期货合约查询返回
     * @Param: [list, requestId]
     * @return: void
     * @Author: liujianping
     * @Date: 2020/10/12
     */
    void resQueryFuturesInstrument(List<FuturesInstrument> list, Long requestId);

    /***
     * @Description: 期货持仓查询返回
     * @Param: [list, requestId]
     * @return: void
     * @Author: liujianping
     * @Date: 2020/10/12
     */
    void resQueryFuturesPosition(List<FuturesPositionBean> list, Long requestId);

    /***
     * @Description: 期货持仓明细查询返回
     * @Param: [list, requestId]
     * @return: void
     * @Author: liujianping
     * @Date: 2020/10/12
     */
    void resQueryFuturesPositionDetail(List<FuturesPositionDetailBean> list, Long requestId);

    /***
     * @Description: 期货保证金查询返回
     * @Param: [list, requestId]
     * @return: void
     * @Author: liujianping
     * @Date: 2020/10/12
     */
    void resQueryFuturesInstrumentMarginRate(List<FuturesInstrumentMarginRateBean> list, Long requestId);

    /***
     * @Description: 期货手续费查询返回
     * @Param: [list, requestId]
     * @return: void
     * @Author: liujianping
     * @Date: 2020/10/12
     */
    void resQueryFuturesInstrumentCommissionRate(List<FuturesInstrumentCommissionRateBean> list, Long requestId);

    void resQueryReissueEntrust(String accountId, Map<String, EntrustInterface> entrustInterfaceMap, Long requestId);

    void resQueryReissueTrade(String accountId, List<Trade> tradeList, Long requestId);
    
    /**
     * @Description: 我收到的协商成交行情查询返回
     * @param list
     * @param requestId
     */
    void resQueryNegotiateQuoteReceive(List<NegotiateQuoteReceiveBean> list, Long requestId);
}
