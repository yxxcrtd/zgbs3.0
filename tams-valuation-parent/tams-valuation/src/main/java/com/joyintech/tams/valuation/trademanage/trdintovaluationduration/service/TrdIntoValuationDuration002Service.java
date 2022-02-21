/*
 * 文件名：TrdLendInvestTrade002Service.java 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved. 描述：理财资管管理系统V2.0 修改人：杨智 修改时间：2018年10月10日
 * 修改内容：新建 系统名称：理财资管管理系统V2.0
 */
package com.joyintech.tams.valuation.trademanage.trdintovaluationduration.service;

import com.joyintech.framework.base.BaseService;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.AssetDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.secdl.SecPeriodDealTemplateRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSecVestPeriod;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSeclendAssetPeriodDeal;
import com.joyintech.tams.valuation.trademanage.trdintovaluationduration.dto.TrdIntoValuationDuration001Dto;
import com.joyintech.tams.valuation.trademanage.trdintovaluationduration.dto.TrdIntoValuationDurationResult;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class TrdIntoValuationDuration002Service extends BaseService<TrdIntoValuationDuration001Dto> implements BaseProcessService<TrdIntoValuationDuration001Dto> {

    @Autowired
    private SecPeriodDealTemplateRecService secPeriodDealTemplateRecService;

    /**
     * 根据交易编号查询详情
     */
    public TrdIntoValuationDuration001Dto queryTradeInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdIntoValuationDuration001/queryTradeInfoByTradeId",
                tradeId, TrdIntoValuationDuration001Dto.class);
    }

    /**
     * 策略代码生成
     *
     * @see
     * @since 1.0
     */
    public String createPlanTradeId() {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdIntoValuationDuration001/createPlanTradeId",
                null, String.class);

    }
    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(TrdIntoValuationDuration001Dto entity, OperationType operationType) {
        String action = StringUtil.EMPTY;
        TrdIntoValuationDurationResult trdIntoValuationDurationResult = new TrdIntoValuationDurationResult();
        trdIntoValuationDurationResult = generateEntity(entity);
        if (OperationType.NEW.equals(operationType)) {
            action = "insert";
        }
        else {
            action = "update";
        }

        restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdIntoValuationDuration001/" + action, trdIntoValuationDurationResult, Integer.class);
    }

    //封装实体类
    public TrdIntoValuationDurationResult generateEntity(TrdIntoValuationDuration001Dto dto){
        //设置接口参数
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(dto.getPortfolioId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        List<ValSeclendAssetPeriodDeal> listValSeclendAssetPeriodDeal = new ArrayList<>();
        ValSeclendAssetPeriodDeal entity = new ValSeclendAssetPeriodDeal();
        BeanUtil.copyPropertiesIgnoreNull(entity,dto);
//        entity.setFundSettleDate(DateUtil.parse(dto.getFundSettleDate()));
        //比较界面和接口字段差异，并手动给接口字段赋值
        entity.setProdId(dto.getPortfolioId()); //产品代码
        entity.setSecId("F01_"+dto.getFinprodMarketId()); //金融产品代码
        entity.setFinprodId("F01_"+dto.getFinprodMarketId()); //金融产品代码=资产代码
        entity.setFinprodType("F04"); //金融产品类型
        entity.setDataActionSign("01");//数据动作标志
        entity.setPs(dto.getPs());//交易方向
        entity.setIsAmt("Y"); //是否还本带息
        entity.setActInt(dto.getActInt()); //实收利息
        entity.setActCapi(dto.getActCapi()); //实收本金
        entity.setTardeAmt(dto.getActInt().add(dto.getActCapi())); //交易金额=实收本金+实收利息
        entity.setVdate(dto.getVdate()); //计息开始日
        entity.setMdate(dto.getMdate()); //计息结束日
        entity.setTradeDate(dto.getTradeDate());//交易日
        entity.setSettleDate(dto.getSettleDate()); //交割日
        entity.setIsClean2("Y");//是否结清
        entity.setCcy(dto.getCcy());//币种
        entity.setTradeId(dto.getTradeId());  //交易编号
        entity.setIsClean("Y");//有无清算指令
        listValSeclendAssetPeriodDeal.add(entity);
        Map<String, Object> map = secPeriodDealTemplateRecService.handleValSeclendDeal(listValSeclendAssetPeriodDeal,new Date(),params,null);
        //定义返回变量
        AssetDealInfoValCommonResult result = new AssetDealInfoValCommonResult();
        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal=(List<TrdProductDeal>)map.get("listValFinProductDeal");
        result.setListValFinProductDeal(listValFinProductDeal);
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt=(List<TrdProductDealAdd>)map.get("listValFinProductDealExt");
        result.setListValFinProductDealExt(listValFinProductDealExt);
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting=(List<BokDealAccounting>)map.get("listValDealAccounting");
        result.setListValDealAccounting(listValDealAccounting);
        //账务明细list
        List<BokDealAccountingDetail> listValDealAccountingDetail=(List<BokDealAccountingDetail>)map.get("listValDealAccountingDetail");
        result.setListValDealAccountingDetail(listValDealAccountingDetail);
        //账务明细代码表
        List<BokDetailId> listValBokDetailId=(List<BokDetailId>)map.get("listValBokDetailId");
        result.setListValBokDetailId(listValBokDetailId);
        // 添加日志
        //result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        //结算指令
        List<StlOrders> valOrdersList=(List<StlOrders>)map.get("listValOrders");
        result.setListValOrders(valOrdersList);
        //结算指令拓展表list
        List<StlOrdersExt> valOrdersExtList=(List<StlOrdersExt>)map.get("listValOrdersExt");
        result.setListValOrdersExt(valOrdersExtList);

        TrdIntoValuationDurationResult rst = new TrdIntoValuationDurationResult(result);
        rst.setTrdIntoValuationDuration001Dto(dto);
        return rst;
    }

}
