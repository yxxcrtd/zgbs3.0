package com.joyintech.service.valuation.trademanage.bondlend.service;


import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.service.valuation.trademanage.bond.dto.TrdBondTrade001Dto;
import com.joyintech.service.valuation.trademanage.bond.dto.TrdBondTradeResult;
import com.joyintech.service.valuation.trademanage.bond.service.TrdBondTrade001Service;
import com.joyintech.service.valuation.trademanage.bondlend.dao.TrdBondLendTradeMapper;
import com.joyintech.service.valuation.trademanage.bondlend.dto.TrdBondLendTrade002Dto;
import com.joyintech.service.valuation.trademanage.bondlend.dto.TrdBondLendTrade003Dto;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service(value="serviceTrdBondLendTrade002Service")
public class TrdBondLendTrade002Service extends BaseService {

    @Autowired
    private TrdBondLendTradeMapper trdBondLendTradeMapper;

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
    private TrdBondTrade001Service trdBondTrade001Service;

    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @param  tradeId
     * @return 产品信息
     * @see
     * @since 1.0
     */
    public TrdBondLendTrade002Dto queryTradeInfoByTradeId(String tradeId){
        return  trdBondLendTradeMapper.queryTradeInfoByTradeId(tradeId);
    }

    /**
     * 通过交易编号查询交易信息（到期）
     *
     * @param  tradeId
     * @return 产品信息
     * @see
     * @since 1.0
     */
    public TrdBondLendTrade003Dto queryInfoByTradeId(String tradeId){
        return  trdBondLendTradeMapper.queryInfoByTradeId(tradeId);
    }

    /**
     * 资金账户下拉框
     *
     * @param map 用户输入值
     * @return page<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getTrdAcctId(int page, int length, Map<String, Object> map) {
        return this.queryForPageForSelectReturn(page, length,() -> trdBondLendTradeMapper.getTrdAcctId(map));
    }

    public int querySequences(){
        int i=trdBondLendTradeMapper.querySequences();
        return i;
    }

    public int insert(TrdBondTradeResult dto){
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

    public int update(TrdBondTradeResult dto){
        TrdBondTrade001Dto dto1 = new TrdBondTrade001Dto();
        dto1.setFinprodId(dto.getTrdBondTrade002Dto().getProdId());
        dto1.setTradeId(dto.getTrdBondTrade002Dto().getTradeId());
       trdBondTrade001Service.delete(dto1);
       return insert(dto);
    }

}
