package com.joyintech.tams.cfets.trd.repo.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * 指令列表查询条件
 *
 * @author jiangpeng
 * @date 2021/12/9 16:00
 */
@ApiModel(value = "查询条件", description = "本币业务交易-质押式回购指令列表、当日登记、当日报价查询条件")
public class RepoEntrustRequest {

    @ApiModelProperty(name = "pageMode", value = "分页对象", example = "{pageSize: 10,pageNo: 1, sort: 'portfolioId', direction: 'asc'}")
    private PageMode pageMode;

    @ApiModelProperty(name = "entrustStatus", value = "指令状态", example = "['01','02']")
    private List<String> entrustStatus;

    @ApiModelProperty(name = "portfolioId", value = "组合名称", example = "['A','B']")
    private List<String> portfolioId;

    @ApiModelProperty(name = "repoTerm", value = "回购期限", example = "1")
    private String repoTerm;

    @ApiModelProperty(name = "settleSpeedList", value = "清算速度代码List", example = "['02','01']")
    private List<String> settleSpeedList;

    @ApiModelProperty(name = "psList", value = "交易方向代码List", example = "['03','04']")
    private List<String> psList;

    @ApiModelProperty(name = "priceModelList", value = "价格模式代码List", example = "['PRM01','PRM02']")
    private List<String> priceModelList;

    @ApiModelProperty(name = "entrustDate", value = "指令日期")
    private Date entrustDate;

    @ApiModelProperty(name = "entrustDateStart", value = "指令开始日期", example = "2021-07-12")
    private String entrustDateStart;

    @ApiModelProperty(name = "entrustDateEnd", value = "指令结束日期", example = "2021-07-13")
    private String entrustDateEnd;

    @ApiModelProperty(name = "entrustIdList", value = "指令代码List", example = "['WTREPO202107081619']")
    private List<String> entrustIdList;

    @ApiModelProperty(name = "invManagerId", value = "投资经理", example = "['01','02']")
    private List<String> invManagerId;

    @ApiModelProperty(name = "pledgeStockIdList", value = "质押券库IdList", example = "['999999']")
    private List<String> pledgeStockIdList;

    @ApiModelProperty(name = "quoteType", value = "报价方式", example = "A")
    private String quoteType;

    @ApiModelProperty(name = "quoteTypes", value = "报价方式List", example = "['A']")
    private List<String> quoteTypes;

    @ApiModelProperty(name = "quoteId", value = "业务编号/报价编号", example = "A")
    private String quoteId;

    @ApiModelProperty(name = "quoteIds", value = "业务编号/报价编号List", example = "['A']")
    private List<String> quoteIds;

    @ApiModelProperty(name = "search", value = "关键词", example = "xxx")
    private String search;

    @ApiModelProperty(name = "ouTraderNameList", value = "本方交易员List", example = "['A']")
    private List<String> ouTraderNameList;

    @ApiModelProperty(name = "cpPartyNameList", value = "对手方List", example = "['A']")
    private List<String> cpPartyNameList;

    @ApiModelProperty(name = "cpTraderNameList", value = "对手方交易员List", example = "['A']")
    private List<String> cpTraderNameList;

    @ApiModelProperty(name = "dayOfferStatus", value = "成交状态 whole undo done", example = "whole")
    private String dayOfferStatus;

    @ApiModelProperty(name = "entrustId", value = "委托代码", example = "WTREPO202107082043")
    private String entrustId;

    @ApiModelProperty(name = "cfetsQuoteId", value = "CFETS报价编号")
    private String cfetsQuoteId;

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

    public List<String> getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(List<String> portfolioId) {
        this.portfolioId = portfolioId;
    }

    public void setInvManagerId(List<String> invManagerId) {
        this.invManagerId = invManagerId;
    }


    public String getRepoTerm() {
        return repoTerm;
    }

    public void setRepoTerm(String repoTerm) {
        this.repoTerm = repoTerm;
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

    public List<String> getInvManagerId() {
        return invManagerId;
    }

    public Date getEntrustDate() {
        return entrustDate;
    }

    public void setEntrustDate(Date entrustDate) {
        this.entrustDate = entrustDate;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public List<String> getSettleSpeedList() {
        return settleSpeedList;
    }

    public void setSettleSpeedList(List<String> settleSpeedList) {
        this.settleSpeedList = settleSpeedList;
    }

    public List<String> getPsList() {
        return psList;
    }

    public void setPsList(List<String> psList) {
        this.psList = psList;
    }

    public List<String> getPriceModelList() {
        return priceModelList;
    }

    public void setPriceModelList(List<String> priceModelList) {
        this.priceModelList = priceModelList;
    }

    public List<String> getEntrustIdList() {
        return entrustIdList;
    }

    public void setEntrustIdList(List<String> entrustIdList) {
        this.entrustIdList = entrustIdList;
    }

    public String getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public List<String> getPledgeStockIdList() {
        return pledgeStockIdList;
    }

    public void setPledgeStockIdList(List<String> pledgeStockIdList) {
        this.pledgeStockIdList = pledgeStockIdList;
    }

    public List<String> getQuoteTypes() {
        return quoteTypes;
    }

    public void setQuoteTypes(List<String> quoteTypes) {
        this.quoteTypes = quoteTypes;
    }

    public List<String> getQuoteIds() {
        return quoteIds;
    }

    public void setQuoteIds(List<String> quoteIds) {
        this.quoteIds = quoteIds;
    }

    public List<String> getOuTraderNameList() {
        return ouTraderNameList;
    }

    public void setOuTraderNameList(List<String> ouTraderNameList) {
        this.ouTraderNameList = ouTraderNameList;
    }

    public List<String> getCpPartyNameList() {
        return cpPartyNameList;
    }

    public void setCpPartyNameList(List<String> cpPartyNameList) {
        this.cpPartyNameList = cpPartyNameList;
    }

    public List<String> getCpTraderNameList() {
        return cpTraderNameList;
    }

    public void setCpTraderNameList(List<String> cpTraderNameList) {
        this.cpTraderNameList = cpTraderNameList;
    }

    public String getDayOfferStatus() {
        return dayOfferStatus;
    }

    public void setDayOfferStatus(String dayOfferStatus) {
        this.dayOfferStatus = dayOfferStatus;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getCfetsQuoteId() {
        return cfetsQuoteId;
    }

    public void setCfetsQuoteId(String cfetsQuoteId) {
        this.cfetsQuoteId = cfetsQuoteId;
    }
}