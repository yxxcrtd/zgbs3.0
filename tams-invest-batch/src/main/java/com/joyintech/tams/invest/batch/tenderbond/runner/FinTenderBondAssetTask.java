package com.joyintech.tams.invest.batch.tenderbond.runner;

import com.joyintech.batch.batchbase.baseclass.BatchBaseService;
import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.constant.BatchDictCodeList;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.web.PlatformDate;
import com.joyintech.tams.invest.batch.tenderbond.service.FinTenderBondAssetBatchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 新债标的自动上市流通
 *
 * @author gwl
 * @version 1.0 2022年04月18日
 * @see FinTenderBondAssetTask
 * @since 1.0
 */
@Scope("prototype")
@Service("finTenderBondAssetTask")
public class FinTenderBondAssetTask extends BatchBaseService {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Resource
    private PlatformDate platformDateService;
    @Resource
    private FinTenderBondAssetBatchService finTenderBondAssetService;

    @Override
    public JobExecution execute() throws BatchException {
        JobExecution result = new JobExecution(1l);
        result.setStatus(BatchStatus.FAILED);
        SysBatchExecInfo batchExecuteInfo = this.getBatchExecuteInfo();
        if (batchExecuteInfo == null) {
            logger.info("无启动参数，批处理退出。");
            return result;
        }
        Date batchDate = batchExecuteInfo.getExecuteDate();
        if (StringUtil.equals(BatchDictCodeList.批处理启动模式_自动.getValue(), batchExecuteInfo.getStartMode())) {
            batchDate = platformDateService.getSysDate();
        }
        try {
            finTenderBondAssetService.execute(batchDate);
            result.setStatus(BatchStatus.COMPLETED);
        } catch (Exception e) {
            logger.error("自动上市流通跑批失败",e);
        }
        return result;
    }
}
