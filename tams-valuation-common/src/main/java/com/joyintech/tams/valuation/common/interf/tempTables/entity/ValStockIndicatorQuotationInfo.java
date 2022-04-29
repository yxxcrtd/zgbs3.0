package com.joyintech.tams.valuation.common.interf.tempTables.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName ValStockIndicatorQuotationInfo
 * @Description: 股票指数行情接口
 * @Author weipeng
 * @Date 2022-04-28
 * @Version V1.0
 **/
public class ValStockIndicatorQuotationInfo extends BaseEntity {

    /**股票代码:市场代码**/
    private String indicatorCode;
    /**交易日**/
    private Date dealDate;
    /** 币种 **/
    private String ccy;
    /**前收盘价**/
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal preClosePrice;
    /**开盘价**/
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal openPrice;
    /**最高价**/
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal highPrice;
    /**最低价**/
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lowPrice;
    /**收盘价**/
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal closePrice;
    /**数据来源**/
    private String dataSource;

    public String getIndicatorCode() {
        return indicatorCode;
    }

    public void setIndicatorCode(String indicatorCode) {
        this.indicatorCode = indicatorCode;
    }

    public Date getDealDate() {
        return dealDate;
    }

    public void setDealDate(Date dealDate) {
        this.dealDate = dealDate;
    }

    public BigDecimal getPreClosePrice() {
        return preClosePrice;
    }

    public void setPreClosePrice(BigDecimal preClosePrice) {
        this.preClosePrice = preClosePrice;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }

    public BigDecimal getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(BigDecimal highPrice) {
        this.highPrice = highPrice;
    }

    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(BigDecimal lowPrice) {
        this.lowPrice = lowPrice;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}
