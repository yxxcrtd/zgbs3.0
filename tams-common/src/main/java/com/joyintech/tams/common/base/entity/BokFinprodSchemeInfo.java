package com.joyintech.tams.common.base.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.CustomDateDeserialize;
import java.util.Date;

public class BokFinprodSchemeInfo extends BaseEntity {
    /**
     * 账套代码
     */
    private String booksetId;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 交易市场
     */
    private String tradeMarket;

    /**
     * 证券品种
     */
    private String secType;

    /**
     * 资产类型
     */
    private String type1;

    /**
     * 投资目的
     */
    private String invAim;

    /**
     * 方案明细代码
     */
    private String schemeDetailId;

    /**
     * 方案代码
     */
    private String schemeId;

    /**
     * 估值方法
     */
    private String valuationMethod;

    /**
     * 估值市价
     */
    private String valuationPrice;

    /**
     * 估值第二市价
     */
    private String valuationPrice2;

    /**
     * 行情位数
     */
    private Long quotaDigit;

    /**
     * 行情日期
     */
    private String valuationDate;

    /**
     * 开始日
     */
    private Date vdate;

    /**
     * 结束日
     */
    private Date mdate;

    /**
     * 估值来源
     */
    private String valuationSource;

    /**
     * 行情尾数处理方式
     */
    private String priceTailMode;

    /**
     * 价格类型
     */
    private String priceClass;

    /**
     * 取得 账套代码
     * @return 账套代码
     */
    public String getBooksetId() {
        return booksetId;
    }

    /**
     * 设置 账套代码
     * @param booksetId 账套代码
     */
    public void setBooksetId(String booksetId) {
        this.booksetId = booksetId == null ? null : booksetId.trim();
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
     * 取得 交易市场
     * @return 交易市场
     */
    public String getTradeMarket() {
        return tradeMarket;
    }

    /**
     * 设置 交易市场
     * @param tradeMarket 交易市场
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket == null ? null : tradeMarket.trim();
    }

    /**
     * 取得 证券品种
     * @return 证券品种
     */
    public String getSecType() {
        return secType;
    }

    /**
     * 设置 证券品种
     * @param secType 证券品种
     */
    public void setSecType(String secType) {
        this.secType = secType == null ? null : secType.trim();
    }

    /**
     * 取得 资产类型
     * @return 资产类型
     */
    public String getType1() {
        return type1;
    }

    /**
     * 设置 资产类型
     * @param type1 资产类型
     */
    public void setType1(String type1) {
        this.type1 = type1 == null ? null : type1.trim();
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
     * 取得 方案明细代码
     * @return 方案明细代码
     */
    public String getSchemeDetailId() {
        return schemeDetailId;
    }

    /**
     * 设置 方案明细代码
     * @param schemeDetailId 方案明细代码
     */
    public void setSchemeDetailId(String schemeDetailId) {
        this.schemeDetailId = schemeDetailId == null ? null : schemeDetailId.trim();
    }

    /**
     * 取得 方案代码
     * @return 方案代码
     */
    public String getSchemeId() {
        return schemeId;
    }

    /**
     * 设置 方案代码
     * @param schemeId 方案代码
     */
    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId == null ? null : schemeId.trim();
    }

    /**
     * 取得 估值方法
     * @return 估值方法
     */
    public String getValuationMethod() {
        return valuationMethod;
    }

    /**
     * 设置 估值方法
     * @param valuationMethod 估值方法
     */
    public void setValuationMethod(String valuationMethod) {
        this.valuationMethod = valuationMethod == null ? null : valuationMethod.trim();
    }

    /**
     * 取得 估值市价
     * @return 估值市价
     */
    public String getValuationPrice() {
        return valuationPrice;
    }

    /**
     * 设置 估值市价
     * @param valuationPrice 估值市价
     */
    public void setValuationPrice(String valuationPrice) {
        this.valuationPrice = valuationPrice == null ? null : valuationPrice.trim();
    }

    /**
     * 取得 估值第二市价
     * @return 估值第二市价
     */
    public String getValuationPrice2() {
        return valuationPrice2;
    }

    /**
     * 设置 估值第二市价
     * @param valuationPrice2 估值第二市价
     */
    public void setValuationPrice2(String valuationPrice2) {
        this.valuationPrice2 = valuationPrice2 == null ? null : valuationPrice2.trim();
    }

    /**
     * 取得 行情位数
     * @return 行情位数
     */
    public Long getQuotaDigit() {
        return quotaDigit;
    }

    /**
     * 设置 行情位数
     * @param quotaDigit 行情位数
     */
    public void setQuotaDigit(Long quotaDigit) {
        this.quotaDigit = quotaDigit;
    }

    /**
     * 取得 行情日期
     * @return 行情日期
     */
    public String getValuationDate() {
        return valuationDate;
    }

    /**
     * 设置 行情日期
     * @param valuationDate 行情日期
     */
    public void setValuationDate(String valuationDate) {
        this.valuationDate = valuationDate == null ? null : valuationDate.trim();
    }

    /**
     * 取得 开始日
     * @return 开始日
     */
    public Date getVdate() {
        return vdate;
    }

    /**
     * 设置 开始日
     * @param vdate 开始日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    /**
     * 取得 结束日
     * @return 结束日
     */
    public Date getMdate() {
        return mdate;
    }

    /**
     * 设置 结束日
     * @param mdate 结束日
     */
    @JsonDeserialize(using = CustomDateDeserialize.class)
    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }

    /**
     * 取得 估值来源
     * @return 估值来源
     */
    public String getValuationSource() {
        return valuationSource;
    }

    /**
     * 设置 估值来源
     * @param valuationSource 估值来源
     */
    public void setValuationSource(String valuationSource) {
        this.valuationSource = valuationSource == null ? null : valuationSource.trim();
    }

    /**
     * 取得 行情尾数处理方式
     * @return 行情尾数处理方式
     */
    public String getPriceTailMode() {
        return priceTailMode;
    }

    /**
     * 设置 行情尾数处理方式
     * @param priceTailMode 行情尾数处理方式
     */
    public void setPriceTailMode(String priceTailMode) {
        this.priceTailMode = priceTailMode == null ? null : priceTailMode.trim();
    }

    /**
     * 取得 价格类型
     * @return 价格类型
     */
    public String getPriceClass() {
        return priceClass;
    }

    /**
     * 设置 价格类型
     * @param priceClass 价格类型
     */
    public void setPriceClass(String priceClass) {
        this.priceClass = priceClass == null ? null : priceClass.trim();
    }
}