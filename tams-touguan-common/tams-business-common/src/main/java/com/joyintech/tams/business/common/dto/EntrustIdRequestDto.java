package com.joyintech.tams.business.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="指令代码列表查询请求参数",description="指令代码列表查询请求参数")
public class EntrustIdRequestDto {

    @ApiModelProperty(name="etrId",value="指令代码",example="A1")
    private String etrId;

    @ApiModelProperty(name="pageMode",value="分页对象")
    private PageMode pageMode;

    public String getEtrId() {
        return etrId;
    }

    public void setEtrId(String etrId) {
        this.etrId = etrId;
    }

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }
}
