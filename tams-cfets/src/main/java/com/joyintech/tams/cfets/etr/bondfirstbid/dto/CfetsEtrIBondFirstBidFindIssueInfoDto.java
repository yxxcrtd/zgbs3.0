package com.joyintech.tams.cfets.etr.bondfirstbid.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: kchen
 * @Date: 2022/4/21 9:51
 */
@ApiModel(value="一级投标待发行信息列表",description="一级投标待发行信息列表")
public class CfetsEtrIBondFirstBidFindIssueInfoDto {

    @ApiModelProperty(name="finprodId",value="金融产品代码",example="ZC1")
    private String finprodId;

    @ApiModelProperty(name = "finprodMarketId", value = "资产代码")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="ZC名")
    private String finprodAbbr;

    @ApiModelProperty(name="issueClass",value="发行方式",example="报价发行/招标发行/簿记建档")
    private String issueClass;

    @ApiModelProperty(name="tenderType",value="招标方式")
    private String tenderType;

    @ApiModelProperty(name="issueStyle",value="发行类型")
    private String issueStyle;

    @ApiModelProperty(name="planIssueAmt",value="计划发行量")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal planIssueAmt;

    @ApiModelProperty(name="issueDate",value="投标日/发行日")
    private Date issueDate;

    @ApiModelProperty(name="payEdate",value="缴款截止日")
    private Date payEdate;

    @ApiModelProperty(name="couponSpecies",value="息票品种")
    private String couponSpecies;

    @ApiModelProperty(name="intType",value="利率类型")
    private String intType;

    @ApiModelProperty(name="issuePrice",value="发行价格")
    private BigDecimal issuePrice;

    @ApiModelProperty(name="referYield",value="参考收益率/票面利率")
    private BigDecimal referYield;

    @ApiModelProperty(name="firstConfirmDate",value="首次利率确定日")
    private Date firstConfirmDate;

    @ApiModelProperty(name="minPamt",value="单笔/标位最低量")
    private BigDecimal minPamt;

    @ApiModelProperty(name="maxPamt",value="单笔/标位最高量")
    private BigDecimal maxPamt;

    @ApiModelProperty(name = "ztGrade", value = "主体评级")
    private String ztGrade;

    @ApiModelProperty(name = "cyy", value = "币种")
    private String cyy;

    @ApiModelProperty(name = "type9", value = "资产一级分类")
    private String type9;

    @ApiModelProperty(name = "type9Value", value = "资产一级分类字典值")
    private String type9Value;

    @ApiModelProperty(name = "type10", value = "资产二级分类")
    private String type10;

    @ApiModelProperty(name = "type10Value", value = "资产二级分类字典值")
    private String type10Value;

    @ApiModelProperty(name = "type11", value = "资产三级分类")
    private String type11;

    @ApiModelProperty(name = "type11Value", value = "资产三级分类字典值")
    private String type11Value;

    @ApiModelProperty(name = "issuer", value = "发行人")
    private String issuer;

    @ApiModelProperty(name = "tradeMarket", value = "交易场所")
    private String tradeMarket;

    @ApiModelProperty(name = "subRate", value = "主体评级")
    private String subRate;

    @ApiModelProperty(name = "debRate", value = "债项评级")
    private String debRate;

    @ApiModelProperty(name = "subRateOrg", value = "主体评级机构")
    private String subRateOrg;

    @ApiModelProperty(name = "debRateOrg", value = "债项评级机构")
    private String debRateOrg;

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

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getPayEdate() {
        return payEdate;
    }

    public void setPayEdate(Date payEdate) {
        this.payEdate = payEdate;
    }

    public Date getFirstConfirmDate() {
        return firstConfirmDate;
    }

    public void setFirstConfirmDate(Date firstConfirmDate) {
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

    public String getType9() {
        return type9;
    }

    public void setType9(String type9) {
        this.type9 = type9;
    }

    public String getType9Value() {
        return type9Value;
    }

    public void setType9Value(String type9Value) {
        this.type9Value = type9Value;
    }

    public String getType10() {
        return type10;
    }

    public void setType10(String type10) {
        this.type10 = type10;
    }

    public String getType10Value() {
        return type10Value;
    }

    public void setType10Value(String type10Value) {
        this.type10Value = type10Value;
    }

    public String getType11() {
        return type11;
    }

    public void setType11(String type11) {
        this.type11 = type11;
    }

    public String getType11Value() {
        return type11Value;
    }

    public void setType11Value(String type11Value) {
        this.type11Value = type11Value;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getSubRate() {
        return subRate;
    }

    public void setSubRate(String subRate) {
        this.subRate = subRate;
    }

    public String getDebRate() {
        return debRate;
    }

    public void setDebRate(String debRate) {
        this.debRate = debRate;
    }

    public String getSubRateOrg() {
        return subRateOrg;
    }

    public void setSubRateOrg(String subRateOrg) {
        this.subRateOrg = subRateOrg;
    }

    public String getDebRateOrg() {
        return debRateOrg;
    }

    public void setDebRateOrg(String debRateOrg) {
        this.debRateOrg = debRateOrg;
    }

    public String getTenderType() {
        return tenderType;
    }

    public void setTenderType(String tenderType) {
        this.tenderType = tenderType;
    }

    public String getIssueStyle() {
        return issueStyle;
    }

    public void setIssueStyle(String issueStyle) {
        this.issueStyle = issueStyle;
    }
}
