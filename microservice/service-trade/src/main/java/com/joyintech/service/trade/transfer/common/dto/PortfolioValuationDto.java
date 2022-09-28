package com.joyintech.service.trade.transfer.common.dto;

import java.util.Date;

/**
 * @author lifangpeng
 * @date 2022/8/20 11:02
 */
public class PortfolioValuationDto {
    /**
     * 日期
     */
    private Date cdate;

    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 对账状态
     */
    private int checkStatus;
    /**
     * 估值状态
     */
    private int valuationTableStatus;

    public Date getCdate() {
        return cdate;
    }

    public void setCdate(Date cdate) {
        this.cdate = cdate;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

    public int getValuationTableStatus() {
        return valuationTableStatus;
    }

    public void setValuationTableStatus(int valuationTableStatus) {
        this.valuationTableStatus = valuationTableStatus;
    }
}
