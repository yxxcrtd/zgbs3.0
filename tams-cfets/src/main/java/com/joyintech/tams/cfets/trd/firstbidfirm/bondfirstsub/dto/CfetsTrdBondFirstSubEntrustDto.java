package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

@ApiModel(value = "指令列表", description = "一级申购指令列表")
public class CfetsTrdBondFirstSubEntrustDto {

    @ApiModelProperty(required = false, name = "entrustStatus", value = "指令状态", example = "04")
    private String entrustStatus;

    @ApiModelProperty(required = false, name = "entrustStatusStr", value = "指令状态名称,展示用", example = "已完成")
    private String entrustStatusStr;

    @ApiModelProperty(required = false, name = "finishAmt", value = "已完成额度", example = "1000.00")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal finishAmt;

    @ApiModelProperty(required = false, name = "transactioncom", value = "执行进度", example = "50.00")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal transactioncom;

    @ApiModelProperty(required = false, name = "finishDealAmt", value = "已成交额度", example = "1000.00")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal finishDealAmt;

    @ApiModelProperty(required = false, name = "tradeFinishPercent", value = "成交进度", example = "50.00")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeFinishPercent;

    @ApiModelProperty(required = false, name = "entrustNum", value = "指令额度、券面总额", example = "1000.00")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal entrustNum;

    @ApiModelProperty(required = false, name = "finprodId", value = "资产代码",example = "F01_aa.IB")
    private String finprodId;

    @ApiModelProperty(required = false, name = "finprodMarketId", value = "市场代码",example = "aa.IB")
    private String finprodMarketId;

    @ApiModelProperty(required = false, name = "finprodAbbr", value = "资产简称",example = "债券aa")
    private String finprodAbbr;

    @ApiModelProperty(required = false, name = "issueDate", value = "发行日")
    private Date issueDate;

    @ApiModelProperty(required = false, name = "issueDateStr", value = "发行日格式化,展示用")
    private String issueDateStr;

    @ApiModelProperty(required = false, name = "payDate", value = "缴款日")
    private Date payDate;

    @ApiModelProperty(required = false, name = "payDateStr", value = "缴款日格式化,展示用")
    private String payDateStr;

    @ApiModelProperty(required = false, name = "portfolioId", value = "组合代码",example = "A")
    private String portfolioId;

    @ApiModelProperty(required = false, name = "portfolioName", value = "组合名称",example = "组合A")
    private String portfolioName;

    @ApiModelProperty(required = false, name = "secManageAcctId", value = "证券账户",example = "zqzh")
    private String secManageAcctId;

    @ApiModelProperty(required = false, name = "secAcctName", value = "证券账户名称,展示用")
    private String secAcctName;

    @ApiModelProperty(required = false, name = "businessModel", value = "业务模式代码",example = "AC")
    private String businessModel;

    @ApiModelProperty(required = false, name = "businessModelStr", value = "业务模式名称,展示用")
    private String businessModelStr;

    @ApiModelProperty(required = false, name = "invManagerName", value = "投资经理",example = "张三")
    private String invManagerName;

    @ApiModelProperty(required = false, name = "entrustDate", value = "指令日期")
    private Date entrustDate;

    @ApiModelProperty(required = false, name = "entrustDate", value = "指令日期格式化,展示用")
    private String entrustDateStr;

    @ApiModelProperty(name = "transmitTime", value = "指令下达时间",example = "11:23:45")
    private String transmitTime;

    @ApiModelProperty(required = false, name = "entrustId", value = "指令代码",example = "WTBOND202204191858")
    private String entrustId;

    @ApiModelProperty(required = false, name = "remark", value = "备注")
    private String remark;

    @ApiModelProperty(required = false, name = "avaNum", value = "剩余指令额度",example = "1000.00")
    private BigDecimal avaNum;

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getIssueDateStr() {
        if (null != this.getIssueDate()) {
            return DateUtil.format(this.getIssueDate());
        }
        return issueDateStr;
    }

    public void setIssueDateStr(String issueDateStr) {
        this.issueDateStr = issueDateStr;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public String getPayDateStr() {
        if (null != this.getPayDate()) {
            return DateUtil.format(this.getPayDate());
        }
        return payDateStr;
    }

    public void setPayDateStr(String payDateStr) {
        this.payDateStr = payDateStr;
    }

    public String getEntrustStatus() {
        return entrustStatus;
    }

    public void setEntrustStatus(String entrustStatus) {
        this.entrustStatus = entrustStatus;
    }

    public String getEntrustStatusStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.委托状态, getEntrustStatus());
    }

    public void setEntrustStatusStr(String entrustStatusStr) {
        this.entrustStatusStr = entrustStatusStr;
    }

    public BigDecimal getTransactioncom() {
        return transactioncom;
    }

    public void setTransactioncom(BigDecimal transactioncom) {
        this.transactioncom = transactioncom;
    }

    public BigDecimal getFinishAmt() {
        return finishAmt;
    }

    public void setFinishAmt(BigDecimal finishAmt) {
        this.finishAmt = finishAmt;
    }

    public BigDecimal getAvaNum() {
        return avaNum;
    }

    public void setAvaNum(BigDecimal avaNum) {
        this.avaNum = avaNum;
    }

    public BigDecimal getTradeFinishPercent() {
        return tradeFinishPercent;
    }

    public void setTradeFinishPercent(BigDecimal tradeFinishPercent) {
        this.tradeFinishPercent = tradeFinishPercent;
    }

    public BigDecimal getEntrustNum() {
        return entrustNum;
    }

    public void setEntrustNum(BigDecimal entrustNum) {
        this.entrustNum = entrustNum;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }

    public String getBusinessModelStr() {
        if (StringUtil.isEmpty(getBusinessModel())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.业务模式, getBusinessModel());
    }

    public void setBusinessModelStr(String businessModelStr) {
        this.businessModelStr = businessModelStr;
    }

    public String getInvManagerName() {
        return invManagerName;
    }

    public void setInvManagerName(String invManagerName) {
        this.invManagerName = invManagerName;
    }

    public Date getEntrustDate() {
        return entrustDate;
    }

    public void setEntrustDate(Date entrustDate) {
        this.entrustDate = entrustDate;
    }

    public String getEntrustDateStr() {
        if (null != this.getEntrustDate()) {
            return DateUtil.format(this.getEntrustDate());
        }
        return entrustDateStr;
    }

    public void setEntrustDateStr(String entrustDateStr) {
        this.entrustDateStr = entrustDateStr;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getTransmitTime() {
        return transmitTime;
    }

    public void setTransmitTime(String transmitTime) {
        this.transmitTime = transmitTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    public String getSecAcctName() {
        return secAcctName;
    }

    public void setSecAcctName(String secAcctName) {
        this.secAcctName = secAcctName;
    }

    public BigDecimal getFinishDealAmt() {
        return finishDealAmt;
    }

    public void setFinishDealAmt(BigDecimal finishDealAmt) {
        this.finishDealAmt = finishDealAmt;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

}



