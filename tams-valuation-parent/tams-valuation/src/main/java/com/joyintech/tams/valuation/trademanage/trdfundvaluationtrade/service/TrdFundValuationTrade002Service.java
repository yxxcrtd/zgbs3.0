/*
 * 文件名：TrdPlanRateInvestTrade001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.trdfundvaluationtrade.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.dao.*;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.FundDealInfoForInterfRecCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.fund.FundBuyAndSellDealRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValFundTrade;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValMngMnfTade;
import com.joyintech.tams.valuation.trademanage.trdfundvaluationtrade.dto.TrdFundTradeResult;
import com.joyintech.tams.valuation.trademanage.trdfundvaluationtrade.dto.TrdFundValuationTradeDto;
import com.joyintech.tams.valuation.trademanage.trdplanvaluationtrade.dto.TrdPlanTradeResult;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 基金交易新建
 *
 * @author fanshuanghu
 * @version 1.0 2018年11月22日
 * @see TrdFundValuationTrade002Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TrdFundValuationTrade002Service extends CommonExportService<TrdFundValuationTradeDto>
        implements BaseProcessService<TrdFundValuationTradeDto> {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired(required = false)
    private IPlatformDate iPlatformDate;
    @Autowired
    private FundBuyAndSellDealRecService fundBuyAndSellDealRecService;


    /**
     * 合成初始化实体类
     *
     * @param tradeId 交易编号
     * @return 基金实体类
     * @see
     * @since 1.0
     */
    public TrdFundValuationTradeDto getInitEntity(String tradeId) {
        TrdFundValuationTradeDto entity = new TrdFundValuationTradeDto();
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
    public void doComplete(TrdFundValuationTradeDto entity, OperationType operationType) {
        String action = StringUtil.EMPTY;
        TrdFundTradeResult trdFundTradeResult = new TrdFundTradeResult();
        trdFundTradeResult = generateEntity(entity);
        if (OperationType.NEW.equals(operationType)) {
            action = "insert";
        }
        else {
            action = "update";
        }

        restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdFundValuationTrade/" + action, trdFundTradeResult, Integer.class);
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
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdFundValuationTrade/findPortfolioId",
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
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdFundValuationTrade/findFinprodId",
                params, SelectOptionDto.class);
    }

    public List<SelectOptionDto> getTrdAcctId(Map<String, Object> param) {
        return restTemplate.postForList(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdFundValuationTrade/getTrdAcctId", param,
                SelectOptionDto.class);
    }

    /**
     * 策略代码生成
     *
     * @see
     * @since 1.0
     */
    public String createFundTradeId() {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdFundValuationTrade/createFundTradeId",
                null, String.class);

    }

    /**
     * @Description:根据交易编号查询详情
     * @auther: yuzj
     * @date: 2022/1/7
     * @param:
     * @return:
     */
    public TrdFundValuationTradeDto queryTradeInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdFundValuationTrade/queryTradeInfoByTradeId",
                tradeId, TrdFundValuationTradeDto.class);
    }

    //封装实体类
    public TrdFundTradeResult generateEntity(TrdFundValuationTradeDto trdFundValuationTradeDto){
        //设置接口参数
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(trdFundValuationTradeDto.getPortfolioId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        List<ValFundTrade> listValFundTrade = new ArrayList<ValFundTrade>();
        ValFundTrade valFundTrade = new ValFundTrade();
        BeanUtil.copyPropertiesIgnoreNull(valFundTrade,trdFundValuationTradeDto);
        valFundTrade.setTradeDate(trdFundValuationTradeDto.getTradeDate());
        //比较界面和接口字段差异，并手动给接口字段赋值
        valFundTrade.setAssetId("F06_"+trdFundValuationTradeDto.getFinprodId());//资产代码  F06基金
        valFundTrade.setDataActionSign("01");
        valFundTrade.setCounterId("FYDLCA365001");
        valFundTrade.setCounterType("01");
        valFundTrade.setIsChlFeeAmt("Y");
        valFundTrade.setIsClean("N");

        listValFundTrade.add(valFundTrade);
        Map<String, Object>  map = fundBuyAndSellDealRecService.handleValFundTrade(listValFundTrade,new Date(),params,null);
        // 定义返回变量
        FundDealInfoForInterfRecCommonResult result = new FundDealInfoForInterfRecCommonResult();
        if (!map.isEmpty()) {
            result.setListValFinProductDeal(
                    (List<TrdProductDeal>)map.get("ValFinProductDeal"));
            result.setListValFinProductDealExt(
                    (List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));
            List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get(
                    "ValDealAccounting");
            result.setListValDealAccounting(listValDealAccounting);

            List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get(
                    "ValDealAccountingDetail");
            result.setListValDealAccountingDetail(listValDealAccountingDetail);

            List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get(
                    "ValBokDetailId");
            result.setListValBokDetailId(listValBokDetailId);

            result.setListValTradeFeeDetail((List<TrdTradeFeeDetail>)map.get("ValTradeFeeDetail"));

            //结算指令
            List<StlOrders> listValOrders = (List<StlOrders>)map.get(
                    "listValOrders");
            List<StlOrdersExt> listValOrdersExt = (List<StlOrdersExt>)map.get(
                    "listValOrdersExt");
            result.setListValOrders(listValOrders);
            result.setListValOrdersExt(listValOrdersExt);
            // 日志
            result.setListValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        }
        TrdFundTradeResult rst = new TrdFundTradeResult(result);
        return rst;

    }


}
