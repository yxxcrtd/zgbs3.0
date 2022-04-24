package com.joyintech.tams.exchange.common.constant;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.enums.IEnum;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public enum Market implements IEnum<String> {

    SZSE("深圳证券交易所", "SZ", "04"),
    SHSE("上海证券交易所", "SH", "03"),

    SHFE("上海期货交易所", "SHFE", ""),
    DCE("大连期货交易所", "DCE", ""),
    CZCE("郑州期货交易所", "CZCE", ""),
    CFFEX("中国金融期货交易所", "CFFEX", "12"),
    INE("上海能源期货交易所", "INE", "");
    public final String value;//交易系统市场
    public final String investValue;//投管市场
    public final String name;

    Market(String name, String value, String investValue) {
        this.name = name;
        this.value = value;
        this.investValue = investValue;
    }

    private static Set<String> futuresMarket = new HashSet<>();

    private static Map<String, String> marketMap = new HashMap<>();

    static {
        futuresMarket.add(SHFE.value);
        futuresMarket.add(DCE.value);
        futuresMarket.add(CZCE.value);
        futuresMarket.add(CFFEX.value);
        futuresMarket.add(INE.value);

        marketMap.put(SZSE.value, SZSE.name);
        marketMap.put(SHSE.value, SHSE.name);
        marketMap.put(SHFE.value, SHFE.name);
        marketMap.put(DCE.value, DCE.name);
        marketMap.put(CZCE.value, CZCE.name);
        marketMap.put(CFFEX.value, CFFEX.name);
        marketMap.put(INE.value, INE.name);
    }

    // 判断是否为为期货
    public static boolean isFutures(String market) {
        if (futuresMarket.contains(market)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断是否深交所
     */
    public static boolean isSzMarket(String market) {
        return StringUtil.equals(Market.SZSE.value, market);
    }

    /**
     * 判断是否上交所
     */
    public static boolean isShMarket(String market) {
        return StringUtil.equals(Market.SHSE.value, market);
    }

    public static String getMarketName(String market) {
        String name = marketMap.get(market);
        return StringUtil.isNotEmpty(name) ? name : market;
    }

    @Override
    public String getCode() {
        return value;
    }

    @Override
    public String getDescription() {
        return name;
    }
}
