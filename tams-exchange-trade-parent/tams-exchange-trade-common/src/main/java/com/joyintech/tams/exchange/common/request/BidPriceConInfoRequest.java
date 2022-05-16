package com.joyintech.tams.exchange.common.request;

import com.google.common.base.MoreObjects;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;

@ApiModel(value = "SPPI检查请求报文", description = "SPPI检查请求报文")
public class BidPriceConInfoRequest {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(required = true, name = "ps", value = "交易方向", example = "T01")
    private String ps;

    @ApiModelProperty(required = true, name = "finprodId", value = "资产代码", example = "F01_100303.SZ")
    private String finprodId;

    @ApiModelProperty(required = true, name = "businessModel", value = "投资目的", example = "AC")
    private String businessModel;

    @ApiModelProperty(required = true, name = "price", value = "价格", example = "10.01")
    private BigDecimal price;

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getBusinessModel() {
        return businessModel;
    }

    public void setBusinessModel(String businessModel) {
        this.businessModel = businessModel;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("ps", ps)
                .add("finprodId", finprodId)
                .add("businessModel", businessModel)
                .add("price", price)
                .toString();
    }
}
