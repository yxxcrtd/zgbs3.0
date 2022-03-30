package com.joyintech.tams.valuation.common.interf.tempTables.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class ValSeclendDeal extends BaseEntity {
    /**
     * 成交编号
     */
    private String tradeId;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 金融产品类型
     */
    private String finprodType;

    /**
     * 产品代码
     */
    private String prodId;

    /**
     * 数据动作标志
     */
    private String dataActionSign;

    /**
     * 交易日
     */
    private Date tradeDate;

    /**
     * 起息日
     */
    private Date vdate;

    /**
     * 到期日
     */
    private Date mdate;

    /**
     * 借贷期限
     */
    private Long term;

    /**
     * 交易方向
     */
    private String ps;

    /**
     * 计息基础
     */
    private String basis;

    /**
     * 交易对手
     */
    private String counterId;

    /**
     * 交易对手类型
     */
    private String counterType;

    /**
     * 费率
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal feeRate;

    /**
     * 借贷费用
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal fee;

    /**
     * 交易场所
     */
    private String tradeMarket;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 标的债券代码
     */
    private String secId;

    /**
     * 标的券面总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal prinAmt;

    /**
     * 质押券面总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal mPrinAmt;

    /**
     * 有无清算指令
     */
    private String isClean;

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
     * 通道代码
     */
    private String channelId;

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
     * 登记结算机构
     */
    private String registAgency;

    /**
     * 结算手续费
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal stlFeeAmt;

    /**
     * 结算手续费是否随交易支付
     */
    private String isStlFeeAmt;

    /*add by sunshuaishuai 20220325 交易管理 start*/
    /**
     * 外部成交编号
     */
    private String outTradeId;
    /*add by sunshuaishuai 20220325 end*/

    /**
     * 取得 成交编号
     * @return 成交编号
     */
    public String getTradeId() {
        return tradeId;
    }

    /**
     * 设置 成交编号
     * @param tradeId 成交编号
     */
    public void setTradeId(String tradeId) {
        this.tradeId = tradeId == null ? null : tradeId.trim();
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
     * 取得 金融产品类型
     * @return 金融产品类型
     */
    public String getFinprodType() {
        return finprodType;
    }

    /**
     * 设置 金融产品类型
     * @param finprodType 金融产品类型
     */
    public void setFinprodType(String finprodType) {
        this.finprodType = finprodType == null ? null : finprodType.trim();
    }

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
     * 取得 数据动作标志
     * @return 数据动作标志
     */
    public String getDataActionSign() {
        return dataActionSign;
    }

    /**
     * 设置 数据动作标志
     * @param dataActionSign 数据动作标志
     */
    public void setDataActionSign(String dataActionSign) {
        this.dataActionSign = dataActionSign == null ? null : dataActionSign.trim();
    }

    /**
     * 取得 交易日
     * @return 交易日
     */
    public Date getTradeDate() {
        return tradeDate;
    }

    /**
     * 设置 交易日
     * @param tradeDate 交易日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
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
     * 取得 借贷期限
     * @return 借贷期限
     */
    public Long getTerm() {
        return term;
    }

    /**
     * 设置 借贷期限
     * @param term 借贷期限
     */
    public void setTerm(Long term) {
        this.term = term;
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
     * 取得 计息基础
     * @return 计息基础
     */
    public String getBasis() {
        return basis;
    }

    /**
     * 设置 计息基础
     * @param basis 计息基础
     */
    public void setBasis(String basis) {
        this.basis = basis == null ? null : basis.trim();
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
     * 取得 费率
     * @return 费率
     */
    public BigDecimal getFeeRate() {
        return feeRate;
    }

    /**
     * 设置 费率
     * @param feeRate 费率
     */
    public void setFeeRate(BigDecimal feeRate) {
        this.feeRate = feeRate;
    }

    /**
     * 取得 借贷费用
     * @return 借贷费用
     */
    public BigDecimal getFee() {
        return fee;
    }

    /**
     * 设置 借贷费用
     * @param fee 借贷费用
     */
    public void setFee(BigDecimal fee) {
        this.fee = fee;
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
     * 取得 标的债券代码
     * @return 标的债券代码
     */
    public String getSecId() {
        return secId;
    }

    /**
     * 设置 标的债券代码
     * @param secId 标的债券代码
     */
    public void setSecId(String secId) {
        this.secId = secId == null ? null : secId.trim();
    }

    /**
     * 取得 标的券面总额
     * @return 标的券面总额
     */
    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    /**
     * 设置 标的券面总额
     * @param prinAmt 标的券面总额
     */
    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    /**
     * 取得 质押券面总额
     * @return 质押券面总额
     */
    public BigDecimal getmPrinAmt() {
        return mPrinAmt;
    }

    /**
     * 设置 质押券面总额
     * @param mPrinAmt 质押券面总额
     */
    public void setmPrinAmt(BigDecimal mPrinAmt) {
        this.mPrinAmt = mPrinAmt;
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
     * 取得 通道代码
     * @return 通道代码
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * 设置 通道代码
     * @param channelId 通道代码
     */
    public void setChannelId(String channelId) {
        this.channelId = channelId == null ? null : channelId.trim();
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
     * 取得 登记结算机构
     * @return 登记结算机构
     */
    public String getRegistAgency() {
        return registAgency;
    }

    /**
     * 设置 登记结算机构
     * @param registAgency 登记结算机构
     */
    public void setRegistAgency(String registAgency) {
        this.registAgency = registAgency;
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
        this.isStlFeeAmt = isStlFeeAmt;
    }

    /*add by sunshuaishuai 20220325 交易管理 start*/
    /**
     * 取得 外部成交编号
     * @return 外部成交编号
     */
    public String getOutTradeId() {
        return outTradeId;
    }

    /**
     * 设置 外部成交编号
     * @param outTradeId 外部成交编号
     */
    public void setOutTradeId(String outTradeId) {
        this.outTradeId = outTradeId == null ? null : outTradeId.trim();
    }
    /*add by sunshuaishuai 20220325 end*/
}