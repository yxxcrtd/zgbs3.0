package com.joyintech.tams.cfets.etr.bondfirstbid.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author: kchen
 * @Date: 2022/4/21 9:51
 */
@ApiModel(value="一级投标待发行信息请求参数",description="一级投标待发行信息请求参数")
public class CfetsEtrIBondFirstBidFindIssueInfoRequest {

    @ApiModelProperty(name="pageMode",value="分页对象",example="{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;

    @ApiModelProperty(name="finprodId",value="资产代码",example="ZC1",notes ="市场代码" )
    private String finprodId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="ZC名")
    private String finprodAbbr;

    @ApiModelProperty(name="issueClass",value="发行方式",example="报价发行/招标发行/簿记建档")
    private String issueClass;

    @ApiModelProperty(name="tenderSdate",value="投标起始日")
    private String tenderSdate;

    @ApiModelProperty(name="tenderEdate",value="投标截止日")
    private String tenderEdate;

    @ApiModelProperty(name="couponSpecies",value="息票品种")
    private String couponSpecies;

    @ApiModelProperty(name="intType",value="利率类型")
    private String intType;

    @ApiModelProperty(name="customAssetType",value="资产分类")
    private String customAssetType;

    @ApiModelProperty(name="search",value="关键字",example="12")
    private String search;

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getIssueClass() {
        return issueClass;
    }

    public void setIssueClass(String issueClass) {
        this.issueClass = issueClass;
    }

    public String getTenderSdate() {
        return tenderSdate;
    }

    public void setTenderSdate(String tenderSdate) {
        this.tenderSdate = tenderSdate;
    }

    public String getTenderEdate() {
        return tenderEdate;
    }

    public void setTenderEdate(String tenderEdate) {
        this.tenderEdate = tenderEdate;
    }

    public String getCouponSpecies() {
        return couponSpecies;
    }

    public void setCouponSpecies(String couponSpecies) {
        this.couponSpecies = couponSpecies;
    }

    public String getIntType() {
        return intType;
    }

    public void setIntType(String intType) {
        this.intType = intType;
    }

    public String getCustomAssetType() {
        return customAssetType;
    }

    public void setCustomAssetType(String customAssetType) {
        this.customAssetType = customAssetType;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
