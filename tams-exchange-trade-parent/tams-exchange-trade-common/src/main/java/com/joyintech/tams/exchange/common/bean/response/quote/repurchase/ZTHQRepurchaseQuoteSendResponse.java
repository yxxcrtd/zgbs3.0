package com.joyintech.tams.exchange.common.bean.response.quote.repurchase;


import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value = "我发起的协议回购(中途换券)行情返回报文", description = "我发起的协议回购(中途换券)行情返回报文")
public class ZTHQRepurchaseQuoteSendResponse extends NonFirstBaseRepurchaseQuoteSendResponse{

    @ApiModelProperty(name = "discount", value = "当前折算率")
    private BigDecimal discount;

    @ApiModelProperty(name = "exchangeAmount", value = "换出金额")
    private BigDecimal exchangeAmount;

    @ApiModelProperty(name = "swapIntoBonds", value = "换入债券")
    private String swapIntoBonds;

    @ApiModelProperty(name = "swapIntoBondsVolume", value = "换入债券数量")
    private BigDecimal swapIntoBondsVolume;

    @ApiModelProperty(name = "swapOutBonds", value = "换出债券")
    private String swapOutBonds;

    @ApiModelProperty(name = "swapOutBondsVolume", value = "换出债券数量")
    private BigDecimal swapOutBondsVolume;

    @ApiModelProperty(name = "newDiscount", value = "新折算率")
    private BigDecimal newDiscount;


    @ApiModelProperty(name = "discountStr", value = "当前折算率（单位：%，保留两位小数）")
    private String discountStr;
    @ApiModelProperty(name = "exchangeAmountStr", value = "换出金额（单位：元，保留两位小数）")
    private String exchangeAmountStr;
    @ApiModelProperty(name = "swapIntoBondsVolumeStr", value = "换入债券数量（单位：万元，保留两位小数）")
    private String swapIntoBondsVolumeStr;
    @ApiModelProperty(name = "swapOutBondsVolumeStr", value = "换出债券数量（单位：万元，保留两位小数）")
    private String swapOutBondsVolumeStr;
    @ApiModelProperty(name = "newDiscountStr", value = "新折算率（单位：%，保留两位小数）")
    private String newDiscountStr;

    public BigDecimal getDiscount() {
        return discount;
    }

    public void setDiscount(BigDecimal discount) {
        this.discount = discount;
    }

    public BigDecimal getExchangeAmount() {
        return exchangeAmount;
    }

    public void setExchangeAmount(BigDecimal exchangeAmount) {
        this.exchangeAmount = exchangeAmount;
    }

    public String getSwapIntoBonds() {
        return swapIntoBonds;
    }

    public void setSwapIntoBonds(String swapIntoBonds) {
        this.swapIntoBonds = swapIntoBonds;
    }

    public BigDecimal getSwapIntoBondsVolume() {
        return swapIntoBondsVolume;
    }

    public void setSwapIntoBondsVolume(BigDecimal swapIntoBondsVolume) {
        this.swapIntoBondsVolume = swapIntoBondsVolume;
    }

    public String getSwapOutBonds() {
        return swapOutBonds;
    }

    public void setSwapOutBonds(String swapOutBonds) {
        this.swapOutBonds = swapOutBonds;
    }

    public BigDecimal getSwapOutBondsVolume() {
        return swapOutBondsVolume;
    }

    public void setSwapOutBondsVolume(BigDecimal swapOutBondsVolume) {
        this.swapOutBondsVolume = swapOutBondsVolume;
    }

    public BigDecimal getNewDiscount() {
        return newDiscount;
    }

    public void setNewDiscount(BigDecimal newDiscount) {
        this.newDiscount = newDiscount;
    }

    public String getDiscountStr() {
        return discountStr;
    }

    public void setDiscountStr(String discountStr) {
        this.discountStr = discountStr;
    }

    public String getExchangeAmountStr() {
        return exchangeAmountStr;
    }

    public void setExchangeAmountStr(String exchangeAmountStr) {
        this.exchangeAmountStr = exchangeAmountStr;
    }

    public String getNewDiscountStr() {
        return newDiscountStr;
    }

    public void setNewDiscountStr(String newDiscountStr) {
        this.newDiscountStr = newDiscountStr;
    }

    public String getSwapIntoBondsVolumeStr() {
        if (ObjectUtil.isEmpty(this.getSwapIntoBondsVolume())) {
            return null;
        }

        return NumberUtil.div(this.getSwapIntoBondsVolume(), 100,2).toString();
    }

    public String getSwapOutBondsVolumeStr() {
        if (ObjectUtil.isEmpty(this.getSwapOutBondsVolume())) {
            return null;
        }

        return NumberUtil.div(this.getSwapOutBondsVolume(), 100,2).toString();
    }
}
