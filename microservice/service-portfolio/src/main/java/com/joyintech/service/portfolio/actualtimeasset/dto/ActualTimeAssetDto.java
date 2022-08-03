/*
 * 文件名：ActualTimeAssetDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Administrator
 * 修改时间：2019年10月15日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.service.portfolio.actualtimeasset.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.StringUtil;

public class ActualTimeAssetDto extends BaseEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -40087906108612581L;
    
    /**
     * 组合代码
     */
    private String portfolioId;
    
    /**
     * 组合名称
     */
    private String portfolioName;
    
    /**
     * 证券账户ID
     */
    private String secAcctId;
    
    /**
     * 证券账户名称
     */
    private String secAcctName;
    
    
    /**
     * 金融产品ID
     */
    private String finprodId;
    
    /**
     * 资产代码
     */
    private String finprodMarketId;
    
    /**
     * 资产名称
     */
    private String finprodAbbr;
    
    /**
     * 资产类型
     */
    private String type8;
    
    /**
     * 资产二级类型
     */
    private String type7;

    /**
     * 资产一级分类
     */
    private  String type6;

    /**
     * 行内二级分类
     */
    private  String type9;

    /**
     * 获得 资产一级分类
     * @return
     */
    public String getType6() {
        return type6;
    }

    /**
     * 设置 资产一级分类
     * @param type6
     */
    public void setType6(String type6) {
        this.type6 = type6;
    }

    /**
     * 实际持仓
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal actualPositionAmount;
    
    /**
     * 交易可用持仓
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal tradePositionAmount;
    
    /**
     * 指令可用持仓
     */
    private BigDecimal instructionsAmount;
    
    /**
     * 质押持仓
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal pledgeAmount;
    
    /**
     * 预减持仓
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal preReductionAmount;

    /**
     * 市值
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal marketValue;

    public String getPortfolioId() {
        return portfolioId;
    }

    
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    
    public String getPortfolioName() {
        return portfolioName;
    }

    
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    
    public String getSecAcctId() {
        return secAcctId;
    }

    
    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    
    public String getSecAcctName() {
        return secAcctName;
    }

    
    public void setSecAcctName(String secAcctName) {
        this.secAcctName = secAcctName;
    }

    
    public String getSecAcctInfo() {
        if(StringUtil.isNotEmpty(this.getSecAcctId())) {
            return this.getSecAcctId() + GlobalConstant.COLON + this.getSecAcctName();
        }
        else {
            return "";
        }
    }


    
    public String getFinprodId() {
        return finprodId;
    }

    
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    
    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    
    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    
    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    
    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    
    public String getType8() {
        return type8;
    }

    
    public void setType8(String type8) {
        this.type8 = type8;
    }

    
    public String getType7() {
        return type7;
    }

    
    public void setType7(String type7) {
        this.type7 = type7;
    }

    
    public BigDecimal getActualPositionAmount() {
        return actualPositionAmount;
    }

    
    public void setActualPositionAmount(BigDecimal actualPositionAmount) {
        this.actualPositionAmount = actualPositionAmount;
    }

    
    public BigDecimal getTradePositionAmount() {
        return tradePositionAmount;
    }

    
    public void setTradePositionAmount(BigDecimal tradePositionAmount) {
        this.tradePositionAmount = tradePositionAmount;
    }

    
    public BigDecimal getInstructionsAmount() {
        return instructionsAmount;
    }

    
    public void setInstructionsAmount(BigDecimal instructionsAmount) {
        this.instructionsAmount = instructionsAmount;
    }

    
    public BigDecimal getPledgeAmount() {
        return pledgeAmount;
    }

    
    public void setPledgeAmount(BigDecimal pledgeAmount) {
        this.pledgeAmount = pledgeAmount;
    }

    
    public BigDecimal getPreReductionAmount() {
        return preReductionAmount;
    }

    
    public void setPreReductionAmount(BigDecimal preReductionAmount) {
        this.preReductionAmount = preReductionAmount;
    }

    public BigDecimal getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(BigDecimal marketValue) {
        this.marketValue = marketValue;
    }

    public String getType9() {
        return type9;
    }

    public void setType9(String type9) {
        this.type9 = type9;
    }
}
