package com.joyintech.tams.valuation.common.interf.tempTables.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;

import java.math.BigDecimal;
import java.util.Date;

public class ValIrsInfo extends BaseEntity {
    /**
     * 利率互换交易编号
     */
    private String tradeId;

    /**
     * 新增、修改、删除
     */
    private String dataActionSign;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 名义本金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal prinAmt;

    /**
     * 起息日
     */
    private Date vdate;

    /**
     * 到期日
     */
    private Date mdate;

    /**
     * 期限品种
     */
    private String termKind;

    /**
     * 互换方式
     */
    private String irsType;



    /**
     * 本金收益特征
     */
    private String capiProfFeature;


    /**
     * 固定利率付息频率
     */
    private String fixPayCycle;

    /**
     * 固定利率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal fixRate;


    /**
     * 固定利率计息基础
     */
    private String fixBasis;

    /**
     * 固定利率首期起息日
     */
    private Date fixVdate;

    /**
     * 浮动挂钩利率
     */
    private String linkRate;

    /**
     * 利差
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal spreadRate;
    /**
     * 浮动利率计息基础
     */
    private String floatBasis;

    /**
     * 浮动利率付息频率
     */
    private String floatPayCycle;

    /**
     * 浮动利率首期起息日
     */
    private Date floatVdate;

    /**
     * 首期利率确定日
     */
    private Date floatComfimDate;

    /**
     * 利率重置频率
     */
    private String resetCycle;

    /**
     * 利率重置类型
     */
    private String resetType;

    /**
     * 交易对手
     */
    private String counterId;
    /**
     * 交易日历
     */
    private String calendarId;

    /**
     * 提前观察数量，所有重置类型都有
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal observeBefDay;

    /**
     * 提前观察单位，自然日、工作日、自然月等
     */
    private String observeBefUnit;


    public String getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getDataActionSign() {
        return dataActionSign;
    }

    public void setDataActionSign(String dataActionSign) {
        this.dataActionSign = dataActionSign;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    public Date getVdate() {
        return vdate;
    }

    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public String getTermKind() {
        return termKind;
    }

    public void setTermKind(String termKind) {
        this.termKind = termKind;
    }

    public String getIrsType() {
        return irsType;
    }

    public void setIrsType(String irsType) {
        this.irsType = irsType;
    }


    public String getCapiProfFeature() {
        return capiProfFeature;
    }

    public void setCapiProfFeature(String capiProfFeature) {
        this.capiProfFeature = capiProfFeature;
    }

    public String getFixPayCycle() {
        return fixPayCycle;
    }

    public void setFixPayCycle(String fixPayCycle) {
        this.fixPayCycle = fixPayCycle;
    }

    public BigDecimal getFixRate() {
        return fixRate;
    }

    public void setFixRate(BigDecimal fixRate) {
        this.fixRate = fixRate;
    }

    public String getFixBasis() {
        return fixBasis;
    }

    public void setFixBasis(String fixBasis) {
        this.fixBasis = fixBasis;
    }

    public Date getFixVdate() {
        return fixVdate;
    }

    public void setFixVdate(Date fixVdate) {
        this.fixVdate = fixVdate;
    }

    public String getLinkRate() {
        return linkRate;
    }

    public void setLinkRate(String linkRate) {
        this.linkRate = linkRate;
    }

    public BigDecimal getSpreadRate() {
        return spreadRate;
    }

    public void setSpreadRate(BigDecimal spreadRate) {
        this.spreadRate = spreadRate;
    }

    public String getFloatBasis() {
        return floatBasis;
    }

    public void setFloatBasis(String floatBasis) {
        this.floatBasis = floatBasis;
    }

    public String getFloatPayCycle() {
        return floatPayCycle;
    }

    public void setFloatPayCycle(String floatPayCycle) {
        this.floatPayCycle = floatPayCycle;
    }

    public Date getFloatVdate() {
        return floatVdate;
    }

    public void setFloatVdate(Date floatVdate) {
        this.floatVdate = floatVdate;
    }

    public Date getFloatComfimDate() {
        return floatComfimDate;
    }

    public void setFloatComfimDate(Date floatComfimDate) {
        this.floatComfimDate = floatComfimDate;
    }

    public String getResetCycle() {
        return resetCycle;
    }

    public void setResetCycle(String resetCycle) {
        this.resetCycle = resetCycle;
    }

    public String getResetType() {
        return resetType;
    }

    public void setResetType(String resetType) {
        this.resetType = resetType;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public BigDecimal getObserveBefDay() {
        return observeBefDay;
    }

    public void setObserveBefDay(BigDecimal observeBefDay) {
        this.observeBefDay = observeBefDay;
    }

    public String getObserveBefUnit() {
        return observeBefUnit;
    }

    public void setObserveBefUnit(String observeBefUnit) {
        this.observeBefUnit = observeBefUnit;
    }
}
