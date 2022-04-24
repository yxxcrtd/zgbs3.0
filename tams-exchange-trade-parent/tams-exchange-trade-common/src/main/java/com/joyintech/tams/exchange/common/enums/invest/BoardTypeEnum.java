package com.joyintech.tams.exchange.common.enums.invest;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * 上市版枚举
 *
 * @author swwan
 * @date 2022/4/24 - 21:59
 * @see com.joyintech.tams.constant.DictCode#上市板
 */
@SuppressWarnings("all")
public enum BoardTypeEnum implements IEnum<String> {

    ZB("BD01", "主板"),
    ZXQYB("BD02", "中小企业板"),
    KCB("BD03", "科创板"),
    CYB("BD04", "创业板"),
    ;

    public final String code;
    public final String description;

    BoardTypeEnum(String code, String description) {
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
