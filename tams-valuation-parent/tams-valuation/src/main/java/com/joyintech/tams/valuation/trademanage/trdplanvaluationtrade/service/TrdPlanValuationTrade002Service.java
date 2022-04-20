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
import com.joyintech.framework.select.SelectOptionDto;
import com.joyintech.framework.util.ArithmeticUtil;
import com.joyintech.framework.util.BeanUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.valuation.common.constants.ServiceApplication;
import com.joyintech.tams.valuation.common.interf.base.dto.PlanAssetCurrencyDealCommonResult;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.fund.FundBuyAndSellDealRecService;
import com.joyintech.tams.valuation.common.interf.base.service.assetdeal.pacd.*;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.*;
import com.joyintech.tams.valuation.trademanage.trdfundvaluationtrade.dto.TrdFundTradeResult;
import com.joyintech.tams.valuation.trademanage.trdfundvaluationtrade.dto.TrdFundValuationTradeDto;
import com.joyintech.tams.valuation.trademanage.trdfundvaluationtrade.service.TrdFundValuationTrade002Service;
import com.joyintech.tams.valuation.trademanage.trdplanvaluationtrade.dto.TrdPlanTradeResult;
import com.joyintech.tams.valuation.trademanage.trdplanvaluationtrade.dto.TrdPlanValuationTradeDto;
import com.joyintech.workflow.engine.constant.OperationType;
import com.joyintech.workflow.engine.service.BaseProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
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

    @Autowired
    private TrdFundValuationTrade002Service trdFundValuationTrade002Service;

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
     * @Description:根据交易编号查询详情
     * @auther: yuzj
     * @date: 2022/1/7
     * @param:
     * @return:
     */
    public TrdPlanValuationTradeDto queryTradeInfoByTradeId( Map<String,Object> param) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdPlanValuationTrade/queryTradeInfoByTradeId",
                param, TrdPlanValuationTradeDto.class);
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

    /**
     * 赎回到账详情查询
     */
    public TrdPlanValuationTradeDto queryRedInfoByTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdPlanValuationTrade/queryRedInfoByTradeId",
                tradeId, TrdPlanValuationTradeDto.class);
    }

    /**
     * 赎回到账双击明细页面
     */
    public TrdPlanValuationTradeDto queryRedInfoByOutTradeId(String tradeId) {
        return restTemplate.postForObject(
                ServiceApplication.VALUATION_SERVICE_NAME + "/TrdPlanValuationTrade/queryRedInfoByOutTradeId",
                tradeId, TrdPlanValuationTradeDto.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doComplete(TrdPlanValuationTradeDto entity, OperationType operationType) {
        String action = StringUtil.EMPTY;
        TrdPlanTradeResult trdPlanTradeResult = new TrdPlanTradeResult();
        TrdFundTradeResult trdFundTradeResult = new TrdFundTradeResult();

        if (OperationType.NEW.equals(operationType)) {
            action = "insert";
        }
        else {
            action = "update";
        }

        //净值型走基金&计划类（净值型）接口
         if(DictCodeList.收益类型_净值型.getValue().equals(entity.getProfitType())){
            TrdFundValuationTradeDto dto = new TrdFundValuationTradeDto();
            BeanUtil.copyPropertiesIgnoreNull(dto,entity);
            trdFundTradeResult = trdFundValuationTrade002Service.generateEntity(dto,"B");
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdFundValuationTrade/" + action, trdFundTradeResult, Integer.class);
        }else if(DictCodeList.收益类型_货币型.getValue().equals(entity.getProfitType())){
            trdPlanTradeResult = generateEntity(entity);
            restTemplate.postForObject(ServiceApplication.VALUATION_SERVICE_NAME+"/TrdPlanValuationTrade/" + action, trdPlanTradeResult, Integer.class);
        }

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
                    prodIds.add(trdPlanValuationTradeDto.getProdId());
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
                    rst.setTrdPlanValuationTradeDto(trdPlanValuationTradeDto);
                    return rst;
            } else if (trdPlanValuationTradeDto.getPs().equals(DictCodeList.交易方向_申购.getValue())) {
                //申购、认购 ==>对应接口【计划类资产(货币型)申购申请交易】
                //设置接口参数
                Map<String, Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdPlanValuationTradeDto.getProdId());
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
                rst.setTrdPlanValuationTradeDto(trdPlanValuationTradeDto);
                return rst;
            } else if(trdPlanValuationTradeDto.getPs().equals(DictCodeList.交易方向_申购确认.getValue())){
                //申购确认、认购确认 ==>对应接口【计划类资产(货币型)申购确认交易】
                //设置接口参数
                Map<String, Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdPlanValuationTradeDto.getProdId());
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
                rst.setTrdPlanValuationTradeDto(trdPlanValuationTradeDto);
                return rst;
            }else if(trdPlanValuationTradeDto.getPs().equals(DictCodeList.交易方向_赎回确认.getValue()) ){
                //赎回确认 ==>对应接口【计划类资产(货币型)赎回确认交易】
                //设置接口参数
                Map<String, Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdPlanValuationTradeDto.getProdId());
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
                rst.setTrdPlanValuationTradeDto(trdPlanValuationTradeDto);
                return rst;
            }else if(trdPlanValuationTradeDto.getPs().equals(DictCodeList.交易方向_赎回到账.getValue()) ){
                //赎回到账 ==>对应接口【计划类资产(货币型)赎回到账交易】
                //赎回确认 ==>对应接口【计划类资产(货币型)赎回确认交易】
                //设置接口参数
                Map<String, Object> params = new HashMap<>();
                List<String> prodIds = new ArrayList<>();
                prodIds.add(trdPlanValuationTradeDto.getProdId());
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
                rst.setTrdPlanValuationTradeDto(trdPlanValuationTradeDto);
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
        valMngMnfTade.setAssetName(dto.getAssetAbbr());//资产名称
        valMngMnfTade.setShareAmt(dto.getDealNum());//交易份额
        valMngMnfTade.setDataActionSign("01");//数据动作标志
        valMngMnfTade.setPrinAmt(dto.getTradeAmt());//交易本金
        valMngMnfTade.setTradeMarket("09");//交易市场
        valMngMnfTade.setTradeAmt(dto.getSettleAmt());
        valMngMnfTade.setUnitCprice(ArithmeticUtil.divide(dto.getTradeAmt(),dto.getDealNum(),4, RoundingMode.HALF_UP));//单位净价
        valMngMnfTade.setIsClean("N"); //有无清算指令

        valMngMnfTade.setMyAcctNo(dto.getTrdMyAcctNo());//资金账户

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
        //比较界面和接口字段差异，并手动给接口字段赋值
        valMngMnfApplyPurTade.setAssetName(dto.getAssetAbbr());//资产名称

        if(StringUtil.isNotEmpty(dto.getPs())){
            if(dto.getPs().equals(DictCodeList.交易方向_申购.getValue()) ){
                valMngMnfApplyPurTade.setTradeDate(dto.getPurApplyDate());//交易日
                valMngMnfApplyPurTade.setPurAmt(dto.getPurApplyAmt());//申购金额
                valMngMnfApplyPurTade.setMyAcctNo(dto.getPurMyAcctNo());//资金账户
            }else if(dto.getPs().equals(DictCodeList.交易方向_认购.getValue()) ){
                valMngMnfApplyPurTade.setTradeDate(dto.getApplyDate());//交易日
                valMngMnfApplyPurTade.setPurAmt(dto.getApplyAmt());//认购金额
                valMngMnfApplyPurTade.setMyAcctNo(dto.getSubMyAcctNo());//资金账户
            }
        }

        valMngMnfApplyPurTade.setDataActionSign("01");//数据动作标志
        valMngMnfApplyPurTade.setTradeMarket("09");
        valMngMnfApplyPurTade.setIsClean("N"); //有无清算指令

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
        //比较界面和接口字段差异，并手动给接口字段赋值
        valMngMnfAffirmPurTrade.setAssetName(dto.getAssetAbbr());//资产名称

        if(StringUtil.isNotEmpty(dto.getPs())){
            if(dto.getPs().equals(DictCodeList.交易方向_申购确认.getValue()) ){
                valMngMnfAffirmPurTrade.setTradeDate(dto.getPurConfirmDate());//交易日
                valMngMnfAffirmPurTrade.setCfmAmt(dto.getPurConfirmAmt());//确认金额
                valMngMnfAffirmPurTrade.setCfmShare(dto.getPurConfirmNum());//确认份额
                valMngMnfAffirmPurTrade.setMyAcctNo(dto.getPurConfirmMyAcctNo());//资金账户
                valMngMnfAffirmPurTrade.setUnitCprice(dto.getPurUnitValue());//单位净值
                valMngMnfAffirmPurTrade.setFee(dto.getPurFee());//申购费
            }
        }

        valMngMnfAffirmPurTrade.setTradeMarket("09");//交易市场
        valMngMnfAffirmPurTrade.setDataActionSign("01");//数据动作标志
        valMngMnfAffirmPurTrade.setIsClean("N"); //有无清算指令

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
        //比较界面和接口字段差异，并手动给接口字段赋值
        valMngMnfRedAffirm.setAssetName(dto.getAssetAbbr());//资产名称

        valMngMnfRedAffirm.setTradeDate(dto.getRedConfirmDate());//交易日
        valMngMnfRedAffirm.setPreDate(dto.getArriveDate());//预计到账日
        valMngMnfRedAffirm.setRedShare(dto.getRedConfirmNum());//确认数量
        valMngMnfRedAffirm.setRedAmt(dto.getRedConfirmAmt());//赎回金额
        valMngMnfRedAffirm.setFee(dto.getRedFee());//赎回费

        valMngMnfRedAffirm.setTradeMarket("09");//交易市场
        valMngMnfRedAffirm.setDataActionSign("01");//数据动作标志
        valMngMnfRedAffirm.setIsClean("N"); //有无清算指令

        valMngMnfRedAffirm.setMyAcctNo(dto.getRedMyAcctNo());//资金账户
        valMngMnfRedAffirm.setUnitCprice(dto.getRedUnitValue());//单位净值

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
        //比较界面和接口字段差异，并手动给接口字段赋值
        valMngMnfRedAccoutTrade.setAssetId(dto.getAssetId());//资产代码  F06基金
        valMngMnfRedAccoutTrade.setTradeDate(dto.getArriveDate());//交易日
        valMngMnfRedAccoutTrade.setTradeAmt(dto.getTradeAmtArrive());//赎回费

        valMngMnfRedAccoutTrade.setDataActionSign("01");//数据动作标志
        valMngMnfRedAccoutTrade.setIsClean("N"); //有无清算指令

        valMngMnfRedAccoutTrade.setMyAcctNo(dto.getRedMyAcctNo());//资金账户

        return valMngMnfRedAccoutTrade;
    }




}
