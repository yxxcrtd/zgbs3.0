/*
 * 文件名：CPlannedAssetBaseInfoRecWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：huangjian
 * 修改时间：2018年6月1日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.cplannedinfo.writer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.tams.valuation.interf.valuation.recv.common.service.AssetmanageRecvCommonService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.github.pagehelper.util.StringUtil;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.security.data.MultilevelDataAuthorityService;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.common.base.entity.FinCashInfo;
import com.joyintech.tams.common.base.entity.FinCashPayRule;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.FinProductAdd;
import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.common.base.entity.FinProductValuationPara;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCashInfoBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCashPayRuleBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCashPlanBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductAddBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductTypeBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductValuationParaBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValInterflogBatchMapper;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.cplannedinfo.dto.CPlannedAssetBaseInfoProcessorResult;

/**
  * 计划类货币型-基础数据接口Writer
 * @author zhangzhao
 * @version 1.0 2018年6月28日
 * @see CPlannedAssetBaseInfoRecWriter
 * @since 1.0
 */
@Service
@Scope("prototype")
public class CPlannedAssetBaseInfoRecWriter extends InterfBaseWriter<BaseEntity> {

    /**
     * 标的类金融产品Mapper
     */
	@Autowired
	private ValFinProductBatchMapper valFinProductBatchMapper;
	
	 /**
     * 标的类金融产品扩展表
     */
    @Autowired
    private ValFinProductAddBatchMapper valFinProductAddBatchMapper;
    
    /**
     * 金融产品分类表
     */
    @Autowired
    private ValFinProductTypeBatchMapper valFinProductTypeBatchMapper;
    
    /**
     * 金融产品估值参数表
     */
    @Autowired
    private ValFinProductValuationParaBatchMapper valFinProductValuationParaBatchMapper;
    
    /**
     * 现金流信息表
     */
    @Autowired
    private ValCashInfoBatchMapper valCashInfoBatchMapper;
    
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
     * 日志
     */
    @Autowired
    private ValInterflogBatchMapper valInterflogBatchMapper;
    
    /**
     * 多级法人数据权限接口 add by tanxuan on 20210310 多级法人权限控制改造
     */
    @Autowired
    private MultilevelDataAuthorityService multilevelDataAuthorityService;
    
    @Value("${interf.properties.maxSqlNum}")
    private int maxSqlNum;

    //add by liupengfei for 多级法人改造 S
    @Autowired
    private AssetmanageRecvCommonService assetmanageRecvCommonService;
    //add by liupengfei for 多级法人改造 E
    /**
     * 参数
     */
    @SuppressWarnings("unused")
    private Map<String, Object> param;

    /**
     * 设置 param
     * 
     * @param param param
     */
    public void setParam(Map<String, Object> param) {
        this.param = param;
    }

    /**
     * 更新数据 {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends BaseEntity> items)
        throws Exception {
        // 获取process返回组合的list值
        List<CPlannedAssetBaseInfoProcessorResult> resultList = (List<CPlannedAssetBaseInfoProcessorResult>)items;
        
        // 循环processor结果DTO，读取所有的入库数据结构LIST，并进行入库操作。
        for (CPlannedAssetBaseInfoProcessorResult result : resultList) {
        	
            // 标的类金融产品表
            List<FinProduct> listValFinProduct = result.getListValFinProduct();
            Map<String, FinProduct> mapValFinProduct = new HashMap<String, FinProduct>();
            List<FinProduct> rstValFinProduct = new ArrayList<FinProduct>();
            for (FinProduct obj : listValFinProduct) {
                if (mapValFinProduct.get(obj.getFinprodId() + obj.getFinprodType()) == null) {
                    rstValFinProduct.add(obj);
                }
                mapValFinProduct.put(obj.getFinprodId() + obj.getFinprodType(), obj);
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
//                    //add by liupengfei for 多级法人改造 S
//                    // 记录二级法人权限
//                    if(MultilevelDataAuthorityUtil.isEnabled()){
//                        assetmanageRecvCommonService.insertDataAuthInst(e.getFinprodId(),e.getBelongInstitutionCode(), DictCodeList.共通业务类型_计划类资产_货币型_信息.getValue());
//                    }
//                    //add by liupengfei for 多级法人改造 E
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                	valFinProductBatchMapper.deleteBatch(listInsert);
                	valFinProductBatchMapper.insertBatch(listInsert);
                }
                if(DictCodeListExt3.权限模式_联社模式.getValue().equals(CacheUtil.getSysParam("AUTHORITY_TYPE"))){
                	List<String> editOrgCodeList = new ArrayList<>();
                	for (FinProduct e : rstValFinProduct) {
                		editOrgCodeList.clear();
                		if(StringUtil.isNotEmpty(e.getBelongInstitutionCode())){
                			editOrgCodeList.add(e.getBelongInstitutionCode());
                		}
                		multilevelDataAuthorityService.saveForOrganization(e.getFinprodId(), null, editOrgCodeList,
                    			DictCodeList.共通业务类型_计划类资产_货币型_信息.getValue());
                    }
                }
            }
                        
         // 标的类金融产品扩展表
            
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
            
            //金融产品分类表
            List<FinProductType> listFinProductType = result.getListValFinProductType();
            Map<String, FinProductType> mapFinProductType = new HashMap<String, FinProductType>();
            List<FinProductType> rstFinProductType = new ArrayList<FinProductType>();
//            for (FinProductType obj : listFinProductType) {
//                if (mapFinProductType.get(obj.getFinprodId() + obj.getFinprodType()) == null) {
//                	rstFinProductType.add(obj);
//                }
//                mapFinProductType.put(obj.getFinprodId() + obj.getFinprodType(), obj);
//            }
            for (FinProductType obj : listFinProductType) {
                if (mapFinProductType.get(obj.getFinprodId()) == null) {
                	rstFinProductType.add(obj);
                }
                mapFinProductType.put(obj.getFinprodId(), obj);
            }
            // 入库
            if (CollectionUtils.isNotEmpty(rstFinProductType)) {
                // 定义一个固定大小插入存放集合
                List<FinProductType> listInsert = new ArrayList<FinProductType>();
                for (FinProductType e : rstFinProductType) {
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
            //金融产品估值参数表 
            List<FinProductValuationPara> listFinProductValuationPara = result.getListValFinProductValuationPara();
            Map<String, FinProductValuationPara> mapFinProductValuationPara = new HashMap<String, FinProductValuationPara>();
            List<FinProductValuationPara> rstFinProductValuationPara = new ArrayList<FinProductValuationPara>();
            for (FinProductValuationPara obj : listFinProductValuationPara) {
                if (mapFinProductValuationPara.get(obj.getFinprodId() + obj.getBranch()) == null) {
                	rstFinProductValuationPara.add(obj);
                }
                mapFinProductValuationPara.put(obj.getFinprodId() + obj.getBranch(), obj);
            }
            // 入库
            if (CollectionUtils.isNotEmpty(rstFinProductValuationPara)) {
                // 定义一个固定大小插入存放集合
                List<FinProductValuationPara> listInsert = new ArrayList<FinProductValuationPara>();
                for (FinProductValuationPara e : rstFinProductValuationPara) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                    	valFinProductValuationParaBatchMapper.deleteBatch(listInsert);
                    	valFinProductValuationParaBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                	valFinProductValuationParaBatchMapper.deleteBatch(listInsert);
                	valFinProductValuationParaBatchMapper.insertBatch(listInsert);
                }
            }
            
            //现金流信息表
            List<FinCashInfo> listFinCashInfo = result.getListValCashInfo();
            Map<String, FinCashInfo> mapFinCashInfo = new HashMap<String, FinCashInfo>();
            List<FinCashInfo> rstFinCashInfo = new ArrayList<FinCashInfo>();
            for (FinCashInfo obj : listFinCashInfo) {
                if (mapFinCashInfo.get(obj.getCashId()) == null) {
                	rstFinCashInfo.add(obj);
                }
                mapFinCashInfo.put(obj.getCashId(), obj);
            }
            // 入库
            if (CollectionUtils.isNotEmpty(rstFinCashInfo)) {
                // 定义一个固定大小插入存放集合
                List<FinCashInfo> listInsert = new ArrayList<FinCashInfo>();
                for (FinCashInfo e : rstFinCashInfo) {
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
            
            //现金流支付规则表 
            List<FinCashPayRule> listFinCashPayRule = result.getListValCashPayRule();
            Map<String, FinCashPayRule> mapFinCashPayRule = new HashMap<String, FinCashPayRule>();
            List<FinCashPayRule> rstFinCashPayRule = new ArrayList<FinCashPayRule>();
            for (FinCashPayRule obj : listFinCashPayRule) {
                if (mapFinCashPayRule.get(obj.getCashId() + obj.getEffDate()) == null) {
                	rstFinCashPayRule.add(obj);
                }
                mapFinCashPayRule.put(obj.getCashId() + obj.getEffDate(),obj);
            }
            // 入库
            if (CollectionUtils.isNotEmpty(rstFinCashInfo)) {
                // 定义一个固定大小插入存放集合
                List<FinCashPayRule> listInsert = new ArrayList<FinCashPayRule>();
                for (FinCashPayRule e : rstFinCashPayRule) {
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
            
            //现金流计划表 
           /* List<FinCashPlan> listFinCashPlan = result.getListValCashPlan();
            Map<String, FinCashPlan> mapFinCashPlan = new HashMap<String, FinCashPlan>();
            List<FinCashPlan> rstFinCashPlan = new ArrayList<FinCashPlan>();
            for (FinCashPlan obj : listFinCashPlan) {
                if (mapFinCashPlan.get(obj.getCashId() + obj.getCashNum()) == null) {
                	rstFinCashPlan.add(obj);
                }
                mapFinCashPlan.put(obj.getCashId() + obj.getCashNum(),obj);
            }
            // 入库
            if (CollectionUtils.isNotEmpty(rstFinCashPlan)) {
                // 定义一个固定大小插入存放集合
                List<FinCashPlan> listInsert = new ArrayList<FinCashPlan>();
                for (FinCashPlan e : rstFinCashPlan) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                    	valCashPlanBatchMapper.deleteBatch(listInsert);
                    	valCashPlanBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                	valCashPlanBatchMapper.deleteBatch(listInsert);
                	valCashPlanBatchMapper.insertBatch(listInsert);
                }
            }*/
            
         // 现金流计划
            List<FinCashPlan> listValCashPlan = result.getListValCashPlan();
            // 入库
            if (CollectionUtils.isNotEmpty(listValCashPlan)) {
                List<FinCashPlan> delFinCashPlanList = new ArrayList<FinCashPlan>();
                Map<String,FinCashPlan> delFinCashPlanMap = new HashMap<String,FinCashPlan>();
                for(FinCashPlan obj :listValCashPlan){
                	delFinCashPlanMap.put(obj.getCashId(), obj);
                }
                for(String key :delFinCashPlanMap.keySet()){
	            	delFinCashPlanList.add(delFinCashPlanMap.get(key));
	            }
                valCashPlanBatchMapper.deleteFinCashPlanByCashId(delFinCashPlanList);
            	
              //定义一个固定大小插入存放集合
                List<FinCashPlan> listInsert =  new ArrayList<FinCashPlan>();
                for(FinCashPlan e :listValCashPlan){
                   //小于最大值
                    if(listInsert.size() < maxSqlNum){
                        listInsert.add(e); 
                    }else if(listInsert.size() >= maxSqlNum){
                        valCashPlanBatchMapper.insertBatch(listInsert); 
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                //如果循环结束，小于最大值
                if(CollectionUtils.isNotEmpty(listInsert)){
                    valCashPlanBatchMapper.insertBatch(listInsert);
                }
            }
           
            
            // 日志
            List<ValInterflog> valInterflog = result.getListValInterflog();
            // 入库
            if (CollectionUtils.isNotEmpty(valInterflog)) {
                // 定义一个固定大小插入存放集合
                List<ValInterflog> listInsert = new ArrayList<ValInterflog>();
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
