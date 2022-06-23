/*
 * 文件名：ResetBaseRuleInfoWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2019年2月16日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.model.valuation.batch.incomemethodmodel.writer;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.framework.base.entity.SysBatchExecInfo;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.ModCurveResult;
import com.joyintech.tams.common.base.entity.ModRiskPremium;
import com.joyintech.tams.common.base.entity.ModValBatchParams;
import com.joyintech.tams.common.base.entity.MstBondValuationInfo;
import com.joyintech.tams.model.valuation.batch.incomemethodmodel.dao.IncomeMethodMapper;
import com.joyintech.tams.model.valuation.batch.incomemethodmodel.dto.ProductAssetsIncomeMethodResultDto;
import com.joyintech.tams.valuation.common.utils.DataHandleUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description:收益法估值批处理Writer
 * @auther: yuzj
 * @date: 2021/7/6
 * @param:
 * @return:
 */
@Service
@Scope("prototype")
public class ProductAssetsIncomeMethodWriter extends BaseWriter<ProductAssetsIncomeMethodResultDto> implements StepExecutionListener {

    @Autowired
	private IncomeMethodMapper incomeMethodMapper;

    private StepExecution stepExecution;

    private final String BATCH_LOG_RESULTS = "logResults";


    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution=stepExecution;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void write(List<? extends ProductAssetsIncomeMethodResultDto> items)
            throws Exception {
        Map<String,Object> mapParam = this.getParams();
        List<ProductAssetsIncomeMethodResultDto> resultList = (List<ProductAssetsIncomeMethodResultDto>)items;
        List<ModCurveResult>   valuationResultList = new ArrayList<ModCurveResult>();
//        List<ModRiskPremium>   makePriceResultList = new ArrayList<ModRiskPremium>();
        List<MstBondValuationInfo> mstBondValuationInfos = new ArrayList<MstBondValuationInfo>();


        if (!StringUtil.isEmptyList(resultList)) {
            for (ProductAssetsIncomeMethodResultDto incomeMethodResultDto : resultList) {
                if(incomeMethodResultDto.getModCurveResult() !=null){
                    valuationResultList.add(incomeMethodResultDto.getModCurveResult());
                }
//                if(incomeMethodResultDto.getModRiskPremium() !=null){
//                    makePriceResultList.add(incomeMethodResultDto.getModRiskPremium());
//                }
                if(incomeMethodResultDto.getMstBondValuationInfo() !=null){
                    mstBondValuationInfos.add(incomeMethodResultDto.getMstBondValuationInfo());
                }

            }
        }
        //估值结果
        if (valuationResultList != null && valuationResultList.size() > 0) {
            deleteValuationResult(valuationResultList);
            insertValuationResult(valuationResultList);
        }
//        资产定价批操作
//        if (makePriceResultList != null && makePriceResultList.size() > 0) {
//            deleteMakePriceResultPremium(makePriceResultList);
//            insertMakePriceResultPremium(makePriceResultList);
//        }
        //债券估值行情数据
        if (mstBondValuationInfos != null && mstBondValuationInfos.size() > 0) {
            deleteMstBondQuotationInfo(mstBondValuationInfos);
            insertMstBondQuotationInfo(mstBondValuationInfos);
        }
    }

    /**
     * 插入债券估值行情
     * @param listMstBondQuotationInfoDto
     */
    private void insertMstBondQuotationInfo(List<MstBondValuationInfo> listMstBondQuotationInfoDto) {
        if (listMstBondQuotationInfoDto == null || listMstBondQuotationInfoDto.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<MstBondValuationInfo>> list = DataHandleUtil.splitList(listMstBondQuotationInfoDto);
        for (List<MstBondValuationInfo> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                incomeMethodMapper.insertMstBondValuationInfo(listInsert);
            }
        }
    }

    /**
     * 删除债券估值行情
     * @param listMstBondQuotationInfoDto
     */
    private void deleteMstBondQuotationInfo(List<MstBondValuationInfo> listMstBondQuotationInfoDto) {
        if (listMstBondQuotationInfoDto == null || listMstBondQuotationInfoDto.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<MstBondValuationInfo>> list = DataHandleUtil.splitList(listMstBondQuotationInfoDto);
        for (List<MstBondValuationInfo> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                incomeMethodMapper.deleteMstBondValuationInfo(listInsert);
            }
        }
    }


    /**
     * 插入债券估值行情结果集
     * @param valuationResultList
     */
    private void insertValuationResult(List<ModCurveResult> valuationResultList) {
        if (valuationResultList == null || valuationResultList.isEmpty()) {
            return;
        }
//        List<ModValBatchParams> modValBatchParamsList = new ArrayList<>();
        // 定义一个固定大小插入存放集合
        List<List<ModCurveResult>> list = DataHandleUtil.splitList(valuationResultList);
        for (List<ModCurveResult> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                incomeMethodMapper.insertValuationResult(listInsert);
            }
        }

    }

    /**
     * 删除债券估值行情结果集
     * @param listModRiskPremium
     */
    private void deleteValuationResult(List<ModCurveResult> listModRiskPremium) {
        if (listModRiskPremium == null || listModRiskPremium.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<ModCurveResult>> list = DataHandleUtil.splitList(listModRiskPremium);
        for (List<ModCurveResult> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                incomeMethodMapper.deleteValuationResult(listInsert);
            }
        }
    }


    /**
     * 插入资产定价结果集
     * @param makePriceResultList
     */
//    private void insertMakePriceResultPremium(List<ModRiskPremium> makePriceResultList) {
//
//        if (makePriceResultList == null || makePriceResultList.isEmpty()) {
//            return;
//        }
//        //日志list
//        List<ModValBatchParams> modValBatchParamsList = new ArrayList<>();
//        // 定义一个固定大小插入存放集合
//        List<List<ModRiskPremium>> list = DataHandleUtil.splitList(makePriceResultList);
//        for (List<ModRiskPremium> listInsert : list) {
//            if (CollectionUtils.isNotEmpty(listInsert)) {
//                incomeMethodMapper.insertModRiskPremium(listInsert);
//            }
//        }
//
//    }

    /**
     * 删除资产定价结果集
     * @param makePriceResultList
     */
//    private void deleteMakePriceResultPremium(List<ModRiskPremium> makePriceResultList) {
//        if (makePriceResultList == null || makePriceResultList.isEmpty()) {
//            return;
//        }
//        // 定义一个固定大小插入存放集合
//        List<List<ModRiskPremium>> list = DataHandleUtil.splitList(makePriceResultList);
//        for (List<ModRiskPremium> listInsert : list) {
//            if (CollectionUtils.isNotEmpty(listInsert)) {
//                incomeMethodMapper.deleteModRiskPremium(listInsert);
//            }
//        }
//    }
}