/*
 * 文件名：CurrentDepositRecWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chendijun
 * 修改时间：2018年8月21日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.currentdeposit.writer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.joyintech.tams.common.base.entity.BokDealAccounting;
import com.joyintech.tams.common.base.entity.BokDealAccountingDetail;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.StlOrders;
import com.joyintech.tams.common.base.entity.StlOrdersExt;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValBokDetailIdBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValDealAccountingBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValDealAccountingDetailBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductDealBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValInterflogBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValOrdersBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValOrdersExtBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValTrdProductDealAddBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.core.entity.InterfExecuteInfo;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.currentdeposit.dto.CurrentDepositProcessorResult;

/**
 * 活期存款利息-交易数据接口 的 writer
 * 
 * @author chendijun
 * @version 1.0 2018年8月21日
 * @see CurrentDepositRecWriter
 * @since 1.0
 */
@Service
public class CurrentDepositRecWriter extends InterfBaseWriter<BaseEntity> {

    /**
     * 金融产品交易BatchMapper
     */
    @Autowired
    private ValFinProductDealBatchMapper valFinProductDealBatchMapper;

    /**
     * 金融产品交易扩展表BatchMapper
     */
    @Autowired
    private ValTrdProductDealAddBatchMapper valTrdProductDealAddBatchMapper;

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
     * 账务明细代码BatchMapper
     */
    @Autowired
    private ValBokDetailIdBatchMapper valBokDetailIdBatchMapper;

    /**
     * 结算指令BatchMapper
     */
    @Autowired
    private ValOrdersBatchMapper valOrdersBatchMapper;

    /**
     * 结算指令扩展BatchMapper
     */
    @Autowired
    private ValOrdersExtBatchMapper valOrdersExtBatchMapper;

    @Autowired
    private InterfCommonService interfCommonService;

    /**
     * 日志BatchMapper
     */
    @Autowired
    private ValInterflogBatchMapper valInterflogBatchMapper;

    @Value("${interf.properties.maxSqlNum}")
    private int maxSqlNum;

    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends BaseEntity> item)
        throws Exception {
        InterfExecuteInfo interfExecuteInfo = (InterfExecuteInfo)this.getParams().get(
            "executeInfo");
        String interfDate = new SimpleDateFormat("yyyyMMdd").format(
            interfExecuteInfo.getInterfDate());
        // 获取process返回组合的list值
        List<CurrentDepositProcessorResult> resultList = (List<CurrentDepositProcessorResult>)item;
        // 循环resultList 读取所有的入库数据结构LIST，并进行入库操作。
        for (CurrentDepositProcessorResult result : resultList) {
        	// 日志
            List<ValInterflog> valInterflog = result.getValInterflog();
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


            interfCommonService.dealBatchTrdFinprodDeal(result.getParamMap(),interfDate);
            /**modify by wujie on 20190123 end**/

            // 金融产品交易
            List<TrdProductDeal> listValFinProductDeal = result.getListValFinProductDeal();
            // 入库
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

            // 估值核算交易扩展表
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
                    	valTrdProductDealAddBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                	valTrdProductDealAddBatchMapper.insertBatch(listInsert);
                }
            }

            // 入账信息表
            List<BokDealAccounting> listValDealAccounting = result.getListValDealAccounting();
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
            List<BokDealAccountingDetail> listValDealAccountingDetail = result.getListValDealAccountingDetail();
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
            List<BokDetailId> listValBokDetailId = 
            		interfCommonService.toRepeatBokDetailId(result.getListValBokDetailId());
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

            // 结算指令表
            List<StlOrders> listValOrders = result.getListValOrders();
            // 入库
            if (CollectionUtils.isNotEmpty(listValOrders)) {
                // 定义一个固定大小插入存放集合
                List<StlOrders> listInsert = new ArrayList<StlOrders>();
                
                for (StlOrders e : listValOrders) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valOrdersBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valOrdersBatchMapper.insertBatch(listInsert);
                }
            }

            // 结算指令扩展表
            List<StlOrdersExt> listValOrdersExt = result.getListValOrdersExt();
            // 入库
            if (CollectionUtils.isNotEmpty(listValOrdersExt)) {
                // 定义一个固定大小插入存放集合
                List<StlOrdersExt> listInsert = new ArrayList<StlOrdersExt>();
                
                for (StlOrdersExt e : listValOrdersExt) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valOrdersExtBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valOrdersExtBatchMapper.insertBatch(listInsert);
                }
            }
        }
    }

}
