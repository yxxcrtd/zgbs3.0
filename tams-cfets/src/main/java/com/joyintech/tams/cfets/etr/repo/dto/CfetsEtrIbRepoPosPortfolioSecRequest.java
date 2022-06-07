package com.joyintech.tams.cfets.etr.repo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

/**
 * 头寸信息查询，请求参数
 */
@ApiModel(value="新建指令发送组合参数",description="新建指令发送组合参数")
public class CfetsEtrIbRepoPosPortfolioSecRequest {
    @ApiModelProperty(name="portfolioId",value="组合代码",example="1")
    private String portfolioId;

    @ApiModelProperty(name="tradeId",value="交易编号/指令编号",example="1")
    private String tradeId;

    @ApiModelProperty(name="secAcctId",value="证券管理账户/通道代码",example="1")
    private String secAcctId;

    @ApiModelProperty(name="secAmount",value="券面总额",example="1")
    private BigDecimal secAmount;

    @ApiModelProperty(name="etrId",value="指令编号",example="1")
    private String etrId;
    
    @ApiModelProperty(name="finprodId",value="债券代码",example="1")
    private String finprodId;

    @ApiModelProperty(name="integrateSecAmount",value="融入券券面总额",example="1")
    private BigDecimal integrateSecAmount;

    @ApiModelProperty(name="ownSecAmount",value="本方券券面总额",example="1")
    private BigDecimal ownSecAmount;

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

	public String getSecAcctId() {
		return secAcctId;
	}

	public void setSecAcctId(String secAcctId) {
		this.secAcctId = secAcctId;
	}

	public BigDecimal getSecAmount() {
		return secAmount;
	}

	public void setSecAmount(BigDecimal secAmount) {
		this.secAmount = secAmount;
	}

	public String getEtrId() {
		return etrId;
	}

	public void setEtrId(String etrId) {
		this.etrId = etrId;
	}

	public String getFinprodId() {
		return finprodId;
	}

	public void setFinprodId(String finprodId) {
		this.finprodId = finprodId;
	}

	public BigDecimal getIntegrateSecAmount() {
		return integrateSecAmount;
	}

	public void setIntegrateSecAmount(BigDecimal integrateSecAmount) {
		this.integrateSecAmount = integrateSecAmount;
	}

	public BigDecimal getOwnSecAmount() {
		return ownSecAmount;
	}

	public void setOwnSecAmount(BigDecimal ownSecAmount) {
		this.ownSecAmount = ownSecAmount;
	}

}
