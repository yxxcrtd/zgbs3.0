package com.joyintech.tams.exchange.common.constant;

import com.joyintech.framework.util.StringUtil;

/**
 * @author lifangpeng
 * @date 2022/5/12 20:36
 */
public enum EntrustSide {
    BUY("T01", "买入", "1", "2"),
    SELL("T02", "卖出", "2", "1"),
    ;

    private String code;
    private String name;
    private String entrustBs;
    private String opEntrustBs;

    EntrustSide(String code, String name, String entrustBs, String opEntrustBs) {
        this.code = code;
        this.name = name;
        this.entrustBs = entrustBs;
        this.opEntrustBs = opEntrustBs;
    }

    public static String getOpEntrustBs(String code) {
        for (EntrustSide side : values()) {
            if (StringUtil.equals(side.getCode(), code)) {
                return side.getOpEntrustBs();
            }
        }

        return "";
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEntrustBs() {
        return entrustBs;
    }

    public void setEntrustBs(String entrustBs) {
        this.entrustBs = entrustBs;
    }

    public String getOpEntrustBs() {
        return opEntrustBs;
    }

    public void setOpEntrustBs(String opEntrustBs) {
        this.opEntrustBs = opEntrustBs;
    }
}
