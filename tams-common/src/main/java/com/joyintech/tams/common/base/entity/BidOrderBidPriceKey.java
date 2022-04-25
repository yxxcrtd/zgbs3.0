package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import java.math.BigDecimal;

public class BidOrderBidPriceKey extends BaseEntity {
    /**
     * 投标编号
     */
    private String bidId;

    /**
     * 委托代码
     */
    private String entrustId;

    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 投标标位
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal bidPrice;

    /**
     * 投标编号
     *
     * @return BID_ID 投标编号
     */
    public String getBidId() {
        return bidId;
    }

    /**
     * 投标编号
     *
     * @param bidId 投标编号
     */
    public void setBidId(String bidId) {
        this.bidId = bidId == null ? null : bidId.trim();
    }

    /**
     * 委托代码
     *
     * @return ENTRUST_ID 委托代码
     */
    public String getEntrustId() {
        return entrustId;
    }

    /**
     * 委托代码
     *
     * @param entrustId 委托代码
     */
    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId == null ? null : entrustId.trim();
    }

    /**
     * 投标标位
     *
     * @return BID_PRICE 投标标位
     */
    public BigDecimal getBidPrice() {
        return bidPrice;
    }

    /**
     * 投标标位
     *
     * @param bidPrice 投标标位
     */
    public void setBidPrice(BigDecimal bidPrice) {
        this.bidPrice = bidPrice;
    }

    /**
     * 组合代码
     *
     * @return PORTFOLIO_ID 组合代码
     */
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * 组合代码
     *
     * @param portfolioId 组合代码
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId == null ? null : portfolioId.trim();
    }
}