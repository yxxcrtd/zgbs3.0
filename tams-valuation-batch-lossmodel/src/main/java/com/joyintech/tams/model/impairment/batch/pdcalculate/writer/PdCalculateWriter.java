/*
 * 文件名：ValuationBatchApplication.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：何阳阳
 * 修改时间：2018年5月21日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.model.impairment.batch.pdcalculate.writer;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.ModelDictCodeList;
import com.joyintech.tams.model.impairment.batch.common.utils.DataHandleUtil;
import com.joyintech.tams.model.impairment.batch.pdcalculate.dao.PdCalculateMapper;
import com.joyintech.tams.model.impairment.batch.pdcalculate.dto.AssetImpairmentPdCalculateResultDto;
import com.joyintech.tams.model.impairment.batch.pdcalculate.dto.ModConfigUseageResultDto;
import com.joyintech.tams.model.impairment.batch.pdcalculate.service.PdCalculateProcessService;
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
import java.util.Map;

/**
 * @Description:PD计算批处理Writer
 * @auther: yuzj
 * @date: 2021/7/19
 * @param:
 * @return:
 */
@Service
@Scope("prototype")
public class PdCalculateWriter extends BaseWriter<AssetImpairmentPdCalculateResultDto> implements StepExecutionListener {
    private final String BATCH_PARAMS_KEY = "params";

    private StepExecution stepExecution;


	@Autowired
	private PdCalculateMapper pdCalculateMapper;

    @Autowired
    private PdCalculateProcessService pdCalculateProcessService;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution=stepExecution;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

    @Autowired
    private LossCommonMapper lossCommonMapper;
    /**
     * {@inheritDoc}
     */
    @Override
    public void write(List<? extends AssetImpairmentPdCalculateResultDto> items)
        throws Exception {
        Map<String,Object> mapParam = this.getParams();
        SysBatchExecInfo batchExecuteInfo = (SysBatchExecInfo)mapParam.get("BATCH_INFO");
        List<AssetImpairmentPdCalculateResultDto> resultList = (List<AssetImpairmentPdCalculateResultDto>)items;
        List<ModEclResultProcess> modEclResultProcessResultList = new ArrayList<ModEclResultProcess>();
        List<ModConfigUseageResultDto> modConfigUseageResultList = new ArrayList<ModConfigUseageResultDto>();
        String taskId =(String)mapParam.get("TASK_ID");
        try {
            if (!StringUtil.isEmptyList(resultList)) {
                for (AssetImpairmentPdCalculateResultDto resultDto : resultList) {
                    if (resultDto.getModEclResultProcessResultList() != null) {
                        //批量处理
                        modEclResultProcessResultList.addAll(resultDto.getModEclResultProcessResultList());
                    }
                    if (resultDto.getModConfigUseageResultList() != null) {
                        //批量处理
                        modConfigUseageResultList.addAll(resultDto.getModConfigUseageResultList());
                    }
                }
                //减值结果过程表批处理
                if (modEclResultProcessResultList != null && modEclResultProcessResultList.size() > 0) {
                    insertEclProcessResultPremium(modEclResultProcessResultList, batchExecuteInfo);
                }
                //减值配置使用情况表批处理
                if (modConfigUseageResultList != null && modConfigUseageResultList.size() > 0) {
                    insertConfigUseageResultPremium(modConfigUseageResultList, batchExecuteInfo);
                }
//                if(!org.springframework.util.CollectionUtils.isEmpty(modEclResultProcessResultList)){
//                    //=====记录成功日志=====start=====
//                    for(ModEclResultProcess resultDto :modEclResultProcessResultList){
//                        ModValBatchLogResult batchParam = new ModValBatchLogResult();
//                        batchParam.setFinprodId(resultDto.getProdId());
//                        batchParam.setTaskId(taskId);
//                        batchParam.setBatchExecSeqno(batchExecuteInfo.getBatchExedetailSeqNo());
//                        batchParam.setType(ModelDictCodeList.模型计算结果_成功.getValue());
//                        batchParam.setMsg("执行批处理【" + resultDto.getProdId()+ "】成功结束！");
//                        bBatchLogResultList.add(batchParam);
//                    }
//                    pdCalculateProcessService.sendlogResults(bBatchLogResultList,stepExecution);
//                    //=====记录成功日志=====end=====
//                }
            }
        } catch (Exception e) {
            //=====记录失败日志=====start=====
//            for(ModEclResultProcess resultDto :modEclResultProcessResultList){
//                ModValBatchLogResult batchParam = new ModValBatchLogResult();
//                batchParam.setFinprodId(resultDto.getProdId());
//                batchParam.setTaskId(taskId);
//                batchParam.setBatchExecSeqno(batchExecuteInfo.getBatchExedetailSeqNo());
//                batchParam.setType(ModelDictCodeList.模型计算结果_失败.getValue());
//                batchParam.setMsg("【PD计算批处理】：计算错误");
//                bBatchLogResultList.add(batchParam);
//            }
//            pdCalculateProcessService.sendlogResults(bBatchLogResultList,stepExecution);
            //=====记录失败日志=====end=====
        }


    }


    /**
     * 插入减值结果过程表结果集
     * @param EclProcessResultList
     */
    private void insertEclProcessResultPremium(List<ModEclResultProcess> EclProcessResultList,SysBatchExecInfo batchExecuteInfo) {
        if (EclProcessResultList == null || EclProcessResultList.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<ModEclResultProcess>> list = DataHandleUtil.splitList(EclProcessResultList);
        for (List<ModEclResultProcess> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                lossCommonMapper.insertModEclResultProcessBatch(listInsert);
            }
        }

    }




    /**
     * 插入资产定价结果集
     * @param modConfigUseageResultList
     */
    private void insertConfigUseageResultPremium(List<ModConfigUseageResultDto> modConfigUseageResultList,SysBatchExecInfo batchExecuteInfo ) {
        if (modConfigUseageResultList == null || modConfigUseageResultList.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<ModConfigUseageResultDto>> list = DataHandleUtil.splitList(modConfigUseageResultList);
        for (List<ModConfigUseageResultDto> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                pdCalculateMapper.insertConfigUseageResultPremium(listInsert);
            }
        }
    }




}