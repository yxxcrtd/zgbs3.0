package com.joyintech.tams.exchange.common.constant;

import com.joyintech.tams.exchange.common.enums.IEnum;

public enum ActionIn implements IEnum<Integer> {
    PUBLIC_QUOTE("公开行情查询", 0),
    NON_PUBLIC_QUOTE("非公开行情查询", 1),
    RECEIVE_QUOTE("我收到的行情查询", 2),
    ;

    public final int value;
    public final String description;

    ActionIn(String description, int value) {
        this.description = description;
        this.value = value;
    }

    @Override
    public Integer getCode() {
        return value;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
