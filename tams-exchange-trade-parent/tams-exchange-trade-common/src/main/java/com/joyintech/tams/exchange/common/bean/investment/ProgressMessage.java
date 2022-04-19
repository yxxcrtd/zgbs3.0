package com.joyintech.tams.exchange.common.bean.investment;

import com.joyintech.tams.exchange.common.db.domain.Instruction;

import java.math.BigDecimal;

public class ProgressMessage extends BasicMessage {
    private static final long serialVersionUID = 8657021646470132189L;
    private String entrustId;       // 委托代码
    private BigDecimal frozenBalance;       // 剩余未完成委托金额
    private BigDecimal tradedBalance;     // 已成交金额

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public BigDecimal getFrozenBalance() {
        return frozenBalance;
    }

    public void setFrozenBalance(BigDecimal frozenBalance) {
        this.frozenBalance = frozenBalance;
    }

    public BigDecimal getTradedBalance() {
        return tradedBalance;
    }

    public void setTradedBalance(BigDecimal tradedBalance) {
        this.tradedBalance = tradedBalance;
    }

    private static ProgressMessage templateMessage = new ProgressMessage();

    public static ProgressMessage createTradeMessage(Instruction instruction) {
        ProgressMessage tradeMessage;
        try {
            tradeMessage = (ProgressMessage) templateMessage.clone();
            tradeMessage.setEntrustId(instruction.getEntrustId());
            tradeMessage.setFrozenBalance(instruction.getFrozenQuantity());
            tradeMessage.setTradedBalance(instruction.getEntrustLeftAmt());
        } catch (CloneNotSupportedException e) {
            return null;
        }
        return tradeMessage;
    }

    ;
}
