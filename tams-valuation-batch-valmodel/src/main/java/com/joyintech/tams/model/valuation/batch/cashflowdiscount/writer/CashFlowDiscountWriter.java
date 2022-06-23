/*
 * 文件名：AssetIntdateCarryoverWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chengzhong
 * 修改时间：2018年5月9日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.model.valuation.batch.cashflowdiscount.writer;


import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.FinProductValuation;
import com.joyintech.tams.common.base.entity.ModCurveResult;
import com.joyintech.tams.common.base.entity.ModRiskPremium;
import com.joyintech.tams.common.base.entity.MstBondValuationInfo;
import com.joyintech.tams.model.valuation.batch.cashflowdiscount.dao.CashFlowDiscountMapper;
import com.joyintech.tams.model.valuation.batch.cashflowdiscount.dto.CashFlowDiscountResult;
import com.joyintech.tams.model.valuation.batch.cashflowdiscount.dto.ModCurveResultDetail;
import com.joyintech.tams.valuation.common.utils.DataHandleUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 现金流贴现批处理(写入)
 * @author zznn
 * @date 20210706
 */
@Service
@Scope("prototype")
public class CashFlowDiscountWriter extends BaseWriter<NeedToWriteEntity> implements StepExecutionListener {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private StepExecution stepExecution;

    @Autowired
    private CashFlowDiscountMapper cashFlowDiscountMapper;
    @Override
    public void write(List<? extends NeedToWriteEntity> items) throws Exception {
        List<CashFlowDiscountResult> resultList = (List<CashFlowDiscountResult>)items;
        List<ModRiskPremium> modRiskPremiums = new ArrayList<>();
        List<ModCurveResult> modCurveResults = new ArrayList<>();
        List<ModCurveResultDetail> modCurveResultDetails = new ArrayList<>();
        List<MstBondValuationInfo> mstBondValuationInfos = new ArrayList<>();
        List<FinProductValuation> finProductValuations = new ArrayList<>();
        if(!StringUtil.isEmptyList(resultList)){
            resultList.forEach(item ->{
                Map<String,Object> map = new HashMap<>();
                ModCurveResult modCurveResult = item.getModCurveResult();
                ModRiskPremium modRiskPremium = item.getModRiskPremium();
                if(null!=modCurveResult){
                    map.put("finprodId", modCurveResult.getFinprodId());
                    map.put("cdate", modCurveResult.getCdate());
                    modCurveResults.add(item.getModCurveResult());
                    cashFlowDiscountMapper.deleteCurveResult(map);
                }
                if(null!=modRiskPremium){
                    map.put("basedate", modRiskPremium.getBasedate());
                    modRiskPremiums.add(modRiskPremium);
                    cashFlowDiscountMapper.deleteRiskPremium(map);
                }
                if(!StringUtil.isEmptyList(item.getModCurveResultDetails())){
                    modCurveResultDetails.addAll(item.getModCurveResultDetails());
                    cashFlowDiscountMapper.deleteCurveResultDetail(map);
                }
                if(null!=item.getMstBondValuationInfo()){
                    mstBondValuationInfos.add(item.getMstBondValuationInfo());
                }
                if(null!=item.getFinProductValuation()){
                    finProductValuations.add(item.getFinProductValuation());
                }
            });
            if(CollectionUtils.isNotEmpty(modRiskPremiums)){
                cashFlowDiscountMapper.insertBatchRiskPremium(modRiskPremiums);
            }
            if(CollectionUtils.isNotEmpty(modCurveResults)){
                cashFlowDiscountMapper.insertBatchCurveResult(modCurveResults);
            }
            if(CollectionUtils.isNotEmpty(modCurveResultDetails)){
                cashFlowDiscountMapper.insertBatchCurveResultDetail(modCurveResultDetails);
            }
            if(CollectionUtils.isNotEmpty(finProductValuations)){
                deleteFinProductValuation(finProductValuations);
                insertFinProductValuation(finProductValuations);
            }
            if(CollectionUtils.isNotEmpty(mstBondValuationInfos)){
                deleteMstBondQuotationInfo(mstBondValuationInfos);
                insertMstBondQuotationInfo(mstBondValuationInfos);
            }
        }
    }

    /**
     * 批量插入其他债权类估值
     * @param finProductValuations
     */
    private void insertFinProductValuation(List<FinProductValuation> finProductValuations) {
        if (finProductValuations == null || finProductValuations.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<FinProductValuation>> list = DataHandleUtil.splitList(finProductValuations);
        for (List<FinProductValuation> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                cashFlowDiscountMapper.insertFinProductValuation(listInsert);
            }
        }
    }

    /**
     * 批量删除其他债权类估值
     * @param finProductValuations
     */
    private void deleteFinProductValuation(List<FinProductValuation> finProductValuations) {
        if (finProductValuations == null || finProductValuations.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<FinProductValuation>> list = DataHandleUtil.splitList(finProductValuations);
        for (List<FinProductValuation> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                cashFlowDiscountMapper.deleteFinProductValuation(listInsert);
            }
        }
    }

    /**
     * 插入债券估值行情
     * @param listMstBondValuationInfo
     */
    private void insertMstBondQuotationInfo(List<MstBondValuationInfo> listMstBondValuationInfo) {
        if (listMstBondValuationInfo == null || listMstBondValuationInfo.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<MstBondValuationInfo>> list = DataHandleUtil.splitList(listMstBondValuationInfo);
        for (List<MstBondValuationInfo> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                cashFlowDiscountMapper.insertMstBondValuationInfo(listInsert);
            }
        }
    }

    /**
     * 删除债券估值行情
     * @param listMstBondValuationInfo
     */
    private void deleteMstBondQuotationInfo(List<MstBondValuationInfo> listMstBondValuationInfo) {
        if (listMstBondValuationInfo == null || listMstBondValuationInfo.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<MstBondValuationInfo>> list = DataHandleUtil.splitList(listMstBondValuationInfo);
        for (List<MstBondValuationInfo> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                cashFlowDiscountMapper.deleteMstBondValuationInfo(listInsert);
            }
        }
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return stepExecution.getExitStatus();
    }

}
