/*
 * 文件名：FundBaseInfoRecWriter.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：HuangJian 修改时间：2018年6月26日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.penaltyinterest.writer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.common.base.dao.FinTradeProductAddMapper;
import com.joyintech.tams.common.base.dao.FinTradeProductMapper;
import com.joyintech.tams.common.base.entity.FinCashCalcRule;
import com.joyintech.tams.common.base.entity.FinCashInfo;
import com.joyintech.tams.common.base.entity.FinCashPlan;
import com.joyintech.tams.common.base.entity.FinTradeProduct;
import com.joyintech.tams.common.base.entity.FinTradeProductAdd;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCashCalcRuleBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCashInfoBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCashPlanBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValInterflogBatchMapper;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.baseinfo.penaltyinterest.dto.PenaltyInterestRecResult;

/**
 * 基金类资产 FundBaseInfoRecWriter
 * 
 * @author zhangquan
 * @version 1.0 2019年12月27日
 * @see PenaltyInterestInfoWriter
 * @since 1.0
 */
@Service
@Scope("prototype")
public class PenaltyInterestInfoWriter extends InterfBaseWriter<BaseEntity> {

    /**
     * 交易类金融产品
     */
    @Autowired
    private FinTradeProductMapper finTradeProductMapper;

    /**
     * 交易类金融产品
     */
    @Autowired
    private FinTradeProductAddMapper finTradeProductAddMapper;

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
     * 现金流计划
     */
    @Autowired
    private ValCashPlanBatchMapper valCashPlanBatchMapper;

    @Value("${interf.properties.maxSqlNum}")
    private int maxSqlNum;

    /** add by wujie on 20180823 end **/

    /**
     * 日志
     */
    @Autowired
    private ValInterflogBatchMapper valInterflogBatchMapper;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends BaseEntity> items)
        throws Exception {
        List<PenaltyInterestRecResult> resultList = (List<PenaltyInterestRecResult>)items;
        for (PenaltyInterestRecResult result : resultList) {
            // 交易类金融产品表
            List<FinTradeProduct> listFinTradeProduct = result.getListValTradeFinProduct();
            if (CollectionUtils.isNotEmpty(listFinTradeProduct)) {
                // 循环更新操作
                for (FinTradeProduct e : listFinTradeProduct) {
                    finTradeProductMapper.updateByPrimaryKeySelective(e);
                }
            }
            // 交易类金融产品附属表
            List<FinTradeProductAdd> listFinTradeProductAdd = result.getListValTradeFinProductAdd();
            if (CollectionUtils.isNotEmpty(listFinTradeProductAdd)) {
                // 循环更新操作
                for (FinTradeProductAdd e : listFinTradeProductAdd) {
                    int count = finTradeProductAddMapper.updateByPrimaryKeySelective(e);
                    // 若无数据，则插入
                    if(count == 0){
                        finTradeProductAddMapper.insert(e);
                    }
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
            // 现金流计划表
            List<FinCashPlan> listValCashPlan = result.getListValCashPlan();
            if (CollectionUtils.isNotEmpty(listValCashPlan)) {
                Map<String, FinCashPlan> mapValCashPlan = new HashMap<String, FinCashPlan>();
                List<FinCashPlan> rstValCashPlan = new ArrayList<FinCashPlan>();

                /** add by wujie on 20190107 start **/
                List<FinCashPlan> delFinCashPlanList = new ArrayList<FinCashPlan>();
                Map<String, FinCashPlan> delFinCashPlanMap = new HashMap<String, FinCashPlan>();
                /** add by wujie on 20190107 end **/

                for (FinCashPlan obj : listValCashPlan) {
                    if (mapValCashPlan.get(obj.getCashId() + obj.getCashNum()) == null) {
                        rstValCashPlan.add(obj);
                    }
                    mapValCashPlan.put(obj.getCashId() + obj.getCashNum(), obj);

                    /** add by wujie on 20190107 start **/
                    delFinCashPlanMap.put(obj.getCashId(), obj);
                    /** add by wujie on 20190107 end **/
                }

                /** add by wujie on 20190107 start **/
                for (String key : delFinCashPlanMap.keySet()) {
                    delFinCashPlanList.add(delFinCashPlanMap.get(key));
                }
                valCashPlanBatchMapper.deleteFinCashPlanByCashId(delFinCashPlanList);
                /** add by wujie on 20190107 end **/

                // 定义一个固定大小插入存放集合
                List<FinCashPlan> listInsert = new ArrayList<FinCashPlan>();
                for (FinCashPlan e : rstValCashPlan) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        /**
                         * modify by wujie on 20190107 valCashPlanBatchMapper.deleteBatch(listInsert);
                         **/
                        valCashPlanBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    /**
                     * modify by wujie on 20190107 valCashPlanBatchMapper.deleteBatch(listInsert);
                     **/
                    valCashPlanBatchMapper.insertBatch(listInsert);
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
