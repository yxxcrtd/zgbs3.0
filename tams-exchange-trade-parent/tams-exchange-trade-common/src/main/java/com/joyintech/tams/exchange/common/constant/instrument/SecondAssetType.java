package com.joyintech.tams.exchange.common.constant.instrument;

import com.joyintech.tams.exchange.common.enums.IEnum;

/**
 * @Author: think
 * @Description: 资产二级分类
 * @Date: Created in 16:37 2020/10/22
 */
public enum SecondAssetType implements IEnum<String> {
    GZ("B01", "国债"),
    DFZFZ("B02", "地方政府债"),
    YHPJ("B03", "央行票据"),
    TYCD("B04", "同业存单"),
    JRZ("B05", "金融债"),
    QYZ("B06", "企业债"),
    GSZ("B07", "公司债"),
    ZQPJ("B08", "中期票据"),
    DQRZZ("B09", "短期融资券"),
    DXGJ("B10", "定向工具"),
    GJJGZ("B11", "国际机构债"),
    ZFZCJGZ("B12", "政府支持机构债"),
    ZCZCZQ("B13", "资产支持证券"),
    KZZ("B14", "可转债"),
    ZJHZ("B15", "可交换债"),
    KFLZZCZ("B16", "可分离转债存债"),
    YXG("B17", "优先股"),
    HBJJ("F01", "货币基金"),
    FHBJJ("F02", "非货币基金");


    public final String value;
    public final String name;

    SecondAssetType(String value, String name) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getCode() {
        return value;
    }

    @Override
    public String getDescription() {
        return name;
    }
}
