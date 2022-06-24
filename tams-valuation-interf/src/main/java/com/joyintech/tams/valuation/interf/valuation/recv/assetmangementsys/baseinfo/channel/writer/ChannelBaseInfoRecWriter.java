/*
 * 文件名：ChannelBaseInfoRecWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：HuangJian
 * 修改时间：2019年3月12日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */
 
package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.channel.writer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.tams.constant.DictCodeList;
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
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinCashInfo;
import com.joyintech.tams.common.base.entity.FinProduct;
import com.joyintech.tams.common.base.entity.FinProductAdd;
import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.common.base.entity.FinProductValuationPara;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCashCalcRuleBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCashInfoBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductAddBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductTypeBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductValuationParaBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValInterflogBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.core.entity.InterfExecuteInfo;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.channel.dto.ChannelBaseInfoProcessorResult;

/**
 * 〈一句话功能简述〉
 * 〈功能详细描述〉
 * @author HuangJian
 * @version 1.0 2019年3月12日
 * @see ChannelBaseInfoRecWriter
 * @since 1.0
 */
@Service
@Scope("prototype")
public class ChannelBaseInfoRecWriter extends InterfBaseWriter<BaseEntity>{
    
    /**
     * 标的类金融产品Mapper
     */
    @Autowired
    private ValFinProductBatchMapper valFinProductBatchMapper;
    
     /**
     * 标的类金融产品扩展表
     */
    @Autowired
    private ValFinProductAddBatchMapper valFinProductExtBatchMapper;
    
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
     * 现金流计算规则表 
     */
    @Autowired
    private ValCashCalcRuleBatchMapper valCashCalcRuleBatchMapper;
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
    
    @Autowired
    private InterfCommonService interfCommonService;
    
    @Value("${interf.properties.maxSqlNum}")
    private int maxSqlNum;

    /**
     * 参数
     */
    @SuppressWarnings("unused")
    private Map<String, Object> param;
    //add by liupengfei for 多级法人改造 S
    @Autowired
    private AssetmanageRecvCommonService assetmanageRecvCommonService;
    //add by liupengfei for 多级法人改造 E

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
        InterfExecuteInfo interfExecuteInfo = (InterfExecuteInfo)this.getParams().get("executeInfo");
        Date interfDate =interfExecuteInfo.getInterfDate();
        List<ChannelBaseInfoProcessorResult> resultList = (List<ChannelBaseInfoProcessorResult>)items;
        
        for (ChannelBaseInfoProcessorResult result : resultList) {
            // 标的类金融产品表
            List<FinProduct> listValFinProduct = result.getListValFinProduct();
            // 入库
            if (CollectionUtils.isNotEmpty(listValFinProduct)) {
                // 定义一个固定大小插入存放集合
                List<FinProduct> listInsert = new ArrayList<FinProduct>();
                for (FinProduct e : listValFinProduct) {
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
//                        assetmanageRecvCommonService.insertDataAuthInst(e.getFinprodId(),e.getBelongInstitutionCode(), DictCodeList.共通业务类型_通道管理.getValue());
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
                	for (FinProduct e : listValFinProduct) {
                		editOrgCodeList.clear();
                		if(StringUtil.isNotEmpty(e.getBelongInstitutionCode())){
                			editOrgCodeList.add(e.getBelongInstitutionCode());
                		}
                		multilevelDataAuthorityService.saveForOrganization(e.getFinprodId(), null, editOrgCodeList,
                    			DictCodeList.共通业务类型_通道管理.getValue());
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
            
            //金融产品分类表
            List<FinProductType> listFinProductType = result.getListValFinProductType();
            // 入库
            if (CollectionUtils.isNotEmpty(listFinProductType)) {
                // 定义一个固定大小插入存放集合
                List<FinProductType> listInsert = new ArrayList<FinProductType>();
                for (FinProductType e : listFinProductType) {
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
            
            //现金流信息表
            List<FinCashInfo> listFinCashInfo = result.getListValCashInfo();
            // 入库
            if (CollectionUtils.isNotEmpty(listFinCashInfo)) {
                // 定义一个固定大小插入存放集合
                List<FinCashInfo> listInsert = new ArrayList<FinCashInfo>();
                for (FinCashInfo e : listFinCashInfo) {
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
            /**add by wujie on 20190326 start remark:子账套费用现金流信息更新**/
            if (CollectionUtils.isNotEmpty(listFinCashInfo)) {
            	List<String>  tdlist= result.getTdlist();
            	List<FinCashInfo> listFinCashInfoF = null;
            	if (CollectionUtils.isNotEmpty(tdlist)) {
            		listFinCashInfoF = interfCommonService.updateFinCashInfoTd(interfDate, tdlist);
            	}
            	if (CollectionUtils.isNotEmpty(listFinCashInfoF)) {
            		valCashInfoBatchMapper.deleteFeeFinCashInfo(listFinCashInfoF); 
            		List<FinCashInfo> listInsert = new ArrayList<FinCashInfo>();
                    for (FinCashInfo e : listFinCashInfoF) {
                        // 小于最大值
                        if (listInsert.size() < maxSqlNum) {
                            listInsert.add(e);
                        }
                        else if (listInsert.size() >= maxSqlNum) {
                            valCashInfoBatchMapper.insertBatch(listInsert);
                            listInsert.clear();
                            listInsert.add(e);
                        }
                    }
                    // 如果循环结束，小于最大值
                    if (CollectionUtils.isNotEmpty(listInsert)) {
                        valCashInfoBatchMapper.insertBatch(listInsert);
                    }
            	}
            }
            /**add by wujie on 20190326 end**/
            
            //现金流计算规则表
            List<FinCashCalcRule> listFinCashCalcRule = result.getListValCashCalcRule();
            // 入库
            if (CollectionUtils.isNotEmpty(listFinCashCalcRule)) {
            	valCashCalcRuleBatchMapper.deleteBatchC(listFinCashCalcRule);
                // 定义一个固定大小插入存放集合
                List<FinCashCalcRule> listInsert = new ArrayList<FinCashCalcRule>();
                for (FinCashCalcRule e : listFinCashCalcRule) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valCashCalcRuleBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valCashCalcRuleBatchMapper.insertBatch(listInsert);
                }
            }
            
            if(listFinCashCalcRule!=null&&listFinCashCalcRule.size()>0){
                //通道代码  通道费用计算规则表更新
                List<String>  list= result.getTdlist();
                List<FinCashCalcRule> listFinCashCalcRuleC = null;
                if (CollectionUtils.isNotEmpty(listFinCashCalcRule)) {
                	listFinCashCalcRuleC = interfCommonService.updateChannelFinCashCalcRule(interfDate, list);
                }
                if (CollectionUtils.isNotEmpty(listFinCashCalcRuleC)) {
                	valCashCalcRuleBatchMapper.deleteBatchC(listFinCashCalcRuleC);
                    // 定义一个固定大小插入存放集合
                    List<FinCashCalcRule> listInsert = new ArrayList<FinCashCalcRule>();
                    for (FinCashCalcRule e : listFinCashCalcRuleC) {
                        // 小于最大值
                        if (listInsert.size() < maxSqlNum) {
                            listInsert.add(e);
                        }
                        else if (listInsert.size() >= maxSqlNum) {
                            valCashCalcRuleBatchMapper.insertBatch(listInsert);
                            listInsert.clear();
                            listInsert.add(e);
                        }
                    }
                    // 如果循环结束，小于最大值
                    if (CollectionUtils.isNotEmpty(listInsert)) {
                        valCashCalcRuleBatchMapper.insertBatch(listInsert);
                    }
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
