/*
 * 文件名：ValItemValueGenUtil.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：etliu
 * 修改时间：2019年1月13日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.common.util;

import com.joyintech.tams.common.base.dao.FinProductMapper;
import com.joyintech.tams.common.base.dao.FinProductTypeMapper;
import com.joyintech.tams.common.base.dao.MstCustomerInfoAddMapper;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.framework.util.CacheUtil;
import com.joyintech.framework.util.SpringContextUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessConstant;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.invest.common.dto.xmlrelation.XmlRelationInputParaDto;
import com.joyintech.tams.invest.common.dto.xmlrelation.XmlRelationOutputDto;
import com.joyintech.tams.invest.common.util.xmlrelation.XmlRelationUtil;

import java.util.ArrayList;
import java.util.List;

import org.thymeleaf.util.StringUtils;

/**
 * 估值核算相关字段值获取util
 *
 * @author etliu
 * @version 1.0 2019年1月13日
 * @see ValItemValueGenUtil
 * @since 1.0
 */
public class ValItemValueGenUtil {
    
    /**
     * 金融产品类型 投管分类
     */
    public static final String KEY_FINPROD_TYPE_TG = "FINPROD_TYPE_TG";
    
    /**
     * 资产一级分类
     */
    public static final String KEY_ASSET_FIRST_TYPE = "ASSET_FIRST_TYPE";
    
    /**
     * 资产二级分类
     */
    public static final String KEY_ASSET_SECOND_TYPE = "ASSET_SECOND_TYPE";
    
    /**
     * 是否境内外
     */
    public static final String KEY_YES_OR_NO = "YES_OR_NO";
    
    /**
     * 交易场所
     */
    public static final String KEY_TRADE_PLACE = "TRADE_PLACE";
    
    /**
     * 机构核算分类
     */
    public static final String KEY_ISSUER_HS_TYPE = "ISSUER_HS_TYPE";
    
    /**
     * 金融产品类型 估值分类
     */
    public static final String KEY_FINPROD_TYPE = "FINPROD_TYPE";
    
    /**
     * 估值 资产类型
     */
    public static final String KEY_TYPE1 = "TYPE1";
    
    /**
     * 估值 核算分类
     */
    public static final String KEY_ACCOUNTING_TYPE = "ACCOUNTING_TYPE";
    
    /**
     * 估值 税务分类
     */
    public static final String KEY_TAX_ASSET_TYPE = "TAX_ASSET_TYPE";

    /**
     * 根据金融产品交易实体，获取金融产品类型（估值核算）
     * 并设置到金融产品交易实体
     *
     * @param targetTalbleObj 金融产品交易实体
     * @see
     * @since 1.0
     */
    public static void genFinprodType(TrdProductDeal targetTalbleObj) {
        if (targetTalbleObj == null || StringUtil.isEmpty(targetTalbleObj.getFinprodType2())) {
            return;
        }
        
        // 金融产品类型（投管分类）
        String finprodType2 = targetTalbleObj.getFinprodType2();
        // 交易场所
        String tradeMarket = targetTalbleObj.getTradeMarket();
        // 金融产品代码
        String finprodId = targetTalbleObj.getFinprodId();
        
        String FINPROD_TYPE = genFinprodType(finprodType2, finprodId, tradeMarket);
        if (StringUtil.isNotEmpty(FINPROD_TYPE)) {
            targetTalbleObj.setFinprodType(FINPROD_TYPE);
        }
    }
    
    /**
     * 根据交易类金融产品表实体，获取金融产品类型（估值核算）
     * 并设置到交易类金融产品表
     *
     * @param targetTalbleObj 交易类金融产品表实体
     * @see
     * @since 1.0
     */
    public static void genFinprodType(FinTradeProduct targetTalbleObj) {
        if (targetTalbleObj == null || StringUtil.isEmpty(targetTalbleObj.getFinprodType2())) {
            return;
        }
        
        // 金融产品类型（投管分类）
        String finprodType2 = targetTalbleObj.getFinprodType2();
        // 交易场所
        String tradeMarket = targetTalbleObj.getTradeMarket();
        
        // 金融产品代码
        String finprodId = targetTalbleObj.getFinprodId();
        
        String FINPROD_TYPE = genFinprodType(finprodType2, finprodId, tradeMarket);
        targetTalbleObj.setFinprodType(FINPROD_TYPE);
    }
    
    /**
     * 根据标的类金融产品表实体，获取金融产品类型（估值核算）
     * 并设置到标的类金融产品表
     *
     * @param finProduct 标的类金融产品表实体
     * @param finProductType 金融产品分类表
     * @see
     * @since 1.0
     */
    public static void genFinprodTypeForFinProduct(FinProduct finProduct, FinProductType finProductType) {
        if (finProduct == null) {
            return;
        }
        
        // 金融产品类型（投管分类）
        String finprodType2 = finProduct.getFinprodType2();
        // 资产一级分类
        String assetFirstType = null;
        // 资产二级分类
        String assetSecondType = null;
        if (finProductType != null) {
            assetFirstType = finProductType.getType6();
            assetSecondType = finProductType.getType7();
        }
        // 交易场所
        String tradeMarket = finProduct.getTradeMarket();
        // 境内外
        String blnArea = finProduct.getBlnArea();
        
        // 存款类资产需要机构核算分类维度，特殊处理
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_存款.getValue(), finprodType2)) {
            // 机构核算分类(默认01，根据存/借款机构配置的机构核算分类)
            String ISSUER_HS_TYPE = genIssuerHsTypeOrgCode(finProduct.getIssuer());
            String FINPROD_TYPE = genFinprodTypeByItemValue(finprodType2, assetFirstType, assetSecondType, tradeMarket, blnArea, ISSUER_HS_TYPE);
            if (StringUtil.isNotEmpty(FINPROD_TYPE)) {
                finProduct.setFinprodType(FINPROD_TYPE);
            }
            return;
        }
        
        String FINPROD_TYPE = genFinprodTypeByItemValue(finprodType2, assetFirstType, assetSecondType, tradeMarket, blnArea);
        if (StringUtil.isNotEmpty(FINPROD_TYPE)) {
            finProduct.setFinprodType(FINPROD_TYPE);
        }
    }
    
    /**
     * 根据现金流支付规则表实体，获取金融产品类型（估值核算）
     * 并设置到现金流支付规则表实体
     *
     * @param targetTalbleObj 现金流支付规则表
     * @see
     * @since 1.0
     */
    public static void genFinprodType(FinCashPayRule targetTalbleObj) {
        if (targetTalbleObj == null || StringUtil.isEmpty(targetTalbleObj.getFinprodType2())) {
            return;
        }
        
        // 金融产品类型（投管分类）
        String finprodType2 = targetTalbleObj.getFinprodType2();
        // 交易场所
        String tradeMarket = null;
        // 金融产品代码
        String finprodId = targetTalbleObj.getFinprodId();
        
        String FINPROD_TYPE = genFinprodType(finprodType2, finprodId, tradeMarket);
        targetTalbleObj.setFinprodType(FINPROD_TYPE);
    }
    
    /**
     * 根据现金流计算规则表实体，获取金融产品类型（估值核算）
     * 并设置到现金流计算规则表实体
     *
     * @param targetTalbleObj 现金流支付规则表
     * @see
     * @since 1.0
     */
    public static void genFinprodType(FinCashCalcRule targetTalbleObj) {
        if (targetTalbleObj == null || StringUtil.isEmpty(targetTalbleObj.getFinprodType2())) {
            return;
        }
        
        // 金融产品类型（投管分类）
        String finprodType2 = targetTalbleObj.getFinprodType2();
        // 交易场所
        String tradeMarket = null;
        // 金融产品代码
        String finprodId = targetTalbleObj.getFinprodId();
        
        String FINPROD_TYPE = genFinprodType(finprodType2, finprodId, tradeMarket);
        targetTalbleObj.setFinprodType(FINPROD_TYPE);
    }
    
    /**
     * 根据场外金融产品估值表实体，获取金融产品类型（估值核算）
     * 并设置到场外金融产品估值表实体
     *
     * @param targetTalbleObj 场外金融产品估值表
     * @see
     * @since 1.0
     */
    public static void genFinprodType(FinProductValuation targetTalbleObj) {
        // 代码中已设置值，不用转换
        if (StringUtil.isEmpty(targetTalbleObj.getFinprodType())) {
            targetTalbleObj.setFinprodType(targetTalbleObj.getFinprodType2());
        }
        return;
//        if (targetTalbleObj == null || StringUtil.isEmpty(targetTalbleObj.getFinprodType2())) {
//            return;
//        }
//
//        // 金融产品类型（投管分类）
//        String finprodType2 = targetTalbleObj.getFinprodType2();
//        // 交易场所
//        String tradeMarket = null;
//        // 金融产品代码
//        String finprodId = targetTalbleObj.getFinprodId();
//
//        String FINPROD_TYPE = genFinprodType(finprodType2, finprodId, tradeMarket);
//        targetTalbleObj.setFinprodType(FINPROD_TYPE == null ? targetTalbleObj.getFinprodType() : FINPROD_TYPE);
    }
    
    /**
     * 根据金融产品账户表实体，获取金融产品类型（估值核算）
     * 并设置到金融产品账户表实体
     *
     * @param targetTalbleObj 金融产品账户表
     * @see
     * @since 1.0
     */
    public static void genFinprodType(StlFinProductAcct targetTalbleObj) {
        if (targetTalbleObj == null || StringUtil.isEmpty(targetTalbleObj.getFinprodType2())) {
            return;
        }
        
        // 金融产品类型（投管分类）
        String finprodType2 = targetTalbleObj.getFinprodType2();
        // 交易场所
        String tradeMarket = null;
        // 金融产品代码
        String finprodId = targetTalbleObj.getFinprodId();
        
        String FINPROD_TYPE = genFinprodType(finprodType2, finprodId, tradeMarket);
        targetTalbleObj.setFinprodType(FINPROD_TYPE);
    }
    
    /**
     * 根据金融产品交易汇总表实体，获取金融产品类型（估值核算）
     * 并设置到金融产品交易汇总表实体
     *
     * @param targetTalbleObj 金融产品交易汇总表实体
     * @see
     * @since 1.0
     */
    public static void genFinprodType(TrdFinProductDealTotal targetTalbleObj) {
        if (targetTalbleObj == null || StringUtil.isEmpty(targetTalbleObj.getFinprodType2())) {
            return;
        }
        
        // 金融产品类型（投管分类）
        String finprodType2 = targetTalbleObj.getFinprodType2();
        // 交易场所
        String tradeMarket = targetTalbleObj.getTradeMarket();
        // 金融产品代码
        String finprodId = targetTalbleObj.getFinprodId();
        
        String FINPROD_TYPE = genFinprodType(finprodType2, finprodId, tradeMarket);
        targetTalbleObj.setFinprodType(FINPROD_TYPE);
    }
    
    /**
     * 根据行权计划表实体，获取金融产品类型（估值核算）
     * 并设置到行权计划表实体
     *
     * @param targetTalbleObj 行权计划表
     * @see
     * @since 1.0
     */
    public static void genFinprodType(FinExercisePlan targetTalbleObj) {
        if (targetTalbleObj == null || StringUtil.isEmpty(targetTalbleObj.getFinprodType2())) {
            return;
        }
        
        // 金融产品类型（投管分类）
        String finprodType2 = targetTalbleObj.getFinprodType2();
        // 交易场所
        String tradeMarket = null;
        // 金融产品代码
        String finprodId = targetTalbleObj.getFinprodId();
        
        String FINPROD_TYPE = genFinprodType(finprodType2, finprodId, tradeMarket);
        targetTalbleObj.setFinprodType(FINPROD_TYPE);
    }
    
    /**
     * 根据现金流计划表实体，获取金融产品类型（估值核算）
     * 并设置到现金流计划表实体
     *
     * @param targetTalbleObj 现金流计划表
     * @see
     * @since 1.0
     */
    public static void genFinprodType(FinCashPlan targetTalbleObj) {
        if (targetTalbleObj == null || StringUtil.isEmpty(targetTalbleObj.getFinprodType2())) {
            return;
        }
        
        // 金融产品类型（投管分类）
        String finprodType2 = targetTalbleObj.getFinprodType2();
        // 交易场所
        String tradeMarket = null;
        // 金融产品代码
        String finprodId = targetTalbleObj.getFinprodId();
        
        String FINPROD_TYPE = genFinprodType(finprodType2, finprodId, tradeMarket);
        targetTalbleObj.setFinprodType(FINPROD_TYPE);
    }
    
    /**
     * 根据行权规则表实体，获取金融产品类型（估值核算）
     * 并设置到行权规则表实体
     *
     * @param targetTalbleObj 行权规则表
     * @see
     * @since 1.0
     */
    public static void genFinprodType(FinExerciseRule targetTalbleObj) {
        if (targetTalbleObj == null || StringUtil.isEmpty(targetTalbleObj.getFinprodType2())) {
            return;
        }
        
        // 金融产品类型（投管分类）
        String finprodType2 = targetTalbleObj.getFinprodType2();
        // 交易场所
        String tradeMarket = null;
        // 金融产品代码
        String finprodId = targetTalbleObj.getFinprodId();
        
        String FINPROD_TYPE = genFinprodType(finprodType2, finprodId, tradeMarket);
        targetTalbleObj.setFinprodType(FINPROD_TYPE);
    }
    
    /**
     * 根据交易类金融产品附属表实体，获取金融产品类型（估值核算）
     * 并设置到交易类金融产品附属表
     *
     * @param targetTalbleObj 交易类金融产品附属表实体
     * @see
     * @since 1.0
     */
    public static void genFinprodType(FinTradeProductAdd targetTalbleObj) {
        if (targetTalbleObj == null || StringUtil.isEmpty(targetTalbleObj.getFinprodType2())) {
            return;
        }

        // 金融产品类型（投管分类）
        String finprodType2 = targetTalbleObj.getFinprodType2();
        // 交易场所
        String tradeMarket = null;
        // 金融产品代码
        String finprodId = targetTalbleObj.getFinprodId();
        
        //add by wangpeng 20190822 start
        //债券质押式回购 对应估值分类  （附属表没有交易场所，无法映射 特殊处理）
        if((StringUtil.equals(finprodType2, DictCodeListExt1.金融产品类型_债券质押式正回购.getValue()))
        		||(StringUtil.equals(finprodType2, DictCodeListExt1.金融产品类型_债券质押式逆回购.getValue()))){
        	targetTalbleObj.setFinprodType(finprodType2);
        }
        else{
        String FINPROD_TYPE = genFinprodType(finprodType2, finprodId, tradeMarket);
        targetTalbleObj.setFinprodType(FINPROD_TYPE);
    }
        //add by wangpeng 20190822 end
    
    }
    
    /**
     * 根据现金流利率信息表实体，获取金融产品类型（估值核算）
     * 并设置到现金流利率信息表
     *
     * @param targetTalbleObj 现金流利率信息表
     * @see
     * @since 1.0
     */
    public static void genFinprodType(FinRateInfo targetTalbleObj) {
        if (targetTalbleObj == null || StringUtil.isEmpty(targetTalbleObj.getFinprodType2())) {
            return;
        }

        // 金融产品类型（投管分类）
        String finprodType2 = targetTalbleObj.getFinprodType2();
        // 交易场所
        String tradeMarket = null;
        // 金融产品代码
        String finprodId = targetTalbleObj.getFinprodId();
        
        String FINPROD_TYPE = genFinprodType(finprodType2, finprodId, tradeMarket);
        targetTalbleObj.setFinprodType(FINPROD_TYPE);
    }
    
    /**
     * 根据金融产品交易汇总业务流程表实体，获取金融产品类型（估值核算）
     * 并设置到金融产品交易汇总业务流程表
     *
     * @param targetTalbleObj 金融产品交易汇总业务流程表
     * @see
     * @since 1.0
     */
    public static void genFinprodType(WflTrdFinProductDealTotal targetTalbleObj) {
        if (targetTalbleObj == null || StringUtil.isEmpty(targetTalbleObj.getFinprodType2())) {
            return;
        }

        // 金融产品类型（投管分类）
        String finprodType2 = targetTalbleObj.getFinprodType2();
        // 交易场所
        String tradeMarket = null;
        // 金融产品代码
        String finprodId = targetTalbleObj.getFinprodId();
        
        String FINPROD_TYPE = genFinprodType(finprodType2, finprodId, tradeMarket);
        targetTalbleObj.setFinprodType(FINPROD_TYPE);
    }
    
    /**
     * 根据金融产品交易业务流程表实体，获取金融产品类型（估值核算）
     * 并设置到金融产品交易业务流程表
     *
     * @param targetTalbleObj 金融产品交易业务流程表
     * @see
     * @since 1.0
     */
    public static void genFinprodType(WflTrdProductDeal targetTalbleObj) {
        if (targetTalbleObj == null || StringUtil.isEmpty(targetTalbleObj.getFinprodType2())) {
            return;
        }

        // 金融产品类型（投管分类）
        String finprodType2 = targetTalbleObj.getFinprodType2();
        // 交易场所
        String tradeMarket = null;
        // 金融产品代码
        String finprodId = targetTalbleObj.getFinprodId();
        
        String FINPROD_TYPE = genFinprodType(finprodType2, finprodId, tradeMarket);
        if (StringUtil.isNotEmpty(FINPROD_TYPE)) {
            targetTalbleObj.setFinprodType(FINPROD_TYPE);
        }
    }
    
    /**
     * 金融产品分类表的分类1-分类3
     *
     * @param finProduct 标的类金融产品
     * @param finProductType 金融产品分类表
     * @see
     * @since 1.0
     */
    public static void genTypeForFinProductType(FinProduct finProduct, FinProductType finProductType) {
        if (finProductType == null) {
            return;
        }

        // 金融产品类型（投管分类）
        String finprodType2 = finProduct.getFinprodType2();
        // 资产一级分类
        String assetFirstType = null;
        // 资产二级分类
        String assetSecondType = null;
        if (finProductType != null) {
            assetFirstType = finProductType.getType6();
            assetSecondType = finProductType.getType7();
        }
        // 交易场所
        String tradeMarket = finProduct.getTradeMarket();
        // 境内外
        String blnArea = finProduct.getBlnArea();
        
        XmlRelationOutputDto outputDto = genResultByItemValue(finprodType2, assetFirstType, assetSecondType, tradeMarket, blnArea);
        
        // 存款类资产需要机构核算分类维度，特殊处理
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_存款.getValue(), finprodType2)) {
            // 机构核算分类(默认01，根据存/借款机构配置的机构核算分类)
            String ISSUER_HS_TYPE = genIssuerHsTypeOrgCode(finProduct.getIssuer());
            outputDto = genResultByItemValue(finprodType2, assetFirstType, assetSecondType, tradeMarket, blnArea, ISSUER_HS_TYPE);
        }
        
        if (StringUtil.isBlank(finProductType.getType1())) {
            finProductType.setType1(XmlRelationUtil.getVALOutPutResultItemCode(outputDto, KEY_TYPE1));
        }
        if (StringUtil.isBlank(finProductType.getType2())) {
            finProductType.setType2(XmlRelationUtil.getVALOutPutResultItemCode(outputDto, KEY_ACCOUNTING_TYPE));
        }
        if (StringUtil.isBlank(finProductType.getType3())) {
            finProductType.setType3(XmlRelationUtil.getVALOutPutResultItemCode(outputDto, KEY_TAX_ASSET_TYPE));
        }
    }
    
    /**
     * 金融产品分类表的分类1-分类3
     *
     * @param finTradeProduct 交易类金融产品表
     * @param finProductType 金融产品分类表
     * @see
     * @since 1.0
     */
    public static void genTypeForFinProductType(FinTradeProduct finTradeProduct, FinProductType finProductType) {
        if (finProductType == null) {
            return;
        }
        
        // 金融产品类型（投管分类）
        String finprodType2 = finTradeProduct.getFinprodType2();
        // 交易场所
        String tradeMarket = finTradeProduct.getTradeMarket();
        
        // 金融产品代码
        String finprodId = finTradeProduct.getFinprodId();
        
        XmlRelationOutputDto outputDto = genResult(finprodType2, finprodId, tradeMarket);
        
        if (StringUtil.isBlank(finProductType.getType1())) {
            finProductType.setType1(XmlRelationUtil.getVALOutPutResultItemCode(outputDto, KEY_TYPE1));
        }
        if (StringUtil.isBlank(finProductType.getType2())) {
            finProductType.setType2(XmlRelationUtil.getVALOutPutResultItemCode(outputDto, KEY_ACCOUNTING_TYPE));
        }
        if (StringUtil.isBlank(finProductType.getType3())) {
            finProductType.setType3(XmlRelationUtil.getVALOutPutResultItemCode(outputDto, KEY_TAX_ASSET_TYPE));
        }
    }
    
    /**
     * 根据金融产品交易实体，获取业务类型
     * 并设置到金融产品交易实体
     *
     * @param targetTalbleObj 金融产品交易实体
     * @see
     * @since 1.0
     */
    public static void genBusiType(TrdProductDeal targetTalbleObj) {
        if (targetTalbleObj == null || StringUtil.isEmpty(targetTalbleObj.getFinprodType2())) {
            return;
        }
        
        // 金融产品类型（投管分类）
        String finprodType2 = targetTalbleObj.getFinprodType2();
        // 金融产品代码
        String finprodId = targetTalbleObj.getFinprodId();
        // 交易方向
        String ps = targetTalbleObj.getPs();
        //投资目的
        String invAim = targetTalbleObj.getInvAim();
        //20191020新增         浮动管理费挂账交易设置busiType34     净值型周期型需求
        if(!DictCodeListExt2.业务类型_浮动管理费挂账交易.getValue().equals(targetTalbleObj.getBusiType())) {
        	String busiType = genBusiType(finprodType2, finprodId, ps);
//        	处理债券存续期融投资目的为卖空时直接登录业务类型为29
        	if(StringUtil.isNotEmpty(invAim)&& StringUtil.equals(DictCodeListExt1.投资目的_卖空.getValue(),invAim)){
                busiType = DictCodeList.业务类型_债券借贷标的债券交易.getValue();
            }
        	if(StringUtil.equals(targetTalbleObj.getBusiType(), DictCodeList.业务类型_逾期交易.getValue())){
        		busiType = DictCodeList.业务类型_逾期交易.getValue();
        	}
            if (StringUtil.isNotEmpty(busiType)) {
                targetTalbleObj.setBusiType(busiType);
            }
        }
    }
    
    /**
     * 根据金融产品交易业务流程实体，获取业务类型
     * 并设置到金融产品交易业务流程实体
     *
     * @param targetTalbleObj 金融产品交易业务流程实体
     * @see
     * @since 1.0
     */
    public static void genBusiType(WflTrdProductDeal targetTalbleObj) {
        if (targetTalbleObj == null || StringUtil.isEmpty(targetTalbleObj.getFinprodType2())) {
            return;
        }
        
        // 金融产品类型（投管分类）
        String finprodType2 = targetTalbleObj.getFinprodType2();
        // 金融产品代码
        String finprodId = targetTalbleObj.getFinprodId();
        // 交易方向
        String ps = targetTalbleObj.getPs();
//        投资目的
        String invAim = targetTalbleObj.getInvAim();
        String busiType = genBusiType(finprodType2, finprodId, ps);
        //        	处理债券存续期融投资目的为卖空时直接登录业务类型为29
        if(StringUtil.isNotEmpty(invAim)&& StringUtil.equals(DictCodeListExt1.投资目的_卖空.getValue(),invAim)){
            busiType = DictCodeList.业务类型_债券借贷标的债券交易.getValue();
        }
        if(StringUtil.equals(targetTalbleObj.getBusiType(), DictCodeList.业务类型_逾期交易.getValue())){
    		busiType = DictCodeList.业务类型_逾期交易.getValue();
    	}
        if (StringUtil.isNotEmpty(busiType)) {
            targetTalbleObj.setBusiType(busiType);
        }
    }
    
    
    // 以下为私有方法------------------------------------------------------------------------------
    /**
     * 根据金融产品类型（投管分类）和金融产品代码获取金融产品类型
     *
     * @param finprodType2 金融产品类型（投管分类）
     * @param finprodId 金融产品代码
     * @param tradeMarket 交易场所
     * @return 金融产品类型
     * @see
     * @since 1.0
     */
    private static String genFinprodType(String finprodType2, String finprodId, String tradeMarket) {
        // 资产一级分类
        String assetFirstType = null;
        // 资产二级分类
        String assetSecondType = null;
        // 境内外
        String blnArea = null;
        
        // 根据金融产品代码查询标的类金融产品
        FinProductMapper finProductMapper = SpringContextUtil.getBean(FinProductMapper.class);
        FinProduct finProduct = finProductMapper.selectByPrimaryKey(finprodId);
        if (finProduct != null) {
            
            // 特殊处理：理财子产品（F26）不能用标的的分类，需要重新映射
            if (StringUtil.isNotBlank(finProduct.getFinprodType())
                && !StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_理财子产品.getValue(), finprodType2)
                && !StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_固定管理费.getValue(), finprodType2)
                && !StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_浮动管理费.getValue(), finprodType2)
                && !StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_托管费.getValue(), finprodType2)
                && !StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_投资顾问费.getValue(), finprodType2)
                && !StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_销售费.getValue(), finprodType2)
                && !StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_税额.getValue(), finprodType2)
                && !StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_代销费.getValue(), finprodType2)
                && !StringUtil.equals(DictCodeListExt1.一次性费用类型_投管_交易手续费.getValue(), finprodType2)
                && !StringUtil.equals(DictCodeListExt1.一次性费用类型_结算手续费.getValue(), finprodType2)
                && !StringUtil.equals(DictCodeListExt1.一次性费用类型_账户维护费.getValue(), finprodType2)
                && !StringUtil.equals(DictCodeListExt1.一次性费用类型_其他费用.getValue(), finprodType2)
                && !StringUtil.equals(DictCodeListExt1.一次性费用类型_通道费.getValue(), finprodType2)
                && !StringUtil.equals(DictCodeListExt1.现金流二级类型_外包服务费.getValue(), finprodType2)
                && !StringUtil.equals(DictCodeListExt1.一次性费用类型_佣金.getValue(), finprodType2)
                && !StringUtil.equals(DictCodeListExt1.金融产品类型_交易费用.getValue(), finprodType2)
                && !StringUtil.equals(DictCodeListExt1.金融产品类型_预提费用.getValue(), finprodType2)
                ) {
                return finProduct.getFinprodType();
            }

            // 交易场所
            if (StringUtil.isBlank(tradeMarket)) {
                tradeMarket = finProduct.getTradeMarket();
            }
            // 境内外
            blnArea = finProduct.getBlnArea();
        }
        
        // 根据金融产品代码查询金融产品分类表
        FinProductTypeMapper finProductTypeMapper = SpringContextUtil.getBean(FinProductTypeMapper.class);
        FinProductTypeKey finProductTypeKey = new FinProductTypeKey();
        finProductTypeKey.setFinprodId(finprodId);
        // 分支序号
        Long branchLong = 0L;
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        if (StringUtil.isNotBlank(branch)) {
            try {
                branchLong = Long.valueOf(branch);
            }
            catch (Exception e) {
            }
        }
        finProductTypeKey.setBranch(branchLong);
        FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(finProductTypeKey);
        if (finProductType != null) {
            // 资产一级分类
            assetFirstType = finProductType.getType6();
            // 资产二级分类
            assetSecondType = finProductType.getType7();
        }
        if (finProductType != null) {
            // 存款类资产需要机构核算分类维度，特殊处理
            if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_存款.getValue(), finprodType2)) {
                // 机构核算分类(默认01，根据存/借款机构配置的机构核算分类)
                String ISSUER_HS_TYPE = genIssuerHsTypeOrgCode(finProduct.getIssuer());
                String FINPROD_TYPE = genFinprodTypeByItemValue(finprodType2, assetFirstType, assetSecondType, tradeMarket, blnArea, ISSUER_HS_TYPE);
                finProduct.setFinprodType(FINPROD_TYPE);
                return FINPROD_TYPE;
            }
        }
        
        String FINPROD_TYPE = genFinprodTypeByItemValue(finprodType2, assetFirstType, assetSecondType, tradeMarket, blnArea);
        return FINPROD_TYPE;
    }
    
    /**
     * 根据金融产品类型（投管分类）和金融产品代码获取映射结果
     *
     * @param finprodType2 金融产品类型（投管分类）
     * @param finprodId 金融产品代码
     * @param tradeMarket 交易场所
     * @return 映射结果
     * @see
     * @since 1.0
     */
    private static XmlRelationOutputDto genResult(String finprodType2, String finprodId, String tradeMarket) {
        // 资产一级分类
        String assetFirstType = null;
        // 资产二级分类
        String assetSecondType = null;
        // 境内外
        String blnArea = null;
        
        // 根据金融产品代码查询标的类金融产品
        FinProductMapper finProductMapper = SpringContextUtil.getBean(FinProductMapper.class);
        FinProduct finProduct = finProductMapper.selectByPrimaryKey(finprodId);
        if (finProduct != null) {

            // 交易场所
            if (StringUtil.isBlank(tradeMarket)) {
                tradeMarket = finProduct.getTradeMarket();
            }
            // 境内外
            blnArea = finProduct.getBlnArea();
        }
        
        // 根据金融产品代码查询金融产品分类表
        FinProductTypeMapper finProductTypeMapper = SpringContextUtil.getBean(FinProductTypeMapper.class);
        FinProductTypeKey finProductTypeKey = new FinProductTypeKey();
        finProductTypeKey.setFinprodId(finprodId);
        // 分支序号
        Long branchLong = 0L;
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        if (StringUtil.isNotBlank(branch)) {
            try {
                branchLong = Long.valueOf(branch);
            }
            catch (Exception e) {
            }
        }
        finProductTypeKey.setBranch(branchLong);
        FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(finProductTypeKey);
        if (finProductType != null) {
            // 资产一级分类
            assetFirstType = finProductType.getType6();
            // 资产二级分类
            assetSecondType = finProductType.getType7();
        }
        
        // 调用共通获取金融产品类型（估值核算）
        List<XmlRelationInputParaDto> inputParamList = genInputParamList(finprodType2, assetFirstType, assetSecondType, tradeMarket, blnArea);
        
        return XmlRelationUtil.getOutPut(inputParamList);
    }
    
    /**
     * 获取映射关系参数list
     *
     * @param finprodType2 金融产品类型（投管分类）
     * @param assetFirstType 资产一级分类
     * @param assetSecondType 资产二级分类
     * @param tradeMarket 交易场所
     * @param blnArea 境内外
     * @return 映射关系参数list
     * @see
     * @since 1.0
     */
    private static List<XmlRelationInputParaDto> genInputParamList(String finprodType2, String assetFirstType, String assetSecondType, String tradeMarket, String blnArea) {
        // 特殊场景处理
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_存款.getValue(), finprodType2)) {
            assetSecondType = null;
            tradeMarket = null;
            blnArea = null;
        }
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_拆出.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_拆入.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_特拆拆入.getValue(), finprodType2)) {
            assetFirstType = null;
            assetSecondType = null;
            tradeMarket = null;
            blnArea = null;
        }
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_基金.getValue(), finprodType2)) {
            //update by  zhangnian  20191127 start
            //tradeMarket = null;
            //update by  zhangnian  20191127 end
        }
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue(), finprodType2)) {
            assetSecondType = null;
            tradeMarket = null;
        }
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue(), finprodType2)) {
            assetFirstType = null;
            assetSecondType = null;
            blnArea = null;
        }
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue(), finprodType2)
                || StringUtil.equals(DictCodeListExt3.金融产品类型_投管用_权益类资产.getValue(), finprodType2)) {
            assetSecondType = null;
            tradeMarket = null;
        }
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_理财子产品.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_固定管理费.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_浮动管理费.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_托管费.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_投资顾问费.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_销售费.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_税额.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_代销费.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.一次性费用类型_投管_交易手续费.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.一次性费用类型_结算手续费.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.一次性费用类型_账户维护费.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.一次性费用类型_其他费用.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_资金账户.getValue(), finprodType2)) {
            assetFirstType = null;
            assetSecondType = null;
            tradeMarket = null;
            blnArea = null;
        }
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue(), finprodType2)
            && StringUtil.equals(DictCodeList.WIND一级分类_含债券一级分类等_同业存单.getValue(), assetFirstType)) {
            assetSecondType = null;
        }
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue(), finprodType2)
                || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue(), finprodType2)) {
                assetSecondType = null;
        }

        // 境内外转化
        if (StringUtil.equals(DictCodeList.境内外_境内.getValue(), blnArea)) {
            blnArea = DictCodeList.是否_是.getValue();
        }
        else if (StringUtil.equals(DictCodeList.境内外_境外.getValue(), blnArea)) {
            blnArea = DictCodeList.是否_否.getValue();
        }
        
        // 调用共通获取金融产品类型（估值核算）
        List<XmlRelationInputParaDto> inputParamList = new ArrayList<XmlRelationInputParaDto>();
        
        // 金融产品类型（投管分类）
        XmlRelationInputParaDto inputParaDto = new XmlRelationInputParaDto();
        inputParaDto.setDictCode(KEY_FINPROD_TYPE_TG);
        inputParaDto.setItemCode(finprodType2);
        inputParamList.add(inputParaDto);
        
        // 资产一级分类
        if (StringUtil.isNotBlank(assetFirstType)) {
            inputParaDto = new XmlRelationInputParaDto();
            inputParaDto.setDictCode(KEY_ASSET_FIRST_TYPE);
            inputParaDto.setItemCode(assetFirstType);
            inputParamList.add(inputParaDto);
        }
        
        // 资产二级分类
        if (StringUtil.isNotBlank(assetSecondType)) {
            inputParaDto = new XmlRelationInputParaDto();
            inputParaDto.setDictCode(KEY_ASSET_SECOND_TYPE);
            inputParaDto.setItemCode(assetSecondType);
            inputParamList.add(inputParaDto);
        }
        
        // 交易场所
        if (StringUtil.isNotBlank(tradeMarket)) {
            inputParaDto = new XmlRelationInputParaDto();
            inputParaDto.setDictCode(KEY_TRADE_PLACE);
            inputParaDto.setItemCode(tradeMarket);
            inputParamList.add(inputParaDto);
        }
        
        // 境内外
        if (StringUtil.isNotBlank(blnArea)) {
            inputParaDto = new XmlRelationInputParaDto();
            inputParaDto.setDictCode(KEY_YES_OR_NO);
            inputParaDto.setItemCode(blnArea);
            inputParamList.add(inputParaDto);
        }
        
        return inputParamList;
    }

    /**
     * 根据参数获取金融产品类型
     *
     * @param finprodType2 金融产品类型（投管分类）
     * @param assetFirstType 资产一级分类
     * @param assetSecondType 资产二级分类
     * @param tradeMarket 交易场所
     * @param blnArea 境内外
     * @return 金融产品类型
     * @see
     * @since 1.0
     */
    private static String genFinprodTypeByItemValue(String finprodType2, String assetFirstType, String assetSecondType, String tradeMarket, String blnArea) {
        // 调用共通获取金融产品类型（估值核算）
        List<XmlRelationInputParaDto> inputParamList = genInputParamList(finprodType2, assetFirstType, assetSecondType, tradeMarket, blnArea);
        String FINPROD_TYPE = XmlRelationUtil.getVALOutPutResultItemCode(inputParamList, KEY_FINPROD_TYPE);
        
        return FINPROD_TYPE;
    }

    /**
     * 根据参数获取金融产品类型
     *
     * @param finprodType2 金融产品类型（投管分类）
     * @param assetFirstType 资产一级分类
     * @param assetSecondType 资产二级分类
     * @param tradeMarket 交易场所
     * @param blnArea 境内外
     * @param issuerHsTypeOrgCode 机构核算分类机构代码
     * @return 金融产品类型
     * @see
     * @since 1.0
     */
    private static String genFinprodTypeByItemValue(String finprodType2, String assetFirstType, String assetSecondType, String tradeMarket, String blnArea, String issuerHsTypeOrgCode) {
        // 调用共通获取金融产品类型（估值核算）
        List<XmlRelationInputParaDto> inputParamList = genInputParamList(finprodType2, assetFirstType, assetSecondType, tradeMarket, blnArea);
        if (StringUtil.isNotBlank(issuerHsTypeOrgCode)) {
            // 机构核算分类机构代码
            XmlRelationInputParaDto inputParaDto = new XmlRelationInputParaDto();
            inputParaDto.setDictCode(KEY_ISSUER_HS_TYPE);
            inputParaDto.setItemCode(issuerHsTypeOrgCode);
            inputParamList.add(inputParaDto);
        }
        String FINPROD_TYPE = XmlRelationUtil.getVALOutPutResultItemCode(inputParamList, KEY_FINPROD_TYPE);
        
        return FINPROD_TYPE;
    }
    
    /**
     * 根据存/借款机构获取对应机构核算分类机构代码
     *
     * @param issuer
     * @return
     * @see
     * @since 1.0
     */
    private static String genIssuerHsTypeOrgCode(String issuer) {
        String issuerHsTypeOrgCode = "01";
        
        MstCustomerInfoAddMapper mstCustomerInfoAddMapper = SpringContextUtil.getBean(MstCustomerInfoAddMapper.class);
        MstCustomerInfoAdd mstCustomerInfoAdd = mstCustomerInfoAddMapper.selectByPrimaryKey(issuer);
        if (mstCustomerInfoAdd != null) {
            issuerHsTypeOrgCode = mstCustomerInfoAdd.getHsType();
        }
        if (StringUtils.isEmpty(issuerHsTypeOrgCode)) {
        	issuerHsTypeOrgCode = "01";
        }
        return issuerHsTypeOrgCode;
    }
    
    /**
     * 根据参数获取映射结果
     *
     * @param finprodType2 金融产品类型（投管分类）
     * @param assetFirstType 资产一级分类
     * @param assetSecondType 资产二级分类
     * @param tradeMarket 交易场所
     * @param blnArea 境内外
     * @return 映射结果
     * @see
     * @since 1.0
     */
    private static XmlRelationOutputDto genResultByItemValue(String finprodType2, String assetFirstType, String assetSecondType, String tradeMarket, String blnArea) {
        // 调用共通获取金融产品类型（估值核算）
        List<XmlRelationInputParaDto> inputParamList = genInputParamList(finprodType2, assetFirstType, assetSecondType, tradeMarket, blnArea);
        return XmlRelationUtil.getOutPut(inputParamList);
    }
    
    /**
     * 根据参数获取映射结果
     *
     * @param finprodType2 金融产品类型（投管分类）
     * @param assetFirstType 资产一级分类
     * @param assetSecondType 资产二级分类
     * @param tradeMarket 交易场所
     * @param blnArea 境内外
     * @param issuerHsTypeOrgCode 机构核算分类机构代码
     * @return 映射结果
     * @see
     * @since 1.0
     */
    private static XmlRelationOutputDto genResultByItemValue(String finprodType2, String assetFirstType, String assetSecondType, String tradeMarket, String blnArea, String issuerHsTypeOrgCode) {
        // 调用共通获取金融产品类型（估值核算）
        List<XmlRelationInputParaDto> inputParamList = genInputParamList(finprodType2, assetFirstType, assetSecondType, tradeMarket, blnArea);
        if (StringUtil.isNotBlank(issuerHsTypeOrgCode)) {
            // 机构核算分类机构代码
            XmlRelationInputParaDto inputParaDto = new XmlRelationInputParaDto();
            inputParaDto.setDictCode(KEY_ISSUER_HS_TYPE);
            inputParaDto.setItemCode(issuerHsTypeOrgCode);
            inputParamList.add(inputParaDto);
        }
        return XmlRelationUtil.getOutPut(inputParamList);
    }
    
    /**
     * 获取业务类型
     *
     * @param finprodType2 金融产品类型（投管分类）
     * @param finprodId 金融产品代码
     * @param ps 交易方向
     * @see
     * @since 1.0
     */
    private static String genBusiType(String finprodType2, String finprodId, String ps) {
     // 根据金融产品代码查询标的类金融产品
        FinProductMapper finProductMapper = SpringContextUtil.getBean(FinProductMapper.class);
        FinProduct finProduct = finProductMapper.selectByPrimaryKey(finprodId);
        if (finProduct == null) {
            finProduct = new FinProduct();
        }
        
        // 根据金融产品代码查询金融产品分类表
        FinProductTypeMapper finProductTypeMapper = SpringContextUtil.getBean(FinProductTypeMapper.class);
        FinProductTypeKey finProductTypeKey = new FinProductTypeKey();
        finProductTypeKey.setFinprodId(finprodId);
        // 分支序号
        Long branchLong = 0L;
        String branch = CacheUtil.getSysParam(BussinessConstant.NOBRANCHNOVALUE);
        if (StringUtil.isNotBlank(branch)) {
            try {
                branchLong = Long.valueOf(branch);
            }
            catch (Exception e) {
            }
        }
        finProductTypeKey.setBranch(branchLong);
        FinProductType finProductType = finProductTypeMapper.selectByPrimaryKey(finProductTypeKey);
        if (finProductType == null) {
            finProductType = new FinProductType();
        }
        
        String resultBusiType = null;
        // 交易.金融产品类型(投管分类) = FINPROD_TYPE_TG.'拆入'、'拆出'
        if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_拆入.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_特拆拆入.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_拆出.getValue(), finprodType2)) {
            resultBusiType = DictCodeList.业务类型_拆借交易.getValue();
        }
        // 交易.金融产品类型(投管分类) = FINPROD_TYPE_TG.'存款类资产' 并且 金融产品分类表.分类6 = DEPO_ASSET_TYPE.'同业借款'
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_存款.getValue(), finprodType2)
            && StringUtil.equals(DictCodeList.存款类资产类型_同业借款.getValue(), finProductType.getType6())) {
            resultBusiType = DictCodeList.业务类型_拆借交易.getValue();
        }
        // 交易.金融产品类型(投管分类) = FINPROD_TYPE_TG.'债券'
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue(), finprodType2)) {
            resultBusiType = DictCodeList.业务类型_银行间债券交易.getValue();
        }
        // 交易.金融产品类型(投管分类) = FINPROD_TYPE_TG.'存款类资产' 并且 金融产品分类表.分类6 = DEPO_ASSET_TYPE.'同业存款'
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_存款.getValue(), finprodType2)
            && StringUtil.equals(DictCodeList.存款类资产类型_存放同业.getValue(), finProductType.getType6())) {
            resultBusiType = DictCodeList.业务类型_存款类资产交易.getValue();
        }
        // 交易.金融产品类型(投管分类) = FINPROD_TYPE_TG.'存款类资产' 并且 金融产品分类表.分类6 = DEPO_ASSET_TYPE.'活期存放'
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_存款.getValue(), finprodType2)
            && StringUtil.equals(DictCodeListExt3.存款类资产类型_活期存放.getValue(), finProductType.getType6())) {
            resultBusiType = DictCodeList.业务类型_存款类资产交易.getValue();
        }
        // 交易.金融产品类型(投管分类) = FINPROD_TYPE_TG.'债券质押式正回购'、'债券质押式逆回购'
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue(), finprodType2)) {
            resultBusiType = DictCodeList.业务类型_债券质押式回购交易.getValue();
        }
        // 交易.金融产品类型(投管分类) = FINPROD_TYPE_TG.'债券买断式正回购'、'债券买断式逆回购'
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券买断式正回购.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券买断式逆回购.getValue(), finprodType2)) {
            resultBusiType = DictCodeList.业务类型_债券买断式回购交易.getValue();
        }
        // 交易.金融产品类型(投管分类) = FINPROD_TYPE_TG.'计划类资产' 并且 标的.收益类型 = 收益类型.'货币型'
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue(), finprodType2)
            && StringUtil.equals(DictCodeList.收益类型_货币型.getValue(), finProduct.getProfitType())) {
            resultBusiType = DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue();
        }
        // 交易.金融产品类型(投管分类) = FINPROD_TYPE_TG.'基金' 并且 标的.收益类型 = 收益类型.'货币型'
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_基金.getValue(), finprodType2)
            && StringUtil.equals(DictCodeList.收益类型_货币型.getValue(), finProduct.getProfitType())) {
            resultBusiType = DictCodeList.业务类型_货币基金交易_货基_类货基资产交易.getValue();
        }
        // 交易.金融产品类型(投管分类) = FINPROD_TYPE_TG.'基金' 并且 标的.收益类型 = 收益类型.'净值型'
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_基金.getValue(), finprodType2)
            && StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), finProduct.getProfitType())) {
            resultBusiType = DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue();
        }
        // 交易.金融产品类型(投管分类) = FINPROD_TYPE_TG.'计划类资产' 并且 标的.收益类型 = 收益类型.'净值型'
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue(), finprodType2)
            && StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), finProduct.getProfitType())) {
            resultBusiType = DictCodeList.业务类型_非货币基金交易_非货基_非类货基资产交易.getValue();
        }
        // 交易.金融产品类型(投管分类) = FINPROD_TYPE_TG.'计划类资产' 并且 标的.收益类型 = 收益类型.'收益型'
        else if ((StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_计划类资产.getValue(), finprodType2)
                && StringUtil.equals(DictCodeList.收益类型_收益型.getValue(), finProduct.getProfitType()))
                || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_项目类资产.getValue(), finprodType2)
        ) {
            resultBusiType = DictCodeList.业务类型_计划类资产交易_类债券资产交易.getValue();
        }
        // 交易.金融产品类型(投管分类) = FINPROD_TYPE_TG.'理财产品'或者'理财产品分层'或者'理财子产品' 并且 标的.收益类型 = 收益类型.'净值型'
        else if ((StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_理财子产品.getValue(), finprodType2))
            && StringUtil.equals(DictCodeList.收益类型_净值型.getValue(), finProduct.getProfitType())) {
            resultBusiType = DictCodeList.业务类型_净值型TA交易.getValue();
        }
        // 交易.金融产品类型(投管分类) = FINPROD_TYPE_TG.'理财产品'或者'理财产品分层'或者'理财子产品' 并且 标的.收益类型 = 收益类型.'货币性'
        else if ((StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_理财子产品.getValue(), finprodType2))
            && StringUtil.equals(DictCodeList.收益类型_货币型.getValue(), finProduct.getProfitType())) {
            resultBusiType = DictCodeList.业务类型_货币型TA交易.getValue();
        }
        // 交易.金融产品类型(投管分类) = FINPROD_TYPE_TG.'理财产品'或者'理财产品分层'或者'理财子产品' 并且 标的.收益类型 = 收益类型.'收益型'
        else if ((StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_理财产品.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_理财产品分层.getValue(), finprodType2)
            || StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_理财子产品.getValue(), finprodType2))
            && StringUtil.equals(DictCodeList.收益类型_收益型.getValue(), finProduct.getProfitType())) {
            resultBusiType = DictCodeList.业务类型_预期收益型TA交易.getValue();
        }
        // 交易.交易方向 = PS.'产品付费'
        else if (StringUtil.equals(DictCodeList.交易方向_产品付费.getValue(), ps)) {
            resultBusiType = DictCodeList.业务类型_产品费用交易.getValue();
        }
        // 交易.交易方向 = PS.'税费缴税'、‘税费退税’
        else if (StringUtil.equals(DictCodeList.交易方向_税费缴税.getValue(), ps)
            || StringUtil.equals(DictCodeList.交易方向_税费退税.getValue(), ps)) {
            resultBusiType = DictCodeList.业务类型_增值税交易.getValue();
        }
        // 交易.金融产品类型(投管分类) = FINPROD_TYPE_TG.'资金账户'
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_资金账户.getValue(), finprodType2)
            && StringUtil.equals(DictCodeList.交易方向_收息.getValue(), ps)) {
            resultBusiType = DictCodeList.业务类型_活期存款交易.getValue();
        }
        // 交易.金融产品类型(投管分类) = FINPROD_TYPE_TG.'即期'
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_即期.getValue(), finprodType2)
            ) {
            resultBusiType = DictCodeList.业务类型_外汇即期交易.getValue();
        }
        // 交易.金融产品类型(投管分类) = FINPROD_TYPE_TG.'远期'
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_远期.getValue(), finprodType2)
            ) {
            resultBusiType = DictCodeList.业务类型_外汇远期交易.getValue();
        }
        // 交易.金融产品类型(投管分类) = FINPROD_TYPE_TG.'掉期'
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_掉期.getValue(), finprodType2)
            ) {
            resultBusiType = DictCodeList.业务类型_外汇掉期交易.getValue();
        }
        // 交易.金融产品类型(投管分类) = FINPROD_TYPE_TG.'股票'
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_股票.getValue(), finprodType2)
            ) {
            resultBusiType = DictCodeList.业务类型_股票交易.getValue();
        }
        // 交易.金融产品类型(投管分类) = FINPROD_TYPE_TG.'利率互换'
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_利率互换.getValue(), finprodType2)
            ) {
            resultBusiType = DictCodeList.业务类型_利率互换交易.getValue();
        }
        // 交易.金融产品类型(投管分类) = FINPROD_TYPE_TG.'债券借贷'
        else if (StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券借贷融入.getValue(), finprodType2)
        		|| StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券借贷融出.getValue(), finprodType2)
            ) {
            resultBusiType = DictCodeList.业务类型_债券借贷交易.getValue();
        }
        //业绩回补合版本相关 start wangcheng 20191025
        else if(StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_固定管理费.getValue(), finprodType2)
        		||StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_浮动管理费.getValue(), finprodType2)){
        	resultBusiType = DictCodeList.业务类型_产品回补交易.getValue();
        }
        //业绩回补合版本相关 end wangcheng 20191025
        
        //特殊处理可转债，保证金划款  保证金回款
        if(StringUtil.equals(DictCodeListExt1.金融产品类型_投管用_债券.getValue(), finprodType2)
        		&&(StringUtil.equals(DictCodeListExt1.交易方向_保证金划款.getValue(), ps)
        				||StringUtil.equals(DictCodeListExt1.交易方向_保证金回款.getValue(), ps))){
        	resultBusiType = DictCodeList.业务类型_指令交易.getValue();
        }
        return resultBusiType;
    }
}
