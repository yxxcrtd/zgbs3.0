/*
 * 文件名：PtlCashFlowRecWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：tanxuan
 * 修改时间：2020年4月3日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.ptlCashFlow.writer;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.core.entity.InterfExecuteInfo;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.ptlCashFlow.dto.PtlCashFlowProcessorResult;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义现金流交易信息接口 Writer
 * 
 * @author tanxuan
 * @version 1.0 2020年4月3日
 * @see PtlCashFlowRecWriter
 * @since 1.0
 */
@Service
@Scope("prototype")
public class PtlCashFlowRecWriter extends InterfBaseWriter<BaseEntity> {
	 
	/**
	 * 结算指令BatchMapper
	 */
	@Autowired
	private ValOrdersBatchMapper valOrdersBatchMapper;
	
	/**
	 * 结算指令拓展BatchMapper
	 */
	@Autowired
	private ValOrdersExtBatchMapper valOrdersExtBatchMapper;
	
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
     * 账务明细BatchBatchMapper
     */
    @Autowired
    private ValDealAccountingDetailBatchMapper valDealAccountingDetailBatchMapper;
    
    /**
     * 账务明细代码BatchBatchMapper
     */
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
     * 更新数据 {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends BaseEntity> items)
        throws Exception {
        InterfExecuteInfo interfExecuteInfo = (InterfExecuteInfo)this.getParams().get("executeInfo");
        String interfDate =new SimpleDateFormat("yyyyMMdd").format(interfExecuteInfo.getInterfDate());
        // 获取process返回组合的list值
        List<PtlCashFlowProcessorResult> resultList = (List<PtlCashFlowProcessorResult>)items;

        // 循环自定义现金流交易信息processor结果DTO，读取所有的入库数据结构LIST，并进行入库操作。
        for (PtlCashFlowProcessorResult result : resultList) {
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
            //如果没有数据就跳出循环
            if(result.getDataNum() == 0) {
                continue;
            }
            /** 调用交易删除共通 **/
            interfCommonService.deletebatchFinprodDeal(result.getParamMap(),interfDate);
            
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
            List<TrdProductDealAdd> listValFinProductDealExt = result.getListValFinProductDealAdd();
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
            
            //账务明细代码表
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
            
            //结算指令表
            List<StlOrders>  ValOrdersList=result.getValOrdersList();
            //入库
            if (CollectionUtils.isNotEmpty(ValOrdersList)) {
                // 定义一个固定大小插入存放集合
                List<StlOrders> listInsert = new ArrayList<StlOrders>();
                
                for (StlOrders e : ValOrdersList) {
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
            
            //结算指令拓展表
            List<StlOrdersExt> ValOrdersExtList=result.getValOrdersExtList();
            //入库
            if (CollectionUtils.isNotEmpty(ValOrdersExtList)) {
                // 定义一个固定大小插入存放集合
                List<StlOrdersExt> listInsert = new ArrayList<StlOrdersExt>();
                
                for (StlOrdersExt e : ValOrdersExtList) {
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
