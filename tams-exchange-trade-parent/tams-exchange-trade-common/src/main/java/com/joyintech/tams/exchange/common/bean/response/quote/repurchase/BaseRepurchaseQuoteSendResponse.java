package com.joyintech.tams.exchange.common.bean.response.quote.repurchase;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value = "我发起的协议回购行情返回报文", description = "我发起的协议回购行情返回报文")
public class BaseRepurchaseQuoteSendResponse {

    @ApiModelProperty(name = "requestStatus", value = "请求状态")
    private Integer requestStatus;

    @ApiModelProperty(name = "requestType", value = "本方请求类型")
    private String requestType;

    @ApiModelProperty(name = "bs", value = "本方委托方向")
    private String bs;

    @ApiModelProperty(name = "stockCode", value = "质押券代码")
    private String stockCode;

    @ApiModelProperty(name = "stockName", value = "质押券名称")
    private String stockName;

    @ApiModelProperty(name = "pledgeNumber", value = "质押数量")
    private BigDecimal pledgeNumber;

    @ApiModelProperty(name = "counterId", value = "交易对手")
    private String counterId;

    @ApiModelProperty(name = "traderCode", value = "本方交易员")
    private String traderCode;
    @ApiModelProperty(name = "memberId", value = "本方交易商")
    private String memberId;
    @ApiModelProperty(name = "investorId", value = "本方交易主体")
    private String investorId;
    @ApiModelProperty(name = "investorType", value = "本方交易主体类型")
    private String investorType;

    @ApiModelProperty(name = "cpTraderCode", value = "对手方交易员")
    private String cpTraderCode;
    @ApiModelProperty(name = "cpMemberId", value = "对手方交易商")
    private String cpMemberId;
    @ApiModelProperty(name = "cpInvestorId", value = "对手方交易主体")
    private String cpInvestorId;
    @ApiModelProperty(name = "cpInvestorType", value = "对手方交易主体类型")
    private String cpInvestorType;
    @ApiModelProperty(name = "cpClientName", value = "对手方客户名称")
    private String cpClientName;

    @ApiModelProperty(name = "remark", value = "补充协议")
    private String remark;

    @ApiModelProperty(name = "requestStatusStr", value = "请求状态（待确认、已成交、已撤销）")
    private String requestStatusStr;
    @ApiModelProperty(name = "requestTypeStr", value = "本方请求类型（首期、提前购回、中途换券、到期续做、解除质押、到期购回）")
    private String requestTypeStr;
    @ApiModelProperty(name = "bsStr", value = "本方委托方向（1：正回购 2：逆回购）")
    private String bsStr;
    @ApiModelProperty(name = "pledgeNumberStr", value = "质押数量（单位：万元，保留两位小数）")
    private String pledgeNumberStr;

    @ApiModelProperty(name = "investorTypeStr", value = "本方交易主体类型（1.自营 2.资管 3.机构经纪 4.个人经纪）")
    private String investorTypeStr;
    @ApiModelProperty(name = "cpInvestorTypeStr", value = "对手方交易主体类型（1.自营 2.资管 3.机构经纪 4.个人经纪）")
    private String cpInvestorTypeStr;

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getBs() {
        return bs;
    }

    public void setBs(String bs) {
        this.bs = bs;
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

    public BigDecimal getPledgeNumber() {
        return pledgeNumber;
    }

    public void setPledgeNumber(BigDecimal pledgeNumber) {
        this.pledgeNumber = pledgeNumber;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getTraderCode() {
        return traderCode;
    }

    public void setTraderCode(String traderCode) {
        this.traderCode = traderCode;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
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

    public String getCpTraderCode() {
        return cpTraderCode;
    }

    public void setCpTraderCode(String cpTraderCode) {
        this.cpTraderCode = cpTraderCode;
    }

    public String getCpMemberId() {
        return cpMemberId;
    }

    public void setCpMemberId(String cpMemberId) {
        this.cpMemberId = cpMemberId;
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

    public String getCpClientName() {
        return cpClientName;
    }

    public void setCpClientName(String cpClientName) {
        this.cpClientName = cpClientName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getRequestTypeStr() {
        return requestTypeStr;
    }

    public void setRequestTypeStr(String requestTypeStr) {
        this.requestTypeStr = requestTypeStr;
    }

    public String getBsStr() {
        return bsStr;
    }

    public void setBsStr(String bsStr) {
        this.bsStr = bsStr;
    }

    public String getPledgeNumberStr() {
        return pledgeNumberStr;
    }

    public void setPledgeNumberStr(String pledgeNumberStr) {
        this.pledgeNumberStr = pledgeNumberStr;
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

    public Integer getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(Integer requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getRequestStatusStr() {
        return requestStatusStr;
    }

    public void setRequestStatusStr(String requestStatusStr) {
        this.requestStatusStr = requestStatusStr;
    }
}
