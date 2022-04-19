package com.joyintech.tams.exchange.common.constant;

import java.time.LocalTime;

/**
 * create By MAXX on 2020/12/15
 */
public enum TradeTime {
    THE_OPENING_BEGIN("开盘集合开始", "09:15"),
    THE_OPENING_END("开盘集合结束", "09:25"),
    THE_CONTINUOUS_AUCTION_BEGIN1("连续竞价开始1", "09:30"),
    THE_CONTINUOUS_AUCTION_END1("连续竞价结束1", "11:30"),
    THE_CONTINUOUS_AUCTION_BEGIN2("连续竞价开始2", "13:00"),
    THE_CONTINUOUS_AUCTION_END2("连续竞价结束2", "14:57"),
    THE_CLOSED_BEGIN("收盘集合开始", "14:57"),
    THE_CLOSED_END("收盘集合结束", "15:00");

    public final String value;
    public final String name;

    TradeTime(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public LocalTime getTime() {
        return LocalTime.parse(this.value);
    }
}
