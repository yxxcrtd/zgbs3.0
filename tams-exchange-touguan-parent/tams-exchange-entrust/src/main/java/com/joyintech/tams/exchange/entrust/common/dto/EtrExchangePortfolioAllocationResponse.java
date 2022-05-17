/*
 * 文件名：EtrExchangeTradeDto001.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 *  交易信息 （左边下）
 * @author meifaxin
 * @version 1.0 2022年5月13日
 * @see EtrExchangePortfolioAllocationResponse
 * @since 1.0
 */
@ApiModel(value="组合分配",description="组合分配")
public class EtrExchangePortfolioAllocationResponse {
    @ApiModelProperty(required = true, name = "targetType", value = "目标类型")
    private String  targetType;
    @ApiModelProperty(required = true, name = "minNum", value = "最低交易数量")
    private BigDecimal  minNum;
    @ApiModelProperty(required = true, name = "minStep", value = "最小交易单元")
    private BigDecimal minStep;
    @ApiModelProperty(name="portfolioIdList",value="组合代码集合",example="[对象1,对象1,...]", required = true)
    List<EtrExchangeProtfolioDetailRequest> portfolioList;

    public String getTargetType() {
        return targetType;
    }

    public void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public BigDecimal getMinNum() {
        return minNum;
    }

    public void setMinNum(BigDecimal minNum) {
        this.minNum = minNum;
    }

    public BigDecimal getMinStep() {
        return minStep;
    }

    public void setMinStep(BigDecimal minStep) {
        this.minStep = minStep;
    }

    public List<EtrExchangeProtfolioDetailRequest> getPortfolioList() {
        return portfolioList;
    }

    public void setPortfolioList(List<EtrExchangeProtfolioDetailRequest> portfolioList) {
        this.portfolioList = portfolioList;
    }
}
