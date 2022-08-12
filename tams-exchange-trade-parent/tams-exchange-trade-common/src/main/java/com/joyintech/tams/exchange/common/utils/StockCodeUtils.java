package com.joyintech.tams.exchange.common.utils;

import com.google.common.base.Strings;

public class StockCodeUtils {

    public static String getMarket(String stockCode) {
        if (Strings.isNullOrEmpty(stockCode) || !stockCode.contains("."))
            return "";
        return stockCode.split("\\.")[1];
    }

    public static String getStockCode(String stockCode) {
        if (Strings.isNullOrEmpty(stockCode))
            return "";
        return stockCode.split("\\.")[0];
    }


    public static String[] getMarketAndStockCode(String stockCode) {
        return stockCode.split("\\.");
    }

    public static String formatStockCode(String stockCode, String market) {
        if (!Strings.isNullOrEmpty(market)) {
            if (stockCode.contains(market)) {
                return stockCode;
            } else {
                return stockCode + "." + market;
            }
        }
        return stockCode;
    }
}
