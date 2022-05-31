package com.joyintech.tams.business.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.business.common.entity.CfgDataTableShow;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 银行间债券信息
 *
 * @author zhuys
 * @version 2022年04月14日
 * @see CfetsEtrFinProductInfoResponse
 * @since 1.0
 */
public class CfetsEtrFinProductInfoResponse {

    /**
     * 页面展示表头
     */
    private List<CfgDataTableShow> tableShows;

    /**
     * 标题
     */
    @ApiModelProperty(name = "title", value = "标题")
    private String title;

    /**
     * 金融产品代码
     */
    @ApiModelProperty(name = "finprodId", value = "金融产品代码")
    private String finprodId;

    /**
     * 市场代码
     */
    @ApiModelProperty(name = "finprodMarketId", value = "市场代码")
    private String finprodMarketId;

    /**
     * 资产简称
     */
    @ApiModelProperty(name = "finprodAbbr", value = "资产简称")
    private String finprodAbbr;

    /**
     * 资产名称
     */
    @ApiModelProperty(name = "finprodName", value = "资产名称")
    private String finprodName;

    /**
     * 交易场所
     */
    @ApiModelProperty(name = "tradeMarket", value = "交易场所")
    private String tradeMarket;

    /**
     * 登记托管机构
     */
    @ApiModelProperty(name = "registOrg", value = "登记托管机构")
    private String registOrg;


    /**
     * 资产一级分类
     */
    @ApiModelProperty(name = "assetTypeOne", value = "资产一级分类")
    private String assetTypeOne;


    /**
     * 资产二级分类
     */
    @ApiModelProperty(name = "assetTypeTwo", value = "资产二级分类")
    private String assetTypeTwo;


    /**
     * 资产三级分类
     */
    @ApiModelProperty(name = "assetTypeThree", value = "资产三级分类")
    private String assetTypeThree;

    /**
     * 债券分类
     */
    @ApiModelProperty(name = "bondType", value = "债券分类")
    private String bondType;

    /**
     * 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    private String finprodType2;

    /**
     * 收益率，利息贴现为参考收益率，附息为票面利率
     */
    @ApiModelProperty(name = "referYield", value = "收益率，利息贴现为参考收益率，附息为票面利率")
    private String referYield;

    /**
     * 基准利率
     */
    @ApiModelProperty(name = "basicRate", value = "基准利率")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal basicRate;

    /**
     * 基准利率精度
     */
    @ApiModelProperty(name = "rateGrade", value = "基准利率精度")
    private String rateGrade;

    /**
     * 利差
     */
    @ApiModelProperty(name = "bp", value = "利差")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal bp;

    /**
     * 发行开始时间
     */
    @ApiModelProperty(name = "issueSdate", value = "发行开始时间")
    private Date issueSdate;

    /**
     * 发行结束时间
     */
    @ApiModelProperty(name = "issueEdate", value = "发行结束时间")
    private Date issueEdate;

    /**
     * 债券债务登记日
     */
    @ApiModelProperty(name = "assignRegdate", value = "债券债务登记日")
    private Date assignRegdate;

    /**
     * 上市日
     */
    @ApiModelProperty(name = "listingDate", value = "上市日")
    private Date listingDate;

    /**
     * 增发批次,首发时为0
     */
    @ApiModelProperty(name = "addBatch", value = "增发批次")
    private Long addBatch;

    /**
     * 每标位最低投标量
     */
    @ApiModelProperty(name = "minPerTamt", value = "每标位最低投标量")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal minPerTamt;

    /**
     * 每标位最高投标量
     */
    @ApiModelProperty(name = "maxPerUtamt", value = "每标位最高投标量")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal maxPerUtamt;

    /**
     * 单笔最低认购量
     */
    @ApiModelProperty(name = "minPamt", value = "单笔最低认购量")
    private String minPamt;

    /**
     * 单笔最高认购量
     */
    @ApiModelProperty(name = "maxPamt", value = "单笔最高认购量")
    private String maxPamt;

    /**
     * 最高认购总量
     */
    @ApiModelProperty(name = "maxTotalamt", value = "最高认购总量")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal maxTotalamt;

    /**
     * 最高标位
     */
    @ApiModelProperty(name = "maxTamt", value = "最高标位")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal maxTamt;

    /**
     * 最低标位
     */
    @ApiModelProperty(name = "minTamt", value = "最低标位")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal minTamt;

    /**
     * 投标步长
     */
    @ApiModelProperty(name = "tenderUnit", value = "投标步长")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tenderUnit;

    /**
     * 最大投标标位数
     */
    @ApiModelProperty(name = "maxTenderUnit", value = "最大投标标位数")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal maxTenderUnit;

    /**
     * 最大投标标位差
     */
    @ApiModelProperty(name = "maxTenderPlus", value = "最大投标标位差")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal maxTenderPlus;

    /**
     * 连续投标限制
     */
    @ApiModelProperty(name = "seriYN", value = "连续投标限制")
    private String seriYN;

    /**
     * 最高投标总量
     */
    @ApiModelProperty(name = "maxTotalTender", value = "最高投标总量")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal maxTotalTender;

    /**
     * 增发投标日
     */
    @ApiModelProperty(name = "settleDate", value = "增发投标日")
    private Date settleDate;

    /**
     * 投标起始日
     */
    @ApiModelProperty(name = "tenderSdate", value = "投标起始日")
    private Date tenderSdate;

    /**
     * 投标截止日
     */
    @ApiModelProperty(name = "tenderEdate", value = "投标截止日")
    private Date tenderEdate;

    /**
     * 缴款日
     */
    @ApiModelProperty(name = "payDate", value = "缴款日")
    private Date payDate;

    /**
     * 缴款截止日
     */
    @ApiModelProperty(name = "payEdate", value = "缴款截止日")
    private Date payEdate;

    /**
     * 首次利率确定日
     */
    @ApiModelProperty(name = "firstConfirmDate", value = "首次利率确定日")
    private Date firstConfirmDate;

    /**
     * 发行价格
     */
    @ApiModelProperty(name = "issuePrice", value = "发行价格")
    private String issuePrice;

    /**
     * 发行类型
     */
    @ApiModelProperty(name = "issueStyle", value = "发行类型")
    private String issueStyle;

    /**
     * 发行日
     */
    @ApiModelProperty(name = "issueDate", value = "发行日")
    private Date issueDate;

    /**
     * 招标方式
     */
    @ApiModelProperty(name = "tenderType", value = "招标方式")
    private String tenderType;

    /**
     * 上市日
     */
    @ApiModelProperty(name = "onmarketDate", value = "上市日")
    private Date onmarketDate;

    /**
     * 上市代码
     */
    @ApiModelProperty(name = "appearMarketId", value = "上市代码")
    private String appearMarketId;

    /**
     * 招标标的
     */
    @ApiModelProperty(name = "tenderTarget", value = "招标标的")
    private String tenderTarget;

    /**
     * 计划发行量
     */
    @ApiModelProperty(name = "planIssueAmt", value = "计划发行量")
    private String planIssueAmt;

    /**
     * 币种
     */
    @ApiModelProperty(name = "ccy", value = "币种code")
    private String ccy;

    /**
     * 保本标识
     */
    @ApiModelProperty(name = "capiIncomeFeature", value = "保本标识")
    private String capiIncomeFeature;

    /**
     * 是否接口自动更新
     */
    @ApiModelProperty(name = "isAutoUpdate", value = "是否接口自动更新")
    private String isAutoUpdate;

    /**
     * 债券分类
     */
    @ApiModelProperty(name = "secType", value = "债券分类")
    private String secType;

    /**
     * 发行人
     */
    @ApiModelProperty(name = "issuer", value = "发行人")
    private String issuer;

    /**
     * 发行人
     */
    @ApiModelProperty(name = "issuerName", value = "发行人")
    private String issuerName;

    /**
     * 起息日
     */
    @ApiModelProperty(name = "vdate", value = "起息日")
    private Date vdate;

    /**
     * 到期日
     */
    @ApiModelProperty(name = "mdate", value = "到期日")
    private Date mdate;

    /**
     * 利率类型
     */
    @ApiModelProperty(name = "intType", value = "利率类型")
    private String intType;

    /**
     * 付息频率
     */
    @ApiModelProperty(name = "payCycle", value = "付息频率")
    private String payCycle;

    /**
     * 债项评级
     */
    @ApiModelProperty(name = "zxGrade", value = "债项评级")
    private String zxGrade;

    /**
     * 主体评级
     */
    @ApiModelProperty(name = "ztGrade", value = "主体评级")
    private String ztGrade;

    /**
     * 主体评级机构
     */
    @ApiModelProperty(name = "ztGradeOrgId", value = "主体评级机构")
    private String ztGradeOrgId;
    /**
     * 债项评级机构
     */
    @ApiModelProperty(name = "zxGradeOrgId", value = "债项评级机构")
    private String zxGradeOrgId;

    /**
     * 发行方式
     */
    @ApiModelProperty(name = "issueType", value = "发行方式/募集方式")
    private String issueType;


    /**
     * 发行方式
     */
    @ApiModelProperty(name = "issueClass", value = "发行方式")
    private String issueClass;

    /**
     * 息票品种
     */
    @ApiModelProperty(name = "couponSpecies", value = "息票品种")
    private String couponSpecies;

    public String getTitle() {
        return getFinprodMarketId() + getFinprodAbbr();
    }

    public void setTitle(String title) {
        this.title = title;
    }

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

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getIssueSdate() {
        return issueSdate;
    }

    public void setIssueSdate(Date issueSdate) {
        this.issueSdate = issueSdate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date getIssueEdate() {
        return issueEdate;
    }

    public void setIssueEdate(Date issueEdate) {
        this.issueEdate = issueEdate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getAssignRegdate() {
        return assignRegdate;
    }

    public void setAssignRegdate(Date assignRegdate) {
        this.assignRegdate = assignRegdate;
    }

    public Long getAddBatch() {
        return addBatch;
    }

    public void setAddBatch(Long addBatch) {
        this.addBatch = addBatch;
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

    public String getMinPamt() {
        return minPamt;
    }

    public String getMinPamtStr() {
        return StringUtil.isNotBlank(getMinPamt()) ? getMinPamt() + "亿元" : "";
    }


    public void setMinPamt(String minPamt) {
        this.minPamt = minPamt;
    }

    public String getMaxPamt() {
        return maxPamt;
    }

    public String getMaxPamtStr() {
        return StringUtil.isNotBlank(getMaxPamt()) ? getMaxPamt() + "亿元" : "";
    }

    public void setMaxPamt(String maxPamt) {
        this.maxPamt = maxPamt;
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getPayEdate() {
        return payEdate;
    }

    public void setPayEdate(Date payEdate) {
        this.payEdate = payEdate;
    }

    public String getIssuePrice() {
        return issuePrice;
    }

    public String getIssuePriceStr() {
        return StringUtil.isNotBlank(getIssuePrice()) ? getIssuePrice() + "元" : "";
    }

    public void setIssuePrice(String issuePrice) {
        this.issuePrice = issuePrice;
    }

    public String getIssueStyle() {
        return issueStyle;
    }

    public void setIssueStyle(String issueStyle) {
        this.issueStyle = issueStyle;
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getTenderType() {
        return tenderType;
    }

    public void setTenderType(String tenderType) {
        this.tenderType = tenderType;
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getOnmarketDate() {
        return onmarketDate;
    }

    public void setOnmarketDate(Date onmarketDate) {
        this.onmarketDate = onmarketDate;
    }

    public String getAppearMarketId() {
        return appearMarketId;
    }

    public void setAppearMarketId(String appearMarketId) {
        this.appearMarketId = appearMarketId;
    }

    public String getTenderTarget() {
        return tenderTarget;
    }

    public void setTenderTarget(String tenderTarget) {
        this.tenderTarget = tenderTarget;
    }

    public String getPlanIssueAmt() {
        return planIssueAmt;
    }

    public void setPlanIssueAmt(String planIssueAmt) {
        this.planIssueAmt = planIssueAmt;
    }

    public String getSecType() {
        return secType;
    }

    public void setSecType(String secType) {
        this.secType = secType;
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

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getVdate() {
        return vdate;
    }

    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getMdate() {
        return mdate;
    }

    public void setMdate(Date mdate) {
        this.mdate = mdate;
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

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
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

    public String getReferYield() {
        return referYield;
    }

    public void setReferYield(String referYield) {
        this.referYield = referYield;
    }


    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getBondType() {
        return bondType;
    }

    public void setBondType(String bondType) {
        this.bondType = bondType;
    }


    public List<CfgDataTableShow> getTableShows() {
        return tableShows;
    }

    public void setTableShows(List<CfgDataTableShow> tableShows) {
        this.tableShows = tableShows;
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

    public String getAssetTypeOne() {
        return assetTypeOne;
    }

    public void setAssetTypeOne(String assetTypeOne) {
        this.assetTypeOne = assetTypeOne;
    }

    public String getAssetTypeTwo() {
        return assetTypeTwo;
    }

    public void setAssetTypeTwo(String assetTypeTwo) {
        this.assetTypeTwo = assetTypeTwo;
    }

    public String getAssetTypeThree() {
        return assetTypeThree;
    }

    public void setAssetTypeThree(String assetTypeThree) {
        this.assetTypeThree = assetTypeThree;
    }

    public BigDecimal getBasicRate() {
        return basicRate;
    }

    public void setBasicRate(BigDecimal basicRate) {
        this.basicRate = basicRate;
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

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getListingDate() {
        return listingDate;
    }

    public void setListingDate(Date listingDate) {
        this.listingDate = listingDate;
    }

    public BigDecimal getMaxTotalamt() {
        return maxTotalamt;
    }

    public void setMaxTotalamt(BigDecimal maxTotalamt) {
        this.maxTotalamt = maxTotalamt;
    }

    public BigDecimal getMaxTamt() {
        return maxTamt;
    }

    public void setMaxTamt(BigDecimal maxTamt) {
        this.maxTamt = maxTamt;
    }

    public BigDecimal getMinTamt() {
        return minTamt;
    }

    public void setMinTamt(BigDecimal minTamt) {
        this.minTamt = minTamt;
    }

    public BigDecimal getTenderUnit() {
        return tenderUnit;
    }

    public void setTenderUnit(BigDecimal tenderUnit) {
        this.tenderUnit = tenderUnit;
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

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getTenderSdate() {
        return tenderSdate;
    }

    public void setTenderSdate(Date tenderSdate) {
        this.tenderSdate = tenderSdate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getTenderEdate() {
        return tenderEdate;
    }

    public void setTenderEdate(Date tenderEdate) {
        this.tenderEdate = tenderEdate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Date getFirstConfirmDate() {
        return firstConfirmDate;
    }

    public void setFirstConfirmDate(Date firstConfirmDate) {
        this.firstConfirmDate = firstConfirmDate;
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

    public String getIntType() {
        return intType;
    }

    public void setIntType(String intType) {
        this.intType = intType;
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

    public String getCouponSpeciesStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.息票品种, this.getCouponSpecies());
    }

    public String getIntTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.利率类型, this.getIntType());
    }

    public String getPayCycleStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.频率, this.getPayCycle());
    }

    public String getIssueTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.发行方式, this.getIssueType());
    }

    public String getIssueClassStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.新债_发行方式, this.getIssueClass());
    }

    public String getTenderTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.招标方式, this.getTenderType());
    }

    public String getTenderTargetStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.招标标的, this.getTenderTarget());
    }


    public String getIssueStyleStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.发行类型, this.getIssueStyle());
    }

    public String getBondTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.行内资产四级分类, this.getBondType());
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
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

    public String getIssueClass() {
        return issueClass;
    }

    public void setIssueClass(String issueClass) {
        this.issueClass = issueClass;
    }

    public String getZtGradeOrgId() {
        return ztGradeOrgId;
    }

    public void setZtGradeOrgId(String ztGradeOrgId) {
        this.ztGradeOrgId = ztGradeOrgId;
    }

    public String getZxGradeOrgId() {
        return zxGradeOrgId;
    }

    public void setZxGradeOrgId(String zxGradeOrgId) {
        this.zxGradeOrgId = zxGradeOrgId;
    }
}
