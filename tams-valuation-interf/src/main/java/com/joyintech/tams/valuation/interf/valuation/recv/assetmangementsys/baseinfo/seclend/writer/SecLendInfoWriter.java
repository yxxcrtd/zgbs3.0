/*
 * 文件名：SecLendInfoWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangqidong
 * 修改时间：2018年10月29日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.seclend.writer;

import java.util.ArrayList;
import java.util.List;

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
import com.joyintech.tams.common.base.entity.FinDealTarget;
import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.common.base.entity.FinTradeProduct;
import com.joyintech.tams.common.base.entity.FinTradeProductAdd;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.FinDealTargetBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.FinTradeProductAddBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.FinfDealPledgeBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductTypeBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinTradeProductBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValInterflogBatchMapper;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.seclend.dto.SecLendInfoProcessorResult;

/**
 * 基础数据接口 - 债券借贷
 * @author wangqidong
 * @version 1.0 2018年10月29日
 * @see SecLendInfoWriter
 * @since 1.0
 */
@Service
@Scope("prototype")
public class SecLendInfoWriter extends InterfBaseWriter<BaseEntity>{

    /**
     * 金融产品分类mapper
     */
    @Autowired
    private ValFinProductTypeBatchMapper valFinProductTypeBatchMapper;
    /**
     * 交易类金融产品mapper
     */
    @Autowired
    private ValFinTradeProductBatchMapper valFinTradeProductBatchMapper;
    /**
     * 交易类金融产品附属表
     */
    @Autowired
    private FinTradeProductAddBatchMapper finTradeProductAddBatchMapper;
    /**
     * 质押品
     */
    @Autowired
    private FinfDealPledgeBatchMapper finfDealPledgeBatchMapper;
    /**
     * 金融产品标的信息表
     */
    @Autowired
    private FinDealTargetBatchMapper finDealTargetBatchMapper;
    
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
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends BaseEntity> items)
        throws Exception {
//        InterfExecuteInfo interfExecuteInfo = (InterfExecuteInfo)this.getParams().get("executeInfo");
//        String interfDate = new SimpleDateFormat("yyyyMMdd").format(
//            interfExecuteInfo.getInterfDate());
        
        // 获取process返回组合的list值
        List<SecLendInfoProcessorResult> resultList = (List<SecLendInfoProcessorResult>)items;
        // 循环债券借贷processor结果DTO，读取所有的入库数据结构LIST，并进行入库操作。
        for (SecLendInfoProcessorResult result : resultList) {           
            
            // 日志
            List<ValInterflog> valInterflog = result.getValInterflog();
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
           
            // 金融产品分类表
            List<FinProductType> listValFinProductType = result.getListValFinProductType();
            // 入库
            if (CollectionUtils.isNotEmpty(listValFinProductType)) {
                // 定义一个固定大小插入存放集合
                List<FinProductType> listInsert = new ArrayList<FinProductType>();
                for (FinProductType e : listValFinProductType) {
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
//                    //add by liupengfei for 多级法人改造 S
//                    // 记录二级法人权限
//                    if(MultilevelDataAuthorityUtil.isEnabled()){
//                        assetmanageRecvCommonService.insertDataAuthInst(e.getFinprodId(),e.getBelongInstitutionCode(), DictCodeListExt3.共通业务类型_债券借贷标的信息.getValue());
//                    }
//                    //add by liupengfei for 多级法人改造 E
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valFinProductTypeBatchMapper.deleteBatch(listInsert);
                    valFinProductTypeBatchMapper.insertBatch(listInsert);
                }
            }
            
           // 交易类金融产品
           List<FinTradeProduct> listValFinTradeProduct = result.getListValFinTradeProduct();
           // 入库
           if (CollectionUtils.isNotEmpty(listValFinTradeProduct)) {
               // 定义一个固定大小插入存放集合
               List<FinTradeProduct> listInsert = new ArrayList<FinTradeProduct>();
               for (FinTradeProduct e : listValFinTradeProduct) {
                   // 小于最大值
                   if (listInsert.size() < maxSqlNum) {
                       listInsert.add(e);
                   }
                   else if (listInsert.size() >= maxSqlNum) {
                       valFinTradeProductBatchMapper.deleteBatch(listInsert);
                       valFinTradeProductBatchMapper.insertBatch(listInsert);
                       listInsert.clear();
                       listInsert.add(e);
                   }
               }
               // 如果循环结束，小于最大值
               if (CollectionUtils.isNotEmpty(listInsert)) {
                   valFinTradeProductBatchMapper.deleteBatch(listInsert);
                   valFinTradeProductBatchMapper.insertBatch(listInsert);
               }
               if(DictCodeListExt3.权限模式_联社模式.getValue().equals(CacheUtil.getSysParam("AUTHORITY_TYPE"))){
            	   List<String> editOrgCodeList = new ArrayList<>();
               	for (FinTradeProduct e : listValFinTradeProduct) {
               		editOrgCodeList.clear();
            		if(StringUtil.isNotEmpty(e.getBelongInstitutionCode())){
            			editOrgCodeList.add(e.getBelongInstitutionCode());
            		}
            		multilevelDataAuthorityService.saveForOrganization(e.getFinprodId(), null, editOrgCodeList,
                   			DictCodeListExt3.共通业务类型_债券借贷标的信息.getValue());
                   }
               }
           }
           // 交易类金融产品附属表
           List<FinTradeProductAdd> listValFinTradeProductAdd = result.getListValFinTradeProductAdd();
           // 入库
           if (CollectionUtils.isNotEmpty(listValFinTradeProductAdd)) {
               // 定义一个固定大小插入存放集合
               List<FinTradeProductAdd> listInsert = new ArrayList<FinTradeProductAdd>();
               for (FinTradeProductAdd e : listValFinTradeProductAdd) {
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
           // 质押品
           List<FinDealPledge> listValFinDealPledge = result.getListFinDealPledge();
           // 入库
           if (CollectionUtils.isNotEmpty(listValFinDealPledge)) {
               // 定义一个固定大小插入存放集合
               List<FinDealPledge> listInsert = new ArrayList<FinDealPledge>();
               for (FinDealPledge e : listValFinDealPledge) {
                   // 小于最大值
                   if (listInsert.size() < maxSqlNum) {
                       listInsert.add(e);
                   }
                   else if (listInsert.size() >= maxSqlNum) {
                       finfDealPledgeBatchMapper.deleteBatch(listInsert);
                       finfDealPledgeBatchMapper.insertBatch(listInsert);
                       listInsert.clear();
                       listInsert.add(e);
                   }
               }
               // 如果循环结束，小于最大值
               if (CollectionUtils.isNotEmpty(listInsert)) {
                   finfDealPledgeBatchMapper.deleteBatch(listInsert);
                   finfDealPledgeBatchMapper.insertBatch(listInsert);
               }
           }
           // 金融产品标的信息表
           List<FinDealTarget> listValFinDealTarget = result.getListFinDealTarget();
           // 入库
           if (CollectionUtils.isNotEmpty(listValFinDealTarget)) {
               // 定义一个固定大小插入存放集合
               List<FinDealTarget> listInsert = new ArrayList<FinDealTarget>();
               for (FinDealTarget e : listValFinDealTarget) {
                   // 小于最大值
                   if (listInsert.size() < maxSqlNum) {
                       listInsert.add(e);
                   }
                   else if (listInsert.size() >= maxSqlNum) {
                       finDealTargetBatchMapper.deleteBatch(listInsert);
                       finDealTargetBatchMapper.insertBatch(listInsert);
                       listInsert.clear();
                       listInsert.add(e);
                   }
               }
               // 如果循环结束，小于最大值
               if (CollectionUtils.isNotEmpty(listInsert)) {
                   finDealTargetBatchMapper.deleteBatch(listInsert);
                   finDealTargetBatchMapper.insertBatch(listInsert);
               }
           }
                      
        }
        
    }

}
