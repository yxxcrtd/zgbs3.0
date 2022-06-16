package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class TrdDealTotalAdd extends BaseEntity {
    /**
     * 汇总交易编号
     */
    private String totleTradeId;

    /**
     * 委托代码
     */
    private String entrustId;

    /**
     * 本方券交易份额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal ourShareAmt;

    /**
     * 融入券交易份额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lendinShareAmt;

    /**
     * 本方券净价总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal ourCpriceAmt;

    /**
     * 融入券净价总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lendinCpriceAmt;

    /**
     * 本方券全价总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal ourFpriceAmt;

    /**
     * 融入券全价总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lendinFpriceAmt;

    /**
     * 本方券全价总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal ourFpriceAmt2;

    /**
     * 融入券全价总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal lendinFpriceAmt2;

    /**
     * 申购方式，网上申购、网下申购
     */
    private String purchModle;

    /**
     * 申购类型，优先配售、普通申购
     */
    private String purchType;

    /**
     * 申购日
     */
    private Date purchDate;

    /**
     * 预计申购缴款日
     */
    private Date purPayDate;

    /**
     * 预计申购确认日
     */
    private Date purCfmDate;

    /**
     * 申购申请份额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal purAmt;

    /**
     * 保证金金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal depositAmt;

    /**
     * 正股价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal firstUnitCprice3;

    /**
     * 预计转股价值
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal expConValue;

    /**
     * 转股溢价率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal preb;

    /**
     * 纯债溢价率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal prec;

    /**
     * 零星股数
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal spoShareNum;

    /**
     * 偏离度
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal deviation;

    /**
     * 折算比例
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal conversionRat;

    /**
     * 折算前单位净值
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal bconUnitCprice2;

    /**
     * 份额折算类型
     */
    private String conversionType;

    /**
     * 资产推荐方
     */
    private String assetRecommandOrg;

    /**
     * 提前终止利率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal terminateRate;

    /**
     * 主协议金融产品代码
     */
    private String mpFinprodId;

    /**
     * 主协议分支序号
     */
    private Long mpBranch;

    /**
     * 是否现金交割
     */
    private String isCashset;

    /**
     * 生成方式：接口接入、画面录入、批处理生成
     */
    private String genType;

    /**
     * 罚息金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal penaltyIntamt;

    /**
     * 结息方式,适用于活期存放
     */
    private String intSettType;

    /**
     * 清理应收款
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal clearRecAmt;

    /**
     * 清理减值
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal clearLossAmt;
    /**
     *本方
     */
    private String ouPartyId;

    /**
     * 取得 汇总交易编号
     * @return 汇总交易编号
     */
    public String getTotleTradeId() {
        return totleTradeId;
    }

    /**
     * 设置 汇总交易编号
     * @param totleTradeId 汇总交易编号
     */
    public void setTotleTradeId(String totleTradeId) {
        this.totleTradeId = totleTradeId == null ? null : totleTradeId.trim();
    }

    /**
     * 取得 委托代码
     * @return 委托代码
     */
    public String getEntrustId() {
        return entrustId;
    }

    /**
     * 设置 委托代码
     * @param entrustId 委托代码
     */
    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId == null ? null : entrustId.trim();
    }

    /**
     * 取得 本方券交易份额
     * @return 本方券交易份额
     */
    public BigDecimal getOurShareAmt() {
        return ourShareAmt;
    }

    /**
     * 设置 本方券交易份额
     * @param ourShareAmt 本方券交易份额
     */
    public void setOurShareAmt(BigDecimal ourShareAmt) {
        this.ourShareAmt = ourShareAmt;
    }

    /**
     * 取得 融入券交易份额
     * @return 融入券交易份额
     */
    public BigDecimal getLendinShareAmt() {
        return lendinShareAmt;
    }

    /**
     * 设置 融入券交易份额
     * @param lendinShareAmt 融入券交易份额
     */
    public void setLendinShareAmt(BigDecimal lendinShareAmt) {
        this.lendinShareAmt = lendinShareAmt;
    }

    /**
     * 取得 本方券净价总额
     * @return 本方券净价总额
     */
    public BigDecimal getOurCpriceAmt() {
        return ourCpriceAmt;
    }

    /**
     * 设置 本方券净价总额
     * @param ourCpriceAmt 本方券净价总额
     */
    public void setOurCpriceAmt(BigDecimal ourCpriceAmt) {
        this.ourCpriceAmt = ourCpriceAmt;
    }

    /**
     * 取得 融入券净价总额
     * @return 融入券净价总额
     */
    public BigDecimal getLendinCpriceAmt() {
        return lendinCpriceAmt;
    }

    /**
     * 设置 融入券净价总额
     * @param lendinCpriceAmt 融入券净价总额
     */
    public void setLendinCpriceAmt(BigDecimal lendinCpriceAmt) {
        this.lendinCpriceAmt = lendinCpriceAmt;
    }

    /**
     * 取得 本方券全价总额
     * @return 本方券全价总额
     */
    public BigDecimal getOurFpriceAmt() {
        return ourFpriceAmt;
    }

    /**
     * 设置 本方券全价总额
     * @param ourFpriceAmt 本方券全价总额
     */
    public void setOurFpriceAmt(BigDecimal ourFpriceAmt) {
        this.ourFpriceAmt = ourFpriceAmt;
    }

    /**
     * 取得 融入券全价总额
     * @return 融入券全价总额
     */
    public BigDecimal getLendinFpriceAmt() {
        return lendinFpriceAmt;
    }

    /**
     * 设置 融入券全价总额
     * @param lendinFpriceAmt 融入券全价总额
     */
    public void setLendinFpriceAmt(BigDecimal lendinFpriceAmt) {
        this.lendinFpriceAmt = lendinFpriceAmt;
    }

    /**
     * 取得 本方券全价总额
     * @return 本方券全价总额
     */
    public BigDecimal getOurFpriceAmt2() {
        return ourFpriceAmt2;
    }

    /**
     * 设置 本方券全价总额
     * @param ourFpriceAmt2 本方券全价总额
     */
    public void setOurFpriceAmt2(BigDecimal ourFpriceAmt2) {
        this.ourFpriceAmt2 = ourFpriceAmt2;
    }

    /**
     * 取得 融入券全价总额
     * @return 融入券全价总额
     */
    public BigDecimal getLendinFpriceAmt2() {
        return lendinFpriceAmt2;
    }

    /**
     * 设置 融入券全价总额
     * @param lendinFpriceAmt2 融入券全价总额
     */
    public void setLendinFpriceAmt2(BigDecimal lendinFpriceAmt2) {
        this.lendinFpriceAmt2 = lendinFpriceAmt2;
    }

    /**
     * 取得 申购方式，网上申购、网下申购
     * @return 申购方式，网上申购、网下申购
     */
    public String getPurchModle() {
        return purchModle;
    }

    /**
     * 设置 申购方式，网上申购、网下申购
     * @param purchModle 申购方式，网上申购、网下申购
     */
    public void setPurchModle(String purchModle) {
        this.purchModle = purchModle == null ? null : purchModle.trim();
    }

    /**
     * 取得 申购类型，优先配售、普通申购
     * @return 申购类型，优先配售、普通申购
     */
    public String getPurchType() {
        return purchType;
    }

    /**
     * 设置 申购类型，优先配售、普通申购
     * @param purchType 申购类型，优先配售、普通申购
     */
    public void setPurchType(String purchType) {
        this.purchType = purchType == null ? null : purchType.trim();
    }

    /**
     * 取得 申购日
     * @return 申购日
     */
    public Date getPurchDate() {
        return purchDate;
    }

    /**
     * 设置 申购日
     * @param purchDate 申购日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setPurchDate(Date purchDate) {
        this.purchDate = purchDate;
    }

    /**
     * 取得 预计申购缴款日
     * @return 预计申购缴款日
     */
    public Date getPurPayDate() {
        return purPayDate;
    }

    /**
     * 设置 预计申购缴款日
     * @param purPayDate 预计申购缴款日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setPurPayDate(Date purPayDate) {
        this.purPayDate = purPayDate;
    }

    /**
     * 取得 预计申购确认日
     * @return 预计申购确认日
     */
    public Date getPurCfmDate() {
        return purCfmDate;
    }

    /**
     * 设置 预计申购确认日
     * @param purCfmDate 预计申购确认日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setPurCfmDate(Date purCfmDate) {
        this.purCfmDate = purCfmDate;
    }

    /**
     * 取得 申购申请份额
     * @return 申购申请份额
     */
    public BigDecimal getPurAmt() {
        return purAmt;
    }

    /**
     * 设置 申购申请份额
     * @param purAmt 申购申请份额
     */
    public void setPurAmt(BigDecimal purAmt) {
        this.purAmt = purAmt;
    }

    /**
     * 取得 保证金金额
     * @return 保证金金额
     */
    public BigDecimal getDepositAmt() {
        return depositAmt;
    }

    /**
     * 设置 保证金金额
     * @param depositAmt 保证金金额
     */
    public void setDepositAmt(BigDecimal depositAmt) {
        this.depositAmt = depositAmt;
    }

    /**
     * 取得 正股价
     * @return 正股价
     */
    public BigDecimal getFirstUnitCprice3() {
        return firstUnitCprice3;
    }

    /**
     * 设置 正股价
     * @param firstUnitCprice3 正股价
     */
    public void setFirstUnitCprice3(BigDecimal firstUnitCprice3) {
        this.firstUnitCprice3 = firstUnitCprice3;
    }

    /**
     * 取得 预计转股价值
     * @return 预计转股价值
     */
    public BigDecimal getExpConValue() {
        return expConValue;
    }

    /**
     * 设置 预计转股价值
     * @param expConValue 预计转股价值
     */
    public void setExpConValue(BigDecimal expConValue) {
        this.expConValue = expConValue;
    }

    /**
     * 取得 转股溢价率
     * @return 转股溢价率
     */
    public BigDecimal getPreb() {
        return preb;
    }

    /**
     * 设置 转股溢价率
     * @param preb 转股溢价率
     */
    public void setPreb(BigDecimal preb) {
        this.preb = preb;
    }

    /**
     * 取得 纯债溢价率
     * @return 纯债溢价率
     */
    public BigDecimal getPrec() {
        return prec;
    }

    /**
     * 设置 纯债溢价率
     * @param prec 纯债溢价率
     */
    public void setPrec(BigDecimal prec) {
        this.prec = prec;
    }

    /**
     * 取得 零星股数
     * @return 零星股数
     */
    public BigDecimal getSpoShareNum() {
        return spoShareNum;
    }

    /**
     * 设置 零星股数
     * @param spoShareNum 零星股数
     */
    public void setSpoShareNum(BigDecimal spoShareNum) {
        this.spoShareNum = spoShareNum;
    }

    /**
     * 取得 偏离度
     * @return 偏离度
     */
    public BigDecimal getDeviation() {
        return deviation;
    }

    /**
     * 设置 偏离度
     * @param deviation 偏离度
     */
    public void setDeviation(BigDecimal deviation) {
        this.deviation = deviation;
    }

    /**
     * 取得 折算比例
     * @return 折算比例
     */
    public BigDecimal getConversionRat() {
        return conversionRat;
    }

    /**
     * 设置 折算比例
     * @param conversionRat 折算比例
     */
    public void setConversionRat(BigDecimal conversionRat) {
        this.conversionRat = conversionRat;
    }

    /**
     * 取得 折算前单位净值
     * @return 折算前单位净值
     */
    public BigDecimal getBconUnitCprice2() {
        return bconUnitCprice2;
    }

    /**
     * 设置 折算前单位净值
     * @param bconUnitCprice2 折算前单位净值
     */
    public void setBconUnitCprice2(BigDecimal bconUnitCprice2) {
        this.bconUnitCprice2 = bconUnitCprice2;
    }

    /**
     * 取得 份额折算类型
     * @return 份额折算类型
     */
    public String getConversionType() {
        return conversionType;
    }

    /**
     * 设置 份额折算类型
     * @param conversionType 份额折算类型
     */
    public void setConversionType(String conversionType) {
        this.conversionType = conversionType == null ? null : conversionType.trim();
    }

    /**
     * 取得 资产推荐方
     * @return 资产推荐方
     */
    public String getAssetRecommandOrg() {
        return assetRecommandOrg;
    }

    /**
     * 设置 资产推荐方
     * @param assetRecommandOrg 资产推荐方
     */
    public void setAssetRecommandOrg(String assetRecommandOrg) {
        this.assetRecommandOrg = assetRecommandOrg == null ? null : assetRecommandOrg.trim();
    }

    /**
     * 取得 提前终止利率
     * @return 提前终止利率
     */
    public BigDecimal getTerminateRate() {
        return terminateRate;
    }

    /**
     * 设置 提前终止利率
     * @param terminateRate 提前终止利率
     */
    public void setTerminateRate(BigDecimal terminateRate) {
        this.terminateRate = terminateRate;
    }

    /**
     * 取得 主协议金融产品代码
     * @return 主协议金融产品代码
     */
    public String getMpFinprodId() {
        return mpFinprodId;
    }

    /**
     * 设置 主协议金融产品代码
     * @param mpFinprodId 主协议金融产品代码
     */
    public void setMpFinprodId(String mpFinprodId) {
        this.mpFinprodId = mpFinprodId == null ? null : mpFinprodId.trim();
    }

    /**
     * 取得 主协议分支序号
     * @return 主协议分支序号
     */
    public Long getMpBranch() {
        return mpBranch;
    }

    /**
     * 设置 主协议分支序号
     * @param mpBranch 主协议分支序号
     */
    public void setMpBranch(Long mpBranch) {
        this.mpBranch = mpBranch;
    }

    /**
     * 取得 是否现金交割
     * @return 是否现金交割
     */
    public String getIsCashset() {
        return isCashset;
    }

    /**
     * 设置 是否现金交割
     * @param isCashset 是否现金交割
     */
    public void setIsCashset(String isCashset) {
        this.isCashset = isCashset == null ? null : isCashset.trim();
    }

    /**
     * 取得 生成方式：接口接入、画面录入、批处理生成
     * @return 生成方式：接口接入、画面录入、批处理生成
     */
    public String getGenType() {
        return genType;
    }

    /**
     * 设置 生成方式：接口接入、画面录入、批处理生成
     * @param genType 生成方式：接口接入、画面录入、批处理生成
     */
    public void setGenType(String genType) {
        this.genType = genType == null ? null : genType.trim();
    }

    /**
     * 取得 罚息金额
     * @return 罚息金额
     */
    public BigDecimal getPenaltyIntamt() {
        return penaltyIntamt;
    }

    /**
     * 设置 罚息金额
     * @param penaltyIntamt 罚息金额
     */
    public void setPenaltyIntamt(BigDecimal penaltyIntamt) {
        this.penaltyIntamt = penaltyIntamt;
    }

    /**
     * 取得 结息方式,适用于活期存放
     * @return 结息方式,适用于活期存放
     */
    public String getIntSettType() {
        return intSettType;
    }

    /**
     * 设置 结息方式,适用于活期存放
     * @param intSettType 结息方式,适用于活期存放
     */
    public void setIntSettType(String intSettType) {
        this.intSettType = intSettType == null ? null : intSettType.trim();
    }

    /**
     * 取得 清理应收款
     * @return 清理应收款
     */
    public BigDecimal getClearRecAmt() {
        return clearRecAmt;
    }

    /**
     * 设置 清理应收款
     * @param clearRecAmt 清理应收款
     */
    public void setClearRecAmt(BigDecimal clearRecAmt) {
        this.clearRecAmt = clearRecAmt;
    }

    /**
     * 取得 清理减值
     * @return 清理减值
     */
    public BigDecimal getClearLossAmt() {
        return clearLossAmt;
    }

    /**
     * 设置 清理减值
     * @param clearLossAmt 清理减值
     */
    public void setClearLossAmt(BigDecimal clearLossAmt) {
        this.clearLossAmt = clearLossAmt;
    }

    public String getOuPartyId() {
        return ouPartyId;
    }

    public void setOuPartyId(String ouPartyId) {
        this.ouPartyId = ouPartyId;
    }

    /**
     * 信用事件发生时间
     */
    private Date creditEventHappenedDate;

    /**
     * 信用事件决定日
     */
    private Date creditEventResolveDate;


    /**
     * 交付债券
     */
    private String deliverBond;

    public Date getCreditEventResolveDate() {
        return creditEventResolveDate;
    }

    public void setCreditEventResolveDate(Date creditEventResolveDate) {
        this.creditEventResolveDate = creditEventResolveDate;
    }

    public String getDeliverBond() {
        return deliverBond;
    }

    public void setDeliverBond(String deliverBond) {
        this.deliverBond = deliverBond;
    }

    public Date getCreditEventHappenedDate() {
        return creditEventHappenedDate;
    }

    public void setCreditEventHappenedDate(Date creditEventHappenedDate) {
        this.creditEventHappenedDate = creditEventHappenedDate;
    }
}