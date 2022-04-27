package com.joyintech.tams.exchange.common.dto;

import java.math.BigDecimal;

/**
 * @author lifangpeng
 * @date 2022/4/26 16:42
 */
public class EntrustPriceAndYieldDto {

    private int round;
    private BigDecimal price;
    private long entrustVolume;

    private BigDecimal netPrice;
    private BigDecimal fullPrice;
    private BigDecimal yield;

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public long getEntrustVolume() {
        return entrustVolume;
    }

    public void setEntrustVolume(long entrustVolume) {
        this.entrustVolume = entrustVolume;
    }

    public BigDecimal getNetPrice() {
        return netPrice;
    }

    public void setNetPrice(BigDecimal netPrice) {
        this.netPrice = netPrice;
    }

    public BigDecimal getFullPrice() {
        return fullPrice;
    }

    public void setFullPrice(BigDecimal fullPrice) {
        this.fullPrice = fullPrice;
    }

    public BigDecimal getYield() {
        return yield;
    }

    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }
}
