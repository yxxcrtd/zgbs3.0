/*
 * 文件名：FinBondTargetAssetDto.java
 * 版权：Copyright 2017-2018 JoyinTech. Co. Ltd. All Rights Reserved.
 * 描述：理财资管管理系统V2.0
 * 修改人：zhangnian
 * 修改时间：2018年9月20日
 * 修改内容：新建
 * 系统名称：理财资管管理系统V2.0
 */
 
package com.joyintech.tams.cfets.trd.primarybidmanage.dto;

import com.joyintech.tams.business.common.dto.PageMode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * 中标结果维护信息DTO
 *
 * @author wangyang1
 * @version 1.0 2022年4月13日
 * @see CfetsPrimaryBidManageRequest
 * @since 1.0
 */
@ApiModel(value="中标结果维护查询参数",description="中标结果维护查询参数")
public class CfetsPrimaryBidManageRequest {

    @ApiModelProperty(name="search",value="关键字",example="A1")
    private String search;

    @ApiModelProperty(name="pageMode",value="分页对象")
    private PageMode pageMode;

    @ApiModelProperty(name="isWin",value="中标状态")
    private String isWin;

    @ApiModelProperty(name="bidId",value="投标编号")
    private String bidId;

    @ApiModelProperty(name="finprodId",value="资产代码")
    private String finprodId ;

    @ApiModelProperty(name="tenderTarget",value="招标标的code")
    private String tenderTarget;

    @ApiModelProperty(name="bidDate",value="投标日")
    private List<String> bidDate;

    @ApiModelProperty(name="bidDateStart",value="投标日开始日")
    private String bidDateStart;

    @ApiModelProperty(name="bidDateEnd",value="投标日结束日")
    private String bidDateEnd;

    @ApiModelProperty(name="bidPrinAmt",value="指令投标券面总额(万元)")
    private BigDecimal bidPrinAmt;

    @ApiModelProperty(name="totalBiddingNum",value="汇总投标券面总额(万元)")
    private BigDecimal totalBiddingNum;

    @ApiModelProperty(name="bidPrice",value="投标标位")
    private String bidPrice;

    @ApiModelProperty(name="winType",value="投标方式")
    private String winType;

    @ApiModelProperty(name="payType",value="缴款方式")
    private String payType;

    @ApiModelProperty(name="winDate",value="投标确认日")
    private List<String> winDate;

    @ApiModelProperty(name="winDateStart",value="投标确认日开始日")
    private String winDateStart;

    @ApiModelProperty(name="winDateEnd",value="投标确认日结束日")
    private String winDateEnd;

    @ApiModelProperty(name="payDate",value="投标确认日")
    private List<String> payDate;

    @ApiModelProperty(name="payDateStart",value="投标确认日开始日")
    private String payDateStart;

    @ApiModelProperty(name="payDateEnd",value="投标确认日结束日")
    private String payDateEnd;

    @ApiModelProperty(name="listingDate",value="投标确认日")
    private List<String> listingDate;

    @ApiModelProperty(name="listingDateStart",value="投标确认日开始日")
    private String listingDateStart;

    @ApiModelProperty(name="listingDateEnd",value="投标确认日结束日")
    private String listingDateEnd;

    @ApiModelProperty(name="bidType",value="中标方式")
    private String bidType;

    @ApiModelProperty(name="tradeMarket",value="交易场所code")
    private String tradeMarket;


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

    public String getIsWin() {
        return isWin;
    }

    public void setIsWin(String isWin) {
        this.isWin = isWin;
    }

    public String getBidId() {
        return bidId;
    }

    public void setBidId(String bidId) {
        this.bidId = bidId;
    }

    public String getFinprodId() {
        return finprodId;
    }

    public void setFinprodId(String finprodId) {
        this.finprodId = finprodId;
    }

    public String getTenderTarget() {
        return tenderTarget;
    }

    public void setTenderTarget(String tenderTarget) {
        this.tenderTarget = tenderTarget;
    }

    public List<String> getBidDate() {
        return bidDate;
    }

    public void setBidDate(List<String> bidDate) {
        this.bidDate = bidDate;
    }

    public String getBidDateStart() {
        return bidDateStart;
    }

    public void setBidDateStart(String bidDateStart) {
        this.bidDateStart = bidDateStart;
    }

    public String getBidDateEnd() {
        return bidDateEnd;
    }

    public void setBidDateEnd(String bidDateEnd) {
        this.bidDateEnd = bidDateEnd;
    }

    public BigDecimal getBidPrinAmt() {
        return bidPrinAmt;
    }

    public void setBidPrinAmt(BigDecimal bidPrinAmt) {
        this.bidPrinAmt = bidPrinAmt;
    }

    public BigDecimal getTotalBiddingNum() {
        return totalBiddingNum;
    }

    public void setTotalBiddingNum(BigDecimal totalBiddingNum) {
        this.totalBiddingNum = totalBiddingNum;
    }

    public String getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(String bidPrice) {
        this.bidPrice = bidPrice;
    }

    public String getWinType() {
        return winType;
    }

    public void setWinType(String winType) {
        this.winType = winType;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }

    public List<String> getWinDate() {
        return winDate;
    }

    public void setWinDate(List<String> winDate) {
        this.winDate = winDate;
    }

    public String getWinDateStart() {
        return winDateStart;
    }

    public void setWinDateStart(String winDateStart) {
        this.winDateStart = winDateStart;
    }

    public String getWinDateEnd() {
        return winDateEnd;
    }

    public void setWinDateEnd(String winDateEnd) {
        this.winDateEnd = winDateEnd;
    }

    public List<String> getPayDate() {
        return payDate;
    }

    public void setPayDate(List<String> payDate) {
        this.payDate = payDate;
    }

    public String getPayDateStart() {
        return payDateStart;
    }

    public void setPayDateStart(String payDateStart) {
        this.payDateStart = payDateStart;
    }

    public String getPayDateEnd() {
        return payDateEnd;
    }

    public void setPayDateEnd(String payDateEnd) {
        this.payDateEnd = payDateEnd;
    }

    public List<String> getListingDate() {
        return listingDate;
    }

    public void setListingDate(List<String> listingDate) {
        this.listingDate = listingDate;
    }

    public String getListingDateStart() {
        return listingDateStart;
    }

    public void setListingDateStart(String listingDateStart) {
        this.listingDateStart = listingDateStart;
    }

    public String getListingDateEnd() {
        return listingDateEnd;
    }

    public void setListingDateEnd(String listingDateEnd) {
        this.listingDateEnd = listingDateEnd;
    }

    public String getBidType() {
        return bidType;
    }

    public void setBidType(String bidType) {
        this.bidType = bidType;
    }
}
