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
 * @see EtrExchangePortfolioAllocationRequest
 * @since 1.0
 */
@ApiModel(value="组合分配",description="组合分配")
public class EtrExchangePortfolioAllocationRequest  {
    @ApiModelProperty(required = true, name = "targetType", value = "目标类型")
    private String  targetType;
    @ApiModelProperty(required = true, name = "minNum", value = "最低交易数量")
    private BigDecimal  minNum;
    @ApiModelProperty(required = true, name = "minStep", value = "最小交易单元")
    private BigDecimal minStep;
    @ApiModelProperty(name="price",value="净价",example="")
    private BigDecimal price;

    @ApiModelProperty(name="declaType",value="全价价格",example="")
    private BigDecimal fprice;

    @ApiModelProperty(name="yield",value="收益率",example="")
    private BigDecimal yield;

    @ApiModelProperty(name="entrustPrice",value="指令价格",example="")
    private BigDecimal entrustPrice;
    @ApiModelProperty(name="entrustAmt",value="指令金额",example="12")
    private BigDecimal entrustAmt=BigDecimal.ZERO;

    @ApiModelProperty(name="entrustNum",value="指令数量",example="12")
    private BigDecimal entrustNum=BigDecimal.ZERO;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getEntrustAmt() {
        return entrustAmt;
    }

    public void setEntrustAmt(BigDecimal entrustAmt) {
        this.entrustAmt = entrustAmt;
    }

    public BigDecimal getEntrustNum() {
        return entrustNum;
    }

    public void setEntrustNum(BigDecimal entrustNum) {
        this.entrustNum = entrustNum;
    }

    public BigDecimal getFprice() {
        return fprice;
    }

    public void setFprice(BigDecimal fprice) {
        this.fprice = fprice;
    }

    public BigDecimal getYield() {
        return yield;
    }

    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

    public BigDecimal getEntrustPrice() {
        return entrustPrice;
    }

    public void setEntrustPrice(BigDecimal entrustPrice) {
        this.entrustPrice = entrustPrice;
    }
}
