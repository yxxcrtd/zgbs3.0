/*
 * 文件名：RskParamReCalcTask.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：裴家俊
 * 修改时间：2020年4月17日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.riskparamcalc.runner;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.joyintech.batch.batchbase.baseclass.*;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.constant.BatchDictCodeList;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.web.PlatformDate;
import com.joyintech.tams.invest.batch.common.util.BatchConstant;
import com.joyintech.tams.invest.batch.ocphistory.service.RskOccupyHistoryService;
import com.joyintech.tams.invest.batch.riskparamcalc.processor.RskParamCalcProcessor;
import com.joyintech.tams.invest.batch.riskparamcalc.processor.RskParamReCalcProcessor;
import com.joyintech.tams.invest.batch.riskparamcalc.reader.RskParamCalcReader;
import com.joyintech.tams.invest.batch.riskparamcalc.reader.RskParamReCalcReader;
import com.joyintech.tams.invest.batch.riskparamcalc.writer.RskParamCalcWriter;
import com.joyintech.tams.invest.batch.riskparamcalc.writer.RskParamReCalcWriter;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.batch.batchbase.exception.BatchException;


/**
 * 批处理-指标参数重新更新redis
 * 
 * @author 裴家俊
 * @version 1.0 2020年4月17日
 * @see RskParamReCalcTask
 * @since 1.0
 */
@Service(value = "rskParamReCalcTask")
@Scope(value = "prototype")
public class RskParamReCalcTask extends BatchBaseService {

    /**
     * 控制台日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * BatchBuilder
     */
    @Autowired
    private BatchBuilder<BaseReader<NeedOperateEntity>, BaseProcessor<NeedOperateEntity, NeedToWriteEntity>, BaseWriter<NeedToWriteEntity>> batchBuilder;

    /**
     * RskParamReCalcReader
     */
    @Autowired
    private RskParamReCalcReader step1Reader;

    /**
     * RskParamReCalcProcessor
     */
    @Autowired
    private RskParamReCalcProcessor step1Processor;

    /**
     * RskParamReCalcWriter
     */
    @Autowired
    private RskParamReCalcWriter step1Writer;

    /**
     * RskParamReCalcReader
     */
    @Autowired
    private RskParamCalcReader step2Reader;

    /**
     * RskParamReCalcProcessor
     */
    @Autowired
    private RskParamCalcProcessor step2Processor;

    /**
     * RskParamReCalcWriter
     */
    @Autowired
    private RskParamCalcWriter step2Writer;

    @Autowired(required = false)
    private PlatformDate platformDateService;
    @Autowired(required = false)
    private RskOccupyHistoryService rskPreOccupyInfoProService;
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public JobExecution execute()
        throws BatchException {
        logger.info("批处理-预占重计算开始");
        Map<String, Object> params = new HashMap<>();
        SysBatchExecInfo batchExecuteInfo = this.getBatchExecuteInfo();
        if (batchExecuteInfo == null) {
            logger.info("无启动参数，批处理退出。");
            return null;
        }
        if(null!=batchExecuteInfo.getExecuteDate()
            && !StringUtil.equals(BatchDictCodeList.批处理启动模式_自动.getValue(),batchExecuteInfo.getStartMode())){
            params.put(BatchConstant.PLATE_DATE, batchExecuteInfo.getExecuteDate());
            params.put("sysDate", batchExecuteInfo.getExecuteDate());
        }
        else{
            Date sysDate = platformDateService.getSysDate(batchExecuteInfo.getCreateUser());
            params.put(BatchConstant.PLATE_DATE, sysDate);
            params.put("sysDate", sysDate);
        }
        params.put(BatchConstant.BATCH_INFO, batchExecuteInfo);
        rskPreOccupyInfoProService.execute(params);

        params.put(BatchConstant.KEY_PRODID_LIST, this.getProductIdList());
        step1Reader.setParams(params);
        step1Processor.setParams(params);
        step1Writer.setParams(params);
        step2Reader.setParams(params);
        step2Processor.setParams(params);
        step2Writer.setParams(params);
        return batchBuilder.init(this)
            .batch("step1", (BaseReader)step1Reader, (BaseProcessor)step1Processor, step1Writer)
            .batch("step2",step2Reader,step2Processor,step2Writer)
            .builder(this.getBatchTask().getBeanName() + this.getBatchTask().getTaskId())
            .run();
    }

}
