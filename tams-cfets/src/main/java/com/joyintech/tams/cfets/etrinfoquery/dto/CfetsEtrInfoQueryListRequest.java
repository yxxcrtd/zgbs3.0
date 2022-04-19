package com.joyintech.tams.cfets.etrinfoquery.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 场外指令分发
 * 指令列表分页查询条件DTO
 * @author sjj
 * @version 1.0 2021年10月26日
 * @see CfetsEtrInfoQueryListRequest
 * @since 1.0
 */
@ApiModel(value="指令分发菜单指令列表请求参数",description="指令分发菜单指令列表请求参数")
public class CfetsEtrInfoQueryListRequest {

    @ApiModelProperty(name="entrustStatus",value="指令状态",example="字符串1,字符串2,...")
    private String[] entrustStatus;

    @ApiModelProperty(name="portfolioId",value="组合代码集合",example="字符串1,字符串2,...")
    private String portfolioId;

    @ApiModelProperty(name="ps",value="交易方向",example="A1")
    private String ps;

    @ApiModelProperty(name="finprodType2",value="资产类型",example="A1")
    private String finprodType2;

    @ApiModelProperty(name="busiTypePosition",value="业务类型",example="A1")
    private String busiTypePosition;

    @ApiModelProperty(name="finprodMarketId",value="资产代码",example="字符串1,字符串2,...")
    private String finprodMarketId;

    @ApiModelProperty(name="tradeMarket",value="交易场所",example="A1")
    private String tradeMarket;

    @ApiModelProperty(name="settleSpeed",value="清算速度",example="A1")
    private String settleSpeed;

    @ApiModelProperty(name="secAcctId",value="证券账户",example="字符串1,字符串2,...")
    private String secAcctId;

    @ApiModelProperty(name="entrustId",value="指令代码",example="A1")
    private String entrustId;

    @ApiModelProperty(name="invManagerId",value="投资经理",example="字符串1,字符串2,...")
    private String invManagerId;

    @ApiModelProperty(name="entrustDate",value="指令日期",example="2021-01-01")
    private String entrustDate;

    @ApiModelProperty(name="entrustEndDate",value="指令日期结束日",example="2021-01-01")
    private String entrustEndDate;

    @ApiModelProperty(name="entrustStartDate",value="指令日期开始日",example="2021-01-01")
    private String entrustStartDate;

    @ApiModelProperty(name="ccy",value="币种",example="A1")
    private String ccy;

    @ApiModelProperty(name="pageMode",value="分页对象")
    private PageMode pageMode;

    @ApiModelProperty(name="search",value="关键字",example="12")
    private String search;

    public String[] getEntrustStatus() {
        return entrustStatus;
    }

    public void setEntrustStatus(String[] entrustStatus) {
        this.entrustStatus = entrustStatus;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getBusiTypePosition() {
        return busiTypePosition;
    }

    public void setBusiTypePosition(String busiTypePosition) {
        this.busiTypePosition = busiTypePosition;
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

    public String getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getInvManagerId() {
        return invManagerId;
    }

    public void setInvManagerId(String invManagerId) {
        this.invManagerId = invManagerId;
    }

    public String getEntrustDate() {
        return entrustDate;
    }

    public void setEntrustDate(String entrustDate) {
        this.entrustDate = entrustDate;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
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

    public String getEntrustEndDate() {
        return entrustEndDate;
    }

    public void setEntrustEndDate(String entrustEndDate) {
        this.entrustEndDate = entrustEndDate;
    }

    public String getEntrustStartDate() {
        return entrustStartDate;
    }

    public void setEntrustStartDate(String entrustStartDate) {
        this.entrustStartDate = entrustStartDate;
    }
}

