package com.joyintech.tams.invest.batch.autocoupon.fund.runner;

import com.joyintech.batch.batchbase.baseclass.BatchBaseService;
import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.tams.invest.batch.autocoupon.dao.AutoCouponMapper;
import com.joyintech.tams.invest.batch.autocoupon.dto.AutoCouponErrLog;
import com.joyintech.tams.invest.batch.autocoupon.fund.service.AutoCouponFundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author zhangnian
 * @ClassName AutoCouponFundTask
 * @description TODO
 * @date 2020/10/16 0016 9:49
 **/
@Service("autoCouponFundTask")
@Scope("prototype")
public class AutoCouponFundTask extends BatchBaseService {
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AutoCouponFundService autoCouponFundService;

    @Autowired
    private AutoCouponMapper autoCouponMapper;

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
            if(isRunning) return result;
            else isRunning = true;
        }
        if(null!=batchExecuteInfo&&null!=batchExecuteInfo.getExecuteDate()){
            supDate=batchExecuteInfo.getExecuteDate();
        }
        else{
            supDate = new Date();
        }
        List<AutoCouponErrLog> logs = new ArrayList<>();
        List<Future> futures =  autoCouponFundService.run(supDate);
        if(null!=futures&&!futures.isEmpty()){
            for (Future future : futures) {
                try {
                    logs.addAll((List<AutoCouponErrLog>)future.get());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    // Sonar Fix modify by huwei 220608
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }
        isRunning = false;
        //日志入库
        if(!logs.isEmpty()){
            logs.forEach(item->{
                //保存异常信息
                autoCouponMapper.insertAutoCouponErrLog(item);
            });
            throw new BatchException("部分数据异常,具体信息请查看相关日志表(auto_coupon_err_log)!");
        }
        result.setStatus(BatchStatus.COMPLETED);
        isRunning=false;
        return result;
    }
} 

