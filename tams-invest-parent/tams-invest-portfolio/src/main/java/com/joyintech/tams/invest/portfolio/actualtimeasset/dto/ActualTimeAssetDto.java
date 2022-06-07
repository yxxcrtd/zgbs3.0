/*
 * 文件名：ActualTimeAssetDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：Administrator
 * 修改时间：2019年10月15日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.invest.portfolio.actualtimeasset.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.constant.GlobalConstant;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeList;
import com.joyintech.tams.constant.DictCodeListExt2;
import com.joyintech.tams.systemtools.util.SysDatadictUtil;

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
     * 证券账户ID + 证券账户名称
     */
    private String secAcctInfo;
    
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
     * 资产二级类型名称
     */
    private String type7Name;
    
    /**
     * 资产二级类型名称
     */
    private String type8Name;
    
    
    
    
    public void setSecAcctInfo(String secAcctInfo) {
        this.secAcctInfo = secAcctInfo;
    }

    
    public void setType7Name(String type7Name) {
        this.type7Name = type7Name;
    }

    
    public void setType8Name(String type8Name) {
        this.type8Name = type8Name;
    }

    /**
     * 资产类型名称
     * 
     * @return
     * @see
     * @since 1.0
     */
    public String getType8Name() {
        return SysDataUtil.getNameFromDataDict(DictCode.平安资产类型, this.getType8());
    }
    
    /**
     * 资产二级类型名称
     * 
     * @return
     * @see
     * @since 1.0
     */
    public String getType7Name() {
        //基金类资产二级类型
        //WIND二级分类（含债券二级分类等）
        //PLAN_ASSET_SEC_TYPE计划类资产二级分类
        //项目类资产二级分类
        //存款类资产存款类型 DEPO_TYPE
        //回购拆借方向BACK_LEND_DIRECTION
        if(DictCodeList.平安资产类型_债券.getValue().equals(this.getType8()) && !(SysDatadictUtil.getSysDictDataMap(DictCode.WIND一级分类_含债券一级分类等).containsKey(this.getType7()))) {
            return SysDataUtil.getNameFromDataDict(DictCode.WIND二级分类_含债券二级分类等, this.getType7());
        }
        if((DictCodeList.平安资产类型_债券.getValue().equals(this.getType8()) && (SysDatadictUtil.getSysDictDataMap(DictCode.WIND一级分类_含债券一级分类等).containsKey(this.getType7())))){
            return SysDataUtil.getNameFromDataDict(DictCode.WIND一级分类_含债券一级分类等, this.getType7());
        }
        if(DictCodeList.平安资产类型_基金.getValue().equals(this.getType8()) && !(SysDatadictUtil.getSysDictDataMap(DictCode.基金类资产类型).containsKey(this.getType7()))) {
            return SysDataUtil.getNameFromDataDict(DictCode.基金类资产二级类型, this.getType7());
        }
        if(DictCodeList.平安资产类型_基金.getValue().equals(this.getType8()) && (SysDatadictUtil.getSysDictDataMap(DictCode.基金类资产类型).containsKey(this.getType7()))) {
            return SysDataUtil.getNameFromDataDict(DictCode.基金类资产类型, this.getType7());
        }
        if(DictCodeList.平安资产类型_计划类资产_利率型.getValue().equals(this.getType8()) 
            || DictCodeList.平安资产类型_计划类资产_净值型.getValue().equals(this.getType8()) 
            || DictCodeList.平安资产类型_计划类资产_货币型.getValue().equals(this.getType8())) {
            return SysDataUtil.getNameFromDataDict(DictCode.计划类资产二级分类, this.getType7());
        }
        if(DictCodeList.平安资产类型_存款.getValue().equals(this.getType8())) {
            return SysDataUtil.getNameFromDataDict(DictCode.存款类资产存款类型, this.getType7());
        }
        if(DictCodeList.平安资产类型_项目类资产.getValue().equals(this.getType8()) && !(SysDatadictUtil.getSysDictDataMap(DictCode.项目类一级分类).containsKey(this.getType7()))) {
            return SysDataUtil.getNameFromDataDict(DictCode.项目类资产二级类型, this.getType7());
        }
        if((DictCodeList.平安资产类型_项目类资产.getValue().equals(this.getType8()) && (SysDatadictUtil.getSysDictDataMap(DictCode.项目类一级分类).containsKey(this.getType7())))){
            return SysDataUtil.getNameFromDataDict(DictCode.项目类一级分类, this.getType7());
        }

        if(DictCodeList.平安资产类型_质押式回购.getValue().equals(this.getType8()) 
            || DictCodeList.平安资产类型_买断式回购.getValue().equals(this.getType8()) 
            || DictCodeList.平安资产类型_拆借.getValue().equals(this.getType8())) {
            return SysDataUtil.getNameFromDataDict(DictCode.回购拆借方向, this.getType7());
        }

        return "";
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
    @JsonSerialize(using=BigDecimalSerializer.class)
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

}
