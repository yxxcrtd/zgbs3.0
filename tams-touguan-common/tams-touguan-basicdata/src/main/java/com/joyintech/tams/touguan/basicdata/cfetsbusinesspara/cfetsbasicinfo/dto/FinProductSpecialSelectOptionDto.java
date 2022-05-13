package com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.cfetsbasicinfo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 特殊债券下拉框信息
 *
 * @author zhuys
 * @date 2022-05-06
 */
@ApiModel(value = "特殊债券信息", description = "特殊债券信息")
public class FinProductSpecialSelectOptionDto {

    @ApiModelProperty(name = "finprodMarketId", value = "债券代码")
    private String finprodMarketId;

    @ApiModelProperty(name = "finprodAbbr", value = "债券简称")
    private String finprodAbbr;

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }
}