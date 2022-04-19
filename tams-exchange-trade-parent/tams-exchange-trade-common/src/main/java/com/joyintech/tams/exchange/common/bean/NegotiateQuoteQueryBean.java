package com.joyintech.tams.exchange.common.bean;


import org.hibernate.validator.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "我收到的协商成交行情请求报文", description = "我收到的协商成交行情请求报文")
public class NegotiateQuoteQueryBean {
	
	@ApiModelProperty(name = "requestStatus", value = "请求状态(1：待确认 2：已回复 3：已撤销 4：已成交 )")
    private int requestStatus;
    @ApiModelProperty(name = "stockCode", value = "证券代码")
    private String stockCode;
    
    @ApiModelProperty(name = "side", value = "买卖方向")
    private String side;
    
    @ApiModelProperty(name = "traderCode", value = "本方交易员代码")
    private String traderCode;
    
    @ApiModelProperty(name = "investorId", value = "本方交易主体代码")
    private String investorId;
    
    @ApiModelProperty(name = "confirmId", value = "约定号(当交易主体类型为03或04时必须送值)")
    private String confirmId;
    
    @ApiModelProperty(name = "exchangeType", value = "交易类别（1-上海，2-深圳）", example = "2")
    @NotBlank(message = "交易类别不能为空")
    private String exchangeType;

    @ApiModelProperty(required = true, name = "accountId", value = "网点账号", example = "A0001:12345")
    @NotBlank(message = "网点账号不能为空")
    private String accountId;

    @ApiModelProperty(name = "pageSize", value = "记录数", example = "1000")
    private int pageSize;
    
	public int getRequestStatus() {
		return requestStatus;
	}
	public void setRequestStatus(int requestStatus) {
		this.requestStatus = requestStatus;
	}
	public String getStockCode() {
		return stockCode;
	}
	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}
	public String getExchangeType() {
		return exchangeType;
	}
	public void setExchangeType(String exchangeType) {
		this.exchangeType = exchangeType;
	}
	public String getAccountId() {
		return accountId;
	}
	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public String getSide() {
		return side;
	}
	public void setSide(String side) {
		this.side = side;
	}
	public String getTraderCode() {
		return traderCode;
	}
	public void setTraderCode(String traderCode) {
		this.traderCode = traderCode;
	}
	public String getInvestorId() {
		return investorId;
	}
	public void setInvestorId(String investorId) {
		this.investorId = investorId;
	}
	public String getConfirmId() {
		return confirmId;
	}
	public void setConfirmId(String confirmId) {
		this.confirmId = confirmId;
	}
    
}
