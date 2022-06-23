package com.joyintech.tams.model.impairment.batch.eclcalculation.writer;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.model.impairment.batch.common.utils.DataHandleUtil;
import com.joyintech.tams.model.impairment.batch.eclcalculation.dao.EclCalculationMapper;
import com.joyintech.tams.model.impairment.batch.eclcalculation.dto.EclCalculationResultDto;
import com.joyintech.tams.common.base.entity.ModConfigUseage;
import com.joyintech.tams.common.base.entity.ModEclResult;
import com.joyintech.tams.model.impairment.batch.impairmentdataload.dao.ImpairmentDataLoadMapper;
import com.joyintech.tams.common.base.entity.ModEclResultProcess;
import com.joyintech.tams.valuation.common.batch.losscommon.dao.LossCommonMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class EclCalculationWriter extends BaseWriter<EclCalculationResultDto> {
    /**
     * Mapper
     */
    @Autowired
    private EclCalculationMapper eclCalculationMapper;
    @Autowired
    private ImpairmentDataLoadMapper impairmentDataLoadMapper;
    @Autowired
    private LossCommonMapper lossCommonMapper;

    @Override
    public void write(List<? extends EclCalculationResultDto> items) throws Exception {
        List<EclCalculationResultDto> resultList = (List<EclCalculationResultDto>) items;
        List<ModConfigUseage> listModConfigUseage = new ArrayList<ModConfigUseage>();
        List<ModEclResultProcess> listModEclResultProcess = new ArrayList<ModEclResultProcess>();
        List<ModEclResult> listModEclResult = new ArrayList<ModEclResult>();
        if (resultList != null && resultList.size() > 0) {
            for (EclCalculationResultDto dto : resultList) {
                // 减值结果过程表
                listModEclResultProcess.addAll(dto.getModEclResultProcessList());
                // 减值配置使用情况表
                listModEclResult.addAll(dto.getModEclResultList());
                // 减值配置使用情况表
                if (CollectionUtils.isNotEmpty(dto.getModConfigUseageList())) {
                    for(ModConfigUseage item : dto.getModConfigUseageList()){
                        if(StringUtil.isNotEmpty(item.getSeqno())){
                            listModConfigUseage.add(item);
                        }
                    }
                }
            }
            insertModConfigUseage(listModConfigUseage);
            insertModEclResultProcess(listModEclResultProcess);
            insertModEclResult(listModEclResult);
        }
    }

    /**
     * 插入结果集
     * @param listModEclResult
     */
    private void insertModEclResult(List<ModEclResult> listModEclResult) {
        if (listModEclResult == null || listModEclResult.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<ModEclResult>> list = DataHandleUtil.splitList(listModEclResult);
        for (List<ModEclResult> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                eclCalculationMapper.insertModEclResult(listInsert);
            }
        }
    }

    /**
     * 插入结果集
     * @param listModConfigUseage
     */
    private void insertModConfigUseage(List<ModConfigUseage> listModConfigUseage) {
        if (listModConfigUseage == null || listModConfigUseage.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<ModConfigUseage>> list = DataHandleUtil.splitList(listModConfigUseage);
        for (List<ModConfigUseage> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                impairmentDataLoadMapper.insertModConfigUseage(listInsert);
            }
        }
    }

    /**
     * 插入结果集
     * @param listModEclResultProcess
     */
    private void insertModEclResultProcess(List<ModEclResultProcess> listModEclResultProcess) {
        if (listModEclResultProcess == null || listModEclResultProcess.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<ModEclResultProcess>> list = DataHandleUtil.splitList(listModEclResultProcess);
        for (List<ModEclResultProcess> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                lossCommonMapper.insertModEclResultProcessBatch(listInsert);
            }
        }
    }

}
