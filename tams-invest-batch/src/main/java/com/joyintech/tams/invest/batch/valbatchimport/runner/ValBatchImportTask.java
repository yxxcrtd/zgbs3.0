/*
 * 文件名：ValRiftTask.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年1月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.batch.valbatchimport.runner;

import java.util.Date;

import com.joyintech.tams.invest.batch.valbatchimport.service.ValBatchImportService;
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

/**
 *  估值表批量自动导入
 * 〈功能详细描述〉
 * @author wangyang1
 * @version 1.0 2021年5月17日
 * @see ValBatchImportTask
 * @since 1.0
 */
@Service("valBatchImportTask")
@Scope("prototype")
public class ValBatchImportTask extends BatchBaseService{

    /**
    * 日志
    */
    private Logger logger = LoggerFactory.getLogger(getClass());

    private static final Object lock1 = new Object();

    @Autowired
    private ValBatchImportService valBatchImportService;
    
    private static Boolean isRunning = false;
    
	private Date supDate;
	
    @Override
    public JobExecution execute()
        throws BatchException {
        JobExecution result = new JobExecution(1l);
        result.setStatus(BatchStatus.FAILED);
        SysBatchExecInfo batchExecuteInfo = this.getBatchExecuteInfo();
        // Sonar Fix modify by huwei 220609
        synchronized (lock1) {
            if(isRunning) return result;
            else isRunning = true;
        }
        try {
        	if(null!=batchExecuteInfo&&null!=batchExecuteInfo.getExecuteDate()){
        		supDate=batchExecuteInfo.getExecuteDate();
        	}
        	else{
        		supDate = new Date();
        	}
            valBatchImportService.execute(supDate);
            result.setStatus(BatchStatus.COMPLETED);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        }finally {
        	isRunning=false;
        }
        return result;
    }
}
