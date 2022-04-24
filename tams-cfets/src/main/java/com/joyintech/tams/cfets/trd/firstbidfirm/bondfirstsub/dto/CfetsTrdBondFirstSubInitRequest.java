package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author jiangpeng
 * @date 2022年04月19日 18:49
 */
@ApiModel(value = "指令信息", description = "一级申购指令信息输入")
public class CfetsTrdBondFirstSubInitRequest {

    @ApiModelProperty(name = "entrustDtos", value = "指令列表")
    private List<CfetsTrdBondFirstSubEntrustDto> entrustDtos;

    @ApiModelProperty(name = "workflowId", value = "工作流代码", example = "104123904810")
    private String workflowId;

    public List<CfetsTrdBondFirstSubEntrustDto> getEntrustDtos() {
        return entrustDtos;
    }

    public void setEntrustDtos(List<CfetsTrdBondFirstSubEntrustDto> entrustDtos) {
        this.entrustDtos = entrustDtos;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }
}
