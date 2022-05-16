package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author jiangpeng
 * @date 2022年04月19日 18:49
 */
@ApiModel(value = "指令信息", description = "投标确认指令信息输入")
public class CfetsTrdFirstBidFirmInitRequest {

    @ApiModelProperty(name = "entrustDtos", value = "指令列表")
    private List<CfetsTrdFirstBidFirmEntrustDto> entrustDtos;

    @ApiModelProperty(name = "workflowId", value = "工作流代码", example = "104123904810")
    private String workflowId;

    @ApiModelProperty(name = "bidId", value = "交易编号,申购交易查看", example = "104123904810")
    private String bidId;

    public List<CfetsTrdFirstBidFirmEntrustDto> getEntrustDtos() {
        return entrustDtos;
    }

    public void setEntrustDtos(List<CfetsTrdFirstBidFirmEntrustDto> entrustDtos) {
        this.entrustDtos = entrustDtos;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }
}
