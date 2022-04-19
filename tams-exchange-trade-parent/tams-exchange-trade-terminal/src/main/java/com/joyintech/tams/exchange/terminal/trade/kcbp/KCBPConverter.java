package com.joyintech.tams.exchange.terminal.trade.kcbp;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.AccountFinance;
import com.joyintech.tams.exchange.common.bean.HistoryTrade;
import com.joyintech.tams.exchange.common.bean.IntegratedBulkCloseQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedBulkCloseQuoteQryBean;
import com.joyintech.tams.exchange.common.bean.IntegratedBulkQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedFixedIncomeQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedQuote;
import com.joyintech.tams.exchange.common.bean.IntegratedRepurchaseQuoteBean;
import com.joyintech.tams.exchange.common.bean.IntegratedRepurchaseQuoteQryBean;
import com.joyintech.tams.exchange.common.bean.NegotiateQuoteReceiveBean;
import com.joyintech.tams.exchange.common.bean.NegotiateQuoteSendBean;
import com.joyintech.tams.exchange.common.bean.PledgeDiscount;
import com.joyintech.tams.exchange.common.bean.QuoteQryBean;
import com.joyintech.tams.exchange.common.bean.StandardBonds;
import com.joyintech.tams.exchange.common.bean.terminal.RTGSStatusBean;
import com.joyintech.tams.exchange.common.constant.AssetType;
import com.joyintech.tams.exchange.common.constant.EntrustBs;
import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.constant.EntrustStatus;
import com.joyintech.tams.exchange.common.constant.Market;
import com.joyintech.tams.exchange.common.constant.QuoteType;
import com.joyintech.tams.exchange.common.constant.TradeType;
import com.joyintech.tams.exchange.common.db.domain.Account;
import com.joyintech.tams.exchange.common.db.domain.CancelEntrustBean;
import com.joyintech.tams.exchange.common.db.domain.Entrust;
import com.joyintech.tams.exchange.common.db.domain.Instrument;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.db.domain.PositionBean;
import com.joyintech.tams.exchange.common.db.domain.Trade;
import com.joyintech.tams.exchange.common.utils.EntrustUtils;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;
import com.joyintech.tams.exchange.terminal.trade.EntrustIdManager;
import com.joyintech.tams.exchange.terminal.trade.kcbp.constant.Emmlb;

public class KCBPConverter {
    private static final Logger logger = LoggerFactory.getLogger(KCBPConverter.class);

    public static int convertToEntrustStatus(String orderStatus) {
        logger.debug("orderStatus:{}" + orderStatus);
        switch (orderStatus) {
            case "0": {
                return EntrustStatus.UNREPORTED.value;
            }
            case "1":
            case "B":
            case "A": {
                return EntrustStatus.TOBE_REPORTED.value;
            }
            case "C":
            case "D":
            case "2": {
                return EntrustStatus.REPORTED.value;
            }
            case "3": {
                return EntrustStatus.REPORTED_TOBE_CANCEL.value;
            }
            case "4": {
                return EntrustStatus.PART_TRADED_TOBE_CANCELED.value;
            }
            case "7": {
                return EntrustStatus.PART_SUCCESS.value;
            }
            case "5": {
                return EntrustStatus.PART_TRADED_CANCELED.value;
            }
            case "6": {
                return EntrustStatus.CANCELED.value;
            }
            case "8": {
                return EntrustStatus.SUCCEEDED.value;
            }
            case "9": {
                return EntrustStatus.INVALID.value;
            }
        }
        // 不会调用到这里
        return EntrustStatus.INVALID.value;
    }

    public static KCBPAccount convertToAccount(Account account, List<List<String>> data) {
        // todo 验证不同的股东orgId,fundid,custid
        KCBPAccount kcbpAccount = new KCBPAccount();
        kcbpAccount.setAccountId(account.getId());
        Map<String, String> secuIds = Maps.newHashMap();
        List<String> columnNames = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            List<String> content = data.get(i);
            String market = "";
            String secuid = "";
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                String value = content.get(j);
                switch (columnName) {
                    case "custcert": {
                        kcbpAccount.setCustert(value);
                        break;
                    }
                    case "orgid": {
                        kcbpAccount.setOrgId(value);
                        break;
                    }
                    case "fundid": {
                        kcbpAccount.setFundId(value);
                        break;
                    }
                    case "custid": {
                        kcbpAccount.setCustId(value);
                        break;
                    }
                    case "secuid": {
                        secuid = value;
                        break;
                    }
                    case "market": {
                        market = value;
                        break;
                    }
                }
            }
            secuIds.put(market, secuid);
        }
        kcbpAccount.setSecuIds(secuIds);
        return kcbpAccount;
    }

    public static List<Entrust> convertToEntrust(String accountId, List<List<String>> data, EntrustIdManager entrustIdMap) {
        List<Entrust> entrusts = Lists.newArrayListWithExpectedSize(data.size() - 1);
        List<String> columnNames = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            List<String> content = data.get(i);
            Entrust entrust = new Entrust();
            entrust.setAccountId(accountId);
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                String value = content.get(j);
                switch (columnName) {
                    case "orderid": {
                        entrust.setReportNo(value);
                        break;
                    }
                    case "ordersno": {
                        entrust.setBrokerEntrustId(value);
                        break;
                    }
                    case "fundid": {
                        entrust.setFinanceAccount(value);
                        break;
                    }
                    case "bsflag": {
                        entrust.setBsFlag(value);
                        break;
                    }
                    case "reporttime":
                    case "opertime": {
                        entrust.setReportTime(value);
                        break;
                    }
                    case "market": {
                        if ("0".equals(value))
                            entrust.setMarket(Market.SZSE.value);
                        if ("1".equals(value))
                            entrust.setMarket(Market.SHSE.value);
                        break;
                    }
                    case "ordergroup": {
                        entrust.setLocalEntrustId(value);
                        entrust.setId(entrustIdMap.getIdByLocalEntrustId(accountId, value));
                        break;
                    }
                    case "stkname": {
                        entrust.setStockName(value);
                        break;
                    }
                    case "orderprice": {
                        entrust.setEntrustPrice(BigDecimal.valueOf(Double.parseDouble(value)));
                        break;
                    }
                    case "orderqty": {
                        entrust.setEntrustVolume(Long.parseLong(value));
                        break;
                    }
                    case "matchqty": {
                        entrust.setTradedVolume(Long.parseLong(value));
                        break;
                    }
                    case "matchamt": {
                        entrust.setTradedBalance(BigDecimal.valueOf(Double.parseDouble(value)));
                        break;
                    }
                    case "cancelqty": {
                        entrust.setCancelVolume(Long.parseLong(value));
                        break;
                    }
                    case "orderstatus": {
                        entrust.setEntrustStatus(convertToEntrustStatus(value));
                        break;
                    }
                    case "remark": {
                        entrust.setErrorMsg(value);
                        break;
                    }
                }
            }
            entrust.setEntrustProp(EntrustUtils.getEntrustProp(entrust.getBsFlag(), entrust.getMarket()));
            entrust.setEntrustBs(EntrustUtils.getEntrustBs(entrust.getBsFlag()));
            entrusts.add(entrust);
        }
        return entrusts;
    }

    public static List<Trade> convertToTrade(String accountId, List<List<String>> data, EntrustIdManager entrustIdMap) {
        List<Trade> trades = Lists.newArrayListWithExpectedSize(data.size() - 1);
        List<String> columnNames = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            List<String> content = data.get(i);
            Trade trade = new Trade();
            trade.setAccountId(accountId);
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                String value = content.get(j);
                switch (columnName) {
                    case "orderid": {
                        trade.setReportNo(value);
                        break;
                    }
                    case "ordersno": {
                        trade.setBrokerEntrustId(value);
                        break;
                    }
                    case "ordergroup": {
                        trade.setLocalEntrustId(value);
                        trade.setEntrustId(entrustIdMap.getIdByLocalEntrustId(accountId, value));
                        break;
                    }
                    case "fundid": {
                        trade.setFinanceAccount(value);
                        break;
                    }
                    case "bsflag": {
                        trade.setBsFlag(value);
                        break;
                    }
                    case "market": {
                        if ("0".equals(value))
                            trade.setMarket(Market.SZSE.value);
                        if ("1".equals(value))
                            trade.setMarket(Market.SHSE.value);
                        break;
                    }
                    case "stkcode": {
                        trade.setStockCode(value);
                        break;
                    }
                    case "stkname": {
                        trade.setStockName(value);
                        break;
                    }
                    case "matchtime": {
                        trade.setTradeTime(value);
                        break;
                    }
                    case "matchcode": {
                        trade.setBrokerTradeId(value);
                        break;
                    }
                    case "matchprice": {
                        trade.setTradedPrice(BigDecimal.valueOf(Double.parseDouble(value)));
                        break;
                    }
                    case "matchqty": {
                        trade.setTradedVolume(Long.parseLong(value));
                        break;
                    }
                    case "matchamt": {
                        trade.setTradedBalance(BigDecimal.valueOf(Double.parseDouble(value)));
                        break;
                    }
                    case "matchtype": {
                        trade.setTradeType(Integer.parseInt(value));
                        break;
                    }
                    case "sysdate":
                    case "trddate": {
                        trade.setTradingDay(value);
                    }
                }
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

    public static void convertToEntrustResponse(Entrust entrust, List<List<String>> data) {
        List<String> columnNames = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            List<String> content = data.get(i);
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                switch (columnName) {
                    case "ordersno": {
                        entrust.setBrokerEntrustId(content.get(j));
                        break;
                    }
                    case "orderid": {
                        entrust.setReportNo(content.get(j));
                        break;
                    }
                }
            }
            break;
        }
    }

    public static void convertToIntegratedEntrustResponse(IntegratedEntrust integratedEntrust, List<List<String>> data) {
        List<String> columnNames = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            List<String> content = data.get(i);
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                switch (columnName) {
                    case "ordersno": {
                        integratedEntrust.setBrokerEntrustId(content.get(j));
                        break;
                    }
                    case "orderid": {
                        integratedEntrust.setReportNo(content.get(j));
                        break;
                    }
                }
            }
            break;
        }
    }

    public static void convertToCancelEntrustResponse(CancelEntrustBean cancelEntrustBean, List<List<String>> data) {
        List<String> columnNames = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            List<String> content = data.get(i);
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                switch (columnName) {
                    case "ordersno": {
                        cancelEntrustBean.setCancelBrokerEntrustId(content.get(j));
                        break;
                    }
                }
            }
            break;
        }
    }

    public static void convertToCancelIntegratedEntrustEntrustResponse(CancelEntrustBean cancelEntrustBean, List<List<String>> data) {
        List<String> columnNames = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            List<String> content = data.get(i);
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                switch (columnName) {
                    case "ordersno": {
                        cancelEntrustBean.setCancelBrokerEntrustId(content.get(j));
                        break;
                    }
                }
            }
            break;
        }
    }

    public static List<IntegratedQuote> convertToIntegratedQuoteResponse(QuoteQryBean quoteQryBean, List<List<String>> data) {
        List<IntegratedQuote> integratedQuotes = Lists.newArrayListWithExpectedSize(data.size() - 1);
        List<String> columnNames = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            List<String> content = data.get(i);
            IntegratedQuote integratedQuote = new IntegratedQuote();
            integratedQuote.setAccountId(quoteQryBean.getAccountId());
            integratedQuote.setMarket(quoteQryBean.getMarket());
            integratedQuote.setQuoteType(quoteQryBean.getQuoteType());
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                String value = content.get(j);
                switch (columnName) {
                    case "HQJLH": {
                        integratedQuote.setOrderNo(value);
                        break;
                    }
                    case "HQZQDM":
                    case "stkcode": {
                        integratedQuote.setStockCode(value);
                        break;
                    }
                    case "HQSBSL":
                    case "orderqty": {
                        integratedQuote.setEntrustVolume(Integer.parseInt(value));
                        break;
                    }
                    case "HQZLLB":
                    case "bsflag": {
                        integratedQuote.setBsFlag(value);
                        break;
                    }
                    case "HQBDLX": {
                        integratedQuote.setEntrustProp(convertEntrustProp(value));
                        break;
                    }
                    case "HQMMBZ": {
                        integratedQuote.setEntrustBs(convertEntrustBs(value));
                        break;
                    }
                    case "HQSBJG":
                    case "orderprice":
                    case "closeprice":
                    case "Price": {
                        integratedQuote.setEntrustPrice(Double.parseDouble(value));
                        break;
                    }
                    case "HQYDH":
                    case "orderid": {
                        integratedQuote.setContractId(value);
                        break;
                    }
                    case "HQLXR":
                    case "PartyIDfqyaname": {
                        integratedQuote.setLiaisonName(value);
                        break;
                    }
                    case "HQLXFS":
                    case "CompanyOper":
                    case "PartyIDfqya": {
                        String[] liaisonTel = convertLiaisonTel(value, quoteQryBean);
                        if (Objects.nonNull(liaisonTel) && liaisonTel.length > 1) {
                            integratedQuote.setLiaisonTel(liaisonTel[1]);
                            integratedQuote.setLiaisonName(liaisonTel[0]);
                        } else {
                            integratedQuote.setLiaisonTel(value);
                        }
                        break;
                    }
                    case "HQSBSJ":
                    case "ordertime": {
                        integratedQuote.setReportTime(value);
                        break;
                    }
                    case "HQFQF":
                    case "PartyIDfqsa": {
                        integratedQuote.setPropSeatNo(value);
                        break;
                    }
                    // 固收
                    case "market": {
                        if ("1".equals(value))
                            integratedQuote.setMarket(Market.SHSE.value);
                        if ("0".equals(value))
                            integratedQuote.setMarket(Market.SZSE.value);
                        break;
                    }
                    case "stkname": {
                        integratedQuote.setStockName(value);
                        break;
                    }
                    case "company": {
                        integratedQuote.setCompany(value);
                        break;
                    }
                    case "fullprice": {
                        integratedQuote.setFullPrice(Double.parseDouble(value));
                        break;
                    }
                    case "lastrate": {
                        integratedQuote.setExpireIncomeRate(Double.parseDouble(value));
                        break;
                    }
                    case "bondintr": {
                        integratedQuote.setBondAccruedRate(Double.parseDouble(value));
                        break;
                    }
                    case "status": {
                        integratedQuote.setStatus(Integer.parseInt(value));
                        break;
                    }
                    case "posstr": {
                        integratedQuote.setPosstr(value);
                        break;
                    }
                    // 协议回购
                    case "MsgType": {
                        integratedQuote.setMsgType(value);
                        break;
                    }
                    case "ApplReqID": {
                        integratedQuote.setQueryReqId(value);
                        break;
                    }
                    case "EndSeqNo": {
                        integratedQuote.setEndQuoteNo(value);
                        break;
                    }
                    case "QuoteRefID": {
                        integratedQuote.setBrokerEntrustId(value);
                        break;
                    }
                    case "OrdType": {
                        integratedQuote.setReportType(value);
                        break;
                    }
                    case "RepurchaseTerm": {
                        integratedQuote.setCompactTerm(Integer.parseInt(value));
                        break;
                    }
                    case "UAInterestAccrualDays": {
                        integratedQuote.setRealOccupyDays(Integer.parseInt(value));
                        break;
                    }
                    case "SettlDate": {
                        integratedQuote.setFirstSettleDate(value);
                        break;
                    }
                    case "MaturityDate": {
                        integratedQuote.setBackDate(value);
                        break;
                    }
                    case "SettlDate2": {
                        integratedQuote.setExpireSettleDate(value);
                        break;
                    }
                    case "Side": {
                        integratedQuote.setEntrustBs(value);
                        break;
                    }
                    case "SecurityID": {
                        integratedQuote.setPledgeStampCode(value);
                        break;
                    }
                    case "Symbol":
                    case "SecurityIDname": {
                        integratedQuote.setPledgeStampName(value);
                        break;
                    }
                    case "UnderlyingSecurityID": {
                        integratedQuote.setOrigPledgeStampCode(value);
                        break;
                    }
                    case "OrderQty": {
                        integratedQuote.setPledgeStampVolume(Integer.parseInt(value));
                        break;
                    }
                    case "ContractMultiplier": {
                        integratedQuote.setDiscount(Double.parseDouble(value));
                        break;
                    }
                    case "TotalValueTraded": {
                        integratedQuote.setTradeBalance(Double.parseDouble(value));
                        break;
                    }
                    case "UnderlyingQty": {
                        integratedQuote.setOrigTradeBalance(Double.parseDouble(value));
                        break;
                    }
                    case "AccruedInterestAmt": {
                        integratedQuote.setBackInterest(Double.parseDouble(value));
                        break;
                    }
                    case "SettlCurrAmt": {
                        integratedQuote.setExpireSettleBalance(Double.parseDouble(value));
                        break;
                    }
                    case "LastQty": {
                        integratedQuote.setPledgeStampBalance(Double.parseDouble(value));
                        break;
                    }
                    case "OrderRestrictions": {
                        integratedQuote.setReductionType(value);
                        break;
                    }
                    case "OrigTradeDate": {
                        integratedQuote.setOrigTradeDate(value);
                        break;
                    }
                    case "ExecRefID": {
                        integratedQuote.setOrigSerialNo(value);
                        break;
                    }
                    case "CompanyName":
                    case "PartyIDfqname": {
                        integratedQuote.setPropSeatName(value);
                        break;
                    }
                    case "PartyIDdsfyb": {
                        integratedQuote.setOppositeLiaisonTel(value);
                        break;
                    }
                    case "Partyiddsfybname": {
                        integratedQuote.setOppositeLiaisonName(value);
                        break;
                    }
                    case "PartyIddsfsb": {
                        integratedQuote.setOppositePropSeatNo(value);
                        break;
                    }
                    case "PartyIDname": {
                        integratedQuote.setOppositePropSeatName(value);
                        break;
                    }
                    case "Zqrname": {
                        integratedQuote.setZQRName(value);
                        break;
                    }
                    case "Remark": {
                        integratedQuote.setRemark(value);
                        break;
                    }
                }
            }
            if (Market.SZSE.value.equals(quoteQryBean.getMarket())) {
                integratedQuote.setEntrustProp(EntrustUtils.getEntrustProp(integratedQuote.getBsFlag(), quoteQryBean.getMarket()));
                integratedQuote.setEntrustBs(EntrustUtils.getEntrustBs(integratedQuote.getBsFlag()));
            }
            if (!"C".equals(integratedQuote.getEntrustProp()))
                integratedQuotes.add(integratedQuote);
        }
        return integratedQuotes;
    }

    public static List<IntegratedBulkQuoteBean> convertToIntegratedBulkQuoteResponse(List<List<String>> data, String market) {
        List<IntegratedBulkQuoteBean> integratedBulkQuoteBeans = Lists.newArrayListWithExpectedSize(data.size() - 1);
        List<String> columnNames = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            List<String> content = data.get(i);
            IntegratedBulkQuoteBean integratedBulkQuoteBean = new IntegratedBulkQuoteBean();
            String bsFlag = null;
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                String value = content.get(j);
                switch (columnName) {
                    //SH
                    case "HQJLH": {//记录号
                        integratedBulkQuoteBean.setOrderNo(value);
                        break;
                    }
                    case "HQZQDM": {//证券代码
                        if (Strings.isNullOrEmpty(market)) {
                            integratedBulkQuoteBean.setStockCode(StockCodeUtils.formatStockCode(value, market));
                        } else {
                            integratedBulkQuoteBean.setStockCode(value);
                        }
                        break;
                    }
                    case "HQHYBH": {//会员编号
                        //integratedBulkQuoteBean.setSeatNo(value);
                        break;
                    }
                    case "HQYWLB": {
                        //深圳BsFlag
                        integratedBulkQuoteBean.setEntrustProp(EntrustUtils.getEntrustProp(value, "SZ"));
                        integratedBulkQuoteBean.setEntrustBs(EntrustUtils.getEntrustBs(value));
                    }
                    case "HQBDLX": {
                        //上海报单类型 "N"为意向买
                        if (value.equals("N")) {
                            integratedBulkQuoteBean.setEntrustProp(EntrustProp.INTENTION.value);
                        }
                        break;
                    }
                    case "HQMMBZ": {//买卖标志
                        //上海买卖标志
                        if (value.equals("B")) {
                            integratedBulkQuoteBean.setEntrustBs(EntrustBs.BUY.value);
                        } else if (value.equals("S")) {
                            integratedBulkQuoteBean.setEntrustBs(EntrustBs.SELL.value);
                        }
                        break;
                    }
                    case "HQSBJG": {//申报价格
                        integratedBulkQuoteBean.setEntrustPrice(Double.parseDouble(value));
                        break;
                    }
                    case "HQSBSL": {//申报数量
                        integratedBulkQuoteBean.setEntrustAmount(Integer.parseInt(value));
                        break;
                    }
                    case "HQCDBH": {//撤单编号
                        break;
                    }
                    case "HQFQF": {//发起方席位
                        integratedBulkQuoteBean.setSeatNo(value);
                        break;
                    }
                    case "HQLXFS": {//发起方联系方式
                        if (value.contains("-")) {
                            String[] liaisonTel = value.split("-");
                            if (Objects.nonNull(liaisonTel) && liaisonTel.length > 1) {
                                integratedBulkQuoteBean.setRelationName(liaisonTel[1]);
                                integratedBulkQuoteBean.setRelationTel(liaisonTel[0]);
                                break;
                            }
                        }
                        integratedBulkQuoteBean.setRelationTel(value);
                        break;
                    }
                    //SZ
                    case "HQLXR": {//联系人
                        integratedBulkQuoteBean.setRelationName(value);
                        break;
                    }
                    case "HQYDH": {//约定号
                        integratedBulkQuoteBean.setCbpconferId(value);
                        break;
                    }
                    case "HQBYWB": {//备用文本
                        integratedBulkQuoteBean.setRemark(value);
                        break;
                    }
                    case "HQSBSJ": {//申报时间
                        integratedBulkQuoteBean.setReportTime(value);
                        break;
                    }
                    case "HQZLLB": {//指令类别
                        bsFlag = value;
                        break;
                    }
                }
            }
            if (!Strings.isNullOrEmpty(bsFlag)
                    && (Emmlb.PHDJDZMR.value.equals(bsFlag) || Emmlb.PHDJDZMC.value.equals(bsFlag))) {
                continue;
            }
            integratedBulkQuoteBeans.add(integratedBulkQuoteBean);
        }
        return integratedBulkQuoteBeans;
    }

    public static List<IntegratedBulkCloseQuoteBean> convertToIntegratedBulkCloseQuoteResponseSH(IntegratedBulkCloseQuoteQryBean integratedBulkCloseQuoteQryBean, List<List<String>> data) {
        List<IntegratedBulkCloseQuoteBean> integratedBulkCloseQuoteBeans = Lists.newArrayListWithExpectedSize(data.size() - 1);
        List<String> columnNames = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            List<String> content = data.get(i);
            IntegratedBulkCloseQuoteBean integratedBulkCloseQuoteBean = new IntegratedBulkCloseQuoteBean();
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                String value = content.get(j);
                integratedBulkCloseQuoteBean.setExchangeType(integratedBulkCloseQuoteQryBean.getExchangeType());
                switch (columnName) {
                    case "stkcode": {
                        integratedBulkCloseQuoteBean.setStockCode(StockCodeUtils.formatStockCode(value, "SH"));
                        break;
                    }
                    case "stkname": {
                        integratedBulkCloseQuoteBean.setStockName(value);
                        break;
                    }
                    case "tradevolume": {
                        integratedBulkCloseQuoteBean.setBusinessAmount(Integer.parseInt(value));
                        break;
                    }
                    case "totalvaluetraded": {
                        integratedBulkCloseQuoteBean.setBusinessBalance(Double.parseDouble(value));
                        break;
                    }
                    case "closeprice": {
                        integratedBulkCloseQuoteBean.setClosingPrice(Double.parseDouble(value));
                        break;
                    }
                    case "buyvolume": {
                        integratedBulkCloseQuoteBean.setBuyAmount1(Integer.parseInt(value));
                        break;
                    }
                    case "sellvolume": {
                        integratedBulkCloseQuoteBean.setSaleAmount1(Integer.parseInt(value));
                        break;
                    }

                }
            }
            integratedBulkCloseQuoteBeans.add(integratedBulkCloseQuoteBean);
        }
        return integratedBulkCloseQuoteBeans;
    }

    public static List<IntegratedBulkCloseQuoteBean> convertToIntegratedBulkCloseQuoteResponseSZ(List<List<String>> data) {
        List<IntegratedBulkCloseQuoteBean> integratedBulkCloseQuoteBeans = Lists.newArrayListWithExpectedSize(data.size() - 1);
        List<String> columnNames = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            List<String> content = data.get(i);
            IntegratedBulkCloseQuoteBean integratedBulkCloseQuoteBean = new IntegratedBulkCloseQuoteBean();
            String bsFlag = null;
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                String value = content.get(j);
                switch (columnName) {

                    case "HQZQDM": {//证券代码
                        integratedBulkCloseQuoteBean.setStockCode(StockCodeUtils.formatStockCode(value, "SZ"));
                        break;
                    }
                    case "HQSBJG": {//申报价格
                        integratedBulkCloseQuoteBean.setClosingPrice(Double.parseDouble(value));
                        break;
                    }
                    case "HQSBSL": {//申报数量
                        integratedBulkCloseQuoteBean.setBuyAmount1(Integer.parseInt(value));
                        break;
                    }
                    case "HQSBSL2": {//申报数量2
                        integratedBulkCloseQuoteBean.setBuyAmount2(Integer.parseInt(value));
                        break;
                    }
                    case "HQZLLB": {//指令类别
                        bsFlag = value;
                        break;
                    }
                }
            }
            if (!Strings.isNullOrEmpty(bsFlag)
                    && (Emmlb.PHDJDZMR.value.equals(bsFlag) || Emmlb.PHDJDZMC.value.equals(bsFlag))) {
                integratedBulkCloseQuoteBeans.add(integratedBulkCloseQuoteBean);
            }
        }
        return integratedBulkCloseQuoteBeans;
    }

    public static List<IntegratedRepurchaseQuoteBean> convertToIntegratedRepurchaseQuoteResponse(IntegratedRepurchaseQuoteQryBean integratedRepurchaseQuoteQryBean, List<List<String>> data) {
        List<IntegratedRepurchaseQuoteBean> integratedFixedIncomeQuoteBeans = Lists.newArrayListWithExpectedSize(data.size() - 1);
        List<String> columnNames = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            List<String> content = data.get(i);
            IntegratedRepurchaseQuoteBean integratedRepurchaseQuoteBean = new IntegratedRepurchaseQuoteBean();
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                String value = content.get(j);
                switch (columnName) {
                    case "MsgType": {//消息类型
                        if ("U026".equals(value)) {
                            integratedRepurchaseQuoteBean.setExchangeType("1");
                        } else {
                            integratedRepurchaseQuoteBean.setExchangeType("0");
                        }
                        break;
                    }
                    case "ApplReqID": {//查询请求编号
                        break;
                    }
                    case "EndSeqNo": {//结束行情序号
                        break;
                    }
                    case "QuoteRefID": {//交易所报价请求编号
                        integratedRepurchaseQuoteBean.setCbpconferId(value);
                        break;
                    }
                    case "OrdType": {//报价申报类型
                        /**
                         * F:成交申报、 X:到期续做、 C:解除质押、 H:换券申报、 Z:提前终止
                         * BPB	上海协议回购成交申报		正回购
                         * BPC	上海协议回购到期续做申报		到期续做
                         * BPD	上海协议回购解除质押		接触质押
                         * BPE	上海协议回购换券申报		中途换券
                         * BPF	上海协议回购提前终止申报		提前购回
                         */
                        if (StringUtil.equals(value, "F")) {
                            integratedRepurchaseQuoteBean.setEntrustProp("BPB");
                        } else if (StringUtil.equals(value, "X")) {
                            integratedRepurchaseQuoteBean.setEntrustProp("BPC");
                        } else if (StringUtil.equals(value, "C")) {
                            integratedRepurchaseQuoteBean.setEntrustProp("BPD");
                        } else if (StringUtil.equals(value, "H")) {
                            integratedRepurchaseQuoteBean.setEntrustProp("BPE");
                        } else if (StringUtil.equals(value, "Z")) {
                            integratedRepurchaseQuoteBean.setEntrustProp("BPF");
                        }
                        break;
                    }
                    case "Price": {//回购利率
                        if (!Strings.isNullOrEmpty(value))
                            integratedRepurchaseQuoteBean.setExpireYearRate(Double.parseDouble(value));
                        break;
                    }
                    case "RepurchaseTerm": {//回购期限
                        if (!Strings.isNullOrEmpty(value))
                            integratedRepurchaseQuoteBean.setRepoTerm(Integer.parseInt(value));
                        break;
                    }
                    case "UAInterestAccrualDays": {//实际占款天数
                        if (!Strings.isNullOrEmpty(value))
                            integratedRepurchaseQuoteBean.setFundUsedDays(Integer.parseInt(value));
                        break;
                    }
                    case "SettlDate": {//首次结算日
                        if (!Strings.isNullOrEmpty(value))
                            integratedRepurchaseQuoteBean.setFirstExchdate(Integer.parseInt(value));
                        break;
                    }
                    case "MaturityDate": {//回购到期日
                        if (!Strings.isNullOrEmpty(value))
                            integratedRepurchaseQuoteBean.setRepurchaseDate(Integer.parseInt(value));
                        break;
                    }
                    case "SettlDate2": {//到期结算日
                        if (!Strings.isNullOrEmpty(value))
                            integratedRepurchaseQuoteBean.setSettleEndDate(Integer.parseInt(value));
                        break;
                    }
                    case "Side": {//买卖方向
                        integratedRepurchaseQuoteBean.setEntrustBs(value); //1:正回购，2:逆回购 对应EntrustBs字段，无需转换
                        break;
                    }
                    case "SecurityID": {//质押券代码
                        if (!Strings.isNullOrEmpty(value))
                            integratedRepurchaseQuoteBean.setStockCode(StockCodeUtils.formatStockCode(value, "SH"));//目前仅支持上海协议回购
                        break;
                    }
                    case "SecurityIDname": {//质押券名称
                        integratedRepurchaseQuoteBean.setStockName(value);
                        break;
                    }
                    case "UnderlyingSecurityID": {//原质押券代码
                        integratedRepurchaseQuoteBean.setOldImpawnCode(value);
                        break;
                    }
                    case "OrderQty": {//质押券数量
                        if (!Strings.isNullOrEmpty(value))
                            integratedRepurchaseQuoteBean.setImpawnAmount(Integer.parseInt(value));
                        break;
                    }
                    case "ContractMultiplier": {//折算比例
                        if (!Strings.isNullOrEmpty(value))
                            integratedRepurchaseQuoteBean.setFunderRatio(Double.parseDouble(value));
                        break;
                    }
                    case "TotalValueTraded": {//成交金额
                        if (!Strings.isNullOrEmpty(value))
                            integratedRepurchaseQuoteBean.setImpawnBalance(Double.parseDouble(value));
                        break;
                    }
                    case "UnderlyingQty": {//原成交金额
                        if (!Strings.isNullOrEmpty(value))
                            integratedRepurchaseQuoteBean.setPrevImpawnBalance(Double.parseDouble(value));
                        break;
                    }
                    case "AccruedInterestAmt": {//回购利息
                        if (!Strings.isNullOrEmpty(value))
                            integratedRepurchaseQuoteBean.setProfit(Double.parseDouble(value));
                        break;
                    }
                    case "SettlCurrAmt": {//到期结算金额
                        if (!Strings.isNullOrEmpty(value))
                            integratedRepurchaseQuoteBean.setSettleBalance(Double.parseDouble(value));
                        break;
                    }
                    case "LastQty": {//质押券面总额合计
                        integratedRepurchaseQuoteBean.setAssetNetValue(Double.parseDouble(value));
                        break;
                    }
                    case "OrderRestrictions": {//到期续做类型
                        integratedRepurchaseQuoteBean.setBprsrcStatus(value);
                        break;
                    }
                    case "OrigTradeDate": {//初始交易日期
                        if (!Strings.isNullOrEmpty(value))
                            integratedRepurchaseQuoteBean.setOrigEntrustDate(Integer.parseInt(value));
                        break;
                    }
                    case "ExecRefID": {//成交编号
                        integratedRepurchaseQuoteBean.setOrigCbpconferId(value);
                        break;
                    }
                    case "PartyIDfqya": {//发起方交易员代码
                        integratedRepurchaseQuoteBean.setTraderId(value);
                        break;
                    }
                    case "PartyIDfqyaname": {//发起方交易员姓名
                        break;
                    }
                    case "PartyIDfqsa": {//发起方交易商代码
                        integratedRepurchaseQuoteBean.setAgencyNo(value);
                        break;
                    }
                    case "PartyIDfqname": {//发起方交易商名称
                        integratedRepurchaseQuoteBean.setAgencyName(value);
                        break;
                    }
                    case "PartyIDdsfyb": {//对手方交易员代码
                        integratedRepurchaseQuoteBean.setOppoTraderId(value);
                        break;
                    }
                    case "Partyiddsfybname": {//对手方交易员姓名
                        break;
                    }
                    case "PartyIddsfsb": {//对手方交易商代码
                        integratedRepurchaseQuoteBean.setOppoAgency(value);
                        break;
                    }
                    case "PartyIDname": {//对手方交易商名称
                        integratedRepurchaseQuoteBean.setOppoAgencyName(value);
                        break;
                    }
                    case "CompanyName": {//交易商简称
                        integratedRepurchaseQuoteBean.setAgencyName(value);
                        break;
                    }
                    case "CompanyOper": {//交易员代码
                        integratedRepurchaseQuoteBean.setTraderId(value);
                        break;
                    }
                    case "Symbol": {//质押债券简称
                        integratedRepurchaseQuoteBean.setStockName(value);
                        break;
                    }
                    case "Zqrname": {
                        integratedRepurchaseQuoteBean.setPledgeeName(value);
                        break;
                    }
                    case "Remark": {
                        integratedRepurchaseQuoteBean.setRemark(value);
                        break;
                    }
                    case "posstr": {
                        integratedRepurchaseQuoteBean.setPositionStr(value);
                        break;
                    }
                }
            }
            integratedFixedIncomeQuoteBeans.add(integratedRepurchaseQuoteBean);
        }
        return integratedFixedIncomeQuoteBeans;
    }

    public static List<IntegratedFixedIncomeQuoteBean> convertToIntegratedFixedIncomeQuoteResponse(List<List<String>> data) {
        List<IntegratedFixedIncomeQuoteBean> integratedFixedIncomeQuoteBeans = Lists.newArrayListWithExpectedSize(data.size() - 1);
        List<String> columnNames = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            List<String> content = data.get(i);
            IntegratedFixedIncomeQuoteBean integratedFixedIncomeQuoteBean = new IntegratedFixedIncomeQuoteBean();
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                String value = content.get(j);
                String market = null;
                switch (columnName) {
                    case "market": {
                        market = value;
                        break;
                    }
                    case "stkcode": {
                        integratedFixedIncomeQuoteBean.setStockCode(value);
                        break;
                    }
                    case "stkname": {
                        integratedFixedIncomeQuoteBean.setStockName(value);
                        break;
                    }
                    case "bsflag": {
                        integratedFixedIncomeQuoteBean.setEntrustBs(EntrustUtils.getEntrustBs(value));
                        break;
                    }
                    case "orderid": {
                        integratedFixedIncomeQuoteBean.setOrderNo(value);
                        break;
                    }
                    case "ordertime": {
                        integratedFixedIncomeQuoteBean.setReportTime(Integer.parseInt(value));
                        break;
                    }
                    case "company": {
                        integratedFixedIncomeQuoteBean.setAgencyName(value);
                        break;
                    }
                    case "orderprice": {
                        integratedFixedIncomeQuoteBean.setEntrustPrice(Double.parseDouble(value));
                        break;
                    }
                    case "fullprice": {
                        integratedFixedIncomeQuoteBean.setFullPrice(Double.parseDouble(value));
                        break;
                    }
                    case "orderqty": {
                        integratedFixedIncomeQuoteBean.setEntrustVolume(Integer.parseInt(value));
                        break;
                    }
                    case "bondintr": {
                        integratedFixedIncomeQuoteBean.setInterest(Double.parseDouble(value));
                        break;
                    }
                    case "lastrate": {
                        integratedFixedIncomeQuoteBean.setExpireIncomeRate(Double.parseDouble(value));
                        break;
                    }
                }
                if (!Strings.isNullOrEmpty(market)) {
                    integratedFixedIncomeQuoteBean.setStockCode(StockCodeUtils.formatStockCode(value, market));
                }
            }
            integratedFixedIncomeQuoteBeans.add(integratedFixedIncomeQuoteBean);
        }
        return integratedFixedIncomeQuoteBeans;
    }

    public static int convertToEnable(List<List<String>> data) {
        int amountMap = 0;
        List<String> columnNames = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            List<String> content = data.get(i);
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                String value = content.get(j);
                switch (columnName) {
                    case "maxstkqty": {
                        amountMap = Integer.parseInt(value);
                        break;
                    }
                }
            }
        }
        return amountMap;
    }

    public static List<HistoryTrade> convertToHistoryTrade(String accountId, List<List<String>> data) {
        List<HistoryTrade> historyTradeList = Lists.newArrayListWithExpectedSize(data.size() - 1);
        List<String> columnNames = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            List<String> content = data.get(i);
            HistoryTrade historyTrade = new HistoryTrade();
            historyTrade.setAccountId(accountId);
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                String value = content.get(j);
                switch (columnName) {
                    case "poststr": {
                        historyTrade.setPostStr(value);
                        break;
                    }
                    case "bizdate": {
                        historyTrade.setClearDay(value);
                        break;
                    }
                    case "cleardate": {
                        historyTrade.setTradingDay(value);
                        break;
                    }
                    case "fundid": {
                        historyTrade.setFinanceAccount(value);
                        break;
                    }
                    case "orgid": {
                        historyTrade.setOrgId(value);
                        break;
                    }
                    case "secuid": {
                        historyTrade.setStockHolderId(value);
                        break;
                    }
                    case "bsflag": {
                        historyTrade.setBsFlag(value);
                        break;
                    }
                    case "orderid": {
                        historyTrade.setOrderId(value);
                        break;
                    }
                    case "ordersno": {
                        historyTrade.setSerialNo(value);
                        break;
                    }
                    case "market": {
                        historyTrade.setMarket(value);
                        break;
                    }
                    case "stkcode": {
                        historyTrade.setStockCode(value);
                        break;
                    }
                    case "stkname": {
                        historyTrade.setStockName(value);
                        break;
                    }
                    case "matchtime": {
                        historyTrade.setTradeTime(value);
                        break;
                    }
                    case "matchcode": {
                        historyTrade.setBrokerTradeId(value);
                        break;
                    }
                    case "matchprice": {
                        historyTrade.setTradePrice(Double.parseDouble(value));
                        break;
                    }
                    case "matchqty": {
                        historyTrade.setTradeVolume(Long.parseLong(value));
                        break;
                    }
                    case "matchamt": {
                        historyTrade.setTradedBalance(Double.parseDouble(value));
                        break;
                    }
                    case "orderqty": {
                        historyTrade.setEntrustVolume(Long.parseLong(value));
                        break;
                    }
                    case "orderprice": {
                        historyTrade.setEntrustPrice(Double.parseDouble(value));
                        break;
                    }
                    case "fee_jsxf": {
                        historyTrade.setJinShouXuFei(Double.parseDouble(value));
                        break;
                    }
                    case "fee_sxf": {
                        historyTrade.setProcedureCost(Double.parseDouble(value));
                        break;
                    }
                    case "fee_yhs": {
                        historyTrade.setPrintingCost(Double.parseDouble(value));
                        break;
                    }
                    case "fee_ghf": {
                        historyTrade.setTransferCost(Double.parseDouble(value));
                        break;
                    }
                    case "fee_qsf": {
                        historyTrade.setClearCost(Double.parseDouble(value));
                        break;
                    }
                    case "fee_jygf": {
                        historyTrade.setTradeRuleCost(Double.parseDouble(value));
                        break;
                    }
                    case "fee_jsf": {
                        historyTrade.setHandleCost(Double.parseDouble(value));
                        break;
                    }
                    case "fee_zgf": {
                        historyTrade.setSecCost(Double.parseDouble(value));
                        break;
                    }
                    case "fee_qtf": {
                        historyTrade.setOtherCost(Double.parseDouble(value));
                        break;
                    }
                    case "feefront": {
                        historyTrade.setFrontCost(Double.parseDouble(value));
                        break;
                    }
                    case "stkbal": {
                        historyTrade.setStockResidue(Long.parseLong(value));
                        break;
                    }
                    case "fundeffect": {
                        historyTrade.setFinanceAmount(Double.parseDouble(value));
                        break;
                    }
                    case "fundbal": {
                        historyTrade.setUseMoney(Double.parseDouble(value));
                        break;
                    }
                    case "bondintr": {
                        historyTrade.setBondInterest(Double.parseDouble(value));
                        break;
                    }
                    case "prodcode": {
                        historyTrade.setPortfolioId(value);
                        break;
                    }
                    case "stktype": {
                        historyTrade.setStockType(value);
                        break;
                    }
                }
            }
            historyTradeList.add(historyTrade);
        }
        return historyTradeList;
    }

    public static List<AccountFinance> convertToCustomerFinance(String accountId, List<List<String>> data) {
        List<AccountFinance> accountFinanceList = Lists.newArrayListWithExpectedSize(data.size() - 1);
        List<String> columnNames = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            List<String> content = data.get(i);
            AccountFinance accountFinance = new AccountFinance();
            accountFinance.setAccountId(accountId);
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                String value = content.get(j);
                switch (columnName) {
                    case " custid": {
                        accountFinance.setCustomerId(value);
                        break;
                    }
                    case " fundid": {
                        accountFinance.setFinanceAccount(value);
                        break;
                    }
                    case " fortuneid": {
                        accountFinance.setFortuneId(value);
                        break;
                    }
                    case "orgid": {
                        accountFinance.setOrgId(value);
                        break;
                    }
                    case " moneytype": {
                        accountFinance.setMoneyType(value);
                        break;
                    }
                    case " fundbal": {
                        accountFinance.setFinanceBalance(Double.parseDouble(value));
                        break;
                    }
                    case " fundavl": {
                        accountFinance.setEnableBalance(Double.parseDouble(value));
                        break;
                    }
                    case " marketvalue": {
                        accountFinance.setTotalAssets(Double.parseDouble(value));
                        break;
                    }
                    case " fund": {
                        accountFinance.setFinanceAssets(Double.parseDouble(value));
                        break;
                    }
                    case " stkvalue": {
                        accountFinance.setStkValue(Double.parseDouble(value));
                        break;
                    }
                    case "fundloan": {
                        accountFinance.setFundLoan(Double.parseDouble(value));
                        break;
                    }
                    case "fundfrz": {
                        accountFinance.setTotalFrozenFinance(Double.parseDouble(value));
                        break;
                    }
                    case "fundlastbal": {
                        accountFinance.setPrevDayFinanceBalance(Double.parseDouble(value));
                        break;
                    }
                }
            }
            accountFinanceList.add(accountFinance);
        }
        return accountFinanceList;
    }

    public static List<Instrument> convertToInstrumentInfo(List<List<String>> data) {
        List<Instrument> instrumentList = Lists.newArrayListWithExpectedSize(data.size() - 1);
        List<String> columnNames = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            List<String> content = data.get(i);
            Instrument instrument = new Instrument();
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                String value = content.get(j);
                switch (columnName) {
                    case "poststr": {
                        instrument.setPostStr(value);
                        break;
                    }
                    case "market": {
                        instrument.setMarket(getMarket(value));
                        break;
                    }
                    case "stkname": {
                        instrument.setStockName(value);
                        break;
                    }
                    case "stkcode": {
                        instrument.setStockCode(value);
                        instrument.setInnerCode(getInnerCode(instrument.getMarket(), value));
                        break;
                    }
                    case "priceunit": {
                        instrument.setPriceStep(getPriceStep(value));
                        break;
                    }
                    case "stktype": {
                        instrument.setAssetType(getAssetType(value));
                        break;
                    }
                    case "maxrisevalue": {
                        instrument.setUpPrice(new BigDecimal(value));
                        break;
                    }
                    case "maxdownvalue": {
                        instrument.setDownPrice(new BigDecimal(value));
                        break;
                    }
                    case "maxqty": {
                        instrument.setHighVolume(Long.parseLong(value));
                        break;
                    }
                    case "minqty": {
                        instrument.setLowVolume(Integer.parseInt(value));
                        break;
                    }
                    case "buyunit": {
                        instrument.setBuyUnit(Integer.parseInt(value));
                        break;
                    }
                    case "stkstatus": {
                        instrument.setStkcodeStatus(value);
                        break;
                    }
                    case "marketmaxqty": {
                        instrument.setMarketHighVolume(Integer.parseInt(value));
                        break;
                    }
                    case "marketminqty": {
                        instrument.setMarketLowVolume(Integer.parseInt(value));
                        break;
                    }
                }
            }
            instrumentList.add(instrument);
        }
        return instrumentList;
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

    public static String convertEntrustProp(String entrustProp) {
        if ("N".equals(entrustProp)) {
            return EntrustProp.INTENTION.value;
        }
        return entrustProp;
    }

    public static String[] convertLiaisonTel(String liaisonTel, QuoteQryBean quoteQryBean) {
        if ((QuoteType.BULK_QUOTE.value == quoteQryBean.getQuoteType()
                || QuoteType.AFTER_BULK_QUOTE.value == quoteQryBean.getQuoteType())
                && Market.SHSE.value.equals(quoteQryBean.getMarket()))
            return liaisonTel.split("-");
        else
            return null;
    }

    /***
     * @Description: 标准券查询
     * @Param: [data]
     * @return: java.util.List<com.joyintech.tams.exchange.common.bean.StandardBonds>
     * @Author: MAXX
     * @Date: 2020/5/13
     */
    public static List<StandardBonds> convertToStandardBonds(List<List<String>> data) {
        List<StandardBonds> standardBondsList = Lists.newArrayListWithExpectedSize(data.size() - 1);
        List<String> columnNames = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            List<String> content = data.get(i);
            StandardBonds standardBonds = new StandardBonds();
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                String value = content.get(j);
                switch (columnName) {
                    case "market": {
                        if ("1".equals(value))
                            standardBonds.setMarket(Market.SHSE.value);
                        if ("0".equals(value))
                            standardBonds.setMarket(Market.SZSE.value);
                        break;
                    }
                    case "secuid": {
                        standardBonds.setStockAccount(value);
                        break;
                    }
                    case "stkcode": {
                        standardBonds.setStockCode(value);
                        break;
                    }
                    case "stktype": {
                        standardBonds.setStockType(value);
                        break;
                    }
                    case "exchrate": {
                        standardBonds.setDiscount(Double.parseDouble(value));
                        break;
                    }
                    case "bondunfrz": {
                        standardBonds.setBondUnFrozen(Integer.parseInt(value));
                        break;
                    }
                    case "bondfrz": {
                        standardBonds.setBondFrozen(Integer.parseInt(value));
                        break;
                    }
                    case "remainqty": {
                        standardBonds.setBondEnable(Integer.parseInt(value));
                        break;
                    }
                }
            }
            standardBondsList.add(standardBonds);
        }
        return standardBondsList;
    }

    /***
     * @Description: 债券质押率返回数据解析
     * @Param: [accountId, data]
     * @return: java.util.List<com.joyintech.tams.exchange.common.bean.PledgeDiscount>
     * @Author: MAXX
     * @Date: 2020/5/14
     */
    public static List<PledgeDiscount> convertToPledgeDiscount(String accountId, List<List<String>> data) {
        List<PledgeDiscount> pledgeDiscountList = Lists.newArrayListWithExpectedSize(data.size() - 1);
        List<String> columnNames = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            List<String> content = data.get(i);
            PledgeDiscount pledgeDiscount = new PledgeDiscount();
            pledgeDiscount.setAccountId(accountId);
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                String value = content.get(j);
                switch (columnName) {
                    case "market": {
                        if ("1".equals(value))
                            pledgeDiscount.setMarket(Market.SHSE.value);
                        if ("0".equals(value))
                            pledgeDiscount.setMarket(Market.SZSE.value);
                        break;
                    }
                    case "secuid": {
                        pledgeDiscount.setStockAccount(value);
                        break;
                    }
                    case "stkcode": {
                        pledgeDiscount.setStockCode(value);
                        break;
                    }
                    case "fundid": {
                        pledgeDiscount.setFinanceAccount(value);
                        break;
                    }
                    case "bondlastbal": {
                        pledgeDiscount.setBondlastbal(Integer.parseInt(value));
                        break;
                    }
                    case "bondbal": {
                        pledgeDiscount.setBondbal(Integer.parseInt(value));
                        break;
                    }
                    case "bondavl": {
                        pledgeDiscount.setBondEnable(Integer.parseInt(value));
                        break;
                    }
                    case "exchrate": {
                        pledgeDiscount.setDiscount(Double.parseDouble(value));
                        break;
                    }
                }
            }
            pledgeDiscount.setFinprodMarketId(pledgeDiscount.getStockCode() + "." + pledgeDiscount.getMarket());
            pledgeDiscountList.add(pledgeDiscount);
        }
        return pledgeDiscountList;
    }

    /***
     * @Description: 持仓查询返回数据解析
     * @Param: [financeAccount, accountId, data]
     * @return: java.util.List<com.joyintech.tams.exchange.common.bean.PositionBean>
     * @Author: MAXX
     * @Date: 2020/9/29
     */
    public static List<PositionBean> convertToAccountPosition(String financeAccount, String accountId, List<List<String>> data) {
        List<PositionBean> list = Lists.newArrayListWithExpectedSize(data.size() - 1);
        List<String> columnNames = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            List<String> content = data.get(i);
            PositionBean positionBean = new PositionBean();
            positionBean.setAccountId(accountId);
            positionBean.setFinanceAccount(financeAccount);
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                String value = content.get(j);
                switch (columnName.trim()) {
                    case "market": {
                        positionBean.setMarket(getMarket(value));
                        break;
                    }
                    case "stkcode": {
                        positionBean.setStockCode(value);
                        break;
                    }
                    case "stkname": {
                        positionBean.setStockName(value);
                        break;
                    }
                    case "costprice": {
                        positionBean.setCostPrice(Double.parseDouble(value));
                        break;
                    }
                    case "mktval": {
                        positionBean.setMarketValue(Double.parseDouble(value));
                        break;
                    }
                    case "stkbal": {
                        positionBean.setHoldVolume(Long.parseLong(value));
                        break;
                    }
                    case "stkavl": {
                        positionBean.setEnableVolume(Long.parseLong(value));
                        break;
                    }
                }
            }
            positionBean.setStockCode(positionBean.getStockCode() + "." + positionBean.getMarket());
            list.add(positionBean);
        }
        return list;
    }

    public static String getMarket(String market) {
        if ("1".equals(market))
            return Market.SHSE.value;
        else if ("0".equals(market))
            return Market.SZSE.value;
        else
            return market;
    }

    public static String getKCBPMarket(String market) {
        return Market.SHSE.value.equals(market) ? "1" : "0";
    }

    public static String getInnerCode(String market, String stockCode) {
        if (Market.SHSE.value.equals(market))
            return stockCode + "." + Market.SHSE.value;
        else
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

    //获取报价申报类型
    public static String getOrdtype(String entrustProp) {
        if (EntrustProp.SH_REPURCHASE_TRADE_CONFIRM.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_TRADE_REFUSE.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_TRADE.value.equals(entrustProp)) {
            return "F";//成交申报
        } else if (EntrustProp.SH_REPURCHASE_RENEWAL_CONFIRM.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_RENEWAL_REFUSE.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_RENEWAL.value.equals(entrustProp)) {
            return "X";//到期续做
        } else if (EntrustProp.SH_REPURCHASE_RELEASE_CONFIRM.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_RELEASE_REFUSE.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_RELEASE.value.equals(entrustProp)) {
            return "C";//解除质押
        } else if (EntrustProp.SH_REPURCHASE_EXCHANGE_CONFIRM.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_EXCHANGE_REFUSE.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_EXCHANGE.value.equals(entrustProp)) {
            return "H";//换券申报
        } else if (EntrustProp.SH_REPURCHASE_TERMINATE_CONFIRM.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_TERMINATE_REFUSE.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_TERMINATE.value.equals(entrustProp)) {
            return "Z";//提前终止
        }
        return "";
    }

    public static List<RTGSStatusBean> convertToRTGS(String accountId, List<List<String>> data) {
        List<RTGSStatusBean> rtgsStatusBeanList = Lists.newArrayListWithExpectedSize(data.size() - 1);
        List<String> columnNames = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            List<String> content = data.get(i);
            RTGSStatusBean rtgsStatusBean = new RTGSStatusBean();
            rtgsStatusBean.setAccountId(accountId);
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                String value = content.get(j);
                switch (columnName.trim()) {
                    case "orderdate": {
                        rtgsStatusBean.setEntrustDate(value);
                        break;
                    }
                    case "orderid": {
                        rtgsStatusBean.setReportNo(value);
                        break;
                    }
                    case "code": {
                        rtgsStatusBean.setErrorCode(value);
                        break;
                    }
                    case "description": {
                        rtgsStatusBean.setErrorMsg(value);
                        break;
                    }
                    case "status": {
                        rtgsStatusBean.setStatus(value);
                        break;
                    }
                }
            }
            rtgsStatusBeanList.add(rtgsStatusBean);
        }
        return rtgsStatusBeanList;
    }
    
    public static List<NegotiateQuoteReceiveBean> convertToNegotiateQuoteReceiveResponseSZ(List<List<String>> data) {
        List<NegotiateQuoteReceiveBean> negotiateQuoteReceiveBeans = Lists.newArrayListWithExpectedSize(data.size() - 1);
        List<String> columnNames = data.get(0);
        for (int i = 1; i < data.size(); i++) {
            List<String> content = data.get(i);
            NegotiateQuoteReceiveBean negotiateQuoteReceiveBean = new NegotiateQuoteReceiveBean();
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                String value = content.get(j);
                //TODO 根据接口返回转换成对应bean
                switch (columnName) {
                    case "transacttime": {//申报时间
                    	negotiateQuoteReceiveBean.setRequestTime(Integer.parseInt(value));
                        break;
                    }
                    case "reportstatus": {//成交申报请求状态
                    	negotiateQuoteReceiveBean.setRequestStatus(Integer.parseInt(value)); 
                        break;
                    }
                    case "stkcode": {//证券代码
                    	negotiateQuoteReceiveBean.setStockCode(StockCodeUtils.formatStockCode(value, "SZ"));
                        break;
                    }
                    case "stkname": {//证券名称
                    	negotiateQuoteReceiveBean.setStockName(value);
                        break;
                    }
                    case "side": {//买卖方向
                    	negotiateQuoteReceiveBean.setBs(value);
                        break;
                    }
                    case "orderqty": {//数量
                    	negotiateQuoteReceiveBean.setAmount(Integer.parseInt(value));
                        break;
                    }
                    //TODO 接口返回没有全价  委托金额
                    case "orderprice": {//价格（净价） 
                    	negotiateQuoteReceiveBean.setEntrustPrice(BigDecimal.valueOf(Double.parseDouble(value)));
                        break;
                    }
                    case "cpmemberid": {//接收方交易商代码 --> 本方交易商
                    	negotiateQuoteReceiveBean.setMemberId(value);
                        break;
                    }
                    case "cptradercode": {//接收方交易员代码 --> 本方交易员
                    	negotiateQuoteReceiveBean.setTraderCode(value);
                        break;
                    }
                    case "cpinvestorid": {//接收方交易主体代码 --> 本方交易主体
                    	negotiateQuoteReceiveBean.setInvestorId(value);
                        break;
                    }
                    case "cpinvestortype": {//接收方交易主体类型 --> 本方交易主体类型
                    	negotiateQuoteReceiveBean.setInvestorType(value);
                        break;
                    }
                    case "memberid": {//申报方交易商 --> 对手方交易商
                    	negotiateQuoteReceiveBean.setCpInvestorType(value);
                        break;
                    }
                    case "tradercode": {//申报方交易员代码 --> 对手方交易员
                    	negotiateQuoteReceiveBean.setCpTraderCode(value);
                        break;
                    }
                    case "investorid": {//申报方交易主体代码 --> 对手方交易主体
                    	negotiateQuoteReceiveBean.setCpInvestorId(value);
                        break;
                    }
                    case "investortype": {//申报方交易主体类型 --> 对手方交易主体类型
                    	negotiateQuoteReceiveBean.setCpInvestorType(value);
                        break;
                    }
                    case "confirmid": {//约定号
                    	negotiateQuoteReceiveBean.setConfirmId(value);
                        break;
                    }
                    case "posstr": {//定位串
                    	negotiateQuoteReceiveBean.setPositionStr(value);
                        break;
                    }
                    case "memo": {//备注
                    	negotiateQuoteReceiveBean.setRemark(value);
                        break;
                    }
                }
            }
        	negotiateQuoteReceiveBeans.add(negotiateQuoteReceiveBean);
        }
        return negotiateQuoteReceiveBeans;
    }
    
//    public static List<NegotiateQuoteReceiveBean> convertToNegotiateQuoteSendResponseSZ(List<List<String>> data) {
//        List<NegotiateQuoteSendBean> negotiateQuoteSendBeans = Lists.newArrayListWithExpectedSize(data.size() - 1);
//        List<String> columnNames = data.get(0);
//        for (int i = 1; i < data.size(); i++) {
//            List<String> content = data.get(i);
//            NegotiateQuoteSendBean negotiateQuoteSendBean = new NegotiateQuoteSendBean();
//            for (int j = 0; j < columnNames.size(); j++) {
//                String columnName = columnNames.get(j);
//                String value = content.get(j);
//                //TODO 根据接口返回转换成对应bean
//                switch (columnName) {
//	                case "ordersno": {//委托序号 --> 请求编号
//	                	negotiateQuoteSendBean.setRequestNo(value);
//	                    break;
//	                }
//                    case "orderdate": {//委托日期 --> 请求时间
//                    	negotiateQuoteSendBean.setRequestTime(Integer.parseInt(value));
//                        break;
//                    }
//                    case "orderstatus": {//委托状态 --> 请求状态
//                    	negotiateQuoteSendBean.setRequestStatus(Integer.parseInt(value));
//                        break;
//                    }
//                    case "stkcode": {//证券代码
//                    	negotiateQuoteSendBean.setStockCode(StockCodeUtils.formatStockCode(value, "SZ"));
//                        break;
//                    }
//                    case "stkname": {//证券名称
//                    	negotiateQuoteSendBean.setStockName(value);
//                        break;
//                    }
//                    case "bsflag": {//买卖类别 --> 对手方买卖方向
//                    	negotiateQuoteSendBean.setBs(value);
//                        break;
//                    }
//                    case "orderqty": {//数量
//                    	negotiateQuoteSendBean.setAmount(Integer.parseInt(value));
//                        break;
//                    }
//                    //TODO 接口返回没有全价  委托金额
//                    case "orderprice": {//价格（净价） 
//                    	negotiateQuoteSendBean.setEntrustPrice(BigDecimal.valueOf(Double.parseDouble(value)));
//                        break;
//                    }
//                    case "cpmemberid": {//接收方交易商代码 --> 本方交易商
//                    	negotiateQuoteReceiveBean.setMemberId(value);
//                        break;
//                    }
//                    case "cptradercode": {//接收方交易员代码 --> 本方交易员
//                    	negotiateQuoteReceiveBean.setTraderCode(value);
//                        break;
//                    }
//                    case "cpinvestorid": {//接收方交易主体代码 --> 本方交易主体
//                    	negotiateQuoteReceiveBean.setInvestorId(value);
//                        break;
//                    }
//                    case "cpinvestortype": {//接收方交易主体类型 --> 本方交易主体类型
//                    	negotiateQuoteReceiveBean.setInvestorType(value);
//                        break;
//                    }
//                    case "memberid": {//申报方交易商 --> 对手方交易商
//                    	negotiateQuoteReceiveBean.setCpInvestorType(value);
//                        break;
//                    }
//                    case "tradercode": {//申报方交易员代码 --> 对手方交易员
//                    	negotiateQuoteReceiveBean.setCpTraderCode(value);
//                        break;
//                    }
//                    case "investorid": {//申报方交易主体代码 --> 对手方交易主体
//                    	negotiateQuoteReceiveBean.setCpInvestorId(value);
//                        break;
//                    }
//                    case "investortype": {//申报方交易主体类型 --> 对手方交易主体类型
//                    	negotiateQuoteReceiveBean.setCpInvestorType(value);
//                        break;
//                    }
//                    case "confirmid": {//约定号
//                    	negotiateQuoteReceiveBean.setConfirmId(value);
//                        break;
//                    }
//                    case "posstr": {//定位串
//                    	negotiateQuoteReceiveBean.setPositionStr(value);
//                        break;
//                    }
//                    case "memo": {//备注
//                    	negotiateQuoteReceiveBean.setRemark(value);
//                        break;
//                    }
//                }
//            }
//        	negotiateQuoteReceiveBeans.add(negotiateQuoteReceiveBean);
//        }
//        return negotiateQuoteReceiveBeans;
//    }
}
