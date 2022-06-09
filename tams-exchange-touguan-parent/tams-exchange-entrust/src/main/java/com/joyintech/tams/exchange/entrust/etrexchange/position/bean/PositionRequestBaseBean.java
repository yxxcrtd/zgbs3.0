package com.joyintech.tams.exchange.entrust.etrexchange.position.bean;

import com.joyintech.background.component.common.pack.common.util.ArithmeticUtil;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.tams.exchange.entrust.etrexchange.common.dto.EtrExchangeTradePortfolioRequest;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradePortfolioSecRepoRequest;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * @author swwan
 * @date 2022/2/22 - 10:49
 */
public class PositionRequestBaseBean {

    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 证券代码
     */
    private String finprodId;

    /**
     * 证券管理账户
     */
    private String secAcctId;

    /**
     * 资金账号
     */
    private String cashAcctId;


    /**
     * 业务编号
     */
    private String tradeId;

    /**
     * 指令编号
     */
    private String etrId;

    /**
     * 交易数量
     */
    private BigDecimal secAmount;

    /**
     * 交易金额
     */
    private BigDecimal cashAmount;


    public PositionRequestBaseBean() {
    }

    public PositionRequestBaseBean(EtrExchangeTradePortfolioRequest request) {
        this.portfolioId = request.getPortfolioId();
        this.finprodId = request.getFinprodId();
        this.secAcctId = request.getSecAcctId();
        this.cashAcctId = request.getAcctNo();
        this.tradeId = null;
        this.etrId = request.getEntrustId();
        this.secAmount = request.getEntrustNum();
        BigDecimal entrustAmt = request.getEntrustAmt();
        if (Objects.isNull(entrustAmt) || BigDecimal.ZERO.compareTo(entrustAmt) < 0 || BigDecimal.ZERO.compareTo(entrustAmt) == 0) {
            BigDecimal entrustPrice= Objects.isNull(request.getEntrustPrice())?new BigDecimal(0):request.getEntrustPrice();
            this.cashAmount = this.secAmount.multiply(entrustPrice);
        } else {
            this.cashAmount = entrustAmt;
        }
    }

    public PositionRequestBaseBean(EtrExchangeTradePortfolioSecRepoRequest request) {
        this.portfolioId = request.getPortfolioId();
        this.finprodId = request.getFinprodId();
        this.secAcctId = request.getSecAcctId();
        this.cashAcctId = request.getAcctNo();
        this.tradeId = null;
        this.etrId = request.getEntrustId();
        this.secAmount = request.getEntrustNum();
        BigDecimal entrustAmt = request.getEntrustAmt();
        if (Objects.isNull(entrustAmt) || BigDecimal.ZERO.compareTo(entrustAmt) < 0 || BigDecimal.ZERO.compareTo(entrustAmt) == 0) {
            BigDecimal entrustPrice= Objects.isNull(request.getEntrustPrice())?new BigDecimal(0):request.getEntrustPrice();
            this.cashAmount = ArithmeticUtil.divide(this.secAmount.multiply(entrustPrice),BigDecimal.ONE,4);
        } else {
            this.cashAmount = ArithmeticUtil.divide(entrustAmt,BigDecimal.ONE,4);
        }
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getCashAcctId() {
        return cashAcctId;
    }

    public void setCashAcctId(String cashAcctId) {
        this.cashAcctId = cashAcctId;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getEtrId() {
        return etrId;
    }

    public void setEtrId(String etrId) {
        this.etrId = etrId;
    }

    public BigDecimal getSecAmount() {
        return secAmount;
    }

    public void setSecAmount(BigDecimal secAmount) {
        this.secAmount = secAmount;
    }

    public BigDecimal getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(BigDecimal cashAmount) {
        this.cashAmount = cashAmount;
    }
}
