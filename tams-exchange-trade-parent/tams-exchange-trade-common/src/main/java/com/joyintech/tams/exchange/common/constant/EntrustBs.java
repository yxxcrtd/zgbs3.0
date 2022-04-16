package com.joyintech.tams.exchange.common.constant;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * 买卖方向枚举
 */
public enum EntrustBs implements IEnum<String> {
    /**
     * 买入
     */
    BUY("买入", "1"),
    /**
     * 卖出
     */
    SELL("卖出", "2");
    public final String value;
    public final String name;

    EntrustBs(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 买卖方向是否为买入
     *
     * @param bs 买卖方向
     * @return true 买入； false 卖出
     */
    public static boolean isBuy(String bs) {
        return EntrustBs.BUY.value.equals(bs);
    }

    /**
     * 买卖方向是否为卖出
     *
     * @param bs 买卖方向
     * @return true 卖出； false 买入
     */
    public static boolean isSell(String bs) {
        return EntrustBs.SELL.value.equals(bs);
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
