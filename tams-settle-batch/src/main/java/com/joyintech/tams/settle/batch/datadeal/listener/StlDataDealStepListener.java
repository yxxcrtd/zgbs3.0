package com.joyintech.tams.settle.batch.datadeal.listener;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.SettleBatchResultMapper;
import com.joyintech.tams.common.base.entity.SettleBatchPara;
import com.joyintech.tams.common.base.entity.SettleBatchResultWithBLOBs;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datadeal.dto.step2.StepContext;
import com.joyintech.tams.settle.batch.datadeal.service.StlDownDataDealReaderService;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author 何阳阳
 * @date 2020/8/10
 * @see StlDataDealStepListener
 * @since 1.0
 */
@Component
public class StlDataDealStepListener implements StepExecutionListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SettleBatchResultMapper settleBatchResultMapper;

    @Autowired
    private StlDownDataDealReaderService stlDownDataDealService;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.debug("[{}]开始执行", stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.debug("[{}]执行结束，执行结果[{}]，执行耗时时间[{}毫秒]，读入数据量[{}]，成功数据量[{}]，失败数据量[{}]",
                stepExecution.getStepName(), stepExecution.getExitStatus().getExitCode(),
                System.currentTimeMillis() - stepExecution.getStartTime().getTime(),
                stepExecution.getReadCount(), stepExecution.getWriteCount(), stepExecution.getRollbackCount());
        saveLog(stepExecution);
        return stepExecution.getExitStatus();
    }

    private void saveLog(StepExecution stepExecution) {

        List<SettleBatchPara> list = stlDownDataDealService.selectAllParam(stepExecution.getJobParameters().getDate("date"));

        try {
            if(CollectionUtils.isNotEmpty(list)) {
                list.stream().collect(JoyinCollectors.groupingBy(SettleBatchPara::getSecType)).forEach((key, value) -> {
                    saveLogBySecType(stepExecution, value.get(0).getSeqno(), key);
                });
            }
        }
        finally {
            StepContext.destroy();
        }
    }

    private void saveLogBySecType(StepExecution stepExecution, String seqNo, String secType) {
        ExitStatus exitStatus = stepExecution.getExitStatus();
        StepContext context = StepContext.get(secType);
        if(null == context) return;
        SettleBatchResultWithBLOBs batchResult = new SettleBatchResultWithBLOBs();
        batchResult.setSeqno(seqNo);
        batchResult.setDealDate(stepExecution.getJobParameters().getDate("date"));
        batchResult.setSecType(secType);
        if(CollectionUtils.isNotEmpty(context.getPortfolioId())) {
            batchResult.setPortfolioId(StringUtil.join(context.getPortfolioId().toArray(), GlobalConstant.COMMA));
        }
        if(context.getStartTime() != null) {
            batchResult.setStartTime(context.getStartTime());
        }
        else {
            batchResult.setStartTime(stepExecution.getStartTime());
        }
        if(context.getEndTime() != null) {
            batchResult.setEndTime(context.getEndTime());
        }
        else if(null != stepExecution.getEndTime()){
            batchResult.setEndTime(stepExecution.getEndTime());
        }
        else {
            batchResult.setEndTime(new Date());
        }
        if(ExitStatus.FAILED.equals(exitStatus) && (stepExecution.getReadCount() > 0 && stepExecution.getWriteCount() == 0)) {
            batchResult.setTaskResult(DictCodeListStl.批处理执行结果_异常结束.getValue());
            batchResult.setFailureMsg("数据清算批处理异常结束");
        }
        else if(ExitStatus.COMPLETED.equals(exitStatus) && (stepExecution.getReadCount() == stepExecution.getWriteCount())) {
            batchResult.setTaskResult(DictCodeListStl.批处理执行结果_正常结束.getValue());
            batchResult.setSuccessMsg("数据清算批处理正常结束，读取" + stepExecution.getReadCount() + "条，成功"+stepExecution.getWriteCount()+"条");
        }
        else {
            batchResult.setTaskResult(DictCodeListStl.批处理执行结果_警告.getValue());
            batchResult.setFailureMsg("数据清算批处理出现警告，读取" + stepExecution.getReadCount() + "条，成功"+stepExecution.getWriteCount()+"条");
        }
        settleBatchResultMapper.insertSelective(batchResult);
    }
}
