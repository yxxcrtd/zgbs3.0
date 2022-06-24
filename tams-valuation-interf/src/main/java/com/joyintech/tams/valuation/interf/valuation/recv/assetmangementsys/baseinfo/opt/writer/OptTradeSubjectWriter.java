/*
 * 文件名：OptTradeSubjectWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：TanXuan
 * 修改时间：2020年01月08日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.opt.writer;

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
import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.common.base.entity.FinTradeProduct;
import com.joyintech.tams.common.base.entity.FinTradeProductAdd;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductTypeBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinTradeProductAddBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinTradeProductBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValInterflogBatchMapper;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.opt.dto.OptTradeSubjectProcessorResult;

/**
 * 场外期权标的信息
 * 
 * @author TanXuan
 * @version 1.0 2020年01月08日
 * @see OptTradeSubjectWriter
 * @since 1.0
 */
@Service
@Scope("prototype")
public class OptTradeSubjectWriter extends InterfBaseWriter<BaseEntity>{
    
    
    /**
     * 交易类金融产品
     */
    @Autowired
    private ValFinTradeProductBatchMapper valFinTradeProductBatchMapper;
    /**
     * 金融产品分类
     */
    @Autowired
    private ValFinProductTypeBatchMapper valFinProductTypeBatchMapper;
    /**
     * 交易类金融产品扩展表
     */
    @Autowired
    private ValFinTradeProductAddBatchMapper  valFinTradeProductAddBatchMapper;

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
        List<OptTradeSubjectProcessorResult> resultList = (List<OptTradeSubjectProcessorResult>)items;
        for (OptTradeSubjectProcessorResult result : resultList) {
        	
        	//交易类金融产品表
            List<FinTradeProduct> listValFinTradeProduct = result.getListValFinTradeProduct();
            Map<String,FinTradeProduct> mapValFinTradeProduct = new HashMap<String,FinTradeProduct>();
            List<FinTradeProduct> rstValFinTradeProduct= new ArrayList<FinTradeProduct>();
            for(FinTradeProduct obj :listValFinTradeProduct){
                if(mapValFinTradeProduct.get(obj.getFinprodId()+obj.getFinprodType()) ==null){
                    rstValFinTradeProduct.add(obj);
                }
                mapValFinTradeProduct.put(obj.getFinprodId()+obj.getFinprodType(), obj);
            }
            // 入库
            if (CollectionUtils.isNotEmpty(rstValFinTradeProduct)) {
                // 定义一个固定大小插入存放集合
                List<FinTradeProduct> listInsert = new ArrayList<FinTradeProduct>();
                for (FinTradeProduct e : rstValFinTradeProduct) {
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
//                    //add by liupengfei for 多级法人改造 S
//                    // 记录二级法人权限
//                    if(MultilevelDataAuthorityUtil.isEnabled()){
//                        assetmanageRecvCommonService.insertDataAuthInst(e.getFinprodId(),e.getBelongInstitutionCode(), DictCodeListExt3.共通业务类型_期权标的管理.getValue());
//                    }
//                    //add by liupengfei for 多级法人改造 E
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valFinTradeProductBatchMapper.deleteBatch(listInsert);
                    valFinTradeProductBatchMapper.insertBatch(listInsert);
                }
                if(DictCodeListExt3.权限模式_联社模式.getValue().equals(CacheUtil.getSysParam("AUTHORITY_TYPE"))){
                	List<String> editOrgCodeList = new ArrayList<>();
                	for (FinTradeProduct e : rstValFinTradeProduct) {
                		editOrgCodeList.clear();
                		if(StringUtil.isNotEmpty(e.getBelongInstitutionCode())){
                			editOrgCodeList.add(e.getBelongInstitutionCode());
                		}
                		multilevelDataAuthorityService.saveForOrganization(e.getFinprodId(), null, editOrgCodeList,
                    			DictCodeListExt3.共通业务类型_期权标的管理.getValue());
                    }
                }
            }
            
            
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
            
        	//交易类金融产品附属表
            List<FinTradeProductAdd> listValFinTradeProductAdd = result.getListValFinTradeProductAdd();
            Map<String,FinTradeProductAdd> mapValFinTradeProductAdd = new HashMap<String,FinTradeProductAdd>();
            List<FinTradeProductAdd> rstValFinTradeProductAdd= new ArrayList<FinTradeProductAdd>();
            for(FinTradeProductAdd obj :listValFinTradeProductAdd){
                if(mapValFinTradeProductAdd.get(obj.getFinprodId()+obj.getFinprodType()) ==null){
                    rstValFinTradeProductAdd.add(obj);
                }
                mapValFinTradeProductAdd.put(obj.getFinprodId()+obj.getFinprodType(), obj);
            }
            // 入库
            if (CollectionUtils.isNotEmpty(rstValFinTradeProductAdd)) {
                // 定义一个固定大小插入存放集合
                List<FinTradeProductAdd> listInsert = new ArrayList<FinTradeProductAdd>();
                for (FinTradeProductAdd e : rstValFinTradeProductAdd) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valFinTradeProductAddBatchMapper.deleteBatch(listInsert);
                        valFinTradeProductAddBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valFinTradeProductAddBatchMapper.deleteBatch(listInsert);
                    valFinTradeProductAddBatchMapper.insertBatch(listInsert);
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
