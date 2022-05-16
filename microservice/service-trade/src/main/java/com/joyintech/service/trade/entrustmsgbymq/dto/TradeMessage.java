package com.joyintech.service.trade.entrustmsgbymq.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class TradeMessage implements Cloneable, Serializable {
    private String entrustId;       // 委托代码
    private String orderId;         // 订单代码
    private String entrustNo;       // 委托编号
    private String dealNo;          // 成交编号
    private String portfolioId;     // 组合代码
    private String acctNo;          // 资金账户代码
    private String secAcctId;       // 证券账户代码
    private String finprodId;       // 金融产品代码
    private String tradeMarket;     // 交易场所
    private String tradePlat;       // 交易平台
    private String finprodMarketId; // 市场代码
    private String PS;              // 交易方向
    private Double dealVolume;      // 成交数量
    private Double dealPrice;       // 成交价格
    private Double fullPrice;       // 全价价格
    private Double dealBalance;     // 成交金额
    private String trdMatchID;      // 首期交易编号
    private String tradingDay;      // 交易日
    private String valueDate;       // 首期结算日
    private String maturityDate;    // 到期结算日
	private String ccy;             //币种
    private String settleDay; //交收日
    private String busiType; //业务类别
    private BigDecimal dealMargin;      //交易保证金
    private BigDecimal pledgeRatio;      //折算比例
    private String counterId;       //交易对手

    /**
     * 下单组合明细数据消息
     */
    private List<PortfolioTradeMessage> portfolioTradeMessages;

    public List<PortfolioTradeMessage> getPortfolioTradeMessages() {
        return portfolioTradeMessages;
    }

    public void setPortfolioTradeMessages(List<PortfolioTradeMessage> portfolioTradeMessages) {
        this.portfolioTradeMessages = portfolioTradeMessages;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getEntrustNo() {
        return entrustNo;
    }

    public void setEntrustNo(String entrustNo) {
        this.entrustNo = entrustNo;
    }

    public String getDealNo() {
        return dealNo;
    }

    public void setDealNo(String dealNo) {
        this.dealNo = dealNo;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getTradePlat() {
        return tradePlat;
    }

    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getPS() {
        return PS;
    }

    public void setPS(String PS) {
        this.PS = PS;
    }

    public Double getDealVolume() {
        return dealVolume;
    }

    public void setDealVolume(Double dealVolume) {
        this.dealVolume = dealVolume;
    }

    public Double getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(Double dealPrice) {
        this.dealPrice = dealPrice;
    }

    public Double getFullPrice() {
        return fullPrice;
    }

    public void setFullPrice(Double fullPrice) {
        this.fullPrice = fullPrice;
    }

    public Double getDealBalance() {
        return dealBalance;
    }

    public void setDealBalance(Double dealBalance) {
        this.dealBalance = dealBalance;
    }

    public String getTrdMatchID() {
        return trdMatchID;
    }

    public void setTrdMatchID(String trdMatchID) {
        this.trdMatchID = trdMatchID;
    }

    public String getTradingDay() {
        return tradingDay;
    }

    public void setTradingDay(String tradingDay) {
        this.tradingDay = tradingDay;
    }

    public String getValueDate() {
        return valueDate;
    }

    public void setValueDate(String valueDate) {
        this.valueDate = valueDate;
    }

    public String getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(String maturityDate) {
        this.maturityDate = maturityDate;
    }
	
    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getSettleDay() {
        return settleDay;
    }

    public void setSettleDay(String settleDay) {
        this.settleDay = settleDay;
    }

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }
    
    public BigDecimal getDealMargin() {
		return dealMargin;
	}

	public void setDealMargin(BigDecimal dealMargin) {
		this.dealMargin = dealMargin;
	}

    public BigDecimal getPledgeRatio() {
        return pledgeRatio;
    }

    public void setPledgeRatio(BigDecimal pledgeRatio) {
        this.pledgeRatio = pledgeRatio;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }
}
