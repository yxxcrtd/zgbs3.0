package com.joyintech.tams.cfets.etr.repo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * 可质押券面总额查询
 */
@ApiModel(value="可质押券面总额查询入参",description="可质押券面总额查询入参")
public class CfetsEtrRepoPledgeableRequest {

    @ApiModelProperty(name="portfolioId",value="组合代码",example="cp001")
    private String portfolioId;

    @ApiModelProperty(name="pledgeStockId",value="质押券库id",example="'2'")
    private String pledgeStockId;

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getPledgeStockId() {
        return pledgeStockId;
    }

    public void setPledgeStockId(String pledgeStockId) {
        this.pledgeStockId = pledgeStockId;
    }
}
