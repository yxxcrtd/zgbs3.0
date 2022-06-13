/*
 * 文件名：NPrdDealForInterfRecProcessorResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年6月25日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.interf.base.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.common.base.entity.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 交易接口 模板处理返回共通结果
 * 
 * @author weipeng
 * @version 1.0 2018年6月25日
 * @see AssetDealInfoValCommonResult
 * @since 1.0
 */
public class AssetDealInfoValCommonResult extends BaseEntity {

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
     * 金融产品标的信息
     */
    private List<FinDealTarget> listFinDealTarget=new ArrayList<FinDealTarget>();
    /**
     * 结算指令
     */
    private List<StlOrders> listValOrders = new ArrayList<StlOrders>();
    private List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
    
    /**
     * 交易类金融产品
     */
    private List<FinTradeProduct> listValFinTradeProduct = new ArrayList<FinTradeProduct>();

    /**
     * 行权申请更新list
     */
    private List<SecExecApplyDto> listSecExecApply = new ArrayList<>();

    /**
     * 股票锁定业务表list
     */
    private List<TrdTradeNewStockLocked> listTrdTradeNewStockLocked = new ArrayList<>();
    
    /**
     * 日志
     */
    private List<ValInterflog> valInterflog = new ArrayList<ValInterflog>();

    private Map<String,Object> paramMap = new HashMap<String,Object>();
    
    /**
     * 读取数据数量
     */
    private int dataNum;

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
    }
    
    /**
     * 获取 listValFinProductDeal
     * @return listValFinProductDeal.
     */
    public List<TrdProductDeal> getListValFinProductDeal() {
        return listValFinProductDeal;
    }

    
    /**
     * 设置 listValFinProductDeal
     * @param listValFinProductDeal listValFinProductDeal
     */
    public void setListValFinProductDeal(List<TrdProductDeal> listValFinProductDeal) {
        this.listValFinProductDeal = listValFinProductDeal;
    }

    
    /**
     * 获取 listValFinProductDealExt
     * @return listValFinProductDealExt.
     */
    public List<TrdProductDealAdd> getListValFinProductDealExt() {
        return listValFinProductDealExt;
    }

    
    /**
     * 设置 listValFinProductDealExt
     * @param listValFinProductDealExt listValFinProductDealExt
     */
    public void setListValFinProductDealExt(List<TrdProductDealAdd> listValFinProductDealExt) {
        this.listValFinProductDealExt = listValFinProductDealExt;
    }

    
    /**
     * 获取 listValDealAccounting
     * @return listValDealAccounting.
     */
    public List<BokDealAccounting> getListValDealAccounting() {
        return listValDealAccounting;
    }

    
    /**
     * 设置 listValDealAccounting
     * @param listValDealAccounting listValDealAccounting
     */
    public void setListValDealAccounting(List<BokDealAccounting> listValDealAccounting) {
        this.listValDealAccounting = listValDealAccounting;
    }

    
    /**
     * 获取 listValDealAccountingDetail
     * @return listValDealAccountingDetail.
     */
    public List<BokDealAccountingDetail> getListValDealAccountingDetail() {
        return listValDealAccountingDetail;
    }

    
    /**
     * 设置 listValDealAccountingDetail
     * @param listValDealAccountingDetail listValDealAccountingDetail
     */
    public void setListValDealAccountingDetail(List<BokDealAccountingDetail> listValDealAccountingDetail) {
        this.listValDealAccountingDetail = listValDealAccountingDetail;
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
     * 获取 listValTradeFeeDetail
     * @return listValTradeFeeDetail.
     */
    public List<TrdTradeFeeDetail> getListValTradeFeeDetail() {
        return listValTradeFeeDetail;
    }

    
    /**
     * 设置 listValTradeFeeDetail
     * @param listValTradeFeeDetail listValTradeFeeDetail
     */
    public void setListValTradeFeeDetail(List<TrdTradeFeeDetail> listValTradeFeeDetail) {
        this.listValTradeFeeDetail = listValTradeFeeDetail;
    }

    
    /**
     * 获取 listValOrders
     * @return listValOrders.
     */
    public List<StlOrders> getListValOrders() {
        return listValOrders;
    }

    
    /**
     * 设置 listValOrders
     * @param listValOrders listValOrders
     */
    public void setListValOrders(List<StlOrders> listValOrders) {
        this.listValOrders = listValOrders;
    }

    
    /**
     * 获取 listValOrdersExt
     * @return listValOrdersExt.
     */
    public List<StlOrdersExt> getListValOrdersExt() {
        return listValOrdersExt;
    }

    
    /**
     * 设置 listValOrdersExt
     * @param listValOrdersExt listValOrdersExt
     */
    public void setListValOrdersExt(List<StlOrdersExt> listValOrdersExt) {
        this.listValOrdersExt = listValOrdersExt;
    }

    
    /**
     * 获取 valInterflog
     * @return valInterflog.
     */
    public List<ValInterflog> getValInterflog() {
        return valInterflog;
    }

    
    /**
     * 设置 valInterflog
     * @param valInterflog valInterflog
     */
    public void setValInterflog(List<ValInterflog> valInterflog) {
        this.valInterflog = valInterflog;
    }

    
    /**
     * 获取 serialversionuid
     * @return serialversionuid.
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
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
     * 获取 listFinDealTarget
     * @param FinDealTarget finDealTarget
     */
	public List<FinDealTarget> getListFinDealTarget() {
		return listFinDealTarget;
	}
	 /**
     * 设置 listFinDealTarget
     * @param listFinDealTarget 
     */
	public void setListFinDealTarget(List<FinDealTarget> listFinDealTarget) {
		this.listFinDealTarget = listFinDealTarget;
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

    public List<SecExecApplyDto> getListSecExecApply() {
        return listSecExecApply;
    }

    public void setListSecExecApply(List<SecExecApplyDto> listSecExecApply) {
        this.listSecExecApply = listSecExecApply;
    }

    /**
     * 获取 listTrdTradeNewStockLocked
     * @@return listTrdTradeNewStockLocked
     */
    public List<TrdTradeNewStockLocked> getListTrdTradeNewStockLocked() {
        return listTrdTradeNewStockLocked;
    }

    /**
     * 设置 listTrdTradeNewStockLocked
     * @param listTrdTradeNewStockLocked
     */
    public void setListTrdTradeNewStockLocked(List<TrdTradeNewStockLocked> listTrdTradeNewStockLocked) {
        this.listTrdTradeNewStockLocked = listTrdTradeNewStockLocked;
    }
}
