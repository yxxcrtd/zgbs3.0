/*
 * 文件名：CfetsRepoDealQuotedPriceDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：yufei
 * 修改时间：2020年5月14日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.service.bankuplink.repo.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.framework.validation.annotation.NotEmpty;
import com.joyintech.tams.common.base.entity.EtrEntrust;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import com.joyintech.tams.invest.common.dto.common.PtlAllocationDto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * CFETS质押式回购报价画面委托指令dto 〈功能详细描述〉
 * 
 * @author yufei
 * @version 1.0 2020年5月14日
 * @see CfetsRepoDealQuotedPrice001Dto
 * @since 1.0
 */
public class CfetsRepoDealQuotedPrice001Dto extends EtrEntrust {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -5534440153855152285L;

    /**
     * 报价编号
     */
    private String quoteId;

    /**
     * 完成度
     */
    private BigDecimal transactioncom;
    
    /**
     * 交谈记录
     */
    private String dialogueRecord;
    
    /**
     * 操作类型
     */
    private String exeType;
    
    /**
     * 成交进度
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeFinishPercent;

    /**
     * 组合名称
     */
    private String portfolioName;

    /**
     * 证券账户
     */
    private String secAcctName;

    /**
     * 已完成额度
     */
    private BigDecimal finishAmt;

    /**
     * 剩余额度
     */
    private BigDecimal avaNum;

    /**
     * 上次 查询时间
     */
    private String lastQueryTime;

    /**
     * 清算速度
     */
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.settleSpreed")
    private String settleSpreed;

    /**
     * 证券账户ID
     */
    private String secAcctId;

    /**
     * 交割日期
     */
    private Date settleDate;

    /**
     * 结算方式
     */
    private String settleMethod;

    /**
     * 清算类型
     */
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.settleType")
    private String settleType;

    /**
     * 结算币种
     */
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.settleCcy")
    private String settleCcy;

    /**
     * 本方交易员
     */
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.mySideTrader")
    private String mySideTrader;
    
    /**
     * 本方交易员名称
     */
    private String mySideTraderName;
    
    /**
     * 对手方交易员名称
     */
    private String counterTraderName;

    /**
     * 本方
     */
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.mySide")
    private String mySide;

    /**
     * 本方简称
     */
    private String mySideAbbr;

    /**
     * 回购期限(天)
     */
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.term")
    private long term;

    /**
     * 首次结算方式
     */
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.vDeliveryType")
    private String vDeliveryType;

    /**
     * 到期结算方式
     */
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.mDeliveryType")
    private String mDeliveryType;

    /**
     * 卷面总额合计(万)
     */
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.pledgePrinTotal")
    private BigDecimal pledgePrinTotal;

    /**
     * 质押券折算总金额
     */
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.shareCtAmt")
    private BigDecimal shareCtAmt;

    /**
     * 交易金额(中债)
     */
    private BigDecimal cbDealAmt;

    /**
     * 交易金额(上清)
     */
    private BigDecimal scDealAmt;

    /**
     * 质押券折算总金额(中债)
     */
    private BigDecimal cbShareCtAmt;

    /**
     * 质押券折算总金额(上清)
     */
    private BigDecimal scShareCtAmt;

    /**
     * 交易金额
     */
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.dealAmt")
    private BigDecimal dealAmt;

    /**
     * 回购利息
     */
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.mIntAmt")
    private BigDecimal mIntAmt;

    /**
     * 到期结算金额
     */
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.mTradeAmt")
    private BigDecimal mTradeAmt;

    /**
     * 首期结算日
     */
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.vSettleDate")
    private Date vSettleDate;

    /**
     * 到期结算日
     */
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.mSettleDate")
    private Date mSettleDate;

    /**
     * 实际占款天数
     */
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.actDays")
    private long actDays;

    /**
     * 期限品种
     */
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.termType")
    private String termType;

    /**
     * 计息基础
     */
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.basis")
    private String basis;

    private List<CfetsRepoDealQuotedPrice003Dto> pledgeBondList;

    /**
     * 交易状态
     */
    private String tradeStatus;

    /**
     * 发送状态
     */
    private String sendStatus;

    /**
     * 本方角色
     */
    private String quoteRoleType;

    /**
     * 投资经理姓名
     */
    private String invManagerName;
    
    /**
     * 回购起息日
     */
    private Date repoVdate;
    
    /**
     * 回购到期日
     */
    private Date repoMdate;

    /**
     * 画面打开模式
     */
    private String quotaPageModel;


    /**
     * 操作
     */
    private String operatorType;
    /**
     * 版本号
     */
    private Long version;

    /**
     * 前置版本号
     */
    private Long bVersion;


    /**
     * CFETS报价编号
     */
    private String cfetsQuoteId;

    /**
     * 外部报价编号
     */
    private String pQuoteId;

    /**
     * 全价总额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal fpriceAmt;

    /**
     * 期限天数2，输入期限范围时使用
     */
    private Long termDays;


    /**
     * 组合分配信息
     */
    private List<PtlAllocationDto> ptlAllocationList;

    /**
     * 是否多组合
     */
    private String isDzh;

    /**
     * 是否跨托管
     */
    private String isKtg;

    /**
     * 是否mq推送
     * @return
     */
    private String isMq;

    /**
     * 是否软控
     */
    private String softSkipMode;

    /**
     * 交易手续费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal dealFeeAmt;

    /**
     * 结算服务费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal settFeeAmt;

    // 分配方式
    private String dipType;
    /**
     *数据已入新头寸，Y：已入
     */
    private String isExistNew;

    public Date getRepoVdate() {
		return repoVdate;
	}

	public void setRepoVdate(Date repoVdate) {
		this.repoVdate = repoVdate;
	}

	public Date getRepoMdate() {
		return repoMdate;
	}

	public void setRepoMdate(Date repoMdate) {
		this.repoMdate = repoMdate;
	}

	/**
     * 获取 invManagerName
     * 
     * @return invManagerName.
     */
    public String getInvManagerName() {
        return invManagerName;
    }

    /**
     * 设置 invManagerName
     * 
     * @param invManagerName invManagerName
     */
    public void setInvManagerName(String invManagerName) {
        this.invManagerName = invManagerName;
    }

    /**
     * 获取 quoteRoleType
     * 
     * @return quoteRoleType.
     */
    public String getQuoteRoleType() {
        return quoteRoleType;
    }

    /**
     * 设置 quoteRoleType
     * 
     * @param quoteRoleType quoteRoleType
     */
    public void setQuoteRoleType(String quoteRoleType) {
        this.quoteRoleType = quoteRoleType;
    }

    /**
     * 获取 tradeStatus
     * 
     * @return tradeStatus.
     */
    public String getTradeStatus() {
        return tradeStatus;
    }

    /**
     * 设置 tradeStatus
     * 
     * @param tradeStatus tradeStatus
     */
    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus;
    }

    /**
     * 获取 sendStatus
     * 
     * @return sendStatus.
     */
    public String getSendStatus() {
        return sendStatus;
    }

    /**
     * 设置 sendStatus
     * 
     * @param sendStatus sendStatus
     */
    public void setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
    }

    /**
     * 获取 pledgeBondList
     * 
     * @return pledgeBondList.
     */
    public List<CfetsRepoDealQuotedPrice003Dto> getPledgeBondList() {
        return pledgeBondList;
    }

    /**
     * 设置 pledgeBondList
     * 
     * @param pledgeBondList pledgeBondList
     */
    public void setPledgeBondList(List<CfetsRepoDealQuotedPrice003Dto> pledgeBondList) {
        this.pledgeBondList = pledgeBondList;
    }

    /**
     * 获取 pledgePrinTotal
     * 
     * @return pledgePrinTotal.
     */
    public BigDecimal getPledgePrinTotal() {
        return pledgePrinTotal;
    }

    /**
     * 设置 pledgePrinTotal
     * 
     * @param pledgePrinTotal pledgePrinTotal
     */
    public void setPledgePrinTotal(BigDecimal pledgePrinTotal) {
        this.pledgePrinTotal = pledgePrinTotal;
    }

    /**
     * 获取 mTradeAmt
     * 
     * @return mTradeAmt.
     */
    public BigDecimal getmTradeAmt() {
        return mTradeAmt;
    }

    /**
     * 设置 mTradeAmt
     * 
     * @param mTradeAmt mTradeAmt
     */
    public void setmTradeAmt(BigDecimal mTradeAmt) {
        this.mTradeAmt = mTradeAmt;
    }

    /**
     * 获取 shareCtAmt
     * 
     * @return shareCtAmt.
     */
    public BigDecimal getShareCtAmt() {
        return shareCtAmt;
    }

    /**
     * 设置 shareCtAmt
     * 
     * @param shareCtAmt shareCtAmt
     */
    public void setShareCtAmt(BigDecimal shareCtAmt) {
        this.shareCtAmt = shareCtAmt;
    }

    /**
     * 获取 cbDealAmt
     * 
     * @return cbDealAmt.
     */
    public BigDecimal getCbDealAmt() {
        return cbDealAmt;
    }

    /**
     * 设置 cbDealAmt
     * 
     * @param cbDealAmt cbDealAmt
     */
    public void setCbDealAmt(BigDecimal cbDealAmt) {
        this.cbDealAmt = cbDealAmt;
    }

    /**
     * 获取 scDealAmt
     * 
     * @return scDealAmt.
     */
    public BigDecimal getScDealAmt() {
        return scDealAmt;
    }

    /**
     * 设置 scDealAmt
     * 
     * @param scDealAmt scDealAmt
     */
    public void setScDealAmt(BigDecimal scDealAmt) {
        this.scDealAmt = scDealAmt;
    }

    /**
     * 获取 cbShareCtAmt
     * 
     * @return cbShareCtAmt.
     */
    public BigDecimal getCbShareCtAmt() {
        return cbShareCtAmt;
    }

    /**
     * 设置 cbShareCtAmt
     * 
     * @param cbShareCtAmt cbShareCtAmt
     */
    public void setCbShareCtAmt(BigDecimal cbShareCtAmt) {
        this.cbShareCtAmt = cbShareCtAmt;
    }

    /**
     * 获取 scShareCtAmt
     * 
     * @return scShareCtAmt.
     */
    public BigDecimal getScShareCtAmt() {
        return scShareCtAmt;
    }

    /**
     * 设置 scShareCtAmt
     * 
     * @param scShareCtAmt scShareCtAmt
     */
    public void setScShareCtAmt(BigDecimal scShareCtAmt) {
        this.scShareCtAmt = scShareCtAmt;
    }

    /**
     * 获取 dealAmt
     * 
     * @return dealAmt.
     */
    public BigDecimal getDealAmt() {
        return dealAmt;
    }

    /**
     * 设置 dealAmt
     * 
     * @param dealAmt dealAmt
     */
    public void setDealAmt(BigDecimal dealAmt) {
        this.dealAmt = dealAmt;
    }

    /**
     * 获取 mIntAmt
     * 
     * @return mIntAmt.
     */
    public BigDecimal getmIntAmt() {
        return mIntAmt;
    }

    /**
     * 设置 mIntAmt
     * 
     * @param mIntAmt mIntAmt
     */
    public void setmIntAmt(BigDecimal mIntAmt) {
        this.mIntAmt = mIntAmt;
    }

    /**
     * 获取 vSettleDate
     * 
     * @return vSettleDate.
     */
    public Date getvSettleDate() {
        return vSettleDate;
    }

    /**
     * 设置 vSettleDate
     * 
     * @param vSettleDate vSettleDate
     */
    public void setvSettleDate(Date vSettleDate) {
        this.vSettleDate = vSettleDate;
    }

    /**
     * 获取 mSettleDate
     * 
     * @return mSettleDate.
     */
    public Date getmSettleDate() {
        return mSettleDate;
    }

    /**
     * 设置 mSettleDate
     * 
     * @param mSettleDate mSettleDate
     */
    public void setmSettleDate(Date mSettleDate) {
        this.mSettleDate = mSettleDate;
    }

    /**
     * 获取 actDays
     * 
     * @return actDays.
     */
    public long getActDays() {
        return actDays;
    }

    /**
     * 设置 actDays
     * 
     * @param actDays actDays
     */
    public void setActDays(long actDays) {
        this.actDays = actDays;
    }

    /**
     * 获取 termType
     * 
     * @return termType.
     */
    public String getTermType() {
        return termType;
    }

    /**
     * 设置 termType
     * 
     * @param termType termType
     */
    public void setTermType(String termType) {
        this.termType = termType;
    }

    /**
     * 获取 basis
     * 
     * @return basis.
     */
    public String getBasis() {
        return basis;
    }

    /**
     * 设置 basis
     * 
     * @param basis basis
     */
    public void setBasis(String basis) {
        this.basis = basis;
    }

    /**
     * 获取 vDeliveryType
     * 
     * @return vDeliveryType.
     */
    public String getvDeliveryType() {
        return vDeliveryType;
    }

    /**
     * 设置 vDeliveryType
     * 
     * @param vDeliveryType vDeliveryType
     */
    public void setvDeliveryType(String vDeliveryType) {
        this.vDeliveryType = vDeliveryType;
    }

    /**
     * 获取 mDeliveryType
     * 
     * @return mDeliveryType.
     */
    public String getmDeliveryType() {
        return mDeliveryType;
    }

    /**
     * 设置 mDeliveryType
     * 
     * @param mDeliveryType mDeliveryType
     */
    public void setmDeliveryType(String mDeliveryType) {
        this.mDeliveryType = mDeliveryType;
    }

    /**
     * 获取 term
     * 
     * @return term.
     */
    public long getTerm() {
        return term;
    }

    /**
     * 设置 term
     * 
     * @param term term
     */
    public void setTerm(long term) {
        this.term = term;
    }

    /**
     * 获取 mySide
     * 
     * @return mySide.
     */
    public String getMySide() {
        return mySide;
    }

    /**
     * 设置 mySide
     * 
     * @param mySide mySide
     */
    public void setMySide(String mySide) {
        this.mySide = mySide;
    }

    /**
     * 获取 mySideAbbr
     * @return mySideAbbr.
     */
    public String getMySideAbbr() {
        return mySideAbbr;
    }


    /**
     * 设置 mySideAbbr
     * @param mySideAbbr mySideAbbr
     */
    public void setMySideAbbr(String mySideAbbr) {
        this.mySideAbbr = mySideAbbr;
    }

    /**
     * 获取 secAcctId
     * 
     * @return secAcctId.
     */
    public String getSecAcctId() {
        return secAcctId;
    }

    /**
     * 设置 secAcctId
     * 
     * @param secAcctId secAcctId
     */
    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    /**
     * 获取 mySideTrader
     * 
     * @return mySideTrader.
     */
    public String getMySideTrader() {
        return mySideTrader;
    }

    /**
     * 设置 mySideTrader
     * 
     * @param mySideTrader mySideTrader
     */
    public void setMySideTrader(String mySideTrader) {
        this.mySideTrader = mySideTrader;
    }

    /**
     * 获取 settleMethod
     * 
     * @return settleMethod.
     */
    public String getSettleMethod() {
        return settleMethod;
    }

    /**
     * 设置 settleMethod
     * 
     * @param settleMethod settleMethod
     */
    public void setSettleMethod(String settleMethod) {
        this.settleMethod = settleMethod;
    }

    /**
     * 获取 settleType
     * 
     * @return settleType.
     */
    public String getSettleType() {
        return settleType;
    }

    /**
     * 设置 settleType
     * 
     * @param settleType settleType
     */
    public void setSettleType(String settleType) {
        this.settleType = settleType;
    }

    /**
     * 获取 settleCcy
     * 
     * @return settleCcy.
     */
    public String getSettleCcy() {
        return settleCcy;
    }

    /**
     * 设置 settleCcy
     * 
     * @param settleCcy settleCcy
     */
    public void setSettleCcy(String settleCcy) {
        this.settleCcy = settleCcy;
    }

    /**
     * 获取 settleDate
     * 
     * @return settleDate.
     */
    public Date getSettleDate() {
        return settleDate;
    }

    /**
     * 设置 settleDate
     * 
     * @param settleDate settleDate
     */
    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    /**
     * 获取 settleSpreed
     * 
     * @return settleSpreed.
     */
    public String getSettleSpreed() {
        return settleSpreed;
    }

    /**
     * 设置 settleSpreed
     * 
     * @param settleSpreed settleSpreed
     */
    public void setSettleSpreed(String settleSpreed) {
        this.settleSpreed = settleSpreed;
    }

    /**
     * 获取 lastQueryTime
     * 
     * @return lastQueryTime.
     */
    public String getLastQueryTime() {
        return lastQueryTime;
    }

    /**
     * 设置 lastQueryTime
     * 
     * @param lastQueryTime lastQueryTime
     */
    public void setLastQueryTime(String lastQueryTime) {
        this.lastQueryTime = lastQueryTime;
    }

    /**
     * 获取 transactioncom
     * 
     * @return transactioncom.
     */
    public BigDecimal getTransactioncom() {
        return transactioncom;
    }

    /**
     * 设置 transactioncom
     * 
     * @param transactioncom transactioncom
     */
    public void setTransactioncom(BigDecimal transactioncom) {
        this.transactioncom = transactioncom;
    }

    /**
     * 获取 portfolioName
     * 
     * @return portfolioName.
     */
    public String getPortfolioName() {
        return portfolioName;
    }

    /**
     * 设置 portfolioName
     * 
     * @param portfolioName portfolioName
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    /**
     * 获取 secAcctName
     * 
     * @return secAcctName.
     */
    public String getSecAcctName() {
        return secAcctName;
    }

    /**
     * 设置 secAcctName
     * 
     * @param secAcctName secAcctName
     */
    public void setSecAcctName(String secAcctName) {
        this.secAcctName = secAcctName;
    }

    /**
     * 获取 finishAmt
     * 
     * @return finishAmt.
     */
    public BigDecimal getFinishAmt() {
        return finishAmt;
    }

    /**
     * 设置 finishAmt
     * 
     * @param finishAmt finishAmt
     */
    public void setFinishAmt(BigDecimal finishAmt) {
        this.finishAmt = finishAmt;
    }

    /**
     * 获取 avaNum
     * 
     * @return avaNum.
     */
    public BigDecimal getAvaNum() {
        return avaNum;
    }

    /**
     * 设置 avaNum
     * 
     * @param avaNum avaNum
     */
    public void setAvaNum(BigDecimal avaNum) {
        this.avaNum = avaNum;
    }

    /**
     * 返回字典格式化后的数据
     * 
     * @return 字典显示名称
     * @see
     * @since 1.0
     */
    public String getEntrustStatusStr() {
        if (getEntrustStatus() != null) {
            return SysDataUtil.getNameFromDataDict(BussinessDictCode.委托状态,
                this.getEntrustStatus());
        }
        else {
            return null;
        }
    }

    /**
     * 返回字典格式化后的数据
     * 
     * @return 字典显示名称
     * @see
     * @since 1.0
     */
    public String getPsStr() {
        if (getPs() != null) {
            return SysDataUtil.getNameFromDataDict(BussinessDictCode.回购拆借方向, this.getPs());
        }
        else {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.entrustId")
    public String getEntrustId() {
        return super.getEntrustId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.portfolioId")
    public String getPortfolioId() {
        return super.getPortfolioId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.ps")
    public String getPs() {
        return super.getPs();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.ccy")
    public String getCcy() {
        return super.getCcy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.counterId")
    public String getCounterId() {
        return super.getCounterId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.counterTrader")
    public String getCounterTrader() {
        return super.getCounterTrader();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NotEmpty(fieldName = "CfetsBondDealQuotedPrice002.price")
    public BigDecimal getPrice() {
        return super.getPrice();
    }

    /**
     * 获取 quoteId
     * 
     * @return quoteId.
     */
    public String getQuoteId() {
        return quoteId;
    }

    /**
     * 设置 quoteId
     * 
     * @param quoteId quoteId
     */
    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

	public BigDecimal getTradeFinishPercent() {
		return tradeFinishPercent;
	}

	public void setTradeFinishPercent(BigDecimal tradeFinishPercent) {
		this.tradeFinishPercent = tradeFinishPercent;
	}

    public String getQuotaPageModel() {
        return quotaPageModel;
    }

    public void setQuotaPageModel(String quotaPageModel) {
        this.quotaPageModel = quotaPageModel;
    }

    public String getOperatorType() {
        return operatorType;
    }

    public void setOperatorType(String operatorType) {
        this.operatorType = operatorType;
    }

    public Long getbVersion() {
        return bVersion;
    }

    public void setbVersion(Long bVersion) {
        this.bVersion = bVersion;
    }

    public String getCfetsQuoteId() {
        return cfetsQuoteId;
    }

    public void setCfetsQuoteId(String cfetsQuoteId) {
        this.cfetsQuoteId = cfetsQuoteId;
    }

    public BigDecimal getFpriceAmt() {
        return fpriceAmt;
    }

    public void setFpriceAmt(BigDecimal fpriceAmt) {
        this.fpriceAmt = fpriceAmt;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getpQuoteId() {
        return pQuoteId;
    }

    public void setpQuoteId(String pQuoteId) {
        this.pQuoteId = pQuoteId;
    }

	public String getDialogueRecord() {
		return dialogueRecord;
	}

	public void setDialogueRecord(String dialogueRecord) {
		this.dialogueRecord = dialogueRecord;
	}

	public String getExeType() {
		return exeType;
	}

	public void setExeType(String exeType) {
		this.exeType = exeType;
	}

	public String getMySideTraderName() {
		return mySideTraderName;
	}

	public void setMySideTraderName(String mySideTraderName) {
		this.mySideTraderName = mySideTraderName;
	}

	public String getCounterTraderName() {
		return counterTraderName;
	}

	public void setCounterTraderName(String counterTraderName) {
		this.counterTraderName = counterTraderName;
	}

    public Long getTermDays() {
        return termDays;
    }

    public void setTermDays(Long termDays) {
        this.termDays = termDays;
    }

    public List<PtlAllocationDto> getPtlAllocationList() {
        return ptlAllocationList;
    }

    public void setPtlAllocationList(List<PtlAllocationDto> ptlAllocationList) {
        this.ptlAllocationList = ptlAllocationList;
    }

    public String getIsDzh() {
        return isDzh;
    }

    public void setIsDzh(String isDzh) {
        this.isDzh = isDzh;
    }

    public String getIsMq() {
        return isMq;
    }

    public void setIsMq(String isMq) {
        this.isMq = isMq;
    }

    public String getIsKtg() {
        return isKtg;
    }

    public void setIsKtg(String isKtg) {
        this.isKtg = isKtg;
    }

    private Date tradeDate;

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }
    /**
     * 组合系列
     */
    private String portfolioSeries;

    public String getPortfolioSeries() {
        return portfolioSeries;
    }

    public void setPortfolioSeries(String portfolioSeries) {
        this.portfolioSeries = portfolioSeries;
    }

    /**
     * 中债登券面总额合计（万元）
     */
    private BigDecimal zzTotalAmt;

    /**
     * 上清所券面总额合计（万元）
     */
    private BigDecimal sqTotalAmt;

    public BigDecimal getZzTotalAmt() {
        return zzTotalAmt;
    }

    public void setZzTotalAmt(BigDecimal zzTotalAmt) {
        this.zzTotalAmt = zzTotalAmt;
    }

    public BigDecimal getSqTotalAmt() {
        return sqTotalAmt;
    }

    public void setSqTotalAmt(BigDecimal sqTotalAmt) {
        this.sqTotalAmt = sqTotalAmt;
    }

    public BigDecimal getDealFeeAmt() {
        return dealFeeAmt;
    }

    public void setDealFeeAmt(BigDecimal dealFeeAmt) {
        this.dealFeeAmt = dealFeeAmt;
    }

    public BigDecimal getSettFeeAmt() {
        return settFeeAmt;
    }

    public void setSettFeeAmt(BigDecimal settFeeAmt) {
        this.settFeeAmt = settFeeAmt;
    }

    public String getSoftSkipMode() {
        return softSkipMode;
    }

    public void setSoftSkipMode(String softSkipMode) {
        this.softSkipMode = softSkipMode;
    }

    public String getDipType() {
        return dipType;
    }

    public void setDipType(String dipType) {
        this.dipType = dipType;
    }

    public String getIsExistNew() {
        return isExistNew;
    }

    public void setIsExistNew(String isExistNew) {
        this.isExistNew = isExistNew;
    }
}
