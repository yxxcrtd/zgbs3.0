package com.joyintech.tams.exchange.common.dto;

import java.math.BigDecimal;

/**
 * @author lifangpeng
 * @date 2022/6/30 18:35
 */
public class PositionCashSecRespDto {
    private BigDecimal amount;
    private String field;
    private String name;
    private String type;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
