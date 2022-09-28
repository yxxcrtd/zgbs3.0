package com.joyintech.tams.invest.trade.transfer.common.dto;

import java.math.BigDecimal;

/**
 * @author lifangpeng
 * @date 2022/9/2 14:37
 */
public class DeviationDegreeDto {
    /**
     * 金融产品代码
     */
    private String finprodId;
    /**
     * 交易场所
     */
    private String tradePlace;
    /**
     * 单位净值
     */
    private BigDecimal unitPrice;
    /**
     * 偏离度
     */
    private BigDecimal deviationDegree;

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getTradePlace() {
        return tradePlace;
    }

    public void setTradePlace(String tradePlace) {
        this.tradePlace = tradePlace;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getDeviationDegree() {
        return deviationDegree;
    }

    public void setDeviationDegree(BigDecimal deviationDegree) {
        this.deviationDegree = deviationDegree;
    }
}
