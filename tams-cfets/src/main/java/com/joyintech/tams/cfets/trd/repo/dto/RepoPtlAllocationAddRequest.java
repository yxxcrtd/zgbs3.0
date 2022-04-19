package com.joyintech.tams.cfets.trd.repo.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * 根据本方所关联组合和基本信息中 交易方向+清算速度+回购期限+交易对手（登记界面：本方为空时不判断本方关联的组合）和已添加指令的组合下证券账户、回购利率 进行判断，
 * 匹配满足的已接收未完成的指令列表，组合分配中已添加的指令不在列表显示；
 * 其中交易对手如指令上指定交易对手必须与基本信息中交易对手一致才能查询出此指令，如指令上没有指定则无需判断
 *
 * @author jiangpeng
 * @date 2021/12/8 16:53
 */
@ApiModel(value = "组合分配添加请求参数", description = "组合分配添加请求参数")
public class RepoPtlAllocationAddRequest {

    @ApiModelProperty(required = true, name = "ps", value = "交易方向", example = "03")
    private String ps;

    @ApiModelProperty(required = true, name = "settleSpreed", value = "清算速度")
    private String settleSpreed;

    @ApiModelProperty(required = true, name = "repoTerm", value = "回购期限")
    private int repoTerm;

    @ApiModelProperty(required = true, name = "partyId", value = "交易对手")
    private String partyId;

    @ApiModelProperty(required = true, name = "secAcctId", value = "证券账户")
    private String secAcctId;

    @ApiModelProperty(required = true, name = "quoteId", value = "报价编号（内部代码）",example = "DHREPO202112211051")
    private String quoteId;

    @ApiModelProperty(required = true, name = "tradeYield", value = "回购利率")
    private BigDecimal tradeYield;

    @ApiModelProperty(required = true, name = "notEntrustIdList", value = "已经选择的指令代码", example = "['WTREPO202107081619']")
    private List<String> notEntrustIdList;

    @ApiModelProperty(required = true, name = "portfolioId", value = "查询条件,组合代码", example = "['jp001']")
    private List<String> portfolioId;

    @ApiModelProperty(required = true, name = "priceModelList", value = "查询条件,价格模式", example = "['PMR01']")
    private List<String> priceModelList;

    @ApiModelProperty(required = true, name = "invManagerId", value = "查询条件,投资经理", example = "['100112912389']")
    private List<String> invManagerId;

    @ApiModelProperty(required = true, name = "entrustIdList", value = "查询条件,指令代码", example = "['WTREPO202107081619']")
    private List<String> entrustIdList;

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getSettleSpreed() {
        return settleSpreed;
    }

    public void setSettleSpreed(String settleSpreed) {
        this.settleSpreed = settleSpreed;
    }

    public int getRepoTerm() {
        return repoTerm;
    }

    public void setRepoTerm(int repoTerm) {
        this.repoTerm = repoTerm;
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(String quoteId) {
        this.quoteId = quoteId;
    }

    public BigDecimal getTradeYield() {
        return tradeYield;
    }

    public void setTradeYield(BigDecimal tradeYield) {
        this.tradeYield = tradeYield;
    }

    public List<String> getNotEntrustIdList() {
        return notEntrustIdList;
    }

    public void setNotEntrustIdList(List<String> notEntrustIdList) {
        this.notEntrustIdList = notEntrustIdList;
    }

    public List<String> getEntrustIdList() {
        return entrustIdList;
    }

    public void setEntrustIdList(List<String> entrustIdList) {
        this.entrustIdList = entrustIdList;
    }

    public List<String> getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(List<String> portfolioId) {
        this.portfolioId = portfolioId;
    }

    public List<String> getPriceModelList() {
        return priceModelList;
    }

    public void setPriceModelList(List<String> priceModelList) {
        this.priceModelList = priceModelList;
    }

    public List<String> getInvManagerId() {
        return invManagerId;
    }

    public void setInvManagerId(List<String> invManagerId) {
        this.invManagerId = invManagerId;
    }
}

