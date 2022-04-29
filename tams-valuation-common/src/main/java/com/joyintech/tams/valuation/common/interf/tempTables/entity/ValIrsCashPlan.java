package com.joyintech.tams.valuation.common.interf.tempTables.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;

import java.math.BigDecimal;
import java.util.Date;

public class ValIrsCashPlan extends BaseEntity {
    /**
     * 利率互换交易编号
     */
    private String tradeId;

    /**
     * 现金流序号
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal orderNo;

    /**
     * 理论开始日
     */
    private Date theVdate;

    /**
     * 理论截止日
     */
    private Date theMdate;

    /**
     * 实际开始日
     */
    private Date actVdate;

    /**
     * 实际截止日
     */
    private Date actMdate;



    /**
     * 支付日
     */
    private Date payDay;



    /**
     * 新增、修改、删除
     */
    private String dataActionSign;


    /**
     * 交易类型
     */
    private String dealType;

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public BigDecimal getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(BigDecimal orderNo) {
        this.orderNo = orderNo;
    }

    public Date getTheVdate() {
        return theVdate;
    }

    public void setTheVdate(Date theVdate) {
        this.theVdate = theVdate;
    }

    public Date getTheMdate() {
        return theMdate;
    }

    public void setTheMdate(Date theMdate) {
        this.theMdate = theMdate;
    }

    public Date getActVdate() {
        return actVdate;
    }

    public void setActVdate(Date actVdate) {
        this.actVdate = actVdate;
    }

    public Date getActMdate() {
        return actMdate;
    }

    public void setActMdate(Date actMdate) {
        this.actMdate = actMdate;
    }

    public Date getPayDay() {
        return payDay;
    }

    public void setPayDay(Date payDay) {
        this.payDay = payDay;
    }

    public String getDataActionSign() {
        return dataActionSign;
    }

    public void setDataActionSign(String dataActionSign) {
        this.dataActionSign = dataActionSign;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(String dealType) {
        this.dealType = dealType;
    }
}