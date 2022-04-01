package com.joyintech.service.valuation.trademanage.trdbondchangevaluationtrade.service;


import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.valuation.trademanage.trdbondchangevaluationtrade.dao.TrdBondChangeTradeMapper;
import com.joyintech.service.valuation.trademanage.trdbondchangevaluationtrade.dto.TrdBondChangeTradeDto;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCommonBatchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value="serviceTrdBondChangeTrade001Service")
public class TrdBondChangeTrade001Service extends BaseService {

    @Autowired
    private TrdBondChangeTradeMapper trdBondChangeTradeMapper;

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
    public Page<TrdBondChangeTradeDto> selectForPage(int page, int length,
                                                     Map<String, Object> pageParam)
            throws Exception {
        //产品代码
        String finprodId = (String)pageParam.get("finprodId");
        String[] finprodIdArr = null;
        if(StringUtil.isNotEmpty(finprodId)){
            finprodIdArr = finprodId.split(",");
        }
        pageParam.put("finprodIdArr", finprodIdArr);
        //转出证券代码
        String oAssetId = (String)pageParam.get("oAssetId");
        String[] assetIdArr = null;
        if(StringUtil.isNotEmpty(oAssetId)){
            assetIdArr = oAssetId.split(",");
        }
        pageParam.put("assetIdArr", assetIdArr);
        //转入证券代码
        String cAssetId = (String)pageParam.get("cAssetId");
        String[] cAssetIdArr = null;
        if(StringUtil.isNotEmpty(cAssetId)){
            cAssetIdArr = cAssetId.split(",");
        }
        pageParam.put("cAssetIdArr", cAssetIdArr);

        // 获取交易方向
        String ps = (String)pageParam.get("ps");
        // 交易方向多选，转为数组
        String[] psArr = null;
        if (StringUtil.isNotEmpty(ps)) {
            psArr = ps.split(",");
        }
        pageParam.put("psArr", psArr);

        return this.queryForPage(page, length,
                () -> trdBondChangeTradeMapper.selectDeal(pageParam));
    }

    /**
     * 不分页查询
     *
     * @see
     * @since 1.0
     */
    public List<TrdBondChangeTradeDto> queryPageList(Map<String, Object> params){
        //产品代码
        String finprodId = (String)params.get("finprodId");
        String[] finprodIdArr = null;
        if(StringUtil.isNotEmpty(finprodId)){
            finprodIdArr = finprodId.split(",");
        }
        params.put("finprodIdArr", finprodIdArr);
        //转出证券代码
        String oAssetId = (String)params.get("oAssetId");
        String[] assetIdArr = null;
        if(StringUtil.isNotEmpty(oAssetId)){
            assetIdArr = oAssetId.split(",");
        }
        params.put("assetIdArr", assetIdArr);
        //转入证券代码
        String cAssetId = (String)params.get("cAssetId");
        String[] cAssetIdArr = null;
        if(StringUtil.isNotEmpty(cAssetId)){
            cAssetIdArr = cAssetId.split(",");
        }
        params.put("cAssetIdArr", cAssetIdArr);

        // 获取交易方向
        String ps = (String)params.get("ps");
        // 交易方向多选，转为数组
        String[] psArr = null;
        if (StringUtil.isNotEmpty(ps)) {
            psArr = ps.split(",");
        }
        params.put("psArr", psArr);

        return trdBondChangeTradeMapper.selectDeal(params);
    }

    /**
     * 转出证券名称联想 <功能详细描述>
     *
     * @param map 用户输入值
     * @return page<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getAssetId(int page,int length,Map<String, Object> map) {
        map.put("Bond_Type1",DictCodeList.WIND一级分类_含债券一级分类等_可转债.getValue());
        map.put("Bond_Type2",DictCodeList.WIND一级分类_含债券一级分类等_可交换债.getValue());
        map.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        return this.queryForPageForSelectReturn(page, length,() -> trdBondChangeTradeMapper.getAssetId(map));
    }

    /**
     * 正股代码联想 <功能详细描述>
     *
     * @param map 用户输入值
     * @return page<SelectOptionDto>
     * @see
     * @since 1.0
     */
    public Page<SelectOptionDto> getStockId(int page,int length,Map<String, Object> map) {
        map.put("FINPROD_TYPE_TG_F06", DictCodeListExt1.金融产品类型_投管用_基金.getValue());
        return this.queryForPageForSelectReturn(page, length,() -> trdBondChangeTradeMapper.getStockId(map));
    }

    public int delete(TrdBondChangeTradeDto dto) {
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
        // 8.金融产品实物交易 TRD_FIN_PRODUCT_BARTER
        count += valCommonBatchMapper.delTrdFinProductBarterByTradeId(listDeal);
        // 9.账务明细代码表 BOK_DETAIL_ID
        count += trdBondChangeTradeMapper.delBokDetailIdByTradeId(params);

        return count;
    }


}
