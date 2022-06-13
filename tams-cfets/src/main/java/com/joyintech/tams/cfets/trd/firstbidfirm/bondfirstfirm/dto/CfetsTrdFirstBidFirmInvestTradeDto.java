/*
 * 文件名：TrdBondInvestTradeDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Zane.Y
 * 修改时间：2018年9月7日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto;

import com.joyintech.tams.common.base.entity.*;

import java.util.List;

/**
 * 一级投标确认交易Dto
 *
 * @author jiangpeng
 * @date 2022/5/6 14:47
 */
public class CfetsTrdFirstBidFirmInvestTradeDto {
    private TrdFinProductDealTotal trdFinProductDealTotal;
    private TrdDealTotalAdd trdDealTotalAdd;
    private TrdTradeFeeTotal trdTradeFeeTotal;
    private StlPayOrders stlPayOrders;
    private TrdFinProductDealTotal trdFinProductDealTempTotal;
    private TrdDealTotalAdd trdDealTempTotalAdd;

    private WflTrdFinProductDealTotal wflTrdFinProductDealTotal;
    private WflTrdDealTotalAdd wflTrdDealTotalAdd;
    private WflStlPayOrders wflStlPayOrders;

    private List<TrdProductDeal> trdProductDeals;
    private List<TrdProductDealAdd> trdProductDealAdds;
    private List<TrdTradeFeeDetail> trdTradeFeeDetails;
    private List<PtlTrade> ptlTrades;

    private List<WflTrdProductDeal> wflTrdProductDeals;
    private List<WflTrdProductDealAdd> wflTrdProductDealAdds;
    private List<WflTrdTradeFeeDetail> wflTrdTradeFeeDetails;

    private List<TrdProductTempDeal> trdProductTempDeals;
    private List<TrdProductTempDealAdd> trdProductTempDealAdds;

    public TrdFinProductDealTotal getTrdFinProductDealTotal() {
        return trdFinProductDealTotal;
    }

    public void setTrdFinProductDealTotal(TrdFinProductDealTotal trdFinProductDealTotal) {
        this.trdFinProductDealTotal = trdFinProductDealTotal;
    }

    public TrdDealTotalAdd getTrdDealTotalAdd() {
        return trdDealTotalAdd;
    }

    public void setTrdDealTotalAdd(TrdDealTotalAdd trdDealTotalAdd) {
        this.trdDealTotalAdd = trdDealTotalAdd;
    }

    public TrdTradeFeeTotal getTrdTradeFeeTotal() {
        return trdTradeFeeTotal;
    }

    public void setTrdTradeFeeTotal(TrdTradeFeeTotal trdTradeFeeTotal) {
        this.trdTradeFeeTotal = trdTradeFeeTotal;
    }

    public StlPayOrders getStlPayOrders() {
        return stlPayOrders;
    }

    public void setStlPayOrders(StlPayOrders stlPayOrders) {
        this.stlPayOrders = stlPayOrders;
    }

    public List<TrdProductDeal> getTrdProductDeals() {
        return trdProductDeals;
    }

    public void setTrdProductDeals(List<TrdProductDeal> trdProductDeals) {
        this.trdProductDeals = trdProductDeals;
    }

    public List<TrdProductDealAdd> getTrdProductDealAdds() {
        return trdProductDealAdds;
    }

    public void setTrdProductDealAdds(List<TrdProductDealAdd> trdProductDealAdds) {
        this.trdProductDealAdds = trdProductDealAdds;
    }

    public List<TrdTradeFeeDetail> getTrdTradeFeeDetails() {
        return trdTradeFeeDetails;
    }

    public void setTrdTradeFeeDetails(List<TrdTradeFeeDetail> trdTradeFeeDetails) {
        this.trdTradeFeeDetails = trdTradeFeeDetails;
    }

    public List<PtlTrade> getPtlTrades() {
        return ptlTrades;
    }

    public void setPtlTrades(List<PtlTrade> ptlTrades) {
        this.ptlTrades = ptlTrades;
    }

    public TrdFinProductDealTotal getTrdFinProductDealTempTotal() {
        return trdFinProductDealTempTotal;
    }

    public void setTrdFinProductDealTempTotal(TrdFinProductDealTotal trdFinProductDealTempTotal) {
        this.trdFinProductDealTempTotal = trdFinProductDealTempTotal;
    }

    public TrdDealTotalAdd getTrdDealTempTotalAdd() {
        return trdDealTempTotalAdd;
    }

    public void setTrdDealTempTotalAdd(TrdDealTotalAdd trdDealTempTotalAdd) {
        this.trdDealTempTotalAdd = trdDealTempTotalAdd;
    }

    public List<TrdProductTempDeal> getTrdProductTempDeals() {
        return trdProductTempDeals;
    }

    public void setTrdProductTempDeals(List<TrdProductTempDeal> trdProductTempDeals) {
        this.trdProductTempDeals = trdProductTempDeals;
    }

    public List<TrdProductTempDealAdd> getTrdProductTempDealAdds() {
        return trdProductTempDealAdds;
    }

    public void setTrdProductTempDealAdds(List<TrdProductTempDealAdd> trdProductTempDealAdds) {
        this.trdProductTempDealAdds = trdProductTempDealAdds;
    }

    public WflTrdFinProductDealTotal getWflTrdFinProductDealTotal() {
        return wflTrdFinProductDealTotal;
    }

    public void setWflTrdFinProductDealTotal(WflTrdFinProductDealTotal wflTrdFinProductDealTotal) {
        this.wflTrdFinProductDealTotal = wflTrdFinProductDealTotal;
    }

    public WflTrdDealTotalAdd getWflTrdDealTotalAdd() {
        return wflTrdDealTotalAdd;
    }

    public void setWflTrdDealTotalAdd(WflTrdDealTotalAdd wflTrdDealTotalAdd) {
        this.wflTrdDealTotalAdd = wflTrdDealTotalAdd;
    }

    public WflStlPayOrders getWflStlPayOrders() {
        return wflStlPayOrders;
    }

    public void setWflStlPayOrders(WflStlPayOrders wflStlPayOrders) {
        this.wflStlPayOrders = wflStlPayOrders;
    }

    public List<WflTrdProductDeal> getWflTrdProductDeals() {
        return wflTrdProductDeals;
    }

    public void setWflTrdProductDeals(List<WflTrdProductDeal> wflTrdProductDeals) {
        this.wflTrdProductDeals = wflTrdProductDeals;
    }

    public List<WflTrdProductDealAdd> getWflTrdProductDealAdds() {
        return wflTrdProductDealAdds;
    }

    public void setWflTrdProductDealAdds(List<WflTrdProductDealAdd> wflTrdProductDealAdds) {
        this.wflTrdProductDealAdds = wflTrdProductDealAdds;
    }

    public List<WflTrdTradeFeeDetail> getWflTrdTradeFeeDetails() {
        return wflTrdTradeFeeDetails;
    }

    public void setWflTrdTradeFeeDetails(List<WflTrdTradeFeeDetail> wflTrdTradeFeeDetails) {
        this.wflTrdTradeFeeDetails = wflTrdTradeFeeDetails;
    }
}
