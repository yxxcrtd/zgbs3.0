/*
 * 文件名：FundDealInfoRecProcessorResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：HuangJian
 * 修改时间：2018年6月27日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.valuation.trademanage.trdlendvaluationtrade.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.common.interf.base.dto.InterfProductDealBookCommonDto;
import com.joyintech.tams.valuation.common.interf.base.dto.LendDealCommonResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 拆借交易
 * 
 * @author HuangJian
 * @version 1.0 2018年6月27日
 * @see TrdLendValuationTradeResult
 * @since 1.0
 */
public class TrdLendValuationTradeResult extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * @param result
     */
    public TrdLendValuationTradeResult(LendDealCommonResult result) {
        this.listValFinProductDeal = result.getListValFinProductDeal();
        this.listValFinProductDealExt = result.getListValFinProductDealExt();
        this.listValTradeFeeDetail = result.getListValTradeFeeDetail();
        this.listValInterflog = result.getListValInterflog();
        this.listInterfProductDealBookCommonDto = result.getListInterfProductDealBookCommonDto();
        this.setListValOrders(result.getListValOrders());
        this.setListValOrdersExt(result.getListValOrdersExt());
        this.setDataNum(result.getDataNum());
        /**add by wujie on 20181213 start**/
        this.setListValBokDetailId(result.getBokDetailIdList());
        this.setListValDealAccounting(result.getBokDealAccountingList());
        this.setListValDealAccountingDetail(result.getBokDealAccountingDetailList());
        /**add by wujie on 20181213 end**/
        /***add by tanxuan on 20191227 start*/
        this.setListValFinTradeProduct(result.getListValFinTradeProduct());
        /***add by tanxuan on 20191227 end*/
        this.paramMap = result.getParamMap();
    }

    public TrdLendValuationTradeResult() {
    }

    private TrdLendValuationTradeDto trdLendValuationTradeDto;

    public TrdLendValuationTradeDto getTrdLendValuationTradeDto() {
        return trdLendValuationTradeDto;
    }

    public void setTrdLendValuationTradeDto(TrdLendValuationTradeDto trdLendValuationTradeDto) {
        this.trdLendValuationTradeDto = trdLendValuationTradeDto;
    }

    /**
     * 金融产品交易表
     */
    private List<TrdProductDeal> listValFinProductDeal = new ArrayList<TrdProductDeal>();;

    /**
     * 估值核算交易扩展
     */
    private List<TrdProductDealAdd> listValFinProductDealExt = new ArrayList<TrdProductDealAdd>();
    
    /**
     * 交易类金融产品
     */
    private List<FinTradeProduct> listValFinTradeProduct = new ArrayList<FinTradeProduct>();
    
    /**
     * 账务明细代码表
     */
    private List<BokDetailId> listValBokDetailId=new ArrayList<BokDetailId>();

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
     * 日志
     */
    private List<ValInterflog> listValInterflog = new ArrayList<ValInterflog>();
    
    /**
     * 结算指令
     */
    private List<StlOrders> listValOrders = new ArrayList<StlOrders>();
    private List<StlOrdersExt> listValOrdersExt = new ArrayList<StlOrdersExt>();
    
    /**
     * 交易入账
     */
    private List<InterfProductDealBookCommonDto> listInterfProductDealBookCommonDto = new ArrayList<InterfProductDealBookCommonDto>();

    
    /**
     * 读取数据数量
     */
    private int dataNum;
    
    /**
     * 获取 listValFinProductDeal
     * 
     * @return listValFinProductDeal.
     */
    public List<TrdProductDeal> getListValFinProductDeal() {
        return listValFinProductDeal;
    }

    /**
     * 设置 listValFinProductDeal
     * 
     * @param listValFinProductDeal listValFinProductDeal
     */
    public void setListValFinProductDeal(List<TrdProductDeal> listValFinProductDeal) {
        this.listValFinProductDeal = listValFinProductDeal;
    }

    /**
     * 获取 listValFinProductDealExt
     * 
     * @return listValFinProductDealExt.
     */
    public List<TrdProductDealAdd> getListValFinProductDealExt() {
        return listValFinProductDealExt;
    }

    /**
     * 设置 listValFinProductDealExt
     * 
     * @param listValFinProductDealExt listValFinProductDealExt
     */
    public void setListValFinProductDealExt(List<TrdProductDealAdd> listValFinProductDealExt) {
        this.listValFinProductDealExt = listValFinProductDealExt;
    }

    /**
     * 获取 listValDealAccounting
     * 
     * @return listValDealAccounting.
     */
    public List<BokDealAccounting> getListValDealAccounting() {
        return listValDealAccounting;
    }

    /**
     * 设置 listValDealAccounting
     * 
     * @param listValDealAccounting listValDealAccounting
     */
    public void setListValDealAccounting(List<BokDealAccounting> listValDealAccounting) {
        this.listValDealAccounting = listValDealAccounting;
    }

    /**
     * 获取 listValDealAccountingDetail
     * 
     * @return listValDealAccountingDetail.
     */
    public List<BokDealAccountingDetail> getListValDealAccountingDetail() {
        return listValDealAccountingDetail;
    }

    /**
     * 设置 listValDealAccountingDetail
     * 
     * @param listValDealAccountingDetail listValDealAccountingDetail
     */
    public void setListValDealAccountingDetail(List<BokDealAccountingDetail> listValDealAccountingDetail) {
        this.listValDealAccountingDetail = listValDealAccountingDetail;
    }

    /**
     * 获取 listValTradeFeeDetail
     * 
     * @return listValTradeFeeDetail.
     */
    public List<TrdTradeFeeDetail> getListValTradeFeeDetail() {
        return listValTradeFeeDetail;
    }

    /**
     * 设置 listValTradeFeeDetail
     * 
     * @param listValTradeFeeDetail listValTradeFeeDetail
     */
    public void setListValTradeFeeDetail(List<TrdTradeFeeDetail> listValTradeFeeDetail) {
        this.listValTradeFeeDetail = listValTradeFeeDetail;
    }

    /**
     * 获取 listValInterflog
     * 
     * @return listValInterflog.
     */
    public List<ValInterflog> getListValInterflog() {
        return listValInterflog;
    }

    /**
     * 设置 listValInterflog
     * 
     * @param listValInterflog listValInterflog
     */
    public void setListValInterflog(List<ValInterflog> listValInterflog) {
        this.listValInterflog = listValInterflog;
    }

    
    /**
     * 获取 listInterfProductDealBookCommonDto
     * @return listInterfProductDealBookCommonDto.
     */
    public List<InterfProductDealBookCommonDto> getListInterfProductDealBookCommonDto() {
        return listInterfProductDealBookCommonDto;
    }

    
    /**
     * 设置 listInterfProductDealBookCommonDto
     * @param listInterfProductDealBookCommonDto listInterfProductDealBookCommonDto
     */
    public void setListInterfProductDealBookCommonDto(List<InterfProductDealBookCommonDto> listInterfProductDealBookCommonDto) {
        this.listInterfProductDealBookCommonDto = listInterfProductDealBookCommonDto;
    }

	public List<BokDetailId> getListValBokDetailId() {
		return listValBokDetailId;
	}

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

}
