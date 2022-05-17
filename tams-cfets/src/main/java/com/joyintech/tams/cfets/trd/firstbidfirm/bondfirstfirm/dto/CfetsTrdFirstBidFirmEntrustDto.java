package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto;

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

/**
 * @author jiangpeng
 * @date 2022年05月14日 16:45
 */
@ApiModel(value = "指令信息", description = "指令列表信息")
public class CfetsTrdFirstBidFirmEntrustDto {

    @ApiModelProperty(name = "entrustStatus", value = "指令状态", example = "04")
    private String entrustStatus;

    @ApiModelProperty(name = "entrustStatusStr", value = "指令状态名称,展示用", example = "已完成")
    private String entrustStatusStr;

    @ApiModelProperty(name = "entrustId", value = "指令代码", example = "WTBOND202204191858")
    private String entrustId;

    @ApiModelProperty(name = "finprodId", value = "资产代码,系统内代码一般不用于显示", example = "F01_aa.IB")
    private String finprodId;

    @ApiModelProperty(name = "finprodMarketId", value = "市场代码,显示用代码", example = "aa.IB")
    private String finprodMarketId;

    @ApiModelProperty(name = "finprodAbbr", value = "资产简称", example = "债券aa")
    private String finprodAbbr;

    @ApiModelProperty(name = "avaNum", value = "剩余指令额度", example = "1000.00")
    private BigDecimal avaNum;

    @ApiModelProperty(name = "avaDealAmt", value = "剩余成交额度", example = "1000.00")
    private BigDecimal avaDealAmt;

    @ApiModelProperty(name = "transactioncom", value = "执行进度", example = "50.00")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal transactioncom;

    @ApiModelProperty(name = "finishAmt", value = "已完成额度", example = "1000.00")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal finishAmt;

    @ApiModelProperty(name = "finishDealAmt", value = "已成交额度", example = "1000.00")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal finishDealAmt;

    @ApiModelProperty(name = "tradeFinishPercent", value = "成交进度", example = "50.00")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeFinishPercent;

    /**
     *  招标标的
     */
    @ApiModelProperty(name="tenderTarget",value="招标标的")
    private String tenderTarget;

    /**
     *  投标日
     */
    @ApiModelProperty(name = "issueDate", value = "投标日")
    private Date issueDate;


    /**
     *  投标日字符串
     */
    @ApiModelProperty(name = "issueDateStr", value = "投标日字符串")
    private String issueDateStr;

    /**
     *  汇总投标券面总额
     */
    @ApiModelProperty(name="totalBiddingNum",value="汇总投标券面总额")
    private BigDecimal totalBiddingNum;

    /**
     *  投标标位
     */
    @ApiModelProperty(name = "bidPrice", value = "投标标位", example = "1")
    private String bidPrice;

    /**
     *  组合代码
     */
    @ApiModelProperty(name = "portfolioId", value = "组合代码", example = "A1")
    private String portfolioId;

    /**
     *  组合代码
     */
    @ApiModelProperty(name = "portfolioName", value = "组合名称", example = "名称A1")
    private String portfolioName;

    /**
     *  投资经理
     */
    @ApiModelProperty(name = "invManagerId", value = "投资经理", example = "A1")
    private String invManagerId;

    /**
     *  交易场所
     */
    @ApiModelProperty(name = "tradeMarket", value = "交易场所")
    private String tradeMarket;

    /**
     *  交易场所名称
     */
    @ApiModelProperty(name = "tradeMarketStr", value = "交易场所名称")
    private String tradeMarketStr;

    /**
     *  交易对手
     */
    @ApiModelProperty(name = "counterId", value = "交易对手", example = "A1")
    private String counterId;


    /**
     *  交易对手名称
     */
    @ApiModelProperty(name = "counterName", value = "交易对手名称", example = "A1")
    private String counterName;

    @ApiModelProperty(name = "entrustDate", value = "指令日期")
    private Date entrustDate;

    @ApiModelProperty(name = "entrustDateStr", value = "指令日期格式化,展示用")
    private String entrustDateStr;

    @ApiModelProperty(name = "transmitTime", value = "指令下达时间", example = "11:23:45")
    private String transmitTime;

    @ApiModelProperty(name = "remark", value = "备注")
    private String remark;

    @ApiModelProperty(name = "LastModifiedDate", value = "最近修改日期")
    private Date LastModifiedDate;

    @ApiModelProperty(name = "LastModifiedDateStr", value = "最近修改日期格式化,展示用")
    private String LastModifiedDateStr;

    @ApiModelProperty(name = "LastModifiedTime", value = "最近修改时间")
    private Date LastModifiedTime;

    @ApiModelProperty(name = "LastModifiedTimeStr", value = "最近修改时间格式化,展示用")
    private String LastModifiedTimeStr;


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

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
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

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }
    public String getTradeMarketStr() {
        if (StringUtil.isEmpty(getTradeMarket())) {
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, getTradeMarket());
    }

    public void setTradeMarketStr(String tradeMarketStr) {
        this.tradeMarketStr = tradeMarketStr;
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

    public BigDecimal getAvaNum() {
        return avaNum;
    }

    public void setAvaNum(BigDecimal avaNum) {
        this.avaNum = avaNum;
    }

    public BigDecimal getAvaDealAmt() {
        return avaDealAmt;
    }

    public void setAvaDealAmt(BigDecimal avaDealAmt) {
        this.avaDealAmt = avaDealAmt;
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

    public BigDecimal getFinishDealAmt() {
        return finishDealAmt;
    }

    public void setFinishDealAmt(BigDecimal finishDealAmt) {
        this.finishDealAmt = finishDealAmt;
    }

    public BigDecimal getTradeFinishPercent() {
        return tradeFinishPercent;
    }

    public void setTradeFinishPercent(BigDecimal tradeFinishPercent) {
        this.tradeFinishPercent = tradeFinishPercent;
    }

    public String getTenderTarget() {
        return tenderTarget;
    }

    public void setTenderTarget(String tenderTarget) {
        this.tenderTarget = tenderTarget;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getIssueDateStr() {
        return issueDateStr;
    }

    public void setIssueDateStr(String issueDateStr) {
        this.issueDateStr = issueDateStr;
    }

    public BigDecimal getTotalBiddingNum() {
        return totalBiddingNum;
    }

    public void setTotalBiddingNum(BigDecimal totalBiddingNum) {
        this.totalBiddingNum = totalBiddingNum;
    }

    public String getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(String bidPrice) {
        this.bidPrice = bidPrice;
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

    public Date getLastModifiedDate() {
        return LastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        LastModifiedDate = lastModifiedDate;
    }

    public String getLastModifiedDateStr() {
        return LastModifiedDateStr;
    }

    public void setLastModifiedDateStr(String lastModifiedDateStr) {
        LastModifiedDateStr = lastModifiedDateStr;
    }

    public Date getLastModifiedTime() {
        return LastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        LastModifiedTime = lastModifiedTime;
    }

    public String getLastModifiedTimeStr() {
        return LastModifiedTimeStr;
    }

    public void setLastModifiedTimeStr(String lastModifiedTimeStr) {
        LastModifiedTimeStr = lastModifiedTimeStr;
    }
}
