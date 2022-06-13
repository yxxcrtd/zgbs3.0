/*
 * 文件名：ResetBaseRuleInfoTask.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月16日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.batch.resetbaseruleinfo.runner;

import com.joyintech.batch.batchbase.baseclass.*;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.web.PlatformDate;
import com.joyintech.tams.valuation.batch.resetbaseruleinfo.processor.ResetBaseRuleInfoDelProcessor;
import com.joyintech.tams.valuation.batch.resetbaseruleinfo.processor.ResetBaseRuleInfoProcessor;
import com.joyintech.tams.valuation.batch.resetbaseruleinfo.reader.ResetBaseRuleInfoDelReader;
import com.joyintech.tams.valuation.batch.resetbaseruleinfo.reader.ResetBaseRuleInfoReader;
import com.joyintech.tams.valuation.batch.resetbaseruleinfo.writer.ResetBaseRuleInfoDelWriter;
import com.joyintech.tams.valuation.batch.resetbaseruleinfo.writer.ResetBaseRuleInfoWriter;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 *  业绩基准重置
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年2月16日
 * @see ResetBaseRuleInfoTask
 * @since 1.0
 */
@Service("resetBaseRuleInfoTask")
@Scope("prototype")
public class ResetBaseRuleInfoTask extends BatchBaseService{

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * BatchBuilder
     */
    @Autowired
    private BatchBuilder<BaseReader<NeedOperateEntity>, BaseProcessor<NeedOperateEntity, NeedToWriteEntity>, BaseWriter<NeedToWriteEntity>> batchBuilder;

    /**
     * PlatformDate
     */
    @Autowired(required = false)
    private PlatformDate platformDateService;

    /**
     * ResetBaseRuleInfoDelReader
     */
    @Autowired
    private ResetBaseRuleInfoDelReader step1Reader;

    /**
     * ResetBaseRuleInfoDelProcessor
     */
    @Autowired
    private ResetBaseRuleInfoDelProcessor step1Processor;

    /**
     * ResetBaseRuleInfoDelWriter
     */
    @Autowired
    private ResetBaseRuleInfoDelWriter step1Writer;

    /**
     * ResetBaseRuleInfoReader
     */
    @Autowired
    private ResetBaseRuleInfoReader step2Reader;

    /**
     * ResetBaseRuleInfoProcessor
     */
    @Autowired
    private ResetBaseRuleInfoProcessor step2Processor;

    /**
     * ResetBaseRuleInfoWriter
     */
    @Autowired
    private ResetBaseRuleInfoWriter step2Writer;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public JobExecution execute()
        throws BatchException {

        SysBatchExecInfo batchExecuteInfo = this.getBatchExecuteInfo();
        if (batchExecuteInfo == null) {
            logger.info("无启动参数，批处理退出。");
            return null;
        }

        // 设置此task的序列任务公用的params参数
        Map<String, Object> params = new HashMap<>();

        params.put(GlobalConstant.BATCH_INFO, batchExecuteInfo);
        params.put(GlobalConstant.PLATE_DATE, platformDateService.getSysDate(batchExecuteInfo.getCreateUser()));

        // 各步骤设置参数
        step1Reader.setParams(params);
        step1Processor.setParams(params);
        step1Writer.setParams(params);
        step2Reader.setParams(params);
        step2Processor.setParams(params);
        step2Writer.setParams(params);

        return batchBuilder.init(this).batch("step1", (BaseReader)step1Reader,
            (BaseProcessor)step1Processor, (BaseWriter)step1Writer).batch("step2", (BaseReader)step2Reader,
                (BaseProcessor)step2Processor, (BaseWriter)step2Writer).builder(
                    this.getBatchTask().getBeanName() + this.getBatchTask().getTaskId()).run();
    }
    
}
