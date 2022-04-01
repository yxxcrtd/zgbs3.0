package com.joyintech.service.valuation.trademanage.trdstockvaluationduration.service;


import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.valuation.trademanage.trdstockvaluationduration.dao.TrdStockValuationDurationMapper;
import com.joyintech.service.valuation.trademanage.trdstockvaluationduration.dto.TrdStockValuationDurationDto;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCommonBatchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value="serviceTrdStockValuationDuration001Service")
public class TrdStockValuationDuration001Service extends BaseService {

    @Autowired
    private TrdStockValuationDurationMapper trdStockValuationDurationMapper;

    @Autowired
    private ValCommonBatchMapper valCommonBatchMapper;

    /**
     * 分页查询
     *
     * @param page 页编号
     * @param length 页条数
     * @param pageParam 画面参数
     * @return 查询结果
     * @throws Exception 异常
     * @see
     * @since 1.0
     */
    public Page<TrdStockValuationDurationDto> selectForPage(int page, int length,
                                                            Map<String, Object> pageParam)
            throws Exception {
        //产品代码
        String finprodId = (String)pageParam.get("finprodId");
        String[] finprodIdArr = null;
        if(StringUtil.isNotEmpty(finprodId)){
            finprodIdArr = finprodId.split(",");
        }
        pageParam.put("finprodIdArr", finprodIdArr);
        //资产代码
        String asset = (String)pageParam.get("asset");
        String[] assetIdArr = null;
        if(StringUtil.isNotEmpty(asset)){
            assetIdArr = asset.split(",");
        }
        pageParam.put("assetIdArr", assetIdArr);
        // 获取交易方向
        String ps = (String)pageParam.get("ps");
        // 交易方向多选，转为数组
        String[] psArr = null;
        if (StringUtil.isNotEmpty(ps)) {
            psArr = ps.split(",");
        }
        pageParam.put("psArr", psArr);

        return this.queryForPage(page, length,
                () -> trdStockValuationDurationMapper.selectDeal(pageParam));
    }

    /**
     * 不分页查询
     *
     * @see
     * @since 1.0
     */
    public List<TrdStockValuationDurationDto> queryPageList(Map<String, Object> params){
        //产品代码
        String finprodId = (String)params.get("finprodId");
        String[] finprodIdArr = null;
        if(StringUtil.isNotEmpty(finprodId)){
            finprodIdArr = finprodId.split(",");
        }
        params.put("finprodIdArr", finprodIdArr);
        //资产代码
        String asset = (String)params.get("asset");
        String[] assetIdArr = null;
        if(StringUtil.isNotEmpty(asset)){
            assetIdArr = asset.split(",");
        }
        params.put("assetIdArr", assetIdArr);
        // 获取交易方向
        String ps = (String)params.get("ps");
        // 交易方向多选，转为数组
        String[] psArr = null;
        if (StringUtil.isNotEmpty(ps)) {
            psArr = ps.split(",");
        }
        params.put("psArr", psArr);
        return trdStockValuationDurationMapper.selectDeal(params);
    }


    public int delete(TrdStockValuationDurationDto dto) {
        int count=0;
        Map<String, Object> params = new HashMap<>();
        params.put("booksetId",dto.getProdId());
        params.put("tradeId",dto.getTradeId());

        List<TrdProductDeal> listDeal = new ArrayList<>();
        TrdProductDeal entity = new TrdProductDeal();
        entity.setTradeId(dto.getTradeId());
        listDeal.add(entity);
        // 1.估值核算交易扩展表 TRD_PRODUCT_DEAL_ADD
        count += valCommonBatchMapper.delProductDealExtByTradeId(listDeal);
        // 2.入账账务明细表 BOK_DEAL_ACCOUNTING_DETAIL
        count += valCommonBatchMapper.delDealAccountingDetailByTradeId(listDeal);
        // 3.入账信息表 BOK_DEAL_ACCOUNTING
        count += valCommonBatchMapper.delDealAccountingByTradeId(listDeal);
        // 4.结算指令扩展表 STL_ORDERS_EXT
        count += valCommonBatchMapper.delOrdersExtByTradeId(listDeal);
        // 5.结算指令表 STL_ORDERS
        count += valCommonBatchMapper.delOrdersByTradeId(listDeal);
        // 6.金融产品交易 TRD_PRODUCT_DEAL
        count += valCommonBatchMapper.delProductDealByTradeId(listDeal);
        // 7.账务明细代码表 BOK_DETAIL_ID
        count += trdStockValuationDurationMapper.delBokDetailIdByTradeId(params);

        return count;
    }


}
