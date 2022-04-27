package com.joyintech.tams.exchange.common.enums.invest;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * WIND一级分类枚举
 *
 * @author swwan
 * @date 2022/4/24 - 22:05
 * @see com.joyintech.tams.constant.DictCode#WIND一级分类_含债券一级分类等
 */
@SuppressWarnings("all")
public enum WindTypeOneEnum implements IEnum<String> {

    GZ("B01", "国债"),
    DFZFZ("B02", "地方政府债"),
    YHPJ("B03", "央行票据"),
    TYCD("B04", "同业存单"),
    JRZ("B05", "金融债"),
    QYZ("B06", "企业债"),
    GSZ("B07", "公司债"),
    ZQPJ("B08", "中期票据"),
    DQRZQ("B09", "短期融资券"),
    DXGJ("B10", "定向工具"),
    GJJGZ("B11", "国际机构债"),
    ZFZCJGZ("B12", "政府支持机构债"),
    ZCZCZQ("B13", "资产支持证券"),
    KZZ("B14", "可转债"),
    KJHZ("B15", "可交换债"),
    KFLZZCZ("B16", "可分离转债存债"),
    YXG("B17", "优先股"),
    ;

    public String code;
    public String description;

    WindTypeOneEnum(String code, String description) {
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
