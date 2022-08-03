package com.joyintech.tams.exchange.common.request;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.NotEmpty;

import java.io.Serializable;
import java.util.List;

@ApiModel(value = "查询指令列表请求报文", description = "查询指令列表请求报文")
public class InstructionQueryRequest implements Serializable {
    private static final long serialVersionUID = 1238733335264286511L;

    @ApiModelProperty(required = true, name = "traderList", value = "交易员")
    @NotEmpty(message = "交易员不能为空")
    private List<String> traderList;

    @ApiModelProperty(required = false, name = "amount", value = "数量")
    private int amount = 0;

    @ApiModelProperty(required = false, name = "pageNo", value = "页码")
    private Integer pageNo;

    @ApiModelProperty(required = false, name = "entrustId", value = "指令编号D")
    private String entrustId;

    @ApiModelProperty(required = false, name = "portfolioId", value = "组合代码")
    private String portfolioId;

    @ApiModelProperty(required = false, name = "entrustStatus", value = "指令状态")
    private List<String> entrustStatus;

    @ApiModelProperty(required = false, name = "tradeMarket", value = "交易场所")
    private String tradeMarket;

    @ApiModelProperty(required = false, name = "tradePlat", value = "交易平台")
    private String tradePlat;

    @ApiModelProperty(required = false, name = "finprodMarketId", value = "市场代码")
    private String finprodMarketId;

    @ApiModelProperty(required = false, name = "ps", value = "交易方向")
    private String ps;

    @ApiModelProperty(required = false, name = "dataType", value = "数据类型")
    private String dataType;

    @ApiModelProperty(hidden = true)
    private String fairCode;
    @ApiModelProperty(hidden = true)
    private String platDate;
    @ApiModelProperty(hidden = true)
    private String approveCode;
    @ApiModelProperty(hidden = true)
    private String tradeTimeSwitch;
    @ApiModelProperty(hidden = true)
    private String fairModeEntrustPriceBestThanInstruction;
    @ApiModelProperty(hidden = true)
    private String fairModeWithUnacceptInstruction;

    public List<String> getTraderList() {
        return traderList;
    }

    public void setTraderList(List<String> traderList) {
        this.traderList = traderList;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public String getEntrustId() {
        return entrustId;
    }

    public void setEntrustId(String entrustId) {
        this.entrustId = entrustId;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public List<String> getEntrustStatus() {
        return entrustStatus;
    }

    public void setEntrustStatus(List<String> entrustStatus) {
        this.entrustStatus = entrustStatus;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getTradePlat() {
        return tradePlat;
    }

    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getPs() {
        return ps;
    }

    public void setPs(String ps) {
        this.ps = ps;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getFairCode() {
        return fairCode;
    }

    public void setFairCode(String fairCode) {
        this.fairCode = fairCode;
    }

    public String getPlatDate() {
        return platDate;
    }

    public void setPlatDate(String platDate) {
        this.platDate = platDate;
    }

    public String getApproveCode() {
        return approveCode;
    }

    public void setApproveCode(String approveCode) {
        this.approveCode = approveCode;
    }

    public String getTradeTimeSwitch() {
        return tradeTimeSwitch;
    }

    public void setTradeTimeSwitch(String tradeTimeSwitch) {
        this.tradeTimeSwitch = tradeTimeSwitch;
    }

    public String getFairModeEntrustPriceBestThanInstruction() {
        return fairModeEntrustPriceBestThanInstruction;
    }

    public void setFairModeEntrustPriceBestThanInstruction(String fairModeEntrustPriceBestThanInstruction) {
        this.fairModeEntrustPriceBestThanInstruction = fairModeEntrustPriceBestThanInstruction;
    }

    public String getFairModeWithUnacceptInstruction() {
        return fairModeWithUnacceptInstruction;
    }

    public void setFairModeWithUnacceptInstruction(String fairModeWithUnacceptInstruction) {
        this.fairModeWithUnacceptInstruction = fairModeWithUnacceptInstruction;
    }
}
