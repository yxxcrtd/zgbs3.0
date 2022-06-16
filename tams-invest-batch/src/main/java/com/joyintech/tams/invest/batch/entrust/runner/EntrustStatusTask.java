/*
 * 文件名：EntrustStatusTask.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月15日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.entrust.runner;

import com.joyintech.batch.batchbase.baseclass.BatchBaseService;
import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.tams.invest.batch.entrust.service.EntrustStatusService;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 更新关闭委托状态
 * 〈功能详细描述〉
 *
 * @author zhangnian
 * @version 1.0 2019年2月15日
 * @see EntrustStatusTask
 * @since 1.0
 */
@Service("entrustStatusTask")
@Scope("prototype")
public class EntrustStatusTask extends BatchBaseService {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private EntrustStatusService entrustStatusService;

    private static Boolean isRunning = false;
    private static final Object lock1 = new Object();

    @Override
    public JobExecution execute()
            throws BatchException {
        SysBatchExecInfo batchExecuteInfo = this.getBatchExecuteInfo();
        JobExecution result = new JobExecution(1L);
        result.setStatus(BatchStatus.FAILED);
        synchronized (lock1) {
            if (isRunning) {
                while (isRunning) {
                    // Sonar Fix modify by huwei 220609
                    try {
                        lock1.wait(30000);
                    } catch (InterruptedException e) {
                        logger.error(e.getMessage(), e);
                        Thread.currentThread().interrupt();
                    }
                }
                isRunning = true;
            } else {
                isRunning = true;
            }
        }
        try {
            entrustStatusService.run(batchExecuteInfo);
            result.setStatus(BatchStatus.COMPLETED);
        } catch (Exception e) {
            logger.error(e.getMessage());
            //异常信息记录到批处理的日志详情
            throw new BatchException(RiskUtil.getLogString(e));
        }
        isRunning = false;
        return result;
    }
}
