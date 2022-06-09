package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class WflTrdFinProductDealTotal extends WflTrdFinProductDealTotalKey {
    /**
     * ????
     */
    private String busiType;

    /**
     * ??????
     */
    private String finprodId;

    /**
     * ?????????????????????????????????????
     */
    private String finprodType;

    /**
     * ?????????????????????????????????????
     */
    private String finprodType2;

    /**
     * ????
     */
    private Long branch;

    /**
     * ????
     */
    private Date tradeDate;

    /**
     * ???
     */
    private Date vdate;

    /**
     * ???
     */
    private Date mdate;

    /**
     * ????
     */
    private Date settleDate;

    /**
     * ????
     */
    private String ccy;

    /**
     * ????
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal unitCprice;

    /**
     * ????
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal unitInt;

    /**
     * ????
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal unitFprice;

    /**
     * ???????100???????????1
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal parValue;

    /**
     * ????
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal cpriceAmt;

    /**
     * ????
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal shareAmt;

    /**
     * ????
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal prinAmt;

    /**
     * ????
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal intAmt;

    /**
     * ????
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal fpriceAmt;

    /**
     * ???????
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal feeAmtPay;

    /**
     * ????
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tradeAmt;

    /**
     * ????????
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal feeAmtUnpay;

    /**
     * ?????
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal feeAmt;

    /**
     * ????
     */
    private String ps;

    /**
     * ?????
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal yield;

    /**
     * ?????
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal exerYield;

    /**
     * ????
     */
    private String invAim;

    /**
     * ?????????????
     */
    private String tradeStatus;

    /**
     * ?????
     */
    private String pTradeId;

    /**
     * ??????????????????????????
     */
    private String isCancel;

    /**
     * ???????????
     */
    private String isClean;

    /**
     * ????????????+??????+?????????????????????????????
     */
    private String cashId;

    /**
     * ???????
     */
    private String secManageAcctId;

    /**
     * ????????????????????????????????????
     */
    private String bTradeId;

    /**
     * ??/????
     */
    private String chlId;

    /**
     * ????
     */
    private String counterId;

    /**
     * ??????????
     */
    private String isOutTrade;

    /**
     * ????????????????????
     */
    private String tradeMarket;

    /**
     * ????????????????????????????????????????????????????
     */
    private String tradePlat;

    /**
     * ??????????????
     */
    private String tradeMarketMode;

    /**
     * ???
     */
    private String trader;

    /**
     * ??????
     */
    private String counterTrader;

    /**
     * ???????
     */
    private String counterContact;

    /**
     * ????????
     */
    private String fTradeId;

    /**
     * ??????
     */
    private String portfolioId;

    /**
     * ??????
     */
    private String prodId;

    /**
     * ????????
     */
    private String pTotleTradeId;

    /**
     * ??
     */
    private String remark;

    /**
     * ?????
     */
    private String workflowId;

    /**
     * 剩余名义本金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal remainPrin;

    /**
     * 投管业务类型
     */
    private String tgBusiType;

    /**
     * 清算速度
     */
    private Long settleSpeed;

    /**
     * 清算类型
     */
    private String clearType;

    /**
     * 有效时间
     */
    private Date effTime;

    /**
     * 首期结算方式
     */
    private String deliveryType;

    /**
     * 到期结算方式
     */
    private String mDeliveryType;

    /**
     * 利率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal rate;

    /**
     * 回购期限(天)
     */
    private Long repoTerm;

    /**
     * 期限品种
     */
    private String termType;

    /**
     * 是否逾期交易
     */
    private String isOverdueDeal;

    /**
     * 支付未支付利息
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal payUnpaidintAmt;

    /**
     * 是否转让还本未支付利息
     */
    private String ispayUnpaidint;

    // 多级法人改造 add by caiqun 20200615
    /**
     * 机构代码
     */
    private String institutionCode;
    // 多级法人改造 add by caiqun 20200615

    /**
     * 前一日港股持仓市值
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal yesterdayPosition;

    /**
     * 交易金额(港元)
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tradeAmtHk;

    /**
     * 卖出结算汇兑比率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal sellSettleRate;
    /**
     * 取得 ????
     * @return ????
     */
    public String getBusiType() {
        return busiType;
    }

    /**
     * 设置 ????
     * @param busiType ????
     */
    public void setBusiType(String busiType) {
        this.busiType = busiType == null ? null : busiType.trim();
    }

    /**
     * 取得 ??????
     * @return ??????
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 设置 ??????
     * @param finprodId ??????
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId == null ? null : finprodId.trim();
    }

    /**
     * 取得 ?????????????????????????????????????
     * @return ?????????????????????????????????????
     */
    public String getFinprodType() {
        return finprodType;
    }

    /**
     * 设置 ?????????????????????????????????????
     * @param finprodType ?????????????????????????????????????
     */
    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType == null ? null : finprodType.trim();
    }

    /**
     * 取得 ?????????????????????????????????????
     * @return ?????????????????????????????????????
     */
    public String getFinprodType2() {
        return finprodType2;
    }

    /**
     * 设置 ?????????????????????????????????????
     * @param finprodType2 ?????????????????????????????????????
     */
    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2 == null ? null : finprodType2.trim();
    }

    /**
     * 取得 ????
     * @return ????
     */
    public Long getBranch() {
        return branch;
    }

    /**
     * 设置 ????
     * @param branch ????
     */
    public void setBranch(Long branch) {
        this.branch = branch;
    }

    /**
     * 取得 ????
     * @return ????
     */
    public Date getTradeDate() {
        return tradeDate;
    }

    /**
     * 设置 ????
     * @param tradeDate ????
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    /**
     * 取得 ???
     * @return ???
     */
    public Date getVdate() {
        return vdate;
    }

    /**
     * 设置 ???
     * @param vdate ???
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    /**
     * 取得 ???
     * @return ???
     */
    public Date getMdate() {
        return mdate;
    }

    /**
     * 设置 ???
     * @param mdate ???
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    /**
     * 取得 ????
     * @return ????
     */
    public Date getSettleDate() {
        return settleDate;
    }

    /**
     * 设置 ????
     * @param settleDate ????
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    /**
     * 取得 ????
     * @return ????
     */
    public String getCcy() {
        return ccy;
    }

    /**
     * 设置 ????
     * @param ccy ????
     */
    public void setCcy(String ccy) {
        this.ccy = ccy == null ? null : ccy.trim();
    }

    /**
     * 取得 ????
     * @return ????
     */
    public BigDecimal getUnitCprice() {
        return unitCprice;
    }

    /**
     * 设置 ????
     * @param unitCprice ????
     */
    public void setUnitCprice(BigDecimal unitCprice) {
        this.unitCprice = unitCprice;
    }

    /**
     * 取得 ????
     * @return ????
     */
    public BigDecimal getUnitInt() {
        return unitInt;
    }

    /**
     * 设置 ????
     * @param unitInt ????
     */
    public void setUnitInt(BigDecimal unitInt) {
        this.unitInt = unitInt;
    }

    /**
     * 取得 ????
     * @return ????
     */
    public BigDecimal getUnitFprice() {
        return unitFprice;
    }

    /**
     * 设置 ????
     * @param unitFprice ????
     */
    public void setUnitFprice(BigDecimal unitFprice) {
        this.unitFprice = unitFprice;
    }

    /**
     * 取得 ???????100???????????1
     * @return ???????100???????????1
     */
    public BigDecimal getParValue() {
        return parValue;
    }

    /**
     * 设置 ???????100???????????1
     * @param parValue ???????100???????????1
     */
    public void setParValue(BigDecimal parValue) {
        this.parValue = parValue;
    }

    /**
     * 取得 ????
     * @return ????
     */
    public BigDecimal getCpriceAmt() {
        return cpriceAmt;
    }

    /**
     * 设置 ????
     * @param cpriceAmt ????
     */
    public void setCpriceAmt(BigDecimal cpriceAmt) {
        this.cpriceAmt = cpriceAmt;
    }

    /**
     * 取得 ????
     * @return ????
     */
    public BigDecimal getShareAmt() {
        return shareAmt;
    }

    /**
     * 设置 ????
     * @param shareAmt ????
     */
    public void setShareAmt(BigDecimal shareAmt) {
        this.shareAmt = shareAmt;
    }

    /**
     * 取得 ????
     * @return ????
     */
    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    /**
     * 设置 ????
     * @param prinAmt ????
     */
    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    /**
     * 取得 ????
     * @return ????
     */
    public BigDecimal getIntAmt() {
        return intAmt;
    }

    /**
     * 设置 ????
     * @param intAmt ????
     */
    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    /**
     * 取得 ????
     * @return ????
     */
    public BigDecimal getFpriceAmt() {
        return fpriceAmt;
    }

    /**
     * 设置 ????
     * @param fpriceAmt ????
     */
    public void setFpriceAmt(BigDecimal fpriceAmt) {
        this.fpriceAmt = fpriceAmt;
    }

    /**
     * 取得 ???????
     * @return ???????
     */
    public BigDecimal getFeeAmtPay() {
        return feeAmtPay;
    }

    /**
     * 设置 ???????
     * @param feeAmtPay ???????
     */
    public void setFeeAmtPay(BigDecimal feeAmtPay) {
        this.feeAmtPay = feeAmtPay;
    }

    /**
     * 取得 ????
     * @return ????
     */
    public BigDecimal getTradeAmt() {
        return tradeAmt;
    }

    /**
     * 设置 ????
     * @param tradeAmt ????
     */
    public void setTradeAmt(BigDecimal tradeAmt) {
        this.tradeAmt = tradeAmt;
    }

    /**
     * 取得 ????????
     * @return ????????
     */
    public BigDecimal getFeeAmtUnpay() {
        return feeAmtUnpay;
    }

    /**
     * 设置 ????????
     * @param feeAmtUnpay ????????
     */
    public void setFeeAmtUnpay(BigDecimal feeAmtUnpay) {
        this.feeAmtUnpay = feeAmtUnpay;
    }

    /**
     * 取得 ?????
     * @return ?????
     */
    public BigDecimal getFeeAmt() {
        return feeAmt;
    }

    /**
     * 设置 ?????
     * @param feeAmt ?????
     */
    public void setFeeAmt(BigDecimal feeAmt) {
        this.feeAmt = feeAmt;
    }

    /**
     * 取得 ????
     * @return ????
     */
    public String getPs() {
        return ps;
    }

    /**
     * 设置 ????
     * @param ps ????
     */
    public void setPs(String ps) {
        this.ps = ps == null ? null : ps.trim();
    }

    /**
     * 取得 ?????
     * @return ?????
     */
    public BigDecimal getYield() {
        return yield;
    }

    /**
     * 设置 ?????
     * @param yield ?????
     */
    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }

    /**
     * 取得 ?????
     * @return ?????
     */
    public BigDecimal getExerYield() {
        return exerYield;
    }

    /**
     * 设置 ?????
     * @param exerYield ?????
     */
    public void setExerYield(BigDecimal exerYield) {
        this.exerYield = exerYield;
    }

    /**
     * 取得 ????
     * @return ????
     */
    public String getInvAim() {
        return invAim;
    }

    /**
     * 设置 ????
     * @param invAim ????
     */
    public void setInvAim(String invAim) {
        this.invAim = invAim == null ? null : invAim.trim();
    }

    /**
     * 取得 ?????????????
     * @return ?????????????
     */
    public String getTradeStatus() {
        return tradeStatus;
    }

    /**
     * 设置 ?????????????
     * @param tradeStatus ?????????????
     */
    public void setTradeStatus(String tradeStatus) {
        this.tradeStatus = tradeStatus == null ? null : tradeStatus.trim();
    }

    /**
     * 取得 ?????
     * @return ?????
     */
    public String getpTradeId() {
        return pTradeId;
    }

    /**
     * 设置 ?????
     * @param pTradeId ?????
     */
    public void setpTradeId(String pTradeId) {
        this.pTradeId = pTradeId == null ? null : pTradeId.trim();
    }

    /**
     * 取得 ??????????????????????????
     * @return ??????????????????????????
     */
    public String getIsCancel() {
        return isCancel;
    }

    /**
     * 设置 ??????????????????????????
     * @param isCancel ??????????????????????????
     */
    public void setIsCancel(String isCancel) {
        this.isCancel = isCancel == null ? null : isCancel.trim();
    }

    /**
     * 取得 ???????????
     * @return ???????????
     */
    public String getIsClean() {
        return isClean;
    }

    /**
     * 设置 ???????????
     * @param isClean ???????????
     */
    public void setIsClean(String isClean) {
        this.isClean = isClean == null ? null : isClean.trim();
    }

    /**
     * 取得 ????????????+??????+?????????????????????????????
     * @return ????????????+??????+?????????????????????????????
     */
    public String getCashId() {
        return cashId;
    }

    /**
     * 设置 ????????????+??????+?????????????????????????????
     * @param cashId ????????????+??????+?????????????????????????????
     */
    public void setCashId(String cashId) {
        this.cashId = cashId == null ? null : cashId.trim();
    }

    /**
     * 取得 ???????
     * @return ???????
     */
    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    /**
     * 设置 ???????
     * @param secManageAcctId ???????
     */
    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId == null ? null : secManageAcctId.trim();
    }

    /**
     * 取得 ????????????????????????????????????
     * @return ????????????????????????????????????
     */
    public String getbTradeId() {
        return bTradeId;
    }

    /**
     * 设置 ????????????????????????????????????
     * @param bTradeId ????????????????????????????????????
     */
    public void setbTradeId(String bTradeId) {
        this.bTradeId = bTradeId == null ? null : bTradeId.trim();
    }

    /**
     * 取得 ??/????
     * @return ??/????
     */
    public String getChlId() {
        return chlId;
    }

    /**
     * 设置 ??/????
     * @param chlId ??/????
     */
    public void setChlId(String chlId) {
        this.chlId = chlId == null ? null : chlId.trim();
    }

    /**
     * 取得 ????
     * @return ????
     */
    public String getCounterId() {
        return counterId;
    }

    /**
     * 设置 ????
     * @param counterId ????
     */
    public void setCounterId(String counterId) {
        this.counterId = counterId == null ? null : counterId.trim();
    }

    /**
     * 取得 ??????????
     * @return ??????????
     */
    public String getIsOutTrade() {
        return isOutTrade;
    }

    /**
     * 设置 ??????????
     * @param isOutTrade ??????????
     */
    public void setIsOutTrade(String isOutTrade) {
        this.isOutTrade = isOutTrade == null ? null : isOutTrade.trim();
    }

    /**
     * 取得 ????????????????????
     * @return ????????????????????
     */
    public String getTradeMarket() {
        return tradeMarket;
    }

    /**
     * 设置 ????????????????????
     * @param tradeMarket ????????????????????
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket == null ? null : tradeMarket.trim();
    }

    /**
     * 取得 ????????????????????????????????????????????????????
     * @return ????????????????????????????????????????????????????
     */
    public String getTradePlat() {
        return tradePlat;
    }

    /**
     * 设置 ????????????????????????????????????????????????????
     * @param tradePlat ????????????????????????????????????????????????????
     */
    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat == null ? null : tradePlat.trim();
    }

    /**
     * 取得 ??????????????
     * @return ??????????????
     */
    public String getTradeMarketMode() {
        return tradeMarketMode;
    }

    /**
     * 设置 ??????????????
     * @param tradeMarketMode ??????????????
     */
    public void setTradeMarketMode(String tradeMarketMode) {
        this.tradeMarketMode = tradeMarketMode == null ? null : tradeMarketMode.trim();
    }

    /**
     * 取得 ???
     * @return ???
     */
    public String getTrader() {
        return trader;
    }

    /**
     * 设置 ???
     * @param trader ???
     */
    public void setTrader(String trader) {
        this.trader = trader == null ? null : trader.trim();
    }

    /**
     * 取得 ??????
     * @return ??????
     */
    public String getCounterTrader() {
        return counterTrader;
    }

    /**
     * 设置 ??????
     * @param counterTrader ??????
     */
    public void setCounterTrader(String counterTrader) {
        this.counterTrader = counterTrader == null ? null : counterTrader.trim();
    }

    /**
     * 取得 ???????
     * @return ???????
     */
    public String getCounterContact() {
        return counterContact;
    }

    /**
     * 设置 ???????
     * @param counterContact ???????
     */
    public void setCounterContact(String counterContact) {
        this.counterContact = counterContact == null ? null : counterContact.trim();
    }

    /**
     * 取得 ????????
     * @return ????????
     */
    public String getfTradeId() {
        return fTradeId;
    }

    /**
     * 设置 ????????
     * @param fTradeId ????????
     */
    public void setfTradeId(String fTradeId) {
        this.fTradeId = fTradeId == null ? null : fTradeId.trim();
    }

    /**
     * 取得 ??????
     * @return ??????
     */
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * 设置 ??????
     * @param portfolioId ??????
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId == null ? null : portfolioId.trim();
    }

    /**
     * 取得 ??????
     * @return ??????
     */
    public String getProdId() {
        return prodId;
    }

    /**
     * 设置 ??????
     * @param prodId ??????
     */
    public void setProdId(String prodId) {
        this.prodId = prodId == null ? null : prodId.trim();
    }

    /**
     * 取得 ????????
     * @return ????????
     */
    public String getpTotleTradeId() {
        return pTotleTradeId;
    }

    /**
     * 设置 ????????
     * @param pTotleTradeId ????????
     */
    public void setpTotleTradeId(String pTotleTradeId) {
        this.pTotleTradeId = pTotleTradeId == null ? null : pTotleTradeId.trim();
    }

    /**
     * 取得 ??
     * @return ??
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置 ??
     * @param remark ??
     */
    public void setRemark(String remark) {
        this.remark = remark == null ? null : remark.trim();
    }

    /**
     * 取得 ?????
     * @return ?????
     */
    public String getWorkflowId() {
        return workflowId;
    }

    /**
     * 设置 ?????
     * @param workflowId ?????
     */
    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId == null ? null : workflowId.trim();
    }

    /**
     * 取得 剩余名义本金
     * @return 剩余名义本金
     */
    public BigDecimal getRemainPrin() {
        return remainPrin;
    }

    /**
     * 设置 剩余名义本金
     * @param remainPrin 剩余名义本金
     */
    public void setRemainPrin(BigDecimal remainPrin) {
        this.remainPrin = remainPrin;
    }

    /**
     * 取得 投管业务类型
     * @return 投管业务类型
     */
    public String getTgBusiType() {
        return tgBusiType;
    }

    /**
     * 设置 投管业务类型
     * @param tgBusiType 投管业务类型
     */
    public void setTgBusiType(String tgBusiType) {
        this.tgBusiType = tgBusiType == null ? null : tgBusiType.trim();
    }

    /**
     * 取得 清算速度
     * @return 清算速度
     */
    public Long getSettleSpeed() {
        return settleSpeed;
    }

    /**
     * 设置 清算速度
     * @param settleSpeed 清算速度
     */
    public void setSettleSpeed(Long settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    /**
     * 取得 清算类型
     * @return 清算类型
     */
    public String getClearType() {
        return clearType;
    }

    /**
     * 设置 清算类型
     * @param clearType 清算类型
     */
    public void setClearType(String clearType) {
        this.clearType = clearType == null ? null : clearType.trim();
    }

    /**
     * 取得 有效时间
     * @return 有效时间
     */
    public Date getEffTime() {
        return effTime;
    }

    /**
     * 设置 有效时间
     * @param effTime 有效时间
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setEffTime(Date effTime) {
        this.effTime = effTime;
    }

    /**
     * 取得 首期结算方式
     * @return 首期结算方式
     */
    public String getDeliveryType() {
        return deliveryType;
    }

    /**
     * 设置 首期结算方式
     * @param deliveryType 首期结算方式
     */
    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType == null ? null : deliveryType.trim();
    }

    /**
     * 取得 到期结算方式
     * @return 到期结算方式
     */
    public String getmDeliveryType() {
        return mDeliveryType;
    }

    /**
     * 设置 到期结算方式
     * @param mDeliveryType 到期结算方式
     */
    public void setmDeliveryType(String mDeliveryType) {
        this.mDeliveryType = mDeliveryType == null ? null : mDeliveryType.trim();
    }

    /**
     * 取得 利率
     * @return 利率
     */
    public BigDecimal getRate() {
        return rate;
    }

    /**
     * 设置 利率
     * @param rate 利率
     */
    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    /**
     * 取得 回购期限(天)
     * @return 回购期限(天)
     */
    public Long getRepoTerm() {
        return repoTerm;
    }

    /**
     * 设置 回购期限(天)
     * @param repoTerm 回购期限(天)
     */
    public void setRepoTerm(Long repoTerm) {
        this.repoTerm = repoTerm;
    }

    /**
     * 取得 期限品种
     * @return 期限品种
     */
    public String getTermType() {
        return termType;
    }

    /**
     * 设置 期限品种
     * @param termType 期限品种
     */
    public void setTermType(String termType) {
        this.termType = termType == null ? null : termType.trim();
    }

    /**
     * 取得 是否逾期交易
     * @return 是否逾期交易
     */
    public String getIsOverdueDeal() {
        return isOverdueDeal;
    }

    /**
     * 设置 是否逾期交易
     * @param isOverdueDeal 是否逾期交易
     */
    public void setIsOverdueDeal(String isOverdueDeal) {
        this.isOverdueDeal = isOverdueDeal == null ? null : isOverdueDeal.trim();
    }

    /**
     * 取得 支付未支付利息
     * @return 支付未支付利息
     */
    public BigDecimal getPayUnpaidintAmt() {
        return payUnpaidintAmt;
    }

    /**
     * 设置 支付未支付利息
     * @param payUnpaidintAmt 支付未支付利息
     */
    public void setPayUnpaidintAmt(BigDecimal payUnpaidintAmt) {
        this.payUnpaidintAmt = payUnpaidintAmt;
    }

    /**
     * 取得 是否转让还本未支付利息
     * @return 是否转让还本未支付利息
     */
    public String getIspayUnpaidint() {
        return ispayUnpaidint;
    }

    /**
     * 设置 是否转让还本未支付利息
     * @param ispayUnpaidint 是否转让还本未支付利息
     */
    public void setIspayUnpaidint(String ispayUnpaidint) {
        this.ispayUnpaidint = ispayUnpaidint == null ? null : ispayUnpaidint.trim();
    }

    // 多级法人改造 add by caiqun 20200615
    /**
     * 机构代码
     * @return
     */
    public String getInstitutionCode() {
        return institutionCode;
    }

    /**
     * 机构代码
     * @param institutionCode
     */
    public void setInstitutionCode(String institutionCode) {
        this.institutionCode = institutionCode;
    }
    // 多级法人改造 add by caiqun 20200615

    public BigDecimal getYesterdayPosition() {
        return yesterdayPosition;
    }

    public void setYesterdayPosition(BigDecimal yesterdayPosition) {
        this.yesterdayPosition = yesterdayPosition;
    }

    public BigDecimal getTradeAmtHk() {
        return tradeAmtHk;
    }

    public void setTradeAmtHk(BigDecimal tradeAmtHk) {
        this.tradeAmtHk = tradeAmtHk;
    }

    public BigDecimal getSellSettleRate() {
        return sellSettleRate;
    }

	public void setSellSettleRate(BigDecimal sellSettleRate) {
		this.sellSettleRate = sellSettleRate;
	}

    /**
     * 是否含股利选择权
     */
    private String ifDividend;

    /**
     * 派息币种
     */
    private String dividCcy;

    /**
     * 汇率(派息等)
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal exchangeRate;

    /**
     * 以股代息价格
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal replacePrice;

    /**
     * 取得 是否含股利选择权
     * @return 是否含股利选择权
     */
    public String getIfDividend() {
        return ifDividend;
    }

    /**
     * 设置 是否含股利选择权
     * @param ifDividend 是否含股利选择权
     */
    public void setIfDividend(String ifDividend) {
        this.ifDividend = ifDividend == null ? null : ifDividend.trim();
    }

    /**
     * 取得 派息币种
     * @return 派息币种
     */
    public String getDividCcy() {
        return dividCcy;
    }

    /**
     * 设置 派息币种
     * @param dividCcy 派息币种
     */
    public void setDividCcy(String dividCcy) {
        this.dividCcy = dividCcy == null ? null : dividCcy.trim();
    }

    /**
     * 取得 汇率(派息等)
     * @return 汇率(派息等)
     */
    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    /**
     * 设置 汇率(派息等)
     * @param exchangeRate 汇率(派息等)
     */
    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    /**
     * 取得 以股代息价格
     * @return 以股代息价格
     */
    public BigDecimal getReplacePrice() {
        return replacePrice;
    }

    /**
     * 设置 以股代息价格
     * @param replacePrice 以股代息价格
     */
    public void setReplacePrice(BigDecimal replacePrice) {
        this.replacePrice = replacePrice;
    }

}