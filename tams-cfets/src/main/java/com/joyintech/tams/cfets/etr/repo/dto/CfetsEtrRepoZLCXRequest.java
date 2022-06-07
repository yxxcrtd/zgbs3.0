package com.joyintech.tams.cfets.etr.repo.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 *
 * 指令下拉框模糊查询
 */
@ApiModel(value="指令查询入参",description="指令查询入参")
public class CfetsEtrRepoZLCXRequest {

    @ApiModelProperty(name="pageMode",value="分页对象",example="{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;

    @ApiModelProperty(name="portfolioIdList",value="组合代码",example="['1','2']")
    private List<String> portfolioIdList;

    @ApiModelProperty(name="psList",value="交易方向",example="['03','04']")
    private List<String> psList;

    @ApiModelProperty(name="termTypeList",value="期限品种",example="['R001','R007']")
    private List<String> termTypeList;

    @ApiModelProperty(name="entrustStatusList",value="指令状态",example="['1','2']")
    private List<String> entrustStatusList;

    @ApiModelProperty(name="entrustDateStart",value="指令日期",example="2021-11-29")
    private String entrustDateStart;

    @ApiModelProperty(name="entrustDateEnd",value="指令日期",example="2021-11-29")
    private String entrustDateEnd;

    @ApiModelProperty(name="entrustIdList",value="指令编号",example="['ETRREPO202111290001','ETRREPO202111290001'}")
    private List<String>  entrustIdList;

    @ApiModelProperty(name="entrustId",value="指令编号",example="'ETRREPO202111290001'")
    private String  entrustId;

    @ApiModelProperty(name="invManagerIdList",value="投资经理",example="['wp']")
    private List<String> invManagerIdList;

    @ApiModelProperty(name="search",value="关键词",example="wp")
    private String search;

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

    public List<String> getPortfolioIdList() {
        return portfolioIdList;
    }

    public void setPortfolioIdList(List<String> portfolioIdList) {
        this.portfolioIdList = portfolioIdList;
    }

    public List<String> getPsList() {
        return psList;
    }

    public void setPsList(List<String> psList) {
        this.psList = psList;
    }

    public List<String> getTermTypeList() {
        return termTypeList;
    }

    public void setTermTypeList(List<String> termTypeList) {
        this.termTypeList = termTypeList;
    }

    public List<String> getEntrustIdList() {
        return entrustIdList;
    }

    public void setEntrustIdList(List<String> entrustIdList) {
        this.entrustIdList = entrustIdList;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public List<String> getInvManagerIdList() {
        return invManagerIdList;
    }

    public void setInvManagerIdList(List<String> invManagerIdList) {
        this.invManagerIdList = invManagerIdList;
    }

    public List<String> getEntrustStatusList() {
        return entrustStatusList;
    }

    public void setEntrustStatusList(List<String> entrustStatusList) {
        this.entrustStatusList = entrustStatusList;
    }

    public String getEntrustDateStart() {
        return entrustDateStart;
    }

    public void setEntrustDateStart(String entrustDateStart) {
        this.entrustDateStart = entrustDateStart;
    }

    public String getEntrustDateEnd() {
        return entrustDateEnd;
    }

    public void setEntrustDateEnd(String entrustDateEnd) {
        this.entrustDateEnd = entrustDateEnd;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
