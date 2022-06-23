package com.joyintech.tams.invest.batch.overduesecpositionforward.runner;

import com.joyintech.batch.batchbase.baseclass.*;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.tams.invest.batch.common.util.BatchConstant;
import com.joyintech.tams.invest.batch.overduesecpositionforward.processor.OverdueSecPositionForwardDelProcessor;
import com.joyintech.tams.invest.batch.overduesecpositionforward.processor.OverdueSecPositionForwardProcessor;
import com.joyintech.tams.invest.batch.overduesecpositionforward.reader.OverdueSecPositionForwardDelReader;
import com.joyintech.tams.invest.batch.overduesecpositionforward.reader.OverdueSecPositionForwardReader;
import com.joyintech.tams.invest.batch.overduesecpositionforward.writer.OverdueSecPositionForwardDelWriter;
import com.joyintech.tams.invest.batch.overduesecpositionforward.writer.OverdueSecPositionForwardWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service("overdueSecPositionForwardTask")
@Scope("prototype")
public class OverdueSecPositionForwardTask extends BatchBaseService {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static Boolean isRunning = false;
    /**
     * BatchBuilder
     */
    @Autowired
    private BatchBuilder<BaseReader<NeedOperateEntity>, BaseProcessor<NeedOperateEntity, NeedToWriteEntity>, BaseWriter<NeedToWriteEntity>> batchBuilder;
    /**
     * 读
     */
    @Autowired
    private OverdueSecPositionForwardReader overdueSecPositionForwardReader;
    /**
     * 删除读
     */
    @Autowired
    private  OverdueSecPositionForwardDelReader overdueSecPositionForwardDelReader;
    /**
     * 执行
     */
    @Autowired
    private OverdueSecPositionForwardProcessor overdueSecPositionForwardProcessor;
    /**
     * 删除执行
     */
    @Autowired
    private OverdueSecPositionForwardDelProcessor overdueSecPositionForwardDelProcessor;
    /**
     * 写
     */
    @Autowired
    private OverdueSecPositionForwardWriter overdueSecPositionForwardWriter;
    /**
     * 删除写
     */
    @Autowired
    private OverdueSecPositionForwardDelWriter overdueSecPositionForwardDelWriter;
    @Override
    public JobExecution execute() throws BatchException {
        SysBatchExecInfo batchExecuteInfo = this.getBatchExecuteInfo();
        if (batchExecuteInfo == null) {
            logger.info("无启动参数，批处理退出。");
            return null;
        }

        // 设置此task的序列任务公用的params参数
        Map<String, Object> params = new HashMap<>();
        params.put(BatchConstant.BATCH_INFO, batchExecuteInfo);
//        组合代码
//        params.put("productIdList",getProductIdList());
//        把执行日期当作平台日来替换处理
        params.put(BatchConstant.PLATE_DATE, batchExecuteInfo.getExecuteDate());
        params.put("TASK_ID",batchExecuteInfo.getTaskId());
        // 各步骤设置参数
        overdueSecPositionForwardDelReader.setParams(params);
        overdueSecPositionForwardDelProcessor.setParams(params);
        overdueSecPositionForwardDelWriter.setParams(params);
        overdueSecPositionForwardReader.setParams(params);
        overdueSecPositionForwardProcessor.setParams(params);
        overdueSecPositionForwardWriter.setParams(params);

        return batchBuilder.init(this).batch("step1", (BaseReader)overdueSecPositionForwardDelReader,
                (BaseProcessor)overdueSecPositionForwardDelProcessor, (BaseWriter)overdueSecPositionForwardDelWriter).batch("step2",
                (BaseReader)overdueSecPositionForwardReader,(BaseProcessor)overdueSecPositionForwardProcessor, (BaseWriter)overdueSecPositionForwardWriter).builder(
                this.getBatchTask().getBeanName() + this.getBatchTask().getTaskId()).run();
    }
}
