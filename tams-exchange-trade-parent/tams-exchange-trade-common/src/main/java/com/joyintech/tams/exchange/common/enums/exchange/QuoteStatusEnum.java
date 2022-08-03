package com.joyintech.tams.exchange.common.enums.exchange;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * 协议回购行情状态枚举
 *
 * @author swwan
 * @date 2022/6/6 - 16:28
 */
public enum QuoteStatusEnum implements IEnum<Integer> {

    TO_BE_CONFIRMED(1, "待确认"),
    REPLIED(2, "已回复"),
    REVOKED(3, "已撤销"),
    DEAL_DONE(4, "已成交"),
    ;

    private final Integer code;
    private final String description;

    QuoteStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
