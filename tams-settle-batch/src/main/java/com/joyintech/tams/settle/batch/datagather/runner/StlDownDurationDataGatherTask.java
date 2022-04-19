package com.joyintech.tams.settle.batch.datagather.runner;

import com.joyintech.batch.batchbase.baseclass.BaseProcessor;
import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.BatchBaseService;
import com.joyintech.batch.batchbase.baseclass.BatchBuilder;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.web.PlatformDate;
import com.joyintech.tams.invest.common.trade.service.TrdCommonBaseService;
import com.joyintech.tams.settle.batch.datagather.dto.InvestTradeWriteDto;
import com.joyintech.tams.settle.batch.datagather.listener.StlDataGatherJobListener;
import com.joyintech.tams.settle.batch.datagather.processor.StlDataGatherFund2Processor;
import com.joyintech.tams.settle.batch.datagather.processor.StlDataGatherShares2Processor;
import com.joyintech.tams.settle.batch.datagather.processor.bond.StlDataGatherBondDurationProcessor;
import com.joyintech.tams.settle.batch.datagather.processor.repo.StlDataGatherRepoDurationProcessor;
import com.joyintech.tams.settle.batch.datagather.reader.StlDataGatherFund2Reader;
import com.joyintech.tams.settle.batch.datagather.reader.StlDataGatherReader;
import com.joyintech.tams.settle.batch.datagather.reader.StlDataGatherShares2Reader;
import com.joyintech.tams.settle.batch.datagather.reader.bond.StlDataGatherBondDurationReader;
import com.joyintech.tams.settle.batch.datagather.reader.repo.StlDataGatherRepoDurationReader;
import com.joyintech.tams.settle.batch.datagather.writer.StlDataGatherWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 存续期交易生成批处理任务
 * @author fuyongsheng
 * @date 2022/4/8
 * @see StlDownDurationDataGatherTask
 * @since 1.0
 */
@Service("stlDownDurationDataGatherTask")
@Lazy
public class StlDownDurationDataGatherTask extends BatchBaseService {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StlDataGatherFund2Reader stlDataGatherFund2Reader;
    
    @Autowired
    private StlDataGatherFund2Processor stlDataGatherFund2Processor;

    @Autowired
    private StlDataGatherShares2Reader stlDataGatherShares2Reader;

    @Autowired
    private StlDataGatherShares2Processor stlDataGatherShares2Processor;

    @Autowired
    private StlDataGatherWriter stlDataGatherWriter;

    @Autowired
    private StlDataGatherJobListener stlDataGatherJobListener;

    @Autowired
    private StlDataGatherBondDurationReader stlDataGatherBondDurationReader;

    @Autowired
    private StlDataGatherBondDurationProcessor stlDataGatherBondDurationProcessor;

    @Autowired
    private StlDataGatherRepoDurationReader stlDataGatherRepoDurationReader;

    @Autowired
    private StlDataGatherRepoDurationProcessor stlDataGatherRepoDurationProcessor;

    @Autowired
    private TrdCommonBaseService trdCommonBaseService;

    /**
     * PlatformDate
     */
    @Autowired(required = false)
    private PlatformDate platformDateService;

    /**
     * BatchBuilder
     */
    @Autowired
    private BatchBuilder<StlDataGatherReader, BaseProcessor<NeedOperateEntity, InvestTradeWriteDto>, BaseWriter<NeedToWriteEntity>> batchBuilder;

    @Override
    public JobExecution execute() throws BatchException {
        SysBatchExecInfo batchExecuteInfo = this.getBatchExecuteInfo();
        if (batchExecuteInfo == null) {
            logger.info("无启动参数，批处理退出。");
            return null;
        }
        Date date = batchExecuteInfo.getExecuteDate();
        if(null == batchExecuteInfo.getExecuteDate()){
            date = platformDateService.getSysDate();
        }
        Integer unFinishMatchNum = trdCommonBaseService.getUnFinishMatchSettleTransDataNum(date);
        if(unFinishMatchNum > 0){
            logger.debug("当日未完全成交匹配数量大于0,批处理退出。");
            return null;
        }
        JobExecution jobExecution = batchBuilder.init(this)
            .batch("fund2", stlDataGatherFund2Reader, stlDataGatherFund2Processor, stlDataGatherWriter)
            .batch("repo5", stlDataGatherRepoDurationReader, stlDataGatherRepoDurationProcessor, stlDataGatherWriter)
            .batch("shares2", stlDataGatherShares2Reader, stlDataGatherShares2Processor, stlDataGatherWriter)
            .batch("bond6", stlDataGatherBondDurationReader, stlDataGatherBondDurationProcessor, stlDataGatherWriter)
            .buildFlow("基金交易分红交易", "fund2")
            .buildFlow("债券质押式竞价回购_到期", "repo5")
            .buildFlow("股票分红及送转交易", "shares2")
            .buildFlow("债券存续期", "bond6")
            .parallelBuilder(this.getBatchTask().getBeanName() + StringUtil.SPACE + this.getBatchTask().getTaskId(), stlDataGatherJobListener,
                      "基金交易分红交易", "债券质押式竞价回购_到期", "股票分红及送转交易","债券存续期")
            .run();

        return jobExecution;
    }
}