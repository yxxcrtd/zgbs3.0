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

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.tams.common.base.entity.BokBatchTaskTodo;
import com.joyintech.tams.valuation.batch.batchdispatch.dao.BatchDispatchMapper;
import com.joyintech.tams.valuation.batch.batchdispatch.dto.BatchDispatchProcessorDto;
import com.joyintech.tams.valuation.common.utils.DataHandleUtil;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author wangzhiang
 * @version 1.0 2018年4月27日
 * @see BatchDispatchWriter
 * @since 1.0
 */
@Service
@Scope("prototype")
public class BatchDispatchWriter extends BaseWriter<NeedToWriteEntity> {

    /**
     * batchExecuteDetailMapper
     */
    @Autowired
    BatchDispatchMapper batchDispatchMapper;
    
    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends NeedToWriteEntity> list)
        throws Exception {
        if (list == null || list.isEmpty()) {
            return;
        }
        List<BatchDispatchProcessorDto> dtoList = (List<BatchDispatchProcessorDto>) list;
        List<BokBatchTaskTodo> todoList = new ArrayList<BokBatchTaskTodo>();
        dtoList.forEach(x->{
            todoList.addAll(x.getBokBatchTaskTodoList());
        });
        insertBokBatchTaskTodo(todoList);
        //batchDispatchMapper.insertBokBatchTaskTodo(todoList);
    }

    /**
     * <一句话功能简述>
     * <功能详细描述>
     * @param batchJobList
     * @see
     * @since 1.0
     */
    private void insertBokBatchTaskTodo(List<BokBatchTaskTodo> todoList) {
        if (todoList == null || todoList.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<BokBatchTaskTodo>> list = DataHandleUtil.splitList(todoList);
        for (List<BokBatchTaskTodo> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                batchDispatchMapper.insertBokBatchTaskTodo(listInsert);
            }
        }
        
//        List<BokBatchTaskTodo> listInsert = new ArrayList<>();
//        todoList.forEach(x -> {
//            // 小于最大值
//            if (listInsert.size() < CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                listInsert.add(x);
//            }
//            else if (listInsert.size() >= CacheUtil.getAppCache(GlobalConstant.MAX_SQL_NUM, Integer.class)) {
//                batchDispatchMapper.insertBokBatchTaskTodo(listInsert);
//                listInsert.clear();
//                listInsert.add(x);
//            }
//        });
//        // 如果循环结束，小于最大值
//        if (CollectionUtils.isNotEmpty(listInsert)) {
//            batchDispatchMapper.insertBokBatchTaskTodo(listInsert);
//        }
    }
}
