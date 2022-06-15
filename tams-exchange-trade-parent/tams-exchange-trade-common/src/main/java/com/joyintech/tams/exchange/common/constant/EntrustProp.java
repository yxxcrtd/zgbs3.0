package com.joyintech.tams.exchange.common.constant;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.common.enums.IEnum;

import java.util.HashSet;
import java.util.Set;

public enum EntrustProp implements IEnum<String> {
    //根据恒生柜台转入可能使用的值
    NORMAL("限价", "0"),
    ALLOTMENT("配股", "1"),
    APPLY("申购", "3"),
    REPURCHASE("回购", "4"),
    PS("配售", "5"),
    CHANGE_TO_STOCK("转股", "7"),
    HS("回售", "8"),
    PSFQ("配售放弃", "B"),
    PSCZ("配售冲正", "G"),
    ETF_APPLY("ETF申赎", "N"),
    COUNTERPARTY("对手方最优价格", "Q"),
    BEST5_TO_LIMIT("最优五档即时成交剩余转限价", "R"),
    OWN_FIRST("本方最优价格", "S"),
    IMMEDIATE_TO_CANCEL("即时成交剩余撤销", "T"),
    BEST5_TO_CANCEL("最优五档即时成交剩余撤销", "U"),
    FILL("全额成交或撤单", "V"),

    //盘后固定价格委托
    NORMAL_AFTER_SET_PRICE("收盘价固定价格", "PFP"),

    ZYCRK("质押出入库", "f"),
    JJSHSH("基金申赎", "L"),

    INTENTION("意向委托", "a"),
    SET_PRICE("定价委托", "b"),
    CONFIRM("确认委托", "c"),
    LIMIT_PRICE("限价委托", "d"),
    MUTUAL_TRADE("互报成交确认委托", "e"),
    LARGE_AFTER_CLOSING("盘后大宗委托", "ABT"),
    LARGE_AFTER_CLOSING_PRICE("盘后大宗收盘价委托", "AFC"),
    LARGE_AFTER_CLOSING_WEIGHT_PRICE("盘后大宗成交量加权平均价委托", "AFW"),
    NATIONAL_DEBT_PRICE("国债价格招标", "BIP"),
    NATIONAL_DEBT_RATE("国债利率招标", "BIY"),

    SH_LOF_SUBSCRIBE("上证LOF认购", "LFS"),
    SH_LOF_APPLY("上证LOF申购", "LFC"),
    SH_LOF_RANSOM("上证LOF赎回", "LFR"),
    SH_LOF_REDEPOSITING("上证LOF转托管", "LFT"),
    SH_LOF_SPLIT("上证LOF母基金分拆", "LFP"),
    SH_LOF_MERGE("上证LOF子基金合并", "LFM"),

    MONETARY_FUND_APPLY("货币基金申购", "OFC"),
    MONETARY_FUND_RANSOM("货币基金赎回", "OFR"),

    LIMIT_IMMEDIATE_TO_CANCEL("限价即时全部成交否则撤单", "OPA"),
    MARKET_IMMEDIATE_REMAIN_CANCEL("市价即时成交剩余撤单", "OPB"),
    MARKET_IMMEDIATE_TO_CANCEL("市价即时全部成交否则撤单", "OPC"),
    MARKET_REMAIN_LIMIT("市价剩余转限价", "OPD"),
    // 意向
    SH_REPURCHASE_INTENTION("上海协议回购意向申报", "BPA"),
    //报价
    SH_REPURCHASE_TRADE("上海协议回购成交申报", "BPB"),
    SH_REPURCHASE_RENEWAL("上海协议回购到期续做申报", "BPC"),
    SH_REPURCHASE_RELEASE("上海协议回购解除质押", "BPD"),
    SH_REPURCHASE_EXCHANGE("上海协议回购换券申报", "BPE"),
    SH_REPURCHASE_TERMINATE("上海协议回购提前终止申报", "BPF"),
    SH_REPURCHASE_EXPIRE("上海协议回购到期确认申报", "BPG"),
    //成交申报
    SH_REPURCHASE_TRADE_CONFIRM("上海协议回购成交申报确认", "BPH"),
    SH_REPURCHASE_TRADE_REFUSE("上海协议回购成交申报拒绝", "BPI"),
    SH_REPURCHASE_RENEWAL_CONFIRM("上海协议回购到期续做申报确认", "BPJ"),
    SH_REPURCHASE_RENEWAL_REFUSE("上海协议回购到期续做申报拒绝", "BPK"),
    SH_REPURCHASE_RELEASE_CONFIRM("上海协议回购解除质押确认", "BPL"),
    SH_REPURCHASE_RELEASE_REFUSE("上海协议回购解除质押拒绝", "BPM"),
    SH_REPURCHASE_EXCHANGE_CONFIRM("上海协议回购换券申报确认", "BPO"),
    SH_REPURCHASE_EXCHANGE_REFUSE("上海协议回购换券申报拒绝", "BPP"),
    SH_REPURCHASE_TERMINATE_CONFIRM("上海协议回购提前终止申报确认", "BPQ"),
    SH_REPURCHASE_TERMINATE_REFUSE("上海协议回购提前终止申报拒绝", "BPS"),

    SZ_REPURCHASE_BEGIN("深圳债券质押协议回购初始交易", "BPN"),
    SZ_REPURCHASE_BUYBACK("深圳债券质押协议回购购回交易", "BPR"),
    SZ_REPURCHASE_RENEWAL("深圳债券质押协议回购到期续做", "BPT"),

    //深圳 协议回购 增加 解除质押、换券申报
    SZ_REPURCHASE_RELEASE("深圳债券质押协议回购解除质押", "BPD"),
    SZ_REPURCHASE_EXCHANGE("深圳债券质押协议回购换券申报", "BPE"),

    // 上海固收平台
    SH_FIXEDINCOME_BAOJIA_CONFIRM("确定报价", "FI1"),
    SH_FIXEDINCOME_TRADE_CLICK("点击成交", "FI3"),
    SH_FIXEDINCOME_BAOJIA_OPPOSITE("指定对手方报价", "FI9"),
    SH_FIXEDINCOME_KZHCJSB("可转换成交申报", "FI100JY"),
    SH_FIXEDINCOME_ZYJCJSB("最优价成交申报", "FI101JY"),


    AGREED_REPURCHASE_REPLENISH("约定购回补充交易", "RNA"),
    AGREED_REPURCHASE_CLAWBACK("约定购回回补交易", "RNB"),
    AGREED_REPURCHASE_BEGIN("约定购回初始交易", "RNE"),
    AGREED_REPURCHASE_BUYBACK_SMALL("约定购回零碎股购回", "RNL"),
    AGREED_REPURCHASE_BUYBACK_WARRANT("约定购回配股权证购回", "RNP"),
    AGREED_REPURCHASE_BUYBACK("约定购回购回交易", "RNR"),

    //新债改造
//  	XZ_PPCJ_XD("匹配成交下单", "0"),//重复了，使用原有的
    SZ_XSCJ_XD("协商成交下单", "FI9-1"),
    SZ_XSCJ_JS("协商成交接受", "FI9-2"),
    SZ_XSCJ_JJ("协商成交拒绝", "FI9-3"),
    UN_MAKE_SURE("未指令", null),

    //期货市价
    FTDC_OPT_ANYPRICE("任意价", "FU1"),
    //FTDC_OPT_LIMITPRICE("限价","FU2"),
    FTDC_OPT_BESTPRICE("最优价", "FU3"),
    FTDC_OPT_LASTPRICE("最新价", "FU4"),
    FTDC_OPT_LASTPRICEPLUSONETICKS("最新价浮动上浮1个ticks", "FU5"),
    FTDC_OPT_LASTPRICEPLUSTWOTICKS("最新价浮动上浮2个ticks", "FU6"),
    FTDC_OPT_LASTPRICEPLUSTHREETICKS("最新价浮动上浮3个ticks", "FU7"),
    FTDC_OPT_ASKPRICE1("卖一价", "FU8"),
    FTDC_OPT_ASKPRICE1PLUSONETICKS("卖一价浮动上浮1个ticks", "FU9"),
    FTDC_OPT_ASKPRICE1PLUSTWOTICKS("卖一价浮动上浮2个ticks", "FUA"),
    FTDC_OPT_ASKPRICE1PLUSTHREETICKS("卖一价浮动上浮3个ticks", "FUB"),
    FTDC_OPT_BIDPRICE1("买一价", "FUC"),
    FTDC_OPT_BIDPRICE1PLUSONETICKS("买一价浮动上浮1个ticks", "FUD"),
    FTDC_OPT_BidPrice1PlusTwoTicks("买一价浮动上浮2个ticks", "FUE"),
    FTDC_OPT_BIDPRICE1PLUSTHREETICKS("买一价浮动上浮3个ticks", "FUF"),
    FTDC_OPT_FIVELEVELPRICE("五档价", "FUG");

    private static Set<String> marketPriceSet = new HashSet<>();

    static {
        marketPriceSet.add(EntrustProp.COUNTERPARTY.value);
        marketPriceSet.add(EntrustProp.OWN_FIRST.value);
        marketPriceSet.add(EntrustProp.IMMEDIATE_TO_CANCEL.value);
        marketPriceSet.add(EntrustProp.BEST5_TO_CANCEL.value);
        marketPriceSet.add(EntrustProp.FILL.value);
        marketPriceSet.add(EntrustProp.BEST5_TO_LIMIT.value);

        marketPriceSet.add(FTDC_OPT_ANYPRICE.value);
        marketPriceSet.add(FTDC_OPT_BESTPRICE.value);
        marketPriceSet.add(FTDC_OPT_LASTPRICE.value);
        marketPriceSet.add(FTDC_OPT_LASTPRICEPLUSONETICKS.value);
        marketPriceSet.add(FTDC_OPT_LASTPRICEPLUSTWOTICKS.value);
        marketPriceSet.add(FTDC_OPT_LASTPRICEPLUSTHREETICKS.value);
        marketPriceSet.add(FTDC_OPT_ASKPRICE1.value);
        marketPriceSet.add(FTDC_OPT_ASKPRICE1PLUSONETICKS.value);
        marketPriceSet.add(FTDC_OPT_ASKPRICE1PLUSTWOTICKS.value);
        marketPriceSet.add(FTDC_OPT_ASKPRICE1PLUSTHREETICKS.value);
        marketPriceSet.add(FTDC_OPT_BIDPRICE1.value);
        marketPriceSet.add(FTDC_OPT_BIDPRICE1PLUSONETICKS.value);
        marketPriceSet.add(FTDC_OPT_BidPrice1PlusTwoTicks.value);
        marketPriceSet.add(FTDC_OPT_BIDPRICE1PLUSTHREETICKS.value);
        marketPriceSet.add(FTDC_OPT_FIVELEVELPRICE.value);
    }

    public final String value;
    public final String name;

    EntrustProp(String name, String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * 是否现券交易
     */
    public static boolean isNewBondTrade(String entrustProp) {
        return EntrustProp.NORMAL.value.equals(entrustProp)
                || EntrustProp.SH_FIXEDINCOME_BAOJIA_OPPOSITE.value.equals(entrustProp);
    }

    /**
     * 是否协商成交交易
     */
    public static boolean isConsultingTrade(String entrustProp) {
        if (StringUtil.equals(entrustProp, EntrustProp.SH_FIXEDINCOME_BAOJIA_OPPOSITE.value)
                || StringUtil.equals(entrustProp, EntrustProp.SZ_XSCJ_XD.value)
                || StringUtil.equals(entrustProp, EntrustProp.SZ_XSCJ_JS.value)
                || StringUtil.equals(entrustProp, EntrustProp.SZ_XSCJ_JJ.value)) {
            return true;
        }

        return false;
    }

    // 是否需要跳过风控
    public static boolean isSkipRisk(String entrustProp) {
        return EntrustProp.ZYCRK.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_EXCHANGE.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_EXCHANGE_REFUSE.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_EXCHANGE_CONFIRM.value.equals(entrustProp);
    }

    public static boolean isMarketPrice(String entrustProp) {
        return marketPriceSet.contains(entrustProp);
    }

    // 是否为需要拆单业务
    public static boolean isSplitEntrust(String entrustProp) {
        return isMarketPrice(entrustProp) || EntrustProp.NORMAL.value.equals(entrustProp);
    }

    // 是否为解除质押和中途换券
    public static boolean isRepurchaseReleaseOrExchange(String entrustProp) {
        return EntrustProp.SH_REPURCHASE_RELEASE.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_RELEASE_CONFIRM.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_RELEASE_REFUSE.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_EXCHANGE.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_EXCHANGE_CONFIRM.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_EXCHANGE_REFUSE.value.equals(entrustProp);
    }

    // 中途换券
    public static boolean isRepurchaseExchange(String entrustProp) {
        return EntrustProp.SH_REPURCHASE_EXCHANGE.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_EXCHANGE_CONFIRM.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_EXCHANGE_REFUSE.value.equals(entrustProp);
    }

    // 是否协议回购首期交易
    public static boolean isRepurchase_First(String entrustProp) {
        return EntrustProp.SH_REPURCHASE_TRADE.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_TRADE_CONFIRM.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_TRADE_REFUSE.value.equals(entrustProp);
    }

    // 是否到期续作
    public static boolean isRepurchaseRenewal(String entrustProp) {
        return EntrustProp.SH_REPURCHASE_RENEWAL.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_RENEWAL_CONFIRM.value.equals(entrustProp)
                || EntrustProp.SH_REPURCHASE_RENEWAL_REFUSE.value.equals(entrustProp);
    }

    public static boolean equalsProp(String prop, EntrustProp entrustProp) {
        if (prop == null) {
            return false;
        }

        return entrustProp.value.equals(prop);
    }

    /**
     * 根据价格模式判断是否是非交易类
     *
     * @param entrustProp 委托模式
     * @return true: 非交易类；false: 交易类
     */
    public static boolean isUnTrade(String entrustProp) {
        if (entrustProp == null) {
            return true;
        }
        return equalsProp(entrustProp, EntrustProp.HS)
                || equalsProp(entrustProp, EntrustProp.ZYCRK)
                || equalsProp(entrustProp, EntrustProp.CHANGE_TO_STOCK)
                || equalsProp(entrustProp, EntrustProp.APPLY)
                || equalsProp(entrustProp, EntrustProp.ALLOTMENT)
                || equalsProp(entrustProp, EntrustProp.PS);
    }

    public static boolean isTrade(String entrustProp) {
        return !isUnTrade(entrustProp);
    }

    public String getValue() {
        return value;
    }

    public String getName() {
        return name;
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
