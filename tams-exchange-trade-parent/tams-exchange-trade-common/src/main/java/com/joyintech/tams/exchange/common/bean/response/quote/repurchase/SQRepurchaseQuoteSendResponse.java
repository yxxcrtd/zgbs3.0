package com.joyintech.tams.exchange.common.bean.response.quote.repurchase;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value = "我发起的协议回购(首期)行情返回报文", description = "我发起的协议回购(首期)行情返回报文")
public class SQRepurchaseQuoteSendResponse extends BaseRepurchaseQuoteSendResponse{

    @ApiModelProperty(name = "repoAmount", value = "回购金额")
    private BigDecimal repoAmount;

    @ApiModelProperty(name = "repoRate", value = "回购利率")
    private BigDecimal repoRate;

    @ApiModelProperty(name = "firstSettleDate", value = "首次结算日")
    private String firstSettleDate;

    @ApiModelProperty(name = "repoMdate", value = "回购到期日")
    private String repoMdate;

    @ApiModelProperty(name = "backDate", value = "到期结算日")
    private String backDate;

    @ApiModelProperty(name = "capDays", value = "占款天数")
    private Integer capDays;

    @ApiModelProperty(name = "mIntAmt", value = "回购利息")
    private BigDecimal mIntAmt;

    @ApiModelProperty(name = "backBalance", value = "到期结算金额")
    private BigDecimal backBalance;

    @ApiModelProperty(name = "discount", value = "折算率")
    private BigDecimal discount;

    @ApiModelProperty(name = "repoAmountStr", value = "回购金额（单位：元，保留两位小数）")
    private String repoAmountStr;
    @ApiModelProperty(name = "repoRateStr", value = "回购利率（单位：%，保留三位小数）")
    private String repoRateStr;
    @ApiModelProperty(name = "mIntAmtStr", value = "回购利息（单位：元，保留两位小数）")
    private String mIntAmtStr;
    @ApiModelProperty(name = "backBalanceStr", value = "到期结算金额（单位：元，保留两位小数）")
    private String backBalanceStr;
    @ApiModelProperty(name = "discountStr", value = "折算率（单位：%，保留两位小数）")
    private String discountStr;

    public BigDecimal getRepoAmount() {
        return repoAmount;
    }

    public void setRepoAmount(BigDecimal repoAmount) {
        this.repoAmount = repoAmount;
    }

    public BigDecimal getRepoRate() {
        return repoRate;
    }

    public void setRepoRate(BigDecimal repoRate) {
        this.repoRate = repoRate;
    }

    public String getFirstSettleDate() {
        return firstSettleDate;
    }

    public void setFirstSettleDate(String firstSettleDate) {
        this.firstSettleDate = firstSettleDate;
    }

    public String getRepoMdate() {
        return repoMdate;
    }

    public void setRepoMdate(String repoMdate) {
        this.repoMdate = repoMdate;
    }

    public String getBackDate() {
        return backDate;
    }

    public void setBackDate(String backDate) {
        this.backDate = backDate;
    }

    public Integer getCapDays() {
        return capDays;
    }

    public void setCapDays(Integer capDays) {
        this.capDays = capDays;
    }

    public BigDecimal getmIntAmt() {
        return mIntAmt;
    }

    public void setmIntAmt(BigDecimal mIntAmt) {
        this.mIntAmt = mIntAmt;
    }

    public BigDecimal getBackBalance() {
        return backBalance;
    }

    public void setBackBalance(BigDecimal backBalance) {
        this.backBalance = backBalance;
    }

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public String getRepoAmountStr() {
        return repoAmountStr;
    }

    public void setRepoAmountStr(String repoAmountStr) {
        this.repoAmountStr = repoAmountStr;
    }

    public String getRepoRateStr() {
        return repoRateStr;
    }

    public void setRepoRateStr(String repoRateStr) {
        this.repoRateStr = repoRateStr;
    }

    public String getmIntAmtStr() {
        return mIntAmtStr;
    }

    public void setmIntAmtStr(String mIntAmtStr) {
        this.mIntAmtStr = mIntAmtStr;
    }

    public String getBackBalanceStr() {
        return backBalanceStr;
    }

    public void setBackBalanceStr(String backBalanceStr) {
        this.backBalanceStr = backBalanceStr;
    }

    public String getDiscountStr() {
        return discountStr;
    }

    public void setDiscountStr(String discountStr) {
        this.discountStr = discountStr;
    }
}
