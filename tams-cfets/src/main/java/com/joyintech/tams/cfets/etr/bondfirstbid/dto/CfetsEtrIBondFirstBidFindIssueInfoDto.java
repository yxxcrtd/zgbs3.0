package com.joyintech.tams.cfets.etr.bondfirstbid.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * @Author: kchen
 * @Date: 2022/4/21 9:51
 */
@ApiModel(value="一级投标待发行信息列表",description="一级投标待发行信息列表")
public class CfetsEtrIBondFirstBidFindIssueInfoDto {

    @ApiModelProperty(name="finprodId",value="资产代码",example="ZC1",notes ="市场代码" )
    private String finprodId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="ZC名")
    private String finprodAbbr;

    @ApiModelProperty(name="issueClass",value="发行方式",example="报价发行/招标发行/簿记建档")
    private String issueClass;

    @ApiModelProperty(name="planIssueAmt",value="计划发行量")
    private BigDecimal planIssueAmt;

    @ApiModelProperty(name="issueDate",value="投标日/发行日")
    private String issueDate;

    @ApiModelProperty(name="payEdate",value="缴款截止日")
    private String payEdate;

    @ApiModelProperty(name="couponSpecies",value="息票品种")
    private String couponSpecies;

    @ApiModelProperty(name="intType",value="利率类型")
    private String intType;

    @ApiModelProperty(name="issuePrice",value="发行价格")
    private BigDecimal issuePrice;

    @ApiModelProperty(name="referYield",value="参考收益率/票面利率")
    private BigDecimal referYield;

    @ApiModelProperty(name="firstConfirmDate",value="首次利率确定日")
    private String firstConfirmDate;

    @ApiModelProperty(name="minPamt",value="单笔/标位最低量")
    private BigDecimal minPamt;

    @ApiModelProperty(name="maxPamt",value="单笔/标位最高量")
    private BigDecimal maxPamt;

    @ApiModelProperty(name = "ztGrade", value = "主体评级")
    private String ztGrade;

    @ApiModelProperty(name = "cyy", value = "币种")
    private String cyy;

    @ApiModelProperty(name="assetType",value="资产类型")
    private String assetType;

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
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

    public BigDecimal getPlanIssueAmt() {
        return planIssueAmt;
    }

    public void setPlanIssueAmt(BigDecimal planIssueAmt) {
        this.planIssueAmt = planIssueAmt;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(String issueDate) {
        this.issueDate = issueDate;
    }

    public String getPayEdate() {
        return payEdate;
    }

    public void setPayEdate(String payEdate) {
        this.payEdate = payEdate;
    }

    public String getCouponSpecies() {
        return couponSpecies;
    }

    public void setCouponSpecies(String couponSpecies) {
        this.couponSpecies = couponSpecies;
    }

    public String getIntType() {
        return intType;
    }

    public void setIntType(String intType) {
        this.intType = intType;
    }

    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    public void setIssuePrice(BigDecimal issuePrice) {
        this.issuePrice = issuePrice;
    }

    public BigDecimal getReferYield() {
        return referYield;
    }

    public void setReferYield(BigDecimal referYield) {
        this.referYield = referYield;
    }

    public String getFirstConfirmDate() {
        return firstConfirmDate;
    }

    public void setFirstConfirmDate(String firstConfirmDate) {
        this.firstConfirmDate = firstConfirmDate;
    }

    public BigDecimal getMinPamt() {
        return minPamt;
    }

    public void setMinPamt(BigDecimal minPamt) {
        this.minPamt = minPamt;
    }

    public BigDecimal getMaxPamt() {
        return maxPamt;
    }

    public void setMaxPamt(BigDecimal maxPamt) {
        this.maxPamt = maxPamt;
    }

    public String getZtGrade() {
        return ztGrade;
    }

    public void setZtGrade(String ztGrade) {
        this.ztGrade = ztGrade;
    }

    public String getCyy() {
        return cyy;
    }

    public void setCyy(String cyy) {
        this.cyy = cyy;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }
}
