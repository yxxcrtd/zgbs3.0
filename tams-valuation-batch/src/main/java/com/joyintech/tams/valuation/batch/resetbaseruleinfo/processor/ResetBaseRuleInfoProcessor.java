/*
 * 文件名：ResetBaseRuleInfoProcessor.java
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
import com.joyintech.tams.valuation.batch.resetbaseruleinfo.dto.ResetBaseRuleInfoReaderResult;
import com.joyintech.tams.valuation.batch.resetbaseruleinfo.dto.ResetBaseRuleInfoResult;
import com.joyintech.tams.valuation.batch.resetbaseruleinfo.service.ResetBaseRuleInfoProcessorService;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Processor
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月16日
 * @see ResetBaseRuleInfoProcessor
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ResetBaseRuleInfoProcessor extends BaseProcessor<NeedOperateEntity, ResetBaseRuleInfoResult>{

    @Autowired
    private ResetBaseRuleInfoProcessorService processorService;
    /**
     * {@inheritDoc}
     */
    @Override
    public ResetBaseRuleInfoResult process(NeedOperateEntity item)
        throws Exception {
        // 获取reader方法的返回值
        ResetBaseRuleInfoReaderResult readerResult = (ResetBaseRuleInfoReaderResult) item;

        Map<String, Object> thisParams = new HashMap<>();
        thisParams.putAll(this.getParams());
        // 将返回值塞入参数中
        thisParams.put(GlobalConstant.READER_RESULT, readerResult);

        // 调用service方法进行逻辑逻辑
        ResetBaseRuleInfoResult result = processorService.execute(thisParams);

        // 当为null时候，给予初始化
        if (result == null) {
            result = new ResetBaseRuleInfoResult();
        }

        return result;
    }
    
}
