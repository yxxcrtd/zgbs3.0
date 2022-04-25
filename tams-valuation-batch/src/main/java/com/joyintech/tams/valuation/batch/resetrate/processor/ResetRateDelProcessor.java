/*
 * 文件名：ResetRateDelProcessor.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.batch.resetrate.processor;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.tams.valuation.batch.resetrate.dto.ResetRateResult;

/**
 * DelProcessor
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月21日
 * @see ResetRateDelProcessor
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ResetRateDelProcessor extends BaseProcessor<NeedOperateEntity, ResetRateResult>{

    /**
     * {@inheritDoc}
     */
    @Override
    public ResetRateResult process(NeedOperateEntity item)
        throws Exception {
        ResetRateResult result = new ResetRateResult();
        result.setProdId("利率重置删除处理");
        return result;
    }
}
