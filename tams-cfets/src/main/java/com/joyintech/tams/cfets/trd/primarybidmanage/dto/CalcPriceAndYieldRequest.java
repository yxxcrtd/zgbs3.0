package com.joyintech.tams.cfets.trd.primarybidmanage.dto;

import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@ApiModel(value = "计算价格和到期收益率请求参数", description = "计算价格和到期收益率请求参数")
public class CalcPriceAndYieldRequest {

    @ApiModelProperty(name = "finprodId", value = "金融产品代码", example = "F01_113052.SH")
    @NotNull(message = "金融产品代码不可为空")
    private String finprodId;

    @ApiModelProperty(name = "prinAmt", value = "默认使用百元券面计算", example = "100")
    private BigDecimal prinAmt;

    @ApiModelProperty(required = true, name = "tradeMarket", value = "交易场所", example = "03")
    @NotNull(message = "交易场所不可为空")
    private String tradeMarket;

    @ApiModelProperty(name = "ps", value = "交易方向", example = "T01")
    @NotNull(message = "交易方向不可为空")
    private String ps;

    /**
     * @see DictCode.债券计算模式
     */
    @ApiModelProperty(name = "calculateField", value = "计算方式", example = "01")
    @NotNull(message = "计算方式不可为空")
    private String calculateField;

    @ApiModelProperty(name = "price", value = "净价", example = "100.00")
    private BigDecimal price;

    @ApiModelProperty(name = "fprice", value = "全价", example = "110.00")
    private BigDecimal fprice;

    @ApiModelProperty(name = "yield", value = "到期收益率", example = "3.58")
    private BigDecimal yield;

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public BigDecimal getPrinAmt() {
        return prinAmt;
    }

    public void setPrinAmt(BigDecimal prinAmt) {
        this.prinAmt = prinAmt;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getCalculateField() {
        return calculateField;
    }

    public void setCalculateField(String calculateField) {
        this.calculateField = calculateField;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getFprice() {
        return fprice;
    }

    public void setFprice(BigDecimal fprice) {
        this.fprice = fprice;
    }

    public BigDecimal getYield() {
        return yield;
    }

    public void setYield(BigDecimal yield) {
        this.yield = yield;
    }
}