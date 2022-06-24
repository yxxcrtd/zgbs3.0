package com.joyintech.tams.invest.batch.etragrerepdelay.runner;

import java.util.Date;
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
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.invest.batch.common.util.BatchConstant;
import com.joyintech.tams.invest.batch.etragrerepdelay.service.EtrAgreRepDelayService;
import com.joyintech.tams.invest.common.risktrialrds.util.Const;
//import com.netflix.discovery.converters.Auto;

/**
 * 协议回购到期指令延期
 * @author yaozhenguo
 *	20210305
 */
@Service("etrAgreRepDelayTask")
@Scope("prototype")
public class EtrAgreRepDelayTask extends BatchBaseService {

	/**
     * 日志
     */
     private Logger logger = LoggerFactory.getLogger(getClass());
     
     @Autowired
     private EtrAgreRepDelayService etrAgreRepDelayService;
     
     @Override
 	 public JobExecution execute() throws BatchException {
 		JobExecution result = new JobExecution(1l);
         SysBatchExecInfo batchExecuteInfo = this.getBatchExecuteInfo();
         if (batchExecuteInfo == null) {
             logger.info("无启动参数，批处理退出。");
             return null;
         }
         try {
        	 String sysDate = DateUtil.format(batchExecuteInfo.getExecuteDate());
         	 etrAgreRepDelayService.run(sysDate);
         	 result.setStatus(BatchStatus.COMPLETED);
             logger.info("协议回购到期指令延期批处理执行成功");
 		} catch (Exception e) {
 			logger.error(e.getMessage(),e);
 			result.setStatus(BatchStatus.FAILED);
 			logger.info("协议回购到期指令延期批处理执行异常");
 			return result;
 		}
 		return result;
 	}
}
