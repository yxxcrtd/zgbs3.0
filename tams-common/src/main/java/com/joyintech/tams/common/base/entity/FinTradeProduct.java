package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class FinTradeProduct extends FinTradeProductKey {
    /**
     * ????????????????????????
     */
    private String finprodType;

    /**
     * ????????????????????????????????????????
     */
    private String finprodType2;

    /**
     * ??????????????????
     */
    private String profitType;

    /**
     * ????
     */
    private String branchType;

    /**
     * ????
     */
    private String chlAgrtId;

    /**
     * ????
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal prin;

    /**
     * ????
     */
    private String ccy;

    /**
     * ???
     */
    private Date vdate;

    /**
     * ???
     */
    private Date mdate;

    /**
     * ??(?)
     */
    private Long term;

    /**
     * ????
     */
    private String intType;

    /**
     * ????
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal intRate;

    /**
     * ????????
     */
    private String intRateId;

    /**
     * ????
     */
    private String basis;

    /**
     * ????
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mPrinAmt;

    /**
     * ????
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mIntAmt;

    /**
     * ????
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mTradeAmt;

    /**
     * ?????????????
     */
    private String capiIncomeFeature;

    /**
     * ????????????????
     */
    private String oFinprodId;

    /**
     * ????
     */
    private String tradeMarket;

    /**
     * ????
     */
    private String calendarId;

    /**
     * ????
     */
    private String termType;

    /**
     * ????
     */
    private String counterId;

    /**
     * null
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mUnitCprice;

    /**
     * null
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mUnitInt;

    /**
     * null
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mUnitFprice;

    /**
     * null
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mYield;

    /**
     * null
     */
    private String mDeliveryType;

    /**
     * ?????
     */
    private Date vpayDate;

    /**
     * ?????
     */
    private Date mpayDate;

    /**
     * ????
     */
    private String contractNo;

    /**
     * ??????
     */
    private Long actCapDays;

    /**
     * ????
     */
    private String irsType;

    /**
     * ??????
     */
    private String excPs;

    /**
     * ???
     */
    private String curPair;

    /**
     * ??????
     */
    private String excFxsTermType;

    /**
     * ???????
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal usdAmt;

    /**
     * ????????
     */
    private String difSettleRefRate;

    /**
     * ??????
     */
    private String conflictSolveWay;

    /**
     * 期次代码
     */
    private String periodId;

    /**
     * 截止日是否冲回
     */
    private String isRushBack;

    /**
     * 合同名称
     */
    private String contractName;
    
    /**
     * 持有目的 HOLD_PURPOSE 中债新增 20200409
     */
    private String holdPurpose;
    /**
     * 数据来源
     */
     private  String dataSource;

    /**
     * 取得 ????????????????????????
     * @return ????????????????????????
     */
    public String getFinprodType() {
        return finprodType;
    }

    /**
     * 设置 ????????????????????????
     * @param finprodType ????????????????????????
     */
    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType == null ? null : finprodType.trim();
    }

    /**
     * 取得 ????????????????????????????????????????
     * @return ????????????????????????????????????????
     */
    public String getFinprodType2() {
        return finprodType2;
    }

    /**
     * 设置 ????????????????????????????????????????
     * @param finprodType2 ????????????????????????????????????????
     */
    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2 == null ? null : finprodType2.trim();
    }

    /**
     * 取得 ??????????????????
     * @return ??????????????????
     */
    public String getProfitType() {
        return profitType;
    }

    /**
     * 设置 ??????????????????
     * @param profitType ??????????????????
     */
    public void setProfitType(String profitType) {
        this.profitType = profitType == null ? null : profitType.trim();
    }

    /**
     * 取得 ????
     * @return ????
     */
    public String getBranchType() {
        return branchType;
    }

    /**
     * 设置 ????
     * @param branchType ????
     */
    public void setBranchType(String branchType) {
        this.branchType = branchType == null ? null : branchType.trim();
    }

    /**
     * 取得 ????
     * @return ????
     */
    public String getChlAgrtId() {
        return chlAgrtId;
    }

    /**
     * 设置 ????
     * @param chlAgrtId ????
     */
    public void setChlAgrtId(String chlAgrtId) {
        this.chlAgrtId = chlAgrtId == null ? null : chlAgrtId.trim();
    }

    /**
     * 取得 ????
     * @return ????
     */
    public BigDecimal getPrin() {
        return prin;
    }

    /**
     * 设置 ????
     * @param prin ????
     */
    public void setPrin(BigDecimal prin) {
        this.prin = prin;
    }

    /**
     * 取得 ????
     * @return ????
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * 设置 ????
     * @param ccy ????
     */
    public void setCcy(String ccy) {
        this.ccy = ccy == null ? null : ccy.trim();
    }

    /**
     * 取得 ???
     * @return ???
     */
    public Date getVdate() {
        return vdate;
    }

    /**
     * 设置 ???
     * @param vdate ???
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    /**
     * 取得 ???
     * @return ???
     */
    public Date getMdate() {
        return mdate;
    }

    /**
     * 设置 ???
     * @param mdate ???
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    /**
     * 取得 ??(?)
     * @return ??(?)
     */
    public Long getTerm() {
        return term;
    }

    /**
     * 设置 ??(?)
     * @param term ??(?)
     */
    public void setTerm(Long term) {
        this.term = term;
    }

    /**
     * 取得 ????
     * @return ????
     */
    public String getIntType() {
        return intType;
    }

    /**
     * 设置 ????
     * @param intType ????
     */
    public void setIntType(String intType) {
        this.intType = intType == null ? null : intType.trim();
    }

    /**
     * 取得 ????
     * @return ????
     */
    public BigDecimal getIntRate() {
        return intRate;
    }

    /**
     * 设置 ????
     * @param intRate ????
     */
    public void setIntRate(BigDecimal intRate) {
        this.intRate = intRate;
    }

    /**
     * 取得 ????????
     * @return ????????
     */
    public String getIntRateId() {
        return intRateId;
    }

    /**
     * 设置 ????????
     * @param intRateId ????????
     */
    public void setIntRateId(String intRateId) {
        this.intRateId = intRateId == null ? null : intRateId.trim();
    }

    /**
     * 取得 ????
     * @return ????
     */
    public String getBasis() {
        return basis;
    }

    /**
     * 设置 ????
     * @param basis ????
     */
    public void setBasis(String basis) {
        this.basis = basis == null ? null : basis.trim();
    }

    /**
     * 取得 ????
     * @return ????
     */
    public BigDecimal getmPrinAmt() {
        return mPrinAmt;
    }

    /**
     * 设置 ????
     * @param mPrinAmt ????
     */
    public void setmPrinAmt(BigDecimal mPrinAmt) {
        this.mPrinAmt = mPrinAmt;
    }

    /**
     * 取得 ????
     * @return ????
     */
    public BigDecimal getmIntAmt() {
        return mIntAmt;
    }

    /**
     * 设置 ????
     * @param mIntAmt ????
     */
    public void setmIntAmt(BigDecimal mIntAmt) {
        this.mIntAmt = mIntAmt;
    }

    /**
     * 取得 ????
     * @return ????
     */
    public BigDecimal getmTradeAmt() {
        return mTradeAmt;
    }

    /**
     * 设置 ????
     * @param mTradeAmt ????
     */
    public void setmTradeAmt(BigDecimal mTradeAmt) {
        this.mTradeAmt = mTradeAmt;
    }

    /**
     * 取得 ?????????????
     * @return ?????????????
     */
    public String getCapiIncomeFeature() {
        return capiIncomeFeature;
    }

    /**
     * 设置 ?????????????
     * @param capiIncomeFeature ?????????????
     */
    public void setCapiIncomeFeature(String capiIncomeFeature) {
        this.capiIncomeFeature = capiIncomeFeature == null ? null : capiIncomeFeature.trim();
    }

    /**
     * 取得 ????????????????
     * @return ????????????????
     */
    public String getoFinprodId() {
        return oFinprodId;
    }

    /**
     * 设置 ????????????????
     * @param oFinprodId ????????????????
     */
    public void setoFinprodId(String oFinprodId) {
        this.oFinprodId = oFinprodId == null ? null : oFinprodId.trim();
    }

    /**
     * 取得 ????
     * @return ????
     */
    public String getTradeMarket() {
        return tradeMarket;
    }

    /**
     * 设置 ????
     * @param tradeMarket ????
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket == null ? null : tradeMarket.trim();
    }

    /**
     * 取得 ????
     * @return ????
     */
    public String getCalendarId() {
        return calendarId;
    }

    /**
     * 设置 ????
     * @param calendarId ????
     */
    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId == null ? null : calendarId.trim();
    }

    /**
     * 取得 ????
     * @return ????
     */
    public String getTermType() {
        return termType;
    }

    /**
     * 设置 ????
     * @param termType ????
     */
    public void setTermType(String termType) {
        this.termType = termType == null ? null : termType.trim();
    }

    /**
     * 取得 ????
     * @return ????
     */
    public String getCounterId() {
        return counterId;
    }

    /**
     * 设置 ????
     * @param counterId ????
     */
    public void setCounterId(String counterId) {
        this.counterId = counterId == null ? null : counterId.trim();
    }

    /**
     * 取得 null
     * @return null
     */
    public BigDecimal getmUnitCprice() {
        return mUnitCprice;
    }

    /**
     * 设置 null
     * @param mUnitCprice null
     */
    public void setmUnitCprice(BigDecimal mUnitCprice) {
        this.mUnitCprice = mUnitCprice;
    }

    /**
     * 取得 null
     * @return null
     */
    public BigDecimal getmUnitInt() {
        return mUnitInt;
    }

    /**
     * 设置 null
     * @param mUnitInt null
     */
    public void setmUnitInt(BigDecimal mUnitInt) {
        this.mUnitInt = mUnitInt;
    }

    /**
     * 取得 null
     * @return null
     */
    public BigDecimal getmUnitFprice() {
        return mUnitFprice;
    }

    /**
     * 设置 null
     * @param mUnitFprice null
     */
    public void setmUnitFprice(BigDecimal mUnitFprice) {
        this.mUnitFprice = mUnitFprice;
    }

    /**
     * 取得 null
     * @return null
     */
    public BigDecimal getmYield() {
        return mYield;
    }

    /**
     * 设置 null
     * @param mYield null
     */
    public void setmYield(BigDecimal mYield) {
        this.mYield = mYield;
    }

    /**
     * 取得 null
     * @return null
     */
    public String getmDeliveryType() {
        return mDeliveryType;
    }

    /**
     * 设置 null
     * @param mDeliveryType null
     */
    public void setmDeliveryType(String mDeliveryType) {
        this.mDeliveryType = mDeliveryType == null ? null : mDeliveryType.trim();
    }

    /**
     * 取得 ?????
     * @return ?????
     */
    public Date getVpayDate() {
        return vpayDate;
    }

    /**
     * 设置 ?????
     * @param vpayDate ?????
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setVpayDate(Date vpayDate) {
        this.vpayDate = vpayDate;
    }

    /**
     * 取得 ?????
     * @return ?????
     */
    public Date getMpayDate() {
        return mpayDate;
    }

    /**
     * 设置 ?????
     * @param mpayDate ?????
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setMpayDate(Date mpayDate) {
        this.mpayDate = mpayDate;
    }

    /**
     * 取得 ????
     * @return ????
     */
    public String getContractNo() {
        return contractNo;
    }

    /**
     * 设置 ????
     * @param contractNo ????
     */
    public void setContractNo(String contractNo) {
        this.contractNo = contractNo == null ? null : contractNo.trim();
    }

    /**
     * 取得 ??????
     * @return ??????
     */
    public Long getActCapDays() {
        return actCapDays;
    }

    /**
     * 设置 ??????
     * @param actCapDays ??????
     */
    public void setActCapDays(Long actCapDays) {
        this.actCapDays = actCapDays;
    }

    /**
     * 取得 ????
     * @return ????
     */
    public String getIrsType() {
        return irsType;
    }

    /**
     * 设置 ????
     * @param irsType ????
     */
    public void setIrsType(String irsType) {
        this.irsType = irsType == null ? null : irsType.trim();
    }

    /**
     * 取得 ??????
     * @return ??????
     */
    public String getExcPs() {
        return excPs;
    }

    /**
     * 设置 ??????
     * @param excPs ??????
     */
    public void setExcPs(String excPs) {
        this.excPs = excPs == null ? null : excPs.trim();
    }

    /**
     * 取得 ???
     * @return ???
     */
    public String getCurPair() {
        return curPair;
    }

    /**
     * 设置 ???
     * @param curPair ???
     */
    public void setCurPair(String curPair) {
        this.curPair = curPair == null ? null : curPair.trim();
    }

    /**
     * 取得 ??????
     * @return ??????
     */
    public String getExcFxsTermType() {
        return excFxsTermType;
    }

    /**
     * 设置 ??????
     * @param excFxsTermType ??????
     */
    public void setExcFxsTermType(String excFxsTermType) {
        this.excFxsTermType = excFxsTermType == null ? null : excFxsTermType.trim();
    }

    /**
     * 取得 ???????
     * @return ???????
     */
    public BigDecimal getUsdAmt() {
        return usdAmt;
    }

    /**
     * 设置 ???????
     * @param usdAmt ???????
     */
    public void setUsdAmt(BigDecimal usdAmt) {
        this.usdAmt = usdAmt;
    }

    /**
     * 取得 ????????
     * @return ????????
     */
    public String getDifSettleRefRate() {
        return difSettleRefRate;
    }

    /**
     * 设置 ????????
     * @param difSettleRefRate ????????
     */
    public void setDifSettleRefRate(String difSettleRefRate) {
        this.difSettleRefRate = difSettleRefRate == null ? null : difSettleRefRate.trim();
    }

    /**
     * 取得 ??????
     * @return ??????
     */
    public String getConflictSolveWay() {
        return conflictSolveWay;
    }

    /**
     * 设置 ??????
     * @param conflictSolveWay ??????
     */
    public void setConflictSolveWay(String conflictSolveWay) {
        this.conflictSolveWay = conflictSolveWay == null ? null : conflictSolveWay.trim();
    }

    /**
     * 取得 期次代码
     * @return 期次代码
     */
    public String getPeriodId() {
        return periodId;
    }

    /**
     * 设置 期次代码
     * @param periodId 期次代码
     */
    public void setPeriodId(String periodId) {
        this.periodId = periodId == null ? null : periodId.trim();
    }

    /**
     * 取得 截止日是否冲回
     * @return 截止日是否冲回
     */
    public String getIsRushBack() {
        return isRushBack;
    }

    /**
     * 设置 截止日是否冲回
     * @param isRushBack 截止日是否冲回
     */
    public void setIsRushBack(String isRushBack) {
        this.isRushBack = isRushBack == null ? null : isRushBack.trim();
    }

    /**
     * 取得 合同名称
     * @return 合同名称
     */
    public String getContractName() {
        return contractName;
    }

    /**
     * 设置 合同名称
     * @param contractName 合同名称
     */
    public void setContractName(String contractName) {
        this.contractName = contractName == null ? null : contractName.trim();
    }

	/**
	 * @return the holdPurpose
	 */
	public String getHoldPurpose() {
		return holdPurpose;
	}

	/**
	 * @param holdPurpose the holdPurpose to set
	 */
	public void setHoldPurpose(String holdPurpose) {
		this.holdPurpose = holdPurpose;
	}

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}