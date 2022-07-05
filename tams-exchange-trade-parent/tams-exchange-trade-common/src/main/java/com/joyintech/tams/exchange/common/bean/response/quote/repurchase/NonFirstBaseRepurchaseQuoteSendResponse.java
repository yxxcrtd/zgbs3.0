package com.joyintech.tams.exchange.common.bean.response.quote.repurchase;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "我发起的协议回购(非首期)行情返回报文", description = "我发起的协议回购(非首期)行情返回报文")
public class NonFirstBaseRepurchaseQuoteSendResponse extends BaseRepurchaseQuoteSendResponse{

    @ApiModelProperty(name = "origSerialNo", value = "初始交易编号")
    private String origSerialNo;

    public String getOrigSerialNo() {
        return origSerialNo;
    }

    public void setOrigSerialNo(String origSerialNo) {
        this.origSerialNo = origSerialNo;
    }
}
