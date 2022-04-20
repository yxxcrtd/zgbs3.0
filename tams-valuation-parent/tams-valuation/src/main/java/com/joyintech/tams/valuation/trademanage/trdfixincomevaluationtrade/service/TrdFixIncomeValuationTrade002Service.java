/*
 * 文件名：TrdPlanRateInvestTrade001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.trdfixincomevaluationtrade.service;

import com.joyintech.calculation.util.ArithmeticUtil;
import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.PlannedAssetDealCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.mngasset.PlannedBuyTradeTemplateRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValMngBuyTrade;
import com.joyintech.tams.valuation.trademanage.trdfixincomevaluationtrade.dto.TrdFixIncomeTradeResult;
import com.joyintech.tams.valuation.trademanage.trdfixincomevaluationtrade.dto.TrdFixIncomeValuationTradeDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 计划类资产投资交易新建
 *
 * @author fanshuanghu
 * @version 1.0 2018年11月22日
 * @see TrdFixIncomeValuationTrade002Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TrdFixIncomeValuationTrade002Service extends CommonExportService<TrdFixIncomeValuationTradeDto>
        implements BaseProcessService<TrdFixIncomeValuationTradeDto> {

    @Autowired
    private PlannedBuyTradeTemplateRecService plannedBuyTradeTemplateRecService;


    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(TrdFixIncomeValuationTradeDto entity, OperationType operationType) {
        String action = StringUtil.EMPTY;
        TrdFixIncomeTradeResult trdFixIncomeTradeResult  = generateEntity(entity);
        if (OperationType.NEW.equals(operationType)) {
            action = "insert";
        }
        else {
            action = "update";
        }
        restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdFixIncomeValuationTrade/" + action, trdFixIncomeTradeResult, Integer.class);
    }

    /**
     * @Description:根据交易编号查询详情
     * @auther: yuzj
     * @date: 2022/1/7
     * @param:
     * @return:
     */
    public TrdFixIncomeValuationTradeDto queryTradeInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdFixIncomeValuationTrade/queryTradeInfoByTradeId",
                tradeId, TrdFixIncomeValuationTradeDto.class);
    }

    //封装实体类
    public TrdFixIncomeTradeResult generateEntity(TrdFixIncomeValuationTradeDto dto){
        //设置接口参数
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(dto.getProdId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        List<ValMngBuyTrade> listValMngBuyTrade = new ArrayList<ValMngBuyTrade>();
        ValMngBuyTrade valMngBuyTrade = new ValMngBuyTrade();
        BeanUtil.copyPropertiesIgnoreNull(valMngBuyTrade,dto);
        //相同字段有：交易日期、交易方向、应收利息总额、净价总额、全价总额、币种、成交编号、会计分类
        //比较界面和接口字段差异，并手动给接口字段赋值
        valMngBuyTrade.setFprice(ArithmeticUtil.doAdd(dto.getCpriceAmt(),dto.getIntAmt()));//全价=净价总额+应收利息总额
        valMngBuyTrade.setNums(dto.getCapital()); //数量
        valMngBuyTrade.setParValue(new BigDecimal(0));//单位面值
        valMngBuyTrade.setFeeAmtUnpay(new BigDecimal(0));//未随交易支付费用
        valMngBuyTrade.setYield(new BigDecimal(0));//到期收益率
        valMngBuyTrade.setCounterId(null);//必填 交易对手
        valMngBuyTrade.setCounterType(null);//必填 交易对手类型
        valMngBuyTrade.setTrader(null);//交易员
        valMngBuyTrade.setUnitInt(new BigDecimal(0));//应收利息
        valMngBuyTrade.setFpriceAmt(dto.getSettleAmt());
        valMngBuyTrade.setFee(new BigDecimal(0));//费用
        valMngBuyTrade.setDataActionSign("01");//数据动作标志
        valMngBuyTrade.setIsClean("N"); //有无清算指令

        //设置数据来源：画面录入
        valMngBuyTrade.setDataSource(DictCodeListExt1.生成方式_画面录入.getValue());

        listValMngBuyTrade.add(valMngBuyTrade);

        Map<String, Object>  map = plannedBuyTradeTemplateRecService.handleListValMngBuyTrade(listValMngBuyTrade,new Date(),params,null);
        // 定义返回变量
        PlannedAssetDealCommonResult result = new PlannedAssetDealCommonResult();
        // 判断返回结果不为空
        if (map != null) {
            result.setListValBokDetailId((List<BokDetailId>)map.get("ValBokDetailId"));
            result.setListValFinProductDeal((List<TrdProductDeal>)map.get("ValFinProductDeal"));
            result.setListValFinProductDealExt((List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));
            result.setValTradeFeeDetailList((List<TrdTradeFeeDetail>)map.get("ValTradeFeeDetail"));
            result.setValOrdersList((List<StlOrders>)map.get("ValOrders"));
            result.setValOrdersExtList((List<StlOrdersExt>)map.get("ValOrdersExt"));
            result.setListValDealAccounting((List<BokDealAccounting>)map.get("ValDealAccounting"));
            result.setListValDealAccountingDetail((List<BokDealAccountingDetail>)map.get("ValDealAccountingDetail"));
            // 添加日志
            result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        }
        TrdFixIncomeTradeResult rst = new TrdFixIncomeTradeResult(result);
        rst.setTrdFixIncomeValuationTradeDto(dto);
        return rst;

    }


}
