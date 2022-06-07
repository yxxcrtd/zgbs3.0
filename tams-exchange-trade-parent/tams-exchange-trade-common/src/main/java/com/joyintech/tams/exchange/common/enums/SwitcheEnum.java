package com.joyintech.tams.exchange.common.enums;

/**
 * @author swwan
 * @date 2022/4/13 - 17:03
 */
public enum SwitcheEnum implements IEnum<String> {
    OPEN("Y", "开启"),
    CLOSE("N", "关闭");

    private final String code;
    private final String description;

    SwitcheEnum(String code, String description) {
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
