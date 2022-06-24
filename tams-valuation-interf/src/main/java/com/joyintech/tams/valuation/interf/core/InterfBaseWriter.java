/*
 * 文件名：InterfBaseWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：王波
 * 修改时间：2018年6月11日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.valuation.interf.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValInterflogBatchMapper;
import com.joyintech.tams.common.base.entity.ValInterflog;


public abstract class InterfBaseWriter<T> implements ItemWriter<T> {
    
    @Value("${interf.properties.maxSqlNum}")
    private int maxSqlNum;
    /**
     * 参数
     */
    private Map<String, Object> params;
    
    /**
     * 日志
     */
    @Autowired
    private ValInterflogBatchMapper valInterflogBatchMapper;
    
    
    /**
     * 获取 params
     * @return params.
     */
    public Map<String, Object> getParams() {
        return params;
    }

    
    /**
     * 设置 params
     * @param params params
     */
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
    
    /**
     * 日志批量入库
     * @param valInterflog
     */
    public void insertValInterflog(List<ValInterflog> valInterflog){
        //判断是否非空
        if(CollectionUtils.isNotEmpty(valInterflog)){
            //定义一个固定大小插入存放集合
               List<ValInterflog> listInsert =  new ArrayList<>();
               for(ValInterflog e :valInterflog){
                  //小于最大值
                   if(listInsert.size() < maxSqlNum){
                       listInsert.add(e); 
                   }else if(listInsert.size() >= maxSqlNum){
                       valInterflogBatchMapper.insertBatch(listInsert); 
                       listInsert.clear();
                       listInsert.add(e);
                   }
               }
               //如果循环结束，小于最大值
               if(CollectionUtils.isNotEmpty(listInsert)){
                   valInterflogBatchMapper.insertBatch(listInsert);
               }
        }
    }
}
