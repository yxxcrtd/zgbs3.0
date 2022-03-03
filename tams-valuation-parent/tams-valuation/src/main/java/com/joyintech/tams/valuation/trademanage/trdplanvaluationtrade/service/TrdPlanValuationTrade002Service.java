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
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.systemtools.interf.IPlatformDate;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.PlanAssetCurrencyDealCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.fund.FundBuyAndSellDealRecService;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.pacd.CPlannedBuyAndSellDealTemplateRecService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValMngMnfTade;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.pacd.*;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.*;
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
    private CPlannedApplyPurDealTemplateRecService pacdApplyPurDealTemplateRecService;
    @Autowired
    private CPlannedAffirmPurDealTemplateRecService cPlannedAffirmPurDealTemplateRecService;

    @Autowired
    private FundBuyAndSellDealRecService fundBuyAndSellDealRecService;
    @Autowired
    private CPlannedRedAffirmDealTemplateRecService cPlannedRedAffirmDealTemplateRecService;
    @Autowired
    private CPlannedRedAccoutDealTemplateRecService cPlannedRedAccoutDealTemplateRecService;

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

        //20220224改为根据交易类型判断
        trdPlanTradeResult = generateEntity(entity);
        if (OperationType.NEW.equals(operationType)) {
            action = "insert";
        }
        else {
            action = "update";
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
        TrdPlanTradeResult trdPlanTradeResult = new TrdPlanTradeResult();
        if(StringUtil.isNotEmpty(trdPlanValuationTradeDto.getPs())){
            if (trdPlanValuationTradeDto.getPs().equals(DictCodeList.交易方向_买入.getValue())
                || trdPlanValuationTradeDto.getPs().equals(DictCodeList.交易方向_卖出.getValue())) {
                    //设置接口参数
                    Map<String,Object> params = new HashMap<>();
                    List<String> prodIds = new ArrayList<>();
                    prodIds.add(trdPlanValuationTradeDto.getPortfolioId());
                    params.put("INTERF_BOOKSETID",prodIds);
                    params.put("VAL_MODEL","INTERF_MODEL_SINGLE");

                    List<ValMngMnfTade> listValValMngMnfTade = new ArrayList<ValMngMnfTade>();
                    ValMngMnfTade valMngMnfTade = new ValMngMnfTade();
                    //实体类字段映射处理
                    valMngMnfTade = dealBean(valMngMnfTade,trdPlanValuationTradeDto);
                    listValValMngMnfTade.add(valMngMnfTade);

                    Map<String, Object> map = pacdBuyAndSellDealTemplateRecService.handleValMngMnfTade(listValValMngMnfTade, new Date(), params, null);
                    // 定义返回变量
                    PlanAssetCurrencyDealCommonResult result = new PlanAssetCurrencyDealCommonResult();
                    if (map != null) {
                        // 金融产品交易
                        List<TrdProductDeal> listTrdProductDeal = (List<TrdProductDeal>) map.get(
                                "TrdProductDeal");
                        result.setListTrdProductDeal(listTrdProductDeal);
                        // 估值核算交易扩展表
                        List<TrdProductDealAdd> listTrdProductDealExt = (List<TrdProductDealAdd>) map.get(
                                "TrdProductDealExt");
                        result.setListTrdProductDealExt(listTrdProductDealExt);
                        // 入账信息表
                        List<BokDealAccounting> listBokDealAccounting = (List<BokDealAccounting>) map.get(
                                "BokDealAccounting");
                        result.setListBokDealAccounting(listBokDealAccounting);
                        // 账务明细表
                        List<BokDealAccountingDetail> listBokDealAccountingDetail = (List<BokDealAccountingDetail>) map.get(
                                "BokDealAccountingDetail");
                        result.setListBokDealAccountingDetail(listBokDealAccountingDetail);
                        // 账务明细代码表
                        List<BokDetailId> listBokDetailId = (List<BokDetailId>) map.get("BokDetailId");
                        result.setListBokDetailId(listBokDetailId);
                        // 结算指令表
                        List<StlOrders> listStlOrders = (List<StlOrders>) map.get("StlOrders");
                        result.setListStlOrders(listStlOrders);
                        // 结算指令扩展表
                        List<StlOrdersExt> listStlOrdersExt = (List<StlOrdersExt>) map.get("StlOrdersExt");
                        result.setListStlOrdersExt(listStlOrdersExt);
                        // 添加日志
                        result.setValInterflog((List<ValInterflog>) map.get("valInterflogList"));
                    }
                    TrdPlanTradeResult rst = new TrdPlanTradeResult(result);
                    return rst;
            } else if (trdPlanValuationTradeDto.getPs().equals(DictCodeList.交易方向_申购.getValue())
                    || trdPlanValuationTradeDto.getPs().equals(DictCodeList.交易方向_认购.getValue())) {
                //申购、认购 ==>对应接口【计划类资产(货币型)申购申请交易】
                //设置接口参数
                Map<String, Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdPlanValuationTradeDto.getPortfolioId());
                params.put("INTERF_BOOKSETID", prodIds);
                params.put("VAL_MODEL", "INTERF_MODEL_SINGLE");

                List<ValMngMnfApplyPurTade> listValMngMnfApplyPurTade = new ArrayList<ValMngMnfApplyPurTade>();
                ValMngMnfApplyPurTade valMngMnfApplyPurTade = new ValMngMnfApplyPurTade();
                //实体类字段映射处理
                valMngMnfApplyPurTade = dealBean(valMngMnfApplyPurTade,trdPlanValuationTradeDto);

                listValMngMnfApplyPurTade.add(valMngMnfApplyPurTade);
                Map<String, Object> map = pacdApplyPurDealTemplateRecService.handleValMngMnfApplyPurTade(listValMngMnfApplyPurTade, new Date(), params, null);
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
            } else if(trdPlanValuationTradeDto.getPs().equals(DictCodeList.交易方向_申购确认.getValue())
                    || trdPlanValuationTradeDto.getPs().equals(DictCodeList.交易方向_认购确认.getValue()) ){
                //申购确认、认购确认 ==>对应接口【计划类资产(货币型)申购确认交易】
                //设置接口参数
                Map<String, Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdPlanValuationTradeDto.getPortfolioId());
                params.put("INTERF_BOOKSETID", prodIds);
                params.put("VAL_MODEL", "INTERF_MODEL_SINGLE");

                List<ValMngMnfAffirmPurTrade> listValMngMnfAffirmPurTrade = new ArrayList<ValMngMnfAffirmPurTrade>();
                ValMngMnfAffirmPurTrade valMngMnfAffirmPurTrade = new ValMngMnfAffirmPurTrade();
                //实体类字段映射处理
                valMngMnfAffirmPurTrade = dealBean(valMngMnfAffirmPurTrade,trdPlanValuationTradeDto);

                listValMngMnfAffirmPurTrade.add(valMngMnfAffirmPurTrade);
                Map<String, Object> map = cPlannedAffirmPurDealTemplateRecService.handleValMngMnfAffirmPurTrade(listValMngMnfAffirmPurTrade, new Date(), params, null);
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

                    /**add by wujie on 20210501 START**/
                    result.setListValTradeFeeDetail((List<TrdTradeFeeDetail>)map.get("ValTradeFeeDetail"));
                    /**add by wujie on 20210501 END**/

                    // 添加日志
                    result.setValInterflog((List<ValInterflog>)map.get("valInterflogList"));
                }
                TrdPlanTradeResult rst = new TrdPlanTradeResult(result);
                return rst;
            }else if(trdPlanValuationTradeDto.getPs().equals(DictCodeList.交易方向_赎回确认.getValue()) ){
                //赎回确认 ==>对应接口【计划类资产(货币型)赎回确认交易】
                //设置接口参数
                Map<String, Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdPlanValuationTradeDto.getPortfolioId());
                params.put("INTERF_BOOKSETID", prodIds);
                params.put("VAL_MODEL", "INTERF_MODEL_SINGLE");

                List<ValMngMnfRedAffirm> listValMngMnfRedAffirm = new ArrayList<ValMngMnfRedAffirm>();
                ValMngMnfRedAffirm valMngMnfRedAffirm = new ValMngMnfRedAffirm();
                //实体类字段映射处理
                valMngMnfRedAffirm = dealBean(valMngMnfRedAffirm,trdPlanValuationTradeDto);

                listValMngMnfRedAffirm.add(valMngMnfRedAffirm);
                Map<String, Object> map = cPlannedRedAffirmDealTemplateRecService.handleValMngMnfRedAffirm(listValMngMnfRedAffirm, new Date(), params, null);
                // 定义返回变量
                PlanAssetCurrencyDealCommonResult result = new PlanAssetCurrencyDealCommonResult();
                if (map != null) {
                    // 金融产品交易
                    List<TrdProductDeal> listTrdProductDeal = (List<TrdProductDeal>) map.get(
                            "TrdProductDeal");
                    result.setListTrdProductDeal(listTrdProductDeal);
                    // 估值核算交易扩展表
                    List<TrdProductDealAdd> listTrdProductDealExt = (List<TrdProductDealAdd>) map.get(
                            "TrdProductDealExt");
                    result.setListTrdProductDealExt(listTrdProductDealExt);
                    // 入账信息表
                    List<BokDealAccounting> listBokDealAccounting = (List<BokDealAccounting>) map.get(
                            "BokDealAccounting");
                    result.setListBokDealAccounting(listBokDealAccounting);
                    // 账务明细表
                    List<BokDealAccountingDetail> listBokDealAccountingDetail = (List<BokDealAccountingDetail>) map.get(
                            "BokDealAccountingDetail");
                    result.setListBokDealAccountingDetail(listBokDealAccountingDetail);
                    // 账务明细代码表
                    List<BokDetailId> listBokDetailId = (List<BokDetailId>) map.get("BokDetailId");
                    result.setListBokDetailId(listBokDetailId);
                    // 结算指令表
                    List<StlOrders> listStlOrders = (List<StlOrders>) map.get("StlOrders");
                    result.setListStlOrders(listStlOrders);
                    // 结算指令扩展表
                    List<StlOrdersExt> listStlOrdersExt = (List<StlOrdersExt>) map.get("StlOrdersExt");
                    result.setListStlOrdersExt(listStlOrdersExt);

                    /**add by wujie on 20210501 START**/
                    result.setListValTradeFeeDetail((List<TrdTradeFeeDetail>) map.get("ValTradeFeeDetail"));
                    /**add by wujie on 20210501 END**/

                    // 添加日志
                    result.setValInterflog((List<ValInterflog>) map.get("valInterflogList"));
                }
                TrdPlanTradeResult rst = new TrdPlanTradeResult(result);
                return rst;
            }else if(trdPlanValuationTradeDto.getPs().equals(DictCodeList.交易方向_赎回到账.getValue()) ){
                //赎回到账 ==>对应接口【计划类资产(货币型)赎回到账交易】
                //赎回确认 ==>对应接口【计划类资产(货币型)赎回确认交易】
                //设置接口参数
                Map<String, Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdPlanValuationTradeDto.getPortfolioId());
                params.put("INTERF_BOOKSETID", prodIds);
                params.put("VAL_MODEL", "INTERF_MODEL_SINGLE");

                List<ValMngMnfRedAccoutTrade> listValMngMnfRedAccoutTrade = new ArrayList<ValMngMnfRedAccoutTrade>();
                ValMngMnfRedAccoutTrade valMngMnfRedAccoutTrade = new ValMngMnfRedAccoutTrade();
                //实体类字段映射处理
                valMngMnfRedAccoutTrade = dealBean(valMngMnfRedAccoutTrade,trdPlanValuationTradeDto);

                listValMngMnfRedAccoutTrade.add(valMngMnfRedAccoutTrade);
                Map<String, Object> map = cPlannedRedAccoutDealTemplateRecService.handleValMngMnfRedAccoutTrade(listValMngMnfRedAccoutTrade, new Date(), params, null);
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
            }else{
                //返回初始化
                return trdPlanTradeResult;
            }

        }else{
        //返回初始化
        return trdPlanTradeResult;
        }

    }

    /**
     * @auther: 属性转换处理-买入、卖出
     * @date: 2022-3-2
     * @param:
     * @return:
     */
    public ValMngMnfTade dealBean(ValMngMnfTade valMngMnfTade,TrdPlanValuationTradeDto dto){
        BeanUtil.copyPropertiesIgnoreNull(valMngMnfTade,dto);
        //相同字段名： 成交编号、交易方向、交易场所、交易日、交易金额、币种
        //实体类字段映射处理
        valMngMnfTade.setProdId(dto.getPortfolioId());//产品代码
        valMngMnfTade.setAssetId("F01_"+dto.getFinprodId());//资产代码
        valMngMnfTade.setAssetName(dto.getFinprodName());//资产名称
        valMngMnfTade.setDataActionSign("01");//数据动作标志
        valMngMnfTade.setPrinAmt(new BigDecimal("0.00"));//交易本金
        valMngMnfTade.setShareAmt(dto.getNums());//交易份额
        valMngMnfTade.setIsClean("N"); //有无清算指令

        valMngMnfTade.setCounterId("FYDLCA365001");//非必填 交易对手
        valMngMnfTade.setCounterType("01");//非必填 交易对手类型
        valMngMnfTade.setIsChlFeeAmt("Y");//非必填 通道费是否随交易支付
        return valMngMnfTade;
    }

    /**
     * @auther: 属性转换处理-申购、认购
     * @date: 2022-3-2
     * @param:
     * @return:
     */
    public ValMngMnfApplyPurTade dealBean(ValMngMnfApplyPurTade valMngMnfApplyPurTade,TrdPlanValuationTradeDto dto){
        BeanUtil.copyPropertiesIgnoreNull(valMngMnfApplyPurTade,dto);
        //相同字段名： 成交编号、交易方向、交易场所、币种
        valMngMnfApplyPurTade.setProdId(dto.getPortfolioId());//产品代码
        //比较界面和接口字段差异，并手动给接口字段赋值
        valMngMnfApplyPurTade.setAssetId("F01_"+dto.getFinprodId());//资产代码
        valMngMnfApplyPurTade.setAssetName(dto.getFinprodName());//资产名称
        valMngMnfApplyPurTade.setDataActionSign("01");//数据动作标志
        valMngMnfApplyPurTade.setIsClean("N"); //有无清算指令
        valMngMnfApplyPurTade.setCounterId("FYDLCA365001");//非必填 交易对手
        valMngMnfApplyPurTade.setCounterType("01");//非必填 交易对手类型
        if(StringUtil.isNotEmpty(dto.getPs())){
            if(dto.getPs().equals(DictCodeList.交易方向_申购.getValue()) ){
                valMngMnfApplyPurTade.setTradeDate(dto.getSqTradeDate());//交易日
                valMngMnfApplyPurTade.setPurAmt(dto.getSqTradeAmt());//申购金额
            }else if(dto.getPs().equals(DictCodeList.交易方向_认购.getValue()) ){
                valMngMnfApplyPurTade.setTradeDate(dto.getRgTradeDate());//交易日
                valMngMnfApplyPurTade.setPurAmt(dto.getSqTradeAmt());//认购金额
            }
        }
        return valMngMnfApplyPurTade;
    }

    /**
     * @auther: 属性转换处理-申购确认、认购确认
     * @date: 2022/3/2
     * @param:
     * @return:
     */
    public ValMngMnfAffirmPurTrade dealBean(ValMngMnfAffirmPurTrade valMngMnfAffirmPurTrade,TrdPlanValuationTradeDto dto){
        BeanUtil.copyPropertiesIgnoreNull(valMngMnfAffirmPurTrade,dto);
        //相同字段名： 成交编号、交易方向、交易场所、币种
        valMngMnfAffirmPurTrade.setProdId(dto.getPortfolioId());//产品代码
        //比较界面和接口字段差异，并手动给接口字段赋值
        valMngMnfAffirmPurTrade.setAssetId("F01_"+dto.getFinprodId());//资产代码  F06基金
        valMngMnfAffirmPurTrade.setAssetName(dto.getFinprodName());//资产名称
        valMngMnfAffirmPurTrade.setDataActionSign("01");//数据动作标志
        valMngMnfAffirmPurTrade.setIsClean("N"); //有无清算指令
        if(StringUtil.isNotEmpty(dto.getPs())){
            if(dto.getPs().equals(DictCodeList.交易方向_申购确认.getValue()) ){
                valMngMnfAffirmPurTrade.setTradeDate(dto.getSgqrTradeDate());//交易日
                valMngMnfAffirmPurTrade.setCfmAmt(dto.getSgqrTradeAmt());//确认金额
                valMngMnfAffirmPurTrade.setCfmShare(dto.getSgqrNums());//确认份额
            }else if(dto.getPs().equals(DictCodeList.交易方向_认购确认.getValue()) ){
                valMngMnfAffirmPurTrade.setTradeDate(dto.getRgqrTradeDate());//交易日
                valMngMnfAffirmPurTrade.setCfmAmt(dto.getRgqrTradeAmt());//确认金额
                valMngMnfAffirmPurTrade.setCfmShare(dto.getRgqrNums());//确认份额
            }
        }
        return valMngMnfAffirmPurTrade;
    }

    /**
     * @auther: 属性转换处理-赎回确认
     * @date: 2022/3/2
     * @param:
     * @return:
     */
    public ValMngMnfRedAffirm dealBean(ValMngMnfRedAffirm valMngMnfRedAffirm,TrdPlanValuationTradeDto dto){
        BeanUtil.copyPropertiesIgnoreNull(valMngMnfRedAffirm,dto);
        //相同字段名： 成交编号、交易方向、交易场所、币种
        valMngMnfRedAffirm.setProdId(dto.getPortfolioId());//产品代码
        //比较界面和接口字段差异，并手动给接口字段赋值
        valMngMnfRedAffirm.setAssetId("F01_"+dto.getFinprodId());//资产代码  F06基金
        valMngMnfRedAffirm.setAssetName(dto.getFinprodName());//资产名称
        valMngMnfRedAffirm.setTradeDate(dto.getShqrTradeDate());//交易日
        valMngMnfRedAffirm.setPreDate(dto.getShqrSettleDate());//预计到账日
        valMngMnfRedAffirm.setRedShare(dto.getShqrNums());//确认数量
        valMngMnfRedAffirm.setRedAmt(dto.getShqrTradeAmt());//赎回金额

        valMngMnfRedAffirm.setDataActionSign("01");//数据动作标志
        valMngMnfRedAffirm.setIsClean("N"); //有无清算指令
        return valMngMnfRedAffirm;
    }

    /**
     * @auther: 属性转换处理-赎回到账
     * @date: 2022/3/2
     * @param:
     * @return:
     */
    public ValMngMnfRedAccoutTrade dealBean(ValMngMnfRedAccoutTrade valMngMnfRedAccoutTrade,TrdPlanValuationTradeDto dto){
        BeanUtil.copyPropertiesIgnoreNull(valMngMnfRedAccoutTrade,dto);
        //相同字段名： 成交编号、交易方向、交易场所、币种
        valMngMnfRedAccoutTrade.setProdId(dto.getPortfolioId());//产品代码
        //比较界面和接口字段差异，并手动给接口字段赋值
        valMngMnfRedAccoutTrade.setAssetId("F01_"+dto.getFinprodId());//资产代码  F06基金
        valMngMnfRedAccoutTrade.setTradeDate(dto.getShqrSettleDate());//交易日
        valMngMnfRedAccoutTrade.setTradeAmt(dto.getRedemptionFee());//赎回费

        valMngMnfRedAccoutTrade.setDataActionSign("01");//数据动作标志
        valMngMnfRedAccoutTrade.setIsClean("N"); //有无清算指令
        return valMngMnfRedAccoutTrade;
    }




}
