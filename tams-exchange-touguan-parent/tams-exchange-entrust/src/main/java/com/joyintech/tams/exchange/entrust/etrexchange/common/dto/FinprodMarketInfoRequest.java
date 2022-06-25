/*
 * 文件名：EtrExchangeTradeQuotation001Dto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月6日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.exchange.entrust.etrexchange.common.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 *  行情信息(左上)
 * 〈功能详细描述〉
 * @author meifaxin
 * @version 1.0 2021年12月21日
 * @see FinprodMarketInfoRequest
 * @since 1.0
 */
@ApiModel(value="指令下单资产列表请求参数",description="指令下单资产列表请求参数")
public class FinprodMarketInfoRequest {

    @ApiModelProperty(name="tradePlat",value="交易平台",example="A1")
    private String tradePlat;
    @ApiModelProperty(name="tradeMarket",value="交易场所",example="04")
    private String tradeMarket;

    @ApiModelProperty(name="search",value="关键字",example="A1")
    private String search;
    @ApiModelProperty(name="initValue",value="市场代码id",example="A1")
    private String initValue;

    private String searchPageType;

    @ApiModelProperty(name="pageMode",value="分页对象")
    private PageMode pageMode;

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

    public String getTradePlat() {
        return tradePlat;
    }

    public void setTradePlat(String tradePlat) {
        this.tradePlat = tradePlat;
    }

    public String getInitValue() {
        return initValue;
    }

    public void setInitValue(String initValue) {
        this.initValue = initValue;
    }

    public String getSearchPageType() {
        return searchPageType;
    }

    public void setSearchPageType(String searchPageType) {
        this.searchPageType = searchPageType;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }
}
