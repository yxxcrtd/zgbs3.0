package com.joyintech.service.entrust.common.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * 指令详情
 * @Author: kchen
 * @Date: 2022/6/11 15:34
 */
public class EtrBondFirstBidInfoDto {

    @ApiModelProperty(name = "entrustStatus", value = "指令状态", example = "A1")
    private String entrustStatus;

    @ApiModelProperty(name = "firstBidStatus", value = "中标状态", example = "A1")
    private String firstBidStatus;

    @ApiModelProperty(name = "entrustId", value = "指令编号", example = "A1")
    private String entrustId;

    @ApiModelProperty(name="ps",value="交易方向Code",example="T01")
    private String ps;

    public String getEntrustStatus() {
        return entrustStatus;
    }

    public void setEntrustStatus(String entrustStatus) {
        this.entrustStatus = entrustStatus;
    }

    public String getFirstBidStatus() {
        return firstBidStatus;
    }

    public void setFirstBidStatus(String firstBidStatus) {
        this.firstBidStatus = firstBidStatus;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }
}
