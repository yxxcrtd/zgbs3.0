package com.joyintech.tams.exchange.common.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 请求状态
 *
 * @author lifangpeng
 * @date 2022/4/24 18:31
 */
public enum RequestStatus {

    WAIT_CONFIRM("待确认", 1),
    REPLIED("已回复", 2),
    CANCELED("已撤销", 3),
    DEALED("已成交", 4),
    OTHER("其他", 0),
    ;

    private static Map<Integer, RequestStatus> REQUEST_STATUS_MAP = new HashMap<>();

    static {
        REQUEST_STATUS_MAP.put(WAIT_CONFIRM.value, WAIT_CONFIRM);
        REQUEST_STATUS_MAP.put(REPLIED.value, REPLIED);
        REQUEST_STATUS_MAP.put(CANCELED.value, CANCELED);
        REQUEST_STATUS_MAP.put(DEALED.value, DEALED);
        REQUEST_STATUS_MAP.put(OTHER.value, OTHER);
    }

    private String name;
    private int value;

    RequestStatus(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public static String getRequestStatusName(int value) {
        return REQUEST_STATUS_MAP.get(value).getName();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
