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

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.PlanAssetCurrencyDealCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.PlannedAssetDealCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.mngasset.PlannedBuyTradeTemplateRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValMngBuyTrade;
import com.joyintech.tams.valuation.trademanage.trdfixincomevaluationtrade.dto.TrdFixIncomeTradeResult;
import com.joyintech.tams.valuation.trademanage.trdfixincomevaluationtrade.dto.TrdFixIncomeValuationTradeDto;
import com.joyintech.tams.valuation.trademanage.trdplanvaluationtrade.dto.TrdPlanTradeResult;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    @Autowired
    private PlannedBuyTradeTemplateRecService plannedBuyTradeTemplateRecService;


    /**
     * 合成初始化实体类
     *
     * @param tradeId 交易编号
     * @return 计划类资产实体类
     * @see
     * @since 1.0
     */
    public TrdFixIncomeValuationTradeDto getInitEntity(String tradeId) {
        TrdFixIncomeValuationTradeDto entity = new TrdFixIncomeValuationTradeDto();
        TrdProductDeal trdProductDeal = null;
        TrdProductDealAdd trdProductDealAdd = null;
        String finprodId = null;
        FinProduct finProduct = null;
        return entity;
    }

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
     * 产品代码下拉框
     * @auther: Administrator
     * @date: 2021/12/29 17:10
     * @param:
     * @return:
     */
    public Page<SelectOptionDto> findPortfolioId(Map<String, Object> params) {
        return restTemplate.postForPage(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdFixIncomeValuationTrade/findPortfolioId",
                params, SelectOptionDto.class);
    }

    /**
     * 资产代码下拉框
     * @auther: Administrator
     * @date: 2021/12/29 17:10
     * @param:
     * @return:
     */
    public Page<SelectOptionDto> findFinprodId(Map<String, Object> params) {
        return restTemplate.postForPage(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdFixIncomeValuationTrade/findFinprodId",
                params, SelectOptionDto.class);
    }

    public List<SelectOptionDto> getTrdAcctId(Map<String, Object> param) {
        return restTemplate.postForList(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdFixIncomeValuationTrade/getTrdAcctId", param,
                SelectOptionDto.class);
    }

    /**
     * 策略代码生成
     *
     * @see
     * @since 1.0
     */
    public String createPlanTradeId() {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdFixIncomeValuationTrade/createPlanTradeId",
                null, String.class);

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
    public TrdFixIncomeTradeResult generateEntity(TrdFixIncomeValuationTradeDto trdFixIncomeValuationTradeDto){
        //设置接口参数
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(trdFixIncomeValuationTradeDto.getPortfolioId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        List<ValMngBuyTrade> listValMngBuyTrade = new ArrayList<ValMngBuyTrade>();
        ValMngBuyTrade valMngBuyTrade = new ValMngBuyTrade();
        BeanUtil.copyPropertiesIgnoreNull(valMngBuyTrade,trdFixIncomeValuationTradeDto);
        //相同字段有：交易日期、交易方向、应收利息总额、净价总额、全价总额、币种、成交编号、会计分类
        valMngBuyTrade.setProdId(trdFixIncomeValuationTradeDto.getPortfolioId());//prodId 产品代码
        //比较界面和接口字段差异，并手动给接口字段赋值
        valMngBuyTrade.setAssetId("F09_"+trdFixIncomeValuationTradeDto.getFinprodId());//资产代码
        valMngBuyTrade.setAssetName(trdFixIncomeValuationTradeDto.getFinprodName()); //资产名称
        valMngBuyTrade.setEffDate(trdFixIncomeValuationTradeDto.getSettleDate());//交割日
        valMngBuyTrade.setCapital(trdFixIncomeValuationTradeDto.getTradeAmt());//本金金额
        valMngBuyTrade.setNums(new BigDecimal("1.00")); //数量
        valMngBuyTrade.setParValue(new BigDecimal("100.00"));//单位面值
        valMngBuyTrade.setFeeAmtUnpay(new BigDecimal("1.00"));//未随交易支付费用
        valMngBuyTrade.setYield(new BigDecimal("0.05"));//到期收益率
        valMngBuyTrade.setCounterId("FYDLCA365001");//必填 交易对手
        valMngBuyTrade.setCounterType("01");//必填 交易对手类型
        valMngBuyTrade.setTrader("system");//交易员
        valMngBuyTrade.setChlId("2214312412");//非必填 通道代码
        valMngBuyTrade.setCprice(trdFixIncomeValuationTradeDto.getCpriceAmt());//净价
        valMngBuyTrade.setFprice(new BigDecimal("1.00"));//全价
        valMngBuyTrade.setUnitInt(new BigDecimal("1.00"));//应收利息
        valMngBuyTrade.setFee(new BigDecimal("1.00"));//费用
        valMngBuyTrade.setDataActionSign("01");//数据动作标志
        valMngBuyTrade.setIsClean("N"); //有无清算指令

        valMngBuyTrade.setIsChlFeeAmt("Y");//非必填 通道费是否随交易支付
        valMngBuyTrade.setChlFeeAmt(new BigDecimal("1.00"));//非必填 通道费

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
        return rst;

    }


}
