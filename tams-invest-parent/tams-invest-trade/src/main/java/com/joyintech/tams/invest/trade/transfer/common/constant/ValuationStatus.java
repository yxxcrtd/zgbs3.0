package com.joyintech.tams.invest.trade.transfer.common.constant;

/**
 * @author lifangpeng
 * @date 2022/8/20 11:33
 */
public enum ValuationStatus {
    未确认(0),

    已确认(1),

    ;

    private int code;

    ValuationStatus(int code) {
        this.code = code;
    }

    public static String getValuationStatusName(int code) {
        for (ValuationStatus valuationStatus : values()) {
            if (valuationStatus.code == code) {
                return valuationStatus.name();
            }
        }
        return null;
    }
}
