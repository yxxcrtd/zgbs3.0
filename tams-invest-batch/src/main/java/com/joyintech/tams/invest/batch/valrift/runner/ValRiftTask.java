/*
 * 文件名：ValRiftTask.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年1月25日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.batch.valrift.runner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
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
import com.joyintech.tams.invest.batch.valrift.service.ValRiftService;

/**
 *  估值穿透任务
 * 〈功能详细描述〉
 * @author zhangnian
 * @version 1.0 2019年1月25日
 * @see ValRiftTask
 * @since 1.0
 */
@Service("valRiftTask")
@Scope("prototype")
public class ValRiftTask extends BatchBaseService{

    /**
    * 日志
    */
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    @Autowired
    private ValRiftService valRiftService;
    
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
            if(null!=batchExecuteInfo&&null!=batchExecuteInfo.getExecuteDate()){
                supDate=batchExecuteInfo.getExecuteDate();
            }
            else{
                supDate = new Date();
            }
            //add by lpf for 估值穿透改造 S 将产品分为指定日期范围内的导入估值表 和没导入估值表的产品
            String deptCode = null;
            try{
                deptCode = batchExecuteInfo.getCreateUser() != null?MultilevelDataAuthorityUtil.getOrganizationCode(batchExecuteInfo.getCreateUser()):null;
            }catch (Exception e){

            }
            List<Map<String,Object>> importValTableProdList = valRiftService.selectImportValTableProdList(supDate,this.getProductIdList(),deptCode);
            List<String> importProdList = new ArrayList<>();
            List<String> unImportProdList = new ArrayList<>();
            Date minDate = supDate;
            if(!StringUtil.isEmptyList(importValTableProdList)){
                for (Map<String,Object> entry:importValTableProdList) {
                    //导入了估值表的产品寻找最小跑批日
                    if(null!=entry){
                        if(StringUtil.isNotEmpty(entry.get("valDate"))){
                            if(minDate.compareTo((Date)entry.get("valDate"))>0){
                                minDate = (Date)entry.get("valDate");
                            }
                            importProdList.add((String)entry.get("finprodId"));
                        }
                        else {
                            unImportProdList.add((String)entry.get("finprodId"));
                        }
                    }
                }
            }

            //未导入估值表的产品仅跑跑批日当日
            if(!StringUtil.isEmptyList(unImportProdList)){
                valRiftService.run(supDate, unImportProdList);
            }
            //导入估值表的产品循环最小跑批日到当前跑批日
            if(!StringUtil.isEmptyList(importProdList)){
                Date currentDate = minDate;
                for(int i = 0; i<= DateUtil.getBetweenDayNumber(minDate,supDate); i++){
                    valRiftService.run(currentDate, importProdList);
                    currentDate = DateUtil.addDay(currentDate,1);
                }
            }
            //add by lpf for 估值穿透改造 E
//            valRiftService.run(supDate, this.getProductIdList());
            result.setStatus(BatchStatus.COMPLETED);
        }
        catch (Exception e) {
            logger.error(e.getMessage());
        }
        isRunning=false;
        return result;
    }
}
