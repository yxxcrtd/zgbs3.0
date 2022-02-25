package com.joyintech.service.valuation.trademanage.trdbondvaluationduration.service;


import com.joyintech.framework.base.BaseService;
import com.joyintech.service.valuation.trademanage.trdbondvaluationduration.dao.TrdBondValuationDurationMapper;
import com.joyintech.service.valuation.trademanage.trdbondvaluationduration.dto.TrdBondValuationDurationDto;
import com.joyintech.service.valuation.trademanage.trdbondvaluationduration.dto.TrdBondValuationDurationResult;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service(value="serviceTrdBondValuationDuration002Service")
public class TrdBondValuationDuration002Service extends BaseService {

    @Autowired
    private TrdBondValuationDurationMapper trdBondValuationDurationMapper;

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
     * 金融产品实物交易表BatchMapper
     */
    @Autowired
    private ValTrdFinProductBarterBatchMapper valTrdFinProductBarterBatchMapper;

    /**
     * 交易类金融产品表BatchMapper
     */
    @Autowired
    private ValFinTradeProductBatchMapper valFinTradeProductBatchMapper;

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
     * 账务明细代码BatchBatchMapper add by wujie on 20180712
     */
    @Autowired
    private ValBokDetailIdBatchMapper valBokDetailIdBatchMapper;

    /**
     * 交易费用明细BatchBatchMapper
     */
    @Autowired
    private ValTradeFeeDetailBatchMapper valTradeFeeDetailBatchMapper;

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

    @Autowired
    private InterfCommonService interfCommonService;

    @Autowired
    private TrdBondValuationDuration001Service trdBondValuationDuration001Service;

    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @param  tradeId
     * @return 产品信息
     * @see
     * @since 1.0
     */
    public TrdBondValuationDurationDto queryTradeInfoByTradeId(String tradeId){
        return  trdBondValuationDurationMapper.queryTradeInfoByTradeId(tradeId);
    }

    public int querySequences(){
        int i=trdBondValuationDurationMapper.querySequences();
        return i;
    }

    public int insert(TrdBondValuationDurationResult result){
        // 金融产品交易
        List<TrdProductDeal> listValFinProductDeal = result.getListValFinProductDeal();
        // 入库
        if (CollectionUtils.isNotEmpty(listValFinProductDeal)) {
            // 定义一个固定大小插入存放集合
            List<TrdProductDeal> listInsert = new ArrayList<TrdProductDeal>();
            for (TrdProductDeal e : listValFinProductDeal) {
                    listInsert.add(e);
                }
            valFinProductDealBatchMapper.insertBatch(listInsert);
        }

        // 金融产品实物交易
        List<TrdFinProductBarter> listTrdFinProductBarter = result.getListValFinProductBarter();
        // 入库
        if (CollectionUtils.isNotEmpty(listTrdFinProductBarter)) {
            // 定义一个固定大小插入存放集合
            List<TrdFinProductBarter> listInsert = new ArrayList<TrdFinProductBarter>();
            for (TrdFinProductBarter e : listTrdFinProductBarter) {
                    listInsert.add(e);
                }
            valTrdFinProductBarterBatchMapper.insertBatch(listInsert);
        }

        // 估值核算交易扩展表
        List<TrdProductDealAdd> listValFinProductDealExt = result.getListValFinProductDealExt();
        // 入库
        if (CollectionUtils.isNotEmpty(listValFinProductDealExt)) {
            // 定义一个固定大小插入存放集合
            List<TrdProductDealAdd> listInsert = new ArrayList<TrdProductDealAdd>();
            for (TrdProductDealAdd e : listValFinProductDealExt) {
                listInsert.add(e);
            }
            valTrdProductDealAddBatchMapper.insertBatch(listInsert);
        }

        // 交易类金融产品
        List<FinTradeProduct> listValFinTradeProduct = result.getListValFinTradeProduct();
        // 入库
        if (CollectionUtils.isNotEmpty(listValFinTradeProduct)) {
            // 定义一个固定大小插入存放集合
            List<FinTradeProduct> listInsert = new ArrayList<FinTradeProduct>();
            for (FinTradeProduct e : listValFinTradeProduct) {
                listInsert.add(e);
            }
            valFinTradeProductBatchMapper.insertBatch(listInsert);
        }

        // 入账信息表
        List<BokDealAccounting> listValDealAccounting = result.getListValDealAccounting();
        // 入库
        if (CollectionUtils.isNotEmpty(listValDealAccounting)) {
            // 定义一个固定大小插入存放集合
            List<BokDealAccounting> listInsert = new ArrayList<BokDealAccounting>();
            for (BokDealAccounting e : listValDealAccounting) {
                listInsert.add(e);
            }
            valDealAccountingBatchMapper.insertBatch(listInsert);
        }

        // 账务明细表
        List<BokDealAccountingDetail> listValDealAccountingDetail = result.getListValDealAccountingDetail();
        // 入库
        if (CollectionUtils.isNotEmpty(listValDealAccountingDetail)) {
            // 定义一个固定大小插入存放集合
            List<BokDealAccountingDetail> listInsert = new ArrayList<BokDealAccountingDetail>();
            for (BokDealAccountingDetail e : listValDealAccountingDetail) {
                listInsert.add(e);
            }
            valDealAccountingDetailBatchMapper.insertBatch(listInsert);
        }

        //账务明细代码表
        List<BokDetailId> listValBokDetailId =
                interfCommonService.toRepeatBokDetailId(result.getListValBokDetailId());
        // 入库
        if (CollectionUtils.isNotEmpty(listValBokDetailId)) {
            // 定义一个固定大小插入存放集合
            List<BokDetailId> listInsert = new ArrayList<BokDetailId>();
            for (BokDetailId e : listValBokDetailId) {
                listInsert.add(e);
            }
            valBokDetailIdBatchMapper.insertBatch(listInsert);
        }

        // 交易费用明细表
        List<TrdTradeFeeDetail> listValTradeFeeDetail = result.getListValTradeFeeDetail();
        // 入库
        if (CollectionUtils.isNotEmpty(listValTradeFeeDetail)) {
            // 定义一个固定大小插入存放集合
            List<TrdTradeFeeDetail> listInsert = new ArrayList<TrdTradeFeeDetail>();
            for (TrdTradeFeeDetail e : listValTradeFeeDetail) {
                listInsert.add(e);
            }
            valTradeFeeDetailBatchMapper.insertBatch(listInsert);
        }
        //结算指令表
        List<StlOrders>  ValOrdersList=result.getValOrdersList();
        //入库
        if (CollectionUtils.isNotEmpty(ValOrdersList)) {
            // 定义一个固定大小插入存放集合
            List<StlOrders> listInsert = new ArrayList<StlOrders>();
            for (StlOrders e : ValOrdersList) {
                listInsert.add(e);
            }
            valOrdersBatchMapper.insertBatch(listInsert);
        }
        //结算指令拓展表
        List<StlOrdersExt> ValOrdersExtList=result.getValOrdersExtList();
        //入库
        if (CollectionUtils.isNotEmpty(ValOrdersExtList)) {
            // 定义一个固定大小插入存放集合
            List<StlOrdersExt> listInsert = new ArrayList<StlOrdersExt>();
            for (StlOrdersExt e : ValOrdersExtList) {
                listInsert.add(e);
            }
            valOrdersExtBatchMapper.insertBatch(listInsert);
        }
        return 1;
    }

    public int update(TrdBondValuationDurationResult dto){
        TrdBondValuationDurationDto dto1 = new TrdBondValuationDurationDto();
        dto1.setProdId(dto.getTrdBondValuationDurationDto().getProdId());
        dto1.setTradeId(dto.getTrdBondValuationDurationDto().getTradeId());
        trdBondValuationDuration001Service.delete(dto1);
       return insert(dto);
    }

}
