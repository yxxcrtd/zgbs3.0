/*
 * 文件名：EtrExchangeTradeDto002.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.systemtools.util.SysDatadictUtil;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author chendijun
 * @version 1.0 2020年7月2日
 * @see EtrExchangeOnWayRep002Dto
 * @since 1.0
 */
@PageId("EtrExchangeOnWayRep002Dto")
public class EtrExchangeOnWayRep002Dto extends BaseEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -5799209247492596989L;

    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 组合名称
     */
    private String portfolioName;

    /**
     * 交易方向
     */
    private String ps;
    /**
     * 交易方向
     */
    private String psStr;
    /**
     * 购回状态
     */
    private String purStatus;
    /**
     * 交收状态
     */
    private String settleStatus;
    /**
     * 回购天数
     */
    private Integer repDays;
    /**
     * 回购到期日
     */
    private Date backDate;
    /**
     * 市场
     */
    private String market;
    private String marketStr;
    /**
     * 质押券代码
     */
    private String stockCode;
    /**
     * 折算率
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal discountRate;
    /**
     * 质押券数量
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal repSecNum;
    /**
     * 质押券面总额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal pledStrdPrinAmt;
    /**
     * 回购金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal tradeAmt;
    /**
     * 回购利率
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal yield;
    /**
     * 首次结算日
     */
    private Date fristSettleDate;
    /**
     * 到期结算日
     */
    private Date expireSettleDate;
    /**
     * 实际占款天数
     */
    private Integer occupyDays;
    /**
     * 到期利息
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal mIntAmt;
    /**
     * 到期结算金额
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal expireSettleAmt;
    /**
     * 成交编号
     */
    private String tradeId;
    /**
     * 初始交易编号
     */
    private String intiTradeId;

    /**
     * 质押券名称
     */
    private String stockName;

    /**
     * 是否到期
     */
    private String isExpire;

    /**
     * 质押券单位
     */
    private String pledgeUnit;

    /**
     * 质押券面值
     */
    private String pledgeFaceValue;

    /**
     * 剩余日期
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal remainDays;

    /**
     * 资金账户名称
     */
    private String acctName;

    /**
     * 回购期限
     */
    private Long term;

    /**
     * 剩余天数
     */
    private Long leftDay;

    /**
     * 交易对手
     */
    private String counterName;
    /**
     * 正回购方交易员代码
     */
    private String repurTraderId;
    /**
     * 正回购方交易商代码
     */
    private String repurOrgId;
    /**
     * 逆回购方交易员代码
     */
    private String revRepurTraderId;
    /**
     * 逆回购方交易商代码
     */
    private String revRepurOrgId;
    /**
     * 本方交易员
     */
    private String tradeName;
    /**
     * 本方交易商
     */
    private String dealerName;
    /**
     * 本方交易主体
     */
    private String investorName;
    /**
     * 对手方交易员
     */
    private String counterTradeName;
    /**
     * 对手方交易商
     */
    private String counterDealerName;
    /**
     * 对手方交易主体
     */
    private String counterInvestorName;




    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getPurStatus() {
        return purStatus;
    }

    public void setPurStatus(String purStatus) {
        this.purStatus = purStatus;
    }

    public String getSettleStatus() {
        return settleStatus;
    }

    public void setSettleStatus(String settleStatus) {
        this.settleStatus = settleStatus;
    }

    public Integer getRepDays() {
        return repDays;
    }

    public void setRepDays(Integer repDays) {
        this.repDays = repDays;
    }

    public Date getBackDate() {
        return backDate;
    }

    public void setBackDate(Date backDate) {
        this.backDate = backDate;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getStockCode() {
        return stockCode;
    }

    public void setStockCode(String stockCode) {
        this.stockCode = stockCode;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public BigDecimal getRepSecNum() {
        return repSecNum;
    }

    public void setRepSecNum(BigDecimal repSecNum) {
        this.repSecNum = repSecNum;
    }

    public BigDecimal getPledStrdPrinAmt() {
        return pledStrdPrinAmt;
    }

    public void setPledStrdPrinAmt(BigDecimal pledStrdPrinAmt) {
        this.pledStrdPrinAmt = pledStrdPrinAmt;
    }

    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    public BigDecimal getYield() {
        return yield;
    }

    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

    public Date getFristSettleDate() {
        return fristSettleDate;
    }

    public void setFristSettleDate(Date fristSettleDate) {
        this.fristSettleDate = fristSettleDate;
    }

    public Date getExpireSettleDate() {
        return expireSettleDate;
    }

    public void setExpireSettleDate(Date expireSettleDate) {
        this.expireSettleDate = expireSettleDate;
    }

    public Integer getOccupyDays() {
        return occupyDays;
    }

    public void setOccupyDays(Integer occupyDays) {
        this.occupyDays = occupyDays;
    }

    public BigDecimal getmIntAmt() {
        return mIntAmt;
    }

    public void setmIntAmt(BigDecimal mIntAmt) {
        this.mIntAmt = mIntAmt;
    }

    public BigDecimal getExpireSettleAmt() {
        return expireSettleAmt;
    }

    public void setExpireSettleAmt(BigDecimal expireSettleAmt) {
        this.expireSettleAmt = expireSettleAmt;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getIntiTradeId() {
        return intiTradeId;
    }

    public void setIntiTradeId(String intiTradeId) {
        this.intiTradeId = intiTradeId;
    }

    public String getPsStr() {
        return SysDatadictUtil.getNameFromDataDict(DictCode.交易方向,this.ps);
    }

    public String getMarketStr() {
        return SysDatadictUtil.getNameFromDataDict(DictCode.交易流通场所,this.market);
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getIsExpire() {
        return isExpire;
    }

    public void setIsExpire(String isExpire) {
        this.isExpire = isExpire;
    }

    public String getIsExpireStr() {
        return SysDatadictUtil.getNameFromDataDict(DictCode.是否,this.isExpire);
    }

    public String getPledgeUnit() {
        return pledgeUnit;
    }

    public void setPledgeUnit(String pledgeUnit) {
        this.pledgeUnit = pledgeUnit;
    }

    public String getPledgeUnitStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易单位,getPledgeUnit());
    }

    public BigDecimal getRemainDays() {
        return remainDays;
    }

    public void setRemainDays(BigDecimal remainDays) {
        this.remainDays = remainDays;
    }

    public String getPledgeFaceValue() {
        return pledgeFaceValue;
    }

    public void setPledgeFaceValue(String pledgeFaceValue) {
        this.pledgeFaceValue = pledgeFaceValue;
    }

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }

    public Long getTerm() {
        return term;
    }

    public void setTerm(Long term) {
        this.term = term;
    }

    public Long getLeftDay() {
        return leftDay;
    }

    public void setLeftDay(Long leftDay) {
        this.leftDay = leftDay;
    }

    public String getRepurTraderId() {
        return repurTraderId;
    }

    public void setRepurTraderId(String repurTraderId) {
        this.repurTraderId = repurTraderId;
    }

    public String getRepurOrgId() {
        return repurOrgId;
    }

    public void setRepurOrgId(String repurOrgId) {
        this.repurOrgId = repurOrgId;
    }

    public String getRevRepurTraderId() {
        return revRepurTraderId;
    }

    public void setRevRepurTraderId(String revRepurTraderId) {
        this.revRepurTraderId = revRepurTraderId;
    }

    public String getRevRepurOrgId() {
        return revRepurOrgId;
    }

    public void setRevRepurOrgId(String revRepurOrgId) {
        this.revRepurOrgId = revRepurOrgId;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }


    public String getTradeName() {
        return tradeName;
    }

    public void setTradeName(String tradeName) {
        this.tradeName = tradeName;
    }

    public String getDealerName() {
        return dealerName;
    }

    public void setDealerName(String dealerName) {
        this.dealerName = dealerName;
    }

    public String getInvestorName() {
        return investorName;
    }

    public void setInvestorName(String investorName) {
        this.investorName = investorName;
    }

    public String getCounterTradeName() {
        return counterTradeName;
    }

    public void setCounterTradeName(String counterTradeName) {
        this.counterTradeName = counterTradeName;
    }

    public String getCounterDealerName() {
        return counterDealerName;
    }

    public void setCounterDealerName(String counterDealerName) {
        this.counterDealerName = counterDealerName;
    }

    public String getCounterInvestorName() {
        return counterInvestorName;
    }

    public void setCounterInvestorName(String counterInvestorName) {
        this.counterInvestorName = counterInvestorName;
    }
}
