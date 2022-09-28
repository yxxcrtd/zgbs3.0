package com.joyintech.tams.invest.common.dto.position;

/**
 * 组合数据集
 */
public class PositionQueryDto {

    private String portfolioId; // 组合代码

    private String cashAcctId ; // 资金账号

    private String secAcctId  ; // 证券管理账户/通道代码 查询资产时必传


    private String finprodId ; // 查询资产时必传

    private String dateType="0"; // 处理速度

    private String positionType;
    
    private String flowType; // 01 指令现金 02 指令持仓 03 交易现金 04 交易持仓
    /**
     * 币种
     */
    private String ccy;

    private String AMOUNT;

    /**
     * 投管资产类型
     */
    private String finprodType2;

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }


    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public String getFlowType() {
        return flowType;
    }

    public void setFlowType(String flowType) {
        this.flowType = flowType;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getCashAcctId() {
        return cashAcctId;
    }

    public void setCashAcctId(String cashAcctId) {
        this.cashAcctId = cashAcctId;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getPositionType() {
        return positionType;
    }

    public void setPositionType(String positionType) {
        this.positionType = positionType;
    }


    public String getAMOUNT() {
        return AMOUNT;
    }

    public void setAMOUNT(String AMOUNT) {
        this.AMOUNT = AMOUNT;
    }
}
