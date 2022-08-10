package com.joyintech.tams.exchange.common.constant;

public enum AgentType {

    KINGDOM_KCBPW("金证kcbpw", "kcbpw"),
    UFX("恒生ufx", "ufx"),
    KD_GATEWAY("金证KDGateway", "KDGateway"),
    JSD("金仕达", "jsd"),
    MOCK("模拟撮合终端", "mock"),
    CTP("ctp交易终端", "ctp");

    public final String value;
    public final String name;

    AgentType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static AgentType agentType(String value) {
        switch (value) {
            case "kcbpw":
                return KINGDOM_KCBPW;
            case "ufx":
                return UFX;
            case "KDGateway":
                return KD_GATEWAY;
            case "ctp":
                return CTP;
        }
        return null;
    }

    // 区分债券柜台， 成交为净价成交柜台
    public static boolean isNetPriceTrade(String agentType) {
        return KINGDOM_KCBPW.value.equals(agentType) || KD_GATEWAY.value.equals(agentType) || JSD.value.equals(agentType);
    }
}
