package com.joyintech.service.valuation.trademanage.option.service;


import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.valuation.trademanage.option.dao.TrdOptionValuationTradeMapper;
import com.joyintech.service.valuation.trademanage.option.dto.TrdOptionValuationTrade001Dto;
import com.joyintech.tams.common.base.entity.TrdProductDeal;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.valuation.common.interf.base.batch.dao.ValCommonBatchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service(value="serviceTrdOptionValuationTrade001Service")
public class TrdOptionValuationTrade001Service extends BaseService {

    @Autowired
    private TrdOptionValuationTradeMapper trdOptionValuationTradeMapper;

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
    public Page<TrdOptionValuationTrade001Dto> selectForPage(int page, int length,
                                                     Map<String, Object> pageParam)
            throws Exception {
        //产品代码
        String finprodId = (String)pageParam.get("finprodId");
        String[] finprodIdArr = null;
        if(StringUtil.isNotEmpty(finprodId)){
            finprodIdArr = finprodId.split(",");
        }
        pageParam.put("finprodIdArr", finprodIdArr);

        //合约标的
        String targetId = (String)pageParam.get("targetId");
        String[] targetIdArr = null;
        if(StringUtil.isNotEmpty(targetId)){
            targetIdArr = targetId.split(",");
        }
        pageParam.put("targetIdArr", targetIdArr);

        //合约类型
        String contractType = (String)pageParam.get("contractType");
        String[] contractTypeArr = null;
        if(StringUtil.isNotEmpty(contractType)){
            contractTypeArr = contractType.split(",");
        }
        pageParam.put("contractTypeArr", contractTypeArr);

        // 获取交易方向
        String ps = (String)pageParam.get("ps");
        // 交易方向多选，转为数组
        String[] psArr = null;
        if (StringUtil.isNotEmpty(ps)) {
            psArr = ps.split(",");
        }
        pageParam.put("psArr", psArr);

        pageParam.put("busiType", DictCodeListExt3.业务类型_场外期权.getValue());

        return this.queryForPage(page, length,
                () -> trdOptionValuationTradeMapper.selectDeal(pageParam));
    }

    /**
     * 不分页查询
     *
     * @see
     * @since 1.0
     */
    public List<TrdOptionValuationTrade001Dto> queryPageList(Map<String, Object> params){
        //产品代码
        String finprodId = (String)params.get("finprodId");
        String[] finprodIdArr = null;
        if(StringUtil.isNotEmpty(finprodId)){
            finprodIdArr = finprodId.split(",");
        }
        params.put("finprodIdArr", finprodIdArr);

        //合约标的
        String targetId = (String)params.get("targetId");
        String[] targetIdArr = null;
        if(StringUtil.isNotEmpty(targetId)){
            targetIdArr = targetId.split(",");
        }
        params.put("targetIdArr", targetIdArr);

        //合约类型
        String contractType = (String)params.get("contractType");
        String[] contractTypeArr = null;
        if(StringUtil.isNotEmpty(contractType)){
            contractTypeArr = contractType.split(",");
        }
        params.put("contractTypeArr", contractTypeArr);

        // 获取交易方向
        String ps = (String)params.get("ps");
        // 交易方向多选，转为数组
        String[] psArr = null;
        if (StringUtil.isNotEmpty(ps)) {
            psArr = ps.split(",");
        }
        params.put("psArr", psArr);

        params.put("busiType", DictCodeListExt3.业务类型_场外期权.getValue());
        return trdOptionValuationTradeMapper.selectDeal(params);
    }

    /**
     * 合约标的查询
     * @param param
     * @return
     */
    public List<SelectOptionDto> selectTargetIdAbbr(Map<String, Object> param){
        param.put("FINPROD_TYPE_F35", DictCodeListExt1.金融产品类型_投管用_期权.getValue());
        return trdOptionValuationTradeMapper.selectTargetIdAbbr(param);
    }


    public int delete(TrdOptionValuationTrade001Dto dto) {
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
        count += trdOptionValuationTradeMapper.delBokDetailIdByTradeId(params);

        return count;
    }


}
