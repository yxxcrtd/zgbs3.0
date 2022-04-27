package com.joyintech.tams.exchange.common.constant;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * create By MAXX on 2020/10/22
 */
public enum NetPriceFlag implements IEnum<String> {
    IS_NET_PRICE_FLAG("Y", "净价交易"),
    NOT_NET_PRICE_FLAG("N", "全价交易");

    NetPriceFlag(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public final String value;
    public final String name;


    @Override
    public String getCode() {
        return value;
    }

    @Override
    public String getDescription() {
        return name;
    }
}
