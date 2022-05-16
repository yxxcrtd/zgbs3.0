package com.joyintech.tams.exchange.common.response;

import com.google.common.base.MoreObjects;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value = "SPPI检查", description = "SPPI检查")
public class BidPriceConInfoResponse {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(required = true, name = "status", value = "0 成功；1 失败", example = "0")
    private Integer status;

    @ApiModelProperty(required = true, name = "message", value = "消息", example = "成功")
    private String message;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("status", status)
                .add("message", message)
                .toString();
    }
}
