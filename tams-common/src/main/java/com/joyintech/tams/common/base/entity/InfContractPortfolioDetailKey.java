package com.joyintech.tams.common.base.entity;

import com.joyintech.framework.base.BaseEntity;

public class InfContractPortfolioDetailKey extends BaseEntity {
    /**
     * 序号
     */
    private String seqno;

    /**
     * 组合代码
     */
    private String portfolioId;

    public String getSeqno() {
        return seqno;
    }

    public void setSeqno(String seqno) {
        this.seqno = seqno == null ? null : seqno.trim();
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId == null ? null : portfolioId.trim();
    }
}