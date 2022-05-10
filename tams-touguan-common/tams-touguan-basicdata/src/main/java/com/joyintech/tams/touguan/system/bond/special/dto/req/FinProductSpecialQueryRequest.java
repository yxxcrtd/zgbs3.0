package com.joyintech.tams.touguan.system.bond.special.dto.req;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "特殊债券信息查询参数", description = "特殊债券信息查询参数")
public class FinProductSpecialQueryRequest {

    @ApiModelProperty(name = "pageMode", value = "分页对象", example = "{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;

    @ApiModelProperty(name = "secId", value = "债券ID/资产代码")
    private String secId;

    @ApiModelProperty(name = "secName", value = "资产简称")
    private String secName;

    @ApiModelProperty(name = "market", value = "市场")
    private String market;

    @ApiModelProperty(name = "search", value = "关键词")
    private String search;

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

    public String getSecId() {
        return secId;
    }

    public void setSecId(String secId) {
        this.secId = secId;
    }

    public String getSecName() {
        return secName;
    }

    public void setSecName(String secName) {
        this.secName = secName;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
