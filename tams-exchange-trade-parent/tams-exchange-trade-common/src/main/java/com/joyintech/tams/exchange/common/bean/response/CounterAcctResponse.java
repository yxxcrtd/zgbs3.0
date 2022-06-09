package com.joyintech.tams.exchange.common.bean.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author lifangpeng
 * @date 2022/5/25 9:56
 */
@ApiModel(value = "查询对手方资金账户响应报文", description = "查询对手方资金账户响应报文")
public class CounterAcctResponse {

    @ApiModelProperty(value = "交易对手代码", example = "C0001")
    private String counterId;

    @ApiModelProperty(value = "资金账户", example = "A0002")
    private String acctNo;

    @ApiModelProperty(value = "资金账户类型", example = "资金账户类型")
    private String acctType;

    @ApiModelProperty(value = "是否开放", example = "Y")
    private String isOpen;

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public String getAcctType() {
        return acctType;
    }

    public void setAcctType(String acctType) {
        this.acctType = acctType;
    }

    public String getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(String isOpen) {
        this.isOpen = isOpen;
    }
}
