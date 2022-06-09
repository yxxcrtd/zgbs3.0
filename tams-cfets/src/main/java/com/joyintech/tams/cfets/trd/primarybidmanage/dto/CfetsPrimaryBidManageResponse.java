package com.joyintech.tams.cfets.trd.primarybidmanage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.joyintech.framework.util.DateUtil;
import com.joyintech.framework.util.SysDataUtil;
import com.joyintech.tams.constant.DictCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 中标结果维护信息DTO
 *
 * @author wangyang1
 * @version 1.0 2022年5月5日
 * @see CfetsPrimaryBidManageResponse
 * @since 1.0
 */
@ApiModel(value="中标结果维护信息查询返回参数",description="中标结果维护信息查询返回参数")
public class CfetsPrimaryBidManageResponse {

    @ApiModelProperty(name="status",value="有效状态")
    private String status;

    @ApiModelProperty(name="statusStr",value="有效状态")
    private String statusStr;

    @ApiModelProperty(name="isWin",value="中标状态Str")
    private String isWin;

    @ApiModelProperty(name="isWinStr",value="中标状态")
    private String isWinStr;

    @ApiModelProperty(name="totalWinId",value="中标编号")
    private String totalWinId;

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
    private String bidDate;

    @ApiModelProperty(name="bidDateStart",value="投标日")
    private Date bidDateStart;

    @ApiModelProperty(name="bidDateEnd",value="投标日")
    private Date bidDateEnd;

    @ApiModelProperty(name="totalBiddingNum",value="汇总投标券面总额(万元)")
    private BigDecimal totalBiddingNum;

    @ApiModelProperty(name="bidPrice",value="投标标位")
    private String bidPrice;

    @ApiModelProperty(name="winType",value="投标方式")
    private String winType;

    @ApiModelProperty(name="winTypeStr",value="投标方式")
    private String winTypeStr;

    @ApiModelProperty(name="payType",value="缴款方式")
    private String payType;

    @ApiModelProperty(name="payTypeStr",value="缴款方式")
    private String payTypeStr;

    @ApiModelProperty(name="bidType",value="中标方式")
    private String bidType;

    @ApiModelProperty(name="bidTypeStr",value="中标方式")
    private String bidTypeStr;

    @ApiModelProperty(name="winPrinamt",value="中标券面总额（万元）")
    private BigDecimal winPrinamt;

    @ApiModelProperty(name="winPrice",value="中标标位")
    private String winPrice;

    @ApiModelProperty(name="payAmt",value="缴款金额（元）")
    private BigDecimal payAmt;

    @ApiModelProperty(name="settFeeAmt",value="结算服务费（元）")
    private BigDecimal settFeeAmt;

    @ApiModelProperty(name="payDate",value="缴款日")
    private Date payDate;

    @ApiModelProperty(name="creditorDebtDate",value="债权债务登记日")
    private Date creditorDebtDate;

    @ApiModelProperty(name="listingDate",value="上市日")
    private Date listingDate;

    @ApiModelProperty(name="winDate",value="中标日期")
    private Date winDate;

    @ApiModelProperty(name="tradeDate",value="成交日期")
    private Date tradeDate;

    @ApiModelProperty(name="tradeMarket",value="交易场所code")
    private String tradeMarket;

    @ApiModelProperty(name="tradeMarketStr",value="交易场所")
    private String tradeMarketStr;

    @ApiModelProperty(name="counterId",value="交易对手code")
    private String counterId;

    @ApiModelProperty(name="counterName",value="交易对手")
    private String counterName;

    @ApiModelProperty(name="remark",value="备注")
    private String remark ;

    @ApiModelProperty(name="bidModle",value="投标方式")
    private String bidModle;

    @ApiModelProperty(name="bidModleStr",value="投标方式Str")
    private String bidModleStr;

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

    public String getBidDate() {
        return DateUtil.format(this.getBidDateStart(),"YYYY-MM-DD") + "-"
              +DateUtil.format(this.getBidDateEnd(),"YYYY-MM-DD");
    }

    public void setBidDate(String bidDate) {
        this.bidDate = bidDate;
    }

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getBidDateStart() {
        return bidDateStart;
    }

    public void setBidDateStart(Date bidDateStart) {
        this.bidDateStart = bidDateStart;
    }

    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getBidDateEnd() {
        return bidDateEnd;
    }

    public void setBidDateEnd(Date bidDateEnd) {
        this.bidDateEnd = bidDateEnd;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.有效状态, this.getStatus());
    }

    public void setStatusStr(String statusStr) {
        this.statusStr = statusStr;
    }

    public String getBidType() {
        return bidType;
    }

    public void setBidType(String bidType) {
        this.bidType = bidType;
    }

    public String getBidTypeStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.中标方式, this.getBidType());
    }

    public void setBidTypeStr(String bidTypeStr) {
        this.bidTypeStr = bidTypeStr;
    }

    public BigDecimal getWinPrinamt() {
        return winPrinamt;
    }

    public void setWinPrinamt(BigDecimal winPrinamt) {
        this.winPrinamt = winPrinamt;
    }

    public String getWinPrice() {
        return winPrice;
    }

    public void setWinPrice(String winPrice) {
        this.winPrice = winPrice;
    }

    public BigDecimal getPayAmt() {
        return payAmt;
    }

    public void setPayAmt(BigDecimal payAmt) {
        this.payAmt = payAmt;
    }

    public BigDecimal getSettFeeAmt() {
        return settFeeAmt;
    }

    public void setSettFeeAmt(BigDecimal settFeeAmt) {
        this.settFeeAmt = settFeeAmt;
    }
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getPayDate() {
        return payDate;
    }

    public void setPayDate(Date payDate) {
        this.payDate = payDate;
    }
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getCreditorDebtDate() {
        return creditorDebtDate;
    }

    public void setCreditorDebtDate(Date creditorDebtDate) {
        this.creditorDebtDate = creditorDebtDate;
    }
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getListingDate() {
        return listingDate;
    }

    public void setListingDate(Date listingDate) {
        this.listingDate = listingDate;
    }
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getWinDate() {
        return winDate;
    }

    public void setWinDate(Date winDate) {
        this.winDate = winDate;
    }
    @JsonFormat(pattern="yyyy-MM-dd",timezone = "GMT+8")
    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public String getIsWinStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.一级投标中标状态, this.getIsWin());
    }

    public void setIsWinStr(String isWinStr) {
        this.isWinStr = isWinStr;
    }

    public String getFinprodMarketId() {
        return finprodMarketId;
    }

    public void setFinprodMarketId(String finprodMarketId) {
        this.finprodMarketId = finprodMarketId;
    }

    public String getTotalWinId() {
        return totalWinId;
    }

    public void setTotalWinId(String totalWinId) {
        this.totalWinId = totalWinId;
    }

    public String getBidModle() {
        return bidModle;
    }

    public void setBidModle(String bidModle) {
        this.bidModle = bidModle;
    }

    public String getBidModleStr() {
        return SysDataUtil.getNameFromDataDict(DictCode.投标方式, this.getBidModle());
    }

    public void setBidModleStr(String bidModleStr) {
        this.bidModleStr = bidModleStr;
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
