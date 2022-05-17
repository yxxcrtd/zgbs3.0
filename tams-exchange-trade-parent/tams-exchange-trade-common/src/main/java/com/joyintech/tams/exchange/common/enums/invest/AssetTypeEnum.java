package com.joyintech.tams.exchange.common.enums.invest;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * 投管资产类型
 *
 * @author swwan
 * @date 2020/12/2
 */
public enum AssetTypeEnum implements IEnum<String> {

    BOND("债券", "F01"),
    FUND("基金", "F02"),
    PLEDGE_REPURCHASE("质押式回购", "F07"),
    STOCK("股票", "F11"),
    SHARE_OPTION("期权", "F18"),
    FUTURES("期货", "F20"),
    CERTIFICATE("信用保护凭证", "F36"),
    ;

    private final String code;
    private final String description;

    AssetTypeEnum(String description, String code) {
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
