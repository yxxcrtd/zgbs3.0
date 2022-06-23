package com.joyintech.tams.model.impairment.batch.common;

import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.tams.common.base.entity.ModBatchLogs;
import com.joyintech.tams.common.base.entity.ModValBatchParams;
import com.joyintech.tams.valuation.common.batch.batchTasks.CommonErrorParams;
import com.joyintech.tams.valuation.component.batch.service.ModLogsService;
import com.joyintech.tams.valuation.component.batch.service.ModParamsService;
import com.joyintech.tams.valuation.component.common.constant.ValComponentConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Scope("prototype")
public class LossModelCommonListener implements JobExecutionListener {

    @Autowired
    private ModParamsService modParamsService;

    @Autowired
    private ModLogsService modLogsService;

    private SysBatchExecInfo batchExecuteInfo;

    public SysBatchExecInfo getBatchExecuteInfo() {
        return batchExecuteInfo;
    }

    public void setBatchExecuteInfo(SysBatchExecInfo batchExecuteInfo) {
        this.batchExecuteInfo = batchExecuteInfo;
    }

    @Override
    public void beforeJob(JobExecution jobExecution) {
        batchExecuteInfo.getExecuteDate();
        // 查询参数
        List<ModValBatchParams> params = modParamsService.selectByBatchExecSeqno(batchExecuteInfo.getBatchExedetailSeqNo());
        if (CollectionUtils.isNotEmpty(params)) {
            Map<String, ModBatchLogs> logsMap = new HashedMap<>();
            params.stream().forEach(x->{
                ModBatchLogs log = modLogsService.initLog(batchExecuteInfo.getTaskId(),batchExecuteInfo.getBatchExedetailSeqNo(),x.getFinprodId());
                logsMap.put(x.getFinprodId(),log);
            });
            jobExecution.getExecutionContext().put(ValComponentConstant.MODEL_LOGS,logsMap);
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        Map<String,CommonErrorParams> logs = (Map<String,CommonErrorParams>)jobExecution.getExecutionContext().get(ValComponentConstant.MODEL_LOGS);
        if(logs != null && logs.size() > 0){
            modLogsService.saveBatch(new ArrayList(logs.values()));
        }
    }
}
