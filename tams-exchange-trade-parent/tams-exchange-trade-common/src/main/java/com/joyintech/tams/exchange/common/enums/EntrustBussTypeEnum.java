package com.joyintech.tams.exchange.common.enums;

/**
 * 指令业务类型
 *
 * @author swwan
 * @date 2020/12/2
 */
public enum EntrustBussTypeEnum {
    STOCK_AUCTION("股票竞价指令", "01"),
    FUND_AUCTION("基金竞价指令", "02"),
    BULK_BOND_PLEDGED_REPURCHASE("大宗债券质押式回购指令", "03"),
    BULK_STOCK("大宗股票指令", "04"),
    BULK_BOND("大宗基金指令", "05"),
    BOND_AUCTION("大宗债券指令", "06"),
    SH_FIXED_INCOME_PLATFORM_CASH_COUPONS("上交所固定收益平台现券交易指令", "08"),
    BOND_REPURCHASE("债券回售指令", "09"),
    ALLOTMENT("配股指令", "10"),
    IPO_SUBSCRIPTION("新股申购指令", "11"),
    ALLOTMENT_DEBT("配债指令", "12"),
    CONVERTIBLE_BOND_SUBSCRIPTION("可转债申购指令", "13"),
    STANDARD_COUPONS_IN_AND_OUT("标准券出入库指令", "14"),
    BOND_PLEDGE_REPURCHASE_BIDDING("债券质押式回购竞价指令", "15"),
    DEBT_TO_EQUITY("债转股指令", "16"),
    REPO_TO_FIRST_PHASE("协议回购首期指令", "17"),
    REPO_TO_ADVANCE_PHASE("协议回购提前购回指令", "18"),
    REPO_TO_MATURITY("协议回购到期购回指令", "19"),
    REPO_TO_RENEWAL("协议回购到期续作指令", "20"),
    REPO_TO_HALFWAY_EXCHANGE("协议回购中途换券指令", "21"),
    REPO_TO_RELEASE_PLEDGE("协议回购解除质押指令", "22"),
    FUTURES("期货指令", "23"),
    ETF_PURCHASE_AND_RANSOM("ETF申购赎回指令", "24"),
    CREDIT_PROTECTION_CERTIFICATE("信用保护凭证指令", "30"),
    ;

    public final String name;
    public final String value;

    EntrustBussTypeEnum(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
