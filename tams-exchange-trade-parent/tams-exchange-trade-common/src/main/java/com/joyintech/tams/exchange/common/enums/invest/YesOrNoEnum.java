package com.joyintech.tams.exchange.common.enums.invest;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * 是否枚举
 *
 * @author swwan
 * @date 2022/4/24 - 22:28
 * @see com.joyintech.tams.constant.DictCode#是否
 */
public enum YesOrNoEnum implements IEnum<String> {

    YES("Y", "是"),
    NO("N", "否"),
    ;

    public final String code;
    public final String description;

    YesOrNoEnum(String code, String description) {
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
