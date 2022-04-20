/*
 * 文件名：ProfitAndLossAccountingTask.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：杨智
 * 修改时间：2018年5月23日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.profitandlossaccounting.runner;

import com.joyintech.batch.batchbase.baseclass.*;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.tams.valuation.batch.common.accountingmethod.accounting.BookCommonLogic;
import com.joyintech.tams.valuation.batch.common.bookgenerateresult.BookGenerateResultForCurrentCommonWriter;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.dto.ProfitAndLossAccountingNeedOperateEntity;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.dto.ProfitAndLossAccountingResult;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.processor.*;
import com.joyintech.tams.valuation.batch.profitandlossaccounting.reader.*;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.GlobalConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * ProfitAndLossAccountingTask
 *
 * @author 杨智
 * @version 1.0 2018年5月23日
 * @see ProfitAndLossAccountingTask
 * @since 1.0
 */
@Service("profitAndLossAccountingTask")
@Scope("prototype")
public class ProfitAndLossAccountingTask extends BatchBaseService {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private BatchBuilder<BaseReader<ProfitAndLossAccountingNeedOperateEntity>, BaseProcessor<NeedOperateEntity, ProfitAndLossAccountingResult>, BaseWriter<NeedToWriteEntity>> batchBuilder;

    /**
     * 记账引擎
     */
    @Autowired
    private BookCommonLogic bookCommonLogic;

    /**
     * step1Reader
     */
    @Autowired
    private ProfitAndLossAccountingStep1Reader step1Reader;

    /**
     * step1Processor
     */
    @Autowired
    private ProfitAndLossAccountingStep1Processor step1Processor;
    
    /**
     * step2Reader
     */
    @Autowired
    private ProfitAndLossAccountingLayeringReader step2Reader;
    
    /**
     * step2Processor
     */
    @Autowired
    private ProfitAndLossAccountingLayeringProcessor step2Processor;
    
    /**
     * step1Reader
     */
    @Autowired
    private ProfitAndLossAccountingStep2Reader step3Reader;

    /**
     * step1Processor
     */
    @Autowired
    private ProfitAndLossAccountingStep2Processor step3Processor;
    
    /**
     * step1Reader
     */
    @Autowired
    private ProfitAndLossAccountingStep3Reader step4Reader;

    /**
     * step1Processor
     */
    @Autowired
    private ProfitAndLossAccountingStep3Processor step4Processor;
    
    /**
     * step1Reader
     */
    @Autowired
    private ProfitAndLossAccountingStep4Reader step5Reader;

    /**
     * step1Processor
     */
    @Autowired
    private ProfitAndLossAccountingStep4Processor step5Processor;

    /**
     * step1Writer
     */
    @Autowired
    private BookGenerateResultForCurrentCommonWriter step1Writer;

    @Override
    public JobExecution execute()
        throws BatchException {
        // 获取批处理信息
        SysBatchExecInfo batchExecuteInfo = this.getBatchExecuteInfo();
        if (batchExecuteInfo == null) {
            logger.info("无启动参数，批处理退出。");
            return null;
        }

        Map<String, Object> params = new HashMap<String, Object>();
        Map<String, String> dimValueMap = bookCommonLogic.getDimensionValue(this.getBatchExecuteInfo());
        params.put(BookConstant.BOOKSET_DATE, DateUtil.parse(dimValueMap.get(BookConstant.BOOKSET_DATE)));
        params.put(BookConstant.BOOK_DATE, DateUtil.parse(dimValueMap.get(BookConstant.BOOK_DATE)));
        params.put(BookConstant.HAPPEN_DATE, DateUtil.parse(dimValueMap.get(BookConstant.HAPPEN_DATE)));
        params.put("seqNo", this.getBatchExecuteDetail().getSeqNo());
        params.put("taskId", this.getBatchExecuteInfo().getTaskId());
        params.put(BookConstant.IS_SUB_BOOKSET, dimValueMap.get(BookConstant.IS_SUB_BOOKSET));
        step1Reader.setParams(params);
        step2Reader.setParams(params);
        step3Reader.setParams(params);
        step4Reader.setParams(params);
        step5Reader.setParams(params);
        step1Processor.setParams(params);
        step2Processor.setParams(params);
        step3Processor.setParams(params);
        step4Processor.setParams(params);
        step5Processor.setParams(params);
        logger.info("批处理[" + this.getBatchTask().getTaskName() + "]开始。");
        // 费前结转只处理step1和step2
        if(GlobalConstant.批处理任务_损益结转费前.equals(this.getBatchExecuteInfo().getTaskId())){
            return batchBuilder.init(this).batch("step1", step1Reader, step1Processor,
                step1Writer).batch("step2", step2Reader, step2Processor, step1Writer).builder(
                                this.getBatchTask().getBeanName() + this.getBatchTask().getTaskId()).run();
        }
        // 费后结转处理step1到step5
        else{
//            return batchBuilder.init(this).batch("step1", step1Reader, step1Processor,
//                step1Writer).batch("step2", step2Reader, step2Processor, step1Writer).batch("step3",
//                    step3Reader, step3Processor, step1Writer).batch("step4", step4Reader,
//                        step4Processor, step1Writer).batch("step5", step5Reader,
//                            step5Processor, step1Writer).builder(
//                                this.getBatchTask().getBeanName() + this.getBatchTask().getTaskId()).run();
            // 去除待分配利润，只结转到未分配利润-未实现和未分配利润-已实现中
            return batchBuilder.init(this).batch("step1", step1Reader, step1Processor,
                    step1Writer).batch("step2", step2Reader, step2Processor, step1Writer).batch("step3",
                    step3Reader, step3Processor, step1Writer).batch("step4", step4Reader,
                    step4Processor, step1Writer).builder(
                    this.getBatchTask().getBeanName() + this.getBatchTask().getTaskId()).run();
        }
    }

}
