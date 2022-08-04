/*
 * 文件名：BondAutoDurDealGenerateTask.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：etliu
 * 修改时间：2018年12月11日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.autodurdealgenerate.cfetsbond.runner;

import com.joyintech.batch.batchbase.baseclass.BatchBaseService;
import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.constant.BatchDictCodeList;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.web.PlatformDate;
import com.joyintech.tams.invest.batch.autodurdealgenerate.cfetsbond.service.CfetsAutoDurDealProcessorService;
import com.joyintech.tams.invest.common.risktrialrds.util.RiskUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 债券存续期 自动生成债券还本、收息、到期交易。赎回交易
 *
 * @author sjj
 * @version 1.0 2021年11月22日
 * @see CfetsBondAutoDurDealGenerateTask
 * @since 1.0
 */
@Service("cfetsbondAutoDurDealGenerateTask")
@Scope("prototype")
public class CfetsBondAutoDurDealGenerateTask extends BatchBaseService {

    /**
     * 日志
     */
    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * PlatformDate
     */
    @Autowired(required = false)
    private PlatformDate platformDateService;

    @Autowired
    private CfetsAutoDurDealProcessorService cfetsAutoDurDealProcessorService;

    /**
     * {@inheritDoc}
     */
    @Override
    public JobExecution execute()
        throws BatchException {
        JobExecution result = new JobExecution(1l);
        result.setStatus(BatchStatus.FAILED);
        SysBatchExecInfo batchExecuteInfo = this.getBatchExecuteInfo();
        if (batchExecuteInfo == null) {
            logger.info("无启动参数，批处理退出。");
            return result;
        }

        // 自动批处理，取平台日期为跑批日，手动批处理，取画面选择日期为跑批日
        Date batchDate = batchExecuteInfo.getExecuteDate();
        if (StringUtil.equals(BatchDictCodeList.批处理启动模式_自动.getValue(),
                batchExecuteInfo.getStartMode())) {
            batchDate = platformDateService.getSysDate();
        }

        try {
            cfetsAutoDurDealProcessorService.execute(batchDate, this.getProductIdList());
            result.setStatus(BatchStatus.COMPLETED);
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("存续期自动生成批处理报错:{}",e.getMessage());
            //异常信息记录到批处理的日志详情
            RiskUtil.getLogString(e);
        }

        return result;
    }
}
