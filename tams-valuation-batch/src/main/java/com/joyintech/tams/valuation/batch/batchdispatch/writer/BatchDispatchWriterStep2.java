/*
 * 文件名：BatchDispatchWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangzhiang
 * 修改时间：2018年4月27日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.batchdispatch.writer;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.framework.base.entity.SysBatchJob;
import com.joyintech.framework.jdbc.datasource.DataSource;
import com.joyintech.framework.jdbc.enums.DataSourceType;
import com.joyintech.tams.valuation.batch.batchdispatch.dao.BatchDispatchMapper;
import com.joyintech.tams.valuation.batch.batchdispatch.dto.BatchDispatchProcessorDto;
import com.joyintech.tams.valuation.batch.batchdispatch.dto.BatchGrpPri;
import com.joyintech.tams.valuation.batch.batchdispatch.dto.DimensionDto;
import com.joyintech.tams.valuation.batch.batchdispatch.dto.DimensionValue;
import com.joyintech.tams.valuation.common.utils.DataHandleUtil;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author wangzhiang
 * @version 1.0 2018年4月27日
 * @see BatchDispatchWriterStep2
 * @since 1.0
 */
@Service
@Scope("prototype")
public class BatchDispatchWriterStep2 extends BaseWriter<NeedToWriteEntity> {

    /**
     * batchExecuteDetailMapper
     */
    @Autowired
    BatchDispatchMapper batchDispatchMapper;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @DataSource(DataSourceType.OTHER)
    @Override
    public void write(List<? extends NeedToWriteEntity> list)
        throws Exception {
        if (list == null || list.isEmpty()) {
            return;
        }
        List<BatchDispatchProcessorDto> dtoList = (List<BatchDispatchProcessorDto>)list;
        List<DimensionValue> dimValueList = new ArrayList<>();
        List<BatchGrpPri> batchGrpPriList = new ArrayList<>();
        List<SysBatchJob> batchJobList = new ArrayList<>();
        for (BatchDispatchProcessorDto dto : dtoList) {
            if (dto == null) {
                continue;
            }
            for (DimensionDto dimDto : dto.getDimensionDtoList()) {
                if (dimDto.getBatchGrpPri() != null) {
                    batchGrpPriList.add(dimDto.getBatchGrpPri());
                }
                if (dimDto.getBatchJobList() != null) {
                    batchJobList.addAll(dimDto.getBatchJobList());
                }
                if (dimDto.getDimValueList() != null) {
                    dimValueList.addAll(dimDto.getDimValueList());
                }
            }
        }
        insertDimValue(dimValueList);
        // batchDispatchMapper.insertDimValue(dimValueList);
        
        insertBatchGrpPri(batchGrpPriList);
        // batchDispatchMapper.insertBatchGrpPri(batchGrpPriList);
        
        insertBatchJob(batchJobList);
        // batchDispatchMapper.insertBatchJob(batchJobList);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param batchJobList
     * @see
     * @since 1.0
     */
    private void insertBatchJob(List<SysBatchJob> batchJobList) {
        if (batchJobList == null || batchJobList.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<SysBatchJob>> list = DataHandleUtil.splitList(batchJobList);
        for (List<SysBatchJob> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                batchDispatchMapper.insertBatchJob(listInsert);
            }
        }
//        List<SysBatchJob> listInsert = new ArrayList<>();
//        batchJobList.forEach(x -> {
//            // 小于最大值
//            if (listInsert.size() < CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                listInsert.add(x);
//            }
//            else if (listInsert.size() >= CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                batchDispatchMapper.insertBatchJob(listInsert);
//                listInsert.clear();
//                listInsert.add(x);
//            }
//        });
//        // 如果循环结束，小于最大值
//        if (CollectionUtils.isNotEmpty(listInsert)) {
//            batchDispatchMapper.insertBatchJob(listInsert);
//        }
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param batchGrpPriList
     * @see
     * @since 1.0
     */
    private void insertBatchGrpPri(List<BatchGrpPri> batchGrpPriList) {
        if (batchGrpPriList == null || batchGrpPriList.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<BatchGrpPri>> list = DataHandleUtil.splitList(batchGrpPriList);
        for (List<BatchGrpPri> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                batchDispatchMapper.insertBatchGrpPri(listInsert);
            }
        }
//        List<BatchGrpPri> listInsert = new ArrayList<>();
//        batchGrpPriList.forEach(x -> {
//            // 小于最大值
//            if (listInsert.size() < CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                listInsert.add(x);
//            }
//            else if (listInsert.size() >= CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                batchDispatchMapper.insertBatchGrpPri(listInsert);
//                listInsert.clear();
//                listInsert.add(x);
//            }
//        });
//        // 如果循环结束，小于最大值
//        if (CollectionUtils.isNotEmpty(listInsert)) {
//            batchDispatchMapper.insertBatchGrpPri(listInsert);
//        }
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param dimValueList
     * @see
     * @since 1.0
     */
    private void insertDimValue(List<DimensionValue> dimValueList) {
        if (dimValueList == null || dimValueList.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<DimensionValue>> list = DataHandleUtil.splitList(dimValueList);
        for (List<DimensionValue> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                batchDispatchMapper.insertDimValue(listInsert);
            }
        }
//        List<DimensionValue> listInsert = new ArrayList<>();
//        dimValueList.forEach(x -> {
//            // 小于最大值
//            if (listInsert.size() < CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                listInsert.add(x);
//            }
//            else if (listInsert.size() >= CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                batchDispatchMapper.insertDimValue(listInsert);
//                listInsert.clear();
//                listInsert.add(x);
//            }
//        });
//        // 如果循环结束，小于最大值
//        if (CollectionUtils.isNotEmpty(listInsert)) {
//            batchDispatchMapper.insertDimValue(listInsert);
//        }
    }

}
