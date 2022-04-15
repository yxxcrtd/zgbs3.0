/*
 * 文件名：EtrExchangeTradeQuotation001Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 *  行情信息(左上)
 * 〈功能详细描述〉
 * @author meifaxin
 * @version 1.0 2021年12月21日
 * @see EtrExchangeTradeQuotationHqSecRepoResponse
 * @since 1.0
 */
@ApiModel(value="行情信息查询返回参数",description="行情信息查询返回参数")
public class EtrExchangeTradeQuotationHqSecRepoResponse {


    @ApiModelProperty(name="secId",value="证券代码",example="A1")
    private String secId;
    
    @ApiModelProperty(name="secName",value="证券名称",example="A1")
    private String secName;
    
    @ApiModelProperty(name="finprodType",value="资产类型",example="A1")
    private String finprodType;

    @ApiModelProperty(name="openPrice",value="开盘价",example="A1")
    private BigDecimal openPrice;

    @ApiModelProperty(name="highPrice",value="最高价",example="12")
    private BigDecimal highPrice;
    
    @ApiModelProperty(name="lowPrice",value="最低价",example="12")
    private BigDecimal lowPrice;
    
    @ApiModelProperty(name="lastPrice",value="最新价",example="12")
    private BigDecimal lastPrice;
    
    @ApiModelProperty(name="preClosePrice",value="昨收盘价",example="12")
    private BigDecimal preClosePrice;
    
    @ApiModelProperty(name="volume",value="成交总量",example="12")
    private String volume;

    @ApiModelProperty(name="tradeBalance",value="平均价",example="12")
    private String tradeBalance;
    
    @ApiModelProperty(name="upperLimitPrice",value="涨停价",example="12")
    private BigDecimal upperLimitPrice;
    
    @ApiModelProperty(name="lowerLimitPrice",value="跌停价",example="12")
    private BigDecimal lowerLimitPrice;
    
    @ApiModelProperty(name="latest",value="最新价",example="12")
    private BigDecimal latest;
    
    @ApiModelProperty(name="preclose",value="预收价",example="12")
    private BigDecimal preclose;

    @ApiModelProperty(name="askPrice",value="卖出报价",example="12")
    private List<BigDecimal> askPrice;

    @ApiModelProperty(name="askVolume",value="卖出数量",example="12")
    private List<Integer> askVolume;

    @ApiModelProperty(name="bidPrice",value="买入报价",example="12")
    private List<BigDecimal> bidPrice;
    
    @ApiModelProperty(name="bidVolume",value="买入数量",example="12")
    private List<Integer> bidVolume;
    
    @ApiModelProperty(name="turnover",value="换手率",example="12")
    private BigDecimal turnover;
    
    @ApiModelProperty(name="position",value="合约持仓量(期)",example="12")
    private BigDecimal position;

    @ApiModelProperty(name="settlePrice",value="结算价",example="12")
    private String settlePrice;

    public String getSecId() {
        return secId;
    }

    public void setSecId(String secId) {
        this.secId = secId;
    }

    public String getSecName() {
        return secName;
    }

    public void setSecName(String secName) {
        this.secName = secName;
    }

    public String getFinprodType() {
        return finprodType;
    }

    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public void setOpenPrice(BigDecimal openPrice) {
        this.openPrice = openPrice;
    }

    public BigDecimal getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(BigDecimal highPrice) {
        this.highPrice = highPrice;
    }

    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(BigDecimal lowPrice) {
        this.lowPrice = lowPrice;
    }

    public BigDecimal getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(BigDecimal lastPrice) {
        this.lastPrice = lastPrice;
    }

    public BigDecimal getPreClosePrice() {
        return preClosePrice;
    }

    public void setPreClosePrice(BigDecimal preClosePrice) {
        this.preClosePrice = preClosePrice;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getTradeBalance() {
        return tradeBalance;
    }

    public void setTradeBalance(String tradeBalance) {
        this.tradeBalance = tradeBalance;
    }

    public BigDecimal getUpperLimitPrice() {
        return upperLimitPrice;
    }

    public void setUpperLimitPrice(BigDecimal upperLimitPrice) {
        this.upperLimitPrice = upperLimitPrice;
    }

    public BigDecimal getLowerLimitPrice() {
        return lowerLimitPrice;
    }

    public void setLowerLimitPrice(BigDecimal lowerLimitPrice) {
        this.lowerLimitPrice = lowerLimitPrice;
    }

    public BigDecimal getLatest() {
        return latest;
    }

    public void setLatest(BigDecimal latest) {
        this.latest = latest;
    }

    public BigDecimal getPreclose() {
        return preclose;
    }

    public void setPreclose(BigDecimal preclose) {
        this.preclose = preclose;
    }

    public List<BigDecimal> getAskPrice() {
        return askPrice;
    }

    public void setAskPrice(List<BigDecimal> askPrice) {
        this.askPrice = askPrice;
    }

    public List<Integer> getAskVolume() {
        return askVolume;
    }

    public void setAskVolume(List<Integer> askVolume) {
        this.askVolume = askVolume;
    }

    public List<BigDecimal> getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(List<BigDecimal> bidPrice) {
        this.bidPrice = bidPrice;
    }

    public List<Integer> getBidVolume() {
        return bidVolume;
    }

    public void setBidVolume(List<Integer> bidVolume) {
        this.bidVolume = bidVolume;
    }

    public BigDecimal getTurnover() {
        return turnover;
    }

    public void setTurnover(BigDecimal turnover) {
        this.turnover = turnover;
    }

    public BigDecimal getPosition() {
        return position;
    }

    public void setPosition(BigDecimal position) {
        this.position = position;
    }

    public String getSettlePrice() {
        return settlePrice;
    }

    public void setSettlePrice(String settlePrice) {
        this.settlePrice = settlePrice;
    }
}
