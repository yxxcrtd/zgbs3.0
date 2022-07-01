package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.feedeal.writer;

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
import com.joyintech.tams.common.base.entity.FinProductType;
import com.joyintech.tams.common.base.entity.FinTradeProduct;
import com.joyintech.tams.common.base.entity.FinTradeProductAdd;
import com.joyintech.tams.common.base.entity.StlOrders;
import com.joyintech.tams.common.base.entity.StlOrdersExt;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.common.base.entity.TrdProductDealAdd;
import com.joyintech.tams.common.base.entity.TrdTradeFeeDetail;
import com.joyintech.tams.common.base.entity.ValInterflog;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.FinTradeProductAddBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValBokDetailIdBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValDealAccountingBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValDealAccountingDetailBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductDealBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinProductTypeBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValFinTradeProductBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValInterflogBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValOrdersBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValOrdersExtBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValTradeFeeDetailBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValTrdProductDealAddBatchMapper;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.interf.core.InterfBaseWriter;
import com.joyintech.tams.valuation.interf.core.entity.InterfExecuteInfo;
import com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.feedeal.dto.FeeDealProcessorResult;
/**
 * 
 * 费用交易接口 writer
 * @author huwei
 * @version 1.0 2018年10月8日
 * @see FeeDealRecWriter
 * @since 1.0
 */
@Service
public class FeeDealRecWriter extends InterfBaseWriter<BaseEntity>{

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
     * 交易费用明细BatchMapper
     */
    @Autowired
    private ValTradeFeeDetailBatchMapper valTradeFeeDetailBatchMapper;
    
    @Autowired
    private ValOrdersBatchMapper valOrdersBatchMapper;
    
    @Autowired
    private ValOrdersExtBatchMapper valOrdersExtBatchMapper;
    
    @Autowired
    private ValFinTradeProductBatchMapper valFinTradeProductBatchMapper;
    
    @Autowired
    private FinTradeProductAddBatchMapper finTradeProductAddBatchMapper;
    
    @Autowired
    private ValFinProductTypeBatchMapper valFinProductTypeBatchMapper;

    /**
     * 日志
     */
    @Autowired
    private ValInterflogBatchMapper valInterflogBatchMapper;
    
    @Autowired
    private InterfCommonService interfCommonService;

    @Autowired
    private ValBokDetailIdBatchMapper valBokDetailIdBatchMapper;
    
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
        List<FeeDealProcessorResult> resultList = (List<FeeDealProcessorResult>)items;

        // 循环存款类资产交易信息processor结果DTO，读取所有的入库数据结构LIST，并进行入库操作。
        for (FeeDealProcessorResult result : resultList) {
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

            String isDealTrade = result.getParamMap().get(InterfCommonConstant.读取文件状态) == null? DictCodeList.是否_否.getValue():(String)result.getParamMap().get(InterfCommonConstant.读取文件状态);
            //如果没有数据就跳出循环
            if(result.getDatanum() == 0 || CollectionUtils.isEmpty(result.getListValFinProductDeal())) {
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
            
            //指令
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
            
            // 交易类金融产品表
            List<FinTradeProduct> listFinTradeProduct = result.getListFinTradeProduct();
            // 入库
            if (CollectionUtils.isNotEmpty(listFinTradeProduct)) {
                // 定义一个固定大小插入存放集合
                List<FinTradeProduct> listInsert = new ArrayList<FinTradeProduct>();
                for (FinTradeProduct e : listFinTradeProduct) {
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
            
            // 交易类金融产品附属表
            List<FinTradeProductAdd> listFinTradeProductAdd = result.getListFinTradeProductAdd();
            // 入库
            if (CollectionUtils.isNotEmpty(listFinTradeProductAdd)) {
                // 定义一个固定大小插入存放集合
                List<FinTradeProductAdd> listInsert = new ArrayList<FinTradeProductAdd>();
                for (FinTradeProductAdd e : listFinTradeProductAdd) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        finTradeProductAddBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    finTradeProductAddBatchMapper.insertBatch(listInsert);
                }
            }
            
            // 金融产品分类List
            List<FinProductType> listFinProductType = result.getListFinProductType();
            // 入库
            if (CollectionUtils.isNotEmpty(listFinProductType)) {
                // 定义一个固定大小插入存放集合
                List<FinProductType> listInsert = new ArrayList<FinProductType>();
                for (FinProductType e : listFinProductType) {
                    // 小于最大值
                    if (listInsert.size() < maxSqlNum) {
                        listInsert.add(e);
                    }
                    else if (listInsert.size() >= maxSqlNum) {
                        valFinProductTypeBatchMapper.insertBatch(listInsert);
                        listInsert.clear();
                        listInsert.add(e);
                    }
                }
                // 如果循环结束，小于最大值
                if (CollectionUtils.isNotEmpty(listInsert)) {
                    valFinProductTypeBatchMapper.insertBatch(listInsert);
                }
            }
            
        }
    }
}
