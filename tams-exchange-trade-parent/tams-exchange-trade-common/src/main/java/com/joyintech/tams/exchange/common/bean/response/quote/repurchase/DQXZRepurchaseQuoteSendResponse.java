package com.joyintech.tams.exchange.common.bean.response.quote.repurchase;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value = "我发起的协议回购(到期续做)行情返回报文", description = "我发起的协议回购(到期续做)行情返回报文")
public class DQXZRepurchaseQuoteSendResponse extends SQRepurchaseQuoteSendResponse{

    @ApiModelProperty(name = "origSerialNo", value = "初始交易编号")
    private String origSerialNo;

    @ApiModelProperty(name = "compactTerm", value = "回购期限")
    private Integer compactTerm;

    @ApiModelProperty(name = "compactTermStr", value = "回购期限（单位：天）")
    private String compactTermStr;

    public String getOrigSerialNo() {
        return origSerialNo;
    }

    public void setOrigSerialNo(String origSerialNo) {
        this.origSerialNo = origSerialNo;
    }

    public Integer getCompactTerm() {
        return compactTerm;
    }

    public void setCompactTerm(Integer compactTerm) {
        this.compactTerm = compactTerm;
    }

    public String getCompactTermStr() {
        return compactTermStr;
    }

    public void setCompactTermStr(String compactTermStr) {
        this.compactTermStr = compactTermStr;
    }
}
