package com.joyintech.tams.exchange.common.enums.invest;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * @author swwan
 * @date 2022/4/26 - 17:05
 */
public enum PsEnum implements IEnum<String> {

    POSITIVE_REPO("03", "正回购"),
    REVERSED_REPO("04", "逆回购"),

    BUY("T01", "买入"),
    SELL("T02", "卖出"),

    RESALE("T03", "行权（回售）"),
    APPLY("T04", "申购"),
    ALLOCATE_SHARE("T46", "配股"),
    BOND_STOCK("T49", "债转股"),
    SEAL("T68", "质押入库"),
    UNSEAL("T69", "质押出库"),
    ALLOCATE_BOND("T97", "配债"),

    REPO_RENEW("T56", "到期续做"),
    REPO_UNSTAKE("T93", "解除质押"),
    REPO_EXCHANGE_COUPONS_MIDWAY("T94", "中途换券"),
    REPO_MATURITY("T95", "到期购回"),
    REPO_ADVANCE("T96", "提前购回"),

    OPEN_LONG("TF01", "开多"),
    CLOSE_SHORT("TF02", "平空"),
    CLOSE_TODAY_SHORT("TF03", "平今空"),
    OPENS_HORT("TF04", "开空"),
    CLOSE_LONG("TF05", "平多"),
    CLOSE_TODAY_LONG("TF06", "平今多"),

    FINANCE_BUY("T104", "融资买入"),
    SECURITIES_LOAN_SELL("T105", "融券卖出"),
    VOTE("T107", "网络投票"),
    BONUS("T110", "分红设置"),
    ;

    public final String description;
    public final String code;

    PsEnum(String code, String description) {
        this.description = description;
        this.code = code;
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
