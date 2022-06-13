/*
 * 文件名：ResetRateResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.batch.resetrate.dto;

import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.tams.valuation.common.asset.dto.RateAdjDto;

/**
 * NeedToWriteEntity
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月21日
 * @see ResetRateResult
 * @since 1.0
 */
public class ResetRateResult extends NeedToWriteEntity{

    /**
     * 将要登录的数据对象
     */
    private RateAdjDto rateAdjDto;

    
    /**
     * 获取 rateAdjDto
     * @return rateAdjDto.
     */
    public RateAdjDto getRateAdjDto() {
        return rateAdjDto;
    }

    
    /**
     * 设置 rateAdjDto
     * @param rateAdjDto rateAdjDto
     */
    public void setRateAdjDto(RateAdjDto rateAdjDto) {
        this.rateAdjDto = rateAdjDto;
    }
    
    

    
}
