package com.joyintech.tams.invest.common.transmatch.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.common.base.entity.EtrEntrust;

import java.math.BigDecimal;
import java.util.List;

public class ExchangeTransMatchCommonEntrustDto extends EtrEntrust {
    /**
     * 已匹配份额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal marchQty;

    /**
     * 已匹配金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal marchAmt;

    /**
     * 已分配随交易费用
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal marchFeeAmtPay;

    /**
     * 已分配不随交易费用
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal marchFeeAmtUnPay;

    /**
     * 指令已匹配份额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal marchQtyEn;

    /**
     * 指令已匹配金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal marchAmtEn;

    /**
     * 目标类型
     */
    private String targetType;

    /**
     * 剩余成交金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal remainEntrustAmt;

    /**
     * 剩余成交数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal remainEntrustNum;

    /**
     * 本方交易员
     */
    private String trader;

    /**
     * 是否是最后一笔匹配委托
     */
    private boolean lastEntrust;


    /**
     * 指令多组合明细list
     */
    private List<ExchangeTransMatchCommonEntrustDetailDto> entrustDetailDtoList;

    public BigDecimal getMarchQty() {
        return marchQty;
    }

    public void setMarchQty(BigDecimal marchQty) {
        this.marchQty = marchQty;
    }

    public BigDecimal getMarchAmt() {
        return marchAmt;
    }

    public void setMarchAmt(BigDecimal marchAmt) {
        this.marchAmt = marchAmt;
    }

    public BigDecimal getMarchQtyEn() {
        return marchQtyEn;
    }

    public void setMarchQtyEn(BigDecimal marchQtyEn) {
        this.marchQtyEn = marchQtyEn;
    }

    public BigDecimal getMarchAmtEn() {
        return marchAmtEn;
    }

    public void setMarchAmtEn(BigDecimal marchAmtEn) {
        this.marchAmtEn = marchAmtEn;
    }

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public BigDecimal getRemainEntrustAmt() {
        return remainEntrustAmt;
    }

    public void setRemainEntrustAmt(BigDecimal remainEntrustAmt) {
        this.remainEntrustAmt = remainEntrustAmt;
    }

    public BigDecimal getRemainEntrustNum() {
        return remainEntrustNum;
    }

    public void setRemainEntrustNum(BigDecimal remainEntrustNum) {
        this.remainEntrustNum = remainEntrustNum;
    }

    public BigDecimal getMarchFeeAmtPay() {
        return marchFeeAmtPay;
    }

    public void setMarchFeeAmtPay(BigDecimal marchFeeAmtPay) {
        this.marchFeeAmtPay = marchFeeAmtPay;
    }

    public BigDecimal getMarchFeeAmtUnPay() {
        return marchFeeAmtUnPay;
    }

    public void setMarchFeeAmtUnPay(BigDecimal marchFeeAmtUnPay) {
        this.marchFeeAmtUnPay = marchFeeAmtUnPay;
    }

    public String getTrader() {
        return trader;
    }

    public void setTrader(String trader) {
        this.trader = trader;
    }

    public boolean getLastEntrust() {
        return lastEntrust;
    }

    public void setLastEntrust(boolean lastEntrust) {
        this.lastEntrust = lastEntrust;
    }

    public List<ExchangeTransMatchCommonEntrustDetailDto> getEntrustDetailDtoList() {
        return entrustDetailDtoList;
    }

    public void setEntrustDetailDtoList(List<ExchangeTransMatchCommonEntrustDetailDto> entrustDetailDtoList) {
        this.entrustDetailDtoList = entrustDetailDtoList;
    }


    /**
     * 软硬控标识
     */
    private String softSkipMode;

    public String getSoftSkipMode() {
        return softSkipMode;
    }

    public void setSoftSkipMode(String softSkipMode) {
        this.softSkipMode = softSkipMode;
    }

    /**
     * 操作类型
     */
    private String optType;

    public String getOptType() {
        return optType;
    }

    public void setOptType(String optType) {
        this.optType = optType;
    }

    /**
     * 交易方式
     */
    private String dealMode;

    public String getDealMode() {
        return dealMode;
    }

    public void setDealMode(String dealMode) {
        this.dealMode = dealMode;
    }
}
