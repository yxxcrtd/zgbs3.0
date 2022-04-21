package com.joyintech.tams.exchange.common.constant;

import cn.hutool.core.util.StrUtil;
import com.joyintech.tams.exchange.common.enums.IEnum;

public enum InstructionPs implements IEnum<String> {

    BUY("T01", "买入", "1"),
    SELL("T02", "卖出", "2"),
    APPLY("T04", "申购", ""),
    REDEEM("T06", "赎回", ""),
    ISSUE("T47", "增发（配售）", ""),
    RESALE("T03", "行权-回售", ""),
    ALLOCATESHARE("T46", "配股", ""),
    BOND2STOCK("T49", "债转股", ""),
    SUBSCRIBE("T52", "认购", ""),
    POSITIVEREPO("03", "正回购", "1"),
    REVERSEDREPO("04", "逆回购", "2"),
    // todo 投管暂未支持，质押出入质方向，暂时搁置
    SEAL("T68", "质押入库", ""),
    UNSEAL("T69", "质押出库", ""),
    PZ("T97", "配债", ""),

    BUYOPEN("T100", "买入开仓", ""),
    SELLCLOSE("T101", "卖出平仓", ""),
    SELLOPEN("T102", "卖出开仓", ""),
    BUYCLOSE("T103", "买入平仓", "");

    public final String value;
    public final String name;
    public final String entrustBs;

    InstructionPs(String value, String name, String entrustBs) {
        this.name = name;
        this.value = value;
        this.entrustBs = entrustBs;
    }

    public static String getEntrustBs(String value) {
        for (InstructionPs ps : values()) {
            if (ps.value.equals(value) && StrUtil.isNotEmpty(ps.entrustBs)) {
                return ps.entrustBs;
            }
        }
        return value;
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
