package com.joyintech.tams.valuation.common.interf.tempTables.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class ValRepDealSecSubject extends BaseEntity {
    /**
     * 回购起息的交易编号
     */
    private String tradeId;

    /**
     * 回购标的名称
     */
    private String tradeType;

    /**
     * 新增、修改、删除
     */
    private String dataActionSign;

    /**
     * 期限品种
     */
    private String termKind;

    /**
     * 默认：债券质押式回购
     */
    private String busiType;

    /**
     * 起息日
     */
    private Date vdate;

    /**
     * 到期日
     */
    private Date mdate;

    /**
     * 计息基础
     */
    private String basis;

    /**
     * 资产类型
     */
    private String assetClass;

    /**
     * 利率类型
     */
    private String rateType;

    /**
     * 回购利率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal redRate;

    /**
     * 名义本金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal capital;

    /**
     * 到期利息
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mIntAmt;

    /**
     * 到期本金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mPrinAmt;

    /**
     * 到期本金+到期利息
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mTradeAmt;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 本金收益特征
     */
    private String capiIncomeFeature;

    /**
     * 核算分类
     */
    private String valClas;

    /**
     * 登记托管机构
     */
    private String registOrg;

    /**
     * 押品只数
     */
    private Long pledgeSecNum;

    /**
     * 交易场所
     */
    private String tradeMarket;
    
    /**
     * 首期交付日
     */
    private Date vpayDate;

    /**
     * 到期交付日
     */
    private Date mpayDate;

    /**
     * 交易对手编码
     */
    private String counterId;
    /**
     * 回购类型
     */
    private String targetBackType;

    /**
     * 取得 回购起息的交易编号
     * @return 回购起息的交易编号
     */
    public String getTradeId() {
        return tradeId;
    }

    /**
     * 设置 回购起息的交易编号
     * @param tradeId 回购起息的交易编号
     */
    public void setTradeId(String tradeId) {
        this.tradeId = tradeId == null ? null : tradeId.trim();
    }

    /**
     * 取得 回购标的名称
     * @return 回购标的名称
     */
    public String getTradeType() {
        return tradeType;
    }

    /**
     * 设置 回购标的名称
     * @param tradeType 回购标的名称
     */
    public void setTradeType(String tradeType) {
        this.tradeType = tradeType == null ? null : tradeType.trim();
    }

    /**
     * 取得 新增、修改、删除
     * @return 新增、修改、删除
     */
    public String getDataActionSign() {
        return dataActionSign;
    }

    /**
     * 设置 新增、修改、删除
     * @param dataActionSign 新增、修改、删除
     */
    public void setDataActionSign(String dataActionSign) {
        this.dataActionSign = dataActionSign == null ? null : dataActionSign.trim();
    }

    /**
     * 取得 期限品种
     * @return 期限品种
     */
    public String getTermKind() {
        return termKind;
    }

    /**
     * 设置 期限品种
     * @param termKind 期限品种
     */
    public void setTermKind(String termKind) {
        this.termKind = termKind == null ? null : termKind.trim();
    }

    /**
     * 取得 默认：债券质押式回购
     * @return 默认：债券质押式回购
     */
    public String getBusiType() {
        return busiType;
    }

    /**
     * 设置 默认：债券质押式回购
     * @param busiType 默认：债券质押式回购
     */
    public void setBusiType(String busiType) {
        this.busiType = busiType == null ? null : busiType.trim();
    }

    /**
     * 取得 起息日
     * @return 起息日
     */
    public Date getVdate() {
        return vdate;
    }

    /**
     * 设置 起息日
     * @param vdate 起息日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    /**
     * 取得 到期日
     * @return 到期日
     */
    public Date getMdate() {
        return mdate;
    }

    /**
     * 设置 到期日
     * @param mdate 到期日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    /**
     * 取得 计息基础
     * @return 计息基础
     */
    public String getBasis() {
        return basis;
    }

    /**
     * 设置 计息基础
     * @param basis 计息基础
     */
    public void setBasis(String basis) {
        this.basis = basis == null ? null : basis.trim();
    }

    /**
     * 取得 资产类型
     * @return 资产类型
     */
    public String getAssetClass() {
        return assetClass;
    }

    /**
     * 设置 资产类型
     * @param assetClass 资产类型
     */
    public void setAssetClass(String assetClass) {
        this.assetClass = assetClass == null ? null : assetClass.trim();
    }

    /**
     * 取得 利率类型
     * @return 利率类型
     */
    public String getRateType() {
        return rateType;
    }

    /**
     * 设置 利率类型
     * @param rateType 利率类型
     */
    public void setRateType(String rateType) {
        this.rateType = rateType == null ? null : rateType.trim();
    }

    /**
     * 取得 回购利率
     * @return 回购利率
     */
    public BigDecimal getRedRate() {
        return redRate;
    }

    /**
     * 设置 回购利率
     * @param redRate 回购利率
     */
    public void setRedRate(BigDecimal redRate) {
        this.redRate = redRate;
    }

    /**
     * 取得 名义本金
     * @return 名义本金
     */
    public BigDecimal getCapital() {
        return capital;
    }

    /**
     * 设置 名义本金
     * @param capital 名义本金
     */
    public void setCapital(BigDecimal capital) {
        this.capital = capital;
    }

    /**
     * 取得 到期利息
     * @return 到期利息
     */
    public BigDecimal getmIntAmt() {
        return mIntAmt;
    }

    /**
     * 设置 到期利息
     * @param mIntAmt 到期利息
     */
    public void setmIntAmt(BigDecimal mIntAmt) {
        this.mIntAmt = mIntAmt;
    }

    /**
     * 取得 到期本金
     * @return 到期本金
     */
    public BigDecimal getmPrinAmt() {
        return mPrinAmt;
    }

    /**
     * 设置 到期本金
     * @param mPrinAmt 到期本金
     */
    public void setmPrinAmt(BigDecimal mPrinAmt) {
        this.mPrinAmt = mPrinAmt;
    }

    /**
     * 取得 到期本金+到期利息
     * @return 到期本金+到期利息
     */
    public BigDecimal getmTradeAmt() {
        return mTradeAmt;
    }

    /**
     * 设置 到期本金+到期利息
     * @param mTradeAmt 到期本金+到期利息
     */
    public void setmTradeAmt(BigDecimal mTradeAmt) {
        this.mTradeAmt = mTradeAmt;
    }

    /**
     * 取得 币种
     * @return 币种
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * 设置 币种
     * @param ccy 币种
     */
    public void setCcy(String ccy) {
        this.ccy = ccy == null ? null : ccy.trim();
    }

    /**
     * 取得 本金收益特征
     * @return 本金收益特征
     */
    public String getCapiIncomeFeature() {
        return capiIncomeFeature;
    }

    /**
     * 设置 本金收益特征
     * @param capiIncomeFeature 本金收益特征
     */
    public void setCapiIncomeFeature(String capiIncomeFeature) {
        this.capiIncomeFeature = capiIncomeFeature == null ? null : capiIncomeFeature.trim();
    }

    /**
     * 取得 核算分类
     * @return 核算分类
     */
    public String getValClas() {
        return valClas;
    }

    /**
     * 设置 核算分类
     * @param valClas 核算分类
     */
    public void setValClas(String valClas) {
        this.valClas = valClas == null ? null : valClas.trim();
    }

    /**
     * 取得 登记托管机构
     * @return 登记托管机构
     */
    public String getRegistOrg() {
        return registOrg;
    }

    /**
     * 设置 登记托管机构
     * @param registOrg 登记托管机构
     */
    public void setRegistOrg(String registOrg) {
        this.registOrg = registOrg == null ? null : registOrg.trim();
    }

    /**
     * 取得 押品只数
     * @return 押品只数
     */
    public Long getPledgeSecNum() {
        return pledgeSecNum;
    }

    /**
     * 设置 押品只数
     * @param pledgeSecNum 押品只数
     */
    public void setPledgeSecNum(Long pledgeSecNum) {
        this.pledgeSecNum = pledgeSecNum;
    }

    /**
     * 取得 交易场所
     * @return 交易场所
     */
    public String getTradeMarket() {
        return tradeMarket;
    }

    /**
     * 设置 交易场所
     * @param tradeMarket 交易场所
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket == null ? null : tradeMarket.trim();
    }

    /**
     * 获取 vpayDate
     * @return vpayDate.
     */
    public Date getVpayDate() {
        return vpayDate;
    }

    /**
     * 设置 vpayDate
     * @param vpayDate vpayDate
     */
    public void setVpayDate(Date vpayDate) {
        this.vpayDate = vpayDate;
    }

    /**
     * 获取 mpayDate
     * @return mpayDate.
     */
    public Date getMpayDate() {
        return mpayDate;
    }

    /**
     * 设置 mpayDate
     * @param mpayDate mpayDate
     */
    public void setMpayDate(Date mpayDate) {
        this.mpayDate = mpayDate;
    }
    
    private String belongInstitutionCode;

    public String getBelongInstitutionCode() {
        return belongInstitutionCode;
    }

    public void setBelongInstitutionCode(String belongInstitutionCode) {
        this.belongInstitutionCode = belongInstitutionCode;
    }

    /**
     * 获取 counterId
     * @return counterId.
     */
    public String getCounterId() {
        return counterId;
    }

    /**
     * 设置 counterId
     * @param counterId counterId
     */
    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getTargetBackType() {
        return targetBackType;
    }

    public void setTargetBackType(String targetBackType) {
        this.targetBackType = targetBackType;
    }
}