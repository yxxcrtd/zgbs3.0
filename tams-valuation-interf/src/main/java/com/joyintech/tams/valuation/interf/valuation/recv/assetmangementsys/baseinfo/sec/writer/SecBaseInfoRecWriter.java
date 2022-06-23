/*
 * 文件名：SecBaseInfoRecWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：HuangJian
 * 修改时间：2018年6月28日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.sec.writer;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.*;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.sec.dto.SecBaseInfoRecProcessorResult;
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
 * 基础数据接口--债券
 * 
 * @author HuangJian
 * @version 1.0 2018年6月28日
 * @see SecBaseInfoRecWriter
 * @since 1.0
 */
@Service
@Scope("prototype")
public class SecBaseInfoRecWriter extends InterfBaseWriter<BaseEntity>{
    
    
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
     * 现金流信息表
     */
    @Autowired
    private ValCashInfoBatchMapper  valCashInfoBatchMapper;
    /**
     * 现金流计算规则表
     */
    @Autowired
    private ValCashCalcRuleBatchMapper valCashCalcRuleBatchMapper;
    /**
     * 现金流支付规则表
     */
    @Autowired
    private ValCashPayRuleBatchMapper valCashPayRuleBatchMapper;
    /**
     * 现金流计划表
     */
    @Autowired
    private ValCashPlanBatchMapper valCashPlanBatchMapper;
    
    /**
     * 行权计划表
     */
    @Autowired
    private ValExercisePlanBatchMapper  valExercisePlanBatchMapper;
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
     * 现金流利率信息表
     */
    @Autowired
    private ValCashIntInfoBatchMapper valCashIntInfoBatchMapper;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends BaseEntity> items)
        throws Exception {
        List<SecBaseInfoRecProcessorResult> resultList = (List<SecBaseInfoRecProcessorResult>)items;
        for (SecBaseInfoRecProcessorResult result : resultList) {
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
                        //modify by yanya on 20210828 增加转债信息的删除
                        valFinProductBatchMapper.deleteConvertibleBatch(listInsert);
                        valFinProductBatchMapper.insertBatch(listInsert);

                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valFinProductBatchMapper.deleteBatch(listInsert);
                    //modify by yanya on 20210828 增加转债信息的删除
                    valFinProductBatchMapper.deleteConvertibleBatch(listInsert);
                    valFinProductBatchMapper.insertBatch(listInsert);
                }
            }

            /** 标的类金融产品扩展表 add by yanya on 20210318 start**/
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
            /** 标的类金融产品扩展表 add by yanya on 20210318 end**/

            //modify by yanya on 20210829 增加转债信息 start
            List<FinProductConvertible> listFinProductConvertible = result.getListFinProductConvertible();
            Map<String, FinProductConvertible> maplistFinProductConvertible = new HashMap<String, FinProductConvertible>();
            List<FinProductConvertible> rstlistFinProductConvertible = new ArrayList<FinProductConvertible>();
            for(FinProductConvertible obj :listFinProductConvertible){
                if(maplistFinProductConvertible.get(obj.getFinprodId()) == null){
                    rstlistFinProductConvertible.add(obj);
                }
                maplistFinProductConvertible.put(obj.getFinprodId(), obj);
            }
            // 入库
            if (CollectionUtils.isNotEmpty(rstlistFinProductConvertible)) {
                // 定义一个固定大小插入存放集合
                List<FinProductConvertible> listInsert = new ArrayList<FinProductConvertible>();
                for (FinProductConvertible e : rstlistFinProductConvertible) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valFinProductBatchMapper.insertConvertibleBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valFinProductBatchMapper.insertConvertibleBatch(listInsert);
                }
            }
            //modify by yanya on 20210829 增加转债信息 end
            
            // 金融产品分类
            List<FinProductType> listValFinProductType = result.getListValFinProductType();
            Map<String,FinProductType> mapValFinProductType = new HashMap<String,FinProductType>();
            List<FinProductType>   rstValFinProductType= new ArrayList<FinProductType>();
//            for(FinProductType obj :listValFinProductType){
//                if(mapValFinProductType.get(obj.getFinprodId()+obj.getFinprodType()) ==null){
//                    rstValFinProductType.add(obj);
//                }
//                mapValFinProductType.put(obj.getFinprodId()+obj.getFinprodType(), obj);
//            }
            
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
            
//            modify by wujie on 20190121
//            /**add by wujie on 20180815 start remark:含权债初始化现金流信息**/
//            Map<String,Object> paramMap = result.getParamMap();
//            if(paramMap!=null && paramMap.get("listValCashInfo_no") !=null){
//            	List<FinCashInfo> listValCashInfo_no = (List<FinCashInfo>)paramMap.get("listValCashInfo_no");
//                // 入库
//                if (CollectionUtils.isNotEmpty(listValCashInfo_no)) {
//                    // 定义一个固定大小插入存放集合
//                    List<FinCashInfo> listInsert = new ArrayList<FinCashInfo>();
//                    for (FinCashInfo e : listValCashInfo_no) {
//                        // 小于最大值
//                        if (listInsert.size() < maxSqlNum) {
//                            listInsert.add(e);
//                        }
//                        else if (listInsert.size() >= maxSqlNum) {
//                            valCashInfoBatchMapper.deleteBatch(listInsert);
//                            listInsert.clear();
//                            listInsert.add(e);
//                        }
//                    }
//                    // 如果循环结束，小于最大值
//                    if (CollectionUtils.isNotEmpty(listInsert)) {
//                        valCashInfoBatchMapper.deleteBatch(listInsert);
//                    }
//                }
//            }
//            /**add by wujie on 20180815 end**/
            
            
            // 现金流信息表
            List<FinCashInfo> listValCashInfo = result.getListValCashInfo();
            if(listValCashInfo!=null && listValCashInfo.size()>0){
            	Map<String,FinCashInfo> mapValCashInfo = new HashMap<String,FinCashInfo>();
                List<FinCashInfo> rstValCashInfo= new ArrayList<FinCashInfo>();
                for(FinCashInfo obj :listValCashInfo){
                    if(mapValCashInfo.get(obj.getCashId()) ==null){
                        rstValCashInfo.add(obj);
                    }
                    mapValCashInfo.put(obj.getCashId(), obj);
                }
                // 入库
                if (CollectionUtils.isNotEmpty(rstValCashInfo)) {
                    // 定义一个固定大小插入存放集合
                    List<FinCashInfo> listInsert = new ArrayList<FinCashInfo>();
                    for (FinCashInfo e : rstValCashInfo) {
                        // 小于最大值
                        if (listInsert.size() < maxSqlNum) {
                            listInsert.add(e);
                        }
                        else if (listInsert.size() >= maxSqlNum) {
                            valCashInfoBatchMapper.deleteBatch(listInsert);
                            valCashInfoBatchMapper.insertBatch(listInsert);
                            listInsert.clear();
                            listInsert.add(e);
                        }
                    }
                    // 如果循环结束，小于最大值
                    if (CollectionUtils.isNotEmpty(listInsert)) {
                        valCashInfoBatchMapper.deleteBatch(listInsert);
                        valCashInfoBatchMapper.insertBatch(listInsert);
                    }
                }
            }
            // 现金流支付规则表
            List<FinCashPayRule> listValCashPayRule = result.getListValCashPayRule();
            Map<String,FinCashPayRule> mapValCashPayRule = new HashMap<String,FinCashPayRule>();
            List<FinCashPayRule> rstValCashPayRule= new ArrayList<FinCashPayRule>();
            for(FinCashPayRule obj :listValCashPayRule){
                if(mapValCashPayRule.get(obj.getCashId()+obj.getEffDate()) ==null){
                    rstValCashPayRule.add(obj);
                }
                mapValCashPayRule.put(obj.getCashId()+obj.getEffDate(), obj);
            }
            // 入库
            if (CollectionUtils.isNotEmpty(rstValCashPayRule)) {
                // 定义一个固定大小插入存放集合
                List<FinCashPayRule> listInsert = new ArrayList<FinCashPayRule>();
                for (FinCashPayRule e : rstValCashPayRule) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valCashPayRuleBatchMapper.deleteBatch(listInsert);
                        valCashPayRuleBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valCashPayRuleBatchMapper.deleteBatch(listInsert);
                    valCashPayRuleBatchMapper.insertBatch(listInsert);
                }
            }

          // 现金流计算规则表
          List<FinCashCalcRule> listValCashCalcRule = result.getListValCashCalcRule();
          Map<String,FinCashCalcRule> mapValCashCalcRule = new HashMap<String,FinCashCalcRule>();
          List<FinCashCalcRule> rstValCashCalcRule= new ArrayList<FinCashCalcRule>();
          for(FinCashCalcRule obj :listValCashCalcRule){
              if(mapValCashCalcRule.get(obj.getCashId()+obj.getEffDate()) ==null){
                  rstValCashCalcRule.add(obj);
              }
              mapValCashCalcRule.put(obj.getCashId()+obj.getEffDate(), obj);
          }
          // 入库
          if (CollectionUtils.isNotEmpty(rstValCashCalcRule)) {
              // 定义一个固定大小插入存放集合
              List<FinCashCalcRule> listInsert = new ArrayList<FinCashCalcRule>();
              for (FinCashCalcRule e : rstValCashCalcRule) {
                  // 小于最大值
                  if (listInsert.size() < maxSqlNum) {
                      listInsert.add(e);
                  }
                  else if (listInsert.size() >= maxSqlNum) {
                      valCashCalcRuleBatchMapper.deleteBatch(listInsert);
                      valCashCalcRuleBatchMapper.insertBatch(listInsert);
                      listInsert.clear();
                      listInsert.add(e);
                  }
              }
              // 如果循环结束，小于最大值
              if (CollectionUtils.isNotEmpty(listInsert)) {
                  valCashCalcRuleBatchMapper.deleteBatch(listInsert);
                  valCashCalcRuleBatchMapper.insertBatch(listInsert);
              }
          }
                      
            // 现金流计划表
            List<FinCashPlan> listValCashPlan = result.getListValCashPlan();
            // 入库
            if (CollectionUtils.isNotEmpty(listValCashPlan)) {
            	Map<String,FinCashPlan> mapValCashPlan = new HashMap<String,FinCashPlan>();
                List<FinCashPlan> rstValCashPlan= new ArrayList<FinCashPlan>();
                for(FinCashPlan obj :listValCashPlan){
                    if(mapValCashPlan.get(obj.getCashId()+obj.getCashNum()) ==null){
                        rstValCashPlan.add(obj);
                    }
                    mapValCashPlan.put(obj.getCashId()+obj.getCashNum(), obj);
                }
            	/**add by wujie on 20181207 start remark:债券现金流计划表删除逻辑调整**/
            	Map<String,FinCashPlan> paramsMap = new HashMap<String,FinCashPlan>();
                List<FinCashPlan> delList = new ArrayList<FinCashPlan>();
                for(FinCashPlan obj :rstValCashPlan){
                	if(paramsMap.get(obj.getCashId())==null){
                		delList.add(obj);
                	}
                	paramsMap.put(obj.getCashId(), obj);
                }
                valCashPlanBatchMapper.deleteFinCashPlanByCashId(delList);
                /**add by wujie on 20181207 end remark:债券现金流计划表删除逻辑调整**/
                // 定义一个固定大小插入存放集合
                List<FinCashPlan> listInsert = new ArrayList<FinCashPlan>();
                for (FinCashPlan e : rstValCashPlan) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
//                        valCashPlanBatchMapper.deleteBatch(listInsert);//modify by wujie on 20181207 remark：债券现金流计划表删除逻辑调整
                        valCashPlanBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
//                    valCashPlanBatchMapper.deleteBatch(listInsert);//modify by wujie on 20181207 remark：债券现金流计划表删除逻辑调整
                    valCashPlanBatchMapper.insertBatch(listInsert);
                }
            }

            /** modify by yanya on 20210318 现金流利率信息表 start**/
            List<FinRateInfo> listValCashIntInfo = result.getListValCashIntInfo();
            Map<String,FinRateInfo> mapValCashIntInfo = new HashMap<String, FinRateInfo>();
            List<FinRateInfo> rstValCashIntInfo = new ArrayList<FinRateInfo>();
            for(FinRateInfo obj :listValCashIntInfo){
                if(mapValCashIntInfo.get(obj.getCashId()+ obj.getEffDate()) == null){
                    rstValCashIntInfo.add(obj);
                }
                mapValCashIntInfo.put(obj.getCashId()+obj.getEffDate(), obj);
            }
            // 入库
            if (CollectionUtils.isNotEmpty(rstValCashIntInfo)) {
                // 定义一个固定大小插入存放集合
                List<FinRateInfo> listInsert = new ArrayList<FinRateInfo>();
                for (FinRateInfo e : rstValCashIntInfo) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valCashIntInfoBatchMapper.deleteBatch(listInsert);
                        valCashIntInfoBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valCashIntInfoBatchMapper.deleteBatch(listInsert);
                    valCashIntInfoBatchMapper.insertBatch(listInsert);
                }
            }
            /** modify by yanya on 20210318 现金流利率信息表 end**/

            //行权计划表
            List<FinExercisePlan> listValExercisePlan = result.getListValExercisePlan();
            Map<String,FinExercisePlan> mapValExercisePlan = new HashMap<String,FinExercisePlan>();
            List<FinExercisePlan> rstValExercisePlan = new ArrayList<FinExercisePlan>();
            /**add by wujie on 20190121 start**/
            List<FinExercisePlan> dellist = new ArrayList<FinExercisePlan>();
            Map<String,FinExercisePlan> delMap = new HashMap<String,FinExercisePlan>();
            /**add by wujie on 20190121 end**/
            for(FinExercisePlan obj :listValExercisePlan){
            	if(mapValExercisePlan.get(obj.getCashId()+obj.getExerciseType()+obj.getExerciseDate())==null){//modify by dongjun on 20181217
            		rstValExercisePlan.add(obj);           		
            	}
            	/**add by wujie on 20190121 start**/
            	mapValExercisePlan.put(obj.getCashId()+obj.getFinprodId(),obj);
            	if(delMap.get(obj.getFinprodId()+obj.getFinprodType())==null){
            		dellist.add(obj);
            	}
            	delMap.put(obj.getFinprodId()+obj.getFinprodType(), obj);
            	/**add by wujie on 20190121 end**/
            }
            // 入库
            if (CollectionUtils.isNotEmpty(rstValExercisePlan)) {
            	/**add by wujie on 20190121 start**/
            	valExercisePlanBatchMapper.deleteByFinprodId(dellist);
            	/**add by wujie on 20190121 end**/
            	
                // 定义一个固定大小插入存放集合
                List<FinExercisePlan> listInsert = new ArrayList<FinExercisePlan>();
                for (FinExercisePlan e : rstValExercisePlan) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                    	/**modify by wujie on 20190121
                        valExercisePlanBatchMapper.deleteBatch(listInsert);
                        **/
                        valExercisePlanBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                	/**modify by wujie on 20190121
                    valExercisePlanBatchMapper.deleteBatch(listInsert);
                    **/
                    valExercisePlanBatchMapper.insertBatch(listInsert);
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
