package com.joyintech.tams.exchange.common.enums.exchange;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * 净价/全价枚举
 * @author swwan
 * @date 2022/4/26 - 16:33
 */
public enum NetOrFullPriceEnum implements IEnum<String> {

    NET_PRICE("Y", "净价"),
    FULL_PRICE("N", "全价"),
            ;

    public final String code;
    public final String description;

    NetOrFullPriceEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
