/*
 * 文件名：TrdLendInvestTrade002Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：杨智 修改时间：2018年10月10日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.valuation.trademanage.trddepovaluationduration.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;

import com.joyintech.tams.valuation.common.interf.base.dto.AssetDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.sec.SecVestPeriodTemplateRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSecVestPeriod;
import com.joyintech.tams.valuation.trademanage.trddepovaluationduration.dto.TrdDepoValuationDuration001Dto;
import com.joyintech.tams.valuation.trademanage.trddepovaluationduration.dto.TrdDepoValuationDurationResult;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class TrdDepoValuationDuration002Service extends BaseService<TrdDepoValuationDuration001Dto> implements BaseProcessService<TrdDepoValuationDuration001Dto> {

    @Autowired
    private SecVestPeriodTemplateRecService secVestPeriodTemplateRecService;

    /**
     * 合成初始化实体类
     *
     * @param tradeId 交易编号
     * @return 存款类资产实体类
     * @see
     * @since 1.0
     */
    public TrdDepoValuationDuration001Dto getInitEntity(String tradeId) {
        TrdDepoValuationDuration001Dto entity = new TrdDepoValuationDuration001Dto();
        TrdProductDeal trdProductDeal = null;
        TrdProductDealAdd trdProductDealAdd = null;
        String finprodId = null;
        FinProduct finProduct = null;
        return entity;
    }
    /**
     * 根据交易编号查询详情
     */
    public TrdDepoValuationDuration001Dto queryTradeInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdDepoValuationDuration001/queryTradeInfoByTradeId",
                tradeId, TrdDepoValuationDuration001Dto.class);
    }

    /**
     * 策略代码生成
     *
     * @see
     * @since 1.0
     */
    public String createPlanTradeId() {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdDepoValuationDuration001/createPlanTradeId",
                null, String.class);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(TrdDepoValuationDuration001Dto entity, OperationType operationType) {
        String action = StringUtil.EMPTY;
        TrdDepoValuationDurationResult trdDepositValuationTradeResult = new TrdDepoValuationDurationResult();
        trdDepositValuationTradeResult = generateEntity(entity);
        if (OperationType.NEW.equals(operationType)) {
            action = "insert";
        }
        else {
            action = "update";
        }

        restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdDepositValuationTrade001/" + action, trdDepositValuationTradeResult, Integer.class);
    }

    //封装实体类
    public TrdDepoValuationDurationResult generateEntity(TrdDepoValuationDuration001Dto dto){
        //设置接口参数
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(dto.getPortfolioId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        List<ValSecVestPeriod> listValSecVestPeriod = new ArrayList<>();
        ValSecVestPeriod entity = new ValSecVestPeriod();
        BeanUtil.copyPropertiesIgnoreNull(entity,dto);
//        entity.setFundSettleDate(DateUtil.parse(dto.getFundSettleDate()));
        //比较界面和接口字段差异，并手动给接口字段赋值
//        entity.setAssetId("F01_"+dto.getAssetId());
//        entity.setSecId(dto.getAssetId());
//        entity.setSecAbbr(dto.getAssetAbbr());
//        entity.setDataActionSign("01");//数据动作标志
//        entity.setIsAmt("N");//是否还本带息
//        entity.setVdate(new Date());//计息开始日
//        entity.setMdate(new Date());//计息结束日
//        entity.setParValue(new BigDecimal(100));//单位面值
//        entity.setSettleDate(new Date());//交割日
//        entity.setTradeDate(new Date());//交易日
//        entity.setIsClean("N");//有无清算指令
//        entity.setDealNum(new BigDecimal(0));//成交数量

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
        TrdDepoValuationDurationResult rst = new TrdDepoValuationDurationResult(result);
        return rst;
    }

}
