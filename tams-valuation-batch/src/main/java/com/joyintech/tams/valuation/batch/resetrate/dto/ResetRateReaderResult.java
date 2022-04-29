/*
 * 文件名：ResetRateReaderResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.batch.resetrate.dto;

import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.tams.valuation.common.asset.dto.FinRateInfoDto;

/**
 *  reader结果
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月21日
 * @see ResetRateReaderResult
 * @since 1.0
 */
public class ResetRateReaderResult extends NeedOperateEntity{

    /**
     * 待处理数据dto
     */
    private FinRateInfoDto finRateInfoDto;

    
    /**
     * 获取 finRateInfoDto
     * @return finRateInfoDto.
     */
    public FinRateInfoDto getFinRateInfoDto() {
        return finRateInfoDto;
    }

    
    /**
     * 设置 finRateInfoDto
     * @param finRateInfoDto finRateInfoDto
     */
    public void setFinRateInfoDto(FinRateInfoDto finRateInfoDto) {
        this.finRateInfoDto = finRateInfoDto;
    }

    
    
    
    
}
