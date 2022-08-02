package com.joyintech.service.trade.investtrade.commanddispatcher.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;

public class EntrTradePledgeDto  extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * 委托代码
     */
    private String entrustId;

    /**
     * 金融产品代码
     */
    private String finprodId;
    
    /**
     * 金融产品简称
     */
    private String finprodAbbr;
    
    /**
     * 交易场所，银行间、上交所、深交所、柜面等
     */
    private String tradeMarket;
    
    /**
     * 市场代码
     */
    private String finprodMarketId;
    
   
    /**
     * 质押面值
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal pledgeFaceValue;
    
    /**
     * 质押数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal pledgeNumber;
  

    /**
     * 质押率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal pledgeRatio;
    
    /**
     * 质押金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal pledgeAmt;

	/**
	 * 原金融产品代码
	 */
	private String oldFinprodId;

	/**
	 * 原金融产品简称
	 */
	private String oldFinprodAbbr;

	/**
	 * 原交易场所，银行间、上交所、深交所、柜面等
	 */
	private String oldTradeMarket;

	/**
	 * 原市场代码
	 */
	private String oldFinprodMarketId;


	/**
	 * 质押面值
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal oldPledgeFaceValue;

	/**
	 * 原质押数量
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal oldPledgeNumber;

	/**
	 * 原质押率
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal oldPledgeRatio;

	/**
	 * 原质押金额
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal oldPledgeAmt;

	/**
	 * 置换方向
	 */
	private String displaceDir;

	public String getDisplaceDir() {
		return displaceDir;
	}

	public void setDisplaceDir(String displaceDir) {
		this.displaceDir = displaceDir;
	}

	public String getEntrustId() {
		return entrustId;
	}

	public void setEntrustId(String entrustId) {
		this.entrustId = entrustId;
	}

	public String getFinprodId() {
		return finprodId;
	}

	public void setFinprodId(String finprodId) {
		this.finprodId = finprodId;
	}

	public String getFinprodAbbr() {
		return finprodAbbr;
	}

	public void setFinprodAbbr(String finprodAbbr) {
		this.finprodAbbr = finprodAbbr;
	}

	public String getTradeMarket() {
		return tradeMarket;
	}

	public void setTradeMarket(String tradeMarket) {
		this.tradeMarket = tradeMarket;
	}

	public String getFinprodMarketId() {
		return finprodMarketId;
	}

	public void setFinprodMarketId(String finprodMarketId) {
		this.finprodMarketId = finprodMarketId;
	}

	public BigDecimal getPledgeFaceValue() {
		return pledgeFaceValue;
	}

	public void setPledgeFaceValue(BigDecimal pledgeFaceValue) {
		this.pledgeFaceValue = pledgeFaceValue;
	}

	public BigDecimal getPledgeNumber() {
		return pledgeNumber;
	}

	public void setPledgeNumber(BigDecimal pledgeNumber) {
		this.pledgeNumber = pledgeNumber;
	}

	public BigDecimal getPledgeRatio() {
		return pledgeRatio;
	}

	public void setPledgeRatio(BigDecimal pledgeRatio) {
		this.pledgeRatio = pledgeRatio;
	}

	public BigDecimal getPledgeAmt() {
		return pledgeAmt;
	}

	public void setPledgeAmt(BigDecimal pledgeAmt) {
		this.pledgeAmt = pledgeAmt;
	}

	public String getOldFinprodId() {
		return oldFinprodId;
	}

	public void setOldFinprodId(String oldFinprodId) {
		this.oldFinprodId = oldFinprodId;
	}

	public String getOldFinprodAbbr() {
		return oldFinprodAbbr;
	}

	public void setOldFinprodAbbr(String oldFinprodAbbr) {
		this.oldFinprodAbbr = oldFinprodAbbr;
	}

	public String getOldTradeMarket() {
		return oldTradeMarket;
	}

	public void setOldTradeMarket(String oldTradeMarket) {
		this.oldTradeMarket = oldTradeMarket;
	}

	public String getOldFinprodMarketId() {
		return oldFinprodMarketId;
	}

	public void setOldFinprodMarketId(String oldFinprodMarketId) {
		this.oldFinprodMarketId = oldFinprodMarketId;
	}

	public BigDecimal getOldPledgeFaceValue() {
		return oldPledgeFaceValue;
	}

	public void setOldPledgeFaceValue(BigDecimal oldPledgeFaceValue) {
		this.oldPledgeFaceValue = oldPledgeFaceValue;
	}

	public BigDecimal getOldPledgeNumber() {
		return oldPledgeNumber;
	}

	public void setOldPledgeNumber(BigDecimal oldPledgeNumber) {
		this.oldPledgeNumber = oldPledgeNumber;
	}

	public BigDecimal getOldPledgeRatio() {
		return oldPledgeRatio;
	}

	public void setOldPledgeRatio(BigDecimal oldPledgeRatio) {
		this.oldPledgeRatio = oldPledgeRatio;
	}

	public BigDecimal getOldPledgeAmt() {
		return oldPledgeAmt;
	}

	public void setOldPledgeAmt(BigDecimal oldPledgeAmt) {
		this.oldPledgeAmt = oldPledgeAmt;
	}
}