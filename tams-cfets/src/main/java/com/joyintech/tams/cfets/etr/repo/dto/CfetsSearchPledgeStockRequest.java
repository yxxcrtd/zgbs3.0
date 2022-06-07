package com.joyintech.tams.cfets.etr.repo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * 指令下拉框模糊查询
 */
@ApiModel(value="下拉框请求参数",description="下拉框请求参数")
public class CfetsSearchPledgeStockRequest {

    @ApiModelProperty(name="portfolioId",value="组合代码",example="wp")
    private String portfolioId;

    private String initValue;

    private String search;

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getInitValue() {
        return initValue;
    }

    public void setInitValue(String initValue) {
        this.initValue = initValue;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
