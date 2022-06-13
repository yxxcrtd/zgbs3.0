/*
 * 文件名：BookDetailSubjectAllWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：huangjian
 * 修改时间：2018年6月1日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.informationsys.hqinfo.writer;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.core.entity.InterfExecuteInfo;
import com.joyintech.tams.valuation.interf.valuation.recv.informationsys.hqinfo.dto.HqDetailInfoProcessorResult;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
  * 行情信息接口Writer
 * @author wangwei1
 * @version 1.0 2018年6月28日
 * @see BookGenerateResultForCurrentCommonWriter
 * @since 1.0
 */
@Service
@Scope("prototype")
public class HqDetailInfoInfoRecWriter extends InterfBaseWriter<BaseEntity> {

    /**
     * 标的类金融产品Mapper
     */
    @Autowired
    private ValFundMarketInfoBatchMapper valFundMarketInfoBatchMapper;
    @Autowired
    private ValBondMarketInfoBatchMapper valBondMarketInfoBatchMapper;
    @Autowired
    private ValFinProductValuationBatchMapper valFinProductValuationBatchMapper;
//    @Autowired
//    private ValMstRateInfoBatchMapper valMstRateInfoBatchMapper;
    @Autowired
    private ValMstIntRateInfoBatchMapper valMstIntRateInfoBatchMapper;
    @Autowired
    private ValBondValuationInfoBatchMapper valBondValuattionInfoBatchMapper;
    @Autowired
    private InterfCommonService interfCommonService;
    @Autowired
    private ValCashCalcRuleBatchMapper valCashCalcRuleBatchMapper;
    /** add by dongjun on 20181011 title:债券行权计划 start**/
    @Autowired
    private ValExercisePlanBatchMapper valExercisePlanBatchMapper;
    @Autowired
    private ValCashInfoBatchMapper valCashInfoBatchMapper;
    /** add by dongjun on 20181011 title:债券行权计划 end**/
    /**
     * 日志
     */
    @Autowired
    private ValInterflogBatchMapper valInterflogBatchMapper;

    /** add by yanya on 20210831 增加股票行情接口 start**/
    @Autowired
    ValStockQuotationInfoMapper valStockQuotationInfoMapper;
    /** add by yanya on 20210831 增加股票行情接口 end**/

    /** add by weipeng on 20220429 增加股票流动性折扣信息、指数行情接口 start**/
    @Autowired
    ValStockLiquidDiscountInfoBatchMapper valStockLiquidDiscountInfoBatchMapper;
    @Autowired
    ValStockIndicatorQuotationBatchMapper valStockIndicatorQuotationBatchMapper;
    /** add by yanya on 20210831 增加股票行情接口 end**/
    @Value("${interf.properties.maxSqlNum}")
    private int maxSqlNum;

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
        List<HqDetailInfoProcessorResult> resultList = (List<HqDetailInfoProcessorResult>)items;
        
        InterfExecuteInfo interfExecuteInfo = (InterfExecuteInfo)this.getParams().get("executeInfo");
        // 循环processor结果DTO，读取所有的入库数据结构LIST，并进行入库操作。
        for (HqDetailInfoProcessorResult result : resultList) {
            // 基金市场行情
            List<MstFundMarketInfo> valFundMarketInfoList = result.getListValFundMarketInfo();
            // 入库
            if (CollectionUtils.isNotEmpty(valFundMarketInfoList)) {
                // 定义一个固定大小插入存放集合
                List<MstFundMarketInfo> listInsert = new ArrayList<MstFundMarketInfo>();
                for (MstFundMarketInfo e : valFundMarketInfoList) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valFundMarketInfoBatchMapper.deleteData(listInsert);
                        valFundMarketInfoBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valFundMarketInfoBatchMapper.deleteData(listInsert);
                    valFundMarketInfoBatchMapper.insertBatch(listInsert);
                }
            }
                        
            //债券市场行情
            List<MstBondMarketInfo> valBondMarketInfoList = result.getListValBondMarketInfo();
            // 入库
            if (CollectionUtils.isNotEmpty(valBondMarketInfoList)) {
                // 定义一个固定大小插入存放集合
                List<MstBondMarketInfo> listInsert = new ArrayList<MstBondMarketInfo>();
                for (MstBondMarketInfo e : valBondMarketInfoList) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valBondMarketInfoBatchMapper.deleteData(listInsert);
                        valBondMarketInfoBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valBondMarketInfoBatchMapper.deleteData(listInsert);
                    valBondMarketInfoBatchMapper.insertBatch(listInsert);
                }
            }
            
            //场外金融产品行情
            List<FinProductValuation> valFinProductValuationList = result.getListValFinProductValuation();
            // 入库
            if (CollectionUtils.isNotEmpty(valFinProductValuationList)) {
                // 定义一个固定大小插入存放集合
                List<FinProductValuation> listInsert = new ArrayList<FinProductValuation>();
                for (FinProductValuation e : valFinProductValuationList) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valFinProductValuationBatchMapper.deleteData(listInsert);
                        valFinProductValuationBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valFinProductValuationBatchMapper.deleteData(listInsert);
                    valFinProductValuationBatchMapper.insertBatch(listInsert);
                }
            }
            
//            //行情信息表
//            List<MstRateInfo> mstRateInfoList = result.getMstRateInfoList();
//            // 入库
//            if (CollectionUtils.isNotEmpty(mstRateInfoList)) {
//                // 定义一个固定大小插入存放集合
//                List<MstRateInfo> listInsert = new ArrayList<MstRateInfo>();
//                for (MstRateInfo e : mstRateInfoList) {
//                    // 小于最大值
//                    if (listInsert.size() < maxSqlNum) {
//                        listInsert.add(e);
//                    }
//                    else if (listInsert.size() >= maxSqlNum) {
//                    	valMstRateInfoBatchMapper.deleteData(listInsert);
//                    	valMstRateInfoBatchMapper.insertBatch(listInsert);
//                        listInsert.clear();
//                        listInsert.add(e);
//                    }
//                }
//                // 如果循环结束，小于最大值
//                if (CollectionUtils.isNotEmpty(listInsert)) {
//                	valMstRateInfoBatchMapper.deleteData(listInsert);
//                	valMstRateInfoBatchMapper.insertBatch(listInsert);
//                }
//            }
            
            
            //利率行情表
            List<MstIntRateInfo> mstIntRateInfoList = result.getMstIntRateInfoList();
            // 入库
            if (CollectionUtils.isNotEmpty(mstIntRateInfoList)) {
                // 定义一个固定大小插入存放集合
                List<MstIntRateInfo> listInsert = new ArrayList<MstIntRateInfo>();
                for (MstIntRateInfo e : mstIntRateInfoList) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                    	valMstIntRateInfoBatchMapper.deleteData(listInsert);
                    	valMstIntRateInfoBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                	valMstIntRateInfoBatchMapper.deleteData(listInsert);
                	valMstIntRateInfoBatchMapper.insertBatch(listInsert);
                }
            }
            
            /**现金流利率信息表-更新 add by wujie on 20180910 start**/
            List<FinCashCalcRule> listFinCashCalcRule = interfCommonService.getFinCashCalcRule(mstIntRateInfoList,interfExecuteInfo.getInterfDate());
           //TODO  
//            List<FinCashCalcRule> listFinCashCalcRule = new ArrayList<>();
//            listFinCashCalcRule.add(interfCommonService.getFinCashCalcRule(new FinRateInfo()));
            
            if (CollectionUtils.isNotEmpty(listFinCashCalcRule)) {
            	// 定义一个固定大小插入存放集合
                List<FinCashCalcRule> listInsert = new ArrayList<FinCashCalcRule>();
                for (FinCashCalcRule e : listFinCashCalcRule) {
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
            /**add by wujie on 20180910 end**/

            //债券估值
            List<MstBondValuationInfo> mstBondValuationInfoList = result.getListBondValuation();
            // 入库
            if (CollectionUtils.isNotEmpty(mstBondValuationInfoList)) {
                // 定义一个固定大小插入存放集合
                List<MstBondValuationInfo> listInsert = new ArrayList<MstBondValuationInfo>();
                for (MstBondValuationInfo e : mstBondValuationInfoList) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valBondValuattionInfoBatchMapper.deleteData(listInsert);
                        valBondValuattionInfoBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valBondValuattionInfoBatchMapper.deleteData(listInsert);
                    valBondValuattionInfoBatchMapper.insertBatch(listInsert);
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
           
            /** add by dongjun on 20181011 title:债券行权计划 start**/
            // 债券行权
            List<FinExercisePlan> finExercisePlanList = result.getFinExercisePlanList();
            // 入库
            if (CollectionUtils.isNotEmpty(finExercisePlanList)) {
                // 定义一个固定大小插入存放集合
                List<FinExercisePlan> listInsert = new ArrayList<FinExercisePlan>();
                for (FinExercisePlan e : finExercisePlanList) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valExercisePlanBatchMapper.deleteBatch(listInsert);
                        valExercisePlanBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valExercisePlanBatchMapper.deleteBatch(listInsert);
                    valExercisePlanBatchMapper.insertBatch(listInsert);
                }              
            }
            
            //债券行权现金流信息生成
            List<FinCashInfo> listFinCashInfo = interfCommonService.secVestFinCashInfoInit(finExercisePlanList);
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
            /** add by dongjun on 20181011 title:债券行权计划 end**/

            /** add by yanya on 20210831 增加股票行情接口 end**/
            List<MstSharePrice> valShareInfoList = result.getValShareInfo();
            // 入库
            if (CollectionUtils.isNotEmpty(valShareInfoList)) {
                // 定义一个固定大小插入存放集合
                List<MstSharePrice> listInsert = new ArrayList<MstSharePrice>();
                for (MstSharePrice e : valShareInfoList) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valStockQuotationInfoMapper.deleteBatch(listInsert);
                        valStockQuotationInfoMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valStockQuotationInfoMapper.deleteBatch(listInsert);
                    valStockQuotationInfoMapper.insertBatch(listInsert);
                }
            }
            /** add by yanya on 20210831 增加股票行情接口 end**/

            /** add by weipeng on 20220428 增加股票流动性折扣信息、指数行情 start**/
            List<MstStockLiquidDiscountInfo> valDiscountInfoList = result.getMstStockLiquidDiscountInfoList();
            // 入库
            if (CollectionUtils.isNotEmpty(valDiscountInfoList)) {
                // 定义一个固定大小插入存放集合
                List<MstStockLiquidDiscountInfo> listInsert = new ArrayList<MstStockLiquidDiscountInfo>();
                for (MstStockLiquidDiscountInfo e : valDiscountInfoList) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valStockLiquidDiscountInfoBatchMapper.deleteBatch(listInsert);
                        valStockLiquidDiscountInfoBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valStockLiquidDiscountInfoBatchMapper.deleteBatch(listInsert);
                    valStockLiquidDiscountInfoBatchMapper.insertBatch(listInsert);
                }
            }
            List<MstIndicatorQuotation> valIndicatorInfoList = result.getMstIndicatorQuotationList();
            // 入库
            if (CollectionUtils.isNotEmpty(valIndicatorInfoList)) {
                // 定义一个固定大小插入存放集合
                List<MstIndicatorQuotation> listInsert = new ArrayList<MstIndicatorQuotation>();
                for (MstIndicatorQuotation e : valIndicatorInfoList) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valStockIndicatorQuotationBatchMapper.deleteBatch(listInsert);
                        valStockIndicatorQuotationBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valStockIndicatorQuotationBatchMapper.deleteBatch(listInsert);
                    valStockIndicatorQuotationBatchMapper.insertBatch(listInsert);
                }
            }
            /** add by weipeng on 20220428 增加股票流动性折扣信息、指数行情 end**/
        }

    }

}
