package com.joyintech.exchange.service.quote.sz.binary.enums;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public enum SZSEBinaryMDEntryTypeEnum {
    /**
     * 未知
     */
    UNKNOWN(""),

    /**
     * 买入
     */
    BUY("0"),
    /**
     * 卖出
     */
    SELL("1"),
    /**
     * 最新价
     */
    LAST("2"),
    /**
     * 开盘价
     */
    OPEN("4"),
    /**
     * 收盘价
     */
    CLOSE("5"),
    /**
     * 最高价
     */
    HIGH("7"),
    /**
     * 最低价
     */
    LOW("8"),
    /**
     * 加权平均价
     */
    AVG("9"),
    /**
     * 升跌1
     */
    UD1("x1"),
    /**
     * 升跌2
     */
    UD2("x2"),
    /**
     * 买入汇总
     */
    SUMMARY_BUY("x3"),
    /**
     * 卖出汇总
     */
    SUMMARY_SELL("x4"),
    /**
     * 股票市盈率1
     */
    STOCK_PE1("x5"),
    /**
     * 股票市盈率2
     */
    STOCK_PE2("x6"),
    /**
     * 基金 T-1 日净值
     */
    FUND_DNW("x7"),
    /**
     * 基金实时参考净值（包括 ETF的 IOPV）
     */
    FUND_RT_RNW("x8"),
    /**
     * 权证溢价率
     */
    WPR("x9"),
    /**
     * 涨停价
     */
    HIGH_LIMIT("xe"),
    /**
     * 跌停价
     */
    LOW_LIMIT("xf"),
    /**
     * 合约持仓量
     */
    CONTRACT_POSITION("xg"),
    /**
     * 加权平均价涨跌BP
     */
    WEIGHTED_AVERAGE_BP("xj"),
    /**
     * 昨收盘加权平均价
     */
    WEIGHTED_AVERAGE_YESTDAY_CLOSE("xk"),
    /**
     * 匹配成交最近价
     */
    MATCH_THE_LATEST_PRICE("xv");

    private static final Map<String, SZSEBinaryMDEntryTypeEnum> mapping;

    static {
        mapping = new HashMap<>(16);
        Arrays.stream(SZSEBinaryMDEntryTypeEnum.values()).forEach(priceType -> mapping.put(priceType.getPriceType(), priceType));
    }

    private final String priceType;

    SZSEBinaryMDEntryTypeEnum(String priceType) {
        this.priceType = priceType;
    }

    public static Optional<SZSEBinaryMDEntryTypeEnum> lookup(String priceType) {
        return Optional.ofNullable(mapping.get(priceType));
    }

    public String getPriceType() {
        return priceType;
    }
}
