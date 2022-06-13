package com.joyintech.tams.cfets.trd.primarybidmanage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.business.common.dto.PageMode;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 投标确认列表DTO
 *
 * @author wangyang1
 * @version 1.0 2022年5月5日
 * @see CfetsPrimaryBidManageCfmRequest
 * @since 1.0
 */
@ApiModel(value="投标确认列表查询参数",description="投标确认列表查询参数")
public class CfetsPrimaryBidManageCfmRequest {

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

    @ApiModelProperty(name="bidCfmDate",value="投标确认日")
    private List<String> bidCfmDate;

    @ApiModelProperty(name="bidCfmDateStart",value="投标确认日开始日")
    private String bidCfmDateStart;

    @ApiModelProperty(name="bidDateEnd",value="投标确认日结束日")
    private String bidCfmDateEnd;

    @ApiModelProperty(name="counterId",value="交易对手")
    private String counterId;


    @ApiModelProperty(name="remark",value="备注")
    private String remark ;

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

    public List<String> getBidCfmDate() {
        return bidCfmDate;
    }

    public void setBidCfmDate(List<String> bidCfmDate) {
        this.bidCfmDate = bidCfmDate;
    }

    public String getBidCfmDateStart() {
        return bidCfmDateStart;
    }

    public void setBidCfmDateStart(String bidCfmDateStart) {
        this.bidCfmDateStart = bidCfmDateStart;
    }

    public String getBidCfmDateEnd() {
        return bidCfmDateEnd;
    }

    public void setBidCfmDateEnd(String bidCfmDateEnd) {
        this.bidCfmDateEnd = bidCfmDateEnd;
    }

    public String getCounterId() {
        return counterId;
    }

    public void setCounterId(String counterId) {
        this.counterId = counterId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTradeMarket() {
        return tradeMarket;
    }

    public void setTradeMarket(String tradeMarket) {
        this.tradeMarket = tradeMarket;
    }
}
