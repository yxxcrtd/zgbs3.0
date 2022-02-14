package com.joyintech.service.valuation.trademanage.trdstockvaluationtrade.service;


import com.joyintech.framework.base.BaseService;
import com.joyintech.service.valuation.trademanage.trdstockvaluationtrade.dao.TrdStockTradeMapper;
import com.joyintech.service.valuation.trademanage.trdstockvaluationtrade.dto.TrdStockTrade001Dto;
import com.joyintech.service.valuation.trademanage.trdstockvaluationtrade.dto.TrdStockTrade002Dto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(value="serviceTrdStockTrade002Service")
public class TrdStockTrade002Service extends BaseService {

    @Autowired
    private TrdStockTradeMapper trdStockTradeMapper;

//    /**
//     * 金融产品交易BatchMapper
//     */
//    @Autowired
//    private ValFinProductDealBatchMapper valFinProductDealBatchMapper;
//
//    /**
//     * 金融产品交易扩展表BatchMapper
//     */
//    @Autowired
//    private ValTrdProductDealAddBatchMapper valTrdProductDealAddBatchMapper;
//
//    /**
//     * 金融产品实物交易表BatchMapper
//     */
//    @Autowired
//    private ValTrdFinProductBarterBatchMapper valTrdFinProductBarterBatchMapper;
//
//    /**
//     * 交易类金融产品表BatchMapper
//     */
//    @Autowired
//    private ValFinTradeProductBatchMapper valFinTradeProductBatchMapper;
//
//    /**
//     * 入账信息BatchMapper
//     */
//    @Autowired
//    private ValDealAccountingBatchMapper valDealAccountingBatchMapper;
//
//    /**
//     * 账务明细BatchBatchMapper
//     */
//    @Autowired
//    private ValDealAccountingDetailBatchMapper valDealAccountingDetailBatchMapper;
//
//    /**
//     * 账务明细代码BatchBatchMapper add by wujie on 20180712
//     */
//    @Autowired
//    private ValBokDetailIdBatchMapper valBokDetailIdBatchMapper;
//
//    /**
//     * 交易费用明细BatchBatchMapper
//     */
//    @Autowired
//    private ValTradeFeeDetailBatchMapper valTradeFeeDetailBatchMapper;
//
//    /**
//     * 结算指令BatchMapper
//     */
//    @Autowired
//    private ValOrdersBatchMapper valOrdersBatchMapper;
//
//    /**
//     * 结算指令拓展BatchMapper
//     */
//    @Autowired
//    private ValOrdersExtBatchMapper valOrdersExtBatchMapper;
//
//    @Autowired
//    private InterfCommonService interfCommonService;

    @Autowired
    private TrdStockTrade001Service trdStockTrade001Service;

    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @param  tradeId
     * @return 产品信息
     * @see
     * @since 1.0
     */
    public TrdStockTrade002Dto queryTradeInfoByTradeId(String tradeId){
        return  trdStockTradeMapper.queryTradeInfoByTradeId(tradeId);
    }

    public int querySequences(){
        int i=trdStockTradeMapper.querySequences();
        return i;
    }

    public int insert(TrdStockTrade002Dto dto){
//        // 金融产品交易
//        List<TrdProductDeal> listValFinProductDeal = dto.getListValFinProductDeal();
//        // 入库
//        if (CollectionUtils.isNotEmpty(listValFinProductDeal)) {
//            // 定义一个固定大小插入存放集合
//            List<TrdProductDeal> listInsert = new ArrayList<TrdProductDeal>();
//            for (TrdProductDeal e : listValFinProductDeal) {
//                    listInsert.add(e);
//                    valFinProductDealBatchMapper.insertBatch(listInsert);
//
//                }
//            }

//        // 金融产品实物交易
//        List<TrdFinProductBarter> listTrdFinProductBarter = dto.getListValFinProductBarter();
//        // 入库
//        if (CollectionUtils.isNotEmpty(listTrdFinProductBarter)) {
//            // 定义一个固定大小插入存放集合
//            List<TrdFinProductBarter> listInsert = new ArrayList<TrdFinProductBarter>();
//            for (TrdFinProductBarter e : listTrdFinProductBarter) {
//                    listInsert.add(e);
//                    valTrdFinProductBarterBatchMapper.insertBatch(listInsert);
//            }
//        }
//
//        // 估值核算交易扩展表
//        List<TrdProductDealAdd> listValFinProductDealExt = dto.getListValFinProductDealExt();
//        // 入库
//        if (CollectionUtils.isNotEmpty(listValFinProductDealExt)) {
//            // 定义一个固定大小插入存放集合
//            List<TrdProductDealAdd> listInsert = new ArrayList<TrdProductDealAdd>();
//            for (TrdProductDealAdd e : listValFinProductDealExt) {
//                    listInsert.add(e);
//                    valTrdProductDealAddBatchMapper.insertBatch(listInsert);
//            }
//        }
//
//        // 交易类金融产品
//        List<FinTradeProduct> listValFinTradeProduct = dto.getListValFinTradeProduct();
//        // 入库
//        if (CollectionUtils.isNotEmpty(listValFinTradeProduct)) {
//            // 定义一个固定大小插入存放集合
//            List<FinTradeProduct> listInsert = new ArrayList<FinTradeProduct>();
//            for (FinTradeProduct e : listValFinTradeProduct) {
//                    listInsert.add(e);
//                    valFinTradeProductBatchMapper.insertBatch(listInsert);
//            }
//        }

        return 1;
    }

    public int update(TrdStockTrade002Dto dto){
        TrdStockTrade001Dto dto1 = new TrdStockTrade001Dto();
        dto1.setFinprodId(dto.getProdId());
        dto1.setTradeId(dto.getTradeId());
        trdStockTrade001Service.delete(dto1);
       return insert(dto);
    }

}
