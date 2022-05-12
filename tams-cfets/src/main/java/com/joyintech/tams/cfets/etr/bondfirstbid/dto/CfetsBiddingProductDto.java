package com.joyintech.tams.cfets.etr.bondfirstbid.dto;

import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 组合分配：投标产品列表Dto
 * @Author: kchen
 * @Date: 2022/4/19 14:15
 */
public class CfetsBiddingProductDto {

    /**
     * 组合ID
     */
    @ApiModelProperty(name="portfolioId",value="组合ID",example="z001")
    private String portfolioId;

    /**
     * 组合名称
     */
    @ApiModelProperty(name="portfolioName",value="组合名称",example="组合名称1")
    private String portfolioName;

    /**
     * 投标标位
     */
    @ApiModelProperty(name="bidPrice",value="投标标位",example="1")
    private BigDecimal bidPrice;

    /**
     * 投标券面总额
     */
    @ApiModelProperty(name="bidPrinAmt",value="投标券面总额",example="1")
    private BigDecimal bidPrinAmt;

    /**
     * 业务模式
     */
    @ApiModelProperty(name="businessModel",value="业务模式",example="A1")
    private String businessModel;

    /**
     * 组合汇总投标券面总额
     */
    @ApiModelProperty(name="portfolioTotalBidPrinAmt",value="组合汇总投标券面总额",example="1")
    private BigDecimal portfolioTotalBidPrinAmt;

    /**
     * 证券托管户
     */
    @ApiModelProperty(name="secManageAcctId",value="证券托管户",example="1")
    private String secAcctId;

    /**
     * 资金账户
     */
    @ApiModelProperty(name="acctNo",value="资金账户",example="A1", required = true)
    private String acctNo;

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

    public BigDecimal getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(BigDecimal bidPrice) {
        this.bidPrice = bidPrice;
    }

    public BigDecimal getBidPrinAmt() {
        return bidPrinAmt;
    }

    public void setBidPrinAmt(BigDecimal bidPrinAmt) {
        this.bidPrinAmt = bidPrinAmt;
    }

    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }

    public BigDecimal getPortfolioTotalBidPrinAmt() {
        return portfolioTotalBidPrinAmt;
    }

    public void setPortfolioTotalBidPrinAmt(BigDecimal portfolioTotalBidPrinAmt) {
        this.portfolioTotalBidPrinAmt = portfolioTotalBidPrinAmt;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }
}
