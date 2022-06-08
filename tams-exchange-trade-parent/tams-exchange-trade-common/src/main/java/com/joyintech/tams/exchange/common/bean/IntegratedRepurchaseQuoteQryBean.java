package com.joyintech.tams.exchange.common.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

@ApiModel(value = "质押式协议回购行情请求报文", description = "质押式协议回购行情请求报文")
public class IntegratedRepurchaseQuoteQryBean {
    @ApiModelProperty(required = true, name = "instructionId", value = "指令编号", example = "FUND000121")
    @NotBlank(message = "指令编号不能为空")
    private String instructionId;

    @ApiModelProperty(required = true, name = "actionIn", value = "行情类别（0-公开行情，1-非公开行情）", example = "1")
    private int actionIn;

    @ApiModelProperty(name = "entrustProp", value = "委托属性", example = "BPB", hidden = true)
    private String entrustProp;

    @ApiModelProperty(required = true, name = "accountId", value = "网点账号", example = "A0001:12345")
    private String accountId;

    @ApiModelProperty(name = "pageSize", value = "记录数", example = "100")
    private int pageSize;
    
    @ApiModelProperty(name = "market", value = "交易市场(SH:上海证券交易所,SZ:深圳证券交易所)", example = "SH")
    private String market;
    
    @ApiModelProperty(name = "quoteRequestId", value = "行情请求编号", example = "test001")
    private String quoteRequestId;
    
    @ApiModelProperty(name = "orderType", value = "首期-F 到期续做-X 解除质押-C 中途换券-H 提前购回-Z", example = "F")
    private String orderType;

    public IntegratedRepurchaseQuoteQryBean() {
    }

    public IntegratedRepurchaseQuoteQryBean(int actionIn, String entrustProp, String accountId, int pageSize) {
        this.actionIn = actionIn;
        this.entrustProp = entrustProp;
        this.accountId = accountId;
        this.pageSize = pageSize;
    }

    public String getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(String instructionId) {
        this.instructionId = instructionId;
    }

    public int getActionIn() {
        return actionIn;
    }

    public void setActionIn(int actionIn) {
        this.actionIn = actionIn;
    }

    public String getEntrustProp() {
        return entrustProp;
    }

    public void setEntrustProp(String entrustProp) {
        this.entrustProp = entrustProp;
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

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getQuoteRequestId() {
		return quoteRequestId;
	}

	public void setQuoteRequestId(String quoteRequestId) {
		this.quoteRequestId = quoteRequestId;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
}
