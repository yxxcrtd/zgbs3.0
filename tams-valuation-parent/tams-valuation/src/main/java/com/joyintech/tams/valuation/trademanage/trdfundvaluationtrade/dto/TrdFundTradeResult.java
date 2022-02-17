/*
 * 文件名：NPrdDealForInterfRecProcessorResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：weipeng
 * 修改时间：2018年6月28日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.trademanage.trdfundvaluationtrade.dto;

import com.joyintech.framework.base.BaseEntity;
import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.valuation.common.interf.base.dto.FundDealInfoForInterfRecCommonResult;
import com.joyintech.tams.valuation.common.interf.base.dto.InterfProductDealBookCommonDto;
import com.joyintech.tams.valuation.common.interf.base.dto.PlanAssetCurrencyDealCommonResult;
import com.joyintech.tams.valuation.trademanage.bond.dto.TrdBondTradeResult;
import com.joyintech.tams.valuation.trademanage.trdplanvaluationtrade.dto.TrdPlanValuationTradeDto;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 债券交易信息Processor返回共通结果
 * 
 * @author weipeng
 * @version 1.0 2018年6月28日
 * @see TrdBondTradeResult
 * @since 1.0
 */
public class TrdFundTradeResult extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private TrdFundValuationTradeDto trdFundValuationTradeDto;


    /**
     *基金型/净值型
     */
    public TrdFundTradeResult(FundDealInfoForInterfRecCommonResult result) {
        this.datanum = result.getDataNum();
        this.listBokDealAccounting = result.getListValDealAccounting();
        this.listBokDealAccountingDetail = result.getListValDealAccountingDetail();
        this.listBokDetailId = result.getListValBokDetailId();
        this.listStlOrders = result.getListValOrders();
        this.listStlOrdersExt = result.getListValOrdersExt();
        this.listTrdProductDeal = result.getListValFinProductDeal();
        this.listTrdProductDealExt = result.getListValFinProductDealExt();
        this.valInterflog = result.getListValInterflog();
        /**ADD BY WUJIE ON 20210501 START**/
        this.listValTradeFeeDetail = result.getListValTradeFeeDetail();
        this.listInterfProductDealBookCommonDto =result.getListInterfProductDealBookCommonDto();
        /**ADD BY WUJIE ON 20210501 END**/
        this.paramMap = result.getParamMap();
    }

    /**
     *
     */
    public TrdFundTradeResult() {
        super();
    }

    /**
     * 金融产品交易
     */
    private List<TrdProductDeal> listTrdProductDeal = new ArrayList<TrdProductDeal>();

    /**
     * 估值核算交易扩展表
     */
    private List<TrdProductDealAdd> listTrdProductDealExt = new ArrayList<TrdProductDealAdd>();

    /**
     * 入账信息表
     */
    private List<BokDealAccounting> listBokDealAccounting = new ArrayList<BokDealAccounting>();

    /**
     * 账务明细表
     */
    private List<BokDealAccountingDetail> listBokDealAccountingDetail = new ArrayList<BokDealAccountingDetail>();

    /**
     * 账务明细代码表
     */
    private List<BokDetailId> listBokDetailId = new ArrayList<BokDetailId>();

    /**
     * 结算指令表
     */
    private List<StlOrders> listStlOrders = new ArrayList<StlOrders>();

    /**
     * 结算指令扩展表
     */
    private List<StlOrdersExt> listStlOrdersExt = new ArrayList<StlOrdersExt>();

    /**
     * 交易费用明细表
     * ADD BY WUJIE ON 20210501
     */
    private List<TrdTradeFeeDetail> listValTradeFeeDetail = new ArrayList<TrdTradeFeeDetail>();


    /**
     * 交易入账
     */
    private List<InterfProductDealBookCommonDto> listInterfProductDealBookCommonDto = new ArrayList<InterfProductDealBookCommonDto>();



    /**
     * 获取 listTrdProductDeal
     *
     * @return listTrdProductDeal.
     */
    public List<TrdProductDeal> getListTrdProductDeal() {
        return listTrdProductDeal;
    }

    /**
     * 设置 listTrdProductDeal
     *
     * @param listTrdProductDeal listTrdProductDeal
     */
    public void setListTrdProductDeal(List<TrdProductDeal> listTrdProductDeal) {
        this.listTrdProductDeal = listTrdProductDeal;
    }

    /**
     * 获取 listTrdProductDealExt
     *
     * @return listTrdProductDealExt.
     */
    public List<TrdProductDealAdd> getListTrdProductDealExt() {
        return listTrdProductDealExt;
    }

    /**
     * 设置 listTrdProductDealExt
     *
     * @param listTrdProductDealExt listTrdProductDealExt
     */
    public void setListTrdProductDealExt(List<TrdProductDealAdd> listTrdProductDealExt) {
        this.listTrdProductDealExt = listTrdProductDealExt;
    }

    /**
     * 获取 listBokDealAccounting
     *
     * @return listBokDealAccounting.
     */
    public List<BokDealAccounting> getListBokDealAccounting() {
        return listBokDealAccounting;
    }

    /**
     * 设置 listBokDealAccounting
     *
     * @param listBokDealAccounting listBokDealAccounting
     */
    public void setListBokDealAccounting(List<BokDealAccounting> listBokDealAccounting) {
        this.listBokDealAccounting = listBokDealAccounting;
    }

    /**
     * 获取 listBokDealAccountingDetail
     *
     * @return listBokDealAccountingDetail.
     */
    public List<BokDealAccountingDetail> getListBokDealAccountingDetail() {
        return listBokDealAccountingDetail;
    }

    /**
     * 设置 listBokDealAccountingDetail
     *
     * @param listBokDealAccountingDetail listBokDealAccountingDetail
     */
    public void setListBokDealAccountingDetail(List<BokDealAccountingDetail> listBokDealAccountingDetail) {
        this.listBokDealAccountingDetail = listBokDealAccountingDetail;
    }

    /**
     * 获取 listBokDetailId
     *
     * @return listBokDetailId.
     */
    public List<BokDetailId> getListBokDetailId() {
        return listBokDetailId;
    }

    /**
     * 设置 listBokDetailId
     *
     * @param listBokDetailId listBokDetailId
     */
    public void setListBokDetailId(List<BokDetailId> listBokDetailId) {
        this.listBokDetailId = listBokDetailId;
    }

    /**
     * 获取 listStlOrders
     *
     * @return listStlOrders.
     */
    public List<StlOrders> getListStlOrders() {
        return listStlOrders;
    }

    /**
     * 设置 listStlOrders
     *
     * @param listStlOrders listStlOrders
     */
    public void setListStlOrders(List<StlOrders> listStlOrders) {
        this.listStlOrders = listStlOrders;
    }

    /**
     * 获取 listStlOrdersExt
     *
     * @return listStlOrdersExt.
     */
    public List<StlOrdersExt> getListStlOrdersExt() {
        return listStlOrdersExt;
    }

    /**
     * 设置 listStlOrdersExt
     *
     * @param listStlOrdersExt listStlOrdersExt
     */
    public void setListStlOrdersExt(List<StlOrdersExt> listStlOrdersExt) {
        this.listStlOrdersExt = listStlOrdersExt;
    }

    /**
     * 日志
     */
    private List<ValInterflog> valInterflog = new ArrayList<ValInterflog>();

    /**
     * 获取 valInterflog
     *
     * @return valInterflog.
     */
    public List<ValInterflog> getValInterflog() {
        return valInterflog;
    }

    /**
     * 设置 valInterflog
     *
     * @param valInterflog valInterflog
     */
    public void setValInterflog(List<ValInterflog> valInterflog) {
        this.valInterflog = valInterflog;
    }

    private int datanum;// 读取数据数

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

    /**
     * 用于存放去重数据筛选条件
     */
    private Map<String, Object> paramMap;

    public Map<String, Object> getParamMap() {
        return paramMap;
    }

    public void setParamMap(Map<String, Object> paramMap) {
        this.paramMap = paramMap;
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

    public List<InterfProductDealBookCommonDto> getListInterfProductDealBookCommonDto() {
        return listInterfProductDealBookCommonDto;
    }

    public void setListInterfProductDealBookCommonDto(List<InterfProductDealBookCommonDto> listInterfProductDealBookCommonDto) {
        this.listInterfProductDealBookCommonDto = listInterfProductDealBookCommonDto;
    }

    public TrdFundValuationTradeDto getTrdFundValuationTradeDto() {
        return trdFundValuationTradeDto;
    }

    public void setTrdFundValuationTradeDto(TrdFundValuationTradeDto trdFundValuationTradeDto) {
        this.trdFundValuationTradeDto = trdFundValuationTradeDto;
    }
}
