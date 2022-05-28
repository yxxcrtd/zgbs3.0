package com.joyintech.tams.exchange.common.constant;

import cn.hutool.core.util.StrUtil;

/**
 * 交易方向-交易方向名称-买卖方向-委托属性 关系映射表
 *
 * 根据交易方向ps，获取买卖方向entrustBs和委托属性entrustProp
 */
public enum EntrustBsProp {
    /**
     * 竞价交易
     */
    BUY("T01", "买入", "1", EntrustProp.NORMAL),
    SELL("T02", "卖出", "2", EntrustProp.NORMAL),
    APPLY("T04", "申购", "1", EntrustProp.APPLY),
    REDEEM("T06", "赎回", "2", EntrustProp.REPURCHASE),
    ISSUE("T47", "增发（配售）", "1", EntrustProp.PS),
    RESALE("T03", "行权（回售）", "2", EntrustProp.HS),
    ALLOCATE_SHARE("T46", "配股", "1", EntrustProp.ALLOTMENT),
    BOND_STOCK("T49", "债转股", "2", EntrustProp.CHANGE_TO_STOCK),
    SUBSCRIBE("T52", "认购", "1", EntrustProp.APPLY),
    POSITIVE_REPO("03", "正回购", "1", EntrustProp.NORMAL),
    REVERSED_REPO("04", "逆回购", "2", EntrustProp.NORMAL),
    SEAL("T68", "质押入库", "2", EntrustProp.ZYCRK),
    UNSEAL("T69", "质押出库", "1", EntrustProp.ZYCRK),
    allocate_bond("T97", "配债", "1", EntrustProp.PS),
    EXERCISE("T99", "行权", "2"),
    open_long("TF01", "开多", "1"),
    close_short("TF02", "平空", "1"),
    CLOSE_TODAY_SHORT("TF03", "平今空", "1"),
    OPENS_HORT("TF04", "开空", "2"),
    CLOSE_LONG("TF05", "平多", "2"),
    CLOSE_TODAY_LONG("TF06", "平今多", "2"),
    VOTE("T107", "网络投票", "T107"),
    BONUS("T110", "分红设置", "T110"),
    FINANCE_BUY("T104", "融资买入", "1"),
    SECURITIES_LOAN_SELL("T105", "融券卖出", "2"),

    /**
     * 大宗交易申报类型
     */
    INTENTION("a", "意向申报", EntrustProp.INTENTION),
    SET_PRICE("b", "定价申报", EntrustProp.SET_PRICE),
    CONFIRM("c", "成交申报", EntrustProp.CONFIRM),
    LIMIT_PRICE("d", "限价委托", EntrustProp.LIMIT_PRICE),
    MUTUAL_TRADE("e", "互报成交确认委托", EntrustProp.MUTUAL_TRADE),
    LARGE_AFTER_CLOSING("ABT", "盘后大宗委托", EntrustProp.LARGE_AFTER_CLOSING),
    LARGE_AFTER_CLOSING_PRICE("AFC", "收盘价盘后定价申报", EntrustProp.LARGE_AFTER_CLOSING_PRICE),
    LARGE_AFTER_CLOSING_WEIGHT_PRICE("AFW", "日均价盘后定价申报", EntrustProp.LARGE_AFTER_CLOSING_WEIGHT_PRICE),

    /**
     * 上海固收平台申报类型
     */
    SH_FIXEDINCOME_BAOJIA_CONFIRM("FI1", "成交申报", EntrustProp.SH_FIXEDINCOME_BAOJIA_CONFIRM),
    SH_FIXEDINCOME_TRADE_CLICK("FI3", "点击成交申报", EntrustProp.SH_FIXEDINCOME_TRADE_CLICK),
    SH_FIXEDINCOME_BAOJIA_OPPOSITE("FI9", "指定对手方报价", EntrustProp.SH_FIXEDINCOME_BAOJIA_OPPOSITE),
    SH_FIXEDINCOME_KZHCJSB("FI100JY", "可转换成交申报", EntrustProp.SH_FIXEDINCOME_KZHCJSB),
    SH_FIXEDINCOME_ZYJCJSB("FI101JY", "最优价成交申报", EntrustProp.SH_FIXEDINCOME_ZYJCJSB),
    ;

    String code;
    String name;
    String entrustBs;
    EntrustProp entrustProp;

    EntrustBsProp(String code, String name, String entrustBs) {
        this.code = code;
        this.name = name;
        this.entrustBs = entrustBs;
    }

    EntrustBsProp(String code, String name, String entrustBs, EntrustProp entrustProp) {
        this.code = code;
        this.name = name;
        this.entrustBs = entrustBs;
        this.entrustProp = entrustProp;
    }

    EntrustBsProp(String code, String name, EntrustProp entrustProp) {
        this.code = code;
        this.name = name;
        this.entrustProp = entrustProp;
    }

    public static EntrustProp getEntrustProp(String code) {
        for (EntrustBsProp entrustBsProp : values()) {
            if (entrustBsProp.code.equals(code)) {
                return entrustBsProp.entrustProp;
            }
        }
        return null;
    }

    public static String getEntrustBs(String code) {
        for (EntrustBsProp entrustBsProp : values()) {
            if (entrustBsProp.code.equals(code) && StrUtil.isNotEmpty(entrustBsProp.entrustBs)) {
                return entrustBsProp.entrustBs;
            }
        }
        return code;
    }
}
