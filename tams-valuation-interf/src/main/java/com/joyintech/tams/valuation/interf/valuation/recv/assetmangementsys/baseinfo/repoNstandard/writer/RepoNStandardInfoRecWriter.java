/*
 * 文件名：BookDetailSubjectAllWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：huangjian
 * 修改时间：2018年6月1日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.repoNstandard.writer;

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
import com.joyintech.tams.common.base.entity.FinDealPledge;
import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.common.base.entity.FinProductValuationPara;
import com.joyintech.tams.common.base.entity.FinTradeProduct;
import com.joyintech.tams.common.base.entity.FinTradeProductAdd;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.FinTradeProductAddBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.FinfDealPledgeBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCommonBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductTypeBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductValuationParaBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValInterflogBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValTradeFinProductBatchMapper;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.repoNstandard.entity.RepoNStandardInfoProcessorResult;

/**
 * 债券质押式回购标的信息接口 Writer
 * 
 * @author leipeng
 * @version 1.0 2018年8月06日
 * @see RepoNStandardInfoRecWriter
 * @since 1.0
 */
@Service
@Scope("prototype")
public class RepoNStandardInfoRecWriter extends InterfBaseWriter<BaseEntity> {

    /**
     * 交易类金融产品Mapper
     */
    @Autowired
    private ValTradeFinProductBatchMapper valTradeFinProductBatchMapper;

    /**
     * 金融产品分类BatchMapper
     */
    @Autowired
    private ValFinProductTypeBatchMapper valFinProductTypeBatchMapper;

    /**
     * 交易类金融产品附属表batchMapper
     */
    @Autowired
    private FinTradeProductAddBatchMapper finTradeProductAddBatchMapper;
    
    /**
     * 质押品产品信息batchMapper
     */
    @Autowired
    private FinfDealPledgeBatchMapper finfDealPledgeBatchMapper;
    
    /**
     * 金融产品估值参数表
     * add by wujie on 20190420
     */
    @Autowired
    private ValFinProductValuationParaBatchMapper valFinProductValuationParaBatchMapper;

    /**
     * 多级法人数据权限接口 add by tanxuan on 20210310 多级法人权限控制改造
     */
    @Autowired
    private MultilevelDataAuthorityService multilevelDataAuthorityService;

    /**
     * 日志
     */
    @Autowired
    private ValInterflogBatchMapper valInterflogBatchMapper;

    @Autowired
    private ValCommonBatchMapper valCommonBatchMapper;// 去重数据Mapper

    @Value("${interf.properties.maxSqlNum}")
    private int maxSqlNum;

    //add by liupengfei for 多级法人改造 S
    @Autowired
    private AssetmanageRecvCommonService assetmanageRecvCommonService;
    //add by liupengfei for 多级法人改造 E

    /**
     * 更新数据 {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends BaseEntity> items)
        throws Exception {
        // 获取process返回组合的list值
        List<RepoNStandardInfoProcessorResult> resultList = (List<RepoNStandardInfoProcessorResult>)items;

        // 循环基金类processor结果DTO，读取所有的入库数据结构LIST，并进行入库操作。
        for (RepoNStandardInfoProcessorResult result : resultList) {
        	
        	// 交易类金融产品
			List<FinTradeProduct> listValTradeFinProduct = result.getListValTradeFinProduct();
			if (CollectionUtils.isNotEmpty(listValTradeFinProduct)) {
				Map<String,FinTradeProduct> map_CQ = new HashMap<String,FinTradeProduct>();
	            List<FinTradeProduct> listValTradeFinProduct_QC= new ArrayList<FinTradeProduct>();
	            for(FinTradeProduct obj :listValTradeFinProduct){
	                if(map_CQ.get(obj.getFinprodId()+obj.getFinprodType()+obj.getBranch()) ==null){
	                	listValTradeFinProduct_QC.add(obj);
	                }
	                map_CQ.put(obj.getFinprodId()+obj.getFinprodType()+obj.getBranch(), obj);
	            }
	            // 定义一个固定大小插入存放集合
                List<FinTradeProduct> listInsert = new ArrayList<FinTradeProduct>();
                for (FinTradeProduct e : listValTradeFinProduct_QC) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                    	valTradeFinProductBatchMapper.deleteBatch(listInsert);
                    	valTradeFinProductBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
//                    //add by liupengfei for 多级法人改造 S
//                    // 记录二级法人权限
//                    if(MultilevelDataAuthorityUtil.isEnabled()){
//                        assetmanageRecvCommonService.insertDataAuthInst(e.getFinprodId(),e.getBelongInstitutionCode(), DictCodeListExt3.共通业务类型_回购标的信息.getValue());
//                    }
//                    //add by liupengfei for 多级法人改造 E
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                	valTradeFinProductBatchMapper.deleteBatch(listInsert);
                	valTradeFinProductBatchMapper.insertBatch(listInsert);
                }
                if(DictCodeListExt3.权限模式_联社模式.getValue().equals(CacheUtil.getSysParam("AUTHORITY_TYPE"))){
                	List<String> editOrgCodeList = new ArrayList<>();
                	for (FinTradeProduct e : listValTradeFinProduct_QC) {
                		editOrgCodeList.clear();
                		if(StringUtil.isNotEmpty(e.getBelongInstitutionCode())){
                			editOrgCodeList.add(e.getBelongInstitutionCode());
                		}
                		multilevelDataAuthorityService.saveForOrganization(e.getFinprodId(), null, editOrgCodeList,
                				DictCodeListExt3.共通业务类型_回购类资产.getValue());
                    }
                }
			}

			// 金融产品分类
			List<FinProductType> listValFinProductType = result.getListValFinProductType();
			if (CollectionUtils.isNotEmpty(listValFinProductType)) {
				Map<String,FinProductType> map_CQ = new HashMap<String,FinProductType>();
	            List<FinProductType> listValFinProductType_CQ= new ArrayList<FinProductType>();
	            for(FinProductType obj :listValFinProductType){
	                if(map_CQ.get(obj.getFinprodId()) ==null){
	                	listValFinProductType_CQ.add(obj);
	                }
	                map_CQ.put(obj.getFinprodId(), obj);
	            }
	            // 定义一个固定大小插入存放集合
                List<FinProductType> listInsert = new ArrayList<FinProductType>();
                for (FinProductType e : listValFinProductType_CQ) {
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
			
			// 交易类金融产品附属表
			List<FinTradeProductAdd> listFinTradeProductAdd = result.getListFinTradeProductAdd();
			if (CollectionUtils.isNotEmpty(listFinTradeProductAdd)) {
				Map<String,FinTradeProductAdd> map_CQ = new HashMap<String,FinTradeProductAdd>();
	            List<FinTradeProductAdd> listFinTradeProductAdd_CQ= new ArrayList<FinTradeProductAdd>();
	            for(FinTradeProductAdd obj :listFinTradeProductAdd){
	                if(map_CQ.get(obj.getFinprodId()+obj.getFinprodType()+obj.getBranch()) ==null){
	                	listFinTradeProductAdd_CQ.add(obj);
	                }
	                map_CQ.put(obj.getFinprodId()+obj.getFinprodType()+obj.getBranch(), obj);
	            }
	            // 定义一个固定大小插入存放集合
                List<FinTradeProductAdd> listInsert = new ArrayList<FinTradeProductAdd>();
                for (FinTradeProductAdd e : listFinTradeProductAdd_CQ) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                    	finTradeProductAddBatchMapper.deleteBatch(listInsert);
                    	finTradeProductAddBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                	finTradeProductAddBatchMapper.deleteBatch(listInsert);
                	finTradeProductAddBatchMapper.insertBatch(listInsert);
                }
			}
			//金融产品估值参数表 
            List<FinProductValuationPara> listFinProductValuationPara = result.getListFinProductValuationPara();
            // 入库
            if (CollectionUtils.isNotEmpty(listFinProductValuationPara)) {
                // 定义一个固定大小插入存放集合
                List<FinProductValuationPara> listInsert = new ArrayList<FinProductValuationPara>();
                for (FinProductValuationPara e : listFinProductValuationPara) {
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
			
            // 质押品产品信息扩展表
            List<FinDealPledge> listFinDealPledge = result.getListFinDealPledge();
            // 入库
            if (CollectionUtils.isNotEmpty(listFinDealPledge)) {
            	Map<String,FinDealPledge> mapDeal = new HashMap<String,FinDealPledge>();
            	List<FinDealPledge> delList = new ArrayList<FinDealPledge>();
            	
            	Map<String,FinDealPledge> map_CQ = new HashMap<String,FinDealPledge>();
	            List<FinDealPledge> listFinDealPledge_CQ= new ArrayList<FinDealPledge>();
	            for(FinDealPledge obj :listFinDealPledge){
	            	String key = obj.getFinprodId()+obj.getBranch()+obj.getPledgeFinprodId();
	                if(map_CQ.get(key) ==null){
	                	listFinDealPledge_CQ.add(obj);
	                }
	                map_CQ.put(key, obj);
	                
	                if(mapDeal.get(obj.getFinprodId()) == null){
	                	delList.add(obj);
	                }
	                mapDeal.put(obj.getFinprodId(), obj);
	            }
	            //根据金融产品代码删除抵押品
            	valCommonBatchMapper.deleteBatchPledgeById(delList);
	            
	            
	            // 定义一个固定大小插入存放集合
                List<FinDealPledge> listInsert = new ArrayList<FinDealPledge>();
                for (FinDealPledge e : listFinDealPledge_CQ) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                    	finfDealPledgeBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                	finfDealPledgeBatchMapper.insertBatch(listInsert);
                }
            }
            // 日志
            List<ValInterflog> valInterflog = result.getValInterflog();
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
