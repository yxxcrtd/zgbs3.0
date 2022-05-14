/*
 * 文件名：CfetsTrdConfirmRegInfoListResponse.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：
 * 修改时间：2021年01月25日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.cfets.trdconfirm.dto;


import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 银行间交易成交确认
 * 一级申购交易返回数据DTO
 *
 * @author jiangpeng
 * @date 2022/5/9 17:30
 */
@ApiModel(value = "查询一级申购交易执行/cstp成交信息列表返回数据", description = "查询一级申购交易执行/cstp成交信息列表返回数据")
public class CfetsTrdConfirmExecFirstBondInfoResponse {

    @ApiModelProperty(name = "busiCode", value = "业务编号", example = "1")
    private String busiCode;

    @ApiModelProperty(name = "tradeDate", value = "申购日期(成交日期)", example = "admin")
    private Date tradeDate;

    @ApiModelProperty(name = "tradeDateStr", value = "申购日期(成交日期)", example = "admin")
    private String tradeDateStr;

    @ApiModelProperty(name = "finprodId", value = "资产代码Id", example = "ZC1")
    private String finprodId;

    @ApiModelProperty(name = "finprodMarketId", value = "资产代码", example = "ZC1")
    private String finprodMarketId;

    @ApiModelProperty(name = "finprodAbbr", value = "资产简称", example = "ZC名")
    private String finprodAbbr;

    @ApiModelProperty(name = "secManageAcctId", value = "证券账户ID", example = "1")
    private String secManageAcctId;

    @ApiModelProperty(name = "secManageAcctIdName", value = "证券账户", example = "1")
    private String secManageAcctIdName;
    //资产分类
    @ApiModelProperty(name = "subscriptionAmount", value = "认购量,需转化为万元", example = "1000.00")
    private BigDecimal subscriptionAmount;

    @ApiModelProperty(name = "payableAmount", value = "应缴款金额(元)", example = "1000.00")
    private BigDecimal payableAmount;

    @ApiModelProperty(name = "settFeeAmt", value = "结算服务费（元）", example = "1000.00")
    private BigDecimal settFeeAmt;

    @ApiModelProperty(name = "rate", value = "参考收益率/票面利率 需转化为百分数保留四位小数", example = "0.3")
    private BigDecimal rate;

    @ApiModelProperty(name = "portfolioId", value = "组合代码")
    private String portfolioId;

    @ApiModelProperty(name = "portfolioName", value = "组合名称")
    private String portfolioName;

    @ApiModelProperty(name = "issuePrice", value = "发行价格", example = "99.0000")
    private BigDecimal issuePrice;

    @ApiModelProperty(name = "issueDate", value = "发行日")
    private Date issueDate;

    @ApiModelProperty(name = "issueDateStr", value = "发行日格式化,展示用")
    private String issueDateStr;

    @ApiModelProperty(name = "payDate", value = "缴款日")
    private Date payDate;

    @ApiModelProperty(name = "payDateStr", value = "缴款日格式化,展示用")
    private String payDateStr;

    @ApiModelProperty(name = "ouPartyId", value = "本方id(申购机构)", example = "114912384")
    private String ouPartyId;

    @ApiModelProperty(name = "ouPartyIdAbbr", value = "本方简称", example = "广发银行")
    private String ouPartyIdAbbr;

    @ApiModelProperty(name = "ourTrader", value = "本方交易员id(申购用户)", example = "001")
    private String ourTrader;

    @ApiModelProperty(name = "ourTraderName", value = "本方交易员名称", example = "交易员1")
    private String ourTraderName;

    @ApiModelProperty(name = "ccy", value = "币种", example = "CNY")
    private String ccy;

    @ApiModelProperty(name = "ccyStr", value = "币种,展示用", example = "CNY")
    private String ccyStr;

    @ApiModelProperty(name = "remark", value = "备注", example = "CNY")
    private String remark;

    @ApiModelProperty(name = "execid", value = "成交编号", example = "1")
    private String execid;

    @ApiModelProperty(name = "issuetype", value = "发行方式", example = "1")
    private String issuetype;

    @ApiModelProperty(name = "issuetypeStr", value = "发行方式名称", example = "1")
    private String issuetypeStr;

    @ApiModelProperty(name = "bp", value = "利差", example = "1")
    private BigDecimal bp;

    @ApiModelProperty(name = "couponRateType", value = "息票类型", example = "1")
    private String couponRateType;

    @ApiModelProperty(name = "couponRateTypeStr", value = "息票类型名称", example = "1")
    private String couponRateTypeStr;

    @ApiModelProperty(name = "issuer", value = "发行人", example = "1")
    private String issuer;

    @ApiModelProperty(name = "issuerName", value = "发行人名称", example = "1")
    private String issuerName;

    @ApiModelProperty(name = "ratetype", value = "利率类型", example = "1")
    private String ratetype;

    @ApiModelProperty(name = "ratetypeStr", value = "利率类型名称", example = "1")
    private String ratetypeStr;

    @ApiModelProperty(name = "busiType", value = "业务类型 04 登记", example = "04")
    private String busiType;


    public String getBusiCode() {
        return busiCode;
    }

    public void setBusiCode(String busiCode) {
        this.busiCode = busiCode;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getTradeDateStr() {
        if (null != this.getTradeDate()) {
            return DateUtil.format(this.getTradeDate());
        }
        return tradeDateStr;
    }

    public void setTradeDateStr(String tradeDateStr) {
        this.tradeDateStr = tradeDateStr;
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

    public String getSecManageAcctIdName() {
        return secManageAcctIdName;
    }

    public void setSecManageAcctIdName(String secManageAcctIdName) {
        this.secManageAcctIdName = secManageAcctIdName;
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

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
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

    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    public void setIssuePrice(BigDecimal issuePrice) {
        this.issuePrice = issuePrice;
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

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getCcyStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, getCcy());
    }

    public void setCcyStr(String ccyStr) {
        this.ccyStr = ccyStr;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getExecid() {
        return execid;
    }

    public void setExecid(String execid) {
        this.execid = execid;
    }

    public String getIssuetype() {
        return issuetype;
    }

    public void setIssuetype(String issuetype) {
        this.issuetype = issuetype;
    }

    public String getIssuetypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.新债_发行方式, getIssuetype());
    }

    public void setIssuetypeStr(String issuetypeStr) {
        this.issuetypeStr = issuetypeStr;
    }

    public BigDecimal getBp() {
        return bp;
    }

    public void setBp(BigDecimal bp) {
        this.bp = bp;
    }

    public String getCouponRateType() {
        return couponRateType;
    }

    public void setCouponRateType(String couponRateType) {
        this.couponRateType = couponRateType;
    }

    public String getCouponRateTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.息票品种, getCouponRateType());
    }

    public void setCouponRateTypeStr(String couponRateTypeStr) {
        this.couponRateTypeStr = couponRateTypeStr;
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

    public String getRatetype() {
        return ratetype;
    }

    public void setRatetype(String ratetype) {
        this.ratetype = ratetype;
    }

    public String getRatetypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.利率类型, getRatetype());
    }

    public void setRatetypeStr(String ratetypeStr) {
        this.ratetypeStr = ratetypeStr;
    }

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }
}
