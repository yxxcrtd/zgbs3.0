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
import com.joyintech.tams.settle.batch.datagather.dto.InvestTradeWriteDto;
import com.joyintech.tams.settle.batch.datagather.listener.StlDataGatherJobListener;
import com.joyintech.tams.settle.batch.datagather.processor.*;
import com.joyintech.tams.settle.batch.datagather.processor.bond.*;
import com.joyintech.tams.settle.batch.datagather.processor.repo.*;
import com.joyintech.tams.settle.batch.datagather.reader.*;
import com.joyintech.tams.settle.batch.datagather.reader.bond.*;
import com.joyintech.tams.settle.batch.datagather.reader.repo.*;
import com.joyintech.tams.settle.batch.datagather.writer.StlDataGatherWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * @author 何阳阳
 * @date 2020/7/16
 * @see StlDownDataGatherTask
 * @since 1.0
 */
@Service("stlDownDataGatherTask")
@Lazy
public class StlDownDataGatherTask extends BatchBaseService {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private StlDataGatherBond1Reader stlDataGatherBond1Reader;

    @Autowired
    private StlDataGatherBond2Reader stlDataGatherBond2Reader;

    @Autowired
    private StlDataGatherBond3Reader stlDataGatherBond3Reader;

    @Autowired
    private StlDataGatherBond4Reader stlDataGatherBond4Reader;

    @Autowired
    private StlDataGatherBond5Reader stlDataGatherBond5Reader;

    @Autowired
    private StlDataGatherBond7Reader stlDataGatherBond7Reader;

    @Autowired
    private StlDataGatherBond1Processor stlDataGatherBond1Processor;

    @Autowired
    private StlDataGatherBond2Processor stlDataGatherBond2Processor;

    @Autowired
    private StlDataGatherBond3Processor stlDataGatherBond3Processor;

    @Autowired
    private StlDataGatherBond4Processor stlDataGatherBond4Processor;

    @Autowired
    private StlDataGatherBond5Processor stlDataGatherBond5Processor;

    @Autowired
    private StlDataGatherBond7Processor stlDataGatherBond7Processor;

    @Autowired
    private StlDataGatherFund1Reader stlDataGatherFund1Reader;

    @Autowired
    private StlDataGatherFund1Processor stlDataGatherFund1Processor;

    @Autowired
    private StlDataGatherRepo1Reader stlDataGatherRepo1Reader;

    @Autowired
    private StlDataGatherRepo6Reader stlDataGatherRepo6Reader;

    @Autowired
    private StlDataGatherRepo1Processor stlDataGatherRepo1Processor;

    @Autowired
    private StlDataGatherRepo6Processor stlDataGatherRepo6Processor;

    @Autowired
    private StlDataGatherShares1Reader stlDataGatherShares1Reader;

    @Autowired
    private StlDataGatherShares3Reader stlDataGatherShares3Reader;

    @Autowired
    private StlDataGatherShares1Processor stlDataGatherShares1Processor;

    @Autowired
    private StlDataGatherShares3Processor stlDataGatherShares3Processor;

    @Autowired
    private StlDataGatherWriter stlDataGatherWriter;

    @Autowired
    private StlDataGatherJobListener stlDataGatherJobListener;

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
        JobExecution jobExecution = batchBuilder.init(this)
            .batch("bond1", stlDataGatherBond1Reader, stlDataGatherBond1Processor, stlDataGatherWriter)
            .batch("bond2", stlDataGatherBond2Reader, stlDataGatherBond2Processor, stlDataGatherWriter)
            .batch("bond3", stlDataGatherBond3Reader, stlDataGatherBond3Processor, stlDataGatherWriter)
            .batch("bond4", stlDataGatherBond4Reader, stlDataGatherBond4Processor, stlDataGatherWriter)
            .batch("bond5", stlDataGatherBond5Reader, stlDataGatherBond5Processor, stlDataGatherWriter)
            .batch("fund1", stlDataGatherFund1Reader, stlDataGatherFund1Processor, stlDataGatherWriter)
            .batch("repo1", stlDataGatherRepo1Reader, stlDataGatherRepo1Processor, stlDataGatherWriter)
            .batch("repo6", stlDataGatherRepo6Reader, stlDataGatherRepo6Processor, stlDataGatherWriter)
            .batch("shares1", stlDataGatherShares1Reader, stlDataGatherShares1Processor, stlDataGatherWriter)
            .batch("shares3", stlDataGatherShares3Reader, stlDataGatherShares3Processor, stlDataGatherWriter)
            .batch("bond7", stlDataGatherBond7Reader, stlDataGatherBond7Processor, stlDataGatherWriter)
            .buildFlow("债券买卖交易", "bond1")
            .buildFlow("可转债申购", "bond2")
            .buildFlow("可转债申购确认", "bond3")
            .buildFlow("债转股", "bond4")
            .buildFlow("配债", "bond5")
            .buildFlow("基金买入、卖出、除权交易", "fund1")
            .buildFlow("债券质押式回购_首期", "repo1")
            .buildFlow("债券质押式协议回购_逆回购_到期交易", "repo6")
            .buildFlow("股票分红及送转交易", "shares2")
            .buildFlow("股票申购确认交易", "shares3")
            .buildFlow("债券存续期", "bond6")
            .buildFlow("债券股票上市流通", "bond7")
            .parallelBuilder(this.getBatchTask().getBeanName() + StringUtil.SPACE + this.getBatchTask().getTaskId(),
                    stlDataGatherJobListener,
                    "债券买卖交易", "可转债申购", "可转债申购确认", "债转股", "配债", "基金买入、卖出、除权交易"
                    , "债券质押式回购_首期", "债券质押式协议回购_逆回购_到期交易", "股票分红及送转交易","股票申购确认交易",
                    "债券存续期", "债券股票上市流通")
            .run();

        return jobExecution;
    }
}