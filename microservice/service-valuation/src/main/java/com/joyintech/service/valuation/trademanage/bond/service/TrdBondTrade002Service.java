package com.joyintech.service.valuation.trademanage.bond.service;


import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.valuation.trademanage.bond.dao.TrdBondTradeMapper;
import com.joyintech.service.valuation.trademanage.bond.dto.TrdBondTradeDto;
import com.joyintech.service.valuation.trademanage.bond.dto.TrdBondTradeResult;
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

@Service(value="serviceTrdBondTrade002Service")
public class TrdBondTrade002Service extends BaseService {

    @Autowired
    private TrdBondTradeMapper trdBondTradeMapper;

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
     * 通过资产代码查询资产简称和交易场所
     *
     * @param param 参数map
     * @return 产品信息
     * @see
     * @since 1.0
     */
    public FinProduct selectInfoBySecId(@RequestBody Map<String, Object> param){
        return  trdBondTradeMapper.selectInfoBySecId(param);
    }

    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @param  tradeId
     * @return 产品信息
     * @see
     * @since 1.0
     */
    public TrdBondTradeDto queryTradeInfoByTradeId(String tradeId){
        return  trdBondTradeMapper.queryTradeInfoByTradeId(tradeId);
    }

    /**
     * 查询证券托管户：下拉框
     *
     * @param param 画面参数
     * @return 证券托管户：下拉框
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getSecManageAcctId(Map<String, Object> param) {
        Object finprodIdTemp = param.get("finprodId");
        String finProdType = DictCodeListExt1.金融产品类型_投管用_债券.getValue();
        //部分页面没有finprodId特殊处理
        if(StringUtil.isNotEmpty(finprodIdTemp) && !StringUtil.equals(finprodIdTemp.toString().substring(0, 3), "F01")){
            param.put("finprodId",finProdType.concat(GlobalConstant.UNDER_LINE).concat(finprodIdTemp.toString()));
        }
        return  trdBondTradeMapper.getSecManageAcctId(param);
    }

    public Page<SelectOptionDto> getChannelId(int page, int length, Map<String, Object> params) {
        params.put("Y", DictCodeList.是否_是.getValue());
        params.put("F28", DictCodeListExt1.金融产品类型_投管用_通道.getValue());
        return super.queryForPageForSelectReturn(page, length, ()->trdBondTradeMapper.getChannelId(params));
    }

    public int querySequences(){
        int i=trdBondTradeMapper.querySequences();
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
        TrdBondTradeDto dto1 = new TrdBondTradeDto();
        dto1.setProdId(dto.getTrdBondTradeDto().getProdId());
        dto1.setTradeId(dto.getTrdBondTradeDto().getTradeId());
       trdBondTrade001Service.delete(dto1);
       return insert(dto);
    }

}
