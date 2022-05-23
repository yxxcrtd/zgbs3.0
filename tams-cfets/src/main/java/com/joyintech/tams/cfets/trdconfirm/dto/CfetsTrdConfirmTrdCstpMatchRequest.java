package com.joyintech.tams.cfets.trdconfirm.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 银行间交易成交确认
 * 成交确认分页查询条件DTO
 * @author
 * @version 1.0 2021年10月26日
 * @see CfetsTrdConfirmTrdCstpMatchRequest
 * @since 1.0
 */
@ApiModel(value="查询成交确认分页请求数据",description="查询成交确认分页请求数据")
public class CfetsTrdConfirmTrdCstpMatchRequest {

    @ApiModelProperty(name="portfolioId",value="组合代码集合",example="字符串1,字符串2,...")
    private String portfolioId;

    @ApiModelProperty(name="ps",value="交易方向",example="T01")
    private String ps;

    @ApiModelProperty(name="regAssetType",value="资产类型",example="01")
    private String regAssetType;

    @ApiModelProperty(name="finprodType2",value="业务类型",example="01")
    private String finprodType2;

    @ApiModelProperty(name="finprodId",value="资产代码",example="字符串1,字符串2,...")
    private String finprodId;

    @ApiModelProperty(name="quoteType",value="报价方式",example="字符串1,字符串2,...")
    private String quoteType;

    @ApiModelProperty(name="dealType",value="交易类型",example="字符串1,字符串2,...")
    private String dealType;

    @ApiModelProperty(name="settleSpeed",value="清算速度",example="01")
    private String settleSpeed;

    @ApiModelProperty(name="secAcctId",value="证券账户",example="字符串1,字符串2,...")
    private String secAcctId;

    @ApiModelProperty(name="exeid",value="成交编号",example="A1")
    private String exeid;

    @ApiModelProperty(name="busiCode",value="业务编号",example="A1")
    private String busiCode;

    @ApiModelProperty(name="matchType",value="匹配方式，手工匹配，自动匹配",example="字符串1")
    private String matchType;

    @ApiModelProperty(name="counterId",value="对手方",example="字符串1")
    private String counterId;

    @ApiModelProperty(name="trdConfStatus",value="成交确认状态",example="字符串1")
    private String trdConfStatus;

    @ApiModelProperty(name="pageMode",value="分页对象")
    private PageMode pageMode;

    @ApiModelProperty(name="search",value="关键字",example="12")
    private String search;

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

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getQuoteType() {
        return quoteType;
    }

    public void setQuoteType(String quoteType) {
        this.quoteType = quoteType;
    }

    public String getSettleSpeed() {
        return settleSpeed;
    }

    public void setSettleSpeed(String settleSpeed) {
        this.settleSpeed = settleSpeed;
    }



    public String getBusiCode() {
        return busiCode;
    }

    public void setBusiCode(String busiCode) {
        this.busiCode = busiCode;
    }

    public String getMatchType() {
        return matchType;
    }

    public void setMatchType(String matchType) {
        this.matchType = matchType;
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

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getExeid() {
        return exeid;
    }

    public void setExeid(String exeid) {
        this.exeid = exeid;
    }

    public String getTrdConfStatus() {
        return trdConfStatus;
    }

    public void setTrdConfStatus(String trdConfStatus) {
        this.trdConfStatus = trdConfStatus;
    }

    public String getDealType() {
        return dealType;
    }

    public void setDealType(String dealType) {
        this.dealType = dealType;
    }
}

