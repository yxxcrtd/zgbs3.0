package com.joyintech.service.valuation.trademanage.bondlend.service;


import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.service.valuation.trademanage.bondlend.dao.TrdBondLendTradeMapper;
import com.joyintech.service.valuation.trademanage.bondlend.dto.TrdBondLendTradeDto;
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

@Service(value="serviceTrdBondLendTrade001Service")
public class TrdBondLendTrade001Service extends BaseService {

    @Autowired
    private TrdBondLendTradeMapper trdBondLendTradeMapper;

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
    public Page<TrdBondLendTradeDto> selectForPage(int page, int length,
                                                   Map<String, Object> pageParam)
            throws Exception {
        //产品代码
        String finprodId = (String)pageParam.get("finprodId");
        String[] finprodIdArr = null;
        if(StringUtil.isNotEmpty(finprodId)){
            finprodIdArr = finprodId.split(",");
        }
        pageParam.put("finprodIdArr", finprodIdArr);

        // 获取交易方向
        String ps = (String)pageParam.get("ps");
        String flag = "0";
        String rongru = "";
        String rongchu = "";
        String daoqi = "";
        // 交易方向多选，转为数组
        String[] psArr = null;
        if (StringUtil.isNotEmpty(ps)) {
            psArr = ps.split(",");
            if(psArr.length == 1){
                if(psArr[0].equals("01")){//融入
                    flag = "01";
                }else if(psArr[0].equals("02")){//融出
                    flag = "02";
                }else if(psArr[0].equals("03")){//到期
                    flag = "03";
                }
            }else if(psArr.length == 2){
                for(int i = 0 ; i < psArr.length; i++){
                    if(psArr[i].equals("01")){//融入
                        rongru = psArr[i];
                    }else if(psArr[i].equals("02")){//融出
                        rongchu = psArr[i];
                    }else{//到期
                        daoqi = psArr[i];
                    }
                }
                if(StringUtil.isNotEmpty(rongru) && StringUtil.isNotEmpty(rongchu)){
                    flag = "04";
                }else if(StringUtil.isNotEmpty(rongru) && StringUtil.isNotEmpty(daoqi)){
                    flag = "05";
                }else if(StringUtil.isNotEmpty(rongchu) && StringUtil.isNotEmpty(daoqi)){
                    flag = "06";
                }
            }else if(psArr.length == 3){
                flag = "0";
            }else{
                flag = "0";
            }
        }

        pageParam.put("flag", flag);
        pageParam.put("FINPROD_TYPE_F23", DictCodeListExt1.金融产品类型_债券借贷融入.getValue());
        pageParam.put("FINPROD_TYPE_F24", DictCodeListExt1.金融产品类型_债券借贷融出.getValue());
        pageParam.put("ps", DictCodeList.交易方向_到期.getValue());

        return this.queryForPage(page, length,
                () -> trdBondLendTradeMapper.selectDeal(pageParam));
    }

    /**
     * 不分页查询
     *
     * @see
     * @since 1.0
     */
    public List<TrdBondLendTradeDto> queryPageList(Map<String, Object> params){
        //产品代码
        String finprodId = (String)params.get("finprodId");
        String[] finprodIdArr = null;
        if(StringUtil.isNotEmpty(finprodId)){
            finprodIdArr = finprodId.split(",");
        }
        params.put("finprodIdArr", finprodIdArr);

        // 获取交易方向
        String ps = (String)params.get("ps");
        String flag = "0";
        String rongru = "";
        String rongchu = "";
        String daoqi = "";
        // 交易方向多选，转为数组
        String[] psArr = null;
        if (StringUtil.isNotEmpty(ps)) {
            psArr = ps.split(",");
            if(psArr.length == 1){
                if(psArr[0].equals("01")){//融入
                    flag = "01";
                }else if(psArr[0].equals("02")){//融出
                    flag = "02";
                }else if(psArr[0].equals("03")){//到期
                    flag = "03";
                }
            }else if(psArr.length == 2){
                for(int i = 0 ; i < psArr.length; i++){
                    if(psArr[i].equals("01")){//融入
                        rongru = psArr[i];
                    }else if(psArr[i].equals("02")){//融出
                        rongchu = psArr[i];
                    }else{//到期
                        daoqi = psArr[i];
                    }
                }
                if(StringUtil.isNotEmpty(rongru) && StringUtil.isNotEmpty(rongchu)){
                    flag = "04";
                }else if(StringUtil.isNotEmpty(rongru) && StringUtil.isNotEmpty(daoqi)){
                    flag = "05";
                }else if(StringUtil.isNotEmpty(rongchu) && StringUtil.isNotEmpty(daoqi)){
                    flag = "06";
                }
            }else if(psArr.length == 3){
                flag = "0";
            }else{
                flag = "0";
            }
        }

        params.put("flag", flag);
        params.put("FINPROD_TYPE_F23", DictCodeListExt1.金融产品类型_债券借贷融入.getValue());
        params.put("FINPROD_TYPE_F24", DictCodeListExt1.金融产品类型_债券借贷融出.getValue());
        params.put("ps", DictCodeList.交易方向_到期.getValue());

        return trdBondLendTradeMapper.selectDeal(params);
    }


    public int delete(TrdBondLendTradeDto dto) {
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
        // 9.账务明细代码表 BOK_DETAIL_ID
        count += trdBondLendTradeMapper.delBokDetailIdByTradeId(params);

        return count;
    }


}
