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
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.FundDealInfoForInterfRecCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.fund.*;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.*;
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

import java.math.BigDecimal;
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
    @Autowired
    private FundSubscribeApplyDealRecService fundSubscribeApplyDealRecService;
    @Autowired
    private FundSubscribeConfirmDealRecService fundSubscribeConfirmDealRecService;
    @Autowired
    private FundMoneyFundRedeemConfirmDealRecService fundMoneyFundRedeemConfirmDealRecService;
    @Autowired
    private FundRedeemArrivalAccountDealRecService fundRedeemArrivalAccountDealRecService;


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
        TrdFundTradeResult trdFundTradeResult = new TrdFundTradeResult();
        if (StringUtil.isNotEmpty(trdFundValuationTradeDto.getPs())) {
            if (trdFundValuationTradeDto.getPs().equals(DictCodeList.交易方向_买入.getValue())
                    || trdFundValuationTradeDto.getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
                //买入、卖出 ==>对应接口【基金&计划类买入卖出交易】
                //设置接口参数
                Map<String,Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdFundValuationTradeDto.getPortfolioId());
                params.put("INTERF_BOOKSETID",prodIds);
                params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

                List<ValFundTrade> listValFundTrade = new ArrayList<ValFundTrade>();
                ValFundTrade valFundTrade = new ValFundTrade();
                BeanUtil.copyPropertiesIgnoreNull(valFundTrade,trdFundValuationTradeDto);
                //相同字段名： 成交编号、交易方向、交易场所、交易日、交易金额、币种
                valFundTrade.setProdId(trdFundValuationTradeDto.getPortfolioId());//产品代码
                //比较界面和接口字段差异，并手动给接口字段赋值
                valFundTrade.setAssetId("F06_"+trdFundValuationTradeDto.getFinprodId());//资产代码  F06基金
                valFundTrade.setAssetName(trdFundValuationTradeDto.getFinprodName());//资产名称
                valFundTrade.setBusiType("06");//业务类型
                valFundTrade.setDataActionSign("01");//数据动作标志
                valFundTrade.setPrinAmt(new BigDecimal("0.00"));//交易本金
                valFundTrade.setShareAmt(trdFundValuationTradeDto.getNums());//交易份额
                valFundTrade.setIntAmt(trdFundValuationTradeDto.getReceivableBonus());//应收红利
                valFundTrade.setUnitCprice(trdFundValuationTradeDto.getUnitValue());//单位净值
                valFundTrade.setIsClean("N"); //有无清算指令

                valFundTrade.setCounterId("FYDLCA365001");//非必填 交易对手
                valFundTrade.setCounterType("01");//非必填 交易对手类型
                valFundTrade.setIsChlFeeAmt("Y");//非必填 通道费是否随交易支付


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
            } else if (trdFundValuationTradeDto.getPs().equals(DictCodeList.交易方向_申购.getValue())
                || trdFundValuationTradeDto.getPs().equals(DictCodeList.交易方向_认购.getValue())) {
                //申购、认购 ==>对应接口【基金&计划类认申购申请交易】
                //设置接口参数
                Map<String,Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdFundValuationTradeDto.getPortfolioId());
                params.put("INTERF_BOOKSETID",prodIds);
                params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

                List<ValApplyPurTade> listValApplyPurTade = new ArrayList<ValApplyPurTade>();
                ValApplyPurTade valApplyPurTade = new ValApplyPurTade();
                BeanUtil.copyPropertiesIgnoreNull(valApplyPurTade,trdFundValuationTradeDto);
                //相同字段名： 成交编号、交易方向、交易场所、交易日、交易金额、币种
                valApplyPurTade.setProdId(trdFundValuationTradeDto.getPortfolioId());//产品代码
                //比较界面和接口字段差异，并手动给接口字段赋值
                valApplyPurTade.setAssetId("F06_"+trdFundValuationTradeDto.getFinprodId());//资产代码  F06基金
                valApplyPurTade.setAssetName(trdFundValuationTradeDto.getFinprodName());//资产名称
                valApplyPurTade.setBusiType("06");//业务类型
                valApplyPurTade.setDataActionSign("01");//数据动作标志
                valApplyPurTade.setIsClean("N"); //有无清算指令

                valApplyPurTade.setCounterId("FYDLCA365001");//非必填 交易对手
                valApplyPurTade.setCounterType("01");//非必填 交易对手类型


                listValApplyPurTade.add(valApplyPurTade);
                Map<String, Object>  map = fundSubscribeApplyDealRecService.handleValApplyPurTade(listValApplyPurTade,new Date(),params,null);
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
            }else if(trdFundValuationTradeDto.getPs().equals(DictCodeList.交易方向_申购确认.getValue())
                    || trdFundValuationTradeDto.getPs().equals(DictCodeList.交易方向_认购确认.getValue()) ) {
                //申购确认、认购确认 ==>对应接口【基金&计划类认申购确认交易】
                //设置接口参数
                Map<String,Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdFundValuationTradeDto.getPortfolioId());
                params.put("INTERF_BOOKSETID",prodIds);
                params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

                List<ValAffirmPurTrade> listValAffirmPurTrade = new ArrayList<ValAffirmPurTrade>();
                ValAffirmPurTrade valAffirmPurTrade = new ValAffirmPurTrade();
                BeanUtil.copyPropertiesIgnoreNull(valAffirmPurTrade,trdFundValuationTradeDto);
                //相同字段名： 成交编号、交易方向、交易场所、交易日、交易金额、币种
                valAffirmPurTrade.setProdId(trdFundValuationTradeDto.getPortfolioId());//产品代码
                //比较界面和接口字段差异，并手动给接口字段赋值
                valAffirmPurTrade.setAssetId("F06_"+trdFundValuationTradeDto.getFinprodId());//资产代码  F06基金
                valAffirmPurTrade.setAssetName(trdFundValuationTradeDto.getFinprodName());//资产名称
                valAffirmPurTrade.setDataActionSign("01");//数据动作标志
                valAffirmPurTrade.setIsClean("N"); //有无清算指令

                listValAffirmPurTrade.add(valAffirmPurTrade);
                Map<String, Object>  map = fundSubscribeConfirmDealRecService.handleValApplyPurTade(listValAffirmPurTrade,new Date(),params,null);
                // 定义返回变量
                FundDealInfoForInterfRecCommonResult result = new FundDealInfoForInterfRecCommonResult();
                if (!map.isEmpty()) {
                    result.setListValFinProductDeal(
                            (List<TrdProductDeal>)map.get("ValFinProductDeal"));

                    result.setListValFinProductDealExt(
                            (List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));

                    result.setListValTradeFeeDetail((List<TrdTradeFeeDetail>)map.get("ValTradeFeeDetail"));

                    result.setListValOrders( (List<StlOrders>)map.get(
                            "listValOrders"));

                    result.setListValOrdersExt((List<StlOrdersExt>)map.get(
                            "listValOrdersExt"));

                    result.setListValDealAccounting((List<BokDealAccounting>)map.get(
                            "ValDealAccounting"));

                    result.setListValDealAccountingDetail((List<BokDealAccountingDetail>)map.get(
                            "ValDealAccountingDetail"));

                    result.setListValBokDetailId((List<BokDetailId>)map.get(
                            "ValBokDetailId"));

                    // 日志
                    result.setListValInterflog((List<ValInterflog>)map.get("valInterflogList"));
                }
                TrdFundTradeResult rst = new TrdFundTradeResult(result);
                return rst;
            }else if(trdFundValuationTradeDto.getPs().equals(DictCodeList.交易方向_赎回确认.getValue()) ) {
                //赎回确认 ==>对应接口【货币基金赎回确认交易】
                //设置接口参数
                Map<String,Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdFundValuationTradeDto.getPortfolioId());
                params.put("INTERF_BOOKSETID",prodIds);
                params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

                List<ValFundMnfRedAffirm> listValFundMnfRedAffirm = new ArrayList<ValFundMnfRedAffirm>();
                ValFundMnfRedAffirm valFundMnfRedAffirm = new ValFundMnfRedAffirm();
                BeanUtil.copyPropertiesIgnoreNull(valFundMnfRedAffirm,trdFundValuationTradeDto);
                //相同字段名： 成交编号、交易方向、交易场所、交易日、交易金额、币种
                valFundMnfRedAffirm.setProdId(trdFundValuationTradeDto.getPortfolioId());//产品代码
                //比较界面和接口字段差异，并手动给接口字段赋值
                valFundMnfRedAffirm.setAssetId("F06_"+trdFundValuationTradeDto.getFinprodId());//资产代码  F06基金
                valFundMnfRedAffirm.setAssetName(trdFundValuationTradeDto.getFinprodName());//资产名称
                valFundMnfRedAffirm.setDataActionSign("01");//数据动作标志
                valFundMnfRedAffirm.setIsClean("N"); //有无清算指令

                valFundMnfRedAffirm.setIsChlFeeAmt("Y");//非必填 通道费是否随交易支付


                listValFundMnfRedAffirm.add(valFundMnfRedAffirm);
                Map<String, Object>  map = fundMoneyFundRedeemConfirmDealRecService.handleValFundMnfRedAffirm(listValFundMnfRedAffirm,new Date(),params,null);
                // 定义返回变量
                FundDealInfoForInterfRecCommonResult result = new FundDealInfoForInterfRecCommonResult();
                if (!map.isEmpty()) {
                    result.setListValFinProductDeal(
                            (List<TrdProductDeal>)map.get("ValFinProductDeal"));
                    result.setListValFinProductDealExt(
                            (List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));

                    result.setListValTradeFeeDetail((List<TrdTradeFeeDetail>)map.get("ValTradeFeeDetail"));

                    //结算指令
                    List<StlOrders> listValOrders = (List<StlOrders>)map.get(
                            "listValOrders");
                    List<StlOrdersExt> listValOrdersExt = (List<StlOrdersExt>)map.get(
                            "listValOrdersExt");
                    result.setListValOrders(listValOrders);
                    result.setListValOrdersExt(listValOrdersExt);

                    result.setListValDealAccounting((List<BokDealAccounting>)map.get(
                            "ValDealAccounting"));

                    result.setListValDealAccountingDetail((List<BokDealAccountingDetail>)map.get(
                            "ValDealAccountingDetail"));

                    result.setListValBokDetailId((List<BokDetailId>)map.get(
                            "ValBokDetailId"));

                    // 日志
                    result.setListValInterflog((List<ValInterflog>)map.get("valInterflogList"));
                }
                TrdFundTradeResult rst = new TrdFundTradeResult(result);
                return rst;
            }else if(trdFundValuationTradeDto.getPs().equals(DictCodeList.交易方向_赎回到账.getValue()) ) {
                //赎回到账 ==>对应接口【基金&计划类赎回到账交易】
                //设置接口参数
                Map<String,Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdFundValuationTradeDto.getPortfolioId());
                params.put("INTERF_BOOKSETID",prodIds);
                params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

                List<ValRedAccoutTrade> listValRedAccoutTrade = new ArrayList<ValRedAccoutTrade>();
                ValRedAccoutTrade valRedAccoutTrade = new ValRedAccoutTrade();
                BeanUtil.copyPropertiesIgnoreNull(valRedAccoutTrade,trdFundValuationTradeDto);
                //相同字段名： 成交编号、交易方向、交易场所、交易日、交易金额、币种
                valRedAccoutTrade.setProdId(trdFundValuationTradeDto.getPortfolioId());//产品代码
                //比较界面和接口字段差异，并手动给接口字段赋值
                valRedAccoutTrade.setAssetId("F06_"+trdFundValuationTradeDto.getFinprodId());//资产代码  F06基金
                valRedAccoutTrade.setDataActionSign("01");//数据动作标志
                valRedAccoutTrade.setIsClean("N"); //有无清算指令

                listValRedAccoutTrade.add(valRedAccoutTrade);
                Map<String, Object>  map = fundRedeemArrivalAccountDealRecService.handleValRedAccoutTrade(listValRedAccoutTrade,new Date(),params,null);
                // 定义返回变量
                FundDealInfoForInterfRecCommonResult result = new FundDealInfoForInterfRecCommonResult();
                if (!map.isEmpty()) {
                    result.setListValFinProductDeal(
                            (List<TrdProductDeal>)map.get("ValFinProductDeal"));
                    result.setListValFinProductDealExt(
                            (List<TrdProductDealAdd>)map.get("ValFinProductDealExt"));
                    // 入账信息表List
                    List<BokDealAccounting> listValDealAccounting = (List<BokDealAccounting>)map.get(
                            "ValDealAccounting");
                    result.setListValDealAccounting(listValDealAccounting);

                    // 入账明细信息表List
                    List<BokDealAccountingDetail> listValDealAccountingDetail = (List<BokDealAccountingDetail>)map.get(
                            "ValDealAccountingDetail");
                    result.setListValDealAccountingDetail(listValDealAccountingDetail);

                    // 账务明细代码表List
                    List<BokDetailId> listValBokDetailId = (List<BokDetailId>)map.get(
                            "ValBokDetailId");
                    result.setListValBokDetailId(listValBokDetailId);

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
            }else{
                //返回初始化
                return trdFundTradeResult;
            }
        }else{
            //返回初始化
            return trdFundTradeResult;
        }
    }


}
