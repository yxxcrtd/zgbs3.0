/*
 * 文件名：ValCronJob.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：王波
 * 修改时间：2019年2月13日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.invest.batch.common.service;

import com.joyintech.batch.constant.BatchConstant;
import com.joyintech.batch.core.JoyinBatchQuartzSkipConfigInfo;
import com.joyintech.batch.core.dto.SysBatchQuartzConfigDto;
import com.joyintech.batch.service.BatchCoreService;
import com.joyintech.batch.service.ServerDoubleAliveService;
import com.joyintech.framework.base.entity.SysBatchJob;
import com.joyintech.framework.base.entity.SysBatchgrpExecInfo;
import com.joyintech.framework.constant.BatchDictCodeList;
import com.joyintech.framework.jdbc.datasource.DynamicDataSourceHolder;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.framework.util.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.invest.batch.common.dao.BatchDispatchMapper;
import com.joyintech.tams.invest.batch.common.dao.ValBatchCronJobMapper;
import com.joyintech.tams.invest.batch.common.dto.BatchGrpPri;
import com.joyintech.tams.invest.batch.common.dto.DimensionValue;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * 自定义批处理定时任务实现
 *
 * @author 王波
 * @version 1.0 2019年2月13日
 * @see TgCronJob
 * @since 1.0
 */
public class TgCronJob implements Job {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final BatchCoreService batchCoreService = SpringContextUtil.getBean(BatchCoreService.class);

    private final String enableServerDoubleAlive = SpringContextUtil.getTextValue("batch.properties.enableServerDoubleAlive");

    private final String serverName = SpringContextUtil.getTextValue("batch.properties.serverName");

    private final String sysCode = SpringContextUtil.getTextValue("batch.properties.sysCode");

    private final ServerDoubleAliveService serverDoubleAliveService = SpringContextUtil.getBean(ServerDoubleAliveService.class);

    @Override
    public void execute(JobExecutionContext context)
            throws JobExecutionException {

        if (Boolean.parseBoolean(enableServerDoubleAlive)) {
            if (!serverDoubleAliveService.selectMinPriAllDown(serverName, sysCode)) {
                // 没有全部宕机时，本机不处理
                logger.debug("比当前服务器："+serverName + ", 优先级高的服务器没有全部宕机，本机不处理。");
                return;
            }
        }

        List<SysBatchgrpExecInfo> listSysBatchgrpExecInfo = batchCoreService
                .findAllUnrunDetail(BatchDictCodeList.批处理执行状态_未执行.getValue(), "VAL");
        if (listSysBatchgrpExecInfo.size() > 0) {
            // 有未执行的任务待处理
            LoggerFactory.getLogger(this.getClass()).error("有未执行的任务待处理!");
            return;
        }

        listSysBatchgrpExecInfo = batchCoreService
                .findAllUnrunDetail(BatchDictCodeList.批处理执行状态_执行中.getValue(), "VAL");
        if (listSysBatchgrpExecInfo.size() > 0) {
            // 有未执行的任务待处理
            LoggerFactory.getLogger(this.getClass()).error("有执行中的任务!");
            return;
        }

        // 任务组ID
        String taskGrpId = (String) context.getMergedJobDataMap().get("taskGrpId");
        // 产品组ID
        String prdGrpId = (String) context.getMergedJobDataMap().get("prdGrpId");
        // 批详细名称
        String detailName = (String) context.getMergedJobDataMap().get("detailName");
        // 查询任务组所有任务
        List<String> tasks = selectGroupBySeqNo(taskGrpId);

        // 产品列表
        List<String> prodList = findAllProdbyGroup(prdGrpId);

        SysBatchgrpExecInfo executeDetail = new SysBatchgrpExecInfo();

        // 明细Seqno
        String detailSeqNo = IDGenerator.generate();
        // 任务实例
        List<SysBatchJob> insertJobInstList = new ArrayList<>();
        // 任务组优先级
        List<BatchGrpPri> insertGrpPriList = new ArrayList<>();
        // 维度值
        List<DimensionValue> insertDimValueList = new ArrayList<>();
        // 日期维度列表
        List<Date> dates = new ArrayList<>();
        //当前日期
        ValBatchCronJobMapper valBatchCronJobMapper = SpringContextUtil.getBean(ValBatchCronJobMapper.class);
        Date nowDate = valBatchCronJobMapper.getCommonSysDate();

        // etliu 批处理定时任务支持跳过节假日 20200116 modify start
        // 当配置的系统参数（是否放开支持批处理定时任务跳过节假日功能）的值为01:支持 时
        if (StringUtil.equals(com.joyintech.framework.constant.DictCodeList.是否_是.getValue(), CacheUtil.getSysParam(BatchConstant.BATCH_QUARTZ_SKIP_HOLIDAY))) {
            String batchQuartzConfigId = (String)context.getMergedJobDataMap().get("batchQuartzConfigId");
            if (StringUtil.isNotBlank(batchQuartzConfigId)) {
                // 根据定时任务代码，重新查询跳过节假日相关信息
                SysBatchQuartzConfigDto configAddIndo = batchCoreService.selectSkipHolidayInfo(batchQuartzConfigId);
                if (configAddIndo != null) {
//        			String isSkipHoliday = (String)context.getMergedJobDataMap().get("isSkipHoliday");
//                	String marketCalendarType = (String)context.getMergedJobDataMap().get("marketCalendarType");
                    String isSkipHoliday = configAddIndo.getIsSkipHoliday();
                    String marketCalendarType = configAddIndo.getMarketCalendarType();
                    // 批处理定时任务支持跳过节假日相关接口
                    JoyinBatchQuartzSkipConfigInfo joyinBatchQuartzSkipConfigInfo = null;
                    try {
                        joyinBatchQuartzSkipConfigInfo = SpringContextUtil.getBean(JoyinBatchQuartzSkipConfigInfo.class);
                    } catch (Exception ignored) {
                        ignored.printStackTrace();
                    }
                    if (StringUtil.equals(com.joyintech.framework.constant.DictCodeList.是否_是.getValue(), isSkipHoliday)
                            && joyinBatchQuartzSkipConfigInfo != null
                            && joyinBatchQuartzSkipConfigInfo.isSkip(marketCalendarType, nowDate)) {
                        logger.debug("当前定时任务配置的是跳过节假日，当前日期为节假日，本次定时任务跳过");
                        return;
                    }
                }
            }
        }
        // etliu 批处理定时任务支持跳过节假日 20200116 modify end


        // 跑当日批
        dates.add(nowDate);
        dates.forEach(x->{
            // 任务实例
            List<SysBatchJob> jobInstList = new ArrayList<>();
            // 任务的Seqno
            String batchGroupSeqNoInst = IDGenerator.generate();
            tasks.forEach(taskId -> {
                SysBatchJob job = new SysBatchJob();
                job.setBatchSeqNo(batchGroupSeqNoInst);
                job.setTaskId(taskId);
                jobInstList.add(job);
            });
            insertJobInstList.addAll(jobInstList);
            // 任务组优先级
            BatchGrpPri batchGrpPri = new BatchGrpPri(detailSeqNo, batchGroupSeqNoInst, x, BigDecimal.ZERO);
            insertGrpPriList.add(batchGrpPri);
            // 维度值
            DimensionValue dimensionValue = new DimensionValue(batchGroupSeqNoInst, "1", DateUtil.format(x));
            insertDimValueList.add(dimensionValue);
        });
        // 维度批处理必备参数
        insertDimensionTask(insertJobInstList, insertGrpPriList, insertDimValueList);

        executeDetail.setSystemCode("VAL");
        executeDetail.setSeqNo(detailSeqNo);
        executeDetail.setBatchSeqNo(taskGrpId);
        executeDetail.setPrdGroupSeqNo(prdGrpId);
        executeDetail.setStartDate(nowDate);
        executeDetail.setEndDate(nowDate);
        executeDetail.setStatus(BatchDictCodeList.批处理执行状态_未执行.getValue());
        executeDetail.setDetailName("任务组名称："+detailName+", 启动时间:" + DateUtil.format(nowDate, "yyyy-MM-dd HH:mm:ss.SSS"));
        // 是否日切:是
        executeDetail.setOtherParam1(DictCodeList.是否_是.getValue());
        // 估值分组
        executeDetail.setOtherParam3(executeDetail.getSeqNo());
        batchCoreService.insertBatchgrpExecInfo(executeDetail);
    }

    /**
     * 维度批处理必备参数 入库
     * @param jobInstList
     * @param batchGrpPriList
     * @param dimValueList
     */
    private void insertDimensionTask(List<SysBatchJob> jobInstList, List<BatchGrpPri> batchGrpPriList,
                                     List<DimensionValue> dimValueList) {
        DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
        try {
            BatchDispatchMapper batchDispatchMapper =  SpringContextUtil.getBean(BatchDispatchMapper.class);
            batchDispatchMapper.insertBatchJob(jobInstList);
            batchDispatchMapper.insertBatchGrpPri(batchGrpPriList);
            batchDispatchMapper.insertDimValue(dimValueList);
        } catch (Exception e) {
            LoggerFactory.getLogger(this.getClass()).error("", e);
        } finally {
            DynamicDataSourceHolder.clearDataSource();
        }
    }

    /**
     *  查询任务组的任务
     * @param taskGrpId 任务组ID
     * @return
     */
    private List<String> selectGroupBySeqNo(String taskGrpId) {
        DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
        ValBatchCronJobMapper valBatchCronJobMapper = SpringContextUtil.getBean(ValBatchCronJobMapper.class);
        List<String> res  = valBatchCronJobMapper.findAllTaskbyGroup(taskGrpId);
        DynamicDataSourceHolder.clearDataSource();
        return res;
    }

    /**
     *  查询产品组所有产品
     * @param prdGrpId 产品组ID
     * @return
     */
    private List<String> findAllProdbyGroup(String prdGrpId) {
        DynamicDataSourceHolder.putDataSource(DataSourceType.OTHER);
        ValBatchCronJobMapper valBatchCronJobMapper = SpringContextUtil.getBean(ValBatchCronJobMapper.class);
        List<String> res = valBatchCronJobMapper.findAllProdbyGroup(prdGrpId);
        DynamicDataSourceHolder.clearDataSource();
        return res;
    }
}
