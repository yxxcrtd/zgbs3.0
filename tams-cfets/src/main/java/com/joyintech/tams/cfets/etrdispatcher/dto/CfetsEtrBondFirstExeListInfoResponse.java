package com.joyintech.tams.cfets.etrdispatcher.dto;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

@ApiModel(value = "一级投标投标确认执行明细信息", description = "一级投标投标确认执行明细信息")
public class CfetsEtrBondFirstExeListInfoResponse {

    @ApiModelProperty(name = "effectiveStatus", value = "有效状态Code", example = "A1")
    private String effectiveStatus;

    @ApiModelProperty(name = "effectiveStatusName", value = "有效状态", example = "A1")
    private String effectiveStatusName;

    @ApiModelProperty(name = "firstBidStatus", value = "中标状态", example = "A1")
    private String firstBidStatus;

    @ApiModelProperty(name = "firstBidStatusStr", value = "中标状态名称", example = "A1")
    private String firstBidStatusStr;

    @ApiModelProperty(name = "bidId", value = "投标编号", example = "111")
    private String bidId;

    @ApiModelProperty(required = false, name = "finprodId", value = "资产代码,系统内代码一般不用于显示")
    private String finprodId;

    @ApiModelProperty(required = false, name = "finprodMarketId", value = "市场代码,显示用代码")
    private String finprodMarketId;

    @ApiModelProperty(required = false, name = "finprodAbbr", value = "资产简称")
    private String finprodAbbr;

    @ApiModelProperty(name = "tenderTarget", value = "招标标的")
    private String tenderTarget;

    @ApiModelProperty(name = "tenderTargetStr", value = "招标标的字典值")
    private String tenderTargetStr;

    @ApiModelProperty(required = false, name = "issueDate", value = "投标日")
    private Date issueDate;

    @ApiModelProperty(name = "issueDateStart", value = "投标日开始日期,字符串", example = "2021-12-21")
    private Date issueDateStart;

    @ApiModelProperty(name = "issueDateEnd", value = "投标日结束日期,字符串", example = "2021-12-21")
    private Date issueDateEnd;

    @ApiModelProperty(required = false, name = "issueDateStr", value = "投标日格式化")
    private String issueDateStr;

    @ApiModelProperty(name = "etrBidPriceTotal", value = "指令投标券面总额(万元) 需前端转化", example = "1")
    private BigDecimal etrBidPriceTotal;

    @ApiModelProperty(name = "totalBiddingNum", value = "汇总投标券面总额")
    private BigDecimal totalBiddingNum;

    @ApiModelProperty(name = "bidPrinAmt", value = "投标券面总额", example = "1")
    private BigDecimal bidPrinAmt;

    @ApiModelProperty(name = "bidPrice", value = "投标标位", example = "1")
    private String bidPrice;

    @ApiModelProperty(name = "winPrice", value = "中标标位", example = "100.0000")
    private String winPrice;

    @ApiModelProperty(name = "price", value = "净价", example = "1")
    private BigDecimal price;

    @ApiModelProperty(name = "payableAmount", value = "缴款金额", example = "1000.00")
    private BigDecimal payableAmount;

    @ApiModelProperty(name = "settFeeAmt", value = "结算服务费", example = "1000.00")
    private BigDecimal settFeeAmt;

    @ApiModelProperty(name = "payDate", value = "缴款日")
    private Date payDate;

    @ApiModelProperty(name = "payDateStr", value = "缴款日格式化,展示用")
    private String payDateStr;

    @ApiModelProperty(name = "assignRegdate", value = "债权债务登记日")
    private Date assignRegdate;

    @ApiModelProperty(name = "assignRegdateStr", value = "债权债务登记日格式化,展示用")
    private String assignRegdateStr;

    @ApiModelProperty(name = "listingDate", value = "上市日")
    private Date listingDate;

    @ApiModelProperty(name = "listingDateStr", value = "上市日日格式化,展示用", example = "2021-01-01")
    private String listingDateStr;

    @ApiModelProperty(name = "biddingMethod", value = "投标方式", example = "01")
    private String biddingMethod;

    @ApiModelProperty(name = "biddingMethodStr", value = "投标方式名称,展示用", example = "自主投标")
    private String biddingMethodStr;

    @ApiModelProperty(name = "payType", value = "缴款方式", example = "01")
    private String payType;

    @ApiModelProperty(name = "payTypeStr", value = "缴款方式名称,展示用", example = "一级分销")
    private String payTypeStr;

    @ApiModelProperty(name = "bidConfirmDate", value = "投标确认日")
    private Date bidConfirmDate;

    @ApiModelProperty(required = false, name = "bidConfirmDateStr", value = "投标确认日格式化")
    private String bidConfirmDateStr;

    @ApiModelProperty(name = "dealDate", value = "成交日期")
    private Date dealDate;

    @ApiModelProperty(required = false, name = "dealDateStr", value = "成交日期格式化")
    private String dealDateStr;

    @ApiModelProperty(name = "tradeMarket", value = "交易场所")
    private String tradeMarket;

    @ApiModelProperty(name = "tradeMarketStr", value = "交易场所名称")
    private String tradeMarketStr;

    @ApiModelProperty(name = "counterId", value = "交易对手", example = "A1")
    private String counterId;

    @ApiModelProperty(name = "counterName", value = "交易对手名称", example = "A1")
    private String counterName;

    @ApiModelProperty(required = false, name = "remark", value = "备注")
    private String remark;

    public String getEffectiveStatus() {
        return effectiveStatus;
    }

    public void setEffectiveStatus(String effectiveStatus) {
        this.effectiveStatus = effectiveStatus;
    }

    public String getEffectiveStatusName() {
        if (StringUtil.isEmpty(getEffectiveStatus())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.交易流程状态, getEffectiveStatus());
    }

    public void setEffectiveStatusName(String effectiveStatusName) {
        this.effectiveStatusName = effectiveStatusName;
    }

    public String getFirstBidStatus() {
        return firstBidStatus;
    }

    public void setFirstBidStatus(String firstBidStatus) {
        this.firstBidStatus = firstBidStatus;
    }

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
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

    public String getTenderTarget() {
        return tenderTarget;
    }

    public void setTenderTarget(String tenderTarget) {
        this.tenderTarget = tenderTarget;
    }

    public String getTenderTargetStr() {
        if (StringUtil.isEmpty(getTenderTarget())) {
            return "";
        }
        return SysDataUtil.getNameFromDataDict(DictCode.招标标的, getTenderTarget());
    }

    public void setTenderTargetStr(String tenderTargetStr) {
        this.tenderTargetStr = tenderTargetStr;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getIssueDateStr() {
        if (null != this.getIssueDateStart() && null != this.getIssueDateEnd()) {
            return DateUtil.format(this.getIssueDateStart()) + "~" + DateUtil.format(this.getIssueDateEnd());
        }
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

    public BigDecimal getBidPrinAmt() {
        return bidPrinAmt;
    }

    public void setBidPrinAmt(BigDecimal bidPrinAmt) {
        this.bidPrinAmt = bidPrinAmt;
    }

    public String getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(String bidPrice) {
        this.bidPrice = bidPrice;
    }

    public String getBiddingMethod() {
        return biddingMethod;
    }

    public void setBiddingMethod(String biddingMethod) {
        this.biddingMethod = biddingMethod;
    }

    public String getBiddingMethodStr() {
        if (StringUtil.isEmpty(getBiddingMethod())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.投标方式, getBiddingMethod());
    }

    public void setBiddingMethodStr(String biddingMethodStr) {
        this.biddingMethodStr = biddingMethodStr;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayTypeStr() {
        if (StringUtil.isEmpty(getPayType())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.缴款方式, getPayType());
    }

    public void setPayTypeStr(String payTypeStr) {
        this.payTypeStr = payTypeStr;
    }

    public Date getBidConfirmDate() {
        return bidConfirmDate;
    }

    public void setBidConfirmDate(Date bidConfirmDate) {
        this.bidConfirmDate = bidConfirmDate;
    }

    public String getWinPrice() {
        return winPrice;
    }

    public void setWinPrice(String winPrice) {
        this.winPrice = winPrice;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(BigDecimal payableAmount) {
        this.payableAmount = payableAmount;
    }

    public BigDecimal getSettFeeAmt() {
        return settFeeAmt;
    }

    public void setSettFeeAmt(BigDecimal settFeeAmt) {
        this.settFeeAmt = settFeeAmt;
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

    public Date getAssignRegdate() {
        return assignRegdate;
    }

    public void setAssignRegdate(Date assignRegdate) {
        this.assignRegdate = assignRegdate;
    }

    public String getAssignRegdateStr() {
        if (null != this.getAssignRegdate()) {
            return DateUtil.format(this.getAssignRegdate());
        }
        return assignRegdateStr;
    }

    public void setAssignRegdateStr(String assignRegdateStr) {
        this.assignRegdateStr = assignRegdateStr;
    }

    public Date getListingDate() {
        return listingDate;
    }

    public void setListingDate(Date listingDate) {
        this.listingDate = listingDate;
    }

    public String getListingDateStr() {
        if (null != this.getListingDate()) {
            return DateUtil.format(this.getListingDate());
        }
        return listingDateStr;
    }

    public void setListingDateStr(String listingDateStr) {
        this.listingDateStr = listingDateStr;
    }

    public String getBidConfirmDateStr() {
        if (null != this.getBidConfirmDate()) {
            return DateUtil.format(this.getBidConfirmDate());
        }
        return bidConfirmDateStr;
    }

    public void setBidConfirmDateStr(String bidConfirmDateStr) {
        this.bidConfirmDateStr = bidConfirmDateStr;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getTradeMarketStr() {
        if (StringUtil.isEmpty(getTradeMarket())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.交易场所, getTradeMarket());
    }

    public void setTradeMarketStr(String tradeMarketStr) {
        this.tradeMarketStr = tradeMarketStr;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getFirstBidStatusStr() {
        if (StringUtil.isEmpty(getFirstBidStatus())) {
            return "--";
        }
        return SysDataUtil.getNameFromDataDict(DictCode.一级投标中标状态, getFirstBidStatus());
    }

    public void setFirstBidStatusStr(String firstBidStatusStr) {
        this.firstBidStatusStr = firstBidStatusStr;
    }

    public Date getDealDate() {
        return dealDate;
    }

    public void setDealDate(Date dealDate) {
        this.dealDate = dealDate;
    }

    public String getDealDateStr() {
        if (null != this.getDealDate()) {
            return DateUtil.format(this.getDealDate());
        }
        return dealDateStr;
    }

    public void setDealDateStr(String dealDateStr) {
        this.dealDateStr = dealDateStr;
    }

    public BigDecimal getEtrBidPriceTotal() {
        return etrBidPriceTotal;
    }

    public void setEtrBidPriceTotal(BigDecimal etrBidPriceTotal) {
        this.etrBidPriceTotal = etrBidPriceTotal;
    }

    public Date getIssueDateStart() {
        return issueDateStart;
    }

    public void setIssueDateStart(Date issueDateStart) {
        this.issueDateStart = issueDateStart;
    }

    public Date getIssueDateEnd() {
        return issueDateEnd;
    }

    public void setIssueDateEnd(Date issueDateEnd) {
        this.issueDateEnd = issueDateEnd;
    }
}



