package com.joyintech.tams.exchange.common.bean.response.quote.repurchase;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value = "我发起的协议回购(解除质押)行情返回报文", description = "我发起的协议回购(解除质押)行情返回报文")
public class JCZYRepurchaseQuoteSendResponse extends NonFirstBaseRepurchaseQuoteSendResponse{

    @ApiModelProperty(name = "settleAmount", value = "结算金额")
    private BigDecimal settleAmount;

    public BigDecimal getSettleAmount() {
        return settleAmount;
    }

    public void setSettleAmount(BigDecimal settleAmount) {
        this.settleAmount = settleAmount;
    }

    @ApiModelProperty(name = "settleAmountStr", value = "结算金额（单位：元，保留两位小数）")
    private String settleAmountStr;

    public String getSettleAmountStr() {
        return settleAmountStr;
    }

    public void setSettleAmountStr(String settleAmountStr) {
        this.settleAmountStr = settleAmountStr;
    }
}
