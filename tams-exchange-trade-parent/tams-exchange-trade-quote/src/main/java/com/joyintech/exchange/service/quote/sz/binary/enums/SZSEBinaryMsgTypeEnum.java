package com.joyintech.exchange.service.quote.sz.binary.enums;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public enum SZSEBinaryMsgTypeEnum {
    /**
     * 集中竞价交易行情快照
     */
//    LEVEL1("M002"),
    /**
     * 市场状态
     */
//    MARKET_("M001"),
    /**
     * 登陆
     */
//    LOGON("S001"),

    /**
     * 登出
     */
//    LOGOUT("S002"),

    /**
     * 心跳
     */
//    HEARTBEAT("S003"),

    /**
     * 盘后大宗定价行情
     */
    MSG_TYPE_300611(300611);

    private static final Map<Integer, SZSEBinaryMsgTypeEnum> mapping;

    static {
        mapping = new HashMap<>(16);
        Arrays.stream(SZSEBinaryMsgTypeEnum.values()).forEach(msgType -> mapping.put(msgType.getValue(), msgType));
    }

    private final int value;

    SZSEBinaryMsgTypeEnum(int value) {
        this.value = value;
    }

    public static Optional<SZSEBinaryMsgTypeEnum> lookup(int msgType) {
        return Optional.ofNullable(mapping.get(msgType));
    }

    public int getValue() {
        return value;
    }
}
