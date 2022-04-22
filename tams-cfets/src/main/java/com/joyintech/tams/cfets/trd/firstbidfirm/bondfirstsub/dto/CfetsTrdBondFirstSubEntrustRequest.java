package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstsub.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "指令查询条件", description = "一级申购指令查询条件")
public class CfetsTrdBondFirstSubEntrustRequest {

    @ApiModelProperty(name = "pageMode", value = "分页对象", example = "{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;

    @ApiModelProperty(name = "entrustStatus", value = "指令状态,列表", example = "[01,02]")
    private List<String> entrustStatus;

    @ApiModelProperty(name = "portfolioIds", value = "组合名称,列表", example = "[A,B]")
    private List<String> portfolioIds;

    @ApiModelProperty(name = "finprodMarketIds", value = "市场代码,列表 etrAddSearch接口必传", example = "[A,B]")
    private List<String> finprodMarketIds;

    @ApiModelProperty(name = "secManageAcctIds ", value = "证券账户,列表 etrAddSearch接口必传", example = "[A,B]")
    private List<String> secManageAcctIds;

    @ApiModelProperty(name = "entrustDateStart", value = "指令开始日期,字符串", example = "2021-12-21")
    private String entrustDateStart;

    @ApiModelProperty(name = "entrustDateEnd", value = "指令结束日期,字符串", example = "2021-12-21")
    private String entrustDateEnd;

    @ApiModelProperty(name = "issueDateStart", value = "发行开始日期,字符串", example = "2021-12-21")
    private String issueDateStart;

    @ApiModelProperty(name = "issueDateEnd", value = "发行结束日期,字符串", example = "2021-12-21")
    private String issueDateEnd;

    @ApiModelProperty(name = "entrustId", value = "指令代码,字符串", example = "WTBOND20220418")
    private String entrustId;

    @ApiModelProperty(name = "entrustIds ", value = "指令代码集合,列表", example = "[WTBOND2022041801,WTBOND2022041802]")
    private List<String> entrustIds;

    @ApiModelProperty(name = "invManagerIds ", value = "投资经理集合", example = "[01,02]")
    private List<String> invManagerIds;

    @ApiModelProperty(name = "search", value = "关键词")
    private String search;

    @ApiModelProperty(name = "existEntrustIds ", value = "etrAddSearch接口,已存在的指令代码,用于过滤申购产品列表中已添加的指令", example = "[01,02]")
    private List<String> existEntrustIds;

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

    public List<String> getPortfolioIds() {
        return portfolioIds;
    }

    public void setPortfolioIds(List<String> portfolioIds) {
        this.portfolioIds = portfolioIds;
    }


    public List<String> getEntrustIds() {
        return entrustIds;
    }

    public void setEntrustIds(List<String> entrustIds) {
        this.entrustIds = entrustIds;
    }

    public List<String> getInvManagerIds() {
        return invManagerIds;
    }

    public void setInvManagerIds(List<String> invManagerIds) {
        this.invManagerIds = invManagerIds;
    }


    public List<String> getFinprodMarketIds() {
        return finprodMarketIds;
    }

    public void setFinprodMarketIds(List<String> finprodMarketIds) {
        this.finprodMarketIds = finprodMarketIds;
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


    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }


    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public List<String> getSecManageAcctIds() {
        return secManageAcctIds;
    }

    public void setSecManageAcctIds(List<String> secManageAcctIds) {
        this.secManageAcctIds = secManageAcctIds;
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
}