package com.joyintech.tams.exchange.common.bean.instrument;

import com.google.common.base.MoreObjects;
import com.joyintech.tams.exchange.common.bean.MinPrice;
import com.joyintech.tams.exchange.common.bean.MinTrdUnitLimit;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

@ApiModel(value = "最小价差、买卖单位及交易上下限控制信息", description = "最小价差、买卖单位及交易上下限控制信息")
public class MinPriceAndMinTrdUnitLimitBean implements Serializable {
    private static final long serialVersionUID = -8413378793368636977L;

    @ApiModelProperty(required = false, name = "errorCode", value = "返回码")
    private int errorCode;

    @ApiModelProperty(required = false, name = "errorCode", value = "描述信息")
    private String errorMsg;

    @ApiModelProperty(required = false, name = "stockCode", value = "市场代码")
    private String stockCode;

    @ApiModelProperty(required = false, name = "minPriceList", value = "最小价差控制信息")
    private List<MinPrice> minPriceList;

    @ApiModelProperty(required = false, name = "minTrdUnitLimitList", value = "买卖单位及交易上下限控制信息")
    private List<MinTrdUnitLimit> minTrdUnitLimitList;

    @ApiModelProperty(required = false, name = "volumeMultiple", value = "")
    private int volumeMultiple = 1;

    @ApiModelProperty(required = false, name = "isFullPrice", value = "是否全价，默认为净价")
    private String isFullPrice = "N";

    @ApiModelProperty( name = "subStockType", value = "股票类型")
    private String subStockType;

    public MinPriceAndMinTrdUnitLimitBean() {
    }

    public MinPriceAndMinTrdUnitLimitBean(String stockCode, int errorCode, String errorMsg) {
        this.stockCode = stockCode;
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public List<MinPrice> getMinPriceList() {
        return minPriceList;
    }

    public void setMinPriceList(List<MinPrice> minPriceList) {
        this.minPriceList = minPriceList;
    }

    public List<MinTrdUnitLimit> getMinTrdUnitLimitList() {
        return minTrdUnitLimitList;
    }

    public void setMinTrdUnitLimitList(List<MinTrdUnitLimit> minTrdUnitLimitList) {
        this.minTrdUnitLimitList = minTrdUnitLimitList;
    }

    public int getVolumeMultiple() {
        return volumeMultiple;
    }

    public void setVolumeMultiple(int volumeMultiple) {
        this.volumeMultiple = volumeMultiple;
    }

    public String getIsFullPrice() {
        return isFullPrice;
    }

    public void setIsFullPrice(String isFullPrice) {
        this.isFullPrice = isFullPrice;
    }

    public String getSubStockType() {
        return subStockType;
    }

    public void setSubStockType(String subStockType) {
        this.subStockType = subStockType;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("errorCode", errorCode)
                .add("errorMsg", errorMsg)
                .add("stockCode", stockCode)
                .add("minPriceList", minPriceList)
                .add("minTrdUnitLimitList", minTrdUnitLimitList)
                .add("volumeMultiple", volumeMultiple)
                .add("isFullPrice", isFullPrice)
                .toString();
    }
}
