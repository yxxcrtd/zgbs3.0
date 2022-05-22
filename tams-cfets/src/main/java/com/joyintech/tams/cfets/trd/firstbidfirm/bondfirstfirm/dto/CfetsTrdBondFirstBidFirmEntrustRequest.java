package com.joyintech.tams.cfets.trd.firstbidfirm.bondfirstfirm.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "指令查询条件", description = "一级投标确认指令查询条件")
public class CfetsTrdBondFirstBidFirmEntrustRequest {

    @ApiModelProperty(name = "pageMode", value = "分页对象", example = "{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;

    @ApiModelProperty(name = "entrustStatus", value = "指令状态,列表", example = "[01,02]")
    private List<String> entrustStatus;

    @ApiModelProperty(name = "portfolioIds", value = "组合名称,列表", example = "[A,B]")
    private List<String> portfolioIds;

    @ApiModelProperty(name = "finprodIds", value = "资产代码,列表 etrAddSearch接口必传", example = "[A,B]")
    private List<String> finprodIds;

    @ApiModelProperty(name="firstBidStatus",value="中标状态",example="A1")
    private String firstBidStatus;

    @ApiModelProperty(name="tradeMarket",value="交易场所")
    private String tradeMarket;

    @ApiModelProperty(name="tenderTarget",value="招标标的")
    private String tenderTarget;

    @ApiModelProperty(name = "entrustDateStart", value = "指令开始日期,字符串", example = "2021-12-21")
    private String entrustDateStart;

    @ApiModelProperty(name = "entrustDateEnd", value = "指令结束日期,字符串", example = "2021-12-21")
    private String entrustDateEnd;

    @ApiModelProperty(name = "issueDateStart", value = "投标日开始日期,字符串", example = "2021-12-21")
    private String issueDateStart;

    @ApiModelProperty(name = "issueDateEnd", value = "投标日结束日期,字符串", example = "2021-12-21")
    private String issueDateEnd;

    @ApiModelProperty(name = "entrustId", value = "指令代码,字符串", example = "WTBOND20220418")
    private String entrustId;

    @ApiModelProperty(name = "entrustIds ", value = "指令代码集合,列表", example = "[WTBOND2022041801,WTBOND2022041802]")
    private List<String> entrustIds;

    @ApiModelProperty(name = "invManagerIds ", value = "投资经理集合", example = "[01,02]")
    private List<String> invManagerIds;

    @ApiModelProperty(name = "search", value = "关键词")
    private String search;

    @ApiModelProperty(name="counterId",value="交易对手",example="A1")
    private String counterId;

    @ApiModelProperty(name="counterName",value="交易对手名称",example="A1")
    private String counterName;

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

    public List<String> getEntrustStatus() {
        return entrustStatus;
    }

    public void setEntrustStatus(List<String> entrustStatus) {
        this.entrustStatus = entrustStatus;
    }

    public List<String> getPortfolioIds() {
        return portfolioIds;
    }

    public void setPortfolioIds(List<String> portfolioIds) {
        this.portfolioIds = portfolioIds;
    }


    public List<String> getEntrustIds() {
        return entrustIds;
    }

    public void setEntrustIds(List<String> entrustIds) {
        this.entrustIds = entrustIds;
    }

    public List<String> getInvManagerIds() {
        return invManagerIds;
    }

    public void setInvManagerIds(List<String> invManagerIds) {
        this.invManagerIds = invManagerIds;
    }

    public List<String> getFinprodIds() {
        return finprodIds;
    }

    public void setFinprodIds(List<String> finprodIds) {
        this.finprodIds = finprodIds;
    }

    public String getEntrustDateStart() {
        return entrustDateStart;
    }

    public void setEntrustDateStart(String entrustDateStart) {
        this.entrustDateStart = entrustDateStart;
    }

    public String getEntrustDateEnd() {
        return entrustDateEnd;
    }

    public void setEntrustDateEnd(String entrustDateEnd) {
        this.entrustDateEnd = entrustDateEnd;
    }


    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }


    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getIssueDateStart() {
        return issueDateStart;
    }

    public void setIssueDateStart(String issueDateStart) {
        this.issueDateStart = issueDateStart;
    }

    public String getIssueDateEnd() {
        return issueDateEnd;
    }

    public void setIssueDateEnd(String issueDateEnd) {
        this.issueDateEnd = issueDateEnd;
    }

    public String getFirstBidStatus() {
        return firstBidStatus;
    }

    public void setFirstBidStatus(String firstBidStatus) {
        this.firstBidStatus = firstBidStatus;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getTenderTarget() {
        return tenderTarget;
    }

    public void setTenderTarget(String tenderTarget) {
        this.tenderTarget = tenderTarget;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getCounterName() {
        return counterName;
    }

    public void setCounterName(String counterName) {
        this.counterName = counterName;
    }
}