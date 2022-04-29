package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import java.math.BigDecimal;

public class MstIndicatorQuotation extends MstIndicatorQuotationKey {
    /**
     * 前收盘价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal preClosePrice;

    /**
     * 开盘价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal openPrice;

    /**
     * 最高价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal highPrice;

    /**
     * 最低价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lowPrice;

    /**
     * 收盘价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal closePrice;

    /**
     * 数据来源
     */
    private String dataSource;

    /**
     * 取得 前收盘价
     * @return 前收盘价
     */
    public BigDecimal getPreClosePrice() {
        return preClosePrice;
    }

    /**
     * 设置 前收盘价
     * @param preClosePrice 前收盘价
     */
    public void setPreClosePrice(BigDecimal preClosePrice) {
        this.preClosePrice = preClosePrice;
    }

    /**
     * 取得 开盘价
     * @return 开盘价
     */
    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    /**
     * 设置 开盘价
     * @param openPrice 开盘价
     */
    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }

    /**
     * 取得 最高价
     * @return 最高价
     */
    public BigDecimal getHighPrice() {
        return highPrice;
    }

    /**
     * 设置 最高价
     * @param highPrice 最高价
     */
    public void setHighPrice(BigDecimal highPrice) {
        this.highPrice = highPrice;
    }

    /**
     * 取得 最低价
     * @return 最低价
     */
    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    /**
     * 设置 最低价
     * @param lowPrice 最低价
     */
    public void setLowPrice(BigDecimal lowPrice) {
        this.lowPrice = lowPrice;
    }

    /**
     * 取得 收盘价
     * @return 收盘价
     */
    public BigDecimal getClosePrice() {
        return closePrice;
    }

    /**
     * 设置 收盘价
     * @param closePrice 收盘价
     */
    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }

    /**
     * 取得 数据来源
     * @return 数据来源
     */
    public String getDataSource() {
        return dataSource;
    }

    /**
     * 设置 数据来源
     * @param dataSource 数据来源
     */
    public void setDataSource(String dataSource) {
        this.dataSource = dataSource == null ? null : dataSource.trim();
    }
}