/*
 * 文件名：EtrExchangeTradeDto002.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.invest.common.constant.BussinessDictCode;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author chendijun
 * @version 1.0 2020年7月2日
 * @see EtrExchangeAgreRepTrade002Dto
 * @since 1.0
 */
@PageId("EtrExchangeAgreRepTrade002Dto")
public class EtrExchangeAgreRepTrade002Dto extends BaseEntity {

    /**
     * <意义，目的和功能，以及可能被用到的地方>
     */
    private static final long serialVersionUID = -1548012609915542103L;

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
     * 资金账户
     */
    private String acctNo;

    /**
     * 资金账户名称
     */
    private String acctName;

    /**
     * 证券代码
     */
    private String secId;

    /**
     * 证券名称
     */
    private String secName;

    /**
     * 交易市场
     */
    private String tradeMarket;

    /**
     * 证券市场代码
     */
    private String finprodMarketId;

    /**
     * 资产代码
     */
    private String finprodId;

    /**
     * 资产名称
     */
    private String finpordName;

    /**
     * 金融产品类型（投管分类）
     */
    private String finprodType2;

    /**
     * 投资目的
     */
    private String invAim;
    /**
     * 业务模式
     */
    private String businessModel;

    /**
     * 投资目的
     */
    private String invAimStr;

    /**
     * 持仓数量
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal positionsNum;

    /**
     * 可卖数量
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal sellNum;

    /**
     * 持仓成本
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal positionsAmt;

    /**
     * 最新价
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal price;

    /**
     * 浮动盈亏
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal floatingPL;

    @ApiModelProperty(name="floatingPLPercent",value="盈亏比例",example="12")
    private BigDecimal floatingPLPercent;

    /**
     * 组合持仓.数量
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal ptlPositionsNum;

    /**
     * 质押数量
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal repoNum;

    /**
     * 标准券数量
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal secNum;

    /**
     * 可质押数量
     */
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal canRepoNum;

    /**
     * 共通业务类型
     */
    private String assetType;

    /**
     * 获取 ptlPositionsNum
     * @return ptlPositionsNum
     */
    public BigDecimal getPtlPositionsNum() {
        return ptlPositionsNum;
    }

    /**
     * 设置 ptlPositionsNum
     *
     * @param ptlPositionsNum ptlPositionsNum
     */
    public void setPtlPositionsNum(BigDecimal ptlPositionsNum) {
        this.ptlPositionsNum = ptlPositionsNum;
    }

    /**
     * 获取 portfolioId
     * 
     * @return portfolioId.
     */
    public String getPortfolioId() {
        return portfolioId;
    }

    /**
     * 设置 portfolioId
     * 
     * @param portfolioId portfolioId
     */
    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    /**
     * 获取 portfolioName
     * 
     * @return portfolioName.
     */
    public String getPortfolioName() {
        return portfolioName;
    }

    /**
     * 设置 portfolioName
     * 
     * @param portfolioName portfolioName
     */
    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    /**
     * 获取 secAcctId
     * 
     * @return secAcctId.
     */
    public String getSecAcctId() {
        return secAcctId;
    }

    /**
     * 设置 secAcctId
     * 
     * @param secAcctId secAcctId
     */
    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    /**
     * 获取 secAcctName
     * 
     * @return secAcctName.
     */
    public String getSecAcctName() {
        return secAcctName;
    }

    /**
     * 设置 secAcctName
     * 
     * @param secAcctName secAcctName
     */
    public void setSecAcctName(String secAcctName) {
        this.secAcctName = secAcctName;
    }

    /**
     * 获取 tradeMarket
     * 
     * @return tradeMarket.
     */
    public String getTradeMarket() {
        return tradeMarket;
    }

    /**
     * 设置 tradeMarket
     * 
     * @param tradeMarket tradeMarket
     */
    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    /**
     * 获取 finprodId
     * 
     * @return finprodId.
     */
    public String getFinprodId() {
        return finprodId;
    }

    /**
     * 设置 finprodId
     * 
     * @param finprodId finprodId
     */
    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    /**
     * 获取 finpordName
     * 
     * @return finpordName.
     */
    public String getFinpordName() {
        return finpordName;
    }

    /**
     * 设置 finpordName
     * 
     * @param finpordName finpordName
     */
    public void setFinpordName(String finpordName) {
        this.finpordName = finpordName;
    }

    /**
     * 获取 invAim
     * 
     * @return invAim.
     */
    public String getInvAim() {
        return invAim;
    }

    /**
     * 设置 invAim
     * 
     * @param invAim invAim
     */
    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    /**
     * 获取 positionsNum
     * 
     * @return positionsNum.
     */
    public BigDecimal getPositionsNum() {
        return positionsNum;
    }

    /**
     * 设置 positionsNum
     * 
     * @param positionsNum positionsNum
     */
    public void setPositionsNum(BigDecimal positionsNum) {
        this.positionsNum = positionsNum;
    }

    /**
     * 获取 sellNum
     * 
     * @return sellNum.
     */
    public BigDecimal getSellNum() {
        return sellNum;
    }

    /**
     * 设置 sellNum
     * 
     * @param sellNum sellNum
     */
    public void setSellNum(BigDecimal sellNum) {
        this.sellNum = sellNum;
    }

    /**
     * 获取 positionsAmt
     * 
     * @return positionsAmt.
     */
    public BigDecimal getPositionsAmt() {
        return positionsAmt;
    }

    /**
     * 设置 positionsAmt
     * 
     * @param positionsAmt positionsAmt
     */
    public void setPositionsAmt(BigDecimal positionsAmt) {
        this.positionsAmt = positionsAmt;
    }

    /**
     * 获取 price
     * 
     * @return price.
     */
    public BigDecimal getPrice() {
        return price;
    }

    /**
     * 设置 price
     * 
     * @param price price
     */
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    /**
     * 获取 floatingPL
     * 
     * @return floatingPL.
     */
    public BigDecimal getFloatingPL() {
        return floatingPL;
    }

    /**
     * 设置 floatingPL
     * 
     * @param floatingPL floatingPL
     */
    public void setFloatingPL(BigDecimal floatingPL) {
        this.floatingPL = floatingPL;
    }

    /**
     * 获取 acctNo
     * 
     * @return acctNo.
     */
    public String getAcctNo() {
        return acctNo;
    }

    /**
     * 设置 acctNo
     * 
     * @param acctNo acctNo
     */
    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    /**
     * 获取 secId
     * 
     * @return secId.
     */
    public String getSecId() {
        return secId;
    }

    /**
     * 设置 secId
     * 
     * @param secId secId
     */
    public void setSecId(String secId) {
        this.secId = secId;
    }

    /**
     * 获取 secName
     * 
     * @return secName.
     */
    public String getSecName() {
        return secName;
    }

    /**
     * 设置 secName
     * 
     * @param secName secName
     */
    public void setSecName(String secName) {
        this.secName = secName;
    }

    /**
     * 获取 finprodType2
     *
     * @return finprodType2.
     */
    public String getFinprodType2() {
        return finprodType2;
    }

    /**
     * 设置 finprodType2
     *
     * @param finprodType2 finprodType2
     */
    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    /**
     * 获取 finprodMarketId
     *
     * @return finprodMarketId.
     */
    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    /**
     * 设置 finprodMarketId
     *
     * @param finprodMarketId finprodMarketId
     */
    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    /**
     * 获取 repoNum
     * 
     * @return repoNum
     */
    public BigDecimal getRepoNum() {
        return repoNum;
    }

    /**
     * 设置 repoNum
     * 
     * @param repoNum BigDecimal
     */
    public void setRepoNum(BigDecimal repoNum) {
        this.repoNum = repoNum;
    }

    /**
     * 获取 secNum
     * 
     * @return secNum
     */
    public BigDecimal getSecNum() {
        return secNum;
    }

    /**
     * 设置 secNum
     * 
     * @param secNum BigDecimal
     */
    public void setSecNum(BigDecimal secNum) {
        this.secNum = secNum;
    }

    /**
     * 获取 canRepoNum
     * 
     * @return canRepoNum
     */
    public BigDecimal getCanRepoNum() {
        return canRepoNum;
    }

    /**
     * 设置 canRepoNum
     * 
     * @param canRepoNum BigDecimal
     */
    public void setCanRepoNum(BigDecimal canRepoNum) {
        this.canRepoNum = canRepoNum;
    }

    public String getTradeMarketStr(){
        if(StringUtil.isNotEmpty(getTradeMarket())){
            return SysDataUtil.getNameFromDataDict(BussinessDictCode.交易场所,getTradeMarket());
        }
        else {
            return "";
        }
    }

    public String getAcctName() {
        return acctName;
    }

    public void setAcctName(String acctName) {
        this.acctName = acctName;
    }

    public BigDecimal getFloatingPLPercent() {
        return floatingPLPercent;
    }

    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }

    public void setFloatingPLPercent(BigDecimal floatingPLPercent) {
        this.floatingPLPercent = floatingPLPercent;
    }

    public String getInvAimStr() {
        return SysDataUtil.getNameFromDataDict(BussinessDictCode.投资目的, this.getInvAim());
    }
    /**
     * 获取业务模式名称
     * @return businessModelStr
     */
    public String getBusinessModelStr(){
        if(StringUtil.isNotEmpty(getBusinessModel())){
            return SysDataUtil.getNameFromDataDict(DictCode.业务模式,getBusinessModel());
        }
        else {
            return "";
        }
    }

    public String getAssetType() {
        return assetType;
    }


    public String getAssetTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.资产三级分类, getAssetType());
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }
}
