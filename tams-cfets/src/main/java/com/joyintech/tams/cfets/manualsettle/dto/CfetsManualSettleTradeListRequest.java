package com.joyintech.tams.cfets.manualsettle.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 银行间手工交收
 * 银行间手工交收分页查询条件DTO
 * @author sjj
 * @version 1.0 2021年11月12日
 * @see CfetsManualSettleTradeListRequest
 * @since 1.0
 */

@ApiModel(value="银行间手工交收列表请求参数",description="银行间手工交收列表请求参数")
public class CfetsManualSettleTradeListRequest {

    @ApiModelProperty(name="settleStatus",value="交收状态",example="[未交收],[已交收]",required = true)
    private String[] settleStatus;

    @ApiModelProperty(name="tradeId",value="原业务编号",example="A1")
    private String tradeId;

    @ApiModelProperty(name="finprodMarketId",value="资产简称集合",example="字符串1,字符串2,...")
    private String finprodMarketId;

    @ApiModelProperty(name="portfolioId",value="组合名称集合",example="字符串1,字符串2,...")
    private String portfolioId;

    @ApiModelProperty(name="assetType",value="资产类型",example="A1")
    private String assetType;

    @ApiModelProperty(name="tgBusiType",value="业务类型",example="A1")
    private String tgBusiType;

    @ApiModelProperty(name="ps",value="交易方向",example="A1")
    private String ps;

    @ApiModelProperty(name="cleanStatus",value="清算状态",example="A1,A2")
    private String cleanStatus;

    @ApiModelProperty(name="tradeDate",value="交易日期",example="2021-01-01")
    private String[] tradeDate;

    @ApiModelProperty(name="settleDate",value="交收日期",example="2021-01-01",required = true)
    private String[] settleDate;

    @ApiModelProperty(name="cleanStlStatus",value="结算指令状态",example="A1")
    private String cleanStlStatus;

    @ApiModelProperty(name="cleanArgStatus",value="结算合同状态",example="A1")
    private String cleanArgStatus;

    @ApiModelProperty(name="pageMode",value="分页对象",required = true)
    private PageMode pageMode;

    @ApiModelProperty(name="search",value="关键字",example="12")
    private String search;

    public String[] getSettleStatus() {
        return settleStatus;
    }

    public void setSettleStatus(String[] settleStatus) {
        this.settleStatus = settleStatus;
    }

    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public String getTgBusiType() {
        return tgBusiType;
    }

    public void setTgBusiType(String tgBusiType) {
        this.tgBusiType = tgBusiType;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getCleanStatus() {
        return cleanStatus;
    }

    public void setCleanStatus(String cleanStatus) {
        this.cleanStatus = cleanStatus;
    }



    public String[] getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String[] settleDate) {
        this.settleDate = settleDate;
    }

    public String getCleanStlStatus() {
        return cleanStlStatus;
    }

    public void setCleanStlStatus(String cleanStlStatus) {
        this.cleanStlStatus = cleanStlStatus;
    }

    public String getCleanArgStatus() {
        return cleanArgStatus;
    }

    public void setCleanArgStatus(String cleanArgStatus) {
        this.cleanArgStatus = cleanArgStatus;
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

    public String[] getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(String[] tradeDate) {
        this.tradeDate = tradeDate;
    }
}
