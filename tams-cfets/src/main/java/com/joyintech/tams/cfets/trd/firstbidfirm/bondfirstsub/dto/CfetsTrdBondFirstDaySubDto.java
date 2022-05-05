package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto;

import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

@ApiModel(value = "当日申购", description = "当日申购列表")
public class CfetsTrdBondFirstDaySubDto {

    @ApiModelProperty(name = "issueStatus", value = "发行状态", example = "01")
    private String issueStatus;

    @ApiModelProperty(name = "issueStatusStr", value = "发行状态名称,显示用", example = "发行成功")
    private String issueStatusStr;

    @ApiModelProperty(name = "finprodId", value = "资产代码,系统内代码一般不用于显示", example = "F01_001.IB")
    private String finprodId;

    @ApiModelProperty(name = "finprodMarketId", value = "市场代码,显示用代码", example = "001.IB")
    private String finprodMarketId;

    @ApiModelProperty(name = "finprodAbbr", value = "资产简称", example = "001债券")
    private String finprodAbbr;

    @ApiModelProperty(name = "secManageAcctId", value = "证券账户", example = "zqzh")
    private String secManageAcctId;

    @ApiModelProperty(name = "secAcctName", value = "证券账户名称,展示用")
    private String secAcctName;

    @ApiModelProperty(name = "subscriptionAmount", value = "认购量", example = "1000.00")
    private BigDecimal subscriptionAmount;

    @ApiModelProperty(name = "payableAmount", value = "应缴款金额", example = "1000.00")
    private BigDecimal payableAmount;

    @ApiModelProperty(name = "issuePrice", value = "发行价格", example = "99.0000")
    private BigDecimal issuePrice;

    @ApiModelProperty(name = "settFeeAmt", value = "结算服务费", example = "1000.00")
    private BigDecimal settFeeAmt;

    @ApiModelProperty(name = "rate", value = "参考收益率/票面利率", example = "99.0000")
    private BigDecimal rate;

    @ApiModelProperty(name = "issueClass", value = "发行方式")
    private String issueClass;

    @ApiModelProperty(name = "issueClassStr", value = "发行方式名称,展示用")
    private String issueClassStr;

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

    @ApiModelProperty(name = "ouPartyId", value = "本方id", example = "114912384")
    private String ouPartyId;

    @ApiModelProperty(name = "ouPartyIdAbbr", value = "本方简称", example = "广发银行")
    private String ouPartyIdAbbr;

    @ApiModelProperty(name = "issuer", value = "发行人id", example = "001")
    private String issuer;

    @ApiModelProperty(name = "issuerName", value = "发行人名称,展示用", example = "发行人1")
    private String issuerName;


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

    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    public void setIssuePrice(BigDecimal issuePrice) {
        this.issuePrice = issuePrice;
    }

    public BigDecimal getSettFeeAmt() {
        return settFeeAmt;
    }

    public void setSettFeeAmt(BigDecimal settFeeAmt) {
        this.settFeeAmt = settFeeAmt;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getIssueClass() {
        return issueClass;
    }

    public void setIssueClass(String issueClass) {
        this.issueClass = issueClass;
    }

    public String getIssueClassStr() {
        if (StringUtil.isEmpty(getIssueClass())) {
            return StringUtil.EMPTY;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.新债_发行方式, getIssueClass());
    }

    public void setIssueClassStr(String issueClassStr) {
        this.issueClassStr = issueClassStr;
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
}



