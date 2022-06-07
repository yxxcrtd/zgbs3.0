package com.joyintech.tams.exchange.common.constant.instrument;

/**
 * @Author: think
 * @Description: 证券子类
 * @Date: Created in 14:01 2020/10/21
 */
public enum SubStockType {
    ZCYHZ("B001", "政策银行债"),
    SYYHZ("B002", "商业银行债"),
    SYYHCJZQ("B003", "商业银行次级债券"),
    BXGSZ("B004", "保险公司债"),
    ZQGSZ("B005", "证券公司债"),
    ZQGSDQRZQ("B006", "证券公司短期融资券"),
    QTJRJGZ("B007", "其它金融机构债"),
    YBQYZ("B008", "一般企业债"),
    JHQYZ("B009", "集合企业债"),
    GZ("B01", "国债"),
    YBGSZ("B010", "一般公司债"),
    SMZ("B011", "私募债"),
    YBZQPJ("B012", "一般中期票据"),
    JHPJ("B013", "集合票据"),
    CDQRZZQ("B014", "超短期融资债券"),
    YBDQRZQ("B015", "一般短期融资券"),
    ZJHZGABS("B016", "证监会主管ABS"),
    YJHZBABS("B017", "银监会主管ABS"),
    JYSXHABN("B018", "交易商协会ABN"),
    DFZFZ("B02", "地方政府债"),
    YHPJ("B03", "央行票据"),
    TYCD("B04", "同业存单"),
    DXGJ("B10", "定向工具"),
    GJJGZ("B11", "国际机构债"),
    ZFZCJGZ("B12", "政府支持机构债"),
    KZZ("B14", "可转债"),
    KJHZ("B15", "可交换债"),
    KFLZZCZ("B16", "可分离转债存债"),
    YXG("B17", "优先股"),
    ZQJJ("F001", "债券基金"),
    GPJJ("F003", "股票基金"),
    HHJJ("F004", "混合基金"),
    QTGMJJ("F006", "其他公募基金"),
    JJZJJ("F007", "基金中基金"),
    HBJJ("F01", "货币基金"),
    ZB("BD01", "主板"),
    ZXQYB("BD02", "中小企业板"),
    KCB("BD03", "科创板"),
    CYB("BD04", "创业板");

    public final String value;
    public final String name;

    SubStockType(String value, String name) {
        this.value = value;
        this.name = name;
    }
}
