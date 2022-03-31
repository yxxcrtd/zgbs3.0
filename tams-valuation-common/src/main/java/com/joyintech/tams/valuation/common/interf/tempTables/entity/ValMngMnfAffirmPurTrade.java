package com.joyintech.tams.valuation.common.interf.tempTables.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class ValMngMnfAffirmPurTrade extends BaseEntity {
    /**
     * 产品代码
     */
    private String prodId;

    /**
     * 数据动作标志
     */
    private String dataActionSign;

    /**
     * 交易编号
     */
    private String tradeId;

    /**
     * 资产代码
     */
    private String assetId;

    /**
     * 资产名称
     */
    private String assetName;

    /**
     * 交易场所
     */
    private String tradeMarket;

    /**
     * 交易日
     */
    private Date tradeDate;

    /**
     * 退款金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal refundAmt;

    /**
     * 确认金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal cfmAmt;

    /**
     * 确认份额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal cfmShare;

    /**
     * 币种
     */
    private String ccy;

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
     * 费用
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal fee;

    /*add by sunshuaishuai 20220325 交易管理 start*/
    /**
     * 单位净值
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal unitCprice;

    /**
     * 外部成交编号
     */
    private String outTradeId;

    /**
     * 资金账户
     */
    private String myAcctNo;
    /*add by sunshuaishuai 20220325 end*/

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
     * 取得 资产名称
     * @return 资产名称
     */
    public String getAssetName() {
        return assetName;
    }

    /**
     * 设置 资产名称
     * @param assetName 资产名称
     */
    public void setAssetName(String assetName) {
        this.assetName = assetName == null ? null : assetName.trim();
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
     * 取得 退款金额
     * @return 退款金额
     */
    public BigDecimal getRefundAmt() {
        return refundAmt;
    }

    /**
     * 设置 退款金额
     * @param refundAmt 退款金额
     */
    public void setRefundAmt(BigDecimal refundAmt) {
        this.refundAmt = refundAmt;
    }

    /**
     * 取得 确认金额
     * @return 确认金额
     */
    public BigDecimal getCfmAmt() {
        return cfmAmt;
    }

    /**
     * 设置 确认金额
     * @param cfmAmt 确认金额
     */
    public void setCfmAmt(BigDecimal cfmAmt) {
        this.cfmAmt = cfmAmt;
    }

    /**
     * 取得 确认份额
     * @return 确认份额
     */
    public BigDecimal getCfmShare() {
        return cfmShare;
    }

    /**
     * 设置 确认份额
     * @param cfmShare 确认份额
     */
    public void setCfmShare(BigDecimal cfmShare) {
        this.cfmShare = cfmShare;
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
     * 取得 费用
     * @return 费用
     */
    public BigDecimal getFee() {
        return fee;
    }

    /**
     * 设置 费用
     * @param fee
     */
    public void setFee(BigDecimal fee) {
        this.fee = fee;
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

    /*add by sunshuaishuai 20220325 交易管理 start*/
    /**
     * 取得 单位净值
     * @return 单位净值
     */
    public BigDecimal getUnitCprice() {
        return unitCprice;
    }

    /**
     * 设置 单位净值
     * @param unitCprice 单位净值
     */
    public void setUnitCprice(BigDecimal unitCprice) {
        this.unitCprice = unitCprice;
    }

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

    /**
     * 取得 资金账户
     * @return 资金账户
     */
    public String getMyAcctNo() {
        return myAcctNo;
    }

    /**
     * 设置 资金账户
     * @param myAcctNo 资金账户
     */
    public void setMyAcctNo(String myAcctNo) {
        this.myAcctNo = myAcctNo;
    }

    /*add by sunshuaishuai 20220325 end*/
}