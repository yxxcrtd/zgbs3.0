package com.joyintech.tams.valuation.common.interf.tempTables.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class ValBotAstTrade extends BaseEntity {
    /**
     * 产品代码
     */
    private String prodId;

    /**
     * 资产代码
     */
    private String assetId;

    /**
     * 新增、修改、删除
     */
    private String dataActionSign;

    /**
     * 交易日
     */
    private Date dealDate;

    /**
     * 交割日
     */
    private Date settDate;

    /**
     * 交易方向
     */
    private String ps;

    /**
     * 交易本金
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tradeCapi;

    /**
     * 应收利息
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal intAmt;

    /**
     * 交易费用
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal trdFee;

    /**
     * 全价总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal dealAmt;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 交易编号
     */
    private String tradeId;

    /**
     * 交易对手
     */
    private String counterId;

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
     * 取得 资产代码
     * @return 资产代码
     */
    public String getAssetId() {
        return assetId;
    }

    /**
     * 设置 资产代码
     * @param assetId 资产代码
     */
    public void setAssetId(String assetId) {
        this.assetId = assetId == null ? null : assetId.trim();
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
     * 取得 交易日
     * @return 交易日
     */
    public Date getDealDate() {
        return dealDate;
    }

    /**
     * 设置 交易日
     * @param dealDate 交易日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setDealDate(Date dealDate) {
        this.dealDate = dealDate;
    }

    /**
     * 取得 交割日
     * @return 交割日
     */
    public Date getSettDate() {
        return settDate;
    }

    /**
     * 设置 交割日
     * @param settDate 交割日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setSettDate(Date settDate) {
        this.settDate = settDate;
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
     * 取得 交易本金
     * @return 交易本金
     */
    public BigDecimal getTradeCapi() {
        return tradeCapi;
    }

    /**
     * 设置 交易本金
     * @param tradeCapi 交易本金
     */
    public void setTradeCapi(BigDecimal tradeCapi) {
        this.tradeCapi = tradeCapi;
    }

    /**
     * 取得 应收利息
     * @return 应收利息
     */
    public BigDecimal getIntAmt() {
        return intAmt;
    }

    /**
     * 设置 应收利息
     * @param intAmt 应收利息
     */
    public void setIntAmt(BigDecimal intAmt) {
        this.intAmt = intAmt;
    }

    /**
     * 取得 交易费用
     * @return 交易费用
     */
    public BigDecimal getTrdFee() {
        return trdFee;
    }

    /**
     * 设置 交易费用
     * @param trdFee 交易费用
     */
    public void setTrdFee(BigDecimal trdFee) {
        this.trdFee = trdFee;
    }

    /**
     * 取得 全价总额
     * @return 全价总额
     */
    public BigDecimal getDealAmt() {
        return dealAmt;
    }

    /**
     * 设置 全价总额
     * @param dealAmt 全价总额
     */
    public void setDealAmt(BigDecimal dealAmt) {
        this.dealAmt = dealAmt;
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
     *资金账户
     */
    private String myAcctNo;

    public String getMyAcctNo() {
        return myAcctNo;
    }

    public void setMyAcctNo(String myAcctNo) {
        this.myAcctNo = myAcctNo;
    }
}