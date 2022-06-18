package com.joyintech.tams.business.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *
 * 指令下拉框模糊查询
 */
@ApiModel(value="下拉框请求参数",description="下拉框请求参数")
public class CfetsSearchInfoRequest {

    @ApiModelProperty(name="search",value="模糊查询输入值",example="1")
    private String search;
    @ApiModelProperty(name="pageMode",value="分页对象",example="{pageSize: 10,pageNo: 1}")
    private PageMode pageMode;
    @ApiModelProperty(name="ps",value="交易方向",example="T03")
    private String ps;
    @ApiModelProperty(name="partyId",value="本方/对手方的21位码",example="111")
    private String partyId;
    @ApiModelProperty(name="dealType",value="交易类型",example="04")
    private String dealType;
    @ApiModelProperty(name="searchs",value="模糊查询输入多个值",example="1")
    private String searchs;
    @ApiModelProperty(name="dateType",value="时间类型",example="历史：01")
    private String dateType;

    @ApiModelProperty(name="initValue",value="初始化值",example="历史：01")
    private String initValue;

    @ApiModelProperty(name="isNewBond",value="是否查询新债",example="Y")
    private String isNewBond;

    @ApiModelProperty(name="isOnlyNewBond",value="是否只查询新债",example="Y")
    private String isOnlyNewBond;

    @ApiModelProperty(name="finProductId",value="产品代码",example="历史：01")
    private String finprodId;

    @ApiModelProperty(name="portfolioId",value="组合代码",example="历史：01")
    private String portfolioId;

    private String counterId;

    private Integer page = 1;

    private Integer length = 20;


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

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(String dealType) {
        this.dealType = dealType;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getSearchs() {
        return searchs;
    }

    public void setSearchs(String searchs) {
        this.searchs = searchs;
    }

    public String getDateType() {
        return dateType;
    }

    public void setDateType(String dateType) {
        this.dateType = dateType;
    }

    public String getInitValue() {
        return initValue;
    }

    public void setInitValue(String initValue) {
        this.initValue = initValue;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getIsNewBond() {
        return isNewBond;
    }

    public String getIsOnlyNewBond() {
        return isOnlyNewBond;
    }

    public void setIsOnlyNewBond(String isOnlyNewBond) {
        this.isOnlyNewBond = isOnlyNewBond;
    }

    public void setIsNewBond(String isNewBond) {
        this.isNewBond = isNewBond;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }
}
