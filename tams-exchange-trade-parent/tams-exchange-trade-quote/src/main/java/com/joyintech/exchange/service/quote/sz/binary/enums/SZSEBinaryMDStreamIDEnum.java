package com.joyintech.exchange.service.quote.sz.binary.enums;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 行情类别
 */
public enum SZSEBinaryMDStreamIDEnum {
    /**
     * 未知状态
     */
    UNKNOWN("unknown"),
    /**
     * 指数类产品
     */
    INDEX("MD001"),
    /**
     * 股票（A,B股）类产品
     */
    STOCK("MD002"),
    /**
     * 债券类产品
     */
    BOND("MD003"),
    /**
     * 基金类产品
     */
    FUND("MD004"),
    /**
     * 期权类产品
     */
    OPTION("MD301"),
    /**
     * 国债预发行产品
     */
    NATION_DEBT("MD101"),
    /**
     * 盘后固定价格
     */
    AFTER_HOURS_FIXED_PRICE("MD102"),
    /**
     * 以收盘价交易的盘后定价大宗交易快照行情
     */
    MD_STREAM_ID_060("060"),
    /**
     * 以成交量加权平均价交易的盘后定价大宗交易快照行情
     */
    MD_STREAM_ID_061("061");

    private static final Map<String, SZSEBinaryMDStreamIDEnum> mapping;

    static {
        mapping = new HashMap<>(16);
        Arrays.stream(SZSEBinaryMDStreamIDEnum.values()).forEach(type -> mapping.put(type.getValue(), type));
    }

    private final String value;

    SZSEBinaryMDStreamIDEnum(String value) {
        this.value = value;
    }

    public static Optional<SZSEBinaryMDStreamIDEnum> lookup(String type) {
        return Optional.ofNullable(mapping.get(type));
    }

    public String getValue() {
        return value;
    }
}
