package com.joyintech.service.valuation.trademanage.trdbondchangevaluationtrade.service;


import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.trademanage.trdbondchangevaluationtrade.dao.TrdBondChangeTradeMapper;
import com.joyintech.service.valuation.trademanage.trdbondchangevaluationtrade.dto.TrdBondChangeTrade001Dto;
import com.joyintech.service.valuation.trademanage.trdbondchangevaluationtrade.dto.TrdBondChangeTrade002Dto;
import com.joyintech.service.valuation.trademanage.trdbondchangevaluationtrade.dto.TrdBondChangeTradeFundResult;
import com.joyintech.service.valuation.trademanage.trdbondchangevaluationtrade.dto.TrdBondChangeTradeResult;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service(value="serviceTrdBondChangeTrade002Service")
public class TrdBondChangeTrade002Service extends BaseService {

    @Autowired
    private TrdBondChangeTradeMapper trdBondChangeTradeMapper;

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
    private TrdBondChangeTrade001Service trdBondChangeTrade001Service;

    /*
    * 转出证券代码下拉框显示可转债资产
    */
    public Page<SelectOptionDto> getSecExgId(int page, int length, Map<String, Object> map) {
        map.put("Bond_Type1", DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue());
        map.put("Bond_Type2",DictCodeList.WIND一级分类_含债券一级分类等_可交换债.getValue());
        return this.queryForPageForSelectReturn(page, length,() -> trdBondChangeTradeMapper.getSecExgId(map));
    }

    /*
     * 转出证券代码下拉框显示可转债资产
     */
    public Page<SelectOptionDto> getFundId(int page, int length, Map<String, Object> map) {
        map.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        return this.queryForPageForSelectReturn(page, length,() -> trdBondChangeTradeMapper.getFundId(map));
    }

    /*
     * 转展示股票代码和基金
     */
    public Page<SelectOptionDto> getCAssetId(int page, int length, Map<String, Object> map) {
        map.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        return this.queryForPageForSelectReturn(page, length,() -> trdBondChangeTradeMapper.getCAssetId(map));
    }

    /**
     * 通过资产代码查询资产简称和交易场所
     *
     * @param param 参数map
     * @return 产品信息
     * @see
     * @since 1.0
     */
    public FinProduct selectInfoBySecId(@RequestBody Map<String, Object> param){
        return  trdBondChangeTradeMapper.selectInfoBySecId(param);
    }

    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @param  tradeId
     * @return 产品信息
     * @see
     * @since 1.0
     */
    public TrdBondChangeTrade002Dto queryTradeInfoByTradeId(String tradeId){
        return  trdBondChangeTradeMapper.queryTradeInfoByTradeId(tradeId);
    }

    public int querySequences(){
        int i=trdBondChangeTradeMapper.querySequences();
        return i;
    }

    public int insert(TrdBondChangeTradeResult dto){
        // 金融产品交易
        List<TrdProductDeal> listValFinProductDeal = dto.getListValFinProductDeal();
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
        List<TrdFinProductBarter> listTrdFinProductBarter = dto.getListValFinProductBarter();
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
        List<TrdProductDealAdd> listValFinProductDealExt = dto.getListValFinProductDealExt();
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
        List<FinTradeProduct> listValFinTradeProduct = dto.getListValFinTradeProduct();
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
        List<BokDealAccounting> listValDealAccounting = dto.getListValDealAccounting();
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
        List<BokDealAccountingDetail> listValDealAccountingDetail = dto.getListValDealAccountingDetail();
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
        List<BokDetailId> listValBokDetailId = interfCommonService.toRepeatBokDetailId(dto.getListValBokDetailId());
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
        List<TrdTradeFeeDetail> listValTradeFeeDetail = dto.getListValTradeFeeDetail();
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
        List<StlOrders>  ValOrdersList=dto.getValOrdersList();
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
        List<StlOrdersExt> ValOrdersExtList=dto.getValOrdersExtList();
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

    public int update(TrdBondChangeTradeResult dto){
        TrdBondChangeTrade001Dto dto1 = new TrdBondChangeTrade001Dto();
        dto1.setFinprodId(dto.getTrdBondChangeTrade002Dto().getProdId());
        dto1.setTradeId(dto.getTrdBondChangeTrade002Dto().getTradeId());
        trdBondChangeTrade001Service.delete(dto1);
       return insert(dto);
    }

    public int insertFund(TrdBondChangeTradeFundResult dto){
        // 金融产品交易
        List<TrdProductDeal> listValFinProductDeal = dto.getListValFinProductDeal();
        // 入库
        if (CollectionUtils.isNotEmpty(listValFinProductDeal)) {
            // 定义一个固定大小插入存放集合
            List<TrdProductDeal> listInsert = new ArrayList<TrdProductDeal>();
            for (TrdProductDeal e : listValFinProductDeal) {
                listInsert.add(e);
            }
            valFinProductDealBatchMapper.insertBatch(listInsert);
        }
        // 估值核算交易扩展表
        List<TrdProductDealAdd> listValFinProductDealExt = dto.getListValFinProductDealExt();
        // 入库
        if (CollectionUtils.isNotEmpty(listValFinProductDealExt)) {
            // 定义一个固定大小插入存放集合
            List<TrdProductDealAdd> listInsert = new ArrayList<TrdProductDealAdd>();
            for (TrdProductDealAdd e : listValFinProductDealExt) {
                listInsert.add(e);
            }
            valTrdProductDealAddBatchMapper.insertBatch(listInsert);
        }

        // 入账信息表
        List<BokDealAccounting> listValDealAccounting = dto.getListValDealAccounting();
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
        List<BokDealAccountingDetail> listValDealAccountingDetail = dto.getListValDealAccountingDetail();
        // 入库
        if (CollectionUtils.isNotEmpty(listValDealAccountingDetail)) {
            // 定义一个固定大小插入存放集合
            List<BokDealAccountingDetail> listInsert = new ArrayList<BokDealAccountingDetail>();
            for (BokDealAccountingDetail e : listValDealAccountingDetail) {
                listInsert.add(e);
            }
            valDealAccountingDetailBatchMapper.insertBatch(listInsert);
        }

        //  账务明细代码表
        List<BokDetailId> listValBokDetailId = dto.getListValBokDetailId();
        listValBokDetailId = interfCommonService.toRepeatBokDetailId(listValBokDetailId);
        // 入库
        if (CollectionUtils.isNotEmpty(listValBokDetailId)) {
            // 定义一个固定大小插入存放集合
            List<BokDetailId> listInsert = new ArrayList<BokDetailId>();
            for (BokDetailId e : listValBokDetailId) {
                listInsert.add(e);
            }
            valBokDetailIdBatchMapper.deleteBatch(listInsert);
            valBokDetailIdBatchMapper.insertBatch(listInsert);
        }

        // 交易费用明细表
        List<TrdTradeFeeDetail> listValTradeFeeDetail = dto.getListValTradeFeeDetail();
        // 入库
        if (CollectionUtils.isNotEmpty(listValTradeFeeDetail)) {
            // 定义一个固定大小插入存放集合
            List<TrdTradeFeeDetail> listInsert = new ArrayList<TrdTradeFeeDetail>();
            for (TrdTradeFeeDetail e : listValTradeFeeDetail) {
                listInsert.add(e);
            }
            valTradeFeeDetailBatchMapper.insertBatch(listInsert);
        }

        //指令
        List<StlOrders> listValOrders = dto.getListValOrders();
        // 入库
        if (CollectionUtils.isNotEmpty(listValOrders)) {
            // 定义一个固定大小插入存放集合
            List<StlOrders> listInsert = new ArrayList<StlOrders>();
            for (StlOrders e : listValOrders) {
                listInsert.add(e);
            }
            valOrdersBatchMapper.insertBatch(listInsert);
        }

        List<StlOrdersExt> listValOrdersExt = dto.getListValOrdersExt();
        // 入库
        if (CollectionUtils.isNotEmpty(listValOrdersExt)) {
            // 定义一个固定大小插入存放集合
            List<StlOrdersExt> listInsert = new ArrayList<StlOrdersExt>();
            for (StlOrdersExt e : listValOrdersExt) {
                listInsert.add(e);
            }
            valOrdersExtBatchMapper.insertBatch(listInsert);
        }

        return 1;
    }

    public int updateFund(TrdBondChangeTradeFundResult dto){
        TrdBondChangeTrade001Dto dto1 = new TrdBondChangeTrade001Dto();
        dto1.setFinprodId(dto.getTrdBondChangeTrade002Dto().getProdId());
        dto1.setTradeId(dto.getTrdBondChangeTrade002Dto().getTradeId());
        trdBondChangeTrade001Service.delete(dto1);
        return insertFund(dto);
    }

}
