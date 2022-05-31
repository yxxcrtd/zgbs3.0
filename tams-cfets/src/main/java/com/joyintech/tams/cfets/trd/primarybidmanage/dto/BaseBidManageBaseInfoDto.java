package com.joyintech.tams.cfets.trd.primarybidmanage.dto;

import com.joyintech.tams.common.base.entity.*;
import com.joyintech.tams.invest.common.dto.period.common.BussTrdTransferOrderDto;

import java.util.List;

/**
 *  流程生效入【金融产品交易汇总表】、【金融产品交易表】、【金融产品交易附属表】、【汇总交易费用明细表】、【交易费用明细表】、【组合交易表】
 *             、【委托完成状况表】、【结算指令表】
 * 〈功能详细描述〉
 * @author wangyang1
 * @version 1.0 2022年5月13日
 * @see BaseBidManageBaseInfoDto
 * @since 1.0
 */
public class BaseBidManageBaseInfoDto {


    /**
     * 中标结果汇总表entity
     */
    private BidResultTotal bidResultTotal;

    /**
     * 中标结果明细表entity
     */
    private List<BidResultDetail> bidResultDetailList;

    /**
     * 金融产品交易汇总表entity
     */
    private List<TrdFinProductDealTotal> trdFinProductDealTotalList;

    /**
     * 金融产品交易表entity
     */
    private List<TrdProductDeal> trdProductDealList;

    /**
     * 金融产品交易表dtolist
     */
    private List<TrdProductDealAdd> trdProductDealAddList;

    /**
     * 汇总交易费用明细表entity
     */
    private List<TrdTradeFeeTotal> trdTradeFeeTotalList;

    /**
     * 交易费用明细表dtolist
     */
    private List<TrdTradeFeeDetail> trdTradeFeeDetailList;

    /**
     * 组合交易表dtolist
     */
    private List<PtlTrade> ptlTradeList;

    /**
     * 委托完成状况表entity
     */
    private List<EtrFinishStatus> etrFinishStatusList;

    /**
     * 结算指令表entity
     */
    private List<StlOrders> stlOrdersList;

    private List<BussTrdTransferOrderDto> bussTrdTransferOrderList;

    /**
     * 获取 中标结果汇总表entity
     *
     * @return bidResultTotal 中标结果汇总表entity
     */
    public BidResultTotal getBidResultTotal() {
        return this.bidResultTotal;
    }

    /**
     * 设置 中标结果汇总表entity
     *
     * @param bidResultTotal 中标结果汇总表entity
     */
    public void setBidResultTotal(BidResultTotal bidResultTotal) {
        this.bidResultTotal = bidResultTotal;
    }


    /**
     * 获取 金融产品交易汇总表entity
     *
     * @return trdFinProductDealTotalList 金融产品交易汇总表entity
     */
    public List<TrdFinProductDealTotal> getTrdFinProductDealTotalList() {
        return this.trdFinProductDealTotalList;
    }

    /**
     * 设置 金融产品交易汇总表entity
     *
     * @param trdFinProductDealTotalList 金融产品交易汇总表entity
     */
    public void setTrdFinProductDealTotalList(List<TrdFinProductDealTotal> trdFinProductDealTotalList) {
        this.trdFinProductDealTotalList = trdFinProductDealTotalList;
    }

    /**
     * 获取 金融产品交易表entity
     *
     * @return trdProductDealList 金融产品交易表entity
     */
    public List<TrdProductDeal> getTrdProductDealList() {
        return this.trdProductDealList;
    }

    /**
     * 设置 金融产品交易表entity
     *
     * @param trdProductDealList 金融产品交易表entity
     */
    public void setTrdProductDealList(List<TrdProductDeal> trdProductDealList) {
        this.trdProductDealList = trdProductDealList;
    }

    /**
     * 获取 金融产品交易表dtolist
     *
     * @return trdProductDealAddList 金融产品交易表dtolist
     */
    public List<TrdProductDealAdd> getTrdProductDealAddList() {
        return this.trdProductDealAddList;
    }

    /**
     * 设置 金融产品交易表dtolist
     *
     * @param trdProductDealAddList 金融产品交易表dtolist
     */
    public void setTrdProductDealAddList(List<TrdProductDealAdd> trdProductDealAddList) {
        this.trdProductDealAddList = trdProductDealAddList;
    }

    /**
     * 获取 汇总交易费用明细表entity
     *
     * @return trdTradeFeeTotalList 汇总交易费用明细表entity
     */
    public List<TrdTradeFeeTotal> getTrdTradeFeeTotalList() {
        return this.trdTradeFeeTotalList;
    }

    /**
     * 设置 汇总交易费用明细表entity
     *
     * @param trdTradeFeeTotalList 汇总交易费用明细表entity
     */
    public void setTrdTradeFeeTotalList(List<TrdTradeFeeTotal> trdTradeFeeTotalList) {
        this.trdTradeFeeTotalList = trdTradeFeeTotalList;
    }

    /**
     * 获取 交易费用明细表dtolist
     *
     * @return trdTradeFeeDetailList 交易费用明细表dtolist
     */
    public List<TrdTradeFeeDetail> getTrdTradeFeeDetailList() {
        return this.trdTradeFeeDetailList;
    }

    /**
     * 设置 交易费用明细表dtolist
     *
     * @param trdTradeFeeDetailList 交易费用明细表dtolist
     */
    public void setTrdTradeFeeDetailList(List<TrdTradeFeeDetail> trdTradeFeeDetailList) {
        this.trdTradeFeeDetailList = trdTradeFeeDetailList;
    }

    /**
     * 获取 组合交易表dtolist
     *
     * @return ptlTradeList 组合交易表dtolist
     */
    public List<PtlTrade> getPtlTradeList() {
        return this.ptlTradeList;
    }

    /**
     * 设置 组合交易表dtolist
     *
     * @param ptlTradeList 组合交易表dtolist
     */
    public void setPtlTradeList(List<PtlTrade> ptlTradeList) {
        this.ptlTradeList = ptlTradeList;
    }


    /**
     * 获取 委托完成状况表entity
     *
     * @return etrFinishStatusList 委托完成状况表entity
     */
    public List<EtrFinishStatus> getEtrFinishStatusList() {
        return this.etrFinishStatusList;
    }

    /**
     * 设置 委托完成状况表entity
     *
     * @param etrFinishStatusList 委托完成状况表entity
     */
    public void setEtrFinishStatusList(List<EtrFinishStatus> etrFinishStatusList) {
        this.etrFinishStatusList = etrFinishStatusList;
    }

    /**
     * 获取 结算指令表entity
     *
     * @return stlOrdersList 结算指令表entity
     */
    public List<StlOrders> getStlOrdersList() {
        return this.stlOrdersList;
    }

    /**
     * 设置 结算指令表entity
     *
     * @param stlOrdersList 结算指令表entity
     */
    public void setStlOrdersList(List<StlOrders> stlOrdersList) {
        this.stlOrdersList = stlOrdersList;
    }

    /**
     * 获取 中标结果明细表entity
     *
     * @return bidResultDetailList 中标结果明细表entity
     */
    public List<BidResultDetail> getBidResultDetailList() {
        return this.bidResultDetailList;
    }

    /**
     * 设置 中标结果明细表entity
     *
     * @param bidResultDetailList 中标结果明细表entity
     */
    public void setBidResultDetailList(List<BidResultDetail> bidResultDetailList) {
        this.bidResultDetailList = bidResultDetailList;
    }

    public List<BussTrdTransferOrderDto> getBussTrdTransferOrderList() {
        return bussTrdTransferOrderList;
    }

    public void setBussTrdTransferOrderList(List<BussTrdTransferOrderDto> bussTrdTransferOrderList) {
        this.bussTrdTransferOrderList = bussTrdTransferOrderList;
    }
}