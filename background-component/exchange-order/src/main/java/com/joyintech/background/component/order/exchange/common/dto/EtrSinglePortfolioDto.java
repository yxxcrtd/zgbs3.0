/*
 * 文件名：EtrSinglePortfolioDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangrui1
 * 修改时间：2020年7月8日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.background.component.order.exchange.common.dto;

import java.math.BigDecimal;

import com.joyintech.background.component.common.pack.common.validation.annotation.NotEmpty;
import com.joyintech.background.component.order.exchange.common.constant.DictCodeList;

/**
 * 单组合单资产指令Dto
 * 
 * @author zhangrui1
 * @version 1.0 2020年7月8日
 * @see EtrSinglePortfolioDto
 * @since 1.0
 */
public class EtrSinglePortfolioDto extends EtrBaseDto {

    /**
     * 序列号
     */
    private static final long serialVersionUID = -3442121719452544327L;

    /**
     * 组合代码
     */
    private String portfolioId;

    /**
     * 资产代码
     */
    private String finprodId;

    /**
     * 交易场所
     */
    private String tradeMarket;

    /**
     * 交易平台
     */
    private String tradePlat;

    /**
     * 委托方向
     */
    private String ps;

    /**
     * 投资目的
     */
    private String invAim;

    /**
     * 指令金额
     */
    private BigDecimal entrustAmt;

    /**
     * 指令数量
     */
    private BigDecimal entrustNum;

    /**
     * 指令价格
     */
    private BigDecimal price;

    /**
     * 价格模式
     */
    private String priceModel;

    /**
     * 金融产品类型（投管分类）
     */
    private String finprodType2;

    /**
     * 币种
     */
    private String ccy;

    /**
     * 证券代码
     */
    private String stockId;

    /**
     * 证券账户代码
     */
    private String secAcctId;

    /**
     * 资金账户代码
     */
    private String dealCurAcct;

    /**
     * 资产大类
     */
    private String assetType;

    /**
     * 资产二级类型
     */
    private String assetType1;

    /**
     * 交易方式
     */
    private String dealMode;
    /**
     *业务模式
     */
    private String businessModel;
    /**
     * 投管业务类型
     */
    private String busiTypePosition;

    /**
     * 计价方式
     */
    private String priceType;


    /**
     * Constructor
     */
    public EtrSinglePortfolioDto() {
        super();
    }

    /**
     * @param entrustType 指令业务类型
     */
    protected EtrSinglePortfolioDto(String entrustType) {
        super(entrustType, DictCodeList.指令类型_单组合单资产投资指令.getValue());
    }

    /**
     * 取得 组合代码
     * 
     * @return 组合代码
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
     * 取得 资产代码
     * 
     * @return 资产代码
     */
    public String getFinprodId() {
        return this.finprodId;
    }

    /**
     * 设置 资产代码
     * 
     * @param finprodId 资产代码
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    /**
     * 取得 交易场所
     * 
     * @return 交易场所
     */
    @NotEmpty(fieldName = "EtrComponent.tradeMarket")
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
     * 取得 交易平台
     * 
     * @return 交易平台
     */
    public String getTradePlat() {
        return this.tradePlat;
    }

    /**
     * 设置 交易平台
     * 
     * @param tradePlat 交易平台
     */
    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat;
    }

    /**
     * 取得 委托方向
     * 
     * @return 委托方向
     */
    @NotEmpty(fieldName = "EtrComponent.ps")
    public String getPs() {
        return this.ps;
    }

    /**
     * 设置 委托方向
     * 
     * @param ps 委托方向
     */
    public void setPs(String ps) {
        this.ps = ps;
    }

    /**
     * 取得 投资目的
     * 
     * @return 投资目的
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
     * 取得 指令金额
     * 
     * @return 指令金额
     */
    @NotEmpty(fieldName = "EtrComponent.entrustAmt")
    public BigDecimal getEntrustAmt() {
        return this.entrustAmt;
    }

    /**
     * 设置 指令金额
     * 
     * @param entrustAmt 指令金额
     */
    public void setEntrustAmt(BigDecimal entrustAmt) {
        this.entrustAmt = entrustAmt;
    }

    /**
     * 取得 指令数量
     * 
     * @return 指令数量
     */
    @NotEmpty(fieldName = "EtrComponent.entrustNum")
    public BigDecimal getEntrustNum() {
        return this.entrustNum;
    }

    /**
     * 设置 指令数量
     * 
     * @param entrustNum 指令数量
     */
    public void setEntrustNum(BigDecimal entrustNum) {
        this.entrustNum = entrustNum;
    }

    /**
     * 取得 指令价格
     * 
     * @return 指令价格
     */
    @NotEmpty(fieldName = "EtrComponent.price")
    public BigDecimal getPrice() {
        return this.price;
    }

    /**
     * 设置 指令价格
     * 
     * @param price 指令价格
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 取得 价格模式
     * 
     * @return 价格模式
     */
    @NotEmpty(fieldName = "EtrComponent.priceModel")
    public String getPriceModel() {
        return this.priceModel;
    }

    /**
     * 设置 价格模式
     * 
     * @param priceModel 价格模式
     */
    public void setPriceModel(String priceModel) {
        this.priceModel = priceModel;
    }

    /**
     * 获取 金融产品类型（投管分类）
     * 
     * @return 金融产品类型（投管分类）.
     */
    public String getFinprodType2() {
        return finprodType2;
    }

    /**
     * 设置 金融产品类型（投管分类）
     * 
     * @param finprodType2 金融产品类型（投管分类）
     */
    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    /**
     * 获取 币种
     * 
     * @return 币种.
     */
    public String getCcy() {
        return ccy;
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
     * 获取 证券代码
     * 
     * @return 证券代码.
     */
    @NotEmpty(fieldName = "EtrComponent.stockId")
    public String getStockId() {
        return stockId;
    }

    /**
     * 设置 证券代码
     * 
     * @param stockId 证券代码
     */
    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    /**
     * 获取 证券账户代码
     * 
     * @return 证券账户代码.
     */
    public String getSecAcctId() {
        return secAcctId;
    }

    /**
     * 设置 证券账户代码
     * 
     * @param secAcctId 证券账户代码
     */
    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    /**
     * 获取 资金账户
     * 
     * @return 资金账户.
     */
    public String getDealCurAcct() {
        return dealCurAcct;
    }

    /**
     * 设置 资金账户
     * 
     * @param dealCurAcct 资金账户
     */
    public void setDealCurAcct(String dealCurAcct) {
        this.dealCurAcct = dealCurAcct;
    }

    /**
     * 获取 资产大类
     * 
     * @return 资产大类.
     */
    public String getAssetType() {
        return assetType;
    }

    /**
     * 设置 资产大类
     * 
     * @param assetType 资产大类
     */
    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    /**
     * 获取 资产二级类型
     * 
     * @return 资产二级类型.
     */
    public String getAssetType1() {
        return assetType1;
    }

    /**
     * 设置 资产二级类型
     * 
     * @param assetType1 资产二级类型
     */
    public void setAssetType1(String assetType1) {
        this.assetType1 = assetType1;
    }

    /**
     * 获取 交易方式
     * 
     * @return 交易方式.
     */
    public String getDealMode() {
        return dealMode;
    }

    /**
     * 设置 交易方式
     * 
     * @param dealMode 交易方式
     */
    public void setDealMode(String dealMode) {
        this.dealMode = dealMode;
    }
    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }

    public String getBusiTypePosition() {
        return busiTypePosition;
    }

    public void setBusiTypePosition(String busiTypePosition) {
        this.busiTypePosition = busiTypePosition;
    }

    public String getPriceType() {
        return priceType;
    }

    public void setPriceType(String priceType) {
        this.priceType = priceType;
    }
}
