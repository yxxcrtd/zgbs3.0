package com.joyintech.tams.exchange.service.trade.check;

import java.io.Serializable;

/**
 * 责任链Bean
 *
 * @author lifangpeng
 * @date 2022/4/1 14:11
 */
public class ChainBean implements Serializable {
    private static final long serialVersionUID = 1020716714264651361L;

    /**
     * 是否中断
     */
    private boolean isInterrupt;

    public boolean isInterrupt() {
        return isInterrupt;
    }

    public void setInterrupt(boolean interrupt) {
        isInterrupt = interrupt;
    }
}
