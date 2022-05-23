package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * 分销买入列表DTO
 *
 * @author jiangpeng
 * @date 2022/5/23 14:26
 */
@ApiModel(value = "一级投标确认-分销买入", description = "一级投标 分销买入列表DTO")
public class CfetsTrdFirstBidFirmDistributionInfoRequest {

    @ApiModelProperty(name = "tradeMarket", value = "交易场所", example = "01")
    private String tradeMarket;

    @ApiModelProperty(name = "finprodId", value = "资产代码", example = "F01_001.IB")
    private String finprodId;

    @ApiModelProperty(name = "finprodMarketId", value = "市场代码", example = "001.IB")
    private String finprodMarketId;

    @ApiModelProperty(name = "finprodAbbr", value = "资产简称", example = "001债券")
    private String finprodAbbr;

    @ApiModelProperty(name = "tenderTarget", value = "招标标的")
    private String tenderTarget;

    @ApiModelProperty(name = "bidDateStart", value = "投标日开始")
    private Date bidDateStart;

    @ApiModelProperty(name = "bidDateEnd", value = "投标日结束")
    private Date bidDateEnd;

    @ApiModelProperty(name = "bidCfmDateStart", value = "投标确认日开始日")
    private Date bidCfmDateStart;

    @ApiModelProperty(name = "bidCfmDateEnd", value = "投标确认日结束日")
    private Date bidCfmDateEnd;

    @ApiModelProperty(name = "counterId", value = "交易对手id", example = "114912384")
    private String counterId;

    @ApiModelProperty(name = "bidId", value = "投标编号", example = "111")
    private String bidId;

    @ApiModelProperty(name = "validStatus", value = "有效状态", example = "01")
    private String validStatus;

    @ApiModelProperty(name = "pageMode", value = "分页对象", example = "{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;

    @ApiModelProperty(name = "search", value = "关键词")
    private String search;

    public String getValidStatus() {
        return validStatus;
    }

    public void setValidStatus(String validStatus) {
        this.validStatus = validStatus;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
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

    public String getTenderTarget() {
        return tenderTarget;
    }

    public void setTenderTarget(String tenderTarget) {
        this.tenderTarget = tenderTarget;
    }

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
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



