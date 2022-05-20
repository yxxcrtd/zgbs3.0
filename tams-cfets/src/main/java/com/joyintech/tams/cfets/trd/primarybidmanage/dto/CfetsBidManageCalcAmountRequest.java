package com.joyintech.tams.cfets.trd.primarybidmanage.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * 指令下拉框模糊查询
 */
@ApiModel(value="新建指令金额计算功能请求参数",description="新建指令金额计算功能请求参数")
public class CfetsBidManageCalcAmountRequest {

    @ApiModelProperty(name="allocationDetails",value="分配明细")
    private List<CfetsPrimaryBidManageSaveDetailDto> allocationDetails ;

    @ApiModelProperty(name="winPrinamt",value="中标券面总额")
    private BigDecimal winPrinamt;

    public List<CfetsPrimaryBidManageSaveDetailDto> getAllocationDetails() {
        return allocationDetails;
    }

    public void setAllocationDetails(List<CfetsPrimaryBidManageSaveDetailDto> allocationDetails) {
        this.allocationDetails = allocationDetails;
    }

    public BigDecimal getWinPrinamt() {
        return winPrinamt;
    }

    public void setWinPrinamt(BigDecimal winPrinamt) {
        this.winPrinamt = winPrinamt;
    }
}
