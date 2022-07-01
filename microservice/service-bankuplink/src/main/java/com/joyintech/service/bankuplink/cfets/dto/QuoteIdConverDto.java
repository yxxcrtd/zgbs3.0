package com.joyintech.service.bankuplink.cfets.dto;

import com.joyintech.framework.base.BaseEntity;

import java.util.Date;

public class QuoteIdConverDto extends BaseEntity {
    private Date quoteDate;

    private String quoteId;

    private String clOrdId;

    public Date getQuoteDate() {
        return quoteDate;
    }

    public void setQuoteDate(Date quoteDate) {
        this.quoteDate = quoteDate;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public String getClOrdId() {
        return clOrdId;
    }

    public void setClOrdId(String clOrdId) {
        this.clOrdId = clOrdId;
    }
}
