package com.joyintech.tams.common.base.entity;

import com.joyintech.framework.base.BaseEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 描述:TRD_TEMP_DEAL_TOTAL_ADD表的实体类
 * @version
 * @author:  Administrator
 * @创建时间: 2022-05-23
 */
public class TrdTempDealTotalAdd extends BaseEntity {
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
    private BigDecimal ourShareAmt;

    /**
     * 融入券交易份额
     */
    private BigDecimal lendinShareAmt;

    /**
     * 本方券净价总额
     */
    private BigDecimal ourCpriceAmt;

    /**
     * 融入券净价总额
     */
    private BigDecimal lendinCpriceAmt;

    /**
     * 本方券全价总额
     */
    private BigDecimal ourFpriceAmt;

    /**
     * 融入券全价总额
     */
    private BigDecimal lendinFpriceAmt;

    /**
     * OUR_FPRICE_AMT2
     */
    private BigDecimal ourFpriceAmt2;

    /**
     * LENDIN_FPRICE_AMT2
     */
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
    private BigDecimal purAmt;

    /**
     * 保证金金额
     */
    private BigDecimal depositAmt;

    /**
     * 正股价
     */
    private BigDecimal firstUnitCprice3;

    /**
     * 预计转股价值
     */
    private BigDecimal expConValue;

    /**
     * 转股溢价率
     */
    private BigDecimal preb;

    /**
     * 纯债溢价率
     */
    private BigDecimal prec;

    /**
     * 零星股数
     */
    private BigDecimal spoShareNum;

    /**
     * 偏离度
     */
    private BigDecimal deviation;

    /**
     * 折算比例
     */
    private BigDecimal conversionRat;

    /**
     * 折算前单位净值
     */
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
    private BigDecimal penaltyIntamt;

    /**
     * 结息方式,适用于活期存放
     */
    private String intSettType;

    /**
     * 清理应收款
     */
    private BigDecimal clearRecAmt;

    /**
     * 清理减值
     */
    private BigDecimal clearLossAmt;

    /**
     * 本方
     */
    private String ouPartyId;

    /**
     * 汇总交易编号
     * @return TOTLE_TRADE_ID 汇总交易编号
     */
    public String getTotleTradeId() {
        return totleTradeId;
    }

    /**
     * 汇总交易编号
     * @param totleTradeId 汇总交易编号
     */
    public void setTotleTradeId(String totleTradeId) {
        this.totleTradeId = totleTradeId == null ? null : totleTradeId.trim();
    }

    /**
     * 委托代码
     * @return ENTRUST_ID 委托代码
     */
    public String getEntrustId() {
        return entrustId;
    }

    /**
     * 委托代码
     * @param entrustId 委托代码
     */
    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId == null ? null : entrustId.trim();
    }

    /**
     * 本方券交易份额
     * @return OUR_SHARE_AMT 本方券交易份额
     */
    public BigDecimal getOurShareAmt() {
        return ourShareAmt;
    }

    /**
     * 本方券交易份额
     * @param ourShareAmt 本方券交易份额
     */
    public void setOurShareAmt(BigDecimal ourShareAmt) {
        this.ourShareAmt = ourShareAmt;
    }

    /**
     * 融入券交易份额
     * @return LENDIN_SHARE_AMT 融入券交易份额
     */
    public BigDecimal getLendinShareAmt() {
        return lendinShareAmt;
    }

    /**
     * 融入券交易份额
     * @param lendinShareAmt 融入券交易份额
     */
    public void setLendinShareAmt(BigDecimal lendinShareAmt) {
        this.lendinShareAmt = lendinShareAmt;
    }

    /**
     * 本方券净价总额
     * @return OUR_CPRICE_AMT 本方券净价总额
     */
    public BigDecimal getOurCpriceAmt() {
        return ourCpriceAmt;
    }

    /**
     * 本方券净价总额
     * @param ourCpriceAmt 本方券净价总额
     */
    public void setOurCpriceAmt(BigDecimal ourCpriceAmt) {
        this.ourCpriceAmt = ourCpriceAmt;
    }

    /**
     * 融入券净价总额
     * @return LENDIN_CPRICE_AMT 融入券净价总额
     */
    public BigDecimal getLendinCpriceAmt() {
        return lendinCpriceAmt;
    }

    /**
     * 融入券净价总额
     * @param lendinCpriceAmt 融入券净价总额
     */
    public void setLendinCpriceAmt(BigDecimal lendinCpriceAmt) {
        this.lendinCpriceAmt = lendinCpriceAmt;
    }

    /**
     * 本方券全价总额
     * @return OUR_FPRICE_AMT 本方券全价总额
     */
    public BigDecimal getOurFpriceAmt() {
        return ourFpriceAmt;
    }

    /**
     * 本方券全价总额
     * @param ourFpriceAmt 本方券全价总额
     */
    public void setOurFpriceAmt(BigDecimal ourFpriceAmt) {
        this.ourFpriceAmt = ourFpriceAmt;
    }

    /**
     * 融入券全价总额
     * @return LENDIN_FPRICE_AMT 融入券全价总额
     */
    public BigDecimal getLendinFpriceAmt() {
        return lendinFpriceAmt;
    }

    /**
     * 融入券全价总额
     * @param lendinFpriceAmt 融入券全价总额
     */
    public void setLendinFpriceAmt(BigDecimal lendinFpriceAmt) {
        this.lendinFpriceAmt = lendinFpriceAmt;
    }

    /**
     * OUR_FPRICE_AMT2
     * @return OUR_FPRICE_AMT2 OUR_FPRICE_AMT2
     */
    public BigDecimal getOurFpriceAmt2() {
        return ourFpriceAmt2;
    }

    /**
     * OUR_FPRICE_AMT2
     * @param ourFpriceAmt2 OUR_FPRICE_AMT2
     */
    public void setOurFpriceAmt2(BigDecimal ourFpriceAmt2) {
        this.ourFpriceAmt2 = ourFpriceAmt2;
    }

    /**
     * LENDIN_FPRICE_AMT2
     * @return LENDIN_FPRICE_AMT2 LENDIN_FPRICE_AMT2
     */
    public BigDecimal getLendinFpriceAmt2() {
        return lendinFpriceAmt2;
    }

    /**
     * LENDIN_FPRICE_AMT2
     * @param lendinFpriceAmt2 LENDIN_FPRICE_AMT2
     */
    public void setLendinFpriceAmt2(BigDecimal lendinFpriceAmt2) {
        this.lendinFpriceAmt2 = lendinFpriceAmt2;
    }

    /**
     * 申购方式，网上申购、网下申购
     * @return PURCH_MODLE 申购方式，网上申购、网下申购
     */
    public String getPurchModle() {
        return purchModle;
    }

    /**
     * 申购方式，网上申购、网下申购
     * @param purchModle 申购方式，网上申购、网下申购
     */
    public void setPurchModle(String purchModle) {
        this.purchModle = purchModle == null ? null : purchModle.trim();
    }

    /**
     * 申购类型，优先配售、普通申购
     * @return PURCH_TYPE 申购类型，优先配售、普通申购
     */
    public String getPurchType() {
        return purchType;
    }

    /**
     * 申购类型，优先配售、普通申购
     * @param purchType 申购类型，优先配售、普通申购
     */
    public void setPurchType(String purchType) {
        this.purchType = purchType == null ? null : purchType.trim();
    }

    /**
     * 申购日
     * @return PURCH_DATE 申购日
     */
    public Date getPurchDate() {
        return purchDate;
    }

    /**
     * 申购日
     * @param purchDate 申购日
     */
    public void setPurchDate(Date purchDate) {
        this.purchDate = purchDate;
    }

    /**
     * 预计申购缴款日
     * @return PUR_PAY_DATE 预计申购缴款日
     */
    public Date getPurPayDate() {
        return purPayDate;
    }

    /**
     * 预计申购缴款日
     * @param purPayDate 预计申购缴款日
     */
    public void setPurPayDate(Date purPayDate) {
        this.purPayDate = purPayDate;
    }

    /**
     * 预计申购确认日
     * @return PUR_CFM_DATE 预计申购确认日
     */
    public Date getPurCfmDate() {
        return purCfmDate;
    }

    /**
     * 预计申购确认日
     * @param purCfmDate 预计申购确认日
     */
    public void setPurCfmDate(Date purCfmDate) {
        this.purCfmDate = purCfmDate;
    }

    /**
     * 申购申请份额
     * @return PUR_AMT 申购申请份额
     */
    public BigDecimal getPurAmt() {
        return purAmt;
    }

    /**
     * 申购申请份额
     * @param purAmt 申购申请份额
     */
    public void setPurAmt(BigDecimal purAmt) {
        this.purAmt = purAmt;
    }

    /**
     * 保证金金额
     * @return DEPOSIT_AMT 保证金金额
     */
    public BigDecimal getDepositAmt() {
        return depositAmt;
    }

    /**
     * 保证金金额
     * @param depositAmt 保证金金额
     */
    public void setDepositAmt(BigDecimal depositAmt) {
        this.depositAmt = depositAmt;
    }

    /**
     * 正股价
     * @return FIRST_UNIT_CPRICE3 正股价
     */
    public BigDecimal getFirstUnitCprice3() {
        return firstUnitCprice3;
    }

    /**
     * 正股价
     * @param firstUnitCprice3 正股价
     */
    public void setFirstUnitCprice3(BigDecimal firstUnitCprice3) {
        this.firstUnitCprice3 = firstUnitCprice3;
    }

    /**
     * 预计转股价值
     * @return EXP_CON_VALUE 预计转股价值
     */
    public BigDecimal getExpConValue() {
        return expConValue;
    }

    /**
     * 预计转股价值
     * @param expConValue 预计转股价值
     */
    public void setExpConValue(BigDecimal expConValue) {
        this.expConValue = expConValue;
    }

    /**
     * 转股溢价率
     * @return PREB 转股溢价率
     */
    public BigDecimal getPreb() {
        return preb;
    }

    /**
     * 转股溢价率
     * @param preb 转股溢价率
     */
    public void setPreb(BigDecimal preb) {
        this.preb = preb;
    }

    /**
     * 纯债溢价率
     * @return PREC 纯债溢价率
     */
    public BigDecimal getPrec() {
        return prec;
    }

    /**
     * 纯债溢价率
     * @param prec 纯债溢价率
     */
    public void setPrec(BigDecimal prec) {
        this.prec = prec;
    }

    /**
     * 零星股数
     * @return SPO_SHARE_NUM 零星股数
     */
    public BigDecimal getSpoShareNum() {
        return spoShareNum;
    }

    /**
     * 零星股数
     * @param spoShareNum 零星股数
     */
    public void setSpoShareNum(BigDecimal spoShareNum) {
        this.spoShareNum = spoShareNum;
    }

    /**
     * 偏离度
     * @return DEVIATION 偏离度
     */
    public BigDecimal getDeviation() {
        return deviation;
    }

    /**
     * 偏离度
     * @param deviation 偏离度
     */
    public void setDeviation(BigDecimal deviation) {
        this.deviation = deviation;
    }

    /**
     * 折算比例
     * @return CONVERSION_RAT 折算比例
     */
    public BigDecimal getConversionRat() {
        return conversionRat;
    }

    /**
     * 折算比例
     * @param conversionRat 折算比例
     */
    public void setConversionRat(BigDecimal conversionRat) {
        this.conversionRat = conversionRat;
    }

    /**
     * 折算前单位净值
     * @return BCON_UNIT_CPRICE2 折算前单位净值
     */
    public BigDecimal getBconUnitCprice2() {
        return bconUnitCprice2;
    }

    /**
     * 折算前单位净值
     * @param bconUnitCprice2 折算前单位净值
     */
    public void setBconUnitCprice2(BigDecimal bconUnitCprice2) {
        this.bconUnitCprice2 = bconUnitCprice2;
    }

    /**
     * 份额折算类型
     * @return CONVERSION_TYPE 份额折算类型
     */
    public String getConversionType() {
        return conversionType;
    }

    /**
     * 份额折算类型
     * @param conversionType 份额折算类型
     */
    public void setConversionType(String conversionType) {
        this.conversionType = conversionType == null ? null : conversionType.trim();
    }

    /**
     * 资产推荐方
     * @return ASSET_RECOMMAND_ORG 资产推荐方
     */
    public String getAssetRecommandOrg() {
        return assetRecommandOrg;
    }

    /**
     * 资产推荐方
     * @param assetRecommandOrg 资产推荐方
     */
    public void setAssetRecommandOrg(String assetRecommandOrg) {
        this.assetRecommandOrg = assetRecommandOrg == null ? null : assetRecommandOrg.trim();
    }

    /**
     * 提前终止利率
     * @return TERMINATE_RATE 提前终止利率
     */
    public BigDecimal getTerminateRate() {
        return terminateRate;
    }

    /**
     * 提前终止利率
     * @param terminateRate 提前终止利率
     */
    public void setTerminateRate(BigDecimal terminateRate) {
        this.terminateRate = terminateRate;
    }

    /**
     * 主协议金融产品代码
     * @return MP_FINPROD_ID 主协议金融产品代码
     */
    public String getMpFinprodId() {
        return mpFinprodId;
    }

    /**
     * 主协议金融产品代码
     * @param mpFinprodId 主协议金融产品代码
     */
    public void setMpFinprodId(String mpFinprodId) {
        this.mpFinprodId = mpFinprodId == null ? null : mpFinprodId.trim();
    }

    /**
     * 主协议分支序号
     * @return MP_BRANCH 主协议分支序号
     */
    public Long getMpBranch() {
        return mpBranch;
    }

    /**
     * 主协议分支序号
     * @param mpBranch 主协议分支序号
     */
    public void setMpBranch(Long mpBranch) {
        this.mpBranch = mpBranch;
    }

    /**
     * 是否现金交割
     * @return IS_CASHSET 是否现金交割
     */
    public String getIsCashset() {
        return isCashset;
    }

    /**
     * 是否现金交割
     * @param isCashset 是否现金交割
     */
    public void setIsCashset(String isCashset) {
        this.isCashset = isCashset == null ? null : isCashset.trim();
    }

    /**
     * 生成方式：接口接入、画面录入、批处理生成
     * @return GEN_TYPE 生成方式：接口接入、画面录入、批处理生成
     */
    public String getGenType() {
        return genType;
    }

    /**
     * 生成方式：接口接入、画面录入、批处理生成
     * @param genType 生成方式：接口接入、画面录入、批处理生成
     */
    public void setGenType(String genType) {
        this.genType = genType == null ? null : genType.trim();
    }

    /**
     * 罚息金额
     * @return PENALTY_INTAMT 罚息金额
     */
    public BigDecimal getPenaltyIntamt() {
        return penaltyIntamt;
    }

    /**
     * 罚息金额
     * @param penaltyIntamt 罚息金额
     */
    public void setPenaltyIntamt(BigDecimal penaltyIntamt) {
        this.penaltyIntamt = penaltyIntamt;
    }

    /**
     * 结息方式,适用于活期存放
     * @return INT_SETT_TYPE 结息方式,适用于活期存放
     */
    public String getIntSettType() {
        return intSettType;
    }

    /**
     * 结息方式,适用于活期存放
     * @param intSettType 结息方式,适用于活期存放
     */
    public void setIntSettType(String intSettType) {
        this.intSettType = intSettType == null ? null : intSettType.trim();
    }

    /**
     * 清理应收款
     * @return CLEAR_REC_AMT 清理应收款
     */
    public BigDecimal getClearRecAmt() {
        return clearRecAmt;
    }

    /**
     * 清理应收款
     * @param clearRecAmt 清理应收款
     */
    public void setClearRecAmt(BigDecimal clearRecAmt) {
        this.clearRecAmt = clearRecAmt;
    }

    /**
     * 清理减值
     * @return CLEAR_LOSS_AMT 清理减值
     */
    public BigDecimal getClearLossAmt() {
        return clearLossAmt;
    }

    /**
     * 清理减值
     * @param clearLossAmt 清理减值
     */
    public void setClearLossAmt(BigDecimal clearLossAmt) {
        this.clearLossAmt = clearLossAmt;
    }

    /**
     * 本方
     * @return OU_PARTY_ID 本方
     */
    public String getOuPartyId() {
        return ouPartyId;
    }

    /**
     * 本方
     * @param ouPartyId 本方
     */
    public void setOuPartyId(String ouPartyId) {
        this.ouPartyId = ouPartyId == null ? null : ouPartyId.trim();
    }
}