package com.joyintech.tams.exchange.terminal.trade.ufx;

import com.google.common.base.Strings;
import com.hundsun.t2sdk.interfaces.share.dataset.IDataset;
import com.hundsun.t2sdk.interfaces.share.dataset.IDatasets;
import com.hundsun.t2sdk.interfaces.share.event.IEvent;
import com.joyintech.tams.exchange.common.bean.*;
import com.joyintech.tams.exchange.common.constant.*;
import com.joyintech.tams.exchange.common.db.domain.*;
import com.joyintech.tams.exchange.common.utils.*;
import com.joyintech.tams.exchange.terminal.trade.ufx.constant.TradeConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.*;

/**
 * create By MAXX on 2020/3/25
 */
public class UfxConverter {
    protected final static Logger logger = LoggerFactory.getLogger(UfxConverter.class);

    /***
     * @Description: 解析登录信息
     * @Param: [ds, fundAccount] 
     * @return: com.joyintech.tams.exchange.common.db.domain.LoginInfo
     * @Author: MAXX
     * @Date: 2020/3/25 
     */
    public static LoginInfo parseLoginMsg(IDataset ds, String fundAccount) {
        LoginInfo loginInfo = new LoginInfo();
        loginInfo.setInitDate(ds.getInt("init_date"));
        loginInfo.setSysStatus(ds.getString("sys_status"));
        loginInfo.setCompanyName(ds.getString("company_name"));
        loginInfo.setContentType(ds.getString("content_type"));
        loginInfo.setAccountContent(ds.getString("account_content"));
        loginInfo.setBranchNo(ds.getInt("branch_no"));
        loginInfo.setClientId(ds.getString("client_id"));
        loginInfo.setClientName(ds.getString("client_name"));
        loginInfo.setCorpClientGroup(ds.getInt("corp_client_group"));
        loginInfo.setCorpRiskLevel(ds.getInt("corp_risk_level"));
        loginInfo.setCorpEndDate(ds.getInt("corp_end_date"));
        loginInfo.setValidFlag(ds.getString("valid_flag"));
        loginInfo.setFundaccountCount(ds.getInt("fundaccount_count"));
        loginInfo.setFundAccount(fundAccount);
        loginInfo.setClientRights(ds.getString("client_rights"));
        loginInfo.setLastOpEntrustWay(ds.getString("last_op_entrust_way"));
        loginInfo.setLastOpStation(ds.getString("last_op_station"));
        loginInfo.setSysnodeId(ds.getInt("sysnode_id"));
        loginInfo.setUserToken(ds.getString("user_token"));
        loginInfo.setErrorNo(ds.getInt("error_no"));
        loginInfo.setErrorInfo(ds.getString("error_info"));
        loginInfo.setAssetProp(ds.getString("asset_prop"));
        loginInfo.setProductFlag(ds.getString("product_flag"));
        loginInfo.setMessageFlag(ds.getString("message_flag"));
        loginInfo.setTabconfirmFlag(ds.getString("tabconfirm_flag"));
        loginInfo.setLastDate(ds.getInt("last_date"));
        loginInfo.setSessionNo(ds.getInt("session_no"));
        loginInfo.setForeignFlag(ds.getString("foreign_flag"));
        loginInfo.setPasswordRiskTips(ds.getString("password_risk_tips"));
        loginInfo.setInputContent(ds.getString("input_content"));
        loginInfo.setTerminalVersion(ds.getString("terminal_version"));
        loginInfo.setUftSysnodeId(ds.getInt("uft_sysnode_id"));
        return loginInfo;
    }

    /*** 
     * @Description: 客户资金快速查询返回解析
     * @Param: [rsp]
     * @return: java.util.List<com.joyintech.tams.exchange.common.bean.CustomerFinance>
     * @Author: MAXX
     * @Date: 2020/3/25
     */
    public static List<AccountFinance> queryCustomerFinanceReMsg(String accountId, IEvent rsp) {
        IDatasets result = rsp.getEventDatas();
        IDataset ds = result.getDataset(0);
        List<AccountFinance> list = new ArrayList<AccountFinance>();
        ds.beforeFirst();
        while (ds.hasNext()) {
            ds.next();
            AccountFinance accountFinance = new AccountFinance();
            accountFinance.setAccountId(accountId);
            accountFinance.setMoneyType(ds.getString("money_type"));
            accountFinance.setFinanceBalance(ds.getDouble("current_balance"));
            accountFinance.setEnableBalance(ds.getDouble("enable_balance"));
            accountFinance.setTotalFrozenFinance(ds.getDouble("frozen_balance"));
            list.add(accountFinance);
        }
        return list;
    }

    /*** 
     * @Description: 证券成交查询返回数据解析
     * @Param: [rsp, accountId]
     * @return: java.util.List<com.joyintech.tams.exchange.common.db.domain.Trade>
     * @Author: MAXX
     * @Date: 2020/3/25 
     */
    public static List<Trade> parseQueryTradeMsg(IEvent rsp, String accountId) {
        IDatasets result = rsp.getEventDatas();
        IDataset ds = result.getDataset(0);
        List<Trade> list = new ArrayList<Trade>();
        ds.beforeFirst();
        while (ds.hasNext()) {
            ds.next();
            int real_status = ds.getInt("real_status");//处理标志
            String real_type = ds.getString("real_type");
            logger.info("real_status:{} ,brokerEntrustId:{},real_type:{},entrust_no:{},business_id:{})",
                    real_status, ds.getString("entrust_no"), real_type, ds.getString("entrust_no"), ds.getString("business_id"));
            Trade trade = new Trade();
            trade.setPositionStr(ds.getString("position_str"));
            trade.setAccountId(accountId);
            trade.setTradingDay(ds.getString("date"));
            String exchangeType = ds.getString("exchange_type");
            if (ExchangeType.SH.value.equals(exchangeType)) {
                trade.setMarket(Market.SHSE.value);
            } else if (ExchangeType.SZ.value.equals(exchangeType)) {
                trade.setMarket(Market.SZSE.value);
            }
            trade.setFinanceAccount(ds.getString("fund_account"));
            trade.setStockCode(ds.getString("stock_code"));
            trade.setEntrustBs(ds.getString("entrust_bs"));
            trade.setTradedPrice(BigDecimal.valueOf(ds.getDouble("business_price")));
            String businessAmount = ds.getString("business_amount");
            double business_amount = Double.parseDouble(businessAmount);
            //撤单成交成交数量小于0
            trade.setTradedVolume((long) (business_amount < 0 ? -business_amount : business_amount));
            trade.setTradeTime(ds.getString("business_time"));
            convertTradeTypeAndBrokerTradeId(ds, real_status, real_type, trade);
            trade.setBrokerEntrustId(ds.getString("entrust_no"));
            trade.setErrorMsg(ErrorMsgUtils.subErrorMsg(ds.getString("cancel_info")));
            trade.setTradedBalance(BigDecimal.valueOf(ds.getDouble("business_balance")));
            trade.setStockName(ds.getString("stock_name"));
            trade.setEntrustProp(ds.getString("entrust_prop"));
            trade.setReportNo(ds.getString("report_no"));
            trade.setEntrustType(ds.getString("entrust_type"));
            list.add(trade);
        }
        return list;
    }

    private static void convertTradeTypeAndBrokerTradeId(IDataset ds, int real_status, String real_type, Trade trade) {
        if (real_status != TradeConstant.realStatusTrade && real_status != TradeConstant.realStatusConfirm) {
            trade.setBrokerTradeId(ds.getString("entrust_no"));
            if (TradeConstant.realTypeCancel.equals(real_type)) {
                trade.setTradeType(TradeType.CANCEL_INVALID.value);
            } else if (TradeConstant.realTypeTrade.equals(real_type)) {
                trade.setTradeType(TradeType.INVALID.value);
            }
        } else {
            trade.setBrokerTradeId(ds.getString("business_id"));
            if (TradeConstant.realTypeCancel.equals(real_type)) {
                trade.setTradeType(TradeType.CANCEL.value);
            } else if (TradeConstant.realTypeTrade.equals(real_type)) {
                trade.setTradeType(TradeType.NORMAL.value);
            }
        }
    }

    /*** 
     * @Description: 历史证券成交查询返回解析
     * @Param: [rsp, historyQryBean]
     * @return: java.util.List<com.joyintech.tams.exchange.common.bean.HistoryTrade>
     * @Author: MAXX
     * @Date: 2020/3/26
     */
    public static List<HistoryTrade> qryHisBusinessReback(IEvent rsp, HistoryQryBean historyQryBean) {
        IDatasets result = rsp.getEventDatas();
        IDataset ds = result.getDataset(0);
        List<HistoryTrade> list = new ArrayList<HistoryTrade>();
        ds.beforeFirst();
        while (ds.hasNext()) {
            ds.next();
            HistoryTrade historyTrade = new HistoryTrade();
            historyTrade.setTradingDay(ds.getString("init_date"));
            String exchangeType = ds.getString("exchange_type");
            if (ExchangeType.SH.value.equals(exchangeType)) {
                historyTrade.setMarket(Market.SHSE.value);
            } else if (ExchangeType.SZ.value.equals(exchangeType)) {
                historyTrade.setMarket(Market.SZSE.value);
            }
            historyTrade.setStockHolderId(ds.getString("stock_account"));
            historyTrade.setStockCode(ds.getString("stock_code"));
            historyTrade.setStockType(ds.getString("stock_type"));
            historyTrade.setStockName(ds.getString("stock_name"));
            String entrustBs = ds.getString("entrust_bs");
            if (EntrustBs.BUY.value.equals(entrustBs)) {
                historyTrade.setBsFlag(BSFlag.ZQMR.value);
            } else if (EntrustBs.SELL.value.equals(entrustBs)) {
                historyTrade.setBsFlag(BSFlag.ZQMC.value);
            }
            historyTrade.setTradePrice(ds.getDouble("business_price"));
            historyTrade.setTradeTime(ds.getString("business_time"));
            historyTrade.setOrderId(ds.getString("entrust_no"));
            historyTrade.setTradedBalance(ds.getDouble("business_balance"));
            historyTrade.setFinanceAmount(ds.getDouble("occur_balance"));
            historyTrade.setProcedureCost(ds.getDouble("fare0"));
            historyTrade.setPrintingCost(ds.getDouble("fare1"));
            historyTrade.setTransferCost(ds.getDouble("fare2"));
            historyTrade.setPostStr(ds.getString("position_str"));
            historyTrade.setBrokerTradeId(ds.getString("business_id"));
            historyTrade.setOtherCost(ds.getDouble("exchange_fare6"));
            historyTrade.setJinShouXuFei(ds.getDouble("clear_fare0"));
            historyTrade.setTradingDay(ds.getString("date_back"));
            historyTrade.setAccountId(historyQryBean.getAccountId());
            historyTrade.setFinanceAccount(ds.getString("fund_account"));
            String businessAmount = ds.getString("business_amount");
            double business_amount = Double.parseDouble(businessAmount);
            historyTrade.setTradeVolume((long) business_amount);
            historyTrade.setBrokerTradeId(ds.getString("business_no"));
            list.add(historyTrade);
        }
        return list;
    }

    /***
     * @Description: 大宗交易行情查询返回解析
     * @Param: [integratedQuote, rsp]
     * @return: java.util.List<com.joyintech.tams.exchange.common.bean.IntegratedQuote>
     * @Author: MAXX
     * @Date: 2020/3/27 
     */
    public static List<IntegratedQuote> queryIntegratedQuotesReBack(QuoteQryBean quoteQryBean, IEvent rsp) {
        IDatasets result = rsp.getEventDatas();
        IDataset ds = result.getDataset(0);
        List<IntegratedQuote> list = new ArrayList<IntegratedQuote>();
        ds.beforeFirst();
        while (ds.hasNext()) {
            ds.next();
            IntegratedQuote integratedQuoteBt = new IntegratedQuote();
            integratedQuoteBt.setContractId(ds.getString("cbpconfer_id"));
            integratedQuoteBt.setBrokerEntrustId(ds.getString("entrust_no"));
            integratedQuoteBt.setBuyConfirmId(ds.getString("buy_confirm_id"));
            String cbptransType = ds.getString("cbptrans_type", null);
            if (!Strings.isNullOrEmpty(cbptransType)) {
                if (CbptransType.YXMR.value.equals(cbptransType)) {
                    integratedQuoteBt.setBsFlag(BSFlag.YXMR.value);
                } else if (CbptransType.YXMC.value.equals(cbptransType)) {
                    integratedQuoteBt.setBsFlag(BSFlag.YXMC.value);
                } else if (CbptransType.DJMR.value.equals(cbptransType)) {
                    integratedQuoteBt.setBsFlag(BSFlag.DJMR.value);
                } else if (CbptransType.DJMC.value.equals(cbptransType)) {
                    integratedQuoteBt.setBsFlag(BSFlag.DJMC.value);
                }
                integratedQuoteBt.setEntrustProp(EntrustUtils.getEntrustProp(integratedQuoteBt.getBsFlag(), integratedQuoteBt.getMarket()));
                integratedQuoteBt.setEntrustBs(EntrustUtils.getEntrustBs(integratedQuoteBt.getBsFlag()));
            }
            String entrust_prop = ds.getString("entrust_prop", null);
            if (!Strings.isNullOrEmpty(entrust_prop)) {
                integratedQuoteBt.setEntrustProp(entrust_prop);
            }
            String entrust_bs = ds.getString("entrust_bs", null);
            if (!Strings.isNullOrEmpty(entrust_bs)) {
                integratedQuoteBt.setEntrustBs(entrust_bs);
            }
            String exchangeType = ds.getString("exchange_type", null);
            if (!Strings.isNullOrEmpty(exchangeType)) {
                if (ExchangeType.SH.value.equals(exchangeType)) {
                    integratedQuoteBt.setMarket(Market.SHSE.value);
                } else if (ExchangeType.SZ.value.equals(exchangeType)) {
                    integratedQuoteBt.setMarket(Market.SZSE.value);
                }
            }
            integratedQuoteBt.setStockType(ds.getString("stock_type"));
            integratedQuoteBt.setStockCode(ds.getString("stock_code"));
            integratedQuoteBt.setStockName(ds.getString("stock_name"));
            String expire_year_rate = ds.getString("expire_year_rate", null);
            if (!Strings.isNullOrEmpty(expire_year_rate)) {
                integratedQuoteBt.setExpireIncomeRate(ds.getDouble("expire_year_rate"));
            }
            String repo_term = ds.getString("repo_term", null);
            if (!Strings.isNullOrEmpty(repo_term)) {
                integratedQuoteBt.setCompactTerm(ds.getInt("repo_term"));
            }
            String fund_used_days = ds.getString("fund_used_days", null);
            if (!Strings.isNullOrEmpty(fund_used_days)) {
                integratedQuoteBt.setRealOccupyDays(ds.getInt("fund_used_days"));
            }
            integratedQuoteBt.setOrigTradeDate(ds.getString("first_exchdate"));
            integratedQuoteBt.setBackDate(ds.getString("repurchase_date"));
            integratedQuoteBt.setExpireSettleDate(ds.getString("settle_end_date"));
            String component_num = ds.getString("component_num", null);
            if (!Strings.isNullOrEmpty(component_num)) {
                integratedQuoteBt.setComponentNum(ds.getInt("component_num"));
            }
            integratedQuoteBt.setOrigPledgeStampCode(ds.getString("old_impawn_code"));
            String impawn_amount = ds.getString("impawn_amount", null);
            if (!Strings.isNullOrEmpty(impawn_amount)) {
                int impawnAmount = Integer.parseInt(impawn_amount.substring(0, impawn_amount.lastIndexOf(".")));
                integratedQuoteBt.setPledgeStampVolume(impawnAmount);
            }
            String funder_ratio = ds.getString("funder_ratio", null);
            if (!Strings.isNullOrEmpty(funder_ratio)) {
                integratedQuoteBt.setDiscount(ds.getDouble("funder_ratio"));
            }
            String impawn_balance = ds.getString("impawn_balance", null);
            if (!Strings.isNullOrEmpty(impawn_balance)) {
                integratedQuoteBt.setImpawnBalance(ds.getDouble("impawn_balance"));
            }
            String prev_impawn_balance = ds.getString("prev_impawn_balance", null);
            if (!Strings.isNullOrEmpty(prev_impawn_balance)) {
                integratedQuoteBt.setPrevImpawnBalance(ds.getDouble("prev_impawn_balance"));
            }
            String profit = ds.getString("profit", null);
            if (!Strings.isNullOrEmpty(profit)) {
                integratedQuoteBt.setBackInterest(ds.getDouble("profit"));
            }
            String settle_balance = ds.getString("settle_balance", null);
            if (!Strings.isNullOrEmpty(settle_balance)) {
                integratedQuoteBt.setExpireSettleBalance(ds.getDouble("settle_balance"));
            }
            String asset_net_value = ds.getString("asset_net_value", null);
            if (!Strings.isNullOrEmpty(asset_net_value)) {
                integratedQuoteBt.setPledgeStampBalance(ds.getDouble("asset_net_value"));
            }
            integratedQuoteBt.setBprsrcStatus(ds.getString("bprsrc_status"));
            integratedQuoteBt.setOrigEntrustDate(ds.getString("orig_entrust_date"));
            integratedQuoteBt.setOrigCbpconferId(ds.getString("orig_cbpconfer_id"));
            String agency_no = ds.getString("agency_no", null);
            if (!Strings.isNullOrEmpty(agency_no)) {
                integratedQuoteBt.setAgencyNo(agency_no);
            }
            integratedQuoteBt.setPropSeatNo(ds.getString("prop_seat_no"));
            String trader_id = ds.getString("trader_id", null);
            if (!Strings.isNullOrEmpty(trader_id)) {
                integratedQuoteBt.setLiaisonTel(trader_id);
            }
            integratedQuoteBt.setFundAccount(ds.getString("fund_account"));
            integratedQuoteBt.setStockAccount(ds.getString("stock_account"));
            integratedQuoteBt.setReportTimeX(ds.getString("report_time_x"));
            integratedQuoteBt.setReportTime(ds.getString("report_time"));
            integratedQuoteBt.setPropSeatName(ds.getString("agency_name"));
            integratedQuoteBt.setRemark(ds.getString("remark"));
            integratedQuoteBt.setAccountId(quoteQryBean.getAccountId());
            integratedQuoteBt.setOrderNo(ds.getString("order_no"));
            integratedQuoteBt.setPropAccountName(ds.getString("prop_account_name"));
            integratedQuoteBt.setSeatNo(ds.getString("seat_no"));
            String oppo_agency = ds.getString("oppo_agency", null);
            if (!Strings.isNullOrEmpty(oppo_agency)) {
                integratedQuoteBt.setOppositePropSeatNo(oppo_agency);
            }
            String other_agency = ds.getString("other_agency", null);
            if (!Strings.isNullOrEmpty(other_agency)) {
                integratedQuoteBt.setOppositePropSeatNo(other_agency);
            }
            String sell_price = ds.getString("sell_price", null);
            if (!Strings.isNullOrEmpty(sell_price)) {
                integratedQuoteBt.setSellPrice(ds.getDouble("sell_price"));
            }
            String sell_quote_amount = ds.getString("sell_quote_amount", null);
            if (!Strings.isNullOrEmpty(sell_quote_amount)) {
                int sellQuoteAmount = Integer.parseInt(sell_quote_amount.substring(0, sell_quote_amount.lastIndexOf(".")));
                integratedQuoteBt.setSellQuoteAmount(sellQuoteAmount);
            }
            String sell_quote_price = ds.getString("sell_quote_price", null);
            if (!Strings.isNullOrEmpty(sell_quote_price)) {
                integratedQuoteBt.setSellQuotePrice(ds.getDouble("sell_quote_price"));
            }
            String sell_exchange_rate = ds.getString("sell_exchange_rate", null);
            if (!Strings.isNullOrEmpty(sell_exchange_rate)) {
                integratedQuoteBt.setSellExchangeRate(ds.getDouble("sell_exchange_rate"));
            }
            String stock_interest = ds.getString("stock_interest", null);
            if (!Strings.isNullOrEmpty(stock_interest)) {
                integratedQuoteBt.setBondAccruedRate(ds.getDouble("stock_interest"));
            }
            integratedQuoteBt.setOppositeLiaisonTel(ds.getString("oppo_trader_id"));
            integratedQuoteBt.setPosstr(ds.getString("position_str"));
            integratedQuoteBt.setOppositePropSeatName(ds.getString("oppo_agency_name"));
            integratedQuoteBt.setPropStockAccount(ds.getString("prop_stock_account"));
            integratedQuoteBt.setRealDateBack(ds.getString("real_date_back"));
            integratedQuoteBt.setZQRName(ds.getString("pledgee_name"));
            String entrust_price = ds.getString("entrust_price", null);
            if (!Strings.isNullOrEmpty(entrust_price)) {
                integratedQuoteBt.setEntrustPrice(ds.getDouble("entrust_price"));
            }
            String buy_price = ds.getString("buy_price", null);
            if (!Strings.isNullOrEmpty(buy_price)) {
                integratedQuoteBt.setBuyPrice(ds.getDouble("buy_price"));
            }
            String buy_quote_amount = ds.getString("buy_quote_amount", null);
            if (!Strings.isNullOrEmpty(buy_quote_amount)) {
                int buyQuoteAmount = Integer.parseInt(buy_quote_amount.substring(0, buy_quote_amount.lastIndexOf(".")));
                integratedQuoteBt.setBuyQuoteAmount(buyQuoteAmount);
            }
            String buy_quote_price = ds.getString("buy_quote_price", null);
            if (!Strings.isNullOrEmpty(buy_quote_price)) {
                integratedQuoteBt.setBuyQuotePrice(ds.getDouble("buy_quote_price"));
            }
            String buy_exchange_rate = ds.getString("buy_exchange_rate", null);
            if (!Strings.isNullOrEmpty(buy_exchange_rate)) {
                integratedQuoteBt.setBuyExchangeRate(ds.getDouble("buy_exchange_rate"));
            }
            integratedQuoteBt.setSellConfirmId(ds.getString("sell_confirm_id"));
            String entrust_amount = ds.getString("entrust_amount", null);
            if (!Strings.isNullOrEmpty(entrust_amount)) {
                int entrustAmount = (int) Double.parseDouble(entrust_amount);
                integratedQuoteBt.setEntrustVolume(entrustAmount);
            }
            integratedQuoteBt.setLiaisonName(ds.getString("relation_name"));
            String relation_tel = ds.getString("relation_tel", null);
            if (!Strings.isNullOrEmpty(relation_tel)) {
                integratedQuoteBt.setLiaisonTel(relation_tel);
            }
            list.add(integratedQuoteBt);
        }
        return list;
    }

    /*** 
     * @Description: 委托下单返回
     * @Param: [entrust, reBack] 
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/27
     */
    public static void entrustReBack(Entrust entrust, IDataset ds) {
        entrust.setTradingDay(ds.getString("init_date"));
        entrust.setBrokerEntrustId(ds.getString("entrust_no"));
        entrust.setReportTime(ds.getString("entrust_time"));
        //entrust.setId(ds.getString("entrust_reference"));
        entrust.setReportNo(ds.getString("report_no"));
    }

    /***
     * @Description: 委托撤单返回
     * @Param: [entrust, reBack]
     * @return: void
     * @Author: MAXX
     * @Date: 2020/3/27
     */
    public static void cancelEntrustReBack(CancelEntrustBean cancelEntrustBean, IDataset ds) {
        cancelEntrustBean.setTradingDay(ds.getString("init_date"));
        cancelEntrustBean.setCancelBrokerEntrustId(ds.getString("entrust_no"));
        cancelEntrustBean.setBrokerEntrustId(ds.getString("entrust_no_old"));
        String exchangeType = ds.getString("exchange_type");
        if (ExchangeType.SH.value.equals(exchangeType)) {
            cancelEntrustBean.setMarket(Market.SHSE.value);
        } else if (ExchangeType.SZ.value.equals(exchangeType)) {
            cancelEntrustBean.setMarket(Market.SZSE.value);
        }
        cancelEntrustBean.setStockCode(ds.getString("stock_code"));
    }

    /*** 
     * @Description: 证券委托查询返回数据解析
     * @Param: [rsp]
     * @return: java.util.List<com.joyintech.tams.exchange.common.db.domain.Entrust>
     * @Author: MAXX
     * @Date: 2020/3/30 
     */
    public static List<Entrust> queryOrderReBack(IEvent rsp, String accountId) {
        IDatasets result = rsp.getEventDatas();
        IDataset ds = result.getDataset(0);
        List<Entrust> list = new ArrayList<Entrust>();
        ds.beforeFirst();
        while (ds.hasNext()) {
            ds.next();
            Entrust entrust = new Entrust();
            entrust.setAccountId(accountId);
            entrust.setPositionStr(ds.getString("position_str", null));
            entrust.setTradingDay(ds.getString("init_date"));
            entrust.setBrokerEntrustId(ds.getString("entrust_no"));
            entrust.setTradingDay(ds.getString("entrust_date"));
            entrust.setReportTime(ds.getString("entrust_time", null));
            entrust.setFinanceAccount(ds.getString("fund_account"));
            String exchangeType = ds.getString("exchange_type");
            if (ExchangeType.SH.value.equals(exchangeType)) {
                entrust.setMarket(Market.SHSE.value);
            } else if (ExchangeType.SZ.value.equals(exchangeType)) {
                entrust.setMarket(Market.SZSE.value);
            }
            entrust.setStockCode(ds.getString("stock_code"));
            entrust.setStockName(ds.getString("stock_name"));
            entrust.setEntrustBs(ds.getString("entrust_bs"));
            entrust.setEntrustType(ds.getString("entrust_type"));
            entrust.setEntrustProp(ds.getString("entrust_prop"));
            if (FullPriceConst.FULL_PRICE.value.equals(entrust.getIsFullPrice()) || (Strings.isNullOrEmpty(entrust.getIsFullPrice()) && EntrustUtils.isFullPriceTerminal(entrust.getMarket(), entrust.getStockCode(), entrust.getAssetType(), entrust.getEntrustProp()))) {
                entrust.setFullPrice(BigDecimal.valueOf(ds.getDouble("entrust_price")));
                entrust.setIsFullPrice(FullPriceConst.FULL_PRICE.value);
            } else {
                entrust.setEntrustPrice(BigDecimal.valueOf(ds.getDouble("entrust_price")));
                entrust.setIsFullPrice(FullPriceConst.NET_PRICE.value);
            }
            String entrustAmount = ds.getString("entrust_amount", null);
            if (!Strings.isNullOrEmpty(entrustAmount)) {
                double entrust_amount = Double.parseDouble(entrustAmount);
                entrust.setEntrustVolume((long) entrust_amount);
            }
            String businessAmount = ds.getString("business_amount", null);
            if (!Strings.isNullOrEmpty(businessAmount)) {
                try {
                    double business_amount = Double.parseDouble(businessAmount);
                    entrust.setTradedVolume((long) business_amount);
                } catch (Exception e) {
                    logger.error("businessAmount:{}", e.getMessage());
                }
            }
            entrust.setTradedPrice(BigDecimal.valueOf(ds.getDouble("business_price")));
            entrust.setReportTime(ds.getString("report_time", null));
            try {
                String entrustStatus = ds.getString("entrust_status");
                if (entrustStatus.lastIndexOf(".") == -1) {
                    getEntrustStatusByUfx(entrustStatus, entrust);
                } else {
                    logger.error("entrustStatus:{}", entrustStatus);
                }
            } catch (Exception e) {
                logger.error("entrustStatus:{}", e.getMessage());
            }
            entrust.setErrorMsg(ErrorMsgUtils.subErrorMsg(ds.getString("cancel_info")));
            String withdrawAmount = ds.getString("withdraw_amount", null);
            if (!Strings.isNullOrEmpty(withdrawAmount)) {
                double withdraw_amount = Double.parseDouble(withdrawAmount);
                entrust.setCancelVolume((long) withdraw_amount);
            }
            entrust.setId(ds.getString("entrust_reference"));
            entrust.setReportNo(ds.getString("report_no"));
            list.add(entrust);
        }
        return list;
    }

    /***
     * @Description: 证券委托回报返回解析
     * @Param: [obj]
     * @return: com.joyintech.tams.exchange.common.db.domain.Entrust
     * @Author: MAXX
     * @Date: 2020/4/7
     */
    public static Entrust entrustWriteBack(IDataset ds, UfxTradeApi tradeApi) {
        Entrust entrust = new Entrust();
        entrust.setId(ds.getString("entrust_reference"));
        String businessAmount = ds.getString("business_amount");
        double business_amount = Double.parseDouble(businessAmount);
        entrust.setTradedVolume((long) business_amount);
        entrust.setTradedPrice(BigDecimal.valueOf(ds.getDouble("business_price")));
        String exchangeType = ds.getString("exchange_type");
        if (ExchangeType.SH.value.equals(exchangeType)) {
            entrust.setMarket(Market.SHSE.value);
        } else if (ExchangeType.SZ.value.equals(exchangeType)) {
            entrust.setMarket(Market.SZSE.value);
        }
        String entrustAmount = ds.getString("entrust_amount");
        double entrust_amount = Double.parseDouble(entrustAmount);
        entrust.setEntrustVolume((long) entrust_amount);
        entrust.setStockCode(ds.getString("stock_code"));
        entrust.setEntrustBs(ds.getString("entrust_bs"));
        entrust.setBrokerEntrustId(ds.getString("entrust_no"));
        if (FullPriceConst.FULL_PRICE.value.equals(entrust.getIsFullPrice()) || (Strings.isNullOrEmpty(entrust.getIsFullPrice()) && EntrustUtils.isFullPriceTerminal(entrust.getMarket(), entrust.getStockCode(), entrust.getAssetType(), entrust.getEntrustProp()))) {
            entrust.setFullPrice(BigDecimal.valueOf(ds.getDouble("entrust_price")));
            entrust.setIsFullPrice(FullPriceConst.FULL_PRICE.value);
        } else {
            entrust.setEntrustPrice(BigDecimal.valueOf(ds.getDouble("entrust_price")));
            entrust.setIsFullPrice(FullPriceConst.NET_PRICE.value);
        }
        PositionStr positionStr = tradeApi.getPositionStrMap(ds.getString("fund_account"));
        if (Objects.isNull(positionStr)) {
            positionStr = new PositionStr();
        }
        Set<String> entrustNoSet = positionStr.getEntrustNoSet();
        if (Objects.isNull(entrustNoSet)) {
            entrustNoSet = new HashSet<String>();
        }
        entrustNoSet.add(ds.getString("entrust_no"));
        positionStr.setEntrustNoSet(entrustNoSet);
        tradeApi.putPositionStrMap(ds.getString("fund_account"), positionStr);
        entrust.setEntrustType(ds.getString("entrust_type"));
        String entrust_status = ds.getString("entrust_status");
        getEntrustStatusByUfx(entrust_status, entrust);
        String fundAccount = ds.getString("fund_account");
        entrust.setFinanceAccount(fundAccount);
        entrust.setAccountId(tradeApi.getUfxTradeClient().getAccountIdByFinanceAccount(fundAccount));
        entrust.setErrorMsg(ErrorMsgUtils.subErrorMsg(ds.getString("cancel_info")));
        entrust.setEntrustProp(ds.getString("entrust_prop"));
        entrust.setReportTime(ds.getString("report_time"));
        entrust.setReportNo(ds.getString("report_no"));
        return entrust;
    }

    public static IntegratedEntrust integratedEntrustWriteBack(IDataset ds, UfxTradeApi ufxTradeApi) {
        String fundAccount = ds.getString("fund_account");
        String accountId = ufxTradeApi.getUfxTradeClient().getAccountIdByFinanceAccount(fundAccount);
        return createIntegratedEntrust(accountId, ds);
    }

    /*** 
     * @Description: 证券成交回报解析
     * @Param: [obj]
     * @return: com.joyintech.tams.exchange.common.db.domain.Trade
     * @Author: MAXX
     * @Date: 2020/4/7
     */
    public static Trade dealReturn(IDataset ds, int realStatus, String realType, UfxTradeApi tradeApi) {
        Trade trade = new Trade();
        trade.setEntrustId(ds.getString("entrust_reference"));
        trade.setTradingDay(ds.getString("init_date"));
        String fundAccount = ds.getString("fund_account");
        trade.setFinanceAccount(fundAccount);
        trade.setAccountId(tradeApi.getUfxTradeClient().getAccountIdByFinanceAccount(fundAccount));
        convertTradeTypeAndBrokerTradeId(ds, realStatus, realType, trade);
        // 成交撤单主推时。成交的委托编号 对应 撤单号
        trade.setBrokerEntrustId(ds.getString("entrust_no"));
        if (TradeConstant.realTypeCancel.equals(realType)) {
            trade.setBrokerEntrustId(ds.getString("withdraw_no"));
        }
        String exchangeType = ds.getString("exchange_type");
        if (ExchangeType.SH.value.equals(exchangeType)) {
            trade.setMarket(Market.SHSE.value);
        } else if (ExchangeType.SZ.value.equals(exchangeType)) {
            trade.setMarket(Market.SZSE.value);
        }
        trade.setStockCode(ds.getString("stock_code"));
        trade.setEntrustBs(ds.getString("entrust_bs"));
        String businessAmount = ds.getString("business_amount");
        double business_amount = Double.parseDouble(businessAmount);
        trade.setTradedVolume((long) (business_amount < 0 ? -business_amount : business_amount));
        trade.setTradedPrice(BigDecimal.valueOf(ds.getDouble("business_price")));
        trade.setTradeTime(ds.getString("business_time"));
        trade.setEntrustType(ds.getString("entrust_type"));
        PositionStr positionStr = tradeApi.getPositionStrMap(ds.getString("fund_account"));
        if (Objects.isNull(positionStr)) {
            positionStr = new PositionStr();
        }
        Set<String> businessIdSet = positionStr.getBusinessIdSet();
        if (Objects.isNull(businessIdSet)) {
            businessIdSet = new HashSet<String>();
        }
        businessIdSet.add(ds.getString("business_id"));
        positionStr.setBusinessIdSet(businessIdSet);
        tradeApi.putPositionStrMap(ds.getString("fund_account"), positionStr);
        trade.setTradedBalance(BigDecimal.valueOf(ds.getDouble("business_balance")));
        trade.setStockName(ds.getString("stock_name"));
        trade.setEntrustProp(ds.getString("entrust_prop"));
        trade.setReportNo(ds.getString("report_no"));
        return trade;
    }


    public static Trade dealIntegratedTrade(IDataset ds, int realStatus, UfxTradeApi ufxTradeApi) {
        String fundAccount = ds.getString("fund_account");
        String accountId = ufxTradeApi.getUfxTradeClient().getAccountIdByFinanceAccount(fundAccount);
        return createTrade(accountId, ds, realStatus);
    }

    /***
     * @Description: 质押回购可用查询返回
     * @Param: [rsp]
     * @return: java.util.List<com.joyintech.tams.exchange.common.bean.StandardBonds>
     * @Author: MAXX
     * @Date: 2020/5/11
     */
    public static List<StandardBonds> standardBondsReturn(IEvent rsp) {
        IDatasets result = rsp.getEventDatas();
        IDataset ds = result.getDataset(0);
        List<StandardBonds> list = new ArrayList<StandardBonds>();
        ds.beforeFirst();
        while (ds.hasNext()) {
            ds.next();
            StandardBonds standardBonds = new StandardBonds();
            standardBonds.setStockCode(ds.getString("bond_code"));
            String exchangeType = ds.getString("exchange_type");
            if (ExchangeType.SH.value.equals(exchangeType)) {
                standardBonds.setMarket(Market.SHSE.value);
            } else if (ExchangeType.SZ.value.equals(exchangeType)) {
                standardBonds.setMarket(Market.SZSE.value);
            }
            String bondFinAmount = ds.getString("bond_fin_amount");
            double bond_fin_amount = Double.parseDouble(bondFinAmount);
            standardBonds.setBondEnable((int) bond_fin_amount);
            standardBonds.setStockAccount(ds.getString("stock_account"));
            standardBonds.setStockName(ds.getString("bond_name"));
            standardBonds.setNetAsset(ds.getDouble("net_asset"));
            String enoutAmount = ds.getString("enout_amount");
            double enout_amount = Double.parseDouble(enoutAmount);
            standardBonds.setBondEnout((int) enout_amount);
            list.add(standardBonds);
        }
        return list;
    }

    /**
     * 合约数据查询
     *
     * @param rsp
     * @return
     */
    public static List<Instrument> instrumentInfoReturn(IEvent rsp) {
        logger.info("instrumentInfoReturn start");
        IDatasets result = rsp.getEventDatas();
        IDataset ds = result.getDataset(0);
        List<Instrument> list = new ArrayList<Instrument>();
        ds.beforeFirst();
        while (ds.hasNext()) {
            ds.next();
            Instrument instrument = new Instrument();
            instrument.setStockCode(ds.getString("stock_code"));
            String exchangeType = ds.getString("exchange_type");
            if (ExchangeType.SH.value.equals(exchangeType)) {
                instrument.setMarket(Market.SHSE.value);
                instrument.setInnerCode(instrument.getStockCode() + "." + Market.SHSE.value);
            } else if (ExchangeType.SZ.value.equals(exchangeType)) {
                instrument.setMarket(Market.SZSE.value);
                instrument.setInnerCode(instrument.getStockCode() + "." + Market.SZSE.value);
            }
            instrument.setStockName(ds.getString("stock_name"));
            instrument.setBuyUnit(ds.getInt("buy_unit"));
            instrument.setPriceStep(UfxConverter.getPriceStep(ds.getString("price_step")));
            instrument.setStoreUnit(ds.getInt("store_unit"));
            instrument.setUpPrice(BigDecimal.valueOf(ds.getDouble("up_price")));
            instrument.setDownPrice(BigDecimal.valueOf(ds.getDouble("down_price")));
            instrument.setStkcodeStatus(ds.getString("stkcode_status"));
            instrument.setAssetType(ds.getString("stock_type"));
            instrument.setHighVolume(ds.getLong("high_amount"));
            instrument.setLowVolume(ds.getInt("low_amount"));
            instrument.setTradePlat(ds.getString("trade_plat"));
            instrument.setPostStr(ds.getString("position_str"));
            instrument.setParValue(BigDecimal.valueOf(ds.getDouble("par_value")));
            instrument.setStbtransType(ds.getString("stbtrans_type"));
            instrument.setStbtransStatus(ds.getString("stbtrans_status"));
            instrument.setSubStockType(ds.getString("sub_stock_type"));
            instrument.setLowBalance(BigDecimal.valueOf(ds.getDouble("low_balance")));
            instrument.setHighBalance(BigDecimal.valueOf(ds.getDouble("high_balance")));
            instrument.setExdividendFlag(ds.getString("exdividend_flag"));
            instrument.setStbtransFlag(ds.getString("stbtrans_flag"));
            instrument.setMarketLowVolume(ds.getInt("market_low_amount"));
            instrument.setMarketHighVolume(ds.getLong("market_high_amount"));
            instrument.setBondinvFlag(ds.getString("bondinv_flag"));
            list.add(instrument);
        }
        return list;
    }

    /***
     * @Description: 债券质押协议回购成交查询解析
     * @Param: []
     * @return: java.util.List<com.joyintech.tams.exchange.common.db.domain.Trade>
     * @Author: MAXX
     * @Date: 2020/4/9
     */
    public static List<Trade> qryProtocolBack_Trade_Back(IEvent rsp, String accountId) {
        IDatasets result = rsp.getEventDatas();
        IDataset ds = result.getDataset(0);
        List<Trade> list = new ArrayList<Trade>();
        ds.beforeFirst();
        while (ds.hasNext()) {
            ds.next();
            int real_status = ds.getInt("real_status");//处理标志
            logger.info("real_status:{} ,brokerEntrustId:{},real_type:{},entrust_no:{},business_id:{})",
                    real_status, ds.getString("entrust_no"), ds.getString("real_type"), ds.getString("entrust_no"), ds.getString("business_id"));
            Trade trade = createTrade(accountId, ds, real_status);
            list.add(trade);
        }
        return list;
    }

    private static Trade createTrade(String accountId, IDataset ds, int real_status) {
        Trade trade = new Trade();
        trade.setAccountId(accountId);
        trade.setPositionStr(ds.getString("position_str"));
        trade.setTradingDay(ds.getString("init_date"));
        trade.setBrokerEntrustId(ds.getString("entrust_no"));
        trade.setFinanceAccount(ds.getString("fund_account"));
        String exchangeType = ds.getString("exchange_type");
        if (ExchangeType.SH.value.equals(exchangeType)) {
            trade.setMarket(Market.SHSE.value);
        } else if (ExchangeType.SZ.value.equals(exchangeType)) {
            trade.setMarket(Market.SZSE.value);
        }
        trade.setStockCode(ds.getString("stock_code"));
        trade.setStockName(ds.getString("stock_name"));
        trade.setEntrustBs(ds.getString("entrust_bs"));
        trade.setEntrustProp(ds.getString("entrust_prop"));
        String businessAmount = ds.getString("business_amount");
        double business_amount = Double.parseDouble(businessAmount);
        trade.setTradedVolume((long) business_amount);
        trade.setPropSeatNo(ds.getString("prop_seat_no"));
        if (real_status != 0 && real_status != 4) {
            trade.setBrokerTradeId(ds.getString("entrust_no"));
            if (2 == ds.getInt("real_type")) {
                trade.setTradeType(TradeType.CANCEL_INVALID.value);
            } else if (0 == ds.getInt("real_type")) {
                trade.setTradeType(TradeType.INVALID.value);
            }
        } else {
            trade.setBrokerTradeId(ds.getString("business_id"));
            if (2 == ds.getInt("real_type")) {
                trade.setTradeType(TradeType.CANCEL.value);
            } else if (0 == ds.getInt("real_type")) {
                trade.setTradeType(TradeType.NORMAL.value);
            }
        }
        trade.setTradedPrice(BigDecimal.valueOf(ds.getDouble("business_price")));
        trade.setTradedBalance(BigDecimal.valueOf(ds.getDouble("business_balance")));
        String business_time = ds.getString("business_time", null);
        if (!Strings.isNullOrEmpty(business_time)) {
            trade.setTradeTime(business_time);
        }
        return trade;
    }
    /***
     * @Description: 返回值转json字符串
     * @Param: [arg0]
     * @return: java.lang.String
     * @Author: MAXX
     * @Date: 2020/3/18
     */
   /* public static String resultToString(IDatasets results) {
        StringBuilder result = new StringBuilder();
        StringBuilder title = new StringBuilder();
        StringBuilder data = new StringBuilder();
        int datasetCount_ = results.getDatasetCount();
        for (int i = 0; i < datasetCount_; i++) {
            IDataset ds = results.getDataset(i);
            int columnCount = ds.getColumnCount();
            for (int j = 1; j <= columnCount; j++) {
                String nameUpperCase = "";
                String[] keyArr = ds.getColumnName(j).split("_");
                for (int k = 0; k < keyArr.length; k++) {

                    if (k != 0) {
                        nameUpperCase += keyArr[k].substring(0, 1).toUpperCase() + keyArr[k].substring(1);
                    } else {
                        nameUpperCase += keyArr[k];
                    }
                }
                title.append(nameUpperCase);
                if (j != columnCount) {
                    title.append(",");
                }

            }
            ds.beforeFirst();
            while (ds.hasNext()) {
                ds.next();
                for (int j = 1; j <= columnCount; j++) {
                    data.append(ds.getString(j));
                    if (j != columnCount) {
                        data.append(",");
                    }
                }
                if (ds.hasNext()) {
                    data.append("###");
                }

            }
        }
        if ("".equals(data.toString())) {
            return null;
        }

        List<String> titleArr = Arrays.asList(title.toString().split(","));
        List<String> dataArr = Arrays.asList(data.toString().split("###"));

        if (!Objects.isNull(dataArr)) {

            if (dataArr.size() > 1) {
                result.append("[");
            }
            dataArr.forEach(dataInfo -> {
                result.append("{");
                List<String> infoArr = Arrays.asList(dataInfo.split(",", -1));

                for (int i = 0; i < titleArr.size(); i++) {
                    result.append("\"").append(titleArr.get(i)).append("\"");
                    result.append(":");
                    result.append("\"").append(infoArr.get(i)).append("\"");
                    if (i != titleArr.size() - 1) {
                        result.append(',');
                    }

                }
                result.append("}");
                result.append(',');
            });
            result.deleteCharAt(result.length() - 1);
            if (dataArr.size() > 1) {
                result.append("]");
            }
        }


        return result.toString();
    }*/
    /***
     * @Description: 返回值转json字符串
     * @Param: [arg0]
     * @return: java.lang.String
     * @Author: MAXX
     * @Date: 2020/3/18
     */
    /*public static String resultToString(IEvent arg0) {
        return resultToString(arg0.getEventDatas());
    }*/

    /***
     * @Description: 证券股东信息查询返回解析
     * @Param: [rsp]
     * @return: java.util.List<com.joyintech.tams.exchange.common.db.domain.ClientStkacct>
     * @Author: MAXX
     * @Date: 2020/6/1
     */
    public static List<ClientStkacct> reqClientStkacctReBack(IEvent rsp) {
        IDatasets result = rsp.getEventDatas();
        IDataset ds = result.getDataset(0);
        List<ClientStkacct> clientStkacctList = new ArrayList<ClientStkacct>();
        ds.beforeFirst();
        while (ds.hasNext()) {
            ds.next();
            ClientStkacct clientStkacct = new ClientStkacct();
            clientStkacct.setPositionStr(ds.getString("position_str"));
            clientStkacct.setExchangeType(ds.getString("exchange_type"));
            clientStkacct.setStockAccount(ds.getString("stock_account"));
            clientStkacct.setMainFlag(ds.getString("main_flag"));
            clientStkacct.setHolderStatus(ds.getString("holder_status"));
            clientStkacct.setRegister(ds.getString("register"));
            clientStkacct.setHolderRights(ds.getString("holder_rights"));
            clientStkacct.setSeatNo(ds.getString("seat_no"));
            clientStkacct.setHolderName(ds.getString("holder_name"));
            clientStkacct.setHolderKind(ds.getString("holder_kind"));
            clientStkacct.setFundAccount(ds.getString("fund_account"));
            clientStkacct.setAssetProp(ds.getString("asset_prop"));
            clientStkacct.setAcodeAccount(ds.getString("acode_account"));
            clientStkacct.setOpenDate(ds.getInt("open_date"));
            clientStkacct.setCsdcOptacctUseage(ds.getString("csdc_optacct_useage"));
            clientStkacctList.add(clientStkacct);
        }
        return clientStkacctList;
    }

    public static BigDecimal getPriceStep(String priceUnit) {
        if ("1".equals(priceUnit)) {
            return new BigDecimal("0.001");
        } else if ("10".equals(priceUnit)) {
            return new BigDecimal("0.01");
        } else {
            return BigDecimal.valueOf(Double.parseDouble(priceUnit) / 1000);
        }
    }

    /***
     * @Description: 综合业务委托返回解析
     * @Param: [rsp]
     * @return: java.util.List<com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust>
     * @Author: MAXX
     * @Date: 2020/6/10
     */
    public static List<IntegratedEntrust> queryIntegratedOrder(IEvent rsp, String accountId) {
        IDatasets result = rsp.getEventDatas();
        IDataset ds = result.getDataset(0);
        List<IntegratedEntrust> list = new ArrayList<IntegratedEntrust>();
        ds.beforeFirst();
        while (ds.hasNext()) {
            ds.next();
            IntegratedEntrust entrust = createIntegratedEntrust(accountId, ds);
            list.add(entrust);
        }
        return list;
    }

    private static IntegratedEntrust createIntegratedEntrust(String accountId, IDataset ds) {
        IntegratedEntrust entrust = new IntegratedEntrust();
        entrust.setAccountId(accountId);
        entrust.setPositionStr(ds.getString("position_str", null));
        entrust.setTradingDay(ds.getString("init_date"));
        entrust.setBrokerEntrustId(ds.getString("entrust_no"));
        entrust.setTradingDay(ds.getString("entrust_date"));
        entrust.setReportTime(ds.getString("entrust_time", null));
        entrust.setFinanceAccount(ds.getString("fund_account"));
        String exchangeType = ds.getString("exchange_type");
        if (ExchangeType.SH.value.equals(exchangeType)) {
            entrust.setMarket(Market.SHSE.value);
        } else if (ExchangeType.SZ.value.equals(exchangeType)) {
            entrust.setMarket(Market.SZSE.value);
        }
        entrust.setReportNo(ds.getString("report_no"));
        entrust.setStockCode(ds.getString("stock_code"));
        entrust.setStockName(ds.getString("stock_name"));
        entrust.setEntrustBs(ds.getString("entrust_bs"));
        entrust.setEntrustType(ds.getString("entrust_type"));
        entrust.setEntrustProp(ds.getString("entrust_prop"));
        if (FullPriceConst.FULL_PRICE.value.equals(entrust.getIsFullPrice()) || (Strings.isNullOrEmpty(entrust.getIsFullPrice()) && EntrustUtils.isFullPriceTerminal(entrust.getMarket(), entrust.getStockCode(), entrust.getAssetType(), entrust.getEntrustProp()))) {
            entrust.setFullPrice(BigDecimal.valueOf(ds.getDouble("entrust_price")));
            entrust.setIsFullPrice(FullPriceConst.FULL_PRICE.value);
        } else {
            entrust.setEntrustPrice(BigDecimal.valueOf(ds.getDouble("entrust_price")));
            entrust.setIsFullPrice(FullPriceConst.NET_PRICE.value);
        }
        String entrustAmount = ds.getString("entrust_amount", null);
        if (!Strings.isNullOrEmpty(entrustAmount)) {
            double entrust_amount = Double.parseDouble(entrustAmount);
            entrust.setEntrustVolume((int) entrust_amount);
        }
        String businessAmount = ds.getString("business_amount", null);
        if (!Strings.isNullOrEmpty(businessAmount)) {
            try {
                if (businessAmount.lastIndexOf(".") != -1) {
                    double business_amount = Double.parseDouble(businessAmount);
                    entrust.setTradedVolume((long) business_amount);
                } else {
                    logger.error("businessAmount:{}", businessAmount);
                }
            } catch (Exception e) {
                logger.error("businessAmount:{}", e.getMessage());
            }
        }
        entrust.setTradedPrice(BigDecimal.valueOf(ds.getDouble("business_price")));
        entrust.setReportTime(ds.getString("report_time", null));
        try {
            String entrustStatus = ds.getString("entrust_status");
            if (entrustStatus.lastIndexOf(".") == -1) {
                getEntrustStatusByUfx(entrustStatus, entrust);
            } else {
                logger.error("entrustStatus:{}", entrustStatus);
            }
        } catch (Exception e) {
            logger.error("entrustStatus:{}", e.getMessage());
        }
        String cancel_info = ds.getString("cancel_info");
        String remark = ds.getString("remark");
        if (!Strings.isNullOrEmpty(cancel_info)) {
            entrust.setErrorMsg(ErrorMsgUtils.subErrorMsg(cancel_info));
        } else if (!Strings.isNullOrEmpty(remark)) {
            entrust.setErrorMsg(ErrorMsgUtils.subErrorMsg(remark));
        }
        String withdrawAmount = ds.getString("withdraw_amount", null);
        if (!Strings.isNullOrEmpty(withdrawAmount)) {
            double withdraw_amount = Double.parseDouble(withdrawAmount);
            entrust.setCancelVolume((long) withdraw_amount);
        }

        //TODO:: 确认国金的协议回购和固收委托是否是这个字段
        entrust.setId(ds.getString("entrust_reference"));
        return entrust;
    }

    public static void getEntrustStatusByUfx(String entrustStatus, EntrustInterface entrust) {
        switch (entrustStatus) {
            case "5":
                entrust.setEntrustStatus(EntrustStatus.PART_TRADED_CANCELED.value);
                break;
            case "6":
                entrust.setEntrustStatus(EntrustStatus.CANCELED.value);
                break;
            case "7":
                entrust.setEntrustStatus(EntrustStatus.PART_SUCCESS.value);
                break;
            case "C":
                entrust.setEntrustStatus(EntrustStatus.TOBE_REPORTED.value);
                break;
            case "D":
                entrust.setEntrustStatus(EntrustStatus.INVALID.value);
                break;
            case "S":
                entrust.setEntrustStatus(EntrustStatus.REPORTED.value);
                break;
            case "T":
                entrust.setEntrustStatus(EntrustStatus.PART_SUCCESS.value);
                break;
            case "U":
                entrust.setEntrustStatus(EntrustStatus.REPORTED_TOBE_CANCEL.value);
                break;
            case "V":
                entrust.setEntrustStatus(EntrustStatus.REPORTED.value);
                break;
            default:
                entrust.setEntrustStatus(Integer.parseInt(entrustStatus));
        }
    }

    /*** 
     * @Description: 协议回购行情查询返回数据解析
     * @Param: [quoteQryBean, rsp] 
     * @return: java.util.List<com.joyintech.tams.exchange.common.bean.IntegratedRepurchaseQuoteBean>
     * @Author: MAXX
     * @Date: 2020/9/22 
     */
    public static List<IntegratedRepurchaseQuoteBean> queryRepoQuotesReBack(IEvent rsp) {
        IDatasets result = rsp.getEventDatas();
        IDataset ds = result.getDataset(0);
        List<IntegratedRepurchaseQuoteBean> list = new ArrayList<IntegratedRepurchaseQuoteBean>();
        ds.beforeFirst();
        while (ds.hasNext()) {
            ds.next();
            IntegratedRepurchaseQuoteBean integratedRepurchaseQuoteBean = new IntegratedRepurchaseQuoteBean();
            integratedRepurchaseQuoteBean.setCbpconferId(ds.getString("cbpconfer_id"));
            integratedRepurchaseQuoteBean.setEntrustNo(ds.getInt("entrust_no"));
            integratedRepurchaseQuoteBean.setEntrustProp(ds.getString("entrust_prop"));
            integratedRepurchaseQuoteBean.setEntrustBs(ds.getString("entrust_bs"));
            integratedRepurchaseQuoteBean.setExchangeType(ds.getString("exchange_type"));
            integratedRepurchaseQuoteBean.setStockType(ds.getString("stock_type"));
            integratedRepurchaseQuoteBean.setStockCode(ds.getString("stock_code") + "." + getMarket(ds.getString("exchange_type")));
            integratedRepurchaseQuoteBean.setStockName(ds.getString("stock_name"));
            integratedRepurchaseQuoteBean.setExpireYearRate(ds.getDouble("expire_year_rate") * 100);
            integratedRepurchaseQuoteBean.setRepoTerm(ds.getInt("repo_term"));
            integratedRepurchaseQuoteBean.setFundUsedDays(ds.getInt("fund_used_days"));
            integratedRepurchaseQuoteBean.setFirstExchdate(ds.getInt("first_exchdate"));
            integratedRepurchaseQuoteBean.setRepurchaseDate(ds.getInt("repurchase_date"));
            integratedRepurchaseQuoteBean.setSettleEndDate(ds.getInt("settle_end_date"));
            integratedRepurchaseQuoteBean.setComponentNum(ds.getInt("component_num"));
            integratedRepurchaseQuoteBean.setOldImpawnCode(ds.getString("old_impawn_code"));
            integratedRepurchaseQuoteBean.setImpawnAmount(ds.getInt("impawn_amount"));
            integratedRepurchaseQuoteBean.setFunderRatio(ds.getDouble("funder_ratio"));
            integratedRepurchaseQuoteBean.setImpawnBalance(ds.getDouble("impawn_balance"));
            integratedRepurchaseQuoteBean.setPrevImpawnBalance(ds.getDouble("prev_impawn_balance"));
            integratedRepurchaseQuoteBean.setProfit(ds.getDouble("profit"));
            integratedRepurchaseQuoteBean.setSettleBalance(ds.getDouble("settle_balance"));
            integratedRepurchaseQuoteBean.setAssetNetValue(ds.getDouble("asset_net_value"));
            integratedRepurchaseQuoteBean.setBprsrcStatus(ds.getString("bprsrc_status"));
            integratedRepurchaseQuoteBean.setOrigEntrustDate(ds.getInt("orig_entrust_date"));
            integratedRepurchaseQuoteBean.setOrigCbpconferId(ds.getString("orig_cbpconfer_id"));
            integratedRepurchaseQuoteBean.setAgencyNo(ds.getString("agency_no"));
            integratedRepurchaseQuoteBean.setTraderId(ds.getString("trader_id"));
            integratedRepurchaseQuoteBean.setFundAccount(ds.getString("fund_account"));
            integratedRepurchaseQuoteBean.setStockAccount(ds.getString("stock_account"));
            integratedRepurchaseQuoteBean.setSeatNo(ds.getString("seat_no"));
            integratedRepurchaseQuoteBean.setOppoAgency(ds.getString("oppo_agency"));
            integratedRepurchaseQuoteBean.setOppoTraderId(ds.getString("oppo_trader_id"));
            integratedRepurchaseQuoteBean.setAgencyName(ds.getString("agency_name"));
            integratedRepurchaseQuoteBean.setPositionStr(ds.getString("position_str"));
            integratedRepurchaseQuoteBean.setOppoAgencyName(ds.getString("oppo_agency_name"));
            integratedRepurchaseQuoteBean.setPropStockAccount(ds.getString("prop_stock_account"));
            integratedRepurchaseQuoteBean.setPropSeatNo(ds.getString("prop_seat_no"));
            integratedRepurchaseQuoteBean.setRemark(ds.getString("remark"));
            integratedRepurchaseQuoteBean.setPropAccountName(ds.getString("prop_account_name"));
            integratedRepurchaseQuoteBean.setRealDateBack(ds.getInt("real_date_back"));
            integratedRepurchaseQuoteBean.setPledgeeName(ds.getString("pledgee_name"));
            list.add(integratedRepurchaseQuoteBean);
        }
        return list;
    }

    public static String getMarket(String exchangeType) {
        switch (exchangeType) {
            case "1":
                return "SH";
            case "2":
                return "SZ";
        }
        return "";
    }

    /*** 
     * @Description: 大宗交易行情查询返回数据解析
     * @Param: [rsp]
     * @return: java.util.List<com.joyintech.tams.exchange.common.bean.IntegratedBulkQuoteBean>
     * @Author: MAXX
     * @Date: 2020/9/22 
     */
    public static List<IntegratedBulkQuoteBean> queryIntegratedBulkQuoteReBack(IEvent rsp, IntegratedBulkQuoteQryBean integratedBulkQuoteQryBean) {
        IDatasets result = rsp.getEventDatas();
        IDataset ds = result.getDataset(0);
        List<IntegratedBulkQuoteBean> list = new ArrayList<IntegratedBulkQuoteBean>();
        ds.beforeFirst();
        while (ds.hasNext()) {
            ds.next();
            IntegratedBulkQuoteBean integratedBulkQuoteBean = new IntegratedBulkQuoteBean();
            integratedBulkQuoteBean.setOrderNo(ds.getString("order_no"));
            integratedBulkQuoteBean.setSeatNo(ds.getString("seat_no"));
            integratedBulkQuoteBean.setEntrustBs(CbptransTypeUtils.getEntrustBsByCbptransType(ds.getString("cbptrans_type")));
            integratedBulkQuoteBean.setEntrustProp(CbptransTypeUtils.getEntrustPropByCbptransType(ds.getString("cbptrans_type")));
            integratedBulkQuoteBean.setStockCode(ds.getString("stock_code") + "." + StockCodeUtils.getMarket(integratedBulkQuoteQryBean.getStockCode()));
            integratedBulkQuoteBean.setStockName(ds.getString("stock_name"));
            integratedBulkQuoteBean.setEntrustPrice(ds.getDouble("entrust_price"));
            integratedBulkQuoteBean.setEntrustAmount(ds.getInt("entrust_amount"));
            integratedBulkQuoteBean.setBusinessAmount(ds.getInt("business_amount"));
            integratedBulkQuoteBean.setCompactTerm(ds.getInt("compact_term"));
            integratedBulkQuoteBean.setRelationName(ds.getString("relation_name"));
            integratedBulkQuoteBean.setRelationTel(ds.getString("relation_tel"));
            integratedBulkQuoteBean.setCbpconferId(ds.getString("cbpconfer_id"));
            integratedBulkQuoteBean.setReportTime(ds.getString("report_time"));
            integratedBulkQuoteBean.setPositionStr(ds.getString("position_str"));
            integratedBulkQuoteBean.setRemark(ds.getString("remark"));
            list.add(integratedBulkQuoteBean);
        }
        return list;
    }

    /*** 
     * @Description: 盘后大宗交易行情查询
     * @Param: [rsp]
     * @return: java.util.List<com.joyintech.tams.exchange.common.bean.IntegratedBulkCloseQuoteBean>
     * @Author: MAXX
     * @Date: 2020/9/22 
     */
    public static List<IntegratedBulkCloseQuoteBean> qryIntegratedBulkCloseQuoteReBack(IEvent rsp, IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean) {
        IDatasets result = rsp.getEventDatas();
        IDataset ds = result.getDataset(0);
        List<IntegratedBulkCloseQuoteBean> list = new ArrayList<IntegratedBulkCloseQuoteBean>();
        ds.beforeFirst();
        while (ds.hasNext()) {
            ds.next();
            IntegratedBulkCloseQuoteBean integratedBulkCloseQuoteBean = new IntegratedBulkCloseQuoteBean();
            integratedBulkCloseQuoteBean.setExchangeType(ds.getString("exchange_type"));
            integratedBulkCloseQuoteBean.setStockCode(ds.getString("stock_code") + "." + StockCodeUtils.getMarket(integratedBulkCloseQuoteQryBean.getStockCode()));
            integratedBulkCloseQuoteBean.setStockName(ds.getString("stock_name"));
            integratedBulkCloseQuoteBean.setClosingPrice(ds.getDouble("closing_price"));
            integratedBulkCloseQuoteBean.setBusinessBalance(ds.getDouble("business_balance"));
            integratedBulkCloseQuoteBean.setBusinessAmount(ds.getInt("business_amount"));
            integratedBulkCloseQuoteBean.setBuyAmount1(ds.getDouble("buy_amount1"));
            integratedBulkCloseQuoteBean.setSaleAmount1(ds.getDouble("sale_amount1"));
            integratedBulkCloseQuoteBean.setWeightavgPrice(ds.getDouble("weightavg_price"));
            list.add(integratedBulkCloseQuoteBean);
        }
        return list;
    }

    //上海固收行情查询返回数据解析
    public static List<IntegratedFixedIncomeQuoteBean> qryIntegratedFixedIncomeQuoteReBack(IEvent rsp, IntegratedFixedIncomeQuoteQryBean integratedFixedIncomeQuoteQryBean) {
        IDatasets result = rsp.getEventDatas();
        IDataset ds = result.getDataset(0);
        List<IntegratedFixedIncomeQuoteBean> list = new ArrayList<IntegratedFixedIncomeQuoteBean>();
        ds.beforeFirst();
        while (ds.hasNext()) {
            ds.next();
            IntegratedFixedIncomeQuoteBean integratedFixedIncomeQuoteBean = new IntegratedFixedIncomeQuoteBean();
            integratedFixedIncomeQuoteBean.setStockCode(ds.getString("stock_code") + "." + StockCodeUtils.getMarket(integratedFixedIncomeQuoteQryBean.getStockCode()));
            integratedFixedIncomeQuoteBean.setStockName(ds.getString("stock_name"));
            integratedFixedIncomeQuoteBean.setOrderNo(ds.getString("buy_confirm_id"));
            integratedFixedIncomeQuoteBean.setReportTime(ds.getInt("report_time"));
            integratedFixedIncomeQuoteBean.setAgencyName(ds.getString("agency_name"));
            integratedFixedIncomeQuoteBean.setEntrustPrice(ds.getDouble("buy_price"));
            integratedFixedIncomeQuoteBean.setEntrustVolume(ds.getInt("buy_quote_amount"));
            integratedFixedIncomeQuoteBean.setFullPrice(ds.getDouble("buy_quote_price"));
            integratedFixedIncomeQuoteBean.setExpireIncomeRate(ds.getDouble("buy_exchange_rate"));
            integratedFixedIncomeQuoteBean.setInterest(ds.getDouble("stock_interest"));
            integratedFixedIncomeQuoteBean.setEntrustBs(EntrustBs.BUY.value);
            list.add(integratedFixedIncomeQuoteBean);
            if (!Strings.isNullOrEmpty(ds.getString("sell_price"))) {
                IntegratedFixedIncomeQuoteBean integratedFixedIncomeQuoteBean2 = new IntegratedFixedIncomeQuoteBean();
                integratedFixedIncomeQuoteBean2.setStockCode(ds.getString("stock_code") + "." + StockCodeUtils.getMarket(integratedFixedIncomeQuoteQryBean.getStockCode()));
                integratedFixedIncomeQuoteBean2.setStockName(ds.getString("stock_name"));
                integratedFixedIncomeQuoteBean2.setOrderNo(ds.getString("sell_confirm_id"));
                integratedFixedIncomeQuoteBean2.setReportTime(ds.getInt("report_time_x"));
                integratedFixedIncomeQuoteBean2.setAgencyName(ds.getString("other_agency"));
                integratedFixedIncomeQuoteBean2.setEntrustPrice(ds.getDouble("sell_price"));
                integratedFixedIncomeQuoteBean2.setEntrustVolume(ds.getInt("sell_quote_amount"));
                integratedFixedIncomeQuoteBean2.setFullPrice(ds.getDouble("sell_quote_price"));
                integratedFixedIncomeQuoteBean2.setExpireIncomeRate(ds.getDouble("sell_exchange_rate"));
                integratedFixedIncomeQuoteBean2.setInterest(ds.getDouble("stock_interest"));
                integratedFixedIncomeQuoteBean.setEntrustBs(EntrustBs.SELL.value);
                list.add(integratedFixedIncomeQuoteBean2);
            }
        }
        return list;
    }

    //证券持仓快速查询
    public static List<PositionBean> qryAccountPositionReBack(IEvent rsp, PositionQueryBean positionQueryBean) {
        IDatasets result = rsp.getEventDatas();
        IDataset ds = result.getDataset(0);
        List<PositionBean> list = new ArrayList<PositionBean>();
        ds.beforeFirst();
        while (ds.hasNext()) {
            ds.next();
            PositionBean positionBean = new PositionBean();
            positionBean.setAccountId(positionQueryBean.getAccountId());
            positionBean.setFinanceAccount(ds.getString("fund_account"));
            String exchangeType = ds.getString("exchange_type");
            if (ExchangeType.SH.value.equals(exchangeType)) {
                positionBean.setMarket(Market.SHSE.value);
            } else if (ExchangeType.SZ.value.equals(exchangeType)) {
                positionBean.setMarket(Market.SZSE.value);
            }
            positionBean.setStockCode(ds.getString("stock_code") + "." + positionBean.getMarket());
            positionBean.setStockName(ds.getString("stock_name"));
            positionBean.setCostPrice(ds.getDouble("cost_price"));
            positionBean.setMarketValue(ds.getDouble("market_value"));
            positionBean.setHoldVolume(ds.getLong("hold_amount"));
            positionBean.setEnableVolume(ds.getLong("enable_amount"));
            positionBean.setPositionStr(ds.getString("position_str"));
            list.add(positionBean);
        }
        return list;
    }

    // 针对协议回购利率入参，传值为0--100，柜台接收为0--1
    public static double convertExpireYearRate(BigDecimal entrustPrice) {
        return TradeUtils.divide(entrustPrice, 100, 3).doubleValue();
    }
}
