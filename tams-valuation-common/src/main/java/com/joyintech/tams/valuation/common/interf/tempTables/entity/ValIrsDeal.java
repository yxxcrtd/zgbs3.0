package com.joyintech.tams.valuation.common.interf.tempTables.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;

import java.math.BigDecimal;
import java.util.Date;

public class ValIrsDeal extends BaseEntity {
    /**
     * 产品代码
     */
    private String prodId;

    /**
     * 新增、撤销
     */
    private String dataActionSign;

    /**
     * 资管系统的交易编号
     */
    private String tradeId;

    /**
     * 交易日
     */
    private Date tradeDate;

    /**
     * 交割日
     */
    private Date settleDate;

    /**
     * 起息日
     */
    private Date vdate;

    /**
     * 到期日
     */
    private Date mdate;

    /**
     * 交易方向
     */
    private String ps;


    /**
     * 本金金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal prinAmt;

    /**
     * 币种
     */
    private String ccy;


    /**
     * 交易对手
     */
    private String counterId;

    /**
     * 有无清算指令
     */
    private String isClean;


    /**
     * 交易手续费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal dealFeeAmt;

    /**
     * 交易手续费是否随交易支付
     */
    private String isDealFeeAmt;

    /**
     * 结算费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal stlFeeAmt;

    /**
     * 结算费是否随交易支付
     */
    private String isStlFeeAmt;

    public String getProdId() {
        return prodId;
    }

    public void setProdId(String prodId) {
        this.prodId = prodId;
    }

    public String getDataActionSign() {
        return dataActionSign;
    }

    public void setDataActionSign(String dataActionSign) {
        this.dataActionSign = dataActionSign;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
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

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getIsClean() {
        return isClean;
    }

    public void setIsClean(String isClean) {
        this.isClean = isClean;
    }

    public BigDecimal getDealFeeAmt() {
        return dealFeeAmt;
    }

    public void setDealFeeAmt(BigDecimal dealFeeAmt) {
        this.dealFeeAmt = dealFeeAmt;
    }

    public String getIsDealFeeAmt() {
        return isDealFeeAmt;
    }

    public void setIsDealFeeAmt(String isDealFeeAmt) {
        this.isDealFeeAmt = isDealFeeAmt;
    }

    public BigDecimal getStlFeeAmt() {
        return stlFeeAmt;
    }

    public void setStlFeeAmt(BigDecimal stlFeeAmt) {
        this.stlFeeAmt = stlFeeAmt;
    }

    public String getIsStlFeeAmt() {
        return isStlFeeAmt;
    }

    public void setIsStlFeeAmt(String isStlFeeAmt) {
        this.isStlFeeAmt = isStlFeeAmt;
    }
}