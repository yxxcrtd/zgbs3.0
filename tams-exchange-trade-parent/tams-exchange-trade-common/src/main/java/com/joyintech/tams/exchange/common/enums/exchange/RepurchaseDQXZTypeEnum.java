package com.joyintech.tams.exchange.common.enums.exchange;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * 协议回购到期续做类型枚举
 *
 * @author swwan
 * @date 2022/6/6 - 16:28
 */
public enum RepurchaseDQXZTypeEnum implements IEnum<String> {

    变更对手方("0", "变更对手方"),
    不变更对手方("1", "不变更对手方"),
    ;

    private final String code;
    private final String description;

    RepurchaseDQXZTypeEnum(String code, String description) {
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
