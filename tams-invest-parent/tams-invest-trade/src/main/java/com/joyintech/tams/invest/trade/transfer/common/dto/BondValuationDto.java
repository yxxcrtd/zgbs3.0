package com.joyintech.tams.invest.trade.transfer.common.dto;

import java.math.BigDecimal;

/**
 * @author lifangpeng
 * @date 2022/9/2 14:48
 */
public class BondValuationDto {

    /**
     * 债券代码，市场代码
     */
    private String finprodId;

    /**
     * 每日估值
     */
    public BigDecimal valuation;

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public BigDecimal getValuation() {
        return valuation;
    }

    public void setValuation(BigDecimal valuation) {
        this.valuation = valuation;
    }
}
