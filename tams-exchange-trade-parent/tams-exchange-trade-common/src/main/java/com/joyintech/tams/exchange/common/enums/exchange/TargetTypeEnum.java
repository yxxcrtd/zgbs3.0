package com.joyintech.tams.exchange.common.enums.exchange;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * 目标类型枚举
 * 指令数量单还是金额单
 *
 * @author swwan
 * @date 2022/4/24 - 22:28
 */
public enum TargetTypeEnum implements IEnum<String> {

    VOLUME("01", "数量"),
    AMT("02", "金额"),
    ;

    private final String code;
    private final String description;

    TargetTypeEnum(String code, String description) {
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
