package com.joyintech.tams.exchange.common.enums.invest;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * 投管业务类型
 *
 * @author swwan
 * @date 2020/12/2
 */
public enum BussTypeEnum implements IEnum<String> {

    AUCTION("EX001", "竞价交易"),
    BULK("EX002", "大宗交易"),
    EXERCISE("EX003", "行权"),
    DEBT_TO_EQUITY("EX004", "债转股"),
    STANDARD_COUPONS_IN_AND_OUT("EX005", "质押券出入库"),
    ALLOTMENT_DEBT("EX006", "配债"),
    CONVERTIBLE_BOND_SUBSCRIPTION("EX007", "可转债申购"),
    ALLOTMENT("EX008", "配股"),
    IPO_SUBSCRIPTION("EX009", "新股申购"),
    REPO_TO_FIRST_PHASE("EX010", "首期"),
    REPO_TO_ADVANCE_PHASE("EX011", "提前购回"),
    REPO_TO_MATURITY("EX012", "到期购回"),
    REPO_TO_RENEWAL("EX013", "到期续做"),
    REPO_TO_HALFWAY_EXCHANGE("EX014", "中途换券"),
    REPO_TO_RELEASE_PLEDGE("EX015", "解除质押"),
    AGREEMENT_COUPON("EX016", "协议现券"),
    COUPON("EX017", "现券交易"),
    UNIVERSAL_PLEDGE_REPURCHASE("EX018", "通用式质押回购"),
    CREDIT_PROTECTION_CERTIFICATE("EX019", "信用保护凭证"),
    ;

    private final String code;
    private final String description;

    BussTypeEnum(String code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}
