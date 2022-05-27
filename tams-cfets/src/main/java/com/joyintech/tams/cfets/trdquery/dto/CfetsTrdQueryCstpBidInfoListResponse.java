/*
 * 文件名：CfetsTrdConfirmRegInfoListResponse.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：
 * 修改时间：2021年01月25日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.cfets.trdquery.dto;


import com.joyintech.background.component.common.pack.common.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 银行间交易成交确认
 * 查询Cstp下行列表返回数据DTO
 * @author 1
 * @version 1.0 2021年10月26日
 * @see CfetsTrdQueryCstpBidInfoListResponse
 * @since 1.0
 */
@ApiModel(value="查询Cstp下行列表返回数据",description="查询Cstp下行列表返回数据")
public class CfetsTrdQueryCstpBidInfoListResponse {

    @ApiModelProperty(name="tradeDate",value="成交日期")
    private String tradeDate;

    @ApiModelProperty(name="execid",value="成交编号",example="1")
    private String execid;

    @ApiModelProperty(name="finprodId",value="资产代码Id",example="ZC1")
    private String finprodId;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",example="ZC1")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="ZC名")
    private String finprodAbbr;

    @ApiModelProperty(name="businesstypeStr",value="资产类型",example="A1")
    private String businesstypeStr;

    @ApiModelProperty(name="businesstype",value="资产类型Code",example="A1")
    private String  businesstype;

    @ApiModelProperty(name="issuePrice",value="发行价格",example="字符串1")
    private BigDecimal issuePrice;

    @ApiModelProperty(name="couponRate",value="票面利率",example="字符串1")
    private BigDecimal couponRate;

    @ApiModelProperty(name="spread",value="利差",example="字符串1")
    private BigDecimal spread;

    @ApiModelProperty(name="couponRateType",value="息票类型Id")
    private String couponRateType;

    @ApiModelProperty(name="couponRateTypeStr",value="息票类型")
    private String couponRateTypeStr;

    @ApiModelProperty(name="ccy",value="币种id")
    private String ccy;

    @ApiModelProperty(name="ccyStr",value="币种")
    private String ccyStr;

    @ApiModelProperty(name="subscribeOrgan",value="申购机构")
    private String subscribeOrgan;

    @ApiModelProperty(name="subscribe21Organ",value="申购机构21位码")
    private String subscribe21Organ;

    @ApiModelProperty(name="submitUser",value="申购人")
    private String submitUser;

    @ApiModelProperty(name="subAmt",value="认购量")
    private BigDecimal subAmt;

    @ApiModelProperty(name="payAmt",value="应缴款金额")
    private BigDecimal payAmt;

    @ApiModelProperty(name="issuer",value="发行人")
    private String issuer;

    @ApiModelProperty(name="issuer21FirmId",value="发行人机构21位码")
    private String issuer21FirmId;

    @ApiModelProperty(name="issueClass",value="发行方式")
    private String issueClass;

    @ApiModelProperty(name="issueAmt",value="实际发行量")
    private String issueAmt;

    @ApiModelProperty(name="instrumentStatus",value="发行状态")
    private String instrumentStatus;

    public String getExecid() {
        return execid;
    }

    public void setExecid(String execid) {
        this.execid = execid;
    }

    public String getBusinesstypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.投管业务类型, getBusinesstype());
    }

    public void setBusinesstypeStr(String businesstypeStr) {
        this.businesstypeStr = businesstypeStr;
    }

    public String getBusinesstype() {
        return businesstype;
    }

    public void setBusinesstype(String businesstype) {
        this.businesstype = businesstype;
    }

    public String getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String tradeDate) {
        this.tradeDate = tradeDate;
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

    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    public void setIssuePrice(BigDecimal issuePrice) {
        this.issuePrice = issuePrice;
    }

    public BigDecimal getCouponRate() {
        return couponRate;
    }

    public void setCouponRate(BigDecimal couponRate) {
        this.couponRate = couponRate;
    }

    public BigDecimal getSpread() {
        return spread;
    }

    public void setSpread(BigDecimal spread) {
        this.spread = spread;
    }

    public String getCouponRateType() {
        return couponRateType;
    }

    public void setCouponRateType(String couponRateType) {
        this.couponRateType = couponRateType;
    }

    public String getCouponRateTypeStr() {
        return couponRateTypeStr;
    }

    public void setCouponRateTypeStr(String couponRateTypeStr) {
        this.couponRateTypeStr = couponRateTypeStr;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getCcyStr() {
        return ccyStr;
    }

    public void setCcyStr(String ccyStr) {
        this.ccyStr = ccyStr;
    }

    public String getSubscribeOrgan() {
        return subscribeOrgan;
    }

    public void setSubscribeOrgan(String subscribeOrgan) {
        this.subscribeOrgan = subscribeOrgan;
    }

    public String getSubscribe21Organ() {
        return subscribe21Organ;
    }

    public void setSubscribe21Organ(String subscribe21Organ) {
        this.subscribe21Organ = subscribe21Organ;
    }

    public String getSubmitUser() {
        return submitUser;
    }

    public void setSubmitUser(String submitUser) {
        this.submitUser = submitUser;
    }

    public BigDecimal getSubAmt() {
        return subAmt;
    }

    public void setSubAmt(BigDecimal subAmt) {
        this.subAmt = subAmt;
    }

    public BigDecimal getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(BigDecimal payAmt) {
        this.payAmt = payAmt;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getIssuer21FirmId() {
        return issuer21FirmId;
    }

    public void setIssuer21FirmId(String issuer21FirmId) {
        this.issuer21FirmId = issuer21FirmId;
    }

    public String getIssueClass() {
        return issueClass;
    }

    public void setIssueClass(String issueClass) {
        this.issueClass = issueClass;
    }

    public String getIssueAmt() {
        return issueAmt;
    }

    public void setIssueAmt(String issueAmt) {
        this.issueAmt = issueAmt;
    }

    public String getInstrumentStatus() {
        return instrumentStatus;
    }

    public void setInstrumentStatus(String instrumentStatus) {
        this.instrumentStatus = instrumentStatus;
    }
}
