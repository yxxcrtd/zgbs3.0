package com.joyintech.tams.cfets.manualsettle.dto;

import com.joyintech.tams.common.base.entity.TrdProductDeal;

import java.math.BigDecimal;

public class CfetsManualSettleDealInfo extends TrdProductDeal {

    /**
     * 投资组合代码
     */
    private String portfolioId;

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

    @Override
    public String getPortfolioId() {
        return portfolioId;
    }

    @Override
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public BigDecimal getOurShareAmt() {
        return ourShareAmt;
    }

    public void setOurShareAmt(BigDecimal ourShareAmt) {
        this.ourShareAmt = ourShareAmt;
    }

    public BigDecimal getLendinShareAmt() {
        return lendinShareAmt;
    }

    public void setLendinShareAmt(BigDecimal lendinShareAmt) {
        this.lendinShareAmt = lendinShareAmt;
    }
}
