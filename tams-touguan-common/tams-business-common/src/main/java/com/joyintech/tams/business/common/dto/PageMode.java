package com.joyintech.tams.business.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "分页对象", description = "分页对象")
public class PageMode {

    @ApiModelProperty(required = true, name = "pageSize", value = "每页记录数", example = "20")
    private int pageSize;

    @ApiModelProperty(required = true, name = "pageNo", value = "页码", example = "1")
    private int pageNo;

    @ApiModelProperty(required = true, name = "sort", value = "排序字段", example = "")
    private String sort;

    @ApiModelProperty(required = true, name = "direction", value = "排序方向", example = "DESC|ASC")
    private String direction;

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

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
