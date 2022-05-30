package com.joyintech.tams.cfets.trd.primarybidmanage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import com.joyintech.tams.constant.DictCodeListExt1;
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
 * @see CfetsPrimaryBidManageCfmResponse
 * @since 1.0
 */
@ApiModel(value="投标确认列表查询返回参数",description="投标确认列表查询返回参数")
public class CfetsPrimaryBidManageCfmResponse {

    @ApiModelProperty(name="otherEffectFlag",value="有效状态ID")
    private String otherEffectFlag;

    @ApiModelProperty(name="otherEffectFlagStr",value="有效状态")
    private String otherEffectFlagStr;

    @ApiModelProperty(name="isWin",value="中标状态")
    private String isWin;

    @ApiModelProperty(name="isWinStr",value="中标状态")
    private String isWinStr;

    @ApiModelProperty(name="bidId",value="投标编号")
    private String bidId;

    @ApiModelProperty(name="finprodId",value="资产代码")
    private String finprodId ;

    @ApiModelProperty(name="finprodMarketId",value="资产市场代码")
    private String finprodMarketId ;

    @ApiModelProperty(name="finprodAbbr",value="资产简称")
    private String finprodAbbr;

    @ApiModelProperty(name="tenderTarget",value="招标标的code")
    private String tenderTarget;

    @ApiModelProperty(name="tenderTargetStr",value="招标标的")
    private String tenderTargetStr;

    @ApiModelProperty(name="bidDate",value="投标日")
    private Date bidDate;

    @ApiModelProperty(name="bidPrinAmt",value="指令投标券面总额(万元)")
    private BigDecimal bidPrinAmt;

    @ApiModelProperty(name="totalBiddingNum",value="汇总投标券面总额(万元)")
    private BigDecimal totalBiddingNum;

    @ApiModelProperty(name="bidPrice",value="投标标位")
    private String bidPrice;

    @ApiModelProperty(name="winType",value="投标方式")
    private String winType;

    @ApiModelProperty(name="winType",value="投标方式")
    private String winTypeStr;

    @ApiModelProperty(name="payType",value="缴款方式")
    private String payType;

    @ApiModelProperty(name="payTypeStr",value="缴款方式")
    private String payTypeStr;

    @ApiModelProperty(name="payDate",value="缴款日期")
    private Date payDate;

    @ApiModelProperty(name="bidCfmDate",value="投标确认日")
    private Date bidCfmDate;

    @ApiModelProperty(name="counterId",value="交易对手code")
    private String counterId;

    @ApiModelProperty(name="counterName",value="交易对手")
    private String counterName;

    @ApiModelProperty(name="remark",value="备注")
    private String remark ;

    @ApiModelProperty(name="ccy",value="币种")
    private String ccy ;

    @ApiModelProperty(name="ccyStr",value="币种")
    private String ccyStr ;

    @ApiModelProperty(name="trader",value="本方交易员Code")
    private String trader;

    @ApiModelProperty(name="traderStr",value="本方交易员")
    private String traderStr;

    @ApiModelProperty(name="tradeMarket",value="交易场所code")
    private String tradeMarket;

    @ApiModelProperty(name="tradeMarket",value="交易场所")
    private String tradeMarketStr;

    @ApiModelProperty(name="bidProductList",value="投标产品列表")
    private List<BidProductDto> bidProductList;

    @ApiModelProperty(name="summaryBidList",value="汇总投标列表")
    private List<SummaryBidDto> summaryBidList;

    public String getOtherEffectFlag() {
        return otherEffectFlag;
    }

    public void setOtherEffectFlag(String otherEffectFlag) {
        this.otherEffectFlag = otherEffectFlag;
    }

    public String getOtherEffectFlagStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.有效状态, this.getOtherEffectFlag());
    }

    public void setOtherEffectFlagStr(String otherEffectFlagStr) {
        this.otherEffectFlagStr = otherEffectFlagStr;
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

    public String getFinprodAbbr() {
        return finprodAbbr;
    }

    public void setFinprodAbbr(String finprodAbbr) {
        this.finprodAbbr = finprodAbbr;
    }

    public String getTenderTarget() {
        return tenderTarget;
    }

    public void setTenderTarget(String tenderTarget) {
        this.tenderTarget = tenderTarget;
    }

    public String getTenderTargetStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.招标标的, this.getTenderTarget());
    }

    public void setTenderTargetStr(String tenderTargetStr) {
        this.tenderTargetStr = tenderTargetStr;
    }
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getBidDate() {
        return bidDate;
    }

    public void setBidDate(Date bidDate) {
        this.bidDate = bidDate;
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

    public String getWinTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.投标方式, this.getWinType());
    }

    public void setWinTypeStr(String winTypeStr) {
        this.winTypeStr = winTypeStr;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getPayTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.缴款方式, this.getPayType());
    }

    public void setPayTypeStr(String payTypeStr) {
        this.payTypeStr = payTypeStr;
    }
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getBidCfmDate() {
        return bidCfmDate;
    }

    public void setBidCfmDate(Date bidCfmDate) {
        this.bidCfmDate = bidCfmDate;
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

    public String getTradeMarketStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, this.getTradeMarket());
    }

    public void setTradeMarketStr(String tradeMarketStr) {
        this.tradeMarketStr = tradeMarketStr;
    }

    public List<BidProductDto> getBidProductList() {
        return bidProductList;
    }

    public void setBidProductList(List<BidProductDto> bidProductList) {
        this.bidProductList = bidProductList;
    }

    public List<SummaryBidDto> getSummaryBidList() {
        return summaryBidList;
    }

    public void setSummaryBidList(List<SummaryBidDto> summaryBidList) {
        this.summaryBidList = summaryBidList;
    }

    public String getIsWinStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.一级投标中标状态, this.getIsWin());
    }

    public void setIsWinStr(String isWinStr) {
        this.isWinStr = isWinStr;
    }

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }

    public String getCcy() {
        return ccy;
    }

    public void setCcy(String ccy) {
        this.ccy = ccy;
    }

    public String getCcyStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.币种, this.getCcy());
    }

    public void setCcyStr(String ccyStr) {
        this.ccyStr = ccyStr;
    }

    public String getTrader() {
        return trader;
    }

    public void setTrader(String trader) {
        this.trader = trader;
    }

    public String getTraderStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.交易场所, this.getTrader());
    }

    public void setTraderStr(String traderStr) {
        this.traderStr = traderStr;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }
}
