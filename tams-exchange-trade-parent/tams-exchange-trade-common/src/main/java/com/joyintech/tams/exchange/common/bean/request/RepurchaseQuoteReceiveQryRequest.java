package com.joyintech.tams.exchange.common.bean.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

@ApiModel(value = "我收到的质押式协议回购行情请求报文", description = "我收到的质押式协议回购行情请求报文")
public class RepurchaseQuoteReceiveQryRequest {
    @ApiModelProperty(required = true, name = "instructionId", value = "指令编号", example = "FUND000121")
    @NotBlank(message = "指令编号不能为空")
    private String instructionId;

    @ApiModelProperty(name = "entrustProp", value = "委托属性", example = "BPB", hidden = true)
    private String entrustProp;

    @ApiModelProperty(required = true, name = "accountId", value = "网点账号", example = "A0001:12345")
//    @NotBlank(message = "网点账号不能为空")
    private String accountId;

    @ApiModelProperty(name = "pageSize", value = "记录数", example = "100")
    private int pageSize;

    public RepurchaseQuoteReceiveQryRequest() {
    }

    public RepurchaseQuoteReceiveQryRequest(String entrustProp, String accountId, int pageSize) {
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
}
