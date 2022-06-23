/*
 * 文件名：PrdSupplementDealRecWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：dongjun
 * 修改时间：2019年2月21日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.prdclawback.writer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.common.base.entity.BokDealAccounting;
import com.joyintech.tams.common.base.entity.BokDealAccountingDetail;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValBokDetailIdBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValDealAccountingBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValDealAccountingDetailBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductDealBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValInterflogBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValTradeFeeDetailBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValTrdProductDealAddBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.core.entity.InterfExecuteInfo;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.prdclawback.dto.PrdSupplementDealRecResult;

/**
 * 产品回补交易Writer
 * 
 * @author dongjun
 * @version 1.0 2019年2月21日
 * @see PrdSupplementDealRecWriter
 * @since 1.0
 */
@Service
public class PrdSupplementDealRecWriter extends InterfBaseWriter<BaseEntity> {

    /**
     * 金融产品交易BatchMapper
     */
    @Autowired
    private ValFinProductDealBatchMapper valFinProductDealBatchMapper;

    /**
     * 金融产品交易扩展表BatchMapper
     */
    @Autowired
    private ValTrdProductDealAddBatchMapper valFinProductDealExtBatchMapper;

    /**
     * 入账信息BatchMapper
     */
    @Autowired
    private ValDealAccountingBatchMapper valDealAccountingBatchMapper;

    /**
     * 账务明细BatchMapper
     */
    @Autowired
    private ValDealAccountingDetailBatchMapper valDealAccountingDetailBatchMapper;

    /**
     * 交易费用明细BatchMapper
     */
    @Autowired
    private ValTradeFeeDetailBatchMapper valTradeFeeDetailBatchMapper;

    @Autowired
    private ValBokDetailIdBatchMapper valBokDetailIdBatchMapper;

    /**
     * 日志
     */
    @Autowired
    private ValInterflogBatchMapper valInterflogBatchMapper;

    @Autowired
    private InterfCommonService interfCommonService;

    @Value("${interf.properties.maxSqlNum}")
    private int maxSqlNum;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({"unchecked"})
    @Override
    public void write(List<? extends BaseEntity> items)
        throws Exception {
        InterfExecuteInfo interfExecuteInfo = (InterfExecuteInfo)this.getParams().get(
            "executeInfo");
        String interfDate = new SimpleDateFormat("yyyyMMdd").format(
            interfExecuteInfo.getInterfDate());

        // 获取process返回组合的list值
        List<PrdSupplementDealRecResult> resultList = (List<PrdSupplementDealRecResult>)items;

        // 循环基金类processor结果DTO，读取所有的入库数据结构LIST，并进行入库操作。
        for (PrdSupplementDealRecResult result : resultList) {
            // 日志
            List<ValInterflog> valInterflog = result.getListValInterflog();
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

            String isDealTrade = result.getParamMap().get(InterfCommonConstant.读取文件状态) == null? DictCodeList.是否_否.getValue():(String)result.getParamMap().get(InterfCommonConstant.读取文件状态);
            //如果没有数据就跳出循环
            if(result.getDataNum() == 0 || CollectionUtils.isEmpty(result.getListValFinProductDeal())) {
                if(DictCodeList.是否_是.getValue().equals(isDealTrade)){
                    interfCommonService.dealBatchTrdFinprodDeal(result.getParamMap(),interfDate);
                }
                continue;
            }

            Map<String, Object> paramMap = result.getParamMap();
            interfCommonService.dealBatchTrdFinprodDeal(paramMap, interfDate);

            // 金融产品交易
            List<TrdProductDeal> listValFinProductDeal = result.getListValFinProductDeal();
            if (CollectionUtils.isNotEmpty(listValFinProductDeal)) {
                // 定义一个固定大小插入存放集合
                List<TrdProductDeal> listInsert = new ArrayList<TrdProductDeal>();

                for (TrdProductDeal e : listValFinProductDeal) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valFinProductDealBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valFinProductDealBatchMapper.insertBatch(listInsert);
                }
            }

            // 金融产品交易扩展表
            List<TrdProductDealAdd> listValFinProductDealExt = result.getListValFinProductDealExt();
            // 入库
            if (CollectionUtils.isNotEmpty(listValFinProductDealExt)) {
                // 定义一个固定大小插入存放集合
                List<TrdProductDealAdd> listInsert = new ArrayList<TrdProductDealAdd>();

                for (TrdProductDealAdd e : listValFinProductDealExt) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valFinProductDealExtBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valFinProductDealExtBatchMapper.insertBatch(listInsert);
                }
            }

            // 入账信息表
            List<BokDealAccounting> listValDealAccounting = result.getBokDealAccountingList();
            // 入库
            if (CollectionUtils.isNotEmpty(listValDealAccounting)) {
                // 定义一个固定大小插入存放集合
                List<BokDealAccounting> listInsert = new ArrayList<BokDealAccounting>();

                for (BokDealAccounting e : listValDealAccounting) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valDealAccountingBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valDealAccountingBatchMapper.insertBatch(listInsert);
                }
            }

            // 账务明细表
            List<BokDealAccountingDetail> listValDealAccountingDetail = result.getBokDealAccountingDetailList();
            // 入库
            if (CollectionUtils.isNotEmpty(listValDealAccountingDetail)) {
                // 定义一个固定大小插入存放集合
                List<BokDealAccountingDetail> listInsert = new ArrayList<BokDealAccountingDetail>();

                for (BokDealAccountingDetail e : listValDealAccountingDetail) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valDealAccountingDetailBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valDealAccountingDetailBatchMapper.insertBatch(listInsert);
                }
            }
            
            // 账务明细代码表
            List<BokDetailId> listValBokDetailId = interfCommonService.toRepeatBokDetailId(
                result.getBokDetailIdList());
            // 入库
            if (CollectionUtils.isNotEmpty(listValBokDetailId)) {
                // 定义一个固定大小插入存放集合
                List<BokDetailId> listInsert = new ArrayList<BokDetailId>();

                for (BokDetailId e : listValBokDetailId) {
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

            // 交易费用明细表
            List<TrdTradeFeeDetail> listValTradeFeeDetail = result.getListValTradeFeeDetail();
            // 入库
            if (CollectionUtils.isNotEmpty(listValTradeFeeDetail)) {
                // 定义一个固定大小插入存放集合
                List<TrdTradeFeeDetail> listInsert = new ArrayList<TrdTradeFeeDetail>();

                for (TrdTradeFeeDetail e : listValTradeFeeDetail) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valTradeFeeDetailBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valTradeFeeDetailBatchMapper.insertBatch(listInsert);
                }
            }

        }

    }

}
