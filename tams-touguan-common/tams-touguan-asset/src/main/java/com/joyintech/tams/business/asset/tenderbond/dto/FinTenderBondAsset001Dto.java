package com.joyintech.tams.business.asset.tenderbond.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.dto.BaseDto;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 新债标的列表信息DTO
 *
 * @author gwl
 * @version 1.0 2022年4月11日
 * @see FinTenderBondAsset001Dto
 * @since 1.0
 */
@ApiModel(value = "FinTenderBondAsset001Dto", description = "新债标的列表信息")
public class FinTenderBondAsset001Dto extends BaseDto {

    @ApiModelProperty(name = "issueStage", value = "发行阶段")
    private String issueStage;

    @ApiModelProperty(name = "issueStageValue", value = "发行阶段字典值")
    private String issueStageValue;

    @ApiModelProperty(name = "issueStatus", value = "发行状态")
    private String issueStatus;

    @ApiModelProperty(name = "issueStatusValue", value = "发行状态字典值")
    private String issueStatusValue;

    @ApiModelProperty(name = "finprodId", value = "金融产品代码")
    private String finprodId;

    @ApiModelProperty(name = "finprodMarketId", value = "资产代码")
    private String finprodMarketId;

    @ApiModelProperty(name = "finprodAbbr", value = "资产简称")
    private String finprodAbbr;

    @ApiModelProperty(name = "tradeMarket", value = "交易场所")
    private String tradeMarket;

    @ApiModelProperty(name = "tradeMarketValue", value = "交易场所字典值")
    private String tradeMarketValue;

    @ApiModelProperty(name = "registOrg", value = "登记托管机构")
    private String registOrg;

    @ApiModelProperty(name = "registOrgValue", value = "登记托管机构字典值")
    private String registOrgValue;

    @ApiModelProperty(name = "issuer", value = "发行人")
    private String issuer;

    @ApiModelProperty(name = "issuerName", value = "发行人名称")
    private String issuerName;

    @ApiModelProperty(name = "issueType", value = "募集方式")
    private String issueType;

    @ApiModelProperty(name = "issueTypeValue", value = "募集方式字典值")
    private String issueTypeValue;

    @ApiModelProperty(name = "issueClass", value = "发行方式")
    private String issueClass;

    @ApiModelProperty(name = "issueClassValue", value = "发行方式字典值")
    private String issueClassValue;

    @ApiModelProperty(name = "tenderType", value = "招标方式")
    private String tenderType;

    @ApiModelProperty(name = "tenderTypeValue", value = "招标方式字典值")
    private String tenderTypeValue;

    @ApiModelProperty(name = "tenderTarget", value = "招标标的")
    private String tenderTarget;

    @ApiModelProperty(name = "tenderTargetValue", value = "招标标的字典值")
    private String tenderTargetValue;

    @ApiModelProperty(name = "issueStyle", value = "发行类型")
    private String issueStyle;

    @ApiModelProperty(name = "issueStyleValue", value = "issueStyle字典值")
    private String issueStyleValue;

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

    @ApiModelProperty(name = "crossMarketValue", value = "跨市场发行字典值")
    private String crossMarketValue;

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

    @ApiModelProperty(name = "couponSpecies", value = "息票品种")
    private String couponSpecies;

    @ApiModelProperty(name = "couponSpeciesValue", value = "息票品种字典值")
    private String couponSpeciesValue;

    @ApiModelProperty(name = "intType", value = "利率类型")
    private String intType;

    @ApiModelProperty(name = "intTypeValue", value = "利率类型字典值")
    private String intTypeValue;

    @ApiModelProperty(name = "payCycle", value = "付息频率,月付/季付/年付/半年付/到期一次")
    private String payCycle;

    @ApiModelProperty(name = "payCycleValue", value = "付息频率字典值")
    private String payCycleValue;

    @ApiModelProperty(name = "ccy", value = "币种")
    private String ccy;

    @ApiModelProperty(name = "ccyValue", value = "币种字典值")
    private String ccyValue;

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

    @ApiModelProperty(name = "subRate", value = "主体评级")
    private String subRate;

    @ApiModelProperty(name = "debRate", value = "债项评级")
    private String debRate;

    @ApiModelProperty(name = "subRateOrg", value = "主体评级机构")
    private String subRateOrg;

    @ApiModelProperty(name = "debRateOrg", value = "债项评级机构")
    private String debRateOrg;

    @ApiModelProperty(name = "remark", value = "备注")
    private String remark;

    /**
     * 操作类型：NEW,MODIFY,DELETE,REVOKE
     */
    @ApiModelProperty(name="operateType",value="操作类型：NEW,MODIFY,DELETE,REVOKE")
    private String operateType;

    public String getIssueStage() {
        return issueStage;
    }

    public void setIssueStage(String issueStage) {
        this.issueStage = issueStage;
    }

    public String getIssueStageValue() {
        if(this.issueStage != null){
            return SysDataUtil.getNameFromDataDict(DictCode.发行阶段, this.issueStage);
        }else{
            return issueStageValue;
        }
    }

    public void setIssueStageValue(String issueStageValue) {
        this.issueStageValue = issueStageValue;
    }

    public String getIssueStatus() {
        return issueStatus;
    }

    public void setIssueStatus(String issueStatus) {
        this.issueStatus = issueStatus;
    }

    public String getIssueStatusValue() {
        return SysDataUtil.getNameFromDataDict(DictCode.发行状态, this.issueStatus);
    }

    public void setIssueStatusValue(String issueStatusValue) {
        this.issueStatusValue = issueStatusValue;
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

    public String getTradeMarketValue() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, this.tradeMarket);
    }

    public void setTradeMarketValue(String tradeMarketValue) {
        this.tradeMarketValue = tradeMarketValue;
    }

    public String getRegistOrg() {
        return registOrg;
    }

    public void setRegistOrg(String registOrg) {
        this.registOrg = registOrg;
    }

    public String getRegistOrgValue() {
        return SysDataUtil.getNameFromDataDict(DictCode.登记托管机构, this.registOrg);
    }

    public void setRegistOrgValue(String registOrgValue) {
        this.registOrgValue = registOrgValue;
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

    public String getIssueTypeValue() {
        return SysDataUtil.getNameFromDataDict(DictCode.募集方式, this.issueType);
    }

    public void setIssueTypeValue(String issueTypeValue) {
        this.issueTypeValue = issueTypeValue;
    }

    public String getIssueClass() {
        return issueClass;
    }

    public void setIssueClass(String issueClass) {
        this.issueClass = issueClass;
    }

    public String getIssueClassValue() {
        return SysDataUtil.getNameFromDataDict(DictCode.新债_发行方式, this.issueClass);
    }

    public void setIssueClassValue(String issueClassValue) {
        this.issueClassValue = issueClassValue;
    }

    public String getTenderType() {
        return tenderType;
    }

    public void setTenderType(String tenderType) {
        this.tenderType = tenderType;
    }

    public String getTenderTypeValue() {
        return SysDataUtil.getNameFromDataDict(DictCode.招标方式, this.tenderType);
    }

    public void setTenderTypeValue(String tenderTypeValue) {
        this.tenderTypeValue = tenderTypeValue;
    }

    public String getTenderTarget() {
        return tenderTarget;
    }

    public void setTenderTarget(String tenderTarget) {
        this.tenderTarget = tenderTarget;
    }

    public String getTenderTargetValue() {
        return SysDataUtil.getNameFromDataDict(DictCode.招标标的, this.tenderTarget);
    }

    public void setTenderTargetValue(String tenderTargetValue) {
        this.tenderTargetValue = tenderTargetValue;
    }

    public String getIssueStyle() {
        return issueStyle;
    }

    public void setIssueStyle(String issueStyle) {
        this.issueStyle = issueStyle;
    }

    public String getIssueStyleValue() {
        return SysDataUtil.getNameFromDataDict(DictCode.发行类型, this.issueStyle);
    }

    public void setIssueStyleValue(String issueStyleValue) {
        this.issueStyleValue = issueStyleValue;
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

    public String getIntTypeValue() {
        return SysDataUtil.getNameFromDataDict(DictCode.利率类型, this.intType);
    }

    public void setIntTypeValue(String intTypeValue) {
        this.intTypeValue = intTypeValue;
    }

    public String getPayCycle() {
        return payCycle;
    }

    public void setPayCycle(String payCycle) {
        this.payCycle = payCycle;
    }

    public String getPayCycleValue() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.频率, this.payCycle);
    }

    public void setPayCycleValue(String payCycleValue) {
        this.payCycleValue = payCycleValue;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getCcyValue() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.币种, this.ccy);
    }

    public void setCcyValue(String ccyValue) {
        this.ccyValue = ccyValue;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getType9Value() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产二级分类, this.type9);
    }

    public void setType9Value(String type9Value) {
        this.type9Value = type9Value;
    }

    public String getType10Value() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产三级分类, this.type10);
    }

    public void setType10Value(String type10Value) {
        this.type10Value = type10Value;
    }

    public String getType11Value() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产四级分类, this.type11);
    }

    public void setType11Value(String type11Value) {
        this.type11Value = type11Value;
    }

    public String getCrossMarketValue() {
        return SysDataUtil.getNameFromDataDict(DictCode.是否, this.crossMarket);
    }

    public void setCrossMarketValue(String crossMarketValue) {
        this.crossMarketValue = crossMarketValue;
    }

    public String getCouponSpeciesValue() {
        return SysDataUtil.getNameFromDataDict(DictCode.息票品种, this.couponSpecies);
    }

    public void setCouponSpeciesValue(String couponSpeciesValue) {
        this.couponSpeciesValue = couponSpeciesValue;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getAppearMarketAbbr() {
        return appearMarketAbbr;
    }

    public void setAppearMarketAbbr(String appearMarketAbbr) {
        this.appearMarketAbbr = appearMarketAbbr;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }
}
