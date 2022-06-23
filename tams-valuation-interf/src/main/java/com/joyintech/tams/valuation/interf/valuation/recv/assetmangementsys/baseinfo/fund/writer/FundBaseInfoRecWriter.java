/*
 * 文件名：FundBaseInfoRecWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：HuangJian
 * 修改时间：2018年6月26日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.fund.writer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.entity.FinCashInfo;
import com.joyintech.tams.common.base.entity.FinCashPayRule;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.FinProductAdd;
import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCashInfoBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCashPayRuleBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCashPlanBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductAddBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductTypeBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValInterflogBatchMapper;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.fund.dto.FundBaseInfoForInterfRecProcessorResult;

/**
 * 基金类资产 FundBaseInfoRecWriter
 * 
 * @author HuangJian
 * @version 1.0 2018年6月26日
 * @see FundBaseInfoRecWriter
 * @since 1.0
 */
@Service
@Scope("prototype")
public class FundBaseInfoRecWriter extends InterfBaseWriter<BaseEntity> {

    /**
     * 标的金融产品
     */
    @Autowired
    private ValFinProductBatchMapper valFinProductBatchMapper;

    /**
     * 标的金融产品扩展
     */
    @Autowired
    private ValFinProductAddBatchMapper valFinProductExtBatchMapper;

    /**
     * 金融产品分类
     */
    @Autowired
    private ValFinProductTypeBatchMapper valFinProductTypeBatchMapper;

    /**
     * 现金流信息
     */
    @Autowired
    private ValCashInfoBatchMapper valCashInfoBatchMapper;

    /**
     * 现金流支付规则
     */
    @Autowired
    private ValCashPayRuleBatchMapper valCashPayRuleBatchMapper;

    /**
     * 现金流计划
     */
    @Autowired
    private ValCashPlanBatchMapper valCashPlanBatchMapper;

    /**
     * 日志
     */
    @Autowired
    private ValInterflogBatchMapper valInterflogBatchMapper;

    
    @Value("${interf.properties.maxSqlNum}")
    private int maxSqlNum;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends BaseEntity> items)
        throws Exception {
        List<FundBaseInfoForInterfRecProcessorResult> resultList = (List<FundBaseInfoForInterfRecProcessorResult>)items;
        for (FundBaseInfoForInterfRecProcessorResult result : resultList) {
            // 标的金融产品
            List<FinProduct> listValFinProduct = result.getListValFinProduct();
            // 入库
            if (CollectionUtils.isNotEmpty(listValFinProduct)) {
            //    valFinProductBatchMapper.insertBatch(listValFinProduct);
                //定义一个固定大小插入存放集合
                List<FinProduct> listInsert =  new ArrayList<FinProduct>();
                for(FinProduct e :listValFinProduct){
                   //小于最大值
                    if(listInsert.size() < maxSqlNum){
                        listInsert.add(e); 
                    }else if(listInsert.size() >= maxSqlNum){
                        valFinProductBatchMapper.deleteBatch(listInsert); 
                        valFinProductBatchMapper.insertBatch(listInsert); 
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                //如果循环结束，小于最大值
                if(CollectionUtils.isNotEmpty(listInsert)){
                    valFinProductBatchMapper.deleteBatch(listInsert); 
                    valFinProductBatchMapper.insertBatch(listInsert);
                }

            }
            // 标的金融产品扩展
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
                            valFinProductExtBatchMapper.deleteBatch(listInsert); 
                            valFinProductExtBatchMapper.insertBatch(listInsert); 
                            listInsert.clear();
                            listInsert.add(e);
                        }
                    }
                    //如果循环结束，小于最大值
                    if(CollectionUtils.isNotEmpty(listInsert)){
                        valFinProductExtBatchMapper.deleteBatch(listInsert); 
                        valFinProductExtBatchMapper.insertBatch(listInsert);
                    }
            }
            // 金融产品分类
            List<FinProductType> listValFinProductType = result.getListValFinProductType();
            // 入库
            if (CollectionUtils.isNotEmpty(listValFinProductType)) {
                //定义一个固定大小插入存放集合
                List<FinProductType> listInsert =  new ArrayList<FinProductType>();
                for(FinProductType e :listValFinProductType){
                   //小于最大值
                    if(listInsert.size() < maxSqlNum){
                        listInsert.add(e); 
                    }else if(listInsert.size() >= maxSqlNum){
                        valFinProductTypeBatchMapper.deleteBatch(listInsert); 
                        valFinProductTypeBatchMapper.insertBatch(listInsert); 
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                //如果循环结束，小于最大值
                if(CollectionUtils.isNotEmpty(listInsert)){
                    valFinProductTypeBatchMapper.deleteBatch(listInsert); 
                    valFinProductTypeBatchMapper.insertBatch(listInsert);
                }
            }
            // 现金流计划
            List<FinCashPlan> listValCashPlan = result.getListValCashPlan();
            // 入库
            if (CollectionUtils.isNotEmpty(listValCashPlan)) {
            	/**add by wujie on 20190107 start**/
                List<FinCashPlan> delFinCashPlanList = new ArrayList<FinCashPlan>();
                Map<String,FinCashPlan> delFinCashPlanMap = new HashMap<String,FinCashPlan>();
                for(FinCashPlan obj :listValCashPlan){
                	delFinCashPlanMap.put(obj.getCashId(), obj);
                }
                for(String key :delFinCashPlanMap.keySet()){
	            	delFinCashPlanList.add(delFinCashPlanMap.get(key));
	            }
                valCashPlanBatchMapper.deleteFinCashPlanByCashId(delFinCashPlanList);
                /**add by wujie on 20190107 end**/
            	
              //定义一个固定大小插入存放集合
                List<FinCashPlan> listInsert =  new ArrayList<FinCashPlan>();
                for(FinCashPlan e :listValCashPlan){
                   //小于最大值
                    if(listInsert.size() < maxSqlNum){
                        listInsert.add(e); 
                    }else if(listInsert.size() >= maxSqlNum){
                    	/**modify by wujie on 20190107 
                        valCashPlanBatchMapper.deleteBatch(listInsert); **/
                        valCashPlanBatchMapper.insertBatch(listInsert); 
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                //如果循环结束，小于最大值
                if(CollectionUtils.isNotEmpty(listInsert)){
                	/**modify by wujie on 20190107 
                    valCashPlanBatchMapper.deleteBatch(listInsert); **/
                    valCashPlanBatchMapper.insertBatch(listInsert);
                }
            }
            
            // 现金流信息
            List<FinCashInfo> listValCashInfo = result.getListValCashInfo();
            // 入库
            if (CollectionUtils.isNotEmpty(listValCashInfo)) {
                //定义一个固定大小插入存放集合
                List<FinCashInfo> listInsert =  new ArrayList<FinCashInfo>();
                for(FinCashInfo e :listValCashInfo){
                   //小于最大值
                    if(listInsert.size() < maxSqlNum){
                        listInsert.add(e); 
                    }else if(listInsert.size() >= maxSqlNum){
                        valCashInfoBatchMapper.deleteBatch(listInsert); 
                        valCashInfoBatchMapper.insertBatch(listInsert); 
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                //如果循环结束，小于最大值
                if(CollectionUtils.isNotEmpty(listInsert)){
                    valCashInfoBatchMapper.deleteBatch(listInsert); 
                    valCashInfoBatchMapper.insertBatch(listInsert);
                }
            }
            
            // 现金流支付规则
            List<FinCashPayRule> listValCashPayRule = result.getListValCashPayRule();
            // 入库
            if (CollectionUtils.isNotEmpty(listValCashPayRule)) {
                //定义一个固定大小插入存放集合
                List<FinCashPayRule> listInsert =  new ArrayList<FinCashPayRule>();
                for(FinCashPayRule e :listValCashPayRule){
                   //小于最大值
                    if(listInsert.size() < maxSqlNum){
                        listInsert.add(e); 
                    }else if(listInsert.size() >= maxSqlNum){
                        valCashPayRuleBatchMapper.deleteBatch(listInsert); 
                        valCashPayRuleBatchMapper.insertBatch(listInsert); 
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                //如果循环结束，小于最大值
                if(CollectionUtils.isNotEmpty(listInsert)){
                    valCashPayRuleBatchMapper.deleteBatch(listInsert); 
                    valCashPayRuleBatchMapper.insertBatch(listInsert);
                }
            }
            // 日志
            List<ValInterflog> listValInterflog = result.getValInterflog();
            // 入库
            if (CollectionUtils.isNotEmpty(listValInterflog)) {

                //定义一个固定大小插入存放集合
                  List<ValInterflog> listInsert =  new ArrayList<>();
                  for(ValInterflog e :listValInterflog){
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

}
