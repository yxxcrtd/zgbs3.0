package com.joyintech.tams.touguan.basicdata.marketdata.listedbondInfoQuery.dto.req;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 待上市债券市场信息查询条件
 *
 * @author zhuys
 * @version 1.0 2022年4月11日
 * @see FinListedBondInfoQueryRequest
 * @since 1.0
 */
@ApiModel(value = "待上市债券市场信息查询条件", description = "待上市债券市场信息查询条件")
public class FinListedBondInfoQueryRequest {

    @ApiModelProperty(name = "pageMode", value = "分页对象", example = "{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;

    @ApiModelProperty(name = "secId", value = "债券ID/资产代码")
    private String secId;

    @ApiModelProperty(name = "secName", value = "资产简称")
    private String secName;

    @ApiModelProperty(name = "market", value = "市场")
    private String market;

    @ApiModelProperty(name = "biddingMethod", value = "招标方式")
    private String biddingMethod;

    @ApiModelProperty(name = "raiseMode", value = "募集方式")
    private String raiseMode;

    @ApiModelProperty(name = "tenderTarget", value = "招标标的")
    private String tenderTarget;

    @ApiModelProperty(name = "issueMethod", value = "发行方式")
    private String issueMethod;

    @ApiModelProperty(name = "eventDateStart", value = "缴款截止日-开始")
    private Date eventDateStart;

    @ApiModelProperty(name = "eventDateEnd", value = "缴款截止日-结束")
    private Date eventDateEnd;

    @ApiModelProperty(name = "listingDate", value = "上市日-开始")
    private Date listingDateStart;

    @ApiModelProperty(name = "listingDate", value = "上市日-结束")
    private Date listingDateEnd;

    @ApiModelProperty(name = "listingCode", value = "上市代码")
    private String listingCode;

    @ApiModelProperty(name = "secType", value = "债券分类")
    private String secType;

    @ApiModelProperty(name = "institution", value = "登记托管机构")
    private String institution;

    @ApiModelProperty(name = "issuer", value = "发行人")
    private String issuer;

    @ApiModelProperty(name = "issueDateStart", value = "发行日开始")
    private Date issueDateStart;

    @ApiModelProperty(name = "issueDateEnd", value = "发行日结束")
    private Date issueDateEnd;

    @ApiModelProperty(name = "vdateStart", value = "起息日开始")
    private Date vdateStart;

    @ApiModelProperty(name = "vdateEnd", value = "起息日结束")
    private Date vdateEnd;

    @ApiModelProperty(name = "mdateStart", value = "到期日开始")
    private Date mdateStart;

    @ApiModelProperty(name = "mdateEnd", value = "到期日结束")
    private Date mdateEnd;

    @ApiModelProperty(name = "couponSpecies", value = "息票品种")
    private String couponSpecies;

    @ApiModelProperty(name = "rateType", value = "利率类型")
    private String rateType;

    @ApiModelProperty(name = "payCycle", value = "付息频率")
    private String payCycle;

    @ApiModelProperty(name = "currency", value = "币种")
    private String currency;

    @ApiModelProperty(name = "isRight", value = "是否含权债", example = "Y/N")
    private String isRight;

    @ApiModelProperty(name = "optionType", value = "行权方式")
    private String optionType;

    @ApiModelProperty(name = "theOryOptionPayDayStart", value = "理论行权日开始")
    private Date theOryOptionPayDayStart;

    @ApiModelProperty(name = "theOryOptionPayDayEnd", value = "理论行权日结束")
    private Date theOryOptionPayDayEnd;

    @ApiModelProperty(name = "isSus", value = "是否永续")
    private String isSus;

    @ApiModelProperty(name = "zxGrade", value = "债项评级")
    private String zxGrade;

    @ApiModelProperty(name = "ztGrade", value = "主体评级")
    private String ztGrade;

    @ApiModelProperty(name = "crossMarketCode", value = "跨市场代码")
    private String crossMarketCode;

    @ApiModelProperty(name = "guarantor", value = "担保人")
    private String guarantor;

    @ApiModelProperty(name = "platformDate", value = "平台日")
    private String platformDate;

    @ApiModelProperty(name = "search", value = "关键词")
    private String search;

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }


    public String getSecId() {
        return secId;
    }

    public void setSecId(String secId) {
        this.secId = secId;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
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

    public Date getVdateStart() {
        return vdateStart;
    }

    public void setVdateStart(Date vdateStart) {
        this.vdateStart = vdateStart;
    }

    public Date getVdateEnd() {
        return vdateEnd;
    }

    public void setVdateEnd(Date vdateEnd) {
        this.vdateEnd = vdateEnd;
    }

    public Date getMdateStart() {
        return mdateStart;
    }

    public void setMdateStart(Date mdateStart) {
        this.mdateStart = mdateStart;
    }

    public Date getMdateEnd() {
        return mdateEnd;
    }

    public void setMdateEnd(Date mdateEnd) {
        this.mdateEnd = mdateEnd;
    }

    public String getCouponSpecies() {
        return couponSpecies;
    }

    public void setCouponSpecies(String couponSpecies) {
        this.couponSpecies = couponSpecies;
    }

    public String getPayCycle() {
        return payCycle;
    }

    public void setPayCycle(String payCycle) {
        this.payCycle = payCycle;
    }

    public String getIsRight() {
        return isRight;
    }

    public void setIsRight(String isRight) {
        this.isRight = isRight;
    }

    public String getOptionType() {
        return optionType;
    }

    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }

    public Date getTheOryOptionPayDayStart() {
        return theOryOptionPayDayStart;
    }

    public void setTheOryOptionPayDayStart(Date theOryOptionPayDayStart) {
        this.theOryOptionPayDayStart = theOryOptionPayDayStart;
    }

    public Date getTheOryOptionPayDayEnd() {
        return theOryOptionPayDayEnd;
    }

    public void setTheOryOptionPayDayEnd(Date theOryOptionPayDayEnd) {
        this.theOryOptionPayDayEnd = theOryOptionPayDayEnd;
    }

    public String getIsSus() {
        return isSus;
    }

    public void setIsSus(String isSus) {
        this.isSus = isSus;
    }

    public String getZxGrade() {
        return zxGrade;
    }

    public void setZxGrade(String zxGrade) {
        this.zxGrade = zxGrade;
    }

    public String getZtGrade() {
        return ztGrade;
    }

    public void setZtGrade(String ztGrade) {
        this.ztGrade = ztGrade;
    }

    public String getCrossMarketCode() {
        return crossMarketCode;
    }

    public void setCrossMarketCode(String crossMarketCode) {
        this.crossMarketCode = crossMarketCode;
    }

    public String getGuarantor() {
        return guarantor;
    }

    public void setGuarantor(String guarantor) {
        this.guarantor = guarantor;
    }

    public String getPlatformDate() {
        return platformDate;
    }

    public void setPlatformDate(String platformDate) {
        this.platformDate = platformDate;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getSecName() {
        return secName;
    }

    public void setSecName(String secName) {
        this.secName = secName;
    }

    public String getBiddingMethod() {
        return biddingMethod;
    }

    public void setBiddingMethod(String biddingMethod) {
        this.biddingMethod = biddingMethod;
    }

    public String getRaiseMode() {
        return raiseMode;
    }

    public void setRaiseMode(String raiseMode) {
        this.raiseMode = raiseMode;
    }

    public String getTenderTarget() {
        return tenderTarget;
    }

    public void setTenderTarget(String tenderTarget) {
        this.tenderTarget = tenderTarget;
    }

    public Date getEventDateStart() {
        return eventDateStart;
    }

    public void setEventDateStart(Date eventDateStart) {
        this.eventDateStart = eventDateStart;
    }

    public Date getEventDateEnd() {
        return eventDateEnd;
    }

    public void setEventDateEnd(Date eventDateEnd) {
        this.eventDateEnd = eventDateEnd;
    }

    public String getListingCode() {
        return listingCode;
    }

    public void setListingCode(String listingCode) {
        this.listingCode = listingCode;
    }

    public String getSecType() {
        return secType;
    }

    public void setSecType(String secType) {
        this.secType = secType;
    }

    public String getIssueMethod() {
        return issueMethod;
    }

    public void setIssueMethod(String issueMethod) {
        this.issueMethod = issueMethod;
    }

    public Date getListingDateStart() {
        return listingDateStart;
    }

    public void setListingDateStart(Date listingDateStart) {
        this.listingDateStart = listingDateStart;
    }

    public Date getListingDateEnd() {
        return listingDateEnd;
    }

    public void setListingDateEnd(Date listingDateEnd) {
        this.listingDateEnd = listingDateEnd;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}