/*
 * 文件名：IrsBaseInfoWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V3.0.5.0
 * 修改人：殷亚
 * 修改时间：2022年4月21日
 * 修改内容：新建
 * 系统名称：估值核算系统V3.0.5
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.irs.writer;

import com.github.pagehelper.util.StringUtil;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.security.data.MultilevelDataAuthorityService;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.*;
import com.joyintech.tams.valuation.common.interf.base.constant.TempConstant;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.irs.dto.IrsBaseInfoRecResult;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 利率互换 IrsBaseInfoWriter
 * @author 殷亚
 * @date 2022/4/18 10:47
 * @see
 * @version 1.0
 */
@Service
@Scope("prototype")
public class IrsBaseInfoWriter extends InterfBaseWriter<BaseEntity> {

    /**
     * 交易类金融产品
     */
    @Autowired
    private ValTradeFinProductBatchMapper valTradeFinProductBatchMapper;

    /**
     * 金融产品分类
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
     * 现金流支付规则
     */
    @Autowired
    private ValCashPayRuleBatchMapper valCashPayRuleBatchMapper;
    /**
     * 现金流计划
     */
    @Autowired
    private ValCashPlanBatchMapper valCashPlanBatchMapper;

    @Autowired
    private MultilevelDataAuthorityService multilevelDataAuthorityService;

    @Value("${interf.properties.maxSqlNum}")
    private int maxSqlNum;
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

    @Autowired
    private ValCommonBatchMapper valCommonBatchMapper;// 去重数据Mapper

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends BaseEntity> items)
        throws Exception {
        List<IrsBaseInfoRecResult> resultList = (List<IrsBaseInfoRecResult>)items;
        for (IrsBaseInfoRecResult result : resultList) {
            Map<String, Object> paramMap = result.getParamMap();
            // 交易类金融产品
            List<FinTradeProduct> listValTradeFinProduct = result.getListValTradeFinProduct();
            if (CollectionUtils.isNotEmpty(listValTradeFinProduct)) {
                Map<String, FinTradeProduct> map_CQ = new HashMap<String, FinTradeProduct>();
                List<FinTradeProduct> listValTradeFinProduct_QC = new ArrayList<FinTradeProduct>();
                for (FinTradeProduct obj : listValTradeFinProduct) {
                    if (map_CQ.get(obj.getFinprodId() + obj.getFinprodType() + obj.getBranch()) == null) {
                        listValTradeFinProduct_QC.add(obj);
                    }
                    map_CQ.put(obj.getFinprodId() + obj.getFinprodType() + obj.getBranch(), obj);
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
                				DictCodeListExt3.共通业务类型_利率互换交易.getValue());
                    }
                }
            }
            // 金融产品分类
            List<FinProductType> listValFinProductType = result.getListValFinProductType();
            if (CollectionUtils.isNotEmpty(listValFinProductType)) {
                Map<String, FinProductType> map_CQ = new HashMap<String, FinProductType>();
                List<FinProductType> listValFinProductType_CQ = new ArrayList<FinProductType>();
                for (FinProductType obj : listValFinProductType) {
                    if (map_CQ.get(obj.getFinprodId()) == null) {
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
            /** 判断本次入库时的模板 **/
            String tmpEName = (String)paramMap.get("TMP_E_NAME");
            if (TempConstant.利率互换标的信息.equals(tmpEName)) {
                    if (CollectionUtils.isNotEmpty(listValTradeFinProduct)) {
                        // 1. 删除现金流信息表 --根据金融产品代码、金融产品类型 删除
                        valCommonBatchMapper.deleteFinCashInfo(listValTradeFinProduct);
                        // 2. 删除现金流计算规则表
                        valCommonBatchMapper.deleteFinCashCalcRule(listValTradeFinProduct);
                        // 3. 删除现金流支付规则表
                        valCommonBatchMapper.deleteFinCashPayRule(listValTradeFinProduct);

                    }
            }

            if (TempConstant.利率互换付息计划.equals(tmpEName)) {
                if (CollectionUtils.isNotEmpty(listValTradeFinProduct)) {
                    // 4. 删除现金流计划表
                    valCommonBatchMapper.deleteFinCashPlan(listValTradeFinProduct);

                }
            }




            // 现金流信息表
            List<FinCashInfo> listValCashInfo = result.getListValCashInfo();
            if (CollectionUtils.isNotEmpty(listValCashInfo)) {
                Map<String, FinCashInfo> mapValCashInfo = new HashMap<String, FinCashInfo>();
                List<FinCashInfo> rstValCashInfo = new ArrayList<FinCashInfo>();
                for (FinCashInfo obj : listValCashInfo) {
                    if (mapValCashInfo.get(obj.getCashId()) == null) {
                        rstValCashInfo.add(obj);
                    }
                    mapValCashInfo.put(obj.getCashId(), obj);
                }

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
            // 现金流计算规则表
            List<FinCashCalcRule> listValCashCalcRule = result.getListValCashCalcRule();
            if (CollectionUtils.isNotEmpty(listValCashCalcRule)) {
                Map<String, FinCashCalcRule> mapValCashCalcRule = new HashMap<String, FinCashCalcRule>();
                List<FinCashCalcRule> rstValCashCalcRule = new ArrayList<FinCashCalcRule>();
                for (FinCashCalcRule obj : listValCashCalcRule) {
                    if (mapValCashCalcRule.get(obj.getCashId() + obj.getEffDate()) == null) {
                        rstValCashCalcRule.add(obj);
                    }
                    mapValCashCalcRule.put(obj.getCashId() + obj.getEffDate(), obj);
                }
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
            // 现金流支付规则表
            List<FinCashPayRule> listValCashPayRule = result.getListValCashPayRule();
            if (CollectionUtils.isNotEmpty(listValCashPayRule)) {
                Map<String, FinCashPayRule> mapValCashPayRule = new HashMap<String, FinCashPayRule>();
                List<FinCashPayRule> rstValCashPayRule = new ArrayList<FinCashPayRule>();
                for (FinCashPayRule obj : listValCashPayRule) {
                    if (mapValCashPayRule.get(obj.getCashId() + obj.getEffDate()) == null) {
                        rstValCashPayRule.add(obj);
                    }
                    mapValCashPayRule.put(obj.getCashId() + obj.getEffDate(), obj);
                }

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
            // 现金流计划表
            List<FinCashPlan> listValCashPlan = result.getListValCashPlan();
            if (CollectionUtils.isNotEmpty(listValCashPlan)) {
                Map<String, FinCashPlan> mapValCashPlan = new HashMap<String, FinCashPlan>();
                List<FinCashPlan> rstValCashPlan = new ArrayList<FinCashPlan>();

                List<FinCashPlan> delFinCashPlanList = new ArrayList<FinCashPlan>();
                Map<String, FinCashPlan> delFinCashPlanMap = new HashMap<String, FinCashPlan>();

                for (FinCashPlan obj : listValCashPlan) {
                    if (mapValCashPlan.get(obj.getCashId() + obj.getCashNum()) == null) {
                        rstValCashPlan.add(obj);
                    }
                    mapValCashPlan.put(obj.getCashId() + obj.getCashNum(), obj);
                    delFinCashPlanMap.put(obj.getCashId(), obj);
                }

                for (String key : delFinCashPlanMap.keySet()) {
                    delFinCashPlanList.add(delFinCashPlanMap.get(key));
                }
                valCashPlanBatchMapper.deleteFinCashPlanByCashId(delFinCashPlanList);

                // 定义一个固定大小插入存放集合
                List<FinCashPlan> listInsert = new ArrayList<FinCashPlan>();
                for (FinCashPlan e : rstValCashPlan) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valCashPlanBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valCashPlanBatchMapper.insertBatch(listInsert);
                }
            }


            /** 现金流利率信息表**/
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
