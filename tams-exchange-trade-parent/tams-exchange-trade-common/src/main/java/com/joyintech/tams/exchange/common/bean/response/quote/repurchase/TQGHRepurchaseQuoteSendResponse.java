package com.joyintech.tams.exchange.common.bean.response.quote.repurchase;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "我发起的协议回购(提前购回)行情返回报文", description = "我发起的协议回购(提前购回)行情返回报文")
public class TQGHRepurchaseQuoteSendResponse extends SQRepurchaseQuoteSendResponse{

    @ApiModelProperty(name = "origSerialNo", value = "初始交易编号")
    private String origSerialNo;

    @ApiModelProperty(name = "repoAdvanceDate", value = "提前购回日")
    private Integer repoAdvanceDate;

    public String getOrigSerialNo() {
        return origSerialNo;
    }

    public void setOrigSerialNo(String origSerialNo) {
        this.origSerialNo = origSerialNo;
    }

    public Integer getRepoAdvanceDate() {
        return repoAdvanceDate;
    }

    public void setRepoAdvanceDate(Integer repoAdvanceDate) {
        this.repoAdvanceDate = repoAdvanceDate;
    }
}
