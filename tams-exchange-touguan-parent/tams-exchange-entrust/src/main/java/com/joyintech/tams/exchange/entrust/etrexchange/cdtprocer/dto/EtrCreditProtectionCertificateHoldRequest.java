package com.joyintech.tams.exchange.entrust.etrexchange.cdtprocer.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import com.joyintech.tams.exchange.entrust.etrexchange.secrepo.dto.EtrExchangeTradePositionsSecRepoRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 *
 * 〈功能详细描述〉
 * @author wsz
 * @version 1.0 20220526
 * @see EtrCreditProtectionCertificateHoldRequest
 * @since 1.0
 */
@ApiModel(value="持仓信息查询请求参数",description="持仓信息查询请求参数")
public class EtrCreditProtectionCertificateHoldRequest {


    @ApiModelProperty(name="search",value="关键字",example="A1")
    private String search;

    @ApiModelProperty(name="portfolioIdList",value="组合代码",example="A1")
    private List<String> portfolioIdList;

    @ApiModelProperty(name="pageMode",value="分页对象",example="{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;

    @ApiModelProperty(name="tradeMarket",value="交易场所",example="A1")
    private String tradeMarket;

    @ApiModelProperty(name="secAcctId",value="证券账户",example="A1")
    private String secAcctId;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",example="A1")
    private String finprodMarketId;

    @ApiModelProperty(name="secId",value="证券代码",example="A1")
    private String secId;

    @ApiModelProperty(name="portfolioName",value="组合名称",example="A1")
    private String portfolioName;

    @ApiModelProperty(name="finprodName",value="资产名称",example="A1")
    private String finprodName;

    @ApiModelProperty(name="finprodMarketIds",value="多个市场代码",example="A,B")
    private String finprodMarketIds;

    @ApiModelProperty(name="从pageMode获取",value="从pageMode获取")
    public String getSort() {
        return pageMode.getSort();
    }
    @ApiModelProperty(name="从pageMode获取",value="从pageMode获取")
    public String getDirection() {
        return pageMode.getDirection();
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public List<String> getPortfolioIdList() {
        return portfolioIdList;
    }

    public void setPortfolioIdList(List<String> portfolioIdList) {
        this.portfolioIdList = portfolioIdList;
    }

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getSecId() {
        return secId;
    }

    public void setSecId(String secId) {
        this.secId = secId;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getFinprodName() {
        return finprodName;
    }

    public void setFinprodName(String finprodName) {
        this.finprodName = finprodName;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public String getFinprodMarketIds() {
        return finprodMarketIds;
    }

    public void setFinprodMarketIds(String finprodMarketIds) {
        this.finprodMarketIds = finprodMarketIds;
    }

    @ApiModelProperty(name="assetType",value="资产类型",example="A1")
    private String assetType;

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }
}
