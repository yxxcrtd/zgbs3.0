package com.joyintech.tams.cfets.etr.bondfirstbid.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Author: kchen
 * @Date: 2022/4/21 9:51
 */
@ApiModel(value = "一级投标待发行信息请求参数", description = "一级投标待发行信息请求参数")
public class CfetsEtrIBondFirstBidFindIssueInfoRequest {

    @ApiModelProperty(name = "pageMode", value = "分页对象", example = "{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;

    @ApiModelProperty(name = "finprodMarketId", value = "资产代码")
    private String finprodMarketId;

    @ApiModelProperty(name = "finprodId", value = "金融产品代码", example = "ZC1")
    private String finprodId;

    @ApiModelProperty(name = "finprodAbbr", value = "资产简称", example = "ZC名")
    private String finprodAbbr;

    @ApiModelProperty(name = "issueClass", value = "发行方式", example = "01")
    private String issueClass;

    @ApiModelProperty(name = "issueDateStart", value = "投标日/发行日开始日期,字符串", example = "2021-12-21")
    private String issueDateStart;

    @ApiModelProperty(name = "issueDateEnd", value = "投标日/发行日结束日期,字符串", example = "2021-12-21")
    private String issueDateEnd;

    @ApiModelProperty(name = "couponSpecies", value = "息票品种", example = "01")
    private String couponSpecies;

    @ApiModelProperty(name = "intType", value = "利率类型", example = "01")
    private String intType;

    @ApiModelProperty(name = "customAssetType", value = "资产分类")
    private String customAssetType;

    @ApiModelProperty(name = "search", value = "关键字", example = "12")
    private String search;

    @ApiModelProperty(name = "finprodType2", value = "金融产品类型（投管分类）")
    private String finprodType2;

    @ApiModelProperty(name = "platformDate", value = "平台日", example = "12")
    private String platformDate;

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

    public String getIssueDateStart() {
        return issueDateStart;
    }

    public void setIssueDateStart(String issueDateStart) {
        this.issueDateStart = issueDateStart;
    }

    public String getIssueDateEnd() {
        return issueDateEnd;
    }

    public void setIssueDateEnd(String issueDateEnd) {
        this.issueDateEnd = issueDateEnd;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getPlatformDate() {
        return platformDate;
    }

    public void setPlatformDate(String platformDate) {
        this.platformDate = platformDate;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }
}
