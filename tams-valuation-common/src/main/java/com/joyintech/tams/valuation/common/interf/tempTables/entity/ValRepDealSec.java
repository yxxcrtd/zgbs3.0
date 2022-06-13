package com.joyintech.tams.valuation.common.interf.tempTables.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class ValRepDealSec extends BaseEntity {
    /**
     * 产品代码
     */
    private String prodId;

    /**
     * 新增、修改、删除
     */
    private String dataActionSign;

    /**
     * 交易编号
     */
    private String tradeId;

    /**
     * 期限品种
     */
    private String termKind;

    /**
     * 交易场所
     */
    private String tradeMarket;

    /**
     * 业务类型
     */
    private String busiType;

    /**
     * 交易方向
     */
    private String ps;

    /**
     * 交易员
     */
    private String trader;

    /**
     * 通道代码
     */
    private String chlId;

    /**
     * 成交日
     */
    private Date tradeDate;

    /**
     * 默认=交易日
     */
    private Date settleDate;

    /**
     * 起息日
     */
    private Date vdate;

    /**
     * 到期日
     */
    private Date mdate;

    /**
     * 首次净价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal cprice;

    /**
     * 首次全价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal fprice;

    /**
     * 到期净价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mcprice;

    /**
     * 到期全价
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mfprice;

    /**
     * 首次结算金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal firstAmt;

    /**
     * 到期结算金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal endAmt;

    /**
     * 利息金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal intAmt;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 本金收益特征
     */
    private String capiProfFeature;

    /**
     * 交易对手
     */
    private String counterId;

    /**
     * 交易对手类型
     */
    private String counterType;

    /**
     * 登记托管机构
     */
    private String registOrg;

    /**
     * 交易费用
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tradeFee;

    /**
     * 交易费用是否随交易支付
     */
    private String isTradeFee;

    /**
     * 印花税
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal stampDuty;

    /**
     * 印花税是否随交易支付
     */
    private String isStampDuty;

    /**
     * 经手费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal brokerage;

    /**
     * 经手费是否随交易支付
     */
    private String isBrokerage;

    /**
     * 证管费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal certfFee;

    /**
     * 证管费是否随交易支付
     */
    private String isCertfFee;

    /**
     * 结算费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal stlAmt;

    /**
     * 结算费是否随交易支付
     */
    private String isStlAmt;

    /**
     * 佣金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal commission;

    /**
     * 佣金是否随交易支付
     */
    private String isCommission;

    /**
     * 交易手续费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal dealFeeAmt;

    /**
     * 交易手续费是否随交易支付
     */
    private String isDealFeeAmt;

    /**
     * 结算手续费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal stlFeeAmt;

    /**
     * 结算手续费是否随交易支付
     */
    private String isStlFeeAmt;

    /**
     * 通道费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal chlFeeAmt;

    /**
     * 通道费是否随交易支付
     */
    private String isChlFeeAmt;

    /**
     * 原始交易编号
     */
    private String pTradeId;

    /**
     * 有无清算指令
     */
    private String isClean;

    /**
     * 交收日
     */
    private Date paydate;

    /**
     * 付款账户
     */
    private String myAcctNo;

    /**
     * 收款账户
     */
    private String counterAcctNo;

    /**
     * 资金交收日
     */
    private Date fundSettleDate;

    /**
     * 取得 产品代码
     * @return 产品代码
     */
    public String getProdId() {
        return prodId;
    }

    /**
     * 设置 产品代码
     * @param prodId 产品代码
     */
    public void setProdId(String prodId) {
        this.prodId = prodId == null ? null : prodId.trim();
    }

    /**
     * 取得 新增、修改、删除
     * @return 新增、修改、删除
     */
    public String getDataActionSign() {
        return dataActionSign;
    }

    /**
     * 设置 新增、修改、删除
     * @param dataActionSign 新增、修改、删除
     */
    public void setDataActionSign(String dataActionSign) {
        this.dataActionSign = dataActionSign == null ? null : dataActionSign.trim();
    }

    /**
     * 取得 交易编号
     * @return 交易编号
     */
    public String getTradeId() {
        return tradeId;
    }

    /**
     * 设置 交易编号
     * @param tradeId 交易编号
     */
    public void setTradeId(String tradeId) {
        this.tradeId = tradeId == null ? null : tradeId.trim();
    }

    /**
     * 取得 期限品种
     * @return 期限品种
     */
    public String getTermKind() {
        return termKind;
    }

    /**
     * 设置 期限品种
     * @param termKind 期限品种
     */
    public void setTermKind(String termKind) {
        this.termKind = termKind == null ? null : termKind.trim();
    }

    /**
     * 取得 交易场所
     * @return 交易场所
     */
    public String getTradeMarket() {
        return tradeMarket;
    }

    /**
     * 设置 交易场所
     * @param tradeMarket 交易场所
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket == null ? null : tradeMarket.trim();
    }

    /**
     * 取得 业务类型
     * @return 业务类型
     */
    public String getBusiType() {
        return busiType;
    }

    /**
     * 设置 业务类型
     * @param busiType 业务类型
     */
    public void setBusiType(String busiType) {
        this.busiType = busiType == null ? null : busiType.trim();
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
     * 取得 交易员
     * @return 交易员
     */
    public String getTrader() {
        return trader;
    }

    /**
     * 设置 交易员
     * @param trader 交易员
     */
    public void setTrader(String trader) {
        this.trader = trader == null ? null : trader.trim();
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
     * 取得 成交日
     * @return 成交日
     */
    public Date getTradeDate() {
        return tradeDate;
    }

    /**
     * 设置 成交日
     * @param tradeDate 成交日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    /**
     * 取得 默认=交易日
     * @return 默认=交易日
     */
    public Date getSettleDate() {
        return settleDate;
    }

    /**
     * 设置 默认=交易日
     * @param settleDate 默认=交易日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setSettleDate(Date settleDate) {
        this.settleDate = settleDate;
    }

    /**
     * 取得 起息日
     * @return 起息日
     */
    public Date getVdate() {
        return vdate;
    }

    /**
     * 设置 起息日
     * @param vdate 起息日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    /**
     * 取得 到期日
     * @return 到期日
     */
    public Date getMdate() {
        return mdate;
    }

    /**
     * 设置 到期日
     * @param mdate 到期日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    /**
     * 取得 首次净价
     * @return 首次净价
     */
    public BigDecimal getCprice() {
        return cprice;
    }

    /**
     * 设置 首次净价
     * @param cprice 首次净价
     */
    public void setCprice(BigDecimal cprice) {
        this.cprice = cprice;
    }

    /**
     * 取得 首次全价
     * @return 首次全价
     */
    public BigDecimal getFprice() {
        return fprice;
    }

    /**
     * 设置 首次全价
     * @param fprice 首次全价
     */
    public void setFprice(BigDecimal fprice) {
        this.fprice = fprice;
    }

    /**
     * 取得 到期净价
     * @return 到期净价
     */
    public BigDecimal getMcprice() {
        return mcprice;
    }

    /**
     * 设置 到期净价
     * @param mcprice 到期净价
     */
    public void setMcprice(BigDecimal mcprice) {
        this.mcprice = mcprice;
    }

    /**
     * 取得 到期全价
     * @return 到期全价
     */
    public BigDecimal getMfprice() {
        return mfprice;
    }

    /**
     * 设置 到期全价
     * @param mfprice 到期全价
     */
    public void setMfprice(BigDecimal mfprice) {
        this.mfprice = mfprice;
    }

    /**
     * 取得 首次结算金额
     * @return 首次结算金额
     */
    public BigDecimal getFirstAmt() {
        return firstAmt;
    }

    /**
     * 设置 首次结算金额
     * @param firstAmt 首次结算金额
     */
    public void setFirstAmt(BigDecimal firstAmt) {
        this.firstAmt = firstAmt;
    }

    /**
     * 取得 到期结算金额
     * @return 到期结算金额
     */
    public BigDecimal getEndAmt() {
        return endAmt;
    }

    /**
     * 设置 到期结算金额
     * @param endAmt 到期结算金额
     */
    public void setEndAmt(BigDecimal endAmt) {
        this.endAmt = endAmt;
    }

    /**
     * 取得 利息金额
     * @return 利息金额
     */
    public BigDecimal getIntAmt() {
        return intAmt;
    }

    /**
     * 设置 利息金额
     * @param intAmt 利息金额
     */
    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
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
     * 取得 本金收益特征
     * @return 本金收益特征
     */
    public String getCapiProfFeature() {
        return capiProfFeature;
    }

    /**
     * 设置 本金收益特征
     * @param capiProfFeature 本金收益特征
     */
    public void setCapiProfFeature(String capiProfFeature) {
        this.capiProfFeature = capiProfFeature == null ? null : capiProfFeature.trim();
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
     * 取得 交易对手类型
     * @return 交易对手类型
     */
    public String getCounterType() {
        return counterType;
    }

    /**
     * 设置 交易对手类型
     * @param counterType 交易对手类型
     */
    public void setCounterType(String counterType) {
        this.counterType = counterType == null ? null : counterType.trim();
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
     * 取得 交易费用
     * @return 交易费用
     */
    public BigDecimal getTradeFee() {
        return tradeFee;
    }

    /**
     * 设置 交易费用
     * @param tradeFee 交易费用
     */
    public void setTradeFee(BigDecimal tradeFee) {
        this.tradeFee = tradeFee;
    }

    /**
     * 取得 交易费用是否随交易支付
     * @return 交易费用是否随交易支付
     */
    public String getIsTradeFee() {
        return isTradeFee;
    }

    /**
     * 设置 交易费用是否随交易支付
     * @param isTradeFee 交易费用是否随交易支付
     */
    public void setIsTradeFee(String isTradeFee) {
        this.isTradeFee = isTradeFee == null ? null : isTradeFee.trim();
    }

    /**
     * 取得 印花税
     * @return 印花税
     */
    public BigDecimal getStampDuty() {
        return stampDuty;
    }

    /**
     * 设置 印花税
     * @param stampDuty 印花税
     */
    public void setStampDuty(BigDecimal stampDuty) {
        this.stampDuty = stampDuty;
    }

    /**
     * 取得 印花税是否随交易支付
     * @return 印花税是否随交易支付
     */
    public String getIsStampDuty() {
        return isStampDuty;
    }

    /**
     * 设置 印花税是否随交易支付
     * @param isStampDuty 印花税是否随交易支付
     */
    public void setIsStampDuty(String isStampDuty) {
        this.isStampDuty = isStampDuty == null ? null : isStampDuty.trim();
    }

    /**
     * 取得 经手费
     * @return 经手费
     */
    public BigDecimal getBrokerage() {
        return brokerage;
    }

    /**
     * 设置 经手费
     * @param brokerage 经手费
     */
    public void setBrokerage(BigDecimal brokerage) {
        this.brokerage = brokerage;
    }

    /**
     * 取得 经手费是否随交易支付
     * @return 经手费是否随交易支付
     */
    public String getIsBrokerage() {
        return isBrokerage;
    }

    /**
     * 设置 经手费是否随交易支付
     * @param isBrokerage 经手费是否随交易支付
     */
    public void setIsBrokerage(String isBrokerage) {
        this.isBrokerage = isBrokerage == null ? null : isBrokerage.trim();
    }

    /**
     * 取得 证管费
     * @return 证管费
     */
    public BigDecimal getCertfFee() {
        return certfFee;
    }

    /**
     * 设置 证管费
     * @param certfFee 证管费
     */
    public void setCertfFee(BigDecimal certfFee) {
        this.certfFee = certfFee;
    }

    /**
     * 取得 证管费是否随交易支付
     * @return 证管费是否随交易支付
     */
    public String getIsCertfFee() {
        return isCertfFee;
    }

    /**
     * 设置 证管费是否随交易支付
     * @param isCertfFee 证管费是否随交易支付
     */
    public void setIsCertfFee(String isCertfFee) {
        this.isCertfFee = isCertfFee == null ? null : isCertfFee.trim();
    }

    /**
     * 取得 结算费
     * @return 结算费
     */
    public BigDecimal getStlAmt() {
        return stlAmt;
    }

    /**
     * 设置 结算费
     * @param stlAmt 结算费
     */
    public void setStlAmt(BigDecimal stlAmt) {
        this.stlAmt = stlAmt;
    }

    /**
     * 取得 结算费是否随交易支付
     * @return 结算费是否随交易支付
     */
    public String getIsStlAmt() {
        return isStlAmt;
    }

    /**
     * 设置 结算费是否随交易支付
     * @param isStlAmt 结算费是否随交易支付
     */
    public void setIsStlAmt(String isStlAmt) {
        this.isStlAmt = isStlAmt == null ? null : isStlAmt.trim();
    }

    /**
     * 取得 佣金
     * @return 佣金
     */
    public BigDecimal getCommission() {
        return commission;
    }

    /**
     * 设置 佣金
     * @param commission 佣金
     */
    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    /**
     * 取得 佣金是否随交易支付
     * @return 佣金是否随交易支付
     */
    public String getIsCommission() {
        return isCommission;
    }

    /**
     * 设置 佣金是否随交易支付
     * @param isCommission 佣金是否随交易支付
     */
    public void setIsCommission(String isCommission) {
        this.isCommission = isCommission == null ? null : isCommission.trim();
    }

    /**
     * 取得 交易手续费
     * @return 交易手续费
     */
    public BigDecimal getDealFeeAmt() {
        return dealFeeAmt;
    }

    /**
     * 设置 交易手续费
     * @param dealFeeAmt 交易手续费
     */
    public void setDealFeeAmt(BigDecimal dealFeeAmt) {
        this.dealFeeAmt = dealFeeAmt;
    }

    /**
     * 取得 交易手续费是否随交易支付
     * @return 交易手续费是否随交易支付
     */
    public String getIsDealFeeAmt() {
        return isDealFeeAmt;
    }

    /**
     * 设置 交易手续费是否随交易支付
     * @param isDealFeeAmt 交易手续费是否随交易支付
     */
    public void setIsDealFeeAmt(String isDealFeeAmt) {
        this.isDealFeeAmt = isDealFeeAmt == null ? null : isDealFeeAmt.trim();
    }

    /**
     * 取得 结算手续费
     * @return 结算手续费
     */
    public BigDecimal getStlFeeAmt() {
        return stlFeeAmt;
    }

    /**
     * 设置 结算手续费
     * @param stlFeeAmt 结算手续费
     */
    public void setStlFeeAmt(BigDecimal stlFeeAmt) {
        this.stlFeeAmt = stlFeeAmt;
    }

    /**
     * 取得 结算手续费是否随交易支付
     * @return 结算手续费是否随交易支付
     */
    public String getIsStlFeeAmt() {
        return isStlFeeAmt;
    }

    /**
     * 设置 结算手续费是否随交易支付
     * @param isStlFeeAmt 结算手续费是否随交易支付
     */
    public void setIsStlFeeAmt(String isStlFeeAmt) {
        this.isStlFeeAmt = isStlFeeAmt == null ? null : isStlFeeAmt.trim();
    }

    /**
     * 取得 通道费
     * @return 通道费
     */
    public BigDecimal getChlFeeAmt() {
        return chlFeeAmt;
    }

    /**
     * 设置 通道费
     * @param chlFeeAmt 通道费
     */
    public void setChlFeeAmt(BigDecimal chlFeeAmt) {
        this.chlFeeAmt = chlFeeAmt;
    }

    /**
     * 取得 通道费是否随交易支付
     * @return 通道费是否随交易支付
     */
    public String getIsChlFeeAmt() {
        return isChlFeeAmt;
    }

    /**
     * 设置 通道费是否随交易支付
     * @param isChlFeeAmt 通道费是否随交易支付
     */
    public void setIsChlFeeAmt(String isChlFeeAmt) {
        this.isChlFeeAmt = isChlFeeAmt == null ? null : isChlFeeAmt.trim();
    }

    /**
     * 取得 原始交易编号
     * @return 原始交易编号
     */
    public String getpTradeId() {
        return pTradeId;
    }

    /**
     * 设置 原始交易编号
     * @param pTradeId 原始交易编号
     */
    public void setpTradeId(String pTradeId) {
        this.pTradeId = pTradeId == null ? null : pTradeId.trim();
    }

    /**
     * 取得 有无清算指令
     * @return 有无清算指令
     */
    public String getIsClean() {
        return isClean;
    }

    /**
     * 设置 有无清算指令
     * @param isClean 有无清算指令
     */
    public void setIsClean(String isClean) {
        this.isClean = isClean == null ? null : isClean.trim();
    }

    /**
     * 取得 交收日
     * @return 交收日
     */
    public Date getPaydate() {
        return paydate;
    }

    /**
     * 设置 交收日
     * @param paydate 交收日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setPaydate(Date paydate) {
        this.paydate = paydate;
    }

    /**
     * 取得 付款账户
     * @return 付款账户
     */
    public String getMyAcctNo() {
        return myAcctNo;
    }

    /**
     * 设置 付款账户
     * @param myAcctNo 付款账户
     */
    public void setMyAcctNo(String myAcctNo) {
        this.myAcctNo = myAcctNo == null ? null : myAcctNo.trim();
    }

    /**
     * 取得 收款账户
     * @return 收款账户
     */
    public String getCounterAcctNo() {
        return counterAcctNo;
    }

    /**
     * 设置 收款账户
     * @param counterAcctNo 收款账户
     */
    public void setCounterAcctNo(String counterAcctNo) {
        this.counterAcctNo = counterAcctNo == null ? null : counterAcctNo.trim();
    }

    /**
     * 取得 资金交收日
     * @return 资金交收日
     */
    public Date getFundSettleDate() {
        return fundSettleDate;
    }

    /**
     * 设置 资金交收日
     * @param fundSettleDate 资金交收日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setFundSettleDate(Date fundSettleDate) {
        this.fundSettleDate = fundSettleDate;
    }

    /**
     * 关联系统交易编号  外部交易编号
     */
    private String fTradeId;

    public String getfTradeId() {
        return fTradeId;
    }

    public void setfTradeId(String fTradeId) {
        this.fTradeId = fTradeId;
    }
}