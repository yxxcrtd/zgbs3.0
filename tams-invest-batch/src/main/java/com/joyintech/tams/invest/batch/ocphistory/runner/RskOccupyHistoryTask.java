package com.joyintech.tams.invest.batch.ocphistory.runner;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.batch.batchbase.baseclass.BatchBaseService;
import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.web.PlatformDate;
import com.joyintech.tams.invest.batch.common.util.BatchConstant;
import com.joyintech.tams.invest.batch.ocphistory.service.RskOccupyHistoryService;

/**
 * 批处理历史化预占/实占信息
 * 
 * @author zhangrui1
 * @version 1.0 2020年11月5日
 * @see RskOccupyHistoryTask
 * @since 1.0
 **/
@Service(value = "rskOccupyHistoryTask")
@Scope(value="prototype")
public class RskOccupyHistoryTask extends BatchBaseService{

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * PlatformDate
     */
    @Autowired(required = false)
    private PlatformDate platformDateService;

    @Autowired(required = false)
    private RskOccupyHistoryService rskPreOccupyInfoProService;


    @Override
    public JobExecution execute() throws BatchException {
        JobExecution result = new JobExecution(1l);
        result.setStatus(BatchStatus.FAILED);
        SysBatchExecInfo batchExecuteInfo = this.getBatchExecuteInfo();
        if (batchExecuteInfo == null) {
            logger.info("无启动参数，批处理退出。");
            return null;
        }
        // 设置此task的序列任务公用的params参数
        Map<String, Object> params = new HashMap<>();

        params.put(BatchConstant.BATCH_INFO, batchExecuteInfo);
        params.put(BatchConstant.PLATE_DATE, platformDateService.getSysDate());
        rskPreOccupyInfoProService.execute(params);
        result.setStatus(BatchStatus.COMPLETED);
        return result;
    }
}
