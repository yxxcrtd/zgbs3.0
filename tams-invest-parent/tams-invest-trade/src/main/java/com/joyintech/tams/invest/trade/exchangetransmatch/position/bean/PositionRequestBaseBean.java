package com.joyintech.tams.invest.trade.exchangetransmatch.position.bean;

import java.math.BigDecimal;

/**
 * @author swwan
 * @date 2022/2/22 - 10:49
 */
public class PositionRequestBaseBean {

    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 证券代码
     */
    private String finprodId;

    /**
     * 证券管理账户
     */
    private String secAcctId;

    /**
     * 资金账号
     */
    private String cashAcctId;


    /**
     * 业务编号
     */
    private String tradeId;

    /**
     * 指令编号
     */
    private String etrId;

    /**
     * 交易数量
     */
    private BigDecimal secAmount;

    /**
     * 本方券券面
     */
    private BigDecimal ownSecAmount;

    /**
     * 交易金额
     */
    private BigDecimal cashAmount;

    /**
     * 融入券券面总额
     */
    private BigDecimal integrateSecAmount;


    public PositionRequestBaseBean() {
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

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getCashAcctId() {
        return cashAcctId;
    }

    public void setCashAcctId(String cashAcctId) {
        this.cashAcctId = cashAcctId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getEtrId() {
        return etrId;
    }

    public void setEtrId(String etrId) {
        this.etrId = etrId;
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

    public BigDecimal getOwnSecAmount() {
        return ownSecAmount;
    }

    public void setOwnSecAmount(BigDecimal ownSecAmount) {
        this.ownSecAmount = ownSecAmount;
    }

    public BigDecimal getIntegrateSecAmount() {
        return integrateSecAmount;
    }

    public void setIntegrateSecAmount(BigDecimal integrateSecAmount) {
        this.integrateSecAmount = integrateSecAmount;
    }
}
