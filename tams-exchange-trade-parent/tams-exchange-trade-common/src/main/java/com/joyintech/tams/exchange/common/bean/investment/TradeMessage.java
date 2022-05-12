package com.joyintech.tams.exchange.common.bean.investment;

import com.joyintech.tams.exchange.common.constant.BusinessFlag;
import com.joyintech.tams.exchange.common.constant.EntrustProp;
import com.joyintech.tams.exchange.common.constant.InvestmentAssetType;
import com.joyintech.tams.exchange.common.db.domain.EntrustInterface;
import com.joyintech.tams.exchange.common.db.domain.Trade;
import com.joyintech.tams.exchange.common.utils.TradeUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;


public class TradeMessage extends BasicMessage {
    private static final long serialVersionUID = -7909494233501258978L;
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
    private BigDecimal dealPrice;       // 成交价格
    private Double fullPrice;       // 全价价格
    private BigDecimal dealBalance;     // 成交金额
    private String trdMatchID;      // 首期交易编号
    private String tradingDay;      // 交易日
    private String valueDate;       // 首期结算日
    private String maturityDate;    // 到期结算日
    private String ccy;             //币种
    private BigDecimal dealMargin;  //保证金
    private String settleDay;       // 结算日
    private String busiType;        // 业务类型（手工交收用）
    private String counterId;     // 交易对手
    private BigDecimal pledgeRatio;     // 折算比例%
    private Double entrustVolume;      // 委托数量

    /**
     * 下单组合明细数据消息
     */
    private List<PortfolioTradeMessage> portfolioTradeMessages;

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

    public BigDecimal getDealPrice() {
        return dealPrice;
    }

    public void setDealPrice(BigDecimal dealPrice) {
        this.dealPrice = dealPrice;
    }

    public Double getFullPrice() {
        return fullPrice;
    }

    public void setFullPrice(Double fullPrice) {
        this.fullPrice = fullPrice;
    }

    public BigDecimal getDealBalance() {
        return dealBalance;
    }

    public void setDealBalance(BigDecimal dealBalance) {
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

    public BigDecimal getDealMargin() {
        return dealMargin;
    }

    public void setDealMargin(BigDecimal dealMargin) {
        this.dealMargin = dealMargin;
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

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public BigDecimal getPledgeRatio() {
        return pledgeRatio;
    }

    public void setPledgeRatio(BigDecimal pledgeRatio) {
        this.pledgeRatio = pledgeRatio;
    }

    public Double getEntrustVolume() {
        return entrustVolume;
    }

    public void setEntrustVolume(Double entrustVolume) {
        this.entrustVolume = entrustVolume;
    }

    public List<PortfolioTradeMessage> getPortfolioTradeMessages() {
        return portfolioTradeMessages;
    }

    public void setPortfolioTradeMessages(List<PortfolioTradeMessage> portfolioTradeMessages) {
        this.portfolioTradeMessages = portfolioTradeMessages;
    }

    private static TradeMessage templateMessage = new TradeMessage();

    public static TradeMessage createTradeMessage(Trade trade, EntrustInterface entrust) {
        TradeMessage tradeMessage;
        try {
            tradeMessage = (TradeMessage) templateMessage.clone();
            tradeMessage.setId(trade.getId());
            tradeMessage.setEntrustId(trade.getInstructionId());
            tradeMessage.setOrderId(trade.getEntrustId());
            tradeMessage.setEntrustNo(trade.getBrokerEntrustId());
            tradeMessage.setDealNo(trade.getBrokerTradeId());
            tradeMessage.setAcctNo(trade.getFinanceAccount());
            tradeMessage.setFinprodMarketId(trade.getStockCode());
            tradeMessage.setDealVolume((double) trade.getTradedVolume());
            tradeMessage.setDealPrice(trade.getTradedPrice());
            tradeMessage.setDealBalance(trade.getTradedBalance());
            if (!BusinessFlag.PROTOCOL_BACK.value.equals(trade.getBusinessFlag())) {
                tradeMessage.setFullPrice(trade.getTradedVolume() > 0 ? TradeUtils.divide(trade.getTradedBalance(), trade.getTradedVolume(), 4).doubleValue() : 0);
            }
            //协议回购
            if (BusinessFlag.PROTOCOL_BACK.value.equals(trade.getBusinessFlag())) {
                //到期结算金额
                tradeMessage.setDealBalance(entrust.getBackBalance());
            } else if (InvestmentAssetType.isBond(trade.getAssetType())
                    && EntrustProp.isTrade(entrust.getEntrustProp())
                    && Objects.nonNull(tradeMessage.getFullPrice())
                    && tradeMessage.getFullPrice() != 0) {
                //成交全价金额
                tradeMessage.setDealBalance(TradeUtils.multiply(BigDecimal.valueOf(tradeMessage.getFullPrice()), tradeMessage.getDealVolume().longValue(), 2));
            }
            tradeMessage.setTradingDay(trade.getTradingDay());
        } catch (CloneNotSupportedException e) {
            return null;
        }
        return tradeMessage;
    }
}
