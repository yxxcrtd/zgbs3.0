/*
 * 文件名：CfetsTrdQueryRegInfoListResponse.java
 * 版权：Copyright 2020-2022 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管系统V2.0
 * 修改人：
 * 修改时间：2021年01月25日
 * 修改内容：新建
 * 系统名称：理财资管系统V2.0
 */

package com.joyintech.tams.cfets.trdquery.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 银行间交易查询
 * 查询交易列表请求参数DTO
 * @author 1
 * @version 1.0 2021年10月26日
 * @see CfetsTrdQueryTradeInfoListRequest
 * @since 1.0
 */
@ApiModel(value="交易查询列表请求参数",description="交易登记查询列表请求参数")
public class CfetsTrdQueryTradeInfoListRequest {

    @ApiModelProperty(name="effectiveStatus",value="有效状态Code",example="A1")
    private String effectiveStatus;

    @ApiModelProperty(name="totleTradeId",value="汇总交易编号",example="A1")
    private String totleTradeId;

    @ApiModelProperty(name="regAssetType",value="资产类型",example="01")
    private String regAssetType;

    @ApiModelProperty(name="finprodId",value="资产代码",example="字符串1,字符串2,...")
    private String finprodId;

    @ApiModelProperty(name="portfolioId",value="组合代码",example="字符串1,字符串2,...")
    private String portfolioId;

    @ApiModelProperty(name="busiType",value="业务类型",example="01")
    private String busiType;

    @ApiModelProperty(name="execId",value="成交编号",example="01")
    private String execId;

    @ApiModelProperty(name="tradeMarket",value="交易场所",example="01")
    private String tradeMarket;

    @ApiModelProperty(name="ps",value="交易方向",example="T01")
    private String ps;

    @ApiModelProperty(name="cleanStatus",value="清算状态",example="A1,A2")
    private String cleanStatus;

    @ApiModelProperty(name="settleStatus",value="交收状态",example="01")
    private String settleStatus;

    @ApiModelProperty(name="pageMode",value="分页对象")
    private PageMode pageMode;

    @ApiModelProperty(name="search",value="关键字",example="12")
    private String search;

    @ApiModelProperty(name="tabType",value="查询tab页",example="12")
    private String tabType;

    @ApiModelProperty(name="startDate",value="查询开始日期",example="12")
    private Date startDate;

    @ApiModelProperty(name="endDate",value="查询结束日期",example="12")
    private Date endDate;

    @ApiModelProperty(name="tradeMarketMode",value="交易市场",example="01")
    private String tradeMarketMode;

    @ApiModelProperty(name="busiCode",value="业务编号",example="字符串1")
    private String busiCode;

    @ApiModelProperty(name="entrustId",value="指令代码",example="字符串1")
    private String entrustId;

    @ApiModelProperty(name="bidId",value="投标编号",example="字符串1")
    private String bidId;

    public String getEffectiveStatus() {
        return effectiveStatus;
    }

    public void setEffectiveStatus(String effectiveStatus) {
        this.effectiveStatus = effectiveStatus;
    }

    public String getTotleTradeId() {
        return totleTradeId;
    }

    public void setTotleTradeId(String totleTradeId) {
        this.totleTradeId = totleTradeId;
    }

    public String getRegAssetType() {
        return regAssetType;
    }

    public void setRegAssetType(String regAssetType) {
        this.regAssetType = regAssetType;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    public String getExecId() {
        return execId;
    }

    public void setExecId(String execId) {
        this.execId = execId;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
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

    public String getSettleStatus() {
        return settleStatus;
    }

    public void setSettleStatus(String settleStatus) {
        this.settleStatus = settleStatus;
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

    public String getTabType() {
        return tabType;
    }

    public void setTabType(String tabType) {
        this.tabType = tabType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getTradeMarketMode() {
        return tradeMarketMode;
    }

    public void setTradeMarketMode(String tradeMarketMode) {
        this.tradeMarketMode = tradeMarketMode;
    }

    public String getBusiCode() {
        return busiCode;
    }

    public void setBusiCode(String busiCode) {
        this.busiCode = busiCode;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }
}
