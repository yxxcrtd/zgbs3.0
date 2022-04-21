package com.joyintech.tams.invest.batch.tenderbond.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

public class FinTenderBondAssetDto {

    @ApiModelProperty(name = "issueStage", value = "发行阶段")
    private String issueStage;

    @ApiModelProperty(name = "issueStatus", value = "发行状态")
    private String issueStatus;

    @ApiModelProperty(name = "finprodId", value = "金融产品代码")
    private String finprodId;

    @ApiModelProperty(name = "finprodMarketId", value = "资产代码")
    private String finprodMarketId;

    @ApiModelProperty(name = "finprodAbbr", value = "资产简称")
    private String finprodAbbr;

    @ApiModelProperty(name = "finprodName", value = "资产名称")
    private String finprodName;

    @ApiModelProperty(name = "tradeMarket", value = "交易场所")
    private String tradeMarket;

    @ApiModelProperty(name = "registOrg", value = "登记托管机构")
    private String registOrg;

    @ApiModelProperty(name = "issuer", value = "发行人")
    private String issuer;

    @ApiModelProperty(name = "issueType", value = "募集方式")
    private String issueType;

    @ApiModelProperty(name = "issueClass", value = "发行方式")
    private String issueClass;

    @ApiModelProperty(name = "tenderType", value = "招标方式")
    private String tenderType;

    @ApiModelProperty(name = "tenderTarget", value = "招标标的")
    private String tenderTarget;

    @ApiModelProperty(name = "issueStyle", value = "发行类型")
    private String issueStyle;

    @ApiModelProperty(name = "planIssueAmt", value = "计划发行量(亿元)")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal planIssueAmt;

    @ApiModelProperty(name = "issueDate", value = "发行日")
    private Date issueDate;

    @ApiModelProperty(name = "payEdate", value = "缴款截止日")
    private Date payEdate;

    @ApiModelProperty(name = "assignRegdate", value = "债权债务登记日")
    private Date assignRegdate;

    @ApiModelProperty(name = "listingDate", value = "上市日期")
    private Date listingDate;

    @ApiModelProperty(name = "appearMarketId", value = "上市代码")
    private String appearMarketId;

    @ApiModelProperty(name = "appearMarketId", value = "上市简称")
    private String appearMarketAbbr;

    @ApiModelProperty(name = "crossMarket", value = "跨市场发行")
    private String crossMarket;

    @ApiModelProperty(name = "crossMarketCode", value = "跨市场代码")
    private String crossMarketCode;

    @ApiModelProperty(name = "firstConfirmDate", value = "首次利率确定日")
    private Date firstConfirmDate;

    @ApiModelProperty(name = "vdate", value = "起息日")
    private Date vdate;

    @ApiModelProperty(name = "mdate", value = "到期日")
    private Date mdate;

    @ApiModelProperty(name = "type9", value = "资产一级分类")
    private String type9;

    @ApiModelProperty(name = "type10", value = "资产二级分类")
    private String type10;

    @ApiModelProperty(name = "type11", value = "资产三级分类")
    private String type11;

    @ApiModelProperty(name = "couponSpecies", value = "息票品种")
    private String couponSpecies;

    @ApiModelProperty(name = "intType", value = "利率类型")
    private String intType;

    @ApiModelProperty(name = "payCycle", value = "付息频率,月付/季付/年付/半年付/到期一次")
    private String payCycle;

    @ApiModelProperty(name = "ccy", value = "币种")
    private String ccy;

    @ApiModelProperty(name = "issuePrice", value = "发行价格")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal issuePrice;

    @ApiModelProperty(name = "referYield", value = "收益率，利息贴现为参考收益率，附息为票面利率")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal referYield;

    @ApiModelProperty(name = "minPerTamt", value = "每标位最低投标量")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal minPerTamt;

    @ApiModelProperty(name = "maxPerUtamt", value = "每标位最高投标量")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal maxPerUtamt;

    @ApiModelProperty(name="circulationCode",value="上市流通代码")
    private String circulationCode;

    @ApiModelProperty(name="circulationDate",value="上市流通日期")
    private Date circulationDate;

    @ApiModelProperty(name = "remark", value = "备注")
    private String remark;

    public String getIssueStage() {
        return issueStage;
    }

    public void setIssueStage(String issueStage) {
        this.issueStage = issueStage;
    }

    public String getIssueStatus() {
        return issueStatus;
    }

    public void setIssueStatus(String issueStatus) {
        this.issueStatus = issueStatus;
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

    public String getRegistOrg() {
        return registOrg;
    }

    public void setRegistOrg(String registOrg) {
        this.registOrg = registOrg;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getIssueClass() {
        return issueClass;
    }

    public void setIssueClass(String issueClass) {
        this.issueClass = issueClass;
    }

    public String getTenderType() {
        return tenderType;
    }

    public void setTenderType(String tenderType) {
        this.tenderType = tenderType;
    }

    public String getTenderTarget() {
        return tenderTarget;
    }

    public void setTenderTarget(String tenderTarget) {
        this.tenderTarget = tenderTarget;
    }

    public String getIssueStyle() {
        return issueStyle;
    }

    public void setIssueStyle(String issueStyle) {
        this.issueStyle = issueStyle;
    }

    public BigDecimal getPlanIssueAmt() {
        return planIssueAmt;
    }

    public void setPlanIssueAmt(BigDecimal planIssueAmt) {
        this.planIssueAmt = planIssueAmt;
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

    public Date getAssignRegdate() {
        return assignRegdate;
    }

    public void setAssignRegdate(Date assignRegdate) {
        this.assignRegdate = assignRegdate;
    }

    public Date getListingDate() {
        return listingDate;
    }

    public void setListingDate(Date listingDate) {
        this.listingDate = listingDate;
    }

    public String getAppearMarketId() {
        return appearMarketId;
    }

    public void setAppearMarketId(String appearMarketId) {
        this.appearMarketId = appearMarketId;
    }

    public String getAppearMarketAbbr() {
        return appearMarketAbbr;
    }

    public void setAppearMarketAbbr(String appearMarketAbbr) {
        this.appearMarketAbbr = appearMarketAbbr;
    }

    public String getCrossMarket() {
        return crossMarket;
    }

    public void setCrossMarket(String crossMarket) {
        this.crossMarket = crossMarket;
    }

    public String getCrossMarketCode() {
        return crossMarketCode;
    }

    public void setCrossMarketCode(String crossMarketCode) {
        this.crossMarketCode = crossMarketCode;
    }

    public Date getFirstConfirmDate() {
        return firstConfirmDate;
    }

    public void setFirstConfirmDate(Date firstConfirmDate) {
        this.firstConfirmDate = firstConfirmDate;
    }

    public Date getVdate() {
        return vdate;
    }

    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    public String getType9() {
        return type9;
    }

    public void setType9(String type9) {
        this.type9 = type9;
    }

    public String getType10() {
        return type10;
    }

    public void setType10(String type10) {
        this.type10 = type10;
    }

    public String getType11() {
        return type11;
    }

    public void setType11(String type11) {
        this.type11 = type11;
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

    public String getPayCycle() {
        return payCycle;
    }

    public void setPayCycle(String payCycle) {
        this.payCycle = payCycle;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
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

    public BigDecimal getMinPerTamt() {
        return minPerTamt;
    }

    public void setMinPerTamt(BigDecimal minPerTamt) {
        this.minPerTamt = minPerTamt;
    }

    public BigDecimal getMaxPerUtamt() {
        return maxPerUtamt;
    }

    public void setMaxPerUtamt(BigDecimal maxPerUtamt) {
        this.maxPerUtamt = maxPerUtamt;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCirculationCode() {
        return circulationCode;
    }

    public void setCirculationCode(String circulationCode) {
        this.circulationCode = circulationCode;
    }

    public Date getCirculationDate() {
        return circulationDate;
    }

    public void setCirculationDate(Date circulationDate) {
        this.circulationDate = circulationDate;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }
}
