package com.joyintech.tams.exchange.common.enums.exchange;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * 协议回购类型枚举
 *
 * @author swwan
 * @date 2022/6/6 - 16:28
 */
public enum RepurchaseTypeEnum implements IEnum<String> {

    首期("F", "首期", "EX010"),
    提前购回("Z", "提前购回", "EX011"),
    到期购回("", "到期购回", "EX012"),
    到期续做("X", "到期续做", "EX013"),
    中途换券("H", "中途换券", "EX014"),
    解除质押("C", "解除质押", "EX015"),
    ;

    private final String code;
    private final String description;
    private final String busiTypeTgCode;

    RepurchaseTypeEnum(String code, String description, String busiTypeTgCode) {
        this.code = code;
        this.description = description;
        this.busiTypeTgCode = busiTypeTgCode;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public String getBusiTypeTgCode() {
        return busiTypeTgCode;
    }
}
