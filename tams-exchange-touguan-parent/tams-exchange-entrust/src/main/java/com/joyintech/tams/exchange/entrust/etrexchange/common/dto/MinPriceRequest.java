package com.joyintech.tams.exchange.entrust.etrexchange.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="查询最小价差",description="查询最小价差请求参数")
public class MinPriceRequest {

    /**
     * 资产代码
     */
    @ApiModelProperty(name="finprodId",value="资产代码",example="A1")
    private String finprodId;
    /**
     * 资产代码
     */
    @ApiModelProperty(name="assetType",value="资产类型",example="A1")
    private String assetType;
    @ApiModelProperty(name="tradePlat",value="交易平台",example="A1")
    private String tradePlat;

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getTradePlat() {
        return tradePlat;
    }

    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat;
    }
}
