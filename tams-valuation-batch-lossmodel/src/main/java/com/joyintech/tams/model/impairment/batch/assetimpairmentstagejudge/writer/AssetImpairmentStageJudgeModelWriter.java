package com.joyintech.tams.model.impairment.batch.assetimpairmentstagejudge.writer;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.tams.constant.ModelDictCodeList;
import com.joyintech.tams.model.impairment.batch.assetimpairmentstagejudge.dto.AssetImpairmentStageJudgeModelResultDto;
import com.joyintech.tams.model.impairment.batch.common.utils.DataHandleUtil;
import com.joyintech.tams.valuation.common.batch.losscommon.dao.LossCommonMapper;
import com.joyintech.tams.common.base.entity.ModEclResultProcess;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AssetImpairmentStageJudgeModelWriter extends BaseWriter<AssetImpairmentStageJudgeModelResultDto> implements StepExecutionListener {
    /**
     * Mapper
     */
    @Autowired
    private LossCommonMapper lossCommonMapper;

    @Override
    public void write(List<? extends AssetImpairmentStageJudgeModelResultDto> items) throws Exception {
        List<AssetImpairmentStageJudgeModelResultDto> rstList = (List<AssetImpairmentStageJudgeModelResultDto>) items;
        List<ModEclResultProcess> rstProcessList = new ArrayList<ModEclResultProcess>();
        for (AssetImpairmentStageJudgeModelResultDto item : rstList) {
            rstProcessList.add(item.getModEclResultProcess());
        }

        // 插入定义一个固定大小插入存放集合
        List<List<ModEclResultProcess>> list = DataHandleUtil.splitList(rstProcessList);
        try {
            for (List<ModEclResultProcess> listInsert : list) {
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    lossCommonMapper.insertModEclResultProcessBatch(listInsert);
                }
            }
        }catch (Exception e){

        }
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }
}
