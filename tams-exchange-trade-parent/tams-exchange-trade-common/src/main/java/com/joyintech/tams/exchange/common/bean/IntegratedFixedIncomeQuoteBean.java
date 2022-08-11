package com.joyintech.tams.exchange.common.bean;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "上海固收行情查询返回报文", description = "上海固收行情查询返回报文")
public class IntegratedFixedIncomeQuoteBean {
    @ApiModelProperty(name = "errorCode", value = "错误码")
    private int errorCode;
    @ApiModelProperty(name = "errorMsg", value = "错误描述")
    private String errorMsg;

    @ApiModelProperty(name = "orderNo", value = "订单编号")
    private String orderNo;
    @ApiModelProperty(name = "stockCode", value = "证券代码")
    private String stockCode;
    @ApiModelProperty(name = "stockName", value = "证券名称")
    private String stockName;
    @ApiModelProperty(name = "agencyName", value = "报价方")
    private String agencyName;
    @ApiModelProperty(name = "entrustBs", value = "买卖方向")
    private String entrustBs;
    @ApiModelProperty(name = "entrustPrice", value = "价格（净价）")
    private double entrustPrice;
    @ApiModelProperty(name = "fullPrice", value = "全价")
    private double fullPrice;
    @ApiModelProperty(name = "entrustVolume", value = "数量")
    private int entrustVolume;
    @ApiModelProperty(name = "expireIncomeRate", value = "到期收益率")
    private double expireIncomeRate;//到期收益率 lastrate 到期年收益率
    @ApiModelProperty(name = "interest", value = "债券应计利息")
    private double interest;//债券应计利息 bondintr
    @ApiModelProperty(name = "reportTime", value = "报价时间")
    private Long reportTime;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getAgencyName() {
        return agencyName;
    }

    public void setAgencyName(String agencyName) {
        this.agencyName = agencyName;
    }

    public String getEntrustBs() {
        return entrustBs;
    }

    public void setEntrustBs(String entrustBs) {
        this.entrustBs = entrustBs;
    }

    public double getEntrustPrice() {
        return entrustPrice;
    }

    public void setEntrustPrice(double entrustPrice) {
        this.entrustPrice = entrustPrice;
    }

    public double getFullPrice() {
        return fullPrice;
    }

    public void setFullPrice(double fullPrice) {
        this.fullPrice = fullPrice;
    }

    public int getEntrustVolume() {
        return entrustVolume;
    }

    public void setEntrustVolume(int entrustVolume) {
        this.entrustVolume = entrustVolume;
    }

    public double getExpireIncomeRate() {
        return expireIncomeRate;
    }

    public void setExpireIncomeRate(double expireIncomeRate) {
        this.expireIncomeRate = expireIncomeRate;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public Long getReportTime() {
        return reportTime;
    }

    public void setReportTime(Long reportTime) {
        this.reportTime = reportTime;
    }
}
