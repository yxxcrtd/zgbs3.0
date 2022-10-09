package com.joyintech.tams.invest.batch.risktrialrds.runner;

import com.joyintech.batch.batchbase.baseclass.*;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.constant.BatchDictCodeList;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.invest.batch.common.util.BatchConstant;
import com.joyintech.tams.invest.batch.risktrialrds.processor.RiskTrialDayEndProcessor;
import com.joyintech.tams.invest.batch.risktrialrds.reader.RiskTrialDayEndReader;
import com.joyintech.tams.invest.batch.risktrialrds.writer.RiskTrialDayEndWriter;
import com.joyintech.tams.invest.common.risktrialrds.util.Const;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 日终批处理指标计算（优化RiskTrialDayEndTask的性能）
 * 待优化点：
 * 1. 优化指标计算SQL逻辑，将统计维度修改，减少数据IO操作
 * 2. 相同的指标参数按照统计参数仅计算一次
 * 3. 计算过程中产生的日志可以收集后异步批量写入
 * 4. RSK_ISUP_POINT_VAL和RSK_ISUP_POINT_DET_VAL 数据归档及表分区，减少数据量
 * @author 何阳阳
 * @version 1.0 2022/4/25
 * @see RiskTrialDayEndTask
 * @since 1.0
 **/
@Service(value="riskTrialDayEndTask")
@Scope(value="prototype")
public class RiskTrialDayEndTask extends BatchBaseService{
    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * PlatformDate
     */
    @Autowired(required = false)
    private IPlatformDate platformDateService;

    @Autowired
    private BatchBuilder<BaseReader<NeedOperateEntity>, BaseProcessor<NeedOperateEntity, NeedToWriteEntity>, BaseWriter<NeedToWriteEntity>> batchBuilder;

    @Autowired
    @Qualifier("riskTrialDayEndReader")
    private RiskTrialDayEndReader reader;

    @Autowired
    private RiskTrialDayEndProcessor processor;

    @Autowired
    private RiskTrialDayEndWriter writer;

    @Override
    public JobExecution execute() throws BatchException {
        SysBatchExecInfo batchExecuteInfo = this.getBatchExecuteInfo();
        if (batchExecuteInfo == null) {
            logger.info("无启动参数，批处理退出。");
            return null;
        }

        Date platDate;
        // 设置此task的序列任务公用的params参数
        Map<String, Object> params = new HashMap<>();
        if(null != batchExecuteInfo.getExecuteDate() && StringUtil.equals(BatchDictCodeList.批处理启动模式_手动.getValue(), batchExecuteInfo.getStartMode())){
            platDate = batchExecuteInfo.getExecuteDate();
        }
        else{
            platDate = platformDateService.getSysDate();
        }
        params.put(BatchConstant.PLATE_DATE, platDate);
        params.put(Const.PROD_LIST, this.getProductIdList());
        // 组合代码
        params.put("portfolioId", this.getBatchExecuteDetail().getOtherParam1());
        // 指标代码
        params.put("indId", this.getBatchExecuteDetail().getOtherParam2());

        reader.setParams(params);
        processor.initParams(DateUtil.format(platDate));
        writer.initParams(platDate);

        batchBuilder.init(this);

        return batchBuilder
                .batch("日终计算当日指标", (BaseReader)reader, (BaseProcessor)processor, writer)
                .builder("日终指标计算")
                .run();
    }
}
