/*
 * 文件名：NPrdDealForInterfRecProcessorResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年6月28日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.service.valuation.trademanage.trdfixincomevaluationtrade.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.common.interf.base.dto.PlannedAssetDealCommonResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description:固收类交易信息Processor返回共通结果
 * @auther: yuzj
 * @date: 2022/1/7
 * @param:
 * @return:
 */
public class TrdFixIncomeTradeResult extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private TrdFixIncomeValuationTradeDto trdFixIncomeValuationTradeDto;

    /**
     * 金融产品交易表
     */
    private List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();

    /**
     * 估值核算交易扩展表
     */
    private List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();

    /**
     * 入账信息表
     */
    private List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();

    /**
     * 账务明细表
     */
    private List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();

    private List<BokDetailId> listValBokDetailId=new ArrayList<BokDetailId>();
    /**
     * 日志
     */
    private List<ValInterflog> valInterflog = new ArrayList<ValInterflog>();


    /**
     * 交易费用明细
     */
    private List<TrdTradeFeeDetail> valTradeFeeDetailList = new ArrayList<TrdTradeFeeDetail>();
    /**
     *
     */
    private List<StlOrders> valOrdersList = new ArrayList<StlOrders>();
    /**
     *
     */
    private List<StlOrdersExt> valOrdersExtList = new ArrayList<StlOrdersExt>();

    /**
     * 交易类金融产品
     */
    private List<FinTradeProduct> listValTradeFinProduct = new ArrayList<FinTradeProduct>();

    /**
     * 读取数据数量
     */
    private int dataNum;

    public TrdFixIncomeTradeResult(){

    }

    /**
     * 重载构造方法 从valuation-common中返回的result重新赋值
     *
     * @param rtnResult 共通结果
     */
    public TrdFixIncomeTradeResult(PlannedAssetDealCommonResult result) {
        this.setListValFinProductDeal(result.getListValFinProductDeal());
        this.setListValFinProductDealExt(result.getListValFinProductDealExt());
        this.setListValDealAccounting(result.getListValDealAccounting());
        this.setListValDealAccountingDetail(result.getListValDealAccountingDetail());
        this.setValInterflog(result.getValInterflog());
        this.setListValBokDetailId(result.getListValBokDetailId());
        this.setValOrdersExtList(result.getValOrdersExtList());
        this.setValOrdersList(result.getValOrdersList());
        this.setValTradeFeeDetailList(result.getValTradeFeeDetailList());
        this.setListValTradeFinProduct(result.getListValTradeFinProduct());
        this.setDataNum(result.getDataNum());
        this.paramMap = result.getParamMap();
    }


    /**
     * 取得 金融产品交易
     *
     * @return 金融产品交易表
     */
    public List<TrdProductDeal> getListValFinProductDeal() {
        return listValFinProductDeal;
    }

    /**
     * 设置 金融产品交易表
     *
     * @param listValFinProductDeal 金融产品交易表
     */
    public void setListValFinProductDeal(List<TrdProductDeal> listValFinProductDeal) {
        this.listValFinProductDeal = listValFinProductDeal;
    }

    /**
     * 取得  估值核算交易扩展表
     *
     * @return 估值核算交易扩展表
     */
    public List<TrdProductDealAdd> getListValFinProductDealExt() {
        return listValFinProductDealExt;
    }

    /**
     * 设置  估值核算交易扩展表
     *
     * @param listValFinProductDealExt 估值核算交易扩展表
     */
    public void setListValFinProductDealExt(List<TrdProductDealAdd> listValFinProductDealExt) {
        this.listValFinProductDealExt = listValFinProductDealExt;
    }

    /**
     * 取得 入账信息表
     *
     * @return 入账信息表
     */
    public List<BokDealAccounting> getListValDealAccounting() {
        return listValDealAccounting;
    }

    /**
     * 设置 入账信息表
     *
     * @param listValDealAccounting 入账信息表
     */
    public void setListValDealAccounting(List<BokDealAccounting> listValDealAccounting) {
        this.listValDealAccounting = listValDealAccounting;
    }

    /**
     * 取得 账务明细表
     *
     * @return 账务明细表
     */
    public List<BokDealAccountingDetail> getListValDealAccountingDetail() {
        return listValDealAccountingDetail;
    }

    /**
     * 设置 账务明细表
     *
     * @param  账务明细表
     */
    public void setListValDealAccountingDetail(List<BokDealAccountingDetail> listValDealAccountingDetail) {
        this.listValDealAccountingDetail = listValDealAccountingDetail;
    }


    /**
     * 获取 日志
     *
     * @return 日志.
     */
    public List<ValInterflog> getValInterflog() {
        return valInterflog;
    }

    /**
     * 设置 日志
     *
     * @param valInterflog 日志
     */
    public void setValInterflog(List<ValInterflog> valInterflog) {
        this.valInterflog = valInterflog;
    }


    /**
     * 获取 listValBokDetailId
     * @return listValBokDetailId.
     */
    public List<BokDetailId> getListValBokDetailId() {
        return listValBokDetailId;
    }


    /**
     * 设置 listValBokDetailId
     * @param listValBokDetailId listValBokDetailId
     */
    public void setListValBokDetailId(List<BokDetailId> listValBokDetailId) {
        this.listValBokDetailId = listValBokDetailId;
    }


    /**
     * 获取 valTradeFeeDetailList
     * @return valTradeFeeDetailList.
     */
    public List<TrdTradeFeeDetail> getValTradeFeeDetailList() {
        return valTradeFeeDetailList;
    }


    /**
     * 设置 valTradeFeeDetailList
     * @param valTradeFeeDetailList valTradeFeeDetailList
     */
    public void setValTradeFeeDetailList(List<TrdTradeFeeDetail> valTradeFeeDetailList) {
        this.valTradeFeeDetailList = valTradeFeeDetailList;
    }


    /**
     * 获取 valOrdersList
     * @return valOrdersList.
     */
    public List<StlOrders> getValOrdersList() {
        return valOrdersList;
    }


    /**
     * 设置 valOrdersList
     * @param valOrdersList valOrdersList
     */
    public void setValOrdersList(List<StlOrders> valOrdersList) {
        this.valOrdersList = valOrdersList;
    }


    /**
     * 获取 valOrdersExtList
     * @return valOrdersExtList.
     */
    public List<StlOrdersExt> getValOrdersExtList() {
        return valOrdersExtList;
    }


    /**
     * 设置 valOrdersExtList
     * @param valOrdersExtList valOrdersExtList
     */
    public void setValOrdersExtList(List<StlOrdersExt> valOrdersExtList) {
        this.valOrdersExtList = valOrdersExtList;
    }

    /**
     * 用于存放去重数据筛选条件
     */
    private Map<String,Object> paramMap;

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }


    /**
     * 获取 listValTradeFinProduct
     * @return listValTradeFinProduct.
     */
    public List<FinTradeProduct> getListValTradeFinProduct() {
        return listValTradeFinProduct;
    }


    /**
     * 设置 listValTradeFinProduct
     * @param listValTradeFinProduct listValTradeFinProduct
     */
    public void setListValTradeFinProduct(List<FinTradeProduct> listValTradeFinProduct) {
        this.listValTradeFinProduct = listValTradeFinProduct;
    }


    /**
     * 获取 dataNum
     * @return dataNum.
     */
    public int getDataNum() {
        return dataNum;
    }


    /**
     * 设置 dataNum
     * @param dataNum dataNum
     */
    public void setDataNum(int dataNum) {
        this.dataNum = dataNum;
    }

    public TrdFixIncomeValuationTradeDto getTrdFixIncomeValuationTradeDto() {
        return trdFixIncomeValuationTradeDto;
    }

    public void setTrdFixIncomeValuationTradeDto(TrdFixIncomeValuationTradeDto trdFixIncomeValuationTradeDto) {
        this.trdFixIncomeValuationTradeDto = trdFixIncomeValuationTradeDto;
    }
}
