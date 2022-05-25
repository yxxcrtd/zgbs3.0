package com.joyintech.tams.exchange.common.bean.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author lifangpeng
 * @date 2022/5/25 9:47
 */
@ApiModel(value = "查询对手方资金账户信息报文", description = "查询对手方资金账户信息报文")
public class CounterAcctRequest {

    @NotBlank(message = "交易对手代码不可为空")
    @ApiModelProperty(value = "交易对手代码", example = "C0001")
    private String counterId;

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }
}
