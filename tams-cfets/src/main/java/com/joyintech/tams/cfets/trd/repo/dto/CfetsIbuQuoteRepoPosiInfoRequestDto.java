package com.joyintech.tams.cfets.trd.repo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 头寸信息查询，请求参数
 */
@ApiModel(value="本币业务交易-质押式回购头寸信息查询请求参数",description="本币业务交易-质押式回购头寸信息查询请求参数")
public class CfetsIbuQuoteRepoPosiInfoRequestDto {

    @ApiModelProperty(name="portfolioIdList",value="组合代码集合",example="['A','B']", required = true)
    private List<String> portfolioIdList;

    @ApiModelProperty(name="cdate",value="交易日期",example="2021-07-08", required = true)
    private String cdate;

    public List<String> getPortfolioIdList() {
        return portfolioIdList;
    }

    public void setPortfolioIdList(List<String> portfolioIdList) {
        this.portfolioIdList = portfolioIdList;
    }

    public String getCdate() {
        return cdate;
    }

    public void setCdate(String cdate) {
        this.cdate = cdate;
    }

}
