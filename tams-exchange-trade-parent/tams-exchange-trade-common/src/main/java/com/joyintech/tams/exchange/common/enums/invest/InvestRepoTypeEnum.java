package com.joyintech.tams.exchange.common.enums.invest;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * 回购类型枚举
 *
 * @author swwan
 * @date 2022/4/24 - 21:43
 * @see com.joyintech.tams.constant.DictCode#回购类型
 */
@SuppressWarnings("all")
public enum InvestRepoTypeEnum implements IEnum<String> {

    NEGOTIATED_REPO("质押式协议回购", "H01"),
    BIDDING_REPO("质押式竞价回购", "H02"),
    ;

    public String code;
    public String description;

    InvestRepoTypeEnum(String description, String code) {
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