/*
 * 文件名：FinProductCommonBatchService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：wangzhiang
 * 修改时间：2018年5月31日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.batch.common.finproduct.service;

import com.joyintech.tams.common.base.entity.BokAmoConfig;
import com.joyintech.tams.common.base.entity.BokFinProductValuationPara;
import com.joyintech.tams.common.base.entity.FinExercisePlan;
import com.joyintech.tams.common.base.entity.FinProductAdd;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComDTO;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.dto.DrawBaseInfoSelectComResult;
import com.joyintech.tams.valuation.batch.common.drawbaseinfoselectcom.service.DrawBaseInfoReaderParameterGenerator;
import com.joyintech.tams.valuation.common.batch.finproduct.dao.InitFinProductCommonMapper;
import com.joyintech.tams.valuation.common.batch.finproduct.entity.*;
import com.joyintech.tams.valuation.common.constants.BookConstant;
import com.joyintech.tams.valuation.common.constants.InitConstant;
import com.joyintech.tams.valuation.common.constants.ValDictCodeList;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 标的类共通方法
 * 
 * @author wangzhiang
 * @version 1.0 2018年5月31日
 * @see InitFinProductCommonBatchService
 * @since 1.0searchInvAim
 */
@Service
public class InitFinProductCommonBatchService {

    private static final String FIN_PROD_ID_LIST = "finprodIdList";

    private static final String BRANCH = "branch";

    /**
     * 标的类共同mapper
     */
    @Autowired
    private InitFinProductCommonMapper initFinProductCommonMapper;

    /**
     * 统一查询资产基本信息，并传入processer处理
     *
     */
    public void quaryResultInfo(List<DrawBaseInfoSelectComResult> result, DrawBaseInfoReaderParameterGenerator parameterGenerator) {
        //发生日
        Date happenDate = result.get(0).getHappenDate();

        List<TargetSearchDto> params = parameterGenerator.createSearchParam(happenDate);

        if(null == params || params.isEmpty()) return;
        //账套代码list
        Collection<String> booksetIdList = result.stream().map(DrawBaseInfoSelectComResult::getBooksetId).collect(Collectors.toSet());

        //资产代码list
        List<String> finprodIdList = result.stream().map(DrawBaseInfoSelectComResult::getBusiId).distinct().collect(Collectors.toList());
        //标的_信息
        Map<String, TargetFinProduct> productMap = new HashMap<>();
        // 还本计划
        Map<String, List<TargetCashPlan>> cashPlanMap = new HashMap<>();
        // 付息计划
        Map<String, List<TargetCashPlan>> cashPlanIntMap = new HashMap<>();
        // 现金流计算规则
        Map<String, List<TargetCashCalcRule>> cashCalcRuleMap = new HashMap<>();
        // 现金流计算规则收取
        Map<String, List<TargetCashCalcRule>> cashCalcRuleMapR = new HashMap<>();
        // 现金流计算规则支付
        Map<String, List<TargetCashCalcRule>> cashCalcRuleMapP = new HashMap<>();
        // 现金流支付规则
        Map<String, List<TargetCashPayRule>> cashPayRuleMap = new HashMap<>();
        // 行权计划
        Map<String, List<FinExercisePlan>> finExercisePlanMap = new HashMap<>();
        // 附加信息
        Map<String, FinProductAdd> finProductAddMap = new HashMap<>();
        // 账套金融产品估值参数
        Map<String, BokFinProductValuationPara> bokFinProductValuationParaMap = new HashMap<>();
        // 摊销重算配置
        Map<String, List<BokAmoConfig>> bokAmoConfigMap = new HashMap<>();
        //区间到账应计利息
        Map<String, List<InitTrdProductDealDto>> intervalArrivalAccountMap = new HashMap<>();

        //交易类金融产品标的信息
        Map<String, List<TradeInfoDto>> tradeInfoMap = new HashMap<>();

        // 查询
        params.stream().parallel().forEach(svo -> {
            // 获取当前查询类型
            String queryTypeNow = svo.getQueryType();
            String cashType=svo.getCashType2();
            // 设置sql查询参数map
            Map<String, Object> paMap = createSqlMapParam(finprodIdList, svo);

            // 标的信息
            if(ValDictCodeList.标的_信息.getValue().equals(queryTypeNow)) {
                productMap.putAll(getFinProdInfo(paMap));
            }
            // 现金流计划
            if(ValDictCodeList.标的_现金流计划.getValue().equals(queryTypeNow)) {
                if(DictCodeListExt1.现金流一级类型_利息.getValue().equals(svo.getCashType1())
                        || DictCodeListExt1.现金流二级类型_利息.getValue().equals(svo.getCashType2())) {
                    // 付息计划
                    cashPlanIntMap.putAll(getCashPlan(paMap));
                }
                else {
                    // 现金流计划
                    cashPlanMap.putAll(getCashPlan(paMap));
                }
            }
            // 现金流计算规则
            if(ValDictCodeList.标的_现金流计算规则.getValue().equals(queryTypeNow)) {
                Map<String, List<TargetCashCalcRule>> cashCalcRule=getCashCalcRule(paMap);
                cashCalcRuleMap.putAll(cashCalcRule);
                if(cashType.equals(DictCodeListExt1.现金流二级类型_收取.getValue())){
                    cashCalcRuleMapR.putAll(cashCalcRule);
                }
                if(cashType.equals(DictCodeListExt1.现金流二级类型_支付.getValue())){
                    cashCalcRuleMapP.putAll(cashCalcRule);
                }
            }
            // 现金流支付规则
            if(ValDictCodeList.标的_现金流支付规则.getValue().equals(queryTypeNow)) {
                cashPayRuleMap.putAll(getCashPayRule(paMap));
            }
            // 行权计划信息
            if(InitConstant.BATCH_FIN_EXERCISE_PLAN.equals(queryTypeNow)) {
                // 行权计划特殊参数
                paMap.put("exerciseDate", happenDate);
                finExercisePlanMap.putAll(getExercisePlan(paMap));
            }
            // 附加信息
            if(InitConstant.BATCH_FIN_PRODUCT_ADD.equals(queryTypeNow)) {
                finProductAddMap.putAll(getFinProductAdd(booksetIdList));
            }
            // 账套金融产品估值参数
            if(InitConstant.BATCH_BOK_FIN_PRODUCT_VALUATION_PARA.equals(queryTypeNow)) {
                bokFinProductValuationParaMap.putAll(getBokFinProductValuationParaMap());
            }
            // 摊销重算配置
            if(InitConstant.BATCH_BOK_AMO_CONFIG.equals(queryTypeNow)) {
                bokAmoConfigMap.putAll(getBokAmoConfigMap(finprodIdList, booksetIdList, happenDate));
            }
            // 区间到账应计利息
            if(InitConstant.INTERVALARRIVALACCOUNT.equals(queryTypeNow)) {
                intervalArrivalAccountMap.putAll(getntervalArrivalAccountMap(finprodIdList, booksetIdList));
            }

            // 金融类产品标的信息
            if(ValDictCodeList.标的_金融类产品标的.getValue().equals(queryTypeNow)) {
                tradeInfoMap.putAll(getFinTradeProdInfo(finprodIdList));
            }



        });

        // 赋值
        setInfo(result, productMap, cashPlanMap, cashPlanIntMap, cashCalcRuleMap,cashCalcRuleMapR,cashCalcRuleMapP, cashPayRuleMap, finExercisePlanMap, finProductAddMap, bokAmoConfigMap, bokFinProductValuationParaMap, intervalArrivalAccountMap,tradeInfoMap);
    }

    private Map<String, Object> createSqlMapParam(List<String> finprodIdList, TargetSearchDto searchDto) {
        Map<String, Object> paMap = new HashMap<>();
        if(finprodIdList.size() == 1) {
            paMap.put("finprodId", finprodIdList.get(0));// 标的代码

        }
        paMap.put(FIN_PROD_ID_LIST, finprodIdList);// 标的代码List
        paMap.put("finprodType", searchDto.getFinprodType());// 金融产品类型
        paMap.put("cashId", searchDto.getCashId());// 现金流代码
        paMap.put("cashType1", searchDto.getCashType1());// 现金流一级类型
        paMap.put("cashType2", searchDto.getCashType2());// 现金流二级类型
        paMap.put("payStatus", searchDto.getPayStatus());// 现金流计划支付标志
        paMap.put("effdateStart", searchDto.getEffdateStart());// 生效日期 - 开始
        paMap.put("effdateEnd", searchDto.getEffdateEnd());// 生效日期 - 结束
        paMap.put(BRANCH, searchDto.getBranch() == null ? BookConstant.DEFALUT_BRANCH : searchDto.getBranch()); // 分支序号
        return paMap;
    }

    /**
     * 设置标的信息
     */
    private Map<String, TargetFinProduct> getFinProdInfo(Map<String, Object> paMap) {
        List<TargetFinProduct> targetFinProductList = initFinProductCommonMapper.getTargetFinProduct(paMap);
        if (targetFinProductList != null && !targetFinProductList.isEmpty()) {
            //标的_信息
            return targetFinProductList.stream().collect(Collectors.toMap(TargetFinProduct::getFinprodId, item -> item));
        }
        return Collections.emptyMap();
    }

    /**
     * 标的_现金流计划
     */
    private Map<String, List<TargetCashPlan>> getCashPlan(Map<String, Object> paMap) {
        List<TargetCashPlan> targetCashPlanList = initFinProductCommonMapper.getTargetCashPlanList(paMap);
        if (targetCashPlanList != null && !targetCashPlanList.isEmpty()) {
            return targetCashPlanList.stream().collect(Collectors.groupingBy(TargetCashPlan::getFinprodId));
        }
        return Collections.emptyMap();
    }
//
//    /**
//     * 付息计划
//     */
//    private Map<String, List<TargetCashPlan>> getCashPlanInt(Map<String, Object> paMap) {
//        List<TargetCashPlan> targetCashPlanIntList = initFinProductCommonMapper.getTargetCashPlanList(paMap);
//        if (targetCashPlanIntList != null && !targetCashPlanIntList.isEmpty()) {
//            return targetCashPlanIntList.stream().collect(Collectors.groupingBy(TargetCashPlan::getFinprodId));
//        }
//        return Collections.emptyMap();
//    }

    /**
     * 现金流计算规则
     */
    private Map<String, List<TargetCashCalcRule>> getCashCalcRule(Map<String, Object> paMap) {
        List<TargetCashCalcRule> targetCashCalcRuleList = initFinProductCommonMapper.getTargetCashCalcRuleList(paMap);
        if (targetCashCalcRuleList != null && !targetCashCalcRuleList.isEmpty()) {
            return targetCashCalcRuleList.stream().collect(Collectors.groupingBy(TargetCashCalcRule::getFinprodId));
        }
        return Collections.emptyMap();
    }

    /**
     * 现金流支付规则
     */
    private Map<String, List<TargetCashPayRule>> getCashPayRule(Map<String, Object> paMap) {
        List<TargetCashPayRule> targetCashPayRuleList = initFinProductCommonMapper.getTargetCashPayRuleList(paMap);
        if (targetCashPayRuleList != null && !targetCashPayRuleList.isEmpty()) {
            return targetCashPayRuleList.stream().collect(Collectors.groupingBy(TargetCashPayRule::getFinprodId));
        }
        return Collections.emptyMap();
    }

    /**
     * 行权计划
     */
    private Map<String, List<FinExercisePlan>> getExercisePlan(Map<String, Object> paMap) {
        List<FinExercisePlan> finExercisePlanList = initFinProductCommonMapper.queryFinExercisePlan(paMap);
        if (finExercisePlanList != null && !finExercisePlanList.isEmpty()) {
            return finExercisePlanList.stream().collect(Collectors.groupingBy(FinExercisePlan::getFinprodId));
        }
        return Collections.emptyMap();
    }

    /**
     * 附加信息
     */
    private Map<String, FinProductAdd> getFinProductAdd(Collection<String> booksetIdList) {
        // 设置sql查询参数map
        Map<String, Object> paMap = new HashMap<>();
        //资产代码
        paMap.put("booksetIdList", booksetIdList);
        List<FinProductAdd> finProductAddList = initFinProductCommonMapper.queryFinProductAdd(paMap);
        if (finProductAddList != null && !finProductAddList.isEmpty()) {
            return finProductAddList.stream().collect(Collectors.toMap(FinProductAdd::getFinprodId, item -> item));
        }
        return Collections.emptyMap();
    }
    
    /**
     * 账套金融产品估值参数
     */
    private Map<String, BokFinProductValuationPara> getBokFinProductValuationParaMap() {
        // 账套金融产品估值参数
        // 设置sql查询参数map
        Map<String, Object> paMap = new HashMap<>();
        paMap.put(BRANCH, InterfCommonConstant.分支序号_默认);
        List<BokFinProductValuationPara> bokFinProductValuationParaList = initFinProductCommonMapper.queryBokFinProductValuationPara(paMap);
        if (bokFinProductValuationParaList != null && !bokFinProductValuationParaList.isEmpty()) {
            return bokFinProductValuationParaList.stream()
                    .collect(Collectors.toMap(item -> item.getBoksetId() + "_" + item.getFinprodId() + "_" + item.getBranch(), item -> item));
        }
        return Collections.emptyMap();
    }

    /**
     * 摊销重算配置
     */
    private Map<String, List<BokAmoConfig>> getBokAmoConfigMap(List<String> finprodIdList, Collection<String> booksetIdList, Date happenDate) {
        //摊销重算配置
        // 设置sql查询参数map
        Map<String, Object> paMap = new HashMap<>();
        //资产代码
        paMap.put(FIN_PROD_ID_LIST, finprodIdList);
        paMap.put("booksetIdList", booksetIdList);
        paMap.put("cdate", happenDate);
        List<BokAmoConfig> bokAmoConfigList = initFinProductCommonMapper.getBokAmoConfig(paMap);
        if (bokAmoConfigList != null && !bokAmoConfigList.isEmpty()) {
            return bokAmoConfigList.stream().collect(Collectors.groupingBy(BokAmoConfig::getBusiId));
        }
        return Collections.emptyMap();
    }

    /**
     * 区间到账应计利息
     */
    private Map<String, List<InitTrdProductDealDto>> getntervalArrivalAccountMap(List<String> finprodIdList, Collection<String> booksetIdList) {
        //摊销重算配置
        // 设置sql查询参数map
        Map<String, Object> paMap = new HashMap<>();
        //资产代码
        paMap.put(FIN_PROD_ID_LIST, finprodIdList);
        paMap.put("booksetIdList", booksetIdList);
        paMap.put("calDate", null);
        List<InitTrdProductDealDto> intervalArrivalAccountList = initFinProductCommonMapper.intervalArrivalAccount(paMap);
        if (intervalArrivalAccountList != null && !intervalArrivalAccountList.isEmpty()) {
            return intervalArrivalAccountList.stream().collect(Collectors.groupingBy(item -> item.getBooksetId() + "_" + item.getFinprodId()));
        }
        return Collections.emptyMap();
    }

    /**
     * 金融产品标的信息
     */
    private Map<String, List<TradeInfoDto>> getFinTradeProdInfo(List<String> finprodIdList) {
        // 设置sql查询参数map
        Map<String, Object> paMap = new HashMap<>();
        //资产代码
        paMap.put(FIN_PROD_ID_LIST, finprodIdList);
        List<TradeInfoDto> tradeInfoList = initFinProductCommonMapper.queryTradeInfoList(paMap);
        if (tradeInfoList != null && !tradeInfoList.isEmpty()) {
            return tradeInfoList.stream().collect(Collectors.groupingBy(item -> item.getFinprodId()+"_"+item.getFinprodType()+"_"+item.getBranch()));
        }
        return Collections.emptyMap();
    }


    /**
     * @param result 计提基础信息列表
     * @param productMap 标的信息MAP
     * @param cashPlanMap 还本计划MAP
     * @param cashPlanIntMap 付息计划MAP
     * @param cashCalcRuleMap 现金流计算规则信息
     * @param cashPayRuleMap 现金流支付规则信息
     * @param finExercisePlanMap 行权计划信息
     * @param finProductAddMap 标的附加信息
     * @param bokAmoConfigMap 摊销重算配置
     * @param bokAmoConfigMap 区间到账应计利息
     * @param tradeInfoMap 金融产品标的信息
     */
    private void setInfo(List<DrawBaseInfoSelectComResult> result,
                         Map<String, TargetFinProduct> productMap, Map<String, List<TargetCashPlan>> cashPlanMap,
                         Map<String, List<TargetCashPlan>> cashPlanIntMap, Map<String, List<TargetCashCalcRule>> cashCalcRuleMap,
                         Map<String, List<TargetCashCalcRule>> cashCalcRuleMapR, Map<String, List<TargetCashCalcRule>> cashCalcRuleMapP,
                         Map<String, List<TargetCashPayRule>> cashPayRuleMap, Map<String, List<FinExercisePlan>> finExercisePlanMap,
                         Map<String, FinProductAdd> finProductAddMap, Map<String, List<BokAmoConfig>> bokAmoConfigMap,
                         Map<String, BokFinProductValuationPara> bokFinProductValuationParaMap, Map<String, List<InitTrdProductDealDto>> intervalArrivalAccountMap, Map<String, List<TradeInfoDto>> tradeInfoMap) {
        for (DrawBaseInfoSelectComResult draw : result) {
            //标的信息
            draw.setTargetFinProduct(productMap.get(draw.getBusiId()));
            //还本计划
            List<TargetCashPlan> cashPlanList = cashPlanMap.get(draw.getBusiId());
            if (CollectionUtils.isNotEmpty(cashPlanList)) {
            	draw.setTargetCashPlanList(cashPlanList);
            }
            else {
            	draw.setTargetCashPlanList(new ArrayList<>());
            }
            //付息计划
            List<TargetCashPlan> cashPlanIntList = cashPlanIntMap.get(draw.getBusiId());
            if (CollectionUtils.isNotEmpty(cashPlanIntList)) {
            	draw.setTargetCashPlanIntList(cashPlanIntList);
            }
            else {
            	draw.setTargetCashPlanIntList(new ArrayList<>());
            }
            //标的_现金流计算规则信息
            List<TargetCashCalcRule> cashCalcRuleList = cashCalcRuleMap.get(draw.getBusiId());
            if (CollectionUtils.isNotEmpty(cashCalcRuleList)) {
            	draw.setTargetCashCalcRuleList(cashCalcRuleList);
            }
            else {
            	draw.setTargetCashCalcRuleList(new ArrayList<>());
            }
            //标的_现金流计算规则信息 收取
            List<TargetCashCalcRule> cashCalcRuleListR = cashCalcRuleMapR.get(draw.getBusiId());
            if (CollectionUtils.isNotEmpty(cashCalcRuleListR)) {
                draw.setTargetCashCalcRuleListR(cashCalcRuleListR);
            }
            else {
                draw.setTargetCashCalcRuleListR(new ArrayList<>());
            }
            //标的_现金流计算规则信息 支付
            List<TargetCashCalcRule> cashCalcRuleListP = cashCalcRuleMapP.get(draw.getBusiId());
            if (CollectionUtils.isNotEmpty(cashCalcRuleListP)) {
                draw.setTargetCashCalcRuleListP(cashCalcRuleListP);
            }
            else {
                draw.setTargetCashCalcRuleListP(new ArrayList<>());
            }
            //标的_现金流支付规则信息
            List<TargetCashPayRule> cashPayRuleList = cashPayRuleMap.get(draw.getBusiId());
            if (CollectionUtils.isNotEmpty(cashPayRuleList)) {
            	draw.setTargetCashPayRuleList(cashPayRuleList);
            }
            else {
            	draw.setTargetCashPayRuleList(new ArrayList<>());
            }
            //标的_行权计划信息
            if (draw.getTargetFinProduct() != null) {
                List<FinExercisePlan> listFinExercisePlan = finExercisePlanMap.get(draw.getBusiId());
                if (listFinExercisePlan != null && !listFinExercisePlan.isEmpty()) {
                    String exerciseType = null;
                    if (DictCodeList.含权债摊销日_最近赎回行权日.getValue().equals(draw.getTargetFinProduct().getExeDate())) {
                        exerciseType = DictCodeList.行权方式_赎回.getValue();
                    } else if (DictCodeList.含权债摊销日_最近回售行权日.getValue().equals(draw.getTargetFinProduct().getExeDate())) {
                        exerciseType = DictCodeList.行权方式_回售.getValue();
                    }
                    for (FinExercisePlan plan : listFinExercisePlan) {
                        if (exerciseType == null || exerciseType.equals(plan.getExerciseType())) {
                            draw.setFinExercisePlan(plan);
                            break;
                        }
                    }
                }
            }
            //标的_附加信息
            FinProductAdd finProductAdd = finProductAddMap.get(draw.getBooksetId());
            if (finProductAdd != null) {
            	draw.setFinProductAdd(finProductAdd);
            }
            //账套金融产品估值参数
            BokFinProductValuationPara bokFinProductValuationPara = bokFinProductValuationParaMap.get(draw.getBooksetId() + "_" + draw.getBusiId() + "_" + InterfCommonConstant.分支序号_默认);
            if (bokFinProductValuationPara != null) {
            	draw.setBokFinProductValuationPara(bokFinProductValuationPara);
            }
            // 摊销重算配置
            List<BokAmoConfig> configList = bokAmoConfigMap.get(draw.getBusiId());
            List<BokAmoConfig> newList = new ArrayList<>();
            if (configList != null && !configList.isEmpty()) {
                for (BokAmoConfig bokAmoConfig : configList) {
                    if ("ALL".equals(draw.getBooksetId()) || bokAmoConfig.getBooksetId().equals(draw.getBooksetId())) {
                        newList.add(bokAmoConfig);
                    }
                }
                draw.setBokAmoConfigList(newList);
            }
            //区间到账应计利息
            List<InitTrdProductDealDto> intervalArrivalAccountList = intervalArrivalAccountMap.get(draw.getBooksetId() + "_" + draw.getBusiId());
            if (CollectionUtils.isNotEmpty(intervalArrivalAccountList)) {
            	draw.setIntervalArrivalAccountList(intervalArrivalAccountList);
            }
            else {
            	draw.setIntervalArrivalAccountList(new ArrayList<InitTrdProductDealDto>());
            }

            //金融产品标的信息
            List<TradeInfoDto> tradeInfoDtoList =new ArrayList<TradeInfoDto>();
            for(DrawBaseInfoSelectComDTO dto:draw.getListDrawBaseInfoSelectComDTO()){
                List<TradeInfoDto> list = tradeInfoMap.get(dto.getBusiId() + "_" + dto.getBusiDetailType()+"_"+dto.getBranch());
                if (CollectionUtils.isNotEmpty(list)) {
                    tradeInfoDtoList.addAll(list);
                }
            }

            if (CollectionUtils.isNotEmpty(tradeInfoDtoList)) {
                draw.setTradeInfoDtoList(tradeInfoDtoList);
            }
            else {
                draw.setTradeInfoDtoList(new ArrayList<TradeInfoDto>());
            }

        }
    }
}
