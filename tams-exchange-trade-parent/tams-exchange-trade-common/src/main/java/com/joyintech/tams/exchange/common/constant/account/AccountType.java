package com.joyintech.tams.exchange.common.constant.account;

public enum AccountType {
    UFX("恒生", "1"),
    KINGDOM_KCBPW("金证", "2"),
    JSD("金仕达", "3"),
    KDGateway("金证KDGateway", "4"),
    CTP("上期所综合交易平台", "5"),
    MOCK("模拟撮合终端", "10"),
    KINGDOM_KCBPU("金证U","15");


    public final String value;
    public final String name;

    AccountType(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
