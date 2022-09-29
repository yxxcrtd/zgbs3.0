package com.joyintech.service.trade.transfer.assetstransfer.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;

import java.math.BigDecimal;
import java.util.Date;

public class AssetsTransferDto extends BaseEntity {
    /**
     * 业务编号
     */
    private String tradeId;

    private String totleTradeId;
    /**
     * 投资目的
     */
    private String invAim;
    /**
     * 分类6
     */
    private String type6;
    /**
     * 回购拆借方向
     */
    private String backLendDirection;
    /**
     * 组合代码
     */
    private String portfolioId;
    /**
     * 组合名称
     */
    private String portfolioName;
    /**
     * 资产类型
     */
    private String assetType;
    /**
     * 资产代码/标的资产代码
     */
    private String finprodMarketId;
    /**
     * 资产名称/标的资产简称
     */
    private String finprodName;
    /**
     * 通道
     */
    private String chlName;
    /**
     * 证券账户
     */
    private String secAcctName;
    /**
     * 起息日
     */
    private Date vdate;
    /**
     * 到期日
     */
    private Date mdate;
    /**
     * 交易方向
     */
    private String ps;
    /**
     * 剩余天数
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal days;
    /**
     * 利率/收益率（%）
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal yield;
    /**
     * 券面总额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal amount;
    /**
     * 本金金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal printAmt;
    /**
     * 持仓数量/持仓份额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal posAmt;
    /**
     * 通道代码
     */
    private String chlId;
    /**
     * 证券托管户代码
     */
    private String secAcctId;
    /**
     * 金融产品类型
     */
    private String finprodType2;
    /**
     * 金融产品代码
     */
    private String finprodId;
    /**
     * 收益类型
     */
    private String profitType;
    /**
     * 转仓状态
     */
    private String status;
    /**
     * 交易日期
     */
    private Date tradeDate;
    /**
     * 交易场所
     */
    private String tradeMarket;
    /**
     * 币种名称
     */
    private String ccyName;
    /**
     * 净价/单位净值/交易价格
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal netPrice;
    /**
     * 交易数量/交易份额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal tradeAmt;
    /**
     * 币种
     */
    private String ccy;
    /**
     * 交易日历编号
     */
    private String calendarId;

    /**
     * 获取 业务编号
     *
     * @return tradeId 业务编号
     */
    public String getTradeId() {
        return this.tradeId;
    }

    /**
     * 设置 业务编号
     *
     * @param tradeId 业务编号
     */
    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    /**
     * 获取 组合代码
     *
     * @return portfolioId 组合代码
     */
    public String getPortfolioId() {
        return this.portfolioId;
    }

    /**
     * 设置 组合代码
     *
     * @param portfolioId 组合代码
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    /**
     * 获取 组合名称
     *
     * @return portfolioName 组合名称
     */
    public String getPortfolioName() {
        return this.portfolioName;
    }

    /**
     * 设置 组合名称
     *
     * @param portfolioName 组合名称
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    /**
     * 获取 资产类型
     *
     * @return assetType 资产类型
     */
    public String getAssetType() {
        return this.assetType;
    }

    /**
     * 设置 资产类型
     *
     * @param assetType 资产类型
     */
    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    /**
     * 获取 资产代码标的资产代码
     *
     * @return finprodMarketId 资产代码标的资产代码
     */
    public String getFinprodMarketId() {
        return this.finprodMarketId;
    }

    /**
     * 设置 资产代码标的资产代码
     *
     * @param finprodMarketId 资产代码标的资产代码
     */
    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    /**
     * 获取 资产名称标的资产简称
     *
     * @return finprodName 资产名称标的资产简称
     */
    public String getFinprodName() {
        return this.finprodName;
    }

    /**
     * 设置 资产名称标的资产简称
     *
     * @param finprodName 资产名称标的资产简称
     */
    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    /**
     * 获取 通道
     *
     * @return chlName 通道
     */
    public String getChlName() {
        return this.chlName;
    }

    /**
     * 设置 通道
     *
     * @param chlName 通道
     */
    public void setChlName(String chlName) {
        this.chlName = chlName;
    }

    /**
     * 获取 证券账户
     *
     * @return secAcctName 证券账户
     */
    public String getSecAcctName() {
        return this.secAcctName;
    }

    /**
     * 设置 证券账户
     *
     * @param secAcctName 证券账户
     */
    public void setSecAcctName(String secAcctName) {
        this.secAcctName = secAcctName;
    }

    /**
     * 获取 起息日
     *
     * @return vdate 起息日
     */
    public Date getVdate() {
        return this.vdate;
    }

    /**
     * 设置 起息日
     *
     * @param vdate 起息日
     */
    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    /**
     * 获取 到期日
     *
     * @return mdate 到期日
     */
    public Date getMdate() {
        return this.mdate;
    }

    /**
     * 设置 到期日
     *
     * @param mdate 到期日
     */
    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    /**
     * 获取 交易方向
     *
     * @return ps 交易方向
     */
    public String getPs() {
        return this.ps;
    }

    /**
     * 设置 交易方向
     *
     * @param ps 交易方向
     */
    public void setPs(String ps) {
        this.ps = ps;
    }

    /**
     * 获取 剩余天数
     *
     * @return days 剩余天数
     */
    public BigDecimal getDays() {
        return this.days;
    }

    /**
     * 设置 剩余天数
     *
     * @param days 剩余天数
     */
    public void setDays(BigDecimal days) {
        this.days = days;
    }

    /**
     * 获取 利率收益率（%）
     *
     * @return yield 利率收益率（%）
     */
    public BigDecimal getYield() {
        return this.yield;
    }

    /**
     * 设置 利率收益率（%）
     *
     * @param yield 利率收益率（%）
     */
    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

    /**
     * 获取 券面总额
     *
     * @return amount 券面总额
     */
    public BigDecimal getAmount() {
        return this.amount;
    }

    /**
     * 设置 券面总额
     *
     * @param amount 券面总额
     */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * 获取 本金金额
     *
     * @return printAmt 本金金额
     */
    public BigDecimal getPrintAmt() {
        return this.printAmt;
    }

    /**
     * 设置 本金金额
     *
     * @param printAmt 本金金额
     */
    public void setPrintAmt(BigDecimal printAmt) {
        this.printAmt = printAmt;
    }

    /**
     * 获取 持仓数量持仓份额
     *
     * @return posAmt 持仓数量持仓份额
     */
    public BigDecimal getPosAmt() {
        return this.posAmt;
    }

    /**
     * 设置 持仓数量持仓份额
     *
     * @param posAmt 持仓数量持仓份额
     */
    public void setPosAmt(BigDecimal posAmt) {
        this.posAmt = posAmt;
    }

    /**
     * 获取 通道代码
     *
     * @return chlId 通道代码
     */
    public String getChlId() {
        return this.chlId;
    }

    /**
     * 设置 通道代码
     *
     * @param chlId 通道代码
     */
    public void setChlId(String chlId) {
        this.chlId = chlId;
    }

    /**
     * 获取 证券托管户代码
     *
     * @return secAcctId 证券托管户代码
     */
    public String getSecAcctId() {
        return this.secAcctId;
    }

    /**
     * 设置 证券托管户代码
     *
     * @param secAcctId 证券托管户代码
     */
    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    /**
     * 获取 金融产品类型
     *
     * @return finprodType2 金融产品类型
     */
    public String getFinprodType2() {
        return this.finprodType2;
    }

    /**
     * 设置 金融产品类型
     *
     * @param finprodType2 金融产品类型
     */
    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    /**
     * 获取 金融产品代码
     *
     * @return finprodId 金融产品代码
     */
    public String getFinprodId() {
        return this.finprodId;
    }

    /**
     * 设置 金融产品代码
     *
     * @param finprodId 金融产品代码
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    /**
     * 获取 收益类型
     *
     * @return profitType 收益类型
     */
    public String getProfitType() {
        return this.profitType;
    }

    /**
     * 设置 收益类型
     *
     * @param profitType 收益类型
     */
    public void setProfitType(String profitType) {
        this.profitType = profitType;
    }

    /**
     * 获取 转仓状态
     *
     * @return status 转仓状态
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * 设置 转仓状态
     *
     * @param status 转仓状态
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 获取 交易日期
     *
     * @return tradeDate 交易日期
     */
    public Date getTradeDate() {
        return this.tradeDate;
    }

    /**
     * 设置 交易日期
     *
     * @param tradeDate 交易日期
     */
    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    /**
     * 获取 币种名称
     *
     * @return ccyName 币种名称
     */
    public String getCcyName() {
        return this.ccyName;
    }

    /**
     * 设置 币种名称
     *
     * @param ccyName 币种名称
     */
    public void setCcyName(String ccyName) {
        this.ccyName = ccyName;
    }

    /**
     * 获取 净价单位净值交易价格
     *
     * @return netPrice 净价单位净值交易价格
     */
    public BigDecimal getNetPrice() {
        return this.netPrice;
    }

    /**
     * 设置 净价单位净值交易价格
     *
     * @param netPrice 净价单位净值交易价格
     */
    public void setNetPrice(BigDecimal netPrice) {
        this.netPrice = netPrice;
    }

    /**
     * 获取 交易数量交易份额
     *
     * @return tradeAmt 交易数量交易份额
     */
    public BigDecimal getTradeAmt() {
        return this.tradeAmt;
    }

    /**
     * 设置 交易数量交易份额
     *
     * @param tradeAmt 交易数量交易份额
     */
    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    /**
     * 获取 币种
     *
     * @return ccy 币种
     */
    public String getCcy() {
        return this.ccy;
    }

    /**
     * 设置 币种
     *
     * @param ccy 币种
     */
    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    /**
     * 获取 分类6
     *
     * @return type6 分类6
     */
    public String getType6() {
        return this.type6;
    }

    /**
     * 设置 分类6
     *
     * @param type6 分类6
     */
    public void setType6(String type6) {
        this.type6 = type6;
    }

    /**
     * 获取 交易日历编号
     *
     * @return calendarId 交易日历编号
     */
    public String getCalendarId() {
        return this.calendarId;
    }

    /**
     * 设置 交易日历编号
     *
     * @param calendarId 交易日历编号
     */
    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    /**
     * 获取 投资目的
     *
     * @return invAim 投资目的
     */
    public String getInvAim() {
        return this.invAim;
    }

    /**
     * 设置 投资目的
     *
     * @param invAim 投资目的
     */
    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    /**
     * 获取 交易场所
     *
     * @return tradeMarket 交易场所
     */
    public String getTradeMarket() {
        return this.tradeMarket;
    }

    /**
     * 设置 交易场所
     *
     * @param tradeMarket 交易场所
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    /**
     * 获取 回购拆借方向
     *
     * @return backLendDirection 回购拆借方向
     */
    public String getBackLendDirection() {
        return this.backLendDirection;
    }

    /**
     * 设置 回购拆借方向
     *
     * @param backLendDirection 回购拆借方向
     */
    public void setBackLendDirection(String backLendDirection) {
        this.backLendDirection = backLendDirection;
    }

    public String getTotleTradeId() {
        return totleTradeId;
    }

    public void setTotleTradeId(String totleTradeId) {
        this.totleTradeId = totleTradeId;
    }

    /**
     * 是否行权
     */
    private String isRight;

    public String getIsRight() {
        return isRight;
    }

    public void setIsRight(String isRight) {
        this.isRight = isRight;
    }
}
