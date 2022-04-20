/*
 * 文件名：ValBokBalanceGenerateResult.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V1.0
 * 修改人：ruanhongming
 * 修改时间：2018年5月31日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V1.0
 */

package com.joyintech.tams.valuation.batch.valschemelanding.dto;

import com.joyintech.batch.batchbase.baseclass.entity.NeedToWriteEntity;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 估值方案关联关系落地Processor结果Dto
 * 
 * @author weipeng
 * @version 1.0 2022年3月29日
 * @see ValSchemeLandingProceesorDto
 * @since 1.0
 */
public class ValSchemeLandingProceesorDto extends NeedToWriteEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 账套代码
     */
    private String booksetId;

    /**
     * 金融产品代码
     */
    private String finprodId;

    /**
     * 证券品种
     */
    private String secType;

    /**
     *  交易场所
     */
    private String tradeMarket;

    /**
     * 投资目的
     */
    private String invAim;

    /**
     *  资产类型
     */
    private String type1;

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
    private BigDecimal quotaDigit;
    /**
     * 估值日期
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
     * 价格类型
     */
    private String priceClass;

    /**
     * 尾数处理方式
     */
    private String priceTailMode;

    /**
     * 取得 账套代码
     *
     * @return 账套代码
     */
    public String getBooksetId() {
        return booksetId;
    }

    /**
     * 设置 账套代码
     *
     * @param booksetId 账套代码
     */
    public void setBooksetId(String booksetId) {
        this.booksetId = booksetId;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getType1() {
        return type1;
    }

    public void setType1(String type1) {
        this.type1 = type1;
    }
    /**
     * 取得 方案明细代码
     *
     * @return 方案明细代码
     */
    public String getSchemeDetailId() {
        return schemeDetailId;
    }

    /**
     * 设置 方案明细代码
     *
     * @param schemeDetailId
     *            方案明细代码
     */
    public void setSchemeDetailId(String schemeDetailId) {
        this.schemeDetailId = schemeDetailId;
    }

    /**
     * 取得 方案代码
     *
     * @return 方案代码
     */
    public String getSchemeId() {
        return schemeId;
    }

    /**
     * 设置 方案代码
     *
     * @param schemeId
     *            方案代码
     */
    public void setSchemeId(String schemeId) {
        this.schemeId = schemeId;
    }

    /**
     * 取得 交易市场
     *
     * @return 交易市场
     */
    public String getTradeMarket() {
        return tradeMarket;
    }

    /**
     * 设置 交易市场
     *
     * @param tradeMarket
     *            交易市场
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    /**
     * 取得 证券品种
     *
     * @return 证券品种
     */
    public String getSecType() {
        return secType;
    }

    /**
     * 设置 证券品种
     *
     * @param secType
     *            证券品种
     */
    public void setSecType(String secType) {
        this.secType = secType;
    }

    /**
     * 取得 投资目的
     *
     * @return 投资目的
     */
    public String getInvAim() {
        return invAim;
    }

    /**
     * 设置 投资目的
     *
     * @param invAim
     *            投资目的
     */
    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    /**
     * 取得 估值方法
     *
     * @return 估值方法
     */
    public String getValuationMethod() {
        return valuationMethod;
    }

    /**
     * 设置 估值方法
     *
     * @param valuationMethod
     *            估值方法
     */
    public void setValuationMethod(String valuationMethod) {
        this.valuationMethod = valuationMethod;
    }

    /**
     * 取得 估值市价
     *
     * @return 估值市价
     */
    public String getValuationPrice() {
        return valuationPrice;
    }

    /**
     * 设置 估值市价
     *
     * @param valuationPrice
     *            估值市价
     */
    public void setValuationPrice(String valuationPrice) {
        this.valuationPrice = valuationPrice;
    }

    /**
     * 取得 行情位数
     *
     * @return 行情位数
     */
    public BigDecimal getQuotaDigit() {
        return quotaDigit;
    }

    /**
     * 设置 行情位数
     *
     * @param quotaDigit
     *            行情位数
     */
    public void setQuotaDigit(BigDecimal quotaDigit) {
        this.quotaDigit = quotaDigit;
    }

    /**
     * 取得 估值日期
     *
     * @return 估值日期
     */
    public String getValuationDate() {
        return valuationDate;
    }

    /**
     * 设置 估值日期
     *
     * @param valuationDate
     *            估值日期
     */
    public void setValuationDate(String valuationDate) {
        this.valuationDate = valuationDate;
    }

    /**
     * 取得 开始日
     *
     * @return 开始日
     */
    public Date getVdate() {
        return vdate;
    }

    /**
     * 设置 开始日
     *
     * @param vdate
     *            开始日
     */
    public void setVdate(Date vdate) {
        this.vdate = vdate;
    }

    /**
     * 取得 结束日
     *
     * @return 结束日
     */
    public Date getMdate() {
        return mdate;
    }

    /**
     * 设置 结束日
     *
     * @param mdate
     *            结束日
     */
    public void setMdate(Date mdate) {
        this.mdate = mdate;
    }


    /**
     * 获取 估值第二市价
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
        this.valuationPrice2 = valuationPrice2;
    }



    public String getValuationSource() {
        return valuationSource;
    }

    public void setValuationSource(String valuationSource) {
        this.valuationSource = valuationSource;
    }


    /**
     * 获取 价格类型
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
        this.priceClass = priceClass;
    }


    /**
     * 获取 尾数处理方式
     * @return 尾数处理方式
     */
    public String getPriceTailMode() {
        return priceTailMode;
    }


    /**
     * 设置 尾数处理方式
     * @param priceTailMode 尾数处理方式
     */
    public void setPriceTailMode(String priceTailMode) {
        this.priceTailMode = priceTailMode;
    }

}
