package com.joyintech.tams.exchange.common.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value="我发起的协议回购行情请求报文",description="我发起的协议回购行情请求报文")
public class RepurchaseQuoteSendRequest extends BaseRequest {
	private static final long serialVersionUID = 1L;
	
	@ApiModelProperty(required = false, name = "accountId", value = "账户Id", example = "")
	private String accountId;
	
	@ApiModelProperty(required = false, name = "instructionId", value = "指令Id", example = "")
	private String instructionId;
	
	@ApiModelProperty(required = false, name = "orderId", value = "订单Id", example = "o2022030568941")
	private String orderId;

	@ApiModelProperty(required = false, name = "requestStatus", value = "请求状态", example = "3")
	private List<Integer> requestStatus;

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	public String getInstructionId() {
		return instructionId;
	}

	public void setInstructionId(String instructionId) {
		this.instructionId = instructionId;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public List<Integer> getRequestStatus() {
		return requestStatus;
	}

	public void setRequestStatus(List<Integer> requestStatus) {
		this.requestStatus = requestStatus;
	}
}
