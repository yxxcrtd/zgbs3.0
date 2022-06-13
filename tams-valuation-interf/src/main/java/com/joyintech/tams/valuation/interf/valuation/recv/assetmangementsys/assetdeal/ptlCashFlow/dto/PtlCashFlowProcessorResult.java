/*
 * 文件名：PtlCashFlowProcessorResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：tanxuan
 * 修改时间：2020年4月2日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.interf.valuation.recv.assetmangementsys.assetdeal.ptlCashFlow.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.common.interf.base.dto.PtlCashFlowCommonResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 自定义现金流交易信息Processor返回共通结果
 * 
 * @author tanxuan
 * @version 1.0 2020年4月2日
 * @see PtlCashFlowProcessorResult
 * @since 1.0
 */
public class PtlCashFlowProcessorResult extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 金融产品交易
     */
    private List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();

    /**
     * 估值核算交易扩展表
     */
    private List<TrdProductDealAdd> listValFinProductDealAdd = new ArrayList<TrdProductDealAdd>();

    /**
     * 入账信息表
     */
    private List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();

    /**
     * 账务明细表
     */
    private List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();
    
    /**
     * 账务明细代码表 
     */
    private List<BokDetailId> listValBokDetailId = new ArrayList<BokDetailId>();

    /**
     * 交易费用明细表
     */
    private List<TgTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TgTradeFeeDetail>();
    
    /**
     * 结算指令表
     */
   private List<StlOrders> valOrdersList = new ArrayList<StlOrders>();
   
    /**
     * 结算指令拓展表
     */
   private List<StlOrdersExt> valOrdersExtList = new ArrayList<StlOrdersExt>();
   
    /**
     * 日志
     */
    private List<ValInterflog> valInterflog = new ArrayList<ValInterflog>();
    
    /**
     * 读取数据数量
     */
    private int dataNum;
    
    /**
     * 用于存放去重数据筛选条件
     */
    private Map<String,Object> paramMap;
    
    
    public PtlCashFlowProcessorResult() {

    }

    /**
     * 重载构造方法 从valuation-common中返回的result重新赋值
     * 
     * @param rtnResult 共通结果
     */
    public PtlCashFlowProcessorResult(PtlCashFlowCommonResult rtnResult) {
        this.setListValDealAccounting(rtnResult.getListValDealAccounting());
        this.setListValDealAccountingDetail(rtnResult.getListValDealAccountingDetail());
        this.setListValTradeFeeDetail(rtnResult.getListValTradeFeeDetail());
        this.setListValFinProductDeal(rtnResult.getListValFinProductDeal());
        this.setListValFinProductDealAdd(rtnResult.getListValFinProductDealAdd());
        this.setValInterflog(rtnResult.getValInterflog());
        this.setListValBokDetailId(rtnResult.getListValBokDetailId());
        this.setValOrdersList(rtnResult.getListValOrders());
        this.setValOrdersExtList(rtnResult.getListValOrdersExt());
        this.setDataNum(rtnResult.getDataNum());
        this.paramMap = rtnResult.getParamMap();
    }

    /**
     * 获取 金融产品交易
     * 
     * @return 金融产品交易.
     */
    public List<TrdProductDeal> getListValFinProductDeal() {
        return listValFinProductDeal;
    }

    /**
     * 设置 金融产品交易
     * 
     * @param listValFinProductDeal 金融产品交易
     */
    public void setListValFinProductDeal(List<TrdProductDeal> listValFinProductDeal) {
        this.listValFinProductDeal = listValFinProductDeal;
    }

    /**
     * 获取 估值核算交易扩展表
     * 
     * @return 估值核算交易扩展表.
     */
    public List<TrdProductDealAdd> getListValFinProductDealAdd() {
        return listValFinProductDealAdd;
    }

    /**
     * 设置 估值核算交易扩展表
     * 
     * @param listValFinProductDealAdd 估值核算交易扩展表
     */
    public void setListValFinProductDealAdd(List<TrdProductDealAdd> listValFinProductDealAdd) {
        this.listValFinProductDealAdd = listValFinProductDealAdd;
    }

    /**
     * 获取 入账信息表
     * 
     * @return 入账信息表.
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
     * 获取 账务明细表
     * 
     * @return 账务明细表.
     */
    public List<BokDealAccountingDetail> getListValDealAccountingDetail() {
        return listValDealAccountingDetail;
    }

    /**
     * 设置 账务明细表
     * 
     * @param listValDealAccountingDetail 账务明细表
     */
    public void setListValDealAccountingDetail(List<BokDealAccountingDetail> listValDealAccountingDetail) {
        this.listValDealAccountingDetail = listValDealAccountingDetail;
    }
    
    /**
     * 获取 账务明细代码表
     * 
     * @return 账务明细代码表.
     */
    public List<BokDetailId> getListValBokDetailId() {
		return listValBokDetailId;
	}

    /**
     * 设置 账务明细代码表
     * 
     * @param listValBokDetailId 账务明细代码表
     */
	public void setListValBokDetailId(List<BokDetailId> listValBokDetailId) {
		this.listValBokDetailId = listValBokDetailId;
	}

    /**
     * 获取 交易费用明细表
     * 
     * @return 交易费用明细表.
     */
    public List<TgTradeFeeDetail> getListValTradeFeeDetail() {
        return listValTradeFeeDetail;
    }


	/**
     * 设置 交易费用明细表
     * 
     * @param listValTradeFeeDetail 交易费用明细表
     */
    public void setListValTradeFeeDetail(List<TgTradeFeeDetail> listValTradeFeeDetail) {
        this.listValTradeFeeDetail = listValTradeFeeDetail;
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
     * 获取 结算指令表
     * 
     * @return 结算指令表
     */
	public List<StlOrders> getValOrdersList() {
		return valOrdersList;
	}

    /**
     * 设置 结算指令表
     * 
     * @param valInterflog 结算指令表
     */
	public void setValOrdersList(List<StlOrders> valOrdersList) {
		this.valOrdersList = valOrdersList;
	}

    /**
     * 获取 结算指令拓展表
     * 
     * @return 结算指令拓展表
     */
	public List<StlOrdersExt> getValOrdersExtList() {
		return valOrdersExtList;
	}

    /**
     * 设置 结算指令拓展表
     * 
     * @param valInterflog 结算指令拓展表
     */
	public void setValOrdersExtList(List<StlOrdersExt> valOrdersExtList) {
		this.valOrdersExtList = valOrdersExtList;
	}

    /**
     * 获取 去重数据筛选条件
     * 
     * @return 去重数据筛选条件
     */
    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    /**
     * 设置 去重数据筛选条件
     * 
     * @param dataNum 去重数据筛选条件
     */
    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }

    /**
     * 获取 读取数据数量
     * 
     * @return 读取数据数量
     */
    public int getDataNum() {
        return dataNum;
    }

    /**
     * 设置 读取数据数量
     * 
     * @param dataNum 读取数据数量
     */
    public void setDataNum(int dataNum) {
        this.dataNum = dataNum;
    }

}
