package com.joyintech.tams.cfets.etr.bondfirstbid.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 一级投标指令查询请求参数
 * @Author: kchen
 * @Date: 2022/4/20 11:22
 */
@ApiModel(value="一级投标指令列表",description="一级投标指令列表")
public class CfetsEtrIBondFirstBidFindEtrInfoDto {

    @ApiModelProperty(name="effectiveStatus",value="有效状态Code",example="A1")
    private String effectiveStatus;

    @ApiModelProperty(name="effectiveStatusName",value="有效状态",example="A1")
    private String effectiveStatusName;

    @ApiModelProperty(name="entrustStatus",value="指令状态Code",example="A1")
    private String entrustStatus;

    @ApiModelProperty(name="entrustStatusName",value="指令状态",example="A1")
    private String entrustStatusName;

    @ApiModelProperty(name="execProcess",value="执行进度%",example="A1")
    private BigDecimal execProcess;

    @ApiModelProperty(name="transProcess",value="成交进度%",example="A1")
    private BigDecimal transProcess;

    @ApiModelProperty(name="firstBidStatus",value="中标状态",example="A1")
    private String firstBidStatus;

    @ApiModelProperty(name="finprodId",value="资产代码Id",example="ZC1")
    private String finprodId;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",example="ZC1")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="ZC名")
    private String finprodAbbr;

    @ApiModelProperty(name="issueClass",value="发行方式",example="报价发行/招标发行/簿记建档")
    private String issueClass;

    @ApiModelProperty(name="issueDate",value="投标日/发行日",example="2021-10-27")
    private String issueDate;

    @ApiModelProperty(name="totalBiddingNum",value="汇总投标券面总额")
    private String totalBiddingNum;

    @ApiModelProperty(name="bidPrice",value="投标标位",example="1")
    private BigDecimal bidPrice;

    @ApiModelProperty(name="payEdate",value="缴款截止日")
    private Date payEdate;

    @ApiModelProperty(name="portfolioId",value="组合代码",example="A1")
    private String portfolioId;

    @ApiModelProperty(name="portfolioName",value="组合名称",example="名称A1")
    private String portfolioName;

    @ApiModelProperty(name="invManagerId",value="投资经理",example="A1")
    private String invManagerId;

    @ApiModelProperty(name="etrDate",value="指令日期",example="2021-07-06")
    private String etrDate;

    @ApiModelProperty(name="counterId",value="交易对手",example="A1")
    private String counterId;

    @ApiModelProperty(name="counterName",value="交易对手名称",example="A1")
    private String counterName;

    @ApiModelProperty(name="approver",value="审批人",example="1")
    private String approver;

    @ApiModelProperty(name="remark",value="备注",example="A1")
    private String remark;

    @ApiModelProperty(name="execFinishAmt",value="已完成指令额度",example="1")
    private BigDecimal execFinishAmt;

    @ApiModelProperty(name="execRemainderAmt",value="剩余指令额度",example="1")
    private BigDecimal execRemainderAmt;

    @ApiModelProperty(name="transFinishAmt",value="已成交额度",example="1")
    private BigDecimal transFinishAmt;

    @ApiModelProperty(name="transRemainderAmt",value="剩余成交额度",example="1")
    private BigDecimal transRemainderAmt;

    @ApiModelProperty(name="entrustId",value="指令编号",example="A1")
    private String entrustId;

    public String getEffectiveStatus() {
        return effectiveStatus;
    }

    public void setEffectiveStatus(String effectiveStatus) {
        this.effectiveStatus = effectiveStatus;
    }

    public String getEffectiveStatusName() {
        return effectiveStatusName;
    }

    public void setEffectiveStatusName(String effectiveStatusName) {
        this.effectiveStatusName = effectiveStatusName;
    }

    public String getEntrustStatus() {
        return entrustStatus;
    }

    public void setEntrustStatus(String entrustStatus) {
        this.entrustStatus = entrustStatus;
    }

    public String getEntrustStatusName() {
        return entrustStatusName;
    }

    public void setEntrustStatusName(String entrustStatusName) {
        this.entrustStatusName = entrustStatusName;
    }

    public BigDecimal getExecProcess() {
        return execProcess;
    }

    public void setExecProcess(BigDecimal execProcess) {
        this.execProcess = execProcess;
    }

    public BigDecimal getTransProcess() {
        return transProcess;
    }

    public void setTransProcess(BigDecimal transProcess) {
        this.transProcess = transProcess;
    }

    public String getFirstBidStatus() {
        return firstBidStatus;
    }

    public void setFirstBidStatus(String firstBidStatus) {
        this.firstBidStatus = firstBidStatus;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
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

    public String getIssueClass() {
        return issueClass;
    }

    public void setIssueClass(String issueClass) {
        this.issueClass = issueClass;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getTotalBiddingNum() {
        return totalBiddingNum;
    }

    public void setTotalBiddingNum(String totalBiddingNum) {
        this.totalBiddingNum = totalBiddingNum;
    }

    public BigDecimal getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(BigDecimal bidPrice) {
        this.bidPrice = bidPrice;
    }

    public Date getPayEdate() {
        return payEdate;
    }

    public void setPayEdate(Date payEdate) {
        this.payEdate = payEdate;
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

    public String getInvManagerId() {
        return invManagerId;
    }

    public void setInvManagerId(String invManagerId) {
        this.invManagerId = invManagerId;
    }

    public String getEtrDate() {
        return etrDate;
    }

    public void setEtrDate(String etrDate) {
        this.etrDate = etrDate;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public String getApprover() {
        return approver;
    }

    public void setApprover(String approver) {
        this.approver = approver;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public BigDecimal getExecFinishAmt() {
        return execFinishAmt;
    }

    public void setExecFinishAmt(BigDecimal execFinishAmt) {
        this.execFinishAmt = execFinishAmt;
    }

    public BigDecimal getExecRemainderAmt() {
        return execRemainderAmt;
    }

    public void setExecRemainderAmt(BigDecimal execRemainderAmt) {
        this.execRemainderAmt = execRemainderAmt;
    }

    public BigDecimal getTransFinishAmt() {
        return transFinishAmt;
    }

    public void setTransFinishAmt(BigDecimal transFinishAmt) {
        this.transFinishAmt = transFinishAmt;
    }

    public BigDecimal getTransRemainderAmt() {
        return transRemainderAmt;
    }

    public void setTransRemainderAmt(BigDecimal transRemainderAmt) {
        this.transRemainderAmt = transRemainderAmt;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }
}
