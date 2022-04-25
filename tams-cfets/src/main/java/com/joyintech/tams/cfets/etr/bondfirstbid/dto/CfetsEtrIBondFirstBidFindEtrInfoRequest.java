package com.joyintech.tams.cfets.etr.bondfirstbid.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * 一级投标指令查询请求参数
 * @Author: kchen
 * @Date: 2022/4/20 11:22
 */
@ApiModel(value="一级投标指令查询请求参数",description="一级投标指令查询请求参数")
public class CfetsEtrIBondFirstBidFindEtrInfoRequest {

    @ApiModelProperty(name="pageMode",value="分页对象",example="{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;

    @ApiModelProperty(name="portfolioId",value="组合名称",example="A1")
    private String portfolioId;

    @ApiModelProperty(name="finprodId",value="资产代码",example="ZC1",notes ="市场代码" )
    private String finprodId;

    @ApiModelProperty(name="finprodAbbr",value="资产简称",example="ZC名")
    private String finprodAbbr;

    @ApiModelProperty(name="issueClass",value="发行方式",example="报价发行/招标发行/簿记建档")
    private String issueClass;

    @ApiModelProperty(name="issueDateStart",value="投标日/发行日开始")
    private Date issueDateStart;

    @ApiModelProperty(name="issueDateEnd",value="投标日/发行日结束")
    private Date issueDateEnd;

    @ApiModelProperty(name="invManagerId",value="投资经理",example="A1")
    private String invManagerId;

    @ApiModelProperty(name="entrustId",value="指令代码",example="A1")
    private String entrustId;

    @ApiModelProperty(name="entrustStartDate",value="指令开始日",example="2021-10-27")
    private Date entrustStartDate;

    @ApiModelProperty(name="entrustEndDate",value="指令结束日",example="2021-10-27")
    private Date entrustEndDate;

    @ApiModelProperty(name="firstBidStatus",value="中标状态",example="A1")
    private String firstBidStatus;

    @ApiModelProperty(name="counterId",value="交易对手",example="A1")
    private String counterId;

    @ApiModelProperty(name="counterName",value="交易对手名称",example="A1")
    private String counterName;

    @ApiModelProperty(name="entrustStatus",value="指令状态",example="[02,03]")
    private List<String> entrustStatus;

    @ApiModelProperty(name="search",value="关键字",example="12")
    private String search;

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

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

    public String getIssueClass() {
        return issueClass;
    }

    public void setIssueClass(String issueClass) {
        this.issueClass = issueClass;
    }

    public String getInvManagerId() {
        return invManagerId;
    }

    public void setInvManagerId(String invManagerId) {
        this.invManagerId = invManagerId;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getFirstBidStatus() {
        return firstBidStatus;
    }

    public void setFirstBidStatus(String firstBidStatus) {
        this.firstBidStatus = firstBidStatus;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }

    public List<String> getEntrustStatus() {
        return entrustStatus;
    }

    public void setEntrustStatus(List<String> entrustStatus) {
        this.entrustStatus = entrustStatus;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public Date getIssueDateStart() {
        return issueDateStart;
    }

    public void setIssueDateStart(Date issueDateStart) {
        this.issueDateStart = issueDateStart;
    }

    public Date getIssueDateEnd() {
        return issueDateEnd;
    }

    public void setIssueDateEnd(Date issueDateEnd) {
        this.issueDateEnd = issueDateEnd;
    }

    public Date getEntrustStartDate() {
        return entrustStartDate;
    }

    public void setEntrustStartDate(Date entrustStartDate) {
        this.entrustStartDate = entrustStartDate;
    }

    public Date getEntrustEndDate() {
        return entrustEndDate;
    }

    public void setEntrustEndDate(Date entrustEndDate) {
        this.entrustEndDate = entrustEndDate;
    }
}
