package com.joyintech.tams.valuation.trademanage.trdbondvaluationduration.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.AssetDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.sec.SecVestPeriodTemplateRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSecVestPeriod;
import com.joyintech.tams.valuation.trademanage.trdbondvaluationduration.dto.TrdBondValuationDuration002Dto;
import com.joyintech.tams.valuation.trademanage.trdbondvaluationduration.dto.TrdBondValuationDurationResult;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service
public class TrdBondValuationDuration002Service extends CommonExportService<TrdBondValuationDuration002Dto> implements BaseProcessService<TrdBondValuationDuration002Dto> {

    @Autowired
    private SecVestPeriodTemplateRecService secVestPeriodTemplateRecService;

    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @return 产品代码列表
     * @see
     * @since 1.0
     */
    public TrdBondValuationDuration002Dto queryTradeInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondValuationDuration002/queryTradeInfoByTradeId", tradeId,
                TrdBondValuationDuration002Dto.class);
    }

    //自增序列号
    public int querySequences(){
        return restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdBondValuationDuration002/querySequences", null,Integer.class);
    }


    @Override
    public void doComplete(TrdBondValuationDuration002Dto trdBondValuationDuration002Dto, OperationType operationType) {
        TrdBondValuationDurationResult result = generateEntity(trdBondValuationDuration002Dto);
        String action = StringUtil.EMPTY;
        if (OperationType.NEW.equals(operationType)) {
            action = "insert";
        }
        else {
            action = "update";
        }
        restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdBondValuationDuration002/" + action, result,
                Integer.class);

    }

    public TrdBondValuationDurationResult generateEntity(TrdBondValuationDuration002Dto dto){
        //设置接口参数
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(dto.getProdId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        List<ValSecVestPeriod> listValSecVestPeriod = new ArrayList<>();
        ValSecVestPeriod entity = new ValSecVestPeriod();
        BeanUtil.copyPropertiesIgnoreNull(entity,dto);
        entity.setFundSettleDate(dto.getFundSettleDate());
        //比较界面和接口字段差异，并手动给接口字段赋值
        entity.setAssetId("F01_"+dto.getAssetId());
        entity.setSecId(dto.getAssetId());
        entity.setSecAbbr(dto.getAssetAbbr());
        entity.setDataActionSign("01");//数据动作标志
        entity.setIsAmt("N");//是否还本带息
        entity.setVdate(new Date());//计息开始日
        entity.setMdate(new Date());//计息结束日
        entity.setParValue(new BigDecimal(100));//单位面值
        entity.setSettleDate(new Date());//交割日
        entity.setTradeDate(new Date());//交易日
        entity.setIsClean("N");//有无清算指令
        entity.setDealNum(new BigDecimal(0));//成交数量

        listValSecVestPeriod.add(entity);
        Map<String, Object> map = secVestPeriodTemplateRecService.handleValSecVestPeriod(listValSecVestPeriod,new Date(),params,null);
        AssetDealInfoValCommonResult result = new AssetDealInfoValCommonResult();
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
        TrdBondValuationDurationResult rst = new TrdBondValuationDurationResult(result);
        rst.setTrdBondValuationDuration002Dto(dto);
        return rst;
     }

}
