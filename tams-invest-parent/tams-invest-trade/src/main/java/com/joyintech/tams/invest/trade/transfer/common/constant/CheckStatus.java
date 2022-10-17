package com.joyintech.tams.invest.trade.transfer.common.constant;

/**
 * @author lifangpeng
 * @date 2022/8/20 11:23
 */
public enum CheckStatus {
    未对账(0),

    无需对账(1),

    对账一致(2),

    对账不一致(3),
    ;

    private int code;

    CheckStatus(int code) {
        this.code = code;
    }

    public static String getCheckStatusName(int code) {
        for (CheckStatus checkStatus : values()) {
            if (checkStatus.code == code) {
                return checkStatus.name();
            }
        }
        return null;
    }
}
