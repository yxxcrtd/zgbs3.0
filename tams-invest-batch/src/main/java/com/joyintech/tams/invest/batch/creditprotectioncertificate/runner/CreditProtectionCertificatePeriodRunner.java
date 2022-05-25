package com.joyintech.tams.invest.batch.creditprotectioncertificate.runner;

import com.joyintech.batch.batchbase.baseclass.*;
import com.joyintech.batch.batchbase.baseclass.entity.NeedOperateEntity;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.web.PlatformDate;
import com.joyintech.tams.invest.batch.common.util.BatchConstant;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.process.CreditProtectionCertificatePeriodProcess1;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.process.CreditProtectionCertificatePeriodProcess2;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.reader.CreditProtectionCertificatePeriodReader1;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.reader.CreditProtectionCertificatePeriodReader2;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.writer.CreditProtectionCertificatePeriodWriter1;
import com.joyintech.tams.invest.batch.creditprotectioncertificate.writer.CreditProtectionCertificatePeriodWriter2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 〈功能详细描述〉
 * 信用保护凭证到期自动生成
 *
 * @author yangxunpeng
 * @version 1.0 2022年05月25日
 * @since 1.0
 */
@Service("creditProtectionCertificatePeriodRunner")
@Scope("prototype")
public class CreditProtectionCertificatePeriodRunner extends BatchBaseService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired(required = false)
    private PlatformDate platformDateService;

    @Autowired
    private BatchBuilder<BaseReader<NeedOperateEntity>, BaseProcessor<NeedOperateEntity, NeedToWriteEntity>, BaseWriter<NeedToWriteEntity>> batchBuilder;

    @Autowired
    private CreditProtectionCertificatePeriodReader1 reader1;

    @Autowired
    private CreditProtectionCertificatePeriodProcess1 process1;

    @Autowired
    private CreditProtectionCertificatePeriodWriter1 writer1;

    @Autowired
    private CreditProtectionCertificatePeriodReader2 reader2;

    @Autowired
    private CreditProtectionCertificatePeriodProcess2 process2;

    @Autowired
    private CreditProtectionCertificatePeriodWriter2 writer2;

    @Override
    public JobExecution execute() throws BatchException {
        SysBatchExecInfo batchExecuteInfo = this.getBatchExecuteInfo();
        if (batchExecuteInfo == null) {
            logger.info("无启动参数，批处理退出。");
            return null;
        }
        // 设置此task的序列任务公用的params参数
        Map<String, Object> params = new HashMap<>();

        params.put(BatchConstant.BATCH_INFO, batchExecuteInfo);
        params.put(BatchConstant.PLATE_DATE, platformDateService.getSysDate(batchExecuteInfo.getCreateUser()));

        // 各步骤设置参数
        reader1.setParams(params);
        process1.setParams(params);
        writer1.setParams(params);
        reader2.setParams(params);
        process2.setParams(params);
        writer2.setParams(params);

        return batchBuilder.init(this)
                .batch("step1", (BaseReader) reader1, (BaseProcessor) process1, (BaseWriter) writer1)
                .batch("step2", (BaseReader) reader2, (BaseProcessor) process2, (BaseWriter) writer2)
                .builder(this.getBatchTask().getBeanName() + this.getBatchTask().getTaskId())
                .run();
    }
}
