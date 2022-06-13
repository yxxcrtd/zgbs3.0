package com.joyintech.tams.business.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 头寸信息查询，请求参数
 */
@ApiModel(value = "新建指令发送组合参数", description = "新建指令发送组合参数")
public class CfetsBondPortfolioCashRequest {
    @ApiModelProperty(name = "portfolioId", value = "组合代码 必填", example = "1")
    private String portfolioId;

    @ApiModelProperty(name = "ownSecAmount", value = "本方券券面总额", example = "1")
    private String ownSecAmount;

    @ApiModelProperty(name = "etrComplete", value = "指令完成 必填,参数字典 YES_NO", example = "1")
    private String etrComplete;

    @ApiModelProperty(name = "cashAcctId", value = "资金账号 必填", example = "1")
    private String cashAcctId;

    @ApiModelProperty(name = "integrateSecAmount", value = "融入券券面总额", example = "1")
    private String integrateSecAmount;

    @ApiModelProperty(name = "etrSecAmount", value = "指令券面总额 必填", example = "1")
    private BigDecimal etrSecAmount;

    @ApiModelProperty(name = "secAmount", value = "券面总额 必填", example = "1")
    private BigDecimal secAmount;

    @ApiModelProperty(name = "secAcctId", value = "证券管理账户 必填", example = "1")
    private String secAcctId;

    @ApiModelProperty(name = "invAim", value = "投资目的 必填", example = "1")
    private String invAim;

    @ApiModelProperty(name = "tradeId", value = "交易编号/指令编号 必填", example = "1")
    private String tradeId;

    @ApiModelProperty(name = "cashAmount", value = "交易金额 必填", example = "1")
    private BigDecimal cashAmount;

    @ApiModelProperty(name = "etrId", value = "指令编号 必填", example = "1")
    private String etrId;

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getOwnSecAmount() {
        return ownSecAmount;
    }

    public void setOwnSecAmount(String ownSecAmount) {
        this.ownSecAmount = ownSecAmount;
    }

    public String getEtrComplete() {
        return etrComplete;
    }

    public void setEtrComplete(String etrComplete) {
        this.etrComplete = etrComplete;
    }

    public String getCashAcctId() {
        return cashAcctId;
    }

    public void setCashAcctId(String cashAcctId) {
        this.cashAcctId = cashAcctId;
    }

    public String getIntegrateSecAmount() {
        return integrateSecAmount;
    }

    public void setIntegrateSecAmount(String integrateSecAmount) {
        this.integrateSecAmount = integrateSecAmount;
    }

    public BigDecimal getEtrSecAmount() {
        return etrSecAmount;
    }

    public void setEtrSecAmount(BigDecimal etrSecAmount) {
        this.etrSecAmount = etrSecAmount;
    }

    public BigDecimal getSecAmount() {
        return secAmount;
    }

    public void setSecAmount(BigDecimal secAmount) {
        this.secAmount = secAmount;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getInvAim() {
        return invAim;
    }

    public void setInvAim(String invAim) {
        this.invAim = invAim;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public BigDecimal getCashAmount() {
        return cashAmount;
    }

    /**
     * 交易金额
     * @author jiangpeng
     * @date 2022/5/24 19:29
     * @param cashAmount
     */
    public void setCashAmount(BigDecimal cashAmount) {
        this.cashAmount = cashAmount;
    }

    public String getEtrId() {
        return etrId;
    }

    public void setEtrId(String etrId) {
        this.etrId = etrId;
    }
}
