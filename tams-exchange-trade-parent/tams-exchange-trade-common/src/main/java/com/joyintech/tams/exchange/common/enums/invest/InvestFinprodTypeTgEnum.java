package com.joyintech.tams.exchange.common.enums.invest;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * 金融产品类型_投管用枚举
 *
 * @author swwan
 * @date 2022/4/24 - 21:43
 * @see com.joyintech.tams.constant.DictCode#金融产品类型_投管用
 */
@SuppressWarnings("all")
public enum InvestFinprodTypeTgEnum implements IEnum<String> {

    BOND("F01", "债券"),
    BOND_PLEDGED_REPURCHASE("F02", "债券质押式正回购"),
    BOND_PLEDGED_REVERSE_REPURCHASE("F03", "债券质押式逆回购"),
    FUND("F06", "基金"),
    STOCK("F10", "股票"),
    ;

    public String code;
    public String description;

    InvestFinprodTypeTgEnum(String description, String code) {
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