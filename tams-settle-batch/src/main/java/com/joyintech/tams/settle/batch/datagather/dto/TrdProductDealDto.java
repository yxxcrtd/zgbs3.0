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

import java.math.BigDecimal;
import java.util.Date;

import com.joyintech.tams.common.base.entity.TrdProductDeal;

/**
 * 金融产品交易明细DTO
 * @author 王波
 * @date 2020年07月24日
 * @see TrdProductDealDto
 * @since 1.0
 */
public class TrdProductDealDto extends TrdProductDeal {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    /**
     * 结算方式
     */
	private String settMode;

	/**
     * 投资组合代码
     */
    private String portfolioId;
    
    /**
     * 委托代码
     */
    private String entrustId;

    /**
     * 成交编号
     */
    private String dealId;

    /**
     * 权益登记日
     */
    private Date regDate;

    /**
     * 权益登记日份额
     */
    private BigDecimal regDateAmt;

    /**
     * 金融产品代码2
     */
    private String finprodId2;

    /**
     * 金融产品类型2
     */
    private String finprodTypeExt;

    /**
     * 分支序号2
     */
    private Long branch2;

    /**
     * 单位净价2
     */
    private BigDecimal unitCprice2;

    /**
     * 交易本金2
     */
    private BigDecimal prinAmt2;

    /**
     * 交易份额2
     */
    private BigDecimal shareAmt2;

    /**
     * 利息总额2
     */
    private BigDecimal intAmt2;

    /**
     * 投资目的2
     */
    private String invAim2;

    /**
     * 印花税
     */
    private BigDecimal stampFee;

    /**
     * 经手费
     */
    private BigDecimal jsfAmt;

    /**
     * 过户费
     */
    private BigDecimal ghfAmt;

    /**
     * 证管费
     */
    private BigDecimal zgfAmt;

    /**
     * 手续费
     */
    private BigDecimal sxfAmt;

    /**
     * 结算费
     */
    private BigDecimal settleFee;

    /**
     * 佣金
     */
    private BigDecimal commissionAmt;
    /**
     * 是否随交易支付
     */
    private String isPayWithTrade;

    /**
     * 资金账户
     */
    private String acctNo;

    /**
     * 利率
     */
    private BigDecimal rate;

    /**
     * 期限(天)
     */
    private Long repoTerm;

    /**
     * 期限品种
     */
    private String termType;

    /**
     * 是否优先配售
     */
    private String isPurchTypeYx;
    
    /**
     * 转股数
     */
    private BigDecimal conNum;
    
    /**
     * 零股数量
     */
    private BigDecimal spoShareNum;
    
    /**
     * 转股价格
     */
    private BigDecimal zgUnitCprice;
    
    /**
     * 到期利息
     */
    private BigDecimal mIntAmt;

    /**
     * 到期金额
     */
    private BigDecimal mTradeAmt;

    //预计分红日
    private Date bonusCfmDate;

    /**
     * 本金现金流代码
     */
    private String prinCashId;

    public String getPrinCashId() {
        return prinCashId;
    }

    public void setPrinCashId(String prinCashId) {
        this.prinCashId = prinCashId;
    }

    /**
     * 付息现金流代码
     */
    private String intPayCashId;

    public String getIntPayCashId() {
        return intPayCashId;
    }

    public void setIntPayCashId(String intPayCashId) {
        this.intPayCashId = intPayCashId;
    }

    public Date getBonusCfmDate() {
        return bonusCfmDate;
    }

    public void setBonusCfmDate(Date bonusCfmDate) {
        this.bonusCfmDate = bonusCfmDate;
    }

    /**
     * 取得 投资目的2
     * @return 投资目的2
     */
    public String getInvAim2() {
        return invAim2;
    }

    /**
     * 设置 投资目的2
     * @param invAim2 投资目的2
     */
    public void setInvAim2(String invAim2) {
        this.invAim2 = invAim2;
    }

    /**
     * 取得 利息总额2
     * @return 利息总额2
     */
    public BigDecimal getIntAmt2() {
        return intAmt2;
    }

    /**
     * 设置 利息总额2
     * @param intAmt2 利息总额2
     */
    public void setIntAmt2(BigDecimal intAmt2) {
        this.intAmt2 = intAmt2;
    }

    /**
     * 取得 交易本金2
     * @return 交易本金2
     */
    public BigDecimal getPrinAmt2() {
        return prinAmt2;
    }

    /**
     * 设置 交易本金2
     * @param prinAmt2 交易本金2
     */
    public void setPrinAmt2(BigDecimal prinAmt2) {
        this.prinAmt2 = prinAmt2;
    }

    /**
     * 取得 交易份额2
     * @return 交易份额2
     */
    public BigDecimal getShareAmt2() {
        return shareAmt2;
    }

    /**
     * 设置 交易份额2
     * @param shareAmt2 交易份额2
     */
    public void setShareAmt2(BigDecimal shareAmt2) {
        this.shareAmt2 = shareAmt2;
    }

    /**
     * 取得 单位净价2
     * @return 单位净价2
     */
    public BigDecimal getUnitCprice2() {
        return unitCprice2;
    }

    /**
     * 设置 单位净价2
     * @param unitCprice2 单位净价2
     */
    public void setUnitCprice2(BigDecimal unitCprice2) {
        this.unitCprice2 = unitCprice2;
    }

    /**
     * 取得 分支序号2
     * @return 分支序号2
     */
    public Long getBranch2() {
        return branch2;
    }

    /**
     * 设置 分支序号2
     * @param branch2 分支序号2
     */
    public void setBranch2(Long branch2) {
        this.branch2 = branch2;
    }

    /**
     * 取得 金融产品类型2
     * @return 金融产品类型2
     */
    public String getFinprodTypeExt() {
        return finprodTypeExt;
    }

    /**
     * 设置 金融产品类型2
     * @param finprodTypeExt 金融产品类型2
     */
    public void setFinprodTypeExt(String finprodTypeExt) {
        this.finprodTypeExt = finprodTypeExt;
    }

    /**
     * 取得 金融产品代码2
     * @return 金融产品代码2
     */
    public String getFinprodId2() {
        return finprodId2;
    }

    /**
     * 设置 金融产品代码2
     * @param finprodId2 金融产品代码2
     */
    public void setFinprodId2(String finprodId2) {
        this.finprodId2 = finprodId2;
    }

    /**
     * 取得 权益登记日
     * @return 权益登记日
     */
    public Date getRegDate() {
        return regDate;
    }

    /**
     * 设置 权益登记日
     * @param regDate 权益登记日
     */
    public void setRegDate(Date regDate) {
        this.regDate = regDate;
    }

	
	/**
     * 取得 投资组合代码
     * @return 投资组合代码
     */
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * 设置 投资组合代码
     * @param portfolioId 投资组合代码
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId == null ? null : portfolioId.trim();
    }
    
    /**
     * 取得 委托代码
     * @return 委托代码
     */
    public String getEntrustId() {
        return entrustId;
    }

    /**
     * 设置 委托代码
     * @param entrustId 委托代码
     */
    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId == null ? null : entrustId.trim();
    }

    /**
     * 取得 成交编号
     * @return 成交编号
     */
    public String getDealId() {
        return dealId;
    }

    /**
     * 设置 成交编号
     * @param dealId 成交编号
     */
    public void setDealId(String dealId) {
        this.dealId = dealId;
    }
    /**
     * 取得 权益登记日份额
     * @return 权益登记日份额
     */
    public BigDecimal getRegDateAmt() {
        return regDateAmt;
    }

    /**
     * 设置 权益登记日份额
     * @param regDateAmt 权益登记日份额
     */
    public void setRegDateAmt(BigDecimal regDateAmt) {
        this.regDateAmt = regDateAmt;
    }

    /**
     * 取得 结算方式
     * @return 结算方式
     */
    public String getSettMode() {
        return settMode;
    }

    /**
     * 设置 结算方式
     * @param settMode 结算方式
     */
    public void setSettMode(String settMode) {
        this.settMode = settMode;
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

    public String getIsPayWithTrade() {
        return isPayWithTrade;
    }

    public void setIsPayWithTrade(String isPayWithTrade) {
        this.isPayWithTrade = isPayWithTrade;
    }

	public String getAcctNo() {
		return acctNo;
	}

	public void setAcctNo(String acctNo) {
		this.acctNo = acctNo;
	}

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Long getRepoTerm() {
        return repoTerm;
    }

    public void setRepoTerm(Long repoTerm) {
        this.repoTerm = repoTerm;
    }

    public String getTermType() {
        return termType;
    }

    public void setTermType(String termType) {
        this.termType = termType;
    }

	public String getIsPurchTypeYx() {
		return isPurchTypeYx;
	}

	public void setIsPurchTypeYx(String isPurchTypeYx) {
		this.isPurchTypeYx = isPurchTypeYx;
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

	public BigDecimal getmIntAmt() {
		return mIntAmt;
	}

	public void setmIntAmt(BigDecimal mIntAmt) {
		this.mIntAmt = mIntAmt;
	}

	public BigDecimal getmTradeAmt() {
		return mTradeAmt;
	}

	public void setmTradeAmt(BigDecimal mTradeAmt) {
		this.mTradeAmt = mTradeAmt;
	}
    /**
     * 支付类型，交易方向为分红除权时填写，红利再投、现金分红
     */
    private String payType;

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }
}
