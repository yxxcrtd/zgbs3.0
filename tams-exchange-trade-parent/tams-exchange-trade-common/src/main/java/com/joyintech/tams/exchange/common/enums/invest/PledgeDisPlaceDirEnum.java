package com.joyintech.tams.exchange.common.enums.invest;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * 质押券置换方向
 *
 * @author swwan
 * @date 2022/4/26 - 17:05
 */
public enum PledgeDisPlaceDirEnum implements IEnum<String> {

    CURRENT("00", "当前"),
    SWAP_IN("01", "换入"),
    SWAP_OUT("02", "换出"),
    ;

    public final String code;
    public final String description;

    PledgeDisPlaceDirEnum(String code, String description) {
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
