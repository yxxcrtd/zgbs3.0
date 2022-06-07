package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * 分销买入列表DTO
 *
 * @author jiangpeng
 * @date 2022/5/23 14:26
 */
@ApiModel(value = "一级投标确认-分销买入", description = "一级投标 分销买入列表DTO")
public class CfetsTrdFirstBidFirmDistributionInfoRequest {

    @ApiModelProperty(name = "tradeMarkets", value = "交易场所,列表", example = "[01]")
    private List<String> tradeMarkets;

    @ApiModelProperty(name = "finprodIds", value = "资产代码,列表", example = "[F01_001.IB]")
    private List<String> finprodIds;

    //    @ApiModelProperty(name = "finprodMarketIds", value = "市场代码,列表", example = "[001.IB]")
    private List<String> finprodMarketIds;

    //    @ApiModelProperty(name = "finprodAbbrs", value = "资产简称,列表", example = "[001债券]")
    private List<String> finprodAbbrs;

    @ApiModelProperty(name = "tenderTargets", value = "招标标的,列表")
    private List<String> tenderTargets;

    @ApiModelProperty(name = "bidDateStart", value = "投标日开始")
    private Date bidDateStart;

    @ApiModelProperty(name = "bidDateEnd", value = "投标日结束")
    private Date bidDateEnd;

    @ApiModelProperty(name = "bidCfmDateStart", value = "投标确认日开始日")
    private Date bidCfmDateStart;

    @ApiModelProperty(name = "bidCfmDateEnd", value = "投标确认日结束日")
    private Date bidCfmDateEnd;

    @ApiModelProperty(name = "counterIds", value = "交易对手id,列表", example = "[114912384]")
    private List<String> counterIds;

    @ApiModelProperty(name = "bidId", value = "投标编号", example = "111")
    private String bidId;

    @ApiModelProperty(name = "validStatus", value = "有效状态,列表", example = "[01]")
    private List<String> validStatus;

    @ApiModelProperty(name = "pageMode", value = "分页对象", example = "{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;

    @ApiModelProperty(name = "search", value = "关键词")
    private String search;

    public List<String> getTradeMarkets() {
        return tradeMarkets;
    }

    public void setTradeMarkets(List<String> tradeMarkets) {
        this.tradeMarkets = tradeMarkets;
    }

    public List<String> getFinprodIds() {
        return finprodIds;
    }

    public void setFinprodIds(List<String> finprodIds) {
        this.finprodIds = finprodIds;
    }

    public List<String> getFinprodMarketIds() {
        return finprodMarketIds;
    }

    public void setFinprodMarketIds(List<String> finprodMarketIds) {
        this.finprodMarketIds = finprodMarketIds;
    }

    public List<String> getFinprodAbbrs() {
        return finprodAbbrs;
    }

    public void setFinprodAbbrs(List<String> finprodAbbrs) {
        this.finprodAbbrs = finprodAbbrs;
    }

    public List<String> getTenderTargets() {
        return tenderTargets;
    }

    public void setTenderTargets(List<String> tenderTargets) {
        this.tenderTargets = tenderTargets;
    }

    public Date getBidDateStart() {
        return bidDateStart;
    }

    public void setBidDateStart(Date bidDateStart) {
        this.bidDateStart = bidDateStart;
    }

    public Date getBidDateEnd() {
        return bidDateEnd;
    }

    public void setBidDateEnd(Date bidDateEnd) {
        this.bidDateEnd = bidDateEnd;
    }

    public Date getBidCfmDateStart() {
        return bidCfmDateStart;
    }

    public void setBidCfmDateStart(Date bidCfmDateStart) {
        this.bidCfmDateStart = bidCfmDateStart;
    }

    public Date getBidCfmDateEnd() {
        return bidCfmDateEnd;
    }

    public void setBidCfmDateEnd(Date bidCfmDateEnd) {
        this.bidCfmDateEnd = bidCfmDateEnd;
    }

    public List<String> getCounterIds() {
        return counterIds;
    }

    public void setCounterIds(List<String> counterIds) {
        this.counterIds = counterIds;
    }

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    public List<String> getValidStatus() {
        return validStatus;
    }

    public void setValidStatus(List<String> validStatus) {
        this.validStatus = validStatus;
    }

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
}



