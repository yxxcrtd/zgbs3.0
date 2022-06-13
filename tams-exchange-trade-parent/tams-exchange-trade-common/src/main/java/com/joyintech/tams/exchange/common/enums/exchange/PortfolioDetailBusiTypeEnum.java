package com.joyintech.tams.exchange.common.enums.exchange;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * 多组合分配业务类型
 *
 * @author swwan
 * @date 2022/5/11 - 9:08
 */
public enum PortfolioDetailBusiTypeEnum implements IEnum<String> {

    ORDER("1", "下单"),
    CANCEL("2", "撤单"),
    TRADE("3", "成交"),
    ;

    private final String code;
    private final String description;

    PortfolioDetailBusiTypeEnum(String code, String description) {
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
