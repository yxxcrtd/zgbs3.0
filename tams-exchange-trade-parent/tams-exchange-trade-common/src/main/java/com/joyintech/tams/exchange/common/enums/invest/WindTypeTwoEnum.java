package com.joyintech.tams.exchange.common.enums.invest;

import com.joyintech.tams.exchange.common.enums.IEnum;


/**
 * WIND二级分类枚举
 *
 * @author swwan
 * @date 2022/4/24 - 22:05
 * @see com.joyintech.tams.constant.DictCode#WIND二级分类_含债券二级分类等
 */
@SuppressWarnings("all")
public enum WindTypeTwoEnum implements IEnum<String> {

    WIND_TWO_ZCYHZ("01", "政策银行债"),
    WIND_TWO_SYYHZ("02", "商业银行债"),
    WIND_TWO_SYYHCJZQ("03", "商业银行次级债券"),
    WIND_TWO_BXGSZ("04", "保险公司债"),
    WIND_TWO_ZQGSZ("05", "证券公司债"),
    WIND_TWO_ZQGSDQRZQ("06", "证券公司短期融资券"),
    WIND_TWO_QTJRJGZ("07", "其它金融机构债"),
    WIND_TWO_YBQYZ("08", "一般企业债"),
    WIND_TWO_JHQYZ("09", "集合企业债"),
    WIND_TWO_YBGSZ("10", "一般公司债"),
    WIND_TWO_SMZ("11", "私募债"),
    WIND_TWO_YBZQPJ("12", "一般中期票据"),
    WIND_TWO_JHPJ("13", "集合票据"),
    WIND_TWO_CDQRZZQ("14", "超短期融资债券"),
    WIND_TWO_YBDQRZQ("15", "一般短期融资券"),
    WIND_TWO_ZJHZGABS("16", "证监会主管ABS"),
    WIND_TWO_YJHZGABS("17", "银监会主管ABS"),
    WIND_TWO_JYSXHABN("18", "交易商协会ABN"),
    ;

    public String code;
    public String description;

    WindTypeTwoEnum(String code, String description) {
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
