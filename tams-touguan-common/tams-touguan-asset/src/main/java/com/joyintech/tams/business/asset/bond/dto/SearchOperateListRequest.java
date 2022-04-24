package com.joyintech.tams.business.asset.bond.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 操作记录请求参数DTO
 * @author wangyang1
 * @version 1.0 2022年4月14日
 * @see SearchOperateListRequest
 * @since 1.0
 */
@ApiModel(value="操作记录请求参数",description="操作记录请求参数")
public class SearchOperateListRequest {

    @ApiModelProperty(name="entrustId",value="资产代码",example="A1",required = true)
    private String entrustId;

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
}

