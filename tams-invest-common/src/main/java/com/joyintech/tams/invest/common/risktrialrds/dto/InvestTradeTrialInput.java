package com.joyintech.tams.invest.common.risktrialrds.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 监督指标输入条件DTO
 * @author 裴家俊
 * @version 1.0 2018年11月28日
 * @see InvestTradeTrialInput
 * @since 1.0
 */
public class InvestTradeTrialInput {
	/**
     * 日期/必填
     */
	private String valDate; 
	/**
     * 组合代码/必填
     */
	private String portfolioCode;
	/**
     * 资产代码/非必填
     */
	private String assetCode;
	/**
	 * 组合代码/必填
	 */
	private List<String> portfolioCodeList;

	/**
	 * 指标代码
	 */
	private List<String> indCodeList;

	 /**
     * 交易金额/交易份额/此金额实时影响指标表达式的计算/必填
     */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal tradeAmount;
	/**
     * 监控时点/必填
     */
	private String timePoint;
	/**
     * 资产类型/必填
     */
	private String assetType;

	/**
     * 交易方向
     */
	private String tradeDirection;

	/**
     * 是否启用，默认启用，输入参数可以不用传
     */
	private String isEnabled;
	
	private String tradeCode;

	private Date nextDate;
	/**
	 * 监控频率
	 */
	private String supFrequency;
	public String getValDate() {
		return valDate;
	}
	public void setValDate(String valDate) {
		this.valDate = valDate;
	}
	public String getPortfolioCode() {
		return portfolioCode;
	}
	public void setPortfolioCode(String portfolioCode) {
		this.portfolioCode = portfolioCode;
	}
	public List<String> getPortfolioCodeList() {
		return portfolioCodeList;
	}
	public void setPortfolioCodeList(List<String> portfolioCodeList) {
		this.portfolioCodeList = portfolioCodeList;
	}
	public List<String> getIndCodeList() {
		return indCodeList;
	}
	public void setIndCodeList(List<String> indCodeList) {
		this.indCodeList = indCodeList;
	}
	public String getAssetCode() {
		return assetCode;
	}
	public void setAssetCode(String assetCode) {
		this.assetCode = assetCode;
	}
	public BigDecimal getTradeAmount() {
		return tradeAmount;
	}
	public void setTradeAmount(BigDecimal tradeAmount) {
		this.tradeAmount = tradeAmount;
	}
	public String getTimePoint() {
		return timePoint;
	}
	public void setTimePoint(String timePoint) {
		this.timePoint = timePoint;
	}
	public String getAssetType() {
		return assetType;
	}
	public void setAssetType(String assetType) {
		this.assetType = assetType;
	}
	public String getTradeDirection() {
		return tradeDirection;
	}
	public void setTradeDirection(String tradeDirection) {
		this.tradeDirection = tradeDirection;
	}
	public String getIsEnabled() {
		return isEnabled;
	}
	public void setIsEnabled(String isEnabled) {
		this.isEnabled = isEnabled;
	}
	public String getTradeCode() {
		return tradeCode;
	}
	public void setTradeCode(String tradeCode) {
		this.tradeCode = tradeCode;
	}

	public String getSupFrequency() {
		return supFrequency;
	}

	public void setSupFrequency(String supFrequency) {
		this.supFrequency = supFrequency;
	}

	public Date getNextDate() {
		return nextDate;
	}

	public void setNextDate(Date nextDate) {
		this.nextDate = nextDate;
	}
}
