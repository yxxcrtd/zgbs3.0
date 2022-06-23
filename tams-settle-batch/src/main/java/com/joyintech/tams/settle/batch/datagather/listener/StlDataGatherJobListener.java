package com.joyintech.tams.settle.batch.datagather.listener;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.SettleBatchResultMapper;
import com.joyintech.tams.common.base.entity.SettleBatchPara;
import com.joyintech.tams.common.base.entity.SettleBatchResultWithBLOBs;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListStl;
import com.joyintech.tams.settle.batch.datadeal.service.StlDownDataDealReaderService;
import com.joyintech.tams.settle.batch.datagather.service.StlDataGatherService;
import com.joyintech.tools.collectors.JoyinCollectors;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author 何阳阳
 * @date 2020/8/11
 * @see StlDataGatherJobListener
 * @since 1.0
 */
@Component
public class StlDataGatherJobListener implements JobExecutionListener {

    @Autowired
    private SettleBatchResultMapper settleBatchResultMapper;

    @Autowired
    private StlDataGatherService stlDataGatherService;

    @Autowired
    private StlDownDataDealReaderService stlDownDataDealService;

    @Override
    public void beforeJob(JobExecution jobExecution) {
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        Date dealDate = jobExecution.getJobParameters().getDate("date");

        // 日志处理
        long bondSuccessCount=0,bondReadCount=0,fundSuccessCount=0,fundReadCount=0,repoSuccessCount=0,repoReadCount=0,sharesSuccessCount=0,sharesReadCount=0;
        for(StepExecution stepExecution : jobExecution.getStepExecutions()) {
            String stepName = stepExecution.getStepName();
            if(StringUtil.startsWith(stepName, "bond")) {
                // 债券
                bondReadCount += stepExecution.getReadCount();
                bondSuccessCount += stepExecution.getWriteCount();
            }
            else if(StringUtil.startsWith(stepName, "fund")) {
                // 基金
                fundReadCount += stepExecution.getReadCount();
                fundSuccessCount += stepExecution.getWriteCount();
            }
            else if(StringUtil.startsWith(stepName, "repo")) {
                // 回购
                repoReadCount += stepExecution.getReadCount();
                repoSuccessCount += stepExecution.getWriteCount();
            }
            else if(StringUtil.startsWith(stepName, "shares")) {
                // 股票
                sharesReadCount += stepExecution.getReadCount();
                sharesSuccessCount += stepExecution.getWriteCount();
            }
        }
        List<SettleBatchPara> list = stlDownDataDealService.selectAllParam(dealDate);
        if(CollectionUtils.isNotEmpty(list)) {
            Map<String, List<SettleBatchPara>> paramMap = list.stream().collect(JoyinCollectors.groupingBy(SettleBatchPara::getSecType));
            Iterator<String> iterator = paramMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if(StringUtil.equals(DictCodeListExt1.证券品种_债券.getValue(), key)) {
                    saveLogBySecType(paramMap.get(key).get(0).getSeqno(), new Date(), bondReadCount, bondSuccessCount);
                }
                else if(StringUtil.equals(DictCodeListExt1.证券品种_基金.getValue(), key)) {
                    saveLogBySecType(paramMap.get(key).get(0).getSeqno(), new Date(), fundReadCount, fundSuccessCount);
                }
                else if(StringUtil.equals(DictCodeListExt1.证券品种_股票.getValue(), key)) {
                    saveLogBySecType(paramMap.get(key).get(0).getSeqno(), new Date(), sharesReadCount, sharesSuccessCount);
                }
                else if(StringUtil.equals(DictCodeListExt1.证券品种_回购.getValue(), key)) {
                    saveLogBySecType(paramMap.get(key).get(0).getSeqno(), new Date(), repoReadCount, repoSuccessCount);
                }
            }
        }
        // 删除批处理参数
        stlDataGatherService.deleteParam(dealDate);
    }

    private void saveLogBySecType(String seqNo, Date endDate, long readCount, long writeCount) {
        SettleBatchResultWithBLOBs batchResult = settleBatchResultMapper.selectByPrimaryKey(seqNo);
        if(null != batchResult) {
            String preResult = batchResult.getTaskResult();

            batchResult.setSeqno(seqNo);
            batchResult.setEndTime(endDate);
            if(!StringUtil.equals(preResult, DictCodeListStl.批处理执行结果_异常结束.getValue()) && (readCount > 0 && writeCount == 0)) {
                batchResult.setTaskResult(DictCodeListStl.批处理执行结果_异常结束.getValue());
                batchResult.setFailureMsg("共读取数据"+readCount+"条，失败" + readCount + "条，成功0条");
            }
            else if(StringUtil.equals(preResult, DictCodeListStl.批处理执行结果_正常结束.getValue()) && (readCount == writeCount)) {
                batchResult.setTaskResult(DictCodeListStl.批处理执行结果_正常结束.getValue());
                batchResult.setSuccessMsg("共读取数据"+readCount+"条，失败0条，成功" + writeCount + "条");
            }
            else {
                batchResult.setTaskResult(DictCodeListStl.批处理执行结果_警告.getValue());
                batchResult.setSuccessMsg("共读取数据"+readCount+"条，失败" + (readCount - writeCount) + "条，成功" + writeCount + "条");
            }
            settleBatchResultMapper.updateByPrimaryKeySelective(batchResult);
        }
    }
}
