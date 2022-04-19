package com.joyintech.tams.business.asset.tenderbond.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.dto.BaseDto;
import com.joyintech.framework.json.BigDecimalSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 债券市场信息查看DTO
 *
 * @author zhuys
 * @version 1.0 2022年4月18日
 * @see FinTenderBoundAsset004Dto
 * @since 1.0
 */
@ApiModel(value = "债券市场信息查看", description = "债券市场信息查看")
public class FinTenderBoundAsset004Dto extends BaseDto {

    @ApiModelProperty(name = "secId", value = "债券ID")
    private String secId;

    @ApiModelProperty(name = "secName", value = "债券名称")
    private String secName;

    @ApiModelProperty(name = "secFullName", value = "债券全称")
    private String secFullName;

    @ApiModelProperty(name = "secCode", value = "债券代码")
    private String secCode;

    @ApiModelProperty(name = "market", value = "交易场所")
    private String market;

    @ApiModelProperty(name = "institution", value = "登记托管机构")
    private String institution;

    @ApiModelProperty(name = "issuePrice", value = "发行价格")
    private String issuePrice;

    @ApiModelProperty(name = "pubendDate", value = "发行截止日")
    private String pubendDate;

    @ApiModelProperty(name = "vdate", value = "起息日")
    private String vdate;

    @ApiModelProperty(name = "mdate", value = "到期日")
    private String mdate;

    @ApiModelProperty(name = "issueAmt", value = "发行金额")
    private String issueAmt;

    @ApiModelProperty(name = "issuerName", value = "发行人")
    private String issuerName;

    @ApiModelProperty(name = "secType", value = "一级分类")
    private String secType;

    @ApiModelProperty(name = "secType2", value = "二级分类")
    private String secType2;

    @ApiModelProperty(name = "issueType", value = "募集方式")
    private String issueType;

    @ApiModelProperty(name = "blnArea", value = "境内外")
    private String blnArea;

    @ApiModelProperty(name = "issus", value = "是否永续债,Y是N否")
    private String issus;

    @ApiModelProperty(name = "isSecBond", value = "是否次级债,Y是N否")
    private String isSecBond;

    @ApiModelProperty(name = "cityInvestSecWind", value = "是否城投债（WIND）")
    private String cityInvestSecWind;

    @ApiModelProperty(name = "isRight", value = "是否含权债")
    private String isRight;

    @ApiModelProperty(name = "cyy", value = "币种")
    private String cyy;

    @ApiModelProperty(name = "faceValue", value = "面额")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal faceValue;

    @ApiModelProperty(name = "warrantor", value = "担保人")
    private String warrantor;

    @ApiModelProperty(name = "guarType", value = "担保方式")
    private String guarType;

    @ApiModelProperty(name = "remark", value = "特殊条款（备注）")
    private String remark;

    @ApiModelProperty(name = "couponSpecies", value = "息票品种")
    private String couponSpecies;

    @ApiModelProperty(name = "interestRate", value = "利率类型")
    private String interestRate;

    @ApiModelProperty(name = "paperir", value = "利率类型")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal paperir;

    @ApiModelProperty(name = "spreadRate8", value = "浮动利差")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal spreadRate8;

    @ApiModelProperty(name = "coefficient", value = "浮动系数")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal coefficient;

    @ApiModelProperty(name = "rateCode", value = "浮动利率code")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private String rateCode;

    @ApiModelProperty(name = "toprate", value = "利率上限")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal toprate;

    @ApiModelProperty(name = "bottomrate", value = "利率下限")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal bottomrate;

    @ApiModelProperty(name = "resetType", value = "利率调整规则")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private String resetType;

    @ApiModelProperty(name = "observeBefDay", value = "延后时间")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal observeBefDay;

    @ApiModelProperty(name = "observeBefDay", value = "延后时间单位")
    private String observeBefUnit;

    @ApiModelProperty(name = "ratebasic", value = "计息基础")
    private String ratebasic;

    @ApiModelProperty(name = "workDayRule", value = "营业日准则")
    private String workDayRule;

    @ApiModelProperty(name = "payCycle", value = "付息频率")
    private String payCycle;

    @ApiModelProperty(name = "firstRateDay", value = "首次付息日")
    private Date firstRateDay;

    @ApiModelProperty(name = "schecalRule", value = "首次付息日")
    private String schecalRule;

    @ApiModelProperty(name = "tenderBoundAsset004PayHSrcSecSchedulDtos", value = "付息计划列表")
    private List<FinTenderBoundAsset004PayHSrcSecSchedulDto> tenderBoundAsset004PayHSrcSecSchedulDtos;

    public String getSecId() {
        return secId;
    }

    public void setSecId(String secId) {
        this.secId = secId;
    }

    public String getSecName() {
        return secName;
    }

    public void setSecName(String secName) {
        this.secName = secName;
    }

    public String getSecFullName() {
        return secFullName;
    }

    public void setSecFullName(String secFullName) {
        this.secFullName = secFullName;
    }

    public String getSecCode() {
        return secCode;
    }

    public void setSecCode(String secCode) {
        this.secCode = secCode;
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

    public String getIssuePrice() {
        return issuePrice;
    }

    public void setIssuePrice(String issuePrice) {
        this.issuePrice = issuePrice;
    }

    public String getPubendDate() {
        return pubendDate;
    }

    public void setPubendDate(String pubendDate) {
        this.pubendDate = pubendDate;
    }

    public String getVdate() {
        return vdate;
    }

    public void setVdate(String vdate) {
        this.vdate = vdate;
    }

    public String getMdate() {
        return mdate;
    }

    public void setMdate(String mdate) {
        this.mdate = mdate;
    }

    public String getIssueAmt() {
        return issueAmt;
    }

    public void setIssueAmt(String issueAmt) {
        this.issueAmt = issueAmt;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public String getSecType() {
        return secType;
    }

    public void setSecType(String secType) {
        this.secType = secType;
    }

    public String getSecType2() {
        return secType2;
    }

    public void setSecType2(String secType2) {
        this.secType2 = secType2;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getBlnArea() {
        return blnArea;
    }

    public void setBlnArea(String blnArea) {
        this.blnArea = blnArea;
    }

    public String getIssus() {
        return issus;
    }

    public void setIssus(String issus) {
        this.issus = issus;
    }

    public String getIsSecBond() {
        return isSecBond;
    }

    public void setIsSecBond(String isSecBond) {
        this.isSecBond = isSecBond;
    }

    public String getCityInvestSecWind() {
        return cityInvestSecWind;
    }

    public void setCityInvestSecWind(String cityInvestSecWind) {
        this.cityInvestSecWind = cityInvestSecWind;
    }

    public String getIsRight() {
        return isRight;
    }

    public void setIsRight(String isRight) {
        this.isRight = isRight;
    }

    public String getCyy() {
        return cyy;
    }

    public void setCyy(String cyy) {
        this.cyy = cyy;
    }

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    public String getWarrantor() {
        return warrantor;
    }

    public void setWarrantor(String warrantor) {
        this.warrantor = warrantor;
    }

    public String getGuarType() {
        return guarType;
    }

    public void setGuarType(String guarType) {
        this.guarType = guarType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCouponSpecies() {
        return couponSpecies;
    }

    public void setCouponSpecies(String couponSpecies) {
        this.couponSpecies = couponSpecies;
    }

    public String getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(String interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getPaperir() {
        return paperir;
    }

    public void setPaperir(BigDecimal paperir) {
        this.paperir = paperir;
    }

    public BigDecimal getSpreadRate8() {
        return spreadRate8;
    }

    public void setSpreadRate8(BigDecimal spreadRate8) {
        this.spreadRate8 = spreadRate8;
    }

    public BigDecimal getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(BigDecimal coefficient) {
        this.coefficient = coefficient;
    }

    public String getRateCode() {
        return rateCode;
    }

    public void setRateCode(String rateCode) {
        this.rateCode = rateCode;
    }

    public BigDecimal getToprate() {
        return toprate;
    }

    public void setToprate(BigDecimal toprate) {
        this.toprate = toprate;
    }

    public BigDecimal getBottomrate() {
        return bottomrate;
    }

    public void setBottomrate(BigDecimal bottomrate) {
        this.bottomrate = bottomrate;
    }

    public String getResetType() {
        return resetType;
    }

    public void setResetType(String resetType) {
        this.resetType = resetType;
    }

    public BigDecimal getObserveBefDay() {
        return observeBefDay;
    }

    public void setObserveBefDay(BigDecimal observeBefDay) {
        this.observeBefDay = observeBefDay;
    }

    public String getObserveBefUnit() {
        return observeBefUnit;
    }

    public void setObserveBefUnit(String observeBefUnit) {
        this.observeBefUnit = observeBefUnit;
    }

    public String getRatebasic() {
        return ratebasic;
    }

    public void setRatebasic(String ratebasic) {
        this.ratebasic = ratebasic;
    }

    public String getWorkDayRule() {
        return workDayRule;
    }

    public void setWorkDayRule(String workDayRule) {
        this.workDayRule = workDayRule;
    }

    public String getPayCycle() {
        return payCycle;
    }

    public void setPayCycle(String payCycle) {
        this.payCycle = payCycle;
    }

    public Date getFirstRateDay() {
        return firstRateDay;
    }

    public void setFirstRateDay(Date firstRateDay) {
        this.firstRateDay = firstRateDay;
    }

    public String getSchecalRule() {
        return schecalRule;
    }

    public void setSchecalRule(String schecalRule) {
        this.schecalRule = schecalRule;
    }

    public List<FinTenderBoundAsset004PayHSrcSecSchedulDto> getTenderBoundAsset004PayHSrcSecSchedulDtos() {
        return tenderBoundAsset004PayHSrcSecSchedulDtos;
    }

    public void setTenderBoundAsset004PayHSrcSecSchedulDtos(List<FinTenderBoundAsset004PayHSrcSecSchedulDto> tenderBoundAsset004PayHSrcSecSchedulDtos) {
        this.tenderBoundAsset004PayHSrcSecSchedulDtos = tenderBoundAsset004PayHSrcSecSchedulDtos;
    }
}
