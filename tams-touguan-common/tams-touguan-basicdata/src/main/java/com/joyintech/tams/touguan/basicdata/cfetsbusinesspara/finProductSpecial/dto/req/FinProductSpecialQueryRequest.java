package com.joyintech.tams.touguan.basicdata.cfetsbusinesspara.finProductSpecial.dto.req;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "特殊债券信息查询参数", description = "特殊债券信息查询参数")
public class FinProductSpecialQueryRequest {

    @ApiModelProperty(name = "pageMode", value = "分页对象", example = "{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;

    @ApiModelProperty(name = "finprodMarketId", value = "债券ID/资产代码")
    private String finprodMarketId;

    @ApiModelProperty(name = "finprodAbbr", value = "资产简称")
    private String finprodAbbr;

    @ApiModelProperty(name = "isAbs", value = "是否资产支持证券")
    private String isAbs;

    @ApiModelProperty(name = "isSpecialDebt", value = "是否特殊债")
    private String isSpecialDebt;

    @ApiModelProperty(name = "tenderType", value = "是否到期违约债")
    private String isDefaultBondIndicator;

    @ApiModelProperty(name = "search", value = "关键词")
    private String search;

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getIsAbs() {
        return isAbs;
    }

    public void setIsAbs(String isAbs) {
        this.isAbs = isAbs;
    }

    public String getIsSpecialDebt() {
        return isSpecialDebt;
    }

    public void setIsSpecialDebt(String isSpecialDebt) {
        this.isSpecialDebt = isSpecialDebt;
    }

    public String getIsDefaultBondIndicator() {
        return isDefaultBondIndicator;
    }

    public void setIsDefaultBondIndicator(String isDefaultBondIndicator) {
        this.isDefaultBondIndicator = isDefaultBondIndicator;
    }
}
