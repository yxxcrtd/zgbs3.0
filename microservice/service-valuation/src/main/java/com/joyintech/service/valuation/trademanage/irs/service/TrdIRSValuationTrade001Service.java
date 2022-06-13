package com.joyintech.service.valuation.trademanage.irs.service;


import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.valuation.trademanage.irs.dao.TrdIRSValuationTradeMapper;
import com.joyintech.service.valuation.trademanage.irs.dto.TrdIRSValuationTrade001Dto;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCommonBatchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value="serviceTrdIRSValuationTrade001Service")
public class TrdIRSValuationTrade001Service extends BaseService {

    @Autowired
    private TrdIRSValuationTradeMapper trdIRSValuationTradeMapper;

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
    public Page<TrdIRSValuationTrade001Dto> selectForPage(int page, int length,
                                                          Map<String, Object> pageParam)
            throws Exception {
        //产品代码
        String finprodId = (String)pageParam.get("finprodId");
        String[] finprodIdArr = null;
        if(StringUtil.isNotEmpty(finprodId)){
            finprodIdArr = finprodId.split(",");
        }
        pageParam.put("finprodIdArr", finprodIdArr);

        pageParam.put("INT_TYPE_01", DictCodeList.利率类型_固定利率.getValue());
        pageParam.put("INT_TYPE_02", DictCodeList.利率类型_浮动利率.getValue());

        return this.queryForPage(page, length,
                () -> trdIRSValuationTradeMapper.selectDeal(pageParam));
    }

    /**
     * 不分页查询
     *
     * @see
     * @since 1.0
     */
    public List<TrdIRSValuationTrade001Dto> queryPageList(Map<String, Object> params){
        //产品代码
        String finprodId = (String)params.get("finprodId");
        String[] finprodIdArr = null;
        if(StringUtil.isNotEmpty(finprodId)){
            finprodIdArr = finprodId.split(",");
        }
        params.put("finprodIdArr", finprodIdArr);

        params.put("INT_TYPE_01", DictCodeList.利率类型_固定利率.getValue());
        params.put("INT_TYPE_02", DictCodeList.利率类型_浮动利率.getValue());

        return trdIRSValuationTradeMapper.selectDeal(params);
    }


    public int delete(TrdIRSValuationTrade001Dto dto) {
        int count=0;
        Map<String, Object> params = new HashMap<>();
        params.put("booksetId",dto.getFinprodId());
        params.put("tradeId",dto.getTradeId());

        List<TrdProductDeal> listDeal = new ArrayList<>();
        TrdProductDeal entity = new TrdProductDeal();
        entity.setTradeId(dto.getTradeId());
        listDeal.add(entity);
        // 1.估值核算交易扩展表 TRD_PRODUCT_DEAL_ADD
        count += valCommonBatchMapper.delProductDealExtByTradeId(listDeal);
        // 2.交易费用明细表 TRD_TRADE_FEE_DETAIL
        count += valCommonBatchMapper.delTradeFeeDetailByTradeId(listDeal);
        // 3.入账账务明细表 BOK_DEAL_ACCOUNTING_DETAIL
        count += valCommonBatchMapper.delDealAccountingDetailByTradeId(listDeal);
        // 4.入账信息表 BOK_DEAL_ACCOUNTING
        count += valCommonBatchMapper.delDealAccountingByTradeId(listDeal);
        // 5.结算指令扩展表 STL_ORDERS_EXT
        count += valCommonBatchMapper.delOrdersExtByTradeId(listDeal);
        // 6.结算指令表 STL_ORDERS
        count += valCommonBatchMapper.delOrdersByTradeId(listDeal);
        // 7.金融产品交易 TRD_PRODUCT_DEAL
        count += valCommonBatchMapper.delProductDealByTradeId(listDeal);
        // 9.账务明细代码表 BOK_DETAIL_ID
        count += trdIRSValuationTradeMapper.delBokDetailIdByTradeId(params);

        return count;
    }


}
