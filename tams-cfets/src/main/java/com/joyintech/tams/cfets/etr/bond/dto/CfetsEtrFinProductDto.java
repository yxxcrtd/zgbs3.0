package com.joyintech.tams.cfets.etr.bond.dto;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 银行间债券指令
 * @author chengzhong
 * @version 1.0 2021年1月19日
 * @see CfetsEtrFinProductDto
 * @since 1.0
 */
public class CfetsEtrFinProductDto {

    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 资产简称
     */
    private String finprodAbbr;

    /**
     * 资产名称
     */
    private String finprodName;

    /**
     * 证券管理账户/通道代码
     */
    private String secAcctId;

    /**
     * 组合T日可用现金
     */
    private BigDecimal zhkyxj_t;

    /**
     * 组合T+1日可用现金
     */
    private BigDecimal zhkyxj_t1;

    /**
     * 托管户T日可用现金
     */
    private BigDecimal tghkyxj_t;

    /**
     * 托管户T+1日可用现金
     */
    private BigDecimal tghkyxj_t1;

    /**
     * 通道T日可用现金
     */
    private BigDecimal tdkyxj_t;

    /**
     * 通道T+1日可用现金
     */
    private BigDecimal tdkyxj_t1;

    /**
     * 资产持仓券面总额
     */
    private BigDecimal zccc_qmze;

    /**
     * 资产可用券面总额
     */
    private BigDecimal zcky_qmze;

    /**
     * 债券全称
     */
    @ApiModelProperty(required = false,name="secName",value="债券全称")
    private String secName;

    /**
     * 债券简称
     */
    @ApiModelProperty(required = false,name="secAbbr",value="债券简称")
    private String secAbbr;

    /**
     * 债券分类
     */
    @ApiModelProperty(required = false,name="secType",value="债券分类")
    private String secType;

    /**
     * 发行人
     */
    @ApiModelProperty(required = false,name="issuer",value="发行人")
    private String issuer;

    /**
     * 发行人
     */
    private String issuerName;

    /**
     * 债券名称
     */
    @ApiModelProperty(required = false,name="secId",value="查询的主键ID")
    private String secId;

    /**
     * 起息日
     */
    @ApiModelProperty(required = false,name="vdate",value="起息日")
    private String vdate;

    /**
     * 到期日
     */
    @ApiModelProperty(required = false,name="mdate",value="到期日")
    private String mdate;

    /**
     * 剩余期限
     */
    @ApiModelProperty(required = false,name="lastqx",value="剩余期限")
    private String lastqx;

    /**
     * 下一付息日
     */
    @ApiModelProperty(required = false,name="lastPaydate",value="下一付息日")
    private String lastPaydate;

    /**
     * 利率类型
     */
    @ApiModelProperty(required = false,name="rateType",value="利率类型")
    private String rateType;

    /**
     * 票面利率
     */
    @ApiModelProperty(required = false,name="rate",value="票面利率")
    private String rate;

    /**
     * 债项评级
     */
    @ApiModelProperty(required = false,name="zxGrade",value="债项评级")
    private String zxGrade;

    /**
     * 主体评级
     */
    @ApiModelProperty(required = false,name="ztGrade",value="主体评级")
    private String ztGrade;

    /**
     * 含权标识
     */
    @ApiModelProperty(required = false,name="xqFlg",value="含权标识")
    private String xqFlg;

    /**
     * 下一行权日
     */
    @ApiModelProperty(required = false,name="xqdate",value="下一行权日")
    private String xqdate;

    /**
     * 最新净价
     */
    @ApiModelProperty(required = false,name="newPrice",value="最新净价")
    private String newPrice;

    /**
     * 估价净价
     */
    @ApiModelProperty(required = false,name="priceXq",value="估价净价（行权）")
    private String priceXq;

    /**
     * 最新收益率
     */
    @ApiModelProperty(required = false,name="newRate",value="最新收益率")
    private BigDecimal newRate;

    /**
     * 估价净价
     */
    @ApiModelProperty(required = false,name="priceTj",value="估价净价（推荐）")
    private String priceTj;

    /**
     * 估值收益率
     */
    @ApiModelProperty(required = false,name="gzRate",value="估值收益率")
    private String gzRate;

    /**
     * 担保人
     */
    private String sponsorName;

    /**
     * 债券二级分类
     */
    private String type2;

    /**
     * 发行方式
     */
    private String issueType;

    /**
     * 息票品种
     */
    private String couponSpecies;

    /**
     * 付息频率
     */
    private String payCycle;

    /**
     * 计息基础
     */
    private String basis;

    /**
     * 发行金额
     */
    private String issueAmt;

    /**
     * 市价久期
     */
    private String mDuration;

    /**
     * 利率久期
     */
    private String interestDuration;

    /**
     * 利差久期
     */
    private String sDuration;

    /**
     * 市价凸性
     */
    private String mConvexity;

    /**
     * 基点价值
     */
    private String bpvalue;

    /**
     * 债券市场余额
     */
    private String secMarketAmt;

    /**
     * 剩余行权期限
     */
    private String lastXQDate;

    /**
     * 期限
     */
    @ApiModelProperty(required = false,name="deadline",value="期限")
    private String deadline;

    public String getType2() {
        return type2;
    }

    public void setType2(String type2) {
        this.type2 = type2;
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

    public String getBasis() {
        return basis;
    }

    public void setBasis(String basis) {
        this.basis = basis;
    }

    public String getIssueAmt() {
        return issueAmt;
    }

    public void setIssueAmt(String issueAmt) {
        this.issueAmt = issueAmt;
    }

    public String getmDuration() {
        return mDuration;
    }

    public void setmDuration(String mDuration) {
        this.mDuration = mDuration;
    }

    public String getInterestDuration() {
        return interestDuration;
    }

    public void setInterestDuration(String interestDuration) {
        this.interestDuration = interestDuration;
    }

    public String getsDuration() {
        return sDuration;
    }

    public void setsDuration(String sDuration) {
        this.sDuration = sDuration;
    }

    public String getmConvexity() {
        return mConvexity;
    }

    public void setmConvexity(String mConvexity) {
        this.mConvexity = mConvexity;
    }

    public String getBpvalue() {
        return bpvalue;
    }

    public void setBpvalue(String bpvalue) {
        this.bpvalue = bpvalue;
    }

    public String getSecAbbr() {
        return secAbbr;
    }

    public void setSecAbbr(String secAbbr) {
        this.secAbbr = secAbbr;
    }

    /**
     * 市场代码
     */
    private String marketId;

    /**
     * 数据来源valueDate
     */
    private String valueSource;

    /**
     * 估值日
     */
    public String valueDate;

    /**
     * 评级结果
     */
    private String gradeResult;

    /**
     * 评级类型
     */
    private String otype;
    /**
     * 获取评级类型
     */
    public String getOtype() {
        return otype;
    }

    /**
     * 设置评级类型
     */
    public void setOtype(String otype) {
        this.otype = otype;
    }

    /**
     * 获取估值日
     */
    public String getValueDate() {
        return valueDate;
    }

    /**
     * 设置估值日
     */
    public void setValueDate(String valueDate) {
        this.valueDate = valueDate;
    }

    /**
     * 获取数据来源
     */
    public String getValueSource() {
        return valueSource;
    }
    /**
     * 设置数据来源
     */
    public void setValueSource(String valueSource) {
        this.valueSource = valueSource;
    }

    /**
     * 获取市场代码
     */
    public String getMarketId() {
        return marketId;
    }

    /**
     * 设置市场代码
     */
    public void setMarketId(String marketId) {
        this.marketId = marketId;
    }
    /**
     * 取得 评级结果
     *
     * @return 评级结果
     */
    public String getGradeResult() {
        return gradeResult;
    }
    /**
     * 设置 评级结果
     *
     * @param gradeResult
     *            评级结果
     */
    public void setGradeResult(String gradeResult) {
        this.gradeResult = gradeResult == null ? null : gradeResult.trim();
    }

    public String getSponsorName() {
        return sponsorName;
    }

    public void setSponsorName(String sponsorName) {
        this.sponsorName = sponsorName;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public BigDecimal getZhkyxj_t() {
        return zhkyxj_t;
    }

    public void setZhkyxj_t(BigDecimal zhkyxj_t) {
        this.zhkyxj_t = zhkyxj_t;
    }

    public BigDecimal getZhkyxj_t1() {
        return zhkyxj_t1;
    }

    public void setZhkyxj_t1(BigDecimal zhkyxj_t1) {
        this.zhkyxj_t1 = zhkyxj_t1;
    }

    public BigDecimal getTghkyxj_t() {
        return tghkyxj_t;
    }

    public void setTghkyxj_t(BigDecimal tghkyxj_t) {
        this.tghkyxj_t = tghkyxj_t;
    }

    public BigDecimal getTghkyxj_t1() {
        return tghkyxj_t1;
    }

    public void setTghkyxj_t1(BigDecimal tghkyxj_t1) {
        this.tghkyxj_t1 = tghkyxj_t1;
    }

    public BigDecimal getTdkyxj_t() {
        return tdkyxj_t;
    }

    public void setTdkyxj_t(BigDecimal tdkyxj_t) {
        this.tdkyxj_t = tdkyxj_t;
    }

    public BigDecimal getTdkyxj_t1() {
        return tdkyxj_t1;
    }

    public void setTdkyxj_t1(BigDecimal tdkyxj_t1) {
        this.tdkyxj_t1 = tdkyxj_t1;
    }

    public BigDecimal getZccc_qmze() {
        return zccc_qmze;
    }

    public void setZccc_qmze(BigDecimal zccc_qmze) {
        this.zccc_qmze = zccc_qmze;
    }

    public BigDecimal getZcky_qmze() {
        return zcky_qmze;
    }

    public void setZcky_qmze(BigDecimal zcky_qmze) {
        this.zcky_qmze = zcky_qmze;
    }

    public String getSecName() {
        return secName;
    }

    public void setSecName(String secName) {
        this.secName = secName;
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

    public String getLastqx() {
        return lastqx;
    }

    public void setLastqx(String lastqx) {
        this.lastqx = lastqx;
    }

    public String getLastPaydate() {
        return lastPaydate;
    }

    public void setLastPaydate(String lastPaydate) {
        this.lastPaydate = lastPaydate;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
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

    public String getXqFlg() {
        return xqFlg;
    }

    public void setXqFlg(String xqFlg) {
        this.xqFlg = xqFlg;
    }

    public String getXqdate() {
        return xqdate;
    }

    public void setXqdate(String xqdate) {
        this.xqdate = xqdate;
    }

    public String getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(String newPrice) {
        this.newPrice = newPrice;
    }

    public String getPriceXq() {
        return priceXq;
    }

    public void setPriceXq(String priceXq) {
        this.priceXq = priceXq;
    }

    public BigDecimal getNewRate() {
        return newRate;
    }

    public void setNewRate(BigDecimal newRate) {
        this.newRate = newRate;
    }

    public String getPriceTj() {
        return priceTj;
    }

    public void setPriceTj(String priceTj) {
        this.priceTj = priceTj;
    }

    public String getGzRate() {
        return gzRate;
    }

    public void setGzRate(String gzRate) {
        this.gzRate = gzRate;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public String getSecId() {
        return secId;
    }

    public void setSecId(String secId) {
        this.secId = secId;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public String getSecMarketAmt() {
        return secMarketAmt;
    }

    public void setSecMarketAmt(String secMarketAmt) {
        this.secMarketAmt = secMarketAmt;
    }

    public String getLastXQDate() {
        return lastXQDate;
    }

    public void setLastXQDate(String lastXQDate) {
        this.lastXQDate = lastXQDate;
    }
}
