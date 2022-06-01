package com.joyintech.tams.exchange.common.constant.joyin;

import com.joyintech.tams.exchange.common.enums.IEnum;

public enum InvestmentAssetType1 implements IEnum<String> {
    NEGOTIATED_REPO("质押式协议回购", "H01"),
    BIDDING_REPO("质押式竞价回购", "H02"),
    TREASURY_FUTURES("国债期货", "FU02"),
    SHARE_PRICE_INDEX_FUTURES("股指期货", "FU01");

    public String name;
    public String value;

    InvestmentAssetType1(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static boolean isBiddingRepo(String value){
        return InvestmentAssetType1.BIDDING_REPO.value.equals(value);
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
