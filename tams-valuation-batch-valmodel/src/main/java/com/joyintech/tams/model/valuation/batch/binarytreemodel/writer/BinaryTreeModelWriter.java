package com.joyintech.tams.model.valuation.batch.binarytreemodel.writer;

import com.joyintech.batch.batchbase.baseclass.BaseWriter;
import com.joyintech.tams.common.base.entity.ModCurveResult;
import com.joyintech.tams.common.base.entity.ModRiskPremium;
import com.joyintech.tams.common.base.entity.MstBondValuationInfo;
import com.joyintech.tams.model.valuation.batch.binarytreemodel.dao.BinaryTreeModelMapper;
import com.joyintech.tams.model.valuation.batch.binarytreemodel.dto.BinaryTreeModelResultDto;
import com.joyintech.tams.valuation.common.utils.DataHandleUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BinaryTreeModelWriter extends BaseWriter<BinaryTreeModelResultDto> {
    /**
     * Mapper
     */
    @Autowired
    private BinaryTreeModelMapper binaryTreeModelMapper;

    @Override
    public void write(List<? extends BinaryTreeModelResultDto> items) throws Exception {
        List<BinaryTreeModelResultDto> resultList = (List<BinaryTreeModelResultDto>)items;
        List<ModCurveResult> listModCurveResult = new ArrayList<ModCurveResult>();
        List<ModRiskPremium> listModRiskPremium = new ArrayList<ModRiskPremium>();
        List<MstBondValuationInfo> listMstBondValuationInfo = new ArrayList<MstBondValuationInfo>();
        if (resultList != null && resultList.size() > 0) {
            for (BinaryTreeModelResultDto dto : resultList) {
                if(dto.getModCurveResult()!=null){
                    listModCurveResult.add(dto.getModCurveResult());
                }
                if(dto.getModRiskPremium()!=null){
                    listModRiskPremium.add(dto.getModRiskPremium());
                }
                if(dto.getMstBondValuationInfo()!=null){
                    listMstBondValuationInfo.add(dto.getMstBondValuationInfo());
                }
            }
        }

        if (listModCurveResult != null && listModCurveResult.size() > 0) {
            deleteModCurveResult(listModCurveResult);
            insertlistModCurveResult(listModCurveResult);
        }

        if (listModRiskPremium != null && listModRiskPremium.size() > 0) {
            deleteModRiskPremium(listModRiskPremium);
            insertModRiskPremium(listModRiskPremium);
        }
        if (listMstBondValuationInfo != null && listMstBondValuationInfo.size() > 0) {
            deleteMstBondQuotationInfo(listMstBondValuationInfo);
            insertMstBondQuotationInfo(listMstBondValuationInfo);
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
                binaryTreeModelMapper.insertMstBondValuationInfo(listInsert);
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
                binaryTreeModelMapper.deleteMstBondValuationInfo(listInsert);
            }
        }
    }

    /**
     * 删除结果集
     * @param listModCurveResult
     */
    private void deleteModCurveResult(List<ModCurveResult> listModCurveResult) {
        if (listModCurveResult == null || listModCurveResult.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<ModCurveResult>> list = DataHandleUtil.splitList(listModCurveResult);
        for (List<ModCurveResult> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                binaryTreeModelMapper.deleteModCurveResult(listInsert);
            }
        }
    }

    /**
     * 插入结果集
     * @param listModCurveResult
     */
    private void insertlistModCurveResult(List<ModCurveResult> listModCurveResult) {
        if (listModCurveResult == null || listModCurveResult.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<ModCurveResult>> list = DataHandleUtil.splitList(listModCurveResult);
        for (List<ModCurveResult> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                binaryTreeModelMapper.insertModCurveResult(listInsert);
            }
        }
    }

    /**
     * 插入结果集
     * @param listModRiskPremium
     */
    private void insertModRiskPremium(List<ModRiskPremium> listModRiskPremium) {
        if (listModRiskPremium == null || listModRiskPremium.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<ModRiskPremium>> list = DataHandleUtil.splitList(listModRiskPremium);
        for (List<ModRiskPremium> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                binaryTreeModelMapper.insertModRiskPremium(listInsert);
            }
        }
    }

    /**
     * 删除结果集
     * @param listModRiskPremium
     */
    private void deleteModRiskPremium(List<ModRiskPremium> listModRiskPremium) {
        if (listModRiskPremium == null || listModRiskPremium.isEmpty()) {
            return;
        }
        // 定义一个固定大小插入存放集合
        List<List<ModRiskPremium>> list = DataHandleUtil.splitList(listModRiskPremium);
        for (List<ModRiskPremium> listInsert : list) {
            if (CollectionUtils.isNotEmpty(listInsert)) {
                binaryTreeModelMapper.deleteModRiskPremium(listInsert);
            }
        }
    }

}
