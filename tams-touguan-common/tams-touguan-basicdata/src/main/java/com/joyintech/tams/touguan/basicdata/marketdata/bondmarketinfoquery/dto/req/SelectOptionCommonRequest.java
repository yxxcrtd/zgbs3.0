package com.joyintech.tams.touguan.basicdata.marketdata.bondmarketinfoquery.dto.req;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 下拉框请求参数
 *
 * @author zhuys
 * @version 2022年4月13日
 */
@ApiModel(value = "查询请求参数", description = "查询请求参数")
public class SelectOptionCommonRequest {

    @ApiModelProperty(name = "search", value = "关键字")
    private String search;

    @ApiModelProperty(name = "pageMode", value = "分页对象")
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
