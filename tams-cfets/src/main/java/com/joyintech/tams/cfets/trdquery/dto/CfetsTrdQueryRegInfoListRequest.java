package com.joyintech.tams.cfets.trdquery.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import com.joyintech.tams.cfets.trdconfirm.dto.CfetsTrdConfirmCstpInfoListResponse;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

/**
 * 银行间交易查询
 * 交易登记查询请求条件DTO
 * @author
 * @version 1.0 2021年10月26日
 * @see CfetsTrdQueryRegInfoListRequest
 * @since 1.0
 */
@ApiModel(value="交易登记查询请求参数",description="交易登记查询请求参数")
public class CfetsTrdQueryRegInfoListRequest {

    @ApiModelProperty(name="regStatus",value="登记状态集合",example="字符串1,字符串2,...")
    private String   regStatus;

    @ApiModelProperty(name="portfolioId",value="组合代码集合",example="字符串1,字符串2,...")
    private String portfolioId;

    @ApiModelProperty(name="ps",value="交易方向",example="T01")
    private String ps;

    @ApiModelProperty(name="regAssetType",value="资产类型",example="01")
    private String regAssetType;

    @ApiModelProperty(name="finprodId",value="资产代码",example="字符串1,字符串2,...")
    private String finprodId;

    @ApiModelProperty(name="tradeMarket",value="交易场所",example="01")
    private String tradeMarket;

    @ApiModelProperty(name="settleSpeed",value="清算速度",example="01")
    private String settleSpeed;

    @ApiModelProperty(name="secAcctId",value="证券账户",example="字符串1,字符串2,...")
    private String secAcctId;

    @ApiModelProperty(name="busiCode",value="业务编号",example="字符串1")
    private String busiCode;

    @ApiModelProperty(name="effectiveStatus",value="有效状态",example="A1")
    private String effectiveStatus;

    @ApiModelProperty(name="matchStatus",value="匹配状态",example="字符串1")
    private String matchStatus;

    @ApiModelProperty(name="quoteType",value="报价方式",example="字符串1")
    private String quoteType;

    @ApiModelProperty(name="counterId",value="对手方",example="字符串1")
    private String counterId;

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

    @ApiModelProperty(name="busiTypePosition",value="业务类型")
    private String busiTypePosition;

    public String getRegStatus() {
        return regStatus;
    }

    public void setRegStatus(String regStatus) {
        this.regStatus = regStatus;
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

    public String getBusiCode() {
        return busiCode;
    }

    public void setBusiCode(String busiCode) {
        this.busiCode = busiCode;
    }

    public String getEffectiveStatus() {
        return effectiveStatus;
    }

    public void setEffectiveStatus(String effectiveStatus) {
        this.effectiveStatus = effectiveStatus;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public String getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
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

    public String getBusiTypePosition() {
        return busiTypePosition;
    }

    public void setBusiTypePosition(String busiTypePosition) {
        this.busiTypePosition = busiTypePosition;
    }
}

