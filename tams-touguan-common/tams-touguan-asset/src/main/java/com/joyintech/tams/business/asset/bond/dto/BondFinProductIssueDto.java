package com.joyintech.tams.business.asset.bond.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 标的类金融产品发行信息
 */
@ApiModel(value="发行信息",description="发行信息")
public class BondFinProductIssueDto extends BaseEntity {
    
    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 6888665039083565141L;

    /**
     * 金融产品代码
     */
    @ApiModelProperty(name="finprodId",value="新债代码")
    private String finprodId;

    @ApiModelProperty(name="finprodMarketId",value="新债代码ID",required=true)
    private String finprodMarketId;

    /**
     * 新债简称
     */
    @ApiModelProperty(name="finprodAbbr",value="新债简称")
    private String finprodAbbr;

    /**
     * 发行方式，报价发行/招标发行/簿记建档
     */
    @ApiModelProperty(name="issueClass",value="发行方式")
    private String issueClass;

    /**
     * 招标方式，单一价格招标/数量招标/荷兰式招标/混合式招标
     */
    @ApiModelProperty(name="tenderType",value="招标方式")
    private String tenderType;

    /**
     * 招标标的，利率(%)/价格(元)/利差(BP)
     */
    @ApiModelProperty(name="tenderTarget",value="招标标的")
    private String tenderTarget;

    /**
     * 计划发行量
     */
    @ApiModelProperty(name="planIssueAmt",value="计划发行量")
    private BigDecimal planIssueAmt;

    /**
     * 发行类型,首发/增发
     */
    @ApiModelProperty(name="issueStyle",value="发行类型")
    private String issueStyle;

    /**
     * 增发批次,首发时为0
     */
    @ApiModelProperty(name="addBatch",value="增发批次")
    private Long addBatch;

    /**
     * 发行价格
     */
    @ApiModelProperty(name="addBatch",value="发行价格")
    private BigDecimal issuePrice;

    /**
     * 收益率，利息贴现为参考收益率，附息为票面利率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal referYield;

    /**
     * 行情代码
     */
    private String intRateId;

    /**
     * 基准利率精度
     */
    private String rateGrade;

    /**
     * 利差
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal bp;

    /**
     * 发行日
     */
    @ApiModelProperty(name="issueDate",value="发行日")
    private Date issueDate;

    /**
     * 缴款日
     */
    @ApiModelProperty(name="payDate",value="缴款日")
    private Date payDate;

    /**
     * 首次利率确定日
     */
    private Date firstConfirmDate;

    /**
     * 增发投标日
     */
    @ApiModelProperty(name="issueSdate",value="增发投标日")
    private Date issueSdate;

    /**
     * 上市日
     */
    @ApiModelProperty(name="listingDate",value="上市日")
    private Date listingDate;

    /**
     * 投标起始日
     */
    @ApiModelProperty(name="tenderSdate",value="投标日开始日")
    private Date tenderSdate;

    /**
     * 投标截止日
     */
    @ApiModelProperty(name="tenderEdate",value="投标日截止日")
    private Date tenderEdate;

    /**
     * 缴款截止日
     */
    @ApiModelProperty(name="payEdate",value="投标日")
    private Date payEdate;

    /**
     * 债权债务登记日
     */
    @ApiModelProperty(name="assignRegdate",value="债权债务登记日")
    private Date assignRegdate;

    /**
     * 单笔最低认购量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal minPamt;

    /**
     * 单笔最高认购量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal maxPamt;

    /**
     * 最高认购总量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal maxTotalamt;

    /**
     * 每标位最低投标量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal minPerTamt;

    /**
     * 每标位最高投标量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal maxPerUtamt;

    /**
     * 最低标位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal minTamt;

    /**
     * 最高标位
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal maxTamt;

    /**
     * 投标步长
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tenderUnit;

    /**
     * 最大投标标位数
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal maxTenderUnit;

    /**
     * 最大投标标位差
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal maxTenderPlus;

    /**
     * 连续投标限制
     */
    private String seriYN;

    /**
     * 最高投标总量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal maxTotalTender;

    /**
     * 付息频率,月付/季付/年付/半年付/到期一次
     */
    private String payCycle;

    /**
     * 利率类型
     */
    private String intType;


    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
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

    public BigDecimal getPlanIssueAmt() {
        return planIssueAmt;
    }

    public void setPlanIssueAmt(BigDecimal planIssueAmt) {
        this.planIssueAmt = planIssueAmt;
    }

    public String getIssueStyle() {
        return issueStyle;
    }

    public void setIssueStyle(String issueStyle) {
        this.issueStyle = issueStyle;
    }

    public Long getAddBatch() {
        return addBatch;
    }

    public void setAddBatch(Long addBatch) {
        this.addBatch = addBatch;
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

    public String getIntRateId() {
        return intRateId;
    }

    public void setIntRateId(String intRateId) {
        this.intRateId = intRateId;
    }

    public String getRateGrade() {
        return rateGrade;
    }

    public void setRateGrade(String rateGrade) {
        this.rateGrade = rateGrade;
    }

    public BigDecimal getBp() {
        return bp;
    }

    public void setBp(BigDecimal bp) {
        this.bp = bp;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public Date getFirstConfirmDate() {
        return firstConfirmDate;
    }

    public void setFirstConfirmDate(Date firstConfirmDate) {
        this.firstConfirmDate = firstConfirmDate;
    }

    public Date getIssueSdate() {
        return issueSdate;
    }

    public void setIssueSdate(Date issueSdate) {
        this.issueSdate = issueSdate;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public Date getListingDate() {
        return listingDate;
    }

    public void setListingDate(Date listingDate) {
        this.listingDate = listingDate;
    }

    public Date getTenderSdate() {
        return tenderSdate;
    }

    public void setTenderSdate(Date tenderSdate) {
        this.tenderSdate = tenderSdate;
    }

    public Date getTenderEdate() {
        return tenderEdate;
    }

    public void setTenderEdate(Date tenderEdate) {
        this.tenderEdate = tenderEdate;
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

    public BigDecimal getMaxTotalamt() {
        return maxTotalamt;
    }

    public void setMaxTotalamt(BigDecimal maxTotalamt) {
        this.maxTotalamt = maxTotalamt;
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

    public BigDecimal getMinTamt() {
        return minTamt;
    }

    public void setMinTamt(BigDecimal minTamt) {
        this.minTamt = minTamt;
    }

    public BigDecimal getMaxTamt() {
        return maxTamt;
    }

    public void setMaxTamt(BigDecimal maxTamt) {
        this.maxTamt = maxTamt;
    }

    public BigDecimal getTenderUnit() {
        return tenderUnit;
    }

    public void setTenderUnit(BigDecimal tenderUnit) {
        this.tenderUnit = tenderUnit;
    }

    public BigDecimal getMaxTenderUnit() {
        return maxTenderUnit;
    }

    public void setMaxTenderUnit(BigDecimal maxTenderUnit) {
        this.maxTenderUnit = maxTenderUnit;
    }

    public BigDecimal getMaxTenderPlus() {
        return maxTenderPlus;
    }

    public void setMaxTenderPlus(BigDecimal maxTenderPlus) {
        this.maxTenderPlus = maxTenderPlus;
    }

    public String getSeriYN() {
        return seriYN;
    }

    public void setSeriYN(String seriYN) {
        this.seriYN = seriYN;
    }

    public BigDecimal getMaxTotalTender() {
        return maxTotalTender;
    }

    public void setMaxTotalTender(BigDecimal maxTotalTender) {
        this.maxTotalTender = maxTotalTender;
    }

    public String getPayCycle() {
        return payCycle;
    }

    public void setPayCycle(String payCycle) {
        this.payCycle = payCycle;
    }

    public String getIntType() {
        return intType;
    }

    public void setIntType(String intType) {
        this.intType = intType;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }
}