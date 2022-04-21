/*
 * 文件名：EtrBondBiddingDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2020年7月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.background.component.order.exchange.business.dto;

import com.joyintech.background.component.order.exchange.common.constant.DictCodeList;
import com.joyintech.background.component.order.exchange.common.dto.EtrSinglePortfolioDto;

import java.math.BigDecimal;

/**
 * 场内现券指令-债券
 *
 */
public class EtrSecRepoBondDto extends EtrSinglePortfolioDto {

    /**
     * 序列号
     */
    private static final long serialVersionUID = 4549314758171865811L;

    /**
     * 全价价格
     */
    private BigDecimal fullPrice;

    /**
     * 到期收益率
     */
    private BigDecimal matureYield;

    /**
     * 行权收益率
     */
    private BigDecimal exerYield;

    /**
     * 计算方式
     */
    private String calMode;

    /**
     * 计价方式
     */
    private String priceType;

    /**
     * 交易方式
     */
    private String dealMode;

    public String getDealMode() {
        return dealMode;
    }

    public void setDealMode(String dealMode) {
        this.dealMode = dealMode;
    }

    /**
     * Constructor
     */
    public EtrSecRepoBondDto() {
        super(DictCodeList.指令业务类型_债券竞价.getValue());
    }

    /**
     * 获取 全价价格
     * 
     * @return 全价价格.
     */
    public BigDecimal getFullPrice() {
        return fullPrice;
    }

    /**
     * 设置 全价价格
     * 
     * @param fullPrice 全价价格
     */
    public void setFullPrice(BigDecimal fullPrice) {
        this.fullPrice = fullPrice;
    }

    /**
     * 获取 到期收益率
     * 
     * @return 到期收益率.
     */
    public BigDecimal getMatureYield() {
        return matureYield;
    }

    /**
     * 设置 到期收益率
     * 
     * @param matureYield 到期收益率
     */
    public void setMatureYield(BigDecimal matureYield) {
        this.matureYield = matureYield;
    }

    /**
     * 获取 计算方式
     * 
     * @return 计算方式.
     */
    public String getCalMode() {
        return calMode;
    }

    /**
     * 设置 计算方式
     * 
     * @param calMode 计算方式
     */
    public void setCalMode(String calMode) {
        this.calMode = calMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getPrice() {
        return super.getPrice();
    }

    public BigDecimal getExerYield() {
        return exerYield;
    }

    public void setExerYield(BigDecimal exerYield) {
        this.exerYield = exerYield;
    }


    @Override
    public String getPriceType() {
        return priceType;
    }

    @Override
    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }
}
