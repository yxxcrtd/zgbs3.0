/*
 * 文件名：EtrExchangeTradeQuotation001Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 *
 * 〈功能详细描述〉
 * @author wangyang1
 * @version 1.0 2021年12月21日
 * @see EtrExchangeTradeEtrExchangeSecRepoRequest
 * @since 1.0
 */
@ApiModel(value="指令列表查询请求参数",description="指令列表查询请求参数")
public class EtrExchangeTradeEtrExchangeSecRepoRequest {

    @ApiModelProperty(name="tradePlat",value="交易平台",example="A1")
    private String tradePlat;

    @ApiModelProperty(name="entrustId",value="指令代码",example="A1")
    private String entrustId;

    @ApiModelProperty(name="pageMode",value="分页对象",example="{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;

    @ApiModelProperty(name="entrustStatus",value="指令状态",example="A1")
    private List<String> entrustStatus;

    @ApiModelProperty(name="startDate",value="查询开始日期",example="A1")
    private String startDate;

    @ApiModelProperty(name="endDate",value="查询结束日期",example="A1")
    private String endDate;

    @ApiModelProperty(name="portfolioIdList",value="组合代码",example="A1")
    private List<String> portfolioIdList;

    @ApiModelProperty(name="ps",value="交易方向",example="A1")
    private String ps;

    @ApiModelProperty(name="secId",value="证券代码",example="A1")
    private String secId;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",example="A1")
    private String finprodMarketId;

    @ApiModelProperty(name="finprodType2",value="资产类型",example="A1")
    private String finprodType2;

    @ApiModelProperty(name="invManagerId",value="投资经理",example="A1")
    private String invManagerId;

    @ApiModelProperty(name="entrustTypeList",value="指令类型",example="A1")
    private List<String> entrustTypeList;

    @ApiModelProperty(name="search",value="模糊查询使用",example="xxx")
    private String search;

    @ApiModelProperty(name="finprodMarketIds",value="多个市场代码",example="A,B")
    private String finprodMarketIds;

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }


    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<String> getEntrustStatus() {
        return entrustStatus;
    }

    public void setEntrustStatus(List<String> entrustStatus) {
        this.entrustStatus = entrustStatus;
    }

    public List<String> getPortfolioIdList() {
        return portfolioIdList;
    }

    public void setPortfolioIdList(List<String> portfolioIdList) {
        this.portfolioIdList = portfolioIdList;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getSecId() {
        return secId;
    }

    public void setSecId(String secId) {
        this.secId = secId;
    }

    public String getInvManagerId() {
        return invManagerId;
    }

    public void setInvManagerId(String invManagerId) {
        this.invManagerId = invManagerId;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public List<String> getEntrustTypeList() {
        return entrustTypeList;
    }

    public void setEntrustTypeList(List<String> entrustTypeList) {
        this.entrustTypeList = entrustTypeList;
    }

    /**
     * 从pageMode中获取排序规则
     * @return
     */
    @ApiModelProperty(name="从pageMode获取",value="从pageMode获取")
    public String getSort() {
        return pageMode.getSort();
    }
    @ApiModelProperty(name="从pageMode获取",value="从pageMode获取")
    public String getDirection() {
        return pageMode.getDirection();
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getTradePlat() {
        return tradePlat;
    }

    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodMarketIds() {
        return finprodMarketIds;
    }

    public void setFinprodMarketIds(String finprodMarketIds) {
        this.finprodMarketIds = finprodMarketIds;
    }
}
