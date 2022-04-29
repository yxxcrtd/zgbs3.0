package com.joyintech.tams.valuation.common.interf.tempTables.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @ClassName ValStockLiquidDiscountInfo
 * @Description:
 * @Author weipeng
 * @Date 2022-04-28
 * @Version V1.0
 **/
public class ValStockLiquidDiscountInfo extends BaseEntity {

    /**
     * 股票代码:市场代码
     */
    private String sharesId;
    /**
     * 估值日期
     **/
    private Date valDate;
    /**
     * 预计上市流通日
     */
    private Date expCircDate;
    /**
     * 剩余年限
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
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
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal expVolatility;

    /**
     * 预期股利收益率%
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal expYield;

    /**
     * 标的股票公允价值
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal objFairValue;

    /**
     * 流动性折扣系数%
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal liquidDiscFactor;

    /**
     * 限售股估值（元）
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal limitValue;

    /**
     * 行情来源
     */
    private String dataSource;

    public String getSharesId() {
        return sharesId;
    }

    public void setSharesId(String sharesId) {
        this.sharesId = sharesId;
    }

    public Date getValDate() {
        return valDate;
    }

    public void setValDate(Date valDate) {
        this.valDate = valDate;
    }

    public Date getExpCircDate() {
        return expCircDate;
    }

    public void setExpCircDate(Date expCircDate) {
        this.expCircDate = expCircDate;
    }

    public BigDecimal getTerms() {
        return terms;
    }

    public void setTerms(BigDecimal terms) {
        this.terms = terms;
    }

    public Date getLastTradeDate() {
        return lastTradeDate;
    }

    public void setLastTradeDate(Date lastTradeDate) {
        this.lastTradeDate = lastTradeDate;
    }

    public String getReferObject() {
        return referObject;
    }

    public void setReferObject(String referObject) {
        this.referObject = referObject;
    }

    public BigDecimal getExpVolatility() {
        return expVolatility;
    }

    public void setExpVolatility(BigDecimal expVolatility) {
        this.expVolatility = expVolatility;
    }

    public BigDecimal getExpYield() {
        return expYield;
    }

    public void setExpYield(BigDecimal expYield) {
        this.expYield = expYield;
    }

    public BigDecimal getObjFairValue() {
        return objFairValue;
    }

    public void setObjFairValue(BigDecimal objFairValue) {
        this.objFairValue = objFairValue;
    }

    public BigDecimal getLiquidDiscFactor() {
        return liquidDiscFactor;
    }

    public void setLiquidDiscFactor(BigDecimal liquidDiscFactor) {
        this.liquidDiscFactor = liquidDiscFactor;
    }

    public BigDecimal getLimitValue() {
        return limitValue;
    }

    public void setLimitValue(BigDecimal limitValue) {
        this.limitValue = limitValue;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}
