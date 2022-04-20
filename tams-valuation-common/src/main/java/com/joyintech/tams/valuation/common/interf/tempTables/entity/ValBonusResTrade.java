package com.joyintech.tams.valuation.common.interf.tempTables.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.math.BigDecimal;
import java.util.Date;

public class ValBonusResTrade extends BaseEntity {
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
     * 基金类型
     */
    private String fundType;

    /**
     * 资产代码
     */
    private String assetId;

    /**
     * 资产名称
     */
    private String assetName;

    /**
     * 红利再投日
     */
    private Date bonusDate;

    /**
     * 红利再投金额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal bonusAmt;

    /**
     * 红利再投净值
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal bonusNet;

    /**
     * 红利再投份额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal bonusShare;

    /**
     * 有无清算指令
     */
    private String isClean;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 分红截止日
     */
    private Date mdate;

    /**
     * 通道代码
     */
    private String channelId;

    /*add by sunshuaishuai 交易管理 20220323 start*/
    /**
     * 交易场所
     */
    private String tradeMarket;
    /*add by sunshuaishuai 交易管理 20220323 end*/

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
     * 取得 基金类型
     * @return 基金类型
     */
    public String getFundType() {
        return fundType;
    }

    /**
     * 设置 基金类型
     * @param fundType 基金类型
     */
    public void setFundType(String fundType) {
        this.fundType = fundType == null ? null : fundType.trim();
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
     * 取得 红利再投日
     * @return 红利再投日
     */
    public Date getBonusDate() {
        return bonusDate;
    }

    /**
     * 设置 红利再投日
     * @param bonusDate 红利再投日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setBonusDate(Date bonusDate) {
        this.bonusDate = bonusDate;
    }

    /**
     * 取得 红利再投金额
     * @return 红利再投金额
     */
    public BigDecimal getBonusAmt() {
        return bonusAmt;
    }

    /**
     * 设置 红利再投金额
     * @param bonusAmt 红利再投金额
     */
    public void setBonusAmt(BigDecimal bonusAmt) {
        this.bonusAmt = bonusAmt;
    }

    /**
     * 取得 红利再投净值
     * @return 红利再投净值
     */
    public BigDecimal getBonusNet() {
        return bonusNet;
    }

    /**
     * 设置 红利再投净值
     * @param bonusNet 红利再投净值
     */
    public void setBonusNet(BigDecimal bonusNet) {
        this.bonusNet = bonusNet;
    }

    /**
     * 取得 红利再投份额
     * @return 红利再投份额
     */
    public BigDecimal getBonusShare() {
        return bonusShare;
    }

    /**
     * 设置 红利再投份额
     * @param bonusShare 红利再投份额
     */
    public void setBonusShare(BigDecimal bonusShare) {
        this.bonusShare = bonusShare;
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
     * 取得 分红截止日
     * @return 分红截止日
     */
    public Date getMdate() {
        return mdate;
    }

    /**
     * 设置 分红截止日
     * @param mdate 分红截止日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setMdate(Date mdate) {
        this.mdate = mdate;
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
}