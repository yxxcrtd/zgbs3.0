package com.joyintech.service.valuation.trademanage.trdbondvaluationduration.service;


import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.valuation.component.common.constants.BussinessDictCodeList;
import com.joyintech.service.valuation.trademanage.trdbondvaluationduration.dao.TrdBondValuationDurationMapper;
import com.joyintech.service.valuation.trademanage.trdbondvaluationduration.dto.TrdBondValuationDurationDto;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCommonBatchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value="serviceTrdBondValuationDuration001Service")
public class TrdBondValuationDuration001Service extends BaseService {

    @Autowired
    private TrdBondValuationDurationMapper trdBondValuationDurationMapper;

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
    public Page<TrdBondValuationDurationDto> selectForPage(int page, int length,
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

        // 获取数据来源
        String dataSource = (String)pageParam.get("dataSource");
        // 数据来源多选，转为数组
        String[] dataSourceArr = null;
        if (StringUtil.isNotEmpty(dataSource)) {
            dataSourceArr = dataSource.split(",");
        }
        pageParam.put("dataSourceArr", dataSourceArr);

        pageParam.put("BUSI_TYPE_02", BussinessDictCodeList.业务类型_债券交易.getValue());
        pageParam.put("PS_T22", DictCodeList.交易方向_收息到账.getValue());
        pageParam.put("PS_T23", DictCodeList.交易方向_还本到账.getValue());

        return this.queryForPage(page, length,
                () -> trdBondValuationDurationMapper.selectDeal(pageParam));
    }

    public int delete(TrdBondValuationDurationDto dto) {
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
        count += trdBondValuationDurationMapper.delBokDetailIdByTradeId(params);

        return count;
    }


}
