package com.joyintech.tams.exchange.common.constant;

import com.joyintech.tams.exchange.common.enums.IEnum;

import java.util.HashMap;
import java.util.Map;

public enum EntrustStatus implements IEnum<Integer> {

    CREATED("新建", -10),
    PENDING_APPROVAL("待审批", -9),
    PENDING_REVIEW("待复核", -8),
    PENDING_REVOKE("待撤审", -6),
    UNREPORTED("未报", 0),
    TOBE_REPORTED("待报", 1),
    REPORTED("已报", 2),
    REPORTED_TOBE_CANCEL("已报待撤", 3),
    PART_TRADED_TOBE_CANCELED("部成待撤", 4),
    PART_SUCCESS("部成", 5),
    PART_TRADED_CANCELED("部撤", 6),
    CANCELED("已撤", 7),
    SUCCEEDED("已成", 8),
    INVALID("柜台废单", 9),
    SYSTEM_INVALID("内部废单", 10),
    ABNORMAL_CANCELED("异常已撤", 11),
    NOT_APPROVAL("审批拒绝", 12),
    NOT_REVIEW("复核拒绝", 13),
    NOT_REVOKE("撤销拒绝", 14),
    NOT_RISK("风控拒绝", 15);

    public final int value;
    public final String name;

    private static Map<Integer, String> entrustStatusMap = new HashMap<>();

    static {
        entrustStatusMap.put(CREATED.value, CREATED.name);
        entrustStatusMap.put(PENDING_APPROVAL.value, PENDING_APPROVAL.name);
        entrustStatusMap.put(PENDING_REVIEW.value, PENDING_REVIEW.name);
        entrustStatusMap.put(PENDING_REVOKE.value, PENDING_REVOKE.name);
        entrustStatusMap.put(UNREPORTED.value, UNREPORTED.name);
        entrustStatusMap.put(TOBE_REPORTED.value, TOBE_REPORTED.name);
        entrustStatusMap.put(REPORTED.value, REPORTED.name);
        entrustStatusMap.put(REPORTED_TOBE_CANCEL.value, REPORTED_TOBE_CANCEL.name);
        entrustStatusMap.put(PART_TRADED_TOBE_CANCELED.value, PART_TRADED_TOBE_CANCELED.name);
        entrustStatusMap.put(PART_SUCCESS.value, PART_SUCCESS.name);
        entrustStatusMap.put(PART_TRADED_CANCELED.value, PART_TRADED_CANCELED.name);
        entrustStatusMap.put(CANCELED.value, CANCELED.name);
        entrustStatusMap.put(SUCCEEDED.value, SUCCEEDED.name);
        entrustStatusMap.put(INVALID.value, INVALID.name);
        entrustStatusMap.put(SYSTEM_INVALID.value, SYSTEM_INVALID.name);
        entrustStatusMap.put(ABNORMAL_CANCELED.value, ABNORMAL_CANCELED.name);
        entrustStatusMap.put(NOT_APPROVAL.value, NOT_APPROVAL.name);
        entrustStatusMap.put(NOT_REVIEW.value, NOT_REVIEW.name);
        entrustStatusMap.put(NOT_REVOKE.value, NOT_REVOKE.name);
        entrustStatusMap.put(NOT_RISK.value, NOT_RISK.name);
    }

    EntrustStatus(String name, int value) {
        this.name = name;
        this.value = value;
    }

    // 是否处于审批中状态
    public static boolean isPendingStatus(int entrustStatus) {
        return entrustStatus < EntrustStatus.PENDING_REVOKE.value && entrustStatus > EntrustStatus.CREATED.value;
    }

    // 是否为在途委托
    public static boolean isOnOrder(int entrustStatus) {
        return EntrustStatus.PENDING_REVOKE.value < entrustStatus
                && EntrustStatus.PART_TRADED_CANCELED.value > entrustStatus;
    }

    public static boolean isFinished(int status) {
        return status > PART_SUCCESS.value;
    }

    // 判断是否为撤单未完成的委托
    public static boolean isUnCompleteCancelEntrust(int entrustStatus) {
        return entrustStatus > TOBE_REPORTED.value && entrustStatus < PART_TRADED_CANCELED.value;
    }

    // 判断是否为异常委托
    public static boolean isExceptionEntrust(int status) {
        return status == TOBE_REPORTED.value
                || status == REPORTED_TOBE_CANCEL.value
                || status == PART_TRADED_TOBE_CANCELED.value;
    }

    public static String getEntrustStatusName(int entrustStatus) {
        return entrustStatusMap.get(entrustStatus);
    }

    @Override
    public Integer getCode() {
        return value;
    }

    @Override
    public String getDescription() {
        return name;
    }
}
