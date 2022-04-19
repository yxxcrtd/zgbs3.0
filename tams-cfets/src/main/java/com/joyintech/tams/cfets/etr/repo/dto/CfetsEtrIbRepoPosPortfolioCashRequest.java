package com.joyintech.tams.cfets.etr.repo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 头寸信息查询，请求参数
 */
@ApiModel(value="新建指令发送组合参数",description="新建指令发送组合参数")
public class CfetsEtrIbRepoPosPortfolioCashRequest {
    @ApiModelProperty(name="portfolioId",value="组合代码",example="1")
    private String portfolioId;

    @ApiModelProperty(name="tradeId",value="交易编号/指令编号",example="1")
    private String tradeId;

    @ApiModelProperty(name="cashAcctId",value="资金账号",example="1")
    private String cashAcctId;

    @ApiModelProperty(name="cashAmount",value="交易金额",example="1")
    private BigDecimal cashAmount;

    @ApiModelProperty(name="etrId",value="指令编号",example="1")
    private String etrId;

	public String getPortfolioId() {
		return portfolioId;
	}

	public void setPortfolioId(String portfolioId) {
		this.portfolioId = portfolioId;
	}

	public String getTradeId() {
		return tradeId;
	}

	public void setTradeId(String tradeId) {
		this.tradeId = tradeId;
	}

	public String getCashAcctId() {
		return cashAcctId;
	}

	public void setCashAcctId(String cashAcctId) {
		this.cashAcctId = cashAcctId;
	}

	public BigDecimal getCashAmount() {
		return cashAmount;
	}

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
