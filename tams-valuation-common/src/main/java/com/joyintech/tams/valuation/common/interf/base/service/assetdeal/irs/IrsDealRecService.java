/*
 * 文件名：LendDealRecService.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V3.0.5.0
 * 修改人：殷亚
 * 修改时间：2022年4月21日
 * 修改内容：新建
 * 系统名称：估值核算系统V3.0.5
 */

package com.joyintech.tams.valuation.common.interf.base.service.assetdeal.irs;

import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.valuation.common.interf.base.constant.InterfCommonConstant;
import com.joyintech.tams.valuation.common.interf.base.dto.*;
import com.joyintech.tams.valuation.common.interf.base.service.common.InterfCommonService;
import com.joyintech.tams.valuation.common.interf.base.service.common.TemplateAnalysisForXMLService;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValDlDeal;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValIrsDeal;
import com.joyintech.tams.valuation.common.interf.tempTables.entity.ValSecVestAssetDeal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 利率互换交易
 *
 * @author 殷亚
 * @version 1.0
 * @date 2022/4/21 10:53
 * @see
 */
@Service
@Scope("prototype")
public class IrsDealRecService {

    @Autowired
    private InterfCommonService interfCommonService;

    /**
     * TemplateAnalysisForXMLService
     */
    @Autowired
    private TemplateAnalysisForXMLService templateAnalysisForXMLService;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public IrsDealCommonResult execute(TemplateSourceFlgDTO templateSourceFlgDTO,
                                       TemplateOrderInfoReaderResult acctEntity) {
        IrsDealCommonResult result = new IrsDealCommonResult();
        List<ValIrsDeal> listValIrsDeal = new ArrayList<ValIrsDeal>();
        Map<String, Object> params = acctEntity.getParams();
        // 解析模板后的list以及日志list
        TemplateCheckReturnResult returnResult = new TemplateCheckReturnResult();
        // 1、文件读取解析
        returnResult = templateAnalysisForXMLService.execute(acctEntity);

        // 2、进行数据有效性的check,并生出进入日志表的数据结构
        listValIrsDeal = returnResult.getT(ValIrsDeal.class);
        //获取数据数量
        result.setDataNum(listValIrsDeal.size());

        if (returnResult.isReadFile()) {
            result.getParamMap().put(InterfCommonConstant.读取文件状态, DictCodeList.是否_是.getValue());
        }

        // 3、更具有效数据，进行数据的编辑，并生成进入业务表的数据结构

        Map<String, Object> map = this.handleValIrsDeal(listValIrsDeal, acctEntity, params, returnResult.getValInterflog());
        if (map != null) {
            result.setListValFinProductDeal(
                    (List<TrdProductDeal>) map.get("ValFinProductDeal"));
            result.setListValFinProductDealExt(
                    (List<TrdProductDealAdd>) map.get("ValFinProductDealExt"));
            result.setBokDealAccountingList((List<BokDealAccounting>) map.get(
                    "ValDealAccounting"));
            result.setBokDealAccountingDetailList((List<BokDealAccountingDetail>) map.get(
                    "ValDealAccountingDetail"));
            result.setBokDetailIdList((List<BokDetailId>) map.get(
                    "ValBokDetailId"));
            result.setListValOrders(
                    (List<StlOrders>) map.get(
                            "valOrdersList"));
            result.setListValOrdersExt(
                    (List<StlOrdersExt>) map.get(
                            "valOrdersExtList"));
            // 交易费用明细表List
            List<TrdTradeFeeDetail> listValTradeFeeDetail = (List<TrdTradeFeeDetail>)map.get(
                    "ValTradeFeeDetail");
            result.setListValTradeFeeDetail(listValTradeFeeDetail);

            // 日志
            result.setListValInterflog((List<ValInterflog>) map.get("valInterflogList"));
        }
        return result;
    }

    /**
     * 更具有效数据，进行数据的编辑，并生成进入业务表的数据结构
     *
     * @param listValIrsDeal List<ValBonusResTrade>
     * @return Map<String, Object>
     * @see
     * @since 1.0
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> handleValIrsDeal(List<ValIrsDeal> listValIrsDeal, TemplateOrderInfoReaderResult acctEntity, Map<String, Object> params,
                                                List<ValInterflog> valInterflog) {
        Map<String, Object> resultMap = new HashMap<>();
        List<String> bookSetIds = (List<String>) params.get(InterfCommonConstant.接口_运行接口账套ID);
        String valModel = (String) params.get(InterfCommonConstant.接口模式);
        List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();
        List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
        //结算指令
        List<StlOrders> listValOrders = new ArrayList<StlOrders>();
        List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
        // 入账信息表List
        List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
        // 账务明细表List
        List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
        //账务明细代码表
        List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();
        // 入账信息表 账务明细表 账务明细代码表 调用共通
        InterfProductDealBookCommonDto dealAccounting = null;
        // 金融产品交易
        TrdProductDeal valFinProductDeal = null;
        // 交易费用明细表List
        List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
        List<ValInterflog> valInterflogList = new ArrayList<>();
        Map<String, BokBooksetExt> bokBooksetExtMap = new HashMap<String, BokBooksetExt>();

        for (ValIrsDeal item : listValIrsDeal) {
            if (StringUtil.equals(InterfCommonConstant.接口模式_指定账套, valModel) && !bookSetIds.contains(item.getProdId())) {
                continue;
            }
            TargetFinProduct targetFinProduct = interfCommonService.queryTargetFinProduct(item.getTradeId());
            String finProductType = targetFinProduct == null ? null : targetFinProduct.getFinprodType();

            item.setCreateTime(acctEntity.getResovleDate());

            valFinProductDeal = this.getValFinProductDeal(item, targetFinProduct);
            listValFinProductDeal.add(valFinProductDeal);

            dealAccounting = interfCommonService.dealAccounting(
                    valFinProductDeal, item.getProdId());

            TrdProductDealAdd valFinProductDealExt = this.getValFinProductDealExt(item, valFinProductDeal, dealAccounting.getAcctSubjectType());
            listValFinProductDealExt.add(valFinProductDealExt);

            //四级科目尾号、四级科目尾号名称改为资产的金融产品代码、金融产品简称
            String detailSubjectNoTail = null;
            String detailSubnameTail = null;
            if (StringUtil.isEmpty(acctEntity.getFourLevelSubjectKg())
                    || DictCodeList.是否_是.getValue().equals(acctEntity.getFourLevelSubjectKg())) {

                detailSubjectNoTail = targetFinProduct == null ? null : targetFinProduct.getFinprodId();
                detailSubnameTail = targetFinProduct == null ? null : targetFinProduct.getFinprodId();
                //四级科目尾号
                dealAccounting.getBokDetailId().setDetailSubjectNoTail(detailSubjectNoTail);
                //四级科目尾号名称
                dealAccounting.getBokDetailId().setDetailSubnameTail(detailSubnameTail);

                listValDealAccounting.add(dealAccounting.getBokDealAccounting());
                listValDealAccountingDetail.add(dealAccounting.getBokDealAccountingDetail());
                listValBokDetailId.add(dealAccounting.getBokDetailId());

                // 交易费用明细表
                List<TrdTradeFeeDetail> listfeeDetail = this.getValTradeFeeDetail(item,valFinProductDeal);
                listValTradeFeeDetail.addAll(listfeeDetail);

//                //结算指令
//                Map<String, Object> map = interfCommonService.instructionGenerate(valFinProductDeal,
//                        item.getProdId(), dealAccounting.getAcctSubjectType(), null);
//                if (map.get("ORDERS") != null) {
//                    listValOrders.addAll((List<StlOrders>) map.get("ORDERS"));
//                }
//                if (map.get("ORDERS_EXT") != null) {
//                    listValOrdersExt.addAll((List<StlOrdersExt>) map.get("ORDERS_EXT"));
//                }
//                if (map.get("ACCOUNTING") != null) {
//                    listValDealAccounting.addAll((List<BokDealAccounting>) map.get("ACCOUNTING"));
//                }
//                if (map.get("ACCOUNTING_DETAI") != null) {
//                    listValDealAccountingDetail.addAll((List<BokDealAccountingDetail>) map.get("ACCOUNTING_DETAI"));
//                }
//                if (map.get("BOK_DETAIL_ID") != null) {
//                    listValBokDetailId.addAll((List<BokDetailId>) map.get("BOK_DETAIL_ID"));
//                }
            }

        }
        resultMap.put("ValFinProductDeal", listValFinProductDeal);
        resultMap.put("ValFinProductDealExt", listValFinProductDealExt);
        resultMap.put("ValDealAccounting", listValDealAccounting);
        resultMap.put("ValDealAccountingDetail", listValDealAccountingDetail);
        resultMap.put("ValBokDetailId", listValBokDetailId);
        resultMap.put("valOrdersList", listValOrders);
        resultMap.put("valOrdersExtList", listValOrdersExt);
        resultMap.put("ValTradeFeeDetail", listValTradeFeeDetail);

        valInterflogList = interfCommonService.filterValInterflogList(valInterflog, bookSetIds, valModel);
        resultMap.put("valInterflogList", valInterflogList);
        return resultMap;
    }


    /**
     * 金融产品交易表 接收 T1
     *
     * @param dto      01-存续交易信息
     * @param bookType
     * @return 金融产品交易表
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValIrsDeal dto, String finProductType, String bookType) {
        TrdProductDeal result = new TrdProductDeal();
        result.setTradeId(dto.getTradeId());
        // 业务类型 = 13 利率互换交易
        result.setBusiType(DictCodeList.业务类型_利率互换交易.getValue());
        // 金融产品代码 = A.原交易编号
        result.setFinprodId(dto.getTradeId());
        // 金融产品类型  = 
        result.setFinprodType(finProductType);
        // 分支序号 = 设置全局变量(默认为0）
        result.setBranch(InterfCommonConstant.分支序号_默认);
        // 交易日= A.交易日
        result.setTradeDate(dto.getTradeDate());
        //起息日 = A.起息日
        result.setVdate(dto.getVdate());
        //到期日 = A.到期日
        result.setMdate(dto.getMdate());
        //交割日 = A.交割日
        result.setSettleDate(dto.getSettleDate());
        //交易币种 = A.币种
        result.setCcy(dto.getCcy());
        // 交易状态 = A.数据动作标志
        result.setTradeStatus(dto.getDataActionSign());
        // 是否生成结算指令
        result.setIsClean(dto.getIsClean());
        //交易对手 = A.交易对手
        result.setCounterId(dto.getCounterId());
        //交易对手类型 = A.交易对手类型
//        result.setCounterType(dto.getCounterType());
        // 关联系统交易编号 = A.成交编号
        result.setfTradeId(dto.getTradeId());
        // 创建时间 = 系统时间
        result.setCreateTime(dto.getCreateTime());
        //交易状态
        result.setTradeStatus(dto.getDataActionSign());
        // 交易份额 = B.实收本金
        result.setShareAmt(dto.getPrinAmt());
        // 交易本金 = B.实收本金
        result.setPrinAmt(dto.getPrinAmt());
        // 交易金额 = B.实收本金
        result.setTradeAmt(dto.getPrinAmt());
        // 全价总额 = B.实收本金
        result.setFpriceAmt(dto.getPrinAmt());
        //交易方向
        result.setPs(dto.getPs());
        //随交易支付费用
        BigDecimal feeAmtPay = BigDecimal.ZERO;
        //未随交易支付费用
        BigDecimal feeAmtUnpay = BigDecimal.ZERO;
        //总交易费用
        BigDecimal feeAmt = BigDecimal.ZERO;
        //手续费
        if(DictCodeList.是否_是.getValue().equals(dto.getIsDealFeeAmt())){
            feeAmtPay = feeAmtPay.add(dto.getDealFeeAmt()==null?BigDecimal.ZERO:dto.getDealFeeAmt());
        }else{
            feeAmtUnpay = feeAmtUnpay.add(dto.getDealFeeAmt()==null?BigDecimal.ZERO:dto.getDealFeeAmt());
        }
        //结算费
        if(DictCodeList.是否_是.getValue().equals(dto.getIsDealFeeAmt())){
            feeAmtPay = feeAmtPay.add(dto.getStlFeeAmt()==null?BigDecimal.ZERO:dto.getStlFeeAmt());
        }else{
            feeAmtUnpay = feeAmtUnpay.add(dto.getStlFeeAmt()==null?BigDecimal.ZERO:dto.getStlFeeAmt());
        }

        feeAmt.add(dto.getDealFeeAmt()==null?BigDecimal.ZERO:dto.getDealFeeAmt()).add(dto.getStlFeeAmt()==null?BigDecimal.ZERO:dto.getStlFeeAmt());
        result.setFeeAmtUnpay(feeAmtUnpay);
        result.setFeeAmt(feeAmt);
        result.setFeeAmtPay(feeAmtPay);

        //原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
        if (result.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())) {
            result.setpTradeId(interfCommonService.getFTradeId(result));
        }
        return result;
    }

    /**
     * 接收 金融产品交易 T1
     *
     * @param dto ValIrsDeal
     * @return ValFinProductDeal
     * @see
     * @since 1.0
     */
    private TrdProductDeal getValFinProductDeal(ValIrsDeal dto, TargetFinProduct targetFinProduct) {
        TrdProductDeal result = new TrdProductDeal();
        // 交易编号
        String tradeId = dto.getTradeId();
        result.setTradeId(tradeId);
        // 业务类型 = 01 拆借交易
        result.setBusiType(DictCodeList.业务类型_利率互换交易.getValue());
        // 金融产品代码 = A.原交易编号
        result.setFinprodId(dto.getTradeId());
        // 金融产品类型  =
        result.setFinprodType(targetFinProduct == null ? null : targetFinProduct.getFinprodType());
        // 分支序号 = 设置全局变量(默认为0）
        result.setBranch(InterfCommonConstant.分支序号_默认);
        // 交易日= A.交易日
        result.setTradeDate(dto.getTradeDate());
        //起息日 = A.起息日
        result.setVdate(dto.getVdate());
        //到期日 = A.到期日
        result.setMdate(dto.getMdate());
        //交割日 = A.交割日
        result.setSettleDate(dto.getSettleDate());
        //交易币种 = A.币种
        result.setCcy(dto.getCcy());

        //净价总额 = A.本金金额
        result.setCpriceAmt(dto.getPrinAmt());
        //交易份额 = A.本金金额
        result.setShareAmt(dto.getPrinAmt());
        //全价总额 = A.本金金额+A.实收利息
        result.setFpriceAmt(dto.getPrinAmt());
        //交易本金 = A.本金金额
        result.setPrinAmt(dto.getPrinAmt());
        //交易金额 = A.交易金额
        result.setTradeAmt(dto.getPrinAmt());
        // 交易方向 =A.交易类型
        result.setPs(dto.getPs());
        // 交易状态 = A.数据动作标志
        result.setTradeStatus(dto.getDataActionSign());
        // 是否生成结算指令
        result.setIsClean(dto.getIsClean());
        //交易对手 = A.交易对手
        result.setCounterId(dto.getCounterId());
        // 关联系统交易编号 = A.成交编号
        result.setfTradeId(dto.getTradeId());
        //随交易支付费用
        BigDecimal feeAmtPay = BigDecimal.ZERO;
        //未随交易支付费用
        BigDecimal feeAmtUnpay = BigDecimal.ZERO;
        //总交易费用
        BigDecimal feeAmt = BigDecimal.ZERO;
        //手续费
        if(DictCodeList.是否_是.getValue().equals(dto.getIsDealFeeAmt())){
            feeAmtPay = feeAmtPay.add(dto.getDealFeeAmt()==null?BigDecimal.ZERO:dto.getDealFeeAmt());
        }else{
            feeAmtUnpay = feeAmtUnpay.add(dto.getDealFeeAmt()==null?BigDecimal.ZERO:dto.getDealFeeAmt());
        }
        //结算费
        if(DictCodeList.是否_是.getValue().equals(dto.getIsDealFeeAmt())){
            feeAmtPay = feeAmtPay.add(dto.getStlFeeAmt()==null?BigDecimal.ZERO:dto.getStlFeeAmt());
        }else{
            feeAmtUnpay = feeAmtUnpay.add(dto.getStlFeeAmt()==null?BigDecimal.ZERO:dto.getStlFeeAmt());
        }

        feeAmt.add(dto.getDealFeeAmt()==null?BigDecimal.ZERO:dto.getDealFeeAmt()).add(dto.getStlFeeAmt()==null?BigDecimal.ZERO:dto.getStlFeeAmt());
        result.setFeeAmtUnpay(feeAmtUnpay);
        result.setFeeAmt(feeAmt);
        result.setFeeAmtPay(feeAmtPay);
        // 创建时间 = 系统时间
        result.setCreateTime(dto.getCreateTime());
        // 原交易编号 = 交易状态为撤销时： 原交易编号=调用共通设计->【详细设计-接口共通方法设计.xlsx】->撤销交易对应的原交易编号查询SHEET 交易状态为非撤销时：原交易编号为空
        if (result.getTradeStatus().equals(DictCodeListExt1.交易状态_撤销.getValue())) {
            result.setpTradeId(interfCommonService.getFTradeId(result));
        }

        return result;
    }



    /**
     * T5: 交易费用明细表 接收
     *
     * @param dto 01-买卖交易
     * @param valFinProductDeal
     * @return 交易费用明细表
     * @see
     * @since 1.0
     */
    private List<TrdTradeFeeDetail> getValTradeFeeDetail(ValIrsDeal dto, TrdProductDeal valFinProductDeal) {
        List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
        Map<String,BigDecimal> map = new HashMap<String,BigDecimal>();
        Map<String,String> mapIsPay = new HashMap<String,String>();
        //交易手续费
        map.put(DictCodeListExt1.一次性费用类型_交易手续费.getValue(), dto.getDealFeeAmt());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_交易手续费.getValue(), dto.getIsDealFeeAmt());

        //交易结算费
        map.put(DictCodeListExt1.一次性费用类型_结算费.getValue(), dto.getStlFeeAmt());
        mapIsPay.put(DictCodeListExt1.一次性费用类型_结算费.getValue(), dto.getIsStlFeeAmt());


        for (String key : map.keySet()) {
            BigDecimal amt = map.get(key);
            if(amt==null || BigDecimal.ZERO.compareTo(amt)==0){
                continue;
            }
            TrdTradeFeeDetail valTradeFeeDetail = new TrdTradeFeeDetail();
            // 交易编号 = T1.交易编号
            valTradeFeeDetail.setTradeId(valFinProductDeal.getTradeId());
            valTradeFeeDetail.setFeeType(key);
            // 是否随交易支付 = 固定值：Y 是 默认随交易支付
            valTradeFeeDetail.setIsPayWithTrade(mapIsPay.get(key));
            // 对应金额
            valTradeFeeDetail.setFeeAmt(amt);
            valTradeFeeDetail.setFeeId(key);
            // 创建时间 = 系统时间
            valTradeFeeDetail.setCreateTime(dto.getCreateTime());
            listValTradeFeeDetail.add(valTradeFeeDetail);
        }

        return listValTradeFeeDetail;
    }


    /**
     * 接收 估值核算交易扩展表 T2
     *
     * @param dto ValDlDeal
     * @return ValFinProductDealExt
     * @see
     * @since 1.0
     */
    private TrdProductDealAdd getValFinProductDealExt(ValIrsDeal dto, TrdProductDeal deal, String finprodType) {
        TrdProductDealAdd result = new TrdProductDealAdd();
        // 交易编号 = T1.交易编号
        result.setTradeId(deal.getTradeId());
        // 理财产品代码 = A.产品代码
        result.setPortfolioId(dto.getProdId());
        //生成方式
        result.setGenType(com.joyintech.tams.constant.DictCodeListExt1.生成方式_接口接入.getValue());
        return result;
    }


}
