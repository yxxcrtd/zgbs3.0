package com.joyintech.tams.invest.period.trdseclendduration.dto;

import java.math.BigDecimal;

public class TrdSecLendCashPositionDto {
    private String portfolioId;//组合代码
    private String tradeId;//业务编号
    private String etrId;//指令编号
    private String cashAcctId;//资金账号
    private BigDecimal cashAmount;//交易金额

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

    public String getEtrId() {
        return etrId;
    }

    public void setEtrId(String etrId) {
        this.etrId = etrId;
    }

    public String getCashAcctId() {
        return cashAcctId;
    }

    public void setCashAcctId(String cashAcctId) {
        this.cashAcctId = cashAcctId;
    }

    public BigDecimal getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(BigDecimal cashAmount) {
        this.cashAmount = cashAmount;
    }
}
