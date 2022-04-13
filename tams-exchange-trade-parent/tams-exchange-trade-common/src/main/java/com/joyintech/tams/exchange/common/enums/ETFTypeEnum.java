package com.joyintech.tams.exchange.common.enums;

public enum ETFTypeEnum implements ICodeEnum<String>{
    ETF_SINGLE_MARKET("01"),
    ETF_CROSS_MARKET("02"),
    ETF_CROSS_BORDER("03"),
    ETF_BOND("04"),
    ETF_GOLD("05"),
    ETF_CURRENCY("06");

    public final String code;

    ETFTypeEnum(String code) {
        this.code = code;
    }

    @Override
    public String getCode() {
        return code;
    }
}
