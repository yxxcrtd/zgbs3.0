package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 一级投标确认操作检查入参明细
 *
 * @author jiangpeng
 * @date 2022/4/27 10:14
 */
@ApiModel(value = "一级投标确认操作检查入参明细", description = "一级投标确认操作检查入参明细,如撤销")
public class CfetsTrdFirstBidFirmCheckDetaileRequst {
    @ApiModelProperty(name = "bidId", value = "投标编号", required = true)
    private String bidId;

    @ApiModelProperty(name = "validStatus", value = "有效状态", required = true)
    private String validStatus;

    @ApiModelProperty(name = "firstBidStatus", value = "投标状态", required = true)
    private String firstBidStatus;

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    public String getValidStatus() {
        return validStatus;
    }

    public void setValidStatus(String validStatus) {
        this.validStatus = validStatus;
    }

    public String getFirstBidStatus() {
        return firstBidStatus;
    }

    public void setFirstBidStatus(String firstBidStatus) {
        this.firstBidStatus = firstBidStatus;
    }
}

