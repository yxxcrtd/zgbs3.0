package com.joyintech.tams.exchange.service.trade.server.agent.kcbp;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.joyintech.tams.exchange.common.bean.AccountFinance;
import com.joyintech.tams.exchange.common.bean.HistoryTrade;
import com.joyintech.tams.exchange.common.bean.IntegratedBulkCloseQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedBulkQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedFixedIncomeQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedQuote;
import com.joyintech.tams.exchange.common.bean.IntegratedRepurchaseQuoteBean;
import com.joyintech.tams.exchange.common.bean.PledgeDiscount;
import com.joyintech.tams.exchange.common.bean.QuoteQryBean;
import com.joyintech.tams.exchange.common.bean.ResultBean;
import com.joyintech.tams.exchange.common.bean.StandardBonds;
import com.joyintech.tams.exchange.common.bean.terminal.RTGSStatusBean;
import com.joyintech.tams.exchange.common.constant.AssetType;
import com.joyintech.tams.exchange.common.constant.EntrustBs;
import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.constant.EntrustStatus;
import com.joyintech.tams.exchange.common.constant.Market;
import com.joyintech.tams.exchange.common.constant.QuoteType;
import com.joyintech.tams.exchange.common.constant.TradeType;
import com.joyintech.tams.exchange.common.db.domain.Entrust;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.db.domain.PositionBean;
import com.joyintech.tams.exchange.common.db.domain.Trade;
import com.joyintech.tams.exchange.common.utils.EntrustUtils;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.service.trade.server.agent.EntrustIdManager;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author lifangpeng
 * @date 2021/10/12
 */
public class KCBPMsgParseHelper {

    public static List<Entrust> parseEntrusts(ResultBean resultBean, String accountId, EntrustIdManager entrustIdMap) {
        List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
        List<Entrust> entrusts = Lists.newArrayListWithExpectedSize(resList.size());
        for (Map<String, String> resMap : resList) {
            Entrust entrust = new Entrust();
            entrust.setAccountId(accountId);
            entrust.setId(entrustIdMap.getIdByLocalEntrustId(accountId, resMap.get("ordergroup")));
            entrust.setReportNo(resMap.get("orderid"));
            entrust.setBrokerEntrustId(resMap.get("ordersno"));
            entrust.setFinanceAccount(resMap.get("fundid"));
            entrust.setBsFlag(resMap.get("bsflag"));
            entrust.setReportTime(resMap.get("reporttime"));
            entrust.setMarket(getMarket(resMap.get("market")));
            entrust.setLocalEntrustId(resMap.get("ordergroup"));
            entrust.setStockCode(resMap.get("stkcode"));
            entrust.setStockName(resMap.get("stkname"));
            if (!Strings.isNullOrEmpty(resMap.get("orderprice"))) {
                entrust.setEntrustPrice(BigDecimal.valueOf(Double.parseDouble(resMap.get("orderprice"))));
            }
            if (!Strings.isNullOrEmpty(resMap.get("orderqty"))) {
                entrust.setEntrustVolume(Long.parseLong(resMap.get("orderqty")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("matchqty"))) {
                entrust.setTradedVolume(Long.parseLong(resMap.get("matchqty")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("matchamt"))) {
                entrust.setTradedBalance(BigDecimal.valueOf(Double.parseDouble(resMap.get("matchamt"))));
            }
            if (!Strings.isNullOrEmpty(resMap.get("cancelqty"))) {
                entrust.setCancelVolume(Long.parseLong(resMap.get("cancelqty")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("orderstatus"))) {
                entrust.setEntrustStatus(convertToEntrustStatus(resMap.get("orderstatus")));
            }

            entrust.setErrorMsg(resMap.get("remark"));
            entrust.setEntrustProp(EntrustUtils.getEntrustProp(entrust.getBsFlag(), entrust.getMarket()));
            entrust.setEntrustBs(EntrustUtils.getEntrustBs(entrust.getBsFlag()));
            entrusts.add(entrust);
        }

        return entrusts;
    }

    public static List<IntegratedEntrust> parseIntegratedEntrusts(ResultBean resultBean, String accountId, EntrustIdManager entrustIdMap) {
        List<IntegratedEntrust> integratedEntrusts = new ArrayList<>();
        List<Entrust> entrustList = parseEntrusts(resultBean, accountId, entrustIdMap);
        entrustList.forEach(entrust -> {
            IntegratedEntrust integratedEntrust = new IntegratedEntrust();
            BeanUtils.copyProperties(entrust, integratedEntrust);
            integratedEntrusts.add(integratedEntrust);
        });
        return integratedEntrusts;
    }


    public static List<Trade> parseTrades(List<Map<String, String>> resList, String accountId,
                                          EntrustIdManager entrustIdManager) {
        List<Trade> trades = Lists.newArrayListWithExpectedSize(resList.size());
        for (Map<String, String> resMap : resList) {
            Trade trade = new Trade();
            trade.setAccountId(accountId);
            trade.setReportNo(resMap.get("orderid"));
            trade.setBrokerEntrustId(resMap.get("ordersno"));
            trade.setLocalEntrustId(resMap.get("ordergroup"));
            trade.setEntrustId(entrustIdManager.getIdByLocalEntrustId(accountId, resMap.get("ordergroup")));
            trade.setFinanceAccount(resMap.get("fundid"));
            trade.setBsFlag(resMap.get("bsflag"));
            trade.setMarket(getMarket(resMap.get("market")));
            trade.setStockCode(resMap.get("stkcode"));
            trade.setStockName(resMap.get("stkname"));
            trade.setTradeTime(resMap.get("matchtime"));
            trade.setBrokerTradeId(resMap.get("matchcode"));
            if (!Strings.isNullOrEmpty(resMap.get("matchprice"))) {
                trade.setTradedPrice(BigDecimal.valueOf(Double.parseDouble(resMap.get("matchprice"))));
            }
            if (!Strings.isNullOrEmpty(resMap.get("matchqty"))) {
                trade.setTradedVolume(Long.parseLong(resMap.get("matchqty")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("matchamt"))) {
                trade.setTradedBalance(BigDecimal.valueOf(Double.parseDouble(resMap.get("matchamt"))));
            }
            if (!Strings.isNullOrEmpty(resMap.get("matchtype"))) {
                trade.setTradeType(Integer.parseInt(resMap.get("matchtype")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("sysdate"))) {
                trade.setTradingDay(resMap.get("sysdate"));
            } else if (!Strings.isNullOrEmpty(resMap.get("trddate"))) {
                trade.setTradingDay(resMap.get("trddate"));
            }

            trade.setEntrustProp(EntrustUtils.getEntrustProp(trade.getBsFlag(), trade.getMarket()));
            trade.setEntrustBs(EntrustUtils.getEntrustBs(trade.getBsFlag()));
            if (Strings.isNullOrEmpty(trade.getBrokerTradeId()) && TradeType.probablyNoBrokerTradeId(trade.getTradeType())) {
                trade.setBrokerTradeId("cancel_" + trade.getBrokerEntrustId());
            }

            trades.add(trade);
        }

        return trades;
    }

    public static List<IntegratedQuote> parseIntegratedQuotes(ResultBean resultBean, QuoteQryBean quoteQryBean) {
        List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
        List<IntegratedQuote> quotes = Lists.newArrayListWithExpectedSize(resList.size());
        for (Map<String, String> resMap : resList) {
            IntegratedQuote integratedQuote = new IntegratedQuote();
            integratedQuote.setAccountId(quoteQryBean.getAccountId());
            integratedQuote.setMarket(quoteQryBean.getMarket());
            integratedQuote.setQuoteType(quoteQryBean.getQuoteType());
            integratedQuote.setOrderNo(resMap.get("HQJLH"));
            if (resMap.containsKey("HQZQDM")) {
                integratedQuote.setStockCode(resMap.get("HQZQDM"));
            } else if (resMap.containsKey("stkcode")) {
                integratedQuote.setStockCode(resMap.get("stkcode"));
            }
            if (!Strings.isNullOrEmpty(resMap.get("HQSBSL"))) {
                integratedQuote.setEntrustVolume(Integer.parseInt(resMap.get("HQSBSL")));
            } else if (!Strings.isNullOrEmpty(resMap.get("orderqty"))) {
                integratedQuote.setEntrustVolume(Integer.parseInt(resMap.get("orderqty")));
            }
            if (resMap.containsKey("HQZLLB")) {
                integratedQuote.setBsFlag(resMap.get("HQZLLB"));
            } else if (resMap.containsKey("bsflag")) {
                integratedQuote.setBsFlag(resMap.get("bsflag"));
            }
            if (!Strings.isNullOrEmpty(resMap.get("HQBDLX"))) {
                integratedQuote.setEntrustProp(convertEntrustProp(resMap.get("HQBDLX")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("HQMMBZ"))) {
                integratedQuote.setEntrustBs(convertEntrustBs(resMap.get("HQMMBZ")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("HQSBJG"))) {
                integratedQuote.setEntrustPrice(Double.parseDouble(resMap.get("HQSBJG")));
            } else if (!Strings.isNullOrEmpty(resMap.get("orderprice"))) {
                integratedQuote.setEntrustPrice(Double.parseDouble(resMap.get("orderprice")));
            } else if (!Strings.isNullOrEmpty(resMap.get("closeprice"))) {
                integratedQuote.setEntrustPrice(Double.parseDouble(resMap.get("closeprice")));
            } else if (!Strings.isNullOrEmpty(resMap.get("Price"))) {
                integratedQuote.setEntrustPrice(Double.parseDouble(resMap.get("Price")));
            }
            if (resMap.containsKey("HQYDH")) {
                integratedQuote.setContractId(resMap.get("HQYDH"));
            } else if (resMap.containsKey("orderid")) {
                integratedQuote.setContractId(resMap.get("orderid"));
            }
            if (resMap.containsKey("HQLXR")) {
                integratedQuote.setLiaisonName(resMap.get("HQLXR"));
            } else if (resMap.containsKey("PartyIDfqyaname")) {
                integratedQuote.setLiaisonName(resMap.get("PartyIDfqyaname"));
            }
            String liaisonName = "";
            if (resMap.containsKey("HQLXFS")) {
                liaisonName = resMap.get("HQLXFS");
            } else if (resMap.containsKey("CompanyOper")) {
                liaisonName = resMap.get("CompanyOper");
            } else if (resMap.containsKey("PartyIDfqya")) {
                liaisonName = resMap.get("PartyIDfqya");
            }
            String[] liaisonTel = convertLiaisonTel(liaisonName, quoteQryBean);
            if (Objects.nonNull(liaisonTel) && liaisonTel.length > 1) {
                integratedQuote.setLiaisonTel(liaisonTel[1]);
                integratedQuote.setLiaisonName(liaisonTel[0]);
            } else {
                integratedQuote.setLiaisonTel(liaisonName);
            }
            if (resMap.containsKey("HQSBSJ")) {
                integratedQuote.setReportTime(resMap.get("HQSBSJ"));
            } else if (resMap.containsKey("ordertime")) {
                integratedQuote.setReportTime(resMap.get("ordertime"));
            }
            if (resMap.containsKey("HQFQF")) {
                integratedQuote.setPropSeatNo(resMap.get("HQFQF"));
            } else if (resMap.containsKey("PartyIDfqsa")) {
                integratedQuote.setPropSeatNo(resMap.get("PartyIDfqsa"));
            }
            if (resMap.containsKey("market")) {
                integratedQuote.setMarket(getMarket(resMap.get("market")));
            }
            if (resMap.containsKey("stkname")) {
                integratedQuote.setStockName(resMap.get("stkname"));
            }
            if (resMap.containsKey("company")) {
                integratedQuote.setCompany(resMap.get("company"));
            }
            if (!Strings.isNullOrEmpty(resMap.get("fullprice"))) {
                integratedQuote.setFullPrice(Double.parseDouble(resMap.get("fullprice")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("lastrate"))) {
                integratedQuote.setExpireIncomeRate(Double.parseDouble(resMap.get("lastrate")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("bondintr"))) {
                integratedQuote.setBondAccruedRate(Double.parseDouble(resMap.get("bondintr")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("status"))) {
                integratedQuote.setStatus(Integer.parseInt(resMap.get("status")));
            }
            if (resMap.containsKey("posstr")) {
                integratedQuote.setPosstr(resMap.get("posstr"));
            }
            // 协议回购
            if (resMap.containsKey("MsgType")) {
                integratedQuote.setMsgType(resMap.get("MsgType"));
            }
            if (resMap.containsKey("ApplReqID")) {
                integratedQuote.setQueryReqId(resMap.get("ApplReqID"));
            }
            if (resMap.containsKey("EndSeqNo")) {
                integratedQuote.setEndQuoteNo(resMap.get("EndSeqNo"));
            }
            if (resMap.containsKey("QuoteRefID")) {
                integratedQuote.setBrokerEntrustId(resMap.get("QuoteRefID"));
            }
            if (resMap.containsKey("OrdType")) {
                integratedQuote.setReportType(resMap.get("OrdType"));
            }
            if (!Strings.isNullOrEmpty(resMap.get("RepurchaseTerm"))) {
                integratedQuote.setCompactTerm(Integer.parseInt(resMap.get("RepurchaseTerm")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("UAInterestAccrualDays"))) {
                integratedQuote.setRealOccupyDays(Integer.parseInt(resMap.get("UAInterestAccrualDays")));
            }
            if (resMap.containsKey("SettlDate")) {
                integratedQuote.setFirstSettleDate(resMap.get("SettlDate"));
            }
            if (resMap.containsKey("MaturityDate")) {
                integratedQuote.setBackDate(resMap.get("MaturityDate"));
            }
            if (resMap.containsKey("SettlDate2")) {
                integratedQuote.setExpireSettleDate(resMap.get("SettlDate2"));
            }
            if (resMap.containsKey("Side")) {
                integratedQuote.setEntrustBs(resMap.get("Side"));
            }
            if (resMap.containsKey("SecurityID")) {
                integratedQuote.setPledgeStampCode(resMap.get("SecurityID"));
            }

            if (resMap.containsKey("Symbol")) {
                integratedQuote.setPledgeStampName(resMap.get("Symbol"));
            } else if (resMap.containsKey("SecurityIDname")) {
                integratedQuote.setPledgeStampName(resMap.get("SecurityIDname"));
            }
            if (resMap.containsKey("UnderlyingSecurityID")) {
                integratedQuote.setOrigPledgeStampCode(resMap.get("UnderlyingSecurityID"));
            }
            if (!Strings.isNullOrEmpty(resMap.get("OrderQty"))) {
                integratedQuote.setPledgeStampVolume(Integer.parseInt(resMap.get("OrderQty")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("ContractMultiplier"))) {
                integratedQuote.setDiscount(Double.parseDouble(resMap.get("ContractMultiplier")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("TotalValueTraded"))) {
                integratedQuote.setTradeBalance(Double.parseDouble(resMap.get("TotalValueTraded")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("UnderlyingQty"))) {
                integratedQuote.setOrigTradeBalance(Double.parseDouble(resMap.get("UnderlyingQty")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("AccruedInterestAmt"))) {
                integratedQuote.setBackInterest(Double.parseDouble(resMap.get("AccruedInterestAmt")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("SettlCurrAmt"))) {
                integratedQuote.setExpireSettleBalance(Double.parseDouble(resMap.get("SettlCurrAmt")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("LastQty"))) {
                integratedQuote.setPledgeStampBalance(Double.parseDouble(resMap.get("LastQty")));
            }
            if (resMap.containsKey("OrderRestrictions")) {
                integratedQuote.setReductionType(resMap.get("OrderRestrictions"));
            }
            if (resMap.containsKey("OrigTradeDate")) {
                integratedQuote.setOrigTradeDate(resMap.get("OrigTradeDate"));
            }
            if (resMap.containsKey("ExecRefID")) {
                integratedQuote.setOrigSerialNo(resMap.get("ExecRefID"));
            }
            if (resMap.containsKey("OrderQty")) {
                integratedQuote.setPropSeatName(resMap.get("OrderQty"));
            }
            if (resMap.containsKey("CompanyName")) {
                integratedQuote.setPropSeatName(resMap.get("CompanyName"));
            } else if (resMap.containsKey("PartyIDfqname")) {
                integratedQuote.setPropSeatName(resMap.get("PartyIDfqname"));
            }
            if (resMap.containsKey("PartyIDdsfyb")) {
                integratedQuote.setOppositeLiaisonTel(resMap.get("PartyIDdsfyb"));
            }
            if (resMap.containsKey("Partyiddsfybname")) {
                integratedQuote.setOppositeLiaisonName(resMap.get("Partyiddsfybname"));
            }
            if (resMap.containsKey("PartyIddsfsb")) {
                integratedQuote.setOppositePropSeatNo(resMap.get("PartyIddsfsb"));
            }
            if (resMap.containsKey("PartyIDname")) {
                integratedQuote.setOppositePropSeatName(resMap.get("PartyIDname"));
            }
            if (resMap.containsKey("Zqrname")) {
                integratedQuote.setZQRName(resMap.get("Zqrname"));
            }
            if (resMap.containsKey("Zqrname")) {
                integratedQuote.setRemark(resMap.get("Remark"));
            }

            if (Market.SZSE.value.equals(quoteQryBean.getMarket())) {
                integratedQuote.setEntrustProp(EntrustUtils.getEntrustProp(integratedQuote.getBsFlag(), quoteQryBean.getMarket()));
                integratedQuote.setEntrustBs(EntrustUtils.getEntrustBs(integratedQuote.getBsFlag()));
            }
            if (!"C".equals(integratedQuote.getEntrustProp())) {
                quotes.add(integratedQuote);
            }
        }

        return quotes;
    }

    public static List<HistoryTrade> parseHistoryTrade(ResultBean resultBean, String accountId) {
        List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
        List<HistoryTrade> historyTradeList = Lists.newArrayListWithExpectedSize(resList.size());
        for (Map<String, String> resMap : resList) {
            HistoryTrade historyTrade = new HistoryTrade();
            historyTrade.setAccountId(accountId);
            historyTrade.setPostStr(resMap.get("poststr"));
            historyTrade.setClearDay(resMap.get("bizdate"));
            historyTrade.setTradingDay(resMap.get("cleardate"));
            historyTrade.setFinanceAccount(resMap.get("fundid"));
            historyTrade.setOrgId(resMap.get("orgid"));
            historyTrade.setStockHolderId(resMap.get("secuid"));
            historyTrade.setBsFlag(resMap.get("bsflag"));
            historyTrade.setOrderId(resMap.get("orderid"));
            historyTrade.setSerialNo(resMap.get("ordersno"));
            historyTrade.setMarket(resMap.get("market"));
            historyTrade.setStockCode(resMap.get("stkcode"));
            historyTrade.setStockName(resMap.get("stkname"));
            historyTrade.setTradeTime(resMap.get("matchtime"));
            historyTrade.setBrokerTradeId(resMap.get("matchcode"));
            if (!Strings.isNullOrEmpty(resMap.get("matchprice"))) {
                historyTrade.setTradePrice(Double.parseDouble(resMap.get("matchprice")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("matchqty"))) {
                historyTrade.setTradeVolume(Long.parseLong(resMap.get("matchqty")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("matchamt"))) {
                historyTrade.setTradedBalance(Double.parseDouble(resMap.get("matchamt")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("orderqty"))) {
                historyTrade.setEntrustVolume(Long.parseLong(resMap.get("orderqty")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("orderprice"))) {
                historyTrade.setEntrustPrice(Double.parseDouble(resMap.get("orderprice")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("fee_jsxf"))) {
                historyTrade.setJinShouXuFei(Double.parseDouble(resMap.get("fee_jsxf")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("fee_sxf"))) {
                historyTrade.setProcedureCost(Double.parseDouble(resMap.get("fee_sxf")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("fee_yhs"))) {
                historyTrade.setPrintingCost(Double.parseDouble(resMap.get("fee_yhs")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("fee_ghf"))) {
                historyTrade.setTransferCost(Double.parseDouble(resMap.get("fee_ghf")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("fee_qsf"))) {
                historyTrade.setClearCost(Double.parseDouble(resMap.get("fee_qsf")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("fee_jygf"))) {
                historyTrade.setTradeRuleCost(Double.parseDouble(resMap.get("fee_jygf")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("fee_jsf"))) {
                historyTrade.setHandleCost(Double.parseDouble(resMap.get("fee_jsf")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("fee_zgf"))) {
                historyTrade.setSecCost(Double.parseDouble(resMap.get("fee_zgf")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("fee_qtf"))) {
                historyTrade.setOtherCost(Double.parseDouble(resMap.get("fee_qtf")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("feefront"))) {
                historyTrade.setFrontCost(Double.parseDouble(resMap.get("feefront")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("stkbal"))) {
                historyTrade.setStockResidue(Long.parseLong(resMap.get("stkbal")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("fundeffect"))) {
                historyTrade.setFinanceAmount(Double.parseDouble(resMap.get("fundeffect")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("fundbal"))) {
                historyTrade.setUseMoney(Double.parseDouble(resMap.get("fundbal")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("bondintr"))) {
                historyTrade.setBondInterest(Double.parseDouble(resMap.get("bondintr")));
            }
            historyTrade.setPortfolioId(resMap.get("prodcode"));
            historyTrade.setStockType(resMap.get("stktype"));

            historyTradeList.add(historyTrade);
        }

        return historyTradeList;
    }

    public static List<AccountFinance> parseCustomerFinance(ResultBean resultBean, String accountId) {
        List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
        List<AccountFinance> list = Lists.newArrayListWithExpectedSize(resList.size());
        for (Map<String, String> resMap : resList) {
            AccountFinance accountFinance = new AccountFinance();
            accountFinance.setAccountId(accountId);
            accountFinance.setCustomerId(resMap.get("custid"));
            accountFinance.setFinanceAccount(resMap.get("fundid"));
            accountFinance.setFortuneId(resMap.get("fortuneid"));
            accountFinance.setOrgId(resMap.get("orgid"));
            accountFinance.setMoneyType(resMap.get("moneytype"));
            if (!Strings.isNullOrEmpty(resMap.get("fundbal"))) {
                accountFinance.setFinanceBalance(Double.parseDouble(resMap.get("fundbal")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("fundavl"))) {
                accountFinance.setEnableBalance(Double.parseDouble(resMap.get("fundavl")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("marketvalue"))) {
                accountFinance.setTotalAssets(Double.parseDouble(resMap.get("marketvalue")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("fund"))) {
                accountFinance.setFinanceAssets(Double.parseDouble(resMap.get("fund")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("stkvalue"))) {
                accountFinance.setStkValue(Double.parseDouble(resMap.get("stkvalue")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("fundloan"))) {
                accountFinance.setFundLoan(Double.parseDouble(resMap.get("fundloan")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("fundfrz"))) {
                accountFinance.setTotalFrozenFinance(Double.parseDouble(resMap.get("fundfrz")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("fundlastbal"))) {
                accountFinance.setPrevDayFinanceBalance(Double.parseDouble(resMap.get("fundlastbal")));
            }

            list.add(accountFinance);
        }

        return list;
    }

    public static List<PositionBean> parseAccountPosition(ResultBean resultBean, String accountId) {
        List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
        List<PositionBean> list = Lists.newArrayListWithExpectedSize(resList.size());
        for (Map<String, String> resMap : resList) {
            PositionBean positionBean = new PositionBean();
            positionBean.setAccountId(accountId);
            // TODO 待处理
            positionBean.setFinanceAccount("");
            positionBean.setMarket(getMarket(resMap.get("market")));
            positionBean.setStockCode(resMap.get("stkcode"));
            positionBean.setStockName(resMap.get("stkname"));
            if (!Strings.isNullOrEmpty(resMap.get("costprice"))) {
                positionBean.setCostPrice(Double.parseDouble(resMap.get("costprice")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("mktval"))) {
                positionBean.setMarketValue(Double.parseDouble(resMap.get("mktval")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("stkbal"))) {
                positionBean.setHoldVolume(Long.parseLong(resMap.get("stkbal")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("stkavl"))) {
                positionBean.setEnableVolume(Long.parseLong(resMap.get("stkavl")));
            }

            list.add(positionBean);
        }

        return list;
    }

    public static List<Instrument> parseInstrumentInfo(ResultBean resultBean) {
        List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
        List<Instrument> list = Lists.newArrayListWithExpectedSize(resList.size());
        for (Map<String, String> resMap : resList) {
            Instrument instrument = new Instrument();
            instrument.setPostStr(resMap.get("poststr"));
            if (!Strings.isNullOrEmpty(resMap.get("market"))) {
                instrument.setMarket(getMarket(resMap.get("market")));
            }
            instrument.setStockName(resMap.get("stkname"));
            instrument.setStockCode(resMap.get("stkcode"));
            instrument.setInnerCode(getInnerCode(instrument.getMarket(), resMap.get("stkcode")));
            if (!Strings.isNullOrEmpty(resMap.get("priceunit"))) {
                instrument.setPriceStep(getPriceStep(resMap.get("priceunit")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("stktype"))) {
                instrument.setAssetType(getAssetType(resMap.get("stktype")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("maxrisevalue"))) {
                instrument.setUpPrice(new BigDecimal(resMap.get("maxrisevalue")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("maxdownvalue"))) {
                instrument.setDownPrice(new BigDecimal(resMap.get("maxdownvalue")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("maxqty"))) {
                instrument.setHighVolume(Long.parseLong(resMap.get("maxqty")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("minqty"))) {
                instrument.setLowVolume(Integer.parseInt(resMap.get("minqty")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("buyunit"))) {
                instrument.setBuyUnit(Integer.parseInt(resMap.get("buyunit")));
            }

            instrument.setStkcodeStatus(resMap.get("stkstatus"));
            if (!Strings.isNullOrEmpty(resMap.get("marketmaxqty"))) {
                instrument.setMarketHighVolume(Long.parseLong(resMap.get("marketmaxqty")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("marketminqty"))) {
                instrument.setMarketLowVolume(Integer.parseInt(resMap.get("marketminqty")));
            }

            list.add(instrument);
        }

        return list;
    }

    public static List<PledgeDiscount> parsePledgeDiscount(ResultBean resultBean, String accountId) {
        List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
        List<PledgeDiscount> list = Lists.newArrayListWithExpectedSize(resList.size());
        for (Map<String, String> resMap : resList) {
            PledgeDiscount pledgeDiscount = new PledgeDiscount();
            pledgeDiscount.setAccountId(accountId);
            String mkt = resMap.get("market");
            pledgeDiscount.setMarket(getMarket(resMap.get("market")));
            pledgeDiscount.setStockAccount(resMap.get("secuid"));
            pledgeDiscount.setStockCode(resMap.get("stkcode"));
            pledgeDiscount.setFinanceAccount(resMap.get("fundid"));
            if (!Strings.isNullOrEmpty(resMap.get("bondlastbal"))) {
                pledgeDiscount.setBondlastbal(Integer.parseInt(resMap.get("bondlastbal")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("bondbal"))) {
                pledgeDiscount.setBondbal(Integer.parseInt(resMap.get("bondbal")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("bondavl"))) {
                pledgeDiscount.setBondEnable(Integer.parseInt(resMap.get("bondavl")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("secuid"))) {
                pledgeDiscount.setDiscount(Double.parseDouble(resMap.get("secuid")));
            }

            list.add(pledgeDiscount);
        }

        return list;
    }

    public static List<StandardBonds> parseStandardBonds(ResultBean resultBean, String accountId) {
        List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
        List<StandardBonds> list = Lists.newArrayListWithExpectedSize(resList.size());
        for (Map<String, String> resMap : resList) {
            StandardBonds standardBonds = new StandardBonds();
            standardBonds.setAccountId(accountId);
            standardBonds.setMarket(getMarket(resMap.get("market")));
            standardBonds.setStockAccount(resMap.get("secuid"));
            standardBonds.setStockCode(resMap.get("stkcode"));
            standardBonds.setStockType(resMap.get("stktype"));
            if (!Strings.isNullOrEmpty(resMap.get("exchrate"))) {
                standardBonds.setDiscount(Double.parseDouble(resMap.get("exchrate")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("bondunfrz"))) {
                standardBonds.setBondUnFrozen(Integer.parseInt(resMap.get("bondunfrz")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("bondfrz"))) {
                standardBonds.setBondFrozen(Integer.parseInt(resMap.get("bondfrz")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("remainqty"))) {
                standardBonds.setBondEnable(Integer.parseInt(resMap.get("remainqty")));
            }

            list.add(standardBonds);
        }

        return list;
    }

    public static List<IntegratedBulkQuoteBean> parseIntegratedBulkQuote(ResultBean resultBean, String market) {
        List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
        List<IntegratedBulkQuoteBean> list = Lists.newArrayListWithExpectedSize(resList.size());
        for (Map<String, String> resMap : resList) {
            IntegratedBulkQuoteBean integratedBulkQuoteBean = new IntegratedBulkQuoteBean();
            //记录号
            integratedBulkQuoteBean.setOrderNo(resMap.get("HQJLH"));
            //证券代码
            if (Strings.isNullOrEmpty(market)) {
                integratedBulkQuoteBean.setStockCode(StockCodeUtils.formatStockCode(resMap.get("HQZQDM"), market));
            } else {
                integratedBulkQuoteBean.setStockCode(resMap.get("HQZQDM"));
            }
            //会员编号
            //integratedBulkQuoteBean.setSeatNo(resMap.get("HQHYBH"));
            //深圳BsFlag
            if (!Strings.isNullOrEmpty(resMap.get("HQYWLB"))) {
                integratedBulkQuoteBean.setEntrustProp(EntrustUtils.getEntrustProp(resMap.get("HQYWLB"), "SZ"));
                integratedBulkQuoteBean.setEntrustBs(EntrustUtils.getEntrustBs(resMap.get("HQYWLB")));
            }
            //上海报单类型 "N"为意向买
            if ("N".equals(resMap.get("HQBDLX"))) {
                integratedBulkQuoteBean.setEntrustProp(EntrustProp.INTENTION.value);
            }
            //买卖标志，上海买卖标志
            if ("B".equals(resMap.get("HQMMBZ"))) {
                integratedBulkQuoteBean.setEntrustBs(EntrustBs.BUY.value);
            } else if ("S".equals(resMap.get("HQMMBZ"))) {
                integratedBulkQuoteBean.setEntrustBs(EntrustBs.SELL.value);
            }
            //申报价格
            if (!Strings.isNullOrEmpty(resMap.get("HQSBJG"))) {
                integratedBulkQuoteBean.setEntrustPrice(Double.parseDouble(resMap.get("HQSBJG")));
            }
            //申报数量
            if (!Strings.isNullOrEmpty(resMap.get("HQSBSL"))) {
                integratedBulkQuoteBean.setEntrustAmount(Integer.parseInt(resMap.get("HQSBSL")));
            }
            //撤单编号 HQCDBH
            //发起方席位
            integratedBulkQuoteBean.setSeatNo(resMap.get("HQFQF"));
            //发起方联系方式
            if (resMap.get("HQLXFS").contains("-")) {
                String[] liaisonTel = resMap.get("HQLXFS").split("-");
                if (Objects.nonNull(liaisonTel) && liaisonTel.length > 1) {
                    integratedBulkQuoteBean.setRelationName(liaisonTel[1]);
                    integratedBulkQuoteBean.setRelationTel(liaisonTel[0]);
                }
                integratedBulkQuoteBean.setRelationTel(resMap.get("HQLXFS"));
            } else {
                integratedBulkQuoteBean.setRelationTel(resMap.get("HQLXFS"));
            }
            //联系人
            integratedBulkQuoteBean.setRelationName(resMap.get("HQLXR"));
            //约定号
            integratedBulkQuoteBean.setCbpconferId(resMap.get("HQYDH"));
            //备用文本
            integratedBulkQuoteBean.setRemark(resMap.get("HQBYWB"));
            //申报时间
            integratedBulkQuoteBean.setReportTime(resMap.get("HQSBSJ"));
            //指令类别
            String bsFlag = resMap.get("HQZLLB");
            //1m：盘后定价大宗买，1n：盘后定价大宗卖
            if (!Strings.isNullOrEmpty(bsFlag) && ("1m".equals(bsFlag) || "1n".equals(bsFlag))) {
                continue;
            }
            list.add(integratedBulkQuoteBean);
        }

        return list;
    }

    public static List<IntegratedBulkCloseQuoteBean> parseIntegratedBulkCloseQuote(ResultBean resultBean, String market) {
        List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
        List<IntegratedBulkCloseQuoteBean> list = Lists.newArrayListWithExpectedSize(resList.size());
        if (Market.SHSE.value.equals(market)) {
            for (Map<String, String> resMap : resList) {
                IntegratedBulkCloseQuoteBean integratedBulkCloseQuoteBean = new IntegratedBulkCloseQuoteBean();
                integratedBulkCloseQuoteBean.setStockCode(StockCodeUtils.formatStockCode(resMap.get("stkcode"), "SH"));
                integratedBulkCloseQuoteBean.setStockName(resMap.get("stkname"));
                if (!Strings.isNullOrEmpty(resMap.get("tradevolume"))) {
                    integratedBulkCloseQuoteBean.setBusinessAmount(Integer.parseInt(resMap.get("tradevolume")));
                }
                if (!Strings.isNullOrEmpty(resMap.get("totalvaluetraded"))) {
                    integratedBulkCloseQuoteBean.setBusinessBalance(Double.parseDouble(resMap.get("totalvaluetraded")));
                }
                if (!Strings.isNullOrEmpty(resMap.get("closeprice"))) {
                    integratedBulkCloseQuoteBean.setClosingPrice(Double.parseDouble(resMap.get("closeprice")));
                }
                if (!Strings.isNullOrEmpty(resMap.get("buyvolume"))) {
                    integratedBulkCloseQuoteBean.setBuyAmount1(Integer.parseInt(resMap.get("buyvolume")));
                }
                if (!Strings.isNullOrEmpty(resMap.get("sellvolume"))) {
                    integratedBulkCloseQuoteBean.setSaleAmount1(Integer.parseInt(resMap.get("sellvolume")));
                }

                list.add(integratedBulkCloseQuoteBean);
            }
        } else if (Market.SZSE.value.equals(market)) {
            for (Map<String, String> resMap : resList) {
                IntegratedBulkCloseQuoteBean integratedBulkCloseQuoteBean = new IntegratedBulkCloseQuoteBean();
                //证券代码
                integratedBulkCloseQuoteBean.setStockCode(StockCodeUtils.formatStockCode(resMap.get("HQZQDM"), "SZ"));
                //申报价格
                if (!Strings.isNullOrEmpty(resMap.get("HQSBJG"))) {
                    integratedBulkCloseQuoteBean.setClosingPrice(Double.parseDouble(resMap.get("HQSBJG")));
                }
                //申报数量
                if (!Strings.isNullOrEmpty(resMap.get("HQSBSL"))) {
                    integratedBulkCloseQuoteBean.setBuyAmount1(Integer.parseInt(resMap.get("HQSBSL")));
                }
                //申报数量2
                if (!Strings.isNullOrEmpty(resMap.get("HQSBSL2"))) {
                    integratedBulkCloseQuoteBean.setBuyAmount2(Integer.parseInt(resMap.get("HQSBSL2")));
                }
                //指令类别
                String bsFlag = resMap.get("HQZLLB");
                //1m：盘后定价大宗买，1n：盘后定价大宗卖
                if (!Strings.isNullOrEmpty(bsFlag) && ("1m".equals(bsFlag) || "1n".equals(bsFlag))) {
                    list.add(integratedBulkCloseQuoteBean);
                }
            }
        }

        return list;
    }

    public static List<IntegratedRepurchaseQuoteBean> parseIntegratedRepurchaseQuote(ResultBean resultBean) {
        List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
        List<IntegratedRepurchaseQuoteBean> list = Lists.newArrayListWithExpectedSize(resList.size());
        for (Map<String, String> resMap : resList) {
            IntegratedRepurchaseQuoteBean integratedRepurchaseQuoteBean = new IntegratedRepurchaseQuoteBean();
            //"posstr":
            // "EndSeqNo":
            // "QuoteRefID":
            // "OrdType":
            //目前仅支持上海协议回购
            if (!Strings.isNullOrEmpty(resMap.get("SecurityID"))) {
                integratedRepurchaseQuoteBean.setStockCode(StockCodeUtils.formatStockCode(resMap.get("SecurityID"), "SH"));
            }
            integratedRepurchaseQuoteBean.setStockName(resMap.get("SecurityIDname"));
            // "Symbol":
            if (!Strings.isNullOrEmpty(resMap.get("UAInterestAccrualDays"))) {
                integratedRepurchaseQuoteBean.setFundUsedDays(Integer.parseInt(resMap.get("UAInterestAccrualDays")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("SettlDate"))) {
                integratedRepurchaseQuoteBean.setFirstExchdate(Integer.parseInt(resMap.get("SettlDate")));
            }
            //1:正回购，2:逆回购 对应EntrustBs字段，无需转换
            integratedRepurchaseQuoteBean.setEntrustBs(resMap.get("Side"));
            if (!Strings.isNullOrEmpty(resMap.get("OrderQty"))) {
                integratedRepurchaseQuoteBean.setImpawnAmount(Integer.parseInt(resMap.get("OrderQty")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("ContractMultiplier"))) {
                integratedRepurchaseQuoteBean.setFunderRatio(Double.parseDouble(resMap.get("ContractMultiplier")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("RepurchaseTerm"))) {
                integratedRepurchaseQuoteBean.setRepoTerm(Integer.parseInt(resMap.get("RepurchaseTerm")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("MaturityDate"))) {
                integratedRepurchaseQuoteBean.setRepurchaseDate(Integer.parseInt(resMap.get("MaturityDate")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("SettlDate2"))) {
                integratedRepurchaseQuoteBean.setSettleEndDate(Integer.parseInt(resMap.get("SettlDate2")));
            }
            integratedRepurchaseQuoteBean.setOldImpawnCode(resMap.get("UnderlyingSecurityID"));
            integratedRepurchaseQuoteBean.setAgencyName(resMap.get("CompanyName"));
            // "CompanyOper":
            if (!Strings.isNullOrEmpty(resMap.get("SecurityID"))) {
                integratedRepurchaseQuoteBean.setImpawnBalance(Double.parseDouble(resMap.get("TotalValueTraded")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("UnderlyingQty"))) {
                integratedRepurchaseQuoteBean.setPrevImpawnBalance(Double.parseDouble(resMap.get("UnderlyingQty")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("AccruedInterestAmt"))) {
                integratedRepurchaseQuoteBean.setProfit(Double.parseDouble(resMap.get("AccruedInterestAmt")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("SettlCurrAmt"))) {
                integratedRepurchaseQuoteBean.setSettleBalance(Double.parseDouble(resMap.get("SettlCurrAmt")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("OrigTradeDate"))) {
                integratedRepurchaseQuoteBean.setOrigEntrustDate(Integer.parseInt(resMap.get("OrigTradeDate")));
            }
            integratedRepurchaseQuoteBean.setTraderId(resMap.get("PartyIDfqya"));
            integratedRepurchaseQuoteBean.setOppoAgency(resMap.get("PartyIddsfsb"));
            integratedRepurchaseQuoteBean.setOppoTraderId(resMap.get("PartyIDdsfyb"));
            integratedRepurchaseQuoteBean.setOppoAgencyName(resMap.get("PartyIDname"));
            integratedRepurchaseQuoteBean.setPledgeeName(resMap.get("Zqrname"));
            integratedRepurchaseQuoteBean.setRemark(resMap.get("Remark"));
            integratedRepurchaseQuoteBean.setAgencyNo(resMap.get("PartyIDfqsa"));
            if (!Strings.isNullOrEmpty(resMap.get("LastQty"))) {
                integratedRepurchaseQuoteBean.setAssetNetValue(Double.parseDouble(resMap.get("LastQty")));
            }
            integratedRepurchaseQuoteBean.setAgencyName(resMap.get("PartyIDfqname"));
            if (!Strings.isNullOrEmpty(resMap.get("Price"))) {
                integratedRepurchaseQuoteBean.setExpireYearRate(Double.parseDouble(resMap.get("Price")));
            }
            if ("U026".equals(resMap.get("MsgType"))) {
                integratedRepurchaseQuoteBean.setExchangeType("1");
            } else {
                integratedRepurchaseQuoteBean.setExchangeType("0");
            }

            list.add(integratedRepurchaseQuoteBean);
        }

        return list;
    }

    public static List<IntegratedFixedIncomeQuoteBean> parseIntegratedFixedIncomeQuote(ResultBean resultBean) {
        List<Map<String, String>> resList = (List<Map<String, String>>) resultBean.getData();
        List<IntegratedFixedIncomeQuoteBean> list = Lists.newArrayListWithExpectedSize(resList.size());
        for (Map<String, String> resMap : resList) {
            IntegratedFixedIncomeQuoteBean integratedFixedIncomeQuoteBean = new IntegratedFixedIncomeQuoteBean();
            String market = resMap.get("market");
            String stkcode = resMap.get("stkcode");
            integratedFixedIncomeQuoteBean.setStockCode(stkcode);
            if (!Strings.isNullOrEmpty(market)) {
                integratedFixedIncomeQuoteBean.setStockCode(StockCodeUtils.formatStockCode(stkcode, market));
            }
            integratedFixedIncomeQuoteBean.setStockName(resMap.get("stkname"));
            integratedFixedIncomeQuoteBean.setEntrustBs(EntrustUtils.getEntrustBs(resMap.get("bsflag")));
            integratedFixedIncomeQuoteBean.setOrderNo(resMap.get("orderid"));
            integratedFixedIncomeQuoteBean.setReportTime(Integer.parseInt(resMap.get("ordertime")));
            integratedFixedIncomeQuoteBean.setAgencyName(resMap.get("company"));
            if (!Strings.isNullOrEmpty(resMap.get("orderprice"))) {
                integratedFixedIncomeQuoteBean.setEntrustPrice(Double.parseDouble(resMap.get("orderprice")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("fullprice"))) {
                integratedFixedIncomeQuoteBean.setFullPrice(Double.parseDouble(resMap.get("fullprice")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("orderqty"))) {
                integratedFixedIncomeQuoteBean.setEntrustVolume(Integer.parseInt(resMap.get("orderqty")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("bondintr"))) {
                integratedFixedIncomeQuoteBean.setInterest(Double.parseDouble(resMap.get("bondintr")));
            }
            if (!Strings.isNullOrEmpty(resMap.get("lastrate"))) {
                integratedFixedIncomeQuoteBean.setExpireIncomeRate(Double.parseDouble(resMap.get("lastrate")));
            }

            list.add(integratedFixedIncomeQuoteBean);
        }

        return list;
    }

    public static List<RTGSStatusBean> parseRTGS(String accountId, List<Map<String, String>> resList) {
        List<RTGSStatusBean> rtgsStatusBeanList = Lists.newArrayListWithExpectedSize(resList.size());
        for (Map<String, String> resMap : resList) {
            RTGSStatusBean rtgsStatusBean = new RTGSStatusBean();
            rtgsStatusBean.setAccountId(accountId);
            rtgsStatusBean.setEntrustDate(resMap.get("orderdate"));
            rtgsStatusBean.setReportNo(resMap.get("orderid"));
            rtgsStatusBean.setErrorCode(resMap.get("code"));
            rtgsStatusBean.setErrorMsg(resMap.get("description"));
            rtgsStatusBean.setStatus(resMap.get("status"));

            rtgsStatusBeanList.add(rtgsStatusBean);
        }
        return rtgsStatusBeanList;
    }

    public static int convertToEntrustStatus(String orderStatus) {
        switch (orderStatus) {
            case "0":
                return EntrustStatus.UNREPORTED.value;
            case "1":
            case "B":
            case "A":
                return EntrustStatus.TOBE_REPORTED.value;
            case "C":
            case "D":
            case "2":
                return EntrustStatus.REPORTED.value;
            case "3":
                return EntrustStatus.REPORTED_TOBE_CANCEL.value;
            case "4":
                return EntrustStatus.PART_TRADED_TOBE_CANCELED.value;
            case "7":
                return EntrustStatus.PART_SUCCESS.value;
            case "5":
                return EntrustStatus.PART_TRADED_CANCELED.value;
            case "6":
                return EntrustStatus.CANCELED.value;
            case "8":
                return EntrustStatus.SUCCEEDED.value;
            case "9":
                return EntrustStatus.INVALID.value;
            default:
                // 不会调用到这里
                return EntrustStatus.INVALID.value;
        }
    }

    public static String convertEntrustProp(String entrustProp) {
        if ("N".equals(entrustProp)) {
            return EntrustProp.INTENTION.value;
        }
        return entrustProp;
    }

    public static String convertEntrustBs(String entrustBs) {
        switch (entrustBs) {
            case "B":
                return EntrustBs.BUY.value;
            case "S":
                return EntrustBs.SELL.value;
        }
        return "";
    }

    public static String[] convertLiaisonTel(String liaisonTel, QuoteQryBean quoteQryBean) {
        if ((QuoteType.BULK_QUOTE.value == quoteQryBean.getQuoteType()
                || QuoteType.AFTER_BULK_QUOTE.value == quoteQryBean.getQuoteType())
                && Market.SHSE.value.equals(quoteQryBean.getMarket())) {
            return liaisonTel.split("-");
        }
        return null;
    }

    public static String getMarket(String market) {
        if ("1".equals(market)) {
            return Market.SHSE.value;
        } else if ("0".equals(market)) {
            return Market.SZSE.value;
        }

        return market;
    }

    public static String getInnerCode(String market, String stockCode) {
        if (Market.SHSE.value.equals(market)) {
            return stockCode + "." + Market.SHSE.value;
        }

        return stockCode + "." + Market.SZSE.value;
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

    public static String getAssetType(String stkType) {
        switch (stkType) {
            case "0":
                return AssetType.STOCK_HS.value;
            case "1":
                return AssetType.JI_ZHANG_GUO_ZHAI.value;
            case "2":
                return AssetType.QI_YE_ZHAI_QUAN.value;
            case "3":
                return AssetType.GUO_ZHAI_HUI_GOU.value;
            case "4":
                return AssetType.FUND_HS.value;
            case "5":
                return AssetType.TOU_ZI_JI_JIN.value;
            case "6":
                return AssetType.QI_ZHAI_HUI_GOU.value;
            case "8":
                return AssetType.QI_YE_ZHUAN_ZHAI.value;
            case "9":
                return AssetType.KE_JIAO_HUAN_ZHAI_HUAN_GU.value;
            //            case "A" : 实物国债
            //                return AssetType.GUO.value;
            //            case "B" : 实物企债
            //                return AssetType.STOCK_HS.value;
            case "C":
                return AssetType.GONG_SI_ZHAI.value;
            //            case "D":
            //                return AssetType.BUY_OUT_REPURCHASE.value;
            case "E":
                return AssetType.ETF_JI_JIN.value;
            //            case "G":
            //                return AssetType.PLEDGE_REPURCHASE.value;
            case "H":
                return AssetType.ZHI_YA_DAI_MA.value;
            //            case "J" :
            //                return AssetType.GONG_SI_ZHAI.value;
            case "K":
                return AssetType.BO_JIA_GU_FEN.value;
            case "L":
                return AssetType.LOF_JI_JIN.value;
            //            case "M" :
            //                return AssetType.GONG_SI_ZHAI.value;
            //            case "N":
            //                return AssetType.SHARE_OPTION.value;
            case "Q":
                return AssetType.JIAO_YI_QUAN_ZHEN.value;
            //            case "S" :
            //                return AssetType.ZHAI.value;
            //            case "T" :
            //                return AssetType.GONG_SI_ZHAI.value;
            //            case "Y" :
            //                return AssetType.GONG_SI_ZHAI.value;
            case "Z":
                return AssetType.ZHI_DING_JIAO_YI.value;
            //            case "a" :
            //                return AssetType.GONG_SI_ZHAI.value;
            case "b":
                return AssetType.GUO_ZHAI_GOU_HUI.value;
            case "c":
                return AssetType.QI_ZHAI_GOU_HUI.value;
            //            case "d":
            //                return AssetType.PLEDGE_REPURCHASE.value;
            case "e":
                return AssetType.SI_MU_ZHAI.value;
            case "f":
                return AssetType.H_GU.value;
            case "g":
                return AssetType.GUO_ZHAI_ETF_JI_JIN.value;
            case "h":
                return AssetType.HUO_BI_ETF_JI_JIN.value;
            case "i":
                return AssetType.HUANG_JING_ETF.value;
            //            case "j" :
            //                return AssetType.GONG_SI_ZHAI.value;
            //            case "k" :
            //                return AssetType.GONG_SI_ZHAI.value;
            //            case "l" :
            //                return AssetType.GONG_SI_ZHAI.value;
            case "m":
                return AssetType.ZI_GUAN_FEN_E_ZHUAN_RANG.value;
            case "n":
                return AssetType.GUO_ZHAI_YU_FA_XING.value;
            case "o":
                return AssetType.LOF_JI_JIN.value;
            case "p":
                return AssetType.GONG_KAI_YOU_XIAN_GU.value;
            case "q":
                return AssetType.FEI_GONG_KAI_YOU_XIAN_GU.value;
            //            case "r" :
            //                return AssetType.GONG_SI_ZHAI.value;
            //            case "s" :
            //                return AssetType.GONG_SI_ZHAI.value;
            case "t":
                return AssetType.KE_JIAO_HUAN_ZHAI_HUAN_GU.value;
            //            case "z" :
            //                return AssetType.GONG_SI_ZHAI.value;
            //            case "U" :
            //                return AssetType.GONG_SI_ZHAI.value;
            case "V":
                return AssetType.HU_LUN_TONG_CUN_TUO_PING_ZHEN.value;
            //            case "W" :
            //                return AssetType.GONG_SI_ZHAI.value;
        }
        return stkType;
    }


}
