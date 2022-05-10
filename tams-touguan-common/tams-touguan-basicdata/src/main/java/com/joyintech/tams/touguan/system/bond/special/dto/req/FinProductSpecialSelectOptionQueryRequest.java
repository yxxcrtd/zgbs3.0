package com.joyintech.tams.touguan.system.bond.special.dto.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "特殊债券下拉框信息查询参数", description = "特殊债券下拉框信息查询参数")
public class FinProductSpecialSelectOptionQueryRequest {

    @ApiModelProperty(name = "search", value = "关键词")
    private String search;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
