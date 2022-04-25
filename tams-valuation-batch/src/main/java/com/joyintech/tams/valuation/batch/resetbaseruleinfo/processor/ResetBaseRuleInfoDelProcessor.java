/*
 * 文件名：ResetBaseRuleInfoDelProcessor.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月16日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.batch.resetbaseruleinfo.processor;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.tams.valuation.batch.resetbaseruleinfo.dto.ResetBaseRuleInfoResult;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * DelProcessor
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月16日
 * @see ResetBaseRuleInfoDelProcessor
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ResetBaseRuleInfoDelProcessor extends BaseProcessor<NeedOperateEntity, ResetBaseRuleInfoResult>{

    /**
     * {@inheritDoc}
     */
    @Override
    public ResetBaseRuleInfoResult process(NeedOperateEntity item)
        throws Exception {
        ResetBaseRuleInfoResult result = new ResetBaseRuleInfoResult();
        result.setProdId("业绩基准重置删除处理");
        return result;
    }
    
}
