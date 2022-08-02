/*
 * 文件名：BondAutoDurDealGenerateTask.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：etliu
 * 修改时间：2018年12月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.creditoccup.runner;

import com.joyintech.batch.batchbase.baseclass.*;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.invest.batch.common.util.BatchConstant;
import com.joyintech.tams.invest.batch.creditoccup.processor.CreditOccupFirstProcessor;
import com.joyintech.tams.invest.batch.creditoccup.processor.CreditOccupProcessor;
import com.joyintech.tams.invest.batch.creditoccup.processor.CreditOccupSzProcessor;
import com.joyintech.tams.invest.batch.creditoccup.reader.CreditOccupFirstReader;
import com.joyintech.tams.invest.batch.creditoccup.reader.CreditOccupReader;
import com.joyintech.tams.invest.batch.creditoccup.reader.CreditOccupSzReader;
import com.joyintech.tams.invest.batch.creditoccup.writer.CreditOccupFirstWriter;
import com.joyintech.tams.invest.batch.creditoccup.writer.CreditOccupSzWriter;
import com.joyintech.tams.invest.batch.creditoccup.writer.CreditOccupWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 授信日终
 */
@Service("creditOccupTask")
@Scope("prototype")
public class CreditOccupTask extends BatchBaseService {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * BatchBuilder
     */
    @Autowired
    private BatchBuilder<BaseReader<NeedOperateEntity>, BaseProcessor<NeedOperateEntity, NeedToWriteEntity>, BaseWriter<NeedToWriteEntity>> batchBuilder;

    @Autowired
    private CreditOccupReader step1Reader;

    @Autowired
    private CreditOccupProcessor step1Processor;

    @Autowired
    private CreditOccupWriter step1Writer;

    @Autowired
    private CreditOccupSzReader step2Reader;

    @Autowired
    private CreditOccupSzProcessor step2Processor;

    @Autowired
    private CreditOccupSzWriter step2Writer;


    @Autowired
    private CreditOccupFirstReader step3Reader;

    @Autowired
    private CreditOccupFirstProcessor step3Processor;

    @Autowired
    private CreditOccupFirstWriter step3Writer;



    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public JobExecution execute() throws BatchException {
        SysBatchExecInfo batchExecuteInfo = this.getBatchExecuteInfo();
        if (batchExecuteInfo == null) {
            logger.info("无启动参数，批处理退出。");
            return null;
        }
        Map<String, Object> params = new HashMap<>();
        params.put(BatchConstant.BATCH_INFO, batchExecuteInfo);
        params.put("executeDate", DateUtil.format(batchExecuteInfo.getExecuteDate()));
        // 各步骤设置参数
        step1Reader.setParams(params);
        step1Processor.setParams(params);
        step1Writer.setParams(params);
        step2Reader.setParams(params);
        step2Processor.setParams(params);
        step3Reader.setParams(params);
        step3Processor.setParams(params);
        return batchBuilder.init(this).batch("step1", (BaseReader) step1Reader,
                (BaseProcessor) step1Processor, (BaseWriter) step1Writer)
                .batch("step2", (BaseReader) step2Reader,
                        (BaseProcessor) step2Processor, step2Writer)
                .batch("step3", (BaseReader) step3Reader,
                        (BaseProcessor) step3Processor, step3Writer)
                .builder(
                        this.getBatchTask().getBeanName() + this.getBatchTask().getTaskId()).run();

    }

}
