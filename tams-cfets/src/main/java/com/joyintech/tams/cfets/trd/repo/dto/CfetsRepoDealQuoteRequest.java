package com.joyintech.tams.cfets.trd.repo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 质押式回购报价请求参数
 *
 * @author jiangpeng
 * @date 2021/12/6 17:17
 */
@ApiModel(value = "质押式回购报价请求参数", description = "质押式回购报价请求参数")
public class CfetsRepoDealQuoteRequest {

    @ApiModelProperty(required = false, name = "quoteId", value = "报价编号")
    private String quoteId;

    @ApiModelProperty(required = false, name = "quoteType", value = "交易类型")
    private String quoteType;

    @ApiModelProperty(required = false, name = "entrustIdList", value = "指令代码列表", example = "['WTREPO202107081619']")
    private List<String> entrustIdList;

    @ApiModelProperty(required = false, name = "workflowId", value = "工作流代码")
    private String workflowId;

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public List<String> getEntrustIdList() {
        return entrustIdList;
    }

    public void setEntrustIdList(List<String> entrustIdList) {
        this.entrustIdList = entrustIdList;
    }

    public String getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(String workflowId) {
        this.workflowId = workflowId;
    }

    public String getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }
}

