package com.joyintech.tams.model.impairment.batch.datapusheclresult.service;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.tams.common.base.entity.ModBatchLogs;
import com.joyintech.tams.common.base.entity.ModValBatchParams;
import com.joyintech.tams.constant.ModelDictCodeList;
import com.joyintech.tams.model.impairment.batch.datapusheclresult.dao.DataPushEclResultMapper;
import com.joyintech.tams.model.impairment.batch.datapusheclresult.dto.AssetLossResult;
import com.joyintech.tams.model.impairment.batch.datapusheclresult.dto.AssetLossResultReaderDto;
import com.joyintech.tams.valuation.component.batch.service.ModLogsService;
import com.joyintech.tams.valuation.component.batch.service.ModParamsService;
import com.joyintech.tams.valuation.component.common.constant.ValComponentConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.core.StepExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Description:数据推送批处理Service
 * @auther: yuzj
 * @date: 2021/7/6
 * @param:
 * @return:
 */
@Service
public class DataPushEclResultReaderService {
    @Autowired
    private DataPushEclResultMapper dataPushEclResultMapper;
    @Autowired
    DataPushEclResultProcessService dataPushEclResultProcessService;
    @Autowired
    ModParamsService modParamsService;
    @Autowired
    private ModLogsService modLogsService;
    /**
     * @Description:获取待处理资产信息List
     * @auther: yuzj
     * @date: 2021/7/5
     * @param:
     * @return:
     */
    public List<AssetLossResultReaderDto> execute(Map<String, Object> mapParam, StepExecution stepExecution) {
        List<AssetLossResultReaderDto> assetLossResultReaderList = new ArrayList<>();
        Date plateDate = (Date)mapParam.get("PLATE_DATE");//平台日
        String taskId =(String)mapParam.get("TASK_ID");
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)mapParam.get("BATCH_INFO");
        List<ModValBatchParams> listBatch = modParamsService.selectByBatchExecSeqno(batchExecuteInfo.getBatchExedetailSeqNo());
        //1.读取需要进行处理的资产
        //资产代码集合
        List<String> finprodIdList = new ArrayList<String>();
        if (listBatch != null && listBatch.size() > 0) {
            for (ModValBatchParams params1 : listBatch) {
                finprodIdList.add(params1.getFinprodId());
            }
        }
        List<AssetLossResult> assetLossResultList =dataPushEclResultMapper.selectEclResultList(plateDate,finprodIdList);
        for(AssetLossResult assetLossResult:assetLossResultList){
            AssetLossResultReaderDto assetLossResultReaderDto = new AssetLossResultReaderDto();
            assetLossResultReaderDto.setAssetLossResult(assetLossResult);
            assetLossResultReaderList.add(assetLossResultReaderDto);
        }
        if(CollectionUtils.isEmpty(assetLossResultReaderList)){
            finprodIdList.forEach(x->{
                modLogsService.error((Map<String, ModBatchLogs>)stepExecution.getJobExecution().getExecutionContext().get(ValComponentConstant.MODEL_LOGS),x, "【数据推送批处理】：批处理执行错误.");
            });
        }
        return assetLossResultReaderList;
    }




}
