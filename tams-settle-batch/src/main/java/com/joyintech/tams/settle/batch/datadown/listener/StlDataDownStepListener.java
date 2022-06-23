package com.joyintech.tams.settle.batch.datadown.listener;

import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.util.IDGenerator;
import com.joyintech.framework.util.JsonUtils;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.SettleTransResultMapper;
import com.joyintech.tams.common.base.entity.SettleTransResultWithBLOBs;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datadown.dto.LogDto;
import com.joyintech.tams.settle.batch.datadown.dto.StepContext;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import java.util.Collection;
import java.util.Date;

/**
 * @author 何阳阳
 * @date 2020/7/9
 * @see StlDataDownStepListener
 * @since 1.0
 */
public class StlDataDownStepListener implements StepExecutionListener {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void beforeStep(StepExecution stepExecution) {
        logger.info("[{}]开始执行", stepExecution.getStepName());
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        logger.info("[{}]执行结束，执行结果[{}]，执行耗时时间[{}毫秒]，读入数据量[{}]，成功数据量[{}]，失败数据量[{}]",
                stepExecution.getStepName(), stepExecution.getExitStatus().getExitCode(),
                System.currentTimeMillis() - stepExecution.getStartTime().getTime(),
                stepExecution.getReadCount(), stepExecution.getCommitCount(), stepExecution.getRollbackCount());
        saveLog(stepExecution);
        return stepExecution.getExitStatus();
    }

    private void saveLog(StepExecution stepExecution) {
        SettleTransResultMapper settleTransResultMapper = SpringContextUtil.getBean(SettleTransResultMapper.class);
        StepContext context = StepContext.remove(stepExecution.getStepName());
        if(null != context) {
            Collection<LogDto> logDtoList = context.getLogDtoMap().values();
            if(null != logDtoList && logDtoList.size() > 0) {
                for (LogDto logDto : logDtoList) {
                    SettleTransResultWithBLOBs result = createResult(stepExecution, context, logDto);
                    if(null != result) {
                        settleTransResultMapper.insert(result);
                    }
                }
            }
        }
    }

    private SettleTransResultWithBLOBs createResult(StepExecution stepExecution, StepContext context, LogDto logDto) {
        SettleTransResultWithBLOBs transResult = new SettleTransResultWithBLOBs();
        transResult.setSeqno(IDGenerator.generate());
        if(CollectionUtils.isNotEmpty(context.getPortfolioId())) {
            transResult.setPortfolioId(StringUtil.join(context.getPortfolioId().toArray(), GlobalConstant.COMMA));
        }
        transResult.setTaskId(context.getTaskId());
        transResult.setImportDate(context.getImportDate());
        transResult.setDataFileName(logDto.getDataFile());
        transResult.setStartTime(stepExecution.getStartTime());
        if(null != stepExecution.getEndTime()) {
            transResult.setEndTime(stepExecution.getEndTime());
        }
        else {
            transResult.setEndTime(new Date());
        }
        if(/*ExitStatus.COMPLETED.equals(exitStatus)*/logDto.getErrorCount() == 0 && logDto.getWarningCount() == 0) {
            transResult.setTaskResult(DictCodeListStl.批处理执行结果_正常结束.getValue());
            transResult.setSuccessMsg(String.format("共读入记录%d条，导入成功%d条，失败%d条", logDto.getTotalCount(), logDto.getTotalCount() - logDto.getErrorCount(), logDto.getErrorCount()));
        }
        else if(/*ExitStatus.FAILED.equals(exitStatus)*/logDto.getErrorCount() > 0) {
            transResult.setTaskResult(DictCodeListStl.批处理执行结果_异常结束.getValue());
            transResult.setFailureMsg(String.format("共读入记录%d条，导入成功%d条，失败%d条", logDto.getTotalCount(), logDto.getTotalCount() - logDto.getErrorCount(), logDto.getErrorCount()));
        }
        else if(logDto.getWarningCount() > 0) {
            transResult.setTaskResult(DictCodeListStl.批处理执行结果_警告.getValue());
            transResult.setSuccessMsg(String.format("共读入记录%d条，导入成功%d条，失败%d条", logDto.getTotalCount(), logDto.getTotalCount() - logDto.getErrorCount(), logDto.getErrorCount()));
            transResult.setWarningMsg(logDto.getTotalCount() == 0 ? "数据文件"+logDto.getDataFile()+"未读入" : "导入全部成功，但是出现了一些警告信息");
        }
        if(CollectionUtils.isNotEmpty(logDto.getData())) {
            transResult.setOriginalData(JsonUtils.object2Json(logDto.getData()));
        }
        return transResult;
    }
}
