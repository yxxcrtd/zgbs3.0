package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import com.joyintech.framework.validation.annotation.Amount;

import java.math.BigDecimal;
import java.util.Date;

public class PrdSaleParam extends BaseEntity {
    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 产品品牌
     */
    private String prdBand;

    /**
     * 币种汇钞标志
     */
    private String cyyType;

    /**
     * 销售渠道，多选逗号分隔，柜面、网银、直销银行、手机银行、其他电子渠道，可多选
     */
    private String saleChannel;

    /**
     * 销售地区，多选逗号分隔
     */
    private String saleArea;

    /**
     * 募集上限
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal saleMax;

    /**
     * 募集下限
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal saleMin;

    /**
     * 认购起点
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal toffStart;

    /**
     * 最少追加金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lowestAmt;

    /**
     * 巨额赎回比例
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal hugeRedRatio;

    /**
     * 最低账面份额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal minPaperQty;

    /**
     * 最低赎回份额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal minRedmQty;

    /**
     * 是否可质押
     */
    private String canPlge;

    /**
     * 质押率上限
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal maxPlgeRate;

    /**
     * 首次募集开始日
     */
    private Date firstSaleVdate;

    /**
     * 首次募集结束日
     */
    private Date firstSaleMdate;

    /**
     * 是否支持代销
     */
    private String consignmentFlag;

    /**
     * 是否允许提前终止
     */
    private String beforeendFlag;

    /**
     * 是否允许客户赎回
     */
    private String redFlag;

    /**
     * 是否可违约赎回
     */
    private String defaultredFlag;

    /**
     * 是否可提前成立
     */
    private String beforeestablishFlag;

    /**
     * 是否续投
     */
    private String continueFlag;

    /**
     * 计划募集金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal raiseAmtPlan;

    /**
     * 目标客户类型
     */
    private String investorType;

    /**
     * 同业机构
     */
    private String sameOrg;

    /**
     * 单一投资者持有上限
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal singleInvestorholdingMax;

    /**
     * 是否面向单一投资者
     */
    private String isFaceSingleInvestor;

    /**
     * 取得 金融产品代码
     * @return 金融产品代码
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 设置 金融产品代码
     * @param finprodId 金融产品代码
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId == null ? null : finprodId.trim();
    }

    /**
     * 取得 产品品牌
     * @return 产品品牌
     */
    public String getPrdBand() {
        return prdBand;
    }

    /**
     * 设置 产品品牌
     * @param prdBand 产品品牌
     */
    public void setPrdBand(String prdBand) {
        this.prdBand = prdBand == null ? null : prdBand.trim();
    }

    /**
     * 取得 币种汇钞标志
     * @return 币种汇钞标志
     */
    public String getCyyType() {
        return cyyType;
    }

    /**
     * 设置 币种汇钞标志
     * @param cyyType 币种汇钞标志
     */
    public void setCyyType(String cyyType) {
        this.cyyType = cyyType == null ? null : cyyType.trim();
    }

    /**
     * 取得 销售渠道，多选逗号分隔，柜面、网银、直销银行、手机银行、其他电子渠道，可多选
     * @return 销售渠道，多选逗号分隔，柜面、网银、直销银行、手机银行、其他电子渠道，可多选
     */
    public String getSaleChannel() {
        return saleChannel;
    }

    /**
     * 设置 销售渠道，多选逗号分隔，柜面、网银、直销银行、手机银行、其他电子渠道，可多选
     * @param saleChannel 销售渠道，多选逗号分隔，柜面、网银、直销银行、手机银行、其他电子渠道，可多选
     */
    public void setSaleChannel(String saleChannel) {
        this.saleChannel = saleChannel == null ? null : saleChannel.trim();
    }

    /**
     * 取得 销售地区，多选逗号分隔
     * @return 销售地区，多选逗号分隔
     */
    public String getSaleArea() {
        return saleArea;
    }

    /**
     * 设置 销售地区，多选逗号分隔
     * @param saleArea 销售地区，多选逗号分隔
     */
    public void setSaleArea(String saleArea) {
        this.saleArea = saleArea == null ? null : saleArea.trim();
    }

    /**
     * 取得 募集上限
     * @return 募集上限
     */
    public BigDecimal getSaleMax() {
        return saleMax;
    }

    /**
     * 设置 募集上限
     * @param saleMax 募集上限
     */
    public void setSaleMax(BigDecimal saleMax) {
        this.saleMax = saleMax;
    }

    /**
     * 取得 募集下限
     * @return 募集下限
     */
    public BigDecimal getSaleMin() {
        return saleMin;
    }

    /**
     * 设置 募集下限
     * @param saleMin 募集下限
     */
    public void setSaleMin(BigDecimal saleMin) {
        this.saleMin = saleMin;
    }

    /**
     * 取得 认购起点
     * @return 认购起点
     */
    public BigDecimal getToffStart() {
        return toffStart;
    }

    /**
     * 设置 认购起点
     * @param toffStart 认购起点
     */
    public void setToffStart(BigDecimal toffStart) {
        this.toffStart = toffStart;
    }

    /**
     * 取得 最少追加金额
     * @return 最少追加金额
     */
    public BigDecimal getLowestAmt() {
        return lowestAmt;
    }

    /**
     * 设置 最少追加金额
     * @param lowestAmt 最少追加金额
     */
    public void setLowestAmt(BigDecimal lowestAmt) {
        this.lowestAmt = lowestAmt;
    }

    /**
     * 取得 巨额赎回比例
     * @return 巨额赎回比例
     */
    public BigDecimal getHugeRedRatio() {
        return hugeRedRatio;
    }

    /**
     * 设置 巨额赎回比例
     * @param hugeRedRatio 巨额赎回比例
     */
    public void setHugeRedRatio(BigDecimal hugeRedRatio) {
        this.hugeRedRatio = hugeRedRatio;
    }

    /**
     * 取得 最低账面份额
     * @return 最低账面份额
     */
    public BigDecimal getMinPaperQty() {
        return minPaperQty;
    }

    /**
     * 设置 最低账面份额
     * @param minPaperQty 最低账面份额
     */
    public void setMinPaperQty(BigDecimal minPaperQty) {
        this.minPaperQty = minPaperQty;
    }

    /**
     * 取得 最低赎回份额
     * @return 最低赎回份额
     */
    public BigDecimal getMinRedmQty() {
        return minRedmQty;
    }

    /**
     * 设置 最低赎回份额
     * @param minRedmQty 最低赎回份额
     */
    public void setMinRedmQty(BigDecimal minRedmQty) {
        this.minRedmQty = minRedmQty;
    }

    /**
     * 取得 是否可质押
     * @return 是否可质押
     */
    public String getCanPlge() {
        return canPlge;
    }

    /**
     * 设置 是否可质押
     * @param canPlge 是否可质押
     */
    public void setCanPlge(String canPlge) {
        this.canPlge = canPlge == null ? null : canPlge.trim();
    }

    /**
     * 取得 质押率上限
     * @return 质押率上限
     */
    public BigDecimal getMaxPlgeRate() {
        return maxPlgeRate;
    }

    /**
     * 设置 质押率上限
     * @param maxPlgeRate 质押率上限
     */
    public void setMaxPlgeRate(BigDecimal maxPlgeRate) {
        this.maxPlgeRate = maxPlgeRate;
    }

    /**
     * 取得 首次募集开始日
     * @return 首次募集开始日
     */
    public Date getFirstSaleVdate() {
        return firstSaleVdate;
    }

    /**
     * 设置 首次募集开始日
     * @param firstSaleVdate 首次募集开始日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setFirstSaleVdate(Date firstSaleVdate) {
        this.firstSaleVdate = firstSaleVdate;
    }

    /**
     * 取得 首次募集结束日
     * @return 首次募集结束日
     */
    public Date getFirstSaleMdate() {
        return firstSaleMdate;
    }

    /**
     * 设置 首次募集结束日
     * @param firstSaleMdate 首次募集结束日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setFirstSaleMdate(Date firstSaleMdate) {
        this.firstSaleMdate = firstSaleMdate;
    }

    /**
     * 取得 是否支持代销
     * @return 是否支持代销
     */
    public String getConsignmentFlag() {
        return consignmentFlag;
    }

    /**
     * 设置 是否支持代销
     * @param consignmentFlag 是否支持代销
     */
    public void setConsignmentFlag(String consignmentFlag) {
        this.consignmentFlag = consignmentFlag == null ? null : consignmentFlag.trim();
    }

    /**
     * 取得 是否允许提前终止
     * @return 是否允许提前终止
     */
    public String getBeforeendFlag() {
        return beforeendFlag;
    }

    /**
     * 设置 是否允许提前终止
     * @param beforeendFlag 是否允许提前终止
     */
    public void setBeforeendFlag(String beforeendFlag) {
        this.beforeendFlag = beforeendFlag == null ? null : beforeendFlag.trim();
    }

    /**
     * 取得 是否允许客户赎回
     * @return 是否允许客户赎回
     */
    public String getRedFlag() {
        return redFlag;
    }

    /**
     * 设置 是否允许客户赎回
     * @param redFlag 是否允许客户赎回
     */
    public void setRedFlag(String redFlag) {
        this.redFlag = redFlag == null ? null : redFlag.trim();
    }

    /**
     * 取得 是否可违约赎回
     * @return 是否可违约赎回
     */
    public String getDefaultredFlag() {
        return defaultredFlag;
    }

    /**
     * 设置 是否可违约赎回
     * @param defaultredFlag 是否可违约赎回
     */
    public void setDefaultredFlag(String defaultredFlag) {
        this.defaultredFlag = defaultredFlag == null ? null : defaultredFlag.trim();
    }

    /**
     * 取得 是否可提前成立
     * @return 是否可提前成立
     */
    public String getBeforeestablishFlag() {
        return beforeestablishFlag;
    }

    /**
     * 设置 是否可提前成立
     * @param beforeestablishFlag 是否可提前成立
     */
    public void setBeforeestablishFlag(String beforeestablishFlag) {
        this.beforeestablishFlag = beforeestablishFlag == null ? null : beforeestablishFlag.trim();
    }

    /**
     * 取得 是否续投
     * @return 是否续投
     */
    public String getContinueFlag() {
        return continueFlag;
    }

    /**
     * 设置 是否续投
     * @param continueFlag 是否续投
     */
    public void setContinueFlag(String continueFlag) {
        this.continueFlag = continueFlag == null ? null : continueFlag.trim();
    }

    /**
     * 取得 计划募集金额
     * @return 计划募集金额
     */
    public BigDecimal getRaiseAmtPlan() {
        return raiseAmtPlan;
    }

    /**
     * 设置 计划募集金额
     * @param raiseAmtPlan 计划募集金额
     */
    public void setRaiseAmtPlan(BigDecimal raiseAmtPlan) {
        this.raiseAmtPlan = raiseAmtPlan;
    }

    /**
     * 取得 目标客户类型
     * @return 目标客户类型
     */
    public String getInvestorType() {
        return investorType;
    }

    /**
     * 设置 目标客户类型
     * @param investorType 目标客户类型
     */
    public void setInvestorType(String investorType) {
        this.investorType = investorType == null ? null : investorType.trim();
    }

    /**
     * 取得 同业机构
     * @return 同业机构
     */
    public String getSameOrg() {
        return sameOrg;
    }

    /**
     * 设置 同业机构
     * @param sameOrg 同业机构
     */
    public void setSameOrg(String sameOrg) {
        this.sameOrg = sameOrg == null ? null : sameOrg.trim();
    }

    public BigDecimal getSingleInvestorholdingMax() {
        return singleInvestorholdingMax;
    }

    public void setSingleInvestorholdingMax(BigDecimal singleInvestorholdingMax) {
        this.singleInvestorholdingMax = singleInvestorholdingMax;
    }

    public String getIsFaceSingleInvestor() {
        return isFaceSingleInvestor;
    }

    public void setIsFaceSingleInvestor(String isFaceSingleInvestor) {
        this.isFaceSingleInvestor = isFaceSingleInvestor;
    }
}