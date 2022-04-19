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

import java.util.List;

@ApiModel(value="组合树查询",description="组合树查询")
public class CfetsTreePortfolioRequest{

    /**
     * 质押库id
     */
	@ApiModelProperty(name="pledgeStockId",value="质押库id")
    private String pledgeStockId;

    /**
     * 组合代码list
     */
	@ApiModelProperty(name="portfolioIdList",value="组合代码list")
    private List<String> portfolioIdList;

    /**
     * 收益类型
     */
	@ApiModelProperty(name="profitTypeList",value="收益类型")
    private List<String> profitTypeList;

    /**
     * 开发类型
     */
	@ApiModelProperty(name="openTypeList",value="开发类型")
    private List<String> openTypeList;

    /**
     * 募集方式
     */
	@ApiModelProperty(name="raiseTypeList",value="募集方式")
    private List<String> raiseTypeList;
    
    /**
     * 投资性质
     */
	@ApiModelProperty(name="investNatureList",value="投资性质")
    private List<String> investNatureList;
    


	public String getPledgeStockId() {
		return pledgeStockId;
	}

	public void setPledgeStockId(String pledgeStockId) {
		this.pledgeStockId = pledgeStockId;
	}

	public List<String> getPortfolioIdList() {
		return portfolioIdList;
	}

	public void setPortfolioIdList(List<String> portfolioIdList) {
		this.portfolioIdList = portfolioIdList;
	}

	public List<String> getProfitTypeList() {
		return profitTypeList;
	}

	public void setProfitTypeList(List<String> profitTypeList) {
		this.profitTypeList = profitTypeList;
	}

	public List<String> getOpenTypeList() {
		return openTypeList;
	}

	public void setOpenTypeList(List<String> openTypeList) {
		this.openTypeList = openTypeList;
	}

	public List<String> getRaiseTypeList() {
		return raiseTypeList;
	}

	public void setRaiseTypeList(List<String> raiseTypeList) {
		this.raiseTypeList = raiseTypeList;
	}

	public List<String> getInvestNatureList() {
		return investNatureList;
	}

	public void setInvestNatureList(List<String> investNatureList) {
		this.investNatureList = investNatureList;
	}
}
