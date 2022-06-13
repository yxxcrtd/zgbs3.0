package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class MstStockLiquidDiscountInfo extends MstStockLiquidDiscountInfoKey {
    /**
     * 剩余年限
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal terms;

    /**
     * 标的股票最后交易日
     */
    private Date lastTradeDate;

    /**
     * 参照标的
     */
    private String referObject;

    /**
     * 预期波动率%
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal expVolatility;

    /**
     * 预期股利收益率%
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal expYield;

    /**
     * 标的股票公允价值
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal objFairValue;

    /**
     * 流动性折扣系数%
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal liquidDiscFactor;

    /**
     * 限售股估值（元）
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal limitValue;

    /**
     * 数据来源
     */
    private String dataSource;

    /**
     * 取得 剩余年限
     * @return 剩余年限
     */
    public BigDecimal getTerms() {
        return terms;
    }

    /**
     * 设置 剩余年限
     * @param terms 剩余年限
     */
    public void setTerms(BigDecimal terms) {
        this.terms = terms;
    }

    /**
     * 取得 标的股票最后交易日
     * @return 标的股票最后交易日
     */
    public Date getLastTradeDate() {
        return lastTradeDate;
    }

    /**
     * 设置 标的股票最后交易日
     * @param lastTradeDate 标的股票最后交易日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setLastTradeDate(Date lastTradeDate) {
        this.lastTradeDate = lastTradeDate;
    }

    /**
     * 取得 参照标的
     * @return 参照标的
     */
    public String getReferObject() {
        return referObject;
    }

    /**
     * 设置 参照标的
     * @param referObject 参照标的
     */
    public void setReferObject(String referObject) {
        this.referObject = referObject == null ? null : referObject.trim();
    }

    /**
     * 取得 预期波动率%
     * @return 预期波动率%
     */
    public BigDecimal getExpVolatility() {
        return expVolatility;
    }

    /**
     * 设置 预期波动率%
     * @param expVolatility 预期波动率%
     */
    public void setExpVolatility(BigDecimal expVolatility) {
        this.expVolatility = expVolatility;
    }

    /**
     * 取得 预期股利收益率%
     * @return 预期股利收益率%
     */
    public BigDecimal getExpYield() {
        return expYield;
    }

    /**
     * 设置 预期股利收益率%
     * @param expYield 预期股利收益率%
     */
    public void setExpYield(BigDecimal expYield) {
        this.expYield = expYield;
    }

    /**
     * 取得 标的股票公允价值
     * @return 标的股票公允价值
     */
    public BigDecimal getObjFairValue() {
        return objFairValue;
    }

    /**
     * 设置 标的股票公允价值
     * @param objFairValue 标的股票公允价值
     */
    public void setObjFairValue(BigDecimal objFairValue) {
        this.objFairValue = objFairValue;
    }

    /**
     * 取得 流动性折扣系数%
     * @return 流动性折扣系数%
     */
    public BigDecimal getLiquidDiscFactor() {
        return liquidDiscFactor;
    }

    /**
     * 设置 流动性折扣系数%
     * @param liquidDiscFactor 流动性折扣系数%
     */
    public void setLiquidDiscFactor(BigDecimal liquidDiscFactor) {
        this.liquidDiscFactor = liquidDiscFactor;
    }

    /**
     * 取得 限售股估值（元）
     * @return 限售股估值（元）
     */
    public BigDecimal getLimitValue() {
        return limitValue;
    }

    /**
     * 设置 限售股估值（元）
     * @param limitValue 限售股估值（元）
     */
    public void setLimitValue(BigDecimal limitValue) {
        this.limitValue = limitValue;
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