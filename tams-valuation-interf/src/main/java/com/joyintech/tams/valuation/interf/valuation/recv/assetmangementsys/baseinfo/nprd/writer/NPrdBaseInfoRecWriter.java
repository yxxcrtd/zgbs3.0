/*
 * 文件名：BookDetailSubjectAllWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：huangjian
 * 修改时间：2018年6月1日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.nprd.writer;

import com.joyintech.framework.base.BaseEntity;
import com.github.pagehelper.util.StringUtil;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.security.MultilevelDataAuthorityUtil;
import com.joyintech.framework.security.data.MultilevelDataAuthorityService;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.common.base.dao.BokBooksetDateMapper;
import com.joyintech.tams.common.base.dao.BokBooksetMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.*;
import com.joyintech.tams.valuation.common.interf.base.constant.TempConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.BooksetInitResult;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.nprd.dto.NPrdBaseInfoProcessorResult;
import com.joyintech.tams.valuation.interf.valuation.recv.common.service.AssetmanageRecvCommonService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 净值型产品产品信息接口 Writer
 * 
 * @author weipeng
 * @version 1.0 2018年6月1日
 * @see NPrdBaseInfoRecWriter
 * @since 1.0
 */
@Service
@Scope("prototype")
public class NPrdBaseInfoRecWriter extends InterfBaseWriter<BaseEntity> {
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
     * 开放计划表
     */
    @Autowired
    private ValOpenPlanBatchMapper valOpenPlanBatchMapper;

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
     * 现金流支付规则表
     */
    @Autowired
    private ValCashPayRuleBatchMapper valCashPayRuleBatchMapper;
    /**
     * 业绩报酬计算规则表
     */
    @Autowired
    private ValFloatCountingRuleBatchMapper valFloatCountingRuleBatchMapper;
    /**
     * 业绩报酬计提规则表
     */
    @Autowired
    private ValFloatCalcRuleBatchMapper valFloatCalcRuleBatchMapper;

    /**
     * 业绩报酬计算规则明细表
     */
    @Autowired
    private ValFloatCalcRuleDetailBatchMapper ValFloatCalcRuleDetailBatchMapper;
    /**
     * 现金流计划表
     */
    @Autowired
    private ValCashPlanBatchMapper valCashPlanBatchMapper;

    /**
     * 业绩报酬计提计划表
     */
    @Autowired
    private ValPerfRewardAccrscheBatchMapper valPerfRewardAccrscheBatchMapper;

    /**
     * 业绩报酬计提计划表
     */
    @Autowired
    private ValBokDetailIdBatchMapper valBokDetailIdBatchMapper;

    /**
     * 税务方案配置表
     */
    @Autowired
    private BokBooksetPlanBatchMapper bokBooksetPlanBatchMapper;
    
    /**
     * 业绩回补规则
     */
    @Autowired
    private ValFinClawbackCalcRuleBatchMapper valFinCawlbackCalcRuleBatchMapper;
    
    /**
     * 日志
     */
    @Autowired
    private ValInterflogBatchMapper valInterflogBatchMapper;

    @Autowired
    private ValAcctSubjectInfoBatchMapper valAcctSubjectInfoBatchMapper;
    
    @Autowired
    private ValAcctLayeringInfoBatchMapper valAcctLayeringInfoBatchMapper;

//    @Autowired
//    private ValFinBenchmarkDetailBatchMapper valFinBenchmarkDetailBatchMapper;

    @Value("${interf.properties.maxSqlNum}")
    private int maxSqlNum;

    // add by tanxuan on 20210330 接口入表错误，service生成的实体和write入的表不一致
    // 从vas3.0.1合并
    @Autowired
    private ValFinBaseRuleInfoBatchMapper valFinBaseRuleInfoBatchMapper;


    /**
     * 账套信息初始化
     */
    @Autowired
    private BokBooksetMapper valBokBooksetMapper;

    @Autowired
    private BokBooksetDateMapper valBokBooksetDateMapper;

    @Autowired
    private ValBokBooksetExtBatchMapper valBokBooksetExtBatchMapper;

    @Autowired
    private ValFinTradeProductBatchMapper valFinTradeProductBatchMapper;
    //add by liupengfei for 多级法人改造 S
    @Autowired
    private AssetmanageRecvCommonService assetmanageRecvCommonService;
    //add by liupengfei for 多级法人改造 E
    
//	@Autowired
//    private ValFinBaseRuleInfoBatchMapper valFinBaseRuleInfoBatchMapper;
    
    /**
     * 多级法人数据权限接口 add by tanxuan on 20210310 多级法人权限控制改造
     */
    @Autowired
    private MultilevelDataAuthorityService multilevelDataAuthorityService;
    /**
     * 更新数据 {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends BaseEntity> items)
        throws Exception {
        // 获取process返回组合的list值
        List<NPrdBaseInfoProcessorResult> resultList = (List<NPrdBaseInfoProcessorResult>)items;

        // 循环基金类processor结果DTO，读取所有的入库数据结构LIST，并进行入库操作。
        for (NPrdBaseInfoProcessorResult result : resultList) {
            //1.标的类金融产品表
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
                    //add by liupengfei for 多级法人改造 S
                    // 记录二级法人权限
                    if(MultilevelDataAuthorityUtil.isEnabled()){
                        //产品
                        assetmanageRecvCommonService.insertDataAuthInst(e.getFinprodId(),e.getBelongInstitutionCode(),DictCodeList.共通业务类型_产品要素管理.getValue());
                        //账套
                        assetmanageRecvCommonService.insertDataAuthInst(e.getFinprodId(),e.getBelongInstitutionCode(),DictCodeList.共通业务类型_账套管理.getValue());
                        //bank_账套
                        assetmanageRecvCommonService.insertDataAuthInst("BANK_"+e.getFinprodId(),e.getBelongInstitutionCode(),DictCodeList.共通业务类型_账套管理.getValue());
                    }
                    //add by liupengfei for 多级法人改造 E
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valFinProductBatchMapper.deleteBatch(listInsert);
                    valFinProductBatchMapper.insertBatch(listInsert);
                }
            }

            //2.标的类金融产品扩展表
            List<FinProductAdd> listValFinProductExt = result.getListValFinProductExt();
            Map<String, FinProductAdd> mapValFinProductExt = new HashMap<String, FinProductAdd>();
            List<FinProductAdd> rstValFinProductExt = new ArrayList<FinProductAdd>();
            for (FinProductAdd obj : listValFinProductExt) {
            	//TODO-没有金融产品类型
//                if (mapValFinProductExt.get(obj.getFinprodId() + obj.getFinprodType()) == null) {
//                    rstValFinProductExt.add(obj);
//                }
//                mapValFinProductExt.put(obj.getFinprodId() + obj.getFinprodType(), obj);
                
                if (mapValFinProductExt.get(obj.getFinprodId()) == null) {
                    rstValFinProductExt.add(obj);
                }
                mapValFinProductExt.put(obj.getFinprodId(), obj);
            }
            // 入库
            if (CollectionUtils.isNotEmpty(rstValFinProductExt)) {
                // 定义一个固定大小插入存放集合
                List<FinProductAdd> listInsert = new ArrayList<FinProductAdd>();
                for (FinProductAdd e : rstValFinProductExt) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valFinProductExtBatchMapper.deleteBatch(listInsert);
                        valFinProductExtBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valFinProductExtBatchMapper.deleteBatch(listInsert);
                    valFinProductExtBatchMapper.insertBatch(listInsert);
                }
            }
            
            //3.交易类金融产品集合
            List<FinTradeProduct> listFinTradeProduct  = result.getListFinTradeProduct();//modify by dongjun on 20181019 start
            // 入库
            if (CollectionUtils.isNotEmpty(listFinTradeProduct)) {
                // 定义一个固定大小插入存放集合
                List<FinTradeProduct> listInsert = new ArrayList<FinTradeProduct>();
                for(FinTradeProduct e : listFinTradeProduct){
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    } else if (listInsert.size() >= maxSqlNum) {
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
            }
            
            
            // 4.开放计划表
            List<FinOpenPlan> listValOpenPlan = result.getListValOpenPlan();
            Map<String, FinOpenPlan> mapValOpenPlan = new HashMap<String, FinOpenPlan>();
            List<FinOpenPlan> rstValOpenPlan = new ArrayList<FinOpenPlan>();
            for (FinOpenPlan obj : listValOpenPlan) {
                if (mapValOpenPlan.get(obj.getFinprodId() + obj.getOpenDateEnd()) == null) {
                    rstValOpenPlan.add(obj);
                }
                mapValOpenPlan.put(obj.getFinprodId() + obj.getOpenDateEnd(), obj);
            }
            // 入库
            if (CollectionUtils.isNotEmpty(rstValOpenPlan)) {
                // 定义一个固定大小插入存放集合
                List<FinOpenPlan> listInsert = new ArrayList<FinOpenPlan>();
                for (FinOpenPlan e : rstValOpenPlan) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valOpenPlanBatchMapper.deleteBatch(listInsert);
                        valOpenPlanBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valOpenPlanBatchMapper.deleteBatch(listInsert);
                    valOpenPlanBatchMapper.insertBatch(listInsert);
                }
            }
            
            Map<String, Object> paramMap = result.getParamMap();
            String tmpEName = (String)paramMap.get("TMP_E_NAME");
            if (TempConstant.净值型产品费用基础信息表.equals(tmpEName)) {
                // 根据金融产品代码、金融产品类型删除产品费用生成的历史数据,执行顺序不能修改
                // 1.现金流计算规则表
                List<FinCashCalcRule> listValCashCalcRule = result.getListValCashCalcRule();
                if (CollectionUtils.isNotEmpty(listValCashCalcRule)) {
                    valCashCalcRuleBatchMapper.deleteFeeFinCashCalcRule(listValCashCalcRule);
                }
                // 2.现金流支付规则表
                List<FinCashPayRule> listValCashPayRule = result.getListValCashPayRule();
                if (CollectionUtils.isNotEmpty(listValCashPayRule)) {
                    valCashPayRuleBatchMapper.deleteFeeFinCashPayRule(listValCashPayRule);
                }
                // 3.删除现金流信息表
                List<FinCashInfo> listValCashInfo = result.getListValCashInfo();
                if (CollectionUtils.isNotEmpty(listValCashInfo)) {
                    valCashInfoBatchMapper.deleteFeeFinCashInfo(listValCashInfo);
                }
            }
            
            //5.现金流信息表
            List<FinCashInfo> listValCashInfo = result.getListValCashInfo();
            Map<String, FinCashInfo> mapValCashInfo = new HashMap<String, FinCashInfo>();
            List<FinCashInfo> rstValCashInfo = new ArrayList<FinCashInfo>();
            for (FinCashInfo obj : listValCashInfo) {
                if (mapValCashInfo.get(obj.getCashId()) == null) {
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
            
            // 6.现金流计算规则表
            List<FinCashCalcRule> listValCashCalcRule = result.getListValCashCalcRule();
            Map<String, FinCashCalcRule> mapValCashCalcRule = new HashMap<String, FinCashCalcRule>();
            List<FinCashCalcRule> rstValCashCalcRule = new ArrayList<FinCashCalcRule>();
            for (FinCashCalcRule obj : listValCashCalcRule) {
                if (mapValCashCalcRule.get(obj.getCashId() + obj.getEffDate()) == null) {
                    rstValCashCalcRule.add(obj);
                }
                mapValCashCalcRule.put(obj.getCashId() + obj.getEffDate(), obj);
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

            // 7.现金流支付规则表
            List<FinCashPayRule> listValCashPayRule = result.getListValCashPayRule();
            Map<String, FinCashPayRule> mapValCashPayRule = new HashMap<String, FinCashPayRule>();
            List<FinCashPayRule> rstValCashPayRule = new ArrayList<FinCashPayRule>();
            for (FinCashPayRule obj : listValCashPayRule) {
                if (mapValCashPayRule.get(obj.getCashId() + obj.getEffDate()) == null) {
                    rstValCashPayRule.add(obj);
                }
                mapValCashPayRule.put(obj.getCashId() + obj.getEffDate(), obj);
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

            // 8.业绩报酬计算规则表
            List<FinFloatCalcRule> listValFloatCalcRule = result.getListValFloatCalcRule();
            Map<String, FinFloatCalcRule> mapValFloatCalcRule = new HashMap<String, FinFloatCalcRule>();
            List<FinFloatCalcRule> rstValFloatCalcRule = new ArrayList<FinFloatCalcRule>();
            for (FinFloatCalcRule obj : listValFloatCalcRule) {
                if (mapValFloatCalcRule.get(obj.getCashId() + obj.getEffDate()) == null) {
                    rstValFloatCalcRule.add(obj);
                }
                mapValFloatCalcRule.put(obj.getCashId() + obj.getEffDate(), obj);
            }

            // 入库
            if (CollectionUtils.isNotEmpty(rstValFloatCalcRule)) {
                // 定义一个固定大小插入存放集合
                List<FinFloatCalcRule> listInsert = new ArrayList<FinFloatCalcRule>();
                for (FinFloatCalcRule e : rstValFloatCalcRule) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valFloatCalcRuleBatchMapper.deleteBatch(listInsert);
                        valFloatCalcRuleBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valFloatCalcRuleBatchMapper.deleteBatch(listInsert);
                    valFloatCalcRuleBatchMapper.insertBatch(listInsert);
                }
            }

            // 9.业绩报酬计提规则表
            List<FinFloatCountingRule> listValFloatCountingRule = result.getListValFloatCountingRule();
            Map<String, FinFloatCountingRule> mapValFloatCountingRule = new HashMap<String, FinFloatCountingRule>();
            List<FinFloatCountingRule> rstValFloatCountingRule = new ArrayList<FinFloatCountingRule>();
            for (FinFloatCountingRule obj : listValFloatCountingRule) {
                if (mapValFloatCountingRule.get(obj.getCashId() + obj.getEffDate()) == null) {
                    rstValFloatCountingRule.add(obj);
                }
                mapValFloatCountingRule.put(obj.getCashId() + obj.getEffDate(), obj);
            }
            // 入库
            if (CollectionUtils.isNotEmpty(rstValFloatCountingRule)) {
                // 定义一个固定大小插入存放集合
                List<FinFloatCountingRule> listInsert = new ArrayList<FinFloatCountingRule>();
                for (FinFloatCountingRule e : rstValFloatCountingRule) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valFloatCountingRuleBatchMapper.deleteBatch(listInsert);
                        valFloatCountingRuleBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valFloatCountingRuleBatchMapper.deleteBatch(listInsert);
                    valFloatCountingRuleBatchMapper.insertBatch(listInsert);
                }
            }

            // 10.业绩报酬计算规则明细表
            List<FinFloatCalcRuleDetail> listValFloatCalcRuleDetail = result.getListValFloatCalcRuleDetail();
            Map<String, FinFloatCalcRuleDetail> mapValFloatCalcRuleDetail = new HashMap<String, FinFloatCalcRuleDetail>();
            List<FinFloatCalcRuleDetail> rstValFloatCalcRuleDetail = new ArrayList<FinFloatCalcRuleDetail>();
            for (FinFloatCalcRuleDetail obj : listValFloatCalcRuleDetail) {
                if (mapValFloatCalcRuleDetail.get(
                    obj.getCashId() + obj.getEffDate() + obj.getLev()) == null) {
                    rstValFloatCalcRuleDetail.add(obj);
                }
                mapValFloatCalcRuleDetail.put(obj.getCashId() + obj.getEffDate() + obj.getLev(),
                    obj);
            }
            // 入库
            if (CollectionUtils.isNotEmpty(rstValFloatCalcRuleDetail)) {
                // 定义一个固定大小插入存放集合
                List<FinFloatCalcRuleDetail> listInsert = new ArrayList<FinFloatCalcRuleDetail>();
                for (FinFloatCalcRuleDetail e : rstValFloatCalcRuleDetail) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        ValFloatCalcRuleDetailBatchMapper.deleteBatch(listInsert);
                        ValFloatCalcRuleDetailBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    ValFloatCalcRuleDetailBatchMapper.deleteBatch(listInsert);
                    ValFloatCalcRuleDetailBatchMapper.insertBatch(listInsert);
                }
            }

            // 11.现金流计划表
            List<FinCashPlan> listValCashPlan = result.getListValCashPlan();
            // 入库
            if (CollectionUtils.isNotEmpty(listValCashPlan)) {
            	Map<String, FinCashPlan> mapValCashPlan = new HashMap<String, FinCashPlan>();
                List<FinCashPlan> rstValCashPlan = new ArrayList<FinCashPlan>();
                
                /**add by wujie on 20190223 start remark:现金流计划表删除逻辑调整**/
                Map<String,FinCashPlan> paramsMap = new HashMap<String,FinCashPlan>();
                List<FinCashPlan> delList = new ArrayList<FinCashPlan>();
                /**add by wujie on 20190223 start end:现金流计划表删除逻辑调整**/
                
                for (FinCashPlan obj : listValCashPlan) {
                    if (mapValCashPlan.get(obj.getCashId() + obj.getCashNum()) == null) {
                        rstValCashPlan.add(obj);
                    }
                    mapValCashPlan.put(obj.getCashId() + obj.getCashNum(), obj);
                    
                    /**add by wujie on 20190223 start remark:现金流计划表删除逻辑调整**/
                    if(paramsMap.get(obj.getCashId()) == null){
                   	 delList.add(obj);
                    }
                    paramsMap.put(obj.getCashId(), obj);
                    /**add by wujie on 20190223 end remark:现金流计划表删除逻辑调整**/
                }
                
                /**add by wujie on 20190223 start remark:现金流计划表删除逻辑调整**/
                valCashPlanBatchMapper.deleteFinCashPlanByCashId(delList);
                /**add by wujie on 20190223 end remark:现金流计划表删除逻辑调整**/
                // 定义一个固定大小插入存放集合
                List<FinCashPlan> listInsert = new ArrayList<FinCashPlan>();
                for (FinCashPlan e : rstValCashPlan) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                    	/**add by wujie on 20190223 remark:现金流计划表删除逻辑调整
                        valCashPlanBatchMapper.deleteBatch(listInsert);**/
                        valCashPlanBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                	/**add by wujie on 20190223 remark:现金流计划表删除逻辑调整
                    valCashPlanBatchMapper.deleteBatch(listInsert);**/
                    valCashPlanBatchMapper.insertBatch(listInsert);
                }
            }

            // 12.业绩报酬计提计划表
            List<FinFloatAccrualSche> listValPerfRewardAccrsche = result.getListValPerfRewardAccrsche();
            // 入库
            if (CollectionUtils.isNotEmpty(listValPerfRewardAccrsche)) {
            	 Map<String, FinFloatAccrualSche> mapValPerfRewardAccrsche = new HashMap<String, FinFloatAccrualSche>();
                 List<FinFloatAccrualSche> rstValPerfRewardAccrsche = new ArrayList<FinFloatAccrualSche>();
                 
                 /**add by wujie on 20190223 start remark:业绩报酬计提计划表删除逻辑调整**/
                 Map<String,FinFloatAccrualSche> paramsMap = new HashMap<String,FinFloatAccrualSche>();
                 List<FinFloatAccrualSche> delList = new ArrayList<FinFloatAccrualSche>();
                 /**add by wujie on 20190223 end remark:业绩报酬计提计划表删除逻辑调整**/
                 
                 for (FinFloatAccrualSche obj : listValPerfRewardAccrsche) {
                     if (mapValPerfRewardAccrsche.get(obj.getCashId() + obj.getAccrDate()) == null) {
                         rstValPerfRewardAccrsche.add(obj);
                     }
                     mapValPerfRewardAccrsche.put(obj.getCashId() + obj.getAccrDate(), obj);
                     
                     /**add by wujie on 20190223 start remark:业绩报酬计提计划表删除逻辑调整**/
                     if(paramsMap.get(obj.getCashId())==null){
                     	delList.add(obj);
                     }
                     paramsMap.put(obj.getCashId(), obj);
                     /**add by wujie on 20190223 end remark:业绩报酬计提计划表删除逻辑调整**/
                 }
                 
                 /**add by wujie on 20190223 start remark:业绩报酬计提计划表删除逻辑调整**/
                 valPerfRewardAccrscheBatchMapper.deleteByCashId(delList);
                 /**add by wujie on 20190223 end remark:业绩报酬计提计划表删除逻辑调整**/
                 
                // 定义一个固定大小插入存放集合
                List<FinFloatAccrualSche> listInsert = new ArrayList<FinFloatAccrualSche>();
                for (FinFloatAccrualSche e : rstValPerfRewardAccrsche) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                    	/**add by wujie on 20190223 remark:业绩报酬计提计划表删除逻辑调整
                        valPerfRewardAccrscheBatchMapper.deleteBatch(listInsert);**/
                        valPerfRewardAccrscheBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                	/**add by wujie on 20190223 remark:业绩报酬计提计划表删除逻辑调整
                    valPerfRewardAccrscheBatchMapper.deleteBatch(listInsert);
                    **/
                    valPerfRewardAccrscheBatchMapper.insertBatch(listInsert);
                }
            }

            // 13.账务明细代码表
            List<BokDetailId> listValBokDetailId = result.getListValBokDetailId() == null ? new ArrayList<BokDetailId>() : result.getListValBokDetailId();
            Map<String, BokDetailId> map = new HashMap<String, BokDetailId>();
            List<BokDetailId> rst = new ArrayList<BokDetailId>();
            for (BokDetailId obj : listValBokDetailId) {
                if (map.get(obj.getBokDetailId() + obj.getBooksetId()) == null) {
                    rst.add(obj);
                }
                map.put(obj.getBokDetailId() + obj.getBooksetId(), obj);
            }
            if (CollectionUtils.isNotEmpty(rst)) {
                // 定义一个固定大小插入存放集合
                List<BokDetailId> listInsert = new ArrayList<BokDetailId>();
                for (BokDetailId e : rst) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valBokDetailIdBatchMapper.deleteBatch(listInsert);
                        valBokDetailIdBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valBokDetailIdBatchMapper.deleteBatch(listInsert);
                    valBokDetailIdBatchMapper.insertBatch(listInsert);
                }
            }
            // 14.税务方案
            List<BokBooksetPlan> listBokBooksetPlan = result.getListBokBooksetPlan() == null ? new ArrayList<BokBooksetPlan>() : result.getListBokBooksetPlan();
            if (CollectionUtils.isNotEmpty(listBokBooksetPlan)) {
                // 定义一个固定大小插入存放集合
                List<BokBooksetPlan> listInsert = new ArrayList<BokBooksetPlan>();
                for (BokBooksetPlan e : listBokBooksetPlan) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        bokBooksetPlanBatchMapper.deleteBatch(listInsert);
                        bokBooksetPlanBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    bokBooksetPlanBatchMapper.deleteBatch(listInsert);
                    bokBooksetPlanBatchMapper.insertBatch(listInsert);
                }
            }

            // 账套信息初始化
            List<BooksetInitResult> booksetInitResultList = result.getBooksetInitResultList();
            if (CollectionUtils.isNotEmpty(booksetInitResultList)) {
                for (BooksetInitResult booksetInitResult : booksetInitResultList) {
                    // 模板账套信息
                    BokBookset valBokBookset = booksetInitResult.getValBokBookset();
                    if (valBokBookset != null) {
                        valBokBooksetMapper.deleteByPrimaryKey(valBokBookset.getBooksetId());
                        valBokBooksetMapper.insert(valBokBookset);
                    }
                    // 模板账套日期
                    BokBooksetDate valBokBooksetDate = booksetInitResult.getValBokBooksetDate();
                    if (valBokBooksetDate != null) {
                        valBokBooksetDateMapper.deleteByPrimaryKey(valBokBookset.getBooksetId());
                        valBokBooksetDateMapper.insert(valBokBooksetDate);
                        if(DictCodeListExt3.权限模式_联社模式.getValue().equals(CacheUtil.getSysParam("AUTHORITY_TYPE"))){
                        	List<String> editOrgCodeList = null;
                    		if(StringUtil.isNotEmpty(valBokBookset.getBelongInstitutionCode())){
                    			editOrgCodeList = Arrays.asList(valBokBookset.getBelongInstitutionCode());
                    		}
                    		multilevelDataAuthorityService.saveForOrganization(valBokBookset.getBooksetId(), null, editOrgCodeList,
                        			DictCodeList.共通业务类型_账套管理.getValue());
                        }
                    }
                    // 模板账套配置
                    List<BokBooksetExt> listValBokBooksetExt = booksetInitResult.getListValBokBooksetExt();
                    if (CollectionUtils.isNotEmpty(listValBokBooksetExt)) {
                        Map<String, BokBooksetExt> mapValBokBooksetExt = new HashMap<String, BokBooksetExt>();
                        List<BokBooksetExt> rstValBokBooksetExt = new ArrayList<BokBooksetExt>();
                        for (BokBooksetExt obj : listValBokBooksetExt) {
                            if (mapValBokBooksetExt.get(
                                obj.getBooksetId() + obj.getParamCode()) == null) {
                                rstValBokBooksetExt.add(obj);
                            }
                            mapValBokBooksetExt.put(obj.getBooksetId() + obj.getParamCode(), obj);
                        }
                        if (CollectionUtils.isNotEmpty(rstValBokBooksetExt)) {
                            // 定义一个固定大小插入存放集合
                            List<BokBooksetExt> listInsert = new ArrayList<BokBooksetExt>();
                            for (BokBooksetExt e : rstValBokBooksetExt) {
                                // 小于最大值
                                if (listInsert.size() < maxSqlNum) {
                                    listInsert.add(e);
                                }
                                else if (listInsert.size() >= maxSqlNum) {
                                    valBokBooksetExtBatchMapper.deleteBatch(listInsert);
                                    valBokBooksetExtBatchMapper.insertBatch(listInsert);
                                    listInsert.clear();
                                    listInsert.add(e);
                                }
                            }
                            // 如果循环结束，小于最大值
                            if (CollectionUtils.isNotEmpty(listInsert)) {
                                valBokBooksetExtBatchMapper.deleteBatch(listInsert);
                                valBokBooksetExtBatchMapper.insertBatch(listInsert);
                            }
                        }
                    }
                }
            }
            
            //核算主体代码
			List<BokAcctSubjectInfo> listAcctSubjectInfo = result.getListAcctSubjectInfo();
			//入库
			if (CollectionUtils.isNotEmpty(listAcctSubjectInfo)) {
			    // 定义一个固定大小插入存放集合
			    List<BokAcctSubjectInfo> listInsert = new ArrayList<BokAcctSubjectInfo>();
			    for(BokAcctSubjectInfo e : listAcctSubjectInfo){
			        // 小于最大值
			        if (listInsert.size() < maxSqlNum) {
			            listInsert.add(e);
			        } else if (listInsert.size() >= maxSqlNum) {
			            valAcctSubjectInfoBatchMapper.deleteBatch(listInsert);
			            valAcctSubjectInfoBatchMapper.insert(listInsert);
			            listInsert.clear();
			            listInsert.add(e);
			        }    
			    }
			    // 如果循环结束，小于最大值
			    if (CollectionUtils.isNotEmpty(listInsert)) {
			        valAcctSubjectInfoBatchMapper.deleteBatch(listInsert);
                    valAcctSubjectInfoBatchMapper.insert(listInsert);			        
			    }
			}
			
	         //核算分层代码
            List<BokAcctLayeringInfo> listAcctLayeringInfo = result.getListAcctLayeringInfo();
            //入库
            if (CollectionUtils.isNotEmpty(listAcctLayeringInfo)) {
                // 定义一个固定大小插入存放集合
                List<BokAcctLayeringInfo> listInsert = new ArrayList<BokAcctLayeringInfo>();
                for(BokAcctLayeringInfo e : listAcctLayeringInfo){
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    } else if (listInsert.size() >= maxSqlNum) {
                        valAcctLayeringInfoBatchMapper.deleteBatch(listInsert);
                        valAcctLayeringInfoBatchMapper.insert(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }    
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valAcctLayeringInfoBatchMapper.deleteBatch(listInsert);
                    valAcctLayeringInfoBatchMapper.insert(listInsert);                   
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
            
            // 业绩基准信息
            List<FinBaseRuleInfo> finBenchmarkDetailList = result.getFinBenchmarkDetailList();
            // 入库
            if (CollectionUtils.isNotEmpty(finBenchmarkDetailList)) {
                // 定义一个固定大小插入存放集合
                List<FinBaseRuleInfo> listInsert = new ArrayList<>();
                for (FinBaseRuleInfo e : finBenchmarkDetailList) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        //valFinBenchmarkDetailBatchMapper.deleteBatch(listInsert);
                        //valFinBenchmarkDetailBatchMapper.insertBatch(listInsert);
                        valFinBaseRuleInfoBatchMapper.deleteBatch(listInsert);
                        valFinBaseRuleInfoBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    //valFinBenchmarkDetailBatchMapper.deleteBatch(listInsert);
                    //valFinBenchmarkDetailBatchMapper.insertBatch(listInsert);
                    valFinBaseRuleInfoBatchMapper.deleteBatch(listInsert);
                    valFinBaseRuleInfoBatchMapper.insertBatch(listInsert);
                }
            }
                
            /**add by dongjun on 20190219 remark:回补  start**/
            // 业绩回补规则
            List<FinClawbackCalcRule> listFinCawlbackCalcRule = result.getListFinCawlbackCalcRule();
            // 入库
            if (CollectionUtils.isNotEmpty(listFinCawlbackCalcRule)) {
                // 定义一个固定大小插入存放集合
                List<FinClawbackCalcRule> listInsert = new ArrayList<>();
                for (FinClawbackCalcRule e : listFinCawlbackCalcRule) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                    	valFinCawlbackCalcRuleBatchMapper.deleteBatch(listInsert);
                    	valFinCawlbackCalcRuleBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                	valFinCawlbackCalcRuleBatchMapper.deleteBatch(listInsert);
                	valFinCawlbackCalcRuleBatchMapper.insertBatch(listInsert);
                }    
            }
            /**add by dongjun on 20190219 remark:回补  end**/
        }

    }

    public BokBooksetPlanBatchMapper getBokBooksetPlanBatchMapper() {
        return bokBooksetPlanBatchMapper;
    }

    public void setBokBooksetPlanBatchMapper(BokBooksetPlanBatchMapper bokBooksetPlanBatchMapper) {
        this.bokBooksetPlanBatchMapper = bokBooksetPlanBatchMapper;
    }

}
