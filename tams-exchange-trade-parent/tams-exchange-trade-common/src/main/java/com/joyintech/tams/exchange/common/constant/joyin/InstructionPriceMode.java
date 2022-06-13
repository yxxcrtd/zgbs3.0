package com.joyintech.tams.exchange.common.constant.joyin;

import com.joyintech.tams.exchange.common.enums.IEnum;

public enum InstructionPriceMode implements IEnum<String> {
    SPECIFIED("指定价", "PRM01"),
    FIXED("限价", "PRM02"),
    MARKET("市价", "PRM03"),
    LARGE_AFTER_CLOSING_PRICE("收盘价固定价格申报", "PRM10"),
    ;


    public final String value;
    public final String name;

    InstructionPriceMode(String name, String value) {
        this.name = name;
        this.value = value;
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
