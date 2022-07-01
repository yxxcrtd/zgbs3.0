/*
 * 文件名：SubjectBalanceCollectWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：weipeng
 * 修改时间：2018年5月28日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.valuation.batch.repassetindex.writer;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.tams.common.base.dao.RepAssetIndexMapper;
import com.joyintech.tams.common.base.entity.RepAssetIndex;
import com.joyintech.tams.valuation.batch.repassetindex.dto.RepAssetIndexDtoResult;


/**
 * 资产指标计算step2Writer
 * 
 * @author dongxiangyang
 * @version 1.0 2018年12月21日
 * @see RepAssetIndexWriter
 * @since 1.0
 */
@Service
public class RepAssetIndexWriter extends BaseWriter<NeedToWriteEntity>{
    
    /**
     * 资产指标计算mapper
     */
    @Autowired
    private RepAssetIndexMapper repAssetIndexMapper;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends NeedToWriteEntity> items) throws Exception {
        //获取process返回组合的list值 - 记账凭证list
        List<RepAssetIndexDtoResult> resultList = (List<RepAssetIndexDtoResult>) items;
        
        //循环科目余额list，对每个记账凭证进行操作
        for(RepAssetIndexDtoResult result : resultList){
            //入库资产指标表
            List<RepAssetIndex> listBatches = result.getListRepAssetIndex();
            if(CollectionUtils.isNotEmpty(listBatches)){
                listBatches.forEach(item ->{
                    //循环录入库中
                	repAssetIndexMapper.insert(item);
                });
            }
            
        }
        
    }

}
