/*
 * 文件名：EtribRepoInfo003Dto.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2021年01月20日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.cfets.etr.repo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="组合树查询",description="组合树查询")
public class CfetsTreePortfolioResponse{

    /**
     * 组合代码
     */
	@ApiModelProperty(name="portfolioId",value="组合代码")
    private String portfolioId;
	
	/**
     * 组合名称
     */
	@ApiModelProperty(name="portfolioName",value="组合名称")
    private String portfolioName;


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
}
