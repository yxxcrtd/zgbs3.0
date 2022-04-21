package com.joyintech.tams.exchange.common.constant;

import com.joyintech.tams.exchange.common.enums.IEnum;

public enum FullPriceConst implements IEnum<String> {

    FULL_PRICE("Y", "全价"),

    NET_PRICE("N", "非全价");

    FullPriceConst(String value, String name) {
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
