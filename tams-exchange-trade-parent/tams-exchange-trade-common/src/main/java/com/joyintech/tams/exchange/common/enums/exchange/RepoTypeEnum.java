package com.joyintech.tams.exchange.common.enums.exchange;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * 协议回购类型枚举
 *
 * @author swwan
 * @date 2022/6/6 - 16:28
 */
public enum RepoTypeEnum implements IEnum<String> {

    FIRST_PHASE("01", "首期"),
    REPO_ADVANCE("02", "提前购回"),
    REPO_EXCHANGE_COUPONS_MIDWAY("03", "中途换券"),
    REPO_RENEW("04", "到期续做"),
    REPO_MATURITY("05", "到期购回"),
    REPO_UNSTAKE("06", "解除质押"),
    ;

    private final String code;
    private final String description;

    RepoTypeEnum(String code, String description) {
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
