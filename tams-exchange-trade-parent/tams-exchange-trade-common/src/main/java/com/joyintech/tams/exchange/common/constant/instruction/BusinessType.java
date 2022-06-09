package com.joyintech.tams.exchange.common.constant.instruction;

/**
 * @Author: think
 * @Description:
 * @Date: Created in 16:08 2020/10/22
 */
public enum BusinessType {
	FIXED_INCOME_BUY("上交所固定收益平台债券买入", "01"),
    FIXED_INCOME_SELL("上交所固定收益平台债券卖出", "02"),

    INTEGRATED_PROTOCOL_BUY("深交所综合协议平台债券买入", "03"),
    INTEGRATED_PROTOCOL_SELL("深交所综合协议平台债券卖出", "04"),

    // add by etliu 20220531 start
    // 3.0.6场内 ZGCPCN-973:【手工交收管理】债券是否非担保为“是”时，成交回报后，手工交收管理查询不到数据
    BOND_BUY("场内现券买入", "25"),
    BOND_SELL("场内现券卖出", "26"),
    // add by etliu 20220531 end

    CREDIT_PROTECTION_CERTIFICATE_BUY("信用保护凭证买入", "25"),
    CREDIT_PROTECTION_CERTIFICATE_SELL("信用保护凭证卖出", "26"),

    SH_SELL_PROTOCOL_BACK_BEGIN("上交所协议正回购初始交易", "05"),
    SH_SELL_PROTOCOL_BACK_EXCHANGE("上交所协议正回购中途换券", "06"),
    SH_SELL_PROTOCOL_BACK_TERMINATE("上交所协议正回购提前购回", "07"),
    SH_SELL_PROTOCOL_BACK_BUYBACK("上交所协议正回购到期购回", "08"),
    SH_SELL_PROTOCOL_BACK_RENEWAL("上交所协议正回购到期续作", "09"),
    SH_SELL_PROTOCOL_BACK_RELEASE("上交所协议正回购解除质押", "10"),

    SH_ANTI_PROTOCOL_BACK_BEGIN("上交所协议逆回购初始交易", "11"),
    SH_ANTI_PROTOCOL_BACK_EXCHANGE("上交所协议逆回购中途换券", "12"),
    SH_ANTI_PROTOCOL_BACK_TERMINATE("上交所协议逆回购提前购回", "13"),
    SH_ANTI_PROTOCOL_BACK_BUYBACK("上交所协议逆回购到期购回", "14"),
    SH_ANTI_PROTOCOL_BACK_RENEWAL("上交所协议逆回购到期续作", "15"),
    SH_ANTI_PROTOCOL_BACK_RELEASE("上交所协议逆回购解除质押", "16"),

    SZ_SELL_PROTOCOL_BACK_BEGIN("深交所协议正回购初始交易", "17"),
    SZ_SELL_PROTOCOL_BACK_TERMINATE("深交所协议正回购提前购回", "18"),
    // modify by etliu 20220226 start
    // 广农商二阶段 GNS-953：【广农商场内清算】深交所协议回购 正/逆回购首期，手工交收-日终清结算后，头寸更新有误
//    SZ_SELL_PROTOCOL_BACK_RENEWAL("深交所协议正回购到期续作", "19"),
//    SZ_SELL_PROTOCOL_BACK_BUYBACK("深交所协议正回购到期购回", "20"),
    SZ_SELL_PROTOCOL_BACK_BUYBACK("深交所协议正回购到期购回", "19"),
    SZ_SELL_PROTOCOL_BACK_RENEWAL("深交所协议正回购到期续作", "20"),
    // modify by etliu 20220226 end

    SZ_ANTI_PROTOCOL_BACK_BEGIN("深交所协议逆回购初始交易", "21"),
    SZ_ANTI_PROTOCOL_BACK_TERMINATE("深交所协议逆回购提前购回", "22"),
    SZ_ANTI_PROTOCOL_BACK_BUYBACK("深交所协议逆回购到期购回", "23"),
    SZ_ANTI_PROTOCOL_BACK_RENEWAL("深交所协议逆回购到期续作", "24");


    public final String value;
    public final String name;

    BusinessType(String name, String value) {
        this.name = name;
        this.value = value;
    }
}