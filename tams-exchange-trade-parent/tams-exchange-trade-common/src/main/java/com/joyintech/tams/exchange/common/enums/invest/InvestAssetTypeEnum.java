package com.joyintech.tams.exchange.common.enums.invest;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * 平安资产分类
 *
 *  * @see com.joyintech.tams.constant.DictCode#平安资产类型
 */
public enum InvestAssetTypeEnum implements IEnum<String> {

    BOND("债券", "F01"),
    FUND("基金", "F02"),
    PLEDGE_REPURCHASE("质押式回购", "F07"),
    STOCK("股票", "F11"),
    SHARE_OPTION("期权", "F18"),
    FUTURES("期货", "F20"),
    CREDIT_PROTECTION_CERTIFICATE("信用保护凭证", "F37"),
    ;
    public String name;
    public String value;

    InvestAssetTypeEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static boolean isBond(String assetType) {
        return InvestAssetTypeEnum.BOND.value.equals(assetType);
    }

    public static boolean isPledgeRepurchase(String assetType) {
        return InvestAssetTypeEnum.PLEDGE_REPURCHASE.value.equals(assetType);
    }

    public static boolean isCreditProtectionCertificate(String assetType) {
        return InvestAssetTypeEnum.CREDIT_PROTECTION_CERTIFICATE.value.equals(assetType);
    }

    public static boolean isFutures(String assetType) {
        return InvestAssetTypeEnum.FUTURES.value.equals(assetType);
    }

    @Override
    public String getCode() {
        return value;
    }

    @Override
    public String getDescription() {
        return name;
    }
}
