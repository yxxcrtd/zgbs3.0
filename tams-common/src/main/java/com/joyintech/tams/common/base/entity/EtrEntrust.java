package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class EtrEntrust extends BaseEntity {
    /**
     * 委托代码
     */
    private String entrustId;

    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    private String finprodType2;

    /**
     * 交易场所，银行间、上交所、深交所、柜面等
     */
    private String tradeMarket;

    /**
     * 交易平台，银行间、上交所固定收益平台、上交所大宗交易平台、深交所大宗平台、深交所综合协议交易平台、其他等
     */
    private String tradePlat;

    /**
     * 交易市场，一级市场、二级市场
     */
    private String tradeMarketMode;

    /**
     * 交易方向
     */
    private String ps;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 投资目的
     */
    private String invAim;

    /**
     * 交易对手
     */
    private String counterId;

    /**
     * 是否外部交易，是、否
     */
    private String isOutTrade;

    /**
     * 对手方交易员
     */
    private String counterTrader;

    /**
     * 对手方联系方式
     */
    private String contactInfo;

    /**
     * 委托日期
     */
    private Date entrustDate;

    /**
     * 委托开始日
     */
    private Date vdate;

    /**
     * 委托结束日
     */
    private Date mdate;

    /**
     * 委托额度，交易金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal entrustAmt;

    /**
     * 委托数量，数量或者券面总额/100
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal entrustNum;

    /**
     * 可用数量
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal availableNum;

    /**
     * 组合可用现金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal proAvailableCash;

    /**
     * 托管户可用现金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal accAvailableCash;

    /**
     * 委托状态
     */
    private String entrustStatus;

    /**
     * 委托价格，净价、利率等
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal price;

    /**
     * 是否精确委托
     */
    private String isAccurate;

    /**
     * 投资经理
     */
    private String invManagerId;

    /**
     * 交易日历
     */
    private String calendarId;

    /**
     * 备注
     */
    private String remark;

    /**
     * 通道代码
     */
    private String chlId;

    /**
     * 销售代码
     */
    private String saleCode;

    /**
     * 价格模式
     */
    private String priceModel;

    /**
     * 委托类型
     */
    private String entrustType;

    /**
     * 证券代码
     */
    private String stockId;

    /**
     * 指令类别
     */
    private String entrustClass;

    /**
     *业务模式
     */
    private String businessModel;

    /**
     * 结算金额
     */
    private BigDecimal unitFpriceAmt;

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
     * 取得 组合代码
     * @return 组合代码
     */
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * 设置 组合代码
     * @param portfolioId 组合代码
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId == null ? null : portfolioId.trim();
    }

    /**
     * 取得 金融产品代码
     * @return 金融产品代码
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 设置 金融产品代码
     * @param finprodId 金融产品代码
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId == null ? null : finprodId.trim();
    }

    /**
     * 取得 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     * @return 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    public String getFinprodType2() {
        return finprodType2;
    }

    /**
     * 设置 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     * @param finprodType2 金融产品类型（投管分类），债券、理财产品、基金、理财产品模板、理财产品分层
     */
    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2 == null ? null : finprodType2.trim();
    }

    /**
     * 取得 交易场所，银行间、上交所、深交所、柜面等
     * @return 交易场所，银行间、上交所、深交所、柜面等
     */
    public String getTradeMarket() {
        return tradeMarket;
    }

    /**
     * 设置 交易场所，银行间、上交所、深交所、柜面等
     * @param tradeMarket 交易场所，银行间、上交所、深交所、柜面等
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket == null ? null : tradeMarket.trim();
    }

    /**
     * 取得 交易平台，银行间、上交所固定收益平台、上交所大宗交易平台、深交所大宗平台、深交所综合协议交易平台、其他等
     * @return 交易平台，银行间、上交所固定收益平台、上交所大宗交易平台、深交所大宗平台、深交所综合协议交易平台、其他等
     */
    public String getTradePlat() {
        return tradePlat;
    }

    /**
     * 设置 交易平台，银行间、上交所固定收益平台、上交所大宗交易平台、深交所大宗平台、深交所综合协议交易平台、其他等
     * @param tradePlat 交易平台，银行间、上交所固定收益平台、上交所大宗交易平台、深交所大宗平台、深交所综合协议交易平台、其他等
     */
    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat == null ? null : tradePlat.trim();
    }

    /**
     * 取得 交易市场，一级市场、二级市场
     * @return 交易市场，一级市场、二级市场
     */
    public String getTradeMarketMode() {
        return tradeMarketMode;
    }

    /**
     * 设置 交易市场，一级市场、二级市场
     * @param tradeMarketMode 交易市场，一级市场、二级市场
     */
    public void setTradeMarketMode(String tradeMarketMode) {
        this.tradeMarketMode = tradeMarketMode == null ? null : tradeMarketMode.trim();
    }

    /**
     * 取得 交易方向
     * @return 交易方向
     */
    public String getPs() {
        return ps;
    }

    /**
     * 设置 交易方向
     * @param ps 交易方向
     */
    public void setPs(String ps) {
        this.ps = ps == null ? null : ps.trim();
    }

    /**
     * 取得 币种
     * @return 币种
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * 设置 币种
     * @param ccy 币种
     */
    public void setCcy(String ccy) {
        this.ccy = ccy == null ? null : ccy.trim();
    }

    /**
     * 取得 投资目的
     * @return 投资目的
     */
    public String getInvAim() {
        return invAim;
    }

    /**
     * 设置 投资目的
     * @param invAim 投资目的
     */
    public void setInvAim(String invAim) {
        this.invAim = invAim == null ? null : invAim.trim();
    }

    /**
     * 取得 交易对手
     * @return 交易对手
     */
    public String getCounterId() {
        return counterId;
    }

    /**
     * 设置 交易对手
     * @param counterId 交易对手
     */
    public void setCounterId(String counterId) {
        this.counterId = counterId == null ? null : counterId.trim();
    }

    /**
     * 取得 是否外部交易，是、否
     * @return 是否外部交易，是、否
     */
    public String getIsOutTrade() {
        return isOutTrade;
    }

    /**
     * 设置 是否外部交易，是、否
     * @param isOutTrade 是否外部交易，是、否
     */
    public void setIsOutTrade(String isOutTrade) {
        this.isOutTrade = isOutTrade == null ? null : isOutTrade.trim();
    }

    /**
     * 取得 对手方交易员
     * @return 对手方交易员
     */
    public String getCounterTrader() {
        return counterTrader;
    }

    /**
     * 设置 对手方交易员
     * @param counterTrader 对手方交易员
     */
    public void setCounterTrader(String counterTrader) {
        this.counterTrader = counterTrader == null ? null : counterTrader.trim();
    }

    /**
     * 取得 对手方联系方式
     * @return 对手方联系方式
     */
    public String getContactInfo() {
        return contactInfo;
    }

    /**
     * 设置 对手方联系方式
     * @param contactInfo 对手方联系方式
     */
    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo == null ? null : contactInfo.trim();
    }

    /**
     * 取得 委托日期
     * @return 委托日期
     */
    public Date getEntrustDate() {
        return entrustDate;
    }

    /**
     * 设置 委托日期
     * @param entrustDate 委托日期
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setEntrustDate(Date entrustDate) {
        this.entrustDate = entrustDate;
    }

    /**
     * 取得 委托开始日
     * @return 委托开始日
     */
    public Date getVdate() {
        return vdate;
    }

    /**
     * 设置 委托开始日
     * @param vdate 委托开始日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    /**
     * 取得 委托结束日
     * @return 委托结束日
     */
    public Date getMdate() {
        return mdate;
    }

    /**
     * 设置 委托结束日
     * @param mdate 委托结束日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    /**
     * 取得 委托额度，交易金额
     * @return 委托额度，交易金额
     */
    public BigDecimal getEntrustAmt() {
        return entrustAmt;
    }

    /**
     * 设置 委托额度，交易金额
     * @param entrustAmt 委托额度，交易金额
     */
    public void setEntrustAmt(BigDecimal entrustAmt) {
        this.entrustAmt = entrustAmt;
    }

    /**
     * 取得 委托数量，数量或者券面总额/100
     * @return 委托数量，数量或者券面总额/100
     */
    public BigDecimal getEntrustNum() {
        return entrustNum;
    }

    /**
     * 设置 委托数量，数量或者券面总额/100
     * @param entrustNum 委托数量，数量或者券面总额/100
     */
    public void setEntrustNum(BigDecimal entrustNum) {
        this.entrustNum = entrustNum;
    }

    /**
     * 取得 可用数量
     * @return 可用数量
     */
    public BigDecimal getAvailableNum() {
        return availableNum;
    }

    /**
     * 设置 可用数量
     * @param availableNum 可用数量
     */
    public void setAvailableNum(BigDecimal availableNum) {
        this.availableNum = availableNum;
    }

    /**
     * 取得 组合可用现金
     * @return 组合可用现金
     */
    public BigDecimal getProAvailableCash() {
        return proAvailableCash;
    }

    /**
     * 设置 组合可用现金
     * @param proAvailableCash 组合可用现金
     */
    public void setProAvailableCash(BigDecimal proAvailableCash) {
        this.proAvailableCash = proAvailableCash;
    }

    /**
     * 取得 托管户可用现金
     * @return 托管户可用现金
     */
    public BigDecimal getAccAvailableCash() {
        return accAvailableCash;
    }

    /**
     * 设置 托管户可用现金
     * @param accAvailableCash 托管户可用现金
     */
    public void setAccAvailableCash(BigDecimal accAvailableCash) {
        this.accAvailableCash = accAvailableCash;
    }

    /**
     * 取得 委托状态
     * @return 委托状态
     */
    public String getEntrustStatus() {
        return entrustStatus;
    }

    /**
     * 设置 委托状态
     * @param entrustStatus 委托状态
     */
    public void setEntrustStatus(String entrustStatus) {
        this.entrustStatus = entrustStatus == null ? null : entrustStatus.trim();
    }

    /**
     * 取得 委托价格，净价、利率等
     * @return 委托价格，净价、利率等
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置 委托价格，净价、利率等
     * @param price 委托价格，净价、利率等
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 取得 是否精确委托
     * @return 是否精确委托
     */
    public String getIsAccurate() {
        return isAccurate;
    }

    /**
     * 设置 是否精确委托
     * @param isAccurate 是否精确委托
     */
    public void setIsAccurate(String isAccurate) {
        this.isAccurate = isAccurate == null ? null : isAccurate.trim();
    }

    /**
     * 取得 投资经理
     * @return 投资经理
     */
    public String getInvManagerId() {
        return invManagerId;
    }

    /**
     * 设置 投资经理
     * @param invManagerId 投资经理
     */
    public void setInvManagerId(String invManagerId) {
        this.invManagerId = invManagerId == null ? null : invManagerId.trim();
    }

    /**
     * 取得 交易日历
     * @return 交易日历
     */
    public String getCalendarId() {
        return calendarId;
    }

    /**
     * 设置 交易日历
     * @param calendarId 交易日历
     */
    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId == null ? null : calendarId.trim();
    }

    /**
     * 取得 备注
     * @return 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置 备注
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * 取得 通道代码
     * @return 通道代码
     */
    public String getChlId() {
        return chlId;
    }

    /**
     * 设置 通道代码
     * @param chlId 通道代码
     */
    public void setChlId(String chlId) {
        this.chlId = chlId == null ? null : chlId.trim();
    }

    /**
     * 取得 销售代码
     * @return 销售代码
     */
    public String getSaleCode() {
        return saleCode;
    }

    /**
     * 设置 销售代码
     * @param saleCode 销售代码
     */
    public void setSaleCode(String saleCode) {
        this.saleCode = saleCode == null ? null : saleCode.trim();
    }

    /**
     * 取得 价格模式
     * @return 价格模式
     */
    public String getPriceModel() {
        return priceModel;
    }

    /**
     * 设置 价格模式
     * @param priceModel 价格模式
     */
    public void setPriceModel(String priceModel) {
        this.priceModel = priceModel == null ? null : priceModel.trim();
    }

    /**
     * 取得 委托类型
     * @return 委托类型
     */
    public String getEntrustType() {
        return entrustType;
    }

    /**
     * 设置 委托类型
     * @param entrustType 委托类型
     */
    public void setEntrustType(String entrustType) {
        this.entrustType = entrustType == null ? null : entrustType.trim();
    }

    /**
     * 取得 证券代码
     * @return 证券代码
     */
    public String getStockId() {
        return stockId;
    }

    /**
     * 设置 证券代码
     * @param stockId 证券代码
     */
    public void setStockId(String stockId) {
        this.stockId = stockId == null ? null : stockId.trim();
    }

    /**
     * 取得 指令类别
     * @return 指令类别
     */
    public String getEntrustClass() {
        return entrustClass;
    }

    /**
     * 设置 指令类别
     * @param entrustClass 指令类别
     */
    public void setEntrustClass(String entrustClass) {
        this.entrustClass = entrustClass == null ? null : entrustClass.trim();
    }

    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }

    public BigDecimal getUnitFpriceAmt() {
        return unitFpriceAmt;
    }

    public void setUnitFpriceAmt(BigDecimal unitFpriceAmt) {
        this.unitFpriceAmt = unitFpriceAmt;
    }
}