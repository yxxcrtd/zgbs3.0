package com.joyintech.tams.cfets.trd.primarybidmanage.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 下拉框请求requesyt
 * @author wangyang1
 * @version 1.0 2022年4月13日
 */
@ApiModel(value="查询请求参数",description="查询请求参数")
public class SelectOptionCommonRequest {

    @ApiModelProperty(name="search",value="关键字(方法：getfinprodIdForAdd(选填))")
    private String search;

    @ApiModelProperty(name="initValue",value="初始值(方法：getfinprodIdForAdd(选填))")
    private String initValue;

    @ApiModelProperty(name="pageMode",value="分页对象")
    private PageMode pageMode;

    @ApiModelProperty(name="portfolioId",value="组合代码",example="A1")
    private String portfolioId;

    @ApiModelProperty(name="tradeMarket",value="交易场所",example="A1")
    private String tradeMarket;

    @ApiModelProperty(name="counterType",value="交易对手类型",example="A1")
    private String counterType;

    @ApiModelProperty(name="counterIdName",value="交易对手名称",example="A1")
    private String counterIdName;

    @ApiModelProperty(name="finprodType2",value="finprodType2",example="A1")
    private String finprodType2;

    @ApiModelProperty(name="param",value="参数",example="A1")
    private String param;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

    public String getPortfolioId() {
        return portfolioId;
    }

    public void setPortfolioId(String portfolioId) {
        this.portfolioId = portfolioId;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getCounterType() {
        return counterType;
    }

    public void setCounterType(String counterType) {
        this.counterType = counterType;
    }

    public String getCounterIdName() {
        return counterIdName;
    }

    public void setCounterIdName(String counterIdName) {
        this.counterIdName = counterIdName;
    }

    public String getInitValue() {
        return initValue;
    }

    public void setInitValue(String initValue) {
        this.initValue = initValue;
    }

    public String getFinprodType2() {
        return finprodType2;
    }

    public void setFinprodType2(String finprodType2) {
        this.finprodType2 = finprodType2;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
}
