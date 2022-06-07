package com.joyintech.tams.cfets.trd.bond.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author zznn
 * @date 2021/11/10
 */
@ApiModel(value="债券报价请求参数",description="债券报价请求参数")
public class CfetsBondDealQuoteRequest {

    @ApiModelProperty(required = false,name="quoteId",value="报价编号")
    private String quoteId;

    @ApiModelProperty(required = false,name="entrustIdList",value="委托编号")
    private List<String> entrustIdList;

    @ApiModelProperty(required = false,name="workflowId",value="工作流代码")
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
}

