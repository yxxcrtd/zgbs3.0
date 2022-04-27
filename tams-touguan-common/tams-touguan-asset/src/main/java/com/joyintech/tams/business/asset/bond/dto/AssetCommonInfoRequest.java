/*
 * 文件名：FinBondTargetAssetDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.business.asset.bond.dto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 债券标的信息DTO
 *
 * @author wangyang1
 * @version 1.0 2022年4月13日
 * @see AssetCommonInfoRequest
 * @since 1.0
 */
@ApiModel(value="债券标的信息查询参数",description="债券标的信息查询参数")
public class AssetCommonInfoRequest {
    @ApiModelProperty(name="priceFlag",value="发行价格点选标识(选中传Y)")
    private String priceFlag;

    @ApiModelProperty(name="rateFlag",value="初始利率点选标识(选中传Y)")
    private String rateFlag;

    @ApiModelProperty(name="issuePrice",value="发行价格")
    private BigDecimal issuePrice;

    @ApiModelProperty(name="rate",value="初始利率")
    private BigDecimal rate;

    @ApiModelProperty(name="vdate",value="起息日")
    private Date vdate;

    @ApiModelProperty(name="mdate",value="到期日")
    private Date mdate;

    @ApiModelProperty(name="calendarId",value="交易日历")
    private String calendarId;

    @ApiModelProperty(name="couponSpecies",value="息票品种")
    private String couponSpecies;

    @ApiModelProperty(name="tradeMarket",value="交易场所")
    private String tradeMarket;

    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    public void setIssuePrice(BigDecimal issuePrice) {
        this.issuePrice = issuePrice;
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

    public String getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    public String getCouponSpecies() {
        return couponSpecies;
    }

    public void setCouponSpecies(String couponSpecies) {
        this.couponSpecies = couponSpecies;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getPriceFlag() {
        return priceFlag;
    }

    public void setPriceFlag(String priceFlag) {
        this.priceFlag = priceFlag;
    }

    public String getRateFlag() {
        return rateFlag;
    }

    public void setRateFlag(String rateFlag) {
        this.rateFlag = rateFlag;
    }
}
