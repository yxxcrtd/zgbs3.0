package com.joyintech.tams.business.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 场内指令查询
 * 指令操作记录请求参数DTO
 * @author msk
 * @version 1.0 2021年12月22日
 * @see CommonSearchOperateListRequest
 * @since 1.0
 */
@ApiModel(value="指令操作记录请求参数",description="指令操作记录请求参数")
public class CommonSearchOperateListRequest {

    @ApiModelProperty(name="entrustId",value="指令代码",example="A1",required = true)
    private String entrustId;

    @ApiModelProperty(name="busiType",value="业务类型",example="A1",required = true)
    private String busiType;

    @ApiModelProperty(name="pageMode",value="分页对象")
    private PageMode pageMode;

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }
}

