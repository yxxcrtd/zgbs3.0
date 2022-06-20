/*
 * 文件名：SecPosAutoGenerationTask.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：刘二通
 * 修改时间：2018年11月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.batch.autogeneration.secpos.runner;

import com.joyintech.batch.batchbase.baseclass.BatchBaseService;
import com.joyintech.batch.batchbase.exception.BatchException;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.constant.BatchDictCodeList;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.web.PlatformDate;
import com.joyintech.tams.invest.batch.autogeneration.secpos.service.SecPosAutoGenerationService;
import com.joyintech.tams.invest.batch.valrift.service.ValRiftService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 组合持仓分析报表自动生成批处理
 *
 * @author nixiukuan
 * @version 1.0 2019年12月13日
 * @see SecReportPosAutoGenerationTask
 * @since 1.0
 */
@Service("secReportPosAutoGenerationTask")
@Scope("prototype")
public class SecReportPosAutoGenerationTask extends BatchBaseService {

    /**
     * 日志
     */
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * SecPosAutoGenerationService
     */
    @Autowired
    private SecPosAutoGenerationService secPosAutoGenerationService;

    /**
     * PlatformDate
     */
    @Autowired(required = false)
    private PlatformDate platformDateService;
    /**
     * valRiftService
     */
    @Autowired(required = false)
    private ValRiftService valRiftService;

    /**
     * {@inheritDoc}
     */
    @Override
    public JobExecution execute() throws BatchException {
        logger.info("=========secReportPosAutoGenerationTask========start===========");
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
            //add by lpf for 组合持仓报表批处理改造 S 将产品分为指定日期范围内的导入估值表 和没导入估值表的产品
            //add by lpf for 估值穿透改造 S 将产品分为指定日期范围内的导入估值表 和没导入估值表的产品
            String deptCode = null;
            if (MultilevelDataAuthorityUtil.isEnabled()) {
                deptCode = batchExecuteInfo.getCreateUser() != null ? MultilevelDataAuthorityUtil.getOrganizationCode(batchExecuteInfo.getCreateUser()) : null;
            }
            List<Map<String, Object>> importValTableProdList = valRiftService.selectImportValTableProdList(batchDate, this.getProductIdList(), deptCode);
            List<String> importProdList = new ArrayList<>();
            List<String> unImportProdList = new ArrayList<>();
            Date minDate = batchDate;
            if (!StringUtil.isEmptyList(importValTableProdList)) {
                for (Map<String, Object> entry : importValTableProdList) {
                    //导入了估值表的产品寻找最小跑批日
                    if (StringUtil.isNotEmpty(entry.get("valDate"))) {
                        if (minDate.compareTo((Date) entry.get("valDate")) > 0) {
                            minDate = (Date) entry.get("valDate");
                        }
                        importProdList.add((String) entry.get("finprodId"));
                    } else {
                        unImportProdList.add((String) entry.get("finprodId"));
                    }
                }
            }

            //未导入估值表的产品仅跑跑批日当日
            if (!StringUtil.isEmptyList(unImportProdList)) {
                secPosAutoGenerationService.autoSecReportGeneration(batchDate, unImportProdList);
            }
            //导入估值表的产品循环最小跑批日到当前跑批日
            if (!StringUtil.isEmptyList(importProdList)) {
                Date currentDate = minDate;
                for (int i = 0; i <= DateUtil.getBetweenDayNumber(minDate, batchDate); i++) {
                    secPosAutoGenerationService.autoSecReportGeneration(currentDate, importProdList);
                    currentDate = DateUtil.addDay(currentDate, 1);
                }
            }
            //add by lpf for 组合持仓报表批处理改造 E
//            secPosAutoGenerationService.autoSecReportGeneration(batchDate, this.getProductIdList(),batchExecuteInfo.getStartMode());
            result.setStatus(BatchStatus.COMPLETED);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        logger.info("=========secReportPosAutoGenerationTask========end===========");
        return result;
    }
}
