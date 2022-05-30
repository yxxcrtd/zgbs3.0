package com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.dto.BaseDto;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.collections.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 债券市场列表信息查看DTO
 *
 * @author zhuys
 * @version 1.0 2022年4月18日
 * @see FinBondMarketInfoQuery001Dto
 * @since 1.0
 */
@ApiModel(value = "债券市场列表信息查看", description = "债券市场列表信息查看")
public class FinBondMarketInfoQuery001Dto extends BaseDto {

    @ApiModelProperty(name = "secId", value = "债券ID/资产代码")
    private String secId;

    @ApiModelProperty(name = "secName", value = "资产简称")
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
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal issuePrice;

    @ApiModelProperty(name = "pubendDate", value = "发行日（发行截止日）")
    private String pubendDate;

    @ApiModelProperty(name = "vdate", value = "起息日")
    private String vdate;

    @ApiModelProperty(name = "mdate", value = "到期日")
    private String mdate;

    @ApiModelProperty(name = "issueAmt", value = "发行金额")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal issueAmt;

    @ApiModelProperty(name = "issuerName", value = "发行人")
    private String issuerName;

    @ApiModelProperty(name = "secType", value = "一级分类")
    private String secType;

    @ApiModelProperty(name = "secType2", value = "二级分类")
    private String secType2;

    @ApiModelProperty(name = "secType3", value = "三级分类")
    private String secType3;

    @ApiModelProperty(name = "issueType", value = "募集方式")
    private String issueType;

    @ApiModelProperty(name = "blnArea", value = "境内外")
    private String blnArea;

    @ApiModelProperty(name = "issus", value = "是否永续债,Y是N否")
    private String issus;

    @ApiModelProperty(name = "isSecBond", value = "是否次级债,Y是N否")
    private String isSecBond;

    @ApiModelProperty(name = "cityInvestSecChb", value = "是否城投债（中债）")
    private String cityInvestSecChb;

    @ApiModelProperty(name = "cityInvestSecWind", value = "是否城投债（WIND）")
    private String cityInvestSecWind;

    @ApiModelProperty(name = "calloption", value = "是否可赎回,Y是N否")
    private String calloption;

    @ApiModelProperty(name = "putoption", value = "是否可回售,Y是N否")
    private String putoption;

    @ApiModelProperty(name = "isRight", value = "是否含权债")
    private String isRight;

    @ApiModelProperty(name = "ccy", value = "币种")
    private String ccy;

    @ApiModelProperty(name = "faceValue", value = "单位面值")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal faceValue;

    @ApiModelProperty(name = "guarantor", value = "担保人")
    private String guarantor;

    @ApiModelProperty(name = "guarType", value = "担保方式")
    private String guarType;

    @ApiModelProperty(name = "remark", value = "特殊条款（备注）")
    private String remark;

    @ApiModelProperty(name = "couponSpecies", value = "息票品种")
    private String couponSpecies;

    @ApiModelProperty(name = "interestRate", value = "利率类型")
    private String interestRate;

    @ApiModelProperty(name = "paperir", value = "票面利率(%)")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal paperir;

    @ApiModelProperty(name = "spreadRate8", value = "利差（BP）（浮动利差）")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal spreadRate8;

    @ApiModelProperty(name = "coefficient", value = "基准利率系数（浮动系数）")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal coefficient;

    @ApiModelProperty(name = "rateCode", value = "基准利率（浮动利率code）")
    private String rateCode;

    @ApiModelProperty(name = "toprate", value = "利率上限")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal toprate;

    @ApiModelProperty(name = "bottomrate", value = "利率下限")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal bottomrate;

    @ApiModelProperty(name = "capiIncomeFeature", value = "保本标识")
    private String capiIncomeFeature;

    @ApiModelProperty(name = "isAutoUpdate", value = "是否允许资讯数据覆盖")
    private String isAutoUpdate;

    @ApiModelProperty(name = "resetType", value = "利率调整规则")
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

    @ApiModelProperty(name = "schecalRule", value = "付息生成规则(即付息计划推算方法)")
    private String schecalRule;

    @ApiModelProperty(name = "firstConfirmDate", value = "首次利率确定日")
    private Date firstConfirmDate;

    @ApiModelProperty(name = "optionType", value = "行权方式")
    private String optionType;

    @ApiModelProperty(name = "theOryOptionPayDay", value = "理论行权日")
    private Date theOryOptionPayDay;

    @ApiModelProperty(name = "crossMarketCode", value = "跨市场代码")
    private String crossMarketCode;

    @ApiModelProperty(name = "calendarId", value = "交易日历")
    private String calendarId;

    @ApiModelProperty(name = "ztGradeResult", value = "主体评级结果")
    private String ztGradeResult;

    @ApiModelProperty(name = "ztGradeOrgId", value = "主体评级机构")
    private String ztGradeOrgId;

    @ApiModelProperty(name = "zxGradeResult", value = "债项评级结果")
    private String zxGradeResult;

    @ApiModelProperty(name = "zxGradeOrgId", value = "债项评级机构")
    private String zxGradeOrgId;

    @ApiModelProperty(name = "isGreenBond", value = "是否绿色债")
    private String isGreenBond;

    @ApiModelProperty(name = "delistingDate", value = "摘牌日")
    private String delistingDate;

    @ApiModelProperty(name = "conceptType", value = "概念板块Code")
    private List<String> conceptType;

    @ApiModelProperty(name = "conceptTypeStr", value = "概念板块")
    private String conceptTypeStr;

    @ApiModelProperty(name = "finProductConvertibleDto", value = "债项市场转股信息")
    private FinProductConvertibleDto finProductConvertibleDto;

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


    public String getMarketStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, this.getMarket());
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getInstitution() {
        return institution;
    }

    public String getInstitutionStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.登记托管机构, this.getInstitution());
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    public void setIssuePrice(BigDecimal issuePrice) {
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

    public BigDecimal getIssueAmt() {
        return issueAmt;
    }

    public void setIssueAmt(BigDecimal issueAmt) {
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

    public String getSecTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.WIND一级分类_含债券一级分类等, this.getSecType());
    }

    public void setSecType(String secType) {
        this.secType = secType;
    }

    public String getSecType2Str() {
        return SysDataUtil.getNameFromDataDict(DictCode.WIND二级分类_含债券二级分类等, this.getSecType2());
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

    public String getIssueTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.募集方式, this.getIssueType());
    }


    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getBlnArea() {
        return blnArea;
    }

    public String getBlnAreaStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.境内外, this.getBlnArea());
    }

    public void setBlnArea(String blnArea) {
        this.blnArea = blnArea;
    }

    public String getIssus() {
        return issus;
    }

    public String getIssusStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIssus());
    }

    public String getIsSecBondStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsSecBond());
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


    public String getCityInvestSecWindStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getCityInvestSecWind());
    }

    public String getCityInvestSecWind() {
        return cityInvestSecWind;
    }

    public void setCityInvestSecWind(String cityInvestSecWind) {
        this.cityInvestSecWind = cityInvestSecWind;
    }

    public String getIsRightStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsRight());
    }

    public String getIsRight() {
        return isRight;
    }

    public void setIsRight(String isRight) {
        this.isRight = isRight;
    }

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
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

    public String getCouponSpeciesStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.息票品种, this.getCouponSpecies());
    }

    public void setCouponSpecies(String couponSpecies) {
        this.couponSpecies = couponSpecies;
    }

    public String getInterestRate() {
        return interestRate;
    }

    public String getInterestRateStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.利率类型, this.getInterestRate());
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

    public String getPayCycleStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.频率, this.getPayCycle());
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

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getFirstConfirmDate() {
        return firstConfirmDate;
    }

    public void setFirstConfirmDate(Date firstConfirmDate) {
        this.firstConfirmDate = firstConfirmDate;
    }

    public String getCrossMarketCode() {
        return crossMarketCode;
    }

    public void setCrossMarketCode(String crossMarketCode) {
        this.crossMarketCode = crossMarketCode;
    }

    public String getCalendarId() {
        return calendarId;
    }

    public String getCalendarIdStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易日历, this.getCalendarId());
    }

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    public String getZtGradeResult() {
        return ztGradeResult;
    }

    public void setZtGradeResult(String ztGradeResult) {
        this.ztGradeResult = ztGradeResult;
    }

    public String getZtGradeOrgId() {
        return ztGradeOrgId;
    }

    public void setZtGradeOrgId(String ztGradeOrgId) {
        this.ztGradeOrgId = ztGradeOrgId;
    }

    public String getZxGradeResult() {
        return zxGradeResult;
    }

    public void setZxGradeResult(String zxGradeResult) {
        this.zxGradeResult = zxGradeResult;
    }

    public String getZxGradeOrgId() {
        return zxGradeOrgId;
    }

    public void setZxGradeOrgId(String zxGradeOrgId) {
        this.zxGradeOrgId = zxGradeOrgId;
    }

    public String getGuarantor() {
        return guarantor;
    }

    public void setGuarantor(String guarantor) {
        this.guarantor = guarantor;
    }

    public String getOptionType() {
        return optionType;
    }


    public String getOptionTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.行权方式, this.getOptionType());
    }

    public void setOptionType(String optionType) {
        this.optionType = optionType;
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getTheOryOptionPayDay() {
        return theOryOptionPayDay;
    }

    public void setTheOryOptionPayDay(Date theOryOptionPayDay) {
        this.theOryOptionPayDay = theOryOptionPayDay;
    }

    public String getCcy() {
        return ccy;
    }


    public String getCcyStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, this.getCcy());
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getSecType3() {
        return secType3;
    }

    public void setSecType3(String secType3) {
        this.secType3 = secType3;
    }

    public String getCapiIncomeFeature() {
        return capiIncomeFeature;
    }

    public void setCapiIncomeFeature(String capiIncomeFeature) {
        this.capiIncomeFeature = capiIncomeFeature;
    }

    public String getIsAutoUpdate() {
        return isAutoUpdate;
    }

    public void setIsAutoUpdate(String isAutoUpdate) {
        this.isAutoUpdate = isAutoUpdate;
    }

    public FinProductConvertibleDto getFinProductConvertibleDto() {
        return finProductConvertibleDto;
    }

    public void setFinProductConvertibleDto(FinProductConvertibleDto finProductConvertibleDto) {
        this.finProductConvertibleDto = finProductConvertibleDto;
    }

    public String getIsGreenBond() {
        return isGreenBond;
    }


    public String getIsGreenBondStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsGreenBond());
    }

    public void setIsGreenBond(String isGreenBond) {
        this.isGreenBond = isGreenBond;
    }

    public String getDelistingDate() {
        return delistingDate;
    }

    public void setDelistingDate(String delistingDate) {
        this.delistingDate = delistingDate;
    }

    public List<String> getConceptType() {
        List<String> conceptType = new ArrayList<>();
        if (DictCodeList.是否_是.getValue().equals(this.getCityInvestSecChb())) {
            conceptType.add("01");
        }
        if (DictCodeList.是否_是.getValue().equals(this.getIsGreenBond())) {
            conceptType.add("02");
        }
        if (DictCodeList.是否_是.getValue().equals(this.getIsSecBond())) {
            conceptType.add("04");
        }
        if (DictCodeList.是否_是.getValue().equals(this.getIssus())) {
            conceptType.add("05");
        }
        if (DictCodeList.是否_是.getValue().equals(this.getCalloption()) || DictCodeList.是否_是.getValue().equals(this.getPutoption())) {
            conceptType.add("06");
        }
        return conceptType;
    }

    public void setConceptType(List<String> conceptType) {
        this.conceptType = conceptType;
    }

    public String getConceptTypeStr() {
        List<String> conceptTypes = getConceptType();
        if (CollectionUtils.isEmpty(conceptTypes)) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        conceptTypes.stream().forEach(conceptType -> builder.append(SysDataUtil.getNameFromDataDict(DictCode.概念标识, conceptType)).append(","));
        return builder.toString();
    }

    public void setConceptTypeStr(String conceptTypeStr) {
        this.conceptTypeStr = conceptTypeStr;
    }

    public String getCityInvestSecChb() {
        return cityInvestSecChb;
    }

    public void setCityInvestSecChb(String cityInvestSecChb) {
        this.cityInvestSecChb = cityInvestSecChb;
    }

    public String getCalloption() {
        return calloption;
    }

    public void setCalloption(String calloption) {
        this.calloption = calloption;
    }

    public String getPutoption() {
        return putoption;
    }

    public void setPutoption(String putoption) {
        this.putoption = putoption;
    }
}
