package com.joyintech.tams.exchange.common.bean.request;


import com.google.common.base.MoreObjects;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import java.math.BigDecimal;

@ApiModel(value = "我收到的协议回购行情请求报文", description = "我收到的协议回购行情请求报文")
public class RepurchaseQuoteReceiveRequest {
    @ApiModelProperty(required = true, name = "instructionId", value = "指令编号", example = "FUND000121")
    @NotBlank(message = "指令编号不能为空")
    private String instructionId;

    @ApiModelProperty(name = "requestNo", value = "请求编号")
    private String requestNo;
    @ApiModelProperty(name = "origSerialNo", value = "初始交易编号")
    private String origSerialNo;
    @ApiModelProperty(name = "account", value = "本方资金账户")
    private String account;
    @ApiModelProperty(name = "requestType", value = "请求类型： 首期-F,到期续做-X,解除质押-C,中途换券-H,提前购回-Z")
    private String requestType;
    @ApiModelProperty(name = "stockCode", value = "质押券")
    private String stockCode;
    @ApiModelProperty(name = "pledgeNumber", value = "质押数量")
    private BigDecimal pledgeNumber;
    @ApiModelProperty(name = "repoAmount", value = "回购金额")
    private BigDecimal repoAmount;
    @ApiModelProperty(name = "compactTerm", value = "回购期限")
    private Integer compactTerm;
    @ApiModelProperty(name = "repoRate", value = "回购利率")
    private BigDecimal repoRate;
    @ApiModelProperty(name = "settleAmount", value = "结算金额")
    private BigDecimal settleAmount;
    @ApiModelProperty(name = "firstSettleDate", value = "首次结算日")
    private String firstSettleDate;
    @ApiModelProperty(name = "remark", value = "补充协议")
    private String remark;

    public String getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(String instructionId) {
        this.instructionId = instructionId;
    }

    public String getRequestNo() {
        return requestNo;
    }

    public void setRequestNo(String requestNo) {
        this.requestNo = requestNo;
    }

    public String getOrigSerialNo() {
        return origSerialNo;
    }

    public void setOrigSerialNo(String origSerialNo) {
        this.origSerialNo = origSerialNo;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public BigDecimal getPledgeNumber() {
        return pledgeNumber;
    }

    public void setPledgeNumber(BigDecimal pledgeNumber) {
        this.pledgeNumber = pledgeNumber;
    }

    public BigDecimal getRepoAmount() {
        return repoAmount;
    }

    public void setRepoAmount(BigDecimal repoAmount) {
        this.repoAmount = repoAmount;
    }

    public Integer getCompactTerm() {
        return compactTerm;
    }

    public void setCompactTerm(Integer compactTerm) {
        this.compactTerm = compactTerm;
    }

    public BigDecimal getRepoRate() {
        return repoRate;
    }

    public void setRepoRate(BigDecimal repoRate) {
        this.repoRate = repoRate;
    }

    public BigDecimal getSettleAmount() {
        return settleAmount;
    }

    public void setSettleAmount(BigDecimal settleAmount) {
        this.settleAmount = settleAmount;
    }

    public String getFirstSettleDate() {
        return firstSettleDate;
    }

    public void setFirstSettleDate(String firstSettleDate) {
        this.firstSettleDate = firstSettleDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("requestNo", requestNo)
                .add("origSerialNo", origSerialNo)
                .add("account", account)
                .add("requestType", requestType)
                .add("stockCode", stockCode)
                .add("pledgeNumber", pledgeNumber)
                .add("repoAmount", repoAmount)
                .add("compactTerm", compactTerm)
                .add("repoRate", repoRate)
                .add("settleAmount", settleAmount)
                .add("firstSettleDate", firstSettleDate)
                .add("remark", remark)
                .toString();
    }
}
