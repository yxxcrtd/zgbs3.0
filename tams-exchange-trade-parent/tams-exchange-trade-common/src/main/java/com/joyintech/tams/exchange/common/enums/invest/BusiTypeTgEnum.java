package com.joyintech.tams.exchange.common.enums.invest;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * 投管业务类型枚举
 *
 * @author swwan
 * @date 2022/4/24 - 21:59
 * @see com.joyintech.tams.constant.DictCode#投管业务类型
 */
@SuppressWarnings("all")
public enum BusiTypeTgEnum implements IEnum<String> {

    竞价交易("EX001","竞价交易"),
    大宗交易("EX002","大宗交易"),
    行权("EX003","行权"),
    债转股("EX004","债转股"),
    质押券出入库("EX005","质押券出入库"),
    配债("EX006","配债"),
    可转债申购("EX007","可转债申购"),
    配股("EX008","配股"),
    新股申购("EX009","新股申购"),
    首期("EX010","首期"),
    提前购回("EX011","提前购回"),
    到期购回("EX012","到期购回"),
    到期续做("EX013","到期续做"),
    中途换券("EX014","中途换券"),
    解除质押("EX015","解除质押"),
    协议现券("EX016","协议现券"),
    现券交易("EX017","现券交易"),
    通用式质押回购("EX018","通用式质押回购"),
    信用保护凭证("EX019","信用保护凭证"),
    ;

    public final String code;
    public final String description;

    BusiTypeTgEnum(String code, String description) {
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
