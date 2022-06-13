/*
 * 文件名：ResetBaseRuleInfoDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月16日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.batch.resetbaseruleinfo.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.common.base.entity.FinBaseRuleInfo;

import java.math.BigDecimal;

/**
 *  业绩基准重置
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月16日
 * @see ResetBaseRuleInfoDto
 * @since 1.0
 */
public class ResetBaseRuleInfoDto extends FinBaseRuleInfo{

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * 生效利率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal intRate;

    
    /**
     * 获取 intRate
     * @return intRate.
     */
    public BigDecimal getIntRate() {
        return intRate;
    }

    
    /**
     * 设置 intRate
     * @param intRate intRate
     */
    public void setIntRate(BigDecimal intRate) {
        this.intRate = intRate;
    }
    
    

}
