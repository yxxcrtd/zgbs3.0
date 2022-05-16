package com.joyintech.tams.exchange.common.constant;

import com.joyintech.tams.exchange.common.enums.IEnum;

import com.joyintech.framework.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 交易主体类型
 *
 * @author lifangpeng
 * @date 2022/4/24 18:44
 */
public enum InvestorType implements IEnum<String> {

    SELF_SUPPORT("自营", "01"),
    ASSET_MANAGEMENT("资管", "02"),
    PARTY_BROKERAGE("机构经纪", "03"),
    PERSONAL_BROKERAGE("个人经纪", "04"),
    ;

    private static Map<String, InvestorType> REQUEST_STATUS_MAP = new HashMap<>();

    static {
        REQUEST_STATUS_MAP.put(SELF_SUPPORT.value, SELF_SUPPORT);
        REQUEST_STATUS_MAP.put(ASSET_MANAGEMENT.value, ASSET_MANAGEMENT);
        REQUEST_STATUS_MAP.put(PARTY_BROKERAGE.value, PARTY_BROKERAGE);
        REQUEST_STATUS_MAP.put(PERSONAL_BROKERAGE.value, PERSONAL_BROKERAGE);
    }

    private String name;
    private String value;

    InvestorType(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static String getInvestorTypeName(String value) {
        if(StringUtil.isEmpty(value)){
            return value;
        }
        return REQUEST_STATUS_MAP.get(value).getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
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
