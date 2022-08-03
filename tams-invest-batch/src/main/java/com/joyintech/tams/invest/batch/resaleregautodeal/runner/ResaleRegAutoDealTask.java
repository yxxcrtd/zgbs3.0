package com.joyintech.tams.invest.batch.resaleregautodeal.runner;

import com.joyintech.batch.batchbase.baseclass.BatchBaseService;
import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.tams.invest.batch.resaleregautodeal.service.ResaleRegAutoDealService;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 回售登记数据自动生成成交数据
 */
@Service("resaleRegAutoDealTask")
@Scope("prototype")
public class ResaleRegAutoDealTask extends BatchBaseService{

    /**
    * 日志
    */
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ResaleRegAutoDealService resaleRegAutoDealService;
    
    private static Boolean isRunning = false;
    private static final Object lock1 = new Object();
    
	private Date supDate;
	
    @Override
    public JobExecution execute()
        throws BatchException {
        JobExecution result = new JobExecution(1l);
        result.setStatus(BatchStatus.FAILED);
        SysBatchExecInfo batchExecuteInfo = this.getBatchExecuteInfo();
        synchronized (lock1) {
            if(isRunning) {
                while (isRunning) {
                    // Sonar Fix modify by huwei 220609
                    try {
                        lock1.wait(30000);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(),e);
                        Thread.currentThread().interrupt();
                    }
                }
                isRunning = true;
            }

            else {
                isRunning = true;
            }
        }
        if(null!=batchExecuteInfo&&null!=batchExecuteInfo.getExecuteDate()){
            supDate=batchExecuteInfo.getExecuteDate();
        }
        else{
            supDate = new Date();
        }
        try{
            resaleRegAutoDealService.run(supDate);
            result.setStatus(BatchStatus.COMPLETED);
        }catch (Exception e) {
            isRunning=false;
            //异常信息记录到批处理的日志详情
            throw new BatchException(RiskUtil.getLogString(e));
        }
        isRunning=false;
        return result;
    }
}
