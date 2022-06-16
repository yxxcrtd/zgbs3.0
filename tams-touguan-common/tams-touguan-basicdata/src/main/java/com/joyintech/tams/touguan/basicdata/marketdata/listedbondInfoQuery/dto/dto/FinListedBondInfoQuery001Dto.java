package com.joyintech.tams.touguan.basicdata.marketdata.listedbondInfoQuery.dto.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.dto.BaseDto;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCodeList;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondMarketCashPlanDto;
import com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.dto.FinBondPayHSrcSecOptionDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.compress.utils.Lists;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 债券市场列表信息查看DTO
 *
 * @author zhuys
 * @version 1.0 2022年4月18日
 * @see FinListedBondInfoQuery001Dto
 * @since 1.0
 */
@ApiModel(value = "债券市场列表详情信息查看", description = "债券市场列表详情信息查看")
public class FinListedBondInfoQuery001Dto extends BaseDto {

    @ApiModelProperty(name = "secId", value = "债券ID/资产代码", required = true)
    private String secId;

    @ApiModelProperty(name = "finprodId", value = "金融产品代码")
    private String finprodId;

    @ApiModelProperty(name = "bondName", value = "债券名称", required = true)
    private String bondName;

    @ApiModelProperty(name = "bondAbbreviation", value = "债券简称", required = true)
    private String bondAbbreviation;

    @ApiModelProperty(name = "tradingPlaces", value = "交易场所", required = true)
    private String tradingPlaces;

    @ApiModelProperty(name = "registeredCustodian", value = "登记托管机构", required = true)
    private String registeredCustodian;

    @ApiModelProperty(name = "biddingMethod", value = "招标方式")
    private String biddingMethod;

    @ApiModelProperty(name = "tenderTarget", value = "招标标的")
    private String tenderTarget;

    @ApiModelProperty(name = "additionalBatches", value = "增发批次")
    private Integer additionalBatches;

    @ApiModelProperty(name = "issuer", value = "发行人", required = true)
    private String issuer;

    @ApiModelProperty(name = "issuerName", value = "发行人")
    private String issuerName;

    @ApiModelProperty(name = "remark", value = "特殊条款（备注）")
    private String remark;

    @ApiModelProperty(name = "crossMarketCode", value = "跨市场代码")
    private String crossMarketCode;

    @ApiModelProperty(name = "raiseMode", value = "募集方式", required = true)
    private String raiseMode;

    @ApiModelProperty(name = "domesticAndForeign", value = "境内外")
    private String domesticAndForeign;

    @ApiModelProperty(name = "isBondsWithRights", value = "是否含权债券")
    private String isBondsWithRights;

    @ApiModelProperty(name = "tradingCalendar", value = "交易日历")
    private String tradingCalendar;

    @ApiModelProperty(name = "specialTerms", value = "特殊条款")
    private String specialTerms;

    @ApiModelProperty(name = "releaseType", value = "发行类型")
    private String releaseType;

    @ApiModelProperty(name = "issueMethod", value = "发行方式", required = true)
    private String issueMethod;

    @ApiModelProperty(name = "issueSize", value = "计划发行量")
    private String issueSize;

    @ApiModelProperty(name = "issueDate", value = "发行日")
    private Date issueDate;

    @ApiModelProperty(name = "eventDate", value = "缴款截止日")
    private Date eventDate;

    @ApiModelProperty(name = "payDate", value = "缴款日")
    private Date payDate;

    @ApiModelProperty(name = "debtRegistrationDate", value = "债权债务登记日")
    private Date debtRegistrationDate;

    @ApiModelProperty(name = "listingDate", value = "上市日")
    private Date listingDate;

    @ApiModelProperty(name = "listingCode", value = "上市代码")
    private String listingCode;

    @ApiModelProperty(name = "interestAccrualDate", value = "首次利率确定日")
    private Date interestAccrualDate;

    @ApiModelProperty(name = "valueDate", value = "起息日", required = true)
    private Date valueDate;

    @ApiModelProperty(name = "expiryDate", value = "到期日", required = true)
    private Date expiryDate;

    @ApiModelProperty(name = "primaryClassification", value = "一级分类")
    private String primaryClassification;

    @ApiModelProperty(name = "secondaryClassification", value = "二级分类")
    private String secondaryClassification;

    @ApiModelProperty(name = "threearyClassification", value = "三级分类")
    private String threearyClassification;

    @ApiModelProperty(name = "couponSpecies", value = "息票品种", required = true)
    private String couponSpecies;

    @ApiModelProperty(name = "interestPaymentFrequency", value = "付息频率")
    private String interestPaymentFrequency;

    @ApiModelProperty(name = "rateType", value = "利率类型", required = true)
    private String rateType;

    @ApiModelProperty(name = "currency", value = "币种", required = true)
    private String currency;

    @ApiModelProperty(name = "issuePx", value = "发行价格（元）")
    private String issuePx;

    @ApiModelProperty(name = "issueAmt", value = "发行金额（亿元）")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal issueAmt;

    @ApiModelProperty(name = "stipulationValue", value = "参考收益率")
    private String stipulationValue;

    @ApiModelProperty(name = "singleMinSubAmt", value = "单笔最低认购量（亿元）")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal singleMinSubAmt;

    @ApiModelProperty(name = "singleMaxSubAmt", value = "单笔最高认购量（亿元）")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal singleMaxSubAmt;

    @ApiModelProperty(name = "couponRate", value = "票面利率")
    private String couponRate;

    @ApiModelProperty(name = "benchmarkCurveName", value = "基准利率")
    private String benchmarkCurveName;

    @ApiModelProperty(name = "decimalPlaces", value = "基准利率精度")
    private String decimalPlaces;

    @ApiModelProperty(name = "spread", value = "利差")
    private String spread;

    @ApiModelProperty(name = "issueStartTime", value = "发行开始时间")
    private Date issueStartTime;

    @ApiModelProperty(name = "issueEndTime", value = "发行结束时间")
    private Date issueEndTime;

    @ApiModelProperty(name = "bidBeginDate", value = "投标日开始时间")
    private String bidBeginDate;

    @ApiModelProperty(name = "bidEndDate", value = "投标日结束时间")
    private String bidEndDate;

    @ApiModelProperty(name = "additionalTenderBeginDate", value = "增发投标日开始时间")
    private String additionalTenderBeginDate;

    @ApiModelProperty(name = "additionalTenderEndDate", value = "增发投标日结束时间")
    private String additionalTenderEndDate;

    @ApiModelProperty(name = "additionalTenderDate", value = "增发投标日")
    private String additionalTenderDate;

    @ApiModelProperty(name = "maxSubAmt", value = "最高认购总量（亿元）")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal maxSubAmt;

    @ApiModelProperty(name = "minPerTenderAmt", value = "每标位最低投标量（亿元）")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal minPerTenderAmt;

    @ApiModelProperty(name = "maxPerTenderAmt", value = "每标位最高投标量（亿元）")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal maxPerTenderAmt;

    @ApiModelProperty(name = "minTenderPxForPlan", value = "最低标位")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal minTenderPxForPlan;

    @ApiModelProperty(name = "maxTenderPxForPlan", value = "最高标位")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal maxTenderPxForPlan;

    @ApiModelProperty(name = "tenderStep", value = "投标步长")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tenderStep;

    @ApiModelProperty(name = "medianMaxBidMarked", value = "最大投标标位数")
    private Integer medianMaxBidMarked;

    @ApiModelProperty(name = "maxTenderPxDiffer", value = "最大投标标位差（个）")
    private Integer maxTenderPxDiffer;

    @ApiModelProperty(name = "continuousTenderLimit", value = "连续投标限制")
    private String continuousTenderLimit;

    @ApiModelProperty(name = "maxTotalTenderAmt", value = "最高投标总量")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal maxTotalTenderAmt;

    @ApiModelProperty(name = "faceValue", value = "面值")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal faceValue;

    @ApiModelProperty(name = "dayCount", value = "计息基准")
    private String dayCount;

    @ApiModelProperty(name = "isAutoUpdate", value = "是否允许资讯数据覆盖", required = true)
    private String isAutoUpdate;

    @ApiModelProperty(name = "capiIncomeFeature", value = "保本标识")
    private String capiIncomeFeature;

    @ApiModelProperty(name = "delistingDate", value = "摘牌日")
    private Date delistingDate;

    @ApiModelProperty(name = "originalOwner", value = "原始权益人")
    private String originalOwner;

    @ApiModelProperty(name = "issus", value = "是否永续债,Y是N否")
    private String issus;

    @ApiModelProperty(name = "issecbond", value = "是否次级债,Y是N否")
    private String issecbond;

    @ApiModelProperty(name = "guartype", value = "担保方式")
    private String guartype;

    @ApiModelProperty(name = "finBondPayHSrcSecOptionDto", value = "行权计划信息")
    private List<FinBondPayHSrcSecOptionDto> bondPayHSrcSecOptionDtos;

    @ApiModelProperty(name = "bondMarketCashPlanDtos", value = "付息计划信息")
    private List<FinBondMarketCashPlanDto> bondMarketCashPlanDtos;

    public String getSecId() {
        return secId;
    }

    public void setSecId(String secId) {
        this.secId = secId;
    }

    public String getBondName() {
        return bondName;
    }

    public void setBondName(String bondName) {
        this.bondName = bondName;
    }

    public String getBondAbbreviation() {
        return bondAbbreviation;
    }

    public void setBondAbbreviation(String bondAbbreviation) {
        this.bondAbbreviation = bondAbbreviation;
    }

    public String getTradingPlaces() {
        return tradingPlaces;
    }


    public String getTradingPlacesStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, this.getTradingPlaces());
    }

    public void setTradingPlaces(String tradingPlaces) {
        this.tradingPlaces = tradingPlaces;
    }

    public String getRegisteredCustodian() {
        return registeredCustodian;
    }


    public String getRegisteredCustodianStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.登记托管机构, this.getRegisteredCustodian());
    }

    public void setRegisteredCustodian(String registeredCustodian) {
        this.registeredCustodian = registeredCustodian;
    }

    public String getBiddingMethod() {
        return biddingMethod;
    }

    public String getBiddingMethodStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.招标方式, this.getBiddingMethod());
    }

    public void setBiddingMethod(String biddingMethod) {
        this.biddingMethod = biddingMethod;
    }

    public String getTenderTarget() {
        return tenderTarget;
    }

    public String getTenderTargetStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.招标标的, this.getTenderTarget());
    }

    public void setTenderTarget(String tenderTarget) {
        this.tenderTarget = tenderTarget;
    }

    public Integer getAdditionalBatches() {
        return additionalBatches;
    }

    public void setAdditionalBatches(Integer additionalBatches) {
        this.additionalBatches = additionalBatches;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getCrossMarketCode() {
        return crossMarketCode;
    }

    public void setCrossMarketCode(String crossMarketCode) {
        this.crossMarketCode = crossMarketCode;
    }

    public String getRaiseMode() {
        return raiseMode;
    }

    public String getRaiseModeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.债券类资产募集方式, this.getRaiseMode());
    }

    public void setRaiseMode(String raiseMode) {
        this.raiseMode = raiseMode;
    }

    public String getDomesticAndForeign() {
        return domesticAndForeign;
    }


    public String getDomesticAndForeignStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.境内外, this.getDomesticAndForeign());
    }

    public void setDomesticAndForeign(String domesticAndForeign) {
        this.domesticAndForeign = domesticAndForeign;
    }

    public String getIsBondsWithRights() {
        return isBondsWithRights;
    }

    public void setIsBondsWithRights(String isBondsWithRights) {
        this.isBondsWithRights = isBondsWithRights;
    }

    public String getTradingCalendar() {
        return tradingCalendar;
    }

    public void setTradingCalendar(String tradingCalendar) {
        this.tradingCalendar = tradingCalendar;
    }

    public String getSpecialTerms() {
        return specialTerms;
    }

    public void setSpecialTerms(String specialTerms) {
        this.specialTerms = specialTerms;
    }

    public String getReleaseType() {
        return releaseType;
    }

    public void setReleaseType(String releaseType) {
        this.releaseType = releaseType;
    }

    public String getIssueMethod() {
        return issueMethod;
    }

    public String getIssueMethodStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.新债_发行方式, this.getIssueMethod());
    }

    public void setIssueMethod(String issueMethod) {
        this.issueMethod = issueMethod;
    }

    public String getIssueSize() {
        return issueSize;
    }

    public void setIssueSize(String issueSize) {
        this.issueSize = issueSize;
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getEventDate() {
        return eventDate;
    }

    public void setEventDate(Date eventDate) {
        this.eventDate = eventDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getDebtRegistrationDate() {
        return debtRegistrationDate;
    }

    public void setDebtRegistrationDate(Date debtRegistrationDate) {
        this.debtRegistrationDate = debtRegistrationDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getListingDate() {
        return listingDate;
    }

    public void setListingDate(Date listingDate) {
        this.listingDate = listingDate;
    }

    public String getListingCode() {
        return listingCode;
    }

    public void setListingCode(String listingCode) {
        this.listingCode = listingCode;
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getInterestAccrualDate() {
        return interestAccrualDate;
    }

    public void setInterestAccrualDate(Date interestAccrualDate) {
        this.interestAccrualDate = interestAccrualDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getValueDate() {
        return valueDate;
    }

    public void setValueDate(Date valueDate) {
        this.valueDate = valueDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getPrimaryClassification() {
        return primaryClassification;
    }

    public void setPrimaryClassification(String primaryClassification) {
        this.primaryClassification = primaryClassification;
    }

    public String getPrimaryClassificationStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.WIND一级分类_含债券一级分类等, this.getPrimaryClassification());
    }

    public String getSecondaryClassificationStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.WIND二级分类_含债券二级分类等, this.getSecondaryClassification());
    }

    public String getSecondaryClassification() {
        return secondaryClassification;
    }

    public void setSecondaryClassification(String secondaryClassification) {
        this.secondaryClassification = secondaryClassification;
    }

    public String getInterestPaymentFrequency() {
        return interestPaymentFrequency;
    }


    public String getInterestPaymentFrequencyStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.频率, this.getInterestPaymentFrequency());
    }

    public void setInterestPaymentFrequency(String interestPaymentFrequency) {
        this.interestPaymentFrequency = interestPaymentFrequency;
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

    public String getCurrencyStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, this.getCurrency());
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getIssuePx() {
        if (Objects.nonNull(getCouponSpecies()) && (getCouponSpecies().equals(BussinessDictCodeList.息票品种_附息.getValue()) || getCouponSpecies().equals(BussinessDictCodeList.息票品种_利随本清.getValue()))) {
            return "100.0000";
        }
        return issuePx;
    }

    public void setIssuePx(String issuePx) {
        this.issuePx = issuePx;
    }

    public String getStipulationValue() {
        return stipulationValue;
    }


    public String getStipulationValueStr() {
        String couponSpecies = getCouponSpecies();
        String rateType = this.getRateType();
        String spread = getSpread();
        String benchmarkCurveNameStr = getBenchmarkCurveNameStr();
        if (Objects.isNull(couponSpecies) || BussinessDictCodeList.息票品种_零息.getValue().equals(couponSpecies)
                || BussinessDictCodeList.息票品种_贴现.getValue().equals(couponSpecies)) {
            return Objects.nonNull(getStipulationValue()) ? getStipulationValue() + "%" : "";
        } else if (
                (couponSpecies.equals(BussinessDictCodeList.息票品种_利随本清.getValue())) ||
                        (couponSpecies.equals(BussinessDictCodeList.息票品种_附息.getValue()) && BussinessDictCodeList.利率类型_固定利率.getValue().equals(rateType))
        ) {
            return Objects.nonNull(getCouponRate()) ? getCouponRate() + "%" : "";
        } else if (couponSpecies.equals(BussinessDictCodeList.息票品种_附息.getValue()) && BussinessDictCodeList.利率类型_浮动利率.getValue().equals(rateType)) {
            return (StringUtil.isNotBlank(benchmarkCurveNameStr) ? benchmarkCurveNameStr : "") + "+" + (StringUtil.isNotBlank(spread) ? spread : "");
        }
        return stipulationValue;
    }

    public void setStipulationValue(String stipulationValue) {
        this.stipulationValue = stipulationValue;
    }

    public BigDecimal getSingleMinSubAmt() {
        return singleMinSubAmt;
    }

    public void setSingleMinSubAmt(BigDecimal singleMinSubAmt) {
        this.singleMinSubAmt = singleMinSubAmt;
    }

    public BigDecimal getSingleMaxSubAmt() {
        return singleMaxSubAmt;
    }

    public void setSingleMaxSubAmt(BigDecimal singleMaxSubAmt) {
        this.singleMaxSubAmt = singleMaxSubAmt;
    }

    public String getCouponRate() {
        return couponRate;
    }

    public void setCouponRate(String couponRate) {
        this.couponRate = couponRate;
    }

    public String getBenchmarkCurveName() {
        return benchmarkCurveName;
    }

    public String getBenchmarkCurveNameStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.利率行情, this.getBenchmarkCurveName());
    }

    public void setBenchmarkCurveName(String benchmarkCurveName) {
        this.benchmarkCurveName = benchmarkCurveName;
    }

    public String getDecimalPlaces() {
        return decimalPlaces;
    }

    public void setDecimalPlaces(String decimalPlaces) {
        this.decimalPlaces = decimalPlaces;
    }

    public String getSpread() {
        return spread;
    }

    public void setSpread(String spread) {
        this.spread = spread;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getIssueStartTime() {
        return issueStartTime;
    }

    public void setIssueStartTime(Date issueStartTime) {
        this.issueStartTime = issueStartTime;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getIssueEndTime() {
        return issueEndTime;
    }

    public void setIssueEndTime(Date issueEndTime) {
        this.issueEndTime = issueEndTime;
    }

    public String getAdditionalTenderDate() {
        return additionalTenderDate;
    }

    public void setAdditionalTenderDate(String additionalTenderDate) {
        this.additionalTenderDate = additionalTenderDate;
    }

    public BigDecimal getMaxSubAmt() {
        return maxSubAmt;
    }

    public void setMaxSubAmt(BigDecimal maxSubAmt) {
        this.maxSubAmt = maxSubAmt;
    }

    public BigDecimal getMinPerTenderAmt() {
        return minPerTenderAmt;
    }

    public void setMinPerTenderAmt(BigDecimal minPerTenderAmt) {
        this.minPerTenderAmt = minPerTenderAmt;
    }

    public BigDecimal getMaxPerTenderAmt() {
        return maxPerTenderAmt;
    }

    public void setMaxPerTenderAmt(BigDecimal maxPerTenderAmt) {
        this.maxPerTenderAmt = maxPerTenderAmt;
    }

    public BigDecimal getMinTenderPxForPlan() {
        return minTenderPxForPlan;
    }

    public void setMinTenderPxForPlan(BigDecimal minTenderPxForPlan) {
        this.minTenderPxForPlan = minTenderPxForPlan;
    }

    public BigDecimal getMaxTenderPxForPlan() {
        return maxTenderPxForPlan;
    }

    public void setMaxTenderPxForPlan(BigDecimal maxTenderPxForPlan) {
        this.maxTenderPxForPlan = maxTenderPxForPlan;
    }

    public BigDecimal getTenderStep() {
        return tenderStep;
    }

    public void setTenderStep(BigDecimal tenderStep) {
        this.tenderStep = tenderStep;
    }

    public Integer getMedianMaxBidMarked() {
        return medianMaxBidMarked;
    }

    public void setMedianMaxBidMarked(Integer medianMaxBidMarked) {
        this.medianMaxBidMarked = medianMaxBidMarked;
    }

    public Integer getMaxTenderPxDiffer() {
        return maxTenderPxDiffer;
    }

    public void setMaxTenderPxDiffer(Integer maxTenderPxDiffer) {
        this.maxTenderPxDiffer = maxTenderPxDiffer;
    }

    public String getContinuousTenderLimit() {
        return continuousTenderLimit;
    }

    public void setContinuousTenderLimit(String continuousTenderLimit) {
        this.continuousTenderLimit = continuousTenderLimit;
    }

    public BigDecimal getMaxTotalTenderAmt() {
        return maxTotalTenderAmt;
    }

    public void setMaxTotalTenderAmt(BigDecimal maxTotalTenderAmt) {
        this.maxTotalTenderAmt = maxTotalTenderAmt;
    }

    public BigDecimal getFaceValue() {
        return faceValue;
    }

    public void setFaceValue(BigDecimal faceValue) {
        this.faceValue = faceValue;
    }

    public String getDayCount() {
        return dayCount;
    }

    public void setDayCount(String dayCount) {
        this.dayCount = dayCount;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public String getThreearyClassification() {
        return threearyClassification;
    }

    public void setThreearyClassification(String threearyClassification) {
        this.threearyClassification = threearyClassification;
    }

    public String getIsAutoUpdate() {
        return isAutoUpdate;
    }

    public void setIsAutoUpdate(String isAutoUpdate) {
        this.isAutoUpdate = isAutoUpdate;
    }

    public String getCapiIncomeFeature() {
        return capiIncomeFeature;
    }

    public void setCapiIncomeFeature(String capiIncomeFeature) {
        this.capiIncomeFeature = capiIncomeFeature;
    }

    public List<FinBondPayHSrcSecOptionDto> getBondPayHSrcSecOptionDtos() {
        return bondPayHSrcSecOptionDtos;
    }

    public void setBondPayHSrcSecOptionDtos(List<FinBondPayHSrcSecOptionDto> bondPayHSrcSecOptionDtos) {
        this.bondPayHSrcSecOptionDtos = bondPayHSrcSecOptionDtos;
    }

    public List<FinBondMarketCashPlanDto> getBondMarketCashPlanDtos() {
        return bondMarketCashPlanDtos;
    }

    public void setBondMarketCashPlanDtos(List<FinBondMarketCashPlanDto> bondMarketCashPlanDtos) {
        this.bondMarketCashPlanDtos = bondMarketCashPlanDtos;
    }

    public BigDecimal getIssueAmt() {
        return issueAmt;
    }

    public void setIssueAmt(BigDecimal issueAmt) {
        this.issueAmt = issueAmt;
    }

    public Date getDelistingDate() {
        return delistingDate;
    }

    public void setDelistingDate(Date delistingDate) {
        this.delistingDate = delistingDate;
    }

    public String getCouponSpecies() {
        return couponSpecies;
    }

    public void setCouponSpecies(String couponSpecies) {
        this.couponSpecies = couponSpecies;
    }

    public String getCouponSpeciesStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.息票品种, this.getCouponSpecies());
    }

    public String getRateTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.利率类型, this.getRateType());
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public String getBidBeginDate() {
        return bidBeginDate;
    }

    public void setBidBeginDate(String bidBeginDate) {
        this.bidBeginDate = bidBeginDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public String getBidEndDate() {
        return bidEndDate;
    }

    public List<String> getBidDates() {
        List<String> bidDates = Lists.newArrayList();
        bidDates.add(StringUtil.isBlank(getBidBeginDate()) ? "" : getBidBeginDate());
        bidDates.add(StringUtil.isBlank(getBidEndDate()) ? "" : getBidEndDate());
        return bidDates;
    }

    public void setBidEndDate(String bidEndDate) {
        this.bidEndDate = bidEndDate;
    }

    public List<String> getAdditionalTenderDates() {
        List<String> additionalTenderDates = Lists.newArrayList();
        additionalTenderDates.add(StringUtil.isBlank(getAdditionalTenderBeginDate()) ? "" : getAdditionalTenderBeginDate());
        additionalTenderDates.add(StringUtil.isBlank(getAdditionalTenderEndDate()) ? "" : getAdditionalTenderEndDate());
        return additionalTenderDates;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public String getAdditionalTenderBeginDate() {
        return additionalTenderBeginDate;
    }

    public void setAdditionalTenderBeginDate(String additionalTenderBeginDate) {
        this.additionalTenderBeginDate = additionalTenderBeginDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public String getAdditionalTenderEndDate() {
        return additionalTenderEndDate;
    }

    public void setAdditionalTenderEndDate(String additionalTenderEndDate) {
        this.additionalTenderEndDate = additionalTenderEndDate;
    }

    public String getOriginalOwner() {
        return originalOwner;
    }

    public void setOriginalOwner(String originalOwner) {
        this.originalOwner = originalOwner;
    }

    public String getIssus() {
        return issus;
    }

    public void setIssus(String issus) {
        this.issus = issus;
    }

    public String getIssecbond() {
        return issecbond;
    }

    public void setIssecbond(String issecbond) {
        this.issecbond = issecbond;
    }

    public String getGuartype() {
        return guartype;
    }

    public void setGuartype(String guartype) {
        this.guartype = guartype;
    }
}
