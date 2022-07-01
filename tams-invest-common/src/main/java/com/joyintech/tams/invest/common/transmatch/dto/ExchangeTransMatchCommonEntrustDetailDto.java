package com.joyintech.tams.invest.common.transmatch.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.common.base.entity.EtrEntrustDetail;
import com.joyintech.tams.common.base.entity.EtrPledge;

import java.math.BigDecimal;
import java.util.List;

public class ExchangeTransMatchCommonEntrustDetailDto extends EtrEntrustDetail {
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
     * 指令质押品（质押式协议回购使用）
     */
    private EtrPledge etrPledge;

    /**
     * 转股数量
     */
    private BigDecimal conAmt;

    public BigDecimal getConAmt() {
        return conAmt;
    }

    public void setConAmt(BigDecimal conAmt) {
        this.conAmt = conAmt;
    }

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

    public EtrPledge getEtrPledge() {
        return etrPledge;
    }

    public void setEtrPledge(EtrPledge etrPledge) {
        this.etrPledge = etrPledge;
    }
}
