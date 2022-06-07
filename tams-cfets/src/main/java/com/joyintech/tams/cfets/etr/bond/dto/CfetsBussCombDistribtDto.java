/*
 * 文件名：BussCombDistribtDto.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：王波
 * 修改时间：2021年01月25日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.cfets.etr.bond.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.framework.util.StringUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 组合分配DTO
 * @author 王波
 * @date 2021年01月25日
 * @see CfetsBussCombDistribtDto
 * @since 1.0
 */
public class CfetsBussCombDistribtDto {

    /**
     * 组合ID
     */
    private String portfolioId;

    private String entrustId;

    /**
     * 券面总额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal entrustNum;

    private String invAim;

    @ApiModelProperty(name="businessModel",value="业务模式",example="A1")
    private String businessModel;

    @ApiModelProperty(name="elementId",value="按钮id",example="EtribBondInfo003_btn_submit")
    private String  elementId;



    /**
     * 组合T日可用现金
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal proAvailableCash;

    /**
     * 组合T+1日可用现金
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal proAvailableCash1;


    /**
     * 托管户T日可用现金
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal secManageAvailablePosition;

    /**
     * 托管户T+1日可用现金
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal secManageAvailablePosition1;

    /**
     * 通道T日可用现金
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal channelAvailableCash;

    /**
     * 通道T+1日可用现金
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal channelAvailableCash1;

    /**
     *  资产持仓券面总额（万元）
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal assetPositionEntrustAmt;

    /**
     * 资产可用券面总额（万元）
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal assetAvailableEntrustAmt;

    /**
     *  结算金额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal settleAmt;
    /**
     * 本方券券面总额
     */
    @JsonSerialize(using= BigDecimalSerializer.class)
    private BigDecimal entrustOwnNum;
    /**
     * 融入券/卖空购回券面总额
     */
    @JsonSerialize(using=BigDecimalSerializer.class)
    private BigDecimal entrustIntegrateNum;

    @JsonSerialize(using= BigDecimalSerializer.class)
    @ApiModelProperty(name="secAcctAvailableCash",value="账户现金",example="100")
    private BigDecimal secAcctAvailableCash;

    @JsonSerialize(using= BigDecimalSerializer.class)
    @ApiModelProperty(name="entrustIntegrateSecNum",value="融入券卖空购回持仓",example="100")
    private BigDecimal entrustIntegrateSecNum;

    @JsonSerialize(using= BigDecimalSerializer.class)
    @ApiModelProperty(name="ownPorAvailableSec",value="本方券T日可用持仓",example="100")
    private BigDecimal ownPorAvailableSec;

    @JsonSerialize(using= BigDecimalSerializer.class)
    @ApiModelProperty(name="ownProAvailableSec1",value="本方券T+1日可用持仓",example="100")
    private BigDecimal ownProAvailableSec1;

    @JsonSerialize(using= BigDecimalSerializer.class)
    @ApiModelProperty(name="porAvailableSec",value="资产T日可用持仓",example="100")
    private BigDecimal porAvailableSec;

    @JsonSerialize(using= BigDecimalSerializer.class)
    @ApiModelProperty(name="porAvailableSec1",value="资产T+1日可用持仓",example="100")
    private BigDecimal porAvailableSec1;



    /**
     * 证券账户
     */
    private String secManageAcctId;

    @ApiModelProperty(name="busiModelStr",value="业务模式",example="01")
    private String busiModelStr;

    @ApiModelProperty(name="portfolioName",value="组合名称",example="01")
    private String portfolioName;

    public BigDecimal getSettleAmt() {
        return settleAmt;
    }

    public void setSettleAmt(BigDecimal settleAmt) {
        this.settleAmt = settleAmt;
    }

    public String getSecManageAcctId() {
        return secManageAcctId;
    }

    public void setSecManageAcctId(String secManageAcctId) {
        this.secManageAcctId = secManageAcctId;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    public BigDecimal getEntrustNum() {
        return entrustNum;
    }

    public void setEntrustNum(BigDecimal entrustNum) {
        this.entrustNum = entrustNum;
    }

    public BigDecimal getProAvailableCash() {
        return proAvailableCash;
    }

    public void setProAvailableCash(BigDecimal proAvailableCash) {
        this.proAvailableCash = proAvailableCash;
    }

    public BigDecimal getProAvailableCash1() {
        return proAvailableCash1;
    }

    public void setProAvailableCash1(BigDecimal proAvailableCash1) {
        this.proAvailableCash1 = proAvailableCash1;
    }

    public BigDecimal getSecManageAvailablePosition() {
        return secManageAvailablePosition;
    }

    public void setSecManageAvailablePosition(BigDecimal secManageAvailablePosition) {
        this.secManageAvailablePosition = secManageAvailablePosition;
    }

    public BigDecimal getSecManageAvailablePosition1() {
        return secManageAvailablePosition1;
    }

    public void setSecManageAvailablePosition1(BigDecimal secManageAvailablePosition1) {
        this.secManageAvailablePosition1 = secManageAvailablePosition1;
    }

    public BigDecimal getChannelAvailableCash() {
        return channelAvailableCash;
    }

    public void setChannelAvailableCash(BigDecimal channelAvailableCash) {
        this.channelAvailableCash = channelAvailableCash;
    }

    public BigDecimal getChannelAvailableCash1() {
        return channelAvailableCash1;
    }

    public void setChannelAvailableCash1(BigDecimal channelAvailableCash1) {
        this.channelAvailableCash1 = channelAvailableCash1;
    }

    public BigDecimal getAssetPositionEntrustAmt() {
        return assetPositionEntrustAmt;
    }

    public void setAssetPositionEntrustAmt(BigDecimal assetPositionEntrustAmt) {
        this.assetPositionEntrustAmt = assetPositionEntrustAmt;
    }

    public BigDecimal getAssetAvailableEntrustAmt() {
        return assetAvailableEntrustAmt;
    }

    public void setAssetAvailableEntrustAmt(BigDecimal assetAvailableEntrustAmt) {
        this.assetAvailableEntrustAmt = assetAvailableEntrustAmt;
    }
    private String parentPortfolioId;


    public String getParentPortfolioId() {
        return parentPortfolioId;
    }

    public void setParentPortfolioId(String parentPortfolioId) {
        this.parentPortfolioId = parentPortfolioId;
    }

    public BigDecimal getEntrustOwnNum() {
        return entrustOwnNum;
    }

    public void setEntrustOwnNum(BigDecimal entrustOwnNum) {
        this.entrustOwnNum = entrustOwnNum;
    }

    public BigDecimal getEntrustIntegrateNum() {
        return entrustIntegrateNum;
    }

    public void setEntrustIntegrateNum(BigDecimal entrustIntegrateNum) {
        this.entrustIntegrateNum = entrustIntegrateNum;
    }

    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }

    public String getElementId() {
        return elementId;
    }

    public void setElementId(String elementId) {
        this.elementId = elementId;
    }

    public BigDecimal getSecAcctAvailableCash() {
        return secAcctAvailableCash;
    }

    public void setSecAcctAvailableCash(BigDecimal secAcctAvailableCash) {
        this.secAcctAvailableCash = secAcctAvailableCash;
    }

    public BigDecimal getEntrustIntegrateSecNum() {
        return entrustIntegrateSecNum;
    }

    public void setEntrustIntegrateSecNum(BigDecimal entrustIntegrateSecNum) {
        this.entrustIntegrateSecNum = entrustIntegrateSecNum;
    }

    public BigDecimal getOwnPorAvailableSec() {
        return ownPorAvailableSec;
    }

    public void setOwnPorAvailableSec(BigDecimal ownPorAvailableSec) {
        this.ownPorAvailableSec = ownPorAvailableSec;
    }

    public BigDecimal getOwnProAvailableSec1() {
        return ownProAvailableSec1;
    }

    public void setOwnProAvailableSec1(BigDecimal ownProAvailableSec1) {
        this.ownProAvailableSec1 = ownProAvailableSec1;
    }
    public String getBusiModelStr() {
        if (StringUtil.isEmpty(getBusinessModel())) {
            return null;
        }
        return SysDataUtil.getNameFromDataDict(DictCode.业务模式, getBusinessModel());
    }

    public void setBusiModelStr(String busiModelStr) {
        this.busiModelStr = busiModelStr;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public BigDecimal getPorAvailableSec() {
        return porAvailableSec;
    }

    public void setPorAvailableSec(BigDecimal porAvailableSec) {
        this.porAvailableSec = porAvailableSec;
    }

    public BigDecimal getPorAvailableSec1() {
        return porAvailableSec1;
    }

    public void setPorAvailableSec1(BigDecimal porAvailableSec1) {
        this.porAvailableSec1 = porAvailableSec1;
    }
}
