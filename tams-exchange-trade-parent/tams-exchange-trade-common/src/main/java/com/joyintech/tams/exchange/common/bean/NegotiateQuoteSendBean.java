package com.joyintech.tams.exchange.common.bean;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value = "我发起的协商成交行情返回报文", description = "我发起的协商成交行情返回报文")
public class NegotiateQuoteSendBean {
    private String id;

    @ApiModelProperty(name = "requestNo", value = "请求编号")
    private String requestNo;
    @ApiModelProperty(name = "requestTime", value = "请求时间")
    private String requestTime;
    @ApiModelProperty(name = "requestStatus", value = "请求状态(1：待确认  3：已撤销 4：已成交 )")
    private int requestStatus;
    @ApiModelProperty(name = "stockCode", value = "证券代码")
    private String stockCode;
    @ApiModelProperty(name = "stockName", value = "证券名称")
    private String stockName;
    @ApiModelProperty(name = "bs", value = "对手方买卖方向（1：买入 2：卖出）")
    private String bs;
    @ApiModelProperty(name = "amount", value = "数量")
    private long amount;
    @ApiModelProperty(name = "fullPrice", value = "全价价格")
    private BigDecimal fullPrice;
    @ApiModelProperty(name = "entrustPrice", value = "净价价格")
    private BigDecimal entrustPrice;
    @ApiModelProperty(name = "entrustBalance", value = "委托金额")
    private BigDecimal entrustBalance;
    @ApiModelProperty(name = "memberId", value = "本方交易商")
    private String memberId;
    @ApiModelProperty(name = "traderCode", value = "本方交易员")
    private String traderCode;
    @ApiModelProperty(name = "investorId", value = "本方交易主体")
    private String investorId;
    @ApiModelProperty(name = "investorType", value = "本方交易主体类型")
    private String investorType;
    @ApiModelProperty(name = "cpMemberId", value = "对手方交易商")
    private String cpMemberId;
    @ApiModelProperty(name = "cpTraderCode", value = "对手方交易员")
    private String cpTraderCode;
    @ApiModelProperty(name = "cpInvestorId", value = "对手方交易主体")
    private String cpInvestorId;
    @ApiModelProperty(name = "cpInvestorType", value = "对手方交易主体类型")
    private String cpInvestorType;
    //TOTO 对手方经纪客户名称 需要确认
    @ApiModelProperty(name = "confirmId", value = "约定号")
    private String confirmId;
    @ApiModelProperty(name = "positionStr", value = "定位串")
    private String positionStr;
    @ApiModelProperty(name = "remark", value = "备用文本")
    private String remark;

    @ApiModelProperty(name = "requestStatusStr", value = "请求状态(1：待确认 2：已回复 3：已撤销 4：已成交 )")
    private String requestStatusStr;
    @ApiModelProperty(name = "bsStr", value = "对手方买卖方向（1：买入 2：卖出）")
    private String bsStr;
    @ApiModelProperty(name = "investorTypeStr", value = "本方交易主体类型(1.自营 2.资管 3.机构经纪 4.个人经纪)")
    private String investorTypeStr;
    @ApiModelProperty(name = "cpInvestorTypeStr", value = "对手方交易主体类型(1.自营 2.资管 3.机构经纪 4.个人经纪)")
    private String cpInvestorTypeStr;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public int getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(int requestStatus) {
        this.requestStatus = requestStatus;
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

    public String getBs() {
        return bs;
    }

    public void setBs(String bs) {
        this.bs = bs;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public BigDecimal getFullPrice() {
        return fullPrice;
    }

    public void setFullPrice(BigDecimal fullPrice) {
        this.fullPrice = fullPrice;
    }

    public BigDecimal getEntrustPrice() {
        return entrustPrice;
    }

    public void setEntrustPrice(BigDecimal entrustPrice) {
        this.entrustPrice = entrustPrice;
    }

    public BigDecimal getEntrustBalance() {
        return entrustBalance;
    }

    public void setEntrustBalance(BigDecimal entrustBalance) {
        this.entrustBalance = entrustBalance;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getTraderCode() {
        return traderCode;
    }

    public void setTraderCode(String traderCode) {
        this.traderCode = traderCode;
    }

    public String getInvestorId() {
        return investorId;
    }

    public void setInvestorId(String investorId) {
        this.investorId = investorId;
    }

    public String getInvestorType() {
        return investorType;
    }

    public void setInvestorType(String investorType) {
        this.investorType = investorType;
    }

    public String getCpMemberId() {
        return cpMemberId;
    }

    public void setCpMemberId(String cpMemberId) {
        this.cpMemberId = cpMemberId;
    }

    public String getCpTraderCode() {
        return cpTraderCode;
    }

    public void setCpTraderCode(String cpTraderCode) {
        this.cpTraderCode = cpTraderCode;
    }

    public String getCpInvestorId() {
        return cpInvestorId;
    }

    public void setCpInvestorId(String cpInvestorId) {
        this.cpInvestorId = cpInvestorId;
    }

    public String getCpInvestorType() {
        return cpInvestorType;
    }

    public void setCpInvestorType(String cpInvestorType) {
        this.cpInvestorType = cpInvestorType;
    }

    public String getConfirmId() {
        return confirmId;
    }

    public void setConfirmId(String confirmId) {
        this.confirmId = confirmId;
    }

    public String getPositionStr() {
        return positionStr;
    }

    public void setPositionStr(String positionStr) {
        this.positionStr = positionStr;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public String getRequestStatusStr() {
        return requestStatusStr;
    }

    public void setRequestStatusStr(String requestStatusStr) {
        this.requestStatusStr = requestStatusStr;
    }

    public String getBsStr() {
        return bsStr;
    }

    public void setBsStr(String bsStr) {
        this.bsStr = bsStr;
    }

    public String getInvestorTypeStr() {
        return investorTypeStr;
    }

    public void setInvestorTypeStr(String investorTypeStr) {
        this.investorTypeStr = investorTypeStr;
    }

    public String getCpInvestorTypeStr() {
        return cpInvestorTypeStr;
    }

    public void setCpInvestorTypeStr(String cpInvestorTypeStr) {
        this.cpInvestorTypeStr = cpInvestorTypeStr;
    }
}
