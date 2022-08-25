package com.joyintech.exchange.service.quote.sz.binary.bean;


import com.alibaba.fastjson.JSON;
import com.joyintech.exchange.service.quote.sz.binary.enums.SZSEBinaryMDEntryTypeEnum;

import java.util.LinkedList;
import java.util.List;

/**
 * 300111 集中竞价交易行情
 */

public class SZSEBinaryLevel1MarketSnapshot {
    /**
     * 数据生成时间
     */
    protected long origTime;
    /**
     * 频道代码
     */
    protected String channelNo;
    /**
     * 行情类别
     */
    protected String mdStreamId;
    /**
     * 证券代码
     */
    protected String securityId;
    /**
     * 证券代码源
     */
    protected String securityIdSource;
    /**
     * 交易状态
     */
    protected String tradePhaseCode;
    /**
     * 昨收价 , 转成实际价格需要 除以 10000.
     */
    protected double prevClosePx;
    /**
     * 成交笔数
     */
    protected long numberTrades;
    /**
     * 成交总量
     */
    protected long totalVolumeTrade;
    /**
     * 成交总金额
     */
    protected long totalValueTrade;
    /**
     * 行情条目数量
     */
    private int noMDEntries;
    /**
     * 行情条目列表
     */
    private String list;

    private List<Double> offerPrices = new LinkedList<>();

    private List<Double> bidPrices = new LinkedList<>();

    private List<Long> offerVolumes = new LinkedList<>();

    private List<Long> bidVolumes = new LinkedList<>();
    /**
     * 最新价
     */
    private double lastPx;
    /**
     * 开盘价
     */
    private double openPx;
    /**
     * 最高价
     */
    private double highPx;
    /**
     * 最低价
     */
    private double lowPx;
    /**
     * 加权平均价
     */
    private double avgPx;
    /**
     * 升跌1
     */
    private double udPx1;
    /**
     * 升跌2
     */
    private double udPx2;
    /**
     * 买入汇总
     */
    private double summaryBuyPx;
    /**
     * 卖出汇总
     */
    private double summarySellPx;
    /**
     * 股票市盈率1
     */
    private double stockPERatio1;

    /**
     * 股票市盈率2
     */
    private double stockPERatio2;
    /**
     * 基金 T-1 日净值
     */
    private double fundNetWorth;
    /**
     * 基金实时参考净值
     */
    private double fundRtReferenceNetWorth;
    /**
     * 权证溢价率
     */
    private double shareWarrantPremiumRate;
    /**
     * 涨停价
     */
    private double highLimitPx;
    /**
     * 跌停价
     */
    private double lowLimitPx;
    /**
     * 合约持仓量
     */
    private double contractPosition;
    /**
     * 加权平均价涨跌BP
     */
    private double weightedAverageBP;
    /**
     * 昨收盘加权平均价
     */
    private double weightedAverageYestadyClosePx;

    /**
     * 结算价格
     */
    private double clearingPrice;

    private long numberOfOrders;
    /**
     * 委托数量
     */
    private int orderQty;

    public void addBidPx(double px) {
        bidPrices.add(px);
    }

    public void addBidPx(double px, int level) {
        bidPrices.add(level, px);
    }

    public void addBidVolume(long volume) {
        bidVolumes.add(volume);
    }

    public void addBidVolume(long volume, int level) {
        bidVolumes.add(level, volume);
    }

    public void addOfferPx(double px) {
        offerPrices.add(px);
    }

    public void addOfferPx(double px, int level) {
        offerPrices.add(level, px);
    }

    public void addOfferVolume(long volume) {
        offerVolumes.add(volume);
    }

    public void addOfferVolume(long volume, int level) {
        offerVolumes.add(level, volume);
    }

    public long getTotalVolumeTrade() {
        return totalVolumeTrade / 100;
    }

    public void setTotalVolumeTrade(long totalVolumeTrade) {
        this.totalVolumeTrade = totalVolumeTrade;
    }

    public long getTotalValueTrade() {
        return totalValueTrade / 10000;
    }

    public void setTotalValueTrade(long totalValueTrade) {
        this.totalValueTrade = totalValueTrade;
    }

    protected void parseMdEntry() {
        //重复组, 代表有多少个档位
        Integer noMDEntries = this.getNoMDEntries();

        String list = this.getList();
        System.out.println(list);
        List<SZSEMdEntryBean> mdEntryBeanList =
                JSON.parseArray(list, SZSEMdEntryBean.class);
        if (noMDEntries == mdEntryBeanList.size()) {
            for (int i = 0; i < mdEntryBeanList.size(); i++) {
                SZSEMdEntryBean mdEntryBean = mdEntryBeanList.get(i);
                SZSEBinaryMDEntryTypeEnum msgTypeEnum = SZSEBinaryMDEntryTypeEnum.lookup(mdEntryBean.getMdEntryType())
                        .orElseThrow(() -> new IllegalArgumentException("unknown msg type: " + mdEntryBean.getMdEntryType()));
                setPriceDetail(msgTypeEnum, mdEntryBean.getMdPriceLevel() - 1, mdEntryBean.getMdEntrySize(), mdEntryBean.getMdEntryPx());
            }
        }
    }

    private void setPriceDetail(SZSEBinaryMDEntryTypeEnum priceType, int level, long volume, double price) {
        if (priceType == null) {
            return;
        }
        double px = price / 1000000.0;
        switch (priceType) {
            case BUY:
                addBidPx(px, level);
                addBidVolume(volume / 100, level);
                break;
            case SELL:
                addOfferPx(px, level);
                addOfferVolume(volume / 100, level);
                break;
            case LAST:
                this.lastPx = px;
                break;
            case OPEN:
                this.openPx = px;
                break;
            case HIGH:
                this.highPx = px;
                break;
            case LOW:
                this.lowPx = px;
                break;
            case AVG:
                this.avgPx = px;
                break;
            case HIGH_LIMIT:
                this.highLimitPx = px;
                break;
            case LOW_LIMIT:
                this.lowLimitPx = px;
                break;
            case UD1:
                this.udPx1 = px;
                break;
            case UD2:
                this.udPx2 = px;
                break;
            case SUMMARY_BUY:
                this.summaryBuyPx = px;
                break;
            case SUMMARY_SELL:
                this.summarySellPx = px;
                break;
            case STOCK_PE1:
                this.stockPERatio1 = px;
                break;
            case STOCK_PE2:
                this.stockPERatio2 = px;
                break;
            case FUND_DNW:
                this.fundNetWorth = px;
                break;
            case FUND_RT_RNW:
                this.fundRtReferenceNetWorth = px;
                break;
            case WPR:
                this.shareWarrantPremiumRate = px;
                break;
            case CONTRACT_POSITION:
                this.contractPosition = px;
                break;
            case WEIGHTED_AVERAGE_BP:
                this.weightedAverageBP = px;
                break;
            case WEIGHTED_AVERAGE_YESTDAY_CLOSE:
                this.weightedAverageYestadyClosePx = px;
                break;
            default:
                break;
        }
    }

    public List<Double> getOfferPrices() {
        return offerPrices;
    }

    public void setOfferPrices(List<Double> offerPrices) {
        this.offerPrices = offerPrices;
    }

    public List<Double> getBidPrices() {
        return bidPrices;
    }

    public void setBidPrices(List<Double> bidPrices) {
        this.bidPrices = bidPrices;
    }

    public List<Long> getOfferVolumes() {
        return offerVolumes;
    }

    public void setOfferVolumes(List<Long> offerVolumes) {
        this.offerVolumes = offerVolumes;
    }

    public List<Long> getBidVolumes() {
        return bidVolumes;
    }

    public void setBidVolumes(List<Long> bidVolumes) {
        this.bidVolumes = bidVolumes;
    }

    public double getLastPx() {
        return lastPx;
    }

    public void setLastPx(double lastPx) {
        this.lastPx = lastPx;
    }

    public double getOpenPx() {
        return openPx;
    }

    public void setOpenPx(double openPx) {
        this.openPx = openPx;
    }

    public double getHighPx() {
        return highPx;
    }

    public void setHighPx(double highPx) {
        this.highPx = highPx;
    }

    public double getLowPx() {
        return lowPx;
    }

    public void setLowPx(double lowPx) {
        this.lowPx = lowPx;
    }

    public double getUdPx1() {
        return udPx1;
    }

    public void setUdPx1(double udPx1) {
        this.udPx1 = udPx1;
    }

    public double getUdPx2() {
        return udPx2;
    }

    public void setUdPx2(double udPx2) {
        this.udPx2 = udPx2;
    }

    public double getSummaryBuyPx() {
        return summaryBuyPx;
    }

    public void setSummaryBuyPx(double summaryBuyPx) {
        this.summaryBuyPx = summaryBuyPx;
    }

    public double getSummarySellPx() {
        return summarySellPx;
    }

    public void setSummarySellPx(double summarySellPx) {
        this.summarySellPx = summarySellPx;
    }

    public double getStockPERatio1() {
        return stockPERatio1;
    }

    public void setStockPERatio1(double stockPERatio1) {
        this.stockPERatio1 = stockPERatio1;
    }

    public double getStockPERatio2() {
        return stockPERatio2;
    }

    public void setStockPERatio2(double stockPERatio2) {
        this.stockPERatio2 = stockPERatio2;
    }

    public double getFundNetWorth() {
        return fundNetWorth;
    }

    public void setFundNetWorth(double fundNetWorth) {
        this.fundNetWorth = fundNetWorth;
    }

    public double getFundRtReferenceNetWorth() {
        return fundRtReferenceNetWorth;
    }

    public void setFundRtReferenceNetWorth(double fundRtReferenceNetWorth) {
        this.fundRtReferenceNetWorth = fundRtReferenceNetWorth;
    }

    public double getShareWarrantPremiumRate() {
        return shareWarrantPremiumRate;
    }

    public void setShareWarrantPremiumRate(double shareWarrantPremiumRate) {
        this.shareWarrantPremiumRate = shareWarrantPremiumRate;
    }

    public double getHighLimitPx() {
        return highLimitPx;
    }

    public void setHighLimitPx(double highLimitPx) {
        this.highLimitPx = highLimitPx;
    }

    public double getLowLimitPx() {
        return lowLimitPx;
    }

    public void setLowLimitPx(double lowLimitPx) {
        this.lowLimitPx = lowLimitPx;
    }

    public double getContractPosition() {
        return contractPosition;
    }

    public void setContractPosition(double contractPosition) {
        this.contractPosition = contractPosition;
    }

    public double getWeightedAverageBP() {
        return weightedAverageBP;
    }

    public void setWeightedAverageBP(double weightedAverageBP) {
        this.weightedAverageBP = weightedAverageBP;
    }

    public double getWeightedAverageYestadyClosePx() {
        return weightedAverageYestadyClosePx;
    }

    public void setWeightedAverageYestadyClosePx(double weightedAverageYestadyClosePx) {
        this.weightedAverageYestadyClosePx = weightedAverageYestadyClosePx;
    }

    public long getNumberOfOrders() {
        return numberOfOrders;
    }

    public void setNumberOfOrders(long numberOfOrders) {
        this.numberOfOrders = numberOfOrders;
    }

    public int getOrderQty() {
        return orderQty;
    }

    public void setOrderQty(int orderQty) {
        this.orderQty = orderQty;
    }

    public double getClearingPrice() {
        return clearingPrice;
    }

    public void setClearingPrice(double clearingPrice) {
        this.clearingPrice = clearingPrice;
    }

    public long getOrigTime() {
        return origTime;
    }

    public void setOrigTime(long origTime) {
        this.origTime = origTime;
    }

    public String getChannelNo() {
        return channelNo;
    }

    public void setChannelNo(String channelNo) {
        this.channelNo = channelNo;
    }

    public String getMdStreamId() {
        return mdStreamId;
    }

    public void setMdStreamId(String mdStreamId) {
        this.mdStreamId = mdStreamId;
    }

    public String getSecurityId() {
        return securityId;
    }

    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }

    public String getSecurityIdSource() {
        return securityIdSource;
    }

    public void setSecurityIdSource(String securityIdSource) {
        this.securityIdSource = securityIdSource;
    }

    public String getTradePhaseCode() {
        return tradePhaseCode;
    }

    public void setTradePhaseCode(String tradePhaseCode) {
        this.tradePhaseCode = tradePhaseCode;
    }

    public double getPrevClosePx() {
        return prevClosePx;
    }

    public void setPrevClosePx(double prevClosePx) {
        this.prevClosePx = prevClosePx;
    }

    public long getNumberTrades() {
        return numberTrades;
    }

    public void setNumberTrades(long numberTrades) {
        this.numberTrades = numberTrades;
    }

    public double getAvgPx() {
        return avgPx;
    }

    public void setAvgPx(double avgPx) {
        this.avgPx = avgPx;
    }

    public int getNoMDEntries() {
        return noMDEntries;
    }

    public void setNoMDEntries(int noMDEntries) {
        this.noMDEntries = noMDEntries;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
        this.parseMdEntry();
    }
}
