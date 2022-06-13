package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

@ApiModel(value = "申购执行明细", description = "申购执行明细列表,申购明细列表")
public class CfetsTrdBondFirstSubDealDetailDto {

    @ApiModelProperty(name = "bidId", value = "投标编号", example = "01")
    private String bidId;

    @ApiModelProperty(name = "validStatus", value = "有效状态", example = "01")
    private String validStatus;

    @ApiModelProperty(name = "validStatusStr", value = "有效状态名称,展示用", example = "有效")
    private String validStatusStr;

    @ApiModelProperty(name = "issueStatus", value = "发行状态", example = "01")
    private String issueStatus;

    @ApiModelProperty(name = "issueStatusStr", value = "发行状态名称,显示用", example = "发行成功")
    private String issueStatusStr;

    @ApiModelProperty(name = "dealDate", value = "成交日期")
    private Date dealDate;

    @ApiModelProperty(name = "dealDateStr", value = "成交日期式化")
    private String dealDateStr;

    @ApiModelProperty(name = "finprodId", value = "资产代码", example = "F01_001.IB")
    private String finprodId;

    @ApiModelProperty(name = "finprodMarketId", value = "市场代码", example = "001.IB")
    private String finprodMarketId;

    @ApiModelProperty(name = "finprodAbbr", value = "资产简称", example = "001债券")
    private String finprodAbbr;

    @ApiModelProperty(name = "secManageAcctId", value = "证券账户", example = "zqzh")
    private String secManageAcctId;

    @ApiModelProperty(name = "secAcctName", value = "证券账户名称,展示用")
    private String secAcctName;

    @ApiModelProperty(name = "secType", value = "债券分类")
    private String secType;

    @ApiModelProperty(name = "secTypeStr", value = "债券分类名称,展示用")
    private String secTypeStr;

    @ApiModelProperty(name = "subscriptionAmount", value = "认购量,需转化为万元", example = "1000.00")
    private BigDecimal subscriptionAmount;

    @ApiModelProperty(name = "payableAmount", value = "应缴款金额", example = "1000.00")
    private BigDecimal payableAmount;

    @ApiModelProperty(name = "settFeeAmt", value = "结算服务费", example = "1000.00")
    private BigDecimal settFeeAmt;

    @ApiModelProperty(name = "issuePrice", value = "发行价格", example = "99.0000")
    private BigDecimal issuePrice;

    @ApiModelProperty(name = "rate", value = "参考收益率/票面利率", example = "99.0000")
    private String rate;

    @ApiModelProperty(name = "portfolioId", value = "组合代码,多个用逗号分隔", example = "A")
    private String portfolioId;

    @ApiModelProperty(name = "portfolioName", value = "组合名称,多个用逗号分隔", example = "组合A")
    private String portfolioName;

    @ApiModelProperty(name = "issueDate", value = "发行日")
    private Date issueDate;

    @ApiModelProperty(name = "issueDateStr", value = "发行日格式化")
    private String issueDateStr;

    @ApiModelProperty(name = "payDate", value = "缴款日")
    private Date payDate;

    @ApiModelProperty(name = "payDateStr", value = "缴款日格式化")
    private String payDateStr;

    @ApiModelProperty(name = "couponSpecies", value = "息票品种", example = "01")
    private String couponSpecies;

    @ApiModelProperty(name = "couponSpeciesStr", value = "息票品种名称,展示用", example = "零息")
    private String couponSpeciesStr;

    @ApiModelProperty(name = "intType", value = "利率类型", example = "01")
    private String intType;

    @ApiModelProperty(name = "intTypeStr", value = "利率类型名称,展示用", example = "固定利率")
    private String intTypeStr;

    @ApiModelProperty(name = "issuer", value = "发行人id", example = "001")
    private String issuer;

    @ApiModelProperty(name = "issuerName", value = "发行人名称,展示用", example = "发行人1")
    private String issuerName;

    @ApiModelProperty(name = "ouPartyId", value = "本方id", example = "114912384")
    private String ouPartyId;

    @ApiModelProperty(name = "ouPartyIdAbbr", value = "本方简称", example = "广发银行")
    private String ouPartyIdAbbr;

    @ApiModelProperty(name = "ourTrader", value = "本方交易员id(申购人)", example = "001")
    private String ourTrader;

    @ApiModelProperty(name = "ourTraderName", value = "本方交易员名称", example = "交易员1")
    private String ourTraderName;

    @ApiModelProperty(name = "execid", value = "成交编号", example = "110000")
    private String execid;

    @ApiModelProperty(name = "tradeId", value = "业务编号", example = "交易员1")
    private String tradeId;

    @ApiModelProperty(name = "remark", value = "备注")
    private String remark;

    @ApiModelProperty(name = "bidDate", value = "投标日(申购时间)")
    private Date bidDate;

    @ApiModelProperty(name = "bidDateStr", value = "投标日(申购时间)格式化")
    private String bidDateStr;

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    public String getValidStatus() {
        return validStatus;
    }

    public void setValidStatus(String validStatus) {
        this.validStatus = validStatus;
    }

    public String getValidStatusStr() {
        if (StringUtil.isEmpty(getValidStatus())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.交易流程状态, getValidStatus());
    }

    public void setValidStatusStr(String validStatusStr) {
        this.validStatusStr = validStatusStr;
    }

    public String getIssueStatus() {
        return issueStatus;
    }

    public void setIssueStatus(String issueStatus) {
        this.issueStatus = issueStatus;
    }

    public String getIssueStatusStr() {
        if (StringUtil.isEmpty(getIssueStatus())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.发行状态, getIssueStatus());
    }

    public void setIssueStatusStr(String issueStatusStr) {
        this.issueStatusStr = issueStatusStr;
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

    public String getSecType() {
        return secType;
    }

    public void setSecType(String secType) {
        this.secType = secType;
    }

    public String getSecTypeStr() {
        if (StringUtil.isEmpty(getSecType())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产四级分类, getSecType());
    }

    public void setSecTypeStr(String secTypeStr) {
        this.secTypeStr = secTypeStr;
    }

    public BigDecimal getSubscriptionAmount() {
        return subscriptionAmount;
    }

    public void setSubscriptionAmount(BigDecimal subscriptionAmount) {
        this.subscriptionAmount = subscriptionAmount;
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

    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    public void setIssuePrice(BigDecimal issuePrice) {
        this.issuePrice = issuePrice;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
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

    public String getCouponSpecies() {
        return couponSpecies;
    }

    public void setCouponSpecies(String couponSpecies) {
        this.couponSpecies = couponSpecies;
    }

    public String getCouponSpeciesStr() {
        if (StringUtil.isEmpty(getCouponSpecies())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.息票品种, getCouponSpecies());
    }

    public void setCouponSpeciesStr(String couponSpeciesStr) {
        this.couponSpeciesStr = couponSpeciesStr;
    }

    public String getIntType() {
        return intType;
    }

    public void setIntType(String intType) {
        this.intType = intType;
    }

    public String getIntTypeStr() {
        if (StringUtil.isEmpty(getIntType())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.利率类型, getIntType());
    }

    public void setIntTypeStr(String intTypeStr) {
        this.intTypeStr = intTypeStr;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public String getOuPartyId() {
        return ouPartyId;
    }

    public void setOuPartyId(String ouPartyId) {
        this.ouPartyId = ouPartyId;
    }

    public String getOuPartyIdAbbr() {
        return ouPartyIdAbbr;
    }

    public void setOuPartyIdAbbr(String ouPartyIdAbbr) {
        this.ouPartyIdAbbr = ouPartyIdAbbr;
    }

    public String getOurTrader() {
        return ourTrader;
    }

    public void setOurTrader(String ourTrader) {
        this.ourTrader = ourTrader;
    }

    public String getOurTraderName() {
        return ourTraderName;
    }

    public void setOurTraderName(String ourTraderName) {
        this.ourTraderName = ourTraderName;
    }

    public String getExecid() {
        return execid;
    }

    public void setExecid(String execid) {
        this.execid = execid;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getBidDate() {
        return bidDate;
    }

    public void setBidDate(Date bidDate) {
        this.bidDate = bidDate;
    }

    public String getBidDateStr() {
        if (null != this.getBidDate()) {
            return DateUtil.format(this.getBidDate());
        }
        return bidDateStr;
    }

    public void setBidDateStr(String bidDateStr) {
        this.bidDateStr = bidDateStr;
    }
}



