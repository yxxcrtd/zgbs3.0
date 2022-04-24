package com.joyintech.tams.exchange.terminal.trade.mock;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Strings;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.bean.IntegratedQuote;
import com.joyintech.tams.exchange.common.bean.NegotiateQuoteReceiveBean;
import com.joyintech.tams.exchange.common.bean.TickBean;
import com.joyintech.tams.exchange.common.constant.EntrustBs;
import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.constant.FullPriceConst;
import com.joyintech.tams.exchange.common.constant.Market;
import com.joyintech.tams.exchange.common.constant.QuoteType;
import com.joyintech.tams.exchange.common.db.domain.IntegratedEntrust;
import com.joyintech.tams.exchange.common.utils.StockCodeUtils;

public class MockQuoteData {
    private static int[] contractIds = {198, 674};

    private static String[] liaisonName = {"张凤", "汇明发"};

    private static String[] liaisonTel = {"13115741349", "17717641260"};

    private static String[] propSeatNo = {"062700", "432115"};

    private static AtomicInteger orderNo = new AtomicInteger(1);

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

    /**
     * 大宗行情
     */
    public static List<IntegratedQuote> makeMockDataForBulk(String stockCode, TickBean tickBean) {
        List<IntegratedQuote> integratedQuotes = new ArrayList<>();
        if (StringUtil.isEmpty(stockCode)) {
            return integratedQuotes;
        }
        for (int i = 1; i < 5; i++) {
            IntegratedQuote integratedQuote = new IntegratedQuote();
            integratedQuote.setQuoteType(QuoteType.BULK_QUOTE.value);
            integratedQuote.setStockCode(stockCode);
            integratedQuote.setStockName(stockCode);
            integratedQuote.setReportTime(simpleDateFormat.format(new Date(System.currentTimeMillis())));
            integratedQuote.setOrderNo(orderNo.incrementAndGet() + "");
            if (Market.SHSE.value.equals(StockCodeUtils.getMarket(stockCode))) {
                integratedQuote.setPropSeatNo(propSeatNo[0]);
                integratedQuote.setMarket(Market.SHSE.value);
            } else {
                integratedQuote.setPropSeatNo(propSeatNo[1]);
                integratedQuote.setMarket(Market.SZSE.value);
            }
            integratedQuote.setEntrustVolume(300000 + i * 100000);
            integratedQuote.setEntrustPrice(25.11 + i);
            if (i == 1 || i == 2) {
                integratedQuote.setEntrustProp(EntrustProp.INTENTION.value);
                if (i == 1) {
                    integratedQuote.setEntrustBs(EntrustBs.BUY.value);
                } else {
                    integratedQuote.setEntrustBs(EntrustBs.SELL.value);
                }
                integratedQuote.setContractId((contractIds[i - 1] + i * 2) + "");
                integratedQuote.setLiaisonName(liaisonName[i - 1]);
                integratedQuote.setLiaisonTel(liaisonTel[i - 1]);
                integratedQuote.setOrigCbpconferId((contractIds[i - 1] + i * 2) + "");
            } else {
                integratedQuote.setOrigCbpconferId((contractIds[i - 3] + i * 2) + "");
                integratedQuote.setContractId((contractIds[i - 3] + i * 2) + "");
                integratedQuote.setLiaisonName(liaisonName[i - 3]);
                integratedQuote.setLiaisonTel(liaisonTel[i - 3]);
//                if (Market.SHSE.value.equals(StockCodeUtils.getMarket(stockCode))) {
//                    integratedQuote.setEntrustProp(EntrustProp.INTENTION.value);
//                } else {
//                    integratedQuote.setEntrustProp(EntrustProp.SET_PRICE.value);
//                }
                integratedQuote.setEntrustProp(EntrustProp.SET_PRICE.value);
                if (i == 3) {
                    integratedQuote.setEntrustBs(EntrustBs.BUY.value);
                } else {
                    integratedQuote.setEntrustBs(EntrustBs.SELL.value);
                }
            }

            if (Objects.nonNull(tickBean)) {
                if (EntrustBs.BUY.value.equals(integratedQuote.getEntrustBs())) {
                    integratedQuote.setEntrustPrice(tickBean.getBidPrice().get(0));
                } else if (EntrustBs.SELL.value.equals(integratedQuote.getEntrustBs())) {
                    integratedQuote.setEntrustPrice(tickBean.getAskPrice().get(0));
                }
            }

            integratedQuotes.add(integratedQuote);
        }
        return integratedQuotes;
    }

    /**
     * 盘后大宗行情
     * 基金 588000.SH/159883.SZ                 0.74   0.73
     * 股票 600089.SH/002351.SZ                 12.21  12.31
     * 债券 113052.SH/JLZQ002.SZ/JLZQ003.SZ     99.95  100.05
     */
    public static List<IntegratedQuote> makeAfterBulkQuote(String stockCode, TickBean tickBean) {
        List<IntegratedQuote> integratedQuotes = new ArrayList<>();

        IntegratedQuote integratedQuote = new IntegratedQuote();
        integratedQuote.setStockCode(stockCode);
        integratedQuote.setStockName(stockCode);
        integratedQuote.setQuoteType(QuoteType.AFTER_BULK_QUOTE.value);
        integratedQuote.setEntrustVolume(300000);
        integratedQuote.setBuyRemainQuantity1(1000000);
        integratedQuote.setSellRemainQuantity1(2000000);

        integratedQuote.setEntrustPrice(25.11);
        integratedQuote.setFullPrice(25.33);
        integratedQuote.setWeightavgPrice(25.44);

//        List<String> funds = Lists.newArrayList("588000.SH", "159883.SZ");
//        List<String> stocks = Lists.newArrayList("600089.SH", "002351.SZ");
//        List<String> bonds = Lists.newArrayList("113052.SH", "JLZQ002.SZ", "JLZQ003.SZ");
//
//        if (funds.contains(stockCode)) {
//            integratedQuote.setEntrustPrice(0.74);
//            integratedQuote.setWeightavgPrice(0.73);
//        } else if (stocks.contains(stockCode)) {
//            integratedQuote.setEntrustPrice(12.21);
//            integratedQuote.setWeightavgPrice(12.31);
//        } else if (bonds.contains(stockCode)) {
//            integratedQuote.setEntrustPrice(99.95);
//            integratedQuote.setWeightavgPrice(100.05);
//        } else {
//            integratedQuote.setEntrustPrice(100.1);
//            integratedQuote.setFullPrice(102.7);
//            integratedQuote.setWeightavgPrice(101.4);
//        }

        if (Objects.nonNull(tickBean)) {
            double lastPrice = tickBean.getLastPrice();
            integratedQuote.setEntrustPrice(lastPrice);
            integratedQuote.setFullPrice(lastPrice + 0.2);
            integratedQuote.setWeightavgPrice(lastPrice + 0.1);
        }
        integratedQuote.setTradeBalance(integratedQuote.getEntrustVolume() * integratedQuote.getEntrustPrice());
        if (StockCodeUtils.getMarket(stockCode).equals(Market.SHSE.value)) {
            integratedQuote.setMarket(Market.SHSE.value);
        } else {
            integratedQuote.setMarket(Market.SZSE.value);
        }
        integratedQuotes.add(integratedQuote);
        return integratedQuotes;
    }

    public static List<IntegratedQuote> makeFixedIncomeQuote(String stockCode, TickBean tickBean) {
        List<IntegratedQuote> integratedQuotes = new ArrayList<>();
        if (StringUtil.isEmpty(stockCode)) {
            return integratedQuotes;
        }

        IntegratedQuote buy = new IntegratedQuote();
        buy.setOrderNo("11111");
        buy.setMarket(Market.SHSE.value);
        buy.setQuoteType(QuoteType.FIXED_INCOME_QUOTE.value);
        buy.setEntrustPrice(25.11);
        buy.setEntrustVolume(100000);
        buy.setContractId((contractIds[0] + 1) + "");
        buy.setStockCode(stockCode);
        buy.setStockName(stockCode);
        buy.setFullPrice(25.33);
        buy.setExpireIncomeRate(0.12);
        buy.setEntrustBs(EntrustBs.BUY.value);
        integratedQuotes.add(buy);

        IntegratedQuote sell = new IntegratedQuote();
        sell.setOrderNo("22222");
        sell.setMarket(Market.SHSE.value);
        sell.setQuoteType(QuoteType.FIXED_INCOME_QUOTE.value);
        sell.setEntrustPrice(25.22);
        sell.setEntrustVolume(200000);
        sell.setContractId((contractIds[0] + 2) + "");
        sell.setStockCode(stockCode);
        sell.setStockName(stockCode);
        sell.setFullPrice(25.44);
        sell.setExpireIncomeRate(0.21);
        sell.setEntrustBs(EntrustBs.SELL.value);
        integratedQuotes.add(sell);
        if (Objects.nonNull(tickBean)) {
            buy.setEntrustPrice(tickBean.getBidPrice().get(0));
            buy.setFullPrice(tickBean.getBidPrice().get(0) + 0.1);

            sell.setEntrustPrice(tickBean.getAskPrice().get(0));
            sell.setFullPrice(tickBean.getAskPrice().get(0) + 0.2);
        }

//        for (int i = 1; i < 3; i++) {
//            IntegratedQuote integratedQuote = new IntegratedQuote();
//            integratedQuote.setMarket(Market.SHSE.value);
//            integratedQuote.setQuoteType(QuoteType.FIXED_INCOME_QUOTE.value);
//            integratedQuote.setEntrustPrice(100.00);
//            integratedQuote.setContractId((contractIds[i - 1] + i) + "");
//            integratedQuote.setEntrustVolume(300000);
//            if (i == 1) {
//                integratedQuote.setStockCode("010107.SH");
//                integratedQuote.setStockName("21国债⑺");
//                integratedQuote.setFullPrice(101.00);
//                integratedQuote.setExpireIncomeRate(0.12);
//                integratedQuote.setEntrustBs(EntrustBs.BUY.value);
//            } else {
//                integratedQuote.setStockCode("101102.SH");
//                integratedQuote.setStockName("国债1102");
//                integratedQuote.setFullPrice(102.00);
//                integratedQuote.setExpireIncomeRate(0.21);
//                integratedQuote.setEntrustBs(EntrustBs.SELL.value);
//            }
//            integratedQuotes.add(integratedQuote);
//        }
        return integratedQuotes;
    }

    public static IntegratedQuote makeIntegratedEntrustQuote(IntegratedEntrust integratedEntrust) {
        IntegratedQuote integratedQuote = new IntegratedQuote();
        integratedQuote.setOrderNo(orderNo.incrementAndGet() + "");
        integratedQuote.setStockCode(integratedEntrust.getStockCode());
        integratedQuote.setStockName(integratedEntrust.getStockName());
        integratedQuote.setEntrustProp(integratedEntrust.getEntrustProp());
        integratedQuote.setEntrustBs(integratedEntrust.getEntrustBs());
        integratedQuote.setEntrustVolume((int) integratedEntrust.getEntrustVolume());
        if (FullPriceConst.FULL_PRICE.value.equals(integratedEntrust.getIsFullPrice())) {
            integratedQuote.setEntrustPrice(integratedEntrust.getFullPrice().doubleValue());
        } else {
            integratedQuote.setEntrustPrice(integratedEntrust.getEntrustPrice().doubleValue());
        }
        integratedQuote.setReportTime(simpleDateFormat.format(new Date(System.currentTimeMillis())));
        if (!Strings.isNullOrEmpty(integratedEntrust.getPropSeatNo()))
            integratedQuote.setPropSeatNo(integratedEntrust.getPropSeatNo());
        if (!Strings.isNullOrEmpty(integratedEntrust.getContractId()))
            integratedQuote.setContractId(integratedEntrust.getContractId());
        if (!Strings.isNullOrEmpty(integratedEntrust.getLiaisonName()))
            integratedQuote.setLiaisonName(integratedEntrust.getLiaisonName());
        if (!Strings.isNullOrEmpty(integratedEntrust.getLiaisonTel()))
            integratedQuote.setLiaisonTel(integratedEntrust.getLiaisonTel());
        if (integratedEntrust.getEntrustProp().equals(EntrustProp.SET_PRICE.value)) {
            integratedQuote.setPropSeatNo("062700");
            integratedQuote.setContractId(123 + orderNo.incrementAndGet() + "");
        }
        return integratedQuote;
    }
    
    public static List<NegotiateQuoteReceiveBean> makeNegotiateQuoteReceive(String stockCode, TickBean tickBean) {
        List<NegotiateQuoteReceiveBean> negotiateQuoteReceiveBeans = new ArrayList<>();
        if (StringUtil.isEmpty(stockCode)) {
            return negotiateQuoteReceiveBeans;
        }
        //模拟买行情
        NegotiateQuoteReceiveBean buy = new NegotiateQuoteReceiveBean();
        buy.setBs(EntrustBs.BUY.value);
        buy.setRequestTime(System.currentTimeMillis());
        buy.setRequestStatus(1);
        buy.setStockCode(stockCode);
        buy.setStockName("30债券39");
        buy.setAmount(10000);
        buy.setFullPrice(BigDecimal.valueOf(100));
        buy.setEntrustPrice(BigDecimal.valueOf(99));
        buy.setEntrustBalance(BigDecimal.valueOf(100));
        buy.setMemberId("167");
        buy.setTraderCode("Z213610");
        buy.setInvestorId("131");
        buy.setInvestorType("2");
        buy.setCpMemberId("168");
        buy.setCpTraderCode("Z213611");
        buy.setCpInvestorId("132");
        buy.setCpInvestorType("2");
        buy.setConfirmId("confirmId");
        buy.setPositionStr("positionStr");
        buy.setRemark("模拟买名称");
        negotiateQuoteReceiveBeans.add(buy);
        
        //模拟卖行情
        NegotiateQuoteReceiveBean sell = new NegotiateQuoteReceiveBean();
        sell.setBs(EntrustBs.SELL.value);
        sell.setRequestTime(System.currentTimeMillis());
        sell.setRequestStatus(1);
        sell.setStockCode(stockCode);
        sell.setStockName("30债券39");
        sell.setAmount(20000);
        sell.setFullPrice(BigDecimal.valueOf(101));
        sell.setEntrustPrice(BigDecimal.valueOf(100));
        sell.setEntrustBalance(BigDecimal.valueOf(101));
        sell.setMemberId("167");
        sell.setTraderCode("Z213610");
        sell.setInvestorId("131");
        sell.setInvestorType("2");
        sell.setCpMemberId("168");
        sell.setCpTraderCode("Z213611");
        sell.setCpInvestorId("132");
        sell.setCpInvestorType("2");
        sell.setConfirmId("111");
        sell.setPositionStr("positionStr");
        sell.setRemark("模拟卖名称");
        negotiateQuoteReceiveBeans.add(sell);

        return negotiateQuoteReceiveBeans;
    }
}
