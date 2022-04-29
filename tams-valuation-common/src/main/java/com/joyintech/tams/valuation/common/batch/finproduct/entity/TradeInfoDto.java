/*
 * 文件名：TradeInfoDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：估值核算系统V1.0
 * 修改人：niuqianyu
 * 修改时间：2018年6月1日
 * 修改内容：新建
 * 系统名称：估值核算系统V1.0
 */

package com.joyintech.tams.valuation.common.batch.finproduct.entity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 标的查询 - 查询交易信息Dto 
 *
 * @author niuqianyu
 * @version 1.0 2018年6月1日
 * @see TradeInfoDto
 * @since 1.0
 */
public class TradeInfoDto {

//	/**
//	 * 交易编号
//	 */
//	private String tradeId;
//	/**
//	 * 业务类型
//	 */
//	private String busiType;
	/**
	 * 金融产品代码
	 */
	private String finprodId;
	/**
	 * 金融产品类型
	 */
	private String finprodType;
	/**
	 * 分支序号
	 */
	private int branch;
//	/**
//	 * 交易日
//	 */
//	private Date tradeDate;
//	/**
//	 * 起息日-金融产品交易表
//	 */
//	private Date vdateA;
//	/**
//	 * 到期日-金融产品交易表
//	 */
//	private Date mdateA;
//	/**
//	 * 交割日
//	 */
//	private Date settleDate;
//	/**
//	 * 交易币种-金融产品交易表
//	 */
//	private String ccyA;
//	/**
//	 * 单位净价
//	 */
//	private BigDecimal unitCprice;
//	/**
//	 * 单位利息
//	 */
//	private BigDecimal unitInt;
//	/**
//	 * 单位全价
//	 */
//	private BigDecimal unitFprice;
//	/**
//	 * 单位面值
//	 */
//	private BigDecimal parValue;
//	/**
//	 * 净价总额
//	 */
//	private BigDecimal cpriceAmt;
//	/**
//	 * 交易份额
//	 */
//	private BigDecimal shareAmt;
//	/**
//	 * 交易本金
//	 */
//	private BigDecimal prinAmt;
//	/**
//	 * 利息总额
//	 */
//	private BigDecimal intAmt;
//	/**
//	 * 全价总额
//	 */
//	private BigDecimal fpriceAmt;
//	/**
//	 * 交易费用
//	 */
//	private BigDecimal feeAmt;
//	/**
//	 * 交易金额
//	 */
//	private BigDecimal tradeAmt;
//	/**
//	 * 交易方向
//	 */
//	private String ps;
//	/**
//	 * 到期收益率
//	 */
//	private BigDecimal yield;
//	/**
//	 * 行权收益率
//	 */
//	private BigDecimal exerYield;
//	/**
//	 * 投资目的
//	 */
//	private String invAim;
//	/**
//	 * 交易状态
//	 */
//	private String tradeStatus;
//	/**
//	 * 原交易编号
//	 */
//	private String pTradeId;
//	/**
//	 * 是否结清
//	 */
//	private String isClean;

	/**
	 * 分支类型
	 */
	private String branchType;
	/**
	 * 通道代码
	 */
	private String chlAgrtId;
	/**
	 * 名义本金
	 */
	private BigDecimal prin;
	/**
	 * 本金币种-交易类金融产品表
	 */
	private String ccy;
	/**
	 * 起息日-交易类金融产品表
	 */
	private Date vdate;
	/**
	 * 到期日-交易类金融产品表
	 */
	private Date mdate;
	/**
	 * 期限(天)
	 */
	private int term;
	/**
	 * 利率类型
	 */
	private String intType;
	/**
	 * 固定利率
	 */
	private BigDecimal intRate;
	/**
	 * 利率编号
	 */
	private String intRateId;
	/**
	 * 计息基础
	 */
	private String basis;
	/**
	 * 到期本金
	 */
	private BigDecimal mPrinAmt;
	/**
	 * 到期利息
	 */
	private BigDecimal mIntAmt;
	/**
	 * 到期金额
	 */
	private BigDecimal mTradeAmt;
	/**
	 * 原金融产品代码
	 */
	private String oFinprodId;
	/**
	 * 原金融产品类型
	 */
	private String oFinprodType;
	/**
	 * 匹配类型
	 */
	private String matchType;
	/**
	 * 分类1
	 */
	private String type1;
	/**
	 * 分类2
	 */
	private String type2;
	/**
	 * 分类3
	 */
	private String type3;
	/**
	 * 分类4
	 */
	private String type4;
	/**
	 * 分类5
	 */
	private String type5;
	/**
	 * 分类6
	 */
	private String type6;

	/**
	 * 登记托管机构
	 */
	private String registOrg;

	/**
	 * 外汇交易方向
	 */
	private String excPs;

	/**
	 * 期权费
	 */
	private BigDecimal optFeeAmt;


//	/**
//	 *  取得 交易编号
//	 *  @return 交易编号
//	 */
//	public String getTradeId() {
//	   return tradeId;
//	}
//
//	/**
//	 *  设置 交易编号
//	 *  @param tradeId 交易编号
//	 */
//	public void setTradeId( String tradeId)  {
//	   this.tradeId = tradeId;
//	}
//
//	/**
//	 *  取得 业务类型
//	 *  @return 业务类型
//	 */
//	public String getBusiType() {
//	   return busiType;
//	}
//
//	/**
//	 *  设置 业务类型
//	 *  @param busiType 业务类型
//	 */
//	public void setBusiType( String busiType)  {
//	   this.busiType = busiType;
//	}
//
	/**
	 *  取得 金融产品代码
	 *  @return 金融产品代码
	 */
	public String getFinprodId() {
		return finprodId;
	}

	/**
	 *  设置 金融产品代码
	 *  @param finprodId 金融产品代码
	 */
	public void setFinprodId( String finprodId)  {
		this.finprodId = finprodId;
	}

	/**
	 *  取得 金融产品类型
	 *  @return 金融产品类型
	 */
	public String getFinprodType() {
		return finprodType;
	}

	/**
	 *  设置 金融产品类型
	 *  @param finprodType 金融产品类型
	 */
	public void setFinprodType( String finprodType)  {
		this.finprodType = finprodType;
	}

	/**
	 *  取得 分支序号
	 *  @return 分支序号
	 */
	public int getBranch() {
		return branch;
	}

	/**
	 *  设置 分支序号
	 *  @param branch 分支序号
	 */
	public void setBranch( int branch)  {
		this.branch = branch;
	}
//
//	/**
//	 *  取得 交易日
//	 *  @return 交易日
//	 */
//	public Date getTradeDate() {
//	   return tradeDate;
//	}
//
//	/**
//	 *  设置 交易日
//	 *  @param tradeDate 交易日
//	 */
//	public void setTradeDate( Date tradeDate)  {
//	   this.tradeDate = tradeDate;
//	}
//
//	/**
//	 *  取得 起息日
//	 *  @return 起息日
//	 */
//	public Date getVdateA() {
//	   return vdateA;
//	}
//
//	/**
//	 *  设置 起息日
//	 *  @param vdate 起息日
//	 */
//	public void setVdateA( Date vdateA)  {
//	   this.vdateA = vdateA;
//	}
//
//	/**
//	 *  取得 到期日
//	 *  @return 到期日
//	 */
//	public Date getMdateA() {
//	   return mdateA;
//	}
//
//	/**
//	 *  设置 到期日
//	 *  @param mdate 到期日
//	 */
//	public void setMdateA( Date mdateA)  {
//	   this.mdateA = mdateA;
//	}
//
//	/**
//	 *  取得 交割日
//	 *  @return 交割日
//	 */
//	public Date getSettleDate() {
//	   return settleDate;
//	}
//
//	/**
//	 *  设置 交割日
//	 *  @param settleDate 交割日
//	 */
//	public void setSettleDate( Date settleDate)  {
//	   this.settleDate = settleDate;
//	}
//
//	/**
//	 *  取得 交易币种
//	 *  @return 交易币种
//	 */
//	public String getCcyA() {
//	   return ccyA;
//	}
//
//	/**
//	 *  设置 交易币种
//	 *  @param ccy 交易币种
//	 */
//	public void setCcyA( String ccyA)  {
//	   this.ccyA = ccyA;
//	}
//
//	/**
//	 *  取得 单位净价
//	 *  @return 单位净价
//	 */
//	public BigDecimal getUnitCprice() {
//	   return unitCprice;
//	}
//
//	/**
//	 *  设置 单位净价
//	 *  @param unitCprice 单位净价
//	 */
//	public void setUnitCprice( BigDecimal unitCprice)  {
//	   this.unitCprice = unitCprice;
//	}
//
//	/**
//	 *  取得 单位利息
//	 *  @return 单位利息
//	 */
//	public BigDecimal getUnitInt() {
//	   return unitInt;
//	}
//
//	/**
//	 *  设置 单位利息
//	 *  @param unitInt 单位利息
//	 */
//	public void setUnitInt( BigDecimal unitInt)  {
//	   this.unitInt = unitInt;
//	}
//
//	/**
//	 *  取得 单位全价
//	 *  @return 单位全价
//	 */
//	public BigDecimal getUnitFprice() {
//	   return unitFprice;
//	}
//
//	/**
//	 *  设置 单位全价
//	 *  @param unitFprice 单位全价
//	 */
//	public void setUnitFprice( BigDecimal unitFprice)  {
//	   this.unitFprice = unitFprice;
//	}
//
//	/**
//	 *  取得 单位面值
//	 *  @return 单位面值
//	 */
//	public BigDecimal getParValue() {
//	   return parValue;
//	}
//
//	/**
//	 *  设置 单位面值
//	 *  @param parValue 单位面值
//	 */
//	public void setParValue( BigDecimal parValue)  {
//	   this.parValue = parValue;
//	}
//
//	/**
//	 *  取得 净价总额
//	 *  @return 净价总额
//	 */
//	public BigDecimal getCpriceAmt() {
//	   return cpriceAmt;
//	}
//
//	/**
//	 *  设置 净价总额
//	 *  @param cpriceAmt 净价总额
//	 */
//	public void setCpriceAmt( BigDecimal cpriceAmt)  {
//	   this.cpriceAmt = cpriceAmt;
//	}
//
//	/**
//	 *  取得 交易份额
//	 *  @return 交易份额
//	 */
//	public BigDecimal getShareAmt() {
//	   return shareAmt;
//	}
//
//	/**
//	 *  设置 交易份额
//	 *  @param shareAmt 交易份额
//	 */
//	public void setShareAmt( BigDecimal shareAmt)  {
//	   this.shareAmt = shareAmt;
//	}
//
//	/**
//	 *  取得 交易本金
//	 *  @return 交易本金
//	 */
//	public BigDecimal getPrinAmt() {
//	   return prinAmt;
//	}
//
//	/**
//	 *  设置 交易本金
//	 *  @param prinAmt 交易本金
//	 */
//	public void setPrinAmt( BigDecimal prinAmt)  {
//	   this.prinAmt = prinAmt;
//	}
//
//	/**
//	 *  取得 利息总额
//	 *  @return 利息总额
//	 */
//	public BigDecimal getIntAmt() {
//	   return intAmt;
//	}
//
//	/**
//	 *  设置 利息总额
//	 *  @param intAmt 利息总额
//	 */
//	public void setIntAmt( BigDecimal intAmt)  {
//	   this.intAmt = intAmt;
//	}
//
//	/**
//	 *  取得 全价总额
//	 *  @return 全价总额
//	 */
//	public BigDecimal getFpriceAmt() {
//	   return fpriceAmt;
//	}
//
//	/**
//	 *  设置 全价总额
//	 *  @param fpriceAmt 全价总额
//	 */
//	public void setFpriceAmt( BigDecimal fpriceAmt)  {
//	   this.fpriceAmt = fpriceAmt;
//	}
//
//	/**
//	 *  取得 交易费用
//	 *  @return 交易费用
//	 */
//	public BigDecimal getFeeAmt() {
//	   return feeAmt;
//	}
//
//	/**
//	 *  设置 交易费用
//	 *  @param feeAmt 交易费用
//	 */
//	public void setFeeAmt( BigDecimal feeAmt)  {
//	   this.feeAmt = feeAmt;
//	}
//
//	/**
//	 *  取得 交易金额
//	 *  @return 交易金额
//	 */
//	public BigDecimal getTradeAmt() {
//	   return tradeAmt;
//	}
//
//	/**
//	 *  设置 交易金额
//	 *  @param tradeAmt 交易金额
//	 */
//	public void setTradeAmt( BigDecimal tradeAmt)  {
//	   this.tradeAmt = tradeAmt;
//	}
//
//	/**
//	 *  取得 交易方向
//	 *  @return 交易方向
//	 */
//	public String getPs() {
//	   return ps;
//	}
//
//	/**
//	 *  设置 交易方向
//	 *  @param ps 交易方向
//	 */
//	public void setPs( String ps)  {
//	   this.ps = ps;
//	}
//
//	/**
//	 *  取得 到期收益率
//	 *  @return 到期收益率
//	 */
//	public BigDecimal getYield() {
//	   return yield;
//	}
//
//	/**
//	 *  设置 到期收益率
//	 *  @param yield 到期收益率
//	 */
//	public void setYield( BigDecimal yield)  {
//	   this.yield = yield;
//	}
//
//	/**
//	 *  取得 行权收益率
//	 *  @return 行权收益率
//	 */
//	public BigDecimal getExerYield() {
//	   return exerYield;
//	}
//
//	/**
//	 *  设置 行权收益率
//	 *  @param exerYield 行权收益率
//	 */
//	public void setExerYield( BigDecimal exerYield)  {
//	   this.exerYield = exerYield;
//	}
//
//	/**
//	 *  取得 投资目的
//	 *  @return 投资目的
//	 */
//	public String getInvAim() {
//	   return invAim;
//	}
//
//	/**
//	 *  设置 投资目的
//	 *  @param invAim 投资目的
//	 */
//	public void setInvAim( String invAim)  {
//	   this.invAim = invAim;
//	}
//
//	/**
//	 *  取得 交易状态
//	 *  @return 交易状态
//	 */
//	public String getTradeStatus() {
//	   return tradeStatus;
//	}
//
//	/**
//	 *  设置 交易状态
//	 *  @param tradeStatus 交易状态
//	 */
//	public void setTradeStatus( String tradeStatus)  {
//	   this.tradeStatus = tradeStatus;
//	}
//
//	/**
//	 *  取得 原交易编号
//	 *  @return 原交易编号
//	 */
//	public String getPTradeId() {
//	   return pTradeId;
//	}
//
//	/**
//	 *  设置 原交易编号
//	 *  @param pTradeId 原交易编号
//	 */
//	public void setPTradeId( String pTradeId)  {
//	   this.pTradeId = pTradeId;
//	}
//
//	/**
//	 *  取得 是否结清
//	 *  @return 是否结清
//	 */
//	public String getIsClean() {
//	   return isClean;
//	}
//
//	/**
//	 *  设置 是否结清
//	 *  @param isClean 是否结清
//	 */
//	public void setIsClean( String isClean)  {
//	   this.isClean = isClean;
//	}

	/**
	 *  取得 分支类型
	 *  @return 分支类型
	 */
	public String getBranchType() {
		return branchType;
	}

	/**
	 *  设置 分支类型
	 *  @param branchType 分支类型
	 */
	public void setBranchType( String branchType)  {
		this.branchType = branchType;
	}

	/**
	 *  取得 通道代码
	 *  @return 通道代码
	 */
	public String getChlAgrtId() {
		return chlAgrtId;
	}

	/**
	 *  设置 通道代码
	 *  @param chlAgrtId 通道代码
	 */
	public void setChlAgrtId( String chlAgrtId)  {
		this.chlAgrtId = chlAgrtId;
	}

	/**
	 *  取得 名义本金
	 *  @return 名义本金
	 */
	public BigDecimal getPrin() {
		return prin;
	}

	/**
	 *  设置 名义本金
	 *  @param prin 名义本金
	 */
	public void setPrin( BigDecimal prin)  {
		this.prin = prin;
	}

	/**
	 *  取得 本金币种
	 *  @return 本金币种
	 */
	public String getCcy() {
		return ccy;
	}

	/**
	 *  设置 本金币种
	 *  @param ccy 本金币种
	 */
	public void setCcy( String ccy)  {
		this.ccy = ccy;
	}

	/**
	 *  取得 起息日
	 *  @return 起息日
	 */
	public Date getVdate() {
		return vdate;
	}

	/**
	 *  设置 起息日
	 *  @param vdate 起息日
	 */
	public void setVdate( Date vdate)  {
		this.vdate = vdate;
	}

	/**
	 *  取得 到期日
	 *  @return 到期日
	 */
	public Date getMdate() {
		return mdate;
	}

	/**
	 *  设置 到期日
	 *  @param mdate 到期日
	 */
	public void setMdate( Date mdate)  {
		this.mdate = mdate;
	}

	/**
	 *  取得 期限(天)
	 *  @return 期限(天)
	 */
	public int getTerm() {
		return term;
	}

	/**
	 *  设置 期限(天)
	 *  @param term 期限(天)
	 */
	public void setTerm( int term)  {
		this.term = term;
	}

	/**
	 *  取得 利率类型
	 *  @return 利率类型
	 */
	public String getIntType() {
		return intType;
	}

	/**
	 *  设置 利率类型
	 *  @param intType 利率类型
	 */
	public void setIntType( String intType)  {
		this.intType = intType;
	}

	/**
	 *  取得 固定利率
	 *  @return 固定利率
	 */
	public BigDecimal getIntRate() {
		return intRate;
	}

	/**
	 *  设置 固定利率
	 *  @param intRate 固定利率
	 */
	public void setIntRate( BigDecimal intRate)  {
		this.intRate = intRate;
	}

	/**
	 *  取得 利率编号
	 *  @return 利率编号
	 */
	public String getIntRateId() {
		return intRateId;
	}

	/**
	 *  设置 利率编号
	 *  @param intRateId 利率编号
	 */
	public void setIntRateId( String intRateId)  {
		this.intRateId = intRateId;
	}

	/**
	 *  取得 计息基础
	 *  @return 计息基础
	 */
	public String getBasis() {
		return basis;
	}

	/**
	 *  设置 计息基础
	 *  @param basis 计息基础
	 */
	public void setBasis( String basis)  {
		this.basis = basis;
	}

	/**
	 *  取得 到期本金
	 *  @return 到期本金
	 */
	public BigDecimal getMPrinAmt() {
		return mPrinAmt;
	}

	/**
	 *  设置 到期本金
	 *  @param mPrinAmt 到期本金
	 */
	public void setMPrinAmt( BigDecimal mPrinAmt)  {
		this.mPrinAmt = mPrinAmt;
	}

	/**
	 *  取得 到期利息
	 *  @return 到期利息
	 */
	public BigDecimal getMIntAmt() {
		return mIntAmt;
	}

	/**
	 *  设置 到期利息
	 *  @param mIntAmt 到期利息
	 */
	public void setMIntAmt( BigDecimal mIntAmt)  {
		this.mIntAmt = mIntAmt;
	}

	/**
	 *  取得 到期金额
	 *  @return 到期金额
	 */
	public BigDecimal getMTradeAmt() {
		return mTradeAmt;
	}

	/**
	 *  设置 到期金额
	 *  @param mTradeAmt 到期金额
	 */
	public void setMTradeAmt( BigDecimal mTradeAmt)  {
		this.mTradeAmt = mTradeAmt;
	}

	/**
	 *  取得 原金融产品代码
	 *  @return 原金融产品代码
	 */
	public String getOFinprodId() {
		return oFinprodId;
	}

	/**
	 *  设置 原金融产品代码
	 *  @param oFinprodId 原金融产品代码
	 */
	public void setOFinprodId( String oFinprodId)  {
		this.oFinprodId = oFinprodId;
	}

	/**
	 *  取得 原金融产品类型
	 *  @return 原金融产品类型
	 */
	public String getOFinprodType() {
		return oFinprodType;
	}

	/**
	 *  设置 原金融产品类型
	 *  @param oFinprodType 原金融产品类型
	 */
	public void setOFinprodType( String oFinprodType)  {
		this.oFinprodType = oFinprodType;
	}

	/**
	 *  取得 匹配类型
	 *  @return 匹配类型
	 */
	public String getMatchType() {
		return matchType;
	}

	/**
	 *  设置 匹配类型
	 *  @param matchType 匹配类型
	 */
	public void setMatchType( String matchType)  {
		this.matchType = matchType;
	}


	/**
	 * 获取 type1
	 * @return type1.
	 */
	public String getType1() {
		return type1;
	}


	/**
	 * 设置 type1
	 * @param type1 type1
	 */
	public void setType1(String type1) {
		this.type1 = type1;
	}


	/**
	 * 获取 type2
	 * @return type2.
	 */
	public String getType2() {
		return type2;
	}


	/**
	 * 设置 type2
	 * @param type2 type2
	 */
	public void setType2(String type2) {
		this.type2 = type2;
	}


	/**
	 * 获取 type3
	 * @return type3.
	 */
	public String getType3() {
		return type3;
	}


	/**
	 * 设置 type3
	 * @param type3 type3
	 */
	public void setType3(String type3) {
		this.type3 = type3;
	}


	/**
	 * 获取 type4
	 * @return type4.
	 */
	public String getType4() {
		return type4;
	}


	/**
	 * 设置 type4
	 * @param type4 type4
	 */
	public void setType4(String type4) {
		this.type4 = type4;
	}


	/**
	 * 获取 type5
	 * @return type5.
	 */
	public String getType5() {
		return type5;
	}


	/**
	 * 设置 type5
	 * @param type5 type5
	 */
	public void setType5(String type5) {
		this.type5 = type5;
	}


	/**
	 * 获取 type6
	 * @return type6.
	 */
	public String getType6() {
		return type6;
	}


	/**
	 * 设置 type6
	 * @param type6 type6
	 */
	public void setType6(String type6) {
		this.type6 = type6;
	}

	/**
	 * 取得 登记托管机构
	 * @return 登记托管机构
	 */
	public String getRegistOrg() {
		return registOrg;
	}

	/**
	 * 设置 登记托管机构
	 * @param registOrg 登记托管机构
	 */
	public void setRegistOrg(String registOrg) {
		this.registOrg = registOrg == null ? null : registOrg.trim();
	}



	/**
	 * 交易所回购计提天数方式
	 * */
	private String seRepDayType;
	/**
	 * 首期交付日
	 * */

	private Date vpayDate;
	/**
	 * 到期交付日
	 * */
	private Date mpayDate;
	/**
	 * 实际资金占用天数
	 * */
	private int actCapDays;
	/**
	 * 交易所
	 * */
	private String tradeMarket;

	/**
	 * 交易所回购计提天数方式
	 * */
	public String getSeRepDayType() {
		return seRepDayType;
	}
	/**
	 * 交易所回购计提天数方式
	 * */
	public void setSeRepDayType(String seRepDayType) {
		this.seRepDayType = seRepDayType;
	}
	/**
	 * 首期交付日
	 * */
	public Date getVpayDate() {
		return vpayDate;
	}
	/**
	 * 首期交付日
	 * */
	public void setVpayDate(Date vpayDate) {
		this.vpayDate = vpayDate;
	}
	/**
	 * 到期交付日
	 * */
	public Date getMpayDate() {
		return mpayDate;
	}
	/**
	 * 到期交付日
	 * */
	public void setMpayDate(Date mpayDate) {
		this.mpayDate = mpayDate;
	}
	/**
	 * 实际资金占用天数
	 * */
	public int getActCapDays() {
		return actCapDays;
	}
	/**
	 * 实际资金占用天数
	 * */
	public void setActCapDays(int actCapDays) {
		this.actCapDays = actCapDays;
	}
	/**
	 * 交易所
	 * */
	public String getTradeMarket() {
		return tradeMarket;
	}
	/**
	 * 交易所
	 * */
	public void setTradeMarket(String tradeMarket) {
		this.tradeMarket = tradeMarket;
	}

	/**
	 * 获取 excPs
	 * @return excPs.
	 */
	public String getExcPs() {
		return excPs;
	}

	/**
	 * 设置 excPs
	 * @param excPs excPs
	 */
	public void setExcPs(String excPs) {
		this.excPs = excPs;
	}

	/**
	 * 获取 期权费
	 * @return optFeeAmt.
	 */
	public BigDecimal getOptFeeAmt() {
		return optFeeAmt;
	}

	/**
	 * 设置 期权费
	 * @param optFeeAmt
	 */
	public void setOptFeeAmt(BigDecimal optFeeAmt) {
		this.optFeeAmt = optFeeAmt;
	}
}