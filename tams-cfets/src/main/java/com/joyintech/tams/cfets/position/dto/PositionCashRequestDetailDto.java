package com.joyintech.tams.cfets.position.dto;

/**
 * 组合信息
 * @Author: kchen
 * @Date: 2022/6/8 17:01
 */
public class PositionCashRequestDetailDto {

    private String portfolioId; // 组合代码

    private String acctNo ; // 资金账号

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }
}
