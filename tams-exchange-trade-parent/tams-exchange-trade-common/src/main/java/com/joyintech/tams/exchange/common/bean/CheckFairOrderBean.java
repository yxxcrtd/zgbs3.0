package com.joyintech.tams.exchange.common.bean;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.tams.exchange.common.utils.DoubleSerializerUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Digits;
import java.math.BigDecimal;

/**
 * @author lifangpeng
 * @date 2022/5/31 21:46
 */
@ApiModel(value = "判断是否可以做公平交易请求报文", description = "判断是否可以做公平交易请求报文")
public class CheckFairOrderBean {

    @NotBlank(message = "指令编号不能为空")
    @ApiModelProperty(required = true, name = "instructionId", value = "指令编号", example = "W000111")
    protected String instructionId;

    @ApiModelProperty(name = "entrustProp", value = "委托属性", example = "0")
    protected String entrustProp;

    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    @ApiModelProperty(name = "entrustPrice", value = "净价价格", example = "2.34")
    protected BigDecimal entrustPrice;

    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    @ApiModelProperty(name = "fullPrice", value = "全价价格", example = "2.34")
    protected BigDecimal fullPrice;

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

    public BigDecimal getEntrustPrice() {
        return entrustPrice;
    }

    public void setEntrustPrice(BigDecimal entrustPrice) {
        this.entrustPrice = entrustPrice;
    }

    public BigDecimal getFullPrice() {
        return fullPrice;
    }

    public void setFullPrice(BigDecimal fullPrice) {
        this.fullPrice = fullPrice;
    }
}
