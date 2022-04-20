/*
 * 文件名：DeltRightFairValeAlterationsTask.java
 * 版权：Copyright 2020-2021 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V2.1.4
 * 修改人：wujie
 * 修改时间：2020年10月15日
 * 修改内容：新建
 * 系统名称：估值核算系统V2.1.4
 */

package com.joyintech.tams.valuation.batch.deltrightfairvalealterations.runner;

import com.joyintech.batch.batchbase.baseclass.*;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.batch.common.bookgenerateresult.BookGenerateResultForCurrentCommonWriterWithBokSummary;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.reader.DrawBaseInfoSelectComReader;
import com.joyintech.tams.valuation.batch.deltrightfairvalealterations.processor.DeltRightFairValeAlterationsProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 债权类资产公允价值变动批处理
 * 
 * @author wujie
 * @version 1.0 2020年10月15日
 * @see DeltRightFairValeAlterationsTask
 * @since 1.0
 */
@Service("deltRightFairValeAlterationsTask")
@Scope("prototype")
public class DeltRightFairValeAlterationsTask extends BatchBaseService {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());
    
    /**
     * BatchBuilder
     */
    @Autowired
    private BatchBuilder<BaseReader<NeedOperateEntity>, BaseProcessor<NeedOperateEntity, 
                        NeedToWriteEntity>, BaseWriter<NeedToWriteEntity>> batchBuilder;

    /**
     * step1Reader
     */
    @Autowired
    private DrawBaseInfoSelectComReader step1Reader;

    /**
     * step1Processor
     */
    @Autowired
    private DeltRightFairValeAlterationsProcessor step1Processor;

    /**
     * step1Writer
     */
    @Autowired
    private BookGenerateResultForCurrentCommonWriterWithBokSummary step1Writer;

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public JobExecution execute() throws BatchException {

        // 获取批处理信息
        SysBatchExecInfo batchExecuteInfo = this.getBatchExecuteInfo();
        if (batchExecuteInfo == null) {
            logger.info("无启动参数，批处理退出。");
            return null;
        }
 
        // 设置此task的序列任务公用的params参数
        Map<String, Object> params = new HashMap<>();
		        params.put("BATCHINFO", this.getBatchExecuteInfo());// 跑批范围信息list（批处理待处理任务表）
        params.put("BATCHDEL", this.getBatchExecuteDetail());// 跑批范围信息list（批处理待处理任务表）

        /** ADD BY weipeng ON 20220314 科目性质合并，共通reader区分资产类型 start  **/
        params.put("secTypeList", Arrays.asList(DictCodeListExt1.证券品种_计划类资产_收益型.getValue(),
            DictCodeListExt1.证券品种_项目类资产.getValue()));
        /** ADD BY weipeng ON 20220314 科目性质合并，共通reader区分资产类型 end  **/

        // 各步骤设置参数
        step1Reader.setParams(params);
        step1Processor.setParams(params);
        step1Writer.setParams(params);

        return batchBuilder.init(this).batch("step1", (BaseReader)step1Reader,
            (BaseProcessor)step1Processor, (BaseWriter)step1Writer).builder(
                this.getBatchTask().getBeanName() + this.getBatchTask().getTaskId()).run();
    }

}
