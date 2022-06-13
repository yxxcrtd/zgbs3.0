package com.joyintech.tams.exchange.common.enums.exchange;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * 担保/非担保枚举
 *
 * @author swwan
 * @date 2022/4/26 - 16:33
 */
public enum GuaranteedEnum implements IEnum<String> {

    NON_GUARANTEED("Y", "非担保"),
    GUARANTEED("N", "担保"),
    ;

    private final String code;
    private final String description;

    GuaranteedEnum(String code, String description) {
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
