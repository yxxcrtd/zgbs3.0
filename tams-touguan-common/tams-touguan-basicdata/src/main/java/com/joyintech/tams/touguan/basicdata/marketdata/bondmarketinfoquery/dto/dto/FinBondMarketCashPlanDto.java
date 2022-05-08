package com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.dto.BaseDto;
import com.joyintech.framework.json.BigDecimalSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 债券市场付息计划信息DTO
 *
 * @author zhuys
 * @version 1.0 2022年4月18日
 * @see FinBondMarketCashPlanDto
 * @since 1.0
 */
@ApiModel(value = "债券市场付息计划信息", description = "债券市场付息计划信息")
public class FinBondMarketCashPlanDto extends BaseDto {

    /**
     * 债券id
     */
    @ApiModelProperty(name = "secId", value = "债券ID")
    private String secId;

    /**
     * 现金流类型
     */
    @ApiModelProperty(name = "schedutype", value = "现金流类型")
    private String schedutype;

    /**
     * 理论付息起始日
     */
    @ApiModelProperty(name = "totalintereststartday", value = "理论付息起始日")
    private Date totalintereststartday;

    /**
     * 理论付息结束日
     */
    @ApiModelProperty(name = "totalinterestendday", value = "理论付息结束日")
    private Date totalinterestendday;

    /**
     * 实际付息起始日
     */
    @ApiModelProperty(name = "intereststartday", value = "实际付息起始日")
    private Date intereststartday;

    /**
     * 实际付息结束日
     */
    @ApiModelProperty(name = "interestendday", value = "实际付息结束日")
    private Date interestendday;

    /**
     * 理论支付日
     */
    @ApiModelProperty(name = "interestpayday", value = "理论支付日")
    private Date interestpayday;

    /**
     * 当期利率
     */
    @ApiModelProperty(name = "secId", value = "债券ID")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal couponrate;

    /**
     * 应计利息和
     */
    @ApiModelProperty(name = "accrueintamt", value = "应计利息和")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal accrueintamt;

    /**
     * 日应计利息
     */
    @ApiModelProperty(name = "dailyaccrueint", value = "日应计利息")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal dailyaccrueint;

    /**
     * 支付本金
     */
    @ApiModelProperty(name = "prinamtpay", value = "支付本金")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal prinamtpay;

    /**
     * 支付现金流，即支付的本金加利息
     */
    @ApiModelProperty(name = "cashpay", value = "支付现金流")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal cashpay;

    /**
     * 计息本金
     */
    @ApiModelProperty(name = "prinamt", value = "计息本金")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal prinamt;


    public String getSecId() {
        return secId;
    }

    public void setSecId(String secId) {
        this.secId = secId;
    }

    public String getSchedutype() {
        return schedutype;
    }

    public void setSchedutype(String schedutype) {
        this.schedutype = schedutype;
    }

    public Date getTotalintereststartday() {
        return totalintereststartday;
    }

    public void setTotalintereststartday(Date totalintereststartday) {
        this.totalintereststartday = totalintereststartday;
    }

    public Date getTotalinterestendday() {
        return totalinterestendday;
    }

    public void setTotalinterestendday(Date totalinterestendday) {
        this.totalinterestendday = totalinterestendday;
    }

    public Date getIntereststartday() {
        return intereststartday;
    }

    public void setIntereststartday(Date intereststartday) {
        this.intereststartday = intereststartday;
    }

    public Date getInterestendday() {
        return interestendday;
    }

    public void setInterestendday(Date interestendday) {
        this.interestendday = interestendday;
    }

    public Date getInterestpayday() {
        return interestpayday;
    }

    public void setInterestpayday(Date interestpayday) {
        this.interestpayday = interestpayday;
    }

    public BigDecimal getCouponrate() {
        return couponrate;
    }

    public void setCouponrate(BigDecimal couponrate) {
        this.couponrate = couponrate;
    }

    public BigDecimal getAccrueintamt() {
        return accrueintamt;
    }

    public void setAccrueintamt(BigDecimal accrueintamt) {
        this.accrueintamt = accrueintamt;
    }

    public BigDecimal getDailyaccrueint() {
        return dailyaccrueint;
    }

    public void setDailyaccrueint(BigDecimal dailyaccrueint) {
        this.dailyaccrueint = dailyaccrueint;
    }

    public BigDecimal getPrinamtpay() {
        return prinamtpay;
    }

    public void setPrinamtpay(BigDecimal prinamtpay) {
        this.prinamtpay = prinamtpay;
    }

    public BigDecimal getCashpay() {
        return cashpay;
    }

    public void setCashpay(BigDecimal cashpay) {
        this.cashpay = cashpay;
    }

    public BigDecimal getPrinamt() {
        return prinamt;
    }

    public void setPrinamt(BigDecimal prinamt) {
        this.prinamt = prinamt;
    }
}
