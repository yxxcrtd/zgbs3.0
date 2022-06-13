/*
 * 文件名：ResetBaseRuleInfoReaderResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月16日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.batch.resetbaseruleinfo.dto;

import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;

/**
 *  reader结果
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月16日
 * @see ResetBaseRuleInfoReaderResult
 * @since 1.0
 */
public class ResetBaseRuleInfoReaderResult extends NeedOperateEntity{

    /**
     * 待处理数据dto
     */
    private ResetBaseRuleInfoDto resetBaseRuleInfoDto;

    
    /**
     * 获取 resetBaseRuleInfoDto
     * @return resetBaseRuleInfoDto.
     */
    public ResetBaseRuleInfoDto getResetBaseRuleInfoDto() {
        return resetBaseRuleInfoDto;
    }

    
    /**
     * 设置 resetBaseRuleInfoDto
     * @param resetBaseRuleInfoDto resetBaseRuleInfoDto
     */
    public void setResetBaseRuleInfoDto(ResetBaseRuleInfoDto resetBaseRuleInfoDto) {
        this.resetBaseRuleInfoDto = resetBaseRuleInfoDto;
    }
    
    
}
