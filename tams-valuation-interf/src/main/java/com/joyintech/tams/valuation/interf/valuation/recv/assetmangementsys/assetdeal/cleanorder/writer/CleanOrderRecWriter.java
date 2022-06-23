/*
 * 文件名：CleanOrderRecWriter.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：chendijun
 * 修改时间：2018年8月17日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.cleanorder.writer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValBokDetailIdBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValDealAccountingBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValDealAccountingDetailBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValInterflogBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValOrdersBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValOrdersExtBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.core.entity.InterfExecuteInfo;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.cleanorder.dto.CleanOrderProcessorResult;
import com.joyintech.tams.common.base.entity.BokDealAccounting;
import com.joyintech.tams.common.base.entity.BokDealAccountingDetail;
import com.joyintech.tams.common.base.entity.BokDetailId;
import com.joyintech.tams.common.base.entity.StlOrders;
import com.joyintech.tams.common.base.entity.StlOrdersExt;
import com.joyintech.tams.common.base.entity.ValInterflog;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author chendijun
 * @version 1.0 2018年8月17日
 * @see CleanOrderRecWriter
 * @since 1.0
 */
@Service
public class CleanOrderRecWriter extends InterfBaseWriter<BaseEntity> {

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

    /**
     * 账务明细代码表BatchMapper
     */
    @Autowired
    private ValBokDetailIdBatchMapper valBokDetailIdBatchMapper;

    /**
     * 入账信息BatchMapper
     */
    @Autowired
    private ValDealAccountingBatchMapper valDealAccountingBatchMapper;

    /**
     * 入账账务明细BatchMapper
     */
    @Autowired
    private ValDealAccountingDetailBatchMapper valDealAccountingDetailBatchMapper;

    /**
     * 日志
     */
    @Autowired
    private ValInterflogBatchMapper valInterflogBatchMapper;

    /** modify by wujie on 20190123 remark:重复导入删除逻辑调整
    @Autowired
    private ValCommonBatchMapper valCommonBatchMapper;// 去重数据Mapper
    **/
    
    @Autowired
    private InterfCommonService interfCommonService;
    
    @Value("${interf.properties.maxSqlNum}")
    private int maxSqlNum;

    @SuppressWarnings("unchecked")
    @Override
    public void write(List<? extends BaseEntity> item)
        throws Exception {
        InterfExecuteInfo interfExecuteInfo = (InterfExecuteInfo)this.getParams().get("executeInfo");
        String interfDate = new SimpleDateFormat("yyyyMMdd").format(
            interfExecuteInfo.getInterfDate());
        List<CleanOrderProcessorResult> resultList = (List<CleanOrderProcessorResult>)item;
        for (CleanOrderProcessorResult result : resultList) {
        	
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
        	
        	 /** modify by wujie on 20190123 remark:重复导入删除逻辑调整
             Map<String, Object> paramMap = result.getParamMap();
             paramMap.put("createTime", interfDate);
             paramMap.put("createUser", InterfCommonConstant.接口_默认用户);
             **/
             

            String isDealTrade = result.getParamMap().get(InterfCommonConstant.读取文件状态) == null? DictCodeList.是否_否.getValue():(String)result.getParamMap().get(InterfCommonConstant.读取文件状态);
            //如果没有数据就跳出循环
            if(result.getDataNum() == 0 || CollectionUtils.isEmpty(result.getListValOrders())) {
                if(DictCodeList.是否_是.getValue().equals(isDealTrade)){
                    interfCommonService.dealBatchTrdFinprodDeal(result.getParamMap(),interfDate);
                }
                continue;
            }

             
             /** add by wujie on 20190123 start remark:重复导入删除逻辑调整**/
             interfCommonService.dealCleanOrderDeal(result.getParamMap(),interfDate);
             /**add by wujie on 20190123 end**/
            

            // 入账账务明细表
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
                    	/** modify by wujie on 20190123 remark:重复导入删除逻辑调整
                        valCommonBatchMapper.deleteDealAccountingDetailSingle(listInsert);
                        **/
                        valDealAccountingDetailBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                	/** modify by wujie on 20190123 remark:重复导入删除逻辑调整
                    valCommonBatchMapper.deleteDealAccountingDetailSingle(listInsert);
                    **/
                    valDealAccountingDetailBatchMapper.insertBatch(listInsert);
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
                    	/** modify by wujie on 20190123 remark:重复导入删除逻辑调整
                        valCommonBatchMapper.deleteDealAccountingSingle(listInsert);
                        **/
                        valDealAccountingBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                	/** modify by wujie on 20190123 remark:重复导入删除逻辑调整
                    valCommonBatchMapper.deleteDealAccountingSingle(listInsert);
                    **/
                    valDealAccountingBatchMapper.insertBatch(listInsert);
                }
            }
            

            // 账务明细代码表
            List<BokDetailId> listValBokDetailId = interfCommonService.toRepeatBokDetailId(result.getListValBokDetailId());
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

            // 结算指令扩展
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
                    	/** modify by wujie on 20190123 remark:重复导入删除逻辑调整
                        valCommonBatchMapper.deleteOrdersExtSingle(listInsert);
                        **/
                        valOrdersExtBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                	/** modify by wujie on 20190123 remark:重复导入删除逻辑调整
                    valCommonBatchMapper.deleteOrdersExtSingle(listInsert);
                    **/
                    valOrdersExtBatchMapper.insertBatch(listInsert);
                }
            }

            // 结算指令
            List<StlOrders> listValOrders = result.getListValOrders();
            // 入库
            if (CollectionUtils.isNotEmpty(listValOrders)) {
                // 定义一个固定大小插入存放集合
                List<StlOrders> listInsert = new ArrayList<StlOrders>();
                /** modify by wujie on 20190123 remark:重复导入删除逻辑调整
                paramMap.put("busiType", DictCodeList.业务类型_指令交易.getValue());
                valCommonBatchMapper.deleteOrdersSingle(paramMap);
                **/
                
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
            
        }

    }

}
