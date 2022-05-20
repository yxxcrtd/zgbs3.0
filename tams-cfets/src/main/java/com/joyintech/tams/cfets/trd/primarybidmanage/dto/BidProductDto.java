package com.joyintech.tams.cfets.trd.primarybidmanage.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 投标产品列表查询返回数据
 *
 * @author wangyang1
 * @version 1.0 2022年5月5日
 * @see BidProductDto
 * @since 1.0
 */
@ApiModel(value="投标产品列表查询返回数据",description="投标产品列表查询返回数据")
public class BidProductDto {

    @ApiModelProperty(name="bidId",value="投标编号")
    private String bidId;

    @ApiModelProperty(name="entrustId",value="指令代码")
    private String entrustId;

    @ApiModelProperty(name="portfolioId",value="投标编号")
    private String portfolioId;

    @ApiModelProperty(name="totalBidPrinamt",value="汇总投标券面总额")
    private BigDecimal totalBidPrinamt;

    @ApiModelProperty(name="winPrinAmt",value="指令投标券面总额(万元)")
    private BigDecimal winPrinAmt;

    @ApiModelProperty(name="winPrice",value="投标标位")
    private BigDecimal winPrice;

    @ApiModelProperty(name="businessModel",value="业务模式")
    private BigDecimal businessModel;

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public BigDecimal getTotalBidPrinamt() {
        return totalBidPrinamt;
    }

    public void setTotalBidPrinamt(BigDecimal totalBidPrinamt) {
        this.totalBidPrinamt = totalBidPrinamt;
    }

    public BigDecimal getWinPrinAmt() {
        return winPrinAmt;
    }

    public void setWinPrinAmt(BigDecimal winPrinAmt) {
        this.winPrinAmt = winPrinAmt;
    }

    public BigDecimal getWinPrice() {
        return winPrice;
    }

    public void setWinPrice(BigDecimal winPrice) {
        this.winPrice = winPrice;
    }

    public BigDecimal getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(BigDecimal businessModel) {
        this.businessModel = businessModel;
    }
}
