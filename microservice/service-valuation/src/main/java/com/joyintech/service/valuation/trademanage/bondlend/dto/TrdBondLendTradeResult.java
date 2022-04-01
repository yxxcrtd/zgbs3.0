/*
 * 文件名：NPrdDealForInterfRecProcessorResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年6月28日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.service.valuation.trademanage.bondlend.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.common.interf.base.dto.AssetDealInfoValCommonResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 债券借贷交易信息Processor返回共通结果
 * 
 * @author weipeng
 * @version 1.0 2018年6月28日
 * @see TrdBondLendTradeResult
 * @since 1.0
 */
public class TrdBondLendTradeResult extends BaseEntity {

    private static final long serialVersionUID = 1L;
    /**
     * 金融产品交易
     */
    private List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();

    /**
     * 估值核算交易扩展表
     */
    private List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();

    /**
     * 金融产品标的信息
     */
    private List<FinDealTarget> listFinDealTarget=new ArrayList<FinDealTarget>();
    /**
     * 交易费用明细表
     */
    private List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
    /**
     * 入账信息表
     */
    private List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();
    /**
     * 账务明细表
     */
    private List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
    /**
     * 账务明细代码表 add by wujie on 20180712
     */
    private List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();
    /**
     * 日志
     */
    private List<ValInterflog> valInterflog = new ArrayList<ValInterflog>();
    /**
     * 结算指令表
     */
    private List<StlOrders> valOrdersList = new ArrayList<StlOrders>();
    /**
     * 结算指令拓展表
     */
    private List<StlOrdersExt> valOrdersExtList = new ArrayList<StlOrdersExt>();

    private TrdBondLendTradeDto trdBondLendTradeDto;
    /**
     * 读取数据数量
     */
    private int dataNum;
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

    public TrdBondLendTradeResult(){

    }

    /**
     * 重载构造方法 从valuation-common中返回的result重新赋值
     *
     * @param rtnResult 共通结果
     */
    public TrdBondLendTradeResult(AssetDealInfoValCommonResult result){
        this.setListValDealAccounting(result.getListValDealAccounting());
        this.setListValDealAccountingDetail(result.getListValDealAccountingDetail());
        this.setListValFinProductDeal(result.getListValFinProductDeal());
        this.setListValFinProductDealExt(result.getListValFinProductDealExt());
        this.setValInterflog(result.getValInterflog());
        /***add by wujie on 20180712 start*/
        this.setListValBokDetailId(result.getListValBokDetailId());
        /***add by wujie on 20180712 end*/
        this.setDataNum(result.getDataNum());
        this.setValOrdersList(result.getListValOrders());
        this.setValOrdersExtList(result.getListValOrdersExt());
        this.setListValTradeFeeDetail(result.getListValTradeFeeDetail());
        this.paramMap = result.getParamMap();
    }

    public List<TrdProductDeal> getListValFinProductDeal() {
        return listValFinProductDeal;
    }

    public void setListValFinProductDeal(List<TrdProductDeal> listValFinProductDeal) {
        this.listValFinProductDeal = listValFinProductDeal;
    }

    public List<TrdProductDealAdd> getListValFinProductDealExt() {
        return listValFinProductDealExt;
    }

    public void setListValFinProductDealExt(List<TrdProductDealAdd> listValFinProductDealExt) {
        this.listValFinProductDealExt = listValFinProductDealExt;
    }

    public List<FinDealTarget> getListFinDealTarget() {
        return listFinDealTarget;
    }

    public void setListFinDealTarget(List<FinDealTarget> listFinDealTarget) {
        this.listFinDealTarget = listFinDealTarget;
    }

    public List<BokDealAccounting> getListValDealAccounting() {
        return listValDealAccounting;
    }

    public void setListValDealAccounting(List<BokDealAccounting> listValDealAccounting) {
        this.listValDealAccounting = listValDealAccounting;
    }

    public List<BokDealAccountingDetail> getListValDealAccountingDetail() {
        return listValDealAccountingDetail;
    }

    public void setListValDealAccountingDetail(List<BokDealAccountingDetail> listValDealAccountingDetail) {
        this.listValDealAccountingDetail = listValDealAccountingDetail;
    }

    public List<BokDetailId> getListValBokDetailId() {
        return listValBokDetailId;
    }

    public void setListValBokDetailId(List<BokDetailId> listValBokDetailId) {
        this.listValBokDetailId = listValBokDetailId;
    }

    public List<ValInterflog> getValInterflog() {
        return valInterflog;
    }

    public void setValInterflog(List<ValInterflog> valInterflog) {
        this.valInterflog = valInterflog;
    }

    public int getDataNum() {
        return dataNum;
    }

    public void setDataNum(int dataNum) {
        this.dataNum = dataNum;
    }

    public List<StlOrders> getValOrdersList() {
        return valOrdersList;
    }

    public void setValOrdersList(List<StlOrders> valOrdersList) {
        this.valOrdersList = valOrdersList;
    }

    public List<StlOrdersExt> getValOrdersExtList() {
        return valOrdersExtList;
    }

    public void setValOrdersExtList(List<StlOrdersExt> valOrdersExtList) {
        this.valOrdersExtList = valOrdersExtList;
    }

    public List<TrdTradeFeeDetail> getListValTradeFeeDetail() {
        return listValTradeFeeDetail;
    }

    public void setListValTradeFeeDetail(List<TrdTradeFeeDetail> listValTradeFeeDetail) {
        this.listValTradeFeeDetail = listValTradeFeeDetail;
    }


    public TrdBondLendTradeDto getTrdBondLendTradeDto() {
        return trdBondLendTradeDto;
    }

    public void setTrdBondLendTradeDto(TrdBondLendTradeDto trdBondLendTradeDto) {
        this.trdBondLendTradeDto = trdBondLendTradeDto;
    }
}
