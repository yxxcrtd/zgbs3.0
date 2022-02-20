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
import com.joyintech.tams.valuation.common.interf.base.dto.TemplateOrderInfoReaderResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.depositasset.DepositeAssetPeriodFundTemplateRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValBotPeriodFund;
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
    private DepositeAssetPeriodFundTemplateRecService depositeAssetPeriodFundTemplateRecService;

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
        TrdDepoValuationDurationResult trdDepoValuationDurationResult = new TrdDepoValuationDurationResult();
        trdDepoValuationDurationResult = generateEntity(entity);
        if (OperationType.NEW.equals(operationType)) {
            action = "insert";
        }
        else {
            action = "update";
        }

        restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdDepositValuationTrade001/" + action, trdDepoValuationDurationResult, Integer.class);
    }

    //封装实体类
    public TrdDepoValuationDurationResult generateEntity(TrdDepoValuationDuration001Dto dto){
        //设置接口参数
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(dto.getPortfolioId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        //将页面输入值赋值给接口实体类
        ArrayList<ValBotPeriodFund> listValBotPeriodFund = new ArrayList<>();
        ValBotPeriodFund entity = new ValBotPeriodFund();
        BeanUtil.copyPropertiesIgnoreNull(entity,dto);
//        entity.setFundSettleDate(DateUtil.parse(dto.getFundSettleDate()));
        //比较界面和接口字段差异，并手动给接口字段赋值
        entity.setProdId(dto.getPortfolioId()); //资产代码
        entity.setAssetId("F01"+dto.getFinprodMarketId());  //资产代码
        entity.setDataActionSign("01");//数据动作标志
        entity.setDealType(dto.getPs()); //交易类型
        entity.setVdate(new Date());//计息开始日
        entity.setEndDate(new Date());//计息结束日
        entity.setPlanPayDate(dto.getPlanPayDate()); //计划支付日
        entity.setPayDate(dto.getPayDate()); //实际支付日
        entity.setActInt(dto.getActInt()); //实收利息
        entity.setActCapi(dto.getActCapi());//实收本金
        entity.setDealAmt(dto.getDealAmt()); //交易金额
        entity.setIsClean2("Y");//是否结清
        entity.setCcy(dto.getCcy());//币种
        entity.setTradeId(dto.getTradeId());  //交易编号
        entity.setIsClean("Y");//有无清算指令

        listValBotPeriodFund.add(entity);
        TemplateOrderInfoReaderResult acctEntity = new TemplateOrderInfoReaderResult();
        Date date = new Date();
        acctEntity.setResovleDate(date);
        acctEntity.setFourLevelSubjectKg("Y");
        Map<String, Object> map = depositeAssetPeriodFundTemplateRecService.handleValBotPeriodFund(listValBotPeriodFund,acctEntity,params,null);
        // 定义返回变量
        AssetDealInfoValCommonResult result = new AssetDealInfoValCommonResult();
        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal = (List<TrdProductDeal>)map.get("ValFinProductDeal");
        result.setListValFinProductDeal(listValFinProductDeal);
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt = (List<TrdProductDealAdd>)map.get("ValFinProductDealExt");
        result.setListValFinProductDealExt(listValFinProductDealExt);
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get("ValDealAccounting");
        result.setListValDealAccounting(listValDealAccounting);
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get("ValDealAccountingDetail");
        result.setListValDealAccountingDetail(listValDealAccountingDetail);
        //账务明细代码表
        List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get("ValBokDetailId");
        result.setListValBokDetailId(listValBokDetailId);

        //结算指令
        List<StlOrders> listValOrders = (List<StlOrders>)map.get("listValOrders");
        List<StlOrdersExt> listValOrdersExt = (List<StlOrdersExt>)map.get("listValOrdersExt");
        result.setListValOrders(listValOrders);
        result.setListValOrdersExt(listValOrdersExt);
        TrdDepoValuationDurationResult rst = new TrdDepoValuationDurationResult(result);
        return rst;
    }

}
