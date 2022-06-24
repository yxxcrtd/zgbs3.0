/*
 * 文件名：CurrentDepositBaseInfoRecWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：qiangchengxi
 * 修改时间：2018年8月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.currentdeposit.writer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinRateInfo;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCashCalcRuleBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCashIntInfoBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValInterflogBatchMapper;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.currentdeposit.dto.CurrentDepositBaseInfoProcessorResult;

@Service
@Scope("prototype")
public class CurrentDepositBaseInfoRecWriter extends InterfBaseWriter<BaseEntity> {

	/**
	 * 现金流计算规则表
	 */
	@Autowired
	private ValCashCalcRuleBatchMapper valCashCalcRuleBatchMapper;

	/**
	 * 现金流利率信息表
	 */
	@Autowired
	private ValCashIntInfoBatchMapper valCashIntInfoBatchMapper;
	
    /**
     * 日志
     */
    @Autowired
    private ValInterflogBatchMapper valInterflogBatchMapper;

    @Value("${interf.properties.maxSqlNum}")
    private int maxSqlNum;
	/**
	 * 更新数据 {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void write(List<? extends BaseEntity> items) throws Exception {
		// 获取process返回组合的list值
		List<CurrentDepositBaseInfoProcessorResult> resultList = (List<CurrentDepositBaseInfoProcessorResult>) items;
		// 循环基金类processor结果DTO，读取所有的入库数据结构LIST，并进行入库操作。
		for (CurrentDepositBaseInfoProcessorResult result : resultList) {
			// 现金流计算规则表
			List<FinCashCalcRule> listValCashCalcRule = result.getListValCashCalcRule();
			Map<String, FinCashCalcRule> mapValCashCalcRule = new HashMap<String, FinCashCalcRule>();
			List<FinCashCalcRule> rstValCashCalcRule = new ArrayList<FinCashCalcRule>();
			for (FinCashCalcRule obj : listValCashCalcRule) {
				if (mapValCashCalcRule.get(obj.getCashId() + obj.getEffDate()) == null) {
					rstValCashCalcRule.add(obj);
				}
				mapValCashCalcRule.put(obj.getCashId() + obj.getEffDate(), obj);
			}
//			// 入库
//			if (CollectionUtils.isNotEmpty(rstValCashCalcRule)) {
//				if (rstValCashCalcRule.size() < maxSqlNum) {
//					valCashCalcRuleBatchMapper.deleteBatch(rstValCashCalcRule);
//					valCashCalcRuleBatchMapper.insertBatch(rstValCashCalcRule);
//				} else {
//					int times = (int) Math.ceil(rstValCashCalcRule.size() / 1000.0);
//					for (int i = 0; i < times; i++) {
//						valCashCalcRuleBatchMapper.deleteBatch(rstValCashCalcRule.subList(i * 1000,
//								Math.min((i + 1) * 1000, rstValCashCalcRule.size() - 1)));
//						valCashCalcRuleBatchMapper.insertBatch(rstValCashCalcRule.subList(i * 1000,
//								Math.min((i + 1) * 1000, rstValCashCalcRule.size() - 1)));
//					}
//				}
//
//			}
			
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
			
			
			//现金流利率信息表
			List<FinRateInfo> listValCashIntInfo = result.getListValCashIntInfo();
			Map<String,FinRateInfo> mapValCashIntInfo = new HashMap<String, FinRateInfo>();
			List<FinRateInfo> rstValCashIntInfo = new ArrayList<FinRateInfo>();
			for(FinRateInfo obj :listValCashIntInfo){
				if(mapValCashIntInfo.get(obj.getCashId()+ obj.getEffDate()) == null){
					rstValCashIntInfo.add(obj);
				}
				mapValCashIntInfo.put(obj.getCashId()+obj.getEffDate(), obj);
			}
//			//入库
//			if(CollectionUtils.isNotEmpty(rstValCashIntInfo)){
//				if(rstValCashIntInfo.size() <1000){
//					valCashIntInfoBatchMapper.deleteBatch(rstValCashIntInfo);
//					valCashIntInfoBatchMapper.insertBatch(rstValCashIntInfo);				
//				}else{
//					int times = (int) Math.ceil(rstValCashCalcRule.size() / 1000.0);
//					for(int i = 0; i < times; i++){
//						valCashIntInfoBatchMapper.deleteBatch(rstValCashIntInfo.subList(i * 1000,
//								Math.min((i + 1) * 1000, rstValCashIntInfo.size() - 1)));
//						valCashIntInfoBatchMapper.insertBatch(rstValCashIntInfo.subList(i * 1000,
//								Math.min((i + 1) * 1000, rstValCashIntInfo.size() - 1)));
//					}
//				}
//			}
			
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
			
			   // 日志
            List<ValInterflog> valInterflog = result.getValInterflog();
//            // 入库
//            if (CollectionUtils.isNotEmpty(valInterflog)) {
//                if(valInterflog.size()<1000){
//                    valInterflogBatchMapper.insertBatch(valInterflog);
//                }else{
//                    int times = (int)Math.ceil( valInterflog.size()/1000.0 );
//                    for(int i=0; i<times; i++ ){
//                            valInterflogBatchMapper.insertBatch(valInterflog.subList(i*1000, Math.min((i+1)*1000, valInterflog.size()-1)));
//                        
//                    }
//                }            
//            }
            
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
