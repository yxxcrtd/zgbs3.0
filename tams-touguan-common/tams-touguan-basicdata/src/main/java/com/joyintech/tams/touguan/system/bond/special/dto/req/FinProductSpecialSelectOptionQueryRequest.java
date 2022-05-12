package com.joyintech.tams.touguan.system.bond.special.dto.req;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "特殊债券下拉框信息查询参数", description = "特殊债券下拉框信息查询参数")
public class FinProductSpecialSelectOptionQueryRequest {

    @ApiModelProperty(name = "search", value = "关键词")
    private String search;

    @ApiModelProperty(name = "pageMode", value = "分页对象", example = "{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }
}
