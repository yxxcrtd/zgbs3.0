/*
 * 文件名：CreditExtendsTask.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2019年7月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.batch.creditmanager.runner;

import com.joyintech.batch.batchbase.baseclass.BatchBaseService;
import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.constant.BatchDictCodeList;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.web.PlatformDate;
import com.joyintech.tams.invest.batch.creditmanager.service.CreditExtendsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 *  授信继承任务
 * 〈功能详细描述〉
 * @author 裴家俊
 * @version 1.0 2019年7月25日
 * @see CreditExtendsTask
 * @since 1.0
 */
@Service("creditExtendsTask")
@Scope("prototype")
public class CreditExtendsTask extends BatchBaseService{

    /**
    * 日志
    */
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private CreditExtendsService creditExtendsService;

    /**
     * PlatformDate
     */
    @Autowired(required = false)
    private PlatformDate platformDateService;

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
        try {
        	if(null!=batchExecuteInfo&&null!=batchExecuteInfo.getExecuteDate()
                && !StringUtil.equals(BatchDictCodeList.批处理启动模式_自动.getValue(),
                    batchExecuteInfo.getStartMode())){
        		supDate=batchExecuteInfo.getExecuteDate();
        	}
        	else{
                // Sonar Fix modify by huwei 220609
        		supDate = platformDateService.getSysDate(null == batchExecuteInfo ? null : batchExecuteInfo.getCreateUser());
        	}
            if(CacheUtil.getSysParam("IS_EXTEND_CREDIT").equals("true")) {
                creditExtendsService.run(supDate);
            }
            result.setStatus(BatchStatus.COMPLETED);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }
        isRunning=false;
        return result;
    }
}
