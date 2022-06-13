package com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "特殊债券下拉框信息查询参数", description = "特殊债券下拉框信息查询参数")
public class FinProductSpecialSelectOptionQueryRequest {

    @ApiModelProperty(name = "search", value = "关键词")
    private String search;

    @ApiModelProperty(required = true, name = "pageSize", value = "每页记录数", example = "20")
    private int pageSize;

    @ApiModelProperty(required = true, name = "pageNo", value = "页码", example = "1")
    private int pageNo;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }
}
