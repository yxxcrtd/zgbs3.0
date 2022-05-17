package com.joyintech.tams.business.asset.bond.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.joyintech.background.component.common.pack.common.validation.annotation.NotEmpty;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 债券标的信息DTO
 *
 * @author wangyang1
 * @version 1.0 2022年4月13日
 * @see FinBondAssetResponse
 * @since 1.0
 */
@ApiModel(value="债券标的信息查询返回参数",description="债券标的信息查询返回参数")
public class FinBondAssetResponse {

    @ApiModelProperty(name="finprodId",value="资产代码")
    private String finprodId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称")
    private String finprodAbbr;

    @ApiModelProperty(name="finprodMarketId",value="市场代码")
    private String finprodMarketId;

    @ApiModelProperty(name="tradeMarket",value="交易场所code")
    private String tradeMarket;

    @ApiModelProperty(name="tradeMarketStr",value="交易场所")
    private String tradeMarketStr;

    @ApiModelProperty(name="registOrg",value="登记托管机构code")
    private String registOrg;

    @ApiModelProperty(name="registOrgStr",value="登记托管机构")
    private String registOrgStr;

    @ApiModelProperty(name="idate",value="发行日")
    private Date idate;

    @ApiModelProperty(name="vdate",value="起息日")
    private Date vdate;

    @ApiModelProperty(name="mdate",value="到期日")
    private Date mdate;

    @ApiModelProperty(name="delistingDate",value="摘牌日")
    private Date delistingDate;

    @ApiModelProperty(name="issuer",value="发行人code")
    private String issuer;

    @ApiModelProperty(name="issuerName",value="发行人")
    private String issuerName;

    @ApiModelProperty(name="issuePrice",value="发行价格(元)")
    private BigDecimal issuePrice;

    @ApiModelProperty(name="issueAmt",value="发行金额(亿元)")
    private BigDecimal issueAmt;

    @ApiModelProperty(name="type9",value="资产一级分类code")
    private String type9;

    @ApiModelProperty(name="type9Str",value="资产一级分类")
    private String type9Str;

    @ApiModelProperty(name="type10",value="资产二级分类code")
    private String type10;

    @ApiModelProperty(name="type10Str",value="资产二级分类")
    private String type10Str;

    @ApiModelProperty(name="type11",value="资产三级分类code")
    private String type11;

    @ApiModelProperty(name="type11Str",value="资产三级分类")
    private String type11Str;

    @ApiModelProperty(name="couponSpecies",value="息票品种code")
    private String couponSpecies;

    @ApiModelProperty(name="couponSpeciesStr",value="息票品种")
    private String couponSpeciesStr;

    @ApiModelProperty(name="rateType",value="利率类型")
    private String rateType;

    @ApiModelProperty(name="rateTypeStr",value="利率类型")
    private String rateTypeStr;

    @ApiModelProperty(name="payCycle",value="付息频率code")
    private String payCycle;

    @ApiModelProperty(name="payCycleStr",value="付息频率")
    private String payCycleStr;

    @ApiModelProperty(name="rate",value="票面利率（%）")
    private BigDecimal rate;

    @ApiModelProperty(name="basisRate",value="基准利率")
    private String basisRate;

    @ApiModelProperty(name="basisRateRatio",value="基准利率系数")
    private BigDecimal basisRateRatio;

    @ApiModelProperty(name="spreadRate",value="利差")
    private BigDecimal spreadRate;

    @ApiModelProperty(name="firstRateFixDate",value="首次利率确定日")
    private Date firstRateFixDate;

    @ApiModelProperty(name="basis",value="发行人计息基础")
    private String basis;

    @ApiModelProperty(name="marketBasis",value="交易市场计息基础")
    private String marketBasis;

    @ApiModelProperty(name="isRight",value="是否含权债")
    private String isRight;

    @ApiModelProperty(name="isRightStr",value="是否含权债")
    private String isRightStr;

    @ApiModelProperty(name="exerciseType",value="行权方式")
    private String exerciseType;

    @ApiModelProperty(name="exerciseTypeStr",value="行权方式")
    private String exerciseTypeStr;

    @ApiModelProperty(name="exerciseDate",value="理论行权日")
    private Date exerciseDate;

    @ApiModelProperty(name="isSus",value="是否永续")
    private String isSus;

    @ApiModelProperty(name="isSusStr",value="是否永续")
    private String isSusStr;

    @ApiModelProperty(name="issueType",value="募集方式code")
    private String issueType;

    @ApiModelProperty(name="issueTypeStr",value="募集方式")
    private String issueTypeStr;

    @ApiModelProperty(name="ccy",value="币种code")
    private String ccy;

    @ApiModelProperty(name="ccyStr",value="币种")
    private String ccyStr;

    @ApiModelProperty(name="crossMarketCode",value="跨市场代码")
    private String crossMarketCode;

    @ApiModelProperty(name="conceptType",value="概念标识Code")
    private String conceptType;

    @ApiModelProperty(name="conceptTypeStr",value="概念标识")
    private String conceptTypeStr;

    @ApiModelProperty(name="mGradeResult",value="主体评级code")
    private String mGradeResult;

    @ApiModelProperty(name="mGradeResultStr",value="主体评级")
    private String mGradeResultStr;

    @ApiModelProperty(name="mGradeOrgId",value="主体评级机构code")
    private String mGradeOrgId;

    @ApiModelProperty(name="mGradeResultStr",value="主体评级机构")
    private String mGradeOrgIdStr;

    @ApiModelProperty(name="bGradeResult",value="债项评级code")
    private String bGradeResult;

    @ApiModelProperty(name="bGradeResultStr",value="主体评级")
    private String bGradeResultStr;

    @ApiModelProperty(name="bGradeOrgId",value="债项评级机构code")
    private String bGradeOrgId;

    @ApiModelProperty(name="bGradeOrgIdStr",value="债项评级机构")
    private String bGradeOrgIdStr;

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }


    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getTradeMarketStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, this.getTradeMarket());
    }

    public void setTradeMarketStr(String tradeMarketStr) {
        this.tradeMarketStr = tradeMarketStr;
    }

    public String getRegistOrg() {
        return registOrg;
    }

    public void setRegistOrg(String registOrg) {
        this.registOrg = registOrg;
    }

    public String getRegistOrgStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.登记托管机构, this.getRegistOrg());
    }

    public void setRegistOrgStr(String registOrgStr) {
        this.registOrgStr = registOrgStr;
    }
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getIdate() {
        return idate;
    }

    public void setIdate(Date idate) {
        this.idate = idate;
    }

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getVdate() {
        return vdate;
    }

    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getDelistingDate() {
        return delistingDate;
    }

    public void setDelistingDate(Date delistingDate) {
        this.delistingDate = delistingDate;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    public void setIssuePrice(BigDecimal issuePrice) {
        this.issuePrice = issuePrice;
    }

    public BigDecimal getIssueAmt() {
        return issueAmt;
    }

    public void setIssueAmt(BigDecimal issueAmt) {
        this.issueAmt = issueAmt;
    }

    public String getType9() {
        return type9;
    }

    public void setType9(String type9) {
        this.type9 = type9;
    }

    public String getType9Str() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产二级分类, this.getType9());
    }

    public void setType9Str(String type9Str) {
        this.type9Str = type9Str;
    }

    public String getType10() {
        return type10;
    }

    public void setType10(String type10) {
        this.type10 = type10;
    }

    public String getType10Str() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产三级分类, this.getType10());
    }

    public void setType10Str(String type10Str) {
        this.type10Str = type10Str;
    }

    public String getType11() {
        return type11;
    }

    public void setType11(String type11) {
        this.type11 = type11;
    }

    public String getType11Str() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产四级分类, this.getType11());
    }

    public void setType11Str(String type11Str) {
        this.type11Str = type11Str;
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

    public void setCouponSpeciesStr(String couponSpeciesStr) {
        this.couponSpeciesStr = couponSpeciesStr;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public String getRateTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.利率类型, this.getRateType());
    }

    public void setRateTypeStr(String rateTypeStr) {
        this.rateTypeStr = rateTypeStr;
    }

    public String getPayCycle() {
        return payCycle;
    }

    public void setPayCycle(String payCycle) {
        this.payCycle = payCycle;
    }

    public String getPayCycleStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.频率, this.getPayCycle());
    }

    public void setPayCycleStr(String payCycleStr) {
        this.payCycleStr = payCycleStr;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public String getBasisRate() {
        return basisRate;
    }

    public String getBasisRateStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.利率行情, this.getBasisRate());
    }

    public void setBasisRate(String basisRate) {
        this.basisRate = basisRate;
    }

    public BigDecimal getBasisRateRatio() {
        return basisRateRatio;
    }

    public void setBasisRateRatio(BigDecimal basisRateRatio) {
        this.basisRateRatio = basisRateRatio;
    }

    public BigDecimal getSpreadRate() {
        return spreadRate;
    }

    public void setSpreadRate(BigDecimal spreadRate) {
        this.spreadRate = spreadRate;
    }

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getFirstRateFixDate() {
        return firstRateFixDate;
    }

    public void setFirstRateFixDate(Date firstRateFixDate) {
        this.firstRateFixDate = firstRateFixDate;
    }

    public String getBasis() {
        return basis;
    }

    public void setBasis(String basis) {
        this.basis = basis;
    }

    public String getMarketBasis() {
        return marketBasis;
    }

    public void setMarketBasis(String marketBasis) {
        this.marketBasis = marketBasis;
    }

    public String getIsRight() {
        return isRight;
    }

    public void setIsRight(String isRight) {
        this.isRight = isRight;
    }

    public String getIsRightStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsRight());
    }

    public void setIsRightStr(String isRightStr) {
        this.isRightStr = isRightStr;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public String getExerciseTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.行权方式, this.getExerciseType());
    }

    public void setExerciseTypeStr(String exerciseTypeStr) {
        this.exerciseTypeStr = exerciseTypeStr;
    }

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getExerciseDate() {
        return exerciseDate;
    }

    public void setExerciseDate(Date exerciseDate) {
        this.exerciseDate = exerciseDate;
    }



    public String getIsSus() {
        return isSus;
    }

    public void setIsSus(String isSus) {
        this.isSus = isSus;
    }

    public String getIsSusStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.getIsSus());
    }

    public void setIsSusStr(String isSusStr) {
        this.isSusStr = isSusStr;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getCcyStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, this.getCcy());
    }

    public void setCcyStr(String ccyStr) {
        this.ccyStr = ccyStr;
    }


    public String getmGradeResult() {
        return mGradeResult;
    }

    public void setmGradeResult(String mGradeResult) {
        this.mGradeResult = mGradeResult;
    }

    public String getmGradeResultStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.评级结果, this.getmGradeResult());
    }

    public void setmGradeResultStr(String mGradeResultStr) {
        this.mGradeResultStr = mGradeResultStr;
    }

    public String getmGradeOrgId() {
        return mGradeOrgId;
    }

    public void setmGradeOrgId(String mGradeOrgId) {
        this.mGradeOrgId = mGradeOrgId;
    }

    public String getmGradeOrgIdStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.评级机构, this.getmGradeOrgId());
    }

    public void setmGradeOrgIdStr(String mGradeOrgIdStr) {
        this.mGradeOrgIdStr = mGradeOrgIdStr;
    }

    public String getbGradeResult() {
        return bGradeResult;
    }

    public void setbGradeResult(String bGradeResult) {
        this.bGradeResult = bGradeResult;
    }

    public String getbGradeResultStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.评级结果, this.getbGradeResult());
    }

    public void setbGradeResultStr(String bGradeResultStr) {
        this.bGradeResultStr = bGradeResultStr;
    }

    public String getbGradeOrgId() {
        return bGradeOrgId;
    }

    public void setbGradeOrgId(String bGradeOrgId) {
        this.bGradeOrgId = bGradeOrgId;
    }

    public String getbGradeOrgIdStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.评级机构, this.getbGradeOrgId());
    }

    public void setbGradeOrgIdStr(String bGradeOrgIdStr) {
        this.bGradeOrgIdStr = bGradeOrgIdStr;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getIssueTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.募集方式, this.getIssueType());
    }

    public void setIssueTypeStr(String issueTypeStr) {
        this.issueTypeStr = issueTypeStr;
    }

    public String getCrossMarketCode() {
        return crossMarketCode;
    }

    public void setCrossMarketCode(String crossMarketCode) {
        this.crossMarketCode = crossMarketCode;
    }

    public String getConceptType() {
        return conceptType;
    }

    public void setConceptType(String conceptType) {
        this.conceptType = conceptType;
    }

    public String getConceptTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.概念标识, this.getConceptType());
    }

    public void setConceptTypeStr(String conceptTypeStr) {
        this.conceptTypeStr = conceptTypeStr;
    }
}
