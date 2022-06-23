/*
 * 文件名：SecBaseInfoRecWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：HuangJian
 * 修改时间：2018年6月28日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.stock.writer;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.FinProductAdd;
import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductAddBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductTypeBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValInterflogBatchMapper;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.sec.dto.SecBaseInfoRecProcessorResult;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.stock.dto.ValStockBaseRecProcessorResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 基础数据接口--股票
 * 
 * @author yanya
 * @version 1.0 2021-08-27
 * @see ValStockBaseRecWriter
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ValStockBaseRecWriter extends InterfBaseWriter<BaseEntity>{
    
    
    /**
     * 标的类金融产品
     */
    @Autowired
    private ValFinProductBatchMapper valFinProductBatchMapper;
    /**
     * 金融产品分类
     */
    @Autowired
    private ValFinProductTypeBatchMapper valFinProductTypeBatchMapper;

    /**
     * 日志
     */
    @Autowired
    private ValInterflogBatchMapper valInterflogBatchMapper;

    /**
     * 标的类金融产品扩展表
     */
    @Autowired
    private ValFinProductAddBatchMapper valFinProductAddBatchMapper;
    
    @Value("${interf.properties.maxSqlNum}")
    private int maxSqlNum;


    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends BaseEntity> items)
        throws Exception {
        List<ValStockBaseRecProcessorResult> resultList = (List<ValStockBaseRecProcessorResult>)items;
        for (ValStockBaseRecProcessorResult result : resultList) {
        	//标的类金融产品表
            List<FinProduct> listValFinProduct = result.getListValFinProduct();
            Map<String,FinProduct> mapValFinProduct = new HashMap<String,FinProduct>();
            List<FinProduct> rstValFinProduct= new ArrayList<FinProduct>();
            for(FinProduct obj :listValFinProduct){
                if(mapValFinProduct.get(obj.getFinprodId()+obj.getFinprodType()) ==null){
                    rstValFinProduct.add(obj);
                }
                mapValFinProduct.put(obj.getFinprodId()+obj.getFinprodType(), obj);
            }
            // 入库
            if (CollectionUtils.isNotEmpty(rstValFinProduct)) {
                // 定义一个固定大小插入存放集合
                List<FinProduct> listInsert = new ArrayList<FinProduct>();
                for (FinProduct e : rstValFinProduct) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valFinProductBatchMapper.deleteBatch(listInsert);
                        valFinProductBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valFinProductBatchMapper.deleteBatch(listInsert);
                    valFinProductBatchMapper.insertBatch(listInsert);
                }
            }

            List<FinProductAdd> listValFinProductExt = result.getListValFinProductExt();
            // 入库
            if (CollectionUtils.isNotEmpty(listValFinProductExt)) {
                //定义一个固定大小插入存放集合
                List<FinProductAdd> listInsert =  new ArrayList<FinProductAdd>();
                for(FinProductAdd e :listValFinProductExt){
                    //小于最大值
                    if(listInsert.size() < maxSqlNum){
                        listInsert.add(e);
                    }else if(listInsert.size() >= maxSqlNum){
                        valFinProductAddBatchMapper.deleteBatch(listInsert);
                        valFinProductAddBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                //如果循环结束，小于最大值
                if(CollectionUtils.isNotEmpty(listInsert)){
                    valFinProductAddBatchMapper.deleteBatch(listInsert);
                    valFinProductAddBatchMapper.insertBatch(listInsert);
                }
            }
            // 金融产品分类
            List<FinProductType> listValFinProductType = result.getListValFinProductType();
            Map<String,FinProductType> mapValFinProductType = new HashMap<String,FinProductType>();
            List<FinProductType>   rstValFinProductType= new ArrayList<FinProductType>();

            for(FinProductType obj :listValFinProductType){
                if(mapValFinProductType.get(obj.getFinprodId()) ==null){
                    rstValFinProductType.add(obj);
                }
                mapValFinProductType.put(obj.getFinprodId(), obj);
            }
            // 入库
            if (CollectionUtils.isNotEmpty(rstValFinProductType)) {
                // 定义一个固定大小插入存放集合
                List<FinProductType> listInsert = new ArrayList<FinProductType>();
                for (FinProductType e : rstValFinProductType) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valFinProductTypeBatchMapper.deleteBatch(listInsert);
                        valFinProductTypeBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valFinProductTypeBatchMapper.deleteBatch(listInsert);
                    valFinProductTypeBatchMapper.insertBatch(listInsert);
                }
            }

            // 日志
            List<ValInterflog> valInterflog = result.getListValInterflog();
            // 入库
            if (CollectionUtils.isNotEmpty(valInterflog)) {
                // 定义一个固定大小插入存放集合
                List<ValInterflog> listInsert = new ArrayList<>();
                for (ValInterflog e : valInterflog) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valInterflogBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valInterflogBatchMapper.insertBatch(listInsert);
                }
            }
        }
    }

}
