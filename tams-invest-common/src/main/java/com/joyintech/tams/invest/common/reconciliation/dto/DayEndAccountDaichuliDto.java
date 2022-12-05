package com.joyintech.tams.invest.common.reconciliation.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.common.base.entity.StlSecReconDeal;

import java.math.BigDecimal;
import java.util.Date;

public class DayEndAccountDaichuliDto extends StlSecReconDeal {

	/**
	 * 参数值——对账维度参数
	 */
	private String paraValue;

	/**
	 * 参数值——佣金对账方式参数
	 */
	private String paraValue1;

	/**
	 * 清算成交编号
	 */
	private String tradeId2;

	/**
	 * 清算印花税
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal stampFee2;

	/**
	 * 清算经手费
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal jsfAmt2;

	/**
	 * 清算过户费
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal ghfAmt2;

	/**
	 * 清算证管费
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal zgfAmt2;

	/**
	 * 清算手续费
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal sxfAmt2;

	/**
	 * 清算结算费
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal settleFee2;

	/**
	 * 清算佣金
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal commissionAmt2;

	/**
	 * 清算成交金额
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal tradeAmt2;

	/**
	 * 清算成交数量
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal tradeNum2;

	/**
	 * 清算金融产品类型
	 */
	private String finprodType2;

	/**
	 * 清算交易方向
	 */
	private String ps2;

	/**
	 * 清算交易场所
	 */
	private String tradeMarket2;

	/**
	 * 清算交易日期
	 */
	private Date tradeDate2;

	/**
	 * 清算证券账户
	 */
	private String secAcctId2;

	/**
	 * 对账结果
	 */
	private String checkResult;

	/**
	 * 对账信息
	 */
	private String reconInfo;

	/**
	 * 误差值
	 */
	private BigDecimal wuChaZhi;



	/**
	 * 清算印花税差额
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal stampFeeCe;

	/**
	 * 清算经手费差额
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal jsfAmtCe;

	/**
	 * 清算过户费差额
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal ghfAmtCe;

	/**
	 * 清算证管费差额
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal zgfAmtCe;

	/**
	 * 清算手续费差额
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal sxfAmtCe;

	/**
	 * 清算结算费差额
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal settleFeeCe;

	/**
	 * 清算佣金差额
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal commissionAmtCe;

	/**
	 * 清算成交金额差额
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal tradeAmtCe;

	/**
	 * 清算成交数量差额
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal tradeNumCe;

	/**
	 * 清算金额差额
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal settleAmtCe;

	/**
	 * 佣金是否随交易支付
	 */
	private String isPayWithTrade;


	public BigDecimal getStampFeeCe() {
		return stampFeeCe;
	}

	public void setStampFeeCe(BigDecimal stampFeeCe) {
		this.stampFeeCe = stampFeeCe;
	}

	public BigDecimal getJsfAmtCe() {
		return jsfAmtCe;
	}

	public void setJsfAmtCe(BigDecimal jsfAmtCe) {
		this.jsfAmtCe = jsfAmtCe;
	}

	public BigDecimal getGhfAmtCe() {
		return ghfAmtCe;
	}

	public void setGhfAmtCe(BigDecimal ghfAmtCe) {
		this.ghfAmtCe = ghfAmtCe;
	}

	public BigDecimal getZgfAmtCe() {
		return zgfAmtCe;
	}

	public void setZgfAmtCe(BigDecimal zgfAmtCe) {
		this.zgfAmtCe = zgfAmtCe;
	}

	public BigDecimal getSxfAmtCe() {
		return sxfAmtCe;
	}

	public void setSxfAmtCe(BigDecimal sxfAmtCe) {
		this.sxfAmtCe = sxfAmtCe;
	}

	public BigDecimal getSettleFeeCe() {
		return settleFeeCe;
	}

	public void setSettleFeeCe(BigDecimal settleFeeCe) {
		this.settleFeeCe = settleFeeCe;
	}

	public BigDecimal getCommissionAmtCe() {
		return commissionAmtCe;
	}

	public void setCommissionAmtCe(BigDecimal commissionAmtCe) {
		this.commissionAmtCe = commissionAmtCe;
	}

	public BigDecimal getTradeAmtCe() {
		return tradeAmtCe;
	}

	public void setTradeAmtCe(BigDecimal tradeAmtCe) {
		this.tradeAmtCe = tradeAmtCe;
	}

	public BigDecimal getTradeNumCe() {
		return tradeNumCe;
	}

	public void setTradeNumCe(BigDecimal tradeNumCe) {
		this.tradeNumCe = tradeNumCe;
	}

	public String getParaValue() {
		return paraValue;
	}

	public void setParaValue(String paraValue) {
		this.paraValue = paraValue;
	}

	public String getParaValue1() {
		return paraValue1;
	}

	public void setParaValue1(String paraValue1) {
		this.paraValue1 = paraValue1;
	}

	public String getTradeId2() {
		return tradeId2;
	}

	public void setTradeId2(String tradeId2) {
		this.tradeId2 = tradeId2;
	}

	public BigDecimal getStampFee2() {
		return stampFee2;
	}

	public void setStampFee2(BigDecimal stampFee2) {
		this.stampFee2 = stampFee2;
	}

	public BigDecimal getJsfAmt2() {
		return jsfAmt2;
	}

	public void setJsfAmt2(BigDecimal jsfAmt2) {
		this.jsfAmt2 = jsfAmt2;
	}

	public BigDecimal getGhfAmt2() {
		return ghfAmt2;
	}

	public void setGhfAmt2(BigDecimal ghfAmt2) {
		this.ghfAmt2 = ghfAmt2;
	}

	public BigDecimal getZgfAmt2() {
		return zgfAmt2;
	}

	public void setZgfAmt2(BigDecimal zgfAmt2) {
		this.zgfAmt2 = zgfAmt2;
	}

	public BigDecimal getSxfAmt2() {
		return sxfAmt2;
	}

	public void setSxfAmt2(BigDecimal sxfAmt2) {
		this.sxfAmt2 = sxfAmt2;
	}

	public BigDecimal getSettleFee2() {
		return settleFee2;
	}

	public void setSettleFee2(BigDecimal settleFee2) {
		this.settleFee2 = settleFee2;
	}

	public BigDecimal getCommissionAmt2() {
		return commissionAmt2;
	}

	public void setCommissionAmt2(BigDecimal commissionAmt2) {
		this.commissionAmt2 = commissionAmt2;
	}

	public BigDecimal getTradeAmt2() {
		return tradeAmt2;
	}

	public void setTradeAmt2(BigDecimal tradeAmt2) {
		this.tradeAmt2 = tradeAmt2;
	}

	public BigDecimal getTradeNum2() {
		return tradeNum2;
	}

	public void setTradeNum2(BigDecimal tradeNum2) {
		this.tradeNum2 = tradeNum2;
	}

	public String getFinprodType2() {
		return finprodType2;
	}

	public void setFinprodType2(String finprodType2) {
		this.finprodType2 = finprodType2;
	}

	public String getPs2() {
		return ps2;
	}

	public void setPs2(String ps2) {
		this.ps2 = ps2;
	}

	public String getTradeMarket2() {
		return tradeMarket2;
	}

	public void setTradeMarket2(String tradeMarket2) {
		this.tradeMarket2 = tradeMarket2;
	}

	public Date getTradeDate2() {
		return tradeDate2;
	}

	public void setTradeDate2(Date tradeDate2) {
		this.tradeDate2 = tradeDate2;
	}

	public String getSecAcctId2() {
		return secAcctId2;
	}

	public void setSecAcctId2(String secAcctId2) {
		this.secAcctId2 = secAcctId2;
	}

	public String getCheckResult() {
		return checkResult;
	}

	public void setCheckResult(String checkResult) {
		this.checkResult = checkResult;
	}

	public String getReconInfo() {
		return reconInfo;
	}

	public void setReconInfo(String reconInfo) {
		this.reconInfo = reconInfo;
	}

	public BigDecimal getSettleAmtCe() {
		return settleAmtCe;
	}

	public void setSettleAmtCe(BigDecimal settleAmtCe) {
		this.settleAmtCe = settleAmtCe;
	}

	public BigDecimal getWuChaZhi() {
		return wuChaZhi;
	}

	public void setWuChaZhi(BigDecimal wuChaZhi) {
		this.wuChaZhi = wuChaZhi;
	}

	public String getIsPayWithTrade() {
		return isPayWithTrade;
	}

	public void setIsPayWithTrade(String isPayWithTrade) {
		this.isPayWithTrade = isPayWithTrade;
	}
}
