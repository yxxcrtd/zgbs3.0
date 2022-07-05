package com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.enums;


import com.joyintech.tams.constant.DictCode;

/**
 * 协议回购类型枚举
 *
 * @author meifaxin
 * @date 2022/6/15 - 9:54
 */
public enum RepurchaseTypeEnum implements IEnum<String> {

    首期("F", "首期", "T92"),
    提前购回("Z", "提前购回", "T96"),
    到期购回("", "到期购回", "T95"),
    到期续做("X", "到期续做", "T56"),
    中途换券("H", "中途换券", "T94"),
    解除质押("C", "解除质押", "T93"),
    ;


    private final String code;
    private final String description;
    private final String ps;

    RepurchaseTypeEnum(String code, String description, String ps) {
        this.code = code;
        this.description = description;
        this.ps = ps;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public String getPs() {
        return ps;
    }
}
