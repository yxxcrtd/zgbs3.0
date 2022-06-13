package com.joyintech.tams.cfets.etr.bond.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * 指令下拉框模糊查询
 */
@ApiModel(value="新建指令计算返回数据",description="新建指令计算返回数据")
public class CfetsEtrCalcResponse {

    @ApiModelProperty(name="finprodId",value="资产代码",example="1")
    private String finprodId;
}
