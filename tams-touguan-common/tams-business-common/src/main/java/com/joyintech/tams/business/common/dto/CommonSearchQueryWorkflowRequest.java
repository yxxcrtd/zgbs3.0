package com.joyintech.tams.business.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 场外指令分发
 * 指令明细请求参数DTO
 * @author sjj
 * @version 1.0 2021年10月29日
 * @see CommonSearchQueryWorkflowRequest
 * @since 1.0
 */
@ApiModel(value="审批信息请求参数",description="审批信息请求参数")
public class CommonSearchQueryWorkflowRequest {

    @ApiModelProperty(name="busiCode",value="业务编号",example="A1",required = true)
    private String busiCode;

    @ApiModelProperty(name="pageMode",value="分页对象")
    private PageMode pageMode;

    public String getBusiCode() {
        return busiCode;
    }

    public void setBusiCode(String busiCode) {
        this.busiCode = busiCode;
    }

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }
}

