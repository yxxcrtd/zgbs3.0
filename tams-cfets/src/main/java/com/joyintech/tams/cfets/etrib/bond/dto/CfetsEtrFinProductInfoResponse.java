package com.joyintech.tams.cfets.etrib.bond.dto;

import com.joyintech.tams.business.common.entity.CfgDataTableShow;
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
     * 债券分类
     */
    @ApiModelProperty(name = "bondType", value = "债券分类")
    private String bondType;

    /**
     * 收益率
     */
    @ApiModelProperty(name = "referYield", value = "收益率")
    private BigDecimal referYield;

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
     * 增发批次,首发时为0
     */
    @ApiModelProperty(name = "addBatch", value = "增发批次")
    private Long addBatch;

    /**
     * 每标位最低投标量
     */
    @ApiModelProperty(name = "minPerTamt", value = "每标位最低投标量")
    private BigDecimal minPerTamt;

    /**
     * 每标位最高投标量
     */
    @ApiModelProperty(name = "maxPerUtamt", value = "每标位最高投标量")
    private BigDecimal maxPerUtamt;

    /**
     * 单笔最低认购量
     */
    @ApiModelProperty(name = "minPamt", value = "单笔最低认购量")
    private BigDecimal minPamt;

    /**
     * 单笔最高认购量
     */
    @ApiModelProperty(name = "maxPamt", value = "单笔最高认购量")
    private BigDecimal maxPamt;

    /**
     * 增发投标日
     */
    @ApiModelProperty(name = "settleDate", value = "增发投标日")
    private Date settleDate;

    /**
     * 投标起始日
     */
    @ApiModelProperty(name = "tenderDSdate", value = "投标起始日")
    private Date tenderDSdate;

    /**
     * 缴款日
     */
    @ApiModelProperty(name = "payDdate", value = "缴款日")
    private Date payDdate;

    /**
     * 缴款截止日
     */
    @ApiModelProperty(name = "payEdate", value = "缴款截止日")
    private Date payEdate;

    /**
     * 发行价格
     */
    @ApiModelProperty(name = "issuePrice", value = "发行价格")
    private BigDecimal issuePrice;

    /**
     * 发行方式
     */
    @ApiModelProperty(name = "issueClass", value = "发行方式")
    private String issueClass;

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
    @ApiModelProperty(name = "tenderTarget", value = "上市代码")
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
    private BigDecimal planIssueAmt;

    /**
     * 币种
     */
    @ApiModelProperty(name = "cyy", value = "币种")
    private String cyy;

    /**
     * 债券全称
     */
    @ApiModelProperty(name = "secName", value = "债券全称")
    private String secName;

    /**
     * 债券简称
     */
    @ApiModelProperty(name = "secAbbr", value = "债券简称")
    private String secAbbr;

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
    private String issuerName;

    /**
     * 起息日
     */
    @ApiModelProperty(name = "vdate", value = "起息日")
    private String vdate;

    /**
     * 到期日
     */
    @ApiModelProperty(name = "mdate", value = "到期日")
    private String mdate;

    /**
     * 利率类型
     */
    @ApiModelProperty(name = "rateType", value = "利率类型")
    private String rateType;

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
     * 发行方式
     */
    @ApiModelProperty(name = "issueType", value = "发行方式")
    private String issueType;

    /**
     * 息票品种
     */
    @ApiModelProperty(name = "couponSpecies", value = "息票品种")
    private String couponSpecies;

    /**
     * 付息频率
     */
    @ApiModelProperty(name = "payCycle", value = "付息频率")
    private String payCycle;


    public String getTitle() {
        return title;
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

    public Date getIssueSdate() {
        return issueSdate;
    }

    public void setIssueSdate(Date issueSdate) {
        this.issueSdate = issueSdate;
    }

    public Date getIssueEdate() {
        return issueEdate;
    }

    public void setIssueEdate(Date issueEdate) {
        this.issueEdate = issueEdate;
    }

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

    public Date getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    public Date getTenderDSdate() {
        return tenderDSdate;
    }

    public void setTenderDSdate(Date tenderDSdate) {
        this.tenderDSdate = tenderDSdate;
    }

    public Date getPayDdate() {
        return payDdate;
    }

    public void setPayDdate(Date payDdate) {
        this.payDdate = payDdate;
    }

    public Date getPayEdate() {
        return payEdate;
    }

    public void setPayEdate(Date payEdate) {
        this.payEdate = payEdate;
    }

    public BigDecimal getIssuePrice() {
        return issuePrice;
    }

    public void setIssuePrice(BigDecimal issuePrice) {
        this.issuePrice = issuePrice;
    }

    public String getIssueStyle() {
        return issueStyle;
    }

    public void setIssueStyle(String issueStyle) {
        this.issueStyle = issueStyle;
    }

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

    public BigDecimal getPlanIssueAmt() {
        return planIssueAmt;
    }

    public void setPlanIssueAmt(BigDecimal planIssueAmt) {
        this.planIssueAmt = planIssueAmt;
    }

    public String getCyy() {
        return cyy;
    }

    public void setCyy(String cyy) {
        this.cyy = cyy;
    }

    public String getSecName() {
        return secName;
    }

    public void setSecName(String secName) {
        this.secName = secName;
    }

    public String getSecAbbr() {
        return secAbbr;
    }

    public void setSecAbbr(String secAbbr) {
        this.secAbbr = secAbbr;
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

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
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

    public BigDecimal getReferYield() {
        return referYield;
    }

    public void setReferYield(BigDecimal referYield) {
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

    public String getIssueClass() {
        return issueClass;
    }

    public void setIssueClass(String issueClass) {
        this.issueClass = issueClass;
    }
}
