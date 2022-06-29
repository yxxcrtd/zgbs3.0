/*
 * 文件名：TrdProductDealDto.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2020年07月24日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.settle.batch.datagather.dto;

import com.joyintech.tams.common.base.entity.TrdFinProductDealTotal;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 金融产品汇总交易DTO
 * @author 陈卓
 * @date 2020年07月24日
 * @see TotalTrdProductDealDto
 * @since 1.0
 */
public class TotalTrdProductDealDto extends TrdFinProductDealTotal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 100L;
    
    /**
     * 委托代码
     */
    private String entrustId;
    
    /**
     * 申购方式
     */
    private String purchModel;
    
    /**
     * 申购类型
     */
    private String purchType;
    
    /**
     * 预计申购确认日
     */
	private Date purCfmDate;
    
    /**
     * 预计申购缴款日
     */
	private Date purPayDate;

	/**
	 * 交收方式
	 */
	private String settlemenType;

	/**
	 * 席位号
	 */
	private String seatId;

	/**
	 * 分红类型
	 */
	private String payType;

	/**
	 * 分红到账日
	 */
	private Date bonusCfmDate;
	
	/**
	 * 转股数
	 */
	private BigDecimal conNum;
	
	/**
	 * 零星股数
	 */
	private BigDecimal spoShareNum;
    
    /**
     * 转股价格
     */
    private BigDecimal zgUnitCprice;

    /**
     * 金融产品代码2
     */
    private String finprodId2;

    /**
     * 金融产品类型2
     */
    private String finprodTypeExt;

	/**
	 * 总持仓数量
	 */
	private BigDecimal amount;

	private BigDecimal stampFee;

	private BigDecimal jsfFee;

	private BigDecimal ghfFee;

	private BigDecimal zgfFee;

	private BigDecimal sxfFee;

	private BigDecimal settleFee;

	private BigDecimal commissionAmt;

	public BigDecimal getStampFee() {
		return stampFee;
	}

	public void setStampFee(BigDecimal stampFee) {
		this.stampFee = stampFee;
	}

	public BigDecimal getJsfFee() {
		return jsfFee;
	}

	public void setJsfFee(BigDecimal jsfFee) {
		this.jsfFee = jsfFee;
	}

	public BigDecimal getGhfFee() {
		return ghfFee;
	}

	public void setGhfFee(BigDecimal ghfFee) {
		this.ghfFee = ghfFee;
	}

	public BigDecimal getZgfFee() {
		return zgfFee;
	}

	public void setZgfFee(BigDecimal zgfFee) {
		this.zgfFee = zgfFee;
	}

	public BigDecimal getSxfFee() {
		return sxfFee;
	}

	public void setSxfFee(BigDecimal sxfFee) {
		this.sxfFee = sxfFee;
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

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

    private BigDecimal theoryInt; // 理论应计利息

	private BigDecimal theoryprinAmt; // 理论还本金额

	public String getEntrustId() {
		return entrustId;
	}

	public void setEntrustId(String entrustId) {
		this.entrustId = entrustId;
	}

	public String getPurchModel() {
		return purchModel;
	}

	public void setPurchModel(String purchModel) {
		this.purchModel = purchModel;
	}

	public String getPurchType() {
		return purchType;
	}

	public void setPurchType(String purchType) {
		this.purchType = purchType;
	}

	public Date getPurPayDate() {
		return purPayDate;
	}

	public void setPurPayDate(Date purPayDate) {
		this.purPayDate = purPayDate;
	}

	public Date getPurCfmDate() {
		return purCfmDate;
	}

	public void setPurCfmDate(Date purCfmDate) {
		this.purCfmDate = purCfmDate;
	}

	public String getSettlemenType() {
		return settlemenType;
	}

	public void setSettlemenType(String settlemenType) {
		this.settlemenType = settlemenType;
	}

	public String getSeatId() {
		return seatId;
	}

	public void setSeatId(String seatId) {
		this.seatId = seatId;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public Date getBonusCfmDate() {
		return bonusCfmDate;
	}

	public void setBonusCfmDate(Date bonusCfmDate) {
		this.bonusCfmDate = bonusCfmDate;
	}

	public BigDecimal getConNum() {
		return conNum;
	}

	public void setConNum(BigDecimal conNum) {
		this.conNum = conNum;
	}

	public BigDecimal getSpoShareNum() {
		return spoShareNum;
	}

	public void setSpoShareNum(BigDecimal spoShareNum) {
		this.spoShareNum = spoShareNum;
	}

	public BigDecimal getZgUnitCprice() {
		return zgUnitCprice;
	}

	public void setZgUnitCprice(BigDecimal zgUnitCprice) {
		this.zgUnitCprice = zgUnitCprice;
	}

	public String getFinprodId2() {
		return finprodId2;
	}

	public void setFinprodId2(String finprodId2) {
		this.finprodId2 = finprodId2;
	}

	public String getFinprodTypeExt() {
		return finprodTypeExt;
	}

	public void setFinprodTypeExt(String finprodTypeExt) {
		this.finprodTypeExt = finprodTypeExt;
	}

	public BigDecimal getTheoryInt() {
		return theoryInt;
	}

	public void setTheoryInt(BigDecimal theoryInt) {
		this.theoryInt = theoryInt;
	}

	public BigDecimal getTheoryprinAmt() {
		return theoryprinAmt;
	}

	public void setTheoryprinAmt(BigDecimal theoryprinAmt) {
		this.theoryprinAmt = theoryprinAmt;
	}
}
