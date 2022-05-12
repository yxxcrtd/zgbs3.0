package com.joyintech.tams.exchange.common.enums.exchange;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * 多组合分配业务状态
 *
 * @author swwan
 * @date 2022/5/11 - 9:08
 */
public enum PortfolioDetailBusiStatusEnum implements IEnum<String> {

    INITIAL("1", "初始"),
    CONFIRM("2", "确认"),
    ;

    private final String code;
    private final String description;

    PortfolioDetailBusiStatusEnum(String code, String description) {
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
