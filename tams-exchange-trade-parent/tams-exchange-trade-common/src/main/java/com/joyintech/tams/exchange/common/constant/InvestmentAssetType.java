package com.joyintech.tams.exchange.common.constant;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * 投管资产分类
 */
public enum InvestmentAssetType implements IEnum<String> {

    BOND("债券", "F01"),
    FUND("基金", "F02"),
    PLAN_ASSET_RATE("计划类资产(利率型)", "F03"),
    PLAN_ASSET_VALUE("计划类资产(净值型)", "F04"),
    DEPOSIT("存款", "F05"),
    PROJECT_ASSET("项目类资产", "F06"),
    PLEDGE_REPURCHASE("质押式回购", "F07"),
    CHAI_JIE("拆借", "F08"),
    PLAN_ASSET_CURRENCY("计划类资产(货币型)", "F09"),
    BUY_OUT_REPURCHASE("买断式回购", "F10"),
    STOCK("股票", "F11"),
    BOND_LOANS("债券借贷", "F12"),
    FOREX_SPOT_RATE("外汇即期", "F13"),
    FOREX_FORWARD("外汇远期", "F14"),
    FOREX_FALL("外汇掉期", "F15"),
    RATE_EXCHANGE("利率互换", "F16"),
    PRIMARY_MARKET_DEBT("一级市场债", "F17"),
    SHARE_OPTION("期权", "F18"),
    FUTURES("期货", "F20"),
    CREDIT_PROTECTION_CERTIFICATE("信用保护凭证", "F36"),
    ;
    public String name;
    public String value;

    InvestmentAssetType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static boolean isBond(String assetType) {
        return InvestmentAssetType.BOND.value.equals(assetType);
    }

    public static boolean isPledgeRepurchase(String assetType) {
        return InvestmentAssetType.PLEDGE_REPURCHASE.value.equals(assetType);
    }

    public static boolean isCreditProtectionCertificate(String assetType) {
        return InvestmentAssetType.CREDIT_PROTECTION_CERTIFICATE.value.equals(assetType);
    }

    public static boolean isFutures(String assetType) {
        return InvestmentAssetType.FUTURES.value.equals(assetType);
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
