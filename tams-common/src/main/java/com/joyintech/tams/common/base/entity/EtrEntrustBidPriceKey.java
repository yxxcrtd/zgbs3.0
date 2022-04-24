package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import java.math.BigDecimal;

public class EtrEntrustBidPriceKey extends BaseEntity {
    /**
     * 委托代码
     */
    private String entrustId;

    /**
     * 投标标位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal bidPrice;

    /**
     * 组合ID
     */
    private String portfolioId;

    /**
     * 取得 委托代码
     * @return 委托代码
     */
    public String getEntrustId() {
        return entrustId;
    }

    /**
     * 设置 委托代码
     * @param entrustId 委托代码
     */
    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId == null ? null : entrustId.trim();
    }

    /**
     * 取得 投标标位
     * @return 投标标位
     */
    public BigDecimal getBidPrice() {
        return bidPrice;
    }

    /**
     * 设置 投标标位
     * @param bidPrice 投标标位
     */
    public void setBidPrice(BigDecimal bidPrice) {
        this.bidPrice = bidPrice;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }
}