/*
 * 文件名：ResetBaseRuleInfoResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月16日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.batch.resetbaseruleinfo.dto;

import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;

/**
 * NeedToWriteEntity
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月16日
 * @see ResetBaseRuleInfoResult
 * @since 1.0
 */
public class ResetBaseRuleInfoResult extends NeedToWriteEntity{

    /**
     * 将要登录的数据对象
     */
    private MstIntRateInfoDto mstIntRateInfoDto;

    
    /**
     * 获取 mstIntRateInfoDto
     * @return mstIntRateInfoDto.
     */
    public MstIntRateInfoDto getMstIntRateInfoDto() {
        return mstIntRateInfoDto;
    }

    
    /**
     * 设置 mstIntRateInfoDto
     * @param mstIntRateInfoDto mstIntRateInfoDto
     */
    public void setMstIntRateInfoDto(MstIntRateInfoDto mstIntRateInfoDto) {
        this.mstIntRateInfoDto = mstIntRateInfoDto;
    }
    
}
