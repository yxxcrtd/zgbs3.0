package com.joyintech.tams.exchange.common.constant;

public enum BSFlag {

    ZQMR("B", "证券买入", "0B" ),
    ZQMC("S", "证券卖出", "0S" ),
    RZ("R", "融资", "" ),
    RQ("Q", "融券", "" ),
    ZG("G", "转股", "5S" ),
    HS("H", "回售", "6S" ),
    JJRG("5", "基金认购", "7J" ),
    JIJISG("3", "基金申购", "7M" ),
    JJSH("4", "基金赎回", "7N" ),
    YSYY("Y", "预受要约", "ES" ),
    JCYS("E", "解除预受", "FS" ),
    ZTG("Z", "转托管", "3S" ),
    QZTG("A", "全转托管", "" ),
    PSSG("P", "配售申购", "" ),
    JJSG("F", "竞价申购", "" ),
    DJSG("D", "定价申购", "7B" ),
    PGJK("J", "配股缴款", "4B" ),
    ZDJY("U", "指定交易", "Z0" ),
    JCZD("V", "解除指定", "Z1" ),
    HGJC("X", "回购解除", "Z3" ),
    HGZD("W", "回购指定", "Z2" ),
    PZ("K", "配债", "" ),
    PSJK("T", "配售缴款", "8B" ),
    PSFQ("L", "配售放弃", "8C" ),
    DFMR("a", "对方买入", "YB" ),
    BFMR("b", "本方买入", "XB" ),
    JSMR("c", "即时买入", "2B" ),
    WDMR_HS("d", "五档买入(深沪)", "VB" ),
    QEMR("e", "全额买入", "WB"),
    DFMC("f", "对方卖出", "YS"),
    BFMC("g", "本方卖出", "XS"),
    JSMC("h", "即时卖出", "2S"),
    WDMC_HS("i", "五档卖出(深沪)", "VS"),
    QEMC("j", "全额卖出", "WS"),
    ZXMR_H("q", "转限买入(沪)", "UB"),
    ZXMC_H("r", "转限卖出(沪)", "US"),
    YXMR("1E", "意向买", "HB"),
    YXMC("1F", "意向卖", "HS"),
    DJMR("1G", "定价买", "OB"),
    DJMC("1H", "定价卖", "OS"),
    QRMR("1I", "确认买", "1B"),
    QRMC("1J", "确认卖", "1S"),
    PDMR("2I", "配对买（深证）", "NB"),
    PDMC("2J", "配对卖（深证）", "NS"),
    PHDJDZMR("1m", "盘后定价大宗买", "SB"),
    PHDJDZMC("1n", "盘后定价大宗卖", "SS"),
    PHDJMR("3m", "盘后定价买", "QB"),
    PHDJMC("3n", "盘后定价卖", "QS"),

    PPCJMR("4m", "匹配成交买入/债券现券竞价买入",""),
    PPCJMC("4n", "匹配成交卖出/债券现券竞价卖出",""),




    LOF_RG("2e", "LOF认购", ""),
    LOF_SG("2f", "LOF申购", ""),
    LOF_SH("2g", "LOF赎回", ""),
    SH_LOF_FC("1w", "上证LOF分拆", ""),
    SH_LOF_HB("1l", "上证LOF合并", ""),

    ETF_SG("1", "深圳单市场ETF、上海单市场ETF、上海跨市场ETF、上海债券ETF、深圳货币ETF的申购", "7K"),
    ETF_SH("2", "深圳单市场ETF、上海单市场ETF、上海跨市场ETF、上海债券ETF、深圳货币ETF的赎回", "7L"),

    ETF_SWSG("82", "深圳跨市场ETF的实物申购", "82"),
    ETF_SWSH("84", "深圳跨市场ETF的实物赎回", "84"),
    JJCF("85", "基金拆分", "7R"),
    JJHB("86", "基金合并", "7T"),
    ZGCNSG("87", "资管场内申购", ""),
    ZGCNSH("88", "资管场内赎回", ""),
    ETF_SWCZ("89", "ETF实物冲账", "" ),
    ETF_KJKSCSG("90", "跨境跨市场ETF申购", "7V" ),
    ETF_KJKSCSH("91", "跨境跨市场ETF赎回", "7W" ),
    BJHGMR("1K", "报价回购买入", "9O"),
    BJHGRZ("1N", "报价回购入质", "9K"),
    BJHGCZ("1O", "报价回购出质", "9L"),
    YDHGCSJY("1P", "约定购回初始交易", "9U"),
    GTYXMR("1R", "柜台意向买", ""),
    GTYXMC("1S", "柜台意向卖", ""),

    ZYRZ("n", "质押入质", "9M"),
    ZYCZ("p", "质押出质", "9N"),

    // 固收平台买卖委托
    ZDDSFSBMR("2X", "指定对手方申报买入", "BZ"),
    ZDDSFSBMC("2Y", "指定对手方申报卖出", "SZ"),
    XYJYSBMR("4J", "协议交易申报买入", ""),
    XYJYSBMC("4K", "协议交易申报卖出", ""),
    // 固收平台确定报价报价申报
    QDBJSBMR("4A", "确定报价申报买入", "BF"),
    QDBJSBMC("4B", "确定报价申报卖出", "SF"),
    QDBJSXBJ("4C", "确定报价双向报价", ""),
    // 固收平台确定报价成交申报
    DJCJSBMR("4D", "点击成交申报买入", "BC"),
    DJCJSBMC("4E", "点击成交申报卖出", "SC"),
    ZYJCJSBMR("4F", "最优价成交申报买入", ""),
    ZYJCJSBMC("4G", "最优价成交申报卖出", ""),
    KZHCJSBMR("4H", "可转换成交申报买入", ""),
    KZHCJSBMC("4I", "可转换成交申报卖出", ""),

    // 债券协议回购委托申报 (深证)
    CSJYRRZJ("2C", "初始交易融入资金", "EA"),
    CSJYRCZJ("2D", "初始交易融出资金", "EB"),
    GHJYFHZJ("2E", "购回交易返回资金", "ED"),
    GHJYSQZJ("2F", "购回交易收取资金", "EF"),
    DQXZRCZJ("2H", "到期续做融出资金", ""),
    //    DQXZRRZJ("2K", "到期续做融入资金",""),
    // 债券协议回购委托申报 (上海)
    // 上海债券协议回购意向申报
    YXRRZJ("3g", "意向融入资金", "EC"),
    YXRCZJ("2T", "意向融出资金", "Ec"),
    // 上海债券协议回购报价申报
//    CSJYRRZJ("2C","初始交易融入资金"),
//    DQJS("2E", "到期结算",""),
    DQXZ("2K", "到期续做", "EJ"),
    JCZY("2O", "解除质押", "EK"),
    HQSB("2Q", "换券申报", "EM"),
    TQZZRRZJ("2M", "提前终止融入资金", "EN"),
    TQZZRCZJ("2N", "提前终止融出资金", "En"),
    // 上海债券协议回购成交申报
    CJSBQR("2V", "成交申报确认", "EB"),
    CJSBJJ("2U", "成交申报拒绝", ""),
    DQXZSBQR("2L", "到期续做申报确认", "Ej"),
    DQXZSBJJ("2W", "到期续做申报拒绝", ""),
    JCZYSBQR("3e", "解除质押申报确认", "Ek"),
    JCZYSBJJ("3f", "解除质押申报拒绝", ""),
    HQSBQR("2Z", "换券申报确认", "Em"),
    HQSBJJ("3a", "换券申报拒绝", ""),
    TQZZRCQR("3b", "提前终止融出确认", "EN"),
    TQZZRRQR("3c", "提前终止融入确认", "En"),
    TQZZSBJJ("3d", "提前终止申报拒绝", ""),


    GTDE_ZSMR("1T", "柜台大额做市买", ""),
    GTDE_ZSMC("1U", "柜台大额做市卖", ""),
    GTXE_ZSMR("1V", "柜台小额做市买", ""),
    GTXE_ZSMC("1W", "柜台小额做市卖", ""),
    GTCJ_QRMR("1X", "柜台成交确认买", ""),
    GTCJ_QRMC("1Y", "柜台成交确认卖", ""),

    XYDJ_MR("1e", "协议定价买入", ""),
    XYDJ_MC("1f", "协议定价卖出", ""),
    XYCJ_QRMR("1g", "协议成交确认买入", ""),
    XYCJ_QRMC("1h", "协议成交确认卖出", ""),

    ZQZTG("1i", "证券转托管", ""),

    XYHB_CJQRMR("1j", "协议互报成交确认买入", ""),
    XYHB_CJQRMC("1k", "协议互报成交确认卖出", ""),

//    JJXJMR("2B", "竞价限价买入"),
//    ZQXYHG_CSJYRRZJ("2C", "债券协议回购初始交易融入资金"),
//    ZQXYHG_CSJYRCZJ("2D", "债券协议回购初始交易融出资金"),
//    ZQXYHG_GHJYFHZJ("2E", "债券协议回购购回交易返回资金"),
//    ZQXYHG_GHJYSQZJ("2F", "债券协议回购购回交易收取资金"),

    SFHG_CK("3A", "三方回购入库", ""),
    SFHG_RK("3C", "三方回购出库", ""),
    SFHG_CSJY("3D", "三方回购初始交易", ""),
    SFHG_CSJYJJ("3E", "三方回购初始交易拒绝", ""),
    SFHG_CSJYTY("3F", "三方回购初始交易同意", ""),
    SFHG_TQGH("3G", "三方回购提前购回", ""),
    SFHG_TQGHJJ("3H", "三方回购提前购回拒绝", ""),
    SFHG_TQGHTY("3I", "三方回购提前购回同意", ""),
    SFHG_DQGH("3J", "三方回购到期购回", ""),
    SFHG_DQXZ("3K", "三方回购到期续做", ""),
    SFHG_DQXZJJ("3L", "三方回购到期续做拒绝", ""),
    SFHG_DQXZTY("3M", "三方回购到期续做同意", ""),
    SFHG_JCZY("3N", "三方回购解除质押", ""),
    SFHG_JCZYJJ("3O", "三方回购解除质押拒绝", ""),
    SFHG_JCZYTY("3P", "三方回购解除质押同意", ""),
    SFHG_HQ("3Q", "三方回购换券", ""),
    SFHG_HQJJ("3R", "三方回购换券拒绝", ""),
    SFHG_HQTY("3T", "三方回购换券同意", ""),
    //    SFHG_BQ("3", "三方回购补券"),
    SFHG_YXSBRR("3V", "三方回购意向申报融入", ""),
    SFHG_YXSBRC("3W", "三方回购意向申报融出", "");


    public final String value;
    public final String name;
    public final String kdValue;

    BSFlag(String value, String name, String kdValue) {
        this.name = name;
        this.value = value;
        this.kdValue = kdValue;
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public String getKdValue() {
        return kdValue;
    }
}