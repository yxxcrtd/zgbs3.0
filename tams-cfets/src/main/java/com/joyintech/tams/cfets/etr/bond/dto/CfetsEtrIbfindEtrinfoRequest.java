package com.joyintech.tams.cfets.etr.bond.dto;


import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value="债券指令查询请求参数",description="债券指令查询请求参数")
public class CfetsEtrIbfindEtrinfoRequest{

    @ApiModelProperty(name="pageMode",value="分页对象",example="{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;

    @ApiModelProperty(name="portfolioId",value="组合名称",example="A1")
    private String portfolioId;

    @ApiModelProperty(name="finprodId",value="资产代码",example="ZC1",notes ="市场代码" )
    private String finprodId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="ZC名")
    private String finprodAbbr;

    @ApiModelProperty(name="ps",value="交易方向Code",example="T01")
    private String ps;

    @ApiModelProperty(name="settleSpeed",value="清算速度Code",example="1")
    private String settleSpeed;

    @ApiModelProperty(name="repManager",value="投资经理",example="A1")
    private String repManager;

    @ApiModelProperty(name="entrustId",value="指令代码",example="A1")
    private String entrustId;

    @ApiModelProperty(name="entrustStartDate",value="指令开始日",example="2021-10-27")
    private String entrustStartDate;

    @ApiModelProperty(name="entrustEndDate",value="指令结束日",example="2021-10-27")
    private String entrustEndDate;

    @ApiModelProperty(name="entrustStatus",value="指令状态",example="[02,03]")
    private List<String> entrustStatus;

    @ApiModelProperty(name="search",value="关键字",example="12")
    private String search;

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
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

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }



    public String getRepManager() {
        return repManager;
    }

    public void setRepManager(String repManager) {
        this.repManager = repManager;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }



    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }


    public List<String> getEntrustStatus() {
        return entrustStatus;
    }

    public void setEntrustStatus(List<String> entrustStatus) {
        this.entrustStatus = entrustStatus;
    }

    public String getEntrustStartDate() {
        return entrustStartDate;
    }

    public void setEntrustStartDate(String entrustStartDate) {
        this.entrustStartDate = entrustStartDate;
    }

    public String getEntrustEndDate() {
        return entrustEndDate;
    }

    public void setEntrustEndDate(String entrustEndDate) {
        this.entrustEndDate = entrustEndDate;
    }

    public String getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
    }


    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }
}
