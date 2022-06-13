package com.joyintech.tams.valuation.trademanage.trdsecovertransfervaluationduration.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.AssetDealInfoValCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.sec.SecReceicableTradeTemplateRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSecReceicableTrade;
import com.joyintech.tams.valuation.trademanage.trdsecovertransfervaluationduration.dto.TrdSecOverTransferValuationDuration002Dto;
import com.joyintech.tams.valuation.trademanage.trdsecovertransfervaluationduration.dto.TrdSecOverTransferValuationDurationResult;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TrdSecOverTransferValuationDuration002Service extends CommonExportService<TrdSecOverTransferValuationDuration002Dto> implements BaseProcessService<TrdSecOverTransferValuationDuration002Dto> {

    @Autowired
    private SecReceicableTradeTemplateRecService secReceicableTradeTemplateRecService;

    /**
     * 通过交易编号查询交易信息（修改）
     *
     * @return 产品代码列表
     * @see
     * @since 1.0
     */
    public TrdSecOverTransferValuationDuration002Dto queryTradeInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdSecOverTransferValuationDuration002/queryTradeInfoByTradeId", tradeId,
                TrdSecOverTransferValuationDuration002Dto.class);
    }

    //自增序列号
    public int querySequences(){
        return restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdSecOverTransferValuationDuration002/querySequences", null,Integer.class);
    }


    @Override
    public void doComplete(TrdSecOverTransferValuationDuration002Dto trdSecOverTransferValuationDuration002Dto, OperationType operationType) {
        TrdSecOverTransferValuationDurationResult result = generateEntity(trdSecOverTransferValuationDuration002Dto);
        String action = StringUtil.EMPTY;
        if (OperationType.NEW.equals(operationType)) {
            action = "insert";
        }
        else {
            action = "update";
        }
        restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdSecOverTransferValuationDuration002/" + action, result,
                Integer.class);

    }

    public TrdSecOverTransferValuationDurationResult generateEntity(TrdSecOverTransferValuationDuration002Dto dto){
        //设置接口参数
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(dto.getProdId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        // 定义返回变量
        AssetDealInfoValCommonResult result = new AssetDealInfoValCommonResult();

        List<ValSecReceicableTrade> listValSecReceicableTrade = new ArrayList<>();
        ValSecReceicableTrade entity = new ValSecReceicableTrade();
        BeanUtil.copyPropertiesIgnoreNull(entity,dto);
        //比较界面和接口字段差异，并手动给接口字段赋值
        entity.setDataActionSign("01");//数据动作标志
        entity.setVdate(dto.getTradeDate());//起息日
        entity.setMdate(dto.getTradeDate());//到期日
        entity.setFtradeId("");//原交易编号
        entity.setIsClean2("N");//是否结清
        entity.setIsClean("N");//有无清算指令
        entity.setInvestAim("");//投资目的

        listValSecReceicableTrade.add(entity);
        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
        Map<String, Object> map = secReceicableTradeTemplateRecService.handleValSecReceicableTrade(listValSecReceicableTrade,new Date(),params,null);

        // 金融产品交易List
        List<TrdProductDeal> listValFinProductDeal = (List<TrdProductDeal>)map.get(
                "ValFinProductDeal");
        result.setListValFinProductDeal(listValFinProductDeal);
        // 估值核算交易扩展表List
        List<TrdProductDealAdd> listValFinProductDealExt = (List<TrdProductDealAdd>)map.get(
                "ValFinProductDealExt");
        result.setListValFinProductDealExt(listValFinProductDealExt);
        //交易类金融产品表List
        List<FinTradeProduct> listValFinTradeProduct = (List<FinTradeProduct>)map.get(
                "ValFinTradeProduct");
        result.setListValFinTradeProduct(listValFinTradeProduct);
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
        // 添加日志
        result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));

        TrdSecOverTransferValuationDurationResult rst = new TrdSecOverTransferValuationDurationResult(result);
        rst.setTrdSecOverTransferValuationDuration002Dto(dto);
        return rst;
     }

}
