package com.joyintech.tams.invest.basicdata.dayendaccount.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.common.base.entity.StlSecRecon;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt1;
import com.joyintech.tams.constant.DictCodeListExt3;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;

import java.math.BigDecimal;

public class DayEndAccountDto extends StlSecRecon {

	/**
	 * 金融产品类型
	 */
	private String finprodType;

	/**
	 * 金融产品类型
	 */
	private String finprodType2;

	/**
	 * 流水号
	 */
	private String tradeId;

	/**
	 * 成交编号
	 */
	private String cjbh;

	public String getCjbh() {
		return cjbh;
	}

	public void setCjbh(String cjbh) {
		this.cjbh = cjbh;
	}

	public String getFinprodMarketId() {
		return finprodMarketId;
	}

	public void setFinprodMarketId(String finprodMarketId) {
		this.finprodMarketId = finprodMarketId;
	}

	/**
	 * 金融产品市场代码
	 */
	private String finprodMarketId;

	/**
	 * 交易方向
	 */
	private String ps;

	public String getPs() {
		return ps;
	}

	public void setPs(String ps) {
		this.ps = ps;
	}

	/**
	 * 获取 getPsStr
	 * @return getPsStr.
	 */
	public String getPsStr() {
		// ZGZGST-4194:成交明细表回购方向与指令方向不一致,需特殊处理 fuyongsheng 20211213 edit start
		// return SysDataUtil.getNameFromDataDict(DictCode.交易方向, this.getPs());
		if(DictCodeListExt1.金融产品类型_投管用_债券质押式正回购.getValue().equals(this.getFinprodType2())){
			return SysDataUtil.getNameFromDataDict(DictCode.交易方向, DictCodeListExt3.交易方向_正回购.getValue());
		}else if(DictCodeListExt1.金融产品类型_投管用_债券质押式逆回购.getValue().equals(this.getFinprodType2())){
			return SysDataUtil.getNameFromDataDict(DictCode.交易方向, DictCodeListExt3.交易方向_逆回购.getValue());
		}else{
			return SysDataUtil.getNameFromDataDict(DictCode.交易方向, this.getPs());
		}
		// ZGZGST-4194:成交明细表回购方向与指令方向不一致,需特殊处理 fuyongsheng 20211213 edit end
	}

	/**
	 * 印花税
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal stampFee;

	/**
	 * 经手费
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal jsfAmt;

	/**
	 * 过户费
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal ghfAmt;

	/**
	 * 证管费
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal zgfAmt;

	/**
	 * 手续费
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal sxfAmt;

	/**
	 * 结算费
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal settleFee;

	/**
	 * 佣金
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal commissionAmt;

	/**
	 * 净佣金
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal netCommissionAmt;

	/**
	 * 成交金额
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal tradeAmt;

	/**
	 * 成交数量
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal tradeNum;

	/**
	 * 印花税
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal stampFee2;

	/**
	 * 经手费
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal jsfAmt2;

	/**
	 * 过户费
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal ghfAmt2;

	/**
	 * 证管费
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal zgfAmt2;

	/**
	 * 手续费
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal sxfAmt2;

	/**
	 * 结算费
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal settleFee2;

	/**
	 * 佣金
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal commissionAmt2;

	/**
	 * 成交金额
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal tradeAmt2;

	/**
	 * 成交数量
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal tradeNum2;

	/**
	 * 印花税
	 */
	@JsonSerialize(using= BigDecimalSerializer.class)
	private BigDecimal stampFeeFlg;

	/**
	 * 经手费
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal jsfAmtFlg;

	/**
	 * 过户费
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal ghfAmtFlg;

	/**
	 * 证管费
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal zgfAmtFlg;

	/**
	 * 手续费
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal sxfAmtFlg;

	/**
	 * 结算费
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal settleFeeFlg;

	/**
	 * 佣金
	 */
	@JsonSerialize(using=BigDecimalSerializer.class)
	private BigDecimal commissionAmtFlg;

	/**
	 * 席位号
	 */
	private String seatNumber;

	/**
	 * 佣金类型（全佣金，净佣金）
	 */
	private String commissionType;

	public String getCommissionType() {
		return commissionType;
	}

	public void setCommissionType(String commissionType) {
		this.commissionType = commissionType;
	}

	public String getSeatNumber() {
		return seatNumber;
	}

	public void setSeatNumber(String seatNumber) {
		this.seatNumber = seatNumber;
	}

	public BigDecimal getStampFeeFlg() {
		return stampFeeFlg;
	}

	public void setStampFeeFlg(BigDecimal stampFeeFlg) {
		this.stampFeeFlg = stampFeeFlg;
	}

	public BigDecimal getJsfAmtFlg() {
		return jsfAmtFlg;
	}

	public void setJsfAmtFlg(BigDecimal jsfAmtFlg) {
		this.jsfAmtFlg = jsfAmtFlg;
	}

	public BigDecimal getGhfAmtFlg() {
		return ghfAmtFlg;
	}

	public void setGhfAmtFlg(BigDecimal ghfAmtFlg) {
		this.ghfAmtFlg = ghfAmtFlg;
	}

	public BigDecimal getZgfAmtFlg() {
		return zgfAmtFlg;
	}

	public void setZgfAmtFlg(BigDecimal zgfAmtFlg) {
		this.zgfAmtFlg = zgfAmtFlg;
	}

	public BigDecimal getSxfAmtFlg() {
		return sxfAmtFlg;
	}

	public void setSxfAmtFlg(BigDecimal sxfAmtFlg) {
		this.sxfAmtFlg = sxfAmtFlg;
	}

	public BigDecimal getSettleFeeFlg() {
		return settleFeeFlg;
	}

	public void setSettleFeeFlg(BigDecimal settleFeeFlg) {
		this.settleFeeFlg = settleFeeFlg;
	}

	public BigDecimal getCommissionAmtFlg() {
		return commissionAmtFlg;
	}

	public void setCommissionAmtFlg(BigDecimal commissionAmtFlg) {
		this.commissionAmtFlg = commissionAmtFlg;
	}

	public String getTradeMarketStr() {
		return SysDataUtil.getNameFromDataDict(BussinessDictCode.交易场所, super.getTradeMarket());
	}

	public String getAccountStatusStr() {
		return SysDataUtil.getNameFromDataDict(DictCode.对账状态结果, super.getCheckResult());
	}

	public String getFinprodTypeStr() {
		return SysDataUtil.getNameFromDataDict(BussinessDictCode.金融产品类型_投管用, this.getFinprodType());
	}

	public BigDecimal getStampFee() {
		return stampFee;
	}

	public void setStampFee(BigDecimal stampFee) {
		this.stampFee = stampFee;
	}

	public BigDecimal getJsfAmt() {
		return jsfAmt;
	}

	public void setJsfAmt(BigDecimal jsfAmt) {
		this.jsfAmt = jsfAmt;
	}

	public BigDecimal getGhfAmt() {
		return ghfAmt;
	}

	public void setGhfAmt(BigDecimal ghfAmt) {
		this.ghfAmt = ghfAmt;
	}

	public BigDecimal getZgfAmt() {
		return zgfAmt;
	}

	public void setZgfAmt(BigDecimal zgfAmt) {
		this.zgfAmt = zgfAmt;
	}

	public BigDecimal getSxfAmt() {
		return sxfAmt;
	}

	public void setSxfAmt(BigDecimal sxfAmt) {
		this.sxfAmt = sxfAmt;
	}

	public BigDecimal getSettleFee() {
		return settleFee;
	}

	public void setSettleFee(BigDecimal settleFee) {
		this.settleFee = settleFee;
	}

	public BigDecimal getCommissionAmt() {
		return commissionAmt;
	}

	public void setCommissionAmt(BigDecimal commissionAmt) {
		this.commissionAmt = commissionAmt;
	}

	public BigDecimal getNetCommissionAmt() {
		return netCommissionAmt;
	}

	public void setNetCommissionAmt(BigDecimal netCommissionAmt) {
		this.netCommissionAmt = netCommissionAmt;
	}

	public BigDecimal getTradeAmt() {
		return tradeAmt;
	}

	public void setTradeAmt(BigDecimal tradeAmt) {
		this.tradeAmt = tradeAmt;
	}

	public BigDecimal getTradeNum() {
		return tradeNum;
	}

	public void setTradeNum(BigDecimal tradeNum) {
		this.tradeNum = tradeNum;
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

	public String getFinprodType() {
		return finprodType;
	}

	public void setFinprodType(String finprodType) {
		this.finprodType = finprodType;
	}


	public String getFinprodType2() {
		return finprodType2;
	}

	public void setFinprodType2(String finprodType2) {
		this.finprodType2 = finprodType2;
	}

	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public String getFinprodTypeStr1() {
		return SysDataUtil.getNameFromDataDict(BussinessDictCode.平安资产类型, this.getFinprodType());
	}

	/**
	 * 佣金是否随交易支付
	 */
	private String isPayWithTrade;

	public String getIsPayWithTrade() {
		return isPayWithTrade;
	}

	public void setIsPayWithTrade(String isPayWithTrade) {
		this.isPayWithTrade = isPayWithTrade;
	}
}
