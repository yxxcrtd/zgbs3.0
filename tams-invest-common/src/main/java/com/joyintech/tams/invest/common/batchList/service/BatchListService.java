/*
 * 文件名：BatchListService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2019年9月5日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.invest.common.batchList.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.redis.msg.entity.MessageBody.Operation;
import com.joyintech.tams.invest.common.batchList.dao.BatchListMapper;

/**
 * 批量存储数据库Service
 * 
 * @author zhangrui1
 * @version 1.0 2019年9月5日
 * @see BatchListService
 * @since 1.0
 */
@Service
public class BatchListService<V> {

    private ThreadLocal<BatchListMapper<V>> batchListMapper = new ThreadLocal<>();

    /**
     * 批量登录
     * 
     * @param batchListMapper 批量登录Mapper
     * @param infoList 登录数据列表
     * @param offset 分批登录数量
     * @see
     * @since 1.0
     */
    @Transactional(rollbackFor = Throwable.class)
    public <T> void insertBatchList(BatchListMapper<T> batchListMapper, List<T> infoList,
                                    int offset) {
        if (!StringUtil.isEmptyList(infoList)) {
            int size = infoList.size();
            int tail = size % offset;
            if (tail != 0) {
                batchListMapper.insertBatchList(infoList.subList(0, tail));
            }
            int len = size / offset;
            for (int i = 0; i < len; i++ ) {
                int start = tail + i * offset;
                batchListMapper.insertBatchList(infoList.subList(start, start + offset));
            }
        }
    }

    /**
     * 批量插入
     * 
     * @param infoList 登录数据列表
     * @param offset 分批登录数量
     * @see
     * @since 1.0
     */
    @Transactional(rollbackFor = Throwable.class)
    public void insertBatchList(List<V> infoList, int offset) {
        insertBatchList(batchListMapper.get(), infoList, offset);
    }

    /**
     * 批量插入
     * 
     * @param infoList 登录数据列表
     * @see
     * @since 1.0
     */
    @Transactional(rollbackFor = Throwable.class)
    public void insertBatchList(List<V> infoList) {
        // 每500条数据登陆一次数据库
        insertBatchList(infoList, 500);
    }

    /**
     * {@inheritDoc}
     */
    @Transactional(rollbackFor = Throwable.class)
    public void consume(Operation opr, Collection<V> vList) {
        if (opr == Operation.W && !CollectionUtils.isEmpty(vList)) {
            List<V> infoList = new ArrayList<>();
            vList.forEach(info -> {
                if (info != null) {
                    infoList.add(info);
                }
            });
            // 每500条数据登陆一次数据库
            insertBatchList(infoList);
        }
    }

    /**
     * 获取 batchListMapper
     * 
     * @return batchListMapper.
     */
    public BatchListMapper<V> getBatchListMapper() {
        return batchListMapper.get();
    }

    /**
     * 设置 batchListMapper
     * 
     * @param batchListMapper batchListMapper
     */
    public void setBatchListMapper(BatchListMapper<V> batchListMapper) {
        this.batchListMapper.set(batchListMapper);
    }


    public void unload() {
        this.batchListMapper.remove(); // Compliant
    }
}
