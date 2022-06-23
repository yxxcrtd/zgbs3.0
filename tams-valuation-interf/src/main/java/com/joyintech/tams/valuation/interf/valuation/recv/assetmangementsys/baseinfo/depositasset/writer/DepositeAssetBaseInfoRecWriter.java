/*
 * 文件名：BookDetailSubjectAllWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：huangjian
 * 修改时间：2018年6月1日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.depositasset.writer;

import com.github.pagehelper.util.StringUtil;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.security.data.MultilevelDataAuthorityService;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.*;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.depositasset.dto.DepositAssetBaseInfoProcessorResult;
import com.joyintech.tams.valuation.interf.valuation.recv.common.service.AssetmanageRecvCommonService;
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
 * 存款类资产-基础数据接口 Writer
 * 
 * @author weipeng
 * @version 1.0 2018年6月1日
 * @see DepositeAssetBaseInfoRecWriter
 * @since 1.0
 */
@Service
@Scope("prototype")
public class DepositeAssetBaseInfoRecWriter extends InterfBaseWriter<BaseEntity> {

    /**
     * 交易类金融产品Mapper
     */
//    @Autowired
//    private ValTradeFinProductBatchMapper valTradeFinProductBatchMapper;
    
    /**
     * 标的类金融产品Mapper
     */
    @Autowired
    private ValFinProductBatchMapper valFinProductBatchMapper;

    /**
     * 金融产品分类BatchMapper
     */
    @Autowired
    private ValFinProductTypeBatchMapper valFinProductTypeBatchMapper;

    /**
     * 现金流信息表BatchMapper
     */
    @Autowired
    private ValCashInfoBatchMapper valCashInfoBatchMapper;

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
     * 现金流计划
     */
    @Autowired
    private ValCashPlanBatchMapper valCashPlanBatchMapper;

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
    //add by liupengfei for 多级法人改造 S
    @Autowired
    private AssetmanageRecvCommonService assetmanageRecvCommonService;
    //add by liupengfei for 多级法人改造 E
	
    /**
     * 多级法人数据权限接口 add by tanxuan on 20210310 多级法人权限控制改造
     */
    @Autowired
    private MultilevelDataAuthorityService multilevelDataAuthorityService;
    
    @Value("${interf.properties.maxSqlNum}")
    private int maxSqlNum;

    /**
     * 标的类金融产品扩展表
     */
    @Autowired
    private ValFinProductAddBatchMapper valFinProductAddBatchMapper;

    /**
     * 更新数据 {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends BaseEntity> items)
        throws Exception {
        // 获取process返回组合的list值
        List<DepositAssetBaseInfoProcessorResult> resultList = (List<DepositAssetBaseInfoProcessorResult>)items;

        // 循环基金类processor结果DTO，读取所有的入库数据结构LIST，并进行入库操作。
        for (DepositAssetBaseInfoProcessorResult result : resultList) {
            // 标的类金融产品
            List<FinProduct> listValFinProduct = result.getListValFinProduct();
            // 入库
            if (CollectionUtils.isNotEmpty(listValFinProduct)) {
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
//                    //add by liupengfei for 多级法人改造 S
//                    // 记录二级法人权限
//                    if(MultilevelDataAuthorityUtil.isEnabled()){
//                        assetmanageRecvCommonService.insertDataAuthInst(e.getFinprodId(),e.getBelongInstitutionCode(), DictCodeList.共通业务类型_存款类资产信息.getValue());
//                    }
//                    //add by liupengfei for 多级法人改造 E
                }
                //如果循环结束，小于最大值
                if(CollectionUtils.isNotEmpty(listInsert)){
                    valFinProductBatchMapper.deleteBatch(listInsert); 
                    valFinProductBatchMapper.insertBatch(listInsert);
                }
                if(DictCodeListExt3.权限模式_联社模式.getValue().equals(CacheUtil.getSysParam("AUTHORITY_TYPE"))){
                	List<String> editOrgCodeList = new ArrayList<>();
                	for (FinProduct e :listValFinProduct) {
                		editOrgCodeList.clear();
                		if(StringUtil.isNotEmpty(e.getBelongInstitutionCode())){
                			editOrgCodeList.add(e.getBelongInstitutionCode());
                		}
                		multilevelDataAuthorityService.saveForOrganization(e.getFinprodId(), null, editOrgCodeList,
                    			DictCodeList.共通业务类型_存款类资产信息.getValue());
                    }
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
            
            // 金融产品分类表
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
            
            // 现金流信息表
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
            
            
            
            // 现金流计算规则表
            List<FinCashCalcRule> listValCashCalcRule = result.getListValCashCalcRule();
            // 入库
            if (CollectionUtils.isNotEmpty(listValCashCalcRule)) {
                //定义一个固定大小插入存放集合
                List<FinCashCalcRule> listInsert =  new ArrayList<FinCashCalcRule>();
                for(FinCashCalcRule e :listValCashCalcRule){
                   //小于最大值
                    if(listInsert.size() < maxSqlNum){
                        listInsert.add(e); 
                    }else if(listInsert.size() >= maxSqlNum){
                        valCashCalcRuleBatchMapper.deleteBatch(listInsert); 
                        valCashCalcRuleBatchMapper.insertBatch(listInsert); 
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                //如果循环结束，小于最大值
                if(CollectionUtils.isNotEmpty(listInsert)){
                    valCashCalcRuleBatchMapper.deleteBatch(listInsert); 
                    valCashCalcRuleBatchMapper.insertBatch(listInsert);
                }
            }
            
            // 现金流支付规则表
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
            
            
            // 现金流计划表
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

            /** modify by sunshuaishuai on 20210320 现金流利率信息表 start**/
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
            /** modify by sunshuaishuai on 20210320 现金流利率信息表 end**/

            // 日志
            List<ValInterflog> valInterflog = result.getValInterflog();
            // 入库
            if (CollectionUtils.isNotEmpty(valInterflog)) {
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

}
