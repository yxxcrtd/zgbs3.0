package com.joyintech.tams.model.impairment.batch.eadcalculation.writer;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.ModValBatchParams;
import com.joyintech.tams.model.impairment.batch.common.utils.DataHandleUtil;
import com.joyintech.tams.model.impairment.batch.eadcalculation.dto.EadCalCulationResultDto;
import com.joyintech.tams.model.impairment.batch.eadcalculation.dto.EadModEclResultProcess;
import com.joyintech.tams.valuation.common.batch.losscommon.dao.LossCommonMapper;
import com.joyintech.tams.common.base.entity.ModEclResultProcess;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * EAD计算执行写入数据
 * @author zhangfei
 * @date 2021年7月16日16:08:17
 */
@Service
@Scope("prototype")
public class EadCalCulationBatchWriter extends BaseWriter<NeedToWriteEntity> implements StepExecutionListener {

    private StepExecution stepExecution;

    @Autowired
    private LossCommonMapper lossCommonMapper;
    @Override
    public void write(List<? extends NeedToWriteEntity> items) throws Exception {
        List<EadCalCulationResultDto> resultList = (List<EadCalCulationResultDto>)items;
        List<ModValBatchParams> batchParams = new ArrayList<ModValBatchParams>();// (List<ModValBatchParams>)this.stepExecution.getJobExecution().getExecutionContext().get("params");
        List<EadModEclResultProcess> writerList = new ArrayList<EadModEclResultProcess>();
        SysBatchExecInfo batchExecuteInfo  = (SysBatchExecInfo)this.getParams().get("BATCH_INFO");
        String seqNo = batchExecuteInfo.getBatchExedetailSeqNo();
        if(!StringUtil.isEmptyList(resultList)){
            for(EadCalCulationResultDto item : resultList){
                ModValBatchParams dto = new ModValBatchParams();
                dto.setFinprodId(item.getFinprodId());
                dto.setBatchExecSeqno(seqNo);
                batchParams.add(dto);
                writerList.addAll(item.getEadResultList());
            }
            //批处理日志数据处理
            batchParams = batchParams.stream().collect(
                    Collectors.toMap(ModValBatchParams::getFinprodId, Function.identity(), (e1, e2) -> e2)
            ).values().stream().collect(Collectors.toList());
            insertModEclResultProcess(writerList);
            this.stepExecution.getJobExecution().getExecutionContext().put("params",batchParams);
        }
    }

    /**
     * 插入结果集
     * @param listModEclResultProcess
     */
    private void insertModEclResultProcess(List<EadModEclResultProcess> listModEclResultProcess) {
        if (listModEclResultProcess == null || listModEclResultProcess.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<ModEclResultProcess> listProcess = new ArrayList<ModEclResultProcess>();
        for(EadModEclResultProcess item : listModEclResultProcess){
            ModEclResultProcess process = new ModEclResultProcess();
            BeanUtil.copyPropertiesIgnoreNull(process, item);
            listProcess.add(process);
        }
        List<List<ModEclResultProcess>> list = DataHandleUtil.splitList(listProcess);
        for (List<ModEclResultProcess> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                lossCommonMapper.insertModEclResultProcessBatch(listInsert);
            }
        }
    }
    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }
}
