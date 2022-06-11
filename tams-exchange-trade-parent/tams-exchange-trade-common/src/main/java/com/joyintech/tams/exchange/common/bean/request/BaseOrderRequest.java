package com.joyintech.tams.exchange.common.bean.request;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.base.MoreObjects;
import com.joyintech.tams.exchange.common.utils.DoubleSerializerUtils;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Digits;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author swwan
 * @date 2022/4/13 - 16:15
 */
@ApiModel(value = "普通下单请求报文", description = "普通下单请求报文")
public class BaseOrderRequest implements Serializable {

    private static final long serialVersionUID = -8488481568354841828L;

    @NotBlank(message = "指令编号不能为空")
    @ApiModelProperty(required = true, name = "instructionId", value = "指令编号", example = "W000111")
    protected String instructionId;

    @ApiModelProperty(required = false, name = "entrustProp", value = "委托属性", example = "0")
    protected String entrustProp;

    @Digits(integer = 20, fraction = 0)
    @ApiModelProperty(required = false, name = "entrustVolume", value = "委托数量", example = "1000")
    protected long entrustVolume;

    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    @ApiModelProperty(required = false, name = "entrustPrice", value = "净价价格", example = "2.34")
    protected BigDecimal entrustPrice;

    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    @ApiModelProperty(required = false, name = "fullPrice", value = "全价价格", example = "2.34")
    protected BigDecimal fullPrice;

    @ApiModelProperty(required = true, name = "entrustBalance", value = "委托金额")
    @Digits(integer = 20, fraction = 4)
    @JsonSerialize(using = DoubleSerializerUtils.class)
    protected BigDecimal entrustBalance;

    @ApiModelProperty(name="softSkipMode",value="软控是否继续？Y：继续",example="Y")
    protected String softSkipMode;

    @ApiModelProperty(name="orderSoftControl",value="下单软控是否继续？Y：继续",example="Y")
    protected String orderSoftControl;

    @ApiModelProperty(name = "isFairOrder", value = "是否公平交易", example = "true|false")
    private boolean isFairOrder;

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

    public long getEntrustVolume() {
        return entrustVolume;
    }

    public void setEntrustVolume(long entrustVolume) {
        this.entrustVolume = entrustVolume;
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

    public BigDecimal getEntrustBalance() {
        return entrustBalance;
    }

    public void setEntrustBalance(BigDecimal entrustBalance) {
        this.entrustBalance = entrustBalance;
    }

    public String getSoftSkipMode() {
        return softSkipMode;
    }

    public void setSoftSkipMode(String softSkipMode) {
        this.softSkipMode = softSkipMode;
    }

    public String getOrderSoftControl() {
        return orderSoftControl;
    }

    public void setOrderSoftControl(String orderSoftControl) {
        this.orderSoftControl = orderSoftControl;
    }

    public boolean isFairOrder() {
        return isFairOrder;
    }

    public void setFairOrder(boolean fairOrder) {
        isFairOrder = fairOrder;
    }
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("instructionId", instructionId)
                .add("entrustProp", entrustProp)
                .add("entrustVolume", entrustVolume)
                .add("entrustPrice", entrustPrice)
                .add("fullPrice", fullPrice)
                .add("entrustBalance", entrustBalance)
                .add("softSkipMode", softSkipMode)
                .add("orderSoftControl", orderSoftControl)
                .toString();
    }
}
