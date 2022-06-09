package com.joyintech.tams.exchange.common.enums.invest;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * 价格模式枚举
 *
 * @author swwan
 * @date 2022/4/24 - 21:43
 * @see com.joyintech.tams.constant.DictCode#价格模式
 */
@SuppressWarnings("all")
public enum InvestPriceModeEnum implements IEnum<String> {

    FIXED("PRM01", "指定价格"),
    LIMIT("PRM02", "限价指令"),
    MARKET("PRM03", "市价指令"),
    WEIGHTED("PRM04", "加权价格+BP"),
    UNKNOWN("PRM05", "不指定"),
    INTENT("PRM06", "意向申报"),
    PRICING("PRM07", "定价申报"),
    CLICK_TRADE("PRM08", "点击成交申报"),
    TRADE("PRM09", "成交申报"),
    CLOSING_FIXED("PRM10", "收盘价固定价格"),
    AVERAGE_FIXED("PRM11", "日均价固定价格申报"),
    ;

    public String code;
    public String description;

    InvestPriceModeEnum(String code, String description) {
        this.description = description;
        this.code = code;
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