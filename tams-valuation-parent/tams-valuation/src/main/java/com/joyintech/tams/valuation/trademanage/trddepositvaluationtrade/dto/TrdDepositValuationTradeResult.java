/*
 * 文件名：NPrdDealForInterfRecProcessorResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年6月28日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.trademanage.trddepositvaluationtrade.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.common.interf.base.dto.AssetDealInfoValCommonResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 存款类交易交易信息Processor返回共通结果
 * 
 * @author weipeng
 * @version 1.0 2018年6月28日
 * @see TrdDepositValuationTradeResult
 * @since 1.0
 */
public class TrdDepositValuationTradeResult extends BaseEntity {

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
     * 金融产品实物交易表
     */
    private List<TrdFinProductBarter> listValFinProductBarter = new ArrayList<TrdFinProductBarter>();

    /**
     * 交易类金融产品
     */
    private List<FinTradeProduct> listValFinTradeProduct = new ArrayList<FinTradeProduct>();

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
     * 交易费用明细表
     */
    private List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();
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

    private TrdDepositValuationTrade001Dto trdDepositValuationTrade001Dto;

    public TrdDepositValuationTradeResult() {

    }

    /**
     * 重载构造方法 从valuation-common中返回的result重新赋值
     *
     * @param rtnResult 共通结果
     */
    public TrdDepositValuationTradeResult(AssetDealInfoValCommonResult result) {
        this.setListValDealAccounting(result.getListValDealAccounting());
        this.setListValDealAccountingDetail(result.getListValDealAccountingDetail());
        this.setListValTradeFeeDetail(result.getListValTradeFeeDetail());
        this.setListValFinProductDeal(result.getListValFinProductDeal());
        this.setListValFinProductDealExt(result.getListValFinProductDealExt());
        this.setListValFinProductBarter(result.getListValFinProductBarter());
        this.setValInterflog(result.getValInterflog());
        /***add by wujie on 20180712 start*/
        this.setListValBokDetailId(result.getListValBokDetailId());
        /***add by wujie on 20180712 end*/
        this.setValOrdersList(result.getListValOrders());
        this.setValOrdersExtList(result.getListValOrdersExt());
        this.setDataNum(result.getDataNum());
        /***add by tanxuan on 20191227 start*/
        this.setListValFinTradeProduct(result.getListValFinTradeProduct());
        /***add by tanxuan on 20191227 end*/
        this.paramMap = result.getParamMap();
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
    
    /**add by wujie on 20180712 start**/
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
	/**add by wujie on 20180712 end**/

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

    /**
     * 获取 listTrdFinProductBarter
     */
    public List<TrdFinProductBarter> getListValFinProductBarter() {
        return listValFinProductBarter;
    }
    /**
     * 设置 listTrdFinProductBarter
     * @param listValFinProductBarter
     */
    public void setListValFinProductBarter(List<TrdFinProductBarter> listValFinProductBarter) {
        this.listValFinProductBarter = listValFinProductBarter;
    }
    
    /**
     * 获取 listValFinTradeProduct
     * @return listValFinTradeProduct.
     */
    public List<FinTradeProduct> getListValFinTradeProduct() {
        return listValFinTradeProduct;
    }

    /**
     * 设置 listValFinTradeProduct
     * @param listValFinTradeProduct listValFinTradeProduct
     */
    public void setListValFinTradeProduct(List<FinTradeProduct> listValFinTradeProduct) {
        this.listValFinTradeProduct = listValFinTradeProduct;
    }

    public TrdDepositValuationTrade001Dto getTrdDepositValuationTrade001Dto() {
        return trdDepositValuationTrade001Dto;
    }

    public void setTrdDepositValuationTrade001Dto(TrdDepositValuationTrade001Dto trdDepositValuationTrade001Dto) {
        this.trdDepositValuationTrade001Dto = trdDepositValuationTrade001Dto;
    }
}
