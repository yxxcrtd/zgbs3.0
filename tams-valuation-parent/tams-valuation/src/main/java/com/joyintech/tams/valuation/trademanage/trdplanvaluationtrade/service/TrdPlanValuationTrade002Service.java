/*
 * 文件名：TrdPlanRateInvestTrade001Service.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：fanshuanghu
 * 修改时间：2018年11月22日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.trdplanvaluationtrade.service;

import com.joyintech.framework.export.CommonExportService;
import com.joyintech.framework.grid.Page;
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.FundDealInfoForInterfRecCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.PlanAssetCurrencyDealCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.fund.FundBuyAndSellDealRecService;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.pacd.CPlannedBuyAndSellDealTemplateRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValFundTrade;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValMngMnfTade;
import com.joyintech.tams.valuation.trademanage.trdplanvaluationtrade.dto.TrdPlanTradeResult;
import com.joyintech.tams.valuation.trademanage.trdplanvaluationtrade.dto.TrdPlanValuationTradeDto;
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
 * @see TrdPlanValuationTrade002Service
 * @since 1.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TrdPlanValuationTrade002Service extends CommonExportService<TrdPlanValuationTradeDto>
        implements BaseProcessService<TrdPlanValuationTradeDto> {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired(required = false)
    private IPlatformDate iPlatformDate;

    @Autowired
    private CPlannedBuyAndSellDealTemplateRecService pacdBuyAndSellDealTemplateRecService;

    @Autowired
    private FundBuyAndSellDealRecService fundBuyAndSellDealRecService;

    /**
     * 合成初始化实体类
     *
     * @param tradeId 交易编号
     * @return 计划类资产实体类
     * @see
     * @since 1.0
     */
    public TrdPlanValuationTradeDto getInitEntity(String tradeId) {
        TrdPlanValuationTradeDto entity = new TrdPlanValuationTradeDto();
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
    public void doComplete(TrdPlanValuationTradeDto entity, OperationType operationType) {
        String action = StringUtil.EMPTY;
        TrdPlanTradeResult trdPlanTradeResult = new TrdPlanTradeResult();
        //计划类资产分为净值型和货币型  profitType:01净值型，02预期收益型,03货币型
        entity.setFinprodType("F17");
        entity.setProfitType("01");
        if(entity.getFinprodType().equals("F17") && entity.getProfitType().equals("01")){
            trdPlanTradeResult = generateNetEntity(entity);
            if (OperationType.NEW.equals(operationType)) {
                action = "insertNet";
            }
            else {
                action = "updateNet";
            }
        }else if(entity.getFinprodType().equals("F17") && entity.getProfitType().equals("03")){
            trdPlanTradeResult = generateEntity(entity);
            if (OperationType.NEW.equals(operationType)) {
                action = "insert";
            }
            else {
                action = "update";
            }
        }

        restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdPlanValuationTrade/" + action, trdPlanTradeResult, Integer.class);

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
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdPlanValuationTrade/findPortfolioId",
                params, SelectOptionDto.class);
    }

    /**
     * @Description:根据交易编号查询详情
     * @auther: yuzj
     * @date: 2022/1/7
     * @param:
     * @return:
     */
    public TrdPlanValuationTradeDto queryTradeInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdPlanValuationTrade/queryTradeInfoByTradeId",
                tradeId, TrdPlanValuationTradeDto.class);
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
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdPlanValuationTrade/findFinprodId",
                params, SelectOptionDto.class);
    }

    public List<SelectOptionDto> getTrdAcctId(Map<String, Object> param) {
        return restTemplate.postForList(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdPlanValuationTrade/getTrdAcctId", param,
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
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdPlanValuationTrade/createPlanTradeId",
                null, String.class);

    }

    //封装实体类  计划类资产货币型买入卖出交易
    public TrdPlanTradeResult generateEntity(TrdPlanValuationTradeDto trdPlanValuationTradeDto){
        //设置接口参数
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(trdPlanValuationTradeDto.getPortfolioId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        List<ValMngMnfTade> listValValMngMnfTade = new ArrayList<ValMngMnfTade>();
        ValMngMnfTade valMngMnfTade = new ValMngMnfTade();
        BeanUtil.copyPropertiesIgnoreNull(valMngMnfTade,trdPlanValuationTradeDto);
        valMngMnfTade.setProdId(trdPlanValuationTradeDto.getPortfolioId());
        valMngMnfTade.setTradeDate(trdPlanValuationTradeDto.getTradeDate());
        //比较界面和接口字段差异，并手动给接口字段赋值
        valMngMnfTade.setProdId(trdPlanValuationTradeDto.getPortfolioId());//产品代码
        valMngMnfTade.setAssetId("F17_"+trdPlanValuationTradeDto.getFinprodId());//资产代码
        valMngMnfTade.setAssetName(trdPlanValuationTradeDto.getFinprodName());   //资产名称
        valMngMnfTade.setShareAmt(trdPlanValuationTradeDto.getNums());           //交易份额
        valMngMnfTade.setIntAmt(trdPlanValuationTradeDto.getConfirmIntAmt());//应收红利-利息确认金额
        valMngMnfTade.setUnitCprice(trdPlanValuationTradeDto.getUnitValue());//单位净价
        valMngMnfTade.setDataActionSign("01");//数据动作标志
        valMngMnfTade.setCounterId("FYDLCA365001");
//        valMngMnfTade.setCounterType("01");
//        valMngMnfTade.setIsChlFeeAmt("Y");
//        valMngMnfTade.setIsClean("N");

        listValValMngMnfTade.add(valMngMnfTade);

        Map<String, Object>  map = pacdBuyAndSellDealTemplateRecService.handleValMngMnfTade(listValValMngMnfTade,new Date(),params,null);
        // 定义返回变量
        PlanAssetCurrencyDealCommonResult result = new PlanAssetCurrencyDealCommonResult();
        if (map != null) {
            // 金融产品交易
            List<TrdProductDeal> listTrdProductDeal = (List<TrdProductDeal>)map.get(
                    "TrdProductDeal");
            result.setListTrdProductDeal(listTrdProductDeal);
            // 估值核算交易扩展表
            List<TrdProductDealAdd> listTrdProductDealExt = (List<TrdProductDealAdd>)map.get(
                    "TrdProductDealExt");
            result.setListTrdProductDealExt(listTrdProductDealExt);
            // 入账信息表
            List<BokDealAccounting> listBokDealAccounting = (List<BokDealAccounting>)map.get(
                    "BokDealAccounting");
            result.setListBokDealAccounting(listBokDealAccounting);
            // 账务明细表
            List<BokDealAccountingDetail> listBokDealAccountingDetail = (List<BokDealAccountingDetail>)map.get(
                    "BokDealAccountingDetail");
            result.setListBokDealAccountingDetail(listBokDealAccountingDetail);
            // 账务明细代码表
            List<BokDetailId> listBokDetailId = (List<BokDetailId>)map.get("BokDetailId");
            result.setListBokDetailId(listBokDetailId);
            // 结算指令表
            List<StlOrders> listStlOrders = (List<StlOrders>)map.get("StlOrders");
            result.setListStlOrders(listStlOrders);
            // 结算指令扩展表
            List<StlOrdersExt> listStlOrdersExt = (List<StlOrdersExt>)map.get("StlOrdersExt");
            result.setListStlOrdersExt(listStlOrdersExt);
            // 添加日志
            result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        }
        TrdPlanTradeResult rst = new TrdPlanTradeResult(result);
        return rst;

    }

    //封装实体类
    public TrdPlanTradeResult generateNetEntity(TrdPlanValuationTradeDto trdPlanValuationTradeDto){
        //设置接口参数
        Map<String,Object> params = new HashMap<>();
        List<String> prodIds = new ArrayList<>();
        prodIds.add(trdPlanValuationTradeDto.getPortfolioId());
        params.put("INTERF_BOOKSETID",prodIds);
        params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

        List<ValFundTrade> listValFundTrade = new ArrayList<ValFundTrade>();
        ValFundTrade valFundTrade = new ValFundTrade();
        BeanUtil.copyPropertiesIgnoreNull(valFundTrade,trdPlanValuationTradeDto);
        //相同字段： 交易日、交易金额、币种
        //比较界面和接口字段差异，并手动给接口字段赋值
        valFundTrade.setProdId(trdPlanValuationTradeDto.getPortfolioId());//产品代码
        valFundTrade.setAssetId("F17_"+trdPlanValuationTradeDto.getFinprodId());//资产代码
        valFundTrade.setAssetName(trdPlanValuationTradeDto.getFinprodName()); //资产名称
        valFundTrade.setTradeMarket(null); //交易场所
        valFundTrade.setPrinAmt(new BigDecimal("0.00"));//交易本金
        valFundTrade.setShareAmt(trdPlanValuationTradeDto.getNums()); //交易份额
        valFundTrade.setUnitCprice(trdPlanValuationTradeDto.getUnitValue()); //单位净价
        valFundTrade.setIsClean("N");//有无清算指令
        valFundTrade.setDataActionSign("01");//数据动作标志

        listValFundTrade.add(valFundTrade);

        Map<String, Object>  map = fundBuyAndSellDealRecService.handleValFundTrade(listValFundTrade,new Date(),params,null);
        // 定义返回变量
        FundDealInfoForInterfRecCommonResult result = new FundDealInfoForInterfRecCommonResult();
        if (!map.isEmpty()) {
            result.setListValFinProductDeal( (List<TrdProductDeal>)map.get("ValFinProductDeal"));
            result.setListValFinProductDealExt( (List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));
            List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get("ValDealAccounting");
            result.setListValDealAccounting(listValDealAccounting);

            List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get("ValDealAccountingDetail");
            result.setListValDealAccountingDetail(listValDealAccountingDetail);

            List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get("ValBokDetailId");
            result.setListValBokDetailId(listValBokDetailId);

            result.setListValTradeFeeDetail((List<TrdTradeFeeDetail>)map.get("ValTradeFeeDetail"));

            //结算指令
            List<StlOrders> listValOrders = (List<StlOrders>)map.get("listValOrders");
            List<StlOrdersExt> listValOrdersExt = (List<StlOrdersExt>)map.get("listValOrdersExt");
            result.setListValOrders(listValOrders);
            result.setListValOrdersExt(listValOrdersExt);
            // 日志
            result.setListValInterflog((List<ValInterflog>)map.get("valInterflogList"));
        }
        TrdPlanTradeResult rst = new TrdPlanTradeResult(result,null);
        return rst;
    }


}
