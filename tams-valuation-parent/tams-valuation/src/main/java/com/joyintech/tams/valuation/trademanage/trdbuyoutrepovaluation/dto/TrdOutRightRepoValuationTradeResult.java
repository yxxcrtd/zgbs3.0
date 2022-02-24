/*
 * 文件名：NPrdDealForInterfRecProcessorResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年6月25日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.trademanage.trdbuyoutrepovaluation.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.common.interf.base.dto.RepurchaseDealInfoValCommonResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 买断式回购交易信息Processor返回共通结果
 * 
 * @author weipeng
 * @version 1.0 2018年6月25日
 * @see TrdOutRightRepoValuationTradeResult
 * @since 1.0
 */
public class TrdOutRightRepoValuationTradeResult extends BaseEntity {

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
     * 入账信息表
     */
    private List<BokDealAccounting> listValDealAccounting = new ArrayList<BokDealAccounting>();

    /**
     * 账务明细表
     */
    private List<BokDealAccountingDetail> listValDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();

    /**
     * 交易费用明细表
     */
    private List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
    /**
     * 账务明细代码表
     */
    private List<BokDetailId> listValBokDetailId=new ArrayList<BokDetailId>();

    /**
     * 日志
     */
    private List<ValInterflog> valInterflog = new ArrayList<ValInterflog>();

    public TrdOutRightRepoValuationTradeResult() {

    }

    private TrdOutRightRepoValuationTrade001Dto trdOutRightRepoValuationTrade001Dto;

    public TrdOutRightRepoValuationTrade001Dto getTrdOutRightRepoValuationTrade001Dto() {
        return trdOutRightRepoValuationTrade001Dto;
    }

    public void setTrdOutRightRepoValuationTrade001Dto(TrdOutRightRepoValuationTrade001Dto trdOutRightRepoValuationTrade001Dto) {
        this.trdOutRightRepoValuationTrade001Dto = trdOutRightRepoValuationTrade001Dto;
    }

    /**
     * 重载构造方法 从valuation-common中返回的result重新赋值
     *
     * @param rtnResult 共通结果
     */
    public TrdOutRightRepoValuationTradeResult(RepurchaseDealInfoValCommonResult rtnResult) {
        this.setListValDealAccounting(rtnResult.getListValDealAccounting());
        this.setListValDealAccountingDetail(rtnResult.getListValDealAccountingDetail());
        this.setListValTradeFeeDetail(rtnResult.getListValTradeFeeDetail());
        this.setListValFinProductDeal(rtnResult.getListValFinProductDeal());
        this.setListValFinProductDealExt(rtnResult.getListValFinProductDealExt());
        this.setValInterflog(rtnResult.getValInterflog());
        this.setListValBokDetailId(rtnResult.getListValBokDetailId());
        this.setListValOrders(rtnResult.getListValOrders());
        this.setListValOrdersExt(rtnResult.getListValOrdersExt());
        this.paramMap = rtnResult.getParamMap();
        this.setDatanum(rtnResult.getDatanum());
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
    public List<TrdProductDealAdd> getListValFinProductDealExt() {
        return listValFinProductDealExt;
    }

    /**
     * 设置 估值核算交易扩展表
     * 
     * @param listValFinProductDealExt 估值核算交易扩展表
     */
    public void setListValFinProductDealExt(List<TrdProductDealAdd> listValFinProductDealExt) {
        this.listValFinProductDealExt = listValFinProductDealExt;
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
     * 获取 交易费用明细表
     * 
     * @return 交易费用明细表.
     */
    public List<TrdTradeFeeDetail> getListValTradeFeeDetail() {
        return listValTradeFeeDetail;
    }

    /**
     * 设置 交易费用明细表
     * 
     * @param listValTradeFeeDetail 交易费用明细表
     */
    public void setListValTradeFeeDetail(List<TrdTradeFeeDetail> listValTradeFeeDetail) {
        this.listValTradeFeeDetail = listValTradeFeeDetail;
    }
    
    /**
     * 结算指令
     */
    private List<StlOrders> listValOrders = new ArrayList<StlOrders>();
    private List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();

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

	public List<StlOrders> getListValOrders() {
		return listValOrders;
	}

	public void setListValOrders(List<StlOrders> listValOrders) {
		this.listValOrders = listValOrders;
	}

	public List<StlOrdersExt> getListValOrdersExt() {
		return listValOrdersExt;
	}

	public void setListValOrdersExt(List<StlOrdersExt> listValOrdersExt) {
		this.listValOrdersExt = listValOrdersExt;
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
    
    private int datanum;//文件读取数据数

	/**
	 * @return the datanum
	 */
	public int getDatanum() {
		return datanum;
	}

	/**
	 * @param datanum the datanum to set
	 */
	public void setDatanum(int datanum) {
		this.datanum = datanum;
	}


    

}
