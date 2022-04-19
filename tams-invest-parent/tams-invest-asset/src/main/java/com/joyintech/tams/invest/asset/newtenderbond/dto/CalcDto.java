package com.joyintech.tams.invest.asset.newtenderbond.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 计算发行价格和参考收益率
 */
@ApiModel(value="CalcDto",description="计算dto")
public class CalcDto {

    @ApiModelProperty(name = "calModel", value = "参考收益率:rate,发行价格:price")
    private String calModel;

    @ApiModelProperty(name = "vdate", value = "起息日")
    private String vdate;

    @ApiModelProperty(name = "vdate", value = "起息日")
    private String mdate;

    @ApiModelProperty(name = "intRate", value = "参考收益率")
    private BigDecimal intRate;

    @ApiModelProperty(name = "issuePrice", value = "发行价格")
    private BigDecimal issuePrice;

    public String getVdate() {
        return vdate;
    }

    public void setVdate(String vdate) {
        this.vdate = vdate;
    }

    public String getMdate() {
        return mdate;
    }

    public void setMdate(String mdate) {
        this.mdate = mdate;
    }

    public BigDecimal getIntRate() {
        return intRate;
    }

    public void setIntRate(BigDecimal intRate) {
        this.intRate = intRate;
    }

    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    public void setIssuePrice(BigDecimal issuePrice) {
        this.issuePrice = issuePrice;
    }

    public String getCalModel() {
        return calModel;
    }

    public void setCalModel(String calModel) {
        this.calModel = calModel;
    }
}
