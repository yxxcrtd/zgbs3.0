/*
 * 文件名：ActualTimeAssetDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Administrator
 * 修改时间：2019年10月15日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.portfolio.actualtimecapital.dto;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;

public class ActualTimeCapital001Dto extends BaseEntity {

    


    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 2862522622817801899L;

    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 组合名称
     */
    private String portfolioName;

    /**
     * 账号  
     */
    private String acctNo;

    /**
     * 账号名称  
     */
    private String acctName;

    /**
     * 币种  
     */
    private String ccy;

    /**
     * 账户类型
     */
    private String acctType;

    /**
     * 日期
     */
    private Date cdate;

    /**
     * 现金余额  
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal cashBalance;


    /**
     * 指令可用现金    
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal cashAvailable;

    /**
     * 交易可用现金  
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tradeCash;
    /**
     * 可取现金 
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal desirableCash;

    /**
     * 预增现金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal advanceCash;

    /**
     * 预减现金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal preReductionCash;

    /**
     * 手工调整 
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal adjustmentAmount;

    /**
     * 检索可取现金余额 
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal retrievalDesirableCash;

    
    public String getPortfolioId() {
        return portfolioId;
    }

    
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    
    public String getPortfolioName() {
        return portfolioName;
    }

    
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    
    public String getAcctNo() {
        return acctNo;
    }

    
    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    
    public String getAcctName() {
        return acctName;
    }

    
    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }

    
    public String getCcy() {
        return ccy;
    }

    
    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    
    public String getAcctType() {
        return acctType;
    }

    
    public void setAcctType(String acctType) {
        this.acctType = acctType;
    }

    public String getAcctTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.资金账户类型, this.getAcctType());
    }

    
    public Date getCdate() {
        return cdate;
    }

    
    public void setCdate(Date cdate) {
        this.cdate = cdate;
    }

    
    public BigDecimal getCashBalance() {
        return cashBalance;
    }

    
    public void setCashBalance(BigDecimal cashBalance) {
        this.cashBalance = cashBalance;
    }

    
    public BigDecimal getCashAvailable() {
        return cashAvailable;
    }

    
    public void setCashAvailable(BigDecimal cashAvailable) {
        this.cashAvailable = cashAvailable;
    }

    
    public BigDecimal getTradeCash() {
        return tradeCash;
    }

    
    public void setTradeCash(BigDecimal tradeCash) {
        this.tradeCash = tradeCash;
    }

    /**
     * 可取现金
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getDesirableCash() {
//        if(DictCodeListExt1.资金账户类型_券商资金账户.getValue().equals(this.getAcctType()) 
//            && BigDecimal.ZERO.compareTo(this.getRetrievalDesirableCash()) < 0) {
//            return this.getRetrievalDesirableCash();
//        }
//        return this.getTradeCash();
        return desirableCash;
    }

    
    public void setDesirableCash(BigDecimal desirableCash) {
        this.desirableCash = desirableCash;
    }

    
    public BigDecimal getAdvanceCash() {
        return advanceCash;
    }

    
    public void setAdvanceCash(BigDecimal advanceCash) {
        this.advanceCash = advanceCash;
    }

    /**
     * 预减现金
     * <功能详细描述>
     * @return
     * @see
     * @since 1.0
     */
    public BigDecimal getPreReductionCash() {
        return preReductionCash;
    }

    
    public void setPreReductionCash(BigDecimal preReductionCash) {
        this.preReductionCash = preReductionCash;
    }

    
    public BigDecimal getAdjustmentAmount() {
        return adjustmentAmount;
    }

    
    public void setAdjustmentAmount(BigDecimal adjustmentAmount) {
        this.adjustmentAmount = adjustmentAmount;
    }

    
    public BigDecimal getRetrievalDesirableCash() {
        return retrievalDesirableCash;
    }

    
    public void setRetrievalDesirableCash(BigDecimal retrievalDesirableCash) {
        this.retrievalDesirableCash = retrievalDesirableCash;
    }
    
    
}
