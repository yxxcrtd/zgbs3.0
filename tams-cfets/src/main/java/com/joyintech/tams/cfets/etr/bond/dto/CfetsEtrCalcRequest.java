package com.joyintech.tams.cfets.etr.bond.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 *
 * 指令下拉框模糊查询
 */
@ApiModel(value="新建指令计算请求参数",description="新建指令计算请求参数")
public class CfetsEtrCalcRequest {

    @ApiModelProperty(name="settleSpeed",value="清算速度",example="01")
    private String settleSpeed;

    @ApiModelProperty(name="etrDate",value="指令日期",example="2021-10-22")
    private Date etrDate;

    @ApiModelProperty(name="finvdate",value="资产起息日",example="2021-10-22")
    private Date finvdate;

    @ApiModelProperty(name="tradeMarket",value="交易场所",example="01")
    private String tradeMarket;

    @ApiModelProperty(name="finprodId",value="资产代码",example="1")
    private String finprodId;

    @ApiModelProperty(name="ps",value="交易方向",example="01")
    private String ps;

    @ApiModelProperty(name="tradeMarketMode",value="交易市场",example="02")
    private String tradeMarketMode;


}
