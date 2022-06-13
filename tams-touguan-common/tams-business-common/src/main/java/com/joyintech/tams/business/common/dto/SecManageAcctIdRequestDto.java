package com.joyintech.tams.business.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="证券账户列表查询请求参数",description="证券账户列表查询请求参数")
public class SecManageAcctIdRequestDto {
    @ApiModelProperty(name="portfolioId",value="组合代码集合",example="字符串1,字符串2,...")
    private String portfolioId;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",example="字符串1,字符串2,...")
    private String finprodMarketId;

    @ApiModelProperty(name="tradeMarket",value="交易场所",example="A1")
    private String tradeMarket;

    @ApiModelProperty(name="pageMode",value="分页对象")
    private PageMode pageMode;

    @ApiModelProperty(name="search",value="证券账户",example="A1")
    private String search;

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
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
