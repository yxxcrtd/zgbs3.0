/*
 * 文件名：FinCashPlanDetailDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年10月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.common.asset.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.common.base.entity.FinCashPlan;

import java.math.BigDecimal;

/**
 *  付息计划明细
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2018年10月17日
 * @see FinCashPlanDetailDto
 * @since 1.0
 */
public class FinCashPlanDetailDto extends FinCashPlan{

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;
    /**
     * 利率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal yield;
    
    
    /**
     * 获取 yield
     * @return yield.
     */
    public BigDecimal getYield() {
        return yield;
    }
    
    /**
     * 设置 yield
     * @param yield yield
     */
    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }
}
