/*
 * 文件名：EtrExchangeTradeDto002.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：chendijun
 * 修改时间：2020年7月2日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */

package com.joyintech.tams.exchange.entrust.etrexchange.agreementrep.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.joyintech.framework.annotation.PageId;
import com.joyintech.framework.base.BaseEntity;
import com.joyintech.framework.json.BigDecimalSerializer;
import com.joyintech.tams.business.common.dto.PageMode;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.systemtools.util.SysDatadictUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 〈一句话功能简述〉 〈功能详细描述〉
 * 
 * @author chendijun
 * @version 1.0 2020年7月2日
 * @see EtrExchangeOnWayRepRequestDto
 * @since 1.0
 */
@ApiModel(value="在途回购查询请求参数",description="在途回购查询请求参数")
public class EtrExchangeOnWayRepRequestDto{

    @ApiModelProperty(name="search",value="关键字",example="A1")
    private String search;

    @ApiModelProperty(name="pageMode",value="分页对象",example="{pageSize: 10,pageNo: 1, sort: 'name', direction: 'asc'}")
    private PageMode pageMode;

    @ApiModelProperty(name="portfolioIdList",value="组合名称列表",example="A1")
    private List<String> portfolioIdList;

    @ApiModelProperty(name="tradeMarket",value="交易场所",example="03")
    private String tradeMarket;

    @ApiModelProperty(name="secAcctId",value="证券账户",example="A1")
    private String secAcctId;

    @ApiModelProperty(name="tradeId",value="交易编号",example="A1")
    private String tradeId;

    @ApiModelProperty(name="repPs",value="委托方向",example="A1")
    private String repPs;

    @ApiModelProperty(name="zyj",value="质押券代码列表",example="A1")
    private List<String> zyj;

    private String origSerialNo;

    private Boolean isDate;

    public void setIsDate(Boolean isDate) {
        this.isDate = isDate;
    }

    public Boolean getIsDate() {
        return isDate;
    }

    public String getOrigSerialNo() {
        return origSerialNo;
    }

    public void setOrigSerialNo(String origSerialNo) {
        this.origSerialNo = origSerialNo;
    }
    public String getTradeId() {
        return tradeId;
    }

    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }

    public PageMode getPageMode() {
        return pageMode;
    }

    public void setPageMode(PageMode pageMode) {
        this.pageMode = pageMode;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public String getSecAcctId() {
        return secAcctId;
    }

    public void setSecAcctId(String secAcctId) {
        this.secAcctId = secAcctId;
    }

    public String getRepPs() {
        return repPs;
    }

    public void setRepPs(String repPs) {
        this.repPs = repPs;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public List<String> getPortfolioIdList() {
        return portfolioIdList;
    }

    public void setPortfolioIdList(List<String> portfolioIdList) {
        this.portfolioIdList = portfolioIdList;
    }

    public List<String> getZyj() {
        return zyj;
    }

    public void setZyj(List<String> zyj) {
        this.zyj = zyj;
    }
}
