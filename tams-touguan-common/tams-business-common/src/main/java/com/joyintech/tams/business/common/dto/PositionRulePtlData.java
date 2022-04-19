package com.joyintech.tams.business.common.dto;

import java.math.BigDecimal;

/**
 * 组合数据集
 */
public class PositionRulePtlData {

    private String portfolioId; // 组合代码

    private String tradeId    ; // 交易编号

    private String cashAcctId ; // 资金账号

    private String secAcctId  ; // 证券管理账户/通道代码

    private BigDecimal secAmount  ; // 券面总额

    private BigDecimal cashAmount ; // 交易金额

    private String etrId; // modelType为交易时 有指令编号需传
    
    private String etrComplete; // modelType为交易时 有指令编号需传，数据字典_是否

    private String invAim; // 组合投资目的
    
    private BigDecimal integrateSecAmount; // 融入券券面总额
    
    private BigDecimal ownSecAmount; // 本方券券面总额
    
    public String getEtrId() {
        return etrId;
    }

    public void setEtrId(String etrId) {
        this.etrId = etrId;
    }

    public String getEtrComplete() {
        return etrComplete;
    }

    public void setEtrComplete(String etrComplete) {
        this.etrComplete = etrComplete;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getCashAcctId() {
        return cashAcctId;
    }

    public void setCashAcctId(String cashAcctId) {
        this.cashAcctId = cashAcctId;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public BigDecimal getSecAmount() {
        return secAmount;
    }

    public void setSecAmount(BigDecimal secAmount) {
        this.secAmount = secAmount;
    }

    public BigDecimal getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(BigDecimal cashAmount) {
        this.cashAmount = cashAmount;
    }

    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    public BigDecimal getIntegrateSecAmount() {
        return integrateSecAmount;
    }

    public void setIntegrateSecAmount(BigDecimal integrateSecAmount) {
        this.integrateSecAmount = integrateSecAmount;
    }

    public BigDecimal getOwnSecAmount() {
        return ownSecAmount;
    }

    public void setOwnSecAmount(BigDecimal ownSecAmount) {
        this.ownSecAmount = ownSecAmount;
    }
}
