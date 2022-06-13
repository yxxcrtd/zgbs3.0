package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author jiangpeng
 * @date 2022年04月19日 18:49
 */
@ApiModel(value = "一级投标确认-指令信息", description = "投标确认指令信息输入")
public class CfetsTrdFirstBidFirmInitRequest {

    @ApiModelProperty(name = "entrustDtos", value = "指令列表")
    private List<CfetsTrdFirstBidFirmEntrustDto> entrustDtos;

    @ApiModelProperty(name = "workflowId", value = "工作流代码", example = "104123904810")
    private String workflowId;

    @ApiModelProperty(name = "bidId", value = "交易编号,查看页面需要", example = "104123904810")
    private String bidId;

    @ApiModelProperty(name = "flag", value = "指令存在流程中或有效的投标方式为自主投标和代投代缴的投标确认数据,Y/N", example = "Y")
    private String flag;

    @ApiModelProperty(name = "isSearch", value = "是否是指令明细查询,Y/N", example = "Y")
    private String isSearch;

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

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public String getIsSearch() {
        return isSearch;
    }

    public void setIsSearch(String isSearch) {
        this.isSearch = isSearch;
    }
}
