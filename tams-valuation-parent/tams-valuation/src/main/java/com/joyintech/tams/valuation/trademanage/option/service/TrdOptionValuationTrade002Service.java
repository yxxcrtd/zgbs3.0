package com.joyintech.tams.valuation.trademanage.option.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.ValOtcOptTradeCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.opt.ValOtcOptTradeTemplateRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValOtcOptTrade;
import com.joyintech.tams.valuation.trademanage.option.dto.TrdOptionValuationTrade002Dto;
import com.joyintech.tams.valuation.trademanage.option.dto.TrdOptionValuationTradeResult;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TrdOptionValuationTrade002Service extends CommonExportService<TrdOptionValuationTrade002Dto> implements BaseProcessService<TrdOptionValuationTrade002Dto> {

    @Autowired
    private ValOtcOptTradeTemplateRecService valOtcOptTradeTemplateRecService;

    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @return 产品代码列表
     * @see
     * @since 1.0
     */
    public TrdOptionValuationTrade002Dto queryTradeInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdOptionValuationTrade002/queryTradeInfoByTradeId", tradeId,
                TrdOptionValuationTrade002Dto.class);
    }

    /**
     * 交易对手下拉框
     *
     * @param param
     * @return
     * @see
     * @since 1.0
     */
    public List<SelectOptionDto> getCounterId(Map<String, Object> param) {
        return restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME + "/TrdOptionValuationTrade002/getCounterId",
                param, List.class);
    }

//    /**
//     * 通过交易编号查询交易信息（到期）
//     *
//     * @return 产品代码列表
//     * @see
//     * @since 1.0
//     */
//    public TrdBondLendTrade003Dto queryInfoByTradeId(String tradeId) {
//        return restTemplate.postForObject(
//                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondLendTrade002/queryInfoByTradeId", tradeId,
//                TrdBondLendTrade003Dto.class);
//    }

    //自增序列号
    public int querySequences(){
        return restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdOptionValuationTrade002/querySequences", null,Integer.class);
    }


    @Override
    public void doComplete(TrdOptionValuationTrade002Dto trdOptionValuationTrade002Dto, OperationType operationType) {
        TrdOptionValuationTradeResult trdOptionValuationTradeResult = generateEntity(trdOptionValuationTrade002Dto);
        String action = StringUtil.EMPTY;
         if (OperationType.NEW.equals(operationType)) {
             action = "insert";
        }
        else {
            action = "update";
        }
        restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdOptionValuationTrade002/" + action, trdOptionValuationTradeResult,
                Integer.class);

    }

    //封装实体类
    public TrdOptionValuationTradeResult generateEntity(TrdOptionValuationTrade002Dto dto){
        //设置接口参数
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(dto.getProdId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        // 定义返回变量
        ValOtcOptTradeCommonResult result = new ValOtcOptTradeCommonResult();

        //将页面输入值赋值给接口实体类
        List<ValOtcOptTrade> listValOtcOptTrade = new ArrayList<ValOtcOptTrade>();
        ValOtcOptTrade entity = new ValOtcOptTrade();
        entity.setTradeId(dto.getTradeId());//成交编号
        entity.setProdId(dto.getProdId());//产品代码
        entity.setAssetId(dto.getTradeId());//资产代码
        entity.setPs(dto.getPs());//交易方向
        entity.setDealDate(DateUtil.parse(dto.getTradeDate()));//交易日
        entity.setCcy(dto.getCcy());//币种

        //比较界面和接口字段差异，并手动给接口字段赋值
        entity.setBranch((long) 0);//分支序号
        entity.setDataActionSign("01");//数据动作标志
        entity.setEffDate(DateUtil.parse(dto.getTradeDate()));//交割日
        entity.setDealNum(dto.getSettleAmt());//交易份额
        entity.setTradeAmt(dto.getSettleAmt());//交易金额
        entity.setFpriceAmt(dto.getSettleAmt());//全价总额
        entity.setIsChlFeeAmt("N");//通道费是否随交易支付

        listValOtcOptTrade.add(entity);

        //调用接口处理方法
        Map<String, Object> map = valOtcOptTradeTemplateRecService.handleValOtcOptTrade(listValOtcOptTrade,new Date(),params,null);

        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal = (List<TrdProductDeal>)map.get(
                "ValFinProductDeal");
        result.setListValFinProductDeal(listValFinProductDeal);
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt = (List<TrdProductDealAdd>)map.get(
                "ValFinProductDealExt");
        result.setListValFinProductDealExt(listValFinProductDealExt);
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get(
                "ValDealAccounting");
        result.setListValDealAccounting(listValDealAccounting);
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get(
                "ValDealAccountingDetail");
        result.setListValDealAccountingDetail(listValDealAccountingDetail);
        // 账务明细代码表List
        List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get(
                "ValBokDetailId");
        result.setListValBokDetailId(listValBokDetailId);
        //结算指令表List
        List<StlOrders> valOrdersList=(List<StlOrders>)map.get("valOrdersList");
        result.setListValOrders(valOrdersList);
        //结算指令拓展表list
        List<StlOrdersExt> valOrdersExtList=(List<StlOrdersExt>)map.get("valOrdersExtList");
        result.setListValOrdersExt(valOrdersExtList);

        TrdOptionValuationTradeResult rst = new TrdOptionValuationTradeResult(result);
        rst.setTrdOptionValuationTrade002Dto(dto);
        return rst;

    }

}
