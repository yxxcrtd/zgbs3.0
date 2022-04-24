/*
 * 文件名：IrsDealRecWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V3.0.5.0
 * 修改人：殷亚
 * 修改时间：2022年4月21日
 * 修改内容：新建
 * 系统名称：估值核算系统V3.0.5
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.irs.writer;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.*;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.InterfProductDealBookCommonDto;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.core.entity.InterfExecuteInfo;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.irs.dto.IrsDealRecProcessorResult;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.lend.dto.LendDealRecProcessorResult;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 利率互换交易 write
 * @author 殷亚
 * @date 2022/4/21 10:24
 * @see
 * @version 1.0
 */
@Service
@Scope("prototype")
public class IrsDealRecWriter extends InterfBaseWriter<BaseEntity> {

    /**
     * 金融产品交易
     */
    @Autowired
    private ValFinProductDealBatchMapper valFinProductDealBatchMapper;

    /**
     * 估值核算交易扩展表
     */
    @Autowired
    private ValTrdProductDealAddBatchMapper valTrdProductDealAddBatchMapper;
    
    /**
     * 交易类金融产品表BatchMapper
     */
    @Autowired
    private ValFinTradeProductBatchMapper valFinTradeProductBatchMapper;

    /**
     * 入账信息表
     */
    @Autowired
    private ValDealAccountingBatchMapper valDealAccountingBatchMapper;

    /**
     * 账务明细表
     */
    @Autowired
    private ValDealAccountingDetailBatchMapper valDealAccountingDetailBatchMapper;

    /**
     * 交易费用明细表
     */
    @Autowired
    private ValTradeFeeDetailBatchMapper valTradeFeeDetailBatchMapper;

    /**
     * 账务明细代码表
     */
    @Autowired
    private ValBokDetailIdBatchMapper valBokDetailIdBatchMapper;

    @Autowired
    private ValOrdersBatchMapper valOrdersBatchMapper;

    @Autowired
    private ValOrdersExtBatchMapper valOrdersExtBatchMapper;

    @Autowired
    private InterfCommonService interfCommonService;

    @Value("${interf.properties.maxSqlNum}")
    private int maxSqlNum;

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
        InterfExecuteInfo interfExecuteInfo = (InterfExecuteInfo)this.getParams().get(
            "executeInfo");
        String interfDate = new SimpleDateFormat("yyyyMMdd").format(
            interfExecuteInfo.getInterfDate());

        List<IrsDealRecProcessorResult> resultList = (List<IrsDealRecProcessorResult>)items;
        for (IrsDealRecProcessorResult result : resultList) {
        	// 日志
            List<ValInterflog> listValInterflog = result.getListValInterflog();
            if (CollectionUtils.isNotEmpty(listValInterflog)) {
                // 定义一个固定大小插入存放集合
                List<ValInterflog> listInsert = new ArrayList<ValInterflog>();
                
                for (ValInterflog e : listValInterflog) {
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
            
            // 交易类金融产品
            List<FinTradeProduct> listValFinTradeProduct = result.getListValFinTradeProduct();
            // 入库
            if (CollectionUtils.isNotEmpty(listValFinTradeProduct)) {
                // 定义一个固定大小插入存放集合
                List<FinTradeProduct> listInsert = new ArrayList<FinTradeProduct>();
                
                for (FinTradeProduct e : listValFinTradeProduct) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                    	valFinTradeProductBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                	valFinTradeProductBatchMapper.insertBatch(listInsert);
                }
            }

            List<InterfProductDealBookCommonDto> productDealBookCommonDtos = result.getListInterfProductDealBookCommonDto();
            List<BokDealAccounting> listValDealAccountingCommon = new ArrayList<BokDealAccounting>();
            List<BokDealAccountingDetail> listValDealAccountingDetailCommon = new ArrayList<BokDealAccountingDetail>();
            List<BokDetailId> listValBokDetailIdCommon = new ArrayList<BokDetailId>();
            for (InterfProductDealBookCommonDto interfProductDealBookCommonDto : productDealBookCommonDtos) {
                listValDealAccountingCommon.add(
                    interfProductDealBookCommonDto.getBokDealAccounting());
                listValDealAccountingDetailCommon.add(
                    interfProductDealBookCommonDto.getBokDealAccountingDetail());
                listValBokDetailIdCommon.add(interfProductDealBookCommonDto.getBokDetailId());
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
            else if (CollectionUtils.isNotEmpty(listValDealAccountingCommon)) {
                // 定义一个固定大小插入存放集合
                List<BokDealAccounting> listInsert = new ArrayList<BokDealAccounting>();

                for (BokDealAccounting e : listValDealAccountingCommon) {
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
            else if (CollectionUtils.isNotEmpty(listValDealAccountingDetailCommon)) {
                // 定义一个固定大小插入存放集合
                List<BokDealAccountingDetail> listInsert = new ArrayList<BokDealAccountingDetail>();

                for (BokDealAccountingDetail e : listValDealAccountingDetailCommon) {
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
            List<BokDetailId> listValBokDetailId = result.getListValBokDetailId();
            if (CollectionUtils.isEmpty(listValBokDetailId)) {
                listValBokDetailId = listValBokDetailIdCommon;
            }
            listValBokDetailId = 
            		interfCommonService.toRepeatBokDetailId(listValBokDetailId);
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

            // 指令
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
