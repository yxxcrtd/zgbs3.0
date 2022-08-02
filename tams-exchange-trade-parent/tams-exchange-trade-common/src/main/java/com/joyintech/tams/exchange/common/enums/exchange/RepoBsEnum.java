package com.joyintech.tams.exchange.common.enums.exchange;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * 协议回购方向枚举
 *
 * @author swwan
 * @date 2022/6/6 - 16:28
 */
public enum RepoBsEnum implements IEnum<String> {

    /**
     * 回购方向-正回购
     */
    正回购("03", "正回购"),
    /**
     * 回购方向-逆回购
     */
    逆回购("04", "逆回购"),
    ;

    private final String code;
    private final String description;

    RepoBsEnum(String code, String description) {
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
