package com.joyintech.tams.exchange.common.bean.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @author lifangpeng
 * @date 2022/5/14 11:05
 */
@ApiModel(value = "检查本方交易商交易员信息报文", description = "检查本方交易商交易员信息报文")
public class CheckTradeInfoRequest {

    @NotBlank(message = "交易场所不可为空")
    @ApiModelProperty(value = "资金账户", example = "A0001")
    private String acctNo;

    @NotBlank(message = "交易场所不可为空")
    @ApiModelProperty(value = "交易场所,03|04", example = "03")
    private String tradePlace;

    @ApiModelProperty(value = "本方标识,Y|N", example = "Y")
    private String isOur;

    public String getAcctNo() {
        return acctNo;
    }

    public void setAcctNo(String acctNo) {
        this.acctNo = acctNo;
    }

    public String getTradePlace() {
        return tradePlace;
    }

    public void setTradePlace(String tradePlace) {
        this.tradePlace = tradePlace;
    }

    public String getIsOur() {
        return isOur;
    }

    public void setIsOur(String isOur) {
        this.isOur = isOur;
    }
}
